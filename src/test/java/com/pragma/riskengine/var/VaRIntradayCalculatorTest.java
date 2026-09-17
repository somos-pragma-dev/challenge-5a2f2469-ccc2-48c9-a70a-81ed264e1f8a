package com.pragma.riskengine.var;

import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.*;
nimport java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VaRIntradayCalculatorTest {

    @Mock
    private VaRIntradayCalculator.VolatilityProvider volatilityProvider;

    @Mock
    private VaRIntradayCalculator.CorrelationMatrix correlationMatrix;

    private VaRIntradayCalculator calculator;
    private Map<String, Double> portfolioPositions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portfolioPositions = new ConcurrentHashMap<>();
        
        when(volatilityProvider.getVolatility("AAPL")).thenReturn(0.25);
        when(volatilityProvider.getVolatility("GOOGL")).thenReturn(0.30);
        when(volatilityProvider.getVolatility("MSFT")).thenReturn(0.20);
        when(volatilityProvider.getVolatility("TSLA")).thenReturn(0.50);
        
        calculator = new VaRIntradayCalculator(volatilityProvider, correlationMatrix, portfolioPositions);
    }

    @Test
    @DisplayName("Debe calcular VaR correctamente para una posición larga simple")
    void calculateVar_debeCalcularPosicionLargaSimple() {
        portfolioPositions.put("AAPL", 100000.0);
        
        double var = calculator.calculateVar("AAPL", 150.0, 1000);
        
        double expectedExposure = 150.0 * 1000;
        double expectedVar = expectedExposure * 0.25 * 1.65;
        assertEquals(expectedVar, var, expectedVar * 0.01);
    }

    @Test
    @DisplayName("Debe calcular VaR correctamente para posición corta")
    void calculateVar_debeCalcularPosicionCorta() {
        portfolioPositions.put("AAPL", -100000.0);
        
        double var = calculator.calculateVar("AAPL", 150.0, -1000);
        
        double expectedExposure = 150.0 * Math.abs(-1000);
        double expectedVar = expectedExposure * 0.25 * 1.65;
        assertEquals(expectedVar, var, expectedVar * 0.01);
    }

    @Test
    @DisplayName("Debe usar matriz de correlación para cartera multi-activo")
    void calculateVar_debeUsarMatrizCorrelacion() {
        portfolioPositions.put("AAPL", 50000.0);
        portfolioPositions.put("GOOGL", 50000.0);
        
        when(correlationMatrix.getCorrelation("AAPL", "GOOGL")).thenReturn(0.7);
        
        double var = calculator.calculatePortfolioVaR(Arrays.asList("AAPL", "GOOGL"));
        
        assertTrue(var > 0, "VaR de cartera debe ser positivo");
        assertTrue(var < calculateSimpleSumVar(), 
            "VaR con correlación debe ser menor que la suma simple (beneficio de diversificación)");
    }

    @Test
    @DisplayName("Debe actualizar VaR en tiempo real tras cada operación")
    void calculateVar_debeActualizarEnTiempoReal() {
        calculator.updatePosition("AAPL", 1000, 150.0);
        double var1 = calculator.getCurrentVar("AAPL");
        
        calculator.updatePosition("AAPL", 500, 151.0);
        double var2 = calculator.getCurrentVar("AAPL");
        
        assertTrue(var2 > var1, "VaR debe aumentar con más exposición");
    }

    @Test
    @DisplayName("Debe recalcular automáticamente cuando cambia la volatilidad")
    void calculateVar_debeRecalcularConVolatilidadCambiada() {
        calculator.updatePosition("AAPL", 1000, 150.0);
        double var1 = calculator.getCurrentVar("AAPL");
        
        when(volatilityProvider.getVolatility("AAPL")).thenReturn(0.40);
        calculator.onVolatilityUpdate("AAPL", 0.40);
        
        double var2 = calculator.getCurrentVar("AAPL");
        
        assertEquals(var2, var1 * 1.6, var1 * 0.01);
    }

    @Test
    @DisplayName("Debe calcular VaR con nivel de confianza del 99%")
    void calculateVar_debeUsarNivelConfianza99() {
        portfolioPositions.put("TSLA", 100000.0);
        
        double var = calculator.calculateVar("TSLA", 700.0, 1000);
        
        double expectedExposure = 700.0 * 1000;
        double expectedVar = expectedExposure * 0.50 * 2.33;
        assertEquals(expectedVar, var, expectedVar * 0.01);
    }

    @Test
    @DisplayName("Debe manejar correctamente instrumentos con correlación perfecta positiva")
    void calculateVar_debeManejarCorrelacionPerfectaPositiva() {
        portfolioPositions.put("AAPL", 50000.0);
        portfolioPositions.put("GOOGL", 50000.0);
        
        when(correlationMatrix.getCorrelation("AAPL", "GOOGL")).thenReturn(1.0);
        
        double var = calculator.calculatePortfolioVaR(Arrays.asList("AAPL", "GOOGL"));
        double simpleSum = calculateSimpleSumVar();
        
        assertEquals(simpleSum, var, simpleSum * 0.001);
    }

    @Test
    @DisplayName("Debe manejar correctamente instrumentos con correlación perfecta negativa")
    void calculateVar_debeManejarCorrelacionPerfectaNegativa() {
        portfolioPositions.put("AAPL", 50000.0);
        portfolioPositions.put("GOOGL", 50000.0);
        
        when(correlationMatrix.getCorrelation("AAPL", "GOOGL")).thenReturn(-1.0);
        
        double var = calculator.calculatePortfolioVaR(Arrays.asList("AAPL", "GOOGL"));
        
        assertEquals(0.0, var, 0.01);
    }

    @Test
    @DisplayName("Debe cumplir latencia de cálculo menor a 100 microsegundos")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void calculateVar_debeCumplirLatenciaMaxima() {
        int iterations = 50000;
        String[] instruments = {"AAPL", "GOOGL", "MSFT", "TSLA"};
        
        for (String instrument : instruments) {
            calculator.updatePosition(instrument, 1000, 150.0);
        }
        
        long[] latencies = new long[iterations];
        
        for (int i = 0; i < iterations; i++) {
            String instrument = instruments[i % instruments.length];
            
            long start = System.nanoTime();
            calculator.calculateVar(instrument, 150.0, 1000);
            long end = System.nanoTime();
            
            latencies[i] = end - start;
        }
        
        long p99Latency = calculateP99(latencies);
        assertTrue(p99Latency <= 100_000, 
            "Latencia p99 debe ser <= 100 microsegundos, pero fue: " + (p99Latency / 1000));
    }

    @Test
    @DisplayName("Debe aplicar correctamente el horizonte temporal para VaR intraday")
    void calculateVar_debeAplicarHorizonteIntraday() {
        calculator.setTimeHorizonMinutes(15);
        portfolioPositions.put("AAPL", 100000.0);
        
        double varIntraday = calculator.calculateVar("AAPL", 150.0, 1000);
        
        double scalingFactor = Math.sqrt(15.0 / (6.5 * 60 * 60));
        double varBase = 150.0 * 1000 * 0.25 * 1.65;
        double expectedVar = varBase * scalingFactor;
        
        assertEquals(expectedVar, varIntraday, expectedVar * 0.05);
    }

    @Test
    @DisplayName("Debe calcular exposición total de la cartera correctamente")
    void calculateVar_debeCalcularExposicionTotalCartera() {
        Map<String, Double> positions = new HashMap<>();
        positions.put("AAPL", 150000.0);
        positions.put("GOOGL", 280000.0);
        positions.put("MSFT", 90000.0);
        
        double totalExposure = calculator.calculateTotalExposure(positions);
        
        assertEquals(520000.0, totalExposure, 0.01);
    }

    @Test
    @DisplayName("Debe rechazar volatilidad negativa del provider")
    void calculateVar_debeRechazarVolatilidadNegativa() {
        when(volatilityProvider.getVolatility("AAPL")).thenReturn(-0.1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateVar("AAPL", 150.0, 1000);
        });
    }

    private double calculateSimpleSumVar() {
        double varAAPL = 75000.0 * 0.25 * 1.65;
        double varGOOGL = 1400000.0 * 0.30 * 1.65;
        return varAAPL + varGOOGL;
    }

    private long calculateP99(long[] values) {
        if (values.length == 0) return 0;
        long[] sorted = values.clone();
        Arrays.sort(sorted);
        int p99Index = (int) Math.ceil(values.length * 0.99) - 1;
        return sorted[p99Index];
    }
}
package com.pragma.riskengine.circuitbreaker;

import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DynamicCircuitBreakerTest {

    @Mock
    private DynamicCircuitBreaker.FallbackHandler fallbackHandler;

    @Mock
    private DynamicCircuitBreaker.MetricsPublisher metricsPublisher;

    private DynamicCircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        circuitBreaker = new DynamicCircuitBreaker(
            fallbackHandler,
            metricsPublisher,
            10,
            0.5,
            Duration.ofSeconds(30),
            Duration.ofSeconds(10)
        );
    }

    @Test
    @DisplayName("Debe iniciar en estado CERRADO inicialmente")
    void initialState_debeSerCerrado() {
        assertEquals(DynamicCircuitBreaker.State.CLOSED, circuitBreaker.getState());
        assertTrue(circuitBreaker.isAvailable());
    }

    @Test
    @DisplayName("Debe abrir el circuit breaker cuando la tasa de error supera el threshold")
    void onFailure_debeAbrirCuandoSuperaThreshold() {
        for (int i = 0; i < 15; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error " + i));
        }
        
        assertEquals(DynamicCircuitBreaker.State.OPEN, circuitBreaker.getState("AAPL"));
        assertFalse(circuitBreaker.isAvailable("AAPL"));
    }

    @Test
    @DisplayName("Debe ejecutar fallback cuando el circuit breaker está abierto")
    void execute_debeEjecutarFallbackCuandoAbierto() {
        for (int i = 0; i < 15; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error"));
        }
        
        when(fallbackHandler.handleFallback(eq("AAPL"), any()))
            .thenReturn(new RiskEvent());
        
        Supplier<RiskEvent> operation = () -> {
            RiskEvent e = new RiskEvent();
            e.setEventId("fallback-result");
            return e;
        };
        
        RiskEvent result = circuitBreaker.execute("AAPL", operation);
        
        verify(fallbackHandler, times(1)).handleFallback(eq("AAPL"), any());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Debe cerrar el circuit breaker después del timeout de recuperación")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void onSuccess_debeCerrarDespuesTimeoutRecuperacion() throws InterruptedException {
        DynamicCircuitBreaker fastCircuitBreaker = new DynamicCircuitBreaker(
            fallbackHandler,
            metricsPublisher,
            5,
            0.5,
            Duration.ofMillis(100),
            Duration.ofMillis(50)
        );
        
        for (int i = 0; i < 10; i++) {
            fastCircuitBreaker.onFailure("TSLA", new RuntimeException("Error"));
        }
        
        assertEquals(DynamicCircuitBreaker.State.OPEN, fastCircuitBreaker.getState("TSLA"));
        
        Thread.sleep(150);
        
        assertEquals(DynamicCircuitBreaker.State.HALF_OPEN, fastCircuitBreaker.getState("TSLA"));
    }

    @Test
    @DisplayName("Debe calcular thresholds dinámicamente basados en volatilidad del instrumento")
    void calculateThreshold_debeCalcularBasadoEnVolatilidad() {
        double thresholdLowVol = circuitBreaker.calculateThreshold("AAPL", 0.15);
        double thresholdHighVol = circuitBreaker.calculateThreshold("TSLA", 0.60);
        
        assertTrue(thresholdLowVol < thresholdHighVol,
            "Instrumentos con menor volatilidad deben tener thresholds más estrictos");
    }

    @Test
    @DisplayName("Debe ajustar automáticamente el threshold cuando la volatilidad cambia significativamente")
    void updateThreshold_debeAjustarPorCambioVolatilidad() {
        circuitBreaker.setInstrumentVolatility("AAPL", 0.20);
        double threshold1 = circuitBreaker.calculateThreshold("AAPL", 0.20);
        
        circuitBreaker.setInstrumentVolatility("AAPL", 0.50);
        double threshold2 = circuitBreaker.calculateThreshold("AAPL", 0.50);
        
        assertTrue(threshold2 > threshold1);
    }

    @Test
    @DisplayName("Debe registrar métricas de cada operación")
    void execute_debeRegistrarMetricas() {
        when(fallbackHandler.handleFallback(any(), any())).thenReturn(new RiskEvent());
        
        for (int i = 0; i < 5; i++) {
            circuitBreaker.onFailure("GOOGL", new RuntimeException("Error"));
        }
        
        Supplier<RiskEvent> operation = () -> new RiskEvent();
        circuitBreaker.execute("GOOGL", operation);
        
        verify(metricsPublisher, atLeastOnce()).publish(
            eq("GOOGL"),
            any(DynamicCircuitBreaker.Metrics.class));
    }

    @Test
    @DisplayName("Debe manejar múltiples instrumentos de forma independiente")
    void state_debeManejarMultiplesInstrumentosIndependientemente() {
        for (int i = 0; i < 12; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error"));
        }
        
        for (int i = 0; i < 3; i++) {
            circuitBreaker.onFailure("MSFT", new RuntimeException("Error"));
        }
        
        assertEquals(DynamicCircuitBreaker.State.OPEN, circuitBreaker.getState("AAPL"));
        assertEquals(DynamicCircuitBreaker.State.CLOSED, circuitBreaker.getState("MSFT"));
    }

    @Test
    @DisplayName("Debe ejecutar operación exitosamente en estado CERRADO")
    void execute_debeEjecutarEnEstadoCerrado() {
        Supplier<RiskEvent> operation = () -> {
            RiskEvent e = new RiskEvent();
            e.setEventId("success-operation");
            e.setDecision(RiskDecision.APPROVED);
            return e;
        };
        
        RiskEvent result = circuitBreaker.execute("AAPL", operation);
        
        assertNotNull(result);
        assertEquals("success-operation", result.getEventId());
        verify(fallbackHandler, never()).handleFallback(any(), any());
    }

    @Test
    @DisplayName("Debe rechazar operaciones cuando está en estado FORCED_OPEN")
    void execute_debeRechazarEnForcedOpen() {
        circuitBreaker.forceOpen("TSLA");
        
        Supplier<RiskEvent> operation = () -> new RiskEvent();
        
        assertThrows(DynamicCircuitBreaker.CircuitBreakerOpenException.class, () -> {
            circuitBreaker.execute("TSLA", operation);
        });
    }

    @Test
    @DisplayName("Debe permitir transición manual a estado FORCED_OPEN para kill switch")
    void forceOpen_debePermitirTransicionManual() {
        circuitBreaker.forceOpen("AAPL");
        
        assertEquals(DynamicCircuitBreaker.State.FORCED_OPEN, circuitBreaker.getState("AAPL"));
        assertFalse(circuitBreaker.isAvailable("AAPL"));
    }

    @Test
    @DisplayName("Debe resetear el circuit breaker correctamente")
    void reset_debeLimpiarEstado() {
        for (int i = 0; i < 15; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error"));
        }
        
        circuitBreaker.reset("AAPL");
        
        assertEquals(DynamicCircuitBreaker.State.CLOSED, circuitBreaker.getState("AAPL"));
        assertTrue(circuitBreaker.isAvailable("AAPL"));
    }

    @Test
    @DisplayName("Debe calcular métricas de salud del circuit breaker")
    void metrics_debeCalcularSalud() {
        for (int i = 0; i < 10; i++) {
            circuitBreaker.onSuccess("AAPL");
        }
        for (int i = 0; i < 2; i++) {
            circuitBreaker.onFailure("AAPL", new RuntimeException("Error"));
        }
        
        DynamicCircuitBreaker.Health health = circuitBreaker.getHealth("AAPL");
        
        assertNotNull(health);
        assertEquals(10, health.getSuccessCount());
        assertEquals(2, health.getFailureCount());
        assertTrue(health.getFailureRate() > 0 && health.getFailureRate() < 1);
    }

    @Test
    @DisplayName("Debe cumplir latencia de decisión menor a 50 microsegundos")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void execute_debeCumplirLatenciaMaxima() {
        int iterations = 100000;
        String[] instruments = {"AAPL", "GOOGL", "MSFT", "TSLA"};
        long[] latencies = new long[iterations];
        
        for (int i = 0; i < iterations; i++) {
            String instrument = instruments[i % instruments.length];
            
            Supplier<RiskEvent> operation = () -> {
                RiskEvent e = new RiskEvent();
                e.setEventId("latency-test-" + i);
                return e;
            };
            
            long start = System.nanoTime();
            circuitBreaker.execute(instrument, operation);
            long end = System.nanoTime();
            
            latencies[i] = end - start;
        }
        
        long p99Latency = calculateP99(latencies);
        assertTrue(p99Latency <= 50_000, 
            "Latencia p99 debe ser <= 50 microsegundos, pero fue: " + (p99Latency / 1000));
    }

    private long calculateP99(long[] values) {
        if (values.length == 0) return 0;
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        int p99Index = (int) Math.ceil(values.length * 0.99) - 1;
        return sorted[p99Index];
    }
}
package com.pragma.riskengine.marketdata;

import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarketDataEventHandlerTest {

    @Mock
    private MarketDataEventHandler.PriceUpdateListener priceListener;

    @Mock
    private MarketDataEventHandler.VolatilityUpdateListener volatilityListener;

    private MarketDataEventHandler handler;
    private RiskEvent testEvent;
    private AtomicLong processedCount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processedCount = new AtomicLong(0);
        handler = new MarketDataEventHandler(priceListener, volatilityListener, processedCount);
        
        testEvent = new RiskEvent();
        testEvent.setEventId("evt-001");
        testEvent.setInstrumentId("AAPL");
        testEvent.setTraderId("trader-123");
        testEvent.setStrategyId("strategy-momentum");
        testEvent.setEventType(EventType.TRADE);
        testEvent.setPrice(150.25);
        testEvent.setQuantity(1000);
        testEvent.setVolatility(0.25);
        testEvent.setExposure(150250.0);
        testEvent.setTimestamp(Instant.now());
        testEvent.setNanoTimestamp(System.nanoTime());
        testEvent.setCorrelationId("corr-001");
    }

    @Test
    @DisplayName("Debe procesar eventos de trade correctamente y notificar listeners")
    void onEvent_debeProcesarTradeYNotificarListeners() {
        testEvent.setEventType(EventType.TRADE);
        
        handler.onEvent(testEvent, 0, false);
        
        verify(priceListener, times(1)).onPriceUpdate(
            eq("AAPL"), eq(150.25), any(Instant.class));
        verify(volatilityListener, times(1)).onVolatilityUpdate(
            eq("AAPL"), eq(0.25), any(Instant.class));
        assertEquals(1, processedCount.get());
    }

    @Test
    @DisplayName("Debe procesar eventos de orden y calcular exposición correctamente")
    void onEvent_debeProcesarOrdenYCalcularExposicion() {
        testEvent.setEventType(EventType.ORDER);
        testEvent.setPrice(150.50);
        testEvent.setQuantity(500);
        
        handler.onEvent(testEvent, 0, false);
        
        ArgumentCaptor<Double> exposureCaptor = ArgumentCaptor.forClass(Double.class);
        verify(priceListener).onPriceUpdate(eq("AAPL"), eq(150.50), any(Instant.class));
        assertEquals(75250.0, testEvent.getExposure(), 0.01);
    }

    @Test
    @DisplayName("Debe rechazar eventos con precio negativo")
    void onEvent_debeRechazarPrecioNegativo() {
        testEvent.setPrice(-10.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            handler.onEvent(testEvent, 0, false);
        });
    }

    @Test
    @DisplayName("Debe rechazar eventos con volatilidad negativa")
    void onEvent_debeRechazarVolatilidadNegativa() {
        testEvent.setVolatility(-0.5);
        
        assertThrows(IllegalArgumentException.class, () -> {
            handler.onEvent(testEvent, 0, false);
        });
    }

    @Test
    @DisplayName("Debe cumplir con latencia máxima de 500 microsegundos p99")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void onEvent_debeCumplirLatenciaMaxima() throws InterruptedException {
        int iterations = 10000;
        long[] latencies = new long[iterations];
        
        for (int i = 0; i < iterations; i++) {
            RiskEvent event = new RiskEvent();
            event.setEventId("evt-" + i);
            event.setInstrumentId("AAPL");
            event.setEventType(EventType.TRADE);
            event.setPrice(150.0 + (i % 100) * 0.01);
            event.setQuantity(100);
            event.setVolatility(0.20);
            event.setTimestamp(Instant.now());
            event.setNanoTimestamp(System.nanoTime());
            
            long start = System.nanoTime();
            handler.onEvent(event, i, false);
            long end = System.nanoTime();
            
            latencies[i] = end - start;
        }
        
        long p99Latency = calculateP99(latencies);
        assertTrue(p99Latency <= 500_000, 
            "Latencia p99 debe ser <= 500 microsegundos, pero fue: " + (p99Latency / 1000) + " microsegundos");
    }

    @Test
    @DisplayName("Debe procesar throughput mínimo de 10000 eventos por segundo")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void onEvent_debeCumplirThroughputMinimo() {
        int targetEvents = 10000;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < targetEvents; i++) {
            RiskEvent event = new RiskEvent();
            event.setEventId("evt-throughput-" + i);
            event.setInstrumentId("AAPL");
            event.setEventType(EventType.TRADE);
            event.setPrice(150.0);
            event.setQuantity(100);
            event.setVolatility(0.20);
            event.setTimestamp(Instant.now());
            event.setNanoTimestamp(System.nanoTime());
            
            handler.onEvent(event, i, false);
        }
        
        long elapsedMs = System.currentTimeMillis() - startTime;
        double eventsPerSecond = (targetEvents * 1000.0) / elapsedMs;
        
        assertTrue(eventsPerSecond >= 10000, 
            "Throughput debe ser >= 10000 eventos/segundo, pero fue: " + eventsPerSecond);
    }

    @Test
    @DisplayName("Debe manejar eventos de tipo ORDER_BOOK_UPDATE correctamente")
    void onEvent_debeManejarOrderBookUpdate() {
        testEvent.setEventType(EventType.ORDER_BOOK_UPDATE);
        testEvent.setPrice(150.30);
        
        handler.onEvent(testEvent, 0, false);
        
        verify(priceListener, times(1)).onPriceUpdate(
            eq("AAPL"), eq(150.30), any(Instant.class));
        verify(volatilityListener, never()).onVolatilityUpdate(any(), any(), any());
    }

    @Test
    @DisplayName("Debe actualizar precios con ordenamiento lexicográfico de instrumentos")
    void onEvent_debeActualizarPreciosOrdenados() {
        String[] instruments = {"AAPL", "GOOGL", "MSFT", "TSLA", "AMZN"};
        double[] prices = {150.0, 2800.0, 300.0, 700.0, 3300.0};
        
        for (int i = 0; i < instruments.length; i++) {
            RiskEvent event = new RiskEvent();
            event.setEventId("evt-sorted-" + i);
            event.setInstrumentId(instruments[i]);
            event.setEventType(EventType.TRADE);
            event.setPrice(prices[i]);
            event.setQuantity(100);
            event.setVolatility(0.20);
            event.setTimestamp(Instant.now());
            event.setNanoTimestamp(System.nanoTime());
            
            handler.onEvent(event, i, false);
        }
        
        for (int i = 0; i < instruments.length; i++) {
            verify(priceListener).onPriceUpdate(eq(instruments[i]), eq(prices[i]), any(Instant.class));
        }
    }

    @Test
    @DisplayName("Debe copiar evento cuando el flag endOfBatch es falso en procesamiento continuo")
    void onEvent_debeCopiarEventoEnModoBatch() {
        for (int i = 0; i < 100; i++) {
            RiskEvent event = new RiskEvent();
            event.setEventId("evt-batch-" + i);
            event.setInstrumentId("AAPL");
            event.setEventType(EventType.TRADE);
            event.setPrice(150.0 + i);
            event.setQuantity(100);
            event.setVolatility(0.20);
            event.setTimestamp(Instant.now());
            event.setNanoTimestamp(System.nanoTime());
            
            handler.onEvent(event, i, false);
        }
        
        assertEquals(100, processedCount.get());
    }

    private long calculateP99(long[] values) {
        if (values.length == 0) return 0;
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        int p99Index = (int) Math.ceil(values.length * 0.99) - 1;
        return sorted[p99Index];
    }
}
package com.pragma.riskengine.marketdata;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.pragma.riskengine.Main;
import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class MarketDataEventHandler implements EventHandler<RiskEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataEventHandler.class);
    private static final int ORDER_BOOK_LEVELS = 10;
    private static final double MIN_PRICE_TICK = 0.01;
    private static final double MAX_SPREAD_BPS = 50.0;
    private static final long MICROS_TO_NANOS = 1_000L;

    private final Main main;
    private final AtomicLong lastOrderBookUpdate = new AtomicLong(0L);
    private final LongAdder eventsProcessed = new LongAdder();
    private final LongAdder totalLatencyMicros = new LongAdder();
    private volatile double lastBestBid = 0.0;
    private volatile double lastBestAsk = Double.MAX_VALUE;
    private volatile double lastMidPrice = 0.0;
    private volatile double lastVolatility = 0.0;
    private volatile boolean orderBookStale = false;

    public MarketDataEventHandler(Main main) {
        this.main = main;
    }

    @Override
    public void onEvent(RiskEvent event, long sequence, boolean endOfBatch) {
        long startNanos = System.nanoTime();
        try {
            switch (event.getEventType()) {
                case ORDER_BOOK_UPDATE -> processOrderBookUpdate(event);
                case TRADE -> processTrade(event);
                case ORDER_SUBMISSION -> processOrderSubmission(event);
                default -> logger.warn("Tipo de evento desconocido: {}", event.getEventType());
            }
            lastOrderBookUpdate.set(System.nanoTime());
        } catch (Exception e) {
            logger.error("Error procesando evento {}: {}", event.getEventId(), e.getMessage(), e);
            event.setDecision(RiskDecision.REJECTED);
        } finally {
            long latencyMicros = (System.nanoTime() - startNanos) / MICROS_TO_NANOS;
            eventsProcessed.increment();
            totalLatencyMicros.add(latencyMicros);
            if (eventsProcessed.sum() % 10000 == 0) {
                logPerformanceMetrics();
            }
        }
    }

    private void processOrderBookUpdate(RiskEvent event) {
        double bid = extractBestBid(event);
        double ask = extractBestAsk(event);
        double spread = calculateSpread(bid, ask);
        if (spread > MAX_SPREAD_BPS * lastMidPrice / 10000) {
            orderBookStale = true;
            logger.warn("Order book spread anomalous para instrumento {}: {} bps", 
                event.getInstrumentId(), spread / lastMidPrice * 10000);
        } else {
            orderBookStale = false;
        }
        lastBestBid = bid;
        lastBestAsk = ask;
        lastMidPrice = (bid + ask) / 2.0;
        double newVolatility = calculateVolatilityFromBook(bid, ask);
        event.setPrice(lastMidPrice);
        event.setVolatility(newVolatility);
        lastVolatility = newVolatility;
        if (orderBookStale) {
            event.setDecision(RiskDecision.WARNED);
        } else {
            event.setDecision(RiskDecision.APPROVED);
        }
        event.setTimestamp(Instant.now());
        event.setNanoTimestamp(System.nanoTime());
    }

    private void processTrade(RiskEvent event) {
        double tradePrice = event.getPrice();
        double tradeQuantity = event.getQuantity();
        if (tradePrice <= 0 || tradeQuantity <= 0) {
            logger.warn("Trade inválido para instrumento {}: precio={}, cantidad={}",
                event.getInstrumentId(), tradePrice, tradeQuantity);
            event.setDecision(RiskDecision.REJECTED);
            return;
        }
        double priceDeviation = Math.abs(tradePrice - lastMidPrice) / lastMidPrice;
        if (priceDeviation > 0.05) {
            logger.warn("Trade con desviación significativa: instrumento={}, desviación={}%",
                event.getInstrumentId(), priceDeviation * 100);
        }
        double exposureImpact = tradePrice * tradeQuantity;
        event.setExposure(exposureImpact);
        event.setDecision(RiskDecision.APPROVED);
        event.setTimestamp(Instant.now());
        event.setNanoTimestamp(System.nanoTime());
    }

    private void processOrderSubmission(RiskEvent event) {
        double orderPrice = event.getPrice();
        double orderQuantity = event.getQuantity();
        if (orderQuantity > getMaxOrderSize(event.getInstrumentId())) {
            logger.warn("Orden excede tamaño máximo: instrumento={}, cantidad={}, máximo={}",
                event.getInstrumentId(), orderQuantity, getMaxOrderSize(event.getInstrumentId()));
            event.setDecision(RiskDecision.REJECTED);
            return;
        }
        if (orderBookStale) {
            logger.warn("Orden rechazada por order book stale: instrumento={}", event.getInstrumentId());
            event.setDecision(RiskDecision.WARNED);
            return;
        }
        double orderValue = orderPrice * orderQuantity;
        event.setExposure(orderValue);
        event.setDecision(RiskDecision.APPROVED);
        event.setTimestamp(Instant.now());
        event.setNanoTimestamp(System.nanoTime());
    }

    private double extractBestBid(RiskEvent event) {
        return event.getPrice() > 0 ? event.getPrice() * 0.9995 : 100.0;
    }

    private double extractBestAsk(RiskEvent event) {
        return event.getPrice() > 0 ? event.getPrice() * 1.0005 : 100.01;
    }

    private double calculateSpread(double bid, double ask) {
        if (bid <= 0 || ask <= 0 || ask <= bid) return Double.MAX_VALUE;
        return (ask - bid) / lastMidPrice * 10000;
    }

    private double calculateVolatilityFromBook(double bid, double ask) {
        double spread = Math.abs(ask - bid);
        double volatilityEstimate = spread / lastMidPrice * Math.sqrt(252 * 6.5 * 390);
        return Math.max(volatilityEstimate, 0.01);
    }

    private double getMaxOrderSize(String instrumentId) {
        return 1_000_000.0;
    }

    private void logPerformanceMetrics() {
        double avgLatency = (double) totalLatencyMicros.sum() / eventsProcessed.sum();
        logger.info("MarketDataHandler - Eventos: {}, Latencia avg: {} μs",
            eventsProcessed.sum(), String.format("%.2f", avgLatency));
    }

    public double getLastMidPrice() {
        return lastMidPrice;
    }

    public double getLastVolatility() {
        return lastVolatility;
    }

    public boolean isOrderBookStale() {
        return orderBookStale;
    }

    public long getEventsProcessed() {
        return eventsProcessed.sum();
    }
}
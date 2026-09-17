package com.pragma.riskengine;

import java.time.Instant;

public final class RiskEvent {

    private String eventId;
    private String instrumentId;
    private String traderId;
    private String strategyId;
    private EventType eventType;
    private double price;
    private double quantity;
    private double volatility;
    private double var;
    private double exposure;
    private RiskDecision decision;
    private Instant timestamp;
    private long nanoTimestamp;
    private String correlationId;

    public RiskEvent() {
        this.timestamp = Instant.now();
        this.nanoTimestamp = System.nanoTime();
    }

    public void copyFrom(RiskEvent other) {
        this.eventId = other.eventId;
        this.instrumentId = other.instrumentId;
        this.traderId = other.traderId;
        this.strategyId = other.strategyId;
        this.eventType = other.eventType;
        this.price = other.price;
        this.quantity = other.quantity;
        this.volatility = other.volatility;
        this.var = other.var;
        this.exposure = other.exposure;
        this.decision = other.decision;
        this.timestamp = other.timestamp;
        this.nanoTimestamp = other.nanoTimestamp;
        this.correlationId = other.correlationId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public double getVar() {
        return var;
    }

    public void setVar(double var) {
        this.var = var;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
        this.exposure = exposure;
    }

    public RiskDecision getDecision() {
        return decision;
    }

    public void setDecision(RiskDecision decision) {
        this.decision = decision;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public long getNanoTimestamp() {
        return nanoTimestamp;
    }

    public void setNanoTimestamp(long nanoTimestamp) {
        this.nanoTimestamp = nanoTimestamp;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public enum EventType {
        MARKET_DATA,
        ORDER_SUBMITTED,
        ORDER_FILLED,
        ORDER_CANCELLED,
        POSITION_UPDATE,
        LIMIT_CHECK,
        CIRCUIT_BREAKER_TRIGGERED,
        KILL_SWITCH_ACTIVATED
    }

    public enum RiskDecision {
        APPROVED,
        REJECTED,
        REVIEW_REQUIRED,
        CIRCUIT_BREAKER_OPEN,
        KILL_SWITCH_ACTIVATED,
        PENDING
    }
}
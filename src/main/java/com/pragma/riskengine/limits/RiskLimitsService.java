package com.pragma.riskengine.limits;

import com.pragma.riskengine.Main;
import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;

public class RiskLimitsService {

    private static final Logger logger = LoggerFactory.getLogger(RiskLimitsService.class);
    private static final double DEFAULT_POSITION_LIMIT = 10_000_000.0;
    private static final double DEFAULT_TRADER_LIMIT = 50_000_000.0;
    private static final double DEFAULT_STRATEGY_LIMIT = 100_000_000.0;
    private static final double DEFAULT_DAILY_LOSS_LIMIT = 1_000_000.0;
    private static final double DEFAULT_CONCENTRATION_LIMIT = 0.25;
    private static final int MAX_VIOLATIONS_PER_MINUTE = 5;

    private final Main main;
    private final ConcurrentHashMap<String, TraderLimits> traderLimitsMap;
    private final ConcurrentHashMap<String, StrategyLimits> strategyLimitsMap;
    private final ConcurrentHashMap<String, InstrumentLimits> instrumentLimitsMap;
    private final ConcurrentHashMap<String, PositionState> positionStates;
    private final ConcurrentHashMap<String, DailyPnL> dailyPnLs;

    public RiskLimitsService(Main main) {
        this.main = main;
        this.traderLimitsMap = new ConcurrentHashMap<>();
        this.strategyLimitsMap = new ConcurrentHashMap<>();
        this.instrumentLimitsMap = new ConcurrentHashMap<>();
        this.positionStates = new ConcurrentHashMap<>();
        this.dailyPnLs = new ConcurrentHashMap<>();
    }

    public RiskDecision evaluateLimits(RiskEvent event) {
        String traderId = event.getTraderId();
        String strategyId = event.getStrategyId();
        String instrumentId = event.getInstrumentId();
        double exposure = event.getExposure();
        double price = event.getPrice();
        double quantity = event.getQuantity();

        RiskDecision decision = checkTraderLimit(traderId, exposure);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("TRADER", traderId, exposure, "límite de trader");
            return decision;
        }

        decision = checkStrategyLimit(strategyId, exposure);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("STRATEGY", strategyId, exposure, "límite de estrategia");
            return decision;
        }

        decision = checkInstrumentLimit(instrumentId, exposure);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("INSTRUMENT", instrumentId, exposure, "límite de instrumento");
            return decision;
        }

        decision = checkPositionLimit(instrumentId, quantity);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("POSITION", instrumentId, quantity, "límite de posición");
            return decision;
        }

        decision = checkConcentrationLimit(traderId, instrumentId, exposure);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("CONCENTRATION", traderId, exposure, "límite de concentración");
            return decision;
        }

        decision = checkDailyLossLimit(traderId);
        if (decision == RiskDecision.REJECTED) {
            logLimitViolation("DAILY_LOSS", traderId, 0, "límite de pérdida diaria");
            return decision;
        }

        updatePosition(instrumentId, quantity, price);
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkTraderLimit(String traderId, double exposure) {
        TraderLimits limits = traderLimitsMap.computeIfAbsent(traderId, 
            k -> new TraderLimits(DEFAULT_TRADER_LIMIT));
        double currentExposure = getTraderExposure(traderId);
        if (currentExposure + exposure > limits.getMaxExposure()) {
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkStrategyLimit(String strategyId, double exposure) {
        StrategyLimits limits = strategyLimitsMap.computeIfAbsent(strategyId,
            k -> new StrategyLimits(DEFAULT_STRATEGY_LIMIT));
        double currentExposure = getStrategyExposure(strategyId);
        if (currentExposure + exposure > limits.getMaxExposure()) {
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkInstrumentLimit(String instrumentId, double exposure) {
        InstrumentLimits limits = instrumentLimitsMap.computeIfAbsent(instrumentId,
            k -> new InstrumentLimits(DEFAULT_POSITION_LIMIT));
        double currentExposure = getInstrumentExposure(instrumentId);
        if (currentExposure + exposure > limits.getMaxExposure()) {
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkPositionLimit(String instrumentId, double quantity) {
        PositionState state = positionStates.get(instrumentId);
        if (state == null) {
            return RiskDecision.APPROVED;
        }
        double currentPosition = state.getNetPosition();
        if (Math.abs(currentPosition + quantity) > getMaxPositionSize(instrumentId)) {
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkConcentrationLimit(String traderId, String instrumentId, double exposure) {
        double traderTotalExposure = getTraderExposure(traderId);
        if (traderTotalExposure <= 0) {
            return RiskDecision.APPROVED;
        }
        double instrumentExposure = getInstrumentExposure(instrumentId);
        double concentration = (instrumentExposure + exposure) / traderTotalExposure;
        if (concentration > DEFAULT_CONCENTRATION_LIMIT) {
            logger.warn("Concentración excesiva para trader {} en instrumento {}: {}%",
                traderId, instrumentId, concentration * 100);
            return RiskDecision.WARNED;
        }
        return RiskDecision.APPROVED;
    }

    private RiskDecision checkDailyLossLimit(String traderId) {
        DailyPnL pnl = dailyPnLs.get(traderId);
        if (pnl == null) {
            return RiskDecision.APPROVED;
        }
        if (pnl.getDailyLoss() > DEFAULT_DAILY_LOSS_LIMIT) {
            logger.error("Límite de pérdida diaria alcanzado para trader {}: ${}",
                traderId, pnl.getDailyLoss());
            return RiskDecision.REJECTED;
        }
        return RiskDecision.APPROVED;
    }

    private void updatePosition(String instrumentId, double quantity, double price) {
        positionStates.compute(instrumentId, (key, existing) -> {
            if (existing == null) {
                return new PositionState(quantity, quantity * price);
            }
            existing.addQuantity(quantity);
            existing.addExposure(quantity * price);
            return existing;
        });
    }

    private double getTraderExposure(String traderId) {
        return positionStates.values().stream()
            .mapToDouble(PositionState::getTotalExposure)
            .sum();
    }

    private double getStrategyExposure(String strategyId) {
        return positionStates.values().stream()
            .mapToDouble(PositionState::getTotalExposure)
            .sum();
    }

    private double getInstrumentExposure(String instrumentId) {
        PositionState state = positionStates.get(instrumentId);
        return state != null ? state.getTotalExposure() : 0.0;
    }

    private double getMaxPositionSize(String instrumentId) {
        return 1_000_000.0;
    }

    private void logLimitViolation(String limitType, String entityId, double value, String description) {
        logger.warn("Violación de límite {} para {} ({}): valor={}, límite={}",
            limitType, entityId, description, value, "configurado");
    }

    public void reset() {
        traderLimitsMap.clear();
        strategyLimitsMap.clear();
        instrumentLimitsMap.clear();
        positionStates.clear();
        dailyPnLs.clear();
        logger.info("RiskLimitsService reseteado");
    }

    public int getPositionCount() {
        return positionStates.size();
    }

    private static class TraderLimits {
        private final double maxExposure;

        public TraderLimits(double maxExposure) {
            this.maxExposure = maxExposure;
        }

        public double getMaxExposure() {
            return maxExposure;
        }
    }

    private static class StrategyLimits {
        private final double maxExposure;

        public StrategyLimits(double maxExposure) {
            this.maxExposure = maxExposure;
        }

        public double getMaxExposure() {
            return maxExposure;
        }
    }

    private static class InstrumentLimits {
        private final double maxExposure;

        public InstrumentLimits(double maxExposure) {
            this.maxExposure = maxExposure;
        }

        public double getMaxExposure() {
            return maxExposure;
        }
    }

    private static class PositionState {
        private volatile double netPosition;
        private volatile double totalExposure;

        public PositionState(double netPosition, double totalExposure) {
            this.netPosition = netPosition;
            this.totalExposure = totalExposure;
        }

        public double getNetPosition() {
            return netPosition;
        }

        public void addQuantity(double quantity) {
            this.netPosition += quantity;
        }

        public double getTotalExposure() {
            return totalExposure;
        }

        public void addExposure(double exposure) {
            this.totalExposure += exposure;
        }
    }

    private static class DailyPnL {
        private volatile double dailyPnL = 0.0;
        private volatile double dailyLoss = 0.0;

        public double getDailyLoss() {
            return dailyLoss;
        }

        public void updatePnL(double pnl) {
            this.dailyPnL += pnl;
            if (this.dailyPnL < 0) {
                this.dailyLoss = -this.dailyPnL;
            }
        }
    }
}
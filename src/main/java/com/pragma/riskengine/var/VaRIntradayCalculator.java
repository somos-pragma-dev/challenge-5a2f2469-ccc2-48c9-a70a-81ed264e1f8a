package com.pragma.riskengine.var;

import com.pragma.riskengine.Main;
import com.pragma.riskengine.RiskEvent;
import com.pragma.riskengine.RiskEvent.EventType;
import com.pragma.riskengine.RiskEvent.RiskDecision;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;

public class VaRIntradayCalculator {

    private static final Logger logger = LoggerFactory.getLogger(VaRIntradayCalculator.class);
    private static final double DEFAULT_CONFIDENCE_LEVEL = 0.99;
    private static final double DEFAULT_HOLDING_PERIOD_HOURS = 1.0;
    private static final int MAX_PRICE_HISTORY = 1000;
    private static final double MIN_VOLATILITY = 0.01;
    private static final double MAX_VOLATILITY = 5.0;

    private final Main main;
    private final NormalDistribution normalDistribution;
    private final ConcurrentHashMap<String, InstrumentVaRState> instrumentStates;
    private final ConcurrentHashMap<String, double[]> priceHistories;

    public VaRIntradayCalculator(Main main) {
        this.main = main;
        this.normalDistribution = new NormalDistribution();
        this.instrumentStates = new ConcurrentHashMap<>();
        this.priceHistories = new ConcurrentHashMap<>();
    }

    public double calculateVaR(String instrumentId, double exposure, double volatility, Duration timeWindow) {
        InstrumentVaRState state = instrumentStates.computeIfAbsent(instrumentId, 
            k -> new InstrumentVaRState());
        StampedLock lock = state.getLock();
        long stamp = lock.writeLock();
        try {
            double effectiveVolatility = clampVolatility(volatility);
            double scaledVolatility = effectiveVolatility * Math.sqrt(timeWindow.toHours() / DEFAULT_HOLDING_PERIOD_HOURS);
            double var99 = calculateParametricVaR(exposure, scaledVolatility, DEFAULT_CONFIDENCE_LEVEL);
            state.updateVaR(var99);
            state.setLastCalculationTime(Instant.now());
            state.incrementCalculationCount();
            logger.debug("VaR calculado para instrumento {}: exposición={}, volatilidad={}, VaR99={}",
                instrumentId, exposure, effectiveVolatility, var99);
            return var99;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public double calculateVaRHistorical(String instrumentId, double exposure, int lookbackDays) {
        double[] prices = priceHistories.get(instrumentId);
        if (prices == null || prices.length < 2) {
            logger.warn("Sin historial suficiente para VaR histórico: instrumento {}", instrumentId);
            return calculateVaRDefault(exposure);
        }
        double[] returns = calculateReturns(prices);
        double[] sortedReturns = returns.clone();
        java.util.Arrays.sort(sortedReturns);
        int varIndex = (int) Math.ceil((1 - DEFAULT_CONFIDENCE_LEVEL) * sortedReturns.length);
        double varReturn = -sortedReturns[Math.min(varIndex, sortedReturns.length - 1)];
        double historicalVaR = exposure * varReturn;
        InstrumentVaRState state = instrumentStates.get(instrumentId);
        if (state != null) {
            state.updateVaR(historicalVaR);
        }
        return historicalVaR;
    }

    public void updatePriceHistory(String instrumentId, double price) {
        priceHistories.compute(instrumentId, (key, existing) -> {
            if (existing == null) {
                return new double[]{price};
            }
            if (existing.length >= MAX_PRICE_HISTORY) {
                System.arraycopy(existing, 1, existing, 0, existing.length - 1);
                existing[existing.length - 1] = price;
                return existing;
            } else {
                double[] updated = new double[existing.length + 1];
                System.arraycopy(existing, 0, updated, 0, existing.length);
                updated[existing.length] = price;
                return updated;
            }
        });
    }

    public double getCurrentVaR(String instrumentId) {
        InstrumentVaRState state = instrumentStates.get(instrumentId);
        if (state == null) {
            return 0.0;
        }
        StampedLock lock = state.getLock();
        long stamp = lock.tryOptimisticRead();
        double var = state.getCurrentVaR();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                var = state.getCurrentVaR();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return var;
    }

    private double calculateParametricVaR(double exposure, double volatility, double confidenceLevel) {
        double zScore = normalDistribution.inverseCumulativeProbability(confidenceLevel);
        double var = exposure * volatility * zScore;
        return Math.max(var, 0.0);
    }

    private double[] calculateReturns(double[] prices) {
        if (prices.length < 2) {
            return new double[0];
        }
        double[] returns = new double[prices.length - 1];
        for (int i = 1; i < prices.length; i++) {
            returns[i - 1] = (prices[i] - prices[i - 1]) / prices[i - 1];
        }
        return returns;
    }

    private double clampVolatility(double volatility) {
        return Math.max(MIN_VOLATILITY, Math.min(MAX_VOLATILITY, volatility));
    }

    private double calculateVaRDefault(double exposure) {
        return exposure * DEFAULT_CONFIDENCE_LEVEL * 0.02;
    }

    public void reset() {
        instrumentStates.clear();
        priceHistories.clear();
        logger.info("VaR calculator reseteado");
    }

    public int getInstrumentCount() {
        return instrumentStates.size();
    }

    private static class InstrumentVaRState {
        private final StampedLock lock = new StampedLock();
        private final AtomicReference<Double> currentVaR = new AtomicReference<>(0.0);
        private volatile Instant lastCalculationTime;
        private volatile long calculationCount = 0;

        public StampedLock getLock() {
            return lock;
        }

        public double getCurrentVaR() {
            return currentVaR.get();
        }

        public void updateVaR(double var) {
            currentVaR.set(var);
        }

        public void setLastCalculationTime(Instant time) {
            this.lastCalculationTime = time;
        }

        public void incrementCalculationCount() {
            calculationCount++;
        }
    }
}
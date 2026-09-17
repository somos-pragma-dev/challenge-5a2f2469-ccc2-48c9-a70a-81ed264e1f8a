package com.pragma.riskengine;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.pragma.riskengine.marketdata.MarketDataEventHandler;
import com.pragma.riskengine.var.VaRIntradayCalculator;
import com.pragma.riskengine.limits.RiskLimitsService;
import com.pragma.riskengine.circuitbreaker.DynamicCircuitBreaker;
import com.pragma.riskengine.killswitch.KillSwitchPolicy;
import com.pragma.riskengine.compliance.MiFIDIITracer;
import com.pragma.riskengine.sharding.InstrumentShardingStrategy;
import com.pragma.riskengine.replay.DeterministicReplayEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final int RING_BUFFER_SIZE = 1 << 16;
    private static final int RING_BUFFER_POW2 = 65536;

    private final Disruptor<RiskEvent> disruptor;
    private final RingBuffer<RiskEvent> ringBuffer;
    private final AtomicBoolean isRunning;

    private final MarketDataEventHandler marketDataHandler;
    private final VaRIntradayCalculator varCalculator;
    private final RiskLimitsService limitsService;
    private final DynamicCircuitBreaker circuitBreaker;
    private final KillSwitchPolicy killSwitchPolicy;
    private final MiFIDIITracer complianceTracer;
    private final InstrumentShardingStrategy shardingStrategy;
    private final DeterministicReplayEngine replayEngine;

    public Main() {
        this.isRunning = new AtomicBoolean(false);

        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        this.marketDataHandler = new MarketDataEventHandler();
        this.varCalculator = new VaRIntradayCalculator();
        this.limitsService = new RiskLimitsService();
        this.circuitBreaker = new DynamicCircuitBreaker();
        this.killSwitchPolicy = new KillSwitchPolicy();
        this.complianceTracer = new MiFIDIITracer();
        this.shardingStrategy = new InstrumentShardingStrategy();
        this.replayEngine = new DeterministicReplayEngine();

        this.disruptor = new Disruptor<>
            (RiskEvent::new, RING_BUFFER_POW2, threadFactory,
             ProducerType.MULTI, new BlockingWaitStrategy());

        disruptor.handleEventsWith(marketDataHandler)
            .then(varCalculator)
            .then(limitsService)
            .then(circuitBreaker)
            .then(killSwitchPolicy)
            .then(complianceTracer);

        this.ringBuffer = disruptor.getRingBuffer();

        logger.info("Risk Engine inicializado con RingBuffer de {} elementos", RING_BUFFER_POW2);
    }

    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            disruptor.start();
            logger.info("Risk Engine iniciado y escuchando eventos");
        } else {
            logger.warn("Risk Engine ya se encuentra en ejecucion");
        }
    }

    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            disruptor.shutdown();
            logger.info("Risk Engine detenido");
        } else {
            logger.warn("Risk Engine no esta en ejecucion");
        }
    }

    public void publishEvent(RiskEvent event) {
        if (!isRunning.get()) {
            logger.error("No se puede publicar evento: Risk Engine no esta inicializado");
            return;
        }

        long sequence = ringBuffer.next();
        try {
            RiskEvent storedEvent = ringBuffer.get(sequence);
            storedEvent.copyFrom(event);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public VaRIntradayCalculator getVarCalculator() {
        return varCalculator;
    }

    public RiskLimitsService getLimitsService() {
        return limitsService;
    }

    public DynamicCircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    public KillSwitchPolicy getKillSwitchPolicy() {
        return killSwitchPolicy;
    }

    public MiFIDIITracer getComplianceTracer() {
        return complianceTracer;
    }

    public InstrumentShardingStrategy getShardingStrategy() {
        return shardingStrategy;
    }

    public DeterministicReplayEngine getReplayEngine() {
        return replayEngine;
    }

    public static void main(String[] args) {
        Main engine = new Main();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Recibida senial de cierre, deteniendo Risk Engine...");
            engine.stop();
        }));

        engine.start();

        logger.info("Risk Engine listo para procesar eventos de riesgo");
    }
}
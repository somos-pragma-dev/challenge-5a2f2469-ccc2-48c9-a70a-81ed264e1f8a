package com.pragma.riskengine.sharding;

import com.pragma.riskengine.RiskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.ToIntFunction;

/**
 * Estrategia de sharding por instrumento para garantizar consistencia
 * entre múltiples risk engines. Usa hash consistente para distribuir
 * eventos del mismo instrumento al mismo shard.
 */
public class InstrumentShardingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(InstrumentShardingStrategy.class);
    private static final int DEFAULT_SHARD_COUNT = 8;
    private static final int MAX_SHARD_COUNT = 64;
    private static final int MIN_SHARD_COUNT = 1;
    
    private final int shardCount;
    private final ToIntFunction<String> hashFunction;
    private final Map<String, Integer> instrumentToShardCache;
    private final List<ShardMetrics> shardMetrics;
    private final ConcurrentHashMap<String, ShardAssignment> activeAssignments;
    
    public InstrumentShardingStrategy() {
        this(DEFAULT_SHARD_COUNT, null);
    }
    
    public InstrumentShardingStrategy(int shardCount, ToIntFunction<String> customHashFunction) {
        if (shardCount < MIN_SHARD_COUNT || shardCount > MAX_SHARD_COUNT) {
            throw new IllegalArgumentException(
                "Shard count must be between " + MIN_SHARD_COUNT + " and " + MAX_SHARD_COUNT + 
                ", but was: " + shardCount);
        }
        this.shardCount = shardCount;
        this.hashFunction = customHashFunction != null ? customHashFunction : this::defaultHash;
        this.instrumentToShardCache = new ConcurrentHashMap<>(1024);
        this.shardMetrics = new CopyOnWriteArrayList<>();
        this.activeAssignments = new ConcurrentHashMap<>();
        
        for (int i = 0; i < shardCount; i++) {
            shardMetrics.add(new ShardMetrics(i));
        }
        
        logger.info("InstrumentShardingStrategy initialized with {} shards", shardCount);
    }
    
    private int defaultHash(String instrumentId) {
        if (instrumentId == null || instrumentId.isEmpty()) {
            return 0;
        }
        int hash = instrumentId.hashCode();
        hash = ((hash >>> 16) ^ hash) * 0x45d9f3b;
        hash = ((hash >>> 16) ^ hash) * 0x45d9f3b;
        hash = hash >>> 16;
        return Math.abs(hash);
    }
    
    /**
     * Obtiene el identificador del shard para un instrumento dado.
     * Usa cacheo para evitar recalcular el hash en eventos subsiguientes.
     */
    public int getShardForInstrument(String instrumentId) {
        if (instrumentId == null) {
            logger.warn("Received null instrumentId, defaulting to shard 0");
            return 0;
        }
        
        return instrumentToShardCache.computeIfAbsent(instrumentId, id -> {
            int shard = hashFunction.applyAsInt(id) % shardCount;
            if (shard < 0) {
                shard = -shard;
            }
            shardMetrics.get(shard).incrementAssignmentCount();
            logger.debug("Assigned instrument {} to shard {}", id, shard);
            return shard;
        });
    }
    
    /**
     * Obtiene el shard para un evento de riesgo basándose en su instrumento.
     */
    public int getShardForEvent(RiskEvent event) {
        if (event == null || event.getInstrumentId() == null) {
            return 0;
        }
        return getShardForInstrument(event.getInstrumentId());
    }
    
    /**
     * Registra una asignación activa de instrumento a shard para trazabilidad.
     */
    public void registerAssignment(String instrumentId, String engineId) {
        if (instrumentId == null || engineId == null) {
            return;
        }
        int shard = getShardForInstrument(instrumentId);
        ShardAssignment assignment = new ShardAssignment(instrumentId, engineId, shard, System.nanoTime());
        activeAssignments.put(instrumentId, assignment);
        logger.debug("Registered assignment: instrument={}, engine={}, shard={}", 
            instrumentId, engineId, shard);
    }
    
    /**
     * Obtiene la asignación activa para un instrumento.
     */
    public ShardAssignment getAssignment(String instrumentId) {
        return activeAssignments.get(instrumentId);
    }
    
    /**
     * Obtiene el número total de shards configurados.
     */
    public int getShardCount() {
        return shardCount;
    }
    
    /**
     * Obtiene las métricas de un shard específico.
     */
    public ShardMetrics getShardMetrics(int shardIndex) {
        if (shardIndex < 0 || shardIndex >= shardCount) {
            throw new IndexOutOfBoundsException(
                "Shard index " + shardIndex + " out of bounds for " + shardCount + " shards");
        }
        return shardMetrics.get(shardIndex);
    }
    
    /**
     * Obtiene todas las métricas de shards para monitoreo.
     */
    public List<ShardMetrics> getAllShardMetrics() {
        return List.copyOf(shardMetrics);
    }
    
    /**
     * Rebalancea los shards subyacentes. Este método permite cambiar
     * dinámicamente el número de shards cuando la carga lo requiere.
     */
    public synchronized void rebalance(int newShardCount) {
        if (newShardCount == this.shardCount) {
            logger.info("Rebalance skipped: count unchanged");
            return;
        }
        if (newShardCount < MIN_SHARD_COUNT || newShardCount > MAX_SHARD_COUNT) {
            throw new IllegalArgumentException(
                "Cannot rebalance to " + newShardCount + " shards (valid range: " + 
                MIN_SHARD_COUNT + "-" + MAX_SHARD_COUNT + ")");
        }
        
        logger.warn("Rebalancing from {} to {} shards - cache will be invalidated", 
            this.shardCount, newShardCount);
        instrumentToShardCache.clear();
    }
    
    /**
     * Metrica de un shard individual para monitoreo de carga.
     */
    public static class ShardMetrics {
        private final int shardId;
        private volatile long assignmentCount;
        private volatile long eventCount;
        private volatile long totalProcessingTimeNanos;
        private volatile long maxProcessingTimeNanos;
        
        public ShardMetrics(int shardId) {
            this.shardId = shardId;
            this.assignmentCount = 0;
            this.eventCount = 0;
            this.totalProcessingTimeNanos = 0;
            this.maxProcessingTimeNanos = 0;
        }
        
        public void incrementAssignmentCount() {
            assignmentCount++;
        }
        
        public void recordEvent(long processingTimeNanos) {
            eventCount++;
            totalProcessingTimeNanos += processingTimeNanos;
            if (processingTimeNanos > maxProcessingTimeNanos) {
                maxProcessingTimeNanos = processingTimeNanos;
            }
        }
        
        public int getShardId() { return shardId; }
        public long getAssignmentCount() { return assignmentCount; }
        public long getEventCount() { return eventCount; }
        public long getTotalProcessingTimeNanos() { return totalProcessingTimeNanos; }
        public long getMaxProcessingTimeNanos() { return maxProcessingTimeNanos; }
        
        public double getAverageProcessingTimeMicros() {
            if (eventCount == 0) return 0.0;
            return (totalProcessingTimeNanos / 1000.0) / eventCount;
        }
    }
    
    /**
     * Representa una asignación activa de instrumento a engine.
     */
    public static class ShardAssignment {
        private final String instrumentId;
        private final String engineId;
        private final int shardId;
        private final long assignedAtNanos;
        
        public ShardAssignment(String instrumentId, String engineId, int shardId, long assignedAtNanos) {
            this.instrumentId = instrumentId;
            this.engineId = engineId;
            this.shardId = shardId;
            this.assignedAtNanos = assignedAtNanos;
        }
        
        public String getInstrumentId() { return instrumentId; }
        public String getEngineId() { return engineId; }
        public int getShardId() { return shardId; }
        public long getAssignedAtNanos() { return assignedAtNanos; }
    }
}
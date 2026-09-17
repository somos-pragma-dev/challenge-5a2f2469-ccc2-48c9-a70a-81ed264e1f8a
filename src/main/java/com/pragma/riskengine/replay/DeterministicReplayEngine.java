package com.pragma.riskengine.replay;

import com.pragma.riskengine.RiskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Motor de replay determinístico para post-mortem de incidentes.
 * Graba eventos con timestamp nanosegundos para garantizar reproducibililidad
 * exacta del comportamiento del sistema.
 */
public class DeterministicReplayEngine {
    private static final Logger logger = LoggerFactory.getLogger(DeterministicReplayEngine.class);
    private static final int DEFAULT_MAX_EVENTS_PER_SESSION = 1_000_000;
    private static final int MAX_REPLAY_BATCH_SIZE = 1000;
    
    private final int maxEventsPerSession;
    private final Map<String, EventSession> sessions;
    private final Map<String, String> correlationIdToSession;
    private final List<ReplayListener> listeners;
    private final ReplayScheduler scheduler;
    private volatile boolean recordingEnabled;
    
    public DeterministicReplayEngine() {
        this(DEFAULT_MAX_EVENTS_PER_SESSION);
    }
    
    public DeterministicReplayEngine(int maxEventsPerSession) {
        this.maxEventsPerSession = maxEventsPerSession;
        this.sessions = new ConcurrentHashMap<>();
        this.correlationIdToSession = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.scheduler = new ReplayScheduler();
        this.recordingEnabled = true;
        
        logger.info("DeterministicReplayEngine initialized with max {} events per session", 
            maxEventsPerSession);
    }
    
    /**
     * Inicia una nueva sesión de grabación para un correlationId.
     */
    public String startRecordingSession(String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        EventSession session = new EventSession(correlationId, maxEventsPerSession);
        sessions.put(correlationId, session);
        
        logger.info("Started recording session for correlationId: {}", correlationId);
        notifyListeners(l -> l.onSessionStarted(correlationId));
        
        return correlationId;
    }
    
    /**
     * Graba un evento en la sesión activa.
     */
    public void recordEvent(RiskEvent event) {
        if (!recordingEnabled) {
            logger.debug("Recording disabled, skipping event {}", event.getEventId());
            return;
        }
        
        if (event == null) {
            logger.warn("Received null event, skipping");
            return;
        }
        
        String correlationId = event.getCorrelationId();
        if (correlationId == null) {
            correlationId = "default";
        }
        
        EventSession session = sessions.get(correlationId);
        if (session == null) {
            logger.debug("No active session for {}, creating default session", correlationId);
            startRecordingSession(correlationId);
            session = sessions.get(correlationId);
        }
        
        boolean recorded = session.addEvent(event);
        if (recorded) {
            logger.trace("Recorded event {} to session {}", event.getEventId(), correlationId);
        } else {
            logger.warn("Failed to record event {} - session {} is full", 
                event.getEventId(), correlationId);
        }
        
        notifyListeners(l -> l.onEventRecorded(event, correlationId));
    }
    
    /**
     * Obtiene los eventos grabados para un correlationId.
     */
    public List<RiskEvent> getRecordedEvents(String correlationId) {
        EventSession session = sessions.get(correlationId);
        if (session == null) {
            logger.debug("No session found for correlationId: {}", correlationId);
            return Collections.emptyList();
        }
        return session.getEvents();
    }
    
    /**
     * Obtiene los eventos grabados ordenados por timestamp nanosegundos.
     */
    public List<RiskEvent> getRecordedEventsSorted(String correlationId) {
        List<RiskEvent> events = getRecordedEvents(correlationId);
        List<RiskEvent> sorted = new ArrayList<>(events);
        sorted.sort(Comparator.comparingLong(RiskEvent::getNanoTimestamp));
        return sorted;
    }
    
    /**
     * Inicia un replay determinístico para un correlationId dado.
     * Los eventos se reproducen en orden exacto de timestamp nanosegundos.
     */
    public void startReplay(String correlationId, Consumer<RiskEvent> callback) {
        if (correlationId == null || callback == null) {
            throw new IllegalArgumentException("correlationId and callback must not be null");
        }
        
        EventSession session = sessions.get(correlationId);
        if (session == null) {
            logger.error("Cannot replay - no session found for correlationId: {}", correlationId);
            throw new IllegalArgumentException("No session found: " + correlationId);
        }
        
        List<RiskEvent> events = getRecordedEventsSorted(correlationId);
        if (events.isEmpty()) {
            logger.warn("Replay requested but session {} is empty", correlationId);
            return;
        }
        
        logger.info("Starting deterministic replay for session {} with {} events", 
            correlationId, events.size());
        
        notifyListeners(l -> l.onReplayStarted(correlationId, events.size()));
        
        scheduler.schedule(events, callback, (completed, total) -> {
            logger.info("Replay completed: {}/{} events for {}", completed, total, correlationId);
            notifyListeners(l -> l.onReplayCompleted(correlationId, completed, total));
        });
    }
    
    /**
     * Obtiene el número de eventos grabados en una sesión.
     */
    public int getEventCount(String correlationId) {
        EventSession session = sessions.get(correlationId);
        return session != null ? session.getEventCount() : 0;
    }
    
    /**
     * Finaliza una sesión de grabación y opcionalmente la persiste.
     */
    public void endSession(String correlationId) {
        EventSession session = sessions.remove(correlationId);
        if (session != null) {
            logger.info("Ended session {} with {} events", correlationId, session.getEventCount());
            notifyListeners(l -> l.onSessionEnded(correlationId, session.getEventCount()));
        }
    }
    
    /**
     * Habilita o deshabilita la grabación de eventos.
     */
    public void setRecordingEnabled(boolean enabled) {
        this.recordingEnabled = enabled;
        logger.info("Recording enabled: {}", enabled);
    }
    
    /**
     * Agrega un listener para eventos del motor de replay.
     */
    public void addListener(ReplayListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remueve un listener registrado.
     */
    public void removeListener(ReplayListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(Consumer<ReplayListener> action) {
        for (ReplayListener listener : listeners) {
            try {
                action.accept(listener);
            } catch (Exception e) {
                logger.error("Error notifying listener: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * Interfaz para escuchar eventos del motor de replay.
     */
    public interface ReplayListener {
        default void onSessionStarted(String correlationId) {}
        default void onSessionEnded(String correlationId, int eventCount) {}
        default void onEventRecorded(RiskEvent event, String correlationId) {}
        default void onReplayStarted(String correlationId, int eventCount) {}
        default void onReplayCompleted(String correlationId, int completed, int total) {}
    }
    
    /**
     * Sesión de grabación de eventos para un correlationId.
     */
    private static class EventSession {
        private final String correlationId;
        private final int maxEvents;
        private final List<RiskEvent> events;
        private volatile boolean closed;
        
        public EventSession(String correlationId, int maxEvents) {
            this.correlationId = correlationId;
            this.maxEvents = maxEvents;
            this.events = new ArrayList<>(Math.min(maxEvents, 10000));
            this.closed = false;
        }
        
        public synchronized boolean addEvent(RiskEvent event) {
            if (closed || events.size() >= maxEvents) {
                return false;
            }
            events.add(event);
            return true;
        }
        
        public List<RiskEvent> getEvents() {
            return List.copyOf(events);
        }
        
        public int getEventCount() {
            return events.size();
        }
        
        public synchronized void close() {
            this.closed = true;
        }
    }
    
    /**
     * Scheduler para ejecutar replay en batches controlados.
     */
    private static class ReplayScheduler {
        public void schedule(List<RiskEvent> events, Consumer<RiskEvent> callback, 
                            java.util.function.BiConsumer<Integer, Integer> onComplete) {
            int total = events.size();
            int processed = 0;
            
            for (int i = 0; i < total; i += MAX_REPLAY_BATCH_SIZE) {
                int batchEnd = Math.min(i + MAX_REPLAY_BATCH_SIZE, total);
                
                for (int j = i; j < batchEnd; j++) {
                    try {
                        callback.accept(events.get(j));
                        processed++;
                    } catch (Exception e) {
                        logger.error("Error processing event in replay at index {}: {}", j, e.getMessage());
                    }
                }
                
                if (batchEnd < total) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            onComplete.accept(processed, total);
        }
    }
}
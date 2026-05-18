package com.vhalinor.ai.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Orquestrador de Estado Neural - Integra todos os componentes de gestão de estado
 * Versão: 1.0
 * Responsabilidades: Coordenação, Resiliência, Monitoramento, Recuperação
 */
public class NeuralStateOrchestrator implements NeuralStateSynchronizer.SyncListener {
    private static final Logger LOG = Logger.getLogger(NeuralStateOrchestrator.class.getName());

    private final NeuralStateManager stateManager;
    private final NeuralStateCache stateCache;
    private final NeuralStateSynchronizer synchronizer;
    private final String orchestratorId;
    private final ScheduledExecutorService monitoringExecutor = Executors.newScheduledThreadPool(2);
    private final List<OrchestrationListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean isRunning = false;

    /**
     * Listener para eventos de orquestração
     */
    public interface OrchestrationListener {
        void onStateChange(String stateId);
        void onSyncConflict(String stateId, Exception conflict);
        void onHealthCheck(NeuralStateHealth health);
    }

    /**
     * Saúde do sistema de estado
     */
    public static class NeuralStateHealth {
        public boolean cacheHealthy;
        public boolean databaseHealthy;
        public boolean synchronizerHealthy;
        public int cachedStates;
        public double cacheHitRate;
        public int pendingSyncs;
        public String timestamp;

        @Override
        public String toString() {
            return String.format("Health{cache=%s, db=%s, sync=%s, cached=%d, hitRate=%.1f%%, pending=%d}", 
                cacheHealthy, databaseHealthy, synchronizerHealthy, cachedStates, 
                cacheHitRate * 100, pendingSyncs);
        }
    }

    public NeuralStateOrchestrator(String orchestratorId, String stateDir) throws Exception {
        this.orchestratorId = orchestratorId;
        this.stateManager = new NeuralStateManager(stateDir);
        this.stateCache = new NeuralStateCache(500, 600, stateManager);
        this.synchronizer = new NeuralStateSynchronizer(orchestratorId);
        this.synchronizer.addSyncListener(this);

        startHealthMonitoring();
        LOG.info("✅ NeuralStateOrchestrator initialized: " + orchestratorId);
    }

    /**
     * Registra listener de orquestração
     */
    public void addListener(OrchestrationListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove listener
     */
    public void removeListener(OrchestrationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifica listeners de mudança de estado
     */
    private void notifyStateChange(String stateId) {
        for (OrchestrationListener listener : listeners) {
            try {
                listener.onStateChange(stateId);
            } catch (Exception e) {
                LOG.warning("Listener error: " + e.getMessage());
            }
        }
    }

    /**
     * Notifica listeners de conflito
     */
    private void notifySyncConflict(String stateId, Exception conflict) {
        for (OrchestrationListener listener : listeners) {
            try {
                listener.onSyncConflict(stateId, conflict);
            } catch (Exception e) {
                LOG.warning("Listener error: " + e.getMessage());
            }
        }
    }

    /**
     * Notifica listeners de health check
     */
    private void notifyHealthCheck(NeuralStateHealth health) {
        for (OrchestrationListener listener : listeners) {
            try {
                listener.onHealthCheck(health);
            } catch (Exception e) {
                LOG.warning("Listener error: " + e.getMessage());
            }
        }
    }

    /**
     * Inicia sistema de monitoramento
     */
    private void startHealthMonitoring() {
        isRunning = true;
        monitoringExecutor.scheduleAtFixedRate(() -> {
            if (!isRunning) return;
            try {
                NeuralStateHealth health = performHealthCheck();
                notifyHealthCheck(health);
                LOG.fine("📊 Health: " + health);
            } catch (Exception e) {
                LOG.warning("Health check error: " + e.getMessage());
            }
        }, 10, 30, TimeUnit.SECONDS);
    }

    /**
     * Realiza verificação de saúde
     */
    public NeuralStateHealth performHealthCheck() {
        NeuralStateHealth health = new NeuralStateHealth();
        
        // Cache health
        NeuralStateCache.CacheStatistics cacheStats = stateCache.getStatistics();
        health.cachedStates = cacheStats.size;
        health.cacheHitRate = cacheStats.hitRate;
        health.cacheHealthy = health.cachedStates > 0 && health.cacheHitRate > 0.5;

        // Database health
        try {
            List<NeuralStateManager.SystemStateMetadata> states = stateManager.listAllStates();
            health.databaseHealthy = true;
        } catch (Exception e) {
            health.databaseHealthy = false;
            LOG.warning("Database health check failed: " + e.getMessage());
        }

        // Synchronizer health
        List<Map<String, Object>> syncStatus = synchronizer.getSyncStatus();
        long pendingCount = syncStatus.stream()
            .filter(s -> !(boolean) s.getOrDefault("synchronized", true))
            .count();
        health.pendingSyncs = (int) pendingCount;
        health.synchronizerHealthy = health.pendingSyncs < 10;

        health.timestamp = java.time.LocalDateTime.now().toString();
        return health;
    }

    /**
     * Salva estado neural com sincronização
     */
    public String saveNeuralState(String stateId, NeuralStateManager.NeuralSnapshot snapshot) throws Exception {
        try {
            // Salvar no cache e disco
            stateCache.saveState(stateId, snapshot);

            // Registrar versão local
            synchronizer.updateLocalState(stateId, "Snapshot saved");

            // Notificar listeners
            notifyStateChange(stateId);

            LOG.info("💾 Neural state saved: " + stateId);
            return stateId;
        } catch (Exception e) {
            LOG.severe("❌ Failed to save state: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Carrega estado neural com fallback
     */
    public NeuralStateManager.NeuralSnapshot loadNeuralState(String stateId) throws Exception {
        try {
            // Tentar cache primeiro
            Object cached = stateCache.get(stateId);
            if (cached instanceof NeuralStateManager.NeuralSnapshot) {
                LOG.fine("✓ Loaded from cache: " + stateId);
                return (NeuralStateManager.NeuralSnapshot) cached;
            }

            // Carregar do disco
            NeuralStateManager.NeuralSnapshot snapshot = stateManager.loadSnapshot(stateId);
            if (snapshot != null) {
                stateCache.put(stateId, snapshot);
                LOG.info("📂 Loaded from disk: " + stateId);
                return snapshot;
            }

            LOG.warning("⚠️ State not found: " + stateId);
            return null;
        } catch (Exception e) {
            LOG.severe("❌ Failed to load state: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Sincroniza estado com outro nó
     */
    public void syncWithRemote(String stateId, NeuralStateSynchronizer.StateVersion remoteVersion) throws Exception {
        try {
            synchronizer.receiveRemoteState(stateId, remoteVersion);
            LOG.info("🔄 State synchronized: " + stateId);
        } catch (NeuralStateSynchronizer.SyncConflictException e) {
            LOG.warning("⚠️ Sync conflict: " + e.getMessage());
            
            // Resolver conflito automaticamente (estratégia: maior versão)
            NeuralStateSynchronizer.StateVersion local = synchronizer.getLocalVersion(stateId);
            NeuralStateSynchronizer.StateVersion resolved = synchronizer.resolveConflict(stateId, local, remoteVersion);
            
            notifySyncConflict(stateId, e);
            LOG.info("✓ Conflict resolved: " + resolved);
        }
    }

    /**
     * Implementação de SyncListener
     */
    @Override
    public void onSyncEvent(NeuralStateSynchronizer.SyncEvent event) {
        LOG.fine("📨 Sync event: " + event);
        if (event.type == NeuralStateSynchronizer.SyncEvent.Type.STATE_CHANGED) {
            notifyStateChange(event.stateId);
        }
    }

    @Override
    public void onConflict(String stateId, NeuralStateSynchronizer.StateVersion local, 
                           NeuralStateSynchronizer.StateVersion remote) {
        Exception conflict = new Exception(
            "Version conflict: local v" + local.version + " vs remote v" + remote.version
        );
        notifySyncConflict(stateId, conflict);
    }

    /**
     * Realiza checkpoint do estado
     */
    public void performCheckpoint() throws Exception {
        LOG.info("💾 Performing checkpoint...");
        stateCache.checkpoint();
        LOG.info("✅ Checkpoint completed");
    }

    /**
     * Recupera estado de checkpoint anterior
     */
    public void recoverFromCheckpoint() throws Exception {
        LOG.info("📂 Recovering from checkpoint...");
        stateCache.restoreFromCheckpoint();
        LOG.info("✅ Recovery completed");
    }

    /**
     * Lista todos os estados
     */
    public List<String> listAllStates() throws Exception {
        return new ArrayList<>(stateCache.listEntries());
    }

    /**
     * Obtém status de sincronização
     */
    public List<Map<String, Object>> getSyncStatus() {
        return synchronizer.getSyncStatus();
    }

    /**
     * Inicia sincronização periódica
     */
    public void startPeriodicSync(long intervalSeconds) {
        synchronizer.startPeriodicSync(intervalSeconds);
        LOG.info("⏱️ Periodic sync started: interval=" + intervalSeconds + "s");
    }

    /**
     * Limpa estados antigos
     */
    public void cleanupOldStates(int daysOld) throws Exception {
        stateManager.removeOldStates(daysOld);
        LOG.info("🗑️ Old states removed (older than " + daysOld + " days)");
    }

    /**
     * Fecha orquestrador
     */
    public void close() throws Exception {
        isRunning = false;
        monitoringExecutor.shutdown();
        try {
            if (!monitoringExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                monitoringExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            monitoringExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        stateCache.close();
        synchronizer.close();
        stateManager.close();

        LOG.info("✅ NeuralStateOrchestrator closed");
    }

    /**
     * Exemplo de uso
     */
    public static void main(String[] args) throws Exception {
        NeuralStateOrchestrator orchestrator = 
            new NeuralStateOrchestrator("orchestrator_1", "./neural_state");

        // Adicionar listener
        orchestrator.addListener(new OrchestrationListener() {
            @Override
            public void onStateChange(String stateId) {
                System.out.println("📝 State changed: " + stateId);
            }

            @Override
            public void onSyncConflict(String stateId, Exception conflict) {
                System.out.println("⚠️ Sync conflict: " + stateId + " - " + conflict.getMessage());
            }

            @Override
            public void onHealthCheck(NeuralStateHealth health) {
                System.out.println("🏥 Health: " + health);
            }
        });

        // Criar snapshots
        for (int i = 1; i <= 3; i++) {
            NeuralStateManager.NeuralSnapshot snapshot = 
                new NeuralStateManager.NeuralSnapshot("neural_" + i);
            snapshot.energyLevel = 90 + i * 2;
            snapshot.cycleCount = 1000 * i;
            
            orchestrator.saveNeuralState("neural_" + i, snapshot);
        }

        // Listar estados
        System.out.println("\n📋 All states:");
        for (String state : orchestrator.listAllStates()) {
            System.out.println("  - " + state);
        }

        // Verificação de saúde
        System.out.println("\n🏥 Health check:");
        System.out.println(orchestrator.performHealthCheck());

        // Status de sincronização
        System.out.println("\n📊 Sync status:");
        for (Map<String, Object> status : orchestrator.getSyncStatus()) {
            System.out.println("  " + status);
        }

        // Checkpoint
        orchestrator.performCheckpoint();

        orchestrator.close();
    }
}

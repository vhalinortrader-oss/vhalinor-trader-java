package com.vhalinor.ai.core;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.logging.*;

/**
 * Sincronizador de Estado Neural - Gerencia sincronização entre múltiplas instâncias
 * Versão: 1.0
 * Recursos: Replicação de Estado, Resolução de Conflitos, Transações Distribuídas
 */
public class NeuralStateSynchronizer {
    private static final Logger LOG = Logger.getLogger(NeuralStateSynchronizer.class.getName());

    /**
     * Versão de estado para controle de sincronização
     */
    public static class StateVersion implements Comparable<StateVersion> {
        public String stateId;
        public long version;
        public LocalDateTime timestamp;
        public String nodeId;
        public String hash;
        public List<String> changes;

        public StateVersion(String stateId, String nodeId) {
            this.stateId = stateId;
            this.nodeId = nodeId;
            this.version = 1;
            this.timestamp = LocalDateTime.now();
            this.changes = new ArrayList<>();
        }

        @Override
        public int compareTo(StateVersion other) {
            // Versão mais alta ganha, desempate por timestamp
            int versionCmp = Long.compare(this.version, other.version);
            if (versionCmp != 0) return versionCmp;
            return this.timestamp.compareTo(other.timestamp);
        }

        @Override
        public String toString() {
            return String.format("StateVersion{state=%s, v=%d, node=%s, ts=%s}", 
                stateId, version, nodeId, timestamp);
        }
    }

    /**
     * Evento de sincronização
     */
    public static class SyncEvent {
        public enum Type {
            STATE_CHANGED, CONFLICT_DETECTED, SYNC_COMPLETE, SYNC_FAILED
        }

        public Type type;
        public String stateId;
        public StateVersion version;
        public LocalDateTime timestamp;
        public String message;

        public SyncEvent(Type type, String stateId, StateVersion version, String message) {
            this.type = type;
            this.stateId = stateId;
            this.version = version;
            this.timestamp = LocalDateTime.now();
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("SyncEvent{type=%s, state=%s, v=%s, msg=%s}", 
                type, stateId, version, message);
        }
    }

    private final String nodeId;
    private final Map<String, StateVersion> localVersions = new ConcurrentHashMap<>();
    private final Map<String, StateVersion> remoteVersions = new ConcurrentHashMap<>();
    private final List<SyncEvent> syncHistory = Collections.synchronizedList(new ArrayList<>());
    private final ReadWriteLock versionLock = new ReentrantReadWriteLock();
    private final BlockingQueue<SyncEvent> eventQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService syncExecutor = Executors.newScheduledThreadPool(2);
    private final List<SyncListener> listeners = Collections.synchronizedList(new ArrayList<>());

    /**
     * Listener para eventos de sincronização
     */
    public interface SyncListener {
        void onSyncEvent(SyncEvent event);
        void onConflict(String stateId, StateVersion local, StateVersion remote);
    }

    public NeuralStateSynchronizer(String nodeId) {
        this.nodeId = nodeId;
        startEventProcessor();
        LOG.info("✅ NeuralStateSynchronizer initialized for node: " + nodeId);
    }

    /**
     * Registra listener
     */
    public void addSyncListener(SyncListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove listener
     */
    public void removeSyncListener(SyncListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifica listeners de evento
     */
    private void notifyListeners(SyncEvent event) {
        for (SyncListener listener : listeners) {
            try {
                listener.onSyncEvent(event);
            } catch (Exception e) {
                LOG.warning("Listener error: " + e.getMessage());
            }
        }
    }

    /**
     * Atualiza versão local de estado
     */
    public void updateLocalState(String stateId, String changeDescription) {
        versionLock.writeLock().lock();
        try {
            StateVersion version = localVersions.get(stateId);
            if (version == null) {
                version = new StateVersion(stateId, nodeId);
            } else {
                version.version++;
                version.timestamp = LocalDateTime.now();
            }

            version.changes.add(changeDescription);
            localVersions.put(stateId, version);

            SyncEvent event = new SyncEvent(
                SyncEvent.Type.STATE_CHANGED, 
                stateId, 
                version, 
                "Local state updated"
            );
            eventQueue.offer(event);
            
            LOG.fine("📝 Local state updated: " + version);
        } finally {
            versionLock.writeLock().unlock();
        }
    }

    /**
     * Registra versão remota recebida
     */
    public void receiveRemoteState(String stateId, StateVersion remoteVersion) throws SyncConflictException {
        versionLock.writeLock().lock();
        try {
            StateVersion local = localVersions.get(stateId);
            StateVersion existing = remoteVersions.get(stateId);

            // Detectar conflito
            if (local != null && remoteVersion.version > local.version &&
                !remoteVersion.nodeId.equals(local.nodeId)) {
                
                if (existing != null && existing.compareTo(remoteVersion) == 0) {
                    // Mesma versão remota, sem conflito
                    LOG.fine("✓ Remote state consistent: " + remoteVersion);
                } else {
                    // Conflito detectado
                    LOG.warning("⚠️ Conflict detected for " + stateId);
                    SyncEvent event = new SyncEvent(
                        SyncEvent.Type.CONFLICT_DETECTED,
                        stateId,
                        remoteVersion,
                        "Version mismatch with remote"
                    );
                    eventQueue.offer(event);

                    for (SyncListener listener : listeners) {
                        listener.onConflict(stateId, local, remoteVersion);
                    }
                    
                    throw new SyncConflictException(
                        "Conflict: local v" + local.version + " vs remote v" + remoteVersion.version
                    );
                }
            }

            remoteVersions.put(stateId, remoteVersion);
            LOG.fine("📥 Remote state received: " + remoteVersion);
        } finally {
            versionLock.writeLock().unlock();
        }
    }

    /**
     * Resolve conflito usando estratégia de maior versão
     */
    public StateVersion resolveConflict(String stateId, StateVersion local, StateVersion remote) {
        versionLock.writeLock().lock();
        try {
            StateVersion winner = local.compareTo(remote) >= 0 ? local : remote;
            winner.version = Math.max(local.version, remote.version) + 1;
            winner.timestamp = LocalDateTime.now();
            
            localVersions.put(stateId, winner);
            
            LOG.info("✓ Conflict resolved using version: " + winner);
            return winner;
        } finally {
            versionLock.writeLock().unlock();
        }
    }

    /**
     * Obtém versão local
     */
    public StateVersion getLocalVersion(String stateId) {
        versionLock.readLock().lock();
        try {
            return localVersions.get(stateId);
        } finally {
            versionLock.readLock().unlock();
        }
    }

    /**
     * Obtém versão remota
     */
    public StateVersion getRemoteVersion(String stateId) {
        versionLock.readLock().lock();
        try {
            return remoteVersions.get(stateId);
        } finally {
            versionLock.readLock().unlock();
        }
    }

    /**
     * Verifica se estado está em sincronização
     */
    public boolean isSynchronized(String stateId) {
        versionLock.readLock().lock();
        try {
            StateVersion local = localVersions.get(stateId);
            StateVersion remote = remoteVersions.get(stateId);
            
            if (local == null || remote == null) return false;
            
            return local.version == remote.version;
        } finally {
            versionLock.readLock().unlock();
        }
    }

    /**
     * Lista todos os estados e suas versões
     */
    public List<Map<String, Object>> getSyncStatus() {
        List<Map<String, Object>> status = new ArrayList<>();
        versionLock.readLock().lock();
        try {
            Set<String> allStates = new HashSet<>();
            allStates.addAll(localVersions.keySet());
            allStates.addAll(remoteVersions.keySet());

            for (String stateId : allStates) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("stateId", stateId);
                
                StateVersion local = localVersions.get(stateId);
                if (local != null) {
                    entry.put("localVersion", local.version);
                    entry.put("localNode", local.nodeId);
                }
                
                StateVersion remote = remoteVersions.get(stateId);
                if (remote != null) {
                    entry.put("remoteVersion", remote.version);
                    entry.put("remoteNode", remote.nodeId);
                }
                
                entry.put("synchronized", isSynchronized(stateId));
                status.add(entry);
            }
        } finally {
            versionLock.readLock().unlock();
        }
        
        return status;
    }

    /**
     * Obtém histórico de sincronização
     */
    public List<SyncEvent> getSyncHistory(int limit) {
        int start = Math.max(0, syncHistory.size() - limit);
        return new ArrayList<>(syncHistory.subList(start, syncHistory.size()));
    }

    /**
     * Inicia processador de eventos
     */
    private void startEventProcessor() {
        syncExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SyncEvent event = eventQueue.poll(5, TimeUnit.SECONDS);
                    if (event != null) {
                        syncHistory.add(event);
                        notifyListeners(event);
                        
                        if (syncHistory.size() > 10000) {
                            syncHistory.remove(0);
                        }
                        
                        LOG.fine("📨 Sync event processed: " + event);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Inicia sincronização periódica
     */
    public void startPeriodicSync(long intervalSeconds) {
        syncExecutor.scheduleAtFixedRate(() -> {
            try {
                List<Map<String, Object>> status = getSyncStatus();
                for (Map<String, Object> entry : status) {
                    if (!(boolean) entry.getOrDefault("synchronized", true)) {
                        LOG.info("⏱️ Sync status: " + entry);
                    }
                }
            } catch (Exception e) {
                LOG.warning("Periodic sync check error: " + e.getMessage());
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * Fecha sincronizador
     */
    public void close() {
        syncExecutor.shutdown();
        try {
            if (!syncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                syncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            syncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOG.info("NeuralStateSynchronizer closed");
    }

    /**
     * Exceção de conflito de sincronização
     */
    public static class SyncConflictException extends Exception {
        public SyncConflictException(String message) {
            super(message);
        }
    }

    /**
     * Exemplo de uso
     */
    public static void main(String[] args) throws Exception {
        NeuralStateSynchronizer sync = new NeuralStateSynchronizer("node_1");

        // Adicionar listener
        sync.addSyncListener(new SyncListener() {
            @Override
            public void onSyncEvent(SyncEvent event) {
                System.out.println("🔔 Sync event: " + event);
            }

            @Override
            public void onConflict(String stateId, StateVersion local, StateVersion remote) {
                System.out.println("⚠️ Conflict: " + stateId + " (local v" + local.version + " vs remote v" + remote.version + ")");
            }
        });

        // Atualizar estado local
        sync.updateLocalState("neural_001", "Learning phase initialized");
        sync.updateLocalState("neural_001", "Processing market data");
        sync.updateLocalState("neural_002", "Initialization started");

        // Simular versão remota
        StateVersion remote = new StateVersion("neural_001", "node_2");
        remote.version = 2;
        sync.receiveRemoteState("neural_001", remote);

        // Status de sincronização
        System.out.println("\n📊 Sync Status:");
        for (Map<String, Object> entry : sync.getSyncStatus()) {
            System.out.println("  " + entry);
        }

        // Histórico
        System.out.println("\n📋 Sync History:");
        for (SyncEvent event : sync.getSyncHistory(10)) {
            System.out.println("  " + event);
        }

        sync.close();
    }
}

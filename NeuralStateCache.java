package com.vhalinor.ai.core;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Cache de Estado Neural - Mantém estado em memória com persistência
 * Versão: 1.0
 * Recursos: LRU Cache, Snapshots, Recuperação de Falhas, Versionamento
 */
public class NeuralStateCache {
    private static final Logger LOG = Logger.getLogger(NeuralStateCache.class.getName());
    
    private final int maxCacheSize;
    private final long ttlMillis;
    private final LinkedHashMap<String, CacheEntry> cache;
    private final NeuralStateManager stateManager;
    private final ScheduledExecutorService cleanupExecutor = Executors.newScheduledThreadPool(1);

    /**
     * Entrada do cache com metadados
     */
    private static class CacheEntry {
        String id;
        Object state;
        long timestamp;
        int accessCount;
        long lastAccessTime;

        CacheEntry(String id, Object state) {
            this.id = id;
            this.state = state;
            this.timestamp = System.currentTimeMillis();
            this.lastAccessTime = timestamp;
            this.accessCount = 0;
        }

        boolean isExpired(long ttlMillis) {
            return (System.currentTimeMillis() - timestamp) > ttlMillis;
        }

        @Override
        public String toString() {
            return String.format("CacheEntry{id=%s, accessed=%d times, age=%dms}", 
                id, accessCount, System.currentTimeMillis() - timestamp);
        }
    }

    /**
     * Estatísticas do cache
     */
    public static class CacheStatistics {
        public int size;
        public int hits;
        public int misses;
        public double hitRate;
        public long memoryUsage;
        public LocalDateTime createdAt;

        @Override
        public String toString() {
            return String.format("CacheStats{size=%d, hits=%d, misses=%d, hitRate=%.2f%%}", 
                size, hits, misses, hitRate * 100);
        }
    }

    private long hits = 0;
    private long misses = 0;

    public NeuralStateCache(int maxSize, long ttlSeconds, NeuralStateManager stateManager) {
        this.maxCacheSize = maxSize;
        this.ttlMillis = ttlSeconds * 1000;
        this.stateManager = stateManager;
        
        // LinkedHashMap com ordem de acesso LRU
        this.cache = new LinkedHashMap<String, CacheEntry>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxCacheSize;
            }
        };

        startCleanupTask();
        LOG.info("✅ NeuralStateCache initialized (max=" + maxSize + ", ttl=" + ttlSeconds + "s)");
    }

    /**
     * Coloca valor no cache
     */
    public synchronized void put(String key, Object value) {
        cache.put(key, new CacheEntry(key, value));
        LOG.fine("📝 Cache put: " + key);
    }

    /**
     * Recupera valor do cache
     */
    public synchronized Object get(String key) {
        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            misses++;
            LOG.fine("❌ Cache miss: " + key);
            return null;
        }

        if (entry.isExpired(ttlMillis)) {
            cache.remove(key);
            misses++;
            LOG.fine("⏰ Cache expired: " + key);
            return null;
        }

        entry.accessCount++;
        entry.lastAccessTime = System.currentTimeMillis();
        hits++;
        LOG.fine("✓ Cache hit: " + key);
        return entry.state;
    }

    /**
     * Salva estado no cache e persiste
     */
    public synchronized void saveState(String stateId, Object state) throws Exception {
        put(stateId, state);
        
        // Persistir se for um snapshot
        if (state instanceof NeuralStateManager.NeuralSnapshot) {
            stateManager.saveSnapshot((NeuralStateManager.NeuralSnapshot) state);
        }
        
        LOG.info("💾 State saved and cached: " + stateId);
    }

    /**
     * Carrega estado do cache ou disco
     */
    public synchronized Object loadState(String stateId) throws Exception {
        // Tentar cache primeiro
        Object cached = get(stateId);
        if (cached != null) {
            return cached;
        }

        // Carregar do disco
        Object loaded = stateManager.loadSnapshot(stateId);
        if (loaded != null) {
            put(stateId, loaded);
            LOG.info("📂 State loaded from disk: " + stateId);
        }
        
        return loaded;
    }

    /**
     * Remove entrada do cache
     */
    public synchronized void remove(String key) {
        cache.remove(key);
        LOG.fine("🗑️ Cache entry removed: " + key);
    }

    /**
     * Limpa todo o cache
     */
    public synchronized void clear() {
        int size = cache.size();
        cache.clear();
        LOG.info("🧹 Cache cleared (" + size + " entries)");
    }

    /**
     * Obtém tamanho do cache
     */
    public synchronized int size() {
        return cache.size();
    }

    /**
     * Obtém todas as chaves
     */
    public synchronized Set<String> keySet() {
        return new HashSet<>(cache.keySet());
    }

    /**
     * Obtém estatísticas do cache
     */
    public synchronized CacheStatistics getStatistics() {
        CacheStatistics stats = new CacheStatistics();
        stats.size = cache.size();
        stats.hits = (int) hits;
        stats.misses = (int) misses;
        stats.hitRate = (hits + misses) > 0 ? (double) hits / (hits + misses) : 0.0;
        stats.memoryUsage = estimateMemoryUsage();
        stats.createdAt = LocalDateTime.now();
        return stats;
    }

    /**
     * Estima uso de memória do cache
     */
    private long estimateMemoryUsage() {
        long total = 0;
        for (CacheEntry entry : cache.values()) {
            if (entry.state != null) {
                total += entry.state.toString().length() * 2; // Estimativa bruta
            }
        }
        return total;
    }

    /**
     * Lista entradas do cache em ordem de acesso
     */
    public synchronized List<String> listEntries() {
        return new ArrayList<>(cache.keySet());
    }

    /**
     * Encontra entradas antigas
     */
    public synchronized List<String> findOldEntries(long ageMillis) {
        List<String> old = new ArrayList<>();
        long cutoff = System.currentTimeMillis() - ageMillis;
        
        for (CacheEntry entry : cache.values()) {
            if (entry.lastAccessTime < cutoff) {
                old.add(entry.id);
            }
        }
        
        return old;
    }

    /**
     * Remove entradas antigas
     */
    public synchronized void removeOldEntries(long ageMillis) {
        List<String> old = findOldEntries(ageMillis);
        for (String id : old) {
            remove(id);
        }
        LOG.info("🧹 Removed " + old.size() + " old entries");
    }

    /**
     * Inicia tarefa de limpeza periódica
     */
    private void startCleanupTask() {
        cleanupExecutor.scheduleAtFixedRate(() -> {
            try {
                synchronized (this) {
                    // Remover entradas expiradas
                    List<String> toRemove = new ArrayList<>();
                    for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
                        if (entry.getValue().isExpired(ttlMillis)) {
                            toRemove.add(entry.getKey());
                        }
                    }
                    
                    for (String key : toRemove) {
                        cache.remove(key);
                    }
                    
                    if (!toRemove.isEmpty()) {
                        LOG.fine("⏰ Cleanup removed " + toRemove.size() + " expired entries");
                    }
                }
            } catch (Exception e) {
                LOG.warning("Cleanup error: " + e.getMessage());
            }
        }, ttlMillis / 2, ttlMillis / 2, TimeUnit.MILLISECONDS);
    }

    /**
     * Persiste todo cache em disco (checkpoint)
     */
    public synchronized void checkpoint() throws Exception {
        int count = 0;
        for (CacheEntry entry : cache.values()) {
            if (entry.state instanceof NeuralStateManager.NeuralSnapshot) {
                stateManager.saveSnapshot((NeuralStateManager.NeuralSnapshot) entry.state);
                count++;
            }
        }
        LOG.info("✅ Checkpoint completed: " + count + " states persisted");
    }

    /**
     * Recupera cache de checkpoint anterior
     */
    public synchronized void restoreFromCheckpoint() throws Exception {
        List<NeuralStateManager.SystemStateMetadata> states = stateManager.listAllStates();
        int count = 0;
        
        for (NeuralStateManager.SystemStateMetadata metadata : states) {
            if (cache.size() < maxCacheSize) {
                Object snapshot = stateManager.loadSnapshot(metadata.stateId);
                if (snapshot != null) {
                    put(metadata.stateId, snapshot);
                    count++;
                }
            }
        }
        
        LOG.info("📂 Restored " + count + " states from checkpoint");
    }

    /**
     * Fecha cache e executor
     */
    public void close() {
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOG.info("Cache closed");
    }

    /**
     * Exemplo de uso
     */
    public static void main(String[] args) throws Exception {
        NeuralStateManager manager = new NeuralStateManager("./neural_state");
        NeuralStateCache cache = new NeuralStateCache(100, 300, manager);

        // Criar e cachear snapshots
        for (int i = 1; i <= 5; i++) {
            NeuralStateManager.NeuralSnapshot snapshot = 
                new NeuralStateManager.NeuralSnapshot("state_" + i);
            snapshot.energyLevel = 90 + i;
            snapshot.cycleCount = 1000 * i;
            
            cache.saveState("state_" + i, snapshot);
        }

        // Acessar estados (cache hits)
        for (int i = 1; i <= 5; i++) {
            cache.get("state_" + i);
        }

        // Estatísticas
        System.out.println("\n📊 Cache Statistics:");
        System.out.println(cache.getStatistics());

        // Listar entradas
        System.out.println("\n📋 Cache Entries:");
        for (String key : cache.listEntries()) {
            System.out.println("  - " + key);
        }

        // Checkpoint
        cache.checkpoint();

        cache.close();
        manager.close();
    }
}

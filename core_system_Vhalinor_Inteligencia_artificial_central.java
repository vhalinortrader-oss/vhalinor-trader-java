package com.vhalinor.brain;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * VHALINOR IAG 3.0.0 - "Quantum Singularity" (Java Edition)
 * Orquestrador Cerebral Quântico com Integração Total
 *
 * Melhorias em relação ao Python:
 * - Concorrência robusta com ExecutorService e CompletableFuture
 * - Thread‑safety via ConcurrentHashMap, CopyOnWriteArrayList e locks
 * - Graceful shutdown e AutoCloseable
 * - Logging estruturado com java.util.logging
 * - Records para imutabilidade
 * - Configuração centralizada e validação de entrada
 * - Simulação de bibliotecas externas (Qiskit, TensorFlow, etc.)
 */
public class IntegratedBrainOrchestrator implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(IntegratedBrainOrchestrator.class.getName());

    static {
        try {
            FileHandler fh = new FileHandler("vhalinor_brain.log", 10_485_760, 5, true);
            fh.setEncoding("UTF-8");
            fh.setFormatter(new SimpleFormatter());
            LOG.addHandler(fh);
            LOG.setUseParentHandlers(true);
            LOG.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Failed to initialize log file: " + e.getMessage());
        }
    }

    // ======================== CONSTANTES ========================
    public static final String VERSION = "3.0.0";
    public static final String CODENAME = "Quantum Singularity";
    public static final String BUILD_DATE = "2026-02-12";
    public static final String AUTHOR = "Alex Miranda Sales";

    private static final int MAX_WORKERS = Math.min(32, Runtime.getRuntime().availableProcessors() + 4);
    private static final long CACHE_TTL_DEFAULT = 3600;
    private static final int CACHE_MAX_SIZE = 10000;
    private static final int BATCH_SIZE_DEFAULT = 64;
    private static final double LEARNING_RATE_DEFAULT = 0.001;
    private static final long MEMORY_LIMIT_MB = 2048;
    private static final long TIMEOUT_DEFAULT = 30;
    private static final int MAX_NEURONS = 1_000_000;
    private static final int MAX_SYNAPSES = 10_000_000;
    private static final double NEURON_SPARSITY = 0.1;
    private static final double PLASTICITY_RATE = 0.01;
    private static final int QUANTUM_SHOTS = 1024;
    private static final int QUANTUM_QUBITS = 8;
    private static final int MAX_INPUT_SIZE = 10 * 1024 * 1024;
    private static final int RATE_LIMIT_REQUESTS = 1000;
    private static final int RATE_LIMIT_WINDOW = 60;

    private static final Random RANDOM = new SecureRandom();

    // ======================== ENUMS ========================
    public enum NeuronType {
        SENSORY("sensory", 0.3, 1.0),
        PROCESSING("processing", 0.5, 0.8),
        MEMORY("memory", 0.4, 1.2),
        DECISION("decision", 0.6, 1.5),
        OUTPUT("output", 0.3, 0.9),
        QUANTUM("quantum", 0.7, 2.0),
        VISION("vision", 0.4, 1.1),
        AUDITORY("auditory", 0.4, 1.1),
        MOTOR("motor", 0.5, 1.0),
        EMOTIONAL("emotional", 0.6, 0.7),
        CREATIVE("creative", 0.8, 1.3),
        PREDICTIVE("predictive", 0.6, 1.4),
        ANALYTICAL("analytical", 0.5, 1.2),
        SECURITY("security", 0.7, 1.6),
        NETWORK("network", 0.4, 1.0),
        API("api", 0.3, 0.8),
        DATABASE("database", 0.2, 1.0),
        GENERATIVE("generative", 0.8, 1.5),
        REINFORCEMENT("reinforcement", 0.7, 1.3),
        ATTENTION("attention", 0.6, 1.2),
        META("meta", 0.9, 1.8);

        public final String label;
        public final double defaultThreshold;
        public final double importance;

        NeuronType(String label, double defaultThreshold, double importance) {
            this.label = label;
            this.defaultThreshold = defaultThreshold;
            this.importance = importance;
        }
    }

    public enum BrainState {
        IDLE, PROCESSING, LEARNING, DREAMING, FOCUSED, CREATIVE, ANALYTICAL,
        INTUITIVE, MEDITATIVE, HYPER_FOCUS, MULTI_TASKING, OPTIMIZING,
        SECURITY_SCAN, BACKUP, RECOVERY, EMERGENCY
    }

    public enum SecurityLevel {
        PUBLIC(0), INTERNAL(1), CONFIDENTIAL(2), SECRET(3), TOP_SECRET(4);
        public final int level;
        SecurityLevel(int level) { this.level = level; }
    }

    public enum DataPriority {
        LOW(0), MEDIUM(1), HIGH(2), CRITICAL(3), EMERGENCY(4);
        public final int priority;
        DataPriority(int priority) { this.priority = priority; }
    }

    // ======================== RECORDS (DATA CLASSES) ========================
    public static class Neuron {
        public final String id;
        public final String filePath;
        public final NeuronType neuronType;
        public double activationThreshold;
        public double currentActivation;
        public final List<String> connections = Collections.synchronizedList(new ArrayList<>());
        public Instant lastFired;
        public double memoryWeight = 1.0;
        public double learningRate = 0.01;
        public double quantumEntanglement = 0.0;
        public long fileSize;
        public String fileExtension;
        public String contentHash;
        public Instant createdAt = Instant.now();
        public final Map<String, Object> metadata = new ConcurrentHashMap<>();

        // Advanced properties
        public final Deque<Double> activationHistory = new ConcurrentLinkedDeque<>();
        public int fireCount;
        public double learningCoefficient = 0.1;
        public double importanceScore = 1.0;
        public double energyLevel = 100.0;
        public Instant lastModified = Instant.now();
        public final List<String> dependencies = new CopyOnWriteArrayList<>();
        public SecurityLevel securityLevel = SecurityLevel.INTERNAL;
        public String version = "1.0";
        public final List<String> tags = new CopyOnWriteArrayList<>();

        public Neuron(String id, String filePath, NeuronType neuronType) {
            this.id = id;
            this.filePath = filePath;
            this.neuronType = neuronType;
            this.activationThreshold = neuronType.defaultThreshold;
            this.importanceScore = neuronType.importance;
            tags.add(neuronType.label);
        }

        public boolean isActive() {
            return currentActivation >= activationThreshold;
        }

        public double activate(double stimulus) {
            currentActivation = Math.min(1.0, Math.max(0.0, currentActivation + stimulus * learningRate));
            activationHistory.addLast(currentActivation);
            if (currentActivation >= activationThreshold) {
                fireCount++;
                lastFired = Instant.now();
                energyLevel = Math.max(0, energyLevel - 0.1);
                return currentActivation;
            }
            return 0.0;
        }

        public double calculateEntropy() {
            if (activationHistory.size() < 2) return 0.0;
            double mean = activationHistory.stream().mapToDouble(d -> d).average().orElse(0.0);
            double variance = activationHistory.stream().mapToDouble(d -> Math.pow(d - mean, 2)).average().orElse(0.0);
            return Math.sqrt(variance);
        }

        public Map<String, Object> getHealthStatus() {
            return Map.of(
                    "energy_level", energyLevel,
                    "fire_count", fireCount,
                    "entropy", calculateEntropy(),
                    "importance", importanceScore,
                    "is_active", isActive(),
                    "activation", currentActivation
            );
        }
    }

    public static class Synapse {
        public final String id;
        public final String sourceId;
        public final String targetId;
        public double weight;
        public double strength = 0.5;
        public double plasticity = 0.1;
        public Instant lastUsed;
        public Instant createdAt = Instant.now();

        public Synapse(String id, String sourceId, String targetId, double weight) {
            this.id = id;
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.weight = weight;
        }

        public void strengthen(double amount) {
            strength = Math.min(1.0, strength + amount * plasticity);
            weight = Math.min(2.0, weight + amount * 0.05);
            lastUsed = Instant.now();
        }

        public void weaken(double amount) {
            strength = Math.max(0.0, strength - amount * plasticity);
            weight = Math.max(0.1, weight - amount * 0.05);
            lastUsed = Instant.now();
        }

        public double propagate(double signal) {
            lastUsed = Instant.now();
            return signal * weight * strength;
        }
    }

    public record DataPacket(
            String id,
            String sourceModule,
            List<String> targetModules,
            String dataType,
            Object payload,
            Instant timestamp,
            DataPriority priority,
            long ttl,
            Map<String, Object> metadata
    ) {
        public DataPacket {
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString().substring(0, 12);
            }
            if (targetModules == null) targetModules = List.of();
            metadata = metadata != null ? metadata : new HashMap<>();
        }

        public boolean isExpired() {
            return ttl > 0 && Instant.now().isAfter(timestamp.plusSeconds(ttl));
        }

        public static DataPacket create(String source, List<String> targets, String type, Object payload, DataPriority priority) {
            return new DataPacket(null, source, targets, type, payload, Instant.now(), priority, 60, null);
        }
    }

    // ======================== SISTEMAS DE SUPORTE ========================
    public static class IntegrationHub {
        private final Map<String, Object> modules = new ConcurrentHashMap<>();
        private final Map<String, List<Consumer<DataPacket>>> eventHandlers = new ConcurrentHashMap<>();
        private final BlockingQueue<DataPacket> dataQueue = new LinkedBlockingQueue<>(10000);
        private final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_WORKERS);

        public boolean registerModule(String name, Object instance) {
            if (modules.containsKey(name)) return false;
            modules.put(name, instance);
            LOG.info("Módulo registrado: " + name);
            return true;
        }

        public CompletableFuture<Boolean> sendData(DataPacket packet) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    dataQueue.put(packet);
                    if (packet.priority().priority >= DataPriority.HIGH.priority) {
                        routePacket(packet);
                    }
                    return true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }, threadPool);
        }

        public CompletableFuture<Void> processDataQueue() {
            return CompletableFuture.runAsync(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        DataPacket packet = dataQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (packet != null && !packet.isExpired()) {
                            routePacket(packet);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, threadPool);
        }

        private void routePacket(DataPacket packet) {
            for (String target : packet.targetModules()) {
                if ("all".equals(target)) {
                    modules.keySet().stream()
                            .filter(m -> !m.equals(packet.sourceModule()))
                            .forEach(m -> sendToModule(m, packet));
                } else if (modules.containsKey(target)) {
                    sendToModule(target, packet);
                }
            }
        }

        private void sendToModule(String moduleName, DataPacket packet) {
            Object module = modules.get(moduleName);
            if (module instanceof Consumer) {
                try {
                    ((Consumer<DataPacket>) module).accept(packet);
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Erro ao enviar para módulo " + moduleName, e);
                }
            }
        }

        public void subscribe(String event, Consumer<DataPacket> handler) {
            eventHandlers.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>()).add(handler);
        }

        public void shutdown() {
            threadPool.shutdown();
        }
    }

    public static class SecurityFramework {
        private final Set<String> blockedSources = ConcurrentHashMap.newKeySet();
        private final Map<String, int[]> rateLimits = new ConcurrentHashMap<>();
        private final List<Map<String, Object>> auditLog = Collections.synchronizedList(new ArrayList<>());

        public boolean validateInput(Object data, String type) {
            if (data instanceof String s && s.length() > MAX_INPUT_SIZE) return false;
            if (data instanceof Map<?, ?> m && m.size() > 10000) return false;
            return true;
        }

        public boolean checkAccess(String source, String resource, String action) {
            if (blockedSources.contains(source)) {
                logEvent("access_denied", source, resource, "blocked", "critical");
                return false;
            }
            if (!checkRateLimit(source)) {
                logEvent("rate_limit", source, resource, action, "warning");
                return false;
            }
            logEvent("access_granted", source, resource, action, "info");
            return true;
        }

        private boolean checkRateLimit(String source) {
            long now = System.currentTimeMillis();
            int[] counter = rateLimits.computeIfAbsent(source, k -> new int[]{0, (int) (now / 1000) + RATE_LIMIT_WINDOW});
            if (now / 1000 > counter[1]) {
                counter[0] = 0;
                counter[1] = (int) (now / 1000) + RATE_LIMIT_WINDOW;
            }
            counter[0]++;
            return counter[0] <= RATE_LIMIT_REQUESTS;
        }

        private void logEvent(String type, String source, String resource, String action, String severity) {
            auditLog.add(Map.of("timestamp", Instant.now(), "type", type, "source", source, "resource", resource, "action", action, "severity", severity));
            String msg = String.format("🔒 %s - %s %s %s", type, source, action, resource);
            if ("critical".equals(severity)) LOG.severe(msg);
            else if ("warning".equals(severity)) LOG.warning(msg);
            else LOG.info(msg);
        }

        public Map<String, Object> getReport() {
            return Map.of("blocked", blockedSources.size(), "total_events", auditLog.size());
        }
    }

    public static class MonitoringSystem {
        private final Map<String, LinkedList<Double>> metrics = new ConcurrentHashMap<>();
        private final Instant startTime = Instant.now();

        public void recordMetric(String name, double value) {
            metrics.computeIfAbsent(name, k -> new LinkedList<>()).addLast(value);
        }

        public Map<String, Object> getReport() {
            long uptime = Duration.between(startTime, Instant.now()).toSeconds();
            return Map.of(
                    "uptime_seconds", uptime,
                    "metrics_count", metrics.values().stream().mapToInt(List::size).sum()
            );
        }
    }

    public static class CacheSystem {
        private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
        private final int ttlSeconds;

        public CacheSystem(int ttl) { this.ttlSeconds = ttl; }

        private record CacheEntry(Object value, Instant expires) {}

        public void set(String key, Object value) {
            cache.put(key, new CacheEntry(value, Instant.now().plusSeconds(ttlSeconds)));
        }

        public Optional<Object> get(String key) {
            CacheEntry entry = cache.get(key);
            if (entry != null && Instant.now().isBefore(entry.expires())) {
                return Optional.ofNullable(entry.value());
            }
            cache.remove(key);
            return Optional.empty();
        }

        public Map<String, Object> getStats() {
            return Map.of("entries", cache.size(), "ttl", ttlSeconds);
        }
    }

    public static class PersistenceSystem {
        private final Path storagePath;

        public PersistenceSystem(String path) {
            this.storagePath = Path.of(path);
            try { Files.createDirectories(storagePath); } catch (IOException ignored) {}
        }

        public String createCheckpoint(String name, Map<String, Object> state) {
            String id = name + "_" + System.currentTimeMillis();
            try { Files.write(storagePath.resolve(id + ".json"), List.of(state.toString())); } catch (IOException ignored) {}
            return id;
        }

        public Map<String, Object> getStats() {
            return Map.of("path", storagePath.toString());
        }
    }

    public static class OptimizationEngine {
        private final Map<String, Double> params = new ConcurrentHashMap<>(Map.of(
                "neural_threshold", 0.5, "learning_rate", 0.01,
                "cache_ttl", (double) CACHE_TTL_DEFAULT, "batch_size", (double) BATCH_SIZE_DEFAULT
        ));

        public Map<String, Double> getCurrentParams() { return Map.copyOf(params); }
    }

    // ======================== PONTES DE DADOS (simuladas) ========================
    public static class NeuralDataBridge {
        public void broadcastNeuralPattern(Map<String, Object> pattern) {
            // Simulação: enviaria via IntegrationHub
        }
    }

    public static class QuantumDataBridge {
        public Map<String, Object> applyQuantumOptimization(Map<String, Object> data) {
            Map<String, Object> optimized = new HashMap<>(data);
            optimized.put("quantum_optimized", true);
            optimized.put("optimization_factor", 1.0 + RANDOM.nextDouble() * 0.5);
            return optimized;
        }
    }

    public static class AnalysisDataBridge {
        public Map<String, Object> analyzeNeuralPatterns(Map<String, Object> data) {
            return Map.of("pattern_strength", RANDOM.nextDouble(0.5, 1.0), "anomalies", List.of());
        }
        public Map<String, Object> assessRisk(Map<String, Object> data) {
            return Map.of("risk_level", RANDOM.nextBoolean() ? "high" : "low", "risk_score", RANDOM.nextInt(100));
        }
    }

    public static class ContinuousLearningBridge {
        public Map<String, Object> updateModels(Map<String, Object> data) {
            return Map.of("models_updated", List.of("neural", "quantum"), "accuracy_improvement", RANDOM.nextDouble() * 5);
        }
    }

    // ======================== ORQUESTRADORES ========================
    public abstract static class BaseOrchestrator {
        protected final Map<String, Neuron> neurons = new ConcurrentHashMap<>();
        protected final Map<String, Synapse> synapses = new ConcurrentHashMap<>();
        protected final AtomicLong neuronIdGen = new AtomicLong();
        protected final AtomicLong synapseIdGen = new AtomicLong();
        protected volatile BrainState brainState = BrainState.IDLE;

        public String createNeuron(String filePath, NeuronType type) {
            String id = "n_" + neuronIdGen.incrementAndGet();
            neurons.put(id, new Neuron(id, filePath, type));
            return id;
        }

        public String createSynapse(String src, String tgt, double weight) {
            if (!neurons.containsKey(src) || !neurons.containsKey(tgt))
                throw new IllegalArgumentException("Neuron not found");
            String id = "s_" + synapseIdGen.incrementAndGet();
            synapses.put(id, new Synapse(id, src, tgt, weight));
            neurons.get(src).connections.add(tgt);
            return id;
        }

        public void stimulate(String neuronId, double stimulus) {
            Neuron n = neurons.get(neuronId);
            if (n == null) return;
            double activation = n.activate(stimulus);
            if (activation >= n.activationThreshold) {
                synapses.values().stream()
                        .filter(s -> s.sourceId.equals(neuronId))
                        .forEach(s -> {
                            Neuron target = neurons.get(s.targetId);
                            if (target != null) {
                                stimulate(s.targetId, s.propagate(activation) * 0.1);
                            }
                        });
            }
        }

        public Map<String, Object> getBasicStats() {
            return Map.of(
                    "neurons", neurons.size(),
                    "synapses", synapses.size(),
                    "state", brainState.name(),
                    "active", neurons.values().stream().filter(Neuron::isActive).count()
            );
        }
    }

    public static class AdvancedOrchestrator extends BaseOrchestrator {
        protected final Map<String, List<String>> neuralClusters = new ConcurrentHashMap<>();

        public AdvancedOrchestrator(String iagPath, String quantumPath) {
            LOG.info("Advanced Orchestrator criado");
        }

        public CompletableFuture<Map<String, Object>> runOptimizationCycle() {
            return CompletableFuture.supplyAsync(() -> {
                // Simulação de ciclo de otimização
                return Map.of("memory_consolidated", 10, "pruned", 2, "optimized_synapses", 5);
            });
        }
    }

    // ======================== ORQUESTRADOR INTEGRADO ========================
    private final AdvancedOrchestrator brainCore;
    private final IntegrationHub hub;
    private final SecurityFramework security;
    private final MonitoringSystem monitoring;
    private final CacheSystem cache;
    private final PersistenceSystem persistence;
    private final OptimizationEngine optimizer;
    private final NeuralDataBridge neuralBridge = new NeuralDataBridge();
    private final QuantumDataBridge quantumBridge = new QuantumDataBridge();
    private final AnalysisDataBridge analysisBridge = new AnalysisDataBridge();
    private final ContinuousLearningBridge learningBridge = new ContinuousLearningBridge();
    private final ExecutorService mainExecutor = Executors.newFixedThreadPool(MAX_WORKERS);

    public IntegratedBrainOrchestrator(String iagPath, String quantumPath) {
        this.brainCore = new AdvancedOrchestrator(iagPath, quantumPath);
        this.hub = new IntegrationHub();
        this.security = new SecurityFramework();
        this.monitoring = new MonitoringSystem();
        this.cache = new CacheSystem((int) CACHE_TTL_DEFAULT);
        this.persistence = new PersistenceSystem("./neural_state");
        this.optimizer = new OptimizationEngine();

        registerModules();
        startIntegration();
        LOG.info("🧠 VHALINOR IAG " + VERSION + " inicializado – " + CODENAME);
    }

    private void registerModules() {
        hub.registerModule("neural_engine", this);
        hub.registerModule("security", security);
        hub.registerModule("monitoring", monitoring);
        hub.registerModule("cache", cache);
        hub.registerModule("persistence", persistence);
        hub.registerModule("optimization", optimizer);
    }

    private void startIntegration() {
        hub.processDataQueue();
    }

    public String createNeuron(String path, NeuronType type) { return brainCore.createNeuron(path, type); }
    public String createSynapse(String src, String tgt, double w) { return brainCore.createSynapse(src, tgt, w); }

    public CompletableFuture<Map<String, Object>> processWithAllModules(Map<String, Object> input) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> results = new LinkedHashMap<>();
            results.put("timestamp", Instant.now().toString());

            // Validação
            if (!security.validateInput(input, "dict")) {
                results.put("error", "Validation failed");
                return results;
            }

            // Estágio neural
            Map<String, Object> neural = neuralStage(input);
            results.put("neural", neural);

            // Estágio quântico
            Map<String, Object> quantum = quantumStage(neural);
            results.put("quantum", quantum);

            // Análise
            Map<String, Object> analysis = analysisStage(quantum);
            results.put("analysis", analysis);

            // Aprendizado
            Map<String, Object> learning = learningStage(analysis);
            results.put("learning", learning);

            results.put("final_output", combine(neural, quantum, analysis, learning));
            return results;
        }, mainExecutor);
    }

    private Map<String, Object> neuralStage(Map<String, Object> input) {
        long activated = brainCore.neurons.values().stream().filter(n -> {
            if (RANDOM.nextDouble() > n.activationThreshold) {
                n.activate(RANDOM.nextDouble());
                return true;
            }
            return false;
        }).count();
        return Map.of("activated_neurons", activated);
    }

    private Map<String, Object> quantumStage(Map<String, Object> neural) {
        return quantumBridge.applyQuantumOptimization(neural);
    }

    private Map<String, Object> analysisStage(Map<String, Object> quantum) {
        Map<String, Object> patterns = analysisBridge.analyzeNeuralPatterns(quantum);
        Map<String, Object> risk = analysisBridge.assessRisk(quantum);
        return Map.of("patterns", patterns, "risk", risk);
    }

    private Map<String, Object> learningStage(Map<String, Object> analysis) {
        return learningBridge.updateModels(analysis);
    }

    private Map<String, Object> combine(Map<String, Object>... stages) {
        return Map.of(
                "neural_active", stages[0].getOrDefault("activated_neurons", 0),
                "quantum_boost", stages[1].getOrDefault("optimization_factor", 1.0),
                "risk_level", ((Map<?, ?>) stages[2].getOrDefault("risk", Map.of())).getOrDefault("risk_level", "unknown"),
                "models_trained", !((Map<?, ?>) stages[3]).isEmpty(),
                "brain_state", brainCore.brainState.name()
        );
    }

    public Map<String, Object> getStatus() {
        return Map.of(
                "version", VERSION,
                "codename", CODENAME,
                "neurons", brainCore.neurons.size(),
                "synapses", brainCore.synapses.size(),
                "brain_state", brainCore.brainState.name(),
                "cache", cache.getStats(),
                "security", security.getReport(),
                "monitoring", monitoring.getReport()
        );
    }

    @Override
    public void close() {
        mainExecutor.shutdown();
        hub.shutdown();
        LOG.info("Orquestrador desligado corretamente.");
    }

    // ======================== DEMO ========================
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("VHALINOR IAG " + VERSION + " - " + CODENAME);
        System.out.println("=".repeat(80));

        try (IntegratedBrainOrchestrator brain = new IntegratedBrainOrchestrator("./iag", "./quantum")) {
            // Criar neurônios
            for (NeuronType type : NeuronType.values()) {
                brain.createNeuron("./modules/" + type.label + ".py", type);
            }
            System.out.println("Neurônios criados: " + brain.brainCore.neurons.size());

            // Sinapses
            List<String> ids = new ArrayList<>(brain.brainCore.neurons.keySet());
            for (int i = 0; i < ids.size() - 1; i++) {
                brain.createSynapse(ids.get(i), ids.get(i + 1), 0.7);
            }
            System.out.println("Sinapses criadas: " + brain.brainCore.synapses.size());

            // Processar
            Map<String, Object> result = brain.processWithAllModules(Map.of("symbol", "PETR4", "price", 100.0))
                    .get(15, TimeUnit.SECONDS);
            System.out.println("\n📊 Resultado: " + result.get("final_output"));

            // Status
            System.out.println("\n📌 Status final: ");
            brain.getStatus().forEach((k, v) -> System.out.println("  " + k + " = " + v));
        }

        System.out.println("\n✅ Demonstração concluída.");
    }
}
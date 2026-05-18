package com.vhalinor.iag;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.logging.*;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * VHALINOR.IAG 4.5 - Sistema Cerebral Artificial Avançado Otimizado (Java version)
 * 
 * Tradução estrutural do sistema cerebral otimizado para Java.
 * Bibliotecas externas necessárias (não incluídas):
 * - Weka / DL4J para Machine Learning
 * - JFreeChart para gráficos
 * - Apache Commons Math para operações de álgebra linear
 * - Quantum SDKs (opcionais)
 */
public class VhalinorIAG {

    // ========== LOGGER OTIMIZADO ==========
    static class BrainLogger {
        private static final Logger LOGGER = Logger.getLogger("brain_network");
        private final Map<String, List<Long>> profilingData = new ConcurrentHashMap<>();
        private long logStartTime = System.currentTimeMillis();

        static {
            try {
                // Configuração de handlers
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new SimpleFormatter() {
                    @Override
                    public synchronized String format(LogRecord record) {
                        return String.format("%tT.%03d - %s - %s%n",
                                new Date(record.getMillis()),
                                record.getMillis() % 1000,
                                record.getLevel().getLocalizedName(),
                                record.getMessage());
                    }
                });
                LOGGER.addHandler(consoleHandler);

                FileHandler fileHandler = new FileHandler("brain_network.log", true);
                fileHandler.setFormatter(new SimpleFormatter() {
                    @Override
                    public synchronized String format(LogRecord record) {
                        return String.format("%tF %tT.%03d - %s - %s%n",
                                new Date(record.getMillis()),
                                new Date(record.getMillis()),
                                record.getMillis() % 1000,
                                record.getLevel().getLocalizedName(),
                                record.getMessage());
                    }
                });
                LOGGER.addHandler(fileHandler);

                LOGGER.setLevel(Level.INFO);
            } catch (IOException e) {
                System.err.println("Failed to initialize logger: " + e.getMessage());
            }
        }

        public void profile(String funcName, long durationMs) {
            profilingData.computeIfAbsent(funcName, k -> new ArrayList<>()).add(durationMs);
            List<Long> times = profilingData.get(funcName);
            if (times.size() > 100) {
                times.remove(0);
            }
        }

        public Map<String, Double> getPerformanceReport() {
            Map<String, Double> report = new HashMap<>();
            for (Map.Entry<String, List<Long>> entry : profilingData.entrySet()) {
                List<Long> times = entry.getValue();
                if (!times.isEmpty()) {
                    double avg = times.stream().mapToLong(Long::longValue).average().orElse(0);
                    double max = times.stream().mapToLong(Long::longValue).max().orElse(0);
                    report.put(entry.getKey() + "_avg", avg);
                    report.put(entry.getKey() + "_max", max);
                }
            }
            return report;
        }
    }

    private static final BrainLogger brainLogger = new BrainLogger();
    private static final Logger logger = Logger.getLogger("brain_network");

    // ========== TIPOS DE NEURÔNIOS ==========
    enum OptimizedNeuronType {
        SENSORY, PROCESSING, MEMORY, DECISION, OUTPUT, QUANTUM;

        public String getProcessingGroup() {
            switch (this) {
                case SENSORY: return "input";
                case PROCESSING: return "compute";
                case MEMORY: return "storage";
                case DECISION: return "control";
                case OUTPUT: return "output";
                case QUANTUM: return "quantum";
                default: return "other";
            }
        }
    }

    // ========== NEURÔNIO OTIMIZADO ==========
    static class OptimizedNeuron {
        private final String id;
        private final String filePath;
        private final OptimizedNeuronType neuronType;
        private double activationThreshold;
        private double currentActivation;
        private final Set<String> connections = Collections.synchronizedSet(new HashSet<>());
        private Date lastFired;
        private double memoryWeight;
        private double learningRate;
        private double quantumEntanglement;
        private final Deque<Double> activationHistory = new ArrayDeque<>(100);
        private long fireCount;
        private double importanceScore;
        private double energyLevel;
        private Date lastModified;
        private List<String> dependencies = new ArrayList<>();
        private List<String> tags = new ArrayList<>();
        private String contentHash;

        // Cache
        private Double entropyCache = null;
        private long entropyCacheTime = 0;

        public OptimizedNeuron(String id, String filePath, OptimizedNeuronType neuronType,
                               double activationThreshold, double memoryWeight, double learningRate) {
            this.id = id;
            this.filePath = filePath;
            this.neuronType = neuronType;
            this.activationThreshold = activationThreshold;
            this.memoryWeight = memoryWeight;
            this.learningRate = learningRate;
            this.currentActivation = 0.0;
            this.lastFired = null;
            this.quantumEntanglement = 0.0;
            this.fireCount = 0;
            this.importanceScore = 1.0;
            this.energyLevel = 100.0;
            this.lastModified = new Date();
            this.contentHash = computeContentHash();
        }

        private String computeContentHash() {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                try (InputStream is = new FileInputStream(filePath)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead = is.read(buffer);
                    if (bytesRead > 0) {
                        md.update(buffer, 0, bytesRead);
                    }
                } catch (FileNotFoundException e) {
                    // se arquivo não existir, usa caminho
                    md.update(filePath.getBytes());
                }
                byte[] digest = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (Exception e) {
                return filePath;
            }
        }

        public synchronized double stimulate(double stimulus) {
            if (Math.abs(stimulus) < 0.001) return 0.0;

            currentActivation += stimulus * learningRate;
            if (currentActivation >= activationThreshold) {
                return fire();
            }

            activationHistory.addLast(currentActivation);
            if (activationHistory.size() > 100) activationHistory.removeFirst();
            invalidateCache();
            return currentActivation / activationThreshold;
        }

        private double fire() {
            double output = Math.min(1.0, currentActivation);
            currentActivation *= 0.1;
            lastFired = new Date();
            fireCount++;
            activationHistory.addLast(output);
            if (activationHistory.size() > 100) activationHistory.removeFirst();
            invalidateCache();
            return output;
        }

        public double calculateEntropy(boolean force) {
            long now = System.currentTimeMillis();
            if (!force && entropyCache != null && (now - entropyCacheTime) < 60_000) {
                return entropyCache;
            }
            if (activationHistory.size() < 2) {
                entropyCache = 0.0;
            } else {
                // Desvio padrão amostral
                double[] values = activationHistory.stream().mapToDouble(Double::doubleValue).toArray();
                double mean = Arrays.stream(values).average().orElse(0);
                double variance = Arrays.stream(values).map(v -> (v - mean) * (v - mean)).average().orElse(0);
                entropyCache = Math.sqrt(variance);
            }
            entropyCacheTime = now;
            return entropyCache;
        }

        private void invalidateCache() {
            entropyCache = null;
            entropyCacheTime = 0;
        }

        public String getProcessingGroup() {
            return neuronType.getProcessingGroup();
        }

        // Getters e setters (apenas os necessários)
        public String getId() { return id; }
        public String getFilePath() { return filePath; }
        public OptimizedNeuronType getNeuronType() { return neuronType; }
        public double getCurrentActivation() { return currentActivation; }
        public double getActivationThreshold() { return activationThreshold; }
        public long getFireCount() { return fireCount; }
        public double getEnergyLevel() { return energyLevel; }
        public double getImportanceScore() { return importanceScore; }
        public double getQuantumEntanglement() { return quantumEntanglement; }
        public Date getLastModified() { return lastModified; }
        public Set<String> getConnections() { return connections; }
        public double getMemoryWeight() { return memoryWeight; }
        public void setCurrentActivation(double v) { this.currentActivation = v; }
    }

    // ========== GERENCIADOR DE CONEXÕES ==========
    static class NeuralConnectionManager {
        private final Map<String, Set<String>> connections = new ConcurrentHashMap<>();
        private final Map<String, Set<String>> reverseConnections = new ConcurrentHashMap<>();
        private final Map<String, Double> weights = new ConcurrentHashMap<>(); // "from->to"
        private final Map<String, Set<String>> connectionCache = new ConcurrentHashMap<>();

        public void addConnection(String fromNeuron, String toNeuron, double weight) {
            connections.computeIfAbsent(fromNeuron, k -> ConcurrentHashMap.newKeySet()).add(toNeuron);
            reverseConnections.computeIfAbsent(toNeuron, k -> ConcurrentHashMap.newKeySet()).add(fromNeuron);
            weights.put(fromNeuron + "->" + toNeuron, weight);
            invalidateCache(fromNeuron);
        }

        public void removeConnection(String fromNeuron, String toNeuron) {
            Set<String> set = connections.get(fromNeuron);
            if (set != null) set.remove(toNeuron);
            Set<String> rSet = reverseConnections.get(toNeuron);
            if (rSet != null) rSet.remove(fromNeuron);
            weights.remove(fromNeuron + "->" + toNeuron);
            invalidateCache(fromNeuron);
        }

        public Set<String> getConnections(String neuronId) {
            return connectionCache.computeIfAbsent(neuronId,
                    k -> connections.getOrDefault(neuronId, Collections.emptySet())
                                      .stream().collect(Collectors.toSet()));
        }

        private void invalidateCache(String neuronId) {
            connectionCache.remove(neuronId);
        }

        public Map<String, Double> getWeights() {
            return new HashMap<>(weights);
        }
    }

    // ========== MÓDULO DE ML OTIMIZADO (STUB) ==========
    static class OptimizedMLModule {
        private final OptimizedBrainOrchestrator orchestrator;
        // Placeholder para modelos reais (IsolationForest, PCA, KMeans)
        private final Map<String, Object> models = new HashMap<>();
        private final Map<String, double[]> featureCache = new ConcurrentHashMap<>();
        private final Map<String, Integer> predictionCache = new ConcurrentHashMap<>();
        private Date lastTraining;

        public OptimizedMLModule(OptimizedBrainOrchestrator orchestrator) {
            this.orchestrator = orchestrator;
            setupOptimizedModels();
        }

        private void setupOptimizedModels() {
            // Na prática, inicializaria Weka/Deeplearning4j
            models.put("anomaly_detector", new Object()); // stub
            models.put("pca", new Object()); // stub
            models.put("clusterer", new Object()); // stub
        }

        public double[] extractNeuronFeatures(OptimizedNeuron neuron) {
            // mesmo cálculo que original
            double daysSinceModified = neuron.getLastModified() != null ?
                    (System.currentTimeMillis() - neuron.getLastModified().getTime()) / 86_400_000.0 : 0;
            return new double[]{
                neuron.getCurrentActivation(),
                neuron.getActivationThreshold(),
                neuron.getMemoryWeight(),
                neuron.getFireCount(),
                neuron.getEnergyLevel(),
                neuron.getImportanceScore(),
                neuron.calculateEntropy(false),
                neuron.getConnections().size(),
                neuron.getQuantumEntanglement(),
                daysSinceModified
            };
        }

        public CompletableFuture<double[][]> extractFeaturesBatchAsync(List<String> neuronIds) {
            return CompletableFuture.supplyAsync(() -> {
                List<double[]> features = new ArrayList<>();
                for (String nid : neuronIds) {
                    if (featureCache.containsKey(nid)) {
                        features.add(featureCache.get(nid));
                    } else {
                        OptimizedNeuron neuron = orchestrator.getNeurons().get(nid);
                        if (neuron != null) {
                            double[] feat = extractNeuronFeatures(neuron);
                            featureCache.put(nid, feat);
                            features.add(feat);
                        }
                    }
                }
                return features.toArray(double[][]::new);
            });
        }

        public CompletableFuture<Void> trainAsync(int batchSize) {
            if (lastTraining != null && (System.currentTimeMillis() - lastTraining.getTime()) < 3600_000) {
                logger.info("⏭️ Treinamento recente, pulando...");
                return CompletableFuture.completedFuture(null);
            }
            logger.info("🚀 Iniciando treinamento otimizado...");
            List<String> allNeuronIds = new ArrayList<>(orchestrator.getNeurons().keySet());
            // Processa em batches de forma async (simulação)
            return CompletableFuture.runAsync(() -> {
                for (int i = 0; i < allNeuronIds.size(); i += batchSize) {
                    List<String> batch = allNeuronIds.subList(i, Math.min(i + batchSize, allNeuronIds.size()));
                    double[][] features = extractFeaturesBatchAsync(batch).join();
                    // Treinamento stub: armazena predições -1 (normal), 1 (anomalia) aleatório
                    for (int j = 0; j < batch.size(); j++) {
                        predictionCache.put(batch.get(j), new Random().nextInt(3) - 1); // -1,0,1
                    }
                }
                lastTraining = new Date();
                logger.info("✅ Treinamento completo: " + allNeuronIds.size() + " neurônios processados");
            });
        }

        public List<String> predictAnomalies(double threshold) {
            return predictionCache.entrySet().stream()
                    .filter(e -> e.getValue() < threshold)
                    .map(Map.Entry::getKey)
                    .limit(50)
                    .collect(Collectors.toList());
        }
    }

    // ========== SISTEMA QUÂNTICO OTIMIZADO (STUB) ==========
    static class OptimizedQuantumSystem {
        private final OptimizedBrainOrchestrator orchestrator;
        private final int quantumBits = 5;
        private double[] stateVector; // simulação simples

        public OptimizedQuantumSystem(OptimizedBrainOrchestrator orchestrator) {
            this.orchestrator = orchestrator;
            this.stateVector = new double[1 << quantumBits];
            this.stateVector[0] = 1.0; // |00000>
        }

        public CompletableFuture<Map<String, Object>> simulateQuantumCircuitAsync(int depth) {
            return CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                Random rand = new Random();
                for (int i = 0; i < depth; i++) {
                    // Simulação: altera vetor de estado aleatoriamente (não unitário, mas ilustrativo)
                    int qubit = rand.nextInt(quantumBits);
                    // portas H e X simplificadas
                    applyHadamard(qubit);
                    if (quantumBits >= 2) {
                        int control = rand.nextInt(quantumBits - 1);
                        int target = rand.nextInt(quantumBits - control - 1) + control + 1;
                        applyCNOT(control, target);
                    }
                }
                // Probabilidades
                double[] probs = new double[stateVector.length];
                double total = 0;
                for (int i = 0; i < stateVector.length; i++) {
                    probs[i] = stateVector[i] * stateVector[i];
                    total += probs[i];
                }
                // Normaliza e escolhe medição
                for (int i = 0; i < probs.length; i++) probs[i] /= total;
                double r = rand.nextDouble();
                double cumulative = 0;
                int measurement = 0;
                for (int i = 0; i < probs.length; i++) {
                    cumulative += probs[i];
                    if (r <= cumulative) {
                        measurement = i;
                        break;
                    }
                }
                int activated = activateNeuronsFromMeasurement(measurement);
                long duration = System.currentTimeMillis() - startTime;

                Map<String, Object> result = new HashMap<>();
                result.put("measurement", Integer.toBinaryString(measurement | (1 << quantumBits)).substring(1));
                result.put("entropy", calculateQuantumEntropy());
                result.put("activated_neurons", activated);
                result.put("simulation_time", duration);
                result.put("qubits", quantumBits);
                return result;
            });
        }

        private void applyHadamard(int qubit) {
            // Simulação simplificada: mistura amplitudes
            for (int i = 0; i < stateVector.length; i++) {
                if (((i >> qubit) & 1) == 0) {
                    double a = stateVector[i];
                    double b = stateVector[i | (1 << qubit)];
                    stateVector[i] = (a + b) / Math.sqrt(2);
                    stateVector[i | (1 << qubit)] = (a - b) / Math.sqrt(2);
                }
            }
        }

        private void applyCNOT(int control, int target) {
            // Simulação simplificada (XOR)
            double[] newVec = stateVector.clone();
            for (int i = 0; i < stateVector.length; i++) {
                if (((i >> control) & 1) == 1) {
                    int flipped = i ^ (1 << target);
                    double tmp = stateVector[i];
                    newVec[i] = stateVector[flipped];
                    newVec[flipped] = tmp;
                }
            }
            stateVector = newVec;
        }

        public double calculateQuantumEntropy() {
            double[] probs = new double[stateVector.length];
            for (int i = 0; i < stateVector.length; i++) {
                probs[i] = stateVector[i] * stateVector[i];
            }
            double entropy = 0;
            for (double p : probs) {
                if (p > 0) entropy -= p * Math.log(p) / Math.log(2);
            }
            return entropy;
        }

        private int activateNeuronsFromMeasurement(int measurement) {
            List<OptimizedNeuron> quantumNeurons = orchestrator.getNeurons().values().stream()
                    .filter(n -> n.getNeuronType() == OptimizedNeuronType.QUANTUM)
                    .collect(Collectors.toList());
            int activated = 0;
            for (int i = 0; i < Math.min(quantumNeurons.size(), quantumBits); i++) {
                if (((measurement >> i) & 1) == 1) {
                    double activation = 0.3 + Math.random() * 0.7;
                    orchestrator.stimulateNeuronAsync(quantumNeurons.get(i).getId(), activation);
                    activated++;
                }
            }
            return activated;
        }
    }

    // ========== SISTEMA DE MEMÓRIA OTIMIZADO ==========
    static class OptimizedMemorySystem {
        private final Deque<Map.Entry<String, Object>> shortTerm = new ArrayDeque<>(500);
        private final Map<String, MemoryItem> longTerm = new ConcurrentHashMap<>();
        private final Map<String, Set<String>> memoryIndex = new ConcurrentHashMap<>();
        private final LinkedHashMap<String, Object> lruCache = new LinkedHashMap<String, Object>(1000, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                return size() > 1000;
            }
        };
        private long hits = 0;
        private long misses = 0;
        private final long cleanupThreshold; // bytes

        static class MemoryItem {
            String id;
            Object data;
            double importance;
            Date created;
            long accessCount;
            long size;

            public MemoryItem(String id, Object data, double importance) {
                this.id = id;
                this.data = data;
                this.importance = importance;
                this.created = new Date();
                this.accessCount = 0;
                this.size = estimateSize(data);
            }

            private long estimateSize(Object obj) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(obj);
                    return baos.size();
                } catch (IOException e) {
                    return 1024;
                }
            }
        }

        public OptimizedMemorySystem(int maxSizeMb) {
            this.cleanupThreshold = (long) maxSizeMb * 1024 * 1024;
        }

        public String store(String key, Object value, double importance) {
            String memoryId = hashKey(key);
            Object dataToStore = value;
            MemoryItem item = new MemoryItem(memoryId, dataToStore, importance);
            longTerm.put(memoryId, item);

            if (value instanceof String) {
                String text = ((String) value).toLowerCase();
                Arrays.stream(text.split("\\s+"))
                        .limit(10)
                        .filter(w -> w.length() > 2)
                        .forEach(w -> memoryIndex.computeIfAbsent(w, k -> ConcurrentHashMap.newKeySet()).add(memoryId));
            }

            autoCleanupIfNeeded();
            return memoryId;
        }

        public Object retrieve(String memoryId) {
            if (lruCache.containsKey(memoryId)) {
                hits++;
                return lruCache.get(memoryId);
            }
            misses++;
            MemoryItem item = longTerm.get(memoryId);
            if (item != null) {
                item.accessCount++;
                Object data = item.data;
                lruCache.put(memoryId, data);
                return data;
            }
            return null;
        }

        public List<Object> search(String query, int limit) {
            Set<String> queryWords = Arrays.stream(query.toLowerCase().split("\\s+")).collect(Collectors.toSet());
            Map<String, Double> scored = new HashMap<>();
            for (String word : queryWords) {
                Set<String> ids = memoryIndex.get(word);
                if (ids != null) {
                    for (String memId : ids) {
                        MemoryItem item = longTerm.get(memId);
                        if (item != null) {
                            double score = calculateRelevanceScore(item, queryWords);
                            scored.merge(memId, score, Double::sum);
                        }
                    }
                }
            }
            return scored.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(e -> retrieve(e.getKey()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        private double calculateRelevanceScore(MemoryItem item, Set<String> queryWords) {
            double score = item.importance;
            if (item.data instanceof String) {
                Set<String> dataWords = Arrays.stream(((String) item.data).toLowerCase().split("\\s+"))
                        .collect(Collectors.toSet());
                long overlap = queryWords.stream().filter(dataWords::contains).count();
                score += overlap * 0.1;
            }
            score += Math.min(item.accessCount * 0.01, 0.3);
            return score;
        }

        private void autoCleanupIfNeeded() {
            long totalSize = longTerm.values().stream().mapToLong(item -> item.size).sum();
            if (totalSize > cleanupThreshold) {
                logger.info("🧹 Executando limpeza de memória...");
                List<MemoryItem> items = new ArrayList<>(longTerm.values());
                items.sort(Comparator.comparingDouble(item -> item.importance / Math.max(item.size, 1)));
                int toRemove = (int) (items.size() * 0.2);
                for (int i = 0; i < toRemove; i++) {
                    String id = items.get(i).id;
                    longTerm.remove(id);
                    lruCache.remove(id);
                }
            }
        }

        public Map<String, Object> getStats() {
            long totalSize = longTerm.values().stream().mapToLong(item -> item.size).sum();
            Map<String, Object> stats = new HashMap<>();
            stats.put("long_term_items", longTerm.size());
            stats.put("cache_size", lruCache.size());
            stats.put("cache_hits", hits);
            stats.put("cache_misses", misses);
            stats.put("hit_ratio", (hits + misses) > 0 ? (double) hits / (hits + misses) : 0);
            stats.put("total_size_mb", totalSize / (1024.0 * 1024.0));
            return stats;
        }

        private String hashKey(String key) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(key.getBytes());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    sb.append(String.format("%02x", digest[i]));
                }
                return sb.toString();
            } catch (Exception e) {
                return Integer.toHexString(key.hashCode());
            }
        }
    }

    // ========== ORQUESTRADOR OTIMIZADO ==========
    static class OptimizedBrainOrchestrator {
        private final String iagPath;
        private final String quantumPath;
        private final Map<String, OptimizedNeuron> neurons = new ConcurrentHashMap<>();
        private final NeuralConnectionManager connectionManager = new NeuralConnectionManager();
        private final OptimizedMLModule mlModule = new OptimizedMLModule(this);
        private final OptimizedQuantumSystem quantumSystem = new OptimizedQuantumSystem(this);
        private final OptimizedMemorySystem memorySystem = new OptimizedMemorySystem(50);
        private double energy = 1000.0;
        private final double energyRegenRate = 0.1;
        private String state = "IDLE";
        private String performanceMode = "BALANCED"; // BALANCED, PERFORMANCE, ECO
        private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        public OptimizedBrainOrchestrator(String iagPath, String quantumPath) {
            this.iagPath = iagPath;
            this.quantumPath = quantumPath;
            initializeAsync();
        }

        private void initializeAsync() {
            CompletableFuture<Void> loadNeurons = CompletableFuture.runAsync(this::loadNeuronsBatch);
            CompletableFuture<Void> initConnections = CompletableFuture.runAsync(this::initializeConnections);
            CompletableFuture.allOf(loadNeurons, initConnections).join();
            logger.info("✅ Sistema inicializado com sucesso");
        }

        private void loadNeuronsBatch() {
            logger.info("🧠 Carregando neurônios...");
            try {
                Files.walk(Paths.get(iagPath))
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().matches(".*\\.(py|txt|json|csv)$"))
                        .forEach(filePath -> {
                            String neuronId = hashString(filePath.toString());
                            OptimizedNeuronType type = determineNeuronType(filePath.toString());
                            OptimizedNeuron neuron = new OptimizedNeuron(
                                    neuronId, filePath.toString(), type, 0.5, 0.5, 0.1);
                            neurons.put(neuronId, neuron);
                        });
                logger.info("✅ " + neurons.size() + " neurônios carregados");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erro ao carregar neurônios", e);
            }
        }

        private void initializeConnections() {
            // Conexões baseadas em proximidade de diretórios ou aleatório (stub)
            List<String> ids = new ArrayList<>(neurons.keySet());
            Random rand = new Random();
            for (int i = 0; i < ids.size(); i++) {
                for (int j = i + 1; j < Math.min(ids.size(), i + 5); j++) {
                    connectionManager.addConnection(ids.get(i), ids.get(j), 0.2 + rand.nextDouble() * 0.8);
                }
            }
        }

        private OptimizedNeuronType determineNeuronType(String path) {
            String lower = path.toLowerCase();
            if (lower.contains("quantum")) return OptimizedNeuronType.QUANTUM;
            if (lower.contains("decision") || lower.contains("logic")) return OptimizedNeuronType.DECISION;
            if (lower.contains("memory") || lower.contains("storage")) return OptimizedNeuronType.MEMORY;
            if (lower.contains("sensor") || lower.contains("input")) return OptimizedNeuronType.SENSORY;
            if (lower.contains("output") || lower.contains("action")) return OptimizedNeuronType.OUTPUT;
            return OptimizedNeuronType.PROCESSING;
        }

        public void stimulateNeuronAsync(String neuronId, double stimulus) {
            CompletableFuture.runAsync(() -> {
                OptimizedNeuron neuron = neurons.get(neuronId);
                if (neuron != null && energy >= 0.1) {
                    double cost = stimulus * 0.01;
                    energy = Math.max(0, energy - cost);
                    neuron.stimulate(stimulus);
                }
            }, executor);
        }

        public CompletableFuture<Map<String, Object>> runOptimizationCycle(boolean intensive) {
            logger.info("⚙️ Executando ciclo de otimização...");
            List<CompletableFuture<?>> tasks = new ArrayList<>();
            if (intensive) {
                tasks.add(CompletableFuture.runAsync(memorySystem::autoCleanupIfNeeded, executor));
                tasks.add(CompletableFuture.runAsync(this::neuralPathPruning, executor));
                tasks.add(mlModule.trainAsync(200));
            } else {
                tasks.add(CompletableFuture.runAsync(memorySystem::autoCleanupIfNeeded, executor));
                tasks.add(mlModule.trainAsync(100));
            }
            return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new))
                    .thenApply(v -> {
                        energy = Math.min(1000.0, energy + 50.0);
                        logger.info("✅ Ciclo de otimização completo");
                        return getPerformanceMetrics();
                    });
        }

        private void neuralPathPruning() {
            List<String[]> toPrune = new ArrayList<>();
            for (Map.Entry<String, Double> entry : connectionManager.getWeights().entrySet()) {
                if (entry.getValue() < 0.1) {
                    String[] parts = entry.getKey().split("->", 2);
                    if (parts.length == 2) toPrune.add(parts);
                }
            }
            toPrune.stream().limit(100).forEach(pair -> connectionManager.removeConnection(pair[0], pair[1]));
        }

        public Map<String, Object> getPerformanceMetrics() {
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("total_neurons", neurons.size());
            metrics.put("active_neurons", neurons.values().stream().filter(n -> n.getCurrentActivation() > 0.1).count());
            metrics.put("total_connections", connectionManager.getWeights().size());
            metrics.put("system_energy", energy);
            metrics.put("memory_efficiency", memorySystem.getStats().get("hit_ratio"));
            metrics.put("quantum_entropy", quantumSystem.calculateQuantumEntropy());
            metrics.put("performance_report", brainLogger.getPerformanceReport());
            metrics.put("state", state);
            metrics.put("performance_mode", performanceMode);
            return metrics;
        }

        private String hashString(String input) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(input.getBytes());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 4; i++) sb.append(String.format("%02x", digest[i]));
                return sb.toString();
            } catch (Exception e) {
                return Integer.toHexString(input.hashCode());
            }
        }

        // Getters para componentes internos
        public Map<String, OptimizedNeuron> getNeurons() { return neurons; }
        public NeuralConnectionManager getConnectionManager() { return connectionManager; }
        public OptimizedMLModule getMlModule() { return mlModule; }
        public OptimizedQuantumSystem getQuantumSystem() { return quantumSystem; }
        public OptimizedMemorySystem getMemorySystem() { return memorySystem; }
    }

    // ========== INTERFACE GRÁFICA OTIMIZADA ==========
    static class OptimizedBrainGUI {
        private final OptimizedBrainOrchestrator orchestrator;
        private final JFrame frame;
        private final JTabbedPane tabbedPane;
        private final Timer updateTimer;
        private long lastUpdateTime = 0;
        private final Map<String, JLabel> metricLabels = new HashMap<>();

        public OptimizedBrainGUI(OptimizedBrainOrchestrator orchestrator) {
            this.orchestrator = orchestrator;
            frame = new JFrame("LEXTRADER-IAG 4.5 - Sistema Cerebral Otimizado (Java)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);

            tabbedPane = new JTabbedPane();
            setupOverviewTab();
            setupPerformanceTab();

            frame.add(tabbedPane);
            frame.setVisible(true);

            updateTimer = new Timer(2000, this::updateDisplay);
            updateTimer.start();
        }

        private void setupOverviewTab() {
            JPanel overviewPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel title = new JLabel("Métricas Principais", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 14));
            overviewPanel.add(title, gbc);

            gbc.gridwidth = 1;
            gbc.gridy = 1;
            gbc.weighty = 0.0;
            String[][] metrics = {
                    {"Neurônios Ativos", "active_neurons"},
                    {"Conexões", "total_connections"},
                    {"Energia", "system_energy"},
                    {"Cache Hit Ratio", "memory_efficiency"}
            };
            for (int i = 0; i < metrics.length; i++) {
                gbc.gridx = i % 2;
                gbc.gridy = 1 + i / 2;
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JLabel(metrics[i][0] + ": "), BorderLayout.WEST);
                JLabel valueLabel = new JLabel("0");
                panel.add(valueLabel, BorderLayout.CENTER);
                metricLabels.put(metrics[i][1], valueLabel);
                overviewPanel.add(panel, gbc);
            }

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.weighty = 1.0;
            JPanel graphPanel = new JPanel(); // Poderia integrar JFreeChart futuramente
            graphPanel.add(new JLabel("Gráfico de Ativação Neural (placeholder)"));
            overviewPanel.add(graphPanel, gbc);

            gbc.gridy = 4;
            gbc.weighty = 0.0;
            JPanel controls = new JPanel();
            JButton optimizeBtn = new JButton("Otimizar");
            optimizeBtn.addActionListener(e -> orchestrator.runOptimizationCycle(false));
            JButton quantumBtn = new JButton("Processar Quântico");
            quantumBtn.addActionListener(e -> orchestrator.getQuantumSystem().simulateQuantumCircuitAsync(3));
            JButton trainBtn = new JButton("Treinar ML");
            trainBtn.addActionListener(e -> orchestrator.getMlModule().trainAsync(100));
            controls.add(optimizeBtn);
            controls.add(quantumBtn);
            controls.add(trainBtn);
            overviewPanel.add(controls, gbc);

            tabbedPane.addTab("Visão Geral", overviewPanel);
        }

        private void setupPerformanceTab() {
            JPanel perfPanel = new JPanel(new BorderLayout());
            perfPanel.add(new JLabel("Gráficos de Performance (placeholder - integre JFreeChart)"), BorderLayout.CENTER);
            tabbedPane.addTab("Performance", perfPanel);
        }

        private void updateDisplay(ActionEvent e) {
            Map<String, Object> metrics = orchestrator.getPerformanceMetrics();
            for (Map.Entry<String, JLabel> entry : metricLabels.entrySet()) {
                Object value = metrics.getOrDefault(entry.getKey(), "?");
                if (value instanceof Double) {
                    entry.getValue().setText(String.format("%.2f", (Double) value));
                } else {
                    entry.getValue().setText(String.valueOf(value));
                }
            }
        }
    }

    // ========== MAIN ==========
    public static void main(String[] args) {
        System.out.println("""
            ╔══════════════════════════════════════════════════════════╗
            ║        VHALINOR.IAG 4.5 - Sistema Cerebral Otimizado     ║
            ║      Virtual Hybrid Advanced Learning Intelligence       ║
            ║           Neural Optimized Reasoning System              ║
            ╚══════════════════════════════════════════════════════════╝
            """);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Caminho dos arquivos VHALINOR.IAG [./vhalinor_iag]: ");
        String iagPath = scanner.nextLine().trim();
        if (iagPath.isEmpty()) iagPath = "./vhalinor_iag";
        System.out.print("Caminho quântico [./quantum]: ");
        String quantumPath = scanner.nextLine().trim();
        if (quantumPath.isEmpty()) quantumPath = "./quantum";

        try {
            Files.createDirectories(Paths.get(iagPath));
            Files.createDirectories(Paths.get(quantumPath));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretórios: " + e.getMessage());
        }

        OptimizedBrainOrchestrator orchestrator = new OptimizedBrainOrchestrator(iagPath, quantumPath);
        System.out.println("\n✅ Sistema iniciado. Interface gráfica em execução...");

        // Inicia GUI em thread separada
        Executors.newSingleThreadExecutor().submit(() -> new OptimizedBrainGUI(orchestrator));

        // Loop principal de processamento
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // processamento periódico e otimização
                Random rand = new Random();
                if (rand.nextDouble() > 0.7) {
                    List<String> ids = new ArrayList<>(orchestrator.getNeurons().keySet());
                    if (!ids.isEmpty()) {
                        String targetId = ids.get(rand.nextInt(ids.size()));
                        orchestrator.stimulateNeuronAsync(targetId, rand.nextDouble());
                    }
                }
                orchestrator.runOptimizationCycle(false);
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Erro no loop principal", ex);
            }
        }, 30, 60, TimeUnit.SECONDS);

        // Mantém a aplicação rodando
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n💾 Salvando estado...");
            scheduler.shutdown();
            System.out.println("✅ Sistema encerrado.");
        }));

        // Evita que main termine imediatamente (a GUI mantém a thread EDT)
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
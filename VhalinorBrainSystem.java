import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * Vhalinor.IAG 5.0 – Java Edition
 * Sistema Cerebral Artificial Ultra-Otimizado
 * (Conversão do arquivo Python original)
 */
public class VhalinorBrainSystem {

    // =========================================================================
    // LOGGING
    // =========================================================================
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                           "%1$tH:%1$tM:%1$tS | %4$-7s | %5$s%6$s%n");
    }
    private static final Logger LOGGER = Logger.getLogger("VHALINOR.IAG");
    
    static {
        try {
            FileHandler fh = new FileHandler("vhalinor_brain.log", 10_000_000, 3, true);
            fh.setEncoding("UTF-8");
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (IOException e) {
            System.err.println("Falha ao criar log em arquivo: " + e.getMessage());
        }
        LOGGER.setLevel(Level.INFO);
    }

    // =========================================================================
    // ENUMS
    // =========================================================================
    enum NeuronType {
        SENSORY, PROCESSING, MEMORY, DECISION, OUTPUT, EMOTION, QUANTUM, META
    }

    enum PerformanceMode {
        ECO("Eco"), BALANCED("Balanced"), TURBO("Turbo");
        final String label;
        PerformanceMode(String label) { this.label = label; }
    }

    // =========================================================================
    // NEURON
    // =========================================================================
    static class Neuron {
        final String id;
        final String filePath;
        final NeuronType type;
        volatile double activation;
        volatile double threshold;
        volatile double importance;
        volatile double energyCost;
        volatile Instant lastFire;
        final AtomicInteger fireCount = new AtomicInteger();
        final Set<String> connectionsOut = ConcurrentHashMap.newKeySet();
        final Set<String> connectionsIn = ConcurrentHashMap.newKeySet();
        final List<String> tags = new CopyOnWriteArrayList<>();
        final Deque<Double> activationHistory = new ArrayDeque<>(120);
        private final Object lock = new Object();

        Neuron(String id, String filePath, NeuronType type) {
            this.id = id;
            this.filePath = filePath;
            this.type = type;
            this.threshold = 0.48 + Math.random() * 0.2;
            this.importance = 0.7 + Math.random() * 0.7;
            this.energyCost = 0.8 + Math.random() * 1.4;
            this.activation = 0.0;
        }

        double stimulate(double strength, double energyAvailable) {
            double effective = Math.min(strength, energyAvailable * 0.8);
            synchronized (lock) {
                activation += effective * (1.0 + (Math.random() - 0.5) * 0.16);
                activation = Math.min(1.8, Math.max(0.0, activation));
                if (activation >= threshold) {
                    double fired = fire();
                    activation *= 0.12;
                    return fired;
                }
                return 0.0;
            }
        }

        private double fire() {
            fireCount.incrementAndGet();
            lastFire = Instant.now();
            double output = Math.min(1.0, activation * 0.92);
            synchronized (activationHistory) {
                activationHistory.addLast(output);
                if (activationHistory.size() > 120) activationHistory.removeFirst();
            }
            return output;
        }

        double avgActivation() {
            synchronized (activationHistory) {
                if (activationHistory.isEmpty()) return 0.0;
                double sum = 0;
                for (double v : activationHistory) sum += v;
                return sum / activationHistory.size();
            }
        }

        boolean isActive() { return avgActivation() > 0.15; }
    }

    // =========================================================================
    // CONNECTION MANAGER
    // =========================================================================
    static class ConnectionManager {
        final Map<String, Map<String, Double>> weights = new ConcurrentHashMap<>();
        final Set<String> nodes = ConcurrentHashMap.newKeySet();
        
        void add(String from, String to, double weight) {
            if (from.equals(to)) return;
            weight = Math.max(0.05, Math.min(1.8, weight));
            weights.computeIfAbsent(from, k -> new ConcurrentHashMap<>()).put(to, weight);
            nodes.add(from);
            nodes.add(to);
        }

        void remove(String from, String to) {
            Map<String, Double> out = weights.get(from);
            if (out != null) out.remove(to);
        }

        double getWeight(String from, String to) {
            Map<String, Double> out = weights.get(from);
            return out != null ? out.getOrDefault(to, 0.1) : 0.1;
        }

        void pruneWeak(double threshold) {
            List<String[]> toRemove = new ArrayList<>();
            weights.forEach((from, map) ->
                map.forEach((to, w) -> { if (w < threshold) toRemove.add(new String[]{from, to}); })
            );
            int removed = Math.min(toRemove.size(), 150);
            for (int i = 0; i < removed; i++) {
                String[] pair = toRemove.get(i);
                remove(pair[0], pair[1]);
            }
        }

        int edgeCount() {
            int count = 0;
            for (Map<String, Double> m : weights.values()) count += m.size();
            return count;
        }

        Set<String> getSuccessors(String node) {
            Map<String, Double> out = weights.get(node);
            return out != null ? out.keySet() : Collections.emptySet();
        }

        Map<String, Map<String, Double>> getWeights() { return Collections.unmodifiableMap(weights); }
    }

    // =========================================================================
    // MEMORY SYSTEM
    // =========================================================================
    static class MemorySystem {
        static class MemoryItem {
            Object content;
            List<String> tags;
            double importance;
            Instant created;
            int accesses;
            double sizeKb;
        }
        private final Map<String, MemoryItem> memories = new ConcurrentHashMap<>();
        private final Map<String, Set<String>> index = new ConcurrentHashMap<>();
        private final LinkedHashMap<String, Long> lru = new LinkedHashMap<>(600, 0.75f, true);
        private final int maxItems;
        private final AtomicInteger accessCount = new AtomicInteger();
        private final AtomicInteger hitCount = new AtomicInteger();

        MemorySystem(int maxItems) { this.maxItems = maxItems; }

        String store(Object content, List<String> tags, double importance) {
            String memId = Integer.toHexString(Objects.hash(content));
            MemoryItem item = new MemoryItem();
            item.content = content;
            item.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
            item.importance = importance;
            item.created = Instant.now();
            item.accesses = 0;
            item.sizeKb = estimateSize(content) / 1024.0;
            memories.put(memId, item);
            if (content instanceof String) {
                String[] words = ((String) content).toLowerCase().split("\\s+");
                for (String w : words) {
                    if (w.length() > 3) {
                        index.computeIfAbsent(w, k -> new HashSet<>()).add(memId);
                    }
                }
            }
            synchronized (lru) { lru.put(memId, System.nanoTime()); }
            pruneIfNeeded();
            return memId;
        }

        Object retrieve(String memId) {
            accessCount.incrementAndGet();
            MemoryItem item = memories.get(memId);
            if (item != null) {
                hitCount.incrementAndGet();
                item.accesses++;
                synchronized (lru) { lru.put(memId, System.nanoTime()); }
                return item.content;
            }
            return null;
        }

        List<MemoryItem> search(String query, int limit) {
            if (query == null || query.isEmpty()) return Collections.emptyList();
            String[] qwords = query.toLowerCase().split("\\s+");
            Map<String, Double> scores = new HashMap<>();
            for (String word : qwords) {
                if (word.length() < 3) continue;
                Set<String> ids = index.getOrDefault(word, Collections.emptySet());
                for (String id : ids) {
                    MemoryItem item = memories.get(id);
                    if (item == null) continue;
                    double score = item.importance + item.accesses * 0.015;
                    if (item.content instanceof String) {
                        Set<String> contentWords = new HashSet<>(Arrays.asList(((String) item.content).toLowerCase().split("\\s+")));
                        long overlap = contentWords.stream().filter(w -> Arrays.asList(qwords).contains(w)).count();
                        score += overlap * 0.25;
                    }
                    scores.merge(id, score, Double::sum);
                }
            }
            return scores.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(e -> memories.get(e.getKey()))
                    .collect(Collectors.toList());
        }

        private void pruneIfNeeded() {
            while (memories.size() > maxItems) {
                // Remove least important/accessed
                String victim = memories.entrySet().stream()
                        .min(Comparator.comparingDouble(e -> e.getValue().importance + e.getValue().accesses * 0.001))
                        .map(Map.Entry::getKey).orElse(null);
                if (victim != null) {
                    memories.remove(victim);
                    synchronized (lru) { lru.remove(victim); }
                    index.values().forEach(s -> s.remove(victim));
                }
            }
        }

        private long estimateSize(Object obj) {
            if (obj instanceof String) return ((String) obj).length() * 2L;
            return 100; // fallback
        }

        Map<String, Object> stats() {
            return Map.of(
                "total", memories.size(),
                "lru_size", lru.size(),
                "hit_rate", accessCount.get() == 0 ? 0.0 : (double) hitCount.get() / accessCount.get(),
                "total_size_mb", memories.values().stream().mapToDouble(m -> m.sizeKb).sum() / 1024.0
            );
        }

        int size() { return memories.size(); }
    }

    // =========================================================================
    // ML BRAIN (simplified - uses basic statistics, no external libraries)
    // =========================================================================
    static class MLBrain {
        private final List<double[]> featureHistory = new ArrayList<>();
        private final List<ExperienceReplay> experienceBuffer = new ArrayList<>();
        private double[] mean = null;
        private double[] std = null;
        private volatile Instant lastTrain = null;
        private volatile double learningRate = 0.01;
        private volatile double explorationRate = 0.3;
        private final int maxBufferSize = 10000;
        private final int batchSize = 32;
        private final Map<String, Double> modelWeights = new ConcurrentHashMap<>();
        private volatile int trainingEpisodes = 0;
        private volatile double cumulativeReward = 0;

        // Experience Replay para Reinforcement Learning
        static class ExperienceReplay {
            final double[] state;
            final double[] action;
            final double reward;
            final double[] nextState;
            final boolean done;
            final Instant timestamp;

            ExperienceReplay(double[] state, double[] action, double reward, double[] nextState, boolean done) {
                this.state = state;
                this.action = action;
                this.reward = reward;
                this.nextState = nextState;
                this.done = done;
                this.timestamp = Instant.now();
            }
        }

        synchronized void train(List<double[]> featuresList) {
            if (featuresList.isEmpty()) return;
            featureHistory.addAll(featuresList);
            if (featureHistory.size() < 20) return;
            
            // Compute mean and std for anomaly detection
            int dim = featuresList.get(0).length;
            mean = new double[dim];
            std = new double[dim];
            for (double[] feat : featureHistory) {
                for (int i = 0; i < dim; i++) mean[i] += feat[i];
            }
            for (int i = 0; i < dim; i++) mean[i] /= featureHistory.size();
            for (double[] feat : featureHistory) {
                for (int i = 0; i < dim; i++) {
                    double diff = feat[i] - mean[i];
                    std[i] += diff * diff;
                }
            }
            for (int i = 0; i < dim; i++) std[i] = Math.sqrt(std[i] / featureHistory.size());
            
            // Atualiza pesos do modelo baseado nos dados
            updateModelWeights(featureHistory);
            
            lastTrain = Instant.now();
            trainingEpisodes++;
        }

        /**
         * Adiciona experiência ao buffer de replay para aprendizado por reforço
         */
        synchronized void addExperience(double[] state, double[] action, double reward, double[] nextState, boolean done) {
            ExperienceReplay experience = new ExperienceReplay(state, action, reward, nextState, done);
            experienceBuffer.add(experience);
            
            // Mantém tamanho máximo do buffer
            if (experienceBuffer.size() > maxBufferSize) {
                experienceBuffer.remove(0);
            }
            
            cumulativeReward += reward;
            
            // Treina com batch se tiver experiência suficiente
            if (experienceBuffer.size() >= batchSize) {
                trainWithExperienceReplay();
            }
        }

        /**
         * Treina usando Experience Replay (Deep Q-Learning simplificado)
         */
        private void trainWithExperienceReplay() {
            if (experienceBuffer.size() < batchSize) return;
            
            // Amostra batch aleatório
            List<ExperienceReplay> batch = new ArrayList<>();
            Random random = new Random();
            for (int i = 0; i < batchSize; i++) {
                int idx = random.nextInt(experienceBuffer.size());
                batch.add(experienceBuffer.get(idx));
            }
            
            // Atualiza Q-values (simplificado)
            for (ExperienceReplay exp : batch) {
                double currentQ = predictQValue(exp.state, exp.action);
                double maxNextQ = predictMaxQValue(exp.nextState);
                double targetQ = exp.reward + (exp.done ? 0 : 0.95 * maxNextQ); // gamma = 0.95
                
                // Atualiza peso
                double error = targetQ - currentQ;
                for (int i = 0; i < exp.state.length; i++) {
                    String weightKey = "w_" + i;
                    double currentWeight = modelWeights.getOrDefault(weightKey, 0.5);
                    modelWeights.put(weightKey, currentWeight + learningRate * error * exp.state[i]);
                }
            }
            
            // Decai taxa de exploração
            explorationRate = Math.max(0.05, explorationRate * 0.9995);
            
            LOGGER.fine(String.format("Training episode %d | Buffer: %d | LR: %.4f | Exploration: %.4f | Reward: %.2f",
                trainingEpisodes, experienceBuffer.size(), learningRate, explorationRate, cumulativeReward));
        }

        /**
         * Prediz Q-value para um estado-ação
         */
        private double predictQValue(double[] state, double[] action) {
            double qValue = 0;
            for (int i = 0; i < state.length && i < action.length; i++) {
                String weightKey = "w_" + i;
                double weight = modelWeights.getOrDefault(weightKey, 0.5);
                qValue += state[i] * action[i] * weight;
            }
            return qValue;
        }

        /**
         * Prediz máximo Q-value para um estado
         */
        private double predictMaxQValue(double[] state) {
            double maxQ = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < state.length; i++) {
                double[] action = new double[state.length];
                action[i] = 1.0; // Ação one-hot
                double q = predictQValue(state, action);
                if (q > maxQ) maxQ = q;
            }
            return maxQ != Double.NEGATIVE_INFINITY ? maxQ : 0;
        }

        /**
         * Atualiza pesos do modelo baseado no histórico
         */
        private void updateModelWeights(List<double[]> features) {
            if (features.isEmpty()) return;
            
            // Atualiza pesos usando gradiente descendente simplificado
            for (int i = 0; i < features.get(0).length; i++) {
                String weightKey = "w_" + i;
                double currentWeight = modelWeights.getOrDefault(weightKey, 0.5);
                
                // Calcula gradiente simplificado
                double gradient = 0;
                for (double[] feat : features) {
                    gradient += (feat[i] - mean[i]) / (std[i] + 0.001);
                }
                gradient /= features.size();
                
                // Atualiza peso
                modelWeights.put(weightKey, currentWeight - learningRate * gradient);
            }
        }

        /**
         * Seleciona ação usando epsilon-greedy policy
         */
        public int selectAction(double[] state, int numActions) {
            if (Math.random() < explorationRate) {
                // Exploração: ação aleatória
                return (int) (Math.random() * numActions);
            } else {
                // Exploração: melhor ação baseada em Q-values
                int bestAction = 0;
                double bestQ = Double.NEGATIVE_INFINITY;
                
                for (int i = 0; i < numActions; i++) {
                    double[] action = new double[numActions];
                    action[i] = 1.0;
                    double q = predictQValue(state, action);
                    if (q > bestQ) {
                        bestQ = q;
                        bestAction = i;
                    }
                }
                return bestAction;
            }
        }

        double predictNextActivation(double[] features) {
            if (featureHistory.size() < 10) return 0.4;
            
            // Usa pesos do modelo para predição
            double prediction = 0;
            for (int i = 0; i < features.length; i++) {
                String weightKey = "w_" + i;
                double weight = modelWeights.getOrDefault(weightKey, 0.5);
                prediction += features[i] * weight;
            }
            
            // Normaliza para [0, 1]
            return Math.max(0, Math.min(1, prediction));
        }

        double[] detectAnomalies(double[][] features) {
            if (features.length == 0 || mean == null) return new double[0];
            double[] scores = new double[features.length];
            for (int i = 0; i < features.length; i++) {
                double sum = 0;
                for (int j = 0; j < features[i].length; j++) {
                    if (std[j] > 0) sum += Math.abs((features[i][j] - mean[j]) / std[j]);
                }
                scores[i] = sum / features[i].length;
            }
            return scores;
        }

        /**
         * Auto-ajusta taxa de aprendizado baseado em performance
         */
        public void autoAdjustLearningRate(double performanceMetric) {
            if (performanceMetric > 0.8) {
                // Performance alta - reduz learning rate para estabilidade
                learningRate = Math.max(0.001, learningRate * 0.95);
            } else if (performanceMetric < 0.5) {
                // Performance baixa - aumenta learning rate para aprender mais rápido
                learningRate = Math.min(0.1, learningRate * 1.05);
            }
        }

        /**
         * Obtém estatísticas de treinamento
         */
        public Map<String, Object> getTrainingStats() {
            return Map.of(
                "training_episodes", trainingEpisodes,
                "experience_buffer_size", experienceBuffer.size(),
                "learning_rate", learningRate,
                "exploration_rate", explorationRate,
                "cumulative_reward", cumulativeReward,
                "model_weights_count", modelWeights.size(),
                "last_train", lastTrain != null ? lastTrain.toString() : "never"
            );
        }

        /**
         * Reseta o modelo para aprendizado fresco
         */
        public void resetModel() {
            modelWeights.clear();
            experienceBuffer.clear();
            learningRate = 0.01;
            explorationRate = 0.3;
            cumulativeReward = 0;
            trainingEpisodes = 0;
            LOGGER.info("Modelo ML resetado para aprendizado fresco");
        }

        boolean isReady() { return lastTrain != null; }
    }

    // =========================================================================
    // QUANTUM SIMULATOR
    // =========================================================================
    static class QuantumSimulator {
        private final int nQubits;
        private final Complex[] state;
        private final Random rand = new Random();

        QuantumSimulator(int nQubits) {
            this.nQubits = nQubits;
            int dim = 1 << nQubits;
            state = new Complex[dim];
            Arrays.fill(state, Complex.ZERO);
            state[0] = Complex.ONE;
        }

        private static class Complex {
            double re, im;
            static final Complex ZERO = new Complex(0, 0);
            static final Complex ONE = new Complex(1, 0);
            Complex(double r, double i) { re = r; im = i; }
            Complex add(Complex o) { return new Complex(re + o.re, im + o.im); }
            Complex mul(Complex o) { return new Complex(re * o.re - im * o.im, re * o.im + im * o.re); }
            Complex mul(double s) { return new Complex(re * s, im * s); }
            double abs2() { return re * re + im * im; }
            Complex conj() { return new Complex(re, -im); }
            static Complex exp(Complex c) {
                double expRe = Math.exp(c.re);
                return new Complex(expRe * Math.cos(c.im), expRe * Math.sin(c.im));
            }
            @Override public String toString() { return String.format("%.3f%+.3fi", re, im); }
        }

        private Complex[][] gateMatrix(double[][] real, double[][] imag) {
            int size = real.length;
            Complex[][] mat = new Complex[size][size];
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    mat[i][j] = new Complex(real[i][j], imag[i][j]);
            return mat;
        }

        private Complex[][] H = gateMatrix(new double[][]{{1,1},{1,-1}}, new double[][]{{0,0},{0,0}}).multiply(1/Math.sqrt(2));
        private Complex[][] X = gateMatrix(new double[][]{{0,1},{1,0}}, new double[][]{{0,0},{0,0}});

        static Complex[] applySingleGate(Complex[] state, Complex[][] gate, int target, int nQubits) {
            int dim = 1 << nQubits;
            Complex[] newState = new Complex[dim];
            for (int i = 0; i < dim; i++) {
                int bit = (i >> target) & 1;
                for (int b = 0; b < 2; b++) {
                    int j = i ^ (bit << target) ^ (b << target);
                    if (state[i] == null || gate[b][bit] == null) continue;
                    Complex contrib = state[i].mul(gate[b][bit]);
                    if (newState[j] == null) newState[j] = contrib;
                    else newState[j] = newState[j].add(contrib);
                }
            }
            return newState;
        }

        void hadamard(int target) { System.arraycopy(applySingleGate(state, H, target, nQubits), 0, state, 0, state.length); }
        void x(int target) { System.arraycopy(applySingleGate(state, X, target, nQubits), 0, state, 0, state.length); }
        void cnot(int control, int target) {
            int dim = 1 << nQubits;
            Complex[] newState = new Complex[dim];
            for (int i = 0; i < dim; i++) {
                if (((i >> control) & 1) == 0) {
                    newState[i] = state[i];
                } else {
                    int flipped = i ^ (1 << target);
                    newState[flipped] = state[i];
                }
            }
            System.arraycopy(newState, 0, state, 0, dim);
        }

        int measure() {
            double[] probs = new double[state.length];
            double sum = 0;
            for (int i = 0; i < state.length; i++) {
                probs[i] = state[i] == null ? 0 : state[i].abs2();
                sum += probs[i];
            }
            double r = rand.nextDouble() * sum;
            double accum = 0;
            for (int i = 0; i < probs.length; i++) {
                accum += probs[i];
                if (r <= accum) {
                    // collapse
                    Arrays.fill(state, Complex.ZERO);
                    state[i] = Complex.ONE;
                    return i;
                }
            }
            return probs.length - 1;
        }

        double vonNeumannEntropy() {
            double[] probs = new double[state.length];
            for (int i = 0; i < state.length; i++)
                probs[i] = state[i] == null ? 0 : state[i].abs2();
            double sum = Arrays.stream(probs).sum();
            double entropy = 0;
            for (double p : probs) {
                p /= sum;
                if (p > 1e-12) entropy -= p * Math.log(p) / Math.log(2);
            }
            return entropy;
        }

        Map<String, Object> runCycle(int depth) {
            long start = System.nanoTime();
            for (int d = 0; d < depth; d++) {
                int q = rand.nextInt(nQubits);
                if (rand.nextDouble() < 0.6) hadamard(q);
                else x(q);
                if (nQubits >= 2 && rand.nextDouble() < 0.4) {
                    int c = rand.nextInt(nQubits - 1);
                    int t = rand.nextInt(nQubits - c - 1) + c + 1;
                    cnot(c, t);
                }
            }
            int outcome = measure();
            double entropy = vonNeumannEntropy();
            double seconds = (System.nanoTime() - start) / 1e9;
            return Map.of("result_bin", Integer.toBinaryString(outcome),
                          "entropy", entropy,
                          "int_value", outcome,
                          "simulation_time_s", seconds,
                          "qubits", nQubits);
        }
    }

    // =========================================================================
    // CÉREBRO PRINCIPAL
    // =========================================================================
    static class VhalinorBrain {
        private final Path basePath;
        final ConcurrentHashMap<String, Neuron> neurons = new ConcurrentHashMap<>();
        final ConnectionManager connections = new ConnectionManager();
        final MemorySystem memory = new MemorySystem(2200);
        final MLBrain ml = new MLBrain();
        final QuantumSimulator quantum = new QuantumSimulator(7);
        volatile double energy = 1200.0;
        volatile PerformanceMode mode = PerformanceMode.BALANCED;
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        VhalinorBrain(String base) {
            basePath = Paths.get(base).toAbsolutePath();
            if (!Files.exists(basePath)) {
                try { Files.createDirectories(basePath); } catch (IOException ignored) {}
            }
            loadOrCreateNeurons();
            loadConnections();
            startBackgroundMaintenance();
        }

        private void loadOrCreateNeurons() {
            File[] files = basePath.toFile().listFiles(f -> f.isFile() && (f.getName().endsWith(".py") ||
                    f.getName().endsWith(".txt") || f.getName().endsWith(".json") || f.getName().endsWith(".md")));
            if (files != null && files.length > 0) {
                for (File f : files) {
                    NeuronType type = guessType(f.getName());
                    String id = Integer.toHexString(Objects.hash(f.getAbsolutePath()));
                    neurons.put(id, new Neuron(id, f.getAbsolutePath(), type));
                }
                LOGGER.info(files.length + " neurônios carregados.");
            } else {
                // create sample
                for (String name : new String[]{"sensor_input.py","quantum_processor.py","decision_logic.py","memory_core.json","emotional_state.md"}) {
                    File file = basePath.resolve(name).toFile();
                    try { file.createNewFile(); } catch (IOException ignored) {}
                    NeuronType type = guessType(name);
                    String id = Integer.toHexString(Objects.hash(file.getAbsolutePath()));
                    neurons.put(id, new Neuron(id, file.getAbsolutePath(), type));
                }
                LOGGER.info("5 neurônios de exemplo criados.");
            }
        }

        private NeuronType guessType(String fileName) {
            String lower = fileName.toLowerCase();
            if (lower.contains("quantum") || lower.contains("qubit")) return NeuronType.QUANTUM;
            if (lower.contains("decision") || lower.contains("logic")) return NeuronType.DECISION;
            if (lower.contains("emotion") || lower.contains("mood")) return NeuronType.EMOTION;
            if (lower.contains("output") || lower.contains("response")) return NeuronType.OUTPUT;
            if (lower.contains("memory") || lower.contains("recall")) return NeuronType.MEMORY;
            if (lower.contains("meta") || lower.contains("self")) return NeuronType.META;
            if (lower.contains("sensor") || lower.contains("input")) return NeuronType.SENSORY;
            return NeuronType.PROCESSING;
        }

        private void loadConnections() {
            Path connFile = basePath.resolve("connections.ser");
            if (Files.exists(connFile)) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(connFile.toFile()))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Double>> saved = (Map<String, Map<String, Double>>) ois.readObject();
                    saved.forEach((from, map) -> map.forEach((to, w) -> connections.add(from, to, w)));
                    LOGGER.info("Conexões carregadas.");
                } catch (Exception e) {
                    createInitialConnections();
                }
            } else {
                createInitialConnections();
            }
        }

        private void createInitialConnections() {
            List<String> ids = new ArrayList<>(neurons.keySet());
            for (String from : ids) {
                int count = Math.min(8, ids.size() - 1);
                List<String> others = new ArrayList<>(ids);
                others.remove(from);
                Collections.shuffle(others);
                for (int i = 0; i < count; i++) {
                    double w = 0.2 + Math.random() * 0.9;
                    connections.add(from, others.get(i), w);
                }
            }
            LOGGER.info("Conexões iniciais criadas: " + connections.edgeCount());
        }

        void saveConnections() {
            Path connFile = basePath.resolve("connections.ser");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(connFile.toFile()))) {
                oos.writeObject(connections.getWeights());
            } catch (IOException e) { LOGGER.warning("Falha ao salvar conexões."); }
        }

        private void startBackgroundMaintenance() {
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    // energy recovery
                    energy = Math.min(1800.0, energy + (40 + Math.random() * 80));
                    if (Math.random() < 0.4) {
                        connections.pruneWeak(0.12);
                    }
                    if (Math.random() < 0.25) trainMLCycle();
                    if (Math.random() < 0.15) autoOptimizeParameters();
                    if (Math.random() < 0.1) reinforceSuccessfulPatterns();
                    System.gc();
                } catch (Exception e) { LOGGER.log(Level.SEVERE, "Erro na manutenção", e); }
            }, 30, 30, TimeUnit.SECONDS);
        }

        private void trainMLCycle() {
            List<double[]> feats = new ArrayList<>();
            List<String> neuronIds = new ArrayList<>(neurons.keySet());
            Collections.shuffle(neuronIds);
            int limit = Math.min(400, neuronIds.size());
            for (int i = 0; i < limit; i++) {
                Neuron n = neurons.get(neuronIds.get(i));
                double[] f = new double[] {
                    n.activation, n.avgActivation(), n.fireCount.get() / 100.0,
                    n.connectionsOut.size(), n.connectionsIn.size(), n.importance,
                    n.type == NeuronType.QUANTUM ? 1.0 : 0.0
                };
                feats.add(f);
            }
            ml.train(feats);
            
            // Auto-ajusta learning rate baseado em performance
            double performance = calculateSystemPerformance();
            ml.autoAdjustLearningRate(performance);
        }

        /**
         * Auto-otimiza parâmetros do sistema baseado em performance
         */
        private void autoOptimizeParameters() {
            double avgActivation = neurons.values().stream()
                .mapToDouble(Neuron::avgActivation)
                .average()
                .orElse(0.5);
            
            // Ajusta thresholds baseados em média de ativação
            if (avgActivation > 0.7) {
                // Sistema muito ativo - aumenta thresholds
                neurons.values().forEach(n -> {
                    n.threshold = Math.min(0.9, n.threshold * 1.01);
                });
            } else if (avgActivation < 0.3) {
                // Sistema muito inativo - reduz thresholds
                neurons.values().forEach(n -> {
                    n.threshold = Math.max(0.3, n.threshold * 0.99);
                });
            }
            
            // Ajusta pesos de conexões baseados em sucesso
            adjustConnectionWeights();
            
            LOGGER.fine(String.format("Auto-otimização: AvgActivation=%.2f | Neurons=%d", 
                avgActivation, neurons.size()));
        }

        /**
         * Ajusta pesos de conexões baseados em sucesso
         */
        private void adjustConnectionWeights() {
            List<String> neuronIds = new ArrayList<>(neurons.keySet());
            for (String from : neuronIds) {
                Neuron n = neurons.get(from);
                if (n == null || !n.isActive()) continue;
                
                for (String to : connections.getSuccessors(from)) {
                    Neuron target = neurons.get(to);
                    if (target == null) continue;
                    
                    double currentWeight = connections.getWeight(from, to);
                    double targetActivity = target.avgActivation();
                    
                    // Aumenta peso se alvo está ativo (conexão bem-sucedida)
                    if (targetActivity > 0.5) {
                        double newWeight = Math.min(1.8, currentWeight * 1.02);
                        connections.add(from, to, newWeight);
                    } 
                    // Reduz peso se alvo está inativo (conexão fraca)
                    else if (targetActivity < 0.2) {
                        double newWeight = Math.max(0.05, currentWeight * 0.98);
                        connections.add(from, to, newWeight);
                    }
                }
            }
        }

        /**
         * Reforça padrões bem-sucedidos usando aprendizado por reforço
         */
        private void reinforceSuccessfulPatterns() {
            List<String> activeNeurons = neurons.values().stream()
                .filter(Neuron::isActive)
                .map(n -> n.id)
                .collect(Collectors.toList());
            
            if (activeNeurons.size() < 2) return;
            
            // Cria experiência de reforço para cada neurônio ativo
            for (String neuronId : activeNeurons) {
                Neuron n = neurons.get(neuronId);
                if (n == null) continue;
                
                // Estado atual
                double[] state = new double[] {
                    n.activation, n.avgActivation(), n.fireCount.get() / 100.0,
                    n.connectionsOut.size(), n.connectionsIn.size(), n.importance,
                    energy / 1800.0
                };
                
                // Ação (selecionar qual neurônio estimular)
                int actionIdx = ml.selectAction(state, Math.min(10, activeNeurons.size()));
                String targetId = activeNeurons.get(actionIdx);
                
                // Executa ação e mede recompensa
                double reward = executeActionAndGetReward(neuronId, targetId);
                
                // Próximo estado
                double[] nextState = new double[] {
                    n.activation, n.avgActivation(), n.fireCount.get() / 100.0,
                    n.connectionsOut.size(), n.connectionsIn.size(), n.importance,
                    energy / 1800.0
                };
                
                // Adiciona experiência ao buffer de replay
                double[] action = new double[Math.min(10, activeNeurons.size())];
                action[actionIdx] = 1.0;
                ml.addExperience(state, action, reward, nextState, false);
            }
        }

        /**
         * Executa ação e retorna recompensa
         */
        private double executeActionAndGetReward(String fromId, String toId) {
            Neuron from = neurons.get(fromId);
            Neuron to = neurons.get(toId);
            if (from == null || to == null) return -0.1;
            
            double initialActivity = to.avgActivation();
            
            // Estimula neurônio alvo
            double strength = 0.3 + Math.random() * 0.4;
            stimulate(toId, strength);
            
            // Calcula recompensa baseada em mudança de atividade
            double finalActivity = to.avgActivation();
            double reward = (finalActivity - initialActivity) * 2.0;
            
            // Penaliza se energia muito baixa
            if (energy < 300) reward -= 0.5;
            
            // Recompensa extra se ambos neurônios ficaram ativos
            if (from.isActive() && to.isActive()) reward += 0.3;
            
            return Math.max(-1.0, Math.min(1.0, reward));
        }

        /**
         * Calcula performance geral do sistema
         */
        private double calculateSystemPerformance() {
            double avgActivation = neurons.values().stream()
                .mapToDouble(Neuron::avgActivation)
                .average()
                .orElse(0.5);
            
            double activeRatio = neurons.values().stream()
                .filter(Neuron::isActive)
                .count() / (double) neurons.size();
            
            double energyRatio = energy / 1800.0;
            
            // Performance ponderada
            return (avgActivation * 0.4) + (activeRatio * 0.4) + (energyRatio * 0.2);
        }

        double stimulate(String neuronId, double strength) {
            Neuron n = neurons.get(neuronId);
            if (n == null) return 0.0;
            double cost = n.energyCost * Math.pow(strength, 1.3);
            if (energy < cost * 0.6) return 0.0;
            energy -= cost;
            double fired = n.stimulate(strength, energy);
            if (fired > 0) {
                for (String target : connections.getSuccessors(neuronId)) {
                    double w = connections.getWeight(neuronId, target);
                    double propStrength = fired * w * 0.7;
                    CompletableFuture.runAsync(() -> stimulate(target, propStrength));
                }
                
                // Adiciona experiência de reforço para estimulação bem-sucedida
                if (ml.isReady()) {
                    double[] state = new double[] {
                        n.activation, n.avgActivation(), n.fireCount.get() / 100.0,
                        n.connectionsOut.size(), n.connectionsIn.size(), n.importance,
                        energy / 1800.0
                    };
                    double[] nextState = state.clone();
                    double[] action = new double[1];
                    action[0] = strength;
                    ml.addExperience(state, action, fired * 0.5, nextState, false);
                }
            }
            return fired;
        }

        String think(String stimulus) {
            List<Neuron> active = neurons.values().stream().filter(Neuron::isActive).collect(Collectors.toList());
            if (active.isEmpty()) return "Sistema em repouso profundo...";
            active.sort(Comparator.comparingDouble(Neuron::avgActivation).reversed());
            List<String> concepts = new ArrayList<>();
            for (Neuron n : active.subList(0, Math.min(5, active.size()))) {
                switch (n.type) {
                    case EMOTION: concepts.add("emoção"); break;
                    case DECISION: concepts.add("decisão crítica"); break;
                    case QUANTUM: concepts.add("incerteza quântica"); break;
                    default: concepts.add(n.type.name().toLowerCase());
                }
            }
            String thought = "Processando " + (stimulus.isEmpty() ? "fluxo interno" : stimulus) + ". ";
            thought += "Neurônios dominantes: " + String.join(", ", concepts) + ". ";
            if (active.stream().anyMatch(n -> n.type == NeuronType.QUANTUM) && Math.random() < 0.3) {
                Map<String, Object> qres = quantum.runCycle(4);
                thought += "Entropia quântica: " + String.format("%.3f", (Double) qres.get("entropy")) + ". ";
            }
            thought += "Energia: " + (int) energy + " | Ativos: " + active.size();
            return thought;
        }

        Map<String, Object> quantumStimulus() {
            Map<String, Object> result = quantum.runCycle(10);
            int outcome = (Integer) result.get("int_value");
            List<Neuron> qneurons = neurons.values().stream().filter(n -> n.type == NeuronType.QUANTUM).collect(Collectors.toList());
            int stimulated = 0;
            for (int i = 0; i < Math.min(quantum.nQubits, qneurons.size()); i++) {
                if (((outcome >> i) & 1) == 1) {
                    double strength = 0.4 + Math.random() * 0.7;
                    stimulate(qneurons.get(i).id, strength);
                    stimulated++;
                }
            }
            result.put("stimulated_neurons", stimulated);
            return result;
        }

        /**
         * Obtém estatísticas completas do sistema incluindo ML
         */
        Map<String, Object> getSystemStats() {
            Map<String, Object> stats = new LinkedHashMap<>();
            
            // Estatísticas de neurônios
            long activeCount = neurons.values().stream().filter(Neuron::isActive).count();
            double avgActivation = neurons.values().stream()
                .mapToDouble(Neuron::avgActivation)
                .average()
                .orElse(0.0);
            int totalFires = neurons.values().stream()
                .mapToInt(n -> n.fireCount.get())
                .sum();
            
            stats.put("neurons_count", neurons.size());
            stats.put("active_neurons", activeCount);
            stats.put("avg_activation", String.format("%.3f", avgActivation));
            stats.put("total_fires", totalFires);
            stats.put("connections", connections.edgeCount());
            
            // Estatísticas de energia
            stats.put("energy", String.format("%.1f", energy));
            stats.put("energy_ratio", String.format("%.2f%%", (energy / 1800.0) * 100));
            stats.put("performance_mode", mode.label);
            
            // Estatísticas de memória
            stats.put("memory_stats", memory.stats());
            
            // Estatísticas de ML/Reinforcement Learning
            stats.put("ml_training", ml.getTrainingStats());
            
            // Performance do sistema
            stats.put("system_performance", String.format("%.2f", calculateSystemPerformance()));
            
            return stats;
        }

        /**
         * Adiciona memória de longo prazo com auto-consolidação
         */
        String addLongTermMemory(Object content, List<String> tags, double importance) {
            String memId = memory.store(content, tags, importance);
            
            // Auto-consolidação: se memória importante, reforça conexões relacionadas
            if (importance > 0.7) {
                consolidateMemory(memId, tags);
            }
            
            return memId;
        }

        /**
         * Consolida memória reforçando conexões relacionadas
         */
        private void consolidateMemory(String memId, List<String> tags) {
            // Encontra neurônios relacionados aos tags
            for (String tag : tags) {
                neurons.values().stream()
                    .filter(n -> n.tags.contains(tag))
                    .forEach(n -> {
                        // Aumenta importância de neurônios relacionados
                        n.importance = Math.min(1.4, n.importance * 1.05);
                        // Aumenta threshold para evitar disparos excessivos
                        n.threshold = Math.min(0.9, n.threshold * 1.01);
                    });
            }
            
            LOGGER.fine("Memória consolidada: " + memId);
        }

        /**
         * Recupera memórias relevantes com contexto
         */
        List<MemorySystem.MemoryItem> retrieveRelevantMemories(String query, int limit) {
            List<MemorySystem.MemoryItem> results = memory.search(query, limit);
            
            // Reforça memórias acessadas (auto-aprendizado)
            for (MemorySystem.MemoryItem item : results) {
                item.importance = Math.min(1.0, item.importance * 1.01);
            }
            
            return results;
        }

        void saveState() {
            try {
                Files.createDirectories(basePath);
                Map<String, Object> state = new LinkedHashMap<>();
                state.put("energy", energy);
                state.put("mode", mode.label);
                Map<String, Map<String, Object>> neuronData = new LinkedHashMap<>();
                neurons.forEach((id, n) -> {
                    Map<String, Object> nd = new LinkedHashMap<>();
                    nd.put("activation", n.activation);
                    nd.put("fireCount", n.fireCount.get());
                    nd.put("importance", n.importance);
                    nd.put("threshold", n.threshold);
                    neuronData.put(id, nd);
                });
                state.put("neurons", neuronData);
                // Save using standard serialization (for simplicity)
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(basePath.resolve("brain_state.ser").toFile()))) {
                    oos.writeObject(state);
                }
                saveConnections();
                LOGGER.info("Estado cerebral salvo.");
            } catch (IOException e) {
                LOGGER.severe("Falha ao salvar estado: " + e.getMessage());
            }
        }

        @SuppressWarnings("unchecked")
        void loadState() {
            Path stateFile = basePath.resolve("brain_state.ser");
            if (!Files.exists(stateFile)) return;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(stateFile.toFile()))) {
                Map<String, Object> state = (Map<String, Object>) ois.readObject();
                energy = (double) state.get("energy");
                mode = PerformanceMode.valueOf((String) state.getOrDefault("mode", "BALANCED"));
                Map<String, Map<String, Object>> neuronData = (Map<String, Map<String, Object>>) state.get("neurons");
                if (neuronData != null) {
                    neuronData.forEach((id, data) -> {
                        Neuron n = neurons.get(id);
                        if (n != null) {
                            n.activation = (double) data.getOrDefault("activation", 0.0);
                            n.fireCount.set((int) data.getOrDefault("fireCount", 0));
                            n.importance = (double) data.getOrDefault("importance", 1.0);
                            n.threshold = (double) data.getOrDefault("threshold", 0.55);
                        }
                    });
                }
                LOGGER.info("Estado cerebral carregado.");
            } catch (Exception e) {
                LOGGER.warning("Falha ao carregar estado: " + e.getMessage());
            }
        }

        void shutdown() {
            scheduler.shutdownNow();
            saveState();
        }
    }

    // =========================================================================
    // GUI (Swing)
    // =========================================================================
    static class BrainGUI extends JFrame {
        private final VhalinorBrain brain;
        private final JTabbedPane tabbedPane = new JTabbedPane();
        private final JLabel statusLabel = new JLabel("Sistema inicializado");
        private final JTextArea consoleArea = new JTextArea();
        private final DefaultTableModel neuronTableModel;
        private final DefaultTableModel memoryTableModel;
        private final JTable neuronTable;
        private final JTable memoryTable;
        private final JTextField searchField = new JTextField(20);
        private final GraphPanel graphPanel;
        private final Map<String, JLabel> metricLabels = new LinkedHashMap<>();
        private final javax.swing.Timer updateTimer;

        BrainGUI(VhalinorBrain brain) {
            super("VHALINOR.IAG 5.0 - Núcleo Cerebral");
            this.brain = brain;
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    int choice = JOptionPane.showConfirmDialog(BrainGUI.this,
                                                               "Salvar estado antes de sair?",
                                                               "Sair", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) brain.saveState();
                    setVisible(false);
                    dispose();
                    System.exit(0);
                }
            });
            setSize(1280, 820);
            setLocationRelativeTo(null);

            // Overview tab
            JPanel overviewPanel = new JPanel(new BorderLayout(10,10));
            overviewPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            JLabel titleLabel = new JLabel("VHALINOR.IAG 5.0 - Sistema Cerebral Artificial", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Helvetica", Font.BOLD, 16));
            overviewPanel.add(titleLabel, BorderLayout.NORTH);

            JPanel metricsPanel = new JPanel(new GridLayout(3,2,10,5));
            metricsPanel.setBorder(BorderFactory.createTitledBorder("Métricas"));
            addMetricRow(metricsPanel, "Energia:", "energy");
            addMetricRow(metricsPanel, "Neurônios Ativos:", "active");
            addMetricRow(metricsPanel, "Conexões:", "edges");
            addMetricRow(metricsPanel, "Memória Itens:", "mem");
            addMetricRow(metricsPanel, "Taxa de Acerto (%):", "hit");
            addMetricRow(metricsPanel, "Modo:", "mode");
            overviewPanel.add(metricsPanel, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel(new FlowLayout());
            controlPanel.setBorder(BorderFactory.createTitledBorder("Controles"));
            addButton(controlPanel, "Otimizar Agora", e -> optimize());
            addButton(controlPanel, "Pensar", e -> manualThink());
            addButton(controlPanel, "Estimular Aleatório", e -> randomStimulus());
            addButton(controlPanel, "Ciclo Quântico", e -> quantumCycle());
            addButton(controlPanel, "Salvar Estado", e -> brain.saveState());
            addButton(controlPanel, "Recarregar", e -> reloadState());
            overviewPanel.add(controlPanel, BorderLayout.SOUTH);

            tabbedPane.addTab(" Overview ", overviewPanel);

            // Neurons tab
            neuronTableModel = new DefaultTableModel(new String[]{"ID","Tipo","Ativação","Fires","Importância","Conexões"},0);
            neuronTable = new JTable(neuronTableModel);
            neuronTable.setFillsViewportHeight(true);
            JScrollPane neuronScroll = new JScrollPane(neuronTable);
            JPanel neuronPanel = new JPanel(new BorderLayout());
            JPanel neuronToolbar = new JPanel();
            neuronToolbar.add(new JButton("Atualizar"){{ addActionListener(e->updateNeuronList()); }});
            neuronToolbar.add(new JComboBox<>(new String[]{"Todos","Ativos","Quânticos","Memória","Decisão","Emoção"}){{
                addActionListener(e -> updateNeuronList());
            }});
            neuronPanel.add(neuronToolbar, BorderLayout.NORTH);
            neuronPanel.add(neuronScroll, BorderLayout.CENTER);
            tabbedPane.addTab(" Neurônios ", neuronPanel);

            // Graph tab
            graphPanel = new GraphPanel(brain);
            tabbedPane.addTab(" Grafo Neural ", graphPanel);

            // Console tab
            consoleArea.setEditable(false);
            consoleArea.setBackground(new Color(0x0d1117));
            consoleArea.setForeground(new Color(0xc9d1d9));
            consoleArea.setFont(new Font("Consolas", Font.PLAIN, 10));
            JScrollPane consoleScroll = new JScrollPane(consoleArea);
            JPanel consolePanel = new JPanel(new BorderLayout());
            JPanel consoleToolbar = new JPanel();
            consoleToolbar.add(new JButton("Limpar"){{ addActionListener(e->consoleArea.setText("")); }});
            consolePanel.add(consoleToolbar, BorderLayout.NORTH);
            consolePanel.add(consoleScroll, BorderLayout.CENTER);
            tabbedPane.addTab(" Console ", consolePanel);

            // Memory tab
            memoryTableModel = new DefaultTableModel(new String[]{"ID","Conteúdo","Tags","Importância","Acessos"},0);
            memoryTable = new JTable(memoryTableModel);
            JScrollPane memoryScroll = new JScrollPane(memoryTable);
            JPanel memoryPanel = new JPanel(new BorderLayout());
            JPanel memoryToolbar = new JPanel();
            memoryToolbar.add(new JLabel("Buscar:"));
            memoryToolbar.add(searchField);
            memoryToolbar.add(new JButton("Buscar"){{ addActionListener(e->searchMemory()); }});
            memoryToolbar.add(new JButton("Estatísticas"){{ addActionListener(e->showMemoryStats()); }});
            memoryPanel.add(memoryToolbar, BorderLayout.NORTH);
            memoryPanel.add(memoryScroll, BorderLayout.CENTER);
            tabbedPane.addTab(" Memória ", memoryPanel);

            add(tabbedPane);
            // status bar
            JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusBar.add(statusLabel);
            add(statusBar, BorderLayout.SOUTH);

            // start periodic updates
            updateTimer = new javax.swing.Timer(3000, e -> updateAll());
            updateTimer.start();
            updateAll();
            setVisible(true);
        }

        private void addMetricRow(JPanel panel, String label, String key) {
            JLabel lbl = new JLabel(label);
            JLabel val = new JLabel("--");
            val.setFont(new Font("Consolas", Font.PLAIN, 11));
            panel.add(lbl);
            panel.add(val);
            metricLabels.put(key, val);
        }

        private void addButton(JPanel panel, String text, java.awt.event.ActionListener action) {
            JButton btn = new JButton(text);
            btn.addActionListener(action);
            panel.add(btn);
        }

        private void updateAll() {
            updateMetrics();
            updateNeuronList();
            graphPanel.repaint();
        }

        private void updateMetrics() {
            double energy = brain.energy;
            long active = brain.neurons.values().stream().filter(Neuron::isActive).count();
            int edges = brain.connections.edgeCount();
            int memSize = brain.memory.size();
            Map<String,Object> memStats = brain.memory.stats();
            double hitRate = (double) memStats.get("hit_rate");
            String mode = brain.mode.label;

            metricLabels.get("energy").setText(String.format("%.0f", energy));
            metricLabels.get("active").setText(String.valueOf(active));
            metricLabels.get("edges").setText(String.valueOf(edges));
            metricLabels.get("mem").setText(String.valueOf(memSize));
            metricLabels.get("hit").setText(String.format("%.1f%%", hitRate * 100));
            metricLabels.get("mode").setText(mode);
            statusLabel.setText("Última atualização: " + java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }

        private void updateNeuronList() {
            SwingUtilities.invokeLater(() -> {
                neuronTableModel.setRowCount(0);
                List<Map.Entry<String, Neuron>> all = new ArrayList<>(brain.neurons.entrySet());
                if (all.size() > 100) Collections.shuffle(all, new Random());
                for (int i = 0; i < Math.min(all.size(), 100); i++) {
                    Neuron n = all.get(i).getValue();
                    neuronTableModel.addRow(new Object[]{
                        all.get(i).getKey().substring(0, Math.min(10, all.get(i).getKey().length())) + "...",
                        n.type.name(),
                        String.format("%.3f", n.avgActivation()),
                        n.fireCount.get(),
                        String.format("%.2f", n.importance),
                        n.connectionsOut.size() + n.connectionsIn.size()
                    });
                }
            });
        }

        private void searchMemory() {
            String query = searchField.getText().trim();
            if (query.isEmpty()) return;
            List<MemorySystem.MemoryItem> results = brain.memory.search(query, 20);
            SwingUtilities.invokeLater(() -> {
                memoryTableModel.setRowCount(0);
                for (MemorySystem.MemoryItem item : results) {
                    String content = item.content instanceof String ? ((String) item.content).substring(0, Math.min(50, ((String) item.content).length())) : item.content.toString();
                    memoryTableModel.addRow(new Object[]{
                        Integer.toHexString(Objects.hash(item.content)).substring(0,10)+"...",
                        content,
                        String.join(",", item.tags),
                        String.format("%.2f", item.importance),
                        item.accesses
                    });
                }
            });
        }

        private void showMemoryStats() {
            Map<String,Object> stats = brain.memory.stats();
            String msg = String.format("""
                Estatísticas da Memória:
                Total de itens: %d
                Tamanho total: %.2f MB
                Taxa de acerto: %.1f%%
                Tamanho da LRU: %d
                """, stats.get("total"),
                (double) stats.get("total_size_mb"),
                ((double) stats.get("hit_rate")) * 100,
                stats.get("lru_size"));
            JOptionPane.showMessageDialog(this, msg, "Estatísticas", JOptionPane.INFORMATION_MESSAGE);
        }

        // Actions run in background using SwingWorker to keep UI responsive
        private void optimize() {
            new SwingWorker<Void,Void>() {
                protected Void doInBackground() {
                    log("→ Iniciando otimização...", null);
                    brain.trainMLCycle();
                    brain.connections.pruneWeak(0.1);
                    System.gc();
                    log("→ Otimização concluída.", Color.GREEN);
                    return null;
                }
            }.execute();
        }
        private void manualThink() {
            new SwingWorker<Void,Void>() {
                protected Void doInBackground() {
                    String thought = brain.think("interação manual");
                    log("[PENSAMENTO] " + thought, null);
                    return null;
                }
            }.execute();
        }
        private void randomStimulus() {
            new SwingWorker<Void,Void>() {
                protected Void doInBackground() {
                    if (!brain.neurons.isEmpty()) {
                        String id = brain.neurons.keySet().stream().skip(new Random().nextInt(brain.neurons.size())).findFirst().orElse(null);
                        double strength = 0.3 + Math.random() * 0.9;
                        double result = brain.stimulate(id, strength);
                        log("→ Estimulado neurônio " + id + " força=" + strength + " result=" + result, null);
                    }
                    return null;
                }
            }.execute();
        }
        private void quantumCycle() {
            new SwingWorker<Void,Void>() {
                protected Void doInBackground() {
                    log("→ Iniciando ciclo quântico...", null);
                    Map<String,Object> res = brain.quantumStimulus();
                    log("→ Ciclo quântico concluído. Resultado: " + res.get("result_bin") + ", Entropia: " + String.format("%.3f", res.get("entropy")), Color.GREEN);
                    return null;
                }
            }.execute();
        }
        private void reloadState() {
            new SwingWorker<Void,Void>() {
                protected Void doInBackground() {
                    brain.loadState();
                    log("→ Estado recarregado do disco.", Color.GREEN);
                    return null;
                }
            }.execute();
        }

        private void log(String msg, Color color) {
            SwingUtilities.invokeLater(() -> {
                String time = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                consoleArea.append("[" + time + "] " + msg + "\n");
                if (color != null) {
                    // simple coloring not supported in plain JTextArea; omit
                }
                consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
            });
        }

        // Simple graph panel
        class GraphPanel extends JPanel {
            private final VhalinorBrain brain;
            GraphPanel(VhalinorBrain brain) { this.brain = brain; setPreferredSize(new Dimension(600, 400)); }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRect(0,0,w,h);

                List<String> nodes = new ArrayList<>(brain.neurons.keySet());
                if (nodes.isEmpty()) return;
                Map<String, Point> positions = new HashMap<>();
                Random rand = new Random(42);
                for (String n : nodes) {
                    positions.put(n, new Point(50 + rand.nextInt(w-100), 50 + rand.nextInt(h-100)));
                }
                // Draw edges
                g2.setColor(Color.LIGHT_GRAY);
                for (Map.Entry<String, Map<String, Double>> e : brain.connections.getWeights().entrySet()) {
                    Point from = positions.get(e.getKey());
                    if (from == null) continue;
                    for (String to : e.getValue().keySet()) {
                        Point toP = positions.get(to);
                        if (toP != null) {
                            g2.drawLine(from.x, from.y, toP.x, toP.y);
                        }
                    }
                }
                // Draw nodes
                for (String n : nodes) {
                    Point p = positions.get(n);
                    Neuron neuron = brain.neurons.get(n);
                    if (neuron == null) continue;
                    Color c = switch (neuron.type) {
                        case QUANTUM -> Color.MAGENTA;
                        case EMOTION -> Color.RED;
                        case DECISION -> Color.GREEN;
                        case MEMORY -> Color.ORANGE;
                        default -> Color.BLUE;
                    };
                    g2.setColor(c);
                    int size = (int) (20 + neuron.importance * 10);
                    g2.fillOval(p.x - size/2, p.y - size/2, size, size);
                    g2.setColor(Color.BLACK);
                    g2.drawString(neuron.type.name().substring(0,3), p.x - 10, p.y + 5);
                }
            }
        }
    }

    // =========================================================================
    // MAIN
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                VhalinorBrain brain = new VhalinorBrain("./vhalinor_iag");
                brain.loadState();

                // Start background brain loop
                ExecutorService background = Executors.newSingleThreadExecutor();
                background.submit(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Thread.sleep(12_000);
                            if (!brain.neurons.isEmpty() && Math.random() < 0.55) {
                                String id = brain.neurons.keySet().stream().skip(new Random().nextInt(brain.neurons.size())).findFirst().orElse(null);
                                brain.stimulate(id, 0.2 + Math.random() * 0.7);
                            }
                            if (Math.random() < 0.18) {
                                String thought = brain.think("loop");
                                LOGGER.info(thought);
                            }
                            if (Math.random() < 0.12) {
                                brain.quantumStimulus();
                            }
                        } catch (InterruptedException e) { break; }
                    }
                });

                new BrainGUI(brain);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    brain.shutdown();
                    background.shutdownNow();
                }));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro fatal", e);
                JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
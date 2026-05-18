import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Consumer;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * Sistema Avançado de IA para Previsão Financeira - Módulos Adicionais (Java Edition)
 *
 * Contém: AdvancedNeuron, AdvancedNeuralNetwork, RealTimeProcessor,
 *         AdvancedPredictionSystem, CognitiveAnalyzer e estruturas de suporte.
 *
 * Melhorias em relação ao original:
 * - Coleções thread-safe (ConcurrentLinkedDeque, ConcurrentHashMap)
 * - Registro de eventos com timestamps Instant
 * - Processadores assíncronos com ExecutorService
 * - Forte tipagem e encapsulamento
 */
public class AdvancedAIModules {

    // ======================== ENUMS ========================
    public enum NeuronType { HEBOURNE, SPIKING, RECURRENT, CONVOLUTIONAL, ATTENTION, MEMORY, QUANTUM, ADAPTIVE }

    public enum NeuronState { IDLE, ACTIVATING, ACTIVE, INHIBITING, RECOVERING, LEARNING, PREDICTING }

    public enum RealTimeEventType { MARKET_DATA, PREDICTION_UPDATE, ALERT, SYSTEM_STATUS, NEURAL_ACTIVITY, PERFORMANCE_METRIC }

    public enum PredictionModel { ENSEMBLE, QUANTUM, NEURAL, HYBRID, BAYESIAN, REINFORCEMENT }

    public enum CognitivePattern {
        TREND_FOLLOWING, MEAN_REVERSION, BREAKOUT, VOLATILITY_SPIKE,
        LIQUIDITY_CRUNCH, SENTIMENT_SHIFT, TECHNICAL_CONVERGENCE, FUNDAMENTAL_MISMATCH
    }

    // ======================== DATA CLASSES (Records) ========================
    public record Prediction(String symbol, double prediction, double confidence,
                            double uncertainty, PredictionModel modelType,
                            Instant timestamp, List<String> featuresUsed,
                            Map<String, Object> metadata) {}

    public record CognitiveInsight(CognitivePattern pattern, double confidence, double significance,
                                   Instant timestamp, Map<String, Object> evidence,
                                   String recommendation, String expectedOutcome, String timeHorizon) {}

    public static class RealTimeEvent {
        private final RealTimeEventType eventType;
        private final Instant timestamp;
        private final Map<String, Object> data;
        private final int priority;
        private final String source;
        private volatile boolean processed;

        public RealTimeEvent(RealTimeEventType eventType, Map<String, Object> data,
                             int priority, String source) {
            this.eventType = eventType;
            this.timestamp = Instant.now();
            this.data = new HashMap<>(data);
            this.priority = priority;
            this.source = source;
            this.processed = false;
        }

        public RealTimeEventType getEventType() { return eventType; }
        public Instant getTimestamp() { return timestamp; }
        public Map<String, Object> getData() { return Collections.unmodifiableMap(data); }
        public int getPriority() { return priority; }
        public String getSource() { return source; }
        public boolean isProcessed() { return processed; }
        public void markProcessed() { this.processed = true; }
    }

    // ======================== ADVANCED NEURON ========================
    public static class AdvancedNeuron {
        private final String id;
        private final NeuronType type;
        private volatile NeuronState state;
        private volatile double activation;
        private volatile double threshold;
        private volatile double learningRate;
        private final Deque<Double> memory;
        private final Map<String, Double> connections;
        private final Map<String, Double> weights;
        private volatile double bias;
        private Instant lastActivationTime;
        private final Deque<Double> activationHistory;
        private volatile double plasticityFactor;
        private volatile double hebbianFactor;
        private volatile double decayRate;
        private final int refractoryPeriod;
        private volatile int currentRefractory;

        public AdvancedNeuron(String id, NeuronType type, double threshold, double learningRate,
                              int refractoryPeriod) {
            this.id = id;
            this.type = type;
            this.state = NeuronState.IDLE;
            this.activation = 0.0;
            this.threshold = threshold;
            this.learningRate = learningRate;
            this.memory = new ConcurrentLinkedDeque<>();
            this.connections = new ConcurrentHashMap<>();
            this.weights = new ConcurrentHashMap<>();
            this.bias = 0.0;
            this.lastActivationTime = Instant.now();
            this.activationHistory = new ConcurrentLinkedDeque<>();
            this.plasticityFactor = 1.0;
            this.hebbianFactor = 0.1;
            this.decayRate = 0.001;
            this.refractoryPeriod = refractoryPeriod;
            this.currentRefractory = 0;
        }

        public double activate(Map<String, Double> inputs) {
            if (currentRefractory > 0) {
                currentRefractory--;
                return 0.0;
            }

            double weightedSum = bias;
            for (Map.Entry<String, Double> entry : inputs.entrySet()) {
                double weight = weights.getOrDefault(entry.getKey(), 0.0);
                weightedSum += entry.getValue() * weight;
            }

            double act = activationFunction(weightedSum);
            this.activation = act;
            activationHistory.addLast(act);
            if (activationHistory.size() > 1000) activationHistory.pollFirst();
            lastActivationTime = Instant.now();

            hebbianLearning(inputs);

            if (act > threshold) {
                currentRefractory = refractoryPeriod;
                state = NeuronState.ACTIVE;
            } else {
                state = NeuronState.IDLE;
            }

            return act;
        }

        private double activationFunction(double x) {
            return switch (type) {
                case SPIKING -> x > threshold ? 1.0 : 0.0;
                case HEBOURNE -> 1.0 / (1.0 + Math.abs(x - threshold));
                case ATTENTION -> Math.max(0, x); // ReLU
                default -> 1.0 / (1.0 + Math.exp(-x)); // Sigmoid
            };
        }

        private void hebbianLearning(Map<String, Double> inputs) {
            if (activation > threshold) {
                for (Map.Entry<String, Double> entry : inputs.entrySet()) {
                    String key = entry.getKey();
                    double value = entry.getValue();
                    double currentWeight = weights.getOrDefault(key, 0.0);
                    double delta = learningRate * hebbianFactor * value * activation;
                    weights.put(key, (currentWeight + delta) * plasticityFactor);
                }
            }
            // Decay
            for (Map.Entry<String, Double> entry : weights.entrySet()) {
                entry.setValue(entry.getValue() * (1.0 - decayRate));
            }
        }

        public void updateThreshold(double targetActivation) {
            if (activationHistory.size() > 10) {
                double avg = activationHistory.stream().skip(Math.max(0, activationHistory.size() - 10))
                        .mapToDouble(Double::doubleValue).average().orElse(0);
                if (avg > targetActivation) {
                    threshold = Math.min(1.0, threshold + 0.01);
                } else if (avg < targetActivation * 0.5) {
                    threshold = Math.max(0.1, threshold - 0.01);
                }
            }
        }

        // getters e setters necessários
        public String getId() { return id; }
        public NeuronType getType() { return type; }
        public NeuronState getState() { return state; }
        public double getActivation() { return activation; }
        public Map<String, Double> getWeights() { return Collections.unmodifiableMap(weights); }
        public double getBias() { return bias; }
        public void setBias(double bias) { this.bias = bias; }
        public Map<String, Double> getConnections() { return Collections.unmodifiableMap(connections); }
        public void setLearningRate(double lr) { this.learningRate = lr; }
        public void setPlasticityFactor(double pf) { this.plasticityFactor = pf; }
    }

    // ======================== ADVANCED NEURAL NETWORK ========================
    public static class AdvancedNeuralNetwork {
        private final String networkId;
        private final Map<String, AdvancedNeuron> neurons = new ConcurrentHashMap<>();
        private final Map<String, List<String>> layers = new LinkedHashMap<>();
        private final Map<String, Double> performanceMetrics = new ConcurrentHashMap<>();
        private Instant creationTime = Instant.now();
        private Instant lastUpdate = Instant.now();

        public AdvancedNeuralNetwork(String networkId) {
            this.networkId = networkId;
            performanceMetrics.put("total_activations", 0.0);
            performanceMetrics.put("avg_activation", 0.0);
            performanceMetrics.put("learning_efficiency", 0.0);
            performanceMetrics.put("network_coherence", 0.0);
        }

        public void addNeuron(AdvancedNeuron neuron) {
            neurons.put(neuron.getId(), neuron);
            lastUpdate = Instant.now();
        }

        public void connectNeurons(String sourceId, String targetId, double weight) {
            AdvancedNeuron target = neurons.get(targetId);
            if (target != null) target.getWeights().put(sourceId, weight);
            AdvancedNeuron source = neurons.get(sourceId);
            if (source != null) source.getConnections().put(targetId, weight);
        }

        public List<String> createLayer(String layerName, int neuronCount, NeuronType type) {
            List<String> layerNeurons = new ArrayList<>();
            Random rnd = ThreadLocalRandom.current();
            for (int i = 0; i < neuronCount; i++) {
                String id = layerName + "_" + i;
                AdvancedNeuron neuron = new AdvancedNeuron(id, type,
                        rnd.nextDouble(0.3, 0.7),
                        rnd.nextDouble(0.001, 0.05),
                        1);
                addNeuron(neuron);
                layerNeurons.add(id);
            }
            layers.put(layerName, layerNeurons);
            return layerNeurons;
        }

        public Map<String, Double> forwardPass(Map<String, Double> inputs) {
            Map<String, Double> activations = new HashMap<>(inputs);
            Map<String, Double> current = new HashMap<>(inputs);

            for (Map.Entry<String, List<String>> entry : layers.entrySet()) {
                Map<String, Double> layerOutput = new HashMap<>();
                for (String neuronId : entry.getValue()) {
                    AdvancedNeuron neuron = neurons.get(neuronId);
                    if (neuron != null) {
                        layerOutput.put(neuronId, neuron.activate(current));
                    }
                }
                current = new HashMap<>(layerOutput);
                activations.putAll(layerOutput);
            }

            updateMetrics(activations);
            return activations;
        }

        private void updateMetrics(Map<String, Double> activations) {
            if (activations.isEmpty()) return;
            performanceMetrics.put("total_activations",
                    performanceMetrics.get("total_activations") + activations.size());
            double avg = activations.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            performanceMetrics.put("avg_activation", avg);

            if (activations.size() > 1) {
                double mean = avg;
                double variance = activations.values().stream()
                        .mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0);
                double std = Math.sqrt(variance);
                double coherence = 1.0 - std / (mean + 1e-6);
                performanceMetrics.put("network_coherence", Math.max(0, coherence));
            }
            lastUpdate = Instant.now();
        }

        public void learn(Map<String, Double> inputs, Map<String, Double> targets, double learningRate) {
            Map<String, Double> activations = forwardPass(inputs);

            // Calculate errors for output neurons
            Map<String, Double> errors = new HashMap<>();
            for (String neuronId : targets.keySet()) {
                if (activations.containsKey(neuronId)) {
                    errors.put(neuronId, targets.get(neuronId) - activations.get(neuronId));
                }
            }

            // Simple weight update (backpropagation approximation)
            for (Map.Entry<String, Double> errorEntry : errors.entrySet()) {
                AdvancedNeuron neuron = neurons.get(errorEntry.getKey());
                if (neuron == null) continue;
                double error = errorEntry.getValue();
                Map<String, Double> weights = neuron.getWeights();
                for (String inputId : weights.keySet()) {
                    if (activations.containsKey(inputId)) {
                        double delta = learningRate * error * activations.get(inputId);
                        weights.put(inputId, weights.get(inputId) + delta);
                    }
                }
                neuron.setBias(neuron.getBias() + learningRate * error);
            }

            // Update thresholds adaptively
            for (Map.Entry<String, Double> targetEntry : targets.entrySet()) {
                AdvancedNeuron neuron = neurons.get(targetEntry.getKey());
                if (neuron != null) neuron.updateThreshold(targetEntry.getValue());
            }
        }

        public Map<String, Double> getPerformanceMetrics() { return Collections.unmodifiableMap(performanceMetrics); }
        public String getNetworkId() { return networkId; }
    }

    // ======================== REAL-TIME PROCESSOR ========================
    public static class RealTimeProcessor {
        private final BlockingQueue<RealTimeEvent> eventQueue;
        private final Map<RealTimeEventType, List<Consumer<RealTimeEvent>>> subscribers = new ConcurrentHashMap<>();
        private volatile boolean processing = false;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final AtomicLong eventsProcessed = new AtomicLong(0);
        private final AtomicLong eventsQueued = new AtomicLong(0);
        private final AtomicDouble processingRate = new AtomicDouble(0);
        private Instant lastMetricsUpdate = Instant.now();

        public RealTimeProcessor(int maxQueueSize) {
            this.eventQueue = new LinkedBlockingQueue<>(maxQueueSize);
        }

        public void subscribe(RealTimeEventType type, Consumer<RealTimeEvent> callback) {
            subscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(callback);
        }

        public void publishEvent(RealTimeEvent event) {
            if (eventQueue.offer(event)) {
                eventsQueued.incrementAndGet();
            } else {
                Logger.getGlobal().warning("Event queue full, dropping event: " + event.getEventType());
            }
        }

        public void startProcessing() {
            if (processing) return;
            processing = true;
            executor.submit(this::processLoop);
        }

        public void stopProcessing() {
            processing = false;
            executor.shutdown();
        }

        private void processLoop() {
            while (processing || !eventQueue.isEmpty()) {
                try {
                    RealTimeEvent event = eventQueue.poll(1, TimeUnit.SECONDS);
                    if (event != null) {
                        List<Consumer<RealTimeEvent>> callbacks = subscribers.getOrDefault(event.getEventType(), List.of());
                        for (Consumer<RealTimeEvent> cb : callbacks) {
                            try { cb.accept(event); } catch (Exception e) {
                                Logger.getGlobal().severe("Subscriber error: " + e.getMessage());
                            }
                        }
                        event.markProcessed();
                        eventsProcessed.incrementAndGet();
                    }
                    updateMetrics();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private void updateMetrics() {
            Instant now = Instant.now();
            double elapsed = Duration.between(lastMetricsUpdate, now).toMillis() / 1000.0;
            if (elapsed > 0) {
                processingRate.set(eventsProcessed.get() / elapsed);
                lastMetricsUpdate = now;
            }
        }

        public Map<String, Object> getMetrics() {
            Map<String, Object> m = new HashMap<>();
            m.put("events_processed", eventsProcessed.get());
            m.put("events_queued", eventsQueued.get());
            m.put("processing_rate", processingRate.get());
            m.put("queue_size", eventQueue.size());
            return m;
        }
    }

    // ======================== ADVANCED PREDICTION SYSTEM ========================
    public static class AdvancedPredictionSystem {
        private final Map<PredictionModel, Object> models = new EnumMap<>(PredictionModel.class);
        private final Deque<Prediction> predictionHistory = new ConcurrentLinkedDeque<>();
        private final Map<String, Double> performanceMetrics = new ConcurrentHashMap<>();
        private final Map<PredictionModel, Double> modelWeights = new ConcurrentHashMap<>();

        public void addModel(PredictionModel type, Object model) {
            models.put(type, model);
            recalculateWeights();
        }

        private void recalculateWeights() {
            double weight = 1.0 / models.size();
            models.keySet().forEach(k -> modelWeights.put(k, weight));
        }

        public Prediction predict(String symbol, Map<String, Double> features, int horizon) {
            List<Prediction> predictions = new ArrayList<>();
            double totalWeight = 0.0;

            for (Map.Entry<PredictionModel, Object> entry : models.entrySet()) {
                try {
                    Prediction p = predictWithModel(entry.getValue(), entry.getKey(), features, horizon);
                    predictions.add(p);
                    totalWeight += modelWeights.getOrDefault(entry.getKey(), 0.0);
                } catch (Exception e) {
                    Logger.getGlobal().warning("Model " + entry.getKey() + " failed: " + e.getMessage());
                }
            }

            if (predictions.isEmpty()) throw new IllegalStateException("No models available");

            double ensemblePred = 0.0;
            double ensembleConf = 0.0;
            double totalW = 0.0;
            for (int i = 0; i < predictions.size(); i++) {
                Prediction p = predictions.get(i);
                double w = modelWeights.getOrDefault(p.modelType(), 0.0);
                ensemblePred += p.prediction() * w;
                ensembleConf += p.confidence() * w;
                totalW += w;
            }
            ensemblePred /= totalW;
            ensembleConf /= totalW;

            double uncertainty = predictions.stream().mapToDouble(Prediction::uncertainty).average().orElse(0.5);

            Prediction finalPred = new Prediction(symbol, ensemblePred, ensembleConf, uncertainty,
                    PredictionModel.ENSEMBLE, Instant.now(),
                    new ArrayList<>(features.keySet()),
                    Map.of("individual_predictions",
                            predictions.stream().map(Prediction::prediction).collect(Collectors.toList()),
                            "model_types", predictions.stream().map(Prediction::modelType).collect(Collectors.toList())));

            predictionHistory.addLast(finalPred);
            if (predictionHistory.size() > 10000) predictionHistory.pollFirst();
            return finalPred;
        }

        private Prediction predictWithModel(Object model, PredictionModel type, Map<String, Double> features, int horizon) {
            if (model instanceof AdvancedNeuralNetwork ann) {
                Map<String, Double> activations = ann.forwardPass(features);
                double pred = activations.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.5);
                double conf = ann.getPerformanceMetrics().getOrDefault("network_coherence", 0.5);
                if (Double.isNaN(conf)) conf = 0.5;
                return new Prediction("", pred, conf, 1.0 - conf, type, Instant.now(), new ArrayList<>(features.keySet()), Map.of());
            } else {
                // Simulated prediction for other types
                Random rnd = ThreadLocalRandom.current();
                return new Prediction("",
                        rnd.nextDouble() * 2 - 1,   // prediction between -1 and 1
                        rnd.nextDouble(0.3, 0.9),
                        rnd.nextDouble(0.1, 0.5),
                        type, Instant.now(),
                        new ArrayList<>(features.keySet()), Map.of());
            }
        }

        public void updateModelWeights(Map<PredictionModel, Double> performance) {
            double total = performance.values().stream().mapToDouble(Double::doubleValue).sum();
            if (total > 0) {
                performance.forEach((k, v) -> modelWeights.put(k, v / total));
            }
        }

        public Map<String, Double> getPerformanceMetrics() { return Collections.unmodifiableMap(performanceMetrics); }
    }

    // ======================== COGNITIVE ANALYZER ========================
    public static class CognitiveAnalyzer {
        private final Deque<CognitiveInsight> insightHistory = new ConcurrentLinkedDeque<>();
        private double learningRate = 0.01;

        public List<CognitiveInsight> analyzeMarketData(Map<String, Object> marketData) {
            List<CognitiveInsight> insights = new ArrayList<>();
            insights.addAll(analyzeTechnicalPatterns(marketData));
            insights.addAll(analyzeSentimentPatterns(marketData));
            insights.addAll(analyzeVolumePatterns(marketData));
            insights.addAll(analyzeVolatilityPatterns(marketData));
            insights.forEach(insightHistory::add);
            return insights;
        }

        private List<CognitiveInsight> analyzeTechnicalPatterns(Map<String, Object> data) {
            List<CognitiveInsight> insights = new ArrayList<>();
            if (data.containsKey("price") && data.containsKey("volume")) {
                List<Double> price = (List<Double>) data.get("price");
                if (price != null && price.size() > 20) {
                    double change = price.get(price.size() - 1) - price.get(0);
                    if (change > 0) {
                        insights.add(new CognitiveInsight(
                                CognitivePattern.TREND_FOLLOWING, 0.7, 0.6, Instant.now(),
                                Map.of("price_change", change), "BUY",
                                "Continuação da tendência de alta", "Curto prazo"));
                    }
                }
            }
            return insights;
        }

        private List<CognitiveInsight> analyzeSentimentPatterns(Map<String, Object> data) {
            List<CognitiveInsight> insights = new ArrayList<>();
            if (data.containsKey("news_sentiment")) {
                double sentiment = (double) data.get("news_sentiment");
                if (sentiment > 0.7) {
                    insights.add(new CognitiveInsight(
                            CognitivePattern.SENTIMENT_SHIFT, 0.8, 0.7, Instant.now(),
                            Map.of("sentiment_score", sentiment), "BUY",
                            "Movimento positivo impulsionado por sentimento", "Médio prazo"));
                }
            }
            return insights;
        }

        private List<CognitiveInsight> analyzeVolumePatterns(Map<String, Object> data) {
            List<CognitiveInsight> insights = new ArrayList<>();
            if (data.containsKey("volume")) {
                List<Double> volume = (List<Double>) data.get("volume");
                if (volume != null && volume.size() > 10) {
                    List<Double> recent = volume.subList(volume.size() - 10, volume.size());
                    double avg = recent.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    if (recent.get(recent.size() - 1) > avg * 2) {
                        insights.add(new CognitiveInsight(
                                CognitivePattern.BREAKOUT, 0.6, 0.5, Instant.now(),
                                Map.of("volume_spike", recent.get(recent.size() - 1) / avg),
                                "MONITOR", "Possível breakout", "Imediato"));
                    }
                }
            }
            return insights;
        }

        private List<CognitiveInsight> analyzeVolatilityPatterns(Map<String, Object> data) {
            List<CognitiveInsight> insights = new ArrayList<>();
            if (data.containsKey("price") && ((List<Double>) data.get("price")).size() > 20) {
                List<Double> price = (List<Double>) data.get("price");
                List<Double> returns = new ArrayList<>();
                for (int i = 1; i < price.size(); i++) returns.add(price.get(i) / price.get(i - 1) - 1);
                if (returns.size() > 10) {
                    double recentStd = stdDev(returns.subList(returns.size() - 10, returns.size()));
                    double histStd = stdDev(returns);
                    if (recentStd > histStd * 1.5) {
                        insights.add(new CognitiveInsight(
                                CognitivePattern.VOLATILITY_SPIKE, 0.7, 0.6, Instant.now(),
                                Map.of("volatility_ratio", recentStd / histStd),
                                "REDUCE_POSITION", "Aumento de volatilidade", "Curto prazo"));
                    }
                }
            }
            return insights;
        }

        private double stdDev(List<Double> values) {
            double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double variance = values.stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0);
            return Math.sqrt(variance);
        }
    }

    // ======================== MAIN (demonstração) ========================
    public static void main(String[] args) {
        System.out.println("=== Advanced AI Modules - Java Edition ===");

        // Example neural network
        AdvancedNeuralNetwork ann = new AdvancedNeuralNetwork("test_ann");
        ann.createLayer("input", 3, NeuronType.ATTENTION);
        ann.createLayer("hidden", 4, NeuronType.HEBOURNE);
        ann.createLayer("output", 1, NeuronType.SPIKING);

        Map<String, Double> inputs = Map.of("input_0", 0.5, "input_1", 0.3, "input_2", 0.8);
        Map<String, Double> out = ann.forwardPass(inputs);
        System.out.println("Forward pass: " + out);

        // Prediction system
        AdvancedPredictionSystem ps = new AdvancedPredictionSystem();
        ps.addModel(PredictionModel.NEURAL, ann);
        ps.addModel(PredictionModel.QUANTUM, "dummy");

        Prediction p = ps.predict("BTCUSDT", inputs, 1);
        System.out.println("Prediction: " + p);

        // Real-time processor
        RealTimeProcessor rtp = new RealTimeProcessor(100);
        rtp.subscribe(RealTimeEventType.MARKET_DATA, e -> System.out.println("Got market data: " + e.getData()));
        rtp.startProcessing();
        rtp.publishEvent(new RealTimeEvent(RealTimeEventType.MARKET_DATA, Map.of("price", 50000.0), 1, "test"));
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        rtp.stopProcessing();

        // Cognitive analyzer
        CognitiveAnalyzer ca = new CognitiveAnalyzer();
        Map<String, Object> market = Map.of(
                "price", List.of(100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120),
                "volume", List.of(10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,20),
                "news_sentiment", 0.8
        );
        List<CognitiveInsight> insights = ca.analyzeMarketData(market);
        insights.forEach(System.out::println);
    }
}
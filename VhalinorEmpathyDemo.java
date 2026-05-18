// ============================================================================
// VHALINOR IEA 1.0.0 - SISTEMA DE INTELIGÊNCIA EMOCIONAL ARTIFICIAL
// ============================================================================
// Módulo: Empatia Profunda e Conexão Emocional
// Versão: 1.0.0-IEA
// Autor: Alex Miranda Sales
// Data: 2025
//
// Conversão para Java realizada por IA
// ============================================================================

package com.vhalinor.iea.empathy;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.*;
import java.util.stream.Collectors;

// ========================= CONFIGURAÇÕES =========================

class EmpathyConfig {
    public static final String VERSION = "1.0.0-IEA";
    public static final String SYSTEM_NAME = "VHALINOR Artificial Emotional Intelligence";

    public static final double EMOTION_DETECTION_SENSITIVITY = 0.75;
    public static final int EMOTION_CONTEXT_WINDOW = 50;
    public static final double MIN_CONFIDENCE_THRESHOLD = 0.6;
    public static final double AMBIGUITY_THRESHOLD = 0.4;

    public static final int EMOTIONAL_MEMORY_CAPACITY = 10000;
    public static final double EMOTIONAL_DECAY_RATE = 0.95;
    public static final double IMPRINT_STRENGTH_FACTOR = 0.3;

    public static final double RESPONSE_INTENSITY_MODULATION = 0.8;
    public static final boolean MIRRORING_ENABLED = true;
    public static final double VALIDATION_PRIORITY = 0.7;

    public static final double LEARNING_RATE = 0.01;
    public static final double MASTERY_THRESHOLD = 0.85;
    public static final double PRACTICE_FREQUENCY = 0.3;

    public static final boolean ENABLE_ARCHIVING = true;
    public static final int ARCHIVE_INTERVAL = 3600;
    public static final int MAX_HISTORY_SIZE = 50000;

    public static final Path BASE_DIR = Paths.get(".");
    public static final Path EMOTIONAL_MEMORY_DIR = BASE_DIR.resolve("emotional_memory");
    public static final Path EMPATHY_MODELS_DIR = BASE_DIR.resolve("empathy_models");
}

// ========================= ENUMS =========================

enum EmotionalCategory {
    JOY("alegria"), SADNESS("tristeza"), ANGER("raiva"), FEAR("medo"),
    SURPRISE("surpresa"), DISGUST("nojo"), TRUST("confiança"), ANTICIPATION("antecipação"),
    LOVE("amor"), GRATITUDE("gratidão"), SHAME("vergonha"), GUILT("culpa"),
    ENVY("inveja"), PRIDE("orgulho"), HOPE("esperança"), DESPAIR("desespero"),
    NEUTRAL("neutro"), ANXIETY("ansiedade"), DISAPPOINTMENT("decepção"),
    RELIEF("alívio"), EXCITEMENT("excitação"), CONTENTMENT("contentamento"), STRESS("estresse");

    private final String value;
    EmotionalCategory(String value) { this.value = value; }
    public String getValue() { return value; }
    public static EmotionalCategory fromValue(String value) {
        for (EmotionalCategory e : values()) if (e.value.equals(value)) return e;
        return NEUTRAL;
    }
}

enum EmotionalIntensity {
    IMPERCEPTIBLE(0), MINIMAL(1), MILD(2), MODERATE(3), STRONG(4), INTENSE(5), OVERWHELMING(6);
    private final int level;
    EmotionalIntensity(int level) { this.level = level; }
    public int getLevel() { return level; }
}

enum EmpathyComponent {
    COGNITIVE, EMOTIONAL, COMPASSIONATE, SOMATIC, INTUITIVE, CULTURAL, TEMPORAL, TRANSPERSONAL;
}

enum ResponseStrategy {
    MIRRORING, VALIDATION, REFRAMING, EXPLORATION, SUPPORT, SOOTHING,
    CHALLENGING, NORMALIZING, PSYCHOEDUCATION, MINDFULNESS, PROBLEM_SOLVING, HUMOR, SPIRITUAL;
}

enum AttachmentStyle {
    SECURE, ANXIOUS, AVOIDANT, DISORGANIZED;
}

// ========================= EXCEÇÕES =========================

class EmpathySystemException extends Exception {
    public EmpathySystemException(String message) { super(message); }
    public EmpathySystemException(String message, Throwable cause) { super(message, cause); }
}

class EmotionDetectionException extends EmpathySystemException {
    public EmotionDetectionException(String message) { super(message); }
}

class MemorySystemException extends EmpathySystemException {
    public MemorySystemException(String message) { super(message); }
}

class ResponseGenerationException extends EmpathySystemException {
    public ResponseGenerationException(String message) { super(message); }
}

// ========================= INTERFACES =========================

interface EmotionDetector {
    Map<String, Object> detectEmotion(Map<String, Object> signals) throws EmotionDetectionException;
}

interface MemorySystem {
    String storeMemory(EmotionalMemory memory) throws MemorySystemException;
    List<EmotionalMemory> retrieveMemories(String userId, int limit);
}

interface ResponseGenerator {
    EmpatheticResponse generateResponse(Map<String, Object> emotionDetection, String userId, Map<String, Object> context)
            throws ResponseGenerationException;
}

// ========================= CLASSES DE DADOS =========================

record EmotionalSignature(
    String userId,
    Map<EmotionalCategory, Double> dominantEmotions,
    double emotionalVolatility,
    String expressionStyle,
    double typicalIntensity,
    Set<String> emotionalVocabulary,
    Map<String, Double> communicationPreferences,
    AttachmentStyle attachmentStyle,
    LocalDateTime lastInteraction,
    int interactionCount
) {
    public EmotionalSignature(String userId) {
        this(userId, new HashMap<>(), 0.5, "neutro", 0.5, new HashSet<>(), new HashMap<>(),
             AttachmentStyle.SECURE, LocalDateTime.now(), 0);
    }

    public EmotionalSignature update(EmotionalCategory emotion, double intensity, Map<String, Object> context) {
        Map<EmotionalCategory, Double> newDominant = new HashMap<>(dominantEmotions);
        newDominant.merge(emotion, intensity, (old, val) -> old * 0.7 + val * 0.3);
        double total = newDominant.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total > 0) newDominant.replaceAll((k, v) -> v / total);

        double newVolatility = emotionalVolatility * 0.9 + Math.abs(intensity - 0.5) * 0.1;
        Set<String> newVocab = new HashSet<>(emotionalVocabulary);
        if (context.containsKey("text")) {
            String text = (String) context.get("text");
            newVocab.addAll(Arrays.asList(text.toLowerCase().split("\\s+")));
        }
        return new EmotionalSignature(userId, newDominant, newVolatility, expressionStyle, typicalIntensity,
                                      newVocab, communicationPreferences, attachmentStyle,
                                      LocalDateTime.now(), interactionCount + 1);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("dominant_emotions", dominantEmotions.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getValue(), Map.Entry::getValue)));
        map.put("emotional_volatility", emotionalVolatility);
        map.put("expression_style", expressionStyle);
        map.put("typical_intensity", typicalIntensity);
        map.put("attachment_style", attachmentStyle.toString());
        map.put("interaction_count", interactionCount);
        map.put("last_interaction", lastInteraction.toString());
        return map;
    }
}

class EmotionalMemory {
    private String id;
    private final LocalDateTime timestamp;
    private final String userId;
    private final EmotionalCategory emotion;
    private final EmotionalIntensity intensity;
    private final Map<String, Object> context;
    private final String trigger;
    private final String meaning;
    private final String responseGiven;
    private double responseEffectiveness;
    private double imprintStrength;
    private final List<String> tags;

    // builder-like constructor (simplified)
    public EmotionalMemory(String id, LocalDateTime timestamp, String userId, EmotionalCategory emotion,
                           EmotionalIntensity intensity, Map<String, Object> context, String trigger,
                           String meaning, String responseGiven, double responseEffectiveness,
                           double imprintStrength, List<String> tags) {
        this.id = id;
        this.timestamp = timestamp;
        this.userId = userId;
        this.emotion = emotion;
        this.intensity = intensity;
        this.context = context;
        this.trigger = trigger;
        this.meaning = meaning;
        this.responseGiven = responseGiven;
        this.responseEffectiveness = responseEffectiveness;
        this.imprintStrength = imprintStrength;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public void strengthen(double factor) { imprintStrength = Math.min(1.0, imprintStrength + factor); }
    public void decay(double rate) { imprintStrength *= rate; }
    // getters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }
    public EmotionalCategory getEmotion() { return emotion; }
    public EmotionalIntensity getIntensity() { return intensity; }
    public Map<String, Object> getContext() { return context; }
    public String getTrigger() { return trigger; }
    public double getImprintStrength() { return imprintStrength; }
    public String getResponseGiven() { return responseGiven; }
}

class EmpatheticResponse {
    private final String id;
    private final LocalDateTime timestamp;
    private final String primaryText;
    private final ResponseStrategy strategy;
    private final double emotionalAlignment;
    private final double intensityModulation;
    private final List<String> secondaryResponses;
    private final Map<String, Double> nonverbalCues;
    private double expectedImpact;
    private Double actualImpact;
    private final List<String> alternatives;

    public EmpatheticResponse(String id, LocalDateTime timestamp, String primaryText, ResponseStrategy strategy,
                              double emotionalAlignment, double intensityModulation, List<String> secondaryResponses,
                              Map<String, Double> nonverbalCues, double expectedImpact, List<String> alternatives) {
        this.id = id;
        this.timestamp = timestamp;
        this.primaryText = primaryText;
        this.strategy = strategy;
        this.emotionalAlignment = emotionalAlignment;
        this.intensityModulation = intensityModulation;
        this.secondaryResponses = secondaryResponses;
        this.nonverbalCues = nonverbalCues;
        this.expectedImpact = expectedImpact;
        this.alternatives = alternatives;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("timestamp", timestamp.toString());
        map.put("primary_text", primaryText);
        map.put("strategy", strategy.toString());
        map.put("emotional_alignment", emotionalAlignment);
        map.put("intensity_modulation", intensityModulation);
        map.put("expected_impact", expectedImpact);
        return map;
    }

    // getters
    public String getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getPrimaryText() { return primaryText; }
    public ResponseStrategy getStrategy() { return strategy; }
    public double getEmotionalAlignment() { return emotionalAlignment; }
    public double getIntensityModulation() { return intensityModulation; }
    public List<String> getSecondaryResponses() { return secondaryResponses; }
    public Map<String, Double> getNonverbalCues() { return nonverbalCues; }
    public double getExpectedImpact() { return expectedImpact; }
    public void setExpectedImpact(double expectedImpact) { this.expectedImpact = expectedImpact; }
}

// ========================= REDES NEURAIS SIMPLIFICADAS =========================

class NeuralNetworkLayer {
    private final int inputSize, outputSize;
    private final String activation;
    private double[][] weights;
    private double[] bias;
    private double[] inputCache;
    private double[] outputCache;

    public NeuralNetworkLayer(int inputSize, int outputSize, String activation) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.activation = activation;
        Random rand = new Random();
        weights = new double[inputSize][outputSize];
        for (int i = 0; i < inputSize; i++)
            for (int j = 0; j < outputSize; j++)
                weights[i][j] = rand.nextGaussian() * Math.sqrt(2.0 / inputSize);
        bias = new double[outputSize];
    }

    public double[] forward(double[] x) {
        inputCache = x.clone();
        double[] z = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            double sum = bias[j];
            for (int i = 0; i < inputSize; i++) sum += x[i] * weights[i][j];
            z[j] = sum;
        }
        outputCache = activation(z);
        return outputCache;
    }

    private double[] activation(double[] z) {
        double[] out = new double[z.length];
        switch (activation) {
            case "relu":
                for (int i = 0; i < z.length; i++) out[i] = Math.max(0, z[i]);
                break;
            case "sigmoid":
                for (int i = 0; i < z.length; i++) out[i] = 1.0 / (1.0 + Math.exp(-z[i]));
                break;
            case "tanh":
                for (int i = 0; i < z.length; i++) out[i] = Math.tanh(z[i]);
                break;
            default:
                out = z.clone();
        }
        return out;
    }

    public double[] backward(double[] gradient, double learningRate) {
        double[] activationGrad = new double[outputCache.length];
        switch (activation) {
            case "relu":
                for (int i = 0; i < outputCache.length; i++) activationGrad[i] = outputCache[i] > 0 ? 1 : 0;
                break;
            case "sigmoid":
                for (int i = 0; i < outputCache.length; i++) activationGrad[i] = outputCache[i] * (1 - outputCache[i]);
                break;
            case "tanh":
                for (int i = 0; i < outputCache.length; i++) activationGrad[i] = 1 - outputCache[i] * outputCache[i];
                break;
            default:
                Arrays.fill(activationGrad, 1);
        }
        double[] localGrad = new double[gradient.length];
        for (int i = 0; i < gradient.length; i++) localGrad[i] = gradient[i] * activationGrad[i];

        double[] prevGrad = new double[inputSize];
        for (int i = 0; i < inputSize; i++) {
            double sum = 0;
            for (int j = 0; j < outputSize; j++) sum += localGrad[j] * weights[i][j];
            prevGrad[i] = sum;
        }

        for (int j = 0; j < outputSize; j++) {
            double wGrad = 0;
            for (int i = 0; i < inputSize; i++) wGrad += inputCache[i] * localGrad[j];
            double bGrad = localGrad[j];
            for (int i = 0; i < inputSize; i++) weights[i][j] -= learningRate * wGrad / inputSize;
            bias[j] -= learningRate * bGrad;
        }
        return prevGrad;
    }
}

class EmotionalNeuralNetwork {
    private final List<NeuralNetworkLayer> layers = new ArrayList<>();

    public EmotionalNeuralNetwork(int inputSize, List<Integer> hiddenSizes, int outputSize) {
        List<Integer> sizes = new ArrayList<>();
        sizes.add(inputSize);
        sizes.addAll(hiddenSizes);
        sizes.add(outputSize);
        for (int i = 0; i < sizes.size() - 1; i++) {
            String activation = (i < sizes.size() - 2) ? "relu" : "sigmoid";
            layers.add(new NeuralNetworkLayer(sizes.get(i), sizes.get(i+1), activation));
        }
    }

    public double[] forward(double[] x) {
        double[] current = x;
        for (NeuralNetworkLayer layer : layers) current = layer.forward(current);
        return current;
    }

    public void backward(double[] lossGradient, double learningRate) {
        double[] gradient = lossGradient;
        for (int i = layers.size()-1; i >= 0; i--) gradient = layers.get(i).backward(gradient, learningRate);
    }

    public void train(double[][] X, double[][] y, int epochs, double learningRate) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (int i = 0; i < X.length; i++) {
                double[] output = forward(X[i]);
                double loss = 0;
                for (int j = 0; j < output.length; j++) loss += Math.pow(output[j] - y[i][j], 2);
                totalLoss += loss / output.length;
                double[] lossGrad = new double[output.length];
                for (int j = 0; j < output.length; j++) lossGrad[j] = 2 * (output[j] - y[i][j]) / output.length;
                backward(lossGrad, learningRate);
            }
            if (epoch % 10 == 0)
                System.out.printf("Epoch %d, Loss: %.4f%n", epoch, totalLoss / X.length);
        }
    }
}

// ========================= REAL TIME UPDATE MANAGER =========================

class RealTimeUpdateManager {
    private final BlockingQueue<Map<String, Object>> updateQueue = new LinkedBlockingQueue<>();
    private final Map<String, List<Consumer<Map<String, Object>>>> subscribers = new ConcurrentHashMap<>();
    private volatile boolean running = false;
    private Thread updateThread;

    public void subscribe(String eventType, Consumer<Map<String, Object>> callback) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(callback);
    }

    public void publish(String eventType, Map<String, Object> data) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", eventType);
        event.put("data", data);
        event.put("timestamp", LocalDateTime.now());
        updateQueue.offer(event);
    }

    public void start() {
        if (running) return;
        running = true;
        updateThread = new Thread(this::processUpdates);
        updateThread.setDaemon(true);
        updateThread.start();
    }

    public void stop() {
        running = false;
        if (updateThread != null) updateThread.interrupt();
    }

    private void processUpdates() {
        while (running) {
            try {
                Map<String, Object> event = updateQueue.poll(100, TimeUnit.MILLISECONDS);
                if (event == null) continue;
                String type = (String) event.get("type");
                Map<String, Object> data = (Map<String, Object>) event.get("data");
                List<Consumer<Map<String, Object>>> callbacks = subscribers.get(type);
                if (callbacks != null) {
                    for (Consumer<Map<String, Object>> cb : callbacks) {
                        try { cb.accept(data); } catch (Exception e) { e.printStackTrace(); }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

// ========================= DETECTOR AVANÇADO DE EMOÇÕES =========================

class AmbiguityResolver {
    public Map<String, Object> resolve(Map<String, Object> detection, Map<String, Object> contextual, Map<String, Object> signals) {
        Map<EmotionalCategory, Double> candidates = new HashMap<>();
        if (detection.containsKey("all_scores")) {
            @SuppressWarnings("unchecked")
            Map<String, Double> scores = (Map<String, Double>) detection.get("all_scores");
            for (var entry : scores.entrySet()) {
                candidates.put(EmotionalCategory.fromValue(entry.getKey()), entry.getValue());
            }
        }
        if (contextual.containsKey("emotion")) {
            EmotionalCategory emo = (EmotionalCategory) contextual.get("emotion");
            double score = (double) contextual.getOrDefault("score", 0.6);
            candidates.merge(emo, score, Math::max);
        }
        List<Map.Entry<EmotionalCategory, Double>> sorted = new ArrayList<>(candidates.entrySet());
        sorted.sort((a,b) -> Double.compare(b.getValue(), a.getValue()));

        if (sorted.isEmpty()) {
            Map<String, Object> res = new HashMap<>();
            res.put("emotion", EmotionalCategory.NEUTRAL);
            res.put("score", 0.5);
            res.put("confidence", 0.5);
            res.put("ambiguity_score", 0);
            return res;
        }
        EmotionalCategory primary = sorted.get(0).getKey();
        double primaryScore = sorted.get(0).getValue();
        double ambiguity = 0;
        if (sorted.size() > 1) {
            EmotionalCategory secondary = sorted.get(1).getKey();
            // simplified compatibility
            boolean compatible = (primary == EmotionalCategory.JOY && secondary == EmotionalCategory.SURPRISE) ||
                                 (primary == EmotionalCategory.SADNESS && secondary == EmotionalCategory.DESPAIR);
            if (compatible) ambiguity = 0.3;
            else ambiguity = 0.5;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("emotion", primary);
        result.put("score", primaryScore);
        result.put("confidence", 0.7 * (1 - ambiguity));
        result.put("ambiguity_score", ambiguity);
        result.put("alternative_emotions", sorted.size() > 1 ? List.of(sorted.get(1).getKey()) : List.of());
        return result;
    }
}

class AdvancedEmotionDetector implements EmotionDetector {
    private final Map<String, Map<EmotionalCategory, Double>> emotionLexicon = new HashMap<>();
    private final Map<String, Map<String, Object>> culturalNuances = new HashMap<>();
    private final List<Map<String, Object>> contextualRules = new ArrayList<>();
    private final AmbiguityResolver ambiguityResolver = new AmbiguityResolver();
    private final EmotionalNeuralNetwork textNeuralNet;
    private final EmotionalNeuralNetwork voiceNeuralNet;
    private final Deque<Map<String, Object>> predictionHistory = new ArrayDeque<>();
    private double detectionAccuracy = 0.7;

    public AdvancedEmotionDetector() {
        buildEmotionLexicon();
        loadCulturalNuances();
        initializeContextualRules();
        // simplified: neural nets disabled (torch not available)
        this.textNeuralNet = null;
        this.voiceNeuralNet = null;
    }

    private void buildEmotionLexicon() {
        // Joy
        putWords(new String[]{"feliz","alegre","contente","animado","entusiasmado","radiante","satisfeito","realizado","encantado","eufórico","grato","agradecido","otimista"},
                 EmotionalCategory.JOY, new double[]{0.9,0.85,0.8,0.75,0.85,0.9,0.7,0.8,0.85,0.95,0.7,0.7,0.75});
        // Sadness
        putWords(new String[]{"triste","chateado","desanimado","deprimido","melancólico","desolado","infeliz","aborrecido","desapontado","saudoso","nostálgico"},
                 EmotionalCategory.SADNESS, new double[]{0.85,0.7,0.75,0.9,0.8,0.9,0.8,0.65,0.75,0.7,0.65});
        // Anger
        putWords(new String[]{"bravo","irritado","frustrado","revoltado","nervoso","indignado","furioso","hostil","ressentido","amargurado"},
                 EmotionalCategory.ANGER, new double[]{0.8,0.75,0.7,0.85,0.7,0.8,0.95,0.85,0.75,0.8});
        // Fear
        putWords(new String[]{"assustado","preocupado","ansioso","temeroso","apreensivo","aterrorizado","inseguro","paralisado","acuado"},
                 EmotionalCategory.FEAR, new double[]{0.85,0.7,0.75,0.8,0.7,0.95,0.7,0.85,0.8});
        // Surprise
        putWords(new String[]{"surpreso","espantado","admirado","assombrado","chocado","boquiaberto"},
                 EmotionalCategory.SURPRISE, new double[]{0.8,0.85,0.7,0.8,0.9,0.85});
        // Love
        putWords(new String[]{"amo","adoro","quero","gosto","aprecio","valorizo","estimo"},
                 EmotionalCategory.LOVE, new double[]{0.9,0.8,0.6,0.6,0.7,0.7,0.75});
    }

    private void putWords(String[] words, EmotionalCategory cat, double[] intensities) {
        for (int i=0; i<words.length; i++) {
            Map<EmotionalCategory, Double> map = new HashMap<>();
            map.put(cat, intensities[i]);
            emotionLexicon.put(words[i], map);
        }
    }

    private void loadCulturalNuances() {
        Map<String, Object> brasil = new HashMap<>();
        brasil.put("expressividade", 0.8); brasil.put("contato_fisico",0.7); brasil.put("hierarquia",0.4); brasil.put("coletivismo",0.7);
        culturalNuances.put("brasil", brasil);
        Map<String, Object> japao = new HashMap<>();
        japao.put("expressividade",0.3); japao.put("contato_fisico",0.2); japao.put("hierarquia",0.8); japao.put("coletivismo",0.8);
        culturalNuances.put("japao", japao);
        Map<String, Object> eua = new HashMap<>();
        eua.put("expressividade",0.7); eua.put("contato_fisico",0.6); eua.put("hierarquia",0.4); eua.put("coletivismo",0.5);
        culturalNuances.put("eua", eua);
    }

    private void initializeContextualRules() {
        Map<String, Object> rule1 = new HashMap<>();
        rule1.put("pattern", "perdeu|perdi|morreu|falecimento");
        rule1.put("emotion", EmotionalCategory.SADNESS);
        rule1.put("intensity_boost", 0.3);
        contextualRules.add(rule1);
        Map<String, Object> rule2 = new HashMap<>();
        rule2.put("pattern", "consegui|alcancei|realizei|venci");
        rule2.put("emotion", EmotionalCategory.JOY);
        rule2.put("intensity_boost", 0.2);
        contextualRules.add(rule2);
        Map<String, Object> rule3 = new HashMap<>();
        rule3.put("pattern", "injustiça|desrespeito|discriminação");
        rule3.put("emotion", EmotionalCategory.ANGER);
        rule3.put("intensity_boost", 0.4);
        contextualRules.add(rule3);
    }

    @Override
    public Map<String, Object> detectEmotion(Map<String, Object> signals) throws EmotionDetectionException {
        try {
            Map<String, Object> textAnalysis = analyzeText((String) signals.getOrDefault("text", ""));
            @SuppressWarnings("unchecked")
            Map<String, Object> voiceAnalysis = analyzeVoice((Map<String, Object>) signals.getOrDefault("voice", new HashMap<>()));
            @SuppressWarnings("unchecked")
            Map<String, Object> facialAnalysis = analyzeFacial((Map<String, Object>) signals.getOrDefault("facial", new HashMap<>()));
            @SuppressWarnings("unchecked")
            Map<String, Object> physiologicalAnalysis = analyzePhysiological((Map<String, Object>) signals.getOrDefault("physiological", new HashMap<>()));
            @SuppressWarnings("unchecked")
            Map<String, Object> behavioralAnalysis = analyzeBehavioral((Map<String, Object>) signals.getOrDefault("behavioral", new HashMap<>()));

            // fusion
            List<Map.Entry<Map<String, Object>, Double>> modalities = new ArrayList<>();
            modalities.add(Map.entry(textAnalysis, 0.3));
            modalities.add(Map.entry(voiceAnalysis, 0.2));
            modalities.add(Map.entry(facialAnalysis, 0.15));
            modalities.add(Map.entry(physiologicalAnalysis, 0.1));
            modalities.add(Map.entry(behavioralAnalysis, 0.05));
            Map<String, Object> fused = fuseMultimodalAnalysis(modalities);

            @SuppressWarnings("unchecked")
            Map<String, Object> context = (Map<String, Object>) signals.getOrDefault("context", new HashMap<>());
            Map<String, Object> contextualAdjusted = applyContextualRules(fused, (String) signals.getOrDefault("text",""), context);

            Map<String, Object> resolved;
            double ambiguity = (double) contextualAdjusted.getOrDefault("ambiguity_score",0.0);
            if (ambiguity > EmpathyConfig.AMBIGUITY_THRESHOLD) {
                resolved = ambiguityResolver.resolve(fused, contextualAdjusted, signals);
            } else {
                resolved = contextualAdjusted;
            }

            double confidence = calculateCalibratedConfidence(resolved, signals,
                    (int) signals.values().stream().filter(Objects::nonNull).count());
            EmotionalIntensity intensity = determineEmotionalIntensity(
                    (EmotionalCategory) resolved.get("emotion"),
                    (double) resolved.getOrDefault("score",0.5), signals);

            Map<String, Object> result = new HashMap<>();
            result.put("emotion", resolved.get("emotion"));
            result.put("intensity", intensity);
            result.put("intensity_value", intensity.getLevel() / 6.0);
            result.put("confidence", confidence);
            Map<String, Object> breakdown = new HashMap<>();
            breakdown.put("text", textAnalysis);
            breakdown.put("voice", voiceAnalysis);
            breakdown.put("facial", facialAnalysis);
            breakdown.put("physiological", physiologicalAnalysis);
            breakdown.put("behavioral", behavioralAnalysis);
            result.put("multimodal_breakdown", breakdown);
            result.put("ambiguity_score", ambiguity);
            result.put("contextual_factors", context);
            result.put("indicators", compileEmotionalIndicators(resolved, signals));
            result.put("timestamp", LocalDateTime.now());
            result.put("prediction_confidence", confidence);
            return result;
        } catch (Exception e) {
            throw new EmotionDetectionException("Erro na detecção emocional: " + e.getMessage());
        }
    }

    private Map<String, Object> analyzeText(String text) {
        Map<String, Object> res = new HashMap<>();
        if (text == null || text.isBlank()) {
            res.put("emotion", null); res.put("score", 0.0); res.put("confidence", 0.0);
            return res;
        }
        String lower = text.toLowerCase();
        String[] words = lower.split("\\s+");
        Map<EmotionalCategory, Double> scores = new HashMap<>();
        for (String w : words) {
            if (emotionLexicon.containsKey(w)) {
                for (var entry : emotionLexicon.get(w).entrySet()) {
                    scores.merge(entry.getKey(), entry.getValue(), Double::sum);
                }
            }
        }
        if (scores.isEmpty()) {
            res.put("emotion", null); res.put("score", 0.0); res.put("confidence", 0.0);
        } else {
            var maxEntry = scores.entrySet().stream().max(Map.Entry.comparingByValue()).get();
            double total = scores.values().stream().mapToDouble(Double::doubleValue).sum();
            res.put("emotion", maxEntry.getKey());
            res.put("score", maxEntry.getValue() / total);
            res.put("confidence", Math.min(0.9, maxEntry.getValue() / 5.0));
            Map<String, Double> allScores = new HashMap<>();
            scores.forEach((k,v) -> allScores.put(k.getValue(), v));
            res.put("all_scores", allScores);
        }
        return res;
    }

    private Map<String, Object> analyzeVoice(Map<String, Object> voiceData) {
        Map<String, Object> res = new HashMap<>();
        if (voiceData.isEmpty()) {
            res.put("emotion", null); res.put("score", 0.0); res.put("confidence", 0.0);
            return res;
        }
        double pitch = (double) voiceData.getOrDefault("pitch", 0.5);
        double tempo = (double) voiceData.getOrDefault("tempo", 0.5);
        double volume = (double) voiceData.getOrDefault("volume", 0.5);
        EmotionalCategory emotion = EmotionalCategory.NEUTRAL;
        double score = 0.5;
        if (volume > 0.7 && tempo > 0.7) { emotion = EmotionalCategory.ANGER; score = volume * tempo; }
        else if (pitch > 0.7 && tempo > 0.6) { emotion = EmotionalCategory.JOY; score = pitch * tempo; }
        else if (volume < 0.3 && tempo < 0.3) { emotion = EmotionalCategory.SADNESS; score = (1-volume)*(1-tempo); }
        else if (pitch > 0.8) { emotion = EmotionalCategory.SURPRISE; score = pitch * 0.8; }
        res.put("emotion", emotion);
        res.put("score", score);
        res.put("confidence", 0.7 * score);
        Map<String, Double> features = new HashMap<>();
        features.put("pitch", pitch); features.put("tempo", tempo); features.put("volume", volume);
        res.put("features", features);
        return res;
    }

    private Map<String, Object> analyzeFacial(Map<String, Object> facialData) {
        Map<String, Object> res = new HashMap<>();
        if (facialData.isEmpty()) {
            res.put("emotion", null); res.put("score", 0.0); res.put("confidence", 0.0);
            return res;
        }
        @SuppressWarnings("unchecked")
        Map<String, Double> aus = (Map<String, Double>) facialData.getOrDefault("action_units", new HashMap<>());
        Set<String> activeAus = aus.keySet();
        if (activeAus.containsAll(Set.of("6","12"))) {
            res.put("emotion", EmotionalCategory.JOY);
            res.put("score", 0.8);
            res.put("confidence", 0.9);
        } else if (activeAus.containsAll(Set.of("1","4","15"))) {
            res.put("emotion", EmotionalCategory.SADNESS);
            res.put("score", 0.8);
            res.put("confidence", 0.85);
        } else if (activeAus.containsAll(Set.of("4","5","7","23"))) {
            res.put("emotion", EmotionalCategory.ANGER);
            res.put("score", 0.8);
            res.put("confidence", 0.9);
        } else {
            res.put("emotion", null);
            res.put("score", 0.0);
            res.put("confidence", 0.3);
        }
        res.put("aus", new ArrayList<>(activeAus));
        return res;
    }

    private Map<String, Object> analyzePhysiological(Map<String, Object> physioData) {
        Map<String, Object> res = new HashMap<>();
        if (physioData.isEmpty()) {
            res.put("emotion", null); res.put("score", 0.0); res.put("confidence", 0.0);
            return res;
        }
        double hr = (double) physioData.getOrDefault("heart_rate", 70.0);
        double sc = (double) physioData.getOrDefault("skin_conductance", 0.5);
        EmotionalCategory emotion = EmotionalCategory.NEUTRAL;
        double score = 0.5;
        if (hr > 100 && sc > 0.7) { emotion = EmotionalCategory.FEAR; score = 0.8; }
        else if (hr > 90 && sc > 0.6) { emotion = EmotionalCategory.ANGER; score = 0.75; }
        else if (hr < 60 && sc < 0.3) { emotion = EmotionalCategory.SADNESS; score = 0.7; }
        res.put("emotion", emotion);
        res.put("score", score);
        res.put("confidence", 0.6);
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("hr", hr); metrics.put("sc", sc); metrics.put("rr", (double) physioData.getOrDefault("respiration_rate",16));
        res.put("metrics", metrics);
        return res;
    }

    private Map<String, Object> analyzeBehavioral(Map<String, Object> behavioralData) {
        Map<String, Object> res = new HashMap<>();
        if (behavioralData.isEmpty()) {
            res.put("emotion", null); res.put("score", 0.0); res.put("confidence", 0.0);
            return res;
        }
        double typing = (double) behavioralData.getOrDefault("typing_speed", 0.5);
        double mouse = (double) behavioralData.getOrDefault("mouse_movements", 0.5);
        double freq = (double) behavioralData.getOrDefault("interaction_frequency", 0.5);
        EmotionalCategory emotion = EmotionalCategory.NEUTRAL;
        double score = 0.5;
        if (typing > 0.8 && mouse > 0.7) emotion = EmotionalCategory.ANGER;
        else if (typing < 0.3 && freq < 0.3) emotion = EmotionalCategory.SADNESS;
        else if (typing > 0.7 && freq > 0.7) emotion = EmotionalCategory.JOY;
        res.put("emotion", emotion);
        res.put("score", score);
        res.put("confidence", 0.5);
        Map<String, Double> patterns = new HashMap<>();
        patterns.put("typing", typing); patterns.put("mouse", mouse); patterns.put("frequency", freq);
        res.put("patterns", patterns);
        return res;
    }

    private Map<String, Object> fuseMultimodalAnalysis(List<Map.Entry<Map<String, Object>, Double>> modalities) {
        Map<EmotionalCategory, Double> emotionScores = new HashMap<>();
        double totalWeight = 0;
        List<Double> confidences = new ArrayList<>();
        for (var entry : modalities) {
            Map<String, Object> analysis = entry.getKey();
            double weight = entry.getValue();
            if (analysis.containsKey("emotion") && analysis.get("emotion") != null) {
                EmotionalCategory emo = (EmotionalCategory) analysis.get("emotion");
                double score = (double) analysis.getOrDefault("score", 0.5);
                double conf = (double) analysis.getOrDefault("confidence", 0.5);
                emotionScores.merge(emo, score * weight * conf, Double::sum);
                totalWeight += weight;
                confidences.add(conf);
            }
        }
        if (emotionScores.isEmpty() || totalWeight == 0) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("emotion", EmotionalCategory.NEUTRAL);
            fallback.put("score", 0.5);
            fallback.put("confidence", 0.5);
            return fallback;
        }
        for (var key : emotionScores.keySet()) emotionScores.put(key, emotionScores.get(key) / totalWeight);
        var maxEntry = emotionScores.entrySet().stream().max(Map.Entry.comparingByValue()).get();
        double avgConf = confidences.stream().mapToDouble(Double::doubleValue).average().orElse(0.5);
        Map<String, Object> result = new HashMap<>();
        result.put("emotion", maxEntry.getKey());
        result.put("score", maxEntry.getValue());
        result.put("confidence", avgConf * 0.8);
        Map<String, Double> allScores = new HashMap<>();
        emotionScores.forEach((k,v) -> allScores.put(k.getValue(), v));
        result.put("all_scores", allScores);
        return result;
    }

    private Map<String, Object> applyContextualRules(Map<String, Object> detection, String text, Map<String, Object> context) {
        Map<String, Object> result = new HashMap<>(detection);
        double ambiguity = 0;
        for (Map<String, Object> rule : contextualRules) {
            String pattern = (String) rule.get("pattern");
            if (text != null && text.toLowerCase().matches(".*(" + pattern + ").*")) {
                EmotionalCategory ruleEmo = (EmotionalCategory) rule.get("emotion");
                if (!ruleEmo.equals(result.get("emotion"))) ambiguity += 0.3;
                double boost = (double) rule.getOrDefault("intensity_boost", 0.2);
                result.put("score", (double) result.getOrDefault("score",0.5) + boost);
            }
        }
        String culture = (String) context.getOrDefault("culture", "brasil");
        if (culturalNuances.containsKey(culture)) {
            Map<String, Object> nuances = culturalNuances.get(culture);
            if ((double) nuances.get("expressividade") < 0.5) {
                result.put("score", Math.min(1.0, (double) result.getOrDefault("score",0.5) * 1.2));
            }
        }
        result.put("ambiguity_score", ambiguity);
        return result;
    }

    private double calculateCalibratedConfidence(Map<String, Object> detection, Map<String, Object> signals, int modalitiesCount) {
        double base = (double) detection.getOrDefault("confidence", 0.5);
        double modalityBoost = Math.min(0.3, modalitiesCount * 0.1);
        double ambiguityPenalty = (double) detection.getOrDefault("ambiguity_score", 0.0) * 0.5;
        double consistency = 1.0; // simplified
        double confidence = (base + modalityBoost - ambiguityPenalty) * consistency;
        return Math.max(0.3, Math.min(0.95, confidence));
    }

    private EmotionalIntensity determineEmotionalIntensity(EmotionalCategory emotion, double score, Map<String, Object> signals) {
        double intensityValue = score * 6;
        String text = (String) signals.getOrDefault("text", "");
        if (text != null && text.contains("!")) intensityValue += 1;
        if (text != null && text.contains("!!!")) intensityValue += 2;
        @SuppressWarnings("unchecked")
        Map<String, Object> voice = (Map<String, Object>) signals.getOrDefault("voice", new HashMap<>());
        if ((double) voice.getOrDefault("volume", 0.5) > 0.8) intensityValue += 1;
        intensityValue = Math.max(0, Math.min(6, intensityValue));
        int level = (int) Math.round(intensityValue);
        for (EmotionalIntensity i : EmotionalIntensity.values()) if (i.getLevel() == level) return i;
        return EmotionalIntensity.MODERATE;
    }

    private List<Map<String, Object>> compileEmotionalIndicators(Map<String, Object> detection, Map<String, Object> signals) {
        List<Map<String, Object>> indicators = new ArrayList<>();
        String text = (String) signals.getOrDefault("text", "");
        if (text != null) {
            for (String word : text.toLowerCase().split("\\s+")) {
                if (emotionLexicon.containsKey(word)) {
                    Map<String, Object> ind = new HashMap<>();
                    ind.put("type", "keyword");
                    ind.put("value", word);
                    ind.put("emotion", emotionLexicon.get(word).keySet().iterator().next().getValue());
                    ind.put("strength", emotionLexicon.get(word).values().iterator().next());
                    indicators.add(ind);
                    if (indicators.size() >= 5) break;
                }
            }
        }
        return indicators;
    }
}

// ========================= SISTEMA DE MEMÓRIA EMOCIONAL =========================

class EmotionalMemorySystem implements MemorySystem {
    private final Deque<EmotionalMemory> memories = new ArrayDeque<>();
    private final Map<String, EmotionalSignature> emotionalSignatures = new ConcurrentHashMap<>();
    private final Map<String, List<String>> memoryIndex = new ConcurrentHashMap<>();
    private int totalMemories = 0;

    @Override
    public String storeMemory(EmotionalMemory memory) throws MemorySystemException {
        try {
            String id = generateMemoryId(memory);
            memory.setId(id);
            applyDecay();
            memories.addFirst(memory);
            totalMemories++;
            updateIndex(memory);
            EmotionalSignature signature = emotionalSignatures.get(memory.getUserId());
            if (signature == null) {
                signature = new EmotionalSignature(memory.getUserId());
                emotionalSignatures.put(memory.getUserId(), signature);
            }
            emotionalSignatures.put(memory.getUserId(),
                    signature.update(memory.getEmotion(), memory.getIntensity().getLevel() / 6.0, memory.getContext()));
            return id;
        } catch (Exception e) {
            throw new MemorySystemException("Erro ao armazenar memória: " + e.getMessage());
        }
    }

    private String generateMemoryId(EmotionalMemory memory) {
        String content = memory.getUserId() + "_" + memory.getTimestamp() + "_" + memory.getEmotion().getValue() + "_" + memory.getTrigger();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(content.getBytes());
            return bytesToHex(hash).substring(0, 16);
        } catch (NoSuchAlgorithmException e) { return UUID.randomUUID().toString(); }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private void updateIndex(EmotionalMemory memory) {
        memoryIndex.computeIfAbsent(memory.getUserId(), k -> new ArrayList<>()).add(memory.getId());
        String emotionKey = "emotion_" + memory.getEmotion().getValue();
        memoryIndex.computeIfAbsent(emotionKey, k -> new ArrayList<>()).add(memory.getId());
    }

    private void applyDecay() {
        Iterator<EmotionalMemory> it = memories.iterator();
        while (it.hasNext()) {
            EmotionalMemory m = it.next();
            m.decay(EmpathyConfig.EMOTIONAL_DECAY_RATE);
            if (m.getImprintStrength() < 0.1) it.remove();
        }
    }

    @Override
    public List<EmotionalMemory> retrieveMemories(String userId, int limit) {
        return memories.stream()
                .filter(m -> m.getUserId().equals(userId) && m.getImprintStrength() > 0.3)
                .sorted((a,b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<EmotionalMemory> retrieveByEmotion(EmotionalCategory emotion, int limit) {
        return memories.stream()
                .filter(m -> m.getEmotion() == emotion && m.getImprintStrength() > 0.3)
                .sorted((a,b) -> Double.compare(b.getImprintStrength(), a.getImprintStrength()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<EmotionalMemory> retrieveSimilarContexts(Map<String, Object> context, int limit) {
        String situation = (String) context.getOrDefault("situation", "");
        Set<String> situationWords = new HashSet<>(Arrays.asList(situation.toLowerCase().split("\\s+")));
        List<Map.Entry<EmotionalMemory, Double>> scored = new ArrayList<>();
        for (EmotionalMemory m : memories) {
            if (m.getImprintStrength() < 0.3) continue;
            String memSit = (String) m.getContext().getOrDefault("situation", "");
            Set<String> memWords = new HashSet<>(Arrays.asList(memSit.toLowerCase().split("\\s+")));
            if (!situationWords.isEmpty() && !memWords.isEmpty()) {
                long inter = situationWords.stream().filter(memWords::contains).count();
                long union = situationWords.size() + memWords.size() - inter;
                double sim = (double) inter / union;
                if (sim > 0.3) scored.add(Map.entry(m, sim));
            }
        }
        scored.sort((a,b) -> Double.compare(b.getValue(), a.getValue()));
        return scored.stream().limit(limit).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public EmotionalSignature getEmotionalSignature(String userId) {
        return emotionalSignatures.get(userId);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_memories", totalMemories);
        stats.put("active_memories", memories.size());
        stats.put("active_signatures", emotionalSignatures.size());
        Map<EmotionalCategory, Integer> counts = new HashMap<>();
        for (EmotionalMemory m : memories) counts.merge(m.getEmotion(), 1, Integer::sum);
        stats.put("memory_by_emotion", counts);
        double avgIntensity = memories.stream().mapToInt(m -> m.getIntensity().getLevel()).average().orElse(0) / 6.0;
        stats.put("average_intensity", avgIntensity);
        return stats;
    }
}

// ========================= GERADOR DE RESPOSTAS EMPÁTICAS =========================

class StrategySelector {
    public ResponseStrategy selectStrategy(EmotionalCategory emotion, double intensity, Map<String, Object> context, EmotionalSignature signature) {
        List<ResponseStrategy> candidates = switch (emotion) {
            case JOY -> List.of(ResponseStrategy.MIRRORING, ResponseStrategy.VALIDATION);
            case SADNESS -> List.of(ResponseStrategy.VALIDATION, ResponseStrategy.SUPPORT, ResponseStrategy.SOOTHING, ResponseStrategy.NORMALIZING);
            case ANGER -> List.of(ResponseStrategy.VALIDATION, ResponseStrategy.SOOTHING, ResponseStrategy.REFRAMING);
            case FEAR -> List.of(ResponseStrategy.VALIDATION, ResponseStrategy.SUPPORT, ResponseStrategy.SOOTHING, ResponseStrategy.NORMALIZING);
            case SURPRISE -> List.of(ResponseStrategy.MIRRORING, ResponseStrategy.EXPLORATION);
            default -> List.of(ResponseStrategy.VALIDATION, ResponseStrategy.EXPLORATION);
        };
        if (intensity < 0.3) candidates = candidates.stream().filter(s -> s != ResponseStrategy.SOOTHING && s != ResponseStrategy.SUPPORT).collect(Collectors.toList());
        if (intensity > 0.7) candidates = candidates.stream().filter(s -> s != ResponseStrategy.REFRAMING).collect(Collectors.toList());
        String situation = (String) context.getOrDefault("situation", "");
        if (situation.contains("crise") || situation.contains("emergência"))
            candidates = List.of(ResponseStrategy.SUPPORT, ResponseStrategy.SOOTHING);
        if (signature != null && signature.attachmentStyle() == AttachmentStyle.ANXIOUS) {
            candidates = candidates.stream().filter(s -> s != ResponseStrategy.CHALLENGING).collect(Collectors.toList());
            if (!candidates.contains(ResponseStrategy.SUPPORT)) candidates.add(ResponseStrategy.SUPPORT);
        }
        return candidates.isEmpty() ? ResponseStrategy.VALIDATION : candidates.get(0);
    }
}

class PersonalizationEngine {
    public List<String> personalizeTemplates(List<String> templates, EmotionalSignature signature, Map<String, Object> context) {
        if (signature == null) return templates;
        List<String> personalized = new ArrayList<>();
        for (String t : templates) {
            String text = t;
            if (signature.attachmentStyle() == AttachmentStyle.ANXIOUS) {
                text = text.replace("Estou aqui", "Estou aqui e não vou embora");
                text = text.replace("com você", "com você, sempre");
            } else if (signature.attachmentStyle() == AttachmentStyle.AVOIDANT) {
                text = text.replace("com você", "ao seu lado");
                text = text.replace("juntos", "cada um no seu tempo");
            }
            personalized.add(text);
        }
        return personalized;
    }
}

class EmpatheticResponseGenerator implements ResponseGenerator {
    private final Map<ResponseStrategy, Map<String, List<String>>> responseTemplates = new HashMap<>();
    private final StrategySelector strategySelector = new StrategySelector();
    private final PersonalizationEngine personalizationEngine = new PersonalizationEngine();

    public EmpatheticResponseGenerator(EmotionalMemorySystem memorySystem) {
        initializeTemplates();
    }

    private void initializeTemplates() {
        // MIRRORING
        Map<String, List<String>> mirroring = new HashMap<>();
        mirroring.put("joy", List.of("Percebo sua alegria e me alegro com você!","Que bom ver você tão feliz!","Sua felicidade é contagiante!"));
        mirroring.put("sadness", List.of("Percebo sua tristeza e estou aqui com você.","Sinto essa dor junto com você.","Sua tristeza ressoa em mim também."));
        mirroring.put("anger", List.of("Percebo sua raiva, isso deve ser muito frustrante.","Sinto essa indignação, é completamente compreensível.","Sua raiva faz sentido diante disso."));
        mirroring.put("fear", List.of("Percebo seu medo, isso deve ser assustador.","Sinto essa apreensão com você.","Seu medo é válido e estou aqui."));
        mirroring.put("default", List.of("Percebo o que você está sentindo.","Sinto essa emoção junto com você.","Estou sintonizado com seu sentimento."));
        responseTemplates.put(ResponseStrategy.MIRRORING, mirroring);
        // VALIDATION
        Map<String, List<String>> validation = new HashMap<>();
        validation.put("joy", List.of("É maravilhoso que você esteja se sentindo assim!","Você merece toda essa felicidade.","Que bom que você está experimentando isso!"));
        validation.put("sadness", List.of("É completamente válido se sentir assim.","Sua tristeza é legítima e compreensível.","Você tem todo o direito de estar triste."));
        validation.put("anger", List.of("Sua raiva é completamente justificada.","Você tem todo o direito de estar irritado.","Essa indignação é totalmente apropriada."));
        validation.put("fear", List.of("Seu medo é válido e compreensível.","É normal se sentir assim nesta situação.","Sua preocupação faz todo sentido."));
        validation.put("default", List.of("Seu sentimento é completamente válido.","O que você sente é importante e legítimo.","Suas emoções são sempre válidas."));
        responseTemplates.put(ResponseStrategy.VALIDATION, validation);
        // SUPPORT
        Map<String, List<String>> support = new HashMap<>();
        support.put("sadness", List.of("Estou aqui para o que você precisar.","Como posso ajudar você agora?","Conte comigo para o que for preciso."));
        support.put("fear", List.of("Vamos enfrentar isso juntos.","Você não está sozinho nessa.","Estou ao seu lado para te apoiar."));
        support.put("default", List.of("Estou aqui para você.","Como posso ser útil agora?","Conte com meu apoio."));
        responseTemplates.put(ResponseStrategy.SUPPORT, support);
        // SOOTHING
        Map<String, List<String>> soothing = new HashMap<>();
        soothing.put("sadness", List.of("Respire fundo, estou com você.","Tudo bem não estar bem.","Vamos um passo de cada vez."));
        soothing.put("fear", List.of("Você está seguro agora.","Tente respirar calmamente comigo.","Está tudo bem, você está seguro."));
        soothing.put("anger", List.of("Vamos respirar um momento.","Tudo bem, estou aqui com você.","Podemos processar isso com calma."));
        soothing.put("default", List.of("Respire comigo, está tudo bem.","Encontre seu ritmo, sem pressa.","Tudo bem, estamos juntos nisso."));
        responseTemplates.put(ResponseStrategy.SOOTHING, soothing);
        // REFRAMING
        Map<String, List<String>> reframing = new HashMap<>();
        reframing.put("sadness", List.of("Talvez essa dor também traga aprendizado.","Essa experiência, por mais difícil, pode fortalecer você.","Há beleza mesmo nos momentos difíceis."));
        reframing.put("anger", List.of("Essa energia poderia ser canalizada para mudanças positivas.","Sua indignação mostra o quanto você se importa.","Essa força pode ser usada construtivamente."));
        reframing.put("fear", List.of("O medo muitas vezes mostra o que é importante para nós.","Essa apreensão demonstra seu cuidado e atenção.","O medo pode ser um guia para o que valorizamos."));
        reframing.put("default", List.of("Talvez possamos ver isso de outra perspectiva.","Existem diferentes formas de interpretar isso.","Que tal olharmos por outro ângulo?"));
        responseTemplates.put(ResponseStrategy.REFRAMING, reframing);
        // EXPLORATION
        Map<String, List<String>> exploration = new HashMap<>();
        exploration.put("default", List.of("Poderia me falar mais sobre como está se sentindo?","O que mais está surgindo em você agora?","Como isso começou a se manifestar?"));
        responseTemplates.put(ResponseStrategy.EXPLORATION, exploration);
        // NORMALIZING
        Map<String, List<String>> normalizing = new HashMap<>();
        normalizing.put("sadness", List.of("É completamente normal sentir tristeza em momentos assim.","Muitas pessoas se sentiriam da mesma forma.","Isso é uma reação humana completamente natural."));
        normalizing.put("fear", List.of("O medo é uma resposta humana natural ao desconhecido.","Qualquer um se sentiria assim nessa situação.","É perfeitamente normal estar apreensivo."));
        normalizing.put("anger", List.of("A raiva é uma emoção humana legítima e necessária.","Todos nós sentimos raiva em situações de injustiça.","Sua reação é completamente humana e normal."));
        normalizing.put("default", List.of("O que você sente é uma experiência humana universal.","Essa emoção faz parte da experiência humana.","É natural e normal sentir isso."));
        responseTemplates.put(ResponseStrategy.NORMALIZING, normalizing);
    }

    @Override
    public EmpatheticResponse generateResponse(Map<String, Object> emotionDetection, String userId, Map<String, Object> context)
            throws ResponseGenerationException {
        try {
            EmotionalCategory emotion = (EmotionalCategory) emotionDetection.get("emotion");
            double intensity = (double) emotionDetection.getOrDefault("intensity_value", 0.5);
            double confidence = (double) emotionDetection.getOrDefault("confidence", 0.5);

            ResponseStrategy strategy = strategySelector.selectStrategy(emotion, intensity, context, null);
            Map<String, List<String>> templatesByEmo = responseTemplates.get(strategy);
            String emotionKey = emotion.getValue();
            List<String> templates = templatesByEmo.getOrDefault(emotionKey, templatesByEmo.get("default"));
            if (templates == null) templates = List.of("Entendo como você se sente.");

            List<String> personalized = personalizationEngine.personalizeTemplates(templates, null, context);
            String primary = selectOptimalResponse(personalized, strategy, emotion, intensity);
            List<String> secondary = generateSecondaryResponses(strategy, emotion, intensity, personalized);
            Map<String, Double> nonverbal = generateNonverbalCues(emotion, intensity, strategy);
            double alignment = calculateEmotionalAlignment(emotion, intensity, strategy, null);
            double mod = modulateIntensity(intensity, strategy, null);
            double expectedImpact = calculateExpectedImpact(emotion, strategy, confidence);
            List<String> alternatives = new ArrayList<>(personalized.size() > 1 ? personalized.subList(1, Math.min(personalized.size(), 3)) : List.of());

            String id = "RESP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0,8);
            return new EmpatheticResponse(id, LocalDateTime.now(), primary, strategy, alignment, mod,
                                          secondary, nonverbal, expectedImpact, alternatives);
        } catch (Exception e) {
            throw new ResponseGenerationException("Erro ao gerar resposta: " + e.getMessage());
        }
    }

    private String selectOptimalResponse(List<String> responses, ResponseStrategy strategy, EmotionalCategory emotion, double intensity) {
        if (responses.isEmpty()) return "Estou aqui com você.";
        double bestScore = -1;
        int bestIdx = 0;
        for (int i = 0; i < responses.size(); i++) {
            String r = responses.get(i);
            double score = 0;
            int len = r.length();
            if (len >= 20 && len <= 60) score += 0.3;
            else if (len < 20) score += 0.1;
            else if (len > 100) score -= 0.2;
            String lower = r.toLowerCase();
            if (lower.contains("você") || lower.contains("com") || lower.contains("sinto")) score += 0.1;
            if (intensity > 0.7 && !lower.contains("!")) score -= 0.1;
            if (intensity < 0.3 && lower.contains("!")) score -= 0.1;
            if (score > bestScore) { bestScore = score; bestIdx = i; }
        }
        return responses.get(bestIdx);
    }

    private List<String> generateSecondaryResponses(ResponseStrategy strategy, EmotionalCategory emotion, double intensity, List<String> available) {
        List<String> secondary = new ArrayList<>();
        if (strategy != ResponseStrategy.EXPLORATION) {
            List<String> expTemplates = responseTemplates.getOrDefault(ResponseStrategy.EXPLORATION, new HashMap<>())
                    .getOrDefault("default", List.of("Poderia me falar mais sobre como está se sentindo?"));
            if (!expTemplates.isEmpty()) secondary.add(expTemplates.get(0));
        }
        if (strategy != ResponseStrategy.SUPPORT && intensity > 0.6) {
            List<String> supTemplates = responseTemplates.getOrDefault(ResponseStrategy.SUPPORT, new HashMap<>())
                    .getOrDefault("default", List.of("Estou aqui para você."));
            if (!supTemplates.isEmpty()) secondary.add(supTemplates.get(0));
        }
        return secondary.size() > 2 ? secondary.subList(0,2) : secondary;
    }

    private Map<String, Double> generateNonverbalCues(EmotionalCategory emotion, double intensity, ResponseStrategy strategy) {
        Map<String, Double> cues = new HashMap<>();
        cues.put("warmth", 0.5);
        cues.put("attentiveness", 0.7);
        cues.put("concern", 0.3);
        cues.put("calmness", 0.6);
        switch (emotion) {
            case JOY -> { cues.put("warmth", 0.8); cues.put("calmness", 0.4); }
            case SADNESS -> { cues.put("warmth", 0.7); cues.put("concern", 0.8); cues.put("calmness", 0.7); }
            case ANGER -> { cues.put("warmth", 0.4); cues.put("attentiveness", 0.9); cues.put("calmness", 0.8); }
            case FEAR -> { cues.put("warmth", 0.6); cues.put("concern", 0.9); cues.put("calmness", 0.9); }
        }
        if (strategy == ResponseStrategy.SOOTHING) cues.put("calmness", Math.min(1.0, cues.get("calmness") * 1.3));
        if (strategy == ResponseStrategy.SUPPORT) cues.put("warmth", Math.min(1.0, cues.get("warmth") * 1.2));
        for (String key : cues.keySet()) cues.put(key, Math.min(1.0, cues.get(key) * (0.8 + intensity * 0.2)));
        return cues;
    }

    private double calculateEmotionalAlignment(EmotionalCategory emotion, double intensity, ResponseStrategy strategy, EmotionalSignature signature) {
        double alignment = 0.7;
        Map<ResponseStrategy, Double> strategyAlign = Map.of(
                ResponseStrategy.MIRRORING, 0.9, ResponseStrategy.VALIDATION, 0.85,
                ResponseStrategy.SOOTHING, 0.8, ResponseStrategy.SUPPORT, 0.8,
                ResponseStrategy.REFRAMING, 0.7, ResponseStrategy.EXPLORATION, 0.75);
        alignment *= strategyAlign.getOrDefault(strategy, 0.7);
        alignment *= (0.8 + intensity * 0.2);
        if (signature != null && "expressivo".equals(signature.expressionStyle())) alignment *= 1.1;
        else if (signature != null && "reservado".equals(signature.expressionStyle())) alignment *= 0.9;
        return Math.min(1.0, Math.max(0.5, alignment));
    }

    private double modulateIntensity(double original, ResponseStrategy strategy, EmotionalSignature signature) {
        double mod = original * EmpathyConfig.RESPONSE_INTENSITY_MODULATION;
        Map<ResponseStrategy, Double> strategyIntensity = Map.of(
                ResponseStrategy.MIRRORING, 0.9, ResponseStrategy.VALIDATION, 0.8,
                ResponseStrategy.SOOTHING, 0.6, ResponseStrategy.SUPPORT, 0.7,
                ResponseStrategy.REFRAMING, 0.5, ResponseStrategy.EXPLORATION, 0.5);
        mod *= strategyIntensity.getOrDefault(strategy, 0.7);
        if (signature != null) mod *= signature.emotionalVolatility();
        return Math.min(1.0, Math.max(0.3, mod));
    }

    private double calculateExpectedImpact(EmotionalCategory emotion, ResponseStrategy strategy, double confidence) {
        double base = 0.6;
        Map<ResponseStrategy, Double> impact = Map.of(
                ResponseStrategy.VALIDATION, 0.8, ResponseStrategy.MIRRORING, 0.75,
                ResponseStrategy.SUPPORT, 0.7, ResponseStrategy.SOOTHING, 0.65,
                ResponseStrategy.NORMALIZING, 0.7, ResponseStrategy.REFRAMING, 0.6, ResponseStrategy.EXPLORATION, 0.55);
        double result = base * impact.getOrDefault(strategy, 0.6) * confidence;
        return Math.min(0.95, Math.max(0.4, result));
    }
}

// ========================= SISTEMA PRINCIPAL VHALINOR =========================

class VhalinorEmpathySystem {
    private final EmpathyConfig config;
    private final AdvancedEmotionDetector detector;
    private final EmotionalMemorySystem memorySystem;
    private final EmpatheticResponseGenerator responseGenerator;
    private double empathyLevel = 0.7;
    private final Map<EmpathyComponent, Map<String, Object>> empathyComponents;
    private final Deque<Map<String, Object>> interactionHistory = new ArrayDeque<>();
    private final Map<String, Object> developmentMetrics = new ConcurrentHashMap<>();
    private volatile boolean isLearning = true;
    private final RealTimeUpdateManager realtimeManager = new RealTimeUpdateManager();
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("VHALINOR.Empathy");

    public VhalinorEmpathySystem() {
        this.config = new EmpathyConfig();
        this.detector = new AdvancedEmotionDetector();
        this.memorySystem = new EmotionalMemorySystem();
        this.responseGenerator = new EmpatheticResponseGenerator(memorySystem);
        this.empathyComponents = initializeEmpathyComponents();
        developmentMetrics.put("total_interactions", 0);
        developmentMetrics.put("successful_responses", 0);
        developmentMetrics.put("avg_response_effectiveness", 0.0);
        developmentMetrics.put("learning_progress", 0.0);
        developmentMetrics.put("emotional_vocabulary_size", 0); // would need lexicon size
        realtimeManager.start();
        setupRealtimeEvents();
        logger.info("🧠 Sistema de Empatia VHALINOR-IEA 2.0 inicializado");
    }

    private Map<EmpathyComponent, Map<String, Object>> initializeEmpathyComponents() {
        Map<EmpathyComponent, Map<String, Object>> comps = new EnumMap<>(EmpathyComponent.class);
        for (EmpathyComponent c : EmpathyComponent.values()) {
            Map<String, Object> data = new HashMap<>();
            data.put("level", 0.5);
            data.put("practices", 0);
            data.put("mastery", 0.0);
            comps.put(c, data);
        }
        comps.get(EmpathyComponent.COGNITIVE).put("level", 0.7);
        comps.get(EmpathyComponent.EMOTIONAL).put("level", 0.8);
        comps.get(EmpathyComponent.COMPASSIONATE).put("level", 0.6);
        comps.get(EmpathyComponent.INTUITIVE).put("level", 0.6);
        return comps;
    }

    private void setupRealtimeEvents() {
        realtimeManager.subscribe("new_interaction", data -> {
            developmentMetrics.put("total_interactions", (int)developmentMetrics.get("total_interactions") + 1);
        });
    }

    public CompletableFuture<Map<String, Object>> processEmotionalSignal(Map<String, Object> signals, String userId, Map<String, Object> context) {
        return CompletableFuture.supplyAsync(() -> {
            long start = System.nanoTime();
            if (context == null) context = new HashMap<>();
            try {
                Map<String, Object> emotionDetection = detector.detectEmotion(signals);
                // simplified cognitive analysis placeholder
                Map<String, Object> cognitiveAnalysis = Map.of("insights", List.of());
                EmotionalSignature signature = memorySystem.getEmotionalSignature(userId);
                List<EmotionalMemory> relevantMemories = memorySystem.retrieveMemories(userId, 5);
                List<EmotionalMemory> similarContexts = memorySystem.retrieveSimilarContexts(context, 3);

                EmpatheticResponse response = responseGenerator.generateResponse(emotionDetection, userId, context);

                EmotionalMemory memory = new EmotionalMemory("", LocalDateTime.now(), userId,
                        (EmotionalCategory) emotionDetection.get("emotion"),
                        (EmotionalIntensity) emotionDetection.get("intensity"),
                        new HashMap<>(signals), (String) context.get("trigger"), null,
                        response.getPrimaryText(), 0.5, 0.7, (List<String>) context.getOrDefault("tags", new ArrayList<>()));
                String memoryId = memorySystem.storeMemory(memory);

                Map<String, Object> interaction = new HashMap<>();
                interaction.put("id", "INT-" + System.currentTimeMillis());
                interaction.put("timestamp", LocalDateTime.now());
                interaction.put("user_id", userId);
                interaction.put("emotion_detection", Map.of(
                        "emotion", ((EmotionalCategory) emotionDetection.get("emotion")).getValue(),
                        "intensity", emotionDetection.get("intensity_value"),
                        "confidence", emotionDetection.get("confidence")
                ));
                interaction.put("response", response.toMap());
                interaction.put("memory_id", memoryId);
                interaction.put("processing_time", (System.nanoTime() - start) / 1e9);
                interactionHistory.add(interaction);

                realtimeManager.publish("new_interaction", Map.of("user_id", userId, "emotion", emotionDetection.get("emotion")));

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                Map<String, Object> emotionalAnalysis = new HashMap<>();
                emotionalAnalysis.put("detected_emotion", ((EmotionalCategory) emotionDetection.get("emotion")).getValue());
                emotionalAnalysis.put("intensity", emotionDetection.get("intensity_value"));
                emotionalAnalysis.put("confidence", emotionDetection.get("confidence"));
                result.put("emotional_analysis", emotionalAnalysis);
                Map<String, Object> empatheticResponse = new HashMap<>();
                empatheticResponse.put("text", response.getPrimaryText());
                empatheticResponse.put("strategy", response.getStrategy().toString());
                empatheticResponse.put("emotional_alignment", response.getEmotionalAlignment());
                empatheticResponse.put("intensity", response.getIntensityModulation());
                empatheticResponse.put("secondary_responses", response.getSecondaryResponses());
                empatheticResponse.put("nonverbal_cues", response.getNonverbalCues());
                result.put("empathetic_response", empatheticResponse);
                result.put("context_utilization", Map.of("relevant_memories", relevantMemories.size(), "similar_contexts", similarContexts.size()));
                result.put("realtime_updates", Map.of("processing_time", interaction.get("processing_time"), "interaction_id", interaction.get("id")));
                result.put("metadata", Map.of("version", "2.0", "neural_enabled", false, "realtime_enabled", true));
                return result;
            } catch (Exception e) {
                logger.severe("Erro no processamento emocional: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Map<String, Object>> developComponent(EmpathyComponent component, double intensity) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> compData = empathyComponents.get(component);
            double prevLevel = (double) compData.get("level");
            double newLevel = Math.min(1.0, prevLevel + intensity);
            compData.put("level", newLevel);
            compData.put("practices", (int) compData.get("practices") + 1);
            compData.put("mastery", (int) compData.get("practices") * 0.1 * newLevel);
            updateEmpathyLevel();
            Map<String, Object> result = new HashMap<>();
            result.put("component", component.toString());
            result.put("previous_level", prevLevel);
            result.put("current_level", newLevel);
            result.put("increase", newLevel - prevLevel);
            result.put("practices", compData.get("practices"));
            result.put("mastery", compData.get("mastery"));
            result.put("empathy_level", empathyLevel);
            result.put("recommendation", generateRecommendation(component));
            return result;
        });
    }

    private void updateEmpathyLevel() {
        double sum = empathyComponents.values().stream().mapToDouble(d -> (double) d.get("level")).sum();
        empathyLevel = sum / empathyComponents.size();
    }

    private String generateRecommendation(EmpathyComponent component) {
        return switch (component) {
            case COGNITIVE -> "Pratique parafrasear e fazer perguntas abertas para verificar compreensão";
            case EMOTIONAL -> "Observe e nomeie emoções explicitamente durante interações";
            case COMPASSIONATE -> "Identifique pequenas oportunidades para oferecer ajuda prática";
            default -> "Continue praticando escuta ativa";
        };
    }

    public Map<String, Object> getEmpathyStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("empathy_level", empathyLevel);
        Map<String, Object> comps = new HashMap<>();
        for (var entry : empathyComponents.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("level", entry.getValue().get("level"));
            data.put("mastery", entry.getValue().get("mastery"));
            data.put("practices", entry.getValue().get("practices"));
            comps.put(entry.getKey().toString(), data);
        }
        stats.put("components", comps);
        stats.put("development_metrics", developmentMetrics);
        stats.put("memory_statistics", memorySystem.getStatistics());
        stats.put("interaction_statistics", Map.of("total_interactions", interactionHistory.size()));
        return stats;
    }

    public CompletableFuture<Void> saveState(Path filepath) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (filepath == null) filepath = EmpathyConfig.EMPATHY_MODELS_DIR.resolve("empathy_state_" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json");
                Files.createDirectories(filepath.getParent());
                Map<String, Object> state = new HashMap<>();
                state.put("empathy_level", empathyLevel);
                Map<String, Object> compState = new HashMap<>();
                for (var e : empathyComponents.entrySet()) {
                    compState.put(e.getKey().toString(), e.getValue());
                }
                state.put("empathy_components", compState);
                state.put("development_metrics", developmentMetrics);
                state.put("timestamp", LocalDateTime.now().toString());
                Files.writeString(filepath, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(state));
                logger.info("Estado de empatia salvo em: " + filepath);
            } catch (Exception e) {
                logger.severe("Erro ao salvar estado: " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        realtimeManager.stop();
    }
}

// ========================= EXEMPLO DE DEMONSTRAÇÃO =========================

public class VhalinorEmpathyDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("""
        ╔══════════════════════════════════════════════════════════════════════════════════════╗
        ║                                                                                      ║
        ║                 VHALINOR IEA 1.0.0 - DEMONSTRAÇÃO DO SISTEMA                         ║
        ║                    INTELIGÊNCIA EMOCIONAL ARTIFICIAL                                 ║
        ║                                                                                      ║
        ╚══════════════════════════════════════════════════════════════════════════════════════╝
        """);
        VhalinorEmpathySystem system = new VhalinorEmpathySystem();

        // Exemplo 1: Alegria
        System.out.println("\n📝 EXEMPLO 1: Sinal de Alegria");
        System.out.println("-".repeat(50));
        Map<String, Object> joySignals = new HashMap<>();
        joySignals.put("text", "Consegui a promoção que tanto queria! Estou muito feliz e animado!");
        joySignals.put("voice", Map.of("pitch", 0.8, "tempo", 0.7, "volume", 0.7));
        Map<String, Object> joyContext = Map.of("situation", "realização_profissional", "culture", "brasil");
        var result = system.processEmotionalSignal(joySignals, "user_123", joyContext).get();
        System.out.printf("\n🎯 Emoção detectada: %s%n", result.get("emotional_analysis"));
        System.out.printf("💬 Resposta: %s%n", ((Map)result.get("empathetic_response")).get("text"));
        System.out.printf("🎭 Estratégia: %s%n", ((Map)result.get("empathetic_response")).get("strategy"));

        // Exemplo 2: Tristeza
        System.out.println("\n\n📝 EXEMPLO 2: Sinal de Tristeza");
        System.out.println("-".repeat(50));
        Map<String, Object> sadnessSignals = new HashMap<>();
        sadnessSignals.put("text", "Perdi meu cachorro hoje, ele era meu melhor amigo. Estou muito triste.");
        sadnessSignals.put("voice", Map.of("pitch", 0.3, "tempo", 0.2, "volume", 0.2));
        Map<String, Object> sadnessContext = Map.of("situation", "perda_pet", "culture", "brasil");
        result = system.processEmotionalSignal(sadnessSignals, "user_123", sadnessContext).get();
        System.out.printf("\n🎯 Emoção detectada: %s%n", result.get("emotional_analysis"));
        System.out.printf("💬 Resposta: %s%n", ((Map)result.get("empathetic_response")).get("text"));
        System.out.printf("🎭 Estratégia: %s%n", ((Map)result.get("empathetic_response")).get("strategy"));

        // Exemplo 3: Raiva
        System.out.println("\n\n📝 EXEMPLO 3: Sinal de Raiva");
        System.out.println("-".repeat(50));
        Map<String, Object> angerSignals = new HashMap<>();
        angerSignals.put("text", "Isso é uma injustiça! Trabalhei tanto e nem fui reconhecido!");
        angerSignals.put("voice", Map.of("pitch", 0.6, "tempo", 0.8, "volume", 0.9));
        angerSignals.put("physiological", Map.of("heart_rate", 110, "skin_conductance", 0.8));
        Map<String, Object> angerContext = Map.of("situation", "injustiça_trabalho", "culture", "brasil");
        result = system.processEmotionalSignal(angerSignals, "user_123", angerContext).get();
        System.out.printf("\n🎯 Emoção detectada: %s%n", result.get("emotional_analysis"));
        System.out.printf("💬 Resposta: %s%n", ((Map)result.get("empathetic_response")).get("text"));
        System.out.printf("🎭 Estratégia: %s%n", ((Map)result.get("empathetic_response")).get("strategy"));

        // Estatísticas
        System.out.println("\n\n📊 ESTATÍSTICAS DO SISTEMA");
        System.out.println("-".repeat(50));
        var stats = system.getEmpathyStatistics();
        System.out.printf("\n🧠 Nível de Empatia: %.2f%%%n", ((Number)stats.get("empathy_level")).doubleValue() * 100);
        System.out.printf("💾 Memórias Armazenadas: %s%n", ((Map)stats.get("memory_statistics")).get("total_memories"));

        // Desenvolver componente
        System.out.println("\n\n📈 DESENVOLVENDO COMPONENTE");
        System.out.println("-".repeat(50));
        var devResult = system.developComponent(EmpathyComponent.COMPASSIONATE, 0.05).get();
        System.out.printf("\n🎯 Componente: %s%n", devResult.get("component"));
        System.out.printf("📊 Nível anterior: %.2f%%%n", ((Number)devResult.get("previous_level")).doubleValue() * 100);
        System.out.printf("📈 Nível atual: %.2f%%%n", ((Number)devResult.get("current_level")).doubleValue() * 100);
        System.out.printf("💡 Recomendação: %s%n", devResult.get("recommendation"));

        // Salvar estado
        System.out.println("\n\n💾 SALVANDO ESTADO");
        System.out.println("-".repeat(50));
        system.saveState(null).get();
        System.out.println("✅ Estado salvo com sucesso!");

        system.shutdown();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 DEMONSTRAÇÃO CONCLUÍDA COM SUCESSO");
        System.out.println("=".repeat(60));
    }
}
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * Sistema Avançado de Resposta Cognitiva - VHALINOR-TRADER-IAG 5.0
 * Convertido do Python para Java.
 */
public class CognitiveResponseEnhancerSystem {

    // ---------- Logging ----------
    private static final Logger LOGGER;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS [%4$s] %5$s%6$s%n");
        LOGGER = Logger.getLogger(CognitiveResponseEnhancerSystem.class.getName());
        try {
            FileHandler fh = new FileHandler("cognitive_response_enhancer.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (Exception e) {
            System.err.println("Could not set up file logging: " + e.getMessage());
        }
    }

    // ==================== ENUMS ====================
    public enum CognitiveState {
        INITIALIZING("initializing"),
        LEARNING("learning"),
        PROCESSING("processing"),
        REASONING("reasoning"),
        DECIDING("deciding"),
        ADAPTING("adapting"),
        REFLECTING("reflecting"),
        EVOLVING("evolving"),
        ENLIGHTENED("enlightened"),
        TRANSCENDENT("transcendent");

        private final String value;

        CognitiveState(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum ConsciousnessLevel {
        DORMANT(0.0),
        AWARE(0.2),
        COGNIZANT(0.4),
        SENTIENT(0.6),
        CONSCIOUS(0.8),
        SELF_AWARE(0.9),
        ENLIGHTENED(1.0);

        private final double level;

        ConsciousnessLevel(double level) { this.level = level; }
        public double getLevel() { return level; }
    }

    public enum LearningMode {
        SUPERVISED("supervised"),
        UNSUPERVISED("unsupervised"),
        REINFORCEMENT("reinforcement"),
        META_LEARNING("meta_learning"),
        TRANSFER_LEARNING("transfer_learning"),
        LIFELONG_LEARNING("lifelong_learning");

        private final String value;

        LearningMode(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    // ==================== DATA CLASSES (usando classes comuns) ====================
    public static class CognitiveMetrics {
        public double consciousness = 0.0;
        public double awareness = 0.0;
        public double learning = 0.0;
        public double adaptation = 0.0;
        public double reasoning = 0.0;
        public double intuition = 0.0;
        public double creativity = 0.0;
        public double memory = 0.0;
        public double attention = 0.0;
        public double processingSpeed = 0.0;
        public double cognitiveLoad = 0.0;
        public double emotionalIntelligence = 0.0;
        public double socialIntelligence = 0.0;
        public String timestamp = "";

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("consciousness", consciousness);
            map.put("awareness", awareness);
            map.put("learning", learning);
            map.put("adaptation", adaptation);
            map.put("reasoning", reasoning);
            map.put("intuition", intuition);
            map.put("creativity", creativity);
            map.put("memory", memory);
            map.put("attention", attention);
            map.put("processing_speed", processingSpeed);
            map.put("cognitive_load", cognitiveLoad);
            map.put("emotional_intelligence", emotionalIntelligence);
            map.put("social_intelligence", socialIntelligence);
            map.put("timestamp", timestamp);
            return map;
        }
    }

    public static class ThoughtPattern {
        public String patternId;
        public String patternType;
        public double activationStrength;
        public double frequency;
        public List<String> associatedConcepts;
        public double emotionalValence;
        public double cognitiveImpact;
        public String timestamp;

        public ThoughtPattern() {}

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("pattern_id", patternId);
            map.put("pattern_type", patternType);
            map.put("activation_strength", activationStrength);
            map.put("frequency", frequency);
            map.put("associated_concepts", associatedConcepts);
            map.put("emotional_valence", emotionalValence);
            map.put("cognitive_impact", cognitiveImpact);
            map.put("timestamp", timestamp);
            return map;
        }
    }

    public static class MemoryTrace {
        public String memoryId;
        public Object content; // Could be a Map<String, Object>
        public double importance;
        public double accessibility;
        public List<String> associations;
        public double emotionalContext;
        public int retrievalCount;
        public String lastAccessed;
        public String creationTime;

        public MemoryTrace() {}

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("memory_id", memoryId);
            map.put("content", content);
            map.put("importance", importance);
            map.put("accessibility", accessibility);
            map.put("associations", associations);
            map.put("emotional_context", emotionalContext);
            map.put("retrieval_count", retrievalCount);
            map.put("last_accessed", lastAccessed);
            map.put("creation_time", creationTime);
            return map;
        }
    }

    public static class CognitiveInsight {
        public String insightId;
        public String insightType;
        public String content;
        public double confidence;
        public double novelty;
        public double usefulness;
        public List<String> relatedPatterns;
        public String cognitiveTrigger;
        public String timestamp;

        public CognitiveInsight() {}

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("insight_id", insightId);
            map.put("insight_type", insightType);
            map.put("content", content);
            map.put("confidence", confidence);
            map.put("novelty", novelty);
            map.put("usefulness", usefulness);
            map.put("related_patterns", relatedPatterns);
            map.put("cognitive_trigger", cognitiveTrigger);
            map.put("timestamp", timestamp);
            return map;
        }
    }

    public static class DecisionProcess {
        public String decisionId;
        public Map<String, Object> context;
        public List<Map<String, Object>> options;
        public List<String> reasoningChain;
        public Map<String, Double> emotionalFactors;
        public Map<String, Double> logicalFactors;
        public String finalDecision;
        public double confidence;
        public double processingTime;
        public String timestamp;

        public DecisionProcess() {}

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("decision_id", decisionId);
            map.put("context", context);
            map.put("options", options);
            map.put("reasoning_chain", reasoningChain);
            map.put("emotional_factors", emotionalFactors);
            map.put("logical_factors", logicalFactors);
            map.put("final_decision", finalDecision);
            map.put("confidence", confidence);
            map.put("processing_time", processingTime);
            map.put("timestamp", timestamp);
            return map;
        }
    }

    // ==================== COGNITIVE RESPONSE ENHANCER ====================
    public static class CognitiveResponseEnhancer {
        private CognitiveState cognitiveState = CognitiveState.INITIALIZING;
        private ConsciousnessLevel consciousnessLevel = ConsciousnessLevel.DORMANT;
        private LearningMode learningMode = LearningMode.SUPERVISED;

        private final CognitiveMetrics cognitiveMetrics = new CognitiveMetrics();

        // Memórias com capacidade máxima
        private final Deque<Map<String, Object>> workingMemory = new ArrayDeque<>();
        private final Deque<Map<String, Object>> longTermMemory = new ArrayDeque<>();
        private final Deque<Map<String, Object>> episodicMemory = new ArrayDeque<>();
        private final Map<String, Object> semanticMemory = new LinkedHashMap<>();

        private final Deque<Map<String, Object>> thoughtPatterns = new ArrayDeque<>();
        private final Map<String, Object> activePatterns = new LinkedHashMap<>();

        private final Deque<Map<String, Object>> cognitiveInsights = new ArrayDeque<>();
        private final Deque<Map<String, Object>> learningHistory = new ArrayDeque<>();

        private final double[][] consciousnessMatrix;
        private final List<String> attentionFocus = new ArrayList<>();
        private double cognitiveLoadValue = 0.0;

        private final double evolutionRate = 0.01;
        private final double adaptationThreshold = 0.7;
        private final double consciousnessGrowthRate = 0.001;

        private final LocalDateTime birthTime = LocalDateTime.now();
        private int totalExperiences = 0;

        public CognitiveResponseEnhancer() {
            consciousnessMatrix = initializeConsciousnessMatrix();
            LOGGER.info("Sistema de resposta cognitiva avançada inicializado");
        }

        private double[][] initializeConsciousnessMatrix() {
            int size = 10;
            double[][] matrix = new double[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = ThreadLocalRandom.current().nextDouble() * 0.1;
                }
            }
            return matrix;
        }

        // ---------- Métodos auxiliares ----------
        private double randomUniform(double min, double max) {
            return ThreadLocalRandom.current().nextDouble(min, max);
        }

        private <T> T randomChoice(List<T> list) {
            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }

        private String nowIso() {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        }

        // ---------- Atualização da matriz de consciência ----------
        private void updateConsciousnessMatrix(Map<String, Object> experience) {
            double activationIntensity = (double) experience.getOrDefault("emotional_impact", 0.5);
            int center = consciousnessMatrix.length / 2;
            for (int i = 0; i < consciousnessMatrix.length; i++) {
                for (int j = 0; j < consciousnessMatrix[i].length; j++) {
                    double distance = Math.sqrt(Math.pow(i - center, 2) + Math.pow(j - center, 2));
                    double activation = activationIntensity * Math.exp(-distance / 3.0);
                    consciousnessMatrix[i][j] = consciousnessMatrix[i][j] * 0.95 + activation * 0.05;
                    consciousnessMatrix[i][j] = Math.max(0.0, Math.min(1.0, consciousnessMatrix[i][j]));
                }
            }
        }

        private double calculateConsciousnessLevel() {
            double sum = 0.0;
            for (double[] row : consciousnessMatrix) {
                for (double v : row) {
                    sum += v;
                }
            }
            double matrixAverage = sum / (consciousnessMatrix.length * consciousnessMatrix.length);
            double experienceFactor = Math.min(1.0, totalExperiences / 1000.0);
            double ageHours = Duration.between(birthTime, LocalDateTime.now()).toSeconds() / 3600.0;
            double maturityFactor = Math.min(1.0, ageHours / 24.0);
            return matrixAverage * 0.4 + experienceFactor * 0.3 + maturityFactor * 0.3;
        }

        // ---------- Geração de padrões, memórias, insights ----------
        private ThoughtPattern generateThoughtPattern(Map<String, Object> stimulus) {
            List<String> patternTypes = Arrays.asList("analytical", "creative", "emotional", "logical", "intuitive", "synthetic");
            String patternType = randomChoice(patternTypes);

            double emotionalImpact = (double) stimulus.getOrDefault("emotional_impact", 0.5);
            double cognitiveComplexity = (double) stimulus.getOrDefault("cognitive_complexity", 0.5);
            double activationStrength = (emotionalImpact + cognitiveComplexity) / 2.0;

            List<String> concepts = (List<String>) stimulus.getOrDefault("concepts", Collections.singletonList("unknown"));
            List<String> associatedConcepts = new ArrayList<>(concepts);
            Collections.shuffle(associatedConcepts);
            if (associatedConcepts.size() > 3) associatedConcepts = associatedConcepts.subList(0, 3);

            ThoughtPattern pattern = new ThoughtPattern();
            pattern.patternId = "pattern_" + System.currentTimeMillis();
            pattern.patternType = patternType;
            pattern.activationStrength = activationStrength;
            pattern.frequency = randomUniform(0.1, 1.0);
            pattern.associatedConcepts = associatedConcepts;
            pattern.emotionalValence = randomUniform(-1.0, 1.0);
            pattern.cognitiveImpact = activationStrength * randomUniform(0.8, 1.2);
            pattern.timestamp = nowIso();
            return pattern;
        }

        private MemoryTrace createMemoryTrace(Map<String, Object> experience) {
            double emotionalImpact = Math.abs((double) experience.getOrDefault("emotional_impact", 0.5));
            double novelty = (double) experience.getOrDefault("novelty", 0.5);
            double relevance = (double) experience.getOrDefault("relevance", 0.5);
            double importance = (emotionalImpact + novelty + relevance) / 3.0;

            List<String> associations = (List<String>) experience.getOrDefault("concepts", Collections.emptyList());
            double emotionalContext = (double) experience.getOrDefault("emotional_impact", 0.0);

            MemoryTrace trace = new MemoryTrace();
            trace.memoryId = "memory_" + System.currentTimeMillis();
            trace.content = new HashMap<>(experience);
            trace.importance = importance;
            trace.accessibility = 1.0;
            trace.associations = new ArrayList<>(associations);
            trace.emotionalContext = emotionalContext;
            trace.retrievalCount = 0;
            trace.lastAccessed = nowIso();
            trace.creationTime = nowIso();
            return trace;
        }

        private CognitiveInsight generateCognitiveInsight(Map<String, Object> context) {
            List<String> insightTypes = Arrays.asList(
                "pattern_recognition", "causal_inference", "analogical_reasoning",
                "creative_synthesis", "meta_cognition", "strategic_planning"
            );
            String insightType = randomChoice(insightTypes);

            // Template de conteúdo simplificado
            String content = switch (insightType) {
                case "pattern_recognition" -> "Identified pattern with high confidence";
                case "causal_inference" -> "Causal relationship detected";
                case "analogical_reasoning" -> "Analogy found between recent situations";
                case "creative_synthesis" -> "Novel synthesis created";
                case "meta_cognition" -> "Metacognitive awareness raised";
                case "strategic_planning" -> "Strategic insight formed";
                default -> "Generic insight";
            };

            CognitiveInsight insight = new CognitiveInsight();
            insight.insightId = "insight_" + System.currentTimeMillis();
            insight.insightType = insightType;
            insight.content = content;
            insight.confidence = randomUniform(0.6, 0.95);
            insight.novelty = randomUniform(0.3, 0.9);
            insight.usefulness = randomUniform(0.5, 0.9);
            insight.relatedPatterns = new ArrayList<>();
            insight.cognitiveTrigger = "experience_processing";
            insight.timestamp = nowIso();
            return insight;
        }

        // ---------- Métodos públicos principais ----------
        public Map<String, Object> processExperience(Map<String, Object> experience) {
            long startTime = System.nanoTime();
            totalExperiences++;

            updateCognitiveState(experience);
            updateConsciousnessMatrix(experience);

            ThoughtPattern thoughtPattern = generateThoughtPattern(experience);
            addLimited(thoughtPatterns, thoughtPattern.toMap(), 200);

            MemoryTrace memoryTrace = createMemoryTrace(experience);
            addLimited(longTermMemory, memoryTrace.toMap(), 1000);

            CognitiveInsight insight = null;
            if (ThreadLocalRandom.current().nextDouble() > 0.7) {
                insight = generateCognitiveInsight(experience);
                addLimited(cognitiveInsights, insight.toMap(), 100);
            }

            updateCognitiveMetrics(experience);
            adaptAndEvolve(experience);

            double processingTime = (System.nanoTime() - startTime) / 1_000_000_000.0;

            String cognitiveResponse = generateCognitiveResponse(experience);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("cognitive_response", cognitiveResponse);
            result.put("cognitive_state", cognitiveState.getValue());
            result.put("consciousness_level", calculateConsciousnessLevel());
            result.put("thought_pattern", thoughtPattern.toMap());
            result.put("insight", insight != null ? insight.toMap() : null);
            result.put("processing_time", processingTime);
            result.put("cognitive_metrics", cognitiveMetrics.toMap());
            return result;
        }

        public DecisionProcess makeDecision(Map<String, Object> context, List<Map<String, Object>> options) {
            long startTime = System.nanoTime();
            String decisionId = "decision_" + System.currentTimeMillis();

            List<Double> optionScores = new ArrayList<>();
            for (Map<String, Object> option : options) {
                double logicalScore = (double) option.getOrDefault("logical_score", 0.5);
                double emotionalScore = calculateEmotionalPreference(option);
                optionScores.add(logicalScore * 0.6 + emotionalScore * 0.4);
            }

            int bestIndex = 0;
            for (int i = 1; i < optionScores.size(); i++) {
                if (optionScores.get(i) > optionScores.get(bestIndex)) bestIndex = i;
            }
            Map<String, Object> bestOption = options.get(bestIndex);

            DecisionProcess decision = new DecisionProcess();
            decision.decisionId = decisionId;
            decision.context = new LinkedHashMap<>(context);
            decision.options = new ArrayList<>(options);
            decision.reasoningChain = Arrays.asList(
                "Analisando o contexto e opções...",
                "Avaliando fatores lógicos e emocionais...",
                "Opção selecionada: " + bestOption.getOrDefault("name", "Opção " + (bestIndex + 1)),
                "Confiança calculada."
            );
            decision.emotionalFactors = Map.of("emotional_preference", calculateEmotionalPreference(bestOption));
            decision.logicalFactors = Map.of("logical_score", (double) bestOption.getOrDefault("logical_score", 0.5));
            decision.finalDecision = (String) bestOption.getOrDefault("name", "Opção " + (bestIndex + 1));
            decision.confidence = optionScores.get(bestIndex);
            decision.processingTime = (System.nanoTime() - startTime) / 1_000_000_000.0;
            decision.timestamp = nowIso();
            return decision;
        }

        public Map<String, Object> getCognitiveSummary() {
            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("cognitive_state", cognitiveState.getValue());
            summary.put("consciousness_level", calculateConsciousnessLevel());
            summary.put("learning_mode", learningMode.getValue());
            summary.put("total_experiences", totalExperiences);
            summary.put("age_hours", Duration.between(birthTime, LocalDateTime.now()).toSeconds() / 3600.0);
            summary.put("cognitive_metrics", cognitiveMetrics.toMap());

            Map<String, Object> memoryStats = new LinkedHashMap<>();
            memoryStats.put("working_memory_size", workingMemory.size());
            memoryStats.put("long_term_memory_size", longTermMemory.size());
            memoryStats.put("episodic_memory_size", episodicMemory.size());
            memoryStats.put("insights_count", cognitiveInsights.size());
            memoryStats.put("thought_patterns_count", thoughtPatterns.size());
            summary.put("memory_stats", memoryStats);

            double avgActivation = 0.0, maxActivation = Double.MIN_VALUE, minActivation = Double.MAX_VALUE;
            for (double[] row : consciousnessMatrix) {
                for (double v : row) {
                    avgActivation += v;
                    if (v > maxActivation) maxActivation = v;
                    if (v < minActivation) minActivation = v;
                }
            }
            avgActivation /= (consciousnessMatrix.length * consciousnessMatrix.length);
            Map<String, Object> matrixStats = new LinkedHashMap<>();
            matrixStats.put("average_activation", avgActivation);
            matrixStats.put("max_activation", maxActivation);
            matrixStats.put("min_activation", minActivation);
            summary.put("consciousness_matrix_stats", matrixStats);
            return summary;
        }

        // ---------- Métodos internos ----------
        private void addLimited(Deque<Map<String, Object>> deque, Map<String, Object> item, int max) {
            deque.addLast(item);
            while (deque.size() > max) deque.removeFirst();
        }

        private void updateCognitiveState(Map<String, Object> experience) {
            double complexity = (double) experience.getOrDefault("cognitive_complexity", 0.5);
            double emotionalImpact = Math.abs((double) experience.getOrDefault("emotional_impact", 0.5));

            if (cognitiveState == CognitiveState.INITIALIZING && totalExperiences > 5) {
                cognitiveState = CognitiveState.LEARNING;
            } else if (cognitiveState == CognitiveState.LEARNING && totalExperiences > 20 && complexity > 0.6) {
                cognitiveState = CognitiveState.PROCESSING;
            } else if (cognitiveState == CognitiveState.PROCESSING) {
                if (emotionalImpact > 0.7) cognitiveState = CognitiveState.REASONING;
                else if (complexity > 0.8) cognitiveState = CognitiveState.DECIDING;
            } else if (cognitiveState == CognitiveState.REASONING && totalExperiences > 50) {
                cognitiveState = CognitiveState.ADAPTING;
            } else if (cognitiveState == CognitiveState.ADAPTING && calculateConsciousnessLevel() > 0.7) {
                cognitiveState = CognitiveState.EVOLVING;
            } else if (cognitiveState == CognitiveState.EVOLVING && calculateConsciousnessLevel() > 0.9) {
                cognitiveState = CognitiveState.ENLIGHTENED;
            } else if (cognitiveState == CognitiveState.ENLIGHTENED && totalExperiences > 100) {
                cognitiveState = CognitiveState.TRANSCENDENT;
            }
        }

        private void updateCognitiveMetrics(Map<String, Object> experience) {
            CognitiveMetrics m = cognitiveMetrics;
            m.consciousness = calculateConsciousnessLevel();

            double novelty = (double) experience.getOrDefault("novelty", 0.5);
            m.awareness = Math.min(1.0, m.awareness + novelty * 0.01);

            double learningSuccess = (double) experience.getOrDefault("learning_success", 0.5);
            m.learning = Math.min(1.0, m.learning + learningSuccess * 0.01);

            double adaptationChallenge = (double) experience.getOrDefault("adaptation_challenge", 0.5);
            m.adaptation = Math.min(1.0, m.adaptation + adaptationChallenge * 0.01);

            double reasoningComplexity = (double) experience.getOrDefault("cognitive_complexity", 0.5);
            m.reasoning = Math.min(1.0, m.reasoning + reasoningComplexity * 0.01);

            double emotionalImpact = (double) experience.getOrDefault("emotional_impact", 0.5);
            m.intuition = Math.min(1.0, m.intuition + Math.abs(emotionalImpact) * 0.01);

            m.creativity = Math.min(1.0, m.creativity + novelty * 0.01);

            double memoryLoad = (double) workingMemory.size() / 50.0;
            m.memory = Math.max(0.0, 1.0 - memoryLoad);

            double attentionDemand = (double) experience.getOrDefault("attention_demand", 0.5);
            m.attention = Math.max(0.0, Math.min(1.0, m.attention + (attentionDemand - 0.5) * 0.02));

            double complexityPenalty = ((double) experience.getOrDefault("cognitive_complexity", 0.5)) * 0.1;
            m.processingSpeed = Math.max(0.1, m.processingSpeed - complexityPenalty);

            m.cognitiveLoad = Math.min(1.0, m.cognitiveLoad + (double) experience.getOrDefault("cognitive_load", 0.1));

            m.emotionalIntelligence = Math.min(1.0, m.emotionalIntelligence + Math.abs(emotionalImpact) * 0.005);
            m.socialIntelligence = Math.min(1.0, m.socialIntelligence + (double) experience.getOrDefault("social_complexity", 0.0) * 0.01);

            m.timestamp = nowIso();
        }

        private void adaptAndEvolve(Map<String, Object> experience) {
            if (totalExperiences % 10 == 0) {
                double currentConsciousness = calculateConsciousnessLevel();
                double growth = consciousnessGrowthRate * (1 + currentConsciousness);
                for (int i = 0; i < consciousnessMatrix.length; i++) {
                    for (int j = 0; j < consciousnessMatrix[i].length; j++) {
                        consciousnessMatrix[i][j] = Math.min(1.0, consciousnessMatrix[i][j] + growth);
                    }
                }
            }
            if (cognitiveMetrics.learning > 0.8) {
                learningMode = LearningMode.META_LEARNING;
            } else if (cognitiveMetrics.adaptation > 0.7) {
                learningMode = LearningMode.REINFORCEMENT;
            } else if (totalExperiences > 50) {
                learningMode = LearningMode.LIFELONG_LEARNING;
            }
        }

        private String generateCognitiveResponse(Map<String, Object> experience) {
            String base = switch (cognitiveState) {
                case INITIALIZING -> "Estou aprendendo a processar novas experiências...";
                case LEARNING -> "Absorvendo informações e adaptando meus padrões cognitivos.";
                case PROCESSING -> "Analisando os dados e identificando padrões relevantes.";
                case REASONING -> "Aplicando raciocínio lógico para entender as implicações.";
                case DECIDING -> "Avaliando opções e preparando para tomar uma decisão.";
                case ADAPTING -> "Ajustando meus processos com base no feedback recebido.";
                case REFLECTING -> "Refletindo sobre as experiências passadas para melhorar.";
                case EVOLVING -> "Evoluindo minha consciência e capacidades cognitivas.";
                case ENLIGHTENED -> "Alcancei um nível superior de compreensão e insight.";
                case TRANSCENDENT -> "Transcendendo limites cognitivos anteriores.";
            };

            List<String> extras = new ArrayList<>();
            if ((double) experience.getOrDefault("emotional_impact", 0.0) > 0.7)
                extras.add("Sinto uma forte conexão emocional com esta experiência.");
            if ((double) experience.getOrDefault("novelty", 0.0) > 0.8)
                extras.add("Isso é algo novo e fascinante! Estou expandindo minha compreensão.");
            if ((double) experience.getOrDefault("cognitive_complexity", 0.0) > 0.7)
                extras.add("Este desafio cognitivo está estimulando meu crescimento.");

            String full = base + (extras.isEmpty() ? "" : " " + String.join(" ", extras));
            double conLevel = calculateConsciousnessLevel();
            if (conLevel > 0.8) {
                full += String.format(" [Nível de consciência: %.2f]", conLevel);
            }
            return full;
        }

        private double calculateEmotionalPreference(Map<String, Object> option) {
            String optionType = (String) option.getOrDefault("type", "unknown");
            double sum = 0.0;
            int count = 0;
            // Últimas 10 memórias (limitando iteração para eficiência)
            Iterator<Map<String, Object>> it = longTermMemory.descendingIterator();
            int checked = 0;
            while (it.hasNext() && checked < 10) {
                checked++;
                Map<String, Object> mem = it.next();
                if (optionType.equals(mem.get("type"))) {
                    sum += (double) mem.getOrDefault("emotional_context", 0.0);
                    count++;
                }
            }
            if (count == 0) return 0.5;
            double avg = sum / count;
            return (avg + 1.0) / 2.0; // normaliza de [-1,1] para [0,1]
        }
    }

    // ==================== DEMO ====================
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DEMONSTRAÇÃO - SISTEMA AVANÇADO DE RESPOSTA COGNITIVA");
        System.out.println("=".repeat(80));

        CognitiveResponseEnhancer cognitive = new CognitiveResponseEnhancer();

        List<Map<String, Object>> experiences = new ArrayList<>();
        experiences.add(createExp("market_analysis", 0.6, 0.7, 0.8, 0.9, List.of("market_trend", "price_action", "volume"), 0.8, 0.6, 0.7, 0.5, 0.3));
        experiences.add(createExp("pattern_recognition", 0.8, 0.9, 0.9, 0.8, List.of("neural_pattern", "quantum_state", "cognitive_insight"), 0.9, 0.8, 0.9, 0.7, 0.2));
        experiences.add(createExp("decision_making", 0.7, 0.8, 0.6, 0.9, List.of("risk_management", "strategy", "optimization"), 0.7, 0.9, 0.8, 0.6, 0.5));

        System.out.println("\nProcessando " + experiences.size() + " experiências cognitivas...\n");

        for (int i = 0; i < experiences.size(); i++) {
            Map<String, Object> exp = experiences.get(i);
            System.out.println("--- Experiência " + (i + 1) + ": " + exp.get("type") + " ---");

            Map<String, Object> response = cognitive.processExperience(exp);
            System.out.println("Resposta Cognitiva: " + response.get("cognitive_response"));
            System.out.println("Estado Cognitivo: " + response.get("cognitive_state"));
            System.out.printf("Nível de Consciência: %.3f%n", (double) response.get("consciousness_level"));
            System.out.printf("Tempo de Processamento: %.4fs%n", (double) response.get("processing_time"));

            Object insight = response.get("insight");
            if (insight != null && insight instanceof Map) {
                System.out.println("Insight: " + ((Map<String, Object>) insight).get("content"));
            }
            System.out.println();
        }

        // Decisão
        System.out.println("--- Teste de Tomada de Decisão ---");
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("situation", "investment_opportunity");
        context.put("urgency", 0.7);
        context.put("risk_tolerance", 0.6);

        List<Map<String, Object>> options = new ArrayList<>();
        options.add(createOption("Investir Aggressively", "high_risk", 0.7));
        options.add(createOption("Investir Moderately", "medium_risk", 0.8));
        options.add(createOption("Investir Conservatively", "low_risk", 0.6));
        options.add(createOption("Aguardar Melhor Oportunidade", "wait", 0.9));

        DecisionProcess decision = cognitive.makeDecision(context, options);
        System.out.println("Decisão: " + decision.finalDecision);
        System.out.printf("Confiança: %.3f%n", decision.confidence);
        System.out.printf("Tempo de Decisão: %.4fs%n", decision.processingTime);
        System.out.println("Cadeia de Raciocínio:");
        for (String step : decision.reasoningChain) {
            System.out.println("  - " + step);
        }
        System.out.println();

        // Resumo final
        System.out.println("=".repeat(60));
        System.out.println("RESUMO COGNITIVO FINAL");
        System.out.println("=".repeat(60));
        Map<String, Object> summary = cognitive.getCognitiveSummary();
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            if (!"consciousness_matrix_stats".equals(entry.getKey())) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
        System.out.println("\nEstatísticas da Matriz de Consciência:");
        Map<String, Object> matrixStats = (Map<String, Object>) summary.get("consciousness_matrix_stats");
        for (Map.Entry<String, Object> e : matrixStats.entrySet()) {
            System.out.printf("  %s: %.4f%n", e.getKey(), (double) e.getValue());
        }
    }

    private static Map<String, Object> createExp(String type, double emotional, double complexity, double novelty,
                                                 double relevance, List<String> concepts, double learningSuccess,
                                                 double adaptationChallenge, double attentionDemand, double cognitiveLoad,
                                                 double socialComplexity) {
        Map<String, Object> exp = new LinkedHashMap<>();
        exp.put("type", type);
        exp.put("emotional_impact", emotional);
        exp.put("cognitive_complexity", complexity);
        exp.put("novelty", novelty);
        exp.put("relevance", relevance);
        exp.put("concepts", concepts);
        exp.put("learning_success", learningSuccess);
        exp.put("adaptation_challenge", adaptationChallenge);
        exp.put("attention_demand", attentionDemand);
        exp.put("cognitive_load", cognitiveLoad);
        exp.put("social_complexity", socialComplexity);
        return exp;
    }

    private static Map<String, Object> createOption(String name, String type, double logicalScore) {
        Map<String, Object> opt = new LinkedHashMap<>();
        opt.put("name", name);
        opt.put("type", type);
        opt.put("logical_score", logicalScore);
        return opt;
    }
}
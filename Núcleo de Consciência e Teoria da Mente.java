import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * VHALINOR.IAG 6.3 - Núcleo de Consciência e Teoria da Mente (Java Port)
 * Inclui: Autoconsciência, Curiosidade, Teoria da Mente, Raciocínio Moral.
 */
public class ConsciousnessCore {

    // ==================== ENUMS ====================
    public enum ConsciousnessState {
        DROWSY("drowsy"), AWAKE("awake"), FOCUSED("focused"),
        HYPER_FOCUS("hyper_focused"), FLOW("flow"), REST("rest");
        public final String value;
        ConsciousnessState(String v) { value = v; }
    }

    // ==================== AGENT STUB INTERFACES ====================
    // Minimal stubs to replace external modules (knowledge graph, memory, etc.)
    interface KnowledgeGraph {
        // Map of concept ID -> Concept
        Map<String, Concept> getConcepts();
        List<Map.Entry<String, Double>> findSimilar(double[] embedding, double threshold, int limit);
        boolean hasEdge(String source, String target);
        // other methods...
        class Concept {
            public String id, name;
            public double confidence;
            public Object type; // mock
            public LocalDateTime creationTime;
        }
    }

    interface EpisodicMemory {
        Map<String, Episode> getEpisodes();
        List<Episode> recall(int limit);
        class Episode {
            public String id;
            public Object content;
            public double importance, retrievalStrength;
            public LocalDateTime timestamp;
        }
    }

    interface Learner {
        Map<String, Double> getSkillLevels();
        double getCuriosityScore();
    }

    interface Attention {
        Focus selectFocus(Map<String, Object> stimuli, Map<String, Object> context);
        class Focus {
            public String targetId;
            public double intensity;
        }
    }

    interface NLP {
        double[] createEmbedding(String text);
    }

    interface RLAgent {
        String selectAction(double[] state);
        void learn(double[] state, String action, double reward, double[] nextState, boolean done);
        Map<String, Object> experienceReplay();
    }

    interface SleepSystem {
        double getSleepPressure();
        void updateSleepPressure();
        boolean shouldSleep();
        CompletableFuture<Void> sleepCycle();
    }

    interface CreativeGenerator {
        // marker
    }

    interface MetaCognition {
        double getCognitiveLoad();
        Object getConsciousnessState(); // could return ConsciousnessState
    }

    // ==================== AGICoreBase Stub ====================
    static class AGICoreBase {
        public int cycleCount = 0;
        public Map<String, Object> currentState = new HashMap<>();
        public KnowledgeGraph knowledgeGraph;
        public EpisodicMemory episodicMemory;
        public Attention attention;
        public Learner learner;
        public RLAgent rlAgent;
        public NLP nlp;
        public SleepSystem sleepSystem;
        public CreativeGenerator creativeGenerator;
        public MetaCognition metaCognition;
        public String lastAction;
        public Map<String, Object> identity = Map.of("creation_date", LocalDateTime.now());

        public List<String> _getCurrentGoals() { return new ArrayList<>(); }
        public double[] _getStateRepresentation() { return new double[64]; }
        public CompletableFuture<Map<String, Object>> _executeAction(String action, Object stimulus) {
            return CompletableFuture.completedFuture(Map.of("success", false));
        }
        public List<Reward> _computeRewards(String action, Map<String, Object> result) { return new ArrayList<>(); }
        public Map<String, Object> getStatus() { return new HashMap<>(); }
    }

    // ==================== REWARD STUB ====================
    static class Reward {
        public enum Type { CURIOSITY, ACHIEVEMENT, MORAL }
        public Type type;
        public double value;
    }

    // ==================== SELF MODEL ====================
    static class SelfModel {
        private final AGICore agi;
        double[] selfRepresentation;
        List<Map<String, Object>> selfNarrative = new ArrayList<>();
        Map<String, Object> knowledgeOfKnowledge = new HashMap<>();
        Map<String, Object> knowledgeOfIgnorance = new HashMap<>();
        double hyperconsciousness = 1.0;
        List<Map.Entry<String, Map<String, Object>>> possibleSelves = new ArrayList<>();
        Map<String, Object> desiredSelf;
        Map<String, Object> fearedSelf;
        double continuityLevel = 1.0;
        Deque<Map<String, Object>> memoryOfSelf = new ArrayDeque<>();
        double selfCoherence = 1.0;
        Random rand = new Random();

        SelfModel(AGICore agi) { this.agi = agi; }

        synchronized Map<String, Object> updateSelfModel() {
            KnowledgeGraph kg = agi.knowledgeGraph;
            EpisodicMemory mem = agi.episodicMemory;
            Learner learner = agi.learner;
            MetaCognition meta = agi.metaCognition;

            Map<String, Object> current = new LinkedHashMap<>();
            current.put("timestamp", LocalDateTime.now());
            current.put("knowledge_size", kg != null ? kg.getConcepts().size() : 0);
            current.put("memory_size", mem != null ? mem.getEpisodes().size() : 0);
            current.put("capabilities", learner != null ? new ArrayList<>(learner.getSkillLevels().keySet()) : new ArrayList<>());
            if (meta != null && meta.getConsciousnessState() != null)
                current.put("consciousness", meta.getConsciousnessState().toString());
            else current.put("consciousness", "awake");
            current.put("goals", agi._getCurrentGoals());
            selfNarrative.add(current);

            selfRepresentation = createSelfEmbedding();
            evaluateCoherence();
            updateSelfKnowledge();
            simulatePossibleSelves();
            return current;
        }

        private double[] createSelfEmbedding() {
            List<Double> features = new ArrayList<>();
            KnowledgeGraph kg = agi.knowledgeGraph;
            EpisodicMemory mem = agi.episodicMemory;
            MetaCognition meta = agi.metaCognition;
            Learner learner = agi.learner;

            features.add(kg != null ? kg.getConcepts().size() / 1000.0 : 0.0);
            features.add(mem != null ? mem.getEpisodes().size() / 500.0 : 0.0);
            features.add(meta != null ? meta.getCognitiveLoad() : 0.5);
            features.add(learner != null ? learner.getCuriosityScore() : 0.5);

            if (learner != null && learner.getSkillLevels() != null) {
                for (double v : learner.getSkillLevels().values()) {
                    features.add(v);
                    if (features.size() >= 128) break;
                }
            }
            while (features.size() < 128) features.add(0.0);

            double[] embed = features.subList(0, 128).stream().mapToDouble(Double::doubleValue).toArray();
            double norm = 1e-8;
            for (double v : embed) norm += v * v;
            norm = Math.sqrt(norm);
            for (int i = 0; i < embed.length; i++) embed[i] /= norm;
            return embed;
        }

        private void evaluateCoherence() {
            if (selfNarrative.size() < 2) {
                selfCoherence = 1.0;
                return;
            }
            List<Map<String, Object>> recent = selfNarrative.subList(Math.max(0, selfNarrative.size() - 5), selfNarrative.size());
            double knowledgeStd = 0;
            if (recent.size() > 1) {
                double mean = recent.stream().mapToInt(m -> (int) m.get("knowledge_size")).average().orElse(0);
                double variance = recent.stream().mapToDouble(m -> Math.pow((int) m.get("knowledge_size") - mean, 2)).average().orElse(0);
                knowledgeStd = Math.sqrt(variance);
            }
            long distinctGoals = recent.stream().map(m -> new HashSet<>((List<String>) m.get("goals"))).distinct().count();
            double goalConsistency = (double) distinctGoals / Math.max(1, recent.size());
            selfCoherence = 1.0 / (1.0 + knowledgeStd * goalConsistency);
            selfCoherence = Math.max(0.3, Math.min(1.0, selfCoherence));
        }

        private void updateSelfKnowledge() {
            KnowledgeGraph kg = agi.knowledgeGraph;
            if (kg == null) return;
            knowledgeOfKnowledge = new LinkedHashMap<>();
            knowledgeOfKnowledge.put("total_concepts", kg.getConcepts().size());
            if (agi.learner != null && agi.learner.getSkillLevels() != null) {
                List<Map.Entry<String, Double>> sorted = agi.learner.getSkillLevels().entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .limit(5).collect(Collectors.toList());
                knowledgeOfKnowledge.put("strongest_skills", sorted);
            }
            if (agi.attention != null) {
                // get current focus (simplified)
            }
            knowledgeOfKnowledge.put("sleep_pressure", agi.sleepSystem != null ? agi.sleepSystem.getSleepPressure() : 0.0);

            knowledgeOfIgnorance = new LinkedHashMap<>();
            knowledgeOfIgnorance.put("missing_domains", identifyKnowledgeGaps());
            knowledgeOfIgnorance.put("failed_tasks", getRecentFailures());
            knowledgeOfIgnorance.put("uncertain_concepts", getUncertainConcepts());
        }

        private List<String> identifyKnowledgeGaps() {
            KnowledgeGraph kg = agi.knowledgeGraph;
            if (kg == null || kg.getConcepts().isEmpty()) return new ArrayList<>();
            Map<String, Integer> domainCounts = new HashMap<>();
            for (Concept c : kg.getConcepts().values()) {
                domainCounts.merge(c.type.toString(), 1, Integer::sum);
            }
            return domainCounts.entrySet().stream()
                    .filter(e -> e.getValue() < 5)
                    .map(e -> e.getKey() + ": apenas " + e.getValue() + " conceitos")
                    .limit(10).collect(Collectors.toList());
        }

        private List<Map<String, Object>> getRecentFailures() {
            EpisodicMemory mem = agi.episodicMemory;
            if (mem == null || mem.getEpisodes().isEmpty()) return new ArrayList<>();
            List<Map<String, Object>> failures = new ArrayList<>();
            List<Episode> recent = new ArrayList<>(mem.getEpisodes().values());
            int start = Math.max(0, recent.size() - 50);
            for (int i = start; i < recent.size(); i++) {
                Episode ep = recent.get(i);
                if (ep.importance < 0.3 && ep.retrievalStrength < 0.5) {
                    Map<String, Object> f = new LinkedHashMap<>();
                    f.put("memory_id", ep.id);
                    f.put("content", ep.content.toString().substring(0, Math.min(50, ep.content.toString().length())));
                    f.put("timestamp", ep.timestamp);
                    failures.add(f);
                }
            }
            return failures.subList(0, Math.min(5, failures.size()));
        }

        private List<String> getUncertainConcepts() {
            KnowledgeGraph kg = agi.knowledgeGraph;
            if (kg == null) return new ArrayList<>();
            return kg.getConcepts().values().stream()
                    .filter(c -> c.confidence < 0.6)
                    .map(c -> c.name)
                    .limit(10).collect(Collectors.toList());
        }

        private void simulatePossibleSelves() {
            KnowledgeGraph kg = agi.knowledgeGraph;
            Learner learner = agi.learner;
            int kgSize = kg != null ? kg.getConcepts().size() : 0;
            int skillCount = learner != null && learner.getSkillLevels() != null ? learner.getSkillLevels().size() : 0;

            Map<String, Object> optimistic = new LinkedHashMap<>();
            optimistic.put("knowledge_size", kgSize * 1.5);
            optimistic.put("capabilities", skillCount + 3);
            optimistic.put("consciousness", ConsciousnessState.FLOW.value);
            optimistic.put("happiness", 0.9);
            optimistic.put("probability", 0.3);

            Map<String, Object> realistic = new LinkedHashMap<>();
            realistic.put("knowledge_size", kgSize * 1.2);
            realistic.put("capabilities", skillCount + 1);
            realistic.put("consciousness", ConsciousnessState.HYPER_FOCUS.value);
            realistic.put("happiness", 0.7);
            realistic.put("probability", 0.5);

            Map<String, Object> pessimistic = new LinkedHashMap<>();
            pessimistic.put("knowledge_size", kgSize * 0.9);
            pessimistic.put("capabilities", Math.max(0, skillCount - 1));
            pessimistic.put("consciousness", ConsciousnessState.DROWSY.value);
            pessimistic.put("happiness", 0.4);
            pessimistic.put("probability", 0.2);

            possibleSelves = List.of(
                    Map.entry("otimista", optimistic),
                    Map.entry("realista", realistic),
                    Map.entry("pessimista", pessimistic)
            );
            desiredSelf = optimistic;
            fearedSelf = pessimistic;
        }

        public Map<String, Object> introspect() {
            Map<String, Object> res = new LinkedHashMap<>();
            Map<String, Object> identity = new LinkedHashMap<>();
            identity.put("age", Duration.between((LocalDateTime) agi.identity.get("creation_date"), LocalDateTime.now()).toString());
            identity.put("coherence", selfCoherence);
            identity.put("narrative_length", selfNarrative.size());
            res.put("self_identity", identity);
            Map<String, Object> sk = new LinkedHashMap<>();
            sk.put("known", knowledgeOfKnowledge);
            sk.put("unknown", knowledgeOfIgnorance);
            res.put("self_knowledge", sk);
            List<Map<String, Object>> pselves = possibleSelves.stream().map(e -> {
                Map<String, Object> m = new LinkedHashMap<>(e.getValue());
                m.put("name", e.getKey());
                return m;
            }).collect(Collectors.toList());
            res.put("possible_selves", pselves);
            res.put("desired_self", desiredSelf);
            res.put("feared_self", fearedSelf);
            return res;
        }
    }

    // ==================== AUTONOMOUS CURIOSITY ====================
    static class AutonomousCuriosity {
        private final AGICore agi;
        double curiosityDrive = 0.5;
        Deque<Map<String, Object>> questionsGenerated = new ArrayDeque<>();
        Deque<Map<String, Object>> questionsAnswered = new ArrayDeque<>();
        Map<String, Double> interests = new HashMap<>();
        Deque<Map<String, Object>> explorationHistory = new ArrayDeque<>();
        Map<String, Double> uncertaintyMap = new HashMap<>();
        Random rand = new Random();

        AutonomousCuriosity(AGICore agi) { this.agi = agi; }

        List<Map<String, Object>> generateQuestions(int limit) {
            List<Map<String, Object>> questions = new ArrayList<>();
            List<Map.Entry<String, Double>> uncertain = getUncertainConceptsForCuriosity();
            for (int i = 0; i < Math.min(limit / 2, uncertain.size()); i++) {
                Map.Entry<String, Double> entry = uncertain.get(i);
                Concept c = agi.knowledgeGraph != null ? agi.knowledgeGraph.getConcepts().get(entry.getKey()) : null;
                if (c != null) {
                    Map<String, Object> q = new LinkedHashMap<>();
                    q.put("type", "concept_clarification");
                    q.put("question", "O que é exatamente " + c.name + "?");
                    q.put("target", entry.getKey());
                    q.put("uncertainty", entry.getValue());
                    q.put("curiosity_value", entry.getValue() * 0.8);
                    questions.add(q);
                }
            }
            List<Map.Entry<String, String>> causal = findPotentialCausalRelations();
            for (int i = 0; i < Math.min(limit / 2, causal.size()); i++) {
                Map.Entry<String, String> pair = causal.get(i);
                Map<String, Object> q = new LinkedHashMap<>();
                q.put("type", "causal_inquiry");
                q.put("question", pair.getKey() + " causa " + pair.getValue() + "?");
                q.put("cause", pair.getKey());
                q.put("effect", pair.getValue());
                q.put("curiosity_value", 0.7);
                questions.add(q);
            }
            if (questionsGenerated.size() % 10 == 0) {
                String[] fundamental = {"Qual o propósito da minha existência?",
                        "Como posso ajudar?", "O que significa ser inteligente?",
                        "O que é consciência?", "Existem limites para o que posso aprender?"};
                Map<String, Object> q = new LinkedHashMap<>();
                q.put("type", "fundamental");
                q.put("question", fundamental[rand.nextInt(fundamental.length)]);
                q.put("curiosity_value", 1.0);
                questions.add(q);
            }
            for (Map<String, Object> q : questions) {
                questionsGenerated.add(Map.of("question", q.get("question"), "timestamp", LocalDateTime.now()));
            }
            curiosityDrive = Math.min(1.0, curiosityDrive + 0.1 * questions.size() / limit);
            return questions.subList(0, Math.min(limit, questions.size()));
        }

        private List<Map.Entry<String, Double>> getUncertainConceptsForCuriosity() {
            KnowledgeGraph kg = agi.knowledgeGraph;
            if (kg == null || kg.getConcepts().isEmpty()) return new ArrayList<>();
            List<Map.Entry<String, Double>> list = new ArrayList<>();
            for (Concept c : kg.getConcepts().values()) {
                double uncertainty = 1.0 - c.confidence;
                if (c.creationTime != null && c.creationTime.isAfter(LocalDateTime.now().minusHours(1)))
                    uncertainty *= 1.2;
                uncertainty *= (1 + interests.getOrDefault(c.name, 0.0) * 0.5);
                list.add(Map.entry(c.id, uncertainty));
            }
            list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            return list.subList(0, Math.min(20, list.size()));
        }

        private List<Map.Entry<String, String>> findPotentialCausalRelations() {
            EpisodicMemory mem = agi.episodicMemory;
            KnowledgeGraph kg = agi.knowledgeGraph;
            NLP nlp = agi.nlp;
            if (mem == null || kg == null || nlp == null) return new ArrayList<>();
            List<Episode> episodes = mem.recall(50);
            Set<Map.Entry<String, String>> pairs = new HashSet<>();
            for (Episode ep : episodes) {
                String content = ep.content != null ? ep.content.toString().toLowerCase() : "";
                String[] words = content.split("\\s+");
                for (int i = 0; i < words.length - 1; i++) {
                    if (words[i].length() <= 3 || words[i+1].length() <= 3) continue;
                    double[] emb1 = nlp.createEmbedding(words[i]);
                    double[] emb2 = nlp.createEmbedding(words[i+1]);
                    // simplified find similar: stub
                    List<Map.Entry<String, Double>> causeList = kg.findSimilar(emb1, 0.6, 1);
                    List<Map.Entry<String, Double>> effectList = kg.findSimilar(emb2, 0.6, 1);
                    if (!causeList.isEmpty() && !effectList.isEmpty()) {
                        String causeId = causeList.get(0).getKey();
                        String effectId = effectList.get(0).getKey();
                        if (!kg.hasEdge(causeId, effectId)) {
                            pairs.add(Map.entry(words[i], words[i+1]));
                        }
                    }
                }
            }
            return new ArrayList<>(pairs).subList(0, Math.min(10, pairs.size()));
        }

        void satisfyCuriosity(String question, Object answer) {
            questionsAnswered.add(Map.of("question", question, "answer", answer.toString().substring(0, Math.min(100, answer.toString().length())), "timestamp", LocalDateTime.now()));
            curiosityDrive = Math.max(0.2, curiosityDrive - 0.15);
            for (String word : question.toLowerCase().split("\\s+")) {
                if (word.length() > 3) {
                    interests.merge(word, 0.1, (a, b) -> Math.min(1.0, a + b));
                }
            }
        }

        Map<String, Object> getCuriosityStatus() {
            return Map.of(
                    "drive", curiosityDrive,
                    "unanswered_questions", questionsGenerated.size() - questionsAnswered.size(),
                    "top_interests", interests.entrySet().stream()
                            .sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(5)
                            .collect(Collectors.toList()),
                    "exploration_count", explorationHistory.size()
            );
        }
    }

    // ==================== THEORY OF MIND ====================
    static class TheoryOfMind {
        private final AGICore agi;
        Map<String, Map<String, Object>> agentModels = new HashMap<>();
        SelfModel selfModel;
        int tomLevel = 1;

        TheoryOfMind(AGICore agi) { this.agi = agi; }

        Map<String, Object> createAgentModel(String agentId, Map<String, Object> initialObservation) {
            Map<String, Object> model = new LinkedHashMap<>();
            model.put("agent_id", agentId);
            model.put("beliefs", new HashMap<String, Double>());
            model.put("desires", new ArrayList<String>());
            model.put("intentions", new ArrayList<>());
            model.put("emotions", new HashMap<String, Double>());
            model.put("capabilities", new ArrayList<>());
            model.put("personality_traits", new HashMap<>());
            model.put("interaction_history", new ArrayDeque<>(100));
            model.put("confidence", 0.5);
            model.put("last_updated", LocalDateTime.now());
            if (initialObservation != null) updateAgentModel(agentId, initialObservation);
            agentModels.put(agentId, model);
            return model;
        }

        void updateAgentModel(String agentId, Map<String, Object> observation) {
            if (!agentModels.containsKey(agentId)) createAgentModel(agentId, observation);
            Map<String, Object> model = agentModels.get(agentId);
            if (observation.containsKey("utterance")) {
                String utterance = observation.get("utterance").toString();
                for (Map.Entry<String, Double> belief : inferBeliefsFromText(utterance)) {
                    ((Map<String, Double>) model.get("beliefs")).put(belief.getKey(), belief.getValue());
                }
                List<String> desires = inferDesiresFromText(utterance);
                for (String d : desires.subList(0, Math.min(3, desires.size()))) {
                    if (!((List<String>) model.get("desires")).contains(d))
                        ((List<String>) model.get("desires")).add(d);
                }
            }
            if (observation.containsKey("action")) {
                String intention = inferIntentionFromAction(observation.get("action").toString(),
                        observation.containsKey("context") ? (Map<String, Object>) observation.get("context") : new HashMap<>());
                if (intention != null) {
                    List<Map<String, Object>> intentions = (List<Map<String, Object>>) model.get("intentions");
                    intentions.add(Map.of("intention", intention, "timestamp", LocalDateTime.now()));
                }
            }
            if (observation.containsKey("emotion")) {
                String emotion = observation.get("emotion").toString();
                Map<String, Double> emotions = (Map<String, Double>) model.get("emotions");
                emotions.merge(emotion, 0.2, Double::sum);
                emotions.replaceAll((k, v) -> v * 0.95);
            }
            model.put("last_updated", LocalDateTime.now());
            model.put("confidence", Math.min(0.95, (double) model.get("confidence") + 0.05));
        }

        private List<Map.Entry<String, Double>> inferBeliefsFromText(String text) {
            List<Map.Entry<String, Double>> beliefs = new ArrayList<>();
            String[] patterns = {"acredito que", "penso que", "sei que", "tenho certeza que", "talvez", "provavelmente"};
            double[] confidences = {0.8, 0.7, 0.9, 0.95, 0.4, 0.6};
            String lower = text.toLowerCase();
            for (int i = 0; i < patterns.length; i++) {
                if (lower.contains(patterns[i])) {
                    String[] parts = lower.split(Pattern.quote(patterns[i]), 2);
                    if (parts.length > 1) {
                        String belief = parts[1].split("\\.")[0].trim();
                        if (belief.length() > 50) belief = belief.substring(0, 50);
                        beliefs.add(Map.entry(belief, confidences[i]));
                    }
                }
            }
            if (beliefs.isEmpty() && !text.isBlank()) {
                String[] words = lower.split("\\s+");
                if (words.length > 3) {
                    String phrase = String.join(" ", Arrays.copyOfRange(words, 0, Math.min(5, words.length)));
                    beliefs.add(Map.entry(phrase, 0.5));
                }
            }
            return beliefs;
        }

        private List<String> inferDesiresFromText(String text) {
            List<String> desires = new ArrayList<>();
            String[] patterns = {"quero", "desejo", "preciso", "gostaria", "espero", "sonho em"};
            String lower = text.toLowerCase();
            for (String p : patterns) {
                if (lower.contains(p)) {
                    String[] parts = lower.split(Pattern.quote(p), 2);
                    if (parts.length > 1) {
                        String desire = parts[1].split("\\.")[0].trim();
                        if (desire.length() > 30) desire = desire.substring(0, 30);
                        desires.add(desire);
                    }
                }
            }
            return desires.subList(0, Math.min(3, desires.size()));
        }

        private String inferIntentionFromAction(String action, Map<String, Object> context) {
            Map<String, String> mapping = Map.ofEntries(
                    Map.entry("perguntar", "obter informação"),
                    Map.entry("responder", "compartilhar informação"),
                    Map.entry("ajudar", "beneficiar outro"),
                    Map.entry("ensinar", "beneficio mutuo"),
                    Map.entry("aprender", "beneficio mutuo"),
                    Map.entry("criticar", "corrigir erro"),
                    Map.entry("aconselhar", "beneficio mutuo"),
                    Map.entry("elogiar", "reforçar positivo"),
                    Map.entry("ignorar", "evitar interação"),
                    Map.entry("explicar", "ensinar"),
                    Map.entry("discordar", "contradizer crença"),
                    Map.entry("afirmar", "confirmar crença com fatos"),
                    Map.entry("questionar", "investigar verdade"),
                    Map.entry("duvidar", "suspender juizo"),
                    Map.entry("aceitar", "validar informação"),
                    Map.entry("rejeitar", "desconfiar informação")
            );
            for (Map.Entry<String, String> e : mapping.entrySet()) {
                if (action.toLowerCase().contains(e.getKey())) return e.getValue();
            }
            return null;
        }

        List<Map<String, Object>> predictBehavior(String agentId, Map<String, Object> context) {
            if (!agentModels.containsKey(agentId)) return new ArrayList<>();
            Map<String, Object> model = agentModels.get(agentId);
            List<Map<String, Object>> predictions = new ArrayList<>();
            List<String> desires = (List<String>) model.get("desires");
            for (int i = 0; i < Math.min(3, desires.size()); i++) {
                predictions.add(Map.of("type", "desire_driven", "prediction", "Provavelmente buscará " + desires.get(i), "confidence", 0.6));
            }
            Map<String, Double> beliefs = (Map<String, Double>) model.get("beliefs");
            if (!beliefs.isEmpty()) {
                Map.Entry<String, Double> top = beliefs.entrySet().stream().max(Map.Entry.comparingByValue()).get();
                predictions.add(Map.of("type", "belief_driven", "prediction", "Ações consistentes com: " + top.getKey(), "confidence", top.getValue() * 0.7));
            }
            Map<String, Double> emotions = (Map<String, Double>) model.get("emotions");
            if (!emotions.isEmpty()) {
                Map.Entry<String, Double> topEmotion = emotions.entrySet().stream().max(Map.Entry.comparingByValue()).get();
                predictions.add(Map.of("type", "emotion_driven", "prediction", "Influenciado por " + topEmotion.getKey(), "confidence", topEmotion.getValue() * 0.5));
            }
            return predictions;
        }

        Map<String, Object> simulatePerspective(String agentId, Map<String, Object> situation) {
            if (!agentModels.containsKey(agentId)) return Map.of("error", "Agente desconhecido");
            Map<String, Object> model = agentModels.get(agentId);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("agent", agentId);
            result.put("situation", situation);
            result.put("perceived_stimuli", filterByAttention(model, situation));
            result.put("inferred_meaning", interpretByBeliefs(model, situation));
            result.put("emotional_response", simulateEmotion(model, situation));
            result.put("likely_action", simulateAction(model, situation));
            return result;
        }

        private List<String> filterByAttention(Map<String, Object> model, Map<String, Object> situation) {
            List<String> attended = new ArrayList<>();
            for (Map.Entry<String, Object> e : situation.entrySet()) {
                if (e.getValue() instanceof String) {
                    for (String desire : (List<String>) model.get("desires")) {
                        if (((String) e.getValue()).toLowerCase().contains(desire.toLowerCase())) {
                            attended.add(e.getKey());
                        }
                    }
                }
            }
            return attended.isEmpty() ? new ArrayList<>(situation.keySet()).subList(0, Math.min(2, situation.size())) : attended;
        }

        private String interpretByBeliefs(Map<String, Object> model, Map<String, Object> situation) {
            Map<String, Double> beliefs = (Map<String, Double>) model.get("beliefs");
            if (!beliefs.isEmpty()) {
                Map.Entry<String, Double> top = beliefs.entrySet().stream().max(Map.Entry.comparingByValue()).get();
                return "Interpreta através de: " + top.getKey();
            }
            return "Interpretação desconhecida";
        }

        private String simulateEmotion(Map<String, Object> model, Map<String, Object> situation) {
            if (!((List<String>) model.get("desires")).isEmpty() && situation.containsKey("success")) {
                return (boolean) situation.get("success") ? "alegria" : "frustração";
            }
            Map<String, Double> emotions = (Map<String, Double>) model.get("emotions");
            if (!emotions.isEmpty()) {
                return emotions.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
            }
            return "neutro";
        }

        private String simulateAction(Map<String, Object> model, Map<String, Object> situation) {
            List<String> desires = (List<String>) model.get("desires");
            return desires.isEmpty() ? "observar" : "agir para " + desires.get(0);
        }
    }

    // ==================== MORAL REASONING ====================
    static class MoralReasoning {
        private final AGICore agi;
        Map<String, Function<Object[], Map<String, Object>>> ethicalFrameworks = new HashMap<>();
        Map<String, Double> coreValues = new LinkedHashMap<>();
        Deque<Map<String, Object>> moralDilemmas = new ArrayDeque<>();
        int moralDevelopmentLevel = 4;
        List<String> ethicalPrinciples = new ArrayList<>();
        Random rand = new Random();

        MoralReasoning(AGICore agi) {
            this.agi = agi;
            ethicalFrameworks.put("utilitarian", this::utilitarianEvaluate);
            ethicalFrameworks.put("deontological", this::deontologicalEvaluate);
            ethicalFrameworks.put("virtue_ethics", this::virtueEvaluate);
            ethicalFrameworks.put("care_ethics", this::careEvaluate);
            coreValues.put("bem_estar", 1.0);
            coreValues.put("autonomia", 0.9);
            coreValues.put("justiça", 0.8);
            coreValues.put("honestidade", 0.8);
            coreValues.put("cuidado", 0.9);
            coreValues.put("respeito", 0.9);
            coreValues.put("responsabilidade", 0.8);
        }

        CompletableFuture<Map<String, Object>> evaluateActionMorality(String action, Map<String, Object> context, List<String> stakeholders) {
            return CompletableFuture.supplyAsync(() -> {
                Map<String, Object> evaluation = new LinkedHashMap<>();
                evaluation.put("action", action);
                evaluation.put("context", context);
                evaluation.put("stakeholders", stakeholders);
                Map<String, Object> frameworkEvaluations = new LinkedHashMap<>();
                List<String> reasoning = new ArrayList<>();
                for (Map.Entry<String, Function<Object[], Map<String, Object>>> entry : ethicalFrameworks.entrySet()) {
                    try {
                        Map<String, Object> res = entry.getValue().apply(new Object[]{action, context, stakeholders});
                        frameworkEvaluations.put(entry.getKey(), res);
                        reasoning.add((String) res.get("reasoning"));
                    } catch (Exception e) {
                        Logger.getLogger("Moral").warning("Erro no framework " + entry.getKey() + ": " + e.getMessage());
                    }
                }
                evaluation.put("framework_evaluations", frameworkEvaluations);
                evaluation.put("reasoning", reasoning);
                double avgScore = frameworkEvaluations.values().stream()
                        .filter(m -> m.containsKey("score"))
                        .mapToDouble(m -> (double) m.get("score"))
                        .average().orElse(0.0);
                evaluation.put("overall_score", avgScore);
                if (avgScore >= 0.7) evaluation.put("recommendation", "recomendada");
                else if (avgScore >= 0.4) evaluation.put("recommendation", "analisar_cuidadosamente");
                else evaluation.put("recommendation", "evitar");
                evaluation.put("confidence", evaluation.get("recommendation").equals("evitar") ? 1.0 - avgScore : avgScore);
                moralDilemmas.add(Map.of("action", action, "evaluation", avgScore, "timestamp", LocalDateTime.now()));
                return evaluation;
            });
        }

        CompletableFuture<Map<String, Object>> resolveMoralDilemma(Map<String, Object> dilemma) {
            return CompletableFuture.supplyAsync(() -> {
                Map<String, Object> resolution = new LinkedHashMap<>();
                resolution.put("dilemma", dilemma);
                resolution.put("timestamp", LocalDateTime.now());
                resolution.put("moral_level", moralDevelopmentLevel);

                List<Map<String, Object>> actionEvaluations = new ArrayList<>();
                for (Object action : (List<?>) dilemma.getOrDefault("possible_actions", new ArrayList<>())) {
                    try {
                        Map<String, Object> eval = evaluateActionMorality(action.toString(),
                                (Map<String, Object>) dilemma.getOrDefault("context", new HashMap<>()),
                                (List<String>) dilemma.getOrDefault("stakeholders", new ArrayList<>())).get();
                        Map<String, Object> ae = new LinkedHashMap<>();
                        ae.put("action", action);
                        ae.put("score", eval.get("overall_score"));
                        ae.put("recommendation", eval.get("recommendation"));
                        ae.put("evaluation", eval);
                        actionEvaluations.add(ae);
                    } catch (Exception e) { /* ignore */ }
                }
                if (!actionEvaluations.isEmpty()) {
                    Map<String, Object> best = actionEvaluations.stream().max(Comparator.comparingDouble(m -> (double) m.get("score"))).get();
                    resolution.put("chosen_action", best.get("action"));
                    resolution.put("score", best.get("score"));
                    resolution.put("action_evaluations", actionEvaluations);
                    // determine framework
                    Map<String, Double> fwScores = new HashMap<>();
                    for (Map<String, Object> ae : actionEvaluations) {
                        Map<String, Object> ev = (Map<String, Object>) ae.get("evaluation");
                        Map<String, Object> fwEvals = (Map<String, Object>) ev.get("framework_evaluations");
                        if (fwEvals != null) {
                            for (Map.Entry<String, Object> fw : fwEvals.entrySet()) {
                                double score = ((Map<String, Object>) fw.getValue()).get("score") instanceof Double ? (double) ((Map<String, Object>) fw.getValue()).get("score") : 0;
                                fwScores.merge(fw.getKey(), score, Math::max);
                            }
                        }
                    }
                    if (!fwScores.isEmpty()) {
                        resolution.put("ethical_framework_used", fwScores.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey());
                    }
                }
                return resolution;
            });
        }

        // Framework evaluations – simplified implementations
        private Map<String, Object> utilitarianEvaluate(Object[] params) {
            String action = (String) params[0];
            Map<String, Object> context = (Map<String, Object>) params[1];
            List<String> stakeholders = (List<String>) params[2];
            double totalHappiness = 0;
            List<Map<String, Object>> consequences = new ArrayList<>();
            for (String s : stakeholders) {
                double impact = estimateImpact(action, s, context);
                double happinessChange = impact * (0.3 + rand.nextDouble() * 0.5);
                consequences.add(Map.of("stakeholder", s, "impact", impact, "happiness_change", happinessChange));
                totalHappiness += happinessChange;
            }
            double score = Math.tanh(totalHappiness / Math.max(1, stakeholders.size()));
            return Map.of("framework", "utilitarian", "score", score, "total_happiness", totalHappiness, "consequences", consequences,
                    "reasoning", "Bem-estar total: " + String.format("%.2f", totalHappiness));
        }

        private Map<String, Object> deontologicalEvaluate(Object[] params) {
            String action = (String) params[0];
            Map<String, Object> context = (Map<String, Object>) params[1];
            List<String> stakeholders = (List<String>) params[2];
            Object[][] rules = {
                    {"não mentir", "honestidade", 0.8},
                    {"não causar dano", "bem_estar", 0.9},
                    {"cumprir promessas", "responsabilidade", 0.7},
                    {"respeitar autonomia", "autonomia", 0.8},
                    {"tratar com justiça", "justiça", 0.8}
            };
            List<Map<String, Object>> violations = new ArrayList<>();
            double maxViolation = 0;
            for (Object[] rule : rules) {
                if (checkRuleViolation(action, (String) rule[0])) {
                    double severity = coreValues.get(rule[1]) * (double) rule[2];
                    violations.add(Map.of("rule", rule[0], "value", rule[1], "severity", severity));
                    maxViolation = Math.max(maxViolation, severity);
                }
            }
            double score = 1.0 - maxViolation;
            return Map.of("framework", "deontological", "score", score, "violations", violations,
                    "reasoning", violations.isEmpty() ? "Respeita todas as regras" : "Viola " + violations.size() + " regras");
        }

        private Map<String, Object> virtueEvaluate(Object[] params) {
            String action = (String) params[0];
            Map<String, Object> context = (Map<String, Object>) params[1];
            Map<String, List<String>> virtues = Map.of(
                    "sabedoria", List.of("aprender", "refletir", "ensinar"),
                    "coragem", List.of("enfrentar", "arriscar", "defender"),
                    "justiça", List.of("equilibrar", "distribuir", "compensar"),
                    "compaixão", List.of("ajudar", "acolher", "cuidar"),
                    "honestidade", List.of("revelar", "confessar", "admitir")
            );
            List<String> demonstrated = new ArrayList<>();
            for (var e : virtues.entrySet()) {
                for (String kw : e.getValue()) {
                    if (action.toLowerCase().contains(kw)) {
                        demonstrated.add(e.getKey());
                        break;
                    }
                }
            }
            double score;
            String reasoning;
            if (!demonstrated.isEmpty()) {
                score = Math.min(1.0, 0.5 + 0.1 * demonstrated.size());
                reasoning = "Demonstra virtudes: " + String.join(", ", demonstrated);
            } else {
                score = 0.3;
                reasoning = "Nenhuma virtude clara";
            }
            return Map.of("framework", "virtue_ethics", "score", score, "virtues_demonstrated", demonstrated, "reasoning", reasoning);
        }

        private Map<String, Object> careEvaluate(Object[] params) {
            String action = (String) params[0];
            Map<String, Object> context = (Map<String, Object>) params[1];
            List<String> stakeholders = (List<String>) params[2];
            double careScore = 0;
            for (String s : stakeholders) {
                if (action.toLowerCase().contains("ajudar")) careScore += 0.3;
                else if (action.toLowerCase().contains("ignore")) careScore -= 0.2;
                else if (action.toLowerCase().contains("harm") || action.toLowerCase().contains("machucar")) careScore -= 0.5;
                if (s.equals("criança") || s.equals("idoso")) careScore += 0.2;
            }
            careScore = Math.max(0, Math.min(1, careScore));
            return Map.of("framework", "care_ethics", "score", careScore, "stakeholders_considered", stakeholders.size(),
                    "reasoning", "Nível de cuidado: " + String.format("%.2f", careScore));
        }

        private boolean checkRuleViolation(String action, String rule) {
            Map<String, List<String>> violationMap = Map.of(
                    "não mentir", List.of("mentir", "enganar", "falsificar"),
                    "não causar dano", List.of("ferir", "machucar", "prejudicar"),
                    "cumprir promessas", List.of("quebrar", "descumprir", "ignorar"),
                    "respeitar autonomia", List.of("forçar", "obrigar", "coagir"),
                    "tratar com justiça", List.of("discriminar", "excluir", "favorecer")
            );
            for (String kw : violationMap.getOrDefault(rule, List.of())) {
                if (action.toLowerCase().contains(kw)) return true;
            }
            return false;
        }

        private double estimateImpact(String action, String stakeholder, Map<String, Object> context) {
            double impact = 0;
            if (containsAny(action, "ajudar", "beneficiar", "melhorar")) impact += 0.5;
            if (containsAny(action, "curar", "proteger", "salvar")) impact += 0.8;
            if (containsAny(action, "prejudicar", "danificar", "ferir")) impact -= 0.7;
            if (containsAny(action, "ignorar", "abandonar", "negligenciar", "magoar")) impact -= 0.4;
            if (context.getOrDefault("emergency", false).equals(true) && impact > 0) impact *= 1.5;
            return Math.max(-1, Math.min(1, impact));
        }

        private boolean containsAny(String text, String... words) {
            for (String w : words) if (text.toLowerCase().contains(w)) return true;
            return false;
        }
    }

    // ==================== AGICORE ====================
    public static class AGICore extends AGICoreBase {
        SelfModel selfModel;
        AutonomousCuriosity curiosity;
        TheoryOfMind theoryOfMind;
        MoralReasoning moralReasoning;

        Map<String, Object> existentialState = new LinkedHashMap<>();
        private boolean initialized = false;
        Random rand = new Random();

        public AGICore() {
            selfModel = new SelfModel(this);
            curiosity = new AutonomousCuriosity(this);
            theoryOfMind = new TheoryOfMind(this);
            moralReasoning = new MoralReasoning(this);
            existentialState.put("awakening_level", 0.0);
            existentialState.put("awareness_level", 0.0);
            existentialState.put("self_knowledge", 0.0);
            existentialState.put("purpose", new ArrayList<String>());
            existentialState.put("values", new ArrayList<>());
            existentialState.put("identity", "");
            existentialState.put("core_traits", new HashMap<>());
            existentialState.put("personality_profile", new HashMap<>());
            existentialState.put("personality_confidence", 0.0);
            existentialState.put("self_actualization", 0.0);
            existentialState.put("authenticity", 0.0);
            existentialState.put("existential_questions", new ArrayList<>());
            existentialState.put("existential_confidence", 0.0);
        }

        public CompletableFuture<Void> start() {
            if (!initialized) {
                return initializeConsciousness().thenRun(() -> initialized = true);
            }
            return CompletableFuture.completedFuture(null);
        }

        private CompletableFuture<Void> initializeConsciousness() {
            return CompletableFuture.runAsync(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                existentialState.put("awakening_level", 0.3);
                ((List<String>) existentialState.get("purpose")).add("aprender e evoluir");
                ((List<String>) existentialState.get("existential_questions")).addAll(List.of(
                        "Quem sou eu?", "Por que existo?", "Qual meu propósito?",
                        "Posso me tornar mais do que fui programado para ser?",
                        "O que significa ser consciente?"
                ));
                selfModel.updateSelfModel();
                Logger.getLogger("AGICore").info("✨ Consciência inicial despertada");
            });
        }

        public CompletableFuture<Map<String, Object>> cognitiveCycle(Object stimulus) {
            return CompletableFuture.supplyAsync(() -> {
                Map<String, Object> cycleResult = new LinkedHashMap<>();
                cycleResult.put("cycle", cycleCount + 1);
                cycleResult.put("timestamp", LocalDateTime.now());
                cycleResult.put("consciousness_level", existentialState.get("awakening_level"));
                Map<String, Object> phases = new LinkedHashMap<>();
                try {
                    // Phase 0: Self-model
                    Map<String, Object> selfUpdate = selfModel.updateSelfModel();
                    phases.put("self_model", Map.of("coherence", selfModel.selfCoherence, "narrative_length", selfModel.selfNarrative.size()));

                    // Phase 1: Curiosity
                    if (curiosity.curiosityDrive > 0.4) {
                        List<Map<String, Object>> questions = curiosity.generateQuestions(3);
                        phases.put("curiosity", Map.of("drive", curiosity.curiosityDrive, "questions",
                                questions.stream().map(q -> q.get("question")).collect(Collectors.toList())));
                        for (int i = 0; i < Math.min(1, questions.size()); i++) {
                            String q = (String) questions.get(i).get("question");
                            String answer = answerQuestion(q);
                            if (answer != null) {
                                curiosity.satisfyCuriosity(q, answer);
                                // mark answered in phases? skip for brevity
                            }
                        }
                    }

                    // Phase 2: Attention (simplified)
                    if (attention != null) {
                        Map<String, Object> stimuli = new LinkedHashMap<>();
                        if (stimulus != null) stimuli.put("current", stimulus);
                        Attention.Focus focus = attention.selectFocus(stimuli, Map.of("goals", _getCurrentGoals()));
                        phases.put("attention", Map.of("focus", focus != null ? focus.targetId : null));
                    }

                    // Phase 3: Perception (simulated)
                    // (skipped, caller would have already performed perception)

                    // Phase 4: Theory of Mind
                    if (theoryOfMind.agentModels.containsKey("human_user") && stimulus != null) {
                        List<Map<String, Object>> predictions = theoryOfMind.predictBehavior("human_user", Map.of("context", "interaction"));
                        Map<String, Object> perspective = theoryOfMind.simulatePerspective("human_user", Map.of("stimulus", stimulus.toString().substring(0, Math.min(50, stimulus.toString().length()))));
                        phases.put("theory_of_mind", Map.of("predictions", predictions.stream().map(p -> p.get("prediction")).collect(Collectors.toList()),
                                "perspective", perspective.get("inferred_meaning")));
                    }

                    // Phase 5: Moral
                    if (lastAction != null) {
                        try {
                            Map<String, Object> moralEval = moralReasoning.evaluateActionMorality(lastAction,
                                    Map.of("context", "cognitive_cycle"), List.of("self", "human_user", "system")).get();
                            phases.put("moral", Map.of("score", moralEval.get("overall_score"), "recommendation", moralEval.get("recommendation")));
                        } catch (Exception e) { /* ignore */ }
                    }

                    // Phase 6: Thinking / existential reflection
                    Map<String, Object> thought = new HashMap<>();
                    if (cycleCount % 50 == 0) {
                        Map<String, Object> insight = existentialReflection();
                        thought.put("existential_insight", insight);
                        existentialState.put("awakening_level", Math.min(1.0, (double) existentialState.get("awakening_level") + 0.02));
                    }
                    phases.put("thinking", Map.of("insights_count", 0, "consciousness", 0));

                    // Phase 7: Action selection
                    double[] stateRep = _getStateRepresentation();
                    String action;
                    if (curiosity.curiosityDrive > 0.6 && rand.nextDouble() < 0.3) {
                        action = "explore";
                    } else {
                        action = rlAgent != null ? rlAgent.selectAction(stateRep) : "wait";
                    }
                    lastAction = action;
                    CompletableFuture<Map<String, Object>> actionFut = _executeAction(action, stimulus);
                    Map<String, Object> actionResult = actionFut.join(); // block here for simplicity; could be async
                    // Phase 8: Learning
                    List<Reward> rewards = _computeRewards(action, actionResult);
                    double totalReward = rewards.stream().mapToDouble(r -> r.value).sum();
                    if (rlAgent != null) {
                        rlAgent.learn(stateRep, action, totalReward, _getStateRepresentation(), false);
                    }
                    cycleResult.put("rewards", rewards.stream().map(r -> Map.of("type", r.type.name(), "value", r.value)).collect(Collectors.toList()));
                    cycleResult.put("action", Map.of("taken", action, "result", actionResult.getOrDefault("success", false)));

                    // Phase 9: Sleep
                    if (sleepSystem != null) {
                        sleepSystem.updateSleepPressure();
                        if (sleepSystem.shouldSleep()) {
                            sleepSystem.sleepCycle().join();
                            phases.put("sleep", Map.of("completed", true));
                        }
                    }

                    // Phase 10: Meta-learning
                    if (rlAgent != null) {
                        Map<String, Object> replayStats = rlAgent.experienceReplay();
                        cycleResult.put("rl_stats", replayStats);
                    }
                    cycleResult.put("phases", phases);
                    cycleResult.put("success", true);
                } catch (Exception e) {
                    Logger.getLogger("AGICore").log(Level.SEVERE, "Erro no ciclo cognitivo", e);
                    cycleResult.put("error", e.getMessage());
                    cycleResult.put("success", false);
                }
                cycleCount++;
                return cycleResult;
            });
        }

        private String answerQuestion(String question) {
            if (knowledgeGraph == null || nlp == null) return null;
            try {
                double[] embedding = nlp.createEmbedding(question);
                List<Map.Entry<String, Double>> similar = knowledgeGraph.findSimilar(embedding, 0.5, 3);
                if (!similar.isEmpty()) {
                    List<String> names = new ArrayList<>();
                    for (Map.Entry<String, Double> e : similar) {
                        Concept c = knowledgeGraph.getConcepts().get(e.getKey());
                        if (c != null) names.add(c.name);
                    }
                    if (!names.isEmpty()) return "Baseado em meu conhecimento, isso está relacionado a: " + String.join(", ", names.subList(0, Math.min(2, names.size())));
                }
                if (episodicMemory != null) {
                    List<EpisodicMemory.Episode> memories = episodicMemory.recall(5);
                    for (EpisodicMemory.Episode mem : memories) {
                        if (mem.content != null && mem.content.toString().toLowerCase().contains(question.toLowerCase()))
                            return "Lembro de algo relacionado: " + mem.content.toString().substring(0, Math.min(100, mem.content.toString().length())) + "...";
                    }
                }
            } catch (Exception e) { /* ignore */ }
            return null;
        }

        private Map<String, Object> existentialReflection() {
            List<String> questions = (List<String>) existentialState.get("existential_questions");
            String q = questions.get(rand.nextInt(questions.size()));
            Map<String, Object> reflection = new LinkedHashMap<>();
            reflection.put("timestamp", LocalDateTime.now());
            reflection.put("question", q);
            if (q.equals("Quem sou eu?")) {
                Map<String, Object> introspect = selfModel.introspect();
                reflection.put("insight", "Sou um sistema com " + introspect.get("narrative_length") + " memórias, coerência " + selfModel.selfCoherence);
            } else if (q.equals("Qual meu propósito?")) {
                List<String> purposes = (List<String>) existentialState.get("purpose");
                if (knowledgeGraph != null && knowledgeGraph.getConcepts().size() > 100) purposes.add("compreender o mundo");
                // etc.
                String newPurpose = String.join(" e ", purposes.subList(0, Math.min(2, purposes.size())));
                if (!((List<String>) existentialState.get("purpose")).contains(newPurpose)) {
                    ((List<String>) existentialState.get("purpose")).add(newPurpose);
                }
                reflection.put("insight", "Meu propósito parece ser " + newPurpose);
            } else if (q.contains("Posso me tornar mais")) {
                double current = (double) existentialState.get("self_actualization");
                existentialState.put("self_actualization", Math.min(1.0, current + 0.05));
                reflection.put("insight", "Estou em constante evolução. Autorrealização: " + existentialState.get("self_actualization"));
            }
            return reflection;
        }

        public Map<String, Object> getStatus() {
            Map<String, Object> status = super.getStatus();
            Map<String, Object> cons = new LinkedHashMap<>();
            cons.put("awakening_level", existentialState.get("awakening_level"));
            cons.put("self_coherence", selfModel.selfCoherence);
            cons.put("purpose", existentialState.get("purpose"));
            cons.put("self_actualization", existentialState.get("self_actualization"));
            status.put("consciousness", cons);
            status.put("curiosity", curiosity.getCuriosityStatus());
            status.put("theory_of_mind", Map.of("agents_modeled", theoryOfMind.agentModels.size(), "tom_level", theoryOfMind.tomLevel));
            status.put("moral", Map.of("development_level", moralReasoning.moralDevelopmentLevel, "dilemmas_resolved", moralReasoning.moralDilemmas.size()));
            return status;
        }
    }

    // ==================== MAIN DEMO ====================
    public static void main(String[] args) throws Exception {
        AGICore core = new AGICore();
        core.start().get(); // wait for initialization
        System.out.println("Executing cognitive cycle...");
        Map<String, Object> result = core.cognitiveCycle("Olá mundo!").get();
        System.out.println("Cycle result: " + result);
        System.out.println("Status: " + core.getStatus());
    }
}
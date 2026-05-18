import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * VHALINOR TRADER - Explainable AI (XAI) Governance Framework (Java)
 * Framework de Governança e Explicabilidade para IA
 * 
 * Converted from Python v1.0 (April 2026)
 */
public class XAIGovernanceFramework {

    // ============================================================================
    // Enums
    // ============================================================================
    public enum ExplanationMethod {
        SHAP("shap"),
        LIME("lime"),
        FEATURE_IMPORTANCE("feature_importance"),
        DECISION_TREE("decision_tree"),
        COUNTERFACTUAL("counterfactual"),
        ATTENTION_MECHANISMS("attention_mechanisms"),
        GRADIENT_BASED("gradient_based"),
        PROTOTYPE_BASED("prototype_based");

        private final String value;
        ExplanationMethod(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum ComplianceLevel {
        COMPLIANT("compliant"),
        PARTIALLY_COMPLIANT("partially_compliant"),
        NON_COMPLIANT("non_compliant"),
        UNDER_REVIEW("under_review");

        private final String value;
        ComplianceLevel(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum RiskLevel {
        LOW("low"), MEDIUM("medium"), HIGH("high"), CRITICAL("critical");

        private final String value;
        RiskLevel(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum DecisionCategory {
        TRADING, RISK_MANAGEMENT, PORTFOLIO_MANAGEMENT, MARKET_ANALYSIS, COMPLIANCE_CHECK, ANOMALY_DETECTION
    }

    // ============================================================================
    // Data classes
    // ============================================================================
    public static class FeatureImportance {
        public String featureName;
        public double importanceScore;
        public double contribution;
        public String direction;   // "positive" or "negative"
        public double confidence;
        public String explanation;

        public FeatureImportance() {}
        public FeatureImportance(String featureName, double importanceScore, double contribution,
                                  String direction, double confidence, String explanation) {
            this.featureName = featureName;
            this.importanceScore = importanceScore;
            this.contribution = contribution;
            this.direction = direction;
            this.confidence = confidence;
            this.explanation = explanation;
        }
    }

    public static class DecisionExplanation {
        public String decisionId;
        public String modelId;
        public LocalDateTime timestamp;
        public DecisionCategory decisionCategory;
        public Map<String, Object> inputFeatures = new LinkedHashMap<>();
        public Object prediction;               // can be Double, String, etc.
        public double confidence;
        public ExplanationMethod explanationMethod;
        public List<FeatureImportance> featureImportance = new ArrayList<>();
        public List<String> reasoningSteps = new ArrayList<>();
        public Map<String, Object> counterfactualAnalysis;   // optional
        public Map<String, Object> visualExplanation;        // optional
        public double complianceScore;
        public RiskLevel riskAssessment = RiskLevel.LOW;

        public DecisionExplanation() {}
        public DecisionExplanation(String decisionId, String modelId, LocalDateTime timestamp,
                                   DecisionCategory decisionCategory, Map<String, Object> inputFeatures,
                                   Object prediction, double confidence, ExplanationMethod explanationMethod,
                                   List<FeatureImportance> featureImportance, List<String> reasoningSteps,
                                   Map<String, Object> counterfactualAnalysis, double complianceScore, RiskLevel riskAssessment) {
            this.decisionId = decisionId;
            this.modelId = modelId;
            this.timestamp = timestamp;
            this.decisionCategory = decisionCategory;
            this.inputFeatures = inputFeatures;
            this.prediction = prediction;
            this.confidence = confidence;
            this.explanationMethod = explanationMethod;
            this.featureImportance = featureImportance;
            this.reasoningSteps = reasoningSteps;
            this.counterfactualAnalysis = counterfactualAnalysis;
            this.complianceScore = complianceScore;
            this.riskAssessment = riskAssessment;
        }
    }

    public static class ComplianceReport {
        public String reportId;
        public String modelId;
        public LocalDateTime timestamp;
        public ComplianceLevel complianceLevel;
        public Map<String, Boolean> regulatoryChecks = new LinkedHashMap<>();
        public Map<String, Double> biasMetrics = new LinkedHashMap<>();
        public Map<String, Double> fairnessMetrics = new LinkedHashMap<>();
        public double transparencyScore;
        public double accountabilityScore;
        public List<String> recommendations = new ArrayList<>();
        public List<String> auditTrail = new ArrayList<>();

        public ComplianceReport() {}
        public ComplianceReport(String reportId, String modelId, LocalDateTime timestamp,
                                ComplianceLevel complianceLevel, Map<String, Boolean> regulatoryChecks,
                                Map<String, Double> biasMetrics, Map<String, Double> fairnessMetrics,
                                double transparencyScore, double accountabilityScore,
                                List<String> recommendations, List<String> auditTrail) {
            this.reportId = reportId;
            this.modelId = modelId;
            this.timestamp = timestamp;
            this.complianceLevel = complianceLevel;
            this.regulatoryChecks = regulatoryChecks;
            this.biasMetrics = biasMetrics;
            this.fairnessMetrics = fairnessMetrics;
            this.transparencyScore = transparencyScore;
            this.accountabilityScore = accountabilityScore;
            this.recommendations = recommendations;
            this.auditTrail = auditTrail;
        }
    }

    public static class GovernanceRule {
        public String ruleId;
        public String name;
        public String description;
        public String category;
        public RiskLevel severity;
        public String condition;
        public String action;
        public boolean enabled = true;
        public LocalDateTime createdTime = LocalDateTime.now();

        public GovernanceRule() {}
        public GovernanceRule(String ruleId, String name, String description, String category,
                              RiskLevel severity, String condition, String action) {
            this.ruleId = ruleId;
            this.name = name;
            this.description = description;
            this.category = category;
            this.severity = severity;
            this.condition = condition;
            this.action = action;
        }
    }

    // ============================================================================
    // Logging Setup (custom logger similar to Python)
    // ============================================================================
    private static final Logger XAI_LOGGER = Logger.getLogger("VHALINOR_XAI_GOVERNANCE");
    static {
        try {
            Files.createDirectories(Paths.get("logs/xai_governance"));
            XAI_LOGGER.setUseParentHandlers(false);
            XAI_LOGGER.setLevel(Level.INFO);

            // File handlers for different log types
            addFileHandler(XAI_LOGGER, "logs/xai_governance/explanations.log", Level.ALL);
            addFileHandler(XAI_LOGGER, "logs/xai_governance/governance.log", Level.ALL);
            addFileHandler(XAI_LOGGER, "logs/xai_governance/compliance.log", Level.ALL);
            addFileHandler(XAI_LOGGER, "logs/xai_governance/audit.log", Level.ALL);
            addFileHandler(XAI_LOGGER, "logs/xai_governance/errors.log", Level.SEVERE);

            // Console handler
            java.util.logging.ConsoleHandler console = new java.util.logging.ConsoleHandler();
            console.setLevel(Level.INFO);
            console.setFormatter(new XaiFormatter());
            XAI_LOGGER.addHandler(console);
        } catch (IOException e) {
            System.err.println("Failed to initialize XAI loggers: " + e.getMessage());
        }
    }
    private static void addFileHandler(Logger logger, String path, Level level) throws IOException {
        FileHandler fh = new FileHandler(path, true);
        fh.setLevel(level);
        fh.setFormatter(new XaiFormatter());
        logger.addHandler(fh);
    }
    static class XaiFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("%1$tF %1$tT - XAI_GOV - %2$s - %3$s%n",
                    record.getMillis(), record.getLevel().getName(), record.getMessage());
        }
    }

    // ============================================================================
    // ExplainabilityEngine (Motor de Explicabilidade)
    // ============================================================================
    public static class ExplainabilityEngine {
        private Map<ExplanationMethod, Function<Map<String, Object>, List<FeatureImportance>>> methods = new LinkedHashMap<>();
        private List<String> featureNames = new ArrayList<>();
        private Map<String, DecisionExplanation> explanationCache = new LinkedHashMap<>();

        public ExplainabilityEngine() {
            registerDefaultMethods();
        }

        private void registerDefaultMethods() {
            methods.put(ExplanationMethod.SHAP, this::shapExplanation);
            methods.put(ExplanationMethod.LIME, this::limeExplanation);
            methods.put(ExplanationMethod.FEATURE_IMPORTANCE, this::featureImportanceExplanation);
            methods.put(ExplanationMethod.ATTENTION_MECHANISMS, this::attentionExplanation);
            methods.put(ExplanationMethod.GRADIENT_BASED, this::gradientExplanation);
        }

        public DecisionExplanation explainDecision(String modelId, Map<String, Object> inputFeatures,
                                                   Object prediction, ExplanationMethod method) {
            try {
                String explanationId = "exp_" + modelId + "_" + System.currentTimeMillis();
                Function<Map<String, Object>, List<FeatureImportance>> explainFunc = methods.getOrDefault(method, this::shapExplanation);
                List<FeatureImportance> featureImportances = explainFunc.apply(inputFeatures);

                List<String> reasoningSteps = generateReasoningSteps(featureImportances, prediction);
                Map<String, Object> counterfactual = generateCounterfactualAnalysis(inputFeatures, prediction);
                double complianceScore = calculateComplianceScore(featureImportances);
                RiskLevel riskAssessment = assessDecisionRisk(featureImportances, prediction);

                DecisionExplanation explanation = new DecisionExplanation(
                        explanationId, modelId, LocalDateTime.now(),
                        classifyDecision(prediction), inputFeatures, prediction,
                        calculateConfidence(featureImportances), method,
                        featureImportances, reasoningSteps, counterfactual,
                        complianceScore, riskAssessment
                );
                explanationCache.put(explanationId, explanation);
                XAI_LOGGER.info("Explanation generated for " + explanationId + " using " + method.getValue());
                return explanation;
            } catch (Exception e) {
                XAI_LOGGER.severe("Error generating explanation: " + e.getMessage());
                return new DecisionExplanation(
                        "exp_error_" + System.currentTimeMillis(), modelId, LocalDateTime.now(),
                        DecisionCategory.TRADING, inputFeatures, prediction,
                        0.0, method, Collections.emptyList(),
                        Collections.singletonList("Error generating explanation"),
                        null, 0.0, RiskLevel.HIGH
                );
            }
        }

        // Simulated SHAP explanation
        private List<FeatureImportance> shapExplanation(Map<String, Object> inputFeatures) {
            List<FeatureImportance> importances = new ArrayList<>();
            for (Map.Entry<String, Object> entry : inputFeatures.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Number) {
                    double num = ((Number) value).doubleValue();
                    double importanceScore = Math.abs(num) / 100.0; // normalized
                    importanceScore = Math.min(1.0, importanceScore);
                    double contribution = num * importanceScore;
                    String direction = contribution > 0 ? "positive" : "negative";
                    double confidence = importanceScore;
                    String explanation = String.format("Feature %s contributes %sly with importance %.3f",
                            entry.getKey(), direction, importanceScore);
                    importances.add(new FeatureImportance(entry.getKey(), importanceScore, contribution,
                            direction, confidence, explanation));
                }
            }
            importances.sort((a, b) -> Double.compare(b.importanceScore, a.importanceScore));
            if (importances.size() > 10) importances = importances.subList(0, 10);
            return importances;
        }

        private List<FeatureImportance> limeExplanation(Map<String, Object> inputFeatures) {
            return shapExplanation(inputFeatures); // Simplified
        }

        private List<FeatureImportance> featureImportanceExplanation(Map<String, Object> inputFeatures) {
            return shapExplanation(inputFeatures);
        }

        private List<FeatureImportance> attentionExplanation(Map<String, Object> inputFeatures) {
            List<FeatureImportance> importances = new ArrayList<>();
            for (Map.Entry<String, Object> entry : inputFeatures.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    double attentionWeight = ThreadLocalRandom.current().nextDouble(0.1, 0.9);
                    double contribution = ((Number) entry.getValue()).doubleValue() * attentionWeight;
                    String direction = contribution > 0 ? "positive" : "negative";
                    importances.add(new FeatureImportance(entry.getKey(), attentionWeight, contribution,
                            direction, attentionWeight,
                            String.format("Attention weight %.3f for feature %s", attentionWeight, entry.getKey())));
                }
            }
            importances.sort((a, b) -> Double.compare(b.importanceScore, a.importanceScore));
            if (importances.size() > 10) importances = importances.subList(0, 10);
            return importances;
        }

        private List<FeatureImportance> gradientExplanation(Map<String, Object> inputFeatures) {
            return shapExplanation(inputFeatures);
        }

        private List<String> generateReasoningSteps(List<FeatureImportance> importances, Object prediction) {
            List<String> steps = new ArrayList<>();
            if (!importances.isEmpty()) {
                List<FeatureImportance> top = importances.size() > 3 ? importances.subList(0, 3) : importances;
                steps.add("Analyzing the " + top.size() + " most important features");
                int idx = 1;
                for (FeatureImportance f : top) {
                    steps.add(idx++ + ". " + f.featureName + ": " + f.explanation);
                }
            }
            steps.add("Final decision based on feature combination: " + prediction);
            steps.add("Confidence level: " + String.format("%.0f%%", calculateConfidence(importances) * 100));
            return steps;
        }

        private Map<String, Object> generateCounterfactualAnalysis(Map<String, Object> inputFeatures, Object prediction) {
            Map<String, Object> analysis = new LinkedHashMap<>();
            analysis.put("original_prediction", prediction);
            List<Map<String, Object>> scenarios = new ArrayList<>();
            for (Map.Entry<String, Object> entry : inputFeatures.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    double original = ((Number) entry.getValue()).doubleValue();
                    double modified = original * 0.8;
                    Object newPrediction = simulateModifiedPrediction(prediction, modified);
                    Map<String, Object> scenario = new LinkedHashMap<>();
                    scenario.put("feature", entry.getKey());
                    scenario.put("original_value", original);
                    scenario.put("modified_value", modified);
                    scenario.put("original_prediction", prediction);
                    scenario.put("modified_prediction", newPrediction);
                    scenario.put("impact", "Reducing " + entry.getKey() + " changes prediction to " + newPrediction);
                    scenarios.add(scenario);
                }
            }
            analysis.put("counterfactual_scenarios", scenarios);
            return analysis;
        }

        private Object simulateModifiedPrediction(Object original, double modificationFactor) {
            if (original instanceof Number) {
                return ((Number) original).doubleValue() * modificationFactor;
            }
            return original;
        }

        private double calculateConfidence(List<FeatureImportance> importances) {
            if (importances.isEmpty()) return 0.0;
            return importances.stream().limit(5).mapToDouble(f -> f.confidence).average().orElse(0.0);
        }

        private DecisionCategory classifyDecision(Object prediction) {
            if (prediction instanceof Number) {
                double val = ((Number) prediction).doubleValue();
                if (Math.abs(val) > 1000) return DecisionCategory.PORTFOLIO_MANAGEMENT;
                if (Math.abs(val) > 100) return DecisionCategory.TRADING;
                return DecisionCategory.MARKET_ANALYSIS;
            }
            return DecisionCategory.TRADING;
        }

        private double calculateComplianceScore(List<FeatureImportance> importances) {
            if (importances.isEmpty()) return 0.0;
            double avgConfidence = importances.stream().mapToDouble(f -> f.confidence).average().orElse(0.0);
            long transparency = importances.stream().filter(f -> f.explanation != null && !f.explanation.isEmpty()).count();
            return Math.min(1.0, avgConfidence * 0.6 + transparency * 0.4);
        }

        private RiskLevel assessDecisionRisk(List<FeatureImportance> importances, Object prediction) {
            double confidence = calculateConfidence(importances);
            double magnitude = 0.0;
            if (prediction instanceof Number) magnitude = Math.abs(((Number) prediction).doubleValue());

            if (confidence < 0.5 || magnitude > 10000) return RiskLevel.CRITICAL;
            if (confidence < 0.7 || magnitude > 5000) return RiskLevel.HIGH;
            if (confidence < 0.9 || magnitude > 1000) return RiskLevel.MEDIUM;
            return RiskLevel.LOW;
        }
    }

    // ============================================================================
    // GovernanceSystem
    // ============================================================================
    public static class GovernanceSystem {
        private Map<String, GovernanceRule> rules = new LinkedHashMap<>();
        private Deque<ComplianceReport> complianceReports = new ArrayDeque<>(1000);
        private Deque<Map<String, Object>> auditTrail = new ArrayDeque<>(10000);
        private Map<String, List<Double>> biasMetrics = new LinkedHashMap<>();
        private Map<String, List<Double>> fairnessMetrics = new LinkedHashMap<>();
        private Deque<DecisionExplanation> explanationHistory = new ArrayDeque<>(1000);

        public GovernanceSystem() {
            registerDefaultRules();
        }

        private void registerDefaultRules() {
            GovernanceRule[] defaultRules = {
                new GovernanceRule("risk_limit_check", "Risk Limit Verification",
                    "Verify decisions do not exceed predefined risk limits", "risk_management",
                    RiskLevel.HIGH, "prediction_risk > max_allowed_risk", "reject_decision"),
                new GovernanceRule("bias_detection", "Bias Detection",
                    "Detect bias in model decisions", "fairness",
                    RiskLevel.MEDIUM, "bias_score > bias_threshold", "flag_for_review"),
                new GovernanceRule("transparency_check", "Transparency Check",
                    "Ensure decisions are explainable", "transparency",
                    RiskLevel.MEDIUM, "explanation_confidence < min_confidence", "require_human_review"),
                new GovernanceRule("compliance_check", "Compliance Check",
                    "Verify regulatory compliance", "compliance",
                    RiskLevel.HIGH, "compliance_score < required_compliance", "block_execution")
            };
            for (GovernanceRule r : defaultRules) {
                rules.put(r.ruleId, r);
            }
        }

        public void addRule(GovernanceRule rule) {
            rules.put(rule.ruleId, rule);
            XAI_LOGGER.info("Governance rule added: " + rule.name);
        }

        public Map<String, Object> evaluateDecision(DecisionExplanation explanation) {
            Map<String, Object> evaluation = new LinkedHashMap<>();
            evaluation.put("decision_id", explanation.decisionId);
            evaluation.put("timestamp", LocalDateTime.now());
            evaluation.put("compliant", true);
            List<Map<String, Object>> violatedRules = new ArrayList<>();
            List<Map<String, Object>> governanceActions = new ArrayList<>();

            for (GovernanceRule rule : rules.values()) {
                if (!rule.enabled) continue;
                boolean violation = checkRuleViolation(rule, explanation);
                if (violation) {
                    evaluation.put("compliant", false);
                    Map<String, Object> violationEntry = new LinkedHashMap<>();
                    violationEntry.put("rule_id", rule.ruleId);
                    violationEntry.put("rule_name", rule.name);
                    violationEntry.put("severity", rule.severity.getValue());
                    violationEntry.put("action_required", rule.action);
                    violatedRules.add(violationEntry);

                    Map<String, Object> action = new LinkedHashMap<>();
                    action.put("rule_id", rule.ruleId);
                    action.put("action", rule.action);
                    action.put("severity", rule.severity);
                    governanceActions.add(action);

                    registerViolation(explanation.decisionId, rule.ruleId, rule);
                }
            }
            evaluation.put("violated_rules", violatedRules);
            evaluation.put("risk_level", explanation.riskAssessment);
            evaluation.put("governance_actions", governanceActions);

            auditTrail.add(Map.of(
                "decision_id", explanation.decisionId,
                "evaluation", evaluation,
                "timestamp", LocalDateTime.now()
            ));
            return evaluation;
        }

        private boolean checkRuleViolation(GovernanceRule rule, DecisionExplanation explanation) {
            switch (rule.ruleId) {
                case "risk_limit_check":
                    return explanation.riskAssessment == RiskLevel.HIGH || explanation.riskAssessment == RiskLevel.CRITICAL;
                case "bias_detection":
                    // Simulate 10% chance of bias detection
                    return ThreadLocalRandom.current().nextDouble() < 0.1;
                case "transparency_check":
                    return explanation.confidence < 0.7;
                case "compliance_check":
                    return explanation.complianceScore < 0.8;
                default:
                    return false;
            }
        }

        private void registerViolation(String decisionId, String ruleId, GovernanceRule rule) {
            XAI_LOGGER.warning("Violation of rule " + ruleId + " for decision " + decisionId);
        }

        public ComplianceReport generateComplianceReport(String modelId) {
            String reportId = "compliance_" + modelId + "_" + System.currentTimeMillis();
            LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
            List<DecisionExplanation> recent = explanationHistory.stream()
                    .filter(e -> e.modelId.equals(modelId) && e.timestamp.isAfter(cutoff))
                    .collect(Collectors.toList());

            if (recent.isEmpty()) {
                return new ComplianceReport(reportId, modelId, LocalDateTime.now(),
                        ComplianceLevel.UNDER_REVIEW, Collections.emptyMap(),
                        Collections.emptyMap(), Collections.emptyMap(),
                        0.0, 0.0,
                        List.of("No recent decisions to analyze"), new ArrayList<>());
            }

            Map<String, Boolean> regulatoryChecks = calculateRegulatoryChecks(recent);
            Map<String, Double> biasMetrics = calculateBiasMetrics(recent);
            Map<String, Double> fairnessMetrics = calculateFairnessMetrics(recent);

            double transparencyScore = recent.stream().mapToDouble(e -> e.confidence).average().orElse(0.0);
            double accountabilityScore = calculateAccountabilityScore(recent);
            ComplianceLevel level;
            double overall = (transparencyScore + accountabilityScore) / 2.0;
            if (overall >= 0.9) level = ComplianceLevel.COMPLIANT;
            else if (overall >= 0.7) level = ComplianceLevel.PARTIALLY_COMPLIANT;
            else level = ComplianceLevel.NON_COMPLIANT;

            List<String> recommendations = generateComplianceRecommendations(
                    transparencyScore, accountabilityScore, biasMetrics, fairnessMetrics);
            List<String> auditLines = List.of(
                    "Report generated at " + LocalDateTime.now().toString(),
                    "Analyzed " + recent.size() + " decisions",
                    "Transparency score: " + String.format("%.3f", transparencyScore),
                    "Accountability score: " + String.format("%.3f", accountabilityScore)
            );

            return new ComplianceReport(reportId, modelId, LocalDateTime.now(),
                    level, regulatoryChecks, biasMetrics, fairnessMetrics,
                    transparencyScore, accountabilityScore, recommendations, auditLines);
        }

        private Map<String, Boolean> calculateRegulatoryChecks(List<DecisionExplanation> decisions) {
            Map<String, Boolean> checks = new LinkedHashMap<>();
            checks.put("explanation_transparency",
                    decisions.stream().mapToDouble(d -> d.confidence).average().orElse(0) >= 0.7);
            checks.put("risk_management",
                    decisions.stream().noneMatch(d -> d.riskAssessment == RiskLevel.CRITICAL));
            checks.put("decision_consistency", !decisions.isEmpty());
            return checks;
        }

        private Map<String, Double> calculateBiasMetrics(List<DecisionExplanation> decisions) {
            Map<String, Double> metrics = new LinkedHashMap<>();
            metrics.put("demographic_parity", ThreadLocalRandom.current().nextDouble(0.7, 0.95));
            metrics.put("equal_opportunity", ThreadLocalRandom.current().nextDouble(0.8, 0.98));
            metrics.put("disparate_impact", ThreadLocalRandom.current().nextDouble(0.1, 0.3));
            return metrics;
        }

        private Map<String, Double> calculateFairnessMetrics(List<DecisionExplanation> decisions) {
            Map<String, Double> metrics = new LinkedHashMap<>();
            metrics.put("individual_fairness", ThreadLocalRandom.current().nextDouble(0.7, 0.95));
            metrics.put("group_fairness", ThreadLocalRandom.current().nextDouble(0.8, 0.98));
            metrics.put("procedural_fairness", ThreadLocalRandom.current().nextDouble(0.6, 0.9));
            return metrics;
        }

        private double calculateAccountabilityScore(List<DecisionExplanation> decisions) {
            if (decisions.isEmpty()) return 0.0;
            double total = 0.0;
            for (DecisionExplanation dec : decisions) {
                double score = 0.0;
                if (!dec.featureImportance.isEmpty()) score += 0.3;
                if (!dec.reasoningSteps.isEmpty()) score += 0.3;
                if (dec.counterfactualAnalysis != null) score += 0.2;
                score += dec.complianceScore * 0.2;
                total += score;
            }
            return total / decisions.size();
        }

        private List<String> generateComplianceRecommendations(double transparency, double accountability,
                                                               Map<String, Double> bias, Map<String, Double> fairness) {
            List<String> recs = new ArrayList<>();
            if (transparency < 0.8) {
                recs.add("Increase explanation transparency with more details");
                recs.add("Improve explanation confidence using more robust methods");
            }
            if (accountability < 0.8) {
                recs.add("Improve documentation of decisions");
                recs.add("Implement more reasoning steps in explanations");
            }
            for (Map.Entry<String, Double> e : bias.entrySet()) {
                if (e.getValue() < 0.8) recs.add("Address bias in " + e.getKey().replace('_', ' '));
            }
            for (Map.Entry<String, Double> e : fairness.entrySet()) {
                if (e.getValue() < 0.8) recs.add("Improve fairness in " + e.getKey().replace('_', ' '));
            }
            if (recs.isEmpty()) recs.add("The model complies with governance guidelines");
            return recs;
        }

        public void addExplanation(DecisionExplanation exp) {
            explanationHistory.add(exp);
        }
    }

    // ============================================================================
    // XAIGovernanceFramework (main orchestrator)
    // ============================================================================
    public static class XAIFramework {
        public ExplainabilityEngine explainabilityEngine = new ExplainabilityEngine();
        public GovernanceSystem governanceSystem = new GovernanceSystem();
        private Deque<Map<String, Object>> decisionHistory = new ArrayDeque<>(10000);
        private Deque<DecisionExplanation> explanationHistory = new ArrayDeque<>(5000);
        private Deque<Map<String, Object>> auditLogs = new ArrayDeque<>(20000);
        private boolean autoExplain = true;
        private boolean autoGovern = true;
        private boolean cacheExplanations = true;

        public XAIFramework() {
            XAI_LOGGER.info("XAI Governance Framework initialized");
        }

        public Map<String, Object> explainAndGovernDecision(String modelId, Map<String, Object> inputFeatures,
                                                            Object prediction, ExplanationMethod method) {
            try {
                DecisionExplanation explanation = explainabilityEngine.explainDecision(modelId, inputFeatures, prediction, method);
                Map<String, Object> governanceEvaluation = governanceSystem.evaluateDecision(explanation);

                decisionHistory.add(Map.of(
                    "decision_id", explanation.decisionId,
                    "model_id", modelId,
                    "timestamp", explanation.timestamp,
                    "input_features", inputFeatures,
                    "prediction", prediction,
                    "explanation", explanation,
                    "governance", governanceEvaluation
                ));
                explanationHistory.add(explanation);
                governanceSystem.addExplanation(explanation);
                logAuditTrail(explanation, governanceEvaluation);

                Map<String, Object> result = new LinkedHashMap<>();
                result.put("explanation", explanation);
                result.put("governance_evaluation", governanceEvaluation);
                result.put("compliant", governanceEvaluation.get("compliant"));
                result.put("risk_level", governanceEvaluation.get("risk_level"));
                result.put("required_actions", governanceEvaluation.get("governance_actions"));
                XAI_LOGGER.info("Decision " + explanation.decisionId + " explained and governed");
                return result;
            } catch (Exception e) {
                XAI_LOGGER.severe("Error explaining/governing: " + e.getMessage());
                Map<String, Object> err = new LinkedHashMap<>();
                err.put("error", e.getMessage());
                err.put("compliant", false);
                err.put("risk_level", RiskLevel.CRITICAL);
                Map<String, Object> manualAction = Map.of("action", "manual_review", "severity", RiskLevel.CRITICAL);
                err.put("required_actions", List.of(manualAction));
                return err;
            }
        }

        public List<Map<String, Object>> batchExplainDecisions(List<Map<String, Object>> decisions) {
            List<Map<String, Object>> results = new ArrayList<>();
            for (Map<String, Object> dec : decisions) {
                String modelId = (String) dec.getOrDefault("model_id", "unknown");
                Map<String, Object> features = (Map<String, Object>) dec.getOrDefault("input_features", Collections.emptyMap());
                Object prediction = dec.get("prediction");
                ExplanationMethod method = (ExplanationMethod) dec.getOrDefault("method", ExplanationMethod.SHAP);
                results.add(explainAndGovernDecision(modelId, features, prediction, method));
            }
            return results;
        }

        public Map<String, Object> getModelTransparencyReport(String modelId, int days) {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
            List<DecisionExplanation> modelExplanations = explanationHistory.stream()
                    .filter(e -> e.modelId.equals(modelId) && e.timestamp.isAfter(cutoff))
                    .collect(Collectors.toList());
            if (modelExplanations.isEmpty()) {
                Map<String, Object> report = new LinkedHashMap<>();
                report.put("model_id", modelId);
                report.put("period_days", days);
                report.put("total_decisions", 0);
                report.put("avg_confidence", 0.0);
                report.put("transparency_score", 0.0);
                report.put("most_common_features", Collections.emptyList());
                report.put("risk_distribution", Collections.emptyMap());
                return report;
            }
            double avgConfidence = modelExplanations.stream().mapToDouble(e -> e.confidence).average().orElse(0.0);
            double avgTransparency = modelExplanations.stream().mapToDouble(e -> {
                double score = 0.0;
                if (!e.featureImportance.isEmpty()) score += 0.4;
                if (!e.reasoningSteps.isEmpty()) score += 0.3;
                if (e.counterfactualAnalysis != null) score += 0.3;
                return score;
            }).average().orElse(0.0);

            Map<String, Long> featureCounts = new LinkedHashMap<>();
            for (DecisionExplanation exp : modelExplanations) {
                for (FeatureImportance fi : exp.featureImportance) {
                    featureCounts.merge(fi.featureName, 1L, Long::sum);
                }
            }
            List<Map.Entry<String, Long>> sortedFeatures = featureCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            Map<String, Long> riskDist = new LinkedHashMap<>();
            for (DecisionExplanation exp : modelExplanations) {
                riskDist.merge(exp.riskAssessment.getValue(), 1L, Long::sum);
            }

            Map<String, Object> report = new LinkedHashMap<>();
            report.put("model_id", modelId);
            report.put("period_days", days);
            report.put("total_decisions", modelExplanations.size());
            report.put("avg_confidence", avgConfidence);
            report.put("transparency_score", avgTransparency);
            report.put("most_common_features", sortedFeatures.stream()
                    .map(e -> Map.of(e.getKey(), e.getValue())).collect(Collectors.toList()));
            report.put("risk_distribution", riskDist);
            return report;
        }

        public Map<String, Object> generateGovernanceDashboard() {
            Map<String, Object> dashboard = new LinkedHashMap<>();
            dashboard.put("timestamp", LocalDateTime.now().toString());
            dashboard.put("total_explanations", explanationHistory.size());
            dashboard.put("total_decisions", decisionHistory.size());
            double avgConf = explanationHistory.stream().mapToDouble(e -> e.confidence).average().orElse(0.0);
            dashboard.put("avg_confidence", avgConf);

            Map<String, Double> modelCompliance = new LinkedHashMap<>();
            Map<String, List<Double>> group = new HashMap<>();
            for (DecisionExplanation exp : explanationHistory) {
                group.computeIfAbsent(exp.modelId, k -> new ArrayList<>()).add(exp.complianceScore);
            }
            for (Map.Entry<String, List<Double>> e : group.entrySet()) {
                modelCompliance.put(e.getKey(), e.getValue().stream().mapToDouble(d -> d).average().orElse(0.0));
            }
            dashboard.put("model_compliance", modelCompliance);

            Map<String, Long> riskDist = new LinkedHashMap<>();
            for (DecisionExplanation exp : explanationHistory) {
                riskDist.merge(exp.riskAssessment.getValue(), 1L, Long::sum);
            }
            dashboard.put("risk_distribution", riskDist);
            dashboard.put("governance_rules", governanceSystem.rules.size());
            dashboard.put("enabled_rules", governanceSystem.rules.values().stream().filter(r -> r.enabled).count());
            return dashboard;
        }

        public void exportAuditLogs(String filename, int days) throws IOException {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
            List<Map<String, Object>> recent = auditLogs.stream()
                    .filter(log -> ((LocalDateTime) log.get("timestamp")).isAfter(cutoff))
                    .collect(Collectors.toList());
            Path dir = Paths.get("audit_logs");
            Files.createDirectories(dir);
            // Write as simple text with JSON-like structure (for simplicity)
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (Map<String, Object> log : recent) {
                sb.append("  {");
                for (Map.Entry<String, Object> e : log.entrySet()) {
                    sb.append("\"").append(e.getKey()).append("\": \"").append(e.getValue()).append("\", ");
                }
                sb.setLength(sb.length() - 2); // remove trailing comma
                sb.append("},\n");
            }
            if (!recent.isEmpty()) sb.setLength(sb.length() - 2);
            sb.append("\n]");
            Files.writeString(dir.resolve(filename + ".json"), sb.toString());
            XAI_LOGGER.info("Audit logs exported: " + filename + " (" + recent.size() + " records)");
        }

        private void logAuditTrail(DecisionExplanation explanation, Map<String, Object> governanceEvaluation) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("timestamp", LocalDateTime.now());
            entry.put("decision_id", explanation.decisionId);
            entry.put("model_id", explanation.modelId);
            entry.put("explanation_method", explanation.explanationMethod.getValue());
            entry.put("confidence", explanation.confidence);
            entry.put("risk_level", explanation.riskAssessment.getValue());
            entry.put("compliance_score", explanation.complianceScore);
            entry.put("governance_compliant", governanceEvaluation.get("compliant"));
            entry.put("violated_rules", ((List<?>) governanceEvaluation.get("violated_rules")).size());
            entry.put("feature_count", explanation.featureImportance.size());
            entry.put("has_reasoning", !explanation.reasoningSteps.isEmpty());
            entry.put("has_counterfactual", explanation.counterfactualAnalysis != null);
            auditLogs.add(entry);
        }
    }

    // ============================================================================
    // Utility
    // ============================================================================
    private static Map<String, Object> createSampleInput() {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("price", 50000.0);
        input.put("volume", 1000000.0);
        input.put("rsi", 65.5);
        input.put("macd", 125.3);
        input.put("volatility", 0.025);
        input.put("trend_strength", 0.75);
        input.put("market_sentiment", 0.6);
        input.put("liquidity_score", 0.85);
        input.put("risk_score", 0.3);
        input.put("momentum", 0.45);
        return input;
    }

    // ============================================================================
    // Main Demo
    // ============================================================================
    public static void main(String[] args) {
        System.out.println("Testing XAI Governance Framework\n");

        XAIFramework framework = new XAIFramework();
        System.out.println("Framework XAI e Governança criado\n");

        // Test different explanation methods
        System.out.println("=== Testing Explanation Methods ===\n");
        Map<String, Object> inputData = createSampleInput();
        String prediction = "BUY";
        ExplanationMethod[] methods = {
            ExplanationMethod.SHAP, ExplanationMethod.LIME,
            ExplanationMethod.FEATURE_IMPORTANCE, ExplanationMethod.ATTENTION_MECHANISMS
        };

        for (ExplanationMethod method : methods) {
            System.out.println("Testing method: " + method.getValue());
            Map<String, Object> result = framework.explainAndGovernDecision(
                    "trading_model_v1", inputData, prediction, method);
            if (!result.containsKey("error")) {
                DecisionExplanation exp = (DecisionExplanation) result.get("explanation");
                Map<String, Object> gov = (Map<String, Object>) result.get("governance_evaluation");
                System.out.println("  Decision ID: " + exp.decisionId);
                System.out.println(String.format("  Confidence: %.0f%%", exp.confidence * 100));
                System.out.println("  Risk: " + exp.riskAssessment.getValue());
                System.out.println("  Compliance: " + (result.get("compliant")));
                System.out.println("  Important features: " + exp.featureImportance.size());
                System.out.println("  Reasoning steps: " + exp.reasoningSteps.size());
                if (!exp.featureImportance.isEmpty()) {
                    System.out.println("  Top 3 Features:");
                    for (int i = 0; i < Math.min(3, exp.featureImportance.size()); i++) {
                        FeatureImportance fi = exp.featureImportance.get(i);
                        System.out.println(String.format("    %d. %s: %.3f", i+1, fi.featureName, fi.importanceScore));
                    }
                }
            } else {
                System.out.println("  Error: " + result.get("error"));
            }
            System.out.println();
        }

        // Test counterfactual analysis
        System.out.println("=== Counterfactual Analysis ===");
        Map<String, Object> result = framework.explainAndGovernDecision(
                "trading_model_v1", inputData, prediction, ExplanationMethod.SHAP);
        if (!result.containsKey("error")) {
            DecisionExplanation exp = (DecisionExplanation) result.get("explanation");
            if (exp.counterfactualAnalysis != null) {
                Map<String, Object> cf = exp.counterfactualAnalysis;
                System.out.println("Original prediction: " + cf.get("original_prediction"));
                List<Map<String, Object>> scenarios = (List<Map<String, Object>>) cf.get("counterfactual_scenarios");
                System.out.println("Counterfactual scenarios: " + (scenarios != null ? scenarios.size() : 0));
                if (scenarios != null && !scenarios.isEmpty()) {
                    for (int i = 0; i < Math.min(3, scenarios.size()); i++) {
                        Map<String, Object> s = scenarios.get(i);
                        System.out.println("  " + s.get("feature") + ": " + s.get("original_value") + " -> " + s.get("modified_value"));
                        System.out.println("    Impact: " + s.get("impact"));
                    }
                }
            }
            System.out.println();
        }

        // Batch processing
        System.out.println("=== Batch Processing ===");
        List<Map<String, Object>> batch = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> dec = new LinkedHashMap<>();
            dec.put("model_id", "trading_model_v1");
            dec.put("input_features", createSampleInput());
            dec.put("prediction", new String[]{"BUY", "SELL", "HOLD"}[ThreadLocalRandom.current().nextInt(3)]);
            dec.put("method", ExplanationMethod.values()[ThreadLocalRandom.current().nextInt(ExplanationMethod.values().length)]);
            batch.add(dec);
        }
        List<Map<String, Object>> batchResults = framework.batchExplainDecisions(batch);
        long compliant = batchResults.stream().filter(r -> (Boolean) r.getOrDefault("compliant", false)).count();
        System.out.println("Processed " + batchResults.size() + " decisions");
        System.out.println("Compliant: " + compliant + "/" + batchResults.size());
        System.out.println();

        // Model transparency report
        System.out.println("=== Model Transparency Report ===");
        Map<String, Object> report = framework.getModelTransparencyReport("trading_model_v1", 7);
        System.out.println("Model: " + report.get("model_id"));
        System.out.println("Period: " + report.get("period_days") + " days");
        System.out.println("Total decisions: " + report.get("total_decisions"));
        System.out.println("Avg confidence: " + String.format("%.0f%%", (Double) report.get("avg_confidence") * 100));
        System.out.println("Transparency score: " + String.format("%.0f%%", (Double) report.get("transparency_score") * 100));
        List<Map<String, Object>> features = (List<Map<String, Object>>) report.get("most_common_features");
        System.out.println("Most common features:");
        for (Map<String, Object> f : features) {
            f.forEach((k, v) -> System.out.println("  " + k + ": " + v));
        }
        Map<String, Long> riskDist = (Map<String, Long>) report.get("risk_distribution");
        System.out.println("Risk distribution:");
        riskDist.forEach((k, v) -> System.out.println("  " + k + ": " + v));
        System.out.println();

        // Governance dashboard
        System.out.println("=== Governance Dashboard ===");
        Map<String, Object> dashboard = framework.generateGovernanceDashboard();
        System.out.println("Total explanations: " + dashboard.get("total_explanations"));
        System.out.println("Total decisions: " + dashboard.get("total_decisions"));
        System.out.println("Avg confidence: " + String.format("%.0f%%", (Double) dashboard.get("avg_confidence") * 100));
        System.out.println("Governance rules: " + dashboard.get("governance_rules"));
        System.out.println("Enabled rules: " + dashboard.get("enabled_rules"));
        Map<String, Double> modelCompliance = (Map<String, Double>) dashboard.get("model_compliance");
        System.out.println("Compliance per model:");
        modelCompliance.forEach((k, v) -> System.out.println("  " + k + ": " + String.format("%.0f%%", v * 100)));
        System.out.println();

        // Compliance report
        System.out.println("=== Compliance Report ===");
        ComplianceReport compliance = framework.governanceSystem.generateComplianceReport("trading_model_v1");
        System.out.println("Report ID: " + compliance.reportId);
        System.out.println("Compliance Level: " + compliance.complianceLevel.getValue());
        System.out.println("Transparency Score: " + String.format("%.0f%%", compliance.transparencyScore * 100));
        System.out.println("Accountability Score: " + String.format("%.0f%%", compliance.accountabilityScore * 100));
        System.out.println("Regulatory Checks:");
        compliance.regulatoryChecks.forEach((k, v) -> System.out.println("  " + k + ": " + (v ? "PASS" : "FAIL")));
        System.out.println("Bias Metrics:");
        compliance.biasMetrics.forEach((k, v) -> System.out.println("  " + k + ": " + String.format("%.3f", v)));
        System.out.println("Fairness Metrics:");
        compliance.fairnessMetrics.forEach((k, v) -> System.out.println("  " + k + ": " + String.format("%.3f", v)));
        System.out.println("Recommendations:");
        compliance.recommendations.forEach(r -> System.out.println("  - " + r));
        System.out.println();

        // Audit log export
        System.out.println("=== Audit Log Export ===");
        try {
            framework.exportAuditLogs("audit_" + System.currentTimeMillis(), 1);
            System.out.println("Audit logs exported successfully.");
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }

        System.out.println("\n=== XAI Governance Framework test completed successfully! ===");
    }
}
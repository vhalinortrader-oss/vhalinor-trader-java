import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Módulo de Otimização Preditiva para VHALINOR.IAG
 * =====================================================
 * Otimização de configurações baseada em aprendizado e predição
 * 
 * Convertido de Python para Java.
 */
public class PredictiveConfigOptimizer {

    private static final Logger LOGGER = Logger.getLogger(PredictiveConfigOptimizer.class.getName());
    private static final int MAX_HISTORY_SIZE = 1000;
    private static final int MAX_OPTIMIZATION_HISTORY = 100;

    // Configuração associada
    private final Config config;

    // Dados históricos com capacidade limitada (comportamento de deque com maxlen)
    private final LinkedList<StateRecord> historicalData = new LinkedList<>();

    // Histórico de otimizações aplicadas
    private final List<OptimizationEntry> optimizationHistory = new ArrayList<>();

    // Matriz de correlações (chave composta -> coeficiente)
    private final Map<String, Double> correlationMatrix = new HashMap<>();

    // Padrões detectados (nome do padrão -> valor booleano)
    private final Map<String, Boolean> patterns = new HashMap<>();

    /**
     * Construtor do otimizador, recebe a configuração atual do sistema.
     */
    public PredictiveConfigOptimizer(Config config) {
        this.config = Objects.requireNonNull(config, "Config não pode ser nula");
    }

    // ---------------------------------------------------------------
    // Classes internas para representação de configuração e histórico
    // ---------------------------------------------------------------

    /**
     * Configuração completa do sistema VHALINOR.
     */
    public static class Config {
        public Neural neural;
        public Quantum quantum;
        public PerformanceMode performanceMode;

        public Config() {
            this.neural = new Neural();
            this.quantum = new Quantum();
            this.performanceMode = PerformanceMode.BALANCED;
        }
    }

    /**
     * Subconfiguração neural.
     */
    public static class Neural {
        public int maxNeurons = 10000;
        public double learningRate = 0.001;
    }

    /**
     * Subconfiguração quântica.
     */
    public static class Quantum {
        public boolean enabled = false;
        public int qubits = 4;
        public int maxQubits = 10;
    }

    /**
     * Modo de performance com representação textual.
     */
    public enum PerformanceMode {
        ECO("eco"),
        BALANCED("balanced"),
        PERFORMANCE("performance"),
        QUANTUM("quantum");

        private final String value;

        PerformanceMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static PerformanceMode fromString(String text) {
            for (PerformanceMode pm : values()) {
                if (pm.value.equalsIgnoreCase(text)) {
                    return pm;
                }
            }
            throw new IllegalArgumentException("Modo de performance inválido: " + text);
        }
    }

    /**
     * Registro de estado em um instante (métricas + configuração).
     */
    private static class StateRecord {
        final LocalDateTime timestamp;
        final Map<String, Double> metrics;         // métricas numéricas
        final ConfigSnapshot configSnapshot;

        StateRecord(LocalDateTime timestamp,
                    Map<String, Double> metrics,
                    ConfigSnapshot configSnapshot) {
            this.timestamp = timestamp;
            this.metrics = new HashMap<>(metrics);
            this.configSnapshot = configSnapshot;
        }
    }

    /**
     * Fotografia das configurações relevantes em um instante.
     */
    private static class ConfigSnapshot {
        final int neuralMaxNeurons;
        final double neuralLearningRate;
        final boolean quantumEnabled;
        final int quantumQubits;
        final String performanceMode;  // valor textual do enum

        ConfigSnapshot(int neuralMaxNeurons,
                       double neuralLearningRate,
                       boolean quantumEnabled,
                       int quantumQubits,
                       String performanceMode) {
            this.neuralMaxNeurons = neuralMaxNeurons;
            this.neuralLearningRate = neuralLearningRate;
            this.quantumEnabled = quantumEnabled;
            this.quantumQubits = quantumQubits;
            this.performanceMode = performanceMode;
        }
    }

    /**
     * Entrada do histórico de otimizações aplicadas.
     */
    private static class OptimizationEntry {
        final LocalDateTime timestamp;
        final List<String> changes;
        final Map<String, Object> predictedConfig;
        final boolean success;

        OptimizationEntry(LocalDateTime timestamp,
                          List<String> changes,
                          Map<String, Object> predictedConfig,
                          boolean success) {
            this.timestamp = timestamp;
            this.changes = new ArrayList<>(changes);
            this.predictedConfig = new HashMap<>(predictedConfig);
            this.success = success;
        }
    }

    // ---------------------------------------------------------------
    // Método principal de registro de estado
    // ---------------------------------------------------------------

    /**
     * Registra estado atual para aprendizado.
     * @param metrics Mapa com métricas atuais (ex.: cpu_usage, memory_usage, performance_score)
     */
    public void recordState(Map<String, Double> metrics) {
        ConfigSnapshot snapshot = new ConfigSnapshot(
                config.neural.maxNeurons,
                config.neural.learningRate,
                config.quantum.enabled,
                config.quantum.qubits,
                config.performanceMode.getValue()
        );

        StateRecord record = new StateRecord(LocalDateTime.now(), metrics, snapshot);

        // Comportamento de deque com tamanho máximo
        if (historicalData.size() >= MAX_HISTORY_SIZE) {
            historicalData.removeFirst();
        }
        historicalData.addLast(record);

        // Atualização periódica de correlações
        if (historicalData.size() % 10 == 0) {
            updateCorrelations();
        }

        // Detecção periódica de padrões
        if (historicalData.size() % 50 == 0) {
            detectPatterns();
        }

        LOGGER.fine(() -> String.format("Estado registrado: %s", metrics));
    }

    // ---------------------------------------------------------------
    // Cálculo de correlações
    // ---------------------------------------------------------------

    private void updateCorrelations() {
        if (historicalData.size() < 10) {
            return;
        }

        Map<String, Double> newCorrelations = new HashMap<>();
        String[] configKeys = {"neural_max_neurons", "neural_learning_rate"};
        String[] metricKeys = {"cpu_usage", "memory_usage", "performance_score"};

        for (String configKey : configKeys) {
            for (String metricKey : metricKeys) {
                if (historicalData.getFirst().metrics.containsKey(metricKey)) {
                    double correlation = calculateCorrelation(configKey, metricKey);
                    newCorrelations.put(configKey + "_vs_" + metricKey, correlation);
                }
            }
        }

        correlationMatrix.clear();
        correlationMatrix.putAll(newCorrelations);
        LOGGER.fine(() -> String.format("Correlações atualizadas: %d", newCorrelations.size()));
    }

    private double calculateCorrelation(String configKey, String metricKey) {
        List<Double> configValues = new ArrayList<>();
        List<Double> metricValues = new ArrayList<>();

        try {
            for (StateRecord state : historicalData) {
                Double configValue = getConfigValue(state.configSnapshot, configKey);
                Double metricValue = state.metrics.get(metricKey);

                if (configValue != null && metricValue != null) {
                    configValues.add(configValue);
                    metricValues.add(metricValue);
                }
            }

            if (configValues.size() < 2) {
                return 0.0;
            }

            int n = configValues.size();
            double sumConfig = 0.0, sumMetric = 0.0;
            double sumConfigSq = 0.0, sumMetricSq = 0.0, sumProducts = 0.0;

            for (int i = 0; i < n; i++) {
                double x = configValues.get(i);
                double y = metricValues.get(i);
                sumConfig += x;
                sumMetric += y;
                sumConfigSq += x * x;
                sumMetricSq += y * y;
                sumProducts += x * y;
            }

            double numerator = n * sumProducts - sumConfig * sumMetric;
            double denominator = Math.sqrt(
                    (n * sumConfigSq - sumConfig * sumConfig) *
                    (n * sumMetricSq - sumMetric * sumMetric)
            );

            if (denominator == 0.0) {
                return 0.0;
            }
            return numerator / denominator;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao calcular correlação", e);
            return 0.0;
        }
    }

    private Double getConfigValue(ConfigSnapshot snapshot, String configKey) {
        switch (configKey) {
            case "neural_max_neurons":
                return (double) snapshot.neuralMaxNeurons;
            case "neural_learning_rate":
                return snapshot.neuralLearningRate;
            default:
                return null;
        }
    }

    // ---------------------------------------------------------------
    // Detecção de padrões
    // ---------------------------------------------------------------

    private void detectPatterns() {
        if (historicalData.size() < 50) {
            return;
        }

        Map<String, Boolean> newPatterns = new HashMap<>();

        // Padrão 1: Alta CPU → mais neurônios?
        List<StateRecord> highCpuStates = historicalData.stream()
                .filter(s -> s.metrics.getOrDefault("cpu_usage", 0.0) > 70.0)
                .collect(Collectors.toList());
        if (!highCpuStates.isEmpty()) {
            double avgNeurons = highCpuStates.stream()
                    .mapToInt(s -> s.configSnapshot.neuralMaxNeurons)
                    .average()
                    .orElse(0.0);
            newPatterns.put("high_cpu_more_neurons", avgNeurons > 5000);
        }

        // Padrão 2: Baixa performance → menos qubits?
        List<StateRecord> lowPerfStates = historicalData.stream()
                .filter(s -> s.metrics.getOrDefault("performance_score", 0.0) < 0.5)
                .collect(Collectors.toList());
        if (!lowPerfStates.isEmpty()) {
            double avgQubits = lowPerfStates.stream()
                    .mapToInt(s -> s.configSnapshot.quantumQubits)
                    .average()
                    .orElse(0.0);
            newPatterns.put("low_performance_less_qubits", avgQubits < 5);
        }

        patterns.clear();
        patterns.putAll(newPatterns);
        LOGGER.fine(() -> String.format("Padrões detectados: %s", patterns));
    }

    // ---------------------------------------------------------------
    // Predição e aplicação da configuração ótima
    // ---------------------------------------------------------------

    /**
     * Prediz configuração ótima baseada nas métricas atuais.
     * @param currentMetrics Métricas atuais do sistema
     * @return Mapa com as configurações preditas
     */
    public Map<String, Object> predictOptimalConfig(Map<String, Double> currentMetrics) {
        if (historicalData.size() < 10) {
            // Dados insuficientes – retorna defaults otimizados
            Map<String, Object> defaults = new HashMap<>();
            defaults.put("neural_max_neurons", 10000);
            defaults.put("neural_learning_rate", 0.001);
            defaults.put("quantum_enabled", true);
            defaults.put("quantum_qubits", 5);
            defaults.put("performance_mode", "balanced");
            return defaults;
        }

        Map<String, Object> prediction = new HashMap<>();
        prediction.put("neural_max_neurons", config.neural.maxNeurons);
        prediction.put("neural_learning_rate", config.neural.learningRate);
        prediction.put("quantum_enabled", config.quantum.enabled);
        prediction.put("quantum_qubits", config.quantum.qubits);
        prediction.put("performance_mode", config.performanceMode.getValue());

        double cpuUsage = currentMetrics.getOrDefault("cpu_usage", 50.0);
        double memoryUsage = currentMetrics.getOrDefault("memory_usage", 50.0);
        double performanceScore = currentMetrics.getOrDefault("performance_score", 0.7);

        // Aplica heurísticas baseadas nas métricas e padrões detectados
        if (cpuUsage > 70 && patterns.getOrDefault("high_cpu_more_neurons", false)) {
            int currentNeurons = (int) prediction.get("neural_max_neurons");
            int newNeurons = Math.min(50000, (int) (currentNeurons * 1.2));
            prediction.put("neural_max_neurons", newNeurons);
        }

        if (performanceScore < 0.5) {
            double currentLR = (double) prediction.get("neural_learning_rate");
            prediction.put("neural_learning_rate", Math.min(0.01, currentLR * 1.5));
        }

        if (memoryUsage > 80 && !(boolean) prediction.get("quantum_enabled")) {
            prediction.put("quantum_enabled", true);
            prediction.put("quantum_qubits", 8);
        }

        if (performanceScore < 0.3) {
            prediction.put("performance_mode", "eco");
        } else if (performanceScore > 0.8) {
            prediction.put("performance_mode", "performance");
        }

        return prediction;
    }

    /**
     * Aplica uma otimização preditiva à configuração real.
     * @param predictedConfig Configuração predita (retornada por {@link #predictOptimalConfig})
     * @return Lista de descrições das alterações realizadas
     */
    public List<String> applyOptimization(Map<String, Object> predictedConfig) {
        List<String> changes = new ArrayList<>();

        try {
            // Neurônios
            if (predictedConfig.containsKey("neural_max_neurons")) {
                int oldNeurons = config.neural.maxNeurons;
                int newNeurons = ((Number) predictedConfig.get("neural_max_neurons")).intValue();
                if (oldNeurons != newNeurons && newNeurons >= 100 && newNeurons <= 100000) {
                    config.neural.maxNeurons = newNeurons;
                    changes.add(String.format("Neurônios: %d -> %d", oldNeurons, newNeurons));
                }
            }

            // Learning rate
            if (predictedConfig.containsKey("neural_learning_rate")) {
                double oldLR = config.neural.learningRate;
                double newLR = ((Number) predictedConfig.get("neural_learning_rate")).doubleValue();
                if (oldLR != newLR && newLR >= 0.0001 && newLR <= 1.0) {
                    config.neural.learningRate = newLR;
                    changes.add(String.format("Learning rate: %.4f -> %.4f", oldLR, newLR));
                }
            }

            // Quântico habilitado
            if (predictedConfig.containsKey("quantum_enabled")) {
                boolean oldEnabled = config.quantum.enabled;
                boolean newEnabled = (Boolean) predictedConfig.get("quantum_enabled");
                if (oldEnabled != newEnabled) {
                    config.quantum.enabled = newEnabled;
                    changes.add(String.format("Quântico: %b -> %b", oldEnabled, newEnabled));
                }
            }

            // Qubits
            if (predictedConfig.containsKey("quantum_qubits")) {
                int oldQubits = config.quantum.qubits;
                int newQubits = ((Number) predictedConfig.get("quantum_qubits")).intValue();
                if (oldQubits != newQubits && newQubits >= 1 && newQubits <= config.quantum.maxQubits) {
                    config.quantum.qubits = newQubits;
                    changes.add(String.format("Qubits: %d -> %d", oldQubits, newQubits));
                }
            }

            // Modo de performance
            if (predictedConfig.containsKey("performance_mode")) {
                String oldMode = config.performanceMode.getValue();
                String newMode = (String) predictedConfig.get("performance_mode");
                if (!oldMode.equalsIgnoreCase(newMode)) {
                    PerformanceMode newPerformanceMode = PerformanceMode.fromString(newMode);
                    config.performanceMode = newPerformanceMode;
                    changes.add(String.format("Modo performance: %s -> %s", oldMode, newMode));
                }
            }

            // Registro da otimização
            optimizationHistory.add(new OptimizationEntry(
                    LocalDateTime.now(),
                    changes,
                    new HashMap<>(predictedConfig),
                    !changes.isEmpty()
            ));

            // Limita o tamanho do histórico de otimizações
            if (optimizationHistory.size() > MAX_OPTIMIZATION_HISTORY) {
                optimizationHistory.subList(0, optimizationHistory.size() - MAX_OPTIMIZATION_HISTORY).clear();
            }

            LOGGER.info(() -> String.format("Otimização aplicada: %d alterações", changes.size()));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao aplicar otimização", e);
        }

        return changes;
    }

    // ---------------------------------------------------------------
    // Relatório de otimizações
    // ---------------------------------------------------------------

    /**
     * Retorna um relatório com estatísticas das otimizações realizadas.
     * @return Mapa com as informações do relatório
     */
    public Map<String, Object> getOptimizationReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        if (optimizationHistory.isEmpty()) {
            report.put("total_optimizations", 0);
            report.put("successful_optimizations", 0);
            report.put("success_rate", 0.0);
            report.put("historical_data_points", historicalData.size());
            report.put("correlations", correlationMatrix.size());
            report.put("patterns", patterns.size());
            report.put("recommendations", Collections.emptyList());
            return report;
        }

        long total = optimizationHistory.size();
        long successful = optimizationHistory.stream().filter(e -> e.success).count();
        double successRate = (double) successful / total;

        List<String> recommendations = new ArrayList<>();
        if (successRate < 0.7) {
            recommendations.add("Considere revisar os critérios de otimização automática.");
        }
        if (patterns.size() < 3) {
            recommendations.add("Colete mais dados para melhor detecção de padrões.");
        }

        report.put("total_optimizations", total);
        report.put("successful_optimizations", successful);
        report.put("success_rate", successRate);
        report.put("historical_data_points", historicalData.size());
        report.put("correlations", correlationMatrix.size());
        report.put("patterns", patterns.size());
        report.put("recommendations", recommendations);

        if (!optimizationHistory.isEmpty()) {
            OptimizationEntry last = optimizationHistory.get(optimizationHistory.size() - 1);
            Map<String, Object> lastInfo = new LinkedHashMap<>();
            lastInfo.put("timestamp", last.timestamp.toString());
            lastInfo.put("changes", last.changes);
            lastInfo.put("predicted_config", last.predictedConfig);
            lastInfo.put("success", last.success);
            report.put("last_optimization", lastInfo);
        } else {
            report.put("last_optimization", null);
        }

        return report;
    }
}
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * VhalinorLearningLogger – Java conversion of the intelligent logging system.
 *
 * Provides:
 * <ul>
 *   <li>Rotating file logs with configurable size and backup count</li>
 *   <li>Console output</li>
 *   <li>JSON formatted logs (optional)</li>
 *   <li>Configurable log level via environment variable VHALINOR_LOG_LEVEL</li>
 *   <li>Intelligent analysis: pattern recognition, anomaly detection, sentiment & urgency scoring</li>
 *   <li>Continuous background optimization</li>
 *   <li>Integration placeholder for Vhalinor AI Central</li>
 * </ul>
 */
public class VhalinorLearningLogger {

    // =========================================================================
    // CONSTANTS
    // =========================================================================
    private static final String DEFAULT_LOG_FILE = "vhalinor_learning.log";
    private static final int DEFAULT_MAX_LOG_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final int DEFAULT_BACKUP_COUNT = 5;
    private static final String LOG_FORMAT = "%1$tF %1$tT [%2$s] [%3$s] %4$s%n";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private static final Level DEFAULT_LOG_LEVEL;

    static {
        String envLevel = System.getenv("VHALINOR_LOG_LEVEL");
        if (envLevel != null) {
            try {
                DEFAULT_LOG_LEVEL = Level.parse(envLevel.toUpperCase());
            } catch (IllegalArgumentException e) {
                DEFAULT_LOG_LEVEL = Level.INFO;
            }
        } else {
            DEFAULT_LOG_LEVEL = Level.INFO;
        }
    }

    // =========================================================================
    // DATA STRUCTURES
    // =========================================================================

    /** Pattern detected by the AI analyzer. */
    public static class LogPattern {
        public String patternId;
        public String patternType;
        public double frequency;
        public double confidence;
        public String description;
        public Instant firstSeen;
        public Instant lastSeen;
        public int occurrences;
        public double anomalyScore;

        public LogPattern(String patternId, String patternType, double confidence, String description) {
            this.patternId = patternId;
            this.patternType = patternType;
            this.confidence = confidence;
            this.description = description;
            this.firstSeen = Instant.now();
            this.lastSeen = Instant.now();
            this.occurrences = 0;
            this.frequency = 0.0;
            this.anomalyScore = 0.0;
        }

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("pattern_id", patternId);
            map.put("pattern_type", patternType);
            map.put("frequency", frequency);
            map.put("confidence", confidence);
            map.put("description", description);
            map.put("first_seen", firstSeen.toString());
            map.put("last_seen", lastSeen.toString());
            map.put("occurrences", occurrences);
            map.put("anomaly_score", anomalyScore);
            return map;
        }
    }

    /** Metrics of the logging system. */
    public static class LogMetrics {
        public long totalLogs;
        public long errorCount;
        public long warningCount;
        public long infoCount;
        public long debugCount;
        public double avgLogSize;
        public double peakMemoryMb;
        public double processingTimeMs;
        public int patternsDetected;
        public int anomaliesDetected;
        public List<String> optimizationSuggestions = new ArrayList<>();
        public Instant lastUpdate = Instant.now();

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("total_logs", totalLogs);
            map.put("error_count", errorCount);
            map.put("warning_count", warningCount);
            map.put("info_count", infoCount);
            map.put("debug_count", debugCount);
            map.put("avg_log_size", avgLogSize);
            map.put("peak_memory_mb", peakMemoryMb);
            map.put("processing_time_ms", processingTimeMs);
            map.put("patterns_detected", patternsDetected);
            map.put("anomalies_detected", anomaliesDetected);
            map.put("optimization_suggestions", optimizationSuggestions);
            map.put("last_update", lastUpdate.toString());
            return map;
        }
    }

    /** Enriched log entry with learning information. */
    public static class LearningLogEntry {
        public Instant timestamp;
        public Level level;
        public String message;
        public String module;
        public Map<String, Object> context;
        public double anomalyScore;
        public List<String> patternMatches;
        public double sentimentScore;
        public double urgencyScore;
        public List<String> learningInsights;

        public LearningLogEntry(Level level, String message, String module, Map<String, Object> context) {
            this.timestamp = Instant.now();
            this.level = level;
            this.message = message;
            this.module = module;
            this.context = context != null ? new HashMap<>(context) : new HashMap<>();
            this.patternMatches = new ArrayList<>();
            this.learningInsights = new ArrayList<>();
        }

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("timestamp", timestamp.toString());
            map.put("level", level.getName());
            map.put("message", message);
            map.put("module", module);
            map.put("context", context);
            map.put("anomaly_score", anomalyScore);
            map.put("pattern_matches", patternMatches);
            map.put("sentiment_score", sentimentScore);
            map.put("urgency_score", urgencyScore);
            map.put("learning_insights", learningInsights);
            return map;
        }
    }

    // =========================================================================
    // SIMPLE ANOMALY DETECTOR (without external ML libraries)
    // =========================================================================

    /**
     * Uses a moving window to compute mean and standard deviation of log length
     * and then scores new entries as Z-score (0 to 1).
     */
    private static class SimpleAnomalyDetector {
        private final Deque<Integer> window = new LinkedList<>();
        private final int windowSize = 50;
        private double mean = 0.0;
        private double sqSum = 0.0; // (sum of squares) used for running std

        public synchronized double score(int logLength) {
            window.addLast(logLength);
            if (window.size() > windowSize) {
                int removed = window.removeFirst();
                // Update running statistics
                updateStats(-removed);
            }
            updateStats(logLength);

            if (window.size() < 5) {
                return 0.0; // not enough data
            }

            double std = Math.sqrt(sqSum / window.size() - mean * mean);
            if (std < 0.001) return 0.0;

            double z = (logLength - mean) / std;
            // Convert Z to a 0-1 score (higher = more anomalous)
            return Math.min(1.0, Math.abs(z) / 3.0);
        }

        private synchronized void updateStats(int delta) {
            // Recalculate mean and sqSum with the new window (simplified: recompute)
            // Since we only add/remove one element, we can do incremental:
            // But for simplicity, recompute each time? Too slow. Use incremental.
            // We'll store sum and sum of squares.
            // Actually we'll store a double sum and sum of squares.
        }

        // Reset with incremental approach
        private double sumVal = 0.0;
        private double sumSqVal = 0.0;

        public synchronized double scoreV2(int logLength) {
            window.addLast(logLength);
            sumVal += logLength;
            sumSqVal += (double) logLength * logLength;
            if (window.size() > windowSize) {
                int removed = window.removeFirst();
                sumVal -= removed;
                sumSqVal -= (double) removed * removed;
            }

            if (window.size() < 5) return 0.0;

            double mean = sumVal / window.size();
            double variance = (sumSqVal / window.size()) - (mean * mean);
            double std = Math.sqrt(Math.max(0, variance));
            if (std < 0.001) return 0.0;

            double z = (logLength - mean) / std;
            return Math.min(1.0, Math.abs(z) / 3.0);
        }
    }

    // =========================================================================
    // INTELLIGENT LOG ANALYZER
    // =========================================================================

    public static class IntelligentLogAnalyzer {
        private static final int MAX_HISTORY = 10_000;

        private final Deque<LearningLogEntry> logHistory = new ArrayDeque<>(MAX_HISTORY);
        private final Map<String, LogPattern> patterns = new ConcurrentHashMap<>();
        private final LogMetrics metrics = new LogMetrics();
        private final SimpleAnomalyDetector anomalyDetector = new SimpleAnomalyDetector();

        private Thread optimizationThread;
        private volatile boolean stopOptimization = false;

        // Static keyword patterns
        private static final Map<String, List<String>> ERROR_PATTERNS = Map.of(
                "connection_error", List.of("connection", "timeout", "network"),
                "memory_error", List.of("memory", "out of memory", "allocation"),
                "file_error", List.of("file", "directory", "path"),
                "permission_error", List.of("permission", "access denied", "unauthorized"),
                "validation_error", List.of("validation", "invalid", "malformed")
        );

        private static final List<String> POSITIVE_WORDS = List.of("success", "complete", "ready", "ok", "good", "excellent");
        private static final List<String> NEGATIVE_WORDS = List.of("error", "fail", "critical", "exception", "timeout", "lost");

        public IntelligentLogAnalyzer() {
            System.out.println("🧠 Analisador Inteligente de Logs inicializado");
        }

        /**
         * Main analysis method – enriches the log entry and stores it.
         */
        public synchronized LearningLogEntry analyzeLogEntry(LearningLogEntry entry) {
            long start = System.nanoTime();

            // Anomaly score based on message length (simple)
            entry.anomalyScore = anomalyDetector.scoreV2(entry.message.length());

            // Pattern matching
            entry.patternMatches = matchPatterns(entry);

            // Sentiment analysis
            entry.sentimentScore = analyzeSentiment(entry.message);

            // Urgency calculation
            entry.urgencyScore = calculateUrgency(entry);

            // Generate insights
            entry.learningInsights = generateInsights(entry);

            // Add to history (trim if needed)
            if (logHistory.size() >= MAX_HISTORY) {
                logHistory.removeFirst();
            }
            logHistory.addLast(entry);

            // Update metrics
            updateMetrics(entry);

            // Optionally send to AI Central (placeholder)
            // sendToAiCentral(entry);

            long elapsed = System.nanoTime() - start;
            metrics.processingTimeMs = (double) elapsed / 1_000_000;

            return entry;
        }

        private List<String> matchPatterns(LearningLogEntry entry) {
            String msgLower = entry.message.toLowerCase();
            List<String> matches = new ArrayList<>();

            for (Map.Entry<String, List<String>> pattern : ERROR_PATTERNS.entrySet()) {
                String patternId = pattern.getKey();
                List<String> keywords = pattern.getValue();
                if (keywords.stream().anyMatch(msgLower::contains)) {
                    matches.add(patternId);
                    patterns.computeIfAbsent(patternId, k -> new LogPattern(k, "error", 0.8,
                            "Padrão de " + k + " detectado"));
                    LogPattern pat = patterns.get(patternId);
                    pat.occurrences++;
                    pat.lastSeen = Instant.now();
                    pat.frequency = (double) pat.occurrences / Math.max(1, logHistory.size());
                }
            }
            return matches;
        }

        private double analyzeSentiment(String message) {
            String msgLower = message.toLowerCase();
            long posCount = POSITIVE_WORDS.stream().filter(msgLower::contains).count();
            long negCount = NEGATIVE_WORDS.stream().filter(msgLower::contains).count();
            int totalWords = message.split("\\s+").length;
            if (totalWords == 0) return 0.5;
            double sentiment = (double) (posCount - negCount) / totalWords + 0.5;
            return Math.max(0.0, Math.min(1.0, sentiment));
        }

        private double calculateUrgency(LearningLogEntry entry) {
            double urgency;
            switch (entry.level.getName()) {
                case "DEBUG": urgency = 0.1; break;
                case "INFO":  urgency = 0.2; break;
                case "WARNING": urgency = 0.6; break;
                case "ERROR": urgency = 0.8; break;
                case "CRITICAL": case "SEVERE": urgency = 1.0; break;
                default: urgency = 0.2;
            }

            String msgLower = entry.message.toLowerCase();
            List<String> urgentWords = List.of("critical", "urgent", "immediate", "emergency", "fatal");
            if (urgentWords.stream().anyMatch(msgLower::contains)) {
                urgency = Math.min(1.0, urgency + 0.3);
            }
            urgency += entry.anomalyScore * 0.2;
            return Math.min(1.0, urgency);
        }

        private List<String> generateInsights(LearningLogEntry entry) {
            List<String> insights = new ArrayList<>();
            if (entry.anomalyScore > 0.7) {
                insights.add(String.format("⚠️ Anomalia detectada com score %.2f", entry.anomalyScore));
            }
            if (!entry.patternMatches.isEmpty()) {
                insights.add("🔍 Padrões identificados: " + String.join(", ", entry.patternMatches));
            }
            if (entry.urgencyScore > 0.8) {
                insights.add(String.format("🚨 Alta urgência detectada: %.2f", entry.urgencyScore));
            }
            if (entry.sentimentScore < 0.3) {
                insights.add(String.format("😟 Sentimento negativo detectado: %.2f", entry.sentimentScore));
            }
            return insights;
        }

        private void updateMetrics(LearningLogEntry entry) {
            metrics.totalLogs++;
            switch (entry.level.getName()) {
                case "ERROR": metrics.errorCount++; break;
                case "WARNING": metrics.warningCount++; break;
                case "INFO": metrics.infoCount++; break;
                case "DEBUG": metrics.debugCount++; break;
            }
            long logSize = entry.toDict().toString().length();
            metrics.avgLogSize = (metrics.avgLogSize * (metrics.totalLogs - 1) + logSize) / metrics.totalLogs;
            if (!entry.patternMatches.isEmpty()) metrics.patternsDetected++;
            if (entry.anomalyScore > 0.7) metrics.anomaliesDetected++;
            metrics.lastUpdate = Instant.now();
        }

        public List<String> getOptimizationSuggestions() {
            List<String> suggestions = new ArrayList<>();
            if (metrics.totalLogs > 0) {
                double errorRate = (double) metrics.errorCount / metrics.totalLogs;
                if (errorRate > 0.1) {
                    suggestions.add(String.format("🔧 Alta taxa de erros (%.1f%%). Verificar configurações.", errorRate * 100));
                }
            }
            if (metrics.anomaliesDetected > 10) {
                suggestions.add("🔍 Muitas anomalias detectadas (" + metrics.anomaliesDetected + "). Revisar padrões.");
            }
            if (metrics.processingTimeMs > 50) {
                suggestions.add(String.format("⚡ Processamento lento (%.1f ms). Otimizar análise.", metrics.processingTimeMs));
            }
            double usedMemMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024);
            if (usedMemMB > 500) {
                suggestions.add(String.format("💾 Alto uso de memória (%.1f MB). Reduzir histórico.", usedMemMB));
            }
            return suggestions;
        }

        public void startContinuousOptimization() {
            if (optimizationThread != null && optimizationThread.isAlive()) {
                return;
            }
            stopOptimization = false;
            optimizationThread = new Thread(this::optimizationLoop, "LogOptimization");
            optimizationThread.setDaemon(true);
            optimizationThread.start();
            System.out.println("🔄 Otimização contínua iniciada");
        }

        private void optimizationLoop() {
            while (!stopOptimization) {
                try {
                    TimeUnit.SECONDS.sleep(60);
                    List<String> suggestions = getOptimizationSuggestions();
                    metrics.optimizationSuggestions = suggestions;
                    cleanupOldPatterns();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Erro na otimização: " + e.getMessage());
                }
            }
        }

        private void cleanupOldPatterns() {
            Instant cutoff = Instant.now().minus(Duration.ofHours(24));
            patterns.entrySet().removeIf(entry -> entry.getValue().lastSeen.isBefore(cutoff) && entry.getValue().occurrences < 5);
        }

        public void stopOptimization() {
            stopOptimization = true;
            if (optimizationThread != null) {
                try {
                    optimizationThread.join(5000);
                } catch (InterruptedException ignored) {}
                System.out.println("⏹️ Otimização contínua parada");
            }
        }

        public Map<String, Object> getStatusReport() {
            Map<String, Object> report = new LinkedHashMap<>();
            report.put("metrics", metrics.toDict());
            Map<String, Object> pattMap = new LinkedHashMap<>();
            patterns.forEach((k, v) -> pattMap.put(k, v.toDict()));
            report.put("patterns", pattMap);
            report.put("ai_central_available", false); // no Python AI here
            report.put("optimization_active", optimizationThread != null && optimizationThread.isAlive());
            report.put("log_history_size", logHistory.size());
            report.put("suggestions", getOptimizationSuggestions());
            return report;
        }
    }

    // =========================================================================
    // ADVANCED LEARNING LOGGER (wraps JUL + IntelligentAnalyzer)
    // =========================================================================

    public static class AdvancedLearningLogger {
        private final Logger baseLogger;
        private final IntelligentLogAnalyzer analyzer;

        public AdvancedLearningLogger(String name) {
            this.baseLogger = Logger.getLogger(name);
            this.baseLogger.setLevel(DEFAULT_LOG_LEVEL);
            this.analyzer = new IntelligentLogAnalyzer();
            this.analyzer.startContinuousOptimization();
            System.out.println("🧠 Advanced Learning Logger '" + name + "' inicializado com IA");
        }

        private LearningLogEntry logWithIntelligence(Level level, String message, Map<String, Object> context) {
            LearningLogEntry entry = new LearningLogEntry(level, message, baseLogger.getName(), context);
            LearningLogEntry analyzed = analyzer.analyzeLogEntry(entry);

            // Basic logging
            baseLogger.log(level, message);

            // Log insights if any
            for (String insight : analyzed.learningInsights) {
                baseLogger.info("🧠 Insights: " + insight);
            }
            return analyzed;
        }

        public LearningLogEntry debug(String msg, Map<String, Object> context) {
            return logWithIntelligence(Level.FINE, msg, context);
        }
        public LearningLogEntry info(String msg, Map<String, Object> context) {
            return logWithIntelligence(Level.INFO, msg, context);
        }
        public LearningLogEntry warning(String msg, Map<String, Object> context) {
            return logWithIntelligence(Level.WARNING, msg, context);
        }
        public LearningLogEntry error(String msg, Map<String, Object> context) {
            return logWithIntelligence(Level.SEVERE, msg, context);
        }
        public LearningLogEntry critical(String msg, Map<String, Object> context) {
            // JUL doesn't have CRITICAL; use SEVERE with a custom note
            return logWithIntelligence(Level.SEVERE, "[CRITICAL] " + msg, context);
        }

        public Map<String, Object> getIntelligenceReport() {
            return analyzer.getStatusReport();
        }

        public Map<String, LogPattern> getPatterns() {
            return new HashMap<>(analyzer.patterns);
        }

        public LogMetrics getMetrics() {
            return analyzer.metrics;
        }

        public void cleanup() {
            analyzer.stopOptimization();
            System.out.println("🧹 Advanced Learning Logger limpo");
        }
    }

    // =========================================================================
    // LOGGING SETUP (rotating files, console, JSON)
    // =========================================================================

    public static Logger setupLogging(String logFile, boolean console, boolean jsonFormat) throws IOException {
        Logger logger = Logger.getLogger("VhalinorLearning");
        logger.setUseParentHandlers(false); // We handle all
        logger.setLevel(DEFAULT_LOG_LEVEL);

        // Remove existing handlers to avoid duplicates
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }

        Formatter formatter;
        if (jsonFormat) {
            formatter = new JsonFormatter();
        } else {
            formatter = new SimpleTextFormatter();
        }

        // File handler with rotation
        FileHandler fileHandler = new FileHandler(logFile, DEFAULT_MAX_LOG_SIZE, DEFAULT_BACKUP_COUNT, true);
        fileHandler.setLevel(DEFAULT_LOG_LEVEL);
        fileHandler.setFormatter(formatter);
        fileHandler.setEncoding("UTF-8");
        logger.addHandler(fileHandler);

        // Console handler
        if (console) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(DEFAULT_LOG_LEVEL);
            consoleHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);
        }

        return logger;
    }

    /** Simple text formatter similar to Python's format. */
    static class SimpleTextFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            Instant instant = Instant.ofEpochMilli(record.getMillis());
            String timestamp = DATE_FORMATTER.format(instant);
            String level = record.getLevel().getName();
            String loggerName = record.getLoggerName();
            String message = formatMessage(record);
            Throwable thrown = record.getThrown();
            String result = String.format(LOG_FORMAT, timestamp, level, loggerName, message);
            if (thrown != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                thrown.printStackTrace(pw);
                result += sw.toString();
            }
            return result;
        }
    }

    /** JSON formatter – produces a simplified JSON line. */
    static class JsonFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("timestamp", DATE_FORMATTER.format(Instant.ofEpochMilli(record.getMillis())));
            map.put("level", record.getLevel().getName());
            map.put("name", record.getLoggerName());
            map.put("message", formatMessage(record));
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                map.put("exception", sw.toString());
            }
            // Simple JSON without extra library
            return map.entrySet().stream()
                    .map(e -> "\"" + e.getKey() + "\": \"" + e.getValue().toString().replace("\"", "\\\"") + "\"")
                    .collect(Collectors.joining(", ", "{", "}")) + "\n";
        }
    }

    // =========================================================================
    // CONTEXT CLASSES
    // =========================================================================

    /** Basic learning context (legacy). */
    public static class LearningContext implements AutoCloseable {
        private final Logger logger;
        private final String aluno;
        private final int nivel;
        private final Instant startTime;

        public LearningContext(Logger logger, String aluno, int nivel) {
            this.logger = logger;
            this.aluno = aluno;
            this.nivel = nivel;
            this.startTime = Instant.now();
        }

        public void info(String msg) { logger.info(msg); }
        public void debug(String msg) { logger.fine(msg); }
        public void warning(String msg) { logger.warning(msg); }
        public void error(String msg) { logger.severe(msg); }

        @Override
        public void close() {
            double duration = Duration.between(startTime, Instant.now()).toMillis() / 1000.0;
            logger.info(String.format("Contexto de %s encerrado em %.2fs", aluno, duration));
        }
    }

    /** Intelligent learning context that uses AdvancedLearningLogger. */
    public static class IntelligentLearningContext implements AutoCloseable {
        private final AdvancedLearningLogger aiLogger;
        private final String aluno;
        private final int nivel;
        private final Instant startTime;

        public IntelligentLearningContext(AdvancedLearningLogger aiLogger, String aluno, int nivel) {
            this.aiLogger = aiLogger;
            this.aluno = aluno;
            this.nivel = nivel;
            this.startTime = Instant.now();
        }

        public void info(String msg) { aiLogger.info(msg, contextMap()); }
        public void debug(String msg) { aiLogger.debug(msg, contextMap()); }
        public void warning(String msg) { aiLogger.warning(msg, contextMap()); }
        public void error(String msg) { aiLogger.error(msg, contextMap()); }

        private Map<String, Object> contextMap() {
            Map<String, Object> ctx = new HashMap<>();
            ctx.put("aluno", aluno);
            ctx.put("nivel", nivel);
            return ctx;
        }

        @Override
        public void close() {
            double duration = Duration.between(startTime, Instant.now()).toMillis() / 1000.0;
            aiLogger.info(String.format("Contexto de %s concluído com sucesso em %.2fs", aluno, duration),
                    Map.of("duration", duration, "final_level", nivel));
        }
    }

    // =========================================================================
    // DEMONSTRATION MAIN
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("🚀 Inicializando Vhalinor Learning Logger com IA...");
        try {
            // Setup basic logger
            Logger baseLogger = setupLogging(DEFAULT_LOG_FILE, true, false);

            // Create advanced logger
            AdvancedLearningLogger aiLogger = new AdvancedLearningLogger("VhalinorLearning");

            aiLogger.info("Sistema Vhalinor Learning v2.3.1 inicializado", null);
            aiLogger.debug("Carregando base de conhecimentos arcanos...", null);
            aiLogger.warning("Módulo de runas antigas não encontrado. Usando fallback básico.", null);

            // Intelligent context
            try (IntelligentLearningContext ctx = new IntelligentLearningContext(aiLogger, "Aelar", 12)) {
                ctx.info("Aluno 'Aelar' conectado. Nível atual: 12.");
                ctx.debug("Iniciando treinamento de feitiços elementais...");
                ctx.warning("Detectada baixa afinidade com magia de fogo.");

                try {
                    throw new RuntimeException("Foco insuficiente para Teletransporte Curto");
                } catch (Exception e) {
                    ctx.error("Falha crítica ao tentar aprender 'Teletransporte Curto'");
                }
            }

            // Generate some patterns
            aiLogger.info("Testando detecção de anomalias...", null);
            aiLogger.error("Erro de conexão com servidor mágico", null);
            aiLogger.error("Timeout na comunicação com o reino elemental", null);
            aiLogger.warning("Memória insuficiente para processar feitiço complexo", null);
            aiLogger.info("Feitiço 'Bola de Fogo' aprendido com sucesso", null);

            TimeUnit.SECONDS.sleep(1);

            // Report
            System.out.println("\n📈 RELATÓRIO DE INTELIGÊNCIA:");
            Map<String, Object> report = aiLogger.getIntelligenceReport();
            Map<String, Object> metrics = (Map<String, Object>) report.get("metrics");
            System.out.println("📊 Total de Logs: " + metrics.get("total_logs"));
            System.out.println("❌ Erros Detectados: " + metrics.get("error_count"));
            System.out.println("⚠️ Avisos Detectados: " + metrics.get("warning_count"));
            System.out.println("🔍 Padrões Detectados: " + metrics.get("patterns_detected"));
            System.out.println("🚨 Anomalias Detectadas: " + metrics.get("anomalies_detected"));
            System.out.printf("⚡ Tempo Médio de Processamento: %.2fms\n", metrics.get("processing_time_ms"));

            Map<String, Object> patterns = (Map<String, Object>) report.get("patterns");
            if (!patterns.isEmpty()) {
                System.out.println("\n🔍 PADRÕES IDENTIFICADOS:");
                patterns.forEach((k, v) -> {
                    Map<?,?> pm = (Map<?,?>) v;
                    System.out.println("  • " + k + ": " + pm.get("occurrences") + " ocorrências");
                });
            }

            List<String> suggestions = (List<String>) metrics.get("optimization_suggestions");
            if (!suggestions.isEmpty()) {
                System.out.println("\n💡 SUGESTÕES DE OTIMIZAÇÃO:");
                suggestions.forEach(s -> System.out.println("  • " + s));
            }

            // Legacy context test
            System.out.println("\n🔄 Testando contexto legado para compatibilidade...");
            Logger legacyLog = Logger.getLogger("VhalinorLegacy");
            legacyLog.setLevel(DEFAULT_LOG_LEVEL);
            try (LearningContext lc = new LearningContext(legacyLog, "Gandalf", 99)) {
                lc.info("Mago lendário conectado ao sistema");
                lc.debug("Acessando biblioteca de feitiços antigos...");
            }

            aiLogger.info("Sistema de logging com IA encerrado com sucesso", null);

            System.out.println("\n✅ Vhalinor Learning Logger configurado com sucesso!");
            System.out.println("📁 Logs gravados em: " + new File(DEFAULT_LOG_FILE).getAbsolutePath());
            System.out.println("🧠 Análise inteligente: Ativa");
            System.out.println("🔍 Detecção de anomalias: Ativa (simples)");

            aiLogger.cleanup();
            System.out.println("\n🧹 Recursos limpos com sucesso.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
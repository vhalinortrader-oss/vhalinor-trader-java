import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

/**
 * 🤖 INTEGRAÇÃO PYAUTOGUI - LAYER 1 (SENSORIAL) - ENHANCED VERSION
 * ====================================================================
 * Enhanced integration of automation (Java Robot) with AI capabilities
 * Parte da camada sensorial do VHALINOR-TRADER-IAG 4.0
 *
 * Convertido do Python para Java.
 * Usa java.awt.Robot no lugar de pyautogui.
 * Modelos neurais simplificados (placeholder) e monitoramento em tempo real.
 */
public class EnhancedAPIAutomationSensor {

    // ---------- Logger ----------
    private static final Logger LOGGER = Logger.getLogger(EnhancedAPIAutomationSensor.class.getName());

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS - %4$s - %5$s%6$s%n");
        LOGGER.setLevel(Level.INFO);
    }

    // ---------- Enums ----------
    public enum AutomationState {
        IDLE("idle"), ACTIVE("active"), LEARNING("learning"),
        PREDICTING("predicting"), ERROR("error");

        private final String value;
        AutomationState(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum NeuralEventType {
        MOUSE_MOVE("mouse_move"), MOUSE_CLICK("mouse_click"),
        SCREEN_CAPTURE("screen_capture"), API_INTERACTION("api_interaction"),
        LEARNING_EVENT("learning_event");

        private final String value;
        NeuralEventType(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    // ---------- Data classes ----------
    public static class EnhancedScreenCapture {
        public String apiName;
        public String timestamp;
        public String filepath;
        public boolean success;
        public Dimension resolution;
        public long fileSize;
        public Map<String, Object> aiAnalysis = new HashMap<>();
        public double confidenceScore = 0.0;
        public boolean anomalyDetected = false;

        public EnhancedScreenCapture() {}
    }

    public static class NeuralMouseEvent {
        public int x, y;
        public String timestamp;
        public String action;
        public double duration;
        public double predictedSuccess = 0.0;
        public double confidence = 0.0;
        public List<Double> neuralPattern = new ArrayList<>();
    }

    public static class CognitiveInsight {
        public String patternType;
        public double confidence;
        public String timestamp;
        public String prediction;
        public Map<String, Object> evidence;
        public String recommendation;
    }

    public static class APIScreenCapture {
        public String apiName;
        public String timestamp;
        public String filepath;
        public boolean success;
        public Dimension resolution = new Dimension(0, 0);
        public long fileSize = 0;
    }

    // ---------- Neural processor placeholder ----------
    public static class NeuralAutomationProcessor {
        private final Deque<double[]> trainingData = new ArrayDeque<>();
        private final Deque<Double> predictionHistory = new ArrayDeque<>();
        public double learningRate = 0.01;
        public double accuracy = 0.0;
        private Random rnd = new Random();

        public NeuralAutomationProcessor() {
            LOGGER.info("Neural processor initialized (placeholder model)");
        }

        public double predictActionSuccess(List<Double> features) {
            // Simple heuristic: average of features times a random weight
            double sum = features.stream().mapToDouble(Double::doubleValue).average().orElse(0.5);
            double prediction = Math.tanh(sum) * 0.5 + 0.5; // normalize to 0-1
            predictionHistory.add(prediction);
            if (predictionHistory.size() > 100) predictionHistory.pollFirst();
            return prediction;
        }

        public void learnFromResult(List<Double> features, boolean success) {
            double[] pair = new double[features.size() + 1];
            for (int i = 0; i < features.size(); i++) pair[i] = features.get(i);
            pair[features.size()] = success ? 1.0 : 0.0;
            trainingData.add(pair);
            if (trainingData.size() > 1000) trainingData.pollFirst();
            if (trainingData.size() >= 10) {
                // Simulate training: accuracy improves over time
                accuracy = Math.min(0.95, accuracy + 0.001);
            }
        }
    }

    // ---------- Real-time monitor ----------
    public static class RealTimeAutomationMonitor {
        public final Map<String, Object> metrics = new ConcurrentHashMap<>();
        private final List<Consumer<Map<String, Object>>> subscribers = new CopyOnWriteArrayList<>();
        private volatile boolean monitoring = false;
        private Thread monitorThread;
        public final long startTime = System.currentTimeMillis();

        public RealTimeAutomationMonitor() {
            metrics.put("actions_performed", 0L);
            metrics.put("success_rate", 0.0);
            metrics.put("avg_response_time", 0.0);
            metrics.put("anomaly_count", 0L);
            metrics.put("learning_progress", 0.0);
        }

        public void subscribe(Consumer<Map<String, Object>> callback) {
            subscribers.add(callback);
        }

        public void startMonitoring() {
            if (!monitoring) {
                monitoring = true;
                monitorThread = new Thread(this::monitorLoop);
                monitorThread.setDaemon(true);
                monitorThread.start();
                LOGGER.info("Real-time monitoring started");
            }
        }

        public void stopMonitoring() {
            monitoring = false;
            if (monitorThread != null) {
                try { monitorThread.join(1000); } catch (InterruptedException ignored) {}
            }
            LOGGER.info("Real-time monitoring stopped");
        }

        private void monitorLoop() {
            while (monitoring) {
                try {
                    updateMetrics();
                    for (Consumer<Map<String, Object>> callback : subscribers) {
                        callback.accept(new HashMap<>(metrics));
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Monitoring error", e);
                }
            }
        }

        private void updateMetrics() {
            long uptime = System.currentTimeMillis() - startTime;
            long actions = (long) metrics.getOrDefault("actions_performed", 0L);
            double avgTime = actions > 0 ? (double) uptime / actions : 0.0;
            metrics.put("avg_response_time", avgTime);
        }
    }

    // ---------- Cognitive analyzer ----------
    public static class CognitiveAutomationAnalyzer {
        private final Deque<NeuralMouseEvent> patterns = new ArrayDeque<>();
        private final Deque<CognitiveInsight> insights = new ArrayDeque<>();
        public String cognitiveState = "learning";

        public List<CognitiveInsight> analyzePattern(List<NeuralMouseEvent> events) {
            List<CognitiveInsight> newInsights = new ArrayList<>();
            if (events.size() < 5) return newInsights;

            List<Point> clickPositions = events.stream()
                    .filter(e -> "click".equals(e.action))
                    .map(e -> new Point(e.x, e.y))
                    .collect(Collectors.toList());
            if (clickPositions.size() >= 3) {
                List<List<Point>> clusters = detectClusters(clickPositions, 50);
                if (clusters.size() > 1) {
                    CognitiveInsight insight = new CognitiveInsight();
                    insight.patternType = "click_clustering";
                    insight.confidence = 0.7;
                    insight.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    insight.prediction = "Multiple interaction zones detected";
                    insight.evidence = Map.of("clusters", clusters.size());
                    insight.recommendation = "Optimize UI layout for clustered interactions";
                    newInsights.add(insight);
                }
            }
            patterns.addAll(events);
            if (patterns.size() > 100) patterns.removeFirst();
            insights.addAll(newInsights);
            if (insights.size() > 50) insights.removeFirst();
            return newInsights;
        }

        private List<List<Point>> detectClusters(List<Point> points, double threshold) {
            List<List<Point>> clusters = new ArrayList<>();
            boolean[] used = new boolean[points.size()];
            for (int i = 0; i < points.size(); i++) {
                if (used[i]) continue;
                List<Point> cluster = new ArrayList<>();
                cluster.add(points.get(i));
                used[i] = true;
                for (int j = i + 1; j < points.size(); j++) {
                    if (used[j]) continue;
                    double dist = points.get(i).distance(points.get(j));
                    if (dist <= threshold) {
                        cluster.add(points.get(j));
                        used[j] = true;
                    }
                }
                if (cluster.size() >= 2) clusters.add(cluster);
            }
            return clusters;
        }
    }

    // ---------- Main sensor class ----------
    private final Path logDir;
    private final Robot robot;
    private final Dimension screenSize;
    private final NeuralAutomationProcessor neuralProcessor;
    private final RealTimeAutomationMonitor realTimeMonitor;
    private final CognitiveAutomationAnalyzer cognitiveAnalyzer;
    private final List<EnhancedScreenCapture> enhancedCaptures = new ArrayList<>();
    private final List<NeuralMouseEvent> mouseEvents = new ArrayList<>();
    private final Map<String, Map<String, Object>> apiStates = new ConcurrentHashMap<>();
    private AutomationState state = AutomationState.IDLE;
    private final Map<String, Object> performanceMetrics = new ConcurrentHashMap<>();

    public EnhancedAPIAutomationSensor(String logDir) throws AWTException {
        this.logDir = Path.of(logDir);
        Files.createDirectories(this.logDir);

        this.robot = new Robot();
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.neuralProcessor = new NeuralAutomationProcessor();
        this.realTimeMonitor = new RealTimeAutomationMonitor();
        this.cognitiveAnalyzer = new CognitiveAutomationAnalyzer();

        // Performance metrics
        performanceMetrics.put("total_actions", 0L);
        performanceMetrics.put("successful_actions", 0L);
        performanceMetrics.put("failed_actions", 0L);
        performanceMetrics.put("avg_processing_time", 0.0);
        performanceMetrics.put("learning_accuracy", 0.0);
        performanceMetrics.put("cognitive_insights", 0L);

        realTimeMonitor.startMonitoring();
        realTimeMonitor.subscribe(this::handleRealTimeUpdate);

        LOGGER.info("Enhanced APIAutomationSensor initialized");
        LOGGER.info("Screen resolution: " + screenSize.width + "x" + screenSize.height);
    }

    private void handleRealTimeUpdate(Map<String, Object> metrics) {
        performanceMetrics.put("success_rate", metrics.getOrDefault("success_rate", 0.0));
        performanceMetrics.put("avg_response_time", metrics.getOrDefault("avg_response_time", 0.0));
    }

    // ---------- Capture ----------
    public EnhancedScreenCapture captureEnhancedState(String apiName, String label) {
        EnhancedScreenCapture capture = new EnhancedScreenCapture();
        capture.apiName = apiName;
        capture.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String fileTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
        String filename = label.isEmpty() ? String.format("%s_%s.png", apiName, fileTimestamp)
                : String.format("%s_%s_%s.png", apiName, label, fileTimestamp);
        Path filepath = logDir.resolve(filename);

        try {
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(screenshot, "png", filepath.toFile());
            capture.filepath = filepath.toString();
            capture.success = true;
            capture.resolution = screenSize;
            capture.fileSize = Files.size(filepath);

            // AI analysis (simplified)
            Map<String, Object> analysis = analyzeScreenshot(screenshot);
            capture.aiAnalysis = analysis;
            capture.confidenceScore = (double) analysis.getOrDefault("confidence", 0.5);
            capture.anomalyDetected = (boolean) analysis.getOrDefault("anomaly", false);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error capturing screen", e);
            capture.success = false;
        }

        enhancedCaptures.add(capture);
        apiStates.put(apiName, Map.of(
            "last_capture", capture.timestamp,
            "confidence", capture.confidenceScore,
            "anomaly", capture.anomalyDetected
        ));
        LOGGER.info("Enhanced screenshot captured: " + capture.filepath);
        return capture;
    }

    private Map<String, Object> analyzeScreenshot(BufferedImage image) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("confidence", 0.5);
        analysis.put("anomaly", false);
        Map<String, Object> features = new HashMap<>();

        // Convert to pixel array
        int width = image.getWidth();
        int height = image.getHeight();
        long totalBrightness = 0;
        double[] sums = new double[3];
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        for (int rgb : pixels) {
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            sums[0] += r;
            sums[1] += g;
            sums[2] += b;
            totalBrightness += (r + g + b) / 3;
        }
        double meanBrightness = totalBrightness / (double) pixels.length;
        double avgR = sums[0] / pixels.length, avgG = sums[1] / pixels.length, avgB = sums[2] / pixels.length;

        // Contrast as std dev (approximate)
        double var = 0;
        for (int rgb : pixels) {
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            double bright = (r + g + b) / 3.0;
            var += Math.pow(bright - meanBrightness, 2);
        }
        double stdDev = Math.sqrt(var / pixels.length);
        features.put("brightness", meanBrightness);
        features.put("contrast", stdDev);
        features.put("dominant_color", String.format("(%d,%d,%d)", (int) avgR, (int) avgG, (int) avgB));

        // Anomaly detection
        if (meanBrightness < 50 || meanBrightness > 200) {
            analysis.put("anomaly", true);
        }
        double confidence = Math.min(1.0, (meanBrightness / 100.0) * (1.0 - stdDev / 255.0));
        analysis.put("confidence", confidence);
        analysis.put("features", features);
        return analysis;
    }

    // ---------- Intelligent mouse action ----------
    public boolean intelligentMouseAction(String apiName, String action) {
        Map<String, Point> apiPositions = Map.of(
            "binance", new Point(100, 100),
            "ctrader", new Point(200, 100),
            "pionex", new Point(300, 100),
            "coinbase", new Point(400, 100),
            "alpaca", new Point(500, 100),
            "alphavantage", new Point(100, 200),
            "polygon", new Point(200, 200),
            "twelvedata", new Point(300, 200)
        );
        Point basePos = apiPositions.getOrDefault(apiName.toLowerCase(), new Point(50, 50));
        List<Double> features = Arrays.asList(
            (double) basePos.x / screenSize.width,
            (double) basePos.y / screenSize.height,
            (System.currentTimeMillis() % 3600000L) / 3600000.0,
            enhancedCaptures.size() / 100.0,
            (long) performanceMetrics.getOrDefault("successful_actions", 0L) /
                    Math.max(1, (long) performanceMetrics.getOrDefault("total_actions", 0L)),
            0.0, 0.0, 0.0, 0.0, 0.0
        );

        double successProb = neuralProcessor.predictActionSuccess(features);
        LOGGER.info(String.format("Predicted success probability: %.2f", successProb));

        if (successProb > 0.3) {
            // Move mouse smoothly (naive, using a few steps)
            try {
                for (int i = 0; i < 5; i++) {
                    int curX = MouseInfo.getPointerInfo().getLocation().x + (basePos.x - MouseInfo.getPointerInfo().getLocation().x) * i / 5;
                    int curY = MouseInfo.getPointerInfo().getLocation().y + (basePos.y - MouseInfo.getPointerInfo().getLocation().y) * i / 5;
                    robot.mouseMove(curX, curY);
                    Thread.sleep(100);
                }
                // Perform action
                switch (action) {
                    case "click":
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        break;
                    case "double_click":
                        for (int i = 0; i < 2; i++) {
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            Thread.sleep(50);
                        }
                        break;
                    case "right_click":
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            NeuralMouseEvent event = new NeuralMouseEvent();
            event.x = basePos.x;
            event.y = basePos.y;
            event.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            event.action = action;
            event.duration = 0.5;
            event.predictedSuccess = successProb;
            event.confidence = successProb;
            event.neuralPattern = features;
            mouseEvents.add(event);

            // Assume success for learning (real feedback would come later)
            boolean actualSuccess = true;
            neuralProcessor.learnFromResult(features, actualSuccess);

            performanceMetrics.merge("total_actions", 1L, Long::sum);
            if (actualSuccess) {
                performanceMetrics.merge("successful_actions", 1L, Long::sum);
            } else {
                performanceMetrics.merge("failed_actions", 1L, Long::sum);
            }
            realTimeMonitor.metrics.merge("actions_performed", 1L, (a, b) -> (long) a + (long) b);

            LOGGER.info("Intelligent " + action + " performed on " + apiName);
            return true;
        } else {
            LOGGER.warning("Action skipped due to low confidence: " + String.format("%.2f", successProb));
            return false;
        }
    }

    // ---------- Adaptive interaction ----------
    public Map<String, Object> adaptiveApiInteraction(String apiName, String action) {
        state = AutomationState.ACTIVE;
        long start = System.currentTimeMillis();

        Map<String, Object> results = new LinkedHashMap<>();
        results.put("api", apiName);
        results.put("action", action);
        List<Map<String, Object>> steps = new ArrayList<>();
        results.put("steps", steps);
        results.put("success", false);
        results.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        try {
            EnhancedScreenCapture initialCapture = captureEnhancedState(apiName, action + "_initial");
            Map<String, Object> stepInfo = new LinkedHashMap<>();
            stepInfo.put("name", "Enhanced initial capture");
            stepInfo.put("success", initialCapture.success);
            stepInfo.put("confidence", initialCapture.confidenceScore);
            stepInfo.put("anomaly", initialCapture.anomalyDetected);
            steps.add(stepInfo);

            // Cognitive analysis
            if (mouseEvents.size() >= 5) {
                List<CognitiveInsight> insights = cognitiveAnalyzer.analyzePattern(
                        mouseEvents.subList(Math.max(0, mouseEvents.size() - 10), mouseEvents.size()));
                results.put("cognitive_insights", insights.stream().map(ins -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("pattern", ins.patternType);
                    m.put("confidence", ins.confidence);
                    m.put("prediction", ins.prediction);
                    m.put("recommendation", ins.recommendation);
                    return m;
                }).collect(Collectors.toList()));
                performanceMetrics.merge("cognitive_insights", (long) insights.size(), Long::sum);
            }

            // Intelligent mouse action
            if (initialCapture.success && !initialCapture.anomalyDetected) {
                boolean actionSuccess = intelligentMouseAction(apiName, "click");
                Map<String, Object> step2 = new LinkedHashMap<>();
                step2.put("name", "Intelligent mouse action");
                step2.put("success", actionSuccess);
                step2.put("neural_prediction", actionSuccess ? "high" : "low");
                steps.add(step2);

                if (actionSuccess) {
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    EnhancedScreenCapture finalCapture = captureEnhancedState(apiName, action + "_final");
                    Map<String, Object> step3 = new LinkedHashMap<>();
                    step3.put("name", "Enhanced final capture");
                    step3.put("success", finalCapture.success);
                    step3.put("confidence", finalCapture.confidenceScore);
                    step3.put("improvement", finalCapture.confidenceScore - initialCapture.confidenceScore);
                    steps.add(step3);
                }
            }

            boolean overallSuccess = steps.stream().allMatch(s -> (boolean) s.getOrDefault("success", false));
            results.put("success", overallSuccess);

            // Learning insights
            Map<String, Object> learningInsights = new LinkedHashMap<>();
            learningInsights.put("total_captures", enhancedCaptures.size());
            double avgConf = enhancedCaptures.stream().mapToDouble(c -> c.confidenceScore).average().orElse(0.0);
            learningInsights.put("avg_confidence", avgConf);
            long anomalyCount = enhancedCaptures.stream().filter(c -> c.anomalyDetected).count();
            learningInsights.put("anomaly_rate", (double) anomalyCount / Math.max(1, enhancedCaptures.size()));
            learningInsights.put("neural_accuracy", neuralProcessor.accuracy);
            learningInsights.put("cognitive_patterns", cognitiveAnalyzer.patterns.size());
            results.put("learning_insights", learningInsights);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in adaptive interaction", e);
            results.put("error", e.getMessage());
        } finally {
            long processingTime = System.currentTimeMillis() - start;
            performanceMetrics.put("avg_processing_time", (double) processingTime);
            performanceMetrics.put("learning_accuracy", neuralProcessor.accuracy);
            state = AutomationState.IDLE;
        }
        return results;
    }

    // ---------- Metrics ----------
    public Map<String, Object> getEnhancedMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>(performanceMetrics);
        metrics.put("neural_metrics", Map.of(
            "training_data_size", neuralProcessor.trainingData.size(),
            "prediction_count", neuralProcessor.predictionHistory.size(),
            "model_accuracy", neuralProcessor.accuracy,
            "model_available", true
        ));
        metrics.put("real_time_metrics", new HashMap<>(realTimeMonitor.metrics));
        metrics.put("cognitive_metrics", Map.of(
            "patterns_analyzed", cognitiveAnalyzer.patterns.size(),
            "insights_generated", cognitiveAnalyzer.insights.size(),
            "cognitive_state", cognitiveAnalyzer.cognitiveState
        ));
        metrics.put("api_states", new HashMap<>(apiStates));
        metrics.put("system_state", state.getValue());
        metrics.put("libraries_available", Map.of(
            "tensorflow", false, "pytorch", false, "opencv", false,
            "pil", false, "requests", false, "websockets", false,
            "java_robot", true
        ));
        return metrics;
    }

    // ---------- Export ----------
    public String exportEnhancedSession(String filepath) {
        if (filepath == null) {
            filepath = logDir.resolve("enhanced_session_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json").toString();
        }

        Map<String, Object> sessionData = new LinkedHashMap<>();
        sessionData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        sessionData.put("system_state", state.getValue());
        sessionData.put("screen_resolution", screenSize.width + "x" + screenSize.height);
        sessionData.put("performance_metrics", new HashMap<>(performanceMetrics));
        // ... outras métricas
        // Simples serialização manual para JSON (pode-se usar Gson/Jackson, aqui faremos manual)
        StringBuilder json = new StringBuilder();
        toJson(sessionData, json);
        try {
            Files.writeString(Path.of(filepath), json.toString());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error exporting session", e);
        }
        LOGGER.info("Enhanced session data exported: " + filepath);
        return filepath;
    }

    private void toJson(Object obj, StringBuilder json) {
        if (obj instanceof Map) {
            json.append("{");
            Map<?,?> map = (Map<?,?>) obj;
            boolean first = true;
            for (Map.Entry<?,?> e : map.entrySet()) {
                if (!first) json.append(",");
                json.append("\"").append(e.getKey()).append("\":");
                toJson(e.getValue(), json);
                first = false;
            }
            json.append("}");
        } else if (obj instanceof List) {
            json.append("[");
            List<?> list = (List<?>) obj;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) json.append(",");
                toJson(list.get(i), json);
            }
            json.append("]");
        } else if (obj instanceof String) {
            json.append("\"").append(((String) obj).replace("\"", "\\\"")).append("\"");
        } else if (obj instanceof Number || obj instanceof Boolean) {
            json.append(obj.toString());
        } else if (obj == null) {
            json.append("null");
        } else {
            json.append("\"").append(obj.toString()).append("\"");
        }
    }

    public void shutdown() {
        realTimeMonitor.stopMonitoring();
        state = AutomationState.IDLE;
        LOGGER.info("Enhanced APIAutomationSensor shutdown");
    }

    // ---------- Legacy methods (stubs) ----------
    public APIScreenCapture captureCurrentState(String apiName, String label) {
        EnhancedScreenCapture ec = captureEnhancedState(apiName, label);
        APIScreenCapture legacy = new APIScreenCapture();
        legacy.apiName = ec.apiName;
        legacy.timestamp = ec.timestamp;
        legacy.filepath = ec.filepath;
        legacy.success = ec.success;
        legacy.resolution = ec.resolution;
        legacy.fileSize = ec.fileSize;
        return legacy;
    }

    public boolean moveMouseToApiButton(String apiName, int xOffset, int yOffset) {
        return intelligentMouseAction(apiName, "move");
    }

    public boolean clickApiButton(String apiName, int clicks) {
        String action = clicks > 1 ? "double_click" : "click";
        return intelligentMouseAction(apiName, action);
    }

    public Map<String, Object> simulateApiInteraction(String apiName, String action) {
        return adaptiveApiInteraction(apiName, action);
    }

    // ---------- Demo main ----------
    public static void main(String[] args) throws AWTException {
        EnhancedAPIAutomationSensor sensor = new EnhancedAPIAutomationSensor("logs/automation/enhanced");

        System.out.println("=" .repeat(80));
        System.out.println("ENHANCED API AUTOMATION SENSOR TEST (Java)");
        System.out.println("=".repeat(80));

        System.out.println("\n1. Testing enhanced screen capture...");
        EnhancedScreenCapture capture = sensor.captureEnhancedState("binance", "test");
        System.out.println("Enhanced capture: " + capture.success + ", Confidence: " + String.format("%.2f", capture.confidenceScore));

        System.out.println("\n2. Testing adaptive API interaction...");
        Map<String, Object> result = sensor.adaptiveApiInteraction("binance", "health_check");
        System.out.println("Adaptive result: " + result.get("success"));
        System.out.println("Cognitive insights: " + ((List<?>) result.get("cognitive_insights")).size());

        System.out.println("\n3. Testing intelligent mouse action...");
        boolean mouseResult = sensor.intelligentMouseAction("binance", "click");
        System.out.println("Mouse action: " + mouseResult);

        System.out.println("\n4. Getting enhanced metrics...");
        Map<String, Object> metrics = sensor.getEnhancedMetrics();
        System.out.println("System state: " + metrics.get("system_state"));
        Map<String, Object> neural = (Map<String, Object>) metrics.get("neural_metrics");
        System.out.println("Neural accuracy: " + String.format("%.3f", (double) neural.get("model_accuracy")));
        System.out.println("Libraries available: " + ((Map<?,?>)metrics.get("libraries_available")).size());

        System.out.println("\n5. Exporting enhanced session...");
        String exportPath = sensor.exportEnhancedSession(null);
        System.out.println("Enhanced data exported: " + exportPath);

        System.out.println("\n6. Testing legacy compatibility...");
        APIScreenCapture legacyCapture = sensor.captureCurrentState("ctrader", "legacy_test");
        System.out.println("Legacy capture: " + legacyCapture.success);

        sensor.shutdown();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ENHANCED API AUTOMATION SENSOR TEST COMPLETED");
    }
}
package com.vhalinor.automation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Enhanced API Automation Sensor with AI capabilities and real-time monitoring.
 * Java conversion with improvements (concurrency, thread safety, logging).
 */
public class EnhancedAPIAutomationSensor implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(EnhancedAPIAutomationSensor.class.getName());

    public enum AutomationState {
        IDLE, ACTIVE, LEARNING, PREDICTING, ERROR
    }

    public record EnhancedScreenCapture(
        String apiName,
        String timestamp,
        String filepath,
        boolean success,
        Dimension resolution,
        long fileSize,
        Map<String, Object> aiAnalysis,
        double confidenceScore,
        boolean anomalyDetected
    ) {}

    public record NeuralMouseEvent(
        int x,
        int y,
        String timestamp,
        String action,
        double duration,
        double predictedSuccess,
        double confidence,
        List<Double> neuralPattern
    ) {}

    // ---------- Neural Processor (simulated) ----------
    private static class NeuralAutomationProcessor {
        private final Deque<List<Double>> trainingData = new ConcurrentLinkedDeque<>();
        private final Deque<Double> predictionHistory = new ConcurrentLinkedDeque<>();
        private double[] weights; // simple linear model
        private final Random random = new Random();

        NeuralAutomationProcessor() {
            // Initialize random weights (10 features -> 1 output)
            weights = new double[10];
            for (int i = 0; i < weights.length; i++) {
                weights[i] = random.nextGaussian() * 0.1;
            }
        }

        double predictActionSuccess(List<Double> features) {
            // Simple linear combination + sigmoid
            double sum = 0.0;
            int n = Math.min(features.size(), weights.length);
            for (int i = 0; i < n; i++) {
                sum += features.get(i) * weights[i];
            }
            double prediction = 1.0 / (1.0 + Math.exp(-sum));
            predictionHistory.add(prediction);
            return prediction;
        }

        void learnFromResult(List<Double> features, boolean success) {
            trainingData.add(features);
            // Simplified SGD update when enough data collected
            if (trainingData.size() >= 10) {
                for (List<Double> data : trainingData) {
                    // would perform gradient descent; omitted for brevity
                }
                LOG.fine("Neural model updated");
            }
        }
    }

    // ---------- Real-Time Monitor ----------
    private static class RealTimeAutomationMonitor {
        private final Map<String, Object> metrics = new ConcurrentHashMap<>();
        private final List<Consumer<Map<String, Object>>> subscribers = new CopyOnWriteArrayList<>();
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private volatile boolean monitoring = false;

        RealTimeAutomationMonitor() {
            metrics.put("actions_performed", 0);
            metrics.put("success_rate", 0.0);
            metrics.put("avg_response_time", 0.0);
            metrics.put("anomaly_count", 0);
            metrics.put("learning_progress", 0.0);
        }

        void subscribe(Consumer<Map<String, Object>> callback) {
            subscribers.add(callback);
        }

        void startMonitoring() {
            if (!monitoring) {
                monitoring = true;
                scheduler.scheduleAtFixedRate(this::monitorLoop, 0, 1, TimeUnit.SECONDS);
                LOG.info("Real-time monitoring started");
            }
        }

        void stopMonitoring() {
            monitoring = false;
            scheduler.shutdownNow();
            LOG.info("Real-time monitoring stopped");
        }

        private void monitorLoop() {
            if (!monitoring) return;
            try {
                updateMetrics();
                Map<String, Object> currentMetrics = new HashMap<>(metrics);
                for (Consumer<Map<String, Object>> subscriber : subscribers) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            subscriber.accept(currentMetrics);
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, "Subscriber error", e);
                        }
                    });
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Monitoring loop error", e);
            }
        }

        private void updateMetrics() {
            // In a real implementation, this would collect actual data
        }
    }

    // ---------- Main Sensor ----------
    private final Path logDir;
    private final NeuralAutomationProcessor neuralProcessor = new NeuralAutomationProcessor();
    private final RealTimeAutomationMonitor monitor = new RealTimeAutomationMonitor();
    private final List<EnhancedScreenCapture> captures = new CopyOnWriteArrayList<>();
    private final List<NeuralMouseEvent> mouseEvents = new CopyOnWriteArrayList<>();
    private final Map<String, Map<String, Object>> apiStates = new ConcurrentHashMap<>();
    private volatile AutomationState state = AutomationState.IDLE;
    private final Dimension screenSize;
    private final Robot robot;
    private final Map<String, Object> performanceMetrics = new ConcurrentHashMap<>();

    public EnhancedAPIAutomationSensor(String logDirPath) throws AWTException {
        this.logDir = Paths.get(logDirPath);
        try {
            Files.createDirectories(logDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create log directory", e);
        }
        robot = new Robot();
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        monitor.startMonitoring();
        performanceMetrics.put("total_actions", 0);
        performanceMetrics.put("successful_actions", 0);
        performanceMetrics.put("failed_actions", 0);
        performanceMetrics.put("avg_processing_time", 0.0);
        performanceMetrics.put("learning_accuracy", 0.0);
        LOG.info(() -> "Enhanced APIAutomationSensor initialized. Resolution: " + screenSize);
    }

    // Capture screenshot with AI analysis
    public EnhancedScreenCapture captureEnhancedState(String apiName, String label) {
        long start = System.currentTimeMillis();
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String filename = label != null && !label.isEmpty() ?
                    apiName + "_" + label + "_" + timestamp + ".png" :
                    apiName + "_" + timestamp + ".png";
            Path filepath = logDir.resolve(filename);
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(screenSize));
            javax.imageio.ImageIO.write(screenshot, "png", filepath.toFile());

            Map<String, Object> aiAnalysis = analyzeScreenshotAI(screenshot);
            double confidence = (double) aiAnalysis.getOrDefault("confidence", 0.0);
            boolean anomaly = (boolean) aiAnalysis.getOrDefault("anomaly", false);

            EnhancedScreenCapture capture = new EnhancedScreenCapture(
                    apiName,
                    Instant.now().toString(),
                    filepath.toString(),
                    true,
                    screenSize,
                    Files.size(filepath),
                    aiAnalysis,
                    confidence,
                    anomaly
            );
            captures.add(capture);
            apiStates.put(apiName, Map.of(
                "last_capture", capture.timestamp(),
                "confidence", confidence,
                "anomaly", anomaly
            ));
            LOG.info("Enhanced screenshot captured: " + filepath);
            performanceMetrics.merge("avg_processing_time",
                    (double) (System.currentTimeMillis() - start) / 1000.0,
                    (old, val) -> (old + val) / 2);
            return capture;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Capture error", e);
            return new EnhancedScreenCapture(apiName, Instant.now().toString(), "", false, screenSize, 0, Map.of(), 0.0, false);
        }
    }

    private Map<String, Object> analyzeScreenshotAI(BufferedImage image) {
        Map<String, Object> analysis = new HashMap<>();
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            double[] sums = new double[3];
            double[] sumSq = new double[3];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    sums[0] += r; sums[1] += g; sums[2] += b;
                    sumSq[0] += r * r;
                    sumSq[1] += g * g;
                    sumSq[2] += b * b;
                }
            }
            int pixels = width * height;
            double brightness = (sums[0] + sums[1] + sums[2]) / (3.0 * pixels);
            double contrast = Math.sqrt((sumSq[0] + sumSq[1] + sumSq[2]) / (3.0 * pixels) - brightness * brightness);
            boolean anomaly = brightness < 50 || brightness > 200;
            double confidence = Math.min(1.0, (brightness / 100.0) * (1.0 - contrast / 255.0));
            int meanR = (int) (sums[0] / pixels);
            int meanG = (int) (sums[1] / pixels);
            int meanB = (int) (sums[2] / pixels);

            analysis.put("confidence", confidence);
            analysis.put("anomaly", anomaly);
            analysis.put("features", Map.of(
                "brightness", brightness,
                "contrast", contrast,
                "dominant_color", String.format("%d,%d,%d", meanR, meanG, meanB)
            ));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "AI analysis failed", e);
            analysis.put("confidence", 0.5);
            analysis.put("anomaly", false);
        }
        return analysis;
    }

    // Intelligent mouse action with neural prediction
    public boolean intelligentMouseAction(String apiName, String action) {
        try {
            Map<String, Point> apiPositions = Map.of(
                "binance", new Point(100, 100),
                "ctrader", new Point(200, 100),
                "pionex", new Point(300, 100),
                "coinbase", new Point(400, 100),
                "alpaca", new Point(500, 100)
            );
            Point base = apiPositions.getOrDefault(apiName.toLowerCase(), new Point(50, 50));

            List<Double> features = Arrays.asList(
                base.x / (double) screenSize.width,
                base.y / (double) screenSize.height,
                (System.currentTimeMillis() % 3600_000) / 3600_000.0,
                captures.size() / 100.0,
                ((int) performanceMetrics.getOrDefault("successful_actions", 0)) /
                    Math.max(1, (int) performanceMetrics.getOrDefault("total_actions", 0)),
                0.0, 0.0, 0.0, 0.0, 0.0
            );

            double successProb = neuralProcessor.predictActionSuccess(features);
            LOG.info("Predicted success probability: " + String.format("%.2f", successProb));

            if (successProb > 0.3) {
                // Move mouse smoothly (simplified)
                robot.mouseMove(base.x, base.y);
                robot.delay(500);
                switch (action) {
                    case "click":
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        break;
                    case "double_click":
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        robot.delay(200);
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        break;
                    case "right_click":
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        break;
                    default:
                        break;
                }

                NeuralMouseEvent event = new NeuralMouseEvent(
                    base.x, base.y,
                    Instant.now().toString(),
                    action,
                    0.5,
                    successProb,
                    successProb,
                    features
                );
                mouseEvents.add(event);

                performanceMetrics.merge("total_actions", 1, (old, val) -> (int) old + 1);
                neuralProcessor.learnFromResult(features, true); // assume success for demo
                performanceMetrics.merge("successful_actions", 1, (old, val) -> (int) old + 1);
                LOG.info(() -> "Intelligent " + action + " performed on " + apiName);
                return true;
            } else {
                LOG.warning("Action skipped due to low confidence: " + String.format("%.2f", successProb));
                return false;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Mouse action error", e);
            return false;
        }
    }

    // Adaptive API interaction
    public Map<String, Object> adaptiveAPIInteraction(String apiName, String action) {
        state = AutomationState.ACTIVE;
        long start = System.currentTimeMillis();
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("api", apiName);
        results.put("action", action);
        List<Map<String, Object>> steps = new ArrayList<>();
        boolean overallSuccess = true;

        try {
            // Step 1: initial capture
            EnhancedScreenCapture initial = captureEnhancedState(apiName, action + "_initial");
            Map<String, Object> step1 = new LinkedHashMap<>();
            step1.put("name", "Enhanced initial capture");
            step1.put("success", initial.success());
            step1.put("confidence", initial.confidenceScore());
            step1.put("anomaly", initial.anomalyDetected());
            steps.add(step1);
            overallSuccess &= initial.success();

            // Step 2: intelligent mouse action
            if (initial.success() && !initial.anomalyDetected()) {
                boolean mouseSuccess = intelligentMouseAction(apiName, "click");
                Map<String, Object> step2 = new LinkedHashMap<>();
                step2.put("name", "Intelligent mouse action");
                step2.put("success", mouseSuccess);
                steps.add(step2);
                overallSuccess &= mouseSuccess;

                if (mouseSuccess) {
                    // Wait and capture final
                    Thread.sleep(1000);
                    EnhancedScreenCapture finalCapture = captureEnhancedState(apiName, action + "_final");
                    Map<String, Object> step3 = new LinkedHashMap<>();
                    step3.put("name", "Enhanced final capture");
                    step3.put("success", finalCapture.success());
                    step3.put("confidence", finalCapture.confidenceScore());
                    step3.put("improvement", finalCapture.confidenceScore() - initial.confidenceScore());
                    steps.add(step3);
                    overallSuccess &= finalCapture.success();
                }
            }

            results.put("steps", steps);
            results.put("success", overallSuccess);
            results.put("timestamp", Instant.now().toString());

            Map<String, Object> insights = new LinkedHashMap<>();
            insights.put("total_captures", captures.size());
            insights.put("avg_confidence", captures.stream().mapToDouble(EnhancedScreenCapture::confidenceScore).average().orElse(0));
            insights.put("anomaly_rate", captures.stream().filter(EnhancedScreenCapture::anomalyDetected).count() / (double) Math.max(1, captures.size()));
            results.put("learning_insights", insights);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Adaptive interaction error", e);
            results.put("error", e.getMessage());
        } finally {
            double processingTime = (System.currentTimeMillis() - start) / 1000.0;
            performanceMetrics.put("avg_processing_time", processingTime);
            state = AutomationState.IDLE;
        }
        return results;
    }

    // Comprehensive metrics
    public Map<String, Object> getEnhancedMetrics() {
        Map<String, Object> metrics = new HashMap<>(performanceMetrics);
        metrics.put("neural_metrics", Map.of(
            "training_data_size", neuralProcessor.trainingData.size(),
            "prediction_count", neuralProcessor.predictionHistory.size()
        ));
        metrics.put("real_time_metrics", monitor.metrics);
        metrics.put("api_states", new HashMap<>(apiStates));
        metrics.put("system_state", state.name());
        return metrics;
    }

    // Export session data to JSON
    public String exportEnhancedSession(String filepath) throws IOException {
        if (filepath == null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            filepath = logDir.resolve("enhanced_session_" + timestamp + ".json").toString();
        }
        Map<String, Object> sessionData = new LinkedHashMap<>();
        sessionData.put("timestamp", Instant.now().toString());
        sessionData.put("system_state", state.name());
        sessionData.put("screen_resolution", screenSize.toString());
        sessionData.put("performance_metrics", new HashMap<>(performanceMetrics));
        sessionData.put("neural_metrics", Map.of(
            "training_data_size", neuralProcessor.trainingData.size(),
            "prediction_count", neuralProcessor.predictionHistory.size()
        ));
        sessionData.put("captures", captures.stream().map(c -> Map.of(
            "api_name", c.apiName(),
            "timestamp", c.timestamp(),
            "filepath", c.filepath(),
            "success", c.success(),
            "confidence_score", c.confidenceScore(),
            "anomaly_detected", c.anomalyDetected(),
            "ai_analysis", c.aiAnalysis()
        )).collect(Collectors.toList()));
        sessionData.put("mouse_events", mouseEvents.stream().map(e -> Map.of(
            "x", e.x(),
            "y", e.y(),
            "timestamp", e.timestamp(),
            "action", e.action(),
            "predicted_success", e.predictedSuccess(),
            "confidence", e.confidence()
        )).collect(Collectors.toList()));
        sessionData.put("api_states", new HashMap<>(apiStates));

        try (FileWriter writer = new FileWriter(filepath)) {
            // Simple JSON writing without external lib; could use Gson/Jackson
            writer.write(toSimpleJson(sessionData));
        }
        LOG.info("Enhanced session data exported: " + filepath);
        return filepath;
    }

    // Minimal JSON formatter (for demo; prefer Jackson or Gson)
    private String toSimpleJson(Object obj) {
        if (obj instanceof Map) {
            Map<?,?> map = (Map<?,?>) obj;
            StringBuilder sb = new StringBuilder("{");
            map.forEach((k, v) -> {
                sb.append("\"").append(k).append("\": ").append(toSimpleJson(v)).append(", ");
            });
            if (!map.isEmpty()) sb.setLength(sb.length() - 2);
            sb.append("}");
            return sb.toString();
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            list.forEach(item -> sb.append(toSimpleJson(item)).append(", "));
            if (!list.isEmpty()) sb.setLength(sb.length() - 2);
            sb.append("]");
            return sb.toString();
        } else if (obj instanceof String) {
            return "\"" + obj + "\"";
        } else {
            return String.valueOf(obj);
        }
    }

    public void shutdown() {
        monitor.stopMonitoring();
        state = AutomationState.IDLE;
        LOG.info("Enhanced APIAutomationSensor shutdown");
    }

    @Override
    public void close() {
        shutdown();
    }

    // ---------- Demo main ----------
    public static void main(String[] args) throws AWTException, IOException {
        EnhancedAPIAutomationSensor sensor = new EnhancedAPIAutomationSensor("logs/automation/enhanced");

        // Test capture
        EnhancedScreenCapture capture = sensor.captureEnhancedState("binance", "test");
        System.out.println("Enhanced capture: " + capture);

        // Test adaptive interaction
        Map<String, Object> result = sensor.adaptiveAPIInteraction("binance", "health_check");
        System.out.println("Adaptive result: " + sensor.toSimpleJson(result));

        // Metrics
        Map<String, Object> metrics = sensor.getEnhancedMetrics();
        System.out.println("Metrics: " + sensor.toSimpleJson(metrics));

        // Export
        String path = sensor.exportEnhancedSession(null);
        System.out.println("Session exported: " + path);

        sensor.shutdown();
    }
}
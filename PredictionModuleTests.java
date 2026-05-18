package com.vhalinor.trader.prediction.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import com.vhalinor.core.VhalinorException;
import com.vhalinor.core.ModelError;
import com.vhalinor.core.ValidationError;
import com.vhalinor.prediction.*;

/**
 * VHALINOR TRADER - Prediction Module Tests (Java Edition)
 * ==========================================================================
 * Comprehensive test suite for the prediction module.
 *
 * Features:
 * - Unit tests for all prediction components
 * - Integration tests for ensemble methods
 * - Performance tests
 * - Mock data generation
 * - Error handling validation
 * - Coverage reporting
 *
 * Version: 5.0 Enhanced
 * Date: March 2026
 */
@DisplayName("Prediction Module Tests")
public class PredictionModuleTests {

    // ============================================================================
    // Helper: synchronous wait for CompletableFuture
    // ============================================================================
    private static <T> T sync(CompletableFuture<T> future) {
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    // ============================================================================
    // Helper: mock config
    // ============================================================================
    private com.vhalinor.core.Configuration createMockConfig() {
        com.vhalinor.core.Configuration config = mock(com.vhalinor.core.Configuration.class);
        com.vhalinor.core.Configuration.AIConfig ai = mock(com.vhalinor.core.Configuration.AIConfig.class);
        when(config.getAi()).thenReturn(ai);
        when(ai.getModelPath()).thenReturn("./test_models");
        when(ai.getTrainingDataPath()).thenReturn("./test_data");
        return config;
    }

    // ============================================================================
    // Sample test data factory
    // ============================================================================
    private Map<String, Object> sampleFeatures() {
        Map<String, Object> features = new HashMap<>();
        features.put("price", 100.0);
        features.put("volume", 1_000_000.0);
        features.put("volatility", 0.02);
        features.put("trend", 0.01);
        features.put("momentum", 0.005);
        features.put("rsi", 50.0);
        features.put("macd", 0.1);
        features.put("bollinger_upper", 102.0);
        features.put("bollinger_lower", 98.0);
        features.put("sentiment_score", 0.5);
        return features;
    }

    private List<Map<String, Object>> sampleTrainingData() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("close", 100.0, "volume", 1_000_000.0, "high", 102.0, "low", 98.0, "sentiment", 0.5));
        data.add(Map.of("close", 101.0, "volume", 1_100_000.0, "high", 103.0, "low", 99.0, "sentiment", 0.6));
        data.add(Map.of("close", 99.0, "volume", 950_000.0, "high", 101.0, "low", 97.0, "sentiment", 0.4));
        data.add(Map.of("close", 102.0, "volume", 1_200_000.0, "high", 104.0, "low", 98.0, "sentiment", 0.7));
        data.add(Map.of("close", 98.0, "volume", 900_000.0, "high", 100.0, "low", 96.0, "sentiment", 0.3));
        return data;
    }

    // ============================================================================
    // Test: Predictor
    // ============================================================================
    @Nested
    @DisplayName("Predictor Tests")
    class PredictorTests {
        private com.vhalinor.prediction.Predictor predictor;
        private com.vhalinor.core.Configuration config;
        private Map<String, Object> features;
        private List<Map<String, Object>> trainingData;

        @BeforeEach
        void setUp() {
            config = createMockConfig();
            predictor = new Predictor(config);
            features = sampleFeatures();
            trainingData = sampleTrainingData();
        }

        @Test
        @DisplayName("Predictor initialization")
        void testPredictorInitialization() {
            assertNotNull(predictor);
            assertEquals(config, predictor.getConfig());
            assertNotNull(predictor.getLogger());
            assertNotNull(predictor.getModels());
            assertNotNull(predictor.getEnsembleWeights());
        }

        @Test
        @DisplayName("Feature extraction")
        void testExtractFeatures() {
            double[] extracted = predictor.extractFeatures(features);
            assertNotNull(extracted);
            // expecting 10 features
            assertEquals(10, extracted.length);
        }

        @Test
        @DisplayName("Basic prediction")
        void testPredictBasic() {
            PredictionResult result = sync(predictor.predict("BTC/USD", features));
            assertNotNull(result);
            assertEquals("BTC/USD", result.getSymbol());
            assertTrue(Double.isFinite(result.getPrediction()));
            assertTrue(result.getConfidence() >= 0.0 && result.getConfidence() <= 1.0);
            assertEquals("price", result.getPredictionType());
            assertEquals("1h", result.getTimeframe());
        }

        @Test
        @DisplayName("Prediction with invalid features")
        void testPredictWithInvalidFeatures() {
            assertThrows(ModelError.class, () -> sync(predictor.predict("BTC/USD", Map.of())));
        }

        @Test
        @DisplayName("Ensemble prediction")
        void testEnsemblePredict() {
            Map<String, Double> predictions = Map.of("model1", 100.5, "model2", 99.8, "model3", 100.2);
            Map<String, Double> confidences = Map.of("model1", 0.8, "model2", 0.7, "model3", 0.9);

            Map<String, Double> result = predictor.ensemblePredict(predictions, confidences);
            assertTrue(result.containsKey("prediction"));
            assertTrue(result.containsKey("confidence"));
            assertTrue(Double.isFinite(result.get("prediction")));
            assertTrue(Double.isFinite(result.get("confidence")));
        }

        @Test
        @DisplayName("Ensemble prediction with empty data")
        void testEnsemblePredictEmpty() {
            Map<String, Double> result = predictor.ensemblePredict(Map.of(), Map.of());
            assertEquals(0.0, result.get("prediction"));
            assertEquals(0.0, result.get("confidence"));
        }

        @Test
        @DisplayName("Model training")
        void testTrainModels() {
            Map<String, Object> results = sync(predictor.trainModels(trainingData));
            assertNotNull(results);
            assertFalse(results.isEmpty());
        }

        @Test
        @DisplayName("Model training with insufficient data")
        void testTrainModelsInsufficientData() {
            Map<String, Object> results = sync(predictor.trainModels(List.of()));
            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Volatility calculation")
        void testCalculateVolatility() {
            double vol = predictor.calculateVolatility(trainingData, 2);
            assertTrue(Double.isFinite(vol) && vol >= 0.0);
        }

        @Test
        @DisplayName("Trend calculation")
        void testCalculateTrend() {
            double trend = predictor.calculateTrend(trainingData, 2);
            assertTrue(Double.isFinite(trend));
        }

        @Test
        @DisplayName("Momentum calculation")
        void testCalculateMomentum() {
            double momentum = predictor.calculateMomentum(trainingData, 2);
            assertTrue(Double.isFinite(momentum));
        }

        @Test
        @DisplayName("RSI calculation")
        void testCalculateRSI() {
            double rsi = predictor.calculateRSI(trainingData, 3);
            assertTrue(rsi >= 0.0 && rsi <= 100.0);
        }

        @Test
        @DisplayName("MACD calculation")
        void testCalculateMACD() {
            double macd = predictor.calculateMACD(trainingData, 3);
            assertTrue(Double.isFinite(macd));
        }

        @Test
        @DisplayName("Support/Resistance (Bollinger) calculation")
        void testFindSupportResistance() {
            double[] closes = {100, 101, 99, 102, 98};
            double[] bands = predictor.findSupportResistance(closes);
            assertEquals(2, bands.length);
            assertTrue(bands[0] >= bands[1]); // upper >= lower
        }

        @Test
        @DisplayName("Performance statistics")
        void testGetPerformanceStats() {
            Map<String, Object> stats = predictor.getPerformanceStats();
            assertNotNull(stats);
            assertTrue(stats.containsKey("prediction_count"));
            assertTrue(stats.containsKey("models_loaded"));
            assertTrue(stats.containsKey("libraries_available"));
        }

        @Test
        @DisplayName("PredictionResult serialization")
        void testPredictionResultSerialization() {
            PredictionResult result = new PredictionResult("BTC/USD", 100.5, 0.85, "price", "1h");
            Map<String, Object> dict = result.toDict();
            assertEquals("BTC/USD", dict.get("symbol"));
            assertEquals(100.5, (Double) dict.get("prediction"), 0.001);
            assertEquals(0.85, (Double) dict.get("confidence"), 0.001);
            assertEquals("price", dict.get("prediction_type"));
            assertEquals("1h", dict.get("timeframe"));
        }
    }

    // ============================================================================
    // Test: EnsemblePredictor
    // ============================================================================
    @Nested
    @DisplayName("Ensemble Predictor Tests")
    class EnsemblePredictorTests {
        private EnsemblePredictor ensemblePredictor;
        private com.vhalinor.core.Configuration config;
        private Map<String, Object> features;

        @BeforeEach
        void setUp() {
            config = createMockConfig();
            ensemblePredictor = new EnsemblePredictor(config);
            features = sampleFeatures();
        }

        @Test
        @DisplayName("Ensemble predictor initialization")
        void testInitialization() {
            assertNotNull(ensemblePredictor);
            assertTrue(ensemblePredictor.isDynamicWeights());
            assertNotNull(ensemblePredictor.getPerformanceTracker());
        }

        @Test
        @DisplayName("Dynamic ensemble weights update")
        void testDynamicEnsembleWeights() {
            ensemblePredictor.getPerformanceTracker().put("random_forest", new double[]{0.8, 0.85, 0.82});
            ensemblePredictor.getPerformanceTracker().put("gradient_boosting", new double[]{0.7, 0.75, 0.78});
            ensemblePredictor.getPerformanceTracker().put("linear_regression", new double[]{0.6, 0.65, 0.62});

            ensemblePredictor.updateEnsembleWeights();

            Map<String, Double> weights = ensemblePredictor.getEnsembleWeights();
            assertTrue(weights.containsKey("random_forest"));
            assertTrue(weights.containsKey("gradient_boosting"));
            assertTrue(weights.containsKey("linear_regression"));

            double sum = weights.values().stream().mapToDouble(Double::doubleValue).sum();
            assertEquals(1.0, sum, 0.001);
        }

        @Test
        @DisplayName("Prediction with dynamic ensemble")
        void testPredictWithDynamicEnsemble() {
            PredictionResult result = sync(ensemblePredictor.predictWithDynamicEnsemble("BTC/USD", features));
            assertNotNull(result);
            assertEquals("BTC/USD", result.getSymbol());
            assertTrue(Double.isFinite(result.getPrediction()));
            assertTrue(Double.isFinite(result.getConfidence()));
        }
    }

    // ============================================================================
    // Integration Tests
    // ============================================================================
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        private Predictor predictor;
        private EnsemblePredictor ensemblePredictor;
        private List<Map<String, Object>> largeDataset;

        @BeforeEach
        void setUp() {
            com.vhalinor.core.Configuration config = createMockConfig();
            predictor = new Predictor(config);
            ensemblePredictor = new EnsemblePredictor(config);

            largeDataset = new ArrayList<>();
            Random rand = new Random(42);
            for (int i = 0; i < 100; i++) {
                double basePrice = 100 + i * 0.1;
                largeDataset.add(Map.of(
                    "close", basePrice + rand.nextGaussian() * 0.5,
                    "volume", 1_000_000.0 + i * 10_000.0,
                    "high", basePrice + 2,
                    "low", basePrice - 2,
                    "sentiment", 0.5 + rand.nextGaussian() * 0.1
                ));
            }
        }

        @Test
        @DisplayName("End-to-end prediction workflow")
        void testEndToEndPredictionWorkflow() {
            Map<String, Object> trainingResults = sync(predictor.trainModels(largeDataset));
            assertNotNull(trainingResults);

            Map<String, Object> features = sampleFeatures();
            features.put("price", 110.0);
            features.put("volume", 2_000_000.0);

            PredictionResult prediction = sync(predictor.predict("BTC/USD", features));
            assertNotNull(prediction);
            assertTrue(prediction.getConfidence() > 0.0);
            assertTrue(prediction.getConfidence() <= 1.0);
        }

        @Test
        @DisplayName("Multiple predictions performance")
        void testMultiplePredictionsPerformance() {
            Map<String, Object> features = sampleFeatures();
            long start = System.nanoTime();
            List<PredictionResult> predictions = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                predictions.add(sync(predictor.predict("SYMBOL_" + i, features)));
            }
            long end = System.nanoTime();
            double totalSec = (end - start) / 1_000_000_000.0;

            assertEquals(10, predictions.size());
            assertTrue(totalSec < 5.0, "Total time too high: " + totalSec);
            double avgSec = totalSec / 10;
            assertTrue(avgSec < 0.5, "Average time per prediction too high: " + avgSec);
        }

        @Test
        @DisplayName("Concurrent predictions")
        void testConcurrentPredictions() {
            Map<String, Object> features = sampleFeatures();
            List<CompletableFuture<PredictionResult>> tasks = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                tasks.add(predictor.predict("SYMBOL_" + i, features));
            }

            List<PredictionResult> predictions = tasks.stream()
                .map(PredictionModuleTests::sync)
                .collect(Collectors.toList());

            assertEquals(5, predictions.size());
            predictions.forEach(p -> assertInstanceOf(PredictionResult.class, p));
        }

        @Test
        @DisplayName("Memory usage during training")
        void testMemoryUsageDuringTraining() {
            Runtime runtime = Runtime.getRuntime();
            long memBefore = runtime.totalMemory() - runtime.freeMemory();

            List<Map<String, Object>> bigDataset = new ArrayList<>(largeDataset);
            for (int i = 0; i < 9; i++) bigDataset.addAll(largeDataset); // 1000 points
            sync(predictor.trainModels(bigDataset));

            long memAfter = runtime.totalMemory() - runtime.freeMemory();
            long memIncrease = memAfter - memBefore;

            // Memory increase less than 100 MB
            assertTrue(memIncrease < 100L * 1024 * 1024, "Memory increase too high: " + (memIncrease / (1024*1024)) + " MB");
        }

        @Test
        @DisplayName("Error handling during prediction")
        void testErrorHandlingDuringPrediction() {
            Map<String, Object> corruptedFeatures = new HashMap<>();
            corruptedFeatures.put("price", "invalid");
            corruptedFeatures.put("volume", null);
            corruptedFeatures.put("volatility", Double.POSITIVE_INFINITY);
            corruptedFeatures.put("trend", "not_a_number");

            assertThrows(Exception.class, () -> sync(predictor.predict("BTC/USD", corruptedFeatures)));
        }
    }

    // ============================================================================
    // Performance Tests
    // ============================================================================
    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {
        private com.vhalinor.core.Configuration config;
        private Predictor predictor;
        private List<Map<String, Object>> performanceData;

        @BeforeEach
        void setUp() {
            config = createMockConfig();
            predictor = new Predictor(config);

            performanceData = new ArrayList<>();
            Random rand = new Random(42);
            for (int i = 0; i < 1000; i++) {
                performanceData.add(Map.of(
                    "close", 100 + i * 0.01,
                    "volume", 1_000_000.0 + i * 1000.0,
                    "high", 102 + i * 0.01,
                    "low", 98 + i * 0.01,
                    "sentiment", 0.5 + rand.nextGaussian() * 0.01
                ));
            }
        }

        @Test
        @DisplayName("Training performance")
        void testTrainingPerformance() {
            long start = System.nanoTime();
            Map<String, Object> results = sync(predictor.trainModels(performanceData));
            long end = System.nanoTime();
            double trainingSec = (end - start) / 1_000_000_000.0;

            assertNotNull(results);
            assertTrue(trainingSec < 30.0, "Training took too long: " + trainingSec + "s");
        }

        @Test
        @DisplayName("Prediction latency")
        void testPredictionLatency() {
            Map<String, Object> features = sampleFeatures();
            List<Double> latencies = new ArrayList<>();

            for (int i = 0; i < 100; i++) {
                long s = System.nanoTime();
                PredictionResult result = sync(predictor.predict("SYMBOL_" + i, features));
                long e = System.nanoTime();
                latencies.add((e - s) / 1_000_000_000.0);
                assertNotNull(result);
            }

            double avg = latencies.stream().mapToDouble(d -> d).average().orElse(0);
            double max = latencies.stream().mapToDouble(d -> d).max().orElse(0);
            double min = latencies.stream().mapToDouble(d -> d).min().orElse(0);

            System.out.printf("Prediction Performance:%n  Average latency: %.2fms%n  Max latency: %.2fms%n  Min latency: %.2fms%n",
                    avg * 1000, max * 1000, min * 1000);

            assertTrue(avg < 0.1, "Average latency too high: " + avg);
            assertTrue(max < 0.5, "Max latency too high: " + max);
            assertTrue(min > 0.001, "Min latency too low: " + min);
        }

        @Test
        @DisplayName("Memory efficiency")
        void testMemoryEfficiency() {
            Runtime runtime = Runtime.getRuntime();
            long memBefore = runtime.totalMemory() - runtime.freeMemory();

            Map<String, Object> features = sampleFeatures();
            for (int i = 0; i < 500; i++) {
                sync(predictor.predict("SYMBOL_" + i, features));
            }

            long memAfter = runtime.totalMemory() - runtime.freeMemory();
            long memIncrease = memAfter - memBefore;

            System.out.printf("Memory efficiency:%n  Initial memory: %.2fMB%n  Final memory: %.2fMB%n  Memory increase: %.2fMB%n",
                    memBefore / (1024.0 * 1024.0), memAfter / (1024.0 * 1024.0), memIncrease / (1024.0 * 1024.0));

            assertTrue(memIncrease < 50L * 1024 * 1024, "Memory increase too high: " + (memIncrease / (1024*1024)) + " MB");
        }
    }

    // ============================================================================
    // Runner (optional)
    // ============================================================================
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("VHALINOR TRADER - Prediction Module Tests (Java)");
        System.out.println("=".repeat(80));

        try {
            // Launch JUnit programmatically
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(selectClass(PredictionModuleTests.class))
                    .build();
            Launcher launcher = LauncherFactory.create();
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);

            TestExecutionSummary summary = listener.getSummary();
            long total = summary.getTestsFoundCount();
            long failures = summary.getTestsFailedCount();
            long aborted = summary.getTestsAbortedCount();
            long succeeded = summary.getTestsSucceededCount();

            System.out.println("\nTest Summary:");
            System.out.printf("Tests run: %d%n", total);
            System.out.printf("Failures: %d%n", failures);
            System.out.printf("Errors: %d%n", aborted);
            if (total > 0) {
                double rate = (double) succeeded / total * 100.0;
                System.out.printf("Success rate: %.1f%%%n", rate);
            }

            if (failures > 0 || aborted > 0) {
                System.out.println("\nFailures:");
                summary.getFailures().forEach(f ->
                    System.out.println("  " + f.getTestIdentifier().getDisplayName() + ": " + f.getException())
                );
            }

            System.exit(failures == 0 && aborted == 0 ? 0 : 1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
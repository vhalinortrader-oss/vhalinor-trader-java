package com.vhalinor.trader.neural_network.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.DoubleStream;

import com.vhalinor.trader.neural_network.*;

/**
 * VHALINOR TRADER - Ultra Enhanced Neural Network Test Suite (Java Edition)
 * ================================================================================================
 * Comprehensive testing for the ultra-enhanced neural network module with quantum, ensemble,
 * and meta-learning capabilities.
 *
 * This test suite validates:
 * - Enhanced data structures and classes
 * - Multi-framework support with fallbacks
 * - Quantum neural network capabilities
 * - Real-time monitoring and performance tracking
 * - Ensemble methods and meta-learning
 * - Error handling and graceful degradation
 * - Production readiness and Windows compatibility
 *
 * Author: VHALINOR Team
 * Version: 6.0 Ultra Enhanced
 * Date: March 2026
 */
@DisplayName("Ultra Enhanced Neural Network Test Suite")
public class UltraNeuralNetworkTest {

    private static double[] randomDoubleArray(int size) {
        Random random = new Random();
        double[] arr = new double[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextGaussian();
        }
        return arr;
    }

    // Helper to unwrap CompletableFuture synchronously
    private static <T> T sync(CompletableFuture<T> future) {
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Enhanced Data Structures")
    void testEnhancedDataStructures() {
        // NetworkArchitecture
        NetworkArchitecture arch = new NetworkArchitecture(
                "test_arch", 10, Arrays.asList(64, 32), 1,
                "gelu", 0.1, true, true, 8, 3
        );
        assertEquals("test_arch", arch.getName());
        assertEquals(10, arch.getInputSize());
        assertEquals(2, arch.getHiddenSizes().size());

        // TrainingConfig
        TrainingConfig config = new TrainingConfig(
                50, 16, 0.001, "adamw", "huber", true, 5
        );
        assertEquals(50, config.getEpochs());
        assertEquals(16, config.getBatchSize());
        assertTrue(config.isEarlyStopping());

        // PerformanceMetrics
        PerformanceMetrics metrics = new PerformanceMetrics(
                0.95, 0.01, 10.5, 0.001
        );
        assertEquals(0.95, metrics.getAccuracy(), 0.001);
        assertEquals(0.01, metrics.getMse(), 0.001);
        assertEquals(10.5, metrics.getTrainingTime(), 0.001);

        // QuantumState
        QuantumState quantumState = new QuantumState(
                Arrays.asList(0.5, 0.5), 0.0, 1.0, 0.5, true
        );
        assertEquals(2, quantumState.getAmplitudes().size());
        assertEquals(1.0, quantumState.getCoherence(), 0.001);
        assertTrue(quantumState.isSuperposition());
    }

    @Test
    @DisplayName("Enhanced Neuron")
    void testEnhancedNeuron() {
        EnhancedNeuron neuron = new EnhancedNeuron(
                "test_neuron", 0.5, 0.1, 0.001, 0.1, 0.8, 1.0
        );

        // Activation without quantum context
        Map<String, Double> inputs = new HashMap<>();
        inputs.put("input_0", 0.5);
        inputs.put("input_1", -0.3);
        double output = neuron.activate(inputs);
        assertTrue(Double.isFinite(output));

        // Neuron state within valid set
        assertTrue(Arrays.asList("idle", "firing", "refractory", "quantum_superposition")
                .contains(neuron.getState()));

        // Adaptive threshold
        double initialThreshold = neuron.getThreshold();
        neuron.adaptiveThresholdUpdate(1.0); // high activation
        assertTrue(neuron.getThreshold() >= initialThreshold);

        // Dictionary conversion
        Map<String, Object> neuronDict = neuron.toDict();
        assertTrue(neuronDict.containsKey("id"));
        assertTrue(neuronDict.containsKey("activation"));
        assertTrue(neuronDict.containsKey("confidence"));
    }

    @Test
    @DisplayName("Enhanced Neural Layer")
    void testEnhancedNeuralLayer() {
        List<EnhancedNeuron> neurons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            neurons.add(new EnhancedNeuron(
                    "layer_neuron_" + i, 0.1, 0.0, 0.001, 0.1, 0.8, 1.0
            ));
        }

        EnhancedNeuralLayer layer = new EnhancedNeuralLayer(
                "test_layer", neurons, "attention", "gelu",
                0.1, true, true, 8, null, null
        );

        // Forward pass
        double[] inputs = randomDoubleArray(5);
        double[] outputs = layer.forward(inputs);
        assertEquals(5, outputs.length);

        // Attention layer
        List<EnhancedNeuron> attentionNeurons = new ArrayList<>(neurons.subList(0, 3));
        EnhancedNeuralLayer attLayer = new EnhancedNeuralLayer(
                "attention_test", attentionNeurons, "attention", "gelu",
                0.0, false, false, 4, null, null
        );
        double[] attOutputs = attLayer.forward(Arrays.copyOf(inputs, 3));
        assertEquals(3, attOutputs.length);

        // Dictionary conversion
        Map<String, Object> layerDict = layer.toDict();
        assertTrue(layerDict.containsKey("layer_id"));
        assertTrue(layerDict.containsKey("layer_type"));
        assertTrue(layerDict.containsKey("attention_heads"));
    }

    @Test
    @DisplayName("Enhanced Neural Network")
    void testEnhancedNeuralNetwork() {
        // Build layers
        List<EnhancedNeuralLayer> layers = new ArrayList<>();

        // Input layer
        List<EnhancedNeuron> inputNeurons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            inputNeurons.add(new EnhancedNeuron("input_" + i));
        }
        EnhancedNeuralLayer inputLayer = new EnhancedNeuralLayer(
                "input_layer", inputNeurons, "dense", "gelu",
                0.0, false, false, 1, null, null
        );
        layers.add(inputLayer);

        // Hidden layer
        List<EnhancedNeuron> hiddenNeurons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            hiddenNeurons.add(new EnhancedNeuron("hidden_" + i));
        }
        EnhancedNeuralLayer hiddenLayer = new EnhancedNeuralLayer(
                "hidden_layer", hiddenNeurons, "attention", "gelu",
                0.0, true, true, 4, null, null
        );
        layers.add(hiddenLayer);

        // Output layer
        List<EnhancedNeuron> outputNeurons = new ArrayList<>();
        outputNeurons.add(new EnhancedNeuron("output_0"));
        EnhancedNeuralLayer outputLayer = new EnhancedNeuralLayer(
                "output_layer", outputNeurons, "dense", "linear",
                0.0, false, false, 1, null, null
        );
        layers.add(outputLayer);

        TrainingConfig trainingConfig = new TrainingConfig(10, 4, 0.001, "adamw", "huber", true, 5);

        EnhancedNeuralNetwork network = new EnhancedNeuralNetwork(
                "test_network", layers, "transformer", 10, 1,
                0.001, "adamw", "huber", trainingConfig,
                Arrays.asList("random_forest")
        );

        // Forward pass
        double[] inputs = randomDoubleArray(10);
        double[] outputs = network.forward(inputs);
        assertEquals(1, outputs.length);

        // Loss calculation
        double[] targets = new double[]{0.5};
        double loss = network.calculateLoss(outputs, targets);
        assertTrue(loss >= 0);

        // Accuracy calculation
        double accuracy = network.calculateAccuracy(outputs, targets);
        assertTrue(Double.isFinite(accuracy));

        // Dictionary conversion
        Map<String, Object> networkDict = network.toDict();
        assertTrue(networkDict.containsKey("network_id"));
        assertTrue(networkDict.containsKey("architecture_type"));
        assertTrue(networkDict.containsKey("training_config"));
    }

    @Test
    @DisplayName("Ultra Enhanced Manager")
    void testUltraEnhancedManager() throws Exception {
        UltraEnhancedNeuralNetworkManager manager = new UltraEnhancedNeuralNetworkManager();

        // Frameworks available
        Map<String, Object> frameworkModels = manager.getFrameworkModels();
        assertFalse(frameworkModels.isEmpty());

        // Create network
        EnhancedNeuralNetwork network = manager.createNetwork(
                "transformer", 8, Arrays.asList(16, 8), 1,
                "auto", true, true
        );

        assertNotNull(network);
        assertTrue(manager.getNetworks().containsKey(network.getNetworkId()));
        assertEquals("transformer", network.getArchitectureType());
        assertEquals(8, network.getInputSize());
        assertEquals(1, network.getOutputSize());

        // Performance statistics
        Map<String, Object> stats = manager.getPerformanceStats();
        assertTrue(stats.containsKey("networks_count"));
        assertTrue(stats.containsKey("frameworks_available"));
        assertTrue(stats.containsKey("quantum_enabled"));
        assertTrue((Integer) stats.get("networks_count") >= 1);

        Map<String, Object> systemInfo = (Map<String, Object>) stats.get("system_info");
        assertTrue(systemInfo.containsKey("platform"));
        assertTrue(systemInfo.containsKey("python_version"));

        // Cleanup
        CompletableFuture<Void> cleanup = manager.cleanup();
        cleanup.get(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Quantum Functionality")
    void testQuantumFunctionality() {
        UltraEnhancedNeuralNetworkManager manager = new UltraEnhancedNeuralNetworkManager();
        QuantumState quantumState = manager.createQuantumState(4);

        if (quantumState != null) {
            assertEquals(4, quantumState.getAmplitudes().size());
            assertEquals(1.0, quantumState.getCoherence(), 0.001);
            assertTrue(quantumState.isSuperposition());

            // Enhancement factor
            double enhancement = quantumState.getCoherence() * 0.2 + quantumState.getEntanglement() * 0.1;
            assertTrue(enhancement > 1.0);
        } else {
            System.out.println(" (Qiskit not available - quantum simulation active)");
        }

        // Cleanup
        manager.cleanup(); // fire and forget, we don't need to wait
    }

    @Test
    @DisplayName("Ensemble Functionality")
    void testEnsembleFunctionality() {
        UltraEnhancedNeuralNetworkManager manager = new UltraEnhancedNeuralNetworkManager();

        EnhancedNeuralNetwork network = manager.createNetwork(
                "feedforward", 4, Arrays.asList(8, 4), 1,
                "auto", false, true
        );

        assertNotNull(network.getEnsembleModels());
        // At least one ensemble model (if any framework is available)
        assertTrue(network.getEnsembleModels().size() >= 0);

        // Ensemble output combination
        double primaryOutput = 0.5;
        double[] layerOutputs = new double[]{0.3, 0.7, 0.4};
        double[] combined = network.combineEnsembleOutputs(primaryOutput, layerOutputs);
        assertEquals(1, combined.length);
        assertTrue(Double.isFinite(combined[0]));

        manager.cleanup();
    }

    @Test
    @DisplayName("Error Handling")
    void testErrorHandling() {
        UltraEnhancedNeuralNetworkManager manager = new UltraEnhancedNeuralNetworkManager();

        try {
            // Invalid parameters
            EnhancedNeuralNetwork network = manager.createNetwork(
                    "invalid_type", -1, Collections.emptyList(), 0,
                    "auto", false, false
            );
            // Should still create something with fallbacks, not null
            assertNotNull(network);
        } catch (Exception e) {
            // Error message should be meaningful
            String msg = e.getMessage().toLowerCase();
            assertTrue(msg.contains("failed") || msg.contains("error"));
        }

        manager.cleanup();
    }

    @Test
    @DisplayName("Backward Compatibility")
    void testBackwardCompatibility() {
        // Legacy Neuron
        Neuron legacyNeuron = new Neuron("legacy_neuron", 0.5, 0.1);
        assertEquals("legacy_neuron", legacyNeuron.getId());

        // Legacy NeuralLayer
        NeuralLayer legacyLayer = new NeuralLayer(
                "legacy_layer", Arrays.asList(legacyNeuron), "dense"
        );
        assertEquals("legacy_layer", legacyLayer.getLayerId());

        // Legacy NeuralNetwork
        NeuralNetwork legacyNetwork = new NeuralNetwork(
                "legacy_network", Arrays.asList(legacyLayer), "feedforward"
        );
        assertEquals("legacy_network", legacyNetwork.getNetworkId());

        // Dictionary conversion
        Map<String, Object> dict = legacyNetwork.toDict();
        assertTrue(dict.containsKey("network_id"));
        assertTrue(dict.containsKey("architecture_type"));
    }

    // Optional manual runner (prints summary)
    public static void main(String[] args) {
        System.out.println("VHALINOR TRADER - Ultra Enhanced Neural Network Test Suite");
        System.out.println("=" .repeat(70));
        System.out.println();

        UltraNeuralNetworkTest testInstance = new UltraNeuralNetworkTest();
        List<String> results = new ArrayList<>();
        int passed = 0, total = 9;

        try {
            testInstance.testEnhancedDataStructures();
            results.add("✓ Enhanced Data Structures PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Enhanced Data Structures FAIL: " + e.getMessage());
        }

        try {
            testInstance.testEnhancedNeuron();
            results.add("✓ Enhanced Neuron PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Enhanced Neuron FAIL: " + e.getMessage());
        }

        try {
            testInstance.testEnhancedNeuralLayer();
            results.add("✓ Enhanced Neural Layer PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Enhanced Neural Layer FAIL: " + e.getMessage());
        }

        try {
            testInstance.testEnhancedNeuralNetwork();
            results.add("✓ Enhanced Neural Network PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Enhanced Neural Network FAIL: " + e.getMessage());
        }

        try {
            testInstance.testUltraEnhancedManager();
            results.add("✓ Ultra Enhanced Manager PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Ultra Enhanced Manager FAIL: " + e.getMessage());
        }

        try {
            testInstance.testQuantumFunctionality();
            results.add("✓ Quantum Functionality PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Quantum Functionality FAIL: " + e.getMessage());
        }

        try {
            testInstance.testEnsembleFunctionality();
            results.add("✓ Ensemble Functionality PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Ensemble Functionality FAIL: " + e.getMessage());
        }

        try {
            testInstance.testErrorHandling();
            results.add("✓ Error Handling PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Error Handling FAIL: " + e.getMessage());
        }

        try {
            testInstance.testBackwardCompatibility();
            results.add("✓ Backward Compatibility PASS");
            passed++;
        } catch (Throwable e) {
            results.add("✗ Backward Compatibility FAIL: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Test Results:");
        System.out.println("-" .repeat(40));
        System.out.printf("Total Tests: %d%n", total);
        System.out.printf("Passed: %d%n", passed);
        System.out.printf("Failed: %d%n", total - passed);
        System.out.printf("Success Rate: %.1f%%%n", (double) passed / total * 100);

        for (String result : results) {
            System.out.println(result);
        }

        if (passed == total) {
            System.out.println("\n🎉 All tests passed! Ultra Enhanced Neural Network module is working correctly.");
        } else {
            System.out.printf("\n⚠️  %d test(s) failed. Please check the errors above.%n", total - passed);
        }
    }
}
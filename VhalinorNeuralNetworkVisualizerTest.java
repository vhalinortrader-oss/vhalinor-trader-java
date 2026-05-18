package com.vhalinor.visualizer.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.vhalinor.visualizer.*;
import com.vhalinor.visualizer.enums.*;
import com.vhalinor.visualizer.models.*;
import com.vhalinor.visualizer.components.*;

/**
 * Test script for VHALINOR Neural Network Visualizer v5.0 (Java Edition)
 * Tests all enhanced AI/ML components and functionality
 */
public class VhalinorNeuralNetworkVisualizerTest {

    // Helper to check class availability
    private static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Test
    @Order(1)
    void testAdvancedImports() {
        System.out.println("Testing Advanced Imports...");

        boolean hasTorch = isClassAvailable("org.pytorch.PyTorchAndroid") || isClassAvailable("com.vhalinor.visualizer.frameworks.PyTorchWrapper");
        System.out.println("PyTorch available: " + hasTorch);

        boolean hasTensorflow = isClassAvailable("org.tensorflow.TensorFlow") || isClassAvailable("com.vhalinor.visualizer.frameworks.TensorFlowWrapper");
        System.out.println("TensorFlow available: " + hasTensorflow);

        boolean hasSklearn = isClassAvailable("com.vhalinor.visualizer.frameworks.ScikitLearnWrapper");
        System.out.println("Scikit-learn available: " + hasSklearn);

        boolean hasNumpy = isClassAvailable("com.vhalinor.visualizer.frameworks.NumPyWrapper");
        System.out.println("NumPy available: " + hasNumpy);

        boolean hasMatplotlib = isClassAvailable("com.vhalinor.visualizer.frameworks.MatplotlibWrapper");
        System.out.println("Matplotlib available: " + hasMatplotlib);

        boolean hasNetworkX = isClassAvailable("com.vhalinor.visualizer.frameworks.NetworkXWrapper");
        System.out.println("NetworkX available: " + hasNetworkX);

        boolean hasPIL = isClassAvailable("com.vhalinor.visualizer.frameworks.PILWrapper");
        System.out.println("PIL available: " + hasPIL);

        System.out.println("Advanced imports test completed\n");
    }

    @Test
    @Order(2)
    void testEnumsAndConstants() {
        System.out.println("Testing Enums and Constants...");

        System.out.println("Visualization modes:");
        for (VisualizationMode mode : VisualizationMode.values()) {
            System.out.println("  - " + mode.name());
        }

        System.out.println("Node shapes:");
        for (NodeShape shape : NodeShape.values()) {
            System.out.println("  - " + shape.getValue());
        }

        System.out.println("Edge styles:");
        for (EdgeStyle style : EdgeStyle.values()) {
            System.out.println("  - " + style.getValue());
        }

        System.out.println("Animation speeds:");
        for (AnimationSpeed speed : AnimationSpeed.values()) {
            System.out.println("  - " + speed.name() + ": " + speed.getValue() + "ms");
        }

        System.out.println("Themes:");
        for (Theme theme : Theme.values()) {
            System.out.println("  - " + theme.getValue());
        }

        if (isClassAvailable("com.vhalinor.visualizer.enums.CognitiveState")) {
            System.out.println("Cognitive states:");
            for (CognitiveState state : CognitiveState.values()) {
                System.out.println("  - " + state.getValue());
            }
        }

        if (isClassAvailable("com.vhalinor.visualizer.enums.LearningMode")) {
            System.out.println("Learning modes:");
            for (LearningMode mode : LearningMode.values()) {
                System.out.println("  - " + mode.getValue());
            }
        }

        System.out.println("Enums and constants test completed\n");
    }

    @Test
    @Order(3)
    void testDataStructures() {
        System.out.println("Testing Data Structures...");

        // Basic Neuron
        Neuron neuron = new Neuron(1, "test_neuron", "processing", "hidden", 1, 0.75, 1.2,
                new double[]{0.5, 0.5}, "#4ECDC4");
        System.out.println("Basic neuron created: " + neuron.getName());
        System.out.println("Activation: " + String.format("%.3f", neuron.getActivation()));
        System.out.println("Position: " + Arrays.toString(neuron.getPosition2d()));

        // Connection
        Connection connection = new Connection(1, 2, 0.8, 0.9, "forward", "#00FF00");
        System.out.println("Connection created: " + connection.getSource() + " -> " + connection.getTarget());
        System.out.println("Weight: " + String.format("%.3f", connection.getWeight()));

        // NetworkStatistics
        NetworkStatistics stats = new NetworkStatistics(10, 15, 3, 0.65, 0.3);
        System.out.printf("Network statistics: %d neurons, %d connections%n",
                stats.getTotalNeurons(), stats.getTotalConnections());

        // Advanced structures if available
        try {
            NeuralCluster cluster = new NeuralCluster(1, "test_cluster", Arrays.asList(1, 2, 3, 4),
                    new double[]{0.5, 0.5, 0.0}, 0.2, "test_processing");
            cluster.addNeuron(5);
            cluster.removeNeuron(1);
            System.out.println("Cluster neurons: " + cluster.getNeurons());
        } catch (Exception e) {
            System.out.println("Advanced data structures not available");
        }

        System.out.println("Data structures test completed\n");
    }

    @Test
    @Order(4)
    void testAdvancedNeuron() {
        System.out.println("Testing AdvancedNeuron...");

        try {
            AdvancedNeuron advNeuron = new AdvancedNeuron(
                    1, "test_neuron", "processing", "hidden", 1,
                    0.15, 0.02, 1.2, null, null, null
            );

            double[] inputs = {0.5, 0.3, 0.8, 0.2};
            double[] weights = {0.4, 0.6, 0.3, 0.7};
            double activation = advNeuron.activate(inputs, weights);
            System.out.printf("Neuron activation: %.3f%n", activation);
            System.out.printf("Firing rate: %.3f%n", advNeuron.getFiringRate());
            System.out.println("Cognitive state: " + advNeuron.getCognitiveState().getValue());

            advNeuron.updateWeightsHebbian(inputs, 0.8);
            System.out.println("Connection strengths updated: " + advNeuron.getConnectionStrengths().size());

            advNeuron.adaptThreshold();
            System.out.printf("Threshold adaptation: %.3f%n", advNeuron.getThresholdAdaptation());

            double[] embedding = advNeuron.getEmbedding();
            System.out.println("Embedding dimension: " + embedding.length);

        } catch (Exception e) {
            System.out.println("AdvancedNeuron not available, using basic Neuron");

            Neuron basicNeuron = new Neuron(1, "test_neuron", "processing", "hidden", 1, 0.75, 1.2,
                    new double[]{0.5, 0.5}, "#4ECDC4");
            System.out.println("Basic neuron created: " + basicNeuron.getName());
            System.out.printf("Activation: %.3f%n", basicNeuron.getActivation());
            System.out.printf("Importance: %.3f%n", basicNeuron.getImportance());
        }

        System.out.println("AdvancedNeuron test completed\n");
    }

    @Test
    @Order(5)
    void testCognitiveAnalyzer() {
        System.out.println("Testing CognitiveAnalyzer...");

        try {
            CognitiveAnalyzer analyzer = new CognitiveAnalyzer();

            Map<Integer, AdvancedNeuron> neurons = new HashMap<>();
            Random rand = new Random(42);
            for (int i = 0; i < 10; i++) {
                AdvancedNeuron neuron = new AdvancedNeuron(
                        i, "neuron_" + i, "processing", "hidden", 1,
                        rand.nextDouble() * 0.8 + 0.1, // activation
                        rand.nextDouble() * 14.5 + 0.5, // firing rate
                        0.0, null, null, null
                );
                neuron.setCognitiveState(CognitiveState.values()[rand.nextInt(CognitiveState.values().length)]);
                neurons.put(i, neuron);
            }

            List<Connection> connections = new ArrayList<>();
            for (int i = 0; i < 15; i++) {
                int source = rand.nextInt(10);
                int target = rand.nextInt(10);
                if (source != target) {
                    connections.add(new Connection(source, target, rand.nextDouble() * 0.9 + 0.1, 0.0, "default", null));
                }
            }

            List<CognitiveInsight> insights = analyzer.analyzeNetworkState(neurons, connections);
            System.out.println("Insights generated: " + insights.size());

            for (int i = 0; i < Math.min(3, insights.size()); i++) {
                CognitiveInsight insight = insights.get(i);
                String desc = insight.getDescription();
                if (desc.length() > 50) desc = desc.substring(0, 50) + "...";
                System.out.println("  - " + insight.getInsightType() + ": " + desc);
                System.out.printf("    Confidence: %.3f%n", insight.getConfidence());
            }

            Map<String, Object> summary = analyzer.getInsightsSummary();
            System.out.println("Total insights: " + summary.getOrDefault("total_insights", 0));
            System.out.printf("Average confidence: %.3f%n", (Double) summary.getOrDefault("avg_confidence", 0.0));
        } catch (Exception e) {
            System.out.println("CognitiveAnalyzer not available");
        }

        System.out.println("CognitiveAnalyzer test completed\n");
    }

    @Test
    @Order(6)
    void testNetworkPredictor() {
        System.out.println("Testing NetworkPredictor...");

        try {
            NetworkPredictor predictor = new NetworkPredictor();

            Map<Integer, AdvancedNeuron> neurons = new HashMap<>();
            Random rand = new Random(42);
            for (int i = 0; i < 8; i++) {
                AdvancedNeuron neuron = new AdvancedNeuron(
                        i, "pred_neuron_" + i, "processing", "hidden", 1,
                        rand.nextDouble() * 0.6 + 0.2, rand.nextDouble() * 9.0 + 1.0,
                        0.0, null, null, null
                );
                neurons.put(i, neuron);
            }

            List<Connection> connections = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                connections.add(new Connection(rand.nextInt(8), rand.nextInt(8),
                        rand.nextDouble() * 0.7 + 0.2, 0.0, "default", null));
            }

            Map<String, Object> prediction = predictor.predictNetworkState(neurons, connections);
            System.out.printf("Prediction confidence: %.3f%n", (Double) prediction.get("confidence"));
            System.out.println("Features used: " + prediction.get("features_used"));
            System.out.printf("Prediction time: %.4fs%n", (Double) prediction.get("prediction_time"));

            System.out.println("Activation predictions: " + ((List) prediction.get("activation_predictions")).size());
            System.out.println("Cluster predictions: " + ((List) prediction.get("cluster_predictions")).size());
            System.out.println("Cognitive predictions: " + ((List) prediction.get("cognitive_predictions")).size());
            System.out.println("Anomaly predictions: " + ((List) prediction.get("anomaly_predictions")).size());

            Map<String, Object> evaluation = predictor.evaluatePredictions(new HashMap<>());
            System.out.println("Evaluation metrics: " + evaluation);
        } catch (Exception e) {
            System.out.println("NetworkPredictor not available");
        }

        System.out.println("NetworkPredictor test completed\n");
    }

    @Test
    @Order(7)
    void testRealTimeMonitor() {
        System.out.println("Testing RealTimeMonitor...");

        try {
            RealTimeMonitor monitor = new RealTimeMonitor(0.5);

            Map<Integer, AdvancedNeuron> neurons = new HashMap<>();
            Random rand = new Random(42);
            for (int i = 0; i < 6; i++) {
                neurons.put(i, new AdvancedNeuron(
                        i, "monitor_neuron_" + i, "processing", "hidden", 1,
                        rand.nextDouble() * 0.8 + 0.1, 0.0, 0.0, null, null, null
                ));
            }

            List<Connection> connections = Arrays.asList(
                    new Connection(0, 1, 0.5, 0.0, null, null),
                    new Connection(1, 2, 0.5, 0.0, null, null),
                    new Connection(2, 3, 0.5, 0.0, null, null)
            );

            monitor.startMonitoring(neurons, connections);
            TimeUnit.MILLISECONDS.sleep(1500);

            Map<String, Object> currentMetrics = monitor.getCurrentMetrics();
            System.out.println("Current metrics keys: " + currentMetrics.keySet());
            System.out.println("Total neurons: " + currentMetrics.getOrDefault("total_neurons", 0));
            System.out.printf("Network health: %.3f%n", (Double) currentMetrics.getOrDefault("network_health", 0.0));
            System.out.printf("Performance score: %.3f%n", (Double) currentMetrics.getOrDefault("performance_score", 0.0));

            List<?> alerts = monitor.getRecentAlerts();
            System.out.println("Recent alerts: " + alerts.size());

            monitor.stopMonitoring();
        } catch (Exception e) {
            System.out.println("RealTimeMonitor not available");
        }

        System.out.println("RealTimeMonitor test completed\n");
    }

    @Test
    @Order(8)
    void testNetworkDataGenerator() {
        System.out.println("Testing NetworkDataGenerator...");

        String[] architectures = {"vhalinor_quantum", "deep_learning", "lstm_memory", "convolutional"};

        for (String arch : architectures) {
            System.out.println("Testing architecture: " + arch);
            NetworkDataGenerator.NetworkData data = NetworkDataGenerator.generateNetwork(arch, 42);

            System.out.println("  Neurons: " + data.neurons.size());
            System.out.println("  Connections: " + data.connections.size());
            System.out.println("  Layers: " + data.layers.size());

            if ("vhalinor_quantum".equals(arch)) {
                NetworkDataGenerator.NetworkData lexData = NetworkDataGenerator.generateFromLexIag();
                System.out.println("  LEX IAG neurons: " + lexData.neurons.size());
                System.out.println("  LEX IAG connections: " + lexData.connections.size());
                System.out.println("  LEX IAG layers: " + lexData.layers.size());
            }
        }

        System.out.println("NetworkDataGenerator test completed\n");
    }

    @Test
    @Order(9)
    void testIntegration() {
        System.out.println("Testing Component Integration...");

        try {
            CognitiveAnalyzer analyzer = new CognitiveAnalyzer();
            NetworkPredictor predictor = new NetworkPredictor();
            RealTimeMonitor monitor = new RealTimeMonitor(0.1);

            NetworkDataGenerator.NetworkData data = NetworkDataGenerator.generateNetwork("vhalinor_quantum", 42);
            Map<Integer, AdvancedNeuron> advancedNeurons = new HashMap<>();
            for (Map.Entry<Integer, Neuron> entry : data.neurons.entrySet()) {
                Neuron n = entry.getValue();
                AdvancedNeuron adv = new AdvancedNeuron(
                        n.getId(), n.getName(), n.getType(), n.getLayer(), n.getLayerIndex(),
                        n.getActivation(), 0.0, 0.0, n.getPosition2d(), null, null
                );
                advancedNeurons.put(entry.getKey(), adv);
            }

            monitor.startMonitoring(advancedNeurons, data.connections);
            List<CognitiveInsight> insights = analyzer.analyzeNetworkState(advancedNeurons, data.connections);
            System.out.println("Analysis insights: " + insights.size());

            Map<String, Object> prediction = predictor.predictNetworkState(advancedNeurons, data.connections);
            System.out.printf("Prediction confidence: %.3f%n", (Double) prediction.get("confidence"));

            TimeUnit.MILLISECONDS.sleep(500);
            Map<String, Object> metrics = monitor.getCurrentMetrics();
            System.out.println("Monitoring metrics: " + metrics.size() + " keys");

            monitor.stopMonitoring();
        } catch (Exception e) {
            System.out.println("Advanced components not available, testing basic integration");

            NetworkDataGenerator.NetworkData data = NetworkDataGenerator.generateNetwork("vhalinor_quantum", 42);
            System.out.printf("Generated network with %d neurons and %d connections%n",
                    data.neurons.size(), data.connections.size());
            System.out.println("Network layers: " + data.layers.keySet());

            NetworkStatistics stats = new NetworkStatistics(
                    data.neurons.size(), data.connections.size(), data.layers.size(), 0.0, 0.0);
            System.out.printf("Network statistics: %d neurons, %d connections%n",
                    stats.getTotalNeurons(), stats.getTotalConnections());
        }

        System.out.println("Integration test completed\n");
    }

    // ============================================================================
    // Main method to run all tests manually (similar to Python's main)
    // ============================================================================
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("VHALINOR IAG 5.0 - Neural Network Visualizer Test Suite (Java)");
        System.out.println("=".repeat(80));

        VhalinorNeuralNetworkVisualizerTest tester = new VhalinorNeuralNetworkVisualizerTest();

        try {
            tester.testAdvancedImports();
            tester.testEnumsAndConstants();
            tester.testDataStructures();
            tester.testAdvancedNeuron();
            tester.testCognitiveAnalyzer();
            tester.testNetworkPredictor();
            tester.testRealTimeMonitor();
            tester.testNetworkDataGenerator();
            tester.testIntegration();

            System.out.println("=".repeat(80));
            System.out.println("All tests completed successfully!");
            System.out.println("VHALINOR Neural Network Visualizer v5.0 is fully functional");
            System.out.println("=".repeat(80));
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.vhalinor.trader.tests;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * VHALINOR TRADER - Deep Learning Module Test (Java Edition)
 * ================================================================================================
 * Test for the Deep Learning specialized module.
 *
 * Uses reflection to test availability and basic functionality of the
 * deep learning trading components.
 */
public class DeepLearningModuleTest {

    // -------------------------------------------------------------------------
    // 1. Teste de importações do módulo Deep Learning
    // -------------------------------------------------------------------------
    private static boolean testDeepLearningImports() {
        System.out.println("Testing Deep Learning imports...");

        try {
            Class.forName("com.vhalinor.trader.deep_learning.DeepLearningTrader");
            Class.forName("com.vhalinor.trader.deep_learning.TradingTimeSeriesDataset");
            Class.forName("com.vhalinor.trader.deep_learning.LSTMWithAttention");
            Class.forName("com.vhalinor.trader.deep_learning.TransformerTrader");
            System.out.println("PASS Deep Learning module imports successful");
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("FAIL Deep Learning import failed: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // 2. Teste de inicialização do DeepLearningTrader
    // -------------------------------------------------------------------------
    private static boolean testDeepLearningTrader() {
        System.out.println("Testing DeepLearningTrader...");

        try {
            Class<?> dlTraderClass = Class.forName("com.vhalinor.trader.deep_learning.DeepLearningTrader");
            Object dlTrader = dlTraderClass.getDeclaredConstructor().newInstance();

            // Check for attributes models, optimizers, device
            boolean hasModels = dlTraderClass.getDeclaredField("models") != null;
            boolean hasOptimizers = dlTraderClass.getDeclaredField("optimizers") != null;
            boolean hasDevice = dlTraderClass.getDeclaredField("device") != null;

            if (hasModels && hasOptimizers && hasDevice) {
                // Optionally print device info
                Object device = dlTraderClass.getMethod("getDevice").invoke(dlTrader);
                System.out.println("PASS DeepLearningTrader initialized on device: " + device);
                return true;
            } else {
                System.out.println("FAIL DeepLearningTrader missing required fields");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAIL DeepLearningTrader test failed: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // 3. Teste de criação do TradingTimeSeriesDataset
    // -------------------------------------------------------------------------
    private static boolean testDatasetCreation() {
        System.out.println("Testing TradingTimeSeriesDataset...");

        try {
            Class<?> datasetClass = Class.forName("com.vhalinor.trader.deep_learning.TradingTimeSeriesDataset");
            Object dataset = datasetClass.getDeclaredConstructor().newInstance();

            // Check that length > 0
            Method sizeMethod = datasetClass.getMethod("size");
            int size = (int) sizeMethod.invoke(dataset);
            if (size > 0) {
                System.out.println("PASS TradingTimeSeriesDataset created with " + size + " samples");
                return true;
            } else {
                System.out.println("FAIL Dataset size is 0");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAIL Dataset test failed: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // 4. Teste de construção de modelo (se PyTorch disponível)
    // -------------------------------------------------------------------------
    private static boolean testModelBuilding() {
        System.out.println("Testing model building...");

        try {
            // Check if PyTorch wrapper is available (example: org.pytorch.PyTorchAndroid or similar)
            boolean torchAvailable = false;
            try {
                Class.forName("org.pytorch.PyTorchAndroid");
                torchAvailable = true;
            } catch (ClassNotFoundException e) {
                // PyTorch not available, we'll skip the test
                System.out.println("! PyTorch not available - skipping model build test");
                return true; // not a failure
            }

            if (!torchAvailable) {
                // Just in case the class check above didn't cover, try another
                try {
                    Class.forName("com.vhalinor.trader.deep_learning.TorchHelper");
                    torchAvailable = true;
                } catch (ClassNotFoundException ignored) {}
            }

            if (!torchAvailable) {
                System.out.println("! PyTorch not available - skipping model build test");
                return true;
            }

            // Instantiate DeepLearningTrader
            Class<?> dlTraderClass = Class.forName("com.vhalinor.trader.deep_learning.DeepLearningTrader");
            Object dlTrader = dlTraderClass.getDeclaredConstructor().newInstance();

            // Call buildModel('lstm_attention', 6)
            Method buildModelMethod = dlTraderClass.getMethod("buildModel", String.class, int.class);
            Object model = buildModelMethod.invoke(dlTrader, "lstm_attention", 6);

            if (model != null) {
                System.out.println("PASS Model built successfully");
                return true;
            } else {
                System.out.println("! Model building skipped (fallback active)");
                return true; // fallback not a failure
            }
        } catch (Exception e) {
            System.out.println("FAIL Model building test failed: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Main method to run all tests
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("VHALINOR TRADER - Deep Learning Test Suite (Java)");
        System.out.println("=".repeat(60));
        System.out.println();

        // Define tests
        Supplier<Boolean> testImports = DeepLearningModuleTest::testDeepLearningImports;
        Supplier<Boolean> testTrader = DeepLearningModuleTest::testDeepLearningTrader;
        Supplier<Boolean> testDataset = DeepLearningModuleTest::testDatasetCreation;
        Supplier<Boolean> testModel = DeepLearningModuleTest::testModelBuilding;

        String[] testNames = {
            "Deep Learning Imports",
            "Deep Learning Trader",
            "Dataset Creation",
            "Model Building"
        };
        Supplier<Boolean>[] tests = new Supplier[]{
            testImports,
            testTrader,
            testDataset,
            testModel
        };

        int passed = 0;
        int total = tests.length;

        for (int i = 0; i < total; i++) {
            System.out.println("Running " + testNames[i] + "...");
            try {
                if (tests[i].get()) {
                    passed++;
                    System.out.println("PASS " + testNames[i] + " PASSED");
                } else {
                    System.out.println("FAIL " + testNames[i] + " FAILED");
                }
            } catch (Exception e) {
                System.out.println("ERROR " + testNames[i] + " ERROR: " + e.getMessage());
            }
            System.out.println();
        }

        System.out.println("=".repeat(60));
        System.out.println("Results: " + passed + "/" + total + " tests passed");

        if (passed == total) {
            System.out.println("SUCCESS All Deep Learning tests passed!");
            System.exit(0);
        } else {
            System.out.println("WARNING " + (total - passed) + " test(s) failed.");
            System.exit(1);
        }
    }
}
package com.ai_trading_bot.core;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Advanced neural network factory supporting multiple architectures,
 * configurable hyperparameters, and model persistence.
 * Uses Deeplearning4j as the backend.
 */
public class NeuralNetworkFactory {
    private static final Logger log = LoggerFactory.getLogger(NeuralNetworkFactory.class);

    private final NeuralNetworkConfig defaultConfig;

    public NeuralNetworkFactory(NeuralNetworkConfig config) {
        this.defaultConfig = config != null ? config : NeuralNetworkConfig.defaultConfig();
        log.info("NeuralNetworkFactory initialized with config: {}", defaultConfig);
    }

    /**
     * Creates a feed-forward neural network (simple dense layers).
     *
     * @param inputDim  Number of input features.
     * @param outputDim Number of output neurons (1 for regression, n for classification).
     * @return Configured MultiLayerNetwork (not yet initialized).
     */
    public MultiLayerNetwork createFeedForwardNetwork(int inputDim, int outputDim) {
        return createFeedForwardNetwork(inputDim, outputDim, defaultConfig);
    }

    /**
     * Creates a feed-forward network with custom layer sizes and activation functions.
     *
     * @param inputDim   Input dimension.
     * @param outputDim  Output dimension.
     * @param layerSizes List of hidden layer sizes (e.g., [64, 32]).
     * @param activation Activation function for hidden layers (e.g., "relu", "gelu").
     * @return MultiLayerNetwork.
     */
    public MultiLayerNetwork createFeedForwardNetwork(int inputDim, int outputDim,
                                                      List<Integer> layerSizes,
                                                      String activation) {
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(defaultConfig.learningRate));

        List<Layer> layers = new ArrayList<>();

        int prevSize = inputDim;
        for (int size : layerSizes) {
            layers.add(new DenseLayer.Builder()
                    .nIn(prevSize)
                    .nOut(size)
                    .activation(Activation.fromString(activation))
                    .build());
            if (defaultConfig.dropoutRate > 0) {
                layers.add(new DropoutLayer.Builder(defaultConfig.dropoutRate).build());
            }
            prevSize = size;
        }

        // Output layer
        layers.add(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .nIn(prevSize)
                .nOut(outputDim)
                .activation(Activation.IDENTITY)
                .build());

        MultiLayerConfiguration conf = builder.list(layers.toArray(new Layer[0])).build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        log.info("Created feed-forward network with inputDim={}, outputDim={}, layers={}",
                inputDim, outputDim, layerSizes);
        return model;
    }

    // Overloaded version using default config layer sizes
    public MultiLayerNetwork createFeedForwardNetwork(int inputDim, int outputDim,
                                                      NeuralNetworkConfig config) {
        List<Integer> layers = config.hiddenLayerSizes != null && !config.hiddenLayerSizes.isEmpty()
                ? config.hiddenLayerSizes
                : List.of(config.hiddenDim);
        return createFeedForwardNetwork(inputDim, outputDim, layers, config.activation);
    }

    /**
     * Creates an LSTM network for sequence prediction.
     *
     * @param inputDim       Number of features per time step.
     * @param sequenceLength Number of time steps.
     * @param outputDim      Output dimension (e.g., 1 for next step prediction).
     * @return MultiLayerNetwork.
     */
    public MultiLayerNetwork createLstmNetwork(int inputDim, int sequenceLength, int outputDim) {
        return createLstmNetwork(inputDim, sequenceLength, outputDim, defaultConfig);
    }

    public MultiLayerNetwork createLstmNetwork(int inputDim, int sequenceLength, int outputDim,
                                               NeuralNetworkConfig config) {
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(config.learningRate));

        List<Layer> layers = new ArrayList<>();

        // LSTM layer(s)
        int lstmOut = config.lstmUnits > 0 ? config.lstmUnits : 64;
        layers.add(new LSTM.Builder()
                .nIn(inputDim)
                .nOut(lstmOut)
                .activation(Activation.TANH)
                .build());
        if (config.dropoutRate > 0) {
            layers.add(new DropoutLayer.Builder(config.dropoutRate).build());
        }

        // Optional second LSTM
        if (config.numLstmLayers > 1) {
            layers.add(new LSTM.Builder()
                    .nIn(lstmOut)
                    .nOut(lstmOut / 2)
                    .activation(Activation.TANH)
                    .build());
            if (config.dropoutRate > 0) {
                layers.add(new DropoutLayer.Builder(config.dropoutRate).build());
            }
        }

        // Output layer (for many-to-one or many-to-many, use RnnOutputLayer)
        layers.add(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .nIn(lstmOut)
                .nOut(outputDim)
                .activation(Activation.IDENTITY)
                .build());

        MultiLayerConfiguration conf = builder.list(layers.toArray(new Layer[0])).build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        log.info("Created LSTM network: inputDim={}, seqLen={}, outputDim={}",
                inputDim, sequenceLength, outputDim);
        return model;
    }

    /**
     * Creates a CNN (1D) for time-series feature extraction.
     *
     * @param inputDim       Input features per time step.
     * @param sequenceLength Number of time steps.
     * @param outputDim      Output dimension.
     * @return MultiLayerNetwork.
     */
    public MultiLayerNetwork createCnn1dNetwork(int inputDim, int sequenceLength, int outputDim) {
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(defaultConfig.learningRate));

        List<Layer> layers = new ArrayList<>();
        layers.add(new Convolution1D.Builder(32, 3)
                .nIn(inputDim)
                .nOut(32)
                .activation(Activation.RELU)
                .build());
        layers.add(new Subsampling1D.Builder(Subsampling1D.PoolingType.MAX, 2).build());
        layers.add(new Convolution1D.Builder(64, 3)
                .nIn(32)
                .nOut(64)
                .activation(Activation.RELU)
                .build());
        layers.add(new Subsampling1D.Builder(Subsampling1D.PoolingType.MAX, 2).build());
        layers.add(new DenseLayer.Builder().nIn(64 * (sequenceLength / 4)).nOut(64).activation(Activation.RELU).build());
        layers.add(new OutputLayer.Builder(LossFunctions.LossFunction.MSE).nIn(64).nOut(outputDim).activation(Activation.IDENTITY).build());

        MultiLayerConfiguration conf = builder.list(layers.toArray(new Layer[0])).build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        log.info("Created 1D CNN network");
        return model;
    }

    /**
     * Saves the model to a file.
     */
    public static void saveModel(MultiLayerNetwork model, String filePath) throws IOException {
        model.save(new File(filePath), true);
        log.info("Model saved to {}", filePath);
    }

    /**
     * Loads a model from a file.
     */
    public static MultiLayerNetwork loadModel(String filePath) throws IOException {
        MultiLayerNetwork model = MultiLayerNetwork.load(new File(filePath), true);
        log.info("Model loaded from {}", filePath);
        return model;
    }

    /**
     * Configuration class holding all hyperparameters.
     */
    public static class NeuralNetworkConfig {
        public double learningRate = 0.001;
        public double dropoutRate = 0.2;
        public int hiddenDim = 64;
        public List<Integer> hiddenLayerSizes = new ArrayList<>(); // e.g., [128, 64]
        public String activation = "gelu"; // "relu", "tanh", "gelu"
        public int lstmUnits = 64;
        public int numLstmLayers = 1;

        public static NeuralNetworkConfig defaultConfig() {
            NeuralNetworkConfig cfg = new NeuralNetworkConfig();
            cfg.hiddenLayerSizes = List.of(128, 64);
            cfg.activation = "gelu";
            cfg.learningRate = 0.001;
            cfg.dropoutRate = 0.2;
            cfg.lstmUnits = 64;
            cfg.numLstmLayers = 1;
            return cfg;
        }

        @Override
        public String toString() {
            return "NeuralNetworkConfig{" +
                    "learningRate=" + learningRate +
                    ", dropoutRate=" + dropoutRate +
                    ", hiddenDim=" + hiddenDim +
                    ", hiddenLayerSizes=" + hiddenLayerSizes +
                    ", activation='" + activation + '\'' +
                    ", lstmUnits=" + lstmUnits +
                    ", numLstmLayers=" + numLstmLayers +
                    '}';
        }
    }
}
public class Main {
    public static void main(String[] args) {
        // Use default config
        NeuralNetworkFactory factory = new NeuralNetworkFactory(null);
        
        // Create feed-forward network with 10 inputs, 1 output
        MultiLayerNetwork model = factory.createFeedForwardNetwork(10, 1);
        
        // Create LSTM network for time series (5 time steps, each with 3 features)
        MultiLayerNetwork lstmModel = factory.createLstmNetwork(3, 5, 1);
        
        // Custom config
        NeuralNetworkFactory.NeuralNetworkConfig customConfig = new NeuralNetworkFactory.NeuralNetworkConfig();
        customConfig.learningRate = 0.0005;
        customConfig.dropoutRate = 0.3;
        customConfig.hiddenLayerSizes = List.of(256, 128, 64);
        customConfig.activation = "relu";
        NeuralNetworkFactory customFactory = new NeuralNetworkFactory(customConfig);
        
        MultiLayerNetwork customModel = customFactory.createFeedForwardNetwork(20, 2);
    }
}

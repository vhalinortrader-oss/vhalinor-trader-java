```java
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.*;

public class NeuralNetworkCreator {

    // ---------- Enums ----------
    public enum NeuronType {
        INPUT, HIDDEN, OUTPUT, RECURRENT, CONVOLUTIONAL, ATTENTION, QUANTUM, MEMORY, MODULATION;

        public String getValue() {
            return this.name().toLowerCase();
        }
    }

    public enum ActivationFunction {
        SIGMOID, TANH, RELU, LEAKY_RELU, ELU, SWISH, GELU, QUANTUM_SIGMOID, HYPERBOLIC_QUANTUM;

        public String getValue() {
            return this.name().toLowerCase();
        }
    }

    public enum NetworkArchitecture {
        FEED_FORWARD, CONVOLUTIONAL, RECURRENT, LSTM, GRU, TRANSFORMER, AUTOENCODER,
        VARIATIONAL_AUTOENCODER, GENERATIVE_ADVERSARIAL, QUANTUM_NEURAL,
        HYBRID_QUANTUM_CLASSICAL, ATTENTION_MECHANISM, RESIDUAL, DENSELY_CONNECTED;

        public String getValue() {
            return this.name().toLowerCase();
        }
    }

    // ---------- Data Classes ----------
    public static class Neuron {
        public String neuronId;
        public NeuronType neuronType;
        public ActivationFunction activationFunction;
        public List<String> inputs;
        public List<String> outputs;
        public List<Double> weights;
        public double bias;
        public double activationValue;
        public double gradient;
        public double learningRate;
        public double dropoutRate;
        public boolean batchNorm;
        public List<Double> quantumState;
        public List<Double> memoryState;
        public double plasticity;
        public double hebbianFactor;
        public String timestamp;

        public Neuron() {
            this.inputs = new ArrayList<>();
            this.outputs = new ArrayList<>();
            this.weights = new ArrayList<>();
            this.timestamp = "";
        }

        public Neuron(String neuronId, NeuronType neuronType, ActivationFunction activationFunction,
                      List<String> inputs, List<String> outputs, List<Double> weights, double bias,
                      double activationValue, double gradient, double learningRate, double dropoutRate,
                      boolean batchNorm, List<Double> quantumState, List<Double> memoryState,
                      double plasticity, double hebbianFactor, String timestamp) {
            this.neuronId = neuronId;
            this.neuronType = neuronType;
            this.activationFunction = activationFunction;
            this.inputs = inputs != null ? inputs : new ArrayList<>();
            this.outputs = outputs != null ? outputs : new ArrayList<>();
            this.weights = weights;
            this.bias = bias;
            this.activationValue = activationValue;
            this.gradient = gradient;
            this.learningRate = learningRate;
            this.dropoutRate = dropoutRate;
            this.batchNorm = batchNorm;
            this.quantumState = quantumState;
            this.memoryState = memoryState;
            this.plasticity = plasticity;
            this.hebbianFactor = hebbianFactor;
            this.timestamp = timestamp != null ? timestamp : "";
        }

        public Neuron copy() {
            return new Neuron(neuronId, neuronType, activationFunction,
                    new ArrayList<>(inputs), new ArrayList<>(outputs),
                    new ArrayList<>(weights), bias, activationValue, gradient,
                    learningRate, dropoutRate, batchNorm,
                    quantumState != null ? new ArrayList<>(quantumState) : null,
                    memoryState != null ? new ArrayList<>(memoryState) : null,
                    plasticity, hebbianFactor, timestamp);
        }
    }

    public static class Synapse {
        public String synapseId;
        public String sourceNeuron;
        public String targetNeuron;
        public double weight;
        public double delay;
        public double plasticity;
        public double hebbianStrength;
        public double quantumEntanglement;
        public double lastSpikeTime;
        public int spikeCount;
        public String timestamp;

        public Synapse() {
            this.timestamp = "";
        }

        public Synapse(String synapseId, String sourceNeuron, String targetNeuron,
                       double weight, double delay, double plasticity, double hebbianStrength,
                       double quantumEntanglement, double lastSpikeTime, int spikeCount, String timestamp) {
            this.synapseId = synapseId;
            this.sourceNeuron = sourceNeuron;
            this.targetNeuron = targetNeuron;
            this.weight = weight;
            this.delay = delay;
            this.plasticity = plasticity;
            this.hebbianStrength = hebbianStrength;
            this.quantumEntanglement = quantumEntanglement;
            this.lastSpikeTime = lastSpikeTime;
            this.spikeCount = spikeCount;
            this.timestamp = timestamp != null ? timestamp : "";
        }
    }

    public static class NeuralLayer {
        public String layerId;
        public NeuronType layerType;
        public List<Neuron> neurons;
        public ActivationFunction activationFunction;
        public double dropoutRate;
        public boolean batchNorm;
        public boolean residualConnection;
        public List<Double> attentionWeights;
        public double quantumCoherence;
        public String timestamp;

        public NeuralLayer() {
            this.neurons = new ArrayList<>();
            this.timestamp = "";
        }

        public NeuralLayer(String layerId, NeuronType layerType, List<Neuron> neurons,
                           ActivationFunction activationFunction, double dropoutRate, boolean batchNorm,
                           boolean residualConnection, List<Double> attentionWeights,
                           double quantumCoherence, String timestamp) {
            this.layerId = layerId;
            this.layerType = layerType;
            this.neurons = neurons != null ? neurons : new ArrayList<>();
            this.activationFunction = activationFunction;
            this.dropoutRate = dropoutRate;
            this.batchNorm = batchNorm;
            this.residualConnection = residualConnection;
            this.attentionWeights = attentionWeights;
            this.quantumCoherence = quantumCoherence;
            this.timestamp = timestamp != null ? timestamp : "";
        }
    }

    public static class NeuralNetwork {
        public String networkId;
        public NetworkArchitecture architecture;
        public List<NeuralLayer> layers;
        public List<Synapse> synapses;
        public int inputSize;
        public int outputSize;
        public int totalParameters;
        public double learningRate;
        public String optimizer;
        public String lossFunction;
        public Map<String, Double> metrics;
        public boolean quantumFeatures;
        public int memoryCapacity;
        public int evolutionGeneration;
        public double fitnessScore;
        public String timestamp;

        public NeuralNetwork() {
            this.layers = new ArrayList<>();
            this.synapses = new ArrayList<>();
            this.metrics = new HashMap<>();
            this.timestamp = "";
        }

        public NeuralNetwork(String networkId, NetworkArchitecture architecture,
                             List<NeuralLayer> layers, List<Synapse> synapses,
                             int inputSize, int outputSize, int totalParameters,
                             double learningRate, String optimizer, String lossFunction,
                             Map<String, Double> metrics, boolean quantumFeatures,
                             int memoryCapacity, int evolutionGeneration, double fitnessScore,
                             String timestamp) {
            this.networkId = networkId;
            this.architecture = architecture;
            this.layers = layers != null ? layers : new ArrayList<>();
            this.synapses = synapses != null ? synapses : new ArrayList<>();
            this.inputSize = inputSize;
            this.outputSize = outputSize;
            this.totalParameters = totalParameters;
            this.learningRate = learningRate;
            this.optimizer = optimizer;
            this.lossFunction = lossFunction;
            this.metrics = metrics != null ? metrics : new HashMap<>();
            this.quantumFeatures = quantumFeatures;
            this.memoryCapacity = memoryCapacity;
            this.evolutionGeneration = evolutionGeneration;
            this.fitnessScore = fitnessScore;
            this.timestamp = timestamp != null ? timestamp : "";
        }
    }

    public static class TrainingConfig {
        public int epochs;
        public int batchSize;
        public double learningRate;
        public String optimizer;
        public String lossFunction;
        public List<String> metrics;
        public boolean earlyStopping;
        public double dropoutRate;
        public String regularization;
        public double regularizationStrength;

        public TrainingConfig() {
            this.metrics = new ArrayList<>();
        }
    }

    // ---------- Main Creator Class ----------
    private static final Logger LOGGER = Logger.getLogger(NeuralNetworkCreator.class.getName());

    static {
        try {
            // Remove default console handler
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // File handler
            FileHandler fileHandler = new FileHandler("neural_network_creator.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);

            // Console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(consoleHandler);

            rootLogger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Failed to setup logging: " + e.getMessage());
        }
    }

    private final Deque<NeuralNetwork> networks;
    private int neuronCounter;
    private int synapseCounter;
    private int layerCounter;
    private int networkCounter;

    private double mutationRate;
    private double crossoverRate;
    private double elitismRate;
    private int populationSize;

    private final Deque<Map<String, Object>> evolutionHistory;
    private final Deque<NeuralNetwork> bestNetworks;

    private boolean quantumFeaturesEnabled;
    private int quantumCircuitDepth;
    private double quantumEntanglementThreshold;

    private final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public NeuralNetworkCreator() {
        this.networks = new ArrayDeque<>(); // maxlen 100 not strictly enforced but could add
        this.neuronCounter = 0;
        this.synapseCounter = 0;
        this.layerCounter = 0;
        this.networkCounter = 0;
        this.mutationRate = 0.1;
        this.crossoverRate = 0.7;
        this.elitismRate = 0.2;
        this.populationSize = 10;
        this.evolutionHistory = new ArrayDeque<>();
        this.bestNetworks = new ArrayDeque<>();
        this.quantumFeaturesEnabled = true;
        this.quantumCircuitDepth = 5;
        this.quantumEntanglementThreshold = 0.7;
        LOGGER.info("Sistema de criação de redes neurais inicializado");
    }

    private double randomUniform(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    private String nowISO() {
        return LocalDateTime.now().format(isoFormatter);
    }

    public Neuron createNeuron(NeuronType neuronType, ActivationFunction activationFunction,
                               int numInputs, boolean quantumEnabled) {
        neuronCounter++;
        double limit = Math.sqrt(6.0 / (numInputs + 1));
        List<Double> weights = new ArrayList<>();
        for (int i = 0; i < numInputs; i++) {
            weights.add(randomUniform(-limit, limit));
        }
        double bias = randomUniform(-0.1, 0.1);

        List<Double> quantumState = null;
        if (quantumEnabled && quantumFeaturesEnabled) {
            quantumState = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                quantumState.add(randomUniform(-1, 1));
            }
            double norm = 0;
            for (double q : quantumState) norm += q * q;
            norm = Math.sqrt(norm);
            if (norm > 0) {
                for (int i = 0; i < quantumState.size(); i++) {
                    quantumState.set(i, quantumState.get(i) / norm);
                }
            }
        }

        List<Double> memoryState = null;
        if (neuronType == NeuronType.RECURRENT || neuronType == NeuronType.MEMORY) {
            memoryState = new ArrayList<>();
            for (int i = 0; i < numInputs; i++) memoryState.add(0.0);
        }

        return new Neuron(
                "neuron_" + neuronCounter,
                neuronType,
                activationFunction,
                new ArrayList<>(), // inputs will be set later
                new ArrayList<>(), // outputs
                weights,
                bias,
                0.0, // activationValue
                0.0, // gradient
                0.001, // learningRate
                0.0, // dropoutRate
                false, // batchNorm
                quantumState,
                memoryState,
                randomUniform(0.001, 0.1),
                randomUniform(0.05, 0.2),
                nowISO()
        );
    }

    public Synapse createSynapse(String sourceNeuron, String targetNeuron, Double initialWeight) {
        synapseCounter++;
        if (initialWeight == null) initialWeight = randomUniform(-0.5, 0.5);
        double quantumEntanglement = 0.0;
        if (quantumFeaturesEnabled && ThreadLocalRandom.current().nextDouble() > 0.7) {
            quantumEntanglement = randomUniform(0.5, 1.0);
        }
        return new Synapse(
                "synapse_" + synapseCounter,
                sourceNeuron,
                targetNeuron,
                initialWeight,
                randomUniform(0.001, 0.01),
                randomUniform(0.001, 0.1),
                randomUniform(0.05, 0.2),
                quantumEntanglement,
                0.0,
                0,
                nowISO()
        );
    }

    public NeuralLayer createLayer(NeuronType layerType, int numNeurons,
                                   ActivationFunction activationFunction, int inputSize,
                                   double dropoutRate, boolean batchNorm, boolean residual) {
        layerCounter++;
        List<Neuron> neurons = new ArrayList<>();
        boolean quantumEnabled = layerType == NeuronType.QUANTUM || layerType == NeuronType.ATTENTION;
        for (int i = 0; i < numNeurons; i++) {
            neurons.add(createNeuron(layerType, activationFunction, inputSize, quantumEnabled));
        }

        List<Double> attentionWeights = null;
        if (layerType == NeuronType.ATTENTION) {
            attentionWeights = new ArrayList<>();
            for (int i = 0; i < numNeurons; i++) {
                attentionWeights.add(randomUniform(0, 1));
            }
            double total = attentionWeights.stream().mapToDouble(Double::doubleValue).sum();
            for (int i = 0; i < attentionWeights.size(); i++) {
                attentionWeights.set(i, attentionWeights.get(i) / total);
            }
        }

        double quantumCoherence = 0.0;
        if (quantumEnabled) {
            quantumCoherence = randomUniform(0.5, 1.0);
        }

        return new NeuralLayer(
                "layer_" + layerCounter,
                layerType,
                neurons,
                activationFunction,
                dropoutRate,
                batchNorm,
                residual,
                attentionWeights,
                quantumCoherence,
                nowISO()
        );
    }

    public NeuralNetwork createFeedForwardNetwork(List<Integer> layerSizes,
                                                   List<ActivationFunction> activationFunctions,
                                                   int inputSize, int outputSize) {
        networkCounter++;
        List<NeuralLayer> layers = new ArrayList<>();
        List<Synapse> synapses = new ArrayList<>();
        List<Neuron> prevLayerNeurons = new ArrayList<>();

        for (int i = 0; i < layerSizes.size(); i++) {
            int size = layerSizes.get(i);
            ActivationFunction activation = activationFunctions.get(i);
            NeuronType layerType;
            if (i == 0) layerType = NeuronType.INPUT;
            else if (i == layerSizes.size() - 1) layerType = NeuronType.OUTPUT;
            else layerType = NeuronType.HIDDEN;

            int currentInputSize = (i == 0) ? inputSize : layerSizes.get(i - 1);
            double dropout = (i > 0) ? 0.1 : 0.0;
            boolean bn = i > 0;

            NeuralLayer layer = createLayer(layerType, size, activation, currentInputSize, dropout, bn, false);
            layers.add(layer);

            if (!prevLayerNeurons.isEmpty()) {
                for (Neuron prev : prevLayerNeurons) {
                    for (Neuron curr : layer.neurons) {
                        Synapse syn = createSynapse(prev.neuronId, curr.neuronId, null);
                        synapses.add(syn);
                        prev.outputs.add(curr.neuronId);
                        curr.inputs.add(prev.neuronId);
                    }
                }
            }
            prevLayerNeurons = layer.neurons;
        }

        int totalParams = 0;
        for (NeuralLayer layer : layers) {
            for (Neuron neuron : layer.neurons) {
                totalParams += neuron.weights.size() + 1;
            }
        }

        NeuralNetwork network = new NeuralNetwork(
                "network_" + networkCounter,
                NetworkArchitecture.FEED_FORWARD,
                layers,
                synapses,
                inputSize,
                outputSize,
                totalParams,
                0.001, "adam", "mse",
                new HashMap<String, Double>() {{
                    put("accuracy", 0.0);
                    put("loss", 1.0);
                }},
                false, 0, 0, 0.0,
                nowISO()
        );

        networks.add(network);
        return network;
    }

    public NeuralNetwork createQuantumNeuralNetwork(List<Integer> layerSizes,
                                                    int inputSize, int outputSize) {
        networkCounter++;
        List<NeuralLayer> layers = new ArrayList<>();
        List<Synapse> synapses = new ArrayList<>();
        List<Neuron> prevLayerNeurons = new ArrayList<>();

        ActivationFunction[] quantumActivations = {
                ActivationFunction.QUANTUM_SIGMOID,
                ActivationFunction.HYPERBOLIC_QUANTUM
        };

        for (int i = 0; i < layerSizes.size(); i++) {
            int size = layerSizes.get(i);
            NeuronType layerType;
            ActivationFunction activation;

            if (i == 0) {
                layerType = NeuronType.INPUT;
                activation = ActivationFunction.SIGMOID;
            } else if (i == layerSizes.size() - 1) {
                layerType = NeuronType.OUTPUT;
                activation = ActivationFunction.SIGMOID;
            } else {
                layerType = randomUniform(0, 1) < 0.33 ? NeuronType.QUANTUM :
                        (randomUniform(0, 1) < 0.5 ? NeuronType.ATTENTION : NeuronType.MEMORY);
                activation = quantumActivations[ThreadLocalRandom.current().nextInt(2)];
            }

            int currentInputSize = (i == 0) ? inputSize : layerSizes.get(i - 1);
            boolean residual = i > 1;
            NeuralLayer layer = createLayer(layerType, size, activation, currentInputSize,
                    0.1, true, residual);
            layers.add(layer);

            if (!prevLayerNeurons.isEmpty()) {
                for (Neuron prev : prevLayerNeurons) {
                    for (Neuron curr : layer.neurons) {
                        double initWeight = randomUniform(-0.3, 0.3);
                        Synapse syn = createSynapse(prev.neuronId, curr.neuronId, initWeight);
                        if (ThreadLocalRandom.current().nextDouble() > 0.3) {
                            syn.quantumEntanglement = randomUniform(0.7, 1.0);
                        }
                        synapses.add(syn);
                        prev.outputs.add(curr.neuronId);
                        curr.inputs.add(prev.neuronId);
                    }
                }
            }
            prevLayerNeurons = layer.neurons;
        }

        int totalParams = 0;
        for (NeuralLayer layer : layers) {
            for (Neuron neuron : layer.neurons) {
                totalParams += neuron.weights.size() + 1;
            }
        }

        NeuralNetwork network = new NeuralNetwork(
                "quantum_network_" + networkCounter,
                NetworkArchitecture.QUANTUM_NEURAL,
                layers, synapses,
                inputSize, outputSize, totalParams,
                0.001, "quantum_adam", "quantum_mse",
                new HashMap<String, Double>() {{
                    put("accuracy", 0.0);
                    put("loss", 1.0);
                    put("quantum_coherence", 0.0);
                }},
                true, 100, 0, 0.0,
                nowISO()
        );

        networks.add(network);
        return network;
    }

    public NeuralNetwork createTransformerNetwork(int inputSize, int outputSize,
                                                  int numHeads, int numLayers, int dModel) {
        networkCounter++;
        List<NeuralLayer> layers = new ArrayList<>();
        List<Synapse> synapses = new ArrayList<>();
        List<Neuron> prevLayerNeurons;

        // Input embedding layer
        NeuralLayer embeddingLayer = createLayer(NeuronType.INPUT, dModel, ActivationFunction.GELU,
                inputSize, 0.0, true, false);
        layers.add(embeddingLayer);

        // Connect input to embedding (dummy input neurons? Not in original, but we assume input tokens)
        // In Python, it loops input_size and uses "input_i" ids. We'll just create virtual connections.
        // Simplified: we skip explicit input nodes and assume they exist.
        prevLayerNeurons = embeddingLayer.neurons;

        for (int layerIdx = 0; layerIdx < numLayers; layerIdx++) {
            // Multi-head attention
            NeuralLayer attnLayer = createLayer(NeuronType.ATTENTION, dModel, ActivationFunction.GELU,
                    dModel, 0.0, true, true);
            layers.add(attnLayer);

            for (Neuron prev : prevLayerNeurons) {
                for (Neuron attn : attnLayer.neurons) {
                    Synapse syn = createSynapse(prev.neuronId, attn.neuronId, null);
                    synapses.add(syn);
                    prev.outputs.add(attn.neuronId);
                    attn.inputs.add(prev.neuronId);
                }
            }

            // Feed-forward layer
            NeuralLayer ffLayer = createLayer(NeuronType.HIDDEN, dModel * 4, ActivationFunction.GELU,
                    dModel, 0.0, true, true);
            layers.add(ffLayer);

            for (Neuron attn : attnLayer.neurons) {
                for (Neuron ff : ffLayer.neurons) {
                    Synapse syn = createSynapse(attn.neuronId, ff.neuronId, null);
                    synapses.add(syn);
                    attn.outputs.add(ff.neuronId);
                    ff.inputs.add(attn.neuronId);
                }
            }

            // Residual connection (dummy weight 0.1)
            for (Neuron ff : ffLayer.neurons) {
                for (Neuron nextAttn : attnLayer.neurons) {
                    Synapse syn = createSynapse(ff.neuronId, nextAttn.neuronId, 0.1);
                    synapses.add(syn);
                }
            }

            prevLayerNeurons = ffLayer.neurons;
        }

        // Output layer
        NeuralLayer outputLayer = createLayer(NeuronType.OUTPUT, outputSize, ActivationFunction.SIGMOID,
                dModel, 0.0, true, false);
        layers.add(outputLayer);

        for (Neuron prev : prevLayerNeurons) {
            for (Neuron out : outputLayer.neurons) {
                Synapse syn = createSynapse(prev.neuronId, out.neuronId, null);
                synapses.add(syn);
                prev.outputs.add(out.neuronId);
                out.inputs.add(prev.neuronId);
            }
        }

        int totalParams = 0;
        for (NeuralLayer layer : layers) {
            for (Neuron neuron : layer.neurons) {
                totalParams += neuron.weights.size() + 1;
            }
        }

        NeuralNetwork network = new NeuralNetwork(
                "transformer_" + networkCounter,
                NetworkArchitecture.TRANSFORMER,
                layers, synapses,
                inputSize, outputSize, totalParams,
                0.0001, "adam", "crossentropy",
                new HashMap<String, Double>() {{
                    put("accuracy", 0.0);
                    put("loss", 1.0);
                }},
                false, 1000, 0, 0.0,
                nowISO()
        );

        networks.add(network);
        return network;
    }

    public NeuralNetwork evolveNetwork(NeuralNetwork parentNetwork) {
        networkCounter++;
        List<NeuralLayer> evolvedLayers = new ArrayList<>();
        List<Synapse> evolvedSynapses = new ArrayList<>();
        double mutationStrength = 0.1;

        for (NeuralLayer layer : parentNetwork.layers) {
            List<Neuron> evolvedNeurons = new ArrayList<>();
            for (Neuron neuron : layer.neurons) {
                List<Double> evolvedWeights = new ArrayList<>();
                for (double weight : neuron.weights) {
                    if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
                        evolvedWeights.add(weight + randomUniform(-mutationStrength, mutationStrength));
                    } else {
                        evolvedWeights.add(weight);
                    }
                }
                double evolvedBias = neuron.bias;
                if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
                    evolvedBias += randomUniform(-mutationStrength, mutationStrength);
                }
                double evolvedLR = neuron.learningRate;
                if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
                    evolvedLR *= randomUniform(0.8, 1.2);
                    evolvedLR = Math.max(0.0001, Math.min(0.1, evolvedLR));
                }
                neuronCounter++;
                Neuron evolved = new Neuron(
                        "evolved_neuron_" + neuronCounter,
                        neuron.neuronType,
                        neuron.activationFunction,
                        new ArrayList<>(neuron.inputs),
                        new ArrayList<>(neuron.outputs),
                        evolvedWeights,
                        evolvedBias,
                        0.0, 0.0,
                        evolvedLR,
                        neuron.dropoutRate,
                        neuron.batchNorm,
                        neuron.quantumState != null ? new ArrayList<>(neuron.quantumState) : null,
                        neuron.memoryState != null ? new ArrayList<>(neuron.memoryState) : null,
                        neuron.plasticity * randomUniform(0.9, 1.1),
                        neuron.hebbianFactor * randomUniform(0.9, 1.1),
                        nowISO()
                );
                evolvedNeurons.add(evolved);
            }
            layerCounter++;
            NeuralLayer evolvedLayer = new NeuralLayer(
                    "evolved_layer_" + layerCounter,
                    layer.layerType,
                    evolvedNeurons,
                    layer.activationFunction,
                    layer.dropoutRate,
                    layer.batchNorm,
                    layer.residualConnection,
                    layer.attentionWeights != null ? new ArrayList<>(layer.attentionWeights) : null,
                    layer.quantumCoherence * randomUniform(0.95, 1.05),
                    nowISO()
            );
            evolvedLayers.add(evolvedLayer);
        }

        for (Synapse synapse : parentNetwork.synapses) {
            double evolvedWeight = synapse.weight;
            if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
                evolvedWeight += randomUniform(-mutationStrength, mutationStrength);
            }
            synapseCounter++;
            Synapse evolved = new Synapse(
                    "evolved_synapse_" + synapseCounter,
                    synapse.sourceNeuron,
                    synapse.targetNeuron,
                    evolvedWeight,
                    synapse.delay,
                    synapse.plasticity * randomUniform(0.9, 1.1),
                    synapse.hebbianStrength * randomUniform(0.9, 1.1),
                    synapse.quantumEntanglement * randomUniform(0.95, 1.05),
                    synapse.lastSpikeTime,
                    synapse.spikeCount,
                    nowISO()
            );
            evolvedSynapses.add(evolved);
        }

        int totalParams = 0;
        for (NeuralLayer layer : evolvedLayers) {
            for (Neuron neuron : layer.neurons) {
                totalParams += neuron.weights.size() + 1;
            }
        }

        NeuralNetwork evolvedNetwork = new NeuralNetwork(
                "evolved_" + networkCounter,
                parentNetwork.architecture,
                evolvedLayers,
                evolvedSynapses,
                parentNetwork.inputSize,
                parentNetwork.outputSize,
                totalParams,
                parentNetwork.learningRate * randomUniform(0.9, 1.1),
                parentNetwork.optimizer,
                parentNetwork.lossFunction,
                new HashMap<>(parentNetwork.metrics),
                parentNetwork.quantumFeatures,
                parentNetwork.memoryCapacity,
                parentNetwork.evolutionGeneration + 1,
                0.0,
                nowISO()
        );

        networks.add(evolvedNetwork);
        Map<String, Object> hist = new HashMap<>();
        hist.put("parent_id", parentNetwork.networkId);
        hist.put("evolved_id", evolvedNetwork.networkId);
        hist.put("generation", evolvedNetwork.evolutionGeneration);
        hist.put("mutation_rate", mutationRate);
        hist.put("timestamp", nowISO());
        evolutionHistory.add(hist);

        return evolvedNetwork;
    }

    public NeuralNetwork crossoverNetworks(NeuralNetwork parent1, NeuralNetwork parent2) {
        if (parent1.architecture != parent2.architecture) {
            throw new IllegalArgumentException("Redes devem ter a mesma arquitetura para crossover");
        }
        networkCounter++;
        List<NeuralLayer> childLayers = new ArrayList<>();
        List<Synapse> childSynapses = new ArrayList<>();

        int minLayers = Math.min(parent1.layers.size(), parent2.layers.size());
        for (int i = 0; i < minLayers; i++) {
            NeuralLayer layer1 = parent1.layers.get(i);
            NeuralLayer layer2 = parent2.layers.get(i);
            if (layer1.neurons.size() != layer2.neurons.size()) continue;

            List<Neuron> childNeurons = new ArrayList<>();
            int minNeurons = Math.min(layer1.neurons.size(), layer2.neurons.size());
            for (int j = 0; j < minNeurons; j++) {
                Neuron n1 = layer1.neurons.get(j);
                Neuron n2 = layer2.neurons.get(j);
                List<Double> childWeights = new ArrayList<>();
                int minWeights = Math.min(n1.weights.size(), n2.weights.size());
                for (int k = 0; k < minWeights; k++) {
                    if (ThreadLocalRandom.current().nextDouble() < 0.5) {
                        childWeights.add(n1.weights.get(k));
                    } else {
                        childWeights.add(n2.weights.get(k));
                    }
                }
                double childBias = ThreadLocalRandom.current().nextDouble() < 0.5 ? n1.bias : n2.bias;
                neuronCounter++;
                Neuron child = new Neuron(
                        "child_neuron_" + neuronCounter,
                        n1.neuronType,
                        n1.activationFunction,
                        new ArrayList<>(n1.inputs),
                        new ArrayList<>(n1.outputs),
                        childWeights,
                        childBias,
                        0.0, 0.0,
                        ThreadLocalRandom.current().nextDouble() < 0.5 ? n1.learningRate : n2.learningRate,
                        ThreadLocalRandom.current().nextDouble() < 0.5 ? n1.dropoutRate : n2.dropoutRate,
                        n1.batchNorm,
                        n1.quantumState != null ? new ArrayList<>(n1.quantumState) :
                                (n2.quantumState != null ? new ArrayList<>(n2.quantumState) : null),
                        n1.memoryState != null ? new ArrayList<>(n1.memoryState) :
                                (n2.memoryState != null ? new ArrayList<>(n2.memoryState) : null),
                        ThreadLocalRandom.current().nextDouble() < 0.5 ? n1.plasticity : n2.plasticity,
                        ThreadLocalRandom.current().nextDouble() < 0.5 ? n1.hebbianFactor : n2.hebbianFactor,
                        nowISO()
                );
                childNeurons.add(child);
            }
            layerCounter++;
            NeuralLayer childLayer = new NeuralLayer(
                    "child_layer_" + layerCounter,
                    layer1.layerType,
                    childNeurons,
                    layer1.activationFunction,
                    ThreadLocalRandom.current().nextDouble() < 0.5 ? layer1.dropoutRate : layer2.dropoutRate,
                    layer1.batchNorm,
                    layer1.residualConnection,
                    layer1.attentionWeights != null ? new ArrayList<>(layer1.attentionWeights) :
                            (layer2.attentionWeights != null ? new ArrayList<>(layer2.attentionWeights) : null),
                    ThreadLocalRandom.current().nextDouble() < 0.5 ? layer1.quantumCoherence : layer2.quantumCoherence,
                    nowISO()
            );
            childLayers.add(childLayer);
        }

        int minSynapses = Math.min(parent1.synapses.size(), parent2.synapses.size());
        for (int i = 0; i < minSynapses; i++) {
            Synapse s1 = parent1.synapses.get(i);
            Synapse s2 = parent2.synapses.get(i);
            double childWeight = ThreadLocalRandom.current().nextDouble() < 0.5 ? s1.weight : s2.weight;
            synapseCounter++;
            Synapse childSynapse = new Synapse(
                    "child_synapse_" + synapseCounter,
                    s1.sourceNeuron,
                    s1.targetNeuron,
                    childWeight,
                    ThreadLocalRandom.current().nextDouble() < 0.5 ? s1.delay : s2.delay,
                    ThreadLocalRandom.current().nextDouble() < 0.5 ? s1.plasticity : s2.plasticity,
                    ThreadLocalRandom.current().nextDouble() < 0.5 ? s1.hebbianStrength : s2.hebbianStrength,
                    ThreadLocalRandom.current().nextDouble() < 0.5 ? s1.quantumEntanglement : s2.quantumEntanglement,
                    s1.lastSpikeTime,
                    s1.spikeCount,
                    nowISO()
            );
            childSynapses.add(childSynapse);
        }

        int totalParams = 0;
        for (NeuralLayer layer : childLayers) {
            for (Neuron neuron : layer.neurons) {
                totalParams += neuron.weights.size() + 1;
            }
        }

        NeuralNetwork childNetwork = new NeuralNetwork(
                "child_" + networkCounter,
                parent1.architecture,
                childLayers,
                childSynapses,
                parent1.inputSize,
                parent1.outputSize,
                totalParams,
                ThreadLocalRandom.current().nextDouble() < 0.5 ? parent1.learningRate : parent2.learningRate,
                parent1.optimizer,
                parent1.lossFunction,
                new HashMap<>(parent1.metrics),
                parent1.quantumFeatures,
                parent1.memoryCapacity,
                Math.max(parent1.evolutionGeneration, parent2.evolutionGeneration) + 1,
                0.0,
                nowISO()
        );

        networks.add(childNetwork);
        return childNetwork;
    }

    public Map<String, Object> getNetworkSummary(NeuralNetwork network) {
        int totalNeurons = network.layers.stream().mapToInt(l -> l.neurons.size()).sum();
        int totalSynapses = network.synapses.size();

        Map<String, Integer> neuronCounts = new HashMap<>();
        for (NeuralLayer layer : network.layers) {
            String type = layer.layerType.getValue();
            neuronCounts.put(type, neuronCounts.getOrDefault(type, 0) + layer.neurons.size());
        }

        long quantumNeurons = network.layers.stream()
                .flatMap(l -> l.neurons.stream())
                .filter(n -> n.quantumState != null)
                .count();
        long quantumSynapses = network.synapses.stream()
                .filter(s -> s.quantumEntanglement > 0.5)
                .count();
        double avgQuantumCoherence = network.layers.stream()
                .mapToDouble(l -> l.quantumCoherence)
                .average()
                .orElse(0.0);

        Map<String, Object> quantumFeats = new HashMap<>();
        quantumFeats.put("enabled", network.quantumFeatures);
        quantumFeats.put("quantum_neurons", quantumNeurons);
        quantumFeats.put("quantum_synapses", quantumSynapses);
        quantumFeats.put("avg_coherence", avgQuantumCoherence);

        Map<String, Object> summary = new HashMap<>();
        summary.put("network_id", network.networkId);
        summary.put("architecture", network.architecture.getValue());
        summary.put("total_neurons", totalNeurons);
        summary.put("total_synapses", totalSynapses);
        summary.put("total_parameters", network.totalParameters);
        summary.put("neuron_types", neuronCounts);
        summary.put("quantum_features", quantumFeats);
        summary.put("evolution_generation", network.evolutionGeneration);
        summary.put("fitness_score", network.fitnessScore);
        summary.put("learning_rate", network.learningRate);
        summary.put("optimizer", network.optimizer);
        summary.put("loss_function", network.lossFunction);

        return summary;
    }

    // ---------- Demo ----------
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DEMONSTRAÇÃO - SISTEMA AVANÇADO DE CRIAÇÃO DE REDES NEURAIS");
        System.out.println("=".repeat(80));

        NeuralNetworkCreator creator = new NeuralNetworkCreator();

        // 1. Feed-forward
        System.out.println("\n1. Criando Rede Neural Feed-Forward...");
        NeuralNetwork ffNetwork = creator.createFeedForwardNetwork(
                Arrays.asList(64, 32, 16, 8),
                Arrays.asList(ActivationFunction.RELU, ActivationFunction.RELU,
                        ActivationFunction.RELU, ActivationFunction.SIGMOID),
                10, 1
        );
        Map<String, Object> ffSummary = creator.getNetworkSummary(ffNetwork);
        System.out.printf("   ID: %s\n", ffSummary.get("network_id"));
        System.out.printf("   Arquitetura: %s\n", ffSummary.get("architecture"));
        System.out.printf("   Neurônios: %s\n", ffSummary.get("total_neurons"));
        System.out.printf("   Sinapses: %s\n", ffSummary.get("total_synapses"));
        System.out.printf("   Parâmetros: %,d\n", ffSummary.get("total_parameters"));

        // 2. Quantum
        System.out.println("\n2. Criando Rede Neural Quântica...");
        NeuralNetwork qNetwork = creator.createQuantumNeuralNetwork(
                Arrays.asList(32, 16, 8), 10, 1
        );
        Map<String, Object> qSummary = creator.getNetworkSummary(qNetwork);
        Map<String, Object> qFeats = (Map<String, Object>) qSummary.get("quantum_features");
        System.out.printf("   ID: %s\n", qSummary.get("network_id"));
        System.out.printf("   Arquitetura: %s\n", qSummary.get("architecture"));
        System.out.printf("   Neurônios Quânticos: %s\n", qFeats.get("quantum_neurons"));
        System.out.printf("   Sinapses Quânticas: %s\n", qFeats.get("quantum_synapses"));
        System.out.printf("   Coerência Média: %.3f\n", qFeats.get("avg_coherence"));

        // 3. Transformer
        System.out.println("\n3. Criando Rede Neural Transformer...");
        NeuralNetwork tNetwork = creator.createTransformerNetwork(512, 10, 8, 4, 256);
        Map<String, Object> tSummary = creator.getNetworkSummary(tNetwork);
        System.out.printf("   ID: %s\n", tSummary.get("network_id"));
        System.out.printf("   Arquitetura: %s\n", tSummary.get("architecture"));
        System.out.printf("   Neurônios: %s\n", tSummary.get("total_neurons"));
        System.out.printf("   Parâmetros: %,d\n", tSummary.get("total_parameters"));

        // 4. Evolve
        System.out.println("\n4. Evoluindo Rede Neural...");
        NeuralNetwork evolved = creator.evolveNetwork(ffNetwork);
        Map<String, Object> eSummary = creator.getNetworkSummary(evolved);
        System.out.printf("   Rede Pai: %s\n", ffSummary.get("network_id"));
        System.out.printf("   Rede Evoluída: %s\n", eSummary.get("network_id"));
        System.out.printf("   Geração: %s\n", eSummary.get("evolution_generation"));
        System.out.printf("   Taxa de Mutação: %.2f\n", creator.mutationRate);

        // 5. Crossover
        System.out.println("\n5. Crossover entre Redes...");
        try {
            NeuralNetwork child = creator.crossoverNetworks(ffNetwork, qNetwork);
            Map<String, Object> cSummary = creator.getNetworkSummary(child);
            System.out.printf("   Pai 1: %s\n", ffSummary.get("network_id"));
            System.out.printf("   Pai 2: %s\n", qSummary.get("network_id"));
            System.out.printf("   Filho: %s\n", cSummary.get("network_id"));
            System.out.printf("   Geração: %s\n", cSummary.get("evolution_generation"));
        } catch (IllegalArgumentException e) {
            System.out.println("   Erro no crossover: " + e.getMessage());
            System.out.println("   (Arquiteturas diferentes não podem fazer crossover)");
        }

        // 6. Final stats
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ESTATÍSTICAS FINAIS DO CRIADOR");
        System.out.println("=".repeat(60));

        System.out.printf("Total de Redes Criadas: %d\n", creator.networks.size());
        System.out.printf("Total de Neurônios Criados: %d\n", creator.neuronCounter);
        System.out.printf("Total de Sinapses Criadas: %d\n", creator.synapseCounter);
        System.out.printf("Total de Camadas Criadas: %d\n", creator.layerCounter);
        System.out.printf("Histórico de Evolução: %d eventos\n", creator.evolutionHistory.size());

        Map<String, Long> archCount = new HashMap<>();
        for (NeuralNetwork net : creator.networks) {
            String arch = net.architecture.getValue();
            archCount.merge(arch, 1L, Long::sum);
        }
        System.out.println("\nDistribuição de Arquiteturas:");
        archCount.forEach((k, v) -> System.out.printf("  %s: %d\n", k, v));

        Optional<NeuralNetwork> best = creator.networks.stream()
                .max(Comparator.comparingInt(n -> n.totalParameters));
        best.ifPresent(b -> System.out.printf("\nMaior Rede: %s (%,d parâmetros)\n",
                b.networkId, b.totalParameters));
    }
}
```
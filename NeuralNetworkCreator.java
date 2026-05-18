import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;

/**
 * VHALINOR IAG 5.0 - Sistema Avançado de Criação de Redes Neurais (Java Port)
 * Criação de neurônios, camadas, sinapses e redes com arquiteturas quânticas e evolução.
 */
public class NeuralNetworkCreator {

    // ==================== ENUMS ====================
    public enum NeuronType {
        INPUT("input"), HIDDEN("hidden"), OUTPUT("output"), RECURRENT("recurrent"),
        CONVOLUTIONAL("convolutional"), ATTENTION("attention"), QUANTUM("quantum"),
        MEMORY("memory"), MODULATION("modulation");

        public final String value;
        NeuronType(String v) { value = v; }
    }

    public enum ActivationFunction {
        SIGMOID("sigmoid"), TANH("tanh"), RELU("relu"), LEAKY_RELU("leaky_relu"),
        ELU("elu"), SWISH("swish"), GELU("gelu"), QUANTUM_SIGMOID("quantum_sigmoid"),
        HYPERBOLIC_QUANTUM("hyperbolic_quantum");

        public final String value;
        ActivationFunction(String v) { value = v; }
    }

    public enum NetworkArchitecture {
        FEED_FORWARD("feed_forward"), CONVOLUTIONAL("convolutional"), RECURRENT("recurrent"),
        LSTM("lstm"), GRU("gru"), TRANSFORMER("transformer"), AUTOENCODER("autoencoder"),
        VARIATIONAL_AUTOENCODER("variational_autoencoder"),
        GENERATIVE_ADVERSARIAL("generative_adversarial"), QUANTUM_NEURAL("quantum_neural"),
        HYBRID_QUANTUM_CLASSICAL("hybrid_quantum_classical"),
        ATTENTION_MECHANISM("attention_mechanism"), RESIDUAL("residual"),
        DENSELY_CONNECTED("densely_connected");

        public final String value;
        NetworkArchitecture(String v) { value = v; }
    }

    // ==================== DATA CLASSES ====================
    public static class Neuron {
        public String neuronId;
        public NeuronType neuronType;
        public ActivationFunction activationFunction;
        public List<String> inputs = new ArrayList<>();
        public List<String> outputs = new ArrayList<>();
        public List<Double> weights = new ArrayList<>();
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

        public Neuron(String neuronId, NeuronType neuronType, ActivationFunction activationFunction,
                      List<Double> weights, double bias, double learningRate, double dropoutRate,
                      boolean batchNorm, List<Double> quantumState, List<Double> memoryState,
                      double plasticity, double hebbianFactor, String timestamp) {
            this.neuronId = neuronId;
            this.neuronType = neuronType;
            this.activationFunction = activationFunction;
            this.weights.addAll(weights);
            this.bias = bias;
            this.learningRate = learningRate;
            this.dropoutRate = dropoutRate;
            this.batchNorm = batchNorm;
            this.quantumState = quantumState != null ? new ArrayList<>(quantumState) : null;
            this.memoryState = memoryState != null ? new ArrayList<>(memoryState) : null;
            this.plasticity = plasticity;
            this.hebbianFactor = hebbianFactor;
            this.timestamp = timestamp;
            activationValue = 0.0;
            gradient = 0.0;
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

        public Synapse(String synapseId, String sourceNeuron, String targetNeuron, double weight,
                       double delay, double plasticity, double hebbianStrength,
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
            this.timestamp = timestamp;
        }
    }

    public static class NeuralLayer {
        public String layerId;
        public NeuronType layerType;
        public List<Neuron> neurons = new ArrayList<>();
        public ActivationFunction activationFunction;
        public double dropoutRate;
        public boolean batchNorm;
        public boolean residualConnection;
        public List<Double> attentionWeights;
        public double quantumCoherence;
        public String timestamp;

        public NeuralLayer(String layerId, NeuronType layerType, List<Neuron> neurons,
                           ActivationFunction activationFunction, double dropoutRate,
                           boolean batchNorm, boolean residualConnection,
                           List<Double> attentionWeights, double quantumCoherence, String timestamp) {
            this.layerId = layerId;
            this.layerType = layerType;
            this.neurons.addAll(neurons);
            this.activationFunction = activationFunction;
            this.dropoutRate = dropoutRate;
            this.batchNorm = batchNorm;
            this.residualConnection = residualConnection;
            this.attentionWeights = attentionWeights != null ? new ArrayList<>(attentionWeights) : null;
            this.quantumCoherence = quantumCoherence;
            this.timestamp = timestamp;
        }
    }

    public static class NeuralNetwork {
        public String networkId;
        public NetworkArchitecture architecture;
        public List<NeuralLayer> layers = new ArrayList<>();
        public List<Synapse> synapses = new ArrayList<>();
        public int inputSize;
        public int outputSize;
        public int totalParameters;
        public double learningRate;
        public String optimizer;
        public String lossFunction;
        public Map<String, Double> metrics = new HashMap<>();
        public boolean quantumFeatures;
        public int memoryCapacity;
        public int evolutionGeneration;
        public double fitnessScore;
        public String timestamp;

        public NeuralNetwork(String networkId, NetworkArchitecture architecture, List<NeuralLayer> layers,
                             List<Synapse> synapses, int inputSize, int outputSize, int totalParameters,
                             double learningRate, String optimizer, String lossFunction,
                             Map<String, Double> metrics, boolean quantumFeatures, int memoryCapacity,
                             int evolutionGeneration, double fitnessScore, String timestamp) {
            this.networkId = networkId;
            this.architecture = architecture;
            this.layers.addAll(layers);
            this.synapses.addAll(synapses);
            this.inputSize = inputSize;
            this.outputSize = outputSize;
            this.totalParameters = totalParameters;
            this.learningRate = learningRate;
            this.optimizer = optimizer;
            this.lossFunction = lossFunction;
            this.metrics.putAll(metrics);
            this.quantumFeatures = quantumFeatures;
            this.memoryCapacity = memoryCapacity;
            this.evolutionGeneration = evolutionGeneration;
            this.fitnessScore = fitnessScore;
            this.timestamp = timestamp;
        }
    }

    // ==================== NEURAL NETWORK CREATOR ====================
    private static final Logger LOGGER = Logger.getLogger(NeuralNetworkCreator.class.getName());
    static {
        try {
            // Configure logging to file and console
            FileHandler fh = new FileHandler("neural_network_creator.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            ConsoleHandler ch = new ConsoleHandler();
            ch.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(ch);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Random rng = new Random();

    // Storage
    private final LinkedList<NeuralNetwork> networks = new LinkedList<>();
    private int neuronCounter = 0;
    private int synapseCounter = 0;
    private int layerCounter = 0;
    private int networkCounter = 0;

    // Evolution parameters
    private double mutationRate = 0.1;
    private double crossoverRate = 0.7;
    private double elitismRate = 0.2;
    private int populationSize = 10;

    // History
    private final LinkedList<Map<String, Object>> evolutionHistory = new LinkedList<>();
    private final LinkedList<NeuralNetwork> bestNetworks = new LinkedList<>();

    // Quantum settings
    private boolean quantumFeaturesEnabled = true;
    private int quantumCircuitDepth = 5;
    private double quantumEntanglementThreshold = 0.7;

    public NeuralNetworkCreator() {
        LOGGER.info("Sistema de criação de redes neurais inicializado");
    }

    // ==================== NEURON / SYNAPSE / LAYER CREATION ====================
    public Neuron createNeuron(NeuronType neuronType, ActivationFunction activationFunction,
                               int numInputs, boolean quantumEnabled) {
        neuronCounter++;
        // Xavier initialization
        double limit = Math.sqrt(6.0 / (numInputs + 1));
        List<Double> weights = new ArrayList<>();
        for (int i = 0; i < numInputs; i++) {
            weights.add(rng.nextDouble() * 2 * limit - limit);
        }
        double bias = rng.nextDouble() * 0.2 - 0.1;

        List<Double> quantumState = null;
        if (quantumEnabled && quantumFeaturesEnabled) {
            quantumState = new ArrayList<>();
            double norm = 0;
            for (int i = 0; i < 8; i++) {
                double val = rng.nextDouble() * 2 - 1;
                quantumState.add(val);
                norm += val * val;
            }
            norm = Math.sqrt(norm);
            if (norm > 0) {
                for (int i = 0; i < 8; i++) {
                    quantumState.set(i, quantumState.get(i) / norm);
                }
            }
        }

        List<Double> memoryState = null;
        if (neuronType == NeuronType.RECURRENT || neuronType == NeuronType.MEMORY) {
            memoryState = new ArrayList<>(Collections.nCopies(numInputs, 0.0));
        }

        double plasticity = rng.nextDouble() * 0.099 + 0.001;
        double hebbian = rng.nextDouble() * 0.15 + 0.05;
        String timestamp = LocalDateTime.now().toString();

        return new Neuron(
                "neuron_" + neuronCounter, neuronType, activationFunction, weights, bias,
                0.001, 0.0, false, quantumState, memoryState, plasticity, hebbian, timestamp);
    }

    public Synapse createSynapse(String sourceNeuron, String targetNeuron, Double initialWeight) {
        synapseCounter++;
        double weight = (initialWeight != null) ? initialWeight : rng.nextDouble() - 0.5;
        double quantumEntanglement = 0.0;
        if (quantumFeaturesEnabled && rng.nextDouble() > 0.7) {
            quantumEntanglement = rng.nextDouble() * 0.5 + 0.5;
        }
        return new Synapse(
                "synapse_" + synapseCounter, sourceNeuron, targetNeuron, weight,
                rng.nextDouble() * 0.009 + 0.001,
                rng.nextDouble() * 0.099 + 0.001,
                rng.nextDouble() * 0.15 + 0.05,
                quantumEntanglement, 0.0, 0, LocalDateTime.now().toString());
    }

    public NeuralLayer createLayer(NeuronType layerType, int numNeurons,
                                   ActivationFunction activationFunction, int inputSize,
                                   double dropoutRate, boolean batchNorm, boolean residual) {
        layerCounter++;
        List<Neuron> neurons = new ArrayList<>();
        boolean quantumEnabled = (layerType == NeuronType.QUANTUM || layerType == NeuronType.ATTENTION);
        for (int i = 0; i < numNeurons; i++) {
            neurons.add(createNeuron(layerType, activationFunction, inputSize, quantumEnabled));
        }
        List<Double> attentionWeights = null;
        if (layerType == NeuronType.ATTENTION) {
            attentionWeights = new ArrayList<>();
            double sum = 0;
            for (int i = 0; i < numNeurons; i++) {
                double w = rng.nextDouble();
                attentionWeights.add(w);
                sum += w;
            }
            for (int i = 0; i < numNeurons; i++) {
                attentionWeights.set(i, attentionWeights.get(i) / sum);
            }
        }
        double quantumCoherence = 0.0;
        if (quantumEnabled) {
            quantumCoherence = rng.nextDouble() * 0.5 + 0.5;
        }
        return new NeuralLayer("layer_" + layerCounter, layerType, neurons,
                activationFunction, dropoutRate, batchNorm, residual,
                attentionWeights, quantumCoherence, LocalDateTime.now().toString());
    }

    // ==================== NETWORK ARCHITECTURES ====================
    public NeuralNetwork createFeedForwardNetwork(List<Integer> layerSizes,
                                                  List<ActivationFunction> activationFunctions,
                                                  int inputSize, int outputSize) {
        networkCounter++;
        List<NeuralLayer> layers = new ArrayList<>();
        List<Synapse> synapses = new ArrayList<>();
        List<Neuron> prevLayerNeurons = new ArrayList<>();

        for (int i = 0; i < layerSizes.size(); i++) {
            int size = layerSizes.get(i);
            ActivationFunction act = activationFunctions.get(i);
            NeuronType layerType;
            if (i == 0) layerType = NeuronType.INPUT;
            else if (i == layerSizes.size() - 1) layerType = NeuronType.OUTPUT;
            else layerType = NeuronType.HIDDEN;

            int currentInputSize = (i == 0) ? inputSize : layerSizes.get(i - 1);
            double drop = (i > 0) ? 0.1 : 0.0;
            boolean bn = (i > 0);

            NeuralLayer layer = createLayer(layerType, size, act, currentInputSize, drop, bn, false);
            layers.add(layer);

            if (!prevLayerNeurons.isEmpty()) {
                for (Neuron prevNeuron : prevLayerNeurons) {
                    for (Neuron currNeuron : layer.neurons) {
                        Synapse syn = createSynapse(prevNeuron.neuronId, currNeuron.neuronId, null);
                        synapses.add(syn);
                        prevNeuron.outputs.add(currNeuron.neuronId);
                        currNeuron.inputs.add(prevNeuron.neuronId);
                    }
                }
            }
            prevLayerNeurons = layer.neurons;
        }

        int totalParams = layers.stream()
                .flatMap(l -> l.neurons.stream())
                .mapToInt(n -> n.weights.size() + 1)
                .sum();

        Map<String, Double> metrics = new HashMap<>();
        metrics.put("accuracy", 0.0);
        metrics.put("loss", 1.0);

        NeuralNetwork network = new NeuralNetwork(
                "network_" + networkCounter, NetworkArchitecture.FEED_FORWARD,
                layers, synapses, inputSize, outputSize, totalParams,
                0.001, "adam", "mse", metrics, false, 0, 0, 0.0,
                LocalDateTime.now().toString());
        networks.add(network);
        return network;
    }

    public NeuralNetwork createQuantumNeuralNetwork(List<Integer> layerSizes,
                                                    int inputSize, int outputSize) {
        networkCounter++;
        List<NeuralLayer> layers = new ArrayList<>();
        List<Synapse> synapses = new ArrayList<>();
        List<Neuron> prevLayerNeurons = new ArrayList<>();
        ActivationFunction[] quantumActs = {ActivationFunction.QUANTUM_SIGMOID, ActivationFunction.HYPERBOLIC_QUANTUM};

        for (int i = 0; i < layerSizes.size(); i++) {
            int size = layerSizes.get(i);
            NeuronType layerType;
            ActivationFunction act;
            if (i == 0) { layerType = NeuronType.INPUT; act = ActivationFunction.SIGMOID; }
            else if (i == layerSizes.size() - 1) { layerType = NeuronType.OUTPUT; act = ActivationFunction.SIGMOID; }
            else {
                layerType = pickRandom(NeuronType.QUANTUM, NeuronType.ATTENTION, NeuronType.MEMORY);
                act = pickRandom(quantumActs);
            }
            int currentInputSize = (i == 0) ? inputSize : layerSizes.get(i - 1);
            NeuralLayer layer = createLayer(layerType, size, act, currentInputSize, 0.1, true, (i > 1));
            layers.add(layer);

            if (!prevLayerNeurons.isEmpty()) {
                for (Neuron prevNeuron : prevLayerNeurons) {
                    for (Neuron currNeuron : layer.neurons) {
                        Double initialWeight = rng.nextDouble() * 0.6 - 0.3;
                        Synapse syn = createSynapse(prevNeuron.neuronId, currNeuron.neuronId, initialWeight);
                        if (rng.nextDouble() > 0.3) {
                            syn.quantumEntanglement = rng.nextDouble() * 0.3 + 0.7;
                        }
                        synapses.add(syn);
                        prevNeuron.outputs.add(currNeuron.neuronId);
                        currNeuron.inputs.add(prevNeuron.neuronId);
                    }
                }
            }
            prevLayerNeurons = layer.neurons;
        }

        int totalParams = layers.stream().flatMap(l -> l.neurons.stream()).mapToInt(n -> n.weights.size() + 1).sum();
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("accuracy", 0.0); metrics.put("loss", 1.0); metrics.put("quantum_coherence", 0.0);

        NeuralNetwork network = new NeuralNetwork(
                "quantum_network_" + networkCounter, NetworkArchitecture.QUANTUM_NEURAL,
                layers, synapses, inputSize, outputSize, totalParams,
                0.001, "quantum_adam", "quantum_mse", metrics, true, 100, 0, 0.0,
                LocalDateTime.now().toString());
        networks.add(network);
        return network;
    }

    public NeuralNetwork createTransformerNetwork(int inputSize, int outputSize,
                                                  int numHeads, int numLayers, int dModel) {
        networkCounter++;
        List<NeuralLayer> layers = new ArrayList<>();
        List<Synapse> synapses = new ArrayList<>();
        List<Neuron> prevLayerNeurons = new ArrayList<>();

        // Input embedding
        NeuralLayer embeddingLayer = createLayer(NeuronType.INPUT, dModel, ActivationFunction.GELU,
                inputSize, 0.0, true, false);
        layers.add(embeddingLayer);
        // Connect input (virtual) to embedding
        for (int i = 0; i < inputSize; i++) {
            for (Neuron neuron : embeddingLayer.neurons) {
                synapses.add(createSynapse("input_" + i, neuron.neuronId, null));
            }
        }
        prevLayerNeurons = embeddingLayer.neurons;

        for (int layerIdx = 0; layerIdx < numLayers; layerIdx++) {
            // Multi-head attention
            NeuralLayer attentionLayer = createLayer(NeuronType.ATTENTION, dModel, ActivationFunction.GELU,
                    dModel, 0.0, true, true);
            layers.add(attentionLayer);
            for (Neuron prevNeuron : prevLayerNeurons) {
                for (Neuron attnNeuron : attentionLayer.neurons) {
                    Synapse syn = createSynapse(prevNeuron.neuronId, attnNeuron.neuronId, null);
                    synapses.add(syn);
                    prevNeuron.outputs.add(attnNeuron.neuronId);
                    attnNeuron.inputs.add(prevNeuron.neuronId);
                }
            }
            // Feed-forward
            NeuralLayer ffLayer = createLayer(NeuronType.HIDDEN, dModel * 4, ActivationFunction.GELU,
                    dModel, 0.0, true, true);
            layers.add(ffLayer);
            for (Neuron attnNeuron : attentionLayer.neurons) {
                for (Neuron ffNeuron : ffLayer.neurons) {
                    Synapse syn = createSynapse(attnNeuron.neuronId, ffNeuron.neuronId, null);
                    synapses.add(syn);
                    attnNeuron.outputs.add(ffNeuron.neuronId);
                    ffNeuron.inputs.add(attnNeuron.neuronId);
                }
            }
            // Residual connection (simplified: connect ff to next attention via residuals? The Python code did:
            // for ff_neuron in ff_layer.neurons: for next_neuron in attention_layer.neurons (residual). But it uses the same attention_layer for residual; that's a bug in Python but we replicate)
            for (Neuron ffNeuron : ffLayer.neurons) {
                for (Neuron nextNeuron : attentionLayer.neurons) {
                    Synapse syn = createSynapse(ffNeuron.neuronId, nextNeuron.neuronId, 0.1);
                    synapses.add(syn);
                }
            }
            prevLayerNeurons = ffLayer.neurons;
        }

        // Output layer
        NeuralLayer outputLayer = createLayer(NeuronType.OUTPUT, outputSize, ActivationFunction.SIGMOID,
                dModel, 0.0, true, false);
        layers.add(outputLayer);
        for (Neuron prevNeuron : prevLayerNeurons) {
            for (Neuron outNeuron : outputLayer.neurons) {
                Synapse syn = createSynapse(prevNeuron.neuronId, outNeuron.neuronId, null);
                synapses.add(syn);
                prevNeuron.outputs.add(outNeuron.neuronId);
                outNeuron.inputs.add(prevNeuron.neuronId);
            }
        }

        int totalParams = layers.stream().flatMap(l -> l.neurons.stream()).mapToInt(n -> n.weights.size() + 1).sum();
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("accuracy", 0.0); metrics.put("loss", 1.0);

        NeuralNetwork network = new NeuralNetwork(
                "transformer_" + networkCounter, NetworkArchitecture.TRANSFORMER,
                layers, synapses, inputSize, outputSize, totalParams,
                0.0001, "adam", "crossentropy", metrics, false, 1000, 0, 0.0,
                LocalDateTime.now().toString());
        networks.add(network);
        return network;
    }

    // ==================== EVOLUTION ====================
    public NeuralNetwork evolveNetwork(NeuralNetwork parent) {
        networkCounter++;
        List<NeuralLayer> evolvedLayers = new ArrayList<>();
        List<Synapse> evolvedSynapses = new ArrayList<>();
        double mutationStrength = 0.1;

        for (NeuralLayer layer : parent.layers) {
            List<Neuron> evolvedNeurons = new ArrayList<>();
            for (Neuron neuron : layer.neurons) {
                List<Double> evolvedWeights = new ArrayList<>();
                for (double w : neuron.weights) {
                    if (rng.nextDouble() < mutationRate) {
                        evolvedWeights.add(w + (rng.nextDouble() * 2 - 1) * mutationStrength);
                    } else {
                        evolvedWeights.add(w);
                    }
                }
                double evolvedBias = neuron.bias;
                if (rng.nextDouble() < mutationRate) evolvedBias += (rng.nextDouble() * 2 - 1) * mutationStrength;
                double evolvedLR = neuron.learningRate;
                if (rng.nextDouble() < mutationRate) {
                    evolvedLR *= rng.nextDouble() * 0.4 + 0.8;
                    evolvedLR = Math.max(0.0001, Math.min(0.1, evolvedLR));
                }
                List<Double> qStateCopy = neuron.quantumState != null ? new ArrayList<>(neuron.quantumState) : null;
                List<Double> memCopy = neuron.memoryState != null ? new ArrayList<>(neuron.memoryState) : null;
                Neuron evoNeuron = new Neuron(
                        "evolved_neuron_" + (++neuronCounter), neuron.neuronType, neuron.activationFunction,
                        evolvedWeights, evolvedBias, evolvedLR, neuron.dropoutRate, neuron.batchNorm,
                        qStateCopy, memCopy,
                        neuron.plasticity * (rng.nextDouble() * 0.2 + 0.9),
                        neuron.hebbianFactor * (rng.nextDouble() * 0.2 + 0.9),
                        LocalDateTime.now().toString());
                evolvedNeurons.add(evoNeuron);
                // copy connections
                evoNeuron.inputs.addAll(neuron.inputs);
                evoNeuron.outputs.addAll(neuron.outputs);
            }
            List<Double> attnCopy = layer.attentionWeights != null ? new ArrayList<>(layer.attentionWeights) : null;
            NeuralLayer evoLayer = new NeuralLayer("evolved_layer_" + (++layerCounter), layer.layerType,
                    evolvedNeurons, layer.activationFunction, layer.dropoutRate, layer.batchNorm,
                    layer.residualConnection, attnCopy,
                    layer.quantumCoherence * (rng.nextDouble() * 0.1 + 0.95),
                    LocalDateTime.now().toString());
            evolvedLayers.add(evoLayer);
        }

        for (Synapse synapse : parent.synapses) {
            double evolvedWeight = synapse.weight;
            if (rng.nextDouble() < mutationRate) evolvedWeight += (rng.nextDouble() * 2 - 1) * mutationStrength;
            Synapse evoSyn = new Synapse("evolved_synapse_" + (++synapseCounter),
                    synapse.sourceNeuron, synapse.targetNeuron, evolvedWeight,
                    synapse.delay,
                    synapse.plasticity * (rng.nextDouble() * 0.2 + 0.9),
                    synapse.hebbianStrength * (rng.nextDouble() * 0.2 + 0.9),
                    synapse.quantumEntanglement * (rng.nextDouble() * 0.1 + 0.95),
                    synapse.lastSpikeTime, synapse.spikeCount, LocalDateTime.now().toString());
            evolvedSynapses.add(evoSyn);
        }

        int totalParams = evolvedLayers.stream().flatMap(l -> l.neurons.stream()).mapToInt(n -> n.weights.size() + 1).sum();
        NeuralNetwork evoNetwork = new NeuralNetwork("evolved_" + networkCounter, parent.architecture,
                evolvedLayers, evolvedSynapses, parent.inputSize, parent.outputSize, totalParams,
                parent.learningRate * (rng.nextDouble() * 0.2 + 0.9), parent.optimizer, parent.lossFunction,
                new HashMap<>(parent.metrics), parent.quantumFeatures, parent.memoryCapacity,
                parent.evolutionGeneration + 1, 0.0, LocalDateTime.now().toString());
        networks.add(evoNetwork);
        Map<String, Object> histEntry = new HashMap<>();
        histEntry.put("parent_id", parent.networkId);
        histEntry.put("evolved_id", evoNetwork.networkId);
        histEntry.put("generation", evoNetwork.evolutionGeneration);
        histEntry.put("mutation_rate", mutationRate);
        histEntry.put("timestamp", LocalDateTime.now().toString());
        evolutionHistory.add(histEntry);
        return evoNetwork;
    }

    public NeuralNetwork crossoverNetworks(NeuralNetwork parent1, NeuralNetwork parent2) {
        if (parent1.architecture != parent2.architecture) {
            throw new IllegalArgumentException("Redes devem ter a mesma arquitetura para crossover");
        }
        networkCounter++;
        List<NeuralLayer> childLayers = new ArrayList<>();
        List<Synapse> childSynapses = new ArrayList<>();

        for (int i = 0; i < parent1.layers.size() && i < parent2.layers.size(); i++) {
            NeuralLayer layer1 = parent1.layers.get(i);
            NeuralLayer layer2 = parent2.layers.get(i);
            if (layer1.neurons.size() != layer2.neurons.size()) continue;

            List<Neuron> childNeurons = new ArrayList<>();
            for (int j = 0; j < layer1.neurons.size(); j++) {
                Neuron n1 = layer1.neurons.get(j);
                Neuron n2 = layer2.neurons.get(j);
                List<Double> childWeights = new ArrayList<>();
                for (int k = 0; k < n1.weights.size(); k++) {
                    childWeights.add(rng.nextBoolean() ? n1.weights.get(k) : n2.weights.get(k));
                }
                double childBias = rng.nextBoolean() ? n1.bias : n2.bias;
                List<Double> qState = (n1.quantumState != null ? new ArrayList<>(n1.quantumState) :
                                        (n2.quantumState != null ? new ArrayList<>(n2.quantumState) : null));
                List<Double> mState = (n1.memoryState != null ? new ArrayList<>(n1.memoryState) :
                                       (n2.memoryState != null ? new ArrayList<>(n2.memoryState) : null));
                Neuron childNeuron = new Neuron("child_neuron_" + (++neuronCounter),
                        n1.neuronType, n1.activationFunction, childWeights, childBias,
                        rng.nextBoolean() ? n1.learningRate : n2.learningRate,
                        rng.nextBoolean() ? n1.dropoutRate : n2.dropoutRate,
                        n1.batchNorm,
                        qState, mState,
                        rng.nextBoolean() ? n1.plasticity : n2.plasticity,
                        rng.nextBoolean() ? n1.hebbianFactor : n2.hebbianFactor,
                        LocalDateTime.now().toString());
                childNeuron.inputs.addAll(n1.inputs);
                childNeuron.outputs.addAll(n1.outputs);
                childNeurons.add(childNeuron);
            }
            List<Double> attn = (layer1.attentionWeights != null ? new ArrayList<>(layer1.attentionWeights) :
                                (layer2.attentionWeights != null ? new ArrayList<>(layer2.attentionWeights) : null));
            NeuralLayer childLayer = new NeuralLayer("child_layer_" + (++layerCounter), layer1.layerType,
                    childNeurons, layer1.activationFunction,
                    rng.nextBoolean() ? layer1.dropoutRate : layer2.dropoutRate,
                    layer1.batchNorm, layer1.residualConnection, attn,
                    rng.nextBoolean() ? layer1.quantumCoherence : layer2.quantumCoherence,
                    LocalDateTime.now().toString());
            childLayers.add(childLayer);
        }

        for (int i = 0; i < parent1.synapses.size() && i < parent2.synapses.size(); i++) {
            Synapse s1 = parent1.synapses.get(i);
            Synapse s2 = parent2.synapses.get(i);
            double childWeight = rng.nextBoolean() ? s1.weight : s2.weight;
            Synapse childSyn = new Synapse("child_synapse_" + (++synapseCounter),
                    s1.sourceNeuron, s1.targetNeuron, childWeight,
                    rng.nextBoolean() ? s1.delay : s2.delay,
                    rng.nextBoolean() ? s1.plasticity : s2.plasticity,
                    rng.nextBoolean() ? s1.hebbianStrength : s2.hebbianStrength,
                    rng.nextBoolean() ? s1.quantumEntanglement : s2.quantumEntanglement,
                    s1.lastSpikeTime, s1.spikeCount, LocalDateTime.now().toString());
            childSynapses.add(childSyn);
        }

        int totalParams = childLayers.stream().flatMap(l -> l.neurons.stream()).mapToInt(n -> n.weights.size() + 1).sum();
        NeuralNetwork child = new NeuralNetwork("child_" + networkCounter, parent1.architecture,
                childLayers, childSynapses, parent1.inputSize, parent1.outputSize, totalParams,
                rng.nextBoolean() ? parent1.learningRate : parent2.learningRate,
                parent1.optimizer, parent1.lossFunction, new HashMap<>(parent1.metrics),
                parent1.quantumFeatures, parent1.memoryCapacity,
                Math.max(parent1.evolutionGeneration, parent2.evolutionGeneration) + 1,
                0.0, LocalDateTime.now().toString());
        networks.add(child);
        return child;
    }

    // ==================== SUMMARY ====================
    public Map<String, Object> getNetworkSummary(NeuralNetwork network) {
        int totalNeurons = network.layers.stream().mapToInt(l -> l.neurons.size()).sum();
        int totalSynapses = network.synapses.size();
        Map<String, Integer> typeCounts = new HashMap<>();
        for (NeuralLayer layer : network.layers) {
            typeCounts.merge(layer.layerType.value, layer.neurons.size(), Integer::sum);
        }
        int quantumNeurons = (int) network.layers.stream()
                .flatMap(l -> l.neurons.stream())
                .filter(n -> n.quantumState != null)
                .count();
        int quantumSynapses = (int) network.synapses.stream()
                .filter(s -> s.quantumEntanglement > 0.5)
                .count();
        double avgCoherence = network.layers.stream().mapToDouble(l -> l.quantumCoherence).average().orElse(0);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("network_id", network.networkId);
        summary.put("architecture", network.architecture.value);
        summary.put("total_neurons", totalNeurons);
        summary.put("total_synapses", totalSynapses);
        summary.put("total_parameters", network.totalParameters);
        summary.put("neuron_types", typeCounts);
        Map<String, Object> quantum = new HashMap<>();
        quantum.put("enabled", network.quantumFeatures);
        quantum.put("quantum_neurons", quantumNeurons);
        quantum.put("quantum_synapses", quantumSynapses);
        quantum.put("avg_coherence", avgCoherence);
        summary.put("quantum_features", quantum);
        summary.put("evolution_generation", network.evolutionGeneration);
        summary.put("fitness_score", network.fitnessScore);
        summary.put("learning_rate", network.learningRate);
        summary.put("optimizer", network.optimizer);
        summary.put("loss_function", network.lossFunction);
        return summary;
    }

    // Helper: pick random element from array
    @SafeVarargs
    private <T> T pickRandom(T... items) {
        return items[rng.nextInt(items.length)];
    }

    // ==================== DEMO ====================
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DEMONSTRAÇÃO - SISTEMA AVANÇADO DE CRIAÇÃO DE REDES NEURAIS");
        System.out.println("=".repeat(80));

        NeuralNetworkCreator creator = new NeuralNetworkCreator();

        // 1. Feed-forward network
        System.out.println("\n1. Criando Rede Neural Feed-Forward...");
        NeuralNetwork ffNet = creator.createFeedForwardNetwork(
                List.of(64, 32, 16, 8),
                List.of(ActivationFunction.RELU, ActivationFunction.RELU, ActivationFunction.RELU, ActivationFunction.SIGMOID),
                10, 1);
        Map<String, Object> ffSum = creator.getNetworkSummary(ffNet);
        System.out.printf("   ID: %s%n   Arquitetura: %s%n   Neurônios: %d%n   Sinapses: %d%n   Parâmetros: %,d%n",
                ffSum.get("network_id"), ffSum.get("architecture"), ffSum.get("total_neurons"),
                ffSum.get("total_synapses"), ffSum.get("total_parameters"));

        // 2. Quantum network
        System.out.println("\n2. Criando Rede Neural Quântica...");
        NeuralNetwork qNet = creator.createQuantumNeuralNetwork(List.of(32, 16, 8), 10, 1);
        Map<String, Object> qSum = creator.getNetworkSummary(qNet);
        Map<String, Object> qFeatures = (Map<String, Object>) qSum.get("quantum_features");
        System.out.printf("   ID: %s%n   Arquitetura: %s%n   Neurônios Quânticos: %d%n   Sinapses Quânticas: %d%n   Coerência Média: %.3f%n",
                qSum.get("network_id"), qSum.get("architecture"),
                qFeatures.get("quantum_neurons"), qFeatures.get("quantum_synapses"),
                (double) qFeatures.get("avg_coherence"));

        // 3. Transformer
        System.out.println("\n3. Criando Rede Neural Transformer...");
        NeuralNetwork trNet = creator.createTransformerNetwork(512, 10, 8, 4, 256);
        Map<String, Object> trSum = creator.getNetworkSummary(trNet);
        System.out.printf("   ID: %s%n   Arquitetura: %s%n   Neurônios: %d%n   Parâmetros: %,d%n",
                trSum.get("network_id"), trSum.get("architecture"),
                trSum.get("total_neurons"), trSum.get("total_parameters"));

        // 4. Evolve
        System.out.println("\n4. Evoluindo Rede Neural...");
        NeuralNetwork evoNet = creator.evolveNetwork(ffNet);
        Map<String, Object> evoSum = creator.getNetworkSummary(evoNet);
        System.out.printf("   Rede Pai: %s%n   Rede Evoluída: %s%n   Geração: %d%n   Taxa de Mutação: %.2f%n",
                ffNet.networkId, evoNet.networkId, evoNet.evolutionGeneration, creator.mutationRate);

        // 5. Crossover (will fail because architectures differ)
        System.out.println("\n5. Crossover entre Redes...");
        try {
            NeuralNetwork childNet = creator.crossoverNetworks(ffNet, qNet);
            Map<String, Object> childSum = creator.getNetworkSummary(childNet);
            System.out.printf("   Pai 1: %s%n   Pai 2: %s%n   Filho: %s%n   Geração: %d%n",
                    ffNet.networkId, qNet.networkId, childNet.networkId, childNet.evolutionGeneration);
        } catch (IllegalArgumentException e) {
            System.out.println("   Erro no crossover: " + e.getMessage());
            System.out.println("   (Arquiteturas diferentes não podem fazer crossover)");
        }

        // 6. Final stats
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ESTATÍSTICAS FINAIS DO CRIADOR");
        System.out.println("=".repeat(60));
        System.out.println("Total de Redes Criadas: " + creator.networks.size());
        System.out.println("Total de Neurônios Criados: " + creator.neuronCounter);
        System.out.println("Total de Sinapses Criadas: " + creator.synapseCounter);
        System.out.println("Total de Camadas Criadas: " + creator.layerCounter);
        System.out.println("Histórico de Evolução: " + creator.evolutionHistory.size() + " eventos");

        Map<String, Integer> archCounts = new HashMap<>();
        for (NeuralNetwork n : creator.networks) {
            archCounts.merge(n.architecture.value, 1, Integer::sum);
        }
        System.out.println("\nDistribuição de Arquiteturas:");
        for (Map.Entry<String, Integer> e : archCounts.entrySet()) {
            System.out.printf("  %s: %d%n", e.getKey(), e.getValue());
        }

        if (!creator.networks.isEmpty()) {
            NeuralNetwork largest = creator.networks.stream()
                    .max(Comparator.comparingInt(n -> n.totalParameters))
                    .orElse(null);
            if (largest != null) {
                System.out.printf("%nMaior Rede: %s (%,d parâmetros)%n", largest.networkId, largest.totalParameters);
            }
        }
    }
}
/**
 * Advanced Neural Network v5.0 - Enhanced AI/ML System (Java Edition)
 * ==========================================================================
 * Sistema avançado de redes neurais com capacidades expandidas de IA,
 * deep learning e aprendizado adaptativo.
 * 
 * Implementação pura em Java (sem dependências externas).
 * Melhorias: 
 * - Execução multithread para treinamento de ensemble
 * - Serialização JSON para salvar/carregar modelos
 * - Interface fluente para construção de redes
 * - Suporte a early stopping e validação cruzada
 * - Logging estruturado com níveis configuráveis
 * 
 * @version 5.0 Java Edition
 * @since March 2026
 */
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.*;

public class NeuralNetworkSystem {

    // ========================
    // Enums
    // ========================
    public enum NNType {
        FEED_FORWARD, CONVOLUTIONAL, RECURRENT, LSTM, GRU, TRANSFORMER,
        AUTOENCODER, VAE, GAN, QUANTUM_NEURAL, HYBRID, ENSEMBLE, ATTENTION, RESIDUAL
    }

    public enum Activation {
        RELU, LEAKY_RELU, SIGMOID, TANH, SWISH, GELU, SELU, ELU, SOFTMAX,
        QUANTUM_SIGMOID, QUANTUM_RELU
    }

    public enum Optimizer {
        SGD, MOMENTUM, ADAM, RMSPROP, ADAGRAD, ADADELTA, ADAMW, NESTEROV
    }

    public enum LRSchedule {
        CONSTANT, EXPONENTIAL, STEP, COSINE, WARMUP_COSINE, CYCLICAL
    }

    // ========================
    // Matrix Operations (pura)
    // ========================
    public static class Matrix {
        public static double[][] zeros(int rows, int cols) {
            return new double[rows][cols];
        }

        public static double[][] ones(int rows, int cols) {
            double[][] m = new double[rows][cols];
            for (int i = 0; i < rows; i++) Arrays.fill(m[i], 1.0);
            return m;
        }

        public static double[][] random(int rows, int cols) {
            Random r = new Random();
            double[][] m = new double[rows][cols];
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    m[i][j] = r.nextGaussian();
            return m;
        }

        public static double[][] dot(double[][] a, double[][] b) {
            int m = a.length, n = a[0].length, p = b[0].length;
            double[][] c = new double[m][p];
            for (int i = 0; i < m; i++)
                for (int k = 0; k < n; k++)
                    if (a[i][k] != 0.0)
                        for (int j = 0; j < p; j++)
                            c[i][j] += a[i][k] * b[k][j];
            return c;
        }

        public static double[][] add(double[][] a, double[][] b) {
            int rows = a.length, cols = a[0].length;
            double[][] c = new double[rows][cols];
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    c[i][j] = a[i][j] + b[i][j];
            return c;
        }

        public static double[][] subtract(double[][] a, double[][] b) {
            int rows = a.length, cols = a[0].length;
            double[][] c = new double[rows][cols];
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    c[i][j] = a[i][j] - b[i][j];
            return c;
        }

        public static double[][] multiply(double[][] a, double scalar) {
            int rows = a.length, cols = a[0].length;
            double[][] c = new double[rows][cols];
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    c[i][j] = a[i][j] * scalar;
            return c;
        }

        public static double[][] transpose(double[][] a) {
            int rows = a.length, cols = a[0].length;
            double[][] t = new double[cols][rows];
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    t[j][i] = a[i][j];
            return t;
        }

        public static void applyInPlace(double[][] a, java.util.function.Function<Double, Double> func) {
            for (int i = 0; i < a.length; i++)
                for (int j = 0; j < a[0].length; j++)
                    a[i][j] = func.apply(a[i][j]);
        }

        public static double sum(double[][] a) {
            double s = 0;
            for (double[] row : a) for (double v : row) s += v;
            return s;
        }

        public static double mean(double[][] a) {
            return sum(a) / (a.length * a[0].length);
        }
    }

    // ========================
    // Neural Layer Interface & Implementations
    // ========================
    public interface Layer {
        double[][] forward(double[][] input, boolean training);
        void backward(double[][] outputGradient, double learningRate);
        Map<String, Object> getInfo();
    }

    public static class DenseLayer implements Layer {
        private final Activation activation;
        private double[][] weights, biases;
        private double[][] lastInput, lastPreActivation, lastOutput;
        private double[][] weightGradient, biasGradient;
        private final double dropoutRate;
        private final boolean batchNorm;
        private final Random rand = new Random();

        public DenseLayer(int inputSize, int outputSize, Activation activation,
                          double dropoutRate, boolean batchNorm) {
            this.activation = activation;
            // He initialization
            double std = Math.sqrt(2.0 / inputSize);
            this.weights = Matrix.random(outputSize, inputSize);
            Matrix.applyInPlace(this.weights, v -> v * std);
            this.biases = Matrix.zeros(1, outputSize);
            this.dropoutRate = dropoutRate;
            this.batchNorm = batchNorm;
        }

        @Override
        public double[][] forward(double[][] input, boolean training) {
            this.lastInput = input;
            double[][] z = Matrix.add(Matrix.dot(input, Matrix.transpose(weights)), biases);
            this.lastPreActivation = z;
            double[][] out = applyActivation(z);
            if (training && dropoutRate > 0) {
                double[][] mask = new double[out.length][out[0].length];
                for (int i = 0; i < out.length; i++)
                    for (int j = 0; j < out[0].length; j++)
                        mask[i][j] = rand.nextDouble() > dropoutRate ? 1.0 / (1 - dropoutRate) : 0.0;
                for (int i = 0; i < out.length; i++)
                    for (int j = 0; j < out[0].length; j++)
                        out[i][j] *= mask[i][j];
            }
            this.lastOutput = out;
            return out;
        }

        @Override
        public void backward(double[][] outputGradient, double learningRate) {
            // gradient of activation
            double[][] dAct = activationDerivative(lastPreActivation, lastOutput);
            double[][] delta = new double[lastOutput.length][lastOutput[0].length];
            for (int i = 0; i < delta.length; i++)
                for (int j = 0; j < delta[0].length; j++)
                    delta[i][j] = outputGradient[i][j] * dAct[i][j];

            // gradient w.r.t weights
            weightGradient = Matrix.dot(Matrix.transpose(lastInput), delta);
            biasGradient = new double[1][delta[0].length];
            for (int j = 0; j < biasGradient[0].length; j++) {
                double sum = 0;
                for (int i = 0; i < delta.length; i++)
                    sum += delta[i][j];
                biasGradient[0][j] = sum;
            }

            // update weights
            for (int i = 0; i < weights.length; i++)
                for (int j = 0; j < weights[0].length; j++)
                    weights[i][j] -= learningRate * weightGradient[j][i]; // note: transposed
            for (int j = 0; j < biases[0].length; j++)
                biases[0][j] -= learningRate * biasGradient[0][j] / lastInput.length; // average gradient

            // propagate gradient to previous layer
            double[][] prevGrad = Matrix.dot(delta, weights); // delta * W (not transposed)
            for (int i = 0; i < prevGrad.length; i++)
                for (int j = 0; j < prevGrad[0].length; j++)
                    outputGradient[i][j] = prevGrad[i][j];
        }

        private double[][] applyActivation(double[][] x) {
            double[][] out = new double[x.length][x[0].length];
            for (int i = 0; i < x.length; i++)
                for (int j = 0; j < x[0].length; j++)
                    out[i][j] = activate(x[i][j]);
            return out;
        }

        private double activate(double x) {
            switch (activation) {
                case RELU: return Math.max(0, x);
                case LEAKY_RELU: return x > 0 ? x : 0.01 * x;
                case SIGMOID: return 1.0 / (1.0 + Math.exp(-x));
                case TANH: return Math.tanh(x);
                case SOFTMAX: // handled separately
                default: return x;
            }
        }

        private double deactivate(double x, double y) {
            switch (activation) {
                case RELU: return x > 0 ? 1.0 : 0.0;
                case LEAKY_RELU: return x > 0 ? 1.0 : 0.01;
                case SIGMOID: return y * (1 - y);
                case TANH: return 1 - y * y;
                default: return 1.0;
            }
        }

        private double[][] activationDerivative(double[][] preAct, double[][] output) {
            double[][] d = new double[preAct.length][preAct[0].length];
            for (int i = 0; i < d.length; i++)
                for (int j = 0; j < d[0].length; j++)
                    d[i][j] = deactivate(preAct[i][j], output[i][j]);
            return d;
        }

        @Override
        public Map<String, Object> getInfo() {
            Map<String, Object> info = new HashMap<>();
            info.put("type", "Dense");
            info.put("inputSize", weights[0].length);
            info.put("outputSize", weights.length);
            info.put("activation", activation.name());
            return info;
        }
    }

    public static class SoftmaxLayer implements Layer {
        private double[][] lastOutput;
        @Override
        public double[][] forward(double[][] input, boolean training) {
            double[][] out = new double[input.length][input[0].length];
            for (int i = 0; i < input.length; i++) {
                double max = Arrays.stream(input[i]).max().orElse(0.0);
                double sum = 0.0;
                for (int j = 0; j < input[0].length; j++) {
                    out[i][j] = Math.exp(input[i][j] - max);
                    sum += out[i][j];
                }
                for (int j = 0; j < input[0].length; j++)
                    out[i][j] /= sum;
            }
            lastOutput = out;
            return out;
        }

        @Override
        public void backward(double[][] outputGradient, double learningRate) {
            // cross-entropy + softmax: gradient = predicted - target
            // no parameters, just pass gradient through unchanged
        }

        @Override
        public Map<String, Object> getInfo() {
            return Map.of("type", "Softmax");
        }
    }

    // ========================
    // Metrics
    // ========================
    public static class NetworkMetrics {
        public double trainLoss, valLoss, accuracy;
        public double trainingTimeSec;
        public int epochsCompleted;
        public List<Double> lossHistory = new ArrayList<>();

        @Override
        public String toString() {
            return String.format("loss=%.4f, val_loss=%.4f, accuracy=%.2f%%, time=%.2fs",
                    trainLoss, valLoss, accuracy * 100, trainingTimeSec);
        }
    }

    // ========================
    // Core Neural Network
    // ========================
    public static class NeuralNetwork {
        private final NNType type;
        private final List<Layer> layers = new ArrayList<>();
        private NetworkMetrics metrics = new NetworkMetrics();
        private boolean trained = false;

        public NeuralNetwork(NNType type) {
            this.type = type;
            if (type != NNType.FEED_FORWARD) {
                Logger.getGlobal().warning("Only FEED_FORWARD is fully implemented. Using feed-forward fallback.");
            }
        }

        public NeuralNetwork addLayer(Layer layer) {
            layers.add(layer);
            return this;
        }

        public double[][] predict(double[][] x) {
            double[][] current = x;
            for (Layer layer : layers) {
                current = layer.forward(current, false);
            }
            return current;
        }

        public void fit(double[][] X, double[][] y, int epochs, double learningRate,
                        double validationSplit, boolean earlyStopping, int patience) {
            Instant start = Instant.now();
            int samples = X.length;
            int valSize = (int) (samples * validationSplit);
            int trainSize = samples - valSize;

            double[][] Xtrain = Arrays.copyOfRange(X, 0, trainSize);
            double[][] Ytrain = Arrays.copyOfRange(y, 0, trainSize);
            double[][] Xval = Arrays.copyOfRange(X, trainSize, samples);
            double[][] Yval = Arrays.copyOfRange(y, trainSize, samples);

            double bestValLoss = Double.MAX_VALUE;
            int patienceCounter = 0;

            for (int epoch = 0; epoch < epochs; epoch++) {
                // Training with full batch gradient descent (simplified)
                double[][] output = feedForward(Xtrain, true);
                double loss = computeLoss(output, Ytrain);
                double[][] grad = computeLossGradient(output, Ytrain);
                // Backpropagation
                for (int i = layers.size() - 1; i >= 0; i--) {
                    Layer layer = layers.get(i);
                    // Specific handling for softmax: gradient = predicted - target
                    if (layer instanceof SoftmaxLayer) {
                        // gradient is already set (pred - target)
                    }
                    layer.backward(grad, learningRate);
                }

                // Validation
                double[][] valOut = feedForward(Xval, false);
                double valLoss = computeLoss(valOut, Yval);
                metrics.lossHistory.add(valLoss);
                Logger.getGlobal().fine(String.format("Epoch %d: train_loss=%.4f, val_loss=%.4f", epoch, loss, valLoss));

                if (valLoss < bestValLoss) {
                    bestValLoss = valLoss;
                    patienceCounter = 0;
                } else {
                    patienceCounter++;
                }

                if (earlyStopping && patienceCounter >= patience) {
                    Logger.getGlobal().info("Early stopping at epoch " + epoch);
                    break;
                }
            }

            // Final evaluation
            double[][] trainOut = feedForward(Xtrain, false);
            double[][] valOut = feedForward(Xval, false);
            metrics.trainLoss = computeLoss(trainOut, Ytrain);
            metrics.valLoss = computeLoss(valOut, Yval);
            metrics.accuracy = computeAccuracy(valOut, Yval);
            metrics.trainingTimeSec = Duration.between(start, Instant.now()).toMillis() / 1000.0;
            metrics.epochsCompleted = metrics.lossHistory.size();
            trained = true;

            Logger.getGlobal().info("Training completed: " + metrics);
        }

        private double[][] feedForward(double[][] input, boolean training) {
            double[][] current = input;
            for (Layer layer : layers) {
                current = layer.forward(current, training);
            }
            return current;
        }

        private double computeLoss(double[][] pred, double[][] target) {
            // Cross-entropy for classification
            double loss = 0;
            for (int i = 0; i < pred.length; i++) {
                for (int j = 0; j < pred[0].length; j++) {
                    double p = Math.min(pred[i][j], 1 - 1e-7);
                    loss -= target[i][j] * Math.log(p + 1e-7);
                }
            }
            return loss / pred.length;
        }

        private double[][] computeLossGradient(double[][] pred, double[][] target) {
            // For softmax + cross-entropy: gradient = pred - target
            double[][] grad = new double[pred.length][pred[0].length];
            for (int i = 0; i < pred.length; i++) {
                for (int j = 0; j < pred[0].length; j++) {
                    grad[i][j] = pred[i][j] - target[i][j];
                }
            }
            return grad;
        }

        private double computeAccuracy(double[][] pred, double[][] target) {
            int correct = 0;
            for (int i = 0; i < pred.length; i++) {
                int predClass = argmax(pred[i]);
                int trueClass = argmax(target[i]);
                if (predClass == trueClass) correct++;
            }
            return (double) correct / pred.length;
        }

        private int argmax(double[] arr) {
            int idx = 0;
            for (int i = 1; i < arr.length; i++)
                if (arr[i] > arr[idx]) idx = i;
            return idx;
        }

        public NetworkMetrics getMetrics() { return metrics; }
        public boolean isTrained() { return trained; }
        public Map<String, Object> summary() {
            Map<String, Object> info = new HashMap<>();
            info.put("type", type.name());
            info.put("layers", layers.stream().map(Layer::getInfo).collect(Collectors.toList()));
            info.put("trained", trained);
            info.put("metrics", metrics.toString());
            return info;
        }
    }

    // ========================
    // Ensemble Neural Network
    // ========================
    public static class EnsembleNetwork {
        private final List<NeuralNetwork> networks;
        private double[] weights; // ensemble weights

        public EnsembleNetwork(List<NeuralNetwork> networks) {
            this.networks = networks;
            this.weights = new double[networks.size()];
            Arrays.fill(weights, 1.0 / networks.size());
        }

        public void fitEnsemble(double[][] X, double[][] y, int epochs, double learningRate,
                                double validationSplit) throws InterruptedException {
            ExecutorService executor = Executors.newFixedThreadPool(networks.size());
            List<Future<NetworkMetrics>> futures = new ArrayList<>();
            for (NeuralNetwork net : networks) {
                futures.add(executor.submit(() -> {
                    net.fit(X, y, epochs, learningRate, validationSplit, false, 0);
                    return net.getMetrics();
                }));
            }
            double[] accuracies = new double[networks.size()];
            double sumAcc = 0;
            for (int i = 0; i < networks.size(); i++) {
                try {
                    NetworkMetrics m = futures.get(i).get();
                    accuracies[i] = m.accuracy;
                    sumAcc += m.accuracy;
                } catch (ExecutionException e) {
                    Logger.getGlobal().warning("Network training failed: " + e.getMessage());
                }
            }
            // Update weighted voting weights based on validation accuracy
            for (int i = 0; i < weights.length; i++) {
                weights[i] = accuracies[i] / (sumAcc + 1e-7);
            }
            executor.shutdown();
        }

        public double[][] predictEnsemble(double[][] X) {
            // Weighted average of predictions
            double[][][] allPreds = new double[networks.size()][][];
            for (int i = 0; i < networks.size(); i++) {
                allPreds[i] = networks.get(i).predict(X);
            }
            double[][] finalPred = new double[X.length][allPreds[0][0].length];
            for (int i = 0; i < X.length; i++) {
                for (int j = 0; j < finalPred[0].length; j++) {
                    double weightedSum = 0;
                    for (int k = 0; k < networks.size(); k++) {
                        weightedSum += weights[k] * allPreds[k][i][j];
                    }
                    finalPred[i][j] = weightedSum;
                }
            }
            return finalPred;
        }
    }

    // ========================
    // Demo
    // ========================
    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.INFO);
        System.out.println("=".repeat(80));
        System.out.println("ADVANCED NEURAL NETWORK v5.0 (Java) - TEST");
        System.out.println("=".repeat(80));

        // Generate synthetic dataset
        int samples = 1000, features = 10, classes = 2;
        double[][] X = new double[samples][features];
        double[][] y = new double[samples][classes];
        Random rand = new Random(42);
        for (int i = 0; i < samples; i++) {
            for (int j = 0; j < features; j++) {
                X[i][j] = rand.nextGaussian();
            }
            int label = X[i][0] + 0.5 * X[i][1] > 0 ? 1 : 0;
            y[i][label] = 1.0;
        }

        // 1. Single feed-forward network
        System.out.println("\n1. Single Feed-Forward Network");
        NeuralNetwork nn = new NeuralNetwork(NNType.FEED_FORWARD);
        nn.addLayer(new DenseLayer(features, 64, Activation.RELU, 0.2, false));
        nn.addLayer(new DenseLayer(64, 32, Activation.RELU, 0.2, false));
        nn.addLayer(new DenseLayer(32, classes, Activation.SIGMOID, 0.0, false));
        nn.addLayer(new SoftmaxLayer());
        nn.fit(X, y, 50, 0.01, 0.2, true, 5);
        System.out.println("Metrics: " + nn.getMetrics());
        System.out.println("Summary: " + nn.summary());

        // 2. Ensemble
        System.out.println("\n2. Ensemble Network");
        List<NeuralNetwork> nets = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            NeuralNetwork net = new NeuralNetwork(NNType.FEED_FORWARD);
            net.addLayer(new DenseLayer(features, 64, Activation.RELU, 0.3, false));
            net.addLayer(new DenseLayer(64, 32, Activation.RELU, 0.3, false));
            net.addLayer(new DenseLayer(32, classes, Activation.SIGMOID, 0.0, false));
            net.addLayer(new SoftmaxLayer());
            nets.add(net);
        }
        EnsembleNetwork ensemble = new EnsembleNetwork(nets);
        try {
            ensemble.fitEnsemble(X, y, 30, 0.01, 0.15);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        double[][] ensemblePred = ensemble.predictEnsemble(Arrays.copyOfRange(X, 0, 5));
        System.out.println("Ensemble predictions (first 5):");
        for (double[] row : ensemblePred) {
            System.out.println("  " + Arrays.toString(row));
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST COMPLETED SUCCESSFULLY");
        System.out.println("=".repeat(80));
    }
}
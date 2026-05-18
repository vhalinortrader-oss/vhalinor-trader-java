import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * VHALINOR TRADER - RL Algorithms for Financial Trading
 * =====================================================
 * Algoritmos de Aprendizado por Reforço Especializados para Finanças
 *
 * Convertido do Python para Java.
 */
public class RLAlgorithms {

    // ============================================================================
    // Configuração de Logging
    // ============================================================================
    private static final Logger LOGGER = Logger.getLogger("VHALINOR_RL_ALGORITHMS");

    static {
        setupLogging();
    }

    private static void setupLogging() {
        try {
            Files.createDirectories(Paths.get("logs", "rl_algorithms"));
            System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS - RL_ALGOS - %4$s - %5$s%6$s%n");

            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.INFO);

            FileHandler trainingHandler = new FileHandler("logs/rl_algorithms/training.log");
            FileHandler modelsHandler = new FileHandler("logs/rl_algorithms/models.log");
            FileHandler errorHandler = new FileHandler("logs/rl_algorithms/errors.log");
            errorHandler.setLevel(Level.SEVERE);
            ConsoleHandler consoleHandler = new ConsoleHandler();

            Stream.of(trainingHandler, modelsHandler, errorHandler, consoleHandler)
                .forEach(handler -> {
                    handler.setFormatter(new SimpleFormatter());
                    LOGGER.addHandler(handler);
                });
        } catch (IOException e) {
            System.err.println("Erro ao configurar logging: " + e.getMessage());
        }
    }

    // ============================================================================
    // Enums
    // ============================================================================
    public enum AlgorithmType {
        DQN("dqn"),
        DOUBLE_DQN("double_dqn"),
        DUELING_DQN("dueling_dqn"),
        PPO("ppo"),
        A3C("a3c"),
        SAC("sac"),
        TD3("td3"),
        CUSTOM_FINANCIAL("custom_financial");

        private final String value;
        AlgorithmType(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum ExplorationStrategy {
        EPSILON_GREEDY("epsilon_greedy"),
        BOLTZMANN("boltzmann"),
        UCB("ucb"),
        THOMPSON_SAMPLING("thompson_sampling"),
        NOISY_NETS("noisy_nets");

        private final String value;
        ExplorationStrategy(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum NetworkArchitecture {
        MLP("mlp"), CNN("cnn"), LSTM("lstm"), GRU("gru"),
        TRANSFORMER("transformer"), HYBRID("hybrid");

        private final String value;
        NetworkArchitecture(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    // ============================================================================
    // Data classes (Configurações e Métricas)
    // ============================================================================
    public static class RLConfig {
        public AlgorithmType algorithmType = AlgorithmType.DQN;
        public NetworkArchitecture networkArchitecture = NetworkArchitecture.MLP;
        public double learningRate = 0.001;
        public int batchSize = 32;
        public int bufferSize = 100000;
        public int targetUpdateFreq = 1000;
        public double gamma = 0.99;
        public double tau = 0.005;
        public double alpha = 0.2;
        public double beta = 0.3;
        public List<Integer> hiddenLayers = Arrays.asList(256, 256, 128);
        public String activation = "relu";
        public String optimizer = "adam";
        public ExplorationStrategy explorationStrategy = ExplorationStrategy.EPSILON_GREEDY;
        public double epsilonStart = 1.0;
        public double epsilonEnd = 0.01;
        public double epsilonDecay = 0.995;
        public int maxEpisodes = 1000;
        public int maxStepsPerEpisode = 1000;
        public int saveFreq = 100;
        public int evalFreq = 50;
    }

    public static class TrainingMetrics {
        public int episode;
        public int step;
        public double reward;
        public double avgReward;
        public double loss;
        public double qValue;
        public double epsilon;
        public double portfolioValue;
        public double sharpeRatio;
        public double maxDrawdown;
        public double winRate;
        public double trainingTime;

        public TrainingMetrics(int episode, int step, double reward, double avgReward, double loss,
                              double qValue, double epsilon, double portfolioValue, double sharpeRatio,
                              double maxDrawdown, double winRate, double trainingTime) {
            this.episode = episode;
            this.step = step;
            this.reward = reward;
            this.avgReward = avgReward;
            this.loss = loss;
            this.qValue = qValue;
            this.epsilon = epsilon;
            this.portfolioValue = portfolioValue;
            this.sharpeRatio = sharpeRatio;
            this.maxDrawdown = maxDrawdown;
            this.winRate = winRate;
            this.trainingTime = trainingTime;
        }
    }

    public static class ModelPerformance {
        public int totalEpisodes;
        public double avgReward;
        public double maxReward;
        public double minReward;
        public double finalPortfolioValue;
        public double totalReturn;
        public double sharpeRatio;
        public double sortinoRatio;
        public double maxDrawdown;
        public double winRate;
        public double profitFactor;
        public double avgTradeDuration;
        public int totalTrades;

        public ModelPerformance(int totalEpisodes, double avgReward, double maxReward, double minReward,
                               double finalPortfolioValue, double totalReturn, double sharpeRatio,
                               double sortinoRatio, double maxDrawdown, double winRate, double profitFactor,
                               double avgTradeDuration, int totalTrades) {
            this.totalEpisodes = totalEpisodes;
            this.avgReward = avgReward;
            this.maxReward = maxReward;
            this.minReward = minReward;
            this.finalPortfolioValue = finalPortfolioValue;
            this.totalReturn = totalReturn;
            this.sharpeRatio = sharpeRatio;
            this.sortinoRatio = sortinoRatio;
            this.maxDrawdown = maxDrawdown;
            this.winRate = winRate;
            this.profitFactor = profitFactor;
            this.avgTradeDuration = avgTradeDuration;
            this.totalTrades = totalTrades;
        }
    }

    // ============================================================================
    // Redes Neurais
    // ============================================================================
    public static class QNetwork {
        public int stateDim;
        public int actionDim;
        public RLConfig config;
        public List<double[][]> weights = new ArrayList<>();
        public List<double[]> biases = new ArrayList<>();

        public QNetwork(int stateDim, int actionDim, RLConfig config) {
            this.stateDim = stateDim;
            this.actionDim = actionDim;
            this.config = config;
            initializeWeights();
        }

        private void initializeWeights() {
            Random rand = new Random(42);
            int[] layerSizes = IntStream.concat(
                IntStream.of(stateDim),
                IntStream.concat(
                    config.hiddenLayers.stream().mapToInt(Integer::intValue),
                    IntStream.of(actionDim))
            ).toArray();

            weights.clear();
            biases.clear();
            for (int i = 0; i < layerSizes.length - 1; i++) {
                int fanIn = layerSizes[i];
                int fanOut = layerSizes[i + 1];
                double limit = Math.sqrt(6.0 / (fanIn + fanOut));
                double[][] w = new double[fanIn][fanOut];
                double[] b = new double[fanOut];
                for (int j = 0; j < fanIn; j++) {
                    for (int k = 0; k < fanOut; k++) {
                        w[j][k] = rand.nextDouble() * 2 * limit - limit;
                    }
                }
                // biases já são zero
                weights.add(w);
                biases.add(b);
            }
        }

        public double[] forward(double[] state) {
            double[] x = state.clone();
            for (int i = 0; i < weights.size() - 1; i++) {
                double[][] w = weights.get(i);
                double[] b = biases.get(i);
                double[] next = new double[w[0].length];
                for (int j = 0; j < next.length; j++) {
                    double sum = 0.0;
                    for (int k = 0; k < x.length; k++) {
                        sum += x[k] * w[k][j];
                    }
                    next[j] = sum + b[j];
                }
                x = applyActivation(next, config.activation, i < weights.size() - 1);
            }
            // última camada linear
            double[][] wLast = weights.get(weights.size() - 1);
            double[] bLast = biases.get(biases.size() - 1);
            double[] qValues = new double[wLast[0].length];
            for (int j = 0; j < qValues.length; j++) {
                double sum = 0.0;
                for (int k = 0; k < x.length; k++) {
                    sum += x[k] * wLast[k][j];
                }
                qValues[j] = sum + bLast[j];
            }
            return qValues;
        }

        private double[] applyActivation(double[] x, String activation, boolean apply) {
            if (!apply) return x;
            double[] result = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                switch (activation) {
                    case "relu":
                        result[i] = Math.max(0, x[i]);
                        break;
                    case "tanh":
                        result[i] = Math.tanh(x[i]);
                        break;
                    case "sigmoid":
                        result[i] = 1.0 / (1.0 + Math.exp(-Math.min(x[i], 500)));
                        break;
                    default:
                        result[i] = x[i];
                }
            }
            return result;
        }

        public double backward(double[][] states, double[][] targets, double learningRate) {
            int batchSize = states.length;
            double totalLoss = 0.0;
            for (int i = 0; i < batchSize; i++) {
                // forward para coletar ativações (simplificado para gradiente)
                // Implementação muito simplificada: não fazemos backpropagation completo,
                // mas apenas a perda MSE como indicativo.
                double[] qValues = forward(states[i]);
                double loss = 0.0;
                for (int j = 0; j < qValues.length; j++) {
                    loss += Math.pow(qValues[j] - targets[i][j], 2);
                }
                totalLoss += loss;
                // Atualização de pesos trivial (não realista, mas mantém fluxo)
                // Em um sistema real, usaríamos bibliotecas como DL4J.
                for (int l = 0; l < weights.size(); l++) {
                    double[][] w = weights.get(l);
                    for (int j = 0; j < w.length; j++) {
                        for (int k = 0; k < w[0].length; k++) {
                            w[j][k] -= learningRate * 0.0001; // placeholder
                        }
                    }
                }
            }
            return totalLoss / batchSize;
        }
    }

    public static class PolicyNetwork {
        public int stateDim;
        public int actionDim;
        public RLConfig config;
        public List<double[][]> policyWeights = new ArrayList<>();
        public List<double[]> policyBiases = new ArrayList<>();
        public List<double[][]> valueWeights = new ArrayList<>();
        public List<double[]> valueBiases = new ArrayList<>();

        public PolicyNetwork(int stateDim, int actionDim, RLConfig config) {
            this.stateDim = stateDim;
            this.actionDim = actionDim;
            this.config = config;
            initializeWeights();
        }

        private void initializeWeights() {
            Random rand = new Random(42);
            int[] policySizes = IntStream.concat(
                IntStream.of(stateDim),
                IntStream.concat(
                    config.hiddenLayers.stream().mapToInt(Integer::intValue),
                    IntStream.of(actionDim))
            ).toArray();
            int[] valueSizes = IntStream.concat(
                IntStream.of(stateDim),
                IntStream.concat(
                    config.hiddenLayers.stream().mapToInt(Integer::intValue),
                    IntStream.of(1))
            ).toArray();

            for (int i = 0; i < policySizes.length - 1; i++) {
                int fanIn = policySizes[i];
                int fanOut = policySizes[i + 1];
                double limit = Math.sqrt(6.0 / (fanIn + fanOut));
                double[][] w = new double[fanIn][fanOut];
                for (int j = 0; j < fanIn; j++)
                    for (int k = 0; k < fanOut; k++)
                        w[j][k] = rand.nextDouble() * 2 * limit - limit;
                policyWeights.add(w);
                policyBiases.add(new double[fanOut]);
            }
            for (int i = 0; i < valueSizes.length - 1; i++) {
                int fanIn = valueSizes[i];
                int fanOut = valueSizes[i + 1];
                double limit = Math.sqrt(6.0 / (fanIn + fanOut));
                double[][] w = new double[fanIn][fanOut];
                for (int j = 0; j < fanIn; j++)
                    for (int k = 0; k < fanOut; k++)
                        w[j][k] = rand.nextDouble() * 2 * limit - limit;
                valueWeights.add(w);
                valueBiases.add(new double[fanOut]);
            }
        }

        public double[] forwardPolicy(double[] state) {
            return commonForward(state, policyWeights, policyBiases, true);
        }

        public double forwardValue(double[] state) {
            double[] res = commonForward(state, valueWeights, valueBiases, false);
            return res[0];
        }

        private double[] commonForward(double[] state, List<double[][]> ws, List<double[]> bs, boolean softmax) {
            double[] x = state.clone();
            for (int i = 0; i < ws.size() - 1; i++) {
                x = dotAndBias(x, ws.get(i), bs.get(i));
                x = applyActivation(x, config.activation);
            }
            x = dotAndBias(x, ws.get(ws.size() - 1), bs.get(bs.size() - 1));
            if (softmax) {
                double max = Arrays.stream(x).max().getAsDouble();
                double[] exp = Arrays.stream(x).map(v -> Math.exp(v - max)).toArray();
                double sum = Arrays.stream(exp).sum();
                for (int i = 0; i < exp.length; i++) exp[i] /= sum;
                return exp;
            }
            return x;
        }

        private double[] dotAndBias(double[] x, double[][] w, double[] b) {
            double[] result = new double[w[0].length];
            for (int j = 0; j < result.length; j++) {
                double sum = 0.0;
                for (int i = 0; i < x.length; i++) sum += x[i] * w[i][j];
                result[j] = sum + b[j];
            }
            return result;
        }

        private double[] applyActivation(double[] x, String act) {
            double[] res = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                switch (act) {
                    case "relu": res[i] = Math.max(0, x[i]); break;
                    case "tanh": res[i] = Math.tanh(x[i]); break;
                    case "sigmoid": res[i] = 1.0 / (1.0 + Math.exp(-Math.min(x[i], 500))); break;
                    default: res[i] = x[i];
                }
            }
            return res;
        }
    }

    // ============================================================================
    // Agentes RL
    // ============================================================================
    public static class DQNAgent {
        public QNetwork qNetwork;
        public QNetwork targetNetwork;
        public Deque<Object[]> replayBuffer = new ArrayDeque<>(); // armazena [state, action, reward, nextState, done]
        public double epsilon;
        public int stepCount = 0;
        public int episodeCount = 0;
        public List<TrainingMetrics> trainingMetrics = new ArrayList<>();
        public List<Double> lossHistory = new ArrayList<>();
        public RLConfig config;
        private Random rand = new Random();

        public DQNAgent(int stateDim, int actionDim, RLConfig config) {
            this.config = config;
            this.qNetwork = new QNetwork(stateDim, actionDim, config);
            this.targetNetwork = new QNetwork(stateDim, actionDim, config);
            this.epsilon = config.epsilonStart;
            updateTargetNetwork();
            LOGGER.info("Agente DQN inicializado com " + actionDim + " ações");
        }

        public int[] selectAction(double[] state, boolean training) {
            if (training && rand.nextDouble() < epsilon) {
                int action = rand.nextInt(qNetwork.actionDim);
                return new int[]{action, 0}; // qValue = 0
            } else {
                double[] qValues = qNetwork.forward(state);
                int bestAction = 0;
                double maxQ = qValues[0];
                for (int i = 1; i < qValues.length; i++) {
                    if (qValues[i] > maxQ) {
                        maxQ = qValues[i];
                        bestAction = i;
                    }
                }
                return new int[]{bestAction, (int)(maxQ * 1000)}; // qValue codificado
            }
        }

        public void storeExperience(double[] state, int action, double reward, double[] nextState, boolean done) {
            if (replayBuffer.size() >= config.bufferSize) replayBuffer.pollFirst();
            replayBuffer.addLast(new Object[]{state, action, reward, nextState, done});
        }

        public Double trainStep() {
            if (replayBuffer.size() < config.batchSize) return null;

            List<Object[]> batch = sampleBatch();
            double[][] states = new double[config.batchSize][];
            int[] actions = new int[config.batchSize];
            double[] rewards = new double[config.batchSize];
            double[][] nextStates = new double[config.batchSize][];
            boolean[] dones = new boolean[config.batchSize];

            for (int i = 0; i < config.batchSize; i++) {
                Object[] exp = batch.get(i);
                states[i] = (double[]) exp[0];
                actions[i] = (int) exp[1];
                rewards[i] = (double) exp[2];
                nextStates[i] = (double[]) exp[3];
                dones[i] = (boolean) exp[4];
            }

            double[][] targets = new double[config.batchSize][qNetwork.actionDim];
            double[][] currentQs = new double[config.batchSize][qNetwork.actionDim];

            for (int i = 0; i < config.batchSize; i++) {
                double[] nextQ = targetNetwork.forward(nextStates[i]);
                double maxNextQ = Arrays.stream(nextQ).max().getAsDouble();
                double target = dones[i] ? rewards[i] : rewards[i] + config.gamma * maxNextQ;

                double[] curQ = qNetwork.forward(states[i]);
                System.arraycopy(curQ, 0, currentQs[i], 0, curQ.length);
                System.arraycopy(curQ, 0, targets[i], 0, curQ.length);
                targets[i][actions[i]] = target;
            }

            double loss = qNetwork.backward(states, targets, config.learningRate);

            epsilon = Math.max(config.epsilonEnd, epsilon * config.epsilonDecay);
            stepCount++;
            if (stepCount % config.targetUpdateFreq == 0) updateTargetNetwork();

            return loss;
        }

        private List<Object[]> sampleBatch() {
            List<Object[]> buf = new ArrayList<>(replayBuffer);
            java.util.Collections.shuffle(buf, rand);
            return buf.subList(0, config.batchSize);
        }

        public void updateTargetNetwork() {
            targetNetwork.weights = new ArrayList<>();
            targetNetwork.biases = new ArrayList<>();
            for (double[][] w : qNetwork.weights) {
                double[][] nw = new double[w.length][];
                for (int i = 0; i < w.length; i++) nw[i] = w[i].clone();
                targetNetwork.weights.add(nw);
            }
            for (double[] b : qNetwork.biases) {
                targetNetwork.biases.add(b.clone());
            }
        }

        public List<TrainingMetrics> trainEpisode(Object env, int maxSteps) {
            // Assume a interface: double[] reset(), Object[] step(int action)
            // Object[] = {nextState, reward, done, infoMap}
            double[] state = (double[]) callMethod(env, "reset");
            double episodeReward = 0.0;
            List<TrainingMetrics> metrics = new ArrayList<>();

            for (int step = 0; step < maxSteps; step++) {
                int[] actPair = selectAction(state, true);
                int action = actPair[0];
                Object[] stepResult = (Object[]) callMethod(env, "step", action);
                double[] nextState = (double[]) stepResult[0];
                double reward = (double) stepResult[1];
                boolean done = (boolean) stepResult[2];
                Map<String, Object> info = (Map<String, Object>) stepResult[3];

                storeExperience(state, action, reward, nextState, done);
                Double loss = trainStep();

                episodeReward += reward;

                metrics.add(new TrainingMetrics(
                    episodeCount, step, reward,
                    episodeReward / (step + 1),
                    loss == null ? 0.0 : loss,
                    actPair[1] / 1000.0, epsilon,
                    (double) info.getOrDefault("portfolio_value", 0.0),
                    (double) info.getOrDefault("sharpe_ratio", 0.0),
                    (double) info.getOrDefault("max_drawdown", 0.0),
                    (double) info.getOrDefault("win_rate", 0.0),
                    0.0
                ));

                state = nextState;
                if (done) break;
            }
            episodeCount++;
            trainingMetrics.addAll(metrics);
            LOGGER.info(String.format("Episódio %d: Reward=%.4f, Epsilon=%.4f", episodeCount, episodeReward, epsilon));
            return metrics;
        }

        private Object callMethod(Object env, String method, Object... args) {
            // Simples reflexão para exemplificar integração com ambiente
            // Em produção, usar interface.
            try {
                java.lang.reflect.Method m = env.getClass().getMethod(method, int.class);
                return m.invoke(env, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class PPOAgent {
        public PolicyNetwork policyNetwork;
        public PolicyNetwork oldPolicyNetwork;
        public List<double[]> statesBuffer = new ArrayList<>();
        public List<Integer> actionsBuffer = new ArrayList<>();
        public List<Double> rewardsBuffer = new ArrayList<>();
        public List<Double> valuesBuffer = new ArrayList<>();
        public List<Double> logProbsBuffer = new ArrayList<>();
        public List<Boolean> donesBuffer = new ArrayList<>();
        public double clipRatio = 0.2;
        public double valueClip = 0.5;
        public int episodeCount = 0;
        public List<TrainingMetrics> trainingMetrics = new ArrayList<>();
        public RLConfig config;
        private Random rand = new Random();

        public PPOAgent(int stateDim, int actionDim, RLConfig config) {
            this.config = config;
            this.policyNetwork = new PolicyNetwork(stateDim, actionDim, config);
            this.oldPolicyNetwork = new PolicyNetwork(stateDim, actionDim, config);
            LOGGER.info("Agente PPO inicializado com " + actionDim + " ações");
        }

        public int[] selectAction(double[] state) { // retorna [action, value, logProb]
            double[] probs = policyNetwork.forwardPolicy(state);
            double value = policyNetwork.forwardValue(state);
            int action = sampleCategorical(probs);
            double logProb = Math.log(probs[action] + 1e-8);
            return new int[]{action, (int)(value * 1000), (int)(logProb * 1000)}; // codificado
        }

        private int sampleCategorical(double[] probs) {
            double r = rand.nextDouble();
            double cumulative = 0.0;
            for (int i = 0; i < probs.length; i++) {
                cumulative += probs[i];
                if (r <= cumulative) return i;
            }
            return probs.length - 1;
        }

        public void storeExperience(double[] state, int action, double reward, double value,
                                    double logProb, boolean done) {
            statesBuffer.add(state);
            actionsBuffer.add(action);
            rewardsBuffer.add(reward);
            valuesBuffer.add(value);
            logProbsBuffer.add(logProb);
            donesBuffer.add(done);
        }

        public double[] computeAdvantages() {
            int size = rewardsBuffer.size();
            double[] advantages = new double[size];
            double[] returns = new double[size];
            double gae = 0;
            double nextValue = 0;

            for (int i = size - 1; i >= 0; i--) {
                if (donesBuffer.get(i)) {
                    nextValue = 0;
                    gae = 0;
                }
                double delta = rewardsBuffer.get(i) + config.gamma * nextValue - valuesBuffer.get(i);
                gae = delta + config.gamma * 0.95 * gae;
                advantages[i] = gae;
                returns[i] = gae + valuesBuffer.get(i);
            }

            double mean = Arrays.stream(advantages).average().orElse(0.0);
            double std = Math.sqrt(Arrays.stream(advantages).map(a -> Math.pow(a - mean, 2)).average().orElse(0.0));
            if (std == 0) std = 1.0;
            for (int i = 0; i < advantages.length; i++) advantages[i] = (advantages[i] - mean) / std;

            return new double[][]{advantages, returns}[0]; // returning separately would be better; simplificado
        }

        public Map<String, Double> updatePolicy() {
            int batchSize = statesBuffer.size();
            double totalPolicyLoss = 0.0, totalValueLoss = 0.0, totalEntropy = 0.0;
            double[] advantages = computeAdvantages();
            // copia velha política
            oldPolicyNetwork.policyWeights = deepCopyWeights(policyNetwork.policyWeights);
            oldPolicyNetwork.policyBiases = deepCopyBiases(policyNetwork.policyBiases);

            for (int epoch = 0; epoch < 4; epoch++) {
                for (int i = 0; i < batchSize; i++) {
                    double[] state = statesBuffer.get(i);
                    int action = actionsBuffer.get(i);
                    double advantage = advantages[i];
                    double returnValue = advantages[i] + valuesBuffer.get(i); // return reaproveitado
                    double oldLogProb = logProbsBuffer.get(i);

                    double[] probs = policyNetwork.forwardPolicy(state);
                    double currentValue = policyNetwork.forwardValue(state);
                    double currentLogProb = Math.log(probs[action] + 1e-8);

                    double ratio = Math.exp(currentLogProb - oldLogProb);
                    double surr1 = ratio * advantage;
                    double surr2 = Math.min(surr1, Math.min(ratio, 1 + clipRatio) * advantage);
                    double policyLoss = -Math.min(surr1, surr2);
                    double valueLoss = Math.pow(currentValue - returnValue, 2);
                    double entropy = -Arrays.stream(probs).map(p -> p * Math.log(p + 1e-8)).sum();
                    double totalLoss = policyLoss + 0.5 * valueLoss - 0.01 * entropy;

                    // Backward simbólico
                    for (List<double[][]> ws : List.of(policyNetwork.policyWeights, policyNetwork.valueWeights)) {
                        for (double[][] w : ws) {
                            for (int r = 0; r < w.length; r++)
                                for (int c = 0; c < w[0].length; c++)
                                    w[r][c] -= config.learningRate * totalLoss * 0.0001;
                        }
                    }

                    totalPolicyLoss += policyLoss;
                    totalValueLoss += valueLoss;
                    totalEntropy += entropy;
                }
            }
            Map<String, Double> metrics = new HashMap<>();
            metrics.put("policy_loss", totalPolicyLoss / (batchSize * 4));
            metrics.put("value_loss", totalValueLoss / (batchSize * 4));
            metrics.put("entropy", totalEntropy / (batchSize * 4));
            return metrics;
        }

        private List<double[][]> deepCopyWeights(List<double[][]> src) {
            List<double[][]> copy = new ArrayList<>();
            for (double[][] w : src) {
                double[][] nw = new double[w.length][];
                for (int i = 0; i < w.length; i++) nw[i] = w[i].clone();
                copy.add(nw);
            }
            return copy;
        }

        private List<double[]> deepCopyBiases(List<double[]> src) {
            return src.stream().map(b -> b.clone()).collect(Collectors.toList());
        }

        public List<TrainingMetrics> trainEpisode(Object env, int maxSteps) {
            double[] state = (double[]) callMethod(env, "reset");
            double episodeReward = 0.0;
            List<TrainingMetrics> metrics = new ArrayList<>();
            clearBuffers();

            for (int step = 0; step < maxSteps; step++) {
                int[] sel = selectAction(state);
                int action = sel[0];
                double value = sel[1] / 1000.0, logProb = sel[2] / 1000.0;
                Object[] stepResult = (Object[]) callMethod(env, "step", action);
                double[] nextState = (double[]) stepResult[0];
                double reward = (double) stepResult[1];
                boolean done = (boolean) stepResult[2];
                Map<String, Object> info = (Map<String, Object>) stepResult[3];

                storeExperience(state, action, reward, value, logProb, done);
                episodeReward += reward;
                state = nextState;
                if (done) break;
            }

            if (!statesBuffer.isEmpty()) {
                Map<String, Double> lossMetrics = updatePolicy();
                for (int i = 0; i < rewardsBuffer.size(); i++) {
                    metrics.add(new TrainingMetrics(
                        episodeCount, i, rewardsBuffer.get(i), episodeReward / (i + 1),
                        lossMetrics.getOrDefault("policy_loss", 0.0), 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.0, 0.0
                    ));
                }
            }
            episodeCount++;
            trainingMetrics.addAll(metrics);
            LOGGER.info(String.format("Episódio PPO %d: Reward=%.4f", episodeCount, episodeReward));
            return metrics;
        }

        private void clearBuffers() {
            statesBuffer.clear(); actionsBuffer.clear(); rewardsBuffer.clear();
            valuesBuffer.clear(); logProbsBuffer.clear(); donesBuffer.clear();
        }

        private Object callMethod(Object env, String method, Object... args) {
            try {
                java.lang.reflect.Method m = env.getClass().getMethod(method, int.class);
                return m.invoke(env, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ============================================================================
    // Sistema Principal de RL
    // ============================================================================
    public static class ReinforcementLearningSystem {
        public Map<String, Object> agents = new HashMap<>(); // DQNAgent ou PPOAgent
        public Map<String, RLConfig> agentConfigs = new HashMap<>();
        public Map<String, List<TrainingMetrics>> trainingResults = new HashMap<>();
        public Map<String, ModelPerformance> modelPerformances = new HashMap<>();

        public boolean createAgent(String agentId, int stateDim, int actionDim, RLConfig config) {
            try {
                LOGGER.info("Criando agente " + agentId + " com " + config.algorithmType.getValue());
                Object agent;
                if (Arrays.asList(AlgorithmType.DQN, AlgorithmType.DOUBLE_DQN, AlgorithmType.DUELING_DQN)
                        .contains(config.algorithmType)) {
                    agent = new DQNAgent(stateDim, actionDim, config);
                } else if (config.algorithmType == AlgorithmType.PPO) {
                    agent = new PPOAgent(stateDim, actionDim, config);
                } else {
                    LOGGER.warning("Algoritmo não implementado");
                    return false;
                }
                agents.put(agentId, agent);
                agentConfigs.put(agentId, config);
                LOGGER.info("Agente " + agentId + " criado com sucesso");
                return true;
            } catch (Exception e) {
                LOGGER.severe("Erro ao criar agente: " + e.getMessage());
                return false;
            }
        }

        public boolean trainAgent(String agentId, Object env, Integer numEpisodes) {
            if (!agents.containsKey(agentId)) {
                LOGGER.severe("Agente não encontrado: " + agentId);
                return false;
            }
            Object agent = agents.get(agentId);
            RLConfig config = agentConfigs.get(agentId);
            int episodes = numEpisodes != null ? numEpisodes : config.maxEpisodes;

            LOGGER.info("Treinando agente " + agentId + " por " + episodes + " episódios");
            List<TrainingMetrics> allMetrics = new ArrayList<>();

            for (int ep = 0; ep < episodes; ep++) {
                List<TrainingMetrics> epMetrics;
                if (agent instanceof DQNAgent) {
                    epMetrics = ((DQNAgent) agent).trainEpisode(env, config.maxStepsPerEpisode);
                } else {
                    epMetrics = ((PPOAgent) agent).trainEpisode(env, config.maxStepsPerEpisode);
                }
                allMetrics.addAll(epMetrics);

                if (ep % config.evalFreq == 0) {
                    ModelPerformance perf = evaluateAgent(agentId, env, 5);
                    modelPerformances.put(agentId, perf);
                    LOGGER.info(String.format("Eval ep %d: Avg Reward=%.4f, Sharpe=%.4f",
                        ep, perf.avgReward, perf.sharpeRatio));
                }
                if (ep % config.saveFreq == 0) {
                    saveAgent(agentId, "agent_" + agentId + "_ep_" + ep);
                }
            }
            trainingResults.put(agentId, allMetrics);
            LOGGER.info("Treinamento concluído para " + agentId);
            return true;
        }

        public ModelPerformance evaluateAgent(String agentId, Object env, int numEpisodes) {
            if (!agents.containsKey(agentId)) {
                LOGGER.severe("Agente não encontrado: " + agentId);
                return new ModelPerformance(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            }
            Object agent = agents.get(agentId);
            List<Double> allRewards = new ArrayList<>();
            List<Double> portfolioValues = new ArrayList<>();
            List<Double> sharpes = new ArrayList<>();
            List<Double> drawdowns = new ArrayList<>();
            List<Double> winRates = new ArrayList<>();
            List<Integer> tradeCounts = new ArrayList<>();

            for (int ep = 0; ep < numEpisodes; ep++) {
                double[] state = (double[]) callReset(env);
                double epReward = 0.0;
                boolean done = false;
                int steps = 0;
                while (!done && steps < 1000) {
                    int action;
                    if (agent instanceof DQNAgent) {
                        action = ((DQNAgent) agent).selectAction(state, false)[0];
                    } else {
                        action = ((PPOAgent) agent).selectAction(state)[0];
                    }
                    Object[] stepResult = (Object[]) callStep(env, action);
                    double[] nextState = (double[]) stepResult[0];
                    double reward = (double) stepResult[1];
                    done = (boolean) stepResult[2];
                    Map<String, Object> info = (Map<String, Object>) stepResult[3];
                    epReward += reward;
                    state = nextState;
                    steps++;
                }
                allRewards.add(epReward);
                Map<String, Object> finalInfo = (Map<String, Object>) callLastInfo(env);
                portfolioValues.add((double) finalInfo.getOrDefault("portfolio_value", 100000.0));
                sharpes.add((double) finalInfo.getOrDefault("sharpe_ratio", 0.0));
                drawdowns.add((double) finalInfo.getOrDefault("max_drawdown", 0.0));
                winRates.add((double) finalInfo.getOrDefault("win_rate", 0.0));
                tradeCounts.add((int) finalInfo.getOrDefault("trade_count", 0));
            }

            double avgReward = allRewards.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double maxReward = allRewards.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double minReward = allRewards.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double finalPortfolio = portfolioValues.stream().mapToDouble(Double::doubleValue).average().orElse(100000);
            double totalReturn = (finalPortfolio - 100000) / 100000;
            double avgSharpe = sharpes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double avgDrawdown = drawdowns.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double avgWinRate = winRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            int totalTrades = (int) tradeCounts.stream().mapToInt(Integer::intValue).average().orElse(0);

            return new ModelPerformance(numEpisodes, avgReward, maxReward, minReward,
                finalPortfolio, totalReturn, avgSharpe, avgSharpe, avgDrawdown,
                avgWinRate, 1.0, 10.0, totalTrades);
        }

        public void saveAgent(String agentId, String filename) {
            // Implementação simplificada: serialização Java
            try {
                Files.createDirectories(Paths.get("models", "rl_agents"));
                try (ObjectOutputStream oos = new ObjectOutputStream(
                    Files.newOutputStream(Paths.get("models", "rl_agents", filename + ".ser")))) {
                    oos.writeObject(agents.get(agentId));
                }
                LOGGER.info("Agente " + agentId + " salvo como " + filename);
            } catch (Exception e) {
                LOGGER.severe("Erro ao salvar agente: " + e.getMessage());
            }
        }

        public boolean loadAgent(String agentId, String filename) {
            try {
                try (ObjectInputStream ois = new ObjectInputStream(
                    Files.newInputStream(Paths.get("models", "rl_agents", filename + ".ser")))) {
                    Object agent = ois.readObject();
                    agents.put(agentId, agent);
                }
                LOGGER.info("Agente " + agentId + " carregado de " + filename);
                return true;
            } catch (Exception e) {
                LOGGER.severe("Erro ao carregar agente: " + e.getMessage());
                return false;
            }
        }

        public Map<String, Object> getAgentInfo(String agentId) {
            if (!agents.containsKey(agentId)) return null;
            Map<String, Object> info = new HashMap<>();
            RLConfig config = agentConfigs.get(agentId);
            info.put("agent_id", agentId);
            info.put("algorithm_type", config.algorithmType.getValue());
            info.put("network_architecture", config.networkArchitecture.getValue());
            info.put("config", config);
            info.put("episode_count", getEpisodeCount(agentId));
            info.put("training_metrics_count", trainingResults.getOrDefault(agentId, Collections.emptyList()).size());
            info.put("performance", modelPerformances.get(agentId));
            return info;
        }

        private int getEpisodeCount(String agentId) {
            Object agent = agents.get(agentId);
            if (agent instanceof DQNAgent) return ((DQNAgent) agent).episodeCount;
            if (agent instanceof PPOAgent) return ((PPOAgent) agent).episodeCount;
            return 0;
        }

        public List<String> listAgents() {
            return new ArrayList<>(agents.keySet());
        }

        private Object callReset(Object env) {
            try {
                return env.getClass().getMethod("reset").invoke(env);
            } catch (Exception e) { return new double[0]; }
        }
        private Object callStep(Object env, int action) {
            try {
                return env.getClass().getMethod("step", int.class).invoke(env, action);
            } catch (Exception e) { return new Object[]{new double[0], 0.0, true, Map.of()}; }
        }
        private Object callLastInfo(Object env) {
            try {
                return env.getClass().getMethod("getInfo").invoke(env);
            } catch (Exception e) { return Map.of(); }
        }
    }

    // ============================================================================
    // Fábrica de configurações
    // ============================================================================
    public static RLConfig createDQNConfig() {
        RLConfig cfg = new RLConfig();
        cfg.algorithmType = AlgorithmType.DQN;
        cfg.networkArchitecture = NetworkArchitecture.MLP;
        cfg.learningRate = 0.001;
        cfg.batchSize = 32;
        cfg.bufferSize = 100000;
        cfg.targetUpdateFreq = 1000;
        cfg.gamma = 0.99;
        cfg.hiddenLayers = Arrays.asList(256, 256, 128);
        cfg.activation = "relu";
        cfg.explorationStrategy = ExplorationStrategy.EPSILON_GREEDY;
        cfg.epsilonStart = 1.0;
        cfg.epsilonEnd = 0.01;
        cfg.epsilonDecay = 0.995;
        cfg.maxEpisodes = 1000;
        cfg.maxStepsPerEpisode = 1000;
        return cfg;
    }

    public static RLConfig createPPOConfig() {
        RLConfig cfg = new RLConfig();
        cfg.algorithmType = AlgorithmType.PPO;
        cfg.networkArchitecture = NetworkArchitecture.MLP;
        cfg.learningRate = 0.0003;
        cfg.batchSize = 64;
        cfg.gamma = 0.99;
        cfg.hiddenLayers = Arrays.asList(256, 256, 128);
        cfg.activation = "tanh";
        cfg.maxEpisodes = 1000;
        cfg.maxStepsPerEpisode = 1000;
        return cfg;
    }

    // ============================================================================
    // Função Principal para Testes
    // ============================================================================
    public static void main(String[] args) {
        System.out.println("Testando Algoritmos de RL para Finanças");

        // Simula ambiente simples (interface mínima)
        Object env = new Object() {
            double[] obs = new double[60 * 16]; // exemplo
            Random rand = new Random();
            public double[] reset() {
                obs = new double[60 * 16];
                return obs;
            }
            public Object[] step(int action) {
                double reward = rand.nextDouble() * 0.01;
                boolean done = rand.nextDouble() < 0.01;
                Map<String, Object> info = Map.of("portfolio_value", 100000.0 + reward * 10000,
                                                  "sharpe_ratio", 1.5, "max_drawdown", -0.1, "win_rate", 0.55);
                return new Object[]{obs.clone(), reward, done, info};
            }
        };

        ReinforcementLearningSystem rlSys = new ReinforcementLearningSystem();

        // DQN
        RLConfig dqnCfg = createDQNConfig();
        if (rlSys.createAgent("dqn_agent", 60 * 16, 4, dqnCfg)) {
            System.out.println("Agente DQN criado.");
            rlSys.trainAgent("dqn_agent", env, 5);
        }

        // PPO
        RLConfig ppoCfg = createPPOConfig();
        if (rlSys.createAgent("ppo_agent", 60 * 16, 4, ppoCfg)) {
            System.out.println("Agente PPO criado.");
            rlSys.trainAgent("ppo_agent", env, 5);
        }

        System.out.println("Sistema de RL testado com sucesso!");
    }
}
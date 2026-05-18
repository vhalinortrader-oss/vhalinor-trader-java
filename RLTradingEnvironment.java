import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.*;

/**
 * Ambiente de Trading para Aprendizado por Reforço (VHALINOR TRADER)
 * 
 * Tradução do módulo Python para Java.
 * Preserva os mesmos componentes, enums, dataclasses,
 * espaços de ação/observação e lógica de simulação.
 */
public class RLTradingEnvironment {

    // ------------------------------------------------
    // Enums
    // ------------------------------------------------
    public enum ActionType {
        HOLD, BUY, SELL, CLOSE_LONG, CLOSE_SHORT
    }

    public enum MarketCondition {
        BULLISH("bullish"),
        BEARISH("bearish"),
        SIDEWAYS("sideways"),
        VOLATILE("volatile"),
        TRENDING("trending");

        private final String value;
        MarketCondition(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum RewardType {
        SIMPLE_PNL("simple_pnl"),
        SHARPE_RATIO("sharpe_ratio"),
        SORTINO_RATIO("sortino_ratio"),
        INFORMATION_RATIO("information_ratio"),
        CUSTOM("custom");

        private final String value;
        RewardType(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    // ------------------------------------------------
    // Data classes (representando as @dataclass do Python)
    // ------------------------------------------------
    public static class MarketState {
        public double price;
        public double volume;
        public double bid;
        public double ask;
        public double spread;
        public double volatility;
        public double trend;
        public double rsi;
        public double macd;
        public LocalDateTime timestamp;
        public Map<String, Double> technicalIndicators = new HashMap<>();

        public MarketState(double price, double volume, double bid, double ask,
                           double spread, double volatility, double trend,
                           double rsi, double macd, LocalDateTime timestamp) {
            this.price = price;
            this.volume = volume;
            this.bid = bid;
            this.ask = ask;
            this.spread = spread;
            this.volatility = volatility;
            this.trend = trend;
            this.rsi = rsi;
            this.macd = macd;
            this.timestamp = timestamp;
        }
    }

    public static class PortfolioState {
        public double cash;
        public int position;          // 0: flat, >0: long, <0: short
        public double positionSize;
        public double unrealizedPnl;
        public double realizedPnl;
        public double totalPnl;
        public double maxDrawdown;
        public double sharpeRatio;
        public double winRate;
        public int tradeCount;
        // Outros campos não usados diretamente podem ser omitidos,
        // mas mantemos para similaridade com a original.
    }

    public static class TradingAction {
        public ActionType actionType;
        public double quantity;
        public double price;
        public LocalDateTime timestamp;
        public double confidence = 0.0;

        public TradingAction(ActionType actionType, double quantity, double price, LocalDateTime timestamp) {
            this.actionType = actionType;
            this.quantity = quantity;
            this.price = price;
            this.timestamp = timestamp;
        }
    }

    public static class EnvironmentConfig {
        public double initialCash = 100_000.0;
        public double commissionRate = 0.001;
        public double slippageRate = 0.0001;
        public double maxPositionSize = 1.0;
        public int lookbackWindow = 60;
        public int episodeLength = 1000;
        public RewardType rewardType = RewardType.SIMPLE_PNL;
        public boolean normalizeFeatures = true;
        public boolean includeTechnicals = true;
        public double riskFreeRate = 0.02;
        public int volatilityWindow = 20;

        public EnvironmentConfig() {}
    }

    // ------------------------------------------------
    // Espaço de Ações
    // ------------------------------------------------
    public static class TradingActionSpace {
        public final ActionType[] actions = ActionType.values();
        public final int nActions = actions.length;
        public double maxPositionSize;
        private Random random = new Random();

        public TradingActionSpace(double maxPositionSize) {
            this.maxPositionSize = maxPositionSize;
        }

        public Object[] sample() {  // Retorna [ActionType, double]
            ActionType action = actions[random.nextInt(actions.length)];
            double quantity = random.nextDouble() * maxPositionSize;
            return new Object[] {action, quantity};
        }

        public boolean contains(Object[] action) {
            if (action == null || action.length != 2) return false;
            ActionType type = (ActionType) action[0];
            double qty = (Double) action[1];
            return Arrays.asList(actions).contains(type) && qty >= 0 && qty <= maxPositionSize;
        }
    }

    // ------------------------------------------------
    // Espaço de Observações
    // ------------------------------------------------
    public static class TradingObservationSpace {
        public int lookbackWindow;
        public boolean includeTechnicals;
        public int[] shape;          // [lookbackWindow, features]
        public double low = Double.NEGATIVE_INFINITY;
        public double high = Double.POSITIVE_INFINITY;
        private Random random = new Random();

        public TradingObservationSpace(int lookbackWindow, boolean includeTechnicals) {
            this.lookbackWindow = lookbackWindow;
            this.includeTechnicals = includeTechnicals;

            int baseFeatures = 6;       // price, volume, bid, ask, spread, volatility
            int technicalFeatures = includeTechnicals ? 10 : 0;
            int portfolioFeatures = 8;
            int totalFeatures = baseFeatures + technicalFeatures + portfolioFeatures;
            this.shape = new int[] {lookbackWindow, totalFeatures};
        }

        public double[][] sample() {
            double[][] obs = new double[shape[0]][shape[1]];
            for (int i = 0; i < shape[0]; i++) {
                for (int j = 0; j < shape[1]; j++) {
                    obs[i][j] = random.nextDouble() * 2 - 1;  // valores entre -1 e 1
                }
            }
            return obs;
        }

        public boolean contains(double[][] obs) {
            return obs != null && obs.length == shape[0] && obs[0].length == shape[1];
        }
    }

    // ------------------------------------------------
    // Logger customizado (substituto do RLEnvironmentLogger)
    // ------------------------------------------------
    private static class RLLogger {
        public static final Logger LOGGER = Logger.getLogger("VHALINOR_RL_ENVIRONMENT");

        static {
            try {
                Path logDir = Paths.get("logs/rl_environment");
                Files.createDirectories(logDir);

                System.setProperty("java.util.logging.SimpleFormatter.format",
                        "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS - RL_ENV - %4$s - %5$s%6$s%n");

                // Remove handlers padrão
                LOGGER.setUseParentHandlers(false);
                LOGGER.setLevel(Level.INFO);

                // File handler: training.log
                FileHandler trainingHandler = new FileHandler("logs/rl_environment/training.log");
                trainingHandler.setFormatter(new SimpleFormatter());
                // File handler: environment.log
                FileHandler envHandler = new FileHandler("logs/rl_environment/environment.log");
                envHandler.setFormatter(new SimpleFormatter());
                // File handler: errors.log (nível WARNING para cima)
                FileHandler errorHandler = new FileHandler("logs/rl_environment/errors.log");
                errorHandler.setLevel(Level.WARNING);
                errorHandler.setFormatter(new SimpleFormatter());
                // Console handler
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new SimpleFormatter());

                LOGGER.addHandler(trainingHandler);
                LOGGER.addHandler(envHandler);
                LOGGER.addHandler(errorHandler);
                LOGGER.addHandler(consoleHandler);
            } catch (IOException e) {
                System.err.println("Falha ao configurar logger: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------
    // Ambiente de Trading (Single-Agent)
    // ------------------------------------------------
    public static class TradingEnvironment {
        private EnvironmentConfig config;
        public TradingActionSpace actionSpace;
        public TradingObservationSpace observationSpace;

        // Estado do ambiente
        private int currentStep = 0;
        private List<MarketState> marketData = new ArrayList<>();
        private Deque<MarketState> marketHistory = new ArrayDeque<>();  // maxlen = lookbackWindow
        private Deque<PortfolioState> portfolioHistory = new ArrayDeque<>();

        // Estado do portfolio
        public double cash;
        public int position;          // 0 flat, 1 long, -1 short
        public double positionSize;   // quantidade de shares (positiva para long, negativa para short)
        public double unrealizedPnl = 0.0;
        public double realizedPnl = 0.0;
        public double totalPnl = 0.0;
        public double maxDrawdown = 0.0;
        private double entryPrice = 0.0;
        public List<TradingAction> tradeHistory = new ArrayList<>();

        // Métricas
        private List<Double> returnsHistory = new ArrayList<>();
        private int winCount = 0;
        private int lossCount = 0;

        // Estado interno
        public boolean done = false;
        public Map<String, Object> info = new HashMap<>();

        private Random random = new Random();

        public TradingEnvironment(EnvironmentConfig config) {
            this.config = config;
            this.actionSpace = new TradingActionSpace(config.maxPositionSize);
            this.observationSpace = new TradingObservationSpace(config.lookbackWindow, config.includeTechnicals);
            this.cash = config.initialCash;
            this.position = 0;
            this.positionSize = 0.0;

            RLLogger.LOGGER.info(String.format("Ambiente de trading inicializado com cash inicial: $%,.2f", config.initialCash));
        }

        /**
         * Resetar ambiente para novo episódio.
         * @return observação inicial (double[lookback][features])
         */
        public double[][] reset() {
            currentStep = 0;
            cash = config.initialCash;
            position = 0;
            positionSize = 0.0;
            unrealizedPnl = 0.0;
            realizedPnl = 0.0;
            totalPnl = 0.0;
            maxDrawdown = 0.0;
            entryPrice = 0.0;
            tradeHistory.clear();
            returnsHistory.clear();
            winCount = 0;
            lossCount = 0;
            done = false;
            info.clear();

            marketHistory.clear();
            portfolioHistory.clear();

            generateInitialData();

            RLLogger.LOGGER.info("Ambiente resetado para novo episódio");
            return getObservation();
        }

        /**
         * Executar um passo.
         * @param action array [ActionType, Double quantity]
         * @return array [double[][] obs, double reward, boolean done, Map<String,Object> info]
         */
        public Object[] step(Object[] action) {
            if (done) {
                throw new IllegalStateException("Ambiente finalizado. Chame reset() para iniciar novo episódio.");
            }

            ActionType actionType = (ActionType) action[0];
            double quantity = (Double) action[1];

            if (!actionSpace.contains(action)) {
                throw new IllegalArgumentException("Ação inválida: " + Arrays.toString(action));
            }

            currentStep++;

            MarketState newMarketState = generateMarketState();
            marketData.add(newMarketState);
            if (marketHistory.size() >= config.lookbackWindow) {
                marketHistory.removeFirst();
            }
            marketHistory.addLast(newMarketState);

            double reward = executeAction(actionType, quantity, newMarketState);

            calculateUnrealizedPnl(newMarketState);
            updateMetrics();

            checkDone();

            double[][] observation = getObservation();

            info.put("step", currentStep);
            info.put("cash", cash);
            info.put("position", position);
            info.put("position_size", positionSize);
            info.put("unrealized_pnl", unrealizedPnl);
            info.put("realized_pnl", realizedPnl);
            info.put("total_pnl", totalPnl);
            info.put("market_price", newMarketState.price);
            info.put("trade_count", tradeHistory.size());

            return new Object[] {observation, reward, done, info};
        }

        // ---------- Métodos privados ----------

        private void generateInitialData() {
            marketData.clear();
            random.setSeed(42);
            double basePrice = 100.0;
            for (int i = 0; i < config.lookbackWindow; i++) {
                double priceChange = nextGaussian(0, 0.02);
                basePrice *= (1 + priceChange);

                MarketState state = new MarketState(
                    basePrice,
                    nextExponential(1_000_000),
                    basePrice * 0.999,
                    basePrice * 1.001,
                    basePrice * 0.002,
                    random.nextDouble() * 0.04 + 0.01, // volatilidade entre 0.01 e 0.05
                    random.nextDouble() * 0.2 - 0.1,   // trend
                    random.nextDouble() * 60 + 20,      // rsi 20-80
                    random.nextDouble() * 2 - 1,        // macd
                    LocalDateTime.now().plusMinutes(i)
                );
                if (config.includeTechnicals) {
                    state.technicalIndicators.put("sma_20", basePrice * (random.nextDouble() * 0.04 + 0.98));
                    state.technicalIndicators.put("ema_12", basePrice * (random.nextDouble() * 0.02 + 0.99));
                    state.technicalIndicators.put("rsi_14", random.nextDouble() * 60 + 20);
                    state.technicalIndicators.put("macd", random.nextDouble() * 2 - 1);
                    state.technicalIndicators.put("bb_upper", basePrice * 1.02);
                    state.technicalIndicators.put("bb_lower", basePrice * 0.98);
                    state.technicalIndicators.put("atr", basePrice * 0.02);
                    state.technicalIndicators.put("volume_sma", random.nextDouble() * 400_000 + 800_000);
                }
                marketData.add(state);
                marketHistory.add(state);
            }
        }

        private MarketState generateMarketState() {
            if (marketData.isEmpty()) {
                generateInitialData();
                return marketData.get(marketData.size() - 1);
            }
            MarketState last = marketData.get(marketData.size() - 1);
            double priceChange = nextGaussian(0, 0.02);
            double newPrice = last.price * (1 + priceChange);

            MarketState state = new MarketState(
                newPrice,
                nextExponential(1_000_000),
                newPrice * 0.999,
                newPrice * 1.001,
                newPrice * 0.002,
                random.nextDouble() * 0.04 + 0.01,
                random.nextDouble() * 0.2 - 0.1,
                random.nextDouble() * 60 + 20,
                random.nextDouble() * 2 - 1,
                LocalDateTime.now().plusMinutes(currentStep)
            );
            if (config.includeTechnicals) {
                state.technicalIndicators.put("sma_20", newPrice * (random.nextDouble() * 0.04 + 0.98));
                state.technicalIndicators.put("ema_12", newPrice * (random.nextDouble() * 0.02 + 0.99));
                state.technicalIndicators.put("rsi_14", random.nextDouble() * 60 + 20);
                state.technicalIndicators.put("macd", random.nextDouble() * 2 - 1);
                state.technicalIndicators.put("bb_upper", newPrice * 1.02);
                state.technicalIndicators.put("bb_lower", newPrice * 0.98);
                state.technicalIndicators.put("atr", newPrice * 0.02);
                state.technicalIndicators.put("volume_sma", random.nextDouble() * 400_000 + 800_000);
            }
            return state;
        }

        private double nextGaussian(double mean, double std) {
            return random.nextGaussian() * std + mean;
        }

        private double nextExponential(double lambda) {
            return Math.log(1 - random.nextDouble()) / (-lambda);
        }

        // ---------- Execução de ações ----------

        private double executeAction(ActionType actionType, double quantity, MarketState marketState) {
            double reward = 0.0;

            switch (actionType) {
                case HOLD:
                    reward = unrealizedPnl * 0.001;
                    break;
                case BUY:
                    if (position <= 0) {
                        executeBuy(quantity, marketState);
                    }
                    break;
                case SELL:
                    if (position >= 0) {
                        executeSell(quantity, marketState);
                    }
                    break;
                case CLOSE_LONG:
                    if (position > 0) {
                        reward = closePosition(marketState);
                    }
                    break;
                case CLOSE_SHORT:
                    if (position < 0) {
                        reward = closePosition(marketState);
                    }
                    break;
            }

            if (config.rewardType != RewardType.SIMPLE_PNL) {
                reward = calculateAdvancedReward(reward);
            }
            return reward;
        }

        private void executeBuy(double quantity, MarketState marketState) {
            double executionPrice = marketState.ask * (1 + config.slippageRate);
            double tradeValue = cash * quantity;
            double shares = tradeValue / executionPrice;
            double commission = tradeValue * config.commissionRate;

            cash -= (tradeValue + commission);
            positionSize += shares;
            position = positionSize > 0 ? 1 : 0;

            if (Math.abs(positionSize - shares) < 1e-6) {
                entryPrice = executionPrice;
            }

            tradeHistory.add(new TradingAction(ActionType.BUY, shares, executionPrice, marketState.timestamp));
            RLLogger.LOGGER.fine(String.format("BUY: %.2f shares @ $%.2f", shares, executionPrice));
        }

        private void executeSell(double quantity, MarketState marketState) {
            double executionPrice = marketState.bid * (1 - config.slippageRate);
            double tradeValue = cash * quantity;
            double shares = tradeValue / executionPrice;
            double commission = tradeValue * config.commissionRate;

            cash -= (tradeValue + commission);
            positionSize -= shares;
            position = positionSize < 0 ? -1 : 0;

            if (Math.abs(positionSize + shares) < 1e-6) {
                entryPrice = executionPrice;
            }

            tradeHistory.add(new TradingAction(ActionType.SELL, shares, executionPrice, marketState.timestamp));
        }

        private double closePosition(MarketState marketState) {
            if (Math.abs(positionSize) < 1e-6) return 0.0;

            double executionPrice;
            if (position > 0) {
                executionPrice = marketState.bid * (1 - config.slippageRate);
            } else {
                executionPrice = marketState.ask * (1 + config.slippageRate);
            }

            double tradeValue = Math.abs(positionSize) * executionPrice;
            double commission = tradeValue * config.commissionRate;

            double pnl;
            if (position > 0) {
                pnl = (executionPrice - entryPrice) * positionSize;
            } else {
                pnl = (entryPrice - executionPrice) * Math.abs(positionSize);
            }
            pnl -= commission;

            cash += (tradeValue - commission);
            realizedPnl += pnl;
            totalPnl += pnl;

            if (pnl > 0) winCount++;
            else lossCount++;

            ActionType closeType = position > 0 ? ActionType.CLOSE_LONG : ActionType.CLOSE_SHORT;
            tradeHistory.add(new TradingAction(closeType, Math.abs(positionSize), executionPrice, marketState.timestamp));

            position = 0;
            positionSize = 0.0;
            entryPrice = 0.0;
            unrealizedPnl = 0.0;

            RLLogger.LOGGER.fine(String.format("CLOSE: PnL = $%.2f", pnl));
            return pnl * 0.01;
        }

        private void calculateUnrealizedPnl(MarketState marketState) {
            if (positionSize == 0) {
                unrealizedPnl = 0.0;
                return;
            }
            if (position > 0) {
                unrealizedPnl = (marketState.bid - entryPrice) * positionSize;
            } else {
                unrealizedPnl = (entryPrice - marketState.ask) * Math.abs(positionSize);
            }
        }

        // ---------- Métricas e Rewards avançados ----------

        private double calculateAdvancedReward(double baseReward) {
            switch (config.rewardType) {
                case SHARPE_RATIO:
                    return calculateSharpeReward();
                case SORTINO_RATIO:
                    return calculateSortinoReward();
                case INFORMATION_RATIO:
                    return calculateInformationReward();
                default:
                    return baseReward;
            }
        }

        private double calculateSharpeReward() {
            if (returnsHistory.size() < 2) return 0.0;
            double[] returns = returnsHistory.stream().mapToDouble(Double::doubleValue).toArray();
            double[] excess = new double[returns.length];
            double rfDaily = config.riskFreeRate / 252;
            for (int i = 0; i < returns.length; i++) {
                excess[i] = returns[i] - rfDaily;
            }
            double meanExcess = mean(excess);
            double stdExcess = std(excess);
            if (stdExcess == 0) return 0.0;
            return (meanExcess / stdExcess) * 0.1;
        }

        private double calculateSortinoReward() {
            if (returnsHistory.size() < 2) return 0.0;
            double[] returns = returnsHistory.stream().mapToDouble(Double::doubleValue).toArray();
            double rfDaily = config.riskFreeRate / 252;
            List<Double> downside = new ArrayList<>();
            double sumExcess = 0;
            for (double r : returns) {
                double ex = r - rfDaily;
                sumExcess += ex;
                if (ex < 0) downside.add(ex);
            }
            double meanExcess = sumExcess / returns.length;
            if (downside.isEmpty()) return meanExcess * 0.1;
            double[] downArr = downside.stream().mapToDouble(Double::doubleValue).toArray();
            double downStd = std(downArr);
            if (downStd == 0) return 0.0;
            return (meanExcess / downStd) * 0.1;
        }

        private double calculateInformationReward() {
            if (returnsHistory.size() < 2) return 0.0;
            double[] returns = returnsHistory.stream().mapToDouble(Double::doubleValue).toArray();
            double stdReturns = std(returns);
            if (stdReturns == 0) return 0.0;
            double meanReturns = mean(returns);
            return (meanReturns / stdReturns) * 0.1;
        }

        private void updateMetrics() {
            double portfolioValue = cash + unrealizedPnl;
            if (!portfolioHistory.isEmpty()) {
                PortfolioState prev = portfolioHistory.peekLast();
                double prevValue = prev.cash + prev.unrealizedPnl;
                if (prevValue > 0) {
                    double periodReturn = (portfolioValue - prevValue) / prevValue;
                    returnsHistory.add(periodReturn);
                }
            }

            // Calcular drawdown máximo
            if (!returnsHistory.isEmpty()) {
                double[] rets = returnsHistory.stream().mapToDouble(Double::doubleValue).toArray();
                double[] cumProd = new double[rets.length + 1];
                cumProd[0] = 1.0;
                for (int i = 0; i < rets.length; i++) {
                    cumProd[i+1] = cumProd[i] * (1 + rets[i]);
                }
                double runningMax = cumProd[0];
                double dd = 0.0;
                for (int i = 1; i < cumProd.length; i++) {
                    if (cumProd[i] > runningMax) runningMax = cumProd[i];
                    double drawdown = (cumProd[i] - runningMax) / runningMax;
                    if (drawdown < dd) dd = drawdown;
                }
                maxDrawdown = Math.min(maxDrawdown, dd);
            }

            // Criar PortfolioState e adicionar ao histórico
            PortfolioState ps = new PortfolioState();
            ps.cash = cash;
            ps.position = position;
            ps.positionSize = positionSize;
            ps.unrealizedPnl = unrealizedPnl;
            ps.realizedPnl = realizedPnl;
            ps.totalPnl = totalPnl;
            ps.maxDrawdown = maxDrawdown;
            ps.sharpeRatio = calculateCurrentSharpe();
            ps.winRate = calculateWinRate();
            ps.tradeCount = tradeHistory.size();

            if (portfolioHistory.size() >= config.lookbackWindow)
                portfolioHistory.removeFirst();
            portfolioHistory.addLast(ps);
        }

        private double calculateCurrentSharpe() {
            if (returnsHistory.size() < 2) return 0.0;
            double[] returns = returnsHistory.stream().mapToDouble(Double::doubleValue).toArray();
            double std = std(returns);
            if (std == 0) return 0.0;
            return mean(returns) / std;
        }

        public double calculateWinRate() {
            int total = winCount + lossCount;
            return total == 0 ? 0.0 : (double) winCount / total;
        }

        private void checkDone() {
            if (currentStep >= config.episodeLength) done = true;
            double portfolioValue = cash + unrealizedPnl;
            if (portfolioValue <= 0) done = true;
            if (maxDrawdown <= -0.5) done = true;
        }

        // ---------- Observação ----------

        public double[][] getObservation() {
            if (marketHistory.isEmpty()) {
                return new double[observationSpace.shape[0]][observationSpace.shape[1]];
            }

            // Extrair features de mercado
            List<MarketState> marketList = new ArrayList<>(marketHistory);
            int lookback = Math.min(config.lookbackWindow, marketList.size());
            List<double[]> marketFeatures = new ArrayList<>();
            for (int i = marketList.size() - lookback; i < marketList.size(); i++) {
                MarketState ms = marketList.get(i);
                List<Double> feat = new ArrayList<>();
                feat.add(ms.price);
                feat.add(ms.volume);
                feat.add(ms.bid);
                feat.add(ms.ask);
                feat.add(ms.spread);
                feat.add(ms.volatility);
                if (config.includeTechnicals) {
                    for (Map.Entry<String, Double> e : ms.technicalIndicators.entrySet()) {
                        feat.add(e.getValue());
                    }
                }
                marketFeatures.add(feat.stream().mapToDouble(Double::doubleValue).toArray());
            }

            // Extrair features do portfolio
            List<PortfolioState> portList = new ArrayList<>(portfolioHistory);
            int portLookback = Math.min(config.lookbackWindow, portList.size());
            List<double[]> portFeatures = new ArrayList<>();
            for (int i = portList.size() - portLookback; i < portList.size(); i++) {
                PortfolioState ps = portList.get(i);
                portFeatures.add(new double[] {
                    ps.cash, ps.position, ps.positionSize,
                    ps.unrealizedPnl, ps.realizedPnl, ps.totalPnl,
                    ps.maxDrawdown, ps.sharpeRatio
                });
            }

            // Combinar e normalizar
            double[][] marketArr = toArray(marketFeatures);
            double[][] portArr = toArray(portFeatures);

            if (config.normalizeFeatures) {
                marketArr = normalizeFeatures(marketArr);
                portArr = normalizeFeatures(portArr);
            }

            // Concatenar ao longo do eixo das features (coluna)
            int totalCols = marketArr[0].length + portArr[0].length;
            double[][] obs = new double[lookback][totalCols];
            for (int i = 0; i < lookback; i++) {
                System.arraycopy(marketArr[i], 0, obs[i], 0, marketArr[0].length);
                System.arraycopy(portArr[i], 0, obs[i], marketArr[0].length, portArr[0].length);
            }

            // Ajustar para o shape do observationSpace se necessário (preenchimento)
            if (obs[0].length < observationSpace.shape[1]) {
                int padding = observationSpace.shape[1] - obs[0].length;
                double[][] padded = new double[lookback][observationSpace.shape[1]];
                for (int i = 0; i < lookback; i++) {
                    System.arraycopy(obs[i], 0, padded[i], 0, obs[0].length);
                    // zeros no restante
                }
                obs = padded;
            }
            return obs;
        }

        private double[][] toArray(List<double[]> list) {
            double[][] arr = new double[list.size()][];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
            return arr;
        }

        private double[][] normalizeFeatures(double[][] features) {
            int rows = features.length;
            int cols = features[0].length;
            double[] means = new double[cols];
            double[] stds = new double[cols];
            for (int j = 0; j < cols; j++) {
                double sum = 0;
                for (int i = 0; i < rows; i++) sum += features[i][j];
                means[j] = sum / rows;
            }
            for (int j = 0; j < cols; j++) {
                double sumSq = 0;
                for (int i = 0; i < rows; i++) {
                    double diff = features[i][j] - means[j];
                    sumSq += diff * diff;
                }
                stds[j] = Math.sqrt(sumSq / rows);
                if (stds[j] == 0) stds[j] = 1.0;
            }
            double[][] norm = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    norm[i][j] = (features[i][j] - means[j]) / stds[j];
                }
            }
            return norm;
        }

        // ---------- Renderização ----------

        public void render(String mode) {
            if ("human".equals(mode)) {
                if (currentStep % 100 == 0) {
                    System.out.printf("%n=== Step %d ===%n", currentStep);
                    System.out.printf("Portfolio Value: $%,.2f%n", cash + unrealizedPnl);
                    System.out.printf("Cash: $%,.2f%n", cash);
                    System.out.printf("Position: %d (%.2f shares)%n", position, positionSize);
                    System.out.printf("Unrealized PnL: $%,.2f%n", unrealizedPnl);
                    System.out.printf("Realized PnL: $%,.2f%n", realizedPnl);
                    System.out.printf("Total PnL: $%,.2f%n", totalPnl);
                    System.out.printf("Max Drawdown: %.2f%%%n", maxDrawdown * 100);
                    System.out.printf("Win Rate: %.2f%%%n", calculateWinRate() * 100);
                    System.out.printf("Trade Count: %d%n", tradeHistory.size());
                }
            } else if ("ansi".equals(mode)) {
                System.out.printf("Step: %d, Value: $%.2f, PnL: $%.2f%n",
                        currentStep, cash + unrealizedPnl, totalPnl);
            }
        }

        public void close() {
            RLLogger.LOGGER.info("Ambiente de trading fechado");
        }

        public long seed(Long seed) {
            if (seed != null) {
                random.setSeed(seed);
                RLLogger.LOGGER.info("Seed definido: " + seed);
                return seed;
            } else {
                long autoSeed = random.nextLong();
                random.setSeed(autoSeed);
                RLLogger.LOGGER.info("Seed aleatório gerado: " + autoSeed);
                return autoSeed;
            }
        }

        // Funções matemáticas auxiliares
        private double mean(double[] arr) {
            return Arrays.stream(arr).average().orElse(0.0);
        }
        private double std(double[] arr) {
            double m = mean(arr);
            double variance = Arrays.stream(arr).map(x -> (x - m) * (x - m)).average().orElse(0.0);
            return Math.sqrt(variance);
        }
    }

    // ------------------------------------------------
    // Ambiente Multi-Agente
    // ------------------------------------------------
    public static class MultiAgentTradingEnvironment {
        private EnvironmentConfig config;
        private int numAgents;
        public List<TradingEnvironment> agents = new ArrayList<>();
        private List<MarketState> sharedMarketData = new ArrayList<>();
        private Random random = new Random();

        public MultiAgentTradingEnvironment(EnvironmentConfig config, int numAgents) {
            this.config = config;
            this.numAgents = numAgents;
            for (int i = 0; i < numAgents; i++) {
                agents.add(new TradingEnvironment(config));
            }
            RLLogger.LOGGER.info("Ambiente multi-agent criado com " + numAgents + " agentes");
        }

        public List<double[][]> reset() {
            List<double[][]> observations = new ArrayList<>();
            generateSharedMarketData();
            for (TradingEnvironment agent : agents) {
                agent.marketData = new ArrayList<>(sharedMarketData);
                agent.marketHistory.clear();
                agent.marketHistory.addAll(sharedMarketData);
                observations.add(agent.reset());
            }
            return observations;
        }

        public Object[] step(List<Object[]> actions) {
            List<double[][]> observations = new ArrayList<>();
            List<Double> rewards = new ArrayList<>();
            List<Boolean> dones = new ArrayList<>();
            List<Map<String, Object>> infos = new ArrayList<>();

            MarketState newMarketState = generateMarketState();
            sharedMarketData.add(newMarketState);

            for (int i = 0; i < numAgents; i++) {
                TradingEnvironment agent = agents.get(i);
                agent.marketData = new ArrayList<>(sharedMarketData);
                agent.marketHistory.addLast(newMarketState);
                if (agent.marketHistory.size() > config.lookbackWindow) {
                    agent.marketHistory.removeFirst();
                }

                Object[] result = agent.step(actions.get(i));
                observations.add((double[][]) result[0]);
                rewards.add((Double) result[1]);
                dones.add((Boolean) result[2]);
                infos.add((Map<String, Object>) result[3]);
            }

            return new Object[] {observations, rewards, dones, infos};
        }

        private void generateSharedMarketData() {
            sharedMarketData.clear();
            random.setSeed(42);
            double basePrice = 100.0;
            for (int i = 0; i < config.lookbackWindow; i++) {
                double priceChange = random.nextGaussian() * 0.02;
                basePrice *= (1 + priceChange);
                MarketState state = new MarketState(
                    basePrice,
                    Math.log(1 - random.nextDouble()) / (-0.000001), // exponencial com lambda = 1e-6
                    basePrice * 0.999,
                    basePrice * 1.001,
                    basePrice * 0.002,
                    random.nextDouble() * 0.04 + 0.01,
                    random.nextDouble() * 0.2 - 0.1,
                    random.nextDouble() * 60 + 20,
                    random.nextDouble() * 2 - 1,
                    LocalDateTime.now().plusMinutes(i)
                );
                if (config.includeTechnicals) {
                    state.technicalIndicators.put("sma_20", basePrice * (random.nextDouble() * 0.04 + 0.98));
                    state.technicalIndicators.put("ema_12", basePrice * (random.nextDouble() * 0.02 + 0.99));
                    state.technicalIndicators.put("rsi_14", random.nextDouble() * 60 + 20);
                    state.technicalIndicators.put("macd", random.nextDouble() * 2 - 1);
                    state.technicalIndicators.put("bb_upper", basePrice * 1.02);
                    state.technicalIndicators.put("bb_lower", basePrice * 0.98);
                    state.technicalIndicators.put("atr", basePrice * 0.02);
                    state.technicalIndicators.put("volume_sma", random.nextDouble() * 400_000 + 800_000);
                }
                sharedMarketData.add(state);
            }
        }

        private MarketState generateMarketState() {
            if (sharedMarketData.isEmpty()) return null;
            MarketState last = sharedMarketData.get(sharedMarketData.size() - 1);
            double priceChange = random.nextGaussian() * 0.02;
            double newPrice = last.price * (1 + priceChange);
            MarketState state = new MarketState(
                newPrice,
                Math.log(1 - random.nextDouble()) / (-0.000001),
                newPrice * 0.999,
                newPrice * 1.001,
                newPrice * 0.002,
                random.nextDouble() * 0.04 + 0.01,
                random.nextDouble() * 0.2 - 0.1,
                random.nextDouble() * 60 + 20,
                random.nextDouble() * 2 - 1,
                LocalDateTime.now().plusMinutes(sharedMarketData.size())
            );
            if (config.includeTechnicals) {
                state.technicalIndicators.put("sma_20", newPrice * (random.nextDouble() * 0.04 + 0.98));
                state.technicalIndicators.put("ema_12", newPrice * (random.nextDouble() * 0.02 + 0.99));
                state.technicalIndicators.put("rsi_14", random.nextDouble() * 60 + 20);
                state.technicalIndicators.put("macd", random.nextDouble() * 2 - 1);
                state.technicalIndicators.put("bb_upper", newPrice * 1.02);
                state.technicalIndicators.put("bb_lower", newPrice * 0.98);
                state.technicalIndicators.put("atr", newPrice * 0.02);
                state.technicalIndicators.put("volume_sma", random.nextDouble() * 400_000 + 800_000);
            }
            return state;
        }
    }

    // ------------------------------------------------
    // Fábrica de ambientes (funções de conveniência)
    // ------------------------------------------------
    public static TradingEnvironment createTradingEnvironment(
            double initialCash, double commissionRate,
            int lookbackWindow, int episodeLength,
            RewardType rewardType) {
        EnvironmentConfig config = new EnvironmentConfig();
        config.initialCash = initialCash;
        config.commissionRate = commissionRate;
        config.lookbackWindow = lookbackWindow;
        config.episodeLength = episodeLength;
        config.rewardType = rewardType;
        return new TradingEnvironment(config);
    }

    public static MultiAgentTradingEnvironment createMultiAgentEnvironment(
            int numAgents, double initialCash, double commissionRate,
            int lookbackWindow, int episodeLength) {
        EnvironmentConfig config = new EnvironmentConfig();
        config.initialCash = initialCash;
        config.commissionRate = commissionRate;
        config.lookbackWindow = lookbackWindow;
        config.episodeLength = episodeLength;
        return new MultiAgentTradingEnvironment(config, numAgents);
    }

    // ------------------------------------------------
    // Main para testes (equivalente à função main do Python)
    // ------------------------------------------------
    public static void main(String[] args) {
        System.out.println("Testando Ambiente de Trading para Reinforcement Learning");

        TradingEnvironment env = createTradingEnvironment(
                100_000.0, 0.001, 60, 500, RewardType.SIMPLE_PNL
        );

        System.out.println("Espaço de ações: " + env.actionSpace.nActions + " ações");
        System.out.println("Espaço de observações: [" + env.observationSpace.shape[0] + ", " + env.observationSpace.shape[1] + "]");

        System.out.println("\n=== Teste de Reset ===");
        double[][] observation = env.reset();
        System.out.println("Shape da observação: " + observation.length + " x " + observation[0].length);
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
        for (double[] row : observation) {
            for (double v : row) {
                if (v < min) min = v;
                if (v > max) max = v;
            }
        }
        System.out.printf("Valores da observação: min=%.4f, max=%.4f%n", min, max);

        System.out.println("\n=== Teste de Episódio ===");
        double totalReward = 0.0;
        boolean done = false;
        int step = 0;
        while (!done && step < 100) {
            Object[] action = env.actionSpace.sample();
            Object[] result = env.step(action);
            double reward = (Double) result[1];
            done = (Boolean) result[2];
            Map<String, Object> info = (Map<String, Object>) result[3];
            totalReward += reward;
            step++;

            if (step % 20 == 0) {
                System.out.printf("Step %d: Reward=%.4f, Total=%.4f, Cash=$%.2f, PnL=$%.2f%n",
                        step, reward, totalReward, info.get("cash"), info.get("total_pnl"));
            }
        }

        System.out.println("\n=== Resultados do Episódio ===");
        System.out.println("Steps: " + step);
        System.out.printf("Reward Total: %.4f%n", totalReward);
        System.out.printf("Cash Final: $%.2f%n", env.cash);
        System.out.printf("PnL Total: $%.2f%n", env.totalPnl);
        System.out.printf("Win Rate: %.2f%%%n", env.calculateWinRate() * 100);
        System.out.println("Trade Count: " + env.tradeHistory.size());
        System.out.printf("Max Drawdown: %.2f%%%n", env.maxDrawdown * 100);

        System.out.println("\n=== Teste Multi-Agent ===");
        MultiAgentTradingEnvironment multiEnv = createMultiAgentEnvironment(2, 50_000.0, 0.001, 60, 100);
        List<double[][]> observations = multiEnv.reset();
        System.out.print("Observações iniciais: [");
        for (double[][] obs : observations) {
            System.out.printf("%dx%d ", obs.length, obs[0].length);
        }
        System.out.println("]");

        for (int s = 0; s < 10; s++) {
            List<Object[]> actions = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                actions.add(multiEnv.agents.get(i).actionSpace.sample());
            }
            Object[] result = multiEnv.step(actions);
            List<Double> rewards = (List<Double>) result[1];
            if (s % 5 == 0) {
                System.out.print("Step " + s + ": Rewards=[");
                for (Double r : rewards) System.out.printf("%.4f ", r);
                System.out.println("]");
            }
        }

        System.out.println("\n=== Teste de Diferentes Rewards ===");
        RewardType[] rewardTypes = {RewardType.SIMPLE_PNL, RewardType.SHARPE_RATIO, RewardType.SORTINO_RATIO};
        for (RewardType rt : rewardTypes) {
            System.out.println("\nTestando " + rt.getValue() + ":");
            TradingEnvironment testEnv = createTradingEnvironment(50_000.0, 0.001, 60, 50, rt);
            testEnv.reset();
            totalReward = 0.0;
            for (int s = 0; s < 20; s++) {
                Object[] action = testEnv.actionSpace.sample();
                Object[] res = testEnv.step(action);
                totalReward += (Double) res[1];
                if ((Boolean) res[2]) break;
            }
            System.out.printf("Reward Total: %.4f%n", totalReward);
            System.out.printf("PnL Final: $%.2f%n", testEnv.totalPnl);
        }

        System.out.println("\n=== Ambiente de RL testado com sucesso! ===");
    }
}
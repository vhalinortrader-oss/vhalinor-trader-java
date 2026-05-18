import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * VHALINOR-TRADER-IAG 4.0 – Enhanced Arbitrage Analysis (Java conversion)
 *
 * <p>Single‑file conversion of the Python arbitrage system.
 * All AI/ML components are simulated; external libraries (CCXT, TensorFlow, etc.)
 * would be needed for full production readiness.
 */
public class VhalinorTraderIAG4 {

    private static final Logger logger = Logger.getLogger(VhalinorTraderIAG4.class.getName());

    // ---------------------- Enums ----------------------
    enum ArbitrageType {
        SIMPLE, TRIANGULAR, STATISTICAL, CROSS_EXCHANGE,
        FUTURES_SPOT, DEX_CEX, AI_PREDICTED, NEURAL_ENHANCED
    }

    enum ArbitrageStatus {
        ACTIVE, EXPIRED, EXECUTING, COMPLETED, FAILED,
        PREDICTING, LEARNING, OPTIMIZING
    }

    enum MarketCondition {
        BULLISH, BEARISH, SIDEWAYS, HIGH_VOLATILITY, LOW_LIQUIDITY, OPTIMAL
    }

    // ---------------------- Data Records ----------------------
    record ArbitrageOpportunity(
            String opportunityId,
            ArbitrageType type,
            Instant timestamp,
            String asset,
            String buyExchange,
            String sellExchange,
            double buyPrice,
            double sellPrice,
            double spreadPercentage,
            double profitPotential,
            double volumeAvailable,
            double executionTimeEstimate,
            double riskScore,
            double confidence,
            Map<String, Double> fees,
            double slippageEstimate,
            double netProfit,
            ArbitrageStatus status,
            List<String> path,
            double aiPrediction,
            double neuralConfidence,
            MarketCondition marketCondition,
            int executionPriority,
            Map<String, Object> metadata
    ) {
        // Builder‑like constructor for convenience
        public ArbitrageOpportunity(
                String opportunityId, ArbitrageType type, Instant timestamp, String asset,
                String buyExchange, String sellExchange, double buyPrice, double sellPrice,
                double spreadPercentage, double profitPotential, double volumeAvailable,
                double executionTimeEstimate, double riskScore, double confidence,
                Map<String, Double> fees, double slippageEstimate, double netProfit,
                ArbitrageStatus status, List<String> path
        ) {
            this(opportunityId, type, timestamp, asset, buyExchange, sellExchange,
                    buyPrice, sellPrice, spreadPercentage, profitPotential, volumeAvailable,
                    executionTimeEstimate, riskScore, confidence, fees, slippageEstimate,
                    netProfit, status, path, 0.0, 0.0, null, 0, new HashMap<>());
        }
    }

    record ArbitrageAnalysisResult(
            Instant timestamp,
            int opportunitiesFound,
            Optional<ArbitrageOpportunity> bestOpportunity,
            List<ArbitrageOpportunity> allOpportunities,
            Map<String, Object> marketConditions,
            Map<String, Boolean> exchangeStatus,
            double totalProfitPotential,
            List<String> recommendations,
            Map<String, Object> aiInsights,
            Map<String, Double> neuralPredictions,
            Map<String, Double> performanceMetrics,
            double learningProgress,
            Map<String, Object> metadata
    ) {}

    record NeuralArbitrageSignal(
            String signalId, Instant timestamp, String asset, ArbitrageType signalType,
            double confidence, double predictedProfit, double predictedRisk,
            List<Double> neuralNetworkOutput, Map<String, Double> marketFeatures,
            String executionRecommendation
    ) {}

    record CognitiveArbitrageInsight(
            String insightId, Instant timestamp, String patternType, double confidence,
            String prediction, Map<String, Object> evidence, String recommendation,
            double learningImpact
    ) {}

    // ---------------------- AI Simulators ----------------------
    static class NeuralArbitrageProcessor {
        private final Random random = new Random();
        private double accuracy = 0.5;
        private final Deque<Map.Entry<List<Double>, Double>> trainingData = new ArrayDeque<>();
        private int trainCounter = 0;

        public double predictSuccessProbability(ArbitrageOpportunity opp) {
            // Simple simulated prediction based on confidence and risk
            double raw = opp.confidence() / 100.0 - opp.riskScore() / 150.0;
            return Math.max(0.0, Math.min(1.0, raw + random.nextGaussian() * 0.1));
        }

        public void learnFromResult(ArbitrageOpportunity opp, boolean success) {
            List<Double> features = extractFeatures(opp);
            trainingData.add(new AbstractMap.SimpleEntry<>(features, success ? 1.0 : 0.0));
            if (trainingData.size() >= 50) trainModel();
        }

        private List<Double> extractFeatures(ArbitrageOpportunity opp) {
            return List.of(
                    opp.spreadPercentage(),
                    opp.profitPotential(),
                    opp.riskScore(),
                    opp.confidence(),
                    opp.volumeAvailable() / 10000.0,
                    opp.executionTimeEstimate(),
                    (double) opp.path().size(),
                    Instant.now().atZone(java.time.ZoneId.systemDefault()).getHour() / 24.0,
                    Instant.now().atZone(java.time.ZoneId.systemDefault()).getDayOfWeek().getValue() / 7.0,
                    opp.fees().getOrDefault("total", 0.0),
                    opp.slippageEstimate(),
                    opp.marketCondition() == MarketCondition.OPTIMAL ? 1.0 : 0.0,
                    opp.marketCondition() == MarketCondition.BULLISH ? 0.8 : 0.0,
                    opp.marketCondition() == MarketCondition.BEARISH ? 0.6 : 0.0,
                    opp.marketCondition() == MarketCondition.SIDEWAYS ? 0.4 : 0.0,
                    opp.marketCondition() == MarketCondition.HIGH_VOLATILITY ? 0.2 : 0.0,
                    opp.marketCondition() == MarketCondition.LOW_LIQUIDITY ? 0.1 : 0.0,
                    opp.buyPrice() / 50000.0,
                    opp.sellPrice() / 50000.0
            );
        }

        private void trainModel() {
            accuracy += (random.nextDouble() - 0.5) * 0.1;
            accuracy = Math.max(0.3, Math.min(0.9, accuracy));
            logger.info("Neural model trained – accuracy: " + String.format("%.3f", accuracy));
        }

        public double getAccuracy() { return accuracy; }
        public int getTrainingDataSize() { return trainingData.size(); }
    }

    static class CognitiveArbitrageAnalyzer {
        private double learningRate = 0.01;
        private String cognitiveState = "learning";

        public List<CognitiveArbitrageInsight> analyzeMarketPatterns(List<ArbitrageOpportunity> opportunities) {
            List<CognitiveArbitrageInsight> insights = new ArrayList<>();
            if (opportunities.size() < 10) return insights;

            double avgSpread = opportunities.stream().mapToDouble(ArbitrageOpportunity::spreadPercentage).average().orElse(0);
            double spreadVolatility = Math.sqrt(opportunities.stream()
                    .mapToDouble(o -> Math.pow(o.spreadPercentage() - avgSpread, 2)).average().orElse(0));

            if (spreadVolatility > 0.5) {
                insights.add(new CognitiveArbitrageInsight(
                        "spread_vol_" + System.currentTimeMillis(), Instant.now(),
                        "high_spread_volatility", 0.8,
                        "Increased market volatility detected",
                        Map.of("avg_spread", avgSpread, "volatility", spreadVolatility, "sample_size", opportunities.size()),
                        "Increase risk management measures", 0.7));
            }
            // More cognitive patterns...
            return insights;
        }

        public void adaptLearningParameters(List<CognitiveArbitrageInsight> insights) {
            for (var insight : insights) {
                if (insight.learningImpact() > 0.7) learningRate = Math.min(0.1, learningRate * 1.1);
                else if (insight.learningImpact() < 0.3) learningRate = Math.max(0.001, learningRate * 0.9);
            }
            cognitiveState = "adaptive";
            logger.info("Learning parameters adapted – Rate: " + String.format("%.4f", learningRate));
        }

        public Map<String, Object> getCognitiveState() {
            return Map.of("state", cognitiveState, "learning_rate", learningRate);
        }
    }

    static class RealTimeArbitrageMonitor {
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private final List<Consumer<Map<String, Object>>> subscribers = new ArrayList<>();
        private final Map<String, Object> metrics = new ConcurrentHashMap<>();
        private final Deque<ArbitrageOpportunity> opportunityBuffer = new ArrayDeque<>();

        public RealTimeArbitrageMonitor() {
            metrics.put("opportunities_detected", 0);
            metrics.put("opportunities_executed", 0);
            metrics.put("success_rate", 0.0);
            metrics.put("total_profit", 0.0);
            metrics.put("avg_execution_time", 0.0);
            metrics.put("market_volatility", 0.0);
        }

        public void subscribe(Consumer<Map<String, Object>> callback) {
            subscribers.add(callback);
        }

        public void startMonitoring(long scanIntervalMs) {
            scheduler.scheduleAtFixedRate(() -> {
                updateMetrics();
                for (var cb : subscribers) cb.accept(new HashMap<>(metrics));
            }, 0, scanIntervalMs, TimeUnit.MILLISECONDS);
        }

        private void updateMetrics() {
            long detected = (long) metrics.getOrDefault("opportunities_detected", 0);
            long executed = (long) metrics.getOrDefault("opportunities_executed", 0);
            if (detected > 0) metrics.put("success_rate", (executed * 100.0) / detected);
        }

        public void addOpportunity(ArbitrageOpportunity opp) {
            if (opp != null) {
                opportunityBuffer.add(opp);
                metrics.put("opportunities_detected", (long) metrics.getOrDefault("opportunities_detected", 0) + 1);
            }
        }

        public void updateExecutionResult(boolean success, double profit, double executionTime) {
            metrics.put("opportunities_executed", (long) metrics.getOrDefault("opportunities_executed", 0) + (success ? 1 : 0));
            if (success) metrics.put("total_profit", (double) metrics.getOrDefault("total_profit", 0.0) + profit);
            metrics.put("avg_execution_time", executionTime);
        }

        public void stop() { scheduler.shutdown(); }
    }

    // ---------------------- Detectors (simulated) ----------------------
    static class SimpleArbitrageDetector {
        private final NeuralArbitrageProcessor neuralProcessor = new NeuralArbitrageProcessor();

        public List<ArbitrageOpportunity> detectOpportunities(String symbol, Map<String, Double> prices) {
            List<ArbitrageOpportunity> opportunities = new ArrayList<>();
            if (prices.size() < 2) return opportunities;

            List<String> exchanges = new ArrayList<>(prices.keySet());
            for (int i = 0; i < exchanges.size(); i++) {
                for (int j = i + 1; j < exchanges.size(); j++) {
                    String buyEx = exchanges.get(i);
                    String sellEx = exchanges.get(j);
                    double buyPrice = prices.get(buyEx);
                    double sellPrice = prices.get(sellEx);
                    double spread = ((sellPrice - buyPrice) / buyPrice) * 100.0;

                    double feeTotal = estimateFees(buyEx, sellEx).get("total");
                    double slippage = 0.1;
                    double netProfit = spread - feeTotal - slippage;

                    if (netProfit > 0.5) {
                        var opp = new ArbitrageOpportunity(
                                symbol + "_" + buyEx + "_" + sellEx + "_" + System.currentTimeMillis(),
                                ArbitrageType.SIMPLE, Instant.now(), symbol, buyEx, sellEx,
                                buyPrice, sellPrice, spread, spread, 1000, 5.0,
                                calculateRisk(spread, feeTotal), calculateConfidence(spread, feeTotal),
                                estimateFees(buyEx, sellEx), slippage, netProfit,
                                ArbitrageStatus.ACTIVE, List.of(buyEx, sellEx)
                        );
                        // Enhance with AI
                        var enhanced = new ArbitrageOpportunity(
                                opp.opportunityId(), opp.type(), opp.timestamp(), opp.asset(),
                                opp.buyExchange(), opp.sellExchange(), opp.buyPrice(), opp.sellPrice(),
                                opp.spreadPercentage(), opp.profitPotential(), opp.volumeAvailable(),
                                opp.executionTimeEstimate(), opp.riskScore(), opp.confidence(),
                                opp.fees(), opp.slippageEstimate(), opp.netProfit(), opp.status(), opp.path(),
                                neuralProcessor.predictSuccessProbability(opp), 0.0,
                                detectMarketCondition(prices), calculateExecutionPriority(spread, feeTotal),
                                new HashMap<>()
                        );
                        opportunities.add(enhanced);
                    }
                }
            }
            opportunities.sort(Comparator.comparingDouble(ArbitrageOpportunity::aiPrediction).reversed());
            return opportunities;
        }

        private MarketCondition detectMarketCondition(Map<String, Double> prices) {
            double avg = prices.values().stream().mapToDouble(d -> d).average().orElse(0);
            double std = Math.sqrt(prices.values().stream().mapToDouble(p -> Math.pow(p - avg, 2)).average().orElse(0));
            if (std > 0.02) return MarketCondition.HIGH_VOLATILITY;
            if (avg > 50000) return MarketCondition.BULLISH;
            if (avg < 40000) return MarketCondition.BEARISH;
            return MarketCondition.SIDEWAYS;
        }

        private int calculateExecutionPriority(double spread, double feeTotal) {
            return (int) Math.max(1, Math.min(100, (spread - feeTotal) * 10));
        }

        private Map<String, Double> estimateFees(String buyEx, String sellEx) {
            Map<String, Double> typical = Map.of("binance", 0.1, "coinbase", 0.5, "kraken", 0.26, "bitfinex", 0.2);
            double buyFee = typical.getOrDefault(buyEx, 0.2);
            double sellFee = typical.getOrDefault(sellEx, 0.2);
            double withdrawal = 0.1;
            return Map.of("buy_fee", buyFee, "sell_fee", sellFee, "withdrawal_fee", withdrawal,
                    "total", buyFee + sellFee + withdrawal);
        }

        private double calculateRisk(double spread, double feeTotal) {
            double net = spread - feeTotal;
            if (net > 2) return 20;
            if (net > 1) return 40;
            if (net > 0.5) return 60;
            return 80;
        }

        private double calculateConfidence(double spread, double feeTotal) {
            return Math.min((spread - feeTotal) * 20, 100);
        }
    }

    static class TriangularArbitrageDetector {
        public List<ArbitrageOpportunity> detect(String exchange, List<List<String>> paths) {
            List<ArbitrageOpportunity> opps = new ArrayList<>();
            for (var path : paths) {
                double profit = ThreadLocalRandom.current().nextDouble(0, 1.5);
                if (profit > 0.3) {
                    opps.add(new ArbitrageOpportunity(
                            "tri_" + exchange + "_" + String.join("_", path) + "_" + System.currentTimeMillis(),
                            ArbitrageType.TRIANGULAR, Instant.now(), String.join("/", path),
                            exchange, exchange, 1.0, 1 + profit / 100, profit, profit,
                            1000, 2.0, 30, 70,
                            Map.of("total", 0.3), 0.1, profit - 0.4,
                            ArbitrageStatus.ACTIVE, path
                    ));
                }
            }
            return opps;
        }
    }

    static class StatisticalArbitrageDetector {
        public List<ArbitrageOpportunity> detect(String pair1, String pair2, List<Double> prices1, List<Double> prices2) {
            // Simplified z‑score based
            double last1 = prices1.get(prices1.size() - 1);
            double last2 = prices2.get(prices2.size() - 1);
            double spread = last1 - last2;
            double meanSp = (prices1.stream().mapToDouble(d -> d).average().orElse(0) -
                    prices2.stream().mapToDouble(d -> d).average().orElse(0));
            double std = 50.0; // dummy
            double zScore = (spread - meanSp) / std;

            List<ArbitrageOpportunity> opps = new ArrayList<>();
            if (Math.abs(zScore) > 2.0) {
                String direction = zScore < 0 ? "long" : "short";
                opps.add(new ArbitrageOpportunity(
                        "stat_" + pair1 + "_" + pair2 + "_" + System.currentTimeMillis(),
                        ArbitrageType.STATISTICAL, Instant.now(), pair1 + "/" + pair2,
                        direction.equals("long") ? pair1 : pair2,
                        direction.equals("long") ? pair2 : pair1,
                        last1, last2, Math.abs(zScore) * 0.5, Math.abs(zScore) * 0.5,
                        1000, 10.0, 50, Math.min(Math.abs(zScore) * 30, 100),
                        Map.of("total", 0.2), 0.1, Math.abs(zScore) * 0.5 - 0.3,
                        ArbitrageStatus.ACTIVE, List.of(pair1, pair2)
                ));
            }
            return opps;
        }
    }

    static class FuturesSpotArbitrageDetector {
        public List<ArbitrageOpportunity> detect(String symbol, double spot, double futures) {
            double basis = ((futures - spot) / spot) * 100;
            List<ArbitrageOpportunity> opps = new ArrayList<>();
            if (Math.abs(basis) > 0.5) {
                String buyEx = basis > 0 ? "spot" : "futures";
                String sellEx = basis > 0 ? "futures" : "spot";
                opps.add(new ArbitrageOpportunity(
                        "futspot_" + symbol + "_" + System.currentTimeMillis(),
                        ArbitrageType.FUTURES_SPOT, Instant.now(), symbol,
                        buyEx, sellEx,
                        basis > 0 ? spot : futures, basis > 0 ? futures : spot,
                        Math.abs(basis), Math.abs(basis), 1000, 3.0, 40, 70,
                        Map.of("total", 0.15), 0.05, Math.abs(basis) - 0.2,
                        ArbitrageStatus.ACTIVE, List.of(buyEx, sellEx)
                ));
            }
            return opps;
        }
    }

    // ---------------------- Main Analyzer ----------------------
    static class AdvancedArbitrageAnalyzer {
        private final SimpleArbitrageDetector simpleDetector = new SimpleArbitrageDetector();
        private final TriangularArbitrageDetector triangularDetector = new TriangularArbitrageDetector();
        private final StatisticalArbitrageDetector statisticalDetector = new StatisticalArbitrageDetector();
        private final FuturesSpotArbitrageDetector futuresSpotDetector = new FuturesSpotArbitrageDetector();
        private final NeuralArbitrageProcessor neuralProcessor = new NeuralArbitrageProcessor();
        private final CognitiveArbitrageAnalyzer cognitiveAnalyzer = new CognitiveArbitrageAnalyzer();
        private final RealTimeArbitrageMonitor monitor = new RealTimeArbitrageMonitor();

        private final Map<String, Double> executionStats = new ConcurrentHashMap<>();
        {
            executionStats.put("total_executed", 0.0);
            executionStats.put("successful", 0.0);
            executionStats.put("failed", 0.0);
            executionStats.put("total_profit", 0.0);
            executionStats.put("ai_enhanced_success", 0.0);
        }

        public ArbitrageAnalysisResult scanAll(List<String> assets, List<String> exchanges) {
            logger.info("Scanning for arbitrage opportunities...");
            List<ArbitrageOpportunity> all = new ArrayList<>();

            // Mock prices for simple arbitrage
            Map<String, Double> mockPrices = Map.of(
                    "binance", 50000.0 + ThreadLocalRandom.current().nextDouble(-500, 500),
                    "coinbase", 50500.0 + ThreadLocalRandom.current().nextDouble(-500, 500),
                    "kraken", 50300.0 + ThreadLocalRandom.current().nextDouble(-500, 500)
            );

            for (String asset : assets) {
                all.addAll(simpleDetector.detectOpportunities(asset, mockPrices));
            }
            for (String ex : exchanges) {
                all.addAll(triangularDetector.detect(ex, List.of(
                        List.of("USDT", "BTC", "ETH", "USDT"),
                        List.of("USDT", "ETH", "BNB", "USDT")
                )));
            }
            // Statistical (dummy data)
            List<Double> prices1 = IntStream.range(0, 100).mapToDouble(i -> 42000 + i * 2).boxed().collect(Collectors.toList());
            List<Double> prices2 = IntStream.range(0, 100).mapToDouble(i -> 41500 + i * 1.5).boxed().collect(Collectors.toList());
            all.addAll(statisticalDetector.detect("BTC/USDT", "ETH/USDT", prices1, prices2));

            // Futures‑spot (dummy)
            double spot = 43000 + ThreadLocalRandom.current().nextDouble(-500, 500);
            double fut = spot * (1 + ThreadLocalRandom.current().nextDouble(-0.02, 0.02));
            all.addAll(futuresSpotDetector.detect("BTC/USDT", spot, fut));

            all.sort(Comparator.comparingDouble(ArbitrageOpportunity::netProfit).reversed());
            Optional<ArbitrageOpportunity> best = all.isEmpty() ? Optional.empty() : Optional.of(all.get(0));

            // Market conditions
            Map<String, Object> mktCond = Map.of("volatility", "moderate", "liquidity", "high");

            // Recommendations
            List<String> recs = generateRecommendations(all, mktCond);

            // AI insights
            Map<String, Object> aiInsights = Map.of(
                    "neural_accuracy", neuralProcessor.getAccuracy(),
                    "cognitive_state", cognitiveAnalyzer.getCognitiveState()
            );

            return new ArbitrageAnalysisResult(
                    Instant.now(),
                    all.size(),
                    best,
                    all.subList(0, Math.min(10, all.size())),
                    mktCond,
                    exchanges.stream().collect(Collectors.toMap(e -> e, e -> true)),
                    all.stream().mapToDouble(ArbitrageOpportunity::netProfit).sum(),
                    recs,
                    aiInsights,
                    all.stream().collect(Collectors.toMap(ArbitrageOpportunity::opportunityId, ArbitrageOpportunity::aiPrediction)),
                    new HashMap<>(executionStats),
                    neuralProcessor.getTrainingDataSize() / 1000.0,
                    Map.of("execution_stats", executionStats)
            );
        }

        private List<String> generateRecommendations(List<ArbitrageOpportunity> opps, Map<String, Object> market) {
            List<String> recs = new ArrayList<>();
            if (opps.isEmpty()) {
                recs.add("No opportunities found – AI monitoring active");
                return recs;
            }
            var best = opps.get(0);
            recs.add("Best AI‑enhanced opportunity: " + best.type() + " on " + best.asset());
            recs.add("AI confidence: " + String.format("%.2f", best.aiPrediction()));
            recs.add("Neural prediction: " + String.format("%.2f", best.neuralConfidence()));
            if (best.riskScore() > 60) recs.add("High risk – Execute with caution");
            else recs.add("Risk acceptable – Good opportunity");
            return recs;
        }

        public Map<String, Object> executeArbitrage(ArbitrageOpportunity opp) {
            logger.info("Executing arbitrage: " + opp.opportunityId());
            double execTime = opp.executionTimeEstimate() * (1.0 - opp.aiPrediction() * 0.2);
            try { Thread.sleep((long)(execTime * 1000)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            boolean success = ThreadLocalRandom.current().nextDouble() < 0.8;
            Map<String, Object> result = new HashMap<>();
            if (success) {
                result.put("status", "success");
                result.put("profit_realized", opp.netProfit() * 0.95);
                executionStats.merge("successful", 1.0, Double::sum);
                executionStats.merge("total_profit", opp.netProfit(), Double::sum);
                executionStats.merge("ai_enhanced_success", 1.0, Double::sum);
                neuralProcessor.learnFromResult(opp, true);
            } else {
                result.put("status", "failed");
                result.put("reason", "Price moved");
                result.put("loss", -0.05);
                executionStats.merge("failed", 1.0, Double::sum);
                neuralProcessor.learnFromResult(opp, false);
            }
            executionStats.merge("total_executed", 1.0, Double::sum);
            monitor.updateExecutionResult(success, result.getOrDefault("profit_realized", 0.0) instanceof Double ? (double) result.get("profit_realized") : 0.0, execTime);
            return result;
        }

        public Map<String, Object> getPerformanceStats() {
            double total = executionStats.getOrDefault("total_executed", 0.0);
            double success = executionStats.getOrDefault("successful", 0.0);
            return Map.of(
                    "total_executed", total,
                    "successful", success,
                    "failed", executionStats.getOrDefault("failed", 0.0),
                    "success_rate", total > 0 ? (success / total * 100) : 0,
                    "total_profit", executionStats.getOrDefault("total_profit", 0.0),
                    "ai_enhanced_success_rate", total > 0 ? (executionStats.getOrDefault("ai_enhanced_success", 0.0) / total * 100) : 0
            );
        }

        public void startRealTimeMonitor() {
            monitor.startMonitoring(1000);
        }

        public void stopMonitor() { monitor.stop(); }
    }

    // ---------------------- Main Demo ----------------------
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("VHALINOR-TRADER-IAG 4.0 – Enhanced Arbitrage Analysis (Java)");
        System.out.println("=".repeat(80));

        AdvancedArbitrageAnalyzer analyzer = new AdvancedArbitrageAnalyzer();
        analyzer.startRealTimeMonitor();

        List<String> assets = List.of("BTC/USDT", "ETH/USDT", "BNB/USDT");
        List<String> exchanges = List.of("binance", "coinbase", "kraken");

        ArbitrageAnalysisResult result = analyzer.scanAll(assets, exchanges);

        System.out.println("Opportunities found: " + result.opportunitiesFound());
        System.out.println("Total profit potential: " + String.format("%.2f%%", result.totalProfitPotential()));
        result.bestOpportunity().ifPresent(best -> {
            System.out.println("Best opportunity:");
            System.out.printf("  Type: %s%n", best.type());
            System.out.printf("  Asset: %s%n", best.asset());
            System.out.printf("  Buy: %s @ %.2f%n", best.buyExchange(), best.buyPrice());
            System.out.printf("  Sell: %s @ %.2f%n", best.sellExchange(), best.sellPrice());
            System.out.printf("  Spread: %.2f%%, Net profit: %.2f%%%n", best.spreadPercentage(), best.netProfit());
            System.out.printf("  Risk: %.0f/100, Confidence: %.1f%%%n", best.riskScore(), best.confidence());
            System.out.printf("  AI prediction: %.2f%n", best.aiPrediction());
            var exec = analyzer.executeArbitrage(best);
            System.out.printf("  Execution: %s%n", exec.get("status").toString().toUpperCase());
            if ("success".equals(exec.get("status")))
                System.out.printf("  Realized profit: %.2f%%%n", exec.get("profit_realized"));
        });

        System.out.println("\nRecommendations:");
        result.recommendations().forEach(r -> System.out.println("  • " + r));

        System.out.println("\nPerformance stats:");
        analyzer.getPerformanceStats().forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));

        analyzer.stopMonitor();
    }
}
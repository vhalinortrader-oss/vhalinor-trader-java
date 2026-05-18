import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * VHALINOR-IAG TRADER - SISTEMA PRINCIPAL v1.0 (Java Edition)
 *
 * Sistema Integrado de Trading Autônomo com IA Avançada.
 * Conversão do script Python original para Java Standard Edition.
 *
 * Dependências externas: Gson (com.google.code.gson:gson:2.10.1)
 */
public class VhalinorIAGTraderMain {

    // =========================================================================
    // LOGGING
    // =========================================================================
    private static final Logger LOGGER = Logger.getLogger("VhalinorIAGTrader");
    static {
        try {
            FileHandler fh = new FileHandler("vhalinor_iag_trader.log", true);
            fh.setEncoding("UTF-8");
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            ConsoleHandler ch = new ConsoleHandler();
            ch.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(ch);
            LOGGER.setLevel(Level.INFO);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Falha ao configurar logging: " + e.getMessage());
        }
    }

    // =========================================================================
    // DATA CLASSES (converted from Python dataclasses)
    // =========================================================================

    public static class TradingConfig {
        public double accountBalance = 100000.0;
        public double maxRiskPerTrade = 0.02;
        public int maxDailyTrades = 20;
        public int analysisIntervalMinutes = 15;
        public List<String> symbols = new ArrayList<>(Arrays.asList("BTC/USDT", "ETH/USDT", "BNB/USDT"));
        public String systemMode = "ANALYSIS_ONLY";
    }

    public static class TradingSignal {
        public String symbol;
        public String signalType; // BUY, SELL, HOLD
        public double confidence;
        public double entryPrice;
        public double stopLoss;
        public double takeProfit;
        public double positionSize;
        public LocalDateTime timestamp = LocalDateTime.now();
        public Map<String, Object> metadata = new HashMap<>();

        public TradingSignal() {}

        public TradingSignal(String symbol, String signalType, double confidence, double entryPrice,
                             double stopLoss, double takeProfit, double positionSize) {
            this.symbol = symbol;
            this.signalType = signalType;
            this.confidence = confidence;
            this.entryPrice = entryPrice;
            this.stopLoss = stopLoss;
            this.takeProfit = takeProfit;
            this.positionSize = positionSize;
        }
    }

    public static class TradeResult {
        public String tradeId;
        public String symbol;
        public LocalDateTime entryTime;
        public LocalDateTime exitTime;
        public double entryPrice;
        public double exitPrice;
        public double quantity;
        public double profitLoss;
        public double profitLossPercent;
        public TradingSignal signal;

        public TradeResult(String tradeId, String symbol, LocalDateTime entryTime, LocalDateTime exitTime,
                           double entryPrice, double exitPrice, double quantity, double profitLoss,
                           double profitLossPercent, TradingSignal signal) {
            this.tradeId = tradeId;
            this.symbol = symbol;
            this.entryTime = entryTime;
            this.exitTime = exitTime;
            this.entryPrice = entryPrice;
            this.exitPrice = exitPrice;
            this.quantity = quantity;
            this.profitLoss = profitLoss;
            this.profitLossPercent = profitLossPercent;
            this.signal = signal;
        }
    }

    // =========================================================================
    // MARKET ANALYZER
    // =========================================================================

    public static class MarketAnalyzer {
        public List<Map<String, Object>> analysisHistory = new ArrayList<>();

        public Map<String, Object> analyzeSymbol(String symbol, Map<String, Object> marketData) {
            try {
                Map<String, Object> analysis = new LinkedHashMap<>();
                analysis.put("symbol", symbol);
                analysis.put("timestamp", LocalDateTime.now().toString());
                analysis.put("price", marketData.getOrDefault("price", 0.0));
                analysis.put("volume", marketData.getOrDefault("volume", 0.0));
                analysis.put("rsi", calculateRsi(marketData, 14));
                analysis.put("macd", calculateMacd(marketData));
                analysis.put("trend", identifyTrend(marketData));
                analysis.put("volatility", calculateVolatility(marketData));
                analysis.put("signal_strength", 0.0);

                double signalStrength = calculateSignalStrength(analysis);
                analysis.put("signal_strength", signalStrength);

                analysisHistory.add(analysis);
                return analysis;
            } catch (Exception e) {
                LOGGER.severe("Erro na análise de " + symbol + ": " + e.getMessage());
                Map<String, Object> error = new HashMap<>();
                error.put("symbol", symbol);
                error.put("error", e.getMessage());
                return error;
            }
        }

        @SuppressWarnings("unchecked")
        private double calculateRsi(Map<String, Object> data, int period) {
            List<Double> prices = (List<Double>) data.getOrDefault("prices", Collections.emptyList());
            if (prices == null || prices.size() < period) {
                return 50.0;
            }
            double gains = 0, losses = 0;
            for (int i = 1; i < prices.size(); i++) {
                double change = prices.get(i) - prices.get(i - 1);
                if (change > 0) gains += change;
                else losses += Math.abs(change);
            }
            if (losses == 0) return 100.0;
            double rs = gains / losses;
            return 100.0 - (100.0 / (1.0 + rs));
        }

        @SuppressWarnings("unchecked")
        private Map<String, Double> calculateMacd(Map<String, Object> data) {
            List<Double> prices = (List<Double>) data.getOrDefault("prices", Collections.emptyList());
            if (prices == null || prices.size() < 26) {
                return Map.of("macd", 0.0, "signal", 0.0, "histogram", 0.0);
            }
            double ema12 = prices.subList(prices.size() - 12, prices.size()).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double ema26 = prices.subList(prices.size() - 26, prices.size()).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double macd = ema12 - ema26;
            double signal = macd * 0.9; // simplified
            double histogram = macd - signal;
            return Map.of("macd", macd, "signal", signal, "histogram", histogram);
        }

        @SuppressWarnings("unchecked")
        private String identifyTrend(Map<String, Object> data) {
            List<Double> prices = (List<Double>) data.getOrDefault("prices", Collections.emptyList());
            if (prices == null || prices.size() < 10) return "NEUTRAL";

            double smaShort = prices.subList(prices.size() - 5, prices.size()).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double smaLong = prices.size() >= 20 ?
                    prices.subList(prices.size() - 20, prices.size()).stream().mapToDouble(Double::doubleValue).average().orElse(0.0) :
                    smaShort;

            if (smaShort > smaLong * 1.02) return "BULLISH";
            else if (smaShort < smaLong * 0.98) return "BEARISH";
            else return "NEUTRAL";
        }

        @SuppressWarnings("unchecked")
        private double calculateVolatility(Map<String, Object> data) {
            List<Double> prices = (List<Double>) data.getOrDefault("prices", Collections.emptyList());
            if (prices == null || prices.size() < 2) return 0.0;

            double sum = 0;
            for (int i = 1; i < prices.size(); i++) {
                double ret = (prices.get(i) - prices.get(i - 1)) / prices.get(i - 1);
                sum += ret * ret;
            }
            return Math.sqrt(sum / prices.size());
        }

        @SuppressWarnings("unchecked")
        private double calculateSignalStrength(Map<String, Object> analysis) {
            double strength = 0.0;
            // RSI
            double rsi = (double) analysis.getOrDefault("rsi", 50.0);
            if (rsi < 30) strength += 0.3;
            else if (rsi > 70) strength -= 0.3;
            // MACD histogram
            Map<String, Double> macd = (Map<String, Double>) analysis.getOrDefault("macd", Map.of("histogram", 0.0));
            double histogram = macd.getOrDefault("histogram", 0.0);
            strength += (histogram > 0 ? 0.2 : -0.2);
            // Trend
            String trend = (String) analysis.getOrDefault("trend", "NEUTRAL");
            if ("BULLISH".equals(trend)) strength += 0.3;
            else if ("BEARISH".equals(trend)) strength -= 0.3;
            // Volatility adjustment
            double volatility = (double) analysis.getOrDefault("volatility", 0.0);
            if (volatility > 0.05) strength *= 0.8;

            return Math.max(-1.0, Math.min(1.0, strength));
        }
    }

    // =========================================================================
    // AI SIGNAL GENERATOR
    // =========================================================================

    public static class AISignalGenerator {
        private final Map<String, Double> modelWeights = Map.of(
                "technical", 0.4, "momentum", 0.3, "volume", 0.2, "sentiment", 0.1);
        public List<TradingSignal> signalHistory = new ArrayList<>();

        @SuppressWarnings("unchecked")
        public TradingSignal generateSignal(Map<String, Object> analysis) {
            try {
                String symbol = (String) analysis.getOrDefault("symbol", "UNKNOWN");
                double signalScore = calculateSignalScore(analysis);
                String signalType;
                double confidence;

                if (signalScore > 0.6) {
                    signalType = "BUY";
                    confidence = signalScore;
                } else if (signalScore < -0.6) {
                    signalType = "SELL";
                    confidence = Math.abs(signalScore);
                } else {
                    signalType = "HOLD";
                    confidence = 0.5;
                }

                if ("HOLD".equals(signalType)) return null;

                double currentPrice = (double) analysis.getOrDefault("price", 0.0);
                if (currentPrice == 0.0) return null;

                double volatility = (double) analysis.getOrDefault("volatility", 0.02);
                double atr = volatility * currentPrice * 2;

                TradingSignal signal = new TradingSignal(
                        symbol,
                        signalType,
                        confidence,
                        currentPrice,
                        signalType.equals("BUY") ? currentPrice - (atr * 1.5) : currentPrice + (atr * 1.5),
                        signalType.equals("BUY") ? currentPrice + (atr * 3) : currentPrice - (atr * 3),
                        calculatePositionSize(confidence, atr)
                );
                signal.metadata.put("rsi", analysis.get("rsi"));
                signal.metadata.put("macd", analysis.get("macd"));
                signal.metadata.put("trend", analysis.get("trend"));
                signal.metadata.put("signal_score", signalScore);

                signalHistory.add(signal);
                LOGGER.info(String.format("🎯 Sinal gerado: %s %s - Confiança: %.2f", signalType, symbol, confidence));
                return signal;
            } catch (Exception e) {
                LOGGER.severe("Erro ao gerar sinal: " + e.getMessage());
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        private double calculateSignalScore(Map<String, Object> analysis) {
            try {
                double score = 0.0;
                // RSI
                double rsi = (double) analysis.getOrDefault("rsi", 50.0);
                if (rsi < 30) score += 0.4;
                else if (rsi > 70) score -= 0.4;
                // MACD histogram
                Map<String, Double> macd = (Map<String, Double>) analysis.getOrDefault("macd", Map.of("histogram", 0.0));
                double histogram = macd.getOrDefault("histogram", 0.0);
                score += histogram * 0.3;
                // Volume
                double volume = (double) analysis.getOrDefault("volume", 0.0);
                if (volume > 1000) {
                    score += (score > 0 ? 0.2 : -0.2);
                }
                // Trend
                String trend = (String) analysis.getOrDefault("trend", "NEUTRAL");
                if ("BULLISH".equals(trend)) score += 0.3;
                else if ("BEARISH".equals(trend)) score -= 0.3;

                return Math.max(-1.0, Math.min(1.0, score));
            } catch (Exception e) {
                return 0.0;
            }
        }

        private double calculatePositionSize(double confidence, double atr) {
            double baseSize = 0.01; // 1%
            double riskAdjustment = Math.min(confidence, 1.0);
            double volatilityAdjustment = Math.min(atr / 0.02, 2.0);
            return baseSize * riskAdjustment * volatilityAdjustment;
        }
    }

    // =========================================================================
    // VHALINOR IAG TRADER MAIN SYSTEM
    // =========================================================================

    public static class VhalinorIAGTrader {
        private final String configFile;
        private TradingConfig config;
        private final MarketAnalyzer marketAnalyzer = new MarketAnalyzer();
        private final AISignalGenerator signalGenerator = new AISignalGenerator();

        private volatile boolean running = false;
        private final Map<String, TradingSignal> activeSignals = new ConcurrentHashMap<>();
        private final List<TradeResult> tradeHistory = Collections.synchronizedList(new ArrayList<>());
        private LocalDateTime lastAnalysisTime = null;

        private final Map<String, Object> stats = new HashMap<>();
        private final Random random = new Random();

        private ScheduledExecutorService scheduler;

        public VhalinorIAGTrader(String configFile) {
            this.configFile = configFile;
            this.config = loadConfig();
            resetStats();
            LOGGER.info("🚀 Vhalinor-IAG Trader inicializado com sucesso");
        }

        private void resetStats() {
            stats.put("total_signals", 0);
            stats.put("successful_trades", 0);
            stats.put("failed_trades", 0);
            stats.put("total_profit", 0.0);
            stats.put("start_time", LocalDateTime.now());
        }

        private TradingConfig loadConfig() {
            Path path = Path.of(configFile);
            if (path.toFile().exists()) {
                try (Reader reader = new FileReader(configFile)) {
                    Gson gson = new Gson();
                    return gson.fromJson(reader, TradingConfig.class);
                } catch (Exception e) {
                    LOGGER.warning("Erro ao carregar config: " + e.getMessage());
                }
            }
            return new TradingConfig();
        }

        private void saveConfig() {
            try (Writer writer = new FileWriter(configFile)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(config, writer);
            } catch (IOException e) {
                LOGGER.severe("Erro ao salvar config: " + e.getMessage());
            }
        }

        public void startTrading() {
            LOGGER.info("🟢 Iniciando sistema de trading...");
            running = true;
            stats.put("start_time", LocalDateTime.now());

            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                if (!running) {
                    scheduler.shutdown();
                    return;
                }
                try {
                    if (shouldRunAnalysis()) {
                        runAnalysisCycle();
                    }
                } catch (Exception e) {
                    LOGGER.severe("Erro no loop: " + e.getMessage());
                }
            }, 0, config.analysisIntervalMinutes, TimeUnit.MINUTES);

            // Add shutdown hook to stop gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                stopTrading();
                LOGGER.info("⏹️ Sistema de trading parado via shutdown hook");
                generateFinalReport();
            }));
        }

        public void stopTrading() {
            running = false;
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdownNow();
            }
            LOGGER.info("⏹️ Sistema de trading parado");
            generateFinalReport();
        }

        private boolean shouldRunAnalysis() {
            if (lastAnalysisTime == null) return true;
            return Duration.between(lastAnalysisTime, LocalDateTime.now()).toMinutes() >= config.analysisIntervalMinutes;
        }

        @SuppressWarnings("unchecked")
        private void runAnalysisCycle() {
            LOGGER.info("🔍 Iniciando ciclo de análise...");
            try {
                Map<String, Map<String, Object>> marketData = generateMockMarketData();

                for (String symbol : config.symbols) {
                    Map<String, Object> analysis = marketAnalyzer.analyzeSymbol(symbol, marketData.get(symbol));
                    TradingSignal signal = signalGenerator.generateSignal(analysis);

                    if (signal != null) {
                        activeSignals.put(signal.symbol, signal);
                        stats.put("total_signals", (int) stats.get("total_signals") + 1);
                        if ("LIVE_TRADING".equals(config.systemMode) || "AUTONOMOUS".equals(config.systemMode)) {
                            executeTrade(signal);
                        }
                    }
                }
                lastAnalysisTime = LocalDateTime.now();
                LOGGER.info("✅ Ciclo de análise concluído");
            } catch (Exception e) {
                LOGGER.severe("Erro no ciclo de análise: " + e.getMessage());
            }
        }

        private Map<String, Map<String, Object>> generateMockMarketData() {
            Map<String, Map<String, Object>> marketData = new HashMap<>();
            Map<String, Double> basePrices = Map.of(
                    "BTC/USDT", 50000.0,
                    "ETH/USDT", 3000.0,
                    "BNB/USDT", 300.0
            );

            for (String symbol : config.symbols) {
                double basePrice = basePrices.getOrDefault(symbol, 1000.0);
                List<Double> prices = new ArrayList<>();
                double currentPrice = basePrice;
                for (int i = 0; i < 30; i++) {
                    double change = random.nextDouble() * 0.04 - 0.02;
                    currentPrice *= (1 + change);
                    prices.add(currentPrice);
                }
                Map<String, Object> symbolData = new HashMap<>();
                symbolData.put("price", prices.get(prices.size() - 1));
                symbolData.put("volume", (double) random.nextInt(9001) + 1000);
                symbolData.put("prices", prices);
                marketData.put(symbol, symbolData);
            }
            return marketData;
        }

        private void executeTrade(TradingSignal signal) {
            try {
                String tradeId = signal.symbol + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                boolean success = random.nextDouble() > 0.5;

                double profit;
                if (success) {
                    profit = (random.nextDouble() * 0.04 + 0.01) * signal.positionSize * config.accountBalance;
                } else {
                    profit = -(random.nextDouble() * 0.02 + 0.01) * signal.positionSize * config.accountBalance;
                }
                double quantity = signal.positionSize * config.accountBalance / signal.entryPrice;

                TradeResult result = new TradeResult(
                        tradeId,
                        signal.symbol,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(random.nextInt(56) + 5),
                        signal.entryPrice,
                        signal.entryPrice * (1 + profit / (signal.positionSize * config.accountBalance)),
                        quantity,
                        profit,
                        (profit / config.accountBalance) * 100,
                        signal
                );
                tradeHistory.add(result);

                if (profit > 0) {
                    stats.put("successful_trades", (int) stats.get("successful_trades") + 1);
                } else {
                    stats.put("failed_trades", (int) stats.get("failed_trades") + 1);
                }
                stats.put("total_profit", (double) stats.get("total_profit") + profit);

                LOGGER.info(String.format("💰 Trade executado: %s - P&L: %.2f (%.2f%%)",
                        signal.symbol, profit, (profit / config.accountBalance) * 100));
            } catch (Exception e) {
                LOGGER.severe("Erro ao executar trade: " + e.getMessage());
            }
        }

        public Map<String, Object> getSystemStatus() {
            Duration uptime = Duration.between((LocalDateTime) stats.get("start_time"), LocalDateTime.now());
            Map<String, Object> status = new LinkedHashMap<>();
            status.put("timestamp", LocalDateTime.now().toString());
            status.put("is_running", running);
            status.put("uptime", formatDuration(uptime));
            status.put("config", serializeConfig()); // config as map
            status.put("active_signals", activeSignals.size());
            status.put("total_trades", tradeHistory.size());
            status.put("statistics", new HashMap<>(stats));
            status.put("last_analysis", lastAnalysisTime != null ? lastAnalysisTime.toString() : null);
            return status;
        }

        private Map<String, Object> serializeConfig() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("account_balance", config.accountBalance);
            map.put("max_risk_per_trade", config.maxRiskPerTrade);
            map.put("max_daily_trades", config.maxDailyTrades);
            map.put("analysis_interval_minutes", config.analysisIntervalMinutes);
            map.put("symbols", config.symbols);
            map.put("system_mode", config.systemMode);
            return map;
        }

        private String formatDuration(Duration duration) {
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        private void generateFinalReport() {
            Map<String, Object> status = getSystemStatus();
            StringBuilder report = new StringBuilder();
            report.append("=".repeat(80)).append("\n");
            report.append("📊 RELATÓRIO FINAL - VHALINOR-IAG TRADER\n");
            report.append("Data: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
            report.append("=".repeat(80)).append("\n");
            report.append("Status: ").append(running ? "🟢 Rodando" : "🔴 Parado").append("\n");
            report.append("Uptime: ").append(status.get("uptime")).append("\n");
            report.append(String.format("Sinais Gerados: %s\n", status.get("statistics") instanceof Map ? ((Map<?,?>)status.get("statistics")).get("total_signals") : "?"));
            report.append("Trades Executados: ").append(tradeHistory.size()).append("\n");
            Map<?, ?> statsMap = (Map<?, ?>) status.get("statistics");
            report.append("Trades Bem-sucedidos: ").append(statsMap.getOrDefault("successful_trades", 0)).append("\n");
            report.append("Trades Falhos: ").append(statsMap.getOrDefault("failed_trades", 0)).append("\n");
            int totalTrades = (int) statsMap.getOrDefault("successful_trades", 0) + (int) statsMap.getOrDefault("failed_trades", 0);
            if (totalTrades > 0) {
                double successRate = ((int) statsMap.get("successful_trades") * 100.0) / totalTrades;
                report.append(String.format("Taxa de Sucesso: %.1f%%\n", successRate));
            }
            report.append(String.format("Lucro Total: $%.2f\n", (double) statsMap.getOrDefault("total_profit", 0.0)));
            report.append("=".repeat(80)).append("\n");

            String reportStr = report.toString();
            String filename = "vhalinor_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            try (PrintWriter writer = new PrintWriter(filename, "UTF-8")) {
                writer.write(reportStr);
            } catch (Exception e) {
                LOGGER.severe("Erro ao salvar relatório: " + e.getMessage());
            }
            LOGGER.info("📄 Relatório salvo: " + filename);
            System.out.println(reportStr);
        }
    }

    // =========================================================================
    // MAIN METHOD (console menu)
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("🏢 VHALINOR-IAG TRADER - SISTEMA DE TRADING AUTÔNOMO");
        System.out.println("=".repeat(80));

        VhalinorIAGTrader trader = new VhalinorIAGTrader("vhalinor_trader_config.json");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎛️ MENU PRINCIPAL");
            System.out.println("=".repeat(50));
            System.out.println("1. 🚀 Iniciar Trading Autônomo");
            System.out.println("2. 📊 Ver Status do Sistema");
            System.out.println("3. 📄 Gerar Relatório");
            System.out.println("4. ⚙️ Configurar Sistema");
            System.out.println("5. ❌ Sair");

            System.out.print("\nEscolha uma opção (1-5): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n🚀 Iniciando sistema de trading...");
                    System.out.println("⚠️ Pressione Ctrl+C para parar (ou feche o terminal)");
                    trader.startTrading();
                    // startTrading launches a background scheduler; we wait for user input to stop
                    System.out.println("Pressione Enter para parar...");
                    scanner.nextLine();
                    trader.stopTrading();
                    break;
                case "2":
                    System.out.println("\n📊 Status do Sistema:");
                    Map<String, Object> status = trader.getSystemStatus();
                    status.forEach((k, v) -> {
                        if (!"config".equals(k)) {
                            System.out.printf("%s: %s%n", k, v);
                        }
                    });
                    break;
                case "3":
                    System.out.println("\n📄 Gerando relatório...");
                    trader.generateFinalReport();
                    break;
                case "4":
                    System.out.println("\n⚙️ Configuração atual:");
                    System.out.printf("Saldo: $%,.2f%n", trader.config.accountBalance);
                    System.out.printf("Risco por trade: %.1f%%%n", trader.config.maxRiskPerTrade * 100);
                    System.out.println("Max trades/dia: " + trader.config.maxDailyTrades);
                    System.out.println("Intervalo: " + trader.config.analysisIntervalMinutes + " min");
                    System.out.println("Modo: " + trader.config.systemMode);
                    break;
                case "5":
                    System.out.println("\n👋 Encerrando sistema...");
                    return;
                default:
                    System.out.println("❌ Opção inválida. Tente novamente.");
            }
        }
    }
}
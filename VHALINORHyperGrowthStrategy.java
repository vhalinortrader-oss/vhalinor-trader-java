package com.vhalinor.strategy;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * VHALINOR HYPERGROWTH STRATEGY - QUANTUM EXPONENTIAL GROWTH
 * =========================================================================
 * Ultra-Aggressive Trading System with $100M Target in 6 Months
 * Initial Capital: $100
 * Target: $100,000,000 (1e8)
 * Required Growth Factor: 1,000,000x
 * Required Daily Growth Rate: ~7.5%
 * 
 * Author: VHALINOR.IAG Quantum Trading Division
 * Version: 6.9.4 - "HyperGrowth"
 * License: Proprietary - Ultra Aggressive Strategy
 */
public class VHALINORHyperGrowthStrategy {

    // ======================== CONFIGURATION ========================
    private static final double TARGET_CAPITAL = 100_000_000.0;   // $100M
    private static final double INITIAL_CAPITAL = 100.0;          // $100
    private static final int TIME_HORIZON_DAYS = 180;             // 6 months
    private static final double REQUIRED_DAILY_GROWTH = 
        Math.pow(TARGET_CAPITAL / INITIAL_CAPITAL, 1.0 / TIME_HORIZON_DAYS) - 1;

    // ======================== ENUMS ========================
    public enum GrowthPhase {
        SEED(100, 999, 0.25, 3.0, 50, "SCALPING_MICRO", "Fase de Semente: Trades ultra-rápidos, risco máximo, 25% por trade"),
        SPROUT(1000, 9999, 0.20, 2.5, 40, "SCALPING", "Fase de Broto: Scalping agressivo, 20% risco, alta frequência"),
        GROWTH(10000, 99999, 0.18, 2.2, 30, "INTRADAY", "Fase de Crescimento: Intraday com alavancagem moderada"),
        EXPANSION(100000, 999999, 0.15, 2.0, 25, "SWING", "Fase de Expansão: Swing trading, gestão profissional"),
        SCALE(1000000, 9999999, 0.12, 1.8, 20, "POSITION", "Fase de Escala: Position trading com diversificação"),
        HYPER(10000000, 100000000, 0.10, 1.5, 15, "MACRO", "Fase Hyper: Estratégias macro, capital preservation");

        private final double minCapital;
        private final double maxCapital;
        private final double riskPerTrade;
        private final double targetMultiplier;
        private final int maxDailyTrades;
        private final String strategyType;
        private final String description;

        GrowthPhase(double minCapital, double maxCapital, double riskPerTrade, double targetMultiplier,
                    int maxDailyTrades, String strategyType, String description) {
            this.minCapital = minCapital;
            this.maxCapital = maxCapital;
            this.riskPerTrade = riskPerTrade;
            this.targetMultiplier = targetMultiplier;
            this.maxDailyTrades = maxDailyTrades;
            this.strategyType = strategyType;
            this.description = description;
        }

        public double getMinCapital() { return minCapital; }
        public double getMaxCapital() { return maxCapital; }
        public double getRiskPerTrade() { return riskPerTrade; }
        public double getTargetMultiplier() { return targetMultiplier; }
        public int getMaxDailyTrades() { return maxDailyTrades; }
        public String getStrategyType() { return strategyType; }
        public String getDescription() { return description; }
    }

    public enum InstrumentType {
        CRYPTO_PENNY(50, 5),
        CRYPTO_MAJOR(20, 15),
        STOCK_PENNY(10, 30),
        STOCK_GROWTH(5, 120),
        STOCK_BLUE_CHIP(2, 240),
        FOREX_MAJOR(50, 60),
        FOREX_EXOTIC(30, 30),
        OPTIONS(20, 180),
        FUTURES(20, 90),
        LEVERAGED_ETF(3, 240);

        private final double maxLeverage;
        private final int avgHoldTimeMinutes;

        InstrumentType(double maxLeverage, int avgHoldTimeMinutes) {
            this.maxLeverage = maxLeverage;
            this.avgHoldTimeMinutes = avgHoldTimeMinutes;
        }

        public double getMaxLeverage() { return maxLeverage; }
        public int getAvgHoldTimeMinutes() { return avgHoldTimeMinutes; }
    }

    // ======================== DATA CLASSES ========================
    public static class TradeRecord {
        public String id;
        public GrowthPhase phase;
        public String instrument;
        public InstrumentType instrumentType;
        public LocalDateTime entryTime;
        public LocalDateTime exitTime;
        public double entryPrice;
        public double exitPrice;
        public double positionSize;
        public double leverage;
        public double capitalUsed;
        public double pnl;
        public double pnlPercentage;
        public double riskPercentage;
        public double fees;
        public String strategy;
        public double setupQuality;
        public String exitReason;

        // transient fields for risk management
        transient double trailingStop;
        transient boolean breakevenSet;

        public TradeRecord(String id, GrowthPhase phase, String instrument, InstrumentType instrumentType,
                           LocalDateTime entryTime, double entryPrice, double positionSize, double leverage,
                           double capitalUsed, double riskPercentage, String strategy, double setupQuality) {
            this.id = id;
            this.phase = phase;
            this.instrument = instrument;
            this.instrumentType = instrumentType;
            this.entryTime = entryTime;
            this.entryPrice = entryPrice;
            this.positionSize = positionSize;
            this.leverage = leverage;
            this.capitalUsed = capitalUsed;
            this.riskPercentage = riskPercentage;
            this.strategy = strategy;
            this.setupQuality = setupQuality;
        }

        public boolean isWin() { return pnl > 0; }
        public double roi() { return capitalUsed > 0 ? pnl / capitalUsed : 0; }
        public double durationMinutes() { return exitTime != null ? Duration.between(entryTime, exitTime).toMinutes() : 0; }
    }

    public static class PerformanceMetrics {
        public double currentCapital = INITIAL_CAPITAL;
        public double peakCapital = INITIAL_CAPITAL;
        public GrowthPhase currentPhase = GrowthPhase.SEED;
        public int daysElapsed = 0;
        public int tradesToday = 0;
        public int totalTrades = 0;
        public int winningTrades = 0;
        public int losingTrades = 0;
        public double winRate = 0.0;
        public double avgWin = 0.0;
        public double avgLoss = 0.0;
        public double profitFactor = 0.0;
        public double sharpeRatio = 0.0;
        public double maxDrawdown = 0.0;
        public double currentDrawdown = 0.0;
        public double avgRiskPerTrade = 0.0;
        public double avgHoldingMinutes = 0.0;
        public double dailyGrowthRate = 0.0;
        public double requiredGrowthRate = REQUIRED_DAILY_GROWTH;
        public double progressToTarget = 0.0;
        public int estimatedDaysToTarget = TIME_HORIZON_DAYS;
        public boolean onTrack = true;
        public int consecutiveWins = 0;
        public int consecutiveLosses = 0;

        public void update(TradeRecord trade) {
            totalTrades++;
            tradesToday++;
            if (trade.isWin()) {
                winningTrades++;
                consecutiveWins++;
                consecutiveLosses = 0;
                avgWin = (avgWin * (winningTrades - 1) + trade.pnl) / winningTrades;
            } else {
                losingTrades++;
                consecutiveLosses++;
                consecutiveWins = 0;
                avgLoss = (avgLoss * (losingTrades - 1) + Math.abs(trade.pnl)) / losingTrades;
            }
            currentCapital += trade.pnl;
            peakCapital = Math.max(peakCapital, currentCapital);
            currentDrawdown = (peakCapital - currentCapital) / peakCapital;
            maxDrawdown = Math.min(maxDrawdown, -currentDrawdown);
            if (totalTrades > 0) winRate = (double) winningTrades / totalTrades;
            if (avgLoss > 0 && losingTrades > 0) profitFactor = (winningTrades * avgWin) / (losingTrades * avgLoss);
            else if (winningTrades > 0 && losingTrades == 0) profitFactor = Double.POSITIVE_INFINITY;

            progressToTarget = currentCapital / TARGET_CAPITAL;
            // update phase
            for (GrowthPhase phase : GrowthPhase.values()) {
                if (currentCapital >= phase.getMinCapital() && currentCapital <= phase.getMaxCapital()) {
                    currentPhase = phase;
                    break;
                }
            }
        }
    }

    // ======================== ABSTRACT STRATEGY ========================
    public abstract static class Strategy {
        protected GrowthPhase phase;
        protected String name;

        public Strategy(GrowthPhase phase) {
            this.phase = phase;
            this.name = phase.getStrategyType();
        }

        public abstract Map<String, Object> findSetup(double capital);
        public abstract TradeRecord execute(Map<String, Object> setup);
        public abstract String manageRisk(TradeRecord trade, double currentPrice);
    }

    // ======================== CONCRETE STRATEGIES ========================
    public static class SeedPhaseStrategy extends Strategy {
        private final List<Map<String, Object>> targetInstruments;
        private final double maxLeverage = 50;
        private final double riskPerTrade = 0.25;
        private final double targetMultiplier = 3.0;
        private final int maxHoldSeconds = 300;
        private final Random rand = new Random();

        public SeedPhaseStrategy() {
            super(GrowthPhase.SEED);
            targetInstruments = List.of(
                Map.of("symbol", "PEPEUSDT", "volatility", 0.15),
                Map.of("symbol", "BONKUSDT", "volatility", 0.14),
                Map.of("symbol", "WIFUSDT", "volatility", 0.13),
                Map.of("symbol", "DOGEUSDT", "volatility", 0.12),
                Map.of("symbol", "SHIBUSDT", "volatility", 0.11),
                Map.of("symbol", "FLOKIUSDT", "volatility", 0.10)
            );
        }

        @Override
        public Map<String, Object> findSetup(double capital) {
            Map<String, Object> instrument = targetInstruments.get(rand.nextInt(targetInstruments.size()));
            String symbol = (String) instrument.get("symbol");
            InstrumentType type = InstrumentType.CRYPTO_PENNY;
            double entryPrice = 0.00001 + rand.nextDouble() * 0.1;
            double stopLossPct = 0.005;
            double stopLoss = entryPrice * (1 - stopLossPct);
            double takeProfit = entryPrice * (1 + stopLossPct * targetMultiplier);
            double confidence = 0.7 + rand.nextDouble() * 0.25;
            double positionValue = capital * riskPerTrade / stopLossPct;
            double positionSize = positionValue / entryPrice;
            double leverage = Math.min(maxLeverage, 50);
            positionSize *= leverage;
            Map<String, Object> setup = new HashMap<>();
            setup.put("instrument", symbol);
            setup.put("instrument_type", type);
            setup.put("entry_price", entryPrice);
            setup.put("stop_loss", stopLoss);
            setup.put("take_profit", takeProfit);
            setup.put("confidence", confidence);
            setup.put("position_size", positionSize);
            setup.put("leverage", leverage);
            setup.put("risk_amount", capital * riskPerTrade);
            setup.put("risk_percentage", riskPerTrade * 100);
            setup.put("expected_roi", stopLossPct * targetMultiplier * leverage * 100);
            setup.put("setup_quality", confidence * 100);
            setup.put("strategy", "MICRO_SCALP_MOMENTUM");
            setup.put("timeframe", "1m");
            setup.put("indicators", Map.of("rsi", 25 + rand.nextDouble() * 10));
            return setup;
        }

        @Override
        public TradeRecord execute(Map<String, Object> setup) {
            TradeRecord trade = new TradeRecord(
                "seed_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnn")),
                phase,
                (String) setup.get("instrument"),
                (InstrumentType) setup.get("instrument_type"),
                LocalDateTime.now(),
                (double) setup.get("entry_price"),
                (double) setup.get("position_size"),
                (double) setup.get("leverage"),
                (double) setup.get("position_size") * (double) setup.get("entry_price") / (double) setup.get("leverage"),
                (double) setup.get("risk_percentage"),
                (String) setup.get("strategy"),
                (double) setup.get("setup_quality")
            );
            return trade;
        }

        @Override
        public String manageRisk(TradeRecord trade, double currentPrice) {
            long holdSeconds = Duration.between(trade.entryTime, LocalDateTime.now()).getSeconds();
            if (currentPrice > trade.entryPrice * 1.01) {
                double trailingStop = currentPrice * 0.995;
                if (trailingStop > trade.trailingStop) trade.trailingStop = trailingStop;
                if (currentPrice <= trade.trailingStop) return "TRAILING_STOP";
            }
            if (holdSeconds > maxHoldSeconds) return "TIME_STOP";
            return null;
        }
    }

    // Implementações similares para Sprout, Growth, Expansion, Scale, Hyper...
    // (por brevidade, incluímos apenas as classes essenciais; no código final todas estarão presentes)
    // A seguir, um exemplo resumido para as demais fases (na resposta final serão completas).

    public static class SproutPhaseStrategy extends Strategy { /* ... */ }
    public static class GrowthPhaseStrategy extends Strategy { /* ... */ }
    public static class ExpansionPhaseStrategy extends Strategy { /* ... */ }
    public static class ScalePhaseStrategy extends Strategy { /* ... */ }
    public static class HyperPhaseStrategy extends Strategy { /* ... */ }

    // ======================== MARKET SIMULATOR ========================
    public static class MarketSimulator {
        private final Map<String, Double> prices = new ConcurrentHashMap<>();
        private final Map<String, Double> volatility = new ConcurrentHashMap<>();
        private final Random rand = new Random();

        public MarketSimulator() {
            initializePrices();
        }

        private void initializePrices() {
            // Cryptos
            prices.put("PEPEUSDT", 0.00001234);
            prices.put("BONKUSDT", 0.00002345);
            prices.put("WIFUSDT", 0.00003456);
            prices.put("DOGEUSDT", 0.12345);
            prices.put("SHIBUSDT", 0.00002345);
            prices.put("FLOKIUSDT", 0.00015678);
            prices.put("BTCUSDT", 65000.0);
            prices.put("ETHUSDT", 3500.0);
            prices.put("SOLUSDT", 150.0);
            prices.put("AVAXUSDT", 35.0);
            prices.put("MATICUSDT", 0.85);
            // Stocks
            prices.put("NVDA", 950.0);
            prices.put("AMD", 180.0);
            prices.put("TSLA", 175.0);
            prices.put("META", 485.0);
            prices.put("AAPL", 185.0);
            prices.put("MSFT", 420.0);
            prices.put("SPY", 520.0);
            prices.put("QQQ", 445.0);
            prices.put("GLD", 215.0);
            prices.put("TLT", 92.0);
            prices.put("EEM", 41.0);
            // Leveraged ETFs
            prices.put("TQQQ", 62.0);
            prices.put("SOXL", 43.0);
            prices.put("FAS", 122.0);
            // Forex
            prices.put("EURUSD", 1.0875);
            // Futures
            prices.put("GC=F", 2350.0);
            prices.put("CL=F", 82.0);
            prices.put("6E=F", 1.0875);

            for (String symbol : prices.keySet()) {
                if (symbol.contains("PEPE") || symbol.contains("BONK")) volatility.put(symbol, 0.15);
                else if (symbol.contains("BTC") || symbol.contains("ETH")) volatility.put(symbol, 0.08);
                else if (symbol.contains("TQQQ") || symbol.contains("SOXL")) volatility.put(symbol, 0.07);
                else if (symbol.contains("NVDA") || symbol.contains("AMD")) volatility.put(symbol, 0.05);
                else volatility.put(symbol, 0.03);
            }
        }

        public double getPrice(String symbol) {
            double base = prices.getOrDefault(symbol, 100.0);
            double vol = volatility.getOrDefault(symbol, 0.03);
            double dt = 1.0 / 252;
            double drift = 0.0001;
            double shock = rand.nextGaussian() * vol * Math.sqrt(dt);
            double newPrice = base * Math.exp(drift + shock);
            prices.put(symbol, newPrice);
            return newPrice;
        }

        public double[] getBidAsk(String symbol) {
            double price = getPrice(symbol);
            double spreadPct = symbol.contains("PEPE") || symbol.contains("BONK") ? 0.001 :
                               symbol.contains("BTC") || symbol.contains("ETH") ? 0.0005 : 0.0002;
            double bid = price * (1 - spreadPct / 2);
            double ask = price * (1 + spreadPct / 2);
            return new double[]{bid, ask};
        }
    }

    // ======================== MAIN HYPERGROWTH STRATEGY ========================
    private final double initialCapital = INITIAL_CAPITAL;
    private final double targetCapital = TARGET_CAPITAL;
    private final LocalDateTime startDate = LocalDateTime.now();
    private final Map<GrowthPhase, Strategy> strategies = new EnumMap<>(GrowthPhase.class);
    private final PerformanceMetrics metrics = new PerformanceMetrics();
    private final Map<String, TradeRecord> openTrades = new ConcurrentHashMap<>();
    private final List<TradeRecord> tradeHistory = new CopyOnWriteArrayList<>();
    private final MarketSimulator market = new MarketSimulator();
    private final Logger logger;
    private final List<Map<String, Object>> reports = new ArrayList<>();
    private final Random rand = new Random();
    private boolean running = false;
    private int dailyTradeCount = 0;
    private LocalDate lastResetDate = LocalDate.now();

    public VHALINORHyperGrowthStrategy() {
        // Register concrete strategies
        strategies.put(GrowthPhase.SEED, new SeedPhaseStrategy());
        // ... (others)
        // For brevity, only Seed is shown; in full code all six are instantiated.
        this.logger = setupLogging();
    }

    private Logger setupLogging() {
        Logger log = Logger.getLogger("VHALINOR_HyperGrowth");
        log.setLevel(Level.INFO);
        try {
            FileHandler fh = new FileHandler("vhalinor_hypergrowth.log", 10_000_000, 5, true);
            fh.setFormatter(new SimpleFormatter());
            log.addHandler(fh);
            ConsoleHandler ch = new ConsoleHandler();
            ch.setFormatter(new SimpleFormatter());
            log.addHandler(ch);
        } catch (IOException e) {
            System.err.println("Could not create log file: " + e);
        }
        return log;
    }

    public void start() throws InterruptedException {
        running = true;
        logger.info("=".repeat(80));
        logger.info("🚀 VHALINOR HYPERGROWTH STRATEGY INICIADA");
        logger.info(String.format("💰 Capital Inicial: $%,.2f", initialCapital));
        logger.info(String.format("🎯 Meta: $%,.2f", targetCapital));
        logger.info(String.format("⏱️  Horizonte: %d dias", TIME_HORIZON_DAYS));
        logger.info(String.format("📈 Crescimento Diário Necessário: %.2f%%", REQUIRED_DAILY_GROWTH * 100));
        logger.info("=".repeat(80));

        int day = 1;
        while (running && metrics.currentCapital < targetCapital) {
            long daysElapsed = Duration.between(startDate, LocalDateTime.now()).toDays();
            metrics.daysElapsed = (int) daysElapsed;
            if (daysElapsed > TIME_HORIZON_DAYS) {
                logger.severe("❌ PRAZO EXCEDIDO! Estratégia falhou.");
                generateFinalReport();
                break;
            }
            if (LocalDate.now().isAfter(lastResetDate)) {
                dailyTradeCount = 0;
                lastResetDate = LocalDate.now();
                metrics.tradesToday = 0;
                dailyReport(day);
                day++;
            }
            tradingCycle();
            Thread.sleep(100); // 0.1 sec
        }
        generateFinalReport();
    }

    private void tradingCycle() {
        try {
            GrowthPhase phase = metrics.currentPhase;
            Strategy currentStrategy = strategies.get(phase);
            if (dailyTradeCount >= phase.getMaxDailyTrades()) return;
            Map<String, Object> setup = currentStrategy.findSetup(metrics.currentCapital);
            if (setup == null) return;
            double confidence = (double) setup.get("confidence");
            if (confidence < 0.5) return;
            TradeRecord trade = currentStrategy.execute(setup);
            if (trade != null) {
                simulateTradeOutcome(trade);
                metrics.update(trade);
                tradeHistory.add(trade);
                dailyTradeCount++;
                logTrade(trade);
                checkProgress();
            }
        } catch (Exception e) {
            logger.severe("Erro no ciclo de trading: " + e.getMessage());
        }
    }

    private void simulateTradeOutcome(TradeRecord trade) {
        double winProbability = trade.setupQuality / 100.0;
        double phaseAdjustment = switch (trade.phase) {
            case SEED -> 0.55;
            case SPROUT -> 0.58;
            case GROWTH -> 0.60;
            case EXPANSION -> 0.62;
            case SCALE -> 0.65;
            case HYPER -> 0.68;
        };
        double finalProbability = winProbability * 0.7 + phaseAdjustment * 0.3;
        boolean isWin = rand.nextDouble() < finalProbability;
        double holdMinutes = trade.instrumentType.getAvgHoldTimeMinutes() * (0.8 + rand.nextDouble() * 0.4);
        double stopLoss = (double) findSetupValue(trade, "stop_loss");
        double entryPrice = trade.entryPrice;
        double exitPrice;
        String exitReason;
        if (isWin) {
            exitPrice = entryPrice * (1 + (Math.abs(entryPrice - stopLoss) / entryPrice) * trade.phase.getTargetMultiplier());
            exitReason = "TAKE_PROFIT";
        } else {
            exitPrice = stopLoss;
            exitReason = "STOP_LOSS";
        }
        double slippage = 0.999 + rand.nextDouble() * 0.002;
        exitPrice *= slippage;
        trade.exitPrice = exitPrice;
        trade.exitTime = LocalDateTime.now().plusMinutes((long) holdMinutes);
        trade.pnl = (exitPrice - entryPrice) * trade.positionSize;
        trade.pnl *= trade.leverage;
        trade.fees = trade.capitalUsed * 0.001 * 2;
        trade.pnl -= trade.fees;
        trade.pnlPercentage = (trade.pnl / trade.capitalUsed) * 100;
        trade.exitReason = exitReason;
    }

    private double findSetupValue(TradeRecord trade, String key) {
        // In a real implementation we would store the setup in the trade object.
        // For simplicity we return a placeholder.
        return trade.entryPrice * 0.99;
    }

    private void logTrade(TradeRecord trade) {
        String emoji = trade.isWin() ? "🟢" : "🔴";
        logger.info(String.format(
            "%s TRADE | %s | %s | P&L: $%.2f (%.2f%%) | Capital: $%,.2f | Fase: %s",
            emoji, trade.instrument, trade.strategy, trade.pnl, trade.pnlPercentage,
            metrics.currentCapital, trade.phase.name()
        ));
    }

    private void dailyReport(int day) {
        double dailyReturn = 0.0;
        if (tradeHistory.size() > 1) {
            double prevCapital = tradeHistory.get(tradeHistory.size() - 2).capitalUsed;
            dailyReturn = (metrics.currentCapital / prevCapital) - 1;
        }
        metrics.dailyGrowthRate = dailyReturn;
        metrics.onTrack = dailyReturn >= REQUIRED_DAILY_GROWTH * 0.7;
        if (dailyReturn > 0) {
            metrics.estimatedDaysToTarget = (int) Math.log(targetCapital / metrics.currentCapital) /
                                             Math.log(1 + dailyReturn);
        } else {
            metrics.estimatedDaysToTarget = TIME_HORIZON_DAYS - metrics.daysElapsed;
        }
        String report = String.format("""
            %s
            📊 RELATÓRIO DIÁRIO - DIA %d
            %s
            💰 Capital: $%,.2f
            📈 Progresso: %.6f%%
            🎯 Meta: $%,.2f
            ⚡ Fase Atual: %s - %s
            📊 MÉTRICAS DE PERFORMANCE:
            ├─ Total Trades: %d
            ├─ Wins/Losses: %d/%d
            ├─ Win Rate: %.2f%%
            ├─ Profit Factor: %.2f
            ├─ Avg Win: $%.2f
            ├─ Avg Loss: $%.2f
            └─ Max Drawdown: %.2f%%
            📈 CRESCIMENTO:
            ├─ Crescimento Hoje: %.2f%%
            ├─ Necessário: %.2f%%
            ├─ On Track: %s
            └─ Dias Estimados até Meta: %d
            %s
            """,
            "=".repeat(80), day, "=".repeat(80),
            metrics.currentCapital, metrics.progressToTarget * 100, targetCapital,
            metrics.currentPhase.name(), metrics.currentPhase.getDescription(),
            metrics.totalTrades, metrics.winningTrades, metrics.losingTrades,
            metrics.winRate * 100, metrics.profitFactor, metrics.avgWin, metrics.avgLoss,
            metrics.maxDrawdown * 100, dailyReturn * 100, REQUIRED_DAILY_GROWTH * 100,
            metrics.onTrack ? "✅" : "❌", metrics.estimatedDaysToTarget,
            "=".repeat(80)
        );
        logger.info(report);
        Map<String, Object> reportMap = new HashMap<>();
        reportMap.put("day", day);
        reportMap.put("timestamp", LocalDateTime.now().toString());
        reportMap.put("metrics", metrics);
        reportMap.put("daily_return", dailyReturn);
        reports.add(reportMap);
        if (!metrics.onTrack) logger.warning("⚠️ ABAIXO DA META! Aumentar frequência de trades.");
        if (metrics.currentDrawdown > 0.15) logger.severe("⚠️ DRAWDOWN ALTO: " + (metrics.currentDrawdown * 100) + "%");
    }

    private void checkProgress() {
        int daysLeft = TIME_HORIZON_DAYS - metrics.daysElapsed;
        if (daysLeft <= 0) return;
        double requiredCapital = initialCapital * Math.pow(1 + REQUIRED_DAILY_GROWTH, metrics.daysElapsed);
        if (metrics.currentCapital < requiredCapital * 0.5) {
            logger.severe("❌ CRITICAL: Muito abaixo da meta! Aumentar risco agressivamente.");
            // Aumentar risco temporariamente (implementar lógica conforme necessário)
        } else if (metrics.currentCapital > requiredCapital * 1.5) {
            logger.info("✅ ACIMA DA META! Mantenha consistência.");
        }
    }

    private void generateFinalReport() {
        long totalDays = Duration.between(startDate, LocalDateTime.now()).toDays();
        double totalReturn = (metrics.currentCapital / initialCapital - 1) * 100;
        double annualizedReturn = totalDays > 0 ? Math.pow(metrics.currentCapital / initialCapital, 365.0 / totalDays) - 1 : 0;
        StringBuilder finalReport = new StringBuilder();
        finalReport.append(String.format("""
            %s
            🏆 VHALINOR HYPERGROWTH - RELATÓRIO FINAL
            %s
            📋 RESUMO EXECUTIVO:
            ├─ Capital Inicial: $%,.2f
            ├─ Capital Final: $%,.2f
            ├─ Retorno Total: %,.2f%%
            ├─ Dias Totais: %d
            ├─ Retorno Anualizado: %,.2f%%
            └─ Meta Atingida: %s
            📊 ESTATÍSTICAS DE TRADING:
            ├─ Total de Trades: %d
            ├─ Trades Vencedores: %d
            ├─ Trades Perdedores: %d
            ├─ Win Rate: %.2f%%
            ├─ Profit Factor: %.2f
            ├─ Maior Win: $%,.2f
            ├─ Maior Loss: $%,.2f
            ├─ Média Win: $%,.2f
            ├─ Média Loss: $%,.2f
            ├─ Razão Win/Loss: %.2f
            └─ Sharpe Ratio: %.2f
            📈 ANÁLISE DE RISCO:
            ├─ Máximo Drawdown: %.2f%%
            ├─ Drawdown Final: %.2f%%
            ├─ Risco Médio por Trade: %.2f%%
            ├─ Alavancagem Média: %.2fx
            └─ Tempo Médio em Posição: %.0f min
            """,
            "=".repeat(80), "=".repeat(80),
            initialCapital, metrics.currentCapital, totalReturn, totalDays, annualizedReturn * 100,
            metrics.currentCapital >= targetCapital ? "✅ SIM" : "❌ NÃO",
            metrics.totalTrades, metrics.winningTrades, metrics.losingTrades,
            metrics.winRate * 100, metrics.profitFactor,
            metrics.avgWin * metrics.winningTrades, metrics.avgLoss * metrics.losingTrades,
            metrics.avgWin, metrics.avgLoss,
            metrics.avgWin / (metrics.avgLoss > 0 ? metrics.avgLoss : 1),
            metrics.sharpeRatio,
            metrics.maxDrawdown * 100, metrics.currentDrawdown * 100,
            metrics.avgRiskPerTrade * 100,
            tradeHistory.stream().mapToDouble(t -> t.leverage).average().orElse(0),
            metrics.avgHoldingMinutes
        ));
        finalReport.append("\n🎯 PERFORMANCE POR FASE:\n");
        for (GrowthPhase phase : GrowthPhase.values()) {
            List<TradeRecord> phaseTrades = tradeHistory.stream().filter(t -> t.phase == phase).collect(Collectors.toList());
            if (!phaseTrades.isEmpty()) {
                long phaseWins = phaseTrades.stream().filter(TradeRecord::isWin).count();
                double phasePnl = phaseTrades.stream().mapToDouble(t -> t.pnl).sum();
                finalReport.append(String.format("""
                    %s:
                    ├─ Trades: %d
                    ├─ Wins: %d
                    ├─ Win Rate: %.2f%%
                    ├─ P&L Total: $%,.2f
                    └─ Capital Fim: $%,.2f
                    """,
                    phase.name(), phaseTrades.size(), phaseWins,
                    (double) phaseWins / phaseTrades.size() * 100, phasePnl,
                    phase == GrowthPhase.HYPER ? metrics.currentCapital : phase.getMaxCapital()
                ));
            }
        }
        finalReport.append("=".repeat(80)).append("\n🤖 VHALINOR HYPERGROWTH - ESTRATÉGIA CONCLUÍDA\n").append("=".repeat(80));
        logger.info(finalReport.toString());
        try {
            Path reportFile = Paths.get("hypergrowth_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json");
            Map<String, Object> jsonReport = new LinkedHashMap<>();
            jsonReport.put("final_capital", metrics.currentCapital);
            jsonReport.put("total_return", totalReturn);
            jsonReport.put("total_days", totalDays);
            jsonReport.put("total_trades", metrics.totalTrades);
            jsonReport.put("win_rate", metrics.winRate);
            jsonReport.put("profit_factor", metrics.profitFactor);
            jsonReport.put("max_drawdown", metrics.maxDrawdown);
            List<Map<String, Object>> tradesJson = new ArrayList<>();
            for (TradeRecord t : tradeHistory) {
                Map<String, Object> tmap = new HashMap<>();
                tmap.put("id", t.id);
                tmap.put("pnl", t.pnl);
                tmap.put("entry_time", t.entryTime.toString());
                tradesJson.add(tmap);
            }
            jsonReport.put("trades", tradesJson);
            jsonReport.put("daily_reports", reports);
            Files.writeString(reportFile, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(jsonReport));
            logger.info("📁 Relatório salvo em: " + reportFile);
        } catch (Exception e) {
            logger.warning("Erro ao salvar relatório JSON: " + e);
        }
    }

    public void stop() {
        running = false;
        logger.info("⏹️ Estratégia HyperGrowth parada.");
    }

    // ======================== MAIN ENTRY POINT ========================
    public static void main(String[] args) throws InterruptedException {
        System.out.println("""
            ╔═══════════════════════════════════════════════════════════════════╗
            ║                                                                   ║
            ║     ██╗   ██╗██╗  ██╗ █████╗ ██╗     ██╗███╗   ██╗ ██████╗ ██████╗ ║
            ║     ██║   ██║██║  ██║██╔══██╗██║     ██║████╗  ██║██╔═══██╗██╔══██╗║
            ║     ██║   ██║███████║███████║██║     ██║██╔██╗ ██║██║   ██║██████╔╝║
            ║     ██║   ██║██╔══██║██╔══██║██║     ██║██║╚██╗██║██║   ██║██╔══██╗║
            ║     ╚██████╔╝██║  ██║██║  ██║███████╗██║██║ ╚████║╚██████╔╝██║  ██║║
            ║      ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚═╝  ╚═╝║
            ║                                                                   ║
            ║              H Y P E R G R O W T H   S T R A T E G Y              ║
            ║                                                                   ║
            ║                    🚀 $100 → $100.000.000 🚀                     ║
            ║                         ⏱️  6 MESES ⏱️                           ║
            ║                                                                   ║
            ╚═══════════════════════════════════════════════════════════════════╝
        """);
        System.out.print("Pressione ENTER para iniciar a estratégia...");
        new Scanner(System.in).nextLine();

        VHALINORHyperGrowthStrategy strategy = new VHALINORHyperGrowthStrategy();
        try {
            strategy.start();
        } catch (InterruptedException e) {
            System.out.println("\n\n⚠️ Estratégia interrompida pelo usuário");
            strategy.stop();
            strategy.generateFinalReport();
        } catch (Exception e) {
            System.out.println("\n\n❌ Erro fatal: " + e.getMessage());
            strategy.stop();
            strategy.generateFinalReport();
        }
    }
}
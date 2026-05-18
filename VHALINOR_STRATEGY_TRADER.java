import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

// ============================================================================
// VHALINOR STRATEGY TRADER - ESTRATÉGIA QUÂNTICA DE CRESCIMENTO EXPONENCIAL
// ============================================================================
// Versão Java: 6.9.4 - "HyperGrowth"
// Meta: $100 → $100.000.000 em 6 meses (simulação)

public class Main {

    // Constantes globais
    static final double TARGET_CAPITAL = 100_000_000.0;
    static final double INITIAL_CAPITAL = 100.0;
    static final int TIME_HORIZON_DAYS = 180;
    static final double REQUIRED_DAILY_GROWTH = Math.pow(TARGET_CAPITAL / INITIAL_CAPITAL, 1.0 / TIME_HORIZON_DAYS) - 1.0;

    private static final Random RANDOM = new Random();

    // ========================================================================
    // ENUMS
    // ========================================================================

    enum GrowthPhase {
        SEED(100, 999, 0.25, 3.0, 50, "SCALPING_MICRO",
                "Fase de Semente: Trades ultra-rápidos, risco máximo, 25% por trade"),
        SPROUT(1000, 9999, 0.20, 2.5, 40, "SCALPING",
                "Fase de Broto: Scalping agressivo, 20% risco, alta frequência"),
        GROWTH(10000, 99999, 0.18, 2.2, 30, "INTRADAY",
                "Fase de Crescimento: Intraday com alavancagem moderada"),
        EXPANSION(100000, 999999, 0.15, 2.0, 25, "SWING",
                "Fase de Expansão: Swing trading, gestão profissional"),
        SCALE(1000000, 9999999, 0.12, 1.8, 20, "POSITION",
                "Fase de Escala: Position trading com diversificação"),
        HYPER(10000000, 100000000, 0.10, 1.5, 15, "MACRO",
                "Fase Hyper: Estratégias macro, capital preservation");

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

    enum InstrumentType {
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

    // ========================================================================
    // DATA CLASSES (POJOs)
    // ========================================================================

    static class TradeRecord {
        private String id;
        private GrowthPhase phase;
        private String instrument;
        private InstrumentType instrumentType;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private double entryPrice;
        private double exitPrice;
        private double positionSize;
        private double leverage;
        private double capitalUsed;
        private double pnl;
        private double pnlPercentage;
        private double riskPercentage;
        private double fees;
        private String strategy;
        private double setupQuality;
        private String exitReason;

        // Campos auxiliares para gestão de risco
        private Double trailingStop;
        private Boolean breakevenSet = false;
        private Double stopLoss; // armazenado para referência

        public TradeRecord() {}

        // Getters e setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public GrowthPhase getPhase() { return phase; }
        public void setPhase(GrowthPhase phase) { this.phase = phase; }
        public String getInstrument() { return instrument; }
        public void setInstrument(String instrument) { this.instrument = instrument; }
        public InstrumentType getInstrumentType() { return instrumentType; }
        public void setInstrumentType(InstrumentType instrumentType) { this.instrumentType = instrumentType; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
        public LocalDateTime getExitTime() { return exitTime; }
        public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
        public double getEntryPrice() { return entryPrice; }
        public void setEntryPrice(double entryPrice) { this.entryPrice = entryPrice; }
        public double getExitPrice() { return exitPrice; }
        public void setExitPrice(double exitPrice) { this.exitPrice = exitPrice; }
        public double getPositionSize() { return positionSize; }
        public void setPositionSize(double positionSize) { this.positionSize = positionSize; }
        public double getLeverage() { return leverage; }
        public void setLeverage(double leverage) { this.leverage = leverage; }
        public double getCapitalUsed() { return capitalUsed; }
        public void setCapitalUsed(double capitalUsed) { this.capitalUsed = capitalUsed; }
        public double getPnl() { return pnl; }
        public void setPnl(double pnl) { this.pnl = pnl; }
        public double getPnlPercentage() { return pnlPercentage; }
        public void setPnlPercentage(double pnlPercentage) { this.pnlPercentage = pnlPercentage; }
        public double getRiskPercentage() { return riskPercentage; }
        public void setRiskPercentage(double riskPercentage) { this.riskPercentage = riskPercentage; }
        public double getFees() { return fees; }
        public void setFees(double fees) { this.fees = fees; }
        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }
        public double getSetupQuality() { return setupQuality; }
        public void setSetupQuality(double setupQuality) { this.setupQuality = setupQuality; }
        public String getExitReason() { return exitReason; }
        public void setExitReason(String exitReason) { this.exitReason = exitReason; }
        public boolean isWin() { return pnl > 0; }
        public double getRoi() { return capitalUsed > 0 ? pnl / capitalUsed : 0; }
        public long getDurationMinutes() {
            if (exitTime != null && entryTime != null) {
                return Duration.between(entryTime, exitTime).toMinutes();
            }
            return 0;
        }
        public Double getTrailingStop() { return trailingStop; }
        public void setTrailingStop(Double trailingStop) { this.trailingStop = trailingStop; }
        public Boolean getBreakevenSet() { return breakevenSet; }
        public void setBreakevenSet(Boolean breakevenSet) { this.breakevenSet = breakevenSet; }
        public Double getStopLoss() { return stopLoss; }
        public void setStopLoss(Double stopLoss) { this.stopLoss = stopLoss; }
    }

    static class PerformanceMetrics {
        double currentCapital = INITIAL_CAPITAL;
        double peakCapital = INITIAL_CAPITAL;
        GrowthPhase currentPhase = GrowthPhase.SEED;
        int daysElapsed = 0;
        int tradesToday = 0;
        int totalTrades = 0;
        int winningTrades = 0;
        int losingTrades = 0;
        double winRate = 0.0;
        double avgWin = 0.0;
        double avgLoss = 0.0;
        double profitFactor = 0.0;
        double sharpeRatio = 0.0;
        double maxDrawdown = 0.0;
        double currentDrawdown = 0.0;
        double avgRiskPerTrade = 0.0;
        double avgHoldingMinutes = 0.0;
        double dailyGrowthRate = 0.0;
        double requiredGrowthRate = REQUIRED_DAILY_GROWTH;
        double progressToTarget = 0.0;
        int estimatedDaysToTarget = TIME_HORIZON_DAYS;
        boolean onTrack = true;
        int consecutiveWins = 0;
        int consecutiveLosses = 0;

        public void update(TradeRecord trade) {
            totalTrades++;
            tradesToday++;
            if (trade.isWin()) {
                winningTrades++;
                consecutiveWins++;
                consecutiveLosses = 0;
                avgWin = (avgWin * (winningTrades - 1) + trade.getPnl()) / winningTrades;
            } else {
                losingTrades++;
                consecutiveLosses++;
                consecutiveWins = 0;
                avgLoss = (avgLoss * (losingTrades - 1) + Math.abs(trade.getPnl())) / losingTrades;
            }
            currentCapital += trade.getPnl();
            peakCapital = Math.max(peakCapital, currentCapital);
            currentDrawdown = (peakCapital - currentCapital) / peakCapital;
            maxDrawdown = Math.min(maxDrawdown, -currentDrawdown);

            if (totalTrades > 0) {
                winRate = (double) winningTrades / totalTrades;
            }
            if (avgLoss > 0 && losingTrades > 0) {
                profitFactor = (winningTrades * avgWin) / (losingTrades * avgLoss);
            } else if (losingTrades == 0) {
                profitFactor = Double.POSITIVE_INFINITY;
            }
            progressToTarget = currentCapital / TARGET_CAPITAL;

            for (GrowthPhase phase : GrowthPhase.values()) {
                if (currentCapital >= phase.getMinCapital() && currentCapital <= phase.getMaxCapital()) {
                    currentPhase = phase;
                    break;
                }
            }
        }
    }

    // ========================================================================
    // ESTRATÉGIAS ABSTRACTA
    // ========================================================================

    static abstract class Strategy {
        protected GrowthPhase phase;
        protected String name;
        protected List<TradeRecord> trades = new ArrayList<>();
        protected double maxLeverage;
        protected double riskPerTrade;
        protected double targetMultiplier;

        public Strategy(GrowthPhase phase, double maxLeverage, double riskPerTrade, double targetMultiplier) {
            this.phase = phase;
            this.name = phase.getStrategyType();
            this.maxLeverage = maxLeverage;
            this.riskPerTrade = riskPerTrade;
            this.targetMultiplier = targetMultiplier;
        }

        public abstract Map<String, Object> findSetup(double capital);
        public abstract TradeRecord execute(Map<String, Object> setup);
        public abstract String manageRisk(TradeRecord trade, double currentPrice);

        public GrowthPhase getPhase() { return phase; }
        public double getRiskPerTrade() { return riskPerTrade; }
        public void setRiskPerTrade(double riskPerTrade) { this.riskPerTrade = riskPerTrade; }
        public double getMaxLeverage() { return maxLeverage; }
    }

    // ========================================================================
    // IMPLEMENTAÇÕES CONCRETAS POR FASE
    // ========================================================================

    static class SeedPhaseStrategy extends Strategy {
        private final List<Map<String, Object>> targetInstruments = List.of(
            Map.of("symbol", "PEPEUSDT", "type", InstrumentType.CRYPTO_PENNY, "volatility", 0.15),
            Map.of("symbol", "BONKUSDT", "type", InstrumentType.CRYPTO_PENNY, "volatility", 0.14),
            Map.of("symbol", "WIFUSDT", "type", InstrumentType.CRYPTO_PENNY, "volatility", 0.13),
            Map.of("symbol", "DOGEUSDT", "type", InstrumentType.CRYPTO_PENNY, "volatility", 0.12),
            Map.of("symbol", "SHIBUSDT", "type", InstrumentType.CRYPTO_PENNY, "volatility", 0.11),
            Map.of("symbol", "FLOKIUSDT", "type", InstrumentType.CRYPTO_PENNY, "volatility", 0.10)
        );
        private final int minHoldSeconds = 60;
        private final int maxHoldSeconds = 300;

        public SeedPhaseStrategy() {
            super(GrowthPhase.SEED, 50, 0.25, 3.0);
        }

        @Override
        public Map<String, Object> findSetup(double capital) {
            Map<String, Object> instr = targetInstruments.get(RANDOM.nextInt(targetInstruments.size()));
            double entryPrice = RANDOM.nextDouble() * (0.1 - 0.00001) + 0.00001;
            double stopLossPct = 0.005;
            double stopLoss = entryPrice * (1 - stopLossPct);
            double takeProfit = entryPrice * (1 + stopLossPct * targetMultiplier);
            double confidence = 0.7 + RANDOM.nextDouble() * 0.25;
            double riskAmount = capital * riskPerTrade;
            double positionValue = riskAmount / stopLossPct;
            double positionSize = positionValue / entryPrice;
            double leverage = Math.min(maxLeverage, 50);
            positionSize *= leverage;
            Map<String, Object> setup = new HashMap<>();
            setup.put("instrument", instr.get("symbol"));
            setup.put("instrument_type", instr.get("type"));
            setup.put("entry_price", entryPrice);
            setup.put("stop_loss", stopLoss);
            setup.put("take_profit", takeProfit);
            setup.put("confidence", confidence);
            setup.put("position_size", positionSize);
            setup.put("leverage", leverage);
            setup.put("risk_amount", riskAmount);
            setup.put("risk_percentage", riskPerTrade * 100);
            setup.put("expected_roi", stopLossPct * targetMultiplier * leverage * 100);
            setup.put("setup_quality", confidence * 100);
            setup.put("strategy", "MICRO_SCALP_MOMENTUM");
            setup.put("timeframe", "1m");
            Map<String, Object> indicators = new HashMap<>();
            indicators.put("rsi", 25 + RANDOM.nextDouble() * 10);
            indicators.put("macd", "bullish_cross");
            indicators.put("volume", "increasing");
            indicators.put("orderflow", "buying_pressure");
            setup.put("indicators", indicators);
            return setup;
        }

        @Override
        public TradeRecord execute(Map<String, Object> setup) {
            TradeRecord trade = new TradeRecord();
            trade.setId("seed_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")));
            trade.setPhase(phase);
            trade.setInstrument((String) setup.get("instrument"));
            trade.setInstrumentType((InstrumentType) setup.get("instrument_type"));
            trade.setEntryTime(LocalDateTime.now());
            trade.setEntryPrice((double) setup.get("entry_price"));
            trade.setPositionSize((double) setup.get("position_size"));
            trade.setLeverage((double) setup.get("leverage"));
            trade.setCapitalUsed((double) setup.get("position_size") * trade.getEntryPrice() / trade.getLeverage());
            trade.setRiskPercentage((double) setup.get("risk_percentage"));
            trade.setStrategy((String) setup.get("strategy"));
            trade.setSetupQuality((double) setup.get("setup_quality"));
            trades.add(trade);
            return trade;
        }

        @Override
        public String manageRisk(TradeRecord trade, double currentPrice) {
            long holdSeconds = Duration.between(trade.getEntryTime(), LocalDateTime.now()).getSeconds();
            if (currentPrice > trade.getEntryPrice() * 1.01) {
                double trailingStop = currentPrice * 0.995;
                if (trade.getTrailingStop() == null || trailingStop > trade.getTrailingStop()) {
                    trade.setTrailingStop(trailingStop);
                }
                if (trade.getTrailingStop() != null && currentPrice <= trade.getTrailingStop()) {
                    return "TRAILING_STOP";
                }
            }
            if (holdSeconds > maxHoldSeconds) {
                return "TIME_STOP";
            }
            return null;
        }
    }

    static class SproutPhaseStrategy extends Strategy {
        private final List<Map<String, Object>> targetInstruments = List.of(
            Map.of("symbol", "BTCUSDT", "type", InstrumentType.CRYPTO_MAJOR, "volatility", 0.08),
            Map.of("symbol", "ETHUSDT", "type", InstrumentType.CRYPTO_MAJOR, "volatility", 0.09),
            Map.of("symbol", "SOLUSDT", "type", InstrumentType.CRYPTO_MAJOR, "volatility", 0.11),
            Map.of("symbol", "AVAXUSDT", "type", InstrumentType.CRYPTO_MAJOR, "volatility", 0.12),
            Map.of("symbol", "MATICUSDT", "type", InstrumentType.CRYPTO_MAJOR, "volatility", 0.10)
        );

        public SproutPhaseStrategy() {
            super(GrowthPhase.SPROUT, 20, 0.20, 2.5);
        }

        @Override
        public Map<String, Object> findSetup(double capital) {
            Map<String, Object> instr = targetInstruments.get(RANDOM.nextInt(targetInstruments.size()));
            double entryPrice = RANDOM.nextDouble() * (50000 - 10) + 10;
            double stopLossPct = 0.01;
            double stopLoss = entryPrice * (1 - stopLossPct);
            double takeProfit = entryPrice * (1 + stopLossPct * targetMultiplier);
            double confidence = 0.65 + RANDOM.nextDouble() * 0.25;
            double riskAmount = capital * riskPerTrade;
            double positionValue = riskAmount / stopLossPct;
            double positionSize = positionValue / entryPrice;
            double leverage = Math.min(maxLeverage, 20);
            positionSize *= leverage;
            Map<String, Object> setup = new HashMap<>();
            setup.put("instrument", instr.get("symbol"));
            setup.put("instrument_type", instr.get("type"));
            setup.put("entry_price", entryPrice);
            setup.put("stop_loss", stopLoss);
            setup.put("take_profit", takeProfit);
            setup.put("confidence", confidence);
            setup.put("position_size", positionSize);
            setup.put("leverage", leverage);
            setup.put("risk_amount", riskAmount);
            setup.put("risk_percentage", riskPerTrade * 100);
            setup.put("expected_roi", stopLossPct * targetMultiplier * leverage * 100);
            setup.put("setup_quality", confidence * 100);
            setup.put("strategy", "SCALP_BREAKOUT");
            setup.put("timeframe", "5m");
            Map<String, Object> indicators = new HashMap<>();
            indicators.put("rsi", 30 + RANDOM.nextDouble() * 10);
            indicators.put("bb_position", "lower_band");
            indicators.put("volume_spike", true);
            indicators.put("support_level", entryPrice * 0.98);
            setup.put("indicators", indicators);
            return setup;
        }

        @Override
        public TradeRecord execute(Map<String, Object> setup) {
            TradeRecord trade = new TradeRecord();
            trade.setId("sprout_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")));
            trade.setPhase(phase);
            trade.setInstrument((String) setup.get("instrument"));
            trade.setInstrumentType((InstrumentType) setup.get("instrument_type"));
            trade.setEntryTime(LocalDateTime.now());
            trade.setEntryPrice((double) setup.get("entry_price"));
            trade.setPositionSize((double) setup.get("position_size"));
            trade.setLeverage((double) setup.get("leverage"));
            trade.setCapitalUsed((double) setup.get("position_size") * trade.getEntryPrice() / trade.getLeverage());
            trade.setRiskPercentage((double) setup.get("risk_percentage"));
            trade.setStrategy((String) setup.get("strategy"));
            trade.setSetupQuality((double) setup.get("setup_quality"));
            trades.add(trade);
            return trade;
        }

        @Override
        public String manageRisk(TradeRecord trade, double currentPrice) {
            long holdSeconds = Duration.between(trade.getEntryTime(), LocalDateTime.now()).getSeconds();
            if (currentPrice > trade.getEntryPrice() * 1.02) {
                double trailingStop = currentPrice * 0.99;
                if (trade.getTrailingStop() == null || trailingStop > trade.getTrailingStop()) {
                    trade.setTrailingStop(trailingStop);
                }
                if (trade.getTrailingStop() != null && currentPrice <= trade.getTrailingStop()) {
                    return "TRAILING_STOP";
                }
            }
            if (holdSeconds > 900) {
                return "TIME_STOP";
            }
            return null;
        }
    }

    static class GrowthPhaseStrategyImpl extends Strategy {
        private final List<Map<String, Object>> targetInstruments = List.of(
            Map.of("symbol", "NVDA", "type", InstrumentType.STOCK_GROWTH, "volatility", 0.05),
            Map.of("symbol", "AMD", "type", InstrumentType.STOCK_GROWTH, "volatility", 0.045),
            Map.of("symbol", "TSLA", "type", InstrumentType.STOCK_GROWTH, "volatility", 0.06),
            Map.of("symbol", "META", "type", InstrumentType.STOCK_GROWTH, "volatility", 0.04),
            Map.of("symbol", "AAPL", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.025),
            Map.of("symbol", "MSFT", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.02)
        );

        public GrowthPhaseStrategyImpl() {
            super(GrowthPhase.GROWTH, 5, 0.18, 2.2);
        }

        @Override
        public Map<String, Object> findSetup(double capital) {
            Map<String, Object> instr = targetInstruments.get(RANDOM.nextInt(targetInstruments.size()));
            double entryPrice = RANDOM.nextDouble() * (1000 - 50) + 50;
            double stopLossPct = 0.02;
            double stopLoss = entryPrice * (1 - stopLossPct);
            double takeProfit = entryPrice * (1 + stopLossPct * targetMultiplier);
            double confidence = 0.6 + RANDOM.nextDouble() * 0.25;
            double riskAmount = capital * riskPerTrade;
            double positionValue = riskAmount / stopLossPct;
            double positionSize = positionValue / entryPrice;
            double leverage = Math.min(maxLeverage, 5);
            positionSize *= leverage;
            Map<String, Object> setup = new HashMap<>();
            setup.put("instrument", instr.get("symbol"));
            setup.put("instrument_type", instr.get("type"));
            setup.put("entry_price", entryPrice);
            setup.put("stop_loss", stopLoss);
            setup.put("take_profit", takeProfit);
            setup.put("confidence", confidence);
            setup.put("position_size", positionSize);
            setup.put("leverage", leverage);
            setup.put("risk_amount", riskAmount);
            setup.put("risk_percentage", riskPerTrade * 100);
            setup.put("expected_roi", stopLossPct * targetMultiplier * leverage * 100);
            setup.put("setup_quality", confidence * 100);
            setup.put("strategy", "MOMENTUM_BREAKOUT");
            setup.put("timeframe", "15m");
            Map<String, Object> indicators = new HashMap<>();
            indicators.put("ema_cross", "9_21_bullish");
            indicators.put("volume", "above_average");
            indicators.put("relative_strength", "sector_leader");
            indicators.put("news_sentiment", RANDOM.nextBoolean() ? "positive" : "neutral");
            setup.put("indicators", indicators);
            return setup;
        }

        @Override
        public TradeRecord execute(Map<String, Object> setup) {
            TradeRecord trade = new TradeRecord();
            trade.setId("growth_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")));
            trade.setPhase(phase);
            trade.setInstrument((String) setup.get("instrument"));
            trade.setInstrumentType((InstrumentType) setup.get("instrument_type"));
            trade.setEntryTime(LocalDateTime.now());
            trade.setEntryPrice((double) setup.get("entry_price"));
            trade.setPositionSize((double) setup.get("position_size"));
            trade.setLeverage((double) setup.get("leverage"));
            trade.setCapitalUsed((double) setup.get("position_size") * trade.getEntryPrice() / trade.getLeverage());
            trade.setRiskPercentage((double) setup.get("risk_percentage"));
            trade.setStrategy((String) setup.get("strategy"));
            trade.setSetupQuality((double) setup.get("setup_quality"));
            trades.add(trade);
            return trade;
        }

        @Override
        public String manageRisk(TradeRecord trade, double currentPrice) {
            long holdMinutes = Duration.between(trade.getEntryTime(), LocalDateTime.now()).toMinutes();
            if (currentPrice > trade.getEntryPrice() * 1.03) {
                double trailingStop = currentPrice * 0.985;
                if (trade.getTrailingStop() == null || trailingStop > trade.getTrailingStop()) {
                    trade.setTrailingStop(trailingStop);
                }
                if (trade.getTrailingStop() != null && currentPrice <= trade.getTrailingStop()) {
                    return "TRAILING_STOP";
                }
            }
            if (currentPrice > trade.getEntryPrice() * 1.01 && (trade.getBreakevenSet() == null || !trade.getBreakevenSet())) {
                trade.setStopLoss(trade.getEntryPrice() * 1.001);
                trade.setBreakevenSet(true);
            }
            if (holdMinutes > 240) {
                return "TIME_STOP";
            }
            return null;
        }
    }

    static class ExpansionPhaseStrategy extends Strategy {
        private final List<Map<String, Object>> targetInstruments = List.of(
            Map.of("symbol", "SPY", "type", InstrumentType.LEVERAGED_ETF, "volatility", 0.02),
            Map.of("symbol", "QQQ", "type", InstrumentType.LEVERAGED_ETF, "volatility", 0.025),
            Map.of("symbol", "TQQQ", "type", InstrumentType.LEVERAGED_ETF, "volatility", 0.07),
            Map.of("symbol", "SOXL", "type", InstrumentType.LEVERAGED_ETF, "volatility", 0.08),
            Map.of("symbol", "FAS", "type", InstrumentType.LEVERAGED_ETF, "volatility", 0.075),
            Map.of("symbol", "EURUSD", "type", InstrumentType.FOREX_MAJOR, "volatility", 0.01)
        );

        public ExpansionPhaseStrategy() {
            super(GrowthPhase.EXPANSION, 3, 0.15, 2.0);
        }

        @Override
        public Map<String, Object> findSetup(double capital) {
            Map<String, Object> instr = targetInstruments.get(RANDOM.nextInt(targetInstruments.size()));
            double entryPrice;
            double stopLossPct;
            if (instr.get("symbol").equals("EURUSD")) {
                entryPrice = 1.05 + RANDOM.nextDouble() * 0.1;
                stopLossPct = 0.005;
            } else {
                entryPrice = 100 + RANDOM.nextDouble() * 400;
                stopLossPct = 0.025;
            }
            double stopLoss = entryPrice * (1 - stopLossPct);
            double takeProfit = entryPrice * (1 + stopLossPct * targetMultiplier);
            double confidence = 0.55 + RANDOM.nextDouble() * 0.25;
            double riskAmount = capital * riskPerTrade;
            double positionValue = riskAmount / stopLossPct;
            double positionSize = positionValue / entryPrice;
            double leverage = Math.min(maxLeverage, 3);
            positionSize *= leverage;
            Map<String, Object> setup = new HashMap<>();
            setup.put("instrument", instr.get("symbol"));
            setup.put("instrument_type", instr.get("type"));
            setup.put("entry_price", entryPrice);
            setup.put("stop_loss", stopLoss);
            setup.put("take_profit", takeProfit);
            setup.put("confidence", confidence);
            setup.put("position_size", positionSize);
            setup.put("leverage", leverage);
            setup.put("risk_amount", riskAmount);
            setup.put("risk_percentage", riskPerTrade * 100);
            setup.put("expected_roi", stopLossPct * targetMultiplier * leverage * 100);
            setup.put("setup_quality", confidence * 100);
            setup.put("strategy", "SWING_TREND_FOLLOW");
            setup.put("timeframe", "1h");
            Map<String, Object> indicators = new HashMap<>();
            indicators.put("trend", "uptrend");
            indicators.put("support", entryPrice * 0.98);
            indicators.put("resistance", entryPrice * 1.05);
            indicators.put("market_regime", "risk_on");
            setup.put("indicators", indicators);
            return setup;
        }

        @Override
        public TradeRecord execute(Map<String, Object> setup) {
            TradeRecord trade = new TradeRecord();
            trade.setId("expansion_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")));
            trade.setPhase(phase);
            trade.setInstrument((String) setup.get("instrument"));
            trade.setInstrumentType((InstrumentType) setup.get("instrument_type"));
            trade.setEntryTime(LocalDateTime.now());
            trade.setEntryPrice((double) setup.get("entry_price"));
            trade.setPositionSize((double) setup.get("position_size"));
            trade.setLeverage((double) setup.get("leverage"));
            trade.setCapitalUsed((double) setup.get("position_size") * trade.getEntryPrice() / trade.getLeverage());
            trade.setRiskPercentage((double) setup.get("risk_percentage"));
            trade.setStrategy((String) setup.get("strategy"));
            trade.setSetupQuality((double) setup.get("setup_quality"));
            trades.add(trade);
            return trade;
        }

        @Override
        public String manageRisk(TradeRecord trade, double currentPrice) {
            long holdHours = Duration.between(trade.getEntryTime(), LocalDateTime.now()).toHours();
            if (currentPrice > trade.getEntryPrice() * 1.05) {
                double trailingStop = currentPrice * 0.98;
                if (trade.getTrailingStop() == null || trailingStop > trade.getTrailingStop()) {
                    trade.setTrailingStop(trailingStop);
                }
                if (trade.getTrailingStop() != null && currentPrice <= trade.getTrailingStop()) {
                    return "TRAILING_STOP";
                }
            }
            if (holdHours > 120) {
                return "TIME_STOP";
            }
            return null;
        }
    }

    static class ScalePhaseStrategy extends Strategy {
        private final List<Map<String, Object>> targetInstruments = List.of(
            Map.of("symbol", "SPY", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.015),
            Map.of("symbol", "QQQ", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.018),
            Map.of("symbol", "GLD", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.012),
            Map.of("symbol", "TLT", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.014),
            Map.of("symbol", "EEM", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.016)
        );

        public ScalePhaseStrategy() {
            super(GrowthPhase.SCALE, 2, 0.12, 1.8);
        }

        @Override
        public Map<String, Object> findSetup(double capital) {
            Map<String, Object> instr = targetInstruments.get(RANDOM.nextInt(targetInstruments.size()));
            double entryPrice = 50 + RANDOM.nextDouble() * 450;
            double stopLossPct = 0.03;
            double stopLoss = entryPrice * (1 - stopLossPct);
            double takeProfit = entryPrice * (1 + stopLossPct * targetMultiplier);
            double confidence = 0.5 + RANDOM.nextDouble() * 0.25;
            double riskAmount = capital * riskPerTrade;
            double positionValue = riskAmount / stopLossPct;
            double positionSize = positionValue / entryPrice;
            double leverage = Math.min(maxLeverage, 2);
            positionSize *= leverage;
            Map<String, Object> setup = new HashMap<>();
            setup.put("instrument", instr.get("symbol"));
            setup.put("instrument_type", instr.get("type"));
            setup.put("entry_price", entryPrice);
            setup.put("stop_loss", stopLoss);
            setup.put("take_profit", takeProfit);
            setup.put("confidence", confidence);
            setup.put("position_size", positionSize);
            setup.put("leverage", leverage);
            setup.put("risk_amount", riskAmount);
            setup.put("risk_percentage", riskPerTrade * 100);
            setup.put("expected_roi", stopLossPct * targetMultiplier * leverage * 100);
            setup.put("setup_quality", confidence * 100);
            setup.put("strategy", "POSITION_CORE_SATELLITE");
            setup.put("timeframe", "daily");
            Map<String, Double> allocation = new HashMap<>();
            allocation.put("core", 0.7);
            allocation.put("satellite", 0.3);
            setup.put("allocation", allocation);
            return setup;
        }

        @Override
        public TradeRecord execute(Map<String, Object> setup) {
            TradeRecord trade = new TradeRecord();
            trade.setId("scale_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")));
            trade.setPhase(phase);
            trade.setInstrument((String) setup.get("instrument"));
            trade.setInstrumentType((InstrumentType) setup.get("instrument_type"));
            trade.setEntryTime(LocalDateTime.now());
            trade.setEntryPrice((double) setup.get("entry_price"));
            trade.setPositionSize((double) setup.get("position_size"));
            trade.setLeverage((double) setup.get("leverage"));
            trade.setCapitalUsed((double) setup.get("position_size") * trade.getEntryPrice() / trade.getLeverage());
            trade.setRiskPercentage((double) setup.get("risk_percentage"));
            trade.setStrategy((String) setup.get("strategy"));
            trade.setSetupQuality((double) setup.get("setup_quality"));
            trades.add(trade);
            return trade;
        }

        @Override
        public String manageRisk(TradeRecord trade, double currentPrice) {
            long holdDays = Duration.between(trade.getEntryTime(), LocalDateTime.now()).toDays();
            if (currentPrice > trade.getEntryPrice() * 1.08) {
                double trailingStop = currentPrice * 0.95;
                if (trade.getTrailingStop() == null || trailingStop > trade.getTrailingStop()) {
                    trade.setTrailingStop(trailingStop);
                }
                if (trade.getTrailingStop() != null && currentPrice <= trade.getTrailingStop()) {
                    return "TRAILING_STOP";
                }
            }
            if (holdDays > 28) {
                return "TIME_STOP";
            }
            return null;
        }
    }

    static class HyperPhaseStrategy extends Strategy {
        private final List<Map<String, Object>> targetInstruments = List.of(
            Map.of("symbol", "SPY", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.015),
            Map.of("symbol", "QQQ", "type", InstrumentType.STOCK_BLUE_CHIP, "volatility", 0.018),
            Map.of("symbol", "BTCUSDT", "type", InstrumentType.CRYPTO_MAJOR, "volatility", 0.04),
            Map.of("symbol", "GC=F", "type", InstrumentType.FUTURES, "volatility", 0.012),
            Map.of("symbol", "CL=F", "type", InstrumentType.FUTURES, "volatility", 0.025),
            Map.of("symbol", "6E=F", "type", InstrumentType.FUTURES, "volatility", 0.01)
        );

        public HyperPhaseStrategy() {
            super(GrowthPhase.HYPER, 1.5, 0.10, 1.5);
        }

        @Override
        public Map<String, Object> findSetup(double capital) {
            Map<String, Object> instr = targetInstruments.get(RANDOM.nextInt(targetInstruments.size()));
            double entryPrice = RANDOM.nextDouble() * (50000 - 1) + 1;
            double stopLossPct = 0.04;
            double stopLoss = entryPrice * (1 - stopLossPct);
            double takeProfit = entryPrice * (1 + stopLossPct * targetMultiplier);
            double confidence = 0.45 + RANDOM.nextDouble() * 0.25;
            double riskAmount = capital * riskPerTrade;
            double positionValue = riskAmount / stopLossPct;
            double positionSize = positionValue / entryPrice;
            double leverage = Math.min(maxLeverage, 1.5);
            positionSize *= leverage;
            Map<String, Object> setup = new HashMap<>();
            setup.put("instrument", instr.get("symbol"));
            setup.put("instrument_type", instr.get("type"));
            setup.put("entry_price", entryPrice);
            setup.put("stop_loss", stopLoss);
            setup.put("take_profit", takeProfit);
            setup.put("confidence", confidence);
            setup.put("position_size", positionSize);
            setup.put("leverage", leverage);
            setup.put("risk_amount", riskAmount);
            setup.put("risk_percentage", riskPerTrade * 100);
            setup.put("expected_roi", stopLossPct * targetMultiplier * leverage * 100);
            setup.put("setup_quality", confidence * 100);
            setup.put("strategy", "MACRO_TREND");
            setup.put("timeframe", "weekly");
            Map<String, String> macroFactors = new HashMap<>();
            macroFactors.put("interest_rates", "neutral");
            macroFactors.put("inflation", "moderate");
            macroFactors.put("gdp_growth", "positive");
            macroFactors.put("geopolitical", "stable");
            setup.put("macro_factors", macroFactors);
            return setup;
        }

        @Override
        public TradeRecord execute(Map<String, Object> setup) {
            TradeRecord trade = new TradeRecord();
            trade.setId("hyper_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")));
            trade.setPhase(phase);
            trade.setInstrument((String) setup.get("instrument"));
            trade.setInstrumentType((InstrumentType) setup.get("instrument_type"));
            trade.setEntryTime(LocalDateTime.now());
            trade.setEntryPrice((double) setup.get("entry_price"));
            trade.setPositionSize((double) setup.get("position_size"));
            trade.setLeverage((double) setup.get("leverage"));
            trade.setCapitalUsed((double) setup.get("position_size") * trade.getEntryPrice() / trade.getLeverage());
            trade.setRiskPercentage((double) setup.get("risk_percentage"));
            trade.setStrategy((String) setup.get("strategy"));
            trade.setSetupQuality((double) setup.get("setup_quality"));
            trades.add(trade);
            return trade;
        }

        @Override
        public String manageRisk(TradeRecord trade, double currentPrice) {
            long holdDays = Duration.between(trade.getEntryTime(), LocalDateTime.now()).toDays();
            if (currentPrice > trade.getEntryPrice() * 1.10) {
                double trailingStop = currentPrice * 0.93;
                if (trade.getTrailingStop() == null || trailingStop > trade.getTrailingStop()) {
                    trade.setTrailingStop(trailingStop);
                }
                if (trade.getTrailingStop() != null && currentPrice <= trade.getTrailingStop()) {
                    return "TRAILING_STOP";
                }
            } else {
                if (trade.getStopLoss() != null && currentPrice <= trade.getStopLoss()) {
                    return "STOP_LOSS";
                }
            }
            if (holdDays > 90) {
                return "TIME_STOP";
            }
            return null;
        }
    }

    // ========================================================================
    // SIMULADOR DE MERCADO
    // ========================================================================

    static class MarketSimulator {
        private Map<String, Double> prices = new HashMap<>();
        private Map<String, Double> volatility = new HashMap<>();

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
            // ETFs alavancados
            prices.put("TQQQ", 62.0);
            prices.put("SOXL", 43.0);
            prices.put("FAS", 122.0);
            // Forex
            prices.put("EURUSD", 1.0875);
            // Futures
            prices.put("GC=F", 2350.0);
            prices.put("CL=F", 82.0);
            prices.put("6E=F", 1.0875);

            // Volatilidades
            for (String sym : prices.keySet()) {
                if (sym.contains("PEPE") || sym.contains("BONK")) {
                    volatility.put(sym, 0.15);
                } else if (sym.contains("BTC") || sym.contains("ETH")) {
                    volatility.put(sym, 0.08);
                } else if (sym.contains("TQQQ") || sym.contains("SOXL")) {
                    volatility.put(sym, 0.07);
                } else if (sym.startsWith("NVDA") || sym.startsWith("AMD")) {
                    volatility.put(sym, 0.05);
                } else {
                    volatility.put(sym, 0.03);
                }
            }
        }

        public double getPrice(String symbol) {
            if (!prices.containsKey(symbol)) {
                return 1 + RANDOM.nextDouble() * 99;
            }
            double basePrice = prices.get(symbol);
            double vol = volatility.getOrDefault(symbol, 0.03);
            double dt = 1.0 / 252;
            double drift = 0.0001;
            double shock = RANDOM.nextGaussian() * vol * Math.sqrt(dt);
            double price = basePrice * Math.exp(drift + shock);
            prices.put(symbol, price);
            return price;
        }

        public double[] getBidAsk(String symbol) {
            double price = getPrice(symbol);
            double spreadPct;
            if (symbol.contains("PEPE") || symbol.contains("BONK")) {
                spreadPct = 0.001;
            } else if (symbol.contains("BTC") || symbol.contains("ETH")) {
                spreadPct = 0.0005;
            } else {
                spreadPct = 0.0002;
            }
            double bid = price * (1 - spreadPct / 2);
            double ask = price * (1 + spreadPct / 2);
            return new double[]{bid, ask};
        }
    }

    // ========================================================================
    // ESTRATÉGIA PRINCIPAL - VHALINOR HYPERGROWTH
    // ========================================================================

    static class VhalinorHyperGrowthStrategy {
        private final double initialCapital = INITIAL_CAPITAL;
        private final double targetCapital = TARGET_CAPITAL;
        private final LocalDateTime startDate = LocalDateTime.now();
        private final Map<GrowthPhase, Strategy> strategies = new EnumMap<>(GrowthPhase.class);
        private PerformanceMetrics metrics = new PerformanceMetrics();
        private Map<String, TradeRecord> openTrades = new HashMap<>();
        private List<TradeRecord> tradeHistory = new ArrayList<>();
        private MarketSimulator market = new MarketSimulator();
        private volatile boolean isRunning = false;
        private int dailyTradeCount = 0;
        private LocalDate lastResetDate = LocalDate.now();
        private Logger logger = setupLogging();
        private List<Map<String, Object>> reports = new ArrayList<>();

        public VhalinorHyperGrowthStrategy() {
            strategies.put(GrowthPhase.SEED, new SeedPhaseStrategy());
            strategies.put(GrowthPhase.SPROUT, new SproutPhaseStrategy());
            strategies.put(GrowthPhase.GROWTH, new GrowthPhaseStrategyImpl());
            strategies.put(GrowthPhase.EXPANSION, new ExpansionPhaseStrategy());
            strategies.put(GrowthPhase.SCALE, new ScalePhaseStrategy());
            strategies.put(GrowthPhase.HYPER, new HyperPhaseStrategy());
        }

        private Logger setupLogging() {
            Logger logger = Logger.getLogger("VHALINOR_HyperGrowth");
            logger.setLevel(Level.INFO);
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format("[%1$tF %1$tT] %2$s %3$s%n", new Date(lr.getMillis()), lr.getLevel(), lr.getMessage());
                }
            });
            logger.addHandler(handler);
            // File handler opcional
            try {
                FileHandler fh = new FileHandler("vhalinor_hypergrowth.log", 10_000_000, 5, true);
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            } catch (IOException e) {
                logger.warning("Não foi possível criar arquivo de log: " + e.getMessage());
            }
            return logger;
        }

        public void start() {
            isRunning = true;
            logger.info("================================================================================");
            logger.info("🚀 VHALINOR HYPERGROWTH STRATEGY INICIADA");
            logger.info(String.format("💰 Capital Inicial: $%,.2f", initialCapital));
            logger.info(String.format("🎯 Meta: $%,.2f", targetCapital));
            logger.info("⏱️  Horizonte: " + TIME_HORIZON_DAYS + " dias");
            logger.info(String.format("📈 Crescimento Diário Necessário: %.2f%%", REQUIRED_DAILY_GROWTH * 100));
            logger.info("================================================================================");

            int day = 1;
            while (isRunning && metrics.currentCapital < targetCapital) {
                int daysElapsed = (int) Duration.between(startDate, LocalDateTime.now()).toDays();
                metrics.daysElapsed = daysElapsed;
                if (daysElapsed > TIME_HORIZON_DAYS) {
                    logger.severe("❌ PRAZO EXCEDIDO! Estratégia falhou.");
                    generateFinalReport();
                    break;
                }
                // Reset diário
                LocalDate today = LocalDate.now();
                if (!today.equals(lastResetDate)) {
                    dailyTradeCount = 0;
                    lastResetDate = today;
                    metrics.tradesToday = 0;
                    dailyReport(day);
                    day++;
                }
                tradingCycle();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            generateFinalReport();
        }

        private void tradingCycle() {
            try {
                Strategy currentStrategy = strategies.get(metrics.currentPhase);
                if (dailyTradeCount >= metrics.currentPhase.getMaxDailyTrades()) {
                    return;
                }
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
                logger.log(Level.SEVERE, "Erro no ciclo de trading: " + e.getMessage(), e);
            }
        }

        private void simulateTradeOutcome(TradeRecord trade) {
            double winProbability = trade.getSetupQuality() / 100.0;
            double phaseAdjustment = switch (trade.getPhase()) {
                case SEED -> 0.55;
                case SPROUT -> 0.58;
                case GROWTH -> 0.60;
                case EXPANSION -> 0.62;
                case SCALE -> 0.65;
                case HYPER -> 0.68;
            };
            double finalProbability = winProbability * 0.7 + phaseAdjustment * 0.3;
            boolean isWin = RANDOM.nextDouble() < finalProbability;

            double holdMinutes = trade.getInstrumentType().getAvgHoldTimeMinutes() * (0.8 + RANDOM.nextDouble() * 0.4);
            double exitPrice;
            String exitReason;
            if (isWin) {
                double stopLossPct = Math.abs(trade.getEntryPrice() - trade.getStopLoss() != null ? trade.getStopLoss() : trade.getEntryPrice() * 0.9) / trade.getEntryPrice(); // fallback
                exitPrice = trade.getEntryPrice() * (1 + stopLossPct * metrics.currentPhase.getTargetMultiplier());
                exitReason = "TAKE_PROFIT";
            } else {
                exitPrice = trade.getStopLoss() != null ? trade.getStopLoss() : trade.getEntryPrice() * 0.95;
                exitReason = "STOP_LOSS";
            }
            // slippage
            exitPrice *= (0.999 + RANDOM.nextDouble() * 0.002);

            double pnl;
            if (trade.getPositionSize() > 0) {
                pnl = (exitPrice - trade.getEntryPrice()) * trade.getPositionSize();
            } else {
                pnl = (trade.getEntryPrice() - exitPrice) * Math.abs(trade.getPositionSize());
            }
            pnl *= trade.getLeverage();
            double fees = trade.getCapitalUsed() * 0.001 * 2;
            pnl -= fees;
            trade.setPnl(pnl);
            trade.setFees(fees);
            trade.setExitPrice(exitPrice);
            trade.setExitTime(trade.getEntryTime().plusMinutes((long) holdMinutes));
            trade.setPnlPercentage((pnl / trade.getCapitalUsed()) * 100);
            trade.setExitReason(exitReason);
        }

        private void logTrade(TradeRecord trade) {
            String emoji = trade.isWin() ? "🟢" : "🔴";
            logger.info(String.format("%s TRADE | %s | %s | P&L: $%.2f (%+.2f%%) | Capital: $%,.2f | Fase: %s",
                    emoji, trade.getInstrument(), trade.getStrategy(),
                    trade.getPnl(), trade.getPnlPercentage(),
                    metrics.currentCapital, trade.getPhase().name()));
        }

        private void dailyReport(int day) {
            List<Double> equity = equityCurve();
            double dailyReturn = 0;
            if (equity.size() > 1) {
                double prevCapital = equity.get(equity.size() - 2);
                dailyReturn = (metrics.currentCapital / prevCapital) - 1;
            }
            metrics.dailyGrowthRate = dailyReturn;
            metrics.onTrack = dailyReturn >= REQUIRED_DAILY_GROWTH * 0.7;
            if (dailyReturn > 0) {
                metrics.estimatedDaysToTarget = (int) (Math.log(targetCapital / metrics.currentCapital) / Math.log(1 + dailyReturn));
            } else {
                metrics.estimatedDaysToTarget = TIME_HORIZON_DAYS - metrics.daysElapsed;
            }

            StringBuilder report = new StringBuilder();
            report.append("\n" + "=".repeat(80) + "\n");
            report.append(String.format("📊 RELATÓRIO DIÁRIO - DIA %d\n", day));
            report.append("=".repeat(80) + "\n");
            report.append(String.format("💰 Capital: $%,.2f\n", metrics.currentCapital));
            report.append(String.format("📈 Progresso: %.6f%%\n", metrics.progressToTarget * 100));
            report.append(String.format("🎯 Meta: $%,.2f\n", targetCapital));
            report.append(String.format("⚡ Fase Atual: %s - %s\n", metrics.currentPhase.name(), metrics.currentPhase.getDescription()));
            report.append("\n📊 MÉTRICAS DE PERFORMANCE:\n");
            report.append(String.format("├─ Total Trades: %d\n", metrics.totalTrades));
            report.append(String.format("├─ Wins/Losses: %d/%d\n", metrics.winningTrades, metrics.losingTrades));
            report.append(String.format("├─ Win Rate: %.2f%%\n", metrics.winRate * 100));
            report.append(String.format("├─ Profit Factor: %.2f\n", metrics.profitFactor));
            report.append(String.format("├─ Avg Win: $%.2f\n", metrics.avgWin));
            report.append(String.format("├─ Avg Loss: $%.2f\n", metrics.avgLoss));
            report.append(String.format("└─ Max Drawdown: %.2f%%\n", metrics.maxDrawdown * 100));
            report.append("\n📈 CRESCIMENTO:\n");
            report.append(String.format("├─ Crescimento Hoje: %.2f%%\n", dailyReturn * 100));
            report.append(String.format("├─ Necessário: %.2f%%\n", REQUIRED_DAILY_GROWTH * 100));
            report.append(String.format("├─ On Track: %s\n", metrics.onTrack ? "✅" : "❌"));
            report.append(String.format("└─ Dias Estimados até Meta: %d\n", metrics.estimatedDaysToTarget));
            report.append("\n🎯 ESTRATÉGIA ATUAL:\n");
            report.append(String.format("├─ Risco por Trade: %.0f%%\n", metrics.currentPhase.getRiskPerTrade() * 100));
            report.append(String.format("├─ Trades/Dia: %d/%d\n", dailyTradeCount, metrics.currentPhase.getMaxDailyTrades()));
            report.append(String.format("├─ Alavancagem Máx: %.0fx\n", strategies.get(metrics.currentPhase).getMaxLeverage()));
            report.append(String.format("└─ Target Multiplier: %.1fx\n", metrics.currentPhase.getTargetMultiplier()));
            report.append("=".repeat(80) + "\n");

            logger.info(report.toString());

            Map<String, Object> rep = new HashMap<>();
            rep.put("day", day);
            rep.put("timestamp", LocalDateTime.now().toString());
            rep.put("daily_return", dailyReturn);
            reports.add(rep);

            if (!metrics.onTrack) {
                logger.warning("⚠️ ABAIXO DA META! Aumentar frequência de trades.");
            }
            if (metrics.currentDrawdown > 0.15) {
                logger.severe(String.format("⚠️ DRAWDOWN ALTO: %.2f%%", metrics.currentDrawdown * 100));
            }
        }

        private void checkProgress() {
            int daysLeft = TIME_HORIZON_DAYS - metrics.daysElapsed;
            if (daysLeft <= 0) return;
            double requiredCapital = initialCapital * Math.pow(1 + REQUIRED_DAILY_GROWTH, metrics.daysElapsed);
            if (metrics.currentCapital < requiredCapital * 0.5) {
                logger.severe("❌ CRITICAL: Muito abaixo da meta! Aumentar risco agressivamente.");
                if (metrics.currentPhase == GrowthPhase.SEED) {
                    strategies.get(GrowthPhase.SEED).setRiskPerTrade(0.30);
                }
            } else if (metrics.currentCapital > requiredCapital * 1.5) {
                logger.info("✅ ACIMA DA META! Mantenha consistência.");
            }
        }

        private void generateFinalReport() {
            long totalDays = Duration.between(startDate, LocalDateTime.now()).toDays();
            double totalReturn = (metrics.currentCapital / initialCapital - 1) * 100;
            double annualizedReturn = Math.pow(metrics.currentCapital / initialCapital, 365.0 / Math.max(1, totalDays)) - 1;

            StringBuilder report = new StringBuilder();
            report.append("=".repeat(80) + "\n");
            report.append("🏆 VHALINOR HYPERGROWTH - RELATÓRIO FINAL\n");
            report.append("=".repeat(80) + "\n\n");
            report.append("📋 RESUMO EXECUTIVO:\n");
            report.append(String.format("├─ Capital Inicial: $%,.2f\n", initialCapital));
            report.append(String.format("├─ Capital Final: $%,.2f\n", metrics.currentCapital));
            report.append(String.format("├─ Retorno Total: %,.2f%%\n", totalReturn));
            report.append(String.format("├─ Dias Totais: %d\n", totalDays));
            report.append(String.format("├─ Retorno Anualizado: %.2f%%\n", annualizedReturn * 100));
            report.append(String.format("└─ Meta Atingida: %s\n", metrics.currentCapital >= targetCapital ? "✅ SIM" : "❌ NÃO"));

            report.append("\n📊 ESTATÍSTICAS DE TRADING:\n");
            report.append(String.format("├─ Total de Trades: %d\n", metrics.totalTrades));
            report.append(String.format("├─ Trades Vencedores: %d\n", metrics.winningTrades));
            report.append(String.format("├─ Trades Perdedores: %d\n", metrics.losingTrades));
            report.append(String.format("├─ Win Rate: %.2f%%\n", metrics.winRate * 100));
            report.append(String.format("├─ Profit Factor: %.2f\n", metrics.profitFactor));
            report.append(String.format("├─ Avg Win: $%,.2f\n", metrics.avgWin));
            report.append(String.format("├─ Avg Loss: $%,.2f\n", metrics.avgLoss));
            report.append(String.format("└─ Sharpe Ratio: %.2f\n", metrics.sharpeRatio));

            report.append("\n📈 ANÁLISE DE RISCO:\n");
            report.append(String.format("├─ Máximo Drawdown: %.2f%%\n", metrics.maxDrawdown * 100));
            report.append(String.format("├─ Drawdown Final: %.2f%%\n", metrics.currentDrawdown * 100));
            report.append(String.format("├─ Risco Médio por Trade: %.2f%%\n", metrics.avgRiskPerTrade));
            double avgLev = tradeHistory.stream().mapToDouble(TradeRecord::getLeverage).average().orElse(0);
            report.append(String.format("├─ Alavancagem Média: %.2fx\n", avgLev));
            report.append(String.format("└─ Tempo Médio em Posição: %.0f min\n", metrics.avgHoldingMinutes));

            report.append("\n🎯 PERFORMANCE POR FASE:\n");
            for (GrowthPhase phase : GrowthPhase.values()) {
                List<TradeRecord> phaseTrades = tradeHistory.stream().filter(t -> t.getPhase() == phase).toList();
                if (!phaseTrades.isEmpty()) {
                    long phaseWins = phaseTrades.stream().filter(TradeRecord::isWin).count();
                    double phasePnl = phaseTrades.stream().mapToDouble(TradeRecord::getPnl).sum();
                    report.append(String.format("%s:\n", phase.name()));
                    report.append(String.format("├─ Trades: %d\n", phaseTrades.size()));
                    report.append(String.format("├─ Wins: %d\n", phaseWins));
                    report.append(String.format("├─ Win Rate: %.2f%%\n", (double) phaseWins / phaseTrades.size() * 100));
                    report.append(String.format("├─ P&L Total: $%,.2f\n", phasePnl));
                    report.append("└─ Capital Fim: $");
                    report.append(String.format("%,.2f\n", (phase == GrowthPhase.HYPER ? metrics.currentCapital : phase.getMaxCapital())));
                }
            }

            report.append("=".repeat(80) + "\n");
            report.append("🤖 VHALINOR HYPERGROWTH - ESTRATÉGIA CONCLUÍDA\n");
            report.append("=".repeat(80));

            logger.info(report.toString());

            // Salva relatório em JSON (simplificado)
            try (PrintWriter pw = new PrintWriter(new FileWriter("hypergrowth_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json"))) {
                pw.println("{ \"final_capital\": " + metrics.currentCapital + ", \"total_return\": " + totalReturn + ", ... }");
            } catch (IOException e) {
                logger.warning("Falha ao salvar relatório: " + e.getMessage());
            }
        }

        private List<Double> equityCurve() {
            List<Double> curve = new ArrayList<>();
            curve.add(initialCapital);
            double cum = initialCapital;
            for (TradeRecord t : tradeHistory) {
                cum += t.getPnl();
                curve.add(cum);
            }
            return curve;
        }

        public void stop() {
            isRunning = false;
            logger.info("⏹️ Estratégia HyperGrowth parada.");
        }
    }

    // ========================================================================
    // MAIN
    // ========================================================================

    public static void main(String[] args) {
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
        System.out.println("Pressione ENTER para iniciar a estratégia...");
        try { System.in.read(); } catch (IOException e) {}

        VhalinorHyperGrowthStrategy strategy = new VhalinorHyperGrowthStrategy();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            strategy.stop();
            strategy.generateFinalReport();
        }));
        strategy.start();
    }
}
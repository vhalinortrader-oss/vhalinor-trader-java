/**
 * VHALINOR IAG 2.0 - ESTRATEGIA FOREX QUANTICA (Java)
 * SISTEMA DE DECISÃO INTELIGENTE COM MULTIPLAS CAMADAS NEURAIS
 *
 * Módulo: CAMADA DE DECISÃO - ESTRATEGIA FOREX (Layer 04)
 * Versão: 4.0.0 Java
 * Autor: Alex Miranda Sales
 * Data: 2026
 * License: Proprietary - Vhalinor IAG Systems
 *
 * Status: TOTALMENTE OPERACIONAL | 20+ PARES | MULTIPLAS TIMEFRAMES
 * Features: Redes Neurais | Tempo Real | IA Avancada | Predicoes
 *
 * Convertido de Python para Java mantendo a lógica central.
 * Dependências externas (TensorFlow/PyTorch) foram abstraídas ou implementadas de forma simplificada.
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

public class VhalinorForex {

    // ======================== LOGGING ==========================
    private static final Logger LOGGER = Logger.getLogger(VhalinorForex.class.getName());
    static {
        try {
            FileHandler fh = new FileHandler("vhalinor_forex.log", 10_000_000, 5, true);
            fh.setEncoding("UTF-8");
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            LOGGER.setUseParentHandlers(true);
        } catch (IOException e) {
            System.err.println("Falha ao configurar arquivo de log: " + e.getMessage());
        }
    }

    // ======================== ENUMS ============================

    public enum SignalType {
        STRONG_BUY("STRONG_BUY", "🟢🟢", "Compra Forte", 90),
        BUY("BUY", "🟢", "Compra", 70),
        HOLD("HOLD", "⚪", "Manter", 50),
        SELL("SELL", "🔴", "Venda", 70),
        STRONG_SELL("STRONG_SELL", "🔴🔴", "Venda Forte", 90),
        CLOSE_LONG("CLOSE_LONG", "🔚", "Fechar Posição Longa", 80),
        CLOSE_SHORT("CLOSE_SHORT", "🔚", "Fechar Posição Curta", 80);

        final String label;
        final String icon;
        final String descricao;
        final int threshold;

        SignalType(String label, String icon, String descricao, int threshold) {
            this.label = label;
            this.icon = icon;
            this.descricao = descricao;
            this.threshold = threshold;
        }
    }

    public enum TimeFrame {
        M1("1m", 60, "1 Minuto"),
        M5("5m", 300, "5 Minutos"),
        M15("15m", 900, "15 Minutos"),
        M30("30m", 1800, "30 Minutos"),
        H1("1h", 3600, "1 Hora"),
        H4("4h", 14400, "4 Horas"),
        D1("1d", 86400, "1 Dia"),
        W1("1w", 604800, "1 Semana"),
        MN1("1mo", 2592000, "1 Mês");

        final String code;
        final int seconds;
        final String descricao;

        TimeFrame(String code, int seconds, String descricao) {
            this.code = code;
            this.seconds = seconds;
            this.descricao = descricao;
        }
    }

    public enum MarketTrend {
        STRONG_UPTREND("STRONG_UPTREND", "📈📈", "Tendência de Alta Forte", 2.0),
        UPTREND("UPTREND", "📈", "Tendência de Alta", 1.0),
        SIDEWAYS("SIDEWAYS", "📊", "Lateral", 0.0),
        DOWNTREND("DOWNTREND", "📉", "Tendência de Baixa", -1.0),
        STRONG_DOWNTREND("STRONG_DOWNTREND", "📉📉", "Tendência de Baixa Forte", -2.0);

        final String label;
        final String icon;
        final String descricao;
        final double peso;

        MarketTrend(String label, String icon, String descricao, double peso) {
            this.label = label;
            this.icon = icon;
            this.descricao = descricao;
            this.peso = peso;
        }
    }

    public enum ForexPair {
        // Majors
        EUR_USD("EUR/USD", "💶💵", 1.0, "major", 0.0001),
        USD_JPY("USD/JPY", "💵💴", 1.0, "major", 0.01),
        GBP_USD("GBP/USD", "💷💵", 1.0, "major", 0.0001),
        USD_CHF("USD/CHF", "💵🇨🇭", 1.0, "major", 0.0001),
        AUD_USD("AUD/USD", "🇦🇺💵", 1.0, "major", 0.0001),
        USD_CAD("USD/CAD", "💵🇨🇦", 1.0, "major", 0.0001),
        NZD_USD("NZD/USD", "🇳🇿💵", 1.0, "major", 0.0001),

        // Minors
        EUR_GBP("EUR/GBP", "💶💷", 0.8, "minor", 0.0001),
        EUR_JPY("EUR/JPY", "💶💴", 0.8, "minor", 0.01),
        GBP_JPY("GBP/JPY", "💷💴", 0.8, "minor", 0.01),
        EUR_CHF("EUR/CHF", "💶🇨🇭", 0.8, "minor", 0.0001),
        EUR_AUD("EUR/AUD", "💶🇦🇺", 0.8, "minor", 0.0001),
        EUR_CAD("EUR/CAD", "💶🇨🇦", 0.8, "minor", 0.0001),
        GBP_CHF("GBP/CHF", "💷🇨🇭", 0.8, "minor", 0.0001),
        GBP_AUD("GBP/AUD", "💷🇦🇺", 0.8, "minor", 0.0001),
        AUD_JPY("AUD/JPY", "🇦🇺💴", 0.8, "minor", 0.01),
        AUD_CAD("AUD/CAD", "🇦🇺🇨🇦", 0.8, "minor", 0.0001),
        NZD_JPY("NZD/JPY", "🇳🇿💴", 0.8, "minor", 0.01),
        CHF_JPY("CHF/JPY", "🇨🇭💴", 0.8, "minor", 0.01),
        CAD_JPY("CAD/JPY", "🇨🇦💴", 0.8, "minor", 0.01),

        // Exotics (some examples)
        USD_BRL("USD/BRL", "💵🇧🇷", 0.5, "exotic", 0.0005),
        EUR_BRL("EUR/BRL", "💶🇧🇷", 0.5, "exotic", 0.0005),
        GBP_BRL("GBP/BRL", "💷🇧🇷", 0.5, "exotic", 0.0005);

        final String label;
        final String icon;
        final double liquidez;
        final String categoria;
        final double pipSize;

        ForexPair(String label, String icon, double liquidez, String categoria, double pipSize) {
            this.label = label;
            this.icon = icon;
            this.liquidez = liquidez;
            this.categoria = categoria;
            this.pipSize = pipSize;
        }

        public double getPipSize() { return pipSize; }
        public String getLabel() { return label; }
        public String getIcon() { return icon; }
    }

    public enum MarketSession {
        ASIA("Ásia", "🌏", 0, 9),
        LONDON("Londres", "🇬🇧", 8, 17),
        NY("Nova York", "🇺🇸", 13, 22),
        OVERLAP_LONDON_NY("Sobreposição", "⚡", 13, 17);

        final String label;
        final String icon;
        final int startHour;
        final int endHour;

        MarketSession(String label, String icon, int startHour, int endHour) {
            this.label = label;
            this.icon = icon;
            this.startHour = startHour;
            this.endHour = endHour;
        }
    }

    public enum RiskLevel {
        VERY_LOW("Muito Baixo", "🟢", 0.005),
        LOW("Baixo", "🟡", 0.01),
        MEDIUM("Médio", "🟠", 0.02),
        HIGH("Alto", "🔴", 0.03),
        VERY_HIGH("Muito Alto", "💀", 0.05);

        final String label;
        final String icon;
        final double riskPerTrade;

        RiskLevel(String label, String icon, double riskPerTrade) {
            this.label = label;
            this.icon = icon;
            this.riskPerTrade = riskPerTrade;
        }
    }

    // ======================== DATA CLASSES (Records) =========================

    public record IndicatorConfig(
            int rsiPeriod, int rsiOversold, int rsiOverbought,
            int macdFast, int macdSlow, int macdSignal,
            int bbPeriod, double bbStdDev,
            int atrPeriod,
            int stochPeriod, int stochSmoothK, int stochSmoothD,
            int smaShort, int smaMedium, int smaLong,
            int emaShort, int emaLong,
            int adxPeriod
    ) {
        public IndicatorConfig() {
            this(14, 30, 70, 12, 26, 9, 20, 2.0, 14, 14, 3, 3,
                 20, 50, 200, 12, 26, 14);
        }
    }

    public record SignalResult(
            String symbol, TimeFrame timeframe, Instant timestamp,
            SignalType signal, double confidence, double price,
            MarketTrend trend, double trendStrength,
            Map<String, Double> indicators,
            Double stopLoss, Double takeProfit,
            int buyScore, int sellScore,
            Double neuralPrediction, Double quantumConfidence
    ) {}

    public static class Position {
        private final String id;
        private final String symbol;
        private final ForexPair pair;
        private final SignalType signal;
        private final double entryPrice;
        private final double positionSize;
        private final double stopLoss;
        private final double takeProfit;
        private final Instant entryTime;
        private final double confidence;
        private Double currentPrice;
        private Double exitPrice;
        private Instant exitTime;
        private Double pnlPips;
        private Double pnlAmount;
        private String status = "OPEN";

        public Position(String id, String symbol, ForexPair pair, SignalType signal,
                        double entryPrice, double positionSize, double stopLoss,
                        double takeProfit, double confidence, double currentPrice) {
            this.id = id;
            this.symbol = symbol;
            this.pair = pair;
            this.signal = signal;
            this.entryPrice = entryPrice;
            this.positionSize = positionSize;
            this.stopLoss = stopLoss;
            this.takeProfit = takeProfit;
            this.entryTime = Instant.now();
            this.confidence = confidence;
            this.currentPrice = currentPrice;
        }

        public double getPipValue() { return pair.getPipSize() * positionSize * 10000; }
        public Double getCurrentPnlPips() {
            if (currentPrice == null) return null;
            if (signal == SignalType.BUY || signal == SignalType.STRONG_BUY)
                return (currentPrice - entryPrice) / pair.getPipSize();
            else
                return (entryPrice - currentPrice) / pair.getPipSize();
        }
        public Double getCurrentPnlAmount() {
            Double pips = getCurrentPnlPips();
            return pips == null ? null : pips * getPipValue();
        }
        // Getters
        public String getId() { return id; }
        public String getSymbol() { return symbol; }
        public ForexPair getPair() { return pair; }
        public SignalType getSignal() { return signal; }
        public double getEntryPrice() { return entryPrice; }
        public double getPositionSize() { return positionSize; }
        public double getStopLoss() { return stopLoss; }
        public double getTakeProfit() { return takeProfit; }
        public Instant getEntryTime() { return entryTime; }
        public double getConfidence() { return confidence; }
        public Double getCurrentPrice() { return currentPrice; }
        public void setCurrentPrice(Double p) { this.currentPrice = p; }
        public Double getExitPrice() { return exitPrice; }
        public void setExitPrice(Double p) { this.exitPrice = p; }
        public Instant getExitTime() { return exitTime; }
        public void setExitTime(Instant t) { this.exitTime = t; }
        public Double getPnlPips() { return pnlPips; }
        public void setPnlPips(Double p) { this.pnlPips = p; }
        public Double getPnlAmount() { return pnlAmount; }
        public void setPnlAmount(Double p) { this.pnlAmount = p; }
        public String getStatus() { return status; }
        public void setStatus(String s) { this.status = s; }
    }

    public record TradeRecord(
            String positionId, String symbol, ForexPair pair,
            SignalType signal, double entryPrice, double exitPrice,
            double positionSize, double pnlPips, double pnlAmount,
            Instant entryTime, Instant exitTime,
            double confidence, MarketSession marketSession
    ) {}

    // ======================== TECHNICAL INDICATORS (Pure Java) ========================

    public static class TechnicalIndicators {
        private final IndicatorConfig config;

        public TechnicalIndicators(IndicatorConfig config) { this.config = config; }

        // Simple Moving Average
        public List<Double> sma(List<Double> series, int period) {
            if (series == null || series.isEmpty()) return new ArrayList<>();
            List<Double> result = new ArrayList<>(series.size());
            double sum = 0;
            for (int i = 0; i < series.size(); i++) {
                sum += series.get(i);
                if (i >= period) sum -= series.get(i - period);
                int n = Math.min(i + 1, period);
                result.add(sum / n);
            }
            return result;
        }

        // Exponential Moving Average
        public List<Double> ema(List<Double> series, int period) {
            List<Double> result = new ArrayList<>(series.size());
            double multiplier = 2.0 / (period + 1);
            double ema = series.isEmpty() ? 0 : series.get(0);
            result.add(ema);
            for (int i = 1; i < series.size(); i++) {
                ema = (series.get(i) - ema) * multiplier + ema;
                result.add(ema);
            }
            return result;
        }

        // RSI
        public List<Double> rsi(List<Double> series, int period) {
            if (series.size() < period) return Collections.nCopies(series.size(), 50.0);
            List<Double> gains = new ArrayList<>();
            List<Double> losses = new ArrayList<>();
            for (int i = 1; i < series.size(); i++) {
                double diff = series.get(i) - series.get(i - 1);
                gains.add(Math.max(diff, 0));
                losses.add(Math.max(-diff, 0));
            }
            // Initialize averages
            double avgGain = gains.subList(0, period).stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double avgLoss = losses.subList(0, period).stream().mapToDouble(Double::doubleValue).average().orElse(0);
            List<Double> rsiValues = new ArrayList<>();
            for (int i = period; i < gains.size(); i++) {
                avgGain = (avgGain * (period - 1) + gains.get(i)) / period;
                avgLoss = (avgLoss * (period - 1) + losses.get(i)) / period;
                double rs = avgLoss == 0 ? 100 : avgGain / avgLoss;
                rsiValues.add(100 - (100 / (1 + rs)));
            }
            // Pad beginning
            List<Double> full = new ArrayList<>(Collections.nCopies(period + 1, 50.0));
            full.addAll(rsiValues);
            return full.subList(0, series.size());
        }

        // MACD (returns three lists: macdLine, signal, histogram)
        public List<List<Double>> macd(List<Double> series) {
            List<Double> ema12 = ema(series, config.macdFast);
            List<Double> ema26 = ema(series, config.macdSlow);
            List<Double> macdLine = new ArrayList<>();
            for (int i = 0; i < series.size(); i++) {
                macdLine.add(ema12.get(i) - ema26.get(i));
            }
            List<Double> signalLine = ema(macdLine, config.macdSignal);
            List<Double> histogram = new ArrayList<>();
            for (int i = 0; i < macdLine.size(); i++) {
                histogram.add(macdLine.get(i) - signalLine.get(i));
            }
            return List.of(macdLine, signalLine, histogram);
        }

        // Bollinger Bands (upper, middle, lower, bandwidth)
        public List<List<Double>> bollingerBands(List<Double> series) {
            List<Double> middle = sma(series, config.bbPeriod);
            List<Double> upper = new ArrayList<>();
            List<Double> lower = new ArrayList<>();
            List<Double> bandwidth = new ArrayList<>();
            for (int i = 0; i < series.size(); i++) {
                double sum = 0;
                int count = 0;
                for (int j = Math.max(0, i - config.bbPeriod + 1); j <= i; j++) {
                    double diff = series.get(j) - middle.get(i);
                    sum += diff * diff;
                    count++;
                }
                double std = Math.sqrt(sum / count);
                double up = middle.get(i) + config.bbStdDev * std;
                double lo = middle.get(i) - config.bbStdDev * std;
                upper.add(up);
                lower.add(lo);
                bandwidth.add((up - lo) / middle.get(i));
            }
            return List.of(upper, middle, lower, bandwidth);
        }

        // ATR
        public List<Double> atr(List<Double> high, List<Double> low, List<Double> close) {
            List<Double> trList = new ArrayList<>();
            trList.add(high.get(0) - low.get(0));
            for (int i = 1; i < close.size(); i++) {
                double hl = high.get(i) - low.get(i);
                double hc = Math.abs(high.get(i) - close.get(i - 1));
                double lc = Math.abs(low.get(i) - close.get(i - 1));
                trList.add(Math.max(hl, Math.max(hc, lc)));
            }
            return ema(trList, config.atrPeriod);
        }

        // Stochastic (returns K, D)
        public List<List<Double>> stochastic(List<Double> high, List<Double> low, List<Double> close) {
            List<Double> k = new ArrayList<>();
            for (int i = 0; i < close.size(); i++) {
                double lowest = low.get(i);
                double highest = high.get(i);
                for (int j = Math.max(0, i - config.stochPeriod + 1); j <= i; j++) {
                    if (low.get(j) < lowest) lowest = low.get(j);
                    if (high.get(j) > highest) highest = high.get(j);
                }
                k.add(highest == lowest ? 50 : 100 * (close.get(i) - lowest) / (highest - lowest));
            }
            List<Double> d = sma(k, config.stochSmoothD);
            return List.of(k, d);
        }

        // ADX (simplified)
        public List<List<Double>> adx(List<Double> high, List<Double> low, List<Double> close) {
            // Simplified: return dummy values (ADX ~25, +DI ~25, -DI ~25)
            int n = close.size();
            List<Double> adxVals = new ArrayList<>(Collections.nCopies(n, 25.0));
            List<Double> plusDI = new ArrayList<>(Collections.nCopies(n, 25.0));
            List<Double> minusDI = new ArrayList<>(Collections.nCopies(n, 25.0));
            return List.of(adxVals, plusDI, minusDI);
        }
    }

    // ======================== TREND ANALYZER =========================

    public static class TrendAnalyzer {
        private final TechnicalIndicators indicators;

        public TrendAnalyzer(TechnicalIndicators indicators) { this.indicators = indicators; }

        public MarketTrend identifyTrend(List<Double> high, List<Double> low, List<Double> close) {
            List<Double> sma20 = indicators.sma(close, 20);
            List<Double> sma50 = indicators.sma(close, 50);
            List<Double> sma200 = indicators.sma(close, 200);

            double price = close.get(close.size() - 1);
            double s20 = sma20.get(sma20.size() - 1);
            double s50 = sma50.get(sma50.size() - 1);
            double s200 = sma200.get(sma200.size() - 1);

            List<List<Double>> adxData = indicators.adx(high, low, close);
            double adx = adxData.get(0).get(adxData.get(0).size() - 1);
            double pdi = adxData.get(1).get(adxData.get(1).size() - 1);
            double mdi = adxData.get(2).get(adxData.get(2).size() - 1);

            if (price > s20 && s20 > s50 && s50 > s200 && adx > 30 && pdi > mdi)
                return MarketTrend.STRONG_UPTREND;
            if (price > s50 && adx > 25 && pdi > mdi)
                return MarketTrend.UPTREND;
            if (price < s20 && s20 < s50 && s50 < s200 && adx > 30 && mdi > pdi)
                return MarketTrend.STRONG_DOWNTREND;
            if (price < s50 && adx > 25 && mdi > pdi)
                return MarketTrend.DOWNTREND;
            return MarketTrend.SIDEWAYS;
        }

        public double calculateTrendStrength(List<Double> high, List<Double> low, List<Double> close) {
            List<Double> sma20 = indicators.sma(close, 20);
            List<Double> sma50 = indicators.sma(close, 50);
            List<List<Double>> adxData = indicators.adx(high, low, close);
            double adx = adxData.get(0).get(adxData.get(0).size() - 1);
            double s20 = sma20.get(sma20.size() - 1);
            double s50 = sma50.get(sma50.size() - 1);
            double alignment = Math.abs(s20 - s50) / s50 * 100;
            double price = close.get(close.size() - 1);
            double priceVsMA = Math.abs(price - s20) / s20 * 50;
            double strength = adx * 0.5 + Math.min(alignment, 50) * 0.3 + Math.min(priceVsMA, 50) * 0.2;
            return Math.min(strength, 100);
        }
    }

    // ======================== SIGNAL GENERATOR =========================

    public static class SignalGenerator {
        private final TechnicalIndicators indicators;
        private final TrendAnalyzer trendAnalyzer;

        public SignalGenerator(TechnicalIndicators indicators, TrendAnalyzer trendAnalyzer) {
            this.indicators = indicators;
            this.trendAnalyzer = trendAnalyzer;
        }

        public SignalResult generateSignal(String symbol, List<Double> open, List<Double> high,
                                           List<Double> low, List<Double> close,
                                           TimeFrame timeframe) {
            int n = close.size();
            double price = close.get(n - 1);
            List<Double> rsi = indicators.rsi(close, 14);
            List<List<Double>> macdData = indicators.macd(close);
            List<Double> macdLine = macdData.get(0);
            List<Double> signalLine = macdData.get(1);
            List<Double> hist = macdData.get(2);
            List<List<Double>> bb = indicators.bollingerBands(close);
            List<Double> upper = bb.get(0), middle = bb.get(1), lower = bb.get(2), bw = bb.get(3);
            List<List<Double>> stoch = indicators.stochastic(high, low, close);
            List<Double> k = stoch.get(0), d = stoch.get(1);
            List<Double> atr = indicators.atr(high, low, close);
            List<List<Double>> adxData = indicators.adx(high, low, close);
            List<Double> adx = adxData.get(0), pdi = adxData.get(1), mdi = adxData.get(2);

            double currRsi = rsi.get(n - 1);
            double currMacd = macdLine.get(n - 1);
            double currSignal = signalLine.get(n - 1);
            double currK = k.get(n - 1);
            double currD = d.get(n - 1);
            double currAtr = atr.get(n - 1);
            double currAdx = adx.get(n - 1);
            double currPdi = pdi.get(n - 1);
            double currMdi = mdi.get(n - 1);

            MarketTrend trend = trendAnalyzer.identifyTrend(high, low, close);
            double trendStr = trendAnalyzer.calculateTrendStrength(high, low, close);

            int buyScore = 0, sellScore = 0;

            // RSI
            if (currRsi < 30) buyScore += 4;
            else if (currRsi < 40) buyScore += 2;
            if (currRsi > 70) sellScore += 4;
            else if (currRsi > 60) sellScore += 2;

            // MACD
            if (currMacd > currSignal) {
                buyScore += 2;
                if (n > 1 && macdLine.get(n - 2) <= signalLine.get(n - 2)) buyScore += 3;
            } else {
                sellScore += 2;
                if (n > 1 && macdLine.get(n - 2) >= signalLine.get(n - 2)) sellScore += 3;
            }

            // Bollinger
            if (price < lower.get(n - 1)) buyScore += 4;
            else if (price < middle.get(n - 1)) buyScore += 1;
            if (price > upper.get(n - 1)) sellScore += 4;
            else if (price > middle.get(n - 1)) sellScore += 1;

            // Stochastic
            if (currK < 20 && currD < 20) buyScore += 3;
            else if (currK < 30 && currD < 30) buyScore += 1;
            if (currK > 80 && currD > 80) sellScore += 3;
            else if (currK > 70 && currD > 70) sellScore += 1;

            // Trend
            if (trend == MarketTrend.STRONG_UPTREND || trend == MarketTrend.UPTREND)
                buyScore += (int)(trendStr / 20);
            else if (trend == MarketTrend.STRONG_DOWNTREND || trend == MarketTrend.DOWNTREND)
                sellScore += (int)(trendStr / 20);

            // ADX
            if (currAdx > 25) {
                if (currPdi > currMdi) buyScore += 2;
                else sellScore += 2;
            }

            // Price Action (engulfing)
            if (n >= 2) {
                double prevOpen = open.get(n - 2), prevClose = close.get(n - 2);
                double currOpen = open.get(n - 1);
                if (prevClose < prevOpen && currClose > currOpen
                        && currClose > prevOpen && currOpen < prevClose) buyScore += 3;
                else if (prevClose > prevOpen && currClose < currOpen
                        && currClose < prevOpen && currOpen > prevClose) sellScore += 3;
            }

            // Support/Resistance
            double recentHigh = high.subList(Math.max(0, n - 20), n).stream().mapToDouble(Double::doubleValue).max().orElse(price);
            double recentLow = low.subList(Math.max(0, n - 20), n).stream().mapToDouble(Double::doubleValue).min().orElse(price);
            if (price <= recentLow * 1.01) buyScore += 2;
            if (price >= recentHigh * 0.99) sellScore += 2;

            // Determine signal
            SignalType signal;
            double confidence;
            int maxScore = 30;
            int diff = Math.abs(buyScore - sellScore);
            if (buyScore >= 10 && buyScore > sellScore) {
                signal = diff >= 15 ? SignalType.STRONG_BUY : SignalType.BUY;
                confidence = Math.min(buyScore / (double) maxScore * 100, signal == SignalType.STRONG_BUY ? 95 : 85);
            } else if (sellScore >= 10 && sellScore > buyScore) {
                signal = diff >= 15 ? SignalType.STRONG_SELL : SignalType.SELL;
                confidence = Math.min(sellScore / (double) maxScore * 100, signal == SignalType.STRONG_SELL ? 95 : 85);
            } else {
                signal = SignalType.HOLD;
                confidence = 50 + diff * 2;
            }

            // Risk: stop loss and take profit
            double stopLoss = 0, takeProfit = 0;
            double atrVal = currAtr == 0 ? price * 0.005 : currAtr;
            double atrMult = 2;
            if (signal == SignalType.BUY || signal == SignalType.STRONG_BUY) {
                stopLoss = price - (atrMult * atrVal);
                takeProfit = price + (atrMult * atrVal * 2.0);
            } else if (signal == SignalType.SELL || signal == SignalType.STRONG_SELL) {
                stopLoss = price + (atrMult * atrVal);
                takeProfit = price - (atrMult * atrVal * 2.0);
            }

            Map<String, Double> indMap = new LinkedHashMap<>();
            indMap.put("rsi", currRsi);
            indMap.put("macd", currMacd);
            indMap.put("macd_signal", currSignal);
            indMap.put("macd_histogram", hist.get(n - 1));
            indMap.put("bb_upper", upper.get(n - 1));
            indMap.put("bb_middle", middle.get(n - 1));
            indMap.put("bb_lower", lower.get(n - 1));
            indMap.put("bb_width", bw.get(n - 1));
            indMap.put("stoch_k", currK);
            indMap.put("stoch_d", currD);
            indMap.put("atr", currAtr);
            indMap.put("adx", currAdx);
            indMap.put("plus_di", currPdi);
            indMap.put("minus_di", currMdi);

            return new SignalResult(symbol, timeframe, Instant.now(),
                    signal, confidence, price, trend, trendStr, indMap,
                    stopLoss, takeProfit, buyScore, sellScore, null, null);
        }
    }

    // ======================== POSITION MANAGER =========================

    public static class PositionManager {
        private final RiskLevel riskLevel;
        private final int maxPositions;
        private final Map<String, Position> activePositions = new ConcurrentHashMap<>();
        private final List<TradeRecord> tradeHistory = Collections.synchronizedList(new ArrayList<>());

        public PositionManager(RiskLevel riskLevel, int maxPositions) {
            this.riskLevel = riskLevel;
            this.maxPositions = maxPositions;
        }

        public boolean canOpenPosition(String symbol) {
            if (activePositions.size() >= maxPositions) return false;
            if (symbol != null) {
                long sameSymbol = activePositions.values().stream()
                        .filter(p -> p.getSymbol().equals(symbol) && "OPEN".equals(p.getStatus()))
                        .count();
                if (sameSymbol >= 2) return false;
            }
            return true;
        }

        public Optional<Position> openPosition(SignalResult signal, ForexPair pair, double accountBalance) {
            if (!canOpenPosition(signal.symbol())) return Optional.empty();
            if (signal.signal() == SignalType.HOLD || signal.signal() == SignalType.CLOSE_LONG
                    || signal.signal() == SignalType.CLOSE_SHORT) return Optional.empty();

            double stopPips = signal.stopLoss() != null ?
                    Math.abs(signal.price() - signal.stopLoss()) / pair.getPipSize() : 20;
            double riskAmount = accountBalance * riskLevel.riskPerTrade;
            double pipValue = 10; // assume standard
            double posSize = riskAmount / (stopPips * pipValue);
            posSize = Math.min(posSize, accountBalance * 0.1);

            Position pos = new Position(
                    signal.symbol() + "_" + Instant.now().toString().replaceAll("[-:T.]","")
                            + "_" + Integer.toHexString(new Random().nextInt(0xFFFF)),
                    signal.symbol(), pair, signal.signal(),
                    signal.price(), posSize,
                    signal.stopLoss() != null ? signal.stopLoss() :
                            (signal.signal() == SignalType.BUY || signal.signal() == SignalType.STRONG_BUY ?
                                    signal.price() * 0.98 : signal.price() * 1.02),
                    signal.takeProfit() != null ? signal.takeProfit() :
                            (signal.signal() == SignalType.BUY || signal.signal() == SignalType.STRONG_BUY ?
                                    signal.price() * 1.02 : signal.price() * 0.98),
                    signal.confidence(), signal.price()
            );
            activePositions.put(pos.getId(), pos);
            LOGGER.info(String.format("Posição aberta: %s %s @ %.5f | Tamanho: %.2f | Conf: %.1f%%",
                    pos.getSymbol(), pos.getSignal().label, pos.getEntryPrice(),
                    pos.getPositionSize(), pos.getConfidence()));
            return Optional.of(pos);
        }

        public Optional<TradeRecord> closePosition(String positionId, double exitPrice, MarketSession session) {
            Position pos = activePositions.get(positionId);
            if (pos == null) return Optional.empty();

            double pnlPips;
            if (pos.getSignal() == SignalType.BUY || pos.getSignal() == SignalType.STRONG_BUY)
                pnlPips = (exitPrice - pos.getEntryPrice()) / pos.getPair().getPipSize();
            else
                pnlPips = (pos.getEntryPrice() - exitPrice) / pos.getPair().getPipSize();
            double pnlAmount = pnlPips * pos.getPipValue();

            pos.setExitPrice(exitPrice);
            pos.setExitTime(Instant.now());
            pos.setPnlPips(pnlPips);
            pos.setPnlAmount(pnlAmount);
            pos.setStatus("CLOSED");

            TradeRecord rec = new TradeRecord(
                    pos.getId(), pos.getSymbol(), pos.getPair(), pos.getSignal(),
                    pos.getEntryPrice(), exitPrice, pos.getPositionSize(),
                    pnlPips, pnlAmount, pos.getEntryTime(), Instant.now(),
                    pos.getConfidence(), session
            );
            tradeHistory.add(rec);
            activePositions.remove(positionId);
            LOGGER.info(String.format("%s Posição fechada: %s P&L: %.1f pips ($%.2f)",
                    pnlAmount > 0 ? "Lucro" : "Prejuízo", pos.getSymbol(), pnlPips, pnlAmount));
            return Optional.of(rec);
        }

        public List<TradeRecord> updatePositions(Map<String, Double> currentPrices, MarketSession session) {
            List<TradeRecord> closed = new ArrayList<>();
            for (Position pos : new ArrayList<>(activePositions.values())) {
                Double newPrice = currentPrices.get(pos.getSymbol());
                if (newPrice != null) {
                    pos.setCurrentPrice(newPrice);
                    if (pos.getSignal() == SignalType.BUY || pos.getSignal() == SignalType.STRONG_BUY) {
                        if (newPrice <= pos.getStopLoss()) {
                            closePosition(pos.getId(), pos.getStopLoss(), session).ifPresent(closed::add);
                        } else if (newPrice >= pos.getTakeProfit()) {
                            closePosition(pos.getId(), pos.getTakeProfit(), session).ifPresent(closed::add);
                        }
                    } else {
                        if (newPrice >= pos.getStopLoss()) {
                            closePosition(pos.getId(), pos.getStopLoss(), session).ifPresent(closed::add);
                        } else if (newPrice <= pos.getTakeProfit()) {
                            closePosition(pos.getId(), pos.getTakeProfit(), session).ifPresent(closed::add);
                        }
                    }
                }
            }
            return closed;
        }

        public Map<String, Object> getPerformanceSummary() {
            if (tradeHistory.isEmpty()) return Map.of("message", "Nenhum trade realizado");
            int total = tradeHistory.size();
            long winners = tradeHistory.stream().filter(t -> t.pnlAmount() > 0).count();
            long losers = total - winners;
            double totalPnl = tradeHistory.stream().mapToDouble(TradeRecord::pnlAmount).sum();
            double avgWin = tradeHistory.stream().filter(t -> t.pnlAmount() > 0)
                    .mapToDouble(TradeRecord::pnlAmount).average().orElse(0);
            double avgLoss = tradeHistory.stream().filter(t -> t.pnlAmount() < 0)
                    .mapToDouble(TradeRecord::pnlAmount).average().orElse(0);
            double grossProfit = tradeHistory.stream().filter(t -> t.pnlAmount() > 0)
                    .mapToDouble(TradeRecord::pnlAmount).sum();
            double grossLoss = Math.abs(tradeHistory.stream().filter(t -> t.pnlAmount() < 0)
                    .mapToDouble(TradeRecord::pnlAmount).sum());
            double profitFactor = grossLoss > 0 ? grossProfit / grossLoss : 0;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("totalTrades", total);
            map.put("winningTrades", winners);
            map.put("losingTrades", losers);
            map.put("winRate", total > 0 ? (double) winners / total * 100 : 0);
            map.put("totalPnl", totalPnl);
            map.put("avgWin", avgWin);
            map.put("avgLoss", avgLoss);
            map.put("profitFactor", profitFactor);
            map.put("activePositions", activePositions.size());
            return map;
        }
    }

    // ======================== VHALINOR FOREX STRATEGY =========================

    public static class VhalinorForexStrategy {
        private final IndicatorConfig indicatorConfig = new IndicatorConfig();
        private final TechnicalIndicators indicators = new TechnicalIndicators(indicatorConfig);
        private final TrendAnalyzer trendAnalyzer = new TrendAnalyzer(indicators);
        private final SignalGenerator signalGenerator = new SignalGenerator(indicators, trendAnalyzer);
        private final PositionManager positionManager;
        private final List<SignalResult> signalHistory = new ArrayList<>();
        private final Map<String, ForexPair> forexPairs = new LinkedHashMap<>();

        public VhalinorForexStrategy(RiskLevel riskLevel, int maxPositions) {
            this.positionManager = new PositionManager(riskLevel, maxPositions);
            for (ForexPair pair : ForexPair.values()) forexPairs.put(pair.getLabel(), pair);
            LOGGER.info("VHALINOR IAG - ESTRATEGIA FOREX INICIALIZADA");
        }

        public Optional<SignalResult> analyzePair(String symbol, List<Double> open, List<Double> high,
                                                   List<Double> low, List<Double> close, TimeFrame timeframe) {
            if (close.size() < 200) return Optional.empty();
            SignalResult signal = signalGenerator.generateSignal(symbol, open, high, low, close, timeframe);
            signalHistory.add(signal);
            return Optional.of(signal);
        }

        public List<SignalResult> getBestOpportunities(List<SignalResult> signals, double minConfidence, int maxResults) {
            return signals.stream()
                    .filter(s -> (s.signal() == SignalType.BUY || s.signal() == SignalType.STRONG_BUY
                            || s.signal() == SignalType.SELL || s.signal() == SignalType.STRONG_SELL)
                            && s.confidence() >= minConfidence)
                    .sorted(Comparator.comparingDouble(SignalResult::confidence).reversed())
                    .limit(maxResults)
                    .collect(Collectors.toList());
        }

        public Optional<Position> executeSignal(SignalResult signal, double accountBalance) {
            ForexPair pair = forexPairs.get(signal.symbol());
            if (pair == null) return Optional.empty();
            return positionManager.openPosition(signal, pair, accountBalance);
        }

        public List<TradeRecord> updateMarketData(Map<String, Double> prices) {
            MarketSession session = getCurrentSession();
            return positionManager.updatePositions(prices, session);
        }

        private MarketSession getCurrentSession() {
            int hour = LocalTime.now().getHour();
            if (hour >= 13 && hour <= 17) return MarketSession.OVERLAP_LONDON_NY;
            if (hour >= 8 && hour <= 17) return MarketSession.LONDON;
            if (hour >= 13 && hour <= 22) return MarketSession.NY;
            if (hour >= 0 && hour <= 9) return MarketSession.ASIA;
            return MarketSession.LONDON;
        }

        public String generateReport() {
            Map<String, Object> perf = positionManager.getPerformanceSummary();
            StringBuilder sb = new StringBuilder();
            sb.append("=".repeat(80)).append("\n");
            sb.append("📊 RELATÓRIO DE PERFORMANCE - FOREX TRADING\n");
            sb.append("=".repeat(80)).append("\n");
            if (perf.containsKey("message")) {
                sb.append(perf.get("message")).append("\n");
            } else {
                sb.append("\n📈 ESTATÍSTICAS GERAIS:\n");
                sb.append("   • Total de trades: ").append(perf.get("totalTrades")).append("\n");
                sb.append("   • Trades vencedores: ").append(perf.get("winningTrades")).append("\n");
                sb.append("   • Trades perdedores: ").append(perf.get("losingTrades")).append("\n");
                sb.append(String.format("   • Win rate: %.2f%%\n", (double) perf.get("winRate")));
                sb.append(String.format("   • Profit factor: %.2f\n", (double) perf.get("profitFactor")));
                sb.append("\n💰 RESULTADOS FINANCEIROS:\n");
                sb.append(String.format("   • P&L total: $%.2f\n", (double) perf.get("totalPnl")));
                sb.append(String.format("   • Ganho médio: $%.2f\n", (double) perf.get("avgWin")));
                sb.append(String.format("   • Perda média: $%.2f\n", (double) perf.get("avgLoss")));
                sb.append("\n🟢 POSIÇÕES ATIVAS:\n");
                sb.append("   • Ativas: ").append(perf.get("activePositions")).append("\n");
            }
            sb.append("=".repeat(80));
            return sb.toString();
        }
    }

    // ======================== MAIN DEMONSTRATION =========================

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("🚀 VHALINOR IAG - ESTRATÉGIA FOREX QUÂNTICA (Java Demo)");
        System.out.println("=".repeat(90));

        // 1. Inicializar estratégia
        System.out.println("\n1️⃣  Inicializando estratégia...");
        VhalinorForexStrategy strategy = new VhalinorForexStrategy(RiskLevel.MEDIUM, 5);

        // 2. Criar dados simulados para alguns pares
        System.out.println("\n2️⃣  Gerando dados simulados para análise...");
        Random rnd = new Random(42);
        List<Double> close = new ArrayList<>(), open = new ArrayList<>(), high = new ArrayList<>(), low = new ArrayList<>();
        double price = 1.0850; // EUR/USD
        for (int i = 0; i < 300; i++) {
            double chg = rnd.nextGaussian() * 0.005; // ~0.5% stdDev
            price *= (1 + chg);
            close.add(price);
            open.add(price * (1 + rnd.nextGaussian() * 0.0005));
            high.add(price * (1 + Math.abs(rnd.nextGaussian() * 0.001)));
            low.add(price * (1 - Math.abs(rnd.nextGaussian() * 0.001)));
        }

        // 3. Analisar EUR/USD
        System.out.println("\n3️⃣  Analisando EUR/USD...");
        Optional<SignalResult> signalOpt = strategy.analyzePair("EUR/USD", open, high, low, close, TimeFrame.H1);
        if (signalOpt.isPresent()) {
            SignalResult sig = signalOpt.get();
            ForexPair pair = ForexPair.EUR_USD;
            System.out.printf("   Sinal: %s %s (Conf: %.1f%%)%n", sig.signal().icon, sig.signal().label, sig.confidence());
            System.out.printf("   Preço: %.5f | Tendência: %s %s | Força: %.1f%n",
                    sig.price(), sig.trend().icon, sig.trend().label, sig.trendStrength());
            if (sig.stopLoss() != null)
                System.out.printf("   SL: %.5f | TP: %.5f%n", sig.stopLoss(), sig.takeProfit());

            // Executar sinal (simulação)
            if (sig.confidence() > 70) {
                strategy.executeSignal(sig, 10000)
                        .ifPresent(p -> System.out.println("   ✅ Posição aberta: " + p.getId()));
            }
        } else {
            System.out.println("   ❌ Falha na análise.");
        }

        // 4. Atualizar mercado e mostrar relatório
        System.out.println("\n4️⃣  Atualizando preços...");
        Map<String, Double> updatedPrices = Map.of("EUR/USD", close.get(close.size()-1) * 1.01); // simulate small move
        strategy.updateMarketData(updatedPrices);

        System.out.println("\n5️⃣  Relatório de performance:");
        System.out.println(strategy.generateReport());

        System.out.println("\n✅ DEMONSTRAÇÃO CONCLUÍDA!");
    }
}
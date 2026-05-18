import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * VHALINOR IAG 2.0 - ESTRATEGIA FOREX QUANTICA (Java Port)
 * Sistema de decisão com múltiplas camadas neurais
 */
public class VhalinorForexStrategyMain {

    // ==================== ENUMS ====================
    enum SignalType {
        STRONG_BUY("STRONG_BUY", "🟢🟢", "Compra Forte", 90),
        BUY("BUY", "🟢", "Compra", 70),
        HOLD("HOLD", "⚪", "Manter", 50),
        SELL("SELL", "🔴", "Venda", 70),
        STRONG_SELL("STRONG_SELL", "🔴🔴", "Venda Forte", 90),
        CLOSE_LONG("CLOSE_LONG", "🔚", "Fechar Posição Longa", 80),
        CLOSE_SHORT("CLOSE_SHORT", "🔚", "Fechar Posição Curta", 80);

        final String label, icon, descricao;
        final int threshold;
        SignalType(String l, String i, String d, int t) {
            label = l; icon = i; descricao = d; threshold = t;
        }
    }

    enum TimeFrame {
        M1("1m", 60), M5("5m", 300), M15("15m", 900), M30("30m", 1800),
        H1("1h", 3600), H4("4h", 14400), D1("1d", 86400),
        W1("1w", 604800), MN1("1mo", 2592000);
        final String code;
        final int seconds;
        TimeFrame(String c, int s) { code = c; seconds = s; }
    }

    enum MarketTrend {
        STRONG_UPTREND("STRONG_UPTREND", "📈📈", 2.0),
        UPTREND("UPTREND", "📈", 1.0),
        SIDEWAYS("SIDEWAYS", "📊", 0.0),
        DOWNTREND("DOWNTREND", "📉", -1.0),
        STRONG_DOWNTREND("STRONG_DOWNTREND", "📉📉", -2.0);
        final String label, icon;
        final double peso;
        MarketTrend(String l, String i, double p) { label = l; icon = i; peso = p; }
    }

    enum RiskLevel {
        VERY_LOW("Muito Baixo", 0.005), LOW("Baixo", 0.01), MEDIUM("Médio", 0.02),
        HIGH("Alto", 0.03), VERY_HIGH("Muito Alto", 0.05);
        final String label;
        final double riskPerTrade;
        RiskLevel(String l, double r) { label = l; riskPerTrade = r; }
    }

    enum MarketSession {
        ASIA(0, 9), LONDON(8, 17), NY(13, 22), OVERLAP_LONDON_NY(13, 17);
        final int startH, endH;
        MarketSession(int s, int e) { startH = s; endH = e; }
    }

    // ==================== DATA CLASSES ====================
    static class IndicatorConfig {
        int rsiPeriod = 14, rsiOversold = 30, rsiOverbought = 70;
        int macdFast = 12, macdSlow = 26, macdSignal = 9;
        int bbPeriod = 20; double bbStdDev = 2.0;
        int atrPeriod = 14;
        int stochPeriod = 14, stochSmoothK = 3, stochSmoothD = 3;
        int smaShort = 20, smaMedium = 50, smaLong = 200, emaShort = 12, emaLong = 26;
        int adxPeriod = 14;
        int ichimokuTenkan = 9, ichimokuKijun = 26, ichimokuSenkou = 52;
    }

    static class SignalResult {
        String symbol;
        TimeFrame timeframe;
        LocalDateTime timestamp;
        SignalType signal;
        double confidence, price, trendStrength;
        MarketTrend trend;
        Map<String, Double> indicators = new HashMap<>();
        double stopLoss, takeProfit;
        int buyScore, sellScore;
        double neuralPrediction, quantumConfidence;

        SignalResult(String sym, TimeFrame tf, SignalType sig, double conf, double pr,
                     MarketTrend tr, double trStr, int buy, int sell) {
            symbol = sym; timeframe = tf; signal = sig; confidence = conf; price = pr;
            trend = tr; trendStrength = trStr; buyScore = buy; sellScore = sell;
            timestamp = LocalDateTime.now();
        }
    }

    static class Position {
        String id, symbol;
        SignalType signal;
        double entryPrice, positionSize, stopLoss, takeProfit;
        LocalDateTime entryTime;
        double confidence, currentPrice;
        double pnlPips, pnlAmount;
        String status = "OPEN";
    }

    static class TradeRecord {
        String positionId, symbol;
        SignalType signal;
        double entryPrice, exitPrice, positionSize, pnlPips, pnlAmount;
        LocalDateTime entryTime, exitTime;
        double confidence;
        MarketSession session;
    }

    // ==================== TECHNICAL INDICATORS (Pure Java) ====================
    static class TechnicalIndicators {
        final IndicatorConfig cfg;
        TechnicalIndicators(IndicatorConfig c) { this.cfg = c; }

        double[] sma(double[] data, int period) {
            double[] result = new double[data.length];
            double sum = 0;
            for (int i = 0; i < data.length; i++) {
                sum += data[i];
                if (i >= period) sum -= data[i - period];
                result[i] = (i >= period - 1) ? sum / period : data[i];
            }
            return result;
        }

        double[] ema(double[] data, int period) {
            double[] result = new double[data.length];
            double k = 2.0 / (period + 1);
            result[0] = data[0];
            for (int i = 1; i < data.length; i++) {
                result[i] = data[i] * k + result[i-1] * (1 - k);
            }
            return result;
        }

        double[] rsi(double[] data, int period) {
            double[] rsi = new double[data.length];
            double avgGain = 0, avgLoss = 0;
            for (int i = 1; i <= period && i < data.length; i++) {
                double diff = data[i] - data[i-1];
                if (diff > 0) avgGain += diff;
                else avgLoss -= diff;
            }
            avgGain /= period; avgLoss /= period;
            rsi[period] = avgLoss == 0 ? 100 : 100 - 100 / (1 + avgGain / avgLoss);
            for (int i = period + 1; i < data.length; i++) {
                double diff = data[i] - data[i-1];
                avgGain = (avgGain * (period - 1) + (diff > 0 ? diff : 0)) / period;
                avgLoss = (avgLoss * (period - 1) + (diff < 0 ? -diff : 0)) / period;
                rsi[i] = avgLoss == 0 ? 100 : 100 - 100 / (1 + avgGain / avgLoss);
            }
            Arrays.fill(rsi, 0, period, 50);
            return rsi;
        }

        // macd retorna [macd, signal, histogram]
        double[][] macd(double[] data) {
            double[] ema12 = ema(data, cfg.macdFast);
            double[] ema26 = ema(data, cfg.macdSlow);
            double[] macdLine = new double[data.length];
            for (int i = 0; i < data.length; i++) macdLine[i] = ema12[i] - ema26[i];
            double[] signalLine = ema(macdLine, cfg.macdSignal);
            double[] hist = new double[data.length];
            for (int i = 0; i < data.length; i++) hist[i] = macdLine[i] - signalLine[i];
            return new double[][]{macdLine, signalLine, hist};
        }

        // bollinger retorna [upper, middle, lower, bandwidth]
        double[][] bollingerBands(double[] data) {
            int p = cfg.bbPeriod;
            double mult = cfg.bbStdDev;
            double[] mid = sma(data, p);
            double[] upper = new double[data.length];
            double[] lower = new double[data.length];
            double[] band = new double[data.length];
            for (int i = p-1; i < data.length; i++) {
                double sumSq = 0;
                for (int j = i-p+1; j <= i; j++) sumSq += (data[j] - mid[i]) * (data[j] - mid[i]);
                double std = Math.sqrt(sumSq / p);
                upper[i] = mid[i] + mult * std;
                lower[i] = mid[i] - mult * std;
                band[i] = (upper[i] - lower[i]) / mid[i];
            }
            return new double[][]{upper, mid, lower, band};
        }

        double[] atr(double[] high, double[] low, double[] close) {
            double[] tr = new double[close.length];
            tr[0] = high[0] - low[0];
            for (int i = 1; i < close.length; i++) {
                double hl = high[i] - low[i];
                double hc = Math.abs(high[i] - close[i-1]);
                double lc = Math.abs(low[i] - close[i-1]);
                tr[i] = Math.max(hl, Math.max(hc, lc));
            }
            return ema(tr, cfg.atrPeriod);  // simplificação (EMA do TR)
        }

        // stochastic returns [k, d]
        double[][] stochastic(double[] high, double[] low, double[] close) {
            int p = cfg.stochPeriod;
            double[] k = new double[close.length];
            double[] d = new double[close.length];
            for (int i = p-1; i < close.length; i++) {
                double lowMin = low[i], highMax = high[i];
                for (int j = i-p+1; j <= i; j++) {
                    if (low[j] < lowMin) lowMin = low[j];
                    if (high[j] > highMax) highMax = high[j];
                }
                k[i] = (close[i] - lowMin) / (highMax - lowMin) * 100;
            }
            d = sma(k, cfg.stochSmoothD);
            return new double[][]{k, d};
        }

        // adx retorna [adx, plusDI, minusDI] - simplificado sem TA-Lib
        double[][] adx(double[] high, double[] low, double[] close) {
            int p = cfg.adxPeriod;
            double[] tr = new double[close.length];
            double[] plusDM = new double[close.length];
            double[] minusDM = new double[close.length];
            double[] adx = new double[close.length];
            double[] plusDI = new double[close.length];
            double[] minusDI = new double[close.length];
            // cálculo simplificado: ADX como ema do DX
            for (int i = 1; i < close.length; i++) {
                double hl = high[i] - low[i];
                double hc = Math.abs(high[i] - close[i-1]);
                double lc = Math.abs(low[i] - close[i-1]);
                tr[i] = Math.max(hl, Math.max(hc, lc));
                double upMove = high[i] - high[i-1];
                double downMove = low[i-1] - low[i];
                plusDM[i] = (upMove > downMove && upMove > 0) ? upMove : 0;
                minusDM[i] = (downMove > upMove && downMove > 0) ? downMove : 0;
            }
            double[] tr14 = ema(tr, p);
            double[] plusDM14 = ema(plusDM, p);
            double[] minusDM14 = ema(minusDM, p);
            for (int i = p; i < close.length; i++) {
                plusDI[i] = (tr14[i] > 0) ? (plusDM14[i] / tr14[i]) * 100 : 0;
                minusDI[i] = (tr14[i] > 0) ? (minusDM14[i] / tr14[i]) * 100 : 0;
                double dx = (plusDI[i] + minusDI[i]) > 0 ?
                        Math.abs(plusDI[i] - minusDI[i]) / (plusDI[i] + minusDI[i]) * 100 : 0;
                adx[i] = (i == p) ? dx : (adx[i-1] * (p-1) + dx) / p; // EMA smoothing
            }
            return new double[][]{adx, plusDI, minusDI};
        }
    }

    // ==================== TREND ANALYZER ====================
    static class TrendAnalyzer {
        final TechnicalIndicators ind;
        TrendAnalyzer(TechnicalIndicators i) { ind = i; }

        MarketTrend identifyTrend(double[] close, double[] high, double[] low) {
            double[] sma20 = ind.sma(close, ind.cfg.smaShort);
            double[] sma50 = ind.sma(close, ind.cfg.smaMedium);
            double[] sma200 = ind.sma(close, ind.cfg.smaLong);
            int last = close.length - 1;
            double price = close[last];
            double s20 = sma20[last], s50 = sma50[last], s200 = sma200[last];
            double[][] adxDI = ind.adx(high, low, close);
            double adxVal = adxDI[0][last];
            double plus = adxDI[1][last], minus = adxDI[2][last];

            if (price > s20 && s20 > s50 && s50 > s200 && adxVal > 30 && plus > minus)
                return MarketTrend.STRONG_UPTREND;
            if (price > s50 && adxVal > 25 && plus > minus)
                return MarketTrend.UPTREND;
            if (price < s20 && s20 < s50 && s50 < s200 && adxVal > 30 && minus > plus)
                return MarketTrend.STRONG_DOWNTREND;
            if (price < s50 && adxVal > 25 && minus > plus)
                return MarketTrend.DOWNTREND;
            return MarketTrend.SIDEWAYS;
        }

        double trendStrength(double[] close, double[] high, double[] low) {
            double[] sma20 = ind.sma(close, 20), sma50 = ind.sma(close, 50);
            double[][] adxD = ind.adx(high, low, close);
            double adx = adxD[0][close.length-1];
            double diffMA = Math.abs(sma20[close.length-1] - sma50[close.length-1]) / sma50[close.length-1] * 50;
            double priceVsMA = Math.abs(close[close.length-1] - sma20[close.length-1]) / sma20[close.length-1] * 50;
            double str = adx * 0.5 + diffMA * 0.3 + priceVsMA * 0.2;
            return Math.min(str, 100);
        }
    }

    // ==================== SIGNAL GENERATOR ====================
    static class SignalGenerator {
        final TechnicalIndicators ind;
        final TrendAnalyzer trend;
        SignalGenerator(TechnicalIndicators i, TrendAnalyzer t) { ind = i; trend = t; }
        Random rand = new Random();

        SignalResult generateSignal(String symbol, double[] close, double[] high, double[] low,
                                    TimeFrame tf) {
            // cálculo de indicadores
            double[] rsi = ind.rsi(close, ind.cfg.rsiPeriod);
            double[][] macdData = ind.macd(close);
            double[] macdLine = macdData[0], signalLine = macdData[1], hist = macdData[2];
            double[][] bb = ind.bollingerBands(close);
            double[] upperBB = bb[0], middleBB = bb[1], lowerBB = bb[2], bbWidth = bb[3];
            double[][] stoch = ind.stochastic(high, low, close);
            double[] k = stoch[0], d = stoch[1];
            double[] atrVals = ind.atr(high, low, close);
            double[][] adxVals = ind.adx(high, low, close);
            double[] adx = adxVals[0], plusDI = adxVals[1], minusDI = adxVals[2];

            int last = close.length - 1;
            MarketTrend mt = trend.identifyTrend(close, high, low);
            double trendStr = trend.trendStrength(close, high, low);

            int buyScore = 0, sellScore = 0;
            // RSI
            double curRsi = rsi[last];
            if (curRsi < ind.cfg.rsiOversold) buyScore += 4;
            else if (curRsi < ind.cfg.rsiOversold + 10) buyScore += 2;
            else if (curRsi > ind.cfg.rsiOverbought) sellScore += 4;
            else if (curRsi > ind.cfg.rsiOverbought - 10) sellScore += 2;
            // MACD
            if (macdLine[last] > signalLine[last]) {
                buyScore += 2;
                if (last > 0 && macdLine[last-1] <= signalLine[last-1]) buyScore += 3;
            } else {
                sellScore += 2;
                if (last > 0 && macdLine[last-1] >= signalLine[last-1]) sellScore += 3;
            }
            // Bollinger
            if (close[last] < lowerBB[last]) buyScore += 4;
            else if (close[last] > upperBB[last]) sellScore += 4;
            else if (close[last] < middleBB[last]) buyScore += 1;
            else sellScore += 1;
            // Stochastic
            if (k[last] < 20 && d[last] < 20) buyScore += 3;
            else if (k[last] < 30) buyScore += 1;
            else if (k[last] > 80 && d[last] > 80) sellScore += 3;
            else if (k[last] > 70) sellScore += 1;
            // Trend
            if (mt == MarketTrend.STRONG_UPTREND || mt == MarketTrend.UPTREND)
                buyScore += (int)(trendStr / 20);
            else if (mt == MarketTrend.STRONG_DOWNTREND || mt == MarketTrend.DOWNTREND)
                sellScore += (int)(trendStr / 20);
            // ADX
            if (adx[last] > 25) {
                if (plusDI[last] > minusDI[last]) buyScore += 2;
                else sellScore += 2;
            }
            // Price Action (exemplo simplificado)
            // Suporte/Resistência
            double recentLow = Arrays.stream(low, Math.max(0, last - 19), last + 1).min().orElse(close[last]);
            double recentHigh = Arrays.stream(high, Math.max(0, last - 19), last + 1).max().orElse(close[last]);
            if (close[last] <= recentLow * 1.01) buyScore += 2;
            if (close[last] >= recentHigh * 0.99) sellScore += 2;

            int maxScore = 30;
            SignalType signal;
            double confidence;
            int signalDiff = Math.abs(buyScore - sellScore);
            if (buyScore >= 10 && buyScore > sellScore) {
                if (signalDiff >= 15) { signal = SignalType.STRONG_BUY; confidence = Math.min(buyScore / (double) maxScore * 100, 95); }
                else { signal = SignalType.BUY; confidence = Math.min(buyScore / (double) maxScore * 100, 85); }
            } else if (sellScore >= 10 && sellScore > buyScore) {
                if (signalDiff >= 15) { signal = SignalType.STRONG_SELL; confidence = Math.min(sellScore / (double) maxScore * 100, 95); }
                else { signal = SignalType.SELL; confidence = Math.min(sellScore / (double) maxScore * 100, 85); }
            } else {
                signal = SignalType.HOLD; confidence = 50 + signalDiff * 2;
            }

            double atrVal = atrVals[last];
            double stopLoss = 0, takeProfit = 0;
            if (signal == SignalType.BUY || signal == SignalType.STRONG_BUY) {
                stopLoss = close[last] - 2 * atrVal;
                takeProfit = close[last] + 2 * atrVal * 2; // RR 2
            } else if (signal == SignalType.SELL || signal == SignalType.STRONG_SELL) {
                stopLoss = close[last] + 2 * atrVal;
                takeProfit = close[last] - 2 * atrVal * 2;
            }

            SignalResult res = new SignalResult(symbol, tf, signal, confidence, close[last], mt, trendStr, buyScore, sellScore);
            res.stopLoss = stopLoss; res.takeProfit = takeProfit;
            Map<String, Double> indMap = new HashMap<>();
            indMap.put("rsi", curRsi);
            indMap.put("macd", macdLine[last]); indMap.put("macd_signal", signalLine[last]);
            indMap.put("bb_upper", upperBB[last]); indMap.put("bb_middle", middleBB[last]); indMap.put("bb_lower", lowerBB[last]);
            indMap.put("stoch_k", k[last]); indMap.put("stoch_d", d[last]);
            indMap.put("atr", atrVal); indMap.put("adx", adx[last]);
            res.indicators = indMap;
            return res;
        }
    }

    // ==================== POSITION MANAGER ====================
    static class PositionManager {
        private final RiskLevel risk;
        private final double riskPerTrade;
        private final int maxPositions;
        private final Map<String, Position> active = new HashMap<>();
        private final List<TradeRecord> history = new ArrayList<>();
        private static final Logger LOG = Logger.getLogger("PositionManager");

        PositionManager(RiskLevel risk, int maxPos) {
            this.risk = risk; this.riskPerTrade = risk.riskPerTrade; maxPositions = maxPos;
        }

        boolean canOpenPosition(String symbol) {
            if (active.size() >= maxPositions) return false;
            long sameSym = active.values().stream().filter(p -> p.symbol.equals(symbol) && "OPEN".equals(p.status)).count();
            return sameSym < 2;
        }

        Position openPosition(SignalResult signal, double accountBalance) {
            if (!canOpenPosition(signal.symbol) || signal.signal == SignalType.HOLD) return null;
            double pipSize = getPipSize(signal.symbol);
            double stopPips = signal.stopLoss != 0 ? Math.abs(signal.price - signal.stopLoss) / pipSize : 20;
            double riskAmount = accountBalance * riskPerTrade;
            double pipValue = 10; // assume standard lot
            double size = riskAmount / (stopPips * pipValue);
            size = Math.min(size, accountBalance * 0.1); // max 10% capital

            Position pos = new Position();
            pos.id = UUID.randomUUID().toString().substring(0, 8);
            pos.symbol = signal.symbol;
            pos.signal = signal.signal;
            pos.entryPrice = signal.price;
            pos.positionSize = Math.round(size * 100.0) / 100.0;
            pos.stopLoss = signal.stopLoss;
            pos.takeProfit = signal.takeProfit;
            pos.entryTime = LocalDateTime.now();
            pos.confidence = signal.confidence;
            pos.currentPrice = signal.price;
            active.put(pos.id, pos);
            LOG.info("Posicao aberta: " + pos.symbol + " " + signal.signal.label + " @ " + pos.entryPrice);
            return pos;
        }

        TradeRecord closePosition(String posId, double exitPrice, MarketSession session) {
            Position pos = active.remove(posId);
            if (pos == null) return null;
            double pipSize = getPipSize(pos.symbol);
            double pips;
            if (pos.signal == SignalType.BUY || pos.signal == SignalType.STRONG_BUY)
                pips = (exitPrice - pos.entryPrice) / pipSize;
            else
                pips = (pos.entryPrice - exitPrice) / pipSize;
            double amount = pips * pos.positionSize * 10; // aproximado
            TradeRecord t = new TradeRecord();
            t.positionId = pos.id; t.symbol = pos.symbol; t.signal = pos.signal;
            t.entryPrice = pos.entryPrice; t.exitPrice = exitPrice;
            t.positionSize = pos.positionSize; t.pnlPips = pips; t.pnlAmount = amount;
            t.entryTime = pos.entryTime; t.exitTime = LocalDateTime.now();
            t.confidence = pos.confidence; t.session = session;
            history.add(t);
            LOG.info(String.format("Trade fechado %s PnL: %.1f pips ($%.2f)", pos.symbol, pips, amount));
            return t;
        }

        List<TradeRecord> updatePositions(Map<String, Double> prices) {
            List<TradeRecord> closed = new ArrayList<>();
            MarketSession session = getCurrentSession();
            for (Position pos : active.values()) {
                Double price = prices.get(pos.symbol);
                if (price != null) {
                    pos.currentPrice = price;
                    if ((pos.signal == SignalType.BUY || pos.signal == SignalType.STRONG_BUY)) {
                        if (price <= pos.stopLoss || price >= pos.takeProfit) {
                            closed.add(closePosition(pos.id, Math.min(price, pos.stopLoss), session)); // simplificado
                        }
                    } else {
                        if (price >= pos.stopLoss || price <= pos.takeProfit) {
                            closed.add(closePosition(pos.id, Math.max(price, pos.stopLoss), session));
                        }
                    }
                }
            }
            return closed;
        }

        Map<String, Object> performanceSummary() {
            Map<String, Object> sum = new HashMap<>();
            sum.put("total", history.size());
            long wins = history.stream().filter(t -> t.pnlAmount > 0).count();
            sum.put("wins", wins);
            sum.put("losses", history.size() - wins);
            sum.put("winRate", history.size() > 0 ? (double) wins / history.size() * 100 : 0);
            double totalPnl = history.stream().mapToDouble(t -> t.pnlAmount).sum();
            sum.put("totalPnl", totalPnl);
            sum.put("activePositions", active.size());
            return sum;
        }

        static MarketSession getCurrentSession() {
            int h = LocalDateTime.now().getHour();
            if (h >= 13 && h <= 16) return MarketSession.OVERLAP_LONDON_NY;
            if (h >= 8 && h <= 16) return MarketSession.LONDON;
            if (h >= 13 && h <= 21) return MarketSession.NY;
            if (h >= 0 && h <= 8) return MarketSession.ASIA;
            return MarketSession.LONDON;
        }

        static double getPipSize(String symbol) {
            if (symbol.contains("JPY")) return 0.01;
            if (symbol.contains("BRL")) return 0.0005;
            return 0.0001;
        }
    }

    // ==================== MAIN STRATEGY ====================
    static class VhalinorForexStrategy {
        private final IndicatorConfig indicatorConfig = new IndicatorConfig();
        private final TechnicalIndicators indicators = new TechnicalIndicators(indicatorConfig);
        private final TrendAnalyzer trendAnalyzer = new TrendAnalyzer(indicators);
        private final SignalGenerator signalGenerator = new SignalGenerator(indicators, trendAnalyzer);
        private final PositionManager positionManager;
        private final Map<String, List<Double>> priceCache = new HashMap<>(); // close prices
        private final Random rand = new Random();
        private static final Logger LOG = Logger.getLogger("VhalinorForex");

        VhalinorForexStrategy(RiskLevel risk, int maxPos) {
            positionManager = new PositionManager(risk, maxPos);
        }

        // Simulates loading historical data (for demo, generate random)
        SignalResult analyzePair(String symbol, TimeFrame tf) {
            // generate synthetic data
            List<Double> closeList = priceCache.computeIfAbsent(symbol, k -> {
                List<Double> l = new ArrayList<>();
                double price = 1.0;
                for (int i = 0; i < 200; i++) {
                    price *= (1 + (rand.nextGaussian() * 0.01));
                    l.add(price);
                }
                return l;
            });
            double[] close = closeList.stream().mapToDouble(Double::doubleValue).toArray();
            double[] high = new double[close.length];
            double[] low = new double[close.length];
            for (int i = 0; i < close.length; i++) {
                double wick = close[i] * 0.002;
                high[i] = close[i] + wick * rand.nextDouble();
                low[i] = close[i] - wick * rand.nextDouble();
            }
            SignalResult sig = signalGenerator.generateSignal(symbol, close, high, low, tf);
            return sig;
        }

        List<SignalResult> analyzeAllPairs(TimeFrame tf) {
            String[] pairs = {"EUR/USD","GBP/USD","USD/JPY","AUD/USD","USD/CHF","USD/CAD","NZD/USD"};
            List<SignalResult> results = new ArrayList<>();
            for (String sym : pairs) {
                SignalResult s = analyzePair(sym, tf);
                if (s != null) results.add(s);
            }
            return results;
        }
    }

    // ==================== DEMO MAIN ====================
    public static void main(String[] args) {
        VhalinorForexStrategy strategy = new VhalinorForexStrategy(RiskLevel.MEDIUM, 5);
        System.out.println("=== VHALINOR FOREX QUANTICA (Java) ===");
        List<SignalResult> signals = strategy.analyzeAllPairs(TimeFrame.H1);
        System.out.println("Top signals:");
        signals.stream()
                .filter(s -> s.signal != SignalType.HOLD)
                .sorted((a,b) -> Double.compare(b.confidence, a.confidence))
                .limit(5)
                .forEach(s -> {
                    System.out.printf("%s %s %s conf:%.1f%% buy:%d sell:%d%n",
                            s.symbol, s.signal.icon, s.signal.label, s.confidence, s.buyScore, s.sellScore);
                });
        System.out.println("Performance summary:");
        Map<String, Object> perf = strategy.positionManager.performanceSummary();
        System.out.println(perf);
    }
}
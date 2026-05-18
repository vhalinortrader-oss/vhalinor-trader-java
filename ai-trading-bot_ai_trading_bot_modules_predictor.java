package com.ai_trading_bot.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Configuration settings for the AI trading bot.
 * Equivalent to AppSettings from Python.
 */
public class AppSettings {
    // Add any necessary configuration fields
    private final Map<String, Object> properties = new HashMap<>();

    public AppSettings() {
        // Default values
        properties.put("momentum_window", 5);
        properties.put("risk_free_rate", 0.02);
        properties.put("annualization_factor", 252);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) properties.getOrDefault(key, defaultValue);
    }

    public void set(String key, Object value) {
        properties.put(key, value);
    }
}

/**
 * Data class representing a prediction outcome.
 * Equivalent to Python dataclass PredictionOutcome.
 */
public class PredictionOutcome {
    private final String symbol;
    private final String direction;   // "long", "short", or "hold"
    private final double score;       // confidence score between 0 and 1
    private final String horizon;     // e.g., "1h", "1d"
    private final Map<String, Object> metadata;

    public PredictionOutcome(String symbol, String direction, double score,
                             String horizon, Map<String, Object> metadata) {
        this.symbol = symbol;
        this.direction = direction;
        this.score = score;
        this.horizon = horizon;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    // Getters
    public String getSymbol() { return symbol; }
    public String getDirection() { return direction; }
    public double getScore() { return score; }
    public String getHorizon() { return horizon; }
    public Map<String, Object> getMetadata() { return metadata; }

    @Override
    public String toString() {
        return String.format("PredictionOutcome{symbol='%s', direction='%s', score=%.4f, horizon='%s'}",
                symbol, direction, score, horizon);
    }
}

/**
 * Utility class for calculating common technical indicators.
 * Provides momentum, Sharpe ratio, Sortino ratio, max drawdown, and returns.
 */
public final class TechnicalIndicators {
    private static final Logger log = LoggerFactory.getLogger(TechnicalIndicators.class);

    private TechnicalIndicators() {} // static only

    /**
     * Calculates momentum as the percentage change over the last `window` periods.
     * Returns a value between -1 and 1 (clipped).
     */
    public static double calculateMomentum(List<Double> prices, int window) {
        if (prices == null || prices.size() <= window) {
            log.warn("Insufficient data for momentum calculation");
            return 0.0;
        }
        double current = prices.get(prices.size() - 1);
        double past = prices.get(prices.size() - 1 - window);
        if (past == 0) return 0.0;
        double momentum = (current - past) / past;
        // Clip to [-1, 1] for stability
        return Math.max(-1.0, Math.min(1.0, momentum));
    }

    /**
     * Calculates the Sharpe ratio from a list of returns.
     * Uses the risk‑free rate from settings (default 0.02).
     */
    public static double calculateSharpeRatio(List<Double> returns, double riskFreeRate, int annualizationFactor) {
        if (returns == null || returns.size() < 2) return 0.0;
        double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double std = stdDev(returns);
        if (std == 0) return 0.0;
        double excessReturn = mean - riskFreeRate / annualizationFactor; // daily risk‑free
        double dailySharpe = excessReturn / std;
        return dailySharpe * Math.sqrt(annualizationFactor);
    }

    /**
     * Calculates the Sortino ratio (downside deviation only).
     */
    public static double calculateSortinoRatio(List<Double> returns, double riskFreeRate, int annualizationFactor) {
        if (returns == null || returns.size() < 2) return 0.0;
        double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double downsideStd = downsideDeviation(returns);
        if (downsideStd == 0) return 0.0;
        double excessReturn = mean - riskFreeRate / annualizationFactor;
        double dailySortino = excessReturn / downsideStd;
        return dailySortino * Math.sqrt(annualizationFactor);
    }

    /**
     * Calculates the maximum drawdown from a price series.
     * Returns a value between 0 and 1.
     */
    public static double calculateMaxDrawdown(List<Double> prices) {
        if (prices == null || prices.size() < 2) return 0.0;
        double peak = prices.get(0);
        double maxDrawdown = 0.0;
        for (double price : prices) {
            if (price > peak) {
                peak = price;
            }
            double drawdown = (peak - price) / peak;
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }
        return maxDrawdown;
    }

    /**
     * Converts price list to simple returns.
     */
    public static List<Double> calculateReturns(List<Double> prices) {
        List<Double> returns = new ArrayList<>();
        if (prices == null || prices.size() < 2) return returns;
        for (int i = 1; i < prices.size(); i++) {
            double prev = prices.get(i - 1);
            if (prev != 0) {
                returns.add((prices.get(i) - prev) / prev);
            } else {
                returns.add(0.0);
            }
        }
        return returns;
    }

    // Helper: standard deviation
    private static double stdDev(List<Double> values) {
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0.0);
        return Math.sqrt(variance);
    }

    // Helper: downside deviation (only negative returns)
    private static double downsideDeviation(List<Double> returns) {
        List<Double> negative = new ArrayList<>();
        for (double r : returns) {
            if (r < 0) negative.add(r);
        }
        if (negative.isEmpty()) return 0.0;
        double meanNeg = negative.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double var = negative.stream().mapToDouble(v -> Math.pow(v - meanNeg, 2)).average().orElse(0.0);
        return Math.sqrt(var);
    }
}

/**
 * Ensemble predictor that combines momentum, risk metrics, and technical indicators
 * to generate trading signals.
 */
public class EnsemblePredictor {
    private static final Logger log = LoggerFactory.getLogger(EnsemblePredictor.class);

    private final AppSettings settings;
    private final List<String> models;

    public EnsemblePredictor(AppSettings settings) {
        this.settings = settings;
        this.models = Arrays.asList("momentum", "risk", "technical");
    }

    /**
     * Generates a structured prediction from market data.
     *
     * @param marketData a map containing at least "symbol" and "prices" (List<Double>)
     * @return PredictionOutcome
     */
    public PredictionOutcome predict(Map<String, Object> marketData) {
        String symbol = marketData.getOrDefault("symbol", "UNKNOWN").toString();

        @SuppressWarnings("unchecked")
        List<Double> prices = (List<Double>) marketData.getOrDefault("prices", Collections.emptyList());

        if (prices.size() < 10) {
            log.warn("Not enough data to predict {}", symbol);
            return new PredictionOutcome(symbol, "hold", 0.2, "1h", Collections.emptyMap());
        }

        int momentumWindow = settings.get("momentum_window", 5);
        double momentumScore = TechnicalIndicators.calculateMomentum(prices, momentumWindow);
        String direction = chooseDirection(prices, momentumScore);

        List<Double> returns = TechnicalIndicators.calculateReturns(prices);
        double riskFreeRate = settings.get("risk_free_rate", 0.02);
        int annualFactor = settings.get("annualization_factor", 252);

        double sharpe = TechnicalIndicators.calculateSharpeRatio(returns, riskFreeRate, annualFactor);
        double sortino = TechnicalIndicators.calculateSortinoRatio(returns, riskFreeRate, annualFactor);
        double maxDrawdown = TechnicalIndicators.calculateMaxDrawdown(prices);
        double riskAdjustment = riskAdjustment(maxDrawdown);

        double score = blendScores(momentumScore, sharpe, sortino, riskAdjustment);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("momentum", momentumScore);
        metadata.put("sharpe_ratio", sharpe);
        metadata.put("sortino_ratio", sortino);
        metadata.put("max_drawdown", maxDrawdown);
        metadata.put("risk_adjustment", riskAdjustment);
        metadata.put("candidate_models", models);

        return new PredictionOutcome(symbol, direction, score, "1h", metadata);
    }

    /**
     * Calculates backtesting metrics for a price series.
     */
    public Map<String, Object> backtest(List<Double> prices) {
        List<Double> returns = TechnicalIndicators.calculateReturns(prices);
        double riskFreeRate = settings.get("risk_free_rate", 0.02);
        int annualFactor = settings.get("annualization_factor", 252);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("sharpe_ratio", TechnicalIndicators.calculateSharpeRatio(returns, riskFreeRate, annualFactor));
        metrics.put("sortino_ratio", TechnicalIndicators.calculateSortinoRatio(returns, riskFreeRate, annualFactor));
        metrics.put("max_drawdown", TechnicalIndicators.calculateMaxDrawdown(prices));
        metrics.put("sample_size", prices.size());
        return metrics;
    }

    // Private helper methods
    private String chooseDirection(List<Double> prices, double momentumScore) {
        if (prices.size() < 6) return "hold";
        double last = prices.get(prices.size() - 1);
        double fiveDaysAgo = prices.get(prices.size() - 6);
        if (momentumScore > 0 && last > fiveDaysAgo) {
            return "long";
        }
        if (momentumScore < 0 && last < fiveDaysAgo) {
            return "short";
        }
        return "hold";
    }

    private double blendScores(double momentumScore, double sharpe, double sortino, double riskAdjustment) {
        double base = 0.5 + 0.08 * (momentumScore > 0 ? 1 : -1);
        double riskBonus = sharpe * 0.05 + sortino * 0.03 - riskAdjustment * 0.1;
        riskBonus = Math.max(0.0, Math.min(0.3, riskBonus));
        double score = base + riskBonus;
        return Math.max(0.0, Math.min(1.0, score));
    }

    private double riskAdjustment(double maxDrawdown) {
        if (maxDrawdown <= 0.0) return 0.0;
        if (maxDrawdown > 0.2) return 0.5;
        return maxDrawdown * 2;
    }
}
public class Main {
    public static void main(String[] args) {
        AppSettings settings = new AppSettings();
        EnsemblePredictor predictor = new EnsemblePredictor(settings);

        // Sample market data
        Map<String, Object> marketData = new HashMap<>();
        marketData.put("symbol", "AAPL");
        marketData.put("prices", Arrays.asList(150.0, 152.0, 151.5, 153.0, 154.5, 155.0, 156.0, 157.5, 158.0, 159.0));

        PredictionOutcome outcome = predictor.predict(marketData);
        System.out.println(outcome);

        Map<String, Object> backtestMetrics = predictor.backtest((List<Double>) marketData.get("prices"));
        System.out.println("Backtest metrics: " + backtestMetrics);
    }
}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * MarketAnalysis – performs market analytics, sentiment, and cognitive signal generation.
 *
 * <p>Improvements over the original Python implementation:
 * <ul>
 *   <li>Pluggable NLP service via an interface, allowing easy switch between different NLP backends (OpenNLP, CoreNLP, Hugging Face REST API).</li>
 *   <li>Embedding generation with configurable fallback (TF‑IDF based when no transformer is available).</li>
 *   <li>Sentiment analysis with a lexicon that can be extended or replaced.</li>
 *   <li>Summarization using extractive methods (Lead‑N) when no summarizer is present.</li>
 *   <li>Robust error handling and detailed logging.</li>
 *   <li>All configuration centralised in an AppSettings class, easily overridable.</li>
 * </ul>
 *
 * <p>Dependencies (add to your build):
 * <pre>
 * // SLF4J + Logback for logging
 * implementation 'org.slf4j:slf4j-api:2.0.7'
 * implementation 'ch.qos.logback:logback-classic:1.4.11'
 *
 * // Optional NLP backends (choose one or none):
 * // OpenNLP
 * implementation 'org.apache.opennlp:opennlp-tools:2.3.0'
 * // Stanford CoreNLP
 * implementation 'edu.stanford.nlp:stanford-corenlp:4.5.4'
 * // To use Hugging Face via REST, just add any HTTP client (e.g., java.net.http)
 * </pre>
 */
public class MarketAnalysis {

    private static final Logger log = LoggerFactory.getLogger(MarketAnalysis.class);

    private final AppSettings settings;
    private final NlpService nlpService;

    public MarketAnalysis(AppSettings settings) {
        this.settings = settings;
        this.nlpService = createNlpService(settings);
    }

    /**
     * Factory method that selects the best available NLP implementation.
     * In this example we always use the simple fallback; you can enhance it to probe the classpath.
     */
    private NlpService createNlpService(AppSettings settings) {
        // Try to load a custom implementation if specified in settings,
        // otherwise fall back to the simple lexicon-based approach.
        if (settings.getNlpClassName() != null) {
            try {
                Class<?> clazz = Class.forName(settings.getNlpClassName());
                return (NlpService) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.warn("Could not instantiate custom NLP service '{}', using fallback", settings.getNlpClassName(), e);
            }
        }
        log.info("Using simple lexicon‑based NLP service.");
        return new SimpleNlpService();
    }

    // ------------------------------------------------------------------------
    // Public methods
    // ------------------------------------------------------------------------

    /**
     * Embed a text into a dense vector. If no advanced model is available,
     * a bag‑of‑words/TF‑IDF based vector is returned.
     */
    public List<Double> embedText(String text) {
        return nlpService.embedText(text);
    }

    /**
     * Analyze market data and produce a trading signal.
     *
     * @param marketData must contain "prices" (List<Double>) and optionally "symbol" (String)
     * @return a map with keys: symbol, signal, confidence, ema_fast, ema_slow, rsi, analysis_time
     */
    public Map<String, Object> analyze(Map<String, Object> marketData) {
        String symbol = (String) marketData.getOrDefault("symbol", "UNKNOWN");

        @SuppressWarnings("unchecked")
        List<Double> rawPrices = (List<Double>) marketData.get("prices");
        if (rawPrices == null || rawPrices.size() < 10) {
            log.warn("Insufficient price data for analysis: {}", symbol);
            Map<String, Object> hold = new HashMap<>();
            hold.put("symbol", symbol);
            hold.put("signal", "hold");
            hold.put("confidence", 0.25);
            return hold;
        }

        // Convert to double array for calculations
        double[] prices = rawPrices.stream().mapToDouble(Double::doubleValue).toArray();

        double[] emaFast = TechnicalIndicators.calculateEma(prices, 12);
        double[] emaSlow = TechnicalIndicators.calculateEma(prices, 26);
        double[] rsi = TechnicalIndicators.calculateRsi(prices, 14);

        double lastEmaFast = emaFast[emaFast.length - 1];
        double lastEmaSlow = emaSlow[emaSlow.length - 1];
        double lastRsi = rsi[rsi.length - 1];

        String signal = "hold";
        double confidence = 0.5;

        if (lastEmaFast > lastEmaSlow && lastRsi < 70) {
            signal = "buy";
            confidence = 0.85;
        } else if (lastEmaFast < lastEmaSlow && lastRsi > 55) {
            signal = "sell";
            confidence = 0.80;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("symbol", symbol);
        result.put("signal", signal);
        result.put("confidence", confidence);
        result.put("ema_fast", lastEmaFast);
        result.put("ema_slow", lastEmaSlow);
        result.put("rsi", lastRsi);
        result.put("analysis_time", marketData.getOrDefault("timestamp", null));
        return result;
    }

    /**
     * Extract sentiment from text using the NLP service.
     */
    public Map<String, Object> extractSentiment(String text) {
        return nlpService.extractSentiment(text);
    }

    /**
     * Summarize market news text.
     */
    public String summarizeMarketNews(String text) {
        return nlpService.summarizeMarketNews(text);
    }
}

// =============================================================================
// Configuration and utility classes
// =============================================================================

/**
 * Application settings – mimics the original AppSettings.
 * In a real project this would be backed by a properties file or environment variables.
 */
class AppSettings {
    private final Map<String, Object> props;

    public AppSettings(Map<String, Object> props) {
        this.props = new HashMap<>(props);
    }

    public static AppSettings defaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("nlp_model", "en_core_web_sm"); // not used in simple mode
        defaults.put("transformer_model", "sentence-transformers/all-MiniLM-L6-v2");
        defaults.put("nlp_class_name", null); // custom NLP implementation
        return new AppSettings(defaults);
    }

    public String getNlpClassName() {
        return (String) props.getOrDefault("nlp_class_name", null);
    }

    public Object get(String key) {
        return props.get(key);
    }
}

/**
 * Utility class for technical indicators.
 */
class TechnicalIndicators {

    private TechnicalIndicators() { /* utility class */ }

    /**
     * Compute Exponential Moving Average.
     *
     * @param prices array of prices (oldest at index 0)
     * @param period the lookback period
     * @return EMA array (same length as input), with first [period-1] values using SMA
     */
    public static double[] calculateEma(double[] prices, int period) {
        if (prices.length < period) {
            throw new IllegalArgumentException("Not enough prices for period " + period);
        }
        double[] ema = new double[prices.length];
        double multiplier = 2.0 / (period + 1);

        // Compute SMA for the first value
        double sum = 0;
        for (int i = 0; i < period; i++) {
            sum += prices[i];
        }
        ema[period - 1] = sum / period;

        // EMA for the rest
        for (int i = period; i < prices.length; i++) {
            ema[i] = (prices[i] - ema[i - 1]) * multiplier + ema[i - 1];
        }
        // Backfill initial values with 0 (or you could leave them as 0, original Python returned list of same length)
        return ema;
    }

    /**
     * Compute Relative Strength Index.
     *
     * @param prices array of closing prices
     * @param period typical RSI period (e.g., 14)
     * @return RSI array (same length), values in 0-100 range
     */
    public static double[] calculateRsi(double[] prices, int period) {
        if (prices.length < period + 1) {
            throw new IllegalArgumentException("Not enough prices for RSI period " + period);
        }
        double[] rsi = new double[prices.length];
        double avgGain = 0;
        double avgLoss = 0;

        // Calculate initial gains/losses
        for (int i = 1; i <= period; i++) {
            double change = prices[i] - prices[i - 1];
            if (change > 0) avgGain += change;
            else avgLoss -= change;
        }
        avgGain /= period;
        avgLoss /= period;

        for (int i = period; i < prices.length; i++) {
            if (i == period) {
                if (avgLoss == 0) {
                    rsi[i] = 100;
                } else {
                    double rs = avgGain / avgLoss;
                    rsi[i] = 100 - (100 / (1 + rs));
                }
            } else {
                double change = prices[i] - prices[i - 1];
                double gain = Math.max(change, 0);
                double loss = Math.max(-change, 0);
                avgGain = (avgGain * (period - 1) + gain) / period;
                avgLoss = (avgLoss * (period - 1) + loss) / period;
                if (avgLoss == 0) {
                    rsi[i] = 100;
                } else {
                    double rs = avgGain / avgLoss;
                    rsi[i] = 100 - (100 / (1 + rs));
                }
            }
        }
        return rsi;
    }
}

// =============================================================================
// NLP Service and implementations
// =============================================================================

/**
 * Interface for NLP operations (embeddings, sentiment, summarization).
 */
interface NlpService {
    List<Double> embedText(String text);

    Map<String, Object> extractSentiment(String text);

    String summarizeMarketNews(String text);
}

/**
 * A simple NLP service that does not rely on external libraries.
 * Embedding uses a basic TF‑IDF over a small preset vocabulary.
 * Sentiment is lexicon‑based (positive/negative word counting).
 * Summarization uses the leading N sentences.
 */
class SimpleNlpService implements NlpService {

    // A tiny vocabulary for TF‑IDF embedding (in a real system you'd use a proper model)
    private static final List<String> VOCABULARY = List.of(
            "bullish", "bearish", "market", "rise", "fall", "strong", "weak",
            "volume", "resistance", "support", "trend", "volatility", "breakout",
            "price", "analysis", "signal", "buy", "sell", "hold", "confidence",
            "ema", "rsi", "macd", "bollinger", "sentiment", "news", "economic"
    );

    @Override
    public List<Double> embedText(String text) {
        if (text == null || text.isEmpty()) {
            return List.of(0.0);
        }
        String lower = text.toLowerCase();
        Map<String, Integer> tf = new HashMap<>();
        for (String word : lower.split("\\W+")) {
            tf.merge(word, 1, Integer::sum);
        }
        // Build vector using simple TF‑IDF (IDF approximated as 1 for all words)
        double[] vector = new double[VOCABULARY.size()];
        for (int i = 0; i < VOCABULARY.size(); i++) {
            vector[i] = tf.getOrDefault(VOCABULARY.get(i), 0);
        }
        // Normalize L2
        double norm = 0;
        for (double v : vector) norm += v * v;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) vector[i] /= norm;
        }
        return Arrays.stream(vector).boxed().collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> extractSentiment(String text) {
        // Lexicon‑based sentiment
        Set<String> positives = Set.of("bullish", "strong", "gain", "rise", "positive", "up");
        Set<String> negatives = Set.of("bearish", "weak", "drop", "fall", "negative", "down");
        String lower = text.toLowerCase();
        int posCount = 0, negCount = 0;
        for (String word : lower.split("\\W+")) {
            if (positives.contains(word)) posCount++;
            else if (negatives.contains(word)) negCount++;
        }
        int totalWords = Math.max(1, lower.split("\\W+").length);
        double polarity = (double) (posCount - negCount) / totalWords;
        double subjectivity = 0.5; // not modelled

        Map<String, Object> result = new HashMap<>();
        result.put("polarity", polarity);
        result.put("subjectivity", subjectivity);
        result.put("summary", text.length() > 256 ? text.substring(0, 256) : text);
        return result;
    }

    @Override
    public String summarizeMarketNews(String text) {
        if (text == null || text.isEmpty()) return "";
        // Simple extractive lead‑N summarization: take first 3 sentences.
        String[] sentences = text.split("(?<=[.!?])\\s+");
        int sentCount = Math.min(3, sentences.length);
        return String.join(" ", Arrays.copyOf(sentences, sentCount));
    }
}
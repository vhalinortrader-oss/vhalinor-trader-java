package com.vhalinor.trader.ai;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * VHALINOR TRADER - AI Analyzer Module (Java)
 * ==========================================================================
 * Advanced AI analysis with multi-framework support substitutes.
 *
 * Features:
 * - Simulated NLP, Transformers, Sentence Transformers
 * - Sentiment analysis (keyword-based + random placeholder)
 * - Pattern recognition (technical, behavioral, temporal)
 * - Embeddings via hashing
 * - Attention mechanisms (simple weights)
 * - Cognitive memory store
 *
 * Version: 5.0 Enhanced
 * Date: March 2026
 */
public class AIAnalyzerModule {

    // ------------------------------------------------------------------
    // Custom Exceptions (reuse or define)
    // ------------------------------------------------------------------
    public static class VhalinorException extends RuntimeException {
        public VhalinorException(String message) { super(message); }
        public VhalinorException(String message, Throwable cause) { super(message, cause); }
    }
    public static class ModelError extends VhalinorException {
        public ModelError(String message) { super(message); }
    }
    public static class ValidationError extends VhalinorException {
        public ValidationError(String message) { super(message); }
    }

    // ------------------------------------------------------------------
    // Data classes (Java records/plain classes)
    // ------------------------------------------------------------------
    public static class AIInsight {
        private String insightType;
        private double confidence;
        private double significance;
        private String description;
        private Map<String, Object> context = new LinkedHashMap<>();
        private ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);
        private String source = "ai_analyzer";
        private boolean actionable;
        private int priority = 5;
        private Map<String, Object> metadata = new LinkedHashMap<>();

        public AIInsight() {}

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("insight_type", insightType);
            map.put("confidence", confidence);
            map.put("significance", significance);
            map.put("description", description);
            map.put("context", context);
            map.put("timestamp", timestamp != null ? timestamp.toString() : null);
            map.put("source", source);
            map.put("actionable", actionable);
            map.put("priority", priority);
            map.put("metadata", metadata);
            return map;
        }

        // Getters and setters (omitted for brevity, add as needed)
        public String getInsightType() { return insightType; }
        public void setInsightType(String insightType) { this.insightType = insightType; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public double getSignificance() { return significance; }
        public void setSignificance(double significance) { this.significance = significance; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
        public ZonedDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public boolean isActionable() { return actionable; }
        public void setActionable(boolean actionable) { this.actionable = actionable; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    public static class SentimentAnalysis {
        private String text;
        private String sentiment; // positive, negative, neutral
        private double confidence;
        private Map<String, Double> emotions = new LinkedHashMap<>();
        private List<String> keywords = new ArrayList<>();
        private List<Map<String, Object>> entities = new ArrayList<>();
        private ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        public SentimentAnalysis() {}

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("text", text);
            map.put("sentiment", sentiment);
            map.put("confidence", confidence);
            map.put("emotions", emotions);
            map.put("keywords", keywords);
            map.put("entities", entities);
            map.put("timestamp", timestamp != null ? timestamp.toString() : null);
            return map;
        }

        // Getters and setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public String getSentiment() { return sentiment; }
        public void setSentiment(String sentiment) { this.sentiment = sentiment; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public Map<String, Double> getEmotions() { return emotions; }
        public void setEmotions(Map<String, Double> emotions) { this.emotions = emotions; }
        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> keywords) { this.keywords = keywords; }
        public List<Map<String, Object>> getEntities() { return entities; }
        public void setEntities(List<Map<String, Object>> entities) { this.entities = entities; }
        public ZonedDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
    }

    public static class PatternAnalysis {
        private String patternType;
        private String patternName;
        private double confidence;
        private Map<String, Object> features = new LinkedHashMap<>();
        private int occurrences;
        private List<String> timeframes = new ArrayList<>();
        private String prediction;
        private ZonedDateTime timestamp = ZonedDateTime.now(ZoneOffset.UTC);

        public PatternAnalysis() {}

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("pattern_type", patternType);
            map.put("pattern_name", patternName);
            map.put("confidence", confidence);
            map.put("features", features);
            map.put("occurrences", occurrences);
            map.put("timeframes", timeframes);
            map.put("prediction", prediction);
            map.put("timestamp", timestamp != null ? timestamp.toString() : null);
            return map;
        }

        // Getters and setters
        public String getPatternType() { return patternType; }
        public void setPatternType(String patternType) { this.patternType = patternType; }
        public String getPatternName() { return patternName; }
        public void setPatternName(String patternName) { this.patternName = patternName; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public Map<String, Object> getFeatures() { return features; }
        public void setFeatures(Map<String, Object> features) { this.features = features; }
        public int getOccurrences() { return occurrences; }
        public void setOccurrences(int occurrences) { this.occurrences = occurrences; }
        public List<String> getTimeframes() { return timeframes; }
        public void setTimeframes(List<String> timeframes) { this.timeframes = timeframes; }
        public String getPrediction() { return prediction; }
        public void setPrediction(String prediction) { this.prediction = prediction; }
        public ZonedDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
    }

    // ------------------------------------------------------------------
    // AI Analyzer (main class)
    // ------------------------------------------------------------------
    public static class AIAnalyzer {
        protected static final Logger logger = Logger.getLogger("ai_analyzer");

        // Library availability flags (simulated)
        protected static final boolean NUMPY_AVAILABLE = false;
        protected static final boolean PANDAS_AVAILABLE = false;
        protected static final boolean TORCH_AVAILABLE = false;
        protected static final boolean TENSORFLOW_AVAILABLE = false;
        protected static final boolean SPACY_AVAILABLE = false;
        protected static final boolean TRANSFORMERS_AVAILABLE = false;
        protected static final boolean SKLEARN_AVAILABLE = false;
        protected static final boolean SENTENCE_TRANSFORMERS_AVAILABLE = false;

        protected Map<String, Object> config;
        protected Object nlpModel = null; // placeholder
        protected Object transformerModel = null; // placeholder
        protected Object sentenceTransformer = null; // placeholder
        protected Map<String, Object> mlModels = new ConcurrentHashMap<>();

        protected Deque<AIInsight> insightsBuffer = new LinkedList<>();
        protected Deque<SentimentAnalysis> sentimentHistory = new LinkedList<>();
        protected Map<String, PatternAnalysis> patternCache = new ConcurrentHashMap<>();

        protected int analysisCount = 0;
        protected Deque<Double> processingTimes = new LinkedList<>();

        protected final ReentrantLock lock = new ReentrantLock();

        public AIAnalyzer(Map<String, Object> config) {
            this.config = config;
            initializeNLP();
            initializeTransformers();
            initializeSentenceTransformers();
            logger.info("AI analyzer initialized");
        }

        public AIAnalyzer() { this(null); }

        protected void initializeNLP() {
            if (!SPACY_AVAILABLE) {
                logger.warning("spaCy not available, NLP features limited");
                return;
            }
            // In real Java, you'd load a model (e.g., using DL4J or Stanford NLP)
            nlpModel = new Object(); // dummy
            logger.info("spaCy model loaded (simulated)");
        }

        protected void initializeTransformers() {
            if (!TRANSFORMERS_AVAILABLE) {
                logger.warning("Transformers not available, using fallback analysis");
                return;
            }
            transformerModel = new Object(); // dummy
            logger.info("Transformer sentiment model loaded (simulated)");
        }

        protected void initializeSentenceTransformers() {
            if (!SENTENCE_TRANSFORMERS_AVAILABLE) {
                logger.warning("Sentence transformers not available, using fallback embeddings");
                return;
            }
            sentenceTransformer = new Object(); // dummy
            logger.info("Sentence transformer model loaded (simulated)");
        }

        // ------------------------------------------------------------------
        // Sentiment analysis (async)
        // ------------------------------------------------------------------
        public CompletableFuture<SentimentAnalysis> analyzeSentiment(String text) {
            return CompletableFuture.supplyAsync(() -> {
                long startTime = System.nanoTime();
                try {
                    SentimentAnalysis analysis;
                    if (transformerModel != null) {
                        // Simulated transformer result (positive/negative)
                        String label = Math.random() > 0.5 ? "POSITIVE" : "NEGATIVE";
                        double score = 0.6 + Math.random() * 0.4;
                        String sentiment = "POSITIVE".equals(label) ? "positive" : "negative";
                        Map<String, Double> emotions = extractEmotions(text);
                        Object[] nlpFeatures = extractNLPFeatures(text).join();
                        List<String> keywords = (List<String>) nlpFeatures[0];
                        List<Map<String, Object>> entities = (List<Map<String, Object>>) nlpFeatures[1];
                        analysis = new SentimentAnalysis();
                        analysis.setText(text);
                        analysis.setSentiment(sentiment);
                        analysis.setConfidence(score);
                        analysis.setEmotions(emotions);
                        analysis.setKeywords(keywords);
                        analysis.setEntities(entities);
                    } else {
                        double[] result = basicSentimentAnalysis(text);
                        String sentiment = result[0] == 1.0 ? "positive" : (result[0] == -1.0 ? "negative" : "neutral");
                        double confidence = result[1];
                        analysis = new SentimentAnalysis();
                        analysis.setText(text);
                        analysis.setSentiment(sentiment);
                        analysis.setConfidence(confidence);
                        // No NLP features
                    }

                    lock.lock();
                    try {
                        sentimentHistory.addLast(analysis);
                    } finally {
                        lock.unlock();
                    }

                    double elapsed = (System.nanoTime() - startTime) / 1_000_000_000.0;
                    processingTimes.addLast(elapsed);
                    analysisCount++;
                    logger.fine("Sentiment analysis completed in " + String.format("%.3f", elapsed) + "s");
                    return analysis;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error in sentiment analysis: " + e.getMessage(), e);
                    throw new ModelError("Sentiment analysis failed: " + e.getMessage());
                }
            });
        }

        protected Map<String, Double> extractEmotions(String text) {
            Map<String, List<String>> emotionKeywords = new LinkedHashMap<>();
            emotionKeywords.put("joy", Arrays.asList("happy", "joy", "excited", "thrilled", "delighted"));
            emotionKeywords.put("sadness", Arrays.asList("sad", "unhappy", "depressed", "disappointed", "gloomy"));
            emotionKeywords.put("anger", Arrays.asList("angry", "furious", "mad", "irritated", "enraged"));
            emotionKeywords.put("fear", Arrays.asList("afraid", "scared", "fearful", "anxious", "worried"));
            emotionKeywords.put("surprise", Arrays.asList("surprised", "amazed", "shocked", "astonished"));
            emotionKeywords.put("disgust", Arrays.asList("disgusted", "revolted", "repulsed", "sickened"));

            Map<String, Double> emotions = new LinkedHashMap<>();
            String textLower = text.toLowerCase();
            for (Map.Entry<String, List<String>> entry : emotionKeywords.entrySet()) {
                double score = 0.0;
                for (String kw : entry.getValue()) {
                    if (textLower.contains(kw)) score += 1.0;
                }
                emotions.put(entry.getKey(), Math.min(score / entry.getValue().size(), 1.0));
            }
            return emotions;
        }

        protected CompletableFuture<Object> extractNLPFeatures(String text) {
            if (nlpModel == null) {
                // Fallback: simple keyword extraction
                List<String> words = new ArrayList<>();
                Matcher m = Pattern.compile("\\b\\w+\\b").matcher(text.toLowerCase());
                while (m.find()) words.add(m.group());
                Set<String> uniqueWords = new LinkedHashSet<>(words);
                List<String> keywords = new ArrayList<>();
                for (String w : uniqueWords) if (w.length() > 3) keywords.add(w);
                return CompletableFuture.completedFuture(new Object[]{keywords, new ArrayList<Map<String, Object>>()});
            }
            // Simulated spaCy processing
            return CompletableFuture.supplyAsync(() -> {
                List<String> keywords = new ArrayList<>();
                keywords.add("sample");
                List<Map<String, Object>> entities = new ArrayList<>();
                Map<String, Object> ent = new LinkedHashMap<>();
                ent.put("text", "Sample");
                ent.put("label", "ORG");
                ent.put("start", 0);
                ent.put("end", 6);
                ent.put("confidence", 1.0);
                entities.add(ent);
                return new Object[]{keywords, entities};
            });
        }

        protected double[] basicSentimentAnalysis(String text) {
            List<String> positiveWords = Arrays.asList("good", "great", "excellent", "positive", "bullish", "buy", "strong");
            List<String> negativeWords = Arrays.asList("bad", "terrible", "negative", "bearish", "sell", "weak", "poor");
            String textLower = text.toLowerCase();
            int posCount = 0, negCount = 0;
            for (String w : positiveWords) if (textLower.contains(w)) posCount++;
            for (String w : negativeWords) if (textLower.contains(w)) negCount++;
            if (posCount > negCount) {
                return new double[]{1.0, Math.min((double)posCount / (posCount + negCount), 1.0)};
            } else if (negCount > posCount) {
                return new double[]{-1.0, Math.min((double)negCount / (posCount + negCount), 1.0)};
            } else {
                return new double[]{0.0, 0.5};
            }
        }

        // ------------------------------------------------------------------
        // Pattern analysis
        // ------------------------------------------------------------------
        public CompletableFuture<List<PatternAnalysis>> analyzePatterns(List<Map<String, Object>> data) {
            return CompletableFuture.supplyAsync(() -> {
                if (data == null || data.isEmpty()) return new ArrayList<>();
                try {
                    List<PatternAnalysis> patterns = new ArrayList<>();
                    patterns.addAll(analyzeTechnicalPatterns(data).join());
                    patterns.addAll(analyzeBehavioralPatterns(data).join());
                    patterns.addAll(analyzeTemporalPatterns(data).join());
                    logger.info("Analyzed " + patterns.size() + " patterns");
                    return patterns;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error in pattern analysis: " + e.getMessage(), e);
                    throw new ModelError("Pattern analysis failed: " + e.getMessage());
                }
            });
        }

        protected CompletableFuture<List<PatternAnalysis>> analyzeTechnicalPatterns(List<Map<String, Object>> data) {
            return CompletableFuture.supplyAsync(() -> {
                List<PatternAnalysis> patterns = new ArrayList<>();
                // Extract price list
                List<Double> prices = new ArrayList<>();
                for (Map<String, Object> row : data) {
                    Object p = row.get("price");
                    if (p instanceof Number) prices.add(((Number) p).doubleValue());
                }
                if (prices.size() < 20) return patterns;
                double[] priceArr = prices.stream().mapToDouble(Double::doubleValue).toArray();
                double support = findMin(priceArr, priceArr.length - 20, priceArr.length);
                double resistance = findMax(priceArr, priceArr.length - 20, priceArr.length);
                if (support < Double.POSITIVE_INFINITY) {
                    PatternAnalysis pa = new PatternAnalysis();
                    pa.setPatternType("technical");
                    pa.setPatternName("support_level");
                    pa.setConfidence(0.7);
                    Map<String, Object> feat = new LinkedHashMap<>();
                    feat.put("support_level", support);
                    pa.setFeatures(feat);
                    pa.setOccurrences(1);
                    pa.setTimeframes(Arrays.asList("1h", "4h", "1d"));
                    pa.setPrediction("bounce_potential");
                    patterns.add(pa);
                }
                if (resistance > Double.NEGATIVE_INFINITY) {
                    PatternAnalysis pa = new PatternAnalysis();
                    pa.setPatternType("technical");
                    pa.setPatternName("resistance_level");
                    pa.setConfidence(0.7);
                    Map<String, Object> feat = new LinkedHashMap<>();
                    feat.put("resistance_level", resistance);
                    pa.setFeatures(feat);
                    pa.setOccurrences(1);
                    pa.setTimeframes(Arrays.asList("1h", "4h", "1d"));
                    pa.setPrediction("rejection_potential");
                    patterns.add(pa);
                }
                // Trend
                String trend = analyzeTrend(priceArr);
                if (trend != null) {
                    PatternAnalysis pa = new PatternAnalysis();
                    pa.setPatternType("technical");
                    pa.setPatternName("trend_" + trend);
                    pa.setConfidence(0.8);
                    Map<String, Object> feat = new LinkedHashMap<>();
                    feat.put("trend_direction", trend);
                    pa.setFeatures(feat);
                    pa.setOccurrences(1);
                    pa.setTimeframes(Arrays.asList("1h", "4h"));
                    pa.setPrediction("continue_" + trend);
                    patterns.add(pa);
                }
                return patterns;
            });
        }

        private double findMin(double[] arr, int from, int to) {
            double min = Double.MAX_VALUE;
            for (int i = from; i < to; i++) if (arr[i] < min) min = arr[i];
            return min == Double.MAX_VALUE ? 0 : min;
        }
        private double findMax(double[] arr, int from, int to) {
            double max = -Double.MAX_VALUE;
            for (int i = from; i < to; i++) if (arr[i] > max) max = arr[i];
            return max == -Double.MAX_VALUE ? 0 : max;
        }

        protected String analyzeTrend(double[] prices) {
            if (prices.length < 10) return null;
            double shortMA = 0, longMA = 0;
            for (int i = prices.length - 5; i < prices.length; i++) shortMA += prices[i];
            shortMA /= 5;
            for (int i = prices.length - 20; i < prices.length; i++) longMA += prices[i];
            longMA /= 20;
            if (shortMA > longMA) return "bullish";
            else if (shortMA < longMA) return "bearish";
            else return "sideways";
        }

        protected CompletableFuture<List<PatternAnalysis>> analyzeBehavioralPatterns(List<Map<String, Object>> data) {
            return CompletableFuture.supplyAsync(() -> {
                List<PatternAnalysis> patterns = new ArrayList<>();
                if (!data.isEmpty() && data.get(0).containsKey("volume")) {
                    double sumVol = 0;
                    for (Map<String, Object> row : data) {
                        Object v = row.get("volume");
                        if (v instanceof Number) sumVol += ((Number) v).doubleValue();
                    }
                    double avgVolume = sumVol / data.size();
                    int cnt = 0;
                    for (Map<String, Object> row : data) {
                        if (cnt++ > data.size() - 11) { // last 10
                            Object v = row.get("volume");
                            if (v instanceof Number) {
                                double vol = ((Number) v).doubleValue();
                                if (vol > avgVolume * 2) {
                                    PatternAnalysis pa = new PatternAnalysis();
                                    pa.setPatternType("behavioral");
                                    pa.setPatternName("volume_spike");
                                    pa.setConfidence(0.6);
                                    Map<String, Object> feat = new LinkedHashMap<>();
                                    feat.put("volume", vol);
                                    feat.put("avg_volume", avgVolume);
                                    pa.setFeatures(feat);
                                    pa.setOccurrences(1);
                                    pa.setTimeframes(Arrays.asList("1m", "5m"));
                                    pa.setPrediction("high_volatility");
                                    patterns.add(pa);
                                }
                            }
                        }
                    }
                }
                return patterns;
            });
        }

        protected CompletableFuture<List<PatternAnalysis>> analyzeTemporalPatterns(List<Map<String, Object>> data) {
            return CompletableFuture.supplyAsync(() -> {
                List<PatternAnalysis> patterns = new ArrayList<>();
                int hour = ZonedDateTime.now().getHour();
                if (hour >= 9 && hour <= 16) {
                    PatternAnalysis pa = new PatternAnalysis();
                    pa.setPatternType("temporal");
                    pa.setPatternName("market_hours");
                    pa.setConfidence(0.8);
                    Map<String, Object> feat = new LinkedHashMap<>();
                    feat.put("hour", hour);
                    feat.put("session", "trading");
                    pa.setFeatures(feat);
                    pa.setOccurrences(1);
                    pa.setTimeframes(Arrays.asList("1h"));
                    pa.setPrediction("higher_liquidity");
                    patterns.add(pa);
                }
                return patterns;
            });
        }

        // ------------------------------------------------------------------
        // Embeddings
        // ------------------------------------------------------------------
        public CompletableFuture<List<double[]>> generateEmbeddings(List<String> texts) {
            return CompletableFuture.supplyAsync(() -> {
                if (texts == null || texts.isEmpty()) return new ArrayList<>();
                try {
                    if (sentenceTransformer != null) {
                        // Simulated: random embeddings of length 384
                        List<double[]> embs = new ArrayList<>();
                        for (String t : texts) {
                            double[] vec = new double[384];
                            for (int i = 0; i < 384; i++) vec[i] = Math.random();
                            embs.add(vec);
                        }
                        return embs;
                    } else if (NUMPY_AVAILABLE) {
                        return generateBasicEmbeddings(texts);
                    } else {
                        // Ultimate fallback: random
                        List<double[]> embs = new ArrayList<>();
                        for (String t : texts) {
                            double[] vec = new double[384];
                            for (int i = 0; i < 384; i++) vec[i] = Math.random();
                            embs.add(vec);
                        }
                        return embs;
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error generating embeddings: " + e.getMessage(), e);
                    throw new ModelError("Embedding generation failed: " + e.getMessage());
                }
            });
        }

        protected List<double[]> generateBasicEmbeddings(List<String> texts) {
            List<double[]> embeddings = new ArrayList<>();
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException nsae) {
                md5 = null;
            }
            for (String text : texts) {
                double[] embedding = new double[384];
                if (md5 != null) {
                    byte[] digest = md5.digest(text.getBytes(StandardCharsets.UTF_8));
                    // Fill embedding with hash values normalized to 0-1
                    for (int i = 0; i < 384 && i * 2 + 1 < digest.length; i++) {
                        int val = ((digest[i * 2] & 0xFF) << 8) | (digest[i * 2 + 1] & 0xFF);
                        embedding[i] = val / 65535.0;
                    }
                } else {
                    // Random if MD5 not available
                    for (int i = 0; i < 384; i++) embedding[i] = Math.random();
                }
                embeddings.add(embedding);
            }
            return embeddings;
        }

        // ------------------------------------------------------------------
        // Attention analysis
        // ------------------------------------------------------------------
        public CompletableFuture<AIInsight> analyzeWithAttention(Map<String, Object> data) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    double[] attentionWeights = calculateAttentionWeights(data);
                    double meanWeight = 0;
                    for (double w : attentionWeights) meanWeight += w;
                    meanWeight /= attentionWeights.length;
                    double maxWeight = 0;
                    for (double w : attentionWeights) if (w > maxWeight) maxWeight = w;
                    AIInsight insight = new AIInsight();
                    insight.setInsightType("attention_analysis");
                    insight.setConfidence(meanWeight);
                    insight.setSignificance(maxWeight);
                    insight.setDescription("Attention-based analysis of market data");
                    Map<String, Object> ctx = new LinkedHashMap<>();
                    ctx.put("attention_weights", attentionWeights);
                    ctx.put("key_factors", identifyKeyFactors(data, attentionWeights));
                    insight.setContext(ctx);
                    insight.setActionable(true);
                    insight.setPriority(3);
                    return insight;
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error in attention analysis: " + e.getMessage(), e);
                    throw new ModelError("Attention analysis failed: " + e.getMessage());
                }
            });
        }

        protected double[] calculateAttentionWeights(Map<String, Object> data) {
            Map<String, Double> featureWeights = new LinkedHashMap<>();
            featureWeights.put("price", 0.3);
            featureWeights.put("volume", 0.2);
            featureWeights.put("volatility", 0.15);
            featureWeights.put("trend", 0.15);
            featureWeights.put("sentiment", 0.1);
            featureWeights.put("news_impact", 0.05);
            featureWeights.put("technical_signals", 0.05);

            double[] weights = new double[featureWeights.size()];
            int idx = 0;
            double totalWeight = 0;
            for (Map.Entry<String, Double> entry : featureWeights.entrySet()) {
                String feature = entry.getKey();
                double weight = entry.getValue();
                if (data.containsKey(feature)) {
                    Object val = data.get(feature);
                    double norm = 0.5;
                    if (val instanceof Number) {
                        double dv = ((Number) val).doubleValue();
                        norm = Math.min(Math.max(dv, 0.0), 1.0);
                    }
                    weights[idx] = norm * weight;
                } else {
                    weights[idx] = 0.0;
                }
                totalWeight += weights[idx];
                idx++;
            }
            if (totalWeight > 0) {
                for (int i = 0; i < weights.length; i++) weights[i] /= totalWeight;
            }
            return weights;
        }

        protected List<String> identifyKeyFactors(Map<String, Object> data, double[] attentionWeights) {
            List<String> features = Arrays.asList("price", "volume", "volatility", "trend", "sentiment", "news_impact", "technical_signals");
            List<String> availableFeatures = new ArrayList<>();
            for (String f : features) if (data.containsKey(f)) availableFeatures.add(f);
            // Sort by weight (descending) and pick top 3
            if (attentionWeights.length >= availableFeatures.size()) {
                // Create index list and sort
                Integer[] indices = new Integer[attentionWeights.length];
                for (int i = 0; i < indices.length; i++) indices[i] = i;
                Arrays.sort(indices, (a, b) -> Double.compare(attentionWeights[b], attentionWeights[a]));
                List<String> keyFactors = new ArrayList<>();
                for (int i = 0; i < Math.min(3, availableFeatures.size()); i++) {
                    keyFactors.add(availableFeatures.get(indices[i]));
                }
                return keyFactors;
            } else {
                return availableFeatures;
            }
        }

        // Performance stats
        public Map<String, Object> getPerformanceStats() {
            lock.lock();
            try {
                Map<String, Object> stats = new LinkedHashMap<>();
                stats.put("analysis_count", analysisCount);
                stats.put("avg_processing_time", processingTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
                stats.put("insights_buffer_size", insightsBuffer.size());
                stats.put("sentiment_history_size", sentimentHistory.size());
                Map<String, Boolean> models = new LinkedHashMap<>();
                models.put("nlp", nlpModel != null);
                models.put("transformers", transformerModel != null);
                models.put("sentence_transformers", sentenceTransformer != null);
                stats.put("models_loaded", models);
                Map<String, Boolean> libs = new LinkedHashMap<>();
                libs.put("numpy", NUMPY_AVAILABLE);
                libs.put("pandas", PANDAS_AVAILABLE);
                libs.put("torch", TORCH_AVAILABLE);
                libs.put("tensorflow", TENSORFLOW_AVAILABLE);
                libs.put("spacy", SPACY_AVAILABLE);
                libs.put("transformers", TRANSFORMERS_AVAILABLE);
                libs.put("sklearn", SKLEARN_AVAILABLE);
                libs.put("sentence_transformers", SENTENCE_TRANSFORMERS_AVAILABLE);
                stats.put("libraries_available", libs);
                return stats;
            } finally {
                lock.unlock();
            }
        }
    }

    // ------------------------------------------------------------------
    // Cognitive Analyzer (extends AIAnalyzer)
    // ------------------------------------------------------------------
    public static class CognitiveAnalyzer extends AIAnalyzer {
        private Map<String, Map<String, Object>> memoryStore = new ConcurrentHashMap<>();
        private Deque<Map<String, Object>> contextWindow = new LinkedList<>();
        private String cognitiveState = "learning";

        public CognitiveAnalyzer(Map<String, Object> config) {
            super(config);
            logger.info("Cognitive analyzer initialized");
        }
        public CognitiveAnalyzer() { this(null); }

        public CompletableFuture<AIInsight> analyzeWithMemory(Map<String, Object> data) {
            return CompletableFuture.supplyAsync(() -> {
                // Add to context window
                contextWindow.addLast(data);
                // Retrieve relevant memories
                List<Map<String, Object>> relevantMemories = retrieveRelevantMemories(data);
                // Analyze with attention
                AIInsight insight = analyzeWithAttention(data).join();
                // Update context
                insight.getContext().put("relevant_memories", relevantMemories);
                insight.getContext().put("cognitive_state", cognitiveState);
                // Store memory
                storeMemory(data, insight);
                return insight;
            });
        }

        protected List<Map<String, Object>> retrieveRelevantMemories(Map<String, Object> data) {
            List<Map<String, Object>> relevantMemories = new ArrayList<>();
            for (Map.Entry<String, Map<String, Object>> entry : memoryStore.entrySet()) {
                Map<String, Object> memory = entry.getValue();
                double similarity = calculateSimilarity(data, (Map<String, Object>) memory.get("data"));
                if (similarity > 0.7) {
                    relevantMemories.add(memory);
                }
            }
            // Top 5
            relevantMemories.sort((a, b) -> {
                double simA = calculateSimilarity(data, (Map<String, Object>) a.get("data"));
                double simB = calculateSimilarity(data, (Map<String, Object>) b.get("data"));
                return Double.compare(simB, simA);
            });
            return relevantMemories.size() > 5 ? relevantMemories.subList(0, 5) : relevantMemories;
        }

        protected double calculateSimilarity(Map<String, Object> data1, Map<String, Object> data2) {
            Set<String> commonFeatures = new HashSet<>(data1.keySet());
            commonFeatures.retainAll(data2.keySet());
            if (commonFeatures.isEmpty()) return 0.0;
            double totalSim = 0;
            for (String feature : commonFeatures) {
                double val1 = 0.0, val2 = 0.0;
                if (data1.get(feature) instanceof Number) val1 = ((Number) data1.get(feature)).doubleValue();
                if (data2.get(feature) instanceof Number) val2 = ((Number) data2.get(feature)).doubleValue();
                double sim = 1.0 - Math.abs(val1 - val2) / Math.max(Math.max(Math.abs(val1), Math.abs(val2)), 1.0);
                totalSim += sim;
            }
            return totalSim / commonFeatures.size();
        }

        protected void storeMemory(Map<String, Object> data, AIInsight insight) {
            String memoryId = "memory_" + System.currentTimeMillis();
            Map<String, Object> mem = new LinkedHashMap<>();
            mem.put("data", data);
            mem.put("insight", insight.toMap());
            mem.put("timestamp", ZonedDateTime.now().toString());
            memoryStore.put(memoryId, mem);
            // Limit size
            if (memoryStore.size() > 1000) {
                // Remove oldest (first key in iteration)
                String oldestKey = memoryStore.keySet().iterator().next();
                memoryStore.remove(oldestKey);
            }
        }
    }

    // ------------------------------------------------------------------
    // Simple main method to demonstrate usage
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        AIAnalyzer analyzer = new AIAnalyzer();
        String sampleText = "The market is showing strong bullish signals today!";
        SentimentAnalysis sentiment = analyzer.analyzeSentiment(sampleText).join();
        System.out.println("Sentiment: " + sentiment.getSentiment() + " (confidence: " + sentiment.getConfidence() + ")");

        // Pattern analysis
        List<Map<String, Object>> fakeData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("price", 100 + Math.random() * 10);
            row.put("volume", 500 + Math.random() * 200);
            fakeData.add(row);
        }
        List<PatternAnalysis> patterns = analyzer.analyzePatterns(fakeData).join();
        for (PatternAnalysis p : patterns) {
            System.out.println("Pattern: " + p.getPatternName() + " - " + p.getPatternType());
        }

        CognitiveAnalyzer cog = new CognitiveAnalyzer();
        AIInsight insight = cog.analyzeWithMemory(fakeData.get(0)).join();
        System.out.println("Insight: " + insight.getDescription() + " (confidence: " + insight.getConfidence() + ")");
    }
}
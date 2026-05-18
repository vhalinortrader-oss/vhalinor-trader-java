```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Converted from the original Python asyncio-based AI trading bot.
 *
 * Assumptions:
 * - AppSettings, RealTimeDataFetcher, MarketAnalysis, EnsemblePredictor,
 *   RealTimeService and DashboardApp exist as Java classes with similar APIs.
 * - Prediction is a simple POJO with getDirection(), getScore(), getMetadata().
 * - Service publish method may throw exceptions; we handle them gracefully.
 */
public class AiTradingBotMain {

    private static final Logger logger = LoggerFactory.getLogger(AiTradingBotMain.class);

    // Helper to build the payload map, mirroring _build_signal_payload
    private static Map<String, Object> buildSignalPayload(String symbol,
                                                          Map<String, Object> signal,
                                                          Prediction prediction) {
        Map<String, Object> predictionMap = new HashMap<>();
        predictionMap.put("direction", prediction.getDirection());
        predictionMap.put("score", prediction.getScore());
        predictionMap.put("metadata", prediction.getMetadata());

        Map<String, Object> payload = new HashMap<>();
        payload.put("symbol", symbol);
        payload.put("signal", signal);
        payload.put("prediction", predictionMap);
        payload.put("timestamp", Instant.now().toString()); // ISO-8601 with UTC offset
        return payload;
    }

    /**
     * Runs the core AI trading services and publishes live signals.
     * This method is blocking and designed to be executed in a dedicated thread.
     */
    public static void startBot(AppSettings settings, RealTimeService service) throws Exception {
        RealTimeDataFetcher fetcher = new RealTimeDataFetcher(settings);
        MarketAnalysis analyzer = new MarketAnalysis(settings);
        EnsemblePredictor predictor = new EnsemblePredictor(settings);

        try {
            fetcher.initialize(); // blocking call
            for (String symbol : settings.getStrategySymbols()) {
                Map<String, Object> latestData = fetcher.fetchMarketSnapshot(symbol);
                Map<String, Object> signal = analyzer.analyze(latestData);
                Prediction prediction = predictor.predict(latestData);

                Map<String, Object> payload = buildSignalPayload(
                        latestData.getOrDefault("symbol", symbol).toString(),
                        signal,
                        prediction
                );
                logger.info("Market signal: {}", payload);

                if (service != null) {
                    try {
                        service.publish(settings.getRedisChannel(), payload);
                    } catch (Exception publishError) {
                        logger.warn("Failed to publish live signal: {}", publishError.getMessage());
                    }
                }
            }
        } finally {
            fetcher.close(); // ensure resources are released
        }
    }

    /**
     * Starts the dashboard (FastAPI equivalent) as a blocking call.
     * The dashboard server runs until the JVM is terminated or an interrupt is received.
     */
    public static void startDashboard(AppSettings settings, RealTimeService service) {
        // Assuming DashboardApp.create returns an object with a start() method
        // that listens on the configured host:port and blocks.
        DashboardApp app = DashboardApp.create(settings, service);
        logger.info("Starting dashboard at http://{}:{}", settings.getDashboardHost(), settings.getDashboardPort());
        app.start(); // blocks until server shutdown
    }

    public static void main(String[] args) {
        AppSettings settings = new AppSettings();
        logger.info("Starting AI Trading Bot");

        RealTimeService service = null;
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            service = new RealTimeService(settings);
            service.connect(); // blocking connect

            if (settings.isDashboardEnabled()) {
                // Run bot and dashboard concurrently
                Future<?> botFuture = executor.submit(() -> {
                    try {
                        startBot(settings, service);
                    } catch (Exception e) {
                        logger.error("Bot task failed", e);
                    }
                });
                Future<?> dashboardFuture = executor.submit(() -> startDashboard(settings, service));

                // Wait for both to complete (dashboard blocks indefinitely)
                botFuture.get();
                dashboardFuture.get();
            } else {
                // Run only the bot (blocking in main thread is acceptable)
                startBot(settings, service);
            }
        } catch (Exception e) {
            logger.error("AI Trading Bot encountered a fatal error", e);
        } finally {
            if (service != null) {
                try {
                    service.shutdown();
                } catch (Exception shutdownError) {
                    logger.warn("Error shutting down service: {}", shutdownError.getMessage());
                }
            }
            executor.shutdownNow();
            logger.info("AI Trading Bot finished");
        }
    }
}
```
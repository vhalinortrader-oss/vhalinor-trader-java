package ai.trading.bot.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * BlockchainClient handles Web3 connectivity and simple blockchain helpers.
 *
 * <p>Improvements over the original Python implementation:
 * <ul>
 *   <li>Uses Web3j – the standard Java Ethereum library – for robust provider communication.</li>
 *   <li>Adds connection caching for `gasPrice` and `chainId` to reduce RPC calls.</li>
 *   <li>Retry logic with exponential backoff for transient network issues.</li>
 *   <li>Graceful shutdown via {@link #close()} to release HTTP resources.</li>
 *   <li>Comprehensive error handling with clear log messages.</li>
 * </ul>
 */
public class BlockchainClient {

    private static final Logger log = LoggerFactory.getLogger(BlockchainClient.class);

    // Configuration
    private final ClientSettings settings;
    private Web3j web3;
    private volatile boolean connected = false;

    // Simple cache for gasPrice and chainId (TTL-based invalidation)
    private long lastGasPriceFetchTime = 0;
    private BigInteger cachedGasPrice;
    private long lastChainIdFetchTime = 0;
    private long cachedChainId;

    public BlockchainClient(ClientSettings settings) {
        this.settings = settings;
    }

    /**
     * Connects to the configured Web3 provider.
     *
     * @return true if the connection was successful
     */
    public boolean connect() {
        try {
            // Initialize the Web3j instance with a configurable HTTP service
            HttpService httpService = new HttpService(settings.getProviderUrl());
            httpService.setConnectTimeout(settings.getConnectionTimeoutSeconds());
            httpService.setReadTimeout(settings.getReadTimeoutSeconds());

            web3 = Web3j.build(httpService);
            // Verify connection by requesting the chain ID (cheapest call)
            EthChainId chainIdResponse = executeWithRetry(() -> web3.ethChainId().send());
            if (chainIdResponse != null && chainIdResponse.getChainId() != null) {
                cachedChainId = chainIdResponse.getChainId().longValue();
                lastChainIdFetchTime = System.currentTimeMillis();
                connected = true;
                log.info("Connected to Web3 provider (chain ID: {})", cachedChainId);
                return true;
            }
            log.warn("Web3 provider is unreachable - could not obtain chain ID");
            return false;
        } catch (Exception e) {
            log.error("Failed to initialize Web3: {}", e.getMessage(), e);
            connected = false;
            return false;
        }
    }

    /**
     * Checks if the client is currently connected to the Web3 provider.
     */
    public boolean isConnected() {
        return connected && web3 != null;
    }

    /**
     * Returns the ETH balance of the given address in wei.
     *
     * @param address Ethereum address (0x...)
     * @return map with "address" and "balance_wei", or null on failure
     */
    public Optional<Map<String, Object>> getAccountBalance(String address) {
        if (!isConnected()) {
            log.warn("Web3 client not connected");
            return Optional.empty();
        }
        try {
            EthGetBalance balanceResponse = executeWithRetry(
                    () -> web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send());
            if (balanceResponse == null || balanceResponse.getBalance() == null) {
                log.warn("Empty balance response for address {}", address);
                return Optional.empty();
            }
            Map<String, Object> result = new HashMap<>();
            result.put("address", address);
            result.put("balance_wei", balanceResponse.getBalance());
            return Optional.of(result);
        } catch (Exception e) {
            log.error("Failed to fetch account balance for {}: {}", address, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Returns the current gas price in wei, using a short-lived cache.
     *
     * @return gas price in wei, or null on failure
     */
    public Optional<BigInteger> getGasPrice() {
        if (!isConnected()) {
            log.warn("Web3 client not connected");
            return Optional.empty();
        }
        // Return cached value if within TTL
        if (cachedGasPrice != null &&
                (System.currentTimeMillis() - lastGasPriceFetchTime) < settings.getGasPriceCacheMillis()) {
            return Optional.of(cachedGasPrice);
        }
        try {
            EthGasPrice gasPriceResponse = executeWithRetry(() -> web3.ethGasPrice().send());
            if (gasPriceResponse == null || gasPriceResponse.getGasPrice() == null) {
                log.warn("Empty gas price response");
                return Optional.empty();
            }
            cachedGasPrice = gasPriceResponse.getGasPrice();
            lastGasPriceFetchTime = System.currentTimeMillis();
            return Optional.of(cachedGasPrice);
        } catch (Exception e) {
            log.error("Failed to fetch gas price: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Returns the connected chain ID, using a short-lived cache.
     *
     * @return chain ID as long, or null on failure
     */
    public Optional<Long> getChainId() {
        if (!isConnected()) {
            log.warn("Web3 client not connected");
            return Optional.empty();
        }
        if (cachedChainId > 0 &&
                (System.currentTimeMillis() - lastChainIdFetchTime) < settings.getChainIdCacheMillis()) {
            return Optional.of(cachedChainId);
        }
        try {
            EthChainId chainIdResponse = executeWithRetry(() -> web3.ethChainId().send());
            if (chainIdResponse == null || chainIdResponse.getChainId() == null) {
                log.warn("Empty chain ID response");
                return Optional.empty();
            }
            cachedChainId = chainIdResponse.getChainId().longValue();
            lastChainIdFetchTime = System.currentTimeMillis();
            return Optional.of(cachedChainId);
        } catch (Exception e) {
            log.error("Failed to fetch chain ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Closes the underlying HTTP client and marks the client as disconnected.
     */
    public void close() {
        if (web3 != null) {
            try {
                web3.shutdown();
                connected = false;
                log.info("Web3 client shut down successfully");
            } catch (Exception e) {
                log.error("Error during Web3 shutdown: {}", e.getMessage(), e);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------------

    /**
     * Executes a Web3 call with configurable retries on transient connection errors.
     */
    private <T> T executeWithRetry(CallableWithException<T> callable) throws Exception {
        int maxRetries = settings.getMaxRetries();
        long initialDelayMs = settings.getRetryInitialDelayMs();
        Exception lastException = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return callable.call();
            } catch (ClientConnectionException e) {
                lastException = e;
                if (attempt < maxRetries) {
                    long delay = initialDelayMs * (1L << attempt);
                    log.warn("Web3 call failed (attempt {}/{}), retrying in {} ms: {}",
                            attempt + 1, maxRetries + 1, delay, e.getMessage());
                    Thread.sleep(delay);
                }
            } catch (IOException e) {
                lastException = e;
                if (attempt < maxRetries) {
                    long delay = initialDelayMs * (1L << attempt);
                    log.warn("Web3 call failed (attempt {}/{}), retrying in {} ms: {}",
                            attempt + 1, maxRetries + 1, delay, e.getMessage());
                    Thread.sleep(delay);
                }
            }
        }
        throw new IOException("Web3 call failed after " + (maxRetries + 1) + " attempts", lastException);
    }

    @FunctionalInterface
    private interface CallableWithException<T> {
        T call() throws IOException;
    }

    // ------------------------------------------------------------------------
    // Configuration
    // ------------------------------------------------------------------------

    /**
     * Settings for the blockchain client, typically loaded from app configuration.
     */
    public static class ClientSettings {
        private final String providerUrl;
        private final int connectionTimeoutSeconds;
        private final int readTimeoutSeconds;
        private final int maxRetries;
        private final long retryInitialDelayMs;
        private final long gasPriceCacheMillis;
        private final long chainIdCacheMillis;

        public ClientSettings(String providerUrl) {
            this(providerUrl, 10, 12, 3, 200, TimeUnit.SECONDS.toMillis(15), TimeUnit.MINUTES.toMillis(5));
        }

        public ClientSettings(String providerUrl, int connectionTimeoutSeconds, int readTimeoutSeconds,
                              int maxRetries, long retryInitialDelayMs,
                              long gasPriceCacheMillis, long chainIdCacheMillis) {
            this.providerUrl = providerUrl;
            this.connectionTimeoutSeconds = connectionTimeoutSeconds;
            this.readTimeoutSeconds = readTimeoutSeconds;
            this.maxRetries = maxRetries;
            this.retryInitialDelayMs = retryInitialDelayMs;
            this.gasPriceCacheMillis = gasPriceCacheMillis;
            this.chainIdCacheMillis = chainIdCacheMillis;
        }

        public String getProviderUrl() { return providerUrl; }
        public int getConnectionTimeoutSeconds() { return connectionTimeoutSeconds; }
        public int getReadTimeoutSeconds() { return readTimeoutSeconds; }
        public int getMaxRetries() { return maxRetries; }
        public long getRetryInitialDelayMs() { return retryInitialDelayMs; }
        public long getGasPriceCacheMillis() { return gasPriceCacheMillis; }
        public long getChainIdCacheMillis() { return chainIdCacheMillis; }
    }
}
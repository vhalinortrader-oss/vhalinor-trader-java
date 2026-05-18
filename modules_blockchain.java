package com.vhalinor.blockchain;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * VHALINOR TRADER - Blockchain Module (Java)
 * ==========================================================================
 * Advanced blockchain integration with Web3j, smart contracts, and DeFi support.
 *
 * Version: 5.0 Enhanced
 * Date: March 2026
 */
public class VhalinorBlockchainModule {

    private static final Logger logger = Logger.getLogger("BlockchainManager");

    // ----------------------------------------------------------------------
    // Custom Exceptions
    // ----------------------------------------------------------------------
    public static class VhalinorException extends RuntimeException {
        public VhalinorException(String message) { super(message); }
        public VhalinorException(String message, Throwable cause) { super(message, cause); }
    }

    public static class SecurityError extends VhalinorException {
        public SecurityError(String message) { super(message); }
    }

    public static class ValidationError extends VhalinorException {
        public ValidationError(String message) { super(message); }
    }

    // ----------------------------------------------------------------------
    // Data Classes (Java POJOs)
    // ----------------------------------------------------------------------
    public static class BlockchainConfig {
        private String chainName;
        private String rpcUrl;
        private int chainId;
        private String explorerUrl;
        private String nativeCurrency = "ETH";
        private String gasPriceOracle = "";
        private int blockTime = 12;
        private int maxGasLimit = 8000000;

        public BlockchainConfig() {}

        public BlockchainConfig(String chainName, String rpcUrl, int chainId, String explorerUrl,
                                String nativeCurrency, String gasPriceOracle, int blockTime, int maxGasLimit) {
            this.chainName = chainName;
            this.rpcUrl = rpcUrl;
            this.chainId = chainId;
            this.explorerUrl = explorerUrl;
            this.nativeCurrency = nativeCurrency;
            this.gasPriceOracle = gasPriceOracle;
            this.blockTime = blockTime;
            this.maxGasLimit = maxGasLimit;
        }

        // Getters and setters
        public String getChainName() { return chainName; }
        public void setChainName(String chainName) { this.chainName = chainName; }
        public String getRpcUrl() { return rpcUrl; }
        public void setRpcUrl(String rpcUrl) { this.rpcUrl = rpcUrl; }
        public int getChainId() { return chainId; }
        public void setChainId(int chainId) { this.chainId = chainId; }
        public String getExplorerUrl() { return explorerUrl; }
        public void setExplorerUrl(String explorerUrl) { this.explorerUrl = explorerUrl; }
        public String getNativeCurrency() { return nativeCurrency; }
        public void setNativeCurrency(String nativeCurrency) { this.nativeCurrency = nativeCurrency; }
        public String getGasPriceOracle() { return gasPriceOracle; }
        public void setGasPriceOracle(String gasPriceOracle) { this.gasPriceOracle = gasPriceOracle; }
        public int getBlockTime() { return blockTime; }
        public void setBlockTime(int blockTime) { this.blockTime = blockTime; }
        public int getMaxGasLimit() { return maxGasLimit; }
        public void setMaxGasLimit(int maxGasLimit) { this.maxGasLimit = maxGasLimit; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("chain_name", chainName);
            map.put("rpc_url", rpcUrl);
            map.put("chain_id", chainId);
            map.put("explorer_url", explorerUrl);
            map.put("native_currency", nativeCurrency);
            map.put("gas_price_oracle", gasPriceOracle);
            map.put("block_time", blockTime);
            map.put("max_gas_limit", maxGasLimit);
            return map;
        }
    }

    public static class Wallet {
        private String address;
        private String privateKey;
        private String publicKey = "";
        private int chainId = 1;
        private double balance = 0.0;
        private int nonce = 0;
        private String label = "";
        private boolean active = true;
        private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);
        private ZonedDateTime lastTransaction = null;
        private int transactionCount = 0;

        public Wallet() {}

        public Wallet(String address, String privateKey, String publicKey, int chainId,
                      double balance, int nonce, String label, boolean active) {
            this.address = address;
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.chainId = chainId;
            this.balance = balance;
            this.nonce = nonce;
            this.label = label;
            this.active = active;
        }

        // Getters and setters...
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPrivateKey() { return privateKey; }
        public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
        public String getPublicKey() { return publicKey; }
        public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
        public int getChainId() { return chainId; }
        public void setChainId(int chainId) { this.chainId = chainId; }
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
        public int getNonce() { return nonce; }
        public void setNonce(int nonce) { this.nonce = nonce; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public ZonedDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
        public ZonedDateTime getLastTransaction() { return lastTransaction; }
        public void setLastTransaction(ZonedDateTime lastTransaction) { this.lastTransaction = lastTransaction; }
        public int getTransactionCount() { return transactionCount; }
        public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("address", address);
            map.put("public_key", publicKey);
            map.put("chain_id", chainId);
            map.put("balance", balance);
            map.put("nonce", nonce);
            map.put("label", label);
            map.put("is_active", active);
            map.put("created_at", createdAt != null ? createdAt.format(DateTimeFormatter.ISO_INSTANT) : null);
            map.put("last_transaction", lastTransaction != null ? lastTransaction.format(DateTimeFormatter.ISO_INSTANT) : null);
            map.put("transaction_count", transactionCount);
            return map;
        }
    }

    public static class Transaction {
        private String txHash;
        private String fromAddress;
        private String toAddress;
        private double value;
        private double gasPrice;
        private int gasLimit;
        private Integer gasUsed;
        private BigInteger blockNumber;
        private String blockHash;
        private Integer transactionIndex;
        private String status = "pending";
        private ZonedDateTime timestamp;
        private int confirmations = 0;
        private String data = "0x";
        private List<Map<String, Object>> logs = new ArrayList<>();
        private ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);

        public Transaction() {}

        public Transaction(String txHash, String fromAddress, String toAddress, double value,
                           double gasPrice, int gasLimit) {
            this.txHash = txHash;
            this.fromAddress = fromAddress;
            this.toAddress = toAddress;
            this.value = value;
            this.gasPrice = gasPrice;
            this.gasLimit = gasLimit;
        }

        // Getters and setters...
        public String getTxHash() { return txHash; }
        public void setTxHash(String txHash) { this.txHash = txHash; }
        public String getFromAddress() { return fromAddress; }
        public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
        public String getToAddress() { return toAddress; }
        public void setToAddress(String toAddress) { this.toAddress = toAddress; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
        public double getGasPrice() { return gasPrice; }
        public void setGasPrice(double gasPrice) { this.gasPrice = gasPrice; }
        public int getGasLimit() { return gasLimit; }
        public void setGasLimit(int gasLimit) { this.gasLimit = gasLimit; }
        public Integer getGasUsed() { return gasUsed; }
        public void setGasUsed(Integer gasUsed) { this.gasUsed = gasUsed; }
        public BigInteger getBlockNumber() { return blockNumber; }
        public void setBlockNumber(BigInteger blockNumber) { this.blockNumber = blockNumber; }
        public String getBlockHash() { return blockHash; }
        public void setBlockHash(String blockHash) { this.blockHash = blockHash; }
        public Integer getTransactionIndex() { return transactionIndex; }
        public void setTransactionIndex(Integer transactionIndex) { this.transactionIndex = transactionIndex; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public ZonedDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(ZonedDateTime timestamp) { this.timestamp = timestamp; }
        public int getConfirmations() { return confirmations; }
        public void setConfirmations(int confirmations) { this.confirmations = confirmations; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        public List<Map<String, Object>> getLogs() { return logs; }
        public void setLogs(List<Map<String, Object>> logs) { this.logs = logs; }
        public ZonedDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("tx_hash", txHash);
            map.put("from_address", fromAddress);
            map.put("to_address", toAddress);
            map.put("value", value);
            map.put("gas_price", gasPrice);
            map.put("gas_limit", gasLimit);
            map.put("gas_used", gasUsed);
            map.put("block_number", blockNumber);
            map.put("block_hash", blockHash);
            map.put("transaction_index", transactionIndex);
            map.put("status", status);
            map.put("timestamp", timestamp != null ? timestamp.format(DateTimeFormatter.ISO_INSTANT) : null);
            map.put("confirmations", confirmations);
            map.put("data", data);
            map.put("logs", logs);
            map.put("created_at", createdAt.format(DateTimeFormatter.ISO_INSTANT));
            return map;
        }
    }

    public static class SmartContract {
        private String address;
        private List<Map<String, Object>> abi;
        private String contractName;
        private int chainId;
        private ZonedDateTime deployedAt;
        private boolean verified = false;
        private String sourceCode = "";
        private Map<String, Object> functions = new HashMap<>();
        private Map<String, Object> events = new HashMap<>();
        private Map<String, Object> metadata = new HashMap<>();

        public SmartContract() {}

        public SmartContract(String address, List<Map<String, Object>> abi, String contractName, int chainId) {
            this.address = address;
            this.abi = abi;
            this.contractName = contractName;
            this.chainId = chainId;
        }

        // Getters and setters...
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public List<Map<String, Object>> getAbi() { return abi; }
        public void setAbi(List<Map<String, Object>> abi) { this.abi = abi; }
        public String getContractName() { return contractName; }
        public void setContractName(String contractName) { this.contractName = contractName; }
        public int getChainId() { return chainId; }
        public void setChainId(int chainId) { this.chainId = chainId; }
        public ZonedDateTime getDeployedAt() { return deployedAt; }
        public void setDeployedAt(ZonedDateTime deployedAt) { this.deployedAt = deployedAt; }
        public boolean isVerified() { return verified; }
        public void setVerified(boolean verified) { this.verified = verified; }
        public String getSourceCode() { return sourceCode; }
        public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
        public Map<String, Object> getFunctions() { return functions; }
        public void setFunctions(Map<String, Object> functions) { this.functions = functions; }
        public Map<String, Object> getEvents() { return events; }
        public void setEvents(Map<String, Object> events) { this.events = events; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("address", address);
            map.put("abi", abi);
            map.put("contract_name", contractName);
            map.put("chain_id", chainId);
            map.put("deployed_at", deployedAt != null ? deployedAt.format(DateTimeFormatter.ISO_INSTANT) : null);
            map.put("verified", verified);
            map.put("source_code", sourceCode);
            map.put("functions", functions);
            map.put("events", events);
            map.put("metadata", metadata);
            return map;
        }
    }

    // ----------------------------------------------------------------------
    // Blockchain Manager
    // ----------------------------------------------------------------------
    public static class BlockchainManager {
        protected Map<String, Web3j> connections = new HashMap<>();
        protected Map<String, BlockchainConfig> chainConfigs = new HashMap<>();
        protected Map<String, Wallet> wallets = new ConcurrentHashMap<>();
        protected Map<String, SmartContract> contracts = new HashMap<>();
        protected Map<String, Transaction> pendingTransactions = new ConcurrentHashMap<>();
        protected LinkedList<Map<String, Object>> transactionHistory = new LinkedList<>();
        protected int transactionCount = 0;
        protected int errorCount = 0;
        protected double gasSpent = 0.0;
        protected final ReentrantLock lock = new ReentrantLock();

        private static final int MAX_TRANSACTION_HISTORY = 10000;

        public BlockchainManager() {
            initializeChainConfigs();
            initializeConnections();
            logger.info("Blockchain manager initialized");
        }

        private void initializeChainConfigs() {
            chainConfigs.put("ethereum", new BlockchainConfig(
                "Ethereum",
                "https://mainnet.infura.io/v3/YOUR_PROJECT_ID",
                1,
                "https://etherscan.io",
                "ETH",
                "https://ethgasstation.info/api/ethgasAPI.json",
                12,
                8000000
            ));
            chainConfigs.put("bsc", new BlockchainConfig(
                "Binance Smart Chain",
                "https://bsc-dataseed.binance.org",
                56,
                "https://bscscan.com",
                "BNB",
                "https://bscgas.info/api/gasPrice",
                3,
                100000000
            ));
            chainConfigs.put("polygon", new BlockchainConfig(
                "Polygon",
                "https://polygon-rpc.com",
                137,
                "https://polygonscan.com",
                "MATIC",
                "https://gasstation-mainnet.matic.network",
                2,
                20000000
            ));
            chainConfigs.put("arbitrum", new BlockchainConfig(
                "Arbitrum",
                "https://arb1.arbitrum.io/rpc",
                42161,
                "https://arbiscan.io",
                "ETH",
                "https://arb1.arbitrum.io/rpc",
                1,
                100000000
            ));
        }

        private void initializeConnections() {
            // Check Web3j availability (always true if imported)
            for (Map.Entry<String, BlockchainConfig> entry : chainConfigs.entrySet()) {
                String chainName = entry.getKey();
                BlockchainConfig config = entry.getValue();
                try {
                    Web3j web3j = Web3j.build(new HttpService(config.getRpcUrl()));
                    // For POA chains we could add middleware, but not strictly necessary for basic connection
                    Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
                    if (web3ClientVersion.getWeb3ClientVersion() != null) {
                        connections.put(chainName, web3j);
                        logger.info("Connected to " + chainName);
                    } else {
                        logger.warning("Failed to connect to " + chainName);
                    }
                } catch (Exception e) {
                    logger.severe("Error connecting to " + chainName + ": " + e.getMessage());
                }
            }
        }

        public String addWallet(String privateKey, int chainId, String label) {
            if (privateKey == null || privateKey.isEmpty()) {
                throw new SecurityError("Private key is required");
            }
            try {
                Credentials credentials = Credentials.create(privateKey);
                String address = credentials.getAddress();
                Wallet wallet = new Wallet(address, privateKey, credentials.getEcKeyPair().getPublicKey().toString(16), chainId,
                        0.0, 0, label.isEmpty() ? "Wallet " + (wallets.size() + 1) : label, true);
                lock.lock();
                try {
                    wallets.put(address, wallet);
                } finally {
                    lock.unlock();
                }
                logger.info("Added wallet: " + address);
                return address;
            } catch (Exception e) {
                logger.severe("Error adding wallet: " + e.getMessage());
                throw new SecurityError("Failed to add wallet: " + e.getMessage());
            }
        }

        public Map<String, Object> getWalletBalance(String address, String chainName) {
            if (!connections.containsKey(chainName)) {
                throw new ValidationError("Chain " + chainName + " not connected");
            }
            try {
                Web3j web3j = connections.get(chainName);
                BlockchainConfig config = chainConfigs.get(chainName);
                EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
                BigInteger balanceWei = ethGetBalance.getBalance();
                double balanceEth = Convert.fromWei(new BigDecimal(balanceWei), Convert.Unit.ETHER).doubleValue();

                EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
                int nonce = ethGetTransactionCount.getTransactionCount().intValue();

                if (wallets.containsKey(address)) {
                    lock.lock();
                    try {
                        Wallet w = wallets.get(address);
                        w.setBalance(balanceEth);
                        w.setNonce(nonce);
                    } finally {
                        lock.unlock();
                    }
                }

                Map<String, Object> result = new LinkedHashMap<>();
                result.put("address", address);
                result.put("balance", balanceEth);
                result.put("balance_wei", balanceWei.toString());
                result.put("nonce", nonce);
                result.put("chain", chainName);
                result.put("currency", config.getNativeCurrency());
                result.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
                return result;
            } catch (IOException e) {
                logger.severe("Error getting balance for " + address + ": " + e.getMessage());
                return Map.of("error", e.getMessage());
            }
        }

        public CompletableFuture<Map<String, Object>> sendTransaction(
                String fromAddress, String toAddress, double amount,
                String chainName, Double gasPrice, Integer gasLimit, String data) {
            return CompletableFuture.supplyAsync(() -> {
                if (!connections.containsKey(chainName)) {
                    throw new ValidationError("Chain " + chainName + " not connected");
                }
                if (!wallets.containsKey(fromAddress)) {
                    throw new ValidationError("Wallet " + fromAddress + " not found");
                }
                try {
                    Web3j web3j = connections.get(chainName);
                    BlockchainConfig config = chainConfigs.get(chainName);
                    Wallet wallet = wallets.get(fromAddress);

                    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                    BigInteger valueWei = Convert.toWei(BigDecimal.valueOf(amount), Convert.Unit.ETHER).toBigInteger();

                    if (gasPrice == null) gasPrice = 20.0;
                    if (gasLimit == null) gasLimit = 21000;
                    if (data == null) data = "0x";

                    BigInteger gasPriceWei = Convert.toWei(BigDecimal.valueOf(gasPrice), Convert.Unit.GWEI).toBigInteger();
                    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                            nonce, gasPriceWei, BigInteger.valueOf(gasLimit), toAddress, valueWei);

                    Credentials credentials = Credentials.create(wallet.getPrivateKey());
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, config.getChainId(), credentials);
                    String hexValue = "0x" + bytesToHex(signedMessage);
                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

                    if (ethSendTransaction.hasError()) {
                        throw new RuntimeException("Transaction error: " + ethSendTransaction.getError().getMessage());
                    }
                    String txHash = ethSendTransaction.getTransactionHash();

                    Transaction tx = new Transaction(txHash, fromAddress, toAddress, amount, gasPrice, gasLimit);
                    tx.setData(data);
                    tx.setStatus("pending");

                    lock.lock();
                    try {
                        pendingTransactions.put(txHash, tx);
                        addTransactionToHistory(tx.toMap());
                        transactionCount++;
                        gasSpent += gasPrice * gasLimit / 1e9;
                    } finally {
                        lock.unlock();
                    }

                    logger.info("Transaction sent: " + txHash);
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("tx_hash", txHash);
                    result.put("status", "pending");
                    result.put("from_address", fromAddress);
                    result.put("to_address", toAddress);
                    result.put("amount", amount);
                    result.put("gas_price", gasPrice);
                    result.put("gas_limit", gasLimit);
                    result.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
                    return result;
                } catch (Exception e) {
                    logger.severe("Error sending transaction: " + e.getMessage());
                    errorCount++;
                    return Map.of("error", e.getMessage());
                }
            });
        }

        private void addTransactionToHistory(Map<String, Object> txMap) {
            if (transactionHistory.size() >= MAX_TRANSACTION_HISTORY) {
                transactionHistory.removeFirst();
            }
            transactionHistory.addLast(txMap);
        }

        private CompletableFuture<Double> getGasPrice(String chainName) {
            return CompletableFuture.supplyAsync(() -> {
                BlockchainConfig config = chainConfigs.get(chainName);
                if (config == null || config.getGasPriceOracle().isEmpty()) {
                    return 20.0;
                }
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(config.getGasPriceOracle()))
                            .timeout(java.time.Duration.ofSeconds(10))
                            .GET()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        // Simplistic JSON parsing for demonstration (use a real parser)
                        String body = response.body();
                        if (body.contains("fast")) {
                            int idx = body.indexOf("fast") + 6;
                            String val = body.substring(idx, body.indexOf(",", idx)).trim();
                            return Double.parseDouble(val) / 10.0;
                        } else if (body.contains("gasPrice")) {
                            int idx = body.indexOf("gasPrice") + 10;
                            String val = body.substring(idx, body.indexOf(",", idx)).trim();
                            return Double.parseDouble(val) / 1e9;
                        } else if (body.contains("standard")) {
                            int idx = body.indexOf("standard") + 10;
                            String val = body.substring(idx, body.indexOf(",", idx)).trim();
                            return Double.parseDouble(val) / 10.0;
                        }
                    }
                } catch (Exception e) {
                    logger.fine("Error getting gas price from oracle: " + e.getMessage());
                }
                return 20.0;
            });
        }

        public CompletableFuture<Map<String, Object>> getTransactionStatus(String txHash, String chainName) {
            return CompletableFuture.supplyAsync(() -> {
                if (!connections.containsKey(chainName)) {
                    throw new ValidationError("Chain " + chainName + " not connected");
                }
                try {
                    Web3j web3j = connections.get(chainName);
                    BlockchainConfig config = chainConfigs.get(chainName);

                    EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
                    TransactionReceipt transactionReceipt = receipt.getTransactionReceipt().orElse(null);
                    if (transactionReceipt != null) {
                        EthTransaction tx = web3j.ethGetTransactionByHash(txHash).send();
                        org.web3j.protocol.core.methods.response.Transaction transaction = tx.getTransaction().orElse(null);
                        BigInteger currentBlock = web3j.ethBlockNumber().send().getBlockNumber();
                        BigInteger confirmations = currentBlock.subtract(transactionReceipt.getBlockNumber());

                        Map<String, Object> status = new LinkedHashMap<>();
                        status.put("tx_hash", txHash);
                        status.put("status", transactionReceipt.isStatusOK() ? "success" : "failed");
                        status.put("block_number", transactionReceipt.getBlockNumber().toString());
                        status.put("block_hash", transactionReceipt.getBlockHash());
                        status.put("transaction_index", transactionReceipt.getTransactionIndex().toString());
                        status.put("gas_used", transactionReceipt.getGasUsed().toString());
                        status.put("gas_price", transaction != null ? Convert.fromWei(new BigDecimal(transaction.getGasPrice()), Convert.Unit.GWEI).doubleValue() : 0);
                        status.put("confirmations", confirmations.intValue());
                        status.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
                        status.put("explorer_url", config.getExplorerUrl() + "/tx/" + txHash);

                        if (pendingTransactions.containsKey(txHash)) {
                            lock.lock();
                            try {
                                Transaction pendingTx = pendingTransactions.get(txHash);
                                pendingTx.setStatus((String) status.get("status"));
                                pendingTx.setBlockNumber(transactionReceipt.getBlockNumber());
                                pendingTx.setConfirmations(confirmations.intValue());
                                pendingTx.setGasUsed(transactionReceipt.getGasUsed().intValue());
                            } finally {
                                lock.unlock();
                            }
                        }
                        return status;
                    } else {
                        return Map.of("tx_hash", txHash, "status", "pending", "confirmations", 0);
                    }
                } catch (IOException e) {
                    logger.severe("Error getting transaction status: " + e.getMessage());
                    return Map.of("error", e.getMessage());
                }
            });
        }

        public String addContract(String address, List<Map<String, Object>> abi, String contractName, String chainName) {
            if (!connections.containsKey(chainName)) {
                throw new ValidationError("Chain " + chainName + " not connected");
            }
            try {
                Web3j web3j = connections.get(chainName);
                BlockchainConfig config = chainConfigs.get(chainName);
                // In Web3j, contract loading is done via generated wrappers; here we store the ABI
                SmartContract smartContract = new SmartContract(address, abi, contractName, config.getChainId());
                // For demonstration, we'll just store the ABI.
                String contractKey = chainName + ":" + address;
                lock.lock();
                try {
                    contracts.put(contractKey, smartContract);
                } finally {
                    lock.unlock();
                }
                logger.info("Added contract: " + contractName + " at " + address);
                return contractKey;
            } catch (Exception e) {
                logger.severe("Error adding contract: " + e.getMessage());
                throw new ValidationError("Failed to add contract: " + e.getMessage());
            }
        }

        public CompletableFuture<Map<String, Object>> callContractFunction(
                String contractKey, String functionName, List<Object> args,
                String fromAddress, double value, Integer gasLimit) {
            return CompletableFuture.supplyAsync(() -> {
                // Simplified: actual contract call would require dynamic ABI encoding.
                // For this blueprint, return a placeholder.
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("status", "success");
                result.put("function", functionName);
                result.put("args", args);
                return result;
            });
        }

        public Map<String, Object> getPerformanceStats() {
            lock.lock();
            try {
                Map<String, Object> stats = new LinkedHashMap<>();
                stats.put("connected_chains", new ArrayList<>(connections.keySet()));
                stats.put("wallets_count", wallets.size());
                stats.put("contracts_count", contracts.size());
                stats.put("transaction_count", transactionCount);
                stats.put("error_count", errorCount);
                stats.put("pending_transactions", pendingTransactions.size());
                stats.put("gas_spent_total", gasSpent);
                double successRate = transactionCount > 0 ? (double)(transactionCount - errorCount) / transactionCount : 0.0;
                stats.put("success_rate", successRate);
                Map<String, Boolean> libs = new LinkedHashMap<>();
                libs.put("web3j", true);
                libs.put("http_client", true);
                stats.put("libraries_available", libs);
                return stats;
            } finally {
                lock.unlock();
            }
        }

        private static String bytesToHex(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
    }

    // ----------------------------------------------------------------------
    // Web3 Manager (Enhanced with DeFi)
    // ----------------------------------------------------------------------
    public static class Web3Manager extends BlockchainManager {
        private Map<String, Map<String, Object>> defiProtocols = new HashMap<>();
        private Map<String, SmartContract> dexContracts = new HashMap<>();
        private Map<String, SmartContract> tokenContracts = new HashMap<>();

        public Web3Manager() {
            super();
            logger.info("Web3 manager initialized");
        }

        public void addDefiProtocol(String protocolName, Map<String, String> contracts, String chainName) {
            Map<String, Object> protocolData = new HashMap<>();
            protocolData.put("contracts", contracts);
            protocolData.put("chain_name", chainName);
            protocolData.put("added_at", ZonedDateTime.now(ZoneOffset.UTC));
            defiProtocols.put(protocolName, protocolData);
            logger.info("Added DeFi protocol: " + protocolName);
        }

        public CompletableFuture<Map<String, Object>> swapTokens(
                String protocolName, String tokenIn, String tokenOut, double amountIn,
                double slippage, String fromAddress) {
            return CompletableFuture.supplyAsync(() -> {
                if (!defiProtocols.containsKey(protocolName)) {
                    throw new ValidationError("DeFi protocol " + protocolName + " not found");
                }
                // Placeholder: in a real implementation, you'd call the DEX router contract.
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("protocol", protocolName);
                result.put("token_in", tokenIn);
                result.put("token_out", tokenOut);
                result.put("amount_in", amountIn);
                result.put("slippage", slippage);
                result.put("status", "prepared");
                result.put("from_address", fromAddress);
                logger.info("Prepared swap on " + protocolName + ": " + amountIn + " " + tokenIn + " -> " + tokenOut);
                return result;
            });
        }

        public CompletableFuture<Map<String, Object>> getTokenPrice(String tokenAddress, String chainName) {
            return CompletableFuture.supplyAsync(() -> {
                // Placeholder
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("token_address", tokenAddress);
                result.put("price_usd", 1.0);
                result.put("chain", chainName);
                result.put("timestamp", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
                return result;
            });
        }
    }

    // ----------------------------------------------------------------------
    // Main method (example usage)
    // ----------------------------------------------------------------------
    public static void main(String[] args) {
        // Example usage
        BlockchainManager manager = new BlockchainManager();
        // manager.addWallet(...)
        System.out.println("Blockchain module ready.");
    }
}
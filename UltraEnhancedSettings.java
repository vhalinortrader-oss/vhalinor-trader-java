import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Ultra Enhanced Configuration Settings for VHALINOR TRADER
 * Converted from Python Pydantic settings.
 *
 * Supports loading from environment variables (prefix: VHALINOR_),
 * validation, and export to Map/JSON.
 */
public class UltraEnhancedSettings {

    // ===================== Fields with defaults =====================
    private String systemName = "VHALINOR-TRADER";
    private String environment = "development";
    private boolean debugMode = false;
    private String logLevel = "INFO";
    private int maxWorkers = 10;

    private String aiFramework = "auto";
    private int modelUpdateInterval = 3600;
    private boolean quantumSimulation = true;
    private boolean enableGpu = false;
    private int batchSize = 64;
    private double learningRate = 0.001;
    private int epochs = 100;
    private int earlyStoppingPatience = 10;

    private String tradingMode = "simulation";
    private String riskLevel = "medium";
    private double maxPositionSize = 10000.0;
    private String defaultTimeframe = "1h";
    private List<String> supportedTimeframes = new ArrayList<>(Arrays.asList("1m", "5m", "15m", "1h", "4h", "1d"));
    private double maxDrawdownPercent = 20.0;
    private double stopLossPercent = 2.0;
    private double takeProfitPercent = 5.0;

    private String binanceApiKey = null;
    private String binanceSecretKey = null;
    private boolean binanceTestnet = true;
    private String alphaVantageKey = null;
    private String coinmarketcapKey = null;
    private String newsApiKey = null;

    private String databaseUrl = "sqlite:///vhalinor_ultra.db";
    private int databasePoolSize = 20;
    private int databaseMaxOverflow = 30;
    private String redisUrl = "redis://localhost:6379/0";
    private int redisPort = 6379;
    private int redisDb = 0;
    private String redisPassword = null;

    private int websocketPort = 8000;
    private String websocketHost = "0.0.0.0";
    private int maxWebsocketConnections = 100;
    private int realTimeDataRetention = 10000;
    private int dataUpdateInterval = 1;

    private String secretKey = "your-secret-key-change-in-production";
    private String encryptionKey = "your-encryption-key-change-in-production";
    private String jwtSecret = "your-jwt-secret-change-in-production";
    private int jwtExpiryHours = 24;
    private List<String> allowedOrigins = new ArrayList<>(Arrays.asList("*"));
    private int rateLimitPerMinute = 60;

    private int neuralNetworkSize = 100;
    private int neuralClusterSize = 10;
    private double synapseDensity = 0.1;
    private String learningAlgorithm = "hebbian";
    private String activationFunction = "relu";
    private int neuralUpdateInterval = 300;

    private String quantumBackend = "qasm_simulator";
    private int quantumQubits = 4;
    private int quantumCircuitDepth = 10;
    private int quantumShots = 1000;
    private boolean quantumNoiseModel = true;

    private boolean enableMonitoring = true;
    private int monitoringInterval = 30;
    private boolean enableMetricsExport = true;
    private String metricsExportFormat = "json";
    private boolean enableAlerts = true;
    private double alertThresholdCpu = 80.0;
    private double alertThresholdMemory = 85.0;

    private String basePath = ".";
    private String dataPath = "data";
    private String logsPath = "logs";
    private String modelsPath = "models";
    private String cachePath = "cache";
    private String tempPath = "temp";

    // ===================== Constructor =====================
    public UltraEnhancedSettings() {
        // defaults already set
    }

    // ===================== Getters (generated) =====================
    public String getSystemName() { return systemName; }
    public String getEnvironment() { return environment; }
    public boolean isDebugMode() { return debugMode; }
    public String getLogLevel() { return logLevel; }
    public int getMaxWorkers() { return maxWorkers; }
    public String getAiFramework() { return aiFramework; }
    public int getModelUpdateInterval() { return modelUpdateInterval; }
    public boolean isQuantumSimulation() { return quantumSimulation; }
    public boolean isEnableGpu() { return enableGpu; }
    public int getBatchSize() { return batchSize; }
    public double getLearningRate() { return learningRate; }
    public int getEpochs() { return epochs; }
    public int getEarlyStoppingPatience() { return earlyStoppingPatience; }
    public String getTradingMode() { return tradingMode; }
    public String getRiskLevel() { return riskLevel; }
    public double getMaxPositionSize() { return maxPositionSize; }
    public String getDefaultTimeframe() { return defaultTimeframe; }
    public List<String> getSupportedTimeframes() { return supportedTimeframes; }
    public double getMaxDrawdownPercent() { return maxDrawdownPercent; }
    public double getStopLossPercent() { return stopLossPercent; }
    public double getTakeProfitPercent() { return takeProfitPercent; }
    public String getBinanceApiKey() { return binanceApiKey; }
    public String getBinanceSecretKey() { return binanceSecretKey; }
    public boolean isBinanceTestnet() { return binanceTestnet; }
    public String getAlphaVantageKey() { return alphaVantageKey; }
    public String getCoinmarketcapKey() { return coinmarketcapKey; }
    public String getNewsApiKey() { return newsApiKey; }
    public String getDatabaseUrl() { return databaseUrl; }
    public int getDatabasePoolSize() { return databasePoolSize; }
    public int getDatabaseMaxOverflow() { return databaseMaxOverflow; }
    public String getRedisUrl() { return redisUrl; }
    public int getRedisPort() { return redisPort; }
    public int getRedisDb() { return redisDb; }
    public String getRedisPassword() { return redisPassword; }
    public int getWebsocketPort() { return websocketPort; }
    public String getWebsocketHost() { return websocketHost; }
    public int getMaxWebsocketConnections() { return maxWebsocketConnections; }
    public int getRealTimeDataRetention() { return realTimeDataRetention; }
    public int getDataUpdateInterval() { return dataUpdateInterval; }
    public String getSecretKey() { return secretKey; }
    public String getEncryptionKey() { return encryptionKey; }
    public String getJwtSecret() { return jwtSecret; }
    public int getJwtExpiryHours() { return jwtExpiryHours; }
    public List<String> getAllowedOrigins() { return allowedOrigins; }
    public int getRateLimitPerMinute() { return rateLimitPerMinute; }
    public int getNeuralNetworkSize() { return neuralNetworkSize; }
    public int getNeuralClusterSize() { return neuralClusterSize; }
    public double getSynapseDensity() { return synapseDensity; }
    public String getLearningAlgorithm() { return learningAlgorithm; }
    public String getActivationFunction() { return activationFunction; }
    public int getNeuralUpdateInterval() { return neuralUpdateInterval; }
    public String getQuantumBackend() { return quantumBackend; }
    public int getQuantumQubits() { return quantumQubits; }
    public int getQuantumCircuitDepth() { return quantumCircuitDepth; }
    public int getQuantumShots() { return quantumShots; }
    public boolean isQuantumNoiseModel() { return quantumNoiseModel; }
    public boolean isEnableMonitoring() { return enableMonitoring; }
    public int getMonitoringInterval() { return monitoringInterval; }
    public boolean isEnableMetricsExport() { return enableMetricsExport; }
    public String getMetricsExportFormat() { return metricsExportFormat; }
    public boolean isEnableAlerts() { return enableAlerts; }
    public double getAlertThresholdCpu() { return alertThresholdCpu; }
    public double getAlertThresholdMemory() { return alertThresholdMemory; }
    public String getBasePath() { return basePath; }
    public String getDataPath() { return dataPath; }
    public String getLogsPath() { return logsPath; }
    public String getModelsPath() { return modelsPath; }
    public String getCachePath() { return cachePath; }
    public String getTempPath() { return tempPath; }

    // ===================== Validation =====================
    public List<String> validateAll() {
        List<String> errors = new ArrayList<>();
        try { validateEnvironment(this.environment); } catch (IllegalArgumentException e) { errors.add(e.getMessage()); }
        try { validateLogLevel(this.logLevel); } catch (IllegalArgumentException e) { errors.add(e.getMessage()); }
        try { validateAiFramework(this.aiFramework); } catch (IllegalArgumentException e) { errors.add(e.getMessage()); }
        try { validateTradingMode(this.tradingMode); } catch (IllegalArgumentException e) { errors.add(e.getMessage()); }
        try { validateRiskLevel(this.riskLevel); } catch (IllegalArgumentException e) { errors.add(e.getMessage()); }
        try { validateLearningAlgorithm(this.learningAlgorithm); } catch (IllegalArgumentException e) { errors.add(e.getMessage()); }

        if (websocketPort < 1 || websocketPort > 65535) {
            errors.add("WebSocket port must be between 1 and 65535");
        }
        if (batchSize < 1) { errors.add("Batch size must be positive"); }
        if (learningRate <= 0) { errors.add("Learning rate must be positive"); }
        if (maxPositionSize <= 0) { errors.add("Max position size must be positive"); }
        return errors;
    }

    private void validateEnvironment(String env) {
        if (!Arrays.asList("development", "staging", "production").contains(env)) {
            throw new IllegalArgumentException("Environment must be one of: development, staging, production");
        }
    }
    private void validateLogLevel(String level) {
        String upper = level.toUpperCase();
        if (!Arrays.asList("DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL").contains(upper)) {
            throw new IllegalArgumentException("Log level must be one of: DEBUG, INFO, WARNING, ERROR, CRITICAL");
        }
    }
    private void validateAiFramework(String framework) {
        if (!Arrays.asList("auto", "pytorch", "tensorflow", "sklearn", "numpy").contains(framework)) {
            throw new IllegalArgumentException("AI framework must be one of: auto, pytorch, tensorflow, sklearn, numpy");
        }
    }
    private void validateTradingMode(String mode) {
        if (!Arrays.asList("simulation", "paper", "live").contains(mode)) {
            throw new IllegalArgumentException("Trading mode must be one of: simulation, paper, live");
        }
    }
    private void validateRiskLevel(String level) {
        if (!Arrays.asList("low", "medium", "high").contains(level)) {
            throw new IllegalArgumentException("Risk level must be one of: low, medium, high");
        }
    }
    private void validateLearningAlgorithm(String algo) {
        if (!Arrays.asList("hebbian", "backprop", "reinforcement", "evolutionary").contains(algo)) {
            throw new IllegalArgumentException("Learning algorithm must be one of: hebbian, backprop, reinforcement, evolutionary");
        }
    }

    // ===================== Load from environment =====================
    /**
     * Load settings from environment variables (prefix: VHALINOR_).
     * Example: VHALINOR_ENVIRONMENT=production
     */
    public static UltraEnhancedSettings loadSettings() {
        UltraEnhancedSettings settings = new UltraEnhancedSettings();
        // Override with environment variables
        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("VHALINOR_")) {
                String fieldName = key.substring("VHALINOR_".length()).toLowerCase();
                String value = entry.getValue();
                // Use reflection to set field – simplify with manual mapping for critical ones
                try {
                    switch (fieldName) {
                        case "system_name": settings.systemName = value; break;
                        case "environment": settings.environment = value; break;
                        case "debug_mode": settings.debugMode = Boolean.parseBoolean(value); break;
                        case "log_level": settings.logLevel = value; break;
                        case "max_workers": settings.maxWorkers = Integer.parseInt(value); break;
                        case "ai_framework": settings.aiFramework = value; break;
                        case "model_update_interval": settings.modelUpdateInterval = Integer.parseInt(value); break;
                        case "quantum_simulation": settings.quantumSimulation = Boolean.parseBoolean(value); break;
                        case "enable_gpu": settings.enableGpu = Boolean.parseBoolean(value); break;
                        case "batch_size": settings.batchSize = Integer.parseInt(value); break;
                        case "learning_rate": settings.learningRate = Double.parseDouble(value); break;
                        case "epochs": settings.epochs = Integer.parseInt(value); break;
                        case "early_stopping_patience": settings.earlyStoppingPatience = Integer.parseInt(value); break;
                        case "trading_mode": settings.tradingMode = value; break;
                        case "risk_level": settings.riskLevel = value; break;
                        case "max_position_size": settings.maxPositionSize = Double.parseDouble(value); break;
                        case "default_timeframe": settings.defaultTimeframe = value; break;
                        case "supported_timeframes": settings.supportedTimeframes = Arrays.asList(value.split(",")); break;
                        case "max_drawdown_percent": settings.maxDrawdownPercent = Double.parseDouble(value); break;
                        case "stop_loss_percent": settings.stopLossPercent = Double.parseDouble(value); break;
                        case "take_profit_percent": settings.takeProfitPercent = Double.parseDouble(value); break;
                        case "binance_api_key": settings.binanceApiKey = value; break;
                        case "binance_secret_key": settings.binanceSecretKey = value; break;
                        case "binance_testnet": settings.binanceTestnet = Boolean.parseBoolean(value); break;
                        case "alpha_vantage_key": settings.alphaVantageKey = value; break;
                        case "coinmarketcap_key": settings.coinmarketcapKey = value; break;
                        case "news_api_key": settings.newsApiKey = value; break;
                        case "database_url": settings.databaseUrl = value; break;
                        case "database_pool_size": settings.databasePoolSize = Integer.parseInt(value); break;
                        case "database_max_overflow": settings.databaseMaxOverflow = Integer.parseInt(value); break;
                        case "redis_url": settings.redisUrl = value; break;
                        case "redis_port": settings.redisPort = Integer.parseInt(value); break;
                        case "redis_db": settings.redisDb = Integer.parseInt(value); break;
                        case "redis_password": settings.redisPassword = value; break;
                        case "websocket_port": settings.websocketPort = Integer.parseInt(value); break;
                        case "websocket_host": settings.websocketHost = value; break;
                        case "max_websocket_connections": settings.maxWebsocketConnections = Integer.parseInt(value); break;
                        case "real_time_data_retention": settings.realTimeDataRetention = Integer.parseInt(value); break;
                        case "data_update_interval": settings.dataUpdateInterval = Integer.parseInt(value); break;
                        case "secret_key": settings.secretKey = value; break;
                        case "encryption_key": settings.encryptionKey = value; break;
                        case "jwt_secret": settings.jwtSecret = value; break;
                        case "jwt_expiry_hours": settings.jwtExpiryHours = Integer.parseInt(value); break;
                        case "allowed_origins": settings.allowedOrigins = Arrays.asList(value.split(",")); break;
                        case "rate_limit_per_minute": settings.rateLimitPerMinute = Integer.parseInt(value); break;
                        case "neural_network_size": settings.neuralNetworkSize = Integer.parseInt(value); break;
                        case "neural_cluster_size": settings.neuralClusterSize = Integer.parseInt(value); break;
                        case "synapse_density": settings.synapseDensity = Double.parseDouble(value); break;
                        case "learning_algorithm": settings.learningAlgorithm = value; break;
                        case "activation_function": settings.activationFunction = value; break;
                        case "neural_update_interval": settings.neuralUpdateInterval = Integer.parseInt(value); break;
                        case "quantum_backend": settings.quantumBackend = value; break;
                        case "quantum_qubits": settings.quantumQubits = Integer.parseInt(value); break;
                        case "quantum_circuit_depth": settings.quantumCircuitDepth = Integer.parseInt(value); break;
                        case "quantum_shots": settings.quantumShots = Integer.parseInt(value); break;
                        case "quantum_noise_model": settings.quantumNoiseModel = Boolean.parseBoolean(value); break;
                        case "enable_monitoring": settings.enableMonitoring = Boolean.parseBoolean(value); break;
                        case "monitoring_interval": settings.monitoringInterval = Integer.parseInt(value); break;
                        case "enable_metrics_export": settings.enableMetricsExport = Boolean.parseBoolean(value); break;
                        case "metrics_export_format": settings.metricsExportFormat = value; break;
                        case "enable_alerts": settings.enableAlerts = Boolean.parseBoolean(value); break;
                        case "alert_threshold_cpu": settings.alertThresholdCpu = Double.parseDouble(value); break;
                        case "alert_threshold_memory": settings.alertThresholdMemory = Double.parseDouble(value); break;
                        case "base_path": settings.basePath = value; break;
                        case "data_path": settings.dataPath = value; break;
                        case "logs_path": settings.logsPath = value; break;
                        case "models_path": settings.modelsPath = value; break;
                        case "cache_path": settings.cachePath = value; break;
                        case "temp_path": settings.tempPath = value; break;
                        default: // ignore unknown
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number for env " + key + ": " + value);
                }
            }
        }
        return settings;
    }

    // ===================== Export to Map =====================
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("system_name", systemName);
        map.put("environment", environment);
        map.put("debug_mode", debugMode);
        map.put("log_level", logLevel);
        map.put("max_workers", maxWorkers);
        map.put("ai_framework", aiFramework);
        map.put("model_update_interval", modelUpdateInterval);
        map.put("quantum_simulation", quantumSimulation);
        map.put("enable_gpu", enableGpu);
        map.put("batch_size", batchSize);
        map.put("learning_rate", learningRate);
        map.put("epochs", epochs);
        map.put("early_stopping_patience", earlyStoppingPatience);
        map.put("trading_mode", tradingMode);
        map.put("risk_level", riskLevel);
        map.put("max_position_size", maxPositionSize);
        map.put("default_timeframe", defaultTimeframe);
        map.put("supported_timeframes", supportedTimeframes);
        map.put("max_drawdown_percent", maxDrawdownPercent);
        map.put("stop_loss_percent", stopLossPercent);
        map.put("take_profit_percent", takeProfitPercent);
        // hide sensitive
        map.put("binance_api_key", binanceApiKey != null ? "***HIDDEN***" : null);
        map.put("binance_secret_key", binanceSecretKey != null ? "***HIDDEN***" : null);
        map.put("binance_testnet", binanceTestnet);
        map.put("alpha_vantage_key", alphaVantageKey != null ? "***HIDDEN***" : null);
        map.put("coinmarketcap_key", coinmarketcapKey != null ? "***HIDDEN***" : null);
        map.put("news_api_key", newsApiKey != null ? "***HIDDEN***" : null);
        map.put("database_url", databaseUrl);
        map.put("database_pool_size", databasePoolSize);
        map.put("database_max_overflow", databaseMaxOverflow);
        map.put("redis_url", redisUrl);
        map.put("redis_port", redisPort);
        map.put("redis_db", redisDb);
        map.put("redis_password", redisPassword != null ? "***HIDDEN***" : null);
        map.put("websocket_port", websocketPort);
        map.put("websocket_host", websocketHost);
        map.put("max_websocket_connections", maxWebsocketConnections);
        map.put("real_time_data_retention", realTimeDataRetention);
        map.put("data_update_interval", dataUpdateInterval);
        map.put("secret_key", secretKey != null ? "***HIDDEN***" : null);
        map.put("encryption_key", encryptionKey != null ? "***HIDDEN***" : null);
        map.put("jwt_secret", jwtSecret != null ? "***HIDDEN***" : null);
        map.put("jwt_expiry_hours", jwtExpiryHours);
        map.put("allowed_origins", allowedOrigins);
        map.put("rate_limit_per_minute", rateLimitPerMinute);
        map.put("neural_network_size", neuralNetworkSize);
        map.put("neural_cluster_size", neuralClusterSize);
        map.put("synapse_density", synapseDensity);
        map.put("learning_algorithm", learningAlgorithm);
        map.put("activation_function", activationFunction);
        map.put("neural_update_interval", neuralUpdateInterval);
        map.put("quantum_backend", quantumBackend);
        map.put("quantum_qubits", quantumQubits);
        map.put("quantum_circuit_depth", quantumCircuitDepth);
        map.put("quantum_shots", quantumShots);
        map.put("quantum_noise_model", quantumNoiseModel);
        map.put("enable_monitoring", enableMonitoring);
        map.put("monitoring_interval", monitoringInterval);
        map.put("enable_metrics_export", enableMetricsExport);
        map.put("metrics_export_format", metricsExportFormat);
        map.put("enable_alerts", enableAlerts);
        map.put("alert_threshold_cpu", alertThresholdCpu);
        map.put("alert_threshold_memory", alertThresholdMemory);
        map.put("base_path", basePath);
        map.put("data_path", dataPath);
        map.put("logs_path", logsPath);
        map.put("models_path", modelsPath);
        map.put("cache_path", cachePath);
        map.put("temp_path", tempPath);
        return map;
    }

    // ===================== Helper: create directories =====================
    public static void createDirectories(UltraEnhancedSettings settings) {
        List<String> dirs = Arrays.asList(
            settings.dataPath,
            settings.logsPath,
            settings.modelsPath,
            settings.cachePath,
            settings.tempPath
        );
        for (String dir : dirs) {
            Path path = Paths.get(dir);
            try {
                java.nio.file.Files.createDirectories(path);
                System.out.println("Created directory: " + path.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to create directory " + dir + ": " + e.getMessage());
            }
        }
    }

    // ===================== Helper: print settings =====================
    public static void printSettings(UltraEnhancedSettings settings) {
        System.out.println("Current Configuration:");
        System.out.println("=".repeat(50));
        Map<String, Object> map = settings.toMap();
        // grouping (similar to Python)
        String[][] groups = {
            {"System", "system_name", "environment", "debug_mode", "log_level"},
            {"AI/ML", "ai_framework", "model_update_interval", "enable_gpu", "batch_size"},
            {"Trading", "trading_mode", "risk_level", "max_position_size", "default_timeframe"},
            {"Database", "database_url", "redis_url"},
            {"Real-time", "websocket_port", "websocket_host"},
            {"Security", "secret_key", "encryption_key"},
            {"Neural Network", "neural_network_size", "learning_algorithm"},
            {"Quantum", "quantum_backend", "quantum_qubits"}
        };
        for (String[] group : groups) {
            String category = group[0];
            System.out.println("\n" + category + ":");
            for (int i = 1; i < group.length; i++) {
                String key = group[i];
                if (map.containsKey(key)) {
                    System.out.println("  " + key + ": " + map.get(key));
                }
            }
        }
        System.out.println("\n" + "=".repeat(50));
    }

    // ===================== Main for demonstration =====================
    public static void main(String[] args) {
        UltraEnhancedSettings settings = loadSettings();

        if (settings == null) {
            System.err.println("Failed to load settings.");
            System.exit(1);
        }

        System.out.println("Settings loaded successfully.\n");

        // Validate
        List<String> errors = settings.validateAll();
        if (!errors.isEmpty()) {
            System.out.println("Configuration validation errors:");
            for (String e : errors) System.out.println("  - " + e);
            System.out.println("Please fix these errors before proceeding.");
            System.exit(1);
        }

        // Create directories
        createDirectories(settings);

        // Print
        printSettings(settings);

        // Save to JSON (simple homemade)
        try {
            Path outDir = Paths.get("config");
            Files.createDirectories(outDir);
            Path file = outDir.resolve("current_settings.json");
            // crude JSON
            StringBuilder json = new StringBuilder("{\n");
            Map<String, Object> map = settings.toMap();
            boolean first = true;
            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (!first) json.append(",\n");
                json.append("  \"").append(e.getKey()).append("\": ");
                Object val = e.getValue();
                if (val == null) json.append("null");
                else if (val instanceof String) json.append("\"").append(((String)val).replace("\"", "\\\"")).append("\"");
                else if (val instanceof List) {
                    json.append("[");
                    List<?> list = (List<?>) val;
                    for (int i=0; i<list.size(); i++) {
                        if (i>0) json.append(", ");
                        json.append("\"").append(list.get(i).toString()).append("\"");
                    }
                    json.append("]");
                }
                else json.append(val.toString());
                first = false;
            }
            json.append("\n}");
            Files.writeString(file, json.toString());
            System.out.println("\nSettings saved to: " + file.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
}
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

/**
 * VHALINOR TRADER - Enhanced Settings Configuration (Java)
 * Converted from Python Pydantic settings with identical structure and defaults.
 *
 * Usage:
 *   Settings settings = Settings.getInstance();
 *   // Access sections: settings.getTrading(), settings.getApi(), etc.
 *   // Update: Settings.updateSettings(Map.of("environment", "production"));
 *   // Load from file: Settings.loadConfigFromFile("config.yml");
 */
public class Settings {

    private static final Logger LOGGER = Logger.getLogger(Settings.class.getName());
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.INDENT_OUTPUT, true);
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory())
            .registerModule(new JavaTimeModule());

    // ---- Singleton ----
    private static Settings instance;

    private Settings() {
        // Initialize with defaults, then override from environment
        loadFromEnvironment();
    }

    /** Get or create the global settings instance. */
    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    /** Update the global settings instance (replaces current). */
    public static Settings updateSettings(Settings newSettings) {
        synchronized (Settings.class) {
            instance = newSettings;
            return instance;
        }
    }

    /** Update specific fields on the global instance (simple string-based). */
    public static void updateSettings(Map<String, Object> values) {
        Settings current = getInstance();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            try {
                // Use reflection to set the top-level field if it exists
                java.lang.reflect.Field field = Settings.class.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(current, entry.getValue());
            } catch (NoSuchFieldException e) {
                LOGGER.warning("Field not found: " + entry.getKey());
            } catch (IllegalAccessException e) {
                LOGGER.severe("Cannot set field: " + entry.getKey());
            }
        }
    }

    // ===================== Environment Loading =====================
    private void loadFromEnvironment() {
        // Load each nested section from environment variables
        trading = TradingConfig.fromEnvironment();
        api = APIConfig.fromEnvironment();
        database = DatabaseConfig.fromEnvironment();
        ai = AIConfig.fromEnvironment();
        quantum = QuantumConfig.fromEnvironment();
        realTime = RealTimeConfig.fromEnvironment();
        logging = LoggingConfig.fromEnvironment();
        security = SecurityConfig.fromEnvironment();
        web = WebConfig.fromEnvironment();
        automation = AutomationConfig.fromEnvironment();
        features = FeatureFlags.fromEnvironment();
        riskManagement = RiskManagementConfig.fromEnvironment();

        // Top-level environment fields
        environment = getEnv("ENVIRONMENT", "development");
        debug = getEnvBool("DEBUG", false);
        testing = getEnvBool("TESTING", false);
    }

    // ===================== File I/O =====================
    /**
     * Load configuration from a YAML or JSON file, merging with current settings.
     * Throws IOException if file not found or format unsupported.
     */
    public static void loadConfigFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Configuration file not found: " + filePath);
        }
        String fileName = path.getFileName().toString().toLowerCase();
        Settings fileSettings;
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            fileSettings = YAML_MAPPER.readValue(path.toFile(), Settings.class);
        } else if (fileName.endsWith(".json")) {
            fileSettings = JSON_MAPPER.readValue(path.toFile(), Settings.class);
        } else {
            throw new IllegalArgumentException("Unsupported config file format: " + fileName);
        }
        // Update global instance with loaded values (replace)
        synchronized (Settings.class) {
            instance = fileSettings;
        }
    }

    /**
     * Save current settings to file (YAML or JSON based on extension).
     */
    public static void saveConfigToFile(String filePath, Settings settings) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        String fileName = path.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            YAML_MAPPER.writeValue(path.toFile(), settings);
        } else if (fileName.endsWith(".json")) {
            JSON_MAPPER.writeValue(path.toFile(), settings);
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + fileName);
        }
    }

    // ===================== Validation =====================
    /** Return a list of missing required API keys. */
    public static List<String> validateApiKeys(Settings settings) {
        List<String> missing = new ArrayList<>();
        if (isBlank(settings.getApi().getAlphaVantageApiKey())) missing.add("ALPHA_VANTAGE_API_KEY");
        if (isBlank(settings.getApi().getBinanceApiKey())) missing.add("BINANCE_API_KEY");
        if (isBlank(settings.getApi().getBinanceSecretKey())) missing.add("BINANCE_SECRET_KEY");
        return missing;
    }

    // ===================== Convenience Getters =====================
    public static String getDatabaseUrl(Settings settings, String dbType) {
        switch (dbType.toLowerCase()) {
            case "redis": return settings.getDatabase().getRedisUrl();
            case "postgres": return settings.getDatabase().getPostgresUrl() != null ?
                    settings.getDatabase().getPostgresUrl() : "postgresql://localhost:5432/vhalinor_trader";
            case "mongodb": return settings.getDatabase().getMongodbUrl() != null ?
                    settings.getDatabase().getMongodbUrl() : "mongodb://localhost:27017/vhalinor_trader";
            default: throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
    }

    public static Map<String, Object> getModelConfig(Settings settings) {
        Map<String, Object> map = new HashMap<>();
        AIConfig ai = settings.getAi();
        map.put("model_path", ai.getModelPath());
        map.put("training_data_path", ai.getTrainingDataPath());
        map.put("epochs", ai.getNeuralNetworkEpochs());
        map.put("batch_size", ai.getBatchSize());
        map.put("learning_rate", ai.getLearningRate());
        map.put("quantum_simulation", ai.isQuantumSimulation());
        return map;
    }

    public static Map<String, Object> getQuantumConfig(Settings settings) {
        QuantumConfig q = settings.getQuantum();
        Map<String, Object> map = new HashMap<>();
        map.put("backend", q.getQuantumBackend());
        map.put("shots", q.getQuantumShots());
        map.put("qubits", q.getQuantumQubits());
        map.put("optimizer", q.getQuantumOptimizer());
        return map;
    }

    public static Map<String, Object> getTradingConfig(Settings settings) {
        TradingConfig t = settings.getTrading();
        Map<String, Object> map = new HashMap<>();
        map.put("symbols", t.getDefaultSymbols());
        map.put("capital", t.getDefaultCapital());
        map.put("risk_per_trade", t.getDefaultRiskPerTrade());
        map.put("timeframe", t.getDefaultTimeframe());
        map.put("max_positions", t.getMaxPositions());
        map.put("stop_loss", t.getStopLossPercentage() / 100.0);
        map.put("take_profit", t.getTakeProfitPercentage() / 100.0);
        return map;
    }

    public static Map<String, Object> getWebConfig(Settings settings) {
        WebConfig w = settings.getWeb();
        Map<String, Object> map = new HashMap<>();
        map.put("dashboard_host", w.getDashboardHost());
        map.put("dashboard_port", w.getDashboardPort());
        map.put("dashboard_debug", w.isDashboardDebug());
        map.put("api_host", w.getApiHost());
        map.put("api_port", w.getApiPort());
        map.put("api_workers", w.getApiWorkers());
        return map;
    }

    public static Map<String, Object> getLoggingConfig(Settings settings) {
        LoggingConfig l = settings.getLogging();
        Map<String, Object> map = new HashMap<>();
        map.put("level", l.getLogLevel());
        map.put("file", l.getLogFile());
        map.put("rotation", l.getLogRotation());
        map.put("retention_days", l.getLogRetentionDays());
        map.put("structured", l.isStructuredLogging());
        return map;
    }

    public static boolean isProduction(Settings settings) {
        return "production".equalsIgnoreCase(settings.getEnvironment());
    }

    public static boolean isDevelopment(Settings settings) {
        return "development".equalsIgnoreCase(settings.getEnvironment());
    }

    public static boolean isTesting(Settings settings) {
        return settings.isTesting() || "testing".equalsIgnoreCase(settings.getEnvironment());
    }

    // ===================== Top-Level Fields =====================
    private String environment = "development";
    private boolean debug = false;
    private boolean testing = false;

    // Nested configuration sections
    private TradingConfig trading = new TradingConfig();
    private APIConfig api = new APIConfig();
    private DatabaseConfig database = new DatabaseConfig();
    private AIConfig ai = new AIConfig();
    private QuantumConfig quantum = new QuantumConfig();
    private RealTimeConfig realTime = new RealTimeConfig();
    private LoggingConfig logging = new LoggingConfig();
    private SecurityConfig security = new SecurityConfig();
    private WebConfig web = new WebConfig();
    private AutomationConfig automation = new AutomationConfig();
    private FeatureFlags features = new FeatureFlags();
    private RiskManagementConfig riskManagement = new RiskManagementConfig();

    // Getters and setters (required for JSON deserialization)
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public boolean isDebug() { return debug; }
    public void setDebug(boolean debug) { this.debug = debug; }
    public boolean isTesting() { return testing; }
    public void setTesting(boolean testing) { this.testing = testing; }

    public TradingConfig getTrading() { return trading; }
    public void setTrading(TradingConfig trading) { this.trading = trading; }
    public APIConfig getApi() { return api; }
    public void setApi(APIConfig api) { this.api = api; }
    public DatabaseConfig getDatabase() { return database; }
    public void setDatabase(DatabaseConfig database) { this.database = database; }
    public AIConfig getAi() { return ai; }
    public void setAi(AIConfig ai) { this.ai = ai; }
    public QuantumConfig getQuantum() { return quantum; }
    public void setQuantum(QuantumConfig quantum) { this.quantum = quantum; }
    public RealTimeConfig getRealTime() { return realTime; }
    public void setRealTime(RealTimeConfig realTime) { this.realTime = realTime; }
    public LoggingConfig getLogging() { return logging; }
    public void setLogging(LoggingConfig logging) { this.logging = logging; }
    public SecurityConfig getSecurity() { return security; }
    public void setSecurity(SecurityConfig security) { this.security = security; }
    public WebConfig getWeb() { return web; }
    public void setWeb(WebConfig web) { this.web = web; }
    public AutomationConfig getAutomation() { return automation; }
    public void setAutomation(AutomationConfig automation) { this.automation = automation; }
    public FeatureFlags getFeatures() { return features; }
    public void setFeatures(FeatureFlags features) { this.features = features; }
    public RiskManagementConfig getRiskManagement() { return riskManagement; }
    public void setRiskManagement(RiskManagementConfig riskManagement) { this.riskManagement = riskManagement; }

    // ===================== Nested Config Classes =====================

    // ---- Trading ----
    public static class TradingConfig {
        private List<String> defaultSymbols = new ArrayList<>(Arrays.asList("AAPL","GOOGL","MSFT","TSLA","BTC-USD","ETH-USD"));
        private double defaultCapital = 10000.0;
        private double defaultRiskPerTrade = 0.02;
        private String defaultTimeframe = "1h";
        private int maxPositions = 5;
        private double stopLossPercentage = 2.0;
        private double takeProfitPercentage = 4.0;

        public static TradingConfig fromEnvironment() {
            TradingConfig cfg = new TradingConfig();
            cfg.defaultSymbols = getEnvList("TRADING_DEFAULT_SYMBOLS", cfg.defaultSymbols);
            cfg.defaultCapital = getEnvDouble("TRADING_DEFAULT_CAPITAL", 10000.0);
            cfg.defaultRiskPerTrade = getEnvDouble("TRADING_DEFAULT_RISK_PER_TRADE", 0.02);
            cfg.defaultTimeframe = getEnv("TRADING_DEFAULT_TIMEFRAME", "1h");
            cfg.maxPositions = getEnvInt("TRADING_MAX_POSITIONS", 5);
            cfg.stopLossPercentage = getEnvDouble("TRADING_STOP_LOSS_PERCENTAGE", 2.0);
            cfg.takeProfitPercentage = getEnvDouble("TRADING_TAKE_PROFIT_PERCENTAGE", 4.0);
            return cfg;
        }
        // getters/setters...
        public List<String> getDefaultSymbols() { return defaultSymbols; }
        public void setDefaultSymbols(List<String> defaultSymbols) { this.defaultSymbols = defaultSymbols; }
        public double getDefaultCapital() { return defaultCapital; }
        public void setDefaultCapital(double defaultCapital) { this.defaultCapital = defaultCapital; }
        public double getDefaultRiskPerTrade() { return defaultRiskPerTrade; }
        public void setDefaultRiskPerTrade(double defaultRiskPerTrade) { this.defaultRiskPerTrade = defaultRiskPerTrade; }
        public String getDefaultTimeframe() { return defaultTimeframe; }
        public void setDefaultTimeframe(String defaultTimeframe) { this.defaultTimeframe = defaultTimeframe; }
        public int getMaxPositions() { return maxPositions; }
        public void setMaxPositions(int maxPositions) { this.maxPositions = maxPositions; }
        public double getStopLossPercentage() { return stopLossPercentage; }
        public void setStopLossPercentage(double stopLossPercentage) { this.stopLossPercentage = stopLossPercentage; }
        public double getTakeProfitPercentage() { return takeProfitPercentage; }
        public void setTakeProfitPercentage(double takeProfitPercentage) { this.takeProfitPercentage = takeProfitPercentage; }
    }

    // ---- API ----
    public static class APIConfig {
        private String alphaVantageApiKey = "";
        private String binanceApiKey = "";
        private String binanceSecretKey = "";
        private String coinbaseApiKey = "";
        private String coinbaseSecretKey = "";
        private String krakenApiKey = "";

        public static APIConfig fromEnvironment() {
            APIConfig cfg = new APIConfig();
            cfg.alphaVantageApiKey = getEnv("API_ALPHA_VANTAGE_API_KEY", "");
            cfg.binanceApiKey = getEnv("API_BINANCE_API_KEY", "");
            cfg.binanceSecretKey = getEnv("API_BINANCE_SECRET_KEY", "");
            cfg.coinbaseApiKey = getEnv("API_COINBASE_API_KEY", "");
            cfg.coinbaseSecretKey = getEnv("API_COINBASE_SECRET_KEY", "");
            cfg.krakenApiKey = getEnv("API_KRAKEN_API_KEY", "");
            return cfg;
        }
        // getters/setters...
        public String getAlphaVantageApiKey() { return alphaVantageApiKey; }
        public void setAlphaVantageApiKey(String alphaVantageApiKey) { this.alphaVantageApiKey = alphaVantageApiKey; }
        public String getBinanceApiKey() { return binanceApiKey; }
        public void setBinanceApiKey(String binanceApiKey) { this.binanceApiKey = binanceApiKey; }
        public String getBinanceSecretKey() { return binanceSecretKey; }
        public void setBinanceSecretKey(String binanceSecretKey) { this.binanceSecretKey = binanceSecretKey; }
        public String getCoinbaseApiKey() { return coinbaseApiKey; }
        public void setCoinbaseApiKey(String coinbaseApiKey) { this.coinbaseApiKey = coinbaseApiKey; }
        public String getCoinbaseSecretKey() { return coinbaseSecretKey; }
        public void setCoinbaseSecretKey(String coinbaseSecretKey) { this.coinbaseSecretKey = coinbaseSecretKey; }
        public String getKrakenApiKey() { return krakenApiKey; }
        public void setKrakenApiKey(String krakenApiKey) { this.krakenApiKey = krakenApiKey; }
    }

    // ---- Database ----
    public static class DatabaseConfig {
        private String redisUrl = "redis://localhost:6379/0";
        private String postgresUrl = null;
        private String mongodbUrl = null;

        public static DatabaseConfig fromEnvironment() {
            DatabaseConfig cfg = new DatabaseConfig();
            cfg.redisUrl = getEnv("DATABASE_REDIS_URL", "redis://localhost:6379/0");
            cfg.postgresUrl = getEnv("DATABASE_POSTGRES_URL", null);
            cfg.mongodbUrl = getEnv("DATABASE_MONGODB_URL", null);
            return cfg;
        }
        public String getRedisUrl() { return redisUrl; }
        public void setRedisUrl(String redisUrl) { this.redisUrl = redisUrl; }
        public String getPostgresUrl() { return postgresUrl; }
        public void setPostgresUrl(String postgresUrl) { this.postgresUrl = postgresUrl; }
        public String getMongodbUrl() { return mongodbUrl; }
        public void setMongodbUrl(String mongodbUrl) { this.mongodbUrl = mongodbUrl; }
    }

    // ---- AI ----
    public static class AIConfig {
        private String modelPath = "./models";
        private String trainingDataPath = "./data";
        private String backtestStartDate = "2020-01-01";
        private String backtestEndDate = "2023-12-31";
        private boolean quantumSimulation = true;
        private int neuralNetworkEpochs = 100;
        private int batchSize = 32;
        private double learningRate = 0.001;

        public static AIConfig fromEnvironment() {
            AIConfig cfg = new AIConfig();
            cfg.modelPath = getEnv("AI_MODEL_PATH", "./models");
            cfg.trainingDataPath = getEnv("AI_TRAINING_DATA_PATH", "./data");
            cfg.backtestStartDate = getEnv("AI_BACKTEST_START_DATE", "2020-01-01");
            cfg.backtestEndDate = getEnv("AI_BACKTEST_END_DATE", "2023-12-31");
            cfg.quantumSimulation = getEnvBool("AI_QUANTUM_SIMULATION", true);
            cfg.neuralNetworkEpochs = getEnvInt("AI_NEURAL_NETWORK_EPOCHS", 100);
            cfg.batchSize = getEnvInt("AI_BATCH_SIZE", 32);
            cfg.learningRate = getEnvDouble("AI_LEARNING_RATE", 0.001);
            return cfg;
        }
        public String getModelPath() { return modelPath; }
        public void setModelPath(String modelPath) { this.modelPath = modelPath; }
        public String getTrainingDataPath() { return trainingDataPath; }
        public void setTrainingDataPath(String trainingDataPath) { this.trainingDataPath = trainingDataPath; }
        public String getBacktestStartDate() { return backtestStartDate; }
        public void setBacktestStartDate(String backtestStartDate) { this.backtestStartDate = backtestStartDate; }
        public String getBacktestEndDate() { return backtestEndDate; }
        public void setBacktestEndDate(String backtestEndDate) { this.backtestEndDate = backtestEndDate; }
        public boolean isQuantumSimulation() { return quantumSimulation; }
        public void setQuantumSimulation(boolean quantumSimulation) { this.quantumSimulation = quantumSimulation; }
        public int getNeuralNetworkEpochs() { return neuralNetworkEpochs; }
        public void setNeuralNetworkEpochs(int neuralNetworkEpochs) { this.neuralNetworkEpochs = neuralNetworkEpochs; }
        public int getBatchSize() { return batchSize; }
        public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
        public double getLearningRate() { return learningRate; }
        public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
    }

    // ---- Quantum ----
    public static class QuantumConfig {
        private String quantumBackend = "qasm_simulator";
        private int quantumShots = 1024;
        private int quantumQubits = 8;
        private String quantumOptimizer = "COBYLA";

        public static QuantumConfig fromEnvironment() {
            QuantumConfig cfg = new QuantumConfig();
            cfg.quantumBackend = getEnv("QUANTUM_QUANTUM_BACKEND", "qasm_simulator");
            cfg.quantumShots = getEnvInt("QUANTUM_QUANTUM_SHOTS", 1024);
            cfg.quantumQubits = getEnvInt("QUANTUM_QUANTUM_QUBITS", 8);
            cfg.quantumOptimizer = getEnv("QUANTUM_QUANTUM_OPTIMIZER", "COBYLA");
            return cfg;
        }
        public String getQuantumBackend() { return quantumBackend; }
        public void setQuantumBackend(String quantumBackend) { this.quantumBackend = quantumBackend; }
        public int getQuantumShots() { return quantumShots; }
        public void setQuantumShots(int quantumShots) { this.quantumShots = quantumShots; }
        public int getQuantumQubits() { return quantumQubits; }
        public void setQuantumQubits(int quantumQubits) { this.quantumQubits = quantumQubits; }
        public String getQuantumOptimizer() { return quantumOptimizer; }
        public void setQuantumOptimizer(String quantumOptimizer) { this.quantumOptimizer = quantumOptimizer; }
    }

    // ---- RealTime ----
    public static class RealTimeConfig {
        private String websocketUrl = "ws://localhost:8765";
        private int realTimeUpdateInterval = 60;
        private int maxBufferSize = 10000;
        private double alertThreshold = 0.8;

        public static RealTimeConfig fromEnvironment() {
            RealTimeConfig cfg = new RealTimeConfig();
            cfg.websocketUrl = getEnv("REAL_TIME_WEBSOCKET_URL", "ws://localhost:8765");
            cfg.realTimeUpdateInterval = getEnvInt("REAL_TIME_REAL_TIME_UPDATE_INTERVAL", 60);
            cfg.maxBufferSize = getEnvInt("REAL_TIME_MAX_BUFFER_SIZE", 10000);
            cfg.alertThreshold = getEnvDouble("REAL_TIME_ALERT_THRESHOLD", 0.8);
            return cfg;
        }
        public String getWebsocketUrl() { return websocketUrl; }
        public void setWebsocketUrl(String websocketUrl) { this.websocketUrl = websocketUrl; }
        public int getRealTimeUpdateInterval() { return realTimeUpdateInterval; }
        public void setRealTimeUpdateInterval(int realTimeUpdateInterval) { this.realTimeUpdateInterval = realTimeUpdateInterval; }
        public int getMaxBufferSize() { return maxBufferSize; }
        public void setMaxBufferSize(int maxBufferSize) { this.maxBufferSize = maxBufferSize; }
        public double getAlertThreshold() { return alertThreshold; }
        public void setAlertThreshold(double alertThreshold) { this.alertThreshold = alertThreshold; }
    }

    // ---- Logging ----
    public static class LoggingConfig {
        private String logLevel = "INFO";
        private String logFile = "./logs/vhalinor.log";
        private String logRotation = "daily";
        private int logRetentionDays = 30;
        private boolean structuredLogging = true;

        public static LoggingConfig fromEnvironment() {
            LoggingConfig cfg = new LoggingConfig();
            cfg.logLevel = getEnv("LOG_LOG_LEVEL", "INFO");
            cfg.logFile = getEnv("LOG_LOG_FILE", "./logs/vhalinor.log");
            cfg.logRotation = getEnv("LOG_LOG_ROTATION", "daily");
            cfg.logRetentionDays = getEnvInt("LOG_LOG_RETENTION_DAYS", 30);
            cfg.structuredLogging = getEnvBool("LOG_STRUCTURED_LOGGING", true);
            return cfg;
        }
        public String getLogLevel() { return logLevel; }
        public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
        public String getLogFile() { return logFile; }
        public void setLogFile(String logFile) { this.logFile = logFile; }
        public String getLogRotation() { return logRotation; }
        public void setLogRotation(String logRotation) { this.logRotation = logRotation; }
        public int getLogRetentionDays() { return logRetentionDays; }
        public void setLogRetentionDays(int logRetentionDays) { this.logRetentionDays = logRetentionDays; }
        public boolean isStructuredLogging() { return structuredLogging; }
        public void setStructuredLogging(boolean structuredLogging) { this.structuredLogging = structuredLogging; }
    }

    // ---- Security ----
    public static class SecurityConfig {
        private String jwtSecretKey = "";
        private String encryptionKey = "";
        private int bcryptRounds = 12;

        public static SecurityConfig fromEnvironment() {
            SecurityConfig cfg = new SecurityConfig();
            cfg.jwtSecretKey = getEnv("SECURITY_JWT_SECRET_KEY", "");
            cfg.encryptionKey = getEnv("SECURITY_ENCRYPTION_KEY", "");
            cfg.bcryptRounds = getEnvInt("SECURITY_BCRYPT_ROUNDS", 12);
            return cfg;
        }
        public String getJwtSecretKey() { return jwtSecretKey; }
        public void setJwtSecretKey(String jwtSecretKey) { this.jwtSecretKey = jwtSecretKey; }
        public String getEncryptionKey() { return encryptionKey; }
        public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }
        public int getBcryptRounds() { return bcryptRounds; }
        public void setBcryptRounds(int bcryptRounds) { this.bcryptRounds = bcryptRounds; }
    }

    // ---- Web ----
    public static class WebConfig {
        private String dashboardHost = "0.0.0.0";
        private int dashboardPort = 8501;
        private boolean dashboardDebug = false;
        private String apiHost = "0.0.0.0";
        private int apiPort = 8000;
        private int apiWorkers = 4;

        public static WebConfig fromEnvironment() {
            WebConfig cfg = new WebConfig();
            cfg.dashboardHost = getEnv("WEB_DASHBOARD_HOST", "0.0.0.0");
            cfg.dashboardPort = getEnvInt("WEB_DASHBOARD_PORT", 8501);
            cfg.dashboardDebug = getEnvBool("WEB_DASHBOARD_DEBUG", false);
            cfg.apiHost = getEnv("WEB_API_HOST", "0.0.0.0");
            cfg.apiPort = getEnvInt("WEB_API_PORT", 8000);
            cfg.apiWorkers = getEnvInt("WEB_API_WORKERS", 4);
            return cfg;
        }
        public String getDashboardHost() { return dashboardHost; }
        public void setDashboardHost(String dashboardHost) { this.dashboardHost = dashboardHost; }
        public int getDashboardPort() { return dashboardPort; }
        public void setDashboardPort(int dashboardPort) { this.dashboardPort = dashboardPort; }
        public boolean isDashboardDebug() { return dashboardDebug; }
        public void setDashboardDebug(boolean dashboardDebug) { this.dashboardDebug = dashboardDebug; }
        public String getApiHost() { return apiHost; }
        public void setApiHost(String apiHost) { this.apiHost = apiHost; }
        public int getApiPort() { return apiPort; }
        public void setApiPort(int apiPort) { this.apiPort = apiPort; }
        public int getApiWorkers() { return apiWorkers; }
        public void setApiWorkers(int apiWorkers) { this.apiWorkers = apiWorkers; }
    }

    // ---- Automation ----
    public static class AutomationConfig {
        private String seleniumDriverPath = "./drivers/chromedriver.exe";
        private boolean seleniumHeadless = true;
        private int seleniumTimeout = 30;
        private boolean autoTradeEnabled = true;
        private int tradeExecutionDelay = 5;

        public static AutomationConfig fromEnvironment() {
            AutomationConfig cfg = new AutomationConfig();
            cfg.seleniumDriverPath = getEnv("AUTOMATION_SELENIUM_DRIVER_PATH", "./drivers/chromedriver.exe");
            cfg.seleniumHeadless = getEnvBool("AUTOMATION_SELENIUM_HEADLESS", true);
            cfg.seleniumTimeout = getEnvInt("AUTOMATION_SELENIUM_TIMEOUT", 30);
            cfg.autoTradeEnabled = getEnvBool("AUTOMATION_AUTO_TRADE_ENABLED", true);
            cfg.tradeExecutionDelay = getEnvInt("AUTOMATION_TRADE_EXECUTION_DELAY", 5);
            return cfg;
        }
        public String getSeleniumDriverPath() { return seleniumDriverPath; }
        public void setSeleniumDriverPath(String seleniumDriverPath) { this.seleniumDriverPath = seleniumDriverPath; }
        public boolean isSeleniumHeadless() { return seleniumHeadless; }
        public void setSeleniumHeadless(boolean seleniumHeadless) { this.seleniumHeadless = seleniumHeadless; }
        public int getSeleniumTimeout() { return seleniumTimeout; }
        public void setSeleniumTimeout(int seleniumTimeout) { this.seleniumTimeout = seleniumTimeout; }
        public boolean isAutoTradeEnabled() { return autoTradeEnabled; }
        public void setAutoTradeEnabled(boolean autoTradeEnabled) { this.autoTradeEnabled = autoTradeEnabled; }
        public int getTradeExecutionDelay() { return tradeExecutionDelay; }
        public void setTradeExecutionDelay(int tradeExecutionDelay) { this.tradeExecutionDelay = tradeExecutionDelay; }
    }

    // ---- FeatureFlags ----
    public static class FeatureFlags {
        private boolean enableQuantumComputing = true;
        private boolean enableNeuralNetworks = true;
        private boolean enableReinforcementLearning = true;
        private boolean enableMetaLearning = true;
        private boolean enableCognitiveAnalysis = true;
        private boolean enableRealTimeMonitoring = true;
        private boolean enableAdvancedPredictions = true;
        private boolean enableBlockchainIntegration = true;
        private boolean enableSentimentAnalysis = true;
        private boolean enableTechnicalAnalysis = true;

        public static FeatureFlags fromEnvironment() {
            FeatureFlags cfg = new FeatureFlags();
            cfg.enableQuantumComputing = getEnvBool("ENABLE_QUANTUM_COMPUTING", true);
            cfg.enableNeuralNetworks = getEnvBool("ENABLE_NEURAL_NETWORKS", true);
            cfg.enableReinforcementLearning = getEnvBool("ENABLE_REINFORCEMENT_LEARNING", true);
            cfg.enableMetaLearning = getEnvBool("ENABLE_META_LEARNING", true);
            cfg.enableCognitiveAnalysis = getEnvBool("ENABLE_COGNITIVE_ANALYSIS", true);
            cfg.enableRealTimeMonitoring = getEnvBool("ENABLE_REAL_TIME_MONITORING", true);
            cfg.enableAdvancedPredictions = getEnvBool("ENABLE_ADVANCED_PREDICTIONS", true);
            cfg.enableBlockchainIntegration = getEnvBool("ENABLE_BLOCKCHAIN_INTEGRATION", true);
            cfg.enableSentimentAnalysis = getEnvBool("ENABLE_SENTIMENT_ANALYSIS", true);
            cfg.enableTechnicalAnalysis = getEnvBool("ENABLE_TECHNICAL_ANALYSIS", true);
            return cfg;
        }
        // boolean getters and setters...
        public boolean isEnableQuantumComputing() { return enableQuantumComputing; }
        public void setEnableQuantumComputing(boolean enableQuantumComputing) { this.enableQuantumComputing = enableQuantumComputing; }
        public boolean isEnableNeuralNetworks() { return enableNeuralNetworks; }
        public void setEnableNeuralNetworks(boolean enableNeuralNetworks) { this.enableNeuralNetworks = enableNeuralNetworks; }
        public boolean isEnableReinforcementLearning() { return enableReinforcementLearning; }
        public void setEnableReinforcementLearning(boolean enableReinforcementLearning) { this.enableReinforcementLearning = enableReinforcementLearning; }
        public boolean isEnableMetaLearning() { return enableMetaLearning; }
        public void setEnableMetaLearning(boolean enableMetaLearning) { this.enableMetaLearning = enableMetaLearning; }
        public boolean isEnableCognitiveAnalysis() { return enableCognitiveAnalysis; }
        public void setEnableCognitiveAnalysis(boolean enableCognitiveAnalysis) { this.enableCognitiveAnalysis = enableCognitiveAnalysis; }
        public boolean isEnableRealTimeMonitoring() { return enableRealTimeMonitoring; }
        public void setEnableRealTimeMonitoring(boolean enableRealTimeMonitoring) { this.enableRealTimeMonitoring = enableRealTimeMonitoring; }
        public boolean isEnableAdvancedPredictions() { return enableAdvancedPredictions; }
        public void setEnableAdvancedPredictions(boolean enableAdvancedPredictions) { this.enableAdvancedPredictions = enableAdvancedPredictions; }
        public boolean isEnableBlockchainIntegration() { return enableBlockchainIntegration; }
        public void setEnableBlockchainIntegration(boolean enableBlockchainIntegration) { this.enableBlockchainIntegration = enableBlockchainIntegration; }
        public boolean isEnableSentimentAnalysis() { return enableSentimentAnalysis; }
        public void setEnableSentimentAnalysis(boolean enableSentimentAnalysis) { this.enableSentimentAnalysis = enableSentimentAnalysis; }
        public boolean isEnableTechnicalAnalysis() { return enableTechnicalAnalysis; }
        public void setEnableTechnicalAnalysis(boolean enableTechnicalAnalysis) { this.enableTechnicalAnalysis = enableTechnicalAnalysis; }
    }

    // ---- RiskManagement ----
    public static class RiskManagementConfig {
        private double maxDailyLossPercentage = 5.0;
        private double maxWeeklyLossPercentage = 10.0;
        private double maxMonthlyLossPercentage = 20.0;
        private double minWinRate = 0.6;
        private int maxConsecutiveLosses = 5;
        private double drawdownLimit = 15.0;

        public static RiskManagementConfig fromEnvironment() {
            RiskManagementConfig cfg = new RiskManagementConfig();
            cfg.maxDailyLossPercentage = getEnvDouble("RISK_MAX_DAILY_LOSS_PERCENTAGE", 5.0);
            cfg.maxWeeklyLossPercentage = getEnvDouble("RISK_MAX_WEEKLY_LOSS_PERCENTAGE", 10.0);
            cfg.maxMonthlyLossPercentage = getEnvDouble("RISK_MAX_MONTHLY_LOSS_PERCENTAGE", 20.0);
            cfg.minWinRate = getEnvDouble("RISK_MIN_WIN_RATE", 0.6);
            cfg.maxConsecutiveLosses = getEnvInt("RISK_MAX_CONSECUTIVE_LOSSES", 5);
            cfg.drawdownLimit = getEnvDouble("RISK_DRAWDOWN_LIMIT", 15.0);
            return cfg;
        }
        public double getMaxDailyLossPercentage() { return maxDailyLossPercentage; }
        public void setMaxDailyLossPercentage(double maxDailyLossPercentage) { this.maxDailyLossPercentage = maxDailyLossPercentage; }
        public double getMaxWeeklyLossPercentage() { return maxWeeklyLossPercentage; }
        public void setMaxWeeklyLossPercentage(double maxWeeklyLossPercentage) { this.maxWeeklyLossPercentage = maxWeeklyLossPercentage; }
        public double getMaxMonthlyLossPercentage() { return maxMonthlyLossPercentage; }
        public void setMaxMonthlyLossPercentage(double maxMonthlyLossPercentage) { this.maxMonthlyLossPercentage = maxMonthlyLossPercentage; }
        public double getMinWinRate() { return minWinRate; }
        public void setMinWinRate(double minWinRate) { this.minWinRate = minWinRate; }
        public int getMaxConsecutiveLosses() { return maxConsecutiveLosses; }
        public void setMaxConsecutiveLosses(int maxConsecutiveLosses) { this.maxConsecutiveLosses = maxConsecutiveLosses; }
        public double getDrawdownLimit() { return drawdownLimit; }
        public void setDrawdownLimit(double drawdownLimit) { this.drawdownLimit = drawdownLimit; }
    }

    // ===================== Utility Methods =====================
    private static String getEnv(String key, String defaultValue) {
        String val = System.getenv(key);
        return (val != null && !val.isEmpty()) ? val : defaultValue;
    }
    private static int getEnvInt(String key, int defaultValue) {
        String val = System.getenv(key);
        if (val != null && !val.isEmpty()) {
            try { return Integer.parseInt(val); } catch (NumberFormatException e) { LOGGER.warning("Invalid int for " + key); }
        }
        return defaultValue;
    }
    private static double getEnvDouble(String key, double defaultValue) {
        String val = System.getenv(key);
        if (val != null && !val.isEmpty()) {
            try { return Double.parseDouble(val); } catch (NumberFormatException e) { LOGGER.warning("Invalid double for " + key); }
        }
        return defaultValue;
    }
    private static boolean getEnvBool(String key, boolean defaultValue) {
        String val = System.getenv(key);
        if (val != null && !val.isEmpty()) {
            return "true".equalsIgnoreCase(val) || "1".equals(val);
        }
        return defaultValue;
    }
    private static List<String> getEnvList(String key, List<String> defaultValue) {
        String val = System.getenv(key);
        if (val != null && !val.isEmpty()) {
            return Arrays.asList(val.split(","));
        }
        return defaultValue;
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
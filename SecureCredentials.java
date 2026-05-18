package com.lextrader.neural_layers.config;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Gerenciador seguro de credenciais para VHALINOR-TRADER IAG (Java)
 * <p>
 * Conversão fiel do módulo Python original.
 * Dependências externas sugeridas:
 * - io.github.cdimascio:dotenv-java (para carregar .env, opcional)
 * - Caso contrário, use variáveis de ambiente do sistema.
 */
public class SecureCredentials {

    private static final Logger LOGGER = Logger.getLogger(SecureCredentials.class.getName());

    // ========================================
    // Classes de configuração (dataclasses)
    // ========================================

    public abstract static class ExchangeConfig {
        protected String platform;
        protected boolean enabled = true;
        protected String environment = "demo";  // demo ou live

        public ExchangeConfig(String platform) {
            this.platform = platform;
        }

        public boolean validate() throws IllegalArgumentException {
            if (!"demo".equals(environment) && !"live".equals(environment)) {
                throw new IllegalArgumentException("Environment inválido: " + environment);
            }
            return true;
        }

        // Getters / Setters
        public String getPlatform() { return platform; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
    }

    public static class BinanceConfig extends ExchangeConfig {
        private String apiKey = "YOUR_BINANCE_API_KEY";
        private String apiSecret = "YOUR_BINANCE_API_SECRET";
        private boolean testnet = true;

        public BinanceConfig() {
            super("binance");
        }

        public boolean validate() throws IllegalArgumentException {
            super.validate();
            if ("YOUR_BINANCE_API_KEY".equals(apiKey)) {
                throw new IllegalArgumentException(
                        "❌ Binance API Key não configurado.\n   Configure BINANCE_API_KEY em .env");
            }
            if ("YOUR_BINANCE_API_SECRET".equals(apiSecret)) {
                throw new IllegalArgumentException(
                        "❌ Binance API Secret não configurado.\n   Configure BINANCE_API_SECRET em .env");
            }
            if (apiKey.length() < 30) {
                throw new IllegalArgumentException("❌ Binance API Key parece inválido (muito curto)");
            }
            return true;
        }

        // Getters e Setters
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getApiSecret() { return apiSecret; }
        public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
        public boolean isTestnet() { return testnet; }
        public void setTestnet(boolean testnet) { this.testnet = testnet; }
    }

    public static class CTraderConfig extends ExchangeConfig {
        private String clientId = "YOUR_CTRADER_CLIENT_ID";
        private String clientSecret = "YOUR_CTRADER_CLIENT_SECRET";
        private String accountId = "YOUR_ACCOUNT_ID";
        private String username = "";
        private String password = "";

        public CTraderConfig() {
            super("ctrader");
            this.environment = "demo";
        }

        public boolean validate() throws IllegalArgumentException {
            super.validate();
            if ("YOUR_CTRADER_CLIENT_ID".equals(clientId)) {
                throw new IllegalArgumentException(
                        "❌ cTrader Client ID não configurado.\n   Configure CTRADER_CLIENT_ID em .env");
            }
            if ("YOUR_CTRADER_CLIENT_SECRET".equals(clientSecret)) {
                throw new IllegalArgumentException(
                        "❌ cTrader Client Secret não configurado.\n   Configure CTRADER_CLIENT_SECRET em .env");
            }
            return true;
        }

        // Getters e Setters
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class PionexConfig extends ExchangeConfig {
        private String apiKey = "YOUR_PIONEX_API_KEY";
        private String apiSecret = "YOUR_PIONEX_API_SECRET";
        private String apiType = "spot";   // spot ou futures

        public PionexConfig() {
            super("pionex");
        }

        public boolean validate() throws IllegalArgumentException {
            super.validate();
            if ("YOUR_PIONEX_API_KEY".equals(apiKey)) {
                throw new IllegalArgumentException(
                        "❌ Pionex API Key não configurado.\n   Configure PIONEX_API_KEY em .env");
            }
            if ("YOUR_PIONEX_API_SECRET".equals(apiSecret)) {
                throw new IllegalArgumentException(
                        "❌ Pionex API Secret não configurado.\n   Configure PIONEX_API_SECRET em .env");
            }
            return true;
        }

        // Getters e Setters
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getApiSecret() { return apiSecret; }
        public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
        public String getApiType() { return apiType; }
        public void setApiType(String apiType) { this.apiType = apiType; }
    }

    public static class CoinbaseConfig extends ExchangeConfig {
        private String apiKey = "YOUR_COINBASE_API_KEY";
        private String apiSecret = "YOUR_COINBASE_API_SECRET";
        private String passphrase = "YOUR_PASSPHRASE";
        private boolean sandbox = true;

        public CoinbaseConfig() {
            super("coinbase");
        }

        public boolean validate() throws IllegalArgumentException {
            super.validate();
            if ("YOUR_COINBASE_API_KEY".equals(apiKey)) {
                LOGGER.warning("⚠️  Coinbase API Key não configurado (opcional)");
            }
            return true;
        }

        // Getters e Setters
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getApiSecret() { return apiSecret; }
        public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
        public String getPassphrase() { return passphrase; }
        public void setPassphrase(String passphrase) { this.passphrase = passphrase; }
        public boolean isSandbox() { return sandbox; }
        public void setSandbox(boolean sandbox) { this.sandbox = sandbox; }
    }

    public static class DataProviderConfig {
        private String alphavantage = "YOUR_ALPHAVANTAGE_KEY";
        private String polygon = "YOUR_POLYGON_KEY";
        private String twelvedata = "YOUR_TWELVEDATA_KEY";
        private String newsapi = "YOUR_NEWSAPI_KEY";

        public boolean validate() {
            List<String> missing = new ArrayList<>();
            if ("YOUR_ALPHAVANTAGE_KEY".equals(alphavantage)) missing.add("ALPHAVANTAGE_KEY");
            // outras chaves podem ser opcionais
            if (!missing.isEmpty()) {
                LOGGER.warning("⚠️  Provedores de dados não configurados: " + String.join(", ", missing));
            }
            return true;
        }

        // Getters e Setters
        public String getAlphavantage() { return alphavantage; }
        public void setAlphavantage(String alphavantage) { this.alphavantage = alphavantage; }
        public String getPolygon() { return polygon; }
        public void setPolygon(String polygon) { this.polygon = polygon; }
        public String getTwelvedata() { return twelvedata; }
        public void setTwelvedata(String twelvedata) { this.twelvedata = twelvedata; }
        public String getNewsapi() { return newsapi; }
        public void setNewsapi(String newsapi) { this.newsapi = newsapi; }
    }

    // ========================================
    // Gerenciador de criptografia (simplificado AES-GCM, estilo Fernet)
    // ========================================
    public static class EncryptedCredentialsManager {
        private final SecretKeySpec keySpec;
        private final SecureRandom secureRandom = new SecureRandom();
        private static final int GCM_IV_LENGTH = 12;   // 96 bits
        private static final int GCM_TAG_LENGTH = 128; // bits

        public EncryptedCredentialsManager(String encryptionKey) {
            if (encryptionKey == null || encryptionKey.trim().isEmpty()) {
                LOGGER.warning("⚠️  ENCRYPTION_KEY não configurado - criptografia desabilitada");
                this.keySpec = null;
                return;
            }
            try {
                // Deriva uma chave AES-128 a partir da string (compatível com Fernet usa 128 bits)
                byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(
                        encryptionKey.trim().getBytes(StandardCharsets.UTF_8));
                System.arraycopy(keyBytes, 0, keyBytes, 0, 16);  // usa primeiros 16 bytes (128 bits)
                this.keySpec = new SecretKeySpec(keyBytes, 0, 16, "AES");
                LOGGER.info("✅ Gerenciador de criptografia inicializado com sucesso");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "❌ Erro ao inicializar criptografia: " + e.getMessage(), e);
                throw new IllegalArgumentException("Chave de criptografia inválida: " + e.getMessage());
            }
        }

        public String encrypt(String value) {
            if (keySpec == null) return value;
            try {
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                byte[] iv = new byte[GCM_IV_LENGTH];
                secureRandom.nextBytes(iv);
                GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
                byte[] cipherText = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
                // concatena IV + cipherText
                byte[] combined = new byte[iv.length + cipherText.length];
                System.arraycopy(iv, 0, combined, 0, iv.length);
                System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
                return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao criptografar: " + e.getMessage(), e);
                return value;
            }
        }

        public String decrypt(String encryptedValue) {
            if (keySpec == null) return encryptedValue;
            try {
                byte[] combined = Base64.getUrlDecoder().decode(encryptedValue);
                if (combined.length < GCM_IV_LENGTH + 16) { // precisa ao menos do IV e do tag mínimo
                    LOGGER.severe("❌ Token de criptografia muito curto");
                    return encryptedValue;
                }
                byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
                byte[] cipherText = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
                cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
                byte[] plainBytes = cipher.doFinal(cipherText);
                return new String(plainBytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao descriptografar (possível token inválido): " + e.getMessage(), e);
                return encryptedValue;
            }
        }
    }

    // ========================================
    // Configuração centralizada de credenciais
    // ========================================
    public static class CredentialsConfig {
        public final BinanceConfig binance;
        public final CTraderConfig ctrader;
        public final PionexConfig pionex;
        public final CoinbaseConfig coinbase;
        public final DataProviderConfig dataProviders;
        public final EncryptedCredentialsManager crypto;

        // Configurações de segurança
        public final boolean logCredentials;
        public final int sessionTimeout;
        public final boolean enable2fa;

        public CredentialsConfig() {
            // Carregar chave de criptografia
            String encryptionKey = System.getenv("ENCRYPTION_KEY");
            this.crypto = new EncryptedCredentialsManager(encryptionKey);

            // Binance
            this.binance = new BinanceConfig();
            binance.setApiKey(System.getenv().getOrDefault("BINANCE_API_KEY", "YOUR_BINANCE_API_KEY"));
            binance.setApiSecret(System.getenv().getOrDefault("BINANCE_API_SECRET", "YOUR_BINANCE_API_SECRET"));
            binance.setTestnet("true".equalsIgnoreCase(System.getenv().getOrDefault("BINANCE_TESTNET", "true")));
            binance.setEnvironment(binance.isTestnet() ? "testnet" : "live");

            // cTrader
            this.ctrader = new CTraderConfig();
            ctrader.setClientId(System.getenv().getOrDefault("CTRADER_CLIENT_ID", "YOUR_CTRADER_CLIENT_ID"));
            ctrader.setClientSecret(System.getenv().getOrDefault("CTRADER_CLIENT_SECRET", "YOUR_CTRADER_CLIENT_SECRET"));
            ctrader.setAccountId(System.getenv().getOrDefault("CTRADER_ACCOUNT_ID", "YOUR_ACCOUNT_ID"));
            ctrader.setUsername(System.getenv().getOrDefault("CTRADER_USERNAME", ""));
            ctrader.setPassword(System.getenv().getOrDefault("CTRADER_PASSWORD", ""));
            ctrader.setEnvironment(System.getenv().getOrDefault("CTRADER_ENVIRONMENT", "demo"));

            // Pionex
            this.pionex = new PionexConfig();
            pionex.setApiKey(System.getenv().getOrDefault("PIONEX_API_KEY", "YOUR_PIONEX_API_KEY"));
            pionex.setApiSecret(System.getenv().getOrDefault("PIONEX_API_SECRET", "YOUR_PIONEX_API_SECRET"));
            pionex.setApiType(System.getenv().getOrDefault("PIONEX_API_TYPE", "spot"));

            // Coinbase (opcional)
            this.coinbase = new CoinbaseConfig();
            coinbase.setApiKey(System.getenv().getOrDefault("COINBASE_API_KEY", "YOUR_COINBASE_API_KEY"));
            coinbase.setApiSecret(System.getenv().getOrDefault("COINBASE_API_SECRET", "YOUR_COINBASE_API_SECRET"));
            coinbase.setPassphrase(System.getenv().getOrDefault("COINBASE_PASSPHRASE", "YOUR_PASSPHRASE"));
            coinbase.setSandbox("true".equalsIgnoreCase(System.getenv().getOrDefault("COINBASE_SANDBOX", "true")));

            // Data Providers
            this.dataProviders = new DataProviderConfig();
            dataProviders.setAlphavantage(System.getenv().getOrDefault("ALPHAVANTAGE_KEY", "YOUR_ALPHAVANTAGE_KEY"));
            dataProviders.setPolygon(System.getenv().getOrDefault("POLYGON_KEY", "YOUR_POLYGON_KEY"));
            dataProviders.setTwelvedata(System.getenv().getOrDefault("TWELVEDATA_KEY", "YOUR_TWELVEDATA_KEY"));
            dataProviders.setNewsapi(System.getenv().getOrDefault("NEWSAPI_KEY", "YOUR_NEWSAPI_KEY"));

            // Segurança
            this.logCredentials = "true".equalsIgnoreCase(System.getenv().getOrDefault("LOG_CREDENTIALS", "false"));
            this.sessionTimeout = Integer.parseInt(System.getenv().getOrDefault("SESSION_TIMEOUT_MINUTES", "30"));
            this.enable2fa = "true".equalsIgnoreCase(System.getenv().getOrDefault("ENABLE_2FA", "false"));
        }

        public boolean validateAll() throws IllegalArgumentException {
            List<String> errors = new ArrayList<>();

            // Binance
            try {
                binance.validate();
                LOGGER.info("✅ Binance: Validado");
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }

            // cTrader
            try {
                ctrader.validate();
                LOGGER.info("✅ cTrader: Validado");
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }

            // Pionex
            try {
                pionex.validate();
                LOGGER.info("✅ Pionex: Validado");
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }

            // Data Providers
            try {
                dataProviders.validate();
                LOGGER.info("✅ Data Providers: Verificado");
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }

            if (!errors.isEmpty()) {
                String errorMsg = String.join("\n", errors);
                LOGGER.severe("\n❌ Erros de Configuração:\n" + errorMsg);
                throw new IllegalArgumentException("Configuração inválida: " + errorMsg);
            }

            LOGGER.info("✅ Todas as configurações validadas com sucesso!");
            return true;
        }

        public String maskCredential(String value, int visible) {
            if (value == null || value.length() <= visible) {
                return "***";
            }
            return value.substring(0, visible) + "***";
        }

        public Map<String, Object> getSafeDict() {
            Map<String, Object> safe = new LinkedHashMap<>();

            Map<String, Object> envMap = new LinkedHashMap<>();
            envMap.put("LOG_CREDENTIALS", logCredentials);
            envMap.put("SESSION_TIMEOUT_MINUTES", sessionTimeout);
            envMap.put("ENABLE_2FA", enable2fa);
            envMap.put("ENCRYPTION_ENABLED", crypto != null && System.getenv("ENCRYPTION_KEY") != null);
            safe.put("environment", envMap);

            Map<String, Object> exchanges = new LinkedHashMap<>();
            Map<String, Object> binMap = new LinkedHashMap<>();
            binMap.put("api_key", maskCredential(binance.getApiKey(), 8));
            binMap.put("testnet", binance.isTestnet());
            binMap.put("environment", binance.getEnvironment());
            exchanges.put("binance", binMap);

            Map<String, Object> ctMap = new LinkedHashMap<>();
            ctMap.put("client_id", maskCredential(ctrader.getClientId(), 8));
            ctMap.put("environment", ctrader.getEnvironment());
            ctMap.put("account_configured", ctrader.getAccountId() != null &&
                    !ctrader.getAccountId().startsWith("YOUR_"));
            exchanges.put("ctrader", ctMap);

            Map<String, Object> pionMap = new LinkedHashMap<>();
            pionMap.put("api_key", maskCredential(pionex.getApiKey(), 8));
            pionMap.put("api_type", pionex.getApiType());
            exchanges.put("pionex", pionMap);

            Map<String, Object> coinMap = new LinkedHashMap<>();
            coinMap.put("api_key", maskCredential(coinbase.getApiKey(), 8));
            coinMap.put("sandbox", coinbase.isSandbox());
            exchanges.put("coinbase", coinMap);

            safe.put("exchanges", exchanges);

            Map<String, Object> dataProv = new LinkedHashMap<>();
            dataProv.put("alphavantage", dataProviders.getAlphavantage().startsWith("YOUR_") ? "NOT_SET" : "configured");
            dataProv.put("polygon", dataProviders.getPolygon().startsWith("YOUR_") ? "NOT_SET" : "configured");
            dataProv.put("twelvedata", dataProviders.getTwelvedata().startsWith("YOUR_") ? "NOT_SET" : "configured");
            dataProv.put("newsapi", dataProviders.getNewsapi().startsWith("YOUR_") ? "NOT_SET" : "configured");
            safe.put("data_providers", dataProv);

            return safe;
        }

        public Map<String, Boolean> getStatus() {
            Map<String, Boolean> status = new LinkedHashMap<>();
            status.put("binance", !binance.getApiKey().startsWith("YOUR_"));
            status.put("ctrader", !ctrader.getClientId().startsWith("YOUR_"));
            status.put("pionex", !pionex.getApiKey().startsWith("YOUR_"));
            status.put("coinbase", !coinbase.getApiKey().startsWith("YOUR_"));
            status.put("encryption", crypto != null && System.getenv("ENCRYPTION_KEY") != null);
            return status;
        }
    }

    // ========================================
    // Instância global (como no Python)
    // ========================================
    public static final CredentialsConfig credentials = new CredentialsConfig();

    // ========================================
    // Métodos auxiliares (exibição / validação)
    // ========================================
    public static void printStatus() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🔐 STATUS DE CONFIGURAÇÃO - LEXTRADER-IAG 4.0");
        System.out.println("=".repeat(70));

        Map<String, Boolean> status = credentials.getStatus();
        System.out.println("\n📊 EXCHANGES:");
        for (Map.Entry<String, Boolean> entry : status.entrySet()) {
            if (!"encryption".equals(entry.getKey())) {
                System.out.println("  " + (entry.getValue() ? "✅" : "❌") + " " + entry.getKey().toUpperCase());
            }
        }

        System.out.println("\n🔒 SEGURANÇA:");
        System.out.println("  " + (status.get("encryption") ? "✅" : "⚠️") + " Criptografia: " +
                (status.get("encryption") ? "Ativa" : "Inativa"));

        System.out.println("\n📋 DETALHES (Mascarados):");
        // Para imprimir o mapa formatado (substitua por Jackson/Gson se preferir)
        System.out.println(formatJsonLike(credentials.getSafeDict()));

        System.out.println("=".repeat(70) + "\n");
    }

    public static boolean validateAndReport() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🔍 VALIDAÇÃO DE CONFIGURAÇÕES");
        System.out.println("=".repeat(70) + "\n");

        try {
            credentials.validateAll();
            printStatus();
            System.out.println("✅ Sistema pronto para operação!");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Erro de Configuração:\n" + e.getMessage() + "\n");
            System.out.println("📝 Próximos passos:");
            System.out.println("  1. Crie arquivo .env na raiz do projeto");
            System.out.println("  2. Copie conteúdo de .env.example");
            System.out.println("  3. Preencha com suas credenciais reais");
            System.out.println("  4. Execute este script novamente\n");
            return false;
        }
    }

    // Método auxiliar para imprimir mapa de forma legível sem bibliotecas externas
    private static String formatJsonLike(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append("  \"").append(entry.getKey()).append("\": ");
            if (entry.getValue() instanceof Map) {
                sb.append(formatJsonLike((Map<String, Object>) entry.getValue(), "  "));
            } else if (entry.getValue() instanceof String) {
                sb.append("\"").append(entry.getValue()).append("\"");
            } else {
                sb.append(entry.getValue());
            }
            sb.append(",\n");
        }
        // remove última vírgula
        if (sb.length() > 2 && sb.charAt(sb.length() - 2) == ',') {
            sb.delete(sb.length() - 3, sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    private static String formatJsonLike(Map<String, Object> map, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        String nextIndent = indent + "  ";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(nextIndent).append("\"").append(entry.getKey()).append("\": ");
            if (entry.getValue() instanceof Map) {
                sb.append(formatJsonLike((Map<String, Object>) entry.getValue(), nextIndent));
            } else if (entry.getValue() instanceof String) {
                sb.append("\"").append(entry.getValue()).append("\"");
            } else {
                sb.append(entry.getValue());
            }
            sb.append(",\n");
        }
        if (sb.length() > 2 && sb.charAt(sb.length() - 2) == ',') {
            sb.delete(sb.length() - (indent.length() + 3), sb.length() - 1);
        }
        sb.append(indent).append("}");
        return sb.toString();
    }

    // Ponto de entrada para testes
    public static void main(String[] args) {
        validateAndReport();
    }
}
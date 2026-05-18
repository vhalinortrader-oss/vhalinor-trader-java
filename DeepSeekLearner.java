import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Cliente DeepSeek para integração com a IA VHALINOR.
 * Compatível com a API do DeepSeek (estilo OpenAI).
 *
 * Funcionalidades:
 * - Geração de respostas (síncrona e streaming)
 * - Few-shot learning
 * - Histórico de conversa
 * - Estatísticas de uso (tokens e custo)
 * - Modo interativo
 *
 * Dependências: org.json (https://mvnrepository.com/artifact/org.json/json)
 */
public class DeepSeekLearner {

    // ======================== Enums ========================
    public enum DeepSeekModel {
        CHAT("deepseek-chat"),
        REASONER("deepseek-reasoner");

        private final String modelName;
        DeepSeekModel(String modelName) { this.modelName = modelName; }
        public String getModelName() { return modelName; }
    }

    // ======================== Pricing ========================
    private static final Map<String, double[]> PRICING = Map.of(
        "deepseek-chat", new double[]{0.28, 0.42},   // input, output per 1M tokens
        "deepseek-reasoner", new double[]{0.28, 0.42}
    );

    // ======================== Configurações ========================
    private static final String DEFAULT_BASE_URL = "https://api.deepseek.com";
    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final int DEFAULT_TIMEOUT_SECONDS = 60;

    // ======================== Campos ========================
    private final String apiKey;
    private final HttpClient httpClient;
    private final String baseUrl;
    private String model;
    private double temperature;
    private int maxTokens;
    private Duration timeout;
    private int maxRetries;
    private boolean autoContext;
    private int maxContextMessages;

    private final List<Map<String, String>> conversationHistory = new ArrayList<>();
    private long totalInputTokens = 0;
    private long totalOutputTokens = 0;

    // ======================== Construtor ========================
    public DeepSeekLearner(String apiKey, String baseUrl, DeepSeekModel model,
                           double temperature, int maxTokens, Duration timeout,
                           int maxRetries, boolean autoContext, int maxContextMessages) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.model = model.getModelName();
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
        this.autoContext = autoContext;
        this.maxContextMessages = maxContextMessages;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();

        System.out.println("DeepSeekLearner inicializado com modelo: " + this.model);
        System.out.println("Base URL: " + this.baseUrl);
    }

    public DeepSeekLearner() {
        this(System.getenv("DEEPSEEK_API_KEY"), DEFAULT_BASE_URL, DeepSeekModel.CHAT,
             DEFAULT_TEMPERATURE, DEFAULT_MAX_TOKENS, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS),
             3, true, 20);
    }

    // ======================== Geração de resposta síncrona ========================
    public String generateResponse(String prompt) {
        return generateResponse(prompt, null, null, null, true);
    }

    public String generateResponse(String prompt, String systemPrompt) {
        return generateResponse(prompt, systemPrompt, null, null, true);
    }

    public String generateResponse(String prompt, String systemPrompt,
                                   Double temperatureOverride, Integer maxTokensOverride,
                                   boolean addToHistory) {
        // Monta as mensagens
        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        if (autoContext && addToHistory) {
            messages.addAll(conversationHistory);
        }
        messages.add(Map.of("role", "user", "content", prompt));

        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("messages", messages);
        payload.put("temperature", temperatureOverride != null ? temperatureOverride : temperature);
        payload.put("max_tokens", maxTokensOverride != null ? maxTokensOverride : maxTokens);
        payload.put("stream", false);

        String url = baseUrl + "/chat/completions";
        try {
            HttpResponse<String> response = makeRequest(url, payload, false);
            JSONObject json = new JSONObject(response.body());
            String assistantMessage = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            // Atualiza contagem de tokens
            if (json.has("usage")) {
                JSONObject usage = json.getJSONObject("usage");
                totalInputTokens += usage.optInt("prompt_tokens", 0);
                totalOutputTokens += usage.optInt("completion_tokens", 0);
            }

            if (autoContext && addToHistory) {
                conversationHistory.add(Map.of("role", "user", "content", prompt));
                conversationHistory.add(Map.of("role", "assistant", "content", assistantMessage));
                trimHistory();
            }
            return assistantMessage;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar resposta: " + e.getMessage(), e);
        }
    }

    // ======================== Geração de resposta em streaming ========================
    public void generateResponseStream(String prompt, Consumer<String> chunkConsumer) {
        generateResponseStream(prompt, null, null, null, chunkConsumer);
    }

    public void generateResponseStream(String prompt, String systemPrompt,
                                       Double temperatureOverride, Integer maxTokensOverride,
                                       Consumer<String> chunkConsumer) {
        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        if (autoContext) {
            messages.addAll(conversationHistory);
        }
        messages.add(Map.of("role", "user", "content", prompt));

        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("messages", messages);
        payload.put("temperature", temperatureOverride != null ? temperatureOverride : temperature);
        payload.put("max_tokens", maxTokensOverride != null ? maxTokensOverride : maxTokens);
        payload.put("stream", true);

        String url = baseUrl + "/chat/completions";
        try {
            HttpResponse<InputStream> response = makeStreamRequest(url, payload);
            StringBuilder fullContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        if (data.equals("[DONE]")) break;
                        JSONObject chunk = new JSONObject(data);
                        JSONArray choices = chunk.optJSONArray("choices");
                        if (choices != null && choices.length() > 0) {
                            JSONObject delta = choices.getJSONObject(0).optJSONObject("delta");
                            if (delta != null) {
                                String content = delta.optString("content", "");
                                if (!content.isEmpty()) {
                                    fullContent.append(content);
                                    chunkConsumer.accept(content);
                                }
                            }
                        }
                        if (chunk.has("usage")) {
                            JSONObject usage = chunk.getJSONObject("usage");
                            totalInputTokens += usage.optInt("prompt_tokens", 0);
                            totalOutputTokens += usage.optInt("completion_tokens", 0);
                        }
                    }
                }
            }
            if (autoContext && fullContent.length() > 0) {
                conversationHistory.add(Map.of("role", "user", "content", prompt));
                conversationHistory.add(Map.of("role", "assistant", "content", fullContent.toString()));
                trimHistory();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro no streaming: " + e.getMessage(), e);
        }
    }

    // ======================== Few-shot Learning ========================
    public Map<String, Object> learnFromExamples(List<Map<String, String>> examples,
                                                 String systemPrompt) {
        String prompt = buildFewShotPrompt(examples);
        String response = generateResponse(prompt, systemPrompt, null, null, false);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("analysis", response);
        result.put("examples_used", examples.size());
        result.put("model", model);
        result.put("timestamp", Instant.now().toString());
        return result;
    }

    // ======================== Informações dos modelos ========================
    public Map<String, Object> getModelInfo() {
        String url = baseUrl + "/models";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(timeout)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Map.of("error", "HTTP " + response.statusCode());
            }
            JSONObject json = new JSONObject(response.body());
            JSONArray data = json.optJSONArray("data");
            List<Map<String, Object>> models = new ArrayList<>();
            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject m = data.getJSONObject(i);
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("id", m.optString("id"));
                    info.put("object", m.optString("object"));
                    info.put("owned_by", m.optString("owned_by"));
                    double[] p = PRICING.getOrDefault(m.optString("id"), new double[]{0,0});
                    info.put("pricing", Map.of("input", p[0], "output", p[1]));
                    models.add(info);
                }
            }
            return Map.of("models", models,
                    "default_context_window", 128000,
                    "api_version", "v1",
                    "timestamp", Instant.now().toString());
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    // ======================== Estatísticas de uso ========================
    public Map<String, Object> getUsageStats() {
        double[] pricing = PRICING.getOrDefault(model, new double[]{0,0});
        double estimatedCost = (totalInputTokens / 1_000_000.0) * pricing[0] +
                               (totalOutputTokens / 1_000_000.0) * pricing[1];
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total_input_tokens", totalInputTokens);
        stats.put("total_output_tokens", totalOutputTokens);
        stats.put("total_tokens", totalInputTokens + totalOutputTokens);
        stats.put("estimated_cost_usd", Math.round(estimatedCost * 1_000_000.0) / 1_000_000.0);
        stats.put("model", model);
        stats.put("pricing_per_1M", Map.of("input", pricing[0], "output", pricing[1]));
        return stats;
    }

    public void clearHistory() {
        conversationHistory.clear();
        System.out.println("Histórico limpo.");
    }

    public void saveConversation(String filepath) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("conversation_history", conversationHistory);
            obj.put("model", model);
            obj.put("timestamp", Instant.now().toString());
            obj.put("usage_stats", getUsageStats());
            Files.writeString(Path.of(filepath), obj.toString(2));
            System.out.println("Conversa salva em " + filepath);
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
        }
    }

    public void loadConversation(String filepath) {
        try {
            String content = Files.readString(Path.of(filepath));
            JSONObject obj = new JSONObject(content);
            JSONArray hist = obj.optJSONArray("conversation_history");
            conversationHistory.clear();
            if (hist != null) {
                for (int i = 0; i < hist.length(); i++) {
                    JSONObject msg = hist.getJSONObject(i);
                    conversationHistory.add(Map.of(
                        "role", msg.getString("role"),
                        "content", msg.getString("content")
                    ));
                }
            }
            System.out.println("Conversa carregada de " + filepath);
        } catch (IOException e) {
            System.err.println("Erro ao carregar: " + e.getMessage());
        }
    }

    // ======================== Métodos privados ========================
    private HttpResponse<String> makeRequest(String url, JSONObject payload, boolean stream) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(timeout)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()));

        int retries = maxRetries;
        while (true) {
            try {
                HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 429 || response.statusCode() >= 500) {
                    if (--retries > 0) {
                        Thread.sleep((long) Math.pow(2, maxRetries - retries) * 1000);
                        continue;
                    }
                }
                if (response.statusCode() >= 400) {
                    throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
                }
                return response;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }
    }

    private HttpResponse<InputStream> makeStreamRequest(String url, JSONObject payload) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(timeout)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        int retries = maxRetries;
        while (true) {
            try {
                HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() == 429 || response.statusCode() >= 500) {
                    if (--retries > 0) {
                        Thread.sleep((long) Math.pow(2, maxRetries - retries) * 1000);
                        continue;
                    }
                }
                if (response.statusCode() >= 400) {
                    // consume and throw
                    String errorBody = new String(response.body().readAllBytes());
                    throw new IOException("HTTP " + response.statusCode() + ": " + errorBody);
                }
                return response;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }
    }

    private void trimHistory() {
        while (conversationHistory.size() > maxContextMessages * 2) {
            conversationHistory.remove(0);
        }
    }

    private String buildFewShotPrompt(List<Map<String, String>> examples) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < examples.size(); i++) {
            Map<String, String> ex = examples.get(i);
            sb.append("Exemplo ").append(i+1).append(":\n");
            sb.append("Input: ").append(ex.getOrDefault("input", "")).append("\n");
            sb.append("Output esperado: ").append(ex.getOrDefault("output", "")).append("\n\n");
        }
        return sb.toString();
    }

    // ======================== Modo interativo ========================
    public static void interactiveMode() {
        System.out.println("=".repeat(60));
        System.out.println("🤖 VHALINOR - Modo de Aprendizado com DeepSeek");
        System.out.println("=".repeat(60));
        printHelp();

        DeepSeekLearner learner;
        try {
            learner = new DeepSeekLearner();
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            System.err.println("Configure a variável DEEPSEEK_API_KEY.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nVocê: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            // Processa comandos
            if (input.equalsIgnoreCase("/sair") || input.equalsIgnoreCase("/exit") ||
                input.equalsIgnoreCase("/quit")) {
                System.out.println("👋 Encerrando...");
                break;
            }
            if (input.equalsIgnoreCase("/clear") || input.equalsIgnoreCase("/limpar")) {
                learner.clearHistory();
                continue;
            }
            if (input.equalsIgnoreCase("/history")) {
                if (learner.conversationHistory.isEmpty()) {
                    System.out.println("📜 Nenhum histórico ainda.");
                } else {
                    System.out.println("\n📜 Histórico:");
                    for (int i = 0; i < learner.conversationHistory.size(); i++) {
                        Map<String, String> msg = learner.conversationHistory.get(i);
                        String role = msg.get("role").equals("user") ? "Você" : "VHALINOR";
                        String content = msg.get("content");
                        if (content.length() > 100) content = content.substring(0, 100) + "...";
                        System.out.printf("%d. %s: %s%n", i+1, role, content);
                    }
                }
                continue;
            }
            if (input.toLowerCase().startsWith("/model ")) {
                String modelName = input.substring(7).trim().toLowerCase();
                if (modelName.equals("chat")) {
                    learner.model = DeepSeekModel.CHAT.getModelName();
                    System.out.println("✅ Modelo alterado para deepseek-chat");
                } else if (modelName.equals("reasoner")) {
                    learner.model = DeepSeekModel.REASONER.getModelName();
                    System.out.println("✅ Modelo alterado para deepseek-reasoner");
                } else {
                    System.out.println("❌ Modelo inválido. Use 'chat' ou 'reasoner'");
                }
                continue;
            }
            if (input.toLowerCase().startsWith("/temp ")) {
                try {
                    double temp = Double.parseDouble(input.substring(6).trim());
                    if (temp >= 0.0 && temp <= 1.0) {
                        learner.temperature = temp;
                        System.out.println("✅ Temperatura ajustada para " + temp);
                    } else {
                        System.out.println("❌ Temperatura deve estar entre 0.0 e 1.0");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Valor inválido.");
                }
                continue;
            }
            if (input.equalsIgnoreCase("/stats")) {
                Map<String, Object> stats = learner.getUsageStats();
                System.out.println("\n📊 Estatísticas:");
                System.out.printf("   Tokens de entrada: %,d%n", stats.get("total_input_tokens"));
                System.out.printf("   Tokens de saída: %,d%n", stats.get("total_output_tokens"));
                System.out.printf("   Total de tokens: %,d%n", stats.get("total_tokens"));
                System.out.printf("   Custo estimado: US$ %.6f%n", stats.get("estimated_cost_usd"));
                continue;
            }
            if (input.toLowerCase().startsWith("/save ")) {
                learner.saveConversation(input.substring(6).trim());
                continue;
            }
            if (input.toLowerCase().startsWith("/load ")) {
                learner.loadConversation(input.substring(6).trim());
                continue;
            }
            if (input.equalsIgnoreCase("/help")) {
                printHelp();
                continue;
            }

            // Resposta
            System.out.print("\n⏳ Processando...");
            String response = learner.generateResponse(input);
            System.out.println("\r" + " ".repeat(30) + "\r");
            System.out.println("🤖 VHALINOR: " + response);
        }
        scanner.close();
    }

    private static void printHelp() {
        System.out.println("\nComandos especiais:");
        System.out.println("  /sair, /exit, /quit  - Encerrar");
        System.out.println("  /clear, /limpar      - Limpar histórico");
        System.out.println("  /history              - Mostrar histórico");
        System.out.println("  /model <chat|reasoner>- Trocar modelo");
        System.out.println("  /temp <valor>         - Ajustar temperatura (0.0 a 1.0)");
        System.out.println("  /stats                - Mostrar estatísticas de uso");
        System.out.println("  /save <arquivo>       - Salvar conversa");
        System.out.println("  /load <arquivo>       - Carregar conversa");
        System.out.println("  /help                 - Esta mensagem\n");
    }

    // ======================== main para demonstração ========================
    public static void main(String[] args) {
        interactiveMode();
    }
}
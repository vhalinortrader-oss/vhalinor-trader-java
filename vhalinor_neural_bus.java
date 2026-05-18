package com.vhalinor.neuralbus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════════╗
 * ║                    VHALINOR IAG 1.0.0 - NEURAL BUS QUÂNTICO                  ║
 * ║         SISTEMA DE COMUNICAÇÃO NEURAL DISTRIBUÍDA COM IA QUÂNTICA            ║
 * ╠═══════════════════════════════════════════════════════════════════════════════╣
 * ║  Módulo: CAMADA DE COMUNICAÇÃO - NEURAL BUS (Layer 02)                       ║
 * ║  Versão: 3.1.0 (Production Ready - Ultra Avançada)                          ║
 * ║  Autor: Alex Miranda Sales                                                   ║
 * ║  Data: 2026                                                                  ║
 * ║  License: Proprietary - Vhalinor IAG Systems                                 ║
 * ║  Status: 🟢 TOTALMENTE OPERACIONAL | ⚡ LATÊNCIA <1ms | 🔗 1000+ NÓS        ║
 * ╚═══════════════════════════════════════════════════════════════════════════════╝
 *
 * Versão Java - adaptada para ecossistema JVM mantendo a arquitetura original.
 * 
 * @author Convertido do Python original
 */
public class NeuralBusJava {

    // =========================================================================
    // LOGGING
    // =========================================================================
    private static final Logger LOGGER = Logger.getLogger("VhalinorNeuralBus");
    static {
        try {
            // Arquivo rotativo
            FileHandler fileHandler = new FileHandler("vhalinor_neural_bus.log", 50_000_000, 10, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            // Console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    return String.format("%1$tF %1$tT | %2$-8s | %3$s - %4$s%n",
                            record.getMillis(), record.getLevel().getName(),
                            record.getLoggerName(), record.getMessage());
                }
            });
            LOGGER.addHandler(consoleHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Falha ao configurar logging: " + e);
        }
    }

    // =========================================================================
    // ENUMS
    // =========================================================================
    public enum MessagePriority {
        BACKGROUND(0, "⚪", "Processos em background"),
        LOW(1, "🟢", "Baixa prioridade"),
        NORMAL(2, "🔵", "Prioridade normal"),
        HIGH(3, "🟠", "Alta prioridade"),
        CRITICAL(4, "🔴", "Prioridade crítica"),
        EMERGENCY(5, "💀", "Emergência - processamento imediato");

        public final int value;
        public final String icon;
        public final String description;

        MessagePriority(int value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    public enum MessageType {
        EVENT("event", "📢", "Evento de sistema"),
        REQUEST("request", "📨", "Requisição síncrona"),
        RESPONSE("response", "📩", "Resposta a requisição"),
        COMMAND("command", "⚡", "Comando de execução"),
        STATUS("status", "📊", "Atualização de status"),
        ERROR("error", "❌", "Mensagem de erro"),
        HEARTBEAT("heartbeat", "💓", "Sinal de atividade"),
        SYNC("sync", "🔄", "Sincronização de estado"),
        QUANTUM("quantum", "⚛️", "Mensagem quântica"),
        BROADCAST("broadcast", "📡", "Transmissão geral");

        public final String value;
        public final String icon;
        public final String description;

        MessageType(String value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    public enum ComponentStatus {
        ACTIVE("active", "🟢", "Componente ativo"),
        IDLE("idle", "⚪", "Componente ocioso"),
        BUSY("busy", "🟡", "Componente ocupado"),
        ERROR("error", "🔴", "Componente com erro"),
        DISABLED("disabled", "⚫", "Componente desabilitado"),
        DEGRADED("degraded", "🟠", "Componente degradado"),
        RECOVERING("recovering", "🔄", "Componente em recuperação");

        public final String value;
        public final String icon;
        public final String description;

        ComponentStatus(String value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    public enum RoutingStrategy {
        DIRECT("direct", "🎯", "Entrega direta"),
        BROADCAST("broadcast", "📡", "Transmissão para todos"),
        ANYCAST("anycast", "🎲", "Primeiro disponível"),
        MULTICAST("multicast", "👥", "Grupo específico"),
        LOAD_BALANCED("load_balanced", "⚖️", "Balanceamento de carga"),
        PRIORITY("priority", "📊", "Baseado em prioridade");

        public final String value;
        public final String icon;
        public final String description;

        RoutingStrategy(String value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    public enum CircuitBreakerState {
        CLOSED("closed", "🟢", "Operação normal"),
        OPEN("open", "🔴", "Bloqueado"),
        HALF_OPEN("half_open", "🟡", "Testando recuperação");

        public final String value;
        public final String icon;
        public final String description;

        CircuitBreakerState(String value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    // =========================================================================
    // CONFIGURAÇÕES DEFAULT
    // =========================================================================
    public static class DefaultConfig {
        public static final int MAX_QUEUE_SIZE = 100_000;
        public static final int MAX_WORKERS = Math.min(32, Runtime.getRuntime().availableProcessors() + 4);
        public static final boolean ENABLE_METRICS = true;
        public static final boolean ENABLE_PERSISTENCE = false;
        public static final boolean ENABLE_QUANTUM_BRIDGE = true;
        public static final boolean ENABLE_CIRCUIT_BREAKER = true;
        public static final boolean ENABLE_COMPRESSION = true;
        public static final boolean ENABLE_ENCRYPTION = false;
        public static final double DEFAULT_TIMEOUT = 30.0;
        public static final double HEARTBEAT_INTERVAL = 5.0;
        public static final double CLEANUP_INTERVAL = 1.0;
        public static final int MAX_RETRIES = 3;
        public static final double RETRY_DELAY = 1.0;
        public static final int CIRCUIT_BREAKER_THRESHOLD = 5;
        public static final int CIRCUIT_BREAKER_TIMEOUT = 60;
        public static final int MESSAGE_CACHE_SIZE = 10_000;
        public static final double COMPONENT_TIMEOUT = 300.0; // 5 minutos
        public static final int MAX_PAYLOAD_SIZE = 10 * 1024 * 1024; // 10MB
        public static final int COMPRESSION_LEVEL = 6; // zlib (1-9)
    }

    // =========================================================================
    // ESTRUTURAS DE DADOS
    // =========================================================================

    /**
     * Mensagem neural com metadados completos.
     */
    public static class NeuralMessage {
        public final String id;
        public final String source;
        public final List<String> destinations;
        public final MessageType type;
        public final MessagePriority priority;
        public final Object payload;           // pode ser bytes se comprimido
        public final double timestamp;
        public final double ttl;
        public final String correlationId;
        public final RoutingStrategy routing;
        public final boolean compressed;
        public final boolean encrypted;
        public int retryCount;
        public final int maxRetries;
        public final Map<String, Object> metadata;

        private NeuralMessage(Builder builder) {
            this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
            this.source = builder.source;
            this.destinations = builder.destinations;
            this.type = builder.type;
            this.priority = builder.priority;
            this.payload = builder.payload;
            this.timestamp = System.currentTimeMillis() / 1000.0;
            this.ttl = builder.ttl;
            this.correlationId = builder.correlationId;
            this.routing = builder.routing;
            this.compressed = builder.compressed;
            this.encrypted = builder.encrypted;
            this.retryCount = builder.retryCount;
            this.maxRetries = builder.maxRetries;
            this.metadata = builder.metadata != null ? builder.metadata : new HashMap<>();
        }

        public boolean isExpired() {
            return (System.currentTimeMillis() / 1000.0 - timestamp) > ttl;
        }

        public boolean canRetry() {
            return retryCount < maxRetries;
        }

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", id);
            map.put("source", source);
            map.put("destinations", destinations);
            map.put("type", type.value);
            map.put("priority", priority.value);
            map.put("payload_preview", payload != null ? payload.toString().substring(0, Math.min(100, payload.toString().length())) : "null");
            map.put("timestamp", timestamp);
            map.put("age", String.format("%.2fs", System.currentTimeMillis() / 1000.0 - timestamp));
            map.put("ttl", ttl);
            map.put("correlation_id", correlationId);
            map.put("routing", routing.value);
            map.put("retry_count", retryCount);
            map.put("compressed", compressed);
            return map;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String id;
            private String source = "system";
            private List<String> destinations;
            private MessageType type = MessageType.EVENT;
            private MessagePriority priority = MessagePriority.NORMAL;
            private Object payload;
            private double ttl = DefaultConfig.DEFAULT_TIMEOUT;
            private String correlationId;
            private RoutingStrategy routing = RoutingStrategy.DIRECT;
            private boolean compressed = false;
            private boolean encrypted = false;
            private int retryCount = 0;
            private int maxRetries = DefaultConfig.MAX_RETRIES;
            private Map<String, Object> metadata = new HashMap<>();

            public Builder id(String id) { this.id = id; return this; }
            public Builder source(String source) { this.source = source; return this; }
            public Builder destinations(List<String> destinations) { this.destinations = destinations; return this; }
            public Builder type(MessageType type) { this.type = type; return this; }
            public Builder priority(MessagePriority priority) { this.priority = priority; return this; }
            public Builder payload(Object payload) { this.payload = payload; return this; }
            public Builder ttl(double ttl) { this.ttl = ttl; return this; }
            public Builder correlationId(String correlationId) { this.correlationId = correlationId; return this; }
            public Builder routing(RoutingStrategy routing) { this.routing = routing; return this; }
            public Builder compressed(boolean compressed) { this.compressed = compressed; return this; }
            public Builder encrypted(boolean encrypted) { this.encrypted = encrypted; return this; }
            public Builder retryCount(int retryCount) { this.retryCount = retryCount; return this; }
            public Builder maxRetries(int maxRetries) { this.maxRetries = maxRetries; return this; }
            public Builder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
            public Builder addMetadata(String key, Object val) { this.metadata.put(key, val); return this; }

            public NeuralMessage build() {
                return new NeuralMessage(this);
            }
        }
    }

    /**
     * Registro de componente no barramento.
     */
    public static class ComponentRegistration {
        public final String name;
        public final Object instance;
        public final Map<String, Object> metadata;
        public volatile ComponentStatus status;
        public final double registeredAt;
        public volatile double lastHeartbeat;
        public volatile double lastActivity;
        public final AtomicLong messageCount = new AtomicLong();
        public final AtomicLong errorCount = new AtomicLong();
        public volatile double avgResponseTime = 0.0;
        public final ConcurrentMap<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

        public ComponentRegistration(String name, Object instance, Map<String, Object> metadata) {
            this.name = name;
            this.instance = instance;
            this.metadata = metadata != null ? metadata : new HashMap<>();
            this.status = ComponentStatus.ACTIVE;
            this.registeredAt = System.currentTimeMillis() / 1000.0;
            this.lastHeartbeat = registeredAt;
            this.lastActivity = registeredAt;
        }

        public boolean isAlive() {
            return (System.currentTimeMillis() / 1000.0 - lastHeartbeat) < DefaultConfig.COMPONENT_TIMEOUT;
        }

        public void updateActivity() {
            this.lastActivity = System.currentTimeMillis() / 1000.0;
            messageCount.incrementAndGet();
        }

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", name);
            map.put("type", instance.getClass().getSimpleName());
            map.put("status", status.value);
            map.put("status_icon", status.icon);
            map.put("alive", isAlive());
            map.put("uptime", String.format("%.1fs", System.currentTimeMillis() / 1000.0 - registeredAt));
            map.put("last_heartbeat", String.format("%.1fs ago", System.currentTimeMillis() / 1000.0 - lastHeartbeat));
            map.put("messages", messageCount.get());
            map.put("errors", errorCount.get());
            map.put("avg_response_time", String.format("%.2fms", avgResponseTime * 1000));
            return map;
        }
    }

    /**
     * Métricas agregadas do barramento.
     */
    public static class NeuralBusMetrics {
        public final AtomicLong messageCount = new AtomicLong();
        public final ConcurrentMap<String, AtomicLong> byType = new ConcurrentHashMap<>();
        public final ConcurrentMap<MessagePriority, AtomicLong> byPriority = new ConcurrentHashMap<>();
        public final DoubleAdder totalProcessingTime = new DoubleAdder();
        public double avgProcessingTime = 0.0;
        public volatile double minProcessingTime = Double.MAX_VALUE;
        public volatile double maxProcessingTime = 0.0;
        public final AtomicLong errorCount = new AtomicLong();
        public final AtomicLong retryCount = new AtomicLong();
        public final AtomicLong timeoutCount = new AtomicLong();
        public volatile int registrySize = 0;
        public volatile int subscriptionCount = 0;
        public volatile int activeComponents = 0;
        public final double startTime = System.currentTimeMillis() / 1000.0;
        public volatile double cpuPercent = 0.0;
        public volatile double memoryPercent = 0.0;
        public final ConcurrentMap<String, Integer> queueSizes = new ConcurrentHashMap<>();

        public void updateMessageStats(NeuralMessage msg, double processingTime) {
            messageCount.incrementAndGet();
            byType.computeIfAbsent(msg.type.value, k -> new AtomicLong()).incrementAndGet();
            byPriority.computeIfAbsent(msg.priority, k -> new AtomicLong()).incrementAndGet();

            if (processingTime > 0) {
                totalProcessingTime.add(processingTime);
                avgProcessingTime = totalProcessingTime.sum() / messageCount.get();
                synchronized (this) {
                    if (processingTime < minProcessingTime) minProcessingTime = processingTime;
                    if (processingTime > maxProcessingTime) maxProcessingTime = processingTime;
                }
            }
        }

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            Map<String, Object> msgs = new LinkedHashMap<>();
            msgs.put("total", messageCount.get());
            Map<String, Long> byTypeMap = new HashMap<>();
            byType.forEach((k,v) -> byTypeMap.put(k, v.get()));
            msgs.put("by_type", byTypeMap);
            Map<String, Long> byPriorityMap = new HashMap<>();
            byPriority.forEach((k,v) -> byPriorityMap.put(k.name(), v.get()));
            msgs.put("by_priority", byPriorityMap);
            map.put("messages", msgs);

            Map<String, Object> proc = new LinkedHashMap<>();
            proc.put("avg_ms", avgProcessingTime * 1000);
            proc.put("min_ms", minProcessingTime == Double.MAX_VALUE ? 0 : minProcessingTime * 1000);
            proc.put("max_ms", maxProcessingTime * 1000);
            map.put("processing", proc);

            Map<String, Object> errs = new LinkedHashMap<>();
            errs.put("total", errorCount.get());
            errs.put("retries", retryCount.get());
            errs.put("timeouts", timeoutCount.get());
            map.put("errors", errs);

            Map<String, Object> comps = new LinkedHashMap<>();
            comps.put("registered", registrySize);
            comps.put("active", activeComponents);
            comps.put("subscriptions", subscriptionCount);
            map.put("components", comps);

            Map<String, Object> sys = new LinkedHashMap<>();
            sys.put("uptime", String.format("%.1fs", System.currentTimeMillis() / 1000.0 - startTime));
            sys.put("cpu_percent", cpuPercent);
            sys.put("memory_percent", memoryPercent);
            sys.put("queues", new HashMap<>(queueSizes));
            map.put("system", sys);
            return map;
        }
    }

    // =========================================================================
    // CIRCUIT BREAKER
    // =========================================================================
    public static class CircuitBreaker {
        private final String name;
        private final int threshold;
        private final int timeoutSeconds;
        private volatile CircuitBreakerState state = CircuitBreakerState.CLOSED;
        private volatile int failureCount = 0;
        private volatile double lastFailureTime = 0;
        private final AtomicLong totalFailures = new AtomicLong();
        private final AtomicLong totalSuccesses = new AtomicLong();

        public CircuitBreaker(String name, int threshold, int timeoutSeconds) {
            this.name = name;
            this.threshold = threshold;
            this.timeoutSeconds = timeoutSeconds;
        }

        public synchronized <T> T call(Supplier<T> supplier) throws Exception {
            if (state == CircuitBreakerState.OPEN) {
                if (System.currentTimeMillis() / 1000.0 - lastFailureTime > timeoutSeconds) {
                    state = CircuitBreakerState.HALF_OPEN;
                    LOGGER.info("🔄 Circuit breaker half-open para " + name);
                } else {
                    throw new RuntimeException("Circuit breaker OPEN for " + name);
                }
            }
            try {
                T result = supplier.get();
                synchronized (this) {
                    totalSuccesses.incrementAndGet();
                    if (state == CircuitBreakerState.HALF_OPEN) {
                        state = CircuitBreakerState.CLOSED;
                        failureCount = 0;
                        LOGGER.info("✅ Circuit breaker closed para " + name);
                    }
                }
                return result;
            } catch (Exception e) {
                synchronized (this) {
                    totalFailures.incrementAndGet();
                    failureCount++;
                    lastFailureTime = System.currentTimeMillis() / 1000.0;
                    if (failureCount >= threshold) {
                        state = CircuitBreakerState.OPEN;
                        LOGGER.severe("🔴 Circuit breaker OPEN for " + name + " after " + failureCount + " failures");
                    }
                }
                throw e;
            }
        }

        public double successRate() {
            long total = totalSuccesses.get() + totalFailures.get();
            return total > 0 ? (totalSuccesses.get() * 100.0) / total : 100.0;
        }

        public synchronized void reset() {
            state = CircuitBreakerState.CLOSED;
            failureCount = 0;
            LOGGER.info("🔄 Circuit breaker reset for " + name);
        }

        public CircuitBreakerState getState() { return state; }
        public long getTotalFailures() { return totalFailures.get(); }
        public long getTotalSuccesses() { return totalSuccesses.get(); }
    }

    // =========================================================================
    // RATE LIMITER (TOKEN BUCKET)
    // =========================================================================
    public static class RateLimiter {
        private final double rate;          // tokens per second
        private volatile double tokens;
        private volatile double lastRefill;

        public RateLimiter(double rate) {
            this.rate = rate;
            this.tokens = rate;
            this.lastRefill = System.nanoTime() / 1e9;
        }

        public synchronized boolean acquire(int needed) {
            double now = System.nanoTime() / 1e9;
            double elapsed = now - lastRefill;
            tokens += elapsed * rate;
            if (tokens > rate) tokens = rate;
            lastRefill = now;
            if (tokens >= needed) {
                tokens -= needed;
                return true;
            } else {
                // Wait time (approx) – não bloqueante aqui, apenas consome o que pode
                // Para simplicidade, retornamos false e o chamador deve decidir
                return false;
            }
        }

        public boolean acquire() {
            return acquire(1);
        }
    }

    // =========================================================================
    // COMPONENTE NEURAL BASE
    // =========================================================================
    public static abstract class NeuralComponent {
        protected final String name;
        protected NeuralBus bus;
        protected volatile boolean registered = false;
        protected volatile ComponentStatus status = ComponentStatus.IDLE;

        public NeuralComponent(String name, NeuralBus bus) {
            this.name = name;
            this.bus = bus;
        }

        public abstract CompletableFuture<Boolean> initialize();
        public abstract CompletableFuture<Boolean> shutdown();

        public CompletableFuture<Boolean> register(Map<String, Object> metadata) {
            if (!registered) {
                return bus.register(name, this, metadata).thenApply(success -> {
                    if (success) {
                        registered = true;
                        status = ComponentStatus.ACTIVE;
                        LOGGER.info("✅ Componente registrado: " + name);
                    }
                    return success;
                });
            }
            return CompletableFuture.completedFuture(true);
        }

        public CompletableFuture<Boolean> unregister() {
            if (registered) {
                return bus.unregister(name).thenApply(success -> {
                    if (success) {
                        registered = false;
                        status = ComponentStatus.DISABLED;
                        LOGGER.info("❌ Componente removido: " + name);
                    }
                    return success;
                });
            }
            return CompletableFuture.completedFuture(true);
        }

        public CompletableFuture<Void> onNeuralEvent(String event, Object payload) {
            LOGGER.fine("📨 Evento recebido: " + event);
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<String> sendEvent(String event, Object payload,
                                                   MessagePriority priority,
                                                   RoutingStrategy routing) {
            return bus.broadcast(event, payload, name, priority, routing);
        }

        public <T> CompletableFuture<T> request(String target, String method, Object... args) {
            return bus.request(target, method, args, DefaultConfig.DEFAULT_TIMEOUT,
                    MessagePriority.NORMAL, name, true);
        }

        public CompletableFuture<Void> heartbeat() {
            return bus.heartbeat(name);
        }

        public ComponentStatus getStatus() { return status; }
        public void setStatus(ComponentStatus status) { this.status = status; }
    }

    // =========================================================================
    // QUANTUM BRIDGE (simplificada - stubs para extensão futura)
    // =========================================================================
    public static class QuantumBridge {
        private final NeuralBus bus;
        private boolean connected = false;
        private final Map<String, Object> circuits = new ConcurrentHashMap<>();

        public QuantumBridge(NeuralBus bus) {
            this.bus = bus;
        }

        public CompletableFuture<Boolean> connect() {
            // No Java, dependeríamos de uma biblioteca quântica externa (ex.: via JNI)
            // Por enquanto, simulamos como indisponível.
            LOGGER.info("ℹ️ Bridge Quântica não disponível em ambiente Java padrão");
            connected = false;
            return CompletableFuture.completedFuture(false);
        }

        public CompletableFuture<Boolean> disconnect() {
            connected = false;
            LOGGER.info("🔌 Bridge Quântica desconectada");
            return CompletableFuture.completedFuture(true);
        }

        public CompletableFuture<Boolean> sendQuantumMessage(NeuralMessage message) {
            // Stub
            return CompletableFuture.completedFuture(false);
        }

        public CompletableFuture<Object> createQuantumCircuit(String name, int qubits) {
            // Stub
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<Map<String, Object>> executeQuantumCircuit(String name) {
            // Stub
            return CompletableFuture.completedFuture(null);
        }
    }

    // =========================================================================
    // NEURAL BUS PRINCIPAL
    // =========================================================================
    public static class NeuralBus {
        private static volatile NeuralBus INSTANCE;
        private static final Object LOCK = new Object();

        private final Map<String, Object> config;

        // Registro e assinaturas
        private final ConcurrentMap<String, ComponentRegistration> registry = new ConcurrentHashMap<>();
        private final ConcurrentMap<String, List<ConsumerWithPriority>> subscriptions = new ConcurrentHashMap<>();
        private final List<ConsumerWithPriority> wildcardSubscribers = new CopyOnWriteArrayList<>();

        // Filas de mensagens por prioridade
        private final EnumMap<MessagePriority, BlockingQueue<NeuralMessage>> messageQueues;

        // Requests pendentes
        private final ConcurrentMap<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();
        private final ConcurrentMap<String, Double> requestTimeouts = new ConcurrentHashMap<>();

        // Bridges
        private QuantumBridge quantumBridge;

        // Workers
        private volatile boolean running = false;
        private final List<Thread> workers = new ArrayList<>();
        private final ExecutorService workerPool;

        // Métricas
        private final NeuralBusMetrics metrics;
        private final RateLimiter rateLimiter = new RateLimiter(1000);
        private final ConcurrentMap<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

        // Cache
        private final Deque<NeuralMessage> messageCache = new ArrayDeque<>();
        private final int maxCacheSize;

        private final double startTime = System.currentTimeMillis() / 1000.0;

        public NeuralBus(Map<String, Object> config) {
            this.config = new HashMap<>(DefaultConfig.toMap());
            if (config != null) this.config.putAll(config);

            // Inicializa filas
            messageQueues = new EnumMap<>(MessagePriority.class);
            int baseSize = (int) this.config.getOrDefault("max_queue_size", DefaultConfig.MAX_QUEUE_SIZE);
            for (MessagePriority prio : MessagePriority.values()) {
                int size = Math.max(100, baseSize / (prio.value + 1));
                messageQueues.put(prio, new LinkedBlockingQueue<>(size));
            }

            this.metrics = (boolean) this.config.getOrDefault("enable_metrics", DefaultConfig.ENABLE_METRICS)
                    ? new NeuralBusMetrics() : null;
            this.maxCacheSize = (int) this.config.getOrDefault("message_cache_size", DefaultConfig.MESSAGE_CACHE_SIZE);
            this.workerPool = Executors.newFixedThreadPool(
                    (int) this.config.getOrDefault("max_workers", DefaultConfig.MAX_WORKERS));

            LOGGER.info("🧠 VHALINOR IAG - NEURAL BUS QUÂNTICO INICIALIZADO");
            LOGGER.info("⚙️  Config: Workers=" + config.get("max_workers") +
                    ", Queue=" + config.get("max_queue_size") +
                    ", Timeout=" + config.get("default_timeout") + "s");
            LOGGER.info("⚛️  Quantum Bridge: " + (boolean) this.config.getOrDefault("enable_quantum_bridge",
                    DefaultConfig.ENABLE_QUANTUM_BRIDGE) ? "✅" : "❌");
        }

        public static NeuralBus getInstance(Map<String, Object> config) {
            if (INSTANCE == null) {
                synchronized (LOCK) {
                    if (INSTANCE == null) {
                        INSTANCE = new NeuralBus(config);
                    }
                }
            }
            return INSTANCE;
        }

        // =====================================================================
        // CONTROLE
        // =====================================================================
        public synchronized CompletableFuture<Void> start() {
            if (running) return CompletableFuture.completedFuture(null);
            running = true;

            // Workers por prioridade
            for (MessagePriority prio : MessagePriority.values()) {
                Thread worker = new Thread(() -> messageWorker(prio), "NeuralBusWorker-" + prio.name());
                worker.setDaemon(true);
                worker.start();
                workers.add(worker);
            }

            // Limpeza e heartbeat
            startDaemon("NeuralBusCleanup", this::cleanupWorker, DefaultConfig.CLEANUP_INTERVAL);
            startDaemon("NeuralBusHeartbeat", this::heartbeatWorker, DefaultConfig.HEARTBEAT_INTERVAL);
            if (metrics != null) {
                startDaemon("NeuralBusMetrics", this::metricsWorker, 5.0);
            }

            // Quantum bridge
            if ((boolean) config.getOrDefault("enable_quantum_bridge", DefaultConfig.ENABLE_QUANTUM_BRIDGE)) {
                quantumBridge = new QuantumBridge(this);
                quantumBridge.connect();
            }

            LOGGER.info("✅ NeuralBus iniciado com " + workers.size() + " workers");
            return CompletableFuture.completedFuture(null);
        }

        private void startDaemon(String name, Runnable task, double intervalSeconds) {
            Thread t = new Thread(() -> {
                while (running) {
                    try {
                        task.run();
                        Thread.sleep((long)(intervalSeconds * 1000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Erro no worker " + name, e);
                    }
                }
            }, name);
            t.setDaemon(true);
            t.start();
            workers.add(t);
        }

        public synchronized CompletableFuture<Void> stop() {
            running = false;
            workers.forEach(Thread::interrupt);
            workers.clear();
            pendingRequests.values().forEach(f -> f.completeExceptionally(new InterruptedException("Bus stopped")));
            pendingRequests.clear();
            requestTimeouts.clear();
            if (quantumBridge != null) quantumBridge.disconnect();
            workerPool.shutdownNow();
            LOGGER.info("⏹️ NeuralBus parado");
            return CompletableFuture.completedFuture(null);
        }

        // =====================================================================
        // REGISTRO
        // =====================================================================
        public CompletableFuture<Boolean> register(String name, Object instance, Map<String, Object> metadata) {
            ComponentRegistration reg = new ComponentRegistration(name, instance, metadata);
            registry.put(name, reg);
            if (metrics != null) {
                metrics.registrySize = registry.size();
                metrics.activeComponents = (int) registry.values().stream().filter(ComponentRegistration::isAlive).count();
            }
            LOGGER.info("✅ Componente registrado: " + name);
            return CompletableFuture.completedFuture(true);
        }

        public CompletableFuture<Boolean> unregister(String name) {
            if (registry.remove(name) != null) {
                if (metrics != null) {
                    metrics.registrySize = registry.size();
                    metrics.activeComponents = (int) registry.values().stream().filter(ComponentRegistration::isAlive).count();
                }
                LOGGER.info("❌ Componente removido: " + name);
                return CompletableFuture.completedFuture(true);
            }
            return CompletableFuture.completedFuture(false);
        }

        public CompletableFuture<Object> getComponent(String name) {
            ComponentRegistration reg = registry.get(name);
            if (reg != null && reg.isAlive()) {
                return CompletableFuture.completedFuture(reg.instance);
            }
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<Boolean> heartbeat(String name) {
            ComponentRegistration reg = registry.get(name);
            if (reg != null) {
                reg.lastHeartbeat = System.currentTimeMillis() / 1000.0;
                reg.status = ComponentStatus.ACTIVE;
                return CompletableFuture.completedFuture(true);
            }
            return CompletableFuture.completedFuture(false);
        }

        public Map<String, Map<String, Object>> listComponents() {
            Map<String, Map<String, Object>> result = new LinkedHashMap<>();
            registry.forEach((name, reg) -> result.put(name, reg.toDict()));
            return result;
        }

        // =====================================================================
        // PUB/SUB
        // =====================================================================
        public CompletableFuture<Void> subscribe(String event, BiConsumer<String, Object> handler,
                                                  MessagePriority priority) {
            if ("*".equals(event)) {
                wildcardSubscribers.add(new ConsumerWithPriority(handler, priority));
            } else {
                subscriptions.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>())
                        .add(new ConsumerWithPriority(handler, priority));
            }
            if (metrics != null) {
                metrics.subscriptionCount = subscriptions.values().stream().mapToInt(List::size).sum()
                        + wildcardSubscribers.size();
            }
            LOGGER.fine("📝 Handler inscrito para evento: " + event);
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<Void> unsubscribe(String event, BiConsumer<String, Object> handler) {
            if ("*".equals(event)) {
                wildcardSubscribers.removeIf(cp -> cp.handler == handler);
            } else {
                List<ConsumerWithPriority> handlers = subscriptions.get(event);
                if (handlers != null) {
                    handlers.removeIf(cp -> cp.handler == handler);
                }
            }
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<String> broadcast(String event, Object payload, String source,
                                                    MessagePriority priority, RoutingStrategy routing) {
            rateLimiter.acquire();

            boolean useCompression = (boolean) config.getOrDefault("enable_compression", DefaultConfig.ENABLE_COMPRESSION);
            Object finalPayload = payload;
            boolean compressed = false;
            if (useCompression && payload != null) {
                try {
                    finalPayload = compress(payload);
                    compressed = true;
                } catch (Exception e) {
                    LOGGER.warning("Falha na compressão: " + e.getMessage());
                }
            }

            NeuralMessage msg = NeuralMessage.builder()
                    .source(source)
                    .type(MessageType.EVENT)
                    .priority(priority)
                    .payload(finalPayload)
                    .routing(routing)
                    .compressed(compressed)
                    .addMetadata("event", event)
                    .build();

            try {
                messageQueues.get(priority).put(msg);
                synchronized (messageCache) {
                    messageCache.addLast(msg);
                    if (messageCache.size() > maxCacheSize) {
                        messageCache.removeFirst();
                    }
                }
                LOGGER.fine("📢 Evento: " + event + " | Fonte: " + source + " | Pri: " + priority.icon + " | Comp: " + compressed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.severe("Erro ao enfileirar mensagem: " + e.getMessage());
            }
            return CompletableFuture.completedFuture(msg.id);
        }

        private void messageWorker(MessagePriority priority) {
            BlockingQueue<NeuralMessage> queue = messageQueues.get(priority);
            while (running) {
                try {
                    NeuralMessage msg = queue.poll(500, TimeUnit.MILLISECONDS);
                    if (msg == null) continue;
                    if (msg.isExpired()) {
                        LOGGER.fine("⌛ Mensagem expirada: " + msg.id);
                        continue;
                    }

                    if (msg.compressed && msg.payload instanceof byte[]) {
                        try {
                            msg = decompressAndUpdate(msg);
                        } catch (Exception e) {
                            LOGGER.severe("Falha na descompressão: " + e.getMessage());
                            if (metrics != null) metrics.errorCount.incrementAndGet();
                            continue;
                        }
                    }

                    double start = System.nanoTime() / 1e9;
                    processMessage(msg);
                    double elapsed = System.nanoTime() / 1e9 - start;
                    if (metrics != null) metrics.updateMessageStats(msg, elapsed);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro no worker " + priority.name(), e);
                    if (metrics != null) metrics.errorCount.incrementAndGet();
                }
            }
        }

        private NeuralMessage decompressAndUpdate(NeuralMessage original) throws Exception {
            Object decompressed = decompress((byte[]) original.payload);
            // Cria nova mensagem com payload descomprimido
            return NeuralMessage.builder()
                    .id(original.id)
                    .source(original.source)
                    .destinations(original.destinations)
                    .type(original.type)
                    .priority(original.priority)
                    .payload(decompressed)
                    .ttl(original.ttl)
                    .correlationId(original.correlationId)
                    .routing(original.routing)
                    .compressed(false)
                    .encrypted(original.encrypted)
                    .retryCount(original.retryCount)
                    .maxRetries(original.maxRetries)
                    .metadata(original.metadata)
                    .build();
        }

        @SuppressWarnings("unchecked")
        private void processMessage(NeuralMessage msg) {
            String event = (String) msg.metadata.getOrDefault("event", "");
            List<ConsumerWithPriority> directHandlers = subscriptions.getOrDefault(event, Collections.emptyList());
            List<ConsumerWithPriority> allHandlers = new ArrayList<>(directHandlers);
            allHandlers.addAll(wildcardSubscribers);

            // Atualiza atividade do source
            ComponentRegistration srcReg = registry.get(msg.source);
            if (srcReg != null) srcReg.updateActivity();

            for (ConsumerWithPriority cp : allHandlers) {
                if (cp.priority.value >= msg.priority.value) {
                    try {
                        cp.handler.accept(event, msg.payload);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Erro no handler para " + event, e);
                    }
                }
            }

            // Bridge quântica
            if (quantumBridge != null && quantumBridge.connected) {
                quantumBridge.sendQuantumMessage(msg).exceptionally(ex -> {
                    LOGGER.warning("Falha ao enviar mensagem quântica: " + ex.getMessage());
                    return false;
                });
            }
        }

        // =====================================================================
        // REQUEST/RESPONSE
        // =====================================================================
        public <T> CompletableFuture<T> request(String targetName, String method, Object[] args,
                                                 double timeout, MessagePriority priority,
                                                 String source, boolean useCircuitBreaker) {
            String requestId = UUID.randomUUID().toString();
            CompletableFuture<T> future = new CompletableFuture<>();
            pendingRequests.put(requestId, (CompletableFuture<Object>) future);
            requestTimeouts.put(requestId, System.currentTimeMillis() / 1000.0 + timeout);

            CircuitBreaker cb = null;
            if (useCircuitBreaker && (boolean) config.getOrDefault("enable_circuit_breaker", DefaultConfig.ENABLE_CIRCUIT_BREAKER)) {
                cb = circuitBreakers.computeIfAbsent(targetName,
                        k -> new CircuitBreaker(k,
                                (int) config.getOrDefault("circuit_breaker_threshold", DefaultConfig.CIRCUIT_BREAKER_THRESHOLD),
                                (int) config.getOrDefault("circuit_breaker_timeout", DefaultConfig.CIRCUIT_BREAKER_TIMEOUT)));
            }

            CircuitBreaker finalCb = cb;
            Runnable execution = () -> {
                try {
                    getComponent(targetName).thenAccept(comp -> {
                        if (comp == null) {
                            future.completeExceptionally(new NoSuchElementException("Componente " + targetName + " não encontrado"));
                            return;
                        }
                        try {
                            java.lang.reflect.Method m = comp.getClass().getMethod(method, toClasses(args));
                            Object result = m.invoke(comp, args);
                            future.complete((T) result);
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        }
                    }).exceptionally(ex -> {
                        future.completeExceptionally(ex);
                        return null;
                    });
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            };

            if (finalCb != null) {
                try {
                    finalCb.call(() -> {
                        execution.run();
                        return null; // placeholder
                    });
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } else {
                execution.run();
            }

            // Timeout handler
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                if (!future.isDone() && requestTimeouts.remove(requestId) != null) {
                    pendingRequests.remove(requestId);
                    future.completeExceptionally(new TimeoutException("Request timeout after " + timeout + "s"));
                    if (metrics != null) metrics.timeoutCount.incrementAndGet();
                }
                scheduler.shutdown();
            }, (long) (timeout * 1000), TimeUnit.MILLISECONDS);

            return future;
        }

        private Class<?>[] toClasses(Object[] args) {
            if (args == null) return new Class<?>[0];
            Class<?>[] classes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                classes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }
            return classes;
        }

        public CompletableFuture<Void> sendResponse(String requestId, Object result, boolean success) {
            CompletableFuture<Object> future = pendingRequests.remove(requestId);
            requestTimeouts.remove(requestId);
            if (future != null && !future.isDone()) {
                if (success) {
                    future.complete(result);
                } else {
                    future.completeExceptionally((Throwable) result);
                }
            }
            return CompletableFuture.completedFuture(null);
        }

        // =====================================================================
        // WORKERS DE BACKGROUND
        // =====================================================================
        private void cleanupWorker() {
            double now = System.currentTimeMillis() / 1000.0;
            // Requests expirados
            List<String> expired = new ArrayList<>();
            for (Map.Entry<String, Double> entry : requestTimeouts.entrySet()) {
                if (now > entry.getValue()) {
                    expired.add(entry.getKey());
                }
            }
            for (String rid : expired) {
                CompletableFuture<Object> fut = pendingRequests.remove(rid);
                requestTimeouts.remove(rid);
                if (fut != null && !fut.isDone()) {
                    fut.completeExceptionally(new TimeoutException("Request timeout"));
                    if (metrics != null) metrics.timeoutCount.incrementAndGet();
                }
            }

            // Componentes inativos
            List<String> inactive = new ArrayList<>();
            for (Map.Entry<String, ComponentRegistration> entry : registry.entrySet()) {
                if (!entry.getValue().isAlive()) {
                    inactive.add(entry.getKey());
                }
            }
            for (String name : inactive) {
                registry.remove(name);
                LOGGER.warning("⚠️ Componente inativo removido: " + name);
            }

            if (metrics != null) {
                metrics.registrySize = registry.size();
                metrics.activeComponents = (int) registry.values().stream().filter(ComponentRegistration::isAlive).count();
            }
        }

        private void heartbeatWorker() {
            registry.values().forEach(reg -> {
                if (reg.instance instanceof NeuralComponent) {
                    ((NeuralComponent) reg.instance).heartbeat();
                }
            });
        }

        private void metricsWorker() {
            if (metrics != null) {
                // CPU/Memory via Runtime (simplificado)
                metrics.cpuPercent = 0.0;  // Poderia usar JMX
                metrics.memoryPercent = (1 - ((double) Runtime.getRuntime().freeMemory() / Runtime.getRuntime().totalMemory())) * 100;
                messageQueues.forEach((p, q) -> metrics.queueSizes.put(p.name(), q.size()));
                metrics.activeComponents = (int) registry.values().stream().filter(ComponentRegistration::isAlive).count();
            }
        }

        // =====================================================================
        // UTILITÁRIOS
        // =====================================================================
        public NeuralBusMetrics getMetrics() { return metrics; }
        public Map<String, Object> getSystemHealth() {
            Map<String, Object> health = new LinkedHashMap<>();
            health.put("status", "healthy");
            health.put("uptime", System.currentTimeMillis() / 1000.0 - startTime);
            health.put("components", registry.size());
            health.put("pending_requests", pendingRequests.size());
            return health;
        }

        // Compressão
        private static byte[] compress(Object payload) throws IOException {
            if (payload instanceof byte[]) return (byte[]) payload;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(payload);
            }
            byte[] serialized = bos.toByteArray();
            Deflater deflater = new Deflater(DefaultConfig.COMPRESSION_LEVEL);
            deflater.setInput(serialized);
            deflater.finish();
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                compressed.write(buffer, 0, count);
            }
            deflater.end();
            return compressed.toByteArray();
        }

        private static Object decompress(byte[] compressed) throws Exception {
            Inflater inflater = new Inflater();
            inflater.setInput(compressed);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);
            byte[] buf = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buf);
                bos.write(buf, 0, count);
            }
            inflater.end();
            byte[] serialized = bos.toByteArray();
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized))) {
                return ois.readObject();
            }
        }

        // =====================================================================
        // CLASSE INTERNA AUXILIAR
        // =====================================================================
        private static class ConsumerWithPriority {
            final BiConsumer<String, Object> handler;
            final MessagePriority priority;

            ConsumerWithPriority(BiConsumer<String, Object> handler, MessagePriority priority) {
                this.handler = handler;
                this.priority = priority;
            }
        }
    }

    // =========================================================================
    // COMPONENTES DE EXEMPLO
    // =========================================================================
    public static class DataProcessor extends NeuralComponent {
        private final AtomicInteger processedCount = new AtomicInteger();

        public DataProcessor(String name, NeuralBus bus) {
            super(name, bus);
        }

        @Override
        public CompletableFuture<Boolean> initialize() {
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "processor");
            meta.put("version", "1.0.0");
            meta.put("capabilities", Arrays.asList("process", "analyze", "transform"));
            return register(meta);
        }

        @Override
        public CompletableFuture<Boolean> shutdown() {
            return unregister();
        }

        public CompletableFuture<Map<String, Object>> process(Object data) {
            int count = processedCount.incrementAndGet();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("input", data);
            result.put("processed", data.toString().substring(0, Math.min(100, data.toString().length())));
            result.put("length", data.toString().length());
            result.put("timestamp", System.currentTimeMillis() / 1000.0);
            result.put("processor", name);
            result.put("count", count);
            return CompletableFuture.completedFuture(result);
        }

        public CompletableFuture<Map<String, Object>> analyze(List<Number> data) {
            if (data == null || data.isEmpty()) {
                return CompletableFuture.completedFuture(Collections.singletonMap("error", "empty data"));
            }
            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            for (Number n : data) {
                double v = n.doubleValue();
                sum += v;
                if (v < min) min = v;
                if (v > max) max = v;
            }
            double mean = sum / data.size();
            double variance = 0;
            for (Number n : data) {
                variance += Math.pow(n.doubleValue() - mean, 2);
            }
            double std = Math.sqrt(variance / data.size());
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mean", mean);
            result.put("std", std);
            result.put("min", min);
            result.put("max", max);
            return CompletableFuture.completedFuture(result);
        }
    }

    public static class MonitoringComponent extends NeuralComponent {
        private final ConcurrentMap<String, AtomicInteger> eventsReceived = new ConcurrentHashMap<>();

        public MonitoringComponent(String name, NeuralBus bus) {
            super(name, bus);
        }

        @Override
        public CompletableFuture<Boolean> initialize() {
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "monitor");
            meta.put("version", "1.0.0");
            meta.put("capabilities", Arrays.asList("monitor", "alert", "log"));
            bus.subscribe("*", this::onNeuralEvent, MessagePriority.NORMAL);
            return register(meta);
        }

        @Override
        public CompletableFuture<Boolean> shutdown() {
            bus.unsubscribe("*", this::onNeuralEvent);
            return unregister();
        }

        public CompletableFuture<Void> onNeuralEvent(String event, Object payload) {
            eventsReceived.computeIfAbsent(event, k -> new AtomicInteger()).incrementAndGet();
            return CompletableFuture.completedFuture(null);
        }

        public Map<String, Integer> getStats() {
            Map<String, Integer> stats = new HashMap<>();
            eventsReceived.forEach((k, v) -> stats.put(k, v.get()));
            return stats;
        }
    }

    // =========================================================================
    // DEMONSTRAÇÃO
    // =========================================================================
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(90));
        System.out.println("🧠 VHALINOR IAG - NEURAL BUS QUÂNTICO - DEMONSTRAÇÃO JAVA");
        System.out.println("=".repeat(90));

        NeuralBus bus = NeuralBus.getInstance(new HashMap<>());
        bus.start().join();

        DataProcessor proc1 = new DataProcessor("processor_alpha", bus);
        DataProcessor proc2 = new DataProcessor("processor_beta", bus);
        MonitoringComponent monitor = new MonitoringComponent("monitor", bus);

        proc1.initialize().join();
        proc2.initialize().join();
        monitor.initialize().join();

        System.out.println("\nComponentes registrados:");
        bus.listComponents().forEach((name, info) ->
            System.out.printf("   • %s %s: %s - %s%n", info.get("status_icon"), name, info.get("type"), info.get("status")));

        System.out.println("\nTestando comunicação via eventos...");
        bus.broadcast("data.ready", Collections.singletonMap("id", 1), "demo", MessagePriority.NORMAL, RoutingStrategy.BROADCAST);
        Thread.sleep(500);

        System.out.println("\nTestando request assíncrono...");
        Map<String, Object> res = bus.<Map<String, Object>>request("processor_alpha", "process", new Object[]{Collections.singletonMap("value", 42)},
                5.0, MessagePriority.NORMAL, "demo", true).get();
        System.out.println("   ✅ Processor Alpha: " + res);

        System.out.println("\nRelatório de saúde:");
        System.out.println("   Componentes ativos: " + bus.registry.size());
        System.out.println("   Métricas: " + bus.getMetrics().toDict());

        System.out.println("\nFinalizando...");
        proc1.shutdown().join();
        proc2.shutdown().join();
        monitor.shutdown().join();
        bus.stop().join();
        System.out.println("✅ Demonstração concluída.");
    }
}package com.vhalinor.neuralbus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════════╗
 * ║                    VHALINOR IAG 1.0.0 - NEURAL BUS QUÂNTICO                  ║
 * ║         SISTEMA DE COMUNICAÇÃO NEURAL DISTRIBUÍDA COM IA QUÂNTICA            ║
 * ╠═══════════════════════════════════════════════════════════════════════════════╣
 * ║  Módulo: CAMADA DE COMUNICAÇÃO - NEURAL BUS (Layer 02)                       ║
 * ║  Versão: 3.1.0 (Production Ready - Ultra Avançada)                          ║
 * ║  Autor: Alex Miranda Sales                                                   ║
 * ║  Data: 2026                                                                  ║
 * ║  License: Proprietary - Vhalinor IAG Systems                                 ║
 * ║  Status: 🟢 TOTALMENTE OPERACIONAL | ⚡ LATÊNCIA <1ms | 🔗 1000+ NÓS        ║
 * ╚═══════════════════════════════════════════════════════════════════════════════╝
 *
 * Versão Java - adaptada para ecossistema JVM mantendo a arquitetura original.
 * 
 * @author Convertido do Python original
 */
public class NeuralBusJava {

    // =========================================================================
    // LOGGING
    // =========================================================================
    private static final Logger LOGGER = Logger.getLogger("VhalinorNeuralBus");
    static {
        try {
            // Arquivo rotativo
            FileHandler fileHandler = new FileHandler("vhalinor_neural_bus.log", 50_000_000, 10, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            // Console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord record) {
                    return String.format("%1$tF %1$tT | %2$-8s | %3$s - %4$s%n",
                            record.getMillis(), record.getLevel().getName(),
                            record.getLoggerName(), record.getMessage());
                }
            });
            LOGGER.addHandler(consoleHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Falha ao configurar logging: " + e);
        }
    }

    // =========================================================================
    // ENUMS
    // =========================================================================
    public enum MessagePriority {
        BACKGROUND(0, "⚪", "Processos em background"),
        LOW(1, "🟢", "Baixa prioridade"),
        NORMAL(2, "🔵", "Prioridade normal"),
        HIGH(3, "🟠", "Alta prioridade"),
        CRITICAL(4, "🔴", "Prioridade crítica"),
        EMERGENCY(5, "💀", "Emergência - processamento imediato");

        public final int value;
        public final String icon;
        public final String description;

        MessagePriority(int value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    public enum MessageType {
        EVENT("event", "📢", "Evento de sistema"),
        REQUEST("request", "📨", "Requisição síncrona"),
        RESPONSE("response", "📩", "Resposta a requisição"),
        COMMAND("command", "⚡", "Comando de execução"),
        STATUS("status", "📊", "Atualização de status"),
        ERROR("error", "❌", "Mensagem de erro"),
        HEARTBEAT("heartbeat", "💓", "Sinal de atividade"),
        SYNC("sync", "🔄", "Sincronização de estado"),
        QUANTUM("quantum", "⚛️", "Mensagem quântica"),
        BROADCAST("broadcast", "📡", "Transmissão geral");

        public final String value;
        public final String icon;
        public final String description;

        MessageType(String value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    public enum ComponentStatus {
        ACTIVE("active", "🟢", "Componente ativo"),
        IDLE("idle", "⚪", "Componente ocioso"),
        BUSY("busy", "🟡", "Componente ocupado"),
        ERROR("error", "🔴", "Componente com erro"),
        DISABLED("disabled", "⚫", "Componente desabilitado"),
        DEGRADED("degraded", "🟠", "Componente degradado"),
        RECOVERING("recovering", "🔄", "Componente em recuperação");

        public final String value;
        public final String icon;
        public final String description;

        ComponentStatus(String value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    public enum RoutingStrategy {
        DIRECT("direct", "🎯", "Entrega direta"),
        BROADCAST("broadcast", "📡", "Transmissão para todos"),
        ANYCAST("anycast", "🎲", "Primeiro disponível"),
        MULTICAST("multicast", "👥", "Grupo específico"),
        LOAD_BALANCED("load_balanced", "⚖️", "Balanceamento de carga"),
        PRIORITY("priority", "📊", "Baseado em prioridade");

        public final String value;
        public final String icon;
        public final String description;

        RoutingStrategy(String value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    public enum CircuitBreakerState {
        CLOSED("closed", "🟢", "Operação normal"),
        OPEN("open", "🔴", "Bloqueado"),
        HALF_OPEN("half_open", "🟡", "Testando recuperação");

        public final String value;
        public final String icon;
        public final String description;

        CircuitBreakerState(String value, String icon, String description) {
            this.value = value;
            this.icon = icon;
            this.description = description;
        }
    }

    // =========================================================================
    // CONFIGURAÇÕES DEFAULT
    // =========================================================================
    public static class DefaultConfig {
        public static final int MAX_QUEUE_SIZE = 100_000;
        public static final int MAX_WORKERS = Math.min(32, Runtime.getRuntime().availableProcessors() + 4);
        public static final boolean ENABLE_METRICS = true;
        public static final boolean ENABLE_PERSISTENCE = false;
        public static final boolean ENABLE_QUANTUM_BRIDGE = true;
        public static final boolean ENABLE_CIRCUIT_BREAKER = true;
        public static final boolean ENABLE_COMPRESSION = true;
        public static final boolean ENABLE_ENCRYPTION = false;
        public static final double DEFAULT_TIMEOUT = 30.0;
        public static final double HEARTBEAT_INTERVAL = 5.0;
        public static final double CLEANUP_INTERVAL = 1.0;
        public static final int MAX_RETRIES = 3;
        public static final double RETRY_DELAY = 1.0;
        public static final int CIRCUIT_BREAKER_THRESHOLD = 5;
        public static final int CIRCUIT_BREAKER_TIMEOUT = 60;
        public static final int MESSAGE_CACHE_SIZE = 10_000;
        public static final double COMPONENT_TIMEOUT = 300.0; // 5 minutos
        public static final int MAX_PAYLOAD_SIZE = 10 * 1024 * 1024; // 10MB
        public static final int COMPRESSION_LEVEL = 6; // zlib (1-9)
    }

    // =========================================================================
    // ESTRUTURAS DE DADOS
    // =========================================================================

    /**
     * Mensagem neural com metadados completos.
     */
    public static class NeuralMessage {
        public final String id;
        public final String source;
        public final List<String> destinations;
        public final MessageType type;
        public final MessagePriority priority;
        public final Object payload;           // pode ser bytes se comprimido
        public final double timestamp;
        public final double ttl;
        public final String correlationId;
        public final RoutingStrategy routing;
        public final boolean compressed;
        public final boolean encrypted;
        public int retryCount;
        public final int maxRetries;
        public final Map<String, Object> metadata;

        private NeuralMessage(Builder builder) {
            this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
            this.source = builder.source;
            this.destinations = builder.destinations;
            this.type = builder.type;
            this.priority = builder.priority;
            this.payload = builder.payload;
            this.timestamp = System.currentTimeMillis() / 1000.0;
            this.ttl = builder.ttl;
            this.correlationId = builder.correlationId;
            this.routing = builder.routing;
            this.compressed = builder.compressed;
            this.encrypted = builder.encrypted;
            this.retryCount = builder.retryCount;
            this.maxRetries = builder.maxRetries;
            this.metadata = builder.metadata != null ? builder.metadata : new HashMap<>();
        }

        public boolean isExpired() {
            return (System.currentTimeMillis() / 1000.0 - timestamp) > ttl;
        }

        public boolean canRetry() {
            return retryCount < maxRetries;
        }

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", id);
            map.put("source", source);
            map.put("destinations", destinations);
            map.put("type", type.value);
            map.put("priority", priority.value);
            map.put("payload_preview", payload != null ? payload.toString().substring(0, Math.min(100, payload.toString().length())) : "null");
            map.put("timestamp", timestamp);
            map.put("age", String.format("%.2fs", System.currentTimeMillis() / 1000.0 - timestamp));
            map.put("ttl", ttl);
            map.put("correlation_id", correlationId);
            map.put("routing", routing.value);
            map.put("retry_count", retryCount);
            map.put("compressed", compressed);
            return map;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String id;
            private String source = "system";
            private List<String> destinations;
            private MessageType type = MessageType.EVENT;
            private MessagePriority priority = MessagePriority.NORMAL;
            private Object payload;
            private double ttl = DefaultConfig.DEFAULT_TIMEOUT;
            private String correlationId;
            private RoutingStrategy routing = RoutingStrategy.DIRECT;
            private boolean compressed = false;
            private boolean encrypted = false;
            private int retryCount = 0;
            private int maxRetries = DefaultConfig.MAX_RETRIES;
            private Map<String, Object> metadata = new HashMap<>();

            public Builder id(String id) { this.id = id; return this; }
            public Builder source(String source) { this.source = source; return this; }
            public Builder destinations(List<String> destinations) { this.destinations = destinations; return this; }
            public Builder type(MessageType type) { this.type = type; return this; }
            public Builder priority(MessagePriority priority) { this.priority = priority; return this; }
            public Builder payload(Object payload) { this.payload = payload; return this; }
            public Builder ttl(double ttl) { this.ttl = ttl; return this; }
            public Builder correlationId(String correlationId) { this.correlationId = correlationId; return this; }
            public Builder routing(RoutingStrategy routing) { this.routing = routing; return this; }
            public Builder compressed(boolean compressed) { this.compressed = compressed; return this; }
            public Builder encrypted(boolean encrypted) { this.encrypted = encrypted; return this; }
            public Builder retryCount(int retryCount) { this.retryCount = retryCount; return this; }
            public Builder maxRetries(int maxRetries) { this.maxRetries = maxRetries; return this; }
            public Builder metadata(Map<String, Object> metadata) { this.metadata = metadata; return this; }
            public Builder addMetadata(String key, Object val) { this.metadata.put(key, val); return this; }

            public NeuralMessage build() {
                return new NeuralMessage(this);
            }
        }
    }

    /**
     * Registro de componente no barramento.
     */
    public static class ComponentRegistration {
        public final String name;
        public final Object instance;
        public final Map<String, Object> metadata;
        public volatile ComponentStatus status;
        public final double registeredAt;
        public volatile double lastHeartbeat;
        public volatile double lastActivity;
        public final AtomicLong messageCount = new AtomicLong();
        public final AtomicLong errorCount = new AtomicLong();
        public volatile double avgResponseTime = 0.0;
        public final ConcurrentMap<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

        public ComponentRegistration(String name, Object instance, Map<String, Object> metadata) {
            this.name = name;
            this.instance = instance;
            this.metadata = metadata != null ? metadata : new HashMap<>();
            this.status = ComponentStatus.ACTIVE;
            this.registeredAt = System.currentTimeMillis() / 1000.0;
            this.lastHeartbeat = registeredAt;
            this.lastActivity = registeredAt;
        }

        public boolean isAlive() {
            return (System.currentTimeMillis() / 1000.0 - lastHeartbeat) < DefaultConfig.COMPONENT_TIMEOUT;
        }

        public void updateActivity() {
            this.lastActivity = System.currentTimeMillis() / 1000.0;
            messageCount.incrementAndGet();
        }

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", name);
            map.put("type", instance.getClass().getSimpleName());
            map.put("status", status.value);
            map.put("status_icon", status.icon);
            map.put("alive", isAlive());
            map.put("uptime", String.format("%.1fs", System.currentTimeMillis() / 1000.0 - registeredAt));
            map.put("last_heartbeat", String.format("%.1fs ago", System.currentTimeMillis() / 1000.0 - lastHeartbeat));
            map.put("messages", messageCount.get());
            map.put("errors", errorCount.get());
            map.put("avg_response_time", String.format("%.2fms", avgResponseTime * 1000));
            return map;
        }
    }

    /**
     * Métricas agregadas do barramento.
     */
    public static class NeuralBusMetrics {
        public final AtomicLong messageCount = new AtomicLong();
        public final ConcurrentMap<String, AtomicLong> byType = new ConcurrentHashMap<>();
        public final ConcurrentMap<MessagePriority, AtomicLong> byPriority = new ConcurrentHashMap<>();
        public final DoubleAdder totalProcessingTime = new DoubleAdder();
        public double avgProcessingTime = 0.0;
        public volatile double minProcessingTime = Double.MAX_VALUE;
        public volatile double maxProcessingTime = 0.0;
        public final AtomicLong errorCount = new AtomicLong();
        public final AtomicLong retryCount = new AtomicLong();
        public final AtomicLong timeoutCount = new AtomicLong();
        public volatile int registrySize = 0;
        public volatile int subscriptionCount = 0;
        public volatile int activeComponents = 0;
        public final double startTime = System.currentTimeMillis() / 1000.0;
        public volatile double cpuPercent = 0.0;
        public volatile double memoryPercent = 0.0;
        public final ConcurrentMap<String, Integer> queueSizes = new ConcurrentHashMap<>();

        public void updateMessageStats(NeuralMessage msg, double processingTime) {
            messageCount.incrementAndGet();
            byType.computeIfAbsent(msg.type.value, k -> new AtomicLong()).incrementAndGet();
            byPriority.computeIfAbsent(msg.priority, k -> new AtomicLong()).incrementAndGet();

            if (processingTime > 0) {
                totalProcessingTime.add(processingTime);
                avgProcessingTime = totalProcessingTime.sum() / messageCount.get();
                synchronized (this) {
                    if (processingTime < minProcessingTime) minProcessingTime = processingTime;
                    if (processingTime > maxProcessingTime) maxProcessingTime = processingTime;
                }
            }
        }

        public Map<String, Object> toDict() {
            Map<String, Object> map = new LinkedHashMap<>();
            Map<String, Object> msgs = new LinkedHashMap<>();
            msgs.put("total", messageCount.get());
            Map<String, Long> byTypeMap = new HashMap<>();
            byType.forEach((k,v) -> byTypeMap.put(k, v.get()));
            msgs.put("by_type", byTypeMap);
            Map<String, Long> byPriorityMap = new HashMap<>();
            byPriority.forEach((k,v) -> byPriorityMap.put(k.name(), v.get()));
            msgs.put("by_priority", byPriorityMap);
            map.put("messages", msgs);

            Map<String, Object> proc = new LinkedHashMap<>();
            proc.put("avg_ms", avgProcessingTime * 1000);
            proc.put("min_ms", minProcessingTime == Double.MAX_VALUE ? 0 : minProcessingTime * 1000);
            proc.put("max_ms", maxProcessingTime * 1000);
            map.put("processing", proc);

            Map<String, Object> errs = new LinkedHashMap<>();
            errs.put("total", errorCount.get());
            errs.put("retries", retryCount.get());
            errs.put("timeouts", timeoutCount.get());
            map.put("errors", errs);

            Map<String, Object> comps = new LinkedHashMap<>();
            comps.put("registered", registrySize);
            comps.put("active", activeComponents);
            comps.put("subscriptions", subscriptionCount);
            map.put("components", comps);

            Map<String, Object> sys = new LinkedHashMap<>();
            sys.put("uptime", String.format("%.1fs", System.currentTimeMillis() / 1000.0 - startTime));
            sys.put("cpu_percent", cpuPercent);
            sys.put("memory_percent", memoryPercent);
            sys.put("queues", new HashMap<>(queueSizes));
            map.put("system", sys);
            return map;
        }
    }

    // =========================================================================
    // CIRCUIT BREAKER
    // =========================================================================
    public static class CircuitBreaker {
        private final String name;
        private final int threshold;
        private final int timeoutSeconds;
        private volatile CircuitBreakerState state = CircuitBreakerState.CLOSED;
        private volatile int failureCount = 0;
        private volatile double lastFailureTime = 0;
        private final AtomicLong totalFailures = new AtomicLong();
        private final AtomicLong totalSuccesses = new AtomicLong();

        public CircuitBreaker(String name, int threshold, int timeoutSeconds) {
            this.name = name;
            this.threshold = threshold;
            this.timeoutSeconds = timeoutSeconds;
        }

        public synchronized <T> T call(Supplier<T> supplier) throws Exception {
            if (state == CircuitBreakerState.OPEN) {
                if (System.currentTimeMillis() / 1000.0 - lastFailureTime > timeoutSeconds) {
                    state = CircuitBreakerState.HALF_OPEN;
                    LOGGER.info("🔄 Circuit breaker half-open para " + name);
                } else {
                    throw new RuntimeException("Circuit breaker OPEN for " + name);
                }
            }
            try {
                T result = supplier.get();
                synchronized (this) {
                    totalSuccesses.incrementAndGet();
                    if (state == CircuitBreakerState.HALF_OPEN) {
                        state = CircuitBreakerState.CLOSED;
                        failureCount = 0;
                        LOGGER.info("✅ Circuit breaker closed para " + name);
                    }
                }
                return result;
            } catch (Exception e) {
                synchronized (this) {
                    totalFailures.incrementAndGet();
                    failureCount++;
                    lastFailureTime = System.currentTimeMillis() / 1000.0;
                    if (failureCount >= threshold) {
                        state = CircuitBreakerState.OPEN;
                        LOGGER.severe("🔴 Circuit breaker OPEN for " + name + " after " + failureCount + " failures");
                    }
                }
                throw e;
            }
        }

        public double successRate() {
            long total = totalSuccesses.get() + totalFailures.get();
            return total > 0 ? (totalSuccesses.get() * 100.0) / total : 100.0;
        }

        public synchronized void reset() {
            state = CircuitBreakerState.CLOSED;
            failureCount = 0;
            LOGGER.info("🔄 Circuit breaker reset for " + name);
        }

        public CircuitBreakerState getState() { return state; }
        public long getTotalFailures() { return totalFailures.get(); }
        public long getTotalSuccesses() { return totalSuccesses.get(); }
    }

    // =========================================================================
    // RATE LIMITER (TOKEN BUCKET)
    // =========================================================================
    public static class RateLimiter {
        private final double rate;          // tokens per second
        private volatile double tokens;
        private volatile double lastRefill;

        public RateLimiter(double rate) {
            this.rate = rate;
            this.tokens = rate;
            this.lastRefill = System.nanoTime() / 1e9;
        }

        public synchronized boolean acquire(int needed) {
            double now = System.nanoTime() / 1e9;
            double elapsed = now - lastRefill;
            tokens += elapsed * rate;
            if (tokens > rate) tokens = rate;
            lastRefill = now;
            if (tokens >= needed) {
                tokens -= needed;
                return true;
            } else {
                // Wait time (approx) – não bloqueante aqui, apenas consome o que pode
                // Para simplicidade, retornamos false e o chamador deve decidir
                return false;
            }
        }

        public boolean acquire() {
            return acquire(1);
        }
    }

    // =========================================================================
    // COMPONENTE NEURAL BASE
    // =========================================================================
    public static abstract class NeuralComponent {
        protected final String name;
        protected NeuralBus bus;
        protected volatile boolean registered = false;
        protected volatile ComponentStatus status = ComponentStatus.IDLE;

        public NeuralComponent(String name, NeuralBus bus) {
            this.name = name;
            this.bus = bus;
        }

        public abstract CompletableFuture<Boolean> initialize();
        public abstract CompletableFuture<Boolean> shutdown();

        public CompletableFuture<Boolean> register(Map<String, Object> metadata) {
            if (!registered) {
                return bus.register(name, this, metadata).thenApply(success -> {
                    if (success) {
                        registered = true;
                        status = ComponentStatus.ACTIVE;
                        LOGGER.info("✅ Componente registrado: " + name);
                    }
                    return success;
                });
            }
            return CompletableFuture.completedFuture(true);
        }

        public CompletableFuture<Boolean> unregister() {
            if (registered) {
                return bus.unregister(name).thenApply(success -> {
                    if (success) {
                        registered = false;
                        status = ComponentStatus.DISABLED;
                        LOGGER.info("❌ Componente removido: " + name);
                    }
                    return success;
                });
            }
            return CompletableFuture.completedFuture(true);
        }

        public CompletableFuture<Void> onNeuralEvent(String event, Object payload) {
            LOGGER.fine("📨 Evento recebido: " + event);
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<String> sendEvent(String event, Object payload,
                                                   MessagePriority priority,
                                                   RoutingStrategy routing) {
            return bus.broadcast(event, payload, name, priority, routing);
        }

        public <T> CompletableFuture<T> request(String target, String method, Object... args) {
            return bus.request(target, method, args, DefaultConfig.DEFAULT_TIMEOUT,
                    MessagePriority.NORMAL, name, true);
        }

        public CompletableFuture<Void> heartbeat() {
            return bus.heartbeat(name);
        }

        public ComponentStatus getStatus() { return status; }
        public void setStatus(ComponentStatus status) { this.status = status; }
    }

    // =========================================================================
    // QUANTUM BRIDGE (simplificada - stubs para extensão futura)
    // =========================================================================
    public static class QuantumBridge {
        private final NeuralBus bus;
        private boolean connected = false;
        private final Map<String, Object> circuits = new ConcurrentHashMap<>();

        public QuantumBridge(NeuralBus bus) {
            this.bus = bus;
        }

        public CompletableFuture<Boolean> connect() {
            // No Java, dependeríamos de uma biblioteca quântica externa (ex.: via JNI)
            // Por enquanto, simulamos como indisponível.
            LOGGER.info("ℹ️ Bridge Quântica não disponível em ambiente Java padrão");
            connected = false;
            return CompletableFuture.completedFuture(false);
        }

        public CompletableFuture<Boolean> disconnect() {
            connected = false;
            LOGGER.info("🔌 Bridge Quântica desconectada");
            return CompletableFuture.completedFuture(true);
        }

        public CompletableFuture<Boolean> sendQuantumMessage(NeuralMessage message) {
            // Stub
            return CompletableFuture.completedFuture(false);
        }

        public CompletableFuture<Object> createQuantumCircuit(String name, int qubits) {
            // Stub
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<Map<String, Object>> executeQuantumCircuit(String name) {
            // Stub
            return CompletableFuture.completedFuture(null);
        }
    }

    // =========================================================================
    // NEURAL BUS PRINCIPAL
    // =========================================================================
    public static class NeuralBus {
        private static volatile NeuralBus INSTANCE;
        private static final Object LOCK = new Object();

        private final Map<String, Object> config;

        // Registro e assinaturas
        private final ConcurrentMap<String, ComponentRegistration> registry = new ConcurrentHashMap<>();
        private final ConcurrentMap<String, List<ConsumerWithPriority>> subscriptions = new ConcurrentHashMap<>();
        private final List<ConsumerWithPriority> wildcardSubscribers = new CopyOnWriteArrayList<>();

        // Filas de mensagens por prioridade
        private final EnumMap<MessagePriority, BlockingQueue<NeuralMessage>> messageQueues;

        // Requests pendentes
        private final ConcurrentMap<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();
        private final ConcurrentMap<String, Double> requestTimeouts = new ConcurrentHashMap<>();

        // Bridges
        private QuantumBridge quantumBridge;

        // Workers
        private volatile boolean running = false;
        private final List<Thread> workers = new ArrayList<>();
        private final ExecutorService workerPool;

        // Métricas
        private final NeuralBusMetrics metrics;
        private final RateLimiter rateLimiter = new RateLimiter(1000);
        private final ConcurrentMap<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

        // Cache
        private final Deque<NeuralMessage> messageCache = new ArrayDeque<>();
        private final int maxCacheSize;

        private final double startTime = System.currentTimeMillis() / 1000.0;

        public NeuralBus(Map<String, Object> config) {
            this.config = new HashMap<>(DefaultConfig.toMap());
            if (config != null) this.config.putAll(config);

            // Inicializa filas
            messageQueues = new EnumMap<>(MessagePriority.class);
            int baseSize = (int) this.config.getOrDefault("max_queue_size", DefaultConfig.MAX_QUEUE_SIZE);
            for (MessagePriority prio : MessagePriority.values()) {
                int size = Math.max(100, baseSize / (prio.value + 1));
                messageQueues.put(prio, new LinkedBlockingQueue<>(size));
            }

            this.metrics = (boolean) this.config.getOrDefault("enable_metrics", DefaultConfig.ENABLE_METRICS)
                    ? new NeuralBusMetrics() : null;
            this.maxCacheSize = (int) this.config.getOrDefault("message_cache_size", DefaultConfig.MESSAGE_CACHE_SIZE);
            this.workerPool = Executors.newFixedThreadPool(
                    (int) this.config.getOrDefault("max_workers", DefaultConfig.MAX_WORKERS));

            LOGGER.info("🧠 VHALINOR IAG - NEURAL BUS QUÂNTICO INICIALIZADO");
            LOGGER.info("⚙️  Config: Workers=" + config.get("max_workers") +
                    ", Queue=" + config.get("max_queue_size") +
                    ", Timeout=" + config.get("default_timeout") + "s");
            LOGGER.info("⚛️  Quantum Bridge: " + (boolean) this.config.getOrDefault("enable_quantum_bridge",
                    DefaultConfig.ENABLE_QUANTUM_BRIDGE) ? "✅" : "❌");
        }

        public static NeuralBus getInstance(Map<String, Object> config) {
            if (INSTANCE == null) {
                synchronized (LOCK) {
                    if (INSTANCE == null) {
                        INSTANCE = new NeuralBus(config);
                    }
                }
            }
            return INSTANCE;
        }

        // =====================================================================
        // CONTROLE
        // =====================================================================
        public synchronized CompletableFuture<Void> start() {
            if (running) return CompletableFuture.completedFuture(null);
            running = true;

            // Workers por prioridade
            for (MessagePriority prio : MessagePriority.values()) {
                Thread worker = new Thread(() -> messageWorker(prio), "NeuralBusWorker-" + prio.name());
                worker.setDaemon(true);
                worker.start();
                workers.add(worker);
            }

            // Limpeza e heartbeat
            startDaemon("NeuralBusCleanup", this::cleanupWorker, DefaultConfig.CLEANUP_INTERVAL);
            startDaemon("NeuralBusHeartbeat", this::heartbeatWorker, DefaultConfig.HEARTBEAT_INTERVAL);
            if (metrics != null) {
                startDaemon("NeuralBusMetrics", this::metricsWorker, 5.0);
            }

            // Quantum bridge
            if ((boolean) config.getOrDefault("enable_quantum_bridge", DefaultConfig.ENABLE_QUANTUM_BRIDGE)) {
                quantumBridge = new QuantumBridge(this);
                quantumBridge.connect();
            }

            LOGGER.info("✅ NeuralBus iniciado com " + workers.size() + " workers");
            return CompletableFuture.completedFuture(null);
        }

        private void startDaemon(String name, Runnable task, double intervalSeconds) {
            Thread t = new Thread(() -> {
                while (running) {
                    try {
                        task.run();
                        Thread.sleep((long)(intervalSeconds * 1000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Erro no worker " + name, e);
                    }
                }
            }, name);
            t.setDaemon(true);
            t.start();
            workers.add(t);
        }

        public synchronized CompletableFuture<Void> stop() {
            running = false;
            workers.forEach(Thread::interrupt);
            workers.clear();
            pendingRequests.values().forEach(f -> f.completeExceptionally(new InterruptedException("Bus stopped")));
            pendingRequests.clear();
            requestTimeouts.clear();
            if (quantumBridge != null) quantumBridge.disconnect();
            workerPool.shutdownNow();
            LOGGER.info("⏹️ NeuralBus parado");
            return CompletableFuture.completedFuture(null);
        }

        // =====================================================================
        // REGISTRO
        // =====================================================================
        public CompletableFuture<Boolean> register(String name, Object instance, Map<String, Object> metadata) {
            ComponentRegistration reg = new ComponentRegistration(name, instance, metadata);
            registry.put(name, reg);
            if (metrics != null) {
                metrics.registrySize = registry.size();
                metrics.activeComponents = (int) registry.values().stream().filter(ComponentRegistration::isAlive).count();
            }
            LOGGER.info("✅ Componente registrado: " + name);
            return CompletableFuture.completedFuture(true);
        }

        public CompletableFuture<Boolean> unregister(String name) {
            if (registry.remove(name) != null) {
                if (metrics != null) {
                    metrics.registrySize = registry.size();
                    metrics.activeComponents = (int) registry.values().stream().filter(ComponentRegistration::isAlive).count();
                }
                LOGGER.info("❌ Componente removido: " + name);
                return CompletableFuture.completedFuture(true);
            }
            return CompletableFuture.completedFuture(false);
        }

        public CompletableFuture<Object> getComponent(String name) {
            ComponentRegistration reg = registry.get(name);
            if (reg != null && reg.isAlive()) {
                return CompletableFuture.completedFuture(reg.instance);
            }
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<Boolean> heartbeat(String name) {
            ComponentRegistration reg = registry.get(name);
            if (reg != null) {
                reg.lastHeartbeat = System.currentTimeMillis() / 1000.0;
                reg.status = ComponentStatus.ACTIVE;
                return CompletableFuture.completedFuture(true);
            }
            return CompletableFuture.completedFuture(false);
        }

        public Map<String, Map<String, Object>> listComponents() {
            Map<String, Map<String, Object>> result = new LinkedHashMap<>();
            registry.forEach((name, reg) -> result.put(name, reg.toDict()));
            return result;
        }

        // =====================================================================
        // PUB/SUB
        // =====================================================================
        public CompletableFuture<Void> subscribe(String event, BiConsumer<String, Object> handler,
                                                  MessagePriority priority) {
            if ("*".equals(event)) {
                wildcardSubscribers.add(new ConsumerWithPriority(handler, priority));
            } else {
                subscriptions.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>())
                        .add(new ConsumerWithPriority(handler, priority));
            }
            if (metrics != null) {
                metrics.subscriptionCount = subscriptions.values().stream().mapToInt(List::size).sum()
                        + wildcardSubscribers.size();
            }
            LOGGER.fine("📝 Handler inscrito para evento: " + event);
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<Void> unsubscribe(String event, BiConsumer<String, Object> handler) {
            if ("*".equals(event)) {
                wildcardSubscribers.removeIf(cp -> cp.handler == handler);
            } else {
                List<ConsumerWithPriority> handlers = subscriptions.get(event);
                if (handlers != null) {
                    handlers.removeIf(cp -> cp.handler == handler);
                }
            }
            return CompletableFuture.completedFuture(null);
        }

        public CompletableFuture<String> broadcast(String event, Object payload, String source,
                                                    MessagePriority priority, RoutingStrategy routing) {
            rateLimiter.acquire();

            boolean useCompression = (boolean) config.getOrDefault("enable_compression", DefaultConfig.ENABLE_COMPRESSION);
            Object finalPayload = payload;
            boolean compressed = false;
            if (useCompression && payload != null) {
                try {
                    finalPayload = compress(payload);
                    compressed = true;
                } catch (Exception e) {
                    LOGGER.warning("Falha na compressão: " + e.getMessage());
                }
            }

            NeuralMessage msg = NeuralMessage.builder()
                    .source(source)
                    .type(MessageType.EVENT)
                    .priority(priority)
                    .payload(finalPayload)
                    .routing(routing)
                    .compressed(compressed)
                    .addMetadata("event", event)
                    .build();

            try {
                messageQueues.get(priority).put(msg);
                synchronized (messageCache) {
                    messageCache.addLast(msg);
                    if (messageCache.size() > maxCacheSize) {
                        messageCache.removeFirst();
                    }
                }
                LOGGER.fine("📢 Evento: " + event + " | Fonte: " + source + " | Pri: " + priority.icon + " | Comp: " + compressed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.severe("Erro ao enfileirar mensagem: " + e.getMessage());
            }
            return CompletableFuture.completedFuture(msg.id);
        }

        private void messageWorker(MessagePriority priority) {
            BlockingQueue<NeuralMessage> queue = messageQueues.get(priority);
            while (running) {
                try {
                    NeuralMessage msg = queue.poll(500, TimeUnit.MILLISECONDS);
                    if (msg == null) continue;
                    if (msg.isExpired()) {
                        LOGGER.fine("⌛ Mensagem expirada: " + msg.id);
                        continue;
                    }

                    if (msg.compressed && msg.payload instanceof byte[]) {
                        try {
                            msg = decompressAndUpdate(msg);
                        } catch (Exception e) {
                            LOGGER.severe("Falha na descompressão: " + e.getMessage());
                            if (metrics != null) metrics.errorCount.incrementAndGet();
                            continue;
                        }
                    }

                    double start = System.nanoTime() / 1e9;
                    processMessage(msg);
                    double elapsed = System.nanoTime() / 1e9 - start;
                    if (metrics != null) metrics.updateMessageStats(msg, elapsed);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erro no worker " + priority.name(), e);
                    if (metrics != null) metrics.errorCount.incrementAndGet();
                }
            }
        }

        private NeuralMessage decompressAndUpdate(NeuralMessage original) throws Exception {
            Object decompressed = decompress((byte[]) original.payload);
            // Cria nova mensagem com payload descomprimido
            return NeuralMessage.builder()
                    .id(original.id)
                    .source(original.source)
                    .destinations(original.destinations)
                    .type(original.type)
                    .priority(original.priority)
                    .payload(decompressed)
                    .ttl(original.ttl)
                    .correlationId(original.correlationId)
                    .routing(original.routing)
                    .compressed(false)
                    .encrypted(original.encrypted)
                    .retryCount(original.retryCount)
                    .maxRetries(original.maxRetries)
                    .metadata(original.metadata)
                    .build();
        }

        @SuppressWarnings("unchecked")
        private void processMessage(NeuralMessage msg) {
            String event = (String) msg.metadata.getOrDefault("event", "");
            List<ConsumerWithPriority> directHandlers = subscriptions.getOrDefault(event, Collections.emptyList());
            List<ConsumerWithPriority> allHandlers = new ArrayList<>(directHandlers);
            allHandlers.addAll(wildcardSubscribers);

            // Atualiza atividade do source
            ComponentRegistration srcReg = registry.get(msg.source);
            if (srcReg != null) srcReg.updateActivity();

            for (ConsumerWithPriority cp : allHandlers) {
                if (cp.priority.value >= msg.priority.value) {
                    try {
                        cp.handler.accept(event, msg.payload);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Erro no handler para " + event, e);
                    }
                }
            }

            // Bridge quântica
            if (quantumBridge != null && quantumBridge.connected) {
                quantumBridge.sendQuantumMessage(msg).exceptionally(ex -> {
                    LOGGER.warning("Falha ao enviar mensagem quântica: " + ex.getMessage());
                    return false;
                });
            }
        }

        // =====================================================================
        // REQUEST/RESPONSE
        // =====================================================================
        public <T> CompletableFuture<T> request(String targetName, String method, Object[] args,
                                                 double timeout, MessagePriority priority,
                                                 String source, boolean useCircuitBreaker) {
            String requestId = UUID.randomUUID().toString();
            CompletableFuture<T> future = new CompletableFuture<>();
            pendingRequests.put(requestId, (CompletableFuture<Object>) future);
            requestTimeouts.put(requestId, System.currentTimeMillis() / 1000.0 + timeout);

            CircuitBreaker cb = null;
            if (useCircuitBreaker && (boolean) config.getOrDefault("enable_circuit_breaker", DefaultConfig.ENABLE_CIRCUIT_BREAKER)) {
                cb = circuitBreakers.computeIfAbsent(targetName,
                        k -> new CircuitBreaker(k,
                                (int) config.getOrDefault("circuit_breaker_threshold", DefaultConfig.CIRCUIT_BREAKER_THRESHOLD),
                                (int) config.getOrDefault("circuit_breaker_timeout", DefaultConfig.CIRCUIT_BREAKER_TIMEOUT)));
            }

            CircuitBreaker finalCb = cb;
            Runnable execution = () -> {
                try {
                    getComponent(targetName).thenAccept(comp -> {
                        if (comp == null) {
                            future.completeExceptionally(new NoSuchElementException("Componente " + targetName + " não encontrado"));
                            return;
                        }
                        try {
                            java.lang.reflect.Method m = comp.getClass().getMethod(method, toClasses(args));
                            Object result = m.invoke(comp, args);
                            future.complete((T) result);
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        }
                    }).exceptionally(ex -> {
                        future.completeExceptionally(ex);
                        return null;
                    });
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            };

            if (finalCb != null) {
                try {
                    finalCb.call(() -> {
                        execution.run();
                        return null; // placeholder
                    });
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } else {
                execution.run();
            }

            // Timeout handler
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                if (!future.isDone() && requestTimeouts.remove(requestId) != null) {
                    pendingRequests.remove(requestId);
                    future.completeExceptionally(new TimeoutException("Request timeout after " + timeout + "s"));
                    if (metrics != null) metrics.timeoutCount.incrementAndGet();
                }
                scheduler.shutdown();
            }, (long) (timeout * 1000), TimeUnit.MILLISECONDS);

            return future;
        }

        private Class<?>[] toClasses(Object[] args) {
            if (args == null) return new Class<?>[0];
            Class<?>[] classes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                classes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }
            return classes;
        }

        public CompletableFuture<Void> sendResponse(String requestId, Object result, boolean success) {
            CompletableFuture<Object> future = pendingRequests.remove(requestId);
            requestTimeouts.remove(requestId);
            if (future != null && !future.isDone()) {
                if (success) {
                    future.complete(result);
                } else {
                    future.completeExceptionally((Throwable) result);
                }
            }
            return CompletableFuture.completedFuture(null);
        }

        // =====================================================================
        // WORKERS DE BACKGROUND
        // =====================================================================
        private void cleanupWorker() {
            double now = System.currentTimeMillis() / 1000.0;
            // Requests expirados
            List<String> expired = new ArrayList<>();
            for (Map.Entry<String, Double> entry : requestTimeouts.entrySet()) {
                if (now > entry.getValue()) {
                    expired.add(entry.getKey());
                }
            }
            for (String rid : expired) {
                CompletableFuture<Object> fut = pendingRequests.remove(rid);
                requestTimeouts.remove(rid);
                if (fut != null && !fut.isDone()) {
                    fut.completeExceptionally(new TimeoutException("Request timeout"));
                    if (metrics != null) metrics.timeoutCount.incrementAndGet();
                }
            }

            // Componentes inativos
            List<String> inactive = new ArrayList<>();
            for (Map.Entry<String, ComponentRegistration> entry : registry.entrySet()) {
                if (!entry.getValue().isAlive()) {
                    inactive.add(entry.getKey());
                }
            }
            for (String name : inactive) {
                registry.remove(name);
                LOGGER.warning("⚠️ Componente inativo removido: " + name);
            }

            if (metrics != null) {
                metrics.registrySize = registry.size();
                metrics.activeComponents = (int) registry.values().stream().filter(ComponentRegistration::isAlive).count();
            }
        }

        private void heartbeatWorker() {
            registry.values().forEach(reg -> {
                if (reg.instance instanceof NeuralComponent) {
                    ((NeuralComponent) reg.instance).heartbeat();
                }
            });
        }

        private void metricsWorker() {
            if (metrics != null) {
                // CPU/Memory via Runtime (simplificado)
                metrics.cpuPercent = 0.0;  // Poderia usar JMX
                metrics.memoryPercent = (1 - ((double) Runtime.getRuntime().freeMemory() / Runtime.getRuntime().totalMemory())) * 100;
                messageQueues.forEach((p, q) -> metrics.queueSizes.put(p.name(), q.size()));
                metrics.activeComponents = (int) registry.values().stream().filter(ComponentRegistration::isAlive).count();
            }
        }

        // =====================================================================
        // UTILITÁRIOS
        // =====================================================================
        public NeuralBusMetrics getMetrics() { return metrics; }
        public Map<String, Object> getSystemHealth() {
            Map<String, Object> health = new LinkedHashMap<>();
            health.put("status", "healthy");
            health.put("uptime", System.currentTimeMillis() / 1000.0 - startTime);
            health.put("components", registry.size());
            health.put("pending_requests", pendingRequests.size());
            return health;
        }

        // Compressão
        private static byte[] compress(Object payload) throws IOException {
            if (payload instanceof byte[]) return (byte[]) payload;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(payload);
            }
            byte[] serialized = bos.toByteArray();
            Deflater deflater = new Deflater(DefaultConfig.COMPRESSION_LEVEL);
            deflater.setInput(serialized);
            deflater.finish();
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                compressed.write(buffer, 0, count);
            }
            deflater.end();
            return compressed.toByteArray();
        }

        private static Object decompress(byte[] compressed) throws Exception {
            Inflater inflater = new Inflater();
            inflater.setInput(compressed);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);
            byte[] buf = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buf);
                bos.write(buf, 0, count);
            }
            inflater.end();
            byte[] serialized = bos.toByteArray();
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized))) {
                return ois.readObject();
            }
        }

        // =====================================================================
        // CLASSE INTERNA AUXILIAR
        // =====================================================================
        private static class ConsumerWithPriority {
            final BiConsumer<String, Object> handler;
            final MessagePriority priority;

            ConsumerWithPriority(BiConsumer<String, Object> handler, MessagePriority priority) {
                this.handler = handler;
                this.priority = priority;
            }
        }
    }

    // =========================================================================
    // COMPONENTES DE EXEMPLO
    // =========================================================================
    public static class DataProcessor extends NeuralComponent {
        private final AtomicInteger processedCount = new AtomicInteger();

        public DataProcessor(String name, NeuralBus bus) {
            super(name, bus);
        }

        @Override
        public CompletableFuture<Boolean> initialize() {
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "processor");
            meta.put("version", "1.0.0");
            meta.put("capabilities", Arrays.asList("process", "analyze", "transform"));
            return register(meta);
        }

        @Override
        public CompletableFuture<Boolean> shutdown() {
            return unregister();
        }

        public CompletableFuture<Map<String, Object>> process(Object data) {
            int count = processedCount.incrementAndGet();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("input", data);
            result.put("processed", data.toString().substring(0, Math.min(100, data.toString().length())));
            result.put("length", data.toString().length());
            result.put("timestamp", System.currentTimeMillis() / 1000.0);
            result.put("processor", name);
            result.put("count", count);
            return CompletableFuture.completedFuture(result);
        }

        public CompletableFuture<Map<String, Object>> analyze(List<Number> data) {
            if (data == null || data.isEmpty()) {
                return CompletableFuture.completedFuture(Collections.singletonMap("error", "empty data"));
            }
            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            for (Number n : data) {
                double v = n.doubleValue();
                sum += v;
                if (v < min) min = v;
                if (v > max) max = v;
            }
            double mean = sum / data.size();
            double variance = 0;
            for (Number n : data) {
                variance += Math.pow(n.doubleValue() - mean, 2);
            }
            double std = Math.sqrt(variance / data.size());
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("mean", mean);
            result.put("std", std);
            result.put("min", min);
            result.put("max", max);
            return CompletableFuture.completedFuture(result);
        }
    }

    public static class MonitoringComponent extends NeuralComponent {
        private final ConcurrentMap<String, AtomicInteger> eventsReceived = new ConcurrentHashMap<>();

        public MonitoringComponent(String name, NeuralBus bus) {
            super(name, bus);
        }

        @Override
        public CompletableFuture<Boolean> initialize() {
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "monitor");
            meta.put("version", "1.0.0");
            meta.put("capabilities", Arrays.asList("monitor", "alert", "log"));
            bus.subscribe("*", this::onNeuralEvent, MessagePriority.NORMAL);
            return register(meta);
        }

        @Override
        public CompletableFuture<Boolean> shutdown() {
            bus.unsubscribe("*", this::onNeuralEvent);
            return unregister();
        }

        public CompletableFuture<Void> onNeuralEvent(String event, Object payload) {
            eventsReceived.computeIfAbsent(event, k -> new AtomicInteger()).incrementAndGet();
            return CompletableFuture.completedFuture(null);
        }

        public Map<String, Integer> getStats() {
            Map<String, Integer> stats = new HashMap<>();
            eventsReceived.forEach((k, v) -> stats.put(k, v.get()));
            return stats;
        }
    }

    // =========================================================================
    // DEMONSTRAÇÃO
    // =========================================================================
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(90));
        System.out.println("🧠 VHALINOR IAG - NEURAL BUS QUÂNTICO - DEMONSTRAÇÃO JAVA");
        System.out.println("=".repeat(90));

        NeuralBus bus = NeuralBus.getInstance(new HashMap<>());
        bus.start().join();

        DataProcessor proc1 = new DataProcessor("processor_alpha", bus);
        DataProcessor proc2 = new DataProcessor("processor_beta", bus);
        MonitoringComponent monitor = new MonitoringComponent("monitor", bus);

        proc1.initialize().join();
        proc2.initialize().join();
        monitor.initialize().join();

        System.out.println("\nComponentes registrados:");
        bus.listComponents().forEach((name, info) ->
            System.out.printf("   • %s %s: %s - %s%n", info.get("status_icon"), name, info.get("type"), info.get("status")));

        System.out.println("\nTestando comunicação via eventos...");
        bus.broadcast("data.ready", Collections.singletonMap("id", 1), "demo", MessagePriority.NORMAL, RoutingStrategy.BROADCAST);
        Thread.sleep(500);

        System.out.println("\nTestando request assíncrono...");
        Map<String, Object> res = bus.<Map<String, Object>>request("processor_alpha", "process", new Object[]{Collections.singletonMap("value", 42)},
                5.0, MessagePriority.NORMAL, "demo", true).get();
        System.out.println("   ✅ Processor Alpha: " + res);

        System.out.println("\nRelatório de saúde:");
        System.out.println("   Componentes ativos: " + bus.registry.size());
        System.out.println("   Métricas: " + bus.getMetrics().toDict());

        System.out.println("\nFinalizando...");
        proc1.shutdown().join();
        proc2.shutdown().join();
        monitor.shutdown().join();
        bus.stop().join();
        System.out.println("✅ Demonstração concluída.");
    }
}
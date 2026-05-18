import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;

/**
 * VHALINOR IAG - NEURAL BUS QUÂNTICO
 * Sistema de comunicação neural distribuída (Java Port)
 */
public class NeuralBusSystem {

    // ==================== ENUMS ====================

    enum MessagePriority {
        BACKGROUND(0, "⚪", "Processos em background"),
        LOW(1, "🟢", "Baixa prioridade"),
        NORMAL(2, "🔵", "Prioridade normal"),
        HIGH(3, "🟠", "Alta prioridade"),
        CRITICAL(4, "🔴", "Prioridade crítica"),
        EMERGENCY(5, "💀", "Emergência");

        final int value;
        final String icon, description;
        MessagePriority(int v, String i, String d) { value = v; icon = i; description = d; }
    }

    enum MessageType {
        EVENT("event", "📢", "Evento de sistema"),
        REQUEST("request", "📨", "Requisição"),
        RESPONSE("response", "📩", "Resposta"),
        COMMAND("command", "⚡", "Comando"),
        STATUS("status", "📊", "Status"),
        ERROR("error", "❌", "Erro"),
        HEARTBEAT("heartbeat", "💓", "Sinal de vida"),
        SYNC("sync", "🔄", "Sincronização"),
        QUANTUM("quantum", "⚛️", "Quântica"),
        BROADCAST("broadcast", "📡", "Broadcast");

        final String value, icon, description;
        MessageType(String v, String i, String d) { value = v; icon = i; description = d; }
    }

    enum ComponentStatus {
        ACTIVE("active", "🟢"), IDLE("idle", "⚪"), BUSY("busy", "🟡"),
        ERROR("error", "🔴"), DISABLED("disabled", "⚫"), DEGRADED("degraded", "🟠"),
        RECOVERING("recovering", "🔄");

        final String value, icon;
        ComponentStatus(String v, String i) { value = v; icon = i; }
    }

    enum RoutingStrategy {
        DIRECT("direct", "🎯"), BROADCAST("broadcast", "📡"), ANYCAST("anycast", "🎲"),
        MULTICAST("multicast", "👥"), LOAD_BALANCED("load_balanced", "⚖️"), PRIORITY("priority", "📊");

        final String value, icon;
        RoutingStrategy(String v, String i) { value = v; icon = i; }
    }

    enum CircuitBreakerState {
        CLOSED("closed", "🟢"), OPEN("open", "🔴"), HALF_OPEN("half_open", "🟡");

        final String value, icon;
        CircuitBreakerState(String v, String i) { value = v; icon = i; }
    }

    // ==================== DATA CLASSES ====================

    static class NeuralMessage {
        String id, source;
        List<String> destination;
        MessageType type;
        MessagePriority priority;
        Object payload;
        long timestamp, ttlMs;
        String correlationId;
        RoutingStrategy routing;
        boolean compressed, encrypted;
        int retryCount, maxRetries;
        Map<String, Object> metadata = new HashMap<>();

        NeuralMessage(String id, String source, List<String> dest, MessageType type, MessagePriority pri,
                      Object payload, long ttlMs, RoutingStrategy routing) {
            this.id = id != null ? id : UUID.randomUUID().toString();
            this.source = source;
            this.destination = dest;
            this.type = type;
            this.priority = pri;
            this.payload = payload;
            this.timestamp = System.currentTimeMillis();
            this.ttlMs = ttlMs;
            this.routing = routing;
            this.maxRetries = 3;
        }
        boolean isExpired() { return System.currentTimeMillis() - timestamp > ttlMs; }
    }

    static class ComponentRegistration {
        String name;
        Object instance;
        Map<String, Object> metadata;
        ComponentStatus status;
        long registeredAt, lastHeartbeat, lastActivity;
        AtomicLong messageCount = new AtomicLong(), errorCount = new AtomicLong();
        double avgResponseTimeMs;
        Map<String, CircuitBreaker> circuitBreakers = new HashMap<>();
        boolean isAlive() { return System.currentTimeMillis() - lastHeartbeat < 300_000; }
    }

    static class NeuralBusMetrics {
        long messageCount;
        Map<String, Long> byType = new HashMap<>(), byPriority = new HashMap<>();
        double totalProcTimeMs;
        double avgProcTimeMs, minProcTimeMs = Double.MAX_VALUE, maxProcTimeMs;
        long errors, retries, timeouts;
        int registrySize, subscriptionCount, activeComponents;
        long startTime = System.currentTimeMillis();
        double cpuPercent, memoryPercent;
        Map<String, Integer> queueSizes = new HashMap<>();
        synchronized void record(NeuralMessage msg, double procTimeMs) {
            messageCount++;
            byType.merge(msg.type.value, 1L, Long::sum);
            byPriority.merge(msg.priority.name(), 1L, Long::sum);
            totalProcTimeMs += procTimeMs;
            avgProcTimeMs = totalProcTimeMs / messageCount;
            if (procTimeMs < minProcTimeMs) minProcTimeMs = procTimeMs;
            if (procTimeMs > maxProcTimeMs) maxProcTimeMs = procTimeMs;
        }
    }

    // ==================== CIRCUIT BREAKER ====================

    static class CircuitBreaker {
        private final String name;
        private final int threshold;
        private final long timeoutMs;
        private CircuitBreakerState state = CircuitBreakerState.CLOSED;
        private int failureCount;
        private long lastFailureTime;
        long totalFailures, totalSuccesses;

        CircuitBreaker(String name, int threshold, long timeoutMs) {
            this.name = name;
            this.threshold = threshold;
            this.timeoutMs = timeoutMs;
        }

        synchronized <T> CompletableFuture<T> execute(Supplier<CompletableFuture<T>> action) {
            if (state == CircuitBreakerState.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime > timeoutMs) {
                    state = CircuitBreakerState.HALF_OPEN;
                    log("🔄 Half-open: " + name);
                } else {
                    return CompletableFuture.failedFuture(new RuntimeException("Circuit breaker OPEN"));
                }
            }
            return action.get().handle((res, ex) -> {
                synchronized (this) {
                    if (ex != null) {
                        totalFailures++;
                        failureCount++;
                        lastFailureTime = System.currentTimeMillis();
                        if (failureCount >= threshold) {
                            state = CircuitBreakerState.OPEN;
                            log("🔴 OPEN: " + name);
                        }
                        throw (ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex));
                    } else {
                        totalSuccesses++;
                        if (state == CircuitBreakerState.HALF_OPEN) {
                            state = CircuitBreakerState.CLOSED;
                            failureCount = 0;
                            log("✅ CLOSED: " + name);
                        }
                        return res;
                    }
                }
            });
        }

        private void log(String msg) { System.out.println("[CircuitBreaker] " + msg); }
    }

    // ==================== RATE LIMITER ====================

    static class RateLimiter {
        private final double rate;
        private double tokens;
        private long lastRefill;
        RateLimiter(double permitsPerSecond) {
            this.rate = permitsPerSecond;
            this.tokens = permitsPerSecond;
            this.lastRefill = System.nanoTime();
        }
        synchronized void acquire() {
            long now = System.nanoTime();
            double elapsedSec = (now - lastRefill) / 1e9;
            tokens = Math.min(rate, tokens + elapsedSec * rate);
            lastRefill = now;
            if (tokens < 1) {
                double waitSec = (1 - tokens) / rate;
                try { Thread.sleep((long)(waitSec * 1000)); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                tokens = 0;
            } else {
                tokens -= 1;
            }
        }
    }

    // ==================== NEURAL COMPONENT ABSTRACT ====================

    abstract static class NeuralComponent {
        protected String name;
        protected NeuralBus bus;
        protected boolean registered;
        protected ComponentStatus status = ComponentStatus.IDLE;

        NeuralComponent(String name, NeuralBus bus) { this.name = name; this.bus = bus; }

        abstract CompletableFuture<Boolean> initialize();
        abstract CompletableFuture<Boolean> shutdown();

        CompletableFuture<Boolean> register(Map<String, Object> metadata) {
            return bus.register(name, this, metadata).thenApply(r -> { registered = r; status = ComponentStatus.ACTIVE; return r; });
        }
        CompletableFuture<Boolean> unregister() {
            return bus.unregister(name).thenApply(r -> { registered = false; status = ComponentStatus.DISABLED; return r; });
        }
        CompletableFuture<String> sendEvent(String event, Object payload, MessagePriority pri, RoutingStrategy routing) {
            return bus.broadcast(event, payload, name, pri, routing);
        }
        CompletableFuture<Object> request(String target, String method, Object... args) {
            return bus.request(target, method, args, 5000, MessagePriority.NORMAL, name);
        }
        void heartbeat() { bus.heartbeat(name); }
        void onNeuralEvent(String event, Object payload) {}
    }

    // ==================== NEURAL BUS ====================

    static class NeuralBus {
        // Configuração
        private final int maxQueueSize = 10000;
        private final int workers = 8;
        private final long defaultTimeoutMs = 30000;
        private final boolean enableMetrics = true, enableCircuitBreaker = true;

        // Executor e filas
        private final ExecutorService executor = Executors.newCachedThreadPool();
        private final Map<MessagePriority, BlockingQueue<NeuralMessage>> queues = new HashMap<>();
        private final RateLimiter rateLimiter = new RateLimiter(1000);

        // Registro
        private final Map<String, ComponentRegistration> registry = new ConcurrentHashMap<>();
        private final Map<String, List<Pair<BiConsumer<String, Object>, MessagePriority>>> subs = new ConcurrentHashMap<>();
        private final List<Pair<BiConsumer<String, Object>, MessagePriority>> wildcardSubs = new CopyOnWriteArrayList<>();

        // Requests pendentes
        private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();
        private final Map<String, Long> requestTimeouts = new ConcurrentHashMap<>();

        // Circuit breakers
        private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

        // Métricas
        private final NeuralBusMetrics metrics = new NeuralBusMetrics();

        // Estado
        private volatile boolean running;
        private final List<Thread> workerThreads = new ArrayList<>();

        NeuralBus() {
            for (MessagePriority p : MessagePriority.values()) {
                queues.put(p, new LinkedBlockingQueue<>(maxQueueSize / (p.value + 1)));
            }
        }

        // Inicialização/parada
        synchronized void start() {
            if (running) return;
            running = true;
            // Workers por prioridade
            for (MessagePriority p : MessagePriority.values()) {
                Thread t = new Thread(() -> messageWorker(p), "BusWorker-" + p.name());
                t.setDaemon(true);
                t.start();
                workerThreads.add(t);
            }
            // Workers auxiliares
            Thread cleanup = new Thread(this::cleanupWorker, "BusCleanup");
            cleanup.setDaemon(true);
            cleanup.start();
            workerThreads.add(cleanup);
        }
        synchronized void stop() {
            running = false;
            workerThreads.forEach(Thread::interrupt);
            pendingRequests.values().forEach(f -> f.completeExceptionally(new CancellationException()));
            executor.shutdown();
        }

        // Registro
        CompletableFuture<Boolean> register(String name, Object instance, Map<String, Object> metadata) {
            ComponentRegistration reg = new ComponentRegistration();
            reg.name = name; reg.instance = instance; reg.metadata = metadata;
            reg.status = ComponentStatus.ACTIVE;
            reg.registeredAt = reg.lastHeartbeat = reg.lastActivity = System.currentTimeMillis();
            registry.put(name, reg);
            metrics.registrySize = registry.size();
            return CompletableFuture.completedFuture(true);
        }
        CompletableFuture<Boolean> unregister(String name) {
            registry.remove(name);
            metrics.registrySize = registry.size();
            return CompletableFuture.completedFuture(true);
        }
        <T> CompletableFuture<T> getComponent(String name) {
            ComponentRegistration reg = registry.get(name);
            if (reg != null && reg.isAlive()) return CompletableFuture.completedFuture((T) reg.instance);
            return CompletableFuture.completedFuture(null);
        }
        void heartbeat(String name) {
            ComponentRegistration reg = registry.get(name);
            if (reg != null) reg.lastHeartbeat = System.currentTimeMillis();
        }

        // Pub/Sub
        void subscribe(String event, BiConsumer<String, Object> handler, MessagePriority pri) {
            if ("*".equals(event)) wildcardSubs.add(Pair.of(handler, pri));
            else subs.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>()).add(Pair.of(handler, pri));
            metrics.subscriptionCount = subs.size() + wildcardSubs.size();
        }
        void unsubscribe(String event, BiConsumer<String, Object> handler) {
            if ("*".equals(event)) wildcardSubs.removeIf(p -> p.first.equals(handler));
            else {
                List<Pair<BiConsumer<String, Object>, MessagePriority>> list = subs.get(event);
                if (list != null) list.removeIf(p -> p.first.equals(handler));
            }
        }

        // Broadcast
        CompletableFuture<String> broadcast(String event, Object payload, String source,
                                            MessagePriority priority, RoutingStrategy routing) {
            rateLimiter.acquire();
            NeuralMessage msg = new NeuralMessage(null, source, null, MessageType.EVENT, priority,
                    payload, defaultTimeoutMs, routing);
            msg.metadata.put("event", event);
            try {
                queues.get(priority).put(msg);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            metrics.messageCount++;
            return CompletableFuture.completedFuture(msg.id);
        }

        // Request
        CompletableFuture<Object> request(String target, String method, Object[] args,
                                          long timeoutMs, MessagePriority priority, String source) {
            rateLimiter.acquire();
            String requestId = UUID.randomUUID().toString();
            CompletableFuture<Object> future = new CompletableFuture<>();
            pendingRequests.put(requestId, future);
            requestTimeouts.put(requestId, System.currentTimeMillis() + timeoutMs);

            CompletableFuture<Object> resultFuture = getComponent(target).thenCompose(comp -> {
                if (comp == null) {
                    return CompletableFuture.failedFuture(new NoSuchElementException("Componente não encontrado: " + target));
                }
                // Usa reflexão simples para invocar method (assume métodos públicos sem args ou com array)
                try {
                    java.lang.reflect.Method m = comp.getClass().getMethod(method, Object[].class);
                    return CompletableFuture.supplyAsync(() -> {
                        try { return m.invoke(comp, (Object) args); }
                        catch (Exception e) { throw new RuntimeException(e); }
                    }, executor);
                } catch (NoSuchMethodException e) {
                    return CompletableFuture.failedFuture(e);
                }
            });

            return resultFuture.orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .whenComplete((r, ex) -> { pendingRequests.remove(requestId); requestTimeouts.remove(requestId); });
        }

        // Workers
        private void messageWorker(MessagePriority priority) {
            BlockingQueue<NeuralMessage> q = queues.get(priority);
            while (running) {
                try {
                    NeuralMessage msg = q.poll(100, TimeUnit.MILLISECONDS);
                    if (msg == null) continue;
                    if (msg.isExpired()) continue;
                    long start = System.nanoTime();
                    dispatchMessage(msg);
                    double procMs = (System.nanoTime() - start) / 1e6;
                    metrics.record(msg, procMs);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
            }
        }

        private void dispatchMessage(NeuralMessage msg) {
            String event = (String) msg.metadata.getOrDefault("event", "");
            List<Pair<BiConsumer<String, Object>, MessagePriority>> handlers = new ArrayList<>();
            List<Pair<BiConsumer<String, Object>, MessagePriority>> specific = subs.get(event);
            if (specific != null) handlers.addAll(specific);
            handlers.addAll(wildcardSubs);
            for (Pair<BiConsumer<String, Object>, MessagePriority> p : handlers) {
                if (p.second.value >= msg.priority.value) {
                    try { p.first.accept(event, msg.payload); } catch (Exception e) { /* log */ }
                }
            }
            // Quantum bridge (stub)
        }

        private void cleanupWorker() {
            while (running) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
                long now = System.currentTimeMillis();
                // remove pending timeouts
                for (Map.Entry<String, Long> e : requestTimeouts.entrySet()) {
                    if (now > e.getValue()) {
                        CompletableFuture<Object> f = pendingRequests.remove(e.getKey());
                        requestTimeouts.remove(e.getKey());
                        if (f != null) f.completeExceptionally(new TimeoutException());
                    }
                }
                // remove inactive components
                registry.entrySet().removeIf(entry -> !entry.getValue().isAlive());
            }
        }

        // Getters de métricas
        NeuralBusMetrics getMetrics() { return metrics; }
        Map<String, ComponentRegistration> getRegistry() { return Collections.unmodifiableMap(registry); }
    }

    // ==================== COMPONENTES EXAMPLE ====================

    static class DataProcessor extends NeuralComponent {
        private final AtomicLong processedCount = new AtomicLong();

        DataProcessor(String name, NeuralBus bus) { super(name, bus); }

        @Override
        CompletableFuture<Boolean> initialize() {
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "processor");
            meta.put("version", "1.0");
            return register(meta);
        }
        @Override
        CompletableFuture<Boolean> shutdown() { return unregister(); }

        // Método exposto para request
        public Map<String, Object> process(Object[] data) {
            processedCount.incrementAndGet();
            Map<String, Object> res = new HashMap<>();
            res.put("input", data);
            res.put("processed", data != null ? data.toString() : "null");
            res.put("count", processedCount.get());
            return res;
        }

        @Override
        void onNeuralEvent(String event, Object payload) {
            if ("data.ready".equals(event)) {
                sendEvent("data.processed", process(new Object[]{payload}), MessagePriority.NORMAL, RoutingStrategy.BROADCAST);
            }
        }
    }

    static class MonitoringComponent extends NeuralComponent {
        private final Map<String, Long> eventCounts = new ConcurrentHashMap<>();

        MonitoringComponent(String name, NeuralBus bus) { super(name, bus); }

        @Override
        CompletableFuture<Boolean> initialize() {
            Map<String, Object> meta = new HashMap<>();
            meta.put("type", "monitor");
            register(meta).thenRun(() -> bus.subscribe("*", this::onEvent, MessagePriority.LOW));
            return CompletableFuture.completedFuture(true);
        }
        @Override
        CompletableFuture<Boolean> shutdown() {
            bus.unsubscribe("*", this::onEvent);
            return unregister();
        }

        private void onEvent(String event, Object payload) {
            eventCounts.merge(event, 1L, Long::sum);
        }
        Map<String, Long> stats() { return new HashMap<>(eventCounts); }
    }

    // ==================== DEMO ====================

    public static void main(String[] args) throws Exception {
        System.out.println("🧠 VHALINOR IAG - NEURAL BUS QUÂNTICO (Java Port)");
        NeuralBus bus = new NeuralBus();
        bus.start();

        // Criar componentes
        DataProcessor proc1 = new DataProcessor("processor_alpha", bus);
        DataProcessor proc2 = new DataProcessor("processor_beta", bus);
        MonitoringComponent monitor = new MonitoringComponent("monitor", bus);

        proc1.initialize().join();
        proc2.initialize().join();
        monitor.initialize().join();

        // Listar componentes
        System.out.println("\nComponentes registrados:");
        bus.getRegistry().forEach((name, reg) -> System.out.println(" - " + name + " [" + reg.status.icon + "]"));

        // Enviar eventos
        System.out.println("\nEnviando eventos...");
        bus.broadcast("data.ready", "sample_data", "demo", MessagePriority.NORMAL, RoutingStrategy.BROADCAST).join();
        bus.broadcast("system.status", Collections.singletonMap("status", "operational"), "demo", MessagePriority.NORMAL, RoutingStrategy.BROADCAST).join();
        Thread.sleep(300);

        // Fazer request
        System.out.println("\nTestando request...");
        CompletableFuture<Object> future = bus.request("processor_alpha", "process", new Object[]{"test data"}, 5000, MessagePriority.NORMAL, "demo");
        try {
            Object result = future.get(6, TimeUnit.SECONDS);
            System.out.println("Resultado: " + result);
        } catch (Exception e) {
            System.out.println("Erro no request: " + e.getMessage());
        }

        // Métricas
        System.out.println("\nMétricas:");
        NeuralBusMetrics m = bus.getMetrics();
        System.out.printf("Mensagens: %d, Erros: %d, Tempo médio: %.2f ms%n", m.messageCount, m.errors, m.avgProcTimeMs);

        // Cleanup
        proc1.shutdown().join();
        proc2.shutdown().join();
        monitor.shutdown().join();
        bus.stop();
        System.out.println("\n✅ Demonstração concluída.");
    }

    // ==================== UTIL ==================== 
    private static class Pair<A, B> {
        final A first; final B second;
        Pair(A a, B b) { first = a; second = b; }
        static <A, B> Pair<A, B> of(A a, B b) { return new Pair<>(a, b); }
    }
}
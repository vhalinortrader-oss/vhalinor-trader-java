import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;
import java.util.function.Consumer;

/**
 * ╔═══════════════════════════════════════════════════════════════════════════════╗
 * ║              ADVANCED AUTOMATION ORCHESTRATOR - VERSÃO 3.0.0                ║
 * ║           Sistema de Orquestração Inteligente com Integração Trading            ║
 * ╠═══════════════════════════════════════════════════════════════════════════════╣
 * ║  Versão: 3.0.0                                                                 ║
 * ║  Integração: TradingOrchestrator                                              ║
 * ║  Status: 🟢 OPERACIONAL                                                        ║
 * ╚═══════════════════════════════════════════════════════════════════════════════╝
 * 
 * Orquestrador genérico de automação com integração opcional ao sistema de trading.
 * Mantém funcionalidade original de automação genérica com capacidade de integrar
 * com o novo sistema de trading modular VHALINOR.
 * 
 * Melhorias na versão 3.0.0:
 * - Encapsulamento melhorado com getters/setters
 * - Validação de dados
 * - Retry automático com backoff exponencial
 * - Scheduling real de tarefas
 * - Métricas mais precisas
 * - Thread-safety aprimorado
 * - Cancelamento e pausa de tarefas
 * - Callbacks para eventos de tarefa
 */
public class AdvancedAutomationOrchestrator {
    private static final Logger logger = Logger.getLogger(AdvancedAutomationOrchestrator.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final long DEFAULT_RETRY_DELAY_MS = 1000;
    private static final double RETRY_BACKOFF_MULTIPLIER = 2.0;

    // Enums equivalentes
    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED, PAUSED
    }

    public enum TaskPriority {
        LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);

        private final int value;
        TaskPriority(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public enum AutomationMode {
        MANUAL, SCHEDULED, CONTINUOUS, ADAPTIVE
    }

    // Classes equivalentes às dataclasses com encapsulamento melhorado
    public static class AutomationTask {
        private final String id;
        private final String name;
        private final String description;
        private final Supplier<Object> function;
        private final Map<String, Object> kwargs;
        private final TaskPriority priority;
        private final int maxRetries;
        private final Integer intervalMinutes;
        private final LocalDateTime scheduleTime;
        private final LocalDateTime createdAt;
        private final Consumer<TaskResult> onSuccess;
        private final Consumer<Exception> onFailure;
        
        private volatile TaskStatus status;
        private volatile int retryCount;
        private volatile LocalDateTime startedAt;
        private volatile LocalDateTime completedAt;
        private volatile Object result;
        private volatile String error;
        private volatile boolean paused;
        private final AtomicBoolean cancelled;

        private AutomationTask(Builder builder) {
            this.id = builder.id;
            this.name = builder.name;
            this.description = builder.description;
            this.function = builder.function;
            this.kwargs = new HashMap<>(builder.kwargs);
            this.priority = builder.priority;
            this.maxRetries = builder.maxRetries;
            this.intervalMinutes = builder.intervalMinutes;
            this.scheduleTime = builder.scheduleTime;
            this.createdAt = LocalDateTime.now();
            this.onSuccess = builder.onSuccess;
            this.onFailure = builder.onFailure;
            this.status = TaskStatus.PENDING;
            this.retryCount = 0;
            this.paused = false;
            this.cancelled = new AtomicBoolean(false);
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public Supplier<Object> getFunction() { return function; }
        public Map<String, Object> getKwargs() { return new HashMap<>(kwargs); }
        public TaskPriority getPriority() { return priority; }
        public int getMaxRetries() { return maxRetries; }
        public Integer getIntervalMinutes() { return intervalMinutes; }
        public LocalDateTime getScheduleTime() { return scheduleTime; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public TaskStatus getStatus() { return status; }
        public int getRetryCount() { return retryCount; }
        public LocalDateTime getStartedAt() { return startedAt; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public Object getResult() { return result; }
        public String getError() { return error; }
        public boolean isPaused() { return paused; }
        public boolean isCancelled() { return cancelled.get(); }

        // Setters para estado (controlado pelo orquestrador)
        void setStatus(TaskStatus status) { this.status = status; }
        void incrementRetryCount() { this.retryCount++; }
        void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        void setResult(Object result) { this.result = result; }
        void setError(String error) { this.error = error; }
        void setPaused(boolean paused) { this.paused = paused; }
        boolean cancel() { return cancelled.compareAndSet(false, true); }

        // Callbacks
        void triggerSuccess(TaskResult taskResult) {
            if (onSuccess != null) {
                try {
                    onSuccess.accept(taskResult);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Erro no callback de sucesso para tarefa " + id, e);
                }
            }
        }

        void triggerFailure(Exception exception) {
            if (onFailure != null) {
                try {
                    onFailure.accept(exception);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Erro no callback de falha para tarefa " + id, e);
                }
            }
        }

        public static Builder builder(String id, String name, Supplier<Object> function) {
            return new Builder(id, name, function);
        }

        public static class Builder {
            private final String id;
            private final String name;
            private final Supplier<Object> function;
            private String description = "";
            private Map<String, Object> kwargs = new HashMap<>();
            private TaskPriority priority = TaskPriority.MEDIUM;
            private int maxRetries = 3;
            private Integer intervalMinutes;
            private LocalDateTime scheduleTime;
            private Consumer<TaskResult> onSuccess;
            private Consumer<Exception> onFailure;

            private Builder(String id, String name, Supplier<Object> function) {
                if (id == null || id.trim().isEmpty()) {
                    throw new IllegalArgumentException("ID da tarefa não pode ser nulo ou vazio");
                }
                if (name == null || name.trim().isEmpty()) {
                    throw new IllegalArgumentException("Nome da tarefa não pode ser nulo ou vazio");
                }
                if (function == null) {
                    throw new IllegalArgumentException("Função da tarefa não pode ser nula");
                }
                this.id = id;
                this.name = name;
                this.function = function;
            }

            public Builder description(String description) {
                this.description = description != null ? description : "";
                return this;
            }

            public Builder kwargs(Map<String, Object> kwargs) {
                this.kwargs = kwargs != null ? new HashMap<>(kwargs) : new HashMap<>();
                return this;
            }

            public Builder priority(TaskPriority priority) {
                this.priority = priority != null ? priority : TaskPriority.MEDIUM;
                return this;
            }

            public Builder maxRetries(int maxRetries) {
                this.maxRetries = Math.max(0, maxRetries);
                return this;
            }

            public Builder intervalMinutes(Integer intervalMinutes) {
                this.intervalMinutes = intervalMinutes;
                return this;
            }

            public Builder scheduleTime(LocalDateTime scheduleTime) {
                this.scheduleTime = scheduleTime;
                return this;
            }

            public Builder onSuccess(Consumer<TaskResult> onSuccess) {
                this.onSuccess = onSuccess;
                return this;
            }

            public Builder onFailure(Consumer<Exception> onFailure) {
                this.onFailure = onFailure;
                return this;
            }

            public AutomationTask build() {
                return new AutomationTask(this);
            }
        }
    }

    public static class TaskResult {
        private final String taskId;
        private final String taskName;
        private final Object result;
        private final LocalDateTime completedAt;
        private final long executionTimeMs;
        private final int retryCount;

        public TaskResult(String taskId, String taskName, Object result, 
                         LocalDateTime completedAt, long executionTimeMs, int retryCount) {
            this.taskId = taskId;
            this.taskName = taskName;
            this.result = result;
            this.completedAt = completedAt;
            this.executionTimeMs = executionTimeMs;
            this.retryCount = retryCount;
        }

        public String getTaskId() { return taskId; }
        public String getTaskName() { return taskName; }
        public Object getResult() { return result; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public long getExecutionTimeMs() { return executionTimeMs; }
        public int getRetryCount() { return retryCount; }
    }

    public static class AutomationMetrics {
        private final AtomicInteger totalTasks = new AtomicInteger(0);
        private final AtomicInteger completedTasks = new AtomicInteger(0);
        private final AtomicInteger failedTasks = new AtomicInteger(0);
        private final AtomicInteger runningTasks = new AtomicInteger(0);
        private final AtomicInteger pendingTasks = new AtomicInteger(0);
        private final AtomicInteger cancelledTasks = new AtomicInteger(0);
        private final AtomicLong totalExecutionTimeMs = new AtomicLong(0);
        private volatile LocalDateTime lastUpdate = LocalDateTime.now();

        public int getTotalTasks() { return totalTasks.get(); }
        public int getCompletedTasks() { return completedTasks.get(); }
        public int getFailedTasks() { return failedTasks.get(); }
        public int getRunningTasks() { return runningTasks.get(); }
        public int getPendingTasks() { return pendingTasks.get(); }
        public int getCancelledTasks() { return cancelledTasks.get(); }
        
        public double getSuccessRate() {
            int total = totalTasks.get();
            if (total == 0) return 0.0;
            return (double) completedTasks.get() / total;
        }
        
        public double getAverageExecutionTimeMs() {
            int completed = completedTasks.get();
            if (completed == 0) return 0.0;
            return (double) totalExecutionTimeMs.get() / completed;
        }
        
        public double getTotalExecutionTimeMs() { return totalExecutionTimeMs.get(); }
        public LocalDateTime getLastUpdate() { return lastUpdate; }

        // Métodos para atualização (usados internamente)
        void incrementTotal() { totalTasks.incrementAndGet(); }
        void incrementCompleted(long executionTimeMs) { 
            completedTasks.incrementAndGet();
            totalExecutionTimeMs.addAndGet(executionTimeMs);
            lastUpdate = LocalDateTime.now();
        }
        void incrementFailed() { 
            failedTasks.incrementAndGet();
            lastUpdate = LocalDateTime.now();
        }
        void incrementRunning() { runningTasks.incrementAndGet(); }
        void decrementRunning() { runningTasks.decrementAndGet(); }
        void incrementPending() { pendingTasks.incrementAndGet(); }
        void decrementPending() { pendingTasks.decrementAndGet(); }
        void incrementCancelled() { cancelledTasks.incrementAndGet(); }
    }

    public static class SystemHealth {
        private volatile double cpuUsage = 0.0;
        private volatile double memoryUsage = 0.0;
        private volatile double diskUsage = 0.0;
        private volatile int activeConnections = 0;
        private volatile double errorRate = 0.0;
        private volatile double uptimeHours = 0.0;
        private volatile String status = "HEALTHY";
        private volatile LocalDateTime timestamp = LocalDateTime.now();

        public double getCpuUsage() { return cpuUsage; }
        public double getMemoryUsage() { return memoryUsage; }
        public double getDiskUsage() { return diskUsage; }
        public int getActiveConnections() { return activeConnections; }
        public double getErrorRate() { return errorRate; }
        public double getUptimeHours() { return uptimeHours; }
        public String getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }

        void update(LocalDateTime startTime) {
            this.timestamp = LocalDateTime.now();
            this.uptimeHours = Duration.between(startTime, timestamp).toMinutes() / 60.0;
        }
    }

    // Sistema principal
    private final ScheduledExecutorService scheduler;
    private final ExecutorService executor;
    private final Map<String, AutomationTask> tasks;
    private final Map<String, ScheduledFuture<?>> scheduledTasks;
    private final AutomationMetrics metrics;
    private final SystemHealth health;
    private final AtomicBoolean running;
    private final LocalDateTime startTime;
    
    // Integração com novo sistema de trading
    private Object tradingOrchestrator; // TradingOrchestrator (opcional)
    private volatile boolean tradingIntegrationEnabled;

    public AdvancedAutomationOrchestrator() {
        this.scheduler = Executors.newScheduledThreadPool(5);
        this.executor = Executors.newCachedThreadPool();
        this.tasks = new ConcurrentHashMap<>();
        this.scheduledTasks = new ConcurrentHashMap<>();
        this.metrics = new AutomationMetrics();
        this.health = new SystemHealth();
        this.running = new AtomicBoolean(true);
        this.startTime = LocalDateTime.now();
        this.tradingIntegrationEnabled = false;

        logger.info("AdvancedAutomationOrchestrator v3.0.0 inicializado com integração Trading opcional");
    }
    
    /**
     * Habilita integração com o novo sistema de trading
     * @param tradingOrchestrator Instância do TradingOrchestrator
     */
    public void enableTradingIntegration(Object tradingOrchestrator) {
        if (!running.get()) {
            throw new IllegalStateException("Orquestrador não está em execução");
        }
        this.tradingOrchestrator = tradingOrchestrator;
        this.tradingIntegrationEnabled = true;
        logger.info("Integração com TradingOrchestrator habilitada");
    }
    
    /**
     * Desabilita integração com sistema de trading
     */
    public void disableTradingIntegration() {
        this.tradingOrchestrator = null;
        this.tradingIntegrationEnabled = false;
        logger.info("Integração com TradingOrchestrator desabilitada");
    }

    /**
     * Adiciona uma tarefa ao orquestrador
     * @param task Tarefa a ser adicionada
     * @throws IllegalArgumentException se task for null
     */
    public void addTask(AutomationTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Tarefa não pode ser nula");
        }
        if (!running.get()) {
            throw new IllegalStateException("Orquestrador não está em execução");
        }
        
        tasks.put(task.getId(), task);
        metrics.incrementTotal();
        metrics.incrementPending();
        
        logger.info(String.format("[%s] Tarefa adicionada: %s (Prioridade: %s)", 
            LocalDateTime.now().format(TIME_FORMATTER), task.getName(), task.getPriority()));
        
        // Se a tarefa tem scheduleTime, agenda automaticamente
        if (task.getScheduleTime() != null) {
            scheduleTask(task.getId(), task.getScheduleTime());
        }
        
        // Se a tarefa tem intervalMinutes, agenda como recorrente
        if (task.getIntervalMinutes() != null) {
            scheduleRecurringTask(task.getId(), task.getIntervalMinutes());
        }
    }

    /**
     * Executa uma tarefa imediatamente
     * @param taskId ID da tarefa
     * @throws IllegalArgumentException se taskId for nulo ou tarefa não existir
     */
    public void executeTask(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da tarefa não pode ser nulo ou vazio");
        }
        
        AutomationTask task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Tarefa não encontrada: " + taskId);
        }
        
        if (!running.get()) {
            logger.warning("Orquestrador não está em execução, tarefa não será executada");
            return;
        }
        
        if (task.getStatus() != TaskStatus.PENDING) {
            logger.warning("Tarefa não está em estado PENDING: " + taskId);
            return;
        }
        
        if (task.isPaused()) {
            logger.info("Tarefa está pausada: " + taskId);
            return;
        }

        executeTaskInternal(task);
    }

    /**
     * Execução interna da tarefa com retry automático
     */
    private void executeTaskInternal(AutomationTask task) {
        task.setStatus(TaskStatus.RUNNING);
        task.setStartedAt(LocalDateTime.now());
        metrics.incrementRunning();
        metrics.decrementPending();

        executor.submit(() -> {
            LocalDateTime startTime = LocalDateTime.now();
            int attempt = 0;
            Exception lastException = null;
            
            while (attempt <= task.getMaxRetries() && !task.isCancelled() && running.get()) {
                if (task.isPaused()) {
                    logger.info("Tarefa pausada durante execução: " + task.getId());
                    task.setStatus(TaskStatus.PAUSED);
                    metrics.decrementRunning();
                    return;
                }
                
                attempt++;
                task.incrementRetryCount();
                
                try {
                    logger.info(String.format("Executando tarefa: %s (tentativa %d/%d)", 
                        task.getName(), attempt, task.getMaxRetries() + 1));
                    
                    Object result = task.getFunction().get();
                    
                    long executionTimeMs = Duration.between(startTime, LocalDateTime.now()).toMillis();
                    
                    task.setResult(result);
                    task.setCompletedAt(LocalDateTime.now());
                    task.setStatus(TaskStatus.COMPLETED);
                    
                    metrics.incrementCompleted(executionTimeMs);
                    metrics.decrementRunning();
                    
                    TaskResult taskResult = new TaskResult(
                        task.getId(), task.getName(), result,
                        task.getCompletedAt(), executionTimeMs, task.getRetryCount()
                    );
                    
                    task.triggerSuccess(taskResult);
                    
                    logger.info(String.format("Tarefa completada com sucesso: %s (tempo: %dms)", 
                        task.getName(), executionTimeMs));
                    return;
                    
                } catch (Exception e) {
                    lastException = e;
                    logger.log(Level.WARNING, String.format("Erro na tarefa: %s (tentativa %d/%d)", 
                        task.getName(), attempt, task.getMaxRetries() + 1), e);
                    
                    if (attempt <= task.getMaxRetries() && !task.isCancelled()) {
                        try {
                            // Backoff exponencial
                            long delayMs = (long) (DEFAULT_RETRY_DELAY_MS * Math.pow(RETRY_BACKOFF_MULTIPLIER, attempt - 1));
                            logger.info(String.format("Aguardando %dms antes da próxima tentativa...", delayMs));
                            Thread.sleep(delayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
            
            // Se chegou aqui, a tarefa falhou após todas as tentativas
            if (!task.isCancelled() && running.get()) {
                task.setStatus(TaskStatus.FAILED);
                task.setError(lastException != null ? lastException.getMessage() : "Erro desconhecido");
                task.setCompletedAt(LocalDateTime.now());
                
                metrics.incrementFailed();
                metrics.decrementRunning();
                
                task.triggerFailure(lastException != null ? lastException : new RuntimeException("Erro desconhecido"));
                
                logger.log(Level.SEVERE, "Tarefa falhou após todas as tentativas: " + task.getName());
            }
        });
    }

    /**
     * Agenda uma tarefa para execução em um horário específico
     * @param taskId ID da tarefa
     * @param scheduleTime Horário de execução
     */
    public void scheduleTask(String taskId, LocalDateTime scheduleTime) {
        if (taskId == null || scheduleTime == null) {
            throw new IllegalArgumentException("TaskId e scheduleTime não podem ser nulos");
        }
        
        AutomationTask task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Tarefa não encontrada: " + taskId);
        }
        
        long delay = Duration.between(LocalDateTime.now(), scheduleTime).toMillis();
        if (delay < 0) {
            logger.warning("Horário de schedule está no passado, executando imediatamente");
            executeTask(taskId);
            return;
        }
        
        ScheduledFuture<?> future = scheduler.schedule(() -> executeTask(taskId), delay, TimeUnit.MILLISECONDS);
        scheduledTasks.put(taskId, future);
        
        logger.info(String.format("Tarefa agendada: %s para %s", task.getName(), scheduleTime));
    }

    /**
     * Agenda uma tarefa recorrente
     * @param taskId ID da tarefa
     * @param intervalMinutes Intervalo em minutos
     */
    public void scheduleRecurringTask(String taskId, int intervalMinutes) {
        if (taskId == null || intervalMinutes <= 0) {
            throw new IllegalArgumentException("TaskId não pode ser nulo e interval deve ser positivo");
        }
        
        AutomationTask task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Tarefa não encontrada: " + taskId);
        }
        
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            () -> executeTask(taskId),
            0,
            intervalMinutes,
            TimeUnit.MINUTES
        );
        scheduledTasks.put(taskId + "_recurring", future);
        
        logger.info(String.format("Tarefa recorrente agendada: %s (intervalo: %d minutos)", 
            task.getName(), intervalMinutes));
    }

    /**
     * Cancela uma tarefa
     * @param taskId ID da tarefa
     * @return true se cancelada com sucesso
     */
    public boolean cancelTask(String taskId) {
        if (taskId == null) {
            return false;
        }
        
        AutomationTask task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        
        // Cancela scheduled task se existir
        ScheduledFuture<?> scheduledFuture = scheduledTasks.remove(taskId);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        
        // Cancela tarefa recorrente se existir
        ScheduledFuture<?> recurringFuture = scheduledTasks.remove(taskId + "_recurring");
        if (recurringFuture != null) {
            recurringFuture.cancel(false);
        }
        
        // Marca tarefa como cancelada
        boolean cancelled = task.cancel();
        if (cancelled) {
            task.setStatus(TaskStatus.CANCELLED);
            metrics.incrementCancelled();
            
            if (task.getStatus() == TaskStatus.RUNNING) {
                metrics.decrementRunning();
            } else if (task.getStatus() == TaskStatus.PENDING) {
                metrics.decrementPending();
            }
            
            logger.info("Tarefa cancelada: " + task.getName());
        }
        
        return cancelled;
    }

    /**
     * Pausa uma tarefa
     * @param taskId ID da tarefa
     * @return true se pausada com sucesso
     */
    public boolean pauseTask(String taskId) {
        if (taskId == null) {
            return false;
        }
        
        AutomationTask task = tasks.get(taskId);
        if (task == null || task.getStatus() != TaskStatus.RUNNING) {
            return false;
        }
        
        task.setPaused(true);
        logger.info("Tarefa pausada: " + task.getName());
        return true;
    }

    /**
     * Retoma uma tarefa pausada
     * @param taskId ID da tarefa
     * @return true se retomada com sucesso
     */
    public boolean resumeTask(String taskId) {
        if (taskId == null) {
            return false;
        }
        
        AutomationTask task = tasks.get(taskId);
        if (task == null || !task.isPaused()) {
            return false;
        }
        
        task.setPaused(false);
        task.setStatus(TaskStatus.PENDING);
        
        // Reexecuta a tarefa
        executeTaskInternal(task);
        
        logger.info("Tarefa retomada: " + task.getName());
        return true;
    }

    /**
     * Obtém uma tarefa por ID
     * @param taskId ID da tarefa
     * @return Tarefa ou null se não encontrada
     */
    public AutomationTask getTask(String taskId) {
        return tasks.get(taskId);
    }

    /**
     * Lista todas as tarefas
     * @return Lista de todas as tarefas
     */
    public List<AutomationTask> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Lista tarefas por status
     * @param status Status das tarefas
     * @return Lista de tarefas com o status especificado
     */
    public List<AutomationTask> getTasksByStatus(TaskStatus status) {
        return tasks.values().stream()
            .filter(t -> t.getStatus() == status)
            .collect(java.util.stream.Collectors.toList());
    }

    public AutomationMetrics getMetrics() {
        return metrics;
    }

    public SystemHealth getHealth() {
        health.update(startTime);
        return health;
    }

    /**
     * Verifica se o orquestrador está em execução
     * @return true se estiver em execução
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Encerra o orquestrador de forma graciosa
     */
    public void shutdown() {
        if (!running.compareAndSet(true, false)) {
            logger.warning("Orquestrador já está encerrado");
            return;
        }
        
        logger.info(String.format("[%s] Iniciando shutdown do orquestrador...", 
            LocalDateTime.now().format(TIME_FORMATTER)));
            
        // Cancela todas as tarefas pendentes e em execução
        tasks.values().stream()
            .filter(t -> t.getStatus() == TaskStatus.PENDING || t.getStatus() == TaskStatus.RUNNING)
            .forEach(t -> {
                cancelTask(t.getId());
                logger.fine("Tarefa cancelada durante shutdown: " + t.getName());
            });
        
        // Cancela todas as tarefas agendadas
        scheduledTasks.values().forEach(future -> future.cancel(false));
        scheduledTasks.clear();
        
        // Desabilita integração com trading
        if (tradingIntegrationEnabled) {
            disableTradingIntegration();
        }
        
        // Encerra executor e scheduler
        scheduler.shutdown();
        executor.shutdown();
        
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                logger.warning("Scheduler forçado a encerrar após timeout");
            }
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                logger.warning("Executor forçado a encerrar após timeout");
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        double uptimeHours = Duration.between(startTime, LocalDateTime.now()).toMinutes() / 60.0;
        logger.info(String.format("[%s] Orquestrador encerrado. Uptime total: %.2f horas", 
            LocalDateTime.now().format(TIME_FORMATTER), uptimeHours));
    }

    /**
     * Método main para teste
     */
    public static void main(String[] args) {
        AdvancedAutomationOrchestrator orchestrator = new AdvancedAutomationOrchestrator();

        // Exemplo de tarefa usando Builder
        AutomationTask task = AutomationTask.builder(
            "test-task",
            "Tarefa de Teste",
            () -> {
                System.out.println("Executando tarefa de teste");
                return "Sucesso";
            }
        )
        .description("Uma tarefa simples para teste")
        .priority(TaskPriority.HIGH)
        .maxRetries(2)
        .onSuccess(taskResult -> {
            System.out.println("Callback de sucesso: " + taskResult.getResult());
        })
        .onFailure(exception -> {
            System.out.println("Callback de falha: " + exception.getMessage());
        })
        .build();

        orchestrator.addTask(task);
        orchestrator.executeTask("test-task");

        // Aguardar um pouco
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Métricas: " + orchestrator.getMetrics().getCompletedTasks() + " tarefas completadas");
        System.out.println("Taxa de sucesso: " + String.format("%.2f%%", orchestrator.getMetrics().getSuccessRate() * 100));
        System.out.println("Tempo médio de execução: " + String.format("%.2fms", orchestrator.getMetrics().getAverageExecutionTimeMs()));

        orchestrator.shutdown();
    }
}
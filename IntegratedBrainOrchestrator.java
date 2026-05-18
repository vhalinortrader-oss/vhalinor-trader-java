package br.com.vhalinor.iag;

import br.com.vhalinor.iag.core.*;
import br.com.vhalinor.iag.integration.IntegrationHub;
import br.com.vhalinor.iag.ml.MLModuleBridge;
import br.com.vhalinor.iag.orchestration.AdaptiveOptimizationEngine;
import br.com.vhalinor.iag.orchestration.DistributedOrchestrator;
import br.com.vhalinor.iag.persistence.PersistenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class IntegratedBrainOrchestrator implements IntegrationHub.DataReceiver {
    private static final Logger log = LoggerFactory.getLogger(IntegratedBrainOrchestrator.class);
    private final IntegrationHub hub = new IntegrationHub();
    private final PersistenceSystem persistence;
    private final DistributedOrchestrator orchestrator;
    private final AdaptiveOptimizationEngine optimizer;
    private final MLModuleBridge mlBridge;
    private final Map<String, AdvancedNeuron> neurons = new ConcurrentHashMap<>();
    private BrainState state = BrainState.IDLE;

    public IntegratedBrainOrchestrator(Path storagePath) throws Exception {
        this.persistence = new PersistenceSystem(storagePath);
        this.orchestrator = new DistributedOrchestrator(Runtime.getRuntime().availableProcessors());
        this.optimizer = new AdaptiveOptimizationEngine();
        this.mlBridge = new MLModuleBridge();
        hub.registerModule("orchestrator", this);
        loadInitialNeurons();
    }

    private void loadInitialNeurons() {
        for (int i = 0; i < 50; i++) {
            String id = "neuron_" + i;
            neurons.put(id, new AdvancedNeuron(id, "/virtual", NeuronType.PROCESSING));
        }
        log.info("{} neurônios carregados", neurons.size());
    }

    @Override
    public void receive(DataPacket packet) {
        log.info("Recebido: {} de {}", packet.dataType(), packet.sourceModule());
        // processar conforme tipo
        if ("optimization_suggestion".equals(packet.dataType())) {
            @SuppressWarnings("unchecked")
            Map<String, Double> suggestions = (Map<String, Double>) packet.payload();
            optimizer.applyOptimization(suggestions);
        }
    }

    public CompletableFuture<Map<String, Object>> processMarketData(Map<String, Object> marketData) {
        return CompletableFuture.supplyAsync(() -> {
            optimizer.recordMetric("market_data_size", marketData.size());
            // executar análise neural, quantum, etc.
            Map<String, Object> result = new HashMap<>();
            result.put("decision", "buy");
            result.put("confidence", 0.85);
            return result;
        }, orchestrator.getPool());
    }

    public void createCheckpoint() {
        Map<String, Object> state = new HashMap<>();
        state.put("neurons_count", neurons.size());
        state.put("brain_state", state.name());
        String id = persistence.createCheckpoint("manual", state, "backup");
        log.info("Checkpoint criado: {}", id);
    }

    public void shutdown() {
        hub.shutdown();
        orchestrator.shutdown();
        persistence.close();
    }

    public static void main(String[] args) throws Exception {
        IntegratedBrainOrchestrator brain = new IntegratedBrainOrchestrator(Path.of("./data"));
        brain.createCheckpoint();

        Map<String, Object> market = Map.of("symbol", "BTCUSDT", "price", 45000.0);
        CompletableFuture<Map<String, Object>> future = brain.processMarketData(market);
        future.thenAccept(result -> log.info("Resultado: {}", result))
              .exceptionally(ex -> { log.error("Erro", ex); return null; })
              .join();

        brain.shutdown();
    }
}
package br.com.vhalinor.iag.integration;

import br.com.vhalinor.iag.core.DataPacket;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class IntegrationHub {
    private static final Logger log = LoggerFactory.getLogger(IntegrationHub.class);
    private final Map<String, Object> modules = new ConcurrentHashMap<>();
    private final BlockingQueue<DataPacket> dataQueue = new LinkedBlockingQueue<>(10000);
    private final Cache<String, Object> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .maximumSize(1000)
            .build();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean running = true;

    public IntegrationHub() {
        startProcessor();
    }

    private void startProcessor() {
        executor.submit(() -> {
            while (running) {
                try {
                    DataPacket packet = dataQueue.take();
                    routePacket(packet);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public boolean registerModule(String name, Object module) {
        modules.put(name, module);
        log.info("Módulo registrado: {}", name);
        return true;
    }

    public void sendData(DataPacket packet) {
        if (packet.isExpired()) {
            log.debug("Pacote expirado: {}", packet.id());
            return;
        }
        dataQueue.offer(packet);
    }

    private void routePacket(DataPacket packet) {
        for (String target : packet.targetModules()) {
            Object module = modules.get(target);
            if (module != null && module instanceof DataReceiver) {
                try {
                    ((DataReceiver) module).receive(packet);
                } catch (Exception e) {
                    log.error("Erro ao enviar para {}: {}", target, e.getMessage());
                }
            }
        }
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
    }

    public interface DataReceiver {
        void receive(DataPacket packet);
    }
}
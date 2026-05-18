package br.com.vhalinor.iag.core;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedNeuron {
    private static final Logger log = LoggerFactory.getLogger(AdvancedNeuron.class);

    private final String id;
    private final String filePath;
    private final NeuronType type;
    private double activationThreshold;
    private double currentActivation;
    private final Set<String> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Instant lastFired;
    private double memoryWeight = 1.0;
    private double learningRate = 0.01;
    private final Deque<Double> activationHistory = new ConcurrentLinkedDeque<>();
    private final AtomicLong fireCount = new AtomicLong(0);
    private double importanceScore;

    public AdvancedNeuron(String id, String filePath, NeuronType type) {
        this.id = id;
        this.filePath = filePath;
        this.type = type;
        this.activationThreshold = type.defaultThreshold;
        this.importanceScore = type.importance;
    }

    public void activate(double stimulus) {
        double delta = stimulus * learningRate;
        currentActivation = Math.min(1.0, Math.max(0.0, currentActivation + delta));
        activationHistory.addLast(currentActivation);
        if (activationHistory.size() > 1000) activationHistory.pollFirst();

        if (currentActivation >= activationThreshold) {
            fireCount.incrementAndGet();
            lastFired = Instant.now();
            log.debug("Neurônio {} disparou (ativação={})", id, currentActivation);
        }
    }

    public void learn(double error) {
        learningRate *= (1 + error * 0.2);
        learningRate = Math.min(0.1, Math.max(0.001, learningRate));
        activationThreshold *= (1 - error * 0.01);
        activationThreshold = Math.min(0.9, Math.max(0.1, activationThreshold));
    }

    // getters, setters, etc.
}
package br.com.vhalinor.iag.orchestration;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AdaptiveOptimizationEngine {
    private static final Logger log = LoggerFactory.getLogger(AdaptiveOptimizationEngine.class);
    private final Map<String, DescriptiveStatistics> metrics = new ConcurrentHashMap<>();
    private final Map<String, Double> parameters = new ConcurrentHashMap<>();
    private final Map<String, double[]> bounds = new HashMap<>();

    public AdaptiveOptimizationEngine() {
        parameters.put("learningRate", 0.01);
        parameters.put("synapticPlasticity", 0.05);
        bounds.put("learningRate", new double[]{0.0001, 0.1});
        bounds.put("synapticPlasticity", new double[]{0.001, 0.2});
    }

    public void recordMetric(String name, double value) {
        metrics.computeIfAbsent(name, k -> new DescriptiveStatistics()).addValue(value);
        log.debug("Métrica {} = {}", name, value);
    }

    public Map<String, Double> suggestOptimization() {
        Map<String, Double> suggestions = new HashMap<>();
        DescriptiveStatistics perf = metrics.get("accuracy");
        if (perf != null && perf.getN() > 20) {
            double meanAccuracy = perf.getMean();
            if (meanAccuracy < 0.7) {
                double lr = parameters.get("learningRate");
                double newLr = Math.min(bounds.get("learningRate")[1], lr * 1.1);
                suggestions.put("learningRate", newLr);
                log.info("Sugestão: aumentar learningRate para {}", newLr);
            }
        }
        return suggestions;
    }

    public void applyOptimization(Map<String, Double> changes) {
        changes.forEach((param, value) -> {
            double[] bound = bounds.get(param);
            if (bound != null) {
                value = Math.min(bound[1], Math.max(bound[0], value));
            }
            parameters.put(param, value);
            log.info("Parâmetro {} alterado para {}", param, value);
        });
    }

    public Map<String, Double> getCurrentParameters() {
        return new HashMap<>(parameters);
    }
}
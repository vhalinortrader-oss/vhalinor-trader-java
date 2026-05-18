package br.com.vhalinor.iag.core;

import java.time.Duration;

public enum NeuronType {
    SENSORY("sensory", "📡", 0.3, 1.0),
    PROCESSING("processing", "⚙️", 0.5, 0.8),
    QUANTUM("quantum", "⚛️", 0.7, 2.0),
    TRADING("trading", "💰", 0.7, 1.5),
    OUTPUT("output", "📤", 0.3, 0.9);

    public final String label;
    public final String icon;
    public final double defaultThreshold;
    public final double importance;

    NeuronType(String label, String icon, double defaultThreshold, double importance) {
        this.label = label;
        this.icon = icon;
        this.defaultThreshold = defaultThreshold;
        this.importance = importance;
    }
}

public enum BrainState {
    IDLE("idle", "💤", "Em repouso"),
    PROCESSING("processing", "⚙️", "Processamento ativo"),
    SECURITY_SCAN("security", "🛡️", "Varredura de segurança");

    public final String label;
    public final String icon;
    public final String description;

    BrainState(String label, String icon, String description) {
        this.label = label;
        this.icon = icon;
        this.description = description;
    }
}

public enum DataPriority {
    LOW(0, "Baixa", "🟢"),
    MEDIUM(1, "Média", "🟡"),
    HIGH(2, "Alta", "🟠"),
    CRITICAL(3, "Crítica", "🔴");

    public final int level;
    public final String label;
    public final String icon;

    DataPriority(int level, String label, String icon) {
        this.level = level;
        this.label = label;
        this.icon = icon;
    }
}

public record DataPacket(
    String id,
    String sourceModule,
    java.util.Set<String> targetModules,
    String dataType,
    Object payload,
    java.time.Instant timestamp,
    DataPriority priority,
    Duration ttl
) {
    public DataPacket {
        if (id == null || id.isBlank()) {
            id = java.util.UUID.randomUUID().toString().substring(0, 12);
        }
        if (timestamp == null) timestamp = java.time.Instant.now();
        if (priority == null) priority = DataPriority.MEDIUM;
    }

    public boolean isExpired() {
        return ttl != null && java.time.Instant.now().isAfter(timestamp.plus(ttl));
    }
}
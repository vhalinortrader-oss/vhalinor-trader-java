package com.vhalinor.ai.core;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Gerenciador de Estado Neural - Persiste e carrega estado do sistema neural
 * Versão: 1.0
 * Responsável por: Serialização, Banco de Dados de Metadados, Versionamento de Estado
 */
public class NeuralStateManager {
    private static final Logger LOG = Logger.getLogger(NeuralStateManager.class.getName());
    
    private final Path stateDirectory;
    private final Path databasePath;
    private Connection dbConnection;
    private final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private final ScheduledExecutorService autoSaveScheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean isRunning = false;

    /**
     * Estados possíveis do sistema neural
     */
    public enum NeuralState {
        INITIALIZING("initializing"),
        LEARNING("learning"),
        PROCESSING("processing"),
        RESTING("resting"),
        CORRUPTED("corrupted"),
        ARCHIVED("archived");

        public final String value;
        NeuralState(String value) { this.value = value; }
    }

    /**
     * Metadados do Estado Neural
     */
    public static class SystemStateMetadata {
        public String stateId;
        public LocalDateTime timestamp;
        public NeuralState state;
        public int version;
        public String hash;
        public Map<String, Object> statistics;
        public String description;
        public boolean isValid;

        public SystemStateMetadata(String stateId) {
            this.stateId = stateId;
            this.timestamp = LocalDateTime.now();
            this.state = NeuralState.INITIALIZING;
            this.version = 1;
            this.statistics = new HashMap<>();
            this.isValid = true;
        }

        @Override
        public String toString() {
            return String.format("StateMetadata{id=%s, ts=%s, state=%s, v=%d, valid=%s}", 
                stateId, timestamp, state.value, version, isValid);
        }
    }

    /**
     * Snapshot de Estado Neural
     */
    public static class NeuralSnapshot implements Serializable {
        public String id;
        public LocalDateTime timestamp;
        public Map<String, Object> neuronStates;
        public Map<String, Object> synapticWeights;
        public Map<String, Object> learningMetrics;
        public Map<String, Object> memoryState;
        public List<String> recentActions;
        public double energyLevel;
        public int cycleCount;

        public NeuralSnapshot(String id) {
            this.id = id;
            this.timestamp = LocalDateTime.now();
            this.neuronStates = new ConcurrentHashMap<>();
            this.synapticWeights = new ConcurrentHashMap<>();
            this.learningMetrics = new ConcurrentHashMap<>();
            this.memoryState = new ConcurrentHashMap<>();
            this.recentActions = Collections.synchronizedList(new ArrayList<>());
            this.energyLevel = 100.0;
            this.cycleCount = 0;
        }

        public String toJSON() {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"id\":\"").append(id).append("\",");
            json.append("\"timestamp\":\"").append(timestamp).append("\",");
            json.append("\"energy\":").append(energyLevel).append(",");
            json.append("\"cycles\":").append(cycleCount).append(",");
            json.append("\"neurons\":").append(neuronStates.size()).append(",");
            json.append("\"synapses\":").append(synapticWeights.size()).append(",");
            json.append("\"actions\":").append(recentActions.size());
            json.append("}");
            return json.toString();
        }
    }

    public NeuralStateManager(String stateDirectoryPath) throws Exception {
        this.stateDirectory = Paths.get(stateDirectoryPath);
        this.databasePath = stateDirectory.resolve("metadata.db");
        
        if (!Files.exists(stateDirectory)) {
            Files.createDirectories(stateDirectory);
            LOG.info("📁 State directory created: " + stateDirectory);
        }
        
        initializeDatabase();
        LOG.info("✅ NeuralStateManager initialized");
    }

    /**
     * Inicializa banco de dados SQLite para metadados
     */
    private void initializeDatabase() throws SQLException {
        String url = "jdbc:sqlite:" + databasePath.toString();
        dbConnection = DriverManager.getConnection(url);
        dbConnection.setAutoCommit(true);
        
        try (Statement stmt = dbConnection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS neural_states (" +
                "  state_id TEXT PRIMARY KEY," +
                "  timestamp TEXT NOT NULL," +
                "  state_type TEXT NOT NULL," +
                "  version INTEGER DEFAULT 1," +
                "  hash TEXT," +
                "  is_valid BOOLEAN DEFAULT 1," +
                "  description TEXT," +
                "  created_at TEXT NOT NULL," +
                "  file_path TEXT" +
                ")"
            );
            
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS state_metrics (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  state_id TEXT NOT NULL," +
                "  metric_name TEXT NOT NULL," +
                "  metric_value REAL," +
                "  timestamp TEXT," +
                "  FOREIGN KEY(state_id) REFERENCES neural_states(state_id)" +
                ")"
            );
            
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS state_versions (" +
                "  version_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  state_id TEXT NOT NULL," +
                "  version_number INTEGER," +
                "  file_path TEXT," +
                "  hash TEXT," +
                "  timestamp TEXT," +
                "  FOREIGN KEY(state_id) REFERENCES neural_states(state_id)" +
                ")"
            );
        }
        
        LOG.info("✅ Database initialized: " + databasePath);
    }

    /**
     * Salva snapshot de estado neural
     */
    public String saveSnapshot(NeuralSnapshot snapshot) throws Exception {
        // Serializar snapshot
        String filename = String.format("system_state_%s_%s.ser", 
            timestampFormat.format(snapshot.timestamp),
            UUID.randomUUID().toString().substring(0, 8));
        
        Path filepath = stateDirectory.resolve(filename);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath.toFile()))) {
            oos.writeObject(snapshot);
        }
        
        // Salvar metadados no banco de dados
        String hash = computeHash(snapshot);
        SystemStateMetadata metadata = new SystemStateMetadata(snapshot.id);
        metadata.hash = hash;
        metadata.description = "Neural state snapshot";
        
        saveMetadata(metadata, filepath.toString());
        
        LOG.info("💾 Snapshot saved: " + filename);
        return snapshot.id;
    }

    /**
     * Carrega snapshot de estado neural
     */
    public NeuralSnapshot loadSnapshot(String snapshotId) throws Exception {
        String query = "SELECT file_path FROM neural_states WHERE state_id = ?";
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setString(1, snapshotId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String filePath = rs.getString("file_path");
                return loadSnapshotFromFile(filePath);
            }
        }
        
        LOG.warning("⚠️ Snapshot not found: " + snapshotId);
        return null;
    }

    /**
     * Carrega snapshot de um arquivo
     */
    private NeuralSnapshot loadSnapshotFromFile(String filePath) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return (NeuralSnapshot) ois.readObject();
        }
    }

    /**
     * Salva metadados no banco de dados
     */
    private void saveMetadata(SystemStateMetadata metadata, String filePath) throws SQLException {
        String insert = "INSERT OR REPLACE INTO neural_states " +
            "(state_id, timestamp, state_type, version, hash, is_valid, file_path, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(insert)) {
            pstmt.setString(1, metadata.stateId);
            pstmt.setString(2, metadata.timestamp.toString());
            pstmt.setString(3, metadata.state.value);
            pstmt.setInt(4, metadata.version);
            pstmt.setString(5, metadata.hash);
            pstmt.setBoolean(6, metadata.isValid);
            pstmt.setString(7, filePath);
            pstmt.setString(8, LocalDateTime.now().toString());
            pstmt.executeUpdate();
        }
    }

    /**
     * Salva métrica de estado
     */
    public void saveMetric(String stateId, String metricName, double value) throws SQLException {
        String insert = "INSERT INTO state_metrics (state_id, metric_name, metric_value, timestamp) " +
            "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(insert)) {
            pstmt.setString(1, stateId);
            pstmt.setString(2, metricName);
            pstmt.setDouble(3, value);
            pstmt.setString(4, LocalDateTime.now().toString());
            pstmt.executeUpdate();
        }
    }

    /**
     * Recupera histórico de métricas
     */
    public List<Double> getMetricHistory(String stateId, String metricName, int limit) throws SQLException {
        List<Double> history = new ArrayList<>();
        String query = "SELECT metric_value FROM state_metrics " +
            "WHERE state_id = ? AND metric_name = ? " +
            "ORDER BY timestamp DESC LIMIT ?";
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setString(1, stateId);
            pstmt.setString(2, metricName);
            pstmt.setInt(3, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(rs.getDouble("metric_value"));
            }
        }
        
        Collections.reverse(history);
        return history;
    }

    /**
     * Lista todos os estados salvos
     */
    public List<SystemStateMetadata> listAllStates() throws SQLException {
        List<SystemStateMetadata> states = new ArrayList<>();
        String query = "SELECT state_id, timestamp, state_type, version, hash, is_valid, description " +
            "FROM neural_states ORDER BY timestamp DESC";
        
        try (Statement stmt = dbConnection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                SystemStateMetadata metadata = new SystemStateMetadata(rs.getString("state_id"));
                metadata.timestamp = LocalDateTime.parse(rs.getString("timestamp"));
                metadata.state = NeuralState.valueOf(rs.getString("state_type").toUpperCase());
                metadata.version = rs.getInt("version");
                metadata.hash = rs.getString("hash");
                metadata.isValid = rs.getBoolean("is_valid");
                metadata.description = rs.getString("description");
                states.add(metadata);
            }
        }
        
        return states;
    }

    /**
     * Valida integridade de snapshot
     */
    public boolean validateSnapshot(String snapshotId) throws Exception {
        NeuralSnapshot snapshot = loadSnapshot(snapshotId);
        if (snapshot == null) return false;
        
        String query = "SELECT hash FROM neural_states WHERE state_id = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setString(1, snapshotId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("hash");
                String computedHash = computeHash(snapshot);
                return storedHash.equals(computedHash);
            }
        }
        
        return false;
    }

    /**
     * Computa hash de checksum do snapshot
     */
    private String computeHash(NeuralSnapshot snapshot) {
        String data = snapshot.toJSON();
        int hash = data.hashCode();
        return Integer.toHexString(Math.abs(hash));
    }

    /**
     * Inicia auto-save periódico
     */
    public void startAutoSave(NeuralSnapshot snapshot, long intervalSeconds) {
        isRunning = true;
        autoSaveScheduler.scheduleAtFixedRate(() -> {
            try {
                if (isRunning) {
                    saveSnapshot(snapshot);
                    LOG.fine("⏱️ Auto-save executed");
                }
            } catch (Exception e) {
                LOG.warning("Auto-save failed: " + e.getMessage());
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * Para auto-save
     */
    public void stopAutoSave() {
        isRunning = false;
        autoSaveScheduler.shutdown();
        try {
            if (!autoSaveScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                autoSaveScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            autoSaveScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOG.info("Auto-save stopped");
    }

    /**
     * Exporta estado para JSON
     */
    public String exportStateAsJSON(String stateId) throws Exception {
        NeuralSnapshot snapshot = loadSnapshot(stateId);
        if (snapshot == null) return null;
        
        return snapshot.toJSON();
    }

    /**
     * Remove estado antigo
     */
    public void removeOldStates(int daysOld) throws SQLException {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysOld);
        String delete = "DELETE FROM neural_states WHERE timestamp < ?";
        
        try (PreparedStatement pstmt = dbConnection.prepareStatement(delete)) {
            pstmt.setString(1, threshold.toString());
            int deleted = pstmt.executeUpdate();
            LOG.info("🗑️ Removed " + deleted + " old states");
        }
    }

    /**
     * Fecha gerenciador e recursos
     */
    public void close() throws SQLException {
        stopAutoSave();
        if (dbConnection != null && !dbConnection.isClosed()) {
            dbConnection.close();
            LOG.info("💾 Database connection closed");
        }
    }

    /**
     * Exemplo de uso
     */
    public static void main(String[] args) throws Exception {
        NeuralStateManager manager = new NeuralStateManager("./neural_state");
        
        // Criar snapshot
        NeuralSnapshot snapshot = new NeuralSnapshot("neural_001");
        snapshot.neuronStates.put("sensory_1", 0.8);
        snapshot.neuronStates.put("processing_1", 0.6);
        snapshot.energyLevel = 95.0;
        snapshot.cycleCount = 1500;
        
        // Salvar
        String stateId = manager.saveSnapshot(snapshot);
        System.out.println("✅ State saved: " + stateId);
        
        // Listar todos os estados
        System.out.println("\n📋 All saved states:");
        for (SystemStateMetadata meta : manager.listAllStates()) {
            System.out.println("  " + meta);
        }
        
        // Validar
        boolean isValid = manager.validateSnapshot(stateId);
        System.out.println("\n✓ State valid: " + isValid);
        
        // Salvar métrica
        manager.saveMetric(stateId, "energy", 95.0);
        manager.saveMetric(stateId, "cycles", 1500);
        
        // Recuperar histórico
        System.out.println("\n📊 Energy history:");
        for (Double value : manager.getMetricHistory(stateId, "energy", 10)) {
            System.out.println("  " + value);
        }
        
        manager.close();
    }
}

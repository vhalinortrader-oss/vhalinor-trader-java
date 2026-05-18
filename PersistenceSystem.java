package br.com.vhalinor.iag.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PersistenceSystem {
    private static final Logger log = LoggerFactory.getLogger(PersistenceSystem.class);
    private final HikariDataSource dataSource;
    private final Path storagePath;
    private final ObjectMapper mapper = new ObjectMapper();
    private boolean compression = true;

    public PersistenceSystem(Path storagePath) throws SQLException {
        this.storagePath = storagePath.toAbsolutePath();
        this.storagePath.toFile().mkdirs();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + this.storagePath.resolve("vhalinor.db"));
        config.setMaximumPoolSize(5);
        this.dataSource = new HikariDataSource(config);
        initTables();
    }

    private void initTables() throws SQLException {
        try (Statement stmt = dataSource.getConnection().createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS checkpoints (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    compressed BOOLEAN,
                    size_bytes INTEGER,
                    tags TEXT
                )
            """);
        }
    }

    public String createCheckpoint(String name, Object state, String... tags) {
        String id = UUID.randomUUID().toString().substring(0, 12);
        byte[] data = serialize(state);
        byte[] stored = compression ? compress(data) : data;
        Path file = storagePath.resolve(id + ".chk");
        try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
            fos.write(stored);
        } catch (IOException e) {
            log.error("Falha ao salvar checkpoint", e);
            return null;
        }
        try (Connection conn = dataSource.getConnection(); 
        PreparedStatement ps = conn.prepareStatement("INSERT INTO checkpoints(id,name,created_at,compressed,size_bytes,tags) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, Instant.now().toString());
            ps.setBoolean(4, compression);
            ps.setInt(5, stored.length);
            ps.setString(6, String.join(",", tags));
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Erro no metadata", e);
        }
        log.info("Checkpoint {} criado ({} bytes)", id, stored.length);
        return id;
    }

    public Optional<Object> restoreCheckpoint(String id) {
        Path file = storagePath.resolve(id + ".chk");
        if (!file.toFile().exists()) return Optional.empty();
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            byte[] data = fis.readAllBytes();
            byte[] decompressed = compression ? decompress(data) : data;
            return Optional.of(deserialize(decompressed));
        } catch (Exception e) {
            log.error("Falha ao restaurar checkpoint", e);
            return Optional.empty();
        }
    }

    private byte[] serialize(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object deserialize(byte[] bytes) {
        try {
            return mapper.readValue(bytes, Object.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] compress(byte[] input) {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(input);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
        byte[] buf = new byte[8192];
        while (!deflater.finished()) {
            int len = deflater.deflate(buf);
            bos.write(buf, 0, len);
        }
        return bos.toByteArray();
    }

    private byte[] decompress(byte[] compressed) {
        Inflater inflater = new Inflater();
        inflater.setInput(compressed);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);
        byte[] buf = new byte[8192];
        try {
            while (!inflater.finished()) {
                int len = inflater.inflate(buf);
                bos.write(buf, 0, len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    public void setCompressionEnabled(boolean enabled) {
        this.compression = enabled;
    }

    public void close() {
        dataSource.close();
    }
}
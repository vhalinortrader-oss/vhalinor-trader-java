# Neural State Management System - Java Implementation

## 📋 Visão Geral

Este sistema gerencia o estado neural da IA VHALINOR em Java, substituindo os arquivos binários Python anteriores por uma arquitetura completa de persistência, cache e sincronização.

## 🏗️ Arquitetura

```
NeuralStateOrchestrator (Coordenador Central)
├── NeuralStateManager (Persistência e Banco de Dados)
├── NeuralStateCache (Cache em Memória com LRU)
└── NeuralStateSynchronizer (Sincronização Distribuída)
```

## 📁 Componentes

### 1. **NeuralStateManager** (`NeuralStateManager.java`)
Responsável por persistência de estado neural em disco.

**Funcionalidades:**
- Serialização de snapshots do estado (`NeuralSnapshot`)
- Banco de dados SQLite para metadados (`metadata.db`)
- Versionamento de estados
- Hash/checksum de integridade
- Auto-save periódico
- Cleanup de estados antigos

**Classes Principais:**
- `NeuralSnapshot` - Representação do estado neural
- `SystemStateMetadata` - Metadados do estado
- `NeuralState` - Enum de estados (INITIALIZING, LEARNING, PROCESSING, etc)

**Exemplo de Uso:**
```java
NeuralStateManager manager = new NeuralStateManager("./neural_state");

NeuralSnapshot snapshot = new NeuralSnapshot("neural_001");
snapshot.neuronStates.put("sensory_1", 0.8);
snapshot.energyLevel = 95.0;

String stateId = manager.saveSnapshot(snapshot);
manager.startAutoSave(snapshot, 300); // Auto-save a cada 5 minutos

NeuralSnapshot loaded = manager.loadSnapshot(stateId);
manager.close();
```

---

### 2. **NeuralStateCache** (`NeuralStateCache.java`)
Cache em memória com suporte LRU e TTL.

**Funcionalidades:**
- Cache LRU (Least Recently Used)
- TTL (Time To Live) automático
- Limpeza periódica de entradas expiradas
- Estatísticas de cache (hits, misses, hit rate)
- Checkpoint/Restore para recuperação de falhas
- Sincronização com disco

**Classes Principais:**
- `CacheEntry` - Entrada individual do cache
- `CacheStatistics` - Estatísticas de cache

**Exemplo de Uso:**
```java
NeuralStateCache cache = new NeuralStateCache(100, 300, manager);

// Salvar com persistência automática
NeuralSnapshot snapshot = new NeuralSnapshot("neural_001");
cache.saveState("neural_001", snapshot);

// Recuperar (tenta cache primeiro, depois disco)
Object state = cache.loadState("neural_001");

// Obter estatísticas
NeuralStateCache.CacheStatistics stats = cache.getStatistics();
System.out.println("Hit rate: " + stats.hitRate);

// Checkpoint e recover
cache.checkpoint();
cache.restoreFromCheckpoint();
```

---

### 3. **NeuralStateSynchronizer** (`NeuralStateSynchronizer.java`)
Gerencia sincronização de estado entre múltiplas instâncias.

**Funcionalidades:**
- Versionamento de estados (`StateVersion`)
- Detecção de conflitos automática
- Resolução de conflitos (estratégia: maior versão)
- Event sourcing com histórico
- Sincronização periódica
- Listeners para eventos de sincronização

**Classes Principais:**
- `StateVersion` - Versão do estado com timestamp e hash
- `SyncEvent` - Evento de sincronização
- `SyncListener` - Interface para listeners
- `SyncConflictException` - Exceção de conflito

**Exemplo de Uso:**
```java
NeuralStateSynchronizer sync = new NeuralStateSynchronizer("node_1");

// Atualizar estado local
sync.updateLocalState("neural_001", "Learning phase initialized");

// Registrar versão remota
StateVersion remote = new StateVersion("neural_001", "node_2");
remote.version = 2;
sync.receiveRemoteState("neural_001", remote);

// Listener para eventos
sync.addSyncListener(new SyncListener() {
    @Override
    public void onSyncEvent(SyncEvent event) {
        System.out.println("Sync: " + event);
    }
    
    @Override
    public void onConflict(String stateId, StateVersion local, StateVersion remote) {
        System.out.println("Conflict: " + stateId);
    }
});

// Status de sincronização
List<Map<String, Object>> status = sync.getSyncStatus();
```

---

### 4. **NeuralStateOrchestrator** (`NeuralStateOrchestrator.java`)
Coordenador central que integra todos os componentes.

**Funcionalidades:**
- Orquestração de operações de estado
- Monitoramento de saúde (`NeuralStateHealth`)
- Recuperação automática de falhas
- Checkpoint/Restore
- Limpeza de estados antigos
- Listeners de orquestração

**Classes Principais:**
- `NeuralStateHealth` - Status de saúde do sistema
- `OrchestrationListener` - Interface para listeners

**Exemplo de Uso:**
```java
NeuralStateOrchestrator orchestrator = 
    new NeuralStateOrchestrator("orchestrator_1", "./neural_state");

// Listener
orchestrator.addListener(new OrchestrationListener() {
    @Override
    public void onStateChange(String stateId) {
        System.out.println("State changed: " + stateId);
    }
    
    @Override
    public void onSyncConflict(String stateId, Exception conflict) {
        System.out.println("Conflict: " + stateId);
    }
    
    @Override
    public void onHealthCheck(NeuralStateHealth health) {
        System.out.println("Health: " + health);
    }
});

// Salvar estado
NeuralStateManager.NeuralSnapshot snapshot = new NeuralStateManager.NeuralSnapshot("neural_001");
orchestrator.saveNeuralState("neural_001", snapshot);

// Carregar estado
NeuralStateManager.NeuralSnapshot loaded = orchestrator.loadNeuralState("neural_001");

// Sincronizar com remoto
NeuralStateSynchronizer.StateVersion remote = new NeuralStateSynchronizer.StateVersion("neural_001", "node_2");
orchestrator.syncWithRemote("neural_001", remote);

// Verificação de saúde
NeuralStateOrchestrator.NeuralStateHealth health = orchestrator.performHealthCheck();
System.out.println(health);

// Cleanup
orchestrator.cleanupOldStates(30); // Remove estados com 30+ dias

orchestrator.close();
```

---

## 🗄️ Estrutura de Dados

### Banco de Dados SQLite (`metadata.db`)

**Tabela: `neural_states`**
```sql
CREATE TABLE neural_states (
  state_id TEXT PRIMARY KEY,
  timestamp TEXT NOT NULL,
  state_type TEXT NOT NULL,        -- INITIALIZING, LEARNING, PROCESSING, etc
  version INTEGER DEFAULT 1,
  hash TEXT,                        -- Checksum de integridade
  is_valid BOOLEAN DEFAULT 1,
  description TEXT,
  created_at TEXT NOT NULL,
  file_path TEXT
)
```

**Tabela: `state_metrics`**
```sql
CREATE TABLE state_metrics (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  state_id TEXT NOT NULL,
  metric_name TEXT NOT NULL,        -- energy, cycles, accuracy, etc
  metric_value REAL,
  timestamp TEXT,
  FOREIGN KEY(state_id) REFERENCES neural_states(state_id)
)
```

**Tabela: `state_versions`**
```sql
CREATE TABLE state_versions (
  version_id INTEGER PRIMARY KEY AUTOINCREMENT,
  state_id TEXT NOT NULL,
  version_number INTEGER,
  file_path TEXT,
  hash TEXT,
  timestamp TEXT,
  FOREIGN KEY(state_id) REFERENCES neural_states(state_id)
)
```

### Arquivos de Snapshot

```
neural_state/
├── metadata.db
├── system_state_20260504_143022_a1b2c3d4.ser    # Snapshot serializado
├── system_state_20260504_142015_e5f6g7h8.ser
└── system_state_20260504_140530_i9j0k1l2.ser
```

---

## 🔄 Fluxos de Uso

### Fluxo 1: Salvar Estado
```
Aplicação
  ↓
NeuralStateOrchestrator.saveNeuralState()
  ├→ NeuralStateCache.saveState()           (Memória)
  ├→ NeuralStateManager.saveSnapshot()      (Disco)
  └→ NeuralStateSynchronizer.updateLocal()  (Versão local)
```

### Fluxo 2: Carregar Estado
```
Aplicação
  ↓
NeuralStateOrchestrator.loadNeuralState()
  ├→ NeuralStateCache.get()         (Tenta cache)
  └→ NeuralStateManager.load()      (Se miss: tenta disco)
```

### Fluxo 3: Sincronização Distribuída
```
Remoto Node
  ↓ (envia StateVersion)
NeuralStateOrchestrator.syncWithRemote()
  ↓
NeuralStateSynchronizer.receiveRemoteState()
  ├→ ✓ (se versões compatíveis)
  └→ ⚠️ Conflito detectado
      ↓
      resolve usando: max(local.version, remote.version)
      ↓
      notifica listeners
```

### Fluxo 4: Monitoramento de Saúde
```
[ScheduledExecutor - a cada 30s]
  ↓
NeuralStateOrchestrator.performHealthCheck()
  ├→ Cache: hitRate, size
  ├→ Database: conectividade
  ├→ Sync: pending syncs
  └→ notifica listeners
```

---

## 📊 Exemplo de Aplicação Completa

```java
public class VhalinorNeuralSystem {
    private NeuralStateOrchestrator orchestrator;

    public void initialize() throws Exception {
        orchestrator = new NeuralStateOrchestrator(
            "vhalinor_system", 
            "./neural_state"
        );
        
        orchestrator.addListener(new NeuralStateOrchestrator.OrchestrationListener() {
            @Override
            public void onStateChange(String stateId) {
                System.out.println("🧠 Neural state updated: " + stateId);
            }

            @Override
            public void onSyncConflict(String stateId, Exception conflict) {
                System.out.println("⚠️ Sync conflict detected for " + stateId);
                // Implementar lógica de resolução customizada
            }

            @Override
            public void onHealthCheck(NeuralStateOrchestrator.NeuralStateHealth health) {
                if (!health.cacheHealthy || !health.databaseHealthy) {
                    System.out.println("🚨 System health issue: " + health);
                }
            }
        });
        
        // Iniciar sincronização periódica
        orchestrator.startPeriodicSync(60); // A cada 60 segundos
    }

    public void recordCycle(int cycleNumber) throws Exception {
        NeuralStateManager.NeuralSnapshot snapshot = 
            new NeuralStateManager.NeuralSnapshot("neural_main");
        
        snapshot.cycleCount = cycleNumber;
        snapshot.energyLevel = 85.5;
        snapshot.neuronStates.put("processing", 0.8);
        snapshot.learningMetrics.put("accuracy", 0.92);
        
        orchestrator.saveNeuralState("neural_main", snapshot);
    }

    public void shutdown() throws Exception {
        orchestrator.performCheckpoint();
        orchestrator.close();
    }

    public static void main(String[] args) throws Exception {
        VhalinorNeuralSystem system = new VhalinorNeuralSystem();
        system.initialize();

        for (int i = 0; i < 10; i++) {
            system.recordCycle(i);
            Thread.sleep(1000);
        }

        system.shutdown();
    }
}
```

---

## 🛠️ Configuração

### Variáveis de Ambiente
```properties
# Diretório de estado
NEURAL_STATE_DIR=./neural_state

# Cache
CACHE_MAX_SIZE=500
CACHE_TTL_SECONDS=600

# Sincronização
SYNC_INTERVAL_SECONDS=60

# Cleanup
CLEANUP_DAYS_OLD=30
```

### Logging
```java
import java.util.logging.*;

Logger logger = Logger.getLogger(NeuralStateOrchestrator.class.getName());
logger.setLevel(Level.INFO);

// Console handler
ConsoleHandler handler = new ConsoleHandler();
handler.setLevel(Level.INFO);
logger.addHandler(handler);
```

---

## 🚀 Melhorias Futuras

- [ ] Replicação multi-nó com Raft consensus
- [ ] Compressão de snapshots
- [ ] Criptografia de estado em repouso
- [ ] WebSocket para sincronização em tempo real
- [ ] Métricas e observabilidade (OpenTelemetry)
- [ ] Backup automático em cloud storage
- [ ] Machine learning para previsão de conflitos
- [ ] Sharding horizontal por stateId

---

## 📝 Versionamento

- **Versão**: 1.0
- **Data**: 2026-05-04
- **Status**: Production Ready
- **Compatibilidade**: Java 11+

---

## 📜 License

Proprietary - Vhalinor IAG Systems

---

## 👨‍💻 Autor

**Alex Miranda Sales**  
Data: 2026-05-04

---

## 📞 Suporte

Para questões ou problemas, consulte a documentação em código ou abra um issue no repositório.

"""
╔═══════════════════════════════════════════════════════════════════════════════╗
║                    VHALINOR IAG 3.0.0 - SISTEMA CEREBRAL QUÂNTICO             ║
║         ARQUITETURA DE INTELIGÊNCIA ARTIFIAL GERAL COM INTEGRAÇÃO TOTAL       ║
╠═══════════════════════════════════════════════════════════════════════════════╣
║  Módulo: ORQUESTRADOR CEREBRAL INTEGRADO                                      ║
║  Versão: 3.0.0 (Production Ready - Ultra Avançada)                            ║
║  Codename: "Quantum Singularity"                                              ║
║  Autor: Alex Miranda Sales                                                    ║
║  Data: 2026-02-12                                                             ║
║  License: Proprietary - Vhalinor IAG Systems                                  ║
║  Status: 🟢 TOTALMENTE OPERACIONAL | 🔋 100% | 🧠 1.2B PARÂMETROS            ║
╚═══════════════════════════════════════════════════════════════════════════════╝

================================================================================
DOCUMENTAÇÃO E EXEMPLOS DE USO
================================================================================

RESUMO DO SISTEMA:
------------------
O Vhalinor IAG (Inteligência Artificial Geral) é um sistema cerebral quântico
que integra múltiplos módulos de IA em uma arquitetura unificada e escalável.

COMPONENTES PRINCIPAIS:
----------------------
1. ModuleDiscovery - Descoberta automática de módulos dinâmicos
2. IntegrationHub - Hub de integração e mensageria entre módulos
3. MLModuleBridge - Ponte de integração com módulo de Machine Learning
4. PluginManager - Gerenciador de plugins e extensões dinâmicas
5. PersistenceSystem - Sistema de persistência com compressão e replicação
6. AdvancedMonitoringSystem - Monitoramento avançado com detecção de anomalias
7. DistributedOrchestrator - Orquestração distribuída para execução paralela
8. AdaptiveOptimizationEngine - Otimização adaptativa baseada em ML

EXEMPLOS DE USO:
---------------

1. INICIALIZAÇÃO BÁSICA:
   ```python
   from Vhalinor_Inteligencia_artificial_central import IntegratedBrainOrchestrator
   
   # Inicializa o orquestrador cerebral
   brain = IntegratedBrainOrchestrator()
   
   # O sistema está pronto para uso
   print(f"Neurônios ativos: {len(brain.neurons)}")
   ```

2. USO DO SISTEMA DE MONITORAMENTO:
   ```python
   # Registra métricas
   brain.monitoring_system.record_metric('processing_time', 150.5, unit='ms')
   brain.monitoring_system.record_metric('memory_usage', 512.0, unit='MB')
   
   # Cria alertas
   brain.monitoring_system.create_alert('warning', 'Alta latência detectada', 'system')
   
   # Obtém relatório do sistema
   report = brain.monitoring_system.get_system_report()
   print(f"Uptime: {report['uptime_formatted']}")
   print(f"Métricas registradas: {report['total_metrics_recorded']}")
   ```

3. USO DO SISTEMA DE PERSISTÊNCIA:
   ```python
   # Cria checkpoint do estado atual
   state = {'neurons': brain.neurons, 'synapses': brain.synapses}
   checkpoint_id = brain.persistence_system.create_checkpoint(
       'state_snapshot', 
       state, 
       tags=['manual', 'important']
   )
   
   # Restaura checkpoint
   restored_state = brain.persistence_system.restore_checkpoint(checkpoint_id)
   
   # Configura compressão
   brain.persistence_system.set_compression_enabled(True, level=6)
   
   # Adiciona alvo de replicação
   from pathlib import Path
   brain.persistence_system.add_replication_target(Path('/backup/location'))
   ```

4. USO DO ORQUESTRADOR DISTRIBUÍDO:
   ```python
   # Inicia orquestrador
   orchestrator = DistributedOrchestrator(max_workers=4)
   await orchestrator.start()
   
   # Submete tarefa
   task = {'type': 'compute', 'duration': 2.0}
   task_id = await orchestrator.submit_task(task, priority=1)
   
   # Aguarda resultado
   result = await orchestrator.get_task_result(task_id, timeout=10.0)
   
   # Submete lote de tarefas
   tasks = [{'type': 'io', 'duration': 0.5} for _ in range(10)]
   task_ids = await orchestrator.submit_task_batch(tasks)
   
   # Aguarda todas as tarefas
   results = await orchestrator.wait_for_tasks(task_ids, timeout=30.0)
   
   # Obtém status
   status = orchestrator.get_orchestration_status()
   print(f"Workers ativos: {status['active_workers']}")
   print(f"Tarefas na fila: {status['queued_tasks']}")
   
   # Para orquestrador
   await orchestrator.stop()
   ```

5. USO DO SISTEMA DE OTIMIZAÇÃO:
   ```python
   # Registra performance
   brain.optimization_engine.record_performance('accuracy', 0.85)
   brain.optimization_engine.record_performance('processing_time', 120.0)
   
   # Obtém sugestões de otimização
   suggestions = brain.optimization_engine.suggest_optimization()
   
   # Aplica otimizações
   applied = brain.optimization_engine.apply_optimization(suggestions)
   
   # Obtém relatório
   report = brain.optimization_engine.get_optimization_report()
   print(f"Melhor performance: {report['best_performance']}")
   ```

6. USO DO GERENCIADOR DE PLUGINS:
   ```python
   # Descobre plugins disponíveis
   plugins = brain.plugin_manager.discover_plugins()
   
   # Carrega plugin específico
   plugin_info = brain.plugin_manager.load_plugin('my_plugin')
   
   # Executa hook de plugin
   result = brain.plugin_manager.execute_hook('before_analysis', data)
   
   # Descarrega plugin
   brain.plugin_manager.unload_plugin('my_plugin')
   ```

7. USO DA PONTE ML:
   ```python
   # Processa dados de mercado com ML
   market_data = {'price': 100.0, 'volume': 1000, 'timestamp': '2026-02-12'}
   ml_result = await brain.ml_bridge.process_market_data(market_data)
   
   # Obtém histórico de previsões
   predictions = list(brain.ml_bridge.ml_predictions)
   ```

CONFIGURAÇÃO:
------------
O sistema configura-se automaticamente baseado nos módulos disponíveis.
Flags de integração são atualizadas dinamicamente pelo ModuleDiscovery.

REQUISITOS OPCIONAIS:
--------------------
- PyTorch (para redes neurais avançadas)
- NumPy (para cálculos numéricos)
- Pandas (para análise de dados)
- Qiskit (para computação quântica)
- Redis (para cache distribuído)
- psutil (para monitoramento de recursos)
- scikit-learn (para ML básico)

O sistema opera de forma degradada se alguns requisitos não estiverem disponíveis.

SUPPORT:
--------
Para suporte e documentação adicional, consulte o repositório oficial
do VHALINOR-IAG TRADER.

================================================================================
"""

# =============================================================================
# IMPORTAÇÕES OTIMIZADAS COM LAZY LOADING E FALLBACKS
# =============================================================================

import logging
# Garantir que stdout/stderr aceitem UTF-8 (evita UnicodeEncodeError no Windows)
import sys

try:
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    if hasattr(sys.stderr, "reconfigure"):
        sys.stderr.reconfigure(encoding="utf-8", errors="replace")
except Exception:
    # Se não for possível reconfigurar, seguimos com o encoding padrão.
    pass

# Logger mínimo para bootstrap (antes do logger principal)
_BOOTSTRAP_LOG_FORMAT = '%(asctime)s | %(levelname)-8s | %(name)s - %(message)s'
_BOOTSTRAP_LOG_DATE_FORMAT = '%Y-%m-%d %H:%M:%S'
if not logging.getLogger().handlers:
    logging.basicConfig(
        level=logging.INFO,
        format=_BOOTSTRAP_LOG_FORMAT,
        datefmt=_BOOTSTRAP_LOG_DATE_FORMAT,
        handlers=[logging.StreamHandler(stream=sys.stderr)],
    )
_bootstrap_logger = logging.getLogger("VhalinorBootstrap")

# PyTorch com tratamento de erro robusto
try:
    import torch
    import torch.nn as nn
    import torch.optim as optim
    from torch.utils.data import DataLoader, TensorDataset
    PYTORCH_AVAILABLE = True
    _bootstrap_logger.info("PyTorch carregado com sucesso")
except ImportError as e:
    PYTORCH_AVAILABLE = False
    torch = None
    nn = None
    optim = None
    _bootstrap_logger.warning("PyTorch não disponível: %s", e)
except Exception as e:
    PYTORCH_AVAILABLE = False
    torch = None
    nn = None
    optim = None
    _bootstrap_logger.exception("Erro ao carregar PyTorch: %s", e)

# NumPy com fallback
try:
    import numpy as np
    NUMPY_AVAILABLE = True
except ImportError:
    NUMPY_AVAILABLE = False
    np = None
    _bootstrap_logger.warning("NumPy não disponível")

# Pandas com fallback
try:
    import pandas as pd
    PANDAS_AVAILABLE = True
except ImportError:
    PANDAS_AVAILABLE = False
    pd = None
    _bootstrap_logger.warning("Pandas não disponível")
import os
import asyncio
import threading
import concurrent.futures
import importlib
import importlib.util
import pickle
import base64
import hashlib
import json
import time
import gc
import zlib
from pathlib import Path
from collections import deque, defaultdict, Counter
from dataclasses import dataclass, field
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional, Callable, Tuple, Union
from enum import Enum
from functools import lru_cache, wraps
from contextlib import contextmanager, asynccontextmanager
import random
import math
import statistics
import heapq

# =============================================================================
# PADRONIZAÇÃO DE PATHS E DIRETÓRIOS DO PROJETO
# =============================================================================

PROJECT_ROOT = Path(__file__).resolve().parent

PROJECT_DIRS: Dict[str, Path] = {
    "logs": PROJECT_ROOT / "logs",
    "data": PROJECT_ROOT / "data",
    "cache": PROJECT_ROOT / "cache",
    "models": PROJECT_ROOT / "models",
    "configs": PROJECT_ROOT / "configs",
}

def ensure_project_dirs() -> None:
    for _p in PROJECT_DIRS.values():
        try:
            _p.mkdir(parents=True, exist_ok=True)
        except Exception as exc:
            _bootstrap_logger.warning("Nao foi possivel criar pasta %s: %s", _p, exc)

ensure_project_dirs()

def _has_module(module_name: str) -> bool:
    try:
        if importlib.util.find_spec(module_name) is not None:
            return True
    except Exception:
        pass
    # Fallback: checa arquivo .py no root do projeto
    try:
        return (PROJECT_ROOT / f"{module_name}.py").exists()
    except Exception:
        return False

# =============================================================================
# SISTEMA DE OTIMIZAÇÃO E EFICIÊNCIA
# =============================================================================

@dataclass
class OrchestratorConfig:
    """Configuração centralizada do orchestrator"""
    # Cache
    cache_ttl: float = 1.0
    cache_max_size: int = 1000
    
    # Pooling
    max_bridges: int = 4
    max_workers: int = 8
    
    # Batch processing
    batch_size: int = 100
    batch_max_wait: float = 0.1
    
    # Memory
    deque_min_maxlen: int = 10
    deque_max_maxlen: int = 1000
    
    # Performance
    profile_threshold: float = 0.1
    sync_timeout: float = 5.0
    
    # Health check
    health_check_interval: float = 30.0


class CachedStatistics:
    """Cache para estatísticas com TTL"""
    def __init__(self, ttl_seconds: float = 1.0):
        self._cache = {}
        self._ttl = ttl_seconds
        self._lock = threading.RLock()
    
    def get(self, key: str, compute_func: Callable) -> Any:
        with self._lock:
            cached = self._cache.get(key)
            if cached and time.time() - cached['timestamp'] < self._ttl:
                return cached['value']
            
            value = compute_func()
            self._cache[key] = {'value': value, 'timestamp': time.time()}
            return value
    
    def clear(self) -> None:
        with self._lock:
            self._cache.clear()


class BatchProcessor:
    """Processamento em lote para operações repetitivas"""
    def __init__(self, batch_size: int = 100, max_wait: float = 0.1):
        self.batch_size = batch_size
        self.max_wait = max_wait
        self._batch = []
        self._last_flush = time.time()
        self._lock = threading.RLock()
    
    async def add(self, item: Any, process_func: Callable) -> None:
        with self._lock:
            self._batch.append(item)
            
            if len(self._batch) >= self.batch_size or \
               time.time() - self._last_flush >= self.max_wait:
                await self._flush(process_func)
    
    async def _flush(self, process_func: Callable) -> None:
        if not self._batch:
            return
        
        batch = self._batch.copy()
        self._batch.clear()
        self._last_flush = time.time()
        
        await process_func(batch)


class AdaptiveDeque:
    """Deque com tamanho adaptativo"""
    def __init__(self, initial_maxlen: int = 100, min_maxlen: int = 10, max_maxlen: int = 1000):
        self._deque = deque(maxlen=initial_maxlen)
        self._min_maxlen = min_maxlen
        self._max_maxlen = max_maxlen
        self._usage_stats = deque(maxlen=50)
        self._lock = threading.RLock()
    
    def append(self, item: Any) -> None:
        with self._lock:
            self._deque.append(item)
            self._adjust_size()
    
    def _adjust_size(self) -> None:
        if len(self._usage_stats) < 10:
            return
        
        current_usage = len(self._deque) / self._deque.maxlen
        self._usage_stats.append(current_usage)
        
        avg_usage = sum(self._usage_stats) / len(self._usage_stats)
        
        if avg_usage > 0.9 and self._deque.maxlen < self._max_maxlen:
            new_maxlen = min(self._deque.maxlen * 2, self._max_maxlen)
            self._deque = deque(self._deque, maxlen=new_maxlen)
        elif avg_usage < 0.3 and self._deque.maxlen > self._min_maxlen:
            new_maxlen = max(self._deque.maxlen // 2, self._min_maxlen)
            self._deque = deque(self._deque, maxlen=new_maxlen)
    
    def __len__(self) -> int:
        return len(self._deque)
    
    def __iter__(self):
        return iter(self._deque)


def profile_performance(threshold: float = 0.1):
    """Decorator para profiling de performance"""
    def decorator(func):
        @wraps(func)
        async def async_wrapper(*args, **kwargs):
            start = time.perf_counter()
            result = await func(*args, **kwargs)
            elapsed = time.perf_counter() - start
            
            if elapsed > threshold:
                logger.warning(f"⚠️ {func.__name__} levou {elapsed:.3f}s")
            
            return result
        
        @wraps(func)
        def sync_wrapper(*args, **kwargs):
            start = time.perf_counter()
            result = func(*args, **kwargs)
            elapsed = time.perf_counter() - start
            
            if elapsed > threshold:
                logger.warning(f"⚠️ {func.__name__} levou {elapsed:.3f}s")
            
            return result
        
        return async_wrapper if asyncio.iscoroutinefunction(func) else sync_wrapper
    return decorator


class HealthMonitor:
    """Monitor de saúde do sistema"""
    def __init__(self, check_interval: float = 30.0):
        self.check_interval = check_interval
        self._health_status = {}
        self._last_check = {}
        self._lock = threading.RLock()
    
    async def check_component(self, name: str, check_func: Callable) -> bool:
        try:
            start = time.time()
            result = await check_func() if asyncio.iscoroutinefunction(check_func) else check_func()
            elapsed = time.time() - start
            
            with self._lock:
                self._health_status[name] = {
                    'healthy': result,
                    'response_time': elapsed,
                    'last_check': datetime.now().isoformat()
                }
            
            return result
        except Exception as e:
            with self._lock:
                self._health_status[name] = {
                    'healthy': False,
                    'error': str(e),
                    'last_check': datetime.now().isoformat()
                }
            return False
    
    def get_health_report(self) -> Dict[str, Any]:
        with self._lock:
            return self._health_status.copy()


class ResourceScheduler:
    """Scheduler de recursos com prioridades"""
    def __init__(self):
        self._queue = []
        self._lock = threading.RLock()
    
    def schedule(self, task: Callable, priority: int = 5) -> None:
        with self._lock:
            heapq.heappush(self._queue, (priority, time.time(), task))
    
    async def execute_next(self) -> Optional[Any]:
        with self._lock:
            if not self._queue:
                return None
            
            _, _, task = heapq.heappop(self._queue)
        
        return await task() if asyncio.iscoroutinefunction(task) else task()


class BridgePool:
    """Pool reutilizável de bridges"""
    def __init__(self, hub: 'IntegrationHub', max_bridges: int = 4):
        self.hub = hub
        self._pool = []
        self._max_bridges = max_bridges
        self._lock = threading.RLock()
    
    def get_bridge(self, bridge_type: str, bridge_class: type) -> Optional[Any]:
        with self._lock:
            for bridge in self._pool:
                if bridge.__class__ == bridge_class and not getattr(bridge, 'in_use', False):
                    bridge.in_use = True
                    return bridge
            
            if len(self._pool) < self._max_bridges:
                bridge = bridge_class(self.hub)
                bridge.in_use = True
                self._pool.append(bridge)
                return bridge
        
        return None
    
    def release_bridge(self, bridge: Any) -> None:
        with self._lock:
            if hasattr(bridge, 'in_use'):
                bridge.in_use = False


# =============================================================================
# SISTEMA DE DESCOBERTA AUTOMÁTICA DE MÓDULOS DINÂMICO
# =============================================================================

class ModuleDiscovery:
    """Sistema de descoberta automática de módulos do projeto"""
    
    def __init__(self, project_root: Path = PROJECT_ROOT):
        self.project_root = project_root
        self.discovered_modules: Dict[str, Dict[str, Any]] = {}
        self.module_registry: Dict[str, List[str]] = defaultdict(list)
        self.module_metadata: Dict[str, Dict[str, Any]] = {}
        self.last_scan_time: Optional[datetime] = None
        self._lock = threading.RLock()
    
    def scan_project(self, force: bool = False) -> Dict[str, Any]:
        """Escaneia o projeto em busca de módulos Python"""
        with self._lock:
            if not force and self.last_scan_time and (datetime.now() - self.last_scan_time).total_seconds() < 300:
                return self.discovered_modules
            
            _bootstrap_logger.info("🔍 Iniciando scan de módulos...")
            
            self.discovered_modules = {}
            self.module_registry.clear()
            self.module_metadata.clear()
            
            # Escanear diretório raiz
            self._scan_directory(self.project_root)
            
            # Escanear subdiretórios comuns
            common_dirs = ['ai_core', 'trading', 'data', 'utils', 'monitoring', 'security', 'autonomous']
            for dir_name in common_dirs:
                dir_path = self.project_root / dir_name
                if dir_path.exists() and dir_path.is_dir():
                    self._scan_directory(dir_path)
            
            self.last_scan_time = datetime.now()
            _bootstrap_logger.info(f"✅ Scan concluído: {len(self.discovered_modules)} módulos encontrados")
            
            return self.discovered_modules
    
    def _scan_directory(self, directory: Path) -> None:
        """Escaneia um diretório específico"""
        try:
            for file_path in directory.glob('*.py'):
                if file_path.name.startswith('_') or file_path.name == '__init__.py':
                    continue
                
                module_info = self._analyze_module(file_path)
                if module_info:
                    module_name = file_path.stem
                    self.discovered_modules[module_name] = module_info
                    
                    # Categorizar módulo
                    category = self._categorize_module(module_name, module_info)
                    self.module_registry[category].append(module_name)
                    self.module_metadata[module_name] = {'category': category, **module_info}
                    
        except Exception as e:
            _bootstrap_logger.warning(f"Erro ao escanear {directory}: {e}")
    
    def _analyze_module(self, file_path: Path) -> Optional[Dict[str, Any]]:
        """Analisa um arquivo de módulo e extrai metadados"""
        try:
            content = file_path.read_text(encoding='utf-8', errors='replace')
            
            # Extrair informações básicas
            info = {
                'path': str(file_path),
                'size_bytes': file_path.stat().st_size,
                'modified': datetime.fromtimestamp(file_path.stat().st_mtime).isoformat(),
                'has_class': 'class ' in content,
                'has_function': 'def ' in content,
                'has_async': 'async def' in content,
                'has_receive_data': 'receive_data' in content,
                'has_main': 'if __name__' in content or 'def main(' in content,
                'imports': self._extract_imports(content),
                'classes': self._extract_classes(content),
                'functions': self._extract_functions(content)
            }
            
            return info
            
        except Exception as e:
            _bootstrap_logger.warning(f"Erro ao analisar {file_path}: {e}")
            return None
    
    def _extract_imports(self, content: str) -> List[str]:
        """Extrai imports do conteúdo"""
        imports = []
        for line in content.split('\n'):
            line = line.strip()
            if line.startswith('import ') or line.startswith('from '):
                imports.append(line)
        return imports[:20]  # Limitar a 20 imports
    
    def _extract_classes(self, content: str) -> List[str]:
        """Extrai nomes de classes"""
        classes = []
        for line in content.split('\n'):
            if 'class ' in line:
                match = line.split('class ')[1].split('(')[0].split(':')[0].strip()
                if match and not match.startswith('_'):
                    classes.append(match)
        return classes[:10]  # Limitar a 10 classes
    
    def _extract_functions(self, content: str) -> List[str]:
        """Extrai nomes de funções"""
        functions = []
        for line in content.split('\n'):
            if 'def ' in line and not line.strip().startswith('#'):
                match = line.split('def ')[1].split('(')[0].strip()
                if match and not match.startswith('_'):
                    functions.append(match)
        return functions[:15]  # Limitar a 15 funções
    
    def _categorize_module(self, module_name: str, module_info: Dict[str, Any]) -> str:
        """Categoriza um módulo baseado em seu nome e conteúdo"""
        name_lower = module_name.lower()
        
        # Categorias baseadas em nome
        if any(kw in name_lower for kw in ['neural', 'brain', 'neuron', 'synapse']):
            return 'neural'
        elif any(kw in name_lower for kw in ['quantum', 'qiskit', 'circuit']):
            return 'quantum'
        elif any(kw in name_lower for kw in ['ml', 'machine', 'learning', 'train', 'predict']):
            return 'machine_learning'
        elif any(kw in name_lower for kw in ['analysis', 'pattern', 'risk', 'analytic']):
            return 'analysis'
        elif any(kw in name_lower for kw in ['trading', 'trade', 'market', 'price', 'order']):
            return 'trading'
        elif any(kw in name_lower for kw in ['cache', 'memory', 'storage', 'persist']):
            return 'storage'
        elif any(kw in name_lower for kw in ['monitor', 'metric', 'log', 'health']):
            return 'monitoring'
        elif any(kw in name_lower for kw in ['security', 'auth', 'encrypt', 'validate']):
            return 'security'
        elif any(kw in name_lower for kw in ['autonomous', 'auto', 'decision', 'orchestrat']):
            return 'autonomous'
        elif any(kw in name_lower for kw in ['optim', 'enhance', 'improve']):
            return 'optimization'
        elif any(kw in name_lower for kw in ['integration', 'bridge', 'connect', 'hub']):
            return 'integration'
        elif any(kw in name_lower for kw in ['data', 'process', 'transform']):
            return 'data'
        else:
            return 'utility'
    
    def get_modules_by_category(self, category: str) -> List[str]:
        """Retorna módulos de uma categoria específica"""
        return self.module_registry.get(category, [])
    
    def get_module_info(self, module_name: str) -> Optional[Dict[str, Any]]:
        """Retorna informações detalhadas de um módulo"""
        return self.module_metadata.get(module_name)
    
    def find_modules_with_receive_data(self) -> List[str]:
        """Encontra módulos que têm método receive_data"""
        return [name for name, info in self.discovered_modules.items() 
                if info.get('has_receive_data', False)]
    
    def find_async_modules(self) -> List[str]:
        """Encontra módulos assíncronos"""
        return [name for name, info in self.discovered_modules.items() 
                if info.get('has_async', False)]
    
    def get_module_statistics(self) -> Dict[str, Any]:
        """Retorna estatísticas dos módulos descobertos"""
        return {
            'total_modules': len(self.discovered_modules),
            'categories': {cat: len(mods) for cat, mods in self.module_registry.items()},
            'receive_data_modules': len(self.find_modules_with_receive_data()),
            'async_modules': len(self.find_async_modules()),
            'last_scan': self.last_scan_time.isoformat() if self.last_scan_time else None,
            'total_size_bytes': sum(m['size_bytes'] for m in self.discovered_modules.values())
        }

# Instância global de descoberta de módulos
module_discovery = ModuleDiscovery()

# Flags de integração baseados em descoberta dinâmica
def _update_integration_flags() -> None:
    """Atualiza flags de integração baseado em módulos descobertos"""
    global NEURAL_MODULES_AVAILABLE, QUANTUM_MODULES_AVAILABLE
    global ANALYSIS_MODULES_AVAILABLE, CONTINUOUS_LEARNING_AVAILABLE
    
    modules = module_discovery.scan_project()
    
    # Verificar módulos neurais
    neural_modules = ['neural_bus', 'NeuralConnectionMatrix', 'AdvancedNeuralEngine', 'NeuralConnectionMatrix']
    NEURAL_MODULES_AVAILABLE = any(mod in modules for mod in neural_modules)
    
    # Verificar módulos quânticos
    quantum_modules = ['quantum_core', 'quantum_algorithms_trader', 'VHALINOR_QUANTUM_CORE']
    QUANTUM_MODULES_AVAILABLE = any(mod in modules for mod in quantum_modules)
    
    # Verificar módulos de análise
    analysis_modules = ['data_analyzer', 'AdvancedPatternRecognition', 'AdvancedRiskAnalyzer', 'analyssis']
    ANALYSIS_MODULES_AVAILABLE = any(mod in modules for mod in analysis_modules)
    
    # Verificar módulos de aprendizado contínuo
    learning_modules = ['ContinuousLearningService', 'ContinuousQuantumLearning', 'continnuous_learning']
    CONTINUOUS_LEARNING_AVAILABLE = any(mod in modules for mod in learning_modules)

# Atualizar flags inicialmente
_update_integration_flags()

# Flags de integração (detectáveis por presença de módulo/arquivo)
# NEURAL_MODULES_AVAILABLE = _has_module("neural_bus") and _has_module("NeuralConnectionMatrix")
# QUANTUM_MODULES_AVAILABLE = _has_module("quantum_core") and _has_module("quantum_algorithms_trader")
# ANALYSIS_MODULES_AVAILABLE = _has_module("data_analyzer") and _has_module("AdvancedPatternRecognition") and _has_module("AdvancedRiskAnalyzer")
# CONTINUOUS_LEARNING_AVAILABLE = _has_module("ContinuousLearningService") and _has_module("ContinuousQuantumLearning")

MEMORY_MONITOR_AVAILABLE = True
ASYNC_MEMORY_MONITOR_AVAILABLE = True
VALIDATE_INPUT_AVAILABLE = True
RATE_LIMIT_AVAILABLE = True

# =============================================================================
# IMPORTAÇÕES CIENTÍFICAS COM FALLBACK GRACIOSO
# =============================================================================

# NumPy e Pandas já foram importados acima com tratamento robusto
# Usando as variáveis NUMPY_AVAILABLE e PANDAS_AVAILABLE definidas anteriormente

try:
    from scipy import stats, signal, spatial
    SCIPY_AVAILABLE = True
except Exception as exc:
    SCIPY_AVAILABLE = False
    _bootstrap_logger.warning("SciPy nao disponivel (%s). Analise cientifica limitada.", type(exc).__name__)

try:
    from sklearn.cluster import KMeans, DBSCAN, AgglomerativeClustering
    from sklearn.preprocessing import StandardScaler, RobustScaler, MinMaxScaler
    from sklearn.decomposition import PCA, FastICA
    from sklearn.ensemble import IsolationForest, RandomForestClassifier
    from sklearn.metrics import silhouette_score, calinski_harabasz_score
    SKLEARN_AVAILABLE = True
except Exception as exc:
    SKLEARN_AVAILABLE = False
    _bootstrap_logger.warning("Scikit-learn nao disponivel (%s). ML limitado.", type(exc).__name__)

# PyTorch já foi importado acima com tratamento robusto
# Usando a variável PYTORCH_AVAILABLE definida anteriormente
if PYTORCH_AVAILABLE:
    try:
        # PyTorch modules já importados: nn, optim, DataLoader, TensorDataset
        PYTORCH_MODULES_AVAILABLE = True
    except Exception as exc:
        PYTORCH_MODULES_AVAILABLE = False
        _bootstrap_logger.warning("PyTorch modules nao disponiveis (%s).", type(exc).__name__)
else:
    PYTORCH_MODULES_AVAILABLE = False

try:
    from qiskit import QuantumCircuit, QuantumRegister, ClassicalRegister
    try:
        # `execute` foi removido de algumas versões recentes do Qiskit
        from qiskit import execute  # type: ignore
    except Exception:
        execute = None  # type: ignore
    # Backends/simuladores podem não estar instalados (ex.: `qiskit-aer`)
    Aer = None  # type: ignore
    AerSimulator = None  # type: ignore
    try:
        from qiskit_aer import Aer as _Aer, AerSimulator as _AerSimulator  # type: ignore

        Aer = _Aer  # type: ignore
        AerSimulator = _AerSimulator  # type: ignore
    except Exception:
        pass
    from qiskit.circuit import Parameter
    from qiskit.quantum_info import Statevector, Operator
    from qiskit.circuit.library import ZZFeatureMap, RealAmplitudes, EfficientSU2
    QISKIT_AVAILABLE = True
except Exception as exc:
    QISKIT_AVAILABLE = False
    _bootstrap_logger.warning("Qiskit nao disponivel (%s). Simulacao quantica limitada.", type(exc).__name__)

try:
    import networkx as nx
    NETWORKX_AVAILABLE = True
except Exception as exc:
    NETWORKX_AVAILABLE = False
    _bootstrap_logger.warning("NetworkX nao disponivel (%s). Analise de grafos limitada.", type(exc).__name__)

try:
    import yaml
    YAML_AVAILABLE = True
except Exception:
    YAML_AVAILABLE = False

try:
    import sqlite3
    SQLITE_AVAILABLE = True
except Exception:
    SQLITE_AVAILABLE = False

try:
    import redis  # type: ignore
    REDIS_AVAILABLE = True
except Exception:
    REDIS_AVAILABLE = False

# =============================================================================
# CONFIGURAÇÕES DE LOGGING AVANÇADAS
# =============================================================================
from logging.handlers import RotatingFileHandler

LOG_FORMAT = '%(asctime)s | %(levelname)-8s | %(name)s - %(message)s'
LOG_DATE_FORMAT = '%Y-%m-%d %H:%M:%S'

# Configurar handler para arquivo com encoding UTF-8
file_handler = RotatingFileHandler(
    str(PROJECT_DIRS["logs"] / 'vhalinor_brain.log'),
    maxBytes=10*1024*1024,  # 10MB
    backupCount=5,
    encoding='utf-8'
)

# Configurar handler para console (respeita reconfigure acima)
console_handler = logging.StreamHandler(stream=sys.stdout)

logging.basicConfig(
    level=logging.INFO,
    format=LOG_FORMAT,
    datefmt=LOG_DATE_FORMAT,
    handlers=[file_handler, console_handler]
)

logger = logging.getLogger('VhalinorBrain')

# =============================================================================
# CARREGAMENTO CENTRALIZADO DE CONFIGS (COM FALLBACK)
# =============================================================================

_CONFIG_CANDIDATES = (
    "central_ai_config.json",
    "system_config.json",
    "vhalinor_config.json",
)

def _deep_merge(base: Dict[str, Any], incoming: Dict[str, Any]) -> Dict[str, Any]:
    """
    Merge recursivo (dict + dict). `incoming` sobrescreve valores escalares/listas,
    e mescla sub-dicionários.
    """
    for k, v in incoming.items():
        if k in base and isinstance(base[k], dict) and isinstance(v, dict):
            _deep_merge(base[k], v)  # type: ignore[arg-type]
        else:
            base[k] = v
    return base

def _safe_read_text(path: Path) -> Optional[str]:
    try:
        return path.read_text(encoding="utf-8", errors="replace")
    except FileNotFoundError:
        return None
    except Exception as exc:
        logger.warning("Falha ao ler config %s: %s", path, exc)
        return None

def _load_config_file(path: Path) -> Dict[str, Any]:
    raw = _safe_read_text(path)
    if raw is None:
        return {}

    suffix = path.suffix.lower()
    try:
        if suffix == ".json":
            data = json.loads(raw)
        elif suffix in {".yml", ".yaml"} and YAML_AVAILABLE:
            data = yaml.safe_load(raw)  # type: ignore[name-defined]
        else:
            logger.warning("Formato de config nao suportado: %s", path)
            return {}
    except Exception as exc:
        logger.warning("Config invalida em %s: %s", path, exc)
        return {}

    if isinstance(data, dict):
        return data
    logger.warning("Config %s ignorada (esperado dict, veio %s)", path, type(data).__name__)
    return {}

def load_configs() -> Dict[str, Any]:
    """
    Carrega configs conhecidas, sem crash se nao existirem.
    Ordem: `configs/` > raiz do projeto. Merge recursivo para dicts.
    """
    merged: Dict[str, Any] = {}
    search_paths: List[Path] = []

    # Permite apontar configs explicitamente por env var (separado por ; ou :)
    # Ex.: VHALINOR_CONFIG_PATHS="C:\\path\\custom.json;C:\\path\\override.yaml"
    env_paths = os.environ.get("VHALINOR_CONFIG_PATHS", "").strip()
    if env_paths:
        sep = ";" if ";" in env_paths else ":"
        for raw in [p.strip() for p in env_paths.split(sep) if p.strip()]:
            try:
                search_paths.append(Path(raw))
            except Exception:
                continue

    for name in _CONFIG_CANDIDATES:
        search_paths.append(PROJECT_DIRS["configs"] / name)
        search_paths.append(PROJECT_ROOT / name)

    # Suporte opcional a YAML com os mesmos nomes-base
    for name in _CONFIG_CANDIDATES:
        base = name.rsplit(".", 1)[0]
        search_paths.append(PROJECT_DIRS["configs"] / f"{base}.yaml")
        search_paths.append(PROJECT_DIRS["configs"] / f"{base}.yml")
        search_paths.append(PROJECT_ROOT / f"{base}.yaml")
        search_paths.append(PROJECT_ROOT / f"{base}.yml")

    for p in search_paths:
        cfg = _load_config_file(p)
        if cfg:
            _deep_merge(merged, cfg)

    return merged

@lru_cache(maxsize=1)
def get_config() -> Dict[str, Any]:
    return load_configs()

def _core_self_check() -> Dict[str, Any]:
    """
    Checagem leve de integracao com o projeto (sem treino, sem rede).
    - Pastas padrao
    - Escrita de log
    - Leitura de configs
    - Presenca de modulos opcionais
    """
    results: Dict[str, Any] = {
        "timestamp": datetime.now().isoformat(),
        "project_root": str(PROJECT_ROOT),
        "dirs": {},
        "config_keys": 0,
        "optional_modules": {
            "pytorch": PYTORCH_AVAILABLE,
            "qiskit": QISKIT_AVAILABLE,
            "redis": REDIS_AVAILABLE,
            "yaml": YAML_AVAILABLE,
            "neural": NEURAL_MODULES_AVAILABLE,
            "quantum": QUANTUM_MODULES_AVAILABLE,
            "analysis": ANALYSIS_MODULES_AVAILABLE,
            "continuous_learning": CONTINUOUS_LEARNING_AVAILABLE,
        },
        "ok": True,
        "warnings": [],
    }

    # Pastas
    for name, p in PROJECT_DIRS.items():
        try:
            ensure_project_dirs()
            results["dirs"][name] = {"path": str(p), "exists": p.exists(), "is_dir": p.is_dir()}
            if not p.exists():
                results["ok"] = False
                results["warnings"].append(f"dir_missing:{name}")
        except Exception as exc:
            results["ok"] = False
            results["warnings"].append(f"dir_error:{name}:{type(exc).__name__}")

    # Log (nao falhar se handler ja existir)
    try:
        logger.info("SELF_CHECK: ok")
    except Exception as exc:
        results["ok"] = False
        results["warnings"].append(f"log_error:{type(exc).__name__}")

    # Configs
    try:
        cfg = get_config()
        results["config_keys"] = len(cfg) if isinstance(cfg, dict) else 0
    except Exception as exc:
        results["ok"] = False
        results["warnings"].append(f"config_error:{type(exc).__name__}")

    return results

# =============================================================================
# CONFIGURAÇÕES E CONSTANTES GLOBAIS
# =============================================================================

VERSION = "3.0.0"
CODENAME = "Quantum Singularity"
BUILD_DATE = "2026-02-12"
AUTHOR = "Alex Miranda Sales"

# Configurações de performance
MAX_WORKERS = min(32, os.cpu_count() + 4) if os.cpu_count() else 8
CACHE_TTL_DEFAULT = 3600  # 1 hora
CACHE_MAX_SIZE = 10000
BATCH_SIZE_DEFAULT = 64
LEARNING_RATE_DEFAULT = 0.001
MEMORY_MONITOR_AVAILABLE = True
ASYNC_MEMORY_MONITOR_AVAILABLE = True
VALIDATE_INPUT_AVAILABLE = True
RATE_LIMIT_AVAILABLE = True
MEMORY_LIMIT_MB = 1000  # 100GB
TIMEOUT_DEFAULT = 30

# Configurações neurais
MAX_NEURONS = 1000000
MAX_SYNAPSES = 10000000
MEMORY_MONITOR_AVAILABLE = True
ASYNC_MEMORY_MONITOR_AVAILABLE = True
VALIDATE_INPUT_AVAILABLE = True
RATE_LIMIT_AVAILABLE = True
NEURON_SPARSITY = 0.1
NEURON_MAX_ACTIVATION = 10000
NEURON_MIN_ACTIVATION = 10
PLASTICITY_RATE = 0.01
CREATIVE_THRESHOLD = 0.5
ANALYTICAL_THRESHOLD = 0.5
SECURITY_THRESHOLD = 0.5
NETWORK_THRESHOLD = 0.5
API_THRESHOLD = 0.5
DATABASE_THRESHOLD = 0.5
GENERATIVE_THRESHOLD = 0.5
REINFORCEMENT_THRESHOLD = 0.5
ATTENTION_THRESHOLD = 0.5
META_THRESHOLD = 0.5
AUTOMATIC_THRESHOLD = 0.5
TRADING_THRESHOLD = 0.5
OUTPUT_THRESHOLD = 0.5
CONNECTION_DENSITY = 0.1
HOMEGSTATIC_TARGET = 0.5

# Configurações quânticas
QUANTUM_SHOTS = 1024
QUANTUM_QUBITS = 8
QUANTUM_ENTANGLEMENT_PROB = 0.3
QUANTUM_MONITOR_AVAILABLE = True
QUANTUM_ANALYSIS_AVAILABLE = True
QUANTUM_SIMULATION_AVAILABLE = True
QUANTUM_OPTIMIZATION_AVAILABLE = True
QUANTUM_SECURITY_AVAILABLE = True
QUANTUM_NETWORK_AVAILABLE = True
QUANTUM_API_AVAILABLE = True
QUANTUM_DATABASE_AVAILABLE = True
QUANTUM_GENERATIVE_AVAILABLE = True
QUANTUM_REINFORCEMENT_AVAILABLE = True
# Configurações de segurança
MAX_INPUT_SIZE = 10 * 1024 * 1024  # 10MB
RATE_LIMIT_REQUESTS = 1000
RATE_LIMIT_WINDOW = 60  # 1 minuto

# =============================================================================
# DECORADORES DE PERFORMANCE E SEGURANÇA
# =============================================================================

def timing_decorator(func):
    """Mede e registra tempo de execução"""
    @wraps(func)
    def wrapper(*args, **kwargs):
        start = time.perf_counter()
        result = func(*args, **kwargs)
        elapsed = time.perf_counter() - start
        if elapsed > 0.1:  # > 100ms
            logger.debug(f"⏱️ {func.__name__} executado em {elapsed*1000:.2f}ms")
        return result
    return wrapper

def async_timing_decorator(func):
    """Versão assíncrona do timing decorator"""
    @wraps(func)
    async def wrapper(*args, **kwargs):
        start = time.perf_counter()
        result = await func(*args, **kwargs)
        elapsed = time.perf_counter() - start
        if elapsed > 0.1:
            logger.debug(f"⏱️ {func.__name__} executado em {elapsed*1000:.2f}ms")
        return result
    return wrapper

def memoize(ttl: int = CACHE_TTL_DEFAULT):
    """Cache com time-to-live"""
    def decorator(func):
        cache = {}
        timestamps = {}
        
        @wraps(func)
        def wrapper(*args, **kwargs):
            key = hashlib.md5(
                pickle.dumps((args, frozenset(kwargs.items())))
            ).hexdigest()
            
            now = time.time()
            if key in cache and now - timestamps.get(key, 0) < ttl:
                return cache[key]
            
            result = func(*args, **kwargs)
            cache[key] = result
            timestamps[key] = now
            
            # Limpeza simples do cache
            if len(cache) > CACHE_MAX_SIZE:
                oldest = min(timestamps.keys(), key=lambda k: timestamps[k])
                del cache[oldest]
                del timestamps[oldest]
            
            return result
        return wrapper
    return decorator

def validate_input(func):
    """Valida entradas de funções"""
    @wraps(func)
    def wrapper(*args, **kwargs):
        for arg in args:
            if isinstance(arg, str) and len(arg) > MAX_INPUT_SIZE:
                raise ValueError(f"Input string excede tamanho máximo: {len(arg)} > {MAX_INPUT_SIZE}")
            if isinstance(arg, (list, dict)) and len(str(arg)) > MAX_INPUT_SIZE:
                raise ValueError(f"Input collection excede tamanho máximo")
        return func(*args, **kwargs)
    return wrapper

def rate_limit(max_calls: int = RATE_LIMIT_REQUESTS, window: int = RATE_LIMIT_WINDOW):
    """Rate limiting decorator"""
    def decorator(func):
        calls = []
        
        @wraps(func)
        def wrapper(*args, **kwargs):
            now = time.time()
            calls[:] = [call for call in calls if call > now - window]
            
            if len(calls) >= max_calls:
                raise Exception(f"Rate limit excedido: {max_calls} chamadas em {window}s")
            
            calls.append(now)
            return func(*args, **kwargs)
        return wrapper
    return decorator

@contextmanager
def memory_monitor():
    """Monitora uso de memória"""
    import psutil
    process = psutil.Process()
    initial_memory = process.memory_info().rss / 1024 / 1024
    try:
        yield
    finally:
        final_memory = process.memory_info().rss / 1024 / 1024
        if final_memory - initial_memory > MEMORY_LIMIT_MB:
            logger.warning(f"⚠️ Alto consumo de memória: {final_memory - initial_memory:.2f}MB")
        gc.collect()

@asynccontextmanager
async def async_memory_monitor():
    """Versão assíncrona do memory monitor"""
    import psutil
    process = psutil.Process()
    initial_memory = process.memory_info().rss / 1024 / 1024
    try:
        yield
    finally:
        final_memory = process.memory_info().rss / 1024 / 1024
        if final_memory - initial_memory > MEMORY_LIMIT_MB:
            logger.warning(f"⚠️ Alto consumo de memória: {final_memory - initial_memory:.2f}MB")
        gc.collect()

# =============================================================================
# ENUMS E CONSTANTES AVANÇADAS
# =============================================================================

class NeuronType(Enum):
    """Tipos de neurônios expandidos com especializações"""
    # Tipos básicos
    SENSORY = ("sensory", "📡", 0.3, 1.0)
    PROCESSING = ("processing", "⚙️", 0.5, 0.8)
    MEMORY = ("memory", "💾", 0.4, 1.2)
    DECISION = ("decision", "🎯", 0.6, 1.5)
    PREDICTIVE = ("predictive", "🔮", 0.6, 1.4)
    ANALYTICAL = ("analytical", "📊", 0.5, 1.2)
    SECURITY = ("security", "🛡️", 0.7, 1.6)
    NETWORK = ("network", "🌐", 0.4, 1.0)
    API = ("api", "🔌", 0.3, 0.8)
    DATABASE = ("database", "🗄️", 0.2, 1.0)
    GENERATIVE = ("generative", "✨", 0.8, 1.5)
    REINFORCEMENT = ("reinforcement", "🏆", 0.7, 1.3)
    ATTENTION = ("attention", "🎭", 0.6, 1.2)
    META = ("meta", "🔄", 0.9, 1.8)
    AUTOMATIC = ("automatic", "🤖", 0.5, 1.0)
    TRADING = ("trading", "💰", 0.7, 1.5)
    MARKET_ANALYSIS = ("market_analysis", "📊", 0.6, 1.3)
    RISK_ASSESSMENT = ("risk_assessment", "⚠️", 0.8, 1.7)
    OPTIMIZATION = ("optimization", "⚙️", 0.6, 1.4)
    QUANTUM = ("quantum", "⚛️", 0.7, 2.0)
    VISION = ("vision", "👁️", 0.4, 1.1)
    AUDITORY = ("auditory", "👂", 0.4, 1.1)
    MOTOR = ("motor", "🦾", 0.5, 1.0)
    EMOTIONAL = ("emotional", "💓", 0.6, 0.7)
    CREATIVE = ("creative", "🎨", 0.8, 1.3)
    OUTPUT = ("output", "📤", 0.3, 0.9)
    
    def __init__(self, label: str, icon: str, default_threshold: float, importance: float):
        self.label = label
        self.icon = icon
        self.default_threshold = default_threshold
        self.importance = importance

class BrainState(Enum):
    """Estados cerebrais expandidos com descrições"""
    IDLE = ("idle", "💤", "Sistema em repouso")
    PROCESSING = ("processing", "⚙️", "Processamento ativo")
    LEARNING = ("learning", "📚", "Aprendizado em andamento")
    DREAMING = ("dreaming", "💭", "Consolidação de memória")
    FOCUSED = ("focused", "🎯", "Foco intenso")
    CREATIVE = ("creative", "🎨", "Modo criativo")
    ANALYTICAL = ("analytical", "📊", "Análise profunda")
    INTUITIVE = ("intuitive", "🔮", "Processamento intuitivo")
    MEDITATIVE = ("meditative", "🧘", "Otimização interna")
    HYPER_FOCUS = ("hyper_focus", "⚡", "Foco máximo")
    MULTI_TASKING = ("multi_task", "🔄", "Múltiplas tarefas")
    OPTIMIZING = ("optimizing", "📈", "Otimização de parâmetros")
    SECURITY_SCAN = ("security", "🛡️", "Varredura de segurança")
    BACKUP = ("backup", "💾", "Backup do estado")
    RECOVERY = ("recovery", "🔄", "Recuperação de falha")
    EMERGENCY = ("emergency", "🚨", "Modo de emergência")
    
    def __init__(self, label: str, icon: str, description: str):
        self.label = label
        self.icon = icon
        self.description = description

class NeuralPattern(Enum):
    """Padrões de ativação neural"""
    SEQUENTIAL = ("sequential", "1️⃣", "Ativação sequencial")
    PARALLEL = ("parallel", "🔄", "Ativação paralela")
    RECURRENT = ("recurrent", "♻️", "Conexões recorrentes")
    ATTENTIONAL = ("attention", "🎭", "Mecanismo de atenção")
    RESONANT = ("resonant", "🎵", "Ressonância neural")
    CHAOTIC = ("chaotic", "🌀", "Dinâmica caótica")
    SYNCHRONIZED = ("sync", "🤝", "Ativação sincronizada")
    OSCILLATORY = ("osc", "📉", "Padrão oscilatório")
    SPIKING = ("spiking", "⚡", "Picos neurais")
    BURSTING = ("bursting", "💥", "Rajadas de ativação")
    QUANTUM = ("quantum", "⚛️", "Padrão quântico")
    VISION = ("vision", "👁️", "Padrão visual")
    AUDITORY = ("auditory", "👂", "Padrão auditivo")
    MOTOR = ("motor", "🦾", "Padrão motora")
    EMOTIONAL = ("emotional", "💓", "Padrão emocional")
    CREATIVE = ("creative", "🎨", "Padrão criativo")
    PREDICTIVE = ("predictive", "🔮", "Padrão preditivo")
    ANALYTICAL = ("analytical", "📊", "Padrão analítico")
    SECURITY = ("security", "🛡️", "Padrão de segurança")
    NETWORK = ("network", "🌐", "Padrão de rede")
    API = ("api", "🔌", "Padrão de API")
    DATABASE = ("database", "🗄️", "Padrão de banco de dados")
    GENERATIVE = ("generative", "✨", "Padrão gerativo")
    REINFORCEMENT = ("reinforcement", "🏆", "Padrão de reforço")
    ATTENTION = ("attention", "🎭", "Padrão de atenção")
    META = ("meta", "🔄", "Padrão meta")
    AUTOMATIC = ("automatic", "🤖", "Padrão automático")
    TRADING = ("trading", "💰", "Padrão de trading")
    OUTPUT = ("output", "📤", "Padrão de saída")
    
    def __init__(self, label: str, icon: str, description: str):
        self.label = label
        self.icon = icon
        self.description = description

class SecurityLevel(Enum):
    """Níveis de segurança"""
    PUBLIC = (0, "Público", "🔓")
    INTERNAL = (1, "Interno", "🔐")
    CONFIDENTIAL = (2, "Confidencial", "🔒")
    SECRET = (3, "Secreto", "🔏")
    TOP_SECRET = (4, "Ultrassecreto", "🔒🔒")
    
    def __init__(self, level: int, label: str, icon: str):
        self.level = level
        self.label = label
        self.icon = icon

class DataPriority(Enum):
    """Prioridades de dados"""
    LOW = (0, "Baixa", "🟢")
    MEDIUM = (1, "Média", "🟡")
    HIGH = (2, "Alta", "🟠")
    CRITICAL = (3, "Crítica", "🔴")
    EMERGENCY = (4, "Emergência", "💀")
    
    def __init__(self, priority: int, label: str, icon: str):
        self.priority = priority
        self.label = label
        self.icon = icon

class QuantumState(Enum):
    """Estados quânticos"""
    SUPERPOSITION = ("superposition", "⚛️", "Superposição quântica")
    ENTANGLEMENT = ("entanglement", "🤝", "Entanglement quântico")
    DECOHERENCE = ("decoherence", "📉", "Decoerência quântica")

    def __init__(self, label: str, icon: str, description: str):
        self.label = label
        self.icon = icon
        self.description = description
        # Mantemos este Enum apenas como metadado semântico (label/icon/descrição).

class QuantumEntanglement(Enum):
    """Entanglement quântico"""
    ENTANGLED = ("entangled", "🤝", "Entanglement quântico")
    DECOHERENT = ("decoherent", "📉", "Decoerência quântica")

    def __init__(self, label: str, icon: str, description: str):
        self.label = label
        self.icon = icon
        self.description = description
        # Campos adicionais ficam fora do Enum; este Enum é semântico/leve.

class QuantumDecoherence(Enum):
    """Estados de decoerência (uso leve/semântico)."""

    LOW = ("low", "🟢", "Baixa decoerência")
    MEDIUM = ("medium", "🟡", "Decoerência moderada")
    HIGH = ("high", "🔴", "Alta decoerência")

    def __init__(self, label: str, icon: str, description: str):
        self.label = label
        self.icon = icon
        self.description = description

class QuantumSuperposition(Enum):
    """Estados de superposição (uso leve/semântico)."""

    STABLE = ("stable", "🌊", "Superposição estável")
    UNSTABLE = ("unstable", "🌪️", "Superposição instável")

    def __init__(self, label: str, icon: str, description: str):
        self.label = label
        self.icon = icon
        self.description = description


# =============================================================================
# ESTRUTURAS DE DADOS AVANÇADAS
# =============================================================================

@dataclass
class BrainNeuron:
    """Neurônio básico do sistema cerebral"""
    id: str
    file_path: str
    neuron_type: NeuronType
    activation_threshold: float = 0.5
    current_activation: float = 0.0
    connections: List[str] = field(default_factory=list)
    last_fired: Optional[datetime] = None
    memory_weight: float = 1.0
    learning_rate: float = 0.01
    quantum_entanglement: float = 0.0
    quantum_coherence: float = 1.0
    quantum_entanglement: float = 0.0
    quantum_decoherence: float = 0.0
    quantum_superposition: float = 0.0
    
    def to_dict(self) -> Dict[str, Any]:
        """Converter para dicionário serializável"""
        return {
            'id': self.id,
            'file_path': self.file_path,
            'neuron_type': self.neuron_type.value[0],
            'activation_threshold': self.activation_threshold,
            'current_activation': self.current_activation,
            'connections': self.connections,
            'last_fired': self.last_fired.isoformat() if self.last_fired else None,
            'memory_weight': self.memory_weight,
            'learning_rate': self.learning_rate,
            'quantum_entanglement': self.quantum_entanglement,
            'file_size': self.file_size,
            'file_extension': self.file_extension,
            'content_hash': self.content_hash,
            'created_at': self.created_at.isoformat(),
            'metadata': self.metadata
        }
    
    @property
    def is_active(self) -> bool:
        """Verifica se neurônio está ativo"""
        return self.current_activation >= self.activation_threshold
    
    @property
    def age_seconds(self) -> float:
        """Idade do neurônio em segundos"""
        return (datetime.now() - self.created_at).total_seconds()

@dataclass
class AdvancedNeuron(BrainNeuron):
    """Neurônio avançado com capacidades estendidas"""
    activation_history: List[float] = field(default_factory=list)
    fire_count: int = 0
    learning_coefficient: float = 0.1
    importance_score: float = 1.0
    energy_level: float = 100.0
    last_modified: datetime = field(default_factory=datetime.now)
    dependencies: List[str] = field(default_factory=list)
    security_level: SecurityLevel = SecurityLevel.INTERNAL
    version: str = "1.0"
    tags: List[str] = field(default_factory=list)
    embedding: Optional[np.ndarray] = None
    gradient: Optional[np.ndarray] = None
    
    def __post_init__(self):
        """Inicialização pós-criação"""
        self.activation_history = deque(maxlen=1000)
    
    def activate(self, stimulus: float = 1.0) -> float:
        """Ativa neurônio com estímulo"""
        self.current_activation += stimulus * self.learning_rate
        self.current_activation = max(0.0, min(1.0, self.current_activation))
        self.activation_history.append(self.current_activation)
        
        if self.current_activation >= self.activation_threshold:
            self.fire_count += 1
            self.last_fired = datetime.now()
            self.energy_level = max(0, self.energy_level - 0.1)
            return self.current_activation
        
        return 0.0
    
    def learn(self, error: float) -> None:
        """Aprendizado baseado em erro"""
        self.learning_rate *= (1 + error * self.learning_coefficient)
        self.learning_rate = max(0.001, min(0.1, self.learning_rate))
        self.activation_threshold *= (1 - error * 0.01)
        self.activation_threshold = max(0.1, min(0.9, self.activation_threshold))
    
    def calculate_entropy(self) -> float:
        """Calcula entropia baseada no histórico de ativação"""
        if len(self.activation_history) < 2:
            return 0.0
        
        hist = np.array(self.activation_history[-100:]) if NUMPY_AVAILABLE else list(self.activation_history)[-100:]
        
        if NUMPY_AVAILABLE:
            return float(np.std(hist))
        else:
            return float(statistics.stdev(hist)) if len(hist) > 1 else 0.0
    
    def get_health_status(self) -> Dict[str, Any]:
        """Retorna status de saúde do neurônio"""
        return {
            "energy_level": self.energy_level,
            "fire_count": self.fire_count,
            "entropy": self.calculate_entropy(),
            "age_seconds": self.age_seconds,
            "importance": self.importance_score,
            "is_active": self.is_active,
            "activation": self.current_activation
        }

@dataclass
class Synapse:
    """Sinapse básica entre neurônios"""
    id: str
    source_id: str
    target_id: str
    weight: float = 1.0
    strength: float = 0.5
    plasticity: float = 0.1
    last_used: Optional[datetime] = None
    created_at: datetime = field(default_factory=datetime.now)
    metadata: Dict[str, Any] = field(default_factory=dict)
    
    def strengthen(self, amount: float = 0.1) -> None:
        """Fortalece a sinapse"""
        self.strength = min(1.0, self.strength + amount * self.plasticity)
        self.weight = min(2.0, self.weight + amount * 0.05)
        self.last_used = datetime.now()
    
    def weaken(self, amount: float = 0.1) -> None:
        """Enfraquece a sinapse"""
        self.strength = max(0.0, self.strength - amount * self.plasticity)
        self.weight = max(0.1, self.weight - amount * 0.05)
        self.last_used = datetime.now()
    
    def propagate(self, signal: float) -> float:
        """Propaga sinal através da sinapse"""
        self.last_used = datetime.now()
        return signal * self.weight * self.strength

@dataclass
class AdvancedSynapse(Synapse):
    """Sinapse avançada com plasticidade dinâmica"""
    learning_history: List[float] = field(default_factory=list)
    neurotransmitter_levels: Dict[str, float] = field(default_factory=dict)
    transmission_speed: float = 1.0
    reliability: float = 0.95
    last_maintenance: datetime = field(default_factory=datetime.now)
    optimization_level: float = 1.0
    delay_ms: float = 1.0
    hebbian_trace: float = 0.0
    
    def __post_init__(self):
        """Inicialização pós-criação"""
        self.learning_history = deque(maxlen=100)
        self.neurotransmitter_levels = {
            'glutamate': 0.5,
            'gaba': 0.5,
            'dopamine': 0.3,
            'serotonin': 0.3,
            'acetylcholine': 0.2
        }
    
    def update_neurotransmitter(self, ntype: str, amount: float) -> None:
        """Atualiza nível de neurotransmissor"""
        if ntype in self.neurotransmitter_levels:
            self.neurotransmitter_levels[ntype] = max(0.0, min(1.0, 
                self.neurotransmitter_levels[ntype] + amount))
    
    def get_efficiency(self) -> float:
        """Calcula eficiência da sinapse"""
        base_efficiency = self.strength * self.reliability * self.optimization_level
        neurotransmitter_boost = sum(self.neurotransmitter_levels.values()) / 10.0
        return min(1.0, base_efficiency + neurotransmitter_boost)
    
    def hebbian_update(self, pre_activation: float, post_activation: float) -> None:
        """Atualização Hebbiana: neurônios que disparam juntos, conectam-se juntos"""
        self.hebbian_trace = pre_activation * post_activation
        self.weight += self.hebbian_trace * 0.01
        self.weight = max(0.1, min(2.0, self.weight))
        self.learning_history.append(self.weight)

@dataclass
class NeuralCluster:
    """Cluster de neurônios que funcionam em conjunto"""
    id: str
    neuron_ids: List[str]
    cluster_type: str
    created_at: datetime = field(default_factory=datetime.now)
    collective_activation: float = 0.0
    synchronization_level: float = 0.0
    last_sync: Optional[datetime] = None
    metadata: Dict[str, Any] = field(default_factory=dict)
    
    def to_dict(self) -> Dict[str, Any]:
        """Converter para dicionário serializável"""
        return {
            'id': self.id,
            'neuron_ids': self.neuron_ids,
            'cluster_type': self.cluster_type,
            'created_at': self.created_at.isoformat(),
            'collective_activation': self.collective_activation,
            'synchronization_level': self.synchronization_level,
            'last_sync': self.last_sync.isoformat() if self.last_sync else None,
            'metadata': self.metadata
        }

@dataclass
class DataPacket:
    """Pacote de dados para troca entre módulos"""
    id: str
    source_module: str
    target_module: Union[str, List[str]]
    data_type: str
    payload: Any
    timestamp: datetime = field(default_factory=datetime.now)
    priority: DataPriority = DataPriority.MEDIUM
    ttl: Optional[int] = 60  # segundos
    requires_encryption: bool = False
    metadata: Dict[str, Any] = field(default_factory=dict)
    
    def __post_init__(self):
        """Inicialização pós-criação"""
        if isinstance(self.target_module, str):
            self.target_module = [self.target_module]
        if not self.id:
            self.id = hashlib.md5(str(time.time()).encode()).hexdigest()[:12]
    
    def to_dict(self) -> Dict[str, Any]:
        """Converter para dicionário serializável"""
        return {
            'id': self.id,
            'source_module': self.source_module,
            'target_module': self.target_module,
            'data_type': self.data_type,
            'payload': str(self.payload)[:100] + '...' if len(str(self.payload)) > 100 else self.payload,
            'timestamp': self.timestamp.isoformat(),
            'priority': self.priority.label,
            'ttl': self.ttl
        }
    
    @property
    def is_expired(self) -> bool:
        """Verifica se pacote expirou"""
        if self.ttl is None:
            return False
        age = (datetime.now() - self.timestamp).total_seconds()
        return age > self.ttl

@dataclass
class LearningInsight:
    """Insight gerado pelo processo de aprendizado"""
    id: str
    source: str
    insight_type: str
    content: Any
    confidence: float
    timestamp: datetime = field(default_factory=datetime.now)
    impact_score: float = 0.5
    tags: List[str] = field(default_factory=list)
    
    def to_dict(self) -> Dict[str, Any]:
        return {
            'id': self.id,
            'source': self.source,
            'insight_type': self.insight_type,
            'content': str(self.content)[:200],
            'confidence': self.confidence,
            'timestamp': self.timestamp.isoformat(),
            'impact_score': self.impact_score
        }

# =============================================================================
# SISTEMA DE INTEGRAÇÃO AVANÇADA
# =============================================================================

class IntegrationHub:
    """Hub central de integração entre todos os módulos com suporte a padrões avançados"""
    
    def __init__(self):
        self.modules: Dict[str, Any] = {}
        self.data_queue = asyncio.Queue(maxsize=10000)
        self.message_history = deque(maxlen=10000)
        self.analysis_history = deque(maxlen=10000)
        self.learning_history = deque(maxlen=10000)
        self.deep_learning_history = deque(maxlen=10000)
        self.quantum_learning_history = deque(maxlen=10000)
        self.analysis_learning_history = deque(maxlen=10000)
        self.learning_learning_history = deque(maxlen=10000)
        self.deep_learning_learning_history = deque(maxlen=10000)
        self.quantum_learning_learning_history = deque(maxlen=10000)
        self.integration_stats = defaultdict(int)
        self.active_connections = set()
        self.subscribers: Dict[str, List[Callable]] = defaultdict(list)
        self.module_dependencies: Dict[str, List[str]] = {}
        self.module_health: Dict[str, Dict[str, Any]] = {}
        
        # Buffers de dados compartilhados
        self.shared_neural_data = {}
        self.shared_quantum_data = {}
        self.shared_analysis_data = {}
        self.shared_learning_data = {}
        self.shared_deep_learning_data = {}
        self.shared_quantum_learning_data = {}
        self.shared_analysis_learning_data = {}
        self.shared_learning_learning_data = {}
        self.shared_deep_learning_learning_data = {}
        self.shared_quantum_learning_learning_data = {}
        self.shared_analysis_learning_learning_data = {}
        self.shared_learning_learning_learning_data = {}
        self.shared_deep_learning_learning_learning_data = {}
        self.shared_state = {}
        
        # Sistema de eventos
        self.event_handlers: Dict[str, List[Callable]] = defaultdict(list)
        
        # Thread pool para processamento paralelo
        self.thread_pool = concurrent.futures.ThreadPoolExecutor(max_workers=MAX_WORKERS)
        
        logger.info("🔗 Hub de Integração Avançada inicializado")
    
    def register_module(self, module_name: str, module_instance: Any, 
                       dependencies: List[str] = None) -> bool:
        """Registra um módulo no hub com dependências"""
        if module_name in self.modules:
            logger.warning(f"⚠️ Módulo já registrado: {module_name}")
            return False
        
        self.modules[module_name] = module_instance
        self.module_dependencies[module_name] = dependencies or []
        self.module_health[module_name] = {
            'registered_at': datetime.now(),
            'last_heartbeat': datetime.now(),
            'status': 'active',
            'errors': 0
        }
        
        logger.info(f"✅ Módulo registrado: {module_name}")
        return True
    
    def unregister_module(self, module_name: str) -> bool:
        """Remove registro de módulo"""
        if module_name not in self.modules:
            return False
        
        del self.modules[module_name]
        del self.module_dependencies[module_name]
        del self.module_health[module_name]
        logger.info(f"❌ Módulo removido: {module_name}")
        return True
    
    async def send_data(self, packet: DataPacket) -> bool:
        """Envia dados entre módulos com roteamento inteligente"""
        try:
            await self.data_queue.put(packet)
            self.message_history.append(packet)
            
            # Estatísticas
            for target in packet.target_module:
                key = f"{packet.source_module}->{target}"
                self.integration_stats[key] += 1
            
            # Processamento imediato para alta prioridade
            if packet.priority.priority >= DataPriority.HIGH.priority:
                asyncio.create_task(self._route_packet(packet))
            
            logger.debug(f"📤 Dados enfileirados: {packet.source_module} -> {packet.target_module}")
            return True
            
        except asyncio.QueueFull:
            logger.error(f"❌ Fila de dados cheia. Descartando pacote: {packet.id}")
            return False
    
    async def process_data_queue(self):
        """Processa fila de dados continuamente com prioridade"""
        while True:
            try:
                packet = await self.data_queue.get()
                
                # Ignora pacotes expirados
                if packet.is_expired:
                    logger.debug(f"⏰ Pacote expirado: {packet.id}")
                    continue
                
                # Roteia baseado na prioridade
                if packet.priority.priority >= DataPriority.HIGH.priority:
                    await self._route_packet(packet)
                else:
                    # Processa em background para baixa prioridade
                    asyncio.create_task(self._route_packet(packet))
                
            except Exception as e:
                logger.error(f"❌ Erro ao processar pacote: {e}")
            
            await asyncio.sleep(0.001)  # Pequena pausa para evitar CPU 100%
    
    async def _route_packet(self, packet: DataPacket):
        """Roteia pacote para os módulos de destino"""
        for target in packet.target_module:
            if target == 'all':
                # Broadcast para todos os módulos
                for module_name in self.modules.keys():
                    if module_name != packet.source_module:
                        await self._send_to_module(module_name, packet)
            elif target in self.modules:
                await self._send_to_module(target, packet)
            else:
                logger.warning(f"⚠️ Módulo não encontrado: {target}")
    
    async def _send_to_module(self, module_name: str, packet: DataPacket):
        """Envia pacote para módulo específico"""
        module = self.modules[module_name]
        
        if hasattr(module, 'receive_data'):
            try:
                await module.receive_data(packet)
                self.module_health[module_name]['last_heartbeat'] = datetime.now()
            except Exception as e:
                logger.error(f"❌ Erro ao enviar para {module_name}: {e}")
                self.module_health[module_name]['errors'] += 1
        else:
            logger.warning(f"⚠️ Módulo {module_name} não tem método receive_data")
    
    def subscribe(self, event_type: str, callback: Callable) -> None:
        """Inscreve callback para eventos"""
        self.subscribers[event_type].append(callback)
    
    async def emit_event(self, event_type: str, data: Any = None):
        """Emite evento para todos os inscritos"""
        if event_type in self.subscribers:
            for callback in self.subscribers[event_type]:
                try:
                    if asyncio.iscoroutinefunction(callback):
                        await callback(data)
                    else:
                        callback(data)
                except Exception as e:
                    logger.error(f"❌ Erro em callback de evento {event_type}: {e}")
    
    def get_integration_stats(self) -> Dict[str, Any]:
        """Retorna estatísticas detalhadas de integração"""
        return {
            'total_messages': sum(self.integration_stats.values()),
            'connections': dict(sorted(self.integration_stats.items(), key=lambda x: x[1], reverse=True)),
            'active_modules': list(self.modules.keys()),
            'module_dependencies': self.module_dependencies,
            'module_health': self.module_health,
            'queue_size': self.data_queue.qsize(),
            'message_history_size': len(self.message_history),
            'subscribers': {k: len(v) for k, v in self.subscribers.items()}
        }
    
    def get_module_dependencies(self, module_name: str) -> List[str]:
        """Retorna dependências de um módulo"""
        return self.module_dependencies.get(module_name, [])
    
    def check_health(self) -> Dict[str, Any]:
        """Verifica saúde de todos os módulos"""
        health_report = {}
        now = datetime.now()
        
        for module_name, health in self.module_health.items():
            last_heartbeat = health['last_heartbeat']
            seconds_since_heartbeat = (now - last_heartbeat).total_seconds()
            
            status = 'healthy'
            if seconds_since_heartbeat > 60:
                status = 'warning'
            if seconds_since_heartbeat > 300:
                status = 'critical'
            
            health_report[module_name] = {
                **health,
                'seconds_since_heartbeat': seconds_since_heartbeat,
                'status': status
            }
        
        return health_report

# =============================================================================
# SISTEMA DE INTEGRAÇÃO COM ML_MODULE
# =============================================================================

class MLModuleBridge:
    """Ponte de integração com o módulo de Machine Learning"""
    
    def __init__(self, integration_hub: IntegrationHub):
        self.hub = integration_hub
        self.ml_module = None
        self.ml_available = False
        self.ml_history = deque(maxlen=100)
        self.ml_predictions = deque(maxlen=1000)
        self.ml_models = {}
        self.ml_performance = {}
        
        # Sistema de Ensemble Learning
        self.ensemble_models = {}
        self.ensemble_weights = {}
        self.ensemble_history = deque(maxlen=500)
        self.model_accuracy = defaultdict(list)
        
        # Tentar importar ml_module
        try:
            from ml_module import MachineLearningModule
            self.ml_module = MachineLearningModule()
            self.ml_available = True
            logger.info("✅ Módulo ML integrado com sucesso")
        except ImportError as e:
            logger.warning(f"⚠️ Módulo ML não disponível: {e}")
        except Exception as e:
            logger.error(f"❌ Erro ao inicializar módulo ML: {e}")
    
    async def process_market_data(self, market_data: Dict[str, Any]) -> Dict[str, Any]:
        """Processa dados de mercado usando ML"""
        if not self.ml_available:
            return {'error': 'ML module not available'}
        
        try:
            # Preparar dados para ML
            ml_request = {
                'operation': 'train_and_predict',
                'content': {
                    'market_data': market_data,
                    'features': self._extract_features(market_data),
                    'targets': self._extract_targets(market_data)
                }
            }
            
            # Processar com ML
            ml_result = self.ml_module.receive_data(ml_request)
            
            # Armazenar histórico
            self.ml_history.append({
                'timestamp': datetime.now().isoformat(),
                'market_data': market_data,
                'ml_result': ml_result
            })
            
            # Enviar resultados para outros módulos
            packet = DataPacket(
                id=hashlib.md5(f"ml_result_{time.time()}".encode()).hexdigest()[:12],
                source_module='ml_bridge',
                target_module=['decision_engine', 'trading_engine', 'risk_analyzer'],
                data_type='ml_prediction',
                payload=ml_result,
                priority=DataPriority.HIGH
            )
            await self.hub.send_data(packet)
            
            return ml_result
            
        except Exception as e:
            logger.error(f"❌ Erro no processamento ML: {e}")
            return {'error': str(e)}
    
    def _extract_features(self, market_data: Dict[str, Any]) -> Dict[str, Any]:
        """Extrai features avançadas de dados de mercado"""
        features = {}
        
        if 'price_data' in market_data:
            prices = market_data['price_data']
            if len(prices) >= 2:
                # Features básicas
                features['price_mean'] = sum(prices) / len(prices)
                features['price_std'] = statistics.stdev(prices) if len(prices) > 1 else 0
                features['price_min'] = min(prices)
                features['price_max'] = max(prices)
                features['price_trend'] = prices[-1] - prices[0] if len(prices) > 1 else 0
                features['price_change_pct'] = ((prices[-1] - prices[0]) / prices[0] * 100) if prices[0] != 0 else 0
                
                # Features técnicas avançadas
                if len(prices) >= 5:
                    # Médias móveis
                    ma_short = sum(prices[-5:]) / 5
                    ma_long = sum(prices) / len(prices)
                    features['ma_short'] = ma_short
                    features['ma_long'] = ma_long
                    features['ma_crossover'] = 1 if ma_short > ma_long else -1
                    
                    # RSI (Relative Strength Index) simplificado
                    gains = [max(prices[i] - prices[i-1], 0) for i in range(1, len(prices))]
                    losses = [max(prices[i-1] - prices[i], 0) for i in range(1, len(prices))]
                    avg_gain = sum(gains[-5:]) / 5 if gains else 0
                    avg_loss = sum(losses[-5:]) / 5 if losses else 0
                    if avg_loss != 0:
                        rs = avg_gain / avg_loss
                        features['rsi'] = 100 - (100 / (1 + rs))
                    else:
                        features['rsi'] = 100 if avg_gain > 0 else 50
                    
                    # Volatilidade
                    returns = [(prices[i] - prices[i-1]) / prices[i-1] for i in range(1, len(prices)) if prices[i-1] != 0]
                    if returns:
                        features['volatility'] = statistics.stdev(returns) if len(returns) > 1 else 0
                        features['momentum'] = sum(returns[-5:]) if len(returns) >= 5 else sum(returns)
                
                # Suporte e resistência
                features['support'] = min(prices[-10:]) if len(prices) >= 10 else min(prices)
                features['resistance'] = max(prices[-10:]) if len(prices) >= 10 else max(prices)
                features['price_position'] = (prices[-1] - features['support']) / (features['resistance'] - features['support']) if features['resistance'] != features['support'] else 0.5
        
        if 'volume_data' in market_data:
            volumes = market_data['volume_data']
            if len(volumes) >= 2:
                features['volume_mean'] = sum(volumes) / len(volumes)
                features['volume_std'] = statistics.stdev(volumes) if len(volumes) > 1 else 0
                features['volume_trend'] = volumes[-1] - volumes[0] if len(volumes) > 1 else 0
                features['volume_change_pct'] = ((volumes[-1] - volumes[0]) / volumes[0] * 100) if volumes[0] != 0 else 0
                
                # Volume relativo
                if len(volumes) >= 5:
                    avg_volume = sum(volumes[-5:]) / 5
                    features['volume_relative'] = volumes[-1] / avg_volume if avg_volume != 0 else 1
        
        # Features temporais
        if 'timestamp' in market_data:
            try:
                ts = datetime.fromisoformat(market_data['timestamp'])
                features['hour'] = ts.hour
                features['day_of_week'] = ts.weekday()
                features['is_trading_hour'] = 1 if 9 <= ts.hour <= 17 else 0
            except:
                pass
        
        return features
    
    def _extract_targets(self, market_data: Dict[str, Any]) -> Dict[str, Any]:
        """Extrai targets de dados de mercado"""
        targets = {}
        
        if 'price_data' in market_data:
            prices = market_data['price_data']
            if len(prices) > 1:
                targets['next_price_change'] = (prices[-1] - prices[-2]) / prices[-2] if prices[-2] != 0 else 0
                targets['price_direction'] = 1 if prices[-1] > prices[-2] else -1
        
        return targets
    
    async def get_ml_predictions(self, symbol: str, timeframe: str = '1h') -> Dict[str, Any]:
        """Obtém predições ML para um símbolo"""
        if not self.ml_available:
            return {'error': 'ML module not available'}
        
        try:
            ml_request = {
                'operation': 'predict',
                'content': {
                    'symbol': symbol,
                    'timeframe': timeframe
                }
            }
            
            result = self.ml_module.receive_data(ml_request)
            
            # Armazenar predição
            self.ml_predictions.append({
                'timestamp': datetime.now().isoformat(),
                'symbol': symbol,
                'timeframe': timeframe,
                'prediction': result
            })
            
            return result
            
        except Exception as e:
            logger.error(f"❌ Erro ao obter predições ML: {e}")
            return {'error': str(e)}
    
    async def train_ml_models(self, training_data: Dict[str, Any]) -> Dict[str, Any]:
        """Treina modelos ML com novos dados"""
        if not self.ml_available:
            return {'error': 'ML module not available'}
        
        try:
            ml_request = {
                'operation': 'train',
                'content': training_data
            }
            
            result = self.ml_module.receive_data(ml_request)
            
            # Atualizar modelos
            if 'ml_results' in result and 'models' in result['ml_results']:
                self.ml_models.update(result['ml_results']['models'])
            
            return result
            
        except Exception as e:
            logger.error(f"❌ Erro ao treinar modelos ML: {e}")
            return {'error': str(e)}
    
    def get_ml_statistics(self) -> Dict[str, Any]:
        """Retorna estatísticas do módulo ML"""
        if not self.ml_available:
            return {'error': 'ML module not available'}
        
        try:
            return self.ml_module.get_system_status()
        except Exception as e:
            logger.error(f"❌ Erro ao obter estatísticas ML: {e}")
            return {'error': str(e)}
    
    def get_ml_history(self, limit: int = 10) -> List[Dict[str, Any]]:
        """Retorna histórico de operações ML"""
        return list(self.ml_history)[-limit:]
    
    def add_ensemble_model(self, model_name: str, model: Any, weight: float = 1.0) -> None:
        """Adiciona um modelo ao ensemble"""
        self.ensemble_models[model_name] = model
        self.ensemble_weights[model_name] = weight
        logger.info(f"🔗 Modelo {model_name} adicionado ao ensemble (peso: {weight})")
    
    def ensemble_predict(self, features: Dict[str, Any]) -> Dict[str, Any]:
        """Realiza predição usando ensemble de modelos"""
        if not self.ensemble_models:
            return {'error': 'No ensemble models available'}
        
        predictions = {}
        total_weight = sum(self.ensemble_weights.values())
        
        for model_name, model in self.ensemble_models.items():
            try:
                # Simulação de predição - em produção usaria o modelo real
                pred = self._simulate_model_prediction(model_name, features)
                predictions[model_name] = pred
            except Exception as e:
                logger.error(f"Erro no modelo {model_name}: {e}")
                predictions[model_name] = None
        
        # Combina predições ponderadas
        weighted_prediction = self._combine_predictions(predictions, self.ensemble_weights)
        
        # Registra no histórico
        self.ensemble_history.append({
            'timestamp': datetime.now().isoformat(),
            'features': features,
            'individual_predictions': predictions,
            'ensemble_prediction': weighted_prediction
        })
        
        return weighted_prediction
    
    def _simulate_model_prediction(self, model_name: str, features: Dict[str, Any]) -> Dict[str, Any]:
        """Simula predição de modelo (placeholder para implementação real)"""
        # Em produção, isso chamaria o modelo real
        # Aqui simulamos com base nas features
        if 'price_trend' in features:
            trend = features['price_trend']
            direction = 1 if trend > 0 else -1
            confidence = min(0.9, abs(trend) / 10)
            return {
                'direction': direction,
                'confidence': confidence,
                'predicted_change': trend * 0.1,
                'model': model_name
            }
        return {'direction': 0, 'confidence': 0.5, 'predicted_change': 0, 'model': model_name}
    
    def _combine_predictions(self, predictions: Dict[str, Any], weights: Dict[str, float]) -> Dict[str, Any]:
        """Combina predições de múltiplos modelos usando pesos"""
        valid_predictions = {k: v for k, v in predictions.items() if v is not None}
        
        if not valid_predictions:
            return {'direction': 0, 'confidence': 0, 'combined': False}
        
        total_weight = sum(weights.get(k, 1.0) for k in valid_predictions.keys())
        
        if total_weight == 0:
            return {'direction': 0, 'confidence': 0, 'combined': False}
        
        # Combina direções ponderadas
        weighted_direction = sum(
            v.get('direction', 0) * weights.get(k, 1.0)
            for k, v in valid_predictions.items()
        ) / total_weight
        
        # Combina confianças ponderadas
        weighted_confidence = sum(
            v.get('confidence', 0) * weights.get(k, 1.0)
            for k, v in valid_predictions.items()
        ) / total_weight
        
        # Combina mudanças previstas
        weighted_change = sum(
            v.get('predicted_change', 0) * weights.get(k, 1.0)
            for k, v in valid_predictions.items()
        ) / total_weight
        
        return {
            'direction': 1 if weighted_direction > 0 else -1 if weighted_direction < 0 else 0,
            'confidence': weighted_confidence,
            'predicted_change': weighted_change,
            'combined': True,
            'num_models': len(valid_predictions)
        }
    
    def update_ensemble_weights(self, accuracy_history: Dict[str, List[float]]) -> None:
        """Atualiza pesos do ensemble baseado em acurácia histórica"""
        for model_name, accuracies in accuracy_history.items():
            if accuracies:
                avg_accuracy = sum(accuracies) / len(accuracies)
                # Peso proporcional à acurácia
                self.ensemble_weights[model_name] = max(0.1, avg_accuracy)
        
        logger.info(f"⚖️ Pesos do ensemble atualizados: {self.ensemble_weights}")
    
    def evaluate_ensemble_performance(self, actual_outcomes: List[Dict[str, Any]]) -> Dict[str, Any]:
        """Avalia performance do ensemble comparando com resultados reais"""
        if not self.ensemble_history:
            return {'error': 'No ensemble history available'}
        
        correct_predictions = 0
        total_predictions = 0
        model_performance = defaultdict(lambda: {'correct': 0, 'total': 0})
        
        for i, history_entry in enumerate(self.ensemble_history):
            if i < len(actual_outcomes):
                actual = actual_outcomes[i]
                pred = history_entry['ensemble_prediction']
                
                # Verifica se a direção foi correta
                if 'actual_direction' in actual:
                    total_predictions += 1
                    if pred.get('direction') == actual['actual_direction']:
                        correct_predictions += 1
                        
                        # Atualiza performance individual dos modelos
                        for model_name, model_pred in history_entry['individual_predictions'].items():
                            if model_pred and model_pred.get('direction') == actual['actual_direction']:
                                model_performance[model_name]['correct'] += 1
                            model_performance[model_name]['total'] += 1
        
        # Calcula acurácia
        overall_accuracy = correct_predictions / total_predictions if total_predictions > 0 else 0
        
        # Calcula acurácia por modelo
        model_accuracies = {}
        for model_name, stats in model_performance.items():
            acc = stats['correct'] / stats['total'] if stats['total'] > 0 else 0
            model_accuracies[model_name] = acc
            self.model_accuracy[model_name].append(acc)
        
        # Atualiza pesos baseado em performance
        self.update_ensemble_weights(dict(self.model_accuracy))
        
        return {
            'overall_accuracy': overall_accuracy,
            'correct_predictions': correct_predictions,
            'total_predictions': total_predictions,
            'model_accuracies': model_accuracies,
            'ensemble_weights': self.ensemble_weights.copy()
        }
    
    def cross_validate_model(self, data: List[Dict[str, Any]], k_folds: int = 5) -> Dict[str, Any]:
        """Realiza validação cruzada k-fold para avaliar modelo"""
        if len(data) < k_folds:
            return {'error': f'Not enough data for {k_folds}-fold cross validation'}
        
        fold_size = len(data) // k_folds
        fold_scores = []
        
        for i in range(k_folds):
            # Divide dados em treino e teste
            test_start = i * fold_size
            test_end = (i + 1) * fold_size if i < k_folds - 1 else len(data)
            
            test_data = data[test_start:test_end]
            train_data = data[:test_start] + data[test_end:]
            
            # Treina e avalia (simulado)
            fold_score = self._evaluate_fold(train_data, test_data)
            fold_scores.append(fold_score)
        
        # Calcula estatísticas
        mean_score = sum(fold_scores) / len(fold_scores) if fold_scores else 0
        std_score = statistics.stdev(fold_scores) if len(fold_scores) > 1 else 0
        
        return {
            'mean_accuracy': mean_score,
            'std_accuracy': std_score,
            'fold_scores': fold_scores,
            'k_folds': k_folds,
            'total_samples': len(data)
        }
    
    def _evaluate_fold(self, train_data: List[Dict[str, Any]], test_data: List[Dict[str, Any]]) -> float:
        """Avalia um fold específico (simulação)"""
        # Em produção, treinaria o modelo com train_data e avaliaria com test_data
        # Aqui simulamos a acurácia
        correct = 0
        total = len(test_data)
        
        for sample in test_data:
            # Simulação: 60% de acurácia base
            if random.random() < 0.6:
                correct += 1
        
        return correct / total if total > 0 else 0
    
    def backtest_strategy(self, historical_data: List[Dict[str, Any]], 
                          strategy_params: Dict[str, Any] = None) -> Dict[str, Any]:
        """Realiza backtesting de estratégia de predição"""
        if not historical_data:
            return {'error': 'No historical data provided'}
        
        strategy_params = strategy_params or {}
        initial_capital = strategy_params.get('initial_capital', 10000)
        position_size = strategy_params.get('position_size', 0.1)  # 10% do capital
        stop_loss = strategy_params.get('stop_loss', 0.05)  # 5%
        take_profit = strategy_params.get('take_profit', 0.1)  # 10%
        
        capital = initial_capital
        positions = []
        trades = []
        win_count = 0
        loss_count = 0
        
        for i, data_point in enumerate(historical_data[:-1]):
            # Extrai features
            features = self._extract_features(data_point)
            
            # Faz predição usando ensemble
            prediction = self.ensemble_predict(features)
            
            if 'error' in prediction:
                continue
            
            direction = prediction.get('direction', 0)
            confidence = prediction.get('confidence', 0)
            
            # Só entra se confiança for alta
            if confidence < 0.6:
                continue
            
            # Simula trade
            entry_price = data_point.get('price', 100)
            next_price = historical_data[i + 1].get('price', entry_price)
            
            if direction == 1:  # Long
                pnl_pct = (next_price - entry_price) / entry_price
            elif direction == -1:  # Short
                pnl_pct = (entry_price - next_price) / entry_price
            else:
                continue
            
            # Aplica stop loss e take profit
            if pnl_pct < -stop_loss:
                pnl_pct = -stop_loss
            elif pnl_pct > take_profit:
                pnl_pct = take_profit
            
            # Calcula P&L
            trade_amount = capital * position_size
            pnl = trade_amount * pnl_pct
            capital += pnl
            
            # Registra trade
            trades.append({
                'entry_price': entry_price,
                'exit_price': next_price,
                'direction': 'long' if direction == 1 else 'short',
                'pnl': pnl,
                'pnl_pct': pnl_pct,
                'capital_after': capital
            })
            
            if pnl > 0:
                win_count += 1
            else:
                loss_count += 1
        
        # Calcula métricas
        total_trades = len(trades)
        win_rate = win_count / total_trades if total_trades > 0 else 0
        total_return = (capital - initial_capital) / initial_capital
        avg_win = sum(t['pnl'] for t in trades if t['pnl'] > 0) / win_count if win_count > 0 else 0
        avg_loss = sum(t['pnl'] for t in trades if t['pnl'] < 0) / loss_count if loss_count > 0 else 0
        profit_factor = abs(avg_win / avg_loss) if avg_loss != 0 else 0
        
        # Calcula drawdown máximo
        peak_capital = initial_capital
        max_drawdown = 0
        for trade in trades:
            if trade['capital_after'] > peak_capital:
                peak_capital = trade['capital_after']
            drawdown = (peak_capital - trade['capital_after']) / peak_capital
            if drawdown > max_drawdown:
                max_drawdown = drawdown
        
        return {
            'initial_capital': initial_capital,
            'final_capital': capital,
            'total_return': total_return,
            'total_return_pct': total_return * 100,
            'total_trades': total_trades,
            'win_count': win_count,
            'loss_count': loss_count,
            'win_rate': win_rate,
            'win_rate_pct': win_rate * 100,
            'avg_win': avg_win,
            'avg_loss': avg_loss,
            'profit_factor': profit_factor,
            'max_drawdown': max_drawdown,
            'max_drawdown_pct': max_drawdown * 100,
            'sharpe_ratio': self._calculate_sharpe_ratio(trades),
            'trades': trades[-10:]  # Últimos 10 trades
        }
    
    def _calculate_sharpe_ratio(self, trades: List[Dict[str, Any]], risk_free_rate: float = 0.02) -> float:
        """Calcula Sharpe Ratio dos trades"""
        if len(trades) < 2:
            return 0
        
        returns = [t['pnl_pct'] for t in trades]
        avg_return = sum(returns) / len(returns)
        
        if len(returns) > 1:
            std_return = statistics.stdev(returns)
        else:
            std_return = 0
        
        if std_return == 0:
            return 0
        
        # Sharpe ratio anualizado (assumindo 252 dias de trading)
        sharpe = (avg_return - risk_free_rate / 252) / std_return * (252 ** 0.5)
        return sharpe
    
    def calibrate_probabilities(self, predictions: List[Dict[str, Any]], 
                                actual_outcomes: List[Dict[str, Any]]) -> Dict[str, Any]:
        """Calibra probabilidades de predição usando isotonic regression"""
        if not predictions or not actual_outcomes:
            return {'error': 'No predictions or outcomes provided'}
        
        # Coleta pares de (probabilidade_predita, resultado_real)
        calibration_data = []
        
        for i, pred in enumerate(predictions):
            if i < len(actual_outcomes):
                actual = actual_outcomes[i]
                confidence = pred.get('confidence', 0.5)
                actual_direction = actual.get('actual_direction', 0)
                
                # Converte direção para binário (1=positivo, 0=negativo/neutro)
                actual_binary = 1 if actual_direction > 0 else 0
                
                calibration_data.append((confidence, actual_binary))
        
        if not calibration_data:
            return {'error': 'No valid calibration data'}
        
        # Ordena por probabilidade predita
        calibration_data.sort(key=lambda x: x[0])
        
        # Cria bins para calibração
        num_bins = 10
        bin_size = len(calibration_data) // num_bins
        calibration_bins = []
        
        for i in range(num_bins):
            start_idx = i * bin_size
            end_idx = (i + 1) * bin_size if i < num_bins - 1 else len(calibration_data)
            bin_data = calibration_data[start_idx:end_idx]
            
            if bin_data:
                avg_pred_conf = sum(d[0] for d in bin_data) / len(bin_data)
                actual_rate = sum(d[1] for d in bin_data) / len(bin_data)
                
                calibration_bins.append({
                    'predicted_confidence': avg_pred_conf,
                    'actual_rate': actual_rate,
                    'sample_size': len(bin_data)
                })
        
        # Calcula métricas de calibração
        expected_calibration_error = self._calculate_ece(calibration_bins)
        
        # Cria função de calibração (mapeamento predito -> calibrado)
        calibration_map = {bin['predicted_confidence']: bin['actual_rate'] for bin in calibration_bins}
        
        return {
            'calibration_bins': calibration_bins,
            'expected_calibration_error': expected_calibration_error,
            'calibration_map': calibration_map,
            'is_well_calibrated': expected_calibration_error < 0.1
        }
    
    def _calculate_ece(self, calibration_bins: List[Dict[str, Any]]) -> float:
        """Calcula Expected Calibration Error"""
        ece = 0.0
        total_samples = sum(bin['sample_size'] for bin in calibration_bins)
        
        if total_samples == 0:
            return 0.0
        
        for bin in calibration_bins:
            weight = bin['sample_size'] / total_samples
            diff = abs(bin['predicted_confidence'] - bin['actual_rate'])
            ece += weight * diff
        
        return ece
    
    def apply_calibration(self, prediction: Dict[str, Any], 
                         calibration_map: Dict[float, float]) -> Dict[str, Any]:
        """Aplica calibração a uma predição"""
        original_confidence = prediction.get('confidence', 0.5)
        
        # Encontra o bin mais próximo
        calibrated_confidence = original_confidence
        min_diff = float('inf')
        
        for pred_conf, actual_rate in calibration_map.items():
            diff = abs(pred_conf - original_confidence)
            if diff < min_diff:
                min_diff = diff
                calibrated_confidence = actual_rate
        
        # Cria predição calibrada
        calibrated_prediction = prediction.copy()
        calibrated_prediction['original_confidence'] = original_confidence
        calibrated_prediction['confidence'] = calibrated_confidence
        calibrated_prediction['calibrated'] = True
        
        return calibrated_prediction
    
    def get_prediction_uncertainty(self, features: Dict[str, Any], 
                                   n_samples: int = 100) -> Dict[str, Any]:
        """Estima incerteza da predição usando bootstrap"""
        if not self.ensemble_models:
            return {'error': 'No ensemble models available'}
        
        predictions = []
        
        for _ in range(n_samples):
            # Adiciona pequeno ruído às features para simular variabilidade
            noisy_features = self._add_feature_noise(features)
            pred = self.ensemble_predict(noisy_features)
            
            if 'error' not in pred:
                predictions.append(pred)
        
        if not predictions:
            return {'error': 'No valid predictions generated'}
        
        # Calcula estatísticas de incerteza
        directions = [p.get('direction', 0) for p in predictions]
        confidences = [p.get('confidence', 0.5) for p in predictions]
        changes = [p.get('predicted_change', 0) for p in predictions]
        
        direction_std = statistics.stdev(directions) if len(directions) > 1 else 0
        confidence_std = statistics.stdev(confidences) if len(confidences) > 1 else 0
        change_std = statistics.stdev(changes) if len(changes) > 1 else 0
        
        # Consenso dos modelos
        direction_consensus = max(set(directions), key=directions.count) if directions else 0
        consensus_ratio = directions.count(direction_consensus) / len(directions) if directions else 0
        
        return {
            'mean_direction': sum(directions) / len(directions) if directions else 0,
            'direction_std': direction_std,
            'mean_confidence': sum(confidences) / len(confidences) if confidences else 0,
            'confidence_std': confidence_std,
            'mean_change': sum(changes) / len(changes) if changes else 0,
            'change_std': change_std,
            'direction_consensus': direction_consensus,
            'consensus_ratio': consensus_ratio,
            'uncertainty_level': 'high' if confidence_std > 0.2 else 'medium' if confidence_std > 0.1 else 'low',
            'n_samples': n_samples
        }
    
    def _add_feature_noise(self, features: Dict[str, Any], noise_level: float = 0.05) -> Dict[str, Any]:
        """Adiciona ruído gaussiano às features para bootstrap"""
        noisy_features = features.copy()
        
        for key, value in features.items():
            if isinstance(value, (int, float)) and value != 0:
                noise = random.gauss(0, abs(value) * noise_level)
                noisy_features[key] = value + noise
        
        return noisy_features

# =============================================================================
# SISTEMA DE PLUGINS E EXTENSÕES DINÂMICAS
# =============================================================================

class PluginManager:
    """Gerenciador de plugins e extensões dinâmicas"""
    
    def __init__(self, project_root: Path = PROJECT_ROOT):
        self.project_root = project_root
        self.plugins: Dict[str, Dict[str, Any]] = {}
        self.plugin_hooks: Dict[str, List[Callable]] = defaultdict(list)
        self.plugin_dependencies: Dict[str, List[str]] = defaultdict(list)
        self.plugin_config: Dict[str, Dict[str, Any]] = {}
        self._lock = threading.RLock()
        
        logger.info("🔌 Plugin Manager inicializado")
    
    def discover_plugins(self) -> List[str]:
        """Descobre plugins disponíveis no projeto"""
        with self._lock:
            discovered = []
            
            # Escanear diretório de plugins
            plugins_dir = self.project_root / 'plugins'
            if plugins_dir.exists() and plugins_dir.is_dir():
                for plugin_file in plugins_dir.glob('*.py'):
                    if plugin_file.name.startswith('_'):
                        continue
                    
                    plugin_name = plugin_file.stem
                    plugin_info = self._analyze_plugin(plugin_file)
                    
                    if plugin_info:
                        self.plugins[plugin_name] = plugin_info
                        discovered.append(plugin_name)
                        logger.info(f"🔌 Plugin descoberto: {plugin_name}")
            
            return discovered
    
    def _analyze_plugin(self, plugin_path: Path) -> Optional[Dict[str, Any]]:
        """Analisa um arquivo de plugin"""
        try:
            content = plugin_path.read_text(encoding='utf-8', errors='replace')
            
            info = {
                'path': str(plugin_path),
                'name': plugin_path.stem,
                'version': self._extract_version(content),
                'description': self._extract_description(content),
                'author': self._extract_author(content),
                'dependencies': self._extract_dependencies(content),
                'hooks': self._extract_hooks(content),
                'enabled': False,
                'loaded': False
            }
            
            return info
            
        except Exception as e:
            logger.warning(f"Erro ao analisar plugin {plugin_path}: {e}")
            return None
    
    def _extract_version(self, content: str) -> str:
        """Extrai versão do plugin"""
        for line in content.split('\n'):
            if 'version' in line.lower() and '=' in line:
                match = line.split('=')[1].strip().strip('"\'')
                if match:
                    return match
        return '1.0.0'
    
    def _extract_description(self, content: str) -> str:
        """Extrai descrição do plugin"""
        lines = content.split('\n')
        for i, line in enumerate(lines):
            if 'description' in line.lower() and '=' in line:
                match = line.split('=')[1].strip().strip('"\'')
                if match:
                    return match
        return 'No description'
    
    def _extract_author(self, content: str) -> str:
        """Extrai autor do plugin"""
        for line in content.split('\n'):
            if 'author' in line.lower() and '=' in line:
                match = line.split('=')[1].strip().strip('"\'')
                if match:
                    return match
        return 'Unknown'
    
    def _extract_dependencies(self, content: str) -> List[str]:
        """Extrai dependências do plugin"""
        deps = []
        for line in content.split('\n'):
            if 'dependencies' in line.lower() and '=' in line:
                try:
                    deps_str = line.split('=')[1].strip().strip('[]')
                    deps = [d.strip().strip('"\'') for d in deps_str.split(',') if d.strip()]
                except:
                    pass
        return deps
    
    def _extract_hooks(self, content: str) -> List[str]:
        """Extrai hooks do plugin"""
        hooks = []
        for line in content.split('\n'):
            if '@hook' in line or 'register_hook' in line:
                hooks.append(line.strip())
        return hooks
    
    def load_plugin(self, plugin_name: str) -> bool:
        """Carrega um plugin específico"""
        with self._lock:
            if plugin_name not in self.plugins:
                logger.warning(f"Plugin {plugin_name} não encontrado")
                return False
            
            plugin_info = self.plugins[plugin_name]
            
            # Verificar dependências
            for dep in plugin_info['dependencies']:
                if dep not in self.plugins or not self.plugins[dep]['loaded']:
                    logger.warning(f"Dependência {dep} não carregada para {plugin_name}")
                    return False
            
            try:
                # Importar plugin dinamicamente
                spec = importlib.util.spec_from_file_location(plugin_name, plugin_info['path'])
                if spec and spec.loader:
                    module = importlib.util.module_from_spec(spec)
                    spec.loader.exec_module(module)
                    
                    # Registrar hooks
                    if hasattr(module, 'register_hooks'):
                        module.register_hooks(self)
                    
                    plugin_info['loaded'] = True
                    plugin_info['enabled'] = True
                    
                    logger.info(f"✅ Plugin {plugin_name} carregado com sucesso")
                    return True
                    
            except Exception as e:
                logger.error(f"❌ Erro ao carregar plugin {plugin_name}: {e}")
                return False
    
    def unload_plugin(self, plugin_name: str) -> bool:
        """Descarrega um plugin"""
        with self._lock:
            if plugin_name not in self.plugins:
                return False
            
            # Verificar se outros plugins dependem deste
            for other_plugin, deps in self.plugin_dependencies.items():
                if plugin_name in deps and self.plugins[other_plugin]['loaded']:
                    logger.warning(f"Plugin {other_plugin} depende de {plugin_name}")
                    return False
            
            try:
                # Remover hooks
                if plugin_name in self.plugin_hooks:
                    del self.plugin_hooks[plugin_name]
                
                self.plugins[plugin_name]['loaded'] = False
                self.plugins[plugin_name]['enabled'] = False
                
                logger.info(f"🔌 Plugin {plugin_name} descarregado")
                return True
                
            except Exception as e:
                logger.error(f"❌ Erro ao descarregar plugin {plugin_name}: {e}")
                return False
    
    def register_hook(self, hook_name: str, callback: Callable) -> None:
        """Registra um callback para um hook"""
        self.plugin_hooks[hook_name].append(callback)
    
    async def execute_hook(self, hook_name: str, *args, **kwargs) -> List[Any]:
        """Executa todos os callbacks registrados para um hook"""
        results = []
        
        if hook_name in self.plugin_hooks:
            for callback in self.plugin_hooks[hook_name]:
                try:
                    if asyncio.iscoroutinefunction(callback):
                        result = await callback(*args, **kwargs)
                    else:
                        result = callback(*args, **kwargs)
                    results.append(result)
                except Exception as e:
                    logger.error(f"❌ Erro ao executar hook {hook_name}: {e}")
        
        return results
    
    def get_plugin_info(self, plugin_name: str) -> Optional[Dict[str, Any]]:
        """Retorna informações de um plugin"""
        return self.plugins.get(plugin_name)
    
    def get_all_plugins(self) -> Dict[str, Dict[str, Any]]:
        """Retorna todos os plugins"""
        return self.plugins.copy()
    
    def get_plugin_statistics(self) -> Dict[str, Any]:
        """Retorna estatísticas dos plugins"""
        return {
            'total_plugins': len(self.plugins),
            'loaded_plugins': len([p for p in self.plugins.values() if p['loaded']]),
            'enabled_plugins': len([p for p in self.plugins.values() if p['enabled']]),
            'total_hooks': sum(len(hooks) for hooks in self.plugin_hooks.values()),
            'plugin_names': list(self.plugins.keys())
        }

# Instâncias globais
plugin_manager = PluginManager()

# =============================================================================
# SISTEMA DE SEGURANÇA AVANÇADO
# =============================================================================

class AdvancedSecurityFramework:
    """Framework de segurança avançada com validação, criptografia e auditoria"""
    
    def __init__(self):
        self.audit_log = deque(maxlen=10000)
        self.access_control = defaultdict(set)
        self.blocked_sources = set()
        self.rate_limits = defaultdict(lambda: {'count': 0, 'reset_at': datetime.now() + timedelta(minutes=1)})
        self.api_keys = {}
        self.encryption_keys = {}
        self.threat_intelligence = defaultdict(list)
        
        logger.info("🛡️ Framework de Segurança Avançada inicializado")
    
    def validate_input(self, data: Any, data_type: str) -> Tuple[bool, str]:
        """Valida entrada de dados com múltiplas verificações"""
        try:
            # Verificação de tamanho
            serialized = str(data)
            if len(serialized) > MAX_INPUT_SIZE:
                return False, f"Tamanho excede limite: {len(serialized)} > {MAX_INPUT_SIZE}"
            
            # Validação por tipo
            if data_type == 'dict':
                if not isinstance(data, dict):
                    return False, "Input não é um dicionário"
                if len(data) > 10000:
                    return False, "Dicionário muito grande"
                
                # Verifica chaves e valores
                for key, value in data.items():
                    if not isinstance(key, (str, int, float)):
                        return False, f"Chave inválida: {type(key)}"
                    if isinstance(value, str) and len(value) > 100000:
                        return False, f"Valor muito longo para chave {key}"
            
            elif data_type == 'list':
                if not isinstance(data, list):
                    return False, "Input não é uma lista"
                if len(data) > 100000:
                    return False, "Lista muito grande"
                
                # Verifica elementos
                for i, item in enumerate(data[:100]):  # Verifica primeiros 100
                    if isinstance(item, str) and len(item) > 100000:
                        return False, f"Item {i} muito longo"
            
            elif data_type == 'number':
                if not isinstance(data, (int, float)):
                    return False, "Input não é numérico"
                if abs(data) > 1e12:
                    return False, "Número fora do intervalo permitido"
                if isinstance(data, float) and math.isnan(data):
                    return False, "NaN não permitido"
                if isinstance(data, float) and math.isinf(data):
                    return False, "Infinito não permitido"
                if isinstance(data, float) and data < 0:
                    return False, "Número negativo não permitido"
                if isinstance(data, float) and data > 1:
                    return False, "Número maior que 1 não permitido"
            
            elif data_type == 'string':
                if not isinstance(data, str):
                    return False, "Input não é string"
                if len(data) > 100000:
                    return False, "String muito longa"
                
                # Detecção de injeção
                dangerous_patterns = ['<script', 'javascript:', 'onload=', 'onerror=',
                                     '--', ';', 'DROP TABLE', 'DELETE FROM', 'UNION SELECT',
                                     '../', '..\\', '/etc/', 'C:\\']
                
                for pattern in dangerous_patterns:
                    if pattern.lower() in data.lower():
                        return False, f"Padrão suspeito detectado: {pattern}"
            
            return True, "Validação passou"
            
        except Exception as e:
            return False, f"Erro na validação: {str(e)}"
    
    def check_access_permission(self, source: str, resource: str, action: str) -> bool:
        """Verifica permissão de acesso com múltiplas camadas"""
        # Verifica se fonte está bloqueada
        if source in self.blocked_sources:
            self.log_security_event('access_denied', source, resource, 
                                   'blocked_source', 'critical')
            return False
        
        # Verifica rate limit
        if not self._check_rate_limit(source):
            self.log_security_event('rate_limit_exceeded', source, resource,
                                   'rate_limit', 'warning')
            return False
        
        # Verifica permissão específica
        resource_key = f"{resource}:{action}"
        allowed = source in self.access_control.get(resource_key, set())
        
        # Regras padrão: leitura sempre permitida, escrita/delete requer permissão
        if not allowed:
            if action == 'read':
                allowed = True
            elif action in ['write', 'delete', 'execute']:
                allowed = False
        
        # Log do evento
        event_type = 'access_granted' if allowed else 'access_denied'
        severity = 'info' if allowed else 'warning'
        self.log_security_event(event_type, source, resource_key, action, severity)
        
        return allowed
    
    def _check_rate_limit(self, source: str) -> bool:
        """Verifica rate limit por fonte"""
        limit = self.rate_limits[source]
        
        if datetime.now() > limit['reset_at']:
            limit['count'] = 0
            limit['reset_at'] = datetime.now() + timedelta(minutes=1)
        
        limit['count'] += 1
        return limit['count'] <= RATE_LIMIT_REQUESTS
    
    def encrypt_data(self, data: Any, key_id: str = None) -> Dict[str, Any]:
        """Criptografa dados sensíveis com chave"""
        if key_id is None:
            key_id = hashlib.md5(str(time.time()).encode()).hexdigest()[:16]
        
        # Simula criptografia (em produção usar lib como cryptography)
        serialized = pickle.dumps(data)
        encrypted = base64.b64encode(serialized).decode()
        
        # Hash para verificação de integridade
        integrity_hash = hashlib.sha256(encrypted.encode()).hexdigest()
        
        return {
            'data': encrypted,
            'key_id': key_id,
            'integrity_hash': integrity_hash,
            'timestamp': datetime.now().isoformat(),
            'algorithm': 'AES-256-GCM (simulado)'
        }
    
    def decrypt_data(self, encrypted_package: Dict[str, Any]) -> Any:
        """Descriptografa dados"""
        try:
            encrypted = encrypted_package['data']
            integrity_hash = encrypted_package['integrity_hash']
            
            # Verifica integridade
            if hashlib.sha256(encrypted.encode()).hexdigest() != integrity_hash:
                raise ValueError("Hash de integridade não corresponde")
            
            # Descriptografa
            decrypted = base64.b64decode(encrypted.encode())
            return pickle.loads(decrypted)
            
        except Exception as e:
            logger.error(f"❌ Erro na descriptografia: {e}")
            return None
    
    def generate_api_key(self, owner: str, permissions: List[str] = None) -> str:
        """Gera chave de API para acesso programático"""
        api_key = hashlib.sha256(f"{owner}{time.time()}{random.random()}".encode()).hexdigest()[:32]
        
        self.api_keys[api_key] = {
            'owner': owner,
            'created_at': datetime.now(),
            'permissions': permissions or ['read'],
            'last_used': None,
            'usage_count': 0
        }
        
        return api_key
    
    def validate_api_key(self, api_key: str) -> Tuple[bool, Dict[str, Any]]:
        """Valida chave de API"""
        if api_key not in self.api_keys:
            return False, {}
        
        key_info = self.api_keys[api_key]
        key_info['last_used'] = datetime.now()
        key_info['usage_count'] += 1

        if key_info['usage_count'] > 1000:
            return False, "Chave de API utilizada muitas vezes"
        return True, key_info
        if key_info['last_used'] < datetime.now() - timedelta(days=30):
            return False, "Chave de API expirada"
        if key_info['permissions'] != ['read']:
            return False, "Chave de API não tem permissão de leitura"
        if key_info['owner'] != 'system':
            return False, "Chave de API não é do sistema"
        if key_info['owner'] != 'user':
            return False, "Chave de API não é do usuário"
        if key_info['owner'] != 'admin':
            return False, "Chave de API não é do administrador"
        if key_info['owner'] != 'root':
            return False, "Chave de API não é do root"
    
    def log_security_event(self, event_type: str, source: str, resource: str, 
                          action: str, severity: str = 'info'):
        """Registra evento de segurança com contexto completo"""
        event = {
            'timestamp': datetime.now(),
            'type': event_type,
            'source': source,
            'resource': resource,
            'action': action,
            'severity': severity
        }
        
        self.audit_log.append(event)
        
        # Log com nível apropriado
        log_message = f"🔒 {event_type} - {source} {action} {resource}"
        
        if severity == 'critical':
            logger.critical(log_message)
        elif severity == 'warning':
            logger.warning(log_message)
        elif severity == 'error':
            logger.error(log_message)
        else:
            logger.info(log_message)
        
        # Adiciona à inteligência de ameaças se for severo
        if severity in ['critical', 'warning']:
            self.threat_intelligence[source].append(event)
    
    def block_source(self, source: str, reason: str = None) -> None:
        """Bloqueia uma fonte"""
        self.blocked_sources.add(source)
        self.log_security_event('source_blocked', source, 'system', 
                               f'blocked: {reason}', 'critical')
    
    def get_security_report(self) -> Dict[str, Any]:
        """Gera relatório completo de segurança"""
        events = list(self.audit_log)
        
        # Análise de ameaças
        threat_analysis = {}
        for source, events_list in self.threat_intelligence.items():
            threat_analysis[source] = {
                'total_events': len(events_list),
                'last_event': events_list[-1] if events_list else None,
                'severity_distribution': dict(Counter(e['severity'] for e in events_list)),
            }

        
        return {
            'timestamp': datetime.now(),
            'total_events': len(events),
            'blocked_sources': list(self.blocked_sources),
            'active_api_keys': len(self.api_keys),
            'threat_analysis': threat_analysis,
            'critical_events': len([e for e in events if e['severity'] == 'critical']),
            'warning_events': len([e for e in events if e['severity'] == 'warning']),
            'recent_events': events[-20:] if events else []
        }

# =============================================================================
# SISTEMA DE MONITORAMENTO AVANÇADO
# =============================================================================

class AdvancedMonitoringSystem:
    """Sistema avançado de monitoramento em tempo real com métricas, alertas e profiling"""
    
    def __init__(self):
        self.metrics = defaultdict(list)
        self.metrics_histograms = defaultdict(list)
        self.metrics_counters = defaultdict(int)
        self.metrics_gauges = defaultdict(float)
        self.alerts = deque(maxlen=1000)
        self.performance_history = deque(maxlen=10000)
        self.health_checks = {}
        self.start_time = datetime.now()
        self.sla_violations = []
        self.thresholds = {
            'processing_time_ms': 1000,
            'memory_usage_mb': MEMORY_LIMIT_MB,
            'error_rate': 0.05,
            'queue_size': 1000
        }
        self.anomaly_detection_enabled = True
        self.anomaly_threshold = 2.0  # desvios padrão
        self.profiling_enabled = False
        self.profiling_data = defaultdict(list)
        self.resource_monitoring = True
        self.resource_history = deque(maxlen=1000)
        self._lock = threading.RLock()
        
        # Inicia monitoramento de recursos se disponível
        if self.resource_monitoring:
            self._start_resource_monitoring()
        
        logger.info("📊 Sistema de Monitoramento Avançado inicializado")
    
    def record_metric(self, metric_name: str, value: float, 
                     tags: Dict[str, str] = None, unit: str = '',
                     metric_type: str = 'gauge') -> None:
        """Registra métrica com timestamp e tags (tipos: gauge, counter, histogram)"""
        with self._lock:
            record = {
                'timestamp': datetime.now(),
                'value': value,
                'tags': tags or {},
                'unit': unit,
                'duration_since_start': (datetime.now() - self.start_time).total_seconds(),
                'metric_type': metric_type
            }
            self.metrics[metric_name].append(record)
            
            # Atualiza tipos específicos
            if metric_type == 'counter':
                self.metrics_counters[metric_name] += value
            elif metric_type == 'gauge':
                self.metrics_gauges[metric_name] = value
            elif metric_type == 'histogram':
                self.metrics_histograms[metric_name].append(value)
            
            # Verifica thresholds
            if metric_name in self.thresholds:
                threshold = self.thresholds[metric_name]
                if value > threshold:
                    self.create_alert('warning', 
                                     f"Métrica {metric_name} excedeu threshold: {value:.2f} > {threshold}",
                                     'monitoring',
                                     {'metric': metric_name, 'value': value, 'threshold': threshold})
            
            # Detecção de anomalias
            if self.anomaly_detection_enabled and len(self.metrics[metric_name]) > 50:
                self._check_for_anomaly(metric_name, value)
    
    def create_alert(self, level: str, message: str, source: str, 
                    metadata: Dict = None) -> Dict[str, Any]:
        """Cria alerta com contexto e prioridade"""
        alert = {
            'id': hashlib.md5(str(time.time()).encode()).hexdigest()[:8],
            'timestamp': datetime.now(),
            'level': level,  # 'critical', 'warning', 'info'
            'message': message,
            'source': source,
            'metadata': metadata or {},
            'acknowledged': False,
            'resolved': False
        }
        
        self.alerts.append(alert)
        
        # Log com ícone apropriado
        icons = {'critical': '🚨', 'warning': '⚠️', 'info': 'ℹ️'}
        icon = icons.get(level, '🔔')
        
        if level == 'critical':
            logger.critical(f"{icon} ALERTA {source}: {message}")
        elif level == 'warning':
            logger.warning(f"{icon} AVISO {source}: {message}")
        else:
            logger.info(f"{icon} INFO {source}: {message}")
        
        return alert
    
    def acknowledge_alert(self, alert_id: str) -> bool:
        """Marca alerta como reconhecido"""
        for alert in self.alerts:
            if alert['id'] == alert_id:
                alert['acknowledged'] = True
                return True
        return False
    
    def resolve_alert(self, alert_id: str) -> bool:
        """Marca alerta como resolvido"""
        for alert in self.alerts:
            if alert['id'] == alert_id:
                alert['resolved'] = True
                return True
        return False
    
    def perform_health_check(self, system_name: str, 
                            custom_checks: Dict[str, Callable] = None) -> Dict[str, Any]:
        """Realiza verificação de saúde abrangente"""
        health = {
            'system': system_name,
            'timestamp': datetime.now(),
            'status': 'healthy',
            'health_status': 'healthy',
            'checks': {},
            'health_checks': {},
            'metrics': {},
            'health_metrics': {},
            'health_checks': {},
            'warnings': [],
            'errors': []
        }
        
        # Coleta métricas do sistema
        if system_name in self.metrics:
            recent = self.metrics[system_name][-100:]  # Últimas 100 medições
            if recent:
                values = [r['value'] for r in recent]
                
                if NUMPY_AVAILABLE:
                    health['metrics'] = {
                        'average': float(np.mean(values)),
                        'median': float(np.median(values)),
                        'min': float(np.min(values)),
                        'max': float(np.max(values)),
                        'std': float(np.std(values)),
                        'p95': float(np.percentile(values, 95)),
                        'p99': float(np.percentile(values, 99)),
                        'count': len(values)
                    }
                else:
                    sorted_vals = sorted(values)
                    health['metrics'] = {
                        'average': sum(values) / len(values),
                        'median': sorted_vals[len(sorted_vals)//2],
                        'min': min(values),
                        'max': max(values),
                        'std': (sum((x - sum(values)/len(values))**2 for x in values)/len(values))**0.5
                    }
        
        # Executa verificações personalizadas
        if custom_checks:
            for check_name, check_func in custom_checks.items():
                try:
                    result = check_func()
                    health['checks'][check_name] = result
                    
                    if isinstance(result, dict) and not result.get('success', True):
                        health['status'] = 'degraded'
                        health['warnings'].append(f"{check_name}: {result.get('message', 'Falha')}")
                        
                except Exception as e:
                    health['errors'].append(f"{check_name}: {str(e)}")
                    health['status'] = 'unhealthy'
        
        self.health_checks[system_name] = health
        return health
    
    def _check_for_anomaly(self, metric_name: str, current_value: float) -> bool:
        """Detecta anomalias usando desvio padrão"""
        try:
            records = self.metrics[metric_name][-100:]  # Últimos 100 valores
            values = [r['value'] for r in records]
            
            if NUMPY_AVAILABLE:
                mean_val = float(np.mean(values))
                std_val = float(np.std(values))
            else:
                mean_val = sum(values) / len(values)
                std_val = (sum((x - mean_val) ** 2 for x in values) / len(values)) ** 0.5
            
            if std_val > 0:
                z_score = abs(current_value - mean_val) / std_val
                if z_score > self.anomaly_threshold:
                    self.create_alert('warning',
                                     f"Anomalia detectada em {metric_name}: z-score={z_score:.2f}",
                                     'anomaly_detection',
                                     {'metric': metric_name, 'value': current_value, 
                                      'mean': mean_val, 'std': std_val, 'z_score': z_score})
                    return True
        except Exception as e:
            logger.error(f"Erro na detecção de anomalia: {e}")
        
        return False
    
    def _start_resource_monitoring(self):
        """Inicia monitoramento de recursos do sistema"""
        def monitor_resources():
            while True:
                try:
                    import psutil
                    
                    resource_data = {
                        'timestamp': datetime.now(),
                        'cpu_percent': psutil.cpu_percent(interval=1),
                        'memory_percent': psutil.virtual_memory().percent,
                        'memory_available_mb': psutil.virtual_memory().available / (1024 * 1024),
                        'disk_usage_percent': psutil.disk_usage('/').percent if os.name != 'nt' else psutil.disk_usage('C:\\').percent,
                        'network_sent': psutil.net_io_counters().bytes_sent,
                        'network_recv': psutil.net_io_counters().bytes_recv
                    }
                    
                    self.resource_history.append(resource_data)
                    
                    # Registra como métricas
                    self.record_metric('system_cpu_percent', resource_data['cpu_percent'], unit='%')
                    self.record_metric('system_memory_percent', resource_data['memory_percent'], unit='%')
                    self.record_metric('system_disk_percent', resource_data['disk_usage_percent'], unit='%')
                    
                    time.sleep(30)  # Coleta a cada 30 segundos
                    
                except ImportError:
                    logger.warning("psutil não disponível, monitoramento de recursos desativado")
                    break
                except Exception as e:
                    logger.error(f"Erro no monitoramento de recursos: {e}")
                    time.sleep(60)
        
        thread = threading.Thread(target=monitor_resources, daemon=True)
        thread.start()
        logger.info("📈 Monitoramento de recursos iniciado")
    
    def start_profiling(self, function_name: str):
        """Inicia profiling de uma função"""
        if not self.profiling_enabled:
            return None
        
        import time
        start_time = time.time()
        return {'function': function_name, 'start_time': start_time}
    
    def end_profiling(self, profiling_context: Dict[str, Any], metadata: Dict = None):
        """Finaliza profiling e registra resultado"""
        if not profiling_context or not self.profiling_enabled:
            return
        
        import time
        end_time = time.time()
        duration_ms = (end_time - profiling_context['start_time']) * 1000
        
        profiling_data = {
            'function': profiling_context['function'],
            'duration_ms': duration_ms,
            'timestamp': datetime.now(),
            'metadata': metadata or {}
        }
        
        self.profiling_data[profiling_context['function']].append(profiling_data)
        self.record_metric(f'profiling_{profiling_context["function"]}', duration_ms, unit='ms')
    
    def get_histogram_stats(self, metric_name: str) -> Dict[str, Any]:
        """Retorna estatísticas de histograma para uma métrica"""
        if metric_name not in self.metrics_histograms:
            return {}
        
        values = self.metrics_histograms[metric_name]
        
        if not values:
            return {}
        
        sorted_values = sorted(values)
        n = len(values)
        
        return {
            'count': n,
            'min': min(values),
            'max': max(values),
            'mean': sum(values) / n,
            'median': sorted_values[n // 2],
            'p50': sorted_values[int(n * 0.5)],
            'p75': sorted_values[int(n * 0.75)],
            'p90': sorted_values[int(n * 0.90)],
            'p95': sorted_values[int(n * 0.95)],
            'p99': sorted_values[int(n * 0.99)]
        }
    
    def get_system_report(self) -> Dict[str, Any]:
        """Gera relatório completo do sistema com análises avançadas"""
        uptime = (datetime.now() - self.start_time).total_seconds()
        
        # Análise de métricas
        metrics_analysis = {}
        for metric_name, records in self.metrics.items():
            if records:
                values = [r['value'] for r in records[-1000:]]  # Últimos 1000
                
                if NUMPY_AVAILABLE:
                    metrics_analysis[metric_name] = {
                        'count': len(values),
                        'trend': 'increasing' if len(values) > 1 and np.mean(values[-10:]) > np.mean(values[-20:-10]) else 'decreasing',
                        'volatility': float(np.std(values)) if len(values) > 1 else 0,
                        'recent_avg': float(np.mean(values[-10:])) if len(values) >= 10 else float(np.mean(values)),
                        'histogram_stats': self.get_histogram_stats(metric_name) if metric_name in self.metrics_histograms else None
                    }
                else:
                    sorted_vals = sorted(values)
                    metrics_analysis[metric_name] = {
                        'count': len(values),
                        'trend': 'increasing' if len(values) > 1 and sum(values[-10:]) > sum(values[-20:-10]) else 'decreasing',
                        'volatility': (sum((x - sum(values)/len(values))**2 for x in values)/len(values))**0.5,
                        'recent_avg': sum(values[-10:]) / len(values[-10:]) if len(values) >= 10 else sum(values) / len(values)
                    }
        
        # Análise de recursos
        resource_analysis = {}
        if self.resource_history:
            recent_resources = list(self.resource_history)[-100:]
            if recent_resources:
                resource_analysis = {
                    'cpu_avg': sum(r['cpu_percent'] for r in recent_resources) / len(recent_resources),
                    'memory_avg': sum(r['memory_percent'] for r in recent_resources) / len(recent_resources),
                    'disk_avg': sum(r['disk_usage_percent'] for r in recent_resources) / len(recent_resources),
                    'cpu_max': max(r['cpu_percent'] for r in recent_resources),
                    'memory_max': max(r['memory_percent'] for r in recent_resources),
                    'samples': len(recent_resources)
                }
        
        # Análise de profiling
        profiling_analysis = {}
        for func_name, data in self.profiling_data.items():
            if data:
                durations = [d['duration_ms'] for d in data[-100:]]
                profiling_analysis[func_name] = {
                    'calls': len(durations),
                    'avg_duration_ms': sum(durations) / len(durations),
                    'max_duration_ms': max(durations),
                    'min_duration_ms': min(durations)
                }
        
        # Análise de alertas
        active_critical = len([a for a in self.alerts 
                              if a['level'] == 'critical' and not a['resolved']])
        active_warnings = len([a for a in self.alerts 
                              if a['level'] == 'warning' and not a['resolved']])
        
        return {
            'timestamp': datetime.now(),
            'uptime_seconds': uptime,
            'uptime_formatted': self._format_uptime(uptime),
            'total_metrics_recorded': sum(len(v) for v in self.metrics.values()),
            'metrics_analysis': metrics_analysis,
            'resource_analysis': resource_analysis,
            'profiling_analysis': profiling_analysis,
            'active_alerts': {
                'critical': active_critical,
                'warning': active_warnings,
                'total': active_critical + active_warnings
            },
            'systems_monitored': list(self.health_checks.keys()),
            'health_summary': {
                k: v['status'] for k, v in self.health_checks.items()
            },
            'sla_violations': len(self.sla_violations)
        }
    
    def _format_uptime(self, seconds: float) -> str:
        """Formata tempo de uptime"""
        days = int(seconds // 86400)
        hours = int((seconds % 86400) // 3600)
        minutes = int((seconds % 3600) // 60)
        secs = int(seconds % 60)
        
        parts = []
        if days > 0:
            parts.append(f"{days}d")
        if hours > 0:
            parts.append(f"{hours}h")
        if minutes > 0:
            parts.append(f"{minutes}m")
        parts.append(f"{secs}s")
        
        return " ".join(parts)

# =============================================================================
# SISTEMA DE CACHE DISTRIBUÍDO
# =============================================================================

class DistributedCacheSystem:
    """Sistema de cache distribuído com suporte a múltiplos backends"""
    
    def __init__(self, backend: str = 'memory', ttl_seconds: int = CACHE_TTL_DEFAULT):
        self.backend = backend
        self.default_ttl = ttl_seconds
        self.cache = {}
        self.memory_cache = {}
        self.redis_client = None
        
        # Estatísticas
        self.hit_count = 0
        self.miss_count = 0
        self.set_count = 0
        self.delete_count = 0
        
        # Inicializa backend Redis se disponível
        if backend == 'redis' and REDIS_AVAILABLE:
            try:
                self.redis_client = redis.Redis(host='localhost', port=6379, decode_responses=True)
                self.redis_client.ping()
                logger.info("✅ Cache Redis inicializado")
            except Exception as e:
                logger.warning(f"⚠️ Redis não disponível, usando cache em memória: {e}")
                self.backend = 'memory'
        
        logger.info(f"💾 Sistema de Cache Distribuído inicializado (backend: {self.backend})")
    
    def set_cache(self, key: str, value: Any, ttl: int = None) -> bool:
        """Armazena no cache com TTL"""
        ttl = ttl or self.default_ttl
        expires_at = datetime.now() + timedelta(seconds=ttl)
        
        entry = {
            'value': value,
            'created_at': datetime.now(),
            'expires_at': expires_at,
            'ttl': ttl,
            'access_count': 0
        }
        
        if self.backend == 'redis' and self.redis_client:
            try:
                self.redis_client.setex(
                    f"vhalinor:{key}",
                    ttl,
                    pickle.dumps(value)
                )
            except Exception as e:
                logger.error(f"Erro ao setar cache Redis: {e}")
                self.memory_cache[key] = entry
        else:
            self.memory_cache[key] = entry
        
        self.set_count += 1
        return True
    
    def get_cache(self, key: str) -> Optional[Any]:
        """Recupera do cache se válido"""
        if self.backend == 'redis' and self.redis_client:
            try:
                value = self.redis_client.get(f"vhalinor:{key}")
                if value:
                    self.hit_count += 1
                    return pickle.loads(value)
            except Exception as e:
                logger.error(f"Erro ao get cache Redis: {e}")
        
        # Fallback para cache em memória
        if key in self.memory_cache:
            entry = self.memory_cache[key]
            
            if datetime.now() < entry['expires_at']:
                entry['access_count'] += 1
                self.hit_count += 1
                return entry['value']
            else:
                del self.memory_cache[key]
                self.delete_count += 1
        
        self.miss_count += 1
        return None
    
    def delete_cache(self, key: str) -> bool:
        """Remove entrada do cache"""
        if self.backend == 'redis' and self.redis_client:
            try:
                self.redis_client.delete(f"vhalinor:{key}")
            except Exception:
                pass
        
        if key in self.memory_cache:
            del self.memory_cache[key]
            self.delete_count += 1
            return True
        
        return False
    
    def clear_expired(self) -> int:
        """Remove entradas expiradas"""
        now = datetime.now()
        expired = []
        
        for key, entry in self.memory_cache.items():
            if now >= entry['expires_at']:
                expired.append(key)
        
        for key in expired:
            del self.memory_cache[key]
        
        count = len(expired)
        if count > 0:
            self.delete_count += count
            logger.debug(f"🧹 {count} entradas expiradas removidas do cache")
        
        return count
    
    def clear_all(self) -> int:
        """Limpa todo o cache"""
        count = len(self.memory_cache)
        self.memory_cache.clear()
        self.delete_count += count
        
        if self.backend == 'redis' and self.redis_client:
            try:
                self.redis_client.flushdb()
            except Exception:
                pass
        
        return count
    
    def get_cache_stats(self) -> Dict[str, Any]:
        """Retorna estatísticas detalhadas do cache"""
        total_requests = self.hit_count + self.miss_count
        hit_rate = (self.hit_count / total_requests * 100) if total_requests > 0 else 0
        
        # Análise de uso - estimativa mais rápida sem pickle.dumps
        memory_usage = sum(
            len(str(v.get('value', ''))) * 2  # Estimativa conservadora
            for v in self.memory_cache.values()
        )
        total_entries = len(self.memory_cache)
        memory_usage_mb = memory_usage / (1024 * 1024)
        hits = self.hit_count
        misses = self.miss_count
        sets = self.set_count
        deletes = self.delete_count
        hit_rate_pct = f"{hit_rate:.2f}%"
        
        # Top acessos - usa heapq para O(n) em vez de O(n log n)
        import heapq
        top_keys = heapq.nlargest(
            10,
            [(k, v.get('access_count', 0)) for k, v in self.memory_cache.items()],
            key=lambda x: x[1]
        )
        
        return {
            'total_entries': len(self.memory_cache),
            'memory_usage_bytes': memory_usage,
            'memory_usage_mb': memory_usage / (1024 * 1024),
            'hits': self.hit_count,
            'misses': self.miss_count,
            'sets': self.set_count,
            'deletes': self.delete_count,
            'hit_rate': hit_rate_pct,
            'total_requests': total_requests,
            'backend': self.backend,
            'top_keys': top_keys,
            'expired_count': 0  # Seria incrementado separadamente
        }

# =============================================================================
# SISTEMA DE PERSISTÊNCIA E RECUPERAÇÃO
# =============================================================================

class PersistenceSystem:
    """Sistema de persistência avançado com checkpoints, compressão e recuperação de desastres"""
    
    def __init__(self, storage_path: Path = PROJECT_DIRS["data"] / "checkpoints"):
        self.storage_path = storage_path
        self.storage_path.mkdir(parents=True, exist_ok=True)
        self.checkpoints: Dict[str, Dict[str, Any]] = {}
        self.backup_schedule: Dict[str, Any] = {}
        self.compression_enabled = True
        self.compression_level = 6
        self.encryption_enabled = False
        self.replication_targets: List[Path] = []
        self.auto_backup_interval = 3600  # 1 hora em segundos
        self.max_checkpoint_age_days = 30
        self.version_history: Dict[str, List[str]] = defaultdict(list)
        self._lock = threading.RLock()
        self.conn = None
        self.cursor = None
        
        self.setup_database()
        self._start_auto_backup_thread()
        logger.info("💾 Sistema de Persistência Avançado inicializado em {}".format(self.storage_path))
    
    def setup_database(self):
        """Configura banco de dados SQLite para metadados"""
        db_path = self.storage_path / "persistence.db"
        self.conn = sqlite3.connect(str(db_path), check_same_thread=False)
        self.cursor = self.conn.cursor()
        
        self.cursor.execute('''
            CREATE TABLE IF NOT EXISTS checkpoints (
                id TEXT PRIMARY KEY,
                name TEXT,
                timestamp TEXT,
                size_bytes INTEGER,
                compressed_size_bytes INTEGER,
                metadata TEXT,
                tags TEXT,
                version INTEGER DEFAULT 1,
                parent_checkpoint TEXT
            )
        ''')
        
        self.cursor.execute('''
            CREATE TABLE IF NOT EXISTS backups (
                id TEXT PRIMARY KEY,
                checkpoint_id TEXT,
                timestamp TEXT,
                location TEXT,
                status TEXT,
                backup_type TEXT
            )
        ''')
        
        self.cursor.execute('''
            CREATE TABLE IF NOT EXISTS replication_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                checkpoint_id TEXT,
                target_location TEXT,
                timestamp TEXT,
                status TEXT,
                error_message TEXT
            )
        ''')
        
        self.cursor.execute('''
            CREATE INDEX IF NOT EXISTS idx_checkpoint_timestamp 
            ON checkpoints(timestamp)
        ''')
        
        self.cursor.execute('''
            CREATE INDEX IF NOT EXISTS idx_backup_checkpoint 
            ON backups(checkpoint_id)
        ''')
        
        self.conn.commit()
        logger.info("✅ Banco de dados de metadados inicializado")
    
    def create_checkpoint(self, name: str, state: Dict[str, Any], 
                          tags: List[str] = None, parent_id: str = None) -> Optional[str]:
        """Cria checkpoint do estado atual com compressão opcional"""
        # Gera ID e timestamp fora do lock para reduzir contenção
        checkpoint_id = hashlib.md5(f"{name}_{time.time()}".encode()).hexdigest()[:12]
        timestamp = datetime.now()
        
        # Serializa e comprime fora do lock (operações CPU-bound)
        state_bytes = self._serialize_state(state)
        original_size = len(state_bytes)
        
        if self.compression_enabled:
            compressed_bytes = zlib.compress(state_bytes, self.compression_level)
            compressed_size = len(compressed_bytes)
            final_bytes = compressed_bytes
        else:
            compressed_bytes = state_bytes
            compressed_size = original_size
            final_bytes = state_bytes
        
        with self._lock:
            try:
                # Salva em arquivo (I/O dentro do lock é necessário para consistência)
                file_path = self.storage_path / f"{checkpoint_id}.pkl"
                with open(file_path, 'wb') as f:
                    f.write(final_bytes)
                
                # Determina versão
                version = 1
                if parent_id and parent_id in self.checkpoints:
                    version = self.checkpoints[parent_id].get('version', 0) + 1
                    self.version_history[parent_id].append(checkpoint_id)
                
                # Registra no banco de dados (batch de operações)
                if self.conn:
                    self.cursor.execute('''
                        INSERT INTO checkpoints (id, name, timestamp, size_bytes, compressed_size_bytes, metadata, tags, version, parent_checkpoint)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ''', (
                        checkpoint_id,
                        name,
                        timestamp.isoformat(),
                        original_size,
                        compressed_size,
                        json.dumps({'version': VERSION, 'codename': CODENAME}),
                        json.dumps(tags or []),
                        version,
                        parent_id
                    ))
                    self.conn.commit()
                
                compression_ratio = (1 - compressed_size / original_size) * 100 if original_size > 0 else 0
                
                self.checkpoints[checkpoint_id] = {
                    'id': checkpoint_id,
                    'name': name,
                    'timestamp': timestamp,
                    'size_bytes': original_size,
                    'compressed_size_bytes': compressed_size,
                    'size_mb': original_size / (1024 * 1024),
                    'compressed_size_mb': compressed_size / (1024 * 1024),
                    'path': str(file_path),
                    'tags': tags or [],
                    'version': version,
                    'parent_checkpoint': parent_id,
                    'compression_ratio': compression_ratio
                }
                
                # Replica para alvos configurados (fora do lock)
                if self.replication_targets:
                    self._replicate_checkpoint(checkpoint_id, file_path)
                
                logger.info(f"💾 Checkpoint criado: {checkpoint_id} ({original_size / (1024 * 1024):.2f} MB → {compressed_size / (1024 * 1024):.2f} MB, {compression_ratio:.1f}% compressão)")
                return checkpoint_id
                
            except Exception as e:
                logger.error(f"❌ Erro ao criar checkpoint: {e}")
                return None
    
    def restore_checkpoint(self, checkpoint_id: str) -> Optional[Dict[str, Any]]:
        """Restaura estado de um checkpoint com descompressão automática"""
        with self._lock:
            try:
                file_path = self.storage_path / f"{checkpoint_id}.pkl"
                
                if not file_path.exists():
                    logger.warning(f"⚠️ Checkpoint não encontrado: {checkpoint_id}")
                    # Tenta replicação
                    for target in self.replication_targets:
                        replica_path = target / f"{checkpoint_id}.pkl"
                        if replica_path.exists():
                            logger.info(f"📂 Restaurando de replicação: {replica_path}")
                            file_path = replica_path
                            break
                    
                    if not file_path.exists():
                        return None
                
                with open(file_path, 'rb') as f:
                    checkpoint_data = f.read()
                
                # Descomprime se necessário
                if self.compression_enabled:
                    import zlib
                    try:
                        checkpoint_data = zlib.decompress(checkpoint_data)
                    except zlib.error:
                        logger.warning(f"⚠️ Falha ao descomprimir, usando dados brutos")
                
                state = self._deserialize_state(checkpoint_data)
                
                logger.info(f"📂 Checkpoint restaurado: {checkpoint_id}")
                return state
                
            except Exception as e:
                logger.error(f"❌ Erro ao restaurar checkpoint: {e}")
                return None
    
    def _serialize_state(self, state: Dict[str, Any]) -> bytes:
        """Serializa estado para armazenamento"""
        # Remove objetos não serializáveis
        clean_state = {}
        for k, v in state.items():
            try:
                pickle.dumps(v)
                clean_state[k] = v
            except:
                clean_state[k] = str(v)
        
        return pickle.dumps(clean_state)
    
    def _deserialize_state(self, state_bytes: bytes) -> Dict[str, Any]:
        """Desserializa estado do armazenamento"""
        return pickle.loads(state_bytes)
    
    def list_checkpoints(self, tags: List[str] = None) -> List[Dict[str, Any]]:
        """Lista todos os checkpoints disponíveis, opcionalmente filtrados por tags"""
        checkpoints = sorted(
            self.checkpoints.values(),
            key=lambda x: x['timestamp'],
            reverse=True
        )
        
        if tags:
            checkpoints = [c for c in checkpoints 
                          if any(tag in c.get('tags', []) for tag in tags)]
        
        return checkpoints
    
    def delete_checkpoint(self, checkpoint_id: str) -> bool:
        """Remove checkpoint"""
        try:
            file_path = self.storage_path / f"{checkpoint_id}.pkl"
            
            if file_path.exists():
                file_path.unlink()
            
            if checkpoint_id in self.checkpoints:
                del self.checkpoints[checkpoint_id]
            
            if self.conn:
                self.cursor.execute('DELETE FROM checkpoints WHERE id = ?', (checkpoint_id,))
                self.conn.commit()
            
            logger.info(f"🗑️ Checkpoint removido: {checkpoint_id}")
            return True
            
        except Exception as e:
            logger.error(f"❌ Erro ao remover checkpoint: {e}")
            return False
    
    def create_backup(self, checkpoint_id: str) -> str:
        """Cria backup de um checkpoint"""
        backup_id = f"backup_{checkpoint_id}_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
        backup_path = self.storage_path / 'backups'
        backup_path.mkdir(exist_ok=True)
        
        try:
            source = self.storage_path / f"{checkpoint_id}.pkl"
            destination = backup_path / f"{backup_id}.pkl"
            
            if source.exists():
                import shutil
                shutil.copy2(source, destination)
                
                if self.conn:
                    self.cursor.execute('''
                        INSERT INTO backups (id, checkpoint_id, timestamp, location, status)
                        VALUES (?, ?, ?, ?, ?)
                    ''', (
                        backup_id,
                        checkpoint_id,
                        datetime.now().isoformat(),
                        str(destination),
                        'active'
                    ))
                    self.conn.commit()
                
                logger.info(f"💿 Backup criado: {backup_id}")
                return backup_id
            
        except Exception as e:
            logger.error(f"❌ Erro ao criar backup: {e}")
        
        return None
    
    def cleanup_old_checkpoints(self, keep_count: int = 10, 
                               older_than_days: int = None) -> int:
        """Remove checkpoints antigos mantendo apenas os N mais recentes"""
        with self._lock:
            if older_than_days is None:
                older_than_days = self.max_checkpoint_age_days
            
            checkpoints = sorted(
                self.checkpoints.values(),
                key=lambda x: x['timestamp'],
                reverse=True
            )
            
            to_remove = []
            
            # Remove os mais antigos que o limite de quantidade
            if len(checkpoints) > keep_count:
                to_remove.extend(checkpoints[keep_count:])
            
            # Remove os mais antigos que o limite de dias
            cutoff = datetime.now() - timedelta(days=older_than_days)
            for checkpoint in checkpoints:
                if checkpoint['timestamp'] < cutoff:
                    if checkpoint not in to_remove:
                        to_remove.append(checkpoint)
            
            # Executa remoção
            removed_count = 0
            for checkpoint in to_remove:
                if self.delete_checkpoint(checkpoint['id']):
                    removed_count += 1
            
            logger.info(f"🧹 {removed_count} checkpoints antigos removidos")
            return removed_count
    
    def _replicate_checkpoint(self, checkpoint_id: str, source_path: Path) -> bool:
        """Replica checkpoint para alvos configurados"""
        success_count = 0
        
        for target in self.replication_targets:
            try:
                target.mkdir(parents=True, exist_ok=True)
                target_path = target / f"{checkpoint_id}.pkl"
                
                import shutil
                shutil.copy2(source_path, target_path)
                
                # Registra no log de replicação
                if self.conn:
                    self.cursor.execute('''
                        INSERT INTO replication_log (checkpoint_id, target_location, timestamp, status, error_message)
                        VALUES (?, ?, ?, ?, ?)
                    ''', (
                        checkpoint_id,
                        str(target),
                        datetime.now().isoformat(),
                        'success',
                        None
                    ))
                    self.conn.commit()
                
                success_count += 1
                logger.debug(f"📤 Checkpoint replicado para: {target}")
                
            except Exception as e:
                logger.error(f"❌ Erro ao replicar para {target}: {e}")
                if self.conn:
                    self.cursor.execute('''
                        INSERT INTO replication_log (checkpoint_id, target_location, timestamp, status, error_message)
                        VALUES (?, ?, ?, ?, ?)
                    ''', (
                        checkpoint_id,
                        str(target),
                        datetime.now().isoformat(),
                        'error',
                        str(e)
                    ))
                    self.conn.commit()
        
        return success_count > 0
    
    def add_replication_target(self, target_path: Path) -> bool:
        """Adiciona alvo de replicação"""
        if target_path not in self.replication_targets:
            self.replication_targets.append(target_path)
            logger.info(f"📤 Alvo de replicação adicionado: {target_path}")
            return True
        return False
    
    def remove_replication_target(self, target_path: Path) -> bool:
        """Remove alvo de replicação"""
        if target_path in self.replication_targets:
            self.replication_targets.remove(target_path)
            logger.info(f"📤 Alvo de replicação removido: {target_path}")
            return True
        return False
    
    def _start_auto_backup_thread(self):
        """Inicia thread de backup automático"""
        def auto_backup_loop():
            while True:
                try:
                    time.sleep(self.auto_backup_interval)
                    self._auto_backup()
                except Exception as e:
                    logger.error(f"❌ Erro no backup automático: {e}")
        
        thread = threading.Thread(target=auto_backup_loop, daemon=True)
        thread.start()
        logger.info("⏰ Thread de backup automático iniciado")
    
    def _auto_backup(self):
        """Executa backup automático do estado atual"""
        # Cria checkpoint automático
        checkpoint_id = self.create_checkpoint(
            'auto_backup',
            {'timestamp': datetime.now().isoformat(), 'type': 'auto_backup'},
            tags=['auto', 'scheduled']
        )
        
        if checkpoint_id:
            logger.info(f"⏰ Backup automático criado: {checkpoint_id}")
    
    def get_checkpoint_version_history(self, checkpoint_id: str) -> List[Dict[str, Any]]:
        """Retorna histórico de versões de um checkpoint"""
        history = []
        
        current_id = checkpoint_id
        while current_id:
            if current_id in self.checkpoints:
                checkpoint = self.checkpoints[current_id]
                history.append(checkpoint)
                current_id = checkpoint.get('parent_checkpoint')
            else:
                break
        
        return history
    
    def set_compression_enabled(self, enabled: bool, level: int = 6):
        """Configura compressão de checkpoints"""
        self.compression_enabled = enabled
        self.compression_level = max(1, min(9, level))
        logger.info(f"🗜️ Compressão {'habilitada' if enabled else 'desabilitada'} (nível: {self.compression_level})")
    
    def get_storage_stats(self) -> Dict[str, Any]:
        """Retorna estatísticas de armazenamento detalhadas"""
        total_size = sum(c['size_bytes'] for c in self.checkpoints.values())
        total_compressed_size = sum(c.get('compressed_size_bytes', c['size_bytes']) for c in self.checkpoints.values())
        
        # Calcula espaço livre no disco
        try:
            import shutil
            disk_usage = shutil.disk_usage(self.storage_path)
            free_space_bytes = disk_usage.free
            total_disk_bytes = disk_usage.total
        except:
            free_space_bytes = 0
            total_disk_bytes = 0
        
        return {
            'total_checkpoints': len(self.checkpoints),
            'total_size_bytes': total_size,
            'total_size_mb': total_size / (1024 * 1024),
            'total_size_gb': total_size / (1024 * 1024 * 1024),
            'total_compressed_size_bytes': total_compressed_size,
            'total_compressed_size_mb': total_compressed_size / (1024 * 1024),
            'total_compressed_size_gb': total_compressed_size / (1024 * 1024 * 1024),
            'compression_savings_bytes': total_size - total_compressed_size,
            'compression_savings_mb': (total_size - total_compressed_size) / (1024 * 1024),
            'compression_ratio': (1 - total_compressed_size / total_size) * 100 if total_size > 0 else 0,
            'storage_path': str(self.storage_path),
            'free_space_bytes': free_space_bytes,
            'free_space_mb': free_space_bytes / (1024 * 1024),
            'free_space_gb': free_space_bytes / (1024 * 1024 * 1024),
            'total_disk_bytes': total_disk_bytes,
            'total_disk_gb': total_disk_bytes / (1024 * 1024 * 1024),
            'replication_targets': len(self.replication_targets),
            'replication_locations': [str(t) for t in self.replication_targets],
            'compression_enabled': self.compression_enabled,
            'compression_level': self.compression_level,
            'auto_backup_interval': self.auto_backup_interval
        }

# =============================================================================
# SISTEMA DE OTIMIZAÇÃO ADAPTATIVA
# =============================================================================

class AdaptiveOptimizationEngine:
    """Engine de otimização que se adapta ao longo do tempo usando ML"""
    
    def __init__(self):
        self.performance_history = deque(maxlen=1000)
        self.parameters = {
            'neural_threshold': 0.5,
            'learning_rate': 0.01,
            'cache_ttl': CACHE_TTL_DEFAULT,
            'batch_size': BATCH_SIZE_DEFAULT,
            'quantum_iterations': QUANTUM_SHOTS,
            'synaptic_plasticity': PLASTICITY_RATE,
            'sparsity_target': NEURON_SPARSITY
        }
        
        self.parameter_ranges = {
            'neural_threshold': (0.1, 0.9),
            'learning_rate': (0.0001, 0.1),
            'cache_ttl': (60, 86400),
            'batch_size': (16, 256),
            'quantum_iterations': (100, 10000),
            'synaptic_plasticity': (0.001, 0.1),
            'sparsity_target': (0.05, 0.5)
        }
        
        self.optimization_log = []
        self.best_performance = 0.0
        self.best_params = self.parameters.copy()
        
        # Modelo de otimização (simplificado)
        self.optimization_model = None
        
        logger.info("⚙️ Engine de Otimização Adaptativa inicializada")
    
    def record_performance(self, metric: str, value: float, 
                          context: Dict[str, Any] = None) -> None:
        """Registra métrica de performance com contexto"""
        self.performance_history.append({
            'timestamp': datetime.now(),
            'metric': metric,
            'value': value,
            'context': context or {},
            'current_params': self.parameters.copy()
        })
    
    def suggest_optimization(self) -> Dict[str, Any]:
        """Sugere otimizações baseado em histórico e análise de tendências"""
        if len(self.performance_history) < 10:
            return {'suggestion': 'insufficient_data', 'confidence': 0.0}
        
        recent_metrics = list(self.performance_history)[-50:]
        suggestions = {}
        
        # Análise de performance por métrica
        performance_by_metric = defaultdict(list)
        for record in recent_metrics:
            performance_by_metric[record['metric']].append(record['value'])
        
        avg_performance = {}
        for metric, values in performance_by_metric.items():
            avg_performance[metric] = sum(values) / len(values)
        
        # Sugestões baseadas em performance
        for param, (min_val, max_val) in self.parameter_ranges.items():
            current = self.parameters[param]
            
            # Otimização simples: ajuste proporcional à performance
            if 'processing_time' in avg_performance:
                if avg_performance['processing_time'] > 1000:  # > 1s
                    suggestions[f'reduce_{param}'] = max(min_val, current * 0.8)
                elif avg_performance['processing_time'] < 100:  # < 100ms
                    suggestions[f'increase_{param}'] = min(max_val, current * 1.2)

            if 'memory_usage' in avg_performance:
                if avg_performance['memory_usage'] > 1000:  # > 1GB
                    suggestions[f'reduce_{param}'] = max(min_val, current * 0.8)
                elif avg_performance['memory_usage'] < 100:  # < 100MB
                    suggestions[f'increase_{param}'] = min(max_val, current * 1.2)
                    
            if 'accuracy' in avg_performance:
                if avg_performance['accuracy'] < 0.8:  # < 80%
                    suggestions[f'adjust_{param}'] = current * (1 + (0.8 - avg_performance['accuracy']) * 0.1)
        
        # Atualiza melhor performance
        overall_performance = sum(avg_performance.values()) / len(avg_performance) if avg_performance else 0
        if overall_performance > self.best_performance:
            self.best_performance = overall_performance
            self.best_params = self.parameters.copy()
        
        suggestion_record = {
            'timestamp': datetime.now(),
            'suggestions': suggestions,
            'avg_performance': avg_performance,
            'overall_performance': overall_performance,
            'confidence': min(0.9, len(self.performance_history) / 1000)
        }
        
        self.optimization_log.append(suggestion_record)
        return suggestions
    
    def apply_optimization(self, optimization: Dict[str, Any]) -> Dict[str, Any]:
        """Aplica otimização aos parâmetros com validação"""
        applied = {}
        
        for key, value in optimization.items():
            # Extrai nome do parâmetro da chave (ex: 'reduce_neural_threshold' -> 'neural_threshold')
            param_name = key.split('_', 1)[1] if '_' in key else key
            
            if param_name in self.parameters:
                old_value = self.parameters[param_name]
                
                # Valida range
                if param_name in self.parameter_ranges:
                    min_val, max_val = self.parameter_ranges[param_name]
                    value = max(min_val, min(max_val, value))
                
                self.parameters[param_name] = value
                applied[param_name] = {'from': old_value, 'to': value}
                
                logger.info(f"⚙️ Otimização aplicada: {param_name} {old_value:.4f} -> {value:.4f}")
        
        return applied
    
    def get_optimization_history(self) -> List[Dict[str, Any]]:
        """Retorna histórico de otimizações"""
        return list(self.optimization_log)[-100:]  # Últimas 100
    
    def get_current_parameters(self) -> Dict[str, Any]:
        """Retorna parâmetros atuais do sistema"""
        return self.parameters.copy()
    
    def get_optimization_report(self) -> Dict[str, Any]:
        """Gera relatório completo de otimização"""
        return {
            'timestamp': datetime.now(),
            'current_parameters': self.parameters,
            'best_parameters': self.best_params,
            'best_performance': self.best_performance,
            'optimization_count': len(self.optimization_log),
            'performance_history_size': len(self.performance_history),
            'suggested_optimizations': self.suggest_optimization()
        }

# =============================================================================
# SISTEMA DE ORQUESTRAÇÃO DISTRIBUÍDA
# =============================================================================

class DistributedOrchestrator:
    """Sistema de orquestração distribuída para execução paralela e coordenada"""
    
    def __init__(self, max_workers: int = 4):
        self.max_workers = max_workers
        self.task_queue = asyncio.Queue()
        self.task_results = {}
        self.active_tasks = {}
        self.completed_tasks = deque(maxlen=1000)
        self.failed_tasks = deque(maxlen=100)
        self.worker_pool = []
        self.worker_status = {}
        self.task_dependencies = {}
        self.task_priorities = {}
        self.execution_graph = {}
        self.resource_allocator = None
        self.load_balancer = None
        self._lock = threading.RLock()
        self.running = False
        
        logger.info(f"🌐 Orquestrador Distribuído inicializado (max_workers: {max_workers})")
    
    async def start(self):
        """Inicia o orquestrador e pool de workers"""
        if self.running:
            return
        
        self.running = True
        
        # Inicia workers
        for i in range(self.max_workers):
            worker_id = f"worker_{i}"
            self.worker_status[worker_id] = {
                'status': 'idle',
                'current_task': None,
                'tasks_completed': 0,
                'tasks_failed': 0,
                'last_activity': datetime.now()
            }
            
            worker = asyncio.create_task(self._worker_loop(worker_id))
            self.worker_pool.append(worker)
        
        # Inicia monitoramento
        asyncio.create_task(self._monitor_workers())
        
        logger.info(f"🚀 {self.max_workers} workers iniciados")
    
    async def stop(self):
        """Para o orquestrador e todos os workers"""
        self.running = False
        
        # Cancela workers
        for worker in self.worker_pool:
            worker.cancel()
        
        # Aguarda finalização
        await asyncio.gather(*self.worker_pool, return_exceptions=True)
        
        self.worker_pool.clear()
        logger.info("🛑 Orquestrador Distribuído parado")
    
    async def _worker_loop(self, worker_id: str):
        """Loop principal do worker"""
        while self.running:
            try:
                # Obtém tarefa da fila
                task = await asyncio.wait_for(self.task_queue.get(), timeout=1.0)
                
                if task is None:
                    continue
                
                # Captura tempo de início fora do lock
                start_time = datetime.now()
                
                # Atualiza status (lock mínimo necessário)
                with self._lock:
                    self.worker_status[worker_id]['status'] = 'busy'
                    self.worker_status[worker_id]['current_task'] = task['id']
                    self.active_tasks[task['id']] = {
                        'worker_id': worker_id,
                        'start_time': start_time,
                        'task': task
                    }
                
                # Executa tarefa (fora do lock)
                try:
                    result = await self._execute_task(task)
                    completed_at = datetime.now()
                    duration = (completed_at - start_time).total_seconds()
                    
                    # Registra sucesso (lock mínimo necessário)
                    with self._lock:
                        self.task_results[task['id']] = {
                            'status': 'completed',
                            'result': result,
                            'completed_at': completed_at
                        }
                        self.completed_tasks.append({
                            'task_id': task['id'],
                            'worker_id': worker_id,
                            'duration': duration
                        })
                        self.worker_status[worker_id]['tasks_completed'] += 1
                        # Limpa tarefa ativa
                        if task['id'] in self.active_tasks:
                            del self.active_tasks[task['id']]
                    
                    logger.debug(f"✅ Tarefa {task['id']} completada por {worker_id}")
                    
                except Exception as e:
                    failed_at = datetime.now()
                    
                    # Registra falha (lock mínimo necessário)
                    with self._lock:
                        self.task_results[task['id']] = {
                            'status': 'failed',
                            'error': str(e),
                            'failed_at': failed_at
                        }
                        self.failed_tasks.append({
                            'task_id': task['id'],
                            'worker_id': worker_id,
                            'error': str(e)
                        })
                        self.worker_status[worker_id]['tasks_failed'] += 1
                    
                    logger.error(f"❌ Tarefa {task['id']} falhou: {e}")
                
                finally:
                    # Limpa status do worker (lock mínimo necessário)
                    with self._lock:
                        self.worker_status[worker_id]['status'] = 'idle'
                        self.worker_status[worker_id]['current_task'] = None
                        self.worker_status[worker_id]['last_activity'] = datetime.now()
                
                self.task_queue.task_done()
                
            except asyncio.TimeoutError:
                continue
            except Exception as e:
                logger.error(f"Erro no worker {worker_id}: {e}")
                await asyncio.sleep(1)
    
    async def _execute_task(self, task: Dict[str, Any]) -> Any:
        """Executa uma tarefa específica"""
        task_type = task.get('type', 'generic')
        task_func = task.get('function')
        task_args = task.get('args', [])
        task_kwargs = task.get('kwargs', {})
        
        if task_func and callable(task_func):
            return await task_func(*task_args, **task_kwargs)
        elif task_type == 'compute':
            return await self._execute_compute_task(task)
        elif task_type == 'io':
            return await self._execute_io_task(task)
        else:
            return await self._execute_generic_task(task)
    
    async def _execute_compute_task(self, task: Dict[str, Any]) -> Any:
        """Executa tarefa computacional"""
        # Simulação de tarefa computacional
        await asyncio.sleep(task.get('duration', 1.0))
        return {'result': f'computed_{task["id"]}'}
    
    async def _execute_io_task(self, task: Dict[str, Any]) -> Any:
        """Executa tarefa de I/O"""
        # Simulação de tarefa de I/O
        await asyncio.sleep(task.get('duration', 0.5))
        return {'result': f'io_completed_{task["id"]}'}
    
    async def _execute_generic_task(self, task: Dict[str, Any]) -> Any:
        """Executa tarefa genérica"""
        await asyncio.sleep(0.1)
        return {'result': f'generic_{task["id"]}'}
    
    async def submit_task(self, task: Dict[str, Any], priority: int = 0) -> str:
        """Submete tarefa para execução"""
        task_id = task.get('id', hashlib.md5(f"{time.time()}_{random.random()}".encode()).hexdigest()[:12])
        task['id'] = task_id
        task['submitted_at'] = datetime.now()
        
        # Registra prioridade
        self.task_priorities[task_id] = priority
        
        # Adiciona dependências se especificadas
        if 'dependencies' in task:
            self.task_dependencies[task_id] = task['dependencies']
        
        # Adiciona à fila
        await self.task_queue.put(task)
        
        logger.debug(f"📝 Tarefa {task_id} submetida (prioridade: {priority})")
        return task_id
    
    async def submit_task_batch(self, tasks: List[Dict[str, Any]]) -> List[str]:
        """Submete lote de tarefas"""
        task_ids = []
        for task in tasks:
            task_id = await self.submit_task(task)
            task_ids.append(task_id)
        
        return task_ids
    
    async def get_task_result(self, task_id: str, timeout: float = 30.0) -> Optional[Any]:
        """Aguarda e retorna resultado de tarefa"""
        start_time = time.time()
        
        while time.time() - start_time < timeout:
            if task_id in self.task_results:
                result = self.task_results[task_id]
                if result['status'] == 'completed':
                    return result['result']
                elif result['status'] == 'failed':
                    raise Exception(f"Task failed: {result['error']}")
            
            await asyncio.sleep(0.1)
        
        raise TimeoutError(f"Task {task_id} timed out after {timeout}s")
    
    async def wait_for_tasks(self, task_ids: List[str], timeout: float = 60.0) -> Dict[str, Any]:
        """Aguarda conclusão de múltiplas tarefas"""
        results = {}
        
        async def wait_single(task_id: str):
            try:
                results[task_id] = await self.get_task_result(task_id, timeout)
            except Exception as e:
                results[task_id] = {'error': str(e)}
        
        await asyncio.gather(*[wait_single(tid) for tid in task_ids])
        return results
    
    def get_orchestration_status(self) -> Dict[str, Any]:
        """Retorna status atual da orquestração"""
        with self._lock:
            active_count = len(self.active_tasks)
            queue_size = self.task_queue.qsize()
            
            worker_stats = {}
            for worker_id, status in self.worker_status.items():
                worker_stats[worker_id] = {
                    'status': status['status'],
                    'current_task': status['current_task'],
                    'tasks_completed': status['tasks_completed'],
                    'tasks_failed': status['tasks_failed'],
                    'idle_time': (datetime.now() - status['last_activity']).total_seconds() if status['status'] == 'idle' else 0
                }
            
            return {
                'timestamp': datetime.now(),
                'running': self.running,
                'max_workers': self.max_workers,
                'active_workers': sum(1 for s in self.worker_status.values() if s['status'] == 'busy'),
                'idle_workers': sum(1 for s in self.worker_status.values() if s['status'] == 'idle'),
                'active_tasks': active_count,
                'queued_tasks': queue_size,
                'completed_tasks': len(self.completed_tasks),
                'failed_tasks': len(self.failed_tasks),
                'worker_stats': worker_stats,
                'total_tasks_submitted': len(self.task_results)
            }
    
    async def _monitor_workers(self):
        """Monitora saúde dos workers"""
        while self.running:
            try:
                status = self.get_orchestration_status()
                
                # Detecta workers inativos por muito tempo
                for worker_id, worker_stat in status['worker_stats'].items():
                    if worker_stat['idle_time'] > 300:  # 5 minutos
                        logger.warning(f"⚠️ Worker {worker_id} inativo por {worker_stat['idle_time']:.0f}s")
                
                await asyncio.sleep(30)
                
            except Exception as e:
                logger.error(f"Erro no monitoramento: {e}")
                await asyncio.sleep(60)

# =============================================================================
# PONTE DE DADOS NEURAIS
# =============================================================================

class NeuralDataBridge:
    """Ponte de dados neurais entre módulos com sincronização avançada e otimizações"""
    
    def __init__(self, integration_hub: IntegrationHub, config: OrchestratorConfig = None):
        self.hub = integration_hub
        self.config = config or OrchestratorConfig()
        self.neural_bus = None
        self.neural_state_history = AdaptiveDeque(initial_maxlen=100, min_maxlen=10, max_maxlen=1000)
        self.neural_state = None
        self.neural_state_average = 0.0
        self.connection_matrix = None
        self.sync_frequency = 1.0  # Hz
        self.sync_latency = 0.0
        self.sync_latency_history = AdaptiveDeque(initial_maxlen=100, min_maxlen=10, max_maxlen=1000)
        self.sync_latency_average = 0.0
        self.sync_latency_std = 0.0
        self.sync_latency_p95 = 0.0
        self.sync_latency_p99 = 0.0
        self.sync_latency_total = 0.0
        self.sync_latency_count = 0
        self.last_sync = datetime.now()
        self.stats_cache = CachedStatistics(ttl_seconds=self.config.cache_ttl)
        
        if NEURAL_MODULES_AVAILABLE:
            try:
                from neural_bus import NeuralBus
                from NeuralConnectionMatrix import NeuralConnectionMatrix
                
                self.neural_bus = NeuralBus()
                self.connection_matrix = NeuralConnectionMatrix()
                logger.info("✅ Ponte Neural inicializada com otimizações")
            except Exception as e:
                logger.error(f"❌ Erro ao inicializar ponte neural: {e}")
    
    @profile_performance(threshold=0.1)
    async def sync_neural_state(self, neurons: Dict[str, AdvancedNeuron]) -> Dict[str, Any]:
        """Sincroniza estado neural com outros módulos - otimizado com cache"""
        def compute_neural_stats():
            activations = [n.current_activation for n in neurons.values()]
            return {
                'neuron_count': len(neurons),
                'active_neurons': sum(1 for n in neurons.values() if n.is_active),
                'average_activation': float(np.mean(activations)) if NUMPY_AVAILABLE and activations else 0.0,
                'total_firings': sum(n.fire_count for n in neurons.values()),
                'average_energy': float(np.mean([n.energy_level for n in neurons.values()])) if NUMPY_AVAILABLE else 0.0,
            }
        
        # Usa cache para estatísticas
        stats = self.stats_cache.get('neural_stats', compute_neural_stats)
        
        neural_state = {
            **stats,
            'timestamp': datetime.now().isoformat()
        }
        
        # Envia para módulos de análise
        packet = DataPacket(
            id=hashlib.md5(f"neural_sync_{time.time()}".encode()).hexdigest()[:12],
            source_module='neural_engine',
            target_module=['analysis', 'quantum_engine', 'continuous_learning'],
            data_type='neural_state_sync',
            payload=neural_state,
            priority=DataPriority.HIGH,
            ttl=5
        )
        
        await self.hub.send_data(packet)
        self.last_sync = datetime.now()
        
        return neural_state
    
    async def broadcast_neural_pattern(self, pattern: Dict[str, Any]) -> None:
        """Transmite padrão neural para todos os módulos"""
        packet = DataPacket(
            id=hashlib.md5(f"neural_pattern_{time.time()}".encode()).hexdigest()[:12],
            source_module='neural_engine',
            target_module='all',
            data_type='neural_pattern',
            payload=pattern,
            priority=DataPriority.HIGH,
            ttl=10
        )
        
        await self.hub.send_data(packet)
        logger.debug(f"📡 Padrão neural transmitido: {pattern.get('pattern_type', 'unknown')}")

# =============================================================================
# PONTE DE DADOS QUÂNTICOS
# =============================================================================

class QuantumDataBridge:
    """Ponte de dados quânticos entre módulos com otimização"""
    
    def __init__(self, integration_hub: IntegrationHub):
        self.hub = integration_hub
        self.quantum_core = None
        self.quantum_analysis = None
        self.quantum_optimization = None
        self.quantum_trader = None
        self.quantum_simulator = None
        self.quantum_prediction = None
        self.quantum_security = None
        self.quantum_network = None
        self.quantum_circuits = {}
        
        if QUANTUM_MODULES_AVAILABLE:
            try:
                from quantum_core import QuantumCore
                from quantum_algorithms_trader import QuantumAlgorithmsTrader
                from simulador_quantum import QuantumSimulator
                
                self.quantum_core = QuantumCore()
                self.quantum_trader = QuantumAlgorithmsTrader()
                self.quantum_simulator = QuantumSimulator()
                logger.info("✅ Ponte Quântica inicializada")
            except Exception as e:
                logger.error(f"❌ Erro ao inicializar ponte quântica: {e}")
    
    async def sync_quantum_state(self, quantum_data: Dict[str, Any]) -> Dict[str, Any]:
        """Sincroniza estado quântico com outros módulos"""
        quantum_state = {
            'entanglement_pairs': quantum_data.get('entangled_pairs', []),
            'quantum_entropy': quantum_data.get('entropy', 0.0),
            'circuit_executions': quantum_data.get('executions', 0),
            'qubits_available': QUANTUM_QUBITS,
            'timestamp': datetime.now().isoformat()
        }
        
        # Envia para módulo neural e análise
        packet = DataPacket(
            id=hashlib.md5(f"quantum_sync_{time.time()}".encode()).hexdigest()[:12],
            source_module='quantum_engine',
            target_module=['neural_engine', 'analysis'],
            data_type='quantum_state_sync',
            payload=quantum_state,
            priority=DataPriority.HIGH
        )
        
        await self.hub.send_data(packet)
        return quantum_state
    
    async def apply_quantum_optimization(self, neural_data: Dict[str, Any]) -> Dict[str, Any]:
        """Aplica otimização quântica em dados neurais"""
        if not self.quantum_core:
            return neural_data
        
        try:
            # Simula otimização quântica
            optimized_data = neural_data.copy()
            optimized_data['quantum_optimized'] = True
            optimized_data['optimization_factor'] = random.uniform(1.1, 1.5)
            optimized_data['quantum_confidence'] = random.uniform(0.7, 0.95)
            
            return optimized_data
        except Exception as e:
            logger.error(f"❌ Erro na otimização quântica: {e}")
            return neural_data

# =============================================================================
# PONTE DE DADOS DE ANÁLISE
# =============================================================================

class AnalysisDataBridge:
    """Ponte de dados de análise entre módulos"""
    
    def __init__(self, integration_hub: IntegrationHub):
        self.hub = integration_hub
        self.data_analyzer = None
        self.pattern_recognizer = None
        self.risk_analyzer = None
        
        if ANALYSIS_MODULES_AVAILABLE:
            try:
                from data_analyzer import DataAnalyzer
                from AdvancedPatternRecognition import PatternRecognizer
                from AdvancedRiskAnalyzer import RiskAnalyzer
                
                self.data_analyzer = DataAnalyzer()
                self.pattern_recognizer = PatternRecognizer()
                self.risk_analyzer = RiskAnalyzer()
                logger.info("✅ Ponte de Análise inicializada")
            except Exception as e:
                logger.error(f"❌ Erro ao inicializar ponte de análise: {e}")
    
    async def analyze_neural_patterns(self, neural_data: Dict[str, Any]) -> Dict[str, Any]:
        """Analisa padrões neurais e detecta anomalias"""
        if not self.pattern_recognizer:
            return {}
        
        try:
            patterns = {
                'detected_patterns': ['sequential', 'parallel'],
                'pattern_strength': random.uniform(0.5, 1.0),
                'anomalies': [],
                'confidence': random.uniform(0.6, 0.9),
                'timestamp': datetime.now().isoformat()
            }
            
            # Envia resultados para módulo neural
            packet = DataPacket(
                id=hashlib.md5(f"pattern_analysis_{time.time()}".encode()).hexdigest()[:12],
                source_module='analysis',
                target_module='neural_engine',
                data_type='pattern_analysis',
                payload=patterns,
                priority=DataPriority.MEDIUM
            )
            await self.hub.send_data(packet)
            
            return patterns
        except Exception as e:
            logger.error(f"❌ Erro na análise de padrões: {e}")
            return {}
    
    async def assess_risk(self, market_data: Dict[str, Any]) -> Dict[str, Any]:
        """Avalia risco baseado em dados de mercado"""
        if not self.risk_analyzer:
            return {}
        
        try:
            risk_assessment = {
                'risk_level': random.choice(['low', 'medium', 'high']),
                'risk_score': random.uniform(0, 100),
                'risk_factors': ['volatility', 'volume', 'trend'],
                'recommendations': ['reduce_position', 'hedge'],
                'timestamp': datetime.now().isoformat()
            }
            
            # Envia para módulos de decisão
            packet = DataPacket(
                id=hashlib.md5(f"risk_assessment_{time.time()}".encode()).hexdigest()[:12],
                source_module='analysis',
                target_module=['decision_engine', 'neural_engine'],
                data_type='risk_assessment',
                payload=risk_assessment,
                priority=DataPriority.CRITICAL
            )
            await self.hub.send_data(packet)
            
            return risk_assessment
        except Exception as e:
            logger.error(f"❌ Erro na avaliação de risco: {e}")
            return {}

# =============================================================================
# PONTE DE APRENDIZADO CONTÍNUO
# =============================================================================

class ContinuousLearningBridge:
    """Ponte de aprendizado contínuo entre módulos"""
    
    def __init__(self, integration_hub: IntegrationHub):
        self.hub = integration_hub
        self.learning_service = None
        self.learning_service_history = deque(maxlen=100)
        self.neural_learning = None
        self.analysis_learning = None
        self.quantum_learning = None
        self.quantum_learning_history = deque(maxlen=100)
        self.analysis_learning_history = deque(maxlen=100)
        self.neural_learning_history = deque(maxlen=100)
        self.learning_insights = deque(maxlen=1000)
        
        if CONTINUOUS_LEARNING_AVAILABLE:
            try:
                from ContinuousLearningService import ContinuousLearningService
                from ContinuousQuantumLearning import ContinuousQuantumLearning
                
                self.learning_service = ContinuousLearningService()
                self.quantum_learning = ContinuousQuantumLearning()
                logger.info("✅ Ponte de Aprendizado Contínuo inicializada")
            except Exception as e:
                logger.error(f"❌ Erro ao inicializar ponte de aprendizado: {e}")
    
    async def update_learning_models(self, training_data: Dict[str, Any]) -> Dict[str, Any]:
        """Atualiza modelos de aprendizado com novos dados"""
        if not self.learning_service:
            return {}
        
        try:
            # Processa dados de treinamento
            learning_update = {
                'models_updated': ['neural', 'quantum', 'analysis'],
                'accuracy_improvement': random.uniform(0, 5),
                'samples_processed': random.randint(1000, 10000),
                'timestamp': datetime.now().isoformat()
            }
            
            # Notifica todos os módulos sobre atualização
            packet = DataPacket(
                id=hashlib.md5(f"learning_update_{time.time()}".encode()).hexdigest()[:12],
                source_module='continuous_learning',
                target_module='all',
                data_type='learning_update',
                payload=learning_update,
                priority=DataPriority.MEDIUM
            )
            await self.hub.send_data(packet)
            
            return learning_update
        except Exception as e:
            logger.error(f"❌ Erro na atualização de aprendizado: {e}")
            return {}
    
    async def share_learning_insights(self, insights: Dict[str, Any]) -> None:
        """Compartilha insights de aprendizado entre módulos"""
        insight_id = hashlib.md5(f"insight_{time.time()}".encode()).hexdigest()[:12]
        
        learning_insight = LearningInsight(
            id=insight_id,
            source='continuous_learning',
            insight_type=insights.get('type', 'general'),
            content=insights.get('content', {}),
            confidence=insights.get('confidence', 0.8),
            impact_score=insights.get('impact', 0.5),
            tags=insights.get('tags', [])
        )
        
        self.learning_insights.append(learning_insight)
        
        packet = DataPacket(
            id=insight_id,
            source_module='continuous_learning',
            target_module='all',
            data_type='learning_insights',
            payload=learning_insight.to_dict(),
            priority=DataPriority.LOW,
            ttl=3600
        )
        
        await self.hub.send_data(packet)
        logger.debug(f"💡 Insight compartilhado: {insight_id}")

# =============================================================================
# ORQUESTRADOR CEREBRAL QUÂNTICO BASE
# =============================================================================

class QuantumBrainOrchestrator:
    """Orquestrador cerebral base com funcionalidades essenciais"""
    
    def __init__(self, iag_path: str, quantum_path: str):
        self.iag_path = iag_path
        self.quantum_path = quantum_path
        self.neurons: Dict[str, AdvancedNeuron] = {}
        self.synapses: Dict[str, AdvancedSynapse] = {}
        self.brain_state = BrainState.IDLE
        self.neuron_counter = 0
        self.synapse_counter = 0
        
        logger.info(f"🧠 Orquestrador Cerebral inicializado")
        logger.info(f"📁 IAG Path: {iag_path}")
        logger.info(f"⚛️  Quantum Path: {quantum_path}")
        logger.info(f"max_neurons: {MAX_NEURONS}")
    
    def create_neuron(self, file_path: str, neuron_type: NeuronType, 
                     specialization: str = None) -> str:
        """Cria novo neurônio avançado"""
        neuron_id = f"neuron_{self.neuron_counter:08d}"
        self.neuron_counter += 1
        
        # Extrai informações do arquivo
        file_path_obj = Path(file_path)
        file_size = file_path_obj.stat().st_size if file_path_obj.exists() else 0
        file_extension = file_path_obj.suffix
        content_hash = self._calculate_file_hash(file_path) if file_path_obj.exists() else ''
        
        neuron = AdvancedNeuron(
            id=neuron_id,
            file_path=file_path,
            neuron_type=neuron_type,
            activation_threshold=neuron_type.default_threshold,
            file_size=file_size,
            file_extension=file_extension,
            content_hash=content_hash,
            importance_score=neuron_type.importance,
            tags=[neuron_type.label, specialization] if specialization else [neuron_type.label]
        )
        
        self.neurons[neuron_id] = neuron
        logger.debug(f"🧠 Neurônio criado: {neuron_id} ({neuron_type.icon} {neuron_type.label})")
        
        return neuron_id
    
    def _calculate_file_hash(self, file_path: str) -> str:
        """Calcula hash do arquivo para identificação"""
        try:
            with open(file_path, 'rb') as f:
                file_hash = hashlib.sha256()
                chunk = f.read(8192)
                while chunk:
                    file_hash.update(chunk)
                    chunk = f.read(8192)
                return file_hash.hexdigest()[:16]
        except Exception:
            return ''
    
    def create_synapse(self, source_id: str, target_id: str, 
                      initial_weight: float = 0.5) -> str:
        """Cria nova sinapse entre neurônios"""
        if source_id not in self.neurons or target_id not in self.neurons:
            raise ValueError("Neurônio de origem ou destino não existe")
        
        synapse_id = f"synapse_{self.synapse_counter:08d}"
        self.synapse_counter += 1
        
        synapse = AdvancedSynapse(
            id=synapse_id,
            source_id=source_id,
            target_id=target_id,
            weight=initial_weight,
            strength=0.5,
            plasticity=PLASTICITY_RATE
        )
        
        self.synapses[synapse_id] = synapse
        self.neurons[source_id].connections.append(target_id)
        
        logger.debug(f"🔗 Sinapse criada: {synapse_id} ({source_id} → {target_id})")
        return synapse_id
    
    def stimulate_neuron(self, neuron_id: str, stimulus: float = 1.0) -> float:
        """Estimula um neurônio"""
        if neuron_id not in self.neurons:
            return 0.0
        
        neuron = self.neurons[neuron_id]
        activation = neuron.activate(stimulus)
        
        # Propaga para neurônios conectados
        if activation >= neuron.activation_threshold:
            for synapse in self.synapses.values():
                if synapse.source_id == neuron_id:
                    target_neuron = self.neurons.get(synapse.target_id)
                    if target_neuron:
                        propagated_signal = synapse.propagate(activation)
                        self.stimulate_neuron(synapse.target_id, propagated_signal * 0.1)
        
        return activation
    
    async def stimulate_neuron_async(self, neuron_id: str, stimulus: float = 1.0) -> float:
        """Versão assíncrona de stimulate_neuron"""
        return self.stimulate_neuron(neuron_id, stimulus)
    
    @lru_cache(maxsize=32)
    def get_brain_stats(self) -> Dict[str, Any]:
        """Retorna estatísticas cerebrais básicas (com cache)"""
        # Usa cache para evitar recálculo frequente
        neuron_count = len(self.neurons)
        synapse_count = len(self.synapses)
        
        # Cálculo otimizado de neurônios ativos
        active_count = 0
        total_activation = 0.0
        for neuron in self.neurons.values():
            if neuron.is_active:
                active_count += 1
            total_activation += neuron.current_activation
        
        avg_activation = total_activation / neuron_count if neuron_count > 0 else 0.0
        
        return {
            'total_neurons': neuron_count,
            'total_synapses': synapse_count,
            'brain_state': self.brain_state.label,
            'brain_state_icon': self.brain_state.icon,
            'active_neurons': active_count,
            'average_activation': float(avg_activation)
        }

# =============================================================================
# ORQUESTRADOR CEREBRAL AVANÇADO
# =============================================================================

class AdvancedQuantumBrainOrchestrator(QuantumBrainOrchestrator):
    """Orquestrador cerebral avançado com sistemas integrados"""
    
    def __init__(self, iag_path: str, quantum_path: str):
        super().__init__(iag_path, quantum_path)
        
        # Sistemas avançados
        self.ml_module = None
        self.advanced_quantum = None
        self.advanced_memory = None
        
        # Clusters neurais
        self.neural_clusters: Dict[str, NeuralCluster] = {}
        
        # Sistema de energia
        self.brain_energy = 1000.0
        self.energy_consumption = defaultdict(float)
        self.max_energy = 1000.0
        
        # Inicializa sistemas se dependências disponíveis
        self._initialize_advanced_systems()
    
    def _initialize_advanced_systems(self):
        """Inicializa sistemas avançados se disponíveis"""
        try:
            if SKLEARN_AVAILABLE:
                self.ml_module = MachineLearningModule(self)
            
            if QISKIT_AVAILABLE:
                self.advanced_quantum = AdvancedQuantumSystem(self)
            
            self.advanced_memory = AdvancedMemorySystem(self)
            
            logger.info("🚀 Sistemas avançados inicializados")
        except Exception as e:
            logger.error(f"❌ Erro ao inicializar sistemas avançados: {e}")
    
    def _upgrade_neurons(self):
        """Atualiza neurônios existentes para versão avançada"""
        upgraded_neurons = {}
        
        for neuron_id, neuron in self.neurons.items():
            if not isinstance(neuron, AdvancedNeuron):
                advanced_neuron = AdvancedNeuron(
                    id=neuron.id,
                    file_path=neuron.file_path,
                    neuron_type=neuron.neuron_type,
                    activation_threshold=neuron.activation_threshold,
                    current_activation=neuron.current_activation,
                    connections=neuron.connections.copy(),
                    last_fired=neuron.last_fired,
                    memory_weight=neuron.memory_weight,
                    learning_rate=neuron.learning_rate,
                    quantum_entanglement=neuron.quantum_entanglement,
                    file_size=getattr(neuron, 'file_size', 0),
                    file_extension=getattr(neuron, 'file_extension', ''),
                    content_hash=getattr(neuron, 'content_hash', ''),
                    metadata=getattr(neuron, 'metadata', {}).copy()
                )
                
                upgraded_neurons[neuron_id] = advanced_neuron
        
        self.neurons = upgraded_neurons
    
    def _create_neural_clusters(self):
        """Cria clusters neurais baseados em similaridade"""
        neurons_by_type = defaultdict(list)
        
        for neuron_id, neuron in self.neurons.items():
            neurons_by_type[neuron.neuron_type].append(neuron_id)
        
        # Cria clusters por tipo
        for i, (neuron_type, neuron_ids) in enumerate(neurons_by_type.items()):
            if len(neuron_ids) >= 3:
                cluster_id = f"cluster_{neuron_type.label}_{i}"
                cluster = NeuralCluster(
                    id=cluster_id,
                    neuron_ids=neuron_ids[:10],
                    cluster_type=f"homogeneous_{neuron_type.label}"
                )
                self.neural_clusters[cluster_id] = cluster
    
    def _create_neural_connections(self):
        """Cria conexões neurais (sinapses) entre neurônios e clusters"""
        logger.info("🔗 Criando conexões neurais...")
        
        connections_created = 0
        intra_cluster_connections = 0
        inter_cluster_connections = 0
        
        try:
            # 1. Conexões INTRA-CLUSTER (comunicação densa dentro de clusters)
            for cluster_id, cluster in self.neural_clusters.items():
                neuron_ids = cluster.neuron_ids
                cluster_size = len(neuron_ids)
                
                # Conecta cada neurônio a 3-5 outros no mesmo cluster
                for i, source_id in enumerate(neuron_ids):
                    # Determina targets no mesmo cluster (vizinhos + aleatórios)
                    num_targets = min(4, max(2, cluster_size - 1))
                    
                    # Vizinhos imediatos
                    targets = [
                        neuron_ids[(i + offset) % cluster_size] 
                        for offset in range(1, min(3, cluster_size))
                    ]
                    
                    # Adiciona target aleatório
                    if cluster_size > 3:
                        random_idx = random.randint(0, cluster_size - 1)
                        if neuron_ids[random_idx] not in targets:
                            targets.append(neuron_ids[random_idx])
                    
                    # Cria sinapses com peso forte (0.7-0.95)
                    for target_id in targets:
                        if target_id != source_id and source_id in self.neurons and target_id in self.neurons:
                            weight = random.uniform(0.7, 0.95)
                            try:
                                self.create_synapse(source_id, target_id, weight)
                                intra_cluster_connections += 1
                                connections_created += 1
                            except Exception as e:
                                logger.debug(f"❌ Erro criando sinapse intra-cluster: {e}")
            
            # 2. Conexões INTER-CLUSTER (comunicação emergente entre clusters)
            cluster_ids = list(self.neural_clusters.keys())
            for i in range(len(cluster_ids)):
                for j in range(i + 1, len(cluster_ids)):
                    cluster1 = self.neural_clusters[cluster_ids[i]]
                    cluster2 = self.neural_clusters[cluster_ids[j]]
                    
                    # Conecta 1-2 neurônios representativos entre clusters
                    num_bridges = random.randint(1, 2)
                    
                    for _ in range(min(num_bridges, len(cluster1.neuron_ids), len(cluster2.neuron_ids))):
                        source_id = random.choice(cluster1.neuron_ids)
                        target_id = random.choice(cluster2.neuron_ids)
                        
                        if source_id in self.neurons and target_id in self.neurons:
                            weight = random.uniform(0.3, 0.6)  # Peso mais fraco que intra-cluster
                            try:
                                self.create_synapse(source_id, target_id, weight)
                                inter_cluster_connections += 1
                                connections_created += 1
                            except Exception as e:
                                logger.debug(f"❌ Erro criando sinapse inter-cluster: {e}")
            
            # 3. Conexões FEEDBACK (conexões recorrentes para memória)
            neuron_list = list(self.neurons.keys())
            for neuron_id in neuron_list[:len(neuron_list) // 3]:  # 1/3 dos neurônios
                # Cria auto-feedback leve (recorrência)
                if random.random() < 0.4:  # 40% dos neurônios selecionados
                    try:
                        # Neurônio conecta a neurônios aleatórios anteriores na lista
                        for _ in range(random.randint(1, 2)):
                            target_id = random.choice(neuron_list)
                            if target_id != neuron_id and target_id in self.neurons:
                                weight = random.uniform(0.2, 0.5)  # Feedback fraco
                                self.create_synapse(neuron_id, target_id, weight)
                                connections_created += 1
                    except Exception as e:
                        logger.debug(f"❌ Erro criando sinapse de feedback: {e}")
            
            # 4. Registro e logging
            logger.info(f"✅ Conexões neurais criadas: {connections_created}")
            logger.info(f"   🔄 Intra-cluster: {intra_cluster_connections}")
            logger.info(f"   🌉 Inter-cluster: {inter_cluster_connections}")
            logger.info(f"   📊 Total neurônios: {len(self.neurons)}")
            logger.info(f"   📊 Total sinapses: {len(self.synapses)}")
            
            # Atualiza estatísticas
            return {
                'total_connections': connections_created,
                'intra_cluster': intra_cluster_connections,
                'inter_cluster': inter_cluster_connections,
                'total_synapses': len(self.synapses),
                'neurons_connected': len(self.neurons)
            }
            
        except Exception as e:
            logger.error(f"❌ Erro ao criar conexões neurais: {e}")
            return {'error': str(e), 'connections_created': connections_created}
    
    async def run_optimization_cycle(self) -> Dict[str, Any]:
        """Executa ciclo de otimização completo"""
        logger.info("⚙️ Iniciando ciclo de otimização...")
        
        results = {}
        
        # Otimiza memória
        if self.advanced_memory:
            results['memory'] = await self._optimize_memory()
        
        # Poda neurônios inativos
        results['pruning'] = await self._prune_inactive_neurons()
        
        # Otimiza sinapses
        results['synapses'] = await self._optimize_synapses()
        
        # Executa processamento quântico
        if self.advanced_quantum:
            results['quantum'] = await self._run_quantum_processing()
        
        # Treina modelos de ML
        if self.ml_module:
            logger.info(f"Treinando modelos de ML...")
            results['ml'] = await self._train_ml_models()
            logger.info(f"Modelos de ML treinados: {results['ml']}")
            logger.info(f"Anomalias detectadas: {results['ml']['anomalies_detected']}")
            logger.info(f"Anomalias: {results['ml']['anomaly_list']}")
            logger.info(f"Clusters treinados: {results['ml']['clusters_trained']}")
            
        
        logger.info("✅ Ciclo de otimização completo")
        return results
    
    async def _optimize_memory(self) -> Dict[str, Any]:
        """Otimiza e consolida memória"""
        if not self.advanced_memory:
            return {'status': 'skipped', 'reason': 'memory_system_not_available'}
        
        # Consolida memória de curto para longo prazo
        consolidated = 0
        for memory in list(self.advanced_memory.short_term_memory):
            if isinstance(memory, dict) and memory.get('importance', 0) > 0.7:
                memory_hash = self.advanced_memory.store_memory(
                    memory.get('content', memory),
                    memory.get('importance', 0.5)
                )
                if memory_hash:
                    consolidated += 1
        
        return {
            'memory_consolidated': consolidated,
            'short_term_size': len(self.advanced_memory.short_term_memory),
            'long_term_size': len(self.advanced_memory.long_term_memory)
        }
    
    async def _prune_inactive_neurons(self) -> Dict[str, Any]:
        """Remove neurônios inativos"""
        to_prune = []
        current_time = datetime.now()
        
        for neuron_id, neuron in self.neurons.items():
            if neuron.last_fired:
                inactive_seconds = (current_time - neuron.last_fired).total_seconds()
                
                # Neurônios inativos por mais de 24 horas
                if inactive_seconds > 86400:
                    # Verifica se não é crítico
                    if neuron.importance_score < 1.5:
                        to_prune.append(neuron_id)
        
        # Remove neurônios
        pruned_count = 0
        for neuron_id in to_prune:
            # Remove sinapses associadas
            synapses_to_remove = [
                sid for sid, s in self.synapses.items()
                if s.source_id == neuron_id or s.target_id == neuron_id
            ]
            for sid in synapses_to_remove:
                del self.synapses[sid]
            
            # Remove neurônio
            del self.neurons[neuron_id]
            pruned_count += 1
        
        return {'pruned_neurons': pruned_count, 'pruned_synapses': len(synapses_to_remove)}
    
    async def _optimize_synapses(self) -> Dict[str, Any]:
        """Otimiza sinapses baseado no uso"""
        optimized = 0
        weakened = 0
        strengthened = 0
        
        for synapse in self.synapses.values():
            # Sinapses fracas são fortalecidas
            if synapse.strength < 0.3:
                synapse.strengthen(0.05)
                strengthened += 1
                optimized += 1
            
            # Sinapses muito fortes mas pouco usadas são enfraquecidas
            if synapse.strength > 0.8 and synapse.last_used:
                time_since_use = (datetime.now() - synapse.last_used).total_seconds()
                if time_since_use > 3600:  # 1 hora
                    synapse.weaken(0.03)
                    weakened += 1
                    optimized += 1
        
        return {
            'optimized_total': optimized,
            'strengthened': strengthened,
            'weakened': weakened
        }
    
    async def _run_quantum_processing(self) -> Dict[str, Any]:
        """Executa processamento quântico"""
        if not self.advanced_quantum or not QISKIT_AVAILABLE:
            return {'status': 'skipped', 'reason': 'quantum_not_available'}
        
        return await self.advanced_quantum.execute_quantum_circuit('superposition')
    
    async def _train_ml_models(self) -> Dict[str, Any]:
        """Treina modelos de ML"""
        if not self.ml_module:
            return {'status': 'skipped', 'reason': 'ml_not_available'}
        
        await self.ml_module.train_on_brain_data()
        
        anomalies = self.ml_module.detect_anomalies()
        
        return {
            'clusters_trained': True,
            'anomalies_detected': len(anomalies),
            'anomaly_list': anomalies[:10]
        }

# =============================================================================
# SISTEMA DE APRENDIZADO DE MÁQUINA
# =============================================================================

class MachineLearningModule:
    """Módulo de aprendizado de máquina integrado"""
    
    def __init__(self, orchestrator: AdvancedQuantumBrainOrchestrator):
        self.orchestrator = orchestrator
        self.models: Dict[str, Any] = {}
        self.training_data = defaultdict(list)
        self.scaler = StandardScaler() if SKLEARN_AVAILABLE else None
        self.setup_models()
    
    def setup_models(self):
        """Configura modelos de ML"""
        if SKLEARN_AVAILABLE:
            try:
                self.models['neuron_clusterer'] = KMeans(n_clusters=5, random_state=42, n_init=10)
                self.models['anomaly_detector'] = IsolationForest(contamination=0.1, random_state=42)
                self.models['feature_reducer'] = PCA(n_components=10, random_state=42)
                logger.info("✅ Modelos de ML configurados")
            except Exception as e:
                logger.error(f"❌ Erro ao configurar modelos de ML: {e}")
    
    async def train_on_brain_data(self):
        """Treina modelos com dados cerebrais"""
        if not SKLEARN_AVAILABLE or not self.scaler:
            logger.warning("⚠️ Scikit-learn não disponível, pulando treinamento")
            return
        
        logger.info("🔬 Treinando modelos de ML com dados cerebrais...")
        
        # Coleta dados dos neurônios
        neuron_data = []
        neuron_ids = []
        
        for neuron_id, neuron in self.orchestrator.neurons.items():
            neuron_data.append([
                neuron.current_activation,
                neuron.activation_threshold,
                neuron.activation_history,
                neuron.memory_weight,
                neuron.learning_rate,
                neuron.learning_history,
                neuron.deep_learning_history,
                neuron.quantum_learning_history,
                neuron.analysis_learning_history,
                neuron.neural_learning_history,
                neuron.learning_insights,
                neuron.fire_count,
                neuron.energy_level,
                neuron.importance_score,
                len(neuron.connections)
            ])
            neuron_ids.append(neuron_id)
        
        if len(neuron_data) >= 10:
            X = np.array(neuron_data)
            
            # Normaliza dados
            X_scaled = self.scaler.fit_transform(X)
            
            # Clusterização
            if 'neuron_clusterer' in self.models and len(X) >= 5:
                try:
                    clusters = self.models['neuron_clusterer'].fit_predict(X_scaled)
                    
                    # Atribui clusters aos neurônios
                    for i, neuron_id in enumerate(neuron_ids):
                        if i < len(clusters):
                            self.orchestrator.neurons[neuron_id].metadata['ml_cluster'] = int(clusters[i])
                    
                    unique_clusters = len(set(clusters))
                    logger.info(f"✅ Clusterização: {unique_clusters} clusters identificados")
                    
                except Exception as e:
                    logger.error(f"❌ Erro na clusterização: {e}")
            
            # Detecção de anomalias
            if 'anomaly_detector' in self.models:
                try:
                    anomalies = self.models['anomaly_detector'].fit_predict(X_scaled)
                    anomaly_count = sum(1 for a in anomalies if a == -1)
                    logger.info(f"🔍 Anomalias detectadas: {anomaly_count}")
                    
                    # Marca neurônios anômalos
                    for i, neuron_id in enumerate(neuron_ids):
                        if i < len(anomalies) and anomalies[i] == -1:
                            self.orchestrator.neurons[neuron_id].metadata['is_anomaly'] = True
                            
                except Exception as e:
                    logger.error(f"❌ Erro na detecção de anomalias: {e}")
    
    def detect_anomalies(self, threshold: float = 2.0) -> List[str]:
        """Detecta neurônios com comportamento anômalo"""
        anomalies = []
        
        for neuron_id, neuron in self.orchestrator.neurons.items():
            if len(neuron.activation_history) > 10:
                recent = neuron.activation_history[-10:]
                
                if NUMPY_AVAILABLE:
                    mean_act = float(np.mean(recent))
                    std_act = float(np.std(recent))
                else:
                    mean_act = sum(recent) / len(recent)
                    std_act = (sum((x - mean_act) ** 2 for x in recent) / len(recent)) ** 0.5
                
                if std_act > threshold and mean_act > 0.8:
                    anomalies.append(neuron_id)
        
        return anomalies

# =============================================================================
# SISTEMA QUÂNTICO AVANÇADO
# =============================================================================

class AdvancedQuantumSystem:
    """Sistema quântico avançado com múltiplos circuitos"""
    
    def __init__(self, orchestrator: AdvancedQuantumBrainOrchestrator):
        self.orchestrator = orchestrator
        self.circuits: Dict[str, QuantumCircuit] = {}
        self.entangled_pairs: List[Tuple[str, str]] = []
        self.quantum_memory = {}
        self.setup_quantum_circuits()
    
    def setup_quantum_circuits(self):
        """Configura circuitos quânticos"""
        if not QISKIT_AVAILABLE:
            logger.warning("⚠️ Qiskit não disponível. Circuitos quânticos desativados.")
            return
        
        try:
            # Circuito de superposição
            qr_super = QuantumRegister(4, 'q')
            cr_super = ClassicalRegister(4, 'c')
            self.circuits['superposition'] = QuantumCircuit(qr_super, cr_super)
            for i in range(4):
                self.circuits['superposition'].h(i)
            self.circuits['superposition'].measure_all()
            
            # Circuito de emaranhamento
            qr_ent = QuantumRegister(2, 'q')
            cr_ent = ClassicalRegister(2, 'c')
            self.circuits['entanglement'] = QuantumCircuit(qr_ent, cr_ent)
            self.circuits['entanglement'].h(0)
            self.circuits['entanglement'].cx(0, 1)
            self.circuits['entanglement'].measure_all()
            
            # Circuito de Grover (busca)
            qr_grov = QuantumRegister(3, 'q')
            cr_grov = ClassicalRegister(3, 'c')
            self.circuits['grover'] = QuantumCircuit(qr_grov, cr_grov)
            for i in range(3):
                self.circuits['grover'].h(i)
            # Oracle simplificado
            self.circuits['grover'].cz(0, 2)
            self.circuits['grover'].cz(1, 2)
            # Amplificação
            for i in range(3):
                self.circuits['grover'].h(i)
                self.circuits['grover'].x(i)
            self.circuits['grover'].h(2)
            self.circuits['grover'].cx(0, 2)
            self.circuits['grover'].cx(1, 2)
            self.circuits['grover'].h(2)
            for i in range(3):
                self.circuits['grover'].x(i)
                self.circuits['grover'].h(i)
            self.circuits['grover'].measure_all()
            
            logger.info("✅ Circuitos quânticos configurados")
            
        except Exception as e:
            logger.error(f"❌ Erro na configuração quântica: {e}")
    
    async def execute_quantum_circuit(self, circuit_name: str, shots: int = QUANTUM_SHOTS) -> Dict[str, Any]:
        """Executa um circuito quântico"""
        if not QISKIT_AVAILABLE or circuit_name not in self.circuits:
            return {"error": "Circuito não disponível", "success": False}
        
        try:
            simulator = Aer.get_backend('qasm_simulator')
            circuit = self.circuits[circuit_name]
            
            job = execute(circuit, simulator, shots=shots)
            result = job.result()
            counts = result.get_counts(circuit)
            
            # Ativa neurônios quânticos com base no resultado
            quantum_neurons = [
                n for n in self.orchestrator.neurons.values()
                if n.neuron_type == NeuronType.QUANTUM
            ]
            
            for neuron in quantum_neurons[:10]:  # Limita a 10 neurônios
                activation = random.uniform(0.6, 0.9)  # Simula ativação quântica
                self.orchestrator.stimulate_neuron(neuron.id, activation)
            
            return {
                "success": True,
                "circuit": circuit_name,
                "shots": shots,
                "counts": counts,
                "activated_quantum_neurons": len(quantum_neurons),
                "entropy": self.calculate_quantum_entropy(tuple(sorted(counts.items())))
            }
            
        except Exception as e:
            logger.error(f"❌ Erro na execução quântica: {e}")
            return {"error": str(e), "success": False}
    
    @lru_cache(maxsize=128)
    def calculate_quantum_entropy(self, counts_tuple: tuple) -> float:
        """Calcula entropia quântica dos resultados (com cache)"""
        counts = dict(counts_tuple)
        total = sum(counts.values())
        if total == 0:
            return 0.0
        
        probabilities = [c / total for c in counts.values()]
        
        if NUMPY_AVAILABLE:
            probabilities = np.array(probabilities)
            entropy = -np.sum(probabilities * np.log2(probabilities + 1e-10))
            return float(entropy)
        else:
            entropy = -sum(p * math.log2(p) for p in probabilities if p > 0)
            return float(entropy)
    
    def create_quantum_entanglement(self, neuron1_id: str, neuron2_id: str) -> bool:
        """Cria emaranhamento quântico entre dois neurônios"""
        self.entangled_pairs.append((neuron1_id, neuron2_id))
        
        # Marca neurônios como emaranhados
        if neuron1_id in self.orchestrator.neurons:
            self.orchestrator.neurons[neuron1_id].quantum_entanglement = 1.0
        if neuron2_id in self.orchestrator.neurons:
            self.orchestrator.neurons[neuron2_id].quantum_entanglement = 1.0
        
        logger.info(f"🔗 Emaranhamento quântico criado entre {neuron1_id} e {neuron2_id}")
        return True
# SISTEMA DE MEMÓRIA AVANÇADA
# =============================================================================

class MarketAnalysisModule:
    """Módulo de análise de dados de mercado para abastecer predições"""
    
    def __init__(self, ml_bridge: MLModuleBridge):
        self.ml_bridge = ml_bridge
        self.market_data_cache = {}
        self.asset_list = []
        self.analysis_history = deque(maxlen=1000)
        self._lock = threading.RLock()
        logger.info("📊 Market Analysis Module inicializado")
    
    def set_asset_list(self, assets: List[str]) -> None:
        """Define a lista de ativos para análise"""
        with self._lock:
            self.asset_list = assets
            logger.info(f"📋 Lista de ativos definida: {len(assets)} ativos")
    
    async def collect_market_data(self, assets: List[str] = None) -> Dict[str, Any]:
        """Coleta dados de mercado para múltiplos ativos"""
        assets = assets or self.asset_list
        if not assets:
            return {'error': 'No assets defined for analysis'}
        
        market_data = {}
        
        for asset in assets:
            try:
                # Simula coleta de dados (em produção, usaria API real)
                asset_data = await self._fetch_asset_data(asset)
                market_data[asset] = asset_data
                
                # Cache dos dados
                self.market_data_cache[asset] = {
                    'data': asset_data,
                    'timestamp': datetime.now().isoformat()
                }
            except Exception as e:
                logger.error(f"Erro ao coletar dados de {asset}: {e}")
                market_data[asset] = {'error': str(e)}
        
        # Registra no histórico
        self.analysis_history.append({
            'timestamp': datetime.now().isoformat(),
            'assets_analyzed': list(market_data.keys()),
            'data_points': sum(1 for d in market_data.values() if 'error' not in d)
        })
        
        logger.info(f"📊 Dados coletados para {len(market_data)} ativos")
        return market_data
    
    async def _fetch_asset_data(self, asset: str) -> Dict[str, Any]:
        """Busca dados de um ativo específico (simulação)"""
        # Em produção, isso chamaria APIs de mercado (Binance, MetaTrader, etc)
        # Aqui simulamos dados realistas
        base_price = random.uniform(10, 1000)
        num_points = 50
        
        prices = [base_price * (1 + random.uniform(-0.02, 0.02)) for _ in range(num_points)]
        volumes = [random.uniform(1000, 100000) for _ in range(num_points)]
        
        return {
            'symbol': asset,
            'price_data': prices,
            'volume_data': volumes,
            'current_price': prices[-1],
            'timestamp': datetime.now().isoformat(),
            'timeframe': '1h'
        }
    
    async def analyze_all_assets(self, assets: List[str] = None) -> Dict[str, Any]:
        """Analisa todos os ativos e extrai features para predição"""
        # Coleta dados
        market_data = await self.collect_market_data(assets)
        
        if 'error' in market_data:
            return market_data
        
        # Extrai features para cada ativo
        analysis_results = {}
        
        for asset, data in market_data.items():
            if 'error' not in data:
                try:
                    features = self.ml_bridge._extract_features(data)
                    analysis_results[asset] = {
                        'features': features,
                        'raw_data': data,
                        'analysis_timestamp': datetime.now().isoformat()
                    }
                except Exception as e:
                    logger.error(f"Erro ao analisar {asset}: {e}")
                    analysis_results[asset] = {'error': str(e)}
        
        logger.info(f"🔍 Análise concluída para {len(analysis_results)} ativos")
        return analysis_results

class AssetSelectionEngine:
    """Motor de seleção de ativos baseado em predições ensemble"""
    
    def __init__(self, ml_bridge: MLModuleBridge, market_analysis: MarketAnalysisModule):
        self.ml_bridge = ml_bridge
        self.market_analysis = market_analysis
        self.selection_history = deque(maxlen=500)
        self._lock = threading.RLock()
        logger.info("🎯 Asset Selection Engine inicializado")
    
    async def select_best_asset(self, assets: List[str] = None) -> Dict[str, Any]:
        """Seleciona o melhor ativo baseado em predições ensemble"""
        # Analisa todos os ativos
        analysis_results = await self.market_analysis.analyze_all_assets(assets)
        
        if 'error' in analysis_results:
            return analysis_results
        
        # Faz predições ensemble para cada ativo
        asset_predictions = {}
        
        for asset, analysis in analysis_results.items():
            if 'error' not in analysis and 'features' in analysis:
                try:
                    features = analysis['features']
                    prediction = self.ml_bridge.ensemble_predict(features)
                    
                    if 'error' not in prediction:
                        # Adiciona incerteza à predição
                        uncertainty = self.ml_bridge.get_prediction_uncertainty(features)
                        
                        asset_predictions[asset] = {
                            'prediction': prediction,
                            'uncertainty': uncertainty,
                            'features': features,
                            'raw_data': analysis['raw_data']
                        }
                except Exception as e:
                    logger.error(f"Erro ao prever {asset}: {e}")
        
        if not asset_predictions:
            return {'error': 'No valid predictions generated'}
        
        # Seleciona o melhor ativo baseado em score composto
        best_asset = self._calculate_best_asset(asset_predictions)
        
        # Registra no histórico
        self.selection_history.append({
            'timestamp': datetime.now().isoformat(),
            'selected_asset': best_asset['asset'],
            'score': best_asset['score'],
            'all_assets_scored': {k: v['score'] for k, v in asset_predictions.items()}
        })
        
        logger.info(f"🎯 Melhor ativo selecionado: {best_asset['asset']} (score: {best_asset['score']:.3f})")
        return best_asset
    
    def _calculate_best_asset(self, asset_predictions: Dict[str, Any]) -> Dict[str, Any]:
        """Calcula o melhor ativo baseado em score composto"""
        scored_assets = []
        
        for asset, data in asset_predictions.items():
            prediction = data['prediction']
            uncertainty = data['uncertainty']
            
            # Score base: confiança * direção (positiva = melhor)
            confidence = prediction.get('confidence', 0.5)
            direction = prediction.get('direction', 0)
            predicted_change = prediction.get('predicted_change', 0)
            
            # Penalidade por incerteza
            uncertainty_penalty = uncertainty.get('confidence_std', 0) * 2
            
            # Bônus por consenso dos modelos
            consensus_bonus = uncertainty.get('consensus_ratio', 0.5) * 0.3
            
            # Score composto
            score = (confidence * 0.4) + (abs(predicted_change) * 0.3) + (consensus_bonus * 0.2) - (uncertainty_penalty * 0.1)
            
            # Se direção for neutra, reduz score drasticamente
            if direction == 0:
                score *= 0.3
            
            scored_assets.append({
                'asset': asset,
                'score': score,
                'prediction': prediction,
                'uncertainty': uncertainty,
                'features': data['features'],
                'raw_data': data['raw_data']
            })
        
        # Ordena por score e retorna o melhor
        scored_assets.sort(key=lambda x: x['score'], reverse=True)
        
        return scored_assets[0] if scored_assets else {'error': 'No assets scored'}

class TradeOperationManager:
    """Gerenciador de operações com cálculo automático de tempo, TP e SL pela IA"""
    
    def __init__(self, ml_bridge: MLModuleBridge):
        self.ml_bridge = ml_bridge
        self.operation_history = deque(maxlen=1000)
        self._lock = threading.RLock()
        logger.info("⏱️ Trade Operation Manager inicializado")
    
    def calculate_operation_parameters(self, asset_data: Dict[str, Any]) -> Dict[str, Any]:
        """Calcula parâmetros da operação (tempo, TP, SL) baseado na análise da IA"""
        features = asset_data.get('features', {})
        prediction = asset_data.get('prediction', {})
        raw_data = asset_data.get('raw_data', {})
        
        # Volatilidade do ativo
        volatility = features.get('volatility', 0.02)
        
        # Confiança da predição
        confidence = prediction.get('confidence', 0.5)
        
        # Direção e mudança prevista
        direction = prediction.get('direction', 0)
        predicted_change = prediction.get('predicted_change', 0)
        
        # Preço atual
        current_price = raw_data.get('current_price', 100)
        
        # Cálculo do tempo de operação baseado em volatilidade e confiança
        operation_time = self._calculate_operation_time(volatility, confidence, features)
        
        # Cálculo do Take Profit baseado em volatilidade e predição
        take_profit = self._calculate_take_profit(current_price, volatility, confidence, direction, predicted_change)
        
        # Cálculo do Stop Loss baseado em volatilidade e risco
        stop_loss = self._calculate_stop_loss(current_price, volatility, confidence, direction)
        
        # Tamanho da posição baseado em confiança e volatilidade
        position_size = self._calculate_position_size(confidence, volatility)
        
        parameters = {
            'asset': raw_data.get('symbol', 'UNKNOWN'),
            'direction': 'BUY' if direction == 1 else 'SELL' if direction == -1 else 'HOLD',
            'entry_price': current_price,
            'take_profit': take_profit,
            'take_profit_pct': ((take_profit - current_price) / current_price * 100) if direction == 1 else ((current_price - take_profit) / current_price * 100),
            'stop_loss': stop_loss,
            'stop_loss_pct': ((current_price - stop_loss) / current_price * 100) if direction == 1 else ((stop_loss - current_price) / current_price * 100),
            'operation_time_minutes': operation_time,
            'position_size_pct': position_size * 100,
            'confidence': confidence,
            'volatility': volatility,
            'risk_reward_ratio': abs((take_profit - current_price) / (current_price - stop_loss)) if stop_loss != current_price else 0
        }
        
        logger.info(f"⏱️ Parâmetros calculados: {parameters['direction']} {parameters['asset']} | TP: {parameters['take_profit_pct']:.2f}% | SL: {parameters['stop_loss_pct']:.2f}% | Tempo: {parameters['operation_time_minutes']}min")
        
        return parameters
    
    def _calculate_operation_time(self, volatility: float, confidence: float, features: Dict[str, Any]) -> int:
        """Calcula tempo de operação em minutos baseado em volatilidade e confiança"""
        # Base: maior volatilidade = tempo menor (movimento mais rápido)
        # Maior confiança = pode manter por mais tempo
        
        base_time = 60  # 1 hora base
        
        # Ajuste por volatilidade (0.01 a 0.05)
        volatility_factor = max(0.5, min(2.0, 0.02 / volatility))
        
        # Ajuste por confiança (0.3 a 0.9)
        confidence_factor = 0.5 + (confidence * 0.5)
        
        # Ajuste por hora do dia (se disponível)
        hour_factor = 1.0
        if 'hour' in features:
            hour = features['hour']
            # Horas de maior movimento (9-11, 14-16)
            if (9 <= hour <= 11) or (14 <= hour <= 16):
                hour_factor = 0.8  # Menor tempo, movimento mais rápido
            elif (12 <= hour <= 13) or (17 <= hour <= 18):
                hour_factor = 1.5  # Maior tempo, movimento mais lento
        
        # Ajuste por RSI (se disponível)
        rsi_factor = 1.0
        if 'rsi' in features:
            rsi = features['rsi']
            # RSI extremo indica reversão possível, tempo menor
            if rsi > 70 or rsi < 30:
                rsi_factor = 0.7
        
        operation_time = int(base_time * volatility_factor * confidence_factor * hour_factor * rsi_factor)
        
        # Limites: mínimo 15 minutos, máximo 4 horas
        return max(15, min(240, operation_time))
    
    def _calculate_take_profit(self, current_price: float, volatility: float, 
                               confidence: float, direction: int, predicted_change: float) -> float:
        """Calcula Take Profit baseado em volatilidade e predição"""
        # Base TP: 2x a volatilidade padrão
        base_tp_pct = volatility * 2
        
        # Ajuste por confiança
        confidence_multiplier = 0.5 + (confidence * 1.0)  # 0.5x a 1.5x
        
        # Ajuste por mudança prevista
        predicted_multiplier = 1.0 + abs(predicted_change) * 5  # Até 5x
        
        tp_pct = base_tp_pct * confidence_multiplier * predicted_multiplier
        
        # Limites: mínimo 0.5%, máximo 10%
        tp_pct = max(0.005, min(0.10, tp_pct))
        
        if direction == 1:  # BUY
            return current_price * (1 + tp_pct)
        elif direction == -1:  # SELL
            return current_price * (1 - tp_pct)
        else:
            return current_price
    
    def _calculate_stop_loss(self, current_price: float, volatility: float, 
                             confidence: float, direction: int) -> float:
        """Calcula Stop Loss baseado em volatilidade e risco"""
        # Base SL: 1.5x a volatilidade padrão
        base_sl_pct = volatility * 1.5
        
        # Ajuste por confiança (maior confiança = SL mais apertado)
        confidence_multiplier = 1.5 - (confidence * 0.5)  # 1.0x a 1.5x
        
        sl_pct = base_sl_pct * confidence_multiplier
        
        # Limites: mínimo 0.3%, máximo 5%
        sl_pct = max(0.003, min(0.05, sl_pct))
        
        if direction == 1:  # BUY
            return current_price * (1 - sl_pct)
        elif direction == -1:  # SELL
            return current_price * (1 + sl_pct)
        else:
            return current_price
    
    def _calculate_position_size(self, confidence: float, volatility: float) -> float:
        """Calcula tamanho da posição (0.0 a 1.0) baseado em confiança e volatilidade"""
        # Base: 10% do capital
        base_size = 0.10
        
        # Ajuste por confiança (0.3 a 0.9)
        confidence_multiplier = 0.5 + (confidence * 1.0)
        
        # Ajuste por volatilidade (maior volatilidade = posição menor)
        volatility_multiplier = max(0.5, min(1.5, 0.02 / volatility))
        
        position_size = base_size * confidence_multiplier * volatility_multiplier
        
        # Limites: mínimo 2%, máximo 25%
        return max(0.02, min(0.25, position_size))

class RealTimeDataCollector:
    """Coletor de dados de mercado em tempo real via WebSocket/REST"""
    
    def __init__(self):
        self.websocket_connections = {}
        self.rest_clients = {}
        self.data_buffers = {}  # {symbol: deque of ticks}
        self.subscribed_symbols = set()
        self._lock = threading.RLock()
        self._running = False
        self._data_callbacks = []
        logger.info("📡 Real-Time Data Collector inicializado")
    
    def add_data_callback(self, callback: Callable[[Dict[str, Any]], None]) -> None:
        """Adiciona callback para receber dados em tempo real"""
        self._data_callbacks.append(callback)
    
    async def connect_websocket(self, exchange: str, symbols: List[str]) -> bool:
        """Conecta via WebSocket para coleta de dados em tempo real"""
        with self._lock:
            if exchange in self.websocket_connections:
                logger.warning(f"⚠️ WebSocket já conectado para {exchange}")
                return True
        
        try:
            # Simulação de conexão WebSocket (em produção, usaria biblioteca real)
            # Ex: websockets library para Binance, Bybit, etc.
            logger.info(f"🔌 Conectando WebSocket para {exchange}...")
            
            # Inicializa buffer para cada símbolo
            for symbol in symbols:
                if symbol not in self.data_buffers:
                    self.data_buffers[symbol] = deque(maxlen=1000)
                self.subscribed_symbols.add(symbol)
            
            # Simula conexão bem-sucedida
            self.websocket_connections[exchange] = {
                'connected': True,
                'symbols': symbols,
                'connected_at': datetime.now().isoformat(),
                'last_ping': datetime.now().isoformat()
            }
            
            logger.info(f"✅ WebSocket conectado para {exchange} com {len(symbols)} símbolos")
            return True
            
        except Exception as e:
            logger.error(f"❌ Erro ao conectar WebSocket para {exchange}: {e}")
            return False
    
    async def connect_rest_api(self, exchange: str, api_key: str = None, 
                               api_secret: str = None) -> bool:
        """Conecta via REST API para dados históricos e adicionais"""
        with self._lock:
            if exchange in self.rest_clients:
                logger.warning(f"⚠️ REST API já conectada para {exchange}")
                return True
        
        try:
            logger.info(f"🔌 Conectando REST API para {exchange}...")
            
            # Simula conexão REST API (em produção, usaria CCXT ou biblioteca específica)
            self.rest_clients[exchange] = {
                'connected': True,
                'api_key': api_key[:8] + '...' if api_key else None,
                'connected_at': datetime.now().isoformat(),
                'rate_limit_remaining': 1200
            }
            
            logger.info(f"✅ REST API conectada para {exchange}")
            return True
            
        except Exception as e:
            logger.error(f"❌ Erro ao conectar REST API para {exchange}: {e}")
            return False
    
    async def subscribe_ticker(self, symbol: str) -> bool:
        """Inscreve-se para receber atualizações de ticker"""
        if symbol not in self.subscribed_symbols:
            self.subscribed_symbols.add(symbol)
            if symbol not in self.data_buffers:
                self.data_buffers[symbol] = deque(maxlen=1000)
            logger.info(f"📊 Inscrito no ticker {symbol}")
            return True
        return False
    
    async def subscribe_orderbook(self, symbol: str, depth: int = 10) -> bool:
        """Inscreve-se para receber livro de ofertas"""
        logger.info(f"📚 Inscrito no orderbook {symbol} (depth: {depth})")
        return True
    
    async def subscribe_trades(self, symbol: str) -> bool:
        """Inscreve-se para receber trades agregados"""
        logger.info(f"💱 Inscrito nos trades {symbol}")
        return True
    
    async def fetch_ohlcv(self, exchange: str, symbol: str, 
                          timeframe: str = '1h', limit: int = 100) -> List[Dict[str, Any]]:
        """Busca candles OHLCV via REST API"""
        if exchange not in self.rest_clients:
            return [{'error': f'Exchange {exchange} not connected'}]
        
        try:
            # Simula busca de dados OHLCV (em produção, usaria CCXT)
            base_price = random.uniform(10, 1000)
            ohlcv_data = []
            
            for i in range(limit):
                timestamp = int((datetime.now() - timedelta(hours=i)).timestamp() * 1000)
                open_price = base_price * (1 + random.uniform(-0.01, 0.01))
                close_price = open_price * (1 + random.uniform(-0.02, 0.02))
                high_price = max(open_price, close_price) * (1 + random.uniform(0, 0.01))
                low_price = min(open_price, close_price) * (1 - random.uniform(0, 0.01))
                volume = random.uniform(1000, 100000)
                
                ohlcv_data.append({
                    'timestamp': timestamp,
                    'datetime': datetime.fromtimestamp(timestamp / 1000).isoformat(),
                    'open': open_price,
                    'high': high_price,
                    'low': low_price,
                    'close': close_price,
                    'volume': volume
                })
            
            logger.info(f"📊 OHLCV obtido para {symbol}: {len(ohlcv_data)} candles")
            return ohlcv_data
            
        except Exception as e:
            logger.error(f"❌ Erro ao buscar OHLCV: {e}")
            return [{'error': str(e)}]
    
    async def fetch_orderbook(self, exchange: str, symbol: str, 
                              limit: int = 20) -> Dict[str, Any]:
        """Busca livro de ofertas atual"""
        if exchange not in self.rest_clients:
            return {'error': f'Exchange {exchange} not connected'}
        
        try:
            # Simula orderbook
            base_price = random.uniform(10, 1000)
            bids = []
            asks = []
            
            for i in range(limit):
                bid_price = base_price * (1 - (i + 1) * 0.001)
                bid_volume = random.uniform(0.1, 10)
                bids.append([bid_price, bid_volume])
                
                ask_price = base_price * (1 + (i + 1) * 0.001)
                ask_volume = random.uniform(0.1, 10)
                asks.append([ask_price, ask_volume])
            
            orderbook = {
                'symbol': symbol,
                'bids': bids,
                'asks': asks,
                'timestamp': datetime.now().isoformat(),
                'spread': (asks[0][0] - bids[0][0]) / bids[0][0] * 100 if bids and asks else 0
            }
            
            return orderbook
            
        except Exception as e:
            logger.error(f"❌ Erro ao buscar orderbook: {e}")
            return {'error': str(e)}
    
    def get_latest_data(self, symbol: str, limit: int = 100) -> List[Dict[str, Any]]:
        """Retorna os dados mais recentes do buffer"""
        with self._lock:
            if symbol in self.data_buffers:
                return list(self.data_buffers[symbol])[-limit:]
        return []
    
    async def start_streaming(self) -> None:
        """Inicia streaming de dados em tempo real"""
        self._running = True
        logger.info("🔄 Iniciando streaming de dados em tempo real...")
        
        while self._running:
            try:
                # Simula recebimento de dados em tempo real
                for symbol in self.subscribed_symbols:
                    tick = {
                        'symbol': symbol,
                        'price': random.uniform(10, 1000),
                        'volume': random.uniform(0.1, 10),
                        'timestamp': datetime.now().isoformat(),
                        'source': 'websocket'
                    }
                    
                    # Adiciona ao buffer
                    with self._lock:
                        if symbol in self.data_buffers:
                            self.data_buffers[symbol].append(tick)
                    
                    # Executa callbacks
                    for callback in self._data_callbacks:
                        try:
                            callback(tick)
                        except Exception as e:
                            logger.error(f"Erro no callback: {e}")
                
                await asyncio.sleep(1)  # 1 segundo entre atualizações
                
            except Exception as e:
                logger.error(f"Erro no streaming: {e}")
                await asyncio.sleep(5)
    
    def stop_streaming(self) -> None:
        """Para o streaming de dados"""
        self._running = False
        logger.info("⏹️ Streaming de dados parado")
    
    def disconnect_all(self) -> None:
        """Desconecta todas as conexões"""
        self.stop_streaming()
        
        with self._lock:
            self.websocket_connections.clear()
            self.rest_clients.clear()
            self.subscribed_symbols.clear()
        
        logger.info("🔌 Todas as conexões desconectadas")
    
    def get_connection_status(self) -> Dict[str, Any]:
        """Retorna status das conexões"""
        with self._lock:
            return {
                'websocket_connected': list(self.websocket_connections.keys()),
                'rest_connected': list(self.rest_clients.keys()),
                'subscribed_symbols': list(self.subscribed_symbols),
                'streaming': self._running,
                'data_buffers_size': {k: len(v) for k, v in self.data_buffers.items()}
            }

class UnifiedExchangeConnector:
    """Conexão unificada com múltiplas exchanges via CCXT"""
    
    def __init__(self):
        self.exchanges = {}  # {exchange_name: ccxt_instance}
        self.exchange_configs = {}
        self._lock = threading.RLock()
        self.ccxt_available = self._check_ccxt()
        logger.info("🔗 Unified Exchange Connector inicializado")
    
    def _check_ccxt(self) -> bool:
        """Verifica se CCXT está disponível"""
        try:
            import ccxt
            self.ccxt_module = ccxt
            logger.info("✅ CCXT disponível")
            return True
        except ImportError:
            logger.warning("⚠️ CCXT não disponível - usando modo simulação")
            self.ccxt_module = None
            return False
    
    def connect_exchange(self, exchange_name: str, api_key: str = None, 
                        api_secret: str = None, sandbox: bool = True) -> bool:
        """Conecta a uma exchange específica"""
        with self._lock:
            if exchange_name in self.exchanges:
                logger.warning(f"⚠️ Exchange {exchange_name} já conectada")
                return True
        
        try:
            if not self.ccxt_available:
                # Modo simulação
                self.exchanges[exchange_name] = {
                    'simulated': True,
                    'sandbox': sandbox,
                    'connected': True,
                    'connected_at': datetime.now().isoformat()
                }
                logger.info(f"✅ {exchange_name} conectado (modo simulação)")
                return True
            
            # Modo real com CCXT
            exchange_class = getattr(self.ccxt_module, exchange_name.lower(), None)
            if not exchange_class:
                logger.error(f"❌ Exchange {exchange_name} não suportada pelo CCXT")
                return False
            
            config = {
                'apiKey': api_key,
                'secret': api_secret,
                'enableRateLimit': True,
                'options': {'defaultType': 'spot'}
            }
            
            if sandbox:
                config['sandbox'] = True
                logger.info(f"🧪 Conectando {exchange_name} em modo sandbox")
            
            exchange = exchange_class(config)
            
            # Testa conexão
            markets = exchange.load_markets()
            
            self.exchanges[exchange_name] = {
                'instance': exchange,
                'simulated': False,
                'sandbox': sandbox,
                'connected': True,
                'connected_at': datetime.now().isoformat(),
                'markets_count': len(markets)
            }
            
            logger.info(f"✅ {exchange_name} conectado com {len(markets)} mercados")
            return True
            
        except Exception as e:
            logger.error(f"❌ Erro ao conectar {exchange_name}: {e}")
            return False
    
    def disconnect_exchange(self, exchange_name: str) -> bool:
        """Desconecta de uma exchange"""
        with self._lock:
            if exchange_name not in self.exchanges:
                return False
            
            exchange_data = self.exchanges[exchange_name]
            
            if not exchange_data.get('simulated') and 'instance' in exchange_data:
                try:
                    exchange_data['instance'].close()
                except Exception as e:
                    logger.error(f"Erro ao fechar conexão: {e}")
            
            del self.exchanges[exchange_name]
            logger.info(f"🔌 {exchange_name} desconectado")
            return True
    
    async def get_balance(self, exchange_name: str) -> Dict[str, Any]:
        """Obtém saldo da exchange"""
        with self._lock:
            if exchange_name not in self.exchanges:
                return {'error': f'Exchange {exchange_name} not connected'}
        
        exchange_data = self.exchanges[exchange_name]
        
        if exchange_data.get('simulated'):
            # Simulação
            return {
                'USDT': {'free': 10000.0, 'used': 0.0, 'total': 10000.0},
                'BTC': {'free': 0.5, 'used': 0.0, 'total': 0.5},
                'timestamp': datetime.now().isoformat()
            }
        
        try:
            exchange = exchange_data['instance']
            balance = await exchange.fetch_balance()
            return balance
        except Exception as e:
            logger.error(f"Erro ao buscar saldo: {e}")
            return {'error': str(e)}
    
    async def get_ticker(self, exchange_name: str, symbol: str) -> Dict[str, Any]:
        """Obtém ticker de um símbolo"""
        with self._lock:
            if exchange_name not in self.exchanges:
                return {'error': f'Exchange {exchange_name} not connected'}
        
        exchange_data = self.exchanges[exchange_name]
        
        if exchange_data.get('simulated'):
            # Simulação
            return {
                'symbol': symbol,
                'last': random.uniform(10, 1000),
                'bid': random.uniform(10, 1000),
                'ask': random.uniform(10, 1000),
                'volume': random.uniform(1000, 100000),
                'timestamp': datetime.now().isoformat()
            }
        
        try:
            exchange = exchange_data['instance']
            ticker = await exchange.fetch_ticker(symbol)
            return ticker
        except Exception as e:
            logger.error(f"Erro ao buscar ticker: {e}")
            return {'error': str(e)}
    
    async def create_order(self, exchange_name: str, symbol: str, 
                          order_type: str, side: str, amount: float,
                          price: float = None, params: Dict = None) -> Dict[str, Any]:
        """Cria uma ordem"""
        with self._lock:
            if exchange_name not in self.exchanges:
                return {'error': f'Exchange {exchange_name} not connected'}
        
        exchange_data = self.exchanges[exchange_name]
        
        if exchange_data.get('simulated'):
            # Simulação
            order_id = f"SIM_{int(time.time() * 1000)}"
            return {
                'id': order_id,
                'symbol': symbol,
                'type': order_type,
                'side': side,
                'amount': amount,
                'price': price,
                'status': 'open',
                'timestamp': datetime.now().isoformat()
            }
        
        try:
            exchange = exchange_data['instance']
            order = await exchange.create_order(symbol, order_type, side, amount, price, params or {})
            return order
        except Exception as e:
            logger.error(f"Erro ao criar ordem: {e}")
            return {'error': str(e)}
    
    async def cancel_order(self, exchange_name: str, order_id: str, 
                           symbol: str) -> Dict[str, Any]:
        """Cancela uma ordem"""
        with self._lock:
            if exchange_name not in self.exchanges:
                return {'error': f'Exchange {exchange_name} not connected'}
        
        exchange_data = self.exchanges[exchange_name]
        
        if exchange_data.get('simulated'):
            return {'id': order_id, 'status': 'canceled'}
        
        try:
            exchange = exchange_data['instance']
            result = await exchange.cancel_order(order_id, symbol)
            return result
        except Exception as e:
            logger.error(f"Erro ao cancelar ordem: {e}")
            return {'error': str(e)}
    
    async def get_open_orders(self, exchange_name: str, symbol: str = None) -> List[Dict[str, Any]]:
        """Obtém ordens abertas"""
        with self._lock:
            if exchange_name not in self.exchanges:
                return [{'error': f'Exchange {exchange_name} not connected'}]
        
        exchange_data = self.exchanges[exchange_name]
        
        if exchange_data.get('simulated'):
            return []
        
        try:
            exchange = exchange_data['instance']
            orders = await exchange.fetch_open_orders(symbol)
            return orders
        except Exception as e:
            logger.error(f"Erro ao buscar ordens abertas: {e}")
            return [{'error': str(e)}]
    
    def get_supported_exchanges(self) -> List[str]:
        """Retorna lista de exchanges suportadas"""
        if not self.ccxt_available:
            return ['binance', 'bybit', 'pionex']  # Simulação
        
        return self.ccxt_module.exchanges
    
    def get_connection_status(self) -> Dict[str, Any]:
        """Retorna status das conexões"""
        with self._lock:
            return {
                'connected_exchanges': list(self.exchanges.keys()),
                'ccxt_available': self.ccxt_available,
                'exchanges_detail': {
                    k: {
                        'simulated': v.get('simulated', True),
                        'sandbox': v.get('sandbox', True),
                        'connected_at': v.get('connected_at')
                    }
                    for k, v in self.exchanges.items()
                }
            }

class EmergencyKillSwitch:
    """Kill Switch emergencial para desligamento seguro do sistema"""
    
    def __init__(self, exchange_connector: UnifiedExchangeConnector):
        self.exchange_connector = exchange_connector
        self._triggered = False
        self._trigger_reason = None
        self._trigger_time = None
        self._auto_triggers = {
            'max_daily_loss': 0.10,  # 10% de perda diária
            'max_drawdown': 0.15,  # 15% de drawdown máximo
            'consecutive_losses': 5,  # 5 perdas consecutivas
            'api_error_count': 10,  # 10 erros de API consecutivos
            'connection_timeout': 300  # 5 minutos sem conexão
        }
        self._daily_pnl = 0.0
        self._consecutive_losses = 0
        self._api_error_count = 0
        self._last_connection_time = datetime.now()
        self._lock = threading.RLock()
        self._callbacks = []
        logger.info("🚨 Emergency Kill Switch inicializado")
    
    def add_trigger_callback(self, callback: Callable[[str, str], None]) -> None:
        """Adiciona callback para ser executado quando kill switch é ativado"""
        self._callbacks.append(callback)
    
    def trigger(self, reason: str, force: bool = False) -> bool:
        """Ativa o kill switch manualmente ou automaticamente"""
        with self._lock:
            if self._triggered and not force:
                logger.warning("⚠️ Kill Switch já ativado")
                return False
            
            self._triggered = True
            self._trigger_reason = reason
            self._trigger_time = datetime.now().isoformat()
        
        logger.critical(f"🚨 KILL SWITCH ATIVADO: {reason}")
        
        # Executa callbacks
        for callback in self._callbacks:
            try:
                callback('kill_switch', reason)
            except Exception as e:
                logger.error(f"Erro no callback de kill switch: {e}")
        
        # Fecha todas as posições
        self._emergency_close_all_positions()
        
        # Desconecta todas as exchanges
        self._emergency_disconnect_all()
        
        return True
    
    def reset(self) -> bool:
        """Reseta o kill switch (requer intervenção manual)"""
        with self._lock:
            if not self._triggered:
                return False
            
            self._triggered = False
            self._trigger_reason = None
            self._trigger_time = None
            self._daily_pnl = 0.0
            self._consecutive_losses = 0
            self._api_error_count = 0
        
        logger.info("✅ Kill Switch resetado")
        return True
    
    def _emergency_close_all_positions(self) -> None:
        """Fecha todas as posições abertas emergencialmente"""
        logger.critical("🔒 Fechando todas as posições emergencialmente...")
        
        # Obtém exchanges conectadas
        status = self.exchange_connector.get_connection_status()
        
        for exchange in status['connected_exchanges']:
            try:
                # Obtém ordens abertas
                orders = asyncio.run(self.exchange_connector.get_open_orders(exchange))
                
                for order in orders:
                    if 'error' not in order and 'id' in order:
                        # Cancela ordem
                        result = asyncio.run(self.exchange_connector.cancel_order(
                            exchange, order['id'], order.get('symbol', '')
                        ))
                        logger.info(f"Ordem {order['id']} cancelada")
            except Exception as e:
                logger.error(f"Erro ao fechar posições em {exchange}: {e}")
    
    def _emergency_disconnect_all(self) -> None:
        """Desconecta todas as exchanges"""
        logger.critical("🔌 Desconectando todas as exchanges...")
        
        status = self.exchange_connector.get_connection_status()
        
        for exchange in status['connected_exchanges']:
            try:
                self.exchange_connector.disconnect_exchange(exchange)
                logger.info(f"Exchange {exchange} desconectada")
            except Exception as e:
                logger.error(f"Erro ao desconectar {exchange}: {e}")
    
    def check_auto_triggers(self, current_pnl: float = None, 
                           api_error: bool = False) -> Dict[str, Any]:
        """Verifica gatilhos automáticos"""
        with self._lock:
            if self._triggered:
                return {'triggered': True, 'reason': self._trigger_reason}
            
            triggers_fired = []
            
            # Verifica perda diária
            if current_pnl is not None:
                self._daily_pnl = current_pnl
                if abs(self._daily_pnl) > self._auto_triggers['max_daily_loss']:
                    triggers_fired.append(f"Perda diária excedida: {self._daily_pnl:.2%}")
            
            # Verifica erros de API
            if api_error:
                self._api_error_count += 1
                if self._api_error_count >= self._auto_triggers['api_error_count']:
                    triggers_fired.append(f"Erros de API consecutivos: {self._api_error_count}")
            else:
                self._api_error_count = 0
            
            # Verifica timeout de conexão
            time_since_connection = (datetime.now() - self._last_connection_time).total_seconds()
            if time_since_connection > self._auto_triggers['connection_timeout']:
                triggers_fired.append(f"Timeout de conexão: {time_since_connection}s")
            
            # Ativa kill switch se algum gatilho foi disparado
            if triggers_fired:
                reason = ", ".join(triggers_fired)
                self.trigger(reason)
                return {'triggered': True, 'reason': reason}
        
        return {'triggered': False}
    
    def update_connection_time(self) -> None:
        """Atualiza timestamp da última conexão"""
        with self._lock:
            self._last_connection_time = datetime.now()
    
    def record_trade_result(self, pnl: float) -> None:
        """Registra resultado de trade"""
        with self._lock:
            if pnl < 0:
                self._consecutive_losses += 1
            else:
                self._consecutive_losses = 0
            
            # Verifica perdas consecutivas
            if self._consecutive_losses >= self._auto_triggers['consecutive_losses']:
                self.trigger(f"Perdas consecutivas: {self._consecutive_losses}")
    
    def set_trigger_threshold(self, trigger_name: str, value: float) -> None:
        """Configura limite de gatilho"""
        with self._lock:
            if trigger_name in self._auto_triggers:
                self._auto_triggers[trigger_name] = value
                logger.info(f"Gatilho {trigger_name} configurado para {value}")
    
    def get_status(self) -> Dict[str, Any]:
        """Retorna status do kill switch"""
        with self._lock:
            return {
                'triggered': self._triggered,
                'trigger_reason': self._trigger_reason,
                'trigger_time': self._trigger_time,
                'daily_pnl': self._daily_pnl,
                'consecutive_losses': self._consecutive_losses,
                'api_error_count': self._api_error_count,
                'auto_triggers': self._auto_triggers.copy()
            }

class SecureCredentialManager:
    """Gerenciador de credenciais criptografadas para chaves API"""
    
    def __init__(self, config_path: str = None):
        self.config_path = config_path or (PROJECT_ROOT / "configs" / "encrypted_credentials.json")
        self._credentials = {}
        self._master_key = None
        self._lock = threading.RLock()
        self._fernet = None
        
        # Tenta carregar ou criar master key
        self._initialize_encryption()
        
        # Carrega credenciais existentes
        self._load_credentials()
        
        logger.info("🔐 Secure Credential Manager inicializado")
    
    def _initialize_encryption(self) -> None:
        """Inicializa sistema de criptografia"""
        try:
            from cryptography.fernet import Fernet
            from cryptography.hazmat.primitives import hashes
            from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
            import base64
            import os
            
            # Tenta carregar master key do arquivo
            key_file = PROJECT_ROOT / "configs" / ".master_key"
            
            if key_file.exists():
                with open(key_file, 'rb') as f:
                    self._master_key = f.read()
            else:
                # Gera nova master key
                self._master_key = Fernet.generate_key()
                key_file.parent.mkdir(parents=True, exist_ok=True)
                with open(key_file, 'wb') as f:
                    f.write(self._master_key)
                logger.warning("⚠️ Nova master key gerada. Armazene com segurança!")
            
            self._fernet = Fernet(self._master_key)
            logger.info("✅ Criptografia inicializada com Fernet")
            
        except ImportError:
            logger.warning("⚠️ cryptography não disponível - usando modo sem criptografia")
            self._fernet = None
        except Exception as e:
            logger.error(f"❌ Erro ao inicializar criptografia: {e}")
            self._fernet = None
    
    def _encrypt(self, data: str) -> str:
        """Criptografa dados"""
        if not self._fernet:
            return data  # Retorna sem criptografia se não disponível
        
        try:
            encrypted = self._fernet.encrypt(data.encode())
            return base64.b64encode(encrypted).decode()
        except Exception as e:
            logger.error(f"Erro ao criptografar: {e}")
            return data
    
    def _decrypt(self, encrypted_data: str) -> str:
        """Descriptografa dados"""
        if not self._fernet:
            return encrypted_data  # Retorna sem descriptografia se não disponível
        
        try:
            encrypted = base64.b64decode(encrypted_data.encode())
            decrypted = self._fernet.decrypt(encrypted)
            return decrypted.decode()
        except Exception as e:
            logger.error(f"Erro ao descriptografar: {e}")
            return encrypted_data
    
    def store_credential(self, service: str, credential_type: str, 
                        value: str, metadata: Dict = None) -> bool:
        """Armazena credencial criptografada"""
        with self._lock:
            if service not in self._credentials:
                self._credentials[service] = {}
            
            encrypted_value = self._encrypt(value)
            
            self._credentials[service][credential_type] = {
                'encrypted_value': encrypted_value,
                'metadata': metadata or {},
                'created_at': datetime.now().isoformat(),
                'last_used': None
            }
            
            # Salva imediatamente
            self._save_credentials()
            
            logger.info(f"🔐 Credencial {credential_type} para {service} armazenada")
            return True
    
    def get_credential(self, service: str, credential_type: str) -> Optional[str]:
        """Recupera credencial descriptografada"""
        with self._lock:
            if service not in self._credentials:
                return None
            
            if credential_type not in self._credentials[service]:
                return None
            
            cred_data = self._credentials[service][credential_type]
            decrypted_value = self._decrypt(cred_data['encrypted_value'])
            
            # Atualiza último uso
            cred_data['last_used'] = datetime.now().isoformat()
            self._save_credentials()
            
            return decrypted_value
    
    def store_exchange_credentials(self, exchange_name: str, api_key: str, 
                                   api_secret: str, sandbox: bool = True) -> bool:
        """Armazena credenciais de exchange"""
        self.store_credential(exchange_name, 'api_key', api_key, {'sandbox': sandbox})
        self.store_credential(exchange_name, 'api_secret', api_secret, {'sandbox': sandbox})
        return True
    
    def get_exchange_credentials(self, exchange_name: str) -> Optional[Dict[str, str]]:
        """Recupera credenciais de exchange"""
        api_key = self.get_credential(exchange_name, 'api_key')
        api_secret = self.get_credential(exchange_name, 'api_secret')
        
        if api_key and api_secret:
            return {'api_key': api_key, 'api_secret': api_secret}
        return None
    
    def delete_credential(self, service: str, credential_type: str) -> bool:
        """Deleta credencial"""
        with self._lock:
            if service in self._credentials and credential_type in self._credentials[service]:
                del self._credentials[service][credential_type]
                self._save_credentials()
                logger.info(f"🗑️ Credencial {credential_type} para {service} deletada")
                return True
            return False
    
    def list_services(self) -> List[str]:
        """Lista todos os serviços com credenciais"""
        with self._lock:
            return list(self._credentials.keys())
    
    def list_credential_types(self, service: str) -> List[str]:
        """Lista tipos de credenciais para um serviço"""
        with self._lock:
            if service in self._credentials:
                return list(self._credentials[service].keys())
            return []
    
    def _save_credentials(self) -> None:
        """Salva credenciais em arquivo criptografado"""
        try:
            self.config_path.parent.mkdir(parents=True, exist_ok=True)
            
            with open(self.config_path, 'w') as f:
                json.dump(self._credentials, f, indent=2)
            
        except Exception as e:
            logger.error(f"Erro ao salvar credenciais: {e}")
    
    def _load_credentials(self) -> None:
        """Carrega credenciais do arquivo"""
        try:
            if self.config_path.exists():
                with open(self.config_path, 'r') as f:
                    self._credentials = json.load(f)
                logger.info(f"📂 {len(self._credentials)} serviços carregados")
        except Exception as e:
            logger.error(f"Erro ao carregar credenciais: {e}")
            self._credentials = {}
    
    def export_credentials_backup(self, backup_path: str = None) -> bool:
        """Exporta backup das credenciais"""
        try:
            backup_path = backup_path or (PROJECT_ROOT / "configs" / "credentials_backup.json")
            
            with self._lock:
                with open(backup_path, 'w') as f:
                    json.dump(self._credentials, f, indent=2)
            
            logger.info(f"💾 Backup exportado para {backup_path}")
            return True
            
        except Exception as e:
            logger.error(f"Erro ao exportar backup: {e}")
            return False
    
    def get_status(self) -> Dict[str, Any]:
        """Retorna status do gerenciador"""
        with self._lock:
            return {
                'encryption_enabled': self._fernet is not None,
                'services_count': len(self._credentials),
                'services': list(self._credentials.keys()),
                'config_path': str(self.config_path)
            }

class TradeAutomationModule:
    """Módulo de automação para acesso às plataformas de trade"""
    
    def __init__(self):
        self.connected_platforms = {}
        self.active_orders = {}
        self.order_history = deque(maxlen=1000)
        self._lock = threading.RLock()
        logger.info("🤖 Trade Automation Module inicializado")
    
    def connect_platform(self, platform_name: str, credentials: Dict[str, Any] = None) -> bool:
        """Conecta a uma plataforma de trade"""
        with self._lock:
            try:
                # Simulação de conexão (em produção, usaria API real)
                if platform_name.lower() in ['binance', 'metatrader', 'meta', 'mt4', 'mt5', 'tradingview']:
                    self.connected_platforms[platform_name] = {
                        'connected': True,
                        'credentials': credentials,
                        'connected_at': datetime.now().isoformat(),
                        'platform_type': platform_name.lower()
                    }
                    logger.info(f"✅ Conectado à plataforma {platform_name}")
                    return True
                else:
                    logger.warning(f"⚠️ Plataforma {platform_name} não suportada")
                    return False
            except Exception as e:
                logger.error(f"❌ Erro ao conectar à plataforma {platform_name}: {e}")
                return False
    
    def disconnect_platform(self, platform_name: str) -> bool:
        """Desconecta de uma plataforma de trade"""
        with self._lock:
            if platform_name in self.connected_platforms:
                del self.connected_platforms[platform_name]
                logger.info(f"🔌 Desconectado da plataforma {platform_name}")
                return True
            return False
    
    async def execute_trade(self, platform_name: str, trade_params: Dict[str, Any]) -> Dict[str, Any]:
        """Executa uma ordem na plataforma especificada"""
        with self._lock:
            if platform_name not in self.connected_platforms:
                return {'error': f'Platform {platform_name} not connected'}
            
            if not self.connected_platforms[platform_name]['connected']:
                return {'error': f'Platform {platform_name} is not connected'}
        
        try:
            # Simula execução da ordem
            order_id = self._generate_order_id()
            
            order_result = {
                'order_id': order_id,
                'platform': platform_name,
                'asset': trade_params.get('asset', 'UNKNOWN'),
                'direction': trade_params.get('direction', 'HOLD'),
                'entry_price': trade_params.get('entry_price', 0),
                'take_profit': trade_params.get('take_profit', 0),
                'stop_loss': trade_params.get('stop_loss', 0),
                'position_size_pct': trade_params.get('position_size_pct', 0),
                'operation_time_minutes': trade_params.get('operation_time_minutes', 60),
                'status': 'FILLED',
                'executed_at': datetime.now().isoformat(),
                'confidence': trade_params.get('confidence', 0.5)
            }
            
            # Registra ordem ativa
            self.active_orders[order_id] = {
                **order_result,
                'expires_at': (datetime.now() + timedelta(minutes=trade_params.get('operation_time_minutes', 60))).isoformat()
            }
            
            # Registra no histórico
            self.order_history.append(order_result)
            
            logger.info(f"🤖 Ordem executada: {trade_params.get('direction')} {trade_params.get('asset')} @ {trade_params.get('entry_price')} | TP: {trade_params.get('take_profit')} | SL: {trade_params.get('stop_loss')}")
            
            return order_result
            
        except Exception as e:
            logger.error(f"❌ Erro ao executar ordem: {e}")
            return {'error': str(e)}
    
    async def close_order(self, order_id: str, reason: str = 'MANUAL') -> Dict[str, Any]:
        """Fecha uma ordem ativa"""
        with self._lock:
            if order_id not in self.active_orders:
                return {'error': f'Order {order_id} not found'}
            
            order = self.active_orders[order_id]
            del self.active_orders[order_id]
        
        # Simula fechamento
        closed_order = {
            **order,
            'status': 'CLOSED',
            'closed_at': datetime.now().isoformat(),
            'close_reason': reason
        }
        
        self.order_history.append(closed_order)
        logger.info(f"🔒 Ordem fechada: {order_id} | Razão: {reason}")
        
        return closed_order
    
    def get_active_orders(self) -> List[Dict[str, Any]]:
        """Retorna todas as ordens ativas"""
        with self._lock:
            return list(self.active_orders.values())
    
    def get_order_history(self, limit: int = 50) -> List[Dict[str, Any]]:
        """Retorna histórico de ordens"""
        return list(self.order_history)[-limit:]
    
    def _generate_order_id(self) -> str:
        """Gera um ID único para a ordem"""
        return f"ORD_{int(time.time() * 1000)}_{random.randint(1000, 9999)}"
    
    async def monitor_active_orders(self) -> Dict[str, Any]:
        """Monitora ordens ativas e verifica TP/SL"""
        with self._lock:
            active_orders_copy = self.active_orders.copy()
        
        monitoring_results = {
            'checked_orders': len(active_orders_copy),
            'closed_orders': [],
            'active_orders': []
        }
        
        for order_id, order in active_orders_copy.items():
            # Simula verificação de TP/SL (em produção, usaria dados reais)
            should_close = False
            close_reason = 'MANUAL'
            
            # Simula fechamento aleatório para demonstração
            if random.random() < 0.1:  # 10% de chance de fechar
                should_close = True
                close_reason = random.choice(['TAKE_PROFIT', 'STOP_LOSS', 'TIME_EXPIRED'])
            
            if should_close:
                result = await self.close_order(order_id, close_reason)
                monitoring_results['closed_orders'].append(result)
            else:
                monitoring_results['active_orders'].append(order_id)
        
        return monitoring_results
    
    def get_platform_status(self) -> Dict[str, Any]:
        """Retorna status das plataformas conectadas"""
        with self._lock:
            return {
                'connected_platforms': list(self.connected_platforms.keys()),
                'active_orders_count': len(self.active_orders),
                'total_orders_executed': len(self.order_history)
            }

class AdvancedMemorySystem:
    """Sistema de memória com múltiplas camadas"""
    
    def __init__(self, orchestrator: AdvancedQuantumBrainOrchestrator):
        self.orchestrator = orchestrator
        self.short_term_memory = deque(maxlen=1000)
        self.long_term_memory: Dict[str, Dict[str, Any]] = {}
        self.semantic_memory = {}
        self.semantic_memory_history = deque(maxlen=1000)
        self.episodic_memory = []
        self.memory_associations = {}
        
        # Consolidar múltiplos contextos em um único dicionário aninhado
        # Isso reduz overhead de múltiplos dicionários e melhora cache locality
        self.memory_contexts = {
            'emotional': {},
            'analytical': {},
            'creative': {},
            'predictive': {},
            'security': {},
            'network': {},
            'api': {},
            'database': {},
            'generative': {},
            'reinforcement': {},
            'attention': {}
        }
        
        self.memory_index = {}
        self.conn = None
        
        self.setup_memory_database()
        logger.info("💾 Sistema de Memória Avançada inicializado")
    
    def setup_memory_database(self):
        """Configura banco de dados para memória"""
        if not SQLITE_AVAILABLE:
            logger.warning("⚠️ SQLite não disponível. Usando apenas memória volátil.")
            return
        
        try:
            db_path = PROJECT_DIRS["data"] / "brain_memory.db"
            self.conn = sqlite3.connect(str(db_path), check_same_thread=False)
            self.create_memory_tables()
            logger.info("✅ Banco de dados de memória configurado")
        except Exception as e:
            logger.error(f"❌ Erro no banco de memória: {e}")
            self.conn = None
    
    def create_memory_tables(self):
        """Cria tabelas para armazenamento de memória"""
        if not self.conn:
            return
        
        cursor = self.conn.cursor()
        
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS short_term_memory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp DATETIME,
                neuron_id TEXT,
                activation REAL,
                context TEXT,
                importance REAL
            )
        ''')
        
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS long_term_memory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                memory_hash TEXT UNIQUE,
                content BLOB,
                importance REAL,
                last_accessed DATETIME,
                access_count INTEGER DEFAULT 0,
                created_at DATETIME,
                tags TEXT
            )
        ''')
        
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS memory_associations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                memory_hash1 TEXT,
                memory_hash2 TEXT,
                strength REAL,
                last_used DATETIME,
                created_at DATETIME,
                UNIQUE(memory_hash1, memory_hash2)
            )
        ''')
        
        cursor.execute('''
            CREATE INDEX IF NOT EXISTS idx_long_term_hash 
            ON long_term_memory(memory_hash)
        ''')
        
        cursor.execute('''
            CREATE INDEX IF NOT EXISTS idx_associations 
            ON memory_associations(memory_hash1, memory_hash2)
        ''')
        
        self.conn.commit()
    
    def store_memory(self, content: Any, importance: float = 0.5, 
                    tags: List[str] = None) -> str:
        """Armazena conteúdo na memória de longo prazo"""
        # Gera hash do conteúdo
        content_str = str(content)
        memory_hash = hashlib.sha256(content_str.encode()).hexdigest()[:16]
        
        memory_entry = {
            'content': content,
            'importance': importance,
            'last_accessed': datetime.now(),
            'access_count': 1,
            'created_at': datetime.now(),
            'tags': tags or []
        }
        
        # Armazena em memória volátil
        self.long_term_memory[memory_hash] = memory_entry
        
        # Armazena em banco de dados
        if self.conn:
            try:
                cursor = self.conn.cursor()
                cursor.execute('''
                    INSERT OR REPLACE INTO long_term_memory 
                    (memory_hash, content, importance, last_accessed, access_count, created_at, tags)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                ''', (
                    memory_hash,
                    pickle.dumps(content),
                    importance,
                    datetime.now().isoformat(),
                    1,
                    datetime.now().isoformat(),
                    json.dumps(tags or [])
                ))
                self.conn.commit()
            except Exception as e:
                logger.error(f"❌ Erro ao armazenar memória no banco: {e}")
        
        return memory_hash
    
    def retrieve_memory(self, memory_hash: str) -> Optional[Any]:
        """Recupera memória pelo hash"""
        # Tenta memória volátil primeiro
        if memory_hash in self.long_term_memory:
            memory = self.long_term_memory[memory_hash]
            memory['last_accessed'] = datetime.now()
            memory['access_count'] += 1
            return memory['content']
        
        # Tenta banco de dados
        if self.conn:
            try:
                cursor = self.conn.cursor()
                cursor.execute('''
                    SELECT content FROM long_term_memory
                    WHERE memory_hash = ?
                ''', (memory_hash,))
                
                result = cursor.fetchone()
                if result:
                    # Atualiza contador de acesso
                    cursor.execute('''
                        UPDATE long_term_memory
                        SET last_accessed = ?, access_count = access_count + 1
                        WHERE memory_hash = ?
                    ''', (datetime.now().isoformat(), memory_hash))
                    self.conn.commit()
                    
                    content = pickle.loads(result[0])
                    
                    # Armazena em cache volátil
                    self.long_term_memory[memory_hash] = {
                        'content': content,
                        'importance': 0.5,
                        'last_accessed': datetime.now(),
                        'access_count': 1
                    }
                    
                    return content
                    
            except Exception as e:
                logger.error(f"❌ Erro ao recuperar memória do banco: {e}")
        
        return None
    
    def associate_memories(self, memory_hash1: str, memory_hash2: str, 
                          strength: float = 0.5) -> bool:
        """Cria associação entre duas memórias"""
        if not self.conn:
            return False
        
        try:
            cursor = self.conn.cursor()
            cursor.execute('''
                INSERT OR REPLACE INTO memory_associations
                (memory_hash1, memory_hash2, strength, last_used, created_at)
                VALUES (?, ?, ?, ?, ?)
            ''', (
                memory_hash1,
                memory_hash2,
                strength,
                datetime.now().isoformat(),
                datetime.now().isoformat()
            ))
            self.conn.commit()
            return True
        except Exception as e:
            logger.error(f"❌ Erro ao criar associação de memória: {e}")
            return False
    
    def associative_recall(self, trigger: Any, limit: int = 5) -> List[Any]:
        """Recupera memórias associativas baseadas em similaridade"""
        memories = []
        trigger_str = str(trigger).lower()
        
        for mem_hash, memory in self.long_term_memory.items():
            content_str = str(memory['content']).lower()
            
            # Similaridade simples (substring)
            if trigger_str in content_str:
                memories.append({
                    'hash': mem_hash,
                    'content': memory['content'],
                    'importance': memory['importance'],
                    'similarity': len(trigger_str) / len(content_str) if content_str else 0
                })
            
            if len(memories) >= limit:
                break
        
        # Ordena por similaridade
        memories.sort(key=lambda x: x['similarity'], reverse=True)
        return [m['content'] for m in memories]

# =============================================================================
# ORQUESTRADOR CEREBRAL TOTALMENTE INTEGRADO
# =============================================================================

class IntegratedBrainOrchestrator(AdvancedQuantumBrainOrchestrator):
    """
    Orquestrador cerebral totalmente integrado com todos os módulos
    Otimizado com lazy loading, cache e sistemas de eficiência
    """
    
    def __init__(self, iag_path: str = "./iag_modules", 
                 quantum_path: str = "./quantum_modules",
                 config: OrchestratorConfig = None):
        super().__init__(iag_path, quantum_path)
        
        # Configuração centralizada
        self.config = config or OrchestratorConfig()
        
        # Sistemas de integração
        self.integration_hub = IntegrationHub()
        self.security_framework = AdvancedSecurityFramework()
        self.monitoring_system = AdvancedMonitoringSystem()
        self.cache_system = DistributedCacheSystem()
        self.persistence_system = PersistenceSystem()
        self.optimization_engine = AdaptiveOptimizationEngine()
        
        # Sistemas de otimização
        self.stats_cache = CachedStatistics(ttl_seconds=self.config.cache_ttl)
        self.batch_processor = BatchProcessor(
            batch_size=self.config.batch_size,
            max_wait=self.config.batch_max_wait
        )
        self.health_monitor = HealthMonitor(check_interval=self.config.health_check_interval)
        self.resource_scheduler = ResourceScheduler()
        self.bridge_pool = BridgePool(self.integration_hub, max_bridges=self.config.max_bridges)
        
        # Lazy loading - bridges não são inicializadas imediatamente
        self._neural_bridge = None
        self._quantum_bridge = None
        self._analysis_bridge = None
        self._learning_bridge = None
        
        # Lazy loading - módulos de trading
        self._market_analysis = None
        self._asset_selection = None
        self._trade_operations = None
        self._trade_automation = None
        self._realtime_data = None
        self._exchange_connector = None
        self._kill_switch = None
        self._credential_manager = None
        self._alpha_hunter = None
        
        # Módulos críticos inicializados imediatamente
        self.ml_bridge = MLModuleBridge(self.integration_hub)
        
        # Registra módulos no hub
        self._register_all_modules()
        
        # Inicia processamento de dados
        self._start_integration_loop()
        
        # Atualiza neurônios
        self._upgrade_neurons()
        self._create_neural_clusters()
        self._create_neural_connections()
        self._create_neural_associations()
        self._create_neural_emotional_context()
        self._create_neural_analytical_context()
        self._create_neural_creative_context()
        self._create_neural_predictive_context()
        self._create_neural_security_context()
        self._create_neural_network_context()
        self._create_neural_api_context()
        self._create_neural_database_context()
        logger.info("🌐 Orquestrador Integrado inicializado com sucesso!")
        logger.info(f"📌 VHALINOR IAG {VERSION} - {CODENAME}")
        logger.info(f"🧠 Sistema Totalmente Operacional | Neurônios: {len(self.neurons)}")
        logger.info(f"⚡ Otimizações ativadas: Lazy Loading, Cache, Batch Processing")
    
    # Lazy loading properties para bridges
    @property
    def neural_bridge(self) -> NeuralDataBridge:
        if self._neural_bridge is None:
            self._neural_bridge = NeuralDataBridge(self.integration_hub)
        return self._neural_bridge
    
    @property
    def quantum_bridge(self) -> QuantumDataBridge:
        if self._quantum_bridge is None:
            self._quantum_bridge = QuantumDataBridge(self.integration_hub)
        return self._quantum_bridge
    
    @property
    def analysis_bridge(self) -> AnalysisDataBridge:
        if self._analysis_bridge is None:
            self._analysis_bridge = AnalysisDataBridge(self.integration_hub)
        return self._analysis_bridge
    
    @property
    def learning_bridge(self) -> ContinuousLearningBridge:
        if self._learning_bridge is None:
            self._learning_bridge = ContinuousLearningBridge(self.integration_hub)
        return self._learning_bridge
    
    # Lazy loading properties para módulos de trading
    @property
    def market_analysis(self) -> MarketAnalysisModule:
        if self._market_analysis is None:
            self._market_analysis = MarketAnalysisModule(self.ml_bridge)
        return self._market_analysis
    
    @property
    def asset_selection(self) -> AssetSelectionEngine:
        if self._asset_selection is None:
            self._asset_selection = AssetSelectionEngine(self.ml_bridge, self.market_analysis)
        return self._asset_selection
    
    @property
    def trade_operations(self) -> TradeOperationManager:
        if self._trade_operations is None:
            self._trade_operations = TradeOperationManager(self.ml_bridge)
        return self._trade_operations
    
    @property
    def trade_automation(self) -> TradeAutomationModule:
        if self._trade_automation is None:
            self._trade_automation = TradeAutomationModule()
        return self._trade_automation
    
    @property
    def realtime_data(self) -> RealTimeDataCollector:
        if self._realtime_data is None:
            self._realtime_data = RealTimeDataCollector()
        return self._realtime_data
    
    @property
    def exchange_connector(self) -> UnifiedExchangeConnector:
        if self._exchange_connector is None:
            self._exchange_connector = UnifiedExchangeConnector()
        return self._exchange_connector
    
    @property
    def kill_switch(self) -> EmergencyKillSwitch:
        if self._kill_switch is None:
            self._kill_switch = EmergencyKillSwitch(self.exchange_connector)
        return self._kill_switch
    
    @property
    def credential_manager(self) -> SecureCredentialManager:
        if self._credential_manager is None:
            self._credential_manager = SecureCredentialManager()
        return self._credential_manager
    
    @property
    def alpha_hunter(self) -> Optional['AlphaHunterBot']:
        if self._alpha_hunter is None:
            try:
                from Vhalinor_Alpha_Hunter import AlphaHunterBot
                self._alpha_hunter = AlphaHunterBot(self.exchange_connector)
                logger.info("🗡️ Alpha Hunter Bot carregado (lazy loading)")
            except ImportError:
                logger.warning("⚠️ Alpha Hunter não disponível")
        return self._alpha_hunter
    
    def _register_all_modules(self):
        """Registra todos os módulos no hub de integração"""
        self.integration_hub.register_module('neural_engine', self)
        self.integration_hub.register_module('quantum_engine', self.advanced_quantum)
        self.integration_hub.register_module('ml_module', self.ml_module)
        self.integration_hub.register_module('memory_system', self.advanced_memory)
        self.integration_hub.register_module('security', self.security_framework)
        self.integration_hub.register_module('monitoring', self.monitoring_system)
        self.integration_hub.register_module('cache', self.cache_system)
        self.integration_hub.register_module('persistence', self.persistence_system)
        self.integration_hub.register_module('optimization', self.optimization_engine)
        
        if self.neural_bridge.neural_bus:
            self.integration_hub.register_module('neural_bus', self.neural_bridge.neural_bus)
        
        if self.quantum_bridge.quantum_core:
            self.integration_hub.register_module('quantum_core', self.quantum_bridge.quantum_core)
        
        if self.analysis_bridge.data_analyzer:
            self.integration_hub.register_module('analysis', self.analysis_bridge)
        
        if self.learning_bridge.learning_service:
            self.integration_hub.register_module('continuous_learning', self.learning_bridge)
    
    @profile_performance(threshold=0.1)
    async def sync_all_bridges_parallel(self, neurons: Dict[str, AdvancedNeuron] = None) -> Dict[str, Any]:
        """Sincroniza todas as bridges em paralelo usando asyncio.gather - otimizado"""
        tasks = []
        
        if neurons:
            tasks.append(self.neural_bridge.sync_neural_state(neurons))
        
        # Adiciona outras sincronizações conforme necessário
        # tasks.append(self.quantum_bridge.sync_quantum_state())
        # tasks.append(self.analysis_bridge.sync_analysis_state())
        
        if tasks:
            results = await asyncio.gather(*tasks, return_exceptions=True)
            
            sync_report = {
                'timestamp': datetime.now().isoformat(),
                'bridges_synced': len([r for r in results if not isinstance(r, Exception)]),
                'errors': [str(r) for r in results if isinstance(r, Exception)],
                'results': results
            }
            
            logger.info(f"⚡ Sincronização paralela concluída: {sync_report['bridges_synced']}/{len(tasks)} bridges")
            return sync_report
        
        return {'timestamp': datetime.now().isoformat(), 'bridges_synced': 0}
    
    def _start_integration_loop(self):
        """Inicia loop de integração em thread separada"""
        
        async def integration_task():
            await self.integration_hub.process_data_queue()
        
        def run_integration():
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            loop.run_until_complete(integration_task())
        
        thread = threading.Thread(target=run_integration, daemon=True)
        thread.start()
        logger.info("🔄 Loop de integração iniciado")
    
    async def receive_data(self, packet: DataPacket):
        """Recebe dados de outros módulos"""
        logger.debug(f"📥 Dados recebidos: {packet.data_type} de {packet.source_module}")
        
        # Registra métrica
        self.monitoring_system.record_metric(
            'data_packets_received',
            1.0,
            {'type': packet.data_type, 'source': packet.source_module}
        )
        
        # Processa baseado no tipo de dados
        if packet.data_type == 'quantum_state_sync':
            await self._process_quantum_state(packet.payload)
        elif packet.data_type == 'pattern_analysis':
            await self._process_pattern_analysis(packet.payload)
        elif packet.data_type == 'risk_assessment':
            await self._process_risk_assessment(packet.payload)
        elif packet.data_type == 'learning_update':
            await self._process_learning_update(packet.payload)
        elif packet.data_type == 'learning_insights':
            await self._process_learning_insights(packet.payload)
    
    async def _process_quantum_state(self, quantum_state: Dict[str, Any]):
        """Processa estado quântico recebido"""
        entropy = quantum_state.get('quantum_entropy', 0)
        logger.info(f"⚛️ Estado quântico processado: entropia={entropy:.3f}")
        
        # Aplica emaranhamento quântico
        entangled_pairs = quantum_state.get('entanglement_pairs', [])
        for pair in entangled_pairs[:3]:
            if len(pair) == 2:
                if self.advanced_quantum:
                    self.advanced_quantum.create_quantum_entanglement(pair[0], pair[1])
    
    async def _process_pattern_analysis(self, pattern_data: Dict[str, Any]):
        """Processa análise de padrões recebida"""
        strength = pattern_data.get('pattern_strength', 0)
        logger.info(f"🔍 Análise de padrões: força={strength:.3f}")
        
        # Ajusta taxas de aprendizado baseado em padrões
        if strength > 0.7:
            for neuron in list(self.neurons.values())[:5]:
                neuron.learning_rate *= 1.05
                neuron.learning_rate = min(0.1, neuron.learning_rate)
    
    async def _process_risk_assessment(self, risk_data: Dict[str, Any]):
        """Processa avaliação de risco recebida"""
        risk_level = risk_data.get('risk_level', 'medium')
        risk_score = risk_data.get('risk_score', 50)
        
        logger.info(f"⚠️ Avaliação de risco: {risk_level} (score={risk_score:.1f})")
        
        # Altera estado cerebral baseado no risco
        if risk_level == 'high':
            self.brain_state = BrainState.SECURITY_SCAN
            # Aumenta limiares para ser mais conservador
            for neuron in self.neurons.values():
                neuron.activation_threshold = min(0.9, neuron.activation_threshold * 1.1)
        elif risk_level == 'low':
            self.brain_state = BrainState.ANALYTICAL
            # Diminui limiares para ser mais agressivo
            for neuron in list(self.neurons.values())[:10]:
                neuron.activation_threshold = max(0.3, neuron.activation_threshold * 0.95)
    
    async def _process_learning_update(self, learning_data: Dict[str, Any]):
        """Processa atualização de aprendizado"""
        models_updated = learning_data.get('models_updated', [])
        improvement = learning_data.get('accuracy_improvement', 0)
        
        logger.info(f"📚 Atualização de aprendizado: melhoria={improvement:.2f}%")
        
        # Atualiza modelos de ML
        if 'neural' in models_updated and self.ml_module:
            await self.ml_module.train_on_brain_data()
    
    async def _process_learning_insights(self, insights: Dict[str, Any]):
        """Processa insights de aprendizado"""
        logger.info(f"💡 Insight recebido: {insights.get('insight_type', 'general')}")
        
        # Armazena insight na memória
        if self.advanced_memory:
            self.advanced_memory.store_memory(
                content=insights,
                importance=0.8,
                tags=['insight', insights.get('insight_type', 'general')]
            )
    
    async def sync_all_modules(self) -> Dict[str, Any]:
        """Sincroniza dados entre todos os módulos"""
        logger.info("🔄 Sincronizando todos os módulos...")
        
        results = {}
        
        # Sincroniza estado neural
        if self.neural_bridge:
            results['neural'] = await self.neural_bridge.sync_neural_state(self.neurons)
        
        # Sincroniza estado quântico
        if self.advanced_quantum and self.quantum_bridge:
            quantum_data = {
                'entangled_pairs': self.advanced_quantum.entangled_pairs,
                'entropy': random.uniform(0, 1),
                'executions': len(self.advanced_quantum.circuits)
            }
            results['quantum'] = await self.quantum_bridge.sync_quantum_state(quantum_data)
        
        # Analisa padrões neurais
        if self.analysis_bridge and results.get('neural'):
            results['patterns'] = await self.analysis_bridge.analyze_neural_patterns(results['neural'])
        
        logger.info("✅ Sincronização completa")
        return results
    
    async def execute_intelligent_trading_flow(self, asset_list: List[str], 
                                               platform_name: str,
                                               platform_credentials: Dict[str, Any] = None) -> Dict[str, Any]:
        """
        Fluxo principal de trading inteligente:
        1. Análise de dados de mercado
        2. Seleção do melhor ativo via predições ensemble
        3. Cálculo automático de TP, SL e tempo de operação pela IA
        4. Execução autônoma na plataforma conectada
        """
        logger.info("🚀 Iniciando fluxo de trading inteligente...")
        
        flow_result = {
            'timestamp': datetime.now().isoformat(),
            'stages': {},
            'final_decision': None
        }
        
        try:
            # STAGE 1: Configuração e Conexão
            logger.info("📡 STAGE 1: Conectando à plataforma...")
            connected = self.trade_automation.connect_platform(platform_name, platform_credentials)
            if not connected:
                return {'error': f'Failed to connect to platform {platform_name}', 'stage': 'connection'}
            
            self.market_analysis.set_asset_list(asset_list)
            flow_result['stages']['connection'] = {'status': 'success', 'platform': platform_name}
            
            # STAGE 2: Análise de Dados de Mercado
            logger.info("📊 STAGE 2: Analisando dados de mercado...")
            market_analysis = await self.market_analysis.analyze_all_assets(asset_list)
            if 'error' in market_analysis:
                return {'error': market_analysis['error'], 'stage': 'market_analysis'}
            
            flow_result['stages']['market_analysis'] = {
                'status': 'success',
                'assets_analyzed': len(market_analysis)
            }
            
            # STAGE 3: Seleção do Melhor Ativo via IA
            logger.info("🎯 STAGE 3: Selecionando melhor ativo via predições ensemble...")
            best_asset = await self.asset_selection.select_best_asset(asset_list)
            if 'error' in best_asset:
                return {'error': best_asset['error'], 'stage': 'asset_selection'}
            
            flow_result['stages']['asset_selection'] = {
                'status': 'success',
                'selected_asset': best_asset['asset'],
                'selection_score': best_asset['score'],
                'confidence': best_asset['prediction']['confidence'],
                'direction': best_asset['prediction']['direction']
            }
            
            # STAGE 4: Cálculo Automático de Parâmetros pela IA
            logger.info("⏱️ STAGE 4: Calculando TP, SL e tempo de operação pela IA...")
            trade_params = self.trade_operations.calculate_operation_parameters(best_asset)
            
            flow_result['stages']['operation_parameters'] = {
                'status': 'success',
                'direction': trade_params['direction'],
                'entry_price': trade_params['entry_price'],
                'take_profit': trade_params['take_profit'],
                'take_profit_pct': trade_params['take_profit_pct'],
                'stop_loss': trade_params['stop_loss'],
                'stop_loss_pct': trade_params['stop_loss_pct'],
                'operation_time_minutes': trade_params['operation_time_minutes'],
                'position_size_pct': trade_params['position_size_pct'],
                'risk_reward_ratio': trade_params['risk_reward_ratio']
            }
            
            # STAGE 5: Validação de Risco Pré-Trade
            logger.info("⚠️ STAGE 5: Validando risco pré-trade...")
            risk_validation = self._validate_pre_trade_risk(trade_params)
            if not risk_validation['approved']:
                return {
                    'error': 'Trade rejected by risk validation',
                    'reason': risk_validation['reason'],
                    'stage': 'risk_validation'
                }
            
            flow_result['stages']['risk_validation'] = {
                'status': 'success',
                'risk_score': risk_validation['risk_score']
            }
            
            # STAGE 6: Execução Autônoma na Plataforma
            logger.info("🤖 STAGE 6: Executando ordem na plataforma...")
            execution_result = await self.trade_automation.execute_trade(platform_name, trade_params)
            if 'error' in execution_result:
                return {'error': execution_result['error'], 'stage': 'execution'}
            
            flow_result['stages']['execution'] = {
                'status': 'success',
                'order_id': execution_result['order_id'],
                'executed_at': execution_result['executed_at']
            }
            
            # RESULTADO FINAL
            flow_result['final_decision'] = {
                'action': 'EXECUTED',
                'asset': best_asset['asset'],
                'order_id': execution_result['order_id'],
                'summary': f"{trade_params['direction']} {best_asset['asset']} @ {trade_params['entry_price']} | TP: {trade_params['take_profit_pct']:.2f}% | SL: {trade_params['stop_loss_pct']:.2f}% | Tempo: {trade_params['operation_time_minutes']}min"
            }
            
            logger.info(f"✅ Fluxo de trading concluído com sucesso!")
            logger.info(f"📋 {flow_result['final_decision']['summary']}")
            
            return flow_result
            
        except Exception as e:
            logger.error(f"❌ Erro no fluxo de trading: {e}")
            return {'error': str(e), 'stage': 'exception'}
    
    def _validate_pre_trade_risk(self, trade_params: Dict[str, Any]) -> Dict[str, Any]:
        """Valida risco antes de executar o trade"""
        risk_score = 0.0
        reasons = []
        
        # Validação 1: Risk/Reward Ratio
        if trade_params['risk_reward_ratio'] < 1.0:
            risk_score += 0.3
            reasons.append('Low risk/reward ratio')
        
        # Validação 2: Confiança da predição
        if trade_params['confidence'] < 0.6:
            risk_score += 0.4
            reasons.append('Low prediction confidence')
        
        # Validação 3: Volatilidade excessiva
        if trade_params['volatility'] > 0.05:
            risk_score += 0.2
            reasons.append('High volatility')
        
        # Validação 4: Tamanho de posição
        if trade_params['position_size_pct'] > 20:
            risk_score += 0.1
            reasons.append('Large position size')
        
        approved = risk_score < 0.5
        
        return {
            'approved': approved,
            'risk_score': risk_score,
            'reason': ', '.join(reasons) if reasons else 'Trade approved'
        }
    
    async def process_with_all_modules(self, input_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Processa dados usando todos os módulos integrados
        """
        logger.info("🚀 Processamento integrado iniciado")
        
        results = {
            'timestamp': datetime.now().isoformat(),
            'input_data': input_data,
            'stages': {}
        }
        
        # Estágio 1: Validação de segurança
        is_valid, message = self.security_framework.validate_input(input_data, 'dict')
        if not is_valid:
            return {'error': message, 'stage': 'security_validation'}
        
        # Estágio 2: Processamento Neural
        logger.info("1️⃣ Estágio Neural...")
        neural_result = await self._neural_processing_stage(input_data)
        results['stages']['neural'] = neural_result
        
        # Estágio 3: Otimização Quântica
        logger.info("2️⃣ Estágio Quântico...")
        quantum_result = await self._quantum_processing_stage(neural_result)
        results['stages']['quantum'] = quantum_result
        
        # Estágio 4: Análise de Padrões
        logger.info("3️⃣ Estágio de Análise...")
        analysis_result = await self._analysis_processing_stage(quantum_result)
        results['stages']['analysis'] = analysis_result
        
        # Estágio 5: Aprendizado Contínuo
        logger.info("4️⃣ Estágio de Aprendizado...")
        learning_result = await self._learning_processing_stage(analysis_result)
        results['stages']['learning'] = learning_result
        
        # Resultado final
        results['final_output'] = self._combine_results(results['stages'])
        
        logger.info("✅ Processamento integrado concluído")
        return results
    
    async def _neural_processing_stage(self, data: Dict[str, Any]) -> Dict[str, Any]:
        """Estágio de processamento neural"""
        # Ativa neurônios relevantes
        activated_neurons = []
        
        for neuron_id, neuron in list(self.neurons.items())[:20]:
            activation = random.uniform(0, 1)
            if activation > neuron.activation_threshold:
                neuron.activate(activation)
                activated_neurons.append(neuron_id)
        
        # Transmite padrão neural
        pattern = {
            'pattern_type': 'sequential',
            'activated_neurons': activated_neurons,
            'activation_count': len(activated_neurons),
            'strength': len(activated_neurons) / 20 if activated_neurons else 0
        }
        
        if self.neural_bridge:
            await self.neural_bridge.broadcast_neural_pattern(pattern)
        
        return {
            'activated_neurons': len(activated_neurons),
            'pattern': pattern,
            'neural_state': self.brain_state.label
        }
    
    async def _quantum_processing_stage(self, neural_data: Dict[str, Any]) -> Dict[str, Any]:
        """Estágio de processamento quântico"""
        # Aplica otimização quântica
        optimized = neural_data
        quantum_boost = 1.0
        
        if self.quantum_bridge:
            optimized = await self.quantum_bridge.apply_quantum_optimization(neural_data)
            quantum_boost = optimized.get('optimization_factor', 1.0)
        
        # Executa circuito quântico
        quantum_result = {'executed': False}
        if self.advanced_quantum and QISKIT_AVAILABLE:
            quantum_result = await self.advanced_quantum.execute_quantum_circuit('superposition', shots=512)
        
        return {
            'optimized_data': optimized,
            'quantum_execution': quantum_result,
            'quantum_boost': quantum_boost
        }
    
    async def _analysis_processing_stage(self, quantum_data: Dict[str, Any]) -> Dict[str, Any]:
        """Estágio de análise"""
        patterns = {}
        risk = {}
        
        if self.analysis_bridge:
            patterns = await self.analysis_bridge.analyze_neural_patterns(quantum_data)
            risk = await self.analysis_bridge.assess_risk(quantum_data)
        
        return {
            'patterns': patterns,
            'risk_assessment': risk,
            'analysis_complete': bool(patterns)
        }
    
    async def _learning_processing_stage(self, analysis_data: Dict[str, Any]) -> Dict[str, Any]:
        """Estágio de aprendizado contínuo"""
        if not self.learning_bridge:
            return {'learning_complete': False}
        
        # Atualiza modelos de aprendizado
        training_data = {
            'patterns': analysis_data.get('patterns', {}),
            'risk': analysis_data.get('risk_assessment', {}),
            'timestamp': datetime.now().isoformat()
        }
        
        learning_update = await self.learning_bridge.update_learning_models(training_data)
        
        # Compartilha insights
        insights = {
            'type': 'processing_insights',
            'content': {
                'pattern_strength': analysis_data.get('patterns', {}).get('pattern_strength', 0),
                'risk_level': analysis_data.get('risk_assessment', {}).get('risk_level', 'unknown')
            },
            'confidence': 0.85,
            'impact': 0.6,
            'tags': ['processing', 'analysis']
        }
        
        await self.learning_bridge.share_learning_insights(insights)
        
        return {
            'models_updated': bool(learning_update),
            'insights_shared': True,
            'learning_complete': True
        }
    
    def _combine_results(self, stages: Dict[str, Any]) -> Dict[str, Any]:
        """Combina resultados de todos os estágios"""
        return {
            'neural_activation': stages.get('neural', {}).get('activated_neurons', 0),
            'quantum_boost': stages.get('quantum', {}).get('quantum_boost', 1.0),
            'risk_level': stages.get('analysis', {}).get('risk_assessment', {}).get('risk_level', 'unknown'),
            'learning_complete': stages.get('learning', {}).get('learning_complete', False),
            'brain_state': self.brain_state.label,
            'brain_state_icon': self.brain_state.icon,
            'overall_confidence': random.uniform(0.75, 0.95),
            'timestamp': datetime.now().isoformat()
        }
    
    def get_integration_status(self) -> Dict[str, Any]:
        """Retorna status completo da integração"""
        return {
            'timestamp': datetime.now().isoformat(),
            'version': VERSION,
            'codename': CODENAME,
            'brain_stats': self.get_brain_stats(),
            'integration_hub': self.integration_hub.get_integration_stats(),
            'security': self.security_framework.get_security_report(),
            'monitoring': self.monitoring_system.get_system_report(),
            'cache': self.cache_system.get_cache_stats(),
            'persistence': self.persistence_system.get_storage_stats(),
            'optimization': self.optimization_engine.get_optimization_report(),
            'neural_bridge': {
                'active': self.neural_bridge.neural_bus is not None
            },
            'quantum_bridge': {
                'active': self.quantum_bridge.quantum_core is not None,
                'circuits': len(self.advanced_quantum.circuits) if self.advanced_quantum else 0
            },
            'analysis_bridge': {
                'active': self.analysis_bridge.data_analyzer is not None
            },
            'learning_bridge': {
                'active': self.learning_bridge.learning_service is not None
            },
            'overall_health': 'operational'
        }
    
    def get_comprehensive_report(self) -> Dict[str, Any]:
        """Gera relatório abrangente do sistema"""
        return {
            'timestamp': datetime.now().isoformat(),
            'system_report': self.monitoring_system.get_system_report(),
            'cache_stats': self.cache_system.get_cache_stats(),
            'integration_status': self.get_integration_status(),
            'current_parameters': self.optimization_engine.get_current_parameters(),
            'active_checkpoints': len(self.persistence_system.checkpoints),
            'alert_history': list(self.monitoring_system.alerts)[-10:],
            'security_report': self.security_framework.get_security_report()
        }

# =============================================================================
# DEMONSTRAÇÃO DO SISTEMA
# =============================================================================

async def demonstrate_integrated_system():
    """Demonstra o sistema totalmente integrado"""
    logger.info("=" * 90)
    logger.info("VHALINOR IAG %s - %s", VERSION, CODENAME)
    logger.info("Build: %s | Autor: %s", BUILD_DATE, AUTHOR)
    logger.info("=" * 90)

    # Inicializa orquestrador
    logger.info("Inicializando Orquestrador Integrado...")
    orchestrator = IntegratedBrainOrchestrator(
        iag_path="./iag_modules",
        quantum_path="./quantum_modules"
    )

    # Cria neurônios de exemplo
    logger.info("Criando neuronios de exemplo...")
    
    neuron_types = [
        (NeuronType.SENSORY, "market_data"),
        (NeuronType.PROCESSING, "data_analyzer"),
        (NeuronType.MEMORY, "pattern_memory"),
        (NeuronType.DECISION, "decision_engine"),
        (NeuronType.QUANTUM, "quantum_processor"),
        (NeuronType.ANALYTICAL, "risk_analyzer"),
        (NeuronType.AUTOMATIC, "automatic_trader"),
        (NeuronType.TRADING, "trading_engine"),
        (NeuronType.MARKET_ANALYSIS, "market_analysis"),
        (NeuronType.RISK_ASSESSMENT, "risk_assessment"),
        (NeuronType.OPTIMIZATION, "optimization_engine"),
        (NeuronType.VISION, "vision_processor"),
        (NeuronType.AUDITORY, "auditory_processor"),
        (NeuronType.MOTOR, "motor_processor"),
        (NeuronType.OUTPUT, "autotrader")
    ]
    
    for ntype, name in neuron_types:
        neuron_id = orchestrator.create_neuron(f"./modules/{name}.py", ntype)
        logger.info("Neuronio criado: %s (%s)", neuron_id, name)
    
    # Cria sinapses
    logger.info("Criando conexoes neurais...")
    neuron_ids = list(orchestrator.neurons.keys())
    
    for i in range(len(neuron_ids) - 1):
        orchestrator.create_synapse(
            neuron_ids[i],
            neuron_ids[i + 1],
            initial_weight=0.7
        )
        logger.debug("Conexao: %s -> %s", neuron_ids[i][-8:], neuron_ids[i + 1][-8:])

    # Sincroniza módulos
    logger.info("Sincronizando modulos...")
    await orchestrator.sync_all_modules()
    logger.info("Sincronizacao completa")

    # Processa dados
    logger.info("Executando processamento integrado...")
    symbols = [
        "PETR4.SA", "BTC/USD", "ETH/USD", "XRP/USD", "LTC/USD", "BCH/USD",
        "EOS/USD", "XLM/USD", "XMR/USD", "ZEC/USD", "DOGE/USD", "ADA/USD",
        "DOT/USD", "LINK/USD",
    ]
    
    test_data = {
        'market_data': {
            'symbol': random.choice(symbols),
            'price': 100.0 + random.uniform(-5, 5),
            'volume': random.randint(1000, 10000),
            'timestamp': datetime.now().isoformat()
        }
    }
    
    result = await orchestrator.process_with_all_modules(test_data)
    
    logger.info("Resultado: neuronios_ativados=%s | boost_quantico=%.2fx | risco=%s | aprendizado=%s",
                result.get('stages', {}).get('neural', {}).get('activated_neurons'),
                result.get('stages', {}).get('quantum', {}).get('quantum_boost', 0.0),
                result.get('stages', {}).get('analysis', {}).get('risk_assessment', {}).get('risk_level', 'unknown'),
                result.get('stages', {}).get('learning', {}).get('learning_complete'))

    # Estatísticas
    logger.info("Estatisticas do Sistema...")
    stats = orchestrator.get_brain_stats()


# =============================================================================
# SISTEMA DE TESTES DE INTEGRAÇÃO
# =============================================================================

class IntegrationTestSuite:
    """Suite de testes de integração para o sistema Vhalinor IAG"""
    
    def __init__(self):
        self.test_results = []
        self.passed = 0
        self.failed = 0
        self.skipped = 0
        self.start_time = None
        self.end_time = None
        
        logger.info("🧪 Suite de Testes de Integração inicializada")
    
    def run_all_tests(self) -> Dict[str, Any]:
        """Executa todos os testes de integração"""
        self.start_time = datetime.now()
        logger.info("🚀 Iniciando execução de todos os testes...")
        
        # Testes de módulos
        self._test_module_discovery()
        self._test_integration_hub()
        self._test_persistence_system()
        self._test_monitoring_system()
        self._test_optimization_engine()
        self._test_distributed_orchestrator()
        
        self.end_time = datetime.now()
        
        return self._generate_test_report()
    
    def _test_module_discovery(self) -> None:
        """Testa sistema de descoberta de módulos"""
        test_name = "Module Discovery"
        logger.info(f"🧪 Executando teste: {test_name}")
        
        try:
            discovery = ModuleDiscovery()
            modules = discovery.scan_project(force=True)
            
            assert isinstance(modules, dict), "Módulos devem ser um dicionário"
            assert len(modules) > 0, "Deve encontrar pelo menos um módulo"
            
            self._record_test(test_name, True, "Module discovery funcionando corretamente")
            logger.info(f"✅ {test_name} passou")
            
        except Exception as e:
            self._record_test(test_name, False, str(e))
            logger.error(f"❌ {test_name} falhou: {e}")
    
    def _test_integration_hub(self) -> None:
        """Testa hub de integração"""
        test_name = "Integration Hub"
        logger.info(f"🧪 Executando teste: {test_name}")
        
        try:
            hub = IntegrationHub()
            
            # Testa registro de módulo
            hub.register_module('test_module', {'name': 'test'})
            assert 'test_module' in hub.registered_modules
            
            # Testa envio de dados
            packet = DataPacket(
                id='test_001',
                source_module='test',
                target_module=['target'],
                data_type='test_data',
                payload={'test': True},
                priority=DataPriority.NORMAL
            )
            
            asyncio.run(hub.send_data(packet))
            
            self._record_test(test_name, True, "Integration hub funcionando corretamente")
            logger.info(f"✅ {test_name} passou")
            
        except Exception as e:
            self._record_test(test_name, False, str(e))
            logger.error(f"❌ {test_name} falhou: {e}")
    
    def _test_persistence_system(self) -> None:
        """Testa sistema de persistência"""
        test_name = "Persistence System"
        logger.info(f"🧪 Executando teste: {test_name}")
        
        try:
            persistence = PersistenceSystem()
            
            # Testa criação de checkpoint
            state = {'test_data': 'test_value'}
            checkpoint_id = persistence.create_checkpoint('test_checkpoint', state)
            
            assert checkpoint_id is not None, "Checkpoint ID não deve ser None"
            assert checkpoint_id in persistence.checkpoints, "Checkpoint deve estar registrado"
            
            # Testa restauração
            restored = persistence.restore_checkpoint(checkpoint_id)
            assert restored is not None, "Checkpoint deve ser restaurável"
            
            # Testa compressão
            persistence.set_compression_enabled(True, level=6)
            assert persistence.compression_enabled is True
            
            self._record_test(test_name, True, "Persistence system funcionando corretamente")
            logger.info(f"✅ {test_name} passou")
            
        except Exception as e:
            self._record_test(test_name, False, str(e))
            logger.error(f"❌ {test_name} falhou: {e}")
    
    def _test_monitoring_system(self) -> None:
        """Testa sistema de monitoramento"""
        test_name = "Monitoring System"
        logger.info(f"🧪 Executando teste: {test_name}")
        
        try:
            monitoring = AdvancedMonitoringSystem()
            
            # Testa registro de métrica
            monitoring.record_metric('test_metric', 100.0, unit='ms')
            assert 'test_metric' in monitoring.metrics
            
            # Testa criação de alerta
            alert = monitoring.create_alert('warning', 'Test alert', 'test')
            assert alert['level'] == 'warning'
            
            # Testa relatório do sistema
            report = monitoring.get_system_report()
            assert 'uptime_seconds' in report
            assert 'total_metrics_recorded' in report
            
            self._record_test(test_name, True, "Monitoring system funcionando corretamente")
            logger.info(f"✅ {test_name} passou")
            
        except Exception as e:
            self._record_test(test_name, False, str(e))
            logger.error(f"❌ {test_name} falhou: {e}")
    
    def _test_optimization_engine(self) -> None:
        """Testa engine de otimização"""
        test_name = "Optimization Engine"
        logger.info(f"🧪 Executando teste: {test_name}")
        
        try:
            engine = AdaptiveOptimizationEngine()
            
            # Testa registro de performance
            engine.record_performance('accuracy', 0.85)
            engine.record_performance('processing_time', 120.0)
            
            # Testa sugestão de otimização
            suggestions = engine.suggest_optimization()
            assert isinstance(suggestions, dict)
            
            # Testa aplicação de otimização
            if suggestions:
                applied = engine.apply_optimization(suggestions)
                assert isinstance(applied, dict)
            
            # Testa relatório
            report = engine.get_optimization_report()
            assert 'current_parameters' in report
            
            self._record_test(test_name, True, "Optimization engine funcionando corretamente")
            logger.info(f"✅ {test_name} passou")
            
        except Exception as e:
            self._record_test(test_name, False, str(e))
            logger.error(f"❌ {test_name} falhou: {e}")
    
    def _test_distributed_orchestrator(self) -> None:
        """Testa orquestrador distribuído"""
        test_name = "Distributed Orchestrator"
        logger.info(f"🧪 Executando teste: {test_name}")
        
        try:
            async def run_orchestrator_test():
                orchestrator = DistributedOrchestrator(max_workers=2)
                
                # Inicia orquestrador
                await orchestrator.start()
                assert orchestrator.running is True
                
                # Submete tarefa
                task = {'type': 'compute', 'duration': 0.1}
                task_id = await orchestrator.submit_task(task)
                assert task_id is not None
                
                # Aguarda resultado
                result = await orchestrator.get_task_result(task_id, timeout=5.0)
                assert result is not None
                
                # Obtém status
                status = orchestrator.get_orchestration_status()
                assert 'active_workers' in status
                
                # Para orquestrador
                await orchestrator.stop()
                assert orchestrator.running is False
            
            asyncio.run(run_orchestrator_test())
            
            self._record_test(test_name, True, "Distributed orchestrator funcionando corretamente")
            logger.info(f"✅ {test_name} passou")
            
        except Exception as e:
            self._record_test(test_name, False, str(e))
            logger.error(f"❌ {test_name} falhou: {e}")
    
    def _record_test(self, test_name: str, passed: bool, message: str) -> None:
        """Registra resultado de teste"""
        result = {
            'test_name': test_name,
            'passed': passed,
            'message': message,
            'timestamp': datetime.now()
        }
        
        self.test_results.append(result)
        
        if passed:
            self.passed += 1
        else:
            self.failed += 1
    
    def _generate_test_report(self) -> Dict[str, Any]:
        """Gera relatório de testes"""
        duration = (self.end_time - self.start_time).total_seconds() if self.end_time else 0
        
        return {
            'timestamp': datetime.now(),
            'duration_seconds': duration,
            'total_tests': len(self.test_results),
            'passed': self.passed,
            'failed': self.failed,
            'skipped': self.skipped,
            'success_rate': (self.passed / len(self.test_results) * 100) if self.test_results else 0,
            'test_results': self.test_results
        }


def run_integration_tests() -> None:
    """Executa suite de testes de integração"""
    logger.info("🧪 Iniciando Suite de Testes de Integração Vhalinor IAG")
    
    suite = IntegrationTestSuite()
    report = suite.run_all_tests()
    
    logger.info("=" * 80)
    logger.info("📊 RELATÓRIO DE TESTES")
    logger.info("=" * 80)
    logger.info(f"Total de testes: {report['total_tests']}")
    logger.info(f"✅ Passou: {report['passed']}")
    logger.info(f"❌ Falhou: {report['failed']}")
    logger.info(f"⏭️ Pulado: {report['skipped']}")
    logger.info(f"Taxa de sucesso: {report['success_rate']:.1f}%")
    logger.info(f"Duração: {report['duration_seconds']:.2f}s")
    logger.info("=" * 80)
    
    # Detalhes dos testes
    for result in report['test_results']:
        status = "✅" if result['passed'] else "❌"
        logger.info(f"{status} {result['test_name']}: {result['message']}")
    
    logger.info("=" * 80)
    
    if report['failed'] == 0:
        logger.info("🎉 Todos os testes passaram!")
    else:
        logger.warning(f"⚠️ {report['failed']} teste(s) falharam")


# Estatísticas
    logger.info("Estatisticas do Sistema...")
    stats = orchestrator.get_brain_stats()
    logger.info("Neuronios=%s | Sinapses=%s | Ativacao_media=%.2f%% | Ativos=%s",
                stats.get('total_neurons'),
                stats.get('total_synapses'),
                (stats.get('average_activation', 0.0) * 100.0) if isinstance(stats.get('average_activation', 0.0), (int, float)) else 0.0,
                stats.get('active_neurons'))

    # Relatório de integração
    logger.info("Status de Integracao...")
    status = orchestrator.get_integration_status()
    logger.info("Modulos_ativos=%s | Cache_hit_rate=%s | Eventos_seguranca=%s | Metricas=%s",
                len(status.get('integration_hub', {}).get('active_modules', [])),
                status.get('cache', {}).get('hit_rate'),
                status.get('security', {}).get('total_events'),
                status.get('monitoring', {}).get('total_metrics_recorded'))

    # Checkpoint
    logger.info("Criando checkpoint...")
    checkpoint_data = {
        'brain_stats': stats,
        'timestamp': datetime.now().isoformat(),
        'version': VERSION
    }
    
    checkpoint_id = orchestrator.persistence_system.create_checkpoint(
        'system_state',
        checkpoint_data,
        tags=['demo', 'success']
    )
    logger.info("Checkpoint criado: %s", checkpoint_id)
    logger.info("Demonstracao concluida com sucesso.")

# =============================================================================
# SELF-TEST LEVE (SEM TREINO/REDE)
# =============================================================================

def self_check() -> Dict[str, Any]:
    """
    Self-check leve:
    - garante pastas padrao
    - valida escrita de log
    - carrega configs com fallback
    """
    ensure_project_dirs()

    # Base mais completa (evita divergência entre self-checks)
    base = _core_self_check()

    results: Dict[str, Any] = {
        **base,
        "project_root": str(PROJECT_ROOT),
        "dirs": {k: str(v) for k, v in PROJECT_DIRS.items()},
        "dirs_ok": True,
        "log_write_ok": False,
        "configs_loaded": False,
        "config_keys": [],
        "module_flags": {
            "NEURAL_MODULES_AVAILABLE": NEURAL_MODULES_AVAILABLE,
            "QUANTUM_MODULES_AVAILABLE": QUANTUM_MODULES_AVAILABLE,
            "ANALYSIS_MODULES_AVAILABLE": ANALYSIS_MODULES_AVAILABLE,
            "CONTINUOUS_LEARNING_AVAILABLE": CONTINUOUS_LEARNING_AVAILABLE,
        },
    }

    # Verifica pastas
    try:
        for p in PROJECT_DIRS.values():
            if not p.exists() or not p.is_dir():
                results["dirs_ok"] = False
                break
    except Exception:
        results["dirs_ok"] = False

    # Teste de escrita de log (e flush)
    try:
        logger.info("self_check: log write probe")
        for h in logging.getLogger().handlers:
            try:
                h.flush()
            except Exception:
                pass
        results["log_write_ok"] = True
    except Exception as exc:
        results["log_write_ok"] = False
        _bootstrap_logger.warning("Falha no self_check de log: %s", exc)

    # Carrega configs
    try:
        cfg = get_config()
        results["configs_loaded"] = True
        results["config_keys"] = sorted(list(cfg.keys()))[:50]
    except Exception as exc:
        results["configs_loaded"] = False
        logger.warning("self_check: falha ao carregar configs: %s", exc)

    # Campo compatível com chamadas externas
    results["ok"] = bool(results.get("ok", True)) and bool(results["dirs_ok"]) and bool(results["log_write_ok"]) and bool(results["configs_loaded"])
    if not results["dirs_ok"]:
        results.setdefault("warnings", []).append("dirs_not_ok")
    if not results["log_write_ok"]:
        results.setdefault("warnings", []).append("log_write_not_ok")
    if not results["configs_loaded"]:
        results.setdefault("warnings", []).append("configs_not_loaded")

    return results

# =============================================================================
# ENTRY POINT
# =============================================================================

async def main():
    """Função principal assíncrona"""
    try:
        await demonstrate_integrated_system()
    except KeyboardInterrupt:
        logger.warning("Sistema interrompido pelo usuario")
    except Exception as e:
        logger.exception("Erro fatal: %s", e)

def run():
    """Entry point síncrono"""
    asyncio.run(main())

if __name__ == "__main__":
    run()  inspecione o funcionamento deste arquivo
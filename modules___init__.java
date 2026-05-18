"""
VHALINOR TRADER - Ultimate Modules Package v6.0
===============================================
Ultimate modules for the VHALINOR trading system.

This package contains all Ultimate modules implementing the 11-step plan:
- Ultimate Data fetching and real-time processing
- Ultimate AI/ML analysis and prediction
- Ultimate Neural network management
- Ultimate Trading execution and automation
- Ultimate Risk management
- Ultimate Technical and sentiment analysis
- Ultimate Monitoring and dashboards

@module UltimateModules
@version 6.0.0
@date April 2026
"""

from .UltimateDataFetcher import (
    UltimateDataFetcher,
    RealTimeDataStream,
    WebSocketManager,
    CCXTIntegration,
)

from .UltimateAIAnalyzer import (
    UltimateAIAnalyzer,
    VectorStore,
    CognitiveMemorySystem,
    ChainOfThoughtEngine,
    AnalysisType,
    MemoryType,
    TextAnalysisResult,
)

from .UltimatePredictor import (
    UltimatePredictor,
    UltimateEnsemblePredictor,
    UltimateBacktestEngine,
    PredictionResult,
    BacktestResult,
    WalkForwardResult,
    ModelArchitecture,
    EnsembleMethod,
    LSTMAttentionModel,
)
from .neural_network import NeuralNetworkManager, QuantumNeuralNetwork
from .automation import TradingAutomation, WebAutomation
from .blockchain import BlockchainManager, Web3Manager
from .dashboard import EnhancedDashboard
from .deep_learning_trader import (
    DeepLearningTrader,
    TradingTimeSeriesDataset,
    LSTMWithAttention,
    TransformerTrader,
)

__all__ = [
    # Ultimate Data Fetching
    "UltimateDataFetcher",
    "RealTimeDataStream",
    "WebSocketManager",
    "CCXTIntegration",
    
    # Ultimate AI Analysis
    "UltimateAIAnalyzer",
    "VectorStore",
    "CognitiveMemorySystem",
    "ChainOfThoughtEngine",
    "AnalysisType",
    "MemoryType",
    "TextAnalysisResult",
    
    # Ultimate Prediction
    "UltimatePredictor",
    "UltimateEnsemblePredictor",
    "UltimateBacktestEngine",
    "PredictionResult",
    "BacktestResult",
    "WalkForwardResult",
    "ModelArchitecture",
    "EnsembleMethod",
    "LSTMAttentionModel",
    
    # Legacy modules (kept for compatibility)
    "DataFetcher",
    "RealTimeDataFetcher",
    "AIAnalyzer",
    "CognitiveAnalyzer",
    "Predictor",
    "EnsemblePredictor",
    
    # Neural Networks
    "NeuralNetworkManager",
    "QuantumNeuralNetwork",
    
    # Automation
    "TradingAutomation",
    "WebAutomation",
    
    # Blockchain
    "BlockchainManager",
    "Web3Manager",
    
    # Dashboard
    "EnhancedDashboard",
    
    # Deep Learning
    "DeepLearningTrader",
    "TradingTimeSeriesDataset",
    "LSTMWithAttention",
    "TransformerTrader",
]

__version__ = "6.0.0"
__author__ = "VHALINOR Team"
__date__ = "April 2026"

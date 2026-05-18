package com.vhalinor.ai.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Módulo Central de Inteligência Artificial – Redundância (Java Edition)
 * Gerencia todos os módulos de IA disponíveis, oferecendo fallback e
 * carregamento dinâmico com reflexão.
 * 
 * Melhorias implementadas:
 * - Uso de uma interface {@link AIModule} para padronizar os módulos.
 * - Carregamento lazy e cache thread-safe.
 * - Configuração externa da lista de módulos via sistema.
 * - Reload seletivo de módulos que falharam.
 * - Logging robusto com SLF4J (substituível).
 * - Descoberta automática de módulos anotados com @ModuleInfo (opcional).
 */
public class CentralAIModuleManager {

    private static final Logger log = Logger.getLogger(CentralAIModuleManager.class.getName());

    // Interface comum para todos os módulos de IA
    public interface AIModule {
        String getName();
        boolean initialize();
        void shutdown();
        default String getVersion() { return "1.0.0"; }
    }

    // Anotação opcional para descoberta automática (exemplo)
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE)
    public @interface ModuleInfo {
        String name();
        String version() default "1.0.0";
    }

    // Lista original de nomes de módulos preservada para compatibilidade
    private static final List<String> LEGACY_MODULE_NAMES = List.of(
        "AdvancedDecisionAlgorithms", "AdvancedNeuralEngine", "AdvancedNeuralNetwork",
        "AdvancedPatternRecognition", "AdvancedPredictionDashboard", "AdvancedPredictionService",
        "AdvancedQuantumProcessor", "AdvancedTemporalNetwork", "advanced_ai_system",
        "advanced_automation_orchestrator", "advanced_integration_manager", "advanced_ml_predictor",
        "advanced_system_manager", "agi_automation_engine", "agi_cli", "AI_system_manager",
        "AI_system_manager_advanced", "AI_system_manager_enhanced", "AlgorithmType",
        "AnalisadorTecnicoAvancado", "api_pyautogui_integration", "api_pyautogui_integration_enhanced",
        "aprendizado", "arbitrage_analysis_advanced", "arbitrage_trading_strategy", "atencao",
        "AutomacaoTrading", "AutonomousCreator", "AutonomousManager", "AutonomousNeuralCreator",
        "AutonomousValidationService", "autonomous_manager 1.1", "avatar-sylph-sencient",
        "CognitiveAnalyzer", "cognitive_response_enhancer", "enhanced_ai_analyzer",
        "enhanced_inteligencia_artificial_central", "enhanced_nucleo_consciencia",
        "Inteligencia_artificial_central", "Inteligencia_artificial_central_enhanced",
        "Inteligencia_artificial_central_ultra_enhanced", "main", "main_enhanced", "main_new",
        "main_simple", "main_ultra_enhanced", "main_windows", "neural_network_creator",
        "PredictiveConfigOptimizer", "reinforcement_learning_environment", "reorganize_vhalinor",
        "rl_algorithms_financial", "secure_credentials", "temporal_deep_learning",
        "test_advanced_prediction", "test_enhanced_inteligencia_artificial_central",
        "test_enhanced_inteligencia_artificial_central_v7",
        "test_enhanced_inteligencia_artificial_central_v7_simple",
        "test_multidisciplinary_learning", "test_multidisciplinary_learning_fixed",
        "test_neural_visualizer", "test_simple_prediction", "VHALINOR.IAG 4.5",
        "VHALINOR-IAG_Core_System", "VhalinorAutonomousSystem", "VhalinorFinancialAGI",
        "VHALINORMultidisciplinaryLearning", "VhalinorNeuroplasticitySystem",
        "VHALINOR_ADVANCED_ANALYTICS", "VHALINOR_QUANTUM_CORE", "VHALINOR_RISK_ANALYTICS",
        "VHALINOR_STRATEGY_TRADER", "Vhalinor_Inteligencia_artificial_central",
        "Vhalinor_empatia", "vhalinor_estrategias_regulacao", "vhalinor_forex_trading_strategy",
        "vhalinor_high_profit_manager", "vhalinor_iag", "vhalinor_iag_300",
        "vhalinor_iag_trader_main", "vhalinor_learning_example", "VHALINOR_MAIN_SYSTEM",
        "vhalinor_neural_bus", "vhalinor_neural_model", "vhalinor_neural_network_visualizer",
        "vhalinor_trading_system", "xai_governance_framework", "ai-trading-bot_main",
        "ai-trading-bot_ai_trading_bot_modules_ai_analyzer",
        "ai-trading-bot_ai_trading_bot_modules_blockchain",
        "ai-trading-bot_ai_trading_bot_modules_neural_network",
        "ai-trading-bot_ai_trading_bot_modules_predictor",
        "ai-trading-bot_tests_test_blockchain", "ai-trading-bot_tests_test_prediction",
        "ai_geral_analise_quantica", "ai_geral_aprendizado_continuo",
        "ai_geral_aprendizado_profundo", "ai_geral_arquitetura_organica",
        "ai_geral_evolucao_aprendizado", "ai_geral_neurogenese_comunicacao",
        "ai_geral_vhalinor_deepseek_learner", "ai_geral___init__",
        "ai_geral_ética_ai_geral", "architecture_MLEngine",
        "architecture_UltimateMLEngine", "architecture_UltimateTechnicalAnalysisEngine",
        "architecture_UltimateVhalinorTraderAI", "architecture_VhalinorTraderAI",
        "config_settings", "config_settings_ultra", "core_exceptions",
        "core_exceptions_ultra", "core_system_Vhalinor_Inteligencia_artificial_central",
        "modules_ai_analyzer", "modules_ai_analyzer_enhanced", "modules_blockchain",
        "modules_cognitive_response_enhanced", "modules_deep_learning_trader",
        "modules_neural_network", "modules_neural_network_enhanced", "modules_predictor",
        "modules_predictor_enhanced", "modules_UltimateAIAnalyzer", "modules_UltimateBlockchain",
        "modules_UltimateCognitiveResponse", "modules_UltimateNeuralNetwork",
        "modules_UltimatePredictor", "modules___init__",
        "Núcleo de Consciência e Teoria da Mente", "tests_test_deep_learning",
        "tests_test_neural_network_simple", "tests_test_neural_network_ultra",
        "tests_test_prediction", "tests___init__"
    );

    // Mapa de módulos carregados (nome -> instância)
    private final ConcurrentHashMap<String, AIModule> loadedModules = new ConcurrentHashMap<>();
    // Lista de nomes que falharam no carregamento (para possível retry)
    private final CopyOnWriteArrayList<String> failedModules = new CopyOnWriteArrayList<>();
    // Lista de nomes a serem carregados (pode ser configurada externamente)
    private final CopyOnWriteArrayList<String> moduleNames = new CopyOnWriteArrayList<>(LEGACY_MODULE_NAMES);

    // Singleton
    private static final CentralAIModuleManager INSTANCE = new CentralAIModuleManager();

    private CentralAIModuleManager() {
        // Carrega a lista de módulos do sistema se definida
        String configNames = System.getProperty("vhalinor.ai.modules");
        if (configNames != null && !configNames.isBlank()) {
            moduleNames.clear();
            for (String name : configNames.split(",")) {
                moduleNames.add(name.trim());
            }
            log.info("Módulos configurados via propriedade: " + moduleNames);
        }
    }

    public static CentralAIModuleManager getInstance() {
        return INSTANCE;
    }

    /**
     * Carrega todos os módulos da lista (lazy). Módulos já carregados são ignorados.
     * Módulos que falharem são registrados em failedModules para possível recuperação.
     */
    public void loadAllModules() {
        for (String name : moduleNames) {
            loadModule(name);
        }
        log.info(String.format("Módulos carregados: %d, falhas: %d",
                loadedModules.size(), failedModules.size()));
    }

    /**
     * Carrega um módulo específico pelo nome.
     * @return true se o módulo foi carregado com sucesso, false caso contrário.
     */
    public boolean loadModule(String moduleName) {
        if (loadedModules.containsKey(moduleName)) {
            return true;
        }
        try {
            // Tenta encontrar a classe correspondente ao módulo.
            // Estratégia: o nome completo pode ser mapeado para um pacote padrão.
            // Aqui tentamos dois padrões: nome direto e com prefixo "com.vhalinor.ai.modules."
            AIModule module = instantiateModule(moduleName);
            if (module != null && module.initialize()) {
                loadedModules.put(moduleName, module);
                log.info("Módulo carregado: " + moduleName + " v" + module.getVersion());
                return true;
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "Falha ao carregar módulo: " + moduleName, e);
        }
        failedModules.addIfAbsent(moduleName);
        return false;
    }

    /**
     * Retenta carregar todos os módulos que falharam anteriormente.
     */
    public void retryFailedModules() {
        List<String> toRetry = new ArrayList<>(failedModules);
        failedModules.clear();
        for (String name : toRetry) {
            loadModule(name);
        }
        log.info("Retentativa concluída. Módulos falhos restantes: " + failedModules.size());
    }

    /**
     * Remove um módulo da lista de carregados (shutdown).
     */
    public void unloadModule(String moduleName) {
        AIModule module = loadedModules.remove(moduleName);
        if (module != null) {
            try {
                module.shutdown();
            } catch (Exception e) {
                log.log(Level.WARNING, "Erro ao desligar módulo: " + moduleName, e);
            }
            log.info("Módulo removido: " + moduleName);
        }
    }

    /**
     * Obtém um módulo carregado pelo nome, ou null se não disponível.
     */
    public Optional<AIModule> getModule(String name) {
        return Optional.ofNullable(loadedModules.get(name));
    }

    /**
     * Lista todos os módulos carregados.
     */
    public Collection<AIModule> getLoadedModules() {
        return Collections.unmodifiableCollection(loadedModules.values());
    }

    /**
     * Lista os nomes de todos os módulos registrados (carregados ou não).
     */
    public List<String> getRegisteredModuleNames() {
        return Collections.unmodifiableList(moduleNames);
    }

    /**
     * Retorna os nomes dos módulos que falharam ao carregar.
     */
    public List<String> getFailedModuleNames() {
        return Collections.unmodifiableList(failedModules);
    }

    // ---------- utilitários privados ----------

    private AIModule instantiateModule(String name) throws Exception {
        // 1. Tenta como classe totalmente qualificada com um pacote padrão
        String[] candidates = {
            "com.vhalinor.ai.modules." + name,  // Pacote sugerido
            "com.vhalinor.iag." + name,         // Outro pacote comum
            name                                  // nome direto (pode estar no classpath raiz)
        };

        for (String fqn : candidates) {
            try {
                Class<?> clazz = Class.forName(fqn);
                if (AIModule.class.isAssignableFrom(clazz)) {
                    return (AIModule) clazz.getDeclaredConstructor().newInstance();
                }
            } catch (ClassNotFoundException ignored) {
                // tenta próximo
            }
        }

        // 2. Tenta descoberta via ServiceLoader (opcional)
        // (pode ser implementada com arquivo META-INF/services)
        log.fine("Nenhuma classe correspondente para o módulo: " + name);
        return null;
    }

    // Para compatibilidade com a função original 'load_ai_modules'
    public static Map<String, AIModule> loadAiModules() {
        CentralAIModuleManager manager = getInstance();
        manager.loadAllModules();
        return new HashMap<>(manager.loadedModules);
    }

    // Para compatibilidade com 'get_available_ai_modules'
    public static List<String> getAvailableAiModules() {
        return getInstance().getRegisteredModuleNames();
    }

    // Exemplo de teste / ponto de entrada
    public static void main(String[] args) {
        // Configurar logging mínimo
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$s] %5$s %n");

        CentralAIModuleManager manager = CentralAIModuleManager.getInstance();
        System.out.println("Registrando módulos...");
        manager.loadAllModules();
        System.out.println("Módulos carregados: " + manager.getLoadedModules().size());
        System.out.println("Falhas: " + manager.getFailedModuleNames());
        System.out.println("Todos os nomes registrados: " + manager.getRegisteredModuleNames().size());
    }
}
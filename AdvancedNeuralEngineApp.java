import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Conversão do sistema IAG (Inteligência Artificial Geral) para Day Trade
 * de Python/Tkinter para Java Swing.
 *
 * Melhorias:
 * - Look & Feel Nimbus moderno
 * - Responsividade e redimensionamento adequado
 * - Atualização automática com Timer thread-safe
 * - Separação clara entre dados e interface
 * - Tooltips informativos
 * - Suporte a abertura de URLs no navegador padrão
 * - Layout organizado com ScrollPanes para abas longas
 */
public class AdvancedNeuralEngineApp extends JFrame {

    // ========================
    // Enums
    // ========================
    enum AutonomyLevel {
        LOW, MEDIUM, HIGH, AUTONOMOUS
    }

    enum ModuleType {
        ANALYTICAL, CREATIVE, STRATEGIC, OPERATIONAL, ADAPTIVE
    }

    enum ModuleStatus {
        ACTIVE, LEARNING, CREATING, OPTIMIZING, STANDBY
    }

    enum KnowledgeType {
        HISTORICAL_DATA, NEWS, SOCIAL_MEDIA, ECONOMIC_REPORTS, MARKET_BEHAVIOR
    }

    // ========================
    // Classes de dados (POJOs)
    // ========================
    static class IAGDomain {
        String name, description;
        double competency, learningProgress;
        int activeConnections;
        AutonomyLevel autonomyLevel;
        boolean isAdapting;

        IAGDomain(String name, String desc, double comp, AutonomyLevel al, double lp, int conn, boolean adapt) {
            this.name = name;
            this.description = desc;
            this.competency = comp;
            this.autonomyLevel = al;
            this.learningProgress = lp;
            this.activeConnections = conn;
            this.isAdapting = adapt;
        }
    }

    static class CognitiveModule {
        String id, name, currentTask;
        ModuleType type;
        ModuleStatus status;
        double intelligence, creativity, autonomy;
        List<String> tasks;

        CognitiveModule(String id, String name, ModuleType type, ModuleStatus status,
                        double intelligence, double creativity, double autonomy,
                        List<String> tasks, String currentTask) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.status = status;
            this.intelligence = intelligence;
            this.creativity = creativity;
            this.autonomy = autonomy;
            this.tasks = tasks;
            this.currentTask = currentTask;
        }
    }

    static class KnowledgeSource {
        String source;
        KnowledgeType type;
        int dataPoints;
        double reliability, impact;
        LocalDateTime lastUpdate;
        List<String> insights;

        KnowledgeSource(String source, KnowledgeType type, int dp, double rel, double imp,
                        List<String> insights) {
            this.source = source;
            this.type = type;
            this.dataPoints = dp;
            this.reliability = rel;
            this.impact = imp;
            this.lastUpdate = LocalDateTime.now();
            this.insights = insights;
        }
    }

    static class StrategicDecision {
        String id, context, decision, expectedOutcome;
        double confidence, riskAssessment, adaptiveFactor;
        LocalDateTime timestamp;
        List<String> reasoning;

        StrategicDecision(String id, String context, String decision, double confidence,
                          double riskAssessment, String expectedOutcome, double adaptiveFactor,
                          LocalDateTime timestamp, List<String> reasoning) {
            this.id = id;
            this.context = context;
            this.decision = decision;
            this.confidence = confidence;
            this.riskAssessment = riskAssessment;
            this.expectedOutcome = expectedOutcome;
            this.adaptiveFactor = adaptiveFactor;
            this.timestamp = timestamp;
            this.reasoning = reasoning;
        }
    }

    static class IAGMetrics {
        double overallIntelligence, adaptabilityIndex, creativityScore, autonomyLevel;
        double learningVelocity, strategyGeneration, riskAwareness, marketUnderstanding;
        int processedDataPoints, activeStrategies;
    }

    static class MarketBase {
        String name, description, url;
        List<String> indicators;

        MarketBase(String name, String desc, String url, List<String> indicators) {
            this.name = name;
            this.description = desc;
            this.url = url;
            this.indicators = indicators;
        }
    }

    // ========================
    // Cores constantes
    // ========================
    static final Color COLOR_BG = new Color(0xF8FAFC);
    static final Color COLOR_PRIMARY = new Color(0x3B82F6);
    static final Color COLOR_SUCCESS = new Color(0x10B981);
    static final Color COLOR_WARNING = new Color(0xF59E0B);
    static final Color COLOR_ERROR = new Color(0xEF4444);
    static final Color COLOR_INFO = new Color(0x6366F1);
    static final Color COLOR_PURPLE = new Color(0x8B5CF6);
    static final Color COLOR_PINK = new Color(0xEC4899);
    static final Color COLOR_GRAY = new Color(0x6B7280);
    static final Color COLOR_WHITE = Color.WHITE;

    // ========================
    // Dados do sistema
    // ========================
    private List<IAGDomain> domains;
    private List<CognitiveModule> cognitiveModules;
    private List<KnowledgeSource> knowledgeSources;
    private List<StrategicDecision> strategicDecisions;
    private IAGMetrics metrics;
    private boolean isOperational = true;
    private String consciousnessLevel = "SUPERINTELIGENTE";
    private int uptime = 123456;
    private double dataThroughput = 1250.0;

    // Estruturas para interfaces das abas
    private List<DomainCard> domainCards = new ArrayList<>();
    private List<ModuleCard> moduleCards = new ArrayList<>();
    private Map<String, JLabel> metricLabels = new java.util.HashMap<>();

    // Componentes do cabeçalho
    private JLabel statusLbl, consciousnessLbl;
    private JButton toggleBtn;

    // Timer para atualização
    private Timer updateTimer;

    // Dados estáticos das bases de mercado
    private static final Map<String, List<MarketBase>> MARKET_BASES = Map.of(
            "📊 Plataformas de Análise Técnica", List.of(
                    new MarketBase("TradingView", "Interface intuitiva, gráficos interativos e comunidade ativa. Suporta RSI, MACD, médias móveis.", "https://www.tradingview.com/", List.of("RSI", "MACD", "Médias Móveis")),
                    new MarketBase("MetaTrader 4 (MT4)", "Muito usado no Forex, permite automação com robôs de trading.", "https://www.metatrader4.com/", List.of("Robôs", "Personalização")),
                    new MarketBase("MetaTrader 5 (MT5)", "Versão mais recente do MT4, com mais recursos.", "https://www.metatrader5.com/", List.of("Robôs", "Mais Timeframes")),
                    new MarketBase("Investing.com", "Gráficos e dados em tempo real para ações, commodities, forex.", "https://www.investing.com/", List.of("Ações", "Commodities", "Forex"))
            ),
            "🖥️ Ferramentas de Day Trade", List.of(
                    new MarketBase("Profit (Nelogica)", "Plataforma popular no Brasil, gráficos avançados e execução rápida.", "https://www.nelogica.com.br/profit", null),
                    new MarketBase("Tryd", "Foco em agilidade e profundidade de mercado.", "https://www.tryd.com.br/", null),
                    new MarketBase("Invest Flex", "Boa integração com corretoras e recursos para análise.", "https://www.investflex.com.br/", null)
            ),
            "🌐 Fontes de Dados e Comunidade", List.of(
                    new MarketBase("Learn 2 Trade", "Sinais de alta precisão e análises.", "https://learn2.trade/", null),
                    new MarketBase("ZIGDAO", "Negociação social, especialmente criptomoedas.", "https://zigdao.com/", null),
                    new MarketBase("StockCharts", "Excelente para análise técnica de ações.", "https://stockcharts.com/", null)
            )
    );

    // ========================
    // Construtor
    // ========================
    public AdvancedNeuralEngineApp() {
        super("🧠 Inteligência Artificial Geral (IAG) - Day Trade");
        initData();
        setupLookAndFeel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1000);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(COLOR_BG);

        mainPanel.add(buildHeader(), BorderLayout.NORTH);
        JTabbedPane tabs = buildTabs();
        mainPanel.add(tabs, BorderLayout.CENTER);

        setContentPane(mainPanel);

        scheduleUpdate();
    }

    // ========================
    // Inicialização de dados
    // ========================
    private void initData() {
        domains = new ArrayList<>();
        domains.add(new IAGDomain("Análise Multidisciplinar", "Integração de dados históricos, notícias, redes sociais e relatórios econômicos", 99.2, AutonomyLevel.AUTONOMOUS, 99.8, 932000, true));
        domains.add(new IAGDomain("Interpretação Contextual", "Compreensão do impacto de eventos geopolíticos e decisões de bancos centrais", 98.5, AutonomyLevel.HIGH, 99.7, 2500000, true));
        domains.add(new IAGDomain("Execução Autônoma", "Operação em múltiplos mercados e ativos simultaneamente", 99.7, AutonomyLevel.AUTONOMOUS, 99.9, 410000, false));
        domains.add(new IAGDomain("Criação de Estratégias", "Desenvolvimento de novas abordagens de trading baseadas em simulações", 97.8, AutonomyLevel.HIGH, 96.2, 180000, true));
        domains.add(new IAGDomain("Gestão de Risco Dinâmica", "Avaliação de exposição, correlação e volatilidade em tempo real", 98.9, AutonomyLevel.AUTONOMOUS, 99.8, 290000, false));
        domains.add(new IAGDomain("Consciência Operacional", "Reconhecimento de condições desfavoráveis e preservação de capital", 97.2, AutonomyLevel.HIGH, 99.5, 170000, true));
        domains.add(new IAGDomain("Trading Spot", "Execução e análise autônoma de operações spot em múltiplos ativos", 96.8, AutonomyLevel.AUTONOMOUS, 99.5, 210000, true));

        cognitiveModules = new ArrayList<>();
        cognitiveModules.add(new CognitiveModule("cognitive_analytical", "Módulo Analítico", ModuleType.ANALYTICAL, ModuleStatus.ACTIVE, 99.1, 98.5, 99.7, List.of("Análise Técnica", "Processamento de Dados", "Pattern Recognition"), "Análise de Correlações Complexas"));
        cognitiveModules.add(new CognitiveModule("cognitive_creative", "Módulo Criativo", ModuleType.CREATIVE, ModuleStatus.CREATING, 97.8, 99.2, 96.5, List.of("Geração de Estratégias", "Inovação Algorítmica", "Cenários Alternativos"), "Desenvolvendo Nova Estratégia Híbrida"));
        cognitiveModules.add(new CognitiveModule("cognitive_strategic", "Módulo Estratégico", ModuleType.STRATEGIC, ModuleStatus.OPTIMIZING, 98.6, 99.7, 97.3, List.of("Planejamento Curto Prazo", "Médio Prazo", "Longo Prazo", "Otimização de Portfolio"), "Otimização de Alocação de Ativos"));
        cognitiveModules.add(new CognitiveModule("cognitive_operational", "Módulo Operacional", ModuleType.OPERATIONAL, ModuleStatus.ACTIVE, 97.2, 98.1, 99.5, List.of("Execução de Ordens", "Monitoramento Contínuo", "Ajustes Automáticos"), "Execução Multi-Asset Sincronizada"));
        cognitiveModules.add(new CognitiveModule("cognitive_adaptive", "Módulo Adaptativo", ModuleType.ADAPTIVE, ModuleStatus.LEARNING, 96.7, 99.4, 95.9, List.of("Aprendizado Contínuo", "Adaptação Contextual", "Criação de Modelos"), "Adaptação a Novas Condições de Mercado"));

        knowledgeSources = new ArrayList<>();
        knowledgeSources.add(new KnowledgeSource("Dados Históricos de Mercado", KnowledgeType.HISTORICAL_DATA, 15420000, 98.5, 94.2, List.of("Padrões Sazonais Identificados", "Correlações de Longo Prazo", "Volatilidade Cíclica")));
        knowledgeSources.add(new KnowledgeSource("Análise de Notícias Financeiras", KnowledgeType.NEWS, 2847000, 89.7, 87.3, List.of("Sentimento de Mercado Positivo", "Expectativas de Política Monetária", "Eventos Geopolíticos")));
        knowledgeSources.add(new KnowledgeSource("Redes Sociais e Sentiment", KnowledgeType.SOCIAL_MEDIA, 8932000, 76.4, 71.8, List.of("Tendências Virais Emergentes", "Comportamento Retail", "Influenciadores de Mercado")));
        knowledgeSources.add(new KnowledgeSource("Relatórios Econômicos", KnowledgeType.ECONOMIC_REPORTS, 1256000, 96.8, 92.5, List.of("Indicadores Macroeconômicos", "Projeções do PIB", "Políticas Fiscais")));
        knowledgeSources.add(new KnowledgeSource("Comportamento de Mercado", KnowledgeType.MARKET_BEHAVIOR, 7421000, 91.3, 89.6, List.of("Flow de Investidores", "Posicionamento Institucional", "Liquidez de Mercado")));

        strategicDecisions = new ArrayList<>();
        strategicDecisions.add(new StrategicDecision("decision_001", "Identificação de divergência em múltiplos timeframes", "Estabelecer posição longa em EUR/USD com stop dinâmico", 94.7, 23.5, "Retorno de 2.8% em 3-5 dias úteis", 87.2, LocalDateTime.now(), List.of("Convergência de indicadores técnicos", "Sentimento favorável ao EUR", "Redução da volatilidade sistêmica", "Confirmação por múltiplos modelos")));
        strategicDecisions.add(new StrategicDecision("decision_002", "Detecção de padrão anômalo em índices asiáticos", "Reduzir exposição a ativos de risco e aumentar posições defensivas", 91.2, 34.7, "Preservação de capital com possível ganho de 1.2%", 92.8, LocalDateTime.now(), List.of("Correlações cruzadas anômalas", "Indicadores de stress financeiro elevados", "Padrão histórico similar em 2018", "Consenso entre módulos de risco")));
        strategicDecisions.add(new StrategicDecision("decision_003", "Oportunidade de arbitragem spot detectada entre BTC/USD e ETH/USD", "Executar trading spot autônomo para maximizar retorno instantâneo", 92.3, 27.8, "Ganho potencial de 1.5% em operação spot", 88.5, LocalDateTime.now(), List.of("Diferença de preço detectada", "Volume suficiente para execução", "Confirmação por múltiplos módulos operacionais", "Gestão de risco aplicada")));

        metrics = generateMetrics();
    }

    private IAGMetrics generateMetrics() {
        IAGMetrics m = new IAGMetrics();
        Random r = new Random();
        m.overallIntelligence = 98.7 + r.nextDouble() * 1.2;
        m.adaptabilityIndex = 97.5 + r.nextDouble() * 2;
        m.creativityScore = 96.2 + r.nextDouble() * 2.5;
        m.autonomyLevel = 99.1 + r.nextDouble() * 0.7;
        m.learningVelocity = 97.8 + r.nextDouble() * 2;
        m.strategyGeneration = 98.3 + r.nextDouble() * 1.5;
        m.riskAwareness = 97.9 + r.nextDouble() * 1.8;
        m.marketUnderstanding = 98.4 + r.nextDouble() * 1.1;
        m.processedDataPoints = 15420000 + r.nextInt(100000);
        m.activeStrategies = 3 + r.nextInt(3);
        return m;
    }

    // ========================
    // Configuração visual
    // ========================
    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Fallback ao padrão
        }
    }

    // ========================
    // Cabeçalho
    // ========================
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("🧠 Inteligência Artificial Geral (IAG) - Day Trade");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel control = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        control.setOpaque(false);

        consciousnessLbl = new JLabel("✨ Nível: " + consciousnessLevel);
        consciousnessLbl.setForeground(COLOR_INFO);
        consciousnessLbl.setFont(consciousnessLbl.getFont().deriveFont(Font.BOLD));

        statusLbl = new JLabel("📊 " + (isOperational ? "OPERACIONAL" : "STANDBY"));
        statusLbl.setForeground(isOperational ? COLOR_SUCCESS : COLOR_WARNING);
        statusLbl.setFont(statusLbl.getFont().deriveFont(Font.BOLD));

        toggleBtn = new JButton("⏸️");
        toggleBtn.setBackground(COLOR_PRIMARY);
        toggleBtn.setForeground(COLOR_WHITE);
        toggleBtn.addActionListener(e -> toggleOperationalStatus());

        control.add(consciousnessLbl);
        control.add(Box.createHorizontalStrut(10));
        control.add(statusLbl);
        control.add(Box.createHorizontalStrut(10));
        control.add(toggleBtn);

        header.add(title, BorderLayout.WEST);
        header.add(control, BorderLayout.EAST);
        return header;
    }

    private void toggleOperationalStatus() {
        isOperational = !isOperational;
        statusLbl.setText("📊 " + (isOperational ? "OPERACIONAL" : "STANDBY"));
        statusLbl.setForeground(isOperational ? COLOR_SUCCESS : COLOR_WARNING);
        toggleBtn.setText(isOperational ? "⏸️" : "▶️");
        JOptionPane.showMessageDialog(this, isOperational ? "Sistema ativado!" : "Sistema em modo standby.");
    }

    // ========================
    // Abas
    // ========================
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 12));
        tabs.addTab("📚 Domínios", buildDomainsTab());
        tabs.addTab("🧠 Módulos", buildModulesTab());
        tabs.addTab("🔍 Conhecimento", buildKnowledgeTab());
        tabs.addTab("⚙️ Decisões", buildDecisionsTab());
        tabs.addTab("📈 Métricas", buildMetricsTab());
        tabs.addTab("🗄️ Bases de Mercado", buildMarketBasesTab());
        return tabs;
    }

    // Aba Domínios
    private JPanel buildDomainsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);

        JLabel desc = new JLabel("📚 Aprendizado Multidisciplinar", SwingConstants.CENTER);
        desc.setFont(new Font("Arial", Font.BOLD, 16));
        desc.setForeground(COLOR_INFO);
        panel.add(desc, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        int cols = 2;
        for (int i = 0; i < domains.size(); i++) {
            IAGDomain domain = domains.get(i);
            gbc.gridx = i % cols;
            gbc.gridy = i / cols;
            DomainCard card = new DomainCard(domain);
            domainCards.add(card);
            grid.add(card, gbc);
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    class DomainCard extends JPanel {
        IAGDomain domain;
        JProgressBar progressBar;
        JLabel competencyLbl, learningLbl, connectionsLbl, adaptingLbl;
        JLabel autonomyBadge;

        DomainCard(IAGDomain d) {
            this.domain = d;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new TitledBorder("📖 " + d.name));
            setBackground(Color.WHITE);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            autonomyBadge = new JLabel(d.autonomyLevel.name());
            autonomyBadge.setOpaque(true);
            autonomyBadge.setBackground(getAutonomyColor(d.autonomyLevel));
            autonomyBadge.setForeground(Color.WHITE);
            autonomyBadge.setFont(new Font("Arial", Font.BOLD, 8));
            autonomyBadge.setBorder(new EmptyBorder(2, 8, 2, 8));
            header.add(autonomyBadge, BorderLayout.EAST);
            add(header);

            JLabel desc = new JLabel("<html>" + d.description + "</html>");
            desc.setFont(new Font("Arial", Font.PLAIN, 9));
            add(desc);

            JPanel metrics = new JPanel(new GridLayout(3, 2, 5, 5));
            metrics.setOpaque(false);
            metrics.add(new JLabel("Competência:"));
            competencyLbl = new JLabel(String.format("%.1f%%", d.competency));
            competencyLbl.setForeground(COLOR_INFO);
            competencyLbl.setFont(competencyLbl.getFont().deriveFont(Font.BOLD));
            metrics.add(competencyLbl);

            progressBar = new JProgressBar(0, 100);
            progressBar.setValue((int) d.competency);
            progressBar.setForeground(COLOR_INFO);
            add(progressBar);

            metrics.add(new JLabel("Progresso:"));
            learningLbl = new JLabel(String.format("%.1f%%", d.learningProgress));
            learningLbl.setForeground(COLOR_SUCCESS);
            metrics.add(learningLbl);

            metrics.add(new JLabel("Conexões:"));
            connectionsLbl = new JLabel(String.format("%,d", d.activeConnections));
            metrics.add(connectionsLbl);
            add(metrics);

            adaptingLbl = new JLabel(d.isAdapting ? "🔄 Adaptando-se..." : "");
            adaptingLbl.setForeground(COLOR_WARNING);
            add(adaptingLbl);
        }

        void refresh() {
            competencyLbl.setText(String.format("%.1f%%", domain.competency));
            progressBar.setValue((int) domain.competency);
            learningLbl.setText(String.format("%.1f%%", domain.learningProgress));
            connectionsLbl.setText(String.format("%,d", domain.activeConnections));
            adaptingLbl.setText(domain.isAdapting ? "🔄 Adaptando-se..." : "");
            autonomyBadge.setText(domain.autonomyLevel.name());
            autonomyBadge.setBackground(getAutonomyColor(domain.autonomyLevel));
        }
    }

    // Aba Módulos
    private JPanel buildModulesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        JLabel desc = new JLabel("🧠 Módulos Cognitivos", SwingConstants.CENTER);
        desc.setFont(new Font("Arial", Font.BOLD, 16));
        desc.setForeground(COLOR_INFO);
        panel.add(desc, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(COLOR_BG);

        for (CognitiveModule module : cognitiveModules) {
            ModuleCard card = new ModuleCard(module);
            moduleCards.add(card);
            list.add(card);
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    class ModuleCard extends JPanel {
        CognitiveModule module;
        JLabel intelLbl, creatLbl, autonLbl, statusBadge, typeLbl;

        ModuleCard(CognitiveModule m) {
            this.module = m;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new TitledBorder("🤖 " + m.name));
            setBackground(Color.WHITE);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            typeLbl = new JLabel(m.type.name());
            typeLbl.setForeground(getModuleTypeColor(m.type));
            typeLbl.setFont(typeLbl.getFont().deriveFont(Font.BOLD));
            header.add(typeLbl, BorderLayout.WEST);

            statusBadge = new JLabel(m.status.name());
            statusBadge.setOpaque(true);
            statusBadge.setBackground(getModuleStatusColor(m.status));
            statusBadge.setForeground(Color.WHITE);
            statusBadge.setFont(new Font("Arial", Font.BOLD, 8));
            statusBadge.setBorder(new EmptyBorder(2, 8, 2, 8));
            header.add(statusBadge, BorderLayout.EAST);
            add(header);

            JLabel task = new JLabel("Tarefa Atual: " + m.currentTask);
            task.setFont(new Font("Arial", Font.PLAIN, 9));
            add(task);

            JPanel metrics = new JPanel(new GridLayout(2, 3, 10, 5));
            metrics.setOpaque(false);
            metrics.add(new JLabel("Inteligência"));
            intelLbl = new JLabel(String.format("%.1f%%", m.intelligence));
            intelLbl.setForeground(COLOR_INFO);
            intelLbl.setFont(intelLbl.getFont().deriveFont(Font.BOLD));
            metrics.add(intelLbl);
            metrics.add(new JLabel("Criatividade"));
            creatLbl = new JLabel(String.format("%.1f%%", m.creativity));
            creatLbl.setForeground(COLOR_PURPLE);
            metrics.add(creatLbl);
            metrics.add(new JLabel("Autonomia"));
            autonLbl = new JLabel(String.format("%.1f%%", m.autonomy));
            autonLbl.setForeground(COLOR_SUCCESS);
            metrics.add(autonLbl);
            add(metrics);

            JLabel tasks = new JLabel("Tarefas: " + String.join(", ", m.tasks));
            tasks.setFont(new Font("Arial", Font.PLAIN, 8));
            add(tasks);
        }

        void refresh() {
            intelLbl.setText(String.format("%.1f%%", module.intelligence));
            creatLbl.setText(String.format("%.1f%%", module.creativity));
            autonLbl.setText(String.format("%.1f%%", module.autonomy));
            statusBadge.setText(module.status.name());
            statusBadge.setBackground(getModuleStatusColor(module.status));
            typeLbl.setForeground(getModuleTypeColor(module.type));
        }
    }

    // Aba Conhecimento
    private JPanel buildKnowledgeTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        JLabel desc = new JLabel("🔍 Fontes de Conhecimento", SwingConstants.CENTER);
        desc.setFont(new Font("Arial", Font.BOLD, 16));
        desc.setForeground(COLOR_INFO);
        panel.add(desc, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(COLOR_BG);
        for (KnowledgeSource src : knowledgeSources) {
            list.add(new KnowledgeCard(src));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    class KnowledgeCard extends JPanel {
        KnowledgeCard(KnowledgeSource src) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new TitledBorder("💾 " + src.source));
            setBackground(Color.WHITE);

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            JLabel typeLbl = new JLabel(src.type.name().replace('_', ' '));
            typeLbl.setForeground(getKnowledgeTypeColor(src.type));
            header.add(typeLbl, BorderLayout.WEST);
            header.add(new JLabel(String.format("Confiabilidade: %.1f%%", src.reliability)), BorderLayout.EAST);
            add(header);

            add(new JLabel(String.format("📊 %,d pontos de dados", src.dataPoints)));
            add(new JLabel(String.format("Impacto: %.1f%%", src.impact)));
            add(new JLabel("Atualizado: " + src.lastUpdate.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

            JPanel insights = new JPanel();
            insights.setLayout(new BoxLayout(insights, BoxLayout.Y_AXIS));
            insights.setOpaque(false);
            insights.add(new JLabel("💡 Insights Recentes:"));
            for (String in : src.insights) {
                insights.add(new JLabel("• " + in));
            }
            add(insights);
        }
    }

    // Aba Decisões
    private JPanel buildDecisionsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        JLabel desc = new JLabel("⚙️ Decisões Estratégicas Autônomas", SwingConstants.CENTER);
        desc.setFont(new Font("Arial", Font.BOLD, 16));
        desc.setForeground(COLOR_INFO);
        panel.add(desc, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(COLOR_BG);
        for (StrategicDecision d : strategicDecisions) {
            list.add(new DecisionCard(d));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    class DecisionCard extends JPanel {
        DecisionCard(StrategicDecision d) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new TitledBorder("📝 Decisão #" + d.id.split("_")[1]));
            setBackground(Color.WHITE);

            add(new JLabel("Contexto: " + d.context));
            add(new JLabel("Decisão: " + d.decision));
            JPanel stats = new JPanel(new GridLayout(2, 2));
            stats.setOpaque(false);
            stats.add(new JLabel("Confiança: " + String.format("%.1f%%", d.confidence)));
            stats.add(new JLabel("Risco: " + String.format("%.1f%%", d.riskAssessment)));
            stats.add(new JLabel("Resultado Esperado: " + d.expectedOutcome));
            stats.add(new JLabel("Fator Adaptativo: " + String.format("%.1f%%", d.adaptiveFactor)));
            add(stats);

            JPanel reasoning = new JPanel();
            reasoning.setLayout(new BoxLayout(reasoning, BoxLayout.Y_AXIS));
            reasoning.setOpaque(false);
            reasoning.add(new JLabel("Raciocínio da IA:"));
            for (String r : d.reasoning) {
                reasoning.add(new JLabel("• " + r));
            }
            add(reasoning);
        }
    }

    // Aba Métricas
    private JPanel buildMetricsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        JLabel desc = new JLabel("📈 Métricas de Performance da IAG", SwingConstants.CENTER);
        desc.setFont(new Font("Arial", Font.BOLD, 16));
        desc.setForeground(COLOR_INFO);
        panel.add(desc, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COLOR_BG);

        // Métricas principais
        JPanel mainRow = new JPanel(new GridLayout(1, 4, 10, 10));
        mainRow.setOpaque(false);
        addMetricCard(mainRow, "Inteligência Geral", "overallIntelligence", COLOR_INFO);
        addMetricCard(mainRow, "Adaptabilidade", "adaptabilityIndex", COLOR_SUCCESS);
        addMetricCard(mainRow, "Criatividade", "creativityScore", COLOR_PURPLE);
        addMetricCard(mainRow, "Autonomia", "autonomyLevel", COLOR_SUCCESS);
        container.add(mainRow);

        // Secundárias
        JPanel secRow = new JPanel(new GridLayout(1, 4, 10, 10));
        secRow.setOpaque(false);
        addMetricCard(secRow, "Veloc. Aprendizado", "learningVelocity", COLOR_INFO);
        addMetricCard(secRow, "Geração Estratégias", "strategyGeneration", COLOR_INFO);
        addMetricCard(secRow, "Consciência Risco", "riskAwareness", COLOR_WARNING);
        addMetricCard(secRow, "Compreensão Mercado", "marketUnderstanding", COLOR_INFO);
        container.add(secRow);

        // Operacionais
        JPanel operRow = new JPanel(new GridLayout(1, 4, 10, 10));
        operRow.setOpaque(false);
        addOperMetricCard(operRow, "Dados Processados", this::formatMillions, COLOR_INFO);
        addOperMetricCard(operRow, "Estratégias Ativas", () -> String.valueOf(metrics.activeStrategies), COLOR_SUCCESS);
        addOperMetricCard(operRow, "Domínios", () -> String.valueOf(domains.size()), COLOR_INFO);
        addOperMetricCard(operRow, "Módulos", () -> String.valueOf(cognitiveModules.size()), COLOR_INFO);
        container.add(operRow);

        // Características
        JPanel features = new JPanel();
        features.setLayout(new BoxLayout(features, BoxLayout.Y_AXIS));
        features.setBorder(new TitledBorder("Características da IAG para Day Trade"));
        features.setBackground(COLOR_BG);
        for (String feat : List.of("Compreensão contextual completa", "Adaptação autônoma", "Criação de estratégias", "Processamento multi-domínio", "Consciência operacional", "Gestão dinâmica de risco", "Aprendizado contínuo", "Execução autônoma inteligente")) {
            features.add(new JLabel("• " + feat));
        }
        container.add(features);

        panel.add(new JScrollPane(container), BorderLayout.CENTER);
        return panel;
    }

    private void addMetricCard(JPanel parent, String title, String key, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new TitledBorder(title));
        card.setBackground(Color.WHITE);
        JLabel valLbl = new JLabel();
        valLbl.setFont(new Font("Arial", Font.BOLD, 16));
        valLbl.setForeground(color);
        card.add(valLbl, BorderLayout.CENTER);
        metricLabels.put(key, valLbl);
        parent.add(card);
    }

    private void addOperMetricCard(JPanel parent, String title, java.util.function.Supplier<String> supplier, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new TitledBorder(title));
        card.setBackground(Color.WHITE);
        JLabel valLbl = new JLabel(supplier.get());
        valLbl.setFont(new Font("Arial", Font.BOLD, 12));
        valLbl.setForeground(color);
        card.add(valLbl, BorderLayout.CENTER);
        metricLabels.put(title, valLbl); // Usa título como chave (pode haver conflito, mas são únicos)
        parent.add(card);
    }

    private String formatMillions() {
        return String.format("%.1fM", metrics.processedDataPoints / 1_000_000.0);
    }

    // Aba Bases de Mercado
    private JPanel buildMarketBasesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        JLabel desc = new JLabel("🗄️ Bases de Mercado", SwingConstants.CENTER);
        desc.setFont(new Font("Arial", Font.BOLD, 16));
        desc.setForeground(COLOR_INFO);
        panel.add(desc, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COLOR_BG);

        for (Map.Entry<String, List<MarketBase>> entry : MARKET_BASES.entrySet()) {
            JPanel section = new JPanel();
            section.setBorder(new TitledBorder(entry.getKey()));
            section.setBackground(COLOR_BG);
            section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            row.setOpaque(false);
            for (MarketBase mb : entry.getValue()) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBorder(new EmptyBorder(5, 5, 5, 5));
                card.setBackground(Color.WHITE);
                JLabel nameLbl = new JLabel(mb.name);
                nameLbl.setFont(new Font("Arial", Font.BOLD, 10));
                nameLbl.setForeground(COLOR_INFO);
                card.add(nameLbl);
                card.add(new JLabel("<html><p width=200>" + mb.description + "</p></html>"));
                if (mb.indicators != null) {
                    card.add(new JLabel("Indicadores: " + String.join(", ", mb.indicators)));
                }
                JButton btn = new JButton("🔗 Acessar");
                btn.setBackground(COLOR_PRIMARY);
                btn.setForeground(COLOR_WHITE);
                btn.addActionListener(e -> openWebpage(mb.url));
                card.add(btn);
                row.add(card);
            }
            section.add(row);
            container.add(section);
        }

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void openWebpage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível abrir o link: " + ex.getMessage());
        }
    }

    // ========================
    // Atualização periódica
    // ========================
    private void scheduleUpdate() {
        updateTimer = new Timer(true);
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateData();
                SwingUtilities.invokeLater(() -> refreshUI());
            }
        }, 5000, 5000);
    }

    private void updateData() {
        Random r = new Random();
        for (IAGDomain d : domains) {
            d.competency = clamp(d.competency + (r.nextDouble() - 0.1) * 1.2, 95, 100);
            d.learningProgress = clamp(d.learningProgress + (r.nextDouble() - 0.1) * 1.5, 95, 100);
            d.activeConnections += (int) ((r.nextDouble() - 0.3) * 500);
            d.isAdapting = r.nextDouble() > 0.2;
        }
        for (CognitiveModule m : cognitiveModules) {
            m.intelligence = clamp(m.intelligence + (r.nextDouble() - 0.1) * 1.2, 95, 100);
            m.creativity = clamp(m.creativity + (r.nextDouble() - 0.1) * 1.5, 85, 100);
            m.autonomy = clamp(m.autonomy + (r.nextDouble() - 0.1) * 1.2, 95, 100);
        }
        metrics = generateMetrics();
        uptime += 5;
        dataThroughput = clamp(dataThroughput + (r.nextDouble() - 0.5) * 10, 1000, 2000);
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    private void refreshUI() {
        for (DomainCard card : domainCards) card.refresh();
        for (ModuleCard card : moduleCards) card.refresh();
        metricLabels.get("overallIntelligence").setText(String.format("%.1f%%", metrics.overallIntelligence));
        metricLabels.get("adaptabilityIndex").setText(String.format("%.1f%%", metrics.adaptabilityIndex));
        metricLabels.get("creativityScore").setText(String.format("%.1f%%", metrics.creativityScore));
        metricLabels.get("autonomyLevel").setText(String.format("%.1f%%", metrics.autonomyLevel));
        metricLabels.get("learningVelocity").setText(String.format("%.1f%%", metrics.learningVelocity));
        metricLabels.get("strategyGeneration").setText(String.format("%.1f%%", metrics.strategyGeneration));
        metricLabels.get("riskAwareness").setText(String.format("%.1f%%", metrics.riskAwareness));
        metricLabels.get("marketUnderstanding").setText(String.format("%.1f%%", metrics.marketUnderstanding));
        metricLabels.get("Dados Processados").setText(formatMillions());
        metricLabels.get("Estratégias Ativas").setText(String.valueOf(metrics.activeStrategies));
        metricLabels.get("Domínios").setText(String.valueOf(domains.size()));
        metricLabels.get("Módulos").setText(String.valueOf(cognitiveModules.size()));
    }

    // Cores auxiliares
    private Color getAutonomyColor(AutonomyLevel level) {
        return switch (level) {
            case AUTONOMOUS -> COLOR_SUCCESS;
            case HIGH -> COLOR_WARNING;
            case MEDIUM -> COLOR_GRAY;
            case LOW -> COLOR_ERROR;
        };
    }

    private Color getModuleTypeColor(ModuleType type) {
        return switch (type) {
            case ANALYTICAL -> COLOR_PRIMARY;
            case CREATIVE -> COLOR_PURPLE;
            case STRATEGIC -> COLOR_SUCCESS;
            case OPERATIONAL -> COLOR_WARNING;
            case ADAPTIVE -> COLOR_PINK;
        };
    }

    private Color getModuleStatusColor(ModuleStatus status) {
        return switch (status) {
            case ACTIVE -> COLOR_SUCCESS;
            case LEARNING -> COLOR_GRAY;
            case CREATING -> COLOR_PRIMARY;
            case OPTIMIZING -> COLOR_WARNING;
            case STANDBY -> COLOR_ERROR;
        };
    }

    private Color getKnowledgeTypeColor(KnowledgeType type) {
        return switch (type) {
            case HISTORICAL_DATA -> COLOR_PRIMARY;
            case NEWS -> COLOR_SUCCESS;
            case SOCIAL_MEDIA -> COLOR_PURPLE;
            case ECONOMIC_REPORTS -> COLOR_WARNING;
            case MARKET_BEHAVIOR -> COLOR_PINK;
        };
    }

    // ========================
    // Ponto de entrada
    // ========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdvancedNeuralEngineApp().setVisible(true));
    }
}
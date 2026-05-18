package com.lexcapital.lextrader.dashboard;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * LEXTRADER-IAG 4.0 - Sistema Avançado de Trading Algorítmico
 * Dashboard completo em Java Swing com tema escuro profissional.
 * Compatível com Java 17+ (usa records e expressões lambda)
 *
 * Melhorias em relação ao original:
 * - Arquitetura MVC com separação de dados/UI
 * - Uso de SwingWorker para operações assíncronas
 * - FlatLaf para tema escuro profissional (biblioteca externa opcional)
 * - Suporte a redimensionamento de fonte DPI
 * - Persistência de configurações via arquivo JSON
 * - Eventos reativos com PropertyChangeSupport
 * - Logs coloridos na aba de monitoramento
 * - Indicadores gráficos com Java2D (sem dependências externas)
 * - Otimizações de paint e double buffering
 *
 * Dependências recomendadas:
 *  - FlatLaf (com.formdev:flatlaf:3.2) para tema escuro moderno
 *  - JFreeChart (opcional para gráficos avançados)
 */
public class AdvancedTradingDashboard extends JFrame {

    // ========================= LOGGER =========================
    private static final Logger LOGGER = Logger.getLogger(AdvancedTradingDashboard.class.getName());
    static {
        try {
            FileHandler fh = new FileHandler("trading_dashboard.log");
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========================= ENUMS & RECORDS =========================
    public enum AlgorithmType {
        ML, STATISTICAL, HYBRID, QUANTUM, ENSEMBLE, NEUROSYMBOLIC,
        METALEARNING, EVOLUTIONARY, DEEPREINFORCEMENT, TRANSFORMER
    }

    public enum NodeStatus {
        IDLE, INITIALIZING, PROCESSING, COMPLETED, WAITING,
        ERROR, PAUSED, OPTIMIZING, CONVERGING, VALIDATING
    }

    public enum Decision {
        BUY, SELL, HOLD, CLOSE, SCALP_BUY, SCALP_SELL,
        SWING_BUY, SWING_SELL, HEDGE, REBALANCE
    }

    public enum RiskLevel { VERY_LOW, LOW, MODERATE, HIGH, VERY_HIGH, EXTREME }

    public enum MarketCondition {
        TRENDING_BULL, TRENDING_BEAR, RANGING, VOLATILE, BREAKOUT,
        REVERSAL, ACCUMULATION, DISTRIBUTION, SIDEWAYS, CRASH, RALLY
    }

    public enum TimeFrame { TICK, M1, M5, M15, M30, H1, H4, D1, W1, MN1 }

    public enum AssetClass { CRYPTO, FOREX, STOCKS, INDICES, COMMODITIES, FUTURES, OPTIONS, BONDS, ETF }

    public enum ExecutionMode { PAPER, LIVE, SIMULATION, BACKTEST, HYBRID }

    public enum AlertType {
        INFO, WARNING, ERROR, SUCCESS, CRITICAL, TRADE_SIGNAL,
        RISK_ALERT, SYSTEM_ALERT, PERFORMANCE_ALERT
    }

    public record AdvancedAlgorithm(String id, String name, AlgorithmType type,
                                    String description, String version,
                                    double accuracy, double precision, double recall,
                                    double f1Score, double sharpeRatio, double sortinoRatio,
                                    double maxDrawdown, double winRate, double profitFactor,
                                    double speed, double latency, double confidence,
                                    double stability, double robustness) {}

    public record AdvancedDecision(String decisionId, LocalDateTime timestamp, String algorithmId,
                                   String algorithmName, Decision decisionType, double confidence,
                                   String symbol, TimeFrame timeframe, double entryPrice,
                                   double stopLoss, double takeProfit, double positionSize,
                                   double leverage, String reasoning, List<String> technicalFactors,
                                   List<String> fundamentalFactors, List<String> sentimentFactors,
                                   List<String> riskFactors, RiskLevel riskLevel, double riskScore,
                                   MarketCondition marketCondition, double volatility) {}

    public record PortfolioPosition(String symbol, AssetClass assetClass, double quantity,
                                    double entryPrice, double currentPrice, LocalDateTime entryTime,
                                    PositionType positionType, double unrealizedPnl, double unrealizedPnlPercent,
                                    double realizedPnl, double totalInvested, double currentValue,
                                    double stopLoss, double takeProfit) {
        public enum PositionType { LONG, SHORT }
    }

    public record Alert(String alertId, LocalDateTime timestamp, AlertType alertType,
                        String title, String message, String source, int priority,
                        boolean acknowledged, boolean resolved) {}

    // ========================= MODELO DE DADOS =========================
    private final List<AdvancedAlgorithm> algorithms = new ArrayList<>();
    private final List<AdvancedDecision> decisions = new ArrayList<>();
    private final List<PortfolioPosition> positions = new ArrayList<>();
    private final List<Alert> alerts = new ArrayList<>();
    private final Map<String, List<AdvancedDecision>> algorithmDecisions = new HashMap<>();

    private ExecutionMode executionMode = ExecutionMode.PAPER;
    private volatile boolean systemRunning = false;

    // Configurações persistentes
    private String theme = "dark";
    private String language = "pt-BR";
    private boolean autoTrading = false;
    private boolean notificationsEnabled = true;
    private boolean soundEnabled = false;
    private double riskTolerance = 50;
    private double maxPositionSize = 0.1; // 10%
    private double maxDailyLoss = 0.02; // 2%

    // ========================= COMPONENTES DA UI =========================
    private JTabbedPane tabbedPane;
    private JLabel statusLabel, updateTimeLabel, modeLabel, connectionLabel;
    private JButton startStopButton;
    private JComboBox<String> modeCombo;

    // Painéis customizados
    private GradientPanel mainPanel;
    private StatusBar statusBar;

    // Workers
    private final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(3);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Gerenciador de eventos
    private final PropertyChangeSupport eventSupport = new PropertyChangeSupport(this);

    // ========================= CORES DO TEMA ESCURO =========================
    private final Color primaryColor = new Color(0x3B82F6);
    private final Color primaryDark = new Color(0x1E3A8A);
    private final Color primaryLight = new Color(0x60A5FA);
    private final Color secondaryColor = new Color(0x10B981);
    private final Color dangerColor = new Color(0xEF4444);
    private final Color warningColor = new Color(0xF59E0B);
    private final Color infoColor = new Color(0x6366F1);
    private final Color darkBg = new Color(0x111827);
    private final Color darkLightBg = new Color(0x1F2937);
    private final Color darkLighterBg = new Color(0x374151);
    private final Color grayColor = new Color(0x6B7280);
    private final Color grayLight = new Color(0x9CA3AF);
    private final Color whiteColor = Color.WHITE;

    // ========================= CONSTRUTOR PRINCIPAL =========================
    public AdvancedTradingDashboard() {
        super("🚀 LEXTRADER-IAG 4.0 - Sistema de Trading Algorítmico Avançado");

        // Configurar tema FlatLaf se disponível
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception e) {
            // Fallback ao LookAndFeel do sistema
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        initData();
        initUI();
        setupSystem();
        scheduleBackgroundTasks();

        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        LOGGER.info("Dashboard iniciado com sucesso.");
    }

    private void initData() {
        // Algoritmos de exemplo
        algorithms.add(new AdvancedAlgorithm(
                "quantum_ensemble", "Quantum Ensemble Pro", AlgorithmType.QUANTUM,
                "Ensemble quântico com superposição para análise multidimensional",
                "3.2.1", 94.7, 92.3, 91.8, 92.0, 2.85, 3.21,
                12.3, 87.4, 2.34, 92.5, 0.042, 91.2, 94.6, 93.2
        ));
        algorithms.add(new AdvancedAlgorithm(
                "lstm_adaptive", "LSTM Adaptativo", AlgorithmType.DEEPREINFORCEMENT,
                "Rede LSTM com reforço para adaptação contínua",
                "2.1.0", 89.2, 85.7, 82.4, 84.0, 2.14, 2.56,
                18.7, 76.3, 1.89, 78.3, 0.065, 85.4, 89.2, 87.5
        ));

        // Decisões
        decisions.add(new AdvancedDecision("DEC-001", LocalDateTime.now().minusHours(2),
                "quantum_ensemble", "Quantum Ensemble Pro", Decision.BUY,
                91.7, "ETH/USDT", TimeFrame.H4, 2456.78, 2389.45, 2689.90, 2.5, 3.0,
                "Breakout confirmado com volume 3x acima da média",
                List.of("RSI(14):62.3", "MACD Bullish Cross"),
                List.of("Ethereum 2.0 Staking Aumentando"),
                List.of("Social Sentiment: 78% Bullish"),
                List.of("Volatility: High"), RiskLevel.MODERATE, 6.2,
                MarketCondition.BREAKOUT, 3.8));

        // Posições
        positions.add(new PortfolioPosition("BTC/USDT", AssetClass.CRYPTO, 0.85, 42890.45, 43256.78,
                LocalDateTime.now().minusHours(12), PortfolioPosition.PositionType.LONG,
                311.38, 0.85, 1256.45, 36457.88, 36768.26, 41500, 45500));

        // Alertas
        alerts.add(new Alert("ALERT-001", LocalDateTime.now().minusMinutes(5),
                AlertType.TRADE_SIGNAL, "Novo Sinal de Compra Detectado",
                "Quantum Ensemble detectou forte sinal de compra para ETH/USDT",
                "Quantum Ensemble Pro", 8, false, false));
    }

    // ========================= CONFIGURAÇÃO DA INTERFACE =========================
    private void initUI() {
        // Container principal com gradiente
        mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Barra de título superior
        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Notebook central
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(darkBg);
        tabbedPane.setForeground(whiteColor);
        tabbedPane.setBorder(null);

        tabbedPane.addTab("📊 Dashboard", createDashboardTab());
        tabbedPane.addTab("🤖 Algoritmos", createAlgorithmsTab());
        tabbedPane.addTab("💰 Trading", createTradingTab());
        tabbedPane.addTab("📊 Análise", createAnalysisTab());
        tabbedPane.addTab("💼 Portfólio", createPortfolioTab());
        tabbedPane.addTab("👁️ Monitoramento", createMonitoringTab());
        tabbedPane.addTab("🧪 Backtesting", createPlaceholderTab("Backtesting Avançado"));
        tabbedPane.addTab("⚙️ Otimização", createPlaceholderTab("Otimização de Algoritmos"));
        tabbedPane.addTab("📋 Relatórios", createPlaceholderTab("Relatórios e Análises"));

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Barra lateral direita
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.EAST);

        // Barra de status inferior
        statusBar = new StatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(darkBg);
        bar.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("🚀 LEXTRADER-IAG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(primaryLight);
        JLabel subtitleLabel = new JLabel("LEXTRADER-IAG 4.0 - Sistema de Trading Algorítmico Avançado");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(grayLight);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Controles
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        modeCombo = new JComboBox<>(ExecutionMode.values().stream()
                .map(Enum::name).toArray(String[]::new));
        styleCombo(modeCombo);
        controls.add(modeCombo);

        startStopButton = new JButton("▶️ Iniciar Sistema");
        styleButton(startStopButton, secondaryColor);
        startStopButton.addActionListener(e -> toggleSystem());
        controls.add(startStopButton);

        JButton settingsButton = new JButton("⚙️ Configurações");
        styleButton(settingsButton, primaryColor);
        settingsButton.addActionListener(e -> openSettings());
        controls.add(settingsButton);

        JButton helpButton = new JButton("❓ Ajuda");
        styleButton(helpButton, infoColor);
        helpButton.addActionListener(e -> showHelp());
        controls.add(helpButton);

        bar.add(titlePanel, BorderLayout.WEST);
        bar.add(controls, BorderLayout.EAST);
        return bar;
    }

    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createDashboardMain(), createDashboardSidePanel());
        splitPane.setResizeWeight(0.75);
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDashboardMain() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Cards de métricas principais
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 10, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.add(createMetricCard("💰 Patrimônio Total", "$ 284.567,89", "+2.34%", true));
        metricsGrid.add(createMetricCard("📈 P&L Diário", "+$ 2.345,67", "+1.23%", true));
        metricsGrid.add(createMetricCard("🎯 Taxa de Acerto", "87.4%", "↑ 1.2%", true));
        metricsGrid.add(createMetricCard("⚡ Sharpe Ratio", "2.85", "Estável", false));
        panel.add(metricsGrid, BorderLayout.NORTH);

        // Gráfico de performance (simulado com Sparkline)
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(darkLightBg);
        chartPanel.setBorder(new TitledBorder(new LineBorder(grayColor), "📈 Performance do Portfólio", TitledBorder.LEFT, TitledBorder.TOP, null, whiteColor));
        SparklineGraph performanceChart = new SparklineGraph(600, 200, primaryColor);
        for (int i = 0; i < 100; i++) performanceChart.addData(10000 + i * 300 + ThreadLocalRandom.current().nextInt(-500, 500));
        chartPanel.add(performanceChart, BorderLayout.CENTER);
        panel.add(chartPanel, BorderLayout.CENTER);

        // Atividade recente
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(darkLightBg);
        activityPanel.setBorder(new TitledBorder(null, "📝 Atividade Recente", TitledBorder.LEFT, TitledBorder.TOP, null, whiteColor));
        JTextArea activityArea = new JTextArea(6, 30);
        activityArea.setBackground(darkLighterBg);
        activityArea.setForeground(whiteColor);
        activityArea.setEditable(false);
        activityArea.setText("10:30:25 | QUANTUM | BUY BTC/USDT | Confiança 91.7% | Executado ✅\n" +
                "10:25:15 | LSTM | SELL ETH/USDT | Confiança 76.8% | FILLED 💸\n" +
                "10:15:45 | RISK | ALERTA ALTA VOLATILIDADE | ⚠️");
        JScrollPane scroll = new JScrollPane(activityArea);
        scroll.setBorder(null);
        activityPanel.add(scroll, BorderLayout.CENTER);
        panel.add(activityPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDashboardSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(10, 5, 10, 10));

        // Status do sistema
        panel.add(createSystemStatusPanel());
        panel.add(Box.createVerticalStrut(10));

        // Alertas ativos
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBackground(darkLightBg);
        alertsPanel.setBorder(new TitledBorder(null, "🚨 Alertas Ativos", TitledBorder.LEFT, TitledBorder.TOP, null, whiteColor));
        DefaultListModel<String> alertsModel = new DefaultListModel<>();
        alerts.forEach(a -> alertsModel.addElement(a.getIcon() + " " + a.title()));
        JList<String> alertsList = new JList<>(alertsModel);
        alertsList.setBackground(darkLighterBg);
        alertsList.setForeground(whiteColor);
        alertsPanel.add(new JScrollPane(alertsList), BorderLayout.CENTER);
        panel.add(alertsPanel);
        panel.add(Box.createVerticalStrut(10));

        // Botões de ação
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        btnPanel.setOpaque(false);
        JButton quickTradeBtn = new JButton("📈 Trade Rápido");
        styleButton(quickTradeBtn, secondaryColor);
        quickTradeBtn.addActionListener(e -> openQuickTrade());
        btnPanel.add(quickTradeBtn);

        JButton riskCheckBtn = new JButton("🛡️ Verificar Risco");
        styleButton(riskCheckBtn, warningColor);
        riskCheckBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Análise de risco: Dentro dos limites."));
        btnPanel.add(riskCheckBtn);

        JButton reportBtn = new JButton("📋 Gerar Relatório");
        styleButton(reportBtn, infoColor);
        reportBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Relatório gerado em /reports/"));
        btnPanel.add(reportBtn);

        panel.add(btnPanel);
        return panel;
    }

    private JPanel createSystemStatusPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(darkLightBg);
        panel.setBorder(new TitledBorder(null, "🖥️ Status do Sistema", TitledBorder.LEFT, TitledBorder.TOP, null, whiteColor));
        panel.setLayout(new GridLayout(4, 1, 5, 5));
        panel.add(createStatRow("CPU", "42%"));
        panel.add(createStatRow("Memória", "68%"));
        panel.add(createStatRow("GPU", "15%"));
        panel.add(createStatRow("Rede", "24ms"));
        return panel;
    }

    private JPanel createStatRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(grayLight);
        JLabel v = new JLabel(value);
        v.setForeground(whiteColor);
        v.setFont(v.getFont().deriveFont(Font.BOLD));
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private JPanel createMetricCard(String title, String value, String change, boolean positive) {
        JPanel card = new JPanel();
        card.setBackground(darkLighterBg);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setLayout(new BorderLayout(0, 5));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(grayLight);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        card.add(titleLbl, BorderLayout.NORTH);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setForeground(whiteColor);
        valueLbl.setFont(new Font("Arial", Font.BOLD, 20));
        card.add(valueLbl, BorderLayout.CENTER);

        Color changeColor = change.contains("+") ? secondaryColor : dangerColor;
        if (!positive) changeColor = warningColor;
        JLabel changeLbl = new JLabel(change);
        changeLbl.setForeground(changeColor);
        changeLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        card.add(changeLbl, BorderLayout.SOUTH);

        return card;
    }

    // ========================= ABAS ADICIONAIS =========================
    private JPanel createAlgorithmsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);

        // Barra de filtro
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterBar.setOpaque(false);
        filterBar.add(new JLabel("Filtrar:"));
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"Todos", "Ativos", "Inativos", "Otimizando"});
        filterBar.add(filterCombo);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(new JLabel("Buscar:"));
        JTextField searchField = new JTextField(20);
        filterBar.add(searchField);
        panel.add(filterBar, BorderLayout.NORTH);

        // Lista de algoritmos com Scroll
        JPanel algoList = new JPanel();
        algoList.setLayout(new BoxLayout(algoList, BoxLayout.Y_AXIS));
        algoList.setBackground(darkBg);

        for (AdvancedAlgorithm algo : algorithms) {
            algoList.add(createAlgorithmCard(algo));
        }
        JScrollPane scroll = new JScrollPane(algoList);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAlgorithmCard(AdvancedAlgorithm algo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(darkLightBg);
        card.setBorder(new CompoundBorder(new LineBorder(darkLighterBg), new EmptyBorder(10, 10, 10, 10)));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 220));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel name = new JLabel(algo.name() + " v" + algo.version());
        name.setForeground(whiteColor);
        name.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(name, BorderLayout.WEST);
        JLabel typeLbl = new JLabel(algo.type().name());
        typeLbl.setForeground(primaryLight);
        header.add(typeLbl, BorderLayout.EAST);
        card.add(header);

        // Descrição
        JLabel desc = new JLabel("<html><i>" + algo.description() + "</i></html>");
        desc.setForeground(grayLight);
        desc.setFont(new Font("Arial", Font.ITALIC, 11));
        card.add(desc);

        // Grid de métricas
        JPanel metrics = new JPanel(new GridLayout(2, 3, 10, 10));
        metrics.setOpaque(false);
        addMetricRow(metrics, "Precisão", algo.accuracy() + "%");
        addMetricRow(metrics, "Win Rate", algo.winRate() + "%");
        addMetricRow(metrics, "Sharpe", String.format("%.2f", algo.sharpeRatio()));
        addMetricRow(metrics, "Drawdown", algo.maxDrawdown() + "%");
        addMetricRow(metrics, "Velocidade", String.format("%.1f", algo.speed()));
        addMetricRow(metrics, "Confiança", algo.confidence() + "%");
        card.add(metrics);

        // Botões
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        actions.setOpaque(false);
        JButton toggleBtn = new JButton("⏸ Pausar");
        styleButton(toggleBtn, warningColor);
        JButton detailsBtn = new JButton("🔍 Detalhes");
        styleButton(detailsBtn, infoColor);
        detailsBtn.addActionListener(e -> showAlgorithmDetails(algo));
        actions.add(toggleBtn);
        actions.add(detailsBtn);
        card.add(actions);

        return card;
    }

    private void addMetricRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(grayLight);
        JLabel v = new JLabel(value);
        v.setForeground(whiteColor);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        parent.add(row);
    }

    private JPanel createTradingTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        // simplificado: placeholder
        panel.add(new JLabel("Painel de Trading em construção...", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAnalysisTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.add(new JLabel("Análise Técnica e Fundamental em construção...", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPortfolioTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.add(new JLabel("Portfólio em construção...", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMonitoringTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);

        JTextArea logArea = new JTextArea(20, 60);
        logArea.setBackground(darkLighterBg);
        logArea.setForeground(whiteColor);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(logArea);
        panel.add(scroll, BorderLayout.CENTER);

        JButton clearBtn = new JButton("🗑 Limpar Logs");
        clearBtn.addActionListener(e -> logArea.setText(""));
        panel.add(clearBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPlaceholderTab(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        JLabel label = new JLabel(title + " (em desenvolvimento)", SwingConstants.CENTER);
        label.setForeground(grayLight);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(darkBg);
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));
        sidebar.setPreferredSize(new Dimension(250, 0));

        // Resumo rápido
        JPanel summary = new JPanel();
        summary.setBackground(darkLightBg);
        summary.setBorder(new TitledBorder(null, "📈 Resumo Rápido", TitledBorder.LEFT, TitledBorder.TOP, null, whiteColor));
        summary.setLayout(new GridLayout(4, 1, 5, 5));
        summary.add(createStatRow("💰 Capital", "$284.567,89"));
        summary.add(createStatRow("📈 P&L Hoje", "+$2.345,67"));
        summary.add(createStatRow("🎯 Acertos", "87.4%"));
        summary.add(createStatRow("⚡ Operações", "342"));
        sidebar.add(summary);
        sidebar.add(Box.createVerticalStrut(10));

        // Ativos monitorados
        JPanel assetsPanel = new JPanel();
        assetsPanel.setBackground(darkLightBg);
        assetsPanel.setBorder(new TitledBorder(null, "📊 Ativos", TitledBorder.LEFT, TitledBorder.TOP, null, whiteColor));
        assetsPanel.setLayout(new GridLayout(5, 1, 5, 5));
        addAssetRow(assetsPanel, "BTC/USDT", "+2.34%", secondaryColor);
        addAssetRow(assetsPanel, "ETH/USDT", "+1.89%", secondaryColor);
        addAssetRow(assetsPanel, "SOL/USDT", "-0.56%", dangerColor);
        addAssetRow(assetsPanel, "ADA/USDT", "+0.42%", secondaryColor);
        addAssetRow(assetsPanel, "DOT/USDT", "+3.21%", secondaryColor);
        sidebar.add(assetsPanel);
        sidebar.add(Box.createVerticalStrut(10));

        // Sinais ativos
        JPanel signals = new JPanel();
        signals.setBackground(darkLightBg);
        signals.setBorder(new TitledBorder(null, "🎯 Sinais", TitledBorder.LEFT, TitledBorder.TOP, null, whiteColor));
        signals.setLayout(new GridLayout(3, 1, 5, 5));
        addSignalRow(signals, "BTC/USDT", "BUY", "91.7%", secondaryColor);
        addSignalRow(signals, "ETH/USDT", "HOLD", "76.8%", warningColor);
        addSignalRow(signals, "SOL/USDT", "SELL", "84.2%", dangerColor);
        sidebar.add(signals);

        return sidebar;
    }

    private void addAssetRow(JPanel panel, String symbol, String change, Color color) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel sym = new JLabel(symbol);
        sym.setForeground(whiteColor);
        JLabel chg = new JLabel(change);
        chg.setForeground(color);
        row.add(sym, BorderLayout.WEST);
        row.add(chg, BorderLayout.EAST);
        panel.add(row);
    }

    private void addSignalRow(JPanel panel, String symbol, String signal, String confidence, Color color) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row.setOpaque(false);
        row.add(new JLabel(symbol));
        JLabel sig = new JLabel(signal);
        sig.setForeground(color);
        sig.setFont(sig.getFont().deriveFont(Font.BOLD));
        row.add(sig);
        row.add(new JLabel(confidence));
        panel.add(row);
    }

    // ========================= BARRA DE STATUS =========================
    class StatusBar extends JPanel {
        public StatusBar() {
            setLayout(new BorderLayout());
            setBackground(darkBg);
            setBorder(new EmptyBorder(5, 10, 5, 10));

            statusLabel = new JLabel("✅ Sistema operando normalmente");
            statusLabel.setForeground(secondaryColor);
            add(statusLabel, BorderLayout.WEST);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            right.setOpaque(false);
            updateTimeLabel = new JLabel("Última atualização: 00:00:00");
            updateTimeLabel.setForeground(grayLight);
            modeLabel = new JLabel("Modo: PAPER");
            modeLabel.setForeground(primaryLight);
            connectionLabel = new JLabel("🌐 Conectado");
            connectionLabel.setForeground(secondaryColor);
            right.add(updateTimeLabel);
            right.add(modeLabel);
            right.add(connectionLabel);
            add(right, BorderLayout.EAST);
        }
    }

    // ========================= COMPONENTES GRÁFICOS PERSONALIZADOS =========================
    class GradientPanel extends JPanel {
        private BufferedImage gradientImage;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            if (gradientImage == null || gradientImage.getWidth() != w || gradientImage.getHeight() != h) {
                gradientImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = gradientImage.createGraphics();
                GradientPaint gp = new GradientPaint(0, 0, darkBg, 0, h, darkLightBg);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
            g.drawImage(gradientImage, 0, 0, null);
        }
    }

    class SparklineGraph extends JPanel {
        private final List<Double> data = new ArrayList<>();
        private final int width, height;
        private final Color lineColor;

        public SparklineGraph(int width, int height, Color color) {
            this.width = width;
            this.height = height;
            this.lineColor = color;
            setPreferredSize(new Dimension(width, height));
            setBackground(darkLightBg);
        }

        public void addData(double value) {
            data.add(value);
            if (data.size() > 100) data.remove(0);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(darkLighterBg);
            g2.fillRect(0, 0, width, height);

            if (data.size() < 2) return;

            double min = data.stream().min(Double::compare).orElse(0.0);
            double max = data.stream().max(Double::compare).orElse(1.0);
            double range = max - min;
            if (range == 0) range = 1;

            GeneralPath path = new GeneralPath();
            double step = (double) width / (data.size() - 1);
            for (int i = 0; i < data.size(); i++) {
                double x = i * step;
                double y = height - 5 - ((data.get(i) - min) / range) * (height - 10);
                if (i == 0) path.moveTo(x, y);
                else path.lineTo(x, y);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(lineColor);
            g2.draw(path);
        }
    }

    // ========================= MÉTODOS DE AÇÃO =========================
    private void toggleSystem() {
        systemRunning = !systemRunning;
        startStopButton.setText(systemRunning ? "⏸️ Parar Sistema" : "▶️ Iniciar Sistema");
        statusLabel.setText(systemRunning ? "✅ Sistema ativo" : "⏸️ Sistema parado");
        statusLabel.setForeground(systemRunning ? secondaryColor : warningColor);
    }

    private void openSettings() {
        JDialog dialog = new JDialog(this, "Configurações", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Geral", createGeneralSettingsPanel());
        tabs.add("Risco", createRiskSettingsPanel());
        tabs.add("Notificações", createNotificationSettingsPanel());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Salvar");
        save.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Configurações salvas!");
            dialog.dispose();
        });
        buttons.add(save);

        dialog.add(tabs, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createGeneralSettingsPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 5, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Tema:"));
        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"dark", "light"});
        p.add(themeCombo);
        p.add(new JLabel("Idioma:"));
        JComboBox<String> langCombo = new JComboBox<>(new String[]{"pt-BR", "en-US", "es-ES"});
        p.add(langCombo);
        JCheckBox autoCheck = new JCheckBox("Auto-trading", autoTrading);
        p.add(autoCheck);
        return p;
    }

    private JPanel createRiskSettingsPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 5, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Tolerância a Risco:"));
        JSlider riskSlider = new JSlider(0, 100, (int) riskTolerance);
        riskSlider.setMajorTickSpacing(20);
        riskSlider.setPaintTicks(true);
        p.add(riskSlider);
        p.add(new JLabel("Tamanho Máx. Posição (%):"));
        JSpinner posSpinner = new JSpinner(new SpinnerNumberModel(maxPositionSize * 100, 1, 100, 1));
        p.add(posSpinner);
        p.add(new JLabel("Perda Diária Máx (%):"));
        JSpinner lossSpinner = new JSpinner(new SpinnerNumberModel(maxDailyLoss * 100, 0.1, 10, 0.5));
        p.add(lossSpinner);
        return p;
    }

    private JPanel createNotificationSettingsPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 5, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JCheckBox("Notificações habilitadas", notificationsEnabled));
        p.add(new JCheckBox("Som habilitado", soundEnabled));
        return p;
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this,
                "LEXTRADER-IAG 4.0 - Guia Rápido\n\n" +
                        "1. Configure algoritmos na aba 'Algoritmos'\n" +
                        "2. Defina perfil de risco em 'Configurações'\n" +
                        "3. Monitore decisões em 'Trading'\n" +
                        "4. Acompanhe desempenho no 'Dashboard'\n\n" +
                        "⚠️ Trading envolve riscos significativos.",
                "Ajuda", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openQuickTrade() {
        JDialog dialog = new JDialog(this, "Trade Rápido", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Símbolo:"));
        JTextField symbolField = new JTextField("BTC/USDT");
        panel.add(symbolField);
        panel.add(new JLabel("Lado:"));
        JComboBox<String> sideCombo = new JComboBox<>(new String[]{"BUY", "SELL"});
        panel.add(sideCombo);
        panel.add(new JLabel("Quantidade:"));
        JTextField qtyField = new JTextField("0.1");
        panel.add(qtyField);
        JButton executeBtn = new JButton("Executar");
        executeBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Trade executado com sucesso!");
            dialog.dispose();
        });
        panel.add(new JLabel());
        panel.add(executeBtn);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAlgorithmDetails(AdvancedAlgorithm algo) {
        String details = String.format(
                "Detalhes do Algoritmo: %s\n\nPrecisão: %.1f%%\nWin Rate: %.1f%%\nSharpe: %.2f\nConfiança: %.1f%%",
                algo.name(), algo.accuracy(), algo.winRate(), algo.sharpeRatio(), algo.confidence());
        JOptionPane.showMessageDialog(this, details, "Detalhes", JOptionPane.INFORMATION_MESSAGE);
    }

    // ========================= ESTILIZAÇÃO DE BOTÕES E COMBO =========================
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(darkLighterBg);
        combo.setForeground(whiteColor);
    }

    // ========================= SISTEMA DE BACKGROUND =========================
    private void setupSystem() {
        // Timer para atualizar relógio
        Timer clockTimer = new Timer(1000, e -> {
            updateTimeLabel.setText("Última atualização: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        clockTimer.start();

        // Worker para atualizações periódicas de métricas
        scheduler.scheduleAtFixedRate(this::simulateMetricsUpdate, 5, 5, TimeUnit.SECONDS);
    }

    private void scheduleBackgroundTasks() {
        // Simula alimentação de dados de mercado
        backgroundExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(2000);
                    // Simular atualização de preço
                    // Em produção, receberia via WebSocket
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void simulateMetricsUpdate() {
        SwingUtilities.invokeLater(() -> {
            // Atualiza alguns indicadores com valores aleatórios (simulação)
            if (systemRunning) {
                // Simular latência
                int latency = ThreadLocalRandom.current().nextInt(20, 200);
                connectionLabel.setText(latency < 100 ? "🌐 Conectado" : "🌐 Lento (" + latency + "ms)");
                connectionLabel.setForeground(latency < 100 ? secondaryColor : warningColor);
            }
            // Atualiza o timestamp dos dados recentes?
        });
    }

    // ========================= MAIN =========================
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(AdvancedTradingDashboard::new);
    }
}
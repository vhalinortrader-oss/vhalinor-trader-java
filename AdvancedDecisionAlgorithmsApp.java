import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Conversão do Python/Tkinter "Advanced Decision Algorithms App" para Java/Swing.
 * Estrutura mantida com enums, classes de dados e interface gráfica similar.
 * Melhorias: uso de SwingWorker para simulação assíncrona, javax.swing.Timer
 * para atualizações periódicas e tratamento adequado da Event Dispatch Thread.
 */
public class AdvancedDecisionAlgorithmsApp extends JFrame {

    // ======================== ENUMS ========================
    enum AlgorithmType {
        ML, STATISTICAL, HYBRID, QUANTUM, ENSEMBLE
    }

    enum NodeStatus {
        PROCESSING, COMPLETED, WAITING, ERROR
    }

    enum Decision {
        BUY, SELL, HOLD, CLOSE
    }

    // ======================== MODELOS DE DADOS ========================
    static class DecisionAlgorithm {
        String id, name, description;
        AlgorithmType type;
        double accuracy, speed, complexity, confidence;
        boolean isActive;
        int decisions;
        double successRate, avgResponseTime;
        List<String> specialization = new ArrayList<>();

        public DecisionAlgorithm(String id, String name, AlgorithmType type, String description,
                                 double accuracy, double speed, double complexity, double confidence,
                                 boolean isActive, int decisions, double successRate, double avgResponseTime,
                                 List<String> specialization) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.description = description;
            this.accuracy = accuracy;
            this.speed = speed;
            this.complexity = complexity;
            this.confidence = confidence;
            this.isActive = isActive;
            this.decisions = decisions;
            this.successRate = successRate;
            this.avgResponseTime = avgResponseTime;
            this.specialization = specialization;
        }
    }

    static class DecisionNode {
        String id, name, input, output;
        double confidence, executionTime;
        NodeStatus status;

        DecisionNode(String id, String name, String input, String output,
                     double confidence, double executionTime, NodeStatus status) {
            this.id = id;
            this.name = name;
            this.input = input;
            this.output = output;
            this.confidence = confidence;
            this.executionTime = executionTime;
            this.status = status;
        }
    }

    static class MarketDecision {
        String timestamp, algorithm, reasoning, timeframe;
        Decision decision;
        double confidence, risk, expectedReturn;
        List<String> factors = new ArrayList<>();

        MarketDecision(String timestamp, String algorithm, Decision decision, double confidence,
                       String reasoning, List<String> factors, double risk, double expectedReturn,
                       String timeframe) {
            this.timestamp = timestamp;
            this.algorithm = algorithm;
            this.decision = decision;
            this.confidence = confidence;
            this.reasoning = reasoning;
            this.factors = factors;
            this.risk = risk;
            this.expectedReturn = expectedReturn;
            this.timeframe = timeframe;
        }
    }

    // ======================== ATRIBUTOS DA APLICAÇÃO ========================
    private final List<DecisionAlgorithm> algorithms = new ArrayList<>();
    private final List<DecisionNode> decisionFlow = new ArrayList<>();
    private final List<MarketDecision> recentDecisions = new ArrayList<>();
    private final Random random = new Random();

    private JLabel executeBtn;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    private int currentDecisions = 0;
    private double systemLoad = 67.3;

    // Métricas
    private JLabel decisionsTodayLabel, successRateLabel, responseTimeLabel, systemLoadLabel;

    // Referências para componentes dinâmicos
    private JPanel algorithmsPanel;
    private JPanel flowPanel;
    private JPanel decisionsPanel;
    private List<AlgorithmCard> algorithmCards = new ArrayList<>();
    private List<FlowNodeWidget> flowWidgets = new ArrayList<>();

    // Cores
    static final Color SUCCESS_COLOR = new Color(0x10, 0xb9, 0x81);
    static final Color WARNING_COLOR = new Color(0xf5, 0x9e, 0x0b);
    static final Color ERROR_COLOR = new Color(0xef, 0x44, 0x44);
    static final Color INFO_COLOR = new Color(0x63, 0x66, 0xf1);
    static final Color PRIMARY_COLOR = new Color(0x3b, 0x82, 0xf6);
    static final Color BG_COLOR = new Color(0xf8, 0xfa, 0xfc);
    static final Color CARD_BG = Color.WHITE;

    // ======================== CONSTRUTOR ========================
    public AdvancedDecisionAlgorithmsApp() {
        setTitle("🧠 Algoritmos de Decisão Avançados");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        initData();
        buildUI();
        startPeriodicUpdates();
        setLocationRelativeTo(null);
    }

    // ======================== INICIALIZAÇÃO DE DADOS ========================
    private void initData() {
        algorithms.add(new DecisionAlgorithm(
                "ensemble", "Ensemble Multi-Algoritmo", AlgorithmType.ENSEMBLE,
                "Combina múltiplos algoritmos usando voting e stacking para decisões mais robustas",
                94.7, 85.2, 95.8, 89.3, true, 2847, 87.4, 0.23,
                List.of("Análise Multi-Modal", "Consenso Algorítmico", "Meta-Learning")
        ));
        algorithms.add(new DecisionAlgorithm(
                "quantum_nn", "Rede Neural Quântica", AlgorithmType.QUANTUM,
                "Utiliza computação quântica simulada para processamento paralelo de cenários",
                91.3, 92.7, 98.5, 85.9, true, 1623, 83.2, 0.08,
                List.of("Superposição", "Entrelaçamento", "Túnel Quântico")
        ));
        algorithms.add(new DecisionAlgorithm(
                "adaptive_lstm", "LSTM Adaptativo Profundo", AlgorithmType.ML,
                "Rede LSTM com arquitetura adaptativa que se reconfigura baseada em condições de mercado",
                88.9, 78.4, 87.3, 82.7, true, 3241, 79.6, 0.45,
                List.of("Séries Temporais", "Memória Longa", "Adaptação Dinâmica")
        ));
        algorithms.add(new DecisionAlgorithm(
                "bayesian_optimizer", "Otimizador Bayesiano Multi-Objetivo", AlgorithmType.STATISTICAL,
                "Optimiza múltiplos objetivos simultaneamente usando inferência bayesiana",
                86.4, 71.8, 82.1, 78.9, true, 1876, 81.3, 0.67,
                List.of("Otimização", "Incerteza", "Multi-Objetivo")
        ));
        algorithms.add(new DecisionAlgorithm(
                "reinforcement_agent", "Agente de Aprendizado por Reforço", AlgorithmType.ML,
                "Agente que aprende estratégias ótimas através de interação com o ambiente de mercado",
                89.7, 83.6, 91.4, 86.1, true, 2156, 84.8, 0.31,
                List.of("Q-Learning", "Policy Gradient", "Actor-Critic")
        ));
        algorithms.add(new DecisionAlgorithm(
                "fuzzy_expert", "Sistema Especialista Fuzzy", AlgorithmType.HYBRID,
                "Combina lógica fuzzy com regras de especialistas para decisões em condições de incerteza",
                84.2, 94.7, 73.9, 76.4, false, 1432, 77.9, 0.12,
                List.of("Lógica Fuzzy", "Regras Especialistas", "Incerteza")
        ));
        algorithms.add(new DecisionAlgorithm(
                "genetic_algorithm", "Algoritmo Genético Evolutivo", AlgorithmType.HYBRID,
                "Evolui estratégias de trading através de seleção natural e mutação genética",
                87.1, 76.3, 88.7, 81.5, true, 998, 82.7, 0.89,
                List.of("Evolução", "Otimização Genética", "Seleção Natural")
        ));

        decisionFlow.add(new DecisionNode("1", "Coleta de Dados", "Market Data", "Processed Data", 98.5, 0.02, NodeStatus.COMPLETED));
        decisionFlow.add(new DecisionNode("2", "Pré-processamento", "Raw Data", "Clean Data", 96.7, 0.08, NodeStatus.COMPLETED));
        decisionFlow.add(new DecisionNode("3", "Feature Engineering", "Clean Data", "Features", 94.2, 0.15, NodeStatus.COMPLETED));
        decisionFlow.add(new DecisionNode("4", "Análise Ensemble", "Features", "Predictions", 89.3, 0.23, NodeStatus.PROCESSING));
        decisionFlow.add(new DecisionNode("5", "Validação Cruzada", "Predictions", "Validated", 0, 0, NodeStatus.WAITING));
        decisionFlow.add(new DecisionNode("6", "Execução de Decisão", "Validated", "Action", 0, 0, NodeStatus.WAITING));

        recentDecisions.add(new MarketDecision(
                "2024-01-15 15:42:23", "Ensemble Multi-Algoritmo", Decision.BUY, 87.4,
                "Confluência de sinais: breakout técnico + momentum positivo + volume acima da média",
                List.of("RSI(14): 45.2", "MACD: Bullish Cross", "Volume: +234%", "Support: 42,100"),
                2.3, 5.7, "4H"
        ));
        recentDecisions.add(new MarketDecision(
                "2024-01-15 15:38:47", "Rede Neural Quântica", Decision.SELL, 92.1,
                "Padrão de reversão detectado com alta probabilidade baseado em análise quântica",
                List.of("Quantum State: Bearish", "Volatility: Increasing", "Resistance: 42,800", "Divergence: Confirmed"),
                1.8, 4.2, "1H"
        ));
        recentDecisions.add(new MarketDecision(
                "2024-01-15 15:35:12", "LSTM Adaptativo", Decision.HOLD, 76.8,
                "Mercado em consolidação, aguardando breakout definitivo da faixa atual",
                List.of("Trend: Sideways", "ATR: Low", "Volume: Decreasing", "Support/Resistance: Strong"),
                1.2, 2.1, "2H"
        ));
    }

    // ======================== CONSTRUÇÃO DA UI ========================
    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BG_COLOR);

        // Cabeçalho
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Métricas
        JPanel metricsPanel = createMetricsPanel();
        mainPanel.add(metricsPanel, BorderLayout.CENTER);

        // Abas (Notebook)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));

        algorithmsPanel = createAlgorithmsTab();
        tabbedPane.addTab("🤖 Algoritmos", algorithmsPanel);

        flowPanel = createFlowTab();
        tabbedPane.addTab("🔀 Fluxo de Decisão", flowPanel);

        decisionsPanel = createDecisionsTab();
        tabbedPane.addTab("📈 Decisões Recentes", decisionsPanel);

        JPanel tabsPanel = new JPanel(new BorderLayout());
        tabsPanel.add(tabbedPane, BorderLayout.CENTER);
        tabsPanel.setBackground(BG_COLOR);

        // Painel de consenso
        JPanel consensusPanel = createConsensusPanel();

        // Organização vertical: métricas -> abas -> consenso
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(metricsPanel, BorderLayout.NORTH);
        centerPanel.add(tabsPanel, BorderLayout.CENTER);
        centerPanel.add(consensusPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("🧠 Algoritmos de Decisão Avançados");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(titleLabel, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(BG_COLOR);

        int activeCount = (int) algorithms.stream().filter(a -> a.isActive).count();
        JLabel statusLabel = new JLabel("🔧 " + activeCount + " Ativos");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 10));
        statusLabel.setForeground(SUCCESS_COLOR);
        controlPanel.add(statusLabel);

        executeBtn = new JButton("⚡ Executar Decisão");
        executeBtn.setBackground(PRIMARY_COLOR);
        executeBtn.setForeground(Color.WHITE);
        executeBtn.setFocusPainted(false);
        executeBtn.addActionListener(e -> runDecisionProcess());
        controlPanel.add(executeBtn);

        header.add(controlPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "📊 Métricas do Sistema",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(CARD_BG);

        decisionsTodayLabel = createMetricBox(panel, "0", "Decisões Hoje", INFO_COLOR);
        successRateLabel = createMetricBox(panel, "0%", "Taxa Sucesso Média", SUCCESS_COLOR);
        responseTimeLabel = createMetricBox(panel, "0s", "Tempo Resposta", WARNING_COLOR);
        systemLoadLabel = createMetricBox(panel, "67.3%", "Carga Sistema", ERROR_COLOR);

        updateMetricsDisplay();
        return panel;
    }

    private JLabel createMetricBox(JPanel parent, String value, String label, Color color) {
        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(color);
        box.add(valueLabel, gbc);

        gbc.gridy = 1;
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        box.add(descLabel, gbc);

        parent.add(box);
        return valueLabel;
    }

    // ======================== ABA ALGORITMOS ========================
    private JPanel createAlgorithmsTab() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_COLOR);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        for (DecisionAlgorithm algo : algorithms) {
            AlgorithmCard card = new AlgorithmCard(algo);
            algorithmCards.add(card);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(8));
        }

        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    class AlgorithmCard extends JPanel {
        private final DecisionAlgorithm algo;
        private final JToggleButton toggleBtn;
        private final JLabel statusLabel;

        AlgorithmCard(DecisionAlgorithm algo) {
            this.algo = algo;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            setBackground(CARD_BG);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

            // Cabeçalho
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(CARD_BG);
            JLabel nameLabel = new JLabel(algo.name + " (" + algo.type + ")");
            nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
            header.add(nameLabel, BorderLayout.WEST);

            statusLabel = new JLabel(algo.isActive ? "● ATIVO" : "● INATIVO");
            statusLabel.setForeground(algo.isActive ? SUCCESS_COLOR : Color.GRAY);
            header.add(statusLabel, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            // Descrição
            JTextArea descArea = new JTextArea(algo.description);
            descArea.setWrapStyleWord(true);
            descArea.setLineWrap(true);
            descArea.setEditable(false);
            descArea.setBackground(CARD_BG);
            descArea.setFont(new Font("Arial", Font.PLAIN, 9));
            add(descArea, BorderLayout.CENTER);

            // Métricas e controles
            JPanel bottom = new JPanel(new BorderLayout(10, 5));
            bottom.setBackground(CARD_BG);

            JPanel barsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
            barsPanel.setBackground(CARD_BG);
            addMetricBar(barsPanel, "Precisão", algo.accuracy, SUCCESS_COLOR);
            addMetricBar(barsPanel, "Velocidade", algo.speed, PRIMARY_COLOR);
            addMetricBar(barsPanel, "Complexidade", algo.complexity, WARNING_COLOR);
            addMetricBar(barsPanel, "Confiança", algo.confidence, INFO_COLOR);
            bottom.add(barsPanel, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            controlPanel.setBackground(CARD_BG);
            toggleBtn = new JToggleButton(algo.isActive ? "Desativar" : "Ativar");
            toggleBtn.addActionListener(e -> toggleAlgorithm());
            controlPanel.add(toggleBtn);
            bottom.add(controlPanel, BorderLayout.EAST);

            add(bottom, BorderLayout.SOUTH);
        }

        private void addMetricBar(JPanel parent, String name, double value, Color color) {
            JPanel row = new JPanel(new BorderLayout(5, 0));
            row.setBackground(CARD_BG);
            JLabel label = new JLabel(name + ": " + String.format("%.1f%%", value));
            label.setFont(new Font("Arial", Font.PLAIN, 9));
            row.add(label, BorderLayout.NORTH);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) value);
            bar.setForeground(color);
            bar.setStringPainted(false);
            row.add(bar, BorderLayout.CENTER);
            parent.add(row);
        }

        private void toggleAlgorithm() {
            algo.isActive = !algo.isActive;
            toggleBtn.setText(algo.isActive ? "Desativar" : "Ativar");
            statusLabel.setText(algo.isActive ? "● ATIVO" : "● INATIVO");
            statusLabel.setForeground(algo.isActive ? SUCCESS_COLOR : Color.GRAY);
            updateMetricsDisplay();
        }
    }

    // ======================== ABA FLUXO ========================
    private JPanel createFlowTab() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);
        JLabel title = new JLabel("🔀 Fluxo de Processamento de Decisão");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        container.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_COLOR);

        for (DecisionNode node : decisionFlow) {
            FlowNodeWidget widget = new FlowNodeWidget(node);
            flowWidgets.add(widget);
            listPanel.add(widget);
            listPanel.add(Box.createVerticalStrut(3));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    class FlowNodeWidget extends JPanel {
        private final DecisionNode node;
        private final JLabel iconLabel;

        FlowNodeWidget(DecisionNode node) {
            this.node = node;
            setLayout(new BorderLayout(10, 0));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            setBackground(CARD_BG);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            iconLabel = new JLabel(getStatusIcon(node.status));
            iconLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            add(iconLabel, BorderLayout.WEST);

            JPanel info = new JPanel(new GridLayout(2, 1));
            info.setBackground(CARD_BG);
            JLabel nameLabel = new JLabel(node.name);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 10));
            info.add(nameLabel);
            JLabel flowLabel = new JLabel(node.input + " → " + node.output);
            flowLabel.setFont(new Font("Arial", Font.PLAIN, 8));
            info.add(flowLabel);
            add(info, BorderLayout.CENTER);

            JPanel metricsBox = new JPanel(new GridLayout(2, 1));
            metricsBox.setBackground(CARD_BG);
            JLabel confLabel = new JLabel(node.confidence > 0 ? String.format("%.1f%%", node.confidence) : "-");
            confLabel.setFont(new Font("Arial", Font.BOLD, 10));
            confLabel.setForeground(INFO_COLOR);
            metricsBox.add(confLabel);
            JLabel timeLabel = new JLabel(node.executionTime > 0 ? node.executionTime + "s" : "-");
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 8));
            metricsBox.add(timeLabel);
            add(metricsBox, BorderLayout.EAST);
        }

        void updateIcon() {
            iconLabel.setText(getStatusIcon(node.status));
        }
    }

    private String getStatusIcon(NodeStatus status) {
        return switch (status) {
            case COMPLETED -> "✅";
            case PROCESSING -> "🔄";
            case WAITING -> "⏳";
            case ERROR -> "❌";
        };
    }

    // ======================== ABA DECISÕES ========================
    private JPanel createDecisionsTab() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);
        JLabel title = new JLabel("📈 Decisões Recentes");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        container.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_COLOR);

        for (MarketDecision dec : recentDecisions) {
            listPanel.add(createDecisionCard(dec));
            listPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    private JPanel createDecisionCard(MarketDecision dec) {
        String icon = switch (dec.decision) {
            case BUY -> "📈";
            case SELL -> "📉";
            case HOLD -> "🎯";
            case CLOSE -> "👁";
        };
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(CARD_BG);

        JLabel titleLabel = new JLabel(icon + " " + dec.decision + " - " + dec.confidence + "% confiança");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        card.add(titleLabel, BorderLayout.NORTH);

        JTextArea reasoningArea = new JTextArea(dec.reasoning);
        reasoningArea.setWrapStyleWord(true);
        reasoningArea.setLineWrap(true);
        reasoningArea.setEditable(false);
        reasoningArea.setBackground(CARD_BG);
        reasoningArea.setFont(new Font("Arial", Font.PLAIN, 9));
        card.add(reasoningArea, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.setBackground(CARD_BG);
        bottom.add(new JLabel("Fatores: " + String.join(" | ", dec.factors)));
        JPanel metricsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        metricsRow.setBackground(CARD_BG);
        metricsRow.add(new JLabel("Risco: " + dec.risk + "%"));
        ((JLabel) metricsRow.getComponent(0)).setForeground(ERROR_COLOR);
        metricsRow.add(new JLabel("Retorno: " + dec.expectedReturn + "%"));
        ((JLabel) metricsRow.getComponent(1)).setForeground(SUCCESS_COLOR);
        metricsRow.add(new JLabel("Timeframe: " + dec.timeframe));
        ((JLabel) metricsRow.getComponent(2)).setForeground(INFO_COLOR);
        bottom.add(metricsRow);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    // ======================== PAINEL DE CONSENSO ========================
    private JPanel createConsensusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(CARD_BG);

        JLabel title = new JLabel("🔗 Sistema de Consenso Algorítmico");
        title.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(title, BorderLayout.NORTH);

        JTextArea desc = new JTextArea("O sistema combina decisões de múltiplos algoritmos usando votação ponderada e análise de consenso para produzir decisões mais robustas e confiáveis.");
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setBackground(CARD_BG);
        desc.setFont(new Font("Arial", Font.PLAIN, 9));
        panel.add(desc, BorderLayout.CENTER);

        JPanel metricsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsRow.setBackground(CARD_BG);
        addConsensusMetric(metricsRow, "89.7%", "Consenso Atual");
        int activeCount = (int) algorithms.stream().filter(a -> a.isActive).count();
        addConsensusMetric(metricsRow, String.valueOf(activeCount), "Algoritmos Ativos");
        addConsensusMetric(metricsRow, "0.31s", "Tempo Consenso");
        panel.add(metricsRow, BorderLayout.SOUTH);

        return panel;
    }

    private void addConsensusMetric(JPanel parent, String value, String label) {
        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valLabel.setForeground(INFO_COLOR);
        box.add(valLabel, gbc);
        gbc.gridy = 1;
        box.add(new JLabel(label), gbc);
        parent.add(box);
    }

    // ======================== ATUALIZAÇÕES PERIÓDICAS ========================
    private void startPeriodicUpdates() {
        Timer timer = new Timer(2000, e -> {
            currentDecisions += random.nextInt(4);
            systemLoad = Math.max(30, Math.min(95, systemLoad + (random.nextDouble() - 0.5) * 6));
            for (DecisionNode node : decisionFlow) {
                if (node.status == NodeStatus.PROCESSING) {
                    node.confidence = Math.min(100, node.confidence + random.nextDouble() * 2);
                }
            }
            updateMetricsDisplay();
            updateFlowIcons();
        });
        timer.start();
    }

    private void updateMetricsDisplay() {
        List<DecisionAlgorithm> activeAlgos = algorithms.stream().filter(a -> a.isActive).toList();
        double avgSuccess = activeAlgos.stream().mapToDouble(a -> a.successRate).average().orElse(0);
        double avgResponse = activeAlgos.stream().mapToDouble(a -> a.avgResponseTime).average().orElse(0);
        decisionsTodayLabel.setText(String.valueOf(currentDecisions));
        successRateLabel.setText(String.format("%.1f%%", avgSuccess));
        responseTimeLabel.setText(String.format("%.2fs", avgResponse));
        systemLoadLabel.setText(String.format("%.1f%%", systemLoad));
    }

    private void updateFlowIcons() {
        for (FlowNodeWidget widget : flowWidgets) {
            widget.updateIcon();
        }
    }

    // ======================== EXECUÇÃO DO PROCESSO DE DECISÃO ========================
    private void runDecisionProcess() {
        if (isProcessing.get()) return;
        isProcessing.set(true);
        executeBtn.setText("🔄 Processando...");
        executeBtn.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < decisionFlow.size(); i++) {
                    Thread.sleep(800);
                    for (int j = 0; j < decisionFlow.size(); j++) {
                        DecisionNode node = decisionFlow.get(j);
                        if (j == i) {
                            node.status = NodeStatus.PROCESSING;
                            node.confidence = Math.min(100, node.confidence + random.nextDouble() * 15);
                        } else if (j < i) {
                            node.status = NodeStatus.COMPLETED;
                            if (node.confidence == 0) {
                                node.confidence = 85 + random.nextDouble() * 14;
                                node.executionTime = 0.1 + random.nextDouble() * 0.4;
                            }
                        } else {
                            node.status = NodeStatus.WAITING;
                        }
                    }
                    // Atualiza UI na thread de eventos
                    SwingUtilities.invokeLater(() -> {
                        updateFlowIcons();
                    });
                }
                // Finaliza todos os nós
                for (DecisionNode node : decisionFlow) {
                    node.status = NodeStatus.COMPLETED;
                    if (node.confidence == 0) {
                        node.confidence = 85 + random.nextDouble() * 14;
                        node.executionTime = 0.1 + random.nextDouble() * 0.4;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                isProcessing.set(false);
                executeBtn.setText("⚡ Executar Decisão");
                executeBtn.setEnabled(true);
                updateFlowIcons();
                JOptionPane.showMessageDialog(AdvancedDecisionAlgorithmsApp.this,
                        "Processo de decisão executado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }

    // ======================== MAIN ========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdvancedDecisionAlgorithmsApp app = new AdvancedDecisionAlgorithmsApp();
            app.setVisible(true);
        });
    }
}
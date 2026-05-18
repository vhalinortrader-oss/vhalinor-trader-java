package com.vhalinor.iag.visualizer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * VHALINOR IAG 5.0 - VISUALIZADOR QUÂNTICO AVANÇADO (Java Edition)
 * 
 * Tradução do sistema de visualização de rede neural com IA integrada para Java.
 * 
 * Principais funcionalidades:
 * - Visualização 2D interativa da rede neural
 * - Geração de arquiteturas predefinidas (Vhalinor Quantum, Deep Learning, LSTM, etc.)
 * - Múltiplos layouts (hierárquico, circular)
 * - Seleção de neurônios, animação
 * - Estatísticas da rede
 * - Temas de cores
 * - Exportação/Importação de redes em JSON
 *
 * @author Alex Miranda Sales
 * @version 5.0.0 Java
 * @since 2026
 */
public class NeuralNetworkVisualizer extends JFrame {

    // ==================== ENUMS ====================
    public enum VisualizationMode {
        AUTO, FORCE_2D, FORCE_3D, SPRING, CIRCULAR, SPECTRAL, HIERARCHICAL, FORCE
    }

    public enum NodeShape {
        CIRCLE, SQUARE, DIAMOND, TRIANGLE, STAR, HEXAGON
    }

    public enum EdgeStyle {
        SOLID, DASHED, DOTTED, DASHDOT
    }

    public enum AnimationSpeed {
        SLOW(2000), NORMAL(1000), FAST(500), VERY_FAST(200), REAL_TIME(50);
        private final int delay;

        AnimationSpeed(int delay) {
            this.delay = delay;
        }

        public int getDelay() {
            return delay;
        }
    }

    public enum Theme {
        DARK, LIGHT, BLUE, GREEN, PURPLE, QUANTUM, CUSTOM
    }

    // ==================== DATA CLASSES ====================
    public static class Neuron {
        private final int id;
        private String name;
        private String type;
        private String layer;
        private int layerIndex;
        private double activation;
        private double bias;
        private final List<Integer> connections;
        private double x, y, z; // position (z for 3D future)
        private String color;
        private double size;
        private double importance;
        private double error;
        private double gradient;
        private Date timestamp;
        private Map<String, Object> metadata;

        public Neuron(int id, String name, String type, String layer, int layerIndex) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.layer = layer;
            this.layerIndex = layerIndex;
            this.activation = 0.5;
            this.bias = 0.0;
            this.connections = new ArrayList<>();
            this.color = "#CCCCCC";
            this.size = 1.0;
            this.importance = 1.0;
            this.error = 0.0;
            this.gradient = 0.0;
            this.timestamp = new Date();
            this.metadata = new HashMap<>();
        }

        // Getters e Setters
        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getLayer() { return layer; }
        public void setLayer(String layer) { this.layer = layer; }
        public double getActivation() { return activation; }
        public void setActivation(double activation) { this.activation = activation; }
        public double getBias() { return bias; }
        public void setBias(double bias) { this.bias = bias; }
        public List<Integer> getConnections() { return connections; }
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        public double getZ() { return z; }
        public void setZ(double z) { this.z = z; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public double getSize() { return size; }
        public void setSize(double size) { this.size = size; }
        public double getImportance() { return importance; }
        public void setImportance(double importance) { this.importance = importance; }
        public double getError() { return error; }
        public void setError(double error) { this.error = error; }
        public double getGradient() { return gradient; }
        public void setGradient(double gradient) { this.gradient = gradient; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public int getLayerIndex() { return layerIndex; }
        public void setLayerIndex(int layerIndex) { this.layerIndex = layerIndex; }
    }

    public static class Connection {
        private final int source;
        private final int target;
        private double weight;
        private double strength;
        private String type; // forward, recurrent
        private boolean recurrent;
        private boolean enabled;
        private String color;
        private String style; // "-", "--", ":", "-."
        private double width;
        private Date timestamp;
        private Map<String, Object> metadata;

        public Connection(int source, int target, double weight, boolean recurrent) {
            this.source = source;
            this.target = target;
            this.weight = weight;
            this.recurrent = recurrent;
            this.strength = weight;
            this.type = recurrent ? "recurrent" : "forward";
            this.enabled = true;
            this.color = recurrent ? "#FF00FF" : "#00FF00";
            this.style = recurrent ? "--" : "-";
            this.width = weight * 2;
            this.timestamp = new Date();
            this.metadata = new HashMap<>();
        }

        public int getSource() { return source; }
        public int getTarget() { return target; }
        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
        public boolean isRecurrent() { return recurrent; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public double getWidth() { return width; }
        public void setWidth(double width) { this.width = width; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public double getStrength() { return strength; }
        public void setStrength(double strength) { this.strength = strength; }
        public String getStyle() { return style; }
        public void setStyle(String style) { this.style = style; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }

    public static class NetworkStatistics {
        public int totalNeurons;
        public int totalConnections;
        public int totalLayers;
        public Map<String, Integer> neuronsByType = new HashMap<>();
        public Map<String, Integer> neuronsByLayer = new HashMap<>();
        public Map<String, Integer> connectionsByType = new HashMap<>();
        public double avgActivation;
        public double maxActivation;
        public double minActivation;
        public double avgWeight;
        public double maxWeight;
        public double minWeight;
        public double density;
        public double clusteringCoefficient;
        public double avgPathLength;
        public Date timestamp = new Date();
    }

    // ==================== COMPONENTS ====================
    // Network data
    private Map<Integer, Neuron> neurons = new HashMap<>();
    private List<Connection> connections = new ArrayList<>();
    private Map<String, List<Integer>> layers = new LinkedHashMap<>();
    private NetworkStatistics stats = new NetworkStatistics();

    // Visual state
    private VisualizationMode viewMode = VisualizationMode.HIERARCHICAL;
    private String currentLayout = "hierarchical";
    private Theme currentTheme = Theme.DARK;
    private ThemeManager themeManager = new ThemeManager();
    private boolean showLabels = true;
    private boolean showWeights = false;
    private boolean showActivations = true;
    private boolean animationEnabled = false;
    private AnimationSpeed animationSpeed = AnimationSpeed.NORMAL;
    private javax.swing.Timer animationTimer;
    private Set<Integer> selectedNeurons = new HashSet<>();
    private Set<Point> highlightedConnections = new HashSet<>(); // (source,target) as Point

    // UI components
    private NetworkCanvas networkCanvas;
    private JTextArea infoTextArea;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JComboBox<String> architectureCombo;
    private JComboBox<String> filterTypeCombo;
    private JComboBox<String> filterLayerCombo;
    private JCheckBox showLabelsCheck;
    private JCheckBox showWeightsCheck;
    private JCheckBox showActivationsCheck;
    private JCheckBox highlightImportantCheck;
    private JComboBox<String> speedCombo;
    private JLabel neuronCountLabel;
    private JLabel connCountLabel;

    // Logger simples para a área de texto
    private void log(String message, String level) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        String formatted = String.format("[%s] [%s] %s%n", timestamp, level, message);
        if (infoTextArea != null) {
            infoTextArea.append(formatted);
            infoTextArea.setCaretPosition(infoTextArea.getDocument().getLength());
        }
        System.out.print(formatted);
    }

    private void logInfo(String message) { log(message, "INFO"); }
    private void logWarning(String message) { log(message, "WARNING"); }
    private void logError(String message) { log(message, "ERROR"); }
    private void logSuccess(String message) { log(message, "SUCCESS"); }

    public NeuralNetworkVisualizer() {
        setTitle("VHALINOR IAG 5.0 - Visualizador Quântico de Rede Neural (Java)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setMinimumSize(new Dimension(1200, 800));
        setupUI();
        setupMenuBar();
        loadDefaultNetwork();
        updateStatistics();
        logSuccess("Visualizador VHALINOR IAG inicializado com sucesso");
    }

    private void setupUI() {
        // Main split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);
        add(mainSplitPane, BorderLayout.CENTER);

        // Left panel - Controls
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JScrollPane leftScroll = new JScrollPane(leftPanel);
        leftScroll.setPreferredSize(new Dimension(300, 0));
        mainSplitPane.setLeftComponent(leftScroll);

        // Right panel - Visualization
        JPanel rightPanel = new JPanel(new BorderLayout());
        networkCanvas = new NetworkCanvas();
        rightPanel.add(networkCanvas, BorderLayout.CENTER);
        mainSplitPane.setRightComponent(rightPanel);

        // Control sections
        leftPanel.add(createControlsPanel());
        leftPanel.add(createArchitecturePanel());
        leftPanel.add(createVisualizationPanel());
        leftPanel.add(createFilterPanel());
        leftPanel.add(createInfoPanel());

        // Status bar
        statusLabel = new JLabel("✅ Pronto");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);

        // Progress bar (hidden)
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        add(progressBar, BorderLayout.NORTH);

        setupKeyBindings();
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("🎮 Controles"));
        panel.setLayout(new GridLayout(0, 3, 5, 5));

        JButton refreshBtn = new JButton("🔄 Atualizar");
        refreshBtn.addActionListener(e -> refreshNetwork());
        JButton statsBtn = new JButton("📊 Estatísticas");
        statsBtn.addActionListener(e -> showStatisticsDialog());
        JButton searchBtn = new JButton("🔍 Buscar");
        searchBtn.addActionListener(e -> searchNeuron());
        JButton exportBtn = new JButton("💾 Exportar");
        exportBtn.addActionListener(e -> exportNetwork());
        JButton importBtn = new JButton("📁 Importar");
        importBtn.addActionListener(e -> importNetwork());
        JButton themeBtn = new JButton("🎨 Tema");
        themeBtn.addActionListener(e -> cycleTheme());
        JButton layoutBtn = new JButton("🎭 Layout");
        layoutBtn.addActionListener(e -> cycleLayout());
        JButton animateBtn = new JButton("⚡ Animar");
        animateBtn.addActionListener(e -> toggleAnimation());
        JButton screenshotBtn = new JButton("📸 Screenshot");
        screenshotBtn.addActionListener(e -> takeScreenshot());
        JButton clearBtn = new JButton("🧹 Limpar");
        clearBtn.addActionListener(e -> clearSelection());

        panel.add(refreshBtn);
        panel.add(statsBtn);
        panel.add(searchBtn);
        panel.add(exportBtn);
        panel.add(importBtn);
        panel.add(themeBtn);
        panel.add(layoutBtn);
        panel.add(animateBtn);
        panel.add(screenshotBtn);
        panel.add(clearBtn);

        return panel;
    }

    private JPanel createArchitecturePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("🏗️ Arquitetura"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Modelo:");
        panel.add(label);
        architectureCombo = new JComboBox<>(new String[]{
                "vhalinor_quantum", "deep_learning", "lstm_memory",
                "convolutional", "lex_iag"
        });
        architectureCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, architectureCombo.getPreferredSize().height));
        panel.add(architectureCombo);
        JButton loadBtn = new JButton("🚀 Carregar Arquitetura");
        loadBtn.addActionListener(e -> loadArchitecture((String) architectureCombo.getSelectedItem()));
        panel.add(loadBtn);
        return panel;
    }

    private JPanel createVisualizationPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("👁️ Visualização"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        showLabelsCheck = new JCheckBox("Mostrar Labels", true);
        showLabelsCheck.addActionListener(e -> { showLabels = showLabelsCheck.isSelected(); redraw(); });
        showWeightsCheck = new JCheckBox("Mostrar Pesos", false);
        showWeightsCheck.addActionListener(e -> { showWeights = showWeightsCheck.isSelected(); redraw(); });
        showActivationsCheck = new JCheckBox("Mostrar Ativações", true);
        showActivationsCheck.addActionListener(e -> { showActivations = showActivationsCheck.isSelected(); redraw(); });
        highlightImportantCheck = new JCheckBox("Destacar Importantes", false);
        highlightImportantCheck.addActionListener(e -> redraw());

        panel.add(showLabelsCheck);
        panel.add(showWeightsCheck);
        panel.add(showActivationsCheck);
        panel.add(highlightImportantCheck);

        panel.add(Box.createVerticalStrut(10));
        JLabel speedLabel = new JLabel("Velocidade:");
        panel.add(speedLabel);
        speedCombo = new JComboBox<>(new String[]{"Muito Lento", "Lento", "Normal", "Rápido", "Tempo Real"});
        speedCombo.setSelectedIndex(2);
        speedCombo.addActionListener(e -> setAnimationSpeed((String) speedCombo.getSelectedItem()));
        speedCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, speedCombo.getPreferredSize().height));
        panel.add(speedCombo);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Filtros"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel typeLabel = new JLabel("Tipo:");
        panel.add(typeLabel);
        filterTypeCombo = new JComboBox<>(new String[]{"Todos", "sensory", "processing", "memory",
                "decision", "output", "quantum", "analytical"});
        filterTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, filterTypeCombo.getPreferredSize().height));
        panel.add(filterTypeCombo);
        filterTypeCombo.addActionListener(e -> applyFilter());

        JLabel layerLabel = new JLabel("Camada:");
        panel.add(layerLabel);
        filterLayerCombo = new JComboBox<>(new String[]{"Todas"});
        filterLayerCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, filterLayerCombo.getPreferredSize().height));
        panel.add(filterLayerCombo);
        filterLayerCombo.addActionListener(e -> applyFilter());

        JButton applyBtn = new JButton("✅ Aplicar Filtro");
        applyBtn.addActionListener(e -> applyFilter());
        JButton clearBtn = new JButton("❌ Limpar Filtros");
        clearBtn.addActionListener(e -> clearFilters());
        panel.add(applyBtn);
        panel.add(clearBtn);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📋 Informações"));
        infoTextArea = new JTextArea(12, 20);
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new Font("Consolas", Font.PLAIN, 9));
        infoTextArea.setBackground(Color.decode("#1a1a1a"));
        infoTextArea.setForeground(Color.decode("#00ff00"));
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // File menu
        JMenu fileMenu = new JMenu("Arquivo");
        JMenuItem importItem = new JMenuItem("Importar Rede...");
        importItem.addActionListener(e -> importNetwork());
        JMenuItem exportItem = new JMenuItem("Exportar Rede...");
        exportItem.addActionListener(e -> exportNetwork());
        JMenuItem screenshotItem = new JMenuItem("Exportar Imagem...");
        screenshotItem.addActionListener(e -> takeScreenshot());
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.add(screenshotItem);
        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Sair");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // View menu
        JMenu viewMenu = new JMenu("Visualização");
        JMenuItem hierarchicalItem = new JMenuItem("Layout Hierárquico");
        hierarchicalItem.addActionListener(e -> setLayout("hierarchical"));
        JMenuItem circularItem = new JMenuItem("Layout Circular");
        circularItem.addActionListener(e -> setLayout("circular"));
        viewMenu.add(hierarchicalItem);
        viewMenu.add(circularItem);
        JMenu themeMenu = new JMenu("Temas");
        for (Theme theme : Theme.values()) {
            if (theme != Theme.CUSTOM) {
                JMenuItem item = new JMenuItem(theme.name());
                item.addActionListener(e -> setTheme(theme));
                themeMenu.add(item);
            }
        }
        viewMenu.add(themeMenu);
        menuBar.add(viewMenu);

        // Analysis menu
        JMenu analysisMenu = new JMenu("Análise");
        JMenuItem statsItem = new JMenuItem("Estatísticas");
        statsItem.addActionListener(e -> showStatisticsDialog());
        JMenuItem importantItem = new JMenuItem("Neurônios Importantes");
        importantItem.addActionListener(e -> showImportantNeurons());
        analysisMenu.add(statsItem);
        analysisMenu.add(importantItem);
        menuBar.add(analysisMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Ajuda");
        JMenuItem aboutItem = new JMenuItem("Sobre");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void setupKeyBindings() {
        // Keyboard shortcuts using InputMap/ActionMap
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "refresh");
        actionMap.put("refresh", new AbstractAction() { public void actionPerformed(ActionEvent e) { refreshNetwork(); } });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "stats");
        actionMap.put("stats", new AbstractAction() { public void actionPerformed(ActionEvent e) { showStatisticsDialog(); } });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "search");
        actionMap.put("search", new AbstractAction() { public void actionPerformed(ActionEvent e) { searchNeuron(); } });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "animate");
        actionMap.put("animate", new AbstractAction() { public void actionPerformed(ActionEvent e) { toggleAnimation(); } });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "layout");
        actionMap.put("layout", new AbstractAction() { public void actionPerformed(ActionEvent e) { cycleLayout(); } });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "theme");
        actionMap.put("theme", new AbstractAction() { public void actionPerformed(ActionEvent e) { cycleTheme(); } });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear");
        actionMap.put("clear", new AbstractAction() { public void actionPerformed(ActionEvent e) { clearSelection(); } });
    }

    // ==================== NETWORK GENERATION ====================
    private void loadDefaultNetwork() {
        // Carrega arquitetura LEX_IAG
        loadLexIAGNetwork();
    }

    private void loadArchitecture(String archName) {
        logInfo("Carregando arquitetura: " + archName);
        if ("lex_iag".equals(archName)) {
            loadLexIAGNetwork();
        } else {
            generateNetwork(archName);
        }
    }

    private void loadLexIAGNetwork() {
        // Implementação simplificada da arquitetura LEX IAG
        neurons.clear();
        connections.clear();
        layers.clear();

        // Define layers
        String[] layerNames = {"Input", "Analysis", "Memory", "Quantum", "Decision", "Output"};
        for (String layer : layerNames) {
            layers.put(layer, new ArrayList<>());
        }

        int idCounter = 0;
        Random rand = new Random(42);

        // Input layer
        String[][] inputSpecs = {{"market_data","sensory"},{"crypto_analysis","sensory"},{"forex_analysis","sensory"},
                                  {"arbitrage_analysis","sensory"},{"sentiment_analysis","sensory"},{"news_processor","sensory"}};
        for (String[] spec : inputSpecs) {
            Neuron n = new Neuron(idCounter, spec[0], spec[1], "Input", 0);
            n.setActivation(rand.nextDouble() * 0.4 + 0.5);
            n.setColor(getNeuronColor(spec[1]));
            neurons.put(idCounter, n);
            layers.get("Input").add(idCounter);
            idCounter++;
        }

        // Analysis layer
        String[][] analysisSpecs = {{"data_analyzer","analytical"},{"pattern_recognizer","analytical"},
                                     {"technical_analyzer","analytical"},{"risk_analyzer","analytical"},
                                     {"statistical_analyzer","analytical"},{"ml_analyzer","processing"}};
        for (String[] spec : analysisSpecs) {
            Neuron n = new Neuron(idCounter, spec[0], spec[1], "Analysis", 1);
            n.setActivation(rand.nextDouble() * 0.4 + 0.4);
            n.setColor(getNeuronColor(spec[1]));
            neurons.put(idCounter, n);
            layers.get("Analysis").add(idCounter);
            idCounter++;
        }

        // Memory layer
        String[][] memorySpecs = {{"advanced_memory","memory"},{"neural_connection_matrix","memory"},
                                   {"brain_memory_db","database"},{"cache_system","memory"},{"experience_replay","memory"}};
        for (String[] spec : memorySpecs) {
            Neuron n = new Neuron(idCounter, spec[0], spec[1], "Memory", 2);
            n.setActivation(rand.nextDouble() * 0.4 + 0.3);
            n.setColor(getNeuronColor(spec[1]));
            neurons.put(idCounter, n);
            layers.get("Memory").add(idCounter);
            idCounter++;
        }

        // Quantum layer
        String[][] quantumSpecs = {{"quantum_core","quantum"},{"quantum_algorithms","quantum"},
                                    {"quantum_processor","quantum"},{"qubit_controller","quantum"},{"quantum_memory","quantum"}};
        for (String[] spec : quantumSpecs) {
            Neuron n = new Neuron(idCounter, spec[0], spec[1], "Quantum", 3);
            n.setActivation(rand.nextDouble() * 0.35 + 0.6);
            n.setColor(getNeuronColor(spec[1]));
            neurons.put(idCounter, n);
            layers.get("Quantum").add(idCounter);
            idCounter++;
        }

        // Decision layer
        String[][] decisionSpecs = {{"decision_engine","decision"},{"autonomous_manager","decision"},
                                     {"strategy_selector","decision"},{"risk_manager","decision"},{"execution_controller","control"}};
        for (String[] spec : decisionSpecs) {
            Neuron n = new Neuron(idCounter, spec[0], spec[1], "Decision", 4);
            n.setActivation(rand.nextDouble() * 0.35 + 0.5);
            n.setColor(getNeuronColor(spec[1]));
            neurons.put(idCounter, n);
            layers.get("Decision").add(idCounter);
            idCounter++;
        }

        // Output layer
        String[][] outputSpecs = {{"autotrader","output"},{"trading_controller","output"},
                                   {"api_interface","api"},{"report_generator","output"},{"alert_system","output"}};
        for (String[] spec : outputSpecs) {
            Neuron n = new Neuron(idCounter, spec[0], spec[1], "Output", 5);
            n.setActivation(rand.nextDouble() * 0.4 + 0.4);
            n.setColor(getNeuronColor(spec[1]));
            neurons.put(idCounter, n);
            layers.get("Output").add(idCounter);
            idCounter++;
        }

        // Build connections between layers
        for (int i = 0; i < layerNames.length - 1; i++) {
            List<Integer> srcLayer = layers.get(layerNames[i]);
            List<Integer> dstLayer = layers.get(layerNames[i+1]);
            for (int srcId : srcLayer) {
                int numConn = Math.max(2, (int)(dstLayer.size() * 0.5));
                Collections.shuffle(dstLayer, rand);
                for (int j = 0; j < Math.min(numConn, dstLayer.size()); j++) {
                    int dstId = dstLayer.get(j);
                    double weight = rand.nextDouble() * 0.7 + 0.3;
                    Connection conn = new Connection(srcId, dstId, weight, false);
                    connections.add(conn);
                    neurons.get(srcId).getConnections().add(dstId);
                }
            }
        }

        // Recurrent connections
        for (int i = 0; i < 10; i++) {
            int src = rand.nextInt(idCounter);
            int dst = rand.nextInt(idCounter);
            if (src != dst && !neurons.get(src).getConnections().contains(dst)) {
                double weight = rand.nextDouble() * 0.4 + 0.1;
                Connection conn = new Connection(src, dst, weight, true);
                connections.add(conn);
                neurons.get(src).getConnections().add(dst);
            }
        }

        calculatePositions();
        updateStatistics();
        updateLayerCombo();
        redraw();
        logSuccess("Rede LEX IAG carregada: " + neurons.size() + " neurônios, " + connections.size() + " conexões");
    }

    private void generateNetwork(String archType) {
        // Generator similar to Python's NetworkDataGenerator
        Map<String, Object> arch = getArchitectureSpec(archType);
        List<Map<String, Object>> layerConfigs = (List<Map<String, Object>>) arch.get("layers");
        neurons.clear();
        connections.clear();
        layers.clear();
        int id = 0;
        for (Map<String, Object> lc : layerConfigs) {
            String layerName = (String) lc.get("name");
            int neuronCount = (int) lc.get("neurons");
            List<String> types = (List<String>) lc.get("types");
            layers.put(layerName, new ArrayList<>());
            for (int i = 0; i < neuronCount; i++) {
                String type = types.get(new Random().nextInt(types.size()));
                Neuron n = new Neuron(id, type + "_" + i, type, layerName, layers.size() - 1);
                n.setActivation(new Random().nextDouble());
                n.setColor(getNeuronColor(type));
                neurons.put(id, n);
                layers.get(layerName).add(id);
                id++;
            }
        }
        // Create connections (forward only)
        List<String> layerNames = new ArrayList<>(layers.keySet());
        for (int i = 0; i < layerNames.size() - 1; i++) {
            List<Integer> src = layers.get(layerNames.get(i));
            List<Integer> dst = layers.get(layerNames.get(i + 1));
            double connectivity = 0.6; // default
            for (int s : src) {
                int num = Math.max(1, (int)(dst.size() * connectivity));
                List<Integer> targets = new ArrayList<>(dst);
                Collections.shuffle(targets);
                for (int j = 0; j < Math.min(num, targets.size()); j++) {
                    int t = targets.get(j);
                    double weight = new Random().nextDouble();
                    connections.add(new Connection(s, t, weight, false));
                    neurons.get(s).getConnections().add(t);
                }
            }
        }
        calculatePositions();
        updateStatistics();
        updateLayerCombo();
        redraw();
        logSuccess("Arquitetura " + archType + " carregada: " + neurons.size() + " neurônios, " + connections.size() + " conexões");
    }

    private Map<String, Object> getArchitectureSpec(String name) {
        Map<String, Object> spec = new HashMap<>();
        List<Map<String, Object>> layers = new ArrayList<>();
        switch (name) {
            case "vhalinor_quantum":
                spec.put("name", "VHALINOR Quantum Core");
                layers.add(createLayerSpec("Quantum Input", 8, "quantum", "sensory"));
                layers.add(createLayerSpec("Quantum Processing", 16, "quantum", "processing"));
                layers.add(createLayerSpec("Neural Matrix", 32, "processing", "memory"));
                layers.add(createLayerSpec("Decision Layer", 16, "decision", "analytical"));
                layers.add(createLayerSpec("Quantum Output", 4, "output", "api"));
                break;
            case "deep_learning":
                layers.add(createLayerSpec("Input", 10, "sensory"));
                layers.add(createLayerSpec("Hidden 1", 64, "processing"));
                layers.add(createLayerSpec("Hidden 2", 128, "processing"));
                layers.add(createLayerSpec("Hidden 3", 64, "processing"));
                layers.add(createLayerSpec("Output", 5, "output"));
                break;
            case "lstm_memory":
                layers.add(createLayerSpec("Input Gate", 12, "sensory"));
                layers.add(createLayerSpec("Forget Gate", 12, "memory"));
                layers.add(createLayerSpec("Cell State", 24, "memory", "processing"));
                layers.add(createLayerSpec("Output Gate", 12, "processing"));
                layers.add(createLayerSpec("Output", 6, "output"));
                break;
            case "convolutional":
                layers.add(createLayerSpec("Input", 16, "sensory"));
                layers.add(createLayerSpec("Conv 1", 32, "processing"));
                layers.add(createLayerSpec("Pool 1", 16, "processing"));
                layers.add(createLayerSpec("Conv 2", 64, "processing"));
                layers.add(createLayerSpec("Dense", 128, "processing", "memory"));
                layers.add(createLayerSpec("Output", 10, "output"));
                break;
            default:
                spec.put("name", "VHALINOR Quantum Core");
                layers.add(createLayerSpec("Input", 6, "sensory"));
                layers.add(createLayerSpec("Hidden", 10, "processing"));
                layers.add(createLayerSpec("Output", 4, "output"));
        }
        spec.put("layers", layers);
        return spec;
    }

    private Map<String, Object> createLayerSpec(String name, int neurons, String... types) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("neurons", neurons);
        map.put("types", Arrays.asList(types));
        return map;
    }

    private String getNeuronColor(String type) {
        switch (type) {
            case "sensory": return "#FF6B6B";
            case "processing": return "#4ECDC4";
            case "memory": return "#95E1D3";
            case "decision": return "#FFD93D";
            case "output": return "#6BCB77";
            case "quantum": return "#9D4EDD";
            case "analytical": return "#3A86FF";
            case "security": return "#FB5607";
            case "api": return "#8338EC";
            case "database": return "#06FFA5";
            case "control": return "#FF006E";
            default: return "#CCCCCC";
        }
    }

    // ==================== POSITION CALCULATION ====================
    private void calculatePositions() {
        switch (currentLayout) {
            case "circular":
                calculateCircularPositions();
                break;
            default: // hierarchical
                calculateHierarchicalPositions();
        }
    }

    private void calculateHierarchicalPositions() {
        double margin = 0.1;
        double width = 1.0;
        double height = 1.0;
        List<String> layerNames = new ArrayList<>(layers.keySet());
        double layerWidth = width / (layerNames.size() + 1);
        for (int i = 0; i < layerNames.size(); i++) {
            List<Integer> layerNeurons = layers.get(layerNames.get(i));
            double x = (i + 1) * layerWidth;
            // order by importance
            Collections.sort(layerNeurons, (a, b) -> Double.compare(
                    neurons.get(b).getImportance(), neurons.get(a).getImportance()));
            double ySpacing = (height - 2 * margin) / Math.max(layerNeurons.size(), 1);
            double yStart = margin;
            for (int j = 0; j < layerNeurons.size(); j++) {
                int nid = layerNeurons.get(j);
                double y = yStart + (j + 0.5) * ySpacing;
                neurons.get(nid).setX(x);
                neurons.get(nid).setY(y);
            }
        }
    }

    private void calculateCircularPositions() {
        double centerX = 0.5, centerY = 0.5;
        double radius = 0.4;
        List<Integer> allIds = new ArrayList<>(neurons.keySet());
        for (int i = 0; i < allIds.size(); i++) {
            double angle = 2 * Math.PI * i / allIds.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            neurons.get(allIds.get(i)).setX(x);
            neurons.get(allIds.get(i)).setY(y);
        }
    }

    // ==================== NETWORK ANALYSIS ====================
    private void updateStatistics() {
        stats = new NetworkStatistics();
        stats.totalNeurons = neurons.size();
        stats.totalConnections = connections.size();
        stats.totalLayers = layers.size();

        for (Neuron n : neurons.values()) {
            stats.neuronsByType.merge(n.getType(), 1, Integer::sum);
            stats.neuronsByLayer.merge(n.getLayer(), 1, Integer::sum);
        }
        for (Connection c : connections) {
            stats.connectionsByType.merge(c.getType(), 1, Integer::sum);
        }

        // activations
        OptionalDouble avgAct = neurons.values().stream().mapToDouble(Neuron::getActivation).average();
        stats.avgActivation = avgAct.orElse(0);
        stats.maxActivation = neurons.values().stream().mapToDouble(Neuron::getActivation).max().orElse(0);
        stats.minActivation = neurons.values().stream().mapToDouble(Neuron::getActivation).min().orElse(0);

        // weights
        OptionalDouble avgW = connections.stream().mapToDouble(Connection::getWeight).average();
        stats.avgWeight = avgW.orElse(0);
        stats.maxWeight = connections.stream().mapToDouble(Connection::getWeight).max().orElse(0);
        stats.minWeight = connections.stream().mapToDouble(Connection::getWeight).min().orElse(0);

        if (stats.totalNeurons > 1) {
            stats.density = stats.totalConnections / (double)(stats.totalNeurons * (stats.totalNeurons - 1));
        }
        // clustering coefficient and avg path length would require graph library, skip
    }

    private void showStatisticsDialog() {
        StringBuilder sb = new StringBuilder("ESTATÍSTICAS DA REDE NEURAL\n\n");
        sb.append("Visão Geral:\n");
        sb.append("  Neurônios: ").append(stats.totalNeurons).append("\n");
        sb.append("  Conexões: ").append(stats.totalConnections).append("\n");
        sb.append("  Camadas: ").append(stats.totalLayers).append("\n");
        sb.append("  Densidade: ").append(String.format("%.4f", stats.density)).append("\n\n");
        sb.append("Neurônios por Tipo:\n");
        stats.neuronsByType.forEach((k, v) -> sb.append("  ").append(k).append(": ").append(v).append("\n"));
        sb.append("\nConexões por Tipo:\n");
        stats.connectionsByType.forEach((k, v) -> sb.append("  ").append(k).append(": ").append(v).append("\n"));
        sb.append("\nAtivação: Média=").append(String.format("%.1f%%", stats.avgActivation * 100))
          .append(" Max=").append(String.format("%.1f%%", stats.maxActivation * 100))
          .append(" Min=").append(String.format("%.1f%%", stats.minActivation * 100)).append("\n");
        sb.append("Pesos: Média=").append(String.format("%.3f", stats.avgWeight))
          .append(" Max=").append(String.format("%.3f", stats.maxWeight))
          .append(" Min=").append(String.format("%.3f", stats.minWeight));

        JOptionPane.showMessageDialog(this, sb.toString(), "Estatísticas da Rede", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== GUI ACTIONS ====================
    private void refreshNetwork() {
        logInfo("Atualizando rede...");
        for (Neuron n : neurons.values()) {
            n.setActivation(new Random().nextDouble());
        }
        updateStatistics();
        redraw();
    }

    private void redraw() {
        networkCanvas.repaint();
    }

    private void applyFilter() {
        logInfo("Aplicando filtro: Tipo=" + filterTypeCombo.getSelectedItem() + ", Camada=" + filterLayerCombo.getSelectedItem());
        networkCanvas.repaint();
    }

    private void clearFilters() {
        filterTypeCombo.setSelectedItem("Todos");
        filterLayerCombo.setSelectedItem("Todas");
        logInfo("Filtros removidos");
        redraw();
    }

    private void setLayout(String layout) {
        this.currentLayout = layout;
        calculatePositions();
        redraw();
        logInfo("Layout alterado para " + layout);
    }

    private void cycleLayout() {
        if ("hierarchical".equals(currentLayout)) {
            setLayout("circular");
        } else {
            setLayout("hierarchical");
        }
    }

    private void setTheme(Theme theme) {
        this.currentTheme = theme;
        redraw();
        logInfo("Tema alterado para " + theme.name());
    }

    private void cycleTheme() {
        Theme[] themes = Theme.values();
        int idx = currentTheme.ordinal();
        idx = (idx + 1) % (themes.length - 1); // skip CUSTOM
        setTheme(themes[idx]);
    }

    private void toggleAnimation() {
        if (animationEnabled) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    private void startAnimation() {
        animationEnabled = true;
        int delay = animationSpeed.getDelay();
        animationTimer = new javax.swing.Timer(delay, e -> {
            for (Neuron n : neurons.values()) {
                n.setActivation(Math.max(0.0, Math.min(1.0, n.getActivation() + new Random().nextGaussian() * 0.1)));
            }
            redraw();
        });
        animationTimer.start();
        logInfo("Animação iniciada");
    }

    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        animationEnabled = false;
        logInfo("Animação parada");
    }

    private void setAnimationSpeed(String speedStr) {
        switch (speedStr) {
            case "Muito Lento": animationSpeed = AnimationSpeed.SLOW; break;
            case "Lento": animationSpeed = AnimationSpeed.NORMAL; break;
            case "Normal": animationSpeed = AnimationSpeed.FAST; break;
            case "Rápido": animationSpeed = AnimationSpeed.VERY_FAST; break;
            case "Tempo Real": animationSpeed = AnimationSpeed.REAL_TIME; break;
        }
        if (animationTimer != null) {
            animationTimer.setDelay(animationSpeed.getDelay());
        }
    }

    private void clearSelection() {
        selectedNeurons.clear();
        highlightedConnections.clear();
        redraw();
        logInfo("Seleção limpa");
    }

    private void searchNeuron() {
        String query = JOptionPane.showInputDialog(this, "Nome do neurônio:", "Buscar Neurônio", JOptionPane.QUESTION_MESSAGE);
        if (query != null && !query.isEmpty()) {
            String lower = query.toLowerCase();
            Optional<Neuron> found = neurons.values().stream()
                    .filter(n -> n.getName().toLowerCase().contains(lower))
                    .findFirst();
            if (found.isPresent()) {
                selectedNeurons.clear();
                selectedNeurons.add(found.get().getId());
                redraw();
                logInfo("Neurônio encontrado: " + found.get().getName());
            } else {
                JOptionPane.showMessageDialog(this, "Neurônio não encontrado.");
            }
        }
    }

    private void showImportantNeurons() {
        // Simple important: highest activation * connections
        List<Neuron> sorted = neurons.values().stream()
                .sorted(Comparator.comparingDouble(n -> n.getActivation() * n.getImportance()))
                .collect(Collectors.toList());
        Collections.reverse(sorted);
        StringBuilder sb = new StringBuilder("Neurônios mais importantes:\n");
        int count = 0;
        for (Neuron n : sorted) {
            sb.append(n.getName()).append(" (ativação=").append(String.format("%.1f", n.getActivation() * 100)).append("%)\n");
            if (++count >= 10) break;
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void showAboutDialog() {
        String about = "VHALINOR IAG 5.0 (Java Edition)\nVisualizador Quântico de Rede Neural\nAutor: Alex Miranda Sales\n2026";
        JOptionPane.showMessageDialog(this, about);
    }

    private void takeScreenshot() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // TODO: implement canvas screenshot
            logWarning("Screenshot não implementado nesta versão.");
        }
    }

    private void exportNetwork() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            // Simple export
            StringBuilder sb = new StringBuilder("{\"neurons\":[");
            for (Neuron n : neurons.values()) {
                sb.append(String.format("{\"id\":%d,\"name\":\"%s\",\"type\":\"%s\"},", n.getId(), n.getName(), n.getType()));
            }
            if (!neurons.isEmpty()) sb.deleteCharAt(sb.length() - 1);
            sb.append("],\"connections\":[");
            for (Connection c : connections) {
                sb.append(String.format("{\"s\":%d,\"t\":%d,\"w\":%f},", c.getSource(), c.getTarget(), c.getWeight()));
            }
            if (!connections.isEmpty()) sb.deleteCharAt(sb.length() - 1);
            sb.append("]}");
            try {
                java.nio.file.Files.writeString(file.toPath(), sb.toString());
                logSuccess("Rede exportada para " + file.getAbsolutePath());
            } catch (IOException e) {
                logError("Erro ao exportar: " + e.getMessage());
            }
        }
    }

    private void importNetwork() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Simple import (stub)
            logWarning("Importação simplificada não implementada nesta versão.");
        }
    }

    private void updateLayerCombo() {
        filterLayerCombo.removeAllItems();
        filterLayerCombo.addItem("Todas");
        for (String layer : layers.keySet()) {
            filterLayerCombo.addItem(layer);
        }
    }

    // ==================== NETWORK CANVAS ====================
    class NetworkCanvas extends JPanel {
        public NetworkCanvas() {
            setBackground(Color.decode("#1a1a1a"));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Find nearest neuron
                    double minDist = 0.05; // in normalized units
                    int clickedId = -1;
                    double w = getWidth();
                    double h = getHeight();
                    for (Neuron n : neurons.values()) {
                        double nx = n.getX() * w;
                        double ny = n.getY() * h;
                        double dist = Math.hypot(e.getX() - nx, e.getY() - ny);
                        if (dist < 20) { // pixel threshold
                            clickedId = n.getId();
                            break;
                        }
                    }
                    if (clickedId >= 0) {
                        if (selectedNeurons.contains(clickedId)) {
                            selectedNeurons.remove(clickedId);
                        } else {
                            selectedNeurons.add(clickedId);
                        }
                        redraw();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Map<String, Color> themeColors = themeManager.getThemeColors(currentTheme);
            g2.setColor(themeColors.get("bg"));
            g2.fillRect(0, 0, getWidth(), getHeight());

            float w = getWidth();
            float h = getHeight();

            // Apply filters
            String filterType = (String) filterTypeCombo.getSelectedItem();
            String filterLayer = (String) filterLayerCombo.getSelectedItem();
            boolean typeAll = "Todos".equals(filterType);
            boolean layerAll = "Todas".equals(filterLayer);

            // Draw connections
            for (Connection c : connections) {
                Neuron src = neurons.get(c.getSource());
                Neuron tgt = neurons.get(c.getTarget());
                if (src == null || tgt == null) continue;
                if (!typeAll && !c.getType().equals(filterType)) continue;
                if (!layerAll && !src.getLayer().equals(filterLayer) && !tgt.getLayer().equals(filterLayer)) continue;

                float x1 = (float) src.getX() * w;
                float y1 = (float) src.getY() * h;
                float x2 = (float) tgt.getX() * w;
                float y2 = (float) tgt.getY() * h;

                Stroke stroke = c.isRecurrent() ? new BasicStroke((float) (c.getWidth() * 1.2),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5, 5}, 0)
                        : new BasicStroke((float) (c.getWidth() * 1.2));
                g2.setStroke(stroke);
                Color connColor = Color.decode(c.getColor());
                if (selectedNeurons.contains(c.getSource()) || selectedNeurons.contains(c.getTarget())) {
                    connColor = Color.YELLOW;
                }
                g2.setColor(new Color(connColor.getRed(), connColor.getGreen(), connColor.getBlue(), 150));
                g2.draw(new Line2D.Float(x1, y1, x2, y2));

                // Weight text
                if (showWeights) {
                    float mx = (x1 + x2) / 2, my = (y1 + y2) / 2;
                    String txt = String.format("%.2f", c.getWeight());
                    g2.setColor(themeColors.get("fg"));
                    g2.drawString(txt, mx, my);
                }
            }

            // Draw neurons
            for (Neuron n : neurons.values()) {
                if (!typeAll && !n.getType().equals(filterType)) continue;
                if (!layerAll && !n.getLayer().equals(filterLayer)) continue;

                float x = (float) n.getX() * w;
                float y = (float) n.getY() * h;
                float radius = (float) (15 * n.getSize() + (showActivations ? 10 * n.getActivation() : 0));
                Color fillColor = Color.decode(n.getColor());
                if (selectedNeurons.contains(n.getId())) {
                    fillColor = Color.YELLOW;
                }
                g2.setColor(fillColor);
                g2.fill(new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2));
                g2.setColor(themeColors.get("fg"));
                g2.setStroke(new BasicStroke(1));
                g2.draw(new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2));

                if (showLabels) {
                    String label = n.getName().length() > 10 ? n.getName().substring(0, 10) : n.getName();
                    if (showActivations) {
                        label += String.format("\n%.0f%%", n.getActivation() * 100);
                    }
                    g2.setColor(themeColors.get("fg"));
                    g2.drawString(label, x - radius, y - radius - 2);
                }
            }

            g2.dispose();
        }
    }

    // ==================== THEME MANAGER ====================
    static class ThemeManager {
        private Map<Theme, Map<String, Color>> themes = new HashMap<>();

        public ThemeManager() {
            Map<String, Color> dark = new HashMap<>();
            dark.put("bg", Color.decode("#1a1a1a"));
            dark.put("fg", Color.WHITE);
            dark.put("grid", Color.decode("#333333"));
            dark.put("panel", Color.decode("#2a2a2a"));
            dark.put("accent", Color.GREEN);
            dark.put("text", Color.WHITE);
            dark.put("edge", Color.decode("#444444"));
            dark.put("highlight", Color.YELLOW);
            themes.put(Theme.DARK, dark);

            Map<String, Color> light = new HashMap<>();
            light.put("bg", Color.decode("#f5f5f5"));
            light.put("fg", Color.BLACK);
            themes.put(Theme.LIGHT, light);

            // simplified: only DARK fully
        }

        public Map<String, Color> getThemeColors(Theme theme) {
            return themes.getOrDefault(theme, themes.get(Theme.DARK));
        }
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            NeuralNetworkVisualizer viz = new NeuralNetworkVisualizer();
            viz.setLocationRelativeTo(null);
            viz.setVisible(true);
        });
    }
}
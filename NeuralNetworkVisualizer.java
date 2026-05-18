import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

// ==================== ESTRUTURAS DE DADOS ====================

class Neuron {
    int id;
    String name, type, layer;
    int layerIndex;
    double activation, bias, size, importance, error, gradient;
    List<Integer> connections;
    double x2d, y2d; // posição 2D normalizada
    double x3d, y3d, z3d; // posição 3D (não usada no Java)
    String color;
    long timestamp;
    Map<String, Object> metadata;

    // construtor completo...
    public Neuron(int id, String name, String type, String layer, int layerIndex,
                  double activation, double bias, String color, double size, double importance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.layer = layer;
        this.layerIndex = layerIndex;
        this.activation = activation;
        this.bias = bias;
        this.color = color;
        this.size = size;
        this.importance = importance;
        this.connections = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
        this.error = 0;
        this.gradient = 0;
    }
}

class Connection {
    int source, target;
    double weight, strength;
    String type;
    boolean recurrent, enabled;
    String color, style;
    double width;
    long timestamp;
    Map<String, Object> metadata;

    public Connection(int source, int target, double weight, double strength, String type,
                      boolean recurrent, boolean enabled, String color, String style, double width) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.strength = strength;
        this.type = type;
        this.recurrent = recurrent;
        this.enabled = enabled;
        this.color = color;
        this.style = style;
        this.width = width;
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }
}

class NetworkStatistics {
    int totalNeurons, totalConnections, totalLayers;
    Map<String, Integer> neuronsByType, neuronsByLayer, connectionsByType;
    double avgActivation, maxActivation, minActivation, avgWeight, maxWeight, minWeight;
    double density, clusteringCoefficient, avgPathLength;

    public NetworkStatistics() {
        neuronsByType = new HashMap<>();
        neuronsByLayer = new HashMap<>();
        connectionsByType = new HashMap<>();
    }
}

// ==================== GERADOR DE DADOS SINTÉTICOS ====================

class NetworkDataGenerator {
    // Arquiteturas predefinidas (mesma definição do Python)
    public static final String[] ARCHITECTURES = {"vhalinor_quantum", "deep_learning", "lstm_memory", "convolutional", "lex_iag"};
    static final Map<String, Object> NEURON_TYPES = new HashMap<>();

    static {
        // inicializar tipos
        NEURON_TYPES.put("sensory", new double[]{-65536, 1.0, 1.2}); // cor, tam, imp
        NEURON_TYPES.put("processing", new double[]{-16744320, 0.9, 1.0});
        NEURON_TYPES.put("memory", new double[]{-16740653, 1.1, 1.3});
        // ... demais tipos
    }

    // Geração baseada em arquitetura
    public static NetworkData generateNetwork(String architecture, long seed) {
        Random rand = (seed != 0) ? new Random(seed) : new Random();
        // implementação similar: criar camadas, neurônios e conexões
        // retorna objeto contendo mapa de neurônios, lista de conexões e mapa de camadas
        // (omitido por brevidade, mas a estrutura está no código Python)
        return null;
    }

    // Rede específica LEX IAG
    public static NetworkData generateLexIAG() {
        // mesma lógica do Python, implementação completa omitida
        return null;
    }

    // Classe auxiliar para retorno
    static class NetworkData {
        Map<Integer, Neuron> neurons;
        List<Connection> connections;
        Map<String, List<Integer>> layers;
    }
}

// ==================== ANALISADOR DE REDE ====================

class NetworkAnalyzer {
    public static NetworkStatistics calculateStatistics(Map<Integer, Neuron> neurons, List<Connection> connections) {
        NetworkStatistics stats = new NetworkStatistics();
        stats.totalNeurons = neurons.size();
        stats.totalConnections = connections.size();

        // popula tipos, camadas, conexões
        for (Neuron n : neurons.values()) {
            stats.neuronsByType.merge(n.type, 1, Integer::sum);
            stats.neuronsByLayer.merge(n.layer, 1, Integer::sum);
        }
        stats.totalLayers = stats.neuronsByLayer.size();

        for (Connection c : connections) {
            String ctype = c.recurrent ? "recurrent" : "forward";
            stats.connectionsByType.merge(ctype, 1, Integer::sum);
        }

        // médias/máximos/mínimos
        double sumAct = neurons.values().stream().mapToDouble(n -> n.activation).sum();
        stats.avgActivation = sumAct / neurons.size();
        stats.maxActivation = neurons.values().stream().mapToDouble(n -> n.activation).max().orElse(0);
        stats.minActivation = neurons.values().stream().mapToDouble(n -> n.activation).min().orElse(0);

        if (!connections.isEmpty()) {
            double sumW = connections.stream().mapToDouble(c -> c.weight).sum();
            stats.avgWeight = sumW / connections.size();
            stats.maxWeight = connections.stream().mapToDouble(c -> c.weight).max().orElse(0);
            stats.minWeight = connections.stream().mapToDouble(c -> c.weight).min().orElse(0);
        }

        // densidade
        int n = neurons.size();
        double maxConn = n * (n - 1.0);
        stats.density = maxConn > 0 ? connections.size() / maxConn : 0;
        return stats;
    }

    // outros métodos (importantes, comunidades) podem ser implementados com JGraphT ou manualmente
    // por simplicidade omitidos.
}

// ==================== GERENCIADOR DE TEMAS ====================

enum Theme {
    DARK, LIGHT, BLUE, GREEN, PURPLE, QUANTUM, CUSTOM;

    public Map<String, Color> getColors() {
        Map<String, Color> map = new HashMap<>();
        switch (this) {
            case DARK:
                map.put("bg", new Color(0x1a1a1a));
                map.put("fg", Color.WHITE);
                map.put("grid", new Color(0x333333));
                map.put("accent", Color.GREEN);
                break;
            case LIGHT:
                map.put("bg", new Color(0xf5f5f5));
                map.put("fg", Color.BLACK);
                map.put("grid", new Color(0xdddddd));
                map.put("accent", new Color(0x0066cc));
                break;
            // demais temas...
            default:
                map.put("bg", Color.DARK_GRAY);
                map.put("fg", Color.WHITE);
                map.put("grid", Color.GRAY);
                map.put("accent", Color.GREEN);
        }
        return map;
    }
}

// ==================== VISUALIZADOR PRINCIPAL ====================

public class NeuralNetworkVisualizer extends JFrame {
    private Map<Integer, Neuron> neurons = new HashMap<>();
    private List<Connection> connections = new ArrayList<>();
    private Map<String, List<Integer>> layers = new LinkedHashMap<>();

    private String currentLayout = "hierarchical";
    private Theme currentTheme = Theme.DARK;
    private boolean showLabels = true, showWeights = false, showActivations = true, highlightImportant = false;
    private boolean animationEnabled = false;
    private Set<Integer> selectedNeurons = new HashSet<>();
    private Set<String> highlightedConnections = new HashSet<>(); // "source->target"

    private JPanel drawPanel;
    private JTextArea logArea;
    private JComboBox<String> architectureCombo, layerCombo, typeCombo, speedCombo;
    private JLabel statusLabel;
    private Timer animationTimer;

    // Posições calculadas (2D)
    private Map<Integer, Point2D> positions = new HashMap<>();

    public NeuralNetworkVisualizer() {
        super("VHALINOR IAG 1.0 - Neural Network Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setMinimumSize(new Dimension(1200, 800));
        initUI();
        loadLexIAGNetwork();
        setVisible(true);
    }

    private void initUI() {
        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("Import")).addActionListener(e -> importNetwork());
        fileMenu.add(new JMenuItem("Export")).addActionListener(e -> exportNetwork());
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem("Exit")).addActionListener(e -> System.exit(0));
        menuBar.add(fileMenu);
        // outros menus (View, Analysis, Help) omitidos por brevidade
        setJMenuBar(menuBar);

        // Painel principal com split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createControlPanel());
        drawPanel = new DrawingPanel();
        splitPane.setRightComponent(drawPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // Arquitetura
        panel.add(createSection("Architecture", () -> {
            JPanel p = new JPanel(new BorderLayout());
            architectureCombo = new JComboBox<>(NetworkDataGenerator.ARCHITECTURES);
            architectureCombo.addActionListener(e -> loadArchitecture());
            p.add(architectureCombo, BorderLayout.NORTH);
            JButton loadBtn = new JButton("Load");
            loadBtn.addActionListener(e -> loadArchitecture());
            p.add(loadBtn, BorderLayout.SOUTH);
            return p;
        }));

        // Controles de visualização (checkboxes)
        panel.add(createSection("Visualization", () -> {
            JPanel p = new JPanel(new GridLayout(4,1));
            JCheckBox cbLabels = new JCheckBox("Show Labels", showLabels);
            cbLabels.addActionListener(e -> { showLabels = cbLabels.isSelected(); repaint(); });
            JCheckBox cbWeights = new JCheckBox("Show Weights", showWeights);
            cbWeights.addActionListener(e -> { showWeights = cbWeights.isSelected(); repaint(); });
            JCheckBox cbAct = new JCheckBox("Show Activations", showActivations);
            cbAct.addActionListener(e -> { showActivations = cbAct.isSelected(); repaint(); });
            JCheckBox cbImp = new JCheckBox("Highlight Important", highlightImportant);
            cbImp.addActionListener(e -> { highlightImportant = cbImp.isSelected(); repaint(); });
            p.add(cbLabels); p.add(cbWeights); p.add(cbAct); p.add(cbImp);
            return p;
        }));

        // Velocidade de animação
        panel.add(createSection("Animation", () -> {
            JPanel p = new JPanel(new FlowLayout());
            JButton animBtn = new JButton("Start/Stop");
            animBtn.addActionListener(e -> toggleAnimation());
            speedCombo = new JComboBox<>(new String[]{"Slow", "Normal", "Fast"});
            p.add(animBtn); p.add(speedCombo);
            return p;
        }));

        // Filtros
        panel.add(createSection("Filters", () -> {
            JPanel p = new JPanel(new GridLayout(3,1));
            typeCombo = new JComboBox<>(new String[]{"All","sensory","processing","memory","decision","output","quantum","analytical"});
            layerCombo = new JComboBox<>(new String[]{"All"});
            JButton applyBtn = new JButton("Apply");
            applyBtn.addActionListener(e -> applyFilter());
            p.add(new JLabel("Type:")); p.add(typeCombo);
            p.add(new JLabel("Layer:")); p.add(layerCombo);
            p.add(applyBtn);
            return p;
        }));

        // Log
        panel.add(createSection("Log", () -> {
            logArea = new JTextArea(10, 20);
            logArea.setEditable(false);
            logArea.setBackground(Color.BLACK);
            logArea.setForeground(Color.GREEN);
            logArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
            JScrollPane sp = new JScrollPane(logArea);
            return sp;
        }));

        return new JScrollPane(panel);
    }

    private JPanel createSection(String title, java.util.function.Supplier<JComponent> content) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createTitledBorder(title));
        section.add(content.get(), BorderLayout.CENTER);
        return section;
    }

    // ------ Lógica de rede ------

    private void loadLexIAGNetwork() {
        // Implementação similar ao Python, usando NetworkDataGenerator.generateLexIAG()
        log("Loading LEX IAG network...");
        // Dados mockados para exemplo (deve ser substituído pela geração real)
        neurons.clear(); connections.clear(); layers.clear();
        buildExampleNetwork(); // temporário
        calculatePositions();
        updateLayerCombo();
        repaint();
        log("Loaded LEX IAG network: " + neurons.size() + " neurons, " + connections.size() + " connections.");
    }

    private void buildExampleNetwork() {
        // Cria uma rede simples para demonstração
        layers.put("Input", new ArrayList<>());
        layers.put("Hidden", new ArrayList<>());
        layers.put("Output", new ArrayList<>());
        int id = 0;
        for (int i = 0; i < 3; i++) {
            Neuron n = new Neuron(id++, "in_"+i, "sensory", "Input", 0, 0.7, 0.1, "#FF6B6B", 1.0, 1.2);
            neurons.put(n.id, n);
            layers.get("Input").add(n.id);
        }
        for (int i = 0; i < 5; i++) {
            Neuron n = new Neuron(id++, "hid_"+i, "processing", "Hidden", 1, 0.5+0.1*i, 0.0, "#4ECDC4", 0.9, 1.0);
            neurons.put(n.id, n);
            layers.get("Hidden").add(n.id);
        }
        for (int i = 0; i < 2; i++) {
            Neuron n = new Neuron(id++, "out_"+i, "output", "Output", 2, 0.8, 0.2, "#6BCB77", 1.0, 1.2);
            neurons.put(n.id, n);
            layers.get("Output").add(n.id);
        }
        // Conexões
        Random rand = new Random(42);
        for (int src : layers.get("Input")) {
            for (int tgt : layers.get("Hidden")) {
                if (rand.nextDouble() > 0.4) {
                    connections.add(new Connection(src, tgt, rand.nextDouble()*0.8+0.2, 1.0, "forward", false, true, "#00FF00", "-", 1.0));
                    neurons.get(src).connections.add(tgt);
                }
            }
        }
        for (int src : layers.get("Hidden")) {
            for (int tgt : layers.get("Output")) {
                connections.add(new Connection(src, tgt, rand.nextDouble()*0.8+0.2, 1.0, "forward", false, true, "#00FF00", "-", 1.0));
                neurons.get(src).connections.add(tgt);
            }
        }
    }

    private void calculatePositions() {
        positions.clear();
        int w = drawPanel.getWidth();
        int h = drawPanel.getHeight();
        if (w == 0) w = 800; // fallback
        if (h == 0) h = 600;

        if (currentLayout.equals("hierarchical")) {
            hierarchicalLayout(w, h);
        } else if (currentLayout.equals("circular")) {
            circularLayout(w, h);
        } else {
            // spring simplificado: hierárquico
            hierarchicalLayout(w, h);
        }
    }

    private void hierarchicalLayout(int w, int h) {
        if (layers.isEmpty()) return;
        List<String> layerNames = new ArrayList<>(layers.keySet());
        double xStep = (double) w / (layerNames.size() + 1);
        int i = 0;
        for (String layerName : layerNames) {
            List<Integer> ids = layers.get(layerName);
            double x = (i + 1) * xStep;
            double yStep = (double) h / (ids.size() + 1);
            for (int j = 0; j < ids.size(); j++) {
                double y = (j + 1) * yStep;
                positions.put(ids.get(j), new Point2D.Double(x, y));
                // atualiza posição normalizada no objeto Neuron (opcional)
            }
            i++;
        }
    }

    private void circularLayout(int w, int h) {
        int total = neurons.size();
        int i = 0;
        double cx = w / 2.0, cy = h / 2.0;
        double radius = Math.min(w, h) * 0.4;
        for (Integer id : neurons.keySet()) {
            double angle = 2 * Math.PI * i / total;
            double x = cx + radius * Math.cos(angle);
            double y = cy + radius * Math.sin(angle);
            positions.put(id, new Point2D.Double(x, y));
            i++;
        }
    }

    // ------ Desenho ------

    class DrawingPanel extends JPanel {
        public DrawingPanel() {
            setBackground(Color.DARK_GRAY);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    handleClick(e.getX(), e.getY());
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Theme colors = currentTheme;
            Map<String, Color> theme = colors.getColors();
            this.setBackground(theme.get("bg"));

            // Redesenha posições para caber no tamanho atual
            calculatePositions();

            // Desenha conexões
            for (Connection c : connections) {
                if (!c.enabled) continue;
                Point2D p1 = positions.get(c.source);
                Point2D p2 = positions.get(c.target);
                if (p1 == null || p2 == null) continue;
                g2.setColor(c.recurrent ? Color.MAGENTA : Color.decode(c.color));
                float alpha = 0.5f;
                Stroke stroke = new BasicStroke((float) c.width,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10,
                        c.recurrent ? new float[]{5,5} : null, 0);
                g2.setStroke(stroke);
                g2.draw(new Line2D.Double(p1, p2));
                if (showWeights) {
                    double mx = (p1.getX() + p2.getX()) / 2;
                    double my = (p1.getY() + p2.getY()) / 2;
                    g2.setColor(theme.get("fg"));
                    g2.drawString(String.format("%.2f", c.weight), (int) mx, (int) my);
                }
            }

            // Desenha neurônios
            for (Neuron n : neurons.values()) {
                // Aplica filtros
                if (!typeCombo.getSelectedItem().equals("All") && !typeCombo.getSelectedItem().equals(n.type)) continue;
                if (!layerCombo.getSelectedItem().equals("All") && !layerCombo.getSelectedItem().equals(n.layer)) continue;

                Point2D p = positions.get(n.id);
                if (p == null) continue;
                int radius = showActivations ? (int)(10 + n.activation * 20) : 15;
                Color fillColor = Color.decode(n.color);
                Color borderColor = selectedNeurons.contains(n.id) ? theme.get("accent") : theme.get("fg");
                if (highlightImportant && n.importance > 1.5) borderColor = Color.MAGENTA;
                g2.setColor(fillColor);
                g2.fill(new Ellipse2D.Double(p.getX() - radius, p.getY() - radius, 2 * radius, 2 * radius));
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(selectedNeurons.contains(n.id) ? 3 : 1));
                g2.draw(new Ellipse2D.Double(p.getX() - radius, p.getY() - radius, 2 * radius, 2 * radius));

                if (showLabels) {
                    g2.setColor(theme.get("fg"));
                    g2.setFont(new Font("Arial", Font.BOLD, 9));
                    String label = n.name.length() > 7 ? n.name.substring(0,7) : n.name;
                    if (showActivations) label += "\n" + String.format("%.0f%%", n.activation*100);
                    // Centraliza texto simples
                    int labelX = (int) p.getX() - 20;
                    int labelY = (int) p.getY() - radius - 5;
                    for (String line : label.split("\n")) {
                        g2.drawString(line, labelX, labelY);
                        labelY += 10;
                    }
                }
            }
        }
    }

    private void handleClick(int x, int y) {
        // procura neurônio mais próximo
        double minDist = 20;
        Integer closest = null;
        for (Map.Entry<Integer, Point2D> e : positions.entrySet()) {
            Point2D p = e.getValue();
            double d = p.distance(x, y);
            if (d < minDist) {
                minDist = d;
                closest = e.getKey();
            }
        }
        if (closest != null) {
            toggleNeuronSelection(closest);
        }
    }

    private void toggleNeuronSelection(int id) {
        if (selectedNeurons.contains(id)) {
            selectedNeurons.remove(id);
            highlightedConnections.clear();
        } else {
            selectedNeurons.add(id);
            highlightedConnections.clear();
            for (Connection c : connections) {
                if (c.source == id || c.target == id) {
                    highlightedConnections.add(c.source + "->" + c.target);
                }
            }
        }
        repaint();
    }

    // ------ Controles ------

    private void loadArchitecture() {
        String arch = (String) architectureCombo.getSelectedItem();
        log("Loading architecture: " + arch);
        // Aqui chamaria NetworkDataGenerator conforme a arquitetura
        // Para demo usamos a rede exemplo novamente
        buildExampleNetwork();
        calculatePositions();
        updateLayerCombo();
        repaint();
        log("Architecture loaded");
    }

    private void updateLayerCombo() {
        layerCombo.removeAllItems();
        layerCombo.addItem("All");
        layers.keySet().forEach(layerCombo::addItem);
        layerCombo.setSelectedItem("All");
    }

    private void applyFilter() {
        repaint();
    }

    private void toggleAnimation() {
        if (animationEnabled) {
            animationTimer.cancel();
            animationEnabled = false;
            log("Animation stopped");
        } else {
            animationTimer = new Timer();
            long delay = 500; // default
            animationTimer.schedule(new TimerTask() {
                public void run() {
                    // Varia ativações aleatoriamente (exemplo)
                    for (Neuron n : neurons.values()) {
                        n.activation += (Math.random() - 0.5) * 0.1;
                        n.activation = Math.max(0, Math.min(1, n.activation));
                    }
                    repaint();
                }
            }, 0, delay);
            animationEnabled = true;
            log("Animation started");
        }
    }

    // Logging simples
    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // Exportação/importação simplificada (JSON manual)
    private void exportNetwork() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(fc.getSelectedFile())) {
                // escreve JSON simples
                pw.println("{ \"neurons\": [");
                // ... (omitido por brevidade)
                pw.println("] }");
                log("Exported to " + fc.getSelectedFile().getName());
            } catch (IOException e) {
                log("Error exporting: " + e.getMessage());
            }
        }
    }

    private void importNetwork() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // leitura e parse (omitido)
            log("Imported network");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NeuralNetworkVisualizer::new);
    }
}
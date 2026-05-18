import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Painel de Previsão Preditiva Avançada - Java Swing
 * Convertido da aplicação Streamlit original.
 * Inclui gráficos customizados (CPU com cone de incerteza e mapa de calor),
 * métricas, log de incidentes e riscos de segurança.
 */
public class PredictionDashboard extends JFrame {

    // ======================== ENUMS ========================
    enum SeverityLevel { CRITICAL, HIGH, MEDIUM, LOW }

    enum ResourceType { CPU, MEMORY, DISK, NETWORK }

    // ======================== RECORDS DE DADOS ========================
    record PredictionResult(ResourceType resourceType, List<Double> predictions,
                            double confidence, LocalDateTime timestamp) {}

    record SystemIncident(String type, String description, SeverityLevel severity,
                          double probability, LocalDateTime timestamp) {}

    record SecurityRisk(String type, String level, String description,
                        double probability, LocalDateTime timestamp) {}

    // ======================== SERVIÇO DE PREDIÇÃO SIMULADO ========================
    static class AdvancedPredictionService {
        public List<PredictionResult> predictResources() {
            int timePoints = 10;
            double baseCpu = 30 + ThreadLocalRandom.current().nextDouble() * 20;
            double cpuTrend = -0.5 + ThreadLocalRandom.current().nextDouble() * 1.5;
            List<Double> cpuPredictions = new ArrayList<>();
            for (int i = 0; i < timePoints; i++) {
                double val = baseCpu + cpuTrend * i + ThreadLocalRandom.current().nextGaussian() * 5;
                cpuPredictions.add(Math.max(0, Math.min(100, val)));
            }
            return List.of(new PredictionResult(
                    ResourceType.CPU, cpuPredictions,
                    0.7 + ThreadLocalRandom.current().nextDouble() * 0.25,
                    LocalDateTime.now()
            ));
        }

        public List<SystemIncident> predictIncidents() {
            List<SystemIncident> incidents = new ArrayList<>();
            if (ThreadLocalRandom.current().nextDouble() > 0.6) {
                String[] types = {"CPU Overload", "Memory Leak", "Disk Space Critical",
                                  "Network Latency Spike", "Service Degradation"};
                int count = ThreadLocalRandom.current().nextInt(1, 4);
                for (int i = 0; i < count; i++) {
                    String type = types[ThreadLocalRandom.current().nextInt(types.length)];
                    incidents.add(new SystemIncident(
                            type,
                            "Potential " + type.toLowerCase() + " detected",
                            SeverityLevel.values()[ThreadLocalRandom.current().nextInt(SeverityLevel.values().length)],
                            0.3 + ThreadLocalRandom.current().nextDouble() * 0.6,
                            LocalDateTime.now().minusMinutes(ThreadLocalRandom.current().nextInt(1, 60))
                    ));
                }
            }
            return incidents;
        }

        public List<SecurityRisk> predictSecurity() {
            List<SecurityRisk> risks = new ArrayList<>();
            if (ThreadLocalRandom.current().nextDouble() > 0.7) {
                String[] riskTypes = {"Brute Force Attempt", "Suspicious Traffic",
                                      "Unauthorized Access", "Data Exfiltration", "Malware Detection"};
                int count = ThreadLocalRandom.current().nextInt(1, 3);
                for (int i = 0; i < count; i++) {
                    String type = riskTypes[ThreadLocalRandom.current().nextInt(riskTypes.length)];
                    String level = new String[]{"LOW", "MEDIUM", "HIGH"}[ThreadLocalRandom.current().nextInt(3)];
                    risks.add(new SecurityRisk(
                            type, level,
                            "Potential security threat: " + type,
                            0.2 + ThreadLocalRandom.current().nextDouble() * 0.6,
                            LocalDateTime.now().minusMinutes(ThreadLocalRandom.current().nextInt(5, 120))
                    ));
                }
            }
            return risks;
        }
    }

    // ======================== COMPONENTES DA UI ========================
    private final AdvancedPredictionService predictionService = new AdvancedPredictionService();
    private List<PredictionResult> predictions = new ArrayList<>();
    private List<SystemIncident> incidents = new ArrayList<>();
    private List<SecurityRisk> risks = new ArrayList<>();
    private LocalDateTime lastUpdate = null;
    private boolean autoRefresh = true;

    // Painéis customizados para gráficos
    private CpuChartPanel cpuChartPanel;
    private HeatmapPanel heatmapPanel;
    private JLabel cpuConfLabel, incidentsLabel, riskLabel, lastUpdateLabel;
    private JPanel incidentLogPanel;
    private JPanel securityPanel;

    public PredictionDashboard() {
        super("🧠 Painel de Previsão Preditiva Avançada");
        setupUI();
        refreshData(); // carrega dados iniciais
        startAutoRefresh();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Centro: Charts
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setOpaque(false);
        cpuChartPanel = new CpuChartPanel();
        cpuChartPanel.setBorder(new LineBorder(Color.DARK_GRAY));
        heatmapPanel = new HeatmapPanel();
        heatmapPanel.setBorder(new LineBorder(Color.DARK_GRAY));
        centerPanel.add(wrapInTitledPanel(cpuChartPanel, "📈 Horizonte de Carga (CPU)", new Color(0x06B6D4)));
        centerPanel.add(wrapInTitledPanel(heatmapPanel, "🔥 Mapa de Calor de Anomalias", new Color(0xF59E0B)));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Sul: Logs e riscos
        JPanel southPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        southPanel.setOpaque(false);
        incidentLogPanel = new JPanel();
        incidentLogPanel.setBackground(new Color(0x111827));
        incidentLogPanel.setLayout(new BoxLayout(incidentLogPanel, BoxLayout.Y_AXIS));
        JScrollPane incidentScroll = new JScrollPane(incidentLogPanel);
        incidentScroll.setBorder(null);
        incidentScroll.setPreferredSize(new Dimension(0, 150));
        southPanel.add(wrapInTitledPanel(incidentScroll, "⚠️ LOG DE PREDIÇÃO DE INCIDENTES (NLP)", new Color(0xEF4444)));

        securityPanel = new JPanel();
        securityPanel.setBackground(new Color(0x111827));
        securityPanel.setLayout(new BoxLayout(securityPanel, BoxLayout.Y_AXIS));
        JScrollPane secScroll = new JScrollPane(securityPanel);
        secScroll.setBorder(null);
        secScroll.setPreferredSize(new Dimension(0, 120));
        southPanel.add(wrapInTitledPanel(secScroll, "🛡️ RISCO DE SEGURANÇA", new Color(0x06B6D4)));
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x0A0A0A));
        header.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Título
        JLabel title = new JLabel("● PREVISÃO PREDITIVA AVANÇADA");
        title.setForeground(new Color(0x06B6D4));
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        JLabel subtitle = new JLabel("CONE DE INCERTEZA • DETECÇÃO DE ANOMALIAS");
        subtitle.setForeground(new Color(0x06B6D4));
        subtitle.setFont(new Font("Monospaced", Font.PLAIN, 10));

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(title, BorderLayout.NORTH);
        left.add(subtitle, BorderLayout.SOUTH);
        header.add(left, BorderLayout.WEST);

        // Controles
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);
        JButton refreshBtn = new JButton("🔄 Atualizar");
        refreshBtn.setBackground(new Color(0x1E293B));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> refreshData());
        controls.add(refreshBtn);

        JCheckBox autoRefreshChk = new JCheckBox("Auto-refresh (3s)", true);
        autoRefreshChk.setForeground(Color.WHITE);
        autoRefreshChk.setOpaque(false);
        autoRefreshChk.addActionListener(e -> autoRefresh = autoRefreshChk.isSelected());
        controls.add(autoRefreshChk);
        header.add(controls, BorderLayout.EAST);

        // Métricas
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        metricsPanel.setOpaque(false);
        cpuConfLabel = createMetricLabel("CONFIANÇA CPU");
        incidentsLabel = createMetricLabel("INCIDENTES ATIVOS");
        riskLabel = createMetricLabel("RISCO SEGURANÇA");
        lastUpdateLabel = createMetricLabel("ÚLTIMA ATUALIZAÇÃO");
        metricsPanel.add(createMetricBox(cpuConfLabel));
        metricsPanel.add(createMetricBox(incidentsLabel));
        metricsPanel.add(createMetricBox(riskLabel));
        metricsPanel.add(createMetricBox(lastUpdateLabel));

        JPanel combined = new JPanel(new BorderLayout());
        combined.setOpaque(false);
        combined.add(header, BorderLayout.NORTH);
        combined.add(metricsPanel, BorderLayout.SOUTH);
        return combined;
    }

    private JLabel createMetricLabel(String text) {
        JLabel label = new JLabel("--", SwingConstants.CENTER);
        label.setForeground(new Color(0x06B6D4));
        label.setFont(new Font("Monospaced", Font.BOLD, 24));
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel title = new JLabel(text, SwingConstants.CENTER);
        title.setForeground(new Color(0x9CA3AF));
        title.setFont(new Font("Monospaced", Font.PLAIN, 10));
        p.add(title, BorderLayout.NORTH);
        p.add(label, BorderLayout.CENTER);
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(0x1E293B));
        outer.setBorder(new LineBorder(new Color(0x374151)));
        outer.add(p, BorderLayout.CENTER);
        // hack: store reference in client property?
        label.putClientProperty("parent", outer);
        return label;
    }

    private JPanel createMetricBox(JLabel valueLabel) {
        JPanel box = (JPanel) valueLabel.getClientProperty("parent");
        return box;
    }

    private JPanel wrapInTitledPanel(JComponent component, String title, Color titleColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x111827));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(titleColor);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void refreshData() {
        predictions = predictionService.predictResources();
        incidents = predictionService.predictIncidents();
        // mantém histórico? Vamos apenas substituir, mas poderia acumular
        risks = predictionService.predictSecurity();
        lastUpdate = LocalDateTime.now();

        // Atualiza labels
        PredictionResult cpu = predictions.stream()
                .filter(p -> p.resourceType == ResourceType.CPU).findFirst().orElse(null);
        cpuConfLabel.setText(cpu != null ? String.format("%.1f%%", cpu.confidence * 100) : "75.5%");
        incidentsLabel.setText(String.valueOf(incidents.size()));
        riskLabel.setText(String.valueOf(risks.size()));
        lastUpdateLabel.setText(lastUpdate.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // Atualiza gráficos
        cpuChartPanel.setData(cpu);
        heatmapPanel.updateData(); // gera novos dados aleatórios
        cpuChartPanel.repaint();
        heatmapPanel.repaint();

        // Atualiza lista de incidentes
        updateIncidentLog();
        updateSecurityRisks();

        revalidate();
        repaint();
    }

    private void updateIncidentLog() {
        incidentLogPanel.removeAll();
        if (incidents.isEmpty()) {
            JLabel stable = new JLabel("✅ Sistema estável. Nenhuma anomalia prevista.");
            stable.setForeground(Color.GREEN);
            stable.setBorder(new EmptyBorder(10, 10, 10, 10));
            incidentLogPanel.add(stable);
        } else {
            for (SystemIncident inc : incidents) {
                JPanel incidentRow = new JPanel(new BorderLayout());
                incidentRow.setBackground(new Color(0x1E293B));
                incidentRow.setBorder(new EmptyBorder(8, 8, 8, 8));

                Color severityColor = switch (inc.severity) {
                    case CRITICAL -> new Color(0xEF4444);
                    case HIGH -> new Color(0xFBBF24);
                    case MEDIUM -> new Color(0xF59E0B);
                    case LOW -> new Color(0x06B6D4);
                };

                JPanel left = new JPanel(new BorderLayout());
                left.setOpaque(false);
                JLabel typeLbl = new JLabel(inc.type);
                typeLbl.setForeground(severityColor);
                typeLbl.setFont(new Font("Monospaced", Font.BOLD, 13));
                JLabel desc = new JLabel(inc.description);
                desc.setForeground(new Color(0x9CA3AF));
                desc.setFont(new Font("Monospaced", Font.PLAIN, 10));
                left.add(typeLbl, BorderLayout.NORTH);
                left.add(desc, BorderLayout.SOUTH);

                JPanel right = new JPanel(new BorderLayout());
                right.setOpaque(false);
                JLabel prob = new JLabel(String.format("%.0f%% PROB", inc.probability * 100));
                prob.setForeground(new Color(0x06B6D4));
                prob.setFont(new Font("Monospaced", Font.BOLD, 12));
                JLabel time = new JLabel(inc.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                time.setForeground(new Color(0x6B7280));
                time.setFont(new Font("Monospaced", Font.PLAIN, 9));
                right.add(prob, BorderLayout.NORTH);
                right.add(time, BorderLayout.SOUTH);

                incidentRow.add(left, BorderLayout.CENTER);
                incidentRow.add(right, BorderLayout.EAST);
                incidentLogPanel.add(incidentRow);
                incidentLogPanel.add(Box.createVerticalStrut(5));
            }
        }
        incidentLogPanel.revalidate();
        incidentLogPanel.repaint();
    }

    private void updateSecurityRisks() {
        securityPanel.removeAll();
        if (risks.isEmpty()) {
            JLabel none = new JLabel("✅ Nenhum risco de segurança detectado.");
            none.setForeground(Color.GREEN);
            none.setBorder(new EmptyBorder(5,5,5,5));
            securityPanel.add(none);
        } else {
            for (SecurityRisk risk : risks) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(new Color(0x1E293B));
                row.setBorder(new EmptyBorder(6,6,6,6));

                Color levelColor = switch (risk.level) {
                    case "HIGH" -> new Color(0xEF4444);
                    case "MEDIUM" -> new Color(0xF59E0B);
                    case "LOW" -> new Color(0x06B6D4);
                    default -> Color.GRAY;
                };

                JPanel left = new JPanel(new BorderLayout());
                left.setOpaque(false);
                JLabel typeLbl = new JLabel(risk.type);
                typeLbl.setForeground(Color.WHITE);
                typeLbl.setFont(new Font("Monospaced", Font.BOLD, 12));
                JLabel desc = new JLabel(risk.description);
                desc.setForeground(new Color(0x9CA3AF));
                desc.setFont(new Font("Monospaced", Font.PLAIN, 10));
                left.add(typeLbl, BorderLayout.NORTH);
                left.add(desc, BorderLayout.SOUTH);

                JPanel right = new JPanel(new BorderLayout());
                right.setOpaque(false);
                JLabel levelLbl = new JLabel(risk.level);
                levelLbl.setForeground(levelColor);
                levelLbl.setFont(new Font("Monospaced", Font.BOLD, 12));
                JLabel prob = new JLabel(String.format("%.0f%%", risk.probability * 100));
                prob.setForeground(new Color(0x06B6D4));
                prob.setFont(new Font("Monospaced", Font.BOLD, 14));
                right.add(levelLbl, BorderLayout.NORTH);
                right.add(prob, BorderLayout.SOUTH);

                row.add(left, BorderLayout.CENTER);
                row.add(right, BorderLayout.EAST);
                securityPanel.add(row);
                securityPanel.add(Box.createVerticalStrut(3));
            }
        }
        securityPanel.revalidate();
        securityPanel.repaint();
    }

    private void startAutoRefresh() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (autoRefresh) {
                    SwingUtilities.invokeLater(() -> refreshData());
                }
            }
        }, 3000, 3000);
    }

    // ======================== GRÁFICOS CUSTOMIZADOS ========================

    /** Gráfico de linha com cone de incerteza preenchido. */
    static class CpuChartPanel extends JPanel {
        private List<Double> predictions;
        private List<Double> upperBound;
        private List<Double> lowerBound;

        public CpuChartPanel() {
            setBackground(new Color(0x0A0A0A));
            setPreferredSize(new Dimension(400, 300));
            // dados iniciais vazios
            predictions = new ArrayList<>();
            upperBound = new ArrayList<>();
            lowerBound = new ArrayList<>();
        }

        public void setData(PredictionResult cpuResult) {
            if (cpuResult == null) {
                // dados sintéticos de fallback
                predictions = new ArrayList<>();
                upperBound = new ArrayList<>();
                lowerBound = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    double base = 45 + i * 2 + (Math.random() - 0.5) * 6;
                    predictions.add(base);
                    upperBound.add(Math.min(100, base + i * 2));
                    lowerBound.add(Math.max(0, base - i * 2));
                }
            } else {
                predictions = new ArrayList<>(cpuResult.predictions);
                upperBound = new ArrayList<>();
                lowerBound = new ArrayList<>();
                for (int i = 0; i < predictions.size(); i++) {
                    double v = predictions.get(i);
                    upperBound.add(Math.min(100, v + i * 2));
                    lowerBound.add(Math.max(0, v - i * 2));
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            if (predictions.isEmpty()) return;

            int n = predictions.size();
            double[] x = new double[n];
            double step = (double) w / (n - 1);
            for (int i = 0; i < n; i++) x[i] = i * step;

            // Escala Y: 0-100
            double yScale = h / 100.0;

            // Preenche cone de incerteza
            GeneralPath area = new GeneralPath();
            area.moveTo(x[0], h - lowerBound.get(0) * yScale);
            for (int i = 1; i < n; i++) area.lineTo(x[i], h - lowerBound.get(i) * yScale);
            for (int i = n - 1; i >= 0; i--) area.lineTo(x[i], h - upperBound.get(i) * yScale);
            area.closePath();
            g2.setColor(new Color(6, 182, 212, 40));
            g2.fill(area);
            g2.setColor(new Color(6, 182, 212, 80));
            g2.draw(area);

            // Linha principal
            g2.setColor(new Color(6, 182, 212));
            g2.setStroke(new BasicStroke(2.5f));
            for (int i = 1; i < n; i++) {
                g2.drawLine((int)x[i-1], (int)(h - predictions.get(i-1)*yScale),
                            (int)x[i], (int)(h - predictions.get(i)*yScale));
            }

            // Pontos
            g2.setColor(Color.WHITE);
            for (int i = 0; i < n; i++) {
                int y = (int)(h - predictions.get(i) * yScale);
                g2.fillOval((int)x[i]-3, y-3, 6, 6);
            }

            // Eixos
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(30, 20, 30, h-30);
            g2.drawLine(30, h-30, w-20, h-30);
            // Labels simples
            g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= 100; i+=20) {
                int y = (int)(h - i * yScale);
                g2.drawString(i+"%", 2, y+4);
                g2.drawLine(25, y, 30, y);
            }
        }
    }

    /** Mapa de calor simples 6x8 */
    static class HeatmapPanel extends JPanel {
        private double[][] data; // 6 linhas x 8 colunas

        HeatmapPanel() {
            setBackground(new Color(0x0A0A0A));
            setPreferredSize(new Dimension(400, 300));
            data = new double[6][8];
            updateData();
        }

        public void updateData() {
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 8; j++)
                    data[i][j] = ThreadLocalRandom.current().nextDouble();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            int cellW = (w - 40) / 8;
            int cellH = (h - 40) / 6;
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 8; j++) {
                    double value = data[i][j];
                    // Mapeia para cor: frio->azul escuro, quente->vermelho
                    Color color = interpolateColor(value);
                    g2.setColor(color);
                    g2.fillRect(30 + j * cellW, 20 + i * cellH, cellW, cellH);
                    g2.setColor(Color.WHITE);
                    g2.drawRect(30 + j * cellW, 20 + i * cellH, cellW, cellH);
                }
            }
            // Labels
            g2.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 8; i++)
                g2.drawString("C"+(i+1), 30 + i*cellW + cellW/2 - 10, 15);
            for (int i = 0; i < 6; i++)
                g2.drawString("R"+(i+1), 5, 20 + i*cellH + cellH/2 + 4);
        }

        private Color interpolateColor(double value) {
            // Escala: baixo->azul escuro, médio->laranja, alto->vermelho
            if (value < 0.4) return new Color(30, 41, 59); // escuro
            else if (value < 0.7) return new Color(30, 64, 175); // azul
            else if (value < 0.9) return new Color(245, 158, 11); // laranja
            else return new Color(220, 38, 38); // vermelho
        }
    }

    // ======================== MAIN ========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PredictionDashboard::new);
    }
}
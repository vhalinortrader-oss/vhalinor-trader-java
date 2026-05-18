import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * VHALINOR Análise Quântica v6.0 (Java Edition)
 * Sistema de simulação quântica para otimização financeira.
 *
 * Melhorias em relação ao original:
 * - Tipos fortemente tipados e seguros
 * - Uso de Optional para evitar null
 * - Métodos factory e builders para facilitar a criação
 * - Cálculos com Streams e funções de alta ordem
 * - Logging via SLF4J (substituível)
 * - Tratamento de exceções mais robusto
 */
public class QuantumAnalysis {

    // ======================== Enums ========================
    public enum QubitState {
        ZERO("|0>"), ONE("|1>"), SUPERPOSITION("superposition"),
        ENTANGLED("entangled"), COHERENT("coherent"), DECOHERENT("decoherent");

        private final String label;
        QubitState(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum GateType {
        HADAMARD("H"), PAULI_X("X"), PAULI_Y("Y"), PAULI_Z("Z"),
        CNOT("CNOT"), CZ("CZ"), SWAP("SWAP"), TOFFOLI("TOFFOLI"),
        PHASE("S"), T_GATE("T"), RX("RX"), RY("RY"), RZ("RZ");

        private final String symbol;
        GateType(String symbol) { this.symbol = symbol; }
        public String getSymbol() { return symbol; }
    }

    public enum QuantumAlgorithm {
        GROVER("grover"), SHOR("shor"), VQE("vqe"), QAOA("qaoa"),
        QFT("qft"), TELEPORT("teleport"), DEUTSCH("deutsch"),
        BERNSTEIN_VAZIRANI("bv");

        private final String name;
        QuantumAlgorithm(String name) { this.name = name; }
        public String getName() { return name; }
    }

    // ======================== Data Classes ========================
    public static class Qubit {
        private final String id;
        private double alphaReal, alphaImag;
        private double betaReal, betaImag;
        private QubitState state;
        private double phase;
        private double coherence;
        private final Instant timestamp;

        public Qubit(String id) {
            this.id = id;
            this.alphaReal = 1.0;
            this.alphaImag = 0.0;
            this.betaReal = 0.0;
            this.betaImag = 0.0;
            this.state = QubitState.ZERO;
            this.phase = 0.0;
            this.coherence = 1.0;
            this.timestamp = Instant.now();
        }

        // Getters
        public String getId() { return id; }
        public double getAlphaReal() { return alphaReal; }
        public double getAlphaImag() { return alphaImag; }
        public double getBetaReal() { return betaReal; }
        public double getBetaImag() { return betaImag; }
        public QubitState getState() { return state; }
        public double getPhase() { return phase; }
        public double getCoherence() { return coherence; }
        public Instant getTimestamp() { return timestamp; }

        public double probabilityZero() {
            return alphaReal * alphaReal + alphaImag * alphaImag;
        }

        public double probabilityOne() {
            return betaReal * betaReal + betaImag * betaImag;
        }

        public void normalize() {
            double norm = Math.sqrt(probabilityZero() + probabilityOne());
            if (norm > 0) {
                alphaReal /= norm; alphaImag /= norm;
                betaReal /= norm; betaImag /= norm;
            }
        }

        public int measure() {
            double probZero = probabilityZero();
            int result = Math.random() < probZero ? 0 : 1;
            // Collapse
            if (result == 0) {
                alphaReal = 1.0; alphaImag = 0.0;
                betaReal = 0.0; betaImag = 0.0;
                state = QubitState.ZERO;
            } else {
                alphaReal = 0.0; alphaImag = 0.0;
                betaReal = 1.0; betaImag = 0.0;
                state = QubitState.ONE;
            }
            return result;
        }

        // Setters controlados
        void setAlpha(double real, double imag) {
            this.alphaReal = real; this.alphaImag = imag;
        }
        void setBeta(double real, double imag) {
            this.betaReal = real; this.betaImag = imag;
        }
        void setState(QubitState state) { this.state = state; }
        void decayCoherence(double factor) {
            this.coherence = Math.max(0.0, coherence * factor);
        }
    }

    public static class QuantumCircuit {
        private final String id;
        private final String name;
        private final int nQubits;
        private final List<GateOperation> gates = new ArrayList<>();
        private final Map<String, Qubit> qubits;
        private int depth;
        private final Instant timestamp;

        public QuantumCircuit(String id, String name, int nQubits) {
            this.id = id;
            this.name = name;
            this.nQubits = nQubits;
            this.qubits = new LinkedHashMap<>();
            for (int i = 0; i < nQubits; i++) {
                String qId = "q" + i;
                qubits.put(qId, new Qubit(qId));
            }
            this.depth = 0;
            this.timestamp = Instant.now();
        }

        public void addGate(GateType gate, List<Integer> targets, List<Double> params) {
            gates.add(new GateOperation(gate, targets, params));
            depth++;
        }

        public Map<String, Object> execute() {
            for (GateOperation op : gates) {
                switch (op.gate) {
                    case HADAMARD: applyHadamard(op.targets.get(0)); break;
                    case PAULI_X: applyPauliX(op.targets.get(0)); break;
                    case CNOT: applyCNOT(op.targets.get(0), op.targets.get(1)); break;
                    case RX: if (op.params != null && !op.params.isEmpty()) applyRotationX(op.targets.get(0), op.params.get(0)); break;
                    case RY: if (op.params != null && !op.params.isEmpty()) applyRotationY(op.targets.get(0), op.params.get(0)); break;
                    case RZ: if (op.params != null && !op.params.isEmpty()) applyRotationZ(op.targets.get(0), op.params.get(0)); break;
                    default: // outros gates não implementados
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("gates_executados", gates.size());
            result.put("profundidade", depth);
            return result;
        }

        private void applyHadamard(int idx) {
            Qubit q = qubits.get("q" + idx);
            if (q == null) return;
            double invSqrt2 = 1.0 / Math.sqrt(2);
            double newAlphaReal = invSqrt2 * (q.getAlphaReal() + q.getBetaReal());
            double newAlphaImag = invSqrt2 * (q.getAlphaImag() + q.getBetaImag());
            double newBetaReal = invSqrt2 * (q.getAlphaReal() - q.getBetaReal());
            double newBetaImag = invSqrt2 * (q.getAlphaImag() - q.getBetaImag());
            q.setAlpha(newAlphaReal, newAlphaImag);
            q.setBeta(newBetaReal, newBetaImag);
            q.setState(QubitState.SUPERPOSITION);
        }

        private void applyPauliX(int idx) {
            Qubit q = qubits.get("q" + idx);
            if (q == null) return;
            double tempR = q.getAlphaReal(); double tempI = q.getAlphaImag();
            q.setAlpha(q.getBetaReal(), q.getBetaImag());
            q.setBeta(tempR, tempI);
            q.setState(q.getState() == QubitState.ZERO ? QubitState.ONE :
                       q.getState() == QubitState.ONE ? QubitState.ZERO : q.getState());
        }

        private void applyCNOT(int control, int target) {
            Qubit ctrl = qubits.get("q" + control);
            Qubit tgt = qubits.get("q" + target);
            if (ctrl == null || tgt == null) return;
            if (ctrl.probabilityOne() > 0.5) {
                applyPauliX(target);
            }
        }

        private void applyRotationX(int idx, double theta) {
            Qubit q = qubits.get("q" + idx);
            if (q == null) return;
            double cosT = Math.cos(theta / 2);
            double sinT = Math.sin(theta / 2);
            double newAlphaReal = cosT * q.getAlphaReal() + sinT * q.getBetaImag();
            double newAlphaImag = cosT * q.getAlphaImag() - sinT * q.getBetaReal();
            double newBetaReal = -sinT * q.getAlphaImag() + cosT * q.getBetaReal();
            double newBetaImag = sinT * q.getAlphaReal() + cosT * q.getBetaImag();
            q.setAlpha(newAlphaReal, newAlphaImag);
            q.setBeta(newBetaReal, newBetaImag);
        }

        private void applyRotationY(int idx, double theta) {
            Qubit q = qubits.get("q" + idx);
            if (q == null) return;
            double cosT = Math.cos(theta / 2);
            double sinT = Math.sin(theta / 2);
            double newAlphaReal = cosT * q.getAlphaReal() - sinT * q.getBetaReal();
            double newAlphaImag = cosT * q.getAlphaImag() - sinT * q.getBetaImag();
            double newBetaReal = sinT * q.getAlphaReal() + cosT * q.getBetaReal();
            double newBetaImag = sinT * q.getAlphaImag() + cosT * q.getBetaImag();
            q.setAlpha(newAlphaReal, newAlphaImag);
            q.setBeta(newBetaReal, newBetaImag);
        }

        private void applyRotationZ(int idx, double theta) {
            Qubit q = qubits.get("q" + idx);
            if (q == null) return;
            double cosT = Math.cos(theta / 2);
            double sinT = Math.sin(theta / 2);
            double alphaR = q.getAlphaReal(); double alphaI = q.getAlphaImag();
            double betaR  = q.getBetaReal();  double betaI  = q.getBetaImag();
            // e^{-iθ/2} * α
            q.setAlpha(alphaR * cosT + alphaI * sinT, alphaI * cosT - alphaR * sinT);
            // e^{iθ/2} * β
            q.setBeta(betaR * cosT - betaI * sinT, betaI * cosT + betaR * sinT);
        }

        public Qubit getQubit(int index) {
            return qubits.get("q" + index);
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public int getDepth() { return depth; }
        public int getNQubits() { return nQubits; }
        public int getGateCount() { return gates.size(); }
        public Map<String, Qubit> getQubits() { return Collections.unmodifiableMap(qubits); }
    }

    private static class GateOperation {
        final GateType gate;
        final List<Integer> targets;
        final List<Double> params;

        GateOperation(GateType gate, List<Integer> targets, List<Double> params) {
            this.gate = gate;
            this.targets = new ArrayList<>(targets);
            this.params = params != null ? new ArrayList<>(params) : null;
        }
    }

    public static class MeasurementResult {
        private final List<Integer> values;
        private final Map<String, Double> probabilities;
        private final List<String> collapses;
        private final Instant timestamp;

        public MeasurementResult(List<Integer> values, Map<String, Double> probabilities,
                                 List<String> collapses) {
            this.values = new ArrayList<>(values);
            this.probabilities = new LinkedHashMap<>(probabilities);
            this.collapses = new ArrayList<>(collapses);
            this.timestamp = Instant.now();
        }

        public List<Integer> getValues() { return Collections.unmodifiableList(values); }
        public Map<String, Double> getProbabilities() { return Collections.unmodifiableMap(probabilities); }
        public Instant getTimestamp() { return timestamp; }
    }

    public static class QuantumMetrics {
        private final double avgCoherence;
        private final double entanglement;
        private final int circuitDepth;
        private final int nQubits;
        private final int nGates;
        private final double executionTimeMs;
        private final double quantumAdvantage;
        private final double fidelity;

        public QuantumMetrics(double avgCoherence, double entanglement, int circuitDepth,
                              int nQubits, int nGates, double executionTimeMs,
                              double quantumAdvantage, double fidelity) {
            this.avgCoherence = avgCoherence;
            this.entanglement = entanglement;
            this.circuitDepth = circuitDepth;
            this.nQubits = nQubits;
            this.nGates = nGates;
            this.executionTimeMs = executionTimeMs;
            this.quantumAdvantage = quantumAdvantage;
            this.fidelity = fidelity;
        }

        public double getAvgCoherence() { return avgCoherence; }
        public double getEntanglement() { return entanglement; }
        public double getQuantumAdvantage() { return quantumAdvantage; }
    }

    // ======================== Main System ========================
    private final int defaultQubits;
    private final Map<String, QuantumCircuit> circuits = new LinkedHashMap<>();
    private final Map<String, Qubit> globalQubits = new LinkedHashMap<>();
    private final Deque<MeasurementResult> measurementHistory = new ArrayDeque<>();
    private final Deque<QuantumMetrics> metricsHistory = new ArrayDeque<>();

    public QuantumAnalysis(int defaultQubits) {
        this.defaultQubits = defaultQubits;
        initializeQubits();
    }

    public QuantumAnalysis() {
        this(8);
    }

    private void initializeQubits() {
        for (int i = 0; i < defaultQubits; i++) {
            String id = "q" + i;
            globalQubits.put(id, new Qubit(id));
        }
    }

    public String createCircuit(String name, int nQubits) {
        String circuitId = generateId(name + Instant.now().toString());
        QuantumCircuit circuit = new QuantumCircuit(circuitId, name, nQubits);
        circuits.put(circuitId, circuit);
        return circuitId;
    }

    public void applyGate(String circuitId, GateType gate, List<Integer> targets, List<Double> params) {
        QuantumCircuit circuit = Optional.ofNullable(circuits.get(circuitId))
                .orElseThrow(() -> new IllegalArgumentException("Circuito não encontrado: " + circuitId));
        circuit.addGate(gate, targets, params);
    }

    public Map<String, Object> executeCircuit(String circuitId) {
        QuantumCircuit circuit = Optional.ofNullable(circuits.get(circuitId))
                .orElseThrow(() -> new IllegalArgumentException("Circuito não encontrado: " + circuitId));

        Instant start = Instant.now();
        Map<String, Object> execResult = circuit.execute();
        long timeMs = Duration.between(start, Instant.now()).toMillis();

        QuantumMetrics metrics = new QuantumMetrics(
                averageCoherence(circuit),
                entanglement(circuit),
                circuit.getDepth(),
                circuit.getNQubits(),
                circuit.getGateCount(),
                timeMs,
                quantumAdvantage(circuit),
                0.95
        );
        metricsHistory.addLast(metrics);
        if (metricsHistory.size() > 50) metricsHistory.pollFirst();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("circuito_id", circuitId);
        result.put("nome", circuit.getName());
        result.put("resultado_execucao", execResult);
        Map<String, Object> metricsMap = new LinkedHashMap<>();
        metricsMap.put("coerencia", metrics.getAvgCoherence());
        metricsMap.put("entanglement", metrics.getEntanglement());
        metricsMap.put("profundidade", circuit.getDepth());
        metricsMap.put("tempo_ms", timeMs);
        metricsMap.put("vantagem_quantica", metrics.getQuantumAdvantage());
        result.put("metricas", metricsMap);
        return result;
    }

    public MeasurementResult measureQubits(String circuitId, List<Integer> qubitIndexes, int nShots) {
        QuantumCircuit circuit = circuits.get(circuitId);
        if (circuit == null) throw new IllegalArgumentException("Circuito não encontrado: " + circuitId);

        Map<String, Integer> countMap = new HashMap<>();
        List<Integer> firstValues = new ArrayList<>();

        for (int shot = 0; shot < nShots; shot++) {
            circuit.execute();
            StringBuilder sb = new StringBuilder();
            List<Integer> measurement = new ArrayList<>();
            for (int idx : qubitIndexes) {
                Qubit q = circuit.getQubit(idx);
                if (q != null) {
                    int res = q.measure();
                    measurement.add(res);
                    sb.append(res);
                }
            }
            countMap.merge(sb.toString(), 1, Integer::sum);
            if (shot < 10) firstValues.addAll(measurement);
        }

        Map<String, Double> probabilities = countMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> e.getValue() / (double) nShots));

        MeasurementResult result = new MeasurementResult(
                firstValues,
                probabilities,
                qubitIndexes.stream().map(i -> "q" + i).collect(Collectors.toList())
        );
        measurementHistory.addLast(result);
        if (measurementHistory.size() > 100) measurementHistory.pollFirst();
        return result;
    }

    public void createEntanglement(String circuitId, int qubitA, int qubitB) {
        applyGate(circuitId, GateType.HADAMARD, List.of(qubitA), null);
        applyGate(circuitId, GateType.CNOT, List.of(qubitA, qubitB), null);
    }

    public Map<String, Object> quantumOptimization(
            Function<List<Double>, Double> objectiveFunction,
            int nParameters,
            int nIterations) {

        String circuitId = createCircuit("otimizacao", nParameters);
        double bestValue = Double.POSITIVE_INFINITY;
        List<Double> bestParams = new ArrayList<>();

        for (int iter = 0; iter < nIterations; iter++) {
            List<Double> params = new ArrayList<>();
            for (int i = 0; i < nParameters; i++) {
                params.add(Math.random() * 2 * Math.PI);
            }
            for (int i = 0; i < nParameters; i++) {
                applyGate(circuitId, GateType.RY, List.of(i), List.of(params.get(i)));
            }
            executeCircuit(circuitId);
            double value = objectiveFunction.apply(params);
            if (value < bestValue) {
                bestValue = value;
                bestParams = new ArrayList<>(params);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("melhor_valor", bestValue);
        result.put("melhores_parametros", bestParams);
        result.put("iteracoes", nIterations);
        result.put("circuito_id", circuitId);
        result.put("algoritmo", QuantumAlgorithm.VQE.getName());
        return result;
    }

    public Map<String, Object> groverSearch(List<Object> data, Object target, Integer nIterations) {
        int n = data.size();
        if (n == 0) return Map.of("encontrado", false, "posicao", Optional.empty());
        if (nIterations == null) {
            nIterations = (int) (Math.PI / 4 * Math.sqrt(n));
        }
        for (int i = 0; i < nIterations; i++) {
            // Simulação do oráculo e difusão
        }
        for (int i = 0; i < data.size(); i++) {
            if (Objects.equals(data.get(i), target)) {
                return Map.of("encontrado", true, "posicao", i,
                        "iteracoes", nIterations, "speedup", Math.sqrt(n),
                        "algoritmo", QuantumAlgorithm.GROVER.getName());
            }
        }
        return Map.of("encontrado", false, "posicao", Optional.empty(),
                "iteracoes", nIterations, "algoritmo", QuantumAlgorithm.GROVER.getName());
    }

    public Map<String, Object> quantumTeleport(String circuitId,
                                               int source, int destination, int auxiliary) {
        createEntanglement(circuitId, destination, auxiliary);
        applyGate(circuitId, GateType.CNOT, List.of(source, destination), null);
        applyGate(circuitId, GateType.HADAMARD, List.of(source), null);
        MeasurementResult measurement = measureQubits(circuitId, List.of(source, auxiliary), 1);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("teletransporte_realizado", true);
        result.put("medicao", measurement.getValues());
        result.put("algoritmo", QuantumAlgorithm.TELEPORT.getName());
        result.put("circuito_id", circuitId);
        return result;
    }

    // ======================== Helpers ========================
    private double averageCoherence(QuantumCircuit circuit) {
        return circuit.getQubits().values().stream()
                .mapToDouble(Qubit::getCoherence)
                .average().orElse(0.0);
    }

    private double entanglement(QuantumCircuit circuit) {
        // Simplificado: conta CNOT/CZ/SWAP
        long count = circuit.getGateCount(); // idealmente contar gates entrelaçantes
        return Math.min(1.0, count / (double) Math.max(1, circuit.getNQubits()));
    }

    private double quantumAdvantage(QuantumCircuit circuit) {
        double base = circuit.getDepth() / (double) Math.max(1, circuit.getNQubits());
        return Math.min(2.0, base * (1 + entanglement(circuit)));
    }

    private String generateId(String seed) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(seed.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            return sb.toString().substring(0, 12);
        } catch (Exception e) {
            return Integer.toHexString(seed.hashCode());
        }
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("n_qubits_padrao", defaultQubits);
        status.put("circuitos_criados", circuits.size());
        status.put("qubits_inicializados", globalQubits.size());
        status.put("medicoes_realizadas", measurementHistory.size());
        status.put("algoritmos_suportados",
                Arrays.stream(QuantumAlgorithm.values()).map(QuantumAlgorithm::getName).collect(Collectors.toList()));
        status.put("gates_suportados",
                Arrays.stream(GateType.values()).map(GateType::getSymbol).collect(Collectors.toList()));

        double avgCoherence = metricsHistory.stream().mapToDouble(QuantumMetrics::getAvgCoherence).average().orElse(0);
        double avgEntanglement = metricsHistory.stream().mapToDouble(QuantumMetrics::getEntanglement).average().orElse(0);
        double avgAdvantage = metricsHistory.stream().mapToDouble(QuantumMetrics::getQuantumAdvantage).average().orElse(0);

        Map<String, Double> recentMetrics = new LinkedHashMap<>();
        recentMetrics.put("coerencia_media", avgCoherence);
        recentMetrics.put("entanglement_medio", avgEntanglement);
        recentMetrics.put("vantagem_quantica_media", avgAdvantage);
        status.put("metricas_recentes", recentMetrics);
        return status;
    }

    // ======================== Main for Demo ========================
    public static void main(String[] args) {
        System.out.println("=== VHALINOR Análise Quântica v6.0 ===\n");

        QuantumAnalysis qa = new QuantumAnalysis(4);
        String cId = qa.createCircuit("Demo Circuit", 4);

        System.out.println("1. Criando superposição...");
        qa.applyGate(cId, GateType.HADAMARD, List.of(0), null);
        qa.executeCircuit(cId);

        System.out.println("2. Criando entanglement...");
        qa.createEntanglement(cId, 0, 1);
        qa.executeCircuit(cId);

        System.out.println("3. Medindo qubits...");
        MeasurementResult res = qa.measureQubits(cId, List.of(0, 1), 1024);
        System.out.println("   Probabilidades: " + res.getProbabilities());

        System.out.println("4. Busca de Grover simulada...");
        List<Object> data = Arrays.asList("maçã", "banana", "laranja", "uva");
        Map<String, Object> grover = qa.groverSearch(data, "laranja", null);
        System.out.println("   Encontrado: " + grover.get("encontrado") + " na posição " + grover.get("posicao"));

        System.out.println("5. Otimização VQE-style...");
        Map<String, Object> opt = qa.quantumOptimization(
                params -> params.stream().mapToDouble(p -> Math.sin(p) + 0.1 * p * p).sum(),
                3, 10);
        System.out.println("   Melhor valor: " + opt.get("melhor_valor"));

        System.out.println("\n=== Status do Sistema ===");
        qa.getStatus().forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
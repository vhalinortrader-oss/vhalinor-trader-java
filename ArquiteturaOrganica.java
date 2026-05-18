import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * VHALINOR Arquitetura Orgânica v6.0 – Java Edition
 *
 * Sistemas de arquitetura neural biologicamente inspirada:
 * <ul>
 *   <li>Neurônios orgânicos simulados com neurotransmissores</li>
 *   <li>Sinapses plásticas (LTP/LTD)</li>
 *   <li>Redes corticais com áreas especializadas</li>
 *   <li>Sistemas límbico e cortical</li>
 *   <li>Neuroplasticidade e pruning sináptico</li>
 *   <li>Ritmos circadianos e homeostase</li>
 *   <li>Emoções como estados neuroquímicos</li>
 *   <li>Memória de trabalho e consolidação</li>
 *   <li>Neurogênese e apoptose</li>
 * </ul>
 *
 * Melhorias:
 * <ul>
 *   <li>Implementação thread‑safe com estruturas concorrentes</li>
 *   <li>Simulação de ciclo com agendamento configurável</li>
 *   <li>Operações atômicas para métricas</li>
 *   <li>Métodos de acesso seguro às estruturas neurais</li>
 * </ul>
 */
public class ArquiteturaOrganica {

    // ======================== Enums ========================
    public enum TipoNeuronio {
        PIRAMIDAL, INTERNEURONIO, GRANULAR, ASTROGLIA,
        Oligodendrocito, DOPAMINERGICO, SEROTONINERGICO, NORADRENERGICO,
        COLINERGICO, GABAERGICO, GLUTAMATERGICO
    }

    public enum TipoSinapse {
        AXODENDRITICA, AXOSSOMATICA, AXOAXONICA, DENDRODENDRITICA, ELETTRICA
    }

    public enum EstadoNeurotransmissor {
        BAIXO, NORMAL, ELEVADO, CRITICO
    }

    // ======================== Componentes Biológicos ========================
    public static class Neurotransmissor {
        private final String tipo;
        private volatile double nivel; // 0.0 a 1.0
        private final double taxaSintese;
        private final double taxaReuptake;

        public Neurotransmissor(String tipo, double nivelInicial, double taxaSintese, double taxaReuptake) {
            this.tipo = tipo;
            this.nivel = Math.max(0.0, Math.min(1.0, nivelInicial));
            this.taxaSintese = taxaSintese;
            this.taxaReuptake = taxaReuptake;
        }

        public void atualizar(double estimulo) {
            nivel += taxaSintese + estimulo * 0.1;
            nivel -= nivel * taxaReuptake;
            nivel = Math.max(0.0, Math.min(1.0, nivel));
        }

        public double getNivel() { return nivel; }
        public String getTipo() { return tipo; }
        public EstadoNeurotransmissor getEstado() {
            if (nivel < 0.2) return EstadoNeurotransmissor.BAIXO;
            if (nivel < 0.4) return EstadoNeurotransmissor.NORMAL;
            if (nivel < 0.7) return EstadoNeurotransmissor.ELEVADO;
            return EstadoNeurotransmissor.CRITICO;
        }
    }

    public static class Sinapse {
        private final String id;
        private final String preId;
        private final String posId;
        private final TipoSinapse tipo;
        private volatile double peso;
        private final double pesoOriginal;
        private final String neurotransmissor;

        // Plasticidade
        private volatile double potenciacaoLongoPrazo = 1.0;
        private volatile double depressaoLongoPrazo = 1.0;
        private final Deque<Double> frequenciaAtivacao = new ConcurrentLinkedDeque<>();

        public Sinapse(String id, String preId, String posId, TipoSinapse tipo,
                       double peso, String neurotransmissor) {
            this.id = id;
            this.preId = preId;
            this.posId = posId;
            this.tipo = tipo;
            this.peso = peso;
            this.pesoOriginal = peso;
            this.neurotransmissor = neurotransmissor;
        }

        public double ativar(double forca) {
            frequenciaAtivacao.addLast(forca);
            if (frequenciaAtivacao.size() > 100) frequenciaAtivacao.pollFirst();

            // Hebbian plasticity
            if (frequenciaAtivacao.size() >= 10) {
                double avg = frequenciaAtivacao.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                if (avg > 0.7) {
                    potenciacaoLongoPrazo = Math.min(2.0, potenciacaoLongoPrazo + 0.01);
                } else if (avg < 0.3) {
                    depressaoLongoPrazo = Math.max(0.5, depressaoLongoPrazo - 0.01);
                }
            }
            double pesoEfetivo = peso * potenciacaoLongoPrazo * depressaoLongoPrazo;
            return forca * pesoEfetivo;
        }

        public boolean aplicarPruning(double threshold) {
            return getForcaEfetiva() < threshold;
        }

        public double getForcaEfetiva() {
            return peso * potenciacaoLongoPrazo * depressaoLongoPrazo;
        }

        public String getId() { return id; }
        public String getPreId() { return preId; }
        public String getPosId() { return posId; }
        public double getPeso() { return peso; }
        public void setPeso(double peso) { this.peso = peso; }
        public String getNeurotransmissor() { return neurotransmissor; }
    }

    public static class NeuronioOrganico {
        private final String id;
        private final TipoNeuronio tipo;
        private final double[] posicao3d; // [x, y, z]
        private volatile double potencialMembrana = -70.0;
        private final double limiarDisparo = -55.0;
        private volatile boolean refratario = false;
        private volatile long ultimoDisparo; // timestamp
        private final double tempoRefratarioMs = 2.0;

        private final List<String> dendritos = new ArrayList<>();
        private final List<String> axonio = new ArrayList<>();

        private volatile double energia = 1.0;
        private volatile double oxigenio = 1.0;
        private int idade = 0;
        private final double taxaMortalidade = 0.0001;
        private final List<String> neurotransmissoresProduzidos = new ArrayList<>();

        public NeuronioOrganico(String id, TipoNeuronio tipo, double x, double y, double z) {
            this.id = id;
            this.tipo = tipo;
            this.posicao3d = new double[]{x, y, z};
        }

        public synchronized boolean receberSinal(double forca, String neurotransmissor) {
            if (refratario) return false;
            switch (neurotransmissor) {
                case "glutamato": potencialMembrana += forca * 5; break;
                case "GABA": potencialMembrana -= forca * 3; break;
                case "acetilcolina": potencialMembrana += forca * 2; break;
                // dopamine modula plasticidade (não altera Vm diretamente)
            }
            if (potencialMembrana >= limiarDisparo) {
                return disparar();
            }
            return false;
        }

        public synchronized boolean disparar() {
            if (energia < 0.1 || refratario) return false;
            energia -= 0.05;
            ultimoDisparo = System.currentTimeMillis();
            refratario = true;
            potencialMembrana = -75.0;

            // Schedule reset of refractory period
            ArquiteturaOrganica.executorService.schedule(() -> {
                refratario = false;
                potencialMembrana = -70.0;
            }, (long) tempoRefratarioMs, TimeUnit.MILLISECONDS);
            return true;
        }

        public synchronized void atualizarMetabolismo() {
            energia = Math.min(1.0, energia + 0.01);
            oxigenio = Math.min(1.0, oxigenio + 0.01);
            idade++;
        }

        public boolean isAtivo() { return energia > 0.1 && oxigenio > 0.1; }

        // Getters
        public String getId() { return id; }
        public TipoNeuronio getTipo() { return tipo; }
        public double[] getPosicao3d() { return posicao3d; }
        public int getIdade() { return idade; }
        public double getEnergia() { return energia; }
        public List<String> getAxonio() { return axonio; }
        public List<String> getDendritos() { return dendritos; }
    }

    public static class CamadaCortical {
        private final int numero;
        private final String nome;
        private final List<String> neuronios = new ArrayList<>();
        private String funcaoPrincipal;

        public CamadaCortical(int numero, String nome, String funcao) {
            this.numero = numero;
            this.nome = nome;
            this.funcaoPrincipal = funcao;
        }

        public List<String> getNeuronios() { return neuronios; }
        public int getNumero() { return numero; }
    }

    public static class AreaCortical {
        private final String id;
        private final String nome;
        private final String funcao;
        private final List<CamadaCortical> camadas = new ArrayList<>();
        private final List<String> aferentes = new ArrayList<>();
        private final List<String> eferentes = new ArrayList<>();
        private String especializacao;

        public AreaCortical(String id, String nome, String funcao, String especializacao) {
            this.id = id;
            this.nome = nome;
            this.funcao = funcao;
            this.especializacao = especializacao;
        }

        public void addCamada(CamadaCortical camada) { camadas.add(camada); }
        public List<CamadaCortical> getCamadas() { return camadas; }
        public String getId() { return id; }
    }

    public static class SistemaLimbico {
        private final Map<String, Object> amigdala = new HashMap<>();
        private final Map<String, Object> hipocampo = new HashMap<>();
        private final Map<String, Object> hipotalamo = new HashMap<>();
        private final Map<String, Object> cingulado = new HashMap<>();

        public Map<String, Double> processarEmocao(Map<String, Object> estimulo) {
            double intensidade = ((Number) estimulo.getOrDefault("intensidade", 0.5)).doubleValue();
            double valencia = ((Number) estimulo.getOrDefault("valencia", 0)).doubleValue();
            double ameaca = valencia < 0 ? intensidade : 0;
            Map<String, Double> resposta = new LinkedHashMap<>();
            resposta.put("medo", Math.min(1.0, ameaca * 1.2));
            resposta.put("ansiedade", Math.min(1.0, ameaca * 0.8));
            resposta.put("excitacao", Math.min(1.0, intensidade));
            resposta.put("prazer", Math.max(0.0, valencia * intensidade));
            return resposta;
        }
    }

    // ======================== Arquitetura Principal ========================
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    private final Map<String, NeuronioOrganico> neuronios = new ConcurrentHashMap<>();
    private final Map<String, Sinapse> sinapses = new ConcurrentHashMap<>();
    private final Map<String, Neurotransmissor> neurotransmissoresGlobais = new ConcurrentHashMap<>();
    private final Map<String, AreaCortical> areasCorticais = new LinkedHashMap<>();
    private final SistemaLimbico sistemaLimbico = new SistemaLimbico();

    // Memória
    private final Deque<Map<String, Object>> memoriaTrabalho = new ConcurrentLinkedDeque<>();
    private final Map<String, Object> memoriaCurtoPrazo = new ConcurrentHashMap<>();
    private final Map<String, Object> memoriaLongoPrazo = new ConcurrentHashMap<>();

    // Ritmo e homeostase
    private volatile double ritmoCircadiano = 0.5;
    private volatile double horaSimulada = 8.0;
    private final Map<String, Double> homeostase = new ConcurrentHashMap<>();

    // Contagens e métricas
    private final AtomicLong totalDisparos = new AtomicLong(0);
    private final AtomicLong totalSinapsesCriadas = new AtomicLong(0);
    private final AtomicLong totalSinapsesEliminadas = new AtomicLong(0);
    private final AtomicLong totalNeuroNascidos = new AtomicLong(0);
    private final AtomicLong totalNeuroMortos = new AtomicLong(0);

    private volatile boolean executando = false;
    private ScheduledFuture<?> cicloAutonomoFuture;

    // Configuração inicial
    public ArquiteturaOrganica(int numNeuroniosInicial) {
        homeostase.put("energia_global", 1.0);
        homeostase.put("temperatura", 37.0);
        homeostase.put("ph", 7.4);
        homeostase.put("oxigenacao", 1.0);

        neurotransmissoresGlobais.put("dopamina", new Neurotransmissor("dopamina", 0.3, 0.01, 0.05));
        neurotransmissoresGlobais.put("serotonina", new Neurotransmissor("serotonina", 0.4, 0.01, 0.05));
        neurotransmissoresGlobais.put("noradrenalina", new Neurotransmissor("noradrenalina", 0.2, 0.01, 0.05));
        neurotransmissoresGlobais.put("acetilcolina", new Neurotransmissor("acetilcolina", 0.5, 0.01, 0.05));
        neurotransmissoresGlobais.put("GABA", new Neurotransmissor("GABA", 0.6, 0.01, 0.05));
        neurotransmissoresGlobais.put("glutamato", new Neurotransmissor("glutamato", 0.7, 0.01, 0.05));

        inicializarCortex();
        criarNeuroniosIniciais(numNeuroniosInicial);
    }

    private void inicializarCortex() {
        String[][] areas = {
                {"V1", "Córtex Visual Primário", "sensorial", "processamento_visual"},
                {"A1", "Córtex Auditivo Primário", "sensorial", "processamento_auditivo"},
                {"S1", "Córtex Somatossensorial", "sensorial", "processamento_tatil"},
                {"M1", "Córtex Motor Primário", "motora", "planejamento_motor"},
                {"PFC", "Córtex Pré-Frontal", "executiva", "cognicao_superior"},
                {"TPJ", "Junção Temporo-Parietal", "associativa", "atencao_social"}
        };
        for (String[] a : areas) {
            AreaCortical area = new AreaCortical(a[0], a[1], a[2], a[3]);
            String[] nomesCamadas = {
                    "Molecular (I)", "Externa Granular (II)", "Externa Piramidal (III)",
                    "Interna Granular (IV)", "Interna Piramidal (V)", "Multiforme (VI)"
            };
            for (int i = 1; i <= 6; i++) {
                area.addCamada(new CamadaCortical(i, nomesCamadas[i-1], i == 1 || i == 4 ? "" : "integracao"));
            }
            areasCorticais.put(area.getId(), area);
        }
    }

    private void criarNeuroniosIniciais(int n) {
        Random rnd = new Random();
        for (int i = 0; i < n; i++) {
            TipoNeuronio tipo = rnd.nextDouble() < 0.7 ? TipoNeuronio.PIRAMIDAL :
                    (rnd.nextDouble() < 0.9 ? TipoNeuronio.INTERNEURONIO : TipoNeuronio.GRANULAR);
            double x = rnd.nextDouble() * 100;
            double y = rnd.nextDouble() * 100;
            double z = rnd.nextDouble() * 4;
            NeuronioOrganico nrn = new NeuronioOrganico("neu_" + i, tipo, x, y, z);
            neuronios.put(nrn.getId(), nrn);
        }
    }

    public String criarSinapse(String preId, String posId, Double pesoInicial) {
        NeuronioOrganico pre = neuronios.get(preId);
        NeuronioOrganico pos = neuronios.get(posId);
        if (pre == null || pos == null) return null;

        String sinId = "syn_" + preId + "_" + posId + "_" + sinapses.size();
        double peso = pesoInicial != null ? pesoInicial : ThreadLocalRandom.current().nextDouble(0.01, 0.5);

        double dist = Math.sqrt(Math.pow(pre.getPosicao3d()[0]-pos.getPosicao3d()[0],2) +
                Math.pow(pre.getPosicao3d()[1]-pos.getPosicao3d()[1],2) +
                Math.pow(pre.getPosicao3d()[2]-pos.getPosicao3d()[2],2));
        TipoSinapse tipoSin = dist < 50 ? TipoSinapse.AXODENDRITICA : TipoSinapse.AXOAXONICA;

        String nt = "glutamato";
        switch (pre.getTipo()) {
            case GABAERGICO: nt = "GABA"; break;
            case DOPAMINERGICO: nt = "dopamina"; break;
            case COLINERGICO: nt = "acetilcolina"; break;
        }

        Sinapse sin = new Sinapse(sinId, preId, posId, tipoSin, peso, nt);
        sinapses.put(sinId, sin);
        pre.getAxonio().add(sinId);
        pos.getDendritos().add(sinId);
        totalSinapsesCriadas.incrementAndGet();
        return sinId;
    }

    public List<String> propagarSinal(String neuronioId, double forca) {
        NeuronioOrganico nrn = neuronios.get(neuronioId);
        if (nrn == null || !nrn.disparar()) return Collections.emptyList();
        totalDisparos.incrementAndGet();

        List<String> ativados = new ArrayList<>();
        for (String sinId : nrn.getAxonio()) {
            Sinapse sin = sinapses.get(sinId);
            if (sin == null) continue;
            double forcaTransmitida = sin.ativar(forca);
            NeuronioOrganico posNrn = neuronios.get(sin.getPosId());
            if (posNrn != null && posNrn.receberSinal(forcaTransmitida, sin.getNeurotransmissor())) {
                ativados.add(sin.getPosId());
                // TODO limit recursion
            }
        }
        return ativados;
    }

    public Map<String, Object> processarEntradaSensorial(Map<String, Object> dadosSensoriais) {
        String tipo = (String) dadosSensoriais.getOrDefault("tipo", "generico");
        double intensidade = ((Number) dadosSensoriais.getOrDefault("intensidade", 0.5)).doubleValue();

        Map<String, String> mapeamento = Map.of(
                "visual", "V1", "auditivo", "A1", "tatil", "S1", "motor", "M1", "cognitivo", "PFC"
        );
        String areaId = mapeamento.getOrDefault(tipo, "PFC");
        AreaCortical area = areasCorticais.get(areaId);
        if (area == null) return Map.of("sucesso", false, "erro", "Área não encontrada");

        List<String> neuroniosAtivados = new ArrayList<>();
        CamadaCortical camadaInput = area.getCamadas().size() > 3 ? area.getCamadas().get(3) : area.getCamadas().get(0);
        for (String neuId : camadaInput.getNeuronios()) {
            if (neuronios.containsKey(neuId)) {
                neuroniosAtivados.addAll(propagarSinal(neuId, intensidade));
            }
        }

        Map<String, Double> respostaEmocional = sistemaLimbico.processarEmocao(dadosSensoriais);

        // Atualizar neurotransmissores globais
        for (Map.Entry<String, Neurotransmissor> e : neurotransmissoresGlobais.entrySet()) {
            double estimulo = respostaEmocional.getOrDefault(e.getKey(), 0.0);
            e.getValue().atualizar(estimulo > 0 ? estimulo : 0.01);
        }

        memoriaTrabalho.addLast(Map.of(
                "tipo", tipo, "intensidade", intensidade,
                "timestamp", Instant.now().toString()
        ));
        if (memoriaTrabalho.size() > 7) memoriaTrabalho.pollFirst();

        Map<String, Object> statusNT = new LinkedHashMap<>();
        neurotransmissoresGlobais.forEach((k, v) -> statusNT.put(k, v.getNivel()));

        return Map.of(
                "sucesso", true,
                "area_processada", areaId,
                "neuronios_ativados", neuroniosAtivados.size(),
                "resposta_emocional", respostaEmocional,
                "neurotransmissores", statusNT
        );
    }

    public synchronized void cicloManutencao() {
        // 1. Metabolismo
        for (NeuronioOrganico nrn : neuronios.values()) {
            nrn.atualizarMetabolismo();
        }

        // 2. Pruning sináptico
        List<String> remover = new ArrayList<>();
        for (Sinapse sin : sinapses.values()) {
            if (sin.aplicarPruning(0.05)) remover.add(sin.getId());
        }
        for (String id : remover) {
            sinapses.remove(id);
            totalSinapsesEliminadas.incrementAndGet();
        }

        // 3. Neurogênese
        if (ThreadLocalRandom.current().nextDouble() < 0.001) {
            criarNeuroniosIniciais(1);
            totalNeuroNascidos.incrementAndGet();
        }

        // 4. Apoptose
        List<String> mortos = new ArrayList<>();
        for (NeuronioOrganico nrn : neuronios.values()) {
            if (nrn.getIdade() > 10000 && ThreadLocalRandom.current().nextDouble() < nrn.getTaxaMortalidade()
                    && nrn.getEnergia() < 0.1) {
                mortos.add(nrn.getId());
            }
        }
        for (String id : mortos) {
            NeuronioOrganico nrn = neuronios.remove(id);
            if (nrn != null) {
                // remove syapses
                for (String sinId : new ArrayList<>(nrn.getAxonio())) {
                    sinapses.remove(sinId);
                    totalSinapsesEliminadas.incrementAndGet();
                }
                for (String sinId : new ArrayList<>(nrn.getDendritos())) {
                    sinapses.remove(sinId);
                    totalSinapsesEliminadas.incrementAndGet();
                }
                totalNeuroMortos.incrementAndGet();
            }
        }

        // 5. Ritmo circadiano
        horaSimulada = (horaSimulada + 0.1) % 24;
        if (horaSimulada >= 23 || horaSimulada < 5) ritmoCircadiano = 0.2;
        else if (horaSimulada >= 8 && horaSimulada < 20) ritmoCircadiano = 0.9;
        else ritmoCircadiano = 0.6;

        // 6. Consolidação de memória periódica
        if (totalDisparos.get() % 100 == 0) {
            consolidarMemorias();
        }
    }

    private void consolidarMemorias() {
        while (memoriaTrabalho.size() > 5) {
            Map<String, Object> item = memoriaTrabalho.pollFirst();
            if (item != null) {
                String chave = "mem_" + item.hashCode() % 10000;
                memoriaLongoPrazo.put(chave, new HashMap<>(item));
            }
        }
    }

    // Controle de ciclo autônomo
    public synchronized boolean iniciarCicloAutonomo(long intervaloMs) {
        if (executando) return false;
        executando = true;
        cicloAutonomoFuture = executorService.scheduleAtFixedRate(this::cicloManutencao, 0, intervaloMs, TimeUnit.MILLISECONDS);
        return true;
    }

    public synchronized void pararCicloAutonomo() {
        executando = false;
        if (cicloAutonomoFuture != null) {
            cicloAutonomoFuture.cancel(false);
            cicloAutonomoFuture = null;
        }
    }

    public Map<String, Object> getStatus() {
        int totalNeu = neuronios.size();
        int totalSin = sinapses.size();
        double densidade = totalSin / (double) Math.max(1, totalNeu);
        double atividadeMedia = totalDisparos.get() / (double) Math.max(1, totalDisparos.get()); // simplistic

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("nome", "VHALINOR Cérebro Orgânico");
        status.put("versao", "6.0.0");

        Map<String, Object> populacao = new LinkedHashMap<>();
        populacao.put("neuronios", totalNeu);
        populacao.put("sinapses", totalSin);
        populacao.put("densidade_sinaptica", String.format("%.2f", densidade));
        populacao.put("neuronios_nascidos", totalNeuroNascidos.get());
        populacao.put("neuronios_mortos", totalNeuroMortos.get());
        populacao.put("sinapses_criadas", totalSinapsesCriadas.get());
        populacao.put("sinapses_eliminadas", totalSinapsesEliminadas.get());
        status.put("populacao", populacao);

        Map<String, Object> atividade = new LinkedHashMap<>();
        atividade.put("total_disparos", totalDisparos.get());
        atividade.put("atividade_media", String.format("%.2f", atividadeMedia));
        atividade.put("ritmo_circadiano", String.format("%.2f", ritmoCircadiano));
        atividade.put("hora_simulada", String.format("%.1f", horaSimulada));
        status.put("atividade", atividade);

        Map<String, Object> neuroquimica = new LinkedHashMap<>();
        neurotransmissoresGlobais.forEach((k, v) -> {
            Map<String, Object> ntInfo = new LinkedHashMap<>();
            ntInfo.put("nivel", String.format("%.3f", v.getNivel()));
            ntInfo.put("estado", v.getEstado().toString().toLowerCase());
            neuroquimica.put(k, ntInfo);
        });
        status.put("neuroquimica", neuroquimica);

        Map<String, Object> memoria = new LinkedHashMap<>();
        memoria.put("trabalho", memoriaTrabalho.size());
        memoria.put("curto_prazo", memoriaCurtoPrazo.size());
        memoria.put("longo_prazo", memoriaLongoPrazo.size());
        status.put("memoria", memoria);

        Map<String, Object> homeo = new LinkedHashMap<>();
        homeostase.forEach((k, v) -> homeo.put(k, String.format("%.3f", v)));
        status.put("homeostase", homeo);

        return status;
    }

    // Para teste
    public static void main(String[] args) throws InterruptedException {
        ArquiteturaOrganica cortex = new ArquiteturaOrganica(100);
        cortex.criarSinapse("neu_0", "neu_1", 0.5);
        System.out.println("Status inicial: " + cortex.getStatus());
        cortex.iniciarCicloAutonomo(1000);
        Thread.sleep(5000);
        cortex.pararCicloAutonomo();
        System.out.println("Após 5s: " + cortex.getStatus());
    }
}
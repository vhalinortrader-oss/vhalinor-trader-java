import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * VHALINOR Neurogênese e Comunicação Neural v6.0 – Java Edition
 *
 * Sistema avançado de criação de neurônios e comunicação para IA geral.
 * Simula neurogênese, sinais químicos/elétricos, assemblies neurais e trofismo.
 *
 * Melhorias:
 * <ul>
 *   <li>Coleções thread‑safe para ambientes concorrentes</li>
 *   <li>Uso de {@code Optional} e {@code EnumMap} para melhor desempenho</li>
 *   <li>Fábricas estáticas para criação de instâncias</li>
 *   <li>Streams para cálculos estatísticos</li>
 *   <li>Separação de responsabilidades em classes internas</li>
 * </ul>
 */
public class NeurogeneseComunicacao {

    // ======================== Enums ========================
    public enum FaseNeurogenese {
        PROLIFERACAO, MIGRACAO, DIFERENCIACAO, MATURACAO, INTEGRACAO, APOPTOSE
    }

    public enum TipoSinalNeural {
        POTENCIAL_ACAO, SINAPSE_QUIMICA, GAP_JUNCTION,
        VOLUME_TRANSMISSION, RETROGRADO, AUTOCRINO
    }

    public enum FatorCrescimento {
        BDNF, NGF, GDNF, NT3, NT4, IGF1
    }

    // ======================== Data Classes ========================
    public static class Neuroblasto {
        private final String id;
        private FaseNeurogenese fase;
        private double[] posicao3d; // x, y, z
        private double[] posicaoDestino;
        private String tipoPrevisto;
        private Map<FatorCrescimento, Double> fatoresCrescimento;
        private Instant timestampNascimento;
        private Instant timestampMaturacaoEsperada;
        private double velocidadeMigracao = 0.1; // mm/hora
        private double taxaDiferenciacao = 0.01;

        public Neuroblasto(String id, FaseNeurogenese fase, double[] posicao, double[] destino,
                           String tipoPrevisto, Map<FatorCrescimento, Double> fatores) {
            this.id = id;
            this.fase = fase;
            this.posicao3d = posicao.clone();
            this.posicaoDestino = destino.clone();
            this.tipoPrevisto = tipoPrevisto;
            this.fatoresCrescimento = new EnumMap<>(fatores);
            this.timestampNascimento = Instant.now();
        }

        public boolean avancarFase() {
            FaseNeurogenese[] fases = FaseNeurogenese.values();
            int idx = Arrays.asList(fases).indexOf(fase);
            if (idx < fases.length - 2) { // não avança para APOPTOSE automaticamente
                fase = fases[idx + 1];
                if (fase == FaseNeurogenese.MATURACAO) {
                    timestampMaturacaoEsperada = Instant.now().plus(Duration.ofHours(48));
                }
                return true;
            }
            return false;
        }

        public double migrar() {
            if (fase != FaseNeurogenese.MIGRACAO) return 0.0;
            double dx = posicaoDestino[0] - posicao3d[0];
            double dy = posicaoDestino[1] - posicao3d[1];
            double dz = posicaoDestino[2] - posicao3d[2];
            double distancia = Math.sqrt(dx*dx + dy*dy + dz*dz);
            if (distancia < 0.1) {
                avancarFase();
                return distancia;
            }
            double fator = velocidadeMigracao / distancia;
            double ruido = 0.1;
            Random rnd = ThreadLocalRandom.current();
            posicao3d[0] += dx * fator + rnd.nextDouble(-ruido, ruido);
            posicao3d[1] += dy * fator + rnd.nextDouble(-ruido, ruido);
            posicao3d[2] += dz * fator + rnd.nextDouble(-ruido, ruido);
            return Math.sqrt(
                Math.pow(posicaoDestino[0]-posicao3d[0],2) +
                Math.pow(posicaoDestino[1]-posicao3d[1],2) +
                Math.pow(posicaoDestino[2]-posicao3d[2],2)
            );
        }

        public boolean isMaduro() { return fase == FaseNeurogenese.INTEGRACAO; }

        // getters
        public String getId() { return id; }
        public FaseNeurogenese getFase() { return fase; }
        public double[] getPosicao3d() { return posicao3d; }
        public double[] getPosicaoDestino() { return posicaoDestino; }
        public String getTipoPrevisto() { return tipoPrevisto; }
        public Map<FatorCrescimento, Double> getFatoresCrescimento() { return fatoresCrescimento; }
        public Instant getTimestampNascimento() { return timestampNascimento; }
        public Instant getTimestampMaturacaoEsperada() { return timestampMaturacaoEsperada; }
    }

    public static class SinalNeural {
        private final String id;
        private final String origemId;
        private final String destinoId;
        private final TipoSinalNeural tipo;
        private final double amplitude;
        private final double duracaoMs;
        private final List<String> neurotransmissores;
        private double concentracaoVesiculas;
        private Instant timestampEmissao;
        private Instant timestampRecebimento;
        private Double latenciaMs;
        private double modulacaoPresinaptica = 1.0;
        private double modulacaoPostsinaptica = 1.0;

        public SinalNeural(String id, String origem, String destino, TipoSinalNeural tipo,
                           double amplitude, double duracaoMs, List<String> neurotransmissores) {
            this.id = id;
            this.origemId = origem;
            this.destinoId = destino;
            this.tipo = tipo;
            this.amplitude = amplitude;
            this.duracaoMs = duracaoMs;
            this.neurotransmissores = neurotransmissores;
            this.concentracaoVesiculas = 0.5;
            this.timestampEmissao = Instant.now();
        }

        public double calcularEficacia() {
            return amplitude * modulacaoPresinaptica * modulacaoPostsinaptica *
                   (concentracaoVesiculas / 0.5);
        }

        // getters/setters
        public String getId() { return id; }
        public String getOrigemId() { return origemId; }
        public String getDestinoId() { return destinoId; }
        public double getAmplitude() { return amplitude; }
        public void setTimestampRecebimento(Instant ts) { this.timestampRecebimento = ts; }
        public void setLatenciaMs(double lat) { this.latenciaMs = lat; }
        // more...
    }

    public static class GapJunction {
        private final String id;
        private final String neuronioAId;
        private final String neuronioBId;
        private double condutancia; // S
        private double resistencia; // Ohms
        private double sincronizacaoFase;

        public GapJunction(String id, String a, String b, double condutancia) {
            this.id = id;
            this.neuronioAId = a;
            this.neuronioBId = b;
            this.condutancia = condutancia;
            this.resistencia = 1.0 / condutancia;
            this.sincronizacaoFase = 0.0;
        }

        public double transmitirCorrente(double potA, double potB) {
            return condutancia * (potA - potB);
        }

        public void sincronizar(double faseA, double faseB) {
            double diff = Math.abs(faseA - faseB);
            sincronizacaoFase = 1.0 - (diff / (2 * Math.PI));
        }

        public double getSincronizacaoFase() { return sincronizacaoFase; }
        // getters...
    }

    public static class AssemblyNeural {
        private final String id;
        private final String nome;
        private final Set<String> neuroniosIds;
        private List<Double> padraoAtivacao = new ArrayList<>();
        private double forcaCohesao;
        private Instant timestampFormacao;
        private int vezesAtivado = 0;

        public AssemblyNeural(String id, String nome, Set<String> ids, double forcaCohesao) {
            this.id = id;
            this.nome = nome;
            this.neuroniosIds = new HashSet<>(ids);
            this.forcaCohesao = forcaCohesao;
            this.timestampFormacao = Instant.now();
        }

        public void ativar(double intensidade) {
            vezesAtivado++;
            padraoAtivacao.add(intensidade);
            if (padraoAtivacao.size() > 100) padraoAtivacao.remove(0);
        }

        public boolean isConsolidado() { return vezesAtivado > 10 && forcaCohesao > 0.7; }

        // getters...
        public String getId() { return id; }
        public String getNome() { return nome; }
        public Set<String> getNeuroniosIds() { return neuroniosIds; }
        public int getVezesAtivado() { return vezesAtivado; }
        public double getForcaCohesao() { return forcaCohesao; }
    }

    public static class TrofismoNeural {
        private final String neuronioId;
        private Map<FatorCrescimento, Double> fatores;
        private double glicose = 1.0;
        private double oxigenio = 1.0;
        private double aminoacidos = 1.0;
        private double lipideos = 1.0;
        private double atpProducao = 1.0;
        private double atpConsumo = 0.5;
        private double stressOxidativo = 0.0;

        public TrofismoNeural(String neuronioId) {
            this.neuronioId = neuronioId;
            this.fatores = new EnumMap<>(FatorCrescimento.class);
        }

        public double calcularSaude() {
            double nutrientes = (glicose + oxigenio + aminoacidos + lipideos) / 4;
            double energia = atpProducao / Math.max(0.1, atpConsumo);
            double fatorTotal = fatores.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            return Math.max(0.0, Math.min(1.0, nutrientes * 0.3 + energia * 0.4 + fatorTotal * 0.3 - stressOxidativo));
        }

        // getters/setters...
        public void setGlicose(double v) { glicose = Math.max(0, Math.min(1, v)); }
        public void setOxigenio(double v) { oxigenio = Math.max(0, Math.min(1, v)); }
        public void setAminoacidos(double v) { aminoacidos = Math.max(0, Math.min(1, v)); }
        public void setLipideos(double v) { lipideos = Math.max(0, Math.min(1, v)); }
        public void setAtpProducao(double v) { atpProducao = v; }
        public void setAtpConsumo(double v) { atpConsumo = v; }
        public void setStressOxidativo(double v) { stressOxidativo = v; }
        public void setFator(FatorCrescimento f, double valor) { fatores.put(f, valor); }
    }

    // ======================== Classe Principal ========================
    private final String nome = "VHALINOR Neurogênese e Comunicação";
    private final String versao = "6.0.0";
    private final double[] ambiente3d; // (x,y,z) dim
    private final Map<String, Neuroblasto> neuroblastos = new ConcurrentHashMap<>();
    private final Deque<String> filaNeurogenese = new ConcurrentLinkedDeque<>();
    private final List<double[]> zonasProliferativas = new ArrayList<>();
    private final Map<String, SinalNeural> sinaisAtivos = new ConcurrentHashMap<>();
    private final Deque<SinalNeural> historicoSinais = new ConcurrentLinkedDeque<>();
    private final Map<String, GapJunction> gapJunctions = new ConcurrentHashMap<>();
    private final Map<String, AssemblyNeural> assemblies = new ConcurrentHashMap<>();
    private final Map<String, TrofismoNeural> estadoTrofico = new ConcurrentHashMap<>();

    // Configurações
    private volatile double taxaBaselineNeurogenese = 0.001;
    private volatile double fatorAtividadeDependente = 2.0;
    private final long limitePopulacional = 100000;

    // Métricas
    private long totalNeuroblastosCriados = 0;
    private long totalNeuroblastosMaduros = 0;
    private long totalNeuroblastosApop = 0;
    private long totalSinaisTransmitidos = 0;
    private long totalGapJunctions = 0;
    private long totalAssemblies = 0;
    private long cicloAtual = 0;

    // Callbacks (opcional)
    private final List<Consumer<Neuroblasto>> onNovoNeuronio = new CopyOnWriteArrayList<>();
    private final List<Consumer<SinalNeural>> onSinalTransmitido = new CopyOnWriteArrayList<>();
    private final List<Consumer<AssemblyNeural>> onAssemblyFormado = new CopyOnWriteArrayList<>();

    public NeurogeneseComunicacao(double[] ambiente) {
        this.ambiente3d = ambiente.clone();
        inicializarZonasProliferativas();
    }

    public NeurogeneseComunicacao() {
        this(new double[]{100, 100, 10});
    }

    private void inicializarZonasProliferativas() {
        zonasProliferativas.add(new double[]{20, 20, 2});
        zonasProliferativas.add(new double[]{80, 80, 2});
        zonasProliferativas.add(new double[]{50, 10, 5});
    }

    // ======================== Neurogênese ========================
    public List<String> iniciarNeurogenese(int quantidade, String tipoPrevisto, double estimuloAtividade) {
        Random rnd = ThreadLocalRandom.current();
        double taxaEfetiva = taxaBaselineNeurogenese * (1 + estimuloAtividade * fatorAtividadeDependente);
        List<String> idsCriados = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            if (rnd.nextDouble() > taxaEfetiva) continue;

            double[] zona = zonasProliferativas.get(rnd.nextInt(zonasProliferativas.size()));
            double[] posicao = new double[]{
                    zona[0] + rnd.nextDouble(-5, 5),
                    zona[1] + rnd.nextDouble(-5, 5),
                    zona[2] + rnd.nextDouble(-1, 1)
            };
            double[] destino = new double[]{
                    rnd.nextDouble(ambiente3d[0]),
                    rnd.nextDouble(ambiente3d[1]),
                    rnd.nextDouble(ambiente3d[2])
            };
            Map<FatorCrescimento, Double> fatores = new EnumMap<>(FatorCrescimento.class);
            fatores.put(FatorCrescimento.BDNF, rnd.nextDouble(0.5, 1.0));
            fatores.put(FatorCrescimento.NGF, rnd.nextDouble(0.3, 0.8));

            Neuroblasto nb = new Neuroblasto(
                    "nb_" + String.format("%06d", totalNeuroblastosCriados),
                    FaseNeurogenese.PROLIFERACAO,
                    posicao, destino, tipoPrevisto, fatores
            );
            neuroblastos.put(nb.getId(), nb);
            filaNeurogenese.add(nb.getId());
            idsCriados.add(nb.getId());
            totalNeuroblastosCriados++;

            for (Consumer<Neuroblasto> cb : onNovoNeuronio) cb.accept(nb);
        }
        return idsCriados;
    }

    public Map<String, Integer> processarNeurogenese(int ciclos) {
        int maduros = 0, emDesenv = 0, apoptose = 0;
        for (int c = 0; c < ciclos; c++) {
            for (Iterator<Neuroblasto> it = neuroblastos.values().iterator(); it.hasNext(); ) {
                Neuroblasto nb = it.next();
                try {
                    switch (nb.getFase()) {
                        case MIGRACAO:
                            nb.migrar();
                            emDesenv++;
                            break;
                        case DIFERENCIACAO:
                            double saude = nb.getFatoresCrescimento().values().stream()
                                    .mapToDouble(Double::doubleValue).average().orElse(0);
                            if (ThreadLocalRandom.current().nextDouble() < saude * 0.01) nb.avancarFase();
                            emDesenv++;
                            break;
                        case MATURACAO:
                            if (nb.getTimestampMaturacaoEsperada() != null &&
                                    Instant.now().isAfter(nb.getTimestampMaturacaoEsperada())) {
                                nb.avancarFase();
                                maduros++;
                                totalNeuroblastosMaduros++;
                            } else {
                                emDesenv++;
                            }
                            break;
                        case APOPTOSE:
                            it.remove();
                            apoptose++;
                            totalNeuroblastosApop++;
                            break;
                        default:
                            if (nb.isMaduro()) maduros++;
                            break;
                    }
                } catch (Exception e) {
                    // log
                }
            }
        }
        Map<String, Integer> res = new LinkedHashMap<>();
        res.put("maduros", maduros);
        res.put("em_desenvolvimento", emDesenv);
        res.put("apoptose", apoptose);
        return res;
    }

    public Optional<Map<String, Object>> converterNeuroblasto(String nbId) {
        Neuroblasto nb = neuroblastos.get(nbId);
        if (nb == null || !nb.isMaduro()) return Optional.empty();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("tipo", nb.getTipoPrevisto());
        data.put("posicao_3d", nb.getPosicao3d());
        data.put("origem", "neurogenese");
        Map<String, Double> fatores = new LinkedHashMap<>();
        nb.getFatoresCrescimento().forEach((k, v) -> fatores.put(k.name(), v));
        data.put("fatores_crescimento", fatores);
        return Optional.of(data);
    }

    // ======================== Comunicação ========================
    public String transmitirSinal(String origemId, String destinoId, TipoSinalNeural tipo,
                                 double amplitude, List<String> neurotransmissores) {
        String sinalId = "sig_" + origemId + "_" + destinoId + "_" + System.currentTimeMillis();
        SinalNeural sinal = new SinalNeural(sinalId, origemId, destinoId, tipo, amplitude, 1.0,
                neurotransmissores != null ? neurotransmissores : List.of("glutamato"));
        double latencia = ThreadLocalRandom.current().nextDouble(0.5, 2.0);
        sinal.setLatenciaMs(latencia);
        // Simular latência (não dormimos na thread principal para demo)
        sinal.setTimestampRecebimento(Instant.now().plusMillis((long) latencia));
        sinaisAtivos.put(sinalId, sinal);
        historicoSinais.add(sinal);
        totalSinaisTransmitidos++;
        for (Consumer<SinalNeural> cb : onSinalTransmitido) cb.accept(sinal);
        return sinalId;
    }

    public String criarGapJunction(String neuronioA, String neuronioB, Double condutancia) {
        String gjId = "gj_" + neuronioA + "_" + neuronioB;
        double cond = condutancia != null ? condutancia : ThreadLocalRandom.current().nextDouble(0.1, 1.0);
        GapJunction gj = new GapJunction(gjId, neuronioA, neuronioB, cond);
        gapJunctions.put(gjId, gj);
        totalGapJunctions++;
        return gjId;
    }

    public Optional<String> detectarAssembly(List<String> neuroniosIds, double atividadeCorrelacionada) {
        if (neuroniosIds.size() < 3 || atividadeCorrelacionada < 0.6) return Optional.empty();
        String asmId = "asm_" + Math.abs(neuroniosIds.hashCode()) % 100000;
        AssemblyNeural assembly = assemblies.get(asmId);
        if (assembly != null) {
            assembly.ativar(atividadeCorrelacionada);
            return Optional.of(asmId);
        }
        assembly = new AssemblyNeural(asmId, "Assembly_" + assemblies.size(),
                new HashSet<>(neuroniosIds), atividadeCorrelacionada);
        assemblies.put(asmId, assembly);
        totalAssemblies++;
        for (Consumer<AssemblyNeural> cb : onAssemblyFormado) cb.accept(assembly);
        return Optional.of(asmId);
    }

    public void atualizarTrofismo(String neuronioId, Map<String, Double> condicoes) {
        TrofismoNeural t = estadoTrofico.computeIfAbsent(neuronioId, k -> new TrofismoNeural(k));
        if (condicoes.containsKey("glicose")) t.setGlicose(condicoes.get("glicose"));
        if (condicoes.containsKey("oxigenio")) t.setOxigenio(condicoes.get("oxigenio"));
        if (condicoes.containsKey("aminoacidos")) t.setAminoacidos(condicoes.get("aminoacidos"));
        if (condicoes.containsKey("lipideos")) t.setLipideos(condicoes.get("lipideos"));
        if (condicoes.containsKey("atp_producao")) t.setAtpProducao(condicoes.get("atp_producao"));
        if (condicoes.containsKey("atp_consumo")) t.setAtpConsumo(condicoes.get("atp_consumo"));
        if (condicoes.containsKey("stress_oxidativo")) t.setStressOxidativo(condicoes.get("stress_oxidativo"));
        // fatores específicos podem ser passados como prefixo "FATOR_BDNF" etc.
    }

    // ======================== Status / Métricas ========================
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("nome", nome);
        status.put("versao", versao);

        Map<String, Object> neuro = new LinkedHashMap<>();
        neuro.put("total_neuroblastos", neuroblastos.size());
        // fase distribution
        Map<String, Long> faseCount = neuroblastos.values().stream()
                .collect(Collectors.groupingBy(nb -> nb.getFase().name(), Collectors.counting()));
        neuro.put("por_fase", faseCount);
        neuro.put("maduros", neuroblastos.values().stream().filter(Neuroblasto::isMaduro).count());
        neuro.put("total_criados", totalNeuroblastosCriados);
        neuro.put("total_maduros_hist", totalNeuroblastosMaduros);
        neuro.put("total_apoptose", totalNeuroblastosApop);
        status.put("neurogenese", neuro);

        Map<String, Object> com = new LinkedHashMap<>();
        com.put("sinais_totais", totalSinaisTransmitidos);
        com.put("sinais_ativos", sinaisAtivos.size());
        com.put("gap_junctions", gapJunctions.size());
        com.put("assemblies", assemblies.size());
        com.put("assemblies_consolidados", assemblies.values().stream().filter(AssemblyNeural::isConsolidado).count());
        status.put("comunicacao", com);

        Map<String, Double> sinc = new LinkedHashMap<>();
        double media = gapJunctions.values().stream().mapToDouble(GapJunction::getSincronizacaoFase).average().orElse(0);
        double max = gapJunctions.values().stream().mapToDouble(GapJunction::getSincronizacaoFase).max().orElse(0);
        sinc.put("media", media);
        sinc.put("maxima", max);
        status.put("sincronizacao", sinc);

        double saudeMedia = estadoTrofico.values().stream().mapToDouble(TrofismoNeural::calcularSaude).average().orElse(0);
        status.put("saude_trofica_media", saudeMedia);
        status.put("ciclo_atual", cicloAtual);
        return status;
    }

    // Callbacks setters
    public void onNovoNeuronio(Consumer<Neuroblasto> cb) { onNovoNeuronio.add(cb); }
    public void onSinalTransmitido(Consumer<SinalNeural> cb) { onSinalTransmitido.add(cb); }
    public void onAssemblyFormado(Consumer<AssemblyNeural> cb) { onAssemblyFormado.add(cb); }

    // ======================== Exemplo de uso ========================
    public static void main(String[] args) {
        NeurogeneseComunicacao sys = new NeurogeneseComunicacao();
        sys.iniciarNeurogenese(10, "piramidal", 0.5);
        sys.processarNeurogenese(5);
        System.out.println("Status após 5 ciclos: " + sys.getStatus());
    }
}
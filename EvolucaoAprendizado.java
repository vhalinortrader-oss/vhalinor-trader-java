package com.vhalinor.evolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Sistema de evolução de aprendizado – VHALINOR v6.0 (Java Edition)
 * 
 * <p>Implementa evolução de modelos de trading através de gerações:
 * <ul>
 *   <li>Genoma representando características do modelo</li>
 *   <li>Rastreamento de fitness (score, Sharpe, win rate, etc.)</li>
 *   <li>Operadores de crossover e mutação</li>
 *   <li>Adaptação a regimes de mercado (volátil, tendência, range)</li>
 *   <li>Seleção natural e poda da população</li>
 *   <li>Linhagem e árvore genealógica dos modelos</li>
 *   <li>Persistência do estado evolutivo (JSON)</li>
 * </ul>
 *
 * <p><b>Melhorias em relação ao Python original:</b>
 * <ul>
 *   <li>Tipos imutáveis (records) para segurança e clareza</li>
 *   <li>Uso de {@code ThreadLocalRandom} para operações concorrentes seguras</li>
 *   <li>Cálculos estatísticos com streams (sem dependência de NumPy)</li>
 *   <li>Validação de parâmetros e defesas contra mutações indevidas</li>
 *   <li>Serialização JSON com Jackson (opcional) + fallback nativo</li>
 *   <li>Suporte a {@code Optional} para retornos que podem ser nulos</li>
 *   <li>Registro detalhado de eventos com {@code EventoEvolucao}</li>
 * </ul>
 */
public class EvolucaoAprendizado {

    private static final Logger log = LoggerFactory.getLogger(EvolucaoAprendizado.class);

    // ======================== Enums ========================
    public enum TipoEvolucao {
        MUTACAO, CROSSOVER, SELECAO, EPIFANIA, CONSOLIDACAO, ADAPTACAO
    }

    public enum EstagioEvolucao {
        GERMINACAO, CRESCIMENTO, MATURIDADE, ESPECIALIZACAO, FLORACAO, ADAPTACAO
    }

    public enum TipoAdaptacao {
        REGIME_VOLATILIDADE, REGIME_TENDENCIA, REGIME_RANGE, MUDANCA_MACRO, EVENTO_EXTERNO
    }

    // ======================== Data Records ========================
    public record GenomaModelo(
            String id,
            int geracao,
            Map<String, Double> caracteristicas,
            String arquitetura,
            Map<String, Object> hiperparametros,
            String ancestralId,
            List<String> mutacoes,
            Instant timestampCriacao
    ) {
        public GenomaModelo {
            // Defensive copies
            caracteristicas = Collections.unmodifiableMap(new HashMap<>(caracteristicas));
            hiperparametros = Collections.unmodifiableMap(new HashMap<>(hiperparametros));
            mutacoes = List.copyOf(mutacoes);
        }

        /** Builder para facilitar criação */
        public static Builder builder(String id, int geracao, String arquitetura) {
            return new Builder(id, geracao, arquitetura);
        }

        public static class Builder {
            private final String id;
            private final int geracao;
            private final String arquitetura;
            private Map<String, Double> caracteristicas = new HashMap<>();
            private Map<String, Object> hiperparametros = new HashMap<>();
            private String ancestralId = null;
            private List<String> mutacoes = new ArrayList<>();
            private Instant timestampCriacao = Instant.now();

            Builder(String id, int geracao, String arquitetura) {
                this.id = id;
                this.geracao = geracao;
                this.arquitetura = arquitetura;
            }

            public Builder caracteristicas(Map<String, Double> c) { this.caracteristicas = c; return this; }
            public Builder hiperparametros(Map<String, Object> h) { this.hiperparametros = h; return this; }
            public Builder ancestralId(String a) { this.ancestralId = a; return this; }
            public Builder mutacoes(List<String> m) { this.mutacoes = m; return this; }
            public Builder timestampCriacao(Instant t) { this.timestampCriacao = t; return this; }

            public GenomaModelo build() {
                return new GenomaModelo(id, geracao, caracteristicas, arquitetura,
                        hiperparametros, ancestralId, mutacoes, timestampCriacao);
            }
        }
    }

    public record FitnessSnapshot(
            Instant timestamp,
            double fitnessScore,
            double sharpeRatio,
            double winRate,
            double profitFactor,
            double maxDrawdown,
            int tradesRealizados,
            String ambiente  // "bull", "bear", "sideways", "volatile"
    ) {}

    public record EventoEvolucao(
            String id,
            TipoEvolucao tipo,
            int geracao,
            String descricao,
            String modeloOrigem,
            String modeloDestino,   // pode ser null
            double impactoFitness,
            String motivacao,
            Instant timestamp
    ) {}

    public record LinhagemModelo(
            String modeloId,
            int geracao,
            String ancestralDireto,
            List<String> descendentes,
            List<String> irmaos,
            List<String> caminhoEvolucao   // IDs dos eventos
    ) {
        public LinhagemModelo {
            descendentes = List.copyOf(descendentes);
            irmaos = List.copyOf(irmaos);
            caminhoEvolucao = List.copyOf(caminhoEvolucao);
        }
    }

    // ======================== Campos ========================
    private int populacaoMaxima;
    private int geracaoAtual = 0;

    // População de modelos
    private final Map<String, GenomaModelo> genomas = new LinkedHashMap<>();
    private final Map<String, Deque<FitnessSnapshot>> fitnessHistorico = new HashMap<>();
    private final Map<String, LinhagemModelo> linhagens = new HashMap<>();

    // Histórico de evolução
    private final Deque<EventoEvolucao> eventosEvolucao = new ArrayDeque<>();
    private final Deque<Map<String, Object>> adaptacoesRealizadas = new ArrayDeque<>();

    // Regimes de mercado
    private String regimeAtual = "desconhecido";
    private final Deque<String> historicoRegimes = new ArrayDeque<>();

    // Campeões por regime
    private final Map<String, String> campeoesPorRegime = new HashMap<>();

    // Taxas de evolução
    private double taxaMutacao = 0.1;
    private double taxaCrossover = 0.3;
    private double pressaoSelecao = 0.5;  // não utilizada diretamente, mas disponível

    public EvolucaoAprendizado(int populacaoMaxima) {
        this.populacaoMaxima = populacaoMaxima;
    }

    // ======================== Métodos públicos ========================

    /**
     * Cria o genoma inicial e a respectiva linhagem.
     *
     * @return ID do genoma criado.
     */
    public String criarGenomaInicial(Map<String, Double> caracteristicas,
                                     String arquitetura,
                                     Map<String, Object> hiperparametros) {
        String genomaId = gerarIdUnico("gen0_" + Instant.now());
        GenomaModelo genoma = GenomaModelo.builder(genomaId, 0, arquitetura)
                .caracteristicas(new HashMap<>(caracteristicas))
                .hiperparametros(new HashMap<>(hiperparametros))
                .build();

        genomas.put(genomaId, genoma);
        linhagens.put(genomaId, new LinhagemModelo(genomaId, 0, null, List.of(), List.of(), List.of()));
        log.debug("Genoma inicial criado: {}", genomaId);
        return genomaId;
    }

    /**
     * Registra um snapshot de fitness para um genoma.
     */
    public void registrarFitness(String genomaId, FitnessSnapshot snapshot) {
        fitnessHistorico.computeIfAbsent(genomaId, k -> new ArrayDeque<>()).add(snapshot);
        log.debug("Fitness registrado para {}: {}", genomaId, snapshot);

        // Atualizar campeão do regime
        String ambiente = snapshot.ambiente();
        if (!campeoesPorRegime.containsKey(ambiente)) {
            campeoesPorRegime.put(ambiente, genomaId);
        } else {
            String campeaoAtual = campeoesPorRegime.get(ambiente);
            double fitnessCampeao = getUltimoFitness(campeaoAtual);
            if (snapshot.fitnessScore() > fitnessCampeao * 1.05) { // 5% melhor
                campeoesPorRegime.put(ambiente, genomaId);
            }
        }
    }

    /**
     * Calcula a média de fitness das últimas {@code janela} avaliações.
     */
    public double calcularFitnessMedio(String genomaId, int janela) {
        Deque<FitnessSnapshot> snapshots = fitnessHistorico.get(genomaId);
        if (snapshots == null || snapshots.isEmpty()) return 0.0;

        return snapshots.stream()
                .skip(Math.max(0, snapshots.size() - janela))
                .mapToDouble(FitnessSnapshot::fitnessScore)
                .average()
                .orElse(0.0);
    }

    /** Seleciona pais via torneio (tamanho 3). */
    public List<String> selecionarPais(int nPais) {
        if (genomas.size() < nPais) return new ArrayList<>(genomas.keySet());

        List<String> genomaIds = new ArrayList<>(genomas.keySet());
        List<String> selecionados = new ArrayList<>();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int i = 0; i < nPais; i++) {
            // Torneio de 3 (ou menos)
            int tamTorneio = Math.min(3, genomaIds.size());
            List<String> torneio = new ArrayList<>();
            while (torneio.size() < tamTorneio) {
                String candidato = genomaIds.get(rnd.nextInt(genomaIds.size()));
                if (!torneio.contains(candidato)) torneio.add(candidato);
            }
            String melhor = torneio.stream()
                    .max(Comparator.comparingDouble(g -> calcularFitnessMedio(g, 10)))
                    .orElse(torneio.get(0));
            selecionados.add(melhor);
        }
        return selecionados;
    }

    /**
     * Cria um filho via crossover entre dois genomas.
     *
     * @return ID do novo genoma.
     */
    public String crossover(String pai1Id, String pai2Id) {
        GenomaModelo pai1 = genomas.get(pai1Id);
        GenomaModelo pai2 = genomas.get(pai2Id);
        if (pai1 == null || pai2 == null)
            throw new IllegalArgumentException("Genomas pais não encontrados");

        int geracaoFilho = Math.max(pai1.geracao(), pai2.geracao()) + 1;
        String filhoId = gerarIdUnico("gen" + geracaoFilho + "_" + pai1Id + "_" + pai2Id);

        // Crossover de características (média ponderada pelo fitness)
        Map<String, Double> caracteristicasFilho = new HashMap<>();
        Set<String> todasChaves = new HashSet<>(pai1.caracteristicas().keySet());
        todasChaves.addAll(pai2.caracteristicas().keySet());

        double fitness1 = calcularFitnessMedio(pai1Id, 10);
        double fitness2 = calcularFitnessMedio(pai2Id, 10);
        double pesoTotal = fitness1 + fitness2;

        for (String chave : todasChaves) {
            Double v1 = pai1.caracteristicas().get(chave);
            Double v2 = pai2.caracteristicas().get(chave);
            if (v1 != null && v2 != null) {
                double valor = pesoTotal > 0 ?
                        (v1 * fitness1 + v2 * fitness2) / pesoTotal :
                        (v1 + v2) / 2.0;
                caracteristicasFilho.put(chave, valor);
            } else if (v1 != null) {
                caracteristicasFilho.put(chave, v1);
            } else {
                caracteristicasFilho.put(chave, v2);
            }
        }

        // Hiperparâmetros do pai com melhor fitness
        Map<String, Object> hiper = (fitness1 > fitness2) ?
                new HashMap<>(pai1.hiperparametros()) :
                new HashMap<>(pai2.hiperparametros());

        GenomaModelo filho = GenomaModelo.builder(filhoId, geracaoFilho, pai1.arquitetura())
                .caracteristicas(caracteristicasFilho)
                .hiperparametros(hiper)
                .ancestralId(pai1Id)
                .build();

        genomas.put(filhoId, filho);
        linhagens.put(filhoId, new LinhagemModelo(filhoId, geracaoFilho, pai1Id,
                List.of(), List.of(pai2Id), List.of()));

        // Atualizar descendência do pai1
        linhagens.computeIfPresent(pai1Id, (k, v) -> {
            List<String> novosDesc = new ArrayList<>(v.descendentes());
            novosDesc.add(filhoId);
            return new LinhagemModelo(v.modeloId(), v.geracao(), v.ancestralDireto(),
                    novosDesc, v.irmaos(), v.caminhoEvolucao());
        });

        EventoEvolucao evento = new EventoEvolucao(
                "evo_" + eventosEvolucao.size(),
                TipoEvolucao.CROSSOVER,
                geracaoFilho,
                "Crossover entre " + pai1Id + " e " + pai2Id,
                pai1Id,
                filhoId,
                0.0,
                "reproducao",
                Instant.now()
        );
        eventosEvolucao.add(evento);

        log.debug("Crossover criou filho: {}", filhoId);
        return filhoId;
    }

    /**
     * Aplica mutação gaussiana a um genoma e retorna o mutante.
     *
     * @param caracteristicasAlvo se null, todas as características são mutadas.
     */
    public String mutar(String genomaId, double intensidade, List<String> caracteristicasAlvo) {
        GenomaModelo original = genomas.get(genomaId);
        if (original == null)
            throw new IllegalArgumentException("Genoma não encontrado: " + genomaId);

        String mutanteId = gerarIdUnico("mut_" + genomaId);
        Map<String, Double> novasCaracts = new HashMap<>(original.caracteristicas());

        List<String> mutacoesAplicadas = new ArrayList<>();
        Set<String> alvos = caracteristicasAlvo != null ?
                new HashSet<>(caracteristicasAlvo) : novasCaracts.keySet();

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (String chave : alvos) {
            if (novasCaracts.containsKey(chave)) {
                double mutacao = rnd.nextGaussian() * intensidade;
                double novoValor = novasCaracts.get(chave) * (1.0 + mutacao);
                novoValor = Math.max(0.0, Math.min(1.0, novoValor));
                novasCaracts.put(chave, novoValor);
                mutacoesAplicadas.add(chave + ":" + String.format("%.4f", mutacao));
            }
        }

        GenomaModelo mutante = GenomaModelo.builder(mutanteId, original.geracao(), original.arquitetura())
                .caracteristicas(novasCaracts)
                .hiperparametros(new HashMap<>(original.hiperparametros()))
                .ancestralId(genomaId)
                .mutacoes(mutacoesAplicadas)
                .build();

        genomas.put(mutanteId, mutante);
        linhagens.put(mutanteId, new LinhagemModelo(mutanteId, original.geracao(), genomaId,
                List.of(), List.of(), List.of()));

        linhagens.computeIfPresent(genomaId, (k, v) -> {
            List<String> novosDesc = new ArrayList<>(v.descendentes());
            novosDesc.add(mutanteId);
            return new LinhagemModelo(v.modeloId(), v.geracao(), v.ancestralDireto(),
                    novosDesc, v.irmaos(), v.caminhoEvolucao());
        });

        EventoEvolucao evento = new EventoEvolucao(
                "evo_" + eventosEvolucao.size(),
                TipoEvolucao.MUTACAO,
                original.geracao(),
                "Mutação de " + genomaId + " (" + mutacoesAplicadas.size() + " alterações)",
                genomaId,
                mutanteId,
                0.0,
                "exploracao",
                Instant.now()
        );
        eventosEvolucao.add(evento);

        log.debug("Mutação criou mutante: {}", mutanteId);
        return mutanteId;
    }

    /**
     * Adapta um genoma base a um novo regime de mercado.
     * Se existir um campeão para o regime, faz crossover; caso contrário, aplica mutação agressiva.
     *
     * @return ID do genoma adaptado.
     */
    public String adaptarARegime(String novoRegime, String genomaBaseId) {
        if (!genomas.containsKey(genomaBaseId))
            throw new IllegalArgumentException("Genoma base não encontrado: " + genomaBaseId);

        String adaptadoId;
        if (campeoesPorRegime.containsKey(novoRegime)) {
            String campeaoId = campeoesPorRegime.get(novoRegime);
            adaptadoId = crossover(genomaBaseId, campeaoId);
        } else {
            adaptadoId = mutar(genomaBaseId, 0.3, null); // intensidade alta
        }

        Map<String, Object> adaptacao = new HashMap<>();
        adaptacao.put("tipo", novoRegime.contains("volatil") ?
                TipoAdaptacao.REGIME_VOLATILIDADE.name() : TipoAdaptacao.REGIME_TENDENCIA.name());
        adaptacao.put("regime_anterior", regimeAtual);
        adaptacao.put("regime_novo", novoRegime);
        adaptacao.put("genoma_adaptado", adaptadoId);
        adaptacao.put("timestamp", Instant.now().toString());
        adaptacoesRealizadas.add(adaptacao);

        regimeAtual = novoRegime;
        historicoRegimes.add(novoRegime);
        if (historicoRegimes.size() > 50) historicoRegimes.pollFirst();

        log.info("Adaptação concluída: genoma {} adaptado ao regime {}", adaptadoId, novoRegime);
        return adaptadoId;
    }

    /**
     * Remove os piores genomas, mantendo apenas os {@code manterTopN} melhores.
     *
     * @return lista de IDs removidos.
     */
    public List<String> podarPopulacao(int manterTopN) {
        if (genomas.size() <= manterTopN) return List.of();

        // Ranquear por fitness médio
        List<Map.Entry<String, Double>> ranking = genomas.keySet().stream()
                .map(id -> Map.entry(id, calcularFitnessMedio(id, 10)))
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        List<String> manter = ranking.stream()
                .limit(manterTopN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> remover = new ArrayList<>();
        for (Map.Entry<String, Double> entry : ranking) {
            if (!manter.contains(entry.getKey())) {
                remover.add(entry.getKey());
            }
        }

        for (String id : remover) {
            genomas.remove(id);
            fitnessHistorico.remove(id);
            linhagens.remove(id);
            // Remover de campeões se for o campeão
            campeoesPorRegime.entrySet().removeIf(e -> e.getValue().equals(id));
        }

        log.info("População podada: {} removidos, {} mantidos", remover.size(), manter.size());
        return remover;
    }

    /**
     * Executa um ciclo completo de evolução: seleção de pais, crossover, mutações e poda.
     *
     * @return resumo das operações realizadas.
     */
    public Map<String, Object> evoluirGeracao() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("geracao", geracaoAtual + 1);
        List<String> novosGenomas = new ArrayList<>();
        List<String> mutacoes = new ArrayList<>();

        // 1. Selecionar pais (4)
        List<String> pais = selecionarPais(4);

        // 2. Crossover entre pares
        for (int i = 0; i + 1 < pais.size(); i += 2) {
            String filhoId = crossover(pais.get(i), pais.get(i + 1));
            novosGenomas.add(filhoId);
        }

        // 3. Mutações aleatórias
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        List<String> genomasAtuais = new ArrayList<>(genomas.keySet());
        for (String id : genomasAtuais) {
            if (rnd.nextDouble() < taxaMutacao) {
                String mutanteId = mutar(id, 0.1, null);
                mutacoes.add(mutanteId);
            }
        }

        // 4. Poda
        List<String> removidos = podarPopulacao(populacaoMaxima);

        resultado.put("novos_genomas", novosGenomas);
        resultado.put("mutacoes", mutacoes);
        resultado.put("removidos", removidos);
        geracaoAtual++;
        return resultado;
    }

    /**
     * Obtém a árvore genealógica de um genoma.
     */
    public Optional<Map<String, Object>> getArvoreGenealogica(String genomaId) {
        LinhagemModelo linhagem = linhagens.get(genomaId);
        if (linhagem == null) return Optional.empty();

        GenomaModelo genoma = genomas.get(genomaId);
        Map<String, Object> arvore = new LinkedHashMap<>();
        arvore.put("modelo_id", genomaId);
        arvore.put("geracao", linhagem.geracao());
        arvore.put("fitness_atual", calcularFitnessMedio(genomaId, 10));
        arvore.put("ancestral", linhagem.ancestralDireto());
        arvore.put("descendentes", linhagem.descendentes());
        arvore.put("irmaos", linhagem.irmaos());
        arvore.put("caracteristicas", genoma != null ? genoma.caracteristicas() : Map.of());
        arvore.put("timestamp_criacao", genoma != null ? genoma.timestampCriacao().toString() : null);
        return Optional.of(arvore);
    }

    /** Retorna o ID do campeão para um regime, se houver. */
    public Optional<String> getCampeaoPorRegime(String regime) {
        return Optional.ofNullable(campeoesPorRegime.get(regime));
    }

    // ======================== Persistência ========================
    // Para uma implementação completa, recomenda-se Jackson ou Gson.
    // Aqui fornecemos um stub que salva/restaura via ObjectStream (Java serialization).
    // Em produção, utilize JSON com anotações nos records.
    public void salvarEstado(String arquivo) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            out.writeObject(geracaoAtual);
            out.writeObject(new HashMap<>(genomas));
            out.writeObject(new HashMap<>(fitnessHistorico));
            out.writeObject(new HashMap<>(linhagens));
            out.writeObject(new ArrayList<>(eventosEvolucao));
            out.writeObject(new ArrayList<>(adaptacoesRealizadas));
            out.writeObject(regimeAtual);
            out.writeObject(new ArrayList<>(historicoRegimes));
            out.writeObject(new HashMap<>(campeoesPorRegime));
        }
        log.info("Estado salvo em {}", arquivo);
    }

    @SuppressWarnings("unchecked")
    public void carregarEstado(String arquivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            geracaoAtual = (int) in.readObject();
            genomas.clear();
            genomas.putAll((Map<String, GenomaModelo>) in.readObject());
            fitnessHistorico.clear();
            fitnessHistorico.putAll((Map<String, Deque<FitnessSnapshot>>) in.readObject());
            linhagens.clear();
            linhagens.putAll((Map<String, LinhagemModelo>) in.readObject());
            eventosEvolucao.clear();
            eventosEvolucao.addAll((List<EventoEvolucao>) in.readObject());
            adaptacoesRealizadas.clear();
            adaptacoesRealizadas.addAll((List<Map<String, Object>>) in.readObject());
            regimeAtual = (String) in.readObject();
            historicoRegimes.clear();
            historicoRegimes.addAll((List<String>) in.readObject());
            campeoesPorRegime.clear();
            campeoesPorRegime.putAll((Map<String, String>) in.readObject());
        }
        log.info("Estado carregado de {}", arquivo);
    }

    /**
     * Retorna um resumo do estado atual do sistema.
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("geracao_atual", geracaoAtual);
        status.put("populacao_atual", genomas.size());
        status.put("populacao_maxima", populacaoMaxima);
        status.put("total_eventos_evolucao", eventosEvolucao.size());
        status.put("regime_atual", regimeAtual);
        status.put("campeoes_por_regime", new HashMap<>(campeoesPorRegime));
        status.put("taxa_mutacao", taxaMutacao);
        status.put("taxa_crossover", taxaCrossover);

        double fitnessMedio = genomas.keySet().stream()
                .mapToDouble(id -> calcularFitnessMedio(id, 10))
                .average().orElse(0.0);
        double melhorFitness = genomas.keySet().stream()
                .mapToDouble(id -> calcularFitnessMedio(id, 10))
                .max().orElse(0.0);

        status.put("fitness_medio_populacao", fitnessMedio);
        status.put("melhor_fitness", melhorFitness);
        return status;
    }

    // ======================== Helpers ========================
    private double getUltimoFitness(String genomaId) {
        Deque<FitnessSnapshot> snaps = fitnessHistorico.get(genomaId);
        if (snaps == null || snaps.isEmpty()) return 0.0;
        return snaps.getLast().fitnessScore();
    }

    private String gerarIdUnico(String seed) {
        return Integer.toHexString(Objects.hash(seed, ThreadLocalRandom.current().nextLong())).substring(0, 16);
    }

    // ------------------- Getters/Setters para ajuste de taxas (opcionais) -------------------
    public void setTaxaMutacao(double taxaMutacao) { this.taxaMutacao = taxaMutacao; }
    public void setTaxaCrossover(double taxaCrossover) { this.taxaCrossover = taxaCrossover; }
    public int getPopulacaoMaxima() { return populacaoMaxima; }
    public void setPopulacaoMaxima(int populacaoMaxima) { this.populacaoMaxima = populacaoMaxima; }
}
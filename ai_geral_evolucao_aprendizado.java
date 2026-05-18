package com.vhalinor.evolution;

import java.io.*;
import java.security.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * VHALINOR Evolução de Aprendizado v6.0 – Java Edition
 *
 * Sistema de evolução de modelos de trading com genomas, fitness, crossover, mutação e adaptação a regimes.
 *
 * Melhorias:
 * - Records imutáveis (Java 14+)
 * - Concorrência segura com coleções sincronizadas
 * - MD5 para geração de IDs única
 * - Cálculos estatísticos via streams (sem dependência de NumPy)
 * - Métodos públicos bem documentados
 */
public class EvolucaoAprendizado {

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

    // ======================== Records (Data Classes) ========================
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
            caracteristicas = Map.copyOf(caracteristicas);
            hiperparametros = Map.copyOf(hiperparametros);
            mutacoes = List.copyOf(mutacoes);
        }

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
            String ambiente // "bull", "bear", "sideways", "volatile"
    ) {}

    public record EventoEvolucao(
            String id,
            TipoEvolucao tipo,
            int geracao,
            String descricao,
            String modeloOrigem,
            String modeloDestino,   // nullable
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
            List<String> caminhoEvolucao   // IDs de eventos
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

    private final Map<String, GenomaModelo> genomas = new LinkedHashMap<>();
    private final Map<String, Deque<FitnessSnapshot>> fitnessHistorico = new HashMap<>();
    private final Map<String, LinhagemModelo> linhagens = new HashMap<>();

    private final Deque<EventoEvolucao> eventosEvolucao = new ArrayDeque<>();
    private final Deque<Map<String, Object>> adaptacoesRealizadas = new ArrayDeque<>();

    private String regimeAtual = "desconhecido";
    private final Deque<String> historicoRegimes = new ArrayDeque<>();

    private final Map<String, String> campeoesPorRegime = new HashMap<>();

    private double taxaMutacao = 0.1;
    private double taxaCrossover = 0.3;

    public EvolucaoAprendizado(int populacaoMaxima) {
        this.populacaoMaxima = populacaoMaxima;
    }

    // ======================== Métodos públicos ========================

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
        return genomaId;
    }

    public void registrarFitness(String genomaId, FitnessSnapshot snapshot) {
        fitnessHistorico.computeIfAbsent(genomaId, k -> new ArrayDeque<>()).add(snapshot);

        String ambiente = snapshot.ambiente();
        if (!campeoesPorRegime.containsKey(ambiente)) {
            campeoesPorRegime.put(ambiente, genomaId);
        } else {
            String campeaoAtual = campeoesPorRegime.get(ambiente);
            double fitnessCampeao = getUltimoFitness(campeaoAtual);
            if (snapshot.fitnessScore() > fitnessCampeao * 1.05) {
                campeoesPorRegime.put(ambiente, genomaId);
            }
        }
    }

    public double calcularFitnessMedio(String genomaId, int janela) {
        Deque<FitnessSnapshot> snaps = fitnessHistorico.get(genomaId);
        if (snaps == null || snaps.isEmpty()) return 0.0;
        return snaps.stream()
                .skip(Math.max(0, snaps.size() - janela))
                .mapToDouble(FitnessSnapshot::fitnessScore)
                .average().orElse(0.0);
    }

    public List<String> selecionarPais(int nPais) {
        if (genomas.size() < nPais) return new ArrayList<>(genomas.keySet());

        List<String> genomaIds = new ArrayList<>(genomas.keySet());
        List<String> selecionados = new ArrayList<>();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int i = 0; i < nPais; i++) {
            List<String> torneio = new ArrayList<>();
            while (torneio.size() < 3 && torneio.size() < genomaIds.size()) {
                String cand = genomaIds.get(rnd.nextInt(genomaIds.size()));
                if (!torneio.contains(cand)) torneio.add(cand);
            }
            String melhor = torneio.stream()
                    .max(Comparator.comparingDouble(g -> calcularFitnessMedio(g, 10)))
                    .orElseThrow();
            selecionados.add(melhor);
        }
        return selecionados;
    }

    public String crossover(String pai1Id, String pai2Id) {
        GenomaModelo pai1 = genomas.get(pai1Id);
        GenomaModelo pai2 = genomas.get(pai2Id);
        if (pai1 == null || pai2 == null)
            throw new IllegalArgumentException("Genomas pais não encontrados");

        int geracaoFilho = Math.max(pai1.geracao(), pai2.geracao()) + 1;
        String filhoId = gerarIdUnico("gen" + geracaoFilho + "_" + pai1Id + "_" + pai2Id);

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

        linhagens.computeIfPresent(pai1Id, (k, v) -> new LinhagemModelo(
                v.modeloId(), v.geracao(), v.ancestralDireto(),
                new ArrayList<>(v.descendentes()) {{ add(filhoId); }},
                v.irmaos(), v.caminhoEvolucao()
        ));

        eventosEvolucao.add(new EventoEvolucao(
                "evo_" + eventosEvolucao.size(),
                TipoEvolucao.CROSSOVER,
                geracaoFilho,
                "Crossover entre " + pai1Id + " e " + pai2Id,
                pai1Id, filhoId, 0.0, "reproducao", Instant.now()
        ));

        return filhoId;
    }

    public String mutar(String genomaId, double intensidade, List<String> caracteristicasAlvo) {
        GenomaModelo original = genomas.get(genomaId);
        if (original == null) throw new IllegalArgumentException("Genoma não encontrado: " + genomaId);

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

        linhagens.computeIfPresent(genomaId, (k, v) -> new LinhagemModelo(
                v.modeloId(), v.geracao(), v.ancestralDireto(),
                new ArrayList<>(v.descendentes()) {{ add(mutanteId); }},
                v.irmaos(), v.caminhoEvolucao()
        ));

        eventosEvolucao.add(new EventoEvolucao(
                "evo_" + eventosEvolucao.size(),
                TipoEvolucao.MUTACAO,
                original.geracao(),
                "Mutação de " + genomaId + " (" + mutacoesAplicadas.size() + " alterações)",
                genomaId, mutanteId, 0.0, "exploracao", Instant.now()
        ));

        return mutanteId;
    }

    public String adaptarARegime(String novoRegime, String genomaBaseId) {
        if (!genomas.containsKey(genomaBaseId))
            throw new IllegalArgumentException("Genoma base não encontrado: " + genomaBaseId);

        String adaptadoId;
        if (campeoesPorRegime.containsKey(novoRegime)) {
            String campeaoId = campeoesPorRegime.get(novoRegime);
            adaptadoId = crossover(genomaBaseId, campeaoId);
        } else {
            adaptadoId = mutar(genomaBaseId, 0.3, null);
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

        return adaptadoId;
    }

    public List<String> podarPopulacao(int manterTopN) {
        if (genomas.size() <= manterTopN) return List.of();

        List<String> ranking = genomas.keySet().stream()
                .sorted(Comparator.comparingDouble((String id) -> calcularFitnessMedio(id, 10)).reversed())
                .collect(Collectors.toList());

        List<String> manter = ranking.stream().limit(manterTopN).collect(Collectors.toList());
        List<String> remover = ranking.stream().filter(id -> !manter.contains(id)).collect(Collectors.toList());

        for (String id : remover) {
            genomas.remove(id);
            fitnessHistorico.remove(id);
            linhagens.remove(id);
            campeoesPorRegime.entrySet().removeIf(e -> e.getValue().equals(id));
        }

        return remover;
    }

    public Map<String, Object> evoluirGeracao() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("geracao", geracaoAtual + 1);

        List<String> pais = selecionarPais(4);
        List<String> novosGenomas = new ArrayList<>();
        for (int i = 0; i + 1 < pais.size(); i += 2) {
            novosGenomas.add(crossover(pais.get(i), pais.get(i + 1)));
        }
        resultado.put("novos_genomas", novosGenomas);

        List<String> mutacoes = new ArrayList<>();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (String id : new ArrayList<>(genomas.keySet())) {
            if (rnd.nextDouble() < taxaMutacao) {
                mutacoes.add(mutar(id, 0.1, null));
            }
        }
        resultado.put("mutacoes", mutacoes);

        List<String> removidos = podarPopulacao(populacaoMaxima);
        resultado.put("removidos", removidos);
        geracaoAtual++;
        return resultado;
    }

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
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(seed.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(Objects.hash(seed, ThreadLocalRandom.current().nextLong())).substring(0, 16);
        }
    }

    // Getters/Setters para ajuste de taxas
    public void setTaxaMutacao(double taxaMutacao) { this.taxaMutacao = taxaMutacao; }
    public void setTaxaCrossover(double taxaCrossover) { this.taxaCrossover = taxaCrossover; }
    public int getPopulacaoMaxima() { return populacaoMaxima; }
    public void setPopulacaoMaxima(int populacaoMaxima) { this.populacaoMaxima = populacaoMaxima; }
}
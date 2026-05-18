package com.vhalinor.ganhos;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.util.logging.*;

/**
 * VHALINOR IAG 1.0.0 - GERENCIADOR DE GANHOS QUÂNTICO (Java)
 * Sistema de gerenciamento de posições com níveis exponenciais.
 *
 * Módulo: GERENCIADOR DE GANHOS ELEVADOS
 * Versão: 3.0.0 Java (adaptado do Python original)
 * Autor: Alex Miranda Sales
 * Data: 2026
 */
public class VhalinorGanhosManager {

    // ============================================================================
    // ENUMS
    // ============================================================================

    public enum NivelGanho {
        INICIAL("80%", "🟢", 80.0, "Nível Inicial - Recuperação de Capital"),
        INTERMEDIARIO("200%", "🔵", 200.0, "Nível Intermediário - Lucro Significativo"),
        AVANCADO("500%", "🟠", 500.0, "Nível Avançado - Performance Superior"),
        EXCEPCIONAL("1000%", "🔴", 1000.0, "Nível Excepcional - Retorno Extraordinário"),
        MITICO("2000%", "💎", 2000.0, "Nível Mítico - Raro"),
        LENDARIO("5000%", "👑", 5000.0, "Nível Lendário - Extremamente Raro");

        private final String label;
        private final String icon;
        private final double valor;
        private final String descricao;

        NivelGanho(String label, String icon, double valor, String descricao) {
            this.label = label;
            this.icon = icon;
            this.valor = valor;
            this.descricao = descricao;
        }

        public String getLabel() { return label; }
        public String getIcon() { return icon; }
        public double getValor() { return valor; }
        public String getDescricao() { return descricao; }

        public double getPorcentagem() { return valor; }
    }

    public enum TipoAtivo {
        CRIPTOMOEDA("Criptomoeda", "₿", "alta_volatilidade"),
        ACAO("Ação", "📈", "media_volatilidade"),
        ETF("ETF", "📊", "media_volatilidade"),
        FUTURO("Futuro", "⚡", "alta_volatilidade"),
        OPCAO("Opção", "🎲", "muito_alta_volatilidade"),
        FOREX("Forex", "💱", "baixa_volatilidade"),
        COMMODITY("Commodity", "🛢️", "media_volatilidade"),
        RENDA_FIXA("Renda Fixa", "🏦", "baixissima_volatilidade");

        private final String label;
        private final String icon;
        private final String perfilRisco;

        TipoAtivo(String label, String icon, String perfilRisco) {
            this.label = label;
            this.icon = icon;
            this.perfilRisco = perfilRisco;
        }

        public String getLabel() { return label; }
        public String getIcon() { return icon; }
        public String getPerfilRisco() { return perfilRisco; }
    }

    public enum EstrategiaSaida {
        PROGRESSIVA, REGRESSIVA, EQUITATIVA, CONSERVADORA,
        AGRESSIVA, TRAILING_STOP, PARCIAL_TOTAL
    }

    public enum EstadoPosicao {
        ABERTA("Aberta", "verde", "Posição ativa"),
        PARCIALMENTE_VENDIDA("Parcial", "amarelo", "Parte da posição vendida"),
        FECHADA("Fechada", "vermelho", "Posição totalmente vendida"),
        AGUARDANDO("Aguardando", "laranja", "Aguardando entrada"),
        CANCELADA("Cancelada", "preto", "Posição cancelada"),
        ERRO("Erro", "erro", "Erro na posição");

        private final String label;
        private final String cor;
        private final String descricao;

        EstadoPosicao(String label, String cor, String descricao) {
            this.label = label;
            this.cor = cor;
            this.descricao = descricao;
        }

        public String getLabel() { return label; }
        public String getCor() { return cor; }
        public String getDescricao() { return descricao; }
    }

    public enum MetricasChave {
        ROI("roi", "📊", "Return on Investment"),
        SHARPE("sharpe", "📈", "Índice de Sharpe"),
        SORTINO("sortino", "📉", "Índice de Sortino"),
        WIN_RATE("win_rate", "🎯", "Taxa de Acerto"),
        PROFIT_FACTOR("profit_factor", "💰", "Fator de Lucro"),
        MAX_DRAWDOWN("max_dd", "⚠️", "Drawdown Máximo"),
        RECOVERY_FACTOR("recovery", "🔄", "Fator de Recuperação"),
        EXPECTANCIA("expectancy", "📐", "Expectância Matemática");

        private final String chave;
        private final String icon;
        private final String descricao;

        MetricasChave(String chave, String icon, String descricao) {
            this.chave = chave;
            this.icon = icon;
            this.descricao = descricao;
        }

        public String getChave() { return chave; }
        public String getIcon() { return icon; }
        public String getDescricao() { return descricao; }
    }

    // ============================================================================
    // CONFIGURAÇÃO DE GANHOS
    // ============================================================================

    public static class ConfiguracaoGanhos {
        private double nivel1 = 80.0, nivel2 = 200.0, nivel3 = 500.0,
                       nivel4 = 1000.0, nivel5 = 2000.0, nivel6 = 5000.0;
        private double volume1 = 0.15, volume2 = 0.15, volume3 = 0.15,
                       volume4 = 0.15, volume5 = 0.20, volume6 = 0.20;
        private EstrategiaSaida estrategia = EstrategiaSaida.PROGRESSIVA;
        private boolean trailingStopAtivado = false;
        private double trailingStopDistancia = 10.0;
        private boolean reentradaPermitida = false;
        private double reentradaNivelMinimo = 50.0;
        private String nome = "Configuração Padrão";
        private String descricao = "Configuração com 6 níveis de ganho progressivos";
        private Instant criadoEm = Instant.now();

        // Factory methods
        public static ConfiguracaoGanhos criarEstrategiaProgressiva() {
            ConfiguracaoGanhos c = new ConfiguracaoGanhos();
            c.volume1=0.10; c.volume2=0.12; c.volume3=0.15;
            c.volume4=0.18; c.volume5=0.20; c.volume6=0.25;
            c.estrategia = EstrategiaSaida.PROGRESSIVA;
            c.nome = "Estratégia Progressiva";
            c.descricao = "Volumes crescentes conforme níveis mais altos";
            return c;
        }

        public static ConfiguracaoGanhos criarEstrategiaConservadora() {
            ConfiguracaoGanhos c = new ConfiguracaoGanhos();
            c.volume1=0.25; c.volume2=0.20; c.volume3=0.15;
            c.volume4=0.15; c.volume5=0.15; c.volume6=0.10;
            c.estrategia = EstrategiaSaida.CONSERVADORA;
            c.nome = "Estratégia Conservadora";
            c.descricao = "Maior volume nos níveis iniciais para garantir lucro";
            return c;
        }

        public static ConfiguracaoGanhos criarEstrategiaAgressiva() {
            ConfiguracaoGanhos c = new ConfiguracaoGanhos();
            c.volume1=0.05; c.volume2=0.08; c.volume3=0.12;
            c.volume4=0.15; c.volume5=0.25; c.volume6=0.35;
            c.estrategia = EstrategiaSaida.AGRESSIVA;
            c.nome = "Estratégia Agressiva";
            c.descricao = "Maior volume nos níveis mais altos para máximo ganho";
            return c;
        }

        public static ConfiguracaoGanhos criarEstrategiaEquitativa() {
            ConfiguracaoGanhos c = new ConfiguracaoGanhos();
            double vol = 1.0 / 6;
            c.volume1=vol; c.volume2=vol; c.volume3=vol;
            c.volume4=vol; c.volume5=vol; c.volume6=vol;
            c.estrategia = EstrategiaSaida.EQUITATIVA;
            c.nome = "Estratégia Equitativa";
            c.descricao = "Volumes iguais em todos os níveis";
            return c;
        }

        // Convenience getters for volumes per level
        public double getVolumePorNivel(int nivel) {
            switch (nivel) {
                case 1: return volume1;
                case 2: return volume2;
                case 3: return volume3;
                case 4: return volume4;
                case 5: return volume5;
                case 6: return volume6;
                default: return 0.0;
            }
        }

        public double getNivelPorcentagem(int nivel) {
            switch (nivel) {
                case 1: return nivel1;
                case 2: return nivel2;
                case 3: return nivel3;
                case 4: return nivel4;
                case 5: return nivel5;
                case 6: return nivel6;
                default: return Double.MAX_VALUE;
            }
        }

        // Getters and setters (omitted for brevity – can be added with Lombok or manually)
        public double getNivel1() { return nivel1; }
        public void setNivel1(double nivel1) { this.nivel1 = nivel1; }
        public double getNivel2() { return nivel2; }
        public void setNivel2(double nivel2) { this.nivel2 = nivel2; }
        public double getNivel3() { return nivel3; }
        public void setNivel3(double nivel3) { this.nivel3 = nivel3; }
        public double getNivel4() { return nivel4; }
        public void setNivel4(double nivel4) { this.nivel4 = nivel4; }
        public double getNivel5() { return nivel5; }
        public void setNivel5(double nivel5) { this.nivel5 = nivel5; }
        public double getNivel6() { return nivel6; }
        public void setNivel6(double nivel6) { this.nivel6 = nivel6; }

        public double getVolume1() { return volume1; }
        public double getVolume2() { return volume2; }
        public double getVolume3() { return volume3; }
        public double getVolume4() { return volume4; }
        public double getVolume5() { return volume5; }
        public double getVolume6() { return volume6; }

        public EstrategiaSaida getEstrategia() { return estrategia; }
        public void setEstrategia(EstrategiaSaida estrategia) { this.estrategia = estrategia; }
        public boolean isTrailingStopAtivado() { return trailingStopAtivado; }
        public void setTrailingStopAtivado(boolean trailingStopAtivado) { this.trailingStopAtivado = trailingStopAtivado; }
        public double getTrailingStopDistancia() { return trailingStopDistancia; }
        public void setTrailingStopDistancia(double trailingStopDistancia) { this.trailingStopDistancia = trailingStopDistancia; }
        public boolean isReentradaPermitida() { return reentradaPermitida; }
        public double getReentradaNivelMinimo() { return reentradaNivelMinimo; }
        public String getNome() { return nome; }
        public String getDescricao() { return descricao; }
        public Instant getCriadoEm() { return criadoEm; }
    }

    // ============================================================================
    // POSIÇÃO
    // ============================================================================

    public static class Posicao {
        private final String id;
        private final String simbolo;
        private final TipoAtivo tipoAtivo;
        private double precoEntrada;
        private double volumeOriginal;
        private Instant dataEntrada = Instant.now();
        private double precoAtual;
        private Instant dataAtualizacao;
        private EstadoPosicao estado = EstadoPosicao.ABERTA;
        private String configuracoesId;

        // Flags de níveis executados
        private boolean nivel1Executado = false, nivel2Executado = false, nivel3Executado = false,
                        nivel4Executado = false, nivel5Executado = false, nivel6Executado = false;

        private double volumeVendido = 0.0;
        private double valorRealizado = 0.0;

        private Double trailingStopPreco = null;
        private Double trailingStopMaximo = null;

        private List<String> tags = new ArrayList<>();
        private String notas = "";
        private Map<String, Object> metadados = new HashMap<>();

        public Posicao(String id, String simbolo, TipoAtivo tipoAtivo, double precoEntrada, double volumeOriginal) {
            this.id = id;
            this.simbolo = simbolo;
            this.tipoAtivo = tipoAtivo;
            this.precoEntrada = precoEntrada;
            this.volumeOriginal = volumeOriginal;
            this.precoAtual = precoEntrada;
            this.dataAtualizacao = Instant.now();
        }

        // Propriedades calculadas
        public double getGanhoPercentual() {
            return precoEntrada == 0 ? 0 : ((precoAtual - precoEntrada) / precoEntrada) * 100;
        }

        public double getGanhoAbsoluto() {
            return (precoAtual - precoEntrada) * getVolumeRestante();
        }

        public double getGanhoRealizado() {
            return valorRealizado - (precoEntrada * volumeVendido);
        }

        public double getGanhoTotal() {
            return getGanhoRealizado() + getGanhoAbsoluto();
        }

        public double getVolumeRestante() {
            return volumeOriginal - volumeVendido;
        }

        public double getPercentualVendido() {
            return volumeOriginal == 0 ? 0 : (volumeVendido / volumeOriginal) * 100;
        }

        public boolean isEstaFechada() {
            return volumeVendido >= volumeOriginal;
        }

        // Venda parcial
        public Map<String, Object> venderParcial(double volume, double preco) {
            double volumeVenda = Math.min(volume, getVolumeRestante());
            if (volumeVenda <= 0) {
                return Map.of("sucesso", false, "erro", "Volume de venda inválido");
            }
            volumeVendido += volumeVenda;
            valorRealizado += preco * volumeVenda;
            double custo = precoEntrada * volumeVenda;
            double resultado = valorRealizado - custo;
            double resultadoPct = (preco - precoEntrada) / precoEntrada * 100;

            if (isEstaFechada()) {
                estado = EstadoPosicao.FECHADA;
            } else {
                estado = EstadoPosicao.PARCIALMENTE_VENDIDA;
            }

            Map<String, Object> ret = new HashMap<>();
            ret.put("sucesso", true);
            ret.put("volumeVendido", volumeVenda);
            ret.put("precoVenda", preco);
            ret.put("resultado", resultado);
            ret.put("resultadoPercentual", resultadoPct);
            ret.put("volumeRestante", getVolumeRestante());
            return ret;
        }

        public void atualizarPreco(double novoPreco) {
            this.precoAtual = novoPreco;
            this.dataAtualizacao = Instant.now();
        }

        // Getters e setters (basic)
        public String getId() { return id; }
        public String getSimbolo() { return simbolo; }
        public TipoAtivo getTipoAtivo() { return tipoAtivo; }
        public double getPrecoEntrada() { return precoEntrada; }
        public double getVolumeOriginal() { return volumeOriginal; }
        public Instant getDataEntrada() { return dataEntrada; }
        public double getPrecoAtual() { return precoAtual; }
        public EstadoPosicao getEstado() { return estado; }
        public void setEstado(EstadoPosicao estado) { this.estado = estado; }
        public double getVolumeVendido() { return volumeVendido; }
        public double getValorRealizado() { return valorRealizado; }

        public boolean isNivel1Executado() { return nivel1Executado; }
        public void setNivel1Executado(boolean nivel1Executado) { this.nivel1Executado = nivel1Executado; }
        public boolean isNivel2Executado() { return nivel2Executado; }
        public void setNivel2Executado(boolean nivel2Executado) { this.nivel2Executado = nivel2Executado; }
        public boolean isNivel3Executado() { return nivel3Executado; }
        public void setNivel3Executado(boolean nivel3Executado) { this.nivel3Executado = nivel3Executado; }
        public boolean isNivel4Executado() { return nivel4Executado; }
        public void setNivel4Executado(boolean nivel4Executado) { this.nivel4Executado = nivel4Executado; }
        public boolean isNivel5Executado() { return nivel5Executado; }
        public void setNivel5Executado(boolean nivel5Executado) { this.nivel5Executado = nivel5Executado; }
        public boolean isNivel6Executado() { return nivel6Executado; }
        public void setNivel6Executado(boolean nivel6Executado) { this.nivel6Executado = nivel6Executado; }

        public Double getTrailingStopPreco() { return trailingStopPreco; }
        public void setTrailingStopPreco(Double trailingStopPreco) { this.trailingStopPreco = trailingStopPreco; }
        public Double getTrailingStopMaximo() { return trailingStopMaximo; }
        public void setTrailingStopMaximo(Double trailingStopMaximo) { this.trailingStopMaximo = trailingStopMaximo; }
    }

    // ============================================================================
    // SAÍDA
    // ============================================================================

    public static class Saida {
        private final String id;
        private final String idPosicao;
        private final NivelGanho nivel;
        private final double volume;
        private final double preco;
        private final double ganhoPercentual;
        private final double ganhoAbsoluto;
        private final Instant timestamp = Instant.now();
        private double precoEntradaReferencia;
        private double volumeRestanteApos;
        private double percentualPosicao;

        public Saida(String id, String idPosicao, NivelGanho nivel, double volume, double preco,
                     double ganhoPercentual, double ganhoAbsoluto) {
            this.id = id;
            this.idPosicao = idPosicao;
            this.nivel = nivel;
            this.volume = volume;
            this.preco = preco;
            this.ganhoPercentual = ganhoPercentual;
            this.ganhoAbsoluto = ganhoAbsoluto;
        }

        public String getId() { return id; }
        public String getIdPosicao() { return idPosicao; }
        public NivelGanho getNivel() { return nivel; }
        public double getVolume() { return volume; }
        public double getPreco() { return preco; }
        public double getGanhoPercentual() { return ganhoPercentual; }
        public double getGanhoAbsoluto() { return ganhoAbsoluto; }
        public Instant getTimestamp() { return timestamp; }
        public double getPrecoEntradaReferencia() { return precoEntradaReferencia; }
        public void setPrecoEntradaReferencia(double val) { this.precoEntradaReferencia = val; }
        public double getVolumeRestanteApos() { return volumeRestanteApos; }
        public void setVolumeRestanteApos(double val) { this.volumeRestanteApos = val; }
        public double getPercentualPosicao() { return percentualPosicao; }
        public void setPercentualPosicao(double val) { this.percentualPosicao = val; }
    }

    // ============================================================================
    // RELATÓRIO DE PERFORMANCE
    // ============================================================================

    public static class RelatorioPerformance {
        private Instant periodoInicio, periodoFim;
        private int totalPosicoes, posicoesFechadas, posicoesAbertas;
        private int totalSaidas;
        private Map<String, Integer> saidasPorNivel = new HashMap<>();
        private double volumeTotalNegociado, valorTotalRealizado, ganhoTotalRealizado, ganhoTotalNaoRealizado;
        private double roiTotal, winRate, profitFactor, maxDrawdown;
        private double sharpeRatio, sortinoRatio, calmarRatio, expectancy;

        // Getters/setters
        public Instant getPeriodoInicio() { return periodoInicio; }
        public void setPeriodoInicio(Instant periodoInicio) { this.periodoInicio = periodoInicio; }
        public Instant getPeriodoFim() { return periodoFim; }
        public void setPeriodoFim(Instant periodoFim) { this.periodoFim = periodoFim; }
        public int getTotalPosicoes() { return totalPosicoes; }
        public void setTotalPosicoes(int totalPosicoes) { this.totalPosicoes = totalPosicoes; }
        public int getPosicoesFechadas() { return posicoesFechadas; }
        public void setPosicoesFechadas(int posicoesFechadas) { this.posicoesFechadas = posicoesFechadas; }
        public int getPosicoesAbertas() { return posicoesAbertas; }
        public void setPosicoesAbertas(int posicoesAbertas) { this.posicoesAbertas = posicoesAbertas; }
        public int getTotalSaidas() { return totalSaidas; }
        public void setTotalSaidas(int totalSaidas) { this.totalSaidas = totalSaidas; }
        public Map<String, Integer> getSaidasPorNivel() { return saidasPorNivel; }
        public void setSaidasPorNivel(Map<String, Integer> saidasPorNivel) { this.saidasPorNivel = saidasPorNivel; }
        public double getVolumeTotalNegociado() { return volumeTotalNegociado; }
        public void setVolumeTotalNegociado(double volumeTotalNegociado) { this.volumeTotalNegociado = volumeTotalNegociado; }
        public double getValorTotalRealizado() { return valorTotalRealizado; }
        public void setValorTotalRealizado(double valorTotalRealizado) { this.valorTotalRealizado = valorTotalRealizado; }
        public double getGanhoTotalRealizado() { return ganhoTotalRealizado; }
        public void setGanhoTotalRealizado(double ganhoTotalRealizado) { this.ganhoTotalRealizado = ganhoTotalRealizado; }
        public double getGanhoTotalNaoRealizado() { return ganhoTotalNaoRealizado; }
        public void setGanhoTotalNaoRealizado(double ganhoTotalNaoRealizado) { this.ganhoTotalNaoRealizado = ganhoTotalNaoRealizado; }
        public double getRoiTotal() { return roiTotal; }
        public void setRoiTotal(double roiTotal) { this.roiTotal = roiTotal; }
        public double getWinRate() { return winRate; }
        public void setWinRate(double winRate) { this.winRate = winRate; }
        public double getProfitFactor() { return profitFactor; }
        public void setProfitFactor(double profitFactor) { this.profitFactor = profitFactor; }
        public double getMaxDrawdown() { return maxDrawdown; }
        public void setMaxDrawdown(double maxDrawdown) { this.maxDrawdown = maxDrawdown; }
        public double getSharpeRatio() { return sharpeRatio; }
        public void setSharpeRatio(double sharpeRatio) { this.sharpeRatio = sharpeRatio; }
        public double getSortinoRatio() { return sortinoRatio; }
        public void setSortinoRatio(double sortinoRatio) { this.sortinoRatio = sortinoRatio; }
        public double getCalmarRatio() { return calmarRatio; }
        public void setCalmarRatio(double calmarRatio) { this.calmarRatio = calmarRatio; }
        public double getExpectancy() { return expectancy; }
        public void setExpectancy(double expectancy) { this.expectancy = expectancy; }
    }

    // ============================================================================
    // GERENCIADOR DE GANHOS
    // ============================================================================

    public static class GerenciadorGanhos {
        private final ConfiguracaoGanhos config;
        private final Logger logger = Logger.getLogger(GerenciadorGanhos.class.getName());

        public GerenciadorGanhos(ConfiguracaoGanhos config) {
            this.config = config != null ? config : new ConfiguracaoGanhos();
            logger.info("Gerenciador de Ganhos inicializado: " + this.config.getNome());
        }

        public double calcularGanhoPercentual(double precoEntrada, double precoAtual) {
            return precoEntrada == 0 ? 0 : ((precoAtual - precoEntrada) / precoEntrada) * 100;
        }

        public List<Integer> verificarNiveisAtingidos(Posicao posicao) {
            List<Integer> niveis = new ArrayList<>();
            double ganho = posicao.getGanhoPercentual();
            if (ganho >= config.getNivel1() && !posicao.isNivel1Executado()) niveis.add(1);
            if (ganho >= config.getNivel2() && !posicao.isNivel2Executado()) niveis.add(2);
            if (ganho >= config.getNivel3() && !posicao.isNivel3Executado()) niveis.add(3);
            if (ganho >= config.getNivel4() && !posicao.isNivel4Executado()) niveis.add(4);
            if (ganho >= config.getNivel5() && !posicao.isNivel5Executado()) niveis.add(5);
            if (ganho >= config.getNivel6() && !posicao.isNivel6Executado()) niveis.add(6);
            return niveis;
        }

        public Saida gerenciarPosicao(Posicao posicao) {
            if (config.isTrailingStopAtivado() && posicao.getTrailingStopPreco() != null) {
                if (posicao.getPrecoAtual() <= posicao.getTrailingStopPreco()) {
                    return executarTrailingStop(posicao);
                }
            }
            List<Integer> niveis = verificarNiveisAtingidos(posicao);
            if (niveis.isEmpty()) return null;
            int nivel = Collections.min(niveis);
            return executarSaida(posicao, nivel);
        }

        private Saida executarSaida(Posicao posicao, int nivel) {
            double volumeVenda = posicao.getVolumeOriginal() * config.getVolumePorNivel(nivel);
            volumeVenda = Math.min(volumeVenda, posicao.getVolumeRestante());
            if (volumeVenda <= 0) return null;

            Map<String, Object> resultado = posicao.venderParcial(volumeVenda, posicao.getPrecoAtual());
            if (!(Boolean) resultado.get("sucesso")) return null;

            switch (nivel) {
                case 1: posicao.setNivel1Executado(true); break;
                case 2: posicao.setNivel2Executado(true); break;
                case 3: posicao.setNivel3Executado(true); break;
                case 4: posicao.setNivel4Executado(true); break;
                case 5: posicao.setNivel5Executado(true); break;
                case 6: posicao.setNivel6Executado(true); break;
            }

            NivelGanho nivelEnum = switch (nivel) {
                case 1 -> NivelGanho.INICIAL;
                case 2 -> NivelGanho.INTERMEDIARIO;
                case 3 -> NivelGanho.AVANCADO;
                case 4 -> NivelGanho.EXCEPCIONAL;
                case 5 -> NivelGanho.MITICO;
                case 6 -> NivelGanho.LENDARIO;
                default -> NivelGanho.INICIAL;
            };

            Saida saida = new Saida(
                "SAIDA_" + Instant.now().toString().replaceAll("[-:T.]","") + "_" + UUID.randomUUID().toString().substring(0,4),
                posicao.getId(),
                nivelEnum,
                volumeVenda,
                posicao.getPrecoAtual(),
                (Double) resultado.get("resultadoPercentual"),
                (Double) resultado.get("resultado")
            );
            saida.setPrecoEntradaReferencia(posicao.getPrecoEntrada());
            saida.setVolumeRestanteApos(posicao.getVolumeRestante());
            saida.setPercentualPosicao((volumeVenda / posicao.getVolumeOriginal()) * 100);

            logger.info(String.format("%s Saída Nível %d (%s) acionada: %.4f @ %.4f | Ganho: %.2f%% | Volume restante: %.4f",
                    nivelEnum.getIcon(), nivel, nivelEnum.getLabel(), volumeVenda, posicao.getPrecoAtual(),
                    (Double) resultado.get("resultadoPercentual"), posicao.getVolumeRestante()));
            return saida;
        }

        private Saida executarTrailingStop(Posicao posicao) {
            double volumeVenda = posicao.getVolumeRestante();
            if (volumeVenda <= 0) return null;
            Map<String, Object> resultado = posicao.venderParcial(volumeVenda, posicao.getTrailingStopPreco());
            if (!(Boolean) resultado.get("sucesso")) return null;

            Saida saida = new Saida(
                "TRAILING_" + Instant.now().toString().replaceAll("[-:T.]","") + "_" + UUID.randomUUID().toString().substring(0,4),
                posicao.getId(),
                NivelGanho.INICIAL,
                volumeVenda,
                posicao.getTrailingStopPreco(),
                (Double) resultado.get("resultadoPercentual"),
                (Double) resultado.get("resultado")
            );
            logger.warning(String.format("⚠️ Trailing Stop acionado: %.4f @ %.4f | Ganho: %.2f%%",
                    volumeVenda, posicao.getTrailingStopPreco(), (Double) resultado.get("resultadoPercentual")));
            return saida;
        }

        public void atualizarTrailingStop(Posicao posicao) {
            if (!config.isTrailingStopAtivado()) return;
            if (posicao.getTrailingStopMaximo() == null || posicao.getPrecoAtual() > posicao.getTrailingStopMaximo()) {
                posicao.setTrailingStopMaximo(posicao.getPrecoAtual());
                double distancia = config.getTrailingStopDistancia() / 100.0;
                posicao.setTrailingStopPreco(posicao.getTrailingStopMaximo() * (1 - distancia));
            }
        }
    }

    // ============================================================================
    // ANALISADOR DE PERFORMANCE
    // ============================================================================

    public static class AnalisadorPerformance {
        private final Logger logger = Logger.getLogger(AnalisadorPerformance.class.getName());

        public Map<String, Object> calcularMetricas(List<Saida> saidas, List<Posicao> posicoes) {
            if (saidas.isEmpty()) return Collections.emptyMap();
            Map<String, Object> metricas = new LinkedHashMap<>();
            metricas.put("totalSaidas", saidas.size());
            metricas.put("totalPosicoes", posicoes.size());
            metricas.put("posicoesFechadas", posicoes.stream().filter(Posicao::isEstaFechada).count());

            DoubleSummaryStatistics stats = saidas.stream().mapToDouble(Saida::getGanhoAbsoluto).summaryStatistics();
            metricas.put("ganhoTotal", stats.getSum());
            metricas.put("ganhoMedio", stats.getAverage());
            metricas.put("ganhoMaximo", stats.getMax());
            metricas.put("ganhoMinimo", stats.getMin());

            Map<String, Integer> porNivel = new HashMap<>();
            for (Saida s : saidas) {
                porNivel.merge(s.getNivel().getLabel(), 1, Integer::sum);
            }
            metricas.put("distribuicaoNiveis", porNivel);

            long ganhadoras = saidas.stream().filter(s -> s.getGanhoAbsoluto() > 0).count();
            metricas.put("winRate", saidas.size() > 0 ? (double) ganhadoras / saidas.size() * 100 : 0);

            double somaGanhos = saidas.stream().filter(s -> s.getGanhoAbsoluto() > 0).mapToDouble(Saida::getGanhoAbsoluto).sum();
            double somaPerdas = Math.abs(saidas.stream().filter(s -> s.getGanhoAbsoluto() < 0).mapToDouble(Saida::getGanhoAbsoluto).sum());
            metricas.put("profitFactor", somaPerdas > 0 ? somaGanhos / somaPerdas : Double.POSITIVE_INFINITY);

            double capitalTotal = posicoes.stream().mapToDouble(p -> p.getPrecoEntrada() * p.getVolumeOriginal()).sum();
            metricas.put("roi", capitalTotal > 0 ? (stats.getSum() / capitalTotal) * 100 : 0);

            // Simplified max drawdown and Sharpe (using cumulative gains)
            double cumulative = 0, peak = 0, maxDD = 0;
            List<Double> retornos = new ArrayList<>();
            for (Saida s : saidas) {
                double g = s.getGanhoAbsoluto();
                cumulative += g;
                retornos.add(g / capitalTotal);
                if (cumulative > peak) peak = cumulative;
                double dd = peak != 0 ? (cumulative - peak) / peak * 100 : 0;
                if (dd < maxDD) maxDD = dd;
            }
            metricas.put("maxDrawdown", Math.abs(maxDD));

            double meanRet = retornos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double stdRet = Math.sqrt(retornos.stream().mapToDouble(r -> Math.pow(r - meanRet, 2)).average().orElse(0));
            metricas.put("sharpeRatio", stdRet > 0 ? (meanRet / stdRet) * Math.sqrt(252) : 0);

            return metricas;
        }

        public RelatorioPerformance gerarRelatorio(List<Saida> saidas, List<Posicao> posicoes) {
            Map<String, Object> m = calcularMetricas(saidas, posicoes);
            RelatorioPerformance rel = new RelatorioPerformance();
            rel.setPeriodoInicio(Instant.now().minus(Duration.ofDays(30)));
            rel.setPeriodoFim(Instant.now());
            rel.setTotalPosicoes((int) m.getOrDefault("totalPosicoes", 0));
            rel.setPosicoesFechadas((int) m.getOrDefault("posicoesFechadas", 0));
            rel.setPosicoesAbertas(rel.getTotalPosicoes() - rel.getPosicoesFechadas());
            rel.setTotalSaidas((int) m.getOrDefault("totalSaidas", 0));
            @SuppressWarnings("unchecked")
            Map<String, Integer> niv = (Map<String, Integer>) m.getOrDefault("distribuicaoNiveis", Collections.emptyMap());
            rel.setSaidasPorNivel(niv);
            rel.setVolumeTotalNegociado(saidas.stream().mapToDouble(Saida::getVolume).sum());
            rel.setValorTotalRealizado(saidas.stream().mapToDouble(s -> s.getPreco() * s.getVolume()).sum());
            rel.setGanhoTotalRealizado((double) m.getOrDefault("ganhoTotal", 0.0));
            rel.setRoiTotal((double) m.getOrDefault("roi", 0.0));
            rel.setWinRate((double) m.getOrDefault("winRate", 0.0));
            rel.setProfitFactor((double) m.getOrDefault("profitFactor", 0.0));
            rel.setMaxDrawdown((double) m.getOrDefault("maxDrawdown", 0.0));
            rel.setSharpeRatio((double) m.getOrDefault("sharpeRatio", 0.0));
            return rel;
        }
    }

    // ============================================================================
    // PERSISTÊNCIA
    // ============================================================================

    public static class PersistenciaGanhos {
        private final String caminho;
        private final Logger logger = Logger.getLogger(PersistenciaGanhos.class.getName());

        public PersistenciaGanhos(String caminho) {
            this.caminho = caminho;
            new File(caminho).mkdirs();
        }

        public void salvarPosicoes(Map<String, Posicao> posicoes, String nomeArquivo) {
            // Simplified: not implementing full JSON serialization here (would need Jackson/Gson)
            logger.info("Persistência de posições não implementada em Java puro – adicionar biblioteca JSON.");
        }

        public Map<String, Posicao> carregarPosicoes(String nomeArquivo) {
            logger.info("Carregamento de posições não implementado.");
            return new HashMap<>();
        }

        public void salvarSaidas(List<Saida> saidas, String nomeArquivo) {
            logger.info("Persistência de saídas não implementada.");
        }
    }

    // ============================================================================
    // NOTIFICADOR
    // ============================================================================

    public static class Notificador {
        private final Logger logger = Logger.getLogger(Notificador.class.getName());
        private final List<BiConsumer<Saida, Posicao>> callbacks = new ArrayList<>();

        public void registrarCallback(BiConsumer<Saida, Posicao> callback) {
            callbacks.add(callback);
        }

        public void notificarSaida(Saida saida, Posicao posicao) {
            String mensagem = String.format(
                "%s SAÍDA EXECUTADA%nPosição: %s%nSímbolo: %s%nNível: %s%nVolume: %.4f%nPreço: %.4f%nGanho: %.2f%%%nLucro: %.2f%nData: %s",
                saida.getNivel().getIcon(), posicao.getId(), posicao.getSimbolo(),
                saida.getNivel().getLabel(), saida.getVolume(), saida.getPreco(),
                saida.getGanhoPercentual(), saida.getGanhoAbsoluto(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(saida.getTimestamp().atZone(ZoneId.systemDefault()))
            );
            logger.info("\n" + mensagem);
            for (BiConsumer<Saida, Posicao> cb : callbacks) {
                try { cb.accept(saida, posicao); } catch (Exception e) { logger.log(Level.SEVERE, "Erro em callback", e); }
            }
        }
    }

    // ============================================================================
    // MONITOR DE GANHOS
    // ============================================================================

    public static class MonitorGanhos {
        private final ConfiguracaoGanhos config;
        private final GerenciadorGanhos gerenciador;
        private final AnalisadorPerformance analisador;
        private final PersistenciaGanhos persistencia;
        private final Notificador notificador;
        private final Map<String, Posicao> posicoes = new ConcurrentHashMap<>();
        private final List<Saida> saidas = Collections.synchronizedList(new ArrayList<>());
        private final Lock mutex = new ReentrantLock();
        private boolean executando = false;
        private Thread threadMonitoramento;
        private final Logger logger = Logger.getLogger(MonitorGanhos.class.getName());

        public MonitorGanhos(ConfiguracaoGanhos config) {
            this.config = config != null ? config : new ConfiguracaoGanhos();
            this.gerenciador = new GerenciadorGanhos(this.config);
            this.analisador = new AnalisadorPerformance();
            this.persistencia = new PersistenciaGanhos("./dados_ganhos");
            this.notificador = new Notificador();
            logger.info("🚀 Monitor de Ganhos inicializado");
        }

        public String adicionarPosicao(Posicao posicao) {
            mutex.lock();
            try {
                if (posicoes.containsKey(posicao.getId())) throw new IllegalArgumentException("Posição já existe: " + posicao.getId());
                posicoes.put(posicao.getId(), posicao);
                logger.info(String.format("📈 Posição adicionada: %s | Símbolo: %s | Entrada: %.4f | Volume: %.4f",
                        posicao.getId(), posicao.getSimbolo(), posicao.getPrecoEntrada(), posicao.getVolumeOriginal()));
                return posicao.getId();
            } finally {
                mutex.unlock();
            }
        }

        public Saida atualizarPosicao(String idPosicao, double novoPreco) {
            mutex.lock();
            try {
                Posicao posicao = posicoes.get(idPosicao);
                if (posicao == null) {
                    logger.warning("⚠️ Posição não encontrada: " + idPosicao);
                    return null;
                }
                posicao.atualizarPreco(novoPreco);
                gerenciador.atualizarTrailingStop(posicao);
                Saida saida = gerenciador.gerenciarPosicao(posicao);
                if (saida != null) {
                    registrarSaida(saida);
                }
                return saida;
            } finally {
                mutex.unlock();
            }
        }

        public void atualizarPrecoSimbolo(String simbolo, double preco) {
            mutex.lock();
            try {
                for (Posicao p : posicoes.values()) {
                    if (p.getSimbolo().equals(simbolo) && !p.isEstaFechada()) {
                        atualizarPosicao(p.getId(), preco);
                    }
                }
            } finally {
                mutex.unlock();
            }
        }

        private void registrarSaida(Saida saida) {
            saidas.add(saida);
            Posicao posicao = posicoes.get(saida.getIdPosicao());
            if (posicao != null) notificador.notificarSaida(saida, posicao);
        }

        public RelatorioPerformance gerarRelatorio() {
            return analisador.gerarRelatorio(new ArrayList<>(saidas), new ArrayList<>(posicoes.values()));
        }

        public void exportarDados() {
            persistencia.salvarPosicoes(posicoes, "posicoes.json");
            persistencia.salvarSaidas(saidas, "saidas.json");
            logger.info("Dados exportados (simulação).");
        }

        public Posicao getPosicao(String id) { return posicoes.get(id); }
        public List<Posicao> getTodasPosicoes() { return new ArrayList<>(posicoes.values()); }
        public List<Posicao> getPosicoesAbertas() {
            return posicoes.values().stream().filter(p -> !p.isEstaFechada()).toList();
        }
        public List<Saida> getSaidasRecentes(int limite) {
            return saidas.stream().sorted((a,b) -> b.getTimestamp().compareTo(a.getTimestamp())).limit(limite).toList();
        }

        // Thread de monitoramento automático (simulação básica)
        public void iniciarMonitoramentoAutomatico(double intervalo) {
            if (executando) return;
            executando = true;
            threadMonitoramento = new Thread(() -> {
                Random rnd = new Random();
                while (executando) {
                    try {
                        for (Posicao p : getPosicoesAbertas()) {
                            double variacao = rnd.nextGaussian() * 0.01;
                            double novoPreco = p.getPrecoAtual() * (1 + variacao);
                            atualizarPosicao(p.getId(), novoPreco);
                        }
                        Thread.sleep((long)(intervalo * 1000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "Monitoramento");
            threadMonitoramento.setDaemon(true);
            threadMonitoramento.start();
            logger.info("🔄 Monitoramento automático iniciado.");
        }

        public void pararMonitoramentoAutomatico() {
            executando = false;
            if (threadMonitoramento != null) {
                threadMonitoramento.interrupt();
            }
            logger.info("⏹️ Monitoramento automático parado.");
        }
    }

    // ============================================================================
    // MAIN – DEMONSTRAÇÃO
    // ============================================================================

    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("🚀 VHALINOR IAG - GERENCIADOR DE GANHOS QUÂNTICO (Java)");
        System.out.println("=".repeat(90));

        // 1. Configuração
        System.out.println("\n1️⃣  Configurando estratégia...");
        ConfiguracaoGanhos config = ConfiguracaoGanhos.criarEstrategiaProgressiva();
        System.out.println("   ✅ Estratégia: " + config.getNome());
        System.out.printf("   📊 Níveis: %.0f%% → %.0f%% → %.0f%% → %.0f%% → %.0f%% → %.0f%%%n",
                config.getNivel1(), config.getNivel2(), config.getNivel3(),
                config.getNivel4(), config.getNivel5(), config.getNivel6());
        System.out.printf("   📈 Volumes: %.0f%% / %.0f%% / %.0f%% / %.0f%% / %.0f%% / %.0f%%%n",
                config.getVolume1()*100, config.getVolume2()*100, config.getVolume3()*100,
                config.getVolume4()*100, config.getVolume5()*100, config.getVolume6()*100);

        // 2. Inicializa monitor
        System.out.println("\n2️⃣  Inicializando monitor...");
        MonitorGanhos monitor = new MonitorGanhos(config);

        // 3. Adicionar posições de exemplo
        System.out.println("\n3️⃣  Adicionando posições de exemplo...");
        monitor.adicionarPosicao(new Posicao("CRIPTO-001", "BTC/USD", TipoAtivo.CRIPTOMOEDA, 50000.0, 1.0));
        monitor.adicionarPosicao(new Posicao("CRIPTO-002", "ETH/USD", TipoAtivo.CRIPTOMOEDA, 3000.0, 10.0));
        monitor.adicionarPosicao(new Posicao("ACAO-001", "PETR4.SA", TipoAtivo.ACAO, 40.0, 1000));

        // 4. Simular evolução de preços
        System.out.println("\n4️⃣  Simulando evolução de preços...");
        double[] precosBTC = {50000, 90000, 100000, 150000, 200000, 250000, 300000, 350000, 400000, 450000, 500000, 550000};
        for (double preco : precosBTC) {
            monitor.atualizarPosicao("CRIPTO-001", preco);
            System.out.printf("   📊 BTC/USD: $%,.0f | Ganho: %.1f%%%n", preco, ((preco-50000)/50000)*100);
        }
        double[] precosETH = {3000, 5400, 6000, 9000, 12000, 15000, 18000};
        for (double preco : precosETH) {
            monitor.atualizarPosicao("CRIPTO-002", preco);
            System.out.printf("   📊 ETH/USD: $%,.0f | Ganho: %.1f%%%n", preco, ((preco-3000)/3000)*100);
        }
        double[] precosPETR4 = {40, 72, 80, 120};
        for (double preco : precosPETR4) {
            monitor.atualizarPosicao("ACAO-001", preco);
            System.out.printf("   📊 PETR4.SA: R$%.2f | Ganho: %.1f%%%n", preco, ((preco-40)/40)*100);
        }

        // 5. Relatório
        System.out.println("\n5️⃣  Gerando relatório de performance...");
        RelatorioPerformance rel = monitor.gerarRelatorio();
        System.out.println("\n" + "=".repeat(90));
        System.out.println("📊 RELATÓRIO DE PERFORMANCE");
        System.out.println("=".repeat(90));
        System.out.printf("%n📈 POSIÇÕES:%n   • Total: %d%n   • Fechadas: %d%n   • Abertas: %d%n",
                rel.getTotalPosicoes(), rel.getPosicoesFechadas(), rel.getPosicoesAbertas());
        System.out.printf("%n💰 SAÍDAS:%n   • Total: %d%n   • Por nível:%n", rel.getTotalSaidas());
        rel.getSaidasPorNivel().forEach((nivel, qtd) -> System.out.printf("     - %s: %d%n", nivel, qtd));
        System.out.printf("%n📊 MÉTRICAS:%n   • ROI: %.2f%%%n   • Win Rate: %.2f%%%n   • Profit Factor: %.2f%n   • Max Drawdown: %.2f%%%n   • Sharpe Ratio: %.2f%n",
                rel.getRoiTotal(), rel.getWinRate(), rel.getProfitFactor(), rel.getMaxDrawdown(), rel.getSharpeRatio());
        System.out.printf("%n💵 RESULTADOS:%n   • Ganho Realizado: $%,.2f%n   • Volume Negociado: %.4f%n   • Valor Realizado: $%,.2f%n",
                rel.getGanhoTotalRealizado(), rel.getVolumeTotalNegociado(), rel.getValorTotalRealizado());

        // 6. Exportar
        System.out.println("\n6️⃣  Exportando dados...");
        monitor.exportarDados();
        System.out.println("\n" + "=".repeat(90));
        System.out.println("✅ DEMONSTRAÇÃO CONCLUÍDA COM SUCESSO!");
        System.out.println("=".repeat(90));
    }
}
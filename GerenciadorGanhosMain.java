import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

/**
 * VHALINOR IAG – GERENCIADOR DE GANHOS QUÂNTICO (Java Port)
 * Sistema de gerenciamento de posições com níveis exponenciais.
 */
public class GerenciadorGanhosMain {

    // ==================== ENUMS ====================

    enum NivelGanho {
        INICIAL("80%", "🟢", 80.0, "Nível Inicial - Recuperação de Capital"),
        INTERMEDIARIO("200%", "🔵", 200.0, "Nível Intermediário - Lucro Significativo"),
        AVANCADO("500%", "🟠", 500.0, "Nível Avançado - Performance Superior"),
        EXCEPCIONAL("1000%", "🔴", 1000.0, "Nível Excepcional - Retorno Extraordinário"),
        MITICO("2000%", "💎", 2000.0, "Nível Mítico - Raro"),
        LENDARIO("5000%", "👑", 5000.0, "Nível Lendário - Extremamente Raro");

        final String label, icon, descricao;
        final double valor;
        NivelGanho(String l, String i, double v, String d) { label = l; icon = i; valor = v; descricao = d; }
    }

    enum TipoAtivo {
        CRIPTOMOEDA("Criptomoeda", "₿", "alta_volatilidade"),
        ACAO("Ação", "📈", "media_volatilidade"),
        ETF("ETF", "📊", "media_volatilidade"),
        FUTURO("Futuro", "⚡", "alta_volatilidade"),
        OPCAO("Opção", "🎲", "muito_alta_volatilidade"),
        FOREX("Forex", "💱", "baixa_volatilidade"),
        COMMODITY("Commodity", "🛢️", "media_volatilidade"),
        RENDA_FIXA("Renda Fixa", "🏦", "baixissima_volatilidade");

        final String label, icon, perfilRisco;
        TipoAtivo(String l, String i, String p) { label = l; icon = i; perfilRisco = p; }
    }

    enum EstrategiaSaida {
        PROGRESSIVA, REGRESSIVA, EQUITATIVA, CONSERVADORA, AGRESSIVA, TRAILING_STOP, PARCIAL_TOTAL
    }

    enum EstadoPosicao {
        ABERTA("Aberta", "verde"), PARCIALMENTE_VENDIDA("Parcial", "amarelo"),
        FECHADA("Fechada", "vermelho"), AGUARDANDO("Aguardando", "laranja"),
        CANCELADA("Cancelada", "preto"), ERRO("Erro", "erro");
        final String label, cor;
        EstadoPosicao(String l, String c) { label = l; cor = c; }
    }

    enum MetricaChave {
        ROI, SHARPE, SORTINO, WIN_RATE, PROFIT_FACTOR, MAX_DRAWDOWN, RECOVERY_FACTOR, EXPECTANCIA
    }

    // ==================== CLASSES DE DADOS ====================

    static class ConfiguracaoGanhos {
        // Níveis de saída (%)
        double nivel1 = 80, nivel2 = 200, nivel3 = 500, nivel4 = 1000, nivel5 = 2000, nivel6 = 5000;
        // Volumes por nível (devem somar 1.0)
        double vol1 = 0.15, vol2 = 0.15, vol3 = 0.15, vol4 = 0.15, vol5 = 0.20, vol6 = 0.20;
        EstrategiaSaida estrategia = EstrategiaSaida.PROGRESSIVA;
        boolean trailingStopAtivado = false;
        double trailingStopDistancia = 10.0; // percentual
        boolean reentradaPermitida = false;
        double reentradaNivelMinimo = 50.0;
        String nome = "Configuração Padrão";
        String descricao = "Configuração com 6 níveis de ganho progressivos";
        LocalDateTime criadoEm = LocalDateTime.now();

        // Validação após construção
        void validar() {
            double soma = vol1 + vol2 + vol3 + vol4 + vol5 + vol6;
            if (Math.abs(soma - 1.0) > 0.0001)
                throw new IllegalArgumentException("Volumes devem somar 1.0. Atual: " + soma);
            double[] niveis = {nivel1, nivel2, nivel3, nivel4, nivel5, nivel6};
            for (int i = 1; i < niveis.length; i++)
                if (niveis[i] <= niveis[i-1])
                    throw new IllegalArgumentException("Níveis devem ser crescentes");
            double[] vols = {vol1, vol2, vol3, vol4, vol5, vol6};
            for (double v : vols) if (v < 0) throw new IllegalArgumentException("Volume negativo");
        }

        double obterVolumePorNivel(int nivel) {
            switch (nivel) {
                case 1: return vol1;
                case 2: return vol2;
                case 3: return vol3;
                case 4: return vol4;
                case 5: return vol5;
                case 6: return vol6;
                default: return 0;
            }
        }

        // Fábricas de estratégias
        static ConfiguracaoGanhos criarProgressiva() {
            ConfiguracaoGanhos c = new ConfiguracaoGanhos();
            c.vol1 = 0.10; c.vol2 = 0.12; c.vol3 = 0.15; c.vol4 = 0.18; c.vol5 = 0.20; c.vol6 = 0.25;
            c.estrategia = EstrategiaSaida.PROGRESSIVA;
            c.nome = "Estratégia Progressiva";
            c.descricao = "Volumes crescentes conforme níveis mais altos";
            return c;
        }
        static ConfiguracaoGanhos criarConservadora() {
            ConfiguracaoGanhos c = new ConfiguracaoGanhos();
            c.vol1 = 0.25; c.vol2 = 0.20; c.vol3 = 0.15; c.vol4 = 0.15; c.vol5 = 0.15; c.vol6 = 0.10;
            c.estrategia = EstrategiaSaida.CONSERVADORA;
            c.nome = "Estratégia Conservadora";
            c.descricao = "Maior volume nos níveis iniciais para garantir lucro";
            return c;
        }
        static ConfiguracaoGanhos criarAgressiva() {
            ConfiguracaoGanhos c = new ConfiguracaoGanhos();
            c.vol1 = 0.05; c.vol2 = 0.08; c.vol3 = 0.12; c.vol4 = 0.15; c.vol5 = 0.25; c.vol6 = 0.35;
            c.estrategia = EstrategiaSaida.AGRESSIVA;
            c.nome = "Estratégia Agressiva";
            c.descricao = "Maior volume nos níveis mais altos para máximo ganho";
            return c;
        }
        static ConfiguracaoGanhos criarEquitativa() {
            ConfiguracaoGanhos c = new ConfiguracaoGanhos();
            double v = 1.0 / 6;
            c.vol1 = v; c.vol2 = v; c.vol3 = v; c.vol4 = v; c.vol5 = v; c.vol6 = v;
            c.estrategia = EstrategiaSaida.EQUITATIVA;
            c.nome = "Estratégia Equitativa";
            c.descricao = "Volumes iguais em todos os níveis";
            return c;
        }
    }

    static class Posicao {
        String id, simbolo;
        TipoAtivo tipoAtivo;
        double precoEntrada, volumeOriginal, precoAtual;
        LocalDateTime dataEntrada = LocalDateTime.now(), dataAtualizacao;
        EstadoPosicao estado = EstadoPosicao.ABERTA;
        String configuracoesId;
        // Controle de saídas
        boolean[] nivelExecutado = new boolean[7]; // índices 1..6
        double volumeVendido, valorRealizado;
        Double trailingStopPreco, trailingStopMaximo;
        List<String> tags = new ArrayList<>();
        String notas = "";
        Map<String, Object> metadados = new HashMap<>();

        Posicao(String id, String simb, TipoAtivo tipo, double precoEntrada, double volume) {
            this.id = id;
            this.simbolo = simb;
            this.tipoAtivo = tipo;
            this.precoEntrada = precoEntrada;
            this.volumeOriginal = volume;
            this.precoAtual = precoEntrada;
        }

        double getGanhoPercentual() {
            if (precoEntrada == 0) return 0;
            return ((precoAtual - precoEntrada) / precoEntrada) * 100;
        }

        double getVolumeRestante() {
            return volumeOriginal - volumeVendido;
        }

        boolean estaFechada() {
            return volumeVendido >= volumeOriginal;
        }

        double getPercentualVendido() {
            return volumeOriginal == 0 ? 0 : (volumeVendido / volumeOriginal) * 100;
        }

        Map<String, Object> venderParcial(double volume, double preco) {
            double volVenda = Math.min(volume, getVolumeRestante());
            if (volVenda <= 0) return Map.of("sucesso", false, "erro", "Volume de venda inválido");
            volumeVendido += volVenda;
            valorRealizado += preco * volVenda;
            double custo = precoEntrada * volVenda;
            double resultado = (preco - precoEntrada) * volVenda;
            double resultadoPct = (preco - precoEntrada) / precoEntrada * 100;

            if (estaFechada()) estado = EstadoPosicao.FECHADA;
            else estado = EstadoPosicao.PARCIALMENTE_VENDIDA;

            Map<String, Object> res = new HashMap<>();
            res.put("sucesso", true);
            res.put("volume_vendido", volVenda);
            res.put("preco_venda", preco);
            res.put("resultado", resultado);
            res.put("resultado_percentual", resultadoPct);
            res.put("volume_restante", getVolumeRestante());
            return res;
        }

        void atualizarPreco(double novoPreco) {
            this.precoAtual = novoPreco;
            this.dataAtualizacao = LocalDateTime.now();
        }

        Map<String, Object> toMap() {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", id);
            m.put("simbolo", simbolo);
            m.put("tipo_ativo", tipoAtivo.label);
            m.put("preco_entrada", precoEntrada);
            m.put("volume_original", volumeOriginal);
            m.put("preco_atual", precoAtual);
            m.put("ganho_percentual", getGanhoPercentual());
            m.put("estado", estado.label);
            m.put("volume_vendido", volumeVendido);
            m.put("volume_restante", getVolumeRestante());
            return m;
        }
    }

    static class Saida {
        String id, idPosicao;
        NivelGanho nivel;
        double volume, preco, ganhoPercentual, ganhoAbsoluto;
        LocalDateTime timestamp = LocalDateTime.now();
        double precoEntradaReferencia, volumeRestanteApos, percentualPosicao;

        Saida(String id, String idPos, NivelGanho nivel, double vol, double preco, double ganhoPct, double ganhoAbs) {
            this.id = id; this.idPosicao = idPos; this.nivel = nivel;
            this.volume = vol; this.preco = preco; this.ganhoPercentual = ganhoPct; this.ganhoAbsoluto = ganhoAbs;
        }

        Map<String, Object> toMap() {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", id);
            m.put("id_posicao", idPosicao);
            m.put("nivel", nivel.label);
            m.put("nivel_icon", nivel.icon);
            m.put("volume", volume);
            m.put("preco", preco);
            m.put("ganho_percentual", ganhoPercentual);
            m.put("ganho_absoluto", ganhoAbsoluto);
            m.put("timestamp", timestamp.toString());
            return m;
        }
    }

    static class RelatorioPerformance {
        LocalDateTime periodoInicio, periodoFim;
        long totalPosicoes, posicoesFechadas, posicoesAbertas, totalSaidas;
        Map<String, Long> saidasPorNivel = new HashMap<>();
        double volumeTotalNegociado, valorTotalRealizado, ganhoTotalRealizado, ganhoNaoRealizado;
        double roiTotal, winRate, profitFactor, maxDrawdown, sharpeRatio, sortinoRatio, calmarRatio, expectancy;
    }

    // ==================== GERENCIADOR DE GANHOS ====================
    static class GerenciadorGanhos {
        private final ConfiguracaoGanhos config;
        private final Logger log = Logger.getLogger("GerenciadorGanhos");

        GerenciadorGanhos(ConfiguracaoGanhos config) {
            this.config = config;
            config.validar();
            log.info("✅ Gerenciador de Ganhos inicializado: " + config.nome);
        }

        double calcularGanhoPercentual(double precoEntrada, double precoAtual) {
            if (precoEntrada == 0) return 0;
            return ((precoAtual - precoEntrada) / precoEntrada) * 100;
        }

        List<Integer> verificarNiveisAtingidos(Posicao posicao) {
            List<Integer> atingidos = new ArrayList<>();
            double ganho = posicao.getGanhoPercentual();
            double[] limites = {config.nivel1, config.nivel2, config.nivel3, config.nivel4, config.nivel5, config.nivel6};
            boolean[] exec = posicao.nivelExecutado;
            for (int i = 0; i < 6; i++) {
                if (ganho >= limites[i] && !exec[i+1]) atingidos.add(i+1);
            }
            return atingidos;
        }

        Optional<Saida> gerenciarPosicao(Posicao posicao) {
            // Trailing stop
            if (config.trailingStopAtivado && posicao.trailingStopPreco != null && posicao.precoAtual <= posicao.trailingStopPreco) {
                return Optional.of(executarTrailingStop(posicao));
            }
            List<Integer> niveis = verificarNiveisAtingidos(posicao);
            if (niveis.isEmpty()) return Optional.empty();
            int nivel = niveis.get(0); // menor nível atingido
            return Optional.of(executarSaida(posicao, nivel));
        }

        Saida executarSaida(Posicao posicao, int nivel) {
            double volAlvo = posicao.volumeOriginal * config.obterVolumePorNivel(nivel);
            double volVenda = Math.min(volAlvo, posicao.getVolumeRestante());
            if (volVenda <= 0) throw new IllegalStateException("Volume de venda <= 0 para nível " + nivel);

            Map<String, Object> res = posicao.venderParcial(volVenda, posicao.precoAtual);
            if (!(boolean)res.get("sucesso")) throw new RuntimeException("Falha na venda");

            posicao.nivelExecutado[nivel] = true;

            NivelGanho nivelEnum = switch (nivel) {
                case 1 -> NivelGanho.INICIAL;
                case 2 -> NivelGanho.INTERMEDIARIO;
                case 3 -> NivelGanho.AVANCADO;
                case 4 -> NivelGanho.EXCEPCIONAL;
                case 5 -> NivelGanho.MITICO;
                case 6 -> NivelGanho.LENDARIO;
                default -> NivelGanho.INICIAL;
            };

            Saida saida = new Saida("S" + UUID.randomUUID().toString().substring(0,8),
                                   posicao.id, nivelEnum, volVenda, posicao.precoAtual,
                                   (double)res.get("resultado_percentual"), (double)res.get("resultado"));
            saida.precoEntradaReferencia = posicao.precoEntrada;
            saida.volumeRestanteApos = posicao.getVolumeRestante();
            saida.percentualPosicao = (volVenda / posicao.volumeOriginal) * 100;

            log.info(String.format("%s Saída Nível %d (%s): %.4f @ %.4f | Ganho: %.2f%% | Vol resto: %.4f",
                    nivelEnum.icon, nivel, nivelEnum.label, volVenda, posicao.precoAtual,
                    saida.ganhoPercentual, posicao.getVolumeRestante()));
            return saida;
        }

        Saida executarTrailingStop(Posicao posicao) {
            double volVenda = posicao.getVolumeRestante();
            Map<String, Object> res = posicao.venderParcial(volVenda, posicao.trailingStopPreco);
            Saida saida = new Saida("TS" + UUID.randomUUID().toString().substring(0,8),
                                    posicao.id, NivelGanho.INICIAL, volVenda, posicao.trailingStopPreco,
                                    (double)res.get("resultado_percentual"), (double)res.get("resultado"));
            log.warning(String.format("⚠️ Trailing Stop: %.4f @ %.4f | Ganho: %.2f%%", volVenda, posicao.trailingStopPreco, saida.ganhoPercentual));
            return saida;
        }

        void atualizarTrailingStop(Posicao posicao) {
            if (!config.trailingStopAtivado) return;
            if (posicao.trailingStopMaximo == null || posicao.precoAtual > posicao.trailingStopMaximo) {
                posicao.trailingStopMaximo = posicao.precoAtual;
                posicao.trailingStopPreco = posicao.trailingStopMaximo * (1 - config.trailingStopDistancia / 100.0);
            }
        }
    }

    // ==================== ANALISADOR DE PERFORMANCE ====================
    static class AnalisadorPerformance {
        Map<String, Object> calcularMetricas(List<Saida> saidas, List<Posicao> posicoes) {
            Map<String, Object> m = new HashMap<>();
            if (saidas.isEmpty()) return m;

            long totalSaidas = saidas.size();
            m.put("total_saidas", totalSaidas);
            m.put("total_posicoes", posicoes.size());
            long fechadas = posicoes.stream().filter(Posicao::estaFechada).count();
            m.put("posicoes_fechadas", fechadas);

            // Ganho
            double ganhoTotal = saidas.stream().mapToDouble(s -> s.ganhoAbsoluto).sum();
            double ganhoMedio = ganhoTotal / totalSaidas;
            double ganhoMax = saidas.stream().mapToDouble(s -> s.ganhoAbsoluto).max().orElse(0);
            double ganhoMin = saidas.stream().mapToDouble(s -> s.ganhoAbsoluto).min().orElse(0);
            m.put("ganho_total", ganhoTotal);
            m.put("ganho_medio", ganhoMedio);
            m.put("ganho_maximo", ganhoMax);
            m.put("ganho_minimo", ganhoMin);

            // Distribuição por nível
            Map<String, Long> porNivel = saidas.stream().collect(Collectors.groupingBy(s -> s.nivel.label, Collectors.counting()));
            m.put("distribuicao_niveis", porNivel);

            // Win rate
            long ganhadoras = saidas.stream().filter(s -> s.ganhoAbsoluto > 0).count();
            double winRate = (totalSaidas > 0) ? (double)ganhadoras / totalSaidas * 100 : 0;
            m.put("win_rate", winRate);

            // Profit factor
            double somaGanhos = saidas.stream().filter(s -> s.ganhoAbsoluto > 0).mapToDouble(s -> s.ganhoAbsoluto).sum();
            double somaPerdas = Math.abs(saidas.stream().filter(s -> s.ganhoAbsoluto < 0).mapToDouble(s -> s.ganhoAbsoluto).sum());
            m.put("profit_factor", somaPerdas > 0 ? somaGanhos / somaPerdas : Double.POSITIVE_INFINITY);

            // ROI
            double capitalTotal = posicoes.stream().mapToDouble(p -> p.precoEntrada * p.volumeOriginal).sum();
            double roi = capitalTotal > 0 ? (ganhoTotal / capitalTotal) * 100 : 0;
            m.put("roi", roi);

            m.put("max_drawdown", 0.0); // Simplificado (necessita histórico cumulativo)
            m.put("sharpe_ratio", 0.0);
            return m;
        }

        RelatorioPerformance gerarRelatorio(List<Saida> saidas, List<Posicao> posicoes) {
            Map<String, Object> met = calcularMetricas(saidas, posicoes);
            RelatorioPerformance r = new RelatorioPerformance();
            r.periodoInicio = LocalDateTime.now().minusDays(30);
            r.periodoFim = LocalDateTime.now();
            r.totalPosicoes = posicoes.size();
            r.posicoesFechadas = (long) met.getOrDefault("posicoes_fechadas", 0L);
            r.posicoesAbertas = r.totalPosicoes - r.posicoesFechadas;
            r.totalSaidas = (long) met.getOrDefault("total_saidas", 0L);
            r.saidasPorNivel = (Map<String, Long>) met.getOrDefault("distribuicao_niveis", Collections.emptyMap());
            r.volumeTotalNegociado = saidas.stream().mapToDouble(s -> s.volume).sum();
            r.valorTotalRealizado = saidas.stream().mapToDouble(s -> s.preco * s.volume).sum();
            r.ganhoTotalRealizado = (double) met.getOrDefault("ganho_total", 0.0);
            r.roiTotal = (double) met.getOrDefault("roi", 0.0);
            r.winRate = (double) met.getOrDefault("win_rate", 0.0);
            r.profitFactor = (double) met.getOrDefault("profit_factor", 0.0);
            r.maxDrawdown = (double) met.getOrDefault("max_drawdown", 0.0);
            r.sharpeRatio = (double) met.getOrDefault("sharpe_ratio", 0.0);
            return r;
        }
    }

    // ==================== PERSISTÊNCIA ====================
    static class PersistenciaGanhos {
        private final Path diretorio;

        PersistenciaGanhos(String caminho) {
            diretorio = Path.of(caminho);
            try { Files.createDirectories(diretorio); } catch (IOException e) { e.printStackTrace(); }
        }

        void salvarPosicoes(Map<String, Posicao> posicoes, String arquivo) {
            try (Writer w = Files.newBufferedWriter(diretorio.resolve(arquivo))) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("timestamp", LocalDateTime.now().toString());
                doc.put("posicoes", posicoes.values().stream().map(Posicao::toMap).collect(Collectors.toList()));
                // Simples JSON writer manual (evitar dependência)
                w.write(toJson(doc));
            } catch (IOException e) { e.printStackTrace(); }
        }

        void salvarSaidas(List<Saida> saidas, String arquivo) {
            try (Writer w = Files.newBufferedWriter(diretorio.resolve(arquivo))) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("timestamp", LocalDateTime.now().toString());
                doc.put("saidas", saidas.stream().map(Saida::toMap).collect(Collectors.toList()));
                w.write(toJson(doc));
            } catch (IOException e) { e.printStackTrace(); }
        }

        // toJson muito simplificado – em produção use Jackson/Gson
        private static String toJson(Map<String, Object> map) {
            StringBuilder sb = new StringBuilder("{");
            for (var e : map.entrySet()) {
                sb.append("\"").append(e.getKey()).append("\": ");
                Object val = e.getValue();
                if (val instanceof String) sb.append("\"").append(val).append("\"");
                else if (val instanceof List) {
                    sb.append("[");
                    List<?> list = (List<?>) val;
                    for (int i = 0; i < list.size(); i++) {
                        Object item = list.get(i);
                        if (item instanceof Map) sb.append(toJson((Map<String, Object>) item));
                        else sb.append(item);
                        if (i < list.size()-1) sb.append(", ");
                    }
                    sb.append("]");
                } else sb.append(val);
                sb.append(", ");
            }
            if (sb.length() > 2) sb.setLength(sb.length()-2);
            sb.append("}");
            return sb.toString();
        }
    }

    // ==================== NOTIFICADOR ====================
    static class Notificador {
        private final List<BiConsumer<Saida, Posicao>> callbacks = new ArrayList<>();
        private final Logger log = Logger.getLogger("Notificador");

        void registrarCallback(BiConsumer<Saida, Posicao> cb) { callbacks.add(cb); }

        void notificarSaida(Saida saida, Posicao posicao) {
            String msg = String.format("%s SAÍDA: %s (%s) %.4f @ %.4f Ganho %.2f%%", saida.nivel.icon,
                    saida.idPosicao, posicao.simbolo, saida.volume, saida.preco, saida.ganhoPercentual);
            log.info(msg);
            callbacks.forEach(cb -> cb.accept(saida, posicao));
        }
    }

    // ==================== MONITOR DE GANHOS (SISTEMA PRINCIPAL) ====================
    static class MonitorGanhos {
        private final ConfiguracaoGanhos config;
        private final GerenciadorGanhos gerenciador;
        private final AnalisadorPerformance analisador = new AnalisadorPerformance();
        private final PersistenciaGanhos persistencia = new PersistenciaGanhos("./dados_ganhos");
        private final Notificador notificador = new Notificador();

        private final Map<String, Posicao> posicoes = new ConcurrentHashMap<>();
        private final List<Saida> saidas = new CopyOnWriteArrayList<>();
        private final Map<String, Deque<Map<String, Object>>> historicoPrecos = new ConcurrentHashMap<>();

        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        private volatile boolean executando = false;

        private static final Logger LOG = Logger.getLogger("MonitorGanhos");

        MonitorGanhos(ConfiguracaoGanhos cfg) {
            this.config = cfg;
            this.gerenciador = new GerenciadorGanhos(cfg);
            LOG.info("🚀 Monitor de Ganhos inicializado");
        }

        String adicionarPosicao(Map<String, Object> dadosPosicao) {
            String id = (String) dadosPosicao.getOrDefault("id", UUID.randomUUID().toString().substring(0,8));
            Posicao p = new Posicao(id,
                    (String) dadosPosicao.get("simbolo"),
                    (TipoAtivo) dadosPosicao.getOrDefault("tipo_ativo", TipoAtivo.CRIPTOMOEDA),
                    (double) dadosPosicao.get("preco_entrada"),
                    (double) dadosPosicao.get("volume"));
            posicoes.put(id, p);
            LOG.info(() -> String.format("📈 Posição adicionada: %s | %s @ %.4f x %.4f", p.id, p.simbolo, p.precoEntrada, p.volumeOriginal));
            return id;
        }

        Optional<Saida> atualizarPosicao(String idPosicao, Map<String, Object> dados) {
            Posicao p = posicoes.get(idPosicao);
            if (p == null) { LOG.warning("Posição não encontrada: " + idPosicao); return Optional.empty(); }
            if (dados.containsKey("preco_atual")) {
                double preco = (double) dados.get("preco_atual");
                p.atualizarPreco(preco);
                // Atualiza trailing stop
                gerenciador.atualizarTrailingStop(p);
            }
            Optional<Saida> saidaOpt = gerenciador.gerenciarPosicao(p);
            saidaOpt.ifPresent(this::registrarSaida);
            return saidaOpt;
        }

        void atualizarPrecoSimbolo(String simbolo, double preco) {
            posicoes.values().stream()
                    .filter(p -> p.simbolo.equals(simbolo) && !p.estaFechada())
                    .forEach(p -> atualizarPosicao(p.id, Map.of("preco_atual", preco)));
        }

        void registrarSaida(Saida saida) {
            saidas.add(saida);
            Posicao p = posicoes.get(saida.idPosicao);
            if (p != null) notificador.notificarSaida(saida, p);
        }

        void removerPosicao(String idPosicao) {
            posicoes.remove(idPosicao);
            LOG.info("🗑️ Posição removida: " + idPosicao);
        }

        List<Posicao> getPosicoesAbertas() {
            return posicoes.values().stream().filter(p -> !p.estaFechada()).collect(Collectors.toList());
        }

        RelatorioPerformance gerarRelatorio() {
            return analisador.gerarRelatorio(saidas, new ArrayList<>(posicoes.values()));
        }

        Map<String, Object> exportarDados() {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            persistencia.salvarPosicoes(posicoes, "posicoes_" + ts + ".json");
            persistencia.salvarSaidas(saidas, "saidas_" + ts + ".json");
            RelatorioPerformance rel = gerarRelatorio();
            LOG.info("📊 Relatório exportado: relatorio_"+ts+".json");
            return Map.of("relatorio", rel); // simplificado
        }

        void iniciarMonitoramentoAutomatico(long intervaloMs) {
            if (executando) return;
            executando = true;
            scheduler.scheduleAtFixedRate(this::loopMonitoramento, 0, intervaloMs, TimeUnit.MILLISECONDS);
            LOG.info("🔄 Monitoramento automático iniciado");
        }

        void pararMonitoramento() {
            executando = false;
            scheduler.shutdown();
            LOG.info("⏹️ Monitoramento automático parado");
        }

        private void loopMonitoramento() {
            try {
                getPosicoesAbertas().forEach(p -> {
                    // Simula variação de preço (substituir por API real)
                    double variacao = (Math.random() - 0.5) * 0.02;
                    double novoPreco = p.precoAtual * (1 + variacao);
                    atualizarPosicao(p.id, Map.of("preco_atual", novoPreco));
                });
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Erro no monitoramento automático", e);
            }
        }
    }

    // ==================== DEMONSTRAÇÃO ====================
    public static void main(String[] args) {
        ConfiguracaoGanhos cfg = ConfiguracaoGanhos.criarProgressiva();
        MonitorGanhos monitor = new MonitorGanhos(cfg);

        // Adicionar posições exemplo
        monitor.adicionarPosicao(Map.of(
            "id", "BTC-001", "simbolo", "BTC/USD", "tipo_ativo", TipoAtivo.CRIPTOMOEDA,
            "preco_entrada", 50000.0, "volume", 1.0
        ));
        monitor.adicionarPosicao(Map.of(
            "id", "ETH-001", "simbolo", "ETH/USD", "tipo_ativo", TipoAtivo.CRIPTOMOEDA,
            "preco_entrada", 3000.0, "volume", 10.0
        ));

        // Simulação rápida
        System.out.println("\n--- Simulação de preços BTC ---");
        double[] precosBTC = {50000, 90000, 100000, 150000, 200000, 250000, 300000, 350000, 400000, 450000, 500000, 550000};
        for (double p : precosBTC) {
            var saida = monitor.atualizarPosicao("BTC-001", Map.of("preco_atual", p));
            System.out.printf("BTC: %.0f (ganho %.0f%%) %s%n", p, ((p-50000)/50000)*100, saida.map(s -> "→ SAÍDA nível "+s.nivel.label).orElse(""));
        }
        System.out.println("\n--- Simulação de preços ETH ---");
        double[] precosETH = {3000, 5400, 6000, 9000, 12000, 15000, 18000};
        for (double p : precosETH) {
            var saida = monitor.atualizarPosicao("ETH-001", Map.of("preco_atual", p));
            System.out.printf("ETH: %.0f (ganho %.0f%%) %s%n", p, ((p-3000)/3000)*100, saida.map(s -> "→ SAÍDA nível "+s.nivel.label).orElse(""));
        }

        System.out.println("\nRelatório:");
        RelatorioPerformance rel = monitor.gerarRelatorio();
        System.out.printf("Posições: %d (fechadas: %d) | Saídas: %d | ROI: %.2f%% | WinRate: %.2f%%%n",
                rel.totalPosicoes, rel.posicoesFechadas, rel.totalSaidas, rel.roiTotal, rel.winRate);
    }
}
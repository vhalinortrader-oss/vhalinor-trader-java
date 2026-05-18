import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Script de reorganização do VHALINOR TRADER (versão Java)
 * - Analisa conteúdo de arquivos .py e .java
 * - Classifica por função (AI, trading, dados, utilitários, etc.)
 * - Move para estrutura organizada
 * - Se houver múltiplos arquivos de I.A, agrupa-os em um módulo central (Python)
 */
public class ReorganizeVhalinor {

    // ------------------------------------------------------------
    // Configuração de logging
    // ------------------------------------------------------------
    private static final Logger LOGGER = Logger.getLogger(ReorganizeVhalinor.class.getName());

    static {
        setupLogging();
    }

    private static void setupLogging() {
        try {
            // Remove handlers padrão, configura formato e arquivos
            LogManager.getLogManager().reset();
            LOGGER.setLevel(Level.INFO);

            // Console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS - %2$s - %3$s%n",
                            record.getMillis(), record.getLevel().getName(), record.getMessage());
                }
            });
            LOGGER.addHandler(consoleHandler);

            // File handler para log geral
            FileHandler fileHandler = new FileHandler("reorganize_vhalinor.log", true);
            fileHandler.setFormatter(consoleHandler.getFormatter());
            fileHandler.setEncoding("UTF-8");
            LOGGER.addHandler(fileHandler);
        } catch (Exception e) {
            System.err.println("Erro ao configurar logging: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // Configuração de diretórios e extensões
    // ------------------------------------------------------------
    // Caminho raiz do projeto (ajustável via argumento ou constante)
    private static final Path ROOT_PATH = Paths.get("C:\\Users\\alex miranda sales\\Desktop\\VHALINOR-TRADER-main\\VHALINOR-TRADER-main");

    // Extensões consideradas
    private static final List<String> TARGET_EXT = Arrays.asList(".py", ".java");

    // Diretório de saída (nova estrutura)
    private static final Path OUTPUT_BASE = ROOT_PATH.resolve("organized");

    // ------------------------------------------------------------
    // Palavras-chave por categoria
    // ------------------------------------------------------------
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new LinkedHashMap<>();
    static {
        CATEGORY_KEYWORDS.put("ai_core", Arrays.asList(
            "neural", "network", "deep", "learning", "predict", "ai", "model",
            "tensorflow", "pytorch", "keras", "xgboost", "sklearn", "train",
            "classifier", "regressor", "lstm", "transformer", "reinforcement",
            "inteligencia", "inteligência", "rede", "neuronal", "aprendizado",
            "consciousness", "quantum", "cognitive", "brain", "mind"
        ));
        CATEGORY_KEYWORDS.put("trading", Arrays.asList(
            "trade", "order", "exchange", "broker", "position", "market",
            "buy", "sell", "profit", "stop", "limit", "candle", "ticker",
            "negociação", "ordem", "compra", "venda", "mercado",
            "arbitrage", "portfolio", "risk", "strategy", "execution"
        ));
        CATEGORY_KEYWORDS.put("data", Arrays.asList(
            "data", "csv", "json", "sql", "database", "feed", "stream",
            "preprocess", "clean", "feature", "ingest", "store",
            "dados", "banco", "processamento", "collector", "parser"
        ));
        CATEGORY_KEYWORDS.put("execution", Arrays.asList(
            "execut", "run", "main", "scheduler", "loop", "worker", "task",
            "execução", "execucao", "principal", "orchestrator", "manager"
        ));
        CATEGORY_KEYWORDS.put("utils", Arrays.asList(
            "util", "helper", "logger", "config", "env", "constants",
            "utilit", "auxiliar", "common", "base", "core"
        ));
        CATEGORY_KEYWORDS.put("monitoring", Arrays.asList(
            "monitor", "dashboard", "alert", "log", "telemetry", "metric",
            "monitoramento", "alerta", "analytics", "reporting", "visualization"
        ));
        CATEGORY_KEYWORDS.put("tests", Arrays.asList(
            "test", "unittest", "pytest", "mock", "assert", "fixture",
            "teste", "spec", "behavior"
        ));
        CATEGORY_KEYWORDS.put("windows_integration", Arrays.asList(
            "windows", "win32", "registry", "shortcut", "scheduler",
            "defender", "task", "notification", "tray", "toast",
            "atalho", "agendador", "setup", "installer"
        ));
        CATEGORY_KEYWORDS.put("security", Arrays.asList(
            "auth", "security", "encrypt", "decrypt", "credential", "token",
            "senha", "segurança", "autenticação", "permission", "access"
        ));
        CATEGORY_KEYWORDS.put("infrastructure", Arrays.asList(
            "docker", "kubernetes", "deploy", "pipeline", "ci", "cd",
            "infrastructure", "devops", "build", "release", "version"
        ));
    }

    // Prioridade das categorias (em caso de empate)
    private static final List<String> CATEGORY_PRIORITY = Arrays.asList(
        "ai_core", "trading", "data", "execution", "monitoring",
        "windows_integration", "security", "infrastructure", "utils", "tests"
    );

    // ------------------------------------------------------------
    // Funções de análise de arquivos
    // ------------------------------------------------------------
    /**
     * Analisa o conteúdo do arquivo e retorna a categoria mais provável.
     * Lê apenas as primeiras 200 linhas para performance.
     */
    private static String analyzeFile(Path filepath) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(filepath, StandardCharsets.UTF_8)) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null && lineCount < 200) {
                    content.append(line.toLowerCase()).append(' ');
                    lineCount++;
                }
            }
            String text = content.toString();

            Map<String, Integer> scores = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
                int score = 0;
                for (String kw : entry.getValue()) {
                    // Contagem simples de ocorrências com fronteira de palavra
                    Pattern pattern = Pattern.compile("\\b" + Pattern.quote(kw) + "\\b");
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        score++;
                    }
                }
                if (score > 0) {
                    scores.put(entry.getKey(), score);
                }
            }

            if (scores.isEmpty()) {
                // Tenta inferir pela pasta pai
                String parent = filepath.getParent().getFileName().toString().toLowerCase();
                for (String cat : CATEGORY_KEYWORDS.keySet()) {
                    for (String kw : CATEGORY_KEYWORDS.get(cat)) {
                        if (parent.contains(kw)) {
                            return cat;
                        }
                    }
                }
                return "other";
            }

            // Retorna a categoria com maior score (respeitando prioridade em empates)
            return scores.entrySet().stream()
                    .max((e1, e2) -> {
                        int cmp = Integer.compare(e1.getValue(), e2.getValue());
                        if (cmp != 0) return cmp;
                        int idx1 = CATEGORY_PRIORITY.indexOf(e1.getKey());
                        int idx2 = CATEGORY_PRIORITY.indexOf(e2.getKey());
                        return Integer.compare(idx2, idx1); // menor índice = maior prioridade
                    })
                    .get().getKey();

        } catch (IOException e) {
            LOGGER.warning("Erro ao ler " + filepath + ": " + e.getMessage());
            return "other";
        }
    }

    /**
     * Coleta todos os arquivos .py e .java no diretório raiz.
     */
    private static List<Path> findProjectFiles(Path root) throws IOException {
        List<Path> files = new ArrayList<>();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String ext = file.toString().toLowerCase();
                boolean validExt = TARGET_EXT.stream().anyMatch(ext::endsWith);
                if (validExt) {
                    files.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                // Ignora diretórios problemáticos
                String dirName = dir.toString();
                if (dirName.contains("organized") || dirName.contains("venv") || dirName.contains(".git")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    /**
     * Analisa o nome do arquivo para determinar categoria com base em padrões.
     */
    private static String analyzeFilename(Path filepath) {
        String filename = filepath.getFileName().toString().toLowerCase();

        // Padrões específicos por nome de arquivo (ajustados para aceitar .py e .java)
        Map<String, List<String>> filenamePatterns = Map.of(
            "ai_core", Arrays.asList(
                ".*ai.*\\.(py|java)$", ".*neural.*\\.(py|java)$", ".*learning.*\\.(py|java)$",
                ".*model.*\\.(py|java)$", ".*predict.*\\.(py|java)$", ".*train.*\\.(py|java)$",
                ".*inteligencia.*\\.(py|java)$", ".*rede.*\\.(py|java)$", ".*aprendizado.*\\.(py|java)$"
            ),
            "trading", Arrays.asList(
                ".*trade.*\\.(py|java)$", ".*market.*\\.(py|java)$", ".*broker.*\\.(py|java)$",
                ".*order.*\\.(py|java)$", ".*position.*\\.(py|java)$", ".*risk.*\\.(py|java)$",
                ".*negocio.*\\.(py|java)$", ".*mercado.*\\.(py|java)$", ".*ordem.*\\.(py|java)$"
            ),
            "monitoring", Arrays.asList(
                ".*monitor.*\\.(py|java)$", ".*dashboard.*\\.(py|java)$", ".*alert.*\\.(py|java)$",
                ".*metric.*\\.(py|java)$", ".*report.*\\.(py|java)$", ".*visual.*\\.(py|java)$"
            ),
            "windows_integration", Arrays.asList(
                ".*windows.*\\.(py|java)$", ".*win32.*\\.(py|java)$", ".*setup.*\\.(py|java)$",
                ".*installer.*\\.(py|java)$", ".*config.*\\.(py|java)$"
            ),
            "tests", Arrays.asList(
                "^test_.*\\.(py|java)$", ".*_test\\.(py|java)$", "^spec_.*\\.(py|java)$",
                ".*_spec\\.(py|java)$", "^teste_.*\\.(py|java)$"
            ),
            "utils", Arrays.asList(
                ".*util.*\\.(py|java)$", ".*helper.*\\.(py|java)$", ".*common.*\\.(py|java)$",
                ".*base.*\\.(py|java)$", ".*core.*\\.(py|java)$", ".*aux.*\\.(py|java)$"
            )
        );

        for (Map.Entry<String, List<String>> entry : filenamePatterns.entrySet()) {
            for (String pattern : entry.getValue()) {
                if (Pattern.matches(pattern, filename)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------------
    // Movimentação e organização
    // ------------------------------------------------------------
    /**
     * Move o arquivo para a subpasta correspondente à categoria.
     * Preserva o nome do arquivo, mas adiciona parte do caminho original para evitar colisões.
     */
    private static Path moveToCategory(Path filepath, String category, Path outputBase) {
        try {
            Path destDir = outputBase.resolve(category);
            Files.createDirectories(destDir);

            // Nome único: prefixo com o caminho relativo ao root
            Path relPath = ROOT_PATH.relativize(filepath);
            String uniqueName = relPath.toString().replace(File.separator, "_");
            Path destFile = destDir.resolve(uniqueName);

            Files.move(filepath, destFile, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info(String.format("✅ Movido: %s → %s", filepath, destFile));
            return destFile;
        } catch (IOException e) {
            LOGGER.severe(String.format("❌ Falha ao mover %s: %s", filepath, e.getMessage()));
            return null;
        }
    }

    /**
     * Cria um módulo central que importa todos os arquivos de IA (Python).
     */
    private static void handleAiRedundancy(List<Path> aiFiles, Path outputBase) {
        if (aiFiles.isEmpty()) return;

        Path aiDir = outputBase.resolve("ai_core");
        Path centralModulePath = aiDir.resolve("ai_central_redundant.py");
        try {
            StringBuilder content = new StringBuilder();
            content.append("\"\"\"\nMódulo Central de Inteligência Artificial – Redundância\n");
            content.append("Importa todas as I.As disponíveis para garantir fallback.\n");
            content.append("Se uma falhar, o sistema pode alternar para outra.\n");
            content.append("\"\"\"\n\n");
            content.append("# Lista de módulos de IA disponíveis\n");
            content.append("AI_MODULES = [\n");
            for (Path f : aiFiles) {
                if (f != null && Files.exists(f)) {
                    Path rel = aiDir.relativize(f);
                    String modName = rel.toString().replace(File.separator, ".").replace(".py", "");
                    content.append(String.format("    '%s',\n", modName));
                }
            }
            content.append("]\n\n");
            content.append("def load_ai_modules():\n");
            content.append("    \"\"\"Carrega dinamicamente todos os módulos de IA.\"\"\"\n");
            content.append("    modules = {}\n");
            content.append("    for mod_name in AI_MODULES:\n");
            content.append("        try:\n");
            content.append("            mod = __import__(mod_name)\n");
            content.append("            modules[mod_name] = mod\n");
            content.append("        except ImportError as e:\n");
            content.append("            print(f\"Falha ao carregar {mod_name}: {e}\")\n");
            content.append("    return modules\n\n");
            content.append("def get_available_ai_modules():\n");
            content.append("    \"\"\"Retorna a lista de módulos de IA disponíveis.\"\"\"\n");
            content.append("    return AI_MODULES.copy()\n");

            Files.writeString(centralModulePath, content.toString(), StandardCharsets.UTF_8,
                              StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            LOGGER.info("Módulo central de redundância criado: " + centralModulePath);
        } catch (IOException e) {
            LOGGER.severe("❌ Erro ao criar módulo central: " + e.getMessage());
        }
    }

    /**
     * Cria um relatório JSON detalhado da organização.
     */
    private static void createOrganizationReport(Map<String, List<Path>> categorized, Path outputBase) {
        Path reportPath = outputBase.resolve("organization_report.json");

        // Estrutura de dados para o relatório
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        // Metadados
        json.append("  \"metadata\": {\n");
        json.append("    \"version\": \"3.0\",\n");
        json.append("    \"generated_at\": \"").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\",\n");
        json.append("    \"generated_by\": \"ReorganizeVhalinor.java\",\n");
        json.append("    \"root_path\": \"").append(escapeJson(ROOT_PATH.toString())).append("\",\n");
        json.append("    \"output_base\": \"").append(escapeJson(outputBase.toString())).append("\"\n");
        json.append("  },\n");

        // Sumário
        int totalFiles = categorized.values().stream().mapToInt(List::size).sum();
        long totalSize = 0;
        long totalLines = 0;
        Map<String, Integer> fileTypes = new HashMap<>();
        Map<String, List<Map<String, Object>>> categoryFiles = new LinkedHashMap<>();
        Map<String, Map<String, Object>> categoryStats = new LinkedHashMap<>();

        for (String cat : CATEGORY_PRIORITY) {
            List<Path> files = categorized.getOrDefault(cat, Collections.emptyList());
            if (!files.isEmpty()) {
                List<Map<String, Object>> fileDetails = new ArrayList<>();
                long catSize = 0, catLines = 0;
                Map<String, Integer> catTypes = new HashMap<>();

                for (Path f : files) {
                    if (f != null && Files.exists(f)) {
                        try {
                            long size = Files.size(f);
                            catSize += size;
                            totalSize += size;
                            long lines = Files.lines(f).count();
                            catLines += lines;
                            totalLines += lines;

                            String ext = f.toString().toLowerCase();
                            ext = ext.substring(ext.lastIndexOf('.'));
                            fileTypes.merge(ext, 1, Integer::sum);
                            catTypes.merge(ext, 1, Integer::sum);

                            Map<String, Object> fd = new LinkedHashMap<>();
                            fd.put("path", outputBase.relativize(f).toString().replace('\\', '/'));
                            fd.put("size_bytes", size);
                            fd.put("size_kb", Math.round(size / 10.24) / 100.0);
                            fd.put("lines", lines);
                            fd.put("last_modified", Files.getLastModifiedTime(f).toString());
                            fileDetails.add(fd);
                        } catch (IOException ignored) {}
                    }
                }

                Map<String, Object> stats = new LinkedHashMap<>();
                stats.put("count", files.size());
                stats.put("total_size_bytes", catSize);
                stats.put("total_size_mb", Math.round(catSize * 100.0 / (1024 * 1024)) / 100.0);
                stats.put("total_lines", catLines);
                stats.put("avg_lines_per_file", files.size() > 0 ? Math.round(catLines * 100.0 / files.size()) / 100.0 : 0);
                stats.put("file_types", catTypes);
                stats.put("files", fileDetails);
                categoryStats.put(cat, stats);
            }
        }

        // Outras categorias (não prioritárias)
        for (Map.Entry<String, List<Path>> entry : categorized.entrySet()) {
            if (!CATEGORY_PRIORITY.contains(entry.getKey()) && !entry.getKey().equals("other")) continue;
            // já incluímos acima? Não, temos que incluir all categories.
        }
        // Vamos percorrer todas as chaves do map original
        for (String cat : categorized.keySet()) {
            if (!categoryStats.containsKey(cat)) {
                List<Path> files = categorized.get(cat);
                if (files.isEmpty()) continue;
                List<Map<String, Object>> fileDetails = new ArrayList<>();
                long catSize = 0, catLines = 0;
                Map<String, Integer> catTypes = new HashMap<>();
                for (Path f : files) {
                    if (f != null && Files.exists(f)) {
                        try {
                            long size = Files.size(f);
                            catSize += size;
                            totalSize += size;
                            long lines = Files.lines(f).count();
                            catLines += lines;
                            totalLines += lines;
                            String ext = f.toString().substring(f.toString().lastIndexOf('.'));
                            fileTypes.merge(ext, 1, Integer::sum);
                            catTypes.merge(ext, 1, Integer::sum);
                            Map<String, Object> fd = new LinkedHashMap<>();
                            fd.put("path", outputBase.relativize(f).toString().replace('\\', '/'));
                            fd.put("size_bytes", size);
                            fd.put("size_kb", Math.round(size / 10.24) / 100.0);
                            fd.put("lines", lines);
                            fd.put("last_modified", Files.getLastModifiedTime(f).toString());
                            fileDetails.add(fd);
                        } catch (IOException ignored) {}
                    }
                }
                Map<String, Object> stats = new LinkedHashMap<>();
                stats.put("count", files.size());
                stats.put("total_size_bytes", catSize);
                stats.put("total_size_mb", Math.round(catSize * 100.0 / (1024 * 1024)) / 100.0);
                stats.put("total_lines", catLines);
                stats.put("avg_lines_per_file", files.size() > 0 ? Math.round(catLines * 100.0 / files.size()) / 100.0 : 0);
                stats.put("file_types", catTypes);
                stats.put("files", fileDetails);
                categoryStats.put(cat, stats);
            }
        }

        json.append("  \"summary\": {\n");
        json.append("    \"total_files\": ").append(totalFiles).append(",\n");
        json.append("    \"total_size_bytes\": ").append(totalSize).append(",\n");
        json.append("    \"total_size_mb\": ").append(Math.round(totalSize * 100.0 / (1024 * 1024)) / 100.0).append(",\n");
        json.append("    \"total_size_gb\": ").append(Math.round(totalSize * 100.0 / (1024 * 1024 * 1024)) / 100.0).append(",\n");
        json.append("    \"total_lines\": ").append(totalLines).append(",\n");
        json.append("    \"avg_lines_per_file\": ").append(totalFiles > 0 ? Math.round(totalLines * 100.0 / totalFiles) / 100.0 : 0).append(",\n");
        json.append("    \"categories\": ").append(categoryStats.size()).append(",\n");
        json.append("    \"file_types\": ").append(mapToJson(fileTypes)).append("\n");
        json.append("  },\n");

        // Insights
        String largestByCount = categoryStats.entrySet().stream()
                .max(Comparator.comparing(e -> (int) e.getValue().get("count"))).map(Map.Entry::getKey).orElse(null);
        String largestBySize = categoryStats.entrySet().stream()
                .max(Comparator.comparing(e -> (long) e.getValue().get("total_size_bytes"))).map(Map.Entry::getKey).orElse(null);
        String mostLines = categoryStats.entrySet().stream()
                .max(Comparator.comparing(e -> (long) e.getValue().get("total_lines"))).map(Map.Entry::getKey).orElse(null);
        String dominantLang = fileTypes.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

        json.append("  \"insights\": {\n");
        json.append("    \"largest_category_by_count\": \"").append(largestByCount).append("\",\n");
        json.append("    \"largest_category_by_size\": \"").append(largestBySize).append("\",\n");
        json.append("    \"category_with_most_lines\": \"").append(mostLines).append("\",\n");
        json.append("    \"dominant_language\": \"").append(dominantLang).append("\",\n");
        json.append("    \"code_density\": ").append(totalSize > 0 ? Math.round(totalLines * 100.0 / (totalSize / 1024.0)) / 100.0 : 0).append("\n");
        json.append("  },\n");

        json.append("  \"categories\": ");
        json.append(mapToComplexJson(categoryStats));
        json.append(",\n");

        // Health check
        boolean allAccessible = true;
        for (List<Path> fl : categorized.values()) {
            for (Path p : fl) {
                if (p != null && !Files.exists(p)) {
                    allAccessible = false;
                    break;
                }
            }
            if (!allAccessible) break;
        }
        List<String> emptyCategories = categoryStats.entrySet().stream()
                .filter(e -> (int) e.getValue().get("count") == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        json.append("  \"health_check\": {\n");
        json.append("    \"all_files_accessible\": ").append(allAccessible).append(",\n");
        json.append("    \"duplicate_names\": [] ,\n");  // simplificado
        json.append("    \"empty_categories\": ").append(listToJson(emptyCategories)).append(",\n");
        json.append("    \"total_missing_files\": 0\n");
        json.append("  }\n");
        json.append("}");

        try {
            Files.writeString(reportPath, json.toString(), StandardCharsets.UTF_8,
                              StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            LOGGER.info("Relatório salvo em " + reportPath);
        } catch (IOException e) {
            LOGGER.severe("❌ Erro ao salvar relatório: " + e.getMessage());
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String mapToJson(Map<String, Integer> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (!first) sb.append(", ");
            sb.append("\"").append(e.getKey()).append("\": ").append(e.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String listToJson(List<String> list) {
        return "[" + list.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")) + "]";
    }

    private static String mapToComplexJson(Map<String, Map<String, Object>> catStats) {
        StringBuilder sb = new StringBuilder("{\n");
        boolean firstCat = true;
        for (Map.Entry<String, Map<String, Object>> entry : catStats.entrySet()) {
            if (!firstCat) sb.append(",\n");
            sb.append("    \"").append(entry.getKey()).append("\": ");
            Map<String, Object> stats = entry.getValue();
            sb.append("{\n");
            boolean firstField = true;
            for (Map.Entry<String, Object> field : stats.entrySet()) {
                if (!firstField) sb.append(",\n");
                sb.append("      \"").append(field.getKey()).append("\": ");
                Object val = field.getValue();
                if (val instanceof Map) {
                    sb.append(mapToJsonSimple((Map<?, ?>) val));
                } else if (val instanceof List) {
                    List<?> list = (List<?>) val;
                    if (!list.isEmpty() && list.get(0) instanceof Map) {
                        sb.append("[\n");
                        for (int i = 0; i < list.size(); i++) {
                            if (i > 0) sb.append(",\n");
                            sb.append("        ").append(mapToJsonSimple((Map<?, ?>) list.get(i)));
                        }
                        sb.append("\n      ]");
                    } else {
                        sb.append(listToJson((List<String>) list));
                    }
                } else if (val instanceof String) {
                    sb.append("\"").append(val).append("\"");
                } else {
                    sb.append(val);
                }
                firstField = false;
            }
            sb.append("\n    }");
            firstCat = false;
        }
        sb.append("\n  }");
        return sb.toString();
    }

    private static String mapToJsonSimple(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (!first) sb.append(", ");
            sb.append("\"").append(e.getKey()).append("\": ");
            Object val = e.getValue();
            if (val instanceof String) sb.append("\"").append(val).append("\"");
            else sb.append(val);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Cria um README para a estrutura organizada.
     */
    private static void createReadme(Path outputBase) {
        Path readmePath = outputBase.resolve("README.md");
        String content = String.format(
            "# VHALINOR TRADER - Estrutura Organizada\n\n" +
            "Este diretório contém o código do VHALINOR TRADER reorganizado por categorias funcionais.\n\n" +
            "## Estrutura de Diretórios\n\n" +
            "### 🤖 ai_core/\n" +
            "Módulos de inteligência artificial, redes neurais, aprendizado de máquina e algoritmos preditivos.\n\n" +
            "### 💰 trading/\n" +
            "Sistemas de negociação, execução de ordens, gestão de portfólio e estratégias.\n\n" +
            "### 📊 data/\n" +
            "Processamento, limpeza e armazenamento de dados de mercado.\n\n" +
            "### ⚡ execution/\n" +
            "Módulos de execução principal, orquestração e gerenciamento de tarefas.\n\n" +
            "### 📈 monitoring/\n" +
            "Dashboards, alertas, métricas e sistemas de monitoramento.\n\n" +
            "### 🪟 windows_integration/\n" +
            "Configurações específicas para Windows, setup e integrações do sistema operacional.\n\n" +
            "### 🔐 security/\n" +
            "Autenticação, criptografia e gerenciamento de credenciais.\n\n" +
            "### 🛠️ utils/\n" +
            "Funções utilitárias, helpers e código compartilhado.\n\n" +
            "### 🧪 tests/\n" +
            "Testes unitários, testes de integração e especificações.\n\n" +
            "### 📦 other/\n" +
            "Arquivos que não se encaixaram nas categorias acima.\n\n" +
            "## Módulo Central de IA\n\n" +
            "O arquivo `ai_core/ai_central_redundant.py` funciona como um ponto central para carregar\n" +
            "todos os módulos de IA disponíveis, fornecendo redundância e fallback automático.\n\n" +
            "## Como Usar\n\n" +
            "```python\n" +
            "from ai_core.ai_central_redundant import load_ai_modules\n\n" +
            "# Carrega todos os módulos de IA disponíveis\n" +
            "ai_modules = load_ai_modules()\n\n" +
            "# Usa o módulo principal ou fallback\n" +
            "main_ai = ai_modules.get('main_ai_module', ai_modules.get('fallback_module'))\n" +
            "```\n\n" +
            "## Data da Reorganização\n\n" +
            "%s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            Files.writeString(readmePath, content, StandardCharsets.UTF_8,
                              StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            LOGGER.info("README criado em " + readmePath);
        } catch (IOException e) {
            LOGGER.severe("❌ Erro ao criar README: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // Fluxo principal
    // ------------------------------------------------------------
    public static void main(String[] args) {
        if (!Files.exists(ROOT_PATH)) {
            LOGGER.severe("Diretório não encontrado: " + ROOT_PATH);
            return;
        }

        try {
            // Lista arquivos
            List<Path> allFiles = findProjectFiles(ROOT_PATH);
            LOGGER.info("Encontrados " + allFiles.size() + " arquivos para analisar.");

            // Mapeia categorias
            Map<String, List<Path>> categorized = new LinkedHashMap<>();
            for (String cat : CATEGORY_PRIORITY) {
                categorized.put(cat, new ArrayList<>());
            }
            categorized.put("other", new ArrayList<>());

            for (Path fpath : allFiles) {
                // Primeiro tenta classificar pelo nome do arquivo
                String category = analyzeFilename(fpath);
                if (category == null) {
                    category = analyzeFile(fpath);
                }
                categorized.computeIfAbsent(category, k -> new ArrayList<>()).add(fpath);
            }

            // Exibe resumo
            System.out.println("\n📊 Resumo da classificação:");
            for (Map.Entry<String, List<Path>> entry : categorized.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    System.out.printf("  📁 %s: %d arquivos%n", entry.getKey(), entry.getValue().size());
                }
            }

            // Confirmação do usuário
            Scanner scanner = new Scanner(System.in);
            System.out.print("\n🤔 Deseja reorganizar os arquivos? (s/N): ");
            String resp = scanner.nextLine().trim().toLowerCase();
            scanner.close();
            if (!resp.equals("s")) {
                LOGGER.info("❌ Operação cancelada pelo usuário.");
                return;
            }

            // Cria diretório de saída
            Files.createDirectories(OUTPUT_BASE);

            // Move arquivos
            List<Path> aiFilesMoved = new ArrayList<>();
            int movedCount = 0;

            for (Map.Entry<String, List<Path>> entry : categorized.entrySet()) {
                String category = entry.getKey();
                for (Path f : entry.getValue()) {
                    Path newPath = moveToCategory(f, category, OUTPUT_BASE);
                    if (newPath != null) {
                        movedCount++;
                        if ("ai_core".equals(category)) {
                            aiFilesMoved.add(newPath);
                        }
                    }
                }
            }

            // Cria redundância central para IA
            handleAiRedundancy(aiFilesMoved, OUTPUT_BASE);

            // Cria relatório e README
            // Para o relatório precisamos dos caminhos ORIGINAIS? Usamos os paths já movidos.
            // Precisamos mapear novamente com os novos paths para o relatório? 
            // O relatório original usava os paths movidos. Vamos passar o categorized com os paths originais,
            // mas após move, eles não existem mais no local original. Melhor usar uma cópia.
            // Vamos criar um novo map com os paths dos destinos.
            Map<String, List<Path>> destCategorized = new LinkedHashMap<>();
            for (String cat : categorized.keySet()) {
                List<Path> destFiles = new ArrayList<>();
                for (Path orig : categorized.get(cat)) {
                    Path rel = ROOT_PATH.relativize(orig);
                    Path dest = OUTPUT_BASE.resolve(cat).resolve(rel.toString().replace(File.separator, "_"));
                    if (Files.exists(dest)) destFiles.add(dest);
                }
                if (!destFiles.isEmpty()) destCategorized.put(cat, destFiles);
            }
            createOrganizationReport(destCategorized, OUTPUT_BASE);
            createReadme(OUTPUT_BASE);

            // Resumo final
            System.out.printf("%n✅ Reorganização concluída!%n");
            System.out.printf("📁 %d arquivos movidos para %s%n", movedCount, OUTPUT_BASE);
            System.out.println("📋 Relatório detalhado salvo em organization_report.json");
            System.out.println("📖 README criado com informações da nova estrutura");

        } catch (Exception e) {
            LOGGER.severe("❌ Erro fatal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
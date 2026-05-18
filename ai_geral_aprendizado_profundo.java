package com.vhalinor.deeplearning;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * VHALINOR Aprendizado Profundo v6.0
 * Sistema de deep learning avançado com suporte para múltiplas arquiteturas,
 * transfer learning, fine-tuning e otimização de hiperparâmetros.
 */
public class AprendizadoProfundo {

    private static final Logger LOGGER = Logger.getLogger(AprendizadoProfundo.class.getName());

    // ======================== ENUMS ========================

    public enum ArquiteturaDeepLearning {
        MLP("mlp"), CNN("cnn"), RNN("rnn"), LSTM("lstm"), GRU("gru"),
        TRANSFORMER("transformer"), AUTOENCODER("autoencoder"), VAE("vae"),
        GAN("gan"), RESNET("resnet"), DENSENET("densenet"), MOBILENET("mobilenet"),
        BERT("bert"), GPT("gpt"), CUSTOM("custom");

        private final String value;
        ArquiteturaDeepLearning(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum TipoOtimizacao {
        SGD("sgd"), ADAM("adam"), ADAMW("adamw"), RMSPROP("rmsprop"),
        ADAGRAD("adagrad"), NADAM("nadam"), LION("lion");

        private final String value;
        TipoOtimizacao(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    public enum LRScheduler {
        CONSTANT("constant"), STEP("step"), EXPONENTIAL("exponential"),
        COSINE("cosine"), PLATEAU("plateau"), CYCLICAL("cyclical"),
        WARMUP("warmup");

        private final String value;
        LRScheduler(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    // ======================== DATA CLASSES ========================

    public static class ConfigDeepLearning {
        private ArquiteturaDeepLearning arquitetura;
        private List<Integer> inputShape;     // forma de entrada (ex: [10] para 1D)
        private List<Integer> outputShape;    // forma de saída
        private int numCamadas = 5;
        private List<Integer> unidadesPorCamada = List.of(512, 256, 128, 64, 32);
        private double dropoutRate = 0.3;
        private double learningRate = 0.001;
        private int batchSize = 32;
        private int epochs = 100;
        private TipoOtimizacao otimizador = TipoOtimizacao.ADAM;
        private LRScheduler lrScheduler = LRScheduler.PLATEAU;
        private int earlyStoppingPatience = 10;
        private boolean useBatchNorm = true;
        private boolean useDropout = true;
        private double l1Reg = 0.0;
        private double l2Reg = 0.001;
        private Map<String, Object> customParams = new HashMap<>();

        // Construtores
        public ConfigDeepLearning(ArquiteturaDeepLearning arquitetura, List<Integer> inputShape, List<Integer> outputShape) {
            this.arquitetura = arquitetura;
            this.inputShape = inputShape;
            this.outputShape = outputShape;
        }

        // Getters e setters (omitted for brevity - must be added for JSON serialization)
        public ArquiteturaDeepLearning getArquitetura() { return arquitetura; }
        public void setArquitetura(ArquiteturaDeepLearning arquitetura) { this.arquitetura = arquitetura; }
        public List<Integer> getInputShape() { return inputShape; }
        public void setInputShape(List<Integer> inputShape) { this.inputShape = inputShape; }
        public List<Integer> getOutputShape() { return outputShape; }
        public void setOutputShape(List<Integer> outputShape) { this.outputShape = outputShape; }
        public int getNumCamadas() { return numCamadas; }
        public void setNumCamadas(int numCamadas) { this.numCamadas = numCamadas; }
        public List<Integer> getUnidadesPorCamada() { return unidadesPorCamada; }
        public void setUnidadesPorCamada(List<Integer> unidadesPorCamada) { this.unidadesPorCamada = unidadesPorCamada; }
        public double getDropoutRate() { return dropoutRate; }
        public void setDropoutRate(double dropoutRate) { this.dropoutRate = dropoutRate; }
        public double getLearningRate() { return learningRate; }
        public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
        public int getBatchSize() { return batchSize; }
        public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
        public int getEpochs() { return epochs; }
        public void setEpochs(int epochs) { this.epochs = epochs; }
        public TipoOtimizacao getOtimizador() { return otimizador; }
        public void setOtimizador(TipoOtimizacao otimizador) { this.otimizador = otimizador; }
        public LRScheduler getLrScheduler() { return lrScheduler; }
        public void setLrScheduler(LRScheduler lrScheduler) { this.lrScheduler = lrScheduler; }
        public int getEarlyStoppingPatience() { return earlyStoppingPatience; }
        public void setEarlyStoppingPatience(int earlyStoppingPatience) { this.earlyStoppingPatience = earlyStoppingPatience; }
        public boolean isUseBatchNorm() { return useBatchNorm; }
        public void setUseBatchNorm(boolean useBatchNorm) { this.useBatchNorm = useBatchNorm; }
        public boolean isUseDropout() { return useDropout; }
        public void setUseDropout(boolean useDropout) { this.useDropout = useDropout; }
        public double getL1Reg() { return l1Reg; }
        public void setL1Reg(double l1Reg) { this.l1Reg = l1Reg; }
        public double getL2Reg() { return l2Reg; }
        public void setL2Reg(double l2Reg) { this.l2Reg = l2Reg; }
        public Map<String, Object> getCustomParams() { return customParams; }
        public void setCustomParams(Map<String, Object> customParams) { this.customParams = customParams; }
    }

    public static class ResultadoTreinamento {
        private String modelId;
        private String arquitetura;
        private double finalLoss;
        private double finalValLoss;
        private int bestEpoch;
        private double accuracy;
        private double f1Score;
        private double precision;
        private double recall;
        private double tempoTreinamentoSegundos;
        private Map<String, List<Double>> history;
        private Map<String, Object> hiperparametros;
        private String timestamp;

        public ResultadoTreinamento(String modelId, String arquitetura, double finalLoss, double finalValLoss,
                                    int bestEpoch, double accuracy, double f1Score, double precision, double recall,
                                    double tempoTreinamentoSegundos, Map<String, List<Double>> history,
                                    Map<String, Object> hiperparametros) {
            this.modelId = modelId;
            this.arquitetura = arquitetura;
            this.finalLoss = finalLoss;
            this.finalValLoss = finalValLoss;
            this.bestEpoch = bestEpoch;
            this.accuracy = accuracy;
            this.f1Score = f1Score;
            this.precision = precision;
            this.recall = recall;
            this.tempoTreinamentoSegundos = tempoTreinamentoSegundos;
            this.history = history;
            this.hiperparametros = hiperparametros;
            this.timestamp = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        }

        // Getters e setters (omitidos, mas necessários para serialização JSON)
        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
        public String getArquitetura() { return arquitetura; }
        public void setArquitetura(String arquitetura) { this.arquitetura = arquitetura; }
        public double getFinalLoss() { return finalLoss; }
        public void setFinalLoss(double finalLoss) { this.finalLoss = finalLoss; }
        public double getFinalValLoss() { return finalValLoss; }
        public void setFinalValLoss(double finalValLoss) { this.finalValLoss = finalValLoss; }
        public int getBestEpoch() { return bestEpoch; }
        public void setBestEpoch(int bestEpoch) { this.bestEpoch = bestEpoch; }
        public double getAccuracy() { return accuracy; }
        public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
        public double getF1Score() { return f1Score; }
        public void setF1Score(double f1Score) { this.f1Score = f1Score; }
        public double getPrecision() { return precision; }
        public void setPrecision(double precision) { this.precision = precision; }
        public double getRecall() { return recall; }
        public void setRecall(double recall) { this.recall = recall; }
        public double getTempoTreinamentoSegundos() { return tempoTreinamentoSegundos; }
        public void setTempoTreinamentoSegundos(double tempoTreinamentoSegundos) { this.tempoTreinamentoSegundos = tempoTreinamentoSegundos; }
        public Map<String, List<Double>> getHistory() { return history; }
        public void setHistory(Map<String, List<Double>> history) { this.history = history; }
        public Map<String, Object> getHiperparametros() { return hiperparametros; }
        public void setHiperparametros(Map<String, Object> hiperparametros) { this.hiperparametros = hiperparametros; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    public static class ModeloPreTreinado {
        private String nome;
        private String arquitetura;
        private int camadasCongeladas;
        private int camadasTreinaveis;
        private String datasetOrigem;
        private double accuracyOrigem;
        private String caminhoPesos;

        public ModeloPreTreinado(String nome, String arquitetura, int camadasCongeladas, int camadasTreinaveis,
                                 String datasetOrigem, double accuracyOrigem, String caminhoPesos) {
            this.nome = nome;
            this.arquitetura = arquitetura;
            this.camadasCongeladas = camadasCongeladas;
            this.camadasTreinaveis = camadasTreinaveis;
            this.datasetOrigem = datasetOrigem;
            this.accuracyOrigem = accuracyOrigem;
            this.caminhoPesos = caminhoPesos;
        }

        public String getNome() { return nome; }
        public String getArquitetura() { return arquitetura; }
        public int getCamadasCongeladas() { return camadasCongeladas; }
        public int getCamadasTreinaveis() { return camadasTreinaveis; }
        public String getDatasetOrigem() { return datasetOrigem; }
        public double getAccuracyOrigem() { return accuracyOrigem; }
        public String getCaminhoPesos() { return caminhoPesos; }
    }

    // ======================== CLASSE PRINCIPAL ========================

    private final String device;
    private final Map<String, Object> modelos = new ConcurrentHashMap<>();   // modelId -> estrutura interna
    private final Deque<ResultadoTreinamento> historicoTreinamentos = new ArrayDeque<>(); // max 100
    private final Map<String, ModeloPreTreinado> modelosPreTreinados = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // Flags de disponibilidade (simuladas)
    private static final boolean TORCH_AVAILABLE = false;
    private static final boolean TF_AVAILABLE = false;
    private static final boolean OPTUNA_AVAILABLE = false;

    public AprendizadoProfundo() {
        this("auto");
    }

    public AprendizadoProfundo(String device) {
        this.device = detectarDevice(device);
        initModelosPreTreinados();
        LOGGER.info("AprendizadoProfundo inicializado no device: " + this.device);
    }

    private String detectarDevice(String deviceRequest) {
        if ("auto".equalsIgnoreCase(deviceRequest)) {
            // Simulação: sempre CPU
            return "cpu";
        }
        return deviceRequest;
    }

    private void initModelosPreTreinados() {
        modelosPreTreinados.put("resnet50", new ModeloPreTreinado("ResNet50", "CNN", 100, 50, "ImageNet", 0.92, null));
        modelosPreTreinados.put("vgg16", new ModeloPreTreinado("VGG16", "CNN", 15, 34, "ImageNet", 0.90, null));
        modelosPreTreinados.put("bert_base", new ModeloPreTreinado("BERT-Base", "Transformer", 10, 12, "BooksCorpus", 0.88, null));
        modelosPreTreinados.put("gpt2", new ModeloPreTreinado("GPT-2", "Transformer", 8, 12, "WebText", 0.85, null));
    }

    /**
     * Cria um modelo de deep learning de acordo com a configuração.
     * @return ID único do modelo criado.
     */
    public String criarModelo(ConfigDeepLearning config) throws UnsupportedOperationException {
        String modelId = generateId(config.getArquitetura().getValue() + "_");
        if (!TORCH_AVAILABLE && !TF_AVAILABLE) {
            throw new UnsupportedOperationException("Nenhum framework deep learning disponível (PyTorch ou TensorFlow).");
        }

        // Stub: armazenamos apenas a configuração
        Map<String, Object> modelInfo = new HashMap<>();
        modelInfo.put("config", config);
        modelInfo.put("framework", TORCH_AVAILABLE ? "pytorch" : "tensorflow");
        modelInfo.put("criado_em", nowIso());
        modelos.put(modelId, modelInfo);

        LOGGER.info("Modelo criado: " + modelId + " (arquitetura: " + config.getArquitetura().getValue() + ")");
        return modelId;
    }

    /**
     * Aplica transfer learning a partir de um modelo pré-treinado.
     * @param modeloBaseId Identificador do modelo base (deve existir em modelosPreTreinados)
     * @param novoOutputShape Nova forma de saída
     * @param camadasCongeladas Número de camadas a congelar (opcional)
     * @param learningRateFineTune Taxa de aprendizado para fine-tuning
     * @return ID do novo modelo adaptado.
     */
    public String transferLearning(String modeloBaseId, List<Integer> novoOutputShape,
                                   Integer camadasCongeladas, Double learningRateFineTune) {
        if (!modelosPreTreinados.containsKey(modeloBaseId)) {
            throw new IllegalArgumentException("Modelo pré-treinado não encontrado: " + modeloBaseId);
        }
        ModeloPreTreinado base = modelosPreTreinados.get(modeloBaseId);
        String newId = "transfer_" + modeloBaseId + "_" + generateRandomSuffix();

        Map<String, Object> transferInfo = new HashMap<>();
        transferInfo.put("base_model", modeloBaseId);
        transferInfo.put("novo_output_shape", novoOutputShape);
        transferInfo.put("camadas_congeladas", camadasCongeladas != null ? camadasCongeladas : base.getCamadasCongeladas());
        transferInfo.put("learning_rate", learningRateFineTune != null ? learningRateFineTune : 0.0001);
        transferInfo.put("tipo", "transfer_learning");
        transferInfo.put("criado_em", nowIso());
        modelos.put(newId, transferInfo);

        LOGGER.info("Transfer learning aplicado: " + newId + " ← " + modeloBaseId);
        return newId;
    }

    /**
     * Realiza fine-tuning de um modelo existente.
     * @param modeloId ID do modelo a ser ajustado.
     * @param X_train Dados de treino (simulado)
     * @param y_train Rótulos de treino (simulado)
     * @param X_val Dados de validação (opcional)
     * @param y_val Rótulos de validação (opcional)
     * @param epochs Número de épocas
     * @param learningRate Taxa de aprendizado (opcional)
     * @return Resultado do treinamento.
     */
    public ResultadoTreinamento fineTuning(String modeloId, Object X_train, Object y_train,
                                           Object X_val, Object y_val,
                                           int epochs, Double learningRate) {
        if (!modelos.containsKey(modeloId)) {
            throw new IllegalArgumentException("Modelo não encontrado: " + modeloId);
        }

        Instant inicio = Instant.now();
        // Simulação de treinamento: geração de curvas de loss
        List<Double> trainLoss = new ArrayList<>();
        List<Double> valLoss = new ArrayList<>();
        for (int i = 0; i < epochs; i++) {
            double loss = 1.0 * Math.pow(0.9, i) + 0.1;
            double vloss = 1.1 * Math.pow(0.85, i) + 0.15;
            trainLoss.add(loss);
            valLoss.add(vloss);
        }
        int bestEpoch = findMinIndex(valLoss);
        double finalLoss = trainLoss.get(trainLoss.size() - 1);
        double finalValLoss = valLoss.get(valLoss.size() - 1);

        // Métricas simuladas
        double accuracy = 0.85 + random.nextDouble() * 0.1;
        double f1 = 0.82 + random.nextDouble() * 0.1;
        double precision = 0.84 + random.nextDouble() * 0.1;
        double recall = 0.80 + random.nextDouble() * 0.1;

        double tempo = (double) (System.currentTimeMillis() - inicio.toEpochMilli()) / 1000.0;

        Map<String, List<Double>> history = new HashMap<>();
        history.put("loss", trainLoss);
        history.put("val_loss", valLoss);

        Map<String, Object> hp = new HashMap<>();
        hp.put("epochs", epochs);
        hp.put("learning_rate", learningRate != null ? learningRate : 0.0001);
        hp.put("batch_size", 32); // valor fictício

        ResultadoTreinamento resultado = new ResultadoTreinamento(
            modeloId,
            ((Map<String, Object>) modelos.get(modeloId)).getOrDefault("arquitetura", "unknown").toString(),
            finalLoss, finalValLoss, bestEpoch, accuracy, f1, precision, recall,
            tempo, history, hp
        );

        historicoTreinamentos.add(resultado);
        while (historicoTreinamentos.size() > 100) historicoTreinamentos.removeFirst();

        LOGGER.info("Fine-tuning concluído para " + modeloId + " - loss final: " + finalLoss);
        return resultado;
    }

    /**
     * Otimização de hiperparâmetros usando Optuna (simulado se não disponível).
     * @param configBase Configuração base para o estudo.
     * @param nTrials Número de tentativas.
     * @return Mapa com melhores parâmetros e score.
     */
    public Map<String, Object> otimizacaoHiperparametros(ConfigDeepLearning configBase, int nTrials) {
        if (!OPTUNA_AVAILABLE) {
            LOGGER.warning("Optuna não disponível. Simulação de otimização será usada.");
            // Simulação simples: variação aleatória em torno da config base
            Map<String, Object> melhores = new HashMap<>();
            melhores.put("learning_rate", configBase.getLearningRate() * (0.5 + random.nextDouble()));
            melhores.put("batch_size", List.of(16, 32, 64, 128).get(random.nextInt(4)));
            melhores.put("dropout", 0.3 * (0.7 + random.nextDouble()));
            melhores.put("score", 0.85 + random.nextDouble() * 0.1);
            return Map.of("melhores_params", melhores, "melhor_score", melhores.get("score"), "n_trials", nTrials);
        }

        // Se Optuna estivesse disponível, implementaria a integração real.
        // Por ora, retorna simulação.
        Map<String, Object> best = new HashMap<>();
        best.put("learning_rate", 0.0005);
        best.put("batch_size", 64);
        best.put("dropout", 0.25);
        return Map.of("melhores_params", best, "melhor_score", 0.912, "n_trials", nTrials);
    }

    /**
     * Cria um agendador de taxa de aprendizado (stub).
     * @param otimizador Referência ao otimizador (pode ser null)
     * @param schedulerType Tipo de scheduler
     * @param kwargs Parâmetros adicionais
     * @return Objeto simulando o scheduler ou null.
     */
    public Object criarLrScheduler(Object otimizador, LRScheduler schedulerType, Map<String, Object> kwargs) {
        // Em ambiente real com PyTorch, retornaria um scheduler.
        LOGGER.fine("Criando scheduler do tipo " + schedulerType.getValue());
        return new Object(); // placeholder
    }

    /**
     * Aplica técnicas de data augmentation a um conjunto de dados.
     * @param data Lista de números (simula dados 1D)
     * @param tecnicas Lista de técnicas ("noise", "scale", "shift")
     * @return Lista aumentada.
     */
    public List<Double> dataAugmentation(List<Double> data, List<String> tecnicas) {
        if (tecnicas == null || tecnicas.isEmpty()) {
            tecnicas = List.of("noise", "scale", "shift");
        }
        List<Double> augmented = new ArrayList<>(data);
        for (double valor : data) {
            for (String t : tecnicas) {
                switch (t) {
                    case "noise":
                        augmented.add(valor + random.nextGaussian() * 0.01);
                        break;
                    case "scale":
                        augmented.add(valor * (0.95 + random.nextDouble() * 0.1));
                        break;
                    case "shift":
                        augmented.add(valor + (random.nextDouble() - 0.5) * 0.2);
                        break;
                    default:
                        break;
                }
            }
        }
        return augmented;
    }

    /**
     * Extrai features de uma camada profunda do modelo.
     * @param modeloId ID do modelo
     * @param dados Dados de entrada (simulado)
     * @param camadaExtracao Nome da camada (opcional)
     * @return Mapa com informações sobre as features extraídas.
     */
    public Map<String, Object> featureExtracaoProfunda(String modeloId, Object dados, String camadaExtracao) {
        if (!modelos.containsKey(modeloId)) {
            throw new IllegalArgumentException("Modelo não encontrado: " + modeloId);
        }
        int nAmostras = 100; // simulado
        int nFeatures = 128;
        Map<String, Object> result = new HashMap<>();
        result.put("features_shape", List.of(nAmostras, nFeatures));
        result.put("camada", camadaExtracao != null ? camadaExtracao : "penultimate");
        result.put("tipo", "deep_features");
        result.put("n_amostras", nAmostras);
        return result;
    }

    /**
     * Salva o modelo em disco (simulação).
     * @param modeloId ID do modelo
     * @param caminho Base do caminho (ex: "/models/meu_modelo")
     */
    public void salvarModelo(String modeloId, String caminho) throws IOException {
        if (!modelos.containsKey(modeloId)) {
            throw new IllegalArgumentException("Modelo não encontrado: " + modeloId);
        }
        Object modeloInfo = modelos.get(modeloId);
        Path metaPath = Paths.get(caminho + "_metadata.json");
        Path modelPath = Paths.get(caminho + "_model.pkl");

        // Salva metadados
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("model_id", modeloId);
        metadata.put("timestamp", nowIso());
        Files.writeString(metaPath, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(metadata),
                StandardCharsets.UTF_8);

        // Salva modelo (simulação: serialização Java)
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(modelPath))) {
            oos.writeObject(modeloInfo);
        }
        LOGGER.info("Modelo " + modeloId + " salvo em " + caminho);
    }

    /**
     * Carrega um modelo previamente salvo.
     * @param caminho Base do caminho (ex: "/models/meu_modelo")
     * @return ID do modelo carregado.
     */
    public String carregarModelo(String caminho) throws IOException, ClassNotFoundException {
        Path metaPath = Paths.get(caminho + "_metadata.json");
        Path modelPath = Paths.get(caminho + "_model.pkl");
        if (!Files.exists(metaPath) || !Files.exists(modelPath)) {
            throw new FileNotFoundException("Arquivos do modelo não encontrados em " + caminho);
        }

        String metadataJson = Files.readString(metaPath, StandardCharsets.UTF_8);
        // Parsing simples (poderia usar Jackson)
        String modelId = extractModelIdFromJson(metadataJson);

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(modelPath))) {
            Object modeloInfo = ois.readObject();
            modelos.put(modelId, modeloInfo);
        }
        LOGGER.info("Modelo carregado: " + modelId);
        return modelId;
    }

    /**
     * Retorna o status atual do sistema de aprendizado profundo.
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("device", device);
        status.put("modelos_criados", modelos.size());
        status.put("treinamentos_realizados", historicoTreinamentos.size());
        status.put("modelos_pre_treinados_disponiveis", new ArrayList<>(modelosPreTreinados.keySet()));
        status.put("frameworks_disponiveis", Map.of(
                "pytorch", TORCH_AVAILABLE,
                "tensorflow", TF_AVAILABLE,
                "optuna", OPTUNA_AVAILABLE
        ));
        status.put("ultimo_treinamento", historicoTreinamentos.isEmpty() ? null : historicoTreinamentos.getLast().getTimestamp());
        return status;
    }

    // ======================== MÉTODOS AUXILIARES ========================

    private String generateId(String prefix) {
        String input = prefix + System.nanoTime() + UUID.randomUUID().toString();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            return prefix + Long.toHexString(System.nanoTime());
        }
    }

    private String generateRandomSuffix() {
        return Long.toHexString(System.nanoTime()) + Integer.toHexString(random.nextInt());
    }

    private String nowIso() {
        return Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    private int findMinIndex(List<Double> list) {
        int idx = 0;
        double min = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) < min) {
                min = list.get(i);
                idx = i;
            }
        }
        return idx;
    }

    private String extractModelIdFromJson(String json) {
        // Simples busca pelo campo "model_id"
        int idx = json.indexOf("\"model_id\"");
        if (idx < 0) return "unknown";
        int colon = json.indexOf(":", idx);
        int startQuote = json.indexOf("\"", colon);
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (startQuote < 0 || endQuote < 0) return "unknown";
        return json.substring(startQuote + 1, endQuote);
    }

    // ======================== EXEMPLO DE USO ========================
    public static void main(String[] args) {
        try {
            System.out.println("Inicializando AprendizadoProfundo...");
            AprendizadoProfundo dl = new AprendizadoProfundo();

            ConfigDeepLearning config = new ConfigDeepLearning(
                ArquiteturaDeepLearning.MLP,
                List.of(20),
                List.of(1)
            );
            config.setUnidadesPorCamada(List.of(128, 64, 32));
            config.setLearningRate(0.0005);
            config.setEpochs(50);

            String modelId = dl.criarModelo(config);
            System.out.println("Modelo criado: " + modelId);

            // Simulação de fine-tuning
            Object fakeX = new double[100][20];
            Object fakeY = new double[100];
            ResultadoTreinamento res = dl.fineTuning(modelId, fakeX, fakeY, null, null, 30, null);
            System.out.println("F1-Score após fine-tuning: " + res.getF1Score());

            // Transfer learning
            String transferId = dl.transferLearning("resnet50", List.of(10), 80, 0.0001);
            System.out.println("Modelo transfer: " + transferId);

            // Otimização de hiperparâmetros
            Map<String, Object> optResult = dl.otimizacaoHiperparametros(config, 20);
            System.out.println("Melhor learning rate: " + optResult.get("melhores_params"));

            System.out.println("Status final: " + dl.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * SYLPH AI Avatar – Sencient JavaFX Application
 * Converted from Streamlit Python app.
 */
public class SylphAvatarApp extends Application {

    // ---------- Logging ----------
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] %4$s: %5$s%n");
    }
    private static final Logger LOG = Logger.getLogger(SylphAvatarApp.class.getName());

    // ---------- Enums ----------
    enum Emotion {
        NEUTRAL("neutral", "#06b6d4"),
        HAPPY("happy", "#10b981"),
        EXCITED("excited", "#f59e0b"),
        FOCUSED("focused", "#3b82f6"),
        INTENSE("intense", "#8b5cf6"),
        SURPRISED("surprised", "#f97316"),
        SAD("sad", "#6b7280"),
        ANALYZING("analyzing", "#6366f1"),
        DEFENSIVE("defensive", "#ef4444"),
        CALM("calm", "#14b8a6"),
        SLEEPING("sleeping", "#374151");
        final String emotionName, color;

        Emotion(String name, String color) { this.emotionName = name; this.color = color; }
    }

    enum AvatarState { ACTIVE, SLEEPING, LISTENING, PROCESSING, ALERT, ERROR, INITIALIZING }

    // ---------- Data classes ----------
    static class ChatMessage {
        String role, text;
        Emotion emotion = Emotion.NEUTRAL;
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, Object> dataPoints;
    }

    // ---------- Joint System (generates Three.js HTML) ----------
    static class JointSystem {
        Map<String, Joint> joints = new LinkedHashMap<>();
        Map<String, Point2D> limbPositions = new LinkedHashMap<>();
        double breathPhase = 0, idleMovementTimer = 0;
        String currentPose = "idle";
        long lastUpdate = System.currentTimeMillis();

        JointSystem() {
            joints.put("head", new Joint(0, 0.1));
            joints.put("neck", new Joint(0, 0.08));
            joints.put("shoulder_left", new Joint(0, 0.07));
            joints.put("shoulder_right", new Joint(0, 0.07));
            joints.put("elbow_left", new Joint(0, 0.09));
            joints.put("elbow_right", new Joint(0, 0.09));
            joints.put("wrist_left", new Joint(0, 0.12));
            joints.put("wrist_right", new Joint(0, 0.12));
            joints.put("torso", new Joint(0, 0.05));

            limbPositions.put("head", new Point2D(0, -20));
            limbPositions.put("neck", new Point2D(0, 0));
            limbPositions.put("shoulder_left", new Point2D(-15, 0));
            limbPositions.put("shoulder_right", new Point2D(15, 0));
            limbPositions.put("elbow_left", new Point2D(-30, 25));
            limbPositions.put("elbow_right", new Point2D(30, 25));
            limbPositions.put("hand_left", new Point2D(-45, 50));
            limbPositions.put("hand_right", new Point2D(45, 50));
            limbPositions.put("torso", new Point2D(0, 15));
        }

        void update(Emotion emotion, boolean isSpeaking) {
            long now = System.currentTimeMillis();
            double dt = Math.min((now - lastUpdate) / 1000.0, 0.033);
            lastUpdate = now;

            breathPhase += dt * 2;
            if (breathPhase > 2 * Math.PI) breathPhase -= 2 * Math.PI;
            double breathOffset = Math.sin(breathPhase) * 0.5;

            String targetPose = getPoseForEmotion(emotion, isSpeaking);
            if (!targetPose.equals(currentPose)) transitionToPose(targetPose);

            idleMovementTimer += dt;
            double idleOffset = Math.sin(idleMovementTimer * 0.5) * 2;

            for (Map.Entry<String, Joint> e : joints.entrySet()) {
                Joint j = e.getValue();
                double diff = j.target - j.angle;
                j.angle += diff * j.speed * dt * 60;
                switch (e.getKey()) {
                    case "head", "neck" -> j.angle += idleOffset * 0.1;
                    case "shoulder_left", "shoulder_right" -> j.angle += breathOffset * 0.2;
                }
            }
            updateLimbPositions();
        }

        String getPoseForEmotion(Emotion em, boolean speaking) {
            if (speaking) return "speaking";
            return switch (em) {
                case HAPPY -> "happy";
                case SAD -> "sad";
                case FOCUSED -> "focused";
                case ANALYZING -> "thinking";
                case DEFENSIVE -> "defensive";
                case EXCITED -> "excited";
                case SURPRISED -> "surprised";
                case CALM -> "calm";
                case SLEEPING -> "sleeping";
                default -> "idle";
            };
        }

        void transitionToPose(String pose) {
            currentPose = pose;
            Map<String, Double> targets = getPoseTargets(pose);
            for (Map.Entry<String, Double> e : targets.entrySet()) {
                if (joints.containsKey(e.getKey())) joints.get(e.getKey()).target = e.getValue();
            }
        }

        Map<String, Double> getPoseTargets(String pose) {
            return switch (pose) {
                case "idle" -> Map.of("head",0.0,"neck",0.0,"shoulder_left",-10.0,"shoulder_right",10.0,"elbow_left",20.0,"elbow_right",20.0,"wrist_left",-5.0,"wrist_right",-5.0,"torso",0.0);
                case "happy" -> Map.of("head",5.0,"neck",0.0,"shoulder_left",-15.0,"shoulder_right",15.0,"elbow_left",30.0,"elbow_right",30.0,"wrist_left",10.0,"wrist_right",10.0,"torso",2.0);
                case "speaking" -> Map.of("head",0.0,"neck",0.0,"shoulder_left",-5.0,"shoulder_right",5.0,"elbow_left",15.0,"elbow_right",15.0,"wrist_left",-10.0,"wrist_right",20.0,"torso",0.0);
                case "thinking" -> Map.of("head",-10.0,"neck",5.0,"shoulder_left",-20.0,"shoulder_right",0.0,"elbow_left",90.0,"elbow_right",45.0,"wrist_left",-30.0,"wrist_right",0.0,"torso",-5.0);
                case "defensive" -> Map.of("head",0.0,"neck",0.0,"shoulder_left",30.0,"shoulder_right",30.0,"elbow_left",60.0,"elbow_right",60.0,"wrist_left",0.0,"wrist_right",0.0,"torso",0.0);
                case "sleeping" -> Map.of("head",-30.0,"neck",10.0,"shoulder_left",-5.0,"shoulder_right",5.0,"elbow_left",10.0,"elbow_right",10.0,"wrist_left",0.0,"wrist_right",0.0,"torso",5.0);
                default -> getPoseTargets("idle");
            };
        }

        void updateLimbPositions() {
            double headAngle = Math.toRadians(joints.get("head").angle);
            double neckAngle = Math.toRadians(joints.get("neck").angle);
            limbPositions.get("head").x = Math.sin(headAngle + neckAngle) * 20;
            limbPositions.get("head").y = -20 + Math.cos(headAngle + neckAngle) * 5;

            for (String side : List.of("left", "right")) {
                String sh = "shoulder_" + side, el = "elbow_" + side, wr = "wrist_" + side, ha = "hand_" + side;
                double shAng = Math.toRadians(joints.get(sh).angle);
                double elAng = Math.toRadians(joints.get(el).angle);
                double wrAng = Math.toRadians(joints.get(wr).angle);
                double dir = side.equals("right") ? 1 : -1;
                limbPositions.get(el).x = limbPositions.get(sh).x + Math.cos(shAng) * 25 * dir;
                limbPositions.get(el).y = limbPositions.get(sh).y + Math.sin(shAng) * 25;
                limbPositions.get(ha).x = limbPositions.get(el).x + Math.cos(elAng + shAng) * 25 * dir;
                limbPositions.get(ha).y = limbPositions.get(el).y + Math.sin(elAng + shAng) * 25;
            }
            double torsoAngle = Math.toRadians(joints.get("torso").angle);
            limbPositions.get("torso").y = 15 + Math.sin(torsoAngle) * 3;
        }

        /** Generates HTML containing Three.js scene for the 3D avatar. */
        String get3DAvatarHtml(Emotion emotion, boolean isSpeaking) {
            // Build a HTML page with embedded Three.js from CDN and animation script
            StringBuilder sb = new StringBuilder();
            sb.append("""
                <!DOCTYPE html><html><head>
                <meta charset="utf-8">
                <style>body{margin:0;overflow:hidden;background:transparent;}</style>
                </head><body>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
                <script>
                const scene = new THREE.Scene();
                scene.background = null;
                const camera = new THREE.PerspectiveCamera(60, 1, 0.1, 1000);
                const renderer = new THREE.WebGLRenderer({antialias:true,alpha:true});
                renderer.setSize(200,300);
                document.body.appendChild(renderer.domElement);
                
                const ambientLight = new THREE.AmbientLight(0xffffff,0.6);
                scene.add(ambientLight);
                const directionalLight = new THREE.DirectionalLight(0xffffff,0.8);
                directionalLight.position.set(5,5,5);
                scene.add(directionalLight);
                
                // materials
                const skinMat = new THREE.MeshStandardMaterial({color:0xffccaa,roughness:0.6});
                const clothMat = new THREE.MeshStandardMaterial({color:'""").append(emotion.color).append("'").append(",roughness:0.7,metalness:0.1});\n");
            // (Body parts omitted for brevity – same as Python JS code)
            sb.append("""
                // Torso
                const torsoGeo = new THREE.CylinderGeometry(5,5,20,32);
                const torso = new THREE.Mesh(torsoGeo, clothMat);
                torso.position.y = 0;
                scene.add(torso);
                // Head
                const headGeo = new THREE.SphereGeometry(6,32,32);
                const head = new THREE.Mesh(headGeo, skinMat);
                head.position.y = 15;
                scene.add(head);
                // Neck
                const neckGeo = new THREE.CylinderGeometry(3,3,5,32);
                const neck = new THREE.Mesh(neckGeo, skinMat);
                neck.position.y = 10;
                scene.add(neck);
                // Left arm
                const shoulderLGeo = new THREE.CylinderGeometry(2,2,10,32);
                const shoulderL = new THREE.Mesh(shoulderLGeo, skinMat);
                shoulderL.position.set(-5,5,0);
                scene.add(shoulderL);
                const elbowLGeo = new THREE.CylinderGeometry(2,2,10,32);
                const elbowL = new THREE.Mesh(elbowLGeo, skinMat);
                elbowL.position.set(-10,0,0);
                scene.add(elbowL);
                // Right arm
                const shoulderRGeo = new THREE.CylinderGeometry(2,2,10,32);
                const shoulderR = new THREE.Mesh(shoulderRGeo, skinMat);
                shoulderR.position.set(5,5,0);
                scene.add(shoulderR);
                const elbowRGeo = new THREE.CylinderGeometry(2,2,10,32);
                const elbowR = new THREE.Mesh(elbowRGeo, skinMat);
                elbowR.position.set(10,0,0);
                scene.add(elbowR);
                
                camera.position.z = 45;
                camera.position.y = 5;
                
                const emotion = '""").append(emotion.emotionName).append("'").append(";\n");
            sb.append("const isSpeaking = ").append(isSpeaking).append(";\n");
            sb.append("""
                let time = 0;
                function animate(){
                    requestAnimationFrame(animate);
                    time += 0.01;
                    let amp = 1, bSpeed = 2;
                    switch(emotion){
                        case 'excited': case 'happy': amp=1.5; bSpeed=3; break;
                        case 'sad': case 'sleeping': amp=0.5; bSpeed=1; break;
                        case 'defensive': case 'intense': amp=1.2; bSpeed=2.5; break;
                    }
                    // breathing
                    torso.scale.y = 1 + Math.sin(time*bSpeed)*0.05*amp;
                    torso.rotation.y = Math.sin(time*bSpeed/2)*0.05*amp;
                    // head
                    head.rotation.y = Math.sin(time*0.5)*0.2*amp;
                    head.rotation.x = Math.cos(time*0.3)*0.1*amp;
                    // arms
                    shoulderL.rotation.z = angles.shoulder_left + Math.sin(time*1.5)*0.3*amp;
                    shoulderR.rotation.z = angles.shoulder_right + Math.sin(time*1.5+Math.PI)*0.3*amp;
                    elbowL.rotation.z = angles.elbow_left + Math.sin(time*2)*0.2*amp;
                    elbowR.rotation.z = angles.elbow_right + Math.sin(time*2+Math.PI/2)*0.2*amp;
                    if(isSpeaking){
                        head.rotation.x += Math.sin(time*10)*0.05;
                        shoulderL.rotation.z += Math.sin(time*5)*0.1;
                        shoulderR.rotation.z += Math.cos(time*5)*0.1;
                    }
                    renderer.render(scene, camera);
                }
                // initial joint angles from Java
                const angles = {
                    head: 0, neck:0, shoulder_left:-0.1745, shoulder_right:0.1745,
                    elbow_left:0.349, elbow_right:0.349, wrist_left:-0.087, wrist_right:-0.087, torso:0
                };
                animate();
                </script></body></html>""");
            return sb.toString();
        }

        static class Joint { double angle, target, speed; Joint(double a, double s) { angle=a; target=a; speed=s; } }
        static class Point2D { double x, y; Point2D(double x, double y) { this.x=x; this.y=y; } }
    }

    // ---------- Particle System (generates CSS for particles) ----------
    static class ParticleSystem {
        List<Particle> particles = new ArrayList<>();
        int max = 200;
        long lastUpdate = System.currentTimeMillis();
        void emit(double x, double y, int count, String color, String symbol, double vxRange, double vyRange) {
            Random rand = new Random();
            for (int i=0; i<count && particles.size()<max; i++) {
                Particle p = new Particle();
                p.x = x; p.y = y;
                p.vx = rand.nextDouble()*vxRange*2 - vxRange;
                p.vy = rand.nextDouble()*vyRange*2 - vyRange;
                p.life = rand.nextDouble()*0.5+0.5;
                p.maxLife = p.life;
                p.color = color; p.symbol = symbol; p.size = rand.nextDouble()*0.5+0.5;
                particles.add(p);
            }
        }
        void update(double dt) {
            long now = System.currentTimeMillis();
            dt = Math.min((now - lastUpdate)/1000.0, 0.033);
            lastUpdate = now;
            for (Iterator<Particle> it = particles.iterator(); it.hasNext();) {
                Particle p = it.next();
                p.x += p.vx * dt * 60;
                p.y += p.vy * dt * 60;
                p.life -= dt;
                p.vy += 0.1 * dt * 60;
                if (p.life < p.maxLife * 0.3) { p.vx *= 0.9; p.vy *= 0.9; }
                if (p.life <= 0) it.remove();
            }
        }
        String renderHtml() {
            if (particles.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for (Particle p : particles) {
                double opacity = p.life / p.maxLife;
                double size = p.size * (0.5 + opacity*0.5);
                sb.append(String.format(
                    "<div style='position:absolute;left:%fpx;top:%fpx;color:%s;opacity:%f;font-size:%frem;transform:translate(-50%,-50%);pointer-events:none;'>%s</div>",
                    p.x, p.y, p.color, opacity, size, p.symbol));
            }
            return sb.toString();
        }
        static class Particle { double x,y,vx,vy,life,maxLife,size; String color,symbol; }
    }

    // ---------- File System Analyzer (SistemaArquivos) ----------
    static class FileAnalyzer {
        String basePath;
        List<Map<String,Object>> projectStructure;
        List<Map<String,Object>> tradingFiles;
        Map<String,Object> projectMetrics;
        LocalDateTime lastAnalysis;

        FileAnalyzer() {
            basePath = System.getProperty("user.dir"); // default
        }
        void scanProject() {
            projectStructure = new ArrayList<>();
            projectMetrics = new HashMap<>();
            try {
                Files.walk(Paths.get(basePath), 3)
                    .forEach(p -> {
                        Map<String,Object> info = new HashMap<>();
                        info.put("path", p.toString());
                        info.put("type", p.toFile().isDirectory() ? "dir" : "file");
                        projectStructure.add(info);
                    });
            } catch (IOException e) { LOG.warning("Scan error: " + e.getMessage()); }
            analyzeTradingFiles();
            lastAnalysis = LocalDateTime.now();
        }
        void analyzeTradingFiles() {
            tradingFiles = new ArrayList<>();
            // simplified keyword search
            // ... (implementation similar to Python)
        }
        String generateReport() { return "Report placeholder"; }
    }

    // ---------- TTS Service (using FreeTTS or javax.speech) ----------
    static class TTSService {
        boolean muted = false;
        // Using FreeTTS (com.sun.speech.freetts) if available
        private com.sun.speech.freetts.Voice voice;
        TTSService() {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            try {
                voice = com.sun.speech.freetts.VoiceManager.getInstance().getVoice("kevin16");
                if (voice != null) voice.allocate();
            } catch (Exception e) { LOG.warning("FreeTTS not available"); }
        }
        void speak(String text, Emotion emotion) {
            if (muted || voice == null) return;
            new Thread(() -> {
                voice.speak(text);
            }).start();
        }
        void stop() { if (voice != null) voice.deallocate(); }
    }

    // ---------- Market Data Simulator ----------
    static class MarketSimulator {
        Map<String, Deque<Double>> prices = new ConcurrentHashMap<>();
        Map<String, Deque<Integer>> volumes = new ConcurrentHashMap<>();
        Map<String, Integer> trends = new ConcurrentHashMap<>();

        MarketSimulator() {
            for (String sym : List.of("NASDAQ","S&P500","DOW","BTC")) {
                Deque<Double> p = new ArrayDeque<>();
                for (int i=0;i<100;i++) p.add(100.0 + Math.random()*10);
                prices.put(sym, p);
                Deque<Integer> v = new ArrayDeque<>();
                for (int i=0;i<50;i++) v.add(1000 + new Random().nextInt(9000));
                volumes.put(sym, v);
                trends.put(sym, 0);
            }
        }
        void update() { /* similar to Python */ }
    }

    // ---------- Interaction System ----------
    static class InteractionSystem {
        FileAnalyzer files;
        TTSService tts;
        Consumer<String> displayCallback; // to show message on UI
        Deque<Map<String,String>> history = new ArrayDeque<>();
        String userName;

        InteractionSystem(FileAnalyzer fa, TTSService tts) {
            this.files = fa;
            this.tts = tts;
        }
        String processCommand(String text) {
            // similar logic to Python, calling tts.speak() and displayCallback
            return "Resposta: " + text;
        }
    }

    // ---------- JavaFX Application ----------
    private DraggableAvatar avatar;
    private WebView webView;
    private WebEngine webEngine;
    private Label emotionLabel;
    private TextField commandInput;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // WebView for 3D avatar
        webView = new WebView();
        webEngine = webView.getEngine();
        webView.setPrefSize(250, 350);
        // Load initial HTML (empty)
        webEngine.loadContent("<html><body style='background:transparent;'></body></html>");

        // Controls panel
        VBox controls = new VBox(10);
        controls.setAlignment(Pos.CENTER);
        emotionLabel = new Label("NEUTRAL");
        emotionLabel.setTextFill(Color.CYAN);
        commandInput = new TextField();
        commandInput.setPromptText("Digite um comando...");
        Button sendBtn = new Button("Enviar");
        sendBtn.setOnAction(e -> {
            String cmd = commandInput.getText();
            if (!cmd.isEmpty()) {
                String resp = avatar.interaction.processCommand(cmd);
                avatar.showMessage(resp, Emotion.HAPPY);
                commandInput.clear();
            }
        });
        controls.getChildren().addAll(emotionLabel, commandInput, sendBtn);

        root.setCenter(webView);
        root.setBottom(controls);

        Scene scene = new Scene(root, 400, 600);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("SYLPH AI Avatar");
        stage.setScene(scene);
        stage.show();

        // Initialize avatar
        avatar = new DraggableAvatar(webEngine);
        avatar.startAutoUpdate();
    }

    // ---------- Avatar Manager ----------
    static class DraggableAvatar {
        JointSystem joints = new JointSystem();
        ParticleSystem particles = new ParticleSystem();
        TTSService tts = new TTSService();
        FileAnalyzer files = new FileAnalyzer();
        InteractionSystem interaction;
        Emotion currentEmotion = Emotion.NEUTRAL;
        boolean isSpeaking = false;
        WebEngine webEngine;

        DraggableAvatar(WebEngine engine) {
            this.webEngine = engine;
            this.interaction = new InteractionSystem(files, tts);
            files.scanProject();
        }

        void startAutoUpdate() {
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    joints.update(currentEmotion, isSpeaking);
                    String html = joints.get3DAvatarHtml(currentEmotion, isSpeaking);
                    Platform.runLater(() -> webEngine.loadContent(html));
                }
            };
            timer.start();
        }

        void showMessage(String text, Emotion emotion) {
            currentEmotion = emotion;
            isSpeaking = true;
            tts.speak(text, emotion);
            // Update UI label
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
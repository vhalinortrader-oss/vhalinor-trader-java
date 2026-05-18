package br.com.vhalinor.iag.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleDiscovery {
    private static final Logger log = LoggerFactory.getLogger(ModuleDiscovery.class);
    private final Path projectRoot;
    private final Map<String, ModuleInfo> modules = new ConcurrentHashMap<>();

    public ModuleDiscovery(Path root) {
        this.projectRoot = root;
    }

    public void scan() throws IOException {
        Files.walk(projectRoot)
            .filter(p -> p.toString().endsWith(".class") && !p.toString().contains("$"))
            .forEach(this::registerClass);
        log.info("{} módulos encontrados", modules.size());
    }

    private void registerClass(Path classFile) {
        String className = projectRoot.relativize(classFile).toString()
            .replace(File.separatorChar, '.')
            .replace(".class", "");
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Module.class)) {
                modules.put(className, new ModuleInfo(clazz));
                log.debug("Módulo encontrado: {}", className);
            }
        } catch (ClassNotFoundException e) {
            // ignora
        }
    }

    public List<String> getModulesByCategory(String category) {
        return modules.entrySet().stream()
            .filter(e -> e.getValue().category().equals(category))
            .map(Map.Entry::getKey)
            .toList();
    }

    public record ModuleInfo(Class<?> clazz, String category) {
        public ModuleInfo(Class<?> clazz) {
            this(clazz, clazz.getAnnotation(Module.class).category());
        }
    }

    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Module {
        String category() default "default";
    }
}
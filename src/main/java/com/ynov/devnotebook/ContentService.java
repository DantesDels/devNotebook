package com.ynov.devnotebook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ContentService: loads default HTML content from classpath and manages user overrides under ~/.dev-notebook.
 * - getContent: override if present, otherwise default resource /content/<key>.html
 * - saveContent: writes UTF-8 to ~/.dev-notebook/<key>.html (creates directory on first use)
 * - deleteOverride: removes override file if present
 */
public final class ContentService {
    private static final Logger LOGGER = Logger.getLogger(ContentService.class.getName());

    public static final String KEY_READABILITY = "readability";
    public static final String KEY_SECURITY = "security";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_REFACTORING = "refactoring";
    public static final String KEY_SCALABILITY = "scalability";

    public String getContent(String sectionKey) {
        Objects.requireNonNull(sectionKey, "sectionKey");
        // Check override first
        Path override = PathsUtil.getOverrideFile(sectionKey);
        if (Files.exists(override)) {
            try {
                String s = Files.readString(override, StandardCharsets.UTF_8);
                LOGGER.fine(() -> "Loaded override for " + sectionKey + " from " + override);
                return s;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to read override for " + sectionKey + ": " + e.getMessage(), e);
            }
        }
        // Fallback to default resource
        String resourcePath = "/content/" + sectionKey + ".html";
        try (InputStream is = ContentService.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                LOGGER.warning("Default resource not found for " + sectionKey + ": " + resourcePath);
                return "<html><body><h1>Contenu indisponible</h1></body></html>";
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading default resource for " + sectionKey + ": " + e.getMessage(), e);
            return "<html><body><h1>Erreur de chargement</h1></body></html>";
        }
    }

    public void saveContent(String sectionKey, String html) throws IOException {
        Objects.requireNonNull(sectionKey, "sectionKey");
        Objects.requireNonNull(html, "html");
        Path dir = PathsUtil.getUserOverrideDir();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        Path file = PathsUtil.getOverrideFile(sectionKey);
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            bw.write(html);
        }
        LOGGER.info(() -> "Saved override for " + sectionKey + " at " + file);
    }

    public void deleteOverride(String sectionKey) throws IOException {
        Objects.requireNonNull(sectionKey, "sectionKey");
        Path file = PathsUtil.getOverrideFile(sectionKey);
        if (Files.exists(file)) {
            Files.delete(file);
            LOGGER.info(() -> "Deleted override for " + sectionKey + " at " + file);
        }
    }
}
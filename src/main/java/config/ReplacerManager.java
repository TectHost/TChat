package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReplacerManager {

    private final ConfigFile replacerConfig;
    private Map<String, Replacement> replacements;
    private static boolean replacerEnabled;

    public ReplacerManager(TChat plugin) {
        this.replacerConfig = new ConfigFile("replacer.yml", null, plugin);
        this.replacerConfig.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = replacerConfig.getConfig();
        loadReplacements(config);
    }

    public void reloadConfig() {
        replacerConfig.reloadConfig();
        loadConfig();
    }

    private void loadReplacements(@NotNull FileConfiguration config) {
        replacements = new HashMap<>();
        replacerEnabled = config.getBoolean("replacer_enabled", false);

        if (config.getConfigurationSection("words") == null) {
            return;
        }

        for (String key : Objects.requireNonNull(config.getConfigurationSection("words")).getKeys(false)) {
            String path = "words." + key;
            String original = config.getString(path + ".original");
            String replace = config.getString(path + ".replace");
            String permission = config.getString(path + ".permission");

            replacements.put(original, new Replacement(original, replace, permission));
        }
    }

    public String replaceWords(String message, Player player) {
        for (Replacement replacement : replacements.values()) {
            if ("none".equals(replacement.getPermission()) || player.hasPermission(replacement.getPermission())) {
                message = message.replaceAll("\\b" + replacement.getOriginal() + "\\b", replacement.getReplace());
            }
        }
        return message;
    }

    public boolean getReplacerEnabled() {
        return replacerEnabled;
    }

    public void addReplacement(String original, String replace, String permission) {
        replacements.put(original, new Replacement(original, replace, permission));
        replacerConfig.getConfig().set("words." + original + ".original", original);
        replacerConfig.getConfig().set("words." + original + ".replace", replace);
        replacerConfig.getConfig().set("words." + original + ".permission", permission);
        replacerConfig.saveConfig();
    }

    public void removeReplacement(String original) {
        replacements.remove(original);
        replacerConfig.getConfig().set("words." + original, null);
        replacerConfig.saveConfig();
    }

    public Map<String, Replacement> getReplacements() {
        return replacements;
    }

    public void setReplacerEnabled(boolean enabled) {
        replacerEnabled = enabled;
        replacerConfig.getConfig().set("replacer_enabled", enabled);
        replacerConfig.saveConfig();
    }

    public static class Replacement {
        private final String original;
        private final String replace;
        private final String permission;

        public Replacement(String original, String replace, String permission) {
            this.original = original;
            this.replace = replace;
            this.permission = permission;
        }

        public String getOriginal() {
            return original;
        }

        public String getReplace() {
            return replace;
        }

        public String getPermission() {
            return permission;
        }
    }
}

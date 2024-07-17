package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReplacerManager {

    private final TChat plugin;
    private FileConfiguration replacerConfig;
    private Map<String, Replacement> replacements;
    private static boolean replacerEnabled;

    public ReplacerManager(TChat plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        File replacerFile = new File(plugin.getDataFolder(), "replacer.yml");
        if (!replacerFile.exists()) {
            plugin.saveResource("replacer.yml", false);
        }
        replacerConfig = YamlConfiguration.loadConfiguration(replacerFile);
        loadReplacements();
    }

    public void reloadConfig() {
        loadConfig();
    }

    private void loadReplacements() {
        replacements = new HashMap<>();
        replacerEnabled = replacerConfig.getBoolean("replacer_enabled");
        for (String key : Objects.requireNonNull(replacerConfig.getConfigurationSection("words")).getKeys(false)) {
            String original = replacerConfig.getString("words." + key + ".original");
            String replace = replacerConfig.getString("words." + key + ".replace");
            String permission = replacerConfig.getString("words." + key + ".permission");
            replacements.put(original, new Replacement(original, replace, permission));
        }
    }

    public Map<String, Replacement> getReplacements() {
        return replacements;
    }

    public String replaceWords(String message, Player player) {
        for (Replacement replacement : replacements.values()) {
            if ("none".equals(replacement.getPermission()) || player.hasPermission(replacement.getPermission())) {
                message = message.replace(replacement.getOriginal(), replacement.getReplace());
            }
        }
        return message;
    }

    public boolean getReplacerEnabled() {
        return replacerEnabled;
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

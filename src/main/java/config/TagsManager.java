package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TagsManager {

    private final ConfigFile configFile;

    private boolean tagsMenusEnabled;
    private final Map<String, Tag> tags = new HashMap<>();

    public TagsManager(TChat plugin) {
        this.configFile = new ConfigFile("tags.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        tags.clear();

        tagsMenusEnabled = config.getBoolean("tags.menus-enabled", true);
        if (config.isConfigurationSection("tags")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("tags")).getKeys(false)) {
                String permission = config.getString("tags." + key + ".permission", "");
                String display = config.getString("tags." + key + ".display", "");

                tags.put(key, new Tag(permission, display));
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isTagsMenusEnabled() {return tagsMenusEnabled;}
    public Map<String, Tag> getTags() {
        return tags;
    }

    public static class Tag {
        private final String permission;
        private final String display;

        public Tag(String permission, String display) {
            this.permission = permission;
            this.display = display;
        }

        public String getPermission() {
            return permission;
        }

        public String getDisplay() {
            return display;
        }
    }
}

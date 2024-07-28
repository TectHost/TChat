package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class WorldsManager {

    private final ConfigFile configFile;
    private final Map<String, Boolean> worldsConfig;

    public WorldsManager(TChat plugin) {
        this.configFile = new ConfigFile("worlds.yml", null, plugin);
        this.configFile.registerConfig();
        this.worldsConfig = new HashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        worldsConfig.clear();
        for (String worldName : Objects.requireNonNull(config.getConfigurationSection("worlds")).getKeys(false)) {
            boolean chatEnabled = config.getBoolean("worlds." + worldName + ".chat-enabled");
            worldsConfig.put(worldName, chatEnabled);
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public Map<String, Boolean> getWorldsConfig() {
        return worldsConfig;
    }
}
package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WorldsManager {

    private final ConfigFile configFile;
    private final Map<String, WorldConfigData> worldsConfig;
    private final Map<String, BridgeConfigData> bridgesConfig;

    public WorldsManager(TChat plugin) {
        this.configFile = new ConfigFile("worlds.yml", null, plugin);
        this.configFile.registerConfig();
        this.worldsConfig = new HashMap<>();
        this.bridgesConfig = new HashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        worldsConfig.clear();
        bridgesConfig.clear();

        for (String worldName : Objects.requireNonNull(config.getConfigurationSection("worlds")).getKeys(false)) {
            boolean chatEnabled = config.getBoolean("worlds." + worldName + ".chat-enabled");
            boolean pwc = config.getBoolean("worlds." + worldName + ".pwc");
            boolean chatrEnabled = config.getBoolean("worlds." + worldName + ".chat-radius.enabled");
            int chatr = config.getInt("worlds." + worldName + ".chat-radius.radius");
            worldsConfig.put(worldName, new WorldConfigData(chatEnabled, pwc, chatrEnabled, chatr));
        }

        for (String bridgeName : Objects.requireNonNull(config.getConfigurationSection("bridges")).getKeys(false)) {
            boolean enabled = config.getBoolean("bridges." + bridgeName + ".enabled");
            List<String> worlds = config.getStringList("bridges." + bridgeName + ".worlds");
            bridgesConfig.put(bridgeName, new BridgeConfigData(enabled, worlds));
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public Map<String, WorldConfigData> getWorldsConfig() {
        return worldsConfig;
    }

    public Map<String, BridgeConfigData> getBridgesConfig() {
        return bridgesConfig;
    }

    public record WorldConfigData(boolean chatEnabled, boolean pwc, boolean chatrEnabled, int chatr) {
    }

    public record BridgeConfigData(boolean enabled, List<String> worlds) {
    }
}

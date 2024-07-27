package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeathManager {

    private final ConfigFile configFile;
    private final TChat plugin;
    private final Map<String, String> deathMessages = new HashMap<>();

    public DeathManager(TChat plugin){
        this.configFile = new ConfigFile("death.yml", null, plugin);
        this.plugin = plugin;
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();
        if (config.contains("death-messages")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("death-messages")).getKeys(false)) {
                String message = config.getString("death-messages." + key);
                if (message != null) {
                    deathMessages.put(key, ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getDeathMessage(String key) {
        return deathMessages.getOrDefault(key, "Message not found (DeathManager)");
    }
}

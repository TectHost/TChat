package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class DiscordManager {
    private final ConfigFile configFile;
    private boolean discordEnabled;
    private String discordHook;

    public DiscordManager(TChat plugin) {
        this.configFile = new ConfigFile("discord.yml", "hooks", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        discordEnabled = config.getBoolean("discord.enabled");
        if (discordEnabled) {
            discordHook = config.getString("discord.webhook");
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public String getDiscordHook() { return discordHook; }
    public boolean isDiscordEnabled() { return discordEnabled; }
}

package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class DiscordManager {
    private final ConfigFile configFile;
    private boolean discordEnabled;
    private String discordHook;

    private boolean joinEnabled;
    private String joinTitle;
    private String joinDescription;
    private int joinColor;
    private boolean joinAvatarEnabled;

    private boolean quitEnabled;
    private String quitTitle;
    private String quitDescription;
    private int quitColor;
    private boolean quitAvatarEnabled;

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

            joinEnabled = config.getBoolean("discord.join.enabled");
            joinTitle = config.getString("discord.join.title");
            joinDescription = config.getString("discord.join.description");
            joinColor = config.getInt("discord.join.color");
            joinAvatarEnabled = config.getBoolean("discord.join.avatar-enabled");

            quitEnabled = config.getBoolean("discord.quit.enabled");
            quitTitle = config.getString("discord.quit.title");
            quitDescription = config.getString("discord.quit.description");
            quitColor = config.getInt("discord.quit.color");
            quitAvatarEnabled = config.getBoolean("discord.quit.avatar-enabled");
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public String getDiscordHook() {
        return discordHook;
    }

    public boolean isDiscordEnabled() {
        return discordEnabled;
    }

    public boolean isJoinEnabled() {
        return joinEnabled;
    }

    public String getJoinTitle() {
        return joinTitle;
    }

    public String getJoinDescription() {
        return joinDescription;
    }

    public int getJoinColor() {
        return joinColor;
    }

    public boolean isJoinAvatarEnabled() {
        return joinAvatarEnabled;
    }

    public boolean isQuitEnabled() {
        return quitEnabled;
    }

    public String getQuitTitle() {
        return quitTitle;
    }

    public String getQuitDescription() {
        return quitDescription;
    }

    public int getQuitColor() {
        return quitColor;
    }

    public boolean isQuitAvatarEnabled() {
        return quitAvatarEnabled;
    }
}
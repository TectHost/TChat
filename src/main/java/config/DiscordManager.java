package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class DiscordManager {

    private final ConfigFile configFile;
    private final TChat plugin;

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

    private boolean deathEnabled;
    private String deathTitle;
    private String deathDescription;
    private int deathColor;
    private boolean deathAvatarEnabled;

    private String bannedWordsTitle;
    private String bannedWordsDescription;
    private int bannedWordsColor;
    private boolean bannedWordsAvatar;

    public DiscordManager(TChat plugin) {
        this.plugin = plugin;
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
            if (joinEnabled) {
            joinTitle = config.getString("discord.join.title");
            joinDescription = config.getString("discord.join.description");
            joinColor = config.getInt("discord.join.color");
            joinAvatarEnabled = config.getBoolean("discord.join.avatar-enabled");
            }

            quitEnabled = config.getBoolean("discord.quit.enabled");
            if (quitEnabled) {
                quitTitle = config.getString("discord.quit.title");
                quitDescription = config.getString("discord.quit.description");
                quitColor = config.getInt("discord.quit.color");
                quitAvatarEnabled = config.getBoolean("discord.quit.avatar-enabled");
            }

            deathEnabled = config.getBoolean("discord.death.enabled");
            if (deathEnabled) {
                deathTitle = config.getString("discord.death.title");
                deathDescription = config.getString("discord.death.description");
                deathColor = config.getInt("discord.death.color");
                deathAvatarEnabled = config.getBoolean("discord.death.avatar-enabled");
            }
        }

        if (plugin.getBannedWordsManager().isDiscordEnabled()) {
            bannedWordsTitle = config.getString("discord.banned-words.title", "Banned Word Detected");
            bannedWordsDescription = config.getString("discord.banned-words.description", "Player %player% used a banned word: %word%\nMessage: \"%message%\"");
            bannedWordsColor = config.getInt("discord.banned-words.color", 15158332);
            bannedWordsAvatar = config.getBoolean("discord.banned-words.avatar-enabled", true);
        }
    }

    public boolean isBannedWordsAvatar() {return bannedWordsAvatar;}
    public String getBannedWordsTitle() {return bannedWordsTitle;}
    public String getBannedWordsDescription() {return bannedWordsDescription;}
    public int getBannedWordsColor() {return bannedWordsColor;}

    public boolean isDeathEnabled() {
        return deathEnabled;
    }

    public boolean isDeathAvatarEnabled() {
        return deathAvatarEnabled;
    }

    public int getDeathColor() {
        return deathColor;
    }

    public String getDeathDescription() {
        return deathDescription;
    }

    public String getDeathTitle() {
        return deathTitle;
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
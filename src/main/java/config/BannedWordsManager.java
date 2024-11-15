package config;

import minealex.tchat.TChat;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class BannedWordsManager {

    private final TChat plugin;
    private final ConfigFile bannedWordsFile;

    private List<String> bannedWords;
    private List<String> whitelist;
    private String type;

    private List<String> blockedMessages;

    private boolean titleEnabled;
    private String title;
    private String subTitle;

    private boolean actionBarEnabled;
    private String actionBar;

    private boolean soundEnabled;
    private String sound;

    private String bypassPermission;

    private boolean particlesEnabled;
    private Particle particle;
    private int particles;

    private boolean discordEnabled;
    private String discordURL;

    public BannedWordsManager(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.bannedWordsFile = new ConfigFile("banned_words.yml", "modules", plugin);
        this.bannedWordsFile.registerConfig();
        loadBannedWords();
    }

    public void reloadBannedWords() {
        bannedWordsFile.reloadConfig();
        loadBannedWords();
    }

    public void saveBannedWords() {
        FileConfiguration config = bannedWordsFile.getConfig();
        config.set("bannedWords", bannedWords);
        bannedWordsFile.saveConfig();
    }

    public void loadBannedWords() {
        FileConfiguration config = bannedWordsFile.getConfig();

        bannedWords = config.getStringList("bannedWords");

        whitelist = config.getStringList("whitelist");

        type = config.getString("type");

        blockedMessages = config.getStringList("blockedMessage");

        titleEnabled = config.getBoolean("title.enabled");
        if (titleEnabled) {
            title = config.getString("title.title");
            subTitle = config.getString("title.subtitle");
        }

        actionBarEnabled = config.getBoolean("actionbar.enabled");
        if (actionBarEnabled) {
            actionBar = config.getString("actionbar.bar");
        }

        soundEnabled = config.getBoolean("sound.enabled");
        if (soundEnabled) {
            sound = config.getString("sound.sound");
        }

        bypassPermission = config.getString("bypass.permission");

        particlesEnabled = config.getBoolean("particles.enabled");
        if (particlesEnabled) {
            particle = Particle.valueOf(config.getString("particles.particle"));
            particles = config.getInt("particles.particles");
        }

        discordEnabled = config.getBoolean("discord.enabled", false);
        if (discordEnabled) {
            discordURL = config.getString("discord.webhook");
        }
    }

    public boolean isDiscordEnabled() {return discordEnabled;}
    public String getDiscordURL() {return discordURL;}
    public List<String> getWhitelist() {return whitelist;}
    public int getParticles() { return particles; }
    public Particle getParticle() { return particle; }
    public boolean isParticlesEnabled() { return particlesEnabled; }
    public String getBypassPermission() { return bypassPermission; }
    public String getSound() { return sound; }
    public boolean getSoundEnabled() { return soundEnabled; }
    public String getActionBar() { return actionBar; }
    public boolean getActionBarEnabled() { return actionBarEnabled; }
    public List<String> getBannedWords() { return bannedWords; }
    public String getType() { return type; }
    public List<String> getBlockedMessages() { return blockedMessages; }
    public boolean getTitleEnabled() { return titleEnabled; }
    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
    public TChat getPlugin() { return plugin; }
}

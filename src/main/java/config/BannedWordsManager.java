package config;

import minealex.tchat.TChat;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class BannedWordsManager {

    private final TChat plugin;
    private final File bannedWordsFile;
    private boolean enabled;
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

    public BannedWordsManager(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.bannedWordsFile = new File(plugin.getDataFolder(), "banned_words.yml");
        if (!bannedWordsFile.exists()) {
            plugin.saveResource("banned_words.yml", false);
        }
        loadBannedWords();
    }

    public void reloadBannedWords() {
        loadBannedWords();
    }

    public void loadBannedWords() {
        FileConfiguration bannedWordsConfig = YamlConfiguration.loadConfiguration(bannedWordsFile);
        enabled = bannedWordsConfig.getBoolean("options.enabled", true);
        if (enabled) {
            bannedWords = bannedWordsConfig.getStringList("bannedWords");
            whitelist = bannedWordsConfig.getStringList("whitelist");
            type = bannedWordsConfig.getString("type");
            blockedMessages = bannedWordsConfig.getStringList("blockedMessage");
            titleEnabled = bannedWordsConfig.getBoolean("title.enabled");
            if (titleEnabled) {
                title = bannedWordsConfig.getString("title.title");
                subTitle = bannedWordsConfig.getString("title.subtitle");
            }
            actionBarEnabled = bannedWordsConfig.getBoolean("actionbar.enabled");
            if (actionBarEnabled) {
                actionBar = bannedWordsConfig.getString("actionbar.bar");
            }
            soundEnabled = bannedWordsConfig.getBoolean("sound.enabled");
            if (soundEnabled) {
                sound = bannedWordsConfig.getString("sound.sound");
            }
            bypassPermission = bannedWordsConfig.getString("bypass.permission");
            particlesEnabled = bannedWordsConfig.getBoolean("particles.enabled");
            if (particlesEnabled) {
                particle = Particle.valueOf(bannedWordsConfig.getString("particles.particle"));
                particles = bannedWordsConfig.getInt("particles.particles");
            }
        }
    }

    public void saveBannedWords() {
        FileConfiguration bannedWordsConfig = YamlConfiguration.loadConfiguration(bannedWordsFile);
        bannedWordsConfig.set("bannedWords", bannedWords);

        try {
            bannedWordsConfig.save(bannedWordsFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Error saving banned words [BannedWordsManager.java]");
            e.printStackTrace();
        }
    }

    public List<String> getWhitelist() {return whitelist;}
    public boolean isEnabled() {return enabled;}
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

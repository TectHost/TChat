package config;

import minealex.tchat.TChat;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class BannedCommandsManager {

    private final TChat plugin;
    private final ConfigFile configFile;
    private List<String> bannedCommands;
    private List<String> blockedMessage;
    private List<String> noTabCompleteCommands;
    private boolean blockAllCommands;
    private boolean titleEnabled;
    private String title;
    private String subTitle;
    private boolean actionBarEnabled;
    private String actionBar;
    private boolean soundEnabled;
    private String sound;
    private String bypassPermissionCommand;
    private String bypassPermissionTab;
    private boolean particlesEnabled;
    private Particle particle;
    private int particles;

    public BannedCommandsManager(TChat plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("banned_commands.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        bypassPermissionCommand = config.getString("bypass.command.permission");
        bypassPermissionTab = config.getString("bypass.tab.permission");
        bannedCommands = config.getStringList("bannedCommands");
        blockedMessage = config.getStringList("blockedMessage");
        noTabCompleteCommands = config.getStringList("tab.noTabCompleteCommands");
        blockAllCommands = config.getBoolean("tab.block-all-additional-commands");

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
        particlesEnabled = config.getBoolean("particles.enabled");
        if (particlesEnabled) {
            particle = Particle.valueOf(config.getString("particles.particle"));
            particles = config.getInt("particles.particles");
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public int getParticles() { return particles;}
    public Particle getParticle() { return particle; }
    public boolean isParticlesEnabled() { return particlesEnabled; }
    public String getBypassPermissionCommand() { return bypassPermissionCommand; }
    public String getBypassPermissionTab() { return bypassPermissionTab; }
    public String getSound() { return sound; }
    public boolean getSoundEnabled() { return soundEnabled; }
    public String getActionBar() { return actionBar; }
    public boolean getActionBarEnabled() { return actionBarEnabled; }
    public String getSubTitle() { return subTitle; }
    public String getTitle() { return title; }
    public boolean getTitleEnabled() { return titleEnabled; }
    public boolean getBlockAllCommands() { return blockAllCommands; }
    public List<String> getBannedCommands() { return bannedCommands; }
    public List<String> getBlockedMessage() { return blockedMessage; }
    public List<String> getNoTabCompleteCommands() { return noTabCompleteCommands; }
    public TChat getPlugin() { return plugin; }

}

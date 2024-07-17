package config;

import minealex.tchat.TChat;
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

    public BannedCommandsManager(TChat plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("banned_commands.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        bannedCommands = config.getStringList("bannedCommands");
        blockedMessage = config.getStringList("blockedMessage");
        noTabCompleteCommands = config.getStringList("tab.noTabCompleteCommands");
        blockAllCommands = config.getBoolean("tab.block-all-additional-commands");
        titleEnabled = config.getBoolean("title.enabled");
        title = config.getString("title.title");
        subTitle = config.getString("title.subtitle");
        actionBarEnabled = config.getBoolean("actionbar.enabled");
        actionBar = config.getString("actionbar.bar");
        soundEnabled = config.getBoolean("sound.enabled");
        sound = config.getString("sound.sound");
    }

    public String getSound() {
        return sound;
    }

    public boolean getSoundEnabled() {
        return soundEnabled;
    }

    public String getActionBar() {
        return actionBar;
    }

    public boolean getActionBarEnabled() {
        return actionBarEnabled;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getTitle() {
        return title;
    }

    public boolean getTitleEnabled() {
        return titleEnabled;
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean getBlockAllCommands() {
        return blockAllCommands;
    }

    public List<String> getBannedCommands() {
        return bannedCommands;
    }

    public List<String> getBlockedMessage() {
        return blockedMessage;
    }

    public List<String> getNoTabCompleteCommands() {
        return noTabCompleteCommands;
    }

    public TChat getPlugin() {
        return plugin;
    }
}

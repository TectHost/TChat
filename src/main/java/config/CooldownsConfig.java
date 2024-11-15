package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class CooldownsConfig {

    private final ConfigFile configFile;

    private boolean cooldownChat;
    private boolean cooldownCommand;
    private long cooldownChatTime;
    private long cooldownCommandTime;

    private boolean depurationChatEnabled;
    private boolean depurationCommandEnabled;

    public CooldownsConfig(TChat plugin){
        this.configFile = new ConfigFile("cooldowns.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        cooldownChat = config.getBoolean("cooldowns.chat.enabled", true);
        cooldownCommand = config.getBoolean("cooldowns.commands.enabled", true);

        if (cooldownChat) {
            cooldownChatTime = config.getLong("cooldowns.chat.time", 3000);
            depurationChatEnabled = config.getBoolean("cooldowns.chat.depuration-enabled", true);
        }

        if (cooldownCommand) {
            cooldownCommandTime = config.getLong("cooldowns.commands.time", 3000);
            depurationCommandEnabled = config.getBoolean("cooldowns.commands.depuration-enabled", true);
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public long getCooldownCommandTime() { return cooldownCommandTime; }
    public long getCooldownChatTime() { return cooldownChatTime; }
    public boolean isCooldownCommand() { return cooldownCommand; }
    public boolean isCooldownChat() { return cooldownChat; }
    
    public boolean isDepurationCommandEnabled() { return depurationCommandEnabled; }
    public boolean isDepurationChatEnabled() { return depurationChatEnabled; }
}

package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final ConfigFile configFile;
    private String format;
    private boolean formatGroup;
    private boolean registerMessagesOnConsole;

    public ConfigManager(TChat plugin){
        this.configFile = new ConfigFile("config.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();
        format = config.getString("chat.format");
        formatGroup = config.getBoolean("chat.use-group-format");
        registerMessagesOnConsole = config.getBoolean("chat.register-messages-on-console");
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getFormat(){
        return format;
    }

    public boolean getFormatGroup() {
        return formatGroup;
    }

    public boolean getRegisterMessagesOnConsole() {
        return registerMessagesOnConsole;
    }
}


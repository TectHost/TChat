package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class HelpOpConfig {

    private final ConfigFile configFile;

    private String helpOpFormat;

    public HelpOpConfig(TChat plugin){
        this.configFile = new ConfigFile("helpop.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        helpOpFormat = config.getString("helpop.format");
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getHelpOpFormat() { return helpOpFormat; }
}

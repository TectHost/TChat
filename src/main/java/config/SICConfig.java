package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class SICConfig {

    private final ConfigFile configFile;

    private String sicPrefix;
    private String sicSuffix;

    public SICConfig(TChat plugin){
        this.configFile = new ConfigFile("show_item_command.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        sicPrefix = config.getString("sic.prefix", "&aThis is my item: &e");
        sicSuffix = config.getString("sic.suffix", "&a!");
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getSicPrefix() {return sicPrefix;}
    public String getSicSuffix() {return sicSuffix;}
}

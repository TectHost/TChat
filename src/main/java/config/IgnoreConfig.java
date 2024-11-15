package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class IgnoreConfig {

    private final ConfigFile configFile;

    private List<String> blackLIgnore;

    public IgnoreConfig(TChat plugin){
        this.configFile = new ConfigFile("ignore.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        blackLIgnore = config.getStringList("ignore.blacklist");
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public List<String> getBlackLIgnore() {return blackLIgnore;}
}

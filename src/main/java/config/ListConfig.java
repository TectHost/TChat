package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class ListConfig {

    private final ConfigFile configFile;

    private String listHeader;
    private String listAppend;
    private String listFooter;

    public ListConfig(TChat plugin){
        this.configFile = new ConfigFile("list.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        listAppend = config.getString("list.append");
        listFooter = config.getString("list.footer");
        listHeader = config.getString("list.header");
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getListFooter() { return listFooter; }
    public String getListAppend() { return listAppend; }
    public String getListHeader() { return listHeader; }
}

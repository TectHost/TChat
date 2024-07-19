package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class SaveManager {

    private final ConfigFile savesFile;
    private FileConfiguration config;

    public SaveManager(TChat plugin){
        this.savesFile = new ConfigFile("saves.yml", null, plugin);
        this.savesFile.registerConfig();
        this.config = savesFile.getConfig();
        loadConfig();
    }

    public void loadConfig(){
        config = savesFile.getConfig();
    }

    public void reloadConfig(){
        savesFile.reloadConfig();
        loadConfig();
    }

    public String getChatColor(UUID playerId){
        String path = "players." + playerId.toString() + ".chatcolor";
        return config.getString(path, "&7");
    }

    public void setChatColor(UUID playerId, String chatColor){
        String path = "players." + playerId.toString() + ".chatcolor";
        config.set(path, chatColor);
        savesFile.saveConfig();
    }

    public String getFormat(UUID playerId){
        String path = "players." + playerId.toString() + ".format";
        return config.getString(path, "");
    }

    public void setFormat(UUID playerId, String format){
        String path = "players." + playerId.toString() + ".format";
        config.set(path, format);
        savesFile.saveConfig();
    }
}

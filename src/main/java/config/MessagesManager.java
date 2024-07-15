package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesManager {

    private final ConfigFile messagesFile;
    private String noPermission;
    private String versionMessage;
    private String reloadMessage;
    private String unknownMessage;

    public MessagesManager(TChat plugin){
        this.messagesFile = new ConfigFile("messages.yml", null, plugin);
        this.messagesFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = messagesFile.getConfig();
        noPermission = config.getString("messages.no-permission");
        versionMessage = config.getString("messages.version-message");
        reloadMessage = config.getString("messages.reload-message");
        unknownMessage = config.getString("messages.unknown-command");
    }

    public void reloadConfig(){
        messagesFile.reloadConfig();
        loadConfig();
    }

    public String getVersionMessage(){
        return versionMessage;
    }

    public String getNoPermission(){
        return noPermission;
    }

    public String getReloadMessage(){
        return reloadMessage;
    }

    public String getUnknownMessage(){
        return unknownMessage;
    }
}


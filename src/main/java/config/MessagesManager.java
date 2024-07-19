package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesManager {

    private final ConfigFile messagesFile;
    private String noPermission;
    private String versionMessage;
    private String reloadMessage;
    private String unknownMessage;
    private String prefix;
    private String noFormatGroup;
    private String noPlayer;
    private String antiCapMessage;
    private String colorSelectedMessage;
    private String formatSelectedMessage;
    private String invalidIdMessage;
    private String noItemFoundMessage;
    private String materialNotFound;

    public MessagesManager(TChat plugin){
        this.messagesFile = new ConfigFile("messages.yml", null, plugin);
        this.messagesFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = messagesFile.getConfig();
        prefix = config.getString("prefix");

        // Messages
        noItemFoundMessage = config.getString("messages.no-item-found");
        invalidIdMessage = config.getString("messages.invalid-id");
        formatSelectedMessage = config.getString("messages.format-selected");
        noPermission = config.getString("messages.no-permission");
        versionMessage = config.getString("messages.version-message");
        reloadMessage = config.getString("messages.reload-message");
        unknownMessage = config.getString("messages.unknown-command");
        antiCapMessage = config.getString("messages.anticap");
        colorSelectedMessage = config.getString("messages.color-selected");

        // Messages depuration
        materialNotFound = config.getString("messages.depuration.material-not-found");
        noFormatGroup = config.getString("messages.depuration.no-format-group");
        noPlayer = config.getString("messages.depuration.no-player");
    }

    public void reloadConfig(){
        messagesFile.reloadConfig();
        loadConfig();
    }

    // Depuration
    public String getMaterialNotFound() { return materialNotFound; }
    public String getNoPlayer() { return noPlayer; }
    public String getNoFormatGroup() { return noFormatGroup; }

    // Messages
    public String getNoItemFoundMessage() { return noItemFoundMessage; }
    public String getInvalidIdMessage() { return invalidIdMessage; }
    public String getFormatSelectedMessage() { return formatSelectedMessage; }
    public String getColorSelectedMessage() { return colorSelectedMessage; }
    public String getVersionMessage(){ return versionMessage; }
    public String getNoPermission(){ return noPermission; }
    public String getReloadMessage(){ return reloadMessage;}
    public String getUnknownMessage(){ return unknownMessage; }
    public String getPrefix() { return prefix; }
    public String getAntiCapMessage() { return antiCapMessage; }
}


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
    private boolean prefixEnabled;
    private String noFormatGroup;
    private String noPlayer;
    private String antiCapMessage;
    private String colorSelectedMessage;
    private String formatSelectedMessage;
    private String invalidIdMessage;
    private String noItemFoundMessage;
    private String materialNotFound;
    private String usageChannel;
    private String usageJoinChannel;
    private String usageLeaveChannel;
    private String channelNotExist;
    private String channelNoPermissionJoin;
    private String channelAlready;
    private String channelLeft;
    private String channelJoin;
    private String channelLeftAnnounce;
    private String channelJoinAnnounce;
    private String noChannel;
    private String noPermissionChannelLeft;

    public MessagesManager(TChat plugin){
        this.messagesFile = new ConfigFile("messages.yml", null, plugin);
        this.messagesFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = messagesFile.getConfig();
        prefixEnabled = config.getBoolean("prefix.enabled");
        if (prefixEnabled) {
            prefix = config.getString("prefix.prefix");
        } else {
            prefix = "";
        }

        // Messages
        noPermissionChannelLeft = config.getString("messages.channel-no-permission-left");
        noChannel = config.getString("messages.no-channel");
        channelLeftAnnounce = config.getString("messages.left-channel-announce");
        channelJoinAnnounce = config.getString("messages.join-channel-announce");
        channelJoin = config.getString("messages.join-channel");
        channelLeft = config.getString("messages.left-channel");
        channelAlready = config.getString("messages.already-channel");
        channelNoPermissionJoin = config.getString("messages.channel-no-permission-join");
        channelNotExist = config.getString("messages.channel-not-exist");
        usageLeaveChannel = config.getString("messages.usage.leave-channel");
        usageJoinChannel = config.getString("messages.usage.join-channel");
        noItemFoundMessage = config.getString("messages.no-item-found");
        invalidIdMessage = config.getString("messages.invalid-id");
        formatSelectedMessage = config.getString("messages.format-selected");
        noPermission = config.getString("messages.no-permission");
        versionMessage = config.getString("messages.version-message");
        reloadMessage = config.getString("messages.reload-message");
        unknownMessage = config.getString("messages.unknown-command");
        antiCapMessage = config.getString("messages.anticap");
        colorSelectedMessage = config.getString("messages.color-selected");
        usageChannel = config.getString("messages.usage.channel");

        // Messages depuration
        materialNotFound = config.getString("messages.debug.material-not-found");
        noFormatGroup = config.getString("messages.debug.no-format-group");
        noPlayer = config.getString("messages.debug.no-player");
    }

    public void reloadConfig(){
        messagesFile.reloadConfig();
        loadConfig();
    }

    // Messages
    public String getNoPermissionChannelLeft() { return noPermissionChannelLeft; }
    public String getNoChannel() { return noChannel; }
    public String getChannelJoinAnnounce() { return channelJoinAnnounce; }
    public String getChannelLeftAnnounce() { return channelLeftAnnounce; }
    public String getChannelJoin() { return channelJoin; }
    public String getChannelLeft() { return channelLeft; }
    public String getChannelAlready() { return channelAlready; }
    public String getChannelNoPermissionJoin() { return channelNoPermissionJoin; }
    public String getChannelNotExist() { return channelNotExist; }
    public String getUsageLeaveChannel() { return usageLeaveChannel; }
    public String getUsageJoinChannel() { return usageJoinChannel; }
    public String getUsageChannel() { return usageChannel; }
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

    // Depuration
    public String getMaterialNotFound() { return materialNotFound; }
    public String getNoPlayer() { return noPlayer; }
    public String getNoFormatGroup() { return noFormatGroup; }
}


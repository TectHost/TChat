package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class MessagesManager {

    private final ConfigFile messagesFile;
    private final TChat plugin;
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
    private String playerNotFound;
    private String usageMsg;
    private String usageReply;
    private String noReply;
    private String chatClearMessage;
    private String chatMuted;
    private String chatMute;
    private String chatUnmute;
    private String repeatMessage;
    private String antibotChat;
    private String antibotCommand;
    private String antibotJoin;
    private String antibotMoved;
    private String noGames;
    private String noEnabledGames;
    private String noMessages;
    private String timeFinished;
    private String gameWin;

    public MessagesManager(TChat plugin){
        this.messagesFile = new ConfigFile("messages.yml", null, plugin);
        this.plugin = plugin;
        this.messagesFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = messagesFile.getConfig();
        boolean prefixEnabled = config.getBoolean("prefix.enabled");
        if (prefixEnabled) {
            prefix = config.getString("prefix.prefix");
        } else {
            prefix = "";
        }

        if (plugin.getConfigManager().isAntibotEnabled()) {
            if (plugin.getConfigManager().isAntibotChat()) {
                antibotChat = config.getString("messages.antibot.antibot-chat");
            }
            if (plugin.getConfigManager().isAntibotCommand()) {
                antibotCommand = config.getString("messages.antibot.antibot-command");
            }
            if (plugin.getConfigManager().isAntibotJoin()) {
                antibotJoin = config.getString("messages.antibot.antibot-join");
            }
            if (plugin.getConfigManager().isAntibotMoved()) {
                antibotMoved = config.getString("messages.antibot.antibot-moved");
            }
        }

        // Messages
        noGames = config.getString("messages.chatgames.no-games");
        noEnabledGames = config.getString("messages.chatgames.no-enabled-games");
        noMessages = config.getString("messages.chatgames.no-messages");
        timeFinished = config.getString("messages.chatgames.time-finished");
        gameWin = config.getString("messages.chatgames.win");
        repeatMessage = config.getString("messages.repeat-message");
        chatUnmute = config.getString("messages.chat-unmute");
        chatMute = config.getString("messages.chat-mute");
        chatMuted = config.getString("messages.chat-muted");
        chatClearMessage = config.getString("messages.chat-clear");
        noReply = config.getString("messages.no-reply");
        usageMsg = config.getString("messages.usage.usage-msg");
        usageReply = config.getString("messages.usage.usage-reply");
        playerNotFound = config.getString("messages.player-not-found");
        noPermissionChannelLeft = config.getString("messages.channel.channel-no-permission-left");
        noChannel = config.getString("messages.channel.no-channel");
        channelLeftAnnounce = config.getString("messages.channel.left-channel-announce");
        channelJoinAnnounce = config.getString("messages.channel.join-channel-announce");
        channelJoin = config.getString("messages.channel.join-channel");
        channelLeft = config.getString("messages.channel.left-channel");
        channelAlready = config.getString("messages.channel.already-channel");
        channelNoPermissionJoin = config.getString("messages.channel.channel-no-permission-join");
        channelNotExist = config.getString("messages.channel.channel-not-exist");
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
    public String getGameWin() { return gameWin; }
    public String getTimeFinished() { return timeFinished; }
    public String getNoEnabledGames() { return noEnabledGames; }
    public String getNoGames() { return noGames; }
    public String getNoMessages() { return noMessages; }
    public String getAntibotMoved() { return antibotMoved; }
    public String getAntibotJoin() { return antibotJoin; }
    public String getAntibotCommand() { return antibotCommand; }
    public String getAntibotChat() { return antibotChat; }
    public String getRepeatMessage() { return repeatMessage; }
    public String getChatUnmute() { return chatUnmute; }
    public String getChatMute() { return chatMute; }
    public String getChatMuted() { return chatMuted; }
    public String getChatClearMessage() { return chatClearMessage; }
    public String getNoReply() { return noReply; }
    public String getUsageReply() { return usageReply; }
    public String getUsageMsg() { return usageMsg; }
    public String getPlayerNotFound() { return playerNotFound; }
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


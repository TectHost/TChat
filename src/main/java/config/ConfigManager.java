package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    private final ConfigFile configFile;
    private String format;
    private boolean formatGroup;
    private boolean registerMessagesOnConsole;
    private AdvertisingConfig ipv4Config;
    private AdvertisingConfig domainConfig;
    private AdvertisingConfig linksConfig;
    private String advertisingBypass;
    private boolean antiCapEnabled;
    private double antiCapPercent;
    private String antiCapMode;
    private boolean antiCapMessageEnabled;
    private boolean grammarEnabled;
    private boolean grammarCapEnabled;
    private int grammarCapLetters;
    private boolean grammarDotEnabled;
    private String grammarDotCharacter;
    private int grammarMinCharactersCap;
    private int grammarMinCharactersDot;
    private String permissionBypassCap;
    private String permissionBypassFinalDot;
    private boolean msgEnabled;
    private boolean replyEnabled;
    private String msgFormatSender;
    private String msgFormatReceiver;
    private String replyFormatSender;
    private String replyFormatReceiver;
    private String msgPermission;
    private String replyPermission;
    private boolean chatClearEnabled;
    private int messagesChatClear;
    private boolean muteChatEnabled;
    private String bypassMuteChatPermission;
    private String muteChatPermission;
    private boolean repeatMessagesEnabled;
    private double repeatMessagesPercent;
    private String bypassRepeatMessages;
    private boolean antibotEnabled;
    private String antibotBypass;
    private boolean antibotChat;
    private boolean antibotCommand;
    private boolean antibotJoin;
    private boolean antibotMoved;
    private boolean logsChatEnabled;
    private boolean logsCommandEnabled;
    private boolean cooldownChat;
    private boolean cooldownCommand;
    private long cooldownChatTime;
    private long cooldownCommandTime;
    private boolean floodRepeatEnabled;
    private boolean floodPercentEnabled;
    private int charactersFlood;
    private double percentageFlood;
    private boolean unicodeEnabled;
    private boolean unicodeBlockAll;
    private String unicodeMatch;
    private boolean broadcastEnabled;
    private String broadcastFormat;
    private boolean spyEnabled;
    private String spyFormat;
    private boolean ignoreEnabled;
    private String pollFill;
    private String pollEmpty;
    private int pollBar;
    private boolean warningEnabled;
    private String warningFormat;
    private boolean mentionsEnabled;
    private String mentionColor;
    private String mentionCharacter;
    private boolean rulesEnabled;
    private boolean rulesPrefixEnabled;
    private List<String> rulesMessage;
    private boolean printEnabled;
    private boolean pingEnabled;
    private boolean formatEnabled;
    private boolean chatColorEnabled;
    private boolean announcementEnabled;
    private String announcementFormat;
    private boolean depurationChatEnabled;
    private boolean depurationCommandEnabled;
    private boolean depurationAntiFloodEnabled;
    private String helpOpFormat;
    private boolean logBannedCommandsEnabled;

    public ConfigManager(TChat plugin) {
        this.configFile = new ConfigFile("config.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        formatEnabled = config.getBoolean("chat.format-enabled");
        if (formatEnabled) {
            format = config.getString("chat.format");
            formatGroup = config.getBoolean("chat.use-group-format");
        }
        registerMessagesOnConsole = config.getBoolean("chat.register-messages-on-console");

        ipv4Config = new AdvertisingConfig(config, "advertising.ipv4");
        domainConfig = new AdvertisingConfig(config, "advertising.domain");
        linksConfig = new AdvertisingConfig(config, "advertising.links");
        advertisingBypass = config.getString("advertising.bypass");

        ignoreEnabled = config.getBoolean("ignore.enabled");

        chatColorEnabled = config.getBoolean("chat-color.enabled");

        printEnabled = config.getBoolean("print.enabled");

        warningEnabled = config.getBoolean("broadcast.warning.enabled");
        if (warningEnabled) {
            warningFormat = config.getString("broadcast.warning.format");
        }

        broadcastEnabled = config.getBoolean("broadcast.broadcast.enabled");
        if (broadcastEnabled) {
            broadcastFormat = config.getString("broadcast.broadcast.format");
        }

        announcementEnabled = config.getBoolean("broadcast.announcement.enabled");
        if (announcementEnabled) {
            announcementFormat = config.getString("broadcast.announcement.format");
        }

        pingEnabled = config.getBoolean("ping.enabled");

        spyEnabled = config.getBoolean("spy.commands.enabled");
        if (spyEnabled) {
            spyFormat = config.getString("spy.commands.format");
        }

        unicodeEnabled = config.getBoolean("unicode.enabled");
        if (unicodeEnabled) {
            unicodeBlockAll = config.getBoolean("unicode.blockAllNonLatin");
            unicodeMatch = config.getString("unicode.match");
        }

        floodPercentEnabled = config.getBoolean("flood.percent.enabled");
        floodRepeatEnabled = config.getBoolean("flood.repeat.enabled");
        depurationAntiFloodEnabled = config.getBoolean("flood.depuration");
        if (floodRepeatEnabled) {
            charactersFlood = config.getInt("flood.repeat.characters");
        }
        if (floodPercentEnabled) {
            percentageFlood = config.getDouble("flood.percent.percentage");
        }

        cooldownChat = config.getBoolean("cooldowns.chat.enabled");
        cooldownCommand = config.getBoolean("cooldowns.commands.enabled");
        if (cooldownChat) {
            cooldownChatTime = config.getLong("cooldowns.chat.time");
            depurationChatEnabled = config.getBoolean("cooldowns.chat.depuration-enabled");
        }
        if (cooldownCommand) {
            cooldownCommandTime = config.getLong("cooldowns.commands.time");
            depurationCommandEnabled = config.getBoolean("cooldowns.commands.depuration-enabled");
        }

        antiCapEnabled = config.getBoolean("cap.enabled");
        if (antiCapEnabled) {
            antiCapPercent = config.getDouble("cap.percent");
            antiCapMode = config.getString("cap.mode");
            antiCapMessageEnabled = config.getBoolean("cap.message-enabled");
        }

        grammarEnabled = config.getBoolean("grammar.enabled");
        if (grammarEnabled) {
            grammarCapEnabled = config.getBoolean("grammar.cap.enabled");
            grammarDotEnabled = config.getBoolean("grammar.final-dot.enabled");
            repeatMessagesEnabled = config.getBoolean("grammar.repeat-messages.enabled");
            if (grammarDotEnabled) {
                permissionBypassFinalDot = config.getString("grammar.final-dot.bypass-permission");
                grammarMinCharactersDot = config.getInt("grammar.final-dot.min-characters");
                grammarDotCharacter = config.getString("grammar.final-dot.character");
            }
            if (grammarCapEnabled) {
                permissionBypassCap = config.getString("grammar.cap.bypass-permission");
                grammarMinCharactersCap = config.getInt("grammar.cap.min-characters");
                grammarCapLetters = config.getInt("grammar.cap.letters");
            }
            if (repeatMessagesEnabled) {
                repeatMessagesPercent = config.getDouble("grammar.repeat-messages.percent");
                bypassRepeatMessages = config.getString("grammar.repeat-messages.bypass-permission");
            }
        }

        msgEnabled = config.getBoolean("pm.msg.enabled");
        if (msgEnabled) {
            msgFormatSender = config.getString("pm.msg.formats.sender");
            msgFormatReceiver = config.getString("pm.msg.formats.receiver");
            msgPermission = config.getString("pm.msg.permission");
        }

        replyEnabled = config.getBoolean("pm.reply.enabled");
        if (replyEnabled) {
            replyPermission = config.getString("pm.reply.permission");
            replyFormatSender = config.getString("pm.reply.formats.sender");
            replyFormatReceiver = config.getString("pm.reply.formats.receiver");
        }

        chatClearEnabled = config.getBoolean("chat-clear.enabled");
        if (chatClearEnabled) {
            messagesChatClear = config.getInt("chat-clear.for.messages");
        }

        muteChatEnabled = config.getBoolean("mute-chat.enabled");
        if (muteChatEnabled) {
            bypassMuteChatPermission = config.getString("mute-chat.bypass-permission");
            muteChatPermission = config.getString("mute-chat.execute-command-permission");
        }

        antibotEnabled = config.getBoolean("antibot.enabled");
        if (antibotEnabled) {
            antibotBypass = config.getString("antibot.bypass-permission");
            antibotChat = config.getBoolean("antibot.messages.antibot-chat");
            antibotCommand = config.getBoolean("antibot.messages.antibot-command");
            antibotJoin = config.getBoolean("antibot.messages.antibot-join");
            antibotMoved = config.getBoolean("antibot.messages.antibot-moved");
        }

        mentionsEnabled = config.getBoolean("mentions.enabled");
        if (mentionsEnabled) {
            mentionColor = config.getString("mentions.color");
            mentionCharacter = config.getString("mentions.character");
        }

        rulesEnabled = config.getBoolean("rules.enabled");
        if (rulesEnabled) {
            rulesMessage = config.getStringList("rules.message");
            rulesPrefixEnabled = config.getBoolean("rules.use-prefix");
        }

        logsChatEnabled = config.getBoolean("logs.chat.enabled");
        logsCommandEnabled = config.getBoolean("logs.command.enabled");
        logBannedCommandsEnabled = config.getBoolean("logs.banned-commands.enabled");

        pollBar = config.getInt("poll.options.bar.length");
        pollFill = config.getString("poll.options.bar.filled");
        pollEmpty = config.getString("poll.options.bar.empty");

        helpOpFormat = config.getString("helpop.format");
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isLogBannedCommandsEnabled() { return logBannedCommandsEnabled; }
    public String getHelpOpFormat() { return helpOpFormat; }
    public boolean isDepurationAntiFloodEnabled() { return depurationAntiFloodEnabled; }
    public boolean isDepurationCommandEnabled() { return depurationCommandEnabled; }
    public boolean isDepurationChatEnabled() { return depurationChatEnabled; }
    public String getAnnouncementFormat() { return announcementFormat; }
    public boolean isAnnouncementEnabled() { return announcementEnabled; }
    public boolean isChatColorEnabled() { return chatColorEnabled; }
    public boolean isFormatEnabled() { return formatEnabled; }
    public boolean isPingEnabled() { return pingEnabled; }
    public boolean isPrintEnabled() { return printEnabled; }
    public List<String> getRulesMessage() { return rulesMessage; }
    public boolean isRulesPrefixEnabled() { return rulesPrefixEnabled; }
    public boolean isRulesEnabled() { return rulesEnabled; }
    public String getMentionCharacter() { return mentionCharacter; }
    public String getMentionColor() { return mentionColor; }
    public boolean isMentionsEnabled() { return mentionsEnabled; }
    public boolean isWarningEnabled() { return warningEnabled; }
    public String getWarningFormat() { return warningFormat; }
    public int getPollBar() { return pollBar; }
    public String getPollEmpty() { return pollEmpty; }
    public String getPollFill () { return pollFill; }
    public boolean isIgnoreEnabled() { return ignoreEnabled; }
    public String getSpyFormat() { return spyFormat; }
    public boolean isSpyEnabled() { return spyEnabled; }
    public String getBroadcastFormat() { return broadcastFormat; }
    public boolean isBroadcastEnabled() { return broadcastEnabled; }
    public boolean isUnicodeBlockAll() { return unicodeBlockAll; }
    public String getUnicodeMatch() { return unicodeMatch; }
    public boolean isUnicodeEnabled() {return unicodeEnabled; }
    public double getPercentageFlood() { return percentageFlood; }
    public int getCharactersFlood() { return charactersFlood; }
    public boolean isFloodPercentEnabled() { return floodPercentEnabled; }
    public boolean isFloodRepeatEnabled() { return floodRepeatEnabled; }
    public long getCooldownCommandTime() { return cooldownCommandTime; }
    public long getCooldownChatTime() { return cooldownChatTime; }
    public boolean isCooldownCommand() { return cooldownCommand; }
    public boolean isCooldownChat() { return cooldownChat; }
    public boolean isLogsCommandEnabled() { return logsCommandEnabled; }
    public boolean isLogsChatEnabled() { return logsChatEnabled; }
    public boolean isAntibotMoved() { return antibotMoved; }
    public boolean isAntibotJoin() { return antibotJoin; }
    public boolean isAntibotCommand() { return antibotCommand; }
    public boolean isAntibotChat() { return antibotChat; }
    public String getAntibotBypass() { return antibotBypass; }
    public boolean isAntibotEnabled() { return antibotEnabled; }
    public String getBypassRepeatMessages() { return bypassRepeatMessages; }
    public double getRepeatMessagesPercent() { return repeatMessagesPercent; }
    public boolean isRepeatMessagesEnabled() { return repeatMessagesEnabled; }
    public String getMuteChatPermission() { return muteChatPermission; }
    public String getBypassMuteChatPermission() { return bypassMuteChatPermission; }
    public boolean isMuteChatEnabled() { return muteChatEnabled; }
    public int getMessagesChatClear() { return messagesChatClear; }
    public boolean isChatClearEnabled() { return chatClearEnabled; }
    public String getMsgPermission() { return msgPermission; }
    public String getReplyPermission() { return replyPermission; }
    public String getReplyFormatReceiver() { return replyFormatReceiver; }
    public String getReplyFormatSender() { return replyFormatSender; }
    public String getMsgFormatReceiver() { return msgFormatReceiver; }
    public String getMsgFormatSender() { return msgFormatSender; }
    public boolean isReplyEnabled() { return replyEnabled; }
    public boolean isMsgEnabled() { return msgEnabled; }
    public String getPermissionBypassCap() { return permissionBypassCap; }
    public String getPermissionBypassFinalDot() { return permissionBypassFinalDot; }
    public int getGrammarMinCharactersCap() { return grammarMinCharactersCap; }
    public int getGrammarMinCharactersDot() { return grammarMinCharactersDot; }
    public String getGrammarDotCharacter() { return grammarDotCharacter; }
    public boolean isGrammarDotEnabled() { return grammarDotEnabled; }
    public boolean isGrammarEnabled() { return grammarEnabled; }
    public boolean isGrammarCapEnabled() { return grammarCapEnabled; }
    public int getGrammarCapLetters() { return grammarCapLetters; }

    public String getFormat() { return format; }
    public boolean isFormatGroup() { return formatGroup; }

    public boolean isRegisterMessagesOnConsole() { return registerMessagesOnConsole; }

    public AdvertisingConfig getIpv4Config() { return ipv4Config; }
    public AdvertisingConfig getDomainConfig() { return domainConfig; }
    public AdvertisingConfig getLinksConfig() { return linksConfig; }
    public String getAdvertisingBypass() { return advertisingBypass; }

    public boolean isAntiCapEnabled() { return antiCapEnabled; }
    public double getAntiCapPercent() { return antiCapPercent; }
    public String getAntiCapMode() { return antiCapMode; }
    public boolean isAntiCapMessageEnabled() { return antiCapMessageEnabled; }
}

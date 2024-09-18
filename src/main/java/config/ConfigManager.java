package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final TChat plugin;
    private final ConfigFile configFile;
    private Map<Integer, String> pingColors;

    private String format;
    private boolean formatGroup;
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
    private boolean logBannedWordsEnabled;
    private String listHeader;
    private String listAppend;
    private String listFooter;
    private boolean ignoreLogEnabled;
    private int socialSpyMode;
    private List<String> socialSpyCommands;
    private boolean antiAdvertisingLogEnabled;
    private boolean colorsChatEnabled;
    private List<String> whitelistCommandsAntiBot;
    private String langFile;
    private boolean chatColorMenuEnabled;
    private boolean deathLogs;
    private Map<String, HoverConfig> pmHoverConfigs;
    private Map<String, HoverConfig> replyHoverConfigs;
    private int maxRepeatMessages;
    private String printPrefix;
    private boolean meEnabled;
    private String meFormat;
    private boolean sccEnabled;
    private String sccFormat;
    private List<String> blackLIgnore;

    public ConfigManager(TChat plugin) {
        this.plugin = plugin;
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

        ipv4Config = new AdvertisingConfig(config, "advertising.ipv4");
        domainConfig = new AdvertisingConfig(config, "advertising.domain");
        linksConfig = new AdvertisingConfig(config, "advertising.links");
        advertisingBypass = config.getString("advertising.bypass");

        ignoreEnabled = config.getBoolean("ignore.enabled");
        if (ignoreEnabled) {
            blackLIgnore = config.getStringList("ignore.blacklist");
        }

        chatColorEnabled = config.getBoolean("chat-color.enabled");
        if (chatColorEnabled) {
            chatColorMenuEnabled = config.getBoolean("chat-color.menu-enabled");
        }
        colorsChatEnabled = config.getBoolean("chat-color.colors-in-chat-enabled");

        printEnabled = config.getBoolean("print.enabled");
        if (printEnabled) {
            printPrefix = config.getString("print.prefix", "%prefix%");
        }

        warningEnabled = config.getBoolean("broadcast.warning.enabled");
        if (warningEnabled) {
            warningFormat = config.getString("broadcast.warning.format");
        }

        sccEnabled = config.getBoolean("scc.enabled", true);
        if (sccEnabled) {
            sccFormat = config.getString("scc.format", "&7[&a%player_name%&7] &#3AFE09These are my coordinates: &#FEB322%x, %y, %z&#3AFE09!");
        }

        meEnabled = config.getBoolean("me.enabled", true);
        if (meEnabled) {
            meFormat = config.getString("me.format", "&5* &#3AFE09%player_name% &#FEAD10> &7%message%");
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
            socialSpyMode = config.getInt("spy.commands.mode");
            if (socialSpyMode != 1) {
                socialSpyCommands = config.getStringList("spy.commands.commands");
            }
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
                maxRepeatMessages = config.getInt("grammar.repeat-messages.max-repeat-messages", 1);
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
            antibotChat = config.getBoolean("antibot.antibot-chat");
            antibotCommand = config.getBoolean("antibot.antibot-command");
            antibotJoin = config.getBoolean("antibot.messages.antibot-join");
            antibotMoved = config.getBoolean("antibot.messages.antibot-moved");
            if (antibotCommand) {
                whitelistCommandsAntiBot = config.getStringList("antibot.commands.whitelist");
            }
        }

        rulesEnabled = config.getBoolean("rules.enabled");
        if (rulesEnabled) {
            rulesMessage = config.getStringList("rules.message");
            rulesPrefixEnabled = config.getBoolean("rules.use-prefix");
        }

        logsChatEnabled = config.getBoolean("logs.chat.enabled");
        logsCommandEnabled = config.getBoolean("logs.command.enabled");
        logBannedCommandsEnabled = config.getBoolean("logs.banned-commands.enabled");
        logBannedWordsEnabled = config.getBoolean("logs.banned-words.enabled");
        antiAdvertisingLogEnabled = config.getBoolean("logs.anti-advertising.enabled");
        ignoreLogEnabled = config.getBoolean("logs.ignore.enabled");
        deathLogs = config.getBoolean("logs.death.enabled");

        pollBar = config.getInt("poll.options.bar.length");
        pollFill = config.getString("poll.options.bar.filled");
        pollEmpty = config.getString("poll.options.bar.empty");

        helpOpFormat = config.getString("helpop.format");

        listAppend = config.getString("list.append");
        listFooter = config.getString("list.footer");
        listHeader = config.getString("list.header");

        langFile = config.getString("chat.lang");

        pingColors = new HashMap<>();

        List<Map<?, ?>> colorConfigs = config.getMapList("ping.colors");
        for (Map<?, ?> colorConfig : colorConfigs) {
            String range = (String) colorConfig.get("range");
            String colorCode = (String) colorConfig.get("color");

            if (range == null || colorCode == null) {
                throw new IllegalArgumentException("[Ping] Error in config: " + colorConfig);
            }

            String[] parts = range.split("-");
            if (parts.length < 1 || parts.length > 2) {
                throw new IllegalArgumentException("[Ping]: '" + range + "' does not have min-max format: '" + range + "'");
            }

            int min;
            int max;

            try {
                min = Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[Ping]: '" + parts[0] + "' is not valid (0)", e);
            }

            try {
                max = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[Ping]: '" + parts[1] + "' is not valid (1)", e);
            }

            for (int i = min; i <= max; i++) {
                pingColors.put(i, colorCode);
            }
        }

        pmHoverConfigs = new HashMap<>();

        ConfigurationSection hoverSection = config.getConfigurationSection("pm.msg.hover");
        if (hoverSection != null) {
            for (String group : hoverSection.getKeys(false)) {
                List<String> senderTexts = hoverSection.getStringList(group + ".sender");
                String senderAction = hoverSection.getString(group + ".sender-action");
                List<String> receiverTexts = hoverSection.getStringList(group + ".receiver");
                String receiverAction = hoverSection.getString(group + ".receiver-action");

                HoverConfig hoverConfig = new HoverConfig(senderTexts, senderAction, receiverTexts, receiverAction);
                pmHoverConfigs.put(group, hoverConfig);
            }
        } else {
            plugin.getLogger().warning("'pm.msg.hover' not found in config.yml");
        }

        replyHoverConfigs = new HashMap<>();

        ConfigurationSection replyHoverSection = config.getConfigurationSection("pm.reply.hover");
        if (replyHoverSection != null) {
            for (String group : replyHoverSection.getKeys(false)) {
                List<String> senderTexts = replyHoverSection.getStringList(group + ".sender");
                String senderAction = replyHoverSection.getString(group + ".sender-action");
                List<String> receiverTexts = replyHoverSection.getStringList(group + ".receiver");
                String receiverAction = replyHoverSection.getString(group + ".receiver-action");

                HoverConfig hoverConfig = new HoverConfig(senderTexts, senderAction, receiverTexts, receiverAction);
                replyHoverConfigs.put(group, hoverConfig);
            }
        } else {
            plugin.getLogger().warning("'pm.reply.hover' not found in config.yml");
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public HoverConfig getPmHoverConfig(String group) {
        return pmHoverConfigs.get(group);
    }

    public HoverConfig getReplyHoverConfig(String group) {
        return replyHoverConfigs.get(group);
    }

    public List<String> getBlackLIgnore() {return blackLIgnore;}
    public boolean isSccEnabled() {return sccEnabled;}
    public String getSccFormat() {return sccFormat;}
    public boolean isMeEnabled() {return meEnabled;}
    public String getMeFormat() {return meFormat;}
    public String getPrintPrefix() {return printPrefix;}
    public int getMaxRepeatMessages() {return maxRepeatMessages;}
    public boolean isDeathLogs() {return deathLogs;}
    public boolean isChatColorMenuEnabled() {return chatColorMenuEnabled;}
    public String getLangFile() { return langFile; }
    public List<String> getWhitelistCommandsAntiBot() { return whitelistCommandsAntiBot; }
    public boolean isColorsChatEnabled() { return colorsChatEnabled; }
    public boolean isAntiAdvertisingLogEnabled() { return antiAdvertisingLogEnabled; }
    public List<String> getSocialSpyCommands() { return socialSpyCommands; }
    public int getSocialSpyMode() { return socialSpyMode; }
    public boolean isIgnoreLogEnabled() { return ignoreLogEnabled; }
    public String getListFooter() { return listFooter; }
    public String getListAppend() { return listAppend; }
    public String getListHeader() { return listHeader; }
    public boolean isLogBannedWordsEnabled() { return logBannedWordsEnabled; }
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

    public AdvertisingConfig getIpv4Config() { return ipv4Config; }
    public AdvertisingConfig getDomainConfig() { return domainConfig; }
    public AdvertisingConfig getLinksConfig() { return linksConfig; }
    public String getAdvertisingBypass() { return advertisingBypass; }

    public boolean isAntiCapEnabled() { return antiCapEnabled; }
    public double getAntiCapPercent() { return antiCapPercent; }
    public String getAntiCapMode() { return antiCapMode; }
    public boolean isAntiCapMessageEnabled() { return antiCapMessageEnabled; }

    public FileConfiguration getConfig() { return this.configFile.getConfig(); }

    public void saveConfig() { configFile.saveConfig(); }

    public String getColorForPing(int ping) {
        return pingColors.entrySet().stream()
                .filter(entry -> ping >= entry.getKey())
                .reduce((first, second) -> second)
                .map(Map.Entry::getValue)
                .orElse("&#FFFFFF");
    }

    public static class HoverConfig {
        private final List<String> senderTexts;
        private final String senderAction;
        private final List<String> receiverTexts;
        private final String receiverAction;

        public HoverConfig(List<String> senderTexts, String senderAction, List<String> receiverTexts, String receiverAction) {
            this.senderTexts = senderTexts;
            this.senderAction = senderAction;
            this.receiverTexts = receiverTexts;
            this.receiverAction = receiverAction;
        }

        public List<String> getSenderTexts() {
            return senderTexts;
        }

        public String getSenderAction() {
            return senderAction;
        }

        public List<String> getReceiverTexts() {
            return receiverTexts;
        }

        public String getReceiverAction() {
            return receiverAction;
        }
    }
}

package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final ConfigFile configFile;

    private String format;
    private boolean formatGroup;
    private String langFile;
    private boolean formatEnabled;

    private boolean muteChatEnabled;
    private boolean repeatCommandsEnabled;
    private boolean antiAdvertisingEnabled;
    private boolean antiBotEnabled;
    private boolean antiCapEnabled;
    private boolean antiFloodEnabled;
    private boolean antiUnicodeEnabled;
    private boolean autoBroadcastEnabled;
    private boolean bannedCommandsEnabled;
    private boolean bannedWordsEnabled;
    private boolean broadcastEnabled;
    private boolean channelsEnabled;
    private boolean chatBotEnabled;
    private boolean chatColorEnabled;
    private boolean chatGamesEnabled;
    private boolean commandProgrammerEnabled;
    private boolean customCommandsEnabled;
    private boolean commandTimerEnabled;
    private boolean cooldownsEnabled;
    private boolean deathEnabled;
    private boolean discordEnabled;
    private boolean grammarEnabled;
    private boolean helpOpEnabled;
    private boolean ignoreEnabled;
    private boolean invseeEnabled;
    private boolean joinsEnabled;
    private boolean levelsEnabled;
    private boolean listEnabled;
    private boolean loggerEnabled;
    private boolean mentionsEnabled;
    private boolean pingEnabled;
    private boolean placeholdersEnabled;
    private boolean pollsEnabled;
    private boolean replacerEnabled;
    private boolean sicEnabled;
    private boolean socialSpyEnabled;
    private boolean tagsEnabled;
    private boolean worldsEnabled;
    private boolean msgEnabled;
    private boolean replyEnabled;
    private boolean muteEnabled;
    private boolean calculatorEnabled;
    private boolean secEnabled;
    private boolean nickEnabled;
    private boolean staffListEnabled;
    private boolean realNameEnabled;
    private boolean pluginEnabled;
    private boolean playerEnabled;
    private boolean seenEnabled;
    private boolean serverEnabled;
    private boolean colorsChatEnabled;

    public ConfigManager(TChat plugin) {
        this.configFile = new ConfigFile("config.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        // General configuration (chat.)
        formatEnabled = config.getBoolean("chat.format-enabled", true);
        langFile = config.getString("chat.lang", "messages_en.yml");
        if (formatEnabled) {
            formatGroup = config.getBoolean("chat.use-group-format", false);
            if (!formatGroup) {
                format = config.getString("chat.format", "%radius_mode%%tchat_prefix% &a%tchat_nick% &e>> ¡&7");
            }
        }

        // Modules (modules.)
        repeatCommandsEnabled = config.getBoolean("modules.repeat-commands", false);
        muteChatEnabled = config.getBoolean("modules.mute-chat", true);
        antiAdvertisingEnabled = config.getBoolean("modules.anti-advertising", true);
        antiBotEnabled = config.getBoolean("modules.anti-bot", true);
        antiCapEnabled = config.getBoolean("modules.anti-cap", true);
        antiFloodEnabled = config.getBoolean("modules.anti-flood", true);
        antiUnicodeEnabled = config.getBoolean("modules.anti-unicode", false);
        autoBroadcastEnabled = config.getBoolean("modules.auto-broadcast", true);
        bannedCommandsEnabled = config.getBoolean("modules.banned-commands", true);
        bannedWordsEnabled = config.getBoolean("modules.banned-words", true);
        broadcastEnabled = config.getBoolean("modules.broadcast", true);
        calculatorEnabled = config.getBoolean("modules.calculator", true);
        channelsEnabled = config.getBoolean("modules.channels", true);
        chatBotEnabled = config.getBoolean("modules.chatbot", true);
        chatColorEnabled = config.getBoolean("modules.chat-color", true);
        chatGamesEnabled = config.getBoolean("modules.chat-games", true);
        colorsChatEnabled = config.getBoolean("modules.colors", true);
        commandProgrammerEnabled = config.getBoolean("modules.command-programmer", true);
        commandTimerEnabled = config.getBoolean("modules.command-timer", true);
        cooldownsEnabled = config.getBoolean("modules.cooldowns", true);
        customCommandsEnabled = config.getBoolean("modules.custom-commands", true);
        deathEnabled = config.getBoolean("modules.death", true);
        discordEnabled = config.getBoolean("modules.discord", false);
        grammarEnabled = config.getBoolean("modules.grammar", true);
        helpOpEnabled = config.getBoolean("modules.help-op", true);
        ignoreEnabled = config.getBoolean("modules.ignore", true);
        invseeEnabled = config.getBoolean("modules.invsee", true);
        joinsEnabled = config.getBoolean("modules.joins", true);
        levelsEnabled = config.getBoolean("modules.levels", true);
        listEnabled = config.getBoolean("modules.list", true);
        loggerEnabled = config.getBoolean("modules.logger", true);
        mentionsEnabled = config.getBoolean("modules.mentions", true);
        msgEnabled = config.getBoolean("modules.msg", true);
        muteEnabled = config.getBoolean("modules.mute", true);
        nickEnabled = config.getBoolean("modules.nick", true);
        pingEnabled = config.getBoolean("modules.ping", true);
        placeholdersEnabled = config.getBoolean("modules.placeholders", true);
        playerEnabled = config.getBoolean("modules.player", true);
        pluginEnabled = config.getBoolean("modules.plugin", true);
        pollsEnabled = config.getBoolean("modules.polls", true);
        realNameEnabled = config.getBoolean("modules.real-name", true);
        replacerEnabled = config.getBoolean("modules.replacer", true);
        replyEnabled = config.getBoolean("modules.reply", true);
        secEnabled = config.getBoolean("modules.sec", true);
        seenEnabled = config.getBoolean("modules.seen", true);
        serverEnabled = config.getBoolean("modules.server", true);
        sicEnabled = config.getBoolean("modules.sic", true);
        socialSpyEnabled = config.getBoolean("modules.social-spy", true);
        staffListEnabled = config.getBoolean("modules.staff-list", true);
        tagsEnabled = config.getBoolean("modules.tags", true);
        worldsEnabled = config.getBoolean("modules.worlds", true);
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public FileConfiguration getConfig() { return this.configFile.getConfig(); }
    public void saveConfig() { configFile.saveConfig(); }

    public String getLangFile() { return langFile; }
    public boolean isFormatEnabled() { return formatEnabled; }
    public String getFormat() { return format; }
    public boolean isFormatGroup() { return formatGroup; }

    public boolean isColorsChatEnabled() {return colorsChatEnabled;}
    public boolean isServerEnabled() {return serverEnabled;}
    public boolean isSeenEnabled() {return seenEnabled;}
    public boolean isPlayerEnabled() {return playerEnabled;}
    public boolean isPluginEnabled() {return pluginEnabled;}
    public boolean isRealNameEnabled() {return realNameEnabled;}
    public boolean isStaffListEnabled() {return staffListEnabled;}
    public boolean isNickEnabled() {return nickEnabled;}
    public boolean isSecEnabled() {return secEnabled;}
    public boolean isCalculatorEnabled() {return calculatorEnabled;}
    public boolean isMuteEnabled() {return muteEnabled;}
    public boolean isReplyEnabled() {return replyEnabled;}
    public boolean isMsgEnabled() {return msgEnabled;}
    public boolean isWorldsEnabled() {return worldsEnabled;}
    public boolean isTagsEnabled() {return tagsEnabled;}
    public boolean isSocialSpyEnabled() {return socialSpyEnabled;}
    public boolean isSicEnabled() {return sicEnabled;}
    public boolean isReplacerEnabled() {return replacerEnabled;}
    public boolean isPollsEnabled() {return pollsEnabled;}
    public boolean isPlaceholdersEnabled() {return placeholdersEnabled;}
    public boolean isPingEnabled() {return pingEnabled;}
    public boolean isMentionsEnabled() {return mentionsEnabled;}
    public boolean isLoggerEnabled() {return loggerEnabled;}
    public boolean isListEnabled() {return listEnabled;}
    public boolean isLevelsEnabled() {return levelsEnabled;}
    public boolean isJoinsEnabled() {return joinsEnabled;}
    public boolean isInvseeEnabled() {return invseeEnabled;}
    public boolean isIgnoreEnabled() {return ignoreEnabled;}
    public boolean isHelpOpEnabled() {return helpOpEnabled;}
    public boolean isGrammarEnabled() {return grammarEnabled;}
    public boolean isDiscordEnabled() {return discordEnabled;}
    public boolean isDeathEnabled() {return deathEnabled;}
    public boolean isCooldownsEnabled() {return cooldownsEnabled;}
    public boolean isCommandTimerEnabled() {return commandTimerEnabled;}
    public boolean isCustomCommandsEnabled() {return customCommandsEnabled;}
    public boolean isCommandProgrammerEnabled() {return commandProgrammerEnabled;}
    public boolean isChatGamesEnabled() {return chatGamesEnabled;}
    public boolean isChatColorEnabled() {return chatColorEnabled;}
    public boolean isChatBotEnabled() {return chatBotEnabled;}
    public boolean isChannelsEnabled() {return channelsEnabled;}
    public boolean isBroadcastEnabled() {return broadcastEnabled;}
    public boolean isBannedWordsEnabled() {return bannedWordsEnabled;}
    public boolean isBannedCommandsEnabled() {return bannedCommandsEnabled;}
    public boolean isAutoBroadcastEnabled() {return autoBroadcastEnabled;}
    public boolean isAntiUnicodeEnabled() {return antiUnicodeEnabled;}
    public boolean isAntiFloodEnabled() {return antiFloodEnabled;}
    public boolean isAntiCapEnabled() {return antiCapEnabled;}
    public boolean isAntiBotEnabled() {return antiBotEnabled;}
    public boolean isAntiAdvertisingEnabled() {return antiAdvertisingEnabled;}
    public boolean isMuteChatEnabled() { return muteChatEnabled; }
    public boolean isRepeatCommandsEnabled() {return repeatCommandsEnabled;}
}

package config;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

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

    public ConfigManager(TChat plugin) {
        this.configFile = new ConfigFile("config.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();
        format = config.getString("chat.format");
        formatGroup = config.getBoolean("chat.use-group-format");
        registerMessagesOnConsole = config.getBoolean("chat.register-messages-on-console");

        ipv4Config = new AdvertisingConfig(config, "advertising.ipv4");
        domainConfig = new AdvertisingConfig(config, "advertising.domain");
        linksConfig = new AdvertisingConfig(config, "advertising.links");
        advertisingBypass = config.getString("advertising.bypass");

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
            if (grammarDotEnabled) {
                permissionBypassCap = config.getString("grammar.final-dot.bypass-permission");
                grammarMinCharactersDot = config.getInt("grammar.final-dot.min-characters");
                grammarDotCharacter = config.getString("grammar.final-dot.character");
            }
            if (grammarCapEnabled) {
                permissionBypassFinalDot = config.getString("grammar.cap.bypass-permission");
                grammarMinCharactersCap = config.getInt("grammar.cap.min-characters");
                grammarCapLetters = config.getInt("grammar.cap.letters");
            }
        }

        msgEnabled = config.getBoolean("pm.msg.enabled");
        if (msgEnabled) {
            msgFormatSender = config.getString("pm.msg.formats.sender");
            msgFormatReceiver = config.getString("pm.msg.formats.receiver");
        }

        replyEnabled = config.getBoolean("pm.reply.enabled");
        if (replyEnabled) {
            replyFormatSender = config.getString("pm.reply.formats.sender");
            replyFormatReceiver = config.getString("pm.reply.formats.receiver");
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

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
    public boolean getFormatGroup() { return formatGroup; }

    public boolean getRegisterMessagesOnConsole() { return registerMessagesOnConsole; }

    public AdvertisingConfig getIpv4Config() { return ipv4Config; }
    public AdvertisingConfig getDomainConfig() { return domainConfig; }
    public AdvertisingConfig getLinksConfig() { return linksConfig; }
    public String getAdvertisingBypass() { return advertisingBypass; }

    public boolean isAntiCapEnabled() { return antiCapEnabled; }
    public double getAntiCapPercent() { return antiCapPercent; }
    public String getAntiCapMode() { return antiCapMode; }
    public boolean isAntiCapMessageEnabled() { return antiCapMessageEnabled; }
}

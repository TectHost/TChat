package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class LoggerConfigManager {

    private final ConfigFile loggerFile;

    private boolean deathLogs;
    private boolean antiAdvertisingLogEnabled;
    private boolean ignoreLogEnabled;
    private boolean logsChatEnabled;
    private boolean logsCommandEnabled;
    private boolean logBannedCommandsEnabled;
    private boolean logBannedWordsEnabled;

    public LoggerConfigManager(TChat plugin){
        this.loggerFile = new ConfigFile("logger.yml", "modules", plugin);
        this.loggerFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = loggerFile.getConfig();

        logsChatEnabled = config.getBoolean("logs.chat.enabled");
        logsCommandEnabled = config.getBoolean("logs.command.enabled");
        logBannedCommandsEnabled = config.getBoolean("logs.banned-commands.enabled");
        logBannedWordsEnabled = config.getBoolean("logs.banned-words.enabled");
        antiAdvertisingLogEnabled = config.getBoolean("logs.anti-advertising.enabled");
        ignoreLogEnabled = config.getBoolean("logs.ignore.enabled");
        deathLogs = config.getBoolean("logs.death.enabled");
    }

    public void reloadConfig(){
        loggerFile.reloadConfig();
        loadConfig();
    }

    public boolean isDeathLogs() {return deathLogs;}
    public boolean isIgnoreLogEnabled() { return ignoreLogEnabled; }
    public boolean isAntiAdvertisingLogEnabled() { return antiAdvertisingLogEnabled; }
    public boolean isLogBannedWordsEnabled() { return logBannedWordsEnabled; }
    public boolean isLogBannedCommandsEnabled() { return logBannedCommandsEnabled; }
    public boolean isLogsCommandEnabled() { return logsCommandEnabled; }
    public boolean isLogsChatEnabled() { return logsChatEnabled; }
}

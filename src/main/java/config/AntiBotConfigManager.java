package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class AntiBotConfigManager {

    private final ConfigFile configFile;
    private boolean antibotChat;
    private boolean antibotCommand;
    private boolean antibotJoin;
    private boolean antibotMoved;
    private List<String> whitelistCommandsAntiBot;

    public AntiBotConfigManager(TChat plugin){
        this.configFile = new ConfigFile("anti_bot.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        antibotChat = config.getBoolean("antibot.chat");
        antibotCommand = config.getBoolean("antibot.command");
        antibotJoin = config.getBoolean("antibot.messages.antibot-join");
        antibotMoved = config.getBoolean("antibot.messages.antibot-moved");
        if (antibotCommand) {
            whitelistCommandsAntiBot = config.getStringList("antibot.commands.whitelist");
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public List<String> getWhitelistCommandsAntiBot() { return whitelistCommandsAntiBot; }
    public boolean isAntibotMoved() { return antibotMoved; }
    public boolean isAntibotJoin() { return antibotJoin; }
    public boolean isAntibotCommand() { return antibotCommand; }
    public boolean isAntibotChat() { return antibotChat; }
}

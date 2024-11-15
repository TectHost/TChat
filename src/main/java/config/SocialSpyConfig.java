package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SocialSpyConfig {

    private final ConfigFile configFile;

    private int socialSpyMode;
    private List<String> socialSpyCommands;
    private String spyFormat;

    public SocialSpyConfig(TChat plugin){
        this.configFile = new ConfigFile("social_spy.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        spyFormat = config.getString("spy.commands.format");
        socialSpyMode = config.getInt("spy.commands.mode");
        if (socialSpyMode != 1) {
            socialSpyCommands = config.getStringList("spy.commands.commands");
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public List<String> getSocialSpyCommands() { return socialSpyCommands; }
    public int getSocialSpyMode() { return socialSpyMode; }
    public String getSpyFormat() { return spyFormat; }
}

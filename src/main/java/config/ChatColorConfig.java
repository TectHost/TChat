package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class ChatColorConfig {

    private final ConfigFile configFile;

    private boolean chatColorMenuEnabled;

    public ChatColorConfig(TChat plugin){
        this.configFile = new ConfigFile("chatcolor.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        chatColorMenuEnabled = config.getBoolean("chat-color.menu-enabled");
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isChatColorMenuEnabled() {return chatColorMenuEnabled;}
}

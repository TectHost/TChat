package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class PollsConfig {

    private final ConfigFile configFile;

    private String pollFill;
    private String pollEmpty;
    private int pollBar;

    public PollsConfig(TChat plugin){
        this.configFile = new ConfigFile("polls.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        pollBar = config.getInt("poll.options.bar.length");
        pollFill = config.getString("poll.options.bar.filled");
        pollEmpty = config.getString("poll.options.bar.empty");
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public int getPollBar() { return pollBar; }
    public String getPollEmpty() { return pollEmpty; }
    public String getPollFill () { return pollFill; }
}

package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class AntiCapManager {

    private final ConfigFile configFile;

    private double antiCapPercent;
    private String antiCapMode;
    private boolean antiCapMessageEnabled;

    public AntiCapManager(TChat plugin){
        this.configFile = new ConfigFile("anti_cap.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        antiCapPercent = config.getDouble("cap.percent");
        antiCapMode = config.getString("cap.mode");
        antiCapMessageEnabled = config.getBoolean("cap.message-enabled");
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public double getAntiCapPercent() { return antiCapPercent; }
    public String getAntiCapMode() { return antiCapMode; }
    public boolean isAntiCapMessageEnabled() { return antiCapMessageEnabled; }

}

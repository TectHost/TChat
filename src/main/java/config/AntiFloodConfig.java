package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class AntiFloodConfig {

    private final ConfigFile configFile;

    private boolean depurationAntiFloodEnabled;
    private boolean floodRepeatEnabled;
    private boolean floodPercentEnabled;
    private int charactersFlood;
    private double percentageFlood;

    public AntiFloodConfig(TChat plugin){
        this.configFile = new ConfigFile("anti_flood.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        floodPercentEnabled = config.getBoolean("flood.percent.enabled");
        floodRepeatEnabled = config.getBoolean("flood.repeat.enabled");
        depurationAntiFloodEnabled = config.getBoolean("flood.depuration");
        if (floodRepeatEnabled) {
            charactersFlood = config.getInt("flood.repeat.characters");
        }
        if (floodPercentEnabled) {
            percentageFlood = config.getDouble("flood.percent.percentage");
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isDepurationAntiFloodEnabled() { return depurationAntiFloodEnabled; }
    public double getPercentageFlood() { return percentageFlood; }
    public int getCharactersFlood() { return charactersFlood; }
    public boolean isFloodPercentEnabled() { return floodPercentEnabled; }
    public boolean isFloodRepeatEnabled() { return floodRepeatEnabled; }
}

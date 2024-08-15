package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class PlaceholdersConfig {
    private final ConfigFile configFile;
    private boolean coordsEnabled;
    private String coordsFormat;
    private String coordsName;
    private boolean itemEnabled;
    private String itemFormat;
    private String itemName;

    public PlaceholdersConfig(TChat plugin) {
        this.configFile = new ConfigFile("placeholders.yml", null, plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        coordsEnabled = config.getBoolean("chat.coords.enabled");
        if (coordsEnabled) {
            coordsFormat = config.getString("chat.coords.format");
            coordsName = config.getString("chat.coords.name");
        }

        itemEnabled = config.getBoolean("chat.item.enabled");
        if (itemEnabled) {
            itemFormat = config.getString("chat.item.format");
            itemName = config.getString("chat.item.name");
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isItemEnabled() { return itemEnabled; }
    public String getItemFormat() { return itemFormat; }
    public String getItemName() { return itemName; }

    public String getCoordsName() { return coordsName; }
    public boolean isCoordsEnabled() { return coordsEnabled; }
    public String getCoordsFormat() { return coordsFormat; }
}
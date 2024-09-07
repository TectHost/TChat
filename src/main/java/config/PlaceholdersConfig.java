package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class PlaceholdersConfig {
    private final ConfigFile configFile;
    private boolean coordsEnabled;
    private String coordsFormat;
    private String coordsName;
    private boolean itemEnabled;
    private String itemFormat;
    private String itemName;
    private boolean worldEnabled;
    private String worldFormat;
    private String worldName;
    private boolean inventoryEnabled;
    private String inventoryFormat;
    private String inventoryName;
    private boolean hoverInventoryTextEnabled;
    private boolean hoverInventoryActionEnabled;
    private String hoverInventoryAction;
    private List<String> hoverInventoryText;

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

        worldEnabled = config.getBoolean("chat.world.enabled");
        if (worldEnabled) {
            worldName = config.getString("chat.world.name");
            worldFormat = config.getString("chat.world.format");
        }

        inventoryEnabled = config.getBoolean("chat.inventory.enabled");
        if (inventoryEnabled) {
            inventoryName = config.getString("chat.inventory.name");
            inventoryFormat = config.getString("chat.inventory.format");
            hoverInventoryActionEnabled = config.getBoolean("chat.inventory.hover.action.enabled");
            if (hoverInventoryActionEnabled) {
                hoverInventoryAction = config.getString("chat.inventory.hover.action.command");
            }
            hoverInventoryTextEnabled = config.getBoolean("chat.inventory.hover.text.enabled");
            if (hoverInventoryTextEnabled) {
                hoverInventoryText = config.getStringList("chat.inventory.hover.text.lines");
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isInventoryEnabled() {return inventoryEnabled;}
    public String getInventoryFormat() {return inventoryFormat;}
    public String getInventoryName() {return inventoryName;}
    public boolean isHoverInventoryTextEnabled() {return hoverInventoryTextEnabled;}
    public boolean isHoverInventoryActionEnabled() {return hoverInventoryActionEnabled;}
    public String getHoverInventoryAction() {return hoverInventoryAction;}
    public List<String> getHoverInventoryText() {return hoverInventoryText;}

    public boolean isWorldEnabled() { return worldEnabled; }
    public String getWorldFormat() { return worldFormat; }
    public String getWorldName() { return worldName; }

    public boolean isItemEnabled() { return itemEnabled; }
    public String getItemFormat() { return itemFormat; }
    public String getItemName() { return itemName; }

    public String getCoordsName() { return coordsName; }
    public boolean isCoordsEnabled() { return coordsEnabled; }
    public String getCoordsFormat() { return coordsFormat; }
}
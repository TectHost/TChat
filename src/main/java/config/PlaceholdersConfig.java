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
    private String itemPrefix;
    private String itemSuffix;
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

    private boolean enderEnabled;
    private String enderFormat;
    private String enderName;
    private boolean hoverEnderTextEnabled;
    private boolean hoverEnderActionEnabled;
    private String hoverEnderAction;
    private List<String> hoverEnderText;

    private boolean commandEnabled;
    private String commandName;
    private String commandFormat;
    private boolean hoverCommandEnabled;
    private List<String> hoverCommand;

    public PlaceholdersConfig(TChat plugin) {
        this.configFile = new ConfigFile("placeholders.yml", "modules", plugin);
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
            itemPrefix = config.getString("chat.item.prefix", "%tchat_format%%before_item%&7[");
            itemSuffix = config.getString("chat.item.suffix", "&7]%after_item%");
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

        enderEnabled = config.getBoolean("chat.ender.enabled");
        if (enderEnabled) {
            enderName = config.getString("chat.ender.name");
            enderFormat = config.getString("chat.ender.format");
            hoverEnderActionEnabled = config.getBoolean("chat.ender.hover.action.enabled");
            if (hoverEnderActionEnabled) {
                hoverEnderAction = config.getString("chat.ender.hover.action.command");
            }
            hoverEnderTextEnabled = config.getBoolean("chat.ender.hover.text.enabled");
            if (hoverEnderTextEnabled) {
                hoverEnderText = config.getStringList("chat.ender.hover.text.lines");
            }
        }

        commandEnabled = config.getBoolean("chat.command.enabled", true);
        if (commandEnabled) {
            commandFormat = config.getString("chat.command.format", "%tchat_format%%before_command%&7[&a/%command%&7]%after_command%");
            commandName = config.getString("chat.command.name", "\\\\[/([^]]+)]");
            hoverCommandEnabled = config.getBoolean("chat.command.hover.enabled", false);
            if (hoverCommandEnabled) {
                hoverCommand = config.getStringList("chat.command.hover.lines");
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isEnderEnabled() {return enderEnabled;}
    public String getEnderFormat() {return enderFormat;}
    public String getEnderName() {return enderName;}
    public boolean isHoverEnderTextEnabled() {return hoverEnderTextEnabled;}
    public boolean isHoverEnderActionEnabled() {return hoverEnderActionEnabled;}
    public String getHoverEnderAction() {return hoverEnderAction;}
    public List<String> getHoverEnderText() {return hoverEnderText;}

    public boolean isCommandEnabled() {return commandEnabled;}
    public String getCommandName() {return commandName;}
    public String getCommandFormat() {return commandFormat;}
    public boolean isHoverCommandEnabled() {return hoverCommandEnabled;}
    public List<String> getHoverCommand() {return hoverCommand;}

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
    public String getItemPrefix() {return itemPrefix;}
    public String getItemSuffix() {return itemSuffix;}
    public String getItemName() { return itemName; }

    public String getCoordsName() { return coordsName; }
    public boolean isCoordsEnabled() { return coordsEnabled; }
    public String getCoordsFormat() { return coordsFormat; }
}
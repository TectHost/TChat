package minealex.tchat;

import commands.Commands;
import config.ConfigManager;
import config.MessagesManager;
import listeners.ChatFormatListener;
import listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import utils.TranslateHexColorCodes;

import java.util.Objects;

public class TChat extends JavaPlugin {
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private ChatFormatListener chatFormatListener;
    private ChatListener chatListener;
    private TranslateHexColorCodes translateHexColorCodes;

    @Override
    public void onEnable() {
        registerConfigFiles();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "TChat" + ChatColor.GRAY + "] Stopping TChat");
    }

    public void registerListeners() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        chatFormatListener = new ChatFormatListener(configManager, translateHexColorCodes);
        chatListener = new ChatListener(chatFormatListener);
        getServer().getPluginManager().registerEvents(chatFormatListener, this);
        getServer().getPluginManager().registerEvents(chatListener, this);
    }

    public void registerConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
    }

    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("tchat")).setExecutor(new Commands(this));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

}
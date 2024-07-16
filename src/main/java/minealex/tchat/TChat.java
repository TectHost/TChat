package minealex.tchat;

import commands.Commands;
import config.ConfigManager;
import config.MessagesManager;
import config.GroupManager;
import listeners.ChatFormatListener;
import listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import placeholders.Placeholders;
import utils.TranslateHexColorCodes;

import java.util.Objects;

public class TChat extends JavaPlugin {
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private ChatFormatListener chatFormatListener;
    private ChatListener chatListener;
    private TranslateHexColorCodes translateHexColorCodes;
    private GroupManager groupManager;

    @Override
    public void onEnable() {
        registerConfigFiles();
        initializeManagers();
        registerListeners();
        registerCommands();
        registerPlaceholders();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "TChat" + ChatColor.GRAY + "] Stopping TChat");
    }

    public void registerListeners() {
        chatFormatListener = new ChatFormatListener(configManager, groupManager, translateHexColorCodes);
        chatListener = new ChatListener(chatFormatListener);
        getServer().getPluginManager().registerEvents(chatListener, this);
    }

    public void registerConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
    }

    public void initializeManagers() {
        translateHexColorCodes = new TranslateHexColorCodes();
        groupManager = new GroupManager(this);
    }

    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("tchat")).setExecutor(new Commands(this));
    }

    public void registerPlaceholders() {
        new Placeholders(groupManager).register();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }
}

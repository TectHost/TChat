package minealex.tchat;

import commands.Commands;
import config.*;
import listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import placeholders.Placeholders;
import utils.TranslateHexColorCodes;
import blocked.BannedWords;
import blocked.BannedCommands;

import java.util.Objects;

public class TChat extends JavaPlugin {
    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private ChatFormatListener chatFormatListener;
    private ChatListener chatListener;
    private TranslateHexColorCodes translateHexColorCodes;
    private GroupManager groupManager;
    private BannedWordsManager bannedWordsManager;
    private BannedWords bannedWords;
    private BannedCommandsManager bannedCommandsManager;
    private BannedCommands bannedCommands;
    private ReplacerManager replacerManager;

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
        bannedWords = new BannedWords(bannedWordsManager, translateHexColorCodes);
        chatListener = new ChatListener(this, chatFormatListener);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(bannedWords, this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(this), this);
    }

    public void registerConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        bannedWordsManager = new BannedWordsManager(this);
        bannedCommandsManager = new BannedCommandsManager(this);
        replacerManager = new ReplacerManager(this);
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

    public BannedWordsManager getBannedWordsManager() {
        return bannedWordsManager;
    }

    public BannedWords getBannedWords() {
        return bannedWords;
    }

    public BannedCommandsManager getBannedCommandsManager() {
        return bannedCommandsManager;
    }

    public BannedCommands getBannedCommands() {
        return bannedCommands;
    }

    public ReplacerManager getReplacerManager() {
        return replacerManager;
    }
}

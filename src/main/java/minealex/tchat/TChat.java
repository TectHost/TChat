package minealex.tchat;

import commands.Commands;
import commands.ChatColorCommand;
import config.*;
import listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import placeholders.Placeholders;
import utils.ChatColorInventoryManager;
import utils.TranslateColors;
import utils.TranslateHexColorCodes;
import blocked.*;

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
    private AntiAdvertising antiAdvertising;
    private ChatColorInventoryManager chatColorInventoryManager;
    private SaveManager saveManager;
    private CapListener capListener;
    private TranslateColors translateColors;
    private ChatColorManager chatColorManager;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "TChat" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Starting TChat...");

        registerConfigFiles();
        initializeManagers();
        registerListeners();
        registerCommands();
        registerPlaceholders();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "TChat" + ChatColor.GRAY + "] " + ChatColor.GREEN + "TChat started.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "TChat" + ChatColor.GRAY + "] " + ChatColor.RED + "TChat stopped");
    }

    public void registerListeners() {
        chatFormatListener = new ChatFormatListener(this, configManager, groupManager, translateHexColorCodes);
        bannedWords = new BannedWords(bannedWordsManager, translateHexColorCodes);
        chatListener = new ChatListener(this, chatFormatListener);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(bannedWords, this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(this), this);
        antiAdvertising = new AntiAdvertising(this);
        capListener = new CapListener(this);
    }

    public void registerConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        bannedWordsManager = new BannedWordsManager(this);
        bannedCommandsManager = new BannedCommandsManager(this);
        replacerManager = new ReplacerManager(this);
        saveManager = new SaveManager(this);
        chatColorManager = new ChatColorManager(this);
    }

    public void initializeManagers() {
        translateHexColorCodes = new TranslateHexColorCodes();
        groupManager = new GroupManager(this);
        chatColorInventoryManager = new ChatColorInventoryManager(this);
        translateColors = new TranslateColors();
    }

    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("tchat")).setExecutor(new Commands(this));
        Objects.requireNonNull(this.getCommand("chatcolor")).setExecutor(new ChatColorCommand(this));
    }

    public void registerPlaceholders() {
        new Placeholders(this, groupManager).register();
    }

    // ------------------------------------------------------------------------------

    public ChatColorInventoryManager getChatColorInventoryManager() { return chatColorInventoryManager; }
    public SaveManager getSaveManager() { return saveManager; }
    public AntiAdvertising getAntiAdvertising() { return antiAdvertising; }
    public CapListener getCapListener() { return capListener; }
    public ConfigManager getConfigManager() { return configManager; }
    public MessagesManager getMessagesManager() { return messagesManager; }
    public GroupManager getGroupManager() { return groupManager; }
    public BannedWordsManager getBannedWordsManager() { return bannedWordsManager; }
    public BannedWords getBannedWords() { return bannedWords; }
    public BannedCommandsManager getBannedCommandsManager() { return bannedCommandsManager; }
    public BannedCommands getBannedCommands() { return bannedCommands; }
    public TranslateColors getTranslateColors() { return translateColors; }
    public ReplacerManager getReplacerManager() { return replacerManager; }
    public ChatColorManager getChatColorManager() { return chatColorManager; }
}

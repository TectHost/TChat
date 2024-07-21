package minealex.tchat;

import commands.*;
import config.*;
import listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import placeholders.Placeholders;
import utils.*;
import blocked.*;

import java.util.Objects;

public class TChat extends JavaPlugin {
    private ConfigManager configManager;
    private MessagesManager messagesManager;
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
    private ChannelsConfigManager channelsConfigManager;
    private ChannelsManager channelsManager;
    private GrammarListener grammarListener;
    private AutoBroadcastManager autoBroadcastManager;
    private ChatBotManager chatBotManager;
    private ChatBotListener chatBotListener;
    private CommandTimerManager commandTimerManager;

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
        ChatFormatListener chatFormatListener = new ChatFormatListener(this, configManager, groupManager, translateHexColorCodes);
        bannedWords = new BannedWords(bannedWordsManager, translateHexColorCodes);
        ChatListener chatListener = new ChatListener(this, chatFormatListener);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(bannedWords, this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(this), this);
        antiAdvertising = new AntiAdvertising(this);
        capListener = new CapListener(this);
        grammarListener = new GrammarListener(this);
        AutoBroadcastSender autoBroadcastSender = new AutoBroadcastSender(this);
        chatBotListener = new ChatBotListener(this);
        CommandTimerSender commandTimerSender = new CommandTimerSender(this);
    }

    public void registerConfigFiles() {
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        bannedWordsManager = new BannedWordsManager(this);
        bannedCommandsManager = new BannedCommandsManager(this);
        replacerManager = new ReplacerManager(this);
        saveManager = new SaveManager(this);
        chatColorManager = new ChatColorManager(this);
        channelsConfigManager = new ChannelsConfigManager(this);
        autoBroadcastManager = new AutoBroadcastManager(this);
        chatBotManager = new ChatBotManager(this);
        commandTimerManager = new CommandTimerManager(this);
    }

    public void initializeManagers() {
        translateHexColorCodes = new TranslateHexColorCodes();
        groupManager = new GroupManager(this);
        chatColorInventoryManager = new ChatColorInventoryManager(this);
        translateColors = new TranslateColors();
        channelsManager = new ChannelsManager();
    }

    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("tchat")).setExecutor(new Commands(this));
        Objects.requireNonNull(this.getCommand("chatcolor")).setExecutor(new ChatColorCommand(this));
        Objects.requireNonNull(this.getCommand("channel")).setExecutor(new ChannelCommand(this));
        if (getConfigManager().isMsgEnabled()) {
            PrivateMessageCommand privateMessageCommand = new PrivateMessageCommand(this);
            Objects.requireNonNull(this.getCommand("msg")).setExecutor(privateMessageCommand);
            if (getConfigManager().isReplyEnabled()) {
                Objects.requireNonNull(this.getCommand("reply")).setExecutor(new ReplyCommand(this, privateMessageCommand));
            }
        }
    }

    public void registerPlaceholders() {
        new Placeholders(this, groupManager).register();
    }

    // ------------------------------------------------------------------------------

    public CommandTimerManager getCommandTimerManager() { return commandTimerManager; }
    public ChatBotListener getChatBotListener() { return chatBotListener; }
    public ChatBotManager getChatBotManager() { return chatBotManager; }
    public AutoBroadcastManager getAutoBroadcastManager() { return autoBroadcastManager; }
    public GrammarListener getGrammarListener() { return grammarListener; }
    public ChannelsManager getChannelsManager() { return channelsManager; }
    public ChannelsConfigManager getChannelsConfigManager() { return channelsConfigManager; }
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
    public TranslateColors getTranslateColors() { return translateColors; }
    public ReplacerManager getReplacerManager() { return replacerManager; }
    public ChatColorManager getChatColorManager() { return chatColorManager; }
}

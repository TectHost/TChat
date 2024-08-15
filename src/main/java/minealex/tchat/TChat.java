package minealex.tchat;

import commands.*;
import config.*;
import hook.DiscordHook;
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
    private GroupManager groupManager;
    private BannedWordsManager bannedWordsManager;
    private BannedWords bannedWords;
    private BannedCommandsManager bannedCommandsManager;
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
    private RepeatMessagesListener repeatMessagesListener;
    private AntiBotListener antiBotListener;
    private PlayerJoinListener playerJoinListener;
    private ChatGamesManager chatGamesManager;
    private ChatGamesSender chatGamesSender;
    private AutoBroadcastSender autoBroadcastSender;
    private LogsManager logsManager;
    private ChatCooldownListener chatCooldownListener;
    private AntiFloodListener antiFloodListener;
    private AntiUnicodeListener antiUnicodeListener;
    private ChatClearCommand chatClearCommand;
    private MuteChatCommand muteChatCommand;
    private CommandProgrammerManager commandProgrammerManager;
    private CommandsManager commandsManager;
    private DiscordHook discordHook;
    private DiscordManager discordManager;
    private SocialSpyListener socialSpyListener;
    private LevelListener levelListener;
    private LevelsManager levelsManager;
    private DeathManager deathManager;
    private WorldsManager worldsManager;
    private ChatEnabledListener chatEnabledListener;
    private PlayerLeftListener playerLeftListener;
    private PlaceholdersConfig placeholdersConfig;

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
        ChatFormatListener chatFormatListener = new ChatFormatListener(this, configManager, groupManager);
        bannedWords = new BannedWords(this, bannedWordsManager);
        ChatListener chatListener = new ChatListener(this, chatFormatListener);
        antiAdvertising = new AntiAdvertising(this);
        capListener = new CapListener(this);
        grammarListener = new GrammarListener(this);
        chatBotListener = new ChatBotListener(this);
        repeatMessagesListener = new RepeatMessagesListener(this);
        playerJoinListener = new PlayerJoinListener(this);
        playerLeftListener = new PlayerLeftListener(this);
        PlayerMoveListener playerMoveListener = new PlayerMoveListener(this);
        antiBotListener = new AntiBotListener(this);
        autoBroadcastSender = new AutoBroadcastSender(this);
        chatCooldownListener = new ChatCooldownListener(this);
        antiFloodListener = new AntiFloodListener(this);
        antiUnicodeListener = new AntiUnicodeListener(this);
        new CommandProgrammerSender(this, commandProgrammerManager);
        discordHook = new DiscordHook(this);
        socialSpyListener = new SocialSpyListener(this);
        SignListener signListener = new SignListener(this);
        levelListener = new LevelListener(this);
        chatEnabledListener = new ChatEnabledListener(this);

        getServer().getPluginManager().registerEvents(new ChatPlaceholdersListener(this), this);
        getServer().getPluginManager().registerEvents(new PollListener(this), this);
        getServer().getPluginManager().registerEvents(signListener, this);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(bannedWords, this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(this), this);
        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(playerLeftListener, this);
        getServer().getPluginManager().registerEvents(playerMoveListener, this);
        getServer().getPluginManager().registerEvents(new DeathsListener(deathManager, this), this);

        PollScheduler.startPollChecker(this);
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
        chatGamesManager = new ChatGamesManager(this);
        chatGamesSender = new ChatGamesSender(this);
        logsManager = new LogsManager(this);
        commandProgrammerManager = new CommandProgrammerManager(this);
        commandsManager = new CommandsManager(this);
        discordManager = new DiscordManager(this);
        levelsManager = new LevelsManager(this);
        deathManager = new DeathManager(this);
        worldsManager = new WorldsManager(this);
        placeholdersConfig = new PlaceholdersConfig(this);
    }

    public void initializeManagers() {
        groupManager = new GroupManager(this);
        chatColorInventoryManager = new ChatColorInventoryManager(this);
        translateColors = new TranslateColors();
        channelsManager = new ChannelsManager();
    }

    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("tchat")).setExecutor(new Commands(this));
        if (getConfigManager().isChatColorEnabled()) {
            Objects.requireNonNull(this.getCommand("chatcolor")).setExecutor(new ChatColorCommand(this));
        }
        Objects.requireNonNull(this.getCommand("channel")).setExecutor(new ChannelCommand(this));
        Objects.requireNonNull(getCommand("channel")).setTabCompleter(new ChannelsCompleter(this));
        if (getConfigManager().isMsgEnabled()) {
            PrivateMessageCommand privateMessageCommand = new PrivateMessageCommand(this);
            Objects.requireNonNull(this.getCommand("msg")).setExecutor(privateMessageCommand);
            if (getConfigManager().isReplyEnabled()) {
                Objects.requireNonNull(this.getCommand("reply")).setExecutor(new ReplyCommand(this, privateMessageCommand));
            }
        }
        if (getConfigManager().isChatClearEnabled()) {
            chatClearCommand = new ChatClearCommand(this);
            Objects.requireNonNull(this.getCommand("chatclear")).setExecutor(new ChatClearCommand(this));
        }
        if (getConfigManager().isMuteChatEnabled()) {
            muteChatCommand = new MuteChatCommand(this);
            Objects.requireNonNull(this.getCommand("mutechat")).setExecutor(new MuteChatCommand(this));
        }
        new CustomCommands(this);
        if (getConfigManager().isPingEnabled()) {
            Objects.requireNonNull(this.getCommand("ping")).setExecutor(new PingCommand(this));
        }
        if (getConfigManager().isBroadcastEnabled()) {
            Objects.requireNonNull(this.getCommand("broadcast")).setExecutor(new BroadcastCommand(this));
        }
        if (getConfigManager().isWarningEnabled()) {
            Objects.requireNonNull(getCommand("warning")).setExecutor(new WarningCommand(this));
        }
        if (getConfigManager().isAnnouncementEnabled()) {
            Objects.requireNonNull(getCommand("announcement")).setExecutor(new AnnouncementCommand(this));
        }
        Objects.requireNonNull(this.getCommand("ignore")).setExecutor(new IgnoreCommand(this));
        Objects.requireNonNull(getCommand("poll")).setExecutor(new PollCommand(this));
        if (getConfigManager().isRulesEnabled()) {
            Objects.requireNonNull(getCommand("rules")).setExecutor(new RulesCommand(this));
        }
        if (getConfigManager().isPrintEnabled()) {
            Objects.requireNonNull(getCommand("print")).setExecutor(new PrintCommand(this));
        }
        AutoBroadcastCommand autoBroadcastCommand = new AutoBroadcastCommand(this);
        Objects.requireNonNull(getCommand("autobroadcast")).setExecutor(autoBroadcastCommand);
        Objects.requireNonNull(getCommand("autobroadcast")).setTabCompleter(new AutoBroadcastTabCompleter(autoBroadcastManager));
        Objects.requireNonNull(getCommand("plugin")).setExecutor(new PluginCommand(this));
        Objects.requireNonNull(getCommand("bannedcommands")).setExecutor(new BannedCommandsCommand(bannedCommandsManager, this));
        Objects.requireNonNull(getCommand("helpop")).setExecutor(new HelpOpCommand(this));
        Objects.requireNonNull(getCommand("showitem")).setExecutor(new ShowItemCommand(this));
        Objects.requireNonNull(getCommand("list")).setExecutor(new ListCommand(this));
        Objects.requireNonNull(getCommand("calculator")).setExecutor(new CalculatorCommand(this));
        Objects.requireNonNull(getCommand("player")).setExecutor(new PlayerCommand(this));
        Objects.requireNonNull(getCommand("server")).setExecutor(new ServerCommand(this));
    }

    public void registerPlaceholders() {
        new Placeholders(this, groupManager).register();
    }

    // ------------------------------------------------------------------------------

    public PlaceholdersConfig getPlaceholdersConfig() { return placeholdersConfig; }
    public AutoBroadcastSender getAutoBroadcastSender() { return autoBroadcastSender; }
    public PlayerLeftListener getPlayerLeftListener() { return playerLeftListener; }
    public ChatEnabledListener getChatEnabledListener() { return chatEnabledListener; }
    public WorldsManager getWorldsManager() { return worldsManager; }
    public LevelsManager getLevelsManager() { return levelsManager; }
    public LevelListener getLevelListener() { return levelListener; }
    public SocialSpyListener getSocialSpyListener() { return socialSpyListener; }
    public DiscordManager getDiscordManager() { return discordManager; }
    public DiscordHook getDiscordHook() { return discordHook; }
    public CommandsManager getCommandsManager() { return commandsManager; }
    public CommandProgrammerManager getCommandProgrammerManager() { return commandProgrammerManager; }
    public MuteChatCommand getMuteChatCommand() { return muteChatCommand; }
    public ChatClearCommand getChatClearCommand() { return chatClearCommand; }
    public AntiUnicodeListener getAntiUnicodeListener() { return antiUnicodeListener; }
    public AntiFloodListener getAntiFloodListener() { return antiFloodListener; }
    public ChatCooldownListener getChatCooldownListener() { return chatCooldownListener; }
    public LogsManager getLogsManager() { return logsManager; }
    public ChatGamesSender getChatGamesSender() { return chatGamesSender; }
    public ChatGamesManager getChatGamesManager() { return chatGamesManager; }
    public PlayerJoinListener getPlayerJoinListener() { return playerJoinListener; }
    public AntiBotListener getAntiBotListener() { return antiBotListener; }
    public RepeatMessagesListener getRepeatMessagesListener() { return repeatMessagesListener; }
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

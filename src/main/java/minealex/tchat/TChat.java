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
    private MuteChatCommand muteChatCommand;
    private CommandProgrammerManager commandProgrammerManager;
    private CommandsManager commandsManager;
    private DiscordHook discordHook;
    private DiscordManager discordManager;
    private SocialSpyListener socialSpyListener;
    private LevelListener levelListener;
    private LevelsManager levelsManager;
    private WorldsManager worldsManager;
    private ChatEnabledListener chatEnabledListener;
    private PlayerLeftListener playerLeftListener;
    private PlaceholdersConfig placeholdersConfig;
    private JoinManager joinManager;
    private MentionsManager mentionsManager;
    private CheckPlayerMuted checkPlayerMuted;
    private InvseeConfigManager invseeConfigManager;
    private ShowEnderChestConfigManager showEnderChestConfigManager;
    private TagsManager tagsManager;
    private TagsMenuConfigManager tagsMenuConfigManager;
    private TagsInventoryManager tagsInventoryManager;
    private TabCompleteListener tabCompleteListener;
    private RepeatCommandsListener repeatCommandsListener;
    private AntiAdvertisingManager antiAdvertisingManager;
    private AntiCapManager antiCapManager;
    private GrammarManager grammarManager;
    private PrivateMessagesConfigManager privateMessagesConfigManager;
    private AntiBotConfigManager antiBotConfigManager;
    private LoggerConfigManager loggerConfigManager;
    private CooldownsConfig cooldownsConfig;
    private AntiFloodConfig antiFloodConfig;
    private AntiUnicodeConfig antiUnicodeConfig;
    private BroadcastConfig broadcastConfig;
    private IgnoreConfig ignoreConfig;
    private SocialSpyConfig socialSpyConfig;
    private PollsConfig pollsConfig;
    private PingConfig pingConfig;
    private ChatColorConfig chatColorConfig;
    private SICConfig sicConfig;
    private ListConfig listConfig;
    private HelpOpConfig helpOpConfig;
    public RegisterCommands registerCommands;
    private CommandTimerSender commandTimerSender;
    private DeathManager deathManager;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "TChat" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Starting TChat...");

        initializeManagers();
        registers();
        registerPlaceholders();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "TChat" + ChatColor.GRAY + "] " + ChatColor.GREEN + "TChat started.");
    }

    @Override
    public void onDisable() {
        if (commandTimerSender != null) { commandTimerSender.stop(); }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "TChat" + ChatColor.GRAY + "] " + ChatColor.RED + "TChat stopped");
    }

    public void registers() {
        registerCommands = new RegisterCommands(this);

        Objects.requireNonNull(this.getCommand("tchat")).setExecutor(new Commands(this));

        configManager = new ConfigManager(this);
        saveManager = new SaveManager(this);

        registerJoins();
        registerBannedWords();
        registerAntiAdvertising();
        registerAntiCap();
        registerGrammar();
        registerChatBot();
        registerAntiBot();
        registerAutoBroadcast();
        registerCooldowns();
        registerAntiFlood();
        registerAntiUnicode();
        registerCommandProgrammer();
        registerDiscord();
        registerSocialSpy();
        registerLevels();
        registerMute();
        registerRepeatCommands();
        registerPlaceholdersConfig();
        registerPolls();
        registerDeath();
        registerBannedCommands();
        registerBroadcast();
        registerChannels();
        registerChatColor();
        registerCustomCommands();
        registerCommandTimer();
        registerHelpOp();
        registerIgnore();
        registerInvsee();
        registerList();
        registerLogger();
        registerMentions();
        registerPing();
        registerSic();
        registerTags();
        registerWorlds();
        registerMuteChat();
        registerMsg();
        registerCalculator();
        registerShowEnderChest();
        registerNick();
        registerStaffList();
        registerRealName();
        registerPluginCommand();
        registerPlayerCommand();
        registerSeenCommand();
        registerServerCommand();

        messagesManager = new MessagesManager(this);
        registerChatGames();
        registerReplacer();

        playerJoinListener = new PlayerJoinListener(this);
        playerLeftListener = new PlayerLeftListener(this);
        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(playerLeftListener, this);
        getServer().getPluginManager().registerEvents(new SignListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this, new ChatFormatListener(this, configManager, groupManager)), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
    }

    public void registerServerCommand() {
        if (getConfigManager().isServerEnabled()) {
            registerCommands.registerCommand("server", new ServerCommand(this));
        }
    }

    public void registerSeenCommand() {
        if (getConfigManager().isSeenEnabled()) {
            registerCommands.registerCommand("seen", new SeenCommand(this));
        }
    }

    public void registerPlayerCommand() {
        if (getConfigManager().isPlayerEnabled()) {
            registerCommands.registerCommand("player", new PlayerCommand(this));
        }
    }

    public void registerPluginCommand() {
        if (getConfigManager().isPluginEnabled()) {
            registerCommands.registerCommand("plugin", new PluginCommand(this));
        }
    }

    public void registerRealName() {
        if (getConfigManager().isRealNameEnabled()) {
            registerCommands.registerCommand("realname", new RealNameCommand(this));
        }
    }

    public void registerStaffList() {
        if (getConfigManager().isStaffListEnabled()) {
            registerCommands.registerCommand("stafflist", new StaffListCommand(this));
        }
    }

    public void registerNick() {
        if (getConfigManager().isNickEnabled()) {
            registerCommands.registerCommand("nick", new NickCommand(this));
        }
    }

    public void registerShowEnderChest() {
        if (getConfigManager().isSecEnabled()) {
            showEnderChestConfigManager = new ShowEnderChestConfigManager(this);
            registerCommands.registerCommand("showenderchest", new ShowEnderChestCommand(this));
        }
    }

    public void registerCalculator() {
        if (getConfigManager().isCalculatorEnabled()) {
            registerCommands.registerCommand("calculator", new CalculatorCommand(this));
        }
    }

    public void registerMute() {
        if (getConfigManager().isMuteEnabled()) {
            this.checkPlayerMuted = new CheckPlayerMuted(this);
            registerCommands.registerCommand("mute", new MuteCommand(this));
        }
    }

    public void registerRepeatCommands() {
        if (getConfigManager().isRepeatCommandsEnabled()) {
            repeatCommandsListener = new RepeatCommandsListener(this);
        }
    }

    public void registerMsg() {
        if (getConfigManager().isMsgEnabled()) {
            PrivateMessageCommand privateMessageCommand = new PrivateMessageCommand(this);
            registerCommands.registerCommand("msg", privateMessageCommand);
            privateMessagesConfigManager = new PrivateMessagesConfigManager(this);
            if (getConfigManager().isReplyEnabled()) {
                registerCommands.registerCommand("reply", new ReplyCommand(this, privateMessageCommand));
            }
        }
    }

    public void registerMuteChat() {
        if (getConfigManager().isMuteChatEnabled()) {
            muteChatCommand = new MuteChatCommand(this);
            registerCommands.registerCommand("mutechat", muteChatCommand);
        }
    }

    public void registerWorlds() {
        if (getConfigManager().isWorldsEnabled()) {
            worldsManager = new WorldsManager(this);
            chatEnabledListener = new ChatEnabledListener(this);
        }
    }

    public void registerTags() {
        if (getConfigManager().isTagsEnabled()) {
            tagsManager = new TagsManager(this);
            if (getTagsManager().isTagsMenusEnabled()) {
                tagsMenuConfigManager = new TagsMenuConfigManager(this);
                tagsInventoryManager = new TagsInventoryManager(this, tagsMenuConfigManager);
            }
            new TagsCommand(this, tagsManager, tagsInventoryManager);
        }
    }

    public void registerSocialSpy() {
        if (getConfigManager().isSocialSpyEnabled()) {
            socialSpyConfig = new SocialSpyConfig(this);
            socialSpyListener = new SocialSpyListener(this);
            registerCommands.registerCommand("socialspy", new SocialSpyCommand(this));
            registerCommands.registerTabCompleter("socialspy", new SocialSpyCommand(this));
        }
    }

    public void registerSic() {
        if (getConfigManager().isSicEnabled()) {
            sicConfig = new SICConfig(this);
            registerCommands.registerCommand("showitem", new ShowItemCommand(this));
        }
    }

    public void registerReplacer() {
        if (getConfigManager().isReplacerEnabled()) {
            replacerManager = new ReplacerManager(this);
            registerCommands.registerCommand("replacer", new ReplacerCommand(this));
        }
    }

    public void registerPolls() {
        if (getConfigManager().isPollsEnabled()) {
            pollsConfig = new PollsConfig(this);
            getServer().getPluginManager().registerEvents(new PollListener(this), this);
            PollScheduler.startPollChecker(this);
            registerCommands.registerCommand("poll", new PollCommand(this));
        }
    }

    public void registerPlaceholdersConfig() {
        if (getConfigManager().isPlaceholdersEnabled()) {
            placeholdersConfig = new PlaceholdersConfig(this);
            getServer().getPluginManager().registerEvents(new ChatPlaceholdersListener(this, new ShowItemCommand(this)), this);
        }
    }

    public void registerPing() {
        if (getConfigManager().isPingEnabled()) {
            pingConfig = new PingConfig(this);
            registerCommands.registerCommand("ping", new PingCommand(this));
        }
    }

    public void registerMentions() {
        if (getConfigManager().isMentionsEnabled()) {
            mentionsManager = new MentionsManager(this);
            registerCommands.registerCommand("mention", new MentionCommand(this));
        }
    }

    public void registerLogger() {
        if (getConfigManager().isLoggerEnabled()) {
            loggerConfigManager = new LoggerConfigManager(this);
            logsManager = new LogsManager(this);
            registerCommands.registerCommand("logs", new LogsCommand(this));
            registerCommands.registerTabCompleter("logs", new LogsCommand(this));
        }
    }

    public void registerList() {
        if (getConfigManager().isListEnabled()) {
            listConfig = new ListConfig(this);
            registerCommands.registerCommand("list", new ListCommand(this));
        }
    }

    public void registerLevels() {
        if (getConfigManager().isLevelsEnabled()) {
            levelsManager = new LevelsManager(this);
            levelListener = new LevelListener(this);
        }
    }

    public void registerJoins() {
        if (getConfigManager().isJoinsEnabled()) {
            joinManager = new JoinManager(this);
        }
    }

    public void registerInvsee() {
        if (getConfigManager().isInvseeEnabled()) {
            invseeConfigManager = new InvseeConfigManager(this);
            registerCommands.registerCommand("invsee", new InvseeCommand(this));
        }
    }

    public void registerIgnore() {
        if (getConfigManager().isIgnoreEnabled()) {
            ignoreConfig = new IgnoreConfig(this);
            registerCommands.registerCommand("ignore", new IgnoreCommand(this));
        }
    }

    public void registerHelpOp() {
        if (getConfigManager().isHelpOpEnabled()) {
            helpOpConfig = new HelpOpConfig(this);
            registerCommands.registerCommand("helpop", new HelpOpCommand(this));
        }
    }

    public void registerGrammar() {
        if (getConfigManager().isGrammarEnabled()) {
            grammarManager = new GrammarManager(this);
            grammarListener = new GrammarListener(this);
            repeatMessagesListener = new RepeatMessagesListener(this);
        }
    }

    public void registerDiscord() {
        if (getConfigManager().isDiscordEnabled()) {
            discordManager = new DiscordManager(this);
            discordHook = new DiscordHook(this);
        }
    }

    public void registerDeath() {
        if (getConfigManager().isDeathEnabled()) {
            deathManager = new DeathManager(this);
            getServer().getPluginManager().registerEvents(new DeathsListener(deathManager, this), this);
        }
    }

    public void registerCooldowns() {
        if (getConfigManager().isCooldownsEnabled()) {
            cooldownsConfig = new CooldownsConfig(this);
            chatCooldownListener = new ChatCooldownListener(this);
        }
    }

    public void registerCommandTimer() {
        if (getConfigManager().isCommandTimerEnabled()) {
            commandTimerManager = new CommandTimerManager(this);
            registerCommands.registerCommand("commandtimer", new CommandTimerCommand(this));
            commandTimerSender = new CommandTimerSender(this);
        }
    }

    public void registerCustomCommands() {
        if (getConfigManager().isCustomCommandsEnabled()) {
            commandsManager = new CommandsManager(this);
            new CustomCommands(this);
        }
    }

    public void registerCommandProgrammer() {
        if (getConfigManager().isCommandProgrammerEnabled()) {
            commandProgrammerManager = new CommandProgrammerManager(this);
            new CommandProgrammerSender(this, commandProgrammerManager);
        }
    }

    public void registerChatGames() {
        if (getConfigManager().isChatGamesEnabled()) {
            chatGamesManager = new ChatGamesManager(this);
            chatGamesSender = new ChatGamesSender(this);
            registerCommands.registerCommand("chatgames", new ChatGamesCommand(this, chatGamesSender, chatGamesManager));
        }
    }

    public void registerChatColor() {
        if (getConfigManager().isChatColorEnabled()) {
            chatColorConfig = new ChatColorConfig(this);
            if (getChatColorConfig().isChatColorMenuEnabled()) {
                chatColorManager = new ChatColorManager(this);
                chatColorInventoryManager = new ChatColorInventoryManager(this);
            }
            registerCommands.registerCommand("chatcolor", new ChatColorCommand(this));
        }
    }

    public void registerChatBot() {
        if (getConfigManager().isChatBotEnabled()) {
            chatBotManager = new ChatBotManager(this);
            chatBotListener = new ChatBotListener(this);
        }
    }

    public void registerChannels() {
        if (getConfigManager().isChannelsEnabled()) {
            channelsConfigManager = new ChannelsConfigManager(this);
            channelsManager = new ChannelsManager();
            registerCommands.registerCommand("channel", new ChannelCommand(this));
            registerCommands.registerTabCompleter("channel", new ChannelsCompleter(this));
        }
    }

    public void registerBroadcast() {
        if (getConfigManager().isBroadcastEnabled()) {
            broadcastConfig = new BroadcastConfig(this);

            if (getBroadcastConfig().isBroadcastEnabled()) {
                registerCommands.registerCommand("broadcast", new BroadcastCommand(this));
            }
            if (getBroadcastConfig().isWarningEnabled()) {
                registerCommands.registerCommand("warning", new WarningCommand(this));
            }
            if (getBroadcastConfig().isAnnouncementEnabled()) {
                registerCommands.registerCommand("announcement", new AnnouncementCommand(this));
            }
        }
    }

    public void registerBannedWords() {
        if (getConfigManager().isBannedWordsEnabled()) {
            bannedWordsManager = new BannedWordsManager(this);
            bannedWords = new BannedWords(this, bannedWordsManager);
            getServer().getPluginManager().registerEvents(bannedWords, this);
            registerCommands.registerCommand("bannedwords", new BannedWordsCommand(this, bannedWordsManager));
        }
    }

    public void registerBannedCommands() {
        if (getConfigManager().isBannedCommandsEnabled()) {
            bannedCommandsManager = new BannedCommandsManager(this);
            registerCommands.registerCommand("bannedcommands", new BannedCommandsCommand(bannedCommandsManager, this));
            tabCompleteListener = new TabCompleteListener(this);
            getServer().getPluginManager().registerEvents(tabCompleteListener, this);
        }
    }

    public void registerAutoBroadcast() {
        if (getConfigManager().isAutoBroadcastEnabled()) {
            autoBroadcastManager = new AutoBroadcastManager(this);
            autoBroadcastSender = new AutoBroadcastSender(this);

            registerCommands.registerCommand("autobroadcast", new AutoBroadcastCommand(this));

            registerCommands.registerTabCompleter("autobroadcast", new AutoBroadcastTabCompleter(autoBroadcastManager));
        }
    }

    public void registerAntiUnicode() {
        if (getConfigManager().isAntiUnicodeEnabled()) {
            antiUnicodeConfig = new AntiUnicodeConfig(this);
            antiUnicodeListener = new AntiUnicodeListener(this);
        }
    }

    public void registerAntiFlood() {
        if (getConfigManager().isAntiFloodEnabled()) {
            antiFloodConfig = new AntiFloodConfig(this);
            antiFloodListener = new AntiFloodListener(this);
        }
    }

    public void registerAntiCap() {
        if (getConfigManager().isAntiCapEnabled()) {
            antiCapManager = new AntiCapManager(this);
            capListener = new CapListener(this);
        }
    }

    public void registerAntiBot() {
        if (getConfigManager().isAntiBotEnabled()) {
            antiBotConfigManager = new AntiBotConfigManager(this);
            antiBotListener = new AntiBotListener(this);
        }
    }

    public void registerAntiAdvertising() {
        if (getConfigManager().isAntiAdvertisingEnabled()) {
            antiAdvertisingManager = new AntiAdvertisingManager(this);
            antiAdvertising = new AntiAdvertising(this);
        }
    }

    public void initializeManagers() {
        groupManager = new GroupManager(this);
        translateColors = new TranslateColors();
        new org.bstats.bukkit.Metrics(this, 23305);
    }

    public void registerPlaceholders() {
        new Placeholders(this, groupManager).register();
    }

    // ------------------------------------------------------------------------------

    public DeathManager getDeathManager() {return deathManager;}
    public HelpOpConfig getHelpOpConfig() {return helpOpConfig;}
    public ListConfig getListConfig() {return listConfig;}
    public SICConfig getSicConfig() {return sicConfig;}
    public ChatColorConfig getChatColorConfig() {return chatColorConfig;}
    public PingConfig getPingConfig() {return pingConfig;}
    public PollsConfig getPollsConfig() {return pollsConfig;}
    public SocialSpyConfig getSocialSpyConfig() {return socialSpyConfig;}
    public IgnoreConfig getIgnoreConfig() {return ignoreConfig;}
    public BroadcastConfig getBroadcastConfig() {return broadcastConfig;}
    public AntiUnicodeConfig getAntiUnicodeConfig() {return antiUnicodeConfig;}
    public AntiFloodConfig getAntiFloodConfig() {return antiFloodConfig;}
    public CooldownsConfig getCooldownsConfig() {return cooldownsConfig;}
    public LoggerConfigManager getLoggerConfigManager() {return loggerConfigManager;}
    public AntiBotConfigManager getAntiBotConfigManager() {return antiBotConfigManager;}
    public PrivateMessagesConfigManager getPrivateMessagesConfigManager() {return privateMessagesConfigManager;}
    public GrammarManager getGrammarManager() {return grammarManager;}
    public AntiCapManager getAntiCapManager() {return antiCapManager;}
    public AntiAdvertisingManager getAntiAdvertisingManager() {return antiAdvertisingManager;}
    public RepeatCommandsListener getRepeatCommandsListener() {return repeatCommandsListener;}
    public TabCompleteListener getTabCompleteListener() {return tabCompleteListener;}
    public TagsMenuConfigManager getTagsMenuConfigManager() {return tagsMenuConfigManager;}
    public TagsManager getTagsManager() {return tagsManager;}
    public ShowEnderChestConfigManager getShowEnderChestConfigManager() {return showEnderChestConfigManager;}
    public InvseeConfigManager getInvseeConfigManager() {return invseeConfigManager;}
    public CheckPlayerMuted getCheckPlayerMuted() {return checkPlayerMuted;}
    public MentionsManager getMentionsManager() { return mentionsManager; }
    public JoinManager getJoinManager() { return joinManager; }
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

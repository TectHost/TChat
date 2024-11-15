package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Commands implements CommandExecutor, TabCompleter {
    private final TChat plugin;

    public Commands(@NotNull TChat plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("tchat")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("tchat")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        String noPerm = plugin.getMessagesManager().getNoPermission();
        if (sender.hasPermission("tchat.admin") || sender.hasPermission("tchat.admin.version") || sender.hasPermission("tchat.admin.reload")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("tchat.admin") || sender.hasPermission("tchat.admin.reload")) {
                        plugin.getConfigManager().reloadConfig();

                        if (plugin.getConfigManager().isAntiAdvertisingEnabled()) plugin.getAntiAdvertisingManager().reloadConfig();
                        if (plugin.getConfigManager().isAntiBotEnabled()) plugin.getAntiBotConfigManager().reloadConfig();
                        if (plugin.getConfigManager().isAntiCapEnabled()) plugin.getAntiCapManager().reloadConfig();
                        if (plugin.getConfigManager().isAntiFloodEnabled()) plugin.getAntiFloodConfig().reloadConfig();
                        if (plugin.getConfigManager().isAntiUnicodeEnabled()) plugin.getAntiUnicodeConfig().reloadConfig();
                        if (plugin.getConfigManager().isAutoBroadcastEnabled()) plugin.getAutoBroadcastManager().reloadConfig();
                        if (plugin.getConfigManager().isBannedCommandsEnabled()) {
                            plugin.getBannedCommandsManager().reloadConfig();
                            plugin.getTabCompleteListener().reloadConfig();
                        }
                        if (plugin.getConfigManager().isBannedWordsEnabled()) plugin.getBannedWordsManager().reloadBannedWords();
                        if (plugin.getConfigManager().isBroadcastEnabled()) plugin.getBroadcastConfig().reloadConfig();
                        if (plugin.getConfigManager().isChannelsEnabled()) plugin.getChannelsConfigManager().reloadConfig();
                        if (plugin.getConfigManager().isChatBotEnabled()) plugin.getChatBotManager().reloadConfig();
                        if (plugin.getConfigManager().isChatColorEnabled()) plugin.getChatColorConfig().reloadConfig();
                        if (plugin.getChatColorConfig().isChatColorMenuEnabled()) plugin.getChatColorManager().reloadConfig();
                        if (plugin.getConfigManager().isChatGamesEnabled()) plugin.getChatGamesManager().reloadConfig();
                        if (plugin.getConfigManager().isCommandProgrammerEnabled()) plugin.getCommandProgrammerManager().reloadConfig();
                        if (plugin.getConfigManager().isCustomCommandsEnabled()) plugin.getCommandsManager().reloadConfig();
                        if (plugin.getConfigManager().isCommandTimerEnabled()) plugin.getCommandTimerManager().reloadConfig();
                        if (plugin.getConfigManager().isCooldownsEnabled()) plugin.getCooldownsConfig().reloadConfig();
                        if (plugin.getConfigManager().isDeathEnabled()) plugin.getDeathManager().reloadConfig();
                        if (plugin.getConfigManager().isDiscordEnabled()) plugin.getDiscordManager().reloadConfig();
                        if (plugin.getConfigManager().isGrammarEnabled()) plugin.getGrammarManager().reloadConfig();
                        if (plugin.getConfigManager().isHelpOpEnabled()) plugin.getHelpOpConfig().reloadConfig();
                        if (plugin.getConfigManager().isIgnoreEnabled()) plugin.getIgnoreConfig().reloadConfig();
                        if (plugin.getConfigManager().isInvseeEnabled()) plugin.getInvseeConfigManager().reloadConfig();
                        if (plugin.getConfigManager().isJoinsEnabled()) plugin.getJoinManager().reloadConfig();
                        if (plugin.getConfigManager().isLevelsEnabled()) plugin.getLevelsManager().reloadConfig();
                        if (plugin.getConfigManager().isListEnabled()) plugin.getListConfig().reloadConfig();
                        if (plugin.getConfigManager().isLoggerEnabled()) plugin.getLoggerConfigManager().reloadConfig();
                        if (plugin.getConfigManager().isMentionsEnabled()) plugin.getMentionsManager().reloadConfig();
                        if (plugin.getConfigManager().isPingEnabled()) plugin.getPingConfig().reloadConfig();
                        if (plugin.getConfigManager().isPlaceholdersEnabled()) plugin.getPlaceholdersConfig().reloadConfig();
                        if (plugin.getConfigManager().isPollsEnabled()) plugin.getPollsConfig().reloadConfig();
                        if (plugin.getConfigManager().isMsgEnabled()) plugin.getPrivateMessagesConfigManager().reloadConfig();
                        if (plugin.getConfigManager().isReplacerEnabled()) plugin.getReplacerManager().reloadConfig();
                        if (plugin.getConfigManager().isSecEnabled()) plugin.getShowEnderChestConfigManager().reloadConfig();
                        if (plugin.getConfigManager().isSicEnabled()) plugin.getSicConfig().reloadConfig();
                        if (plugin.getConfigManager().isSocialSpyEnabled()) plugin.getSocialSpyConfig().reloadConfig();
                        if (plugin.getConfigManager().isTagsEnabled()) plugin.getTagsManager().reloadConfig();
                        if (plugin.getConfigManager().isTagsEnabled()) plugin.getTagsMenuConfigManager().reloadConfig();
                        if (plugin.getConfigManager().isWorldsEnabled()) plugin.getWorldsManager().reloadConfig();

                        plugin.getGroupManager().reloadGroups();
                        plugin.getSaveManager().reloadConfig();
                        plugin.getMessagesManager().reloadConfig();
                        String message = plugin.getMessagesManager().getReloadMessage();
                        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                    } else {
                        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + noPerm));
                    }
                } else if (args[0].equalsIgnoreCase("version")) {
                    if (sender.hasPermission("tchat.admin") || sender.hasPermission("tchat.admin.version")) {
                        String version = plugin.getDescription().getVersion();
                        String message = plugin.getMessagesManager().getVersionMessage().replace("%version%", version);
                        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                    } else {
                        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + noPerm));
                    }
                } else if (args[0].equalsIgnoreCase("mute")) {
                    plugin.getMuteChatCommand().onCommand(sender, command, alias, args);
                } else if (args[0].equalsIgnoreCase("group")) {
                    if (sender.hasPermission("tchat.admin.user-group") || sender.hasPermission("tchat.admin")) {
                        if (args.length == 3) {
                            String targetPlayerName = args[1];
                            String targetGroupName = args[2];

                            if (plugin.getGroupManager().assignGroupToUser(targetPlayerName, targetGroupName)) {
                                String m = plugin.getMessagesManager().getGroupAssigned();
                                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
                            } else {
                                String m = plugin.getMessagesManager().getUnknownGroup();
                                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
                            }
                        } else {
                            String m = plugin.getMessagesManager().getUsageGroup();
                            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
                        }
                    } else {
                        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + noPerm));
                    }
                } else {
                    String message = plugin.getMessagesManager().getUnknownMessage();
                    sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                }
            } else {
                List<String> helpMessage = plugin.getMessagesManager().getChatMessage();
                for (String message : helpMessage) {
                    sender.sendMessage(plugin.getTranslateColors().translateColors(null, message));
                }
            }
        } else {
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + noPerm));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("tchat.admin.reload") || sender.hasPermission("tchat.admin")) {
                completions.add("reload");
            }
            if (sender.hasPermission("tchat.admin.version") || sender.hasPermission("tchat.admin")) {
                completions.add("version");
            }
            if (sender.hasPermission("tchat.admin.mutechat") || sender.hasPermission("tchat.admin")) {
                completions.add("mute");
            }
            if (sender.hasPermission("tchat.admin.user-group") || sender.hasPermission("tchat.admin")) {
                completions.add("group");
            }
        }

        return completions;
    }
}

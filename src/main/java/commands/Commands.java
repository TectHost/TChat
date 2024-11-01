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
                        plugin.getMessagesManager().reloadConfig();
                        plugin.getGroupManager().reloadGroups();
                        plugin.getBannedWordsManager().reloadBannedWords();
                        plugin.getBannedCommandsManager().reloadConfig();
                        plugin.getReplacerManager().reloadConfig();
                        plugin.getSaveManager().reloadConfig();
                        plugin.getChatColorManager().reloadConfig();
                        plugin.getChannelsConfigManager().reloadConfig();
                        plugin.getAutoBroadcastManager().reloadConfig();
                        plugin.getChatBotManager().reloadConfig();
                        plugin.getCommandTimerManager().reloadConfig();
                        plugin.getChatGamesManager().reloadConfig();
                        plugin.getCommandProgrammerManager().reloadConfig();
                        plugin.getCommandsManager().reloadConfig();
                        plugin.getDiscordManager().reloadConfig();
                        plugin.getLevelsManager().reloadConfig();
                        plugin.getWorldsManager().reloadConfig();
                        plugin.getPlaceholdersConfig().reloadConfig();
                        plugin.getJoinManager().reloadConfig();
                        plugin.getMentionsManager().reloadConfig();
                        plugin.getInvseeConfigManager().reloadConfig();
                        plugin.getChatGamesSender().restartGame();
                        plugin.getAutoBroadcastSender().restartBroadcasts();
                        plugin.getTagsManager().reloadConfig();
                        plugin.getTagsMenuConfigManager().reloadConfig();
                        plugin.getTabCompleteListener().reloadConfig();
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
                } else if (args[0].equalsIgnoreCase("clear")) {
                    plugin.getChatClearCommand().onCommand(sender, command, alias, args);
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
            if (sender.hasPermission("tchat.admin.chatclear") || sender.hasPermission("tchat.admin")) {
                completions.add("clear");
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

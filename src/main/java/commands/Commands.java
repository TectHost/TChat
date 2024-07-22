package commands;

import minealex.tchat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import utils.TranslateHexColorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Commands implements CommandExecutor, TabCompleter {
    private final TChat plugin;

    public Commands(TChat plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("tchat")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("tchat")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        Player Player = null;
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
                        String message = plugin.getMessagesManager().getReloadMessage();
                        String prefix = plugin.getMessagesManager().getPrefix();
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                        } else {
                            Player = ((Player) sender).getPlayer();
                            sender.sendMessage(plugin.getTranslateColors().translateColors(Player, prefix + message));
                        }
                    } else {
                        String message = plugin.getMessagesManager().getNoPermission();
                        String prefix = plugin.getMessagesManager().getPrefix();
                        sender.sendMessage(plugin.getTranslateColors().translateColors(Player, prefix + message));
                    }
                } else if (args[0].equalsIgnoreCase("version")) {
                    if (sender.hasPermission("tchat.admin") || sender.hasPermission("tchat.admin.version")) {
                        String version = plugin.getDescription().getVersion();
                        String message = plugin.getMessagesManager().getVersionMessage().replace("%version%", version);
                        String prefix = plugin.getMessagesManager().getPrefix();
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                        } else {
                            Player = ((Player) sender).getPlayer();
                            sender.sendMessage(plugin.getTranslateColors().translateColors(Player, prefix + message));
                        }
                    } else {
                        String message = plugin.getMessagesManager().getNoPermission();
                        String prefix = plugin.getMessagesManager().getPrefix();
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                        } else {
                            Player = ((Player) sender).getPlayer();
                            sender.sendMessage(plugin.getTranslateColors().translateColors(Player, prefix + message));
                        }
                    }
                } else {
                    String message = plugin.getMessagesManager().getUnknownMessage();
                    String prefix = plugin.getMessagesManager().getPrefix();
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                    } else {
                        Player = ((Player) sender).getPlayer();
                        sender.sendMessage(plugin.getTranslateColors().translateColors(Player, prefix + message));
                    }
                }
            } else {
                // Mensaje de ayuda
                String helpMessage = "Usage: /tchat <reload|version>";
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
            }
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            String prefix = plugin.getMessagesManager().getPrefix();
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            } else {
                Player = ((Player) sender).getPlayer();
                sender.sendMessage(plugin.getTranslateColors().translateColors(Player, prefix + message));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
            completions.add("version");
        }

        return completions;
    }
}

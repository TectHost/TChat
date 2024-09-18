package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IgnoreCommand implements CommandExecutor {
    private final TChat plugin;

    public IgnoreCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        List<String> blacklist = plugin.getConfigManager().getBlackLIgnore();

        if (sender.hasPermission("tchat.ignore") || sender.hasPermission("tchat.admin")) {
            if (args.length == 0) {
                String message = plugin.getMessagesManager().getIgnoreUsage();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("tchat.ignore.list")) {
                    List<String> ignoreList = plugin.getSaveManager().getIgnoreList(player.getUniqueId());
                    if (ignoreList.isEmpty()) {
                        String message = plugin.getMessagesManager().getIgnoreListEmpty();
                        sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    } else {
                        String ignoredPlayers = ignoreList.stream()
                                .map(UUID::fromString)
                                .map(uuid -> plugin.getServer().getOfflinePlayer(uuid).getName())
                                .collect(Collectors.joining(", "));
                        String message = plugin.getMessagesManager().getIgnoreListMessage().replace("%players%", ignoredPlayers);
                        sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    }
                } else {
                    String message = plugin.getMessagesManager().getNoPermission();
                    sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
                return true;
            }

            String targetName = args[0];
            Player target = plugin.getServer().getPlayer(targetName);

            if (target == null) {
                String message = plugin.getMessagesManager().getPlayerNotFound().replace("%player%", targetName);
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return true;
            }

            UUID senderId = player.getUniqueId();
            UUID targetId = target.getUniqueId();

            if (blacklist.contains(targetId.toString())) {
                String message = plugin.getMessagesManager().getIgnoreBlacklist().replace("%player%", target.getName());
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return true;
            }

            if (targetId.equals(senderId)) {
                String message = plugin.getMessagesManager().getIgnoreSelf();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                return true;
            }

            List<String> ignoreList = plugin.getSaveManager().getIgnoreList(senderId);
            if (ignoreList.contains(targetId.toString())) {
                String message = plugin.getMessagesManager().getIgnoreAlready().replace("%player%", target.getName());
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                plugin.getSaveManager().removeIgnore(senderId, targetId);
                return true;
            }

            ignoreList.add(targetId.toString());
            plugin.getSaveManager().setIgnore(senderId, ignoreList);

            if (plugin.getConfigManager().isIgnoreLogEnabled()) {
                plugin.getLogsManager().logIgnore(player.getName(), target.getName());
            }

            String addedMessage = plugin.getMessagesManager().getIgnoreMessage().replace("%player%", target.getName());
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + addedMessage));

        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }
        return true;
    }
}

package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ListCommand implements CommandExecutor {

    private final TChat plugin;

    public ListCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.admin.list")) {
            StringBuilder onlinePlayers = new StringBuilder(plugin.getConfigManager().getListHeader());

            for (Player p : Bukkit.getOnlinePlayers()) {
                onlinePlayers.append(p.getName()).append(plugin.getTranslateColors().translateColors(player, plugin.getConfigManager().getListAppend()));
            }

            if (onlinePlayers.length() > 2) {
                onlinePlayers.setLength(onlinePlayers.length() - 2);
            }

            player.sendMessage(plugin.getTranslateColors().translateColors(player, onlinePlayers.toString()));

            player.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getConfigManager().getListFooter()));
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }

        return true;
    }
}

package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShowCoordsCommand implements CommandExecutor {
    private final TChat plugin;

    public ShowCoordsCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();

        if (!(sender instanceof Player player)) {
            String m = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return true;
        }

        if (player.hasPermission("tchat.scc") || player.hasPermission("tchat.admin")) {
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();

            String format = plugin.getConfigManager().getSccFormat();
            format = format.replace("%x", String.valueOf(x)).replace("%y", String.valueOf(y)).replace("%z", String.valueOf(z));

            Bukkit.broadcastMessage(plugin.getTranslateColors().translateColors(player, format));
        } else {
            String noPermissionMessage = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + noPermissionMessage));
        }

        return true;
    }
}

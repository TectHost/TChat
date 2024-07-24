package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {
    private final TChat plugin;

    public BroadcastCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (sender.hasPermission("tchat.admin.broadcast") || sender.hasPermission("tchat.admin")) {
            if (args.length == 0) {
                String message = plugin.getMessagesManager().getBroadcastUsage();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return true;
            }

            String message = String.join(" ", args);

            String format = plugin.getConfigManager().getBroadcastFormat();

            format = format.replace("%message%", message);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(plugin.getTranslateColors().translateColors(player, format));
            }
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        }
        return true;
    }
}

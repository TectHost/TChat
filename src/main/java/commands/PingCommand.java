package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {

    private final TChat plugin;

    public PingCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (player.hasPermission("tchat.ping") ||player.hasPermission("tchat.admin")) {
            int ping = player.getPing();

            String message = plugin.getMessagesManager().getPing();
            message = message.replace("%ping%", String.valueOf(ping));
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }

        return true;
    }
}

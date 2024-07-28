package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PrintCommand implements CommandExecutor {
    private final TChat plugin;

    public PrintCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (sender.hasPermission("tchat.admin.print") || sender.hasPermission("tchat.admin")) {
            if (args.length == 0) {
                String message = plugin.getMessagesManager().getPrintUsage();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return true;
            }

            String message = String.join(" ", args);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(plugin.getTranslateColors().translateColors(player, message));
            }
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        }

        return true;
    }
}

package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SeenCommand implements CommandExecutor {

    private final TChat plugin;

    public SeenCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!commandSender.hasPermission("tchat.admin") && !commandSender.hasPermission("tchat.command.seen") && !commandSender.hasPermission("tchat.admin.command.seen")) {
            String message = plugin.getMessagesManager().getNoPermission();
            commandSender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        }

        if (args.length != 1) {
            String message = plugin.getMessagesManager().getUsageSeen();
            commandSender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        String playerName = args[0];
        Player player = Bukkit.getPlayerExact(playerName);

        if (player == null) {
            String message = plugin.getMessagesManager().getPlayerNotFound();
            commandSender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        for (String m : plugin.getMessagesManager().getSeen()) {
            commandSender.sendMessage(plugin.getTranslateColors().translateColors(player, m));
        }

        if (commandSender.hasPermission("tchat.admin") || commandSender.hasPermission("tchat.admin.command.seen")) {
            for (String m : plugin.getMessagesManager().getSeenAdmin()) {
                commandSender.sendMessage(plugin.getTranslateColors().translateColors(player, m));
            }
        }

        return true;
    }
}
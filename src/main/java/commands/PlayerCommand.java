package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCommand implements CommandExecutor {

    private final TChat plugin;

    public PlayerCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!sender.hasPermission("tchat.player")) {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (args.length != 1) {
            String message = plugin.getMessagesManager().getUsagePlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayerExact(playerName);

        if (targetPlayer == null) {
            String message = plugin.getMessagesManager().getPlayerNotFound();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        for (String m : plugin.getMessagesManager().getPlayerMessage()) {
            sender.sendMessage(plugin.getTranslateColors().translateColors(targetPlayer, m));
        }

        if (sender.hasPermission("tchat.admin.player")) {
            for (String m : plugin.getMessagesManager().getPlayerMessageAdmin()) {
                sender.sendMessage(plugin.getTranslateColors().translateColors(targetPlayer, m));
            }
        }

        return true;
    }
}
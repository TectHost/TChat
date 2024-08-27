package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RealNameCommand implements CommandExecutor {

    private final TChat plugin;

    public RealNameCommand(@NotNull TChat plugin) {
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

        if (args.length != 1) {
            String message = plugin.getMessagesManager().getUsageRealName();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String providedNick = args[0];

        UUID playerId = plugin.getSaveManager().getPlayerIdByNick(providedNick);
        if (playerId == null) {
            String message = plugin.getMessagesManager().getNoPlayerRealName().replace("%nick%", providedNick);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        Player realPlayer = Bukkit.getPlayer(playerId);
        if (realPlayer == null) {
            String message = plugin.getMessagesManager().getOfflineRealName();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String message = plugin.getMessagesManager().getRealName().replace("%nick%", providedNick).replace("%player%", realPlayer.getName());
        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));

        return true;
    }
}

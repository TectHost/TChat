package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class NickCommand implements CommandExecutor {

    private final TChat plugin;

    public NickCommand(@NotNull TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (args.length == 0) {
            String message = plugin.getMessagesManager().getUsageNick();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "set":
                handleSetNick(player, args);
                break;
            case "remove":
                handleRemoveNick(player);
                break;
            default:
                String message = plugin.getMessagesManager().getUsageNick();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                break;
        }

        return true;
    }

    private void handleSetNick(Player player, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (args.length < 2) {
            String message = plugin.getMessagesManager().getUsageNickSet();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return;
        }

        String newNick = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        plugin.getSaveManager().setNick(player.getUniqueId(), newNick);
        String message = plugin.getMessagesManager().getNickSet();
        message = message.replace("%nick%", newNick);
        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
    }

    private void handleRemoveNick(Player player) {
        plugin.getSaveManager().setNick(player.getUniqueId(), null);
        player.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getPrefix() + plugin.getMessagesManager().getNickRemove()));
    }
}

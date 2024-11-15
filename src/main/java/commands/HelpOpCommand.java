package commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import minealex.tchat.TChat;
import org.jetbrains.annotations.NotNull;

public class HelpOpCommand implements CommandExecutor {

    private final TChat plugin;

    public HelpOpCommand(TChat plugin) {
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

        if (!player.hasPermission("tchat.helpop")) {
            String message = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        if (args.length == 0) {
            String message = plugin.getMessagesManager().getUsageHelpOp();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String message = String.join(" ", args);
        String formattedMessage = plugin.getHelpOpConfig().getHelpOpFormat();
        formattedMessage = formattedMessage.replace("%player%", player.getName());
        formattedMessage = formattedMessage.replace("%message%", message);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("tchat.admin") || onlinePlayer.hasPermission("tchat.admin.helpop")) {
                onlinePlayer.sendMessage(plugin.getTranslateColors().translateColors(player, formattedMessage));
            }
        }

        String message1 = plugin.getMessagesManager().getHelpOp();
        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message1));
        return true;
    }
}

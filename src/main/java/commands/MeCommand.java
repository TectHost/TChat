package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MeCommand implements CommandExecutor {
    private final TChat plugin;

    public MeCommand(TChat plugin) {
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

        if (sender.hasPermission("tchat.me") || sender.hasPermission("tchat.admin")) {
            if (args.length == 0) {
                String usageMessage = plugin.getMessagesManager().getMeUsage();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + usageMessage));
                return true;
            }

            String message = String.join(" ", args);
            String format = plugin.getConfigManager().getMeFormat().replace("%message%", message);

            Bukkit.broadcastMessage(plugin.getTranslateColors().translateColors(player, format));
        } else {
            String noPermissionMessage = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + noPermissionMessage));
        }

        return true;
    }
}

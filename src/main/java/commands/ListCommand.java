package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ListCommand implements CommandExecutor {

    private final TChat plugin;

    public ListCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();

        if (sender.hasPermission("tchat.admin") || sender.hasPermission("tchat.admin.list")) {
            StringBuilder onlinePlayers = new StringBuilder(plugin.getListConfig().getListHeader());

            for (Player p : Bukkit.getOnlinePlayers()) {
                onlinePlayers.append(p.getName()).append(plugin.getTranslateColors().translateColors(null, plugin.getListConfig().getListAppend()));
            }

            if (onlinePlayers.length() > 2) {
                onlinePlayers.setLength(onlinePlayers.length() - 2);
            }

            sender.sendMessage(plugin.getTranslateColors().translateColors(null, onlinePlayers.toString()));

            sender.sendMessage(plugin.getTranslateColors().translateColors(null, plugin.getListConfig().getListFooter()));
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        }

        return true;
    }
}

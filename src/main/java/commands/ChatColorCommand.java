package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatColorCommand implements CommandExecutor {

    private final TChat plugin;

    public ChatColorCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            String message = plugin.getTranslateColors().translateColors(null, plugin.getMessagesManager().getNoPlayer());
            String prefix = plugin.getTranslateColors().translateColors(null, plugin.getMessagesManager().getPrefix());
            sender.sendMessage(prefix + message);
            return true;
        }

        if (!sender.hasPermission("tchat.chatcolor.menu")) {
            String message = plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getNoPermission());
            String prefix = plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getPrefix());
            sender.sendMessage(prefix + message);
            return true;
        }

        plugin.getChatColorInventoryManager().openInventory(player, plugin.getTranslateColors().translateColors(player, plugin.getChatColorManager().getTitle()));

        return true;
    }
}

package commands;

import minealex.tchat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    private final TChat plugin;

    public Commands(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (sender.hasPermission("tchat.admin") || sender.isOp()) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.getConfigManager().reloadConfig();
                    plugin.getMessagesManager().reloadConfig();
                    String message = plugin.getMessagesManager().getReloadMessage();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                } else if (args[0].equalsIgnoreCase("version")) {
                    String version = plugin.getDescription().getVersion();
                    String message = plugin.getMessagesManager().getVersionMessage().replace("%version%", version);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                } else {
                    String message = plugin.getMessagesManager().getUnknownMessage();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            } else {
                // Mensaje de ayuda
                String helpMessage = "Usage: /chat <reload|version>";
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
            }
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
       return true;
    }
}

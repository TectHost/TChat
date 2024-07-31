package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PluginCommand implements CommandExecutor {
    private final TChat plugin;

    public PluginCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (commandSender.hasPermission("tchat.admin.plugin") || commandSender.hasPermission("tchat.admin")) {
            if (strings.length == 0) {
                String message = plugin.getMessagesManager().getPluginUsage();
                commandSender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return false;
            }

            String pluginName = strings[0];
            Plugin pluginN = Bukkit.getPluginManager().getPlugin(pluginName);

            if (pluginN == null) {
                String message = plugin.getMessagesManager().getPluginNotFound();
                commandSender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return false;
            }

            String name = pluginN.getDescription().getName();
            String version = pluginN.getDescription().getVersion();
            String author = String.join(", ", pluginN.getDescription().getAuthors());

            for (String message : plugin.getMessagesManager().getPluginMessage()) {
                message = message.replace("%plugin%", name);
                message = message.replace("%version%", version);
                message = message.replace("%author%", author);
                commandSender.sendMessage(plugin.getTranslateColors().translateColors(null, message));
            }
        }
        return true;
    }
}

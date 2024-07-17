package commands;

import minealex.tchat.TChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import utils.TranslateHexColorCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Commands implements CommandExecutor, TabCompleter {
    private final TChat plugin;

    public Commands(TChat plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("tchat")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("tchat")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (sender.hasPermission("tchat.admin")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.getConfigManager().reloadConfig();
                    plugin.getMessagesManager().reloadConfig();
                    plugin.getGroupManager().reloadGroups();
                    plugin.getBannedWordsManager().reloadBannedWords();
                    plugin.getBannedCommandsManager().reloadConfig();
                    plugin.getReplacerManager().reloadConfig();
                    String message = plugin.getMessagesManager().getReloadMessage();
                    String prefix = plugin.getMessagesManager().getPrefix();
                    sender.sendMessage(translateColors(prefix) + ChatColor.translateAlternateColorCodes('&', message));
                } else if (args[0].equalsIgnoreCase("version")) {
                    String version = plugin.getDescription().getVersion();
                    String message = plugin.getMessagesManager().getVersionMessage().replace("%version%", version);
                    String prefix = plugin.getMessagesManager().getPrefix();
                    sender.sendMessage(translateColors(prefix) + ChatColor.translateAlternateColorCodes('&', message));
                } else {
                    String message = plugin.getMessagesManager().getUnknownMessage();
                    String prefix = plugin.getMessagesManager().getPrefix();
                    sender.sendMessage(translateColors(prefix) + ChatColor.translateAlternateColorCodes('&', message));
                }
            } else {
                // Mensaje de ayuda
                String helpMessage = "Usage: /tchat <reload|version>";
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', helpMessage));
            }
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            String prefix = plugin.getMessagesManager().getPrefix();
            sender.sendMessage(translateColors(prefix) + ChatColor.translateAlternateColorCodes('&', message));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
            completions.add("version");
        }

        return completions;
    }

    private String translateColors(String message) {
        message = TranslateHexColorCodes.translateHexColorCodes("&#", "", message);
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }
}

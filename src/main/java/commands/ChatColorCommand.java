package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ChatColorCommand implements CommandExecutor {

    private final TChat plugin;
    private final Map<String, String> colorCodeToNameMap;

    public ChatColorCommand(TChat plugin) {
        this.plugin = plugin;
        this.colorCodeToNameMap = new HashMap<>();
        initializeColorMappings();
    }

    private void initializeColorMappings() {
        colorCodeToNameMap.put("&0", "black");
        colorCodeToNameMap.put("&1", "dark_blue");
        colorCodeToNameMap.put("&2", "dark_green");
        colorCodeToNameMap.put("&3", "dark_aqua");
        colorCodeToNameMap.put("&4", "dark_red");
        colorCodeToNameMap.put("&5", "dark_purple");
        colorCodeToNameMap.put("&6", "gold");
        colorCodeToNameMap.put("&7", "gray");
        colorCodeToNameMap.put("&8", "dark_gray");
        colorCodeToNameMap.put("&9", "blue");
        colorCodeToNameMap.put("&a", "green");
        colorCodeToNameMap.put("&b", "aqua");
        colorCodeToNameMap.put("&c", "red");
        colorCodeToNameMap.put("&d", "light_purple");
        colorCodeToNameMap.put("&e", "yellow");
        colorCodeToNameMap.put("&f", "white");

        colorCodeToNameMap.put("&k", "magic");
        colorCodeToNameMap.put("&l", "bold");
        colorCodeToNameMap.put("&m", "strikethrough");
        colorCodeToNameMap.put("&n", "underline");
        colorCodeToNameMap.put("&o", "italic");
        colorCodeToNameMap.put("&r", "reset");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            String message = plugin.getTranslateColors().translateColors(null, plugin.getMessagesManager().getNoPlayer());
            String prefix = plugin.getTranslateColors().translateColors(null, plugin.getMessagesManager().getPrefix());
            sender.sendMessage(prefix + message);
            return true;
        }

        String prefix = plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getPrefix());

        if (args.length == 0) {
            if (player.hasPermission("tchat.chatcolor.menu")) {
                plugin.getChatColorInventoryManager().openInventory(player, plugin.getTranslateColors().translateColors(player, plugin.getChatColorManager().getTitle()));
            } else {
                String message = plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getNoPermission());
                player.sendMessage(prefix + message);
            }
        } else {
            for (String arg : args) {
                if (arg.matches("&[0-9a-f]")) {
                    String colorPermission = "tchat.chatcolor.command." + arg;
                    if (player.hasPermission(colorPermission)) {
                        plugin.getSaveManager().setChatColor(player.getUniqueId(), arg);
                        String format = plugin.getSaveManager().getFormat(player.getUniqueId());
                        String message = plugin.getMessagesManager().getColorSelectedMessage();
                        message = message.replace("%id%", arg);
                        message = message.replace("%format%", format);
                        message = message.replace("%color%", colorCodeToNameMap.getOrDefault(arg, "unknown"));
                        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    } else {
                        String message = plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getNoPermission());
                        player.sendMessage(prefix + message);
                    }
                } else if (arg.matches("&[k-o&r]")) {
                    String formatPermission = "tchat.chatcolor.command." + arg;
                    if (player.hasPermission(formatPermission)) {
                        plugin.getSaveManager().setFormat(player.getUniqueId(), arg);
                        String color = plugin.getSaveManager().getChatColor(player.getUniqueId());
                        String message = plugin.getMessagesManager().getFormatSelectedMessage();
                        message = message.replace("%id%", arg);
                        message = message.replace("%color%", color);
                        message = message.replace("%format%", colorCodeToNameMap.getOrDefault(arg, "unknown"));
                        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    } else {
                        String message = plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getNoPermission());
                        player.sendMessage(prefix + message);
                    }
                } else if (arg.startsWith("&#")) {
                    String colorPermission = "tchat.chatcolor.command.hex";
                    if (player.hasPermission(colorPermission)) {
                        plugin.getSaveManager().setChatColor(player.getUniqueId(), arg);
                        String format = plugin.getSaveManager().getFormat(player.getUniqueId());
                        String message = plugin.getMessagesManager().getColorSelectedMessage();
                        message = message.replace("%id%", arg);
                        message = message.replace("%format%", format);
                        message = message.replace("%color%", "hex");
                        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    } else {
                        String message = plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getNoPermission());
                        player.sendMessage(prefix + message);
                    }
                } else {
                    String message = plugin.getMessagesManager().getInvalidColor();
                    message = message.replace("%color%", arg);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
            }
        }

        return true;
    }
}

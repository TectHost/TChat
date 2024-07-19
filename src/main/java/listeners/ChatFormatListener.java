package listeners;

import config.ConfigManager;
import config.GroupManager;
import me.clip.placeholderapi.PlaceholderAPI;
import minealex.tchat.TChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import utils.TranslateHexColorCodes;

import java.util.List;

public class ChatFormatListener implements Listener {

    private final ConfigManager configManager;
    private final GroupManager groupManager;
    private final TChat plugin;

    public ChatFormatListener(TChat plugin, ConfigManager configManager, GroupManager groupManager, TranslateHexColorCodes translateHexColorCodes) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.groupManager = groupManager;
    }

    @EventHandler
    public void playerFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String format;

        if (configManager.getFormatGroup()) {
            String groupName = groupManager.getGroup(player);
            format = groupManager.getGroupFormat(groupName);
            if (format.isEmpty()) {
                format = "<" + player.getName() + "> " + message;
                String errorMessage = plugin.getMessagesManager().getNoFormatGroup();
                String prefix = plugin.getMessagesManager().getPrefix();
                Bukkit.getConsoleSender().sendMessage(plugin.getTranslateColors().translateColors(player, prefix) + org.bukkit.ChatColor.translateAlternateColorCodes('&', errorMessage).replace("%group%", groupName));
            }
        } else {
            format = configManager.getFormat();
        }

        format = format.replace("%player%", player.getName())
                .replace("%message%", "%msg%");

        format = PlaceholderAPI.setPlaceholders(player, format);

        format = TranslateHexColorCodes.translateHexColorCodes("&#", "", format);

        format = ChatColor.translateAlternateColorCodes('&', format);


        String[] parts = format.split("%msg%", 2);

        TextComponent mainComponent = new TextComponent(TextComponent.fromLegacyText(parts[0]));

        String groupName = groupManager.getGroup(player);
        GroupManager.HoverClickAction playerHoverClick = groupManager.getPlayerHoverClickAction(groupName);
        if (playerHoverClick.isEnabled()) {
            mainComponent.setHoverEvent(createHoverEvent(player, playerHoverClick.getHoverText()));
            if (playerHoverClick.isClickEnabled()) {
                applyClickAction(mainComponent, playerHoverClick.getClickAction(), player.getName());
            }
        }

        String chatColor = plugin.getSaveManager().getChatColor(player.getUniqueId());
        String playerFormat = plugin.getSaveManager().getFormat(player.getUniqueId());

        message = chatColor + playerFormat + message;
        message = plugin.getTranslateColors().translateColors(player, message);

        TextComponent messageComponent = new TextComponent(TextComponent.fromLegacyText(message));

        GroupManager.HoverClickAction messageHoverClick = groupManager.getMessageHoverClickAction(groupName);
        if (messageHoverClick.isEnabled()) {
            messageComponent.setHoverEvent(createHoverEvent(player, messageHoverClick.getHoverText()));
            if (messageHoverClick.isClickEnabled()) {
                applyClickAction(messageComponent, messageHoverClick.getClickAction(), player.getName());
            }
        }

        mainComponent.addExtra(messageComponent);
        if (parts.length > 1) {
            mainComponent.addExtra(new TextComponent(TextComponent.fromLegacyText(parts[1])));
        }

        event.setCancelled(true);
        for (Player p : event.getRecipients()) {
            p.spigot().sendMessage(mainComponent);
        }

        if (configManager.getRegisterMessagesOnConsole()) {
            String consoleMessage = mainComponent.toLegacyText();
            consoleMessage = plugin.getTranslateColors().translateColors(player, consoleMessage);
            Bukkit.getConsoleSender().sendMessage(consoleMessage);
        }
    }

    private String chatColor(Player player, String message) {
        if (player.hasPermission("tchat.color.all") || player.hasPermission("tchat.admin")) {
            return ChatColor.translateAlternateColorCodes('&', message);
        } else {
            StringBuilder coloredMessage = new StringBuilder();
            char[] chars = message.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '&' && i + 1 < chars.length) {
                    char colorCode = chars[i + 1];
                    if (isColorCodeAllowed(player, colorCode)) {
                        coloredMessage.append('&').append(colorCode);
                        i++;
                    } else {
                        i++;
                    }
                } else {
                    coloredMessage.append(chars[i]);
                }
            }
            return ChatColor.translateAlternateColorCodes('&', coloredMessage.toString());
        }
    }

    private boolean isColorCodeAllowed(Player player, char colorCode) {
        return switch (colorCode) {
            case '0' -> player.hasPermission("tchat.color.black");
            case '1' -> player.hasPermission("tchat.color.dark_blue");
            case '2' -> player.hasPermission("tchat.color.dark_green");
            case '3' -> player.hasPermission("tchat.color.dark_aqua");
            case '4' -> player.hasPermission("tchat.color.dark_red");
            case '5' -> player.hasPermission("tchat.color.dark_purple");
            case '6' -> player.hasPermission("tchat.color.gold");
            case '7' -> player.hasPermission("tchat.color.gray");
            case '8' -> player.hasPermission("tchat.color.dark_gray");
            case '9' -> player.hasPermission("tchat.color.blue");
            case 'a' -> player.hasPermission("tchat.color.green");
            case 'b' -> player.hasPermission("tchat.color.aqua");
            case 'c' -> player.hasPermission("tchat.color.red");
            case 'd' -> player.hasPermission("tchat.color.light_purple");
            case 'e' -> player.hasPermission("tchat.color.yellow");
            case 'f' -> player.hasPermission("tchat.color.white");
            case 'l' -> player.hasPermission("tchat.color.bold");
            case 'm' -> player.hasPermission("tchat.color.strikethrough");
            case 'n' -> player.hasPermission("tchat.color.underline");
            case 'o' -> player.hasPermission("tchat.color.italic");
            default -> false;
        };
    }

    private HoverEvent createHoverEvent(Player player, List<String> hoverText) {
        TextComponent hoverComponent = new TextComponent("");
        boolean first = true;

        for (String line : hoverText) {
            if (!first) {
                hoverComponent.addExtra("\n");
            } else {
                first = false;
            }
            String translatedLine = plugin.getTranslateColors().translateColors(player, line);
            hoverComponent.addExtra(new TextComponent(TextComponent.fromLegacyText(translatedLine)));
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverComponent).create());
    }

    private void applyClickAction(TextComponent component, String clickAction, String playerName) {
        String replacedAction = clickAction.replace("%player%", playerName);

        if (replacedAction.startsWith("[EXECUTE]")) {
            String command = replacedAction.substring("[EXECUTE] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        } else if (replacedAction.startsWith("[OPEN]")) {
            String url = replacedAction.substring("[OPEN] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        } else if (replacedAction.startsWith("[SUGGEST]")) {
            String command = replacedAction.substring("[SUGGEST] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        }
    }
}
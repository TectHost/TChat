package listeners;

import config.ConfigManager;
import config.GroupManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import utils.TranslateHexColorCodes;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class ChatFormatListener implements Listener {

    private final ConfigManager configManager;
    private final GroupManager groupManager;
    private final TranslateHexColorCodes translateHexColorCodes;

    public ChatFormatListener(ConfigManager configManager, GroupManager groupManager, TranslateHexColorCodes translateHexColorCodes) {
        this.configManager = configManager;
        this.groupManager = groupManager;
        this.translateHexColorCodes = translateHexColorCodes;
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
                System.out.println("Error in groups.yml - No format found for group: " + groupName);
            }
        } else {
            format = configManager.getFormat();
        }

        format = format.replace("%player%", player.getName())
                .replace("%message%", "%msg%");

        format = PlaceholderAPI.setPlaceholders(player, format);

        format = translateHexColorCodes.translateHexColorCodes("&#", "", format);

        format = ChatColor.translateAlternateColorCodes('&', format);

        String[] parts = format.split("%msg%", 2);

        TextComponent mainComponent = new TextComponent(TextComponent.fromLegacyText(parts[0]));

        String groupName = groupManager.getGroup(player);
        GroupManager.HoverClickAction playerHoverClick = groupManager.getPlayerHoverClickAction(groupName);
        if (playerHoverClick.isEnabled()) {
            mainComponent.setHoverEvent(createHoverEvent(playerHoverClick.getHoverText()));
            if (playerHoverClick.isClickEnabled()) {
                applyClickAction(mainComponent, playerHoverClick.getClickAction(), player.getName());
            }
        }

        TextComponent messageComponent = new TextComponent(message);

        GroupManager.HoverClickAction messageHoverClick = groupManager.getMessageHoverClickAction(groupName);
        if (messageHoverClick.isEnabled()) {
            messageComponent.setHoverEvent(createHoverEvent(messageHoverClick.getHoverText()));
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
            String consoleMessage = mainComponent.toPlainText();
            Bukkit.getServer().getLogger().info(consoleMessage);
        }
    }

    private HoverEvent createHoverEvent(List<String> hoverText) {
        TextComponent hoverComponent = new TextComponent("");
        boolean first = true;

        for (String line : hoverText) {
            if (!first) {
                hoverComponent.addExtra("\n");
            } else {
                first = false;
            }
            String translatedLine = translateColors(line);
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

    private String translateColors(String text) {
        text = translateHexColorCodes.translateHexColorCodes("&#", "", text);
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }
}

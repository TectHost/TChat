package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Pattern;

public class AntiUnicodeListener implements Listener {
    private final TChat plugin;
    private final Pattern allowedPattern;

    public AntiUnicodeListener(TChat plugin) {
        this.plugin = plugin;
        String regex = plugin.getConfigManager().getUnicodeMatch();
        this.allowedPattern = (regex != null && !regex.isEmpty()) ? Pattern.compile(regex) : Pattern.compile(".*");
    }

    @EventHandler
    public void checkUnicode(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (plugin.getConfigManager().isUnicodeEnabled() && !player.hasPermission("tchat.bypass.unicode") && !player.hasPermission("tchat.admin")) {
            if (event.isCancelled()) { return; }

            if (containsInvalidCharacters(message)) {
                event.setCancelled(true);
                player.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getPrefix() + plugin.getMessagesManager().getAntiUnicode()));
            }
        }
    }

    private boolean containsInvalidCharacters(String message) {
        if (!plugin.getConfigManager().isUnicodeBlockAll()) {
            return !allowedPattern.matcher(message).matches();
        } else {
            for (char c : message.toCharArray()) {
                if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                    return true;
                }
            }
        }
        return false;
    }
}

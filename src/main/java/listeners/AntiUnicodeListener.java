package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class AntiUnicodeListener implements Listener {
    private final TChat plugin;
    private final Pattern allowedPattern;

    public AntiUnicodeListener(@NotNull TChat plugin) {
        this.plugin = plugin;
        String regex = plugin.getConfigManager().getUnicodeMatch();
        this.allowedPattern = (regex != null && !regex.isEmpty()) ? Pattern.compile(regex) : Pattern.compile(".*");
    }

    @EventHandler
    public void checkUnicode(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (plugin.getConfigManager().isUnicodeEnabled() && !player.hasPermission("tchat.bypass.unicode") && !player.hasPermission("tchat.admin")) {
            if (event.isCancelled()) { return; }

            if (containsInvalidCharacters(message)) {
                if (plugin.getConfigManager().getUnicodeMode() == 1) {
                    event.setCancelled(true);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, plugin.getMessagesManager().getPrefix() + plugin.getMessagesManager().getAntiUnicode()));
                } else {
                    String censoredMessage = censorUnicode(message);
                    event.setMessage(censoredMessage);
                }
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

    private @NotNull String censorUnicode(@NotNull String message) {
        StringBuilder censored = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN) {
                censored.append(plugin.getConfigManager().getUnicodeCensor());
            } else {
                censored.append(c);
            }
        }
        return censored.toString();
    }
}

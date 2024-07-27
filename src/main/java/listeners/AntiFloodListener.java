package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class AntiFloodListener implements Listener {

    private final TChat plugin;

    public AntiFloodListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void checkFlood(AsyncPlayerChatEvent event, String message, Player player) {
        if (event.isCancelled()) { return; }

        if (!player.hasPermission("tchat.bypass.antiflood") && !player.hasPermission("tchat.admin")) {
            if (plugin.getConfigManager().isFloodRepeatEnabled() && containsFlood(message) || plugin.getConfigManager().isFloodPercentEnabled() && containsHighPercentageOfSameChar(message)) {
                String message1 = plugin.getMessagesManager().getAntiFlood();
                String prefix = plugin.getMessagesManager().getPrefix();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message1));
                event.setCancelled(true);
            }
        }
    }

    private boolean containsFlood(String message) {
        char[] chars = message.toCharArray();
        int count = 1;

        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == chars[i - 1]) {
                count++;
                if (count >= plugin.getConfigManager().getCharactersFlood()) {
                    return true;
                }
            } else {
                count = 1;
            }
        }
        return false;
    }

    private boolean containsHighPercentageOfSameChar(String message) {
        if (message.isEmpty()) return false;

        Map<Character, Integer> charCount = new HashMap<>();
        int totalChars = 0;

        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                charCount.put(c, charCount.getOrDefault(c, 0) + 1);
                totalChars++;
            }
        }

        if (totalChars == 0) return false;

        for (int count : charCount.values()) {
            double percentage = (double) count / totalChars;
            if (percentage >= plugin.getConfigManager().getPercentageFlood()) {
                return true;
            }
        }

        return false;
    }
}

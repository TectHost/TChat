package listeners;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GrammarListener {
    private final TChat plugin;

    public GrammarListener(TChat plugin) {
        this.plugin = plugin;
    }

    public void checkGrammar(AsyncPlayerChatEvent event, String message) {
        if (plugin.getConfigManager().isGrammarCapEnabled()) {
            message = checkCap(message);
        }

        if (plugin.getConfigManager().isGrammarDotEnabled()) {
            message = checkDot(message);
        }

        event.setMessage(message);
    }

    public String checkCap(String message) {
        if (message.length() > plugin.getConfigManager().getGrammarMinCharactersCap()) {
            return message.substring(0, 1).toUpperCase() + message.substring(plugin.getConfigManager().getGrammarCapLetters());
        }
        return message;
    }

    public String checkDot(String message) {
        String dot = plugin.getConfigManager().getGrammarDotCharacter();
        if (message.length() > plugin.getConfigManager().getGrammarMinCharactersDot()) {
            return message + dot;
        }
        return message;
    }
}
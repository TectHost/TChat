package listeners;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class GrammarListener {
    private final TChat plugin;

    public GrammarListener(TChat plugin) {
        this.plugin = plugin;
    }

    public void checkGrammar(@NotNull AsyncPlayerChatEvent event, Player player, String message) {
        if (event.isCancelled()) { return; }

        if (plugin.getGrammarManager().isGrammarCapEnabled()) {
            if (!player.hasPermission("tchat.bypass.grammar.cap") && !player.hasPermission("tchat.admin")) {
                message = checkCap(message);
            }
        }

        if (plugin.getGrammarManager().isGrammarDotEnabled()) {
            if (!player.hasPermission("tchat.bypass.grammar.finaldot") && !player.hasPermission("tchat.admin")) {
                message = checkDot(message);
            }
        }

        event.setMessage(message);

        if (plugin.getGrammarManager().isRepeatMessagesEnabled()) {
            if (!player.hasPermission(plugin.getGrammarManager().getBypassRepeatMessages()) && !player.hasPermission("tchat.admin")) {
                plugin.getRepeatMessagesListener().checkRepeatMessages(event, player, message);
            }
        }
    }

    public String checkCap(@NotNull String message) {
        if (message.length() > plugin.getGrammarManager().getGrammarMinCharactersCap()) {
            return message.substring(0, 1).toUpperCase() + message.substring(plugin.getGrammarManager().getGrammarCapLetters());
        }
        return message;
    }

    public String checkDot(@NotNull String message) {
        String dot = plugin.getGrammarManager().getGrammarDotCharacter();
        if (message.length() > plugin.getGrammarManager().getGrammarMinCharactersDot() && !message.endsWith(dot)) {
            return message + dot;
        }
        return message;
    }
}
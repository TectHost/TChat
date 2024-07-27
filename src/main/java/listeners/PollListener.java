package listeners;

import minealex.tchat.TChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import utils.Poll;

import java.util.Arrays;

public class PollListener implements Listener {
    private final TChat plugin;

    public PollListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Poll currentPoll = Poll.getCurrentPoll();
        if (currentPoll != null && !currentPoll.hasEnded()) {
            String message1 = plugin.getMessagesManager().getPollMessage();
            message1 = message1.replace("%poll%", currentPoll.getTitle());
            event.getPlayer().sendMessage(plugin.getTranslateColors().translateColors(null, message1));
            String message2 = plugin.getMessagesManager().getPollOptionsMessage();
            message2 = message2.replace("%options%", Arrays.toString(currentPoll.getOptions()));
            event.getPlayer().sendMessage(plugin.getTranslateColors().translateColors(null, message2));
        }
    }
}
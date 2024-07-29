package listeners;

import minealex.tchat.TChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeftListener implements Listener {

    private final TChat plugin;

    public PlayerLeftListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent event) {
        String username = event.getPlayer().getName();

        plugin.getDiscordHook().sendLeftMessage(username);
    }
}

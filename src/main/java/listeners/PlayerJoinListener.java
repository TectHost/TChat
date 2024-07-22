package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.Set;

public class PlayerJoinListener implements Listener {

    private final TChat plugin;
    private final Set<Player> unverifiedPlayers = new HashSet<>();

    public PlayerJoinListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!unverifiedPlayers.contains(player) && plugin.getConfigManager().isAntibotEnabled() && !player.hasPermission(plugin.getConfigManager().getAntibotBypass()) && !player.hasPermission("tchat.admin")) {
            unverifiedPlayers.add(player);
            if (plugin.getConfigManager().isAntibotJoin()) {
                String prefix = plugin.getMessagesManager().getPrefix();
                String message = plugin.getMessagesManager().getAntibotJoin();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            }
        }
    }

    public boolean isUnverified(Player player) {
        return unverifiedPlayers.contains(player);
    }

    public void removeUnverifiedPlayer(Player player) {
        unverifiedPlayers.remove(player);
    }
}

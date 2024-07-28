package listeners;

import minealex.tchat.TChat;
import config.WorldsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class ChatEnabledListener implements Listener {

    private final TChat plugin;
    private final WorldsManager worldsManager;

    public ChatEnabledListener(TChat plugin) {
        this.plugin = plugin;
        this.worldsManager = plugin.getWorldsManager();
    }

    @EventHandler
    public void checkChatEnabled(AsyncPlayerChatEvent event, Player player) {
        if (event.isCancelled()) { return; }

        String worldName = player.getWorld().getName();

        if (worldsManager.getWorldsConfig().containsKey(worldName)) {
            boolean chatEnabled = worldsManager.getWorldsConfig().get(worldName);
            if (!chatEnabled) {
                event.setCancelled(true);
                String prefix = plugin.getMessagesManager().getPrefix();
                String message = plugin.getMessagesManager().getChatDisabledWorld();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            }
        }
    }
}
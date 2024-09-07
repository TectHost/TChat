package listeners;

import minealex.tchat.TChat;
import config.WorldsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class ChatEnabledListener implements Listener {

    private final TChat plugin;
    private final WorldsManager worldsManager;

    public ChatEnabledListener(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.worldsManager = plugin.getWorldsManager();
    }

    @EventHandler
    public void checkChatEnabled(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) { return; }

        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();

        WorldsManager.WorldConfigData configData = worldsManager.getWorldsConfig().get(worldName);

        if (configData != null) {
            boolean chatEnabled = configData.chatEnabled();
            if (!chatEnabled && !player.hasPermission("tchat.admin") && !player.hasPermission("tchat.bypass.chat-enabled")) {
                event.setCancelled(true);
                String prefix = plugin.getMessagesManager().getPrefix();
                String message = plugin.getMessagesManager().getChatDisabledWorld();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            }
        }
    }
}

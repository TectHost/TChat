package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AntiBotListener implements Listener {

    private final TChat plugin;

    public AntiBotListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event, Player player) {
        event.setCancelled(true);
        if (plugin.getConfigManager().isAntibotChat()) {
            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getAntibotChat();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }
    }

    @EventHandler
    public void playerCommand(PlayerCommandPreprocessEvent event, Player player) {
        event.setCancelled(true);
        if (plugin.getConfigManager().isAntibotCommand()) {
            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getAntibotCommand();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }
    }
}

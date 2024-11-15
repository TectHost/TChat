package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AntiBotListener implements Listener {

    private final TChat plugin;

    public AntiBotListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event, Player player) {
        if (plugin.getAntiBotConfigManager().isAntibotChat()) {
            event.setCancelled(true);
            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getAntibotChat();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }
    }

    @EventHandler
    public void playerCommand(@NotNull PlayerCommandPreprocessEvent event) {
        if (plugin.getAntiBotConfigManager().isAntibotCommand()) {
            Player player = event.getPlayer();

            String command = event.getMessage().split(" ")[0].toLowerCase();
            List<String> whitelist = plugin.getAntiBotConfigManager().getWhitelistCommandsAntiBot();
            if (whitelist.contains(command)) { return; }

            event.setCancelled(true);

            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getAntibotCommand();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }
    }
}

package utils;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class CheckPlayerMuted {

    private final TChat plugin;

    public CheckPlayerMuted(TChat plugin) {
        this.plugin = plugin;
    }

    public void checkMuted(@NotNull AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if ((!p.hasPermission("tchat.admin") && !p.hasPermission("tchat.bypass.mute")) && plugin.getSaveManager().isPlayerMuted(p.getUniqueId())) {
            String prefix = plugin.getMessagesManager().getPrefix();
            String m = plugin.getMessagesManager().getMuted();
            p.sendMessage(plugin.getTranslateColors().translateColors(p, prefix + m));
            e.setCancelled(true);
        }
    }
}

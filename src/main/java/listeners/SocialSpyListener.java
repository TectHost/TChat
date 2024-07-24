package listeners;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SocialSpyListener implements Listener {

    private final TChat plugin;

    public SocialSpyListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void spy(PlayerCommandPreprocessEvent event, Player sender, String command) {
        for (Player admin : Bukkit.getServer().getOnlinePlayers()) {
            if (admin.hasPermission("tchat.admin") || admin.hasPermission("tchat.social-spy")) {
                String adminMessage = plugin.getConfigManager().getSpyFormat()
                        .replace("%player%", sender.getName())
                        .replace("%command%", command);
                admin.sendMessage(plugin.getTranslateColors().translateColors(admin, adminMessage));
            }
        }
    }
}

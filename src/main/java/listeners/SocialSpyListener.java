package listeners;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class SocialSpyListener implements Listener {

    private final TChat plugin;

    public SocialSpyListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void spy(PlayerCommandPreprocessEvent event, Player sender, String command) {
        int mode = plugin.getConfigManager().getSocialSpyMode();

        for (Player admin : Bukkit.getServer().getOnlinePlayers()) {
            if (admin.hasPermission("tchat.admin") || admin.hasPermission("tchat.social-spy")) {
                String adminMessage = plugin.getConfigManager().getSpyFormat()
                        .replace("%player%", sender.getName())
                        .replace("%command%", command);

                if (mode == 1) {
                    admin.sendMessage(plugin.getTranslateColors().translateColors(admin, adminMessage));
                } else if (mode == 2) {
                    List<String> commands = plugin.getConfigManager().getSocialSpyCommands();
                    for (String allowedCommand : commands) {
                        if (command.toLowerCase().startsWith(allowedCommand.toLowerCase())) {
                            admin.sendMessage(plugin.getTranslateColors().translateColors(admin, adminMessage));
                            break;
                        }
                    }
                } else if (mode == 3) {
                    List<String> commands = plugin.getConfigManager().getSocialSpyCommands();
                    boolean isAllowed = false;
                    for (String allowedCommand : commands) {
                        if (command.toLowerCase().startsWith(allowedCommand.toLowerCase())) {
                            isAllowed = true;
                            break;
                        }
                    }
                    if (!isAllowed) {
                        admin.sendMessage(plugin.getTranslateColors().translateColors(admin, adminMessage));
                    }
                }
            }
        }
    }
}

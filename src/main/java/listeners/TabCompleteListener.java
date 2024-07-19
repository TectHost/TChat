package listeners;

import config.BannedCommandsManager;
import minealex.tchat.TChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.List;

public class TabCompleteListener implements Listener {

    private final BannedCommandsManager bannedCommandsManager;
    private final List<String> remove;

    public TabCompleteListener(TChat plugin) {
        this.bannedCommandsManager = plugin.getBannedCommandsManager();
        this.remove = bannedCommandsManager.getNoTabCompleteCommands();
    }

    @EventHandler
    void onPlayerCommandSend(PlayerCommandSendEvent event) {
        if (!event.getPlayer().hasPermission("tchat.bypass.command_blocker.tab") && !event.getPlayer().hasPermission("tchat.admin")) {
            if (bannedCommandsManager.getBlockAllCommands()) {
                event.getCommands().removeIf(command -> command.contains(":"));
            }

            event.getCommands().removeAll(this.remove);
        }
    }
}

package listeners;

import minealex.tchat.TChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RepeatCommandsListener implements Listener {

    private final TChat plugin;
    private final Map<Player, String> lastCommandMap = new HashMap<>();

    public RepeatCommandsListener(TChat plugin) {this.plugin = plugin;}

    @EventHandler
    public void checkRepeatCommand(@NotNull PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) { return; }

        Player player = event.getPlayer();
        String command = event.getMessage();

        if (lastCommandMap.containsKey(player) && lastCommandMap.get(player).equals(command)) {
            event.setCancelled(true);
            String p = plugin.getMessagesManager().getPrefix();
            String m = plugin.getMessagesManager().getRepeatCommands();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            return;
        }

        lastCommandMap.put(player, command);
    }
}

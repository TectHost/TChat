package listeners;

import minealex.tchat.TChat;
import config.ChatBotManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ChatBotListener implements Listener {

    private final ChatBotManager chatBotManager;
    private final TChat plugin;

    public ChatBotListener(TChat plugin) {
        this.plugin = plugin;
        this.chatBotManager = plugin.getChatBotManager();
    }

    @EventHandler
    public void chatBot(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (chatBotManager.isEnabled()) {
            List<String> responses = chatBotManager.getMessages(message);

            if (!responses.isEmpty()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (String response : responses) {
                            player.sendMessage(response);
                        }
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }
}

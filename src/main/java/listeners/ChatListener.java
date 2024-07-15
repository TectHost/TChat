package listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final ChatFormatListener chatFormatListener;

    public ChatListener(ChatFormatListener chatFormatListener) {
        this.chatFormatListener = chatFormatListener;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        chatFormatListener.playerFormat(event);
    }
}

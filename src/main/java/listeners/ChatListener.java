package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import blocked.BannedCommands;

public class ChatListener implements Listener {
    private final TChat plugin;
    private final ChatFormatListener chatFormatListener;
    private final BannedCommands bannedCommands;

    public ChatListener(TChat plugin, ChatFormatListener chatFormatListener) {
        this.plugin = plugin;
        this.chatFormatListener = chatFormatListener;
        this.bannedCommands = new BannedCommands(plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        plugin.getChatBotListener().chatBot(event);
        plugin.getBannedWords().playerBannedWords(event);
        plugin.getAntiAdvertising().checkAdvertising(event);
        plugin.getCapListener().playerAntiCap(event);

        if (!event.isCancelled()) {
            if (plugin.getReplacerManager().getReplacerEnabled()) {
                message = plugin.getReplacerManager().replaceWords(message, event.getPlayer());
                event.setMessage(message);
            }

            if (plugin.getConfigManager().isGrammarEnabled()) {
                plugin.getGrammarListener().checkGrammar(event, player, message);
                message = event.getMessage();
                event.setMessage(message);
            }

            chatFormatListener.playerFormat(event);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        bannedCommands.onPlayerCommandPreprocess(event);
    }
}

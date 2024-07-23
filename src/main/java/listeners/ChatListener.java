package listeners;

import commands.MuteChatCommand;
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

    public ChatListener(TChat plugin, ChatFormatListener chatFormatListener) {
        this.plugin = plugin;
        this.chatFormatListener = chatFormatListener;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        chatMuted(event, player);
        plugin.getChatCooldownListener().chatCooldown(event, player);
        plugin.getAntiUnicodeListener().checkUnicode(event, player, message);
        plugin.getChatBotListener().chatBot(event);
        plugin.getBannedWords().playerBannedWords(event);
        plugin.getAntiAdvertising().checkAdvertising(event);
        plugin.getCapListener().playerAntiCap(event);
        replacer(event, message);
        grammar(event, player, message);
        antiBot(event, player, null);
        plugin.getAntiFloodListener().checkFlood(event, message);

        chatFormatListener.playerFormat(event);
        plugin.getChatGamesSender().checkPlayerResponse(player, message);
        logs(player, message, 1);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        logs(player, command, 0);
        plugin.getChatCooldownListener().commandCooldown(event, player);
        antiBot(null, player, event);
        new BannedCommands(plugin).onPlayerCommandPreprocess(event);
    }

    // Other ------------------------------------------------------------------------------

    public void logs(Player player, String message, int action) {
        if (plugin.getConfigManager().isLogsCommandEnabled() && action == 0) {
            plugin.getLogsManager().logCommand(player.getName(), message);
        } else if (plugin.getConfigManager().isLogsChatEnabled()) {
            plugin.getLogsManager().logChatMessage(player.getName(), message);
        }
    }

    public void antiBot(AsyncPlayerChatEvent chatEvent, Player player, PlayerCommandPreprocessEvent commandEvent) {
        if (chatEvent != null) {
            if (chatEvent.isCancelled()) { return; }
        } else if (commandEvent != null) {
            if (commandEvent.isCancelled()) { return; }
        }

        if (plugin.getPlayerJoinListener().isUnverified(player) && plugin.getConfigManager().isAntibotEnabled() && !player.hasPermission(plugin.getConfigManager().getAntibotBypass()) && !player.hasPermission("tchat.admin")) {
            if (chatEvent != null) {
                plugin.getAntiBotListener().playerChat(chatEvent, player);
            } else if (commandEvent != null) {
                plugin.getAntiBotListener().playerCommand(commandEvent, player);
            }
        }
    }

    public void replacer(AsyncPlayerChatEvent event, String message) {
        if (plugin.getReplacerManager().getReplacerEnabled()) {
            message = plugin.getReplacerManager().replaceWords(message, event.getPlayer());
            event.setMessage(message);
        }
    }

    public void grammar(AsyncPlayerChatEvent event, Player player, String message) {
        if (plugin.getConfigManager().isGrammarEnabled()) {
            plugin.getGrammarListener().checkGrammar(event, player, message);
            message = event.getMessage();
            event.setMessage(message);
        }
    }

    public void chatMuted(AsyncPlayerChatEvent event, Player player) {
        if (MuteChatCommand.isChatMuted() && !player.hasPermission(plugin.getConfigManager().getBypassMuteChatPermission())) {
            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getChatMuted();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            event.setCancelled(true);
        }
    }
}

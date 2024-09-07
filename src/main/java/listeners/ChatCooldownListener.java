package listeners;

import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ChatCooldownListener implements Listener {

    private final Map<Player, Long> lastChatTimes = new HashMap<>();
    private final Map<Player, Long> lastCommandTimes = new HashMap<>();
    private final TChat plugin;
    private final long chatCooldownTime;
    private final long commandCooldownTime;

    public ChatCooldownListener(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.chatCooldownTime = plugin.getConfigManager().getCooldownChatTime();
        this.commandCooldownTime = plugin.getConfigManager().getCooldownCommandTime();
    }

    @EventHandler
    public void chatCooldown(@NotNull AsyncPlayerChatEvent event, Player player) {
        if (event.isCancelled()) { return; }

        String channelName = plugin.getChannelsManager().getPlayerChannel(player);
        if (channelName != null && plugin.getChannelsConfigManager().getChannel(channelName).isCooldownEnabled()) {return;}

        if (!player.hasPermission("tchat.admin") && !player.hasPermission("tchat.bypass.chatcooldown")) {
            if (plugin.getConfigManager().isCooldownChat()) {
                long currentTime = System.currentTimeMillis();

                if (isCooldownActive(player, lastChatTimes, currentTime, chatCooldownTime)) {
                    long timeRemainingMillis = chatCooldownTime - (currentTime - lastChatTimes.get(player));
                    long timeRemaining = timeRemainingMillis / 1000;
                    String prefix = plugin.getMessagesManager().getPrefix();
                    String message = plugin.getMessagesManager().getCooldownChat();
                    String timeRemainingStr = String.valueOf(timeRemaining);
                    message = message.replace("%cooldown%", timeRemainingStr);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));

                    if (plugin.getConfigManager().isDepurationChatEnabled()) {
                        String message1 = plugin.getMessagesManager().getDepurationChatCooldown();
                        message1 = message1.replace("%player%", player.getName());
                        message1 = message1.replace("%time%", String.valueOf(timeRemaining));
                        plugin.getLogger().warning(message1);
                    }

                    event.setCancelled(true);
                    return;
                }

                lastChatTimes.put(player, currentTime);
            }
        }
    }

    @EventHandler
    public void commandCooldown(@NotNull PlayerCommandPreprocessEvent event, Player player) {
        if (event.isCancelled()) { return; }

        if (!player.hasPermission("tchat.admin") && !player.hasPermission("tchat.bypass.commandcooldown")) {
            if (plugin.getConfigManager().isCooldownCommand()) {
                long currentTime = System.currentTimeMillis();

                if (isCooldownActive(player, lastCommandTimes, currentTime, commandCooldownTime)) {
                    long timeRemainingMillis = commandCooldownTime - (currentTime - lastCommandTimes.get(player));
                    long timeRemaining = timeRemainingMillis / 1000;
                    String prefix = plugin.getMessagesManager().getPrefix();
                    String message = plugin.getMessagesManager().getCooldownCommand();
                    String timeRemainingStr = String.valueOf(timeRemaining);
                    message = message.replace("%cooldown%", timeRemainingStr);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));

                    if (plugin.getConfigManager().isDepurationCommandEnabled()) {
                        String message1 = plugin.getMessagesManager().getDepurationCommandCooldown();
                        message1 = message1.replace("%player%", player.getName());
                        message1 = message1.replace("%time%", String.valueOf(timeRemaining));
                        plugin.getLogger().warning(message1);
                    }

                    event.setCancelled(true);
                    return;
                }

                lastCommandTimes.put(player, currentTime);
            }
        }
    }

    private boolean isCooldownActive(Player player, @NotNull Map<Player, Long> cooldownMap, long currentTime, long cooldownTime) {
        if (cooldownMap.containsKey(player)) {
            long lastTime = cooldownMap.get(player);
            return (currentTime - lastTime) < cooldownTime;
        }
        return false;
    }
}

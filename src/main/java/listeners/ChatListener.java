package listeners;

import commands.MuteChatCommand;
import config.AutoBroadcastManager;
import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import blocked.BannedCommands;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListener implements Listener {
    private final TChat plugin;
    private final ChatFormatListener chatFormatListener;
    private final Map<Player, String> broadcastNames = new HashMap<>();
    private final Map<Player, Boolean> broadcastEnabled = new HashMap<>();
    private final Map<Player, List<String>> broadcastMessages = new HashMap<>();
    private final Map<Player, Boolean> titleEnabled = new HashMap<>();
    private final Map<Player, String> title = new HashMap<>();
    private final Map<Player, String> subtitle = new HashMap<>();
    private final Map<Player, Boolean> soundEnabled = new HashMap<>();
    private final Map<Player, String> sound = new HashMap<>();
    private final Map<Player, Boolean> particlesEnabled = new HashMap<>();
    private final Map<Player, String> particle = new HashMap<>();
    private final Map<Player, Integer> particleCount = new HashMap<>();
    private final Map<Player, Boolean> actionbarEnabled = new HashMap<>();
    private final Map<Player, String> actionbar = new HashMap<>();
    private final Map<Player, String> permissions = new HashMap<>();

    public ChatListener(TChat plugin, ChatFormatListener chatFormatListener) {
        this.plugin = plugin;
        this.chatFormatListener = chatFormatListener;
    }

    @EventHandler
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (plugin.getConfigManager().isMuteEnabled()) { plugin.getCheckPlayerMuted().checkMuted(event); }
        if (plugin.getConfigManager().isWorldsEnabled()) { plugin.getChatEnabledListener().checkChatEnabled(event); }
        if (plugin.getConfigManager().isMuteChatEnabled()) { chatMuted(event); }
        if (plugin.getConfigManager().isCooldownsEnabled()) { plugin.getChatCooldownListener().chatCooldown(event); }
        if (plugin.getConfigManager().isAntiUnicodeEnabled()) { plugin.getAntiUnicodeListener().checkUnicode(event); }
        if (plugin.getConfigManager().isChatBotEnabled()) { plugin.getChatBotListener().chatBot(event); }
        if (plugin.getConfigManager().isAntiAdvertisingEnabled()) { plugin.getAntiAdvertising().checkAdvertising(event); }
        if (plugin.getConfigManager().isAntiCapEnabled()) { plugin.getCapListener().playerAntiCap(event); }
        if (plugin.getConfigManager().isAntiBotEnabled()) { antiBot(event, player, null); }
        if (plugin.getConfigManager().isAntiFloodEnabled()) { plugin.getAntiFloodListener().checkFlood(event); }
        if (plugin.getConfigManager().isBannedWordsEnabled()) { plugin.getBannedWords().playerBannedWords(event); }
        if (plugin.getConfigManager().isReplacerEnabled()) { replacer(event); }
        if (plugin.getConfigManager().isGrammarEnabled()) { grammar(event); }

        chatFormatListener.playerFormat(event);
        if (plugin.getConfigManager().isChatGamesEnabled()) { plugin.getChatGamesSender().checkPlayerResponse(player, message); }
        if (plugin.getConfigManager().isLoggerEnabled()) { if (plugin.getLoggerConfigManager().isLogsChatEnabled()) { plugin.getLogsManager().logChatMessage(player.getName(), message); } }
        if (plugin.getConfigManager().isLevelsEnabled()) { plugin.getLevelListener().addXp(event); }
        if (plugin.getConfigManager().isAutoBroadcastEnabled()) { handleBroadcastAddition(player, message); }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (plugin.getConfigManager().isSocialSpyEnabled() && !player.hasPermission("tchat.admin")) { plugin.getSocialSpyListener().spy(event, player, command); }
        if (plugin.getConfigManager().isLoggerEnabled()) { if (plugin.getLoggerConfigManager().isLogsCommandEnabled()) { plugin.getLogsManager().logCommand(player.getName(), command); } }
        if (plugin.getConfigManager().isAntiAdvertisingEnabled()) { plugin.getAntiAdvertising().checkAdvertisingCommand(event, player, command); }
        if (plugin.getConfigManager().isCooldownsEnabled()) { plugin.getChatCooldownListener().commandCooldown(event); }
        if (plugin.getConfigManager().isRepeatCommandsEnabled() && !player.hasPermission("tchat.admin") && !player.hasPermission("tchat.bypass.repeat-commands")) { plugin.getRepeatCommandsListener().checkRepeatCommand(event); }
        if (plugin.getConfigManager().isAntiBotEnabled()) { antiBot(null, player, event); }
        if (plugin.getConfigManager().isBannedCommandsEnabled()) { new BannedCommands(plugin).onPlayerCommandPreprocess(event); }
    }

    private void handleBroadcastAddition(Player player, String message) {
        if (broadcastNames.containsKey(player)) {
            String prefix = plugin.getMessagesManager().getPrefix();

            if (broadcastEnabled.containsKey(player)) {
                boolean enabled = message.equalsIgnoreCase("yes");
                broadcastEnabled.put(player, enabled);
                broadcastNames.remove(player);
            } else if (broadcastMessages.containsKey(player)) {
                List<String> messages = broadcastMessages.get(player);
                if (message.equalsIgnoreCase("done")) {
                    if (messages.isEmpty()) {
                        String message1 = plugin.getMessagesManager().getAutoBroadcastAddOneLine();
                        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message1));
                        return;
                    }

                    String channel = plugin.getChannelsManager().getPlayerChannel(player);
                    if (channel == null || channel.isEmpty()) {
                        channel = "none";
                    }

                    String permission = permissions.getOrDefault(player, "default_permission");

                    AutoBroadcastManager.Broadcast broadcast = new AutoBroadcastManager.Broadcast(
                            broadcastEnabled.get(player),
                            messages,
                            titleEnabled.getOrDefault(player, false),
                            title.getOrDefault(player, null),
                            subtitle.getOrDefault(player, null),
                            soundEnabled.getOrDefault(player, false),
                            sound.getOrDefault(player, null),
                            particlesEnabled.getOrDefault(player, false),
                            particle.getOrDefault(player, null),
                            particleCount.getOrDefault(player, 0),
                            actionbarEnabled.getOrDefault(player, false),
                            actionbar.getOrDefault(player, null),
                            channel, permission
                    );

                    plugin.getAutoBroadcastManager().addBroadcast(
                            broadcastNames.get(player),
                            broadcastEnabled.get(player),
                            messages,
                            broadcast.titleEnabled(),
                            broadcast.title(),
                            broadcast.subtitle(),
                            broadcast.soundEnabled(),
                            broadcast.sound(),
                            broadcast.particlesEnabled(),
                            broadcast.particle(),
                            broadcast.particleCount(),
                            broadcast.actionbarEnabled(),
                            broadcast.actionbar(),
                            broadcast.channel(),
                            broadcast.permission()
                    );
                    plugin.getAutoBroadcastManager().reloadConfig();

                    broadcastNames.remove(player);
                    broadcastEnabled.remove(player);
                    broadcastMessages.remove(player);
                    titleEnabled.remove(player);
                    title.remove(player);
                    subtitle.remove(player);
                    soundEnabled.remove(player);
                    sound.remove(player);
                    particlesEnabled.remove(player);
                    particle.remove(player);
                    particleCount.remove(player);
                    actionbarEnabled.remove(player);
                    actionbar.remove(player);
                    permissions.remove(player);
                } else {
                    messages.add(message);
                }
            }
        }
    }

    public void antiBot(AsyncPlayerChatEvent chatEvent, Player player, PlayerCommandPreprocessEvent commandEvent) {
        if (chatEvent != null) {
            if (chatEvent.isCancelled()) { return; }
        } else if (commandEvent != null) {
            if (commandEvent.isCancelled()) { return; }
        }

        if (plugin.getPlayerJoinListener().isUnverified(player) && plugin.getConfigManager().isAntiBotEnabled() && !player.hasPermission("tchat.bypass.antibot") && !player.hasPermission("tchat.admin")) {
            if (chatEvent != null) {
                plugin.getAntiBotListener().playerChat(chatEvent, player);
            } else if (commandEvent != null) {
                plugin.getAntiBotListener().playerCommand(commandEvent);
            }
        }
    }

    public void replacer(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) { return; }

        String message = event.getMessage();
        message = plugin.getReplacerManager().replaceWords(message, event.getPlayer());
        event.setMessage(message);
    }

    public void grammar(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        plugin.getGrammarListener().checkGrammar(event, player, message);
        message = event.getMessage();
        event.setMessage(message);
    }

    public void chatMuted(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (MuteChatCommand.isChatMuted() && !player.hasPermission("tchat.bypass.mutechat")) {
            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getChatMuted();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            event.setCancelled(true);
        }
    }
}

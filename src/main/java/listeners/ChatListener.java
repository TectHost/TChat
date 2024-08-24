package listeners;

import commands.AutoBroadcastCommand;
import commands.MuteChatCommand;
import config.AutoBroadcastManager;
import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import blocked.BannedCommands;

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
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        plugin.getChatEnabledListener().checkChatEnabled(event, player);
        chatMuted(event, player);
        plugin.getChatCooldownListener().chatCooldown(event, player);
        plugin.getAntiUnicodeListener().checkUnicode(event, player, message);
        plugin.getChatBotListener().chatBot(event);
        plugin.getAntiAdvertising().checkAdvertising(event);
        plugin.getCapListener().playerAntiCap(event);
        antiBot(event, player, null);
        plugin.getAntiFloodListener().checkFlood(event, message, player);
        plugin.getBannedWords().playerBannedWords(event);
        grammar(event, player, message);
        replacer(event, message);

        chatFormatListener.playerFormat(event);
        plugin.getChatGamesSender().checkPlayerResponse(player, message);
        logs(player, message, 1);
        plugin.getLevelListener().addXp(event);
        handleBroadcastAddition(player, message);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        socialSpy(event, player, command);
        logs(player, command, 0);
        plugin.getAntiAdvertising().checkAdvertisingCommand(event, player, command);
        plugin.getChatCooldownListener().commandCooldown(event, player);
        antiBot(null, player, event);
        new BannedCommands(plugin).onPlayerCommandPreprocess(event);
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
                            broadcast.isTitleEnabled(),
                            broadcast.getTitle(),
                            broadcast.getSubtitle(),
                            broadcast.isSoundEnabled(),
                            broadcast.getSound(),
                            broadcast.isParticlesEnabled(),
                            broadcast.getParticle(),
                            broadcast.getParticleCount(),
                            broadcast.isActionbarEnabled(),
                            broadcast.getActionbar(),
                            broadcast.getChannel(),
                            broadcast.getPermission()
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

    public void socialSpy(PlayerCommandPreprocessEvent e, Player sender, String command) {
        if (plugin.getConfigManager().isSpyEnabled() && !sender.hasPermission("tchat.admin")) {
            plugin.getSocialSpyListener().spy(e, sender, command);
        }
    }

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
        if (event.isCancelled()) { return; }

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

package listeners;

import config.*;
import me.clip.placeholderapi.PlaceholderAPI;
import minealex.tchat.TChat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import utils.TranslateHexColorCodes;

import java.util.*;
import java.util.stream.Collectors;

public class ChatFormatListener implements Listener {

    private final Map<String, Map<UUID, Long>> channelCooldowns = new HashMap<>();

    private final ConfigManager configManager;
    private final GroupManager groupManager;
    private final TChat plugin;
    private final ChannelsConfigManager channelsConfigManager;
    private final MentionsManager mentionsManager;

    public ChatFormatListener(@NotNull TChat plugin, ConfigManager configManager, GroupManager groupManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.groupManager = groupManager;
        this.channelsConfigManager = plugin.getChannelsConfigManager();
        this.mentionsManager = plugin.getMentionsManager();
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void playerFormat(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) { return; }

        Player player = event.getPlayer();
        String message = event.getMessage();
        String format;

        String worldName = player.getWorld().getName();
        WorldsManager.WorldConfigData worldConfigData = plugin.getWorldsManager().getWorldsConfig().get(worldName);

        boolean perWorldChat = worldConfigData != null && worldConfigData.pwc();

        boolean chatRadiusEnabled = worldConfigData != null && worldConfigData.chatrEnabled();

        if (perWorldChat) {
            Set<Player> recipients = event.getRecipients().stream()
                    .filter(recipient -> recipient.hasPermission("tchat.admin") ||
                            recipient.hasPermission("tchat.bypass.pwc") ||
                            recipient.getWorld().getName().equals(worldName))
                    .collect(Collectors.toSet());

            event.getRecipients().clear();
            event.getRecipients().addAll(recipients);

        } else if (plugin.getWorldsManager().getBridgesConfig().values().stream()
                .anyMatch(bridge -> bridge.enabled() && bridge.worlds().contains(worldName))) {

            Set<String> worldsToInclude = new HashSet<>();
            worldsToInclude.add(worldName);

            for (WorldsManager.BridgeConfigData bridge : plugin.getWorldsManager().getBridgesConfig().values()) {
                if (bridge.enabled() && bridge.worlds().contains(worldName)) {
                    worldsToInclude.addAll(bridge.worlds());
                }
            }

            Set<Player> recipients = Bukkit.getOnlinePlayers().stream()
                    .filter(recipient -> recipient.hasPermission("tchat.admin") ||
                            recipient.hasPermission("tchat.bypass.bridge") ||
                            worldsToInclude.contains(recipient.getWorld().getName()))
                    .collect(Collectors.toSet());

            event.getRecipients().clear();
            event.getRecipients().addAll(recipients);
        }

        assert worldConfigData != null;
        String chatrPrefix = worldConfigData.bpNone();
        if (chatRadiusEnabled) {
            int chatRadius = worldConfigData.chatr();
            String cRadius = worldConfigData.bcchatr();

            Set<Player> recipients = event.getRecipients().stream()
                    .filter(recipient -> recipient.hasPermission("tchat.admin") ||
                            recipient.hasPermission("tchat.bypass.chat-radius") ||
                            (event.getMessage().startsWith(cRadius) && recipient.hasPermission("tchat.bypass.chat-radius.character")) ||
                            (recipient.getWorld().getName().equals(worldName) &&
                                    recipient.getLocation().distance(player.getLocation()) <= chatRadius))
                    .collect(Collectors.toSet());

            event.getRecipients().clear();
            event.getRecipients().addAll(recipients);

            boolean chatr = message.startsWith(worldConfigData.bcchatr());
            if (chatr) {
                chatrPrefix = worldConfigData.bpGlobal();
            } else {
                chatrPrefix = worldConfigData.bpLocal();
            }
        }

        if (plugin.getConfigManager().isChatColorEnabled()) {
            String chatColor = plugin.getSaveManager().getChatColor(player.getUniqueId()) + plugin.getSaveManager().getFormat(player.getUniqueId());

            if (!chatColor.equalsIgnoreCase("")) {
                message = chatColor + message;
                message = plugin.getTranslateColors().translateColors(player, message);
            }
        }

        if (plugin.getConfigManager().isColorsChatEnabled()) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.color.all")) {
                message = plugin.getTranslateColors().translateColors(player, message);
            } else {
                String finalMessage1 = message;

                if (message.length() >= 2 && message.charAt(0) == '&') {
                    char colorCode = finalMessage1.charAt(1);

                    if (ChatColor.ALL_CODES.indexOf(colorCode) > -1) {
                        if (player.hasPermission("tchat.color." + colorCode)) {
                            message = plugin.getTranslateColors().translateColors(player, message);
                        } else {
                            message = ChatColor.stripColor(message);
                        }
                    }
                } else {
                    message = ChatColor.stripColor(message);
                }
            }
        }

        if (plugin.getMentionsManager().isEnabled()) { message = handleMentions(event, player, message); }

        if (plugin.getConfigManager().isIgnoreEnabled()) {
            List<String> playerIgnoreList = plugin.getSaveManager().getIgnoreList(player.getUniqueId());

            List<Player> finalRecipients = event.getRecipients().stream()
                    .filter(recipient -> {
                        if (playerIgnoreList.contains("all")) {
                            return recipient.equals(player);
                        }

                        List<String> recipientIgnoreList = plugin.getSaveManager().getIgnoreList(recipient.getUniqueId());
                        return !recipientIgnoreList.contains("all") && !recipientIgnoreList.contains(player.getUniqueId().toString());
                    })
                    .toList();

            event.getRecipients().clear();
            event.getRecipients().addAll(finalRecipients);
        }

        String channelName = plugin.getChannelsManager().getPlayerChannel(player);
        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);

        if (plugin.getConfigManager().isFormatEnabled() && groupManager.isFormatEnabled(player)) {
            if (channel != null && channel.cooldownEnabled()) {
                long cooldownTime = channel.cooldown();

                if (!canSendMessage(player, channelName, cooldownTime)) {
                    long lastMessageTime = channelCooldowns.getOrDefault(channelName, new HashMap<>())
                            .getOrDefault(player.getUniqueId(), 0L);
                    long currentTime = System.currentTimeMillis();
                    long timeLeft = cooldownTime - (currentTime - lastMessageTime);

                    long secondsLeft = timeLeft / 1000;

                    String p = plugin.getMessagesManager().getPrefix();
                    String m = plugin.getMessagesManager().getCooldownChannel();
                    m = m.replace("%seconds%", String.valueOf(secondsLeft));
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
                    event.setCancelled(true);
                    return;
                }

                setLastMessageTime(player, channelName);
            }
            if (channel != null && channel.formatEnabled() && channel.enabled() &&
                    (player.hasPermission(channel.permission()) || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all"))) {
                format = channel.format();
                format = format.replace("%channel%", channelName);
            } else {
                String groupName = groupManager.getGroup(player);
                if (configManager.isFormatGroup()) {
                    format = groupManager.getGroupFormat(groupName);
                } else {
                    format = configManager.getFormat();
                }
                if (format.isEmpty()) {
                    format = "<" + player.getName() + "> " + message;
                    String errorMessage = plugin.getMessagesManager().getNoFormatGroup();
                    String prefix = plugin.getMessagesManager().getPrefix();
                    Bukkit.getConsoleSender().sendMessage(plugin.getTranslateColors().translateColors(player, prefix) +
                            org.bukkit.ChatColor.translateAlternateColorCodes('&', errorMessage).replace("%group%", groupName));
                }
            }

            if (format.contains("%radius_mode%")) {
                format = format.replace("%radius_mode%", chatrPrefix);
            }

            format = PlaceholderAPI.setPlaceholders(player, format);
            format = TranslateHexColorCodes.translateHexColorCodes("&#", "", format);
            format = ChatColor.translateAlternateColorCodes('&', format);

            String[] parts = format.split("¡", 2);
            String mainFormat = parts[0];
            String extraFormat = parts.length > 1 ? parts[1] : "";

            TextComponent mainComponent = new TextComponent(TextComponent.fromLegacyText(mainFormat));
            TextComponent messageComponent = new TextComponent(TextComponent.fromLegacyText(extraFormat + message));

            String groupName = groupManager.getGroup(player);
            GroupManager.HoverClickAction playerHoverClick = groupManager.getPlayerHoverClickAction(groupName);
            if (playerHoverClick.isEnabled()) {
                mainComponent.setHoverEvent(createHoverEvent(player, playerHoverClick.getHoverText()));
                if (playerHoverClick.isClickEnabled()) {
                    applyClickAction(mainComponent, playerHoverClick.getClickAction(), player.getName());
                }
            }

            GroupManager.HoverClickAction messageHoverClick = groupManager.getMessageHoverClickAction(groupName);
            if (messageHoverClick.isEnabled()) {
                messageComponent.setHoverEvent(createHoverEvent(player, messageHoverClick.getHoverText()));
                if (messageHoverClick.isClickEnabled()) {
                    applyClickAction(messageComponent, messageHoverClick.getClickAction(), player.getName());
                }
            }

            mainComponent.addExtra(messageComponent);

            for (Player p : event.getRecipients()) {
                if (channel == null || !channel.enabled()) {
                    p.spigot().sendMessage(mainComponent);
                } else {
                    String recipientChannel = plugin.getChannelsManager().getPlayerChannel(p);
                    boolean hasPermissionForChannel = p.hasPermission(channel.permission()) || p.hasPermission("tchat.admin") || p.hasPermission("tchat.channel.all");
                    boolean isInRecipientChannel = recipientChannel != null && recipientChannel.equals(channelName);

                    int messageMode = channel.messageMode();
                    if (messageMode == 0 || (messageMode == 1 && hasPermissionForChannel) || (messageMode == 2 && isInRecipientChannel)) {
                        p.spigot().sendMessage(mainComponent);
                    }
                }
            }

            event.getRecipients().clear();
            event.setFormat(mainComponent.toPlainText().replace("%", "%%"));

            if (plugin.getDiscordManager().isDiscordEnabled()) {
                if (channelName == null) {
                    String discordMessage = removeMinecraftColorCodes(mainComponent.toLegacyText());
                    plugin.getDiscordHook().sendMessage(discordMessage, plugin.getDiscordManager().getDiscordHook());
                } else if (plugin.getChannelsConfigManager().getChannel(channelName).discordEnabled()) {
                    String URL = plugin.getChannelsConfigManager().getChannel(channelName).discordHook();
                    String discordMessage = removeMinecraftColorCodes(mainComponent.toLegacyText());
                    plugin.getDiscordHook().sendMessage(discordMessage, URL);
                }
            }
        } else {
            if (plugin.getDiscordManager().isDiscordEnabled()) {
                if (channelName == null) {
                    String discordMessage = removeMinecraftColorCodes(message);
                    plugin.getDiscordHook().sendMessage(discordMessage, plugin.getDiscordManager().getDiscordHook());
                } else if (plugin.getChannelsConfigManager().getChannel(channelName).discordEnabled()) {
                    String URL = plugin.getChannelsConfigManager().getChannel(channelName).discordHook();
                    String discordMessage = removeMinecraftColorCodes(message);
                    plugin.getDiscordHook().sendMessage(discordMessage, URL);
                }
            }
        }
    }

    @Contract(pure = true)
    private @NotNull String removeMinecraftColorCodes(@NotNull String message) {
        String legacyHexPattern = "§x(§[0-9a-fA-F]){6}";
        String standardColorPattern = "§[0-9a-fk-or]";

        String result = message.replaceAll(legacyHexPattern, "");
        result = result.replaceAll(standardColorPattern, "");

        return result;
    }

    @Contract("_, _ -> new")
    @SuppressWarnings("deprecation")
    private @NotNull HoverEvent createHoverEvent(Player player, @NotNull List<String> hoverText) {
        TextComponent hoverComponent = new TextComponent();

        List<String> processedLines = hoverText.stream()
                .map(line -> plugin.getTranslateColors().translateColors(player, line))
                .toList();

        int maxLineLength = processedLines.stream()
                .mapToInt(line -> ChatColor.stripColor(line.replace("%center%", "")).length())
                .max()
                .orElse(0);

        boolean first = true;
        for (String line : processedLines) {
            if (!first) {
                hoverComponent.addExtra("\n");
            } else {
                first = false;
            }

            if (line.contains("%center%")) {
                String strippedLine = ChatColor.stripColor(line.replace("%center%", ""));
                int spacesToAdd = (maxLineLength - strippedLine.length()) / 2;
                String centeredLine = line.replace("%center%", " ".repeat(Math.max(0, spacesToAdd)));
                hoverComponent.addExtra(new TextComponent(TextComponent.fromLegacyText(centeredLine)));
            } else {
                hoverComponent.addExtra(new TextComponent(TextComponent.fromLegacyText(line)));
            }
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverComponent).create());
    }

    private String handleMentions(@NotNull AsyncPlayerChatEvent event, Player player, String message) {
        Set<Player> mentionedPlayers = new HashSet<>();
        String finalMessage = message;

        for (Player recipient : event.getRecipients()) {
            String groupName = groupManager.getGroup(recipient);
            MentionsManager.GroupConfig groupConfig = mentionsManager.getGroupConfig(groupName);

            String mentionCharacter = groupConfig.getCharacter();
            String mentionColor = groupConfig.getColor();

            String mention = mentionCharacter + recipient.getName();
            if (message.contains(mention)) {
                String coloredMention = plugin.getTranslateColors().translateColors(player, mentionColor + mention);
                finalMessage = finalMessage.replace(mention, coloredMention);
                mentionedPlayers.add(recipient);
            }
        }

        for (Player mentionedPlayer : mentionedPlayers) {
            String groupName = groupManager.getGroup(mentionedPlayer);
            MentionsManager.EventConfig personalConfig = mentionsManager.getPersonalEventConfig(groupName);

            if (personalConfig.isMessageEnabled()) {
                mentionedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', String.join("\n", personalConfig.getMessage())));
            }

            if (personalConfig.isTitleEnabled() || personalConfig.isSubtitleEnabled()) {
                String title = personalConfig.isTitleEnabled()
                        ? plugin.getTranslateColors().translateColors(mentionedPlayer, personalConfig.getTitle())
                        : "";
                String subtitle = personalConfig.isSubtitleEnabled()
                        ? plugin.getTranslateColors().translateColors(mentionedPlayer, personalConfig.getSubtitle())
                        : "";

                mentionedPlayer.sendTitle(title, subtitle, 10, 70, 20);
            }

            if (personalConfig.isSoundEnabled()) {
                String sound = personalConfig.getSound();
                mentionedPlayer.playSound(mentionedPlayer.getLocation(), sound, 1.0f, 1.0f);
            }

            if (personalConfig.isParticlesEnabled()) {
                String particle = personalConfig.getParticle();
                mentionedPlayer.getWorld().spawnParticle(org.bukkit.Particle.valueOf(particle.toUpperCase()), mentionedPlayer.getLocation(), 10);
            }

            if (personalConfig.isActionbarEnabled()) {
                mentionedPlayer.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, new TextComponent(plugin.getTranslateColors().translateColors(player, personalConfig.getActionbarMessage())));
            }
        }

        String groupName = groupManager.getGroup(player);
        MentionsManager.EventConfig globalConfig = mentionsManager.getGlobalEventConfig(groupName);

        if (globalConfig.isMessageEnabled() || globalConfig.isTitleEnabled() || globalConfig.isSoundEnabled() || globalConfig.isParticlesEnabled() || globalConfig.isActionbarEnabled()) {
            for (Player recipient : Bukkit.getOnlinePlayers()) {

                if (globalConfig.isMessageEnabled()) {
                    recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', String.join("\n", globalConfig.getMessage())));
                }

                if (globalConfig.isTitleEnabled() || globalConfig.isSubtitleEnabled()) {
                    String title = globalConfig.isTitleEnabled()
                            ? plugin.getTranslateColors().translateColors(player, globalConfig.getTitle())
                            : " ";
                    String subtitle = globalConfig.isSubtitleEnabled()
                            ? plugin.getTranslateColors().translateColors(player, globalConfig.getSubtitle())
                            : " ";

                    recipient.sendTitle(title, subtitle, 10, 70, 20);
                }

                if (globalConfig.isSoundEnabled()) {
                    String sound = globalConfig.getSound();
                    recipient.playSound(recipient.getLocation(), sound, 1.0f, 1.0f);
                }

                if (globalConfig.isParticlesEnabled()) {
                    String particle = globalConfig.getParticle();
                    recipient.getWorld().spawnParticle(org.bukkit.Particle.valueOf(particle.toUpperCase()), recipient.getLocation(), 10);
                }

                if (globalConfig.isActionbarEnabled()) {
                    recipient.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, new TextComponent(plugin.getTranslateColors().translateColors(player, globalConfig.getActionbarMessage())));
                }
            }
        }

        return finalMessage;
    }

    private void applyClickAction(TextComponent component, @NotNull String clickAction, String playerName) {
        String replacedAction = clickAction.replace("%player%", playerName);

        if (replacedAction.startsWith("[EXECUTE]")) {
            String command = replacedAction.substring("[EXECUTE] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        } else if (replacedAction.startsWith("[OPEN]")) {
            String url = replacedAction.substring("[OPEN] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        } else if (replacedAction.startsWith("[SUGGEST]")) {
            String command = replacedAction.substring("[SUGGEST] ".length());
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        }
    }

    private boolean canSendMessage(@NotNull Player player, String channelName, long cooldownTime) {
        Map<UUID, Long> playerCooldowns = channelCooldowns.getOrDefault(channelName, new HashMap<>());
        long lastMessageTime = playerCooldowns.getOrDefault(player.getUniqueId(), 0L);
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastMessageTime) >= cooldownTime;
    }

    private void setLastMessageTime(@NotNull Player player, String channelName) {
        channelCooldowns.computeIfAbsent(channelName, k -> new HashMap<>()).put(player.getUniqueId(), System.currentTimeMillis());
    }
}
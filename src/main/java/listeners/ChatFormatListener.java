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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatFormatListener implements Listener {

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
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();
        String format;

        String worldName = player.getWorld().getName();
        WorldsManager.WorldConfigData worldConfigData = plugin.getWorldsManager().getWorldsConfig().get(worldName);
        boolean perWorldChat = worldConfigData != null && worldConfigData.pwc();

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

        if (plugin.getConfigManager().isChatColorEnabled()) {
            String chatColor = plugin.getSaveManager().getChatColor(player.getUniqueId()) + plugin.getSaveManager().getFormat(player.getUniqueId());
            if (!chatColor.equalsIgnoreCase("")) {
                message = chatColor + message;
                message = plugin.getTranslateColors().translateColors(player, message);
            } else if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.color.all")) {
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
            List<Player> finalRecipients = event.getRecipients().stream()
                    .filter(recipient -> !isIgnored(player, recipient))
                    .toList();

            event.getRecipients().clear();
            event.getRecipients().addAll(finalRecipients);
        }

        String channelName = plugin.getChannelsManager().getPlayerChannel(player);
        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);

        if (plugin.getConfigManager().isFormatEnabled() && groupManager.isFormatEnabled(player)) {
            if (channel != null && channel.isFormatEnabled() && channel.isEnabled() &&
                    (player.hasPermission(channel.getPermission()) || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all"))) {
                format = channel.getFormat();
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

            format = format.replace("%player%", player.getName());
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

            event.setCancelled(true);

            for (Player p : event.getRecipients()) {
                if (channel == null || !channel.isEnabled()) {
                    p.spigot().sendMessage(mainComponent);
                } else {
                    String recipientChannel = plugin.getChannelsManager().getPlayerChannel(p);
                    boolean hasPermissionForChannel = p.hasPermission(channel.getPermission()) || p.hasPermission("tchat.admin") || p.hasPermission("tchat.channel.all");
                    boolean isInRecipientChannel = recipientChannel != null && recipientChannel.equals(channelName);

                    int messageMode = channel.getMessageMode();
                    if (messageMode == 0 || (messageMode == 1 && hasPermissionForChannel) || (messageMode == 2 && isInRecipientChannel)) {
                        p.spigot().sendMessage(mainComponent);
                    }
                }
            }

            if (configManager.isRegisterMessagesOnConsole()) {
                String consoleMessage = mainComponent.toLegacyText();
                consoleMessage = plugin.getTranslateColors().translateColors(player, consoleMessage);
                Bukkit.getConsoleSender().sendMessage(consoleMessage);
            }

            if (plugin.getDiscordManager().isDiscordEnabled()) {
                String discordMessage = removeMinecraftColorCodes(mainComponent.toLegacyText());
                plugin.getDiscordHook().sendMessage(discordMessage);
            }
        } else {
            if (plugin.getDiscordManager().isDiscordEnabled()) {
                String discordMessage = removeMinecraftColorCodes(message);
                plugin.getDiscordHook().sendMessage(discordMessage);
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
        TextComponent hoverComponent = new TextComponent("");
        boolean first = true;

        for (String line : hoverText) {
            if (!first) {
                hoverComponent.addExtra("\n");
            } else {
                first = false;
            }

            String replacedLine = PlaceholderAPI.setPlaceholders(player, line);

            replacedLine = TranslateHexColorCodes.translateHexColorCodes("&#", "", replacedLine);
            replacedLine = ChatColor.translateAlternateColorCodes('&', replacedLine);

            hoverComponent.addExtra(new TextComponent(TextComponent.fromLegacyText(replacedLine)));
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverComponent).create());
    }

    private boolean isIgnored(@NotNull Player sender, @NotNull Player recipient) {
        UUID senderId = sender.getUniqueId();
        UUID recipientId = recipient.getUniqueId();
        List<String> ignoreList = plugin.getSaveManager().getIgnoreList(recipientId);
        return ignoreList.contains(senderId.toString());
    }

    private String handleMentions(AsyncPlayerChatEvent event, Player player, String message) {
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
                mentionedPlayer.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, new TextComponent(personalConfig.getActionbarMessage()));
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
                    recipient.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, new TextComponent(globalConfig.getActionbarMessage()));
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
}
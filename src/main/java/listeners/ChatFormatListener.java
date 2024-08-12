package listeners;

import config.ChannelsConfigManager;
import config.ConfigManager;
import config.GroupManager;
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

import java.util.List;
import java.util.UUID;

public class ChatFormatListener implements Listener {

    private final ConfigManager configManager;
    private final GroupManager groupManager;
    private final TChat plugin;
    private final ChannelsConfigManager channelsConfigManager;

    public ChatFormatListener(@NotNull TChat plugin, ConfigManager configManager, GroupManager groupManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.groupManager = groupManager;
        this.channelsConfigManager = plugin.getChannelsConfigManager();
    }

    @EventHandler
    public void playerFormat(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String message = event.getMessage();
        String format;

        String channelName = plugin.getChannelsManager().getPlayerChannel(player);
        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);

        if (plugin.getConfigManager().isFormatEnabled()) {
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

            if (plugin.getConfigManager().isMentionsEnabled()) {
                String mentionCharacter = plugin.getConfigManager().getMentionCharacter();
                String mentionColor = plugin.getConfigManager().getMentionColor();

                for (Player recipient : event.getRecipients()) {
                    String mention = mentionCharacter + recipient.getName();
                    if (message.contains(mention)) {
                        String coloredMention = TranslateHexColorCodes.translateHexColorCodes("&#", "", mentionColor + mention + "&f");
                        message = message.replace(mention, coloredMention);
                    }
                }
            }

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

            if (plugin.getConfigManager().isChatColorEnabled()) {
                String playerFormat = plugin.getSaveManager().getFormat(player.getUniqueId());
                String chatColor = plugin.getSaveManager().getChatColor(player.getUniqueId());
                if (!chatColor.equalsIgnoreCase("none")) {
                    messageComponent.setColor(ChatColor.of(chatColor));
                    message = chatColor + playerFormat + message;
                    message = plugin.getTranslateColors().translateColors(player, message);
                }
            }

            // Apply hover and click actions to message component
            GroupManager.HoverClickAction messageHoverClick = groupManager.getMessageHoverClickAction(groupName);
            if (messageHoverClick.isEnabled()) {
                messageComponent.setHoverEvent(createHoverEvent(player, messageHoverClick.getHoverText()));
                if (messageHoverClick.isClickEnabled()) {
                    applyClickAction(messageComponent, messageHoverClick.getClickAction(), player.getName());
                }
            }

            mainComponent.addExtra(messageComponent);

            if (plugin.getConfigManager().isIgnoreEnabled()) {
                List<Player> recipients = event.getRecipients().stream()
                        .filter(recipient -> !isIgnored(player, recipient))
                        .toList();

                event.getRecipients().clear();
                event.getRecipients().addAll(recipients);
            }

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
        }
    }

    @Contract(pure = true)
    private @NotNull String removeMinecraftColorCodes(@NotNull String message) {
        return message.replaceAll("(?i)§[0-9a-fk-or]", "");
    }

    @Contract("_, _ -> new")
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
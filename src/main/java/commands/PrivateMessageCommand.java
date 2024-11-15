package commands;

import config.PrivateMessagesConfigManager;
import minealex.tchat.TChat;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import utils.TranslateColors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PrivateMessageCommand implements CommandExecutor {

    private final Map<UUID, UUID> lastMessaged = new HashMap<>();
    private final TChat plugin;

    public PrivateMessageCommand(TChat plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, UUID> getLastMessaged() {
        return lastMessaged;
    }

    public void setLastMessaged(UUID sender, UUID receiver) {
        lastMessaged.put(sender, receiver);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player senderPlayer)) {
            String m = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return true;
        }

        TranslateColors translateColors = plugin.getTranslateColors();

        if (senderPlayer.hasPermission("tchat.msg") || senderPlayer.hasPermission("tchat.admin")) {
            if (args.length < 2) {
                String message = plugin.getMessagesManager().getUsageMsg();
                senderPlayer.sendMessage(translateColors.translateColors(senderPlayer, prefix + message));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[0]);

            if (targetPlayer == null) {
                String message = plugin.getMessagesManager().getPlayerNotFound();
                senderPlayer.sendMessage(translateColors.translateColors(senderPlayer, prefix + message));
                return true;
            }

            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                messageBuilder.append(args[i]).append(" ");
            }

            String messageToSend = messageBuilder.toString().trim();

            String senderFormattedMessage = translateColors.translateColors(senderPlayer,
                    plugin.getPrivateMessagesConfigManager().getMsgFormatSender()
                            .replace("%sender%", sender.getName())
                            .replace("%recipient%", targetPlayer.getName())
                            .replace("%message%", messageToSend));

            String receiverFormattedMessage = translateColors.translateColors(targetPlayer,
                    plugin.getPrivateMessagesConfigManager().getMsgFormatReceiver()
                            .replace("%sender%", sender.getName())
                            .replace("%recipient%", targetPlayer.getName())
                            .replace("%message%", messageToSend));

            String senderGroup = plugin.getGroupManager().getGroupName(senderPlayer);
            String receiverGroup = plugin.getGroupManager().getGroupName(targetPlayer);

            PrivateMessagesConfigManager.HoverConfig senderHoverConfig = plugin.getPrivateMessagesConfigManager().getPmHoverConfig(senderGroup);
            PrivateMessagesConfigManager.HoverConfig receiverHoverConfig = plugin.getPrivateMessagesConfigManager().getPmHoverConfig(receiverGroup);

            List<String> hoverSenderTextList = senderHoverConfig != null
                    ? senderHoverConfig.getSenderTexts().stream()
                    .map(line -> translateColors.translateColors(senderPlayer, line))
                    .collect(Collectors.toList())
                    : List.of("No hover found (sender)");

            String hoverSenderText = String.join("\n", hoverSenderTextList);

            String senderAction = senderHoverConfig != null ? senderHoverConfig.getSenderAction() : null;

            HoverEvent.Action senderHoverActionType = HoverEvent.Action.SHOW_TEXT;

            TextComponent senderMessage = new TextComponent(new ComponentBuilder(senderFormattedMessage).create());
            senderMessage.setHoverEvent(new HoverEvent(senderHoverActionType, new ComponentBuilder(hoverSenderText).create()));

            if (senderAction != null && !senderAction.isEmpty()) {
                applyClickEvent(senderMessage, senderAction);
            }

            List<String> hoverReceiverTextList = receiverHoverConfig != null
                    ? receiverHoverConfig.getReceiverTexts().stream()
                    .map(line -> translateColors.translateColors(targetPlayer, line))
                    .collect(Collectors.toList())
                    : List.of("No hover found (receiver)");

            String hoverReceiverText = String.join("\n", hoverReceiverTextList);

            String receiverAction = receiverHoverConfig != null ? receiverHoverConfig.getReceiverAction() : null;

            HoverEvent.Action receiverHoverActionType = HoverEvent.Action.SHOW_TEXT;

            TextComponent receiverMessage = new TextComponent(new ComponentBuilder(receiverFormattedMessage).create());
            receiverMessage.setHoverEvent(new HoverEvent(receiverHoverActionType, new ComponentBuilder(hoverReceiverText).create()));

            if (receiverAction != null && !receiverAction.isEmpty()) {
                applyClickEvent(receiverMessage, receiverAction);
            }

            senderPlayer.spigot().sendMessage(senderMessage);
            targetPlayer.spigot().sendMessage(receiverMessage);

            setLastMessaged(senderPlayer.getUniqueId(), targetPlayer.getUniqueId());
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            senderPlayer.sendMessage(translateColors.translateColors(senderPlayer, prefix + message));
        }

        return true;
    }

    private void applyClickEvent(TextComponent message, @NotNull String action) {
        if (action.startsWith("[SUGGEST]")) {
            String command = action.substring(10);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        } else if (action.startsWith("[EXECUTE]")) {
            String command = action.substring(10);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        } else if (action.startsWith("[OPEN]")) {
            String url = action.substring(7);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        }
    }
}

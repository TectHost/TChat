package commands;

import config.ChannelsConfigManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import utils.ChannelsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelCommand implements CommandExecutor {

    private final TChat plugin;
    private final ChannelsManager channelsManager;
    private final ChannelsConfigManager channelsConfigManager;

    public ChannelCommand(TChat plugin) {
        this.plugin = plugin;
        this.channelsManager = plugin.getChannelsManager();
        this.channelsConfigManager = plugin.getChannelsConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (args.length == 0) {
            String message = plugin.getMessagesManager().getUsageChannel();
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String action = args[0];

        if ("join".equalsIgnoreCase(action)) {
            if (player.hasPermission("tchat.channel.command.join") || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all")) {
                if (args.length < 2) {
                    String message = plugin.getMessagesManager().getUsageJoinChannel();
                    sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    return true;
                }
                String channelName = args[1];
                joinChannel(player, channelName);
            } else {
                String message = plugin.getMessagesManager().getNoPermission();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            }
        } else if ("leave".equalsIgnoreCase(action)) {
            if (player.hasPermission("tchat.channel.command.leave") || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all")) {
                if (args.length < 2) {
                    String message = plugin.getMessagesManager().getUsageLeaveChannel();
                    sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    return true;
                }
                String channelName = args[1];
                leaveChannel(player, channelName);
            } else {
                String message = plugin.getMessagesManager().getNoPermission();
                sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            }
        } else {
            String message = plugin.getMessagesManager().getUsageChannel();
            sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }

        return true;
    }

    private void joinChannel(Player player, String channelName) {
        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);
        String prefix = plugin.getMessagesManager().getPrefix();

        if (channel == null) {
            String message = plugin.getMessagesManager().getChannelNotExist();
            message = message.replace("%channel%", channelName);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return;
        }

        if (!player.hasPermission(channel.getPermission()) && !player.hasPermission("tchat.admin") && !player.hasPermission("tchat.channel.all")) {
            String message = plugin.getMessagesManager().getChannelNoPermissionJoin();
            message = message.replace("%channel%", channelName);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return;
        }

        String currentChannel = channelsManager.getPlayerChannel(player);

        if (channelName.equalsIgnoreCase(currentChannel)) {
            String message = plugin.getMessagesManager().getChannelAlready();
            message = message.replace("%channel%", channelName);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return;
        }

        if (currentChannel != null) {
            channelsManager.removePlayerFromChannel(player);
            String message = plugin.getMessagesManager().getChannelLeft();
            message = message.replace("%channel%", currentChannel);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }

        channelsManager.setPlayerChannel(player, channelName);
        String message1 = plugin.getMessagesManager().getChannelJoin();
        message1 = message1.replace("%channel%", channelName);
        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message1));

        for (Player p : Bukkit.getOnlinePlayers()) {
            String recipientChannel = plugin.getChannelsManager().getPlayerChannel(p);
            boolean hasPermissionForChannel = p.hasPermission(channel.getPermission()) || p.hasPermission("tchat.admin") || p.hasPermission("tchat.channel.all");
            boolean isInRecipientChannel = recipientChannel != null && recipientChannel.equals(channelName);

            if (!p.equals(player)) {
                int announceMode = channel.getAnnounceMode();

                String message = plugin.getMessagesManager().getChannelJoinAnnounce();
                message = message.replace("%player%", player.getName());
                message = message.replace("%channel%", channelName);

                if (announceMode == 0) {
                    p.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                } else if (announceMode == 1) {
                    if (hasPermissionForChannel) {
                        p.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    }
                } else if (announceMode == 2) {
                    if (isInRecipientChannel) {
                        p.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    }
                }
            }
        }
    }

    private void leaveChannel(Player player, String channelName) {
        String currentChannel = channelsManager.getPlayerChannel(player);
        String prefix = plugin.getMessagesManager().getPrefix();

        if (!channelName.equalsIgnoreCase(currentChannel)) {
            String message = plugin.getMessagesManager().getNoChannel();
            message = message.replace("%channel%", channelName);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return;
        }

        ChannelsConfigManager.Channel channel = channelsConfigManager.getChannel(channelName);

        if (channel != null && !player.hasPermission("tchat.channel.command.leave") && !player.hasPermission("tchat.admin") && !player.hasPermission("tchat.channel.all")) {
            String message = plugin.getMessagesManager().getNoPermissionChannelLeft();
            message = message.replace("%channel%", channelName);
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return;
        }

        channelsManager.removePlayerFromChannel(player);
        String message1 = plugin.getMessagesManager().getChannelLeft();
        message1 = message1.replace("%channel%", currentChannel);
        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message1));

        for (Player p : Bukkit.getOnlinePlayers()) {
            String recipientChannel = plugin.getChannelsManager().getPlayerChannel(p);
            assert channel != null;
            boolean hasPermissionForChannel = p.hasPermission(channel.getPermission()) || p.hasPermission("tchat.admin") || p.hasPermission("tchat.channel.all");
            boolean isInRecipientChannel = recipientChannel != null && recipientChannel.equals(channelName);

            if (!p.equals(player)) {
                int announceMode = channel.getAnnounceMode();

                String message = plugin.getMessagesManager().getChannelLeftAnnounce();
                message = message.replace("%player%", player.getName());
                message = message.replace("%channel%", channelName);

                if (announceMode == 0) {
                    p.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                } else if (announceMode == 1) {
                    if (hasPermissionForChannel) {
                        p.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    }
                } else if (announceMode == 2) {
                    if (isInRecipientChannel) {
                        p.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    }
                }
            }
        }
    }
}

package utils;

import config.ChannelsConfigManager;
import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelsCompleter implements TabCompleter {

    private final TChat plugin;

    public ChannelsCompleter(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            List<String> commands = new ArrayList<>();
            if (player.hasPermission("tchat.channel.command.join") || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all")) {
                commands.add("join");
            }
            if (player.hasPermission("tchat.channel.command.leave") || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all")) {
                commands.add("leave");
            }
            if (player.hasPermission("tchat.channel.command.send") || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all")) {
                commands.add("send");
            }
            return commands.stream().filter(cmd -> cmd.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length == 2) {
            String action = args[0];
            if ("join".equalsIgnoreCase(action) || "leave".equalsIgnoreCase(action)) {
                Map<String, ChannelsConfigManager.Channel> channels = plugin.getChannelsConfigManager().getChannels();
                List<String> filteredChannels = channels.keySet().stream()
                        .filter(channelName -> {
                            if ("join".equalsIgnoreCase(action)) {
                                ChannelsConfigManager.Channel channel = channels.get(channelName);
                                return player.hasPermission(channel.getPermission()) || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all");
                            } else if ("leave".equalsIgnoreCase(action)) {
                                return channelName.equalsIgnoreCase(plugin.getChannelsManager().getPlayerChannel(player));
                            }
                            return false;
                        })
                        .toList();

                return filteredChannels.stream().filter(channelName -> channelName.startsWith(args[1])).collect(Collectors.toList());
            } else if ("send".equalsIgnoreCase(action)) {
                Map<String, ChannelsConfigManager.Channel> channels = plugin.getChannelsConfigManager().getChannels();
                List<String> filteredChannels = channels.keySet().stream()
                        .filter(channelName -> player.hasPermission(channels.get(channelName).getPermission()) || player.hasPermission("tchat.admin") || player.hasPermission("tchat.channel.all"))
                        .toList();

                return filteredChannels.stream().filter(channelName -> channelName.startsWith(args[1])).collect(Collectors.toList());
            }
        } else if (args.length > 2 && "send".equalsIgnoreCase(args[0])) {
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }
}
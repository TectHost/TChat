package commands;

import config.ConfigManager;
import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SocialSpyCommand implements CommandExecutor, TabCompleter {

    private final TChat plugin;
    private final ConfigManager configManager;

    public SocialSpyCommand(TChat plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        if (!player.hasPermission("tchat.admin.command.social-spy") && !player.hasPermission("tchat.admin")) {
            String message = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        if (args.length == 0) {
            String message = plugin.getMessagesManager().getUsageSocialSpy();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            return true;
        }

        String action = args[0].toLowerCase();

        var config = configManager.getConfig();
        if (config == null) {
            player.sendMessage("[ERROR] SocialSpyCommand.java - Error in config.yml! (#7a8cn)");
            return true;
        }

        String message;
        switch (action) {
            case "enable":
                if (plugin.getConfigManager().isSpyEnabled()) {
                    message = plugin.getMessagesManager().getAlreadyEnabledSpy();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                } else {
                    config.set("spy.commands.enabled", true);
                    configManager.saveConfig();
                    configManager.reloadConfig();
                    message = plugin.getMessagesManager().getEnabledSpy();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
                break;

            case "disable":
                if (!plugin.getConfigManager().isSpyEnabled()) {
                    message = plugin.getMessagesManager().getAlreadyDisabledSpy();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                } else {
                    config.set("spy.commands.enabled", false);
                    configManager.saveConfig();
                    message = plugin.getMessagesManager().getDisabledSpy();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
                break;

            case "mode":
                if (args.length < 2) {
                    message = plugin.getMessagesManager().getInvalidModeSpy();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                    return true;
                }

                try {
                    int mode = Integer.parseInt(args[1]);
                    if (mode < 1 || mode > 3) {
                        message = plugin.getMessagesManager().getInvalidModeSpy();
                        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                        return true;
                    }

                    config.set("spy.commands.mode", mode);
                    configManager.saveConfig();
                    configManager.reloadConfig();
                    message = plugin.getMessagesManager().getModeSpy();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message).replace("%mode%", String.valueOf(mode)));

                } catch (NumberFormatException e) {
                    message = plugin.getMessagesManager().getInvalidModeSpy();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
                break;

            default:
                message = plugin.getMessagesManager().getUsageSocialSpy();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return null;
        }

        if (args.length == 1) {
            completions.add("enable");
            completions.add("disable");
            completions.add("mode");
        } else if (args.length == 2 && "mode".equalsIgnoreCase(args[0])) {
            completions.add("1");
            completions.add("2");
            completions.add("3");
        }

        return completions;
    }
}

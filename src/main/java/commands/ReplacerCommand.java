package commands;

import config.ReplacerManager;
import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplacerCommand implements CommandExecutor {

    private final TChat plugin;
    private final ReplacerManager replacerManager;
    private final String prefix;

    public ReplacerCommand(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.replacerManager = plugin.getReplacerManager();
        this.prefix = plugin.getMessagesManager().getPrefix();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        String m = plugin.getMessagesManager().getUsageReplacer();
        if (args.length == 0) {
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            handleListCommand(sender);
        } else if (args[0].equalsIgnoreCase("add")) {
            handleAddCommand(sender, args);
        } else if (args[0].equalsIgnoreCase("remove")) {
            handleRemoveCommand(sender, args);
        } else {
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
        }

        return true;
    }

    private void handleListCommand(CommandSender sender) {
        if (!(sender instanceof Player) || sender.hasPermission("tchat.admin.command.replacer.list") || sender.hasPermission("tchat.admin")) {
            if (replacerManager.getReplacements().isEmpty()) {
                String m = plugin.getMessagesManager().getReplacerNoReplacements();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
                return;
            }

            String m = plugin.getMessagesManager().getReplacerHeader();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));

            String or = plugin.getMessagesManager().getReplacerOriginal();
            String re = plugin.getMessagesManager().getReplacerReplace();
            String pm = plugin.getMessagesManager().getReplacerPermission();
            String pm1 = plugin.getMessagesManager().getReplacerPermissionEnd();
            for (ReplacerManager.Replacement replacement : replacerManager.getReplacements().values()) {
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, or) + replacement.getOriginal() +
                        plugin.getTranslateColors().translateColors(null, re) + replacement.getReplace() +
                        plugin.getTranslateColors().translateColors(null, pm) + replacement.getPermission() + plugin.getTranslateColors().translateColors(null, pm1));
            }
        } else {
            String m = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
        }
    }

    private void handleAddCommand(CommandSender sender, String @NotNull [] args) {
        if (args.length < 4) {
            String m = plugin.getMessagesManager().getUsageReplacerAdd();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return;
        }

        if (!(sender instanceof Player) || sender.hasPermission("tchat.admin.command.replacer.add") || sender.hasPermission("tchat.admin")) {
            String original = args[1];
            String replace = args[2];
            String permission = args[3];

            replacerManager.addReplacement(original, replace, permission);
            String m = plugin.getMessagesManager().getReplacerAdded();
            m = m.replace("%original%", original).replace("%replacement%", replace);
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
        } else {
            String m = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
        }
    }

    private void handleRemoveCommand(CommandSender sender, String @NotNull [] args) {
        if (args.length < 2) {
            String m = plugin.getMessagesManager().getUsageReplacerRemove();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return;
        }

        if (!(sender instanceof Player) || sender.hasPermission("tchat.admin.command.replacer.remove") || sender.hasPermission("tchat.admin")) {
            String original = args[1];
            replacerManager.removeReplacement(original);
            String m = plugin.getMessagesManager().getReplacerRemoved();
            m = m.replace("%original%", original);
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
        } else {
            String m = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
        }
    }
}

package commands;

import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RulesCommand implements CommandExecutor {
    private final TChat plugin;

    public RulesCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (sender.hasPermission("tchat.rules") || sender.hasPermission("tchat.admin")) {
            String prefix;
            if (plugin.getConfigManager().isRulesPrefixEnabled()) {
                prefix = plugin.getMessagesManager().getPrefix();
            } else {
                prefix = "";
            }

            if (sender instanceof Player player) {
                for (String message : plugin.getConfigManager().getRulesMessage()) {
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
                }
            } else {
                for (String message : plugin.getConfigManager().getRulesMessage()) {
                    sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                }
            }
        } else {
           String prefix = plugin.getMessagesManager().getPrefix();
           String message = plugin.getMessagesManager().getNoPermission();
           sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
        }

        return true;
    }
}

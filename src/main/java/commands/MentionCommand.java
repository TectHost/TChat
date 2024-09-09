package commands;

import config.MentionsManager;
import minealex.tchat.TChat;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MentionCommand implements CommandExecutor {

    private final TChat plugin;

    public MentionCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!sender.hasPermission("tchat.command.mention") && !sender.hasPermission("tchat.admin")) {
            String m = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
        }

        if (args.length != 1) {
            String m = plugin.getMessagesManager().getUsageMention();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || !target.isOnline()) {
            String playerNotFoundMessage = plugin.getMessagesManager().getPlayerNotFound();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + playerNotFoundMessage));
            return true;
        }

        executeActions(target);

        String m = plugin.getMessagesManager().getMentionOther();
        m = m.replace("%mentioned%", target.getName());
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));

        return true;
    }

    public void executeActions(Player player) {
        String groupName = plugin.getGroupManager().getGroup(player);
        MentionsManager.EventConfig personalConfig = plugin.getMentionsManager().getPersonalEventConfig(groupName);

        if (personalConfig.isMessageEnabled()) {
            player.sendMessage(plugin.getTranslateColors().translateColors(player, String.join("\n", personalConfig.getMessage())));
        }

        if (personalConfig.isTitleEnabled() || personalConfig.isSubtitleEnabled()) {
            String title = personalConfig.isTitleEnabled()
                    ? plugin.getTranslateColors().translateColors(player, personalConfig.getTitle())
                    : "";
            String subtitle = personalConfig.isSubtitleEnabled()
                    ? plugin.getTranslateColors().translateColors(player, personalConfig.getSubtitle())
                    : "";

            player.sendTitle(title, subtitle, 10, 70, 20);
        }

        if (personalConfig.isSoundEnabled()) {
            String sound = personalConfig.getSound();
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }

        if (personalConfig.isParticlesEnabled()) {
            String particle = personalConfig.getParticle();
            player.getWorld().spawnParticle(org.bukkit.Particle.valueOf(particle.toUpperCase()), player.getLocation(), 10);
        }

        if (personalConfig.isActionbarEnabled()) {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, new TextComponent(plugin.getTranslateColors().translateColors(player, personalConfig.getActionbarMessage())));
        }
    }
}

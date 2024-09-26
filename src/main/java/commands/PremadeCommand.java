package commands;

import config.BroadcastPremadeManager;
import config.BroadcastPremadeManager.Broadcast;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PremadeCommand implements CommandExecutor {

    private final TChat plugin;
    private final BroadcastPremadeManager broadcastManager;

    public PremadeCommand(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.broadcastManager = plugin.getBroadcastPremadeManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        String p = plugin.getMessagesManager().getPrefix();
        if (args.length < 2 || !args[0].equalsIgnoreCase("broadcast")) {
            String m = plugin.getMessagesManager().getUsagePremade();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            return false;
        }

        String broadcastName = args[1];
        Broadcast broadcast = broadcastManager.getBroadcasts().get(broadcastName);

        if (broadcast == null) {
            String m = plugin.getMessagesManager().getBroadcastPremadeNull();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            return false;
        }

        String[] broadcastArgs = new String[args.length - 2];
        System.arraycopy(args, 2, broadcastArgs, 0, args.length - 2);
        String joinedArgs = String.join(" ", broadcastArgs);

        org.bukkit.Particle particle = org.bukkit.Particle.valueOf(broadcast.getParticleType().toUpperCase());
        String sound = broadcast.getSound();
        double volume = broadcast.getVolume();
        double pitch = broadcast.getPitch();
        String title = broadcast.getTitle();
        String subtitle = broadcast.getSubtitle();
        String actionbar = broadcast.getActionBar();
        int particles = broadcast.getParticleAmount();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!broadcast.getMessage().isEmpty() && broadcast.isMessageEnabled()) {
                for (String message : broadcast.getMessage()) {
                    message = message.replace("%args%", joinedArgs);
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, message));
                }
            }

            if (broadcast.getTitle() != null && broadcast.getSubtitle() != null && broadcast.isTitleEnabled()) {
                player.sendTitle(plugin.getTranslateColors().translateColors(player, title), subtitle, broadcast.getFadeIn(), broadcast.getStay(), broadcast.getFadeOut());
            }

            if (broadcast.getSound() != null && !broadcast.getSound().isEmpty() && broadcast.isSoundEnabled()) {
                player.playSound(player.getLocation(), sound, (float) volume, (float) pitch);
            }

            if (broadcast.getActionBar() != null && !broadcast.getActionBar().isEmpty() && broadcast.isActionBarEnabled()) {
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(plugin.getTranslateColors().translateColors(player, actionbar)));
            }

            if (broadcast.isParticlesEnabled()) {
                player.getWorld().spawnParticle(particle, player.getLocation(), particles);
            }
        }

        String m = plugin.getMessagesManager().getBroadcastSent();
        m = m.replace("%broadcast%", broadcastName);
        sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
        return true;
    }
}

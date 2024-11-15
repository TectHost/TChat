package blocked;

import config.BannedCommandsManager;
import minealex.tchat.TChat;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class BannedCommands implements Listener {

    private final BannedCommandsManager bannedCommandsManager;
    private final TChat plugin;

    public BannedCommands(@NotNull TChat plugin) {
        this.bannedCommandsManager = plugin.getBannedCommandsManager();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(@NotNull PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) { return; }

        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!event.getPlayer().hasPermission(bannedCommandsManager.getBypassPermissionCommand()) && !event.getPlayer().hasPermission("tchat.admin")) {
            String command = message.split(" ")[0].substring(1).toLowerCase();
            List<String> bannedCommands = bannedCommandsManager.getBannedCommands();

            if (bannedCommands.contains(command)) {
                List<String> blockedMessages = bannedCommandsManager.getBlockedMessage();
                for (String blockedMessage : blockedMessages) {
                    event.getPlayer().sendMessage(plugin.getTranslateColors().translateColors(player, blockedMessage));
                }

                if (bannedCommandsManager.getTitleEnabled()) {
                    String title = plugin.getTranslateColors().translateColors(player, bannedCommandsManager.getTitle());
                    String subtitle = plugin.getTranslateColors().translateColors(player, bannedCommandsManager.getSubTitle());
                    player.sendTitle(title, subtitle, 10, 70, 20);
                }

                if (bannedCommandsManager.getActionBarEnabled()) {
                    String actionBar = plugin.getTranslateColors().translateColors(player, bannedCommandsManager.getActionBar());
                    player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBar));
                }

                if (bannedCommandsManager.getSoundEnabled()) {
                    Sound sound = Sound.valueOf(bannedCommandsManager.getSound().toUpperCase());
                    player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
                }

                if (bannedCommandsManager.isParticlesEnabled()) {
                    player.getWorld().spawnParticle(bannedCommandsManager.getParticle(), player.getLocation(), bannedCommandsManager.getParticles());
                }

                if (plugin.getLoggerConfigManager().isLogBannedCommandsEnabled()) {
                    plugin.getLogsManager().logBannedCommand(event.getPlayer().getName(), command);
                }

                if (bannedCommandsManager.isDiscordEnabled()) {
                    plugin.getDiscordHook().sendBannedCommandEmbed(player.getName(), command, message, bannedCommandsManager.getDiscordWebhook());
                }

                event.setCancelled(true);
            }
        }
    }
}

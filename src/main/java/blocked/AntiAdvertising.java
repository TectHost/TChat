package blocked;

import config.AdvertisingConfig;
import minealex.tchat.TChat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static utils.TranslateHexColorCodes.translateHexColorCodes;

public class AntiAdvertising {
    private final TChat plugin;

    public AntiAdvertising(TChat plugin) {
        this.plugin = plugin;
    }

    public void checkAdvertising(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        checkAndHandle(event, player, message, plugin.getConfigManager().getIpv4Config());
        checkAndHandle(event, player, message, plugin.getConfigManager().getDomainConfig());
        checkAndHandle(event, player, message, plugin.getConfigManager().getLinksConfig());
    }

    private void checkAndHandle(AsyncPlayerChatEvent event, Player player, String message, AdvertisingConfig config) {
        if (!player.hasPermission(plugin.getConfigManager().getAdvertisingBypass()) || !player.hasPermission("tchat.admin")) {
            if (config.isEnabled() && message.matches(config.getMatch())) {
                event.setCancelled(true);
                if (config.isMessageEnabled()) {
                    config.getMessage().forEach(msg -> player.sendMessage(translateAndReplace(msg, message)));
                }
                if (config.isTitleEnabled()) {
                    String title = translateAndReplace(config.getTitle(), message);
                    String subtitle = translateAndReplace(config.getSubtitle(), message);
                    player.sendTitle(title, subtitle, config.getTitleFadeIn(), config.getTitleStay(), config.getTitleFadeOut());
                }
                if (config.isActionBarEnabled()) {
                    String actionBarMessage = translateAndReplace(config.getActionBar(), message);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));
                }
                if (config.isSoundEnabled()) {
                    Sound sound = Sound.valueOf(config.getSound().toUpperCase());
                    player.playSound(player.getLocation(), sound, config.getSoundVolume(), config.getSoundPitch());
                }
                if (config.isParticlesEnabled()) {
                    Particle particle = Particle.valueOf(config.getParticleType().toUpperCase());
                    player.getWorld().spawnParticle(particle, player.getLocation(), config.getParticles());
                }
            }
        }
    }

    private String translateAndReplace(String text, String word) {
        String translatedText = translateHexColorCodes("&#", "", text);
        return ChatColor.translateAlternateColorCodes('&', translatedText.replace("{word}", word));
    }
}

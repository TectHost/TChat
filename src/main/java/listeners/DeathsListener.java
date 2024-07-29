package listeners;

import config.DeathManager;
import minealex.tchat.TChat;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathsListener implements Listener {

    private final DeathManager deathManager;
    private final TChat plugin;

    public DeathsListener(DeathManager deathManager, TChat plugin) {
        this.deathManager = deathManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        String killerName = (player.getKiller() != null)
                ? player.getKiller().getName()
                : "desconocido";

        String deathMessage;
        if (player.getLastDamageCause() != null) {
            deathMessage = switch (player.getLastDamageCause().getCause()) {
                case ENTITY_ATTACK -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-player");
                    yield String.format(deathMessage, playerName, killerName);
                }
                case ENTITY_EXPLOSION -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-explosion");
                    yield String.format(deathMessage, playerName);
                }
                case FALL -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-falling");
                    yield String.format(deathMessage, playerName);
                }
                case DROWNING -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-drowning");
                    yield String.format(deathMessage, playerName);
                }
                case LAVA -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-lava");
                    yield String.format(deathMessage, playerName);
                }
                case FIRE, FIRE_TICK -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-fire");
                    yield String.format(deathMessage, playerName);
                }
                case POISON -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-poison");
                    yield String.format(deathMessage, playerName);
                }
                case MAGIC -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-magic");
                    yield String.format(deathMessage, playerName);
                }
                case SUFFOCATION -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-suffocation");
                    yield String.format(deathMessage, playerName);
                }
                case VOID -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-void");
                    yield String.format(deathMessage, playerName);
                }
                case STARVATION -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-starvation");
                    yield String.format(deathMessage, playerName);
                }
                default -> {
                    deathMessage = deathManager.getDeathMessage("player-killed-by-environment");
                    yield String.format(deathMessage, playerName, player.getLastDamageCause().getCause());
                }
            };
        } else {
            deathMessage = deathManager.getDeathMessage("player-killed-by-environment");
            deathMessage = String.format(deathMessage, playerName, "desconocido");
        }

        event.setDeathMessage(plugin.getTranslateColors().translateColors(player, deathMessage));

        if (deathManager.isTitleEnabled()) {
            player.sendTitle(plugin.getTranslateColors().translateColors(player, deathManager.getTitle()), plugin.getTranslateColors().translateColors(player, deathManager.getSubtitle()),
                    10,
                    70,
                    20
            );
        }

        if (deathManager.isActionBarEnabled()) {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(plugin.getTranslateColors().translateColors(player, deathManager.getActionBarText())));
        }

        if (deathManager.isSoundEnabled()) {
            Sound sound = Sound.valueOf(deathManager.getSound());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }

        if (deathManager.isParticlesEnabled()) {
            Particle particle = Particle.valueOf(deathManager.getParticle());
            player.getWorld().spawnParticle(particle, player.getLocation(), deathManager.getNumberOfParticles());
        }
    }
}

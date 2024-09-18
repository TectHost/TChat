package listeners;

import config.DeathManager;
import minealex.tchat.TChat;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class DeathsListener implements Listener {

    private final DeathManager deathManager;
    private final TChat plugin;

    public DeathsListener(DeathManager deathManager, TChat plugin) {
        this.deathManager = deathManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        if (!deathManager.isTitleEnabled() &&
                !deathManager.isActionBarEnabled() &&
                !deathManager.isSoundEnabled() &&
                !deathManager.isParticlesEnabled() &&
                deathManager.getDeathMessage("player-killed-by-environment") == null) {
            return;
        }

        if (plugin.getDiscordManager().isDiscordEnabled() && plugin.getDiscordManager().isDeathEnabled()) {
            plugin.getDiscordHook().sendDeathMessage(playerName);
        }

        String killerName = (player.getKiller() != null) ? player.getKiller().getName() : "unknown";
        String causeMessage = null;

        if (player.getLastDamageCause() != null) {
            DamageCause damageCause = player.getLastDamageCause().getCause();

            causeMessage = switch (damageCause) {
                case ENTITY_ATTACK -> {
                    if (player.getKiller() != null) {
                        yield deathManager.getDeathMessage("player-killed-by-player");
                    } else {
                        yield deathManager.getDeathMessage("player-killed-by-entity");
                    }
                }
                case ENTITY_EXPLOSION -> deathManager.getDeathMessage("player-killed-by-explosion");
                case FALL -> deathManager.getDeathMessage("player-killed-by-falling");
                case DROWNING -> deathManager.getDeathMessage("player-killed-by-drowning");
                case LAVA -> deathManager.getDeathMessage("player-killed-by-lava");
                case FIRE, FIRE_TICK -> deathManager.getDeathMessage("player-killed-by-fire");
                case POISON -> deathManager.getDeathMessage("player-killed-by-poison");
                case MAGIC -> deathManager.getDeathMessage("player-killed-by-magic");
                case SUFFOCATION -> deathManager.getDeathMessage("player-killed-by-suffocation");
                case VOID -> deathManager.getDeathMessage("player-killed-by-void");
                case STARVATION -> deathManager.getDeathMessage("player-killed-by-starvation");
                case WITHER -> deathManager.getDeathMessage("player-killed-by-wither");
                case THORNS -> deathManager.getDeathMessage("player-killed-by-thorns");
                case FALLING_BLOCK -> {
                    if (player.getLastDamageCause().getEntity() instanceof FallingBlock fallingBlock) {
                        if (fallingBlock.getBlockData().getMaterial() == Material.ANVIL) {
                            yield deathManager.getDeathMessage("player-killed-by-anvil");
                        }
                    }
                    yield deathManager.getDeathMessage("player-killed-by-falling-block");
                }
                default -> deathManager.getDeathMessage("player-killed-by-environment")
                        .replace("%cause%", damageCause.toString());
            };
        }

        if (causeMessage == null) {
            causeMessage = deathManager.getDeathMessage("player-killed-by-environment");
        }

        if (causeMessage != null) {
            if (plugin.getConfigManager().isDeathLogs()) {
                plugin.getLogsManager().logDeaths(playerName);
            }

            String deathMessage = causeMessage.replace("%player%", playerName)
                    .replace("%killer%", killerName);

            event.setDeathMessage(plugin.getTranslateColors().translateColors(player, deathMessage));

            if (deathManager.isTitleEnabled()) {
                String title = deathManager.getTitle() != null ? deathManager.getTitle().replace("%player%", playerName) : "";
                String subtitle = deathManager.getSubtitle() != null ? deathManager.getSubtitle().replace("%player%", playerName) : "";
                player.sendTitle(plugin.getTranslateColors().translateColors(player, title),
                        plugin.getTranslateColors().translateColors(player, subtitle),
                        10, 70, 20);
            }

            if (deathManager.isActionBarEnabled()) {
                String actionBarText = deathManager.getActionBarText() != null ? deathManager.getActionBarText().replace("%player%", playerName) : "";
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(
                                plugin.getTranslateColors().translateColors(player, actionBarText)));
            }

            if (deathManager.isSoundEnabled()) {
                try {
                    Sound sound = Sound.valueOf(deathManager.getSound());
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Sonido configurado no válido: " + deathManager.getSound());
                }
            }

            if (deathManager.isParticlesEnabled()) {
                try {
                    Particle particle = Particle.valueOf(deathManager.getParticle());
                    player.getWorld().spawnParticle(particle, player.getLocation(), deathManager.getNumberOfParticles());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Partícula configurada no válida: " + deathManager.getParticle());
                }
            }
        }
    }
}

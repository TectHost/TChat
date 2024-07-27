package listeners;

import config.DeathManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathsListener implements Listener {

    private final DeathManager deathManager;

    public DeathsListener(DeathManager deathManager) {
        this.deathManager = deathManager;
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

        event.setDeathMessage(deathMessage);
    }
}

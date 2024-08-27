package listeners;

import config.LevelsManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LevelListener implements Listener {

    private final Random random = new Random();
    private final TChat plugin;

    public LevelListener(@NotNull TChat plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void addXp(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) { return; }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        LevelsManager levelsManager = plugin.getLevelsManager();

        LevelsManager.Multiplier multiplier = levelsManager.getMultiplier(player);
        double xpMultiplier = multiplier.getXpMultiplier();

        int minXp = levelsManager.getMinXp();
        int maxXp = levelsManager.getMaxXp();
        int randomNumber = minXp + random.nextInt(maxXp - minXp + 1);

        int adjustedXp = (int) (randomNumber * xpMultiplier);

        Bukkit.getScheduler().runTask(plugin, () -> {
            int currentXp = plugin.getSaveManager().getXp(playerId);
            int totalXp = currentXp + adjustedXp;

            handleLevelUp(player, totalXp);
        });
    }

    private void handleLevelUp(@NotNull Player player, int totalXp) {
        UUID playerId = player.getUniqueId();
        LevelsManager levelsManager = plugin.getLevelsManager();

        int currentLevel = plugin.getSaveManager().getLevel(playerId);

        for (int levelId = currentLevel + 1; levelsManager.getLevels().containsKey(levelId); levelId++) {
            LevelsManager.Level level = levelsManager.getLevel(levelId);

            int requiredXp = level.getXp();

            if (totalXp >= requiredXp) {
                plugin.getSaveManager().setLevel(playerId, levelId);
                totalXp -= requiredXp;

                applyRewards(player, level.getRewards());
                String prefix = plugin.getMessagesManager().getPrefix();
                String message = plugin.getMessagesManager().getLevelUp();
                message = message.replace("%level%", String.valueOf(levelId));
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            } else {
                break;
            }
        }

        plugin.getSaveManager().setXp(playerId, totalXp);
    }

    private void applyRewards(Player player, List<String> rewards) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (String reward : rewards) {
                    String command = reward.replace("%player%", player.getName());
                    Bukkit.dispatchCommand(console, command);
                }
            }
        }.runTask(plugin);
    }
}

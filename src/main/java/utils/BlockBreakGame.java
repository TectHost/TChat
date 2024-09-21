package utils;

import minealex.tchat.TChat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlockBreakGame implements Listener {

    private final TChat plugin;
    private final Material blockType;
    private final int requiredAmount;
    private final Map<Player, Integer> playerBreakCounts;

    public BlockBreakGame(@NotNull TChat plugin, Material blockType, int requiredAmount) {
        this.plugin = plugin;
        this.blockType = blockType;
        this.requiredAmount = requiredAmount;
        this.playerBreakCounts = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getChatGamesSender().isGameActive()) { return; }

        if (event.getBlock().getType() == blockType) {
            int currentCount = playerBreakCounts.getOrDefault(player, 0) + 1;
            playerBreakCounts.put(player, currentCount);

            if (currentCount >= requiredAmount) {
                plugin.getChatGamesSender().notifyWinner(player);
                cleanup();
            }
        }
    }

    private void cleanup() {
        BlockBreakEvent.getHandlerList().unregister(this);
    }
}
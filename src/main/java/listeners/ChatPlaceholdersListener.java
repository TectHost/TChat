package listeners;

import minealex.tchat.TChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ChatPlaceholdersListener implements Listener {

    private final TChat plugin;

    public ChatPlaceholdersListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String prefix = plugin.getMessagesManager().getPrefix();

        if (plugin.getPlaceholdersConfig().isCoordsEnabled() && message.contains(plugin.getPlaceholdersConfig().getCoordsName())) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.placeholder.coords")) {
                double x = player.getLocation().getX();
                double y = player.getLocation().getY();
                double z = player.getLocation().getZ();

                String xFormatted = String.format("%.2f", x);
                String yFormatted = String.format("%.2f", y);
                String zFormatted = String.format("%.2f", z);

                String coordsMessage = plugin.getTranslateColors().translateColors(player, plugin.getPlaceholdersConfig().getCoordsFormat());

                coordsMessage = coordsMessage.replace("%x%", xFormatted);
                coordsMessage = coordsMessage.replace("%y%", yFormatted);
                coordsMessage = coordsMessage.replace("%z%", zFormatted);

                message = message.replace(plugin.getPlaceholdersConfig().getCoordsName(), coordsMessage);
            } else {
                String error = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
            }
        }

        if (plugin.getPlaceholdersConfig().isItemEnabled() && message.contains(plugin.getPlaceholdersConfig().getItemName())) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.placeholder.item")) {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                String itemName = "None";

                if (itemInHand.getType() != org.bukkit.Material.AIR) {
                    ItemMeta itemMeta = itemInHand.getItemMeta();
                    if (itemMeta != null && itemMeta.hasDisplayName()) {
                        itemName = itemMeta.getDisplayName();
                    } else {
                        itemName = itemInHand.getType().toString().replace('_', ' ').toLowerCase();
                    }
                }

                String itemMessage = plugin.getTranslateColors().translateColors(player, plugin.getPlaceholdersConfig().getItemFormat());
                itemMessage = itemMessage.replace("%item%", itemName);

                message = message.replace(plugin.getPlaceholdersConfig().getItemName(), itemMessage);
            } else {
                String error = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
            }
        }

        if (plugin.getPlaceholdersConfig().isWorldEnabled() && message.contains(plugin.getPlaceholdersConfig().getWorldName())) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.placeholder.world")) {
                String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();

                String worldMessage = plugin.getTranslateColors().translateColors(player, plugin.getPlaceholdersConfig().getWorldFormat());

                worldMessage = worldMessage.replace("%world%", worldName);

                message = message.replace(plugin.getPlaceholdersConfig().getWorldName(), worldMessage);
            } else {
                String error = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
            }
        }

        event.setMessage(message);
    }
}

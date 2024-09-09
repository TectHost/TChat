package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ShowEnderChestCommand implements CommandExecutor, Listener {

    private final TChat plugin;

    public ShowEnderChestCommand(@NotNull TChat plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();

        if (!sender.hasPermission("tchat.admin") && !sender.hasPermission("tchat.command.showec")) {
            String m = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return false;
        }

        if (sender instanceof Player player) {

            Player target = player;
            if (args.length > 0) {
                Player potentialTarget = Bukkit.getPlayer(args[0]);
                if (potentialTarget != null) {
                    target = potentialTarget;
                } else {
                    String m = plugin.getMessagesManager().getPlayerNotFound();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + m));
                    return false;
                }
            }

            inventory(player, target);
            return true;
        } else {
            String m = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + m));
            return false;
        }
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            String title = plugin.getTranslateColors().translateColors(player, plugin.getShowEnderChestConfigManager().getMenuTitle());

            if (event.getView().getTitle().startsWith(title)) {
                event.setCancelled(true);
            }
        }
    }

    public void inventory(@NotNull Player viewer, @NotNull Player target) {
        String title = plugin.getShowEnderChestConfigManager().getMenuTitle();
        title = plugin.getTranslateColors().translateColors(target, title);
        Inventory inv = Bukkit.createInventory(null, plugin.getShowEnderChestConfigManager().getSlots(), title);

        ItemStack[] items = target.getEnderChest().getContents();
        for (int i = plugin.getShowEnderChestConfigManager().getStartSlotEnder(); i < items.length; i++) {
            if (i < plugin.getShowEnderChestConfigManager().getEndSlotEnder()) {
                inv.setItem(i, items[i]);
            }
        }

        ItemStack glass = plugin.getShowEnderChestConfigManager().createGlass();
        for (int i = plugin.getShowEnderChestConfigManager().getStartSlotGlass(); i < plugin.getShowEnderChestConfigManager().getEndSlotGlass(); i++) {
            inv.setItem(i, glass);
        }

        ItemStack head = plugin.getShowEnderChestConfigManager().createHead(target);
        inv.setItem(plugin.getShowEnderChestConfigManager().getHeadSlot(), head);

        viewer.openInventory(inv);
    }
}

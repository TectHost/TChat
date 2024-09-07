package commands;

import config.InvseeConfigManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class InvseeCommand implements CommandExecutor, Listener {

    private final TChat plugin;
    private final InvseeConfigManager configManager;

    public InvseeCommand(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.configManager = new InvseeConfigManager(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String p = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String m = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            return true;
        }

        if (!player.hasPermission("tchat.admin") && !player.hasPermission("tchat.command.invsee")) {
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + plugin.getMessagesManager().getNoPermission()));
            return true;
        }

        if (args.length != 1) {
            String m = plugin.getMessagesManager().getInvseeUsage();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            String m = plugin.getMessagesManager().getPlayerNotFound();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            return true;
        }

        showInventory(target, player);
        return true;
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            String playerName = player.getName();
            String title = plugin.getTranslateColors().translateColors(player, configManager.getInventoryTitle().replace("%player%", playerName));

            if (event.getView().getTitle().startsWith(title)) {
                event.setCancelled(true);
            }
        }
    }

    private void showInventory(@NotNull Player p, Player v) {
        String title = plugin.getTranslateColors().translateColors(p, configManager.getInventoryTitle().replace("%player%", p.getName()));
        Inventory tempInventory = Bukkit.createInventory(null, configManager.getSlots(), title);

        ItemStack[] items = p.getInventory().getContents();
        tempInventory.setContents(items);
        ItemStack[] armor = p.getInventory().getArmorContents();

        ItemStack glass = configManager.getGlass();
        glass = translateItemStack(glass, p);
        for (int slot : configManager.getGlassSlots()) {
            tempInventory.setItem(slot, glass);
        }

        ItemStack empty = configManager.getBarrier();
        empty = translateItemStack(empty, p);
        for (int i = 0; i < configManager.getArmorLength(); ++i) {
            ItemStack armorItem = armor[configManager.getArmorLength() - 1 - i];
            if (armorItem != null && armorItem.getType() != Material.AIR) {
                armorItem = translateItemStack(armorItem, p);
                tempInventory.setItem(configManager.getArmorStartSlot() + i, armorItem);
            } else {
                tempInventory.setItem(configManager.getArmorStartSlot() + i, empty);
            }
        }

        ItemStack offHandItem = p.getInventory().getItemInOffHand();
        offHandItem = translateItemStack(offHandItem, p);
        tempInventory.setItem(configManager.getOffhandSlot(), offHandItem.getType() != Material.AIR ? offHandItem : empty);

        v.openInventory(tempInventory);
    }

    private ItemStack translateItemStack(ItemStack itemStack, Player player) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return itemStack;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && itemMeta.hasDisplayName()) {
            String translatedName = plugin.getTranslateColors().translateColors(player, itemMeta.getDisplayName());
            itemMeta.setDisplayName(translatedName);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }
}

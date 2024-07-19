package utils;

import config.ChatColorManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ChatColorInventoryManager implements Listener {
    private final TChat plugin;

    public ChatColorInventoryManager(TChat plugin) {
        this.plugin = plugin;
    }

    public void openInventory(Player player, String title) {
        Inventory inv = Bukkit.createInventory(null, 54, plugin.getTranslateColors().translateColors(player, title));

        setBorderItems(player, inv);
        setMenuItems(inv, player);

        Bukkit.getPluginManager().registerEvents(this, plugin);
        player.openInventory(inv);
    }

    private void setBorderItems(Player player, Inventory inv) {
        setBorderItem(player, 1, inv, 0, 8, plugin.getChatColorManager().getTopMaterial(), plugin.getChatColorManager().getAmountTop(), plugin.getChatColorManager().getTopName(), plugin.getChatColorManager().getLoreTop());
        setBorderItem(player, 9, inv, 9, 36, plugin.getChatColorManager().getLeftMaterial(), plugin.getChatColorManager().getAmountLeft(), plugin.getChatColorManager().getLeftName(), plugin.getChatColorManager().getLoreLeft());
        setBorderItem(player, 9, inv, 17, 44, plugin.getChatColorManager().getRightMaterial(), plugin.getChatColorManager().getAmountRight(), plugin.getChatColorManager().getRightName(), plugin.getChatColorManager().getLoreRight());
        setBorderItem(player, 1, inv, 45, 53, plugin.getChatColorManager().getBottomMaterial(), plugin.getChatColorManager().getAmountBottom(), plugin.getChatColorManager().getBottomName(), plugin.getChatColorManager().getLoreBottom());

        setItem(player, inv, plugin.getChatColorManager().getCloseSlot(), plugin.getChatColorManager().getCloseMaterial(), plugin.getChatColorManager().getAmountClose(), plugin.getChatColorManager().getCloseName(), plugin.getChatColorManager().getLoreClose());
    }

    private void setBorderItem(Player player, int add, Inventory inv, int startSlot, int endSlot, Material material, int amount, String name, List<String> loreMessages) {
        ItemStack item = createItem(player, material, amount, name, loreMessages, "border_item");
        for (int i = startSlot; i <= endSlot; i += add) {
            inv.setItem(i, item);
        }
    }

    private void setMenuItems(Inventory inv, Player player) {
        ChatColorManager chatColorManager = plugin.getChatColorManager();

        for (Map.Entry<String, ChatColorManager.ChatColorItem> entry : chatColorManager.items.entrySet()) {
            String key = entry.getKey();
            ChatColorManager.ChatColorItem item = entry.getValue();

            Material material = Material.getMaterial(item.getMaterial());
            if (material == null) {
                String message = plugin.getMessagesManager().getMaterialNotFound();
                String prefix = plugin.getMessagesManager().getPrefix();
                Bukkit.getConsoleSender().sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message).replace("%material%", item.getMaterial()));
                continue;
            }

            int slot = item.getSlot();
            int amount = item.getAmount();

            List<String> loreMessages = player.hasPermission("tchat.chatcolor." + key)
                    ? item.getLorePerm()
                    : item.getLoreNoPerm();

            ItemStack itemStack = createItem(player, material, amount, item.getName(), loreMessages, "menu_item");
            inv.setItem(slot, itemStack);
        }
    }

    private void setItem(Player player, Inventory inv, int slot, Material material, int amount, String name, List<String> loreMessages) {
        ItemStack item = createItem(player, material, amount, name, loreMessages, "menu_item");
        inv.setItem(slot, item);
    }

    private ItemStack createItem(Player player, Material material, int amount, String name, List<String> loreMessages, String id) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getTranslateColors().translateColors(player, name));
            List<String> lore = new ArrayList<>();
            for (String msg : loreMessages) {
                lore.add(plugin.getTranslateColors().translateColors(player, msg));
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, id), PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String inventory = event.getView().getTitle().replace("§", "&");
        if (inventory.equals(plugin.getChatColorManager().getTitle())) {
            event.setCancelled(true);
        }

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String displayName = ChatColor.stripColor(meta.getDisplayName());

        String itemKey = displayName.toLowerCase().replace(" ", "_");

        ChatColorManager.ChatColorItem item = plugin.getChatColorManager().getItem(itemKey);

        if (item == null) {
            String message = plugin.getMessagesManager().getNoItemFoundMessage();
            String prefix = plugin.getMessagesManager().getPrefix();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message).replace("%item%", displayName));
            return;
        }

        String id = item.getId();

        String permission = "tchat.chatcolor." + itemKey;

        if (player.hasPermission(permission) || player.hasPermission("tchat.admin") || player.hasPermission("tchat.chatcolor.all")) {
            if (id.matches("&[0-9a-f]")) {
                plugin.getSaveManager().setChatColor(player.getUniqueId(), id);
                String message = plugin.getMessagesManager().getColorSelectedMessage().replace("%id%", id).replace("%color%", itemKey);
                String prefix = plugin.getMessagesManager().getPrefix();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            } else if (id.matches("&[k-o&r]")) {
                plugin.getSaveManager().setFormat(player.getUniqueId(), id);
                String message = plugin.getMessagesManager().getFormatSelectedMessage().replace("%id%", id).replace("%color%", itemKey);
                String prefix = plugin.getMessagesManager().getPrefix();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            } else {
                String message = plugin.getMessagesManager().getInvalidIdMessage();
                String prefix = plugin.getMessagesManager().getPrefix();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            }
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            String prefix = plugin.getMessagesManager().getPrefix();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }
    }
}

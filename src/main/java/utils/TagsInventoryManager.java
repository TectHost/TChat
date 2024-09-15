package utils;

import config.TagsMenuConfigManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TagsInventoryManager implements Listener {

    private final TagsMenuConfigManager tagsMenuConfigManager;
    private final TChat plugin;

    public TagsInventoryManager(TChat plugin, TagsMenuConfigManager tagsMenuConfigManager) {
        this.plugin = plugin;
        this.tagsMenuConfigManager = tagsMenuConfigManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMenu(Player player, String menuName) {
        TagsMenuConfigManager.MenuConfig menuConfig = tagsMenuConfigManager.getMenu(menuName);

        if (menuConfig == null) {
            player.sendMessage(plugin.getTranslateColors().translateColors(player, "&cThe menu '" + menuName + "' does not exist."));
            return;
        }

        String title = plugin.getTranslateColors().translateColors(player, menuConfig.getTitle());
        int size = menuConfig.getSize();

        Inventory menu = Bukkit.createInventory(null, size, title);

        for (Map.Entry<Integer, TagsMenuConfigManager.MenuItem> entry : menuConfig.getItems().entrySet()) {
            int slot = entry.getKey();
            TagsMenuConfigManager.MenuItem menuItem = entry.getValue();

            Material material = Material.matchMaterial(menuItem.getMaterial());
            if (material == null) {
                player.sendMessage(plugin.getTranslateColors().translateColors(player, "&7[&5TChat&7] &cMaterial invalid in slot " + slot + " for the menu: '" + menuName + "' &f(tags_menu.yml)&c."));
                continue;
            }

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String translatedName = plugin.getTranslateColors().translateColors(player, menuItem.getName());
                meta.setDisplayName(translatedName);

                List<String> lore = menuItem.getLore();
                if (lore != null) {
                    lore.replaceAll(message -> plugin.getTranslateColors().translateColors(player, message));
                    meta.setLore(lore);
                }

                item.setItemMeta(meta);
            }

            menu.setItem(slot, item);
        }

        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!tagsMenuConfigManager.isEnabled()) { return; }

        if (!(event.getWhoClicked() instanceof Player player)) { return; }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || event.getView().getTopInventory() != clickedInventory) {
            return;
        }

        String currentTitle = plugin.getTranslateColors().translateColors(player, event.getView().getTitle());
        currentTitle = currentTitle.replace("§", "&");

        TagsMenuConfigManager.MenuConfig menuConfig = tagsMenuConfigManager.getMenuByTitle(currentTitle);
        if (menuConfig == null) {
            return;
        }

        String translatedTitle = plugin.getTranslateColors().translateColors(player, menuConfig.getTitle());

        if (!event.getView().getTitle().startsWith(translatedTitle)) {
            return;
        }

        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        TagsMenuConfigManager.MenuItem menuItem = menuConfig.getItems().get(event.getSlot());
        if (menuItem != null) {

            List<String> commands = menuItem.getCommands();
            if (commands != null && !commands.isEmpty()) {
                for (String command : commands) {
                    String finalCommand = command.replace("%player%", player.getName());
                    Bukkit.dispatchCommand(player, finalCommand);
                }
            }

            String nextMenu = menuItem.getNextMenu();
            if (nextMenu != null && !nextMenu.isEmpty()) {
                openMenu(player, nextMenu);
            }
        }
    }
}

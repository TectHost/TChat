package config;

import minealex.tchat.TChat;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ShowEnderChestConfigManager {

    private final ConfigFile configFile;
    private String menuTitle;
    private int slots;
    private Material glassMaterial;
    private String glassDisplayName;
    private Material headMaterial;
    private String headDisplayName;
    private List<String> headLore;
    private int headSlot;
    private int startSlotGlass;
    private int endSlotGlass;
    private int startSlotEnder;
    private int endSlotEnder;
    private boolean enabled;

    public ShowEnderChestConfigManager(TChat plugin) {
        this.configFile = new ConfigFile("show_ender_chest.yml", "menus", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        enabled = config.getBoolean("menu.enabled", false);

        if (enabled) {
            menuTitle = config.getString("menu.title", "%player_name%'s enderchest");

            slots = config.getInt("menu.slots", 36);

            String glassMaterialName = config.getString("menu.glass.material", "GRAY_STAINED_GLASS_PANE");
            glassMaterial = Material.matchMaterial(glassMaterialName);
            glassDisplayName = config.getString("menu.glass.display_name", " ");
            startSlotGlass = config.getInt("menu.glass.slots.start", 27);
            endSlotGlass = config.getInt("menu.glass.slots.end", 36);

            String headMaterialName = config.getString("menu.head.material", "PLAYER_HEAD");
            headMaterial = Material.matchMaterial(headMaterialName);
            headSlot = config.getInt("menu.head.slot", 31);
            headDisplayName = config.getString("menu.head.display_name", "Information about %player%");
            headLore = config.getStringList("menu.head.lore");

            startSlotEnder = config.getInt("ender.slots.start", 0);
            endSlotEnder = config.getInt("ender.slots.end", 27);
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public boolean isEnabled() {return enabled;}
    public int getSlots() {return slots;}
    public int getHeadSlot() {return headSlot;}
    public int getStartSlotGlass() {return startSlotGlass;}
    public int getEndSlotGlass() {return endSlotGlass;}
    public int getStartSlotEnder() {return startSlotEnder;}
    public int getEndSlotEnder() {return endSlotEnder;}

    public ItemStack createGlass() {
        ItemStack glass = new ItemStack(glassMaterial);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(glassDisplayName);
            glass.setItemMeta(glassMeta);
        }
        return glass;
    }

    public ItemStack createHead(Player target) {
        ItemStack head = new ItemStack(headMaterial);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        if (headMeta != null) {
            String displayName = headDisplayName.replace("%player%", target.getName());
            headMeta.setDisplayName(displayName);

            headMeta.setOwningPlayer(target);

            List<String> lore = new ArrayList<>();
            for (String line : headLore) {
                line = line.replace("%player%", target.getName())
                        .replace("%uuid%", target.getUniqueId().toString())
                        .replace("%health%", String.valueOf(target.getHealth()))
                        .replace("%level%", String.valueOf(target.getLevel()));
                lore.add(line);
            }
            headMeta.setLore(lore);

            head.setItemMeta(headMeta);
        }
        return head;
    }
}

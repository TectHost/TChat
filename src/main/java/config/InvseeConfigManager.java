package config;

import minealex.tchat.TChat;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvseeConfigManager {

    private final ConfigFile configFile;

    private String inventoryTitle;
    private ItemStack barrier;
    private ItemStack glass;
    private int armorStartSlot;
    private int armorLength;
    private int offhandSlot;
    private int[] glassSlots;
    private int slots;

    public InvseeConfigManager(TChat plugin) {
        this.configFile = new ConfigFile("invsee.yml", "menus", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = configFile.getConfig();

        inventoryTitle = config.getString("options.title", "&7%player%'s inventory");
        slots = config.getInt("options.slots", 45);

        String barrierMaterialName = config.getString("empty.material", "BARRIER");
        Material barrierMaterial = Material.matchMaterial(barrierMaterialName);
        assert barrierMaterial != null;
        barrier = new ItemStack(barrierMaterial);
        ItemMeta barrierMeta = barrier.getItemMeta();
        if (barrierMeta != null) {
            barrierMeta.setDisplayName(config.getString("empty.name", "&cEmpty"));
            barrier.setItemMeta(barrierMeta);
        }

        String glassMaterialName = config.getString("glass.material", "GRAY_STAINED_GLASS_PANE");
        Material glassMaterial = Material.matchMaterial(glassMaterialName);
        assert glassMaterial != null;
        glass = new ItemStack(glassMaterial);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(config.getString("glass.name", " "));
            glass.setItemMeta(glassMeta);
        }

        armorStartSlot = config.getInt("armor-slots.start", 41);
        armorLength = config.getInt("armor-slots.length", 4);

        offhandSlot = config.getInt("offhand-slot", 36);

        glassSlots = config.getIntegerList("glass-slots").stream().mapToInt(Integer::intValue).toArray();
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public String getInventoryTitle() {return inventoryTitle;}
    public ItemStack getBarrier() {return barrier;}
    public ItemStack getGlass() {return glass;}
    public int getArmorStartSlot() {return armorStartSlot;}
    public int getArmorLength() {return armorLength;}
    public int getOffhandSlot() {return offhandSlot;}
    public int[] getGlassSlots() {return glassSlots;}
    public int getSlots() {return slots;}
}

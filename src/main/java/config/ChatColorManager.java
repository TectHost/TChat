package config;

import minealex.tchat.TChat;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatColorManager {

    private final ConfigFile messagesFile;
    private String title;
    private Material topMaterial;
    private Material leftMaterial;
    private Material rightMaterial;
    private Material bottomMaterial;
    private int amountTop;
    private int amountLeft;
    private int amountRight;
    private int amountBottom;
    private Material closeMaterial;
    private int closeSlot;
    private int amountClose;
    private String topName;
    private String leftName;
    private String rightName;
    private String bottomName;
    private String closeName;
    private List<String> loreTop;
    private List<String> loreLeft;
    private List<String> loreRight;
    private List<String> loreBottom;
    private List<String> loreClose;

    public final Map<String, ChatColorItem> items = new HashMap<>();

    private static final String MENU_TITLE = "menu.title";
    private static final String MENU_BACKGROUND_TOP_MATERIAL = "menu.background.top.material";
    private static final String MENU_BACKGROUND_LEFT_MATERIAL = "menu.background.left.material";
    private static final String MENU_BACKGROUND_RIGHT_MATERIAL = "menu.background.right.material";
    private static final String MENU_BACKGROUND_BOTTOM_MATERIAL = "menu.background.bottom.material";
    private static final String MENU_BACKGROUND_TOP_AMOUNT = "menu.background.top.amount";
    private static final String MENU_BACKGROUND_LEFT_AMOUNT = "menu.background.left.amount";
    private static final String MENU_BACKGROUND_RIGHT_AMOUNT = "menu.background.right.amount";
    private static final String MENU_BACKGROUND_BOTTOM_AMOUNT = "menu.background.bottom.amount";
    private static final String MENU_BACKGROUND_CLOSE_MATERIAL = "menu.background.close.material";
    private static final String MENU_BACKGROUND_CLOSE_AMOUNT = "menu.background.close.amount";
    private static final String MENU_BACKGROUND_CLOSE_SLOT = "menu.background.close.slot";
    private static final String MENU_BACKGROUND_TOP_NAME = "menu.background.top.name";
    private static final String MENU_BACKGROUND_LEFT_NAME = "menu.background.left.name";
    private static final String MENU_BACKGROUND_CLOSE_NAME = "menu.background.close.name";
    private static final String MENU_BACKGROUND_RIGHT_NAME = "menu.background.right.name";
    private static final String MENU_BACKGROUND_BOTTOM_NAME = "menu.background.bottom.name";
    private static final String MENU_BACKGROUND_TOP_LORE = "menu.background.top.lore";
    private static final String MENU_BACKGROUND_LEFT_LORE = "menu.background.left.lore";
    private static final String MENU_BACKGROUND_RIGHT_LORE = "menu.background.right.lore";
    private static final String MENU_BACKGROUND_BOTTOM_LORE = "menu.background.bottom.lore";
    private static final String MENU_BACKGROUND_CLOSE_LORE = "menu.background.close.lore";
    private static final String MENU_ITEMS_PREFIX = "menu.items.";

    public ChatColorManager(TChat plugin) {
        this.messagesFile = new ConfigFile("chatcolor.yml", "menus", plugin);
        this.messagesFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = messagesFile.getConfig();
        title = config.getString(MENU_TITLE);
        topMaterial = Material.valueOf(config.getString(MENU_BACKGROUND_TOP_MATERIAL));
        leftMaterial = Material.valueOf(config.getString(MENU_BACKGROUND_LEFT_MATERIAL));
        rightMaterial = Material.valueOf(config.getString(MENU_BACKGROUND_RIGHT_MATERIAL));
        bottomMaterial = Material.valueOf(config.getString(MENU_BACKGROUND_BOTTOM_MATERIAL));

        amountTop = config.getInt(MENU_BACKGROUND_TOP_AMOUNT);
        amountLeft = config.getInt(MENU_BACKGROUND_LEFT_AMOUNT);
        amountRight = config.getInt(MENU_BACKGROUND_RIGHT_AMOUNT);
        amountBottom = config.getInt(MENU_BACKGROUND_BOTTOM_AMOUNT);

        closeMaterial = Material.valueOf(config.getString(MENU_BACKGROUND_CLOSE_MATERIAL));
        amountClose = config.getInt(MENU_BACKGROUND_CLOSE_AMOUNT);
        closeSlot = config.getInt(MENU_BACKGROUND_CLOSE_SLOT);

        topName = config.getString(MENU_BACKGROUND_TOP_NAME);
        leftName = config.getString(MENU_BACKGROUND_LEFT_NAME);
        rightName = config.getString(MENU_BACKGROUND_RIGHT_NAME);
        bottomName = config.getString(MENU_BACKGROUND_BOTTOM_NAME);
        closeName = config.getString(MENU_BACKGROUND_CLOSE_NAME);

        loreTop = config.getStringList(MENU_BACKGROUND_TOP_LORE);
        loreLeft = config.getStringList(MENU_BACKGROUND_LEFT_LORE);
        loreRight = config.getStringList(MENU_BACKGROUND_RIGHT_LORE);
        loreBottom = config.getStringList(MENU_BACKGROUND_BOTTOM_LORE);
        loreClose = config.getStringList(MENU_BACKGROUND_CLOSE_LORE);

        items.clear();
        for (String key : Objects.requireNonNull(config.getConfigurationSection(MENU_ITEMS_PREFIX)).getKeys(false)) {
            String path = MENU_ITEMS_PREFIX + key + ".";
            String material = config.getString(path + "material");
            int amount = config.getInt(path + "amount");
            String name = config.getString(path + "name");
            List<String> lorePerm = config.getStringList(path + "lore-perm");
            List<String> loreNoPerm = config.getStringList(path + "lore-no-perm");
            String id = config.getString(path + "id");
            int slot = config.getInt(path + "slot");
            items.put(key, new ChatColorItem(slot, id, material, amount, name, lorePerm, loreNoPerm));
        }
    }

    public void reloadConfig() {
        messagesFile.reloadConfig();
        loadConfig();
    }

    public List<String> getLoreTop() { return loreTop; }
    public List<String> getLoreLeft() { return loreLeft; }
    public List<String> getLoreRight() { return loreRight; }
    public List<String> getLoreBottom() { return loreBottom; }
    public List<String> getLoreClose() { return loreClose; }

    public String getTopName() { return topName; }
    public String getLeftName() { return leftName; }
    public String getCloseName() { return closeName; }
    public String getRightName() { return rightName; }
    public String getBottomName() { return bottomName; }

    public String getTitle() { return title; }
    public Material getTopMaterial() { return topMaterial; }
    public Material getLeftMaterial() { return leftMaterial; }
    public Material getRightMaterial() { return rightMaterial; }
    public Material getBottomMaterial() { return bottomMaterial; }

    public int getAmountTop() { return amountTop; }
    public int getAmountLeft() { return amountLeft; }
    public int getAmountRight() { return amountRight; }
    public int getAmountBottom() { return amountBottom; }

    public int getAmountClose() { return amountClose; }
    public int getCloseSlot() { return closeSlot; }
    public Material getCloseMaterial() { return closeMaterial; }

    public ChatColorItem getItem(String key) {
        return items.get(key);
    }

    public static class ChatColorItem {
        private final int slot;
        private final String id;
        private final String material;
        private final int amount;
        private final String name;
        private final List<String> lorePerm;
        private final List<String> loreNoPerm;

        public ChatColorItem(int slot, String id, String material, int amount, String name, List<String> lorePerm, List<String> loreNoPerm) {
            this.id = id;
            this.slot = slot;
            this.material = material;
            this.amount = amount;
            this.name = name;
            this.lorePerm = lorePerm;
            this.loreNoPerm = loreNoPerm;
        }

        public int getSlot() { return slot; }
        public String getId() { return id; }
        public String getMaterial() { return material; }
        public int getAmount() { return amount; }
        public String getName() { return name; }
        public List<String> getLorePerm() { return lorePerm; }
        public List<String> getLoreNoPerm() { return loreNoPerm; }
    }
}

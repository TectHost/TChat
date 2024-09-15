package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagsMenuConfigManager {

    private final ConfigFile configFile;
    private final Map<String, MenuConfig> menus = new HashMap<>();
    private boolean enabled;
    private String dmenu;

    public TagsMenuConfigManager(TChat plugin) {
        this.configFile = new ConfigFile("tags_menus.yml", "menus", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        menus.clear();
        FileConfiguration config = configFile.getConfig();

        enabled = config.getBoolean("options.enabled", false);
        if (enabled) {
            dmenu = config.getString("options.default_menu", "menu1");

            ConfigurationSection menusSection = config.getConfigurationSection("menus");
            if (menusSection != null) {
                for (String menuName : menusSection.getKeys(false)) {
                    ConfigurationSection menuSection = menusSection.getConfigurationSection(menuName);

                    if (menuSection != null) {
                        String title = menuSection.getString("title", "Menu");
                        int size = menuSection.getInt("size", 27);
                        Map<Integer, MenuItem> items = new HashMap<>();

                        ConfigurationSection itemsSection = menuSection.getConfigurationSection("items");
                        if (itemsSection != null) {
                            for (String slotKey : itemsSection.getKeys(false)) {
                                int slot = Integer.parseInt(slotKey);
                                ConfigurationSection itemSection = itemsSection.getConfigurationSection(slotKey);

                                if (itemSection != null) {
                                    String material = itemSection.getString("material", "STONE");
                                    String name = itemSection.getString("name", "");
                                    List<String> lore = itemSection.getStringList("lore");
                                    List<String> commands = itemSection.getStringList("commands");
                                    String nextMenu = itemSection.getString("open_menu", null);

                                    MenuItem menuItem = new MenuItem(material, name, lore, commands, nextMenu);
                                    items.put(slot, menuItem);
                                }
                            }
                        }

                        MenuConfig menuConfig = new MenuConfig(title, size, items);
                        menus.put(menuName, menuConfig);
                    }
                }
            }
        }
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isEnabled() {return enabled;}
    public String getDmenu() {return dmenu;}

    @Nullable
    public MenuConfig getMenu(String name) {
        return menus.get(name);
    }

    @Nullable
    public MenuConfig getMenuByTitle(String title) {
        for (MenuConfig menuConfig : menus.values()) {
            if (menuConfig.getTitle().equalsIgnoreCase(title)) {
                return menuConfig;
            }
        }
        return null;
    }

    public static class MenuConfig {
        private final String title;
        private final int size;
        private final Map<Integer, MenuItem> items;

        public MenuConfig(String title, int size, Map<Integer, MenuItem> items) {
            this.title = title;
            this.size = size;
            this.items = items;
        }

        public String getTitle() {
            return title;
        }

        public int getSize() {
            return size;
        }

        public Map<Integer, MenuItem> getItems() {
            return items;
        }
    }

    public static class MenuItem {
        private final String material;
        private final String name;
        private final List<String> lore;
        private final List<String> commands;
        private final String nextMenu;

        public MenuItem(String material, String name, List<String> lore, List<String> commands, String nextMenu) {
            this.material = material;
            this.name = name;
            this.lore = lore;
            this.commands = commands;
            this.nextMenu = nextMenu;
        }

        public String getMaterial() {
            return material;
        }

        public String getName() {
            return name;
        }

        public List<String> getLore() {
            return lore;
        }

        public List<String> getCommands() {
            return commands;
        }

        public String getNextMenu() {
            return nextMenu;
        }
    }
}

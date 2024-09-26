package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GroupManager {

    private final TChat plugin;
    private final ConfigFile groupsFile;
    private Map<String, Group> groups;

    public GroupManager(TChat plugin) {
        this.plugin = plugin;
        this.groupsFile = new ConfigFile("groups.yml", null, plugin);
        this.groupsFile.registerConfig();
        loadGroups();
    }

    public void loadGroups() {
        groups = new HashMap<>();
        FileConfiguration config = groupsFile.getConfig();
        Set<String> groupKeys = Objects.requireNonNull(config.getConfigurationSection("groups")).getKeys(false);

        for (String key : groupKeys) {
            String permission = config.getString("groups." + key + ".permission", "");
            String prefix = config.getString("groups." + key + ".prefix", "");
            String suffix = config.getString("groups." + key + ".suffix", "");
            boolean formatEnabled = config.getBoolean("groups." + key + ".format-enabled", false);
            String format = config.getString("groups." + key + ".format", "");
            int priority = config.getInt("groups." + key + ".priority", Integer.MAX_VALUE);
            HoverClickAction playerHoverClick = getHoverClickAction(config, "groups." + key + ".hover.player");
            HoverClickAction messageHoverClick = getHoverClickAction(config, "groups." + key + ".hover.message");
            groups.put(key, new Group(permission, prefix, suffix, formatEnabled, format, priority, playerHoverClick, messageHoverClick));
        }
    }

    @Contract("_, _ -> new")
    private @NotNull HoverClickAction getHoverClickAction(@NotNull FileConfiguration config, String path) {
        if (config.getBoolean(path + ".enabled", false)) {
            List<String> hover = config.getStringList(path + ".hover");
            boolean clickEnabled = config.getBoolean(path + ".click.enabled", false);
            String clickAction = config.getString(path + ".click.action", "");
            return new HoverClickAction(true, hover, clickEnabled, clickAction);
        }
        return new HoverClickAction(false, null, false, null);
    }

    public void reloadGroups() {
        groupsFile.reloadConfig();
        loadGroups();
    }

    public String getGroup(@NotNull Player player) {
        FileConfiguration config = groupsFile.getConfig();
        String opGroup = config.getString("config.op-group");

        String assignedGroup = config.getString("config.default-group");
        int highestPriority = Integer.MAX_VALUE;

        for (Map.Entry<String, Group> entry : groups.entrySet()) {
            String permission = entry.getValue().getPermission();
            int priority = entry.getValue().getPriority();

            if (permission != null && !permission.isEmpty()) {
                if (player.hasPermission(permission) && priority < highestPriority) {
                    assignedGroup = entry.getKey();
                    highestPriority = priority;
                }
            }
        }

        if (player.isOp() && opGroup != null && !opGroup.equals("none") && highestPriority == Integer.MAX_VALUE) {
            plugin.getLogger().info("Player " + player.getName() + " is OP, assigning OP group: " + opGroup);
            return opGroup;
        }

        return assignedGroup;
    }

    public String getGroupFormat(String groupName) {
        Group group = groups.get(groupName);
        return group != null ? group.getFormat() : "";
    }

    public String getGroupPrefix(Player player) {
        String group = getGroup(player);
        return groups.get(group).getPrefix();
    }

    public String getGroupSuffix(Player player) {
        String group = getGroup(player);
        return groups.get(group).getSuffix();
    }

    public String getGroupName(Player player) {
        return getGroup(player);
    }

    public boolean isFormatEnabled(Player player) {
        String group = getGroup(player);
        return groups.get(group).isFormatEnabled();
    }

    public HoverClickAction getPlayerHoverClickAction(String groupName) {
        Group group = groups.get(groupName);
        return group != null ? group.getPlayerHoverClick() : new HoverClickAction(false, null, false, null);
    }

    public HoverClickAction getMessageHoverClickAction(String groupName) {
        Group group = groups.get(groupName);
        return group != null ? group.getMessageHoverClick() : new HoverClickAction(false, null, false, null);
    }

    private static class Group {
        private final String permission;
        private final String prefix;
        private final String suffix;
        private final boolean formatEnabled;
        private final String format;
        private final int priority;
        private final HoverClickAction playerHoverClick;
        private final HoverClickAction messageHoverClick;

        public Group(String permission, String prefix, String suffix, boolean formatEnabled, String format, int priority, HoverClickAction playerHoverClick, HoverClickAction messageHoverClick) {
            this.permission = permission;
            this.prefix = prefix;
            this.suffix = suffix;
            this.formatEnabled = formatEnabled;
            this.format = format;
            this.priority = priority;
            this.playerHoverClick = playerHoverClick;
            this.messageHoverClick = messageHoverClick;
        }

        public String getPermission() {
            return permission;
        }

        public String getPrefix() {
            return prefix;
        }

        public boolean isFormatEnabled() {
            return formatEnabled;
        }

        public String getSuffix() {
            return suffix;
        }

        public String getFormat() {
            return format;
        }

        public int getPriority() { // Método para obtener la prioridad
            return priority;
        }

        public HoverClickAction getPlayerHoverClick() {
            return playerHoverClick;
        }

        public HoverClickAction getMessageHoverClick() {
            return messageHoverClick;
        }
    }

    public static class HoverClickAction {
        private final boolean enabled;
        private final List<String> hoverText;
        private final boolean clickEnabled;
        private final String clickAction;

        public HoverClickAction(boolean enabled, List<String> hoverText, boolean clickEnabled, String clickAction) {
            this.enabled = enabled;
            this.hoverText = hoverText;
            this.clickEnabled = clickEnabled;
            this.clickAction = clickAction;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public List<String> getHoverText() {
            return hoverText;
        }

        public boolean isClickEnabled() {
            return clickEnabled;
        }

        public String getClickAction() {
            return clickAction;
        }
    }
}

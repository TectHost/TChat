package placeholders;

import config.GroupManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {

    private final GroupManager groupManager;

    public Placeholders(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tchat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mine_Alex";
    }

    @Override
    public @NotNull String getVersion() {
        return "4.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("prefix")) {
            return groupManager.getGroupPrefix(player);
        }

        if (identifier.equals("suffix")) {
            return groupManager.getGroupSuffix(player);
        }

        if (identifier.equals("group")) {
            return groupManager.getGroupName(player);
        }

        return null;
    }
}

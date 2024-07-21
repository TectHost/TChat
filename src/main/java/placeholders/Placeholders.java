package placeholders;

import config.GroupManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Placeholders extends PlaceholderExpansion {

    private final GroupManager groupManager;
    private final TChat plugin;

    public Placeholders(TChat plugin, GroupManager groupManager) {
        this.plugin = plugin;
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

        return switch (identifier) {
            case "prefix" -> groupManager.getGroupPrefix(player);
            case "suffix" -> groupManager.getGroupSuffix(player);
            case "group" -> groupManager.getGroupName(player);
            case "chatcolor" -> plugin.getSaveManager().getChatColor(player.getUniqueId()) + plugin.getSaveManager().getFormat(player.getUniqueId());
            case "chatcolor_color" -> plugin.getSaveManager().getChatColor(player.getUniqueId());
            case "chatcolor_format" -> plugin.getSaveManager().getFormat(player.getUniqueId());
            case "channel" -> getChannel(player);
            default -> null;
        };
    }

    private @NotNull String getChannel(Player player) {
        String channel = plugin.getChannelsManager().getPlayerChannel(player);
        return (channel != null) ? channel : "null";
    }
}

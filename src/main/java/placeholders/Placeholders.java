package placeholders;

import config.GroupManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import minealex.tchat.TChat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
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
        if (player == null) { return ""; }

        UUID playerId = player.getUniqueId();

        return switch (identifier) {
            case "prefix" -> groupManager.getGroupPrefix(player);
            case "suffix" -> groupManager.getGroupSuffix(player);
            case "group" -> groupManager.getGroupName(player);
            case "chatcolor" -> plugin.getSaveManager().getChatColor(playerId) + plugin.getSaveManager().getFormat(playerId);
            case "chatcolor_color" -> plugin.getSaveManager().getChatColor(playerId);
            case "chatcolor_format" -> plugin.getSaveManager().getFormat(playerId);
            case "channel" -> getChannel(player);
            case "xp" -> String.valueOf(plugin.getSaveManager().getXp(playerId));
            case "level" -> String.valueOf(plugin.getSaveManager().getLevel(playerId));
            case "chatgames_wins" -> String.valueOf(plugin.getSaveManager().getChatGamesWins(playerId));
            case "ping" -> plugin.getConfigManager().getColorForPing(player.getPing()) + player.getPing();
            case "ping_color" -> plugin.getConfigManager().getColorForPing(player.getPing());
            case "nick" -> plugin.getSaveManager().getNick(playerId, player);
            default -> null;
        };
    }

    private @NotNull String getChannel(Player player) {
        String channel = plugin.getChannelsManager().getPlayerChannel(player);
        return (channel != null) ? channel : "null";
    }
}

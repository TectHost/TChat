package placeholders;

import config.GroupManager;
import config.TagsManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

        if (identifier.startsWith("chatgames_wins_top_")) {
            String topNumberStr = identifier.replace("chatgames_wins_top_", "");
            try {
                int topNumber = Integer.parseInt(topNumberStr);
                return getTopChatGamesWinsPlayer(topNumber);
            } catch (NumberFormatException e) {
                return "Invalid number";
            }
        }

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
            case "ping" -> plugin.getPingConfig().getColorForPing(player.getPing()) + player.getPing();
            case "ping_color" -> plugin.getPingConfig().getColorForPing(player.getPing());
            case "nick" -> plugin.getSaveManager().getNick(playerId, player);
            case "staff_number" -> String.valueOf(getStaffNumber());
            case "staff_list" -> getStaffList();
            case "format" -> getFormat(player);
            case "group_format" -> getGroupFormat(player);
            case "tag" -> plugin.getSaveManager().getSelectedTag(playerId);
            case "tag_displayname" -> getTagDisplayName(playerId);
            case "ignore_list" -> getIgnoreList(playerId);
            default -> null;
        };
    }

    private @NotNull String getIgnoreList(UUID playerId) {
        List<String> ignoreList = plugin.getSaveManager().getIgnoreList(playerId);
        if (ignoreList.isEmpty()) {
            return "No players ignored";
        } else {
            return ignoreList.stream()
                    .map(UUID::fromString)
                    .map(uuid -> plugin.getServer().getOfflinePlayer(uuid).getName())
                    .collect(Collectors.joining(", "));
        }
    }

    private String getTagDisplayName(UUID playerId) {
        String tagName = plugin.getSaveManager().getSelectedTag(playerId);
        TagsManager.Tag tag = plugin.getTagsManager().getTags().get(tagName);

        if (tag == null) { return "No tag"; }

        return tag.getDisplay();
    }

    private @NotNull String getFormat(Player player) {
        String format = plugin.getConfigManager().getFormat().replace("¡", "");
        format = PlaceholderAPI.setPlaceholders(player, format);
        return format;
    }

    private @NotNull String getGroupFormat(Player player) {
        String channelName = plugin.getGroupManager().getGroupName(player);
        String groupFormat = plugin.getGroupManager().getGroupFormat(channelName).replace("¡", "");
        groupFormat = PlaceholderAPI.setPlaceholders(player, groupFormat);
        return groupFormat;
    }

    private @NotNull String getChannel(Player player) {
        String channel = plugin.getChannelsManager().getPlayerChannel(player);
        return (channel != null) ? channel : "null";
    }

    private @NotNull String getTopChatGamesWinsPlayer(int topNumber) {
        List<UUID> allPlayers = plugin.getSaveManager().getAllPlayerUUIDs();

        List<UUID> sortedPlayers = allPlayers.stream()
                .sorted((p1, p2) -> Integer.compare(plugin.getSaveManager().getChatGamesWins(p2), plugin.getSaveManager().getChatGamesWins(p1)))
                .toList();

        if (topNumber > 0 && topNumber <= sortedPlayers.size()) {
            UUID topPlayerId = sortedPlayers.get(topNumber - 1);
            return Objects.requireNonNull(Bukkit.getOfflinePlayer(topPlayerId).getName());
        } else {
            return "No data";
        }
    }

    private int getStaffNumber() {
        return (int) Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("tchat.admin") || p.hasPermission("tchat.staff") || p.isOp())
                .count();
    }

    private @NotNull String getStaffList() {
        List<String> staffList = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("tchat.admin") || p.hasPermission("tchat.staff") || p.isOp())
                .map(Player::getName)
                .toList();

        if (staffList.isEmpty()) {
            return "No staff online";
        }

        return String.join(", ", staffList);
    }
}

package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SaveManager {

    private final ConfigFile savesFile;
    private FileConfiguration config;

    public SaveManager(TChat plugin) {
        this.savesFile = new ConfigFile("saves.yml", null, plugin);
        this.savesFile.registerConfig();
        this.config = savesFile.getConfig();
        loadConfig();
    }

    public void loadConfig() {
        config = savesFile.getConfig();
    }

    public void reloadConfig() {
        savesFile.reloadConfig();
        loadConfig();
    }

    public void removeChatColor(@NotNull UUID playerId) {
        String path = "players." + playerId + ".chatcolor";
        config.set(path, null);
        savesFile.saveConfig();
    }

    public void removeFormat(@NotNull UUID playerId) {
        String path = "players." + playerId + ".format";
        config.set(path, null);
        savesFile.saveConfig();
    }

    public void saveSelectedTag(@NotNull UUID playerId, String tag) {
        String path = "players." + playerId + ".selected-tag";
        config.set(path, tag);
        savesFile.saveConfig();
    }

    public String getSelectedTag(@NotNull UUID playerId) {
        String path = "players." + playerId + ".selected-tag";
        return config.getString(path, null);
    }

    public String getChatColor(@NotNull UUID playerId) {
        String path = "players." + playerId + ".chatcolor";
        return config.getString(path, "");
    }

    public void setChatColor(@NotNull UUID playerId, String chatColor) {
        String path = "players." + playerId + ".chatcolor";
        config.set(path, chatColor);
        savesFile.saveConfig();
    }

    public void setNick(@NotNull UUID playerId, String nick) {
        String path = "players." + playerId + ".nick";
        config.set(path, nick);
        savesFile.saveConfig();
    }

    public String getNick(@NotNull UUID playerId, @NotNull Player p) {
        String path = "players." + playerId + ".nick";
        return config.getString(path, p.getName());
    }

    public int getChatGamesWins(@NotNull UUID playerId) {
        String path = "players." + playerId + ".chatgames-wins";
        return config.getInt(path, 0);
    }

    public void setChatGamesWins(@NotNull UUID playerId, int chatGamesWins) {
        String path = "players." + playerId + ".chatgames-wins";
        config.set(path, chatGamesWins);
        savesFile.saveConfig();
    }

    public List<String> getIgnoreList(@NotNull UUID playerId) {
        String path = "players." + playerId + ".ignore";
        return config.getStringList(path);
    }

    public void setIgnore(@NotNull UUID playerId, List<String> ignoreList) {
        String path = "players." + playerId + ".ignore";
        config.set(path, ignoreList);
        savesFile.saveConfig();
    }

    public void removeIgnore(@NotNull UUID playerId, @NotNull UUID targetId) {
        String path = "players." + playerId + ".ignore";
        List<String> ignoreList = config.getStringList(path);

        if (ignoreList.remove(targetId.toString())) {
            config.set(path, ignoreList);
            savesFile.saveConfig();
        }
    }

    public String getFormat(@NotNull UUID playerId) {
        String path = "players." + playerId + ".format";
        return config.getString(path, "");
    }

    public int getLevel(@NotNull UUID playerId) {
        String path = "players." + playerId + ".level";
        try {
            return config.getInt(path, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getXp(@NotNull UUID playerId) {
        String path = "players." + playerId + ".xp";
        try {
            return config.getInt(path, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setLevel(@NotNull UUID playerId, int level) {
        String path = "players." + playerId + ".level";
        config.set(path, level);
        savesFile.saveConfig();
    }

    public void setXp(@NotNull UUID playerId, int xp) {
        String path = "players." + playerId + ".xp";
        config.set(path, xp);
        savesFile.saveConfig();
    }

    public void setFormat(@NotNull UUID playerId, String format) {
        String path = "players." + playerId + ".format";
        config.set(path, format);
        savesFile.saveConfig();
    }

    public UUID getPlayerIdByNick(String nick) {
        if (config.getConfigurationSection("players") == null) { return null; }

        for (String playerKey : Objects.requireNonNull(config.getConfigurationSection("players")).getKeys(false)) {
            String storedNick = config.getString("players." + playerKey + ".nick");
            if (storedNick != null && storedNick.equalsIgnoreCase(nick)) {
                return UUID.fromString(playerKey);
            }
        }

        return null;
    }

    public List<UUID> getAllPlayerUUIDs() {
        if (config.getConfigurationSection("players") == null) {
            return List.of();
        }

        return Objects.requireNonNull(config.getConfigurationSection("players")).getKeys(false).stream()
                .map(UUID::fromString)
                .toList();
    }

    public void mutePlayer(@NotNull UUID playerId, long duration) {
        String path = "players." + playerId + ".mute";
        config.set(path + ".duration", duration);
        config.set(path + ".startTime", System.currentTimeMillis());
        savesFile.saveConfig();
    }

    public boolean isPlayerMuted(@NotNull UUID playerId) {
        String path = "players." + playerId + ".mute";
        if (!config.contains(path)) {
            return false;
        }

        long duration = config.getLong(path + ".duration", 0);
        long startTime = config.getLong(path + ".startTime", 0);

        if (duration == -1) {
            return true;
        } else if (System.currentTimeMillis() - startTime >= duration) {
            config.set(path, null);
            savesFile.saveConfig();
            return false;
        } else {
            return true;
        }
    }
}

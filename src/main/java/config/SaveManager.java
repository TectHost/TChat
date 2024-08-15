package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
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

    public void removeChatColor(UUID playerId) {
        String path = "players." + playerId.toString() + ".chatcolor";
        config.set(path, null);
        savesFile.saveConfig();
    }

    public void removeFormat(UUID playerId) {
        String path = "players." + playerId.toString() + ".format";
        config.set(path, null);
        savesFile.saveConfig();
    }

    public String getChatColor(UUID playerId) {
        String path = "players." + playerId.toString() + ".chatcolor";
        return config.getString(path, "");
    }

    public void setChatColor(UUID playerId, String chatColor) {
        String path = "players." + playerId.toString() + ".chatcolor";
        config.set(path, chatColor);
        savesFile.saveConfig();
    }

    public int getChatGamesWins(UUID playerId) {
        String path = "players." + playerId.toString() + ".chatgames-wins";
        return config.getInt(path, 0);
    }

    public void setChatGamesWins(UUID playerId, int chatGamesWins) {
        String path = "players." + playerId.toString() + ".chatgames-wins";
        config.set(path, chatGamesWins);
        savesFile.saveConfig();
    }

    public List<String> getIgnoreList(UUID playerId) {
        String path = "players." + playerId.toString() + ".ignore";
        return config.getStringList(path);
    }

    public void setIgnore(UUID playerId, List<String> ignoreList) {
        String path = "players." + playerId.toString() + ".ignore";
        config.set(path, ignoreList);
        savesFile.saveConfig();
    }

    public void removeIgnore(UUID playerId, UUID targetId) {
        String path = "players." + playerId.toString() + ".ignore";
        List<String> ignoreList = config.getStringList(path);

        if (ignoreList.remove(targetId.toString())) {
            config.set(path, ignoreList);
            savesFile.saveConfig();
        }
    }

    public String getFormat(UUID playerId) {
        String path = "players." + playerId.toString() + ".format";
        return config.getString(path, "");
    }

    public int getLevel(UUID playerId) {
        String path = "players." + playerId.toString() + ".level";
        try {
            return config.getInt(path, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getXp(UUID playerId) {
        String path = "players." + playerId.toString() + ".xp";
        try {
            return config.getInt(path, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setLevel(UUID playerId, int level) {
        String path = "players." + playerId.toString() + ".level";
        config.set(path, level);
        savesFile.saveConfig();
    }

    public void setXp(UUID playerId, int xp) {
        String path = "players." + playerId.toString() + ".xp";
        config.set(path, xp);
        savesFile.saveConfig();
    }

    public void setFormat(UUID playerId, String format) {
        String path = "players." + playerId.toString() + ".format";
        config.set(path, format);
        savesFile.saveConfig();
    }
}

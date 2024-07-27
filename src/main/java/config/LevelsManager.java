package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LevelsManager {

    private final ConfigFile levelsFile;
    private int minXp;
    private int maxXp;
    private final Map<Integer, Level> levels;

    public LevelsManager(TChat plugin) {
        this.levelsFile = new ConfigFile("levels.yml", null, plugin);
        this.levels = new HashMap<>();
        this.levelsFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = levelsFile.getConfig();

        minXp = config.getInt("options.min");
        maxXp = config.getInt("options.max");

        levels.clear();
        if (config.contains("levels")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("levels")).getKeys(false)) {
                int levelId = Integer.parseInt(key);
                int xp = config.getInt("levels." + key + ".xp");
                List<String> rewards = config.getStringList("levels." + key + ".rewards");

                Level level = new Level(xp, rewards);
                levels.put(levelId, level);
            }
        }
    }

    public void reloadConfig() {
        levelsFile.reloadConfig();
        loadConfig();
    }

    public int getMaxXp() { return maxXp; }
    public int getMinXp() { return minXp; }

    public Level getLevel(int levelId) {
        return levels.get(levelId);
    }

    public Map<Integer, Level> getLevels() {
        return levels;
    }

    public static class Level {
        private final int xp;
        private final List<String> rewards;

        public Level(int xp, List<String> rewards) {
            this.xp = xp;
            this.rewards = rewards;
        }

        public int getXp() {
            return xp;
        }

        public List<String> getRewards() {
            return rewards;
        }
    }
}

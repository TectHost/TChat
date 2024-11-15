package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LevelsManager {

    private final TChat plugin;
    private final ConfigFile levelsFile;

    private int minXp;
    private int maxXp;

    private final Map<Integer, Level> levels;
    private final Map<String, Multiplier> multipliers;

    public LevelsManager(TChat plugin) {
        this.plugin = plugin;
        this.levelsFile = new ConfigFile("levels.yml", "modules", plugin);
        this.levels = new HashMap<>();
        this.multipliers = new HashMap<>();
        this.levelsFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = levelsFile.getConfig();

        minXp = config.getInt("options.min");
        maxXp = config.getInt("options.max");

        multipliers.clear();
        if (config.contains("multiplier")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("multiplier")).getKeys(false)) {
                double xpMultiplier = config.getDouble("multiplier." + key + ".xp", 1.0);

                Multiplier multiplier = new Multiplier(xpMultiplier);
                multipliers.put(key, multiplier);
            }
        }

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

    public Multiplier getMultiplier(Player player) {
        String playerGroup = plugin.getGroupManager().getGroup(player);
        return multipliers.getOrDefault(playerGroup, new Multiplier(1.0));
    }

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

    public static class Multiplier {
        private final double xpMultiplier;

        public Multiplier(double xpMultiplier) {
            this.xpMultiplier = xpMultiplier;
        }

        public double getXpMultiplier() {
            return xpMultiplier;
        }
    }
}

package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatGamesManager {
    private final List<Game> games = new ArrayList<>();
    private final ConfigFile configFile;
    private final TChat plugin;

    public ChatGamesManager(TChat plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("chatgames.yml", null, plugin);
        this.configFile.registerConfig();
        loadGames();
    }

    private void loadGames() {
        FileConfiguration config = configFile.getConfig();
        if (config.contains("games")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("games")).getKeys(false)) {
                boolean enabled = config.getBoolean("games." + key + ".enabled", false);
                if (enabled) {
                    List<String> messages = config.getStringList("games." + key + ".messages");
                    List<String> keywords = config.getStringList("games." + key + ".keywords");
                    List<String> rewards = config.getStringList("games." + key + ".rewards");
                    int endTime = config.getInt("games." + key + ".options.endTime");
                    int time = config.getInt("games." + key + ".options.time");

                    Game game = new Game(messages, keywords, rewards, new Options(endTime, time), enabled);
                    games.add(game);
                }
            }
        } else {
            plugin.getLogger().warning("No games found in the chatgames.yml file.");
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadGames();
    }

    public List<Game> getGames() {
        return games;
    }

    public static class Game {
        private final List<String> messages;
        private final List<String> keywords;
        private final List<String> rewards;
        private final Options options;
        private final boolean enabled;

        public Game(List<String> messages, List<String> keywords, List<String> rewards, Options options, boolean enabled) {
            this.messages = messages;
            this.keywords = keywords;
            this.rewards = rewards;
            this.options = options;
            this.enabled = enabled;
        }

        public List<String> getMessages() {
            return messages;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public List<String> getRewards() {
            return rewards;
        }

        public Options getOptions() {
            return options;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public static class Options {
        private final int endTime;
        private final int time;

        public Options(int endTime, int time) {
            this.endTime = endTime;
            this.time = time;
        }

        public int getEndTime() {
            return endTime;
        }

        public int getTime() {
            return time;
        }
    }
}

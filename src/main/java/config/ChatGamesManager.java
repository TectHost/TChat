package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatGamesManager {
    private final List<Game> games = new ArrayList<>();
    private final ConfigFile configFile;
    private final TChat plugin;

    public ChatGamesManager(TChat plugin) {
        this.plugin = plugin;
        this.configFile = new ConfigFile("chatgames.yml", "modules", plugin);
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
                    boolean hoverEnabled = config.getBoolean("games." + key + ".hover.enabled");
                    List<String> hover = config.getStringList("games." + key + ".hover.lines");
                    boolean actionEnabled = config.getBoolean("games." + key + ".action.enabled");
                    String action = config.getString("games." + key + ".action.action");
                    List<String> keywords = config.getStringList("games." + key + ".keywords");
                    List<String> rewards = config.getStringList("games." + key + ".rewards");
                    int endTime = config.getInt("games." + key + ".options.endTime");
                    int time = config.getInt("games." + key + ".options.time");

                    Effects startEffects = loadEffects(config, "games." + key + ".startEffects");
                    Effects endEffects = loadEffects(config, "games." + key + ".endEffects");
                    Effects winnerEffects = loadEffects(config, "games." + key + ".winnerEffects");

                    Game game = new Game(messages, hoverEnabled, hover, actionEnabled, action, keywords, rewards, new Options(endTime, time), enabled, startEffects, endEffects, winnerEffects);
                    games.add(game);
                }
            }
        } else {
            plugin.getLogger().warning("No games found in the chatgames.yml file.");
        }
    }

    private @NotNull Effects loadEffects(@NotNull FileConfiguration config, String path) {
        boolean titleEnabled = config.getBoolean(path + ".title.enabled", false);
        String titleText = config.getString(path + ".title.text", "");
        String subtitleText = config.getString(path + ".title.subtitle", "");
        int fadeIn = config.getInt(path + ".title.fadeIn", 10);
        int stay = config.getInt(path + ".title.stay", 70);
        int fadeOut = config.getInt(path + ".title.fadeOut", 20);

        boolean soundEnabled = config.getBoolean(path + ".sound.enabled", false);
        String soundName = config.getString(path + ".sound.name", "ENTITY_PLAYER_LEVELUP");
        float volume = (float) config.getDouble(path + ".sound.volume", 1.0);
        float pitch = (float) config.getDouble(path + ".sound.pitch", 1.0);

        boolean particleEnabled = config.getBoolean(path + ".particle.enabled", false);
        String particleName = config.getString(path + ".particle.name", "EXPLOSION_NORMAL");
        int particleCount = config.getInt(path + ".particle.count", 10);
        double speed = config.getDouble(path + ".particle.speed", 1.0);

        boolean actionBarEnabled = config.getBoolean(path + ".actionBar.enabled", false);
        String actionBarText = config.getString(path + ".actionBar.text", "");

        return new Effects(
                new Title(titleEnabled, titleText, subtitleText, fadeIn, stay, fadeOut),
                new SoundEffect(soundEnabled, soundName, volume, pitch),
                new ParticleEffect(particleEnabled, particleName, particleCount, speed),
                new ActionBar(actionBarEnabled, actionBarText)
        );
    }

    public void reloadConfig(){
        games.clear();
        configFile.reloadConfig();
        loadGames();
    }

    public List<Game> getGames() {
        return games;
    }

    public void addGame(String name, List<String> messages, List<String> keywords, List<String> rewards, Options options, Effects startEffects, Effects endEffects, Effects winnerEffects) {
        Game newGame = new Game(messages, false, new ArrayList<>(), false, "", keywords, rewards, options, true, startEffects, endEffects, winnerEffects);
        games.add(newGame);

        FileConfiguration config = configFile.getConfig();
        String path = "games." + name;
        config.set(path + ".enabled", true);
        config.set(path + ".messages", messages);
        config.set(path + ".keywords", keywords);
        config.set(path + ".rewards", rewards);
        config.set(path + ".options.endTime", options.getEndTime());
        config.set(path + ".options.time", options.getTime());

        saveEffects(config, path + ".startEffects", startEffects);
        saveEffects(config, path + ".endEffects", endEffects);
        saveEffects(config, path + ".winnerEffects", winnerEffects);

        configFile.saveConfig();
    }

    public void removeGame(String name) {
        games.removeIf(game -> game.getKeywords().contains(name));

        FileConfiguration config = configFile.getConfig();
        config.set("games." + name, null);
        configFile.saveConfig();
    }

    private void saveEffects(@NotNull FileConfiguration config, String path, @NotNull Effects effects) {
        config.set(path + ".title.enabled", effects.getTitle().isEnabled());
        config.set(path + ".title.text", effects.getTitle().getText());
        config.set(path + ".title.subtitle", effects.getTitle().getSubtitle());
        config.set(path + ".title.fadeIn", effects.getTitle().getFadeIn());
        config.set(path + ".title.stay", effects.getTitle().getStay());
        config.set(path + ".title.fadeOut", effects.getTitle().getFadeOut());

        config.set(path + ".sound.enabled", effects.getSound().isEnabled());
        config.set(path + ".sound.name", effects.getSound().getName());
        config.set(path + ".sound.volume", effects.getSound().getVolume());
        config.set(path + ".sound.pitch", effects.getSound().getPitch());

        config.set(path + ".particle.enabled", effects.getParticle().isEnabled());
        config.set(path + ".particle.name", effects.getParticle().getName());
        config.set(path + ".particle.count", effects.getParticle().getCount());
        config.set(path + ".particle.speed", effects.getParticle().getSpeed());

        config.set(path + ".actionBar.enabled", effects.getActionBar().isEnabled());
        config.set(path + ".actionBar.text", effects.getActionBar().getText());
    }

    public static class Game {
        private final List<String> messages;
        private final boolean hoverEnabled;
        private final List<String> hover;
        private final boolean actionEnabled;
        private final String action;
        private final List<String> keywords;
        private final List<String> rewards;
        private final Options options;
        private final boolean enabled;
        private final Effects startEffects;
        private final Effects endEffects;
        private final Effects winnerEffects;

        public Game(List<String> messages, boolean hoverEnabled, List<String> hover, boolean actionEnabled, String action, List<String> keywords, List<String> rewards, Options options, boolean enabled, Effects startEffects, Effects endEffects, Effects winnerEffects) {
            this.messages = messages;
            this.hoverEnabled = hoverEnabled;
            this.hover = hover;
            this.actionEnabled = actionEnabled;
            this.action = action;
            this.keywords = keywords;
            this.rewards = rewards;
            this.options = options;
            this.enabled = enabled;
            this.startEffects = startEffects;
            this.endEffects = endEffects;
            this.winnerEffects = winnerEffects;
        }

        public List<String> getMessages() {
            return messages;
        }

        public boolean isHoverEnabled() {
            return hoverEnabled;
        }

        public List<String> getHover() {
            return hover;
        }

        public boolean isActionEnabled() {
            return actionEnabled;
        }

        public String getAction() {
            return action;
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

        public Effects getStartEffects() {
            return startEffects;
        }

        public Effects getEndEffects() {
            return endEffects;
        }

        public Effects getWinnerEffects() {
            return winnerEffects;
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

    public static class Effects {
        private final Title title;
        private final SoundEffect sound;
        private final ParticleEffect particle;
        private final ActionBar actionBar;

        public Effects(Title title, SoundEffect sound, ParticleEffect particle, ActionBar actionBar) {
            this.title = title;
            this.sound = sound;
            this.particle = particle;
            this.actionBar = actionBar;
        }

        public Title getTitle() {
            return title;
        }

        public SoundEffect getSound() {
            return sound;
        }

        public ParticleEffect getParticle() {
            return particle;
        }

        public ActionBar getActionBar() {
            return actionBar;
        }
    }

    public static class Title {
        private final boolean enabled;
        private final String text;
        private final String subtitle;
        private final int fadeIn;
        private final int stay;
        private final int fadeOut;

        public Title(boolean enabled, String text, String subtitle, int fadeIn, int stay, int fadeOut) {
            this.enabled = enabled;
            this.text = text;
            this.subtitle = subtitle;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getText() {
            return text;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public int getFadeIn() {
            return fadeIn;
        }

        public int getStay() {
            return stay;
        }

        public int getFadeOut() {
            return fadeOut;
        }
    }

    public static class SoundEffect {
        private final boolean enabled;
        private final String name;
        private final float volume;
        private final float pitch;

        public SoundEffect(boolean enabled, String name, float volume, float pitch) {
            this.enabled = enabled;
            this.name = name;
            this.volume = volume;
            this.pitch = pitch;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getName() {
            return name;
        }

        public float getVolume() {
            return volume;
        }

        public float getPitch() {
            return pitch;
        }
    }

    public static class ParticleEffect {
        private final boolean enabled;
        private final String name;
        private final int count;
        private final double speed;

        public ParticleEffect(boolean enabled, String name, int count, double speed) {
            this.enabled = enabled;
            this.name = name;
            this.count = count;
            this.speed = speed;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        public double getSpeed() {
            return speed;
        }
    }

    public static class ActionBar {
        private final boolean enabled;
        private final String text;

        public ActionBar(boolean enabled, String text) {
            this.enabled = enabled;
            this.text = text;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getText() {
            return text;
        }
    }
}

package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class GrammarManager {

    private final ConfigFile grammarFile;

    private boolean grammarCapEnabled;
    private int grammarCapLetters;
    private boolean grammarDotEnabled;
    private String grammarDotCharacter;
    private int grammarMinCharactersCap;
    private int grammarMinCharactersDot;

    private boolean repeatMessagesEnabled;
    private double repeatMessagesPercent;
    private String bypassRepeatMessages;
    private int maxRepeatMessages;

    public GrammarManager(TChat plugin){
        this.grammarFile = new ConfigFile("grammar.yml", "modules", plugin);
        this.grammarFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = grammarFile.getConfig();

        grammarCapEnabled = config.getBoolean("grammar.cap.enabled");
        grammarDotEnabled = config.getBoolean("grammar.final-dot.enabled");
        repeatMessagesEnabled = config.getBoolean("grammar.repeat-messages.enabled");
        if (grammarDotEnabled) {
            grammarMinCharactersDot = config.getInt("grammar.final-dot.min-characters");
            grammarDotCharacter = config.getString("grammar.final-dot.character");
        }
        if (grammarCapEnabled) {
            grammarMinCharactersCap = config.getInt("grammar.cap.min-characters");
            grammarCapLetters = config.getInt("grammar.cap.letters");
        }
        if (repeatMessagesEnabled) {
            repeatMessagesPercent = config.getDouble("grammar.repeat-messages.percent");
            bypassRepeatMessages = config.getString("grammar.repeat-messages.bypass-permission");
            maxRepeatMessages = config.getInt("grammar.repeat-messages.max-repeat-messages", 1);
        }
    }

    public void reloadConfig(){
        grammarFile.reloadConfig();
        loadConfig();
    }

    public int getGrammarMinCharactersCap() { return grammarMinCharactersCap; }
    public int getGrammarMinCharactersDot() { return grammarMinCharactersDot; }
    public String getGrammarDotCharacter() { return grammarDotCharacter; }
    public boolean isGrammarDotEnabled() { return grammarDotEnabled; }
    public boolean isGrammarCapEnabled() { return grammarCapEnabled; }
    public int getGrammarCapLetters() { return grammarCapLetters; }
    public int getMaxRepeatMessages() {return maxRepeatMessages;}
    public String getBypassRepeatMessages() { return bypassRepeatMessages; }
    public double getRepeatMessagesPercent() { return repeatMessagesPercent; }
    public boolean isRepeatMessagesEnabled() { return repeatMessagesEnabled; }

}

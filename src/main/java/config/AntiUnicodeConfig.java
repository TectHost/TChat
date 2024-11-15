package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

public class AntiUnicodeConfig {

    private final ConfigFile configFile;

    private boolean unicodeBlockAll;
    private String unicodeMatch;
    private int unicodeMode;
    private String unicodeCensor;

    public AntiUnicodeConfig(TChat plugin){
        this.configFile = new ConfigFile("anti_unicode.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        unicodeBlockAll = config.getBoolean("unicode.blockAllNonLatin");
        unicodeMatch = config.getString("unicode.match");
        unicodeMode = config.getInt("unicode.mode", 1);
        if (unicodeMode == 2) {
            unicodeCensor = config.getString("unicode.censor-char", "*");
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public boolean isUnicodeBlockAll() { return unicodeBlockAll; }
    public String getUnicodeMatch() { return unicodeMatch; }
    public int getUnicodeMode() {return unicodeMode;}
    public String getUnicodeCensor() {return unicodeCensor;}
}

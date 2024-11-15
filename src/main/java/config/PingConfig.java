package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PingConfig {

    private final ConfigFile configFile;

    private Map<Integer, String> pingColors;

    public PingConfig(TChat plugin){
        this.configFile = new ConfigFile("ping.yml", "modules", plugin);
        this.configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = configFile.getConfig();

        pingColors = new HashMap<>();

        List<Map<?, ?>> colorConfigs = config.getMapList("ping.colors");
        for (Map<?, ?> colorConfig : colorConfigs) {
            String range = (String) colorConfig.get("range");
            String colorCode = (String) colorConfig.get("color");

            if (range == null || colorCode == null) {
                throw new IllegalArgumentException("[Ping] Error in config: " + colorConfig);
            }

            String[] parts = range.split("-");
            if (parts.length < 1 || parts.length > 2) {
                throw new IllegalArgumentException("[Ping]: '" + range + "' does not have min-max format: '" + range + "'");
            }

            int min;
            int max;

            try {
                min = Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[Ping]: '" + parts[0] + "' is not valid (0)", e);
            }

            try {
                max = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[Ping]: '" + parts[1] + "' is not valid (1)", e);
            }

            for (int i = min; i <= max; i++) {
                pingColors.put(i, colorCode);
            }
        }
    }

    public void reloadConfig(){
        configFile.reloadConfig();
        loadConfig();
    }

    public String getColorForPing(int ping) {
        return pingColors.entrySet().stream()
                .filter(entry -> ping >= entry.getKey())
                .reduce((first, second) -> second)
                .map(Map.Entry::getValue)
                .orElse("&#FFFFFF");
    }
}

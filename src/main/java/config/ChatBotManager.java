package config;

import minealex.tchat.TChat;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatBotManager {

    private final ConfigFile chatBotFile;
    private boolean enabled;
    private Map<String, List<String>> messages;

    public ChatBotManager(TChat plugin){
        this.chatBotFile = new ConfigFile("chatbot.yml", null, plugin);
        this.chatBotFile.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = chatBotFile.getConfig();
        enabled = config.getBoolean("options.enabled");

        messages = new HashMap<>();
        if (config.isConfigurationSection("chatbot")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("chatbot")).getKeys(false)) {
                List<String> messageList = config.getStringList("chatbot." + key + ".messages");
                messages.put(key, messageList);
            }
        }
    }

    public void reloadConfig(){
        chatBotFile.reloadConfig();
        loadConfig();
    }

    // Options
    public boolean isEnabled() { return enabled; }

    public List<String> getMessages(String key) {
        return messages.getOrDefault(key, List.of());
    }
}

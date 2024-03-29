package minealex.tchat.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MeCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;

    public MeCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tchat.me")) {
            sender.sendMessage(getMessages("messages.noPermission"));
            return true;
        }

        if (args.length == 0) {
        	sender.sendMessage(getMessages("messages.meUsage"));
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (String word : args) {
            message.append(word).append(" ");
        }

        // Obtener la configuración desde config.yml
        FileConfiguration config = plugin.getConfig();

        if (config.contains("Announcements.meFormat")) {
            String format = config.getString("Announcements.meFormat");

            String formattedMessage = ChatColor.translateAlternateColorCodes('&', format.replace("%s", message.toString().trim()));
            Bukkit.broadcastMessage(formattedMessage);
        } else {
            sender.sendMessage("Me format not found in config.yml.");
        }

        return true;
    }

    private String getMessages(String formatKey) {
        if (messagesConfig.contains(formatKey)) {
            return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(formatKey));
        } else {
            return "Invalid04";
        }
    }
}

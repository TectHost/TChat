package commands;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ServerCommand implements CommandExecutor {

    private final TChat plugin;

    public ServerCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!sender.hasPermission("tchat.admin.server")) {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return true;
        }

        for (String infoMessage : plugin.getMessagesManager().getServerMessage()) {
            int port = Bukkit.getServer().getPort();
            String version = Bukkit.getServer().getVersion();
            String javaVersion = System.getProperty("java.version");
            double cpu = Runtime.getRuntime().availableProcessors();
            String osN = System.getProperty("os.name");
            String osV = System.getProperty("os.version");
            String cpuFamily = System.getenv("PROCESSOR_IDENTIFIER");
            int numPlugins = Bukkit.getServer().getPluginManager().getPlugins().length;
            int numWorlds = Bukkit.getServer().getWorlds().size();

            infoMessage = infoMessage.replace("%java_version%", javaVersion);
            infoMessage = infoMessage.replace("%port%", String.valueOf(port));
            infoMessage = infoMessage.replace("%version%", version);
            infoMessage = infoMessage.replace("%os_name%", osN);
            infoMessage = infoMessage.replace("%os_version%", osV);
            infoMessage = infoMessage.replace("%cpu_cores%", String.valueOf(cpu));
            infoMessage = infoMessage.replace("%cpu_family%", cpuFamily);
            infoMessage = infoMessage.replace("%plugins%", String.valueOf(numPlugins));
            infoMessage = infoMessage.replace("%worlds%", String.valueOf(numWorlds));

            sender.sendMessage(plugin.getTranslateColors().translateColors(null, infoMessage));
        }

        return true;
    }
}
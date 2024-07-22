package config;

import minealex.tchat.TChat;
import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogsManager {

    private final File chatLogFile;
    private final File commandLogFile;

    public LogsManager(TChat plugin) {
        File pluginDataFolder = plugin.getDataFolder();
        if (!pluginDataFolder.exists()) {
            pluginDataFolder.mkdirs();
        }

        File logsFolder = new File(pluginDataFolder, "logs");
        if (!logsFolder.exists()) {
            logsFolder.mkdirs();
        }

        this.chatLogFile = new File(logsFolder, "chat.log");
        this.commandLogFile = new File(logsFolder, "commands.log");

        regenerateFiles();
    }

    private void regenerateFiles() {
        if (chatLogFile.exists()) {
            clearFile(chatLogFile);
        }

        if (commandLogFile.exists()) {
            clearFile(commandLogFile);
        }
    }

    private void clearFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("");
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to clear log file: " + e.getMessage());
        }
    }

    public void logChatMessage(String playerName, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatLogFile, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + playerName + ": " + message);
            writer.newLine();
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to log chat message: " + e.getMessage() + "(#c7dhd - LogsManager.java)");
        }
    }

    public void logCommand(String playerName, String command) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(commandLogFile, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + playerName + ": " + command);
            writer.newLine();
        } catch (IOException e) {
            Bukkit.getLogger().severe("[TChat] Failed to log command: " + e.getMessage() + "(#c7dhd - LogsManager.java)");
        }
    }
}

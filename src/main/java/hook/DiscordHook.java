package hook;

import minealex.tchat.TChat;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordHook {

    private final TChat plugin;

    public DiscordHook(TChat plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(String message, String URL) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = "{\"content\": \"" + escapeJson(message) + "\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void sendJoinMessage(String username) {
        if (plugin.getDiscordManager().isJoinEnabled()) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(plugin.getDiscordManager().getDiscordHook());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = buildEmbedJoin(username);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                    plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    public void sendLeftMessage(String username) {
        if (plugin.getDiscordManager().isQuitEnabled()) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(plugin.getDiscordManager().getDiscordHook());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = buildEmbedLeft(username);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                    plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    private @NotNull String buildEmbedLeft(String username) {
        String descriptionTemplate = plugin.getDiscordManager().getQuitDescription();
        String description = descriptionTemplate.replace("%player%", escapeJson(username));
        String avatarUrl = "https://mc-heads.net/avatar/" + username;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{").append("\"title\": \"").append(plugin.getDiscordManager().getQuitTitle()).append("\",").append("\"description\": \"").append(escapeJson(description)).append("\",").append("\"color\": ").append(plugin.getDiscordManager().getQuitColor());

        if (plugin.getDiscordManager().isQuitAvatarEnabled()) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {").append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    private @NotNull String buildEmbedJoin(String username) {
        String descriptionTemplate = plugin.getDiscordManager().getJoinDescription();
        String description = descriptionTemplate.replace("%player%", escapeJson(username));
        String avatarUrl = "https://mc-heads.net/avatar/" + username;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{").append("\"title\": \"").append(plugin.getDiscordManager().getJoinTitle()).append("\",").append("\"description\": \"").append(escapeJson(description)).append("\",").append("\"color\": ").append(plugin.getDiscordManager().getJoinColor());

        if (plugin.getDiscordManager().isJoinAvatarEnabled()) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {").append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    public void sendDeathMessage(String username) {
        if (plugin.getDiscordManager().isDeathEnabled()) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(plugin.getDiscordManager().getDiscordHook());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = buildEmbedDeath(username);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = connection.getResponseCode();
                if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                    plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    private @NotNull String buildEmbedDeath(String username) {
        String descriptionTemplate = plugin.getDiscordManager().getDeathDescription();
        String description = descriptionTemplate.replace("%player%", escapeJson(username));
        String avatarUrl = "https://mc-heads.net/avatar/" + username;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{").append("\"title\": \"").append(plugin.getDiscordManager().getDeathTitle()).append("\",").append("\"description\": \"").append(escapeJson(description)).append("\",").append("\"color\": ").append(plugin.getDiscordManager().getDeathColor());

        if (plugin.getDiscordManager().isDeathAvatarEnabled()) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {").append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    public void sendBannedWordEmbed(String playerName, String word, String message, String URL) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = buildEmbedBannedWord(playerName, word, message);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private @NotNull String buildEmbedBannedWord(String playerName, String word, String message) {
        String title = plugin.getDiscordManager().getBannedWordsTitle();
        String descriptionTemplate = plugin.getDiscordManager().getBannedWordsDescription();
        int color = plugin.getDiscordManager().getBannedWordsColor();
        boolean avatarEnabled = plugin.getDiscordManager().isBannedWordsAvatar();

        String description = descriptionTemplate
                .replace("%player%", escapeJson(playerName))
                .replace("%word%", escapeJson(word))
                .replace("%message%", escapeJson(message));

        String avatarUrl = "https://mc-heads.net/avatar/" + playerName;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{")
                .append("\"title\": \"").append(escapeJson(title)).append("\",")
                .append("\"description\": \"").append(escapeJson(description)).append("\",")
                .append("\"color\": ").append(color);

        if (avatarEnabled) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {")
                    .append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    public void sendMuteEmbed(String playerName, String senderName, String URL) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = buildEmbedMute(playerName, senderName);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private @NotNull String buildEmbedMute(String playerName, @NotNull String senderName) {
        String title = plugin.getDiscordManager().getMuteTitle();
        String descriptionTemplate = plugin.getDiscordManager().getMuteDescription();
        int color = plugin.getDiscordManager().getMuteColor();
        boolean avatarEnabled = plugin.getDiscordManager().isMuteAvatar();

        String description = descriptionTemplate
                .replace("%player%", escapeJson(playerName))
                .replace("%admin%", escapeJson(senderName));

        String avatarUrl = "https://mc-heads.net/avatar/" + playerName;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{")
                .append("\"title\": \"").append(escapeJson(title)).append("\",")
                .append("\"description\": \"").append(escapeJson(description)).append("\",")
                .append("\"color\": ").append(color);

        if (avatarEnabled) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {")
                    .append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    public void sendBannedCommandEmbed(String playerName, String word, String message, String URL) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = buildEmbedBannedCommand(playerName, word, message);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_NO_CONTENT) {
                plugin.getLogger().info("Failed to send message. HTTP error code: " + code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private @NotNull String buildEmbedBannedCommand(String playerName, String word, String message) {
        String title = plugin.getDiscordManager().getBannedCommandsTitle();
        String descriptionTemplate = plugin.getDiscordManager().getBannedCommandsDescription();
        int color = plugin.getDiscordManager().getBannedCommandsColor();
        boolean avatarEnabled = plugin.getDiscordManager().isBannedCommandsAvatar();

        String description = descriptionTemplate
                .replace("%player%", escapeJson(playerName))
                .replace("%word%", escapeJson(word))
                .replace("%message%", escapeJson(message));

        String avatarUrl = "https://mc-heads.net/avatar/" + playerName;

        StringBuilder embedBuilder = new StringBuilder();
        embedBuilder.append("{")
                .append("\"embeds\": [{")
                .append("\"title\": \"").append(escapeJson(title)).append("\",")
                .append("\"description\": \"").append(escapeJson(description)).append("\",")
                .append("\"color\": ").append(color);

        if (avatarEnabled) {
            embedBuilder.append(",")
                    .append("\"thumbnail\": {")
                    .append("\"url\": \"").append(escapeJson(avatarUrl)).append("\"")
                    .append("}");
        }

        embedBuilder.append("}]")
                .append("}");

        return embedBuilder.toString();
    }

    private @NotNull String escapeJson(@NotNull String message) {
        return message.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

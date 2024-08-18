package commands;

import config.AutoBroadcastManager;
import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AutoBroadcastCommand implements CommandExecutor {
    private final TChat plugin;

    public AutoBroadcastCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        if (!sender.hasPermission("tchat.admin.autobroadcast")) {
            String message = plugin.getMessagesManager().getNoPermission();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        if (args.length < 1) {
            String message = plugin.getMessagesManager().getAutoBroadcastUsage();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        AutoBroadcastManager autoBroadcastManager = plugin.getAutoBroadcastManager();

        switch (args[0].toLowerCase()) {
            case "start":
                plugin.getAutoBroadcastSender().startBroadcastTask();
                String startMessage = plugin.getMessagesManager().getAutoBroadcastStart();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + startMessage));
                break;

            case "stop":
                plugin.getAutoBroadcastSender().stopBroadcastTask();
                String stopMessage = plugin.getMessagesManager().getAutoBroadcastStop();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + stopMessage));
                break;

            case "restart":
                plugin.getAutoBroadcastSender().restartBroadcasts();
                String restartMessage = plugin.getMessagesManager().getAutoBroadcastRestart();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + restartMessage));
                break;

            case "remove":
                if (args.length < 2) {
                    String removeUsageMessage = plugin.getMessagesManager().getAutoBroadcastUsageRemove();
                    sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + removeUsageMessage));
                    return false;
                }
                String broadcastKeyToRemove = args[1];
                autoBroadcastManager.removeBroadcast(broadcastKeyToRemove);
                String removeMessage = plugin.getMessagesManager().getAutoBroadcastRemove();
                removeMessage = removeMessage.replace("%broadcast%", broadcastKeyToRemove);
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + removeMessage));
                break;

            case "add":
                if (!(sender instanceof Player player)) {
                    String noPlayerMessage = plugin.getMessagesManager().getNoPlayer();
                    sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + noPlayerMessage));
                    return false;
                }
                if (args.length < 2) {
                    String addUsageMessage = plugin.getMessagesManager().getAutoBroadcastUsageAdd();
                    sender.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + addUsageMessage));
                    return false;
                }
                String newBroadcastKey = args[1];
                startAddBroadcastConversation(player, newBroadcastKey);
                break;

            default:
                String usageMessage = plugin.getMessagesManager().getAutoBroadcastUsage();
                sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + usageMessage));
                return false;
        }

        return true;
    }

    private void startAddBroadcastConversation(Player player, String broadcastKey) {
        String prefix = plugin.getMessagesManager().getPrefix();
        ConversationFactory factory = new ConversationFactory(plugin)
                .withFirstPrompt(new BroadcastNamePrompt(broadcastKey))
                .withLocalEcho(false)
                .withEscapeSequence("cancel")
                .thatExcludesNonPlayersWithMessage(plugin.getTranslateColors().translateColors(player, prefix + plugin.getMessagesManager().getNoPlayer()));

        Conversation conversation = factory.buildConversation(player);
        player.beginConversation(conversation);
    }

    private class BroadcastNamePrompt extends StringPrompt {
        private final String broadcastKey;

        public BroadcastNamePrompt(String broadcastKey) {
            this.broadcastKey = broadcastKey;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getAutoBroadcastAddEnabled();
            return plugin.getTranslateColors().translateColors(null, prefix + message);
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            assert input != null;
            boolean enabled = input.equalsIgnoreCase("yes");
            context.setSessionData("enabled", enabled);
            return new BroadcastMessagePrompt(broadcastKey, enabled);
        }
    }

    private class BroadcastMessagePrompt extends StringPrompt {
        private final String broadcastKey;
        private final boolean enabled;

        public BroadcastMessagePrompt(String broadcastKey, boolean enabled) {
            this.broadcastKey = broadcastKey;
            this.enabled = enabled;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getAutoBroadcastAddNewLine();
            return plugin.getTranslateColors().translateColors(null, prefix + message);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Prompt acceptInput(@NotNull ConversationContext context, String input) {
            if (input == null || input.trim().isEmpty()) {
                return this;
            }

            List<String> messages = (List<String>) context.getSessionData("messages");
            if (messages == null) {
                messages = new ArrayList<>();
                context.setSessionData("messages", messages);
            }

            String prefix = plugin.getMessagesManager().getPrefix();

            if (input.equalsIgnoreCase("done")) {
                if (messages.isEmpty()) {
                    String message = plugin.getMessagesManager().getAutoBroadcastAddOneLine();
                    context.getForWhom().sendRawMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                    return this;
                }
                context.setSessionData("messages", messages);
                return new BroadcastActionsPrompt(broadcastKey, enabled, messages);
            } else {
                messages.add(input);
                return this;
            }
        }
    }

    private class BroadcastActionsPrompt extends StringPrompt {
        private final String broadcastKey;
        private final boolean enabled;
        private final List<String> messages;

        public BroadcastActionsPrompt(String broadcastKey, boolean enabled, List<String> messages) {
            this.broadcastKey = broadcastKey;
            this.enabled = enabled;
            this.messages = messages != null ? messages : new ArrayList<>();
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            String prefix = plugin.getMessagesManager().getPrefix();
            String message = plugin.getMessagesManager().getAutoBroadcastActionsPrompt();
            return plugin.getTranslateColors().translateColors(null, prefix + message);
        }

        @Override
        public Prompt acceptInput(@NotNull ConversationContext context, String input) {
            assert input != null;
            String lowerInput = input.toLowerCase();
            String prefix = plugin.getMessagesManager().getPrefix();

            if (lowerInput.equals("done")) {
                boolean titleEnabled = context.getSessionData("titleEnabled") != null ? (Boolean) context.getSessionData("titleEnabled") : false;
                String title = (String) context.getSessionData("title");
                String subtitle = (String) context.getSessionData("subtitle");
                boolean soundEnabled = context.getSessionData("soundEnabled") != null ? (Boolean) context.getSessionData("soundEnabled") : false;
                String sound = (String) context.getSessionData("sound");
                boolean particlesEnabled = context.getSessionData("particlesEnabled") != null ? (Boolean) context.getSessionData("particlesEnabled") : false;
                String particle = (String) context.getSessionData("particle");
                int particleCount = context.getSessionData("particleCount") != null ? (Integer) context.getSessionData("particleCount") : 0;
                boolean actionbarEnabled = context.getSessionData("actionbarEnabled") != null ? (Boolean) context.getSessionData("actionbarEnabled") : false;
                String actionbar = (String) context.getSessionData("actionbar");

                String channel = context.getSessionData("channel") != null ? (String) context.getSessionData("channel") : "none";
                String permission = context.getSessionData("permission") != null ? (String) context.getSessionData("permission") : "none";

                plugin.getAutoBroadcastManager().addBroadcast(
                        broadcastKey, enabled, messages,
                        titleEnabled, title, subtitle,
                        soundEnabled, sound,
                        particlesEnabled, particle, particleCount,
                        actionbarEnabled, actionbar,
                        channel, permission
                );

                String message = plugin.getMessagesManager().getAutoBroadcastAddAdded();
                message = message.replace("%broadcast%", broadcastKey);
                context.getForWhom().sendRawMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
                return Prompt.END_OF_CONVERSATION;
            }

            if (lowerInput.startsWith("title")) {
                boolean titleEnabled = true;
                String title = lowerInput.substring(6).trim();
                context.setSessionData("titleEnabled", titleEnabled);
                context.setSessionData("title", title);
            } else if (lowerInput.startsWith("subtitle")) {
                String subtitle = lowerInput.substring(9).trim();
                context.setSessionData("subtitle", subtitle);
            } else if (lowerInput.startsWith("sound")) {
                boolean soundEnabled = true;
                String sound = lowerInput.substring(6).trim();
                context.setSessionData("soundEnabled", soundEnabled);
                context.setSessionData("sound", sound);
            } else if (lowerInput.startsWith("particles")) {
                try {
                    int count = Integer.parseInt(lowerInput.substring(11).trim());
                    context.setSessionData("particleCount", count);
                } catch (NumberFormatException e) {
                    context.getForWhom().sendRawMessage(plugin.getTranslateColors().translateColors(null, prefix + "Invalid number for particles."));
                }
            } else if (lowerInput.startsWith("particle")) {
                boolean particlesEnabled = true;
                String particle = lowerInput.substring(9).trim();
                context.setSessionData("particlesEnabled", particlesEnabled);
                context.setSessionData("particle", particle);
            } else if (lowerInput.startsWith("actionbar")) {
                boolean actionbarEnabled = true;
                String actionbar = lowerInput.substring(10).trim();
                context.setSessionData("actionbarEnabled", actionbarEnabled);
                context.setSessionData("actionbar", actionbar);
            }
            else if (lowerInput.startsWith("channel")) {
                String channel = lowerInput.substring(8).trim();
                context.setSessionData("channel", channel);
            } else if (lowerInput.startsWith("permission")) {
                String permission = lowerInput.substring(10).trim();
                context.setSessionData("permission", permission);
            } else {
                context.getForWhom().sendRawMessage(plugin.getTranslateColors().translateColors(null, prefix + "Unknown command. Use 'done' to finish."));
            }

            return this;
        }
    }
}

package commands;

import config.ChatGamesManager;
import minealex.tchat.TChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import utils.ChatGamesSender;

import config.ChatGamesManager.Title;
import config.ChatGamesManager.SoundEffect;
import config.ChatGamesManager.ParticleEffect;
import config.ChatGamesManager.ActionBar;

import java.util.Arrays;

public class ChatGamesCommand implements CommandExecutor {

    private final TChat plugin;
    private final ChatGamesSender chatGamesSender;
    private final ChatGamesManager chatGamesManager;

    public ChatGamesCommand(TChat plugin, ChatGamesSender chatGamesSender, ChatGamesManager chatGamesManager) {
        this.plugin = plugin;
        this.chatGamesSender = chatGamesSender;
        this.chatGamesManager = chatGamesManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String p = plugin.getMessagesManager().getPrefix();
        if (!(sender instanceof Player player)) {
            String m = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, p + m));
            return true;
        }

        if (!player.hasPermission("tchat.admin") && !player.hasPermission("tchat.admin.chatgames")) {
            String m = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            return true;
        }

        if (args.length == 0) {
            String u = plugin.getMessagesManager().getCgUsage();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + u));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                chatGamesSender.startNextGame();
                String s = plugin.getMessagesManager().getCgStart();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + s));
                break;

            case "stop":
                chatGamesSender.stopGame();
                String m = plugin.getMessagesManager().getCgStop();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
                break;

            case "restart":
                chatGamesSender.restartGame();
                String r = plugin.getMessagesManager().getCgRestart();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + r));
                break;

            case "add":
                handleAddGameCommand(player, args);
                break;

            case "remove":
                handleRemoveGameCommand(player, args);
                break;

            default:
                String u = plugin.getMessagesManager().getCgUsage();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, p + u));
                break;
        }

        return true;
    }

    private void handleAddGameCommand(Player player, String @NotNull [] args) {
        String p = plugin.getMessagesManager().getPrefix();
        if (args.length < 4) {
            String m = plugin.getMessagesManager().getCgUsageAdd();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            return;
        }

        String name = args[1];
        String[] messagesArray = args[2].split(",");
        String[] keywordsArray = args[3].split(",");
        String[] rewardsArray = args.length >= 5 ? args[4].split(",") : new String[0];

        ChatGamesManager.Options options = new ChatGamesManager.Options(60, 30);

        Title startTitle = new Title(true, "&aThe game starts!", "", 10, 70, 20);
        SoundEffect startSound = new SoundEffect(true, "ENTITY_PLAYER_LEVELUP", 1.0f, 1.0f);
        ParticleEffect startParticle = new ParticleEffect(true, "FIREWORKS_SPARK", 10, 1.0);
        ActionBar startActionBar = new ActionBar(true, "&bStarting game!");

        Title endTitle = new Title(true, "&cThe Game ended!", "", 10, 70, 20);
        SoundEffect endSound = new SoundEffect(true, "ENTITY_ENDER_DRAGON_DEATH", 1.0f, 1.0f);
        ParticleEffect endParticle = new ParticleEffect(true, "EXPLOSION_LARGE", 10, 1.0);
        ActionBar endActionBar = new ActionBar(true, "&cGame over");

        Title winnerTitle = new Title(true, "&cGame over!", "", 10, 70, 20);
        SoundEffect winnerSound = new SoundEffect(true, "ENTITY_VILLAGER_YES", 1.0f, 1.0f);
        ParticleEffect winnerParticle = new ParticleEffect(true, "HEART", 10, 1.0);
        ActionBar winnerActionBar = new ActionBar(true, "&a%winner% has won the game!");

        ChatGamesManager.Effects startEffects = new ChatGamesManager.Effects(startTitle, startSound, startParticle, startActionBar);
        ChatGamesManager.Effects endEffects = new ChatGamesManager.Effects(endTitle, endSound, endParticle, endActionBar);
        ChatGamesManager.Effects winnerEffects = new ChatGamesManager.Effects(winnerTitle, winnerSound, winnerParticle, winnerActionBar);

        chatGamesManager.addGame(
                name,
                Arrays.asList(messagesArray),
                Arrays.asList(keywordsArray),
                Arrays.asList(rewardsArray),
                options,
                startEffects,
                endEffects,
                winnerEffects
        );

        chatGamesManager.reloadConfig();
        String m = plugin.getMessagesManager().getCgAdd().replace("%name%", name);
        player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
    }

    private void handleRemoveGameCommand(Player player, String @NotNull [] args) {
        String p = plugin.getMessagesManager().getPrefix();
        if (args.length < 2) {
            String m = plugin.getMessagesManager().getCgUsageRemove();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
            return;
        }

        String name = args[1];

        chatGamesManager.removeGame(name);
        chatGamesManager.reloadConfig();
        String m = plugin.getMessagesManager().getCgRemove().replace("%name%", name);
        player.sendMessage(plugin.getTranslateColors().translateColors(player, p + m));
    }
}

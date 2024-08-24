package utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.CommandsManager;
import minealex.tchat.TChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.jetbrains.annotations.NotNull;

public class CustomCommands implements Listener {
    private final TChat plugin;
    private final CommandsManager commandsManager;
    private final Map<UUID, Long> commandCooldowns = new HashMap<>();
    private int currentIteration;
    private boolean inForLoop;

    public CustomCommands(@NotNull TChat plugin) {
        this.plugin = plugin;
        this.commandsManager = plugin.getCommandsManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommand(@NotNull PlayerCommandPreprocessEvent e) {
        if (e.isCancelled()) { return; }

        Player player = e.getPlayer();
        String message = e.getMessage();

        if (message.startsWith("/")) {
            String[] commandArgs = message.substring(1).split(" ");
            String commandName = commandArgs[0].toLowerCase();
            if (commandsManager.getCommands().containsKey(commandName)) {
                String prefix = plugin.getMessagesManager().getPrefix();
                CommandsManager.Command command = commandsManager.getCommands().get(commandName);
                boolean allowArgs = command.isArgs();

                if (allowArgs && commandArgs.length < 2) {
                    String message1 = plugin.getMessagesManager().getCustomCommandsArguments();
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message1));
                    e.setCancelled(true);
                    return;
                }

                if (command.isPermissionRequired()) {
                    String permission = "tchat.customcommand." + commandName;
                    if (!player.hasPermission(permission) && !player.hasPermission("tchat.admin")) {
                        String message1 = plugin.getMessagesManager().getNoPermission();
                        player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message1));
                        e.setCancelled(true);
                        return;
                    }
                }

                int cooldownSeconds = this.getCooldown(commandName);
                if (cooldownSeconds > 0 && this.isOnCooldown(player, commandName, cooldownSeconds)) {
                    int remainingCooldown = this.getRemainingCooldown(player, commandName);
                    String message1 = plugin.getMessagesManager().getCustomCommandsCooldown();
                    message1 = message1.replace("%cooldown%", String.valueOf(remainingCooldown));
                    player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message1));
                    e.setCancelled(true);
                    return;
                }

                List<String> actions = command.getActions();
                processActions(player, actions, allowArgs ? message.substring(commandArgs[0].length() + 1) : null);

                if (cooldownSeconds > 0) {
                    this.setCooldown(player, commandName, cooldownSeconds);
                }

                e.setCancelled(true);
            }
        }
    }

    private void processActions(Player player, @NotNull List<String> actions, String args) {
        boolean isIfBlock = false;
        boolean isElseIfBlock = false;
        boolean skipActions = false;
        boolean inConditional = false;
        boolean inLoop = false;
        List<String> loopActions = new ArrayList<>();
        int loopCount = 0;

        for (String action : actions) {
            action = processPlaceholders(player, action);

            if (action.startsWith("[FOR]")) {
                if (inLoop) {
                    skipActions = true;
                } else {
                    try {
                        loopCount = Integer.parseInt(action.substring(5).trim());
                        loopActions = new ArrayList<>();
                        inLoop = true;
                        inForLoop = true;
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid number in [FOR]: " + action);
                        skipActions = true;
                    }
                }
            } else if (action.startsWith("[ROF]")) {
                if (inLoop) {
                    inLoop = false;
                    for (currentIteration = 1; currentIteration <= loopCount; currentIteration++) {
                        for (String loopAction : loopActions) {
                            executeAction(player, loopAction, args);
                        }
                    }
                    inForLoop = false;
                } else {
                    skipActions = true;
                }
            } else if (inLoop) {
                loopActions.add(action);
            } else if (action.startsWith("[IF]")) {
                if (inConditional) {
                    skipActions = true;
                } else {
                    isIfBlock = evaluateCondition(action.substring(4).trim(), player);
                    skipActions = !isIfBlock;
                    inConditional = true;
                }
            } else if (action.startsWith("[ELSE IF]")) {
                if (inConditional && !isIfBlock) {
                    isElseIfBlock = evaluateCondition(action.substring(9).trim(), player);
                    skipActions = !isElseIfBlock;
                } else {
                    skipActions = true;
                }
            } else if (action.startsWith("[ELSE]")) {
                if (inConditional && !isIfBlock && !isElseIfBlock) {
                    isIfBlock = true;
                    skipActions = false;
                } else {
                    skipActions = true;
                }
            } else if (action.startsWith("[FI]")) {
                inConditional = false;
                isIfBlock = false;
                isElseIfBlock = false;
                skipActions = false;
            } else if (!skipActions) {
                executeAction(player, action, args);
            }
        }
    }

    private boolean evaluateCondition(@NotNull String condition, Player player) {
        String[] orConditions = condition.split("\\|\\|");
        boolean finalResult = false;

        for (String orCondition : orConditions) {
            String[] andConditions = orCondition.split("&&");
            boolean orResult = true;

            for (String andCondition : andConditions) {
                boolean result = evaluateSingleCondition(andCondition.trim(), player);
                orResult = orResult && result;
            }

            finalResult = finalResult || orResult;
        }

        return finalResult;
    }

    private boolean evaluateSingleCondition(String condition, Player player) {
        Pattern pattern = Pattern.compile("%(\\w+)%|([0-9]+)\\s*(>=|<=|>|<|==|!=)\\s*(\\d+)");
        Matcher matcher = pattern.matcher(condition);

        if (matcher.find()) {
            String placeholder = matcher.group(1);
            String literalValue = matcher.group(2);
            String operator = matcher.group(3);
            int value = Integer.parseInt(matcher.group(4));

            int placeholderNumber;
            if (placeholder != null) {
                String placeholderValue = PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%");
                try {
                    placeholderNumber = Integer.parseInt(placeholderValue);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid placeholder value for " + placeholder + ": " + placeholderValue);
                    return false;
                }
            } else {
                placeholderNumber = Integer.parseInt(literalValue);
            }

            boolean result;
            switch (operator) {
                case ">=":
                    result = placeholderNumber >= value;
                    break;
                case "<=":
                    result = placeholderNumber <= value;
                    break;
                case ">":
                    result = placeholderNumber > value;
                    break;
                case "<":
                    result = placeholderNumber < value;
                    break;
                case "==":
                    result = placeholderNumber == value;
                    break;
                case "!=":
                    result = placeholderNumber != value;
                    break;
                case "=~":
                    Pattern regexPattern = Pattern.compile(String.valueOf(value));
                    Matcher regexMatcher = regexPattern.matcher(String.valueOf(placeholderNumber));
                    result = regexMatcher.matches();
                    break;
                default:
                    plugin.getLogger().warning("Unsupported operator: " + operator);
                    return false;
            }

            return result;
        } else {
            return false;
        }
    }

    private @NotNull String processPlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    private void executeAction(Player player, @NotNull String action, String args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        String[] parts = action.split(" ", 2);
        if (parts.length < 2) {
            plugin.getLogger().info("Invalid action format: " + action);
            return;
        }

        String type = parts[0].trim();
        String data = parts[1].trim();

        if (data.startsWith("'") && data.endsWith("'")) {
            data = data.substring(1, data.length() - 1);
        }

        data = data.replace("%prefix%", prefix);
        data = data.replace("%i%", String.valueOf(currentIteration));

        if (args != null) {
            String modifiedArgs = args.substring(1);
            data = data.replace("%args%", modifiedArgs);
        }

        switch (type) {
            case "[MESSAGE]":
                player.sendMessage(plugin.getTranslateColors().translateColors(player, data));
                break;
            case "[TITLE]":
                String[] titleParts = data.split(";");
                if (titleParts.length == 2) {
                    player.sendTitle(titleParts[0], titleParts[1], 10, 70, 20);
                }
                break;
            case "[PLAYER_COMMAND]":
                player.performCommand(data.replace("{player}", player.getName()));
                break;
            case "[CONSOLE_COMMAND]":
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), data.replace("{player}", player.getName()));
                break;
            case "[SOUND]":
                player.playSound(player.getLocation(), Sound.valueOf(data), 1.0F, 1.0F);
                break;
            case "[PARTICLE]":
                player.spawnParticle(Particle.valueOf(data), player.getLocation(), 100, 1.0D, 1.0D, 1.0D);
                break;
            case "[TELEPORT]":
                String[] teleportParts = data.split(";");
                if (teleportParts.length == 4) {
                    World world = Bukkit.getWorld(teleportParts[0]);
                    if (world != null) {
                        Location location = new Location(world,
                                Double.parseDouble(teleportParts[1]),
                                Double.parseDouble(teleportParts[2]),
                                Double.parseDouble(teleportParts[3]));
                        player.teleport(location);
                    }
                }
                break;
            case "[POTION_EFFECT]":
                String[] effectParts = data.split(" ");
                if (effectParts.length >= 2) {
                    String actionType = effectParts[0].toUpperCase();
                    String effectData = effectParts[1].trim();

                    String[] effectParams = effectData.split(":");
                    String effectName = effectParams[0].toUpperCase();
                    int durationInTicks = 0;
                    int amplifier = 0;

                    if (effectParams.length == 2) {
                        try {
                            durationInTicks = Integer.parseInt(effectParams[1]) * 20;
                        } catch (NumberFormatException e) {
                            plugin.getLogger().warning("Invalid duration format: " + effectParams[1]);
                            return;
                        }
                    } else if (effectParams.length == 3) {
                        try {
                            durationInTicks = Integer.parseInt(effectParams[1]) * 20;
                            amplifier = Integer.parseInt(effectParams[2]);
                        } catch (NumberFormatException e) {
                            plugin.getLogger().warning("Invalid duration or amplifier format: " + effectParams[1] + ", " + effectParams[2]);
                            return;
                        }
                    }

                    PotionEffectType potion = PotionEffectType.getByName(effectName);
                    if (potion != null) {
                        if (actionType.equals("ADD")) {
                            player.addPotionEffect(new PotionEffect(potion, durationInTicks, amplifier));
                        } else if (actionType.equals("REMOVE")) {
                            player.removePotionEffect(potion);
                        } else {
                            plugin.getLogger().warning("Invalid potion effect action type: " + actionType);
                        }
                    } else {
                        plugin.getLogger().warning("Invalid potion effect type: " + effectName);
                    }
                } else {
                    plugin.getLogger().warning("Invalid potion effect format: " + data + " (Expected format: ACTION EFFECT:PARAMS)");
                }
                break;
            case "[ACTION_BAR]":
                String translatedData = plugin.getTranslateColors().translateColors(player, data);
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        net.md_5.bungee.api.chat.TextComponent.fromLegacyText(translatedData));
                break;
            case "[BUNGEECORD]":
                String serverName = action.replace("[BUNGEECORD] ", "");
                sendPlayerToBungeeServer(player, serverName);
                break;
            case "[INVENTORY]":
                String[] inventoryParts = data.split(" ");
                if (inventoryParts.length >= 3) {
                    String actionType = inventoryParts[0];
                    String itemName = inventoryParts[1];
                    int quantity;

                    try {
                        quantity = Integer.parseInt(inventoryParts[2]);
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid quantity format: " + inventoryParts[2]);
                        return;
                    }

                    Material material = Material.valueOf(itemName);
                    ItemStack item = new ItemStack(material, quantity);

                    if (inventoryParts.length >= 4) {
                        String itemName2 = inventoryParts[3];
                        Material material2 = Material.valueOf(itemName2);
                        ItemStack item2 = new ItemStack(material2, quantity);

                        if (actionType.equals("CHANGE") && player.getInventory().contains(item)) {
                            player.getInventory().removeItem(item);
                            player.getInventory().addItem(item2);
                        }
                    } else {
                        if (actionType.equals("ADD")) {
                            player.getInventory().addItem(item);
                        } else if (actionType.equals("REMOVE")) {
                            player.getInventory().removeItem(item);
                        }
                    }
                } else {
                    plugin.getLogger().warning("Invalid inventory action format: " + data);
                }
                break;
            case "[CLICK_ACTION]":
                handleClickAction(player, data);
                break;
            case "[CHATCOLOR]":
                handleChatColorAction(player, data);
                break;
            case "[GLOBAL]":
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(plugin.getTranslateColors().translateColors(p, data));
                }
                break;
            case "[BROADCAST]":
                String format = plugin.getConfigManager().getBroadcastFormat();
                format = format.replace("%message%", data);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(plugin.getTranslateColors().translateColors(p, format));
                }
                break;
            case "[SLEEP]":
                String unit = data.substring(data.length() - 1);
                long time;

                try {
                    time = Long.parseLong(data.substring(0, data.length() - 1));
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid sleep time format: " + data);
                    return;
                }

                switch (unit) {
                    case "s":
                        time *= 1000;
                        break;
                    case "t":
                        time *= 50;
                        break;
                    case "m":
                        break;
                    default:
                        plugin.getLogger().warning("Invalid time unit for [SLEEP]: " + unit);
                        return;
                }

                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                break;
            case "[XP]":
                handleXPAction(player, data);
                break;
            case "[LEVEL]":
                handleLevelAction(player, data);
                break;
            default:
                plugin.getLogger().warning("Unknown action type: " + type);
                break;
        }
    }

    private void handleXPAction(Player player, @NotNull String data) {
        String[] parts = data.split(" ");
        if (parts.length != 2) {
            plugin.getLogger().warning("Invalid [XP] format: " + data + " (Expected format: ACTION VALUE)");
            return;
        }

        String actionType = parts[0].toUpperCase();
        int xpAmount;

        try {
            xpAmount = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid XP value: " + parts[1]);
            return;
        }

        UUID puuid = player.getUniqueId();
        int xp = plugin.getSaveManager().getXp(puuid);

        switch (actionType) {
            case "ADD":
                int result = xp + xpAmount;
                plugin.getSaveManager().setXp(puuid, result);
                break;
            case "REMOVE":
                int result1 = xp - xpAmount;
                plugin.getSaveManager().setXp(puuid, result1);
                break;
            case "SET":
                plugin.getSaveManager().setXp(puuid, xpAmount);
                break;
            default:
                plugin.getLogger().warning("Unknown [XP] action type: " + actionType);
                break;
        }
    }

    private void handleLevelAction(Player player, @NotNull String data) {
        String[] parts = data.split(" ");
        if (parts.length != 2) {
            plugin.getLogger().warning("Invalid [LEVEL] format: " + data + " (Expected format: ACTION VALUE)");
            return;
        }

        String actionType = parts[0].toUpperCase();
        int levelAmount;

        try {
            levelAmount = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid Level value: " + parts[1]);
            return;
        }

        UUID puuid = player.getUniqueId();
        int level = plugin.getSaveManager().getLevel(puuid);

        switch (actionType) {
            case "ADD":
                int result = level + levelAmount;
                plugin.getSaveManager().setLevel(puuid, result);
                break;
            case "REMOVE":
                int result1 = level - levelAmount;
                plugin.getSaveManager().setLevel(puuid, result1);
                break;
            case "SET":
                plugin.getSaveManager().setLevel(puuid, levelAmount);
                break;
            default:
                plugin.getLogger().warning("Unknown [XP] action type: " + actionType);
                break;
        }
    }

    private void handleChatColorAction(Player player, @NotNull String data) {
        String[] parts = data.split("/", 2);
        if (parts.length < 2) {
            plugin.getLogger().warning("Invalid [CHATCOLOR] format: " + data);
            return;
        }

        String type = parts[0].trim();
        String colorOrFormat = parts[1].trim();

        switch (type.toUpperCase()) {
            case "COLOR":
                plugin.getSaveManager().setChatColor(player.getUniqueId(), colorOrFormat);
                break;
            case "FORMAT":
                plugin.getSaveManager().setFormat(player.getUniqueId(), colorOrFormat);
                break;
            default:
                plugin.getLogger().warning("Unknown [CHATCOLOR] type: " + type);
                break;
        }
    }

    private int getCooldown(String commandName) {
        return commandsManager.getCommands().get(commandName).getCooldown();
    }

    private void handleClickAction(Player player, @NotNull String data) {
        String[] parts = data.split("\\|", 2);
        if (parts.length < 2) {
            plugin.getLogger().warning("Invalid [CLICK_ACTION] format: " + data);
            return;
        }

        String actionType = parts[0].trim();
        String message = parts[1].trim();

        String translatedMessage = plugin.getTranslateColors().translateColors(player, message);

        net.md_5.bungee.api.chat.TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent(translatedMessage);

        if (actionType.startsWith("OPEN")) {
            String url = actionType.substring("OPEN".length()).trim();
            textComponent.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, url));
        } else if (actionType.startsWith("SUGGEST")) {
            String command = actionType.substring("SUGGEST".length()).trim();
            textComponent.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, command));
        } else if (actionType.startsWith("EXECUTE")) {
            String command = actionType.substring("EXECUTE".length()).trim();
            textComponent.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, command));
        } else {
            plugin.getLogger().warning("Unknown [CLICK_ACTION] type: " + actionType);
            return;
        }

        player.spigot().sendMessage(textComponent);
    }

    private boolean isOnCooldown(@NotNull Player player, String commandName, int cooldownSeconds) {
        long currentTime = System.currentTimeMillis() / 1000L;
        long lastExecution = commandCooldowns.getOrDefault(player.getUniqueId(), 0L);
        return currentTime < (lastExecution + cooldownSeconds);
    }

    private int getRemainingCooldown(@NotNull Player player, String commandName) {
        long currentTime = System.currentTimeMillis() / 1000L;
        long lastExecution = commandCooldowns.getOrDefault(player.getUniqueId(), 0L);
        return (int) ((lastExecution + getCooldown(commandName)) - currentTime);
    }

    public void sendPlayerToBungeeServer(Player player, String serverName) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    private void setCooldown(@NotNull Player player, String commandName, int cooldownSeconds) {
        long currentTime = System.currentTimeMillis() / 1000L;
        commandCooldowns.put(player.getUniqueId(), currentTime);
    }
}

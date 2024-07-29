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

public class CustomCommands implements Listener {
    private final TChat plugin;
    private final CommandsManager commandsManager;
    private final Map<UUID, Long> commandCooldowns = new HashMap<>();

    public CustomCommands(TChat plugin) {
        this.plugin = plugin;
        this.commandsManager = plugin.getCommandsManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
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

    private void processActions(Player player, List<String> actions, String args) {
        boolean isIfBlock = false;
        boolean isElseIfBlock = false;
        boolean skipActions = false;

        for (String action : actions) {
            action = processPlaceholders(player, action);

            if (action.startsWith("[IF]")) {
                isIfBlock = evaluateCondition(action.substring(4).trim(), player);
                isElseIfBlock = false;
                skipActions = !isIfBlock;
            } else if (action.startsWith("[ELSE IF]")) {
                if (!isIfBlock) {
                    isElseIfBlock = evaluateCondition(action.substring(9).trim(), player);
                    skipActions = !isElseIfBlock;
                }
            } else if (action.startsWith("[ELSE]")) {
                if (!isIfBlock && !isElseIfBlock) {
                    isIfBlock = true;
                    skipActions = false;
                }
            } else if (action.startsWith("[FI]")) {
                isIfBlock = false;
                isElseIfBlock = false;
                skipActions = false;
            } else if (!skipActions) {
                executeAction(player, action, args);
            }
        }
    }

    private boolean evaluateCondition(String condition, Player player) {
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
                default:
                    plugin.getLogger().warning("Unsupported operator: " + operator);
                    return false;
            }

            return result;
        } else {
            plugin.getLogger().warning("No match found for condition: " + condition);
            return false;
        }
    }

    private String processPlaceholders(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    private void executeAction(Player player, String action, String args) {
        String prefix = plugin.getMessagesManager().getPrefix();
        String[] parts = action.split(" ", 2);
        if (parts.length < 2) {
            plugin.getLogger().info("Invalid action format: " + action);
            return;
        }

        String type = parts[0].trim();
        String data = parts[1].trim();

        data = data.replace("%prefix%", prefix);

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
        }
    }

    private int getCooldown(String commandName) {
        return commandsManager.getCommands().get(commandName).getCooldown();
    }

    private void handleClickAction(Player player, String data) {
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

    private boolean isOnCooldown(Player player, String commandName, int cooldownSeconds) {
        long currentTime = System.currentTimeMillis() / 1000L;
        long lastExecution = commandCooldowns.getOrDefault(player.getUniqueId(), 0L);
        return currentTime < (lastExecution + cooldownSeconds);
    }

    private int getRemainingCooldown(Player player, String commandName) {
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

    private void setCooldown(Player player, String commandName, int cooldownSeconds) {
        long currentTime = System.currentTimeMillis() / 1000L;
        commandCooldowns.put(player.getUniqueId(), currentTime);
    }
}

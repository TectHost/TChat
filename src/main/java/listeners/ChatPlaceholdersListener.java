package listeners;

import minealex.tchat.TChat;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatPlaceholdersListener implements Listener {

    private final TChat plugin;

    public ChatPlaceholdersListener(TChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String prefix = plugin.getMessagesManager().getPrefix();

        if (plugin.getPlaceholdersConfig().isCoordsEnabled() && message.contains(plugin.getPlaceholdersConfig().getCoordsName())) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.placeholder.coords")) {
                double x = player.getLocation().getX();
                double y = player.getLocation().getY();
                double z = player.getLocation().getZ();

                String xFormatted = String.format("%.2f", x);
                String yFormatted = String.format("%.2f", y);
                String zFormatted = String.format("%.2f", z);

                String coordsMessage = plugin.getTranslateColors().translateColors(player, plugin.getPlaceholdersConfig().getCoordsFormat());

                coordsMessage = coordsMessage.replace("%x%", xFormatted);
                coordsMessage = coordsMessage.replace("%y%", yFormatted);
                coordsMessage = coordsMessage.replace("%z%", zFormatted);

                message = message.replace(plugin.getPlaceholdersConfig().getCoordsName(), coordsMessage);
            } else {
                String error = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
            }
        }

        if (plugin.getPlaceholdersConfig().isItemEnabled() && message.contains(plugin.getPlaceholdersConfig().getItemName())) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.placeholder.item")) {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                String itemName = "None";

                if (itemInHand.getType() != Material.AIR) {
                    ItemMeta itemMeta = itemInHand.getItemMeta();
                    if (itemMeta != null && itemMeta.hasDisplayName()) {
                        itemName = itemMeta.getDisplayName();
                    } else {
                        itemName = itemInHand.getType().toString().replace('_', ' ').toLowerCase();
                    }
                }

                String itemMessage = plugin.getTranslateColors().translateColors(player, plugin.getPlaceholdersConfig().getItemFormat());
                itemMessage = itemMessage.replace("%item%", itemName);

                message = message.replace(plugin.getPlaceholdersConfig().getItemName(), itemMessage);
            } else {
                String error = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
            }
        }

        if (plugin.getPlaceholdersConfig().isWorldEnabled() && message.contains(plugin.getPlaceholdersConfig().getWorldName())) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.placeholder.world")) {
                String worldName = Objects.requireNonNull(player.getLocation().getWorld()).getName();

                String worldMessage = plugin.getTranslateColors().translateColors(player, plugin.getPlaceholdersConfig().getWorldFormat());

                worldMessage = worldMessage.replace("%world%", worldName);

                message = message.replace(plugin.getPlaceholdersConfig().getWorldName(), worldMessage);
            } else {
                String error = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
            }
        }

        if (plugin.getPlaceholdersConfig().isCommandEnabled()) {
            Pattern commandPattern = Pattern.compile(plugin.getPlaceholdersConfig().getCommandName());
            Matcher commandMatcher = commandPattern.matcher(message);

            if (commandMatcher.find()) {
                String command = commandMatcher.group(1);
                String beforeCommand = message.substring(0, commandMatcher.start());
                String afterCommand = message.substring(commandMatcher.end());

                String commandFormat = plugin.getPlaceholdersConfig().getCommandFormat();

                commandFormat = commandFormat.replace("%command%", command).replace("%before_command%", beforeCommand).replace("%after_command%", afterCommand);
                commandFormat = plugin.getTranslateColors().translateColors(player, commandFormat);
                TextComponent commandComponent = getHoverCommand(commandFormat, player, command);

                event.setCancelled(true);
                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                    onlinePlayer.spigot().sendMessage(commandComponent);
                }

                if (plugin.getConfigManager().isRegisterMessagesOnConsole()) {
                    String consoleMessage = commandComponent.toLegacyText();
                    consoleMessage = plugin.getTranslateColors().translateColors(player, consoleMessage);
                    Bukkit.getConsoleSender().sendMessage(consoleMessage);
                }
            } else {
                event.setMessage(message);
            }
        }

        String enderchestName = plugin.getPlaceholdersConfig().getEnderName();
        if (plugin.getPlaceholdersConfig().isEnderEnabled() && message.contains(enderchestName)) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.placeholder.enderchest")) {
                String[] parts = message.split(Pattern.quote(enderchestName), 2);

                if (parts.length == 2) {
                    String beforeEnder = parts[0];
                    String afterEnder = parts[1];

                    String initialMessage = plugin.getPlaceholdersConfig().getEnderFormat();
                    initialMessage = initialMessage.replace("%before_ender%", beforeEnder)
                            .replace("%after_ender%", afterEnder);

                    initialMessage = plugin.getTranslateColors().translateColors(player, initialMessage);

                    TextComponent enderComponent = getEnderTextComponent(initialMessage, player);

                    event.setCancelled(true);
                    for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                        onlinePlayer.spigot().sendMessage(enderComponent);
                    }

                    if (plugin.getConfigManager().isRegisterMessagesOnConsole()) {
                        String consoleMessage = enderComponent.toLegacyText();
                        consoleMessage = plugin.getTranslateColors().translateColors(player, consoleMessage);
                        Bukkit.getConsoleSender().sendMessage(consoleMessage);
                    }
                }
            } else {
                String error = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
            }
        } else {
            event.setMessage(message);
        }

        String inventoryName = plugin.getPlaceholdersConfig().getInventoryName();
        if (plugin.getPlaceholdersConfig().isInventoryEnabled() && message.contains(inventoryName)) {
            if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.placeholder.inventory")) {
                String[] parts = message.split(Pattern.quote(inventoryName), 2);

                if (parts.length == 2) {
                    String beforeInventory = parts[0];
                    String afterInventory = parts[1];

                    String initialMessage = plugin.getPlaceholdersConfig().getInventoryFormat();
                    initialMessage = initialMessage.replace("%before_inv%", beforeInventory)
                            .replace("%after_inv%", afterInventory);

                    initialMessage = plugin.getTranslateColors().translateColors(player, initialMessage);

                    TextComponent inventoryComponent = getTextComponent(initialMessage, player);

                    event.setCancelled(true);
                    for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                        onlinePlayer.spigot().sendMessage(inventoryComponent);
                    }

                    if (plugin.getConfigManager().isRegisterMessagesOnConsole()) {
                        String consoleMessage = inventoryComponent.toLegacyText();
                        consoleMessage = plugin.getTranslateColors().translateColors(player, consoleMessage);
                        Bukkit.getConsoleSender().sendMessage(consoleMessage);
                    }
                }
            } else {
                String error = plugin.getMessagesManager().getNoPermission();
                player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + error));
            }
        } else {
            event.setMessage(message);
        }

        event.setMessage(message);
    }

    private @NotNull TextComponent getHoverCommand(String commandFormat, Player player, String command) {
        TextComponent commandComponent = new TextComponent(commandFormat);

        if (plugin.getPlaceholdersConfig().isHoverCommandEnabled()) {
            ComponentBuilder hoverBuilder = getComponentBuilderCommand(player, command);
            TextComponent hoverComponent = new TextComponent(hoverBuilder.create());

            commandComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{hoverComponent}));
        }

        commandComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        return commandComponent;
    }

    private @NotNull ComponentBuilder getComponentBuilderCommand(Player player, String command) {
        List<String> hoverTextList = plugin.getPlaceholdersConfig().getHoverCommand();
        hoverTextList.replaceAll(s -> plugin.getTranslateColors().translateColors(player, s.replace("%command%", command)));

        ComponentBuilder hoverBuilder = new ComponentBuilder();
        for (int i = 0; i < hoverTextList.size(); i++) {
            String line = hoverTextList.get(i);
            hoverBuilder.append(line);
            if (i < hoverTextList.size() - 1) {
                hoverBuilder.append("\n");
            }
        }
        return hoverBuilder;
    }

    private @NotNull TextComponent getTextComponent(String initialMessage, @NotNull Player player) {
        TextComponent inventoryComponent = new TextComponent(initialMessage);

        if (plugin.getPlaceholdersConfig().isHoverInventoryTextEnabled()) {
            List<String> hoverTextList = plugin.getPlaceholdersConfig().getHoverInventoryText();
            if (hoverTextList != null) {
                ComponentBuilder hoverBuilder = new ComponentBuilder();
                for (int i = 0; i < hoverTextList.size(); i++) {
                    String line = hoverTextList.get(i);
                    String translatedLine = plugin.getTranslateColors().translateColors(player, line);
                    hoverBuilder.append(translatedLine);
                    if (i < hoverTextList.size() - 1) {
                        hoverBuilder.append("\n");
                    }
                }

                TextComponent hoverComponent = new TextComponent(hoverBuilder.create());
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{hoverComponent});
                inventoryComponent.setHoverEvent(hoverEvent);
            }
        }

        if (plugin.getPlaceholdersConfig().isHoverInventoryActionEnabled()){
            String action = plugin.getPlaceholdersConfig().getHoverInventoryAction();
            if (action != null) {
                action = action.replace("%player%", player.getName());
                inventoryComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
            }
        }

        return inventoryComponent;
    }

    private @NotNull TextComponent getEnderTextComponent(String initialMessage, @NotNull Player player) {
        TextComponent inventoryComponent = new TextComponent(initialMessage);

        if (plugin.getPlaceholdersConfig().isHoverEnderTextEnabled()) {
            List<String> hoverTextList = plugin.getPlaceholdersConfig().getHoverEnderText();
            if (hoverTextList != null) {
                ComponentBuilder hoverBuilder = new ComponentBuilder();
                for (int i = 0; i < hoverTextList.size(); i++) {
                    String line = hoverTextList.get(i);
                    String translatedLine = plugin.getTranslateColors().translateColors(player, line);
                    hoverBuilder.append(translatedLine);
                    if (i < hoverTextList.size() - 1) {
                        hoverBuilder.append("\n");
                    }
                }

                TextComponent hoverComponent = new TextComponent(hoverBuilder.create());
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{hoverComponent});
                inventoryComponent.setHoverEvent(hoverEvent);
            }
        }

        if (plugin.getPlaceholdersConfig().isHoverEnderActionEnabled()) {
            String action = plugin.getPlaceholdersConfig().getHoverEnderAction();
            if (action != null) {
                action = action.replace("%player%", player.getName());
                inventoryComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
            }
        }

        return inventoryComponent;
    }
}

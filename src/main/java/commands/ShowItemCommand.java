package commands;

import minealex.tchat.TChat;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ShowItemCommand implements CommandExecutor {

    private final TChat plugin;

    public ShowItemCommand(TChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();

        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.showitem")) {

            ItemStack item = player.getInventory().getItemInMainHand();
            plugin.getLogger().info("Player " + player.getName() + " is holding " + item.getType());

            if (item.getType().isAir()) {
                player.sendMessage("(BETA) You do not have any items in hand.");
                return false;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                player.sendMessage("(BETA) The item has no meta.");
                return false;
            }

            String itemName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();
            plugin.getLogger().info("Item name " + itemName);

            int durability = item.getType().getMaxDurability() - (meta instanceof Damageable ? ((Damageable) meta).getDamage() : 0);
            plugin.getLogger().info("Item durability " + durability);

            String itemTagJson = getItemTagJson(meta);
            plugin.getLogger().info("Generated item tag JSON " + itemTagJson);

            String itemJson = String.format(
                    "{\"id\":\"%s\",\"Count\":1,\"tag\":%s,\"Damage\":%d}",
                    item.getType().getKey().getKey(),
                    itemTagJson,
                    ((Damageable) meta).getDamage()
            );

            plugin.getLogger().info("Final item json: " + itemJson);

            TextComponent itemComponent = new TextComponent(itemName);
            itemComponent.setColor(net.md_5.bungee.api.ChatColor.WHITE);
            itemComponent.setBold(true);

            itemComponent.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(
                    net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM,
                    new ComponentBuilder(itemJson).create()
            ));

            player.spigot().sendMessage(itemComponent);
            plugin.getLogger().info("Item json sent to player " + player.getName());

        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
            plugin.getLogger().warning("Player " + player.getName() + " does not have permission to use /showitem.");
        }

        return true;
    }

    private String getItemTagJson(ItemMeta meta) {
        StringBuilder tagJson = new StringBuilder();
        tagJson.append("{");

        if (meta.hasDisplayName()) {
            tagJson.append("\"display\":{\"Name\":\"")
                    .append(escapeJson(meta.getDisplayName()))
                    .append("\"},");
        }

        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) meta;
            tagJson.append("\"Enchantments\":[");
            boolean first = true;
            for (Map.Entry<Enchantment, Integer> entry : enchantmentMeta.getStoredEnchants().entrySet()) {
                if (!first) {
                    tagJson.append(",");
                }
                first = false;
                tagJson.append("{\"id\":\"")
                        .append(entry.getKey().getKey().getKey())
                        .append("\",\"lvl\":")
                        .append(entry.getValue())
                        .append("}");
            }
            tagJson.append("],");
        } else if (meta.hasEnchants()) {
            tagJson.append("\"Enchantments\":[");
            boolean first = true;
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                if (!first) {
                    tagJson.append(",");
                }
                first = false;
                tagJson.append("{\"id\":\"")
                        .append(entry.getKey().getKey().getKey())
                        .append("\",\"lvl\":")
                        .append(entry.getValue())
                        .append("}");
            }
            tagJson.append("],");
        }

        if (tagJson.charAt(tagJson.length() - 1) == ',') {
            tagJson.deleteCharAt(tagJson.length() - 1);
        }

        tagJson.append("}");
        plugin.getLogger().info("getItemTagJson result: " + tagJson);
        return tagJson.toString();
    }

    private String escapeJson(String text) {
        String escapedText = text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        plugin.getLogger().info("escapeJson result: " + escapedText);
        return escapedText;
    }
}

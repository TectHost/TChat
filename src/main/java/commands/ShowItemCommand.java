package commands;

import minealex.tchat.TChat;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String prefix = plugin.getMessagesManager().getPrefix();

        if (!(sender instanceof Player player)) {
            String message = plugin.getMessagesManager().getNoPlayer();
            sender.sendMessage(plugin.getTranslateColors().translateColors(null, prefix + message));
            return false;
        }

        if (player.hasPermission("tchat.admin") || player.hasPermission("tchat.showitem")) {
            components(player, prefix);
        } else {
            String message = plugin.getMessagesManager().getNoPermission();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + message));
        }

        return true;
    }

    private void components(@NotNull Player player, String prefix) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            String m = plugin.getMessagesManager().getNoItemInHand();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + m));
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            String m = plugin.getMessagesManager().getInvalidItemMeta();
            player.sendMessage(plugin.getTranslateColors().translateColors(player, prefix + m));
            return;
        }

        String itemName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();

        String itemTagJson = getItemTagJson(meta);

        assert meta instanceof Damageable;
        String itemJson = String.format(
                "{\"id\":\"%s\",\"Count\":%d,\"tag\":%s,\"Damage\":%d}",
                item.getType().getKey().getKey(),
                item.getAmount(),
                itemTagJson,
                ((Damageable) meta).getDamage()
        );

        TextComponent prefixComponent = new TextComponent(plugin.getTranslateColors().translateColors(player, plugin.getSicConfig().getSicPrefix()));
        TextComponent suffixComponent = new TextComponent(plugin.getTranslateColors().translateColors(player, plugin.getSicConfig().getSicSuffix()));

        TextComponent itemComponent = new TextComponent(itemName);
        itemComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new ComponentBuilder(itemJson).create()
        ));

        TextComponent finalMessage = new TextComponent();
        finalMessage.addExtra(prefixComponent);
        finalMessage.addExtra(itemComponent);
        finalMessage.addExtra(suffixComponent);

        player.spigot().sendMessage(finalMessage);
    }

    @NotNull
    public String getItemTagJson(@NotNull ItemMeta meta) {
        StringBuilder tagJson = new StringBuilder();
        tagJson.append("{");

        if (meta.hasDisplayName()) {
            tagJson.append("\"display\":{\"Name\":\"")
                    .append(escapeJson(meta.getDisplayName()))
                    .append("\"},");
        }

        if (meta instanceof EnchantmentStorageMeta enchantmentMeta) {
            tagJson.append("\"Enchantments\":[");
            boolean first = true;
            for (Map.Entry<Enchantment, Integer> entry : enchantmentMeta.getStoredEnchants().entrySet()) {
                if (!first) {
                    tagJson.append(",");
                }
                first = false;
                tagJson.append("{\"id\":\"")
                        .append(entry.getKey().getKey())
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
                        .append(entry.getKey().getKey())
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
        return tagJson.toString();
    }

    private @NotNull String escapeJson(@NotNull String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

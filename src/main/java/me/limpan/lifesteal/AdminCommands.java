package me.limpan.lifesteal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class AdminCommands implements CommandExecutor{

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command can only be used by players"));
            return true;
        }

        Player player = (Player) sender;

        if (command == null) {
            // Handle this as a missing or unknown command
            sender.sendMessage(Component.text("Unknown command. Please check your command."));
            return true;
        }

        if(player.isOp())
        {
            if (command.getName().equalsIgnoreCase("giveHearts")) {
                if (args.length != 2) {
                    player.sendMessage(Component.text("Usage: /giveHearts <player> <amount>"));
                    return true;
                }

                String targetPlayerName = args[0];
                int amount = Integer.parseInt(args[1]) * 2; // remove the 2 if you want to be able to remove hp instead of hearts

                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                if (targetPlayer != null) {
                    double currentMaxHealth = targetPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    double newMaxHealth = currentMaxHealth + amount;
                    //Bukkit.getLogger().info(Double.toString(newMaxHealth) + " current health: " + Double.toString(currentMaxHealth) + " amount: " + Integer.toString(amount));
                    newMaxHealth = Math.max(newMaxHealth, LifeSteal.getMinHearts() * 2.0);
                    newMaxHealth = Math.min(newMaxHealth, LifeSteal.getMaxHearts() * 2.0);
                    targetPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMaxHealth);
                    player.sendMessage(Component.text("You have given " + amount / 2 + " ❤  to " + targetPlayerName));
                } else {
                    player.sendMessage(Component.text("Player not found: " + targetPlayerName));
                }
            }
            else if (command.getName().equalsIgnoreCase("takeHearts")) {
                if (args.length != 2) {
                    player.sendMessage(Component.text("Usage: /takeHearts <player> <amount>"));
                    return true;
                }

                String targetPlayerName = args[0];
                int amount = Integer.parseInt(args[1]) * 2; // remove the 2 if you want to be able to remove hp instead of hearts

                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                if (targetPlayer != null) {
                    double currentMaxHealth = targetPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    double newMaxHealth = Math.max(currentMaxHealth - amount, LifeSteal.getMinHearts()); // ensure minimum health is 1
                    targetPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMaxHealth);
                    player.sendMessage(Component.text("You have taken " + amount / 2 + " ❤ from " + targetPlayerName));
                } else {
                    player.sendMessage(Component.text("Player not found: " + targetPlayerName));
                }
            }
            else if (command.getName().equalsIgnoreCase("resetHearts")) {
                if (args.length != 1) {
                    player.sendMessage(Component.text("Usage: /resetHearts <player>"));
                    return true;
                }

                String targetPlayerName = args[0];
                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                if (targetPlayer != null) {
                    targetPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0); // Reset to default health (10 hearts)
                    player.sendMessage(Component.text("You have reset the ❤  of " + targetPlayerName));
                } else {
                    player.sendMessage(Component.text("Player not found: " + targetPlayerName));
                }
            }
            else if (command.getName().equalsIgnoreCase("setMinHearts")) {
                if (args.length != 1) {
                    player.sendMessage(Component.text("Usage: /setMinHearts <hearts>"));
                    return true;
                }

                int minHearts = Integer.parseInt(args[0]);
                LifeSteal.setMinHearts(minHearts * 2);
                player.sendMessage(Component.text("Set the minimum hearts to " + minHearts));
            }
            else if (command.getName().equalsIgnoreCase("setMaxHearts")) {
                if (args.length != 1) {
                    player.sendMessage(Component.text("Usage: /setMaxHearts <hearts>"));
                    return true;
                }

                int maxHearts = Integer.parseInt(args[0]);
                LifeSteal.setMaxHearts(maxHearts);
                player.sendMessage(Component.text("Set the maximum hearts to " + maxHearts));
            }
            else if (command.getName().equalsIgnoreCase("giveHeartItem")) {
                if (args.length != 2) {
                    player.sendMessage(Component.text("Usage: /giveHeartItem <player> <amount>"));
                    return true;
                }
                String targetPlayerName = args[0];
                Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
                ItemStack stack = createCustomHeart(Integer.parseInt(args[1]));
                assert targetPlayer != null;
                targetPlayer.getInventory().addItem(stack);
                targetPlayer.sendMessage(Component.text("You received "+ Integer.parseInt(args[1]) + " extra heart(s)!"));
            }
            else if (command.getName().equalsIgnoreCase("setAdjustableKillHearts")) {
                if(args.length != 1) {
                    player.sendMessage(Component.text("Usage: /setAdjustableKillHearts <amount>"));
                    return true;
                }
                player.sendMessage(Component.text("Set the adjustable kill hearts to " + Integer.parseInt(args[0])));
                LifeSteal.setAdjustableKillHearts(Integer.parseInt(args[0]));
            }
            else if (command.getName().equalsIgnoreCase("setAdjustableDeathHearts")) {
                if(args.length != 1) {
                    player.sendMessage(Component.text("Usage: /setAdjustableDeathHearts <amount>"));
                    return true;
                }
                player.sendMessage(Component.text("Set the adjustable death hearts to " + Integer.parseInt(args[0])));
                LifeSteal.setAdjustableDeathHearts(Integer.parseInt(args[0]));
            }
        }

        return true;
    }

    private ItemStack createCustomHeart(int amount)
    {
        ItemStack heart = new ItemStack(Material.RED_DYE , amount);
        ItemMeta heartMeta = heart.getItemMeta();
        Component heartDisplay = Component.text("Heart").color(NamedTextColor.RED);
        heartMeta.displayName(heartDisplay);
        Component lore = Component.text("Right-click to gain an extra heart").color(NamedTextColor.GRAY);
        heartMeta.lore(Collections.singletonList(lore));
        heart.setItemMeta(heartMeta);
        return heart;
    }
}

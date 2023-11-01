package me.limpan.lifesteal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import me.limpan.lifesteal.LifeSteal;

import java.awt.*;


public class EventListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event)
    {
        /* THIS CAUSED BUGS, CAN BE FIXED BUT OTHER METHOD WORKS BETTER
                if(event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();

            if(event.getFinalDamage() >= player.getHealth())
            {
                if(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() < LifeSteal.getMinHearts() + 2)
                {
                    Bukkit.getLogger().info(Double.toString(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
                    player.setGameMode(GameMode.SPECTATOR);
                    Bukkit.getServer().broadcast(Component.text(player.getName() + "has lost all their hearts and is now a spectator"));
                }
            }
        }
         */
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        String name = player.getName();
        //debug in server
        Bukkit.getServer().getLogger().info(name +" has died");

        if(killer != null)
        {
            double currentMaxHealth = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            double newMaxHealth = currentMaxHealth + LifeSteal.getAdjustableKillHearts();
            newMaxHealth = Math.max(newMaxHealth, LifeSteal.getMinHearts() * 2.0);
            newMaxHealth = Math.min(newMaxHealth, LifeSteal.getMaxHearts() * 2.0);

            killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMaxHealth);

            killer.sendMessage(Component.text("You gained " + LifeSteal.getAdjustableKillHearts() / 2 + " extra hearts for killing " + player.getName()));
        }

        if(player.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null)
        {
            double newMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - LifeSteal.getAdjustableDeathHearts();
            newMaxHealth = Math.max(newMaxHealth, LifeSteal.getMinHearts() * 2.0);
            if(player.getHealth() > newMaxHealth)
            {
                player.setHealth(newMaxHealth);
                Bukkit.getLogger().info(Double.toString(newMaxHealth));
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();

        if(player.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null)
        {
            double newMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - LifeSteal.getAdjustableDeathHearts(); // remove 1 heart when player respawns
            newMaxHealth = Math.max(newMaxHealth, LifeSteal.getMinHearts());
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMaxHealth);
            if(newMaxHealth != LifeSteal.getMinHearts())
                player.sendMessage(Component.text("You lost " + LifeSteal.getAdjustableDeathHearts() / 2 + " for dying "));
            else
                player.sendMessage(Component.text("You didn't lose any hearts since you are at the minimum limit"));
        }

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event)
    {
        if(event.getAction().toString().contains("RIGHT_CLICK"))
        {
            ItemStack item = event.getItem();
            if(item != null && item.getType() == Material.RED_DYE && item.hasItemMeta())
            {
                Player player = event.getPlayer();
                // calculate and set the new max health
                double currentMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                double newMaxHealth = currentMaxHealth + 2.0; // Adjust the value as needed
                newMaxHealth = Math.max(newMaxHealth, LifeSteal.getMinHearts() * 2.0);
                newMaxHealth = Math.min(newMaxHealth, LifeSteal.getMaxHearts() * 2.0);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newMaxHealth);
                player.sendMessage(Component.text("You gained " + 1 + " heart!"));
                item.setAmount(item.getAmount() - 1);
            }
        }
    }
}

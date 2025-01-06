package com.nayechan.combat.mechanics.reinforce;

import com.nayechan.combat.AncyCombat;
import io.th0rgal.oraxen.api.events.OraxenPackUploadEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ReinforceMechanicManager implements Listener {
    private ReinforceMechanicFactory factory;
    public ReinforceMechanicManager(ReinforceMechanicFactory factory)
    {
        this.factory = factory;
        AncyCombat.getInstance().getLogger().info("ReinforceMechanicManager Initialized");
    }

    public void applyReinforceEffect(Player player)
    {
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.hasItemMeta()) {
                ReinforceMechanic mechanic = factory.getMechanic(item);
                if (mechanic != null) {
                    int currentReinforceAmount = mechanic.getItemReinforceAmount(item);
                    AncyCombat.getInstance().getLogger().info("Current Reinforce Amount : "+currentReinforceAmount);

                    mechanic.setItemReinforceAmount(player, item, currentReinforceAmount);
                }
            }
        }
    }

    @EventHandler
    public void onPackUploadComplete(OraxenPackUploadEvent event)
    {
        AncyCombat.getInstance().getLogger().info("Currently Online!! : "+Bukkit.getOnlinePlayers().size());

        for(Player player : Bukkit.getOnlinePlayers()) {
            applyReinforceEffect(player);
        }
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event)
    {
        if (factory == null)
            return;

        Player player = event.getPlayer();
        applyReinforceEffect(player);
    }
}

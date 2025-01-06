package com.nayechan.combat.mechanics.rangedweapon;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.events.ReinforceEvent;
import com.nayechan.combat.mechanics.reinforce.ReinforceMechanic;
import com.nayechan.combat.mechanics.reinforce.ReinforceMechanicFactory;
import io.th0rgal.oraxen.api.events.OraxenPackUploadEvent;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class RangedWeaponMechanicManager implements Listener {
    private RangedWeaponMechanicFactory factory;

    public RangedWeaponMechanicManager(RangedWeaponMechanicFactory factory) {
        this.factory = factory;
    }
    
    public void applyRangedWeaponPower(Player player)
    {
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.hasItemMeta()) {
                RangedWeaponMechanic mechanic = factory.getMechanic(item);
                if (mechanic != null) {
                    mechanic.refreshLore(item);
                }
            }
        }
    }
    
    @EventHandler
    public void onItemReinforce(ReinforceEvent event)
    {
        RangedWeaponMechanic mechanic = factory.getMechanic(event.getItem());
        if(mechanic != null)
            mechanic.refreshLore(event.getItem());
    }

    @EventHandler
    public void onPackUploadComplete(OraxenPackUploadEvent event)
    {
        AncyCombat.getInstance().getLogger().info("Currently Online!! : "+Bukkit.getOnlinePlayers().size());

        for(Player player : Bukkit.getOnlinePlayers()) {
            applyRangedWeaponPower(player);
        }
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event)
    {
        if (factory == null)
            return;

        Player player = event.getPlayer();
        applyRangedWeaponPower(player);
    }
}

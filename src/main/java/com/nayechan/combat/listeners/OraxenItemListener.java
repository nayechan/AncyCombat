package com.nayechan.combat.listeners;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.mechanics.ReinforceMechanicFactory;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.api.events.OraxenNativeMechanicsRegisteredEvent;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.module.Configuration;

public class OraxenItemListener implements Listener {
    private final AncyCombat plugin;
    
    public OraxenItemListener(AncyCombat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMechanicRegister(OraxenNativeMechanicsRegisteredEvent event) {
        plugin.getLogger().info("Mechanic registering!");
        MechanicsManager.registerMechanicFactory(
                "reinforce", new ReinforceMechanicFactory("reinforce"),true);
        OraxenItems.loadItems();
        plugin.getLogger().info("Mechanic registered!");
    }
}

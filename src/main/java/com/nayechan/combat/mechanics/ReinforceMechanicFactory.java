package com.nayechan.combat.mechanics;

import com.nayechan.combat.AncyCombat;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ReinforceMechanicFactory extends MechanicFactory {
    public ReinforceMechanicFactory(String mechanicId) {
        super(mechanicId);
        MechanicsManager.registerListeners(
                OraxenPlugin.get(),
                mechanicId,
                new ReinforceMechanicManager(this)
        );
    }

    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new ReinforceMechanic(this, itemMechanicConfiguration);
        addToImplemented(mechanic);
        return mechanic;
    }

    @Override
    public ReinforceMechanic getMechanic(String itemId) {
        return (ReinforceMechanic) super.getMechanic(itemId);
    }

    @Override
    public ReinforceMechanic getMechanic(ItemStack itemStack) {
        return (ReinforceMechanic) super.getMechanic(itemStack);
    }
}


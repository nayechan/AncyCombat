package com.nayechan.combat.mechanics.rangedweapon;

import com.nayechan.combat.AncyCombat;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class RangedWeaponMechanicFactory extends MechanicFactory {
    public RangedWeaponMechanicFactory(String mechanicId) {
        super(mechanicId);
        
        MechanicsManager.registerListeners(
                AncyCombat.getInstance(),
                mechanicId,
                new RangedWeaponMechanicManager(this)               
        );
    }

    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new RangedWeaponMechanic(this, itemMechanicConfiguration);
        addToImplemented(mechanic);
        return mechanic;
    }

    @Override
    public RangedWeaponMechanic getMechanic(String itemId) {
        return (RangedWeaponMechanic) super.getMechanic(itemId);
    }

    @Override
    public RangedWeaponMechanic getMechanic(ItemStack itemStack) {
        return (RangedWeaponMechanic) super.getMechanic(itemStack);
    }
}
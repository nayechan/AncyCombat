package com.nayechan.combat.mechanics.rangedweapon;

import com.nayechan.combat.mechanics.reinforce.ReinforceMechanic;
import com.nayechan.combat.mechanics.reinforce.ReinforceMechanicFactory;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class RangedWeaponMechanic extends Mechanic {
    public static NamespacedKey NAMESPACED_KEY = new NamespacedKey(OraxenPlugin.get(), "ranged_weapon");
    private final double power;

    protected RangedWeaponMechanic(
            MechanicFactory mechanicFactory,
            ConfigurationSection section) {
        super(mechanicFactory, section, itemBuilder -> {
            List<String> lore = itemBuilder.getLore();
            if(lore == null) {
                lore = new ArrayList<>();
            }
            
            lore.add("활 공격력 +"+(section.getDouble("power", 1)-1));
            itemBuilder.setLore(lore);
            return itemBuilder;
        });
        this.power = section.getDouble("power", 1);
    }

    public double getPower(ItemStack item) {
        double finalPower = power;

        ReinforceMechanicFactory reinforceMechanicFactory =
                (ReinforceMechanicFactory) MechanicsManager.getMechanicFactory("reinforce");

        if(reinforceMechanicFactory != null) {
            ReinforceMechanic reinforceMechanic =
                    reinforceMechanicFactory.getMechanic(item);


            if(reinforceMechanic != null) {
                finalPower += reinforceMechanic.getRangedWeaponBonus(item);
            }

        }
        return finalPower;
    }

    public void refreshLore(ItemStack item)
    {
        ItemUtils.editItemMeta(item, itemMeta -> {
            List<String> lore = item.getLore();
            if(lore == null) {
                lore = new ArrayList<>();
            }

            String reinforcePrefix = "활 공격력 +";
            double _power = getPower(item);
            String reinforceSuffix = String.format("%.1f", _power-1);

            boolean replaced = false;

            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).startsWith(reinforcePrefix)) {
                    if(_power > 0)
                        lore.set(i, reinforcePrefix + reinforceSuffix);
                    else
                        lore.remove(i);
                    replaced = true;
                    break;
                }
            }

            if (!replaced && _power > 0) {
                lore.add(reinforcePrefix + reinforceSuffix);
            }

            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        });
    }
}

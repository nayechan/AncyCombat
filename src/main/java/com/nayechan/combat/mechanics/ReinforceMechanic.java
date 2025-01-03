package com.nayechan.combat.mechanics;

import com.nayechan.combat.AncyCombat;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class ReinforceMechanic extends Mechanic {
    public static NamespacedKey NAMESPACED_KEY = new NamespacedKey(OraxenPlugin.get(), "reinforce");
    private final int reinforceAmount;
    
    protected ReinforceMechanic(
            MechanicFactory mechanicFactory, 
            ConfigurationSection section) {
        super(
                mechanicFactory, section,
                item -> item.setCustomTag(
                        NAMESPACED_KEY, PersistentDataType.INTEGER, section.getInt("reinforceAmount")
                )
        );

        this.reinforceAmount = section.getInt("reinforceAmount", 0);
    }
    
    public int getItemMaxReinforceAmount() {
        return reinforceAmount;
    }
    
    public int getItemReinforceAmount(ItemStack item) {
        return item.getPersistentDataContainer().getOrDefault(
                NAMESPACED_KEY, PersistentDataType.INTEGER, 0);
    }
    
    public boolean setItemReinforceAmount(@NotNull Player player, ItemStack item, int amount) {
        AtomicBoolean check = new AtomicBoolean(false);
        ItemUtils.editItemMeta(item, itemMeta -> {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            check.set(container.has(NAMESPACED_KEY, PersistentDataType.INTEGER));
            
            if(check.get()) {
                int maxReinforceAmount = getItemMaxReinforceAmount();
                int finalReinforceAmount = Math.min(maxReinforceAmount, amount);
                container.set(NAMESPACED_KEY, PersistentDataType.INTEGER, finalReinforceAmount);

                // Get the current lore
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }

                String reinforcePrefix = ChatColor.RED + "+";
                boolean replaced = false;

                for (int i = 0; i < lore.size(); i++) {
                    if (lore.get(i).startsWith(reinforcePrefix)) {
                        lore.set(i, reinforcePrefix + finalReinforceAmount);
                        replaced = true;
                        break;
                    }
                }
                
                if (!replaced) {
                    lore.add(reinforcePrefix + ChatColor.GREEN + finalReinforceAmount);
                }
                itemMeta.setLore(lore);                
                item.setItemMeta(itemMeta);
            }            
        });
        return check.get();
    }
}

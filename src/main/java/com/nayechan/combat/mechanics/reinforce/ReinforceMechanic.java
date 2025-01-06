package com.nayechan.combat.mechanics.reinforce;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.nayechan.combat.events.ReinforceEvent;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReinforceMechanic extends Mechanic {
    public static NamespacedKey NAMESPACED_KEY = new NamespacedKey(OraxenPlugin.get(), "reinforce");
    private final int reinforceAmount;
    private final double rangedWeaponPowerBonus;
    private final List<UnitModifier> modifiers = new ArrayList<>();
    
    private class UnitModifier{
        @Getter
        private Attribute attribute;
        private int operation;
        private double amount;
        private String slot;
        @Getter
        private String key;
        
        public UnitModifier(Attribute attribute, int operation, double amount, String slot) {
            this.attribute = attribute;
            this.operation = operation;
            this.amount = amount;
            this.slot = slot;
            this.key = "reinforce_stat_" + UUID.randomUUID().toString();
        }
        
        public AttributeModifier getModifier(float multiplier) {
            return new AttributeModifier(
                    new NamespacedKey(OraxenPlugin.get(), key),
                    amount * multiplier,
                    AttributeModifier.Operation.values()[operation],
                    EquipmentSlotGroup.getByName(slot)
            );
        }
    }
    
    protected ReinforceMechanic(
            MechanicFactory mechanicFactory, 
            ConfigurationSection section) {
        super(
                mechanicFactory, section,
                itemBuilder -> itemBuilder.setCustomTag(
                        NAMESPACED_KEY, PersistentDataType.INTEGER, 0
                )
        );        
        this.reinforceAmount = section.getInt("max", 0);
        this.rangedWeaponPowerBonus = section.getDouble("ranged_weapon_power", 0);
        List<Map<?, ?>> statList = section.getMapList("AttributeModifiers");
        for (Map<?, ?> statEntry : statList) {
            try {
                String attribute = (String) statEntry.get("attribute");
                Attribute attributeType = Attribute.valueOf(attribute);
                
                double amount = (statEntry.get("amount") instanceof Number)
                        ? ((Number) statEntry.get("amount")).doubleValue()
                        : 0.0;
                int operation = (statEntry.get("operation") instanceof Number)
                        ? ((Number) statEntry.get("operation")).intValue()
                        : 0;
                String slot = (statEntry.get("slot") instanceof String)
                        ? ((String) statEntry.get("slot"))
                        : "";
                
                // Store the UnitModifier in the list
                modifiers.add(new UnitModifier(attributeType, operation, amount, slot));

                // Process the extracted values
                System.out.println("Attribute: " + attribute);
                System.out.println("Amount: " + amount);
                System.out.println("Operation: " + operation);
            }
            catch (Exception e) {
                e.printStackTrace();                
            }
            
        }
    }
    
    public static void updateLore(List<String> existingLore, int current, int max)
    {
        ChatColor[] chatColors = {
                ChatColor.WHITE, ChatColor.AQUA, ChatColor.GREEN,
                ChatColor.YELLOW, ChatColor.RED, ChatColor.LIGHT_PURPLE
        };
        
        String reinforcePrefix = ChatColor.WHITE + "강화 ";
        String reinforceSuffix = chatColors[Math.min(chatColors.length-1, current/4)] + "+" + current + "/" + max;

        existingLore.removeIf(line -> line.startsWith(reinforcePrefix));

        if (current > 0) {
            existingLore.add(0, reinforcePrefix + reinforceSuffix);
        }
    }
    
    public int getItemMaxReinforceAmount() {
        return reinforceAmount;
    }
    
    public int getItemReinforceAmount(ItemStack item) {
        return item.getPersistentDataContainer().getOrDefault(
                NAMESPACED_KEY, PersistentDataType.INTEGER, 0);
    }
    
    public double getRangedWeaponBonus(ItemStack item) {
        return rangedWeaponPowerBonus * getItemReinforceAmount(item);
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

                updateLore(lore, finalReinforceAmount, maxReinforceAmount);
                itemMeta.setLore(lore);

                // Get existing modifiers
                Multimap<Attribute, AttributeModifier> existingModifiers = itemMeta.getAttributeModifiers();
                
                if(existingModifiers == null)
                    existingModifiers = HashMultimap.create();

                // Create a copy of the existing modifiers to safely modify
                Multimap<Attribute, AttributeModifier> newModifiers = HashMultimap.create();

                for(UnitModifier modifier : modifiers) {
                    newModifiers.put(modifier.getAttribute(), modifier.getModifier(finalReinforceAmount));
                }

                // Iterate through the existing modifiers and remove the ones matching the key pattern
                for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : existingModifiers.asMap().entrySet()) {
                    Attribute attribute = entry.getKey();
                    ArrayList<AttributeModifier> modifiers = new ArrayList<>(entry.getValue());

                    // Remove any modifiers with keys starting with "reinforce_stat_"
                    modifiers.removeIf(modifier -> modifier.getKey().getKey().startsWith("reinforce_stat_"));
                    if (!modifiers.isEmpty()) {
                        newModifiers.putAll(attribute, modifiers);
                    }
                }
                
                itemMeta.setAttributeModifiers(newModifiers);
                item.setItemMeta(itemMeta);
            }
        });
        Bukkit.getPluginManager().callEvent(new ReinforceEvent(item));
        return check.get();
    }
}

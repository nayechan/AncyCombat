package com.nayechan.combat.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ReinforceGUI {
    private Inventory gui;

    public ReinforceGUI() {
        gui = Bukkit.createInventory(null, 27, getTitle());

        // Placeholder slots
        ItemStack placeholder = createPlaceholder();
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, placeholder);
        }

        // Central slot for the item to be reinforced
        gui.setItem(13, new ItemStack(Material.AIR));
    }

    public static String getTitle()
    {
        return ChatColor.DARK_GRAY + "아이템 강화";
    }

    public void open(Player player) {
        player.openInventory(gui);
    }

    private ItemStack createPlaceholder() {
        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "빈 슬롯");
        placeholder.setItemMeta(meta);
        return placeholder;
    }    
}
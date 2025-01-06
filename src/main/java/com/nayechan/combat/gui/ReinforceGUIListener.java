package com.nayechan.combat.gui;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.mechanics.reinforce.ReinforceMechanic;
import com.nayechan.combat.mechanics.reinforce.ReinforceMechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReinforceGUIListener implements Listener {
    private final AncyCombat plugin;
    
    public ReinforceGUIListener(AncyCombat plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory clickedInventory = event.getInventory();
        if (!event.getView().getTitle().equals(ReinforceGUI.getTitle())) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        updateReinforceButton(clickedInventory, clickedInventory.getItem(13));
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory clickedInventory = event.getInventory();
        
        if(clickedInventory.getItem(13) != null)
            player.getInventory().addItem(clickedInventory.getItem(13));
    }

    // TODO : drag not working rn
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getInventory();
        if (!event.getView().getTitle().equals(ReinforceGUI.getTitle())) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null) {
            if(clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
            else if(clickedItem.getType() == Material.ANVIL) {
                event.setCancelled(true);                
            }
        }

        if (event.getSlot() == 13 || event.isShiftClick()) {
            plugin.getScheduler().runTask(plugin, ()->{                    
                updateReinforceButton(clickedInventory, clickedInventory.getItem(13));
            });
            return;
        }

        if (event.getRawSlot() < clickedInventory.getSize()) {
            event.setCancelled(true);
        }
        
        if (event.getSlot() == 22) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = clickedInventory.getItem(13);
            ReinforceMechanicFactory reinforceMechanicFactory =
                    (ReinforceMechanicFactory) MechanicsManager.getMechanicFactory("reinforce");
            if(reinforceMechanicFactory != null && item != null) {
                ReinforceMechanic mechanic = reinforceMechanicFactory.getMechanic(item);
                if(mechanic != null) {
                    int currentLevel = mechanic.getItemReinforceAmount(item);
                    double roll = Math.random();
                    if(currentLevel >= mechanic.getItemMaxReinforceAmount()) {
                        player.sendMessage(ChatColor.WHITE + "이미 최대치까지 강화된 아이템입니다.");
                    }
                    else if(roll < calculateSuccessChance(currentLevel)) {
                        mechanic.setItemReinforceAmount(player, item, currentLevel+1);
                        player.sendMessage(ChatColor.GREEN + "강화 성공! +"+(currentLevel+1));
                    }
                    else if(roll >= 1-calculateDestroyChance(currentLevel)) {
                        clickedInventory.setItem(13, new ItemStack(Material.AIR));
                        player.sendMessage(ChatColor.DARK_GRAY + "아이템이 파괴됨...");
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "강화 실패...");
                    }
                    
                }
            }

            updateReinforceButton(clickedInventory, clickedInventory.getItem(13));
        }
    }


    private ItemStack createReinforceButton(
            int currentLevel, int maxLevel, double successChance, double destroyChance, int requiredGold) {
        ItemStack reinforceButton = new ItemStack(Material.ANVIL);
        ItemMeta meta = reinforceButton.getItemMeta();

        // 버튼 이름
        meta.setDisplayName(ChatColor.YELLOW + "강화 시작");

        // 버튼 설명 (Lore)
        List<String> lore = new ArrayList<>();
        if(currentLevel < maxLevel) {
            lore.add(ChatColor.GRAY + "현재 단계: " + ChatColor.GREEN + currentLevel);
            lore.add(ChatColor.GRAY + "다음 단계: " + ChatColor.GREEN + (currentLevel + 1));
            lore.add("");
            lore.add(ChatColor.AQUA + "비용: " + ChatColor.WHITE + requiredGold + "G");
            lore.add(ChatColor.GOLD + "성공 확률: " + ChatColor.GREEN + String.format("%.1f%%", successChance * 100));
            if (destroyChance > 0) {
                lore.add(ChatColor.RED + "파괴 확률: " + ChatColor.RED + String.format("%.1f%%", destroyChance * 100));
            }
            lore.add(ChatColor.DARK_GRAY + "(강화 실패 시 강화 수치는 유지됩니다)");
        }
        else {
            if(currentLevel == 0 && maxLevel == 0)
                lore.add(ChatColor.RED + "강화가 불가능한 아이템입니다!");
            else
                lore.add(ChatColor.RED + "이미 최대 레벨입니다!");
        }

        meta.setLore(lore);
        reinforceButton.setItemMeta(meta);

        return reinforceButton;
    }

    public void updateReinforceButton(Inventory gui, ItemStack item) {
        int currentLevel = 0;
        int maxLevel = 0;

        ReinforceMechanicFactory reinforceMechanicFactory = 
                (ReinforceMechanicFactory) MechanicsManager.getMechanicFactory("reinforce");
        
        if(reinforceMechanicFactory != null) {
            ReinforceMechanic mechanic = reinforceMechanicFactory.getMechanic(item);
            if(mechanic != null) {
                currentLevel = mechanic.getItemReinforceAmount(item);
                maxLevel = mechanic.getItemMaxReinforceAmount();                
            }            
        }
        
        double successChance = calculateSuccessChance(currentLevel);
        double destroyChance = calculateDestroyChance(currentLevel);
        int requiredGold = calculateRequiredGold(currentLevel);

        // 강화 버튼 갱신
        ItemStack reinforceButton = createReinforceButton(
                currentLevel, maxLevel, successChance, destroyChance, requiredGold);
        
        gui.setItem(22, reinforceButton);
    }

    double calculateSuccessChance(int level) {
        return Math.max(Math.pow(0.9, level), 0.1);
    }

    double calculateDestroyChance(int level) {
        if (level < 10) return 0.0;
        return Math.min(0.1, level*0.005-0.02);
    }

    int calculateRequiredGold(int level) {
        return (int)(Math.pow((level+2), 1.6) * 32); // 단계별 재료 증가
    }
}

package com.nayechan.combat.listeners;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.scoreboard.ScoreBoardController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.sql.SQLException;

public class PlayerHealthListener implements Listener {

    private final AncyCombat plugin;
    private final ScoreBoardController scoreBoardController;

    public PlayerHealthListener(AncyCombat plugin) throws SQLException {
        this.plugin = plugin;
        this.scoreBoardController = plugin.getScoreBoardController();
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        Player player = event.getPlayer();
        scoreBoardController.updateScoreboard(player);
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        scoreBoardController.updateScoreboard(player);
    }

    @EventHandler
    public void onPlayerHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            scoreBoardController.updateScoreboard(player);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        scoreBoardController.updateScoreboard(player);
    }

    @EventHandler
    public void onPlayerEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        scoreBoardController.updateScoreboard(player);
    }
    
    @EventHandler
    public void onPlayerEquipItem(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        scoreBoardController.updateScoreboard(player);
    }

    @EventHandler
    public void onPlayerEquipItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        scoreBoardController.updateScoreboard(player);
    }
}

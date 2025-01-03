package com.nayechan.combat.listeners;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.scoreboard.ScoreBoardController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

import static java.lang.Math.sqrt;

public class DurabilityListener implements Listener {
    private final AncyCombat plugin;
    private final ScoreBoardController scoreBoardController;

    public DurabilityListener(AncyCombat plugin){
        this.plugin = plugin;
        this.scoreBoardController = plugin.getScoreBoardController();
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        // Reduce durability loss
        int originalDamage = event.getDamage();
        int reducedDamage = (int)Math.min(originalDamage, sqrt(originalDamage*10)); // Halve durability loss, minimum 1

        event.setDamage(reducedDamage);
    }
}

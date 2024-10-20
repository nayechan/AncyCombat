package com.nayechan.combat.listeners;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.model.CharacterData;
import com.nayechan.combat.model.CharacterStat;
import com.nayechan.combat.scoreboard.ScoreBoardController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.sql.SQLException;

public class PlayerDamageListener implements Listener {
    private final AncyCombat plugin;
    private final ScoreBoardController scoreBoardController;

    public PlayerDamageListener(AncyCombat plugin) throws SQLException{
        this.plugin = plugin;
        this.scoreBoardController = plugin.getScoreBoardController();
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            final var uuid = player.getUniqueId();
            final var damage = event.getDamage();
            
            try {
                CharacterData characterData = plugin.getDatabaseManager().getCharacterData(uuid);
                if (characterData == null) {
                    throw new Exception("Character data is missing.");
                }
                CharacterStat stat = characterData.getStat();
                if (stat == null) {
                    throw new Exception("Character stat is missing.");
                }
                
                var reduction = stat.calculateReduction(damage);
                double finalDamage = Math.round((1.0 - reduction) * damage);
                
                event.setDamage(finalDamage);
                scoreBoardController.updateScoreboard(player);
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
        }        
    }
}

package com.nayechan.combat.listeners;

import com.j256.ormlite.dao.Dao;
import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.model.CharacterData;
import com.nayechan.combat.model.CharacterStat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerRegenerateListener implements Listener {
    private final AncyCombat plugin;

    public PlayerRegenerateListener(AncyCombat plugin) throws SQLException {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerHeal(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            final var uuid = player.getUniqueId();

            try {
                CharacterData characterData = plugin.getDatabaseManager().getCharacterData(uuid);
                if (characterData == null) {
                    throw new Exception("Character data is missing.");
                }
                CharacterStat stat = characterData.getStat();
                if (stat == null) {
                    throw new Exception("Character stat is missing.");
                }

                // Calculate the new regeneration amount
                double regenerateAmount = calculateRegenerateAmount(stat, event.getAmount(), player);
                event.setAmount(regenerateAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }         
        }        
    }

    private double calculateRegenerateAmount(CharacterStat stat, double originalAmount, Player player) {
        double regenerateAmount = stat.getRegenerationMultiplier() * originalAmount;
        if (player.getAbsorptionAmount() > 0) {
            regenerateAmount *= (1 + Math.log(1 + player.getAbsorptionAmount()));
        }
        return regenerateAmount;
    }
}

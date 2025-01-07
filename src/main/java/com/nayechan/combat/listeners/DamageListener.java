package com.nayechan.combat.listeners;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.mechanics.rangedweapon.RangedWeaponMechanic;
import com.nayechan.combat.mechanics.rangedweapon.RangedWeaponMechanicFactory;
import com.nayechan.combat.mechanics.reinforce.ReinforceMechanic;
import com.nayechan.combat.mechanics.reinforce.ReinforceMechanicFactory;
import com.nayechan.combat.models.CharacterData;
import com.nayechan.combat.models.CharacterStat;
import com.nayechan.combat.scoreboard.ScoreBoardController;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;
import java.util.logging.Level;

public class DamageListener implements Listener {
    private static final double BOW_MAX_DAMAGE = 1000000000;
    private final AncyCombat plugin;
    private final ScoreBoardController scoreBoardController;

    public DamageListener(AncyCombat plugin){
        this.plugin = plugin;
        this.scoreBoardController = plugin.getScoreBoardController();
    }

    @EventHandler
    public void onTakeDamage(EntityDamageEvent event) {
        double damage = event.getDamage();

        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            damage = handleArrowDamage(entityDamageByEntityEvent, damage);
        }

        if (event.getEntity() instanceof Player player) {
            damage = handlePlayerDamage(player, damage);
        }

        event.setDamage(damage);
    }

    private double handleArrowDamage(EntityDamageByEntityEvent event, double damage) {
        try {
            if (event.getDamager() instanceof Arrow arrow) {
                ProjectileSource shooter = arrow.getShooter();
                if (shooter instanceof Player player) {
                    UUID playerId = player.getUniqueId();
                    CharacterData characterData = plugin.getDatabaseManager().getCharacterData(playerId);
                    if (characterData != null) {
                        damage *= 1 + (characterData.getStat().getStatAtk() / 10.0);
                    }

                    RangedWeaponMechanicFactory rangedWeaponMechanicFactory =
                            (RangedWeaponMechanicFactory) MechanicsManager.getMechanicFactory("ranged_weapon");

                    ReinforceMechanicFactory reinforceMechanicFactory =
                            (ReinforceMechanicFactory) MechanicsManager.getMechanicFactory("reinforce");
                    
                    if(rangedWeaponMechanicFactory != null && reinforceMechanicFactory != null) {
                        ItemStack mainhandItem = player.getInventory().getItemInMainHand();
                        RangedWeaponMechanic rangedWeaponMechanic = 
                                rangedWeaponMechanicFactory.getMechanic(mainhandItem);
                        
                        if(rangedWeaponMechanic != null) {
                            double power = rangedWeaponMechanic.getPower(mainhandItem);
                            damage *= power;                            
                        }
                        
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to handle arrow damage: ", e);
        }

        if(damage > BOW_MAX_DAMAGE)
            damage = BOW_MAX_DAMAGE;
        
        return damage;
    }

    private double handlePlayerDamage(Player player, double damage) {
        try {
            UUID uuid = player.getUniqueId();
            CharacterData characterData = plugin.getDatabaseManager().getCharacterData(uuid);
            if (characterData != null) {
                CharacterStat stat = characterData.getStat();
                if (stat != null) {
                    double reduction = stat.calculateReduction(damage);
                    damage = Math.round((1.0 - reduction) * damage);
                    scoreBoardController.updateScoreboard(player);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to handle player damage: ", e);
        }
        return damage;
    }
}

package com.nayechan.combat.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.scoreboard.ScoreBoardController;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;


@DatabaseTable(tableName = "character_data")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterData {

    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private CharacterStat stat;
    
    private boolean dirty;
    
    public CharacterData(UUID uuid) {
        this.uuid = uuid;
        stat = new CharacterStat();
        dirty = false;
    }
    
    public void update(){
        setDirty(true);
        updatePlayerAttributes();
        refreshScoreboard();
    }

    // Save both the CharacterData and the CharacterStat
    public void save() {
        try {
            var databaseManager = AncyCombat.getInstance().getDatabaseManager();
            Dao<CharacterData, UUID> characterDataDao = databaseManager.getDao(CharacterData.class);      
            Dao<CharacterStat, UUID> characterStatDao = databaseManager.getDao(CharacterStat.class);
            
            characterStatDao.createOrUpdate(stat);
            characterDataDao.createOrUpdate(this);
            
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to refresh the scoreboard
    public void refreshScoreboard() {
        Player player = Bukkit.getPlayer(uuid);  // Get the player by UUID
        if (player != null) {
            // Retrieve the ScoreBoardManager
            ScoreBoardController scoreBoardController = AncyCombat.getInstance().getScoreBoardController();

            if (scoreBoardController != null) {
                // Perform the scoreboard update on the main thread
                scoreBoardController.updateScoreboard(player);
            } else {
                AncyCombat.getInstance().getLogger().warning("ScoreBoardManager is not initialized.");
            }
        } else {
            AncyCombat.getInstance().getLogger().warning("Player with UUID " + uuid + " is not online.");
        }
        
    }

    // Method to update player attributes based on CharacterStat
    public void updatePlayerAttributes() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            AttributeInstance attackAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

            if (attackAttribute != null) {
                updateAttribute(
                        attackAttribute,
                        "attackModifier",
                        stat.getStatAtk() / 10.0
                );
            }
            if (healthAttribute != null) {
                // Example: Each VIT point gives 2 health points
                updateAttribute(
                        healthAttribute,
                        "healthModifier",
                        stat.getStatVit() / 10.0
                );
            }
        }     
    }

    // Helper method to update a specific attribute
    private void updateAttribute(
            AttributeInstance attributeInstance, String modifierName, double value) {
        if (attributeInstance == null) return;
        
        var modifierNamespacedKey = NamespacedKey.fromString(
            modifierName,
            AncyCombat.getInstance()
        );
        
        
        if(modifierNamespacedKey == null)
        {
            modifierNamespacedKey = new NamespacedKey(
                AncyCombat.getInstance(),
                modifierName
            );
        }
        
        if(attributeInstance.getModifier(modifierNamespacedKey) != null)
            attributeInstance.removeModifier(modifierNamespacedKey);

        // Create and add a new modifier
        AttributeModifier newModifier = new AttributeModifier(
                modifierNamespacedKey,
                value,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
        );
        attributeInstance.addModifier(newModifier);
    }
    
    public void SetDirty()
    {
        dirty = true;
    }
    
    public boolean IsDirty(){return dirty;}
}

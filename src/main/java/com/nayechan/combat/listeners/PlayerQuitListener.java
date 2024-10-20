package com.nayechan.combat.listeners;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.model.CharacterData;
import com.nayechan.combat.utility.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class PlayerQuitListener implements Listener {
    private final AncyCombat plugin;

    public PlayerQuitListener(AncyCombat plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer(); // Get the player from the event
        DatabaseManager database = plugin.getDatabaseManager();
        
        final var uuid = player.getUniqueId();
        
        CharacterData characterData;
            
        try {
            characterData = database.getCharacterData(uuid);
            
            if(characterData.IsDirty()) {
                characterData.save();                
            }
            
            database.removeCharacterDataFromCache(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

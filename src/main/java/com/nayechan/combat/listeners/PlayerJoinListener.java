package com.nayechan.combat.listeners;

import com.j256.ormlite.dao.Dao;
import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.model.CharacterData;
import com.nayechan.combat.utility.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final AncyCombat plugin;

    public PlayerJoinListener(AncyCombat plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer(); // Get the player from the event
            DatabaseManager database = plugin.getDatabaseManager();

            final var uuid = player.getUniqueId();
            database.getCharacterData(uuid);

            plugin.getScoreBoardController().initializeScoreboard(player);
        }
        catch (Exception e) {
            e.printStackTrace();
            
        }

    }
}

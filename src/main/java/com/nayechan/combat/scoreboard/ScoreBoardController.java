package com.nayechan.combat.scoreboard;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreBoardController {
    private final Map<UUID, ScoreBoardData> playerScoreboardData = new HashMap<>();

    // Method to initialize scoreboard for a player
    public void initializeScoreboard(Player player) {
        ScoreBoardData newData = new ScoreBoardData(player);
        playerScoreboardData.put(player.getUniqueId(), newData);
    }
    
    public void updateScoreboard(Player player) {
        ScoreBoardData controller;
        if(player != null)
        {
            var scoreBoard = GetScoreboard(player);
            
            if(scoreBoard != null) {
                scoreBoard.updateScoreboard(player);
            }
        }        
    }
    
    public ScoreBoardData GetScoreboard(Player player) {
        return playerScoreboardData.get(player.getUniqueId());
    }
}


package com.nayechan.combat.listeners;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.models.CharacterData;
import com.nayechan.combat.models.CharacterStat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.sql.SQLException;

public class PlayerExperienceGainListener implements Listener {
    private final AncyCombat plugin;

    public PlayerExperienceGainListener(AncyCombat plugin) throws SQLException {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerGainExp(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        final var uuid = player.getUniqueId();
        try {
            CharacterData characterData = plugin.getDatabaseManager().getCharacterData(uuid);
            CharacterStat stat = characterData.getStat();
            if (stat == null) {
                throw new Exception("Character stat is missing.");
            }
            stat.gainExp(event.getAmount());
            plugin.getDatabaseManager().updateCharacterData(characterData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

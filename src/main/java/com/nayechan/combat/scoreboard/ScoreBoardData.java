package com.nayechan.combat.scoreboard;

import com.j256.ormlite.dao.Dao;
import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.model.CharacterData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.UUID;

public class ScoreBoardData {
    private final Objective currentStatusObjective;
    private final HashMap<String, String> scoreEntries;
    private final NumberFormat formatter = NumberFormat.getNumberInstance();
    private final Scoreboard board;

    // Constructor that sets up the scoreboard
    public ScoreBoardData(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();
        player.setScoreboard(board);
        currentStatusObjective = board.registerNewObjective(
                "PlayerStatus_" + player.getUniqueId(),
                Criteria.DUMMY,
                Component.text("MSR Minecraft Server", NamedTextColor.YELLOW, TextDecoration.BOLD)
        );
        currentStatusObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        scoreEntries = new HashMap<>();

        updateScoreboard(player);
    }

    public void updateScoreboard(Player player) {
        var plugin = AncyCombat.getInstance();
        plugin.getScheduler().runTaskAsynchronously(plugin,()->{
            try {
                var characterData = plugin.getDatabaseManager().getCharacterData(player.getUniqueId());
                var characterStat = characterData.getStat();

                var currentCombatLv = characterStat.getCombatLevel();
                var ap = characterStat.getAp();
                var statAtk = characterStat.getStatAtk();
                var statDef = characterStat.getStatDef();
                var statInt = characterStat.getStatInt();
                var statVit = characterStat.getStatVit();

                String apText = "AP: " + ap + "/" + ((currentCombatLv - 1) * 4);
                String atkText = ChatColor.RED + "ATK: " + ChatColor.WHITE + statAtk;
                String defText = ChatColor.GREEN + "DEF: " + ChatColor.WHITE + statDef;
                String vitText = ChatColor.DARK_PURPLE + "VIT: " + ChatColor.WHITE + statVit;
                String intText = ChatColor.AQUA + "INT: " + ChatColor.WHITE + statInt;

                double experience = characterStat.getCurrentExp();
                double maxExp = characterStat.getMaxExp();
                double expPercentage = experience / maxExp * 100.0;
                expPercentage = (double) Math.round(expPercentage * 100) / 100;
                String levelText = "Lv. " + currentCombatLv +
                        " (" + formatter.format(experience) + "/" + formatter.format(maxExp) + ")";

                var currentMana = characterStat.getCurrentMana();
                var maxMana = characterStat.calculateMaxMana();
                String manaText = ChatColor.BLUE + "MP: " + currentMana + "/" + maxMana;

                plugin.getScheduler().runTask(plugin, ()->{var currentHealth = player.getHealth();
                    var maxHealth = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
                    String healthText = ChatColor.RED + "HP: " + Math.ceil(currentHealth) + "/" + Math.ceil(maxHealth);

                    updateOrCreateScore("Nick", player.getName(), 9);
                    updateOrCreateScore("health", healthText, 8);
                    updateOrCreateScore("mana", manaText, 7);
                    updateOrCreateScore("level", levelText, 6);
                    updateOrCreateScore("ap", apText, 5);
                    updateOrCreateScore("atk", atkText, 4);
                    updateOrCreateScore("def", defText, 3);
                    updateOrCreateScore("int", intText, 2);
                    updateOrCreateScore("vit", vitText, 1);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
            }            
        });
    }

    private void updateOrCreateScore(String key, String text, int scoreValue) {
        if (scoreEntries.containsKey(key) && scoreEntries.get(key).equals(text)) {
            return; // No need to update if the text is the same
        }

        if (scoreEntries.containsKey(key)) {
            board.resetScores(scoreEntries.get(key)); // Reset old score
        }

        scoreEntries.put(key, text); // Store new score entry
        Score score = currentStatusObjective.getScore(text);
        score.setScore(scoreValue); // Set new score
    }
}

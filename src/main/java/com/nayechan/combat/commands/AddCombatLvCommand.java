package com.nayechan.combat.commands;

import com.j256.ormlite.dao.Dao;
import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.model.CharacterData;
import com.nayechan.combat.model.CharacterStat;
import com.nayechan.combat.utility.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AddCombatLvCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return false;
        }

        // Ensure the correct number of arguments
        if (args.length != 2) {
            sender.sendMessage("Usage: /addcombatlv <player> <amount>");
            return false;
        }

        // Get the target player and validate
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        // Parse the amount of levels to add
        int levelsToAdd;
        try {
            levelsToAdd = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Level amount must be a number.");
            return false;
        }

        try {
            // Retrieve the CharacterData for the target player
            var databaseManager = AncyCombat.getInstance().getDatabaseManager();
            CharacterData characterData = databaseManager.getCharacterData(targetPlayer.getUniqueId());
            
            if (characterData == null) {
                sender.sendMessage("No character data found for " + targetPlayer.getName());
                return false;
            }

            // Retrieve the CharacterStat
            CharacterStat stat = characterData.getStat();
            if (stat == null) {
                sender.sendMessage("Character stat is missing.");
                return false;
            }

            // Increase the combat level
            for (int i = 0; i < levelsToAdd; i++) {
                stat.levelUp(); // Assuming levelUp method increases the level and updates stats accordingly
            }
            //stat.save();

            // Save the updated data
            databaseManager.updateCharacterData(characterData);

            // Notify success
            sender.sendMessage("Updated stats for " + targetPlayer.getName());
        } catch (Exception e) {
            sender.sendMessage("An error occurred: " + e.getMessage());
            return false;
        }

        return true;
    }
}

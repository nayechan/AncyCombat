package com.nayechan.combat.commands;

import com.j256.ormlite.dao.Dao;
import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.model.CharacterData;
import com.nayechan.combat.model.CharacterStat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ReduceManaCommand implements CommandExecutor {
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
            sender.sendMessage("Usage: /reducemana <player> <amount>");
            return false;
        }

        // Get the target player and validate
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return false;
        }

        // Parse the amount of mana to reduce
        long manaToReduce;
        try {
            manaToReduce = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Mana amount must be a number.");
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

            // Reduce the mana
            stat.setCurrentMana(stat.getCurrentMana() - manaToReduce);
            if (stat.getCurrentMana() < 0) {
                stat.setCurrentMana(0);
            }
            //stat.save();

            // Save the updated data
            databaseManager.updateCharacterData(characterData);

            // Notify success
            sender.sendMessage("Reduced mana for " + targetPlayer.getName() + " by " + manaToReduce + ".");
        } catch (Exception e) {
            sender.sendMessage("An error occurred: " + e.getMessage());
            return false;
        }

        return true;
    }
}

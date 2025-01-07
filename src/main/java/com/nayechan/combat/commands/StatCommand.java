package com.nayechan.combat.commands;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.models.CharacterData;
import com.nayechan.combat.models.CharacterStat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class StatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        var databaseManager = AncyCombat.getInstance().getDatabaseManager();

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        CharacterData characterData = null;
        try {
            characterData = databaseManager.getCharacterData(playerUUID);
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (characterData == null) {
            player.sendMessage("Character data not found.");
            return false;
        }

        if (args.length != 2) {
            player.sendMessage("Usage: /" + label + " <stat_type> <amount>");
            return false;
        }

        String statType = args[0].toLowerCase();
        long amount;

        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Amount must be a number.");
            return false;
        }

        CharacterStat characterStat = characterData.getStat();
        long availableAp = characterStat.getAp();
        
        if (amount < 0) {
            player.sendMessage(ChatColor.RED + "잘못된 스탯 양입니다." + availableAp);
            return false;
        }

        if (amount > availableAp) {
            player.sendMessage(ChatColor.RED + "충분한 AP가 없습니다. 현재 AP : " + availableAp);
            return false;
        }

        switch (statType) {
            case "atk":
                characterStat.setStatAtk(characterStat.getStatAtk() + amount);
                break;
            case "int":
                characterStat.setStatInt(characterStat.getStatInt() + amount);
                break;
            case "def":
                characterStat.setStatDef(characterStat.getStatDef() + amount);
                break;
            case "vit":
                characterStat.setStatVit(characterStat.getStatVit() + amount);
                break;
            default:
                player.sendMessage("Invalid stat type. Valid types: atk, int, def, vit.");
                return false;
        }

        characterStat.setAp(availableAp - amount);

        try {
            databaseManager.updateCharacterData(characterData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.sendMessage("Distributed " + amount + " points to " + statType + ". Remaining AP: " + characterStat.getAp());

        return true;
    }
}

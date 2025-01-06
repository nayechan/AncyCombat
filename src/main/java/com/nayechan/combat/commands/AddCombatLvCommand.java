package com.nayechan.combat.commands;

import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.models.CharacterData;
import com.nayechan.combat.models.CharacterStat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddCombatLvCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args
    ) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"플레이어만 이 명령어를 사용할 수 있습니다.");
            return false;
        }

        // Ensure the correct number of arguments
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED+"명령어 사용법: /addcombatlv <player> <amount>");
            return false;
        }

        // Get the target player and validate
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED+"해당 플레이어를 찾을 수 없습니다.");
            return false;
        }

        // Parse the amount of levels to add
        int levelsToAdd;
        try {
            levelsToAdd = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED+"잘못된 숫자 형식입니다.");
            return false;
        }

        try {
            // Retrieve the CharacterData for the target player
            var databaseManager = AncyCombat.getInstance().getDatabaseManager();
            CharacterData characterData = databaseManager.getCharacterData(targetPlayer.getUniqueId());
            
            if (characterData == null) {
                sender.sendMessage(ChatColor.RED+"다음 플레이어의 데이터가 존재하지 않습니다 : " + targetPlayer.getName());
                return false;
            }

            // Retrieve the CharacterStat
            CharacterStat stat = characterData.getStat();
            if (stat == null) {
                sender.sendMessage(ChatColor.RED+"플레이어의 스탯이 존재하지 않습니다.");
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
            sender.sendMessage(ChatColor.GREEN+"다음 플레이어에 대한 스탯을 업데이트 했습니다 : " + targetPlayer.getName());
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED+"오류가 발생했습니다 : " + e.getMessage());
            return false;
        }

        return true;
    }
}

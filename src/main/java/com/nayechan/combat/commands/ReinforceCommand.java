package com.nayechan.combat.commands;

import com.nayechan.combat.listeners.DurabilityListener;
import com.nayechan.combat.mechanics.ReinforceMechanic;
import com.nayechan.combat.mechanics.ReinforceMechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ReinforceCommand implements CommandExecutor{

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return false;
        }

        Player player = (Player) sender;

        // Ensure the player is holding an item
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must be holding an item to reinforce.");
            return false;
        }

        // Ensure the player provided a reinforce amount argument
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /reinforce <amount>");
            return false;
        }

        // Parse the provided amount
        int amount;
        try {
            amount = Integer.parseInt(args[0]);
            if(amount <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid reinforce amount. Please enter a valid number.");
            return false;
        }
        
        ReinforceMechanicFactory reinforceMechanicFactory = 
                (ReinforceMechanicFactory) MechanicsManager.getMechanicFactory("reinforce");
        
        ReinforceMechanic reinforceMechanic = reinforceMechanicFactory.getMechanic(item);
        
        int maxReinforceAmount = reinforceMechanic.getItemMaxReinforceAmount();
        int nextReinforceAmount = reinforceMechanic.getReinforceAmount();
        nextReinforceAmount = Math.min(maxReinforceAmount, nextReinforceAmount+amount);
        
        reinforceMechanic.setItemReinforceAmount(player, item, nextReinforceAmount);

        // Provide feedback to the player
        player.sendMessage(ChatColor.GREEN + "Item reinforced to " + ChatColor.GOLD + nextReinforceAmount + ChatColor.GREEN + "!");
        return true;
    }
}

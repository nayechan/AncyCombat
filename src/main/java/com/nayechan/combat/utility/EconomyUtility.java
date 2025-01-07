package com.nayechan.combat.utility;

import com.nayechan.combat.AncyCombat;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class EconomyUtility {
    public static boolean deposit(Player player, BigDecimal amount) {
        return AncyCombat
                .getEcon()
                .deposit(AncyCombat.getPluginName(), player.getUniqueId(), amount)
                .transactionSuccess();
    }

    public static boolean withdraw(Player player, BigDecimal amount) {
        return AncyCombat
                .getEcon()
                .withdraw(AncyCombat.getPluginName(), player.getUniqueId(), amount)
                .transactionSuccess();
    }

    public static BigDecimal getBalance(Player player) {
        return AncyCombat
                .getEcon()
                .balance(AncyCombat.getPluginName(), player.getUniqueId());
    }
}

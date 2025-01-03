package com.nayechan.combat.mechanics;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ReinforceMechanicManager implements Listener {
    private ReinforceMechanicFactory factory;
    public ReinforceMechanicManager(ReinforceMechanicFactory factory)
    {
        this.factory = factory;
    }

}

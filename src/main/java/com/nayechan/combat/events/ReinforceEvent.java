package com.nayechan.combat.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Handler;

public class ReinforceEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    @Getter
    private final ItemStack item;
    
    public ReinforceEvent(ItemStack item) {
        this.item = item;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}

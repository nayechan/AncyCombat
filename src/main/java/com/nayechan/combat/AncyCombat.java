package com.nayechan.combat;

import com.nayechan.combat.commands.AddCombatLvCommand;
import com.nayechan.combat.commands.ReduceManaCommand;
import com.nayechan.combat.commands.StatCommand;
import com.nayechan.combat.listeners.*;
import com.nayechan.combat.scoreboard.ScoreBoardController;
import com.nayechan.combat.utility.DatabaseManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AncyCombat extends JavaPlugin implements Listener {
    @Getter
    private static AncyCombat instance;
    @Getter
    private ScoreBoardController scoreBoardController;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private BukkitScheduler scheduler;
    
    @Override
    public void onEnable() {
        instance = this;

        try {
            databaseManager = new DatabaseManager();      
            scoreBoardController = new ScoreBoardController();
            scheduler = getServer().getScheduler();       
        }
        catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        List<Listener> listeners = null;
        try {
            listeners = List.of(
                    new PlayerJoinListener(this),
                    new PlayerQuitListener(this),
                    new PlayerDamageListener(this),
                    new PlayerExperienceGainListener(this),
                    new PlayerHealthListener(this),
                    new PlayerRegenerateListener(this)
            );
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        if(listeners != null) {
            for (Listener listener : listeners) {
                Bukkit.getPluginManager().registerEvents(listener, this);
            }
        }
        
        // Register the command executors
        try {
            getCommand("addcombatlv").setExecutor(new AddCombatLvCommand());
            getCommand("reducemana").setExecutor(new ReduceManaCommand());
            getCommand("stat").setExecutor(new StatCommand());
        }
        catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            databaseManager.saveDirtyCharacterData();
            databaseManager.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            instance = null;
        }
    }

    public File getFile(String childFile) {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
        return new File(dataFolder, childFile);
    }
}
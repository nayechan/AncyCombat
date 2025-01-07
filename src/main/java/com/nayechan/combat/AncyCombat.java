package com.nayechan.combat;

import com.nayechan.combat.commands.AddCombatLvCommand;
import com.nayechan.combat.commands.ReduceManaCommand;
import com.nayechan.combat.commands.ReinforceCommand;
import com.nayechan.combat.commands.StatCommand;
import com.nayechan.combat.gui.ReinforceGUIListener;
import com.nayechan.combat.listeners.*;
import com.nayechan.combat.scoreboard.ScoreBoardController;
import com.nayechan.combat.utility.DatabaseManager;
import lombok.Getter;
import net.milkbowl.vault2.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AncyCombat extends JavaPlugin implements Listener {
    @Getter
    private static AncyCombat instance;
    @Getter
    private static Economy econ = null;
    @Getter
    private ScoreBoardController scoreBoardController;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private BukkitScheduler scheduler;
    
    private final static String PLUGIN_NAME = "AncyCombat";
    
    @Override
    public void onEnable() {
        instance = this;

        // Initialize DB and Scoreboard
        try {
            databaseManager = new DatabaseManager();      
            scoreBoardController = new ScoreBoardController();
            scheduler = getServer().getScheduler();

            // Handle already connected players after a reload
            getServer().getOnlinePlayers().forEach(player -> {
                try {
                    // Simulate the onPlayerJoin logic for each player
                    DatabaseManager database = getDatabaseManager();

                    UUID uuid = player.getUniqueId();
                    database.getCharacterData(uuid);

                    getScoreBoardController().initializeScoreboard(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    getServer().getPluginManager().disablePlugin(this);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        
        // Register External API
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
        }
        
        // Register Listeners
        List<Listener> listeners = null;
        List<Event> events = null;
        try {
            listeners = List.of(
                    new PlayerJoinListener(this),
                    new PlayerQuitListener(this),
                    new DamageListener(this),
                    new DurabilityListener(this),
                    new PlayerExperienceGainListener(this),
                    new PlayerHealthListener(this),
                    new PlayerRegenerateListener(this),
                    new OraxenItemListener(this),
                    
                    new ReinforceGUIListener(this)
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
            getCommand("reinforce").setExecutor(new ReinforceCommand());
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
            
            getServer().getOnlinePlayers().forEach(player -> {
                player.setScoreboard(getServer().getScoreboardManager().getNewScoreboard());
            });
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            instance = null;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public File getFile(String childFile) {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
        return new File(dataFolder, childFile);
    }
    
    public static String getPluginName(){
        return PLUGIN_NAME;
    }
}
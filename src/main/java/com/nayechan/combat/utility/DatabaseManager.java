package com.nayechan.combat.utility;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nayechan.combat.AncyCombat;
import com.nayechan.combat.model.CharacterData;
import lombok.Getter;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class DatabaseManager {

    private static final String DATABASE_FILENAME = "database.db";

    @Getter
    private ConnectionSource connectionSource;

    private final Map<Class<?>, Dao<?, ?>> daoMap;
    private final Map<UUID, CharacterData> characterDataMap; // Cache

    public DatabaseManager() throws Exception {

        File pluginFolder = new File(AncyCombat.getInstance().getDataFolder(), "db");
        if (!pluginFolder.exists()) {
            if (!pluginFolder.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + pluginFolder.getAbsolutePath());
            }
        }

        String dbPath = new File(pluginFolder, DATABASE_FILENAME).getAbsolutePath();
        System.out.println("Database path: " + dbPath);  // Debug line

        connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + dbPath);
        daoMap = new HashMap<>();
        characterDataMap = new HashMap<>();

        // Schedule the async database save task
        scheduleAsyncDatabaseSave();
    }

    @SuppressWarnings("unchecked")
    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        if (!daoMap.containsKey(clazz)) {
            Dao<T, ?> dao = DaoManager.createDao(connectionSource, clazz);
            daoMap.put(clazz, dao);
            TableUtils.createTableIfNotExists(connectionSource, clazz);
        }
        return (D) daoMap.get(clazz);
    }

    public void close() throws Exception {
        connectionSource.close();
    }

    /**
     * Retrieve character data for the specified player UUID. Cached if already loaded.
     * @param uuid The UUID of the player.
     * @return The CharacterData for the player.
     * @throws SQLException If any SQL error occurs.
     */
    public CharacterData getCharacterData(UUID uuid) throws SQLException {
        // Check if data is already cached
        if (characterDataMap.containsKey(uuid)) {
            return characterDataMap.get(uuid);
        }

        // If not cached, load from the database
        Dao<CharacterData, UUID> dao = getDao(CharacterData.class);
        CharacterData data = dao.queryForId(uuid);

        if (data != null) {
            // Cache the data if it exists
            characterDataMap.put(uuid, data);
        } else {
            // Optionally handle if no data exists for this UUID (e.g., create new entry)
            data = new CharacterData(uuid);
            data.save();
            characterDataMap.put(uuid, data);
        }

        return data;
    }

    /**
     * Update the CharacterData in the cache and the database.
     * @param characterData The character data to be updated.
     * @throws SQLException If any SQL error occurs.
     */
    public void updateCharacterData(CharacterData characterData) {
        characterData.update();
        characterDataMap.put(characterData.getUuid(), characterData); // Update cache
    }

    private void scheduleAsyncDatabaseSave() {
        AncyCombat.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(
                AncyCombat.getInstance(),
                this::saveDirtyCharacterData,
                0L, 200L // Run every 200 ticks (10 seconds)
        );
    }

    public void saveDirtyCharacterData() {
        System.out.println("Running async save task...");

        characterDataMap.values().forEach(characterData -> {
            if (characterData.IsDirty()) {
                characterData.save();
            }
        });
    }
    
    public void removeCharacterDataFromCache(UUID uuid) {
        characterDataMap.remove(uuid);
    }

}

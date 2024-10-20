package com.nayechan.combat.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nayechan.combat.AncyCombat;
import lombok.Getter;
import lombok.Setter;

import java.security.cert.CertificateParsingException;

@DatabaseTable(tableName = "character_stat")
@Getter
@Setter
public class CharacterStat {

    @DatabaseField(generatedId = true)
    private int id;
    
    @DatabaseField
    private long combatLevel;
    
    @DatabaseField
    private long ap;
    
    @DatabaseField
    private long statAtk;

    @DatabaseField
    private long statInt;
    
    @DatabaseField
    private long statDef;
    
    @DatabaseField
    private long statVit;
    
    @DatabaseField
    private long currentMana;

    @DatabaseField
    private double currentExp;
    
    public CharacterStat()
    {
        combatLevel = 1;
        ap = 0;
        
        statAtk = 0;
        statInt = 0;
        statDef = 0;
        statVit = 0;
        currentMana = calculateMaxMana();
        
        currentExp = 0;
        
    }
    
    public void levelUp()
    {
        ++combatLevel;
        ap+=4;
        currentMana = calculateMaxMana();
    }
    
    public void gainExp(double exp)
    {
        currentExp += exp;
        double maxExp = getMaxExp();
        while(currentExp >= maxExp) {
            currentExp -= maxExp;
            levelUp();
        }
    }
    
    private double calculateMaxExp(long combatLevel)
    {
        return Math.ceil(21 + (combatLevel * 4) + (Math.pow(combatLevel, 1.4) * 1.5));
    }
    
    public long calculateMaxMana()
    {
        return 2 * (statInt + 10);
    }
    
    public double calculateReduction(double rawDamage) {
        final double maxReduction = 0.8;
        final double midpoint = 64;
        final double scalingFactor = 40;
        final double damageFactor = 1.1;
        
        // Adjust midpoint based on raw damage
        double adjustedMidpoint = midpoint + rawDamage / damageFactor;

        // Sigmoid function for reduction calculation
        return maxReduction / (1 + Math.exp(-(statDef - adjustedMidpoint) / scalingFactor));
    }
    
    public double getRegenerationMultiplier()
    {
        return (statInt + 20.0) / 20.0;
    }

    public double getMaxExp() {
        return calculateMaxExp(combatLevel);
    }
}

package game.core;

import java.io.Serializable;

/**
 * Stat represents primary and derived statistics.
 * This class handles all stat calculations and HP management.
 */
public class Stat implements Serializable {

    // ===== BALANCED STAT CONSTANTS =====
    private static final int BASE_HP = 60;
    private static final int HP_PER_STRENGTH = 3;

    private static final double EVASION_SCALER = 1.8;
    private static final double ACCURACY_SCALER = 2.2;
    private static final int BASE_ACCURACY = 75;

    private static final double CDR_SCALER = 0.5;
    private static final double SPEED_SCALER = 1.5;

    private int strength;
    private int agility;
    private int intelligence;

    private int maxHp;
    private int hp;

    private int evasion;
    private int accuracy;
    private int cooldownReduction;
    private int speed;

    public Stat(int strength, int agility, int intelligence) {
        this.strength = Math.max(0, strength);
        this.agility = Math.max(0, agility);
        this.intelligence = Math.max(0, intelligence);
        calculateDerivedStats();
        this.hp = maxHp;
    }

    // ===== DERIVED STAT CALCULATION =====
    private void calculateDerivedStats() {
        this.maxHp = BASE_HP + strength * HP_PER_STRENGTH;
        this.evasion = (int)(Math.sqrt(agility) * EVASION_SCALER);
        this.accuracy = BASE_ACCURACY + (int)(Math.sqrt(intelligence) * ACCURACY_SCALER);

        int intelligenceAboveBase = Math.max(0, intelligence - 20);
        this.cooldownReduction = (int)(Math.sqrt(intelligenceAboveBase) * CDR_SCALER);

        this.speed = (int)(Math.sqrt(agility) * SPEED_SCALER);
    }

    // ===== STAT INCREASE METHODS =====
    public void increaseStrength(int amount) {
        if (amount == 0) return;

        int oldMaxHp = maxHp;
        strength = Math.max(0, strength + amount);
        calculateDerivedStats();

        int hpGain = maxHp - oldMaxHp;
        hp = Math.max(0, Math.min(maxHp, hp + hpGain));
    }

    public void increaseAgility(int amount) {
        if (amount == 0) return;
        agility = Math.max(0, agility + amount);
        calculateDerivedStats();
    }

    public void increaseIntelligence(int amount) {
        if (amount == 0) return;
        intelligence = Math.max(0, intelligence + amount);
        calculateDerivedStats();
    }

    // ===== COMBAT METHODS =====
    public boolean isDead() {
        return hp <= 0;
    }

    public void takeDamage(int damage) {
        if (damage < 0) return;
        hp = Math.max(0, hp - damage);
    }

    public void fullHeal() {
        hp = maxHp;
    }

    // ===== GETTERS =====
    public int getStrength() { return strength; }
    public int getAgility() { return agility; }
    public int getIntelligence() { return intelligence; }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getEvasion() { return evasion; }
    public int getAccuracy() { return accuracy; }
    public int getCooldownReduction() { return cooldownReduction; }
    public int getSpeed() { return speed; }

    @Override
    public String toString() {
        return "Stat{STR=" + strength + ", AGI=" + agility + ", INT=" + intelligence + 
               ", HP=" + hp + "/" + maxHp + "}";
    }
}
package game.core;

import java.io.Serializable;

public class Stat implements Serializable {

    private int strength;
    private int agility;
    private int intelligence;
    
    private int hp;
    private double evasion;     // %
    private double accuracy;    // %
    private int cooldownReduction;  // flat
    private int speed;          // flat integer, scales with agility

    public Stat(int strength, int agility, int intelligence) {
        this.strength = strength;
        this.agility = agility;
        this.intelligence = intelligence;
        calculateDerivedStats();
    }

    private void calculateDerivedStats() {
        this.hp = 50 + strength * 5;
        this.evasion = agility / 5.0;
        this.accuracy = 80 + (intelligence / 5.0) * 2;
        this.cooldownReduction = intelligence / 12;
        this.speed = agility / 2;  // flat integer, e.g., 10 agility → speed 5
    }
    
    public void increaseStrength(int amount) {
        strength += amount;
        calculateDerivedStats();
    }

    public void increaseAgility(int amount) {
        agility += amount;
        calculateDerivedStats();
    }

    public void increaseIntelligence(int amount) {
        intelligence += amount;
        calculateDerivedStats();
    }

    public boolean isDead(int damageTaken) {
        return (hp - damageTaken) <= 0;
    }

    public int getStrength() {
        return strength;
    }

    public int getEvasion() {
        return (int) evasion;
    }

    public int getAccuracy() {
        return (int) accuracy;
    }

    public int getCooldownReduction() {
        return cooldownReduction;
    }

    public int getSpeed() {
        return speed;
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }
}

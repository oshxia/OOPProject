package game.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Player represents the player character.
 * Stores cooldown state for all skills using skill IDs as keys.
 */
public class Player implements Serializable {

    private final String name;
    private final Profession profession;
    private final Stat stats;

    private final Map<String, Integer> skillCooldowns; // key = skill.getId()

    public Player(String name, Profession profession, Stat stats) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (profession == null) {
            throw new IllegalArgumentException("Profession cannot be null");
        }
        if (stats == null) {
            throw new IllegalArgumentException("Stats cannot be null");
        }

        this.name = name.trim();
        this.profession = profession;
        this.stats = stats;
        this.skillCooldowns = new HashMap<>();
    }

    // ======== COMBAT METHODS ========

    public void takeDamage(int damage) {
        if (damage > 0) {
            stats.takeDamage(damage);
        }
    }

    public boolean isDead() {
        return stats.isDead();
    }

    // ======== SKILL COOLDOWN MANAGEMENT ========

    /**
     * Get remaining cooldown for a skill.
     * Uses skill ID for consistent lookup.
     * 
     * @param skill The skill to check
     * @return Remaining cooldown in turns (0 = ready)
     */
    public int getSkillCooldown(Skill skill) {
        if (skill == null) return 0;
        return skillCooldowns.getOrDefault(skill.getId(), 0);
    }

    /**
     * Set cooldown for a skill.
     * Uses skill ID for consistent lookup.
     * 
     * @param skill The skill to set cooldown for
     * @param cooldown The cooldown value (0 or negative removes cooldown)
     */
    public void setSkillCooldown(Skill skill, int cooldown) {
        if (skill == null) return;
        
        String key = skill.getId();
        if (cooldown <= 0) {
            skillCooldowns.remove(key);
        } else {
            skillCooldowns.put(key, cooldown);
        }
    }

    /**
     * Tick all active cooldowns (decrease by 1).
     * Called at the start of each turn.
     */
    public void tickAllCooldowns() {
        skillCooldowns.replaceAll((key, cd) -> Math.max(0, cd - 1));
        skillCooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    /**
     * Reset all cooldowns to 0.
     * Called at the start of a new battle.
     */
    public void resetAllCooldowns() {
        skillCooldowns.clear();
    }

    // ======== GETTERS ========

    public String getName() { return name; }
    public Profession getProfession() { return profession; }
    public Stat getStats() { return stats; }

    @Override
    public String toString() {
        return "Player{" + name + ", " + profession + ", HP=" + stats.getHp() + "/" + stats.getMaxHp() + "}";
    }
    
}
package game.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Enemy represents an enemy character.
 * Stores cooldown state for all skills using skill IDs as keys.
 */
public class Enemy implements Serializable {

    private final String name;
    private final Stat stats;
    private final Skill[] skills; // Can be empty, but never null

    private final Map<String, Integer> skillCooldowns; // key = skill.getId()

    public Enemy(String name, Stat stats) {
        this(name, stats, new Skill[0]); // No skills by default
    }

    public Enemy(String name, Stat stats, Skill[] skills) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Enemy name cannot be null or empty");
        }
        if (stats == null) {
            throw new IllegalArgumentException("Stats cannot be null");
        }

        this.name = name.trim();
        this.stats = stats;
        // Never store null - use empty array instead
        this.skills = (skills != null) ? skills : new Skill[0];
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
    public Stat getStats() { return stats; }
    
    /**
     * Get skills array.
     * Never returns null - returns empty array if no skills.
     */
    public Skill[] getSkills() { 
        return skills; // Never null
    }
    
    /**
     * Check if enemy has any skills.
     */
    public boolean hasSkills() { 
        return skills.length > 0; 
    }

    @Override
    public String toString() {
        return "Enemy{" + name + ", HP=" + stats.getHp() + "/" + stats.getMaxHp() + ", skills=" + skills.length + "}";
    }
    
}
package game.core;

import java.io.Serializable;

/**
 * Skill represents a template for player/enemy abilities.
 * Skills are immutable data objects - cooldown state is stored in Player/Enemy.
 * 
 * DESIGN: Skills are templates that can be shared across multiple entities.
 * Each skill has a stable ID based on its name for consistent lookups.
 */
public class Skill implements Serializable {

    private final String id; // Stable ID based on name
    private final String name;
    private final Profession allowedProfession; // null = any profession can use
    private final int baseDamage;
    private final int baseCooldown;

    /**
     * Creates a new skill template.
     * ID is generated from name to ensure stability across instances.
     * 
     * @param name The skill name (used to generate stable ID)
     * @param allowedProfession Profession restriction (null = no restriction)
     * @param baseDamage Base damage before stat modifiers
     * @param baseCooldown Base cooldown in turns (0 = no cooldown)
     */
    public Skill(String name, Profession allowedProfession, int baseDamage, int baseCooldown) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name cannot be null or empty");
        }
        
        this.name = name.trim();
        this.id = generateStableId(this.name); // Stable ID from name
        this.allowedProfession = allowedProfession;
        this.baseDamage = Math.max(0, baseDamage);
        this.baseCooldown = Math.max(0, baseCooldown);
    }

    /**
     * Generates a stable ID from skill name.
     * Same name always produces same ID.
     */
    private String generateStableId(String name) {
        // Use lowercase name as ID for stability
        // Could use hash if needed, but name works for most cases
        return "skill_" + name.toLowerCase().replace(" ", "_");
    }

    /**
     * Checks if a player meets the profession requirement for this skill.
     * Does NOT check cooldown - that's the responsibility of the system layer.
     * 
     * @param player The player to check
     * @return true if player's profession matches (or no restriction exists)
     */
    public boolean matchesProfession(Player player) {
        if (player == null) return false;
        if (allowedProfession == null) return true; // No restriction
        return player.getProfession() == allowedProfession;
    }

    /**
     * Checks if an enemy can use this skill.
     * Enemies ignore profession restrictions.
     * 
     * @return true (enemies can use any skill)
     */
    public boolean matchesProfession(Enemy enemy) {
        return enemy != null; // Enemies can use any skill
    }

    // ======== GETTERS ========

    public String getId() { return id; }
    public String getName() { return name; }
    public Profession getAllowedProfession() { return allowedProfession; }
    public int getBaseDamage() { return baseDamage; }
    public int getBaseCooldown() { return baseCooldown; }

    // ======== EQUALITY & HASH ========

    /**
     * Skills are equal if they have the same ID.
     * This allows proper comparison after serialization.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Skill other = (Skill) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Skill{" + name + ", dmg=" + baseDamage + ", cd=" + baseCooldown + "}";
    }
}
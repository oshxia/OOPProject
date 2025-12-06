package game.system;

import game.core.Player;
import game.core.Enemy;
import game.core.Skill;

/**
 * SkillSystem manages skill validation, queries, and basic skill operations.
 * Does NOT handle cooldown management or combat execution - those are separate systems.
 * 
 * Responsibilities:
 * - Validate if a skill can be used by an entity
 * - Check profession requirements
 * - Query skill information
 * - Calculate final damage values
 * - Provide skill availability status
 * 
 * Design: Stateless utility system - all methods are queries or calculations.
 * GUI-Friendly: All methods return simple types for easy UI binding.
 */
public class SkillSystem {

    // ===== SKILL VALIDATION =====

    /**
     * Check if a player can use a skill.
     * Validates profession requirement and cooldown status.
     * 
     * @param player The player attempting to use the skill
     * @param skill The skill to check
     * @return true if skill can be used
     */
    public boolean canUseSkill(Player player, Skill skill) {
        if (player == null || skill == null) return false;
        
        // Check profession requirement
        if (!skill.matchesProfession(player)) {
            return false;
        }
        
        // Check cooldown (0 = ready)
        return player.getSkillCooldown(skill) == 0;
    }

    /**
     * Check if an enemy can use a skill.
     * Enemies ignore profession requirements, only checks cooldown.
     * 
     * @param enemy The enemy attempting to use the skill
     * @param skill The skill to check
     * @return true if skill can be used
     */
    public boolean canUseSkill(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return false;
        
        // Enemies can use any skill (no profession check)
        // Only check cooldown
        return enemy.getSkillCooldown(skill) == 0;
    }

    /**
     * Check if a skill is on cooldown for a player.
     * 
     * @param player The player to check
     * @param skill The skill to check
     * @return true if skill is on cooldown
     */
    public boolean isOnCooldown(Player player, Skill skill) {
        if (player == null || skill == null) return false;
        return player.getSkillCooldown(skill) > 0;
    }

    /**
     * Check if a skill is on cooldown for an enemy.
     * 
     * @param enemy The enemy to check
     * @param skill The skill to check
     * @return true if skill is on cooldown
     */
    public boolean isOnCooldown(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return false;
        return enemy.getSkillCooldown(skill) > 0;
    }

    /**
     * Check if a player meets the profession requirement for a skill.
     * 
     * @param player The player to check
     * @param skill The skill to check
     * @return true if profession matches (or no restriction)
     */
    public boolean matchesProfession(Player player, Skill skill) {
        if (player == null || skill == null) return false;
        return skill.matchesProfession(player);
    }

    // ===== COOLDOWN QUERIES =====

    /**
     * Get remaining cooldown for a player's skill.
     * 
     * @param player The player
     * @param skill The skill to check
     * @return Remaining cooldown in turns (0 = ready)
     */
    public int getRemainingCooldown(Player player, Skill skill) {
        if (player == null || skill == null) return 0;
        return player.getSkillCooldown(skill);
    }

    /**
     * Get remaining cooldown for an enemy's skill.
     * 
     * @param enemy The enemy
     * @param skill The skill to check
     * @return Remaining cooldown in turns (0 = ready)
     */
    public int getRemainingCooldown(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return 0;
        return enemy.getSkillCooldown(skill);
    }

    // ===== DAMAGE CALCULATION =====

    /**
     * Calculate final damage a skill would deal from a player.
     * Base damage + player's strength modifier.
     * Does NOT apply the damage - just calculates it.
     * 
     * @param player The player using the skill
     * @param skill The skill being used
     * @return Calculated damage value
     */
    public int calculateDamage(Player player, Skill skill) {
        if (player == null || skill == null) return 0;
        return skill.getBaseDamage() + player.getStats().getStrength();
    }

    /**
     * Calculate final damage a skill would deal from an enemy.
     * Base damage + enemy's strength modifier.
     * Does NOT apply the damage - just calculates it.
     * 
     * @param enemy The enemy using the skill
     * @param skill The skill being used
     * @return Calculated damage value
     */
    public int calculateDamage(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return 0;
        return skill.getBaseDamage() + enemy.getStats().getStrength();
    }

    /**
     * Calculate final cooldown after CDR (Cooldown Reduction) is applied.
     * Minimum cooldown is 1 turn for non-zero base cooldowns.
     * 
     * @param skill The skill to calculate for
     * @param cooldownReduction The CDR stat value
     * @return Final cooldown after CDR (minimum 1 for skills with base CD > 0)
     */
    public int calculateFinalCooldown(Skill skill, int cooldownReduction) {
        if (skill == null) return 0;
        
        int baseCooldown = skill.getBaseCooldown();
        if (baseCooldown == 0) return 0; // No cooldown skills stay at 0
        
        return Math.max(1, baseCooldown - cooldownReduction);
    }

    /**
     * Calculate final cooldown for a player's skill.
     * 
     * @param player The player
     * @param skill The skill
     * @return Final cooldown after player's CDR is applied
     */
    public int calculateFinalCooldown(Player player, Skill skill) {
        if (player == null || skill == null) return 0;
        return calculateFinalCooldown(skill, player.getStats().getCooldownReduction());
    }

    /**
     * Calculate final cooldown for an enemy's skill.
     * 
     * @param enemy The enemy
     * @param skill The skill
     * @return Final cooldown after enemy's CDR is applied
     */
    public int calculateFinalCooldown(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return 0;
        return calculateFinalCooldown(skill, enemy.getStats().getCooldownReduction());
    }

    // ===== SKILL INFORMATION QUERIES =====

    /**
     * Get skill name.
     * 
     * @param skill The skill
     * @return Skill name, or empty string if null
     */
    public String getSkillName(Skill skill) {
        return skill != null ? skill.getName() : "";
    }

    /**
     * Get skill base damage.
     * 
     * @param skill The skill
     * @return Base damage value
     */
    public int getBaseDamage(Skill skill) {
        return skill != null ? skill.getBaseDamage() : 0;
    }

    /**
     * Get skill base cooldown.
     * 
     * @param skill The skill
     * @return Base cooldown in turns
     */
    public int getBaseCooldown(Skill skill) {
        return skill != null ? skill.getBaseCooldown() : 0;
    }

    /**
     * Check if a skill has a cooldown.
     * 
     * @param skill The skill
     * @return true if skill has cooldown > 0
     */
    public boolean hasCooldown(Skill skill) {
        return skill != null && skill.getBaseCooldown() > 0;
    }

    // ===== SKILL AVAILABILITY (GUI HELPER) =====

    /**
     * Get skill availability status for GUI display.
     * Returns a status code for easy UI rendering.
     * 
     * Status codes:
     * 0 = READY (can use)
     * 1 = ON_COOLDOWN (cannot use - cooldown active)
     * 2 = WRONG_PROFESSION (cannot use - wrong profession)
     * 3 = INVALID (skill or player is null)
     * 
     * @param player The player
     * @param skill The skill
     * @return Status code (0-3)
     */
    public int getSkillStatus(Player player, Skill skill) {
        if (player == null || skill == null) return 3; // INVALID
        
        if (!skill.matchesProfession(player)) return 2; // WRONG_PROFESSION
        
        if (player.getSkillCooldown(skill) > 0) return 1; // ON_COOLDOWN
        
        return 0; // READY
    }

    /**
     * Get skill availability status for enemy (GUI helper).
     * 
     * Status codes:
     * 0 = READY (can use)
     * 1 = ON_COOLDOWN (cannot use - cooldown active)
     * 3 = INVALID (skill or enemy is null)
     * 
     * @param enemy The enemy
     * @param skill The skill
     * @return Status code (0, 1, or 3)
     */
    public int getSkillStatus(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return 3; // INVALID
        
        if (enemy.getSkillCooldown(skill) > 0) return 1; // ON_COOLDOWN
        
        return 0; // READY
    }

    /**
     * Get a human-readable status string for a player's skill.
     * Perfect for GUI tooltips or status displays.
     * 
     * @param player The player
     * @param skill The skill
     * @return Status string (e.g., "READY", "Cooldown: 2 turns", "Wrong Profession")
     */
    public String getSkillStatusText(Player player, Skill skill) {
        int status = getSkillStatus(player, skill);
        
        switch (status) {
            case 0:
                return "READY";
            case 1:
                int cd = player.getSkillCooldown(skill);
                return "Cooldown: " + cd + (cd == 1 ? " turn" : " turns");
            case 2:
                return "Wrong Profession";
            case 3:
            default:
                return "Invalid";
        }
    }

    /**
     * Get a human-readable status string for an enemy's skill.
     * 
     * @param enemy The enemy
     * @param skill The skill
     * @return Status string
     */
    public String getSkillStatusText(Enemy enemy, Skill skill) {
        int status = getSkillStatus(enemy, skill);
        
        switch (status) {
            case 0:
                return "READY";
            case 1:
                int cd = enemy.getSkillCooldown(skill);
                return "Cooldown: " + cd + (cd == 1 ? " turn" : " turns");
            case 3:
            default:
                return "Invalid";
        }
    }

    // ===== SKILL COMPARISON =====

    /**
     * Check if two skills are the same (by ID).
     * Useful for GUI selection logic.
     * 
     * @param skill1 First skill
     * @param skill2 Second skill
     * @return true if both skills are the same
     */
    public boolean isSameSkill(Skill skill1, Skill skill2) {
        if (skill1 == null || skill2 == null) return false;
        return skill1.equals(skill2);
    }

    /**
     * Find skill index in an array.
     * Returns -1 if not found.
     * Useful for GUI skill selection.
     * 
     * @param skills Array of skills to search
     * @param targetSkill Skill to find
     * @return Index of skill, or -1 if not found
     */
    public int findSkillIndex(Skill[] skills, Skill targetSkill) {
        if (skills == null || targetSkill == null) return -1;
        
        for (int i = 0; i < skills.length; i++) {
            if (isSameSkill(skills[i], targetSkill)) {
                return i;
            }
        }
        
        return -1;
    }

    // ===== VALIDATION =====

    /**
     * Validate that a skill exists and is usable.
     * 
     * @param skill Skill to validate
     * @return true if skill is valid (not null)
     */
    public boolean isValidSkill(Skill skill) {
        return skill != null;
    }

    /**
     * Validate that a player has access to a skill array.
     * 
     * @param skills Skills array to validate
     * @return true if array exists and has at least one skill
     */
    public boolean hasSkills(Skill[] skills) {
        return skills != null && skills.length > 0;
    }

    /**
     * Validate skill index in array.
     * 
     * @param skills Skills array
     * @param index Index to validate
     * @return true if index is valid
     */
    public boolean isValidSkillIndex(Skill[] skills, int index) {
        return skills != null && index >= 0 && index < skills.length;
    }

    /**
     * Get skill at index safely.
     * Returns null if index is invalid.
     * 
     * @param skills Skills array
     * @param index Index to get
     * @return Skill at index, or null if invalid
     */
    public Skill getSkillAt(Skill[] skills, int index) {
        if (isValidSkillIndex(skills, index)) {
            return skills[index];
        }
        return null;
    }
}
package game.system;

import game.core.Player;
import game.core.Enemy;
import game.core.Skill;

/**
 * CooldownSystem manages all cooldown-related operations for skills.
 * Handles cooldown application, ticking, and resetting.
 * 
 * Responsibilities:
 * - Apply cooldowns after skill use (with CDR calculation)
 * - Tick cooldowns at the start of each turn
 * - Reset cooldowns for new battles
 * - Check skill readiness
 * - Query cooldown state
 * 
 * COOLDOWN LIFECYCLE:
 * 1. READY (cooldown = 0) → Skill can be used
 * 2. USE SKILL → System applies cooldown (baseCooldown - CDR, min 1)
 * 3. ON COOLDOWN (cooldown > 0) → Skill cannot be used
 * 4. TICK (each turn start) → Cooldown decreases by 1
 * 5. BACK TO READY → When cooldown reaches 0
 * 
 * Design: Stateless utility system - all operations on passed entities.
 * GUI-Friendly: All methods return simple types for easy UI binding.
 */
public class CooldownSystem {

    // ===== SKILL READINESS =====

    /**
     * Check if a skill is ready to use (cooldown = 0).
     * 
     * @param player The player
     * @param skill The skill to check
     * @return true if skill is ready (not on cooldown)
     */
    public boolean isSkillReady(Player player, Skill skill) {
        if (player == null || skill == null) return false;
        return player.getSkillCooldown(skill) == 0;
    }

    /**
     * Check if an enemy skill is ready to use.
     * 
     * @param enemy The enemy
     * @param skill The skill to check
     * @return true if skill is ready (not on cooldown)
     */
    public boolean isSkillReady(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return false;
        return enemy.getSkillCooldown(skill) == 0;
    }

    /**
     * Check if any player skills are on cooldown.
     * 
     * @param player The player
     * @param skills Array of skills to check
     * @return true if at least one skill is on cooldown
     */
    public boolean hasActivePlayerCooldowns(Player player, Skill[] skills) {
        if (player == null || skills == null) return false;
        
        for (Skill skill : skills) {
            if (skill != null && player.getSkillCooldown(skill) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if any enemy skills are on cooldown.
     * 
     * @param enemy The enemy
     * @return true if at least one skill is on cooldown
     */
    public boolean hasActiveEnemyCooldowns(Enemy enemy) {
        if (enemy == null || !enemy.hasSkills()) return false;
        
        for (Skill skill : enemy.getSkills()) {
            if (skill != null && enemy.getSkillCooldown(skill) > 0) {
                return true;
            }
        }
        return false;
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

    /**
     * Get all cooldowns for player's skills.
     * Returns array matching skills array length.
     * Perfect for GUI display of all skill cooldowns.
     * 
     * @param player The player
     * @param skills Array of skills
     * @return Array of cooldown values (same order as skills)
     */
    public int[] getAllCooldowns(Player player, Skill[] skills) {
        if (player == null || skills == null) {
            return new int[0];
        }
        
        int[] cooldowns = new int[skills.length];
        for (int i = 0; i < skills.length; i++) {
            cooldowns[i] = getRemainingCooldown(player, skills[i]);
        }
        return cooldowns;
    }

    /**
     * Get all cooldowns for enemy's skills.
     * 
     * @param enemy The enemy
     * @return Array of cooldown values
     */
    public int[] getAllCooldowns(Enemy enemy) {
        if (enemy == null || !enemy.hasSkills()) {
            return new int[0];
        }
        
        Skill[] skills = enemy.getSkills();
        int[] cooldowns = new int[skills.length];
        for (int i = 0; i < skills.length; i++) {
            cooldowns[i] = getRemainingCooldown(enemy, skills[i]);
        }
        return cooldowns;
    }

    // ===== APPLYING COOLDOWNS =====

    /**
     * Apply cooldown to a player's skill after use.
     * Automatically applies CDR (Cooldown Reduction) from player stats.
     * 
     * WHEN TO CALL: Immediately after skill execution, regardless of hit/miss.
     * 
     * Rules:
     * - Skills with baseCooldown = 0 stay at 0 (no cooldown)
     * - Other skills: finalCooldown = max(1, baseCooldown - CDR)
     * 
     * @param player The player who used the skill
     * @param skill The skill that was used
     */
    public void applySkillCooldown(Player player, Skill skill) {
        if (player == null || skill == null) return;
        
        int baseCooldown = skill.getBaseCooldown();
        
        // Skills with no base cooldown stay at 0
        if (baseCooldown == 0) {
            player.setSkillCooldown(skill, 0);
            return;
        }
        
        // Apply CDR, minimum 1 turn
        int cdr = player.getStats().getCooldownReduction();
        int finalCooldown = Math.max(1, baseCooldown - cdr);
        
        player.setSkillCooldown(skill, finalCooldown);
    }

    /**
     * Apply cooldown to an enemy's skill after use.
     * Automatically applies CDR from enemy stats.
     * 
     * @param enemy The enemy who used the skill
     * @param skill The skill that was used
     */
    public void applySkillCooldown(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return;
        
        int baseCooldown = skill.getBaseCooldown();
        
        // Skills with no base cooldown stay at 0
        if (baseCooldown == 0) {
            enemy.setSkillCooldown(skill, 0);
            return;
        }
        
        // Apply CDR, minimum 1 turn
        int cdr = enemy.getStats().getCooldownReduction();
        int finalCooldown = Math.max(1, baseCooldown - cdr);
        
        enemy.setSkillCooldown(skill, finalCooldown);
    }

    /**
     * Manually set cooldown for a player's skill.
     * Use this for special effects or testing.
     * 
     * @param player The player
     * @param skill The skill
     * @param cooldown The cooldown value to set (0 or negative removes cooldown)
     */
    public void setSkillCooldown(Player player, Skill skill, int cooldown) {
        if (player == null || skill == null) return;
        player.setSkillCooldown(skill, cooldown);
    }

    /**
     * Manually set cooldown for an enemy's skill.
     * 
     * @param enemy The enemy
     * @param skill The skill
     * @param cooldown The cooldown value to set
     */
    public void setSkillCooldown(Enemy enemy, Skill skill, int cooldown) {
        if (enemy == null || skill == null) return;
        enemy.setSkillCooldown(skill, cooldown);
    }

    // ===== TICKING COOLDOWNS =====

    /**
     * Tick all cooldowns for player.
     * Decreases all active cooldowns by 1.
     * 
     * WHEN TO CALL: At the START of each turn, before any actions.
     * 
     * @param player The player whose cooldowns to tick
     */
    public void tickPlayerCooldowns(Player player) {
        if (player == null) return;
        player.tickAllCooldowns();
    }

    /**
     * Tick all cooldowns for enemy.
     * 
     * @param enemy The enemy whose cooldowns to tick
     */
    public void tickEnemyCooldowns(Enemy enemy) {
        if (enemy == null) return;
        enemy.tickAllCooldowns();
    }

    /**
     * Tick cooldowns for both player and enemy.
     * Use this in your main battle loop.
     * 
     * WHEN TO CALL: At the start of each turn cycle, before checking AP.
     * 
     * @param player The player
     * @param enemy The enemy
     */
    public void tickAllCooldowns(Player player, Enemy enemy) {
        tickPlayerCooldowns(player);
        tickEnemyCooldowns(enemy);
    }

    // ===== RESETTING COOLDOWNS =====

    /**
     * Reset all cooldowns for player (sets all to 0).
     * 
     * WHEN TO CALL:
     * - At the start of a new battle
     * - When player heals/rests between encounters
     * 
     * @param player The player whose cooldowns to reset
     */
    public void resetPlayerCooldowns(Player player) {
        if (player == null) return;
        player.resetAllCooldowns();
    }

    /**
     * Reset all cooldowns for enemy.
     * 
     * @param enemy The enemy whose cooldowns to reset
     */
    public void resetEnemyCooldowns(Enemy enemy) {
        if (enemy == null) return;
        enemy.resetAllCooldowns();
    }

    /**
     * Reset cooldowns for both player and enemy.
     * Use this when starting a fresh battle.
     * 
     * @param player The player
     * @param enemy The enemy
     */
    public void resetAllCooldowns(Player player, Enemy enemy) {
        resetPlayerCooldowns(player);
        resetEnemyCooldowns(enemy);
    }

    // ===== COOLDOWN CALCULATION (PREVIEW) =====

    /**
     * Calculate what the final cooldown would be after CDR.
     * Does NOT actually apply the cooldown - use for preview/calculation only.
     * 
     * @param skill The skill to calculate for
     * @param cooldownReduction The CDR stat value
     * @return The final cooldown after CDR is applied (minimum 1 for non-zero base cooldowns)
     */
    public int calculateFinalCooldown(Skill skill, int cooldownReduction) {
        if (skill == null) return 0;
        
        int baseCooldown = skill.getBaseCooldown();
        if (baseCooldown == 0) return 0;
        
        return Math.max(1, baseCooldown - cooldownReduction);
    }

    /**
     * Calculate final cooldown for a player's skill.
     * Preview what cooldown will be applied.
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

    // ===== GUI HELPER METHODS =====

    /**
     * Get cooldown status as percentage (0.0 to 1.0).
     * Useful for progress bars showing cooldown recovery.
     * 
     * 0.0 = just used (full cooldown)
     * 0.5 = halfway recovered
     * 1.0 = ready (no cooldown)
     * 
     * @param player The player
     * @param skill The skill
     * @return Percentage ready (0.0 = on full CD, 1.0 = ready)
     */
    public double getCooldownProgress(Player player, Skill skill) {
        if (player == null || skill == null) return 1.0;
        
        int baseCooldown = skill.getBaseCooldown();
        if (baseCooldown == 0) return 1.0; // No cooldown = always ready
        
        int remaining = player.getSkillCooldown(skill);
        if (remaining == 0) return 1.0; // Ready
        
        int maxCooldown = calculateFinalCooldown(player, skill);
        if (maxCooldown == 0) return 1.0;
        
        // Calculate how much has recovered
        double recovered = (maxCooldown - remaining) / (double) maxCooldown;
        return Math.max(0.0, Math.min(1.0, recovered));
    }

    /**
     * Get cooldown progress for enemy skill.
     * 
     * @param enemy The enemy
     * @param skill The skill
     * @return Percentage ready (0.0 to 1.0)
     */
    public double getCooldownProgress(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return 1.0;
        
        int baseCooldown = skill.getBaseCooldown();
        if (baseCooldown == 0) return 1.0;
        
        int remaining = enemy.getSkillCooldown(skill);
        if (remaining == 0) return 1.0;
        
        int maxCooldown = calculateFinalCooldown(enemy, skill);
        if (maxCooldown == 0) return 1.0;
        
        double recovered = (maxCooldown - remaining) / (double) maxCooldown;
        return Math.max(0.0, Math.min(1.0, recovered));
    }

    /**
     * Get human-readable cooldown status text.
     * Perfect for GUI tooltips or status displays.
     * 
     * Examples:
     * - "READY"
     * - "Cooldown: 1 turn"
     * - "Cooldown: 3 turns"
     * 
     * @param player The player
     * @param skill The skill
     * @return Status string
     */
    public String getCooldownStatusText(Player player, Skill skill) {
        if (player == null || skill == null) return "Invalid";
        
        int remaining = player.getSkillCooldown(skill);
        
        if (remaining == 0) {
            return "READY";
        } else if (remaining == 1) {
            return "Cooldown: 1 turn";
        } else {
            return "Cooldown: " + remaining + " turns";
        }
    }

    /**
     * Get cooldown status text for enemy skill.
     * 
     * @param enemy The enemy
     * @param skill The skill
     * @return Status string
     */
    public String getCooldownStatusText(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return "Invalid";
        
        int remaining = enemy.getSkillCooldown(skill);
        
        if (remaining == 0) {
            return "READY";
        } else if (remaining == 1) {
            return "Cooldown: 1 turn";
        } else {
            return "Cooldown: " + remaining + " turns";
        }
    }

    /**
     * Count how many skills are ready for player.
     * Useful for UI indicators (e.g., "2/3 skills ready").
     * 
     * @param player The player
     * @param skills Array of skills
     * @return Number of skills ready to use
     */
    public int countReadySkills(Player player, Skill[] skills) {
        if (player == null || skills == null) return 0;
        
        int count = 0;
        for (Skill skill : skills) {
            if (skill != null && isSkillReady(player, skill)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count how many skills are ready for enemy.
     * 
     * @param enemy The enemy
     * @return Number of skills ready to use
     */
    public int countReadySkills(Enemy enemy) {
        if (enemy == null || !enemy.hasSkills()) return 0;
        
        int count = 0;
        for (Skill skill : enemy.getSkills()) {
            if (skill != null && isSkillReady(enemy, skill)) {
                count++;
            }
        }
        return count;
    }

    // ===== VALIDATION =====

    /**
     * Validate that cooldown system can operate on entities.
     * 
     * @param player Player to validate
     * @param enemy Enemy to validate
     * @return true if both entities are valid
     */
    public boolean canManageCooldowns(Player player, Enemy enemy) {
        return player != null && enemy != null;
    }
}
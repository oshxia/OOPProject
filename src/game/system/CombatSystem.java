package game.system;

import game.core.Player;
import game.core.Enemy;
import game.core.Skill;

/**
 * CombatSystem handles all battle mechanics and combat interactions.
 * Orchestrates EntitySystem, SkillSystem, and CooldownSystem for combat.
 * 
 * Responsibilities:
 * - Execute player attacks with skills
 * - Execute enemy attacks with skills
 * - Calculate hit/miss based on accuracy vs evasion
 * - Apply damage to targets
 * - Manage skill cooldowns after use
 * - Validate combat actions
 * - Provide combat state queries
 * 
 * Design: Uses composition - depends on other systems to do the work.
 * GUI-Friendly: Returns simple combat result objects for easy UI display.
 */
public class CombatSystem {

    private final EntitySystem entitySystem;
    private final SkillSystem skillSystem;
    private final CooldownSystem cooldownSystem;

    // Combat constants
    private static final int MIN_HIT_CHANCE = 5;   // Minimum 5% hit chance
    private static final int MAX_HIT_CHANCE = 95;  // Maximum 95% hit chance

    public CombatSystem(EntitySystem entitySystem, SkillSystem skillSystem, CooldownSystem cooldownSystem) {
        if (entitySystem == null || skillSystem == null || cooldownSystem == null) {
            throw new IllegalArgumentException("All systems must be non-null");
        }
        
        this.entitySystem = entitySystem;
        this.skillSystem = skillSystem;
        this.cooldownSystem = cooldownSystem;
    }

    // ===== COMBAT RESULT CLASS =====

    /**
     * CombatResult holds the outcome of a combat action.
     * Perfect for GUI display - contains all relevant information.
     */
    public static class CombatResult {
        private final boolean success;      // Was the action successful?
        private final boolean hit;          // Did the attack hit? (false if missed)
        private final int damageDealt;      // Actual damage dealt
        private final String attackerName;  // Who attacked
        private final String targetName;    // Who was targeted
        private final String skillName;     // Skill used
        private final String message;       // Human-readable message

        public CombatResult(boolean success, boolean hit, int damageDealt, 
                           String attackerName, String targetName, String skillName, String message) {
            this.success = success;
            this.hit = hit;
            this.damageDealt = damageDealt;
            this.attackerName = attackerName;
            this.targetName = targetName;
            this.skillName = skillName;
            this.message = message;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public boolean isHit() { return hit; }
        public int getDamageDealt() { return damageDealt; }
        public String getAttackerName() { return attackerName; }
        public String getTargetName() { return targetName; }
        public String getSkillName() { return skillName; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return message;
        }
    }

    // ===== PLAYER ATTACKS =====

    /**
     * Player attacks enemy with a skill.
     * Handles validation, hit calculation, damage application, and cooldown.
     * 
     * @param player The attacking player
     * @param enemy The target enemy
     * @param skill The skill to use
     * @return CombatResult with all combat information
     */
    public CombatResult playerAttack(Player player, Enemy enemy, Skill skill) {
        // Validation
        if (player == null || enemy == null || skill == null) {
            return new CombatResult(false, false, 0, "Unknown", "Unknown", "Unknown", 
                "Invalid combat action");
        }

        String playerName = player.getName();
        String enemyName = enemy.getName();
        String skillName = skill.getName();

        // Check if player can use skill
        if (!skillSystem.canUseSkill(player, skill)) {
            String reason = !skillSystem.matchesProfession(player, skill) 
                ? "Wrong profession" 
                : "Skill on cooldown";
            return new CombatResult(false, false, 0, playerName, enemyName, skillName,
                playerName + " cannot use " + skillName + " (" + reason + ")");
        }

        // Check if enemy is alive
        if (!entitySystem.isAlive(enemy)) {
            return new CombatResult(false, false, 0, playerName, enemyName, skillName,
                enemyName + " is already defeated");
        }

        // Calculate hit chance
        int accuracy = entitySystem.getAccuracy(player);
        int evasion = entitySystem.getEvasion(enemy);
        boolean hit = attemptHit(accuracy, evasion);

        int damage = 0;
        String message;

        if (hit) {
            // Hit: calculate and apply damage
            damage = skillSystem.calculateDamage(player, skill);
            entitySystem.applyDamage(enemy, damage);
            message = playerName + " used " + skillName + " and dealt " + damage + " damage to " + enemyName + "!";
        } else {
            // Miss: no damage
            damage = 0;
            message = playerName + " used " + skillName + " but missed " + enemyName + "!";
        }

        // Apply cooldown (regardless of hit/miss)
        cooldownSystem.applySkillCooldown(player, skill);

        return new CombatResult(true, hit, damage, playerName, enemyName, skillName, message);
    }

    /**
     * Player attacks enemy with skill by index.
     * Convenience method for array-based skill selection.
     * 
     * @param player The attacking player
     * @param enemy The target enemy
     * @param skills Array of player skills
     * @param skillIndex Index of skill to use
     * @return CombatResult
     */
    public CombatResult playerAttack(Player player, Enemy enemy, Skill[] skills, int skillIndex) {
        if (!skillSystem.isValidSkillIndex(skills, skillIndex)) {
            return new CombatResult(false, false, 0, 
                player != null ? player.getName() : "Unknown",
                enemy != null ? enemy.getName() : "Unknown",
                "Unknown",
                "Invalid skill index");
        }

        Skill skill = skills[skillIndex];
        return playerAttack(player, enemy, skill);
    }

    // ===== ENEMY ATTACKS =====

    /**
     * Enemy attacks player with a skill.
     * 
     * @param enemy The attacking enemy
     * @param player The target player
     * @param skill The skill to use
     * @return CombatResult
     */
    public CombatResult enemyAttack(Enemy enemy, Player player, Skill skill) {
        // Validation
        if (enemy == null || player == null || skill == null) {
            return new CombatResult(false, false, 0, "Unknown", "Unknown", "Unknown",
                "Invalid combat action");
        }

        String enemyName = enemy.getName();
        String playerName = player.getName();
        String skillName = skill.getName();

        // Check if enemy can use skill
        if (!skillSystem.canUseSkill(enemy, skill)) {
            return new CombatResult(false, false, 0, enemyName, playerName, skillName,
                enemyName + " cannot use " + skillName + " (on cooldown)");
        }

        // Check if player is alive
        if (!entitySystem.isAlive(player)) {
            return new CombatResult(false, false, 0, enemyName, playerName, skillName,
                playerName + " is already defeated");
        }

        // Calculate hit chance
        int accuracy = entitySystem.getAccuracy(enemy);
        int evasion = entitySystem.getEvasion(player);
        boolean hit = attemptHit(accuracy, evasion);

        int damage = 0;
        String message;

        if (hit) {
            // Hit: calculate and apply damage
            damage = skillSystem.calculateDamage(enemy, skill);
            entitySystem.applyDamage(player, damage);
            message = enemyName + " used " + skillName + " and dealt " + damage + " damage to " + playerName + "!";
        } else {
            // Miss: no damage
            damage = 0;
            message = enemyName + " used " + skillName + " but missed " + playerName + "!";
        }

        // Apply cooldown
        cooldownSystem.applySkillCooldown(enemy, skill);

        return new CombatResult(true, hit, damage, enemyName, playerName, skillName, message);
    }

    /**
     * Enemy attacks player with basic attack (no skill).
     * Used when enemy has no skills or all skills are on cooldown.
     * 
     * @param enemy The attacking enemy
     * @param player The target player
     * @return CombatResult
     */
    public CombatResult enemyBasicAttack(Enemy enemy, Player player) {
        if (enemy == null || player == null) {
            return new CombatResult(false, false, 0, "Unknown", "Unknown", "Basic Attack",
                "Invalid combat action");
        }

        String enemyName = enemy.getName();
        String playerName = player.getName();

        // Check if player is alive
        if (!entitySystem.isAlive(player)) {
            return new CombatResult(false, false, 0, enemyName, playerName, "Basic Attack",
                playerName + " is already defeated");
        }

        // Calculate hit chance
        int accuracy = entitySystem.getAccuracy(enemy);
        int evasion = entitySystem.getEvasion(player);
        boolean hit = attemptHit(accuracy, evasion);

        int damage = 0;
        String message;

        if (hit) {
            // Basic attack damage = strength only
            damage = enemy.getStats().getStrength();
            entitySystem.applyDamage(player, damage);
            message = enemyName + " used Basic Attack and dealt " + damage + " damage to " + playerName + "!";
        } else {
            message = enemyName + " used Basic Attack but missed " + playerName + "!";
        }

        return new CombatResult(true, hit, damage, enemyName, playerName, "Basic Attack", message);
    }

    // ===== HIT CALCULATION =====

    /**
     * Calculate if an attack hits based on accuracy vs evasion.
     * Formula: hitChance = accuracy - evasion (clamped to 5%-95%)
     * 
     * @param accuracy Attacker's accuracy stat
     * @param evasion Defender's evasion stat
     * @return true if attack hits
     */
    public boolean attemptHit(int accuracy, int evasion) {
        int hitChance = accuracy - evasion;
        hitChance = Math.max(MIN_HIT_CHANCE, Math.min(MAX_HIT_CHANCE, hitChance));
        
        int roll = (int)(Math.random() * 100) + 1; // 1-100
        return roll <= hitChance;
    }

    /**
     * Calculate hit chance percentage.
     * Returns the actual % chance to hit (5-95).
     * Perfect for GUI display.
     * 
     * @param accuracy Attacker's accuracy
     * @param evasion Defender's evasion
     * @return Hit chance as percentage (5-95)
     */
    public int calculateHitChance(int accuracy, int evasion) {
        int hitChance = accuracy - evasion;
        return Math.max(MIN_HIT_CHANCE, Math.min(MAX_HIT_CHANCE, hitChance));
    }

    /**
     * Calculate hit chance for player attacking enemy.
     * 
     * @param player The attacker
     * @param enemy The defender
     * @return Hit chance percentage
     */
    public int calculateHitChance(Player player, Enemy enemy) {
        if (player == null || enemy == null) return 0;
        return calculateHitChance(
            entitySystem.getAccuracy(player),
            entitySystem.getEvasion(enemy)
        );
    }

    /**
     * Calculate hit chance for enemy attacking player.
     * 
     * @param enemy The attacker
     * @param player The defender
     * @return Hit chance percentage
     */
    public int calculateHitChance(Enemy enemy, Player player) {
        if (enemy == null || player == null) return 0;
        return calculateHitChance(
            entitySystem.getAccuracy(enemy),
            entitySystem.getEvasion(player)
        );
    }

    // ===== COMBAT STATE QUERIES =====

    /**
     * Check if combat is over.
     * Combat ends when either player or enemy is dead.
     * 
     * @param player The player
     * @param enemy The enemy
     * @return true if combat is over
     */
    public boolean isCombatOver(Player player, Enemy enemy) {
        return !entitySystem.isAlive(player) || !entitySystem.isAlive(enemy);
    }

    /**
     * Get combat winner.
     * Returns 1 if player won, -1 if enemy won, 0 if ongoing/draw.
     * 
     * @param player The player
     * @param enemy The enemy
     * @return 1 = player won, -1 = enemy won, 0 = ongoing
     */
    public int getCombatWinner(Player player, Enemy enemy) {
        boolean playerAlive = entitySystem.isAlive(player);
        boolean enemyAlive = entitySystem.isAlive(enemy);

        if (!enemyAlive && playerAlive) return 1;  // Player won
        if (!playerAlive && enemyAlive) return -1; // Enemy won
        return 0; // Ongoing or draw
    }

    /**
     * Check if player won the combat.
     * 
     * @param player The player
     * @param enemy The enemy
     * @return true if player is alive and enemy is dead
     */
    public boolean didPlayerWin(Player player, Enemy enemy) {
        return getCombatWinner(player, enemy) == 1;
    }

    /**
     * Check if enemy won the combat.
     * 
     * @param player The player
     * @param enemy The enemy
     * @return true if enemy is alive and player is dead
     */
    public boolean didEnemyWin(Player player, Enemy enemy) {
        return getCombatWinner(player, enemy) == -1;
    }

    // ===== BATTLE PREPARATION =====

    /**
     * Prepare both combatants for a new battle.
     * Resets cooldowns and heals to full HP.
     * 
     * @param player The player
     * @param enemy The enemy
     */
    public void prepareBattle(Player player, Enemy enemy) {
        entitySystem.prepareBattle(player, enemy);
        cooldownSystem.resetAllCooldowns(player, enemy);
    }

    // ===== DAMAGE PREVIEW =====

    /**
     * Preview damage that would be dealt (without actually dealing it).
     * Useful for "Show Damage" tooltips in GUI.
     * 
     * @param player The attacker
     * @param skill The skill
     * @return Damage that would be dealt
     */
    public int previewPlayerDamage(Player player, Skill skill) {
        if (player == null || skill == null) return 0;
        return skillSystem.calculateDamage(player, skill);
    }

    /**
     * Preview damage from enemy skill.
     * 
     * @param enemy The attacker
     * @param skill The skill
     * @return Damage that would be dealt
     */
    public int previewEnemyDamage(Enemy enemy, Skill skill) {
        if (enemy == null || skill == null) return 0;
        return skillSystem.calculateDamage(enemy, skill);
    }

    // ===== VALIDATION =====

    /**
     * Validate that combat can proceed.
     * Checks if both combatants are valid and alive.
     * 
     * @param player The player
     * @param enemy The enemy
     * @return true if combat can proceed
     */
    public boolean canCombatProceed(Player player, Enemy enemy) {
        return entitySystem.isValidForCombat(player, enemy);
    }

    /**
     * Validate that a player can perform an attack action.
     * 
     * @param player The player
     * @param enemy The enemy
     * @param skill The skill to use
     * @return true if action is valid
     */
    public boolean canPlayerAttack(Player player, Enemy enemy, Skill skill) {
        if (!entitySystem.isAlive(player) || !entitySystem.isAlive(enemy)) {
            return false;
        }
        return skillSystem.canUseSkill(player, skill);
    }

    /**
     * Validate that an enemy can perform an attack action.
     * 
     * @param enemy The enemy
     * @param player The player
     * @param skill The skill to use
     * @return true if action is valid
     */
    public boolean canEnemyAttack(Enemy enemy, Player player, Skill skill) {
        if (!entitySystem.isAlive(enemy) || !entitySystem.isAlive(player)) {
            return false;
        }
        return skillSystem.canUseSkill(enemy, skill);
    }

    // ===== ACCESSORS =====

    public EntitySystem getEntitySystem() { return entitySystem; }
    public SkillSystem getSkillSystem() { return skillSystem; }
    public CooldownSystem getCooldownSystem() { return cooldownSystem; }
    
}
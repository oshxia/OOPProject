package game.system;

import game.core.Player;
import game.core.Enemy;
import game.core.Stat;
import game.core.Profession;
import game.core.Skill;

/**
 * EntitySystem manages the creation, initialization, and state of game entities.
 * Handles both Player and Enemy lifecycle management.
 * 
 * BALANCED VERSION:
 * - Players get +5 in their profession specialization stat
 * - Warriors: 25 STR, 20 AGI, 20 INT
 * - Rogues: 20 STR, 25 AGI, 20 INT
 * - Mages: 20 STR, 20 AGI, 25 INT
 * 
 * Responsibilities:
 * - Create players and enemies with proper initialization
 * - Manage entity health and stats
 * - Reset entity state for new battles
 * - Validate entity state
 * - Query entity information
 * 
 * Design: Stateless utility system - operates on passed entities without storing state.
 */
public class EntitySystem {

    // ===== PLAYER CREATION =====

    /**
     * Creates a new player with profession-based stat bonuses.
     * Each profession gets +5 in their specialization stat.
     * 
     * Warriors: 25 STR, 20 AGI, 20 INT
     * Rogues: 20 STR, 25 AGI, 20 INT
     * Mages: 20 STR, 20 AGI, 25 INT
     * 
     * @param name Player name
     * @param profession Player profession
     * @return Newly created player
     * @throws IllegalArgumentException if name is invalid or profession is null
     */
    public Player createPlayer(String name, Profession profession) {
        // Base stats
        int str = 20, agi = 20, intel = 20;
        
        // Apply profession bonus (+5 to specialization)
        if (profession != null) {
            switch (profession) {
                case WARRIOR:
                    str = 25;  // Warriors get +5 STR
                    break;
                case ROGUE:
                    agi = 25;  // Rogues get +5 AGI
                    break;
                case MAGE:
                    intel = 25;  // Mages get +5 INT
                    break;
            }
        }
        
        return createPlayer(name, profession, str, agi, intel);
    }

    /**
     * Creates a new player with custom stats.
     * 
     * @param name Player name
     * @param profession Player profession
     * @param strength Initial strength
     * @param agility Initial agility
     * @param intelligence Initial intelligence
     * @return Newly created player
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Player createPlayer(String name, Profession profession, 
                               int strength, int agility, int intelligence) {
        Stat stats = new Stat(strength, agility, intelligence);
        return new Player(name, profession, stats);
    }

    // ===== ENEMY CREATION =====

    /**
     * Creates a new enemy without skills.
     * 
     * @param name Enemy name
     * @param strength Initial strength
     * @param agility Initial agility
     * @param intelligence Initial intelligence
     * @return Newly created enemy
     * @throws IllegalArgumentException if name is invalid
     */
    public Enemy createEnemy(String name, int strength, int agility, int intelligence) {
        Stat stats = new Stat(strength, agility, intelligence);
        return new Enemy(name, stats);
    }

    /**
     * Creates a new enemy with skills.
     * 
     * @param name Enemy name
     * @param strength Initial strength
     * @param agility Initial agility
     * @param intelligence Initial intelligence
     * @param skills Array of skills for the enemy
     * @return Newly created enemy
     * @throws IllegalArgumentException if name is invalid
     */
    public Enemy createEnemy(String name, int strength, int agility, int intelligence, Skill[] skills) {
        Stat stats = new Stat(strength, agility, intelligence);
        return new Enemy(name, stats, skills);
    }

    /**
     * Creates a copy of an existing enemy with fresh state.
     * Useful for creating multiple instances of the same enemy type.
     * 
     * @param template Enemy to copy
     * @return New enemy with same stats and skills but fresh state
     */
    public Enemy copyEnemy(Enemy template) {
        if (template == null) {
            throw new IllegalArgumentException("Template enemy cannot be null");
        }

        Stat originalStats = template.getStats();
        Stat newStats = new Stat(
            originalStats.getStrength(),
            originalStats.getAgility(),
            originalStats.getIntelligence()
        );

        return new Enemy(template.getName(), newStats, template.getSkills());
    }

    // ===== HEALTH MANAGEMENT =====

    /**
     * Restore entity to full health.
     * 
     * @param player Player to heal
     */
    public void fullHeal(Player player) {
        if (player != null) {
            player.getStats().fullHeal();
        }
    }

    /**
     * Restore entity to full health.
     * 
     * @param enemy Enemy to heal
     */
    public void fullHeal(Enemy enemy) {
        if (enemy != null) {
            enemy.getStats().fullHeal();
        }
    }

    /**
     * Apply damage to player.
     * 
     * @param player Player to damage
     * @param damage Amount of damage
     */
    public void applyDamage(Player player, int damage) {
        if (player != null && damage > 0) {
            player.takeDamage(damage);
        }
    }

    /**
     * Apply damage to enemy.
     * 
     * @param enemy Enemy to damage
     * @param damage Amount of damage
     */
    public void applyDamage(Enemy enemy, int damage) {
        if (enemy != null && damage > 0) {
            enemy.takeDamage(damage);
        }
    }

    // ===== STAT MANAGEMENT =====

    /**
     * Increase player strength.
     * 
     * @param player Player to modify
     * @param amount Amount to increase (can be negative to decrease)
     */
    public void modifyStrength(Player player, int amount) {
        if (player != null && amount != 0) {
            player.getStats().increaseStrength(amount);
        }
    }

    /**
     * Increase player agility.
     * 
     * @param player Player to modify
     * @param amount Amount to increase (can be negative to decrease)
     */
    public void modifyAgility(Player player, int amount) {
        if (player != null && amount != 0) {
            player.getStats().increaseAgility(amount);
        }
    }

    /**
     * Increase player intelligence.
     * 
     * @param player Player to modify
     * @param amount Amount to increase (can be negative to decrease)
     */
    public void modifyIntelligence(Player player, int amount) {
        if (player != null && amount != 0) {
            player.getStats().increaseIntelligence(amount);
        }
    }

    /**
     * Increase enemy strength.
     * 
     * @param enemy Enemy to modify
     * @param amount Amount to increase (can be negative to decrease)
     */
    public void modifyStrength(Enemy enemy, int amount) {
        if (enemy != null && amount != 0) {
            enemy.getStats().increaseStrength(amount);
        }
    }

    /**
     * Increase enemy agility.
     * 
     * @param enemy Enemy to modify
     * @param amount Amount to increase (can be negative to decrease)
     */
    public void modifyAgility(Enemy enemy, int amount) {
        if (enemy != null && amount != 0) {
            enemy.getStats().increaseAgility(amount);
        }
    }

    /**
     * Increase enemy intelligence.
     * 
     * @param enemy Enemy to modify
     * @param amount Amount to increase (can be negative to decrease)
     */
    public void modifyIntelligence(Enemy enemy, int amount) {
        if (enemy != null && amount != 0) {
            enemy.getStats().increaseIntelligence(amount);
        }
    }

    // ===== BATTLE PREPARATION =====

    /**
     * Prepare player for battle.
     * Resets cooldowns and restores to full health.
     * 
     * @param player Player to prepare
     */
    public void prepareBattle(Player player) {
        if (player != null) {
            player.resetAllCooldowns();
            player.getStats().fullHeal();
        }
    }

    /**
     * Prepare enemy for battle.
     * Resets cooldowns and restores to full health.
     * 
     * @param enemy Enemy to prepare
     */
    public void prepareBattle(Enemy enemy) {
        if (enemy != null) {
            enemy.resetAllCooldowns();
            enemy.getStats().fullHeal();
        }
    }

    /**
     * Prepare both combatants for battle.
     * 
     * @param player Player to prepare
     * @param enemy Enemy to prepare
     */
    public void prepareBattle(Player player, Enemy enemy) {
        prepareBattle(player);
        prepareBattle(enemy);
    }

    // ===== STATE QUERIES =====

    /**
     * Check if player is alive.
     * 
     * @param player Player to check
     * @return true if player is alive (HP > 0)
     */
    public boolean isAlive(Player player) {
        return player != null && !player.isDead();
    }

    /**
     * Check if enemy is alive.
     * 
     * @param enemy Enemy to check
     * @return true if enemy is alive (HP > 0)
     */
    public boolean isAlive(Enemy enemy) {
        return enemy != null && !enemy.isDead();
    }

    /**
     * Get player's current HP.
     * 
     * @param player Player to query
     * @return Current HP, or 0 if player is null
     */
    public int getCurrentHP(Player player) {
        return player != null ? player.getStats().getHp() : 0;
    }

    /**
     * Get enemy's current HP.
     * 
     * @param enemy Enemy to query
     * @return Current HP, or 0 if enemy is null
     */
    public int getCurrentHP(Enemy enemy) {
        return enemy != null ? enemy.getStats().getHp() : 0;
    }

    /**
     * Get player's max HP.
     * 
     * @param player Player to query
     * @return Max HP, or 0 if player is null
     */
    public int getMaxHP(Player player) {
        return player != null ? player.getStats().getMaxHp() : 0;
    }

    /**
     * Get enemy's max HP.
     * 
     * @param enemy Enemy to query
     * @return Max HP, or 0 if enemy is null
     */
    public int getMaxHP(Enemy enemy) {
        return enemy != null ? enemy.getStats().getMaxHp() : 0;
    }

    /**
     * Get player's HP percentage (0.0 to 1.0).
     * 
     * @param player Player to query
     * @return HP percentage, or 0.0 if player is null
     */
    public double getHPPercentage(Player player) {
        if (player == null) return 0.0;
        int maxHp = player.getStats().getMaxHp();
        if (maxHp == 0) return 0.0;
        return (double) player.getStats().getHp() / maxHp;
    }

    /**
     * Get enemy's HP percentage (0.0 to 1.0).
     * 
     * @param enemy Enemy to query
     * @return HP percentage, or 0.0 if enemy is null
     */
    public double getHPPercentage(Enemy enemy) {
        if (enemy == null) return 0.0;
        int maxHp = enemy.getStats().getMaxHp();
        if (maxHp == 0) return 0.0;
        return (double) enemy.getStats().getHp() / maxHp;
    }

    // ===== PRIMARY STATS QUERIES =====

    /**
     * Get player's primary stats as array [STR, AGI, INT].
     * 
     * @param player Player to query
     * @return Array of [strength, agility, intelligence]
     */
    public int[] getPrimaryStats(Player player) {
        if (player == null) return new int[]{0, 0, 0};
        Stat stats = player.getStats();
        return new int[]{stats.getStrength(), stats.getAgility(), stats.getIntelligence()};
    }

    /**
     * Get enemy's primary stats as array [STR, AGI, INT].
     * 
     * @param enemy Enemy to query
     * @return Array of [strength, agility, intelligence]
     */
    public int[] getPrimaryStats(Enemy enemy) {
        if (enemy == null) return new int[]{0, 0, 0};
        Stat stats = enemy.getStats();
        return new int[]{stats.getStrength(), stats.getAgility(), stats.getIntelligence()};
    }

    // ===== DERIVED STATS QUERIES =====

    /**
     * Get player's speed (affects turn order).
     * 
     * @param player Player to query
     * @return Speed value, or 0 if player is null
     */
    public int getSpeed(Player player) {
        return player != null ? player.getStats().getSpeed() : 0;
    }

    /**
     * Get enemy's speed (affects turn order).
     * 
     * @param enemy Enemy to query
     * @return Speed value, or 0 if enemy is null
     */
    public int getSpeed(Enemy enemy) {
        return enemy != null ? enemy.getStats().getSpeed() : 0;
    }

    /**
     * Get player's accuracy.
     * 
     * @param player Player to query
     * @return Accuracy value, or 0 if player is null
     */
    public int getAccuracy(Player player) {
        return player != null ? player.getStats().getAccuracy() : 0;
    }

    /**
     * Get enemy's accuracy.
     * 
     * @param enemy Enemy to query
     * @return Accuracy value, or 0 if enemy is null
     */
    public int getAccuracy(Enemy enemy) {
        return enemy != null ? enemy.getStats().getAccuracy() : 0;
    }

    /**
     * Get player's evasion.
     * 
     * @param player Player to query
     * @return Evasion value, or 0 if player is null
     */
    public int getEvasion(Player player) {
        return player != null ? player.getStats().getEvasion() : 0;
    }

    /**
     * Get enemy's evasion.
     * 
     * @param enemy Enemy to query
     * @return Evasion value, or 0 if enemy is null
     */
    public int getEvasion(Enemy enemy) {
        return enemy != null ? enemy.getStats().getEvasion() : 0;
    }

    /**
     * Get player's cooldown reduction.
     * 
     * @param player Player to query
     * @return CDR value, or 0 if player is null
     */
    public int getCooldownReduction(Player player) {
        return player != null ? player.getStats().getCooldownReduction() : 0;
    }

    /**
     * Get enemy's cooldown reduction.
     * 
     * @param enemy Enemy to query
     * @return CDR value, or 0 if enemy is null
     */
    public int getCooldownReduction(Enemy enemy) {
        return enemy != null ? enemy.getStats().getCooldownReduction() : 0;
    }

    // ===== VALIDATION =====

    /**
     * Validate that a player is in valid state for combat.
     * 
     * @param player Player to validate
     * @return true if player is valid and ready for combat
     */
    public boolean isValidForCombat(Player player) {
        return player != null && !player.isDead();
    }

    /**
     * Validate that an enemy is in valid state for combat.
     * 
     * @param enemy Enemy to validate
     * @return true if enemy is valid and ready for combat
     */
    public boolean isValidForCombat(Enemy enemy) {
        return enemy != null && !enemy.isDead();
    }

    /**
     * Validate that both combatants are ready for battle.
     * 
     * @param player Player to validate
     * @param enemy Enemy to validate
     * @return true if both are valid and ready
     */
    public boolean isValidForCombat(Player player, Enemy enemy) {
        return isValidForCombat(player) && isValidForCombat(enemy);
    }
}
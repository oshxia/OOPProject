package game.data;

import game.core.Enemy;
import game.core.Stat;
import game.core.Skill;

/**
 * EnemiesData provides enemy templates and definitions.
 * Contains all enemy types with their base stats and skills.
 * 
 * BALANCED VERSION:
 * - Enemy baseline stats: 20/20/20
 * - Specialized stat: +10 bonus (total 30)
 * - Rebalanced skill damages for fair but challenging gameplay
 * 
 * Enemy Design:
 * - Each enemy has a specialization (STR/AGI/INT)
 * - Base stats: 20 in each, +10 in specialized stat (30 total)
 * - Each enemy has 3 skills: Basic (0 CD), Normal (2-3 CD), Ultimate (3-5 CD)
 * 
 * Enemy Types:
 * 1. Killer Bunny - AGI specialist (fast, evasive) - 20/30/20
 * 2. Minotaur - STR specialist (tank, high damage) - 30/20/20
 * 3. Mindflayer - INT specialist (high accuracy, low cooldowns) - 20/20/30
 */
public class EnemiesData {

    /**
     * Get all enemy types for the game.
     * Returns an array of 3 enemies in order.
     * 
     * @return Array of enemy templates
     */
    public static Enemy[] getAllEnemyTypes() {
        return new Enemy[] {
            createKillerBunny(),
            createMinotaur(),
            createMindflayer()
        };
    }

    /**
     * Get a specific enemy by index.
     * 
     * @param index Enemy index (0 = Killer Bunny, 1 = Minotaur, 2 = Mindflayer)
     * @return Enemy template, or null if invalid index
     */
    public static Enemy getEnemyByIndex(int index) {
        Enemy[] enemies = getAllEnemyTypes();
        if (index >= 0 && index < enemies.length) {
            return enemies[index];
        }
        return null;
    }

    /**
     * Get a specific enemy by name.
     * 
     * @param name Enemy name
     * @return Enemy template, or null if not found
     */
    public static Enemy getEnemyByName(String name) {
        if (name == null) return null;

        Enemy[] enemies = getAllEnemyTypes();
        for (Enemy enemy : enemies) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * Create a copy of an enemy template.
     * Useful for creating multiple instances with fresh state.
     * 
     * @param template Enemy to copy
     * @return New enemy with same stats and skills
     */
    public static Enemy copyEnemy(Enemy template) {
        if (template == null) return null;

        Stat stats = template.getStats();
        Stat newStats = new Stat(
            stats.getStrength(),
            stats.getAgility(),
            stats.getIntelligence()
        );

        return new Enemy(template.getName(), newStats, template.getSkills());
    }

    // ===== ENEMY DEFINITIONS =====

    /**
     * Killer Bunny - AGI Specialist
     * 
     * Base Stats: STR 20, AGI 30, INT 20
     * Specialization: AGILITY
     * AI Strategy: Aggressive burst - always uses highest damage skill
     * 
     * Strategy: Speed demon that gets more turns than player
     * Threat: Pressure through frequency of attacks
     * Counter: Rogue (speed vs speed) or high evasion builds
     * 
     * Skills:
     * - Rapid Bite: 8 damage, 0 cooldown (basic attack)
     * - Pounce: 18 damage, 2 cooldown
     * - Frenzy: 28 damage, 3 cooldown (ultimate)
     * 
     * At 20 STR:
     * - Rapid Bite: 28 total damage
     * - Pounce: 38 total damage
     * - Frenzy: 48 total damage
     */
    private static Enemy createKillerBunny() {
        // AGI specialist: baseline 20, specialized 30
        Stat stats = new Stat(20, 30, 20);

        Skill[] skills = new Skill[] {
            new Skill("Rapid Bite", null, 8, 0),      // Basic
            new Skill("Pounce", null, 18, 2),         // Normal
            new Skill("Frenzy", null, 28, 3)          // Ultimate
        };

        return new Enemy("Killer Bunny", stats, skills);
    }

    /**
     * Minotaur - STR Specialist
     * 
     * Base Stats: STR 30, AGI 20, INT 20
     * Specialization: STRENGTH
     * AI Strategy: Strategic execute - uses ultimate on first turn and for kills
     * 
     * Strategy: Tank with highest damage output
     * Threat: Raw damage and high HP pool
     * Counter: Warrior (HP vs HP) or high evasion
     * 
     * Skills:
     * - Axe Swing: 10 damage, 0 cooldown (basic attack)
     * - Charge: 20 damage, 2 cooldown
     * - Earthquake: 35 damage, 3 cooldown (ultimate)
     * 
     * At 30 STR:
     * - Axe Swing: 40 total damage
     * - Charge: 50 total damage
     * - Earthquake: 65 total damage (devastating!)
     */
    private static Enemy createMinotaur() {
        // STR specialist: baseline 20, specialized 30
        Stat stats = new Stat(30, 20, 20);

        Skill[] skills = new Skill[] {
            new Skill("Axe Swing", null, 10, 0),      // Basic
            new Skill("Charge", null, 20, 2),         // Normal
            new Skill("Earthquake", null, 35, 3)      // Ultimate
        };

        return new Enemy("Minotaur", stats, skills);
    }

    /**
     * Mindflayer - INT Specialist
     * 
     * Base Stats: STR 20, AGI 20, INT 30
     * Specialization: INTELLIGENCE
     * AI Strategy: Adaptive - adjusts strategy based on player HP percentage
     * 
     * Strategy: Precision fighter with high accuracy and CDR
     * Threat: Rarely misses, abilities come back faster
     * Counter: Mage (burst vs burst) or tank through damage
     * 
     * Skills:
     * - Mind Spike: 7 damage, 0 cooldown (basic attack)
     * - Psychic Blast: 19 damage, 3 cooldown
     * - Mind Shatter: 40 damage, 5 cooldown (ultimate)
     * 
     * At 20 STR:
     * - Mind Spike: 27 total damage
     * - Psychic Blast: 39 total damage
     * - Mind Shatter: 60 total damage (powerful finisher)
     * 
     * Special: INT 30 gives CDR 1 (abilities come back 1 turn faster!)
     */
    private static Enemy createMindflayer() {
        // INT specialist: baseline 20, specialized 30
        Stat stats = new Stat(20, 20, 30);

        Skill[] skills = new Skill[] {
            new Skill("Mind Spike", null, 7, 0),      // Basic
            new Skill("Psychic Blast", null, 19, 3),  // Normal
            new Skill("Mind Shatter", null, 40, 5)    // Ultimate
        };

        return new Enemy("Mindflayer", stats, skills);
    }

    // ===== ENEMY INFORMATION =====

    /**
     * Get total number of enemy types.
     * 
     * @return Number of enemy types (currently 3)
     */
    public static int getEnemyCount() {
        return 3;
    }

    /**
     * Get all enemy names.
     * 
     * @return Array of enemy names
     */
    public static String[] getEnemyNames() {
        return new String[] {
            "Killer Bunny",
            "Minotaur",
            "Mindflayer"
        };
    }

    /**
     * Get specialization for an enemy by name.
     * 
     * @param enemyName Enemy name
     * @return Specialization ("STRENGTH", "AGILITY", "INTELLIGENCE")
     */
    public static String getEnemySpecialization(String enemyName) {
        if (enemyName == null) return "UNKNOWN";

        if (enemyName.equalsIgnoreCase("Killer Bunny")) {
            return "AGILITY";
        } else if (enemyName.equalsIgnoreCase("Minotaur")) {
            return "STRENGTH";
        } else if (enemyName.equalsIgnoreCase("Mindflayer")) {
            return "INTELLIGENCE";
        }

        return "UNKNOWN";
    }

    /**
     * Get AI description for an enemy by name.
     * 
     * @param enemyName Enemy name
     * @return AI strategy description
     */
    public static String getEnemyAIDescription(String enemyName) {
        if (enemyName == null) return "Unknown";

        if (enemyName.equalsIgnoreCase("Killer Bunny")) {
            return "Aggressive: Prioritizes burst damage";
        } else if (enemyName.equalsIgnoreCase("Minotaur")) {
            return "Strategic: Uses ultimate for executes";
        } else if (enemyName.equalsIgnoreCase("Mindflayer")) {
            return "Adaptive: Adjusts strategy based on your HP";
        }

        return "Standard AI";
    }
}
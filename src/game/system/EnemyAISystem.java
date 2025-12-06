package game.system;

import game.core.Enemy;
import game.core.Player;
import game.core.Skill;

/**
 * EnemyAISystem handles enemy decision-making during combat.
 * Each enemy type has a unique AI strategy for skill selection.
 * 
 * Enemy AI Strategies:
 * - Killer Bunny: Aggressive burst - always uses highest damage available skill
 * - Minotaur: Strategic execute - uses ultimate on first turn and for kills
 * - Mindflayer: Adaptive intelligence - adjusts strategy based on player HP
 * 
 * Responsibilities:
 * - Choose which skill enemy should use
 * - Implement unique AI behavior per enemy type
 * - Provide AI intent prediction (what enemy will do)
 * - Query AI information
 * 
 * Design: Stateless decision system - makes decisions based on current state.
 * GUI-Friendly: Returns simple indices and provides intent descriptions.
 */
public class EnemyAISystem {

    private final EntitySystem entitySystem;
    private final SkillSystem skillSystem;
    private final CooldownSystem cooldownSystem;

    public EnemyAISystem(EntitySystem entitySystem, SkillSystem skillSystem, CooldownSystem cooldownSystem) {
        if (entitySystem == null || skillSystem == null || cooldownSystem == null) {
            throw new IllegalArgumentException("All systems must be non-null");
        }
        
        this.entitySystem = entitySystem;
        this.skillSystem = skillSystem;
        this.cooldownSystem = cooldownSystem;
    }

    // ===== AI DECISION CLASS =====

    /**
     * AIDecision holds the result of AI decision-making.
     * Contains skill choice and reasoning.
     */
    public static class AIDecision {
        private final int skillIndex;
        private final String skillName;
        private final String reasoning;
        private final boolean isBasicAttack;

        public AIDecision(int skillIndex, String skillName, String reasoning, boolean isBasicAttack) {
            this.skillIndex = skillIndex;
            this.skillName = skillName;
            this.reasoning = reasoning;
            this.isBasicAttack = isBasicAttack;
        }

        public int getSkillIndex() { return skillIndex; }
        public String getSkillName() { return skillName; }
        public String getReasoning() { return reasoning; }
        public boolean isBasicAttack() { return isBasicAttack; }

        @Override
        public String toString() {
            return skillName + " - " + reasoning;
        }
    }

    // ===== MAIN AI DECISION =====

    /**
     * Choose which skill the enemy should use.
     * Routes to specific AI based on enemy type.
     * 
     * @param enemy The enemy choosing
     * @param player The player target
     * @return AIDecision with skill choice and reasoning
     */
    public AIDecision chooseSkill(Enemy enemy, Player player) {
        if (enemy == null || player == null) {
            return new AIDecision(-1, "Basic Attack", "Invalid state", true);
        }

        if (!enemy.hasSkills()) {
            return new AIDecision(-1, "Basic Attack", "No skills available", true);
        }

        String enemyName = enemy.getName();
        Skill[] skills = enemy.getSkills();

        // Route to specific AI
        if (enemyName.equalsIgnoreCase("Killer Bunny")) {
            return killerBunnyAI(enemy, player, skills);
        } else if (enemyName.equalsIgnoreCase("Minotaur")) {
            return minotaurAI(enemy, player, skills);
        } else if (enemyName.equalsIgnoreCase("Mindflayer")) {
            return mindflayerAI(enemy, player, skills);
        }

        // Default AI
        return defaultAI(enemy, skills);
    }

    /**
     * Get the skill index the enemy will use (simplified method).
     * Returns -1 for basic attack.
     * 
     * @param enemy The enemy
     * @param player The player
     * @return Skill index or -1
     */
    public int chooseSkillIndex(Enemy enemy, Player player) {
        AIDecision decision = chooseSkill(enemy, player);
        return decision.getSkillIndex();
    }

    // ===== KILLER BUNNY AI =====

    /**
     * Killer Bunny AI: Aggressive Burst Damage
     * 
     * Strategy: Always tries to use the highest damage skill available.
     * Priority: Ultimate (2) > 2nd Skill (1) > Basic (0)
     * 
     * Behavior:
     * - Extremely aggressive, no tactical thinking
     * - Maximizes damage output every turn
     * - Simple but deadly if unchecked
     */
    private AIDecision killerBunnyAI(Enemy enemy, Player player, Skill[] skills) {
        // Try ultimate first (highest damage)
        if (skills.length > 2 && cooldownSystem.isSkillReady(enemy, skills[2])) {
            return new AIDecision(2, skills[2].getName(), 
                "Maximum burst damage", false);
        }

        // Try 2nd skill
        if (skills.length > 1 && cooldownSystem.isSkillReady(enemy, skills[1])) {
            return new AIDecision(1, skills[1].getName(), 
                "High damage attack", false);
        }

        // Fall back to basic attack
        if (skills.length > 0 && cooldownSystem.isSkillReady(enemy, skills[0])) {
            return new AIDecision(0, skills[0].getName(), 
                "Basic attack", false);
        }

        // All skills on cooldown
        return new AIDecision(-1, "Basic Attack", 
            "All skills on cooldown", true);
    }

    // ===== MINOTAUR AI =====

    /**
     * Minotaur AI: Strategic Execute
     * 
     * Strategy: Uses ultimate strategically for maximum impact.
     * - First turn: ALWAYS use ultimate (intimidation + pressure)
     * - If ultimate can kill: Use it (execute)
     * - Otherwise: Conservative rotation (2nd skill > basic)
     * 
     * Behavior:
     * - Patient and calculating
     * - Saves ultimate for guaranteed kills
     * - Opens with ultimate for psychological advantage
     */
    private AIDecision minotaurAI(Enemy enemy, Player player, Skill[] skills) {
        if (skills.length < 3) return defaultAI(enemy, skills);

        Skill ultimate = skills[2];
        Skill secondSkill = skills[1];
        Skill basic = skills[0];

        boolean ultimateReady = cooldownSystem.isSkillReady(enemy, ultimate);

        if (ultimateReady) {
            // Calculate if ultimate can kill
            int ultimateDamage = skillSystem.calculateDamage(enemy, ultimate);
            int playerHP = entitySystem.getCurrentHP(player);

            // Use ultimate if it can kill
            if (ultimateDamage >= playerHP) {
                return new AIDecision(2, ultimate.getName(), 
                    "Execute! (" + ultimateDamage + " damage >= " + playerHP + " HP)", false);
            }

            // First turn logic: use ultimate if player at full HP
            if (playerHP == entitySystem.getMaxHP(player)) {
                return new AIDecision(2, ultimate.getName(), 
                    "Opening intimidation strike", false);
            }
        }

        // Conservative rotation: 2nd skill if available
        if (cooldownSystem.isSkillReady(enemy, secondSkill)) {
            return new AIDecision(1, secondSkill.getName(), 
                "Steady pressure", false);
        }

        // Basic attack
        if (cooldownSystem.isSkillReady(enemy, basic)) {
            return new AIDecision(0, basic.getName(), 
                "Waiting for ultimate", false);
        }

        // Fallback
        return new AIDecision(-1, "Basic Attack", 
            "All skills on cooldown", true);
    }

    // ===== MINDFLAYER AI =====

    /**
     * Mindflayer AI: Adaptive Intelligence
     * 
     * Strategy: Adjusts aggression based on player HP percentage.
     * 
     * High HP Phase (>70%): Conservative
     * - Uses 2nd skill for poke damage
     * - Saves ultimate for later
     * - Only uses ultimate if 2nd skill unavailable
     * 
     * Medium HP Phase (40-70%): Calculated
     * - Looks for ultimate combo opportunities
     * - Prefers 2nd skill for steady damage
     * - Uses ultimate if it sets up finish
     * 
     * Low HP Phase (<40%): Aggressive Finisher
     * - Prioritizes ultimate for kill
     * - Spams high damage skills
     * - Goes all-in for the finish
     * 
     * Behavior:
     * - Most intelligent AI
     * - Adapts strategy dynamically
     * - Plans ahead for kill combos
     */
    private AIDecision mindflayerAI(Enemy enemy, Player player, Skill[] skills) {
        if (skills.length < 3) return defaultAI(enemy, skills);

        Skill ultimate = skills[2];
        Skill secondSkill = skills[1];
        Skill basic = skills[0];

        // Calculate HP percentage
        int playerHP = entitySystem.getCurrentHP(player);
        int playerMaxHP = entitySystem.getMaxHP(player);
        double hpPercent = (double) playerHP / playerMaxHP;

        // Calculate potential damages
        int ultimateDamage = skillSystem.calculateDamage(enemy, ultimate);
        int secondDamage = skillSystem.calculateDamage(enemy, secondSkill);

        boolean ultimateReady = cooldownSystem.isSkillReady(enemy, ultimate);
        boolean secondReady = cooldownSystem.isSkillReady(enemy, secondSkill);

        // === LOW HP PHASE: Aggressive Finisher (<40%) ===
        if (hpPercent < 0.4) {
            // Try to finish with ultimate
            if (ultimateReady) {
                if (ultimateDamage >= playerHP) {
                    return new AIDecision(2, ultimate.getName(), 
                        "Lethal: " + ultimateDamage + " damage for kill!", false);
                } else {
                    return new AIDecision(2, ultimate.getName(), 
                        "Maximum damage - going for kill", false);
                }
            }

            // Use 2nd skill for high damage
            if (secondReady) {
                return new AIDecision(1, secondSkill.getName(), 
                    "High damage pressure", false);
            }

            // Spam basic
            if (cooldownSystem.isSkillReady(enemy, basic)) {
                return new AIDecision(0, basic.getName(), 
                    "Waiting for cooldowns", false);
            }
        }

        // === MEDIUM HP PHASE: Calculated Aggression (40-70%) ===
        if (hpPercent < 0.7) {
            // Check if ultimate can set up kill combo
            if (ultimateReady) {
                int remainingHP = playerHP - ultimateDamage;
                if (remainingHP > 0 && remainingHP < secondDamage * 2) {
                    return new AIDecision(2, ultimate.getName(), 
                        "Setting up kill combo", false);
                }
            }

            // Prefer 2nd skill for steady pressure
            if (secondReady) {
                return new AIDecision(1, secondSkill.getName(), 
                    "Steady damage buildup", false);
            }

            // Use ultimate if 2nd skill on cooldown
            if (ultimateReady) {
                return new AIDecision(2, ultimate.getName(), 
                    "Efficient cooldown usage", false);
            }

            // Basic filler
            if (cooldownSystem.isSkillReady(enemy, basic)) {
                return new AIDecision(0, basic.getName(), 
                    "Efficient resource management", false);
            }
        }

        // === HIGH HP PHASE: Conservative Poke (>70%) ===
        // Use 2nd skill for poke damage
        if (secondReady) {
            return new AIDecision(1, secondSkill.getName(), 
                "Efficient poke damage", false);
        }

        // Use ultimate if 2nd unavailable (don't waste it sitting ready)
        if (ultimateReady && !secondReady) {
            return new AIDecision(2, ultimate.getName(), 
                "Avoiding wasted cooldown uptime", false);
        }

        // Default to basic
        if (cooldownSystem.isSkillReady(enemy, basic)) {
            return new AIDecision(0, basic.getName(), 
                "Conservative approach", false);
        }

        // Fallback
        return new AIDecision(-1, "Basic Attack", 
            "All skills on cooldown", true);
    }

    // ===== DEFAULT AI =====

    /**
     * Default AI: Simple Priority
     * Uses highest index available skill (usually highest damage).
     */
    private AIDecision defaultAI(Enemy enemy, Skill[] skills) {
        // Try skills from highest to lowest index
        for (int i = skills.length - 1; i >= 0; i--) {
            if (cooldownSystem.isSkillReady(enemy, skills[i])) {
                return new AIDecision(i, skills[i].getName(), 
                    "Highest available skill", false);
            }
        }

        return new AIDecision(-1, "Basic Attack", 
            "All skills on cooldown", true);
    }

    // ===== AI INTENT (PREDICTION) =====

    /**
     * Get what skill the enemy intends to use.
     * Returns skill name for display purposes (shows player what's coming).
     * 
     * @param enemy The enemy
     * @param player The player
     * @return Skill name enemy will use
     */
    public String getEnemyIntent(Enemy enemy, Player player) {
        AIDecision decision = chooseSkill(enemy, player);
        return decision.getSkillName();
    }

    /**
     * Get detailed AI decision with reasoning.
     * Useful for debugging or advanced UI tooltips.
     * 
     * @param enemy The enemy
     * @param player The player
     * @return Full AIDecision with reasoning
     */
    public AIDecision getEnemyIntentDetailed(Enemy enemy, Player player) {
        return chooseSkill(enemy, player);
    }

    // ===== AI INFORMATION =====

    /**
     * Get AI strategy description for an enemy type.
     * 
     * @param enemyName Enemy name
     * @return AI strategy description
     */
    public String getAIDescription(String enemyName) {
        if (enemyName == null) return "Standard AI";

        if (enemyName.equalsIgnoreCase("Killer Bunny")) {
            return "Aggressive: Prioritizes burst damage";
        } else if (enemyName.equalsIgnoreCase("Minotaur")) {
            return "Strategic: Uses ultimate for executes";
        } else if (enemyName.equalsIgnoreCase("Mindflayer")) {
            return "Adaptive: Adjusts strategy based on your HP";
        }

        return "Standard: Uses available skills";
    }

    /**
     * Get AI type/category.
     * 
     * @param enemyName Enemy name
     * @return AI type (AGGRESSIVE, STRATEGIC, ADAPTIVE, DEFAULT)
     */
    public AIType getAIType(String enemyName) {
        if (enemyName == null) return AIType.DEFAULT;

        if (enemyName.equalsIgnoreCase("Killer Bunny")) {
            return AIType.AGGRESSIVE;
        } else if (enemyName.equalsIgnoreCase("Minotaur")) {
            return AIType.STRATEGIC;
        } else if (enemyName.equalsIgnoreCase("Mindflayer")) {
            return AIType.ADAPTIVE;
        }

        return AIType.DEFAULT;
    }

    public enum AIType {
        AGGRESSIVE,
        STRATEGIC,
        ADAPTIVE,
        DEFAULT
    }

    /**
     * Check if enemy can execute player (has a skill that can kill).
     * 
     * @param enemy The enemy
     * @param player The player
     * @return true if enemy has a ready skill that can kill
     */
    public boolean canExecute(Enemy enemy, Player player) {
        if (enemy == null || player == null || !enemy.hasSkills()) {
            return false;
        }

        int playerHP = entitySystem.getCurrentHP(player);

        for (Skill skill : enemy.getSkills()) {
            if (cooldownSystem.isSkillReady(enemy, skill)) {
                int damage = skillSystem.calculateDamage(enemy, skill);
                if (damage >= playerHP) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get which skill can execute player.
     * Returns null if no execute available.
     * 
     * @param enemy The enemy
     * @param player The player
     * @return Skill that can kill, or null
     */
    public Skill getExecuteSkill(Enemy enemy, Player player) {
        if (enemy == null || player == null || !enemy.hasSkills()) {
            return null;
        }

        int playerHP = entitySystem.getCurrentHP(player);

        for (Skill skill : enemy.getSkills()) {
            if (cooldownSystem.isSkillReady(enemy, skill)) {
                int damage = skillSystem.calculateDamage(enemy, skill);
                if (damage >= playerHP) {
                    return skill;
                }
            }
        }

        return null;
    }

    // ===== GUI HELPER METHODS =====

    /**
     * Get formatted intent display for UI.
     * Shows what enemy will do next turn.
     * 
     * @param enemy The enemy
     * @param player The player
     * @return Formatted intent string (e.g., "⚠ Earthquake (42 DMG)")
     */
    public String getFormattedIntent(Enemy enemy, Player player) {
        AIDecision decision = chooseSkill(enemy, player);
        
        if (decision.isBasicAttack()) {
            int damage = enemy.getStats().getStrength();
            return "Basic Attack (" + damage + " DMG)";
        }

        int skillIndex = decision.getSkillIndex();
        if (skillIndex >= 0 && skillIndex < enemy.getSkills().length) {
            Skill skill = enemy.getSkills()[skillIndex];
            int damage = skillSystem.calculateDamage(enemy, skill);
            
            // Add warning if it can kill
            if (damage >= entitySystem.getCurrentHP(player)) {
                return "⚠ " + decision.getSkillName() + " (" + damage + " DMG - LETHAL!)";
            }
            
            return decision.getSkillName() + " (" + damage + " DMG)";
        }

        return decision.getSkillName();
    }

    /**
     * Get AI threat level (0-3).
     * 0 = Low threat (basic attacks)
     * 1 = Medium threat (normal skills)
     * 2 = High threat (ultimate available)
     * 3 = Critical threat (can execute)
     * 
     * @param enemy The enemy
     * @param player The player
     * @return Threat level 0-3
     */
    public int getThreatLevel(Enemy enemy, Player player) {
        if (enemy == null || player == null) return 0;

        // Critical: Can execute
        if (canExecute(enemy, player)) {
            return 3;
        }

        AIDecision decision = chooseSkill(enemy, player);

        // Basic attack only
        if (decision.isBasicAttack()) {
            return 0;
        }

        int skillIndex = decision.getSkillIndex();

        // High: Ultimate available (assuming index 2 is ultimate)
        if (skillIndex == 2) {
            return 2;
        }

        // Medium: Other skills
        if (skillIndex > 0) {
            return 1;
        }

        return 0;
    }

    // ===== ACCESSORS =====

    public EntitySystem getEntitySystem() { return entitySystem; }
    public SkillSystem getSkillSystem() { return skillSystem; }
    public CooldownSystem getCooldownSystem() { return cooldownSystem; }
}
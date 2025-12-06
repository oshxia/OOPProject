package game.system;

import game.core.Player;
import game.core.Enemy;
import game.core.Skill;
import game.core.Profession;

/**
 * GameFlowSystem orchestrates the overall game flow and state management.
 * Coordinates all other systems to manage the complete game experience.
 * 
 * Responsibilities:
 * - Initialize new game sessions
 * - Manage game state (which enemy, which phase)
 * - Coordinate training phases
 * - Coordinate battle phases
 * - Track game progression
 * - Provide game state queries
 * 
 * Game Loop:
 * 1. Initialize game (create player, load enemies)
 * 2. For each enemy:
 *    a. Training Phase (multiple cycles)
 *    b. Battle Phase (until one dies)
 * 3. Victory or Defeat
 * 
 * Design: Stateful coordinator - stores game state and orchestrates systems.
 * GUI-Friendly: Returns simple state values and provides clear queries.
 */
public class GameFlowSystem {

    // Systems
    private final EntitySystem entitySystem;
    private final PlayerTrainingSystem playerTrainingSystem;
    private final EnemyTrainingSystem enemyTrainingSystem;
    private final SkillSystem skillSystem;
    private final CooldownSystem cooldownSystem;
    private final CombatSystem combatSystem;
    private final EnemyAISystem enemyAISystem;

    // Game state
    private Player player;
    private Skill[] playerSkills;
    private Enemy[] enemies;
    private int currentEnemyIndex;
    private GamePhase currentPhase;
    private boolean gameInitialized;
    private boolean gameOver;

    // Training state
    private int currentTrainingCycle;
    private int totalTrainingCycles;

    public GameFlowSystem(EntitySystem entitySystem,
                         PlayerTrainingSystem playerTrainingSystem,
                         EnemyTrainingSystem enemyTrainingSystem,
                         SkillSystem skillSystem,
                         CooldownSystem cooldownSystem,
                         CombatSystem combatSystem,
                         EnemyAISystem enemyAISystem) {
        if (entitySystem == null || playerTrainingSystem == null ||
            enemyTrainingSystem == null || skillSystem == null ||
            cooldownSystem == null || combatSystem == null ||
            enemyAISystem == null) {
            throw new IllegalArgumentException("All systems must be non-null");
        }

        this.entitySystem = entitySystem;
        this.playerTrainingSystem = playerTrainingSystem;
        this.enemyTrainingSystem = enemyTrainingSystem;
        this.skillSystem = skillSystem;
        this.cooldownSystem = cooldownSystem;
        this.combatSystem = combatSystem;
        this.enemyAISystem = enemyAISystem;

        this.currentEnemyIndex = 0;
        this.currentPhase = GamePhase.NOT_STARTED;
        this.gameInitialized = false;
        this.gameOver = false;
        this.currentTrainingCycle = 0;
        this.totalTrainingCycles = 0;
    }

    // ===== GAME PHASE ENUM =====

    public enum GamePhase {
        NOT_STARTED,
        TRAINING,
        BATTLE,
        VICTORY,
        DEFEAT
    }

    // ===== GAME INITIALIZATION =====

    /**
     * Initialize a new game with player and enemies.
     * 
     * @param playerName Player's name
     * @param profession Player's profession
     * @param playerSkills Array of skills for player
     * @param enemies Array of enemies to face
     * @return true if initialization successful
     */
    public boolean initializeGame(String playerName, Profession profession,
                                  Skill[] playerSkills, Enemy[] enemies) {
        if (playerName == null || profession == null || 
            playerSkills == null || enemies == null) {
            return false;
        }

        if (playerSkills.length == 0 || enemies.length == 0) {
            return false;
        }

        // Create player with default stats
        this.player = entitySystem.createPlayer(playerName, profession);
        this.playerSkills = playerSkills;
        this.enemies = enemies;
        this.currentEnemyIndex = 0;
        this.currentPhase = GamePhase.TRAINING;
        this.gameInitialized = true;
        this.gameOver = false;

        return true;
    }

    /**
     * Initialize game with existing player and enemies.
     * Useful for testing or custom scenarios.
     * 
     * @param player Pre-created player
     * @param playerSkills Player's skills
     * @param enemies Array of enemies
     * @return true if successful
     */
    public boolean initializeGame(Player player, Skill[] playerSkills, Enemy[] enemies) {
        if (player == null || playerSkills == null || enemies == null) {
            return false;
        }

        this.player = player;
        this.playerSkills = playerSkills;
        this.enemies = enemies;
        this.currentEnemyIndex = 0;
        this.currentPhase = GamePhase.TRAINING;
        this.gameInitialized = true;
        this.gameOver = false;

        return true;
    }

    /**
     * Reset game state for a new game.
     */
    public void resetGame() {
        this.player = null;
        this.playerSkills = null;
        this.enemies = null;
        this.currentEnemyIndex = 0;
        this.currentPhase = GamePhase.NOT_STARTED;
        this.gameInitialized = false;
        this.gameOver = false;
        this.currentTrainingCycle = 0;
        this.totalTrainingCycles = 0;
    }

    // ===== TRAINING PHASE MANAGEMENT =====

    /**
     * Start training phase for current enemy.
     * 
     * @param cycles Number of training cycles
     * @return true if training phase started
     */
    public boolean startTrainingPhase(int cycles) {
        if (!gameInitialized || gameOver) return false;
        if (currentPhase != GamePhase.TRAINING) return false;

        this.totalTrainingCycles = Math.max(1, cycles);
        this.currentTrainingCycle = 0;
        return true;
    }

    /**
     * Execute one training cycle for player.
     * 
     * @param stat Stat to train ("STR", "AGI", "INT")
     * @param amount Amount to train
     * @return PlayerTrainingSystem.TrainingResult
     */
    public PlayerTrainingSystem.TrainingResult trainPlayer(String stat, int amount) {
        if (!gameInitialized || gameOver) {
            return new PlayerTrainingSystem.TrainingResult(false, "Unknown", "", 0, 0, 0,
                "Game not initialized");
        }

        if (currentPhase != GamePhase.TRAINING) {
            return new PlayerTrainingSystem.TrainingResult(false, player.getName(), "", 0, 0, 0,
                "Not in training phase");
        }

        return playerTrainingSystem.trainStat(player, stat, amount);
    }

    /**
     * Execute group training for all enemies.
     * 
     * @param amount Amount to train
     * @return EnemyTrainingSystem.GroupTrainingResult
     */
    public EnemyTrainingSystem.GroupTrainingResult trainEnemies(int amount) {
        if (!gameInitialized || gameOver) {
            return new EnemyTrainingSystem.GroupTrainingResult(false, null,
                "Game not initialized");
        }

        if (currentPhase != GamePhase.TRAINING) {
            return new EnemyTrainingSystem.GroupTrainingResult(false, null,
                "Not in training phase");
        }

        return enemyTrainingSystem.trainGroup(enemies, amount);
    }

    /**
     * Complete current training cycle.
     * Advances to next cycle or moves to battle phase.
     * 
     * @return true if cycle completed
     */
    public boolean completeTrainingCycle() {
        if (!gameInitialized || gameOver) return false;
        if (currentPhase != GamePhase.TRAINING) return false;

        currentTrainingCycle++;

        if (currentTrainingCycle >= totalTrainingCycles) {
            // Training complete, move to battle
            currentPhase = GamePhase.BATTLE;
            return true;
        }

        return true;
    }

    /**
     * Check if training phase is complete.
     * 
     * @return true if all training cycles done
     */
    public boolean isTrainingComplete() {
        return currentTrainingCycle >= totalTrainingCycles;
    }

    // ===== BATTLE PHASE MANAGEMENT =====

    /**
     * Start battle phase with current enemy.
     * Prepares both combatants (heal + reset cooldowns).
     * 
     * @return true if battle started
     */
    public boolean startBattlePhase() {
        if (!gameInitialized || gameOver) return false;
        if (currentEnemyIndex >= enemies.length) return false;

        currentPhase = GamePhase.BATTLE;
        
        // Prepare both combatants
        Enemy currentEnemy = enemies[currentEnemyIndex];
        combatSystem.prepareBattle(player, currentEnemy);

        return true;
    }

    /**
     * Execute player attack in battle.
     * 
     * @param skillIndex Index of skill to use
     * @return CombatSystem.CombatResult
     */
    public CombatSystem.CombatResult playerAttack(int skillIndex) {
        if (!gameInitialized || gameOver) {
            return new CombatSystem.CombatResult(false, false, 0, "Unknown", "Unknown", "Unknown",
                "Game not initialized");
        }

        if (currentPhase != GamePhase.BATTLE) {
            return new CombatSystem.CombatResult(false, false, 0, player.getName(), "Unknown", "Unknown",
                "Not in battle phase");
        }

        if (currentEnemyIndex >= enemies.length) {
            return new CombatSystem.CombatResult(false, false, 0, player.getName(), "Unknown", "Unknown",
                "No enemy available");
        }

        Enemy currentEnemy = enemies[currentEnemyIndex];
        return combatSystem.playerAttack(player, currentEnemy, playerSkills, skillIndex);
    }

    /**
     * Execute player attack with specific skill.
     * 
     * @param skill Skill to use
     * @return CombatSystem.CombatResult
     */
    public CombatSystem.CombatResult playerAttack(Skill skill) {
        if (!gameInitialized || gameOver) {
            return new CombatSystem.CombatResult(false, false, 0, "Unknown", "Unknown", "Unknown",
                "Game not initialized");
        }

        if (currentPhase != GamePhase.BATTLE) {
            return new CombatSystem.CombatResult(false, false, 0, player.getName(), "Unknown", "Unknown",
                "Not in battle phase");
        }

        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) {
            return new CombatSystem.CombatResult(false, false, 0, player.getName(), "Unknown", "Unknown",
                "No enemy available");
        }

        return combatSystem.playerAttack(player, currentEnemy, skill);
    }

    /**
     * Execute enemy attack in battle using AI.
     * 
     * @return CombatSystem.CombatResult
     */
    public CombatSystem.CombatResult enemyAttack() {
        if (!gameInitialized || gameOver) {
            return new CombatSystem.CombatResult(false, false, 0, "Unknown", "Unknown", "Unknown",
                "Game not initialized");
        }

        if (currentPhase != GamePhase.BATTLE) {
            return new CombatSystem.CombatResult(false, false, 0, "Unknown", player.getName(), "Unknown",
                "Not in battle phase");
        }

        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) {
            return new CombatSystem.CombatResult(false, false, 0, "Unknown", player.getName(), "Unknown",
                "No enemy available");
        }

        // Use AI to choose skill
        EnemyAISystem.AIDecision decision = enemyAISystem.chooseSkill(currentEnemy, player);

        if (decision.isBasicAttack()) {
            return combatSystem.enemyBasicAttack(currentEnemy, player);
        }

        int skillIndex = decision.getSkillIndex();
        Skill skill = currentEnemy.getSkills()[skillIndex];
        
        return combatSystem.enemyAttack(currentEnemy, player, skill);
    }

    /**
     * Check if current battle is over.
     * 
     * @return true if player or enemy is dead
     */
    public boolean isBattleOver() {
        if (!gameInitialized || currentPhase != GamePhase.BATTLE) return false;
        
        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) return true;

        return combatSystem.isCombatOver(player, currentEnemy);
    }

    /**
     * Complete current battle and determine outcome.
     * Advances to next enemy or ends game.
     * 
     * @return BattleOutcome (PLAYER_WIN, ENEMY_WIN, or ONGOING)
     */
    public BattleOutcome completeBattle() {
        if (!gameInitialized || currentPhase != GamePhase.BATTLE) {
            return BattleOutcome.ONGOING;
        }

        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) return BattleOutcome.ONGOING;

        int winner = combatSystem.getCombatWinner(player, currentEnemy);

        if (winner == 1) {
            // Player won
            currentEnemyIndex++;

            if (currentEnemyIndex >= enemies.length) {
                // All enemies defeated
                currentPhase = GamePhase.VICTORY;
                gameOver = true;
                return BattleOutcome.PLAYER_WIN;
            } else {
                // Move to next enemy training
                currentPhase = GamePhase.TRAINING;
                currentTrainingCycle = 0;
                return BattleOutcome.PLAYER_WIN;
            }
        } else if (winner == -1) {
            // Enemy won
            currentPhase = GamePhase.DEFEAT;
            gameOver = true;
            return BattleOutcome.ENEMY_WIN;
        }

        return BattleOutcome.ONGOING;
    }

    public enum BattleOutcome {
        PLAYER_WIN,
        ENEMY_WIN,
        ONGOING
    }

    // ===== TURN MANAGEMENT =====

    /**
     * Tick cooldowns for both player and current enemy.
     * Call this at the start of each turn.
     */
    public void tickCooldowns() {
        if (!gameInitialized || currentPhase != GamePhase.BATTLE) return;
        
        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy != null) {
            cooldownSystem.tickAllCooldowns(player, currentEnemy);
        }
    }

    // ===== ENEMY AI QUERIES =====

    /**
     * Get what the enemy intends to do next turn.
     * Shows player what's coming (formatted for display).
     * 
     * @return Formatted intent string (e.g., "⚠ Earthquake (42 DMG - LETHAL!)")
     */
    public String getEnemyIntent() {
        if (!gameInitialized || currentPhase != GamePhase.BATTLE) {
            return "Unknown";
        }

        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) return "Unknown";

        return enemyAISystem.getFormattedIntent(currentEnemy, player);
    }

    /**
     * Get detailed AI decision with reasoning.
     * 
     * @return AIDecision with skill choice and reasoning
     */
    public EnemyAISystem.AIDecision getEnemyIntentDetailed() {
        if (!gameInitialized || currentPhase != GamePhase.BATTLE) {
            return null;
        }

        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) return null;

        return enemyAISystem.chooseSkill(currentEnemy, player);
    }

    /**
     * Get enemy threat level (0-3).
     * 0 = Low, 1 = Medium, 2 = High, 3 = Critical (can kill)
     * 
     * @return Threat level
     */
    public int getEnemyThreatLevel() {
        if (!gameInitialized || currentPhase != GamePhase.BATTLE) {
            return 0;
        }

        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) return 0;

        return enemyAISystem.getThreatLevel(currentEnemy, player);
    }

    /**
     * Check if enemy can kill player this turn.
     * 
     * @return true if enemy has a lethal skill ready
     */
    public boolean canEnemyExecute() {
        if (!gameInitialized || currentPhase != GamePhase.BATTLE) {
            return false;
        }

        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) return false;

        return enemyAISystem.canExecute(currentEnemy, player);
    }

    /**
     * Get AI description for current enemy.
     * 
     * @return AI strategy description
     */
    public String getEnemyAIDescription() {
        Enemy currentEnemy = getCurrentEnemy();
        if (currentEnemy == null) return "Unknown";

        return enemyAISystem.getAIDescription(currentEnemy.getName());
    }

    // ===== GAME STATE QUERIES =====

    /**
     * Check if game is initialized.
     */
    public boolean isGameInitialized() {
        return gameInitialized;
    }

    /**
     * Check if game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Get current game phase.
     */
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Get current enemy index.
     */
    public int getCurrentEnemyIndex() {
        return currentEnemyIndex;
    }

    /**
     * Get current enemy.
     */
    public Enemy getCurrentEnemy() {
        if (currentEnemyIndex >= 0 && currentEnemyIndex < enemies.length) {
            return enemies[currentEnemyIndex];
        }
        return null;
    }

    /**
     * Get total number of enemies.
     */
    public int getTotalEnemies() {
        return enemies != null ? enemies.length : 0;
    }

    /**
     * Get current training cycle number (0-based).
     */
    public int getCurrentTrainingCycle() {
        return currentTrainingCycle;
    }

    /**
     * Get total training cycles.
     */
    public int getTotalTrainingCycles() {
        return totalTrainingCycles;
    }

    /**
     * Get remaining training cycles.
     */
    public int getRemainingTrainingCycles() {
        return Math.max(0, totalTrainingCycles - currentTrainingCycle);
    }

    /**
     * Get game progress as percentage (0.0 to 1.0).
     * Based on enemies defeated.
     */
    public double getGameProgress() {
        if (enemies == null || enemies.length == 0) return 0.0;
        return (double) currentEnemyIndex / enemies.length;
    }

    /**
     * Check if this is the final enemy.
     */
    public boolean isFinalEnemy() {
        return currentEnemyIndex == (enemies.length - 1);
    }

    // ===== ACCESSORS =====

    public Player getPlayer() { return player; }
    public Skill[] getPlayerSkills() { return playerSkills; }
    public Enemy[] getEnemies() { return enemies; }

    public EntitySystem getEntitySystem() { return entitySystem; }
    public PlayerTrainingSystem getPlayerTrainingSystem() { return playerTrainingSystem; }
    public EnemyTrainingSystem getEnemyTrainingSystem() { return enemyTrainingSystem; }
    public SkillSystem getSkillSystem() { return skillSystem; }
    public CooldownSystem getCooldownSystem() { return cooldownSystem; }
    public CombatSystem getCombatSystem() { return combatSystem; }
    public EnemyAISystem getEnemyAISystem() { return enemyAISystem; }
}
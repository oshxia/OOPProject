package game.system;

import game.core.Player;
import game.core.Enemy;
import java.util.*;

/**
 * ActionValueSystem manages turn order using Action Value (AV) mechanics.
 * Similar to HSR and Eternity Restart - faster units act more frequently.
 * 
 * HOW IT WORKS:
 * - Each combatant has an Action Value (AV) that counts down
 * - When AV reaches 0, the combatant takes their turn
 * - After acting, AV resets based on speed (faster = lower AV = more turns)
 * - Turn order is determined by who reaches 0 first
 * 
 * AV FORMULA:
 * - Base AV = 10000 / Speed
 * - Lower AV = Acts sooner and more frequently
 * - Example: Speed 100 = AV 100, Speed 200 = AV 50 (acts twice as often)
 * 
 * TURN CYCLE:
 * 1. Find combatant with lowest AV
 * 2. Advance time to that AV (subtract from all)
 * 3. That combatant acts (AV becomes 0)
 * 4. Reset their AV based on speed
 * 5. Repeat
 * 
 * Responsibilities:
 * - Track AV for all combatants
 * - Determine whose turn it is
 * - Advance AV after actions
 * - Calculate turn order preview
 * - Query AV state
 * 
 * Design: Stateful system - maintains AV state for current battle.
 * GUI-Friendly: Provides turn order queue and AV percentage for display.
 */
public class ActionValueSystem {

    private final EntitySystem entitySystem;
    
    // AV Constants
    private static final int BASE_AV = 10000;  // Base constant for AV calculation
    private static final int MIN_SPEED = 1;     // Minimum speed to prevent division by zero
    
    // Combatant tracking
    private CombatantAV playerAV;
    private CombatantAV enemyAV;
    
    private boolean battleActive;

    public ActionValueSystem(EntitySystem entitySystem) {
        if (entitySystem == null) {
            throw new IllegalArgumentException("EntitySystem cannot be null");
        }
        
        this.entitySystem = entitySystem;
        this.battleActive = false;
    }

    // ===== COMBATANT AV CLASS =====

    /**
     * Internal class to track AV state for a combatant.
     */
    private static class CombatantAV {
        private final String name;
        private final boolean isPlayer;
        private int speed;
        private double actionValue;
        
        public CombatantAV(String name, boolean isPlayer, int speed) {
            this.name = name;
            this.isPlayer = isPlayer;
            this.speed = Math.max(MIN_SPEED, speed);
            this.actionValue = calculateInitialAV(this.speed);
        }
        
        private double calculateInitialAV(int speed) {
            return (double) BASE_AV / Math.max(MIN_SPEED, speed);
        }
        
        public void resetAV() {
            this.actionValue = calculateInitialAV(this.speed);
        }
        
        public void reduceAV(double amount) {
            this.actionValue = Math.max(0, this.actionValue - amount);
        }
        
        public void updateSpeed(int newSpeed) {
            this.speed = Math.max(MIN_SPEED, newSpeed);
        }
        
        public String getName() { return name; }
        public boolean isPlayer() { return isPlayer; }
        public int getSpeed() { return speed; }
        public double getActionValue() { return actionValue; }
        
        @Override
        public String toString() {
            return name + " [AV: " + String.format("%.2f", actionValue) + ", SPD: " + speed + "]";
        }
    }

    // ===== TURN ORDER ENTRY =====

    /**
     * Represents a turn in the turn order queue.
     * Contains information about whose turn it is.
     */
    public static class TurnOrderEntry {
        private final String combatantName;
        private final boolean isPlayer;
        private final double actionValue;
        private final int turnNumber;
        
        public TurnOrderEntry(String name, boolean isPlayer, double av, int turnNumber) {
            this.combatantName = name;
            this.isPlayer = isPlayer;
            this.actionValue = av;
            this.turnNumber = turnNumber;
        }
        
        public String getCombatantName() { return combatantName; }
        public boolean isPlayer() { return isPlayer; }
        public double getActionValue() { return actionValue; }
        public int getTurnNumber() { return turnNumber; }
        
        @Override
        public String toString() {
            String type = isPlayer ? "[PLAYER]" : "[ENEMY]";
            return "Turn " + turnNumber + ": " + combatantName + " " + type + 
                   " (AV: " + String.format("%.2f", actionValue) + ")";
        }
    }

    // ===== BATTLE INITIALIZATION =====

    /**
     * Initialize battle with player and enemy.
     * Sets up initial AV values based on speed.
     * 
     * @param player The player
     * @param enemy The enemy
     * @return true if initialization successful
     */
    public boolean initializeBattle(Player player, Enemy enemy) {
        if (player == null || enemy == null) {
            return false;
        }
        
        int playerSpeed = entitySystem.getSpeed(player);
        int enemySpeed = entitySystem.getSpeed(enemy);
        
        this.playerAV = new CombatantAV(player.getName(), true, playerSpeed);
        this.enemyAV = new CombatantAV(enemy.getName(), false, enemySpeed);
        this.battleActive = true;
        
        return true;
    }

    /**
     * End current battle and clear AV state.
     */
    public void endBattle() {
        this.playerAV = null;
        this.enemyAV = null;
        this.battleActive = false;
    }

    /**
     * Check if battle is active.
     */
    public boolean isBattleActive() {
        return battleActive;
    }

    // ===== TURN DETERMINATION =====

    /**
     * Get whose turn it is (who has lowest AV).
     * Returns 1 for player, -1 for enemy, 0 if tie or invalid.
     * 
     * @return 1 = player's turn, -1 = enemy's turn, 0 = tie/invalid
     */
    public int getCurrentTurn() {
        if (!battleActive || playerAV == null || enemyAV == null) {
            return 0;
        }
        
        double playerCurrentAV = playerAV.getActionValue();
        double enemyCurrentAV = enemyAV.getActionValue();
        
        // Who has lower AV acts first
        if (playerCurrentAV < enemyCurrentAV) {
            return 1;  // Player's turn
        } else if (enemyCurrentAV < playerCurrentAV) {
            return -1; // Enemy's turn
        } else {
            // Tie: player acts first (tie-breaker)
            return 1;
        }
    }

    /**
     * Check if it's player's turn.
     */
    public boolean isPlayerTurn() {
        return getCurrentTurn() == 1;
    }

    /**
     * Check if it's enemy's turn.
     */
    public boolean isEnemyTurn() {
        return getCurrentTurn() == -1;
    }

    /**
     * Get name of combatant whose turn it is.
     */
    public String getCurrentTurnName() {
        if (!battleActive) return "Unknown";
        
        int turn = getCurrentTurn();
        if (turn == 1) return playerAV.getName();
        if (turn == -1) return enemyAV.getName();
        return "Unknown";
    }

    // ===== AV ADVANCEMENT =====

    /**
     * Advance to next turn.
     * Moves time forward so current actor reaches AV 0, then resets their AV.
     * 
     * WHEN TO CALL: After a combatant takes their action.
     * 
     * @return true if advancement successful
     */
    public boolean advanceToNextTurn() {
        if (!battleActive || playerAV == null || enemyAV == null) {
            return false;
        }
        
        // Find who acts (lowest AV)
        int currentTurn = getCurrentTurn();
        
        if (currentTurn == 1) {
            // Player acts
            double timeAdvance = playerAV.getActionValue();
            
            // Advance time (reduce all AVs)
            playerAV.reduceAV(timeAdvance);
            enemyAV.reduceAV(timeAdvance);
            
            // Reset player's AV
            playerAV.resetAV();
            
        } else if (currentTurn == -1) {
            // Enemy acts
            double timeAdvance = enemyAV.getActionValue();
            
            // Advance time
            playerAV.reduceAV(timeAdvance);
            enemyAV.reduceAV(timeAdvance);
            
            // Reset enemy's AV
            enemyAV.resetAV();
        }
        
        return true;
    }

    // ===== TURN ORDER PREVIEW =====

    /**
     * Calculate turn order for the next N turns.
     * Shows who will act and when (for UI display).
     * 
     * @param turnsAhead Number of turns to preview
     * @return List of turn order entries
     */
    public List<TurnOrderEntry> calculateTurnOrder(int turnsAhead) {
        if (!battleActive || playerAV == null || enemyAV == null) {
            return new ArrayList<>();
        }
        
        // Create simulation state (don't modify actual AV)
        double simPlayerAV = playerAV.getActionValue();
        double simEnemyAV = enemyAV.getActionValue();
        int playerSpeed = playerAV.getSpeed();
        int enemySpeed = enemyAV.getSpeed();
        
        List<TurnOrderEntry> turnOrder = new ArrayList<>();
        
        for (int i = 0; i < turnsAhead; i++) {
            // Find who acts next
            if (simPlayerAV <= simEnemyAV) {
                // Player acts
                turnOrder.add(new TurnOrderEntry(
                    playerAV.getName(), true, simPlayerAV, i + 1
                ));
                
                // Advance time
                double timeAdvance = simPlayerAV;
                simPlayerAV = 0;
                simEnemyAV -= timeAdvance;
                
                // Reset player AV
                simPlayerAV = (double) BASE_AV / playerSpeed;
                
            } else {
                // Enemy acts
                turnOrder.add(new TurnOrderEntry(
                    enemyAV.getName(), false, simEnemyAV, i + 1
                ));
                
                // Advance time
                double timeAdvance = simEnemyAV;
                simEnemyAV = 0;
                simPlayerAV -= timeAdvance;
                
                // Reset enemy AV
                simEnemyAV = (double) BASE_AV / enemySpeed;
            }
        }
        
        return turnOrder;
    }

    /**
     * Get simple turn order as string list.
     * Perfect for UI display.
     * 
     * @param turnsAhead Number of turns to preview
     * @return List of turn strings (e.g., "1. Hero", "2. Goblin", "3. Hero")
     */
    public List<String> getTurnOrderList(int turnsAhead) {
        List<TurnOrderEntry> order = calculateTurnOrder(turnsAhead);
        List<String> result = new ArrayList<>();
        
        for (TurnOrderEntry entry : order) {
            result.add(entry.getTurnNumber() + ". " + entry.getCombatantName());
        }
        
        return result;
    }

    // ===== AV QUERIES =====

    /**
     * Get player's current action value.
     */
    public double getPlayerAV() {
        return playerAV != null ? playerAV.getActionValue() : 0;
    }

    /**
     * Get enemy's current action value.
     */
    public double getEnemyAV() {
        return enemyAV != null ? enemyAV.getActionValue() : 0;
    }

    /**
     * Get player's speed.
     */
    public int getPlayerSpeed() {
        return playerAV != null ? playerAV.getSpeed() : 0;
    }

    /**
     * Get enemy's speed.
     */
    public int getEnemySpeed() {
        return enemyAV != null ? enemyAV.getSpeed() : 0;
    }

    /**
     * Get AV percentage for player (0.0 to 1.0).
     * 0.0 = ready to act (AV is 0)
     * 1.0 = just acted (AV is at max)
     * 
     * Perfect for progress bars showing "action gauge".
     * 
     * @return Percentage until next action (0.0 = ready, 1.0 = just acted)
     */
    public double getPlayerAVPercentage() {
        if (playerAV == null) return 0.0;
        
        double maxAV = (double) BASE_AV / playerAV.getSpeed();
        double currentAV = playerAV.getActionValue();
        
        // Invert so 0 = ready to act, 1 = just acted
        return Math.min(1.0, currentAV / maxAV);
    }

    /**
     * Get AV percentage for enemy.
     */
    public double getEnemyAVPercentage() {
        if (enemyAV == null) return 0.0;
        
        double maxAV = (double) BASE_AV / enemyAV.getSpeed();
        double currentAV = enemyAV.getActionValue();
        
        return Math.min(1.0, currentAV / maxAV);
    }

    /**
     * Get AV readiness percentage (inverse of AV percentage).
     * 0.0 = just acted, 1.0 = ready to act
     * 
     * More intuitive for "filling" progress bars.
     */
    public double getPlayerReadiness() {
        return 1.0 - getPlayerAVPercentage();
    }

    /**
     * Get enemy readiness percentage.
     */
    public double getEnemyReadiness() {
        return 1.0 - getEnemyAVPercentage();
    }

    // ===== SPEED UPDATES =====

    /**
     * Update player speed (recalculates AV proportionally).
     * Use this when player's agility changes during battle.
     * 
     * @param player The player
     */
    public void updatePlayerSpeed(Player player) {
        if (playerAV != null && player != null) {
            int newSpeed = entitySystem.getSpeed(player);
            
            // Store progress ratio
            double maxAV = (double) BASE_AV / playerAV.getSpeed();
            double progressRatio = playerAV.getActionValue() / maxAV;
            
            // Update speed
            playerAV.updateSpeed(newSpeed);
            
            // Recalculate AV maintaining progress
            double newMaxAV = (double) BASE_AV / newSpeed;
            playerAV.actionValue = newMaxAV * progressRatio;
        }
    }

    /**
     * Update enemy speed.
     * 
     * @param enemy The enemy
     */
    public void updateEnemySpeed(Enemy enemy) {
        if (enemyAV != null && enemy != null) {
            int newSpeed = entitySystem.getSpeed(enemy);
            
            double maxAV = (double) BASE_AV / enemyAV.getSpeed();
            double progressRatio = enemyAV.getActionValue() / maxAV;
            
            enemyAV.updateSpeed(newSpeed);
            
            double newMaxAV = (double) BASE_AV / newSpeed;
            enemyAV.actionValue = newMaxAV * progressRatio;
        }
    }

    // ===== STATISTICS =====

    /**
     * Calculate how many turns each combatant gets in N player turns.
     * Useful for balance analysis.
     * 
     * @param playerTurns Number of player turns to simulate
     * @return Array [playerTurns, enemyTurns]
     */
    public int[] calculateTurnDistribution(int playerTurns) {
        if (!battleActive) return new int[]{0, 0};
        
        List<TurnOrderEntry> order = calculateTurnOrder(playerTurns * 3); // Overshoot to ensure we get enough
        
        int playerCount = 0;
        int enemyCount = 0;
        
        for (TurnOrderEntry entry : order) {
            if (entry.isPlayer()) {
                playerCount++;
                if (playerCount >= playerTurns) {
                    break; // Stop when we reach desired player turns
                }
            } else {
                if (playerCount < playerTurns) {
                    enemyCount++;
                }
            }
        }
        
        return new int[]{playerCount, enemyCount};
    }

    /**
     * Calculate speed advantage percentage.
     * Positive = player is faster, negative = enemy is faster.
     * 
     * @return Speed advantage as percentage (-100 to +100)
     */
    public double calculateSpeedAdvantage() {
        if (!battleActive) return 0.0;
        
        int playerSpeed = playerAV.getSpeed();
        int enemySpeed = enemyAV.getSpeed();
        
        return ((double)(playerSpeed - enemySpeed) / enemySpeed) * 100.0;
    }

    /**
     * Estimate turns until player acts.
     * 
     * @return Estimated turns (0 = player acts next)
     */
    public int getTurnsUntilPlayerActs() {
        if (!battleActive) return 0;
        
        List<TurnOrderEntry> order = calculateTurnOrder(10);
        
        for (int i = 0; i < order.size(); i++) {
            if (order.get(i).isPlayer()) {
                return i;
            }
        }
        
        return 0;
    }

    /**
     * Estimate turns until enemy acts.
     */
    public int getTurnsUntilEnemyActs() {
        if (!battleActive) return 0;
        
        List<TurnOrderEntry> order = calculateTurnOrder(10);
        
        for (int i = 0; i < order.size(); i++) {
            if (!order.get(i).isPlayer()) {
                return i;
            }
        }
        
        return 0;
    }

    // ===== GUI HELPER METHODS =====

    /**
     * Get formatted turn order display.
     * Shows next N turns with visual indicators.
     * 
     * @param turnsAhead Number of turns to display
     * @return Formatted string for display
     */
    public String getFormattedTurnOrder(int turnsAhead) {
        if (!battleActive) return "No battle active";
        
        List<TurnOrderEntry> order = calculateTurnOrder(turnsAhead);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Turn Order ===\n");
        
        for (TurnOrderEntry entry : order) {
            String icon = entry.isPlayer() ? "►" : "▼";
            sb.append(icon).append(" ")
              .append(entry.getTurnNumber()).append(". ")
              .append(entry.getCombatantName()).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Get battle status summary.
     * Shows current AV state for both combatants.
     */
    public String getBattleStatus() {
        if (!battleActive) return "No battle active";
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== Battle Status ===\n");
        sb.append(playerAV.toString()).append("\n");
        sb.append(enemyAV.toString()).append("\n");
        sb.append("Current Turn: ").append(getCurrentTurnName()).append("\n");
        
        return sb.toString();
    }

    // ===== ACCESSORS =====

    public EntitySystem getEntitySystem() { return entitySystem; }
    public int getBaseAV() { return BASE_AV; }
}
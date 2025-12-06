package game.system;

import game.core.Enemy;
import java.util.ArrayList;
import java.util.List;

/**
 * EnemyTrainingSystem manages enemy stat progression with specialized training patterns.
 * Handles group training where all enemies train together with weighted probabilities.
 * 
 * Key Features:
 * - Specialized training: Each enemy has a preferred stat (60% chance) vs others (20% each)
 * - Group training: All enemies train together in a coordinated manner
 * - Deterministic specializations: Each enemy type has a fixed specialization
 * 
 * Design: Manages enemy training coordination and specialization logic.
 * GUI-Friendly: Returns detailed results for each enemy's training.
 */
public class EnemyTrainingSystem {

    private final EntitySystem entitySystem;

    // Training constants
    private static final int MIN_TRAINING_AMOUNT = 1;
    private static final int MAX_TRAINING_AMOUNT = 100;

    // Specialization weights
    private static final double SPECIALIZED_WEIGHT = 0.6;  // 60% for specialized stat
    private static final double SECONDARY_WEIGHT = 0.2;    // 20% for each other stat

    public EnemyTrainingSystem(EntitySystem entitySystem) {
        if (entitySystem == null) {
            throw new IllegalArgumentException("EntitySystem cannot be null");
        }
        this.entitySystem = entitySystem;
    }

    // ===== TRAINING RESULT CLASSES =====

    /**
     * Single enemy training result.
     */
    public static class EnemyTrainingResult {
        private final boolean success;
        private final String enemyName;
        private final String statTrained;
        private final int amountTrained;
        private final int oldValue;
        private final int newValue;
        private final String specialization;
        private final String message;

        public EnemyTrainingResult(boolean success, String enemyName, String statTrained,
                                  int amountTrained, int oldValue, int newValue,
                                  String specialization, String message) {
            this.success = success;
            this.enemyName = enemyName;
            this.statTrained = statTrained;
            this.amountTrained = amountTrained;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.specialization = specialization;
            this.message = message;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getEnemyName() { return enemyName; }
        public String getStatTrained() { return statTrained; }
        public int getAmountTrained() { return amountTrained; }
        public int getOldValue() { return oldValue; }
        public int getNewValue() { return newValue; }
        public String getSpecialization() { return specialization; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return message;
        }
    }

    /**
     * Group training result for all enemies.
     */
    public static class GroupTrainingResult {
        private final boolean success;
        private final List<EnemyTrainingResult> individualResults;
        private final int enemiesCount;
        private final String message;

        public GroupTrainingResult(boolean success, List<EnemyTrainingResult> individualResults,
                                  String message) {
            this.success = success;
            this.individualResults = individualResults;
            this.enemiesCount = individualResults.size();
            this.message = message;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public List<EnemyTrainingResult> getIndividualResults() { return individualResults; }
        public int getEnemiesCount() { return enemiesCount; }
        public String getMessage() { return message; }

        /**
         * Get result for a specific enemy by index.
         */
        public EnemyTrainingResult getResult(int index) {
            if (index >= 0 && index < individualResults.size()) {
                return individualResults.get(index);
            }
            return null;
        }

        /**
         * Get result for a specific enemy by name.
         */
        public EnemyTrainingResult getResultByName(String enemyName) {
            for (EnemyTrainingResult result : individualResults) {
                if (result.getEnemyName().equals(enemyName)) {
                    return result;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    // ===== SPECIALIZATION ENUM =====

    /**
     * Enemy specialization types.
     * Determines which stat an enemy prefers to train.
     */
    public enum Specialization {
        STRENGTH,
        AGILITY,
        INTELLIGENCE
    }

    // ===== ENEMY SPECIALIZATION DETECTION =====

    /**
     * Detect enemy's specialization based on their name.
     * This maps enemy names to their specialized stat.
     * 
     * Default specializations:
     * - "Killer Bunny" → AGILITY
     * - "Minotaur" → STRENGTH
     * - "Mindflayer" → INTELLIGENCE
     * 
     * @param enemy The enemy
     * @return Detected specialization
     */
    public Specialization detectSpecialization(Enemy enemy) {
        if (enemy == null) return Specialization.STRENGTH;

        String name = enemy.getName();

        // Map enemy names to specializations
        if (name.equalsIgnoreCase("Killer Bunny")) {
            return Specialization.AGILITY;
        } else if (name.equalsIgnoreCase("Minotaur")) {
            return Specialization.STRENGTH;
        } else if (name.equalsIgnoreCase("Mindflayer")) {
            return Specialization.INTELLIGENCE;
        }

        // Default: check highest stat
        return detectSpecializationByStat(enemy);
    }

    /**
     * Detect specialization by checking which stat is highest.
     * Fallback method when name matching fails.
     * 
     * @param enemy The enemy
     * @return Specialization based on highest stat
     */
    public Specialization detectSpecializationByStat(Enemy enemy) {
        if (enemy == null) return Specialization.STRENGTH;

        int[] stats = entitySystem.getPrimaryStats(enemy);
        int str = stats[0];
        int agi = stats[1];
        int intel = stats[2];

        if (str >= agi && str >= intel) {
            return Specialization.STRENGTH;
        } else if (agi >= str && agi >= intel) {
            return Specialization.AGILITY;
        } else {
            return Specialization.INTELLIGENCE;
        }
    }

    // ===== SPECIALIZED TRAINING =====

    /**
     * Train a single enemy with specialized weighted random stat selection.
     * 60% chance to train specialized stat, 20% each for others.
     * 
     * @param enemy The enemy to train
     * @param amount Amount to train
     * @return EnemyTrainingResult
     */
    public EnemyTrainingResult trainSpecialized(Enemy enemy, int amount) {
        if (enemy == null) {
            return new EnemyTrainingResult(false, "Unknown", "", 0, 0, 0, "NONE",
                "Invalid enemy");
        }

        if (amount < MIN_TRAINING_AMOUNT || amount > MAX_TRAINING_AMOUNT) {
            return new EnemyTrainingResult(false, enemy.getName(), "", 0, 0, 0, "NONE",
                "Training amount must be between " + MIN_TRAINING_AMOUNT + " and " + MAX_TRAINING_AMOUNT);
        }

        // Detect specialization
        Specialization spec = detectSpecialization(enemy);

        // Roll for which stat to train
        double roll = Math.random();
        String statToTrain;

        switch (spec) {
            case STRENGTH:
                // 60% STR, 20% AGI, 20% INT
                if (roll < SPECIALIZED_WEIGHT) {
                    statToTrain = "STRENGTH";
                } else if (roll < SPECIALIZED_WEIGHT + SECONDARY_WEIGHT) {
                    statToTrain = "AGILITY";
                } else {
                    statToTrain = "INTELLIGENCE";
                }
                break;

            case AGILITY:
                // 20% STR, 60% AGI, 20% INT
                if (roll < SECONDARY_WEIGHT) {
                    statToTrain = "STRENGTH";
                } else if (roll < SECONDARY_WEIGHT + SPECIALIZED_WEIGHT) {
                    statToTrain = "AGILITY";
                } else {
                    statToTrain = "INTELLIGENCE";
                }
                break;

            case INTELLIGENCE:
                // 20% STR, 20% AGI, 60% INT
                if (roll < SECONDARY_WEIGHT) {
                    statToTrain = "STRENGTH";
                } else if (roll < SECONDARY_WEIGHT * 2) {
                    statToTrain = "AGILITY";
                } else {
                    statToTrain = "INTELLIGENCE";
                }
                break;

            default:
                statToTrain = "STRENGTH";
        }

        // Get old value
        int oldValue = getStat(enemy, statToTrain);

        // Apply training
        switch (statToTrain) {
            case "STRENGTH":
                entitySystem.modifyStrength(enemy, amount);
                break;
            case "AGILITY":
                entitySystem.modifyAgility(enemy, amount);
                break;
            case "INTELLIGENCE":
                entitySystem.modifyIntelligence(enemy, amount);
                break;
        }

        // Get new value
        int newValue = getStat(enemy, statToTrain);

        String message = enemy.getName() + " trained " + statToTrain + " +" + amount +
                        " (" + oldValue + " → " + newValue + ") [" + spec + " specialist]";

        return new EnemyTrainingResult(true, enemy.getName(), statToTrain, amount,
                                      oldValue, newValue, spec.toString(), message);
    }

    /**
     * Train a specific stat directly (no weighted random).
     * 
     * @param enemy The enemy
     * @param stat Stat to train
     * @param amount Amount to train
     * @return EnemyTrainingResult
     */
    public EnemyTrainingResult trainStat(Enemy enemy, String stat, int amount) {
        if (enemy == null) {
            return new EnemyTrainingResult(false, "Unknown", "", 0, 0, 0, "NONE",
                "Invalid enemy");
        }

        String normalizedStat = normalizeStat(stat);
        if (normalizedStat == null) {
            return new EnemyTrainingResult(false, enemy.getName(), stat, 0, 0, 0, "NONE",
                "Invalid stat: " + stat);
        }

        if (amount < MIN_TRAINING_AMOUNT || amount > MAX_TRAINING_AMOUNT) {
            return new EnemyTrainingResult(false, enemy.getName(), stat, 0, 0, 0, "NONE",
                "Invalid training amount");
        }

        Specialization spec = detectSpecialization(enemy);
        int oldValue = getStat(enemy, normalizedStat);

        switch (normalizedStat) {
            case "STRENGTH":
                entitySystem.modifyStrength(enemy, amount);
                break;
            case "AGILITY":
                entitySystem.modifyAgility(enemy, amount);
                break;
            case "INTELLIGENCE":
                entitySystem.modifyIntelligence(enemy, amount);
                break;
        }

        int newValue = getStat(enemy, normalizedStat);

        String message = enemy.getName() + " trained " + normalizedStat + " +" + amount +
                        " (" + oldValue + " → " + newValue + ")";

        return new EnemyTrainingResult(true, enemy.getName(), normalizedStat, amount,
                                      oldValue, newValue, spec.toString(), message);
    }

    // ===== GROUP TRAINING =====

    /**
     * Train all enemies as a group with specialized weighted training.
     * Each enemy trains based on their specialization (60/20/20 weights).
     * 
     * @param enemies Array of enemies to train
     * @param amount Amount each enemy trains
     * @return GroupTrainingResult with all individual results
     */
    public GroupTrainingResult trainGroup(Enemy[] enemies, int amount) {
        if (enemies == null || enemies.length == 0) {
            return new GroupTrainingResult(false, new ArrayList<>(),
                "No enemies to train");
        }

        List<EnemyTrainingResult> results = new ArrayList<>();

        for (Enemy enemy : enemies) {
            if (enemy != null) {
                EnemyTrainingResult result = trainSpecialized(enemy, amount);
                results.add(result);
            }
        }

        String message = "Group training complete: " + results.size() + " enemies trained";

        return new GroupTrainingResult(true, results, message);
    }

    /**
     * Train all enemies in a group to a specific stat (synchronized training).
     * All enemies train the same stat regardless of specialization.
     * 
     * @param enemies Array of enemies
     * @param stat Stat to train
     * @param amount Amount to train
     * @return GroupTrainingResult
     */
    public GroupTrainingResult trainGroupStat(Enemy[] enemies, String stat, int amount) {
        if (enemies == null || enemies.length == 0) {
            return new GroupTrainingResult(false, new ArrayList<>(),
                "No enemies to train");
        }

        String normalizedStat = normalizeStat(stat);
        if (normalizedStat == null) {
            return new GroupTrainingResult(false, new ArrayList<>(),
                "Invalid stat: " + stat);
        }

        List<EnemyTrainingResult> results = new ArrayList<>();

        for (Enemy enemy : enemies) {
            if (enemy != null) {
                EnemyTrainingResult result = trainStat(enemy, normalizedStat, amount);
                results.add(result);
            }
        }

        String message = "Group trained " + normalizedStat + " +" + amount +
                        " (" + results.size() + " enemies)";

        return new GroupTrainingResult(true, results, message);
    }

    // ===== STAT QUERIES =====

    /**
     * Get enemy's current stat value.
     * 
     * @param enemy The enemy
     * @param stat Stat name
     * @return Current stat value, or 0 if invalid
     */
    public int getStat(Enemy enemy, String stat) {
        if (enemy == null) return 0;

        String normalizedStat = normalizeStat(stat);
        if (normalizedStat == null) return 0;

        int[] stats = entitySystem.getPrimaryStats(enemy);
        switch (normalizedStat) {
            case "STRENGTH": return stats[0];
            case "AGILITY": return stats[1];
            case "INTELLIGENCE": return stats[2];
            default: return 0;
        }
    }

    /**
     * Get all primary stats for enemy as array [STR, AGI, INT].
     * 
     * @param enemy The enemy
     * @return Stats array
     */
    public int[] getAllStats(Enemy enemy) {
        return entitySystem.getPrimaryStats(enemy);
    }

    /**
     * Calculate total stats for enemy.
     * 
     * @param enemy The enemy
     * @return Total of all primary stats
     */
    public int getTotalStats(Enemy enemy) {
        if (enemy == null) return 0;
        int[] stats = getAllStats(enemy);
        return stats[0] + stats[1] + stats[2];
    }

    /**
     * Get specialization as string.
     * 
     * @param enemy The enemy
     * @return Specialization name ("STRENGTH", "AGILITY", "INTELLIGENCE")
     */
    public String getSpecializationName(Enemy enemy) {
        return detectSpecialization(enemy).toString();
    }

    // ===== HELPER METHODS =====

    /**
     * Normalize stat name to standard form.
     */
    private String normalizeStat(String stat) {
        if (stat == null) return null;

        String upper = stat.trim().toUpperCase();

        switch (upper) {
            case "STR":
            case "STRENGTH":
                return "STRENGTH";

            case "AGI":
            case "AGILITY":
                return "AGILITY";

            case "INT":
            case "INTELLIGENCE":
                return "INTELLIGENCE";

            default:
                return null;
        }
    }

    /**
     * Get stat abbreviation.
     */
    public String getStatAbbreviation(String fullStatName) {
        if (fullStatName == null) return "";

        switch (fullStatName.toUpperCase()) {
            case "STRENGTH": return "STR";
            case "AGILITY": return "AGI";
            case "INTELLIGENCE": return "INT";
            default: return fullStatName;
        }
    }

    // ===== VALIDATION =====

    /**
     * Validate training amount.
     */
    public boolean isValidTrainingAmount(int amount) {
        return amount >= MIN_TRAINING_AMOUNT && amount <= MAX_TRAINING_AMOUNT;
    }

    /**
     * Validate stat name.
     */
    public boolean isValidStat(String stat) {
        return normalizeStat(stat) != null;
    }

    /**
     * Check if enemy can train.
     */
    public boolean canTrain(Enemy enemy) {
        return enemy != null && entitySystem.isAlive(enemy);
    }

    // ===== GUI HELPER METHODS =====

    /**
     * Get formatted group training summary.
     * Shows training results for all enemies in group.
     * 
     * @param result GroupTrainingResult
     * @return Formatted summary string
     */
    public String getGroupTrainingSummary(GroupTrainingResult result) {
        if (result == null || !result.isSuccess()) {
            return "Training failed";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Group Training Results ===\n");

        for (EnemyTrainingResult enemyResult : result.getIndividualResults()) {
            sb.append(enemyResult.getEnemyName()).append(": ")
              .append(getStatAbbreviation(enemyResult.getStatTrained()))
              .append(" +").append(enemyResult.getAmountTrained())
              .append(" (").append(enemyResult.getOldValue())
              .append(" → ").append(enemyResult.getNewValue()).append(")\n");
        }

        return sb.toString();
    }

    // ===== ACCESSORS =====

    public EntitySystem getEntitySystem() { return entitySystem; }
    public int getMinTrainingAmount() { return MIN_TRAINING_AMOUNT; }
    public int getMaxTrainingAmount() { return MAX_TRAINING_AMOUNT; }
    public double getSpecializedWeight() { return SPECIALIZED_WEIGHT; }
    public double getSecondaryWeight() { return SECONDARY_WEIGHT; }
}
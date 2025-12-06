package game.system;

import game.core.Player;

/**
 * PlayerTrainingSystem manages player stat progression.
 * Handles player-specific training validation, stat increases, and queries.
 * 
 * Responsibilities:
 * - Train player stats (STR, AGI, INT)
 * - Validate training inputs
 * - Calculate training effects
 * - Query player training information
 * 
 * Design: Stateless utility system - operates on passed player.
 * GUI-Friendly: All methods return simple types for easy UI binding.
 */
public class PlayerTrainingSystem {

    private final EntitySystem entitySystem;

    // Training constants
    private static final int MIN_TRAINING_AMOUNT = 1;
    private static final int MAX_TRAINING_AMOUNT = 100;

    public PlayerTrainingSystem(EntitySystem entitySystem) {
        if (entitySystem == null) {
            throw new IllegalArgumentException("EntitySystem cannot be null");
        }
        this.entitySystem = entitySystem;
    }

    // ===== TRAINING RESULT CLASS =====

    /**
     * TrainingResult holds the outcome of a training action.
     * Perfect for GUI display - contains all relevant information.
     */
    public static class TrainingResult {
        private final boolean success;
        private final String playerName;
        private final String statTrained;
        private final int amountTrained;
        private final int oldValue;
        private final int newValue;
        private final String message;

        public TrainingResult(boolean success, String playerName, String statTrained,
                            int amountTrained, int oldValue, int newValue, String message) {
            this.success = success;
            this.playerName = playerName;
            this.statTrained = statTrained;
            this.amountTrained = amountTrained;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.message = message;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getPlayerName() { return playerName; }
        public String getStatTrained() { return statTrained; }
        public int getAmountTrained() { return amountTrained; }
        public int getOldValue() { return oldValue; }
        public int getNewValue() { return newValue; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return message;
        }
    }

    // ===== PLAYER TRAINING =====

    /**
     * Train a specific player stat.
     * 
     * @param player The player to train
     * @param stat The stat to train ("STR", "STRENGTH", "AGI", "AGILITY", "INT", "INTELLIGENCE")
     * @param amount Amount to increase (must be positive)
     * @return TrainingResult with outcome
     */
    public TrainingResult trainStat(Player player, String stat, int amount) {
        // Validation
        if (player == null) {
            return new TrainingResult(false, "Unknown", "", 0, 0, 0, "Invalid player");
        }

        if (stat == null || stat.trim().isEmpty()) {
            return new TrainingResult(false, player.getName(), "", 0, 0, 0,
                "Invalid stat specified");
        }

        if (amount < MIN_TRAINING_AMOUNT || amount > MAX_TRAINING_AMOUNT) {
            return new TrainingResult(false, player.getName(), stat, 0, 0, 0,
                "Training amount must be between " + MIN_TRAINING_AMOUNT + " and " + MAX_TRAINING_AMOUNT);
        }

        // Normalize stat name
        String normalizedStat = normalizeStat(stat);
        if (normalizedStat == null) {
            return new TrainingResult(false, player.getName(), stat, 0, 0, 0,
                "Unknown stat: " + stat + ". Use STR, AGI, or INT");
        }

        // Get old value
        int oldValue = getStat(player, normalizedStat);

        // Apply training
        switch (normalizedStat) {
            case "STRENGTH":
                entitySystem.modifyStrength(player, amount);
                break;
            case "AGILITY":
                entitySystem.modifyAgility(player, amount);
                break;
            case "INTELLIGENCE":
                entitySystem.modifyIntelligence(player, amount);
                break;
        }

        // Get new value
        int newValue = getStat(player, normalizedStat);

        String message = player.getName() + " trained " + normalizedStat + " +" + amount +
                        " (" + oldValue + " → " + newValue + ")";

        return new TrainingResult(true, player.getName(), normalizedStat, amount, oldValue, newValue, message);
    }

    /**
     * Train player strength.
     * 
     * @param player The player
     * @param amount Amount to increase
     * @return TrainingResult
     */
    public TrainingResult trainStrength(Player player, int amount) {
        return trainStat(player, "STRENGTH", amount);
    }

    /**
     * Train player agility.
     * 
     * @param player The player
     * @param amount Amount to increase
     * @return TrainingResult
     */
    public TrainingResult trainAgility(Player player, int amount) {
        return trainStat(player, "AGILITY", amount);
    }

    /**
     * Train player intelligence.
     * 
     * @param player The player
     * @param amount Amount to increase
     * @return TrainingResult
     */
    public TrainingResult trainIntelligence(Player player, int amount) {
        return trainStat(player, "INTELLIGENCE", amount);
    }

    // ===== STAT QUERIES =====

    /**
     * Get player's current stat value.
     * 
     * @param player The player
     * @param stat Stat name ("STRENGTH", "AGILITY", "INTELLIGENCE")
     * @return Current stat value, or 0 if invalid
     */
    public int getStat(Player player, String stat) {
        if (player == null) return 0;

        String normalizedStat = normalizeStat(stat);
        if (normalizedStat == null) return 0;

        int[] stats = entitySystem.getPrimaryStats(player);
        switch (normalizedStat) {
            case "STRENGTH": return stats[0];
            case "AGILITY": return stats[1];
            case "INTELLIGENCE": return stats[2];
            default: return 0;
        }
    }

    /**
     * Get player's strength.
     * 
     * @param player The player
     * @return Current strength value
     */
    public int getStrength(Player player) {
        return getStat(player, "STRENGTH");
    }

    /**
     * Get player's agility.
     * 
     * @param player The player
     * @return Current agility value
     */
    public int getAgility(Player player) {
        return getStat(player, "AGILITY");
    }

    /**
     * Get player's intelligence.
     * 
     * @param player The player
     * @return Current intelligence value
     */
    public int getIntelligence(Player player) {
        return getStat(player, "INTELLIGENCE");
    }

    /**
     * Get all primary stats for player as array [STR, AGI, INT].
     * 
     * @param player The player
     * @return Stats array
     */
    public int[] getAllStats(Player player) {
        return entitySystem.getPrimaryStats(player);
    }

    /**
     * Calculate total stats for player (STR + AGI + INT).
     * Useful for comparing overall power.
     * 
     * @param player The player
     * @return Total of all primary stats
     */
    public int getTotalStats(Player player) {
        if (player == null) return 0;
        int[] stats = getAllStats(player);
        return stats[0] + stats[1] + stats[2];
    }

    /**
     * Get player's highest stat value.
     * 
     * @param player The player
     * @return Highest stat value
     */
    public int getHighestStat(Player player) {
        if (player == null) return 0;
        int[] stats = getAllStats(player);
        return Math.max(stats[0], Math.max(stats[1], stats[2]));
    }

    /**
     * Get player's lowest stat value.
     * 
     * @param player The player
     * @return Lowest stat value
     */
    public int getLowestStat(Player player) {
        if (player == null) return 0;
        int[] stats = getAllStats(player);
        return Math.min(stats[0], Math.min(stats[1], stats[2]));
    }

    /**
     * Get player's dominant stat name.
     * Returns which stat is highest (for specialization display).
     * 
     * @param player The player
     * @return "STRENGTH", "AGILITY", or "INTELLIGENCE"
     */
    public String getDominantStat(Player player) {
        if (player == null) return "NONE";
        
        int[] stats = getAllStats(player);
        int str = stats[0];
        int agi = stats[1];
        int intel = stats[2];
        
        if (str >= agi && str >= intel) {
            return "STRENGTH";
        } else if (agi >= str && agi >= intel) {
            return "AGILITY";
        } else {
            return "INTELLIGENCE";
        }
    }

    // ===== TRAINING VALIDATION =====

    /**
     * Validate training amount.
     * 
     * @param amount Amount to validate
     * @return true if amount is valid
     */
    public boolean isValidTrainingAmount(int amount) {
        return amount >= MIN_TRAINING_AMOUNT && amount <= MAX_TRAINING_AMOUNT;
    }

    /**
     * Validate stat name.
     * 
     * @param stat Stat name to validate
     * @return true if stat is valid (STR, AGI, INT or variants)
     */
    public boolean isValidStat(String stat) {
        return normalizeStat(stat) != null;
    }

    /**
     * Check if player can train (basic check).
     * Can be extended with additional conditions.
     * 
     * @param player The player
     * @return true if player can train
     */
    public boolean canTrain(Player player) {
        return player != null && entitySystem.isAlive(player);
    }

    /**
     * Get array of valid stat names for display.
     * 
     * @return Array of valid stat names
     */
    public String[] getValidStatNames() {
        return new String[]{"STRENGTH", "AGILITY", "INTELLIGENCE", "STR", "AGI", "INT"};
    }

    // ===== HELPER METHODS =====

    /**
     * Normalize stat name to standard form.
     * Converts various inputs (STR, str, STRENGTH) to consistent format.
     * 
     * @param stat Input stat name
     * @return Normalized stat name ("STRENGTH", "AGILITY", "INTELLIGENCE") or null if invalid
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
     * 
     * @param fullStatName Full stat name ("STRENGTH", "AGILITY", "INTELLIGENCE")
     * @return Abbreviation ("STR", "AGI", "INT")
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

    /**
     * Get full stat name from abbreviation.
     * 
     * @param abbreviation Stat abbreviation ("STR", "AGI", "INT")
     * @return Full name ("STRENGTH", "AGILITY", "INTELLIGENCE")
     */
    public String getFullStatName(String abbreviation) {
        return normalizeStat(abbreviation);
    }

    // ===== GUI HELPER METHODS =====

    /**
     * Get training preview text.
     * Shows what will happen if training is applied.
     * 
     * @param player The player
     * @param stat Stat to train
     * @param amount Amount to train
     * @return Preview text (e.g., "STR: 20 → 25 (+5)")
     */
    public String getTrainingPreview(Player player, String stat, int amount) {
        if (player == null || !isValidStat(stat) || !isValidTrainingAmount(amount)) {
            return "Invalid training";
        }

        int currentValue = getStat(player, stat);
        int newValue = currentValue + amount;
        String abbrev = getStatAbbreviation(normalizeStat(stat));

        return abbrev + ": " + currentValue + " → " + newValue + " (+" + amount + ")";
    }

    /**
     * Get formatted stat display.
     * Returns formatted string for UI display.
     * 
     * @param player The player
     * @return Formatted stat string (e.g., "STR: 25 | AGI: 20 | INT: 15")
     */
    public String getFormattedStats(Player player) {
        if (player == null) return "Invalid player";
        
        int[] stats = getAllStats(player);
        return String.format("STR: %d | AGI: %d | INT: %d", stats[0], stats[1], stats[2]);
    }

    /**
     * Compare player stat to a threshold.
     * Useful for UI indicators (low/medium/high).
     * 
     * @param player The player
     * @param stat Stat to check
     * @param threshold Threshold value
     * @return -1 if below, 0 if equal, 1 if above
     */
    public int compareStatToThreshold(Player player, String stat, int threshold) {
        int value = getStat(player, stat);
        return Integer.compare(value, threshold);
    }

    // ===== ACCESSORS =====

    public EntitySystem getEntitySystem() { return entitySystem; }
    public int getMinTrainingAmount() { return MIN_TRAINING_AMOUNT; }
    public int getMaxTrainingAmount() { return MAX_TRAINING_AMOUNT; }
}
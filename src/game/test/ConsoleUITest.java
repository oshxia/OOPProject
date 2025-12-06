package game.test;

import game.core.*;
import game.data.*;
import game.system.*;
import java.util.*;

/**
 * ConsoleUITest provides a complete console-based UI for testing the game.
 * Demonstrates all systems working together with the new Action Value turn order.
 * 
 * Features:
 * - Full game loop with training and battle phases
 * - Randomized enemy order (no repeats)
 * - Randomized training cycles (3-7 per enemy)
 * - Action Value turn order system (HSR-style)
 * - Turn order preview display
 * - Enemy AI with different strategies
 * - Player stat training
 * - Combat with hit/miss mechanics
 * - Real-time AV readiness bars
 */
public class ConsoleUITest {

    private static final Scanner scanner = new Scanner(System.in);
    
    // Systems
    private static EntitySystem entitySystem;
    private static PlayerTrainingSystem playerTrainingSystem;
    private static EnemyTrainingSystem enemyTrainingSystem;
    private static SkillSystem skillSystem;
    private static CooldownSystem cooldownSystem;
    private static CombatSystem combatSystem;
    private static EnemyAISystem enemyAISystem;
    private static ActionValueSystem actionValueSystem;
    private static GameFlowSystem gameFlowSystem;
    
    // Game state
    private static Player player;
    private static Skill[] playerSkills;
    private static Enemy[] enemies;
    private static int currentEnemyIndex = 0;

    public static void main(String[] args) {
        initializeSystems();
        displayWelcome();
        
        if (setupGame()) {
            runGameLoop();
        }
        
        scanner.close();
    }

    // ===== SYSTEM INITIALIZATION =====

    private static void initializeSystems() {
        entitySystem = new EntitySystem();
        playerTrainingSystem = new PlayerTrainingSystem(entitySystem);
        enemyTrainingSystem = new EnemyTrainingSystem(entitySystem);
        skillSystem = new SkillSystem();
        cooldownSystem = new CooldownSystem();
        combatSystem = new CombatSystem(entitySystem, skillSystem, cooldownSystem);
        enemyAISystem = new EnemyAISystem(entitySystem, skillSystem, cooldownSystem);
        actionValueSystem = new ActionValueSystem(entitySystem);
        gameFlowSystem = new GameFlowSystem(
            entitySystem, playerTrainingSystem, enemyTrainingSystem,
            skillSystem, cooldownSystem, combatSystem, enemyAISystem
        );
    }

    // ===== WELCOME & SETUP =====

    private static void displayWelcome() {
        clearScreen();
        printSeparator("=");
        System.out.println("       TURN-BASED RPG - ACTION VALUE SYSTEM");
        printSeparator("=");
        System.out.println("Features HSR-style turn order based on Speed!");
        System.out.println("Faster units act more frequently!");
        printSeparator("=");
        System.out.println();
    }

    private static boolean setupGame() {
        // Choose profession
        Profession profession = chooseProfession();
        if (profession == null) return false;
        
        // Get player name
        System.out.print("\nEnter your hero's name: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) playerName = "Hero";
        
        // Create player
        player = entitySystem.createPlayer(playerName, profession);
        playerSkills = SkillsData.getSkillsForProfession(profession);
        
        // Load enemies and randomize order
        enemies = EnemiesData.getAllEnemyTypes();
        shuffleEnemies(enemies);
        
        System.out.println("\n✓ Game initialized successfully!");
        System.out.println("You will face " + enemies.length + " enemies in random order:");
        for (int i = 0; i < enemies.length; i++) {
            System.out.println("  " + (i + 1) + ". " + enemies[i].getName() + 
                             " (" + EnemiesData.getEnemySpecialization(enemies[i].getName()) + " specialist)");
        }
        
        pressEnterToContinue();
        return true;
    }
    
    /**
     * Shuffle enemy array using Fisher-Yates algorithm.
     */
    private static void shuffleEnemies(Enemy[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap
            Enemy temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private static Profession chooseProfession() {
        System.out.println("Choose your profession:");
        System.out.println("1. WARRIOR - Balanced fighter with consistent damage");
        System.out.println("2. MAGE - Powerful spellcaster with high burst damage");
        System.out.println("3. ROGUE - Swift assassin with fast attacks");
        System.out.print("\nEnter choice (1-3): ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": return Profession.WARRIOR;
            case "2": return Profession.MAGE;
            case "3": return Profession.ROGUE;
            default:
                System.out.println("Invalid choice. Defaulting to WARRIOR.");
                return Profession.WARRIOR;
        }
    }

    // ===== MAIN GAME LOOP =====

    private static void runGameLoop() {
        for (currentEnemyIndex = 0; currentEnemyIndex < enemies.length; currentEnemyIndex++) {
            Enemy currentEnemy = enemies[currentEnemyIndex];
            
            // Training Phase
            displayTrainingPhaseIntro(currentEnemy);
            runTrainingPhase(currentEnemy);
            
            // Battle Phase
            displayBattlePhaseIntro(currentEnemy);
            boolean playerWon = runBattlePhase(currentEnemy);
            
            if (!playerWon) {
                displayDefeat();
                return;
            }
            
            displayVictory(currentEnemy);
            
            if (currentEnemyIndex < enemies.length - 1) {
                System.out.println("\nPreparing for next enemy...");
                pressEnterToContinue();
            }
        }
        
        displayFinalVictory();
    }

    // ===== TRAINING PHASE =====

    private static void displayTrainingPhaseIntro(Enemy enemy) {
        clearScreen();
        printSeparator("=");
        System.out.println("       TRAINING PHASE - Enemy " + (currentEnemyIndex + 1) + 
                         "/" + enemies.length);
        printSeparator("=");
        System.out.println("Next opponent: " + enemy.getName());
        System.out.println("Specialization: " + EnemiesData.getEnemySpecialization(enemy.getName()));
        System.out.println("AI Strategy: " + EnemiesData.getEnemyAIDescription(enemy.getName()));
        printSeparator("-");
        displayStats(player, enemy);
        printSeparator("=");
        System.out.println();
    }

    private static void runTrainingPhase(Enemy enemy) {
        // Randomize training cycles from 3 to 7
        Random random = new Random();
        int trainingCycles = 3 + random.nextInt(5); // 3 to 7 (inclusive)
        
        System.out.println("\nThis training phase will have " + trainingCycles + " cycles.");
        pressEnterToContinue();
        
        for (int cycle = 1; cycle <= trainingCycles; cycle++) {
            System.out.println("\n--- Training Cycle " + cycle + "/" + trainingCycles + " ---");
            
            // Player training
            System.out.println("\nYour turn to train:");
            playerTrainingTurn();
            
            // Enemy training
            System.out.println("\nEnemies are training...");
            enemyTrainingTurn();
            
            // Show updated stats
            System.out.println("\n--- Updated Stats ---");
            displayStats(player, enemy);
            
            if (cycle < trainingCycles) {
                pressEnterToContinue();
            }
        }
        
        System.out.println("\n✓ Training phase complete!");
        pressEnterToContinue();
    }

    private static void playerTrainingTurn() {
        boolean validChoice = false;
        String stat = "";
        
        while (!validChoice) {
            System.out.println("Choose stat to train:");
            System.out.println("1. Strength (STR) - Increases HP and Damage");
            System.out.println("2. Agility (AGI) - Increases Speed and Evasion");
            System.out.println("3. Intelligence (INT) - Increases Accuracy and CDR");
            System.out.print("Enter choice (1-3): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1": 
                    stat = "STRENGTH";
                    validChoice = true;
                    break;
                case "2": 
                    stat = "AGILITY";
                    validChoice = true;
                    break;
                case "3": 
                    stat = "INTELLIGENCE";
                    validChoice = true;
                    break;
                default:
                    System.out.println("\n❌ Invalid choice! Please enter 1, 2, or 3.\n");
            }
        }
        
        int amount = 5; // Same as enemy training amount
        PlayerTrainingSystem.TrainingResult result = 
            playerTrainingSystem.trainStat(player, stat, amount);
        
        System.out.println("→ " + result.getMessage());
    }

    private static void enemyTrainingTurn() {
        int amount = 5; // Reduced to 5 since enemies train with 60/20/20 probability
        EnemyTrainingSystem.GroupTrainingResult result = 
            enemyTrainingSystem.trainGroup(enemies, amount);
        
        if (result.isSuccess()) {
            for (EnemyTrainingSystem.EnemyTrainingResult enemyResult : result.getIndividualResults()) {
                System.out.println("→ " + enemyResult.getMessage());
            }
        }
    }

    // ===== BATTLE PHASE =====

    private static void displayBattlePhaseIntro(Enemy enemy) {
        clearScreen();
        printSeparator("=");
        System.out.println("       BATTLE PHASE - Enemy " + (currentEnemyIndex + 1) + 
                         "/" + enemies.length);
        printSeparator("=");
        System.out.println("Opponent: " + enemy.getName());
        System.out.println("AI: " + EnemiesData.getEnemyAIDescription(enemy.getName()));
        printSeparator("=");
        
        // Prepare for battle
        combatSystem.prepareBattle(player, enemy);
        actionValueSystem.initializeBattle(player, enemy);
        
        // Show speed comparison
        int playerSpeed = entitySystem.getSpeed(player);
        int enemySpeed = entitySystem.getSpeed(enemy);
        System.out.println("\nSpeed Comparison:");
        System.out.println("  " + player.getName() + ": " + playerSpeed);
        System.out.println("  " + enemy.getName() + ": " + enemySpeed);
        
        double advantage = actionValueSystem.calculateSpeedAdvantage();
        if (advantage > 0) {
            System.out.println("  → You are " + String.format("%.1f", advantage) + "% faster!");
        } else if (advantage < 0) {
            System.out.println("  → Enemy is " + String.format("%.1f", Math.abs(advantage)) + "% faster!");
        } else {
            System.out.println("  → Equal speed!");
        }
        
        System.out.println("\nLet the battle begin!");
        pressEnterToContinue();
    }

    private static boolean runBattlePhase(Enemy enemy) {
        int turnNumber = 0;
        
        while (entitySystem.isAlive(player) && entitySystem.isAlive(enemy)) {
            turnNumber++;
            
            // Display battle status
            displayBattleStatus(enemy, turnNumber);
            
            // Check whose turn based on AV
            if (actionValueSystem.isPlayerTurn()) {
                // Tick player cooldowns at start of their turn
                cooldownSystem.tickPlayerCooldowns(player);
                
                // Player turn
                playerBattleTurn(enemy);
            } else {
                // Tick enemy cooldowns at start of their turn
                cooldownSystem.tickEnemyCooldowns(enemy);
                
                // Enemy turn
                enemyBattleTurn(enemy);
            }
            
            // Advance to next turn
            actionValueSystem.advanceToNextTurn();
            
            // Check if battle ended
            if (!entitySystem.isAlive(enemy)) {
                System.out.println("\n🎉 " + enemy.getName() + " has been defeated!");
                pressEnterToContinue();
                return true;
            }
            
            if (!entitySystem.isAlive(player)) {
                return false;
            }
        }
        
        return entitySystem.isAlive(player);
    }

    private static void displayBattleStatus(Enemy enemy, int turnNumber) {
        clearScreen();
        printSeparator("=");
        System.out.println("       BATTLE - Turn " + turnNumber);
        printSeparator("=");
        
        // HP bars
        displayHPBar(player.getName(), 
                    entitySystem.getCurrentHP(player), 
                    entitySystem.getMaxHP(player));
        displayHPBar(enemy.getName(), 
                    entitySystem.getCurrentHP(enemy), 
                    entitySystem.getMaxHP(enemy));
        
        System.out.println();
        
        // Action Value bars
        displayAVBar(player.getName(), actionValueSystem.getPlayerReadiness(), true);
        displayAVBar(enemy.getName(), actionValueSystem.getEnemyReadiness(), false);
        
        printSeparator("-");
        
        // Turn order preview
        System.out.println("Turn Order (Next 5 turns):");
        List<String> turnOrder = actionValueSystem.getTurnOrderList(5);
        for (int i = 0; i < Math.min(5, turnOrder.size()); i++) {
            String marker = i == 0 ? " ► " : "   ";
            System.out.println(marker + turnOrder.get(i));
        }
        
        printSeparator("=");
        System.out.println();
    }

    private static void playerBattleTurn(Enemy enemy) {
        boolean validActionTaken = false;
        
        while (!validActionTaken) {
            System.out.println("🗡️  YOUR TURN!");
            System.out.println();
            
            // Show player skills
            System.out.println("Your Skills:");
            for (int i = 0; i < playerSkills.length; i++) {
                Skill skill = playerSkills[i];
                int damage = skillSystem.calculateDamage(player, skill);
                int cooldown = cooldownSystem.getRemainingCooldown(player, skill);
                String status = cooldown > 0 ? " [CD: " + cooldown + "]" : " [READY]";
                
                System.out.println((i + 1) + ". " + skill.getName() + 
                                 " (DMG: " + damage + ", CD: " + skill.getBaseCooldown() + ")" + status);
            }
            
            // Enemy intent
            EnemyAISystem.AIDecision intent = enemyAISystem.chooseSkill(enemy, player);
            System.out.println("\n⚠️  Enemy Intent: " + intent.getSkillName());
            System.out.println("   Reasoning: " + intent.getReasoning());
            
            // Get player choice
            System.out.print("\nChoose skill (1-" + playerSkills.length + "): ");
            String choice = scanner.nextLine().trim();
            
            int skillIndex;
            try {
                skillIndex = Integer.parseInt(choice) - 1;
            } catch (NumberFormatException e) {
                System.out.println("\n❌ Invalid input! Please enter a number.");
                pressEnterToContinue();
                clearScreen();
                continue;
            }
            
            if (skillIndex < 0 || skillIndex >= playerSkills.length) {
                System.out.println("\n❌ Invalid skill choice! Please choose between 1 and " + playerSkills.length);
                pressEnterToContinue();
                clearScreen();
                continue;
            }
            
            // Check if skill is ready
            if (cooldownSystem.getRemainingCooldown(player, playerSkills[skillIndex]) > 0) {
                System.out.println("\n❌ " + playerSkills[skillIndex].getName() + " is on cooldown!");
                System.out.println("   Please choose a skill that is READY.");
                pressEnterToContinue();
                clearScreen();
                continue; // Loop back to skill selection
            }
            
            // Execute attack
            CombatSystem.CombatResult result = 
                combatSystem.playerAttack(player, enemy, playerSkills, skillIndex);
            
            System.out.println("\n" + result.getMessage());
            
            if (result.isHit()) {
                System.out.println("💥 " + enemy.getName() + " HP: " + 
                                 entitySystem.getCurrentHP(enemy) + "/" + 
                                 entitySystem.getMaxHP(enemy));
            }
            
            validActionTaken = true; // Exit loop
        }
        
        pressEnterToContinue();
    }

    private static void enemyBattleTurn(Enemy enemy) {
        System.out.println("⚔️  ENEMY TURN: " + enemy.getName());
        System.out.println();
        
        // AI chooses skill
        EnemyAISystem.AIDecision decision = enemyAISystem.chooseSkill(enemy, player);
        
        System.out.println("AI Decision: " + decision.getSkillName());
        System.out.println("Reasoning: " + decision.getReasoning());
        System.out.println();
        
        // Execute attack
        CombatSystem.CombatResult result;
        if (decision.isBasicAttack()) {
            result = combatSystem.enemyBasicAttack(enemy, player);
        } else {
            Skill skill = enemy.getSkills()[decision.getSkillIndex()];
            result = combatSystem.enemyAttack(enemy, player, skill);
        }
        
        System.out.println(result.getMessage());
        
        if (result.isHit()) {
            System.out.println("💥 " + player.getName() + " HP: " + 
                             entitySystem.getCurrentHP(player) + "/" + 
                             entitySystem.getMaxHP(player));
        }
        
        pressEnterToContinue();
    }

    // ===== DISPLAY HELPERS =====

    private static void displayStats(Player player, Enemy enemy) {
        int[] playerStats = entitySystem.getPrimaryStats(player);
        int[] enemyStats = entitySystem.getPrimaryStats(enemy);
        
        System.out.println("YOUR STATS:");
        System.out.println("  STR: " + playerStats[0] + " | AGI: " + playerStats[1] + 
                         " | INT: " + playerStats[2]);
        System.out.println("  HP: " + entitySystem.getMaxHP(player) + 
                         " | Speed: " + entitySystem.getSpeed(player) +
                         " | Acc: " + entitySystem.getAccuracy(player) +
                         " | Eva: " + entitySystem.getEvasion(player));
        
        System.out.println("\nENEMY STATS (" + enemy.getName() + "):");
        System.out.println("  STR: " + enemyStats[0] + " | AGI: " + enemyStats[1] + 
                         " | INT: " + enemyStats[2]);
        System.out.println("  HP: " + entitySystem.getMaxHP(enemy) + 
                         " | Speed: " + entitySystem.getSpeed(enemy) +
                         " | Acc: " + entitySystem.getAccuracy(enemy) +
                         " | Eva: " + entitySystem.getEvasion(enemy));
    }

    private static void displayHPBar(String name, int currentHP, int maxHP) {
        double percentage = (double) currentHP / maxHP;
        int barLength = 30;
        int filled = (int) (barLength * percentage);
        
        StringBuilder bar = new StringBuilder();
        bar.append("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");
        
        System.out.printf("%s HP: %s %d/%d (%.0f%%)\n", 
                         name, bar.toString(), currentHP, maxHP, percentage * 100);
    }

    private static void displayAVBar(String name, double readiness, boolean isPlayer) {
        int barLength = 30;
        int filled = (int) (barLength * readiness);
        
        StringBuilder bar = new StringBuilder();
        bar.append("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("▓");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");
        
        String turnMarker = readiness >= 1.0 ? " ► READY!" : "";
        String icon = isPlayer ? "🗡️ " : "⚔️ ";
        
        System.out.printf("%s%s AV: %s %.0f%%%s\n", 
                         icon, name, bar.toString(), readiness * 100, turnMarker);
    }

    private static void displayVictory(Enemy enemy) {
        clearScreen();
        printSeparator("=");
        System.out.println("       VICTORY!");
        printSeparator("=");
        System.out.println("You have defeated " + enemy.getName() + "!");
        System.out.println("\nYour Stats:");
        int[] stats = entitySystem.getPrimaryStats(player);
        System.out.println("  STR: " + stats[0] + " | AGI: " + stats[1] + " | INT: " + stats[2]);
        System.out.println("  HP: " + entitySystem.getCurrentHP(player) + "/" + 
                         entitySystem.getMaxHP(player));
        printSeparator("=");
        pressEnterToContinue();
    }

    private static void displayDefeat() {
        clearScreen();
        printSeparator("=");
        System.out.println("       DEFEAT");
        printSeparator("=");
        System.out.println("You have been defeated...");
        System.out.println("Game Over");
        printSeparator("=");
    }

    private static void displayFinalVictory() {
        clearScreen();
        printSeparator("=");
        System.out.println("       🎉 FINAL VICTORY! 🎉");
        printSeparator("=");
        System.out.println("Congratulations! You have defeated all enemies!");
        System.out.println("\nFinal Stats:");
        int[] stats = entitySystem.getPrimaryStats(player);
        System.out.println("  STR: " + stats[0] + " | AGI: " + stats[1] + " | INT: " + stats[2]);
        System.out.println("  HP: " + entitySystem.getCurrentHP(player) + "/" + 
                         entitySystem.getMaxHP(player));
        System.out.println("  Speed: " + entitySystem.getSpeed(player));
        printSeparator("=");
        System.out.println("\nThank you for playing!");
    }

    // ===== UTILITY METHODS =====

    private static void printSeparator(String symbol) {
        for (int i = 0; i < 60; i++) {
            System.out.print(symbol);
        }
        System.out.println();
    }

    private static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void clearScreen() {
        // Simple clear - prints blank lines
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
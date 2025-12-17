package game.ui;

import game.core.*;
import game.data.*;
import game.system.*;

import java.util.*;

public class GameSession {

    // ================== SYSTEMS ==================
    private static EntitySystem entitySystem;
    private static SkillSystem skillSystem;
    private static CooldownSystem cooldownSystem;
    private static CombatSystem combatSystem;
    private static EnemyAISystem enemyAISystem;
    private static ActionValueSystem actionValueSystem;

    // ================== GAME STATE ==================
    private static Player player;
    private static Queue<Enemy> enemies;

    /**
     * Call ONCE when starting a new game
     * (after character creation)
     */
    public static void init(Player p, List<Enemy> enemyPool) {
        player = p;

        // Initialize systems (same as ConsoleUITest)
        entitySystem = new EntitySystem();
        skillSystem = new SkillSystem();
        cooldownSystem = new CooldownSystem();
        combatSystem = new CombatSystem(entitySystem, skillSystem, cooldownSystem);
        enemyAISystem = new EnemyAISystem(entitySystem, skillSystem, cooldownSystem);
        actionValueSystem = new ActionValueSystem(entitySystem);

        // Shuffle enemies
        List<Enemy> shuffled = new ArrayList<>(enemyPool);
        Collections.shuffle(shuffled);
        enemies = new ArrayDeque<>(shuffled);
    }

    // ================== ENEMY FLOW ==================

    public static Enemy nextEnemy() {
        if (enemies == null || enemies.isEmpty()) return null;
        return enemies.poll();
    }

    public static boolean hasMoreEnemies() {
        return enemies != null && !enemies.isEmpty();
    }

    public static int battlesLeft() {
        return enemies == null ? 0 : enemies.size();
    }

    public static List<Enemy> getEnemies() {
        return enemies == null ? Collections.emptyList() : new ArrayList<>(enemies);
    }
    
    public static SkillSystem getSkillSystem() {
        return skillSystem;
    }

    // ================== GETTERS ==================

    public static Player getPlayer() {
        return player;
    }

    public static CombatSystem getCombatSystem() {
        return combatSystem;
    }

    public static EnemyAISystem getEnemyAISystem() {
        return enemyAISystem;
    }

    public static ActionValueSystem getActionValueSystem() {
        return actionValueSystem;
    }

    public static CooldownSystem getCooldownSystem() {
        return cooldownSystem;
    }

    public static EntitySystem getEntitySystem() {
        return entitySystem;
    }
}

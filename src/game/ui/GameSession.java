package game.ui;

import game.core.Enemy;
import java.util.*;

public class GameSession {

    private static Queue<Enemy> enemies;

    /**
     * Call this ONCE when starting a new game
     */
    public static void init(List<Enemy> enemyPool) {
        List<Enemy> shuffled = new ArrayList<>(enemyPool);
        Collections.shuffle(shuffled);
        enemies = new ArrayDeque<>(shuffled);
    }

    /**
     * Returns the next enemy.
     * Returns null AFTER all enemies are fought
     */
    public static Enemy nextEnemy() {
        if (enemies == null || enemies.isEmpty()) {
            return null;
        }
        return enemies.poll();
    }

    /**
     * True while battles remain
     */
    public static boolean hasMoreEnemies() {
        return enemies != null && !enemies.isEmpty();
    }

    /**
     * Optional helper for UI/debug
     */
    public static int battlesLeft() {
        return enemies == null ? 0 : enemies.size();
    }

    /**
     * Returns a shuffled copy of remaining enemies
     * Used by TrainingScreen to display and update stats
     */
    public static List<Enemy> getEnemies() {
        if (enemies == null) return Collections.emptyList();
        return new ArrayList<>(enemies);
    }
}

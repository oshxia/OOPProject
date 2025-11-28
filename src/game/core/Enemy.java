package game.core;

import java.io.Serializable;

public class Enemy implements Serializable {

    private String name;
    private Stat stats;

    public Enemy(String name, Stat stats) {
        this.name = name;
        this.stats = stats;
    }

    public void takeDamage(int damage) {
        stats.takeDamage(damage);
    }

    public boolean isDead() {
        return stats.isDead(0);
    }

    public String getName() {
        return name;
    }

    public Stat getStats() {
        return stats;
    }
}

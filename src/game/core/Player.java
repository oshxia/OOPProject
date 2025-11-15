package game.core;

import java.io.Serializable;

public class Player implements Serializable {

    private String name;
    private Profession profession;
    private Stat stats;

    public Player(String name, Profession profession, Stat stats) {
        this.name = name;
        this.profession = profession;
        this.stats = stats;
    }

    public String getName() {
        return name;
    }

    public Profession getProfession() {
        return profession;
    }

    public Stat getStats() {
        return stats;
    }
}

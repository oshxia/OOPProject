package game.core;

import java.io.Serializable;

/**
 * Profession enum for player classes.
 * Each profession defines its available skills.
 */
public enum Profession implements Serializable {

    WARRIOR(new Skill[]{
            new Skill("Power Strike", null, 18, 2)
    }),

    MAGE(new Skill[]{
            new Skill("Fireball", null, 22, 3)
    }),

    ROGUE(new Skill[]{
            new Skill("Backstab", null, 16, 1)
    });

    private final Skill[] skills;

    Profession(Skill[] skills) {
        this.skills = skills;
    }

    /**
     * Returns the skills available to this profession.
     * Never returns null.
     */
    public Skill[] getSkills() {
        return skills;
    }
}

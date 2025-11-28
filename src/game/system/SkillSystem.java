package game.system;

import game.core.Player;
import game.core.Skill;
import game.core.Enemy;

public class SkillSystem {

    public void useSkill(Player player, Skill skill, Enemy enemy) {
        if (skill.canUse(player)) {
            skill.use(player, enemy);
            System.out.println(player.getName() + " used " + skill.getName() +
                               " on " + enemy.getName());
        } else {
            System.out.println(skill.getName() + " cannot be used right now!");
        }
    }

    public void tickSkill(Skill skill) {
        skill.tickCooldown();
    }

    public void tickAllSkills(Skill[] skills) {
        for (Skill skill : skills) {
            skill.tickCooldown();
        }
    }
}

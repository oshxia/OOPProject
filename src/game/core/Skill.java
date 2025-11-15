package game.core;

import java.io.Serializable;

public class Skill implements Serializable {

    private String name;
    private Profession allowedProfession;
    private int baseDamage;
    private int cooldown;
    private int currentCooldown;

    public Skill(String name, Profession allowedProfession, int baseDamage, int cooldown) {
        this.name = name;
        this.allowedProfession = allowedProfession;
        this.baseDamage = baseDamage;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
    }

    public boolean canUse(Player player) {
        return player.getProfession() == allowedProfession && currentCooldown == 0;
    }

    public void use(Player player, Enemy enemy) {
        if (canUse(player)) {
            int damage = baseDamage + player.getStats().getStrength();
            enemy.takeDamage(damage);
            currentCooldown = Math.max(0, cooldown - player.getStats().getCooldownReduction());
        }
    }

    public void tickCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    public String getName() {
        return name;
    }
}

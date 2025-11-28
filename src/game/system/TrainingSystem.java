package game.system;

import game.core.Player;
import game.core.Stat;

public class TrainingSystem {

    public void trainStrength(Player player) {
        Stat stats = player.getStats();
        stats.increaseStrength(1);
        System.out.println(player.getName() + " trained Strength! HP and DMG increased.");
    }

    public void trainAgility(Player player) {
        Stat stats = player.getStats();
        stats.increaseAgility(1);
        System.out.println(player.getName() + " trained Agility! SPD and Evasion increased.");
    }

    public void trainIntellect(Player player) {
        Stat stats = player.getStats();
        stats.increaseIntelligence(1);
        System.out.println(player.getName() + " trained Intellect! Accuracy and cooldown reduction increased.");
    }

    public void train(Player player, String stat) {
        switch (stat.toUpperCase()) {
            case "STR": trainStrength(player); break;
            case "AGI": trainAgility(player); break;
            case "INT": trainIntellect(player); break;
            default:
                System.out.println("Unknown stat: " + stat);
        }
    }
}

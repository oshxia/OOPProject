package game.system;

import game.core.Player;
import game.core.Stat;
import game.core.Profession;

public class PlayerSystem {

    private Player player;

    public Player createPlayer(String name) {
        if (player == null) {
            player = new Player(name, Profession.WARRIOR, new Stat(100, 50, 40));
            System.out.println("Created player: " + player.getName());
        } else {
            System.out.println("Player already exists: " + player.getName());
        }
        return player;
    }

    public Player getPlayer() {
        if (player == null) {
            System.out.println("No player created yet!");
        }
        return player;
    }
}

package game.system;

import game.core.Player;
import java.io.*;

public class SaveLoadSystem {

    private static final String SAVE_FILE = "player_save.dat";

    public void savePlayer(Player player) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(player);
            System.out.println("Player saved successfully!");
        } catch (IOException e) {
            System.out.println("Failed to save player: " + e.getMessage());
        }
    }

    public Player loadPlayer() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            Player player = (Player) ois.readObject();
            System.out.println("Player loaded successfully!");
            return player;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load player: " + e.getMessage());
            return null;
        }
    }
}

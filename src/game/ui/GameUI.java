package game.ui;

import game.core.*;
import game.data.*;
import game.system.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class GameUI extends Application {

    // Systems
    private EntitySystem entitySystem = new EntitySystem();
    private SkillSystem skillSystem = new SkillSystem();
    private CooldownSystem cooldownSystem = new CooldownSystem();
    private CombatSystem combatSystem = new CombatSystem(entitySystem, skillSystem, cooldownSystem);
    private ActionValueSystem actionValueSystem = new ActionValueSystem(entitySystem);
    private EnemyAISystem enemyAISystem = new EnemyAISystem(entitySystem, skillSystem, cooldownSystem);

    // Game state
    private Player player;
    private Skill[] playerSkills;
    private Enemy currentEnemy;

    // UI components
    private PlayerPanel playerPanel;
    private BattlePanel battlePanel;
    private HBox skillButtonsBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        setupGame();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: player info
        playerPanel = new PlayerPanel(player, skillSystem, cooldownSystem);
        root.setTop(playerPanel);

        // Center: battle messages + enemy icon
        battlePanel = new BattlePanel();
        root.setCenter(battlePanel);

        // Bottom: skill buttons
        skillButtonsBox = new HBox(10);
        skillButtonsBox.setAlignment(Pos.CENTER);
        root.setBottom(skillButtonsBox);

        setupSkillButtons();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("RPG - Static UI Test");
        stage.setScene(scene);
        stage.show();

        battlePanel.addMessage("Battle Start! Enemy has appeared!");
    }

    private void setupGame() {
        // Simple player creation
        Profession profession = Profession.WARRIOR; // default
        player = new Player("Hero", profession);

        // Sample skills for profession
        playerSkills = SkillsData.getSkillsForProfession(profession);

        // Create a dummy enemy (info hidden)
        currentEnemy = EnemiesData.getAllEnemyTypes()[0];

        // Prepare systems
        combatSystem.prepareBattle(player, currentEnemy);
        actionValueSystem.initializeBattle(player, currentEnemy);
    }

    private void setupSkillButtons() {
        skillButtonsBox.getChildren().clear();
        for (int i = 0; i < playerSkills.length; i++) {
            Skill skill = playerSkills[i];
            Button btn = new Button(skill.getName());
            btn.setPrefWidth(120);

            // Show damage/cooldown on tooltip
            Tooltip tt = new Tooltip("Damage: " + skillSystem.calculateDamage(player, skill)
                                    + "\nCD: " + skill.getBaseCooldown());
            Tooltip.install(btn, tt);

            int skillIndex = i;
            btn.setOnAction(e -> executePlayerSkill(skillIndex));

            skillButtonsBox.getChildren().add(btn);
        }
    }

    private void executePlayerSkill(int index) {
        Skill skill = playerSkills[index];

        // Check cooldown
        if (cooldownSystem.getRemainingCooldown(player, skill) > 0) {
            battlePanel.addMessage(skill.getName() + " is on cooldown!");
            return;
        }

        // Execute attack
        CombatSystem.CombatResult result = combatSystem.playerAttack(player, currentEnemy, playerSkills, index);
        battlePanel.addMessage(result.getMessage());

        // Update player panel
        playerPanel.update();

        // Advance Action Value
        actionValueSystem.advanceToNextTurn();
    }
}

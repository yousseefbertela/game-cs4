package game.gui;

import game.engine.Role;
import game.engine.monsters.Dasher;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MonsterPanel extends VBox {

    private final Monster monster;
    private final boolean isPlayer;

    private final Label header = new Label();
    private final Label typeLabel = new Label();
    private final Label originalRoleLabel = new Label();
    private final Label currentRoleLabel = new Label();
    private final Label energyLabel = new Label();
    private final Label positionLabel = new Label();
    private final Label statusLabel = new Label();
    private final ProgressBar energyBar = new ProgressBar(0);

    public MonsterPanel(Monster monster, boolean isPlayer) {
        this.monster = monster;
        this.isPlayer = isPlayer;
        setSpacing(6);
        setPadding(new Insets(14));
        getStyleClass().add("monster-panel");
        getStyleClass().add(monster.getOriginalRole() == Role.SCARER ? "panel-scarer" : "panel-laugher");

        header.getStyleClass().add("monster-name");
        typeLabel.getStyleClass().add("monster-type");
        originalRoleLabel.getStyleClass().add("monster-meta");
        currentRoleLabel.getStyleClass().add("monster-meta");
        energyLabel.getStyleClass().add("monster-energy");
        positionLabel.getStyleClass().add("monster-meta");
        statusLabel.getStyleClass().add("monster-status");
        statusLabel.setWrapText(true);

        energyBar.getStyleClass().add("energy-bar");
        energyBar.setPrefWidth(260);

        Label roleHeading = new Label("Roles");
        roleHeading.getStyleClass().add("panel-heading");

        Label statHeading = new Label("Status");
        statHeading.getStyleClass().add("panel-heading");

        HBox topRow = new HBox(8, header);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox roles = new VBox(2, roleHeading, originalRoleLabel, currentRoleLabel);
        VBox stats = new VBox(2, statHeading, positionLabel, energyLabel, energyBar, statusLabel);

        getChildren().addAll(topRow, typeLabel, roles, stats);
        refresh(false);
    }

    public Monster getMonster() { return monster; }

    public void setCurrentTurn(boolean current) {
        if (current && !getStyleClass().contains("panel-current"))
            getStyleClass().add("panel-current");
        else if (!current)
            getStyleClass().remove("panel-current");
    }

    public void refresh(boolean energyChanged) {
        header.setText((isPlayer ? "YOU - " : "OPPONENT - ") + monster.getName());
        typeLabel.setText("Type: " + monster.getClass().getSimpleName());
        originalRoleLabel.setText("Original role: " + monster.getOriginalRole());
        boolean confused = monster.isConfused();
        currentRoleLabel.setText("Current role: " + monster.getRole() + (confused ? "  (CONFUSED)" : ""));
        if (confused) {
            if (!currentRoleLabel.getStyleClass().contains("role-confused"))
                currentRoleLabel.getStyleClass().add("role-confused");
        } else {
            currentRoleLabel.getStyleClass().remove("role-confused");
        }

        positionLabel.setText("Position: cell " + monster.getPosition());
        energyLabel.setText("Energy: " + monster.getEnergy() + " / 1000");
        energyBar.setProgress(Math.min(1.0, monster.getEnergy() / 1000.0));

        StringBuilder st = new StringBuilder();
        if (monster.isShielded()) st.append("[Shield]  ");
        if (monster.isFrozen()) st.append("[Frozen - skips next turn]  ");
        if (confused) st.append("[Confusion x").append(monster.getConfusionTurns()).append("]  ");
        if (monster instanceof Dasher) {
            int m = ((Dasher) monster).getMomentumTurns();
            if (m > 0) st.append("[Momentum Rush x").append(m).append("]  ");
        }
        if (monster instanceof MultiTasker) {
            int f = ((MultiTasker) monster).getNormalSpeedTurns();
            if (f > 0) st.append("[Focus Mode x").append(f).append("]  ");
        }
        if (st.length() == 0) st.append("No active effects");
        statusLabel.setText(st.toString().trim());

        if (energyChanged) {
            getStyleClass().remove("energy-flash");
            getStyleClass().add("energy-flash");
            javafx.animation.PauseTransition pt =
                    new javafx.animation.PauseTransition(javafx.util.Duration.millis(600));
            pt.setOnFinished(e -> getStyleClass().remove("energy-flash"));
            pt.play();
        }
    }
}

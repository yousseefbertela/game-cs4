package game.gui;

import game.engine.Role;
import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EndView {

    private final StackPane root;

    public EndView(Main app, Monster winner, Monster player, Monster opponent) {
        Label crown = new Label("*");
        crown.getStyleClass().add("crown");

        boolean playerWon = (winner == player);
        Label headline = new Label(playerWon ? "VICTORY!" : "GAME OVER");
        headline.getStyleClass().add(playerWon ? "victory-title" : "defeat-title");

        Label announce = new Label(winner.getName()
                + " (" + winner.getOriginalRole() + " - " + winner.getClass().getSimpleName() + ") wins the touchdown!");
        announce.getStyleClass().add("end-announce");
        announce.setWrapText(true);

        VBox playerStats = monsterCard("You", player, playerWon);
        VBox opponentStats = monsterCard("Opponent", opponent, !playerWon);

        HBox stats = new HBox(20, playerStats, opponentStats);
        stats.setAlignment(Pos.CENTER);

        Button back = new Button("Return to Start Menu");
        back.getStyleClass().add("primary-button");
        back.setOnAction(e -> app.showStart());

        Button exit = new Button("Exit");
        exit.getStyleClass().add("secondary-button");
        exit.setOnAction(e -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });

        HBox buttons = new HBox(16, back, exit);
        buttons.setAlignment(Pos.CENTER);

        VBox card = new VBox(20, crown, headline, announce, stats, buttons);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36));
        card.setMaxWidth(900);
        card.getStyleClass().add("end-card");

        root = new StackPane(card);
        root.getStyleClass().add("start-root");
    }

    private VBox monsterCard(String header, Monster m, boolean winner) {
        Label h = new Label(header + (winner ? " - WINNER" : ""));
        h.getStyleClass().add("end-stat-header");

        Label name = new Label(m.getName());
        name.getStyleClass().add("end-stat-name");

        Label role = new Label("Original role: " + m.getOriginalRole());
        Label type = new Label("Type: " + m.getClass().getSimpleName());
        Label energy = new Label("Final energy: " + m.getEnergy());
        Label position = new Label("Final position: cell " + m.getPosition());

        for (Label l : new Label[]{role, type, energy, position})
            l.getStyleClass().add("end-stat-line");

        VBox v = new VBox(4, h, name, role, type, energy, position);
        v.setAlignment(Pos.CENTER_LEFT);
        v.setPadding(new Insets(16));
        v.getStyleClass().add("end-stat-card");
        v.getStyleClass().add(m.getOriginalRole() == Role.SCARER ? "panel-scarer" : "panel-laugher");
        if (winner) v.getStyleClass().add("end-winner");
        return v;
    }

    public StackPane getRoot() {
        return root;
    }
}

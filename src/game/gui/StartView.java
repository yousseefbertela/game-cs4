package game.gui;

import game.engine.Role;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StartView {

    private final StackPane root;
    private Role selectedRole = null;

    public StartView(Main app) {
        Label title = new Label("DoorDasH");
        title.getStyleClass().add("game-title");

        Label subtitle = new Label("Scare vs Laugh Touchdown");
        subtitle.getStyleClass().add("game-subtitle");

        Label tagline = new Label("Pick a side. Race down the corridor. Reach the door with 1000 energy first.");
        tagline.getStyleClass().add("tagline");

        Label sideLabel = new Label("Choose your side:");
        sideLabel.getStyleClass().add("section-heading");

        ToggleGroup group = new ToggleGroup();
        ToggleButton scarer = new ToggleButton("SCARER");
        scarer.setToggleGroup(group);
        scarer.getStyleClass().addAll("side-toggle", "side-scarer");

        ToggleButton laugher = new ToggleButton("LAUGHER");
        laugher.setToggleGroup(group);
        laugher.getStyleClass().addAll("side-toggle", "side-laugher");

        HBox sides = new HBox(30, scarer, laugher);
        sides.setAlignment(Pos.CENTER);

        Button startBtn = new Button("Start Game");
        startBtn.getStyleClass().add("primary-button");
        startBtn.setDisable(true);

        Button instructionsBtn = new Button("Instructions");
        instructionsBtn.getStyleClass().add("secondary-button");

        Button exitBtn = new Button("Exit");
        exitBtn.getStyleClass().add("secondary-button");

        group.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == scarer) selectedRole = Role.SCARER;
            else if (newT == laugher) selectedRole = Role.LAUGHER;
            else selectedRole = null;
            startBtn.setDisable(selectedRole == null);
        });

        startBtn.setOnAction(e -> {
            if (selectedRole == null) {
                Dialogs.error("No side selected", "Please choose SCARER or LAUGHER before starting.");
                return;
            }
            app.showGame(selectedRole);
        });
        instructionsBtn.setOnAction(e -> app.showInstructions());
        exitBtn.setOnAction(e -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });

        HBox buttonRow = new HBox(20, startBtn, instructionsBtn, exitBtn);
        buttonRow.setAlignment(Pos.CENTER);

        VBox card = new VBox(20, title, subtitle, separator(), tagline, sideLabel, sides, buttonRow);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(50, 70, 50, 70));
        card.setMaxWidth(800);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.getStyleClass().add("start-card");

        root = new StackPane(card);
        root.getStyleClass().add("start-root");
    }

    private Region separator() {
        Region r = new Region();
        r.setPrefHeight(2);
        r.setMaxWidth(Double.MAX_VALUE);
        r.getStyleClass().add("divider");
        return r;
    }

    public StackPane getRoot() {
        return root;
    }
}

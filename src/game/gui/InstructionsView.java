package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class InstructionsView {

    private final StackPane root;

    public InstructionsView(Main app) {
        Label title = new Label("How to play");
        title.getStyleClass().add("instructions-title");

        TextFlow flow = new TextFlow();
        flow.getChildren().addAll(
                section("Goal"),
                body("Be the first monster to reach cell 99 with at least 1000 energy."),
                section("Sides"),
                body("There are two roles: SCARER and LAUGHER. Pick a side and one monster of that role becomes your hero. A random opponent of the other role is your rival."),
                section("Turns"),
                body("On your turn you may (optionally) spend 500 energy to activate your powerup, then roll the dice and your monster moves forward. Landing on a cell triggers its effect."),
                section("Cells"),
                body("- Normal corridor: nothing happens.\n"
                        + "- SCARER / LAUGHER doors: first monster to land on it gains/loses energy depending on whether it matches your role. Same-role teammates on stationed cells share the bonus. Doors are then exhausted.\n"
                        + "- Card cells: draw a card and apply its effect.\n"
                        + "- Monster cells: meeting an ally triggers your powerup, meeting a rival lets you steal energy if you have more.\n"
                        + "- Conveyor belts push you forward, contamination socks pull you back and slip you for 100 energy."),
                section("Cards"),
                body("Swap with opponent, steal energy, send someone back to start, equip a shield that blocks the next negative effect, or confuse both monsters (their roles swap temporarily)."),
                section("Monsters"),
                body("- Dasher: moves 2x normally, 3x for 3 turns when powerup is active.\n"
                        + "- Dynamo: gains double energy from any gain; powerup freezes opponent for one turn.\n"
                        + "- MultiTasker: gains a +200 energy bonus on every gain but moves at half speed unless Focus Mode is active.\n"
                        + "- Schemer: every energy gain adds +10; powerup steals 10 from every stationed monster and the opponent.")
        );
        flow.setMaxWidth(960);

        ScrollPane scroll = new ScrollPane(flow);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("instructions-scroll");
        scroll.setPrefViewportHeight(560);

        Button back = new Button("Back");
        back.getStyleClass().add("secondary-button");
        back.setOnAction(e -> app.showStart());

        VBox card = new VBox(18, title, scroll, back);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(36));
        card.setMaxWidth(1040);
        card.getStyleClass().add("instructions-card");

        root = new StackPane(card);
        root.getStyleClass().add("start-root");
    }

    private Text section(String s) {
        Text t = new Text("\n" + s + "\n");
        t.getStyleClass().add("instructions-section");
        t.setStyle("-fx-font-weight:bold; -fx-font-size:18; -fx-fill:#FFD56B;");
        return t;
    }

    private Text body(String s) {
        Text t = new Text(s + "\n");
        t.setStyle("-fx-font-size:14; -fx-fill:#E6E6E6;");
        return t;
    }

    public StackPane getRoot() {
        return root;
    }
}

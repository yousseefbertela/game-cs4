package game.gui;

import game.engine.Constants;
import game.engine.cells.Cell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameView {

    private final BorderPane root;

    private final CellView[] cellViews = new CellView[Constants.BOARD_SIZE];

    private final Label turnLabel = new Label("Turn 1");
    private final Label currentPlayerLabel = new Label();
    private final Label diceLabel = new Label("Roll the dice to begin");
    private final Label freezeBanner = new Label();

    private final Button rollBtn = new Button("Roll Dice");
    private final Button powerupBtn = new Button("Activate Powerup (500)");
    private final Button quitBtn = new Button("Quit to menu");

    private final ListView<String> eventLog = new ListView<>();

    private final StackPane cardArea = new StackPane();
    private final Label cardName = new Label("No card drawn yet");
    private final Label cardEffect = new Label();

    private MonsterPanel playerPanel;
    private MonsterPanel opponentPanel;

    public GameView() {
        root = new BorderPane();
        root.getStyleClass().add("game-root");
        root.setPadding(new Insets(12));

        root.setTop(buildTopBar());
        root.setCenter(buildBoard());
        root.setRight(buildSidePanel());
        root.setLeft(buildLeftPanel());
        root.setBottom(buildBottomBar());
    }

    public BorderPane getRoot() { return root; }
    public CellView[] getCellViews() { return cellViews; }
    public Label getTurnLabel() { return turnLabel; }
    public Label getCurrentPlayerLabel() { return currentPlayerLabel; }
    public Label getDiceLabel() { return diceLabel; }
    public Label getFreezeBanner() { return freezeBanner; }
    public Button getRollBtn() { return rollBtn; }
    public Button getPowerupBtn() { return powerupBtn; }
    public Button getQuitBtn() { return quitBtn; }
    public ListView<String> getEventLog() { return eventLog; }
    public Label getCardName() { return cardName; }
    public Label getCardEffect() { return cardEffect; }

    public void setPlayerPanel(MonsterPanel p) {
        this.playerPanel = p;
        rightStack.getChildren().setAll(p, opponentPanel == null ? new Label() : opponentPanel,
                cardSection, controlSection);
    }
    public void setOpponentPanel(MonsterPanel p) {
        this.opponentPanel = p;
        rightStack.getChildren().setAll(playerPanel == null ? new Label() : playerPanel, p,
                cardSection, controlSection);
    }

    private VBox rightStack;
    private VBox cardSection;
    private VBox controlSection;

    private HBox buildTopBar() {
        Label title = new Label("DoorDasH");
        title.getStyleClass().add("game-header-title");

        turnLabel.getStyleClass().add("turn-label");
        currentPlayerLabel.getStyleClass().add("current-player-label");
        diceLabel.getStyleClass().add("dice-label");
        freezeBanner.getStyleClass().add("freeze-banner");
        freezeBanner.setVisible(false);
        freezeBanner.setManaged(false);

        HBox box = new HBox(20, title, sep(), turnLabel, sep(), currentPlayerLabel, sep(),
                diceLabel, sep(), freezeBanner);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10, 16, 12, 16));
        box.getStyleClass().add("top-bar");
        return box;
    }

    private Region sep() {
        Region r = new Region();
        r.setPrefWidth(1);
        r.setPrefHeight(28);
        r.getStyleClass().add("vertical-divider");
        return r;
    }

    private GridPane buildBoard() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setPadding(new Insets(10));
        grid.getStyleClass().add("board-grid");

        int cols = Constants.BOARD_COLS;
        int rows = Constants.BOARD_ROWS;
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            int row = i / cols;
            int col = i % cols;
            if (row % 2 == 1) col = cols - 1 - col;
            int displayRow = rows - 1 - row;
            CellView cv = new CellView(i);
            cellViews[i] = cv;
            grid.add(cv, col, displayRow);
        }
        return grid;
    }

    private VBox buildSidePanel() {
        rightStack = new VBox(12);
        rightStack.setPadding(new Insets(8, 8, 8, 8));
        rightStack.setPrefWidth(320);
        rightStack.setAlignment(Pos.TOP_CENTER);

        Label cardHeading = new Label("Last Card Drawn");
        cardHeading.getStyleClass().add("panel-heading");
        cardName.getStyleClass().add("card-name");
        cardEffect.getStyleClass().add("card-effect");
        cardEffect.setWrapText(true);
        cardArea.getChildren().add(new VBox(4, cardName, cardEffect));
        cardArea.getStyleClass().add("card-panel");
        cardArea.setMinHeight(110);
        cardSection = new VBox(6, cardHeading, cardArea);

        rollBtn.getStyleClass().add("primary-button");
        powerupBtn.getStyleClass().add("secondary-button");
        quitBtn.getStyleClass().add("secondary-button");
        Label cheatHint = new Label("Cheats:  W = teleport to cell 99    E = +100 energy");
        cheatHint.getStyleClass().add("cheat-hint");
        cheatHint.setWrapText(true);
        controlSection = new VBox(8, rollBtn, powerupBtn, quitBtn, cheatHint);
        controlSection.setAlignment(Pos.CENTER);
        controlSection.setPadding(new Insets(4, 0, 0, 0));

        rightStack.getChildren().addAll(cardSection, controlSection);
        return rightStack;
    }

    private VBox buildLeftPanel() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(8));
        box.setPrefWidth(280);

        Label heading = new Label("Event log");
        heading.getStyleClass().add("panel-heading");
        eventLog.getStyleClass().add("event-log");
        eventLog.setPrefHeight(640);

        Label legendHeading = new Label("Legend");
        legendHeading.getStyleClass().add("panel-heading");

        VBox legend = new VBox(3,
                legendRow("Start (0) / Finish (99)", "cell-finish"),
                legendRow("SCARER door", "cell-door-scarer"),
                legendRow("LAUGHER door", "cell-door-laugher"),
                legendRow("Exhausted door", "cell-door-used"),
                legendRow("Card cell", "cell-card"),
                legendRow("Monster station", "cell-monster-cell"),
                legendRow("Conveyor belt", "cell-conveyor"),
                legendRow("Contamination sock", "cell-sock"),
                legendRow("Normal corridor", "cell-normal")
        );

        box.getChildren().addAll(heading, eventLog, legendHeading, legend);
        return box;
    }

    private HBox legendRow(String text, String styleClass) {
        Region swatch = new Region();
        swatch.setPrefSize(20, 16);
        swatch.getStyleClass().addAll("legend-swatch", styleClass);
        Label l = new Label(text);
        l.getStyleClass().add("legend-text");
        HBox h = new HBox(8, swatch, l);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private HBox buildBottomBar() {
        HBox h = new HBox();
        h.setPadding(new Insets(6));
        return h;
    }

    public void bindCells(Cell[][] boardCells) {
        for (int row = 0; row < Constants.BOARD_ROWS; row++) {
            for (int col = 0; col < Constants.BOARD_COLS; col++) {
                int idx = row * Constants.BOARD_COLS + col;
                if (row % 2 == 1) idx = row * Constants.BOARD_COLS + (Constants.BOARD_COLS - 1 - col);
                cellViews[idx].bind(boardCells[row][col]);
            }
        }
    }

    public void refreshAllCells() {
        for (CellView c : cellViews) c.refresh();
    }

    public void appendLog(String line) {
        eventLog.getItems().add(0, line);
        if (eventLog.getItems().size() > 200)
            eventLog.getItems().remove(eventLog.getItems().size() - 1);
    }
}

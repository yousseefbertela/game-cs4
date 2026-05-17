package game.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static final double STAGE_WIDTH = 1440;
    public static final double STAGE_HEIGHT = 860;

    private Stage primaryStage;
    private final StackPane root = new StackPane();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("DoorDasH: Scare vs Laugh Touchdown");
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });

        Scene scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/game/gui/styles.css").toExternalForm());
        stage.setScene(scene);

        String auto = System.getProperty("doordash.auto");
        if (auto != null) {
            game.engine.Role r = auto.equalsIgnoreCase("LAUGHER")
                    ? game.engine.Role.LAUGHER : game.engine.Role.SCARER;
            showGame(r);
            String turnsProp = System.getProperty("doordash.turns");
            if (turnsProp != null) {
                int n = Integer.parseInt(turnsProp);
                final int[] left = {n};
                Thread driver = new Thread(() -> {
                    while (left[0] > 0) {
                        try { Thread.sleep(150); } catch (InterruptedException e) { return; }
                        javafx.application.Platform.runLater(() -> {
                            javafx.scene.Node nd = stage.getScene().getRoot().lookup(".primary-button");
                            if (nd instanceof javafx.scene.control.Button)
                                ((javafx.scene.control.Button) nd).fire();
                        });
                        left[0]--;
                    }
                }, "auto-driver");
                driver.setDaemon(true);
                driver.start();
            }
        } else {
            showStart();
        }
        stage.show();
    }

    public void showStart() {
        clearKeyBindings();
        root.getChildren().setAll(new StartView(this).getRoot());
    }

    public void showGame(game.engine.Role role) {
        try {
            GameController controller = new GameController(this, role);
            root.getChildren().setAll(controller.getView().getRoot());
            installCheatKeys(controller);
        } catch (Exception ex) {
            Dialogs.error("Cannot start game", ex.getMessage());
        }
    }

    public void showInstructions() {
        clearKeyBindings();
        root.getChildren().setAll(new InstructionsView(this).getRoot());
    }

    public void showEnd(game.engine.monsters.Monster winner,
                        game.engine.monsters.Monster player,
                        game.engine.monsters.Monster opponent) {
        clearKeyBindings();
        root.getChildren().setAll(new EndView(this, winner, player, opponent).getRoot());
    }

    private void installCheatKeys(GameController controller) {
        primaryStage.getScene().setOnKeyPressed(ev -> {
            if (controller.isGameOver()) return;
            switch (ev.getCode()) {
                case W:
                    controller.cheatTeleportToFinish();
                    ev.consume();
                    break;
                case E:
                    controller.cheatGainEnergy();
                    ev.consume();
                    break;
                default:
                    break;
            }
        });
    }

    private void clearKeyBindings() {
        if (primaryStage != null && primaryStage.getScene() != null)
            primaryStage.getScene().setOnKeyPressed(null);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

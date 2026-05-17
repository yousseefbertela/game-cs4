package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class Dialogs {

    private Dialogs() {}

    public static void error(String header, String message) {
        show("Invalid Action", header, message, "dialog-error", false);
    }

    public static void info(String header, String message) {
        show("Info", header, message, "dialog-info", false);
    }

    public static boolean confirm(String header, String message) {
        return show("Confirm", header, message, "dialog-confirm", true);
    }

    private static boolean show(String title, String header, String message,
                                String accentClass, boolean confirm) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(title);

        Label headerLabel = new Label(header == null ? "" : header);
        headerLabel.getStyleClass().add("popup-header");
        headerLabel.setWrapText(true);

        Label messageLabel = new Label(message == null ? "" : message);
        messageLabel.getStyleClass().add("popup-message");
        messageLabel.setWrapText(true);

        final boolean[] result = {false};

        Button ok = new Button(confirm ? "OK" : "Close");
        ok.getStyleClass().add("primary-button");
        ok.setOnAction(e -> {
            result[0] = true;
            stage.close();
        });

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        if (confirm) {
            Button cancel = new Button("Cancel");
            cancel.getStyleClass().add("secondary-button");
            cancel.setOnAction(e -> {
                result[0] = false;
                stage.close();
            });
            buttons.getChildren().addAll(cancel, ok);
        } else {
            buttons.getChildren().add(ok);
        }

        VBox content = new VBox(14, headerLabel, messageLabel, buttons);
        content.setPadding(new Insets(22, 28, 22, 28));
        content.setMaxWidth(520);
        content.getStyleClass().addAll("popup-card", accentClass);

        StackPane root = new StackPane(content);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("popup-root");

        Scene scene = new Scene(root, 560, 240);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(Dialogs.class.getResource("/game/gui/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
        return result[0];
    }
}

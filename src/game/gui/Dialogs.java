package game.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;

public final class Dialogs {

    private Dialogs() {}

    public static void error(String header, String message) {
        show(AlertType.ERROR, "Invalid Action", header, message);
    }

    public static void info(String header, String message) {
        show(AlertType.INFORMATION, "Info", header, message);
    }

    private static void show(AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message == null ? "" : message);
        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(Dialogs.class.getResource("/game/gui/styles.css").toExternalForm());
        pane.getStyleClass().add("doordash-dialog");
        alert.showAndWait();
    }
}

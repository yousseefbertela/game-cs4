package game.gui;

import game.engine.Role;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.monsters.Monster;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CellView extends StackPane {

    public static final double SIZE = 76;

    private final int index;
    private Cell modelCell;

    private final Label indexLabel = new Label();
    private final Label kindLabel = new Label();
    private final Label valueLabel = new Label();
    private final Label monsterLabel = new Label();
    private final Label markerLabel = new Label();

    public CellView(int index) {
        this.index = index;
        setPrefSize(SIZE, SIZE);
        setMinSize(SIZE, SIZE);
        setMaxSize(SIZE, SIZE);
        getStyleClass().add("cell");

        indexLabel.setText(String.valueOf(index));
        indexLabel.getStyleClass().add("cell-index");

        kindLabel.getStyleClass().add("cell-kind");
        valueLabel.getStyleClass().add("cell-value");
        monsterLabel.getStyleClass().add("cell-monster");
        markerLabel.getStyleClass().add("cell-marker");

        VBox content = new VBox(1, kindLabel, valueLabel, monsterLabel);
        content.setAlignment(Pos.CENTER);

        StackPane.setAlignment(indexLabel, Pos.TOP_LEFT);
        StackPane.setAlignment(markerLabel, Pos.BOTTOM_RIGHT);
        getChildren().addAll(content, indexLabel, markerLabel);
    }

    public int getIndex() { return index; }

    public void bind(Cell cell) {
        this.modelCell = cell;
        refresh();
    }

    public Cell getModelCell() { return modelCell; }

    public void refresh() {
        getStyleClass().removeAll(
                "cell-normal", "cell-card", "cell-monster-cell",
                "cell-door-scarer", "cell-door-laugher", "cell-door-used",
                "cell-conveyor", "cell-sock",
                "cell-start", "cell-finish"
        );

        if (modelCell == null) return;

        kindLabel.setText("");
        valueLabel.setText("");
        monsterLabel.setText("");

        if (index == 0) getStyleClass().add("cell-start");
        if (index == 99) getStyleClass().add("cell-finish");

        if (modelCell instanceof DoorCell) {
            DoorCell d = (DoorCell) modelCell;
            String prefix = d.getRole() == Role.SCARER ? "SCARER" : "LAUGHER";
            kindLabel.setText(prefix + " DOOR");
            valueLabel.setText(String.valueOf(d.getEnergy()));
            getStyleClass().add(d.getRole() == Role.SCARER ? "cell-door-scarer" : "cell-door-laugher");
            if (d.isActivated()) {
                getStyleClass().add("cell-door-used");
                kindLabel.setText(prefix + " DOOR (X)");
            }
            setTooltip("Door " + prefix + " • " + d.getEnergy() + " energy" + (d.isActivated() ? " • exhausted" : ""));
        } else if (modelCell instanceof CardCell) {
            kindLabel.setText("CARD");
            valueLabel.setText("?");
            getStyleClass().add("cell-card");
            setTooltip("Card cell — draw a card on landing");
        } else if (modelCell instanceof MonsterCell) {
            MonsterCell mc = (MonsterCell) modelCell;
            kindLabel.setText("STATION");
            valueLabel.setText(mc.getCellMonster().getName());
            getStyleClass().add("cell-monster-cell");
            setTooltip("Monster station: " + mc.getCellMonster().getName()
                    + " (" + mc.getCellMonster().getRole() + ") • " + mc.getCellMonster().getEnergy() + " energy");
        } else if (modelCell instanceof ConveyorBelt) {
            ConveyorBelt cb = (ConveyorBelt) modelCell;
            kindLabel.setText("CONVEYOR");
            valueLabel.setText("+" + cb.getEffect());
            getStyleClass().add("cell-conveyor");
            setTooltip("Conveyor: jumps " + cb.getEffect() + " cells forward");
        } else if (modelCell instanceof ContaminationSock) {
            ContaminationSock cs = (ContaminationSock) modelCell;
            kindLabel.setText("SOCK");
            valueLabel.setText(String.valueOf(cs.getEffect()));
            getStyleClass().add("cell-sock");
            setTooltip("Contamination sock: " + cs.getEffect() + " cells, -100 energy slip");
        } else {
            kindLabel.setText("CORRIDOR");
            getStyleClass().add("cell-normal");
            setTooltip("Normal corridor");
        }

        Monster m = modelCell.getMonster();
        if (m != null && !(modelCell instanceof MonsterCell)) {
            monsterLabel.setText(m.getName());
        }
        markerLabel.setText("");
    }

    public void setMarker(String text, String styleClass) {
        markerLabel.setText(text);
        markerLabel.getStyleClass().removeAll("marker-player", "marker-opponent", "marker-both");
        if (styleClass != null) markerLabel.getStyleClass().add(styleClass);
    }

    public void highlight(boolean on) {
        if (on) {
            if (!getStyleClass().contains("cell-active"))
                getStyleClass().add("cell-active");
        } else {
            getStyleClass().remove("cell-active");
        }
    }

    private void setTooltip(String text) {
        Tooltip t = new Tooltip(text);
        Tooltip.install(this, t);
    }
}

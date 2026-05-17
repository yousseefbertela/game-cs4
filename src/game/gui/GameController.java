package game.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class GameController {

    private final Main app;
    private final Game game;
    private final GameView view;
    private final MonsterPanel playerPanel;
    private final MonsterPanel opponentPanel;

    private int turnNumber = 1;
    private boolean powerupArmed = false;
    private boolean gameOver = false;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    public GameController(Main app, Role role) throws IOException {
        this.app = app;
        this.game = new Game(role);
        this.view = new GameView();

        playerPanel = new MonsterPanel(game.getPlayer(), true);
        opponentPanel = new MonsterPanel(game.getOpponent(), false);
        view.setPlayerPanel(playerPanel);
        view.setOpponentPanel(opponentPanel);

        view.bindCells(game.getBoard().getBoardCells());

        view.getRollBtn().setOnAction(e -> onRoll());
        view.getPowerupBtn().setOnAction(e -> onArmPowerup());
        view.getQuitBtn().setOnAction(e -> confirmQuit());

        view.appendLog("Game started. You are " + role + " - playing as "
                + game.getPlayer().getName() + " (" + game.getPlayer().getClass().getSimpleName() + ").");
        view.appendLog("Opponent: " + game.getOpponent().getName()
                + " (" + game.getOpponent().getClass().getSimpleName() + ", " + game.getOpponent().getRole() + ").");
        view.appendLog("25 cards shuffled into the deck.");

        refreshAll(false, false);
        updateTurnIndicators();

        System.setOut(new PrintStream(captured));
    }

    public GameView getView() { return view; }

    public static final int CHEAT_ENERGY_GAIN = 100;

    public void cheatTeleportToFinish() {
        if (gameOver) return;
        Monster cur = game.getCurrent();
        int oldPos = cur.getPosition();
        cur.setPosition(Constants.WINNING_POSITION);
        view.appendLog("CHEAT (W): " + cur.getName()
                + " teleported from cell " + oldPos
                + " to cell " + Constants.WINNING_POSITION + ".");
        refreshAll(false, false);
        view.refreshAllCells();
        renderMonsterMarkers();

        Monster winner = game.getWinner();
        if (winner != null) {
            gameOver = true;
            view.getRollBtn().setDisable(true);
            view.getPowerupBtn().setDisable(true);
            view.appendLog("GAME OVER - " + winner.getName() + " wins!");
            System.setOut(originalOut);
            PauseTransition pt = new PauseTransition(Duration.seconds(1.2));
            pt.setOnFinished(e -> app.showEnd(winner, game.getPlayer(), game.getOpponent()));
            pt.play();
        }
    }

    public void cheatGainEnergy() {
        if (gameOver) return;
        Monster cur = game.getCurrent();
        int before = cur.getEnergy();
        cur.alterEnergy(CHEAT_ENERGY_GAIN);
        view.appendLog("CHEAT (E): " + cur.getName()
                + " energy " + before + " -> " + cur.getEnergy()
                + " (+" + (cur.getEnergy() - before) + ").");
        refreshAll(cur == game.getPlayer(), cur == game.getOpponent());

        Monster winner = game.getWinner();
        if (winner != null) {
            gameOver = true;
            view.getRollBtn().setDisable(true);
            view.getPowerupBtn().setDisable(true);
            view.appendLog("GAME OVER - " + winner.getName() + " wins!");
            System.setOut(originalOut);
            PauseTransition pt = new PauseTransition(Duration.seconds(1.2));
            pt.setOnFinished(e -> app.showEnd(winner, game.getPlayer(), game.getOpponent()));
            pt.play();
        }
    }

    public boolean isGameOver() { return gameOver; }

    private void onArmPowerup() {
        if (gameOver) return;
        Monster cur = game.getCurrent();
        if (powerupArmed) {
            powerupArmed = false;
            view.appendLog(cur.getName() + " stood down - powerup will NOT trigger this turn.");
            view.getPowerupBtn().setText("Activate Powerup (500)");
            view.getPowerupBtn().getStyleClass().remove("armed");
            return;
        }
        if (cur.getEnergy() < Constants.POWERUP_COST) {
            Dialogs.error("Not enough energy",
                    cur.getName() + " needs " + Constants.POWERUP_COST
                            + " energy to activate a powerup but only has " + cur.getEnergy() + ".");
            return;
        }
        powerupArmed = true;
        view.appendLog(cur.getName() + " armed powerup - will trigger before next dice roll.");
        view.getPowerupBtn().setText("Powerup ARMED - click again to cancel");
        view.getPowerupBtn().getStyleClass().add("armed");
    }

    private void onRoll() {
        if (gameOver) return;
        Monster cur = game.getCurrent();

        captured.reset();

        int playerEnergyBefore = game.getPlayer().getEnergy();
        int opponentEnergyBefore = game.getOpponent().getEnergy();
        int playerPositionBefore = game.getPlayer().getPosition();
        int opponentPositionBefore = game.getOpponent().getPosition();
        Role playerRoleBefore = game.getPlayer().getRole();
        Role opponentRoleBefore = game.getOpponent().getRole();
        boolean playerShieldBefore = game.getPlayer().isShielded();
        boolean opponentShieldBefore = game.getOpponent().isShielded();
        boolean frozenBefore = cur.isFrozen();

        if (powerupArmed) {
            try {
                game.usePowerup();
                view.appendLog(cur.getName() + " activated POWERUP (cost 500).");
            } catch (OutOfEnergyException ex) {
                Dialogs.error("Powerup failed", ex.getMessage());
                powerupArmed = false;
                view.getPowerupBtn().setText("Activate Powerup (500)");
                view.getPowerupBtn().getStyleClass().remove("armed");
                return;
            }
        }

        int roll = game.rollDice();
        view.getDiceLabel().setText("Dice: " + roll + "  (" + cur.getName() + ")");
        view.appendLog(cur.getName() + " rolled " + roll + ".");

        boolean cardCellLanded = isCardCellLandable(cur, roll);

        Card cardBeforeDraw = nextCardOnDeck();

        try {
            game.playTurn(roll);
        } catch (InvalidMoveException ex) {
            String out = captured.toString();
            originalOut.print(out);
            captured.reset();
            powerupArmed = false;
            view.getPowerupBtn().setText("Activate Powerup (500)");
            view.getPowerupBtn().getStyleClass().remove("armed");
            handleInvalidMove(ex, cur);
            view.getDiceLabel().setText("Dice: " + roll + " - move blocked!");
            refreshAll(false, false);
            return;
        }

        String captureText = captured.toString();
        originalOut.print(captureText);
        captured.reset();

        powerupArmed = false;
        view.getPowerupBtn().setText("Activate Powerup (500)");
        view.getPowerupBtn().getStyleClass().remove("armed");

        if (frozenBefore) {
            view.getFreezeBanner().setText(cur.getName() + " was FROZEN - turn skipped!");
            view.getFreezeBanner().setVisible(true);
            view.getFreezeBanner().setManaged(true);
            view.appendLog(cur.getName() + " was frozen and skipped their turn.");
            PauseTransition pt = new PauseTransition(Duration.seconds(3));
            pt.setOnFinished(e -> {
                view.getFreezeBanner().setVisible(false);
                view.getFreezeBanner().setManaged(false);
            });
            pt.play();
        }

        if (cardCellLanded && cardBeforeDraw != null) {
            showCardDrawn(cardBeforeDraw);
        }

        logDeltas(playerEnergyBefore, opponentEnergyBefore,
                playerPositionBefore, opponentPositionBefore,
                playerRoleBefore, opponentRoleBefore,
                playerShieldBefore, opponentShieldBefore);

        logCapturedHighlights(captureText);

        boolean playerEnergyChanged = playerEnergyBefore != game.getPlayer().getEnergy();
        boolean opponentEnergyChanged = opponentEnergyBefore != game.getOpponent().getEnergy();
        refreshAll(playerEnergyChanged, opponentEnergyChanged);
        view.refreshAllCells();
        renderMonsterMarkers();

        turnNumber++;
        view.getTurnLabel().setText("Turn " + turnNumber);

        Monster winner = game.getWinner();
        if (winner != null) {
            gameOver = true;
            view.getRollBtn().setDisable(true);
            view.getPowerupBtn().setDisable(true);
            view.appendLog("GAME OVER - " + winner.getName() + " wins!");
            System.setOut(originalOut);
            PauseTransition pt = new PauseTransition(Duration.seconds(1.4));
            pt.setOnFinished(e -> app.showEnd(winner, game.getPlayer(), game.getOpponent()));
            pt.play();
            return;
        }

        updateTurnIndicators();
    }

    private boolean isCardCellLandable(Monster cur, int roll) {
        try {
            int newPos = previewDestination(cur, roll);
            Cell c = getCellAt(newPos);
            return c instanceof CardCell;
        } catch (Exception ex) {
            return false;
        }
    }

    private int previewDestination(Monster cur, int roll) {
        int distance = roll;
        if (cur instanceof game.engine.monsters.Dasher) {
            int m = ((game.engine.monsters.Dasher) cur).getMomentumTurns();
            distance *= (m > 0 ? 3 : 2);
        } else if (cur instanceof game.engine.monsters.MultiTasker) {
            int f = ((game.engine.monsters.MultiTasker) cur).getNormalSpeedTurns();
            if (f == 0) distance /= 2;
        }
        return (cur.getPosition() + distance) % Constants.BOARD_SIZE;
    }

    private Cell getCellAt(int index) {
        int cols = Constants.BOARD_COLS;
        int row = index / cols;
        int col = index % cols;
        if (row % 2 == 1) col = cols - 1 - col;
        return game.getBoard().getBoardCells()[row][col];
    }

    private Card nextCardOnDeck() {
        if (Board.getCards() == null || Board.getCards().isEmpty()) return null;
        return Board.getCards().get(0);
    }

    private void showCardDrawn(Card card) {
        view.getCardName().setText(card.getName());
        view.getCardEffect().setText(card.getDescription());
        view.appendLog("Card drawn: " + card.getName() + " - " + card.getDescription());
        if (System.getProperty("doordash.auto") == null)
            Dialogs.info("Card drawn", card.getName() + "\n\n" + card.getDescription());
    }

    private void handleInvalidMove(InvalidMoveException ex, Monster cur) {
        Dialogs.error("Invalid move", ex.getMessage()
                + "\n\n" + cur.getName() + " stayed in place. Please choose another action.");
        view.appendLog("Move blocked: " + ex.getMessage());
        refreshAll(false, false);
    }

    private void logDeltas(int playerEnergyBefore, int opponentEnergyBefore,
                           int playerPositionBefore, int opponentPositionBefore,
                           Role playerRoleBefore, Role opponentRoleBefore,
                           boolean playerShieldBefore, boolean opponentShieldBefore) {

        int pe = game.getPlayer().getEnergy();
        int oe = game.getOpponent().getEnergy();
        if (pe != playerEnergyBefore)
            view.appendLog(game.getPlayer().getName() + " energy: "
                    + playerEnergyBefore + " -> " + pe
                    + " (" + (pe - playerEnergyBefore >= 0 ? "+" : "") + (pe - playerEnergyBefore) + ")");
        if (oe != opponentEnergyBefore)
            view.appendLog(game.getOpponent().getName() + " energy: "
                    + opponentEnergyBefore + " -> " + oe
                    + " (" + (oe - opponentEnergyBefore >= 0 ? "+" : "") + (oe - opponentEnergyBefore) + ")");

        int pp = game.getPlayer().getPosition();
        int op = game.getOpponent().getPosition();
        if (pp != playerPositionBefore)
            view.appendLog(game.getPlayer().getName() + " moved: cell "
                    + playerPositionBefore + " -> " + pp);
        if (op != opponentPositionBefore)
            view.appendLog(game.getOpponent().getName() + " moved: cell "
                    + opponentPositionBefore + " -> " + op);

        if (game.getPlayer().getRole() != playerRoleBefore)
            view.appendLog(game.getPlayer().getName() + " role swapped: "
                    + playerRoleBefore + " -> " + game.getPlayer().getRole() + " (CONFUSED)");
        if (game.getOpponent().getRole() != opponentRoleBefore)
            view.appendLog(game.getOpponent().getName() + " role swapped: "
                    + opponentRoleBefore + " -> " + game.getOpponent().getRole() + " (CONFUSED)");

        if (game.getPlayer().isShielded() && !playerShieldBefore)
            view.appendLog(game.getPlayer().getName() + " gained a shield.");
        if (game.getOpponent().isShielded() && !opponentShieldBefore)
            view.appendLog(game.getOpponent().getName() + " gained a shield.");
        if (!game.getPlayer().isShielded() && playerShieldBefore)
            view.appendLog(game.getPlayer().getName() + "'s shield was used / broken.");
        if (!game.getOpponent().isShielded() && opponentShieldBefore)
            view.appendLog(game.getOpponent().getName() + "'s shield was used / broken.");
    }

    private void logCapturedHighlights(String capture) {
        if (capture == null || capture.isEmpty()) return;
        for (String line : capture.split("\\r?\\n")) {
            String s = line.trim();
            if (s.isEmpty()) continue;
            if (s.contains("shield blocked") || s.contains("Shield"))
                view.appendLog("SHIELD: " + s);
            else if (s.contains("Momentum") || s.contains("Focus Mode") || s.contains("Chain Attack"))
                view.appendLog(s);
            else if (s.contains("Swapped") || s.contains("swapped"))
                view.appendLog(s);
            else if (s.contains("Frozen") || s.contains("frozen"))
                view.appendLog(s);
            else if (s.contains("stole") || s.contains("Stole"))
                view.appendLog(s);
            else if (s.contains("got ") && s.contains("energy"))
                view.appendLog(s);
            else if (s.contains("encountered ally"))
                view.appendLog(s);
            else if (s.contains("landed on"))
                view.appendLog(s);
        }
    }

    private void refreshAll(boolean playerEnergyChanged, boolean opponentEnergyChanged) {
        playerPanel.refresh(playerEnergyChanged);
        opponentPanel.refresh(opponentEnergyChanged);
    }

    private void updateTurnIndicators() {
        Monster cur = game.getCurrent();
        boolean playerTurn = (cur == game.getPlayer());
        playerPanel.setCurrentTurn(playerTurn);
        opponentPanel.setCurrentTurn(!playerTurn);
        view.getCurrentPlayerLabel().setText("Current: " + cur.getName()
                + (playerTurn ? " (YOU)" : " (OPPONENT)") + "  vs  "
                + (playerTurn ? game.getOpponent().getName() : game.getPlayer().getName()));

        view.getRollBtn().setText(playerTurn ? "Roll Dice" : "Roll Dice (Opponent's turn)");
        renderMonsterMarkers();
    }

    private void renderMonsterMarkers() {
        for (CellView cv : view.getCellViews()) {
            cv.setMarker("", null);
            cv.highlight(false);
        }
        int playerPos = game.getPlayer().getPosition();
        int opponentPos = game.getOpponent().getPosition();
        if (playerPos == opponentPos) {
            view.getCellViews()[playerPos].setMarker("P+O", "marker-both");
        } else {
            view.getCellViews()[playerPos].setMarker("YOU", "marker-player");
            view.getCellViews()[opponentPos].setMarker("OPP", "marker-opponent");
        }
        view.getCellViews()[game.getCurrent().getPosition()].highlight(true);
    }

    private void confirmQuit() {
        boolean ok = Dialogs.confirm("Quit current game?",
                "Return to the main menu and abandon this game?");
        if (ok) {
            System.setOut(originalOut);
            app.showStart();
        }
    }

}

package game.engine;
import java.util.ArrayList;
import game.engine.cards.Card;
import game.engine.cells.Cell;
import game.engine.monsters.Monster;

public class Board {

	private final Cell[][] boardCells;

	private static ArrayList<Monster> stationedMonsters;

	private static ArrayList<Card> originalCards;

	public static ArrayList<Card> cards;

	public Board(ArrayList<Card> readCards) {

	    boardCells=new Cell[Constants.BOARD_ROWS][Constants.BOARD_COLS];

	    stationedMonsters = new ArrayList<>();

	    cards = new ArrayList<>();

	    originalCards = new ArrayList<>();

	    originalCards.addAll(readCards);
	}

	public Cell[][] getBoardCells() {
		return boardCells;
	}

	public static ArrayList<Monster> getStationedMonsters() {
		return stationedMonsters;
	}
	public static ArrayList<Card> readCards() { 
		return cards; 
	} 

	public static void setStationedMonsters(ArrayList<Monster> stationedMonsters) {
		Board.stationedMonsters = stationedMonsters;
	}

	public static void setCards(ArrayList<Card> cards) {
		Board.cards =new ArrayList<>(cards);
	}

	public static ArrayList<Card> getOriginalCards() {
	    return originalCards;
	}

	

}
package game.engine.exceptions;

public class InvalidMoveException extends GameActionException {
	public static final String MSG = "Invalid move attempted";
	public InvalidMoveException(){
		super();
	}
	public InvalidMoveException(String message){
		super(message);
	}

}

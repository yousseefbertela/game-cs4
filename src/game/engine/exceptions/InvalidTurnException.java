package game.engine.exceptions;

public class InvalidTurnException extends Exception {
	public static final String MSG="Action done on wrong turn";
	public InvalidTurnException(){
		super();
	}
	public InvalidTurnException(String message){
		super(message);
	}

}

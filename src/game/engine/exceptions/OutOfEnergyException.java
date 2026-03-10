package game.engine.exceptions;

public class OutOfEnergyException extends GameActionException {
	public static String MSG= "Not Enough Energy for Power Up";
	public OutOfEnergyException(){
		super();
	}
	public OutOfEnergyException(String message){
		super(message);
	}
}

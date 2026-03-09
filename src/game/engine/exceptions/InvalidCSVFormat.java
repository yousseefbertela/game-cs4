package game.engine.exceptions;

public class InvalidCSVFormat extends Exception {
	public static final String MSG="Invalid input detected while reading csv file, input = \n";
	private String inputLine;
	public InvalidCSVFormat(String inputLine) {
	    super(MSG + inputLine);
	    this.inputLine = inputLine;
	}
	public InvalidCSVFormat(String message, String inputLine) {
	    super(message);
	    this.inputLine = inputLine;
	}
	

}

package game.engine.cells;

public class ConveyerBelt extends TransportCell {
	public ConveyerBelt(String name,int effect){
		super(name,Math.abs(effect));
	}
}

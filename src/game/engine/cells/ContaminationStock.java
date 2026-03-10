package game.engine.cells;

public class ContaminationStock extends TransportCell{
	public ContaminationStock(String name,int effect){
		super(name,-(Math.abs(effect)));
	}
}

package game.engine.monsters;
import game.engine.Role;
public class Dasher extends Monster {
	private int momentumTurns;
	public Dasher(String name, String description, Role role, int energy) {
		super(name,description,role,energy);
		this.momentumTurns=0;
	}
	public int getmomentumTurns(){
		return momentumTurns;
	}
	public void setmomentumTurns(int momentumTurns){
		if(momentumTurns<0){
			this.momentumTurns=0;
		}
		else{
			this.momentumTurns=momentumTurns;
		}
	}

}


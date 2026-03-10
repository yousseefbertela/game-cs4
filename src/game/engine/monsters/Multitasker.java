package game.engine.monsters;
import game.engine.Role;
public class Multitasker extends Monster {
	private int normalSpeedTurns;
	public Multitasker(String name,String description,Role role,int energy) {
		super(name,description,role,energy);
		this.normalSpeedTurns=0;
	}
	public int getnormalSpeedTurns(){
		return normalSpeedTurns;
	}
	public void setnormalSpeedTurns(int normalSpeedTurns){
		if(normalSpeedTurns<0){
			this.normalSpeedTurns=0;
		}
		else{
			this.normalSpeedTurns=normalSpeedTurns;
		}
	}

}

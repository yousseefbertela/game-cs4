package game.engine.monsters;
import game.engine.Role;
public abstract class Monster implements Comparable<Monster>{
	private final String name;
	private final String description;
	private Role role;
	private final Role originalRole;
	private int energy;
	private int position;
	private boolean frozen;
	private boolean shielded;
	private int confusionTurns;
	public Monster(String name, String description, Role originalRole, int energy){
		this.name=name;
		this.description=description;
		this.role=originalRole;
		this.originalRole=originalRole;
		this.energy=energy;
		this.position=0;
		this.frozen=false;
		this.shielded=false;
		this.confusionTurns=0;
	}
	public int compareTo(Monster o){
		if(this.position>o.position){
			return 1;
		}
		else{
			if(this.position==o.position){
				return 0;
			}
			else{
				return -1;
			}
		}
	}
}


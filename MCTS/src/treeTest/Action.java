package treeTest;

public class Action {
	
	/*For our first basic MCTS, the 'players' can rest, move, melee attack, or range attack. Note, these
	 * constants define the 'type', and there is a 1toN relationship of a player to legal actions.  For
	 * example, there could be many possible legal move, melee, range actions available in a given turn,
	 * but each action is defined by one of the codes below.
	 */

	
	int type;
	Position pos;			//Final destination after action taken	
	
	public Action(int type, int x, int y, int player) {
		this.type = type;
		this.pos = new Position(x,y,player);
	}
	
	//Copy constructor
	public Action(Action a) {
		this.type = a.type;
		this.pos = new Position(a.pos);
	}
}

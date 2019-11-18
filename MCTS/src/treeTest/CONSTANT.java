package treeTest;

public class CONSTANT {
	/*Constants for the action types allowed, the parent class will only have the 'Move'
	 * Action, but other examples below for consideration
	 */
	//public static final int REST_ACTION = 0;
	public static final int MOVE_ACTION = 1;
	//public static final int MELEE_ACTION = 2;
	//public static final int RANGE_ACTION = 3;
	public static final int AI_PLAYER = 1;
	public static final int HUMAN_PLAYER = 0;
	public static final boolean PARENT_NODE = false;
	public static final boolean CHILD_NODE = true;
	public static final int GAME_DRAW = 3;
	public static final int GAME_LOSS = 2;
	public static final int GAME_WIN = 1;
	public static final int GAME_OPEN = 0;
	
	private CONSTANT() {}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}

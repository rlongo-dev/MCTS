/*A generic class that provides the basic structure for any NxN board game we intend to play out
 * via AI options such as MinMax, MCTS, Tab Q Learning, etc
 */

package treeTest;

public class Game {
	String name;
	int	maxPlayers;		//maximum number of players allowed for this type of game
	int xDim;			//Dimensions of the board for this game 
	int yDim;
	Board board;
	int isOver;			//Using 1 as a win, 2 as a draw, and 0 as an open game
	
	public Game() {
	}
	
	public boolean IsOver() {
		//check for game ending conditions
		return false;
	}
}

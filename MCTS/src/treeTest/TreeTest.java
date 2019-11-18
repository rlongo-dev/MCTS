/*We can look at the use of AI for game play as two separate game instances.  In the first instance,
 * there is a real game being played between players.  But as they play out the game (and 'they' includes
 * non-human computer players) they are also playing out virtual instances of mini games in their 'mind' 
 * alongside the real game, for purposes of choosing next best action during play.  So there will be an
 * instance of the real game playing out while the MCTS tree plays out instances of the game down the various tree
 * paths
 */


package treeTest;

public class TreeTest {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TicTacToe TTT = new TicTacToe();
		//Test with AI as second player
		TTT.players[0] = CONSTANT.AI_PLAYER;
		//Main Game Loop
		while (TTT.isOver==CONSTANT.GAME_OPEN){
			TTT.PlayTurn();
		}
		//Tree newTree = new Tree();
		//newTree.GrowTree(5, 3);
	}
}

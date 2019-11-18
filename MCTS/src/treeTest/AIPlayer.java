package treeTest;
/*Decided to create a class that controls the activity of the AI Players. So when a move needs to be selected
 * for an AI turn, the AIPlayer will utilize whatever models available to make the best move. To do this, the AIPlayer
 * needs to play out the game in its 'brain', obviously without making any change to the current instance of the TicTacToe class.
 * So we'll instantiate a new Game/TicTacToe object for this virtual brain play out, along with a tree with a root node set
 * at the state of the current real game.  With an AIPlayer class we can then test options such as depth of branching,
 * number of iterations of tree play out, and even a 'dumbing' effect, where we can lower the optimization of the AIPlayer's
 * selection ability to simulate different levels of intelligent opponent.
 */
public class AIPlayer {
	
	public TicTacToe realTTT;		//The real game playing played between players	
	public TicTacToe aiTTT;			//A copy of the game the AI class can manipulate for play selection review; must be deep copy, no references
	public Tree tree;				
	public int pNumber;				//The number that was assigned by the active Game, i.e. tell me who I am
	public int cycles = 1000000;		//Number of cycles we're allowing before making a selection
	public int bDepth;				//How far down we'll allow AI to go in the tree before it needs to make a decision, not being used in TicTacToe
	public int rating;				//Some rating of the AI, arbitrary for now, may be used in the future to allow for a range of AI effectiveness
	
	public AIPlayer(int pNumber) {
		this.pNumber = pNumber;
	}
	
	public AIPlayer(TicTacToe realTTT) {
		this.realTTT = realTTT;
		this.aiTTT = new TicTacToe(realTTT); 	//using the copy constructor to deep copy, do NOT want to change the real game instance
		tree = new Tree(aiTTT);
	}

	/*SelectAction will act as the MCTS control loop each time AI is required to select an action on its turn. Control affectively
	 * passes to the AI instance while it build out a tree for the current game state, then after a set time or number of loop
	 * iterations, it will review best option among the children of the tree's rote node and pass this back to the real game object
	 * as it's selection action(move in case TTT)
	 */
	public Action SelectAction() {
		int isOver = 0;
		Action bestAction;
		for(int loop=0;loop<cycles; loop++) {		//for more complex games this could be time-limited loop instead
			//Need to reset the game to its original State, for now we'll just roll back TicTacToe object, but in future
			//would like to decouple the playouts from any TicTacToe instance, using the State of any node only
			this.aiTTT = new TicTacToe(realTTT);
			Node curNode = tree.root;
				while (!curNode.isTerminating) {
					curNode.ExpandNode();
					curNode = curNode.SelectRandomNode();
					bestAction = curNode.GetParentAction();
					isOver = aiTTT.PlayTurn(bestAction);
					if (isOver != CONSTANT.GAME_OPEN) {
						curNode.isTerminating = true;
					}
				}
			curNode.BackPropogate(isOver);
		}
		//Send back best Child Node's Action of the root after we sufficiently played out (looped) the game situation
		tree.root.GetChildUCBValues();
		//tree.DisplayTree();
		return tree.root.SelectBestAction(1);
	}
}

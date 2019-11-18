/*Properly defining what a 'state' should constitute will have importance in how a game will utilize
 * MCTS for AI purposes. To minimize the tree memory size, I have decided to not have each State object
 * maintain it's own snapshot of the board, but rather to simply include the 'Actions' that are playable
 * from a given Tree Node. Because a board can be a massive 2D array of positions, this makes sense.
 * But because we won't be creating a myriad of board instances to track the changes game, every play
 * from a node would potentially need to run through the game up to a given state in the tree to start
 * a play out from that node. I'm not completely satisfied with this, but it is a tradeoff between
 * time to play down to a node on any given tree path versus a massive tree with potentially thousands
 * of board instances.
 */


package treeTest;

import java.util.*;

public class State {
	
	//'numActs' are number of possible plays from this state, will correspond to # of child nodes
	//Future code will include 'actions' as a collection of move objects for a real game test
	int numActs;
	ArrayList<Action> actions;
	int currentPlayer;
	int noPlayers;
	int turn;
	
	public State(int currentPlayer, ArrayList<Action> actions,int turn) {
		this.currentPlayer = currentPlayer;
		this.actions = actions;
		this.numActs = actions.size();
		this.turn = turn;
	}

	public State(int currentPlayer, Board board,int turn) {
		this.currentPlayer = currentPlayer;
		this.actions = this.ExtractActions(board);
		this.numActs = actions.size();
		this.turn = turn;
	}
	
	public State(TicTacToe TTT) {
		this.currentPlayer = TTT.currentPlayer;
		this.noPlayers = TTT.maxPlayers;
		this.turn = TTT.turn;
		this.actions = this.ExtractActions(TTT.board);
		this.numActs = actions.size();
	}

	
	public State(State s) {
		this.currentPlayer = s.currentPlayer;
		this.noPlayers = s.noPlayers;
		this.turn = s.turn;
		this.actions = new ArrayList<Action>();
		for (int actn = 0; actn<s.getNumActs(); actn++) {
			this.actions.add(new Action(s.actions.get(actn)));
		}
		this.numActs = actions.size();
	}

	
	public ArrayList<Action> ExtractActions(Board b) {
		ArrayList<Action> actions = new ArrayList<Action>();
		/*Extract the open positions as possible actions. This works fine for games where the possible actions correspond
		 *only to moves/placement to open positions. For future versions of the code, we will need to consider that Actions include
		 *other possible options, which may not be a move/placement of a piece
		*/
		for (int x=0; x<b.xSize; x++) {
			for (int y=0; y<b.ySize; y++) {
				if (b.pos.get(x).get(y).posVal == 0) {			//We only want the open board positions, i.e. position value is 0
					actions.add(new Action(CONSTANT.MOVE_ACTION,x,y,this.currentPlayer));
				}
			}
		}
		return actions;
	}
	
	
//*******************************SETTERS AND GETTERS**********************************
	public long getNumActs() {
		return numActs;
	}

	public ArrayList<Action> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(int player) {
		this.currentPlayer = player;
	}

}

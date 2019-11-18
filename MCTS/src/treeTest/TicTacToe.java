package treeTest;
import java.io.*;
import java.util.*;

/*I want to keep all logic for a game inside the control of the game class itself.  We want AIPlayer
 * to maintain its own instance of the game (for purposes of AI play outs).  Since the logic to test game conditions (like win or loss)
 * is maintained in the game class, we'll be calling methods in the game class each time a node plays out. There would be
 * no further issue if we only played out one cycle of the game before the AIPlayer selected its move.  But in the case of MCTS, we'll
 * want to potentially play out thousands of cycles of the game at its current state.  That has me considering how to best 'reset'
 * the game object members back to root state to replay the game again for the next cycle. Or, as another option, we have the
 * Game class provide utility classes that takes actions and test conditions simply with the passing of decoupled objects of the board
 * or state objects.
 */

public class TicTacToe extends Game {
	Board scBoard;		//A parallel board with scored values, used later to test win conditions
	int currentPlayer;
	int turn;
	/*I attempted to instantiate the Scanner Object within the PlayerInput method, closing it in that method.
	 * However, apparently, this causes major issues with System.in.  I am reading online that once you 'close'
	 * System.in, it cannot be reopened, which probably answers why further console input was not being read
	 * once the method instance of the Scanner was deleted (as would be expected when the method terminated processing).
	 * So I'm putting Scanner at the class level to continue reading the Next element in the keyboard buffer.
	 * I suppose I could investigate how  'static' would prevent more than one instance of the Scanner being 
	 * instantiated if another instance of the class was created.
	*/
	Scanner input = new Scanner(System.in);
	int[] players;
	
	public TicTacToe() {
		maxPlayers = 2;
		players = new int[maxPlayers];
		for (int x=0;x<maxPlayers;x++) {
			if (x < 2) {
				players[x] = CONSTANT.HUMAN_PLAYER;		//For simplicity setting first player to the only human one for testing
			} else players[x] = CONSTANT.AI_PLAYER; 
		}
		xDim = 3;
		yDim = 3;
		board = new Board(xDim,yDim);
		scBoard = new Board(xDim,yDim);		//Test to use a scored board that maps to the main board
		isOver = CONSTANT.GAME_OPEN;
		turn = 1;
		currentPlayer = 1;
	}
	
	/*Copy Constructor TicTacToe - after researching shallow and deep copy concepts, I decided on copy constructors for the classes
	 * instead of trying serialization or overriding the Clone capabilities (appears that many programmers advised against
	 * even considering cloning).  However, to do a deep copy of TicTacToe, Board and Position needed their own copy
	 * constructors since they are not immutable, or they contain members that are not immutable objects.
	 */
	public TicTacToe(TicTacToe TTT) {
		maxPlayers = TTT.maxPlayers;
		players = TTT.players;
		xDim = TTT.xDim;
		yDim = TTT.yDim;
		board = new Board(TTT.board);
		scBoard = new Board(TTT.scBoard);		//Test to use a scored board that maps to the main board
		isOver = TTT.isOver;
		turn =TTT.turn;
		currentPlayer = TTT.currentPlayer;
	}
	
	/*Because we've decoupled Actions from the the Game/TicTacToe Class, we need a function to create the State
	/from the current game board. States are important as they hold a collection of open Actions which will directly
	/correspond to the possible child nodes */
	public State ExtractState() {
		ArrayList<Action> actions = new ArrayList<Action>();
		/*Extract the open positions as possible actions. This works fine for games where the possible actions correspond
		 *only to moves/placement to open positions. For future versions of the code, we will need to consider that Actions include
		 *other possible options, which may not be a move/placement of a piece
		*/
		for (int x=0; x<this.board.xSize; x++) {
			for (int y=0; y<this.board.ySize; y++) {
				if (this.board.pos.get(x).get(y).posVal == 0) {			//We only want the open board positions, i.e. position value is 0
					actions.add(new Action(CONSTANT.MOVE_ACTION,x,y,this.currentPlayer));
				}
			}
		}
		return new State(this.currentPlayer,actions,this.turn);
	}
	
	
	
	/*Plays a single turn for the player passed
	 *if 'player' is AI we select Action using MCTS, otherwise get Action input from player
	 */
	public int PlayTurn(Action a) {

		Position p = new Position(a.pos);
		p.posVal = currentPlayer;
		isOver = TakeAction(p,CONSTANT.MOVE_ACTION, currentPlayer);
		//System.out.println("Turn:" + turn + " Player:" + currentPlayer);
		//board.DisplayBoard();
		//The calc below will return the next number in as sequence 1 to n  for value t in a series
		currentPlayer = this.NextPlayer();
		turn++;
		return isOver;
	}
	
	public void PlayTurn() {
		Position p = new Position();	
		//TakeAction based on player input (MCTS selection, AI player, or Human input)
		switch (players[currentPlayer-1]) {
			case CONSTANT.HUMAN_PLAYER:
				GetPlayerInput(p);
				break;
			case CONSTANT.AI_PLAYER:
				AIPlayer ai = new AIPlayer(this);
				Action action = ai.SelectAction();
				p = action.pos;
				break;
		}
		p.posVal = currentPlayer;
		isOver = TakeAction(p,CONSTANT.MOVE_ACTION, currentPlayer);
		board.DisplayBoard();
		scBoard.DisplayBoard();
		if (isOver != CONSTANT.GAME_OPEN) { PrintGameStatus(isOver); }
		currentPlayer = NextPlayer();
		turn++;
	}
	
	
	/*Take an action from a current game state and update the board(s) for the game. I am not
	 * checking the legality of the action here.  That logic will be elsewhere for this version */
	public int TakeAction(Action a, int player) {
		switch (a.type) {
		case CONSTANT.MOVE_ACTION:
			//Use Action's Position coordinates to index Board Positions for update
			board.UpdateBoard(a.pos);
			scBoard.UpdateBoard(a.pos, 10^(2-player)); //10 indicates player 1 position, 1 for player 2 position
			break;
		default:
			break;
		}
		return IsOver(a.pos, player);
	}
	
	public int TakeAction(Position p, int action, int player) {
		switch (action) {
		case CONSTANT.MOVE_ACTION:
			board.UpdateBoard(p);
			scBoard.UpdateBoard(p, (int) Math.pow(10, 2-p.posVal)); //10 indicates player 1 position, 1 for player 2 position
			break;
		default:
			break;
		}
		return IsOver(p, player);
	}
	
	public int NextPlayer() {
		int nextTurn = turn+1;	//I'm changing value before using equation below to keep readability of original algorithm
		//The calc below will return the next number in as sequence 1 to n  for value t in a series
		int nxtPlayer = ((nextTurn-1) % maxPlayers)+1; 
		return nxtPlayer;
	}
	
	public static int NextPlayer(int turn, int noPlayers) {
		int nxtPlayer = ((turn-1) % noPlayers)+1;
		return nxtPlayer;
	}
	
	/*Check only the row and column where the move was made and returning immediately if win is detected at any point
	 * to minimize processing.  Only then check for diagonals, returning right away if true. Again,
	 * looking to reduce the amount of conditional testing we have to do each Action.
	 */
	public int IsOver(Position p, int player) {					//not using flag for this version
		int score = 0;
		int win = 3 *(int)Math.pow(10,2-player);	//30 is a player 1 win score, 3 is a player 2 win score
		//check row first
		for (int y=0; y<xDim; y++) {
			score += scBoard.pos.get(p.xP).get(y).posVal;
			if (score == win) {
				return 	CONSTANT.GAME_WIN;
			}			
		}
		//check column
		score = 0;
		for (int x=0; x<xDim; x++) {
			score += scBoard.pos.get(x).get(p.yP).posVal;
			if (score == win) {
				return CONSTANT.GAME_WIN;
			}
		}				
		//Test if move was made in the first diagonal
		score = 0;
		if (p.xP==p.yP) {	
			score += scBoard.pos.get(0).get(0).posVal;
			score += scBoard.pos.get(1).get(1).posVal;
			score += scBoard.pos.get(2).get(2).posVal;
			if (score == win) {
				return CONSTANT.GAME_WIN;
			}
		}
		//Test if move made in second diagonal.
		score = 0;
		if (p.xP+p.yP==2) {	//Coordinates always add to 2 for this diagonal
			score += scBoard.pos.get(0).get(2).posVal;
			score += scBoard.pos.get(1).get(1).posVal;
			score += scBoard.pos.get(2).get(0).posVal;
			if (score == win) {
				return CONSTANT.GAME_WIN;
			}
		}
		
		//A zero in any position gives us an open board
		for (int x=0; x<3; x++) {
			for (int y=0;y<3; y++) {
					if(scBoard.pos.get(x).get(y).posVal ==0) {						
					return CONSTANT.GAME_OPEN;
					}
				}
			}			
		//If there was no win and the board is filled (no zeros) then we have a draw
		return CONSTANT.GAME_DRAW;
	}
	
	//Because Java passes objects by reference the Position object we passed will be updated, no need to return
	//a Position object
	public void GetPlayerInput(Position p) {
		System.out.print("Enter Row: ");
		
		if(input.hasNextLine() )
			p.xP = input.nextInt(); // if there is another number  
		else 
			p.xP = 0; // nothing added in the input 
		
		System.out.print("Enter Col: ");
		if(input.hasNextLine() )
			p.yP = input.nextInt(); // if there is another number  
		else 
			p.yP = 0; // nothing added in the input 
	}
	
	public void PrintGameStatus(int status) {
		switch (status) {
		case CONSTANT.GAME_DRAW:
			System.out.println("Game ends in a draw.");
			break;
		case CONSTANT.GAME_WIN:
			System.out.println("Player " + currentPlayer + " wins!");
			break;
		case CONSTANT.GAME_OPEN:
			System.out.println("Game still open.");
			break;
		}
	}
	
}

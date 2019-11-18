package treeTest;

import java.util.*;
/*Plan to encapsulate MCTS node selection functionality into the node.  The parent node
 * will be responsible to returning the node of choice during a game play out.
 */

public class Node {
	
	String name;
	//Each Node maintains access to it's own children
	Node parent;
	ArrayList<Node> children = null;
	boolean childVisited = false; //set to true only after at least one child has been played
	long visits = 0;
	long wins = 0;				  //this the wins for the CURRENT player at this point in the game tree	
	double ucbValue;	
	boolean isTerminating = false;
	State state;
	static double alpha = 2*Math.sqrt(2);	//UCB constant, sqrt(2) is default from readings
	//static double alpha = Math.sqrt(2);	//UCB constant, sqrt(2) is default from readings
	
	
	public Node(String name, Node parent, boolean isTerminating) {
		this.children = new ArrayList<>();
		this.name = name;
		this.parent = parent;
		this.isTerminating = isTerminating;
		//Every node starts with value = infinity which ensures it will be chosen at least once
		ucbValue = Double.POSITIVE_INFINITY;
		//System.out.println(name + " " + (isTerminating?"Leaf":"Branch"));
	}
	
	public Node(String name, Node parent, boolean isTerminating, State state) {
		this.children = new ArrayList<>();
		this.name = name;
		this.parent = parent;
		//need to bring over parent visit stats
		this.isTerminating = isTerminating;
		//Every node starts with value = infinity which ensures it will be chosen at least once
		ucbValue = Double.POSITIVE_INFINITY;
		this.state = state;
		//System.out.println(name + " " + (isTerminating?"Leaf":"Branch"));
	}
	
	
	public Node(String name, Node parent, boolean isTerminating, TicTacToe TTT) {
		this.children = new ArrayList<>();
		this.name = name;
		this.parent = parent;
		this.isTerminating = isTerminating;
		this.state = new State(TTT);
		//Every node starts with value = infinity which ensures it will be chosen at least once
		ucbValue = Double.POSITIVE_INFINITY;
	}
		
	/*SelectNode Method will iterate through child nodes, calculating node value and returning
	 * node with highest value as selected Node.  UCB1 will be used for initial tests. Based on several
	 * white papers I've read, nodes that have no visits will start with an initial value of infinity,
	 * however that needs to be represented.  This ensures that every node will be played at least once.
	 * This function assumes child nodes have been expanded.
	 */
	
	//For debug purposes, including a random Node selector
	public Node SelectRandomNode() {
		Random rand = new Random();
		this.childVisited = true;
		return children.get(rand.nextInt(children.size()));
	}
	
	
	public Node SelectNode() {
				
		double maxVal = 0;
		Node selNode = null;
		Random rand = new Random();
		//ArrayList<Integer> notVisited = new ArrayList<Integer>();
		
		if (childVisited) {
			for (Node cNode: children){
				if (maxVal <= cNode.ucbValue) {
					/*
					if (n.ucbValue == Double.POSITIVE_INFINITY) {	//Only unvisited child nodes have this value
						notVisited.add(children.indexOf(n));		//so add it to the unVisited ArrayList
					}
					*/
					maxVal = cNode.ucbValue;
					selNode = cNode;
				}
			}
		} else {
			childVisited = true;
			selNode = children.get(rand.nextInt(children.size()));	//none visited just randomly choose one
		}
		//if (!notVisited.isEmpty()) {
		//	selNode = children.get(rand.nextInt(notVisited.size()));
		//}
		//System.out.println("Node " + selNode.name + " selected.");
		return selNode;
	}	
	
	public void AddChild(Node child) {
		children.add(child);
	}
	
	//As for SelectNode, ExpandNode assumes we have code elsewhere to ensure we don't call this method
	//for a terminating(leaf) node
	public void ExpandNode() {
		if(children.isEmpty()) {	//expansion only needed no child nodes have been inserted
			String childName;
			int childTurn = state.turn+1;
			int nextPlayer = TicTacToe.NextPlayer(childTurn, state.noPlayers);
			
			/*Iterate through current Parent Node's State:action. Create a copy of the parent state for each action, remove the Action
			*not included for the child state, then instantiate the child Node with this modified state. Each action corresponds
			*  to a child node under current node.
			*/
			for (int actn = 0; actn<state.getNumActs(); actn++) {
				State childState = new State(state);
				childState.turn = childTurn;
				childState.currentPlayer = nextPlayer;
				childState.actions.remove(actn);
				childName = this.name + "." + (actn+1);			//parent.[child number]... each tree level add new segment to nanme
				childState.numActs = childState.actions.size();
				children.add(new Node(childName,this,false,childState));
			}
		}
	}
	
	/* Critical step in the Tree Model (MCTS, MinMax, etc).  We want to update the node's information
	 * AFTER we played to a terminating node.  Value for node will be updated at this stage as well,
	 * then successively update each parent node up the tree until we reach the root node;
	 */
	public void BackPropogate(int isOver) {

			if (isOver== CONSTANT.GAME_WIN) wins++;
			visits++;
				//The root node is never considered for selection (it's the node of origin) so
				//no need to calculate it's value. However, we do need to keep record of its visits
				if (parent !=null) {
					long pVisits = parent.visits+1;	//considering we'll increment parent as we back propagate
					ucbValue = ((double)wins/(double)visits) + alpha * (Math.sqrt(Math.log(pVisits) /(visits)));
					//System.out.println("BP: "+ name + " visits: "+ visits + " ucb: " + ucbValue);
				}
			//Because tree node levels alternate between  players, a win at one level is actually a loss at one above/below it, so we alternate
			if (parent !=null) {
				switch(isOver) {
					case CONSTANT.GAME_WIN:
						parent.BackPropogate(CONSTANT.GAME_LOSS);
						break;
					case CONSTANT.GAME_LOSS:
						parent.BackPropogate(CONSTANT.GAME_WIN);
						break;
					case CONSTANT.GAME_DRAW:
						parent.BackPropogate(CONSTANT.GAME_DRAW);
						break;
				}
			}
	}
	/*I chose to not have Node contain a reference to the Action that spawns it. That may change in the future
	 * but for now, we will need to reference the parent Node's array of Actions to determine the Action path
	 * that spawns this current Node. The Node's name is the key to this, as the final integer after the
	 * last '.' delimiter indicates its relative index in the parent Node's children array.  We'll strip that
	 * text, convert to int and use it to grab the Action that originated this current Node.
	 */
	public Action GetParentAction() {
		int startPos = this.name.lastIndexOf('.');
		if (startPos != -1) {
			String subString = this.name.substring(startPos+1);
			int index = (Integer.parseInt(subString))-1;	//using 1-based index for node names, subtract 1 to convert to zero-based
			return this.parent.state.actions.get(index);
		}
		return null;
	}

	public void PrintNode() {
		if (parent != null) {
			Position pos = GetParentAction().pos;
			System.out.print(name + ":" + pos.xP + "," + pos.yP + " ");
		}
	}
	
	public Action SelectBestAction(int method) {
		double maxScore = 0;
		double vFactor = .5;		//constant used for tuning visits as part of the whole
		double cScore = 0;
		int cIndex = 0;
		int bestIndex = 0;
		switch(method) {
		case 0:
				bestIndex = this.children.indexOf(this.SelectNode());
				return this.state.actions.get(bestIndex);
		case 1:
				for(Node n:children) {
					cScore = (double)n.wins/(double)n.visits * (vFactor*(double)n.visits/(double)visits);
					System.out.println(n.name + " : " + cScore);
					if (maxScore < cScore) {
						maxScore = cScore;
						bestIndex=cIndex;
					}
					cIndex++;
				}
				return this.state.actions.get(bestIndex);
		}
		return null;
	}
	
	public void GetChildUCBValues() {
		for (Node cNode: children) {
			System.out.println(cNode.name+":"+ CONSTANT.round((double)cNode.ucbValue,(int)4)+ " w: "+cNode.wins+" v: " + cNode.visits);
		}
	}
	
}

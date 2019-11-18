/*This class holds a copy  root node and functions for manipulating and traversing the nodes in the tree.
 * Treating the code to keep the this Tree class as the access point to any node info.
 * Quoting online article: Game tree is a recursive data structure, therefore after choosing the best move you end up
 * in a child node which is in fact a root node for its subtree. Therefore you can think of a game as a sequence of
 * 'the most promising next move' problems represented by a game tree every time (with different root node).
 * Very often in practice, you do not have to remember the path leading to your current state, as it is not a concern
 * in a current game state."
 */
package treeTest;

import java.util.*;

public class Tree {
	public Node root = null;
	static final boolean LEAF = true;
	static final boolean NOTLEAF = false;
	private int minBranches = 2;
		
	public Tree() {
		root = new Node("1",null,NOTLEAF);
	}
	
	//We pass Tree a game, using the current instance for the game as the 'root' situation for analysis
	public Tree (TicTacToe TTT) {
		root = new Node("1",null,NOTLEAF,TTT);
		}
	
	public void DisplayTree() {
		//  Node curParent = root;
		int level = 0;
		boolean childExists = true;
		ArrayList<ArrayList<Node>> treeNodes = new ArrayList<ArrayList<Node>>();
		treeNodes.add(new ArrayList<Node>());
		treeNodes.get(0).add(0,root);	//level 0 node is rootNode
		
		while(childExists) {
			childExists = false;	//don't set to true again unless we grab children at current level
			for(Node pNode:treeNodes.get(level)) {
				//Add it's children to the next level in the array
				if (!pNode.children.isEmpty()) {
					childExists = true;
					if (treeNodes.size()<level+2) {				//add to compensate for zero based index and need for additional level
						treeNodes.add(new ArrayList<Node>());
					}
					for (Node cNode:pNode.children) {
						treeNodes.get(level+1).add(cNode);
					}
				}
			}
			level++;
		}
		for (int x=0; x<3;x++) {
			for (Node pNode:treeNodes.get(x)) {
				pNode.PrintNode();
			}
			System.out.println();
		}
					
	}
	
}


	/*Below is some sample code to instantiate a random tree give a max depth and max branches per node 
	To build out tree we need to start at root, create it's children
	We then iterate through each child node and create it's own child nodes
	Iteration terminates when we'e reached lowest tree level (depth)
	For next steps in tests, Node class will contain functionality to expand it's children
	**********************************************************	
	public void GrowTree(int depth, int maxBranches) {

		ArrayList<Node> curLevelNodes = new ArrayList<Node>();
		ArrayList<Node> tempList = new ArrayList<Node>();
		
		root = new Node("1",null,NOTLEAF);
		Node curNode = root;
		int level = 2;							 //starting at level below root
		curLevelNodes = GrowBranches(curNode,level,maxBranches, NOTLEAF); //this creates first child nodes below root
		
		for (int d = 3; d<=depth; d++) {			
			for (Node n: curLevelNodes) {
				tempList.addAll(GrowBranches(n,d,maxBranches,(d==depth?LEAF:NOTLEAF)));	//Grab all nodes for next level branching
			}
			curLevelNodes.removeAll(curLevelNodes);
			curLevelNodes.addAll(tempList);
			tempList.removeAll(tempList);
		}
		
	}
	
	private ArrayList<Node> GrowBranches(Node parent, int level, int maxBranches, boolean isLeaf) {
		Random rand = new Random();
		ArrayList<Node> bNodes = new ArrayList<Node>();
		
		//rand.setSeed(seed);  May use this later to use same sequence each time
		int numBranches = rand.nextInt((maxBranches-minBranches) +1)+minBranches;		//create random number of branches 2 to n
		String name;
		System.out.println("Child nodes for " + parent.getName() + ": " + numBranches);
		
		for (int b = 1; b<numBranches+1; b++) {					
			name = parent.getName() + "." + b;			//parent.[child number] down the tree
			bNodes.add(new Node(name,parent,isLeaf));
		}
		parent.setChildren(bNodes);
		return bNodes;
	}
	*/


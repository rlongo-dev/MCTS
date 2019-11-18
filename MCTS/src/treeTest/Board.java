package treeTest;

import java.util.ArrayList;

public class Board {
	int ySize;		//
	int xSize;
	//private long zSize;  // for later use, when height will be considered
	ArrayList<ArrayList<Position>> pos;
	
	public Board() {
	}
	
	//Copy Constructor
	public Board(Board b) {
		this.ySize = b.ySize;
		this.xSize = b.xSize;
		this.pos = new ArrayList<ArrayList<Position>>();
		SetBoard(b);
	}
	
	public Board(int x, int y) {
		this.xSize = x;
		this.ySize = y;
		this.pos = new ArrayList<ArrayList<Position>>();
		SetBoard(0);
	}
	
	public Board(int x, int y, int val) {
		this.xSize = x;
		this.ySize = y;
		this.pos = new ArrayList<ArrayList<Position>>();
		SetBoard(val);
	}
	
	public void SetBoard(int val) {
		for (int x=0; x<xSize; x++) {
			pos.add(new ArrayList<Position>());	//Adds new 'row' to the board
			for (int y=0; y<ySize; y++) {
				pos.get(x).add(y,new Position(x,y,val));//now we add nth column to row created above
			}
		}
	}
	
	/*An override of SetBoard required to create a copy constructor for the class where we
	 * use an existing board's values to set our new board. 
	 */
	public void SetBoard(Board b) {
		for (int x=0; x<xSize; x++) {
			pos.add(new ArrayList<Position>());	//Adds new 'row' to the board
			for (int y=0; y<ySize; y++) {
				pos.get(x).add(y,new Position(b.pos.get(x).get(y)));//Call Position copy constructor to add deep copy of Position
			}
		}
	}
	
	
	public void UpdateBoard(Position p) {
		pos.get(p.xP).get(p.yP).posVal = p.posVal;
	}
	
	//An overload for when we want to explicitly set values outside Position members
	public void UpdateBoard(Position p, int value) {
		pos.get(p.xP).get(p.yP).posVal = value;
	}
	
	public void DisplayBoard() {
		for (int x=0;x<xSize;x++) {
			for (int y=0;y<ySize; y++) {
				//Position Value is a float, but TTT uses integer vals only so downcasting
				System.out.print("["+ (int)this.pos.get(x).get(y).posVal + "]");
			}
			System.out.println();
		}
		System.out.println();
	}
}

package treeTest;

public class Position {
	int xP;
	int yP;
	int posVal;			//may have multiple uses, but by default could hold ID of current player
	
	
	public Position() {
		xP = -1;
		yP = -1;
		posVal = -1;
	}
	
	//Copy constructor
	public Position(Position p) {
		this.xP = p.xP;
		this.yP = p.yP;
		this.posVal = p.posVal;
	}
	
	public Position(int x, int y, int posVal) {
		super();
		this.xP = x;
		this.yP = y;
		this.posVal = posVal;
	}
	
	
	public Position(int posVal) {
		super();
		this.posVal = posVal;
	}
	
//*******************************SETTERS AND GETTERS**********************************
	public long getxPos() {
		return xP;
	}
	public void setxPos(int xPos) {
		this.xP = xPos;
	}
	public long getyPos() {
		return yP;
	}
	public void setyPos(int yPos) {
		this.yP = yPos;
	}
	public float getPosVal() {
		return posVal;
	}
	public void setPosVal(int posVal) {
		this.posVal = posVal;
	}


}

package pieces;

import java.awt.Point;
import java.util.List;

import jade.util.leap.Serializable;

public abstract class Node implements Serializable {

	private Piece piece;
	
	private int x;
	
	private int y;
	
	private boolean debugEnabled = false;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Point getPoint() {
		return new Point(x,y);
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	public abstract boolean contains(Point p, int radius);
	
	public boolean equals(Object o) {
		boolean equals = false;
		if (o != null && o.getClass() == this.getClass()) {
			if (((Node)o).getX() == this.getX() && ((Node)o).getY() == this.getY()) {
				equals = true;
			}
		}
		return equals;
	}
	
	public abstract List<StreetNode> getAdjecentStreet();
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	public boolean getDebug () {
		return debugEnabled;
	}
	
	public void setDebug (boolean debug) {
		debugEnabled = debug; 
	}
}
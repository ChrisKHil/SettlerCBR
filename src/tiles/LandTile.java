package tiles;

import java.awt.Point;
import java.awt.Polygon;

import enums.LandType;
import enums.TilePart;

public class LandTile extends Tile {
	
	private int number;
	
	private LandType type;
	
	private boolean containsThieves;
	
	private Polygon poly;
	
	private int size;
	
	public LandTile(int x, int y, int size, TilePart leftOut) {
		super(x, y);
		poly = new Polygon();
		this.size = size;
		createPolygon(leftOut);
	}
	
	public LandTile(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int x5, int y5, int x6, int y6) {
		super(x1,  y4 + ((y1 - y4)/2));
		poly = new Polygon();
		poly.addPoint(x1, y1);
		poly.addPoint(x2, y2);
		poly.addPoint(x3, y3);
		poly.addPoint(x4, y4);
		poly.addPoint(x5, y5);
		poly.addPoint(x6, y6);
	}
	
	private void createPolygon(TilePart leftOut) {
		int tempCounter = 0;
		int[] leftOutInt = getLeftOut(leftOut);
		
		for (int i = 0; i < 6; i++){
			System.out.println("X: " + (this.getX() + size * Math.sin(i * 2 * Math.PI / 6)) + " Y: " + (this.getY() + size * Math.cos(i * 2 * Math.PI / 6)));
			int xTemp = (int) (this.getX() + size * Math.sin(i * 2 * Math.PI / 6));
			int yTemp = (int) (this.getY() + size * Math.cos(i * 2 * Math.PI / 6));
			if (tempCounter !=  leftOutInt[0] && tempCounter != leftOutInt[1]) {
				poly.addPoint(xTemp, yTemp);	
			}
			tempCounter++;
		}
	}
	
	private int[] getLeftOut(TilePart part) {
		int[] temp = {6,6};
		switch(part) {
			case LEFT:
				temp[0] = 4;
				temp[1] = 5;
				break;
			case RIGHT:
				temp[0] = 1;
				temp[1] = 2;
				break;
			case ULEFT:
				temp[0] = 0;
				temp[1] = 5;
				break;
			case URIGHT:
				temp[0] = 0;
				temp[1] = 1;
				break;
			case OLEFT:
				temp[0] = 3;
				temp[1] = 4;
				break;
			case ORIGHT:
				temp[0] = 2;
				temp[1] = 3;
				break;
			default:
				break;
		}
		return temp;
	}
	
	@Override
	public Polygon drawTile() {
		return poly;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public LandType getType() {
		return type;
	}
	
	public void setType(LandType type) {
		this.type = type;
	}
	
	public void setContainsThieves(boolean containsThieves) {
		this.containsThieves = containsThieves;
		
	}
	
	public boolean getContainsThieves() {
		return containsThieves;
	}

	public int getSize() {
		return size;
	}
	public void addCoordinate(int x, int y) {
		poly.addPoint(x, y);
	}
	
	public void initImage () {
		switch (this.type) {
		case LUMBER:
			this.setImage(Util.ImageUtils.loadImage("ressources/woodHex.gif"));
			break;
		case CORN:
			this.setImage(Util.ImageUtils.loadImage("ressources/wheatHex.gif"));
			break;
		case STONE:
			this.setImage(Util.ImageUtils.loadImage("ressources/oreHex.gif"));
			break;
		case CLAY:
			this.setImage(Util.ImageUtils.loadImage("ressources/clayHex.gif"));
			break;
		case WHOOL:
			this.setImage(Util.ImageUtils.loadImage("ressources/sheepHex.gif"));
			break;
		case DESERT:
			this.setImage(Util.ImageUtils.loadImage("ressources/desertHex.gif"));
		default:
			break;
		}
	}
	
	public boolean isInFiled(int x, int y) {
		boolean isInFiled = poly.contains(new Point(x,y));
		return isInFiled;
	}
}

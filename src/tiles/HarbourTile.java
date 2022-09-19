package tiles;

import java.awt.Point;
import java.awt.Polygon;

import Util.DistanceUtils;
import enums.HarbourType;
import enums.TilePart;
import pieces.CityNode;

public class HarbourTile extends Tile{

	private Polygon poly;
	
	private Point[] harbourPoints;
	
	private HarbourType harbourType;
	
	private TilePart orientation;  
	
	public HarbourTile(int x1, int y1, int x2, int y2, int width, int tileSize, TilePart type, HarbourType harbourType) {
		super(x1, y1 - tileSize);
		poly = new Polygon();
		this.orientation = type;
		this.harbourType = harbourType;
		harbourPoints = new Point[2];
		createHarbourTile(x1, y1, x2, y2, width, tileSize, type);
		this.setImage(Util.ImageUtils.loadImage("ressources/waterHex.gif"));
	}
	
	private void createHarbourTile (int x1, int y1, int x2, int y2, int width, int tileSize, TilePart type) {
		switch(type) {
			case LEFT:
				createHarbourTileLeft(x1, y1, x2, y2, width, tileSize);
				break;
			case RIGHT:
				createHarbourTileRight(x1, y1, x2, y2, width, tileSize);
				break;
			case ULEFT:
				createHarbourTileULeft(x1, y1, x2, y2, width, tileSize);
				break;
			case URIGHT:
				createHarbourTileURight(x1, y1, x2, y2, width, tileSize);
				break;
			case OLEFT:
				createHarbourTileOLeft(x1, y1, x2, y2, width, tileSize);
				break;
			case ORIGHT:
				createHarbourTileORight(x1, y1, x2, y2, width, tileSize);
				break;
			case TOP:
				createHarbourTileTop(x1, y1, x2, y2, width, tileSize);
				break;
			case BOTTOM:
				createHarbourTileBottom(x1, y1, x2, y2, width, tileSize);
				break;
			case COMPLETEOL:
				createHarbourTileCompleteOL(x1, y1, x2, y2, width, tileSize);
				break;
			case COMPLETEOR:
				createHarbourTileCompleteOR(x1, y1, x2, y2, width, tileSize);
				break;
			case COMPLETEUL:
				createHarbourTileCompleteUL(x1, y1, x2, y2, width, tileSize);
				break;
			case COMPLETEUR:
				createHarbourTileCompleteUR(x1, y1, x2, y2, width, tileSize);
				break;
			default:
				break;
		}
	}
	
	private void createHarbourTileTop(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1 , y1);
		poly.addPoint(x2, y2);
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x2 - width, y2 - tileSize);
		poly.addPoint(x2 - width, y2);
		addHarbourPoints(x1, y1, x2 - width, y2);
	}

	private void createHarbourTileBottom(int x1, int y1, int x2, int y2, int width, int tileSize) {
		System.out.println("Bottom:asd" + y1 + " Y2: " + y2);
		poly.addPoint(x2, y2);
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x1, (y1 - 2* tileSize));
		poly.addPoint(x2 - width, y2 - tileSize);
		poly.addPoint(x2 - width, y2);
		addHarbourPoints(x1, y1 - 2 * tileSize, x2 - width, y2 - tileSize);
	}

	private void createHarbourTileOLeft(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1 , y1);
		poly.addPoint(x2, y2);
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x2 - width, y2);
		addHarbourPoints(x1, y1, x2, y2);
	}
	
	private void createHarbourTileORight(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1 , y1);
		poly.addPoint(x2, y2);
		poly.addPoint(x2 - width, y2 - tileSize);
		poly.addPoint(x2 - width, y2);
	}
	
	private void createHarbourTileURight(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x1 , y1 - 2 * tileSize);;
		poly.addPoint(x2 - width, y2 - tileSize);
		poly.addPoint(x2 - width, y2);
	}
	
	private void createHarbourTileLeft(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1 , y1);
		poly.addPoint(x2, y2);
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x1, y1 - 2 * tileSize);
	}
	
	private void createHarbourTileULeft(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x2, y2);
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x1 , y1 - 2 * tileSize);;
		poly.addPoint(x2 - width, y2 - tileSize);
		addHarbourPoints(x2, y2 - tileSize, x1, y1 - 2 * tileSize);
	}
	
	private void createHarbourTileCompleteOL(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1 , y1);
		poly.addPoint(x2, y2);
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x1, y1 - 2 * tileSize);
		poly.addPoint(x2 - width, y2);
		addHarbourPoints(x2, y2, x2, y2 - tileSize);
	}
	
	private void createHarbourTileCompleteUL(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1 , y1);
		poly.addPoint(x2, y2);
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x1, y1 - 2 * tileSize);
		poly.addPoint(x2 - width, y2 - tileSize);
		addHarbourPoints(x2, y2, x2, y2 - tileSize);
	}
	private void createHarbourTileCompleteOR(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1 , y1);
		poly.addPoint(x2, y2);
		poly.addPoint(x1, y1 - 2 * tileSize);
		poly.addPoint(x2 - width, y2 - tileSize);
		poly.addPoint(x2 - width, y2);
		addHarbourPoints(x1, y1, x2 - width, y2);
	}
	private void createHarbourTileCompleteUR(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1 , y1);
		poly.addPoint(x2, y2 - tileSize);
		poly.addPoint(x1, y1 - 2 * tileSize);
		poly.addPoint(x2 - width, y2 - tileSize);
		poly.addPoint(x2 - width, y2);
		addHarbourPoints(x1, y1 - 2 * tileSize, x2 - width, y2 - tileSize);
	}

	private void createHarbourTileRight(int x1, int y1, int x2, int y2, int width, int tileSize) {
		poly.addPoint(x1, y1);
		poly.addPoint(x1, y1 - 2 * tileSize);
		poly.addPoint(x2- width, y2 - tileSize);
		poly.addPoint(x2 - width, y2);
		addHarbourPoints(x2- width, y2 - tileSize, x2- width, y2);
	}
	
	private void addHarbourPoints(int x1, int y1, int x2, int y2) {
		harbourPoints[0] = new Point(x1, y1);
		harbourPoints[1] = new Point(x2, y2);
	}
	
	@Override
	public Polygon drawTile() {
		return poly;
	}
	
	public HarbourType getType() {
		return harbourType;
	}
	
	public void setHarbourType (HarbourType type) {
		this.harbourType = type;
	}
	
	public void initImage () {
		this.setImage(Util.ImageUtils.loadImage("ressources/black_bishop.png"));
	}
	
	public boolean containsCity (CityNode city, int radius) {
		boolean contains = false;
		for (int i = 0; i < harbourPoints.length; i++) {
			if (harbourType != HarbourType.NONE) {
				if (DistanceUtils.contains(harbourPoints[i].x, city.getX(), harbourPoints[i].y, city.getY(), radius)) {
					contains = true;
				}
			}
		}
		
		return contains;
	}
	
	public TilePart getOrientation() {
		return orientation;
	}
}

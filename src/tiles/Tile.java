package tiles;

import java.awt.Polygon;
import java.awt.image.BufferedImage;

import jade.util.leap.Serializable;

public abstract class Tile implements Serializable {

	private int x;
	
	private int y;
	
	private BufferedImage image;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public abstract Polygon drawTile();
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void initImage () {
		image = Util.ImageUtils.loadImage("ressources/white_bishop.png");
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
}

package pieces;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import Util.DistanceUtils;
import enums.StreetOrientation;
import player.Player;

public class StreetNode extends Node{
	private Polygon street;
	
	private Polygon clickStreet;
	
	private List<StreetNode> adjecentStreetNodes;
	
	public StreetNode(int x, int y) {
		super(x, y);
	}

	public StreetNode(int x, int y, StreetOrientation oreintation,int width, int height) {
		super(x, y);
		street = new Polygon();
		clickStreet = new Polygon();
		adjecentStreetNodes = new ArrayList<StreetNode>();
		int heightStreet = (int)(height * 0.1);
		if (oreintation == StreetOrientation.RIGHT) {
			//Width/2 and height *0.5 sould be a parallel roead for the whole line, reducing both equally should result in smaller roads
			street.addPoint(x + width/4 - heightStreet, (int)(y - heightStreet - height*0.25)); //top right
			street.addPoint(x - width/4 - heightStreet,(int)(y - heightStreet + height*0.25)); //top left 
			street.addPoint(x - width/4 + heightStreet, (int)(y + heightStreet + height*0.25)); //bottom left
			street.addPoint(x + width/4 + heightStreet, (int)(y + heightStreet - height*0.25)); //bottom right
			//----The clickable Space-------
			clickStreet.addPoint(x + width/3 - heightStreet*2, (int)(y - heightStreet*2 - height*0.35)); //top right
			clickStreet.addPoint(x - width/3 - heightStreet*2,(int)(y - heightStreet*2+ height*0.35)); //top left 
			clickStreet.addPoint(x - width/3 + heightStreet*2, (int)(y + heightStreet*2 + height*0.35)); //bottom left
			clickStreet.addPoint(x + width/3 + heightStreet*2, (int)(y + heightStreet*2 - height*0.35)); //bottom right
		}
		if (oreintation == StreetOrientation.LEFT) {
			//Width/2 and height *0.5 sould be a parallel roead for the whole line, reducing both equally should result in smaller roads
			street.addPoint(x + width/4 + heightStreet, (int)(y - heightStreet + height*0.25)); //top right
			street.addPoint(x - width/4 + heightStreet,(int)(y - heightStreet - height*0.25)); //top left 
			street.addPoint(x - width/4 - heightStreet, (int)(y + heightStreet - height*0.25)); //bottom left
			street.addPoint(x + width/4 - heightStreet, (int)(y + heightStreet + height*0.25)); //bottom right
			//-----------------The clickable space-----------------
			clickStreet.addPoint(x + width/3 + heightStreet*2, (int)(y - heightStreet*2 + height*0.35)); //top right
			clickStreet.addPoint(x - width/3 + heightStreet*2,(int)(y - heightStreet*2 - height*0.35)); //top left 
			clickStreet.addPoint(x - width/3 - heightStreet*2, (int)(y + heightStreet*2 - height*0.35)); //bottom left
			clickStreet.addPoint(x + width/3 - heightStreet*2, (int)(y + heightStreet*2 + height*0.35)); //bottom right
		}
		if (oreintation == StreetOrientation.CENTER) {
			//Width/2 and height *0.5 sould be a parallel roead for the whole line, reducing both equally should result in smaller roads
			street.addPoint(x + heightStreet, (int)(y -height*0.50)); //top right
			street.addPoint(x - heightStreet,(int)(y - height*0.50)); //top left 
			street.addPoint(x - heightStreet, (int)(y + height*0.50)); //bottom left
			street.addPoint(x + heightStreet, (int)(y + height*0.50)); //bottom right
			//--------------------The clickable space -------------
			clickStreet.addPoint(x + heightStreet*2, (int)(y - height*0.70)); //top right
			clickStreet.addPoint(x - heightStreet*2,(int)(y - height*0.70)); //top left 
			clickStreet.addPoint(x - heightStreet*2, (int)(y + height*0.70)); //bottom left
			clickStreet.addPoint(x + heightStreet*2, (int)(y + height*0.70)); //bottom right
		}
	}
	/**
	 * Raidus is not used, ugly need to change.
	 */
	public boolean containsStreet(Point p) {
		return clickStreet.contains(p);
	}
	
	
	public Polygon getStreet() {
		return street;
	}
	
	public void addAjecentStreetNode(StreetNode s) {
		this.adjecentStreetNodes.add(s);
	}
	
	public List<StreetNode> getAdjecentStreet() {
		return adjecentStreetNodes;
	}
	/**
	 * This rule is functioning as intended without considering the towns for after the first turn there is a street at each city.
	 * These streets will cause this rule to be satisfied. In a way there is no way to build a street without another street adjacent.
	 * @param player
	 * @return
	 */
	public boolean getStreetConnectionRule (Player player) {
		boolean isOk = false;
		for (StreetNode s : adjecentStreetNodes) {
			if (s.getPiece() != null && s.getPiece().getPlayer().equals(player)) {
				isOk = true;
			}
		}
		return isOk;
	}
	
	@Override
	public boolean contains(Point p, int radius) {
		return DistanceUtils.contains(this.getX(), (int)p.getX(), this.getY(), (int)p.getY(), radius);
	}
}

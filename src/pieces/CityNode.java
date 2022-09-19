package pieces;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import Util.DistanceUtils;
import enums.HarbourType;
import enums.LandType;
import player.Player;

public class CityNode extends Node {

	int testNumber;
	
	List<LandType> adjecent;
	
	private HarbourType harbourType;
	
	private List<CityNode> adjecentCities;
	
	private List<StreetNode> adjecentStreets;
	
	private List<LandType> adjecentTypes;
	
	public CityNode(int x, int y, int testNumber) {
		super(x, y);
		harbourType = HarbourType.NONE;
		this.testNumber = testNumber;
	}

	public CityNode(int x, int y) {
		super(x, y);
		harbourType = HarbourType.NONE;
		adjecentCities = new ArrayList<CityNode>();
		adjecentStreets = new ArrayList<StreetNode>();
		adjecentTypes = new ArrayList<LandType>();
	}

	public void setHarbourType(HarbourType type) {
		this.harbourType = type;
	}
	
	public HarbourType getHarbourType() {
		return this.harbourType;
	}
	
	public boolean contains(Point p, int radius) {
		return DistanceUtils.contains(this.getX(), p.x, this.getY(), p.y, radius);
	}
	
	public int getTestNumber() {
		return testNumber;
	}
	
	public void addCityToAdjecentList(CityNode c) {
		adjecentCities.add(c);
	}
	
	public void addStreetToAdjecentList(StreetNode n) {
		adjecentStreets.add(n);
	}
	
	public List<CityNode> getAdjecentCities(){
		return adjecentCities;
	}
	
	public List<StreetNode> getAdjecentStreet(){
		return adjecentStreets;
	}
	
	public boolean distanceRule() {
		boolean isOk = true;
		for (CityNode c : adjecentCities) {
			if (c.getPiece() != null || this.getPiece() != null) {
				isOk = false;
			}
		}
		return isOk;
	}
	
	public void addAdjecnentLandType(LandType type) {
		this.adjecentTypes.add(type);
	}
	
	public List<LandType> getAdjecentTypes() {
		return adjecentTypes;
	}
	
	public boolean isAtStreet(Player player) {
		boolean isOk = false;
		for (StreetNode s : adjecentStreets) {
			//controll if there is a street close of the same player
			if (s.getPiece() != null && s.getPiece().getPlayer().equals(player)) {
				isOk = true;
			}
		}
		return isOk;
	}
}

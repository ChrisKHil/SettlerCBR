package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import pieces.CityNode;
import pieces.StreetNode;
import player.Player;

public class DijkstraReturnPair {
	
	private CityNode origin;
	
	private HashMap<CityNode, CityNode> predesces;
	
	private HashMap<CityNode, Integer> distances;
	
	public DijkstraReturnPair(CityNode origin, HashMap<CityNode, CityNode> predesces, HashMap<CityNode, Integer> distances) {
		this.origin = origin;
		this.predesces = predesces;
		this.distances = distances;
	}
	
	public CityNode getOrigin() {
		return origin;
	}
	
	public HashMap<CityNode, Integer> getDistances() {
		return distances;
	}
	
	public HashMap<CityNode, CityNode> getPredescest() {
		return predesces;
	}
	/**
	 * Streets are all the StreetNodes that are unused or used by a given player. While allCities are all possible CityNodes.
	 * @param streets
	 * @param allCities
	 * @return
	 */
	public List<StreetNode> getShortestDistanceToCityFromPlayerStreet (List<StreetNode> streets, List<CityNode> allCities, Player player) {
		List<StreetNode> shortestPath = null;
		
		List<CityNode> possibleCitys = new ArrayList<CityNode>();
		for (StreetNode s : streets) {
			if (s.getPiece() != null && s.getPiece().getPlayer().equals(player)) {
				for(CityNode c : allCities) {
					if (c.getAdjecentStreet().contains(s)) {
						possibleCitys.add(c);
					}
				}
			}
		}
		
		CityNode shortestDistCity = null;
		int dist = 9999;
		for (Entry<CityNode, Integer> i : distances.entrySet()) {
			if (i.getValue() < dist && !i.getKey().equals(origin) && possibleCitys.contains(i.getKey())) {
				shortestDistCity = i.getKey();
				dist = i.getValue();
			}
		}
		
		shortestPath = getStreetsTo(shortestDistCity, streets);
		
		return shortestPath;
	}
	
	public List<StreetNode> getStreetsTo(CityNode goal, List<StreetNode> streets) {
		List<StreetNode> nodes = new ArrayList<StreetNode>();

		if (originCanBeReached()) {
			if (predesces.get(goal) != null) {
				while(!goal.equals(origin)) {
					nodes.add(getStreetBetweenTwoCities(goal, predesces.get(goal), streets));
					goal = predesces.get(goal);
				}
			} else {
				System.out.println("Goal connnot be reached. " + distances.get(goal) + " ORigin: " + origin + " Goal: " + goal) ;
			}
		} else {
			System.out.println("Origin cannot be reached." + distances.get(goal));
		}
		return nodes;
	}
	
	private boolean originCanBeReached() {
		boolean canBeReached = false;
		
		for (Entry<CityNode, CityNode> e : predesces.entrySet()) {
			if (e.getValue() != null) {
				canBeReached = true;
			}
		}
		return canBeReached;
	}
	
	private StreetNode getStreetBetweenTwoCities(CityNode c1, CityNode c2, List<StreetNode> streets) {
		StreetNode street = null;
		
		for (StreetNode s : streets) {
			if (c1.getAdjecentStreet().contains(s) && c2.getAdjecentStreet().contains(s)) {
				street = s;
				break;
			}
		}
		return street;
	}
	
	public int getLongestPath() {
		int path = 0;
		
		for (Entry<CityNode, Integer> e : distances.entrySet()) {
			if (e.getValue() > path) {
				path = e.getValue();
			}
		}
		return path;
	}
	
	public List<StreetNode> getLongestPathStreets (List<StreetNode> allStreets) {
		List<StreetNode> streets = new ArrayList<StreetNode>();
		CityNode city = null;
		int getLongest = getLongestPath();
		for (Entry<CityNode, Integer> e: distances.entrySet()) {
			if (e.getValue() == getLongest) {
				city = e.getKey();
			}
		}
		System.out.println("Der Szie : " + distances.get(city));
		if (predesces.get(city) != null) {
			System.out.println("not Null");
			System.out.println("cIty Itself: " + city +"Predes of Start: " + predesces.get(city) + " Predes des Predes von City: " + predesces.get(predesces.get(city)));
			while (!city.equals(origin)) {
				System.out.println(" STREETS TO PUT INT : " + getStreetBetweenTwoCities(city, predesces.get(city), allStreets) + " DIST: " + distances.get(city) + " ORGIIN: " + origin + " CITY: " + city);
				streets.add(getStreetBetweenTwoCities(city, predesces.get(city), allStreets));
				city = predesces.get(city);
			}
		}
		System.out.println("Streets size: " + streets.size());
		return streets;
	}
}

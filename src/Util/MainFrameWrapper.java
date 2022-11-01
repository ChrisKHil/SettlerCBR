package Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import enums.TurnStage;
import frames.MainFrame;
import pieces.CityNode;
import pieces.StreetNode;
import player.Player;
import tiles.LandTile;
import tiles.Tile;

public class MainFrameWrapper {
	
	private MainFrame frame;
	
	public MainFrameWrapper(MainFrame frame) {
		this.frame = frame;
	}
	
	public Player getActivePlayer() {
		return frame.getActivePlayer();
	}
	
	public List<CityNode> getBuildableCityNodesFirst() {
		return frame.getBoard().getBuildableCityNodes();
	}
	
	public void buildFirstTurnTown(CityNode c) {
		frame.getBoard().buildFirstTurnTown(c);
	}
	
	public void buildFistTurnStreet(StreetNode s) {
		frame.getBoard().buildFirstTurnStreet(s);
	}
	
	public TurnStage getTurnStage () {
		return frame.getStage();
	}
	
	public boolean firstTurnTownBuild () {
		return frame.getBoard().getFirstCityPlaced();
	}
	
	public boolean didRollDie() {
		return frame.getBottomControl().getRolledDie();
	}

	public List<LandTile> getLandTieles() {
		return frame.getBoard().getLandTiles();
	}
	
	public List<LandTile> getOtherPlayersTiles(){
		List<LandTile> temp = new ArrayList<LandTile>();
		for (LandTile t : getAllEnemyTiles()) {
			if (!t.getContainsThieves()) {
				temp.add(t);
			}
			if (temp.size() == 0) {
				temp.add(getLandTieles().get(0));
			}
		}
		
		return temp;
	}
	
	public Player getPlayerByColor(String color) {
		return frame.getPlayerByColor(color);
	}
	
	public List<Player> getOtherPlayers(String color) {
		List<Player> temp = new ArrayList<Player>();
		for (Player p : frame.getPlayer()) {
			if (!ColorUtils.colorToString(p.getColor()).equals(color)) {
				temp.add(p);
			}
		}
		return temp;
	}
	
	public List<LandTile> getAllEnemyTiles(){
		List<LandTile> temp = new ArrayList<LandTile>();
		for (LandTile t : getLandTieles()) {
			for (CityNode c : frame.getBoard().getCloseNodes(t)) {
				if (c.getPiece() != null && c.getPiece().getPlayer() != getActivePlayer() && !temp.contains(t)) {
					temp.add(t);
				}
			}
		}
		return temp;
	}
	
	public List<StreetNode> getFirstTurnBuildableStreets() {
		List<StreetNode> temp = new ArrayList<StreetNode>();
		for (StreetNode s :frame.getBoard().getBuildableStreetNodesFirst()) {
			if (frame.getBoard().firsTurnStreetRule(s)) {
				temp.add(s);
			}
		}
		return temp;
	}
	
	public List<StreetNode> getBuildableStreetNodes() {
		return frame.getBoard().getBuildableStreetNodes();
	}
	
	public List<CityNode> getBuilalbeCitys() {
		return frame.getBoard().getBuildableCitys(frame.getActivePlayer());
	}
	
	public List<Tile> getTilesByCity(CityNode c) {
		return frame.getBoard().getCloseLandtiles(c);
	}
	
	public List<CityNode> getBuildableTowns() {
		return frame.getBoard().getBuildableCityNodes(frame.getActivePlayer());
	}
	
	public List<Player> getAllPlayer() {
		return frame.getPlayer();
	}
	
	public List<CityNode> getOwndedCities(Player player) {
		List<CityNode> cities = new ArrayList<CityNode>();
		for (CityNode c : frame.getBoard().getCities()) {
			if (c.getPiece() != null && c.getPiece().getPlayer().equals(player)) {
				cities.add(c);
			}
		}
		return cities;
	}
	
	public ResourceProbabilityPair getResourceProbabilityCombi(Player player) {
		ResourceProbabilityPair pair = new ResourceProbabilityPair();
		if (getOwndedCities(player).size() > 0) {
			CityNode startCity=  getOwndedCities(player).get(0);
			List<Tile> tiles = frame.getBoard().getCloseLandtiles(startCity);
			for (Tile t : tiles) {
				if (t instanceof LandTile) {
					System.out.println("Landtile:  " + ((LandTile) t).getNumber() + " TYPE: "  + ((LandTile) t).getType().toString());
					pair.add(((LandTile) t).getNumber(), ((LandTile) t).getType().toString().toLowerCase());
				} else {
					pair.add(1, "unknown");
				}
			}
		}
		return pair;
	}
	
	public CityNode findFittingNode(String solution) {
		CityNode c = null;
		
		List<String> solutionList =Arrays.asList(solution.split(";"));
		HashMap<CityNode, Integer> cityorder =getFittingCities(solutionList);
		if (cityorder.values().contains(3)) {
			for (Entry<CityNode, Integer> e : cityorder.entrySet()) {
				if (e.getValue() == 3) {
					c = e.getKey();
					break;
				}
			}
		} else if (cityorder.values().contains(2)) {
			for (Entry<CityNode, Integer> e : cityorder.entrySet()) {
				if (e.getValue() == 2) {
					c = e.getKey();
					break;
				}
			}
		}
		return c;
	}
	
	public CityNode findFittingNodeNEW(String solution) {
		CityNode c = null;
		
		List<String> solutionList =Arrays.asList(solution.split(";"));
		HashMap<CityNode, Integer> cityorder =getFittingCitiesNEW(solutionList);
		if (cityorder.values().contains(3)) {
			for (Entry<CityNode, Integer> e : cityorder.entrySet()) {
				if (e.getValue() == 3) {
					c = e.getKey();
					break;
				}
			}
		} else if (cityorder.values().contains(2)) {
			for (Entry<CityNode, Integer> e : cityorder.entrySet()) {
				if (e.getValue() == 2) {
					c = e.getKey();
					break;
				}
			}
		}
		return c;
	}
	
	public HashMap<CityNode, Integer> getFittingCities (List<String> solutionList) {
		HashMap<CityNode, Integer> cityorder = new HashMap<CityNode, Integer>();
		for (CityNode b : getBuildableCityNodesFirst()) {				
			int value = 0;
			for (Tile t : frame.getBoard().getCloseLandtiles(b)) {

				if (t instanceof LandTile) {
					if (solutionList.contains(((LandTile) t).getType().toString().toLowerCase())) {
						value++;
					}
				}  else {
					if (solutionList.contains("unkown")) {
						value++;
					}
				}
			}
			cityorder.put(b, value);
		}
		return cityorder;
	}
	
	
	//Diese Methode wird für unseren Agenten aufegrufen, um den möglichen Plätzen einen Score zu geben, abhängig von der Solution
	//Soll Problem beheben, dass Platz mit 3 mal Lumber einen Score von 3 kriegt, wenn Lösung nur 1 Lumber enthält.
	public HashMap<CityNode, Integer> getFittingCitiesNEW (List<String> solutionList) {
		HashMap<CityNode, Integer> cityorder = new HashMap<CityNode, Integer>();
		for (CityNode b : getBuildableCityNodesFirst()) {				
			int value = 0;
			for (Tile t : frame.getBoard().getCloseLandtiles(b)) {

				if (t instanceof LandTile) {
					if (solutionList.contains(((LandTile) t).getType().toString().toLowerCase())) {
						value++;
					}
				}  
				//Entfernt, da Solution, nie unknown enthält, bzw selbst wenn, 
				//sollte dies keinen Punkt geben, eine weitere Ressource ist immer besser
				/*else {
					if (solutionList.contains("unkown")) {
						value++;
					}
				}*/
			}
			cityorder.put(b, value);
		}
		return cityorder;
	}
}

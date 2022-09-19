package Util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import agents.AgentActionSettler;
import frames.MainFrame;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import pieces.CityNode;
import pieces.Node;
import pieces.StreetNode;
import player.Player;

public class AgentUtils {

	private final static String HOST = "localhost";
	
	private final static String PORT = "50000";
	
	public static List<AgentController> creataAgents(MainFrame frame, int KICount, String[] KIColors) {
		List<AgentController> controllers = new ArrayList<AgentController>();
		
		Profile mainProfile = new ProfileImpl();
		mainProfile.setParameter(Profile.LOCAL_HOST, HOST);
		mainProfile.setParameter(Profile.LOCAL_PORT, PORT);
		
		ContainerController controller = jade.core.Runtime.instance().createMainContainer(mainProfile);
		
		jade.core.Runtime rt = jade.core.Runtime.instance();
		
		Profile pf = new ProfileImpl();
		pf.setParameter(Profile.MAIN_HOST, HOST);
		pf.setParameter(Profile.MAIN_PORT, PORT);
		ContainerController cc = rt.createAgentContainer(pf);
		
		for (int i = 0; i < KICount; i++) {
			controllers.add(startPlayerAgent(HOST, PORT, KIColors[i], frame, cc));	
		}
		
		return controllers;
	}
	
	public static AgentController startPlayerAgent(String host, String port, String name, MainFrame frame, ContainerController cc) {
		if (cc != null) {
			try {
				AgentController ac = cc.createNewAgent(name, "agents.CBRPlayerAgent",new Object[] {name, frame});
				ac.start();
				return ac;
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String AgentNameToColorString(AgentController agent) throws StaleProxyException {
		return agent.getName().split("@")[0];
	}
	
	public static boolean AgentNameEqualsPlayerColor (Player player, AgentController controller) throws StaleProxyException {
		return ColorUtils.colorToString(player.getColor())
				.equals(AgentUtils.AgentNameToColorString(controller));
	}
	
	public static AgentController getAgentByPlayername (Player player, List<AgentController> agents) throws StaleProxyException {
		AgentController c = null;
		for (AgentController ac : agents) {
			if (AgentNameEqualsPlayerColor(player, ac)) {
				c = ac;
			}
		}
		
		return c;
	}
	
	public static int[] actionToPriceArray(AgentActionSettler action) {
		int[] temp = new int[5];
		switch (action) {
		case PLACE_CITY:
			temp = TradingUtils.getCityPriceArray();
		break;
		case PLACE_STREET:
			temp = TradingUtils.getSteetPriceArray();
		break;
		case PLACE_TOWN:
			temp = TradingUtils.getTownPriceArray();
		break;
		default:
			System.out.println("There is no adequate pricearray for action: " + action);
		break;
		}
		return temp;
	}
	
	public static int randomChoice (int arraySize) {
		int temp = 0;
		Random rand = new Random();
		temp = rand.nextInt(arraySize);
		return temp;
	}
	
	public static boolean shouldTrade (AgentActionSettler action, Player player) {
		boolean doTrade = true;
		switch(action) {
		case PLACE_CITY:
			if (player.canBuildCity()) {
				doTrade = false;
			}
			break;
		case PLACE_TOWN:
			if (player.canBuildTown()) {
				doTrade = false;
			}
			break;
		case PLACE_STREET:
			if (player.canBuildStreet()) {
				doTrade = false;
			}
			break;
		default:
			doTrade = false;
			break;
		} 
		return doTrade;
	}
	/**
	 * Create sum over an Ressourcearray.
	 */
	public static int sumOfRessources(int[] neededRessources) {
		int sum = 0;
		for (int i = 0; i < 5; i++) {
			sum += neededRessources[i];
		}
		return sum;
	}
	/**
	 * Checks what player could possibly be trading partner for a given resource
	 * @param player The player making the offer
	 * @param allPlayer every other player
	 * @param resources the resources Arrays containing the resources to trade for
	 * @return a list of possible trading partner
	 */
	public static List<Player> playerToTradeWith(Player player, List<Player> allPlayer, int[] resources) {
		List<Player> tempPlayerList = new ArrayList<Player>();
		for (Player p : allPlayer) {
			if (!p.equals(player)) {
				if (p.hasResources(resources)) {
					tempPlayerList.add(p);
				}
			}
		}
		return tempPlayerList;
	}

	/**
	 * 
	 */
	public static DijkstraReturnPair searchStreetsFromAtoBDijkst(CityNode goal, List<StreetNode> streets, List<CityNode> nodes) {
		List<CityNode> street = new ArrayList<CityNode>(nodes);
		
		HashMap<CityNode, Integer> distances = new HashMap<CityNode, Integer>();
		HashMap<CityNode, CityNode> predesces = new HashMap<CityNode, CityNode>();

		for (CityNode c: street) {
			distances.put(c, 9999);
			predesces.put(c, null);
		}
		distances.put(goal, 0);
		while (street.size() > 0) {
			CityNode c = getMinDistNode(distances, street);
			//System.out.println("Stuck in loop " + street.size() + " C; " + c.toString());
			if (c != null) {
				for (CityNode n : c.getAdjecentCities()) {
					//System.out.println("C: " + c  + " N: " + n + " DIST C: " + distances.get(c) + " DIST N: " + distances.get(n));
					if (distances.get(c) + getDistanceBetweenNeighbours(c, n, streets) < distances.get(n)) {
						distances.put(n, distances.get(c) + getDistanceBetweenNeighbours(c, n, streets));
						predesces.put(n, c);
					}
				}
			} else {
				System.out.println("Something is null");
			}
			street.remove(c);
		}	
		return new DijkstraReturnPair(goal, predesces, distances);
	}
	
	public static DijkstraReturnPair searchLongestPathByDijkstra(CityNode start,List<CityNode> nodes, List<StreetNode> streets, Player player) {
		List<CityNode> cities = new ArrayList<CityNode>(nodes);
		//List<CityNode> cities = getAllPlayerCityNodesForLongestPath(nodes, streets, player);
		HashMap<CityNode, Integer> distances = new HashMap<CityNode, Integer>();
		HashMap<CityNode, CityNode> predesces = new HashMap<CityNode, CityNode>();	

		for (CityNode c: cities) {
			distances.put(c, -1);
			predesces.put(c, null);
		}
		distances.put(start, 0);
		predesces.put(start, start);
		
		while (cities.size() > 0) {
			CityNode c = getMaxDistNode(distances, cities);
			//System.out.println("Stuck in loop " + street.size() + " C; " + c.toString());
			if (c != null) {
				for (CityNode n : c.getAdjecentCities()) {
					//System.out.println("C: " + c  + " N: " + n + " DIST C: " + distances.get(c) + " DIST N: " + distances.get(n));
					if (distances.get(c) + getDistanceBetweenNeighboursLongestPath(c, n, streets, player, predesces) > distances.get(n)) {
						distances.put(n, distances.get(c) + getDistanceBetweenNeighbours(c, n, streets));
						predesces.put(n, c);
					}
				}
			} else {
				System.out.println("Something is null");
			}
			cities.remove(c);
		}	
		
		//WOULD PROBALY LOOP THIS
		//distances.put(goal, 0);
		
		return new DijkstraReturnPair(start, predesces, distances);
	}
	
	private static CityNode getMinDistNode (HashMap<CityNode, Integer> map, List<CityNode> stillSearchable) {
		CityNode temp = null;
		int dist = 9999;
		for (Entry<CityNode, Integer> e : map.entrySet()) {
			if (e.getValue() <= dist && stillSearchable.contains(e.getKey())) {
				temp = e.getKey();
				dist = e.getValue();
			}
		}
		return temp;
	}
	
	private static CityNode getMaxDistNode(HashMap<CityNode, Integer> map, List<CityNode> stillSearchable) {
		CityNode temp = null;
		int dist = -999;
		for (Entry<CityNode, Integer> e : map.entrySet()) {
			if (e.getValue() >= dist && stillSearchable.contains(e.getKey())) {
				temp = e.getKey();
				dist = e.getValue();
			}
		}
		return temp;	
	}
	
	private static int getDistanceBetweenNeighbours(CityNode c, CityNode n, List<StreetNode> streets) {
		int distance = 99999;
		
		for(StreetNode s : streets) {
			if (c.getAdjecentStreet().contains(s) && n.getAdjecentStreet().contains(s)) {
				distance = 1;
			}
		}
		return distance;
	}
	
	private static int getDistanceBetweenNeighboursLongestPath(CityNode c, CityNode n ,List<StreetNode> streets, Player player, HashMap<CityNode, CityNode> predes) {
		int distance = -9999;
		
		for(StreetNode s : streets) {
			if ((c.getAdjecentStreet().contains(s) && n.getAdjecentStreet().contains(s) && s.getPiece() != null && s.getPiece().getPlayer().equals(player))
					&& predes.get(c) != null && !predes.get(c).equals(n)) {
				System.out.println("Dist of 1");
				distance = 1;
			} else if (c.getAdjecentStreet().contains(s) && n.getAdjecentStreet().contains(s) && (s.getPiece() == null || !s.getPiece().getPlayer().equals(player))) {
				System.out.println("ElsBLÖCKEs");
				distance = -10;
			}
		}
		return distance;
	}
	
	private static List<CityNode> getAllPlayerCityNodesForLongestPath(List<CityNode> nodes, List<StreetNode> streets, Player player) {
		List<CityNode> citys = new ArrayList<CityNode>();
		
		for (StreetNode s: streets) {
			if (s.getPiece() != null) {
				System.out.println("PIECE COLOR; " + s.getPiece().getPlayer().getColor() + " sdf " + player.getColor());
			}
			if (s.getPiece() != null && s.getPiece().getPlayer().equals(player)) {
				System.out.println("we are gere");
				for (CityNode m : nodes) {
					if (m.getAdjecentStreet().contains(s)) {
						citys.add(m);
					}
				}
			}
		}
		
		return citys;
	}
	
	public static double distance(int xTarget, int yTarget, int xOwned, int yOwned) {
		return Math.sqrt(Math.pow( Math.abs(xTarget - xOwned), 2) + Math.pow(Math.abs(yTarget -yOwned),2));
	}
}

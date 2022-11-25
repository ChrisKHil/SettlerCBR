package frames;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import Util.BoardEvent;
import Util.DijkstraReturnPair;
import Util.DistanceUtils;
import Util.LongestStreetUtils;
import enums.BoardEvents;
import enums.HarbourType;
import enums.LandType;
import enums.StreetOrientation;
import enums.TilePart;
import enums.TurnStage;
import listener.BoardListener;
import pieces.CityNode;
import pieces.Node;
import pieces.StreetNode;
import pieces.TownPiece;
import player.Player;
import tiles.HarbourTile;
import tiles.LandTile;
import tiles.Tile;

public class Board extends JPanel{
	
	private Tile clickedTile;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2089745097043205347L;

	private ArrayList<Integer> numbers = new ArrayList<Integer>(Arrays.asList(4, 6, 9, 2, 5, 12, 4, 9, 8, 8, 10, 3, 5, 10, 11, 3, 6, 11));

	private ArrayList<LandType> types = new ArrayList<LandType>(Arrays.asList(LandType.CORN,LandType.LUMBER,LandType.CORN,LandType.CLAY
			,LandType.LUMBER,LandType.WHOOL,LandType.WHOOL,LandType.WHOOL,LandType.CLAY,LandType.STONE,LandType.WHOOL,LandType.LUMBER,
			LandType.STONE, LandType.CLAY,LandType.LUMBER,LandType.CORN,LandType.CORN, LandType.STONE));
	
	private ArrayList<HarbourType> harbourTypes = new ArrayList<HarbourType>(Arrays.asList(HarbourType.CLAY, 
			HarbourType.CORN, HarbourType.STONE, HarbourType.THREE_TO_ONE, HarbourType.WHOOL, HarbourType.LUMBER, 
			HarbourType.THREE_TO_ONE, HarbourType.THREE_TO_ONE, HarbourType.THREE_TO_ONE));
	
	private HashSet<CityNode> cities;
	
	private HashSet<StreetNode> streets;
	
	private final int TILE_SIZE = 60;
	
	private List<LandTile> landTiles;
	
	private List<HarbourTile> harbours;
	
	private Node clickedNode;
	
	private MainFrame frame;
	
	private CityNode firstTurnCityPlaced;
	
	private List<BoardListener> listener;
	
	private boolean debugPhase = false;
	
	private int debuggedCities = 0;
	
	private Player debug1 = new Player(new Color(0,0,0));
	
	private Player debug2 = new Player(new Color(250,250,250));
	
	private Player activeDebugPlayer = null;
	
	private boolean placingTargetCity = false;
	
	private CityNode debugTargetCity = null;
	 
	public Board(BoardListener listener, BoardListener mainFrame) {
		this.setBackground(Color.LIGHT_GRAY);
		
		landTiles = new ArrayList<LandTile>();
		harbours = new ArrayList<HarbourTile>();
		cities = new HashSet<CityNode>();
		streets = new HashSet<StreetNode>();
		this.listener = new ArrayList<BoardListener>();
		this.listener.add(listener);
		this.listener.add(mainFrame);	
		this.frame = (MainFrame) mainFrame;
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!debugPhase) {
					switch (frame.getStage()) {
					case CHANGE_THIEVES:
						changeThieves(e.getX(), e.getY());
						break;
					case BUILD_TOWN:
						CityNode c = clickedCityNode(e.getX(), e.getY());
						buildTown(c);
						break;
					case BUILD_CITY:
						CityNode s = clickedCityNode(e.getX(), e.getY());
						buildCity(s);
						break;
					case BUILD_STREET:
						StreetNode st = clickedStreetNode(e.getX(), e.getY());
						buildStreet(st);
						break;
					default:
						if (frame.getStage() == TurnStage.FIRST_TURN || frame.getStage() == TurnStage.SECOND_TURN) {
							if (!frame.getBuildStartTown()) {
								CityNode cf = clickedCityNode(e.getX(), e.getY());
								buildFirstTurnTown(cf);
							} else {
								StreetNode sf = clickedStreetNode(e.getX(), e.getY());
								buildFirstTurnStreet(sf);
							}
						}
						break;
					}
					repaint();
				} else {
					CityNode s = clickedCityNode(e.getX(), e.getY());
					
					if (s != null && !placingTargetCity) {
						s.setPiece(activeDebugPlayer.placeTownPiece(true));
					} else if (placingTargetCity) {
						debugTargetCity = s;
					} else {
						StreetNode st = clickedStreetNode(e.getX(), e.getY());
						
						if (st != null) {
							st.setPiece(activeDebugPlayer.placeStreetPiece(true));
						}
					}
					repaint();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {	
			}
		});	
		repaint();
	}
	/**
	 * (int)((4*1.57) * TILE_SIZE) + 2 * TILE_SIZE Length of the Board!!
	 */
	public void setupBoard() {
		
		drawTiles();
		distributeChances();
		addHarbourToTown();
		addAdjecentCities();
		addAdjecnetStreets();
		addAdjeecentLandTypes();
		addAdjecentStreetNodes();
	}
	
	public List<Player> getAdjecentPlayers(LandTile tile){
		List<Player> tempList = new ArrayList<Player>();
		for (CityNode n : cities) {
			if (DistanceUtils.contains(
					tile.getX(), n.getX(), tile.getY(), n.getY(), (int)(TILE_SIZE * 1.4))) {
				if (n.getPiece() != null && !n.getPiece().getPlayer().equals(frame.getActivePlayer())) {
					tempList.add(n.getPiece().getPlayer());
				}
			}
		}
		return tempList;
	}
	
	public void buildTown(CityNode c) {
		if (c != null && c.getPiece() == null && c.distanceRule() && c.isAtStreet(frame.getActivePlayer()) && frame.getActivePlayer().canBuildTown()) {
			c.setPiece(frame.getActivePlayer().placeTownPiece(false));
			frame.getActivePlayer().addHardbour(c.getHarbourType());
			//LongestStreetUtils.reevaluateLongestStreet(c);
			notifieListeners(new BoardEvent(BoardEvents.PIECE_PLACED_SUCCESSFULLY));
		}
	}
	
	public void buildCity(CityNode c) {
		if (c != null && c.getPiece() != null && c.getPiece().getPlayer().equals(frame.getActivePlayer()) && c.getPiece() instanceof TownPiece && frame.getActivePlayer().canBuildCity()) {
			c.setPiece(frame.getActivePlayer().placeCityPiece((TownPiece)c.getPiece()));
			notifieListeners(new BoardEvent(BoardEvents.PIECE_PLACED_SUCCESSFULLY));
		}
	}
	
	public void buildStreet(StreetNode s) {
		if (s != null && s.getPiece() == null && s.getStreetConnectionRule(frame.getActivePlayer()) && frame.getActivePlayer().canBuildStreet()) {
			s.setPiece(frame.getActivePlayer().placeStreetPiece(false));
			LongestStreetUtils.updateLongestStreet(s);
			notifieListeners(new BoardEvent(BoardEvents.PIECE_PLACED_SUCCESSFULLY));
		}
	}
	
	public void buildFirstTurnTown(CityNode c) {
		if (c != null && c.getPiece() == null && c.distanceRule()) {
			c.setPiece(frame.getActivePlayer().placeTownPiece(true));
			firstTurnCityPlaced = c;
			frame.setBuildStartTown(true);
			//entfernt, damit man auch beim ersten Dorf die Ressourcen bekommt
			//if (frame.getStage() == TurnStage.SECOND_TURN) {
				for (LandType t : c.getAdjecentTypes()) {
					c.getPiece().getPlayer().addRessourceByEnum(t, false);
					notifieListeners(new BoardEvent( BoardEvents.RESSOURCES_DISTRIBUTED));
				}
			//}
			frame.getActivePlayer().addHardbour(c.getHarbourType());
			notifieListeners(new BoardEvent(BoardEvents.TOWN_PLACED));
		}
	}
	
	public void buildFirstTurnStreet (StreetNode s) {
		System.out.println("Building a first Turn Street");
		if (firsTurnStreetRule(s)) {
			s.setPiece(frame.getActivePlayer().placeStreetPiece(true));
			firstTurnCityPlaced = null;
			frame.setBuildStartTown(false);
			notifieListeners(new BoardEvent(BoardEvents.STREET_PLACED));
			frame.setNextPlayerFirstTurn();
		}
	}
	
	public boolean firsTurnStreetRule(StreetNode s) {
		System.out.println("PlayerCity: " + firstTurnCityPlaced.getPiece().getPlayer() + " PlayerActive " + frame.getActivePlayer() + " FirstTurnCity: " + firstTurnCityPlaced);
		return s != null && s.getPiece() == null && firstTurnCityPlaced.getAdjecentStreet().contains(s) 
				&& firstTurnCityPlaced.getPiece().getPlayer().equals(frame.getActivePlayer());
	}
	
	private LandTile determineClickedTile(int x, int y) {
		LandTile p = null;
		for (LandTile tp : landTiles) {
			if (tp.isInFiled(x, y)) {
				p = tp;
			}
		}
		return p;
	}
	
	private CityNode clickedCityNode(int x, int y) {
		CityNode temp = null;
		for(CityNode n : cities) {
			if (n.contains(new Point(x,y), 10)) {
				temp = n;
			}
		}
		return temp;
	}
	
	private StreetNode clickedStreetNode(int x, int y) {
		StreetNode temp = null;
		for (StreetNode s : streets) {
			if (s.containsStreet(new Point(x,y))) {
				temp = s;
			}
		}
		return temp;
	}
	
	private void addAllCityNodes() {
		HashSet<Point> tempPoints = new HashSet<Point>();
		for (LandTile l : landTiles) {
			for (int i = 0; i < 6; i++) {
				tempPoints.add(new Point(l.drawTile().xpoints[i], l.drawTile().ypoints[i]));
			}
		}
		for (Point p : tempPoints) {
			cities.add(new CityNode(p.x, p.y));
		}
	}
	
	private void addHarbourToTown() {
		for (CityNode c : cities) {
			for (HarbourTile t: harbours) {
				if (t.containsCity(c, 3)) {
					if (c.getHarbourType() == HarbourType.NONE) {
						c.setHarbourType(t.getType());
					}
				}
			}
		}
	}
	
	private void addAllStreetNodes(int tempWidth) {
		
		int xSlant = landTiles.get(0).drawTile().xpoints[1] - ((landTiles.get(0).drawTile().xpoints[1] - landTiles.get(0).drawTile().xpoints[0])/2);
		int ySlant = landTiles.get(0).drawTile().ypoints[0] - ((landTiles.get(0).drawTile().ypoints[0] - landTiles.get(0).drawTile().ypoints[1])/2);
		int xLine = landTiles.get(0).drawTile().xpoints[1];
		int yLine = landTiles.get(0).getY();
		int multiply = 0;
		
		for (int i = 0; i < 19; i++) {
			streets.add(new StreetNode(xSlant - (multiply*(tempWidth/2)), ySlant, StreetOrientation.RIGHT, tempWidth/2, TILE_SIZE/2));
			streets.add(new StreetNode(xSlant - (multiply*(tempWidth/2)), (int)(ySlant - 1.5*TILE_SIZE), StreetOrientation.LEFT, tempWidth/2, TILE_SIZE/2));
			streets.add(new StreetNode(xSlant - ((multiply+1)*(tempWidth/2)) , (int)(ySlant - 1.5*TILE_SIZE), StreetOrientation.RIGHT, tempWidth/2, TILE_SIZE/2));
			streets.add(new StreetNode(xSlant - ((multiply+1)*(tempWidth/2)) , ySlant, StreetOrientation.LEFT, tempWidth/2, TILE_SIZE/2));
			streets.add(new StreetNode(xLine - ((multiply/2)*(tempWidth)), yLine, StreetOrientation.CENTER, tempWidth/2, TILE_SIZE/2));
			streets.add(new StreetNode(xLine - (((multiply/2)+1)*(tempWidth)), yLine, StreetOrientation.CENTER, tempWidth/2, TILE_SIZE/2));

			multiply+= 2;
			if (i == 2 || i == 6 || i == 11 || i == 15) {
				multiply = 0;
				xSlant += tempWidth/2 * (i > 8 ? -1 : 1);
				ySlant += 1.5*TILE_SIZE;
				xLine +=  tempWidth/2 * (i > 8 ? -1 : 1);
				yLine += 1.5*TILE_SIZE;
			}		
		}
	}
	/**
	 * For all cityNodes adds all three close CityNodes
	 */
	private void addAdjecentCities() {
		for (CityNode c : cities) {
			for (CityNode c1 : cities) {
				if (c.contains(c1.getPoint(), TILE_SIZE + 3)) {
					if (!c.equals(c1)) {
						c.addCityToAdjecentList(c1);
					}
				}
			}
		}
	}
	
	private void addAdjecnetStreets() {
		for (CityNode c : cities) {
			for (StreetNode n : streets) {
				if (c.contains(n.getPoint(), (int)(TILE_SIZE * 0.75))) {
					c.addStreetToAdjecentList(n);
				}
			}
		}
	}
	
	private void addAdjeecentLandTypes() {
		for (CityNode c : cities) {
			for (LandTile l : landTiles) {
				if (c.contains(new Point(l.getX(),l.getY()), (int)(TILE_SIZE*1.2))) {
					c.addAdjecnentLandType(l.getType());
					if (c.getAdjecentTypes().size() > 3) {
						System.out.println("Wied Error Happend");
					} else if (c.getAdjecentTypes().size() == 0) {
						System.out.println("ZERO ERROR HAPPEND");
					}
				}
			}
		}
	}
	
	private void addAdjecentStreetNodes() {
		for (StreetNode s : streets) {
			for (StreetNode st :streets) {
				if (!st.equals(s) && s.contains(new Point(st.getX(), st.getY()), (int)(TILE_SIZE*1.2))) {
					s.addAjecentStreetNode(st);
				}
			}
			System.out.println("Street: " + s.getAdjecentStreet().size());
			System.out.println(s.getAdjecentStreet());
		}
	}
	
	public List<CityNode> getBuildableCityNodes() {
		List<CityNode> tempnodes = new ArrayList<CityNode>();
		for (CityNode c :cities) {
			//Need more rules, the street thing is not yet added
			if (c.distanceRule()) {
				tempnodes.add(c);
			}
		}
		return tempnodes;
	}
	
	public List<CityNode> getBuildableCityNodes(Player player) {
		List<CityNode> tempnodes = new ArrayList<CityNode>();
		for (CityNode c :getBuildableCityNodes()) {
			//Need more rules, the street thing is not yet added
			if (c.isAtStreet(player)) {
				tempnodes.add(c);
			}
		}
		return tempnodes;
	}
	
	public List<StreetNode> getBuildableStreetNodesFirst() {
		List<StreetNode> streets = new ArrayList<StreetNode>();
		for (CityNode c : cities) {
			if (c.getPiece() != null && c.getPiece().getPlayer().equals(frame.getActivePlayer())) {
				for (StreetNode n : c.getAdjecentStreet()) {
					if (n.getPiece() == null) {
						streets.add(n);
					}
				}
			}
		}
		return streets;
	}
	
	
	public List<StreetNode> getBuildableStreetNodes() {
		List<StreetNode> streets = new ArrayList<StreetNode>();
		for (StreetNode s : this.streets) {
			if (s.getPiece() != null && s.getPiece().getPlayer().equals(frame.getActivePlayer())) {
				for (StreetNode n : s.getAdjecentStreet()) {
					if (n.getPiece() == null) {
						streets.add(n);
					}
				}
			}
		}
		return streets;
	}
	
	public List<CityNode> getBuildableCitys (Player player) {
		List<CityNode> nodes = new ArrayList<CityNode>();
		//Here a test of equality on the player should suffice as the town already satisfied the other rules.
		for (CityNode c : cities) {
			if (c.getPiece() != null && c.getPiece().getPlayer().equals(player) && c.getPiece() instanceof TownPiece) {
				nodes.add(c);
			}
		}
		return nodes;
	}
	
	public List<CityNode> getSearchableNodesForPlayer(Player player){
		List<CityNode> nodes = new ArrayList<CityNode>();
		
		for (CityNode c : cities) {
			if (c.getPiece() == null || c.getPiece().getPlayer().equals(player)) {
				nodes.add(c);
			}
		}
		return nodes;
	}
	
	public List<StreetNode> getSearchableStreetNodesForPlayer(Player player){
		List<StreetNode> nodes = new ArrayList<StreetNode>();
		
		for (StreetNode s : streets) {
			if (s.getPiece() == null || s.getPiece().getPlayer().equals(player)) {
				nodes.add(s);
			}
		}
		return nodes;
	}
	
	private void drawTiles() {
		//Determines half of the leftover hight after creating the tiles to center the field.
		int yHeight = 2*TILE_SIZE + (((this.getPreferredSize().height - ((int)10 * TILE_SIZE)))/2);
//		int tileCounter = 0;
		landTiles.add(new LandTile((this.getPreferredSize().width/2)+ ((int)(1.723*TILE_SIZE)),yHeight, TILE_SIZE, TilePart.COMPLETE));
		//Y Values not nessearily equal, thus fixing it as they should be the same.
		landTiles.get(0).drawTile().ypoints[2] = landTiles.get(0).drawTile().ypoints[4];
		Polygon tempPoly = landTiles.get(0).drawTile();
		int tempWidht = tempPoly.xpoints[1] - tempPoly.xpoints[4];
		int multiplicator = 0;
		for (int i = 0; i < 14; i++) {
			if (i < 2) {
//				System.out.println("TempWidth: " + tempWidht + " Multiplicator: " 
//						+ multiplicator + " Multipliator: " + (tempWidht * multiplicator));
				if (i == 0) {
					System.out.println("X: " + (tempPoly.xpoints[4] - (multiplicator+1)*tempWidht) + " Y: " + tempPoly.ypoints[2]);
				}
				if (i == 1) {
					System.out.println("X: " + (tempPoly.xpoints[4] - multiplicator*tempWidht) + " Y: " + tempPoly.ypoints[4]);
				}
			}
			if (i == 2 || i == 5 || i == 9 || i == 12) {
				updatePoly(tempPoly, tempWidht, i == 9 || i == 12);
				tempPoly = landTiles.get(landTiles.size()-1).drawTile();
				multiplicator = 0;
			}
			landTiles.add(new LandTile(tempPoly.xpoints[0] - (multiplicator+1) * tempWidht, tempPoly.ypoints[0]
					,tempPoly.xpoints[5] - multiplicator*tempWidht, tempPoly.ypoints[5]
					,tempPoly.xpoints[4] - multiplicator*tempWidht, tempPoly.ypoints[4]
					,tempPoly.xpoints[3] - (multiplicator+1) *tempWidht, tempPoly.ypoints[3]
					,tempPoly.xpoints[4] - (multiplicator+1)*tempWidht, tempPoly.ypoints[2]
					,tempPoly.xpoints[4] - (multiplicator+1) * tempWidht, tempPoly.ypoints[1]));
			multiplicator++;
		}
		createHarbourTiles(landTiles.get(0).drawTile().xpoints[4], landTiles.get(0).drawTile().ypoints[4],
				landTiles.get(0).drawTile().xpoints[3], landTiles.get(0).drawTile().ypoints[3],
				tempWidht);
		addAllCityNodes();
		addAllStreetNodes(tempWidht);
	}
	
	public List<LandTile> getTilesByNumber(int number) {
		List<LandTile> tiles = new ArrayList<LandTile>();
		for (LandTile t : landTiles) {
			if (t.getNumber() == number) {
				tiles.add(t);
			}
		}
		return tiles;
	}
	/**
	 * Used to distribute ressources.
	 * @param land
	 * @return
	 */
	public List<CityNode> getCloseNodes(LandTile land){
		List<CityNode> cities = new ArrayList<CityNode>();
		for (CityNode c: this.cities) {
			if (DistanceUtils.contains(land.getX(), c.getX(), land.getY(), c.getY(), (int)(TILE_SIZE * 1.2))) {
				cities.add(c);
			}
		}
		return cities;
	}
	/**
	 * Gets all the tiles surrounding a cityNode.
	 * @param c
	 * @return
	 */
	public List<Tile> getCloseLandtiles (CityNode c) {
		List<Tile> tiles = new ArrayList<>();
			for (LandTile l : landTiles) {
				if (DistanceUtils.contains(c.getX(), l.getX(), c.getY(), l.getY(), (int)(TILE_SIZE * 1.2))) {
					tiles.add(l);
				}
			}
			for (HarbourTile t : harbours) {
				if (DistanceUtils.contains(c.getX(), t.getX(), c.getY(), t.getY(), (int)(TILE_SIZE * 1.2))) {
					tiles.add(t);
				}
			}
		return tiles;
	}
	
	private void createHarbourTiles(int x1, int y1, int x2, int y2, int width) {
		for (int i = 0; i < 6; i++) {
			if (i == 0 || i == 1) {
				harbours.add(new HarbourTile(x1, y1, x2, y2, width, TILE_SIZE, TilePart.TOP, HarbourType.NONE));
				harbours.add(new HarbourTile(x1, y1 + (9 * TILE_SIZE), x2, y2 + (9 * TILE_SIZE), width, TILE_SIZE, TilePart.BOTTOM, HarbourType.NONE));
				x1 -= width;
				x2 -= width;
			} else if (i == 2) {
				harbours.add(new HarbourTile(x1, y1, x2, y2, width, TILE_SIZE, TilePart.OLEFT, HarbourType.NONE));
				harbours.add(new HarbourTile(x1, y1 + (9 * TILE_SIZE),
						x2, y2 + (9 * TILE_SIZE), width, TILE_SIZE, TilePart.ULEFT, HarbourType.NONE));
				harbours.add(new HarbourTile(x1 + 3 * width, y1, x2 + 3 * width, y2, width, TILE_SIZE, TilePart.ORIGHT, HarbourType.NONE));
				harbours.add(new HarbourTile(x1 + 3 * width, y1 + (9 * TILE_SIZE),
						x2 + 3 * width, y2 + (9 * TILE_SIZE), width, TILE_SIZE, TilePart.URIGHT, HarbourType.NONE));

				int tempx1 = x1;
				int tempy1 = y1;
				x1 = x2 - width;
				y1 = y2 + (int)(2*TILE_SIZE);
				x2 = tempx1;
				y2 = tempy1 + TILE_SIZE;
			} else if (i == 3) {
				harbours.add(new HarbourTile(x1, y1, x2, y2, width, TILE_SIZE, TilePart.COMPLETEOL, HarbourType.NONE));
				harbours.add(new HarbourTile(x1, y1 + 6 * TILE_SIZE, x2, y2 + 6 * TILE_SIZE, width, TILE_SIZE, TilePart.COMPLETEUL, HarbourType.NONE));
				harbours.add(new HarbourTile(x1 + 4 * width, y1, x2 + 4 * width, y2, width, TILE_SIZE, TilePart.COMPLETEOR, HarbourType.NONE));
				harbours.add(new HarbourTile(x1 + 4 * width, y1 + 6 * TILE_SIZE,
						x2 + 4 * width, y2 + 6 * TILE_SIZE, width, TILE_SIZE, TilePart.COMPLETEUR, HarbourType.NONE));

				int tempx1 = x1;
				int tempy1 = y1;
				x1 = x2 - width;
				y1 = y2 + (int)(2*TILE_SIZE);
				x2 = tempx1;
				y2 = tempy1 + TILE_SIZE;
			} else if (i == 4) {
				harbours.add(new HarbourTile(x1, y1, x2, y2, width, TILE_SIZE, TilePart.COMPLETEOL, HarbourType.NONE));
				harbours.add(new HarbourTile(x1, y1 + 3 * TILE_SIZE, x2, y2 + 3 * TILE_SIZE, width, TILE_SIZE, TilePart.COMPLETEUL, HarbourType.NONE));
				harbours.add(new HarbourTile(x1 + 5 * width, y1, x2 + 5 * width, y2, width, TILE_SIZE, TilePart.COMPLETEOR, HarbourType.NONE));
				harbours.add(new HarbourTile(x1 + 5 * width, y1 + 3 * TILE_SIZE,
						x2 + 5 * width, y2 + 3 * TILE_SIZE, width, TILE_SIZE, TilePart.COMPLETEUR, HarbourType.NONE));
				int tempx1 = x1;
				int tempy1 = y1;
				x1 = x2 - width;
				y1 = y2 + (int)(2*TILE_SIZE);
				x2 = tempx1;
				y2 = tempy1 + TILE_SIZE;	
			} else if (i == 5) {
				harbours.add(new HarbourTile(x1, y1, x2, y2, width, TILE_SIZE, TilePart.LEFT, HarbourType.NONE));
				harbours.add(new HarbourTile(x1 + 6 * width, y1, x2 + 6 * width, y2, width, TILE_SIZE, TilePart.RIGHT, HarbourType.NONE));
			}
		}
	}
	
	private void updatePoly(Polygon tempPoly, int tempWidht, boolean reverse) {
		if (!reverse) {
			landTiles.add(new LandTile(tempPoly.xpoints[1], tempPoly.ypoints[1] + 2*TILE_SIZE
					,tempPoly.xpoints[0] + tempWidht, tempPoly.ypoints[0] + TILE_SIZE
					,tempPoly.xpoints[0] + tempWidht, tempPoly.ypoints[0]
					,tempPoly.xpoints[1], tempPoly.ypoints[1]
					,tempPoly.xpoints[0], tempPoly.ypoints[0]
					,tempPoly.xpoints[0], tempPoly.ypoints[0] + TILE_SIZE));
		} else {
			landTiles.add(new LandTile(tempPoly.xpoints[5], tempPoly.ypoints[5] + 2*TILE_SIZE
					,tempPoly.xpoints[0], tempPoly.ypoints[0] + TILE_SIZE
					,tempPoly.xpoints[0], tempPoly.ypoints[0]
					,tempPoly.xpoints[5], tempPoly.ypoints[5]
					,tempPoly.xpoints[0] - tempWidht, tempPoly.ypoints[0]
					,tempPoly.xpoints[0] - tempWidht, tempPoly.ypoints[0] + TILE_SIZE));
		}
	}
	
	private void distributeChances () {
		Random rand = new Random();
		for (LandTile tile : landTiles) {
			int temp = rand.nextInt(numbers.size());
			if (DistanceUtils.contains(tile.getX(),this.getPreferredSize().width/2, tile.getY(),this.getPreferredSize().height/2, 25)) {
				tile.setType(LandType.DESERT);
				types.remove(LandType.DESERT);
			} else {
				tile.setType(types.get(temp));
				types.remove(temp);
			}
			if (tile.getType() != LandType.DESERT) {
				tile.setNumber(numbers.get(temp));
				numbers.remove(temp);
			} else {
				tile.setNumber(0);
				tile.setContainsThieves(true);
			}
			tile.initImage();
		}
		
		for (int i = 0; i < 10; i++) {
			if (2 * i == 0 || 2 * i == 4 || 2 * i == 10 || 2 * i == 12 || 2 * i == 16) {
				int temp = rand.nextInt(harbourTypes.size());
				harbours.get(2*i + 1).setHarbourType(harbourTypes.get(temp));
				harbourTypes.remove(temp);
				if (2*i != 16) {
					temp = rand.nextInt(harbourTypes.size());
					harbours.get(2*i).setHarbourType(harbourTypes.get(temp));
					harbourTypes.remove(temp);
				}
			}
		}
	}
	
	private void changeThieves(int x, int y) {
		LandTile tile = determineClickedTile(x, y);
		changeThieves(tile);
	}
	
	
	public void changeThieves(LandTile tile) {
		System.out.println("We are ON IT");
		if (tile != null) {
			System.out.println("THE TILE WAS NOT NULL");
			LandTile thiefTile = getTiefTile();
			thiefTile.setContainsThieves(false);
			tile.setContainsThieves(true);
			repaint();
			notifieListeners(new BoardEvent(BoardEvents.THIEVES_PLACED, tile));
		}
	}
	
	private LandTile getTiefTile() {
		LandTile temp = null;
		for (LandTile t : landTiles) {
			if (t.getContainsThieves()) {
				temp = t;
			}
		}
		return temp;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(2.0f));
		g2d.setFont(new Font(Font.SERIF, 0, 15));
		//To determine the middle of the board.
		//g.drawLine((this.getPreferredSize().width/2), 0, (this.getPreferredSize().width/2), 92 +((int)((4*1.57) * TILE_SIZE) + 2 * TILE_SIZE));
		for (LandTile t : landTiles) {
			 
			g2d.setColor(Color.BLACK);
			g2d.drawPolygon(t.drawTile());
			g2d.setClip(t.drawTile());
			g2d.drawImage(t.getImage(), t.getX()- TILE_SIZE, t.getY()- TILE_SIZE, 2*TILE_SIZE, 2*TILE_SIZE, this);
			if (!t.getContainsThieves()) {
				g2d.drawOval(t.getX()- (int)(TILE_SIZE * 0.3), t.getY()- (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.6));
				g2d.setColor(Color.ORANGE);
				g2d.fillOval(t.getX()- (int)(TILE_SIZE * 0.3), t.getY()- (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.6));
				
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font(Font.SERIF, Font.BOLD, 18));
				g2d.drawString(Integer.toString(t.getNumber()), t.getX() - 7, t.getY() + 7);
			} else {
				g2d.drawOval(t.getX() +(int)(TILE_SIZE * 0.1), t.getY()- (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.6));
				g2d.setColor(Color.ORANGE);
				g2d.fillOval(t.getX() + (int)(TILE_SIZE * 0.1), t.getY()- (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.6));
				g2d.setColor(Color.BLACK);
				g2d.drawOval(t.getX() - (int)(TILE_SIZE * 0.6), t.getY()- (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.6));
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.6), t.getY()- (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.6));
				
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font(Font.SERIF, Font.BOLD, 18));
				g2d.drawString(Integer.toString(t.getNumber()), t.getX() + 20, t.getY() + 7);
				g2d.drawString("T", t.getX() - 25, t.getY() + 7);
			}
			g2d.setClip(null);
		}
		for (HarbourTile t : harbours) {
			g2d.drawPolygon(t.drawTile());
			g2d.setClip(t.drawTile());
			g2d.drawImage(t.getImage(), t.getX()- TILE_SIZE, t.getY()- TILE_SIZE, 2*TILE_SIZE, 2*TILE_SIZE, this);
			if (t.getType() != HarbourType.NONE) {
				g2d.setClip(null);
				g2d.drawOval(t.getX()- (int)(TILE_SIZE * 0.05), t.getY()- (int)(TILE_SIZE * 0.35), (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.6));
				g2d.setColor(Color.WHITE);
				g2d.fillOval(t.getX()- (int)(TILE_SIZE * 0.05), t.getY()- (int)(TILE_SIZE * 0.35), (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.6));
				g2d.setColor(Color.BLACK);
				if (t.getType() == HarbourType.THREE_TO_ONE) {
					g2d.drawString("3:1", t.getX(), t.getY());
				} else if  (t.getType() == HarbourType.CLAY) {
					g2d.drawString("CL", t.getX(), t.getY());
				} else if  (t.getType() == HarbourType.CORN) {
					g2d.drawString("CO", t.getX(), t.getY());
				} else if  (t.getType() == HarbourType.STONE) {
					g2d.drawString("ST", t.getX(), t.getY());
				} else if  (t.getType() == HarbourType.LUMBER) {
					g2d.drawString("LU", t.getX(), t.getY());
				} else if  (t.getType() == HarbourType.WHOOL) {
					g2d.drawString("WH", t.getX(), t.getY());
				}
				g2d.setClip(t.drawTile());
			}
			if (t.getType() != HarbourType.NONE) {
				g2d.setColor(Color.WHITE);
				switch(t.getOrientation()) {
				case RIGHT:
					g2d.fillOval(t.getX() - (int)(TILE_SIZE), t.getY() + (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() - (int)(TILE_SIZE), t.getY() - (int)(TILE_SIZE * 0.6), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				case ULEFT:
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.15), t.getY() - (int)(TILE_SIZE * 1.1), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() + (int)(TILE_SIZE * 0.7), t.getY() - (int)(TILE_SIZE * 0.65), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				case OLEFT:
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.15), t.getY() + (int)(TILE_SIZE * 0.8), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() + (int)(TILE_SIZE * 0.7), t.getY() + (int)(TILE_SIZE * 0.35), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				case TOP:
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.15), t.getY() + (int)(TILE_SIZE * 0.8), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.95), t.getY() + (int)(TILE_SIZE * 0.35), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				case BOTTOM:
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.15), t.getY() - (int)(TILE_SIZE * 1.1), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.95), t.getY() - (int)(TILE_SIZE * 0.65), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				case COMPLETEOL:
					g2d.fillOval(t.getX() + (int)(TILE_SIZE * 0.7), t.getY() - (int)(TILE_SIZE * 0.55), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() + (int)(TILE_SIZE * 0.7), t.getY() + (int)(TILE_SIZE * 0.35), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				case COMPLETEOR:
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.15), t.getY() + (int)(TILE_SIZE * 0.8), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.95), t.getY() + (int)(TILE_SIZE * 0.35), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				case COMPLETEUL:
					g2d.fillOval(t.getX() + (int)(TILE_SIZE * 0.7), t.getY() - (int)(TILE_SIZE * 0.55), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() + (int)(TILE_SIZE * 0.7), t.getY() + (int)(TILE_SIZE * 0.35), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				case COMPLETEUR:
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.15), t.getY() - (int)(TILE_SIZE * 1.1), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					g2d.fillOval(t.getX() - (int)(TILE_SIZE * 0.95), t.getY() - (int)(TILE_SIZE * 0.65), (int)(TILE_SIZE * 0.3), (int)(TILE_SIZE * 0.3));
					break;
				default:
					break;
				}
				g2d.setColor(Color.BLACK);
			}
			g2d.setClip(null);
		}
		for (StreetNode s:streets) {
			g2d.drawRect(s.getX(), s.getY(), 3, 3);
			g2d.drawPolygon(s.getStreet());
		}
		CityNode temp = null;
		List<StreetNode> searchStreets = new ArrayList<StreetNode>();
		DijkstraReturnPair pair = null;
		DijkstraReturnPair pairLongest = null;
		List<StreetNode> longestStreet = new ArrayList<StreetNode>();
		
		for (CityNode c : cities) {
			if (c.getPiece() != null && !debugPhase) {
				//g2d.drawRect(c.getX()-2, c.getY()-2, 4, 4);
				g2d.setColor(c.getPiece().getPlayer().getColor());
				if (c.getPiece() instanceof TownPiece) {
					g2d.fillRect(c.getX()-4, c.getY()-4, 8, 8);
				} else  {
					g2d.fillRect(c.getX() - 6, c.getY() - 6, 12, 12);
				}
			} else if (debugPhase && c.getPiece() != null) {
				g2d.setColor(c.getPiece().getPlayer().getColor());
				g2d.fillRect(c.getX() - 6, c.getY() - 6, 12, 12);
				
				if (debugTargetCity != null) {
					List<StreetNode> streets =  getSearchableStreetNodesForPlayer(activeDebugPlayer);
					System.out.println("Searchable Streets sitze :D: " + streets.size());
					//List<CityNode> cities = getSearchableNodesForPlayer(activeDebugPlayer);
					pair = Util.AgentUtils.searchStreetsFromAtoBDijkst(debugTargetCity, streets, new ArrayList<CityNode>(cities));
					searchStreets = pair.getShortestDistanceToCityFromPlayerStreet(streets, new ArrayList<CityNode>(cities), activeDebugPlayer);
					
					pairLongest = Util.AgentUtils.searchLongestPathByDijkstra(debugTargetCity, new ArrayList<CityNode>(cities), new ArrayList<StreetNode>(this.streets), activeDebugPlayer);
					System.out.println("PATH: " + pairLongest.getLongestPathStreets(new ArrayList<StreetNode>(this.streets)));
					
					longestStreet =  pairLongest.getLongestPathStreets(new ArrayList<StreetNode>(this.streets));
					System.out.println("LongestSizte: " + longestStreet.size());
					//longestStreet = pairLongest.get
				}
//				if (debugPhase && debuggedCities == 2) {
//					if (temp == null) {
//						temp = c;
//						g2d.setColor(new Color(0, 0, 0));
//						g2d.fillRect(c.getX() - 6, c.getY() - 6, 12, 12);
//					} else {
//						pair = Util.AgentUtils.searchStreetsFromAtoBDijkst(temp, possibleNodesDebug(), new ArrayList<CityNode>(cities));
//						searchStreets = pair.getStreetsTo(c, new ArrayList<StreetNode>(streets));
//					}
//				}
			} else if (c.equals(debugTargetCity)) {
				g2d.setColor(new Color(250, 0 , 0));
				g2d.fillRect(c.getX() - 6, c.getY() - 6, 12, 12);
			}
		}

		for (StreetNode s : streets) {
			if (s.getPiece() != null && !longestStreet.contains(s)) {
				//g2d.drawRect(c.getX()-2, c.getY()-2, 4, 4);
				g2d.setColor(s.getPiece().getPlayer().getColor());
				g2d.fillPolygon(s.getStreet());
			} else if (debugPhase) {
				//if (tangel != null && tangel.contains(new Point(s.getX(), s.getY()))) {
					g2d.setColor(new Color(75, 75, 220));
					g2d.fillPolygon(s.getStreet());
					if (searchStreets.contains(s)) {
						g2d.setColor(new Color(75, 225, 75));
						g2d.fillPolygon(s.getStreet());
					}
					if (longestStreet.contains(s)) {
						System.out.println("FOUND SOMETHING");
						g2d.setColor(new Color(250, 100, 100));
						g2d.fillPolygon(s.getStreet());
					}
				//}
			}
		}
		
		if (clickedTile != null) {
			g2d.fillPolygon(clickedTile.drawTile());
		}
		if (clickedNode != null) {
			g2d.drawRoundRect(clickedNode.getX() - 10, clickedNode.getY() - 10, 20, 20, 20, 20);
		}
	}
	
	protected void notifieListeners(BoardEvent event) {
		for (BoardListener l: listener) {
			l.boardEventHappened(event);
		}
	}
	
	public CityNode getLocalCityNodeToBuild(CityNode c) {
		CityNode temp = null;
		for (CityNode ct : cities) {
			if (ct.equals(c)) {
				temp = ct;
			}
		}
		return temp;
	}
	
	public StreetNode getLocalStreetNodeToBuild(StreetNode c) {
		StreetNode temp = null;
		for (StreetNode ct : streets) {
			if (ct.equals(c)) {
				temp = ct;
			}
		}
		return temp;
	}
	
	public LandTile getLocalLandTileToChangeThieves (Point p) {
		LandTile temp = null;
		for (LandTile tt : landTiles) {
			System.out.println("Point: " + p.getX() + ", " + p.getY() + " LandtileTestd: " + tt.getX() + ", " + tt.getY());
			if (tt.getX() == p.getX() && tt.getY() == p.getY()) {
				temp = tt;
			}
		}
		return temp;
	}
	
	public boolean  getFirstCityPlaced () {
//		System.out.println(firstTurnCityPlaced);
		return (firstTurnCityPlaced != null);
	}
	
	public List<CityNode> getCities () {
		return new ArrayList<CityNode>(cities);
	}
	
	public void changeActiveDebugPlayer () {
		if (activeDebugPlayer.equals(debug1)) {
			activeDebugPlayer = debug2;
		} else if (activeDebugPlayer.equals(debug2)) {
			activeDebugPlayer = debug1;
		}
	}
	
	public List<LandTile> getLandTiles() {
		return landTiles;
	}
	
	public void addBoardListener(BoardListener listener) {
		this.listener.add(listener);
	}
	
	public void setBoardDebugPhase(boolean setDebug) {
		this.debugPhase = setDebug;
		if (debugPhase) {
			activeDebugPlayer = debug1;
		}
	}
	
	public void setDebugPlacingTargetCity() {
		placingTargetCity = !placingTargetCity;
	}
}
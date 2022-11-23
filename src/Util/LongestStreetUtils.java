package Util;

import java.util.ArrayList;
import java.util.List;

import pieces.StreetNode;
import player.Player;

public class LongestStreetUtils {

    private static List<LongestStreetWrapper> players = new ArrayList<LongestStreetWrapper>();

    public static void updateLongestStreet(StreetNode streetNode) {
        
        StreetCountingStep Step = new StreetCountingStep(streetNode, 0);
        List<StreetNode> visitedStreets = new ArrayList<StreetNode>();
        visitedStreets.add(streetNode);
        StreetCountingStep startingNode = findFarthestStreet(Step, visitedStreets);
        List<StreetNode> visitedStreets2 = new ArrayList<StreetNode>();
        visitedStreets2.add(startingNode.streetNode);
        int length = findLongestStreet(startingNode.streetNode, visitedStreets2, 1);

        LongestStreetWrapper currentPlayer;
        boolean hasLongestStreet = true;
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getPlayer().equals(streetNode.getPiece().getPlayer())) {
                currentPlayer = players.get(i);
                if(length >currentPlayer.getLongestStreet()) {
                    currentPlayer.setLongestStreet(length);
                }
            }

            if(players.get(i).getLongestStreet() > length) {
                hasLongestStreet = false;
            }

        }
        if(hasLongestStreet && length >= 5) {
            streetNode.getPiece().getPlayer().setHastLongestStreet(true);
            for(int i = 0; i < players.size(); i++) {
                if(!streetNode.getPiece().getPlayer().equals(players.get(i).getPlayer())) {
                    players.get(i).getPlayer().setHastLongestStreet(false);
                }
            }
        }
    }

    private static int findLongestStreet(StreetNode streetNode, List<StreetNode> visitedStreets,  int length) {
        List<StreetNode> adjacentStreets = streetNode.getAdjecentStreet();
        int longest = length;
        for(int i = 0; i < adjacentStreets.size(); i++) {
            if(adjacentStreets.get(i).getPiece() != null && adjacentStreets.get(i).getPiece().getPlayer().equals(streetNode.getPiece().getPlayer())) {
                List<StreetNode> visited = new ArrayList<StreetNode>(visitedStreets);
                if(!visited.contains(adjacentStreets.get(i)) && checkValidStep(visited, adjacentStreets.get(i))) {
                    visited.add(adjacentStreets.get(i));
                    int temp = findLongestStreet(adjacentStreets.get(i), visited, length + 1);
                    if(temp > longest) {
                        longest = temp;
                    }
                }
            }
        }

        return longest;
    }

    private static StreetCountingStep findFarthestStreet(StreetCountingStep input, List<StreetNode> visitedStreets) {
        List<StreetNode> adjacentStreets = input.streetNode.getAdjecentStreet();
        int longest = input.countingStep;
        StreetCountingStep farthestStep = input;
        for(int i = 0; i < adjacentStreets.size(); i++) {
            if(adjacentStreets.get(i).getPiece() != null && adjacentStreets.get(i).getPiece().getPlayer().equals(input.streetNode.getPiece().getPlayer())) {
                List<StreetNode> visited = new ArrayList<StreetNode>(visitedStreets);
                if(!visited.contains(adjacentStreets.get(i)) && checkValidStep(visited, adjacentStreets.get(i))) {
                    visited.add(adjacentStreets.get(i));
                    StreetCountingStep nextStep = new StreetCountingStep(adjacentStreets.get(i), input.countingStep + 1);
                    StreetCountingStep temp = findFarthestStreet(nextStep, visited);
                    if(temp.countingStep > longest) {
                        longest = temp.countingStep;
                        farthestStep = temp;
                    }
                }
            }
        }
        return farthestStep;
    }

    /*
     * when all three street nodes surrounding a settlement comtain street pieces of the same player, then all three of them can potentially be counted towards the
     * longest street, because the three streets are adjacent to each other. This method prevents this from happening
     */
    private static boolean checkValidStep(List<StreetNode> predes, StreetNode next) {
        boolean isValid = true;
        if(predes.size() > 1) {
            int size = predes.size();
            StreetNode predes1 = predes.get(size - 1);
            StreetNode predes2 = predes.get(size - 2);

            if(predes1.getY() == predes2.getY()) {
                int y = next.getX();
                if ((y > predes1.getX() && y < predes2.getX()) || (y < predes1.getX() && y > predes2.getX())) {
                    isValid = false;
                }
            } else if(predes1.getY() == next.getY())  {
                int y = predes2.getX();
                if ((y > predes1.getX() && y < next.getX()) || (y < predes1.getX() && y > next.getX())) {
                    isValid = false;
                }
            } else if(predes2.getY() == next.getY()) {
                int y = predes1.getX();
                if ((y > predes2.getX() && y < next.getX()) || (y < predes2.getX() && y > next.getX())) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }
	

    public static void setPlayers(List<Player> allPlayers) {
        for(int i = 0; i < allPlayers.size(); i++) {
            LongestStreetWrapper wrapper = new LongestStreetWrapper(allPlayers.get(i), 1);
            players.add(wrapper);
        }
    }

    private static void setPlayer(Player player) {
        if(players.size() == 0) {
            players.add(new LongestStreetWrapper(player, 1));
        } else {
            boolean isContained = false;
            for(int i = 0; i < players.size(); i++) {
                if(players.get(i).getPlayer().equals(player)) {
                    isContained = true;
                    break;
                }
            }
            if(!isContained) {
                players.add(new LongestStreetWrapper(player, 1));
            }
        }
    }

}
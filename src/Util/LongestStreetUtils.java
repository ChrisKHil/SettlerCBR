package Util;

import java.util.ArrayList;
import java.util.List;

import pieces.CityNode;
import pieces.StreetNode;
import player.Player;
import Util.DistanceUtils;

public class LongestStreetUtils {

    private static List<LongestStreetWrapper> players = new ArrayList<LongestStreetWrapper>();
    private static List<CityNode> towns = new ArrayList<CityNode>();
    private static List<StreetNode> streets = new ArrayList<StreetNode>();

    public static void updateLongestStreet(StreetNode streetNode) {
        if(streetNode != null && streetNode.getPiece() != null) {
            if(!streets.contains(streetNode)) {
                streets.add(streetNode);
            }
            StreetCountingStep Step = new StreetCountingStep(streetNode, 0);
            List<StreetNode> visitedStreets = new ArrayList<StreetNode>();
            visitedStreets.add(streetNode);
            StreetCountingStep startingNode = findFarthestStreet(Step, visitedStreets);
            List<StreetNode> visitedStreets2 = new ArrayList<StreetNode>();
            visitedStreets2.add(startingNode.streetNode);
            int length = findLongestStreet(startingNode.streetNode, visitedStreets2, 1);
    
            setLongestStreet(streetNode.getPiece().getPlayer(), length, false);
            System.out.println("bla");
        }
        
    }

        /*
     * if a settlement is newly built, it needs to be determined wether or not it blocks an existing longest street
     *  of an opponent
     */
    public static void reevaluateLongestStreet(CityNode town) {
        List<StreetNode> adjacentStreets = new ArrayList<StreetNode>();
        for(int i = 0; i < streets.size(); i++) {
            if(!town.getPiece().getPlayer().equals(streets.get(i).getPiece().getPlayer())) {
                if(DistanceUtils.contains(town.getX(), streets.get(i).getX(), town.getY(), streets.get(i).getY(), 55)) {
                    adjacentStreets.add(streets.get(i));
                }
            }
        }
        List<Player> adPlayers = new ArrayList<Player>();
        if(adjacentStreets.size() >= 2) {
            for(int i = 0; i < adjacentStreets.size(); i++) {
                if(!adPlayers.contains(adjacentStreets.get(i).getPiece().getPlayer())) {
                    adPlayers.add(adjacentStreets.get(i).getPiece().getPlayer());
                }
            }
        }
        if(adPlayers.size() == 2) {
            Player opponent = null;
            for(int i = 0; i < adPlayers.size(); i++) {
                if(!town.getPiece().getPlayer().equals(adPlayers.get(i))) {
                    opponent = adPlayers.get(i);
                    break;
                }
            }
            List<StreetNode> visited = new ArrayList<StreetNode>();
            int lengthOne = findLongestStreet(adjacentStreets.get(0),visited , 1);
            List<StreetNode> visited2 = new ArrayList<StreetNode>();
            int lengthTwo = findLongestStreet(adjacentStreets.get(1),visited2 , 1);
            int longest = 0;
            for(int i = 0; i < players.size(); i++) {
                if(opponent.equals(players.get(i).getPlayer())) {
                    longest = players.get(i).getLongestStreet();
                }
            }
            if(lengthOne + lengthTwo >= longest) {
                if(lengthOne > lengthTwo) {
                    longest = lengthOne;
                } else {
                    longest = lengthTwo;
                }
            } else {
                /*
                 * Falls die längste Straße nicht mit der Siedlung verbunden ist und eine längste Handelsstraße
                 * im nicht verbundenen Teil des Straßennetzes existieren könnte wird jede Straße überprüft.
                 * Dies ist sehr ineffizient aber auf die schnelle die praktischste Lösung
                 */
                List<StreetNode> playerStreets = new ArrayList<StreetNode>();
                if(opponent != null) {
                    for(int i = 0; i < streets.size(); i++) {
                        if(streets.get(i).getPiece().getPlayer().equals(opponent)) {
                            playerStreets.add(streets.get(i));
                        }
                    }
                }
                if(playerStreets.size() - lengthOne - lengthTwo > longest) {
                    int temp = 0;
                    for(int i = 0; i < playerStreets.size(); i++) {
                        temp = findLongestStreet(adjacentStreets.get(1),visited2 , 1);
                        if(temp > longest) {
                            longest = temp;
                        }
                    }
                }


            }
            setLongestStreet(opponent, longest, true);
        }
    }

    public static void setPlayers(List<Player> allPlayers) {
        for(int i = 0; i < allPlayers.size(); i++) {
            LongestStreetWrapper wrapper = new LongestStreetWrapper(allPlayers.get(i), 1);
            players.add(wrapper);
        }
    }

    public void setTown(CityNode town) {
        towns.add(town);
    }

        /*
     * This method finds the longest street that is attached to the input street. It only finds the longest street
     * only if the input StreetNode is the first or last street in a possible sequence of streets*/
    private static int findLongestStreet(StreetNode streetNode, List<StreetNode> visitedStreets,  int length) {
        List<StreetNode> adjacentStreets = streetNode.getAdjecentStreet();
        int longest = length;
        for(int i = 0; i < adjacentStreets.size(); i++) {
            if(adjacentStreets.get(i).getPiece() != null && adjacentStreets.get(i).getPiece().getPlayer().equals(streetNode.getPiece().getPlayer())) {
                List<StreetNode> visited = new ArrayList<StreetNode>(visitedStreets);
                if(!visited.contains(adjacentStreets.get(i)) && checkValidStep(visited, adjacentStreets.get(i))) {
                    if(visited.size() >=1) { // && !checkBlockedByOpponent(visited.get(visited.size() - 1), adjacentStreets.get(i))
                        visited.add(adjacentStreets.get(i));
                        int temp = findLongestStreet(adjacentStreets.get(i), visited, length + 1);
                        if(temp > longest) {
                            longest = temp;
                        }

                    } else {
                        visited.add(adjacentStreets.get(i));
                        int temp = findLongestStreet(adjacentStreets.get(i), visited, length + 1);
                        if(temp > longest) {
                        longest = temp;
                        }
                    }
                }
            }
        }

        return longest;
    }

    /*
     * This method finds the Street node that is furthest away from the newly played street. This is necessary as a first 
     * step to find the longest street.
     * The algorithm is identical to findLongestStreet except for the return type. This is not ideal but it does do what
     * it is supposed to do
     */
    private static StreetCountingStep findFarthestStreet(StreetCountingStep input, List<StreetNode> visitedStreets) {
        List<StreetNode> adjacentStreets = input.streetNode.getAdjecentStreet();
        int longest = input.countingStep;
        StreetCountingStep farthestStep = input;
        for(int i = 0; i < adjacentStreets.size(); i++) {
            if(adjacentStreets.get(i).getPiece() != null && adjacentStreets.get(i).getPiece().getPlayer().equals(input.streetNode.getPiece().getPlayer())) {
                List<StreetNode> visited = new ArrayList<StreetNode>(visitedStreets);
                if(!visited.contains(adjacentStreets.get(i)) && checkValidStep(visited, adjacentStreets.get(i))) {
                    if(visited.size() >=1 ) { //&& !checkBlockedByOpponent(visited.get(visited.size() - 1), adjacentStreets.get(i))
                        visited.add(adjacentStreets.get(i));
                        StreetCountingStep nextStep = new StreetCountingStep(adjacentStreets.get(i), input.countingStep + 1);
                        StreetCountingStep temp = findFarthestStreet(nextStep, visited);
                        if(temp.countingStep > longest) {
                            longest = temp.countingStep;
                            farthestStep = temp;
                        }
                    } else {
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

    private static void setLongestStreet(Player player, int length, boolean reevaluate) {
        LongestStreetWrapper currentPlayer;
        boolean hasLongestStreet = true;
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getPlayer().equals(player)) {
                currentPlayer = players.get(i);
                if(length >currentPlayer.getLongestStreet() && !reevaluate) {
                    currentPlayer.setLongestStreet(length);
                } else if(reevaluate) {
                    currentPlayer.setLongestStreet(length);
                }
            }

            if(players.get(i).getLongestStreet() > length) {
                hasLongestStreet = false;
            }

        }
        if(hasLongestStreet && length >= 5) {
            player.setHastLongestStreet(true);
            for(int i = 0; i < players.size(); i++) {
                if(!player.equals(players.get(i).getPlayer())) {
                    players.get(i).getPlayer().setHastLongestStreet(false);
                }
            }
        }
    }

    /*
     * The longest street can be blocked by a settlement of the opponent in between ones street pieces.
     * This method checks if any opponent settlement is blocking the path
     */
    private static boolean checkBlockedByOpponent(StreetNode predes, StreetNode next) {
        boolean isBlocked = false;
        int x1 = predes.getX();
        int x2 = next.getX();
        int y1 = predes.getY();
        int y2 = next.getY();
        // double distance = Math.sqrt((Math.pow((x1- x2), 2) + Math.pow((y1 - y2),2)));
        // int distanceX = x1 - x2;
        // int distanceY = y1 - y2;
        int xCenter = x2 + ((x1 - x2)/2);
        int yCenter = y2 + ((y1 - y2)/2);
        System.out.println("ss");
        for(int i = 0; i < towns.size(); i++) {
            if(towns.get(i).getPiece().getPlayer().equals(next.getPiece().getPlayer())) {
                if (DistanceUtils.contains(xCenter, towns.get(i).getX(), yCenter, towns.get(i).getY(), 40)) {
                    isBlocked = true;
                    break;
                }
            }

        }
        return isBlocked;
    }

    // private static void setPlayer(Player player) {
    //     if(players.size() == 0) {
    //         players.add(new LongestStreetWrapper(player, 1));
    //     } else {
    //         boolean isContained = false;
    //         for(int i = 0; i < players.size(); i++) {
    //             if(players.get(i).getPlayer().equals(player)) {
    //                 isContained = true;
    //                 break;
    //             }
    //         }
    //         if(!isContained) {
    //             players.add(new LongestStreetWrapper(player, 1));
    //         }
    //     }
    // }

}
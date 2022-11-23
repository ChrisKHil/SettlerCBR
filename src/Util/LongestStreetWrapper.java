package Util;

import player.Player;

public class LongestStreetWrapper {

    private Player player;
    private int longestStreet;

    public LongestStreetWrapper(Player player, int length) {
        this.player = player;
        this.longestStreet = length;
    }



    public void setLongestStreet(int length) {
        this.longestStreet = length;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getLongestStreet() {
        return this.longestStreet;
    }
}
package dg.generator.dungeon;

import java.util.HashSet;

public class SafeRoom {

    public final HashSet<Coordinate> safeRoomPlaces;
    public final HashSet<Coordinate> safeRoomDoors;
    public Coordinate npc;

    public SafeRoom() {
        this.safeRoomPlaces = new HashSet<>();
        this.safeRoomDoors = new HashSet<>();
        this.npc = null;
    }
}

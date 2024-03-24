package dg.generator.dungeon;

import java.util.*;

public class MapTransformation {

    private final static int margin=3;
    private final static int trials=3;
    private final static int doorRange=7;
    private final static double entriesRange=0.7;
    private final static int[][] rotationTable={{-1,0},{0,1},{1,0},{0,-1}};
    private final static int minVaultSize=9;
    private final static int maxVaultSize=60;

    private static Random random;

    public static void setRandom(Random r){
        random=r;
    }

    public static void drawBorder(Map map){
        for(int y=1;y<map.getHeight()-1;y++){
            for(int x=1;x<map.getWidth()-1;x++){
                if((map.getTerrain(x-1,y)==Place.FLOOR ||map.getTerrain(x+1,y)==Place.FLOOR ||map.getTerrain(x,y-1)==Place.FLOOR ||map.getTerrain(x,y+1)==Place.FLOOR) && map.getTerrain(x,y)!=Place.FLOOR ){
                    try {
                        map.setTerrain(x, y,Place.WALL);
                    }catch(Exception ignore){}
                }
            }
        }
    }

    //Always drew two door, entry and exit
    public static void drawEntries(Map map){
        ArrayList<Coordinate> possibleLocation=new ArrayList<>();
        for(int y=margin-1;y<map.getHeight()-margin;y++){
            for(int x=margin-1;x<map.getWidth()-margin;x++){
                if(canBePossibleLocationNextToWall(x,y,map) && checkIfNotBlockingNearbyPassage(x,y,map))
                    possibleLocation.add(new Coordinate(x,y));
            }
        }
        int range=(int)(entriesRange * map.getWidth());
        int count=trials;
        while(true){
            Coordinate entry=possibleLocation.get(random.nextInt(possibleLocation.size()));
            Coordinate exit=null;
            while(count>0){
                exit=possibleLocation.get(random.nextInt(possibleLocation.size()));
                int distance=(int)Math.sqrt((entry.x- exit.x)*(entry.x- exit.x)+(entry.y- exit.y)*(entry.y- exit.y));
                if(distance>=range)
                    break;
                count--;
            }
            if(count>0){
                try {
                    map.setTerrain(entry.x, entry.y, Place.ENTRIES);
                    map.setTerrain(exit.x, exit.y, Place.ENTRIES);
                    map.setEntries(entry,exit);
                }catch(Exception ignore){}
                break;
            }
            count=trials;
        }
    }

    public static void drawTreasure(int TreasureCount, Map map) throws Exception {
        //Create list of points that can be for treasure location
        //It must be done, after resize
        ArrayList<Coordinate> possibleLocation=new ArrayList<>();
        for(int y=margin-1;y<map.getHeight()-margin;y++){
            for(int x=margin-1;x<map.getWidth()-margin;x++){
                if(canBePossibleLocationNextToWall(x,y,map))
                    possibleLocation.add(new Coordinate(x,y));
            }
        }
        if(possibleLocation.size()<TreasureCount)
            throw new Exception("Cannot generate that many treasure!");
        while(TreasureCount>0 && possibleLocation.size()>0){
            int i=random.nextInt(possibleLocation.size());
            Coordinate coordinate=possibleLocation.get(i);
            if(checkIfNotBlockingNearbyPassage(coordinate.x,coordinate.y,map)) {
                map.setTerrain(coordinate.x,coordinate.y,Place.TREASURE);
                TreasureCount--;
            }
            possibleLocation.remove(i);
        }
    }

    public static void drawEnemies(int EnemiesCount,Map map) throws Exception {
        //Create list of points that can be for treasure location
        //It must be done, after resize
        ArrayList<Coordinate> possibleLocation=new ArrayList<>();
        ArrayList<Coordinate> entries=new ArrayList<>();
        for(int y=margin-1;y<map.getHeight()-margin;y++){
            for(int x=margin-1;x<map.getWidth()-margin;x++){
                if(map.getTerrain(x,y)==Place.FLOOR)
                    possibleLocation.add(new Coordinate(x,y));
                else if(map.getTerrain(x,y)==Place.ENTRIES)
                    entries.add(new Coordinate(x,y));
            }
        }
        if(possibleLocation.size()<EnemiesCount)
            throw new Exception("Cannot generate that many enemies!");
        while(EnemiesCount>0 && possibleLocation.size()>0){
            int i=random.nextInt(possibleLocation.size());
            Coordinate coordinate=possibleLocation.get(i);
            if(canBeEnemyLocation(coordinate.x,coordinate.y,entries)) {
                map.setTerrain(coordinate.x,coordinate.y,Place.ENEMY);
                EnemiesCount--;
            }
            possibleLocation.remove(i);
        }
    }

    public static void drawDoorAndKey(Map map) {
        //Create list of points that can be for door location
        //It must be done, after resize
        ArrayList<Coordinate> possibleLocationForDoor=new ArrayList<>();
        for(int y=margin-1;y<map.getHeight()-margin;y++){
            for(int x=margin-1;x<map.getWidth()-margin;x++){
                if(map.getTerrain(x,y)==Place.FLOOR && isBlockingPassage(map,x,y))
                    possibleLocationForDoor.add(new Coordinate(x,y));
            }
        }
        //Seek for door place
        while(!possibleLocationForDoor.isEmpty()){
            Coordinate c=possibleLocationForDoor.get(random.nextInt(possibleLocationForDoor.size()));
            //Block potential passage
            map.setTerrain(c.x, c.y,Place.WALL);

            ArrayList<HashMap<String,Coordinate>> coordinates=checkIfBlockingGlobalPassage(c.x,c.y,map);

            //If not blocking passage, and have size in interval
            if(coordinates==null || coordinates.get(1).size()<minVaultSize || coordinates.get(1).size()>maxVaultSize){
                map.setTerrain(c.x, c.y,Place.FLOOR);
                possibleLocationForDoor.remove(c);
            }else{
                //Set door and roll up Key and treasure
                map.setTerrain(c.x, c.y,Place.DOOR);
                try {
                    drawKeyAndTreasure(coordinates.get(0),coordinates.get(1), map);
                }
                //Cannot make key
                catch(Exception e){
                    map.setTerrain(c.x, c.y,Place.FLOOR);
                    possibleLocationForDoor.remove(c);
                    continue;
                }
                break;
            }
        }
    }

    private static void drawKeyAndTreasure(HashMap<String,Coordinate> entrySideCoordinates,HashMap<String,Coordinate> insideCoordinates,Map map) throws Exception {
        int keyCount=1;
        int treasureCount=3;
        //Do it until there is no more location or key and treasures is not deployed
        ArrayList<Coordinate> keyCoordinates= new ArrayList<>(entrySideCoordinates.values());
        while(!keyCoordinates.isEmpty() && keyCount>0){
            Coordinate c=keyCoordinates.get(random.nextInt(keyCoordinates.size()));
            map.setTerrain(c.x,c.y,Place.KEY);
            keyCount--;
            keyCoordinates.remove(c);
        }
        if(keyCount>0){
            throw new Exception("Key is not deployed!");
        }
        ArrayList<Coordinate> treasureCoordinates= new ArrayList<>(insideCoordinates.values());
        while(!treasureCoordinates.isEmpty() && treasureCount>0){
            Coordinate c=treasureCoordinates.get(random.nextInt(treasureCoordinates.size()));
            //Coordinates for treasure
            if(canBePossibleLocationNextToWall(c.x,c.y,map) && checkIfNotBlockingNearbyPassage(c.x,c.y,map)) {
                map.setTerrain(c.x, c.y, Place.TREASURE);
                treasureCount--;
            }
            treasureCoordinates.remove(c);
        }
    }


    //Check if distance to door is longer than see range
    private static boolean canBeEnemyLocation(int x,int y, ArrayList<Coordinate> entries){
        for(Coordinate entry: entries){
            int distance=(int)Math.sqrt((x-entry.x)*(x-entry.x)+(y-entry.y)*(y-entry.y));
            if(distance<doorRange){
                return false;
            }
        }
        return true;
    }

    private static boolean isBlockingPassage(Map map,int x,int y){
        if(map.getTerrain(x,y-1)==Place.WALL && map.getTerrain(x,y+1)==Place.WALL && map.getTerrain(x-1,y)==Place.FLOOR && map.getTerrain(x+1,y)==Place.FLOOR)
            return true;
        if(map.getTerrain(x,y-1)==Place.FLOOR && map.getTerrain(x,y+1)==Place.FLOOR && map.getTerrain(x-1,y)==Place.WALL && map.getTerrain(x+1,y)==Place.WALL)
            return true;
        return false;
    }

    private static boolean canBePossibleLocationNextToWall(int x, int y, Map map){
        //Is space free
        if(map.getTerrain(x,y)!=Place.FLOOR)
            return false;
        if(checkNeighbourhood(x,y,map))
            return false;
        int count=0;    //Count Walls
        if(map.getTerrain(x,y-1)==Place.WALL)
            count++;
        if(map.getTerrain(x-1,y)==Place.WALL)
            count++;
        if(map.getTerrain(x,y+1)==Place.WALL)
            count++;
        if(map.getTerrain(x+1,y)==Place.WALL)
            count++;
        return count>0 && count<4;
    }

    private static boolean checkIfNotBlockingNearbyPassage(int x, int y, Map map){
        if(checkNeighbourhood(x,y,map)){
            return false;
        }
        //Maps neighbours
        boolean[][] freeSpace={
                {map.getTerrain(x-1,y-1)==Place.FLOOR,map.getTerrain(x,y-1)==Place.FLOOR,map.getTerrain(x+1,y-1)==Place.FLOOR},
                {map.getTerrain(x-1,y)==Place.FLOOR,false,map.getTerrain(x+1,y)==Place.FLOOR},
                {map.getTerrain(x-1,y+1)==Place.FLOOR,map.getTerrain(x,y+1)==Place.FLOOR,map.getTerrain(x+1,y+1)==Place.FLOOR}
        };
        //Count free space and select starting coordinates
        int freeSpaceCount=0;
        Coordinate c=null;
        //y->i x->j
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(freeSpace[i][j]){
                    freeSpaceCount++;
                    c=new Coordinate(j,i);
                }
            }
        }

        if(c==null)
            return false;

        Stack<Coordinate> coords=new Stack<>();
        coords.add(c);
        Stack<Integer> rotation=new Stack<>();
        rotation.add(0);
        //Places that are checked if there are connected
        boolean[][] checkedSpace=new boolean[3][3];
        checkedSpace[c.y][c.x]=true;
        int counter=1;
        //Deep search for checking connection
        while(!coords.empty()){
            c=coords.pop();
            int rot=rotation.pop();
            if(rot!=3) {
                rotation.add(rot+1);
                coords.add(c);
            }
            int newX,newY;
            newY=c.y+rotationTable[rot][0];
            newX=c.x+rotationTable[rot][1];
            if(newX>=0 && newX<3 && newY>=0 && newY<3){
                if(freeSpace[newY][newX] && !checkedSpace[newY][newX]){
                    checkedSpace[newY][newX]=true;
                    counter++;
                    coords.add(new Coordinate(newX,newY));
                    rotation.add(0);
                }
            }
        }
        //Check if all spaces are achievable
        return freeSpaceCount==counter;
    }

    private static ArrayList<HashMap<String,Coordinate>> checkIfBlockingGlobalPassage(int x, int y, Map map){
        Coordinate coordinate1,coordinate2;
        Coordinate entry=map.getEntries()[0];

        if(map.getTerrain(x-1,y)==Place.FLOOR && map.getTerrain(x+1,y)==Place.FLOOR){
            coordinate1=new Coordinate(x-1,y);
            coordinate2=new Coordinate(x+1,y);
        }else{
            coordinate1=new Coordinate(x,y-1);
            coordinate2=new Coordinate(x,y+1);
        }
        HashMap<String,Coordinate> checkedCoordinates1= breadthSearchAlgorithm(coordinate1,entry,1,map);
        //is separable
        if(checkedCoordinates1.containsKey(coordinate2.x+";"+coordinate2.y)){
            return null;
        }
        HashMap<String,Coordinate> checkedCoordinates2= breadthSearchAlgorithm(coordinate2,entry,2,map);

        //[0]-> is entry, [1] -> is inside;
        ArrayList<HashMap<String,Coordinate>> orderedCoordinates=new ArrayList<>();
        //entrySide==1
        if(checkedCoordinates1.containsKey(entry.x+";"+entry.y)){
            orderedCoordinates.add(checkedCoordinates1);
            orderedCoordinates.add(checkedCoordinates2);
        }
        //entrySide==2
        else if (checkedCoordinates2.containsKey(entry.x+";"+entry.y)){
            orderedCoordinates.add(checkedCoordinates2);
            orderedCoordinates.add(checkedCoordinates1);
        }
        //entrySide==0
        else{
            return null;
        }

        return orderedCoordinates;
    }

    private static HashMap<String,Coordinate> breadthSearchAlgorithm(Coordinate start, Coordinate entry, int side, Map map){
        HashMap<String,Coordinate> checkedCoordinates=new HashMap<>();
        ArrayList<Coordinate> coordinates;
        ArrayList<Coordinate> nextCoordinates=new ArrayList<>();
        nextCoordinates.add(start);
        do{
            coordinates=nextCoordinates;
            nextCoordinates=new ArrayList<>();
            for(Coordinate c:coordinates){
                for(int[] rot:rotationTable){
                    Coordinate coordinate=new Coordinate(c.x+rot[0],c.y+rot[1]);
                    String key=coordinate.x+";"+coordinate.y;
                    if(!checkedCoordinates.containsKey(key)
                            && (map.getTerrain(coordinate.x,coordinate.y)==Place.FLOOR || map.getTerrain(coordinate.x,coordinate.y)==Place.ENEMY || map.getTerrain(coordinate.x,coordinate.y)==Place.ENTRIES || map.getTerrain(coordinate.x,coordinate.y)==Place.KEY)){
                        checkedCoordinates.put(key,coordinate);
                        nextCoordinates.add(coordinate);
                    }
                }
            }
        }while(!nextCoordinates.isEmpty());
        return checkedCoordinates;
    }

    private static boolean checkNeighbourhood(int x,int y,Map map){
        //Check if something is in neighbourhood
        int[][] positionToCheck={{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        for(int[] mod:positionToCheck)
            if(map.getTerrain(x+mod[1],y+mod[0])!=Place.FLOOR && map.getTerrain(x+mod[1],y+mod[0])!=Place.WALL && map.getTerrain(x+mod[1],y+mod[0])!=Place.VOID)
                return true;
        return false;
    }
}

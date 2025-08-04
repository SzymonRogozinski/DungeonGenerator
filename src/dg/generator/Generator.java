package dg.generator;

import dg.generator.dungeon.Map;
import dg.generator.dungeon.MapTransformation;
import dg.generator.dungeon.algorithm.GeneratingAlgorithm;
import dg.generator.dungeon.algorithm.SimpleAntAlgorithm;
import dg.generator.dungeon.algorithm.TunelAntAlgorithm;
import dg.generator.dungeon.algorithm.WebOfRoomsAlgorithm;

import java.rmi.UnexpectedException;
import java.util.Random;

public class Generator {
    public final static int CAVE_ALGORITHM=0;
    public final static int MINE_ALGORITHM=1;
    public final static int DUNGEON_ALGORITHM=2;
    private static final double DEFAULT_DENSE=0.3;

    private static Random random=new Random();
    private static double dense= DEFAULT_DENSE;

    public static Map generateDungeon(int selectedAlgorithm,int width,int height,int size,int enemies, int treasures, int vaults, int safeRooms) throws UnexpectedException {
        GeneratingAlgorithm genAlg;
        switch(selectedAlgorithm){
            case CAVE_ALGORITHM -> genAlg=new SimpleAntAlgorithm();
            case MINE_ALGORITHM -> genAlg=new TunelAntAlgorithm();
            case DUNGEON_ALGORITHM -> genAlg=new WebOfRoomsAlgorithm();
            default -> throw new IllegalArgumentException("This algorithm don't exist!");
        }
        int counter=3;
        Map map=null;
        //Base map generating
        while(counter>0 && map==null){
            map=generate(genAlg,width,height,size);
            counter--;
        }
        if(map==null)
            throw new UnexpectedException("Ups, generation failed! Try other parameters!");
        //Adding stuff
        int[] limits=genAlg.getSize();
        map.resize(limits[0],limits[1],limits[2],limits[3]);
        MapTransformation.setRandom(random);
        MapTransformation.drawBorder(map);
        for(int i=0;i<safeRooms;i++)
            MapTransformation.drawSafeRoom(map,selectedAlgorithm==DUNGEON_ALGORITHM);
        MapTransformation.drawEntries(map);
        for(int i=0;i<vaults;i++)
            MapTransformation.drawDoorAndKey(map);
        try {
            MapTransformation.drawTreasure(treasures, map);
            MapTransformation.drawEnemies(enemies, map);
        }catch (Exception e){
            throw new IllegalArgumentException("Dungeon is too small for this many treasures/enemies");
        }
        return map;
    }

    public static void setRandom(Random r){
        random=r;
    }

    public static void setRandom(int seed){
        random=new Random(seed);
    }

    public static void setDense(double d){
        dense=d;
    }

    private static Map generate(GeneratingAlgorithm genAlg, int width,int height,int size){
        Map map=new Map(width,height);
        try {
            genAlg.setStart(size, random, map, dense);
        }catch (UnexpectedException e){
            throw new IllegalArgumentException("Size is to small for this dimensions!");
        }
        boolean isEmpty;
        do{
            isEmpty=genAlg.generate();
        }while (genAlg.getLimit()>0 && !isEmpty);

        return genAlg.getLimit()<=0?map:null;
    }

}

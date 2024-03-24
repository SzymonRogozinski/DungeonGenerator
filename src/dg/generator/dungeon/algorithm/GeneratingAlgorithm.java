package dg.generator.dungeon.algorithm;

import dg.generator.dungeon.Map;

import java.rmi.UnexpectedException;
import java.util.Random;

public interface GeneratingAlgorithm {

    public void setStart(int limit, Random random, Map reference, double dense) throws UnexpectedException;
    public int getLimit();
    public boolean generate();
    public int[] getSize();
}

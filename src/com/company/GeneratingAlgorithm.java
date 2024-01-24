package com.company;

import java.util.Random;

public interface GeneratingAlgorithm {

    public void setStart(int limit, Random random,Map reference,double dense);
    public int getLimit();
    public boolean generate();
}

package com.company;

import java.util.Random;

public class GeneratingThread extends Thread{

    private final double dense;
    private static final double DEFAULT_DENSE = 0.3;
    private static final int DEFAULT_DELAY = 100;
    private int delay=DEFAULT_DELAY;
    private Random random;
    private MainPanel panel;
    private GeneratingAlgorithm genAlg;


    public GeneratingThread(int limit, Map reference, MainPanel panel, int algType) {
        this.random=new Random();
        this.panel=panel;
        this.dense= DEFAULT_DENSE;
        if(algType==1){
            genAlg=new SimpleAntAlgorithm();
        }else if(algType==2){
            genAlg=new TunelAntAlgorithm();
        }else if(algType==3){
            genAlg=new WebOfRoomsAlgorithm();
        }
        genAlg.setStart(limit, random,reference,dense);
    }

    public GeneratingThread(int limit, Map reference, MainPanel panel, int algType, int seed) {
        this.random=new Random(seed);
        this.panel=panel;
        this.dense= DEFAULT_DENSE;
        if(algType==1){
            genAlg=new SimpleAntAlgorithm();
        }else if(algType==2){
            genAlg=new TunelAntAlgorithm();
        }else if(algType==3){
            genAlg=new WebOfRoomsAlgorithm();
        }
        genAlg.setStart(limit,random,reference,dense);

    }

    public synchronized void ping(){
        notify();
    }

    @Override
    public void run(){
        generating();
        panel.algorithmEnded();
    }

    public void changeDelay(){
        delay=delay==0?DEFAULT_DELAY:0;
    }

    public int[] getSizesFromAlgorithm(){
        return  genAlg.getSize();
    }

    public synchronized void generating(){
        boolean isEmpty;
        do{
            isEmpty=genAlg.generate();
            //resetEkanu
            panel.resetScreen();
            if(panel.state.isStopped()){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (genAlg.getLimit()>0 && !isEmpty);
        if(genAlg.getLimit()>0)
            System.err.println("Nie wygenerowano pełnego lochu!");
    }

}

package com.company;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GenereatingThread extends Thread{

    private int limit;
    private final double dense;
    private static final double DEFAULT_DENSE = 0.3;
    private static final int DEFAULT_DELAY = 100;
    private Map reference;
    private Random random;
    private MainPanel panel;
    private Stack<Ant> stack;
    private Stack<Ant> newStack;


    public GenereatingThread(int limit,Map reference,MainPanel panel) {
        this.limit = limit;
        this.reference=reference;
        this.random=new Random();
        this.panel=panel;
        this.dense= DEFAULT_DENSE;
    }

    public GenereatingThread(int limit,Map reference,MainPanel panel,int seed) {
        this.limit = limit;
        this.reference=reference;
        this.random=new Random(seed);
        this.panel=panel;
        this.dense= DEFAULT_DENSE;
    }

    public synchronized void ping(){
        notify();
    }

    @Override
    public void run(){
        //Inicjalizacja stosów
        stack=new Stack<>();
        //Dodanie pierwszego elementu
        stack.add(new Ant(random.nextInt(reference.getWidth()/3,2*reference.getWidth())/3,random.nextInt(reference.getHeight()/3,2*reference.getHeight()/3)));
        generating();
    }

    public synchronized void generating(){
        do{
            newStack=new Stack<>();
            while (!stack.empty()) {
                try {
                    reference.setTerrain(stack.peek().getX(), stack.peek().getY());
                }catch (Map.AlreadyTrueException e){
                    stack.pop();
                    continue;
                }
                limit--;
                //Sprawdzenie ile sąsiadów
                ArrayList<Ant> neighbour = howManyNeighbour(stack.pop());
                //Dorzucenie do nowego stosu
                if(neighbour.size()==0){
                }else if (neighbour.size() < 3 || random.nextDouble()>dense) {
                    newStack.add(neighbour.get(random.nextInt(neighbour.size())));
                } else {
                    newStack.add(neighbour.get(random.nextInt(neighbour.size())));
                    newStack.add(neighbour.get(random.nextInt(neighbour.size())));
                }
            }
            //resetEkanu
            panel.resetScreen();
            stack=newStack;
            if(panel.isStop()){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(DEFAULT_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (limit>0 && !stack.empty());
        if(limit>0)
            System.err.println("Nie wygenerowano pełnego lochu!");
    }


    private ArrayList<Ant> howManyNeighbour(Ant p){
        ArrayList<Ant> n=new ArrayList<>();
        //Góra
        if(p.getY()>0 && !reference.getTerrain(p.getX(),p.getY()-1)){
            n.add(new Ant(p.getX(),p.getY()-1));
        }
        //Lewo
        if(p.getX()>0 && !reference.getTerrain(p.getX()-1,p.getY())){
            n.add(new Ant(p.getX()-1,p.getY()));
        }
        //Prawo
        if(p.getX()<reference.getWidth()-1 && !reference.getTerrain(p.getX()+1,p.getY())){
            n.add(new Ant(p.getX()+1,p.getY()));
        }
        //Dół
        if(p.getY()< reference.getHeight()-1 && !reference.getTerrain(p.getX(),p.getY()+1)) {
            n.add(new Ant(p.getX(), p.getY() + 1));
        }
        return  n;
    }

}

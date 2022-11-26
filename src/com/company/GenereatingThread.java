package com.company;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GenereatingThread extends Thread{

    private int limit;
    private Map reference;
    private Random random;
    private MainPanel panel;
    private Stack<Pair> stack;
    private Stack<Pair> newStack;


    public GenereatingThread(int limit,Map reference,MainPanel panel) {
        this.limit = limit;
        this.reference=reference;
        this.random=new Random();
        this.panel=panel;
    }

    public GenereatingThread(int limit,Map reference,MainPanel panel,int seed) {
        this.limit = limit;
        this.reference=reference;
        this.random=new Random(seed);
        this.panel=panel;
    }

    public synchronized void ping(){
        notify();
    }

    @Override
    public void run(){
        //Inicjalizacja stosów
        stack=new Stack<>();
        //Dodanie pierwszego elementu
        stack.add(new Pair(random.nextInt(0,reference.getWidth()),random.nextInt(0,reference.getHeight())));
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
                ArrayList<Pair> neighbour = howManyNeighbour(stack.pop());
                //Dorzucenie do nowego stosu
                if(neighbour.size()==0){
                }else if (neighbour.size() <3) {
                    newStack.add(neighbour.get(random.nextInt(neighbour.size())));
                } else if(random.nextDouble()<0.2) {
                    newStack.add(neighbour.get(random.nextInt(neighbour.size())));
                    newStack.add(neighbour.get(random.nextInt(neighbour.size())));
                }else{
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
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (limit>0);
    }


    private ArrayList<Pair> howManyNeighbour(Pair p){
        ArrayList<Pair> n=new ArrayList<>();
        //Góra
        if(p.getY()>0 && !reference.getTerrain(p.getX(),p.getY()-1)){
            n.add(new Pair(p.getX(),p.getY()-1));
        }
        //Lewo
        if(p.getX()>0 && !reference.getTerrain(p.getX()-1,p.getY())){
            n.add(new Pair(p.getX()-1,p.getY()));
        }
        //Prawo
        if(p.getX()<reference.getWidth()-1 && !reference.getTerrain(p.getX()+1,p.getY())){
            n.add(new Pair(p.getX()+1,p.getY()));
        }
        //Dół
        if(p.getY()< reference.getHeight()-1 && !reference.getTerrain(p.getX(),p.getY()+1)) {
            n.add(new Pair(p.getX(), p.getY() + 1));
        }
        return  n;
    }

}

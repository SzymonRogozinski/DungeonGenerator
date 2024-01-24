package com.company;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class SimpleAntAlgorithm implements GeneratingAlgorithm{

    private int limit;
    private Stack<Ant> stack;
    private Stack<Ant> newStack;
    private Random random;
    private Map reference;
    private double dense;

    @Override
    public void setStart(int limit,Random random, Map reference,double dense){
        this.limit=limit;
        stack=new Stack<>();
        stack.add(new Ant(random.nextInt(reference.getWidth()/3,2*reference.getWidth())/3,random.nextInt(reference.getHeight()/3,2*reference.getHeight()/3)));
        this.random=random;
        this.reference=reference;
        this.dense=dense;
    }

    @Override
    public int getLimit(){
        return limit;
    }

    @Override
    public boolean generate(){
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
            ArrayList<Ant> neighbour = howManyNeighbour(stack.pop(),reference);
            //Dorzucenie do nowego stosu
            if(neighbour.size()==0){
            }else if (neighbour.size() < 3 || random.nextDouble()>dense) {
                newStack.add(neighbour.get(random.nextInt(neighbour.size())));
            } else {
                newStack.add(neighbour.get(random.nextInt(neighbour.size())));
                newStack.add(neighbour.get(random.nextInt(neighbour.size())));
            }
        }
        stack=newStack;
        return stack.empty();
    }

    private ArrayList<Ant> howManyNeighbour(Ant p,Map reference){
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

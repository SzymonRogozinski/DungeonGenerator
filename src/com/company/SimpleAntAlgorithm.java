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
    private int maxX,minX,maxY,minY;

    @Override
    public void setStart(int limit,Random random, Map reference,double dense){
        this.limit=limit;
        stack=new Stack<>();
        stack.add(new Ant(random.nextInt(reference.getWidth()/3,2*reference.getWidth())/3,random.nextInt(reference.getHeight()/3,2*reference.getHeight()/3)));
        //init sizes
        maxX=stack.peek().getX();
        minX=maxX;
        maxY=stack.peek().getY();
        minY=maxY;
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
                Ant nextAnt=neighbour.get(random.nextInt(neighbour.size()));
                checkSizes(nextAnt);
                newStack.add(nextAnt);
            } else {
                Ant nextAnt=neighbour.get(random.nextInt(neighbour.size()));
                checkSizes(nextAnt);
                newStack.add(nextAnt);
                nextAnt=neighbour.get(random.nextInt(neighbour.size()));
                checkSizes(nextAnt);
                newStack.add(nextAnt);
            }
        }
        stack=newStack;
        return stack.empty();
    }

    private void checkSizes(Ant ant){
        if(ant.getX()>maxX){
            maxX=ant.getX();
        } else if (ant.getX()<minX) {
            minX=ant.getX();
        }
        if(ant.getY()>maxY){
            maxY=ant.getY();
        } else if (ant.getY()<minY) {
            minY=ant.getY();
        }
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

    @Override
    public int[] getSize(){
        return new int[]{maxX,minX,maxY,minY};
    }
}

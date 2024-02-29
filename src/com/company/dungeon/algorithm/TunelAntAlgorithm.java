package com.company.dungeon.algorithm;

import com.company.dungeon.Map;

import java.util.Random;
import java.util.Stack;

public class TunelAntAlgorithm implements GeneratingAlgorithm {

    private int limit;
    private Stack<SmartAnt> stack;
    private Stack<SmartAnt> newStack;
    private Random random;
    private Map reference;
    private double dense;
    private int maxX,minX,maxY,minY;

    @Override
    public void setStart(int limit,Random random, Map reference,double dense){
        this.limit=limit;
        stack=new Stack<>();
        stack.add(new SmartAnt(random.nextInt(reference.getWidth()/3,2*reference.getWidth())/3,random.nextInt(reference.getHeight()/3,2*reference.getHeight()/3)));
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
                checkSizes(stack.peek());
            }catch (Map.AlreadyTrueException  | IllegalArgumentException e){
                stack.pop();
                continue;
            }
            limit--;
            SmartAnt ant=stack.pop();
            SmartAnt nextAnt=ant.getNextAnt(reference,random);
            if(nextAnt==null){
            }else if (random.nextDouble()>dense) {
                newStack.add(nextAnt);
            } else {
                newStack.add(nextAnt);
                nextAnt=ant.getNextRandomAnt(reference,random);
                if(nextAnt!=null){
                    newStack.add(nextAnt);
                }
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

    @Override
    public int[] getSize(){
        return new int[]{maxX,minX,maxY,minY};
    }
}


package com.company;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private DungeonPanel dp;
    private ControlPanel con;
    private Map map;
    private GenereatingThread genereate;

    private boolean stop;

    public MainPanel() {
        map=new Map(100,100);
        stop=true;
        genereate=new GenereatingThread(1000,map,this);

        this.setPreferredSize(new Dimension(Main.DRAW_SIZE+Main.CONTROL_SIZE,Main.DRAW_SIZE));
        this.setLayout(null);
        dp=new DungeonPanel();
        this.add(dp);
        con=new ControlPanel();
        this.add(con);
    }

    public void resetScreen(){
        this.repaint();
        this.revalidate();
    }

    public boolean isStop() {
        return stop;
    }

    public synchronized void continueMode(JButton stopButton,JButton nextButton,JButton startButton,JButton restartButton){
        if(genereate.isAlive()) {
            this.stop = false;
            nextButton.setEnabled(false);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            genereate.ping();
        }else{
            this.stop=false;
            stopButton.setEnabled(true);
            nextButton.setEnabled(false);
            startButton.setEnabled(false);
            restartButton.setEnabled(true);
            genereate.start();
        }

    }

    public synchronized void restart(JButton stopButton,JButton nextButton,JButton startButton,JButton restartButton){
        stop=true;
        //genereate.interrupt();
        genereate.stop();

        this.map=new Map(100,100);
        this.genereate=new GenereatingThread(1000,this.map,this);
        resetScreen();

        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        nextButton.setEnabled(false);
        restartButton.setEnabled(false);
    }

    public synchronized void stop(JButton stopButton,JButton nextButton,JButton startButton){
        stop=true;
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        nextButton.setEnabled(true);
    }

    public synchronized void next(){
        genereate.ping();
    }

    private class ControlPanel extends JPanel {

        private JButton start;
        private JButton stop;
        private JButton next;
        private JButton restart;

        public ControlPanel(){
            super();

            this.setPreferredSize(new Dimension(Main.CONTROL_SIZE,Main.DRAW_SIZE));
            this.setBounds(Main.DRAW_SIZE,0,Main.DRAW_SIZE,Main.DRAW_SIZE);
            this.setBackground(Color.RED);
            this.setLayout(null);

            start=new JButton("Start");
            start.setBounds(50,100,100,25);
            start.addActionListener(e->{continueMode(stop,next,start,restart);});

            stop=new JButton("Stop");
            stop.setEnabled(false);
            stop.setBounds(50,200,100,25);
            stop.addActionListener(e->{stop(stop,next,start);});

            next=new JButton("Next");
            next.setEnabled(false);
            next.setBounds(50,300,100,25);
            next.addActionListener(e->{next();});

            restart=new JButton("restart");
            //restart.setEnabled(false);
            restart.setBounds(50,400,100,25);
            restart.addActionListener(e->{
                restart(stop,next,start,restart);});

            this.add(start);
            this.add(stop);
            this.add(next);
            this.add(restart);
        }
    }

    private class DungeonPanel extends JPanel {


        public DungeonPanel(){
            super();
            this.setPreferredSize(new Dimension(Main.DRAW_SIZE,Main.DRAW_SIZE));
            this.setBounds(0,0,Main.DRAW_SIZE,Main.DRAW_SIZE);
        }

        @Override
        public void paint(Graphics g){
            int scaleX=Main.DRAW_SIZE/map.getWidth();
            int scaleY=Main.DRAW_SIZE/map.getHeight();
            Graphics2D gd=(Graphics2D) g;
            gd.setColor(Color.BLACK);
            gd.fillRect(0,0,Main.DRAW_SIZE,Main.DRAW_SIZE);
            gd.setColor(Color.WHITE);
            for(int i=0;i<map.getHeight();i++){
                for(int j=0;j<map.getWidth();j++){
                    if(map.getTerrain(j,i))
                        gd.fillRect(j*scaleX,i*scaleY,scaleX,scaleY);
                }
            }
        }
    }
}

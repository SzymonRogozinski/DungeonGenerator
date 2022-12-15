package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainPanel extends JPanel {

    private DungeonPanel dp;
    private ControlPanel con;
    private Map map;
    private GenereatingThread genereate;

    private boolean stop;

    public MainPanel() {
        stop=true;
        genereate=new GenereatingThread(getLimitFromPanel(),map,this);

        this.setPreferredSize(new Dimension(Main.DRAW_SIZE+Main.CONTROL_SIZE,Main.DRAW_SIZE));
        this.setLayout(null);
        dp=new DungeonPanel();
        this.add(dp);
        con=new ControlPanel();
        this.add(con);
    }

    public boolean isStop() {
        return stop;
    }

    private int getSizeFromPanel(){
        try{
            return Integer.parseInt(con.size_of_dungeon.getText());
        }catch(NumberFormatException e){
            System.err.println(con.size_of_dungeon.getText()+" is not a number!");
        }
        return 0;
    }

    private int getLimitFromPanel(){
        return 1000;
    }

    public void resetScreen(){
        this.repaint();
        this.revalidate();
    }

    public synchronized void continueMode(JButton stopButton,JButton nextButton,JButton startButton,JButton restartButton){
        if(genereate.isAlive()) {
            this.stop = false;
            nextButton.setEnabled(false);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            genereate.ping();
        }else{
            this.map=new Map(getSizeFromPanel(), getSizeFromPanel());
            this.genereate=new GenereatingThread(getLimitFromPanel(),this.map,this);
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
        genereate.stop();

        this.map=null;
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

    public synchronized void save() {
        if(!genereate.isAlive()){
            try {
                ImageIO.write(map.getImage(), "png", new File("mapka.png"));
            }catch (IOException ignore){}
        }
        else{
            System.err.println("Mapa nie została jeszcze wygenerowana!!!");
        }
    }

    private class ControlPanel extends JPanel {

        private JButton start;
        private JButton stop;
        private JButton next;
        private JButton restart;
        private JButton save;
        private JTextField size_of_dungeon;

        public ControlPanel(){
            super();

            int buttonWidth=Main.CONTROL_SIZE/2;
            int buttonHeight=Main.DRAW_SIZE/20;
            int buttonShift=buttonWidth/2;
            int buttonSpacing=buttonHeight*2;

            this.setPreferredSize(new Dimension(Main.CONTROL_SIZE,Main.DRAW_SIZE));
            this.setBounds(Main.DRAW_SIZE,0,Main.DRAW_SIZE,Main.DRAW_SIZE);
            this.setBackground(Color.RED);
            this.setLayout(null);

            start=new JButton("Start");
            start.setBounds(buttonShift,buttonSpacing,buttonWidth,buttonHeight);
            start.addActionListener(e->{continueMode(stop,next,start,restart);});

            stop=new JButton("Stop");
            stop.setEnabled(false);
            stop.setBounds(buttonShift,2*buttonSpacing,buttonWidth,buttonHeight);
            stop.addActionListener(e->{stop(stop,next,start);});

            next=new JButton("Next");
            next.setEnabled(false);
            next.setBounds(buttonShift,3*buttonSpacing,buttonWidth,buttonHeight);
            next.addActionListener(e->{next();});

            restart=new JButton("Restart");
            restart.setBounds(buttonShift,4*buttonSpacing,buttonWidth,buttonHeight);
            restart.addActionListener(e->{
                restart(stop,next,start,restart);});

            save=new JButton("Save");
            save.setBounds(buttonShift,5*buttonSpacing,buttonWidth,buttonHeight);
            save.addActionListener(e->{save();});

            size_of_dungeon=new JTextField("100");
            size_of_dungeon.setBounds(buttonShift,6*buttonSpacing,buttonWidth,buttonHeight);

            this.add(start);
            this.add(stop);
            this.add(next);
            this.add(restart);
            this.add(save);
            this.add(size_of_dungeon);
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
            if(map==null){
                g.setColor(Color.BLACK);
                g.fillRect(0,0,Main.DRAW_SIZE,Main.DRAW_SIZE);
                return;
            }
            int scaleX=Main.DRAW_SIZE/map.getWidth();
            int scaleY=Main.DRAW_SIZE/map.getHeight();
            Graphics2D mapGraphics=map.getImage().createGraphics();
            mapGraphics.setColor(Color.BLACK);
            mapGraphics.fillRect(0,0,Main.DRAW_SIZE,Main.DRAW_SIZE);
            mapGraphics.setColor(Color.WHITE);
            for(int i=0;i<map.getHeight();i++){
                for(int j=0;j<map.getWidth();j++){
                    if(map.getTerrain(j,i))
                        mapGraphics.fillRect(j*scaleX,i*scaleY,scaleX,scaleY);
                }
            }
            mapGraphics.dispose();
            g.drawImage(map.getImage(),0,0,null);
        }
    }
}

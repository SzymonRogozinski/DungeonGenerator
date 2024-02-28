package com.company;

import com.company.dungeon.GeneratingThread;
import com.company.dungeon.Map;
import com.company.dungeon.MapTransformation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainPanel extends JPanel {

    private DungeonPanel dp;
    private ControlPanel con;
    private Map map;
    private GeneratingThread genereate;
    private int algorithmType=1;

    public GeneratingState state;

    public MainPanel() {
        state=new GeneratingState();

        this.setPreferredSize(new Dimension(Main.DRAW_SIZE+Main.CONTROL_SIZE,Main.DRAW_SIZE));
        this.setLayout(null);
        dp=new DungeonPanel();
        this.add(dp);
        con=new ControlPanel();
        this.add(con);
    }

    private int getSizeFromPanel(){
        try{
            return Integer.parseInt(con.size_of_map.getText());
        }catch(NumberFormatException e){
            System.err.println(con.size_of_map.getText()+" is not a number!");
        }
        return 0;
    }

    private int getLimitFromPanel(){
        try{
            return Integer.parseInt(con.numberOfElements.getText());
        }catch(NumberFormatException e){
            System.err.println(con.numberOfElements.getText()+" is not a number!");
        }
        return 0;
    }

    private int getSeedFromPanel(){
        if(con.seed.getText().isEmpty() || con.seed.getText()==null){
            return 0;
        }
        try{
            return Integer.parseInt(con.seed.getText());
        }catch(NumberFormatException e){
            System.err.println(con.seed.getText()+" is not a number!");
            return 0;
        }
    }

    public void resetScreen(){
        this.repaint();
        this.revalidate();
    }

    public synchronized void continueMode(JButton stopButton,JButton nextButton,JButton startButton,JButton restartButton){
        if(genereate!=null && genereate.isAlive()) {
            state.run();
            nextButton.setEnabled(false);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            genereate.ping();
        }else{
            state.restartState();
            this.map=new Map(getSizeFromPanel(), getSizeFromPanel());
            int seed=getSeedFromPanel();
            if(seed==0)
                this.genereate=new GeneratingThread(getLimitFromPanel(),this.map,this,algorithmType);
            else
                this.genereate=new GeneratingThread(getLimitFromPanel(),this.map,this,algorithmType,seed);
            stopButton.setEnabled(true);
            nextButton.setEnabled(false);
            startButton.setEnabled(false);
            restartButton.setEnabled(true);
            state.startWork();
            genereate.start();
        }
    }

    public synchronized void restart(JButton stopButton,JButton nextButton,JButton startButton,JButton restartButton){
        state.stop();
        genereate.stop();

        this.map=null;
        resetScreen();

        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        nextButton.setEnabled(false);
        restartButton.setEnabled(false);
    }

    public synchronized void stop(JButton stopButton,JButton nextButton,JButton startButton){
        state.stop();
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
                //Przeskalowanie
                Image tmp = map.getImage().getScaledInstance(Main.DRAW_SIZE, Main.DRAW_SIZE, Image.SCALE_SMOOTH);
                BufferedImage dimg = new BufferedImage(Main.DRAW_SIZE, Main.DRAW_SIZE, BufferedImage.TYPE_BYTE_BINARY);
                Graphics2D g2d = dimg.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();
                //Zapis
                ImageIO.write(dimg, "png", new File("mapka.png"));
            }catch (IOException ignore){}
        }
        else{
            System.err.println("Mapa nie została jeszcze wygenerowana!!!");
        }
    }

    public synchronized void speedUpGenerate(){
        genereate.changeDelay();
        state.speedUp();
    }

    public synchronized void resizeMap(){
        if(state.readyToResize()){
            int[] res=genereate.getSizesFromAlgorithm();
            map.resize(res[0],res[1],res[2],res[3]);
            resetScreen();
            state.resized();
        }
    }

    public synchronized void drawMapBorder(){
        if(state.isResized()){
            MapTransformation.drawBorder(map);
            //map.drawBorder();
            state.borderDrew();
            resetScreen();
        }
    }

    public synchronized void drawEntries(){
        if(state.isBordered() && !state.entries){
            MapTransformation.drawEntries(map);
            //map.drawEntries();
            state.entriesDrew();
            resetScreen();
        }
    }

    public synchronized void drawTreasure(){
        if(state.isEntriesDrew() && state.isBordered()){
            try {
                MapTransformation.drawTreasure(10,map);
                //map.drawTreasure(10);
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
            resetScreen();
        }
    }

    public synchronized void drawEnemies(){
        if(state.isEntriesDrew()){
            try {
                MapTransformation.drawEnemies(20,map);
                //map.drawEnemies(20);
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
            state.borderDrew();
            resetScreen();
        }
    }

    public synchronized void algorithmEnded(){
        state.GenerateEnd();
    }

    public class GeneratingState{
        private boolean working;
        private boolean stopped;    //Can be true only while working is true
        private boolean resized;    //Only after work was ended
        private boolean bordered;

        private boolean entries;

        public boolean isWorking(){
            return working;
        }

        public void startWork(){
            working=true;
        }

        public void endWork(){
            working=false;
        }

        public void GenerateEnd(){
            working=false;
        }

        public boolean isResized(){
            return resized;
        }

        public void resized(){
            resized=true;
        }

        public boolean readyToResize(){
            return !(working || resized);
        }

        public void stop(){
            stopped=true;
        }

        public void run(){
            stopped=false;
        }

        public void speedUp(){
            stopped=false;
            genereate.ping();
        }

        public boolean isStopped(){
            return stopped;
        }

        public boolean isBordered(){return bordered;}
        public void borderDrew(){
            bordered=true;
        }

        public boolean isEntriesDrew(){
            return entries;
        }

        public void entriesDrew(){
            entries=true;
        }

        public void restartState(){
            working=false;
            stopped=false;
            resized=false;
            bordered=false;
            entries=false;
        }

    }

    private class ControlPanel extends JPanel {

        private final JButton start,stop,next,restart,save,speedUp,resize,border,entries,treasure,enemies;
        private final JRadioButton firstAlg,secondAlg,thirdAlg;
        private final ButtonGroup algorithmChose;
        private final JTextField size_of_map,numberOfElements,seed;
        private final JLabel textOfSize,textOfNumber,textOfSeed;

        public ControlPanel(){
            super();

            int textfieldWidth=Main.CONTROL_SIZE/2;
            int textfieldHeight=Main.DRAW_SIZE/20;
            int textfieldShift=textfieldWidth/2;
            int textfieldSpacing=textfieldHeight*2;

            int buttonSize=50;
            int buttonShift=buttonSize/2;

            int radioButtonHeight=30;
            int radioButtonWidth=100;

            this.setPreferredSize(new Dimension(Main.CONTROL_SIZE,Main.DRAW_SIZE));
            this.setBounds(Main.DRAW_SIZE,0,Main.DRAW_SIZE,Main.DRAW_SIZE);
            this.setBackground(Color.RED);
            this.setLayout(null);

            start=new JButton(new ImageIcon("Menu_Buttons/play-button.png"));
            start.setBounds(buttonShift,buttonShift,buttonSize,buttonSize);

            stop=new JButton(new ImageIcon("Menu_Buttons/pause-button.png"));
            stop.setEnabled(false);
            stop.setBounds(buttonSize+buttonShift,buttonShift,buttonSize,buttonSize);

            next=new JButton(new ImageIcon("Menu_Buttons/next-button.png"));
            next.setEnabled(false);
            next.setBounds(buttonSize*2+buttonShift,buttonShift,buttonSize,buttonSize);

            restart=new JButton(new ImageIcon("Menu_Buttons/clockwise-rotation.png"));
            restart.setBounds(buttonShift,buttonSize+buttonShift,buttonSize,buttonSize);

            start.addActionListener(e->continueMode(stop,next,start,restart));
            stop.addActionListener(e->stop(stop,next,start));
            next.addActionListener(e->next());
            restart.addActionListener(e->restart(stop,next,start,restart));

            save=new JButton(new ImageIcon("Menu_Buttons/save.png"));
            save.setBounds(buttonSize+buttonShift,buttonSize+buttonShift,buttonSize,buttonSize);
            save.addActionListener(e-> save());

            speedUp=new JButton(new ImageIcon("Menu_Buttons/fast-forward-button.png"));
            speedUp.setBounds(2*buttonSize+buttonShift,buttonSize+buttonShift,buttonSize,buttonSize);
            speedUp.addActionListener(e-> speedUpGenerate());

            resize=new JButton(new ImageIcon("Menu_Buttons/contract.png"));
            resize.setEnabled(true);
            resize.setBounds(buttonShift,2*buttonSize+buttonShift,buttonSize,buttonSize);
            resize.addActionListener(e->resizeMap());

            border=new JButton(new ImageIcon("Menu_Buttons/highlighter.png"));
            border.setEnabled(true);
            border.setBounds(buttonSize+buttonShift,2*buttonSize+buttonShift,buttonSize,buttonSize);
            border.addActionListener(e->drawMapBorder());

            entries=new JButton(new ImageIcon("Menu_Buttons/wooden-door.png"));
            entries.setEnabled(true);
            entries.setBounds(2*buttonSize+buttonShift,2*buttonSize+buttonShift,buttonSize,buttonSize);
            entries.addActionListener(e->drawEntries());

            treasure=new JButton(new ImageIcon("Menu_Buttons/open-chest.png"));
            treasure.setEnabled(true);
            treasure.setBounds(buttonShift,3*buttonSize+buttonShift,buttonSize,buttonSize);
            treasure.addActionListener(e->drawTreasure());

            enemies=new JButton(new ImageIcon("Menu_Buttons/bully-minion.png"));
            enemies.setEnabled(true);
            enemies.setBounds(buttonSize+buttonShift,3*buttonSize+buttonShift,buttonSize,buttonSize);
            enemies.addActionListener(e->drawEnemies());

            //Radio Buttons
            firstAlg=new JRadioButton("Jaskinia");
            firstAlg.setBounds(buttonShift,4*buttonSize+buttonShift,radioButtonWidth,radioButtonHeight);
            firstAlg.addActionListener(e->algorithmType=1);
            firstAlg.setSelected(true);

            secondAlg=new JRadioButton("Kopalnia");
            secondAlg.setBounds(buttonShift,4*buttonSize+buttonShift+radioButtonHeight,radioButtonWidth,radioButtonHeight);
            secondAlg.addActionListener(e->algorithmType=2);

            thirdAlg=new JRadioButton("Loch");
            thirdAlg.setBounds(buttonShift,4*buttonSize+buttonShift+radioButtonHeight*2,radioButtonWidth,radioButtonHeight);
            thirdAlg.addActionListener(e->algorithmType=3);

            algorithmChose=new ButtonGroup();
            algorithmChose.add(firstAlg);
            algorithmChose.add(secondAlg);
            algorithmChose.add(thirdAlg);

            textOfSize=new JLabel("Size of map");
            textOfSize.setBounds(textfieldShift,(int)(6.5*textfieldSpacing),textfieldWidth,textfieldHeight);
            textOfSize.setHorizontalAlignment(JLabel.CENTER);
            textOfSize.setForeground(Color.WHITE);

            size_of_map =new JTextField("100");
            size_of_map.setBounds(textfieldShift,(int)7*textfieldSpacing,textfieldWidth,textfieldHeight);
            size_of_map.setHorizontalAlignment(JTextField.CENTER);

            textOfNumber=new JLabel("Size of dungeon");
            textOfNumber.setBounds(textfieldShift,(int)(7.5*textfieldSpacing),textfieldWidth,textfieldHeight);
            textOfNumber.setHorizontalAlignment(JLabel.CENTER);
            textOfNumber.setForeground(Color.WHITE);

            numberOfElements=new JTextField("1000");
            numberOfElements.setBounds(textfieldShift,8*textfieldSpacing,textfieldWidth,textfieldHeight);
            numberOfElements.setHorizontalAlignment(JTextField.CENTER);

            textOfSeed=new JLabel("Seed");
            textOfSeed.setBounds(textfieldShift,(int)(8.5*textfieldSpacing),textfieldWidth,textfieldHeight);
            textOfSeed.setHorizontalAlignment(JLabel.CENTER);
            textOfSeed.setForeground(Color.WHITE);

            seed=new JTextField();
            seed.setBounds(textfieldShift,9*textfieldSpacing,textfieldWidth,textfieldHeight);
            seed.setHorizontalAlignment(JTextField.CENTER);

            this.add(start);
            this.add(stop);
            this.add(next);
            this.add(restart);
            this.add(save);
            this.add(speedUp);
            this.add(resize);
            this.add(border);
            this.add(entries);
            this.add(treasure);
            this.add(enemies);
            this.add(firstAlg);
            this.add(secondAlg);
            this.add(thirdAlg);
            this.add(textOfSize);
            this.add(size_of_map);
            this.add(textOfSize);
            this.add(numberOfElements);
            this.add(textOfNumber);
            this.add(textOfSeed);
            this.add(seed);
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
            Graphics2D mapGraphics=map.getImage().createGraphics();

            //Drawing
            for(int i=0;i<map.getHeight();i++){
                for(int j=0;j<map.getWidth();j++){
                    switch (map.getTerrain(j,i)){
                        case null,VOID -> mapGraphics.setColor(Color.BLACK);
                        case FLOOR -> mapGraphics.setColor(Color.WHITE);
                        case WALL -> mapGraphics.setColor(Color.GRAY);
                        case ENTRIES -> mapGraphics.setColor(Color.ORANGE);
                        case TREASURE -> mapGraphics.setColor(Color.BLUE);
                        case ENEMY -> mapGraphics.setColor(Color.RED);
                    }
                    mapGraphics.fillRect(j, i, 1, 1);
                }
            }
            mapGraphics.dispose();
            g.drawImage(map.getImage(),0,0,Main.DRAW_SIZE,Main.DRAW_SIZE,null);
        }
    }
}

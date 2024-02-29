package com.company;

import javax.swing.*;
import java.awt.*;

public class Main {

    public final static int MAX_SIZE=800;
    public final static int DRAW_SIZE=500;
    public final static int CONTROL_SIZE=200;


    public static void main(String[] args) {
        JFrame frame=new JFrame("Dungeon generator");
        JPanel mainPanel=new MainPanel();

        frame.setSize(new Dimension(MAX_SIZE,MAX_SIZE));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }
}

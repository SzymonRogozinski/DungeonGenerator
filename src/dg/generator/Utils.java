package dg.generator;

import dg.generator.dungeon.Map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Utils {

    public static void main(String[] args) {
        try {
            Map m = Generator.generateDungeon(Generator.CAVE_ALGORITHM, 100, 100, 3000, 30, 30, 3,2);
            Graphics2D mapGraphics=m.getImage().createGraphics();
            //Drawing
            for(int i=0;i<m.getHeight();i++){
                for(int j=0;j<m.getWidth();j++){
                    switch (m.getTerrain(j,i)){
                        case VOID -> mapGraphics.setColor(Color.BLACK);
                        case null -> mapGraphics.setColor(Color.BLACK);
                        case FLOOR -> mapGraphics.setColor(Color.WHITE);
                        case WALL -> mapGraphics.setColor(Color.GRAY);
                        case ENTRIES -> mapGraphics.setColor(Color.ORANGE);
                        case TREASURE -> mapGraphics.setColor(Color.BLUE);
                        case ENEMY -> mapGraphics.setColor(Color.RED);
                        case KEY -> mapGraphics.setColor(Color.cyan);
                        case DOOR -> mapGraphics.setColor(Color.YELLOW);
                        case SAFE_ROOM_DOORS -> mapGraphics.setColor(Color.GREEN);
                        case NPC -> mapGraphics.setColor(Color.MAGENTA);
                    }
                    mapGraphics.fillRect(j, i, 1, 1);
                }
            }
            mapGraphics.dispose();
            Image tmp = m.getImage().getScaledInstance(Main.DRAW_SIZE, Main.DRAW_SIZE, Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage(Main.DRAW_SIZE, Main.DRAW_SIZE, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            //Save
            ImageIO.write(dimg, "png", new File("mapTest.png"));
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

    }
}

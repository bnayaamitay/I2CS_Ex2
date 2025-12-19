package assignments;

import java.awt.*;
import java.io.*;

/**
 * Intro2CS_2026A
 * This class represents a Graphical User Interface (GUI) for Map2D.
 * The class has save and load functions, and a GUI draw function.
 * You should implement this class, it is recommender to use the StdDraw class, as in:
 * https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html
 *
 *
 */
public class Ex2_GUI {
    public static void drawMap(Map2D map) {
        int width = map.getWidth();
        int height = map.getHeight();
        StdDraw.setCanvasSize(width * 20, (height + 3) * 20);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height + 3);
        StdDraw.clear(Color.white);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int colorValue = map.getPixel(i, j);
                Color c = getColor(colorValue);
                StdDraw.setPenColor(c);
                StdDraw.filledCircle(i + 0.5, j + 0.5, 0.4);

            }
        }
        drawToolbar(width,height);
        StdDraw.show();
    }

    /**
     * @param mapFileName
     * @return
     */
    public static Map2D loadMap(String mapFileName) {
        Map2D ans = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(mapFileName));
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                return null;
            }
            String[] dimensions = line.split(",");
            int w = Integer.parseInt(dimensions[0]);
            int h = Integer.parseInt(dimensions[1]);
            ans = new Map(w, h, 0);
            for (int y = 0; y < h; y++) {
                line = reader.readLine();
                if (line != null) {
                    String[] values = line.split(",");
                    for (int x = 0; x < w; x++) {
                        int val = Integer.parseInt(values[x]);
                        ans.setPixel(x, y, val);
                    }
                }
            }
        }
        catch (IOException e) {e.printStackTrace();}
        finally {
        try {
            if (reader != null) reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        }
        return ans;
    }

    /**
     *
     * @param map
     * @param mapFileName
     */
    public static void saveMap(Map2D map, String mapFileName) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mapFileName));
            bw.write(map.getWidth() + "," + map.getHeight());
            bw.newLine();
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    int val = map.getPixel(x, y);
                    bw.write(String.valueOf(val));
                    if (x < map.getWidth() - 1) {
                        bw.write(",");
                    }
                }
                bw.newLine();
            }
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not save file: " + mapFileName);
        }
    }

    public static void main(String[] a) {
        String mapFile = "map.txt";
        Map2D map = loadMap(mapFile);
        drawMap(map);
    }
    /// ///////////// Private functions ///////////////
    private  static Color getColor(int v) {
        if  (v == 0) return Color.WHITE;
        if  (v == 1) return Color.BLACK;
        if  (v == -1) return Color.RED;
        if  (v == 2) return Color.GREEN;
        if  (v == 3) return Color.YELLOW;
        if  (v == 4) return Color.BLUE;
        if  (v == 5) return Color.CYAN;
        return Color.GREEN;
    }

    private static void drawToolbar(int w, int h) {
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        StdDraw.filledRectangle(w / 2.0, h + 1.5, w / 2.0, 1.5);
        drawButton(1, h + 1.5, "Map", Color.BLACK);
        drawButton(4, h + 1.5, "Color", Color.BLACK);
        drawButton(7, h + 1.5, "Algo", Color.BLACK);
    }

    private static void drawButton(double x, double y, String text, Color color) {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(x, y, 1.4, 0.8);
        StdDraw.setPenColor(color);
        StdDraw.text(x, y, text);
    }
}

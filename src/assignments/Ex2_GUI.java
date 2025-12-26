package assignments;

import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.awt.Font;

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

    /**
     * Algorithm (Pseudocode) for drawMap:
     * 1. Setup: Get map size (w, h) and set up the window scale.
     * 2. Loop forever:
     * a. If map size changes, update the window scale.
     * b. Draw the background (White).
     * c. Draw the Grid: Loop through all pixels (x,y) and draw them with their colors.
     * d. Draw the Toolbar: Show buttons for Map, Color, Draw, Algo.
     * e. Show the window.
     * f. Handle Mouse Clicks:
     * - If a toolbar button is clicked -> Open the relevant menu (Map/Color/Draw/Algo).
     * - If the map is clicked -> Perform the action based on the current "Mode":
     * "Point": Draw a single pixel.
     * "Fill": Run flood fill from that point.
     * "Path": Calculate and draw the shortest path.
     * "Rect"/"Circle"/"Line": Draw the shape.
     *
     * The main GUI function.
     * It initializes the window and runs an infinite loop to handle drawing and user input.
     * * @param map The Map2D object to draw and interact with.
     */
    public static void drawMap(Map2D map) {
        int w = map.getWidth();
        int h = map.getHeight();
        StdDraw.setXscale(-0.5, w - 0.5);
        StdDraw.setYscale(-0.5, h + 1.2);
        StdDraw.enableDoubleBuffering();
        int selectedColor = -1;
        String mode = "Draw";
        Pixel2D p1 = null;
        while(true) {
            if (w != map.getWidth() || h != map.getHeight()) {
                w = map.getWidth();
                h = map.getHeight();
                StdDraw.setXscale(-0.5, w - 0.5);
                StdDraw.setYscale(-0.5, h + 1.2);
            }
            StdDraw.clear(Color.WHITE);
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.setFont(new Font("Gisha", Font.PLAIN, 12));
            StdDraw.text(w - 2, h + 1.0, "Mode: " + mode);
            StdDraw.setPenColor(Color.LIGHT_GRAY);
            StdDraw.setPenRadius(0.005);
            for (int i = 0; i < w; i++) { StdDraw.line(i, 0, i, h - 1); }
            for (int j = 0; j < h; j++) { StdDraw.line(0, j, w - 1, j); }
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int colorValue = map.getPixel(i, j);
                    Color c = (colorValue == 0) ? Color.WHITE : getColor(colorValue);
                    StdDraw.setPenColor(c);
                    StdDraw.filledCircle(i, j, 0.3);
                    StdDraw.setPenColor(Color.BLACK);
                    StdDraw.setPenRadius(0.002);
                    StdDraw.circle(i, j, 0.3);
                }
            }
            drawToolbar(w, h);
            StdDraw.show();
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                double toolbarY = h + 0.6;
                double btnHalfW = w * 0.11;
                double btnHalfH = 0.35;
                double mapBtnX   = (w * 0.17) - 0.5;
                double colorBtnX = (w * 0.39) - 0.5;
                double drawBtnX  = (w * 0.61) - 0.5;
                double algoBtnX  = (w * 0.83) - 0.5;
                if (isInside(x, y, mapBtnX, toolbarY, btnHalfW, btnHalfH)) {
                    openMapMenu(map, w, h);
                    while (StdDraw.mousePressed()) { StdDraw.pause(10); }
                }
                else if (isInside(x, y, colorBtnX, toolbarY, btnHalfW, btnHalfH)) {
                    selectedColor = openColorMenu(w, h, selectedColor);
                }
                else if (isInside(x, y, drawBtnX, toolbarY, btnHalfW, btnHalfH)) {
                    String result = openDrawMenu(map, w, h);
                    if (!result.equals("None")) {
                        mode = result;
                        p1 = null;
                    }
                }
                else if (isInside(x, y, algoBtnX, toolbarY, btnHalfW, btnHalfH)) {
                    String result = openAlgoMenu(map, w, h);
                    if (!result.equals("None")) {
                        mode = result;
                        p1 = null;
                    }
                }
                else if (x >= -0.5 && x < w - 0.5 && y >= -0.5 && y < h - 0.5) {
                    int pX = (int)Math.round(x);
                    int pY = (int)Math.round(y);
                    Pixel2D currentClick = new Index2D(pX, pY);
                    if (mode.equals("Point") || mode.equals("Draw")) {
                        map.setPixel(pX, pY, selectedColor);
                        mode = "Draw";
                    }
                    else if (mode.equals("Fill")) {
                        map.fill(currentClick, selectedColor, true);
                        mode = "Draw";
                    }
                    else if (mode.equals("Path")) {
                        if (p1 == null) {
                            p1 = currentClick;
                            int markerColor = (selectedColor == -1) ? 1 : selectedColor;
                            map.setPixel(pX, pY, markerColor);
                        }
                        else {
                            Pixel2D[] path = map.shortestPath(p1, currentClick, -1, true);
                            if (path != null) {
                                int pathColor = (selectedColor == -1) ? 1 : selectedColor;
                                for (Pixel2D p : path) map.setPixel(p, pathColor);
                            }
                            p1 = null;
                            mode = "Draw";
                        }
                    }
                    else if (mode.equals("All")) {
                        Map2D distanceMap = map.allDistance(currentClick, -1, true);
                        map.init(distanceMap.getMap());
                        mode = "Draw";
                    }
                    else if (mode.equals("Rect")) {
                        if (p1 == null) {
                            p1 = currentClick;
                            map.setPixel(pX, pY, selectedColor);
                        }
                        else {
                            map.drawRect(p1, currentClick, selectedColor);
                            p1 = null;
                            mode = "Draw";
                        }
                    }
                    else if (mode.equals("Circle")) {
                        if (p1 == null) {
                            p1 = currentClick;
                            map.setPixel(pX, pY, selectedColor);
                        }
                        else {
                            double dist = Math.sqrt(Math.pow(p1.getX() - pX, 2) + Math.pow(p1.getY() - pY, 2));
                            map.drawCircle(p1, dist, selectedColor);
                            p1 = null;
                            mode = "Draw";
                        }
                    }
                    else if (mode.equals("Line")) {
                        if (p1 == null) {
                            p1 = currentClick;
                            map.setPixel(pX, pY, selectedColor);
                        }
                        else {
                            map.drawLine(p1, currentClick, selectedColor);
                            p1 = null;
                            mode = "Draw";
                        }
                    }
                    StdDraw.show();
                }
                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
            }
            StdDraw.pause(50);
        }
    }

    /**
     * Loads a map from a text file.
     * The file format should be:
     * Line 1: width,height
     * Following lines: the map values (0, 1, -1, etc.) separated by commas.
     * If the file is not found, it creates a default 10x10 map.
     * @param mapFileName The name/path of the file to load.
     * @return A new Map2D object containing the loaded data.
     */
    public static Map2D loadMap(String mapFileName) {
        try {
            Scanner sc = new Scanner(new File(mapFileName));
            String[] dims = sc.nextLine().split(",");
            Map2D map = new Map(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), 0);
            for (int y = 0; y < map.getHeight(); y++) {
                String[] line = sc.nextLine().split(",");
                for (int x = 0; x < map.getWidth(); x++) {
                    map.setPixel(x, y, Integer.parseInt(line[x]));
                }
            }
            sc.close();
            return map;
        }
        catch (Exception e) {
            System.out.println("File not found. Creating new 10x10 map.");
            Map2D newMap = new Map(10, 10, 0);
            saveMap(newMap, mapFileName);
            return newMap;
        }
    }

    /**
     * Saves the current map to a text file.
     * It writes the dimensions first, followed by the grid values.
     * @param map The map to save.
     * @param mapFileName The name of the file to write to.
     */
    public static void saveMap(Map2D map, String mapFileName) {
        try {
            FileWriter myWriter = new FileWriter(mapFileName);
            myWriter.write(map.getWidth() + "," + map.getHeight() + "\n");
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    myWriter.write(String.valueOf(map.getPixel(x, y)));
                    if (x < map.getWidth() - 1) {
                        myWriter.write(",");
                    }
                }
                myWriter.write("\n");
            }
            myWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] a) {
        String mapFile = "map.txt";
        Map2D map = loadMap(mapFile);
        drawMap(map);
    }

    /// ///////////// Private functions ///////////////

    /**
     * Helper function to convert an integer value to a Color object.
     * 0 -> White (Empty)
     * -1 -> Black (Obstacle)
     * 1, 2, 3... -> Red, Green, Yellow, etc.
     * @param v The integer value from the map.
     * @return The corresponding Color object.
     */
    public static Color getColor(int v) {
        if (v == 0) return Color.WHITE;
        if (v == -1) return Color.BLACK;
        int colorIndex = (v - 1) % 5 + 1;
        switch (colorIndex) {
            case 1: return Color.RED;
            case 2: return Color.GREEN;
            case 3: return Color.YELLOW;
            case 4: return Color.BLUE;
            case 5: return Color.CYAN;
            default: return Color.WHITE;
        }
    }

    /**
     * 1. Draw a gray background rectangle at the bottom of the screen.
     * 2. Calculate the X positions for the 4 main buttons.
     * 3. Call drawButton() for each button: Map, Color, Draw, Algo.
     *
     * Draws the bottom toolbar with the main menu buttons.
     * @param w The width of the map.
     * @param h The height of the map.
     */
    private static void drawToolbar(int w, int h) {
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        StdDraw.filledRectangle((w - 1) / 2.0, h + 0.6, w / 2.0 + 0.5, 0.6);
        double btnY = h + 0.6;
        double mapX   = (w * 0.17) - 0.5;
        double colorX = (w * 0.39) - 0.5;
        double drawX  = (w * 0.61) - 0.5;
        double algoX  = (w * 0.83) - 0.5;
        drawButton(mapX,   btnY, "Map",   Color.BLACK, w);
        drawButton(colorX, btnY, "Color", Color.BLACK, w);
        drawButton(drawX,  btnY, "Draw",  Color.BLACK, w);
        drawButton(algoX,  btnY, "Algo",  Color.BLACK, w);
    }

    /**
     * 1. Draw a white filled rectangle (background).
     * 2. Draw a black rectangle outline.
     * 3. Set the text color and font.
     * 4. Draw the text in the center of the button.
     *
     * Helper function to draw a single button with text.
     * @param x Center X coordinate.
     * @param y Center Y coordinate.
     * @param text The text to display.
     * @param color The text color.
     * @param w Map width (used for scaling).
     */
    private static void drawButton(double x, double y, String text, Color color, int w) {
        double btnW = w * 0.22;
        double btnH = 0.7;
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(x, y, btnW / 2, btnH / 2);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.003);
        StdDraw.rectangle(x, y, btnW / 2, btnH / 2);
        StdDraw.setPenColor(color);
        StdDraw.setFont(new Font("Gisha", Font.BOLD, 14));
        StdDraw.text(x, y, text);
    }

    /**
     * 1. Draw the sub-menu buttons: Save, Merge, Rescale, New.
     * 2. Wait for a mouse click.
     * 3. Check where the user clicked:
     * - "Save": Save map to file.
     * - "Merge": Load another map and add it to current.
     * - "Rescale": Open input dialog to resize.
     * - "New": Clear the map.
     * 4. Exit loop after an action is taken.
     *
     * Opens the "Map" sub-menu and handles file operations (Save, Load, Merge, etc).
     * @param map The current map.
     * @param w Map width.
     * @param h Map height.
     */
    private static void openMapMenu(Map2D map, int w, int h) {
        double menuX = (w * 0.17) - 0.5;
        double btnHalfW = w * 0.11; double btnHalfH = 0.35;
        double saveY = (h + 0.6) - 0.75;
        double mergeY = saveY - 0.75;
        double rescaleY = mergeY - 0.75;
        double newY = rescaleY - 0.75;
        drawButton(menuX, saveY, "Save", Color.BLACK, w);
        drawButton(menuX, mergeY, "Merge", Color.BLACK, w);
        drawButton(menuX, rescaleY, "Rescale", Color.BLACK, w);
        drawButton(menuX, newY, "New", Color.BLACK, w);
        StdDraw.show();
        while (StdDraw.mousePressed()) { StdDraw.pause(20); }
        boolean selectionMade = false;
        while (!selectionMade) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                if (isInside(x, y, menuX, saveY, btnHalfW, btnHalfH)) {
                    saveMap(map, "map.txt");
                    System.out.println("Saved to map.txt!");
                    selectionMade = true;
                }
                else if (isInside(x, y, menuX, mergeY, btnHalfW, btnHalfH)) {
                    System.out.println("Merge clicked!");
                    Map2D mapToMerge = loadMap("map.txt");
                    map.addMap2D(mapToMerge);
                    System.out.println("Merged with map.txt successfully!");
                    selectionMade = true;
                }
                else if (isInside(x, y, menuX, rescaleY, btnHalfW, btnHalfH)) {
                    inputAndRescale(map, w, h);
                    selectionMade = true;
                }
                else if (isInside(x, y, menuX, newY, btnHalfW, btnHalfH)) {
                    map.mul(0);
                    selectionMade = true;
                }
                else {
                    selectionMade = true;
                }
                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
            }
            StdDraw.pause(20);
        }
    }

    /**
     * 1. Check if the input X is between the left and right edges of the area.
     * 2. Check if the input Y is between the top and bottom edges.
     * 3. Return true if both are true.
     *
     * Helper function to check if a mouse click is inside a specific rectangular area.
     * @param x Mouse X.
     * @param y Mouse Y.
     * @param centerX Button center X.
     * @param centerY Button center Y.
     * @param halfW Half the button width.
     * @param halfH Half the button height.
     * @return True if inside, False otherwise.
     */
    private static boolean isInside(double x, double y, double centerX, double centerY, double halfW, double halfH) {
        return (x >= centerX - halfW && x <= centerX + halfW && y >= centerY - halfH && y <= centerY + halfH);
    }

    /**
     * 1. Clear previous key presses.
     * 2. Loop until 'Enter' is pressed:
     * a. Draw the input box and the current text.
     * b. Wait for a key press.
     * c. If 'Enter': Try to parse the number. If valid, resize map.
     * d. If 'Backspace': Remove last character.
     * e. If Digit/Dot: Append to text.
     *
     * Opens a dialog box to take a number input from the user and rescale the map.
     * Handles keyboard input and validation.
     * @param map The map to rescale.
     * @param w Current width.
     * @param h Current height.
     */
    private static void inputAndRescale(Map2D map, int w, int h) {
        StringBuilder input = new StringBuilder();
        boolean done = false;
        String errorMessage = "";
        int MAX_LIMIT = 30;
        while (StdDraw.hasNextKeyTyped()) { StdDraw.nextKeyTyped(); }
        while (!done) {
            double centerX = (w * 0.20) - 0.5;
            double centerY = h / 2.0;
            StdDraw.setPenColor(new Color(255, 255, 255, 230));
            StdDraw.filledRectangle(centerX, centerY, w * 0.35, 2.0);
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.setPenRadius(0.005);
            StdDraw.rectangle(centerX, centerY, w * 0.35, 2.0);
            StdDraw.setFont(new Font("Gisha", Font.BOLD, 16));
            StdDraw.text(centerX, centerY + 0.8, "Enter a number:");
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.text(centerX, centerY, input.toString() + "_");
            if (!errorMessage.isEmpty()) {
                StdDraw.setPenColor(Color.RED);
                StdDraw.setFont(new Font("Gisha", Font.BOLD, 12));
                StdDraw.text(centerX, centerY - 0.8, errorMessage);
            }
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == '\n') {
                    try {
                        double scalar = Double.parseDouble(input.toString());
                        if (scalar > 0) {
                            int newW = (int)(w * scalar);
                            int newH = (int)(h * scalar);
                            if (newW < 1 || newH < 1) {
                                errorMessage = "Too small! (Min 1x1)";
                                input.setLength(0);
                            }
                            else if (newW > MAX_LIMIT || newH > MAX_LIMIT) {
                                errorMessage = "Too big! (Max " + MAX_LIMIT + "x" + MAX_LIMIT + ")";
                                input.setLength(0);
                            }
                            else {
                                map.rescale(scalar, scalar);
                                System.out.println("Rescaled to: " + newW + "x" + newH);
                                done = true;
                            }
                        }
                        else {
                            errorMessage = "Scalar must be positive";
                            input.setLength(0);
                        }
                    }
                    catch (NumberFormatException e) {
                        errorMessage = "Invalid number";
                        input.setLength(0);
                    }
                }
                else if (key == '\b') {
                    if (input.length() > 0) {
                        input.deleteCharAt(input.length() - 1);
                        errorMessage = "";
                    }
                }
                else if (Character.isDigit(key) || key == '.') {
                    input.append(key);
                    errorMessage = "";
                }
            }
            StdDraw.pause(20);
        }
        StdDraw.pause(200);
    }

    /**
     * 1. Draw the list of available colors.
     * 2. Wait for a mouse click.
     * 3. Check which color button was clicked.
     * 4. Return the ID of the selected color.
     *
     * Displays color options (Black, White, Red...) and returns the selected color ID.
     * @param w Map width.
     * @param h Map height.
     * @param currentColor The currently selected color.
     * @return The new color ID.
     */
    private static int openColorMenu(int w, int h, int currentColor) {
        double menuX = (w * 0.39) - 0.5;
        double startY = (h + 0.6) - 0.75;
        String[] names = {"Black", "White", "Green", "Blue", "Red", "Yellow", "Cyan"};
        Color[] realColors = {Color.BLACK, Color.WHITE, Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW, Color.CYAN};
        int[] ids = {-1, 0, 2, 4, 1, 3, 5};
        for (int i = 0; i < names.length; i++) {
            drawButton(menuX, startY - (i * 0.75), names[i], Color.BLACK, w);
        }
        StdDraw.show();
        while (StdDraw.mousePressed()) { StdDraw.pause(20); }
        while (true) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                double btnW = w * 0.22;
                double btnH = 0.7;
                for (int i = 0; i < names.length; i++) {
                    if (isInside(x, y, menuX, startY - (i * 0.75), w * 0.11, 0.35)) {
                        while (StdDraw.mousePressed()) { StdDraw.pause(10); }
                        return ids[i];
                    }
                }
                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
                return currentColor;
            }
            StdDraw.pause(20);
        }
    }

    /**
     * 1. Draw algorithm options (Shortest Path, All Paths, Fill).
     * 2. Wait for mouse click.
     * 3. Identify which button was clicked.
     * 4. Return the selected mode string.
     *
     * Displays algorithm options and returns the selected mode.
     * @param map The map object.
     * @param w Width.
     * @param h Height.
     * @return String representing the selected algo ("Path", "Fill", etc).
     */
    private static String openAlgoMenu(Map2D map, int w, int h) {
        double menuX = (w * 0.83) - 0.5;
        double btnHalfW = w * 0.11; double btnHalfH = 0.35;
        double startY = (h + 0.6) - 0.75;
        String[] opts = {"Shortest Path", "All Paths", "Fill"};
        String[] ret  = {"Path", "All", "Fill"};
        for (int i=0; i<opts.length; i++) {
            drawButton(menuX, startY - (i*0.75), opts[i], Color.BLACK, w);
        }
        StdDraw.show();
        while (StdDraw.mousePressed()) { StdDraw.pause(20); }
        boolean selectionMade = false;
        String selectedAction = "None";
        while (!selectionMade) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                for (int i=0; i<opts.length; i++) {
                    if (isInside(x, y, menuX, startY - (i*0.75), btnHalfW, btnHalfH)) {
                        selectedAction = ret[i]; selectionMade = true; break;
                    }
                }
                if (!selectionMade) selectionMade = true;
                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
            }
            StdDraw.pause(20);
        }
        return selectedAction;
    }

    /**
     * 1. Draw shape options (Point, Line, Rectangle, Circle).
     * 2. Wait for mouse click.
     * 3. Identify which button was clicked.
     * 4. Return the selected drawing mode.
     *
     * Displays drawing shapes and returns the selected mode.
     * @param map The map object.
     * @param w Width.
     * @param h Height.
     * @return String representing the drawing mode ("Line", "Rect", etc).
     */
    private static String openDrawMenu(Map2D map, int w, int h) {
        double menuX = (w * 0.61) - 0.5;
        double btnHalfW = w * 0.11; double btnHalfH = 0.35;
        double startY = (h + 0.6) - 0.75;
        String[] opts = {"Point", "Line", "Rectangle", "Circle"};
        String[] ret  = {"Point", "Line", "Rect", "Circle"};
        for (int i=0; i<opts.length; i++) {
            drawButton(menuX, startY - (i*0.75), opts[i], Color.BLACK, w);
        }
        StdDraw.show();
        while (StdDraw.mousePressed()) { StdDraw.pause(20); }
        boolean selectionMade = false;
        String selectedAction = "None";
        while (!selectionMade) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                for (int i=0; i<opts.length; i++) {
                    if (isInside(x, y, menuX, startY - (i*0.75), btnHalfW, btnHalfH)) {
                        selectedAction = ret[i]; selectionMade = true; break;
                    }
                }
                if (!selectionMade) selectionMade = true;
                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
            }
            StdDraw.pause(20);
        }
        return selectedAction;
    }
}

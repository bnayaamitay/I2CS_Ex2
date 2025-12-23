package assignments;

import java.awt.*;
import java.io.*;
import java.util.Scanner;

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
        int w = map.getWidth();
        int h = map.getHeight();

        StdDraw.setXscale(-0.5, w - 0.5);
        StdDraw.setYscale(-0.5, h + 1.2);
        StdDraw.enableDoubleBuffering();

        int selectedColor = -1;
        String mode = "Draw"; // מצב התחלתי

        // --- משתנה עזר לחישוב מסלול (זוכר את הנקודה הראשונה) ---
        Pixel2D p1 = null;

        while(true) {
            if (w != map.getWidth() || h != map.getHeight()) {
                w = map.getWidth();
                h = map.getHeight();
                StdDraw.setXscale(-0.5, w - 0.5);
                StdDraw.setYscale(-0.5, h + 1.2);
            }

            StdDraw.clear(Color.WHITE);

            // כיתוב מצב (כדי שתדע אם אתה במצב מילוי/מסלול)
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.text(w - 2, h + 1.0, "Mode: " + mode);

            // ציור הרשת
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
                double btnHalfW = w * 0.14; double btnHalfH = 0.35;
                double mapBtnX = (w * 0.20) - 0.5;
                double colorBtnX = (w * 0.50) - 0.5;
                double algoBtnX = (w * 0.80) - 0.5;

                // Map Menu
                if (isInside(x, y, mapBtnX, toolbarY, btnHalfW, btnHalfH)) {
                    openMapMenu(map, w, h);
                    while (StdDraw.mousePressed()) { StdDraw.pause(10); }
                }
                // Color Menu
                else if (isInside(x, y, colorBtnX, toolbarY, btnHalfW, btnHalfH)) {
                    selectedColor = openColorMenu(w, h, selectedColor);
                }
                // Algo Menu
                else if (isInside(x, y, algoBtnX, toolbarY, btnHalfW, btnHalfH)) {
                    String result = openAlgoMenu(map, w, h);
                    if (!result.equals("None")) {
                        mode = result;
                        p1 = null; // איפוס נקודת התחלה אם שינינו מצב
                    }
                }

                // --- לחיצה על המפה (כאן כל הלוגיקה החדשה) ---
                else if (x >= 0 && x < w && y >= 0 && y < h) {
                    int pX = (int)Math.round(x);
                    int pY = (int)Math.round(y);
                    Pixel2D currentClick = new Index2D(pX, pY);

                    // 1. מצב מילוי
                    if (mode.equals("Fill")) {
                        // --- התיקון: שינינו את false ל-true ---
                        // עכשיו המילוי יעבור גם דרך הקירות בצדדים (Cyclic)
                        map.fill(currentClick, selectedColor, true);

                        System.out.println("Filled area (Cyclic)!");
                        mode = "Draw";
                    }

                    // 2. מצב מסלול קצר
                    else if (mode.equals("Path")) {
                        if (p1 == null) {
                            p1 = currentClick;
                            System.out.println("Start point: " + p1);
                            int markerColor = (selectedColor == -1) ? 1 : selectedColor;
                            map.setPixel(pX, pY, markerColor);
                        } else {
                            System.out.println("End point: " + currentClick);

                            // --- התיקון: שינינו את false ל-true ---
                            // עכשיו הוא יחפש דרך גם "מסביב לעולם" (כמו פקמן)
                            Pixel2D[] path = map.shortestPath(p1, currentClick, -1, true);

                            if (path != null) {
                                int pathColor = (selectedColor == -1) ? 1 : selectedColor;
                                for (Pixel2D p : path) {
                                    map.setPixel(p, pathColor);
                                }
                                System.out.println("Path found (Cyclic)!");
                            } else {
                                System.out.println("No path found!");
                            }

                            p1 = null;
                            mode = "Draw";
                        }
                    }

                    // 3. מצב ציור רגיל
                    else {
                        map.setPixel(pX, pY, selectedColor);
                    }

                    StdDraw.show();
                }

                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
            }
            StdDraw.pause(50);
        }
    }

    /**
     * @param mapFileName
     * @return
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
     *
     * @param map
     * @param mapFileName
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
    private  static Color getColor(int v) {
        if  (v == 0) return Color.WHITE;
        if  (v == -1) return Color.BLACK;
        if  (v == 1) return Color.RED;
        if  (v == 2) return Color.GREEN;
        if  (v == 3) return Color.YELLOW;
        if  (v == 4) return Color.BLUE;
        if  (v == 5) return Color.CYAN;
        return Color.black;
    }

    private static void drawToolbar(int w, int h) {
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 14));
        StdDraw.setPenColor(Color.LIGHT_GRAY);
        StdDraw.filledRectangle(w / 2.0, h + 0.6, w, 0.6);
        double yPos = h + 0.6;
        drawButton((w * 0.20) - 0.5, yPos, "Map", Color.BLACK, w);
        drawButton((w * 0.50) - 0.5, yPos, "Color", Color.BLACK, w);
        drawButton((w * 0.80) - 0.5, yPos, "Algo", Color.BLACK, w);
    }

    private static void drawButton(double x, double y, String text, Color color, int mapWidth) {
        double btnHalfWidth = mapWidth * 0.14;
        double btnHalfHeight = 0.35;
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.filledRectangle(x, y, btnHalfWidth, btnHalfHeight);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setPenRadius(0.002);
        StdDraw.rectangle(x, y, btnHalfWidth, btnHalfHeight);
        StdDraw.setPenColor(color);
        StdDraw.text(x, y, text);
    }

    private static void openMapMenu(Map2D map, int w, int h) {
        // 1. חישוב גדלים ומיקומים
        double btnHalfW = w * 0.14;
        double btnHalfH = 0.35;
        // המיקום האופקי - מיושר בדיוק מתחת לכפתור Map הראשי
        double menuX = (w * 0.20) - 0.5;
        // חישוב גבהים ל-4 כפתורים (Save, Merge, Rescale, New)
        // מתחילים מתחת לסרגל הכלים ויורדים ב-0.75 כל פעם
        double saveY = (h + 0.6) - 0.75;
        double mergeY = saveY - 0.75;
        double rescaleY = mergeY - 0.75;
        double newY = rescaleY - 0.75;
        // 2. ציור התפריט (4 כפתורים)
        drawButton(menuX, saveY, "Save", Color.BLACK, w);
        drawButton(menuX, mergeY, "Merge", Color.BLACK, w);
        drawButton(menuX, rescaleY, "Rescale", Color.BLACK, w);
        drawButton(menuX, newY, "New", Color.BLACK, w);
        StdDraw.show();
        // מחכים שהמשתמש ישחרר את הלחיצה שפתחה את התפריט
        // אחרת הלחיצה עלולה להיחשב בטעות כבחירה בתפריט שנפתח
        while (StdDraw.mousePressed()) { StdDraw.pause(20); }
        // 3. לולאת הבחירה - מחכה עד שהמשתמש יבחר משהו או ילחץ בחוץ
        boolean selectionMade = false;
        while (!selectionMade) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                // --- בדיקה 1: Save ---
                if (isInside(x, y, menuX, saveY, btnHalfW, btnHalfH)) {
                    saveMap(map, "map.txt");
                    System.out.println("Map Saved!");
                    selectionMade = true;
                }
                // --- בדיקה 2: Merge ---
                else if (isInside(x, y, menuX, mergeY, btnHalfW, btnHalfH)) {
                    // מנסה לטעון מפה מקובץ ולחבר אותה לנוכחית
                    Map2D mapFromFile = loadMap("map.txt");
                    if (mapFromFile != null && map.sameDimensions(mapFromFile)) {
                        map.addMap2D(mapFromFile);
                        System.out.println("Maps Merged!");
                    } else {
                        System.out.println("Merge failed: check file or dimensions.");
                    }
                    selectionMade = true;
                }
                // --- בדיקה 3: Rescale (השינוי: מעבר להקלדה במקלדת) ---
                else if (isInside(x, y, menuX, rescaleY, btnHalfW, btnHalfH)) {
                    // קורא לפונקציה שפותחת חלונית הקלדה
                    inputAndRescale(map, w, h);
                    selectionMade = true;
                }
                // --- בדיקה 4: New Map ---
                else if (isInside(x, y, menuX, newY, btnHalfW, btnHalfH)) {
                    map.mul(0); // איפוס מהיר של המפה
                    System.out.println("New Map Created!");
                    selectionMade = true;
                }
                // --- לחיצה מחוץ לתפריט ---
                else {
                    // סוגר את התפריט בלי לעשות כלום
                    selectionMade = true;
                }
                // המתנה לשחרור העכבר בסיום הפעולה
                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
            }
            StdDraw.pause(20);
        }
    }

    private static boolean isInside(double x, double y, double centerX, double centerY, double halfW, double halfH) {
        return (x >= centerX - halfW && x <= centerX + halfW && y >= centerY - halfH && y <= centerY + halfH);
    }

    private static void inputAndRescale(Map2D map, int w, int h) {
        StringBuilder input = new StringBuilder();
        boolean done = false;
        String errorMessage = "";
        // גבול עליון למניעת קריסה - אתה יכול לשנות את זה
        // 30 זה גבול בטוח ל-StdDraw שיעבוד חלק יחסית
        int MAX_LIMIT = 30;
        while (StdDraw.hasNextKeyTyped()) { StdDraw.nextKeyTyped(); }
        while (!done) {
            double centerX = (w * 0.20) - 0.5;
            double centerY = h / 2.0;
            // ציור החלונית (הגדלתי קצת שתכיל הודעות ארוכות)
            StdDraw.setPenColor(new Color(255, 255, 255, 230));
            StdDraw.filledRectangle(centerX, centerY, w * 0.35, 2.0);
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.setPenRadius(0.005);
            StdDraw.rectangle(centerX, centerY, w * 0.35, 2.0);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 16));
            StdDraw.text(centerX, centerY + 0.8, "Enter a number:");
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.text(centerX, centerY, input.toString() + "_");
            if (!errorMessage.isEmpty()) {
                StdDraw.setPenColor(Color.RED);
                StdDraw.setFont(new Font("Arial", Font.BOLD, 12));
                StdDraw.text(centerX, centerY - 0.8, errorMessage);
            }
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == '\n') { // Enter
                    try {
                        double scalar = Double.parseDouble(input.toString());
                        if (scalar > 0) {
                            // חישוב הגודל העתידי
                            int newW = (int)(w * scalar);
                            int newH = (int)(h * scalar);

                            // --- הגנה 1: קטן מדי ---
                            if (newW < 1 || newH < 1) {
                                errorMessage = "Too small! (Min 1x1)";
                                input.setLength(0);
                            }
                            // --- הגנה 2: גדול מדי (התוספת החדשה) ---
                            else if (newW > MAX_LIMIT || newH > MAX_LIMIT) {
                                errorMessage = "Too big! (Max " + MAX_LIMIT + "x" + MAX_LIMIT + ")";
                                input.setLength(0);
                            }
                            // --- הכל תקין ---
                            else {
                                map.rescale(scalar, scalar);
                                System.out.println("Rescaled to: " + newW + "x" + newH);
                                done = true;
                            }
                        } else {
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
    private static int openColorMenu(int w, int h, int currentColor) {
        double menuX = (w * 0.50) - 0.5;
        double startY = (h + 0.6) - 0.75;
        // רשימת הצבעים והשמות שלהם
        // אתה יכול להוסיף/להוריד לפי מה שיש לך ב-getColor
        String[] names = {"Black", "White", "Green", "Blue", "Red", "Yellow"};
        Color[] colors = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
        int[] ids = {-1, 0, 2, 4, 1, 3}; // ה-ID של הצבע במפה
        // ציור התפריט
        for (int i = 0; i < names.length; i++) {
            // מציירים כל כפתור אחד מתחת לשני
            double y = startY - (i * 0.75);
            drawButton(menuX, y, names[i], colors[i], w);
        }
        StdDraw.show();
        while (StdDraw.mousePressed()) { StdDraw.pause(20); }
        // לולאת בחירה
        while (true) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                double btnW = w * 0.14;
                double btnH = 0.35;
                // בדיקה על איזה צבע לחצו
                for (int i = 0; i < names.length; i++) {
                    double btnY = startY - (i * 0.75);
                    if (isInside(x, y, menuX, btnY, btnW, btnH)) {
                        System.out.println("Selected Color: " + names[i]);
                        // מחכים לשחרור
                        while (StdDraw.mousePressed()) { StdDraw.pause(10); }
                        return ids[i]; // מחזירים את ה-ID שנבחר
                    }
                }
                // אם לחצו בחוץ - לא משנים צבע ויוצאים
                // מחכים לשחרור
                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
                return currentColor;
            }
            StdDraw.pause(20);
        }
    }

    // שינינו את הכותרת מ-void ל-String כדי שהיא תחזיר את המצב שנבחר
    private static String openAlgoMenu(Map2D map, int w, int h) {
        double btnHalfW = w * 0.14;
        double btnHalfH = 0.35;

        // מיקום התפריט - מתחת לכפתור Algo
        double menuX = (w * 0.80) - 0.5;

        double pathY = (h + 0.6) - 0.75;
        double fillY = pathY - 0.75;

        // ציור הכפתורים
        drawButton(menuX, pathY, "Shortest Path", Color.BLACK, w);
        drawButton(menuX, fillY, "Bucket Fill", Color.BLACK, w);

        StdDraw.show();

        // מחכים לשחרור הלחיצה שפתחה את התפריט
        while (StdDraw.mousePressed()) { StdDraw.pause(20); }

        boolean selectionMade = false;
        String selectedAction = "None"; // ברירת מחדל - אם לא נבחר כלום

        while (!selectionMade) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                // 1. בדיקה אם נבחר Shortest Path
                if (isInside(x, y, menuX, pathY, btnHalfW, btnHalfH)) {
                    System.out.println("Selected: Shortest Path");
                    selectedAction = "Path"; // מחזירים את המילה "Path"
                    selectionMade = true;
                }
                // 2. בדיקה אם נבחר Bucket Fill
                else if (isInside(x, y, menuX, fillY, btnHalfW, btnHalfH)) {
                    System.out.println("Selected: Bucket Fill");
                    selectedAction = "Fill"; // מחזירים את המילה "Fill"
                    selectionMade = true;
                }
                // 3. לחיצה בחוץ (ביטול)
                else {
                    selectionMade = true;
                }
                // המתנה לשחרור העכבר
                while (StdDraw.mousePressed()) { StdDraw.pause(10); }
            }
            StdDraw.pause(20);
        }
        return selectedAction; // החזרת התשובה ל-drawMap
    }
}

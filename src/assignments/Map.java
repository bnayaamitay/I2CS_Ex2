package assignments;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

//Student ID: 213741051

/**
 * This class represents a 2D map (int[w][h]) as a "screen" or a raster matrix or maze over integers.
 * This is the main class needed to be implemented.
 *
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D, Serializable{

    private int[][] _map;
    private int _h;
    private int _w;

	/**
	 * Constructs a w*h 2D raster map with an init value v.
     * @param w width
     * @param h height
     * @param v initial value
	 */
	public Map(int w, int h, int v) {init(w, h, v);}
	/**
	 * Constructs a square map (size*size).
	 * @param size map dimension
	 */
	public Map(int size) {this(size,size, 0);}
	
	/**
	 * Constructs a map from a given 2D array (Deep Copy).
	 * @param data source array
	 */
	public Map(int[][] data) {
		init(data);
	}

    /**
     * Constructs a default 1x1 map with value 0.
     */
    public Map() {
        this(1,1,0);
    }

	@Override
	public void init(int w, int h, int v) {
    if (w <= 0 || h <= 0) {
        throw new IllegalArgumentException("Invalid width or height");
    }
    _w = w;
    _h = h;
    _map = new int[w][h];
    for (int i = 0; i < w; i++) {
        for (int j = 0; j < h; j++) {
            _map[i][j] = v;
        }
    }
	}

	@Override
    /**
     * Initializes the map from a 2D array.
     * Throws RuntimeException if the array is null, empty, or jagged.
     * Creates a deep copy of the data.
     */
	public void init(int[][] arr) {
    if (arr == null || arr.length == 0 || arr[0].length == 0 ) {
        throw new IllegalArgumentException("Array is null or empty");
    }
    _w = arr.length;
    _h = arr[0].length;
    _map = new int[_w][_h];
    for (int i = 0; i < _w; i++) {
        if (arr[i].length != _h) {
            throw new IllegalArgumentException("Array is ragged");
        }
        for (int j = 0; j < _h; j++) {
            _map[i][j] = arr[i][j];
        }
    }
	}

	@Override
    /**
     * Returns a deep copy of the map's grid.
     * Changes to the returned array will not affect the map.
     */
	public int[][] getMap() {
		int[][] ans = null;
        ans = new int[_w][_h];
        for (int i = 0; i < _w; i++) {
            for (int j = 0; j < _h; j++) {
                ans[i][j] = _map[i][j];
            }
        }
		return ans;
	}

	@Override
	public int getWidth() {
        return _w;
    }

	@Override
	public int getHeight() {
        return _h;
    }

	@Override
	public int getPixel(int x, int y) {
        int ans = -1;
        if (x < 0 || y < 0 || x >= _w || y >= _h) {
            throw new IndexOutOfBoundsException("Coordinate ("+ x + "," + y + ") out of bounds");
        }
        ans = _map[x][y];
        return ans;
    }

	@Override
	public int getPixel(Pixel2D p) {
        int ans = -1;
        if (p == null) throw new IllegalArgumentException("Pixel cannot be null");
        ans = getPixel(p.getX(), p.getY());
        return ans;
	}

	@Override
	public void setPixel(int x, int y, int v) {
        if (x < 0 || y < 0 || x >= _w || y >= _h) {
            throw new IndexOutOfBoundsException("Coordinate ("+ x + "," + y + ") out of bounds");
        }
        _map[x][y] = v;
    }

	@Override
	public void setPixel(Pixel2D p, int v) {
        if (p == null) throw new IllegalArgumentException("Pixel cannot be null");
        int x = p.getX();
        int y = p.getY();
        if (x < 0 || y < 0 || x >= _w || y >= _h) {
            throw new IndexOutOfBoundsException("Coordinate ("+ x + "," + y + ") out of bounds");
        }
        _map[x][y] = v;
	}

    @Override
    public boolean isInside(Pixel2D p) {
        boolean ans = true;
        if (p == null) {
            ans = false;
        }
        if (p.getX() < 0 || p.getY() < 0 || p.getX() >= _w || p.getY() >= _h) {
            ans = false;
        }
        return ans;
    }

    @Override
    public boolean sameDimensions(Map2D p) {
        boolean ans = false;
        if (p != null && p.getWidth() == _w && p.getHeight() == _h) {
            ans = true;
        }
        return ans;
    }

    @Override
    public void addMap2D(Map2D p) {
    if (sameDimensions(p)) {
        for (int i = 0; i < _w; i++) {
            for (int j = 0; j < _h; j++) {
                setPixel(i, j,getPixel(i,j) + p.getPixel(i, j));
            }
        }
    }
    }

    @Override
    public void mul(double scalar) {
        for (int i = 0; i < _w; i++) {
            for (int j = 0; j < _h; j++) {
                setPixel(i, j,(int)(getPixel(i,j) * scalar));
            }
        }
    }

    @Override
    /**
     * 1. Calculate new dimensions based on the scaling factors.
     * 2. Create a new array for the resized map.
     * 3. Iterate through every pixel of the NEW map:
     * - Calculate the corresponding source coordinate: source = current / scale.
     * - Copy the value from the source map to the new map.
     * 4. Update the map's width, height, and data array.
     */
    public void rescale(double sx, double sy) {
    int newW = (int) (_w * sx);
    int newH = (int) (_h * sy);
    int[][] newMap = new int[newW][newH];
    for (int i = 0; i < newW; i++) {
        for (int j = 0; j < newH; j++) {
            int oldI = (int) (i/sx);
            int oldJ = (int) (j/sy);
            newMap[i][j] = _map[oldI][oldJ];
        }
    }
    _w = newW;
    _h = newH;
    _map = newMap;
    }

    @Override
    /**
     * 1. Iterate over the entire map grid (width x height).
     * 2. For each pixel, calculate the Euclidean distance to the center.
     * 3. Check condition: distance^2 < radius^2.
     * 4. If true, set the pixel to the given color.
     */
    public void drawCircle(Pixel2D center, double rad, int color) {
    for (int i = 0; i <_w; i++) {
        for (int j = 0; j < _h; j++) {
            double dx = i - center.getX();
            double dy = j - center.getY();
            double distance = dx*dx + dy*dy;
            if (distance < rad*rad) {
                setPixel(i,j,color);
            }
        }
    }
    }

    @Override
    /**
     * 1. Handle special cases: vertical or horizontal lines (simple loops).
     * 2. Calculate differences (dx, dy).
     * 3. If the line is "shallow" (width >= height):
     * - Iterate along the X-axis.
     * - Calculate Y using linear equation y = mx + b.
     * 4. If the line is "steep" (height > width):
     * - Iterate along the Y-axis.
     * - Calculate X using linear equation x = my + b.
     */
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
    int x1 = Math.min(p1.getX(), p2.getX());
    int x2 = Math.max(p2.getX(), p1.getX());
    int y1 = Math.min(p1.getY(), p2.getY());
    int y2 = Math.max(p2.getY(), p1.getY());
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);
        if (x1 == x2) {
            for (int y = y1; y <= y2; y++) {
                setPixel(p1.getX(), y, color);
            }
            return;
        }
        if (y2 == y1) {
            for (int x = x1; x <= x2; x++) {
                setPixel(x, p1.getY(), color);
            }
            return;
        }
        if (dx >= dy) {
            double m = (double) ((p1.getY() - p2.getY())) / (p1.getX() - p2.getX());
            double b = p1.getY() - (p1.getX() * m);
            for (int x = x1; x <= x2; x++) {
                int y = (int) Math.round(x * m + b);
                setPixel(x,y,color);
            }
        }
        else {
            double m = (double) (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
            double b = p1.getX() - m * p1.getY();
            for (int y = y1; y <= y2; y++) {
                int x = (int) Math.round(m * y + b);
                setPixel(x, y, color);
            }
        }
    }

    @Override
    /**
     * 1. Calculate the Minimum and Maximum X and Y coordinates from the two input points.
     * (This ensures the loop works correctly regardless of point order).
     * 2. Iterate from minY to maxY.
     * 3. Inside, iterate from minX to maxX.
     * 4. Set every pixel in this range to the specified color.
     */
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {
    int minX = Math.min(p1.getX(), p2.getX());
    int maxX = Math.max(p1.getX(), p2.getX());
    int minY = Math.min(p1.getY(), p2.getY());
    int maxY = Math.max(p1.getY(), p2.getY());
    for (int y = minY; y <= maxY; y++) {
        for (int x = minX; x <= maxX; x++) {
            setPixel(x, y,color);
        }
    }
    }

    @Override
    public boolean equals(Object ob) {
        boolean ans = false;
        if (this == ob) {
            ans = true;
            return ans;
        }
        else if (ob == null || !(ob instanceof Map2D)) {
            return ans;
        }
        else {
            Map2D map = (Map2D) ob;
            if (!sameDimensions(map)) {
                return ans;
            }
            for (int x = 0; x < _w; x++) {
                for (int y = 0; y < _h; y++) {
                        if (this.getPixel(x,y) != map.getPixel(x,y)) {
                            return ans;
                        }
                }
            }
        }
        ans = true;
        return ans;
    }

	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
     *
     * Algorithm: Flood Fill using BFS.
     * 1. Check if the start point is valid. If the target color is the same, return 0.
     * 2. Initialize a queue and add the starting pixel. Change its color.
     * 3. While the queue is not empty:
     * a. Poll the current pixel.
     * b. Check all 4 neighbors (up, down, left, right).
     * c. If cyclic mode is on, wrap coordinates around the map.
     * d. If a neighbor is valid and has the original color:
     * - Update its color.
     * - Add it to the queue.
     * 4. Return the total count of colored pixels.
     */
	public int fill(Pixel2D xy, int new_v,  boolean cyclic) {
		int ans = -1;
        if (!isInside(xy)) {
            return ans;
        }
        ans = 0;
        if (getPixel(xy.getX(), xy.getY()) == new_v) {
            return ans;
        }
        int old_v = getPixel(xy.getX(), xy.getY());
        Queue<Pixel2D> pendingP = new LinkedList<>();
        pendingP.add(xy);
        setPixel(xy.getX(), xy.getY(), new_v);
        ans ++;
        int [] dx = {1,-1,0,0};
        int [] dy = {0,0,1,-1};
        while (!pendingP.isEmpty()) {
            Pixel2D current = pendingP.poll();
            int cx = current.getX();
            int cy = current.getY();
            for (int i = 0; i < 4; i++) {
                int nx = cx + dx[i];
                int ny = cy + dy[i];
                if (cyclic) {
                    nx = (nx + _w) % _w;
                    ny = (ny + _h) % _h;
                }
                Pixel2D neighbor = new Index2D(nx, ny);
                if (isInside(neighbor) && getPixel(nx, ny) == old_v) {
                    setPixel(nx, ny, new_v);
                    pendingP.add(neighbor);
                    ans++;
                }
            }
        }
        return ans;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 *
     * 1. Validate start/end points and ensure they are not obstacles.
     * 2. Phase 1 - Distance Calculation:
     * - Initialize a distance matrix with -1 (unvisited).
     * - Run BFS from the start point to calculate distances to all reachable pixels.
     * - Stop early if the target is reached.
     * 3. Phase 2 - Path Reconstruction (Backtracking):
     * - If target is unreachable, return null.
     * - Start from the target and move to a neighbor with (current_distance - 1).
     * - Repeat until the start point is reached.
     * 4. Return the reconstructed path array.
     */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
		Pixel2D[] ans = null;
        if (!isInside(p1) || !isInside(p2)) {
            return ans;
        }
        if (p1.equals(p2)) {
            ans = new Pixel2D[1];
            ans[0] = p1;
            return ans;
        }
        if (getPixel(p1.getX(), p1.getY()) == obsColor || getPixel(p2.getX(), p2.getY()) == obsColor) {
            return ans;
        }
        int[][] distances = new int[_w][_h];
        for (int i = 0; i < _w; i++) {
            for (int j = 0; j < _h; j++) {
                distances[i][j] = -1;
            }
        }
        distances[p1.getX()][p1.getY()] = 0;
        Queue<Pixel2D> pendingP = new LinkedList<>();
        pendingP.add(p1);
        int[] dx = {1,-1,0,0};
        int[] dy = {0,0,1,-1};
        while (!pendingP.isEmpty()) {
            Pixel2D current = pendingP.poll();
            if (current.equals(p2)) {
                break;
            }
            int cx = current.getX();
            int cy = current.getY();
            for (int i = 0; i < 4; i++) {
                int nx = cx + dx[i];
                int ny = cy + dy[i];
                if (cyclic) {
                    nx = (nx + _w) % _w;
                    ny = (ny + _h) % _h;
                }
                Pixel2D neighbor = new Index2D(nx, ny);
                if (isInside(neighbor) && getPixel(nx, ny) != obsColor && distances[nx][ny] == -1) {
                    distances[nx][ny] = distances[cx][cy] + 1;
                    pendingP.add(neighbor);
                }
            }
        }
        if (distances[p2.getX()][p2.getY()] == -1) {
            return ans;
        }
        int dist = distances[p2.getX()][p2.getY()];
        ans = new Pixel2D[dist + 1];
        Pixel2D currentBack = p2;
        ans[dist] = p2;
        for (int i = dist - 1; i >= 0; i--) {
            int cx = currentBack.getX();
            int cy = currentBack.getY();
            for (int k = 0; k < 4; k++) {
                int nx = cx + dx[k];
                int ny = cy + dy[k];
                if (cyclic) {
                    nx = (nx + _w) % _w;
                    ny = (ny + _h) % _h;
                }
                if (isInside(new Index2D(nx, ny)) && distances[nx][ny] == i) {
                    ans[i] = new Index2D(nx, ny);
                    currentBack = ans[i];
                    break;
                }
            }
        }
		return ans;
	}

    @Override
    /**
     * Algorithm: Full Breadth-First Search (BFS).
     * 1. Validate the start point. If invalid or an obstacle, return null.
     * 2. Create a new result map initialized with -1.
     * 3. Set the start point distance to 0 and add it to a queue.
     * 4. While the queue is not empty:
     * a. Poll the current pixel.
     * b. For each valid neighbor (handling cyclic boundaries):
     * - If unvisited, set distance = current_distance + 1.
     * - Add neighbor to the queue.
     * 5. Return the map containing distances from the start point.
     */
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map2D ans = null;
        if (!isInside(start) || getPixel(start) == obsColor) {
            return ans;
        }
        ans = new Map(_w, _h, -1);
        ans.setPixel(start, 0);
        Queue<Pixel2D> pendingP = new LinkedList<>();
        pendingP.add(start);
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        while (!pendingP.isEmpty()) {
            Pixel2D current = pendingP.poll();
            int cx = current.getX();
            int cy = current.getY();
            for (int i = 0; i < 4; i++) {
                int nx = cx + dx[i];
                int ny = cy + dy[i];
                if (cyclic) {
                    nx = (nx + _w) % _w;
                    ny = (ny + _h) % _h;
                }
                Pixel2D neighbor = new Index2D(nx, ny);
                if (isInside(neighbor) && getPixel(nx, ny) != obsColor && ans.getPixel(neighbor) == -1) {
                    int dist = ans.getPixel(current.getX(), current.getY()) + 1;
                    ans.setPixel(neighbor, dist);
                    pendingP.add(neighbor);
                }
            }
        }
        return ans;
    }
	////////////////////// Private Methods ///////////////////////

}

package assignments;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

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
	 * @param w
	 * @param h
	 * @param v
	 */
	public Map(int w, int h, int v) {init(w, h, v);}
	/**
	 * Constructs a square map (size*size).
	 * @param size
	 */
	public Map(int size) {this(size,size, 0);}
	
	/**
	 * Constructs a map from a given 2D array.
	 * @param data
	 */
	public Map(int[][] data) {
		init(data);
	}

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
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {
    int dx = Math.abs(p1.getX() - p2.getX());
    int dy = Math.abs(p1.getY() - p2.getY());
        if (p1.getX() == p2.getX()) {
            int y1 = Math.min(p1.getY(), p2.getY());
            int y2 = Math.max(p1.getY(), p2.getY());
            for (int y = y1; y <= y2; y++) {
                setPixel(p1.getX(), y, color);
            }
            return;
        }
        if (p1.getY() == p2.getY()) {
            int x1 = Math.min(p1.getX(), p2.getX());
            int x2 = Math.max(p1.getX(), p2.getX());
            for (int x = x1; x <= x2; x++) {
                setPixel(x, p1.getY(), color);
            }
            return;
        }
        if (dx >= dy) {
        int x1 = Math.min(p1.getX(), p2.getX());
        int x2 = Math.max(p2.getX(), p1.getX());
        double m = (double) ((p1.getY() - p2.getY())) / (p1.getX() - p2.getX());
        double b = p1.getY() -(p1.getX() * m);
        for (int x = x1; x <= x2; x++) {
            int y = (int) Math.round(x * m + b);
            setPixel(x,y,color);
        }
    }
    else {
        int y1 = Math.min(p1.getY(), p2.getY());
        int y2 = Math.max(p1.getY(), p2.getY());
        double m = (double) (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
        double b = p1.getX() - m * p1.getY();
        for (int y = y1; y <= y2; y++) {
            int x = (int) Math.round(m * y + b);
            setPixel(x, y, color);
        }
    }
    }

    @Override
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

		return ans;
	}
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map2D ans = null;  // the result.

        return ans;
    }
	////////////////////// Private Methods ///////////////////////

}

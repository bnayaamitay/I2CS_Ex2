package assignments;
import java.io.Serializable;
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

	@Override
	public void init(int w, int h, int v) {
    if ( w<= 0 || h <= 0) {
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
        int ans = -1;
        ans = _w;
        return ans;
    }

	@Override
	public int getHeight() {
        int ans = -1;
        ans = _h;
        return ans;
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
        ans = _map[p.getX()][p.getY()];
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

    }

    @Override
    public void drawLine(Pixel2D p1, Pixel2D p2, int color) {

    }

    @Override
    public void drawRect(Pixel2D p1, Pixel2D p2, int color) {

    }

    @Override
    public boolean equals(Object ob) {
        boolean ans = false;

        return ans;
    }
	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	public int fill(Pixel2D xy, int new_v,  boolean cyclic) {
		int ans = -1;

		return ans;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic) {
		Pixel2D[] ans = null;  // the result.

		return ans;
	}
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic) {
        Map2D ans = null;  // the result.

        return ans;
    }
	////////////////////// Private Methods ///////////////////////

}

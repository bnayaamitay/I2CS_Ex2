package assignments;

//Student ID: 213741051

public class Index2D implements Pixel2D {
    private int _x;
    private int _y;

    public Index2D(int w, int h) {
        _x = w;
        _y = h;
    }

    public Index2D(Pixel2D other) {
        if (other == null) throw new RuntimeException("Pixel2D is null");
        _x = other.getX();
        _y = other.getY();
    }

    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getY() {
        return _y;
    }

    @Override
    public double distance2D(Pixel2D p2) {
        if (p2 == null) throw new RuntimeException("Pixel2D is null");
        double dx = p2.getX() - _x;
        double dy = p2.getY() - _y;
        double dist = Math.sqrt((dx * dx) + (dy * dy));
        return dist;
    }

    @Override
    public String toString() {
        String ans = null;
        ans = "(" + _x + "," + _y + ")";
        return ans;
    }

    @Override
    public boolean equals(Object p) {
        boolean ans = true;
        if (p == null) {
            ans = false;
            return ans;
        }
        else if (!(p instanceof Pixel2D)) {
            ans = false;
            return ans;
        }
        else {
            Pixel2D other = (Pixel2D) p;
            if (_x != other.getX() || _y != other.getY()) {
                ans = false;
            }
        }
        return ans;
    }
}

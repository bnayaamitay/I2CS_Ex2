package assignments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Intro2CS, 2026A, this is a very
 */
class MapTest {
    /**
     */
    private int[][] _map_3_3 = {{0,1,0}, {1,0,1}, {0,1,0}};
    private Map2D _m0, _m1, _m3_3;
    @BeforeEach
    public void setUp() {
        _m0 = new Map();
        _m1 = new Map();
        _m3_3 = new Map(_map_3_3);
    }

    @Test
    @Timeout(value = 1, unit = SECONDS)
    void init() {
        int[][] bigarr = new int [500][500];
        _m1.init(bigarr);
        assertEquals(bigarr.length, _m1.getWidth());
        assertEquals(bigarr[0].length, _m1.getHeight());
        Pixel2D p1 = new Index2D(3,2);
        _m1.fill(p1,1, true);
    }

    @Test
    void testInit() {
        _m0.init(_map_3_3);
        _m1.init(_map_3_3);
        assertEquals(_m0, _m1);
    }

    @Test
    void testEquals() {
        assertEquals(_m0,_m1);
        _m0.init(_map_3_3);
        _m1.init(_map_3_3);
        assertEquals(_m0,_m1);
    }

    /**
     * Verifies that the map rejects "ragged" arrays (rows with different lengths).
     */
    @Test
    void testInitRaggedArray() {
        int[][] ragged = {
                {1, 2},
                {3}
        };
        assertThrows(IllegalArgumentException.class, () -> {
            new Map(ragged);
        }
        , "Should throw exception for ragged (jagged) array");
    }

    /**
     * Verifies that the constructor throws an exception for negative width or height.
     */
    @Test
    void testInitInvalidDimensions() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Map(0, 10, 1);
        }, "Should throw exception for width 0");

        assertThrows(IllegalArgumentException.class, () -> {
            new Map(10, -5, 1);
        }, "Should throw exception for negative height");
    }

    @Test
    void testEncapsulation() {
        int[][] data = {{5}};
        Map map = new Map(data);
        // Modify the original array externally
        data[0][0] = 100;
        // Verify the map was NOT changed (Deep Copy)
        assertEquals(5, map.getPixel(0,0),
                "Map should rely on a deep copy, not the original array reference");
        int[][] exported = map.getMap();
        exported[0][0] = 999;
        assertEquals(5, map.getPixel(0,0),
                "Modifying result of getMap() should not affect the actual map");
    }

    /**
     * Verifies that get/set methods throw exceptions for coordinates outside the map.
     */
    @Test
    void testOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> _m3_3.getPixel(3, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> _m3_3.getPixel(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> _m3_3.setPixel(3, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> _m3_3.getPixel(null));
    }

    /** Tests valid coordinates within the map boundaries.
    */
    @Test
    void testIsInside_ValidPoints() {
        assertTrue(_m3_3.isInside(new Index2D(0, 0)));
        assertTrue(_m3_3.isInside(new Index2D(1, 1)));
        assertTrue(_m3_3.isInside(new Index2D(2, 2)));
    }

    /** Tests that negative coordinates are considered outside the map.
    */
    @Test
    void testIsInside_Negative() {
        assertFalse(_m3_3.isInside(new Index2D(-1, 0)));
        assertFalse(_m3_3.isInside(new Index2D(0, -1)));
    }

    /** Tests that coordinates exceeding the map dimensions are considered outside.
    */
    @Test
    void testIsInside_OutOfBounds() {
        assertFalse(_m3_3.isInside(new Index2D(3, 0)));
        assertFalse(_m3_3.isInside(new Index2D(0, 3)));
        assertFalse(_m3_3.isInside(new Index2D(100, 100)));
    }

    /** Tests that comparing to a map with the same dimensions returns true. */
    @Test
    void testSameDimensions_True() {
        Map2D other = new Map(3, 3,0);
        assertTrue(_m3_3.sameDimensions(other));
    }

    /** Tests that comparing to maps with different dimensions returns false. */
    @Test
    void testSameDimensions_DifferentSize() {
        Map2D diffWidth = new Map(4, 3,0);
        assertFalse(_m3_3.sameDimensions(diffWidth));

        Map2D diffHeight = new Map(3, 4,0);
        assertFalse(_m3_3.sameDimensions(diffHeight));
    }

    /** Tests that comparing to null returns false without throwing an exception. */
    @Test
    void testSameDimensions_Null() {
        assertFalse(_m3_3.sameDimensions(null));
    }

    /** Tests adding two maps with the same dimensions. Values should sum up. */
    @Test
    void testAddMap2D_Success() {
        Map baseMap = new Map(3, 3, 10);
        Map mapToAdd = new Map(3, 3, 5);
        baseMap.addMap2D(mapToAdd);
        assertEquals(15, baseMap.getPixel(0, 0));
        assertEquals(15, baseMap.getPixel(2, 2));
    }

    /** Tests that adding a map with different dimensions does nothing.
     */
    @Test
    void testAddMap2D_DifferentDimensions() {
        Map baseMap = new Map(3, 3, 10);
        Map wrongSizeMap = new Map(4, 3, 5);
        baseMap.addMap2D(wrongSizeMap);
        assertEquals(10, baseMap.getPixel(0, 0));
    }

    /** Tests that adding null does nothing.
     */
    @Test
    void testAddMap2D_Null() {
        Map baseMap = new Map(3, 3, 10);
        baseMap.addMap2D(null);
        assertEquals(10, baseMap.getPixel(0, 0));
    }

    /** Tests multiplying map values by a scalar. */
    @Test
    void testMul_Scalar() {
        Map map = new Map(3, 3, 10);
        map.mul(2.5);
        assertEquals(25, map.getPixel(0, 0));
        assertEquals(25, map.getPixel(2, 2));
    }

    /** Tests multiplying by a negative scalar and then by zero. */
    @Test
    void testMul_NegativeAndZero() {
        Map map = new Map(3, 3, 10);
        map.mul(-2.0);
        assertEquals(-20, map.getPixel(0, 0));
        map.mul(0.0);
        assertEquals(0, map.getPixel(0, 0));
    }

    /** Tests upscaling the map (zooming in). Dimensions should double. */
    @Test
    void testRescale_Upscale() {
        Map map = new Map(2, 2, 0);
        map.setPixel(0, 0, 1);
        map.rescale(2.0, 2.0);
        assertEquals(4, map.getWidth());
        assertEquals(4, map.getHeight());
        assertEquals(1, map.getPixel(0, 0));
        assertEquals(1, map.getPixel(1, 1));
        assertEquals(0, map.getPixel(2, 2));
    }

    /** Tests downscaling the map (zooming out). Dimensions should shrink. */
    @Test
    void testRescale_Downscale() {
        Map map = new Map(4, 4, 5);
        map.rescale(0.5, 0.5);
        assertEquals(2, map.getWidth());
        assertEquals(2, map.getHeight());
        assertEquals(5, map.getPixel(0, 0));
    }

    /** Tests asymmetric scaling (stretching width vs height). */
    @Test
    void testRescale_Asymmetric() {
        Map map = new Map(10, 10, 0);
        map.rescale(2.0, 0.5);
        assertEquals(20, map.getWidth());
        assertEquals(5, map.getHeight());
    }

    /** Tests drawing a circle in the center of the map. */
    @Test
    void testDrawCircle_Center() {
        Map map = new Map(20, 20, 0);
        Pixel2D center = new Index2D(10, 10);
        map.drawCircle(center, 5.0, 1);

        assertEquals(1, map.getPixel(10, 10));
        assertEquals(1, map.getPixel(13, 10));
        assertEquals(0, map.getPixel(15, 15));
        assertEquals(0, map.getPixel(0, 0));
    }

    /** Tests drawing a circle at the corner (0,0) to check boundary calculations. */
    @Test
    void testDrawCircle_Corner() {
        Map map = new Map(10, 10, 0);
        Pixel2D center = new Index2D(0, 0);
        map.drawCircle(center, 3.0, 2);

        assertEquals(2, map.getPixel(0, 0));
        assertEquals(2, map.getPixel(2, 0));
        assertEquals(2, map.getPixel(1, 1));
        assertEquals(0, map.getPixel(3, 0));
    }

    /** Tests drawing simple horizontal and vertical lines. */
    @Test
    void testDrawLine_Straight() {
        Map map = new Map(10, 10, 0);
        Pixel2D p1 = new Index2D(1, 1);
        Pixel2D p2 = new Index2D(5, 1);
        Pixel2D p3 = new Index2D(1, 5);

        map.drawLine(p1, p2, 1);
        map.drawLine(p1, p3, 1);

        assertEquals(1, map.getPixel(3, 1));
        assertEquals(1, map.getPixel(1, 3));
        assertEquals(1, map.getPixel(5, 1));
        assertEquals(1, map.getPixel(1, 5));
    }

    /** Tests drawing a shallow diagonal line where width > height. */
    @Test
    void testDrawLine_DiagonalShallow() {
        Map map = new Map(10, 10, 0);
        Pixel2D p1 = new Index2D(0, 0);
        Pixel2D p2 = new Index2D(4, 2);
        map.drawLine(p1, p2, 2);
        assertEquals(2, map.getPixel(0, 0));
        assertEquals(2, map.getPixel(2, 1));
        assertEquals(2, map.getPixel(4, 2));
    }

    /** Tests drawing a steep diagonal line where height > width. */
    @Test
    void testDrawLine_DiagonalSteep() {
        Map map = new Map(10, 10, 0);
        Pixel2D p1 = new Index2D(0, 0);
        Pixel2D p2 = new Index2D(2, 4);
        map.drawLine(p1, p2, 3);
        assertEquals(3, map.getPixel(0, 0));
        assertEquals(3, map.getPixel(1, 2));
        assertEquals(3, map.getPixel(2, 4));
    }

    /** Tests drawing a rectangle defined by top-left and bottom-right points. */
    @Test
    void testDrawRect_Normal() {
        Map map = new Map(10, 10, 0);
        Pixel2D p1 = new Index2D(1, 1);
        Pixel2D p2 = new Index2D(3, 3);
        map.drawRect(p1, p2, 5);
        assertEquals(5, map.getPixel(1, 1));
        assertEquals(5, map.getPixel(3, 3));
        assertEquals(5, map.getPixel(2, 2));
        assertEquals(0, map.getPixel(4, 4));
    }

    /** Tests drawing a rectangle where the points are swapped (end point is smaller than start). */
    @Test
    void testDrawRect_SwappedPoints() {
        Map map = new Map(10, 10, 0);
        Pixel2D p1 = new Index2D(5, 5);
        Pixel2D p2 = new Index2D(2, 2);
        map.drawRect(p1, p2, 3);
        assertEquals(3, map.getPixel(2, 2));
        assertEquals(3, map.getPixel(5, 5));
        assertEquals(3, map.getPixel(3, 4));
    }

    /** Tests scenarios where equals should return true (reflexive and symmetric). */
    @Test
    void testEquals_True() {
        Map map1 = new Map(3, 3, 5);
        Map map2 = new Map(3, 3, 5);
        assertTrue(map1.equals(map1));
        assertTrue(map1.equals(map2));
    }

    /** Tests scenarios where equals should return false (different content, size, type, or null). */
    @Test
    void testEquals_False() {
        Map map1 = new Map(3, 3, 5);
        Map mapDiffPixel = new Map(3, 3, 5);
        mapDiffPixel.setPixel(0, 0, 9);
        Map mapDiffSize = new Map(4, 4, 5);
        assertFalse(map1.equals(mapDiffPixel));
        assertFalse(map1.equals(mapDiffSize));
        assertFalse(map1.equals(null));
        assertFalse(map1.equals("String Object"));
    }

    /** Tests a standard fill bounded by walls. */
    @Test
    void testFill_NonCyclic_Bounded() {
        Map map = new Map(5, 5, 0);
        for(int y = 0; y < 5; y++) {
            map.setPixel(2, y, 1);
        }
        int changed = map.fill(new Index2D(0, 0), 5, false);
        assertEquals(10, changed);
        assertEquals(5, map.getPixel(0, 0));
        assertEquals(5, map.getPixel(1, 4));
        assertEquals(1, map.getPixel(2, 2));
        assertEquals(0, map.getPixel(3, 0));
    }

    /** Tests a cyclic fill that wraps around the edges of the map. */
    @Test
    void testFill_Cyclic_Wrap() {
        Map map = new Map(3, 3, 0);
        map.setPixel(0, 1, 1);
        map.setPixel(1, 1, 1);
        map.setPixel(2, 1, 1);
        int changed = map.fill(new Index2D(0, 0), 2, true);
        assertEquals(6, changed);
        assertEquals(2, map.getPixel(0, 0));
        assertEquals(2, map.getPixel(0, 2));
        assertEquals(1, map.getPixel(0, 1));
    }

    /** Tests edge cases: coordinates outside map and filling with same color. */
    @Test
    void testFill_EdgeCases() {
        Map map = new Map(3, 3, 0);
        assertEquals(-1, map.fill(new Index2D(10, 10), 1, false));
        assertEquals(0, map.fill(new Index2D(0, 0), 0, false));
    }

    /** Tests finding a path around an obstacle in a non-cyclic map. */
    @Test
    void testShortestPath_WithObstacle() {
        Map map = new Map(5, 5, 0);
        map.setPixel(2, 1, 1);
        map.setPixel(2, 2, 1);
        map.setPixel(2, 3, 1);
        Pixel2D p1 = new Index2D(1, 2);
        Pixel2D p2 = new Index2D(3, 2);
        Pixel2D[] path = map.shortestPath(p1, p2, 1, false);
        assertNotNull(path);
        assertEquals(p1, path[0]);
        assertEquals(p2, path[path.length - 1]);
        assertTrue(path.length > 3);
    }

    /** Tests cyclic pathfinding where wrapping around edges is shorter. */
    @Test
    void testShortestPath_Cyclic() {
        Map map = new Map(10, 10, 0);
        Pixel2D p1 = new Index2D(0, 5);
        Pixel2D p2 = new Index2D(9, 5);
        Pixel2D[] cyclicPath = map.shortestPath(p1, p2, 1, true);
        assertEquals(2, cyclicPath.length);
        Pixel2D[] regularPath = map.shortestPath(p1, p2, 1, false);
        assertEquals(10, regularPath.length);
    }

    /** Tests edge cases: unreachable target, start equals end, and start is obstacle. */
    @Test
    void testShortestPath_EdgeCases() {
        Map map = new Map(3, 3, 0);
        Pixel2D p1 = new Index2D(0, 0);
        Pixel2D[] samePoint = map.shortestPath(p1, p1, 1, false);
        assertEquals(1, samePoint.length);
        map.setPixel(1, 0, 1);
        map.setPixel(0, 1, 1);
        Pixel2D blocked = new Index2D(0, 0);
        Pixel2D target = new Index2D(2, 2);
        assertNull(map.shortestPath(blocked, target, 1, false));
        map.setPixel(2, 2, 1);
        assertNull(map.shortestPath(new Index2D(1, 1), target, 1, false));
    }

    /** Tests that distances are calculated correctly in a simple open map. */
    @Test
    void testAllDistance_Simple() {
        Map map = new Map(5, 5, 0);
        Pixel2D start = new Index2D(2, 2);
        Map2D distances = map.allDistance(start, 1, false);
        assertEquals(0, distances.getPixel(2, 2));
        assertEquals(1, distances.getPixel(2, 3));
        assertEquals(2, distances.getPixel(2, 4));
        assertEquals(4, distances.getPixel(0, 0));
    }

    /** Tests that cyclic mode correctly calculates shorter paths via edges. */
    @Test
    void testAllDistance_Cyclic() {
        Map map = new Map(10, 10, 0);
        Pixel2D start = new Index2D(0, 0);
        Map2D distances = map.allDistance(start, 1, true);
        assertEquals(0, distances.getPixel(0, 0));
        assertEquals(1, distances.getPixel(9, 0));
        assertEquals(1, distances.getPixel(0, 9));
        assertEquals(2, distances.getPixel(9, 9));
    }

    /** Tests that obstacles block the distance calculation and invalid starts return null. */
    @Test
    void testAllDistance_ObstaclesAndInvalid() {
        Map map = new Map(3, 3, 0);
        map.setPixel(1, 0, 1);
        map.setPixel(1, 1, 1);
        map.setPixel(1, 2, 1);
        Pixel2D start = new Index2D(0, 0);
        Map2D distances = map.allDistance(start, 1, false);
        assertEquals(0, distances.getPixel(0, 0));
        assertEquals(-1, distances.getPixel(2, 2));
        map.setPixel(0, 0, 1);
        assertNull(map.allDistance(start, 1, false));
    }
}
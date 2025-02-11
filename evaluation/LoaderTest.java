package evaluation;

import java.util.ArrayList;

import poi.QuadTree;
import utils.Trajectory;

public class LoaderTest {
    public static void main(String[] args) {
        int objNB = 10000;
        int poiNB = 10000;
        Loader l = new Loader(objNB, poiNB);
        // load info
        System.out.println("POIs         size: " + l.points.size());
        System.out.println("Trajectory   size: " + l.trjs.size());
        System.out.println("Trajectory A size: " + l.A.size());
        System.out.println("Trajectory B size: " + l.B.size());
        System.out.println("Lon range: " + l.minLon + " -> " + l.maxLon);
        System.out.println("Lat range: " + l.minLat + " -> " + l.maxLat);
        System.out.println("X range: " + l.minX + " -> " + l.maxX + ": " + (l.maxX -
                l.minX));
        System.out.println("Y range: " + l.minY + " -> " + l.maxY + ": " + (l.maxY -
                l.minY));
        System.out.println("Global Max-Min speed: " + l.maxSpeed + "/" + l.minSpeed);
    }
}

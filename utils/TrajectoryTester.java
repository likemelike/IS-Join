package utils;

import java.util.ArrayList;

import evaluation.Loader;
import evaluation.Settings;
import poi.QuadTree;

public class TrajectoryTester {
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
        System.out.println("Global Max-Min meanSpeed: " + l.maxSpeed + "/" + l.minSpeed);
        // query and database trajectories
        ArrayList<Trajectory> A = l.A;
        ArrayList<Trajectory> B = l.B;
        // check if any filtered trajectory is valid
        // global meanSpeed info of valid trajectory
        double maxSpeed = -Double.MAX_VALUE;
        double minSpeed = Double.MAX_VALUE;
        for (Trajectory newTrj : l.trjs) {
            maxSpeed = newTrj.maxSpeed > maxSpeed ? newTrj.maxSpeed : maxSpeed;
            minSpeed = newTrj.minSpeed < minSpeed ? newTrj.minSpeed : minSpeed;
            assert newTrj.sampleSize == Settings.tsNB;
            assert newTrj.isDelete() == false;
        }
        System.out.println("Global Max-Min meanSpeed: " + l.maxSpeed + "/" + l.minSpeed);
    }
}

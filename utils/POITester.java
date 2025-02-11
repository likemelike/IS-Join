package utils;

import java.util.ArrayList;
import java.util.List;

import evaluation.Loader;
import poi.QuadTree;

public class POITester {
    public static void main(String[] args) {
        int objNB = 100;
        int poiNB = 1000000;
        Loader l = new Loader(objNB, poiNB);
        QuadTree qTree = l.qTree;
        int intervalNum = 6;

        long exactSearchTime = 0;
        long qTreeTime = 0;
        for (Trajectory newTrj : l.trjs) {
            TimeIntervalMR mr = newTrj.getIntervalMR(0, 2, qTree, intervalNum);
            List<Point> res = new ArrayList<>();
            double[] mbr = mr.getEllipseMBR();
            long t1 = System.currentTimeMillis();
            res = l.qTree.query(mbr[0], mbr[2], mbr[1], mbr[3]);
            long t2 = System.currentTimeMillis();
            qTreeTime += (t2 - t1);

            t1 = System.currentTimeMillis();
            int count = 0;
            for (Point db : l.points) {
                if (db.x >= mbr[0] && db.x <= mbr[1] && db.y >= mbr[2] && db.y <= mbr[3]) {
                    count++;
                }
            }
            System.out.println(newTrj.objectID + ": " + count);
            assert res.size() == count;
            t2 = System.currentTimeMillis();
            exactSearchTime += (t2 - t1);
        }
        System.out.println(exactSearchTime + "/" + qTreeTime);
        System.out.println("Global Max-Min meanSpeed: " + l.maxSpeed + "/" + l.minSpeed);
    }
}

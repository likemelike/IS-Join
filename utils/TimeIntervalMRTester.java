package utils;

import java.util.ArrayList;
import java.util.List;

import evaluation.Loader;
import poi.QuadTree;

public class TimeIntervalMRTester {
    public static void main(String[] args) {
        int objNB = 1000;
        int poiNB = 100000000;
        Loader l = new Loader(objNB, poiNB);
        QuadTree qTree = l.qTree;
        int intervalNum = 6;
        for (Trajectory newTrj : l.trjs) {
            System.out.println(newTrj.objectID);
            TimeIntervalMR bead = newTrj.getIntervalMR(0, 2, qTree, intervalNum);
            assert bead.a > 0;
            assert bead.b > 0;
            assert bead.a >= bead.b;
            assert bead.nextLocation.timestamp - bead.curLocation.timestamp > 0;
            assert bead.meanSpeed >= 0 && bead.meanSpeed <= bead.maxSpeed : "meanSpeed: " + bead.meanSpeed;
            assert bead.curLocation.objectID == bead.nextLocation.objectID;

            for (TimePointMR len : bead.timePointMRs) {
                double distance = Math
                        .sqrt((len.Ax - len.Bx) * (len.Ax - len.Bx) + (len.Ay - len.By) * (len.Ay - len.By));
                assert distance < len.r1 + len.r2 : "No Interseection";
                assert len.r1 != 0;
                assert len.r2 != 0;
                System.out.println(len.POI_NB);
            }

        }
        System.out.println("Global Max-Min meanSpeed: " + l.maxSpeed + "/" + l.minSpeed);
    }
}

package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import poi.QuadTree;

public class TimeIntervalMR implements Comparable<TimeIntervalMR> {

    public int objectID;
    public Location curLocation;
    public Location nextLocation;
    // the center of the ellipse (time-interval motion range)
    public double[] center = new double[2];
    // long radius
    public double a = 0;
    // short radius
    public double b = 0;
    public double meanSpeed;
    public double maxSpeed;
    // the angle of the ellipse
    public double angle;
    // the minimal-bound-rectangle of the ellipse
    double[] MBR;
    // generate a set of discrete time-point motion range within this time-interval
    // motion range
    public ArrayList<TimePointMR> timePointMRs = new ArrayList<>();
    // pois in this time-interval motion range
    public HashSet<Point> POIs = new HashSet<>();

    public TimeIntervalMR(Location curLocation, Location nextLocation, double maxSpeed, QuadTree qTree,
            int intervalNum) {
        this.objectID = curLocation.objectID;
        this.curLocation = curLocation;
        this.nextLocation = nextLocation;
        this.maxSpeed = maxSpeed;
        this.angle = Math.atan2(nextLocation.y - curLocation.y, nextLocation.x - curLocation.x);
        // generate time-interval motion range --- get a,b, and meanSpeed
        location2ellipse();
        // generate time-point motion ranges within this ellipse --- get
        timePointMRs = generateTimePointMRs(intervalNum, qTree);
        // POIs = POIsWithinThis(qTree);
    }

    // two locations form an ellipse
    public void location2ellipse() {
        center[0] = (curLocation.x + nextLocation.x) / 2;
        center[1] = (curLocation.y + nextLocation.y) / 2;
        meanSpeed = curLocation.distTo(nextLocation) / (nextLocation.timestamp - curLocation.timestamp);
        a = maxSpeed * (nextLocation.timestamp - curLocation.timestamp) / 2;
        b = Math.sqrt(4 * a * a
                - (Math.pow(curLocation.x - nextLocation.x, 2) + Math.pow(curLocation.y - nextLocation.y, 2))) / 2;
    }

    /* generate a set of time-point motion ranges of size 'intervalNum' */
    public ArrayList<TimePointMR> generateTimePointMRs(int intervalNum, QuadTree qTree) {
        int duration = nextLocation.timestamp - curLocation.timestamp;
        for (int i = 1; i < intervalNum; i++) {
            // get time-point ranges of the two objects at several time points
            double r1 = maxSpeed * duration * i / intervalNum;
            double r2 = maxSpeed * duration * (intervalNum - i) / intervalNum;
            TimePointMR MR = new TimePointMR(curLocation.x, curLocation.y, nextLocation.x, nextLocation.y, r1, r2,
                    qTree);
            timePointMRs.add(MR);
            // add into time-interval POIs
            POIs.addAll(MR.POIs);
        }
        return timePointMRs;
    }

    public int dimensions() {
        return center.length;
    }

    public double get(int index) {
        return center[index];
    }

    @Override
    public int compareTo(TimeIntervalMR that) {
        for (int i = 0; i < 2; i++) {
            double v1 = this.center[i];
            double v2 = that.center[i];
            if (v1 > v2) {
                return +1;
            }
            if (v1 < v2) {
                return -1;
            }
        }
        return 0;
    }

    /* get POIs within this motion range */
    // public ArrayList<Point> POIsWithinThis(QuadTree1 qTree) {
    // double[] mbr = this.getEllipseMBR();
    // List<Point> POIsCandidate = qTree.findAll(mbr[0], mbr[2], mbr[1], mbr[3]);
    // ArrayList<Point> res = new ArrayList<>();
    // for (Point p : POIsCandidate) {
    // if (isPointInEllipse(p.x, p.y)) {
    // res.add(p);
    // }
    // }
    // // System.out.println(POIsCandidate.size() + "/" + res.size());
    // return res;
    // }

    // Time-interval pruning
    public double upperBound1To(TimeIntervalMR that) {
        if (this.POIs.size() == 0 || that.POIs.size() == 0) {
            return 0;
        }
        double simUB = 0;
        int m = this.timePointMRs.size();
        for (int i = 0; i < m; i++) {
            TimePointMR mr1 = this.timePointMRs.get(i);
            TimePointMR mr2 = that.timePointMRs.get(i);
            if (mr1.equals(mr2)) {
                simUB += 1;
                continue;
            }
            if (mr1.POI_NB == 0 || mr2.POI_NB == 0) {
                continue;
            }
            simUB += 1;
        }
        return simUB / m;
    }

    // Time-Point pruning
    public double upperBound2To(TimeIntervalMR that) {
        if (this.POIs.size() == 0 || that.POIs.size() == 0) {
            return 0;
        }
        double simUB = 0;
        int m = this.timePointMRs.size();
        for (int i = 0; i < m; i++) {
            TimePointMR mr1 = this.timePointMRs.get(i);
            TimePointMR mr2 = that.timePointMRs.get(i);
            if (mr1.equals(mr2)) {
                simUB += 1;
                continue;
            }
            if (mr1.POI_NB == 0 || mr2.POI_NB == 0) {
                continue;
            }
            double minSize = 0;
            int maxSize = 0;
            if (mr1.POI_NB > mr2.POI_NB) {
                minSize = mr2.POI_NB;
                maxSize = mr1.POI_NB;
            } else {
                minSize = mr1.POI_NB;
                maxSize = mr2.POI_NB;
            }
            simUB += (minSize / maxSize);
        }

        return simUB / m;
    }

    // the exact intersection similarity to another time-interval motion range
    public double simTo(TimeIntervalMR that) {
        if (this.POIs.size() == 0 || that.POIs.size() == 0) {
            return 0;
        }
        assert this.timePointMRs.size() == that.timePointMRs.size();
        double sim = 0;
        int m = this.timePointMRs.size();
        for (int i = 0; i < m; i++) {
            TimePointMR tpThis = this.timePointMRs.get(i);
            TimePointMR tpThat = that.timePointMRs.get(i);
            ArrayList<Point> poiThis = tpThis.POIs;
            ArrayList<Point> poiThat = tpThat.POIs;
            int thisSize = tpThis.POI_NB;
            int thatSize = tpThat.POI_NB;
            if (tpThis.equals(tpThat)) {
                sim += 1;
                continue;
            }
            if (thisSize == 0 || thatSize == 0) {
                continue;
            }
            ArrayList<Point> intersection = new ArrayList<>(poiThis);
            intersection.retainAll(poiThat);
            sim += Math.pow(intersection.size(), 2) / (thisSize * thatSize);
        }
        return sim / m;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return curLocation.toString() + "->\n" + nextLocation.toString() + "\nCenter: " + Arrays.toString(center)
                + String.format("\nspeed: %.3f maxSpeed: %.3f a: %.3f b %.3f", meanSpeed, maxSpeed, a, b);
    }

    // get MBR of this time-interval motion range
    public double[] getEllipseMBR() {
        double centerX = center[0];
        double centerY = center[1];
        double majorRadius = a;
        double minorRadius = b;
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);

        // Find the coordinates of the four corners of the unrotated bounding box
        double halfWidth = majorRadius;
        double halfHeight = minorRadius;
        double x1 = -halfWidth;
        double y1 = -halfHeight;
        double x2 = halfWidth;
        double y2 = -halfHeight;
        double x3 = halfWidth;
        double y3 = halfHeight;
        double x4 = -halfWidth;
        double y4 = halfHeight;

        // Rotate the coordinates of the four corners
        double rotatedX1 = x1 * cosTheta - y1 * sinTheta;
        double rotatedY1 = x1 * sinTheta + y1 * cosTheta;
        double rotatedX2 = x2 * cosTheta - y2 * sinTheta;
        double rotatedY2 = x2 * sinTheta + y2 * cosTheta;
        double rotatedX3 = x3 * cosTheta - y3 * sinTheta;
        double rotatedY3 = x3 * sinTheta + y3 * cosTheta;
        double rotatedX4 = x4 * cosTheta - y4 * sinTheta;
        double rotatedY4 = x4 * sinTheta + y4 * cosTheta;

        // Find the new coordinates of the rotated bounding box
        double minX = centerX + Math.min(rotatedX1, Math.min(rotatedX2, Math.min(rotatedX3, rotatedX4)));
        double minY = centerY + Math.min(rotatedY1, Math.min(rotatedY2, Math.min(rotatedY3, rotatedY4)));
        double maxX = centerX + Math.max(rotatedX1, Math.max(rotatedX2, Math.max(rotatedX3, rotatedX4)));
        double maxY = centerY + Math.max(rotatedY1, Math.max(rotatedY2, Math.max(rotatedY3, rotatedY4)));
        return new double[] { minX, maxX, minY, maxY };
    }

    // check if a point is within this time-interval motion range or not
    public boolean isPointInEllipse(double px, double py) {
        // Translate point to ellipse's coordinate system
        double translatedX = px - (curLocation.x + nextLocation.x) / 2;
        double translatedY = py - (curLocation.y + nextLocation.y) / 2;
        // Rotate point to align ellipse with the axes
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double rotatedX = cosTheta * translatedX + sinTheta * translatedY;
        double rotatedY = -sinTheta * translatedX + cosTheta * translatedY;
        // Apply ellipse equation
        double ellipseEquation = (rotatedX * rotatedX) / (a * a) + (rotatedY * rotatedY) / (b * b);
        return ellipseEquation <= 1;
    }
}

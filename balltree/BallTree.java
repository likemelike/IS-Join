package balltree;

import java.util.*;

import utils.TimeIntervalMR;

public class BallTree {
    public int minLeafNB;
    public int pointNB;
    public TimeIntervalMR[] db;
    public ArrayList<double[]> centers = new ArrayList<>();
    public ArrayList<Double> dbRadius = new ArrayList<>();
    public int[] indexes = null;
    // the number of node access when constructing the ball-tree
    public int constructCount = 0;
    // the number of node access when conduct queries
    public int searchCount = 0;

    public BallTree(int minLeafNB, TimeIntervalMR[] db) {
        this.minLeafNB = minLeafNB;
        this.db = db;
        this.pointNB = db.length;
        this.indexes = new int[pointNB];
        for (TimeIntervalMR data : db) {
            this.centers.add(data.center);
            this.dbRadius.add(data.a);
        }
    }

    public double[] getPivot(int[] indexes, int idxStart, int idxEnd) {
        assert idxEnd + 1 >= idxStart;
        double[] pivot = new double[2];
        pivot[0] = 0.0;
        pivot[1] = 0.0;
        for (int i = idxStart; i < idxEnd + 1; i++) {
            for (int j = 0; j < 2; j++) {
                pivot[j] = pivot[j] + centers.get(indexes[i])[j];
            }
        }
        int length = idxEnd - idxStart + 1;
        pivot[0] = pivot[0] / length;
        pivot[1] = pivot[1] / length;
        return pivot;
    }

    public double getRadius(int[] indexes, int idxStart, int idxEnd, double[] pivot) {
        // the maximum distance between pivot and other points centered at pivot
        double radius = 0;
        for (int i = idxStart; i < idxEnd + 1; i++) {
            // the distance between the center of ellipse and pivot+the radius of ellipse
            double temp = this.getDistance(pivot, centers.get(indexes[i])) + dbRadius.get(indexes[i]);
            if (temp > radius) {
                radius = temp;
            }
        }
        return radius;
    }

    public double getDistance(double[] A, double[] B) {
        return Math.sqrt(Math.pow(A[0] - B[0], 2) + Math.pow(A[1] - B[1], 2));
    }

    public BallNode buildBallTree() {
        assert this.minLeafNB >= 10;
        for (int i = 0; i < pointNB; i++) {
            this.indexes[i] = i;
        }
        double[] pivot = this.getPivot(this.indexes, 0, pointNB - 1);
        double radius = this.getRadius(this.indexes, 0, pointNB - 1, pivot);
        BallNode root = new BallNode(1, 0, pointNB - 1, pivot, radius);
        try {
            this.localBuildBallTree(root, 1);
        } catch (StackOverflowError e) {
            System.out.println("Stackoverflow: " + constructCount);
        }
        return root;
    }

    public void localBuildBallTree(BallNode node, int depth) {
        if (node.idxEnd - node.idxStart + 1 < this.minLeafNB) {
            return;
        } else {
            constructCount++;
            this.splitNode(node);
            this.localBuildBallTree(node.leftNode, depth + 1);
            this.localBuildBallTree(node.rightNode, depth + 1);
        }
    }

    public void splitNode(BallNode node) {
        // 1.get furthest1: furthest far from node's pivot
        double maxDist = 0.0;
        double tempDist = 0.0;
        int idx = node.idxStart;
        for (int i = node.idxStart; i < node.idxEnd + 1; i++) {
            tempDist = getDistance(centers.get(this.indexes[i]), node.pivot);
            if (tempDist > maxDist) {
                maxDist = tempDist;
                idx = i;
            }
        }
        double[] furthest1 = centers.get(this.indexes[idx]);
        // 2.get furthest2: furthest far from furthest1
        idx = node.idxStart;
        maxDist = 0;
        for (int i = node.idxStart; i < node.idxEnd + 1; i++) {
            tempDist = getDistance(centers.get(this.indexes[i]), furthest1);
            if (tempDist > maxDist) {
                maxDist = tempDist;
                idx = i;
            }
        }
        double[] furthest2 = centers.get(this.indexes[idx]);
        // 3.update indexes, split node
        int split = node.idxEnd;
        idx = node.idxStart;
        while (idx <= split) {
            maxDist = getDistance(centers.get(this.indexes[idx]), furthest1);
            tempDist = getDistance(centers.get(this.indexes[idx]), furthest2);
            if (maxDist >= tempDist) {
                int temp = this.indexes[idx];
                this.indexes[idx] = this.indexes[split];
                if (idx != split) {
                    this.indexes[split] = temp;
                    split = split - 1;
                } else {
                    idx = idx + 1;
                }
            } else {
                idx = idx + 1;
            }
        }
        // 4.update node info
        // leftNode:idxStart->split-1, rightNode:split->idxEnd
        double[] pivotL = getPivot(indexes, node.idxStart, split - 1);
        double radiusL = this.getRadius(indexes, node.idxStart, split - 1, pivotL);
        node.leftNode = new BallNode(node.id * 2, node.idxStart, split - 1, pivotL, radiusL);
        double[] pivotR = this.getPivot(indexes, split, node.idxEnd);
        double radiusR = this.getRadius(indexes, split, node.idxEnd, pivotR);
        node.rightNode = new BallNode(node.id * 2 + 1, split, node.idxEnd, pivotR, radiusR);
    }

    public ArrayList<TimeIntervalMR> searchRange(BallNode node,
            TimeIntervalMR qdata) {
        ArrayList<TimeIntervalMR> res = new ArrayList<>();
        this.rangeSearch(node, qdata, res);
        return res;
    }

    public void rangeSearch(BallNode node, TimeIntervalMR qdata, ArrayList<TimeIntervalMR> res) {
        searchCount++;
        double[] target = qdata.center;
        double range = qdata.a;
        if (this.getDistance(target, node.pivot) > node.radius + range)
            return;
        if (node.leftNode != null && node.rightNode != null) {
            double leftPivotDist = this.getDistance(target, node.leftNode.pivot);
            double rightPivotDist = this.getDistance(target, node.rightNode.pivot);
            if (leftPivotDist <= node.leftNode.radius + range) {
                rangeSearch(node.leftNode, qdata, res);
            }
            if (rightPivotDist <= node.rightNode.radius + range) {
                rangeSearch(node.rightNode, qdata, res);
            }
        } else if (node.leftNode != null || node.rightNode != null) {
            System.out.println("This node only one leaf, Unreasonable!");
        } else if (node.leftNode == null && node.rightNode == null) { // reach leaf node
            for (int i = node.idxStart; i < node.idxEnd + 1; i++) {
                double dist = this.getDistance(target, centers.get(indexes[i]));
                if (dist - dbRadius.get(indexes[i]) <= range) {
                    res.add(db[indexes[i]]);
                }
            }
        } else {
            System.out.println("Search unsuccessfully:" + node.id + " " + node.radius);
        }
    }

}

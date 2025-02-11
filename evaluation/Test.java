package evaluation;

import java.util.ArrayList;
import java.util.PriorityQueue;

import poi.QuadTree;
import utils.NN;
import utils.TimeIntervalMR;
import utils.Trajectory;

public class Test {

        // test all methods with the default settings
        public void singleTest() {
                Loader l = new Loader(Settings.objNB, Settings.poiNB);
                // query and database trajectories
                ArrayList<Trajectory> A = l.A;
                ArrayList<Trajectory> B = l.B;
                double theta = Settings.simThreshold;
                QuadTree poiTree = l.qTree;
                int start = Settings.start;
                int end = Settings.end;
                TimeIntervalMR[] MR_A = l.getMRSet(A, start, end, poiTree, Settings.intervalNum);
                TimeIntervalMR[] MR_B = l.getMRSet(B, start, end, poiTree, Settings.intervalNum);

                BFAlg bf = new BFAlg();
                MJAlg mt = new MJAlg();
                BJAlg bj = new BJAlg(Settings.minLeafNB);
                HBJAlg hbj = new HBJAlg(Settings.repartitionRatio, Settings.minLeafNB);

                int bjMatchNB = bj.tbJoin(MR_A, MR_B, theta);
                System.out.println(
                                String.format("BTR tbJoin MatchNB: %d CTime: %d FTime: %d\n", bjMatchNB,
                                                bj.cTime, bj.rangeJoinTime));
                int hbjMatchNB = hbj.tbJoin(MR_A, MR_B, theta, true);
                System.out.println(
                                String.format("HBTR tbJoin MatchNB: %d CTime: %d FTime: %d\n", hbjMatchNB,
                                                hbj.cTime, hbj.rangeJoinTime));

                int bfMatchNB = bf.tbJoin(MR_A, MR_B, theta);
                System.out.println(String.format("BFR tbJoin MatchNB: %d Time cost:%d\n",
                                bfMatchNB, bf.rangeJoinTime));
                int mtMatchNB = mt.tbJoin(MR_A, MR_B, theta);
                System.out.println(
                                String.format("MTR tbJoin MatchNB: %d CTime: %d FTime:%d\n",
                                                mtMatchNB, mt.cTime, mt.rangeJoinTime));
                assert bfMatchNB == hbjMatchNB;

                PriorityQueue<NN> mNNRes = mt.kJoin(MR_A, MR_B, Settings.k, true);
                System.out.println(String.format("M-tree NN-Join CTime: %d FTime: %d\n",
                                mt.cTime, mt.nnJoinTime));

                PriorityQueue<NN> hbjNNRes = hbj.kJoin(MR_A, MR_B, Settings.k, true);
                System.out.println(String.format("HBall-tree NN-Join CTime: %d FTime: %d\n",
                                hbj.cTime, hbj.nnJoinTime));

                PriorityQueue<NN> bjNNRes = bj.kJoin(MR_A, MR_B, Settings.k, true);
                System.out.println(String.format("Ball-tree NN-Join CTime: %d FTime: %d\n",
                                bj.cTime, bj.nnJoinTime));

                assert mNNRes.size() == Settings.k;
                assert bjNNRes.size() == Settings.k;
                assert hbjNNRes.size() == Settings.k;
                while (!bjNNRes.isEmpty()) {
                        NN nn1 = hbjNNRes.poll();
                        NN nn2 = bjNNRes.poll();
                        assert nn1.sim == nn2.sim : nn1 + " neq " + nn2;
                }

        }

        public static void main(String[] args) {
                Test test = new Test();
                test.singleTest();
        }

}

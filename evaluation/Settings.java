package evaluation;

public class Settings {
        public static String data = "Geolife"; // 10945 valid trajectories undex
        // maxspeed = 30 6087-10
        // public static String data = "Porto"; // 728097-30 valid trajectories under
        // 127881-10
        public static int expNB = 10;
        // the number of processed moving objects
        public static int objNB = (data == "Porto") ? 10000 : 8000;
        public static int[] objNBs = (Settings.data == "Porto") ? new int[] { 10000, 20000, 30000, 40000, 50000 }
                        : new int[] { 4000, 6000, 8000, 10000, 12000 };
        public static int poiNB = (data == "Porto") ? 1000000 : 6000000;
        public static int[] poiNBs = (data == "Porto") ? new int[] { 1000000, 2000000, 3000000, 4000000, 5000000 }
                        : new int[] { 200000, 400000, 600000, 800000, 1000000 };
        // the default threshold of similarity
        public static double simThreshold = 0.5;
        public static int start = 0;
        public static int end = (data == "Porto") ? 1 : 6;
        public static int[] Is = (data == "Porto") ? new int[] { 1, 2, 3, 4, 5 }
                        : new int[] { 2, 4, 6, 8, 10 };
        // kNN k
        public static int k = 600;
        // shuffle data
        public static boolean isShuffle = false;
        // the number of partitions of a time-interval
        public static int intervalNum = (data == "Porto") ? 3 : 6;
        public static int[] intervalNums = (data == "Porto") ? new int[] { 1, 2, 3, 4, 5 }
                        : new int[] { 2, 4, 6, 8, 10 };
        // the default sampling POI number
        public static int tsNB = 20;
        // the node size in the balltree
        public static int minLeafNB = 10;
        public static String geolifePath = "/data/home/like/trajectory/Geolife/Data/";
        public static String portoPath = "/data/home/like/trajectory/porto.csv";
        public static double repartitionRatio = 0.5;

        public static double maxSpeedThreshold = (data == "Porto") ? 0.03 : 0.07;
        public static double minSpeedThreshold = (data == "Porto") ? 0.0001 : 0.00001;
}
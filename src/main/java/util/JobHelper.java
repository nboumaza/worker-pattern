package util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class JobHelper {
    private static Random r = new Random();
    private static AtomicInteger lastID = new AtomicInteger(0);

    public static String getNexJobID() {
        return "Job-" + lastID.incrementAndGet();
    }

    /**
     * Get random number for thread sleeping
     *
     * @return random number between 20 and 100
     */
    public static int getRandomShortTime() {

        return r.nextInt(100) + 20;
    }


    /**
     * Get random number for thread sleeping
     *
     * @return random number between 700 and 10000
     */
    public static int getRandomLongTime() {
        return r.nextInt(10000) + 700;
    }
}

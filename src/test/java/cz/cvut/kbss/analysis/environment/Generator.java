package cz.cvut.kbss.analysis.environment;

import java.net.URI;
import java.util.Random;

public class Generator {

    private static Random random = new Random();

    private Generator() {
        throw new AssertionError();
    }

    public static URI generateUri() {
        return URI.create(Environment.BASE_URI + "randomInstance" + randomInt());
    }

    public static int randomInt(int lowerBound, int upperBound) {
        int rand;
        do {
            rand = random.nextInt(upperBound);
        } while (rand < lowerBound);
        return rand;
    }

    public static int randomInt() {
        return random.nextInt();
    }

    public static double randomDouble() {
        return random.nextDouble();
    }

}
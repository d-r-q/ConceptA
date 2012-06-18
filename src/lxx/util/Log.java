package lxx.util;

import lxx.ConceptA;

/**
 * User: Aleksey Zhidkov
 * Date: 18.06.12
 */
public class Log {

    private static final int NONE_LEVEL = -1;
    private static final int ERROR_LEVEL = 0;
    private static final int WARN_LEVEL = 1;
    private static final int DEBUG_LEVEL = 2;

    private static int logLevel = NONE_LEVEL;

    private Log() {
    }

    public static void increaseLogLevel() {
        if (logLevel < DEBUG_LEVEL) {
            logLevel++;
        }
    }

    public static void decreaseLogLevel() {
        if (logLevel > NONE_LEVEL) {
            logLevel--;
        }
    }

    public static boolean isWarnEnabled() {
        return logLevel >= WARN_LEVEL;
    }

    public static void warn(String msg) {
        System.out.println("[WARN][" + ConceptA.currentRound + ":" + ConceptA.currentTime + "]: " + msg);
    }
}

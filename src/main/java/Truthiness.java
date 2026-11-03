public class Truthiness {

    /**
     * This class was created because I am a C developer.
     */
    public static boolean toBool (int a) {
        return a != 0;
    }

    public static boolean toBool (String s) {
        return s != null;
    }

    public static boolean toBool (double a) {
        return a != 0.0;
    }

    public  static boolean toBool (float a) {
        return a != 0.0f;
    }

    public static boolean or(int a, int b) {
        return toBool(a) || toBool(b);
    }

    public static boolean and(int a, int b) {
        return toBool(a) && toBool(b);
    }

    public static boolean not(int a) {
        return !toBool(a);
    }

    public static int toInt(boolean a) {
        return (a) ? 1 : 0;
    }
}

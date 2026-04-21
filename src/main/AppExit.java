package main;

public final class AppExit {
    private AppExit() {
    }

    public static void request() {
        System.out.println("Thank you for using the 237 Bank App!");
        throw new Requested();
    }

    public static final class Requested extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}

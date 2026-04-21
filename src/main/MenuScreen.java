package main;

public final class MenuScreen {
    private static final String CLEAR_SCREEN = "\u001B[H\u001B[2J";

    private MenuScreen() {
    }

    public static void redraw() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }
}

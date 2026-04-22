package test;

import main.MenuScreen;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class MenuScreenTest {

    @Test
    public void testRedrawPrintsClearScreenEscapeSequence() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            MenuScreen.redraw();
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals("\u001B[H\u001B[2J", output.toString());
    }
}

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Scanner;

/**
 * This class contains tests for the Frontend implementation.
 * Each test simulates user input through TextUITester and 
 * checks the output for correctness.
 */
public class FrontendTests {

    /**
     * frontendTest1:
     * This test checks that the 'help' command displays the command menu.
     */
    @Test
    public void frontendTest1() {
        TextUITester tester = new TextUITester("help\nquit\n");
        BackendInterface backend = new Backend_Placeholder(new Tree_Placeholder());
        FrontendInterface frontend = new Frontend(new Scanner(System.in), backend);

        frontend.runCommandLoop();
        String output = tester.checkOutput();

        assertTrue(output.contains("Commands:"), "Help should display the commands menu.");
    }

    /**
     * frontendTest2:
     * This test checks that energy and show most recent commands
     * trigger backend calls and print song titles from the placeholder.
     */
    @Test
    public void frontendTest2() {
        TextUITester tester = new TextUITester("energy 3\nshow most recent\nquit\n");
        BackendInterface backend = new Backend_Placeholder(new Tree_Placeholder());
        FrontendInterface frontend = new Frontend(new Scanner(System.in), backend);

        frontend.runCommandLoop();
        String output = tester.checkOutput();

        assertTrue(output.contains("BO$$") || output.contains("Cake By The Ocean"),
            "Show most recent should print placeholder songs.");
    }

    /**
     * frontendTest3:
     * This test checks that invalid input is handled gracefully.
     */
    @Test
    public void frontendTest3() {
        TextUITester tester = new TextUITester("asdf\nquit\n");
        BackendInterface backend = new Backend_Placeholder(new Tree_Placeholder());
        FrontendInterface frontend = new Frontend(new Scanner(System.in), backend);

        frontend.runCommandLoop();
        String output = tester.checkOutput();

        assertTrue(output.toLowerCase().contains("invalid"),
            "Invalid commands should print an error message.");
    }
}

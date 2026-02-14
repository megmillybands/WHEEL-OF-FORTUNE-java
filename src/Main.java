import java.sql.SQLException;

public class Main {

    void main(String[] args) throws InterruptedException, SQLException {

        System.out.println("WHEEL...");
        Thread.sleep(1000);
        System.out.println("OF...");
        Thread.sleep(1000);
        System.out.println("FORTUNE!");
        Thread.sleep(1000);

        System.out.println("\nWelcome to Wheel of Fortune! Lined up for you are 3 rounds, each with a unique puzzle, plus a bonus round.\nThis game requires 3 players to play.\n");
        Thread.sleep(1000);

        System.out.println("Player 1, please enter your name: ");
        Wheel.createPlayer();
        System.out.println("Player 2, please enter your name: ");
        Wheel.createPlayer();
        System.out.println("Player 3, please enter your name: ");
        Wheel.createPlayer();

        System.out.println("\nAre you ready?");
        Thread.sleep(1000);
        System.out.println("\nLet's begin the first round.");
        Thread.sleep(1000);

        String puzzle = Wheel.getRandomPuzzle();

    }
}

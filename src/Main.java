import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    static String url = "jdbc:postgresql://localhost:5432/mydatabase";
    static String user = "postgres";
    static String password = "testingpassword";

    static Connection con;
    static {
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
        Players.createPlayer();
        System.out.println("Player 2, please enter your name: ");
        Players.createPlayer();
        System.out.println("Player 3, please enter your name: ");
        Players.createPlayer();

        System.out.println("\nAre you ready?");
        Thread.sleep(1000);
        System.out.println("\nLet's begin the first round.");
        Thread.sleep(1000);

        List<String> puzzle = Wheel.getRandomPuzzle();

        System.out.println("\nThe category is: \n" + puzzle.get(1));
        Thread.sleep(1000);
        System.out.println("\nAnd here is the puzzle: \n" + puzzle.get(0));
        Thread.sleep(1000);

        Statement stmt = con.createStatement();
        ResultSet rsPlayers = stmt.executeQuery("SELECT * FROM players");
        rsPlayers.next();
        String player1 = rsPlayers.getString("player_name");
        rsPlayers.next();
        String player2 = rsPlayers.getString("player_name");
        rsPlayers.next();
        String player3 = rsPlayers.getString("player_name");
        rsPlayers.close();

        int player = 1;
        String currentPlayer = player1;
        while (true) {
            if (player == 1) {
                currentPlayer = player1;
            } else if (player == 2) {
                currentPlayer = player2;
            } else if (player == 3) {
                currentPlayer = player3;
            } else {
                currentPlayer = player1;
            }

            System.out.println("\n" + currentPlayer + ", it's your turn!\n");

            System.out.println(puzzle.get(0));
            System.out.println(puzzle.get(1));

            System.out.println("\nWould you like to [spin] the wheel, [buy] a vowel, or [solve] the puzzle?\n");
            Scanner input = new Scanner(System.in);
            String choice = input.nextLine();

            if (choice.equals("spin")) {
                int result = Wheel.spinTheWheel();
                System.out.println("\n" + result + "!\n");
                Thread.sleep(1000);

                System.out.println("Now, pick a letter: \n");
                Scanner input2 = new Scanner(System.in);
                String letter = input2.nextLine();
            }

        }
    }
}

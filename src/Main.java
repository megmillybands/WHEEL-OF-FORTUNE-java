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

    @SuppressWarnings("BusyWait")
    void main() throws InterruptedException, SQLException {
        Statement stmt = con.createStatement();

        System.out.println("WHEEL...");
        Thread.sleep(1000);
        System.out.println("OF...");
        Thread.sleep(1000);
        System.out.println("FORTUNE!");
        Thread.sleep(1000);

        System.out.println("\nWelcome to Wheel of Fortune! Lined up for you are 3 rounds, each with a unique puzzle, plus a bonus round.\nThis game requires 3 players to play.");
        Thread.sleep(1000);

        System.out.println("\nPlayer 1, please enter your name: ");
        Players.createPlayer();
        System.out.println("\nPlayer 2, please enter your name: ");
        Players.createPlayer();
        System.out.println("\nPlayer 3, please enter your name: ");
        Players.createPlayer();

        System.out.println("\nAre you ready?");
        Thread.sleep(1000);
        System.out.println("\nLet's begin the first round.");
        Thread.sleep(1000);

        List<Object> puzzle = Wheel.getRandomPuzzle();

        System.out.println("\nThe category is: \n" + puzzle.get(1));
        Thread.sleep(1000);
        System.out.println("\nAnd here is the puzzle: \n" + puzzle.get(0));
        Thread.sleep(1000);

        ResultSet rsPlayers = stmt.executeQuery("SELECT * FROM players");
        rsPlayers.next();
        String player1 = rsPlayers.getString("player_name");
        rsPlayers.next();
        String player2 = rsPlayers.getString("player_name");
        rsPlayers.next();
        String player3 = rsPlayers.getString("player_name");
        rsPlayers.close();

        int player = 1;
        String currentPlayer;
        String puzzleHidden = "---";

        end:
            while (puzzleHidden.contains("-")) {
                if (player == 1) {
                    currentPlayer = player1;
                } else if (player == 2) {
                    currentPlayer = player2;
                } else if (player == 3) {
                    currentPlayer = player3;
                } else {
                    currentPlayer = player1;
                    player = 1;
                }

                System.out.println("\n" + currentPlayer + ", it's your turn!\n");
                Thread.sleep(1000);

                ResultSet rs = stmt.executeQuery("SELECT * FROM puzzles WHERE puzzle_id = " + puzzle.get(2));
                rs.next();
                puzzleHidden =  rs.getString("puzzle_hidden");
                rs.close();

                System.out.println(puzzleHidden);
                System.out.println(puzzle.get(1) + "\n");

                int currentPlayerMoney = Players.currentPlayerMoney(currentPlayer);
                System.out.println("You currently have $" + currentPlayerMoney + ".");

                System.out.println("Would you like to [spin] the wheel, [buy] a vowel, [solve] the puzzle, or [end] the game early?\n");
                Scanner input = new Scanner(System.in);
                String choice = input.nextLine().toLowerCase();


                switch (choice) {
                    case "spin" -> {
                        int spinResult = Wheel.spinTheWheel();
                        System.out.print("\nSpinning");
                        Thread.sleep(333);
                        System.out.print(".");
                        Thread.sleep(333);
                        System.out.print(".");
                        Thread.sleep(333);
                        System.out.print(".");
                        Thread.sleep(1000);
                        System.out.println("\n\n" + spinResult + "!\n");
                        Thread.sleep(1000);

                        System.out.println("Now, pick a letter: \n");
                        Scanner input2 = new Scanner(System.in);
                        char letter = input2.nextLine().toUpperCase().charAt(0);

                        if (Wheel.isVowel(letter)) {
                            System.out.println("\nPlease select a consonant.");
                            Thread.sleep(1000);
                        } else if (Wheel.selectedThisRound(letter)) {
                            System.out.println("\nThis letter has already been selected.");
                            Thread.sleep(1000);
                        } else {
                            if (!Wheel.flipLettersAndIsValid(letter, (Integer) puzzle.get(2), currentPlayer, spinResult)) {
                                player++;
                            }
                        }
                    }
                    case "buy" -> {
                        if (Wheel.canBuyVowel(currentPlayer)) {
                            System.out.println("\nWhich vowel would you like to buy?\n");
                            Scanner input3 = new Scanner(System.in);
                            char letter = input3.nextLine().toUpperCase().charAt(0);

                            if (!Wheel.isVowel(letter)) {
                                System.out.println("\nWhy buy a consonant when you could spin for one instead? Please select a vowel.");
                                Thread.sleep(1000);
                            } else if (Wheel.selectedThisRound(letter)) {
                                System.out.println("\nThis vowel has already been bought.");
                                Thread.sleep(1000);
                            } else {
                                if (!Wheel.buyVowelAndIsValid(letter, (Integer) puzzle.get(2), currentPlayer)) {
                                    player++;
                                }
                            }
                        } else {
                            System.out.println("\nYou don't have enough money to buy a vowel! Vowels cost $250.");
                            Thread.sleep(1000);
                        }
                    }
                    case "solve" -> {

                    }
                    case "end" -> {
                        break end;
                    }
                    default -> {
                        System.out.println("\nInvalid choice.");
                        Thread.sleep(1000);
                    }
                }
            }
            System.out.println("\nThank you for playing!");
            stmt.executeUpdate("TRUNCATE TABLE players");
            stmt.executeUpdate("UPDATE letters SET selected_this_round = false WHERE selected_this_round = true");
            PreparedStatement pstmt = Main.con.prepareStatement("UPDATE puzzles SET puzzle_hidden = ? WHERE puzzle_id = ?");
            pstmt.setString(1, (String) puzzle.get(0));
            pstmt.setInt(2, (Integer) puzzle.get(2));
            pstmt.executeUpdate();
    }
}

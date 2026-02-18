import java.sql.*;
import java.util.*;

public class Wheel {

    static List<Integer> wheel = List.of(800,350,450,5000,300,600,700,600,500,300,500,800,550,300,900,500,300,900,350,600,400,300);

    public static List<Object> getRandomPuzzle() throws SQLException {
        Statement stmt = Main.con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM puzzles ORDER BY random() LIMIT 1");

        String randomPuzzle = null;
        String randomCategory = null;
        int randomPuzzleId = 0;
        List<Object> randomItems = new ArrayList<>();

        if (rs.next()) {
            randomPuzzle = rs.getString("puzzle_hidden");
            randomCategory = rs.getString("category");
            randomPuzzleId = rs.getInt("puzzle_id");
            randomItems.add(randomPuzzle);
            randomItems.add(randomCategory);
            randomItems.add(randomPuzzleId);
        }

        return randomItems;
    }

    public static int spinTheWheel() {
        Random random = new Random();
        int randomIndex = random.nextInt(wheel.size());
        return wheel.get(randomIndex);
    }

    public static boolean isVowel(char letter) throws SQLException {
        Statement stmt = Main.con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM letters WHERE letter = '" + letter + "'");

        boolean letterIsVowel = false;
        boolean letterAlreadySelected = false;

        if (rs.next()) {
            letterIsVowel = rs.getBoolean("is_vowel");
            letterAlreadySelected = rs.getBoolean("selected_this_round");
        }

        return letterIsVowel;
    }

    public static boolean selectedThisRound(char letter) throws SQLException {
        Statement stmt = Main.con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM letters WHERE letter = '" + letter + "'");

        boolean letterAlreadySelected = false;

        if (rs.next()) {
            letterAlreadySelected = rs.getBoolean("selected_this_round");
        }

        PreparedStatement pstmt = Main.con.prepareStatement("UPDATE letters SET selected_this_round = ? WHERE letter = '" + letter + "'");
        pstmt.setBoolean(1, true);
        pstmt.executeUpdate();

        return letterAlreadySelected;
    }

    static void flipLetters(char letter, int puzzleId, String currentPlayer, int spinResult) throws SQLException {
        Statement stmt = Main.con.createStatement();

        ResultSet rsGetPuzzles = stmt.executeQuery("SELECT * FROM puzzles WHERE puzzle_id = " + puzzleId);
        String puzzleSolution = null;
        String puzzleHidden = null;
        if (rsGetPuzzles.next()) {
            puzzleSolution = rsGetPuzzles.getString("puzzle_solution");
            puzzleHidden = rsGetPuzzles.getString("puzzle_hidden");
        }
        rsGetPuzzles.close();

        int count = 0;
        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < puzzleSolution.length(); i++) {
            if (puzzleSolution.charAt(i) == letter) {
                count++;
                indexes.add(i);
            }
        }

        System.out.println("\nWe have " + count + " of that letter!");



        ResultSet rsGetPlayerMoney = stmt.executeQuery("SELECT * FROM players WHERE player_name = '" + currentPlayer + "'");
        PreparedStatement pstmt = Main.con.prepareStatement("UPDATE players SET player_money = ? WHERE player_name = '" + currentPlayer + "'");
        rsGetPlayerMoney.next();
        int existingPlayerMoney = rsGetPlayerMoney.getInt("player_money");
        pstmt.setDouble(1, (existingPlayerMoney + (spinResult * count)));
        pstmt.executeUpdate();

        rsGetPlayerMoney.close();

    }
}

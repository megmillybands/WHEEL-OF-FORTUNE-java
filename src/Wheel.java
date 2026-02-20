import java.sql.*;
import java.util.*;

public class Wheel {

    static List<Integer> wheel = List.of(800,350,450,5000,300,600,700,600,500,300,500,800,550,300,900,500,300,900,350,600,400,300);

    public static List<Object> getRandomPuzzle() throws SQLException {
        Statement stmt = Main.con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM puzzles ORDER BY random() LIMIT 1");

        String randomPuzzle;
        String randomCategory;
        int randomPuzzleId;
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

        if (rs.next()) {
            letterIsVowel = rs.getBoolean("is_vowel");
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


    static boolean flipLettersAndIsValid(char letter, int puzzleId, String currentPlayer, int spinResult) throws SQLException, InterruptedException {
        Statement stmtFlip = Main.con.createStatement();

        ResultSet rsGetPuzzles = stmtFlip.executeQuery("SELECT * FROM puzzles WHERE puzzle_id = " + puzzleId);
        String puzzleSolution = null;
        String puzzleHidden = null;
        if (rsGetPuzzles.next()) {
            puzzleSolution = rsGetPuzzles.getString("puzzle_solution");
            puzzleHidden = rsGetPuzzles.getString("puzzle_hidden");
        }

        int count = 0;
        int i = 0;
        List<Integer> indexes = new ArrayList<>();

        while (i < puzzleSolution.length()) {
            if (puzzleSolution.charAt(i) == letter) {
                count++;
                indexes.add(i);
            }
            i++;
        }

        if (count > 0) {
            System.out.println("\nWe have " + count + " of that letter!");
            Thread.sleep(1000);

            StringBuilder newHiddenPuzzle = new StringBuilder(puzzleHidden);

            for (int index : indexes) {
                if (index >= 0 && index < newHiddenPuzzle.length()) {
                    newHiddenPuzzle.setCharAt(index, letter);
                }
            }

            String newString = newHiddenPuzzle.toString();
            PreparedStatement pstmtUpdateHiddenPuzzle = Main.con.prepareStatement("UPDATE puzzles SET puzzle_hidden = ? WHERE puzzle_id = " + puzzleId);
            pstmtUpdateHiddenPuzzle.setString(1, newString);
            pstmtUpdateHiddenPuzzle.executeUpdate();
            rsGetPuzzles.close();

            ResultSet rsGetPlayerMoney = stmtFlip.executeQuery("SELECT * FROM players WHERE player_name = '" + currentPlayer + "'");
            PreparedStatement pstmtUpdatePlayerMoney = Main.con.prepareStatement("UPDATE players SET player_money = ? WHERE player_name = '" + currentPlayer + "'");
            rsGetPlayerMoney.next();
            int existingPlayerMoney = rsGetPlayerMoney.getInt("player_money");
            pstmtUpdatePlayerMoney.setDouble(1, (existingPlayerMoney + (spinResult * count)));
            pstmtUpdatePlayerMoney.executeUpdate();
            rsGetPlayerMoney.close();
            return true;
        }
        else {
            System.out.println("\nSorry! There are none of those in this puzzle.");
            Thread.sleep(1000);
            rsGetPuzzles.close();
            return false;
        }
    }

    static boolean buyVowelAndIsValid(char letter, int puzzleId, String currentPlayer) throws SQLException, InterruptedException {
        Statement stmtBuy = Main.con.createStatement();

        System.out.println("\nIt costs $250 to buy a vowel. Let's deduct the money from your total and see if we have any " + letter + "'s.");
        Thread.sleep(1000);

        ResultSet rsGetPlayerMoney = stmtBuy.executeQuery("SELECT * FROM players WHERE player_name = '" + currentPlayer + "'");
        PreparedStatement pstmtUpdatePlayerMoney = Main.con.prepareStatement("UPDATE players SET player_money = ? WHERE player_name = '" + currentPlayer + "'");
        rsGetPlayerMoney.next();
        int existingPlayerMoney = rsGetPlayerMoney.getInt("player_money");

        pstmtUpdatePlayerMoney.setDouble(1, (existingPlayerMoney - (250)));
        pstmtUpdatePlayerMoney.executeUpdate();
        rsGetPlayerMoney.close();

        ResultSet rsGetPuzzles = stmtBuy.executeQuery("SELECT * FROM puzzles WHERE puzzle_id = " + puzzleId);
        String puzzleSolution = null;
        String puzzleHidden = null;
        if (rsGetPuzzles.next()) {
            puzzleSolution = rsGetPuzzles.getString("puzzle_solution");
            puzzleHidden = rsGetPuzzles.getString("puzzle_hidden");
        }

        int count = 0;
        int i = 0;
        List<Integer> indexes = new ArrayList<>();

        while (i < puzzleSolution.length()) {
            if (puzzleSolution.charAt(i) == letter) {
                count++;
                indexes.add(i);
            }
            i++;
        }

        if (count > 0) {
            System.out.println("\nWe have " + count + " of that letter!");
            Thread.sleep(1000);

            StringBuilder newHiddenPuzzle = new StringBuilder(puzzleHidden);

            for (int index : indexes) {
                if (index >= 0 && index < newHiddenPuzzle.length()) {
                    newHiddenPuzzle.setCharAt(index, letter);
                }
            }

            String newString = newHiddenPuzzle.toString();
            PreparedStatement pstmtUpdateHiddenPuzzle = Main.con.prepareStatement("UPDATE puzzles SET puzzle_hidden = ? WHERE puzzle_id = " + puzzleId);
            pstmtUpdateHiddenPuzzle.setString(1, newString);
            pstmtUpdateHiddenPuzzle.executeUpdate();
            rsGetPuzzles.close();

            return true;
        }
        else {
            System.out.println("\nSorry! There are none of those in this puzzle.");
            Thread.sleep(1000);
            rsGetPuzzles.close();
            return false;
        }
    }

    static boolean canBuyVowel(String currentPlayer) throws SQLException {
        Statement stmtBuy = Main.con.createStatement();
        ResultSet rsGetPlayerMoney = stmtBuy.executeQuery("SELECT * FROM players WHERE player_name = '" + currentPlayer + "'");
        rsGetPlayerMoney.next();
        int existingPlayerMoney = rsGetPlayerMoney.getInt("player_money");
        return existingPlayerMoney >= 250;

    }
}

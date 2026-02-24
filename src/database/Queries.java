package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Queries {


    public static Integer currentPlayerMoney(String currentPlayer) throws SQLException {
        String sql = "SELECT player_money FROM players WHERE player_name = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currentPlayer);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("player_money");
            }
        }

        return null;
    }


    public static void assignPlayerToLetter(String currentPlayer, char letter) throws SQLException {
        String sql = "UPDATE letters SET selected_by = ? WHERE letter = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, currentPlayer);
            ps.setString(2, String.valueOf(letter));
            ps.executeUpdate();
        }
    }


    public static HashMap<String, String> showAssignedLetters() throws SQLException {
        String sql = "SELECT letter, selected_by FROM letters WHERE selected_by IS NOT NULL";

        HashMap<String, String> assignments = new HashMap<>();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                assignments.put(
                        rs.getString("letter"),
                        rs.getString("selected_by")
                );
            }
        }

        return assignments;
    }


    public static List<Object> getRandomPuzzle() throws SQLException {
        String sql = "SELECT puzzle_hidden, category, puzzle_id, puzzle_solution " +
                "FROM puzzles ORDER BY random() LIMIT 1";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                List<Object> puzzle = new ArrayList<>();
                puzzle.add(rs.getString("puzzle_hidden"));
                puzzle.add(rs.getString("category"));
                puzzle.add(rs.getInt("puzzle_id"));
                puzzle.add(rs.getString("puzzle_solution"));
                return puzzle;
            }
        }

        return null;
    }


    public static String updatePuzzle(List<Object> puzzle) throws SQLException {
        String sql = "SELECT puzzle_hidden FROM puzzles WHERE puzzle_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, (int) puzzle.get(2));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("puzzle_hidden");
            }
        }

        return null;
    }


    public static boolean isVowel(char letter) throws SQLException {
        String sql = "SELECT is_vowel FROM letters WHERE letter = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, String.valueOf(letter));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("is_vowel");
            }
        }

        return false;
    }


    public static boolean selectedThisRound(char letter) throws SQLException {
        String sql = "SELECT selected_this_round FROM letters WHERE letter = ?";
        String sql2 = "UPDATE letters SET selected_this_round = true WHERE letter = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             PreparedStatement ps2 = conn.prepareStatement(sql2)) {

            ps.setString(1, String.valueOf(letter));
            ResultSet rs = ps.executeQuery();

            boolean selected = false;
            if (rs.next()) {
                selected = rs.getBoolean("selected_this_round");
                if (selected) {
                    return true;
                } else {
                    ps2.setString(1, String.valueOf(letter));
                    ps2.executeUpdate();
                    return false;
                }
            }
        }

        return false;
    }


    public static boolean flipLettersAndIsValid(char letter, int puzzleId, String currentPlayer, int spinResult) throws SQLException, InterruptedException {

        String getPuzzleSql = "SELECT puzzle_solution, puzzle_hidden FROM puzzles WHERE puzzle_id = ?";
        String updatePuzzleSql = "UPDATE puzzles SET puzzle_hidden = ? WHERE puzzle_id = ?";
        String updateMoneySql = "UPDATE players SET player_money = ? WHERE player_name = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement getPuzzle = conn.prepareStatement(getPuzzleSql)) {

            getPuzzle.setInt(1, puzzleId);
            ResultSet rs = getPuzzle.executeQuery();

            if (!rs.next()) {
                return false;
            }

            String solution = rs.getString("puzzle_solution");
            String hidden = rs.getString("puzzle_hidden");

            List<Integer> indexes = new ArrayList<>();

            for (int i = 0; i < solution.length(); i++) {
                if (solution.charAt(i) == letter) {
                    indexes.add(i);
                }
            }

            if (indexes.isEmpty()) {
                System.out.println("\nSorry! There are none of those in this puzzle.");
                Thread.sleep(1000);
                return false;
            }

            StringBuilder updated = new StringBuilder(hidden);
            for (int index : indexes) {
                updated.setCharAt(index, letter);
            }

            try (PreparedStatement updatePuzzle = conn.prepareStatement(updatePuzzleSql);
                 PreparedStatement updateMoney = conn.prepareStatement(updateMoneySql)) {

                updatePuzzle.setString(1, updated.toString());
                updatePuzzle.setInt(2, puzzleId);
                updatePuzzle.executeUpdate();

                int money = currentPlayerMoney(currentPlayer);
                updateMoney.setInt(1, money + (spinResult * indexes.size()));
                updateMoney.setString(2, currentPlayer);
                updateMoney.executeUpdate();
            }
            System.out.println("\nWe have " + indexes.size() + " of that letter in this puzzle!");
            Thread.sleep(1000);
            return true;
        }
    }


    public static boolean buyVowelAndIsValid(char letter, int puzzleId, String currentPlayer) throws SQLException, InterruptedException {

        System.out.println("\nAlright, let's deduct the $250 from your earnings and see if we have any " + letter + "'s.");
        Thread.sleep(1000);

        String getMoneySql = "SELECT player_money FROM players WHERE player_name = ?";
        String updateMoneySql = "UPDATE players SET player_money = ? WHERE player_name = ?";
        String getPuzzleSql = "SELECT puzzle_solution, puzzle_hidden FROM puzzles WHERE puzzle_id = ?";
        String updatePuzzleSql = "UPDATE puzzles SET puzzle_hidden = ? WHERE puzzle_id = ?";

        try (Connection conn = DatabaseConnection.connect()) {

            int money;

            try (PreparedStatement getMoney = conn.prepareStatement(getMoneySql)) {
                getMoney.setString(1, currentPlayer);
                ResultSet rs = getMoney.executeQuery();

                if (!rs.next()) {
                    return false;
                }

                money = rs.getInt("player_money");
            }

            String solution;
            String hidden;

            try (PreparedStatement getPuzzle = conn.prepareStatement(getPuzzleSql)) {
                getPuzzle.setInt(1, puzzleId);
                ResultSet rs = getPuzzle.executeQuery();

                if (!rs.next()) {
                    return false;
                }

                solution = rs.getString("puzzle_solution");
                hidden = rs.getString("puzzle_hidden");
            }

            List<Integer> indexes = new ArrayList<>();

            for (int i = 0; i < solution.length(); i++) {
                if (solution.charAt(i) == letter) {
                    indexes.add(i);
                }
            }

            if (indexes.isEmpty()) {

                try (PreparedStatement updateMoney = conn.prepareStatement(updateMoneySql)) {
                    updateMoney.setInt(1, money - 250);
                    updateMoney.setString(2, currentPlayer);
                    updateMoney.executeUpdate();
                }

                System.out.println("\nSorry! There are none of those in this puzzle.");
                Thread.sleep(1000);
                return false;
            }

            StringBuilder updatedPuzzle = new StringBuilder(hidden);
            for (int index : indexes) {
                updatedPuzzle.setCharAt(index, letter);
            }

            try (PreparedStatement updatePuzzle = conn.prepareStatement(updatePuzzleSql)) {
                updatePuzzle.setString(1, updatedPuzzle.toString());
                updatePuzzle.setInt(2, puzzleId);
                updatePuzzle.executeUpdate();
            }

            try (PreparedStatement updateMoney = conn.prepareStatement(updateMoneySql)) {
                updateMoney.setInt(1, money - 250);
                updateMoney.setString(2, currentPlayer);
                updateMoney.executeUpdate();
            }

            System.out.println("\nWe have " + indexes.size() + " of that letter in this puzzle!");
            Thread.sleep(1000);
            return true;
        }
    }


    public static boolean canBuyVowel(String currentPlayer) throws SQLException {
        Integer money = currentPlayerMoney(currentPlayer);
        return money != null && money >= 250;
    }


    public static void endGame(List<Object> puzzle) throws SQLException {
        String resetLetters = "UPDATE letters SET selected_by = NULL, selected_this_round = FALSE";
        String deletePlayers = "DELETE FROM players";
        String resetPuzzle = "UPDATE puzzles SET puzzle_hidden = ? WHERE puzzle_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             PreparedStatement ps = conn.prepareStatement(resetPuzzle)) {

            stmt.executeUpdate(resetLetters);
            stmt.executeUpdate(deletePlayers);

            ps.setString(1, (String) puzzle.get(0));
            ps.setInt(2, (int) puzzle.get(2));
            ps.executeUpdate();
        }
    }
}

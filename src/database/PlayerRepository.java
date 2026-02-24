package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class PlayerRepository {

    public static String createPlayer() {
        String result;
        String sql = "INSERT INTO players (player_name, player_money) VALUES (?, ?)";

        while (true) {
            Scanner playerScanner = new Scanner(System.in);
            String playerName = playerScanner.nextLine();

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, playerName);
                ps.setInt(2, 0);
                ps.executeUpdate();
                result = playerName;
                break;
            } catch (SQLException e) {
                System.out.println("\nSorry, that name is already registered to another player. Please try again.");
            }
        }
        return result;
    }
}

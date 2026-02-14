import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Players {

    public static void createPlayer() throws SQLException {
        PreparedStatement pstmt = Main.con.prepareStatement("INSERT INTO players (player_name, player_money) VALUES (?, ?)");

        Scanner playerScanner = new Scanner(System.in);
        String playerName = playerScanner.nextLine();

        pstmt.setString(1, playerName);
        pstmt.setInt(2, 0);

        int newPlayer = pstmt.executeUpdate();
    }

}

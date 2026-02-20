import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public static Integer currentPlayerMoney(String currentPlayer) throws SQLException {
        Statement stmt2 = Main.con.createStatement();
        ResultSet rsGetPlayerMoney = stmt2.executeQuery("SELECT * FROM players where player_name = '" + currentPlayer + "'");

        int playerMoney = 0;
        while (rsGetPlayerMoney.next()) {
            playerMoney = rsGetPlayerMoney.getInt("player_money");
        }
        rsGetPlayerMoney.close();

        return playerMoney;
    }


}

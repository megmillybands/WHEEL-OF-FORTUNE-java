import java.sql.*;
import java.util.*;

public class Wheel {

    static String url = "jdbc:postgresql://localhost:5432/database";
    static String user = "postgres";
    static String password = "testingpassword";
    static String puzzlesQuery = "SELECT * FROM puzzles";
    String playersQuery = "SELECT * FROM players";

    static Connection con;
    static {
        try {
            con = DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static Statement stmt;
    static {
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static ResultSet rs;


    List<Integer> wheel = List.of(800,350,450,5000,300,600,700,600,500,300,500,800,550,300,900,500,300,900,350,600,400,300);

    public static void createPlayer() throws SQLException {
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO players (player_name, player_money) VALUES (?, ?)");

        Scanner playerScanner = new Scanner(System.in);
        String playerName = playerScanner.nextLine();

        pstmt.setString(1, playerName);
        pstmt.setInt(2, 0);

        int newPlayer = pstmt.executeUpdate();
    }

    public int spinTheWheel() {
        Random random = new Random();
        int randomIndex = random.nextInt(wheel.size());
        System.out.println(wheel.get(randomIndex));
        return wheel.get(randomIndex);
    }

    public static String getRandomPuzzle() throws SQLException {
        rs = stmt.executeQuery(puzzlesQuery);
        String allPuzzles = rs.getString("puzzle_solution");

        Random random = new Random();
        return random.toString();
    }

    public void findAllOccurrences() throws SQLException {
        System.out.println(rs.getString("puzzle_id"));
        while (rs.next()) {

            System.out.println(rs.getString("name"));

        }
    }

    public Wheel() throws SQLException {

    }
}

import java.sql.*;
import java.util.*;

public class Wheel {

    static List<Integer> wheel = List.of(800,350,450,5000,300,600,700,600,500,300,500,800,550,300,900,500,300,900,350,600,400,300);

    public static List<String> getRandomPuzzle() throws SQLException {
        Statement stmt = Main.con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM puzzles ORDER BY random() LIMIT 1");

        String randomPuzzle = null;
        String randomCategory = null;
        List<String> randomItems = new ArrayList<>();

        if (rs.next()) {
            randomPuzzle = rs.getString("puzzle_hidden");
            randomCategory = rs.getString("category");
            randomItems.add(randomPuzzle);
            randomItems.add(randomCategory);
        }

        return randomItems;
    }

    public static int spinTheWheel() {
        Random random = new Random();
        int randomIndex = random.nextInt(wheel.size());
        return wheel.get(randomIndex);
    }

}

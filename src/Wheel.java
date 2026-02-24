import java.util.*;

public class Wheel {

    public static int spinTheWheel() {
        List<Integer> wheel = List.of(800,350,450,5000,300,600,700,600,500,300,500,800,550,300,900,500,300,900,350,600,400,300);
        Random random = new Random();
        int randomIndex = random.nextInt(wheel.size());
        return wheel.get(randomIndex);
    }

}

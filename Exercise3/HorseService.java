import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class HorseService {

    public static void shoutWarCry(Horse horse) {
        String warCry = horse.getWarCry().orElse("No war cry.");
        System.out.println(horse.getName().toUpperCase() + " SHOUTS " + warCry);
    }

    public static void addDistanceTraveled(Horse horse, double distance) {
        horse.setDistanceTraveled(distance);
    }

    public static boolean isHealthy(Horse horse) {
        return horse.getHealthy();
    }

    public static void groupHorsesByAge(List<Horse> horses) {
        double averageAge = horses.stream().mapToInt(Horse::getAge).average().orElse(0);

        int mostFrequentAge = horses.stream()
                .collect(Collectors.groupingBy(Horse::getAge, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(0);

        for (Horse horse : horses) {
            if (horse.getAge() == mostFrequentAge) {
                horse.setGroup("Advanced");
            } else if (horse.getAge() > averageAge) {
                horse.setGroup("Intermediate");
            } else {
                horse.setGroup("Beginner");
            }
        }
    }
}

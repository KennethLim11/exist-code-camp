import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.time.LocalTime;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        List<String> warCries = Arrays.asList("Yeehaw!", "Charge!", null, "Let's go!", "Victory!");

        int numberOfHorses = 0;
        int distance = 0;

        while (true) {
            try {
                System.out.println("Enter number of horses in the race: ");
                numberOfHorses = scanner.nextInt(); 
                System.out.println("Enter race track distance: ");
                distance = scanner.nextInt();
                if (numberOfHorses <= 0 || distance <= 0) {
                    System.out.println("Input must be greater than 0.");
                    continue; 
                }
                break; 
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a positive integer.");
                scanner.next(); 
            }
        }

        final int trackDistance = distance;
       
        List<Horse> horses = new ArrayList<>();

        for(int i = 1; i <= numberOfHorses; i++) {
            boolean healthy = random.nextBoolean();
            String warCry = warCries.get(random.nextInt(warCries.size()));
            int age = random.nextInt(10) + 1;
            horses.add(new Horse("Horse" + i, healthy, warCry, age));
        }

        horses = horses.stream().filter(horse -> HorseService.isHealthy(horse)).collect(Collectors.toList());

        if(horses.isEmpty()) {
            System.out.println("No healthy horses to race.");
            return;
        }

        HorseService.groupHorsesByAge(horses);

        System.out.println("QUALIFIED HORSES: " + horses.size());
        horses.forEach(h -> System.out.println(h.getName() + " WARCRY: " + h.getWarCry().orElse("No war cry.") + " AGE: " + h.getAge() + " GROUP: " + h.getGroup()));

        System.out.println("Race starting in ");
        for (int i = 3; i > 0; i--) {
            System.out.println(i + "...");
            try {
                Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("GOO!");

        long startTime = System.currentTimeMillis();

        List<Horse> finishedHorse = new ArrayList<>();

        // Simulate race using parallel streams
        horses.parallelStream().forEach( horse -> {
            Random randomSpeed = new Random();
            int speedGenerationCount = 0;

            while(horse.getDistanceTraveled() < trackDistance) {
                double speed;

                if ("Advanced".equals(horse.getGroup()) && speedGenerationCount >= 2) {
                    speed = randomSpeed.nextInt(6) + 5;
                } else if ("Intermediate".equals(horse.getGroup()) && speedGenerationCount >= 4) {
                    speed = (randomSpeed.nextInt(10) + 1) * 1.1;
                } else {
                    speed = randomSpeed.nextInt(10) + 1;
                }

                HorseService.addDistanceTraveled(horse, speed);
                speedGenerationCount++;

                double remainingDistance = Math.max(0, trackDistance - horse.getDistanceTraveled());
                System.out.println(LocalTime.now() + " " + horse.getName() + " ran " + speed + " remaining " + remainingDistance);
                
                try {
                    Thread.sleep(100); // Simulate time delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            horse.setFinishTime(System.currentTimeMillis() - startTime);
            synchronized (finishedHorse) {
                finishedHorse.add(horse);
            }
            HorseService.shoutWarCry(horse);
            
        });

        System.out.println("\nRace Results: ");
        for (int i = 0; i < finishedHorse.size(); i++) {
            System.out.println((i + 1) + ". " + finishedHorse.get(i).getName() + " Finish Time: " + finishedHorse.get(i).getFinishTime() + "ms");
        }
    }
}
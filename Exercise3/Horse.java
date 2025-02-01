import java.util.Optional;

public class Horse {
    private final String name;
    private final boolean healthy;
    private double distanceTraveled;
    private final String warCry;
    private final int age;
    private String group;
    private long finishTime;

    public Horse(String name, boolean healthy, String warCry, int age) {
        this.name = name;
        this.healthy = healthy;
        this.warCry = warCry;
        this.age = age;
        this.distanceTraveled = 0;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean getHealthy() {
        return healthy;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(double distance) {
        this.distanceTraveled += distance;
    }

    public Optional<String> getWarCry() {
        return Optional.ofNullable(warCry);
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

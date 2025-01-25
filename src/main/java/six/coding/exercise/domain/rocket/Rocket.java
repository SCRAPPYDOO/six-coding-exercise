package six.coding.exercise.domain.rocket;

public class Rocket {

    private String name;
    private RocketStatus status;

    private Rocket(String name, RocketStatus status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public RocketStatus getStatus() {
        return status;
    }

    public static class Builder {

        private String name;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Rocket build() {
            return new Rocket(name, RocketStatus.ON_GROUND);
        }
    }
}

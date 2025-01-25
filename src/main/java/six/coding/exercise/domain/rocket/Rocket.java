package six.coding.exercise.domain.rocket;

import six.coding.exercise.domain.mission.Mission;

public class Rocket {

    private final String name;
    private RocketStatus status;
    private Mission mission;

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

    public void setStatus(RocketStatus status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public static class Builder {

        private Builder() {
        }

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

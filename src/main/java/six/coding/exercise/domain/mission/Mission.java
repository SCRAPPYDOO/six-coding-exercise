package six.coding.exercise.domain.mission;

import six.coding.exercise.domain.rocket.Rocket;

import java.util.ArrayList;
import java.util.List;

public class Mission {

    private final String name;
    private MissionStatus status;
    private final List<Rocket> rockets;

    protected Mission(String name, MissionStatus status, List<Rocket> rockets) {
        this.name = name;
        this.status = status;
        this.rockets = rockets;
    }

    public String getName() {
        return name;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public List<Rocket> getRockets() {
        return rockets;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Builder() {}

        private String name;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Mission build() {
            return new Mission(name, MissionStatus.SCHEDULED, new ArrayList<>());
        }
    }
}

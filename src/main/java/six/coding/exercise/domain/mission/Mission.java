package six.coding.exercise.domain.mission;

public class Mission {

    private final String name;
    private MissionStatus status;

    protected Mission(String name, MissionStatus status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public MissionStatus getStatus() {
        return status;
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
            return new Mission(name, MissionStatus.SCHEDULED);
        }
    }
}

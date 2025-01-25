package six.coding.exercise.service.mission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import six.coding.exercise.domain.mission.MissionStatus;
import six.coding.exercise.exception.RocketNotFoundException;

public class MissionServiceTest {

    private static MissionService missionService;

    @BeforeEach
    public void beforeAll() {
        missionService = new MissionServiceImpl();
    }

    @Test
    public void addMissionTest() {

        final String missionName = "Mars";

        StepVerifier.create(missionService.addMission(missionName))
                .expectNextMatches(mission ->
                        mission.getName().equals(missionName) &&
                                MissionStatus.SCHEDULED.equals(mission.getStatus()) &&
                                mission.getRockets().isEmpty()
                        )
                .expectComplete()
                .verify();
    }

    @Test
    public void addRocketToMissionTest() {

        final String missionName = "Mars";
        final String rocketName = "Dragon 73";

        StepVerifier.create(missionService.addRocketToMission(missionName, rocketName))
                .expectNextMatches(mission ->
                        mission.getName().equals(missionName) && !mission.getRockets().isEmpty() &&
                                mission.getRockets().stream().anyMatch(rocket -> rocketName.equalsIgnoreCase(rocket.getName())))
                .expectComplete()
                .verify();
    }

    @Test
    public void addMissingRocketToMissionThrowsExceptionTest() {
        final String missionName = "Venus";
        final String rocketName = "Dragon 54";

        StepVerifier.create(missionService.addRocketToMission(missionName, rocketName))
                .expectError(RocketNotFoundException.class)
                .verify();
    }

    @Test
    public void changeMissionStatusTest() {

        final String name = "Mars";
        final MissionStatus missionStatus = MissionStatus.ENDED;

        StepVerifier.create(missionService.addMission(name)
                        .flatMap(mission -> missionService.changeMissionStatus(name, missionStatus)))
                .expectNextMatches(mission ->
                        mission.getName().equals(name) && missionStatus.equals(mission.getStatus()))
                .expectComplete()
                .verify();
    }

    @Test
    public void missionSummaryTest() {

    }
}

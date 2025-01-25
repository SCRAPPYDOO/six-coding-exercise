package six.coding.exercise.service.mission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import six.coding.exercise.domain.mission.MissionStatus;
import six.coding.exercise.exception.MissionNotFoundException;
import six.coding.exercise.exception.RocketAlreadyAssignedException;
import six.coding.exercise.exception.RocketNotFoundException;
import six.coding.exercise.service.rocket.RocketService;
import six.coding.exercise.service.rocket.RocketServiceImpl;

public class MissionServiceTest {

    private static MissionService missionService;
    private static RocketService rocketService;

    @BeforeEach
    public void beforeEach() {
        rocketService = new RocketServiceImpl();
        missionService = new MissionServiceImpl(rocketService);
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

        StepVerifier.create(missionService.addMission(missionName)
                        .flatMap(mission -> rocketService.addNewRocket(rocketName))
                        .flatMap(mission -> missionService.addRocketToMission(missionName, rocketName)))
                .expectNextMatches(mission ->
                        mission.getName().equals(missionName) && !mission.getRockets().isEmpty() &&
                                mission.getRockets().stream().anyMatch(rocket -> rocketName.equalsIgnoreCase(rocket.getName())))
                .expectComplete()
                .verify();
    }

    @Test
    public void addRocketToMissingMissionThrowsExceptionTest() {
        final String missionName = "Venus";
        final String rocketName = "Dragon 54";

        StepVerifier.create(missionService.addRocketToMission(missionName, rocketName))
                .expectError(MissionNotFoundException.class)
                .verify();
    }

    @Test
    public void addMissingRocketToMissionThrowsExceptionTest() {
        final String missionName = "Venus";
        final String rocketName = "Dragon 54";

        StepVerifier.create(missionService.addMission(missionName)
                        .flatMap(mission -> missionService.addRocketToMission(missionName, rocketName)))
                .expectError(RocketNotFoundException.class)
                .verify();
    }

    @Test
    public void addRocketAlreadyAssignedToAnotherMissionThrowsExceptionTest() {
        final String missionName = "Venus";
        final String rocketName = "Dragon 54";

        final String missionName2 = "Mars";

        StepVerifier.create(missionService.addMission(missionName2)
                        .flatMap(mission -> rocketService.addNewRocket(rocketName))
                        .flatMap(mission -> missionService.addRocketToMission(missionName2, rocketName))
                        .flatMap(mission -> missionService.addMission(missionName))
                        .flatMap(mission -> missionService.addRocketToMission(missionName, rocketName)))
                .expectError(RocketAlreadyAssignedException.class)
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

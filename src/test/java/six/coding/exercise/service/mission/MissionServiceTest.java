package six.coding.exercise.service.mission;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import six.coding.exercise.domain.mission.MissionStatus;

public class MissionServiceTest {

    private static MissionService missionService;

    @BeforeAll
    public static void beforeAll() {
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

    }

    @Test
    public void changeMissionStatusTest() {

    }

    @Test
    public void missionSummaryTest() {

    }
}

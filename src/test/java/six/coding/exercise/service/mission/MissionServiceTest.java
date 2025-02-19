package six.coding.exercise.service.mission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import six.coding.exercise.domain.mission.MissionStatus;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.domain.rocket.RocketStatus;
import six.coding.exercise.exception.MissionNotFoundException;
import six.coding.exercise.exception.RocketAlreadyAssignedException;
import six.coding.exercise.exception.RocketNotFoundException;
import six.coding.exercise.service.rocket.RocketService;
import six.coding.exercise.service.rocket.RocketServiceImpl;

import java.time.Duration;

public class MissionServiceTest {

    private static MissionService missionService;
    private static RocketService rocketService;

    @BeforeEach
    public void beforeEach() {

        final Sinks.Many<Rocket> rocketStatusSink = Sinks.many().unicast().onBackpressureBuffer();

        rocketService = new RocketServiceImpl(rocketStatusSink);
        missionService = new MissionServiceImpl(rocketService, rocketStatusSink);
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
                        mission.getName().equals(missionName) &&
                                MissionStatus.IN_PROGRESS.equals(mission.getStatus()) &&
                                !mission.getRockets().isEmpty() &&
                                mission.getRockets().stream().anyMatch(rocket -> rocketName.equalsIgnoreCase(rocket.getName()) &&
                                        RocketStatus.IN_SPACE.equals(rocket.getStatus())))
                .expectComplete()
                .verify();
    }

    @Test
    public void addRocketInRepairStatusToMissionShouldChangeMissionStatusToPendingTest() {

        final String missionName = "Mars";
        final String rocketName = "Dragon 73";

        StepVerifier.create(missionService.addMission(missionName)
                        .flatMap(mission -> rocketService.addNewRocket(rocketName))
                        .flatMap(mission -> rocketService.changeRocketStatus(rocketName, RocketStatus.IN_REPAIR))
                        .flatMap(mission -> missionService.addRocketToMission(missionName, rocketName)))
                .expectNextMatches(mission ->
                        mission.getName().equals(missionName) &&
                                MissionStatus.PENDING.equals(mission.getStatus()) &&
                                !mission.getRockets().isEmpty() &&
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
        final MissionStatus missionStatus = MissionStatus.IN_PROGRESS;

        StepVerifier.create(missionService.addMission(name)
                        .flatMap(mission -> missionService.changeMissionStatus(name, missionStatus)))
                .expectNextMatches(mission ->
                        mission.getName().equals(name) && missionStatus.equals(mission.getStatus()))
                .expectComplete()
                .verify();
    }

    @Test
    public void changeMissionStatusToEndedRocketsAreClearedTest() {

        final String name = "Mars";
        final MissionStatus missionStatus = MissionStatus.ENDED;
        final String rocketName = "Dragon 54";

        StepVerifier.create(missionService.addMission(name)
                        .flatMap(mission -> rocketService.addNewRocket(rocketName))
                        .flatMap(mission -> missionService.addRocketToMission(name, rocketName))
                        .flatMap(mission -> missionService.changeMissionStatus(name, missionStatus)))
                .expectNextMatches(mission ->
                        mission.getName().equals(name) && missionStatus.equals(mission.getStatus()) && mission.getRockets().isEmpty())
                .expectComplete()
                .verify();
    }

    @Test
    public void changeMissionStatusToPendingWhenRocketsAreInRepairTest() {
        final String name = "Mars";
        final String rocketName = "Dragon 54";

        StepVerifier.create(missionService.addMission(name)
                        .flatMap(mission -> rocketService.addNewRocket(rocketName))
                        .flatMap(mission -> missionService.addRocketToMission(name, rocketName))
                        .flatMap(mission -> rocketService.changeRocketStatus(rocketName, RocketStatus.IN_REPAIR).delayElement(Duration.ofSeconds(1L)))
                        .flatMap(rocket -> missionService.getMission(name)))
                .expectNextMatches(mission ->
                        mission.getName().equals(name) && MissionStatus.PENDING.equals(mission.getStatus()) &&
                                !mission.getRockets().isEmpty() && mission.getRockets().stream().anyMatch(rocket -> RocketStatus.IN_REPAIR.equals(rocket.getStatus())))
                .expectComplete()
                .verify();
    }

    @Test
    public void changeMissionStatusToInProgressWhenNoInRepairRocketsTest() {

        final String name = "Mars";
        final String rocketName = "Dragon 54";

        StepVerifier.create(missionService.addMission(name)
                        .flatMap(mission -> rocketService.addNewRocket(rocketName))
                        .flatMap(rocket -> missionService.addRocketToMission(name, rocketName))
                        .flatMap(mission -> rocketService.changeRocketStatus(rocketName, RocketStatus.IN_REPAIR).delayElement(Duration.ofSeconds(1L)))
                        .flatMap(rocket -> rocketService.changeRocketStatus(rocketName, RocketStatus.IN_SPACE).delayElement(Duration.ofSeconds(1L)))
                        .flatMap(rocket -> missionService.getMission(name)))
                .expectNextMatches(mission ->
                        mission.getName().equals(name) && MissionStatus.IN_PROGRESS.equals(mission.getStatus()) &&
                                !mission.getRockets().isEmpty() && mission.getRockets().stream().anyMatch(rocket -> RocketStatus.IN_SPACE.equals(rocket.getStatus())))
                .expectComplete()
                .verify();
    }

    @Test
    public void missionSummaryTest() {

        final String mission1 = "Mars";
        final String mission2 = "Luna1";
        final String mission3 = "Double Landing";
        final String mission4 = "Transit";
        final String mission5 = "Luna2";
        final String mission6 = "Vertical Landing";

        StepVerifier.create(missionService.addMission(mission1)
                                .flatMap(o -> missionService.addMission(mission2))
                                .flatMap(o -> missionService.addMission(mission3))
                                .flatMap(o -> missionService.addMission(mission4))
                                .flatMap(o -> missionService.addMission(mission5))
                                .flatMap(o -> missionService.addMission(mission6))
                                .flatMap(o -> rocketService.addNewRocket("Dragon 1"))
                                .flatMap(o -> rocketService.addNewRocket("Dragon 2"))
                                .flatMap(o -> rocketService.addNewRocket("Red Dragon"))
                                .flatMap(o -> rocketService.addNewRocket("Dragon XL"))
                                .flatMap(o -> rocketService.addNewRocket("Falcon Heavy"))
                                .flatMap(o -> missionService.changeMissionStatus(mission3, MissionStatus.ENDED))
                                .flatMap(o -> missionService.changeMissionStatus(mission6, MissionStatus.ENDED))
                                .flatMap(o -> missionService.addRocketToMission(mission2, "Dragon 1"))
                                .flatMap(o -> missionService.addRocketToMission(mission2, "Dragon 2"))
                                .flatMap(o -> missionService.changeMissionStatus(mission2, MissionStatus.PENDING))
                                .flatMap(o -> missionService.addRocketToMission(mission4, "Red Dragon"))
                                .flatMap(o -> missionService.addRocketToMission(mission4, "Dragon XL"))
                                .flatMap(o -> missionService.addRocketToMission(mission4, "Falcon Heavy"))
                                .flatMapMany(o -> missionService.getMissionsSummary()))
                .expectNextMatches(mission -> mission.getName().equals(mission4) && MissionStatus.IN_PROGRESS.equals(mission.getStatus()) &&
                                mission.getRockets().size() == 3)
                .expectNextMatches(mission -> mission.getName().equals(mission2) && MissionStatus.PENDING.equals(mission.getStatus()) &&
                        mission.getRockets().size() == 2)
                .expectNextMatches(mission -> mission.getName().equals(mission6) && MissionStatus.ENDED.equals(mission.getStatus()) &&
                        mission.getRockets().isEmpty())
                .expectNextMatches(mission -> mission.getName().equals(mission1) && MissionStatus.SCHEDULED.equals(mission.getStatus()) &&
                        mission.getRockets().isEmpty())
                .expectNextMatches(mission -> mission.getName().equals(mission5) && MissionStatus.SCHEDULED.equals(mission.getStatus()) &&
                        mission.getRockets().isEmpty())
                .expectNextMatches(mission -> mission.getName().equals(mission3) && MissionStatus.ENDED.equals(mission.getStatus()) &&
                        mission.getRockets().isEmpty())
                .expectComplete()
                .verify();
    }
}

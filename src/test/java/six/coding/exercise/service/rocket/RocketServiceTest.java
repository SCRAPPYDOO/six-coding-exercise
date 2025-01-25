package six.coding.exercise.service.rocket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import six.coding.exercise.domain.rocket.RocketStatus;

public class RocketServiceTest {

    private static RocketService rocketService;

    @BeforeAll
    public static void beforeAll() {
        rocketService = new RocketServiceImpl(Sinks.many().unicast().onBackpressureBuffer());
    }

    @Test
    public void addNewRocketTest() {

        final String rocketName = "Dragon 1";

        StepVerifier.create(rocketService.addNewRocket(rocketName))
                .expectNextMatches(rocket ->
                        rocket.getName().equals(rocketName) && RocketStatus.ON_GROUND.equals(rocket.getStatus()))
                .expectComplete()
                .verify();
    }

    @Test
    public void changeRocketStatusTest() {

        final String rocketName = "Dragon 2";
        final RocketStatus newRocketStatus = RocketStatus.IN_REPAIR;

        StepVerifier.create(rocketService.addNewRocket(rocketName)
                        .flatMap(createdRocket -> rocketService.changeRocketStatus(rocketName, newRocketStatus)))
                .expectNextMatches(rocket ->
                        rocket.getName().equals(rocketName) && newRocketStatus.equals(rocket.getStatus()))
                .expectComplete()
                .verify();

        final String rocketName2 = "Dragon 3";
        final RocketStatus newRocketStatus2 = RocketStatus.ON_GROUND;

        StepVerifier.create(rocketService.addNewRocket(rocketName2)
                        .flatMap(createdRocket -> rocketService.changeRocketStatus(rocketName2, newRocketStatus2)))
                .expectNextMatches(rocket ->
                        rocket.getName().equals(rocketName2) && newRocketStatus2.equals(rocket.getStatus()))
                .expectComplete()
                .verify();

        final String rocketName3 = "Dragon 4";
        final RocketStatus newRocketStatus3 = RocketStatus.IN_SPACE;

        StepVerifier.create(rocketService.addNewRocket(rocketName3)
                        .flatMap(createdRocket -> rocketService.changeRocketStatus(rocketName3, newRocketStatus3)))
                .expectNextMatches(rocket ->
                        rocket.getName().equals(rocketName3) && newRocketStatus3.equals(rocket.getStatus()))
                .expectComplete()
                .verify();
    }
}

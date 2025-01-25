package six.coding.exercise.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import six.coding.exercise.domain.rocket.RocketStatus;
import six.coding.exercise.service.rocket.RocketService;
import six.coding.exercise.service.rocket.RocketServiceImpl;

public class RocketServiceTest {

    private static RocketService rocketService;

    @BeforeAll
    public static void beforeAll() {
        rocketService = new RocketServiceImpl();
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
}

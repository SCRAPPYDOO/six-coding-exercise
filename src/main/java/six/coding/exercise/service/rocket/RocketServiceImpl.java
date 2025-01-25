package six.coding.exercise.service.rocket;

import reactor.core.publisher.Mono;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.domain.rocket.RocketStatus;

public class RocketServiceImpl implements RocketService {

    @Override
    public Mono<Rocket> addNewRocket(final String name) {
        return Mono.empty();
    }

    @Override
    public Mono<Rocket> changeRocketStatus(final String rocketName, final RocketStatus newStatus) {
        return Mono.empty();
    }
}

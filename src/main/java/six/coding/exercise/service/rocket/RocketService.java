package six.coding.exercise.service.rocket;

import reactor.core.publisher.Mono;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.domain.rocket.RocketStatus;

public interface RocketService {
    Mono<Rocket> addNewRocket(String name);

    Mono<Rocket> changeRocketStatus(String rocketName, RocketStatus newStatus);

    Mono<Rocket> getRocket(String rocketName);
}

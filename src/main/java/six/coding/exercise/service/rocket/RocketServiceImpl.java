package six.coding.exercise.service.rocket;

import reactor.core.publisher.Mono;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.domain.rocket.RocketStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RocketServiceImpl implements RocketService {

    private final Map<String, Rocket> rocketRepository = new ConcurrentHashMap<>();

    @Override
    public Mono<Rocket> addNewRocket(final String name) {

        rocketRepository.put(name,
                Rocket.builder().name(name).build());

        return Mono.justOrEmpty(rocketRepository.get(name));
    }

    @Override
    public Mono<Rocket> changeRocketStatus(final String rocketName, final RocketStatus newStatus) {
        return Mono.justOrEmpty(rocketRepository.get(rocketName))
                .map(rocket -> {
                    rocket.setStatus(newStatus);
                    return rocket;
                });
    }
}

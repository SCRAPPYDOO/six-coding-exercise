package six.coding.exercise.service.rocket;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.domain.rocket.RocketStatus;

import java.util.ArrayList;
import java.util.List;

public class RocketServiceImpl implements RocketService {

    private final List<Rocket> rocketRepository = new ArrayList<>();

    public RocketServiceImpl() {

    }

    @Override
    public Mono<Rocket> addNewRocket(final String name) {

        rocketRepository.add(Rocket.builder()
                .name(name)
                .build());

        return getRocketByName(name);
    }

    @Override
    public Mono<Rocket> changeRocketStatus(final String rocketName, final RocketStatus newStatus) {
        return getRocketByName(rocketName)
                .map(rocket -> {
                    rocket.setStatus(newStatus);
                    return rocket;
                });
    }

    private Mono<Rocket> getRocketByName(final String name) {
        return Flux.fromStream(rocketRepository.stream())
                .filter(rocket -> rocket.getName().equalsIgnoreCase(name))
                .single();
    }
}

package six.coding.exercise.service.rocket;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.domain.rocket.RocketStatus;
import six.coding.exercise.exception.RocketNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RocketServiceImpl implements RocketService {

    private final Map<String, Rocket> rocketRepository = new ConcurrentHashMap<>();
    private final Sinks.Many<Rocket> rocketStatusSink;

    public RocketServiceImpl(Sinks.Many<Rocket> rocketStatusSink) {
        this.rocketStatusSink = rocketStatusSink;
    }

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
                }).doOnNext(rocket -> rocketStatusSink.tryEmitNext(rocket).orThrow());
    }

    @Override
    public Mono<Rocket> getRocket(String rocketName) {

        if(rocketRepository.containsKey(rocketName)) {
            return Mono.just(rocketRepository.get(rocketName));
        }

        return Mono.error(RocketNotFoundException::new);
    }
}

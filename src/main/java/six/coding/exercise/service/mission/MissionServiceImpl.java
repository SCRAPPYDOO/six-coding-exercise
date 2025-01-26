package six.coding.exercise.service.mission;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import six.coding.exercise.domain.mission.Mission;
import six.coding.exercise.domain.mission.MissionStatus;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.domain.rocket.RocketStatus;
import six.coding.exercise.exception.MissionNotFoundException;
import six.coding.exercise.exception.RocketAlreadyAssignedException;
import six.coding.exercise.service.rocket.RocketService;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MissionServiceImpl implements MissionService {

    private final Map<String, Mission> missionRepository = new ConcurrentHashMap<>();

    private final RocketService rocketService;

    public MissionServiceImpl(RocketService rocketService, Sinks.Many<Rocket> rocketStatusSink) {
        this.rocketService = rocketService;

        rocketStatusSink.asFlux()
                .doOnNext(this::onRocketStatusChange)
                .subscribe();
    }

    @Override
    public Mono<Mission> addMission(String missionName) {
        missionRepository.put(missionName,
                Mission.builder().name(missionName).build());

        return Mono.justOrEmpty(missionRepository.get(missionName));
    }

    @Override
    public Mono<Mission> changeMissionStatus(String name, MissionStatus missionStatus) {
        return Mono.justOrEmpty(missionRepository.get(name))
                .map(mission -> {

                    if(MissionStatus.ENDED.equals(missionStatus) && !mission.getStatus().equals(missionStatus)) {
                        mission.getRockets().clear();
                    }

                    mission.setStatus(missionStatus);
                    return mission;
                });
    }

    @Override
    public Mono<Mission> addRocketToMission(String missionName, String rocketName) {

        if(missionRepository.values().stream().map(Mission::getRockets)
                .anyMatch(rockets -> rockets.stream().map(Rocket::getName)
                        .anyMatch(s -> s.equalsIgnoreCase(rocketName)))) {
            return Mono.error(RocketAlreadyAssignedException::new);
        }

        if(missionRepository.containsKey(missionName)) {

            final Mission mission = missionRepository.get(missionName);

            if(mission.getRockets().stream().map(Rocket::getName).anyMatch(s -> s.equalsIgnoreCase(rocketName))) {
                return Mono.just(mission);
            } else {
                return rocketService.getRocket(rocketName)
                        .map(rocket -> addRocketToMission(mission, rocket));
            }
        }

        return Mono.error(MissionNotFoundException::new);
    }

    @Override
    public Flux<Mission> getMissionsSummary() {
        return Flux.fromStream(missionRepository.values().stream())
                .sort(Comparator.<Mission>comparingInt(o -> o.getRockets().size())
                        .thenComparing(Mission::getName)
                        .reversed());

    }

    @Override
    public Mono<Mission> getMission(final String name) {

        if(missionRepository.containsKey(name)) {
            return Mono.just(missionRepository.get(name));
        }

        return Mono.error(MissionNotFoundException::new);
    }

    private Mission addRocketToMission(final Mission mission, final Rocket rocket) {
        if(mission.getRockets().isEmpty()) {
            if(RocketStatus.IN_REPAIR.equals(rocket.getStatus())) {
                mission.setStatus(MissionStatus.PENDING);
            } else {
                mission.setStatus(MissionStatus.IN_PROGRESS);
                rocket.setStatus(RocketStatus.IN_SPACE);
            }
        }

        mission.getRockets().add(rocket);
        return mission;
    }

    private void onRocketStatusChange(final Rocket rocket) {
        if(RocketStatus.IN_REPAIR.equals(rocket.getStatus())) {
            missionRepository.values().stream()
                    .filter(mission -> !mission.getRockets().isEmpty())
                    .filter(mission -> mission.getRockets().stream().anyMatch(rocket1 -> rocket1.getName().equalsIgnoreCase(rocket.getName())))
                    .findFirst()
                    .ifPresent(mission -> mission.setStatus(MissionStatus.PENDING));
        } else {
            missionRepository.values().stream()
                    .filter(mission -> !mission.getRockets().isEmpty())
                    .filter(mission -> MissionStatus.PENDING.equals(mission.getStatus()) &&
                            mission.getRockets().stream().map(Rocket::getStatus).noneMatch(RocketStatus.IN_REPAIR::equals))
                    .forEach(mission -> mission.setStatus(MissionStatus.IN_PROGRESS));
        }
    }
}

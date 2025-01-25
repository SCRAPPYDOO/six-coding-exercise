package six.coding.exercise.service.mission;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import six.coding.exercise.domain.mission.Mission;
import six.coding.exercise.domain.mission.MissionStatus;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.domain.rocket.RocketStatus;
import six.coding.exercise.exception.MissionNotFoundException;
import six.coding.exercise.exception.RocketAlreadyAssignedException;
import six.coding.exercise.service.rocket.RocketService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MissionServiceImpl implements MissionService {

    private final Map<String, Mission> missionRepository = new ConcurrentHashMap<>();

    private final RocketService rocketService;

    public MissionServiceImpl(RocketService rocketService) {
        this.rocketService = rocketService;
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
    public Publisher<?> getMissionsSummary() {
        return Mono.empty();
    }

    private Mission addRocketToMission(final Mission mission, final Rocket rocket) {
        if(mission.getRockets().isEmpty()) {
            if(RocketStatus.IN_REPAIR.equals(rocket.getStatus())) {
                mission.setStatus(MissionStatus.PENDING);
            } else {
                mission.setStatus(MissionStatus.IN_PROGRESS);
            }
        }

        mission.getRockets().add(rocket);
        return mission;
    }
}

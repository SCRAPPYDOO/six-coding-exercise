package six.coding.exercise.service.mission;

import reactor.core.publisher.Mono;
import six.coding.exercise.domain.mission.Mission;
import six.coding.exercise.domain.mission.MissionStatus;
import six.coding.exercise.domain.rocket.Rocket;
import six.coding.exercise.exception.MissionNotFoundException;
import six.coding.exercise.exception.RocketAlreadyAssignedException;
import six.coding.exercise.service.rocket.RocketService;

import java.util.Map;
import java.util.Objects;
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
                    mission.setStatus(missionStatus);
                    return mission;
                });
    }

    @Override
    public Mono<Mission> addRocketToMission(String missionName, String rocketName) {

        if(missionRepository.containsKey(missionName)) {

            final Mission mission = missionRepository.get(missionName);

            if(mission.getRockets().stream().map(Rocket::getName).anyMatch(s -> s.equalsIgnoreCase(rocketName))) {
                return Mono.just(mission);
            } else {
                return rocketService.getRocket(rocketName)
                        .filter(rocket -> Objects.isNull(rocket.getMission()) || rocket.getMission().getName().equalsIgnoreCase(mission.getName()))
                        .switchIfEmpty(Mono.defer(() -> Mono.error(RocketAlreadyAssignedException::new)))
                        .map(rocket -> addRocketToMission(mission, rocket));
            }
        }

        return Mono.error(MissionNotFoundException::new);
    }

    private Mission addRocketToMission(final Mission mission, final Rocket rocket) {
        mission.getRockets().add(rocket);
        rocket.setMission(mission);
        return mission;
    }
}

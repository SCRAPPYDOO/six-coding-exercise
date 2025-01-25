package six.coding.exercise.service.mission;

import reactor.core.publisher.Mono;
import six.coding.exercise.domain.mission.Mission;
import six.coding.exercise.domain.mission.MissionStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MissionServiceImpl implements MissionService {

    private final Map<String, Mission> missionRepository = new ConcurrentHashMap<>();

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
        return Mono.empty();
    }
}

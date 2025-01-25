package six.coding.exercise.service.mission;

import reactor.core.publisher.Mono;
import six.coding.exercise.domain.mission.Mission;

public class MissionServiceImpl implements MissionService {

    @Override
    public Mono<Mission> addMission(String missionName) {
        return Mono.empty();
    }
}

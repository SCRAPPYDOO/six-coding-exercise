package six.coding.exercise.service.mission;

import reactor.core.publisher.Mono;
import six.coding.exercise.domain.mission.Mission;

public interface MissionService {
    Mono<Mission> addMission(String missionName);
}

package six.coding.exercise.service.mission;

import reactor.core.publisher.Mono;
import six.coding.exercise.domain.mission.Mission;
import six.coding.exercise.domain.mission.MissionStatus;

public interface MissionService {
    Mono<Mission> addMission(String missionName);

    Mono<Mission> changeMissionStatus(String name, MissionStatus missionStatus);
}

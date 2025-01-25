package six.coding.exercise.service.mission;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import six.coding.exercise.domain.mission.Mission;
import six.coding.exercise.domain.mission.MissionStatus;
import six.coding.exercise.domain.rocket.Rocket;

import java.util.function.Function;

public interface MissionService {
    Mono<Mission> addMission(String missionName);

    Mono<Mission> changeMissionStatus(String name, MissionStatus missionStatus);

    Mono<Mission> addRocketToMission(String missionName, String rocketName);

    Publisher<?> getMissionsSummary();

    Mono<Mission> getMission(String name);
}

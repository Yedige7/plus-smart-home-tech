package ru.practicum.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;

@Component
public class SwitchSensorEventHandler implements SensorEventHandler {
    private static final Logger log = LoggerFactory.getLogger(SwitchSensorEventHandler.class);

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        SwitchSensorProto p = event.getSwitchSensorEvent();
        log.info("SWITCH: deviceId={}, hubId={}, ts={}, state={}",
                event.getId(), event.getHubId(), event.getTimestamp(),
                p.getState());
    }
}
package ru.practicum.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;

@Component
public class MotionSensorEventHandler implements SensorEventHandler {
    private static final Logger log = LoggerFactory.getLogger(MotionSensorEventHandler.class);

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        MotionSensorProto p = event.getMotionSensorEvent();
        log.info("MOTION event: id={}, hubId={}, ts={}, motion={}, linkQuality={}, voltage={}",
                event.getId(),
                event.getHubId(),
                event.getTimestamp(),
                p.getMotion(),
                p.getLinkQuality(),
                p.getVoltage());
    }
}
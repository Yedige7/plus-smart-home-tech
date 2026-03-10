package ru.practicum.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class LightSensorEventHandler implements SensorEventHandler {
    private static final Logger log = LoggerFactory.getLogger(LightSensorEventHandler.class);

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorProto p = event.getLightSensorEvent();
        log.info("LIGHT: deviceId={}, hubId={}, ts={}, linkQuality={}, luminosity={}",
                event.getId(), event.getHubId(), event.getTimestamp(),
                p.getLinkQuality(), p.getLuminosity());
    }
}

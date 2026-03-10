package ru.practicum.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TemperatureSensorEventHandler.class);

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        TemperatureSensorProto p = event.getTemperatureSensorEvent();
        log.info("TEMP: deviceId={}, hubId={}, ts={}, C={}, F={}",
                event.getId(), event.getHubId(), event.getTimestamp(),
                p.getTemperatureC(), p.getTemperatureF());
    }
}

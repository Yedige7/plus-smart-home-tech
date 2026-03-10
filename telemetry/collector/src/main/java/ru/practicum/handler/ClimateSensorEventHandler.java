package ru.practicum.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class ClimateSensorEventHandler implements SensorEventHandler {
    private static final Logger log = LoggerFactory.getLogger(ClimateSensorEventHandler.class);

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        ClimateSensorProto p = event.getClimateSensorEvent();
        log.info("CLIMATE: deviceId={}, hubId={}, ts={}, tempC={}, humidity={}, co2={}",
                event.getId(), event.getHubId(), event.getTimestamp(),
                p.getTemperatureC(), p.getHumidity(), p.getCo2Level());
    }
}
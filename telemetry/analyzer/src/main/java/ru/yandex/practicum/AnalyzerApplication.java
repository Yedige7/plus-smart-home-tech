package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.service.HubEventProcessor;
import ru.yandex.practicum.service.SnapshotProcessor;

@SpringBootApplication
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AnalyzerApplication.class, args);

        HubEventProcessor hubEventProcessor = ctx.getBean(HubEventProcessor.class);
        SnapshotProcessor snapshotProcessor = ctx.getBean(SnapshotProcessor.class);

        Thread hubThread = new Thread(hubEventProcessor);
        hubThread.setName("HubEventHandlerThread");
        hubThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            hubEventProcessor.stop();
            snapshotProcessor.stop();
            try { hubThread.join(1500); } catch (InterruptedException ignored) {}
        }));
        snapshotProcessor.start();
    }
}
package ru.practicum.dto.hubs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ScenarioRemovedEventDto extends HubEventDto {

    @NotNull
    @Size(min = 3)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

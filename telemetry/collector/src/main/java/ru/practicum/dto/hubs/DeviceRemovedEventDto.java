package ru.practicum.dto.hubs;

import jakarta.validation.constraints.NotBlank;

public class DeviceRemovedEventDto extends HubEventDto {

    @NotBlank
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

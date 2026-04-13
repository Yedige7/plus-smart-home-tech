package ru.yandex.practicum.dto;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressEmbeddable {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}

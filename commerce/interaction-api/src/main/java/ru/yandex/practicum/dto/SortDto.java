package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortDto {

    private String direction;
    private String property;
    private boolean ascending;
    private boolean ignoreCase;
    private String nullHandling;

}
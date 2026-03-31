package ru.yandex.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {

    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private int size;
    private List<T> content;
    private int number;
    private List<SortDto> sort;
    private int numberOfElements;
    private PageableDto pageable;
    private boolean empty;

}
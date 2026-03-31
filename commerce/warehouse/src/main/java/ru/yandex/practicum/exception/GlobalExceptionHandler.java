package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WarehouseProductNotFoundException.class)
    public ProblemDetail handleNotFound(WarehouseProductNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Warehouse product not found");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(WarehouseProductAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(WarehouseProductAlreadyExistsException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Warehouse product already exists");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}

package ru.practicum.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {

        Map<String, Object> details = new LinkedHashMap<>();
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        details.put("fieldErrors", fieldErrors);

        return build(HttpStatus.BAD_REQUEST, "Validation failed", request, details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        // Это сюда попадают: неправильный JSON, неизвестный enum value, неверный формат timestamp и т.п.
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("cause", rootCauseMessage(ex));

        return build(HttpStatus.BAD_REQUEST, "Malformed JSON or incompatible payload", request, details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex,
                                                          HttpServletRequest request) {
        // сюда можно кидать "unknown type" / "unsupported event type" / "missing required payload field"
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(KafkaPublishException.class)
    public ResponseEntity<ErrorResponse> handleKafka(KafkaPublishException ex,
                                                     HttpServletRequest request) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("topic", ex.getTopic());
        details.put("key", ex.getKey());

        return build(HttpStatus.SERVICE_UNAVAILABLE, "Kafka unavailable", request, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex,
                                                     HttpServletRequest request) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("cause", rootCauseMessage(ex));

        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, details);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status,
                                                String message,
                                                HttpServletRequest request,
                                                Map<String, Object> details) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details
        );
        return ResponseEntity.status(status).body(body);
    }

    private String rootCauseMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        String msg = cur.getMessage();
        return msg == null ? cur.getClass().getSimpleName() : msg;
    }
}


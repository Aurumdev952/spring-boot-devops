package com.taskman.backend.models.domains;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ApiResponse<T> {
    private final String timestamp = LocalDateTime.now().toString();
    private T data;
    private String message;
    private HttpStatus status;

    public ApiResponse(T data, String message, HttpStatus status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        assert this.status != null;
        return ResponseEntity.status(this.status).body(this);
    }
}

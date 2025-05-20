package com.collaborativeTaskManagementSystem.taskManagement.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data@Builder@AllArgsConstructor@NoArgsConstructor
public class ResponseHandler<T> {
    private HttpStatus status;
    private T data;
}

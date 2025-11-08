package com.example.notification.Service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiResponse {
    private String message;
    private boolean success;
    private Object data;
}


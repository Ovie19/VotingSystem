package dev.fantasma.votingsystem.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private Object data;
    private String message;
    private boolean successful;

    public static ApiResponse success(Object data) {
        return new ApiResponse(data, "success", true);
    }

    public static ApiResponse failure(String message) {
        return new ApiResponse(null, message, false);
    }
}

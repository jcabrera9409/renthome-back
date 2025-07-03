package com.surrender.dto;

import java.time.LocalDateTime;

public class APIResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private int statusCode;
    private LocalDateTime timestamp;

    public APIResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public APIResponseDTO(boolean success, String message, T data, int statusCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> APIResponseDTO<T> success(String message, T data, int statusCode) {
		return new APIResponseDTO<>(true, message, data, statusCode);
	}
	
	public static <T> APIResponseDTO<T> error(String message, int statusCode) {
		return new APIResponseDTO<>(false, message, null, statusCode);
	}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

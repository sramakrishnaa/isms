package com.isms.identity.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

	private boolean success;

	private String message;

	private T data;

	private LocalDateTime timestamp;

	public static <T> ApiResponse<T> success(String message, T data) {

		return ApiResponse.<T>builder().success(true).message(message).data(data).timestamp(LocalDateTime.now())
				.build();
	}

	public static <T> ApiResponse<T> success(String message) {

		return ApiResponse.<T>builder().success(true).message(message).timestamp(LocalDateTime.now()).build();
	}
}
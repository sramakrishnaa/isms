package com.isms.identity.dto.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private Instant timestamp;

	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder().success(true).message(message).data(data).timestamp(Instant.now()).build();
	}

	public static <T> ApiResponse<T> error(String message) {
		return ApiResponse.<T>builder().success(false).message(message).timestamp(Instant.now()).build();
	}

}

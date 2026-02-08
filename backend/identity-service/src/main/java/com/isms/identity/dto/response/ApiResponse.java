package com.isms.identity.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
//@JsonPropertyOrder({ "timestamp", "success", "message", "data" })
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private Instant timestamp;

	public ApiResponse(boolean success, String message, T data) {
		super();
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> success(T data, String message) {
		return ApiResponse.<T>builder().success(true).message(message).data(data).timestamp(Instant.now()).build();
	}

	public static <T> ApiResponse<T> error(String message) {
		return ApiResponse.<T>builder().success(false).message(message).timestamp(Instant.now()).build();
	}

}

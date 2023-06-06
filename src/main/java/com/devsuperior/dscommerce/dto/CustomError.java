package com.devsuperior.dscommerce.dto;

import java.time.Instant;

public class CustomError {

	private Instant timestamp;
	private Integer status;
	private String error;
	private String path;

	public CustomError(final Instant timestamp, final Integer status, final String error, final String path) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.path = path;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getPath() {
		return path;
	}

}

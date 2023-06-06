package com.devsuperior.dscommerce.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException() {
		super("Resource non trouvé");
	}
	
	public ResourceNotFoundException(String msg) {
		super(msg);
	}

}

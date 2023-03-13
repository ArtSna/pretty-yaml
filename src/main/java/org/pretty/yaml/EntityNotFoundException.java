package org.pretty.yaml;

public class EntityNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4196051021232564452L;

	public EntityNotFoundException() {}
	
	public EntityNotFoundException(String message) {
		super(message);
	}
}

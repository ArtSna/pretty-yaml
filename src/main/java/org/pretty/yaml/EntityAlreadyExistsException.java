package org.pretty.yaml;

public class EntityAlreadyExistsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4196051021232564452L;

	public EntityAlreadyExistsException() {}
	
	public EntityAlreadyExistsException(String message) {
		super(message);
	}
}

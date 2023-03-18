package org.pretty.yaml;

import java.lang.reflect.Field;
import java.util.UUID;

public abstract class YamlEntity {

	private String id;
	private long createdAt;
	private long updatedAt;

	public YamlEntity() {
		this(UUID.randomUUID().toString());
	}
	
	public YamlEntity(String id) {
		this.id = id;
		this.createdAt = System.currentTimeMillis();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public long getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}
	
	public long getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public String generateId() {
		id = UUID.randomUUID().toString();
		return id;
	}
	
	protected void update() {
		this.updatedAt = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		String fields = "";
		
		for(Field field : getClass().getFields()) {
			try {
				fields += field.getName() + ": " + (field.get(this) == null ? "null" : field.get(this)) + ", ";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		fields = fields.substring(0, fields.length()-2);
		
		return id + " = [ " + fields + " ]";
	}
}

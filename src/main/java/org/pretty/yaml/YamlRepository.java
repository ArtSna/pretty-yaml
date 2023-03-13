package org.pretty.yaml;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;

public class YamlRepository {

	private final String name;
	private final YamlFile file;
	
	public YamlRepository(Plugin plugin, String name) {
		this.file = new YamlFile(plugin.getDataFolder(), "/yaml/repositories/" + name + ".yaml");
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void save(Object yamlEntity) {
		this.save(yamlEntity, true);
	}

	public void save(Object yamlEntity, boolean replaceData) {
		if(!(yamlEntity instanceof YamlEntity)) 
			throw new EntityException("entity object need extends YamlEntity");
		
		YamlEntity entity = (YamlEntity) yamlEntity;
		
		if(exists(entity.getId())) {
			entity.update();
			set(entity.getId() + ".updatedAt", entity.getUpdatedAt());
		} else {
			set(entity.getId() + ".createdAt", entity.getCreatedAt());
		}
		
		for(Field field : entity.getClass().getFields()) {
			try {
				if(!replaceData) 
					if(contains(entity.getId() + "." + field.getName()))
						continue;
				
				set(entity.getId() + "." + field.getName(), field.get(entity));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		file.saveFile();
	}
	
	public void delete(Object entityId) {
		delete(entityId.toString());
	}
	
	public void delete(String entityId) {
		if(!exists(entityId)) 
			throw new EntityNotFoundException("entity '" + entityId + "' not found");
		
		file.set(name + "." + entityId, null);
		file.saveFile();
	}
	
	public boolean exists(Object entityId) {
		return contains(entityId.toString());
	}
	
	public boolean exists(String entityId) {
		return contains(entityId);
	}
	
	public <T extends YamlEntity> List<T> findAll(Class<T> entityClass) {
		List<T> entities = new ArrayList<>();
		
		for(String entityId : file.getSectionList(name)) {
			entities.add(find(entityClass, entityId));
		}
		
		return entities;
	}
	
	public <T extends YamlEntity> List<T> findAllByField(Class<T> entityClass, String fieldName, Object fieldValue) {		
		return findAll(entityClass).stream().filter(e -> filter(e, fieldName, fieldValue)).collect(Collectors.toList());
	}
	
	public <T extends YamlEntity> T findByField(Class<T> entityClass, String fieldName, Object fieldValue) {
		return findAll(entityClass).stream().filter(e -> filter(e, fieldName, fieldValue)).findFirst().orElse(null);
	}
	
	public <T extends YamlEntity> T find(Class<T> entityClass, Object entityId) {
		return find(entityClass, entityId.toString());
	}
	
	public <T extends YamlEntity> T find(Class<T> entityClass, String entityId) {
		if(!exists(entityId)) 
			throw new EntityNotFoundException("entity '" + entityId + "' not found");
		
		T  entity = null;
		try {
			entity = entityClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}

		if(entity == null) 
			return null;
		
		for(Field field : entity.getClass().getFields()) {	
			try {
				field.set(entity, file.get(name + "." + entityId + "." + field.getName()));
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		entity.setId(entityId);
		entity.setCreatedAt(file.getLong(name + "." + entityId + ".createdAt"));
		if(contains(entityId + ".updatedAt"))
			entity.setUpdatedAt(file.getLong(name + "." + entityId + ".updatedAt"));
				
		return entity;
	}
	
	private void set(String path, Object value) {
		file.set(name + "." + path, value);
	}
	
	private boolean contains(String path) {
		return file.contains(name + "." + path);
	}

	private <T> boolean filter(T e, String fieldName, Object fieldValue) {
		try {
			return e.getClass().getField(fieldName).get(e).equals(fieldValue);
		} catch (IllegalArgumentException | IllegalAccessException  | SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchFieldException e2) {
			throw new EntityException("field '" + fieldName + "' not found");
		}
		return false;
	}
	
}

package org.pretty.yaml;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.plugin.Plugin;

public class YamlRepository<T extends YamlEntity> {

	private final String name;
	private final YamlFile file;
	private final Class<T> entityClass;
	
	public YamlRepository(Plugin plugin, String name, Class<T> entityClass) {
		this.file = new YamlFile(plugin.getDataFolder(), "/repositories/yaml/" + name + ".yaml");
		this.name = name;
		this.entityClass = entityClass;
	}
	
	public String getName() {
		return name;
	}
	
	public void save(YamlEntity entity) {
		this.save(entity, true);
	}

	public void save(YamlEntity entity, boolean replaceData) {
		
		if(exists(entity.getId())) {
			entity.update();
			file.set(entity.getId() + ".updatedAt", entity.getUpdatedAt());
		} else {
			file.set(entity.getId() + ".createdAt", entity.getCreatedAt());
		}
		
		for(Field field : entity.getClass().getFields()) {
			try {
				if(!replaceData) 
					if(file.contains(entity.getId() + "." + field.getName()))
						continue;
				
				Object val = field.get(entity);
				file.set(entity.getId() + "." + field.getName(), val != null && val.getClass().isEnum() ? val.toString() : val);
				
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
		
		file.set(entityId, null);
		file.saveFile();
	}
	
	public boolean exists(Object entityId) {
		return file.contains(entityId.toString());
	}
	
	public boolean exists(String entityId) {
		return file.contains(entityId);
	}
	
	public List<T> findAll() {
		List<T> entities = new ArrayList<>();
		
		for(String entityId : file.getSectionList(name)) {
			entities.add(find(entityId));
		}
		
		return entities;
	}
	
	public List<T> findAllByField(String fieldName, Object fieldValue) {		
		return findAll().stream().filter(e -> filter(e, fieldName, fieldValue)).collect(Collectors.toList());
	}
	
	public T findByField(String fieldName, Object fieldValue) {
		return findAll().stream().filter(e -> filter(e, fieldName, fieldValue)).findFirst().orElse(null);
	}
	
	public T find(Object entityId) {
		return find(entityId.toString());
	}
	
	public T find(String entityId) {
		if(!exists(entityId)) 
			return null;
		
		T  entity = instantiate();
		
		entity.setId(entityId);
		entity.setCreatedAt(file.getLong(entityId + ".createdAt"));
		
		if(file.contains(entityId + ".updatedAt"))
			entity.setUpdatedAt(file.getLong(entityId + ".updatedAt"));
		
		for(Field field : entity.getClass().getFields()) {
			if(!file.contains(entityId + "." + field.getName()))
				continue;
			
			try {
				if(field.getType().isEnum())
					field.set(entity, getEnum(field.getType(), file.getString(entityId + "." + field.getName())));
				else
					field.set(entity, file.get(entityId + "." + field.getName()));
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException  e) {
				e.printStackTrace();
			}
		}
				
		return entity;
	}

	private boolean filter(T e, String fieldName, Object fieldValue) {
		try {
			return e.getClass().getField(fieldName).get(e).equals(fieldValue);
		} catch (IllegalArgumentException | IllegalAccessException  | SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchFieldException e2) {
			throw new EntityException("field '" + fieldName + "' not found");
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private <E extends Enum<E>> E getEnum(Class<?> class1, Object val) {
		return Enum.valueOf((Class<E>) class1, val.toString());
	}
	
	private T instantiate() {
		try {
			return entityClass.getConstructor().newInstance();
		} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}

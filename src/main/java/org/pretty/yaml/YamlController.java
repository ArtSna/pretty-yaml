package org.pretty.yaml;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

public class YamlController {

	private final Plugin plugin;
	private List<YamlRepository> cache = new ArrayList<>();
	
	public YamlController(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public YamlRepository getRepository(String repositoryName) {
		YamlRepository repository = cache.stream().filter(t -> t.getName().equals(repositoryName)).findFirst().orElse(null);
		
		if(repository == null) {
			repository = new YamlRepository(plugin, repositoryName);
			cache.add(repository);
		}
		
		return repository;
	}
}

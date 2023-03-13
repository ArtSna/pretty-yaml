[![](https://jitpack.io/v/TakuiasH/pretty-yaml.svg)](https://jitpack.io/#TakuiasH/pretty-yaml)

# pretty-yaml
A prettier solution of dealing with spigot yaml files

## Usage
Here is an example
```java
public class UserEntity extends YamlEntity {

	public String name;
	public String data;
	
}
```

```java
public class Main extends JavaPlugin {
	
	YamlController yamlController = new YamlController(this);
	
	@Override
	public void onEnable() {
		YamlRepository usersRepo = yamlController.getRepository("users");
		
		//Creating new user
		UserEntity user = new UserEntity();
		user.generateId(); //Generate a new UUID

		if(!usersRepo.exists(user.getId())) {
			
			user.name = "TakuiasH";
			user.data = "test";
			
			usersRepo.save(user);
		}
		
		
		//Updating an user
		UserEntity user2 = usersRepo.findByField(UserEntity.class, "name", "TakuiasH");
		
		if(user2 != null) {
			user2.data = "Hello World!";
			usersRepo.save(user2);
		}
		
		//Find by id
		System.out.println(usersRepo.find(UserEntity.class, user2.getId()).toString());
	}
	
}
```

## Setup
Before get started, make sure you have the [JitPack repository](https://jitpack.io/#TakuiasH/pretty-yaml) included in your build configuration.

Maven
```xml
<dependency>
    <groupId>com.github.TakuiasH</groupId>
    <artifactId>pretty-yaml</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

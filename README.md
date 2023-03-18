[![](https://jitpack.io/v/TakuiasH/pretty-yaml.svg)](https://jitpack.io/#TakuiasH/pretty-yaml)

# pretty-yaml
A prettier solution of dealing with spigot yaml files

## Usage
Here is an example

```java
public enum Role {
	LEADER,
	ADMIN,
	MEMBER;
}
```

```java
public class UserEntity extends YamlEntity {
	public String name;
	public String data;
	public Role role;
}
```

```java
public class Main extends JavaPlugin {
	
	YamlController yamlController = new YamlController(this);
	
	@Override
	public void onEnable() {
		YamlRepository<UserEntity> usersRepo = yamlController.getRepository("users", UserEntity.class);
		
		//Creating new user
		UserEntity user = new UserEntity();
		
		if(!usersRepo.exists(user.getId())) {
			user.name = "TakuiasH";
			user.data = "test";
			user.role = Role.OWNER;

			usersRepo.save(user);
		}
		
		
		//Updating an user
		UserEntity user2 = usersRepo.findByField("name", "TakuiasH");
		
		if(user2 != null) {
			user2.data = "Hello World!";
			user.role = Role.MEMBER;

			usersRepo.save(user2);
		}
		
		//Find by id
		System.out.println(usersRepo.find(user2.getId()).toString());
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
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

package org.pretty.yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

public class YamlFile extends YamlConfiguration {

	private final File file;
	
	public YamlFile(File directory, String fileName) {
		this(new File(directory, fileName));
	}
	
	public YamlFile(File file) {
		this(file, true);
	}

	
	public YamlFile(File file, boolean createIfNotExists) {
		this.file = file;
		
		try {
			if(createIfNotExists && !file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
				
				System.err.println("creating new file '" + file.getPath() + "'");
			}
			
			if(file.exists())
				load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String getFilePath() {
		return file.getPath();
	}
	
    /**
     * Saves this {@link FileConfiguration}.
     * <p>
     * If the file does not exist, it will be created. If already exists, it
     * will be overwritten. If it cannot be overwritten or created, an
     * exception will be thrown.
     * <p>
     * This method will save using the system default encoding, or possibly
     * using UTF8.
     *
     * @throws IOException Thrown when the given file cannot be written to for
     *     any reason.
     * @throws IllegalArgumentException Thrown when file is null.
     */
	public void saveFile() {
		try {
			save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Reload this {@link FileConfiguration}.
     * <p>
     * All the values contained within this configuration will be removed,
     * leaving only settings and defaults, and the new values will be loaded
     * from the given file.
     * <p>
     * If the file cannot be loaded for any reason, an exception will be
     * thrown.
     * <p>
     * This will attempt to use the {@link Charset#defaultCharset()} for
     * files, unless {@link #UTF8_OVERRIDE} but not {@link #UTF_BIG} is
     * specified.
     *
     * @throws FileNotFoundException Thrown when the given file cannot be
     *     opened.
     * @throws IOException Thrown when the given file cannot be read.
     * @throws InvalidConfigurationException Thrown when the given file is not
     *     a valid Configuration.
     * @throws IllegalArgumentException Thrown when file is null.
     */
	public void reloadFile() {
		try {
			load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
    public void saveDefaultConfig(Plugin plugin) {
    	saveResource(plugin, file.getName(), false);
    }
	
	public void saveResource(Plugin plugin, String resourcePath, boolean replace) {
		if(file.exists())
			return;
		
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = plugin.getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + file);
        }

        File outFile = new File(plugin.getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(plugin.getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        
        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
                
                try {
					load(outFile);
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
            } else {
                plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
        	plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }
	
    public Set<String> getSectionList(String path) {
    	ConfigurationSection cs = getConfigurationSection(path);
    	
    	if(cs == null)
    		return null;
    	
    	return cs.getKeys(false);
    }
	
	@Override
	public void set(String path, Object value) {
		if(value instanceof Location)
			this.set(path, (Location) value, true);
		else
			super.set(path, value);
	}
	
    public float getFloat(String path) {
        Object def = getDefault(path);
        return getFloat(path, (def instanceof Number) ? NumberConversions.toFloat(def) : 0);
    }

    public float getFloat(String path, float def) {
        Object val = get(path, def);
        return (val instanceof Number) ? NumberConversions.toFloat(val) : def;
    }
	
	public void set(String path, Location location, boolean yawAndPitch) {
		set(path + ".world", location.getWorld().getName());
		set(path + ".x", location.getX());
		set(path + ".y", location.getY());
		set(path + ".z", location.getZ());
		
		if(yawAndPitch) {
			set(path + ".yaw", location.getYaw());
			set(path + ".pitch", location.getPitch());
		}
	}
	public Location getLocation(String path) {
		return getLocation(path, true);
	}

	public Location getLocation(String path, boolean yawAndPitch) {		
		String worldName = getString(path + ".world");
		double x = getDouble(path + ".x");
		double y = getDouble(path + ".y");
		double z = getDouble(path + ".z");
		float yaw = getFloat(path + ".yaw");
		float pitch = getFloat(path + ".Pitch");
		
		if(contains(path + ".world")) throw new NullPointerException("path '" + path + ".world' is not defined");
		if(contains(path + ".x")) throw new NullPointerException("path '" + path + ".x' is not defined");
		if(contains(path + ".y")) throw new NullPointerException("path '" + path + ".y' is not defined");
		if(contains(path + ".z")) throw new NullPointerException("path '" + path + ".z' is not defined");
		
		if(yawAndPitch) {
			if(contains(path + ".yaw")) throw new NullPointerException("path '" + path + ".yaw' is not defined");
			if(contains(path + ".pitch")) throw new NullPointerException("path '" + path + ".pitch' is not defined");
		}
		
		World world = Bukkit.getWorld(worldName);
		
		if(world == null) throw new NullPointerException("world '" + worldName + "' not exists");
				
		return yawAndPitch ? new Location(world, x, y, z, yaw, pitch) : new Location(world, x, y, z);
	}
}

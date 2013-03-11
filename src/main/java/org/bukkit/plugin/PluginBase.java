package org.bukkit.plugin;

import com.avaje.ebean.EbeanServer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Represents a base {@link Plugin}
 * <p />
 * Extend this class if your plugin is not a {@link org.bukkit.plugin.java.JavaPlugin}
 */
public abstract class PluginBase implements Plugin {
    private PluginEnvironment<? extends PluginEnvironment> environment;
    private boolean isEnabled = false;
    private FileConfiguration newConfig;

    /**
     * Creates a new Plugin, with it's PluginEnvironment configured by 
     * querying this class's classloader for it's PluginEnvironment.
     * 
     * @throws IllegalStateException if this class was not loaded by a PluginClassLoader
     */
    public PluginBase() {
        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader instanceof PluginClassLoader) {
            this.environment = ((PluginClassLoader<? extends PluginEnvironment>)classLoader).getEnvironment();
        } else {
            throw new IllegalStateException("PluginBase() constructor can only be invoked by classes loaded by a PluginClassLoader");
        }
    }

    /**
     * Creates a new Plugin, with the passed PluginEnvironment.
     * 
     * @param environment the environment for this Plugin.
     */
    public PluginBase(PluginEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Plugin)) {
            return false;
        }
        return getName().equals(((Plugin) obj).getName());
    }

    /**
     * Sets the enabled state of this plugin
     *
     * @param enabled true if enabled, otherwise false
     */
    /* pp */ final void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;

            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public final boolean isEnabled() {
        return isEnabled;
    }

    public final PluginEnvironment getPluginEnvironment() {
        return environment;
    }

    public final String getName() {
        return environment.getPluginName();
    }

    public final File getDataFolder() {
        return environment.getDataFolder();
    }

    public final PluginDescriptionFile getDescription() {
        return environment.getPluginDescriptionFile();
    }

    public final PluginLoader getPluginLoader() {
        return environment.getPluginLoader();
    }

    public final Server getServer() {
        return environment.getServer();
    }

    public final Logger getLogger() {
        return environment.getLogger();
    }

    public final File getFile() {
        return environment.getPluginFile();
    }

    public final PluginClassLoader getClassLoader() {
        return environment.getPluginClassLoader();
    }

    public FileConfiguration getConfig() {
        if (newConfig == null) {
            reloadConfig();
        }
        return newConfig;
    }

    public void reloadConfig() {
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);

            newConfig.setDefaults(defConfig);
        }
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

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
            } else {
                getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return getDescription().getFullName();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bukkit.plugin;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.Dependency.Type;

/**
 * An environment for a plugin.
 * 
 * @author antony
 */
public abstract class PluginEnvironment<T extends PluginEnvironment> {
    public static final String CONFIG_FILE = "config.yml";
    private final Server server;
    private final PluginDescriptionFile description;
    private final File file;
    private final File dataFolder;
    private final File configFile;
    private PluginClassLoader<T> classLoader;
    private final PluginLoader loader;
    private final Logger logger;
    private final Set<Dependency> dependency = new HashSet<Dependency>();
    private final Set<Dependency> dependedOn = new HashSet<Dependency>();

    protected PluginEnvironment(Server server, PluginDescriptionFile description, File file, PluginLoader loader) {
        this.server = server;
        this.description = description;
        this.file = file;
        this.loader = loader;
        this.logger = new PluginLogger(this);
        this.dataFolder = new File(file.getParentFile(), description.getName());
        if (!dataFolder.isDirectory()) {
            dataFolder.mkdirs();
        }
        this.configFile = new File(dataFolder, CONFIG_FILE);
    }

    protected void setPluginClassLoader(PluginClassLoader<T> classLoader) {
        this.classLoader = classLoader;
    }

    public PluginLoader getPluginLoader() {
        return loader;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Get the PluginClassLoader associated with this PluginEnvironment.
     * @return A PluginClassLoader
     */
    public PluginClassLoader<T> getPluginClassLoader() {
        return classLoader;
    }

    /**
     * Get the Server this environment is for.
     * @return the server.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Get the name of the plugin this PluginEnvironment is for.
     * @return the plugin name
     */
    public String getPluginName() {
        return description.getName();
    }

    /**
     * Get the plugin description file for this PluginEnvironment.
     * 
     * @return the plugin description file.
     */
    public PluginDescriptionFile getPluginDescriptionFile() {
        return description;
    }

    public File getPluginFile() {
        return file;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public File getConfigFile() {
        return configFile;
    }

    /**
     * Loads the plugin for this Environment.
     * 
     * Each call will create a new Plugin, so use with care.
     * 
     * @return the plugin.
     */
    public abstract Plugin loadPlugin();

    /**
     * Returns a set of direct dependencies of this plugin.
     * 
     * Soft dependencies are only included if they are present.
     * 
     * @return dependencies
     */
    public Collection<Dependency> getDependencies() {
        return Collections.unmodifiableCollection(dependency);
    }
    
    /**
     * Returns a set of plugins this plugin depends on.
     * 
     * Soft dependencies are only included if they are present.
     * 
     * @param deep If true, include dependencies of dependencies
     * @return dependencies
     */
    public Collection<Dependency> getDependencies(boolean deep) {
        if (!deep) {
            return getDependencies();
        } else {
            Set<Dependency> ret = new HashSet<Dependency>();
            Set<Dependency> done = new HashSet<Dependency>();
            getDependencies(ret, done);
            return ret;
        }
    }
    
    /**
     * Returns a set of plugins which depend directly on this plugin.
     * 
     * @return plugins that depend on this plugin.
     */
    public Collection<Dependency> getDependedOn() {
        return Collections.unmodifiableCollection(dependedOn);
    }
    
    /**
     * Returns a set of plugins which depend on this plugin.
     * 
     * @param deep If true, include plugins which do not directly depend on this plugin.
     * @return plugins that depend on this plugin.
     */
    public Collection<Dependency> getDependedOn(boolean deep) {
        if (!deep) {
            return getDependedOn();
        } else {
            Set<Dependency> ret = new HashSet<Dependency>();
            Set<Dependency> done = new HashSet<Dependency>();
            getDependedOn(ret, done);
            return ret;
        }
    }
    
    private void getDependencies(Set<Dependency> result, Set<Dependency> done) {
        result.addAll(dependency);
        for (Dependency dep : dependency) {
            done.add(dep);
            dep.getTo().getDependencies(result, done);
        }
    }
    
    private void getDependedOn(Set<Dependency> result, Set<Dependency> done) {
        result.addAll(dependedOn);
        for (Dependency dep : dependedOn) {
            done.add(dep);
            dep.getFrom().getDependedOn(result, done);
        }
    }

    /**
     * Add a dependency.
     * 
     * Do not call directly, use new Dependency()
     * @param dep 
     */
    /* pp */ void addDependency(Dependency dep) {
        this.dependency.add(dep);
    }

    /**
     * Add a dependency on this environment.
     * 
     * Do not call directly, use new Dependency()
     * @param dep 
     */
    /* pp */ void addDependedOn(Dependency dep) {
        this.dependedOn.add(dep);
    }

    /**
     * Remove a dependency.
     * 
     * Do not call directly, use Dependency.remove() instead.
     * @param dep 
     */
    /* pp */ void removeDependency(Dependency dep) {
        this.dependency.remove(dep);
    }
    
    /**
     * Remove a dependency on this environment.
     * 
     * Do not call directly, use Dependency.remove() instead.
     * @param dep 
     */
    /* pp */ void removeDependedOn(Dependency dep) {
        this.dependedOn.remove(dep);
    }

    /* pp */ void validateDependencies() throws InvalidPluginException {
        boolean valid = true;
        Iterator<Dependency> i = dependency.iterator();
        while (i.hasNext()) {
            Dependency dep = i.next();
            if (dep.getTo() == null) {
                if (dep.getType() == Type.DEPEND) {
                    valid = false;
                    break;
                }
                i.remove();
            }
            // TODO
        }
    }
}

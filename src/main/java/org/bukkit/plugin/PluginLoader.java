package org.bukkit.plugin;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * Represents a plugin loader, which handles direct access to specific types
 * of plugins
 */
public interface PluginLoader {

    /**
     * Loads an environment for the plugin contained in the specified file
     *
     * @param file File to attempt to load
     * @return PluginEnvironment for the specified file, or null if unsuccessful
     * @throws InvalidPluginException Thrown when the specified file is not a plugin
     */
    public PluginEnvironment loadPluginEnvironment(File file) throws InvalidPluginException, InvalidDescriptionException;

    /**
     * Returns a list of all filename filters expected by this PluginLoader
     *
     * @return The filters
     */
    public Pattern[] getPluginFileFilters();

}

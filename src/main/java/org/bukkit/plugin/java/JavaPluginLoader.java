package org.bukkit.plugin.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginEnvironment;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Represents a Java plugin loader, allowing plugins in the form of .jar
 */
public final class JavaPluginLoader implements PluginLoader {
    final Server server;

    private final Pattern[] fileFilters0 = new Pattern[] { Pattern.compile("\\.jar$"), };
    /**
     * @deprecated Internal field that wasn't intended to be exposed
     */
    @Deprecated
    protected final Pattern[] fileFilters = fileFilters0;

    /**
     * Do not instantiate this class.
     * @param instance The server this is for.
     */
    public JavaPluginLoader(Server instance) {
        Validate.notNull(instance, "Server cannot be null");
        server = instance;
    }

    /*
    public Plugin loadPlugin(File file) throws InvalidPluginException {
        Validate.notNull(file, "File cannot be null");

        if (!file.exists()) {
            throw new InvalidPluginException(new FileNotFoundException(file.getPath() + " does not exist"));
        }

        PluginDescriptionFile description;
        try {
            description = getPluginDescription(file);
        } catch (InvalidDescriptionException ex) {
            throw new InvalidPluginException(ex);
        }

        File dataFolder = new File(file.getParentFile(), description.getName());
        File oldDataFolder = extended ? getDataFolder(file) : getDataFolder0(file); // Don't warn on deprecation, but maintain overridability

        // Found old data folder
        if (dataFolder.equals(oldDataFolder)) {
            // They are equal -- nothing needs to be done!
        } else if (dataFolder.isDirectory() && oldDataFolder.isDirectory()) {
            server.getLogger().log(Level.INFO, String.format(
                "While loading %s (%s) found old-data folder: %s next to the new one: %s",
                description.getName(),
                file,
                oldDataFolder,
                dataFolder
            ));
        } else if (oldDataFolder.isDirectory() && !dataFolder.exists()) {
            if (!oldDataFolder.renameTo(dataFolder)) {
                throw new InvalidPluginException("Unable to rename old data folder: '" + oldDataFolder + "' to: '" + dataFolder + "'");
            }
            server.getLogger().log(Level.INFO, String.format(
                "While loading %s (%s) renamed data folder: '%s' to '%s'",
                description.getName(),
                file,
                oldDataFolder,
                dataFolder
            ));
        }

        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidPluginException(String.format(
                "Projected datafolder: '%s' for %s (%s) exists and is not a directory",
                dataFolder,
                description.getName(),
                file
            ));
        }

        List<String> depend = description.getDepend();
        if (depend == null) {
            depend = ImmutableList.<String>of();
        }

        for (String pluginName : depend) {
            if (loaders0 == null) {
                throw new UnknownDependencyException(pluginName);
            }
            PluginClassLoader current = loaders0.get(pluginName);

            if (current == null) {
                throw new UnknownDependencyException(pluginName);
            }
        }

        PluginClassLoader loader = null;
        JavaPlugin result = null;

        try {
            URL[] urls = new URL[1];

            urls[0] = file.toURI().toURL();

            if (description.getClassLoaderOf() != null) {
                loader = loaders0.get(description.getClassLoaderOf());
                loader.addURL(urls[0]);
            } else {
                loader = new PluginClassLoader(this, urls, getClass().getClassLoader(), null);
            }

            Class<?> jarClass = Class.forName(description.getMain(), true, loader);
            Class<? extends JavaPlugin> plugin = jarClass.asSubclass(JavaPlugin.class);

            Constructor<? extends JavaPlugin> constructor = plugin.getConstructor();

            result = constructor.newInstance();

            result.initialize(this, server, description, dataFolder, file, loader);
        } catch (InvocationTargetException ex) {
            throw new InvalidPluginException(ex.getCause());
        } catch (Throwable ex) {
            throw new InvalidPluginException(ex);
        }

        loaders0.put(description.getName(), loader);

        return result;
    }
    */

    public Pattern[] getPluginFileFilters() {
        return fileFilters0.clone();
    }

    // XXX: Move to JavaPluginEnvironment.
    /*
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, final Plugin plugin) {
        Validate.notNull(plugin, "Plugin can not be null");
        Validate.notNull(listener, "Listener can not be null");

        boolean useTimings = server.getPluginManager().useTimings();
        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<Class<? extends Event>, Set<RegisteredListener>>();
        Set<Method> methods;
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            methods = new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
            for (Method method : publicMethods) {
                methods.add(method);
            }
            for (Method method : listener.getClass().getDeclaredMethods()) {
                methods.add(method);
            }
        } catch (NoClassDefFoundError e) {
            plugin.getLogger().severe("Plugin " + plugin.getDescription().getFullName() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.");
            return ret;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            final Class<?> checkClass = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(checkClass) || method.getParameterTypes().length != 1) {
                plugin.getLogger().severe(plugin.getDescription().getFullName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }
            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredListener> eventSet = ret.get(eventClass);
            if (eventSet == null) {
                eventSet = new HashSet<RegisteredListener>();
                ret.put(eventClass, eventSet);
            }

            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    Warning warning = clazz.getAnnotation(Warning.class);
                    WarningState warningState = server.getWarningState();
                    if (!warningState.printFor(warning)) {
                        break;
                    }
                    plugin.getLogger().log(
                            Level.WARNING,
                            String.format(
                                    "\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated." +
                                    " \"%s\"; please notify the authors %s.",
                                    plugin.getDescription().getFullName(),
                                    clazz.getName(),
                                    method.toGenericString(),
                                    (warning != null && warning.reason().length() != 0) ? warning.reason() : "Server performance will be affected",
                                    Arrays.toString(plugin.getDescription().getAuthors().toArray())),
                            warningState == WarningState.ON ? new AuthorNagException(null) : null);
                    break;
                }
            }

            EventExecutor executor = new EventExecutor() {
                public void execute(Listener listener, Event event) throws EventException {
                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        method.invoke(listener, event);
                    } catch (InvocationTargetException ex) {
                        throw new EventException(ex.getCause());
                    } catch (Throwable t) {
                        throw new EventException(t);
                    }
                }
            };
            if (useTimings) {
                eventSet.add(new TimedRegisteredListener(listener, executor, eh.priority(), plugin, eh.ignoreCancelled()));
            } else {
                eventSet.add(new RegisteredListener(listener, executor, eh.priority(), plugin, eh.ignoreCancelled()));
            }
        }
        return ret;
    }
    */

    public PluginEnvironment loadPluginEnvironment(File file) throws InvalidPluginException, InvalidDescriptionException {
        Validate.notNull(file, "File cannot be null");
        JarFile jar = null;
        Exception e;
        try {
            jar = new JarFile(file);
            JarEntry entry = jar.getJarEntry("plugin.yml");

            if (entry == null) {
                throw new FileNotFoundException("Jar does not contain plugin.yml");
            }

            PluginDescriptionFile description;
            InputStream stream = jar.getInputStream(entry);
            try {
                description = new PluginDescriptionFile(stream);
            } finally {
                stream.close();
            }
            return new JavaPluginEnvironment(server, description, file, this);
        } catch (IOException ex) {
            e = ex;
        } catch (YAMLException ex) {
            e = ex;
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException ex) {
                }
            }
        }
        throw new InvalidDescriptionException(e);
    }
}

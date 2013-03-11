/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bukkit.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import org.bukkit.plugin.java.JavaPluginEnvironment;

/**
 * Plugin ClassLoader, resolves classes and resources in the following order:
 * parent, self, dependencies.
 * 
 * The vast majority of scripting languages include the ability to call into
 * Java, without a classloader they will not be able to depend on other
 * plugins.
 * 
 * @author antony
 */
public class PluginClassLoader<T extends PluginEnvironment> extends URLClassLoader {

    private final T environment;

    public PluginClassLoader(T environment, URL[] classPath, ClassLoader parent) throws IOException {
        super(classPath, parent);
        this.environment = environment;
    }

    public T getEnvironment() {
        return environment;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
	throws ClassNotFoundException
    {
        Class c = super.loadClass(name, resolve);
        if (c != null) {
            return c;
        }
        Iterator<Dependency> it = getEnvironment().getDependencies().iterator();
        while (it.hasNext()) {
            Dependency dep = it.next();
            PluginEnvironment depEnvironment = dep.getTo();
            if (depEnvironment instanceof JavaPluginEnvironment && dep.getType().getInherit()) {
                PluginClassLoader<?> parentClassLoader = depEnvironment.getPluginClassLoader();
                if (parentClassLoader != null) {
                    c = parentClassLoader.loadClass(name, resolve);
                    if (c != null) {
                        return c;
                    }
                }
            }
        }
        return c;
    }
    
    // TODO: Resource loading from dependencies.
}

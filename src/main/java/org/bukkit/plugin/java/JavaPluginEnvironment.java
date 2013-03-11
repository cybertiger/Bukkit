/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bukkit.plugin.java;

import org.bukkit.plugin.PluginClassLoader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginEnvironment;
import org.bukkit.plugin.PluginLoader;

/**
 *
 * @author antony
 */
public class JavaPluginEnvironment extends PluginEnvironment<JavaPluginEnvironment> {

    /* pp */ JavaPluginEnvironment(Server server, PluginDescriptionFile description, File file, PluginLoader loader) throws IOException {
        super(server, description, file, loader);
        PluginClassLoader<JavaPluginEnvironment> classLoader = 
                new PluginClassLoader<JavaPluginEnvironment>(
                        this,
                        new URL[] {file.toURI().toURL()},
                        this.getClass().getClassLoader());
        setPluginClassLoader(classLoader);
    }

    @Override
    public Plugin loadPlugin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

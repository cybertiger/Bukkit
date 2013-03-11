package org.bukkit.plugin;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;

import com.avaje.ebean.EbeanServer;

public class TestPlugin extends PluginBase {
    private boolean enabled = true;

    final private String pluginName;

    public TestPlugin(String pluginName, PluginEnvironment<? extends PluginEnvironment> environment) {
        super(environment);
        this.pluginName = pluginName;
    }

    public void onDisable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void onLoad() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void onEnable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean isNaggable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void setNaggable(boolean canNag) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public EbeanServer getDatabase() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        throw new UnsupportedOperationException("Not supported.");
    }
}

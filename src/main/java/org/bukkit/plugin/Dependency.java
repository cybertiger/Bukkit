/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bukkit.plugin;

import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author antony
 */
public class Dependency {
    public enum Type {
        DEPEND(true),
        SOFT_DEPEND(true),
        LOAD_AFTER(false);

        private final boolean inherit;

        private Type(boolean inherit) {
            this.inherit = inherit;
        }

        /**
         * Whether to inherit programmatic objects via this type of dependency.
         * 
         * @return true if we should inherit programmatic objects
         */
        public boolean getInherit() {
            return inherit;
        }
    }

    private final Type type;
    private PluginEnvironment from;
    private PluginEnvironment to;

    /* pp */
    Dependency(Type type, PluginEnvironment from, final PluginEnvironment to) {
        this.type = type;
        this.from = from;
        this.to = to;
        if (from != null) from.addDependency(this);
        if (to != null) to.addDependedOn(this);
    }

    public Type getType() {
        return type;
    }

    public PluginEnvironment getFrom() {
        return from;
    }

    public PluginEnvironment getTo() {
        return to;
    }

    /* pp */ void setFrom(PluginEnvironment from) {
        this.from = from;
    }

    /* pp */ void setTo(PluginEnvironment to) {
        this.to = to;
    }

    /**
     * Remove this dependency, note: this modifies from.dependency and to.dependedOn.
     * Which will cause issues if you call it whilst iterating either set.
     */
    /* pp */ void remove() {
        if (from != null) from.removeDependency(this);
        if (to != null) to.removeDependency(this);
    }
}

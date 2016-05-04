package de.teamlapen.lib.lib.block;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Unlisted String property that accepts all values
 */
public class PropertyStringUnlisted implements IUnlistedProperty<String> {
    private final String name;

    public PropertyStringUnlisted(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public boolean isValid(String value) {
        return value != null;
    }

    @Override
    public String valueToString(String value) {
        return value;
    }
}

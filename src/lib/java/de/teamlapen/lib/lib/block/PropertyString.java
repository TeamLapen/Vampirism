/*
 * Inspired by @WayofTime's https://github.com/WayofTime/BloodMagic/blob/da6f41039499ea85e77beabf1a685901e7a3323e/src/main/java/WayofTime/bloodmagic/block/property/PropertyString.java
 */

package de.teamlapen.lib.lib.block;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class PropertyString extends PropertyHelper<String> {
    public static PropertyString create(String name, String[] values) {
        return new PropertyString(name, values);
    }
    private final ImmutableSet<String> allowedValues;

    protected PropertyString(String name, String[] values) {
        super(name, String.class);

        HashSet<String> hashSet = Sets.newHashSet();
        hashSet.addAll(Arrays.asList(values));
        allowedValues = ImmutableSet.copyOf(hashSet);
    }

    @Override
    public Collection<String> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public String getName(String value) {
        return value;
    }

    @SideOnly(Side.CLIENT)
    public Optional<String> parseValue(String value) {
        try {

            return this.allowedValues.contains(value) ? Optional.of(value) : Optional.<String>absent();
        } catch (NumberFormatException var3) {
            return Optional.absent();
        }
    }
}

package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;

/**
 * Base class for most of Vampirism's tileInventory
 */
public class VampirismItem extends Item {
    protected final String regName;
    private String translation_key;

    /**
     * Set's the registry name and the unlocalized name
     *
     */
    public VampirismItem(String regName, Item.Properties properties) {
        super(properties);
        this.regName = regName;
        setRegistryName(REFERENCE.MODID, regName);
    }

    @Nonnull
    @Override
    protected String getOrCreateDescriptionId() {
        if (this.translation_key == null) {
            this.translation_key = Util.makeDescriptionId("item", Registry.ITEM.getKey(this));
        }

        return this.translation_key;
    }

    /**
     * Set a custom translation key
     */
    protected void setTranslation_key(String name) {
        this.translation_key = Util.makeDescriptionId("item", new ResourceLocation(REFERENCE.MODID, name));
    }
}

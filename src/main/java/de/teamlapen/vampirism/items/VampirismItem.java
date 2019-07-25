package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Base class for most of Vampirism's tileInventory
 */
public class VampirismItem extends Item {
    protected final String regName;
    private String translation_key;

    /**
     * Set's the registry name and the unlocalized name
     *
     * @param regName
     */
    public VampirismItem(String regName, Item.Properties properties) {
        super(properties);
        this.regName = regName;
        setRegistryName(REFERENCE.MODID, regName);
    }

    @Override
    protected String getDefaultTranslationKey() {
        if (this.translation_key == null) {
            this.translation_key = Util.makeTranslationKey("item", Registry.ITEM.getKey(this));
        }

        return this.translation_key;
    }
    /**
     * Set a custom translation key
     */
    protected void setTranslation_key(String name) {
        this.translation_key = Util.makeTranslationKey("item", new ResourceLocation(REFERENCE.MODID, name));
    }

    public ITextComponent getCustomName() {
        return new TranslationTextComponent(this.getDefaultTranslationKey());
    }

}

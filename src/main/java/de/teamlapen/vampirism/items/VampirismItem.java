package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Item;

/**
 * Base class for most of Vampirism's items
 */
public class VampirismItem extends Item {
    protected final String regName;

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

    /**
     * @return The name this item is registered in the GameRegistry
     */
    public String getRegisteredName() {
        return regName;
    }

}

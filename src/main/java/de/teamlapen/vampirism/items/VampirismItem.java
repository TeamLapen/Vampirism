package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
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
    public VampirismItem(String regName) {
        this.regName = regName;
        setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }


    public String getLocalizedName() {
        return UtilLib.translate(getUnlocalizedName() + ".name");
    }

    /**
     * @return The name this item is registered in the GameRegistry
     */
    public String getRegisteredName() {
        return regName;
    }

}

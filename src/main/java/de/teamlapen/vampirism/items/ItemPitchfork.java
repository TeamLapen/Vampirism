package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemSword;

/**
 * Created by max on 08.05.16.
 */
public class ItemPitchfork extends ItemSword {

    private final static String regName = "pitchfork";

    public ItemPitchfork() {
        super(ToolMaterial.IRON);
        this.setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }
}

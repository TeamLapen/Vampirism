package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.item.Item;


public class MinionUpgradeItem extends VampirismItem {
    private final int level;

    public MinionUpgradeItem(int level, String regName) {
        super(regName, new Item.Properties().group(VampirismMod.creativeTab));
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}

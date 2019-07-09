package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;

public class VampirismSpawnEgg extends SpawnEggItem {

    public VampirismSpawnEgg(EntityType<?> typeIn, String id) {
        super(typeIn, 0x8B15A3, id.hashCode(), (new Item.Properties()).group(ItemGroup.MISC));
        this.setRegistryName(REFERENCE.MODID, id);
    }
}

package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ILootTable;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.List;

@Deprecated
public interface ILootTableVampirismMock extends ILootTable {
    @Override
    default List<LootPool> getPools() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}

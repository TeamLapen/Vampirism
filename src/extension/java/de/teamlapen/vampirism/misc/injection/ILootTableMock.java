package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ILootTable;
import net.minecraft.world.level.storage.loot.LootPool;

import java.util.List;

public interface ILootTableMock extends ILootTable {
    @Override
    default List<LootPool> getPools() {
        return List.of();
    }
}

package de.teamlapen.factions.common.world.blockentity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SingleItemHandler<T extends BlockEntity> extends MultipleItemHandler<T> {

    public SingleItemHandler(T blockEntity, Function<T, ItemStack> getter, BiConsumer<T, ItemStack> setter, Predicate<ItemResource> validator, int slotLimit, Runnable onChanged) {
        super(blockEntity, onChanged, new SlotProperties<>(getter, setter, validator, slotLimit));
    }
}

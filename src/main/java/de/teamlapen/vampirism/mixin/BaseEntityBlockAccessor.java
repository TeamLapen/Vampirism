package de.teamlapen.vampirism.mixin;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BaseEntityBlock.class)
public interface BaseEntityBlockAccessor {

    @Invoker("createTickerHelper")
    static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> type2, BlockEntityType<E> type1, BlockEntityTicker<? super E> ticker) {
        throw new UnsupportedOperationException();
    }

}

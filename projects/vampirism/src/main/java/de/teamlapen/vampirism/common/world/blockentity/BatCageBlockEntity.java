package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.vampirism.common.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BatCageBlockEntity extends BlockEntity {

    public BatCageBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BAT_CAGE.get(), pPos, pBlockState);
    }
}

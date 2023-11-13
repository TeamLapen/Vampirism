package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BatCageBlockEntity extends BlockEntity {

    public BatCageBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModTiles.BAT_CAGE.get(), pPos, pBlockState);
    }
}

package de.teamlapen.vampirism.blocks.diffuser;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.diffuser.DiffuserBlockEntity;
import de.teamlapen.vampirism.blockentity.diffuser.FogDiffuserBlockEntity;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FogDiffuserBlock extends DiffuserBlock {

    public static final MapCodec<FogDiffuserBlock> CODEC = simpleCodec(FogDiffuserBlock::new);

    public FogDiffuserBlock(Properties properties) {
        super(properties, ModTiles.FOG_DIFFUSER::get);
    }

    @Override
    protected MapCodec<? extends DiffuserBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable DiffuserBlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FogDiffuserBlockEntity(pPos, pState);
    }
}

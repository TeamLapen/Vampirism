package de.teamlapen.vampirism.common.world.blocks.diffuser;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.DiffuserBlockEntity;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.FogDiffuserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FogDiffuserBlock extends DiffuserBlock {

    public static final MapCodec<FogDiffuserBlock> CODEC = simpleCodec(FogDiffuserBlock::new);

    public FogDiffuserBlock(BlockBehaviour.Properties properties) {
        super(properties, ModBlockEntities.FOG_DIFFUSER::get);
    }

    @Override
    protected MapCodec<? extends DiffuserBlock> codec() {
        return CODEC;
    }

    @Override
    protected void onSuccessfullyOpened(BlockState state, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hitResult) {
        player.awardStat(ModStats.INTERACT_WITH_FOG_DIFFUSER.get());
    }

    @Override
    public @Nullable DiffuserBlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FogDiffuserBlockEntity(pPos, pState);
    }
}

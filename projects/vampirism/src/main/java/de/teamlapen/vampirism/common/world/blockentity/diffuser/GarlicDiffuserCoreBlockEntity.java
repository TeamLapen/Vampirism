package de.teamlapen.vampirism.common.world.blockentity.diffuser;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.world.blocks.diffuser.GarlicDiffuserCoreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GarlicDiffuserCoreBlockEntity extends BlockEntity {

    private static final int RADIUS = 3;
    private static final int TICK_INTERVAL = 6;

    public final EnumStrength strength;

    public GarlicDiffuserCoreBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, ((GarlicDiffuserCoreBlock) blockState.getBlock()).strength);
    }

    public GarlicDiffuserCoreBlockEntity(BlockPos pos, BlockState blockState, EnumStrength strength) {
        super(ModBlockEntities.GARLIC_DIFFUSER_CORE.get(), pos, blockState);
        this.strength = strength;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GarlicDiffuserCoreBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        if (level.getGameTime() % TICK_INTERVAL != 0) return;

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos.getX() - RADIUS, pos.getY() - RADIUS, pos.getZ() - RADIUS, pos.getX() + RADIUS, pos.getY() + RADIUS, pos.getZ() + RADIUS));

        for (LivingEntity entity : entities) {
            if (entity.isAlive() && !entity.isSpectator()) {
                DamageHandler.tryAffectEntityGarlic(entity, blockEntity.strength, 10, true);
            }
        }
    }
}

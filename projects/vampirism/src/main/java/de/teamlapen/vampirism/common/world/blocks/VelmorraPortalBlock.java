package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.world.blockentity.VelmorraPortalBlockEntity;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class VelmorraPortalBlock extends BaseEntityBlock implements Portal {

    public static final MapCodec<VelmorraPortalBlock> CODEC = simpleCodec(VelmorraPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);

    public VelmorraPortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VelmorraPortalBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case Z -> Z_AXIS_AABB;
            default -> X_AXIS_AABB;
        };
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return state.getShape(level, pos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean intersects) {
        if (entity.canUsePortal(false)) {
            if (level.dimension() == Level.OVERWORLD) {
                entity.setData(ModAttachments.VELMORRA_PORTAL, GlobalPos.of(level.dimension(), pos));
            }
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public @Nullable TeleportTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        ResourceKey<Level> levelResourceKey = level.dimension() == Level.OVERWORLD ? ModDimensions.VELMORRA_LEVEL : Level.OVERWORLD;
        ServerLevel serverLevel = level.getServer().getLevel(levelResourceKey);
        if (serverLevel == null) {
            return null;
        } else {
            boolean flag = levelResourceKey == ModDimensions.VELMORRA_LEVEL;
            BlockPos spawnPos = flag ? VelmorraDimension.SPAWN_POINT : entity.getExistingData(ModAttachments.VELMORRA_PORTAL).map(GlobalPos::pos).orElseGet(() -> entity instanceof ServerPlayer player  && player.getRespawnConfig() instanceof ServerPlayer.RespawnConfig config ? config.respawnData().pos() : serverLevel.getRespawnData().pos());
            Vec3 floorPos = spawnPos.getBottomCenter();

            float f;
            Set<Relative> set;
            if (flag) {
                EndPlatformFeature.createEndPlatform(level, BlockPos.containing(floorPos).below(), true);
                f = Direction.WEST.toYRot();
                set = Relative.union(Relative.DELTA, Set.of(Relative.X_ROT));
                if (entity instanceof ServerPlayer) {
                    floorPos = floorPos.subtract(0.0, 1.0, 0.0);
                }
            } else {
                f = 0.0F;
                set = Relative.union(Relative.DELTA, Relative.ROTATION);

                Vec3 vec3 = spawnPos.getCenter();
                int i = level.getChunkAt(spawnPos).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos.getX(), spawnPos.getZ()) + 1;
                floorPos =  BlockPos.containing(vec3.x, i, vec3.z).getBottomCenter();
            }
            return new TeleportTransition(serverLevel, floorPos, Vec3.ZERO, f, 0, set, TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET));
        }
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader p_304508_, BlockPos p_53022_, BlockState p_53023_, boolean p_388548_) {
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(BlockState p_389588_) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
}

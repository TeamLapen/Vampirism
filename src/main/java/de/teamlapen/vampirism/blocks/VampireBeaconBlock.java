package de.teamlapen.vampirism.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.blockentity.VampireBeaconBlockEntity;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class VampireBeaconBlock extends VampirismBlockContainer implements BeaconBeamBlock {

    public static final MapCodec<VampireBeaconBlock> CODEC = simpleCodec(VampireBeaconBlock::new);

    public VampireBeaconBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.RED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new VampireBeaconBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.VAMPIRE_BEACON.get(), VampireBeaconBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(ModStats.INTERACT_WITH_ANCIENT_BEACON.get());
                if (Helper.isHunter(serverPlayer)) {
                    if (level.getBlockEntity(pos) instanceof VampireBeaconBlockEntity vampireBeaconBlockEntity) {
                        player.openMenu(vampireBeaconBlockEntity);
                    }
                } else {
                    player.displayClientMessage(Component.translatable("text.vampirism.unfamiliar"), true);
                }
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME) && level.getBlockEntity(pos) instanceof VampireBeaconBlockEntity vampireBeaconBlockEntity) {
            vampireBeaconBlockEntity.setCustomName(stack.getHoverName());
        }
    }
}

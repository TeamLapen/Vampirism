package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.faction.api.world.blocks.IBlockWithDescription;
import de.teamlapen.vampirism.common.world.blockentity.SunscreenBeaconBlockEntity;
import de.teamlapen.faction.common.world.blocks.base.BaseContainerBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SunscreenBeaconBlock extends BaseContainerBlock implements IBlockWithDescription {

    public static final MapCodec<SunscreenBeaconBlock> CODEC = simpleCodec(SunscreenBeaconBlock::new);

    public SunscreenBeaconBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.SUNSCREEN_BEACON.get(), SunscreenBeaconBlockEntity::serverTick);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.@Nullable TooltipContext context, TooltipDisplay display, TooltipFlag tooltipFlag, Consumer<Component> tooltips) {
        tooltips.accept(Component.translatable("tooltip.vampirism.sunscreen_beacon.desc1").withStyle(ChatFormatting.GRAY));
        tooltips.accept(Component.translatable("tooltip.vampirism.sunscreen_beacon.desc2", ModConfig.server().sunscreenBeaconRadius.get()).withStyle(ChatFormatting.GRAY)); //Only add this if a world is present. Otherwise, the config might not be ready as this is also called during search tree population before setup
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return ModConfig.server().sunscreenBeaconMineable.get();
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return ModConfig.server().sunscreenBeaconMineable.get() ? 50 : -1;
    }

    @Override
    public float getExplosionResistance() {
        return ModConfig.server().sunscreenBeaconMineable.get() ? 50 : 3600000;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SunscreenBeaconBlockEntity(pos, state);
    }
}

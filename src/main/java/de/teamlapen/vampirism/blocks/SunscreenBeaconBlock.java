package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.blockentity.SunscreenBeaconBlockEntity;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SunscreenBeaconBlock extends VampirismBlockContainer {

    public SunscreenBeaconBlock() {
        super(Properties.of(Material.METAL).strength(-1, 3600000).noOcclusion());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModTiles.SUNSCREEN_BEACON.get(), SunscreenBeaconBlockEntity::serverTick);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        super.appendHoverText(stack, world, tooltip, advanced);
        tooltip.add(new TranslatableComponent(getDescriptionId() + ".tooltip1").withStyle(ChatFormatting.GRAY));
        if (world != null)
            tooltip.add(new TranslatableComponent(getDescriptionId() + ".tooltip2", VampirismConfig.SERVER.sunscreenBeaconDistance.get()).withStyle(ChatFormatting.GRAY)); //Only add this if a world is present. Otherwise, the config might not be ready as this is also called during search tree population before setup
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        return VampirismConfig.SERVER.sunscreenBeaconMineable.get();
    }

    @Override
    public float getDestroyProgress(@Nonnull BlockState state, @Nonnull Player player, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos) {
        return VampirismConfig.SERVER.sunscreenBeaconMineable.get() ? 50 : -1;
    }

    @Override
    public float getExplosionResistance() {
        return VampirismConfig.SERVER.sunscreenBeaconMineable.get() ? 50 : 3600000;
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new SunscreenBeaconBlockEntity(pos, state);
    }
}

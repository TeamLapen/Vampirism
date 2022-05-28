package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.tileentity.SunscreenBeaconTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class SunscreenBeaconBlock extends VampirismBlockContainer {

    public SunscreenBeaconBlock() {
        super(Properties.of(Material.METAL).strength(-1, 3600000).noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.appendHoverText(stack, world, tooltip, advanced);
        tooltip.add(new TranslationTextComponent(getDescriptionId() + ".tooltip1").withStyle(TextFormatting.GRAY));
        if (world != null)
            tooltip.add(new TranslationTextComponent(getDescriptionId() + ".tooltip2", VampirismConfig.SERVER.sunscreenBeaconDistance.get()).withStyle(TextFormatting.GRAY)); //Only add this if a world is present. Otherwise the config might not be ready as this is also called during search tree population before setup
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return VampirismConfig.SERVER.sunscreenBeaconMineable.get();
    }

    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
        return VampirismConfig.SERVER.sunscreenBeaconMineable.get() ? 50 : -1;
    }

    @Override
    public float getExplosionResistance() {
        return VampirismConfig.SERVER.sunscreenBeaconMineable.get() ? 50 : 3600000;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new SunscreenBeaconTileEntity();
    }

}

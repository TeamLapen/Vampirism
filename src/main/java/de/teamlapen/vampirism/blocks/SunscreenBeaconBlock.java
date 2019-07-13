package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.tileentity.SunscreenBeaconTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;


public class SunscreenBeaconBlock extends VampirismBlockContainer {

    private static final String regName = "sunscreen_beacon";

    public SunscreenBeaconBlock() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(VampirismConfig.SERVER.sunscreenBeaconMineable.get() ? 50 : -1, VampirismConfig.SERVER.sunscreenBeaconMineable.get() ? 50 : 3600000));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(UtilLib.translated(getTranslationKey() + ".tooltip1"));
        tooltip.add(UtilLib.translated(getTranslationKey() + ".tooltip2", VampirismConfig.SERVER.sunscreenBeaconDistance.get()));
    }


    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return VampirismConfig.SERVER.sunscreenBeaconMineable.get();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new SunscreenBeaconTileEntity();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }
}

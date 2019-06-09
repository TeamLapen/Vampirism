package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.tileentity.TileSunscreenBeacon;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;


public class BlockSunscreenBeacon extends VampirismBlockContainer {

    private static final String regName = "sunscreen_beacon";

    public BlockSunscreenBeacon() {
        super(regName, Properties.create(Material.IRON).hardnessAndResistance(Configs.sunscreen_beacon_mineable ? 50 : -1, Configs.sunscreen_beacon_mineable ? 50 : 3600000));
    }




    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(UtilLib.translated(getTranslationKey() + ".tooltip1"));
        tooltip.add(UtilLib.translated(getTranslationKey() + ".tooltip2", Configs.sunscreen_beacon_distance));
    }


    @Override
    public boolean canHarvestBlock(IBlockState state, IBlockReader world, BlockPos pos, EntityPlayer player) {
        return Configs.sunscreen_beacon_mineable;
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileSunscreenBeacon();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }


}

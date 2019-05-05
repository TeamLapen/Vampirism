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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


public class BlockSunscreenBeacon extends VampirismBlockContainer {

    private static final String regName = "sunscreen_beacon";

    public BlockSunscreenBeacon() {
        super(regName, Material.IRON);
        if (Configs.sunscreen_beacon_mineable) {
            this.setHardness(50);
        } else {
            this.setCreativeTab(null);
            this.setBlockUnbreakable();
        }
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(UtilLib.translate(getTranslationKey() + ".tooltip1"));
        tooltip.add(UtilLib.translateFormatted(getTranslationKey() + ".tooltip2", Configs.sunscreen_beacon_distance));
    }


    @Override
    public boolean canHarvestBlock(IBlockReader world, BlockPos pos, EntityPlayer player) {
        return Configs.sunscreen_beacon_mineable;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileSunscreenBeacon();
    }

    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


}

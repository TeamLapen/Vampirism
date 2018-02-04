package de.teamlapen.vampirism.blocks;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.tileentity.TileSunscreenBeacon;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


public class BlockSunscreenBeacon extends VampirismBlockContainer {

    private static final String regName = "sunscreen_beacon";

    public BlockSunscreenBeacon() {
        super(regName, Material.IRON);
        this.setCreativeTab(null);
        if (Configs.sunscreen_beacon_mineable) {
            this.setHardness(50);
        } else {
            this.setBlockUnbreakable();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(UtilLib.translate(getUnlocalizedName() + ".tooltip1"));
        tooltip.add(UtilLib.translateFormatted(getUnlocalizedName() + ".tooltip2", Configs.sunscreen_beacon_distance));
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return Configs.sunscreen_beacon_mineable;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileSunscreenBeacon();
    }

    @SideOnly(Side.CLIENT)
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

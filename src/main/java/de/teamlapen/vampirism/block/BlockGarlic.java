package de.teamlapen.vampirism.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import java.util.ArrayList;
import java.util.Random;

/**
 * Garlic Plant
 * Similar to potatoes, the (dropped) item also is the seed.
 * 7 grow states with 4 different icons
 *
 * @author Maxanier
 */
public class BlockGarlic extends BlockCrops implements IGarlic {


    public static final String name = "garlic";
    private IIcon[] icons;

    public BlockGarlic() {
        this.setTickRandomly(true);
        float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.disableStats();
        this.setBlockTextureName(REFERENCE.MODID + ":" + name);
        this.setBlockName(name);
    }

    @Override
    public String getUnlocalizedName() {
        return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ".", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_) {
        super.updateTick(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, p_149674_5_);

        if (p_149674_1_.getBlockLightValue(p_149674_2_, p_149674_3_ + 1, p_149674_4_) >= 9) {
            int l = p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_);

            if (l < 7) {
                float f = 0.5F;

                if (p_149674_5_.nextInt((int) (25.0F / f) + 1) == 0) {
                    ++l;
                    p_149674_1_.setBlockMetadataWithNotify(p_149674_2_, p_149674_3_, p_149674_4_, l, 2);
                }
            }
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Crop;
    }

    @Override
    public boolean isWeakGarlic() {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta < 7) {
            if (meta == 6) {
                meta = 5;
            }

            return this.icons[meta >> 1];
        } else {
            return this.icons[3];
        }
    }

    @Override
    protected Item func_149866_i() {
        return ModItems.garlic;
    }

    @Override
    protected Item func_149865_P() {
        return ModItems.garlic;
    }


    @Override
    public void func_149853_b(World p_149853_1_, Random p_149853_2_, int p_149853_3_, int p_149853_4_, int p_149853_5_) {
        growFast(p_149853_1_, p_149853_3_, p_149853_4_, p_149853_5_);
    }

    public void growFast(World world, int p_149863_2_, int p_149863_3_, int p_149863_4_) {
        int l = world.getBlockMetadata(p_149863_2_, p_149863_3_, p_149863_4_) + MathHelper.getRandomIntegerInRange(world.rand, 1, 4);

        if (l > 7) {
            l = 7;
        }

        world.setBlockMetadataWithNotify(p_149863_2_, p_149863_3_, p_149863_4_, l, 2);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        this.icons = new IIcon[4];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = p_149651_1_.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return super.getDrops(world, x, y, z, metadata, fortune-1);
    }
}

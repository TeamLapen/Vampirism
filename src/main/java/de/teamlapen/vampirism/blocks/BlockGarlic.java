package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.EnumGarlicStrength;
import de.teamlapen.vampirism.api.IGarlicBlock;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

/**
 * Garlic Plant
 * Similar to potatoes, the (dropped) item also is the seed.
 * 7 grow states with 4 different icons
 *
 * @author Maxanier
 */
public class BlockGarlic extends BlockCrops implements IGarlicBlock {


    public static final String regName = "garlic";

    public BlockGarlic() {
        this.setCreativeTab(null);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return super.getDrops(world, pos, state, fortune - 1);
    }

    @Override
    public EnumGarlicStrength getGarlicStrength(IBlockAccess world, BlockPos pos) {
        return EnumGarlicStrength.WEAK;
    }

    @Override
    protected Item getCrop() {
        return ModItems.itemGarlic;
    }

    @Override
    protected Item getSeed() {
        return ModItems.itemGarlic;
    }
}
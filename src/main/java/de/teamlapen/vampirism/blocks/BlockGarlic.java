package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * Garlic Plant
 * Similar to potatoes, the (dropped) item also is the seed.
 * 7 grow states with 4 different icons
 *
 * @author Maxanier
 */
public class BlockGarlic extends BlockCrops {


    public static final String regName = "garlic";

    public BlockGarlic() {
        this.setCreativeTab(null);
        setRegistryName(REFERENCE.MODID, regName);
        this.setTranslationKey(REFERENCE.MODID + "." + regName);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return super.getDrops(world, pos, state, fortune - 1);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (state.getValue(AGE) > 5 && Helper.isVampire(entityIn)) {
            DamageHandler.affectVampireGarlicDirect(entityIn instanceof EntityPlayer ? VReference.VAMPIRE_FACTION.getPlayerCapability((EntityPlayer) entityIn) : (IVampire) entityIn, EnumStrength.WEAK);
        }
    }

    @Override
    protected Item getCrop() {
        return ModItems.item_garlic;
    }

    @Override
    protected Item getSeed() {
        return ModItems.item_garlic;
    }
}
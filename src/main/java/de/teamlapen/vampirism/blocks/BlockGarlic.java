package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        super(Properties.create(Material.PLANTS));
        setRegistryName(REFERENCE.MODID, regName);
    }

    @Override
    public void getDrops(IBlockState p_getDrops_1_, NonNullList<ItemStack> p_getDrops_2_, World p_getDrops_3_, BlockPos p_getDrops_4_, int fortune) {
        super.getDrops(p_getDrops_1_, p_getDrops_2_, p_getDrops_3_, p_getDrops_4_, fortune - 1);
    }

    @Override
    public void onEntityCollision(IBlockState state, World world, BlockPos pos, Entity entity) {
        if (state.get(AGE) > 5 && Helper.isVampire(entity)) {
            DamageHandler.affectVampireGarlicDirect(entity instanceof EntityPlayer ? VReference.VAMPIRE_FACTION.getPlayerCapability((EntityPlayer) entity) : (IVampire) entity, EnumStrength.WEAK);
        }
    }

    @Override
    protected IItemProvider getCropsItem() {
        return ModItems.item_garlic;
    }

    @Override
    protected IItemProvider getSeedsItem() {
        return ModItems.item_garlic;
    }

}
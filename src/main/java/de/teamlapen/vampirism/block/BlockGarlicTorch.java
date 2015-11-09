package de.teamlapen.vampirism.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Works like a torch, but does not spread light, but garlic gas.
 * Exists in a strong and weak form.
 * Spreads the gas on placing and randomly afterwards.
 *
 * @author Maxanier
 */
public class BlockGarlicTorch extends BlockTorch implements IGarlic {

    private final boolean weakGarlic;

    public BlockGarlicTorch(boolean weakGarlic) {
        this.weakGarlic = weakGarlic;
        setHardness(0.0F);
        setStepSound(soundTypeWood);
        this.setCreativeTab(VampirismMod.tabVampirism);
    }

    @Override
    public boolean isWeakGarlic() {
        return weakGarlic;
    }

    @Override
    public void updateTick(World world, int posX, int posY, int posZ, Random random) {
        super.updateTick(world, posX, posY, posZ, random);
        if (!Configs.disable_garlic_gas) {
            for (int i = 0; i < 9; i++) {
                int x = posX + random.nextInt(11) - 5;
                int y = posY + random.nextInt(7) - 3;
                int z = posZ + random.nextInt(11) - 5;
                makeGarlic(world, x, y, z);
            }
        }

    }

    @Override
    public void onBlockAdded(World world, int posX, int posY, int posZ) {
        super.onBlockAdded(world, posX, posY, posZ);
        if (!Configs.disable_garlic_gas) {
            for (int x = -5; x < 6; x++) {
                for (int y = -3; y < 4; y++) {
                    for (int z = -5; z < 6; z++) {
                        makeGarlic(world, posX + x, posY + y, posZ + z);
                    }
                }
            }
        }

    }

    @Override
    public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_) {
        for (int x = p_149749_2_ - 5; x < p_149749_2_ + 6; x++) {
            for (int y = p_149749_3_ - 4; y < p_149749_3_ + 5; y++) {
                for (int z = p_149749_4_ - 5; z < p_149749_4_ + 6; z++) {
                    if (p_149749_1_.getBlock(x, y, z) instanceof BlockGarlicGas) {
                        p_149749_1_.setBlockToAir(x, y, z);
                    }
                }
            }
        }
        super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
    }

    private void makeGarlic(World world, int x, int y, int z) {
        Block b = world.getBlock(x, y, z);
        if (b.getMaterial() == Material.air && (!(b instanceof IGarlic) || (!weakGarlic && ((IGarlic) b).isWeakGarlic()))) {
            world.setBlock(x, y, z, weakGarlic ? ModBlocks.garlicGasWeak : ModBlocks.garlicGasStrong);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {

    }
}

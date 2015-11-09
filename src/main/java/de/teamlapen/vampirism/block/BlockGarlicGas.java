package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockAir;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Garlic gas.
 * Should act very similar to air.
 * Used to simulate the garlic area of effect.
 * Disappears slowly.
 * Can exist either represent weak or strong garlic.
 *
 * @author Maxanier
 */
public class BlockGarlicGas extends BlockAir implements IGarlic {

    private final boolean weakGarlic;
    public final static String name = "garlicGas";

    public BlockGarlicGas(boolean weakGarlic) {
        this.weakGarlic = weakGarlic;
        this.setBlockName(REFERENCE.MODID + "." + name);
        this.setTickRandomly(true);
        this.setBlockTextureName(REFERENCE.MODID + ":invisible");
    }

    @Override
    public boolean isWeakGarlic() {
        return weakGarlic;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (Configs.disable_garlic_gas || random.nextInt(3) == 0) {
            world.setBlockToAir(x, y, z);
        }
    }
}

/*
 * Copyright (c) 2016. @maxanier
 * Published under LGPLv3
 *
 */

package de.teamlapen.vampirism.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;


public class BlockCastleBlock extends VampirismBlockString {
    public static final String[] types = {"darkBrick", "purpleBrick", "darkBrickBloody"};
    private static final String name = "castleBlock";

    public BlockCastleBlock() {
        super(name, Material.rock, types);
        this.setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypePiston);
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(getStringProp()).equals("darkBrickBloody")) {
            if (rand.nextInt(180) == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), "vampirism:ambient.castle", 0.8F, 1.0F, false);
            }

        }
    }
}

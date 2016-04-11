/*
 * Copyright (c) 2016. @maxanier
 * Published under LGPLv3
 *
 */

package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;


public class BlockCastleBlock extends VampirismBlockString {
    public static final String[] types = {"dark_brick", "purple_brick", "dark_brick_bloody"};
    private static final String name = "castleBlock";

    public BlockCastleBlock() {
        super(name, Material.rock, types);
        this.setHardness(2.0F);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(getStringProp()).equals("dark_brick_bloody")) {
            if (rand.nextInt(180) == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.ambient_castle, SoundCategory.AMBIENT, 0.8F, 1.0F, false);
            }

        }
    }


}

package de.teamlapen.lib.lib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;

public class RegisterHelper {

    public static <T extends Block> T flammable(T block, int encouragement, int flammability) {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        fireblock.setFlammable(block, encouragement, flammability);
        return block;
    }

    public static <T extends Block> T potted(T potBlock, ResourceLocation plant) {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        pot.addPlant(plant, () -> potBlock);
        return potBlock;
    }
}

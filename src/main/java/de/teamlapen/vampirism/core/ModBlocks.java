package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.blocks.BlockFluidBlood;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Handles all block registrations and reference.
 */
public class ModBlocks {
    public static BlockFluidBlood fluidBlood;


    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerBlocks();
                break;
        }

    }

    private static void registerBlocks() {
        fluidBlood = registerBlock(new BlockFluidBlood(), null);
    }

    private static <T extends Block> T registerBlock(T block, Class<? extends ItemBlock> itemclass) {
        if (block.getRegistryName() == null) {
            throw new IllegalArgumentException("Missing registry name for " + block);
        }
        GameRegistry.registerBlock(block);
        return block;
    }



}

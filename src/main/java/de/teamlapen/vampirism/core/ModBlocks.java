package de.teamlapen.vampirism.core;

import de.teamlapen.lib.item.ItemMetaBlock;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.blocks.BlockCastleBlock;
import de.teamlapen.vampirism.blocks.BlockCursedEarth;
import de.teamlapen.vampirism.blocks.BlockFluidBlood;
import de.teamlapen.vampirism.blocks.VampirismFlower;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Handles all block registrations and reference.
 */
public class ModBlocks {
    public static BlockFluidBlood fluidBlood;
    public static BlockCastleBlock castleBlock;
    public static BlockCursedEarth cursedEarth;
    public static VampirismFlower vampirismFlower;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerBlocks();
                break;
        }

    }

    private static void registerBlocks() {
        fluidBlood = registerBlock(new BlockFluidBlood());//TODO Maybe remove blood block later
        castleBlock = registerBlock(new BlockCastleBlock(), ItemMetaBlock.class);
        cursedEarth = registerBlock(new BlockCursedEarth());
        vampirismFlower = registerBlock(new VampirismFlower(), ItemMetaBlock.class);

    }

    private static <T extends Block> T registerBlock(T block) {
        return registerBlock(block, ItemBlock.class);
    }
    private static <T extends Block> T registerBlock(T block, Class<? extends ItemBlock> itemclass) {
        if (block.getRegistryName() == null) {
            throw new IllegalArgumentException("Missing registry name for " + block);
        }
        GameRegistry.registerBlock(block, itemclass);
        return block;
    }


}

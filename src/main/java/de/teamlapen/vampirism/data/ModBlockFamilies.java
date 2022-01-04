package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;

public class ModBlockFamilies {
    public static final BlockFamily DARK_SPRUCE_PLANKS = BlockFamilies.familyBuilder(ModBlocks.DARK_SPRUCE_PLANKS.get()).button(ModBlocks.DARK_SPRUCE_BUTTON.get()).fence(ModBlocks.DARK_SPRUCE_FENCE.get()).fenceGate(ModBlocks.DARK_SPRUCE_FENCE_GATE.get()).pressurePlate(ModBlocks.DARK_SPRUCE_PRESSURE_PLACE.get()).sign(ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get()).slab(ModBlocks.DARK_SPRUCE_SLAB.get()).stairs(ModBlocks.DARK_SPRUCE_STAIRS.get()).door(ModBlocks.DARK_SPRUCE_DOOR.get()).trapdoor(ModBlocks.DARK_SPRUCE_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily CURSED_SPRUCE_PLANKS = BlockFamilies.familyBuilder(ModBlocks.CURSED_SPRUCE_PLANKS.get()).button(ModBlocks.CURSED_SPRUCE_BUTTON.get()).fence(ModBlocks.CURSED_SPRUCE_FENCE.get()).fenceGate(ModBlocks.CURSED_SPRUCE_FENCE_GATE.get()).pressurePlate(ModBlocks.CURSED_SPRUCE_PRESSURE_PLACE.get()).sign(ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get()).slab(ModBlocks.CURSED_SPRUCE_SLAB.get()).stairs(ModBlocks.CURSED_SPRUCE_STAIRS.get()).door(ModBlocks.CURSED_SPRUCE_DOOR.get()).trapdoor(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();

    public static void init() {
    }
}

package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.mixin.BlockFamiliesAccessor;
import net.minecraft.data.BlockFamily;

@SuppressWarnings("unused")
public class ModBlockFamilies {
    public static final BlockFamily DARK_SPRUCE_PLANKS = BlockFamiliesAccessor.familyBuilder(ModBlocks.DARK_SPRUCE_PLANKS.get()).button(ModBlocks.DARK_SPRUCE_BUTTON.get()).fence(ModBlocks.DARK_SPRUCE_FENCE.get()).fenceGate(ModBlocks.DARK_SPRUCE_FENCE_GATE.get()).pressurePlate(ModBlocks.DARK_SPRUCE_PRESSURE_PLACE.get()).sign(ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get()).slab(ModBlocks.DARK_SPRUCE_SLAB.get()).stairs(ModBlocks.DARK_SPRUCE_STAIRS.get()).door(ModBlocks.DARK_SPRUCE_DOOR.get()).trapdoor(ModBlocks.DARK_SPRUCE_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily CURSED_SPRUCE_PLANKS = BlockFamiliesAccessor.familyBuilder(ModBlocks.CURSED_SPRUCE_PLANKS.get()).button(ModBlocks.CURSED_SPRUCE_BUTTON.get()).fence(ModBlocks.CURSED_SPRUCE_FENCE.get()).fenceGate(ModBlocks.CURSED_SPRUCE_FENCE_GATE.get()).pressurePlate(ModBlocks.CURSED_SPRUCE_PRESSURE_PLACE.get()).sign(ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get()).slab(ModBlocks.CURSED_SPRUCE_SLAB.get()).stairs(ModBlocks.CURSED_SPRUCE_STAIRS.get()).door(ModBlocks.CURSED_SPRUCE_DOOR.get()).trapdoor(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get()).recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks").getFamily();
    public static final BlockFamily DARK_STONE_BRICKS = BlockFamiliesAccessor.familyBuilder(ModBlocks.DARK_STONE_BRICKS.get()).wall(ModBlocks.DARK_STONE_BRICK_WALL.get()).stairs(ModBlocks.DARK_STONE_BRICK_STAIRS.get()).slab(ModBlocks.DARK_STONE_BRICK_SLAB.get()).cracked(ModBlocks.CRACKED_DARK_STONE_BRICKS.get()).chiseled(ModBlocks.CHISELED_DARK_STONE_BRICKS.get()).getFamily();
    public static final BlockFamily DARK_STONE = BlockFamiliesAccessor.familyBuilder(ModBlocks.DARK_STONE.get()).stairs(ModBlocks.DARK_STONE_STAIRS.get()).slab(ModBlocks.DARK_STONE_SLAB.get()).wall(ModBlocks.DARK_STONE_WALL.get()).getFamily();
    public static final BlockFamily COBBLED_DARK_STONE = BlockFamiliesAccessor.familyBuilder(ModBlocks.COBBLED_DARK_STONE.get()).stairs(ModBlocks.COBBLED_DARK_STONE_STAIRS.get()).slab(ModBlocks.COBBLED_DARK_STONE_SLAB.get()).polished(ModBlocks.POLISHED_DARK_STONE.get()).wall(ModBlocks.COBBLED_DARK_STONE_WALL.get()).getFamily();
    public static final BlockFamily POLISHED_DARK_STONE = BlockFamiliesAccessor.familyBuilder(ModBlocks.POLISHED_DARK_STONE.get()).stairs(ModBlocks.POLISHED_DARK_STONE_STAIRS.get()).slab(ModBlocks.POLISHED_DARK_STONE_SLAB.get()).wall(ModBlocks.POLISHED_DARK_STONE_WALL.get()).getFamily();
    public static final BlockFamily DARK_STONE_TILES = BlockFamiliesAccessor.familyBuilder(ModBlocks.DARK_STONE_TILES.get()).stairs(ModBlocks.DARK_STONE_TILES_STAIRS.get()).slab(ModBlocks.DARK_STONE_TILES_SLAB.get()).wall(ModBlocks.DARK_STONE_TILES_WALL.get()).cracked(ModBlocks.CRACKED_DARK_STONE_TILES.get()).getFamily();
    public static final BlockFamily PURPLE_BRICKS = BlockFamiliesAccessor.familyBuilder(ModBlocks.PURPLE_STONE_BRICKS.get()).wall(ModBlocks.PURPLE_STONE_BRICK_WALL.get()).stairs(ModBlocks.PURPLE_STONE_BRICK_STAIRS.get()).slab(ModBlocks.PURPLE_STONE_BRICK_SLAB.get()).getFamily();
    public static final BlockFamily PURPLE_STONE_TILES = BlockFamiliesAccessor.familyBuilder(ModBlocks.PURPLE_STONE_TILES.get()).wall(ModBlocks.PURPLE_STONE_TILES_WALL.get()).stairs(ModBlocks.PURPLE_STONE_TILES_STAIRS.get()).slab(ModBlocks.PURPLE_STONE_TILES_SLAB.get()).getFamily();


    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }
}

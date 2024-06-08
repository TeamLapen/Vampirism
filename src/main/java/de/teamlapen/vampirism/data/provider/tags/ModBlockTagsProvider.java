package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.tags.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return REFERENCE.MODID + " " + super.getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider holderLookup) {
        tag(BlockTags.DIRT).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS.get());
        tag(ModBlockTags.CURSED_EARTH).add(ModBlocks.CURSED_EARTH.get(), ModBlocks.CURSED_GRASS.get());
        tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_VAMPIRE_ORCHID.get());
        tag(ModBlockTags.REMAINS).add(ModBlocks.ACTIVE_VULNERABLE_REMAINS.get(), ModBlocks.VULNERABLE_REMAINS.get(), ModBlocks.REMAINS.get(), ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get());
        tag(ModBlockTags.ACTIVE_REMAINS).add(ModBlocks.ACTIVE_VULNERABLE_REMAINS.get(), ModBlocks.VULNERABLE_REMAINS.get());
        tag(ModBlockTags.VULNERABLE_REMAINS).addTag(ModBlockTags.ACTIVE_REMAINS).add(ModBlocks.INCAPACITATED_VULNERABLE_REMAINS.get());
        // Tool Types
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.CURSED_EARTH.get())
                .add(ModBlocks.CURSED_GRASS.get())
                .add(ModBlocks.CURSED_EARTH_PATH.get())
        ;
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.ALTAR_PILLAR.get())
                .add(ModBlocks.ALTAR_TIP.get())
                .add(ModBlocks.TOTEM_BASE.get())
                .add(ModBlocks.BLOOD_PEDESTAL.get())
                .add(ModBlocks.ALTAR_INFUSION.get())
                .add(ModBlocks.WEAPON_TABLE.get())
                .add(ModBlocks.GRAVE_CAGE.get())
                .add(ModBlocks.TOMBSTONE1.get())
                .add(ModBlocks.TOMBSTONE2.get())
                .add(ModBlocks.TOMBSTONE3.get())
                .add(ModBlocks.BLOOD_GRINDER.get())
                .add(ModBlocks.FIRE_PLACE.get())
                .add(ModBlocks.GRAVE_CAGE.get())
                .add(ModBlocks.ALCHEMICAL_CAULDRON.get())
                .add(ModBlocks.MED_CHAIR.get())
                .add(ModBlocks.GARLIC_DIFFUSER_WEAK.get())
                .add(ModBlocks.GARLIC_DIFFUSER_NORMAL.get())
                .add(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get())
                .add(ModBlocks.FOG_DIFFUSER.get())
                .add(ModBlocks.CHANDELIER.get())
                .add(ModBlocks.CANDELABRA.get())
                .add(ModBlocks.CANDELABRA_WALL.get())
                .add(ModBlocks.ALCHEMY_TABLE.get())
                .add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get())
                .add(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get())
                .addTag(ModBlockTags.TOTEM_TOP)
                .addTag(ModBlockTags.DARK_STONE)
                .addTag(ModBlockTags.DARK_STONE_BRICKS)
                .addTag(ModBlockTags.DARK_STONE_TILES)
                .addTag(ModBlockTags.POLISHED_DARK_STONE)
                .addTag(ModBlockTags.COBBLED_DARK_STONE)
                .add(ModBlocks.VAMPIRE_SOUL_LANTERN.getKey())
        ;
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.ALTAR_INSPIRATION.get())
                .add(ModBlocks.HUNTER_TABLE.get())
                .add(ModBlocks.BLOOD_SIEVE.get())
                .add(ModBlocks.ALTAR_CLEANSING.get())
                .add(ModBlocks.DARK_SPRUCE_SIGN.get())
                .add(ModBlocks.DARK_SPRUCE_WALL_SIGN.get())
                .add(ModBlocks.CURSED_SPRUCE_SIGN.get())
                .add(ModBlocks.CURSED_SPRUCE_WALL_SIGN.get())
                .add(ModBlocks.BLOOD_CONTAINER.get())
                .add(ModBlocks.POTION_TABLE.get())
                .add(ModBlocks.CROSS.get())
                .add(ModBlocks.DARK_SPRUCE_DOOR.get())
                .add(ModBlocks.CURSED_SPRUCE_DOOR.get())
                .add(ModBlocks.DARK_SPRUCE_TRAPDOOR.get())
                .add(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get())
                .add(ModBlocks.DARK_SPRUCE_FENCE_GATE.get())
                .add(ModBlocks.CURSED_SPRUCE_FENCE_GATE.get())
                .add(ModBlocks.VAMPIRE_RACK.get())
                .add(ModBlocks.THRONE.get())
                .addTag(ModBlockTags.COFFIN)
        ;

        // Tool Tiers
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.ALTAR_TIP.get())
                .add(ModBlocks.GRAVE_CAGE.get())
                .add(ModBlocks.MED_CHAIR.get())
                .add(ModBlocks.MED_CHAIR.get())
                .add(ModBlocks.CHANDELIER.get())
        ;
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.BLOOD_PEDESTAL.get())
                .add(ModBlocks.BLOOD_GRINDER.get())
                .add(ModBlocks.WEAPON_TABLE.get())
                .add(ModBlocks.ALTAR_INFUSION.get())
                .add(ModBlocks.ALCHEMICAL_CAULDRON.get())
                .add(ModBlocks.CANDELABRA.get())
                .add(ModBlocks.CANDELABRA_WALL.get())
                .add(ModBlocks.ALCHEMY_TABLE.get())
                .add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get())
                .add(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get())
        ;
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.TOTEM_BASE.get())
                .add(ModBlocks.ALTAR_INFUSION.get())
                .add(ModBlocks.GARLIC_DIFFUSER_WEAK.get())
                .add(ModBlocks.GARLIC_DIFFUSER_NORMAL.get())
                .add(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get())
                .add(ModBlocks.FOG_DIFFUSER.get())
                .addTag(ModBlockTags.TOTEM_TOP_CRAFTED)
        ;

        tag(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.CURSED_HANGING_ROOTS.get());
        tag(ModBlockTags.DARK_SPRUCE_LOG).add(ModBlocks.DARK_SPRUCE_LOG.get(), ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get(), ModBlocks.DARK_SPRUCE_WOOD.get(), ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get());
        tag(ModBlockTags.CURSED_SPRUCE_LOG).add(ModBlocks.CURSED_SPRUCE_LOG.get(), ModBlocks.CURSED_SPRUCE_LOG_CURED.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get(), ModBlocks.CURSED_SPRUCE_WOOD.get(), ModBlocks.CURSED_SPRUCE_WOOD_CURED.get(), ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get());
        tag(BlockTags.LEAVES).add(ModBlocks.DARK_SPRUCE_LEAVES.get());
        tag(BlockTags.SAPLINGS).add(ModBlocks.DARK_SPRUCE_SAPLING.get());
        tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.DARK_SPRUCE_TRAPDOOR.get(), ModBlocks.CURSED_SPRUCE_TRAPDOOR.get());
        tag(BlockTags.WOODEN_DOORS).add(ModBlocks.DARK_SPRUCE_DOOR.get(), ModBlocks.CURSED_SPRUCE_DOOR.get());
        tag(BlockTags.PLANKS).add(ModBlocks.DARK_SPRUCE_PLANKS.get(), ModBlocks.CURSED_SPRUCE_PLANKS.get());
        tag(BlockTags.WOODEN_BUTTONS).add(ModBlocks.DARK_SPRUCE_BUTTON.get(), ModBlocks.CURSED_SPRUCE_BUTTON.get());
        tag(BlockTags.WOODEN_STAIRS).add(ModBlocks.DARK_SPRUCE_STAIRS.get(), ModBlocks.CURSED_SPRUCE_STAIRS.get());
        tag(BlockTags.WOODEN_SLABS).add(ModBlocks.DARK_SPRUCE_SLAB.get(), ModBlocks.CURSED_SPRUCE_SLAB.get());
        tag(BlockTags.WOODEN_FENCES).add(ModBlocks.DARK_SPRUCE_FENCE.get(), ModBlocks.CURSED_SPRUCE_FENCE.get());
        tag(BlockTags.LOGS_THAT_BURN).addTags(ModBlockTags.CURSED_SPRUCE_LOG, ModBlockTags.DARK_SPRUCE_LOG);
        tag(BlockTags.LOGS).addTags(ModBlockTags.CURSED_SPRUCE_LOG, ModBlockTags.DARK_SPRUCE_LOG);
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.DARK_SPRUCE_PRESSURE_PLACE.get(), ModBlocks.CURSED_SPRUCE_PRESSURE_PLACE.get());
        tag(BlockTags.WOODEN_DOORS).add(ModBlocks.DARK_SPRUCE_DOOR.get(), ModBlocks.CURSED_SPRUCE_DOOR.get());
        tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.DARK_SPRUCE_TRAPDOOR.get(), ModBlocks.CURSED_SPRUCE_TRAPDOOR.get());
        tag(ModBlockTags.TOTEM_TOP_FRAGILE).add(ModBlocks.TOTEM_TOP.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get());
        tag(ModBlockTags.TOTEM_TOP_CRAFTED).add(ModBlocks.TOTEM_TOP_CRAFTED.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get());
        tag(ModBlockTags.TOTEM_TOP).addTag(ModBlockTags.TOTEM_TOP_FRAGILE).addTag(ModBlockTags.TOTEM_TOP_CRAFTED);
        tag(ModBlockTags.COFFIN).add(ModBlocks.COFFIN_RED.get()).add(ModBlocks.COFFIN_BLUE.get()).add(ModBlocks.COFFIN_GREEN.get()).add(ModBlocks.COFFIN_BROWN.get()).add(ModBlocks.COFFIN_BLACK.get()).add(ModBlocks.COFFIN_GRAY.get()).add(ModBlocks.COFFIN_LIGHT_BLUE.get()).add(ModBlocks.COFFIN_WHITE.get()).add(ModBlocks.COFFIN_LIGHT_GRAY.get()).add(ModBlocks.COFFIN_CYAN.get()).add(ModBlocks.COFFIN_PURPLE.get()).add(ModBlocks.COFFIN_PINK.get()).add(ModBlocks.COFFIN_LIME.get()).add(ModBlocks.COFFIN_YELLOW.get()).add(ModBlocks.COFFIN_ORANGE.get()).add(ModBlocks.COFFIN_MAGENTA.get());
        tag(BlockTags.CEILING_HANGING_SIGNS).add(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get());
        tag(BlockTags.WALL_HANGING_SIGNS).add(ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get());
        tag(ModBlockTags.DARK_STONE).add(ModBlocks.DARK_STONE.get(), ModBlocks.DARK_STONE_STAIRS.get(), ModBlocks.DARK_STONE_WALL.get(), ModBlocks.DARK_STONE_SLAB.get(), ModBlocks.INFESTED_DARK_STONE.get());
        tag(ModBlockTags.DARK_STONE_BRICKS).add(ModBlocks.DARK_STONE_BRICKS.get(), ModBlocks.DARK_STONE_BRICK_WALL.get(), ModBlocks.DARK_STONE_BRICK_SLAB.get(), ModBlocks.DARK_STONE_BRICK_STAIRS.get(), ModBlocks.CHISELED_DARK_STONE_BRICKS.get(), ModBlocks.BLOODY_DARK_STONE_BRICKS.get(), ModBlocks.CRACKED_DARK_STONE_BRICKS.get());
        tag(ModBlockTags.PURPLE_STONE_BRICKS).add(ModBlocks.PURPLE_STONE_BRICKS.get(), ModBlocks.PURPLE_STONE_BRICK_WALL.get(), ModBlocks.PURPLE_STONE_BRICK_SLAB.get(), ModBlocks.PURPLE_STONE_BRICK_STAIRS.get());
        tag(ModBlockTags.POLISHED_DARK_STONE).add(ModBlocks.POLISHED_DARK_STONE.get(), ModBlocks.POLISHED_DARK_STONE_STAIRS.get(), ModBlocks.POLISHED_DARK_STONE_SLAB.get(), ModBlocks.POLISHED_DARK_STONE_WALL.get());
        tag(ModBlockTags.COBBLED_DARK_STONE).add(ModBlocks.COBBLED_DARK_STONE.get(), ModBlocks.COBBLED_DARK_STONE_STAIRS.get(), ModBlocks.COBBLED_DARK_STONE_SLAB.get(), ModBlocks.POLISHED_DARK_STONE_WALL.get());
        tag(ModBlockTags.DARK_STONE_TILES).add(ModBlocks.DARK_STONE_TILES.get(), ModBlocks.DARK_STONE_TILES_STAIRS.get(), ModBlocks.DARK_STONE_TILES_SLAB.get(), ModBlocks.DARK_STONE_TILES_WALL.get(), ModBlocks.CRACKED_DARK_STONE_TILES.get());
        tag(ModBlockTags.PURPLE_STONE_TILES).add(ModBlocks.PURPLE_STONE_TILES.get(), ModBlocks.PURPLE_STONE_TILES_STAIRS.get(), ModBlocks.PURPLE_STONE_TILES_SLAB.get(), ModBlocks.PURPLE_STONE_TILES_WALL.get());
        tag(ModBlockTags.NO_SPAWN).addTag(ModBlockTags.DARK_STONE);
        tag(ModBlockTags.VAMPIRE_SPAWN).addTags(ModBlockTags.DARK_STONE_BRICKS, ModBlockTags.COBBLED_DARK_STONE, ModBlockTags.POLISHED_DARK_STONE, ModBlockTags.DARK_STONE_TILES);
        tag(Tags.Blocks.STONES).add(ModBlocks.DARK_STONE.get(), ModBlocks.INFESTED_DARK_STONE.get(), ModBlocks.POLISHED_DARK_STONE.get());
        tag(BlockTags.WALLS).add(ModBlocks.DARK_STONE_BRICK_WALL.get(), ModBlocks.POLISHED_DARK_STONE_WALL.get(), ModBlocks.COBBLED_DARK_STONE_WALL.get(), ModBlocks.DARK_STONE_WALL.get(), ModBlocks.DARK_STONE_TILES_WALL.get(), ModBlocks.PURPLE_STONE_BRICK_WALL.get(), ModBlocks.PURPLE_STONE_TILES_WALL.get());
        tag(BlockTags.STAIRS).add(ModBlocks.DARK_STONE_BRICK_STAIRS.get(), ModBlocks.POLISHED_DARK_STONE_STAIRS.get(), ModBlocks.COBBLED_DARK_STONE_STAIRS.get(), ModBlocks.DARK_STONE_STAIRS.get(), ModBlocks.DARK_STONE_TILES_STAIRS.get(), ModBlocks.PURPLE_STONE_BRICK_STAIRS.get(), ModBlocks.PURPLE_STONE_TILES_STAIRS.get());
        tag(BlockTags.SLABS).add(ModBlocks.DARK_STONE_BRICK_SLAB.get(), ModBlocks.POLISHED_DARK_STONE_SLAB.get(), ModBlocks.COBBLED_DARK_STONE_SLAB.get(), ModBlocks.DARK_STONE_SLAB.get(), ModBlocks.DARK_STONE_TILES_SLAB.get(), ModBlocks.PURPLE_STONE_BRICK_SLAB.get(), ModBlocks.PURPLE_STONE_TILES_SLAB.get());
        tag(ModBlockTags.MOTHER_GROWS_ON).addTag(BlockTags.DIRT);
        tag(Tags.Blocks.STORAGE_BLOCKS).add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get(), ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get());
        tag(ModBlockTags.VAMPIRE_BEACON_BASE_BLOCKS).add(ModBlocks.BLOOD_INFUSED_IRON_BLOCK.get(), ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get());
        tag(ModBlockTags.VAMPIRE_BEACON_BASE_ENHANCED_BLOCKS).add(ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK.get());
        tag(BlockTags.CANDLES).add(ModBlocks.CANDLE_STICK_NORMAL.get(), ModBlocks.WALL_CANDLE_STICK_NORMAL.get(), ModBlocks.CANDLE_STICK_WHITE.get(), ModBlocks.WALL_CANDLE_STICK_WHITE.get(), ModBlocks.CANDLE_STICK_ORANGE.get(), ModBlocks.WALL_CANDLE_STICK_ORANGE.get(), ModBlocks.CANDLE_STICK_MAGENTA.get(), ModBlocks.WALL_CANDLE_STICK_MAGENTA.get(), ModBlocks.CANDLE_STICK_LIGHT_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_BLUE.get(), ModBlocks.CANDLE_STICK_YELLOW.get(), ModBlocks.WALL_CANDLE_STICK_YELLOW.get(), ModBlocks.CANDLE_STICK_LIME.get(), ModBlocks.WALL_CANDLE_STICK_LIME.get(), ModBlocks.CANDLE_STICK_PINK.get(), ModBlocks.WALL_CANDLE_STICK_PINK.get(), ModBlocks.CANDLE_STICK_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_GRAY.get(), ModBlocks.CANDLE_STICK_LIGHT_GRAY.get(), ModBlocks.WALL_CANDLE_STICK_LIGHT_GRAY.get(), ModBlocks.CANDLE_STICK_CYAN.get(), ModBlocks.WALL_CANDLE_STICK_CYAN.get(), ModBlocks.CANDLE_STICK_PURPLE.get(), ModBlocks.WALL_CANDLE_STICK_PURPLE.get(), ModBlocks.CANDLE_STICK_BLUE.get(), ModBlocks.WALL_CANDLE_STICK_BLUE.get(), ModBlocks.CANDLE_STICK_BROWN.get(), ModBlocks.WALL_CANDLE_STICK_BROWN.get(), ModBlocks.CANDLE_STICK_GREEN.get(), ModBlocks.WALL_CANDLE_STICK_GREEN.get(), ModBlocks.CANDLE_STICK_RED.get(), ModBlocks.WALL_CANDLE_STICK_RED.get(), ModBlocks.CANDLE_STICK_BLACK.get(), ModBlocks.WALL_CANDLE_STICK_BLACK.get());
        tag(ModBlockTags.CREEPER_REPELLENT).add(ModBlocks.VAMPIRE_SOUL_LANTERN.get());
    }
}

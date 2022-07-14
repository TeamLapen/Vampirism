package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.items.CoffinItem;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Handles all block registrations and reference.
 */
@SuppressWarnings("unused")
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, REFERENCE.MODID);

    //Blocks
    public static final RegistryObject<AlchemicalCauldronBlock> ALCHEMICAL_CAULDRON = registerWithItem("alchemical_cauldron", AlchemicalCauldronBlock::new);
    public static final RegistryObject<AlchemicalFireBlock> ALCHEMICAL_FIRE = BLOCKS.register("alchemical_fire", AlchemicalFireBlock::new);
    public static final RegistryObject<AltarInfusionBlock> ALTAR_INFUSION = registerWithItem("altar_infusion", AltarInfusionBlock::new);
    public static final RegistryObject<AltarInspirationBlock> ALTAR_INSPIRATION = registerWithItem("altar_inspiration", AltarInspirationBlock::new);
    public static final RegistryObject<AltarPillarBlock> ALTAR_PILLAR = registerWithItem("altar_pillar", AltarPillarBlock::new);
    public static final RegistryObject<AltarTipBlock> ALTAR_TIP = registerWithItem("altar_tip", AltarTipBlock::new);
    public static final RegistryObject<BloodContainerBlock> BLOOD_CONTAINER = registerWithItem("blood_container", BloodContainerBlock::new, new Item.Properties().tab(VampirismMod.creativeTab).stacksTo(1));
    public static final RegistryObject<GrinderBlock> BLOOD_GRINDER = registerWithItem("blood_grinder", GrinderBlock::new);
    public static final RegistryObject<PedestalBlock> BLOOD_PEDESTAL = registerWithItem("blood_pedestal", PedestalBlock::new);
    public static final RegistryObject<SieveBlock> BLOOD_SIEVE = registerWithItem("blood_sieve", SieveBlock::new);
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_BRICK = registerWithItem("castle_block_dark_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_BRICK_BLOODY = registerWithItem("castle_block_dark_brick_bloody", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK_BLOODY));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_STONE = registerWithItem("castle_block_dark_stone", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_NORMAL_BRICK = registerWithItem("castle_block_normal_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.NORMAL_BRICK));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_PURPLE_BRICK = registerWithItem("castle_block_purple_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_DARK_BRICK = registerWithItem("castle_slab_dark_brick", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_DARK_STONE = registerWithItem("castle_slab_dark_stone", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_PURPLE_BRICK = registerWithItem("castle_slab_purple_brick", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_DARK_BRICK = registerWithItem("castle_stairs_dark_brick", () -> new CastleStairsBlock(() -> CASTLE_BLOCK_DARK_BRICK.get().defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_DARK_STONE = registerWithItem("castle_stairs_dark_stone", () -> new CastleStairsBlock(() -> CASTLE_BLOCK_DARK_STONE.get().defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_PURPLE_BRICK = registerWithItem("castle_stairs_purple_brick", () -> new CastleStairsBlock(() -> CASTLE_BLOCK_PURPLE_BRICK.get().defaultBlockState(), CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<AltarCleansingBlock> ALTAR_CLEANSING = registerWithItem("altar_cleansing", AltarCleansingBlock::new);
    public static final RegistryObject<CoffinBlock> COFFIN = registerWithItem("coffin", CoffinBlock::new, CoffinItem::new);
    public static final RegistryObject<CursedEarthBlock> CURSED_EARTH = registerWithItem("cursed_earth", CursedEarthBlock::new);
    public static final RegistryObject<FirePlaceBlock> FIRE_PLACE = registerWithItem("fire_place", FirePlaceBlock::new);
    public static final RegistryObject<GarlicBlock> GARLIC = BLOCKS.register("garlic", GarlicBlock::new);
    public static final RegistryObject<GarlicDiffuserBlock> GARLIC_DIFFUSER_IMPROVED = registerWithItem("garlic_diffuser_improved", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.IMPROVED));
    public static final RegistryObject<GarlicDiffuserBlock> GARLIC_DIFFUSER_NORMAL = registerWithItem("garlic_diffuser_normal", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.NORMAL));
    public static final RegistryObject<GarlicDiffuserBlock> GARLIC_DIFFUSER_WEAK = registerWithItem("garlic_diffuser_weak", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.WEAK));
    public static final RegistryObject<HunterTableBlock> HUNTER_TABLE = registerWithItem("hunter_table", HunterTableBlock::new);
    public static final RegistryObject<MedChairBlock> MED_CHAIR = BLOCKS.register("med_chair", MedChairBlock::new);
    public static final RegistryObject<SunscreenBeaconBlock> SUNSCREEN_BEACON = registerWithItem("sunscreen_beacon", SunscreenBeaconBlock::new);
    public static final RegistryObject<TentBlock> TENT = BLOCKS.register("tent", TentBlock::new);
    public static final RegistryObject<TentMainBlock> TENT_MAIN = BLOCKS.register("tent_main", TentMainBlock::new);
    public static final RegistryObject<TotemBaseBlock> TOTEM_BASE = registerWithItem("totem_base", TotemBaseBlock::new);
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP = registerWithItem("totem_top", () -> new TotemTopBlock(false, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE = registerWithItem("totem_top_vampirism_vampire", () -> new TotemTopBlock(false, REFERENCE.VAMPIRE_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER = registerWithItem("totem_top_vampirism_hunter", () -> new TotemTopBlock(false, REFERENCE.HUNTER_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_CRAFTED = registerWithItem("totem_top_crafted", () -> new TotemTopBlock(true, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED = BLOCKS.register("totem_top_vampirism_vampire_crafted", () -> new TotemTopBlock(true, REFERENCE.VAMPIRE_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED = BLOCKS.register("totem_top_vampirism_hunter_crafted", () -> new TotemTopBlock(true, REFERENCE.HUNTER_PLAYER_KEY));
    public static final RegistryObject<VampirismFlowerBlock> VAMPIRE_ORCHID = registerWithItem("vampire_orchid", () -> new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID));
    public static final RegistryObject<FlowerPotBlock> POTTED_VAMPIRE_ORCHID = BLOCKS.register("potted_vampire_orchid", () -> {
                FlowerPotBlock block = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, VAMPIRE_ORCHID, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion());
                ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(VAMPIRE_ORCHID.getId(), () -> block);
                return block;
            });
    public static final RegistryObject<WeaponTableBlock> WEAPON_TABLE = registerWithItem("weapon_table", WeaponTableBlock::new);
    public static final RegistryObject<PotionTableBlock> POTION_TABLE = registerWithItem("potion_table", PotionTableBlock::new);
    public static final RegistryObject<RotatedPillarBlock> BLOODY_SPRUCE_LOG = registerWithItem("bloody_spruce_log", () -> {
            RotatedPillarBlock log = Blocks.log(MaterialColor.PODZOL, MaterialColor.COLOR_BROWN);
            ((FireBlock) Blocks.FIRE).setFlammable(log, 5, 5);
            return log;
        });
    public static final RegistryObject<BloodySpruceLeavesBlock> VAMPIRE_SPRUCE_LEAVES = registerWithItem("vampire_spruce_leaves", BloodySpruceLeavesBlock::new);
    public static final RegistryObject<BloodySpruceLeavesBlock> BLOODY_SPRUCE_LEAVES = registerWithItem("bloody_spruce_leaves", BloodySpruceLeavesBlock::new);
    public static final RegistryObject<BloodySpruceSaplingBlock> BLOODY_SPRUCE_SAPLING = registerWithItem("bloody_spruce_sapling", BloodySpruceSaplingBlock::new);
    public static final RegistryObject<VampireSpruceSaplingBlock> VAMPIRE_SPRUCE_SAPLING = registerWithItem("vampire_spruce_sapling", VampireSpruceSaplingBlock::new);
    public static final RegistryObject<VampirismBlock> CHANDELIER = registerWithItem("chandelier", ChandelierBlock::new);
    public static final RegistryObject<VampirismBlock> CANDELABRA = BLOCKS.register("candelabra", CandelabraBlock::new);
    public static final RegistryObject<VampirismBlock> CANDELABRA_WALL = BLOCKS.register("candelabra_wall", CandelabraWallBlock::new);
    public static final RegistryObject<VampirismBlock> CROSS = registerWithItem("cross", CrossBlock::new);
    public static final RegistryObject<VampirismBlock> TOMBSTONE1 = registerWithItem("tombstone1", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE2 = registerWithItem("tombstone2", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE3 = registerWithItem("tombstone3", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb3).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> GRAVE_CAGE = registerWithItem("grave_cage", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock());
    public static final RegistryObject<CursedGrassBlock> CURSED_GRASS_BLOCK = registerWithItem("cursed_grass_block", CursedGrassBlock::new);

    /**
     * TUTORIAL:
     * - Register blocks here.
     * - To register itemblock, use {@link ModBlocks#registerWithItem}
     * - Register blockstate in {@link de.teamlapen.vampirism.data.BlockStateGenerator#registerStatesAndModels()} (pass existent model if desired)
     * - Maybe set render layer in the json model or blockstate generator.
     * - Register itemrender in {@link de.teamlapen.vampirism.data.ItemModelGenerator#registerModels()}
     * - Register loot table in {@link de.teamlapen.vampirism.data.LootTablesGenerator.ModBlockLootTables#addTables()}
     * - Add lang keys
     * - Consider adding tool type in {@link de.teamlapen.vampirism.data.TagGenerator.ModBlockTagsProvider}
     * - Run genData (twice?)
     */
    @SuppressWarnings("JavadocReference")
    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier, Item.Properties properties) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, ()->new BlockItem(block.get(), properties));
        return block;
    }

    private static <T extends Block, R extends Item> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier, Function<T, R> itemMaker) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ModItems.ITEMS.register(name, ()->itemMaker.apply(block.get()));
        return block;
    }

    private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<T> supplier) {
        return registerWithItem(name, supplier, new Item.Properties().tab(VampirismMod.creativeTab));
    }

    public static void fixMappings(MissingMappingsEvent event) {
        event.getAllMappings(ForgeRegistries.Keys.BLOCKS).forEach(missingMapping -> {
            switch (missingMapping.getKey().toString()) {
                case "vampirism:blood_potion_table" -> missingMapping.remap(ModBlocks.POTION_TABLE.get());
                case "vampirism:garlic_beacon_normal" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_NORMAL.get());
                case "vampirism:garlic_beacon_weak" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_WEAK.get());
                case "vampirism:garlic_beacon_improved" -> missingMapping.remap(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get());
                case "vampirism:church_altar" -> missingMapping.remap(ModBlocks.ALTAR_CLEANSING.get());
            }
        });
    }

    public static Set<Block> getAllBlocks() {
        return BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toUnmodifiableSet());
    }

    public static void registerBlocks(IEventBus bus) {
        BLOCKS.register(bus);
    }
}

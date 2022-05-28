package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BushBlock;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.client.core.ModBlocksRender;
import de.teamlapen.vampirism.data.BlockStateGenerator;
import de.teamlapen.vampirism.data.ItemModelGenerator;
import de.teamlapen.vampirism.data.LootTablesGenerator;
import de.teamlapen.vampirism.items.CoffinBlockItem;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import de.teamlapen.vampirism.world.gen.CursedSpruceTree;
import de.teamlapen.vampirism.world.gen.DarkSpruceTree;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handles all block registrations and reference.
 */
@SuppressWarnings("unused")
public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, REFERENCE.MODID);
    public static final RegistryObject<AlchemicalCauldronBlock> ALCHEMICAL_CAULDRON =
            registerWithItem("alchemical_cauldron", AlchemicalCauldronBlock::new);
    public static final RegistryObject<AlchemicalFireBlock> ALCHEMICAL_FIRE =
            BLOCKS.register("alchemical_fire", AlchemicalFireBlock::new);
    public static final RegistryObject<AltarInfusionBlock> ALTAR_INFUSION =
            registerWithItem("altar_infusion", AltarInfusionBlock::new);
    public static final RegistryObject<AltarInspirationBlock> ALTAR_INSPIRATION =
            registerWithItem("altar_inspiration", AltarInspirationBlock::new);
    public static final RegistryObject<AltarPillarBlock> ALTAR_PILLAR =
            registerWithItem("altar_pillar", AltarPillarBlock::new);
    public static final RegistryObject<AltarTipBlock> ALTAR_TIP =
            registerWithItem("altar_tip", AltarTipBlock::new);
    public static final RegistryObject<BloodContainerBlock> BLOOD_CONTAINER =
            registerWithItem("blood_container", BloodContainerBlock::new, block -> itemBlock(block, new Item.Properties().tab(VampirismMod.creativeTab).stacksTo(1)));
    public static final RegistryObject<GrinderBlock> BLOOD_GRINDER =
            registerWithItem("blood_grinder", GrinderBlock::new);
    public static final RegistryObject<PedestalBlock> BLOOD_PEDESTAL =
            registerWithItem("blood_pedestal", PedestalBlock::new);
    public static final RegistryObject<SieveBlock> BLOOD_SIEVE =
            registerWithItem("blood_sieve", SieveBlock::new);
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_BRICK =
            registerWithItem("castle_block_dark_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_BRICK_BLOODY =
            registerWithItem("castle_block_dark_brick_bloody", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK_BLOODY));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_DARK_STONE =
            registerWithItem("castle_block_dark_stone", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_NORMAL_BRICK =
            registerWithItem("castle_block_normal_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.NORMAL_BRICK));
    public static final RegistryObject<CastleBricksBlock> CASTLE_BLOCK_PURPLE_BRICK =
            registerWithItem("castle_block_purple_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_DARK_BRICK =
            registerWithItem("castle_slab_dark_brick", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_DARK_STONE =
            registerWithItem("castle_slab_dark_stone", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleSlabBlock> CASTLE_SLAB_PURPLE_BRICK =
            registerWithItem("castle_slab_purple_brick", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_DARK_BRICK =
            registerWithItem("castle_stairs_dark_brick", () -> new CastleStairsBlock(()->CASTLE_BLOCK_DARK_BRICK.get().defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_DARK_STONE =
            registerWithItem("castle_stairs_dark_stone", () -> new CastleStairsBlock(()->CASTLE_BLOCK_DARK_STONE.get().defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleStairsBlock> CASTLE_STAIRS_PURPLE_BRICK =
            registerWithItem("castle_stairs_purple_brick", () -> new CastleStairsBlock(()->CASTLE_BLOCK_PURPLE_BRICK.get().defaultBlockState(), CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<ChurchAltarBlock> CHURCH_ALTAR =
            registerWithItem("church_altar", ChurchAltarBlock::new);
    public static final RegistryObject<CoffinBlock> COFFIN_WHITE =
            registerWithItem("coffin_white", () -> new CoffinBlock(DyeColor.WHITE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_ORANGE =
            registerWithItem("coffin_orange", () -> new CoffinBlock(DyeColor.ORANGE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_MAGENTA =
            registerWithItem("coffin_magenta", () -> new CoffinBlock(DyeColor.MAGENTA), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_LIGHT_BLUE =
            registerWithItem("coffin_light_blue", () -> new CoffinBlock(DyeColor.LIGHT_BLUE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_YELLOW =
            registerWithItem("coffin_yellow", () -> new CoffinBlock(DyeColor.YELLOW), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_LIME =
            registerWithItem("coffin_lime", () -> new CoffinBlock(DyeColor.LIME), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_PINK =
            registerWithItem("coffin_pink", () -> new CoffinBlock(DyeColor.PINK), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_GRAY =
            registerWithItem("coffin_gray", () -> new CoffinBlock(DyeColor.GRAY), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_LIGHT_GRAY =
            registerWithItem("coffin_light_gray", () -> new CoffinBlock(DyeColor.LIGHT_GRAY), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_CYAN =
            registerWithItem("coffin_cyan", () -> new CoffinBlock(DyeColor.CYAN), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_PURPLE =
            registerWithItem("coffin_purple", () -> new CoffinBlock(DyeColor.PURPLE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_BLUE =
            registerWithItem("coffin_blue", () -> new CoffinBlock(DyeColor.BLUE), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_BROWN =
            registerWithItem("coffin_brown", () -> new CoffinBlock(DyeColor.BROWN), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_GREEN =
            registerWithItem("coffin_green", () -> new CoffinBlock(DyeColor.GREEN), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_RED =
            registerWithItem("coffin_red", () -> new CoffinBlock(DyeColor.RED), CoffinBlockItem::new);
    public static final RegistryObject<CoffinBlock> COFFIN_BLACK =
            registerWithItem("coffin_black", () -> new CoffinBlock(DyeColor.BLACK), CoffinBlockItem::new);
    public static final RegistryObject<CursedEarthBlock> CURSED_EARTH =
            registerWithItem("cursed_earth", CursedEarthBlock::new);
    public static final RegistryObject<FirePlaceBlock> FIRE_PLACE =
            registerWithItem("fire_place", FirePlaceBlock::new);
    public static final RegistryObject<GarlicBlock> GARLIC =
            BLOCKS.register("garlic", GarlicBlock::new);
    public static final RegistryObject<GarlicBeaconBlock> GARLIC_BEACON_IMPROVED =
            registerWithItem("garlic_beacon_improved", () -> new GarlicBeaconBlock(GarlicBeaconBlock.Type.IMPROVED));
    public static final RegistryObject<GarlicBeaconBlock> GARLIC_BEACON_NORMAL =
            registerWithItem("garlic_beacon_normal", () -> new GarlicBeaconBlock(GarlicBeaconBlock.Type.NORMAL));
    public static final RegistryObject<GarlicBeaconBlock> GARLIC_BEACON_WEAK =
            registerWithItem("garlic_beacon_weak", () -> new GarlicBeaconBlock(GarlicBeaconBlock.Type.WEAK));
    public static final RegistryObject<HunterTableBlock> HUNTER_TABLE =
            registerWithItem("hunter_table", HunterTableBlock::new);
    public static final RegistryObject<MedChairBlock> MED_CHAIR =
            registerWithItem("med_chair", MedChairBlock::new);
    public static final RegistryObject<VampirismFlowerBlock> VAMPIRE_ORCHID =
            registerWithItem("vampire_orchid", () -> new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID));
    public static final RegistryObject<FlowerPotBlock> POTTED_VAMPIRE_ORCHID =
            registerWithItem("potted_vampire_orchid", () -> new FlowerPotBlock(()-> (FlowerPotBlock) Blocks.FLOWER_POT, VAMPIRE_ORCHID, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final RegistryObject<SunscreenBeaconBlock> SUNSCREEN_BEACON =
            registerWithItem("sunscreen_beacon", SunscreenBeaconBlock::new, block -> itemBlock(block, new Item.Properties().tab(VampirismMod.creativeTab).rarity(Rarity.EPIC)));
    public static final RegistryObject<TentBlock> TENT =
            BLOCKS.register("tent", TentBlock::new);
    public static final RegistryObject<TentMainBlock> TENT_MAIN =
            BLOCKS.register("tent_main", TentMainBlock::new);
    public static final RegistryObject<TotemBaseBlock> TOTEM_BASE =
            registerWithItem("totem_base", TotemBaseBlock::new);
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP =
            registerWithItem("totem_top", () -> new TotemTopBlock(false, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE =
            registerWithItem("totem_top_vampirism_vampire", () -> new TotemTopBlock(false, REFERENCE.VAMPIRE_PLAYER_KEY), block -> itemBlock(block, new Item.Properties()));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER =
            registerWithItem("totem_top_vampirism_hunter", () -> new TotemTopBlock(false, REFERENCE.HUNTER_PLAYER_KEY), block -> itemBlock(block, new Item.Properties()));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_CRAFTED =
            registerWithItem("totem_top_crafted", () -> new TotemTopBlock(true, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED =
            BLOCKS.register("totem_top_vampirism_vampire_crafted", () -> new TotemTopBlock(true, REFERENCE.VAMPIRE_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED =
            BLOCKS.register("totem_top_vampirism_hunter_crafted", () -> new TotemTopBlock(true, REFERENCE.HUNTER_PLAYER_KEY));
    public static final RegistryObject<WeaponTableBlock> WEAPON_TABLE =
            registerWithItem("weapon_table", WeaponTableBlock::new);
    public static final RegistryObject<PotionTableBlock> POTION_TABLE =
            registerWithItem("potion_table", PotionTableBlock::new);
    public static final RegistryObject<DarkSpruceLeavesBlock> DARK_SPRUCE_LEAVES =
            registerWithItem("dark_spruce_leaves", DarkSpruceLeavesBlock::new);
    public static final RegistryObject<VampirismBlock> CHANDELIER =
            registerWithItem("chandelier", ChandelierBlock::new);
    public static final RegistryObject<VampirismBlock> CANDELABRA =
            BLOCKS.register("candelabra", CandelabraBlock::new);
    public static final RegistryObject<VampirismBlock> CANDELABRA_WALL =
            BLOCKS.register("candelabra_wall", CandelabraWallBlock::new);
    public static final RegistryObject<VampirismBlock> CROSS =
            registerWithItem("cross", () -> new VampirismSplitBlock(AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.crossBottom, BlockVoxelshapes.crossTop, true).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE1 =
            registerWithItem("tombstone1", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE2 =
            registerWithItem("tombstone2", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> TOMBSTONE3 =
            registerWithItem("tombstone3", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb3).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> GRAVE_CAGE =
            registerWithItem("grave_cage", () -> new VampirismHorizontalBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock());
    public static final RegistryObject<CursedGrass> CURSED_GRASS =
            registerWithItem("cursed_grass", () -> new CursedGrass(AbstractBlock.Properties.of(Material.GRASS, MaterialColor.COLOR_BLACK).randomTicks().strength(0.6F).sound(SoundType.GRASS))));
    public static final RegistryObject<RotatedPillarBlock> DARK_SPRUCE_LOG =
            registerWithItem("dark_spruce_log", () -> new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK));
    public static final RegistryObject<Block> CURSED_ROOTS =
            registerWithItem("cursed_roots", flammable(()->new BushBlock(AbstractBlock.Properties.of(Material.REPLACEABLE_PLANT, MaterialColor.COLOR_RED).noCollission().instabreak().sound(SoundType.GRASS)), 60, 100));
    public static final RegistryObject<Block> POTTED_CURSED_ROOTS =
            registerWithItem("potted_cursed_roots", () -> new FlowerPotBlock(()-> (FlowerPotBlock) Blocks.FLOWER_POT, CURSED_ROOTS, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()));
    public static final RegistryObject<RotatedPillarBlock> CURSED_SPRUCE_LOG =
            registerWithItem("cursed_spruce_log", CursedSpruceBlock::new);
    public static final RegistryObject<SaplingBlock> DARK_SPRUCE_SAPLING =
            registerWithItem("dark_spruce_sapling", DarkSpruceSaplingBlock::new);
    public static final RegistryObject<SaplingBlock> CURSED_SPRUCE_SAPLING =
            registerWithItem("cursed_spruce_sapling", () -> new SaplingBlock(new CursedSpruceTree(), AbstractBlock.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    //TODO: I got this far. Need to do the rest.
    public static final RegistryObject<CursedBarkBlock> CURSED_BARK =
            registerWithItem("cursed_bark", CursedBarkBlock::new);
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_DARK_SPRUCE_LOG =
            registerWithItem("stripped_dark_spruce_log", () -> new RotatedPillarBlock());
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_CURSED_SPRUCE_LOG =
            registerWithItem("stripped_cursed_spruce_log", () -> new RotatedPillarBlock());
    public static final RegistryObject<Block> DARK_SPRUCE_PLANKS =
            registerWithItem("dark_spruce_planks", () -> new Block());
    public static final RegistryObject<Block> CURSED_SPRUCE_PLANKS =
            registerWithItem("cursed_spruce_planks", () -> new Block());
    public static final RegistryObject<DoorBlock> DARK_SPRUCE_DOOR =
            registerWithItem("dark_spruce_door", () -> new DoorBlock());
    public static final RegistryObject<DoorBlock> CURSED_SPRUCE_DOOR =
            registerWithItem("cursed_spruce_door", () -> new DoorBlock());
    public static final RegistryObject<TrapDoorBlock> DARK_SPRUCE_TRAPDOOR =
            registerWithItem("dark_spruce_trapdoor", () -> new TrapDoorBlock());
    public static final RegistryObject<TrapDoorBlock> CURSED_SPRUCE_TRAPDOOR =
            registerWithItem("cursed_spruce_trapdoor", () -> new TrapDoorBlock());
    public static final RegistryObject<StairsBlock> DARK_SPRUCE_STAIRS =
            registerWithItem("dark_spruce_stairs", () -> new StairsBlock());
    public static final RegistryObject<StairsBlock> CURSED_SPRUCE_STAIRS =
            registerWithItem("cursed_spruce_stairs", () -> new StairsBlock());
    public static final RegistryObject<LogBlock> DARK_SPRUCE_WOOD =
            registerWithItem("dark_spruce_wood", () -> new LogBlock());
    public static final RegistryObject<LogBlock> CURSED_SPRUCE_WOOD =
            registerWithItem("cursed_spruce_wood", () -> new LogBlock());
    public static final RegistryObject<LogBlock> STRIPPED_DARK_SPRUCE_WOOD =
            registerWithItem("stripped_dark_spruce_wood", () -> new LogBlock());
    public static final RegistryObject<LogBlock> STRIPPED_CURSED_SPRUCE_WOOD =
            registerWithItem("stripped_cursed_spruce_wood", () -> new LogBlock());
    public static final RegistryObject<StandingSignBlock> DARK_SPRUCE_SIGN =
            registerWithItem("dark_spruce_sign", () -> new StandingSignBlock());
    public static final RegistryObject<StandingSignBlock> CURSED_SPRUCE_SIGN =
            registerWithItem("cursed_spruce_sign", () -> new StandingSignBlock());
    public static final RegistryObject<WallSignBlock> DARK_SPRUCE_WALL_SIGN =
            registerWithItem("dark_spruce_wall_sign", () -> new WallSignBlock());
    public static final RegistryObject<WallSignBlock> CURSED_SPRUCE_WALL_SIGN =
            registerWithItem("cursed_spruce_wall_sign", () -> new WallSignBlock());
    public static final RegistryObject<PressurePlateBlock> DARK_SPRUCE_PRESSURE_PLACE =
            registerWithItem("dark_spruce_pressure_place", () -> new PressurePlateBlock());
    public static final RegistryObject<PressurePlateBlock> CURSED_SPRUCE_PRESSURE_PLACE =
            registerWithItem("cursed_spruce_pressure_place", () -> new PressurePlateBlock());
    public static final RegistryObject<WoodButtonBlock> DARK_SPRUCE_BUTTON =
            registerWithItem("dark_spruce_button", () -> new WoodButtonBlock());
    public static final RegistryObject<WoodButtonBlock> CURSED_SPRUCE_BUTTON =
            registerWithItem("cursed_spruce_button", () -> new WoodButtonBlock());
    public static final RegistryObject<SlabBlock> DARK_SPRUCE_SLAB =
            registerWithItem("dark_spruce_slab", () -> new SlabBlock());
    public static final RegistryObject<SlabBlock> CURSED_SPRUCE_SLAB =
            registerWithItem("cursed_spruce_slab", () -> new SlabBlock());
    public static final RegistryObject<FenceGateBlock> DARK_SPRUCE_FENCE_GATE =
            registerWithItem("dark_spruce_fence_gate", () -> new FenceGateBlock());
    public static final RegistryObject<FenceGateBlock> CURSED_SPRUCE_FENCE_GATE =
            registerWithItem("cursed_spruce_fence_gate", () -> new FenceGateBlock());
    public static final RegistryObject<FenceBlock> DARK_SPRUCE_FENCE =
            registerWithItem("dark_spruce_fence", () -> new FenceBlock());
    public static final RegistryObject<FenceBlock> CURSED_SPRUCE_FENCE =
            registerWithItem("cursed_spruce_fence", () -> new FenceBlock());
    public static final RegistryObject<VampirismBlock> VAMPIRE_RACK =
            registerWithItem("vampire_rack", () -> new VampirismBlock());
    public static final RegistryObject<VampirismBlock> THRONE =
            registerWithItem("throne", () -> new VampirismBlock());

    /**
     * empty unless in datagen
     */
    private static final Set<Block> ALL_BLOCKS = Sets.newHashSet();
    private static Set<Block> ITEM_BLOCKS = Sets.newHashSet();

    static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(itemBlock(blood_container, new Item.Properties().tab(VampirismMod.creativeTab).stacksTo(1)));
        registry.register(new CoffinBlockItem(coffin_white));
        registry.register(new CoffinBlockItem(coffin_orange));
        registry.register(new CoffinBlockItem(coffin_magenta));
        registry.register(new CoffinBlockItem(coffin_light_blue));
        registry.register(new CoffinBlockItem(coffin_yellow));
        registry.register(new CoffinBlockItem(coffin_lime));
        registry.register(new CoffinBlockItem(coffin_pink));
        registry.register(new CoffinBlockItem(coffin_gray));
        registry.register(new CoffinBlockItem(coffin_light_gray));
        registry.register(new CoffinBlockItem(coffin_cyan));
        registry.register(new CoffinBlockItem(coffin_purple));
        registry.register(new CoffinBlockItem(coffin_blue));
        registry.register(new CoffinBlockItem(coffin_brown));
        registry.register(new CoffinBlockItem(coffin_green));
        registry.register(new CoffinBlockItem(coffin_red));
        registry.register(new CoffinBlockItem(coffin_black));
        registry.register(itemBlock(sunscreen_beacon, new Item.Properties().tab(VampirismMod.creativeTab).rarity(Rarity.EPIC)));
        registry.register(itemBlock(totem_top_vampirism_hunter, new Item.Properties()));
        registry.register(itemBlock(totem_top_vampirism_vampire, new Item.Properties()));
        ITEM_BLOCKS.forEach(block -> registry.register(itemBlock(block)));
        ITEM_BLOCKS = null;
    }

    static void registerBlocks(IForgeRegistry<Block> registry) {
        CastleBricksBlock castle_block_dark_brick = new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK);
        CastleBricksBlock castle_block_dark_stone = new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_STONE);
        CastleBricksBlock castle_block_purple_brick = new CastleBricksBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK);
        VampirismFlowerBlock vampire_orchid = new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID);

        registry.register(prepareItemRegistry(new AlchemicalCauldronBlock()));
        registry.register(prepareRegister(new AlchemicalFireBlock()));
        registry.register(prepareItemRegistry(new AltarInfusionBlock()));
        registry.register(prepareItemRegistry(new AltarInspirationBlock()));
        registry.register(prepareItemRegistry(new AltarPillarBlock()));
        registry.register(prepareItemRegistry(new AltarTipBlock()));
        registry.register(prepareRegister(new BloodContainerBlock()));
        registry.register(prepareItemRegistry(new GrinderBlock()));
        registry.register(prepareItemRegistry(new PedestalBlock()));
        registry.register(prepareItemRegistry(new SieveBlock()));
        registry.register(prepareItemRegistry(castle_block_dark_brick));
        registry.register(prepareItemRegistry(new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK_BLOODY)));
        registry.register(prepareItemRegistry(castle_block_dark_stone));
        registry.register(prepareItemRegistry(new CastleBricksBlock(CastleBricksBlock.EnumVariant.NORMAL_BRICK)));
        registry.register(prepareItemRegistry(castle_block_purple_brick));
        registry.register(prepareItemRegistry(new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_BRICK)));
        registry.register(prepareItemRegistry(new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_STONE)));
        registry.register(prepareItemRegistry(new CastleSlabBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK)));
        registry.register(prepareItemRegistry(new CastleStairsBlock(castle_block_dark_brick.defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_BRICK)));
        registry.register(prepareItemRegistry(new CastleStairsBlock(castle_block_dark_stone.defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_STONE)));
        registry.register(prepareItemRegistry(new CastleStairsBlock(castle_block_purple_brick.defaultBlockState(), CastleBricksBlock.EnumVariant.PURPLE_BRICK)));
        registry.register(prepareItemRegistry(new ChurchAltarBlock()));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.WHITE)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.ORANGE)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.MAGENTA)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.LIGHT_BLUE)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.YELLOW)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.LIME)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.PINK)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.GRAY)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.LIGHT_GRAY)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.CYAN)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.PURPLE)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.BLUE)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.BROWN)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.GREEN)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.RED)));
        registry.register(prepareRegister(new CoffinBlock(DyeColor.BLACK)));
        registry.register(prepareItemRegistry(new CursedEarthBlock()));
        registry.register(prepareItemRegistry(new FirePlaceBlock()));
        registry.register(prepareRegister(new GarlicBlock()));
        registry.register(prepareItemRegistry(new GarlicBeaconBlock(GarlicBeaconBlock.Type.IMPROVED)));
        registry.register(prepareItemRegistry(new GarlicBeaconBlock(GarlicBeaconBlock.Type.NORMAL)));
        registry.register(prepareItemRegistry(new GarlicBeaconBlock(GarlicBeaconBlock.Type.WEAK)));
        registry.register(prepareItemRegistry(new HunterTableBlock()));
        registry.register(prepareItemRegistry(new MedChairBlock()));
        registry.register(prepareRegister(new FlowerPotBlock(vampire_orchid, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()).setRegistryName(REFERENCE.MODID, "potted_vampire_orchid")));
        registry.register(prepareRegister(new SunscreenBeaconBlock()));
        registry.register(prepareRegister(new TentBlock()));
        registry.register(prepareRegister(new TentMainBlock()));
        registry.register(prepareItemRegistry(new TotemBaseBlock()));
        registry.register(prepareItemRegistry(new TotemTopBlock(false, new ResourceLocation("none")).setRegistryName(REFERENCE.MODID, "totem_top")));
        registry.register(prepareRegister(new TotemTopBlock(false, REFERENCE.HUNTER_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_hunter")));
        registry.register(prepareRegister(new TotemTopBlock(false, REFERENCE.VAMPIRE_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_vampire")));
        registry.register(prepareItemRegistry(new TotemTopBlock(true, new ResourceLocation("none")).setRegistryName(REFERENCE.MODID, "totem_top_crafted")));
        registry.register(prepareRegister(new TotemTopBlock(true, REFERENCE.HUNTER_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_hunter_crafted")));
        registry.register(prepareRegister(new TotemTopBlock(true, REFERENCE.VAMPIRE_PLAYER_KEY).setRegistryName(REFERENCE.MODID, "totem_top_vampirism_vampire_crafted")));
        registry.register(prepareItemRegistry(vampire_orchid));
        registry.register(prepareItemRegistry(new WeaponTableBlock()));
        registry.register(prepareItemRegistry(new PotionTableBlock()));
        registry.register(prepareItemRegistry(new DarkSpruceLeavesBlock("dark_spruce_leaves")));
        registry.register(prepareItemRegistry(new ChandelierBlock()));
        registry.register(prepareRegister(new CandelabraWallBlock()));
        registry.register(prepareRegister(new CandelabraBlock()));
        registry.register(prepareItemRegistry(new VampirismSplitBlock("cross", AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.crossBottom, BlockVoxelshapes.crossTop, true).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("tombstone1", AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("tombstone2", AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("tombstone3", AbstractBlock.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb3).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("grave_cage", AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new CursedGrass(AbstractBlock.Properties.of(Material.GRASS, MaterialColor.COLOR_BLACK).randomTicks().strength(0.6F).sound(SoundType.GRASS))).setRegistryName(REFERENCE.MODID, "cursed_grass"));
        Block bush = new BushBlock(AbstractBlock.Properties.of(Material.REPLACEABLE_PLANT, MaterialColor.COLOR_RED).noCollission().instabreak().sound(SoundType.GRASS)).setRegistryName(REFERENCE.MODID, "cursed_roots");
        ((FireBlock) Blocks.FIRE).setFlammable(bush, 60, 100);
        registry.register(prepareItemRegistry(bush));
        registry.register(prepareRegister(new FlowerPotBlock(bush, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion()).setRegistryName(REFERENCE.MODID, "potted_cursed_roots")));

        Block dark_spruce_planks = new Block(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_planks");
        Block cursed_spruce_planks = new Block(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_planks");
        Block dark_spruce_log = new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK).setRegistryName(REFERENCE.MODID, "dark_spruce_log");
        Block cursed_spruce_log = new CursedSpruceBlock().setRegistryName(REFERENCE.MODID, "cursed_spruce_log");
        registry.register(prepareItemRegistry(dark_spruce_log));
        registry.register(prepareItemRegistry(cursed_spruce_log));
        registry.register(prepareItemRegistry(new SaplingBlock(new DarkSpruceTree(), AbstractBlock.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)).setRegistryName(REFERENCE.MODID,"dark_spruce_sapling")));
        registry.register(prepareItemRegistry(new SaplingBlock(new CursedSpruceTree(), AbstractBlock.Properties.of(Material.PLANT, MaterialColor.COLOR_BLACK).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)).setRegistryName(REFERENCE.MODID,"cursed_spruce_sapling")));
        registry.register(prepareItemRegistry(new CursedBarkBlock().setRegistryName(REFERENCE.MODID, "cursed_bark")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_GRAY).setRegistryName(REFERENCE.MODID, "stripped_dark_spruce_log")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.CRIMSON_HYPHAE).setRegistryName(REFERENCE.MODID, "stripped_cursed_spruce_log")));
        registry.register(prepareItemRegistry(dark_spruce_planks));
        registry.register(prepareItemRegistry(cursed_spruce_planks));
        registry.register(prepareItemRegistry(new DoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion()).setRegistryName(REFERENCE.MODID, "dark_spruce_door")));
        registry.register(prepareItemRegistry(new DoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(3.0F).sound(SoundType.WOOD).noOcclusion()).setRegistryName(REFERENCE.MODID, "cursed_spruce_door")));
        registry.register(prepareItemRegistry(new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(ModBlocks::never)).setRegistryName(REFERENCE.MODID, "dark_spruce_trapdoor")));
        registry.register(prepareItemRegistry(new TrapDoorBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(ModBlocks::never)).setRegistryName(REFERENCE.MODID, "cursed_spruce_trapdoor")));
        registry.register(prepareItemRegistry(new StairsBlock(dark_spruce_planks.defaultBlockState(), AbstractBlock.Properties.copy(dark_spruce_planks)).setRegistryName(REFERENCE.MODID, "dark_spruce_stairs")));
        registry.register(prepareItemRegistry(new StairsBlock(cursed_spruce_planks.defaultBlockState(), AbstractBlock.Properties.copy(cursed_spruce_planks)).setRegistryName(REFERENCE.MODID, "cursed_spruce_stairs")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_BLACK).setRegistryName(REFERENCE.MODID, "dark_spruce_wood")));
        registry.register(prepareItemRegistry(new CursedSpruceBlock().setRegistryName(REFERENCE.MODID, "cursed_spruce_wood")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.COLOR_GRAY).setRegistryName(REFERENCE.MODID, "stripped_dark_spruce_wood")));
        registry.register(prepareItemRegistry(new LogBlock(MaterialColor.COLOR_BLACK, MaterialColor.CRIMSON_HYPHAE).setRegistryName(REFERENCE.MODID, "stripped_cursed_spruce_wood")));
        registry.register(prepareRegister(new StandingSignBlock(AbstractBlock.Properties.of(Material.WOOD, dark_spruce_log.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.dark_spruce).setRegistryName(REFERENCE.MODID, "dark_spruce_sign")));
        registry.register(prepareRegister(new StandingSignBlock(AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_log.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), LogBlock.cursed_spruce).setRegistryName(REFERENCE.MODID, "cursed_spruce_sign")));
        registry.register(prepareRegister(new WallSignBlock(AbstractBlock.Properties.of(Material.WOOD, dark_spruce_log.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(() -> dark_spruce_sign), LogBlock.dark_spruce).setRegistryName(REFERENCE.MODID, "dark_spruce_wall_sign")));
        registry.register(prepareRegister(new WallSignBlock(AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_log.defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(() -> cursed_spruce_sign), LogBlock.dark_spruce).setRegistryName(REFERENCE.MODID, "cursed_spruce_wall_sign")));
        registry.register(prepareItemRegistry(new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD, dark_spruce_planks.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_pressure_place")));
        registry.register(prepareItemRegistry(new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_planks.defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_pressure_place")));
        registry.register(prepareItemRegistry(new WoodButtonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_button")));
        registry.register(prepareItemRegistry(new WoodButtonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_button")));
        registry.register(prepareItemRegistry(new SlabBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_GRAY).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_slab")));
        registry.register(prepareItemRegistry(new SlabBlock(AbstractBlock.Properties.of(Material.WOOD, MaterialColor.CRIMSON_HYPHAE).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_slab")));
        registry.register(prepareItemRegistry(new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD, dark_spruce_planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_fence_gate")));
        registry.register(prepareItemRegistry(new FenceGateBlock(AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_fence_gate")));
        registry.register(prepareItemRegistry(new FenceBlock(AbstractBlock.Properties.of(Material.WOOD, dark_spruce_planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "dark_spruce_fence")));
        registry.register(prepareItemRegistry(new FenceBlock(AbstractBlock.Properties.of(Material.WOOD, cursed_spruce_planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(REFERENCE.MODID, "cursed_spruce_fence")));
        registry.register(prepareItemRegistry(new VampirismHorizontalBlock("vampire_rack", AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.vampire_rack).markDecorativeBlock()));
        registry.register(prepareItemRegistry(new ThroneBlock()));

        /**
         * TUTORIAL:
         * - Register block here.
         * - Register itemblock in {@link ModBlocks#registerItemBlocks(IForgeRegistry)}
         * - Maybe set render layer in {@link ModBlocksRender#registerRenderType()}
         * - Register blockstate in {@link BlockStateGenerator#registerStatesAndModels()} (pass existent model if desired)
         * - Register itemrender in {@link ItemModelGenerator#registerModels()}
         * - Register loot table in {@link LootTablesGenerator.ModBlockLootTables#addTables()}
         * - Add lang keys
         * - Run genData (twice?)
         */
    }

    private static <T extends Block> T prepareRegister(T block) {
        if (VampirismMod.inDataGen) {
            ALL_BLOCKS.add(block);
        }
        return block;
    }

    private static <T extends Block> T prepareItemRegistry(T block) {
        ITEM_BLOCKS.add(block);
        return prepareRegister(block);
    }

    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block, @Nonnull Item.Properties props) {
        return new BlockItem(block, props);
    }

    @Nonnull
    private static BlockItem itemBlock(@Nonnull Block block) {
        return itemBlock(block, new Item.Properties().tab(VampirismMod.creativeTab));
    }

    private static Boolean never(BlockState p_235427_0_, IBlockReader p_235427_1_, BlockPos p_235427_2_, EntityType<?> p_235427_3_) {
        return false;
    }

    public static void fixMappings(RegistryEvent.MissingMappings<Block> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:blood_potion_table".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.potion_table);
            }else if ("vampirism:vampire_spruce_leaves".equals(missingMapping.key.toString()))  {
                missingMapping.remap(ModBlocks.dark_spruce_leaves);
            } else if ("vampirism:bloody_spruce_leaves".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.dark_spruce_leaves);
            } else if ("vampirism:bloody_spruce_log".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.cursed_spruce_log);
            }
        });
    }

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

    private static <T extends Block> Supplier<T> flammable(Supplier<T> supplier, int i1, int i2) {
        return () -> {
            T block = supplier.get()
            ((FireBlock) Blocks.FIRE).setFlammable(block, i1,i2);
            return block;
        };
    }
    public static Set<Block> getAllBlocks() {
        return ImmutableSet.copyOf(ALL_BLOCKS);
    }
}

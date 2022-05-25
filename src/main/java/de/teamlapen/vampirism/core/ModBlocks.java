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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
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
    public static final RegistryObject<AlchemicalCauldronBlock> alchemical_cauldron =
        registerWithItem("alchemical_cauldron", AlchemicalCauldronBlock::new);
    public static final RegistryObject<AlchemicalFireBlock> alchemical_fire =
        BLOCKS.register("alchemical_fire", AlchemicalFireBlock::new);
    public static final RegistryObject<AltarInfusionBlock> altar_infusion =
        registerWithItem("altar_infusion", AltarInfusionBlock::new);
    public static final RegistryObject<AltarInspirationBlock> altar_inspiration =
        registerWithItem("altar_inspiration", AltarInspirationBlock::new);
    public static final RegistryObject<AltarPillarBlock> altar_pillar =
        registerWithItem("altar_pillar", AltarPillarBlock::new);
    public static final RegistryObject<AltarTipBlock> altar_tip =
        registerWithItem("altar_tip", AltarTipBlock::new);
    public static final RegistryObject<BloodContainerBlock> blood_container =
        registerWithItem("blood_container", BloodContainerBlock::new, new Item.Properties().tab(VampirismMod.creativeTab).stacksTo(1));
    public static final RegistryObject<GrinderBlock> blood_grinder =
        registerWithItem("blood_grinder", GrinderBlock::new);
    public static final RegistryObject<PedestalBlock> blood_pedestal =
        registerWithItem("blood_pedestal", PedestalBlock::new);
    public static final RegistryObject<SieveBlock> blood_sieve =
        registerWithItem("blood_sieve", SieveBlock::new);
    public static final RegistryObject<CastleBricksBlock> castle_block_dark_brick =
        registerWithItem("castle_block_dark_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleBricksBlock> castle_block_dark_brick_bloody =
        registerWithItem("castle_block_dark_brick_bloody", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_BRICK_BLOODY));
    public static final RegistryObject<CastleBricksBlock> castle_block_dark_stone =
        registerWithItem("castle_block_dark_stone", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleBricksBlock> castle_block_normal_brick =
        registerWithItem("castle_block_normal_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.NORMAL_BRICK));
    public static final RegistryObject<CastleBricksBlock> castle_block_purple_brick =
        registerWithItem("castle_block_purple_brick", () -> new CastleBricksBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<CastleSlabBlock> castle_slab_dark_brick =
        registerWithItem("castle_slab_dark_brick", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleSlabBlock> castle_slab_dark_stone =
        registerWithItem("castle_slab_dark_stone", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleSlabBlock> castle_slab_purple_brick =
        registerWithItem("castle_slab_purple_brick", () -> new CastleSlabBlock(CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<CastleStairsBlock> castle_stairs_dark_brick =
        registerWithItem("castle_stairs_dark_brick", () -> new CastleStairsBlock(() -> castle_block_dark_brick.get().defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_BRICK));
    public static final RegistryObject<CastleStairsBlock> castle_stairs_dark_stone =
        registerWithItem("castle_stairs_dark_stone", () -> new CastleStairsBlock(() -> castle_block_dark_stone.get().defaultBlockState(), CastleBricksBlock.EnumVariant.DARK_STONE));
    public static final RegistryObject<CastleStairsBlock> castle_stairs_purple_brick =
        registerWithItem("castle_stairs_purple_brick", () -> new CastleStairsBlock(() -> castle_block_purple_brick.get().defaultBlockState(), CastleBricksBlock.EnumVariant.PURPLE_BRICK));
    public static final RegistryObject<AltarCleansingBlock> altar_cleansing =
        registerWithItem("altar_cleansing", AltarCleansingBlock::new);
    public static final RegistryObject<CoffinBlock> coffin =
        registerWithItem("coffin", CoffinBlock::new, CoffinItem::new);
    public static final RegistryObject<CursedEarthBlock> cursed_earth =
        registerWithItem("cursed_earth", CursedEarthBlock::new);
    public static final RegistryObject<FirePlaceBlock> fire_place =
        registerWithItem("fire_place", FirePlaceBlock::new);
    public static final RegistryObject<GarlicBlock> garlic =
        BLOCKS.register("garlic", GarlicBlock::new);
    public static final RegistryObject<GarlicDiffuserBlock> garlic_diffuser_improved =
        registerWithItem("garlic_diffuser_improved", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.IMPROVED));
    public static final RegistryObject<GarlicDiffuserBlock> garlic_diffuser_normal =
        registerWithItem("garlic_diffuser_normal", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.NORMAL));
    public static final RegistryObject<GarlicDiffuserBlock> garlic_diffuser_weak =
        registerWithItem("garlic_diffuser_weak", () -> new GarlicDiffuserBlock(GarlicDiffuserBlock.Type.WEAK));
    public static final RegistryObject<HunterTableBlock> hunter_table =
        registerWithItem("hunter_table", HunterTableBlock::new);
    public static final RegistryObject<MedChairBlock> med_chair =
        BLOCKS.register("med_chair", MedChairBlock::new);
    public static final RegistryObject<SunscreenBeaconBlock> sunscreen_beacon =
        registerWithItem("sunscreen_beacon", SunscreenBeaconBlock::new);
    public static final RegistryObject<TentBlock> tent =
        BLOCKS.register("tent", TentBlock::new);
    public static final RegistryObject<TentMainBlock> tent_main =
        BLOCKS.register("tent_main", TentMainBlock::new);
    public static final RegistryObject<TotemBaseBlock> totem_base =
        registerWithItem("totem_base", TotemBaseBlock::new);
    public static final RegistryObject<TotemTopBlock> totem_top =
        registerWithItem("totem_top", () -> new TotemTopBlock(false, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> totem_top_vampirism_vampire =
        registerWithItem("totem_top_vampirism_vampire", () -> new TotemTopBlock(false, REFERENCE.VAMPIRE_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> totem_top_vampirism_hunter =
        registerWithItem("totem_top_vampirism_hunter", () -> new TotemTopBlock(false, REFERENCE.HUNTER_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> totem_top_crafted =
        registerWithItem("totem_top_crafted", () -> new TotemTopBlock(true, new ResourceLocation("none")));
    public static final RegistryObject<TotemTopBlock> totem_top_vampirism_vampire_crafted =
        BLOCKS.register("totem_top_vampirism_vampire_crafted", () -> new TotemTopBlock(true, REFERENCE.VAMPIRE_PLAYER_KEY));
    public static final RegistryObject<TotemTopBlock> totem_top_vampirism_hunter_crafted =
        BLOCKS.register("totem_top_vampirism_hunter_crafted", () -> new TotemTopBlock(true, REFERENCE.HUNTER_PLAYER_KEY));
    public static final RegistryObject<VampirismFlowerBlock> vampire_orchid =
        registerWithItem("vampire_orchid", () -> new VampirismFlowerBlock(VampirismFlowerBlock.TYPE.ORCHID));
    public static final RegistryObject<FlowerPotBlock> potted_vampire_orchid =
            BLOCKS.register("potted_vampire_orchid", () -> {
                FlowerPotBlock block = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, vampire_orchid, Block.Properties.of(Material.DECORATION).instabreak().noOcclusion());
                ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(vampire_orchid.getId(), () -> block);
                return block;
            });
    public static final RegistryObject<WeaponTableBlock> weapon_table =
        registerWithItem("weapon_table", WeaponTableBlock::new);
    public static final RegistryObject<PotionTableBlock> potion_table =
        registerWithItem("potion_table", PotionTableBlock::new);
    public static final RegistryObject<RotatedPillarBlock> bloody_spruce_log =
        registerWithItem("bloody_spruce_log", () -> {
            RotatedPillarBlock log = Blocks.log(MaterialColor.PODZOL, MaterialColor.COLOR_BROWN);
            ((FireBlock) Blocks.FIRE).setFlammable(log, 5, 5);
            return log;
        });
    public static final RegistryObject<BloodySpruceLeavesBlock> vampire_spruce_leaves =
        registerWithItem("vampire_spruce_leaves", BloodySpruceLeavesBlock::new);
    public static final RegistryObject<BloodySpruceLeavesBlock> bloody_spruce_leaves =
        registerWithItem("bloody_spruce_leaves", BloodySpruceLeavesBlock::new);
    public static final RegistryObject<BloodySpruceSaplingBlock> bloody_spruce_sapling =
        registerWithItem("bloody_spruce_sapling", BloodySpruceSaplingBlock::new);
    public static final RegistryObject<VampireSpruceSaplingBlock> vampire_spruce_sapling =
        registerWithItem("vampire_spruce_sapling", VampireSpruceSaplingBlock::new);
    public static final RegistryObject<VampirismBlock> chandelier =
        registerWithItem("chandelier", ChandelierBlock::new);
    public static final RegistryObject<VampirismBlock> candelabra =
        BLOCKS.register("candelabra", CandelabraBlock::new);
    public static final RegistryObject<VampirismBlock> candelabra_wall =
        BLOCKS.register("candelabra_wall", CandelabraWallBlock::new);
    public static final RegistryObject<VampirismBlock> cross =
        registerWithItem("cross", CrossBlock::new);
    public static final RegistryObject<VampirismBlock> tombstone1 =
        registerWithItem("tombstone1", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb1).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> tombstone2 =
        registerWithItem("tombstone2", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb2).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> tombstone3 =
        registerWithItem("tombstone3", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2, 6), BlockVoxelshapes.tomb3).markDecorativeBlock());
    public static final RegistryObject<VampirismBlock> grave_cage =
        registerWithItem("grave_cage", () -> new VampirismHorizontalBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).strength(6, 8).requiresCorrectToolForDrops().sound(SoundType.METAL), BlockVoxelshapes.grave_cage).markDecorativeBlock());
    public static final RegistryObject<CursedGrassBlock> cursed_grass_block =
        registerWithItem("cursed_grass_block", CursedGrassBlock::new);

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

    public static void fixMappings(RegistryEvent.MissingMappings<Block> event) {
        event.getAllMappings().forEach(missingMapping -> {
            if ("vampirism:blood_potion_table".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.potion_table.get());
            } else if ("vampirism:garlic_beacon_normal".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.garlic_diffuser_normal.get());
            } else if ("vampirism:garlic_beacon_weak".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.garlic_diffuser_weak.get());
            } else if ("vampirism:garlic_beacon_improved".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.garlic_diffuser_improved.get());
            } else if ("vampirism:church_altar".equals(missingMapping.key.toString())) {
                missingMapping.remap(ModBlocks.altar_cleansing.get());
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

package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.blockentity.*;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.FogDiffuserBlockEntity;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.common.world.blockentity.diffuser.GarlicDiffuserCoreBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;


public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TentBlockEntity>> TENT = BLOCK_ENTITY_TYPES.register("tent", () -> create(TentBlockEntity::new, ModBlocks.TENT_MAIN.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoffinBlockEntity>> COFFIN = BLOCK_ENTITY_TYPES.register("coffin", () -> create(CoffinBlockEntity::new, Stream.of(ModBlocks.COFFIN_WHITE, ModBlocks.COFFIN_ORANGE, ModBlocks.COFFIN_MAGENTA, ModBlocks.COFFIN_LIGHT_BLUE, ModBlocks.COFFIN_YELLOW, ModBlocks.COFFIN_LIME, ModBlocks.COFFIN_PINK, ModBlocks.COFFIN_GRAY, ModBlocks.COFFIN_LIGHT_GRAY, ModBlocks.COFFIN_CYAN, ModBlocks.COFFIN_PURPLE, ModBlocks.COFFIN_BLUE, ModBlocks.COFFIN_BROWN, ModBlocks.COFFIN_GREEN, ModBlocks.COFFIN_RED, ModBlocks.COFFIN_BLACK).map(DeferredHolder::get).toArray(Block[]::new)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AltarInfusionBlockEntity>> ALTAR_INFUSION = BLOCK_ENTITY_TYPES.register("altar_infusion", () -> create(AltarInfusionBlockEntity::new, ModBlocks.ALTAR_INFUSION.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodContainerBlockEntity>> BLOOD_CONTAINER = BLOCK_ENTITY_TYPES.register("blood_container", () -> create(BloodContainerBlockEntity::new, ModBlocks.BLOOD_CONTAINER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AltarInspirationBlockEntity>> ALTAR_INSPIRATION = BLOCK_ENTITY_TYPES.register("altar_inspiration", () -> create(AltarInspirationBlockEntity::new, ModBlocks.ALTAR_INSPIRATION.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SunscreenBeaconBlockEntity>> SUNSCREEN_BEACON = BLOCK_ENTITY_TYPES.register("sunscreen_beacon", () -> create(SunscreenBeaconBlockEntity::new, ModBlocks.SUNSCREEN_BEACON.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemicalCauldronBlockEntity>> ALCHEMICAL_CAULDRON = BLOCK_ENTITY_TYPES.register("alchemical_cauldron", () -> create(AlchemicalCauldronBlockEntity::new, ModBlocks.ALCHEMICAL_CAULDRON.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GarlicDiffuserBlockEntity>> GARLIC_DIFFUSER = BLOCK_ENTITY_TYPES.register("garlic_diffuser", () -> create(GarlicDiffuserBlockEntity::new, ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), ModBlocks.GARLIC_DIFFUSER_WEAK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GarlicDiffuserCoreBlockEntity>> GARLIC_DIFFUSER_CORE = BLOCK_ENTITY_TYPES.register("garlic_diffuser_core", () -> create(GarlicDiffuserCoreBlockEntity::new, ModBlocks.GARLIC_DIFFUSER_CORE.get(), ModBlocks.GARLIC_DIFFUSER_CORE_IMPROVED.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PedestalBlockEntity>> BLOOD_PEDESTAL = BLOCK_ENTITY_TYPES.register("blood_pedestal", () -> create(PedestalBlockEntity::new, ModBlocks.BLOOD_PEDESTAL.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodGrinderBlockEntity>> BLOOD_GRINDER = BLOCK_ENTITY_TYPES.register("blood_grinder", () -> create(BloodGrinderBlockEntity::new, ModBlocks.BLOOD_GRINDER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodSieveBlockEntity>> BLOOD_SIEVE = BLOCK_ENTITY_TYPES.register("blood_sieve", () -> create(BloodSieveBlockEntity::new, ModBlocks.BLOOD_SIEVE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VaporStillBlockEntity>> VAPOR_STILL = BLOCK_ENTITY_TYPES.register("vapor_still", () -> create(VaporStillBlockEntity::new, ModBlocks.VAPOR_STILL.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyTableBlockEntity>> ALCHEMICAL_TABLE = BLOCK_ENTITY_TYPES.register("alchemical_table", () -> create(AlchemyTableBlockEntity::new, ModBlocks.ALCHEMY_TABLE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatCageBlockEntity>> BAT_CAGE = BLOCK_ENTITY_TYPES.register("bat_cage", () -> create(BatCageBlockEntity::new, ModBlocks.BAT_CAGE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MotherBlockEntity>> MOTHER = BLOCK_ENTITY_TYPES.register("mother", () -> create(MotherBlockEntity::new, ModBlocks.MOTHER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VulnerableRemainsBlockEntity>> VULNERABLE_CURSED_ROOTED_DIRT = BLOCK_ENTITY_TYPES.register("vulnerable_cursed_rooted_dirt", () -> create(VulnerableRemainsBlockEntity::new, ModBlocks.ACTIVE_VULNERABLE_REMAINS.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MotherTrophyBlockEntity>> MOTHER_TROPHY = BLOCK_ENTITY_TYPES.register("mother_trophy", () -> create(MotherTrophyBlockEntity::new, ModBlocks.MOTHER_TROPHY.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FogDiffuserBlockEntity>> FOG_DIFFUSER = BLOCK_ENTITY_TYPES.register("fog_diffuser", () -> create(FogDiffuserBlockEntity::new, ModBlocks.FOG_DIFFUSER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VampireBeaconBlockEntity>> VAMPIRE_BEACON = BLOCK_ENTITY_TYPES.register("vampire_beacon", () -> create(VampireBeaconBlockEntity::new, ModBlocks.VAMPIRE_BEACON.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserBlockEntity>> INFUSER = BLOCK_ENTITY_TYPES.register("infuser", () -> create(InfuserBlockEntity::new, ModBlocks.INFUSER.get()));

    private static <T extends BlockEntity> @NotNull BlockEntityType<T> create(BlockEntityType.@NotNull BlockEntitySupplier<T> factoryIn, Block... blocks) {
        return new BlockEntityType<>(factoryIn, blocks);
    }

    static void registerTileExtensions(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.SIGN, ModBlocks.DARK_SPRUCE_SIGN.get(), ModBlocks.CURSED_SPRUCE_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_SIGN.get());
        event.modify(BlockEntityType.HANGING_SIGN, ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get());
    }

    static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}

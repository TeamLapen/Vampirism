package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.*;
import de.teamlapen.vampirism.blockentity.diffuser.GarlicDiffuserBlockEntity;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.mixin.accessor.TileEntityTypeAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


public class ModTiles {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TentBlockEntity>> TENT = BLOCK_ENTITY_TYPES.register("tent", () -> create(TentBlockEntity::new, ModBlocks.TENT_MAIN.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoffinBlockEntity>> COFFIN = BLOCK_ENTITY_TYPES.register("coffin", () -> create(CoffinBlockEntity::new, CoffinBlock.COFFIN_BLOCKS.values().toArray(new Block[0])));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AltarInfusionBlockEntity>> ALTAR_INFUSION = BLOCK_ENTITY_TYPES.register("altar_infusion", () -> create(AltarInfusionBlockEntity::new, ModBlocks.ALTAR_INFUSION.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodContainerBlockEntity>> BLOOD_CONTAINER = BLOCK_ENTITY_TYPES.register("blood_container", () -> create(BloodContainerBlockEntity::new, ModBlocks.BLOOD_CONTAINER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AltarInspirationBlockEntity>> ALTAR_INSPIRATION = BLOCK_ENTITY_TYPES.register("altar_inspiration", () -> create(AltarInspirationBlockEntity::new, ModBlocks.ALTAR_INSPIRATION.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SunscreenBeaconBlockEntity>> SUNSCREEN_BEACON = BLOCK_ENTITY_TYPES.register("sunscreen_beacon", () -> create(SunscreenBeaconBlockEntity::new, ModBlocks.SUNSCREEN_BEACON.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemicalCauldronBlockEntity>> ALCHEMICAL_CAULDRON = BLOCK_ENTITY_TYPES.register("alchemical_cauldron", () -> create(AlchemicalCauldronBlockEntity::new, ModBlocks.ALCHEMICAL_CAULDRON.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GarlicDiffuserBlockEntity>> GARLIC_DIFFUSER = BLOCK_ENTITY_TYPES.register("garlic_diffuser", () -> create(GarlicDiffuserBlockEntity::new, ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), ModBlocks.GARLIC_DIFFUSER_WEAK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PedestalBlockEntity>> BLOOD_PEDESTAL = BLOCK_ENTITY_TYPES.register("blood_pedestal", () -> create(PedestalBlockEntity::new, ModBlocks.BLOOD_PEDESTAL.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodGrinderBlockEntity>> GRINDER = BLOCK_ENTITY_TYPES.register("grinder", () -> create(BloodGrinderBlockEntity::new, ModBlocks.BLOOD_GRINDER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SieveBlockEntity>> SIEVE = BLOCK_ENTITY_TYPES.register("sieve", () -> create(SieveBlockEntity::new, ModBlocks.BLOOD_SIEVE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TotemBlockEntity>> TOTEM = BLOCK_ENTITY_TYPES.register("totem", () -> create(TotemBlockEntity::new, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0])));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PotionTableBlockEntity>> POTION_TABLE = BLOCK_ENTITY_TYPES.register("potion_table", () -> create(PotionTableBlockEntity::new, ModBlocks.POTION_TABLE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyTableBlockEntity>> ALCHEMICAL_TABLE = BLOCK_ENTITY_TYPES.register("alchemical_table", () -> create(AlchemyTableBlockEntity::new, ModBlocks.ALCHEMY_TABLE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatCageBlockEntity>> BAT_CAGE = BLOCK_ENTITY_TYPES.register("bat_cage", () -> create(BatCageBlockEntity::new, ModBlocks.BAT_CAGE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MotherBlockEntity>> MOTHER = BLOCK_ENTITY_TYPES.register("mother", () -> create(MotherBlockEntity::new, ModBlocks.MOTHER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VulnerableRemainsBlockEntity>> VULNERABLE_CURSED_ROOTED_DIRT = BLOCK_ENTITY_TYPES.register("vulnerable_cursed_rooted_dirt", () -> create(VulnerableRemainsBlockEntity::new, ModBlocks.ACTIVE_VULNERABLE_REMAINS.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MotherTrophyBlockEntity>> MOTHER_TROPHY = BLOCK_ENTITY_TYPES.register("mother_trophy", () -> create(MotherTrophyBlockEntity::new, ModBlocks.MOTHER_TROPHY.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<de.teamlapen.vampirism.blockentity.diffuser.FogDiffuserBlockEntity>> FOG_DIFFUSER = BLOCK_ENTITY_TYPES.register("fog_diffuser", () -> create(de.teamlapen.vampirism.blockentity.diffuser.FogDiffuserBlockEntity::new, ModBlocks.FOG_DIFFUSER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VampireBeaconBlockEntity>> VAMPIRE_BEACON = BLOCK_ENTITY_TYPES.register("vampire_beacon", () -> create(VampireBeaconBlockEntity::new, ModBlocks.VAMPIRE_BEACON.get()));

    static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }

    private static <T extends BlockEntity> @NotNull BlockEntityType<T> create(BlockEntityType.@NotNull BlockEntitySupplier<T> factoryIn, Block... blocks) {
        return BlockEntityType.Builder.of(factoryIn, blocks).build(null);
    }

    public static void registerTileExtensionsUnsafe() {
        Set<Block> blocks = new HashSet<>(((TileEntityTypeAccessor) BlockEntityType.SIGN).getValidBlocks());
        blocks.add(ModBlocks.DARK_SPRUCE_SIGN.get());
        blocks.add(ModBlocks.CURSED_SPRUCE_SIGN.get());
        blocks.add(ModBlocks.DARK_SPRUCE_WALL_SIGN.get());
        blocks.add(ModBlocks.CURSED_SPRUCE_WALL_SIGN.get());
        ((TileEntityTypeAccessor) BlockEntityType.SIGN).setValidBlocks(blocks);

        blocks = new HashSet<>(((TileEntityTypeAccessor) BlockEntityType.HANGING_SIGN).getValidBlocks());
        blocks.add(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get());
        blocks.add(ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get());
        blocks.add(ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get());
        blocks.add(ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get());
        ((TileEntityTypeAccessor) BlockEntityType.HANGING_SIGN).setValidBlocks(blocks);

    }

}

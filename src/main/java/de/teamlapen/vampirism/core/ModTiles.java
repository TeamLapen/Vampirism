package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.mixin.TileEntityTypeAccessor;
import de.teamlapen.vampirism.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;


public class ModTiles {
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, REFERENCE.MODID);

    public static final RegistryObject<TileEntityType<TentTileEntity>> TENT = BLOCK_ENTITIES.register("tent", () -> create(TentTileEntity::new, ModBlocks.TENT_MAIN.get()));
    public static final RegistryObject<TileEntityType<CoffinTileEntity>> COFFIN = BLOCK_ENTITIES.register("coffin", () -> create(CoffinTileEntity::new, CoffinBlock.COFFIN_BLOCKS.values().toArray(new Block[0])));
    public static final RegistryObject<TileEntityType<AltarInfusionTileEntity>> ALTAR_INFUSION = BLOCK_ENTITIES.register("altar_infusion", () -> create(AltarInfusionTileEntity::new, ModBlocks.ALTAR_INFUSION.get()));
    public static final RegistryObject<TileEntityType<BloodContainerTileEntity>> BLOOD_CONTAINER = BLOCK_ENTITIES.register("blood_container", () -> create(BloodContainerTileEntity::new, ModBlocks.BLOOD_CONTAINER.get()));
    public static final RegistryObject<TileEntityType<AltarInspirationTileEntity>> ALTAR_INSPIRATION = BLOCK_ENTITIES.register("altar_inspiration", () -> create(AltarInspirationTileEntity::new, ModBlocks.ALTAR_INSPIRATION.get()));
    public static final RegistryObject<TileEntityType<SunscreenBeaconTileEntity>> SUNSCREEN_BEACON = BLOCK_ENTITIES.register("sunscreen_beacon", () -> create(SunscreenBeaconTileEntity::new, ModBlocks.SUNSCREEN_BEACON.get()));
    public static final RegistryObject<TileEntityType<AlchemicalCauldronTileEntity>> ALCHEMICAL_CAULDRON = BLOCK_ENTITIES.register("alchemical_cauldron", () -> create(AlchemicalCauldronTileEntity::new, ModBlocks.ALCHEMICAL_CAULDRON.get()));
    public static final RegistryObject<TileEntityType<GarlicBeaconTileEntity>> GARLIC_BEACON = BLOCK_ENTITIES.register("garlic_beacon", () -> create(GarlicBeaconTileEntity::new, ModBlocks.GARLIC_BEACON_NORMAL.get(), ModBlocks.GARLIC_BEACON_IMPROVED.get(), ModBlocks.GARLIC_BEACON_WEAK.get()));
    public static final RegistryObject<TileEntityType<PedestalTileEntity>> BLOOD_PEDESTAL = BLOCK_ENTITIES.register("blood_pedestal", () -> create(PedestalTileEntity::new, ModBlocks.BLOOD_PEDESTAL.get()));
    public static final RegistryObject<TileEntityType<BloodGrinderTileEntity>> GRINDER = BLOCK_ENTITIES.register("grinder", () -> create(BloodGrinderTileEntity::new, ModBlocks.BLOOD_GRINDER.get()));
    public static final RegistryObject<TileEntityType<SieveTileEntity>> SIEVE = BLOCK_ENTITIES.register("sieve", () -> create(SieveTileEntity::new, ModBlocks.BLOOD_SIEVE.get()));
    public static final RegistryObject<TileEntityType<TotemTileEntity>> TOTEM = BLOCK_ENTITIES.register("totem", () -> create(TotemTileEntity::new, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0])));
    public static final RegistryObject<TileEntityType<PotionTableTileEntity>> POTION_TABLE = BLOCK_ENTITIES.register("potion_table", () -> create(PotionTableTileEntity::new, ModBlocks.POTION_TABLE.get()));
    public static final RegistryObject<TileEntityType<AlchemyTableTileEntity>> ALCHEMICAL_TABLE = BLOCK_ENTITIES.register("alchemical_table", () -> create(AlchemyTableTileEntity::new, ModBlocks.ALCHEMY_TABLE.get()));

    static void registerTiles(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }

    public static void registerTileExtensionsUnsafe() {
        Set<Block> blocks = new HashSet<>(((TileEntityTypeAccessor) TileEntityType.SIGN).getValidBlocks());
        blocks.add(ModBlocks.DARK_SPRUCE_SIGN.get());
        blocks.add(ModBlocks.DARK_SPRUCE_WALL_SIGN.get());
        blocks.add(ModBlocks.CURSED_SPRUCE_SIGN.get());
        blocks.add(ModBlocks.CURSED_SPRUCE_WALL_SIGN.get());
        ((TileEntityTypeAccessor) TileEntityType.SIGN).setValidBlocks(blocks);
    }

    private static <T extends TileEntity> TileEntityType<T> create(Supplier<T> factoryIn, Block... blocks) {
        return TileEntityType.Builder.of(factoryIn, blocks).build(null);
    }
}

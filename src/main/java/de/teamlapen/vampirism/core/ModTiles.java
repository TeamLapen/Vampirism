package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.mixin.TileEntityTypeAccessor;
import de.teamlapen.vampirism.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


@ObjectHolder(REFERENCE.MODID)
public class ModTiles {

    public static final TileEntityType<AlchemicalCauldronTileEntity> alchemical_cauldron = getNull();
    public static final TileEntityType<TentTileEntity> tent = getNull();
    public static final TileEntityType<CoffinTileEntity> coffin = (TileEntityType<CoffinTileEntity>) create("coffin", CoffinTileEntity::new, CoffinBlock.COFFIN_BLOCKS.values().toArray(new Block[0]));
    public static final TileEntityType<AltarInfusionTileEntity> altar_infusion = getNull();
    public static final TileEntityType<BloodContainerTileEntity> blood_container = getNull();
    public static final TileEntityType<AltarInspirationTileEntity> altar_inspiration = getNull();
    public static final TileEntityType<SunscreenBeaconTileEntity> sunscreen_beacon = getNull();
    public static final TileEntityType<GarlicBeaconTileEntity> garlic_beacon = getNull();
    public static final TileEntityType<PedestalTileEntity> blood_pedestal = getNull();
    public static final TileEntityType<BloodGrinderTileEntity> grinder = getNull();
    public static final TileEntityType<SieveTileEntity> sieve = getNull();
    public static final TileEntityType<TotemTileEntity> totem = getNull();
    public static final TileEntityType<PotionTableTileEntity> potion_table = getNull();

    static void registerTiles(IForgeRegistry<TileEntityType<?>> registry) {
        registry.register(create("tent", TentTileEntity::new, ModBlocks.TENT_MAIN.get()));
        registry.register(coffin);
        registry.register(create("altar_infusion", AltarInfusionTileEntity::new, ModBlocks.ALTAR_INFUSION.get()));
        registry.register(create("blood_container", BloodContainerTileEntity::new, ModBlocks.BLOOD_CONTAINER.get()));
        registry.register(create("altar_inspiration", AltarInspirationTileEntity::new, ModBlocks.ALTAR_INSPIRATION.get()));
        registry.register(create("sunscreen_beacon", SunscreenBeaconTileEntity::new, ModBlocks.SUNSCREEN_BEACON.get()));
        registry.register(create("alchemical_cauldron", AlchemicalCauldronTileEntity::new, ModBlocks.ALCHEMICAL_CAULDRON.get()));
        registry.register(create("garlic_beacon", GarlicBeaconTileEntity::new, ModBlocks.GARLIC_BEACON_NORMAL.get(), ModBlocks.GARLIC_BEACON_IMPROVED.get(), ModBlocks.GARLIC_BEACON_WEAK.get()));
        registry.register(create("blood_pedestal", PedestalTileEntity::new, ModBlocks.BLOOD_PEDESTAL.get()));
        registry.register(create("grinder", BloodGrinderTileEntity::new, ModBlocks.BLOOD_GRINDER.get()));
        registry.register(create("sieve", SieveTileEntity::new, ModBlocks.BLOOD_SIEVE.get()));
        registry.register(create("totem", TotemTileEntity::new, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0])));
        registry.register(create("potion_table", PotionTableTileEntity::new, ModBlocks.POTION_TABLE.get()));
    }

    public static void registerTileExtensionsUnsafe() {
        Set<Block> blocks = new HashSet<>(((TileEntityTypeAccessor) TileEntityType.SIGN).getValidBlocks());
        blocks.add(ModBlocks.DARK_SPRUCE_SIGN.get());
        blocks.add(ModBlocks.CURSED_SPRUCE_SIGN.get());
        ((TileEntityTypeAccessor) TileEntityType.SIGN).setValidBlocks(blocks);
    }

    private static <T extends TileEntity> TileEntityType<?> create(String id, Supplier<? extends T> factoryIn, Block... blocks) {
        return TileEntityType.Builder.of(factoryIn, blocks).build(null).setRegistryName(REFERENCE.MODID, id);
    }
}

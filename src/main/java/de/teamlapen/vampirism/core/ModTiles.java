package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.tileentity.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


@ObjectHolder(REFERENCE.MODID)
public class ModTiles {

    public static final TileEntityType<AlchemicalCauldronTileEntity> alchemical_cauldron = getNull();
    public static final TileEntityType<TentTileEntity> tent = getNull();
    public static final TileEntityType<CoffinTileEntity> coffin = getNull();
    public static final TileEntityType<AltarInfusionTileEntity> altar_infusion = getNull();
    public static final TileEntityType<BloodContainerTileEntity> blood_container = getNull();
    public static final TileEntityType<AltarInspirationTileEntity> altar_inspiration = getNull();
    public static final TileEntityType<SunscreenBeaconTileEntity> sunscreen_beacon = getNull();
    public static final TileEntityType<GarlicBeaconTileEntity> garlic_beacon = getNull();
    public static final TileEntityType<PedestalTileEntity> blood_pedestal = getNull();
    public static final TileEntityType<BloodGrinderTileEntity> grinder = getNull();
    public static final TileEntityType<SieveTileEntity> sieve = getNull();
    public static final TileEntityType<TotemTile> totem = getNull();

    static void registerTiles(IForgeRegistry<TileEntityType<?>> registry) {
        registry.register(create("tent", TentTileEntity::new, ModBlocks.tent_main));
        registry.register(create("coffin", TentTileEntity::new, ModBlocks.block_coffin));
        registry.register(create("altar_infusion", AltarInfusionTileEntity::new, ModBlocks.altar_infusion));
        registry.register(create("blood_container", BloodContainerTileEntity::new, ModBlocks.blood_container));
        registry.register(create("altar_inspiration", AltarInspirationTileEntity::new, ModBlocks.altar_inspiration));
        registry.register(create("sunscreen_beacon", SunscreenBeaconTileEntity::new, ModBlocks.sunscreen_beacon));
        registry.register(create("alchemical_cauldron", AlchemicalCauldronTileEntity::new, ModBlocks.alchemical_cauldron));
        registry.register(create("garlic_beacon", GarlicBeaconTileEntity::new, ModBlocks.garlic_beacon_improved, ModBlocks.garlic_beacon_improved, ModBlocks.garlic_beacon_weak));
        registry.register(create("blood_pedestal", PedestalTileEntity::new, ModBlocks.blood_pedestal));
        registry.register(create("grinder", BloodGrinderTileEntity::new, ModBlocks.blood_grinder));
        registry.register(create("sieve", SieveTileEntity::new, ModBlocks.blood_sieve));
        registry.register(create("totem", TotemTile::new, ModBlocks.totem_top));
    }

    private static <T extends TileEntity> TileEntityType<?> create(String id, Supplier<? extends T> factoryIn, Block... blocks) {
        return TileEntityType.Builder.create(factoryIn, blocks).build(null).setRegistryName(REFERENCE.MODID, id);
    }
}

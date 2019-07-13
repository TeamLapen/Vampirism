package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.tileentity.*;
import de.teamlapen.vampirism.util.REFERENCE;
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
        registry.register(create("tent", TentTileEntity::new));
        registry.register(create("coffin", TentTileEntity::new));
        registry.register(create("altar_infusion", AltarInfusionTileEntity::new));
        registry.register(create("blood_container", BloodContainerTileEntity::new));
        registry.register(create("altar_inspiration", AltarInspirationTileEntity::new));
        registry.register(create("sunscreen_beacon", SunscreenBeaconTileEntity::new));
        registry.register(create("alchemical_cauldron", AlchemicalCauldronTileEntity::new));
        registry.register(create("garlic_beacon", GarlicBeaconTileEntity::new));
        registry.register(create("blood_pedestal", PedestalTileEntity::new));
        registry.register(create("grinder", BloodGrinderTileEntity::new));
        registry.register(create("sieve", SieveTileEntity::new));
        registry.register(create("totem", TotemTile::new));
    }

    private static <T extends TileEntity> TileEntityType<?> create(String id, Supplier<? extends T> factoryIn) {
        return TileEntityType.Builder.create(factoryIn).build(null).setRegistryName(REFERENCE.MODID, id);
    }
}

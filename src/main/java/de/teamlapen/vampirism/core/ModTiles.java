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

    public static final TileEntityType<TileAlchemicalCauldron> alchemical_cauldron = getNull();
    public static final TileEntityType<TileTent> tent = getNull();
    public static final TileEntityType<TileCoffin> coffin = getNull();
    public static final TileEntityType<TileAltarInfusion> altar_infusion = getNull();
    public static final TileEntityType<TileBloodContainer> blood_container = getNull();
    public static final TileEntityType<TileAltarInspiration> altar_inspiration = getNull();
    public static final TileEntityType<TileSunscreenBeacon> sunscreen_beacon = getNull();
    public static final TileEntityType<TileGarlicBeacon> garlic_beacon = getNull();
    public static final TileEntityType<TilePedestal> blood_pedestal = getNull();
    public static final TileEntityType<TileGrinder> grinder = getNull();
    public static final TileEntityType<TileSieve> sieve = getNull();
    public static final TileEntityType<TileTotem> totem = getNull();

    static void registerTiles(IForgeRegistry<TileEntityType<?>> registry) {
        registry.register(create("tent", TileTent::new));
        registry.register(create("coffin", TileTent::new));
        registry.register(create("altar_infusion", TileAltarInfusion::new));
        registry.register(create("blood_container", TileBloodContainer::new));
        registry.register(create("altar_inspiration", TileAltarInspiration::new));
        registry.register(create("sunscreen_beacon", TileSunscreenBeacon::new));
        registry.register(create("alchemical_cauldron", TileAlchemicalCauldron::new));
        registry.register(create("garlic_beacon", TileGarlicBeacon::new));
        registry.register(create("blood_pedestal", TilePedestal::new));
        registry.register(create("grinder", TileGrinder::new));
        registry.register(create("sieve", TileSieve::new));
        registry.register(create("totem", TileTotem::new));
    }

    private static <T extends TileEntity> TileEntityType<?> create(String id, Supplier<? extends T> factoryIn) {
        return TileEntityType.Builder.create(factoryIn).build(null).setRegistryName(REFERENCE.MODID, id);
    }
}

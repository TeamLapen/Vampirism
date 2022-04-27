package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
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
    public static final TileEntityType<CoffinTileEntity> coffin = getNull();
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
    public static final TileEntityType<AlchemyTableTileEntity> alchemical_table = getNull();

    static void registerTiles(IForgeRegistry<TileEntityType<?>> registry) {
        registry.register(create("tent", TentTileEntity::new, ModBlocks.tent_main));
        registry.register(create("coffin", CoffinTileEntity::new, ModBlocks.coffin));
        registry.register(create("altar_infusion", AltarInfusionTileEntity::new, ModBlocks.altar_infusion));
        registry.register(create("blood_container", BloodContainerTileEntity::new, ModBlocks.blood_container));
        registry.register(create("altar_inspiration", AltarInspirationTileEntity::new, ModBlocks.altar_inspiration));
        registry.register(create("sunscreen_beacon", SunscreenBeaconTileEntity::new, ModBlocks.sunscreen_beacon));
        registry.register(create("alchemical_cauldron", AlchemicalCauldronTileEntity::new, ModBlocks.alchemical_cauldron));
        registry.register(create("garlic_beacon", GarlicBeaconTileEntity::new, ModBlocks.garlic_beacon_normal, ModBlocks.garlic_beacon_improved, ModBlocks.garlic_beacon_weak));
        registry.register(create("blood_pedestal", PedestalTileEntity::new, ModBlocks.blood_pedestal));
        registry.register(create("grinder", BloodGrinderTileEntity::new, ModBlocks.blood_grinder));
        registry.register(create("sieve", SieveTileEntity::new, ModBlocks.blood_sieve));
        registry.register(create("totem", TotemTileEntity::new, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0])));
        registry.register(create("potion_table", PotionTableTileEntity::new, ModBlocks.potion_table));
        registry.register(create("alchemical_table", AlchemyTableTileEntity::new, ModBlocks.alchemy_table));
    }

    public static void registerTileExtensionsUnsafe() {
        Set<Block> blocks = new HashSet<>(((TileEntityTypeAccessor) TileEntityType.SIGN).getValidBlocks());
        blocks.add(ModBlocks.dark_spruce_sign);
        blocks.add(ModBlocks.cursed_spruce_sign);
        ((TileEntityTypeAccessor) TileEntityType.SIGN).setValidBlocks(blocks);
    }

    private static <T extends TileEntity> TileEntityType<?> create(String id, Supplier<? extends T> factoryIn, Block... blocks) {
        return TileEntityType.Builder.of(factoryIn, blocks).build(null).setRegistryName(REFERENCE.MODID, id);
    }
}

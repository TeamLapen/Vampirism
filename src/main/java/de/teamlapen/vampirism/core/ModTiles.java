package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.tileentity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.function.Supplier;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


@ObjectHolder(REFERENCE.MODID)
public class ModTiles {

    public static final BlockEntityType<AlchemicalCauldronTileEntity> alchemical_cauldron = getNull();
    public static final BlockEntityType<TentTileEntity> tent = getNull();
    public static final BlockEntityType<CoffinTileEntity> coffin = getNull();
    public static final BlockEntityType<AltarInfusionTileEntity> altar_infusion = getNull();
    public static final BlockEntityType<BloodContainerTileEntity> blood_container = getNull();
    public static final BlockEntityType<AltarInspirationTileEntity> altar_inspiration = getNull();
    public static final BlockEntityType<SunscreenBeaconTileEntity> sunscreen_beacon = getNull();
    public static final BlockEntityType<GarlicBeaconTileEntity> garlic_beacon = getNull();
    public static final BlockEntityType<PedestalTileEntity> blood_pedestal = getNull();
    public static final BlockEntityType<BloodGrinderTileEntity> grinder = getNull();
    public static final BlockEntityType<SieveTileEntity> sieve = getNull();
    public static final BlockEntityType<TotemTileEntity> totem = getNull();
    public static final BlockEntityType<PotionTableTileEntity> potion_table = getNull();

    static void registerTiles(IForgeRegistry<BlockEntityType<?>> registry) {
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
    }

    private static <T extends BlockEntity> BlockEntityType<?> create(String id, Supplier<? extends T> factoryIn, Block... blocks) {
        return BlockEntityType.Builder.of(factoryIn, blocks).build(null).setRegistryName(REFERENCE.MODID, id);
    }
}

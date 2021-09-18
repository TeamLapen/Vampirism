package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.*;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


@ObjectHolder(REFERENCE.MODID)
public class ModTiles {

    public static final BlockEntityType<AlchemicalCauldronBlockEntity> alchemical_cauldron = getNull();
    public static final BlockEntityType<TentBlockEntity> tent = getNull();
    public static final BlockEntityType<CoffinBlockEntity> coffin = getNull();
    public static final BlockEntityType<AltarInfusionBlockEntity> altar_infusion = getNull();
    public static final BlockEntityType<BloodContainerBlockEntity> blood_container = getNull();
    public static final BlockEntityType<AltarInspirationBlockEntity> altar_inspiration = getNull();
    public static final BlockEntityType<SunscreenBeaconBlockEntity> sunscreen_beacon = getNull();
    public static final BlockEntityType<GarlicDiffusorBlockEntity> garlic_beacon = getNull();
    public static final BlockEntityType<PedestalBlockEntity> blood_pedestal = getNull();
    public static final BlockEntityType<BloodGrinderBlockEntity> grinder = getNull();
    public static final BlockEntityType<SieveBlockEntity> sieve = getNull();
    public static final BlockEntityType<TotemBlockEntity> totem = getNull();
    public static final BlockEntityType<PotionTableBlockEntity> potion_table = getNull();

    static void registerTiles(IForgeRegistry<BlockEntityType<?>> registry) {
        registry.register(create("tent", TentBlockEntity::new, ModBlocks.tent_main));
        registry.register(create("coffin", CoffinBlockEntity::new, ModBlocks.coffin));
        registry.register(create("altar_infusion", AltarInfusionBlockEntity::new, ModBlocks.altar_infusion));
        registry.register(create("blood_container", BloodContainerBlockEntity::new, ModBlocks.blood_container));
        registry.register(create("altar_inspiration", AltarInspirationBlockEntity::new, ModBlocks.altar_inspiration));
        registry.register(create("sunscreen_beacon", SunscreenBeaconBlockEntity::new, ModBlocks.sunscreen_beacon));
        registry.register(create("alchemical_cauldron", AlchemicalCauldronBlockEntity::new, ModBlocks.alchemical_cauldron));
        registry.register(create("garlic_beacon", GarlicDiffusorBlockEntity::new, ModBlocks.garlic_diffusor_normal, ModBlocks.garlic_diffusor_improved, ModBlocks.garlic_diffusor_weak));
        registry.register(create("blood_pedestal", PedestalBlockEntity::new, ModBlocks.blood_pedestal));
        registry.register(create("grinder", BloodGrinderBlockEntity::new, ModBlocks.blood_grinder));
        registry.register(create("sieve", SieveBlockEntity::new, ModBlocks.blood_sieve));
        registry.register(create("totem", TotemBlockEntity::new, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0])));
        registry.register(create("potion_table", PotionTableBlockEntity::new, ModBlocks.potion_table));
    }

    private static <T extends BlockEntity> BlockEntityType<?> create(String id, BlockEntityType.BlockEntitySupplier<? extends T> factoryIn, Block... blocks) {
        return BlockEntityType.Builder.of(factoryIn, blocks).build(null).setRegistryName(REFERENCE.MODID, id);
    }
}

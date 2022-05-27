package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blockentity.*;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModTiles {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, REFERENCE.MODID);

    public static final RegistryObject<BlockEntityType<TentBlockEntity>> TENT = BLOCK_ENTITY_TYPES.register("tent", () -> create(TentBlockEntity::new, ModBlocks.TENT_MAIN.get()));
    public static final RegistryObject<BlockEntityType<CoffinBlockEntity>> COFFIN = BLOCK_ENTITY_TYPES.register("coffin", () -> create(CoffinBlockEntity::new, ModBlocks.COFFIN.get()));
    public static final RegistryObject<BlockEntityType<AltarInfusionBlockEntity>> ALTAR_INFUSION = BLOCK_ENTITY_TYPES.register("altar_infusion", () -> create(AltarInfusionBlockEntity::new, ModBlocks.ALTAR_INFUSION.get()));
    public static final RegistryObject<BlockEntityType<BloodContainerBlockEntity>> BLOOD_CONTAINER = BLOCK_ENTITY_TYPES.register("blood_container", () -> create(BloodContainerBlockEntity::new, ModBlocks.BLOOD_CONTAINER.get()));
    public static final RegistryObject<BlockEntityType<AltarInspirationBlockEntity>> ALTAR_INSPIRATION = BLOCK_ENTITY_TYPES.register("altar_inspiration", () -> create(AltarInspirationBlockEntity::new, ModBlocks.ALTAR_INSPIRATION.get()));
    public static final RegistryObject<BlockEntityType<SunscreenBeaconBlockEntity>> SUNSCREEN_BEACON = BLOCK_ENTITY_TYPES.register("sunscreen_beacon", () -> create(SunscreenBeaconBlockEntity::new, ModBlocks.SUNSCREEN_BEACON.get()));
    public static final RegistryObject<BlockEntityType<AlchemicalCauldronBlockEntity>> ALCHEMICAL_CAULDRON = BLOCK_ENTITY_TYPES.register("alchemical_cauldron", () -> create(AlchemicalCauldronBlockEntity::new, ModBlocks.ALCHEMICAL_CAULDRON.get()));
    public static final RegistryObject<BlockEntityType<GarlicDiffuserBlockEntity>> GARLIC_DIFFUSER = BLOCK_ENTITY_TYPES.register("garlic_diffuser", () -> create(GarlicDiffuserBlockEntity::new, ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), ModBlocks.GARLIC_DIFFUSER_WEAK.get()));
    public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> BLOOD_PEDESTAL = BLOCK_ENTITY_TYPES.register("blood_pedestal", () -> create(PedestalBlockEntity::new, ModBlocks.BLOOD_PEDESTAL.get()));
    public static final RegistryObject<BlockEntityType<BloodGrinderBlockEntity>> GRINDER = BLOCK_ENTITY_TYPES.register("grinder", () -> create(BloodGrinderBlockEntity::new, ModBlocks.BLOOD_GRINDER.get()));
    public static final RegistryObject<BlockEntityType<SieveBlockEntity>> SIEVE = BLOCK_ENTITY_TYPES.register("sieve", () -> create(SieveBlockEntity::new, ModBlocks.BLOOD_SIEVE.get()));
    public static final RegistryObject<BlockEntityType<TotemBlockEntity>> TOTEM = BLOCK_ENTITY_TYPES.register("totem", () -> create(TotemBlockEntity::new, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0])));
    public static final RegistryObject<BlockEntityType<PotionTableBlockEntity>> POTION_TABLE = BLOCK_ENTITY_TYPES.register("potion_table", () -> create(PotionTableBlockEntity::new, ModBlocks.POTION_TABLE.get()));

    static void registerTiles(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityType.BlockEntitySupplier<T> factoryIn, Block... blocks) {
        return BlockEntityType.Builder.of(factoryIn, blocks).build(null);
    }

    public static void fixMappings(RegistryEvent.MissingMappings<BlockEntityType<?>> event) {
        event.getAllMappings().forEach(missingMapping -> {
            switch (missingMapping.key.toString()) {
               case "vampirism:garlic_beacon": missingMapping.remap(GARLIC_DIFFUSER.get());
            }
        });
    }
}

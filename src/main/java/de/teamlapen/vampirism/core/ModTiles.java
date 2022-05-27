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

    public static final RegistryObject<BlockEntityType<TentBlockEntity>> tent = BLOCK_ENTITY_TYPES.register("tent", () -> create(TentBlockEntity::new, ModBlocks.tent_main.get()));
    public static final RegistryObject<BlockEntityType<CoffinBlockEntity>> coffin = BLOCK_ENTITY_TYPES.register("coffin", () -> create(CoffinBlockEntity::new, ModBlocks.coffin.get()));
    public static final RegistryObject<BlockEntityType<AltarInfusionBlockEntity>> altar_infusion = BLOCK_ENTITY_TYPES.register("altar_infusion", () -> create(AltarInfusionBlockEntity::new, ModBlocks.altar_infusion.get()));
    public static final RegistryObject<BlockEntityType<BloodContainerBlockEntity>> blood_container = BLOCK_ENTITY_TYPES.register("blood_container", () -> create(BloodContainerBlockEntity::new, ModBlocks.blood_container.get()));
    public static final RegistryObject<BlockEntityType<AltarInspirationBlockEntity>> altar_inspiration = BLOCK_ENTITY_TYPES.register("altar_inspiration", () -> create(AltarInspirationBlockEntity::new, ModBlocks.altar_inspiration.get()));
    public static final RegistryObject<BlockEntityType<SunscreenBeaconBlockEntity>> sunscreen_beacon = BLOCK_ENTITY_TYPES.register("sunscreen_beacon", () -> create(SunscreenBeaconBlockEntity::new, ModBlocks.sunscreen_beacon.get()));
    public static final RegistryObject<BlockEntityType<AlchemicalCauldronBlockEntity>> alchemical_cauldron = BLOCK_ENTITY_TYPES.register("alchemical_cauldron", () -> create(AlchemicalCauldronBlockEntity::new, ModBlocks.alchemical_cauldron.get()));
    public static final RegistryObject<BlockEntityType<GarlicDiffuserBlockEntity>> garlic_diffuser = BLOCK_ENTITY_TYPES.register("garlic_diffuser", () -> create(GarlicDiffuserBlockEntity::new, ModBlocks.garlic_diffuser_normal.get(), ModBlocks.garlic_diffuser_improved.get(), ModBlocks.garlic_diffuser_weak.get()));
    public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> blood_pedestal = BLOCK_ENTITY_TYPES.register("blood_pedestal", () -> create(PedestalBlockEntity::new, ModBlocks.blood_pedestal.get()));
    public static final RegistryObject<BlockEntityType<BloodGrinderBlockEntity>> grinder = BLOCK_ENTITY_TYPES.register("grinder", () -> create(BloodGrinderBlockEntity::new, ModBlocks.blood_grinder.get()));
    public static final RegistryObject<BlockEntityType<SieveBlockEntity>> sieve = BLOCK_ENTITY_TYPES.register("sieve", () -> create(SieveBlockEntity::new, ModBlocks.blood_sieve.get()));
    public static final RegistryObject<BlockEntityType<TotemBlockEntity>> totem = BLOCK_ENTITY_TYPES.register("totem", () -> create(TotemBlockEntity::new, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0])));
    public static final RegistryObject<BlockEntityType<PotionTableBlockEntity>> potion_table = BLOCK_ENTITY_TYPES.register("potion_table", () -> create(PotionTableBlockEntity::new, ModBlocks.potion_table.get()));

    static void registerTiles(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityType.BlockEntitySupplier<T> factoryIn, Block... blocks) {
        return BlockEntityType.Builder.of(factoryIn, blocks).build(null);
    }

    public static void fixMappings(RegistryEvent.MissingMappings<BlockEntityType<?>> event) {
        event.getAllMappings().forEach(missingMapping -> {
            switch (missingMapping.key.toString()) {
               case "vampirism:garlic_beacon": missingMapping.remap(garlic_diffuser.get());
            }
        });
    }
}

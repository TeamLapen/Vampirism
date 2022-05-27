package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.*;
import de.teamlapen.vampirism.entity.hunter.*;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.vampire.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Handles all entity registrations and reference.
 */
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, REFERENCE.MODID);

    public static final RegistryObject<EntityType<BasicHunterEntity>> hunter = prepareEntityType("hunter", ()->EntityType.Builder.of(BasicHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);;
    public static final RegistryObject<EntityType<HunterTrainerEntity>> hunter_trainer = prepareEntityType("hunter_trainer", ()->EntityType.Builder.of(HunterTrainerEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<AdvancedHunterEntity>> advanced_hunter = prepareEntityType("advanced_hunter", ()->EntityType.Builder.of(AdvancedHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<VampireBaronEntity>> vampire_baron = prepareEntityType("vampire_baron", ()->EntityType.Builder.of(VampireBaronEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<BasicVampireEntity>> vampire = prepareEntityType("vampire", ()->EntityType.Builder.of(BasicVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<AdvancedVampireEntity>> advanced_vampire = prepareEntityType("advanced_vampire", ()->EntityType.Builder.of(AdvancedVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<ConvertedCreatureEntity<?>>> converted_creature = prepareEntityType("converted_creature", ()->EntityType.Builder.of(ConvertedCreatureEntity::new, MobCategory.CREATURE), false);
    public static final RegistryObject<EntityType<DummyBittenAnimalEntity>> dummy_creature = prepareEntityType("dummy_creature", ()->EntityType.Builder.of(DummyBittenAnimalEntity::new, MobCategory.CREATURE), true);
    public static final RegistryObject<EntityType<BlindingBatEntity>> blinding_bat = prepareEntityType("blinding_bat", ()->EntityType.Builder.of(BlindingBatEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.9F), true);
    public static final RegistryObject<EntityType<AdvancedHunterEntity.IMob>> advanced_hunter_imob = prepareEntityType("advanced_hunter_imob", ()->EntityType.Builder.of(AdvancedHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<AdvancedVampireEntity.IMob>> advanced_vampire_imob = prepareEntityType("advanced_vampire_imob", ()->EntityType.Builder.of(AdvancedVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<ConvertedCreatureEntity.IMob>> converted_creature_imob = prepareEntityType("converted_creature_imob", ()->EntityType.Builder.of(ConvertedCreatureEntity.IMob::new, MobCategory.CREATURE), false);
    public static final RegistryObject<EntityType<ConvertedSheepEntity>> converted_sheep = prepareEntityType("converted_sheep", ()->EntityType.Builder.of(ConvertedSheepEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F), false);
    public static final RegistryObject<EntityType<ConvertedCowEntity>> converted_cow = prepareEntityType("converted_cow", ()->EntityType.Builder.of(ConvertedCowEntity::new, MobCategory.CREATURE).sized(0.9F, 1.4F), false);
    public static final RegistryObject<EntityType<CrossbowArrowEntity>> crossbow_arrow = prepareEntityType("crossbow_arrow", ()->EntityType.Builder.<CrossbowArrowEntity>of(CrossbowArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory((spawnEntity, world) -> new CrossbowArrowEntity(ModEntities.crossbow_arrow.get(), world)), false);
    public static final RegistryObject<EntityType<DarkBloodProjectileEntity>> dark_blood_projectile = prepareEntityType("dark_blood_projectile", ()->EntityType.Builder.<DarkBloodProjectileEntity>of(DarkBloodProjectileEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new DarkBloodProjectileEntity(ModEntities.dark_blood_projectile.get(), world)), false);
    public static final RegistryObject<EntityType<DummyHunterTrainerEntity>> hunter_trainer_dummy = prepareEntityType("hunter_trainer_dummy", ()->EntityType.Builder.of(DummyHunterTrainerEntity::new, MobCategory.MISC).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<AreaParticleCloudEntity>> particle_cloud = prepareEntityType("particle_cloud", ()->EntityType.Builder.of(AreaParticleCloudEntity::new, MobCategory.MISC).sized(6.0F, 0.5F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new AreaParticleCloudEntity(ModEntities.particle_cloud.get(), world)), false);
    public static final RegistryObject<EntityType<SoulOrbEntity>> soul_orb = prepareEntityType("soul_orb", ()->EntityType.Builder.<SoulOrbEntity>of(SoulOrbEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new SoulOrbEntity(ModEntities.soul_orb.get(), world)), false);
    public static final RegistryObject<EntityType<ThrowableItemEntity>> throwable_item = prepareEntityType("throwable_item", ()->EntityType.Builder.<ThrowableItemEntity>of(ThrowableItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).setCustomClientFactory((spawnEntity, world) -> new ThrowableItemEntity(ModEntities.throwable_item.get(), world)), false);
    public static final RegistryObject<EntityType<BasicVampireEntity.IMob>> vampire_imob = prepareEntityType("vampire_imob", ()->EntityType.Builder.of(BasicVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<BasicHunterEntity.IMob>> hunter_imob = prepareEntityType("hunter_imob", ()->EntityType.Builder.of(BasicHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<AggressiveVillagerEntity>> villager_angry = prepareEntityType("villager_angry", ()->EntityType.Builder.of(AggressiveVillagerEntity::new, MobCategory.CREATURE).sized(0.6F, 1.95F), false);
    public static final RegistryObject<EntityType<ConvertedVillagerEntity>> villager_converted = prepareEntityType("villager_converted", ()->EntityType.Builder.of(ConvertedVillagerEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), false);
    public static final RegistryObject<EntityType<ConvertedHorseEntity>> converted_horse = prepareEntityType("converted_horse", ()->EntityType.Builder.of(ConvertedHorseEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F), false);
    public static final RegistryObject<EntityType<VampireMinionEntity>> vampire_minion = prepareEntityType("vampire_minion", ()->EntityType.Builder.of(VampireMinionEntity::new, MobCategory.CREATURE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<ConvertedDonkeyEntity>> converted_donkey = prepareEntityType("converted_donkey", ()->EntityType.Builder.of(ConvertedDonkeyEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F), false);
    public static final RegistryObject<EntityType<ConvertedMuleEntity>> converted_mule = prepareEntityType("converted_mule", ()->EntityType.Builder.of(ConvertedMuleEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F), false);
    public static final RegistryObject<EntityType<HunterMinionEntity>> hunter_minion = prepareEntityType("hunter_minion", ()->EntityType.Builder.of(HunterMinionEntity::new, MobCategory.CREATURE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<VampireTaskMasterEntity>> task_master_vampire = prepareEntityType("task_master_vampire", ()->EntityType.Builder.of(VampireTaskMasterEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), true);
    public static final RegistryObject<EntityType<HunterTaskMasterEntity>> task_master_hunter = prepareEntityType("task_master_hunter", ()->EntityType.Builder.of(HunterTaskMasterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), true);

    /**
     * Registers special extended creature classes
     */
    static void registerCustomExtendedCreatures() {
        IVampirismEntityRegistry registry = VampirismAPI.entityRegistry();
    }

    /**
     * Register convertibles for vanilla creatures and maybe for future vampirism creature as well
     */
    static void registerConvertibles() {
        Function<String, ResourceLocation> overlay = (String name) -> new ResourceLocation(REFERENCE.MODID, String.format("textures/entity/vanilla/%s_overlay.png", name));
        IVampirismEntityRegistry registry = VampirismAPI.entityRegistry();

        registry.addConvertible(EntityType.COW, overlay.apply("cow"), new ConvertedCowEntity.ConvertingHandler());
        registry.addConvertible(EntityType.LLAMA, overlay.apply("llama"));
        registry.addConvertible(EntityType.OCELOT, overlay.apply("cat"));
        registry.addConvertible(EntityType.PANDA, overlay.apply("panda"));
        registry.addConvertible(EntityType.PIG, overlay.apply("pig"));
        registry.addConvertible(EntityType.POLAR_BEAR, overlay.apply("polarbear"));
        registry.addConvertible(EntityType.RABBIT, overlay.apply("rabbit"));
        registry.addConvertible(EntityType.SHEEP, overlay.apply("sheep"), new ConvertedSheepEntity.ConvertingHandler());
        registry.addConvertible(EntityType.VILLAGER, null, new ConvertedVillagerEntity.ConvertingHandler());
        registry.addConvertible(EntityType.HORSE, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.converted_horse.get()));
        registry.addConvertible(EntityType.DONKEY, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.converted_donkey.get()));
        registry.addConvertible(EntityType.MULE, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.converted_mule.get()));
    }

    static void registerEntities(IEventBus bus) {
        ENTITY_TYPES.register(bus);
        //VampireMinionEntity.init();
        //HunterMinionEntity.init();
    }

    static void initializeEntities() {
        VampireMinionEntity.init();
        HunterMinionEntity.init();
    }

    static void registerSpawns() {
        SpawnPlacements.register(advanced_hunter.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter);
        SpawnPlacements.register(advanced_vampire.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire);
        SpawnPlacements.register(blinding_bat.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlindingBatEntity::spawnPredicate);
        SpawnPlacements.register(dummy_creature.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DummyBittenAnimalEntity::spawnPredicate);
        SpawnPlacements.register(converted_creature.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        SpawnPlacements.register(converted_sheep.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        SpawnPlacements.register(converted_cow.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        SpawnPlacements.register(hunter_trainer.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(hunter_trainer_dummy.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(vampire.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire);
        SpawnPlacements.register(vampire_baron.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaronEntity::spawnPredicateBaron);
        SpawnPlacements.register(hunter.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter);
        SpawnPlacements.register(villager_angry.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(villager_converted.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
    }

    static void onRegisterEntityTypeAttributes(EntityAttributeCreationEvent event) {
        event.put(advanced_hunter.get(), AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(advanced_hunter_imob.get(), AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(advanced_vampire.get(), AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(advanced_vampire_imob.get(), AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(blinding_bat.get(), Bat.createAttributes().build());
        event.put(converted_creature.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(converted_creature_imob.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(converted_horse.get(), ConvertedHorseEntity.getAttributeBuilder().build());
        event.put(converted_sheep.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(converted_cow.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(converted_donkey.get(), ConvertedDonkeyEntity.getAttributeBuilder().build());
        event.put(converted_mule.get(), ConvertedMuleEntity.getAttributeBuilder().build());
        event.put(dummy_creature.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(hunter.get(), BasicHunterEntity.getAttributeBuilder().build());
        event.put(hunter_imob.get(), BasicHunterEntity.getAttributeBuilder().build());
        event.put(hunter_trainer.get(), HunterTrainerEntity.getAttributeBuilder().build());
        event.put(hunter_trainer_dummy.get(), HunterTrainerEntity.getAttributeBuilder().build());
        event.put(vampire.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(vampire_imob.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(vampire_baron.get(), VampireBaronEntity.getAttributeBuilder().build());
        event.put(villager_angry.get(), AggressiveVillagerEntity.getAttributeBuilder().build());
        event.put(villager_converted.get(), ConvertedVillagerEntity.getAttributeBuilder().build());
        event.put(hunter_minion.get(), HunterMinionEntity.getAttributeBuilder().build());
        event.put(vampire_minion.get(), VampireMinionEntity.getAttributeBuilder().build());
        event.put(task_master_hunter.get(), HunterTaskMasterEntity.getAttributeBuilder().build());
        event.put(task_master_vampire.get(), VampireTaskMasterEntity.getAttributeBuilder().build());
    }

    static void onModifyEntityTypeAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.sundamage.get());
        event.add(EntityType.PLAYER, ModAttributes.blood_exhaustion.get());
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> prepareEntityType(String id, Supplier<EntityType.Builder<T>> builder, boolean spawnable) {
        return ENTITY_TYPES.register(id, () -> {
            EntityType.Builder<T> type = builder.get().setTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true);
            if (!spawnable)
                type.noSummon();
            return type.build(REFERENCE.MODID + ":" + id);
        });
    }

    static void fixMapping(RegistryEvent.MissingMappings<EntityType<?>> missingMappings) {
        missingMappings.getAllMappings().forEach((mapping) -> {
            if (mapping.key.equals(new ResourceLocation("vampirism:vampire_hunter"))) {
                mapping.remap(ModEntities.hunter.get());
            } else if (mapping.key.equals(new ResourceLocation("vampirism:vampire_hunter_imob"))) {
                mapping.remap(ModEntities.hunter_imob.get());
            }
        });
    }


    public static Set<EntityType<?>> getAllEntities() {
        return ENTITY_TYPES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toUnmodifiableSet());
    }
}

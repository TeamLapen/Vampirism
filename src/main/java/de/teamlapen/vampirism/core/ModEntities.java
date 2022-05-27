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

    public static final RegistryObject<EntityType<BasicHunterEntity>> HUNTER = prepareEntityType("hunter", ()->EntityType.Builder.of(BasicHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);;
    public static final RegistryObject<EntityType<HunterTrainerEntity>> HUNTER_TRAINER = prepareEntityType("hunter_trainer", ()->EntityType.Builder.of(HunterTrainerEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<AdvancedHunterEntity>> ADVANCED_HUNTER = prepareEntityType("advanced_hunter", ()->EntityType.Builder.of(AdvancedHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<VampireBaronEntity>> VAMPIRE_BARON = prepareEntityType("vampire_baron", ()->EntityType.Builder.of(VampireBaronEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<BasicVampireEntity>> VAMPIRE = prepareEntityType("vampire", ()->EntityType.Builder.of(BasicVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<AdvancedVampireEntity>> ADVANCED_VAMPIRE = prepareEntityType("advanced_vampire", ()->EntityType.Builder.of(AdvancedVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<ConvertedCreatureEntity<?>>> CONVERTED_CREATURE = prepareEntityType("converted_creature", ()->EntityType.Builder.of(ConvertedCreatureEntity::new, MobCategory.CREATURE), false);
    public static final RegistryObject<EntityType<DummyBittenAnimalEntity>> DUMMY_CREATURE = prepareEntityType("dummy_creature", ()->EntityType.Builder.of(DummyBittenAnimalEntity::new, MobCategory.CREATURE), true);
    public static final RegistryObject<EntityType<BlindingBatEntity>> BLINDING_BAT = prepareEntityType("blinding_bat", ()->EntityType.Builder.of(BlindingBatEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.9F), true);
    public static final RegistryObject<EntityType<AdvancedHunterEntity.IMob>> ADVANCED_HUNTER_IMOB = prepareEntityType("advanced_hunter_imob", ()->EntityType.Builder.of(AdvancedHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<AdvancedVampireEntity.IMob>> ADVANCED_VAMPIRE_IMOB = prepareEntityType("advanced_vampire_imob", ()->EntityType.Builder.of(AdvancedVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<ConvertedCreatureEntity.IMob>> CONVERTED_CREATURE_IMOB = prepareEntityType("converted_creature_imob", ()->EntityType.Builder.of(ConvertedCreatureEntity.IMob::new, MobCategory.CREATURE), false);
    public static final RegistryObject<EntityType<ConvertedSheepEntity>> CONVERTED_SHEEP = prepareEntityType("converted_sheep", ()->EntityType.Builder.of(ConvertedSheepEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F), false);
    public static final RegistryObject<EntityType<ConvertedCowEntity>> CONVERTED_COW = prepareEntityType("converted_cow", ()->EntityType.Builder.of(ConvertedCowEntity::new, MobCategory.CREATURE).sized(0.9F, 1.4F), false);
    public static final RegistryObject<EntityType<CrossbowArrowEntity>> CROSSBOW_ARROW = prepareEntityType("crossbow_arrow", ()->EntityType.Builder.<CrossbowArrowEntity>of(CrossbowArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory((spawnEntity, world) -> new CrossbowArrowEntity(ModEntities.CROSSBOW_ARROW.get(), world)), false);
    public static final RegistryObject<EntityType<DarkBloodProjectileEntity>> DARK_BLOOD_PROJECTILE = prepareEntityType("dark_blood_projectile", ()->EntityType.Builder.<DarkBloodProjectileEntity>of(DarkBloodProjectileEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new DarkBloodProjectileEntity(ModEntities.DARK_BLOOD_PROJECTILE.get(), world)), false);
    public static final RegistryObject<EntityType<DummyHunterTrainerEntity>> HUNTER_TRAINER_DUMMY = prepareEntityType("hunter_trainer_dummy", ()->EntityType.Builder.of(DummyHunterTrainerEntity::new, MobCategory.MISC).sized(0.6F, 1.95F), true);
    public static final RegistryObject<EntityType<AreaParticleCloudEntity>> PARTICLE_CLOUD = prepareEntityType("particle_cloud", ()->EntityType.Builder.of(AreaParticleCloudEntity::new, MobCategory.MISC).sized(6.0F, 0.5F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new AreaParticleCloudEntity(ModEntities.PARTICLE_CLOUD.get(), world)), false);
    public static final RegistryObject<EntityType<SoulOrbEntity>> SOUL_ORB = prepareEntityType("soul_orb", ()->EntityType.Builder.<SoulOrbEntity>of(SoulOrbEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new SoulOrbEntity(ModEntities.SOUL_ORB.get(), world)), false);
    public static final RegistryObject<EntityType<ThrowableItemEntity>> THROWABLE_ITEM = prepareEntityType("throwable_item", ()->EntityType.Builder.<ThrowableItemEntity>of(ThrowableItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).setCustomClientFactory((spawnEntity, world) -> new ThrowableItemEntity(ModEntities.THROWABLE_ITEM.get(), world)), false);
    public static final RegistryObject<EntityType<BasicVampireEntity.IMob>> VAMPIRE_IMOB = prepareEntityType("vampire_imob", ()->EntityType.Builder.of(BasicVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<BasicHunterEntity.IMob>> HUNTER_IMOB = prepareEntityType("hunter_imob", ()->EntityType.Builder.of(BasicHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<AggressiveVillagerEntity>> VILLAGER_ANGRY = prepareEntityType("villager_angry", ()->EntityType.Builder.of(AggressiveVillagerEntity::new, MobCategory.CREATURE).sized(0.6F, 1.95F), false);
    public static final RegistryObject<EntityType<ConvertedVillagerEntity>> VILLAGER_CONVERTED = prepareEntityType("villager_converted", ()->EntityType.Builder.of(ConvertedVillagerEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), false);
    public static final RegistryObject<EntityType<ConvertedHorseEntity>> CONVERTED_HORSE = prepareEntityType("converted_horse", ()->EntityType.Builder.of(ConvertedHorseEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F), false);
    public static final RegistryObject<EntityType<VampireMinionEntity>> VAMPIRE_MINION = prepareEntityType("vampire_minion", ()->EntityType.Builder.of(VampireMinionEntity::new, MobCategory.CREATURE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<ConvertedDonkeyEntity>> CONVERTED_DONKEY = prepareEntityType("converted_donkey", ()->EntityType.Builder.of(ConvertedDonkeyEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F), false);
    public static final RegistryObject<EntityType<ConvertedMuleEntity>> CONVERTED_MULE = prepareEntityType("converted_mule", ()->EntityType.Builder.of(ConvertedMuleEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F), false);
    public static final RegistryObject<EntityType<HunterMinionEntity>> HUNTER_MINION = prepareEntityType("hunter_minion", ()->EntityType.Builder.of(HunterMinionEntity::new, MobCategory.CREATURE).sized(0.6f, 1.95f), false);
    public static final RegistryObject<EntityType<VampireTaskMasterEntity>> TASK_MASTER_VAMPIRE = prepareEntityType("task_master_vampire", ()->EntityType.Builder.of(VampireTaskMasterEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), true);
    public static final RegistryObject<EntityType<HunterTaskMasterEntity>> TASK_MASTER_HUNTER = prepareEntityType("task_master_hunter", ()->EntityType.Builder.of(HunterTaskMasterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), true);

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
        registry.addConvertible(EntityType.HORSE, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.CONVERTED_HORSE.get()));
        registry.addConvertible(EntityType.DONKEY, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.CONVERTED_DONKEY.get()));
        registry.addConvertible(EntityType.MULE, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.CONVERTED_MULE.get()));
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
        SpawnPlacements.register(ADVANCED_HUNTER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter);
        SpawnPlacements.register(ADVANCED_VAMPIRE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire);
        SpawnPlacements.register(BLINDING_BAT.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlindingBatEntity::spawnPredicate);
        SpawnPlacements.register(DUMMY_CREATURE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DummyBittenAnimalEntity::spawnPredicate);
        SpawnPlacements.register(CONVERTED_CREATURE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        SpawnPlacements.register(CONVERTED_SHEEP.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        SpawnPlacements.register(CONVERTED_COW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        SpawnPlacements.register(HUNTER_TRAINER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(HUNTER_TRAINER_DUMMY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(VAMPIRE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire);
        SpawnPlacements.register(VAMPIRE_BARON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaronEntity::spawnPredicateBaron);
        SpawnPlacements.register(HUNTER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter);
        SpawnPlacements.register(VILLAGER_ANGRY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        SpawnPlacements.register(VILLAGER_CONVERTED.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
    }

    static void onRegisterEntityTypeAttributes(EntityAttributeCreationEvent event) {
        event.put(ADVANCED_HUNTER.get(), AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(ADVANCED_HUNTER_IMOB.get(), AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(ADVANCED_VAMPIRE.get(), AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(ADVANCED_VAMPIRE_IMOB.get(), AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(BLINDING_BAT.get(), Bat.createAttributes().build());
        event.put(CONVERTED_CREATURE.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(CONVERTED_CREATURE_IMOB.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(CONVERTED_HORSE.get(), ConvertedHorseEntity.getAttributeBuilder().build());
        event.put(CONVERTED_SHEEP.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(CONVERTED_COW.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(CONVERTED_DONKEY.get(), ConvertedDonkeyEntity.getAttributeBuilder().build());
        event.put(CONVERTED_MULE.get(), ConvertedMuleEntity.getAttributeBuilder().build());
        event.put(DUMMY_CREATURE.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(HUNTER.get(), BasicHunterEntity.getAttributeBuilder().build());
        event.put(HUNTER_IMOB.get(), BasicHunterEntity.getAttributeBuilder().build());
        event.put(HUNTER_TRAINER.get(), HunterTrainerEntity.getAttributeBuilder().build());
        event.put(HUNTER_TRAINER_DUMMY.get(), HunterTrainerEntity.getAttributeBuilder().build());
        event.put(VAMPIRE.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(VAMPIRE_IMOB.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(VAMPIRE_BARON.get(), VampireBaronEntity.getAttributeBuilder().build());
        event.put(VILLAGER_ANGRY.get(), AggressiveVillagerEntity.getAttributeBuilder().build());
        event.put(VILLAGER_CONVERTED.get(), ConvertedVillagerEntity.getAttributeBuilder().build());
        event.put(HUNTER_MINION.get(), HunterMinionEntity.getAttributeBuilder().build());
        event.put(VAMPIRE_MINION.get(), VampireMinionEntity.getAttributeBuilder().build());
        event.put(TASK_MASTER_HUNTER.get(), HunterTaskMasterEntity.getAttributeBuilder().build());
        event.put(TASK_MASTER_VAMPIRE.get(), VampireTaskMasterEntity.getAttributeBuilder().build());
    }

    static void onModifyEntityTypeAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.SUNDAMAGE.get());
        event.add(EntityType.PLAYER, ModAttributes.BLOOD_EXHAUSTION.get());
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
                mapping.remap(ModEntities.HUNTER.get());
            } else if (mapping.key.equals(new ResourceLocation("vampirism:vampire_hunter_imob"))) {
                mapping.remap(ModEntities.HUNTER_IMOB.get());
            }
        });
    }


    public static Set<EntityType<?>> getAllEntities() {
        return ENTITY_TYPES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toUnmodifiableSet());
    }
}

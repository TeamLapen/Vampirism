package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.*;
import de.teamlapen.vampirism.entity.hunter.*;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.vampire.*;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Set;
import java.util.function.Function;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all entity registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModEntities {
    public static final EntityType<AdvancedHunterEntity> advanced_hunter;
    public static final EntityType<AdvancedHunterEntity.IMob> advanced_hunter_imob = getNull();
    public static final EntityType<AdvancedVampireEntity> advanced_vampire;
    public static final EntityType<AdvancedVampireEntity.IMob> advanced_vampire_imob = getNull();
    public static final EntityType<BlindingBatEntity> blinding_bat;
    public static final EntityType<ConvertedCreatureEntity> converted_creature;
    public static final EntityType<ConvertedCreatureEntity.IMob> converted_creature_imob = getNull();
    public static final EntityType<ConvertedSheepEntity> converted_sheep = getNull();
    public static final EntityType<ConvertedCowEntity> converted_cow = getNull();
    public static final EntityType<ConvertedDonkeyEntity> converted_donkey = getNull();
    public static final EntityType<ConvertedMuleEntity> converted_mule = getNull();
    public static final EntityType<ConvertedHorseEntity> converted_horse = getNull();
    public static final EntityType<CrossbowArrowEntity> crossbow_arrow = getNull();
    public static final EntityType<DarkBloodProjectileEntity> dark_blood_projectile = getNull();
    public static final EntityType<DummyBittenAnimalEntity> dummy_creature;
    public static final EntityType<HunterTrainerEntity> hunter_trainer;
    public static final EntityType<DummyHunterTrainerEntity> hunter_trainer_dummy = getNull();
    public static final EntityType<AreaParticleCloudEntity> particle_cloud = getNull();
    public static final EntityType<SoulOrbEntity> soul_orb = getNull();
    public static final EntityType<ThrowableItemEntity> throwable_item = getNull();
    public static final EntityType<BasicVampireEntity> vampire;
    public static final EntityType<BasicVampireEntity.IMob> vampire_imob = getNull();
    public static final EntityType<VampireBaronEntity> vampire_baron;
    public static final EntityType<BasicHunterEntity> hunter;
    public static final EntityType<BasicHunterEntity.IMob> hunter_imob = getNull();
    public static final EntityType<AggressiveVillagerEntity> villager_angry = getNull();
    public static final EntityType<ConvertedVillagerEntity> villager_converted = getNull();
    public static final EntityType<VampireMinionEntity> vampire_minion = getNull();
    public static final EntityType<HunterMinionEntity> hunter_minion = getNull();
    public static final EntityType<VampireTaskMasterEntity> task_master_vampire = getNull();
    public static final EntityType<HunterTaskMasterEntity> task_master_hunter = getNull();
    /**
     * empty unless in datagen
     */
    private static final Set<EntityType<?>> ALL_ENTITIES = Sets.newHashSet();

    static {
        //IMPORTANT - Must include all entity types that are used in vampire forest spawns
        hunter = prepareEntityType("hunter", EntityType.Builder.of(BasicHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);
        hunter_trainer = prepareEntityType("hunter_trainer", EntityType.Builder.of(HunterTrainerEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);
        advanced_hunter = prepareEntityType("advanced_hunter", EntityType.Builder.of(AdvancedHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6F, 1.95F), true);
        vampire_baron = prepareEntityType("vampire_baron", EntityType.Builder.of(VampireBaronEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
        vampire = prepareEntityType("vampire", EntityType.Builder.of(BasicVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
        advanced_vampire = prepareEntityType("advanced_vampire", EntityType.Builder.of(AdvancedVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), true);
        converted_creature = prepareEntityType("converted_creature", EntityType.Builder.of(ConvertedCreatureEntity::new, EntityClassification.CREATURE), false);
        dummy_creature = prepareEntityType("dummy_creature", EntityType.Builder.of(DummyBittenAnimalEntity::new, EntityClassification.CREATURE), true);
        blinding_bat = prepareEntityType("blinding_bat", EntityType.Builder.of(BlindingBatEntity::new, EntityClassification.AMBIENT).sized(0.5F, 0.9F), true);
    }

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
        registry.addConvertible(EntityType.HORSE, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.converted_horse));
        registry.addConvertible(EntityType.DONKEY, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.converted_donkey));
        registry.addConvertible(EntityType.MULE, overlay.apply("horse"), new SpecialConvertingHandler<>(ModEntities.converted_mule));
    }

    static void registerEntities(IForgeRegistry<EntityType<?>> registry) {
        //simply register EntityType
        registry.register(advanced_hunter);
        registry.register(prepareEntityType("advanced_hunter_imob", EntityType.Builder.of(AdvancedHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), false));
        registry.register(advanced_vampire);
        registry.register(prepareEntityType("advanced_vampire_imob", EntityType.Builder.of(AdvancedVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), false));
        registry.register(blinding_bat);
        registry.register(converted_creature);
        registry.register(prepareEntityType("converted_creature_imob", EntityType.Builder.of(ConvertedCreatureEntity.IMob::new, EntityClassification.CREATURE), false));
        registry.register(prepareEntityType("converted_sheep", EntityType.Builder.of(ConvertedSheepEntity::new, EntityClassification.CREATURE).sized(0.9F, 1.3F), false));
        registry.register(prepareEntityType("converted_cow", EntityType.Builder.of(ConvertedCowEntity::new, EntityClassification.CREATURE).sized(0.9F, 1.4F), false));
        registry.register(prepareEntityType("crossbow_arrow", EntityType.Builder.<CrossbowArrowEntity>of(CrossbowArrowEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).setCustomClientFactory((spawnEntity, world) -> new CrossbowArrowEntity(ModEntities.crossbow_arrow, world)), false));
        registry.register(prepareEntityType("dark_blood_projectile", EntityType.Builder.<DarkBloodProjectileEntity>of(DarkBloodProjectileEntity::new, EntityClassification.MISC).sized(0.6F, 1.95F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new DarkBloodProjectileEntity(ModEntities.dark_blood_projectile, world)), false));
        registry.register(dummy_creature);
        registry.register(hunter_trainer);
        registry.register(prepareEntityType("hunter_trainer_dummy", EntityType.Builder.of(DummyHunterTrainerEntity::new, EntityClassification.MISC).sized(0.6F, 1.95F), true));
        registry.register(prepareEntityType("particle_cloud", EntityType.Builder.of(AreaParticleCloudEntity::new, EntityClassification.MISC).sized(6.0F, 0.5F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new AreaParticleCloudEntity(ModEntities.particle_cloud, world)), false));
        registry.register(prepareEntityType("soul_orb", EntityType.Builder.<SoulOrbEntity>of(SoulOrbEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).fireImmune().setCustomClientFactory((spawnEntity, world) -> new SoulOrbEntity(ModEntities.soul_orb, world)), false));
        registry.register(prepareEntityType("throwable_item", EntityType.Builder.<ThrowableItemEntity>of(ThrowableItemEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).setCustomClientFactory((spawnEntity, world) -> new ThrowableItemEntity(ModEntities.throwable_item, world)), false));
        registry.register(vampire);
        registry.register(prepareEntityType("vampire_imob", EntityType.Builder.of(BasicVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), false));
        registry.register(vampire_baron);
        registry.register(hunter);
        registry.register(prepareEntityType("hunter_imob", EntityType.Builder.of(BasicHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), false));
        registry.register(prepareEntityType("villager_angry", EntityType.Builder.of(AggressiveVillagerEntity::new, EntityClassification.CREATURE).sized(0.6F, 1.95F), false));
        registry.register(prepareEntityType("villager_converted", EntityType.Builder.of(ConvertedVillagerEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6F, 1.95F), false));
        registry.register(prepareEntityType("converted_horse", EntityType.Builder.of(ConvertedHorseEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.6F), false));
        registry.register(prepareEntityType("vampire_minion", EntityType.Builder.of(VampireMinionEntity::new, EntityClassification.CREATURE).sized(0.6f, 1.95f), false));
        registry.register(prepareEntityType("converted_donkey", EntityType.Builder.of(ConvertedDonkeyEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.5F), false));
        registry.register(prepareEntityType("converted_mule", EntityType.Builder.of(ConvertedMuleEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.5F), false));
        VampireMinionEntity.init();
        HunterMinionEntity.init();
        registry.register(prepareEntityType("hunter_minion", EntityType.Builder.of(HunterMinionEntity::new, EntityClassification.CREATURE).sized(0.6f, 1.95f), false));
        registry.register(prepareEntityType("task_master_vampire", EntityType.Builder.of(VampireTaskMasterEntity::new, VReference.VAMPIRE_CREATURE_TYPE).sized(0.6f, 1.95f), true));
        registry.register(prepareEntityType("task_master_hunter", EntityType.Builder.of(HunterTaskMasterEntity::new, VReference.HUNTER_CREATURE_TYPE).sized(0.6f, 1.95f), true));
    }

    static void registerSpawns() {
        EntitySpawnPlacementRegistry.register(advanced_hunter, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter);
        EntitySpawnPlacementRegistry.register(advanced_vampire, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire);
        EntitySpawnPlacementRegistry.register(blinding_bat, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlindingBatEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(dummy_creature, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DummyBittenAnimalEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(converted_creature, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(converted_sheep, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(converted_cow, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(hunter_trainer, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::checkMobSpawnRules);
        EntitySpawnPlacementRegistry.register(hunter_trainer_dummy, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::checkMobSpawnRules);
        EntitySpawnPlacementRegistry.register(vampire, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire);
        EntitySpawnPlacementRegistry.register(vampire_baron, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampireBaronEntity::spawnPredicateBaron);
        EntitySpawnPlacementRegistry.register(hunter, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter);
        EntitySpawnPlacementRegistry.register(villager_angry, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::checkMobSpawnRules);
        EntitySpawnPlacementRegistry.register(villager_converted, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::checkMobSpawnRules);
    }

    static void onRegisterEntityTypeAttributes(EntityAttributeCreationEvent event) {
        event.put(advanced_hunter, AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(advanced_hunter_imob, AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(advanced_vampire, AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(advanced_vampire_imob, AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(blinding_bat, BatEntity.createAttributes().build());
        event.put(converted_creature, BasicVampireEntity.getAttributeBuilder().build());
        event.put(converted_creature_imob, BasicVampireEntity.getAttributeBuilder().build());
        event.put(converted_horse, ConvertedHorseEntity.getAttributeBuilder().build());
        event.put(converted_sheep, BasicVampireEntity.getAttributeBuilder().build());
        event.put(converted_cow, BasicVampireEntity.getAttributeBuilder().build());
        event.put(converted_donkey, ConvertedDonkeyEntity.getAttributeBuilder().build());
        event.put(converted_mule, ConvertedMuleEntity.getAttributeBuilder().build());
        event.put(dummy_creature, BasicVampireEntity.getAttributeBuilder().build());
        event.put(hunter, BasicHunterEntity.getAttributeBuilder().build());
        event.put(hunter_imob, BasicHunterEntity.getAttributeBuilder().build());
        event.put(hunter_trainer, HunterTrainerEntity.getAttributeBuilder().build());
        event.put(hunter_trainer_dummy, HunterTrainerEntity.getAttributeBuilder().build());
        event.put(vampire, BasicVampireEntity.getAttributeBuilder().build());
        event.put(vampire_imob, BasicVampireEntity.getAttributeBuilder().build());
        event.put(vampire_baron, VampireBaronEntity.getAttributeBuilder().build());
        event.put(villager_angry, AggressiveVillagerEntity.getAttributeBuilder().build());
        event.put(villager_converted, ConvertedVillagerEntity.getAttributeBuilder().build());
        event.put(hunter_minion, HunterMinionEntity.getAttributeBuilder().build());
        event.put(vampire_minion, VampireMinionEntity.getAttributeBuilder().build());
        event.put(task_master_hunter, HunterTaskMasterEntity.getAttributeBuilder().build());
        event.put(task_master_vampire, VampireTaskMasterEntity.getAttributeBuilder().build());
    }

    static void onModifyEntityTypeAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.sundamage);
        event.add(EntityType.PLAYER, ModAttributes.blood_exhaustion);
        event.add(EntityType.PLAYER, ModAttributes.bite_damage);

    }

    private static <T extends Entity> EntityType<T> prepareEntityType(String id, EntityType.Builder<T> builder, boolean spawnable) {
        EntityType.Builder<T> type = builder.setTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true);
        if (!spawnable)
            type.noSummon();
        EntityType<T> entry = type.build(REFERENCE.MODID + ":" + id);
        entry.setRegistryName(REFERENCE.MODID, id);
        if (VampirismMod.inDataGen) {
            ALL_ENTITIES.add(entry);
        }
        return entry;
    }

    static void fixMapping(RegistryEvent.MissingMappings<EntityType<?>> missingMappings) {
        missingMappings.getAllMappings().forEach((mapping) -> {
            if (mapping.key.equals(new ResourceLocation("vampirism:vampire_hunter"))) {
                mapping.remap(ModEntities.hunter);
            } else if (mapping.key.equals(new ResourceLocation("vampirism:vampire_hunter_imob"))) {
                mapping.remap(ModEntities.hunter_imob);
            }
        });
    }


    public static Set<EntityType<?>> getAllEntities() {
        return ImmutableSet.copyOf(ALL_ENTITIES);
    }
}

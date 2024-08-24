package de.teamlapen.vampirism.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.advancements.critereon.FactionSubPredicate;
import de.teamlapen.vampirism.advancements.critereon.PlayerFactionSubPredicate;
import de.teamlapen.vampirism.api.VEnums;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.datamaps.EntityExistsCondition;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.*;
import de.teamlapen.vampirism.entity.converted.converter.DefaultConverter;
import de.teamlapen.vampirism.entity.converted.converter.SpecialConverter;
import de.teamlapen.vampirism.entity.hunter.*;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.vampire.*;
import de.teamlapen.vampirism.sit.SitEntity;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Handles all entity registrations and reference.
 */
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends Converter>> CONVERTING_HELPER = DeferredRegister.create(VampirismRegistries.Keys.ENTITY_CONVERTER, REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATES = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, REFERENCE.MODID);
    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, REFERENCE.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<BasicHunterEntity>> HUNTER = prepareEntityType("hunter", () -> EntityType.Builder.of(BasicHunterEntity::new, VEnums.HUNTER_CATEGORY.getValue()).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<HunterTrainerEntity>> HUNTER_TRAINER = prepareEntityType("hunter_trainer", () -> EntityType.Builder.of(HunterTrainerEntity::new, VEnums.HUNTER_CATEGORY.getValue()).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedHunterEntity>> ADVANCED_HUNTER = prepareEntityType("advanced_hunter", () -> EntityType.Builder.of(AdvancedHunterEntity::new, VEnums.HUNTER_CATEGORY.getValue()).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<VampireBaronEntity>> VAMPIRE_BARON = prepareEntityType("vampire_baron", () -> EntityType.Builder.of(VampireBaronEntity::new, VEnums.VAMPIRE_CATEGORY.getValue()).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<BasicVampireEntity>> VAMPIRE = prepareEntityType("vampire", () -> EntityType.Builder.of(BasicVampireEntity::new, VEnums.VAMPIRE_CATEGORY.getValue()).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedVampireEntity>> ADVANCED_VAMPIRE = prepareEntityType("advanced_vampire", () -> EntityType.Builder.of(AdvancedVampireEntity::new, VEnums.VAMPIRE_CATEGORY.getValue()).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCreatureEntity<?>>> CONVERTED_CREATURE = prepareEntityType("converted_creature", () -> EntityType.Builder.of(ConvertedCreatureEntity::new, MobCategory.CREATURE), false);
    public static final DeferredHolder<EntityType<?>, EntityType<DummyBittenAnimalEntity>> DUMMY_CREATURE = prepareEntityType("dummy_creature", () -> EntityType.Builder.of(DummyBittenAnimalEntity::new, MobCategory.CREATURE), true);
    public static final DeferredHolder<EntityType<?>, EntityType<BlindingBatEntity>> BLINDING_BAT = prepareEntityType("blinding_bat", () -> EntityType.Builder.of(BlindingBatEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.9F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedHunterEntity.IMob>> ADVANCED_HUNTER_IMOB = prepareEntityType("advanced_hunter_imob", () -> EntityType.Builder.of(AdvancedHunterEntity.IMob::new, VEnums.HUNTER_CATEGORY.getValue()).sized(0.6f, 1.95f), false);
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedVampireEntity.IMob>> ADVANCED_VAMPIRE_IMOB = prepareEntityType("advanced_vampire_imob", () -> EntityType.Builder.of(AdvancedVampireEntity.IMob::new, VEnums.VAMPIRE_CATEGORY.getValue()).sized(0.6f, 1.95f), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCreatureEntity.IMob<?>>> CONVERTED_CREATURE_IMOB = prepareEntityType("converted_creature_imob", () -> EntityType.Builder.of(ConvertedCreatureEntity.IMob::new, MobCategory.CREATURE), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedSheepEntity>> CONVERTED_SHEEP = prepareEntityType("converted_sheep", () -> EntityType.Builder.of(ConvertedSheepEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCowEntity>> CONVERTED_COW = prepareEntityType("converted_cow", () -> EntityType.Builder.of(ConvertedCowEntity::new, MobCategory.CREATURE).sized(0.9F, 1.4F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<CrossbowArrowEntity>> CROSSBOW_ARROW = prepareEntityType("crossbow_arrow", () -> EntityType.Builder.<CrossbowArrowEntity>of(CrossbowArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<DarkBloodProjectileEntity>> DARK_BLOOD_PROJECTILE = prepareEntityType("dark_blood_projectile", () -> EntityType.Builder.<DarkBloodProjectileEntity>of(DarkBloodProjectileEntity::new, MobCategory.MISC).sized(0.6F, 0.6F).fireImmune(), false);
    public static final DeferredHolder<EntityType<?>, EntityType<DummyHunterTrainerEntity>> HUNTER_TRAINER_DUMMY = prepareEntityType("hunter_trainer_dummy", () -> EntityType.Builder.of(DummyHunterTrainerEntity::new, MobCategory.MISC).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<AreaParticleCloudEntity>> PARTICLE_CLOUD = prepareEntityType("particle_cloud", () -> EntityType.Builder.of(AreaParticleCloudEntity::new, MobCategory.MISC).sized(6.0F, 0.5F).fireImmune(), false);
    public static final DeferredHolder<EntityType<?>, EntityType<SoulOrbEntity>> SOUL_ORB = prepareEntityType("soul_orb", () -> EntityType.Builder.<SoulOrbEntity>of(SoulOrbEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).fireImmune(), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ThrowableItemEntity>> THROWABLE_ITEM = prepareEntityType("throwable_item", () -> EntityType.Builder.<ThrowableItemEntity>of(ThrowableItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<BasicVampireEntity.IMob>> VAMPIRE_IMOB = prepareEntityType("vampire_imob", () -> EntityType.Builder.of(BasicVampireEntity.IMob::new, VEnums.VAMPIRE_CATEGORY.getValue()).sized(0.6f, 1.95f), false);
    public static final DeferredHolder<EntityType<?>, EntityType<BasicHunterEntity.IMob>> HUNTER_IMOB = prepareEntityType("hunter_imob", () -> EntityType.Builder.of(BasicHunterEntity.IMob::new, VEnums.HUNTER_CATEGORY.getValue()).sized(0.6f, 1.95f), false);
    public static final DeferredHolder<EntityType<?>, EntityType<AggressiveVillagerEntity>> VILLAGER_ANGRY = prepareEntityType("villager_angry", () -> EntityType.Builder.of(AggressiveVillagerEntity::new, MobCategory.CREATURE).sized(0.6F, 1.95F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedVillagerEntity>> VILLAGER_CONVERTED = prepareEntityType("villager_converted", () -> EntityType.Builder.of(ConvertedVillagerEntity::new, VEnums.VAMPIRE_CATEGORY.getValue()).sized(0.6F, 1.95F), true);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedHorseEntity>> CONVERTED_HORSE = prepareEntityType("converted_horse", () -> EntityType.Builder.of(ConvertedHorseEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<VampireMinionEntity>> VAMPIRE_MINION = prepareEntityType("vampire_minion", () -> EntityType.Builder.of(VampireMinionEntity::new, MobCategory.CREATURE).sized(0.6f, 1.95f), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedDonkeyEntity>> CONVERTED_DONKEY = prepareEntityType("converted_donkey", () -> EntityType.Builder.of(ConvertedDonkeyEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedMuleEntity>> CONVERTED_MULE = prepareEntityType("converted_mule", () -> EntityType.Builder.of(ConvertedMuleEntity::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<HunterMinionEntity>> HUNTER_MINION = prepareEntityType("hunter_minion", () -> EntityType.Builder.of(HunterMinionEntity::new, MobCategory.CREATURE).sized(0.6f, 1.95f), false);
    public static final DeferredHolder<EntityType<?>, EntityType<VampireTaskMasterEntity>> TASK_MASTER_VAMPIRE = prepareEntityType("task_master_vampire", () -> EntityType.Builder.of(VampireTaskMasterEntity::new, VEnums.VAMPIRE_CATEGORY.getValue()).sized(0.6f, 1.95f), true);
    public static final DeferredHolder<EntityType<?>, EntityType<HunterTaskMasterEntity>> TASK_MASTER_HUNTER = prepareEntityType("task_master_hunter", () -> EntityType.Builder.of(HunterTaskMasterEntity::new, VEnums.HUNTER_CATEGORY.getValue()).sized(0.6f, 1.95f), true);
    public static final DeferredHolder<EntityType<?>, EntityType<SitEntity>> dummy_sit_entity = prepareEntityType("dummy_sit_entity", () -> EntityType.Builder.of(SitEntity::new, MobCategory.MISC).sized(0.0001f, 0.0001f).setTrackingRange(256).setUpdateInterval(20), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedFoxEntity>> CONVERTED_FOX = prepareEntityType("converted_fox", () -> EntityType.Builder.of(ConvertedFoxEntity::new, MobCategory.CREATURE).sized(0.6F, 0.7F).immuneTo(Blocks.SWEET_BERRY_BUSH), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedGoatEntity>> CONVERTED_GOAT = prepareEntityType("converted_goat", () -> EntityType.Builder.of(ConvertedGoatEntity::new, MobCategory.CREATURE).sized(0.9F, 1.3F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<VulnerableRemainsDummyEntity>> VULNERABLE_REMAINS_DUMMY = prepareEntityType("vulnerable_remains_dummy", () -> EntityType.Builder.of(VulnerableRemainsDummyEntity::new, MobCategory.MISC).sized(1.02f, 1.02f).setTrackingRange(10).setUpdateInterval(20), false);
    public static final DeferredHolder<EntityType<?>, EntityType<RemainsDefenderEntity>> REMAINS_DEFENDER = prepareEntityType("remains_defender", () -> EntityType.Builder.of(RemainsDefenderEntity::new, MobCategory.MISC).sized(0.3f, 0.3f).setTrackingRange(10).setUpdateInterval(20), false);
    public static final DeferredHolder<EntityType<?>, EntityType<GhostEntity>> GHOST = prepareEntityType("ghost", () -> EntityType.Builder.of(GhostEntity::new, VEnums.VAMPIRE_CATEGORY.getValue()).sized(0.35F, 0.5F).setTrackingRange(10).setUpdateInterval(20).fireImmune(), true);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCamelEntity>> CONVERTED_CAMEL = prepareEntityType("converted_camel", () -> EntityType.Builder.of(ConvertedCamelEntity::new, MobCategory.CREATURE).sized(1.7F, 2.375F), false);
    public static final DeferredHolder<EntityType<?>, EntityType<ConvertedCatEntity>> CONVERTED_CAT = prepareEntityType("converted_cat", () -> EntityType.Builder.of(ConvertedCatEntity::new, MobCategory.CREATURE).sized(0.6F, 0.7F), false);


    public static final DeferredHolder<MapCodec<? extends Converter>, MapCodec<? extends Converter>> DEFAULT_CONVERTER = CONVERTING_HELPER.register("default", () -> DefaultConverter.CODEC);
    public static final DeferredHolder<MapCodec<? extends Converter>, MapCodec<? extends Converter>> SPECIAL_CONVERTER = CONVERTING_HELPER.register("special", () -> SpecialConverter.CODEC);

    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<PlayerFactionSubPredicate>> PLAYER_FACTION_SUB_PREDICATE = ENTITY_SUB_PREDICATES.register("player_faction", () -> PlayerFactionSubPredicate.CODEC);
    public static final DeferredHolder<MapCodec<? extends EntitySubPredicate>, MapCodec<FactionSubPredicate>> FACTION_SUB_PREDICATE = ENTITY_SUB_PREDICATES.register("faction", () -> FactionSubPredicate.CODEC);

    @SuppressWarnings("unused")
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<? extends ICondition>> ENTITY_EXISTS = CONDITIONS.register("entity_exists", () -> EntityExistsCondition.CODEC);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Item>> ITEM_DATA = DATA_SERIALIZER.register("item", () -> (EntityDataSerializer.ForValueType<Item>) (() -> ByteBufCodecs.registry(Registries.ITEM)));

    static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
        CONVERTING_HELPER.register(bus);
        ENTITY_SUB_PREDICATES.register(bus);
        CONDITIONS.register(bus);
        DATA_SERIALIZER.register(bus);
    }

    static void onRegisterSpawns(@NotNull RegisterSpawnPlacementsEvent event) {
        event.register(ADVANCED_HUNTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ADVANCED_VAMPIRE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(BLINDING_BAT.get(), SpawnPlacementTypes.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlindingBatEntity::spawnPredicate, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(DUMMY_CREATURE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DummyBittenAnimalEntity::spawnPredicate, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_CREATURE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_SHEEP.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedSheepEntity::checkConvertedSheepSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_COW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCowEntity::checkConvertedCowSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(HUNTER_TRAINER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(HUNTER_TRAINER_DUMMY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VAMPIRE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VAMPIRE_BARON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VampireBaronEntity::spawnPredicateBaron, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(HUNTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VILLAGER_ANGRY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(VILLAGER_CONVERTED.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_HORSE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedHorseEntity::checkConvertedHorseSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_DONKEY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedDonkeyEntity::checkConvertedDonkeySpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_MULE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedMuleEntity::checkConvertedMuleSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_FOX.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedFoxEntity::checkConvertedFoxSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_GOAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedGoatEntity::checkConvertedGoatSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_CAMEL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCamelEntity::checkConvertedCamelSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(CONVERTED_CAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ConvertedCatEntity::checkConvertedCatSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
    }

    static void onRegisterEntityTypeAttributes(@NotNull EntityAttributeCreationEvent event) {
        event.put(ADVANCED_HUNTER.get(), AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(ADVANCED_HUNTER_IMOB.get(), AdvancedHunterEntity.getAttributeBuilder().build());
        event.put(ADVANCED_VAMPIRE.get(), AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(ADVANCED_VAMPIRE_IMOB.get(), AdvancedVampireEntity.getAttributeBuilder().build());
        event.put(BLINDING_BAT.get(), Bat.createAttributes().build());
        event.put(CONVERTED_CREATURE.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(CONVERTED_CREATURE_IMOB.get(), BasicVampireEntity.getAttributeBuilder().build());
        event.put(CONVERTED_HORSE.get(), ConvertedHorseEntity.getAttributeBuilder().build());
        event.put(CONVERTED_SHEEP.get(), ConvertedSheepEntity.getAttributeBuilder().build());
        event.put(CONVERTED_COW.get(), ConvertedCowEntity.getAttributeBuilder().build());
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
        event.put(CONVERTED_FOX.get(), ConvertedFoxEntity.createAttributes().build());
        event.put(CONVERTED_GOAT.get(), ConvertedGoatEntity.createAttributes().build());
        event.put(VULNERABLE_REMAINS_DUMMY.get(), VulnerableRemainsDummyEntity.createAttributes().build());
        event.put(REMAINS_DEFENDER.get(), RemainsDefenderEntity.createAttributes().build());
        event.put(GHOST.get(), GhostEntity.createAttributes().build());
        event.put(CONVERTED_CAMEL.get(), ConvertedCamelEntity.getAttributeBuilder().build());
        event.put(CONVERTED_CAT.get(), ConvertedCatEntity.createAttributes().build());
    }

    static void onModifyEntityTypeAttributes(@NotNull EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.SUNDAMAGE);
        event.add(EntityType.PLAYER, ModAttributes.BLOOD_EXHAUSTION);
        event.add(EntityType.PLAYER, ModAttributes.NEONATAL_DURATION);
        event.add(EntityType.PLAYER, ModAttributes.DBNO_DURATION);
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> prepareEntityType(String id, @NotNull Supplier<EntityType.Builder<T>> builder, boolean spawnable) {
        return ENTITY_TYPES.register(id, () -> {
            EntityType.Builder<T> type = builder.get().setTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true);
            if (!spawnable) {
                type.noSummon();
            }
            return type.build(REFERENCE.MODID + ":" + id);
        });
    }

    public static @NotNull Set<EntityType<?>> getAllEntities() {
        return ENTITY_TYPES.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toUnmodifiableSet());
    }
}
